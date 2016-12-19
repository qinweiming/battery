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
 * 证书申请/审批/查看
 * Created by xudongmei on 2016/12/13.
 */
public class Certs extends API {

    /**
     * 申请证书
     */
    public static void save(){
        Cert cert = readBody(Cert.class);
        cert.save();
    }

    public static void approve(@Required String ids, @Required Integer status){
        String[] idArr = ids.split(",");
        for(String id : idArr) {
            Cert cert = getCollection(Cert.class).findOne(new ObjectId(id)).as(Cert.class);
            if (cert == null) {
                notFound(id);
            } else {
                getCollection(Cert.class).update("{status : " + cert.status + "}").with("{$set:{status:" + status + "}}");
            }
        }
    }

    public static void list(Integer pageNum){
        List<Cert> certs =  StreamSupport.stream(getCollection(Cert.class).find().limit(pageNum).as(Cert.class).spliterator(),false).collect(Collectors.toList());
        certs.forEach(cert -> Logger.info("List：" + cert.companyName));
        renderJSON(certs);
    }

    public static void get(@Required String id){
        Cert cert =  getCollection(Cert.class).findOne(new ObjectId(id)).as(Cert.class);
        if (cert == null) {
            notFound(id);
        }else {
            renderJSON(cert);
        }
    }

    public static void main(String[] args) {
        save();
        approve("58528c089cf52e62848b9a11",1);
        list(5);
        get("58528c089cf52e62848b9a11");
    }

}
