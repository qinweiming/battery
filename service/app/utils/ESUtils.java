package utils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import models.Lead;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bson.types.ObjectId;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ES工具类
 *
 * @author <a href="mailto:fivesmallq@gmail.com">fivesmallq</a>
 * @version Revision: 1.0
 * @date 16/6/6 下午11:34
 */
public class ESUtils {
    private static final Logger logger = LoggerFactory.getLogger(ESUtils.class);
    private static Client client = null;
    private static final String index = Play.configuration.getProperty("es.index","data");
    private static final String type = Play.configuration.getProperty("es.type","lead");

    static {
        try {
            String host = Play.configuration.getProperty("es.host","127.0.0.1");
            Integer port = Integer.valueOf(Play.configuration.getProperty("es.port","9300"));
            client = TransportClient.builder().build()
                    .addTransportAddress(
                            new InetSocketTransportAddress(
                                    InetAddress.getByName(host)
                                    , port));
        } catch (UnknownHostException e) {
            logger.error("init es client error!", e);
        }
    }

    static BulkProcessor staticBulkProcessor = null;


    /**
     * 获取一个es操作的client
     *
     * @return
     */
    public static Client getClient() {
        return client;
    }

    public static void close() {
        if (client != null)
            try {
                client.close();
            } catch (Exception e) {
                logger.error("close es client error!", e);
            }
    }

    /**
     * 自动提交文档
     *
     * @return
     */
    public static BulkProcessor getBulkProcessor() {
        //自动批量提交方式
        if (staticBulkProcessor == null) {
            try {
                staticBulkProcessor = BulkProcessor.builder(getClient(),
                        new BulkProcessor.Listener() {
                            @Override
                            public void beforeBulk(long executionId, BulkRequest request) {
                                //提交前调用
//                                System.out.println(new Date().toString() + " before");
                            }

                            @Override
                            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                                //提交结束后调用（无论成功或失败）
//                                System.out.println(new Date().toString() + " response.hasFailures=" + response.hasFailures());
                                logger.info("提交" + response.getItems().length + "个文档，用时"
                                        + response.getTookInMillis() + "MS" + (response.hasFailures() ? " 有文档提交失败！" : ""));
//                                response.hasFailures();//是否有提交失败
                            }

                            @Override
                            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                                //提交结束且失败时调用
                                logger.error(" 有文档提交失败！after failure=" + failure);
                            }
                        })

                        .setBulkActions(1000)//文档数量达到1000时提交
                        .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))//总文档体积达到5MB时提交 //
                        .setFlushInterval(TimeValue.timeValueSeconds(5))//每5S提交一次（无论文档数量、体积是否达到阈值）
                        .setConcurrentRequests(10)//加1后为可并行的提交请求数，即设为0代表只可1个请求并行，设为1为2个并行
                        .build();
//                staticBulkProcessor.awaitClose(10, TimeUnit.MINUTES);//关闭，如有未提交完成的文档则等待完成，最多等待10分钟
            } catch (Exception e) {//关闭时抛出异常
                e.printStackTrace();
            }
        }
        return staticBulkProcessor;
    }

    /**
     * 批量保存数据
     *
     * @param datas
     * @return
     */
    public static String save(List<Lead> datas) {
        for (Lead data : datas) {
            Validate.notNull(data, "lead is can't be null!");
            if (StringUtils.isEmpty(data.getId().toString())) {
                data.setId(ObjectId.get().toString());
            }
            List<Double> location = data.getGeo().getCoordinates();
            data.setLocation(location);
            data.setGeo(null);
            IndexRequest indexRequest = new IndexRequest(index, type, data.getId().toString()).source(JSON.toJSONString(data));
            getBulkProcessor().add(indexRequest);
        }
        return "";
    }

    /**
     * 批量保存数据
     *
     * @param data
     * @return
     */
    public static String saveBatch(Lead data) {
        Validate.notNull(data, "lead is can't be null!");
        if (StringUtils.isEmpty(data.getId().toString())) {
            data.setId(ObjectId.get().toString());
        }
        List<Double> location = data.getGeo().getCoordinates();
        data.setLocation(location);
        data.setGeo(null);
        IndexRequest indexRequest = new IndexRequest(index, type, data.getId().toString()).source(JSON.toJSONString(data));
        getBulkProcessor().add(indexRequest);
        return "";
    }

    /**
     * 直接保存数据。
     *
     * @param data
     * @return
     */
    public static String save(Lead data) {
        Validate.notNull(data, "lead is can't be null!");
        if (StringUtils.isEmpty(data.getId().toString())) {
            data.setId(ObjectId.get().toString());
        }
        List<Double> location = data.getGeo().getCoordinates();
        data.setLocation(location);
        data.setGeo(null);
        return client.prepareIndex(index, type, data.getId().toString()).setSource(JSON.toJSONString(data)).get().getId();
    }


    /**
     * 搜索 ,按距离排序
     *
     * @param companyName
     * @param category
     * @param province
     * @param city
     * @param longitude
     * @param latitude
     * @param sort  unused
     * @param offset
     * @param limit
     * @return
     */
    public static List<Lead> searchLead(String companyName, String category, String province, String city, Double longitude, Double latitude, String sort, long offset, long limit) {
        List<Lead> articles = new ArrayList<>();
        if (limit == 0) {
            limit = 10;
        }
        if (StringUtils.isEmpty(sort)) {
            sort = "rate:desc";
        }

        Iterable<String> splitter = Splitter.on(",").trimResults()
                .omitEmptyStrings().split(sort);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(category)) {
            queryBuilder.filter(QueryBuilders.termQuery("category", category));
        }
        if (StringUtils.isNotEmpty(city)) {
            queryBuilder.filter(QueryBuilders.termQuery("city", city));
        }
        if (StringUtils.isNotEmpty(province)) {
            queryBuilder.filter(QueryBuilders.termQuery("province", province));
        }
        if (StringUtils.isNotEmpty(companyName)) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("companyName", companyName).slop(0).operator(MatchQueryBuilder.Operator.AND));
        }
        if (latitude != null && longitude != null) {
            queryBuilder.must(QueryBuilders.geoDistanceQuery("location")
                    .point(latitude, longitude)
                    .distance(5000, DistanceUnit.KILOMETERS)
                    //.optimizeBbox("memory")
                    .geoDistance(GeoDistance.PLANE));
            //暂时使用不了
            //queryBuilder.must(QueryBuilders.scriptQuery(new Script("doc['dis'].distanceInKm("+latitude+","+longitude+")")));

        }
        SearchRequestBuilder requestBuilder = client.prepareSearch(index)
                .setQuery(queryBuilder).setTypes(type)
                .setFrom((int) offset).setSize((int) limit).setExplain(Play.mode.isDev());
        // process sort
