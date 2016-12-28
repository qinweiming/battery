package controllers.v1;

import controllers.api.API;
import play.Play;
import play.libs.Files;

import java.io.File;

/**
 * 上传下载工具类
 * Created by xudongmei on 2016/12/15.
 */
public class FileUtils extends API{
    public static String storePath = Play.configuration.getProperty("attachments.path");
    /**
     * @上传
     * @param attachment
     * @description 需要上传的文件
     */
    public static void upload(File attachment) {
        String fileName = attachment.getName();
        File storeFile = new File(storePath + "/" + fileName);
        Files.copy(attachment, storeFile);
    }

    /**
     * 下载
     */
    public static void download(String fileName) {
    }

}
