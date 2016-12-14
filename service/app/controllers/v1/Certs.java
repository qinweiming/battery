package controllers.v1;

import controllers.api.API;
import models.Cert;
import org.bson.types.ObjectId;
import play.Logger;
import play.data.validation.Required;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static play.modules.jongo.BaseModel.getCollection;

/**
 * Created by xudongmei on 2016/12/13.
 */
public class Certs extends API {
    /**
     * 申请证书
     */
    public static void save(){
        Cert cert = readBody(Cert.class);
        cert.save();
      //  list();
    }

    /**
     * 审批证书-修改status字段
     * @param id
     */
    public static void approve(@Required String id,@Required Integer status){
        Cert cert =  getCollection(Cert.class).findOne(new ObjectId(id)).as(Cert.class);
        if (cert == null) {
            notFound(id);
        }else {
            Logger.info("未审批之前的证书状态：" + cert.status);
            getCollection(Cert.class).update("{status : " + cert.status + "}").with("{$set:{status:" + status + "}}");

            Cert certNew =  getCollection(Cert.class).findOne(new ObjectId(id)).as(Cert.class);
            Logger.info("假设审批通过后的状态：" + certNew.status);

            renderJSON(certNew);
        }

    }

    /**
     * 显示所有已经提交的申请证书（政府内部）
     */
    public static void list(){
        List<Cert> certses =  StreamSupport.stream(getCollection(Cert.class).find().limit(10).as(Cert.class).spliterator(),false).collect(Collectors.toList());
        certses.forEach(certs -> Logger.info("List：" + certs.companyName));
        renderJSON(certses);
    }

    /**
     * 获取指定证书的信息
     * @param id
     */
    public static void get(@Required String id){
        Cert cert =  getCollection(Cert.class).findOne(new ObjectId(id)).as(Cert.class);
        if (cert == null) {
            notFound(id);
        }else {
            renderJSON(cert);
        }
    }
}
