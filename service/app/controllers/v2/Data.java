package controllers.v2;

import com.alibaba.fastjson.JSON;
import com.codahale.metrics.Timer;
import com.google.common.collect.ImmutableMap;
import controllers.BaseController;
import controllers.interceptor.*;
import models.*;
import org.bson.types.ObjectId;
import org.jongo.MongoCursor;
import play.Play;
import play.cache.CacheFor;
import play.mvc.With;
import plugin.MetricsPlugin;
import utils.ESUtils;
import utils.Utility;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Data API实现.
 *
 * @author <a href="mailto:wuzhiqiang@novacloud.com">wuzq</a>
 * @version Revision: 1.0
 * @date 2014-8-1下午11:31:24
 */

@With({APIRateLimiter.class, RequestLog.class, ExceptionCatcher.class, APIResponseWrapper.class, Compress.class, Signature.class, BillService.class})
public class Data extends BaseController {
    private static final int MAX_OFFSET = 2000;
    private static final int MAX_LIMIT = 50;
    public static final int MAX_AUTO_SUGGESTION = 10;
    private static Timer timerRecommends = MetricsPlugin.getMetricRegistry().timer(name(Data.class, "recommends"));
    private static Timer timerOmnibox = MetricsPlugin.getMetricRegistry().timer(name(Data.class, "omnibox"));
    private static Timer timerAutoSuggest = MetricsPlugin.getMetricRegistry().timer(name(Data.class, "autoSuggest"));
    private static Timer timerCategory = MetricsPlugin.getMetricRegistry().timer(name(Data.class, "category"));
    private static Timer timerDetail = MetricsPlugin.getMetricRegistry().timer(name(Data.class, "detail"));
    private static Timer timerCustomer= MetricsPlugin.getMetricRegistry().timer(name(Data.class, "customer"));
    private static Timer timerCustomers= MetricsPlugin.getMetricRegistry().timer(name(Data.class, "customers"));
    private static Timer timerTags= MetricsPlugin.getMetricRegistry().timer(name(Data.class, "tags"));

    /**
     * 根据输入的公司名称进行自动建议
     *
     * @param company_name  公司名称,模糊匹配
     */
    public static void autoSuggest(String company_name) {
        try (Timer.Context ignored = timerAutoSuggest.time()) {
            //todo: mongodb Chinese language text sort
            MongoCursor<Lead> suggestions = Lead.getCollection(Lead.class).find("{companyName:{$regex:#}}", company_name).projection("{companyName:1,_id:1}").limit(MAX_AUTO_SUGGESTION).sort("{companyName:1}").as(Lead.class);
            List<Map<String, String>> idNamePairs = new ArrayList<>(MAX_AUTO_SUGGESTION);
            StreamSupport.stream(suggestions.spliterator(), false).forEach((lead) -> {
                Map<String, String> pair = new HashMap<>(1);
                pair.put("id", lead.getIdAsStr());
                pair.put("companyName", lead.getCompanyName());
                idNamePairs.add(pair);
            });
            renderJSON(JSON.toJSONString(idNamePairs));
        }
    }

    /**
     * 获取行业分类字典
     */
    @CacheFor("1d")
    public static void category() {
        try (Timer.Context ignored = timerCategory.time()) {

            List<String> categories = Lead.getCollection(Lead.class).distinct("category").as(String.class);
            renderJSON(JSON.toJSONString(categories));
        }
    }

