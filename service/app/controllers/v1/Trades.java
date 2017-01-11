package controllers.v1;

import controllers.api.API;
import models.*;
import models.Package;
import org.bson.types.ObjectId;
import play.data.validation.Required;
import utils.SafeGuard;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static play.modules.jongo.BaseModel.getCollection;

/**
 * Created by xudongmei on 2016/12/13.
 */
public class Trades extends API {
    /**
     * 1.新增交易信息
     */
    public static void save() {
        Trade trade = readBody(Trade.class);
        trade.save();
    }

    /**
     * 2.获取交易信息
     */
    public static void list(String filters,Integer limit,Integer offset) {

        filters= SafeGuard.safeFilters(filters);
        limit = SafeGuard.safeLimit(limit);
        offset= SafeGuard.safeOffset(offset);

//        Search search = readFilters(Search.class);//获取列表页的查询条件
//        /**
//         * sql查询语句
//         * fromId、toId、startDate、endDate、startProductId、endProductId为查询条件
//         */
//        String sql = "";
//        if(StringUtils.isNotNullOrEmpty(search.fromId)) {
//            sql += "{fromId: " + search.fromId;
//        }if(StringUtils.isNotNullOrEmpty(search.toId)) {
//            sql += ",toId: " + search.toId;
//        }else if(search.startDate != null && search.endDate != null) {
//            sql += ",createTime: {\"$gte\": " + search.startDate + ", \"$lte\": " + search.endDate + "}";
//        }else if(search.startDate != null) {
//            sql += ",createTime: {\"$gte\": " + search.startDate + "}";
//        }else if(search.endDate != null) {
//            sql += ",createTime: {\"$lte\": " + search.endDate + "}";
//        }else if(search.startProductId != null && search.endProductId != null) {
//            sql += ",productIds: {\"$gte\": " + search.startProductId + ", \"$lte\": " + search.endProductId + "}";
//        }else if(search.startProductId != null) {
//            sql += ",productIds: {\"$gte\": " + search.startProductId + "}";
//        }else if(search.endProductId != null) {
//            sql += ",productIds: {\"$lte\": " + search.endProductId + "}";
//        }
//        sql += "}";
//        Logger.info("sql: " + sql);
        List<Trades> trades = StreamSupport.stream(getCollection(Trades.class).find().limit(limit).skip(offset).as(Trades.class).spliterator(),false).collect(Collectors.toList());
        Long totalCount = getCollection(Trade.class).count(filters);
        response.setHeader("X-Total-Count",String.valueOf(totalCount));
        //Logger.info("X-Total-Count: " + response.getHeader("X-Total-Count"));
        renderJSON(trades);
    }

    /**
     * 3.获取单个交易信息
     * @param id
     */
    public static void get(@Required String id) {
        Trade trade =  getCollection(Trade.class).findOne(new ObjectId(id)).as(Trade.class);
        if (trade == null) {
            notFound(id);
        }else {
            renderJSON(trade);
        }
    }

    /**
     * 4.获取流量分析图
     * @param beginModuleId
     * @param endModuleId
     */
    public static void flowAnalysis(@Required String beginModuleId,@Required String endModuleId) {
        String sql = "{moduleId: {\"$gte\": " + beginModuleId + ", \"$lte\": " + endModuleId + "}}";
        List<Module> modules = StreamSupport.stream(getCollection(Module.class).find(sql).as(Module.class).spliterator(),false).collect(Collectors.toList());
        renderJSON(modules);
    }

    /**
     * 5.获取密度分析图
     * @param beginModuleId
     * @param endModuleId
     */
    public static void densityAnalysis(@Required String beginModuleId,@Required String endModuleId) {
        flowAnalysis(beginModuleId,endModuleId);
    }

    /**
     * 6.上传电池包（与模组对应关系）数据
     */
    public static void packageAndModule() {
        Package packages = readBody(Package.class);
        packages.save();
    }
    /**
     * 7.汽车制造商上传汽车与电池包的对应关系
     */
    public static void carAndPackage() {
        Car cars = readBody(Car.class);
        cars.save();
    }

    /**
     * 8.获取电池包信息
     * @param id packageId
     */
    public static void getPackage(@Required String id){
        Package Package =  getCollection(Package.class).findOne("{packageId:#}",id).as(Package.class);
        if (Package == null) {
            notFound(id);
        }else {
            renderJSON(Package);
        }
    }

    /**
     * 9.获取汽车信息
     * @param id carId
     */
    public static void getCar(@Required String id){
        Car car =  getCollection(Car.class).findOne("{carId:#}",id).as(Car.class);
        if (car == null) {
            notFound(id);
        }else {
            renderJSON(car);
        }
    }

    /**
     * 10.新增二维码扫描记录
     */
    public static void addRecord(){
        Scan scan = readBody(Scan.class);
        scan.save();
    }

    /**
     * 11.查询二维码扫描记录
     */
 /*   public static void getRecord(String filters,Integer limit,Integer offset){
        filters= SafeGuard.safeFilters(filters);
        limit = SafeGuard.safeLimit(limit);
        offset= SafeGuard.safeOffset(offset);
        List<Scan> scans = StreamSupport.stream(getCollection(Scan.class).find(filters).limit(limit).skip(offset).as(Scan.class).spliterator(),false).collect(Collectors.toList());
        Long totalCount = getCollection(Scan.class).count(filters);
        response.setHeader("X-Total-Count",String.valueOf(totalCount));
        renderJSON(scans);
    }*/
}