//        for (String one : splitter) {
//            String[] sortField = StringUtils.split(one, ":");
//            if (sortField.length == 1) {
//                requestBuilder.addSort(sortField[0], getSort("asc"));
//            } else {
//                requestBuilder.addSort(sortField[0],
//                        getSort(sortField[1]));
//            }
//        }
        if (latitude != null && longitude != null) {
            requestBuilder.addSort(SortBuilders.geoDistanceSort("location").point(latitude, longitude).order(SortOrder.ASC).unit(DistanceUnit.METERS));
        }else{
            //todo: sort by rate
            sort = "rate:desc";
           // requestBuilder.addSort("companyName",SortOrder.DESC);
        }
        logger.info("search request:{}", requestBuilder);
        SearchResponse response = requestBuilder.execute().actionGet();
        SearchHit[] results = response.getHits().getHits();
        logger.info("Current results: {} ", results.length);
        for (SearchHit hit : results) {
            Object[] values = hit.getSortValues();
            Map<String, Object> result = hit.getSource();
            Lead article = JSON.parseObject(JSON.toJSONString(result), Lead.class);
            //设置距离
            if(values!=null && values.length>0)
                article.setDis(Converter.parseDouble( values[0]));
            articles.add(article);
        }
        return articles;
    }

    public static List<Lead> searchLeadByKeyword(String keyword, Double longitude, Double latitude, String sort, long offset, long limit) {
        List<Lead> articles = new ArrayList<>();
        if (limit == 0) {
            limit = 10;
        }
        if (StringUtils.isEmpty(sort)) {
            sort = "rate:desc";
        }
        Iterable<String> splitter = Splitter.on(",").trimResults()
                .omitEmptyStrings().split(sort);
        //todo:
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.multiMatchQuery(keyword,"companyName", "legalPerson","dom","opscope").operator(MatchQueryBuilder.Operator.OR).type(MultiMatchQueryBuilder.Type.PHRASE));
        if (latitude != null && longitude != null) {
            queryBuilder.must(QueryBuilders.geoDistanceQuery("location")
                    .point(latitude, longitude)
                    .distance(5000, DistanceUnit.KILOMETERS)
                    //.optimizeBbox("memory")
                    .geoDistance(GeoDistance.PLANE));
            //暂时使用不了
            //queryBuilder.must(QueryBuilders.scriptQuery(new Script("doc['dis'].distanceInKm("+latitude+","+longitude+")")));

        }
        SearchRequestBuilder requestBuilder = client.prepareSearch(index)
                .setQuery(queryBuilder).setTypes(type)
                .setFrom((int) offset).setSize((int) limit).setExplain(Play.mode.isDev());

        if (latitude != null && longitude != null) {
            requestBuilder.addSort(SortBuilders.geoDistanceSort("location").point(latitude, longitude).order(SortOrder.ASC).unit(DistanceUnit.METERS));
        }
        logger.info("search request:{}", requestBuilder);
        SearchResponse response = requestBuilder.execute().actionGet();
        SearchHit[] results = response.getHits().getHits();
        logger.info("Current results: {} ", results.length);
        for (SearchHit hit : results) {
            Object[] values = hit.getSortValues();
            Map<String, Object> result = hit.getSource();
            Lead article = JSON.parseObject(JSON.toJSONString(result), Lead.class);
            //设置距离
            article.setDis(Converter.parseDouble( values[0]));
            articles.add(article);
        }
        return articles;
    }

    public static SortOrder getSort(String sort) {
        if ("desc".equals(sort)) {
            return SortOrder.DESC;
        } else {
            return SortOrder.ASC;
        }
    }
}