    /**
     * 根据keyword对线索的多个字段进行通用查询
     * @param client_id 客户端编号
     * @param keyword 查询关键字,可以是:公司名称,法人,地址,经营范围中的任意关键字
     * @param longitude  当前位置的经度
     * @param latitude    当前位置的纬度
     * @param sort_by  排序方式, : near代表按距离由近及远排序, rate代表根据推荐分数由高到低排序, 默认near
     * @param offset  offset "default": 0
     * @param limit   limit  "default": 20
     */
    public  static void omnibox(String client_id,String keyword,double longitude, double latitude,String sort_by, int offset, int limit){
        try (Timer.Context ignored = timerOmnibox.time()) {
            List<Lead> leads = ESUtils.searchLeadByKeyword(keyword, longitude, latitude, sort_by, safeOffset(offset), safeLimit(limit));
            List<Recommend> recommendList;
            if (leads.isEmpty()) {
                MissingData missingData = new MissingData();
                missingData.setClientId(client_id);
                missingData.setController(Data.class.getName());
                missingData.setMethod("omnibox");
                missingData.setParams(ImmutableMap.of("keyword",keyword==null?"null":keyword,"longitude",longitude,"latitude",latitude,"sort_by",sort_by==null?"null":sort_by,"offset",offset));
                MissingData.getCollection(MissingData.class).save(missingData);
                notFoundEx(client_id, keyword, String.valueOf(longitude),String .valueOf(latitude),String.valueOf(offset), String.valueOf(limit));
            } else {
                recommendList=leads.stream().map(lead -> new Recommend(client_id,lead)).collect(Collectors.toList());
                renderJSON(JSON.toJSONString(recommendList, Play.mode.isDev()));
            }
        }
    }
    /**
     * 获取推荐的销售线索列表
     *
     * @param client_id    客户端编号
     * @param category     行业
     * @param province     省份
     * @param city         城市
     * @param company_name 公司名称
     * @param longitude    当前位置的经度
     * @param latitude     当前位置的纬度
     * @param min_dis      按距离排序时的最小距离限制
     * @param sort_by      排序方式: near代表按距离由近及远排序, rate代表根据推荐分数由高到低排序, 默认rate
     * @param offset       offset "default": 0
     * @param limit        limit  "default": 20
     */
    public static void recommends(String client_id, String category, String province, String city, String company_name, double longitude, double latitude,double min_dis, String sort_by, int offset, int limit) {
        try (Timer.Context ignored = timerRecommends.time()) {
            String sort = "{rating:-1}";
            if (offset > MAX_OFFSET) {
                notFoundEx(client_id, category, province, city, company_name, String.valueOf(offset), String.valueOf(limit));
            } else {
                offset = safeOffset(offset);
                limit = safeLimit(limit);

                Pattern companyNameRegex = param2Regex(company_name);
                List<String> queryTemplateForRecommend = new ArrayList<>(5);
                List<Object> queryParamsForRecommend = new ArrayList<>(5);
                queryTemplateForRecommend.add("clientId:#");
                queryParamsForRecommend.add(client_id);
                if (Utility.isNotEmpty(category)) {
                    queryTemplateForRecommend.add("lead.category:#");
                    queryParamsForRecommend.add(category);
                }
                if (Utility.isNotEmpty(province)) {
                    queryTemplateForRecommend.add("lead.province:#");
                    queryParamsForRecommend.add(province);
                }
                if (Utility.isNotEmpty(city)) {
                    queryTemplateForRecommend.add("lead.city:#");
                    queryParamsForRecommend.add(city);
                }
                if (Utility.isNotEmpty(company_name)) {
                    queryTemplateForRecommend.add("lead.companyName:#");
                    queryParamsForRecommend.add(companyNameRegex);
                }
                String whereForRecommend = "{" + String.join(",", queryTemplateForRecommend) + "}";

                MongoCursor<Recommend> recommends = Recommend.getCollection(Recommend.class).find(whereForRecommend, queryParamsForRecommend.toArray()).sort(sort).skip(offset).limit(limit).as(Recommend.class);
                List<Recommend> recommendList;
                if (recommends.hasNext()) { //predicted already
                    recommendList = StreamSupport.stream(recommends.spliterator(), false).collect(Collectors.toList());
                } else {//not exist predicted ,so query base on category and/or province and/or city

                    boolean isSortByGeo =false;
                    if ("near".equalsIgnoreCase(sort_by)) { isSortByGeo=true;}
                    List<Lead> leads = ESUtils.searchLead(company_name, category, province, city, isSortByGeo ? longitude : null, isSortByGeo ? latitude : null, sort_by, offset, limit);
                    recommendList =  leads.stream().map(lead -> new Recommend(client_id,lead)).collect(Collectors.toList());


//                    List<String> queryTemplate = new ArrayList<>(5);
//                    List<Object> queryParams = new ArrayList<>(6);
//                    queryTemplate.add("phone:{$ne:''}");
//                    if (Utility.isNotEmpty(category)) {
//                        queryTemplate.add("category:#");
//                        queryParams.add(category);
//                    }
//                    if (Utility.isNotEmpty(province)) {
//                        queryTemplate.add("province:#");
//                        queryParams.add(province);
//                    }
//                    if (Utility.isNotEmpty(city)) {
//                        queryTemplate.add("city:#");
//                        queryParams.add(city);
//                    }
//                    if (Utility.isNotEmpty(company_name)) {
//                        queryTemplate.add("companyName:#");
//                        queryParams.add(companyNameRegex);
//                    }
//                    String where = "{" + String.join(",", queryTemplate) + "}";
//
//                    Spliterator<Lead> leadSpliterator;
//                    if ("near".equalsIgnoreCase(sort_by)) {
//                        min_dis = min_dis+0.01;
//                        queryParams.add(longitude);
//                        queryParams.add(latitude);
//                        queryParams.add(min_dis);
//                        queryParams.add(limit);
//
//                        leadSpliterator = Lead.getJongo().runCommand("{geoNear :'lead',query:"+where+",  near :{ type: 'Point', coordinates:  [#, #]}, spherical : true," +
//                                    "minDistance:#,maxDistance:100000, limit:#}",
//                           queryParams.toArray()).throwOnError().field("results").map(dbObject -> {
//                                Double dis = (Double) dbObject.get("dis");
//                                BasicDBObject obj = (BasicDBObject) dbObject.get("obj");
//                                Lead lead = JSON.parseObject(obj.toJson(), Lead.class);
//                                lead.setDis(dis);
//                                lead.setId(obj.getObjectId("_id"));
//                                return lead;
//                            }).spliterator();
//                    } else {//todo:  sort 会导致性能明显下降，暂时去掉
////                        sort = "{entTel:-1,dom:-1}";
//                        leadSpliterator = Lead.getCollection(Lead.class).find(where, queryParams.toArray()).skip(offset).limit(limit).as(Lead.class).spliterator();
//                    }
//                    recommendList = StreamSupport.stream(leadSpliterator, false).map(lead -> new Recommend(client_id,lead)).collect(Collectors.toList());
                }
                if (recommendList.isEmpty()) {
                    MissingData missingData = new MissingData();
                    missingData.setClientId(client_id);
                    missingData.setController(Data.class.getName());
                    missingData.setMethod("recommends");
                    Map<String , Object> map = ImmutableMap.<String,Object>builder()
                            .put("category", category==null?"null":category)
                            .put("province", province==null?"null":province )
                            .put("city", city==null?"null":city)
                            .put("company_name", company_name==null?"null":company_name)
                            .put("longitude", longitude)
                            .put("latitude", latitude)
                            .put("min_dis", min_dis)
                            .put("sort_by", sort_by==null?"null":sort_by)
                            .put("offset", offset)
                                    .build();
                    missingData.setParams(map);
                    MissingData.getCollection(MissingData.class).save(missingData);
                    notFoundEx(client_id, category, province, city, company_name, String.valueOf(offset), String.valueOf(limit));
                } else {
                    renderJSON(JSON.toJSONString(recommendList, Play.mode.isDev()));
                }
            }
        }
    }

