package controllers.v1;

import com.google.common.base.Strings;
import controllers.api.API;
import javafx.util.Pair;
import models.Cert;
import models.Module;
import models.SeedMatrix;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.jongo.MongoCursor;
import play.Logger;
import play.Play;
import play.data.binding.As;
import play.data.validation.Range;
import play.data.validation.Required;
import play.libs.Files;
import play.libs.IO;
import play.libs.Images;
import play.vfs.VirtualFile;
import utils.FileUtilException;
import utils.SafeGuard;

import java.io.*;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static play.modules.jongo.BaseModel.getCollection;
import static utils.FileUtils.fileToByte;
import static utils.FileUtils.writeFile;

/**
 * 证书管理
 * Created by xudongmei on 2016/12/13.
 */
public class Certs extends API {

    public static String certFilesPath = Play.configuration.getProperty("attachments.path") + File.separator + "certs";
    public static String QRPath = Play.configuration.getProperty("attachments.path") + File.separator + "QRs";//二维码地址

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

        if(Strings.isNullOrEmpty(filters)) {
            filters="{companyName: {$regex: #},_created:{$gte:#},_created:{$lte:#},status:#}";
        }else {
            filters = SafeGuard.safeFilters(filters);
        }
        if(StringUtils.countMatches(filters,"#") != params.size()) {
            badRequest("filters args size should equals params size!");
        }
        //todo: 处理 params中的数据类型
        String companyName = params.get(0);
        Date startDate = formatDate(params.get(1));
        Date endDate = formatDate(params.get(2));
        Long status = Long.valueOf(params.get(3));

        MongoCursor<Cert> mongoCursor = getCollection(Cert.class).find(filters,companyName,startDate,endDate,status).limit(limit).skip(offset).as(Cert.class);
        response.setHeader("X-Total-Count",String.valueOf(mongoCursor.count()));

        List<Cert> certs = StreamSupport.stream(mongoCursor.spliterator(),false).collect(Collectors.toList());
        renderJSON(certs);
    }

    /**
     * 2.1 政府内部用户-证书审批
     * @param ids
     */
    public static void approve(@Required String ids) throws FileUtilException, IOException {

        Integer status = Integer.valueOf(request.params.get("status"));
        String[] idArr = ids.split(",");
        Date modifyTime = new Date();

        //todo: 证书审批后,需要生成证书(zip压缩包格式)文件并保存
        for(String id : idArr) {
            getCollection(Cert.class).update(new ObjectId(id)).multi().with("{$set:{status:#,modifyTime:#}}",status,modifyTime).isUpdateOfExisting();
            //byte[] cc = gc(vendorCode,creditCode);传入厂商代码、企业信用码
            /**
             * 将文件转化为 byte[] , 将 byte[] 写到文件
             */
/*            byte[] cc = fileToByte("F:/新建文本文档.txt");
            Logger.info("cc: " + cc);
            File dir = new File(certFilesPath + "/" + id);
            Logger.info("dir: " + dir);
            if(!dir.exists() && dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            IO.write(cc,dir);//将byte[]写入到文件*/
            /**
             * @Description
             *  1.调用接口生成 tradeSk,productSK,Matrix
             *  2.将3个各生成一个文件、
             *  3.将文件压缩成一个zip包
             */
         /*   String tradeSk = generateSkById(社会信用码).toString;
            String productSK = generateSkById(厂商代码).toString;
            SeedMatrix matrix = CpkMatrixsFactory.generateCpkMatrix();
            String[] sk = {tradeSk,productSK};

            String filePath = certFilesPath + File.separator + id;
            String fileName = id + "_tradeSk";
            writeFile(filePath, fileName,sk);*/
            /**
             * 实现文件压缩，并将源文件删除
             */
        }
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
     * @param attachment 导入的Zip包
     * @param companyId 企业Id
     */
    public static void certsImport(@Required Integer companyId,@Required File attachment){
        String remark = request.params.get("remark");
        //attachment(companyId,attachment);

        String fileName = attachment.getName();
        File storeFile = new File(certFilesPath + "/" + companyId + "/" + fileName);
        //对storeFile进行解压
        Files.copy(attachment, storeFile);
    }

    /**
     * 5.附件上传
     * @param attachment
     */
    public static void attachment(File attachment) throws FileNotFoundException {
        //todo: 通用的附件上传
//        new FileInputStream(attachment),
//                MimeTypes.getContentType(attachment.getName());

        String fileName = attachment.getName();
        File storeFile = new File(certFilesPath + "/"  + fileName);
        Files.copy(attachment, storeFile);
        renderJSON("{\"success\":\"ok\"}");
    }

    /**
     *  证书(压缩包文件)下载
     * @param id
     */
    public static void download(@Required String id) {
        Cert cert =  getCollection(Cert.class).findOne(new ObjectId(id)).as(Cert.class);
        notFoundIfNull(cert);
        renderBinary(new ByteArrayInputStream(VirtualFile.fromRelativePath(certFilesPath+File.separator+cert.getCertPath()).content()),cert.getCompanyName()+".zip");
    }

    /**
     * 单个二维码生成
     * @param moduleId
     */
    public static void singleCode(@Required String moduleId) throws IOException {
        /**
         * 调用sign(BigInteger sk,String moduleId)接口生成签名
         */
     /*   String str = "22223444444444444444";
        BigInteger sk = new BigInteger(str);//BigInteger类型的私钥
        Logger.info("私钥： " + sk);
        Pair pair = sign(sk,moduleId);//根据私钥、电池编号(流水号)生成签名信息

        *//**
         * 调用 generateQRcode(OutputStream fos,String moduleId,Pair pair)生成二维码
         *//*
        File file = new File(QRPath);
        OutputStream fos = new FileOutputStream(file);
        generateQRcode(fos,moduleId,pair);//生成二维码到指定输出流
        *//**
         * 调用 Images.toBase64(File image);
         * 将二维码图片转化为 base64字符串
         *//*
        Images.toBase64(file);
        fos.close();*/
    }

    /**
     * 批量二维码生成
     * @param beginModuleId
     * @param endModuleId
     */
    public static void batchCode(@Required String beginModuleId,@Required String endModuleId) throws IOException {
        List<Module> modules = StreamSupport.stream(getCollection(Module.class).find("moduleId:{$gte:#},moduleId:{$lte:#}}",beginModuleId,endModuleId).as(Module.class).spliterator(),false).collect(Collectors.toList());
        for(int i=0;i<modules.size();i++) {
            singleCode(modules.get(i).getModuleId());
        }
    }

    /**
     * 字符串转时间
     * @param time 表示时间的字符串
     * @return Date 格式化的时间
     */

    public static Date formatDate(String time){
        SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
