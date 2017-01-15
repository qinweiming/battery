package controllers.v1;

import com.google.common.base.Strings;
import controllers.api.API;
import models.*;
import models.Package;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.jongo.MongoCursor;
import play.data.binding.As;
import play.data.validation.Range;
import play.data.validation.Required;
import utils.SafeGuard;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static controllers.v1.Certs.formatDate;
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
        created(trade);
    }

    /**
     * 2.获取交易信息
     */

    public static void list(String filters, @As(value = ",") List<String> params, @Range(min = 0,max = 100) Integer limit, @Range(min = 0) Integer offset) {

        if(Strings.isNullOrEmpty(filters)){
            filters="{from: {$regex: #},to: {$regex: #},_created:{$gte:#},_created:{$lte:#},id:{$gte:#},id:{$lte:#}}";
        }else {
            filters = SafeGuard.safeFilters(filters);
        }
        if(StringUtils.countMatches(filters,"#") != params.size()){
            badRequest("filters args size should equals params size!");
        }
        //todo: 处理 params中的数据类型
        String from = params.get(0);
        String to = params.get(1);
        Date startDate = formatDate(params.get(2));
        Date endDate = formatDate(params.get(3));
        String startModuleId = params.get(4);
        String endModuleId = params.get(5);

        MongoCursor<Trade> mongoCursor = getCollection(Trade.class).find(filters,from,to,startDate,endDate,startModuleId,endModuleId).limit(limit).skip(offset).as(Trade.class);
        response.setHeader("X-Total-Count",String.valueOf(mongoCursor.count()));

        List<Trade> trades = StreamSupport.stream(mongoCursor.spliterator(),false).collect(Collectors.toList());
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
        created(packages);
    }
    /**
     * 7.汽车制造商上传汽车与电池包的对应关系
     */
    public static void carAndPackage() {
        Car cars = readBody(Car.class);
        cars.save();
        created(cars);
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
        created(scan);
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