    /**
     * 将参数值转换为 正则表达式符号.
     * 参数值为空时，将其转换为 正则表达式符号 .,以 匹配所有数据。相当于忽略该查询条件
     *
     * @param param  参数值
     * @return  模糊匹配用的正则表达式
     */

    private static Pattern param2Regex(String param) {
        return Utility.isEmpty(param) ? Pattern.compile(".") : Pattern.compile(param.trim());
    }

    private static int safeLimit(int limit) {
        limit = limit <= 0 ? 20 : limit;
        return limit > MAX_LIMIT ? MAX_LIMIT : limit;
    }

    private static int safeOffset(int offset) {
        offset = offset < 0 ? 0 : offset;
        return offset > MAX_OFFSET ? MAX_OFFSET : offset;
    }

    /**
     * 标记线索是否有用
     *
     * @param client_id 客户端编号
     * @param lead_id   线索编号
     * @param tag       标记
     */
    public static void tags(String client_id, String lead_id, String tag) {
        try (Timer.Context ignored = timerTags.time()) {
            //save feedback , allow each lead  tag multi value by one client
            //such as :一个企业内的两个sales 分别标记该线索为 有用、无用
            // todo: 使用这些反馈数据训练模型时，需要给这些反馈加权
            Feedback feedback = new Feedback();
            feedback.setClientId(client_id);
            feedback.setRating(Feedback.tag2rating(tag));
            feedback.setLeadId(lead_id);
            feedback.setTag(tag);
            feedback.save();
            if (feedback.getRating() < 0.5) {//tag as useless
                Recommend recommend = Recommend.getCollection(Recommend.class).findOne("{clientId:#,leadId:#}", client_id, lead_id).as(Recommend.class);
                if (recommend != null) {//ensure this recommend lead is exist
                    //todo: recommend.setRating(该client_id对该线索lead_id的所有feedback的 avg(rating) )
                    //remove this from recommends collection
                    recommend.remove();
                }
            }
            //mark client's trained model as need retrain
            TrainedModel.getCollection(TrainedModel.class).update("{clientId:#}", client_id).with("{$set:{needRetrain:true}}");
            ok();
        }
    }

