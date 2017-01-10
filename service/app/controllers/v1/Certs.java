package controllers.v1;

import controllers.api.API;
import models.Cert;
import org.bson.types.ObjectId;
import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.libs.Files;
import utils.SafeGuard;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static play.modules.jongo.BaseModel.getCollection;

/**
 * Created by xudongmei on 2016/12/13.
 */
@SuppressWarnings("ALL")
public class Certs extends API {

    public static String storePath = Play.configuration.getProperty("attachments.path");
    /**
     * 1.政府内部用户-离线信息录入（申请证书）
     */
    public static void apply() {
        Cert cert = readBody(Cert.class);
        cert.createTime = new Date();//添加创建时间
        cert.modifyTime = new Date();//添加修改时间
        cert.save();
    }

    /**
     * 2.政府内部用户-申请状态查询（status=0待审批；status=1审批通过；status=2审批不通过）
     * 通过 status 的取值进入到待审批列表、已审批列表
     */
    public static void list(String filters,Integer limit,Integer offset) {

        filters= SafeGuard.safeFilters(filters);
        limit = SafeGuard.safeLimit(limit);
        offset= SafeGuard.safeOffset(offset);
        List<Cert> certs = StreamSupport.stream(getCollection(Cert.class).find(filters).limit(limit).skip(offset).as(Cert.class).spliterator(),false).collect(Collectors.toList());
        //todo: get row count and set into http response HEADER field: X-Total-Count
        //Logger.info("filters: " + filters);
        //Logger.info("certs len: " + certs.size());
        Long totalCount = getCollection(Cert.class).count(filters);
        response.setHeader("X-Total-Count",String.valueOf(totalCount));
        //Logger.info("X-Total-Count: " + response.getHeader("X-Total-Count"));
        renderJSON(certs);
    }

    /**
     * 2.1 政府内部用户-证书审批
     * @param ids
     */
    public static void approve(@Required String ids) {
        Integer status = Integer.valueOf(request.params.get("status"));
        String[] idArr = ids.split(",");
        Date modifyTime = new Date();
        Logger.info("modifyTime: " + modifyTime);
        /**
         * 没考虑 id 不存在的情况
         */
        for(String id : idArr) {
            getCollection(Cert.class).update(new ObjectId(id)).with("{$set:{status:#,modifyTime:#}}",status,modifyTime).isUpdateOfExisting();
        }
    }

    /**
     * 3.查询企业的证书信息
     * @param companyId 企业的Id
     */
    public static void get(@Required String companyId){
        Cert cert =  getCollection(Cert.class).findOne("{companyId:#}",companyId).as(Cert.class);
        if (cert == null) {
            notFound(companyId);
        }else {
            renderJSON(cert);
        }
    }

    /**
     * 4.证书导入
     * @param attachment
     * @param remark
     */
    public static void certsImport(@Required File attachment,String remark){

    }

    /**
     * 5.附件上传
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


}
