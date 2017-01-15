package controllers.v1;

import com.google.common.base.Strings;
import com.google.common.io.ByteArrayDataInput;
import controllers.api.API;
import models.Car;
import models.Cert;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.jongo.MongoCursor;
import play.Logger;
import play.Play;
import play.data.binding.As;
import play.data.validation.Range;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.libs.Files;
import play.libs.MimeTypes;
import play.mvc.Router;
import play.vfs.VirtualFile;
import utils.SafeGuard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static play.modules.jongo.BaseModel.getCollection;

/**
 * 证书管理
 * Created by xudongmei on 2016/12/13.
 */
public class Certs extends API {

    public static String certFilesPath = Play.configuration.getProperty("attachments.path")+File.separator+"certs";
    /**
     * 1.政府内部用户-离线信息录入（申请证书）
     */
    public static void apply() {
        Cert cert = readBody(Cert.class);
        cert.save();
        created(cert);
    }

    /**
     * 2.政府内部用户-申请状态查询（status=0待审批；status=1审批通过；status=2审批不通过）
     * 通过 status 的取值进入到待审批列表、已审批列表
     */
    public static void list(String filters, @As(value = ",") List<String> params, @Range(min = 0,max = 100) Integer limit, @Range(min = 0) Integer offset) {

        if(Strings.isNullOrEmpty(filters)){
            filters="{companyName: {$regex: #},createTime:{$gte:#},createTime:{$lte:#},status:#}";
        }else {
            filters = SafeGuard.safeFilters(filters);
        }
        if(StringUtils.countMatches(filters,"#") != params.size()){
            badRequest("filters args size should equals params size!");
        }
        //todo: 处理params中的数据类型
        MongoCursor<Cert> mongoCursor = getCollection(Cert.class).find(filters,params).limit(limit).skip(offset).as(Cert.class);
        response.setHeader("X-Total-Count",String.valueOf(mongoCursor.count()));

        List<Cert> certs = StreamSupport.stream(mongoCursor.spliterator(),false).collect(Collectors.toList());
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

        /**
         * todo: 没考虑 id 不存在的情况
         */
        for(String id : idArr) {
            getCollection(Cert.class).update(new ObjectId(id)).multi().with("{$set:{status:#,modifyTime:#}}",status,modifyTime).isUpdateOfExisting();
        }
        //todo: 证书审批后,需要生成证书(zip压缩包格式)文件并保存
    }

    /**
     * 3.查询企业的证书信息
     * @param companyId 企业的Id
     */
    public static void get(@Required String companyId) {
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
    public static void attachment( File attachment) throws FileNotFoundException {
        //todo: 通用的附件上传
        renderJSON("{\"success\":\"ok\"}");
//        Car car = new Car();
//        renderJSON(car.toPrettyJson());
//        new FileInputStream(attachment),
//                MimeTypes.getContentType(attachment.getName());
    }

    /**
     *  证书(压缩包文件)下载
     * @param id
     */
    public static void download(@Required String id) {
        Cert cert =  getCollection(Cert.class).findOne(new ObjectId(id)).as(Cert.class);
        notFoundIfNull(cert);
//        renderBinary(new ByteArrayInputStream(VirtualFile.fromRelativePath(certFilesPath+File.separator+cert.getCertPath()).content()),cert.getCompanyName()+".zip");

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