    /**
     * 获取销售线索的详情
     *
     * @param lead_id 线索编号
     */
    public static void detail(String lead_id) {
        try (Timer.Context ignored = timerDetail.time()) {
            Lead lead = Lead.getCollection(Lead.class).findOne(new ObjectId(lead_id)).as(Lead.class);
            if (lead != null) {
                renderJSON(lead.toPrettyJson());
            } else {
                notFoundEx(lead_id);
            }
        }
    }

    /**
     * 更新已有的客户数据
     *
     * @param client_id   客户端编号
     * @param customer_id 客户编号
     * @param body        客户数据, customer.toJson()
     */
    public static void customer(String client_id, String customer_id, String body) {
        try (Timer.Context ignored = timerCustomer.time()) {
            Customer customer = Jsonable.fromJson(body, Customer.class, false);
            if (customer == null) {
                paramsMiss("body_customer");
            } else {
                int n = Customer.getCollection(Customer.class).update("{clientId:#,customerId:#}", client_id, customer_id).with(customer).getN();
                if (n > 0) {
                    ok();
                } else {
                    notFoundEx(client_id, customer_id);
                }
            }
        }
    }

    /**
     * 批量上传新增的客户数据
     *
     * @param client_id 客户端id
     * @param body      Customers json serialization
     */
    public static void customers(String client_id, String body) {
        try (Timer.Context ignored = timerCustomers.time()) {
            List<Customer> customers = JSON.parseArray(body, Customer.class);
            if (customers == null || customers.isEmpty()) {
                paramsMiss("body");
            } else {
                final int[] sum = {0};
                StreamSupport.stream(customers.spliterator(), true).forEach(customer -> {
                    int n = Customer.getCollection(Customer.class).update("{clientId:#,customerId:#}", client_id, customer.getCustomerId()).upsert()
                            .with(customer).getN();
                    sum[0] += n;
                });
                //train model
                //todo: use mq to publish training required message
                if (sum[0] > 0) {
                    new TrainJob(client_id).afterRequest();
                    ok();
                } else {
                    errorJson();
                }
            }
        }

    }
}
