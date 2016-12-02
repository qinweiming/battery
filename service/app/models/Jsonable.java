package models;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import utils.Logs;

import java.io.Serializable;

/**
 *
 * Created by xiaoyong on 24/10/15.
 */
public interface Jsonable extends Serializable{

    default String toJson(){
        return JSON.toJSONString(this);
    }
    default String toPrettyJson(){
        return  JSON.toJSONString(this, SerializerFeature.PrettyFormat);
    }
    static <T> T fromJson(String jsonString, Class<T> clazz){

        try {
            return JSON.parseObject(jsonString, clazz,Feature.config(JSON.DEFAULT_PARSER_FEATURE,Feature.UseBigDecimal,true));
        } catch (Exception e) {
            Logs.logger().warn("parse json error",e);
            return null;
        }
    }
    static <T> T fromJson(String jsonString, Class<T> clazz, boolean useBigDecimal){
        try {
            return JSON.parseObject(jsonString, clazz,Feature.config(JSON.DEFAULT_PARSER_FEATURE,Feature.UseBigDecimal,useBigDecimal));
        } catch (Exception e) {
            Logs.logger().warn("parse json error",e);
            return null;
        }
    }
}
