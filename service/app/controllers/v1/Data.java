package controllers.v1;

import com.alibaba.fastjson.JSON;
import com.codahale.metrics.Timer;
import controllers.interceptor.*;
import models.*;
import models.v1.Response;
import models.v1.ResponseCode;
import org.bson.types.ObjectId;
import org.jongo.MongoCursor;
import play.mvc.Controller;
import play.mvc.With;
import plugin.MetricsPlugin;

import java.util.List;
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
@With({APIRateLimiter.class, RequestLog.class, APIResponseWrapper.class, Signature.class, BillService.class})
public class Data extends Controller {
    public static final int MAX_OFFSET = 1000;
    public static final int MAX_LIMIT = 50;
    private static Timer timer = MetricsPlugin.getMetricRegistry().timer(name(Data.class, "recommends"));

    /**
     * 获取推荐的销售线索列表
     * @param clientId
     * @param category
     * @param province
     * @param offset
     * @param limit
     */
    public static void recommends(String clientId, String category, String province,  int offset, int limit) {
        try (Timer.Context ignored = timer.time()){
            Response response = new Response();
            response.setCode(ResponseCode.SUCCESS);
            try {
                String sort = "{rating:-1}";
                offset = safeOffset(offset);
                limit = safeLimit(limit);
                MongoCursor<Recommend> recommends = Recommend.getCollection(Recommend.class).find("{clientId:#}", clientId).sort(sort).skip(offset).limit(limit).as(Recommend.class);
                List<Recommend> recommendList ;
                if (recommends.hasNext()) { //predicted already
                    recommendList = StreamSupport.stream(recommends.spliterator(), false).collect(Collectors.toList());
                }else {//not exist predicted ,so query base on category and province
                    MongoCursor<Lead> leads = Lead.getCollection(Lead.class).find("{clientId:#,category:#,province:#}", clientId, category, province).skip(offset).limit(limit).as(Lead.class);
                    recommendList = StreamSupport.stream(leads.spliterator(), false).map(lead -> {
                        Recommend recommend=new Recommend();
                        recommend.setClientId(clientId);
                        recommend.setLead(lead);
                        recommend.setLeadId(lead.getIdAsStr());
                        recommend.setRating(0.0);
                        return recommend;
                    }).collect(Collectors.toList());
                }
                response.setData(JSON.toJSONString(recommendList));
            } catch (Exception e) {
                response.setCodeWithDefaultMsg(ResponseCode.SERVER_INTERNAL_ERROR);
                response.setDetail(e.getMessage());
            }
            renderJSON(JSON.toJSONString(response));
        }
    }

    private static int safeLimit(int limit) {
        limit = limit<=0?1:limit;
        return limit> MAX_LIMIT ? MAX_LIMIT :limit;
    }

    private static int safeOffset(int offset) {
        offset = offset<0?0:offset;
        return offset > MAX_OFFSET ? MAX_OFFSET :offset;
    }

    /**
     * 标记线索是否有用
     * @param clientId
     * @param leadId
     * @param tag
     */
    public static void tags(String clientId, String leadId, String tag) {
        Response response = new Response();
        response.setCodeWithDefaultMsg(ResponseCode.SUCCESS);
        try {
            Recommend recommend = Recommend.getCollection(Recommend.class).findOne("{clientId:#,leadId:#}", clientId, leadId).as(Recommend.class);
            if (recommend != null) {//ensure this recommend lead is exist
                //save feedback
                Feedback feedback = Feedback.getCollection(Feedback.class).findOne("{clientId:#,leadId:#}", clientId, leadId).as(Feedback.class);
                if (feedback ==null){
                    feedback=new Feedback();
                }
                feedback.setClientId(clientId);
                feedback.setRating(Feedback.tag2rating(tag));
                feedback.setLeadId(leadId);
                feedback.setTag(tag);
                feedback.save();
                //remove this from recommends collection
                recommend.remove();
                //mark client's trained model as need retrain
                TrainedModel.getCollection(TrainedModel.class).update("{clientId:#}",clientId).with("{$set:{needRetrain:true}}");
            } else {
                response.setCodeWithDefaultMsg(ResponseCode.CLIENT_RESOURCE_NOT_FOUND);
            }
        } catch (Exception e) {
            response.setCodeWithDefaultMsg(ResponseCode.SERVER_INTERNAL_ERROR);
            response.setDetail(e.getMessage());
        }
        renderJSON(JSON.toJSONString(response));
    }

    /**
     * 获取销售线索的详情
     * @param leadId
     */
    public static void detail(String leadId) {
        Response response = new Response();
        response.setCode(ResponseCode.SUCCESS);
        try {
            Lead lead = Lead.getCollection(Lead.class).findOne(new ObjectId(leadId)).as(Lead.class);
            if (lead != null) {
                response.setData(lead.toJson());
            } else {
                response.setCodeWithDefaultMsg(ResponseCode.CLIENT_RESOURCE_NOT_FOUND);
            }
        } catch (Exception e) {
            response.setCodeWithDefaultMsg(ResponseCode.SERVER_INTERNAL_ERROR);
            response.setDetail(e.getMessage());
        }
        renderJSON(JSON.toJSONString(response, true));
    }

    /**
     * 更新已有的客户数据
     * @param clientId
     * @param customerId
     * @param body
     */
    public static void customer(String clientId,String  customerId,String  body){
        Response response = new Response();
        response.setCode(ResponseCode.SUCCESS);
        try {
            Customer customer = Jsonable.fromJson(body, Customer.class, false);
            if (customer == null) {
                response.setCodeWithDefaultMsg(ResponseCode.CLIENT_FORMAT_ERROR);
            } else {
                int n = Customer.getCollection(Customer.class).update("{clientId:#,customerId:#}", clientId, customerId).with(customer).getN();
                response.setCodeWithDefaultMsg(ResponseCode.SUCCESS);
                response.setData(String.format("{count:%s}", n));
            }
        } catch (Exception e) {
            response.setCodeWithDefaultMsg(ResponseCode.SERVER_INTERNAL_ERROR);
            response.setDetail(e.getMessage());
        }
        renderJSON(JSON.toJSONString(response));

    }

    /**
     * 批量上传新增的客户数据
     * @param clientId
     * @param body
     */
    public static void customers(String clientId, String  body){
        Response response = new Response();
        response.setCode(ResponseCode.SUCCESS);
        try {
            List<Customer> customers  = JSON.parseArray(body,Customer.class);
            if (customers == null || customers.isEmpty()) {
                response.setCodeWithDefaultMsg(ResponseCode.CLIENT_FORMAT_ERROR);
            } else {
                final int[] sum = {0};
                StreamSupport.stream(customers.spliterator(), true).forEach(customer -> {
                    int n = Customer.getCollection(Customer.class).update("{clientId:#,customerId:#}", clientId, customer.getCustomerId()).upsert()
                            .with(customer).getN();
                    sum[0] += n;
                });
                response.setData(String.format("{count:%s}", sum[0]));
                response.setCodeWithDefaultMsg(ResponseCode.SUCCESS);
                //train model
                //todo: use mq to publish training required message
                if(sum[0]>0)
                    new TrainJob(clientId).afterRequest();
            }
        } catch (Exception e) {
            response.setCodeWithDefaultMsg(ResponseCode.SERVER_INTERNAL_ERROR);
            response.setDetail(e.getMessage());
        }
        renderJSON(JSON.toJSONString(response));

    }
}
