package controllers.v1;

import controllers.api.API;
import models.Cert;
import org.bson.types.ObjectId;
import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.libs.Files;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static play.modules.jongo.BaseModel.getCollection;

/**
 * 证书申请/审批/查看
 * Created by xudongmei on 2016/12/13.
 */
@SuppressWarnings("ALL")
public class Certs extends API {

    public static String storePath = Play.configuration.getProperty("attachments.path");
    /**
     * 申请证书
     */
    public static void save() {
        Cert cert = readBody(Cert.class);
        cert.save();
    }

    /**
     * 查看所有已申请的证书信息
     * @param filters
     * @param limit
     * @param offset
     */
    public static void list(@Required String filters,Integer limit,Integer offset) {
        List<Cert> certs =  StreamSupport.stream(getCollection(Cert.class).find().limit(limit).as(Cert.class).spliterator(),false).collect(Collectors.toList());
        certs.forEach(cert -> Logger.info("List：" + cert.companyName));
        renderJSON(certs);
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
    
    /**
     * 我的申请
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

    /**
     * 申请查询（status=0待审批；status=1审批通过；status=2审批不通过）
     * @param status
     */
    public static void search(@Required Integer status){
        renderJSON(getCollection(Cert.class).find("{status:#}",status));
    }

    /**
     * 证书导入
     * @param attachment
     * @param remark
     */
    public static void certsImport(@Required File attachment,String remark){

    }

    /**
     * 附件上传
     * @param attachment
     */
    public static void attachment(@Required File attachment) {
        String fileName = attachment.getName();
        File storeFile = new File(storePath + "/" + fileName);
        Files.copy(attachment, storeFile);
    }

    /**
     * 单个二维码生成
     * @param moduleId
     */
    public static void singleCode(@Required String moduleId){

    }

    /**
     * 批量二维码生成
     * @param beginModuleId
     * @param endModuleId
     */
    public static void batchCode(@Required String beginModuleId,@Required String endModuleId){

    }

    /**
     * 获取流量分析图
     * @param beginModuleId
     * @param endModuleId
     */
    public static void flowAnalysis(@Required String beginModuleId,@Required String endModuleId) {

    }

    /**
     * 获取密度分析图
     * @param beginModuleId
     * @param endModuleId
     */
    public static void densityAnalysis(@Required String beginModuleId,@Required String endModuleId) {

    }

}
