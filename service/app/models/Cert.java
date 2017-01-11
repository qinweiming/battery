package models;

import models.api.Jsonable;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.modules.jongo.BaseModel;

import java.io.File;
import java.util.Date;

/**
 * 证书的数据模型
 * Created by xudongmei on 2016/12/13.
 */
@SuppressWarnings("ALL")
public class Cert extends BaseModel implements Jsonable {
    /**
     * 企业类型 0：电池厂 1：汽车厂
      */
    @Required
    public Integer companyType;

    @Required
    public String companyName;

    @Required
    public String companyId;
    /**
     * 社会信用码
     */
    @Required
    public String creditCode;
    /**
     * 厂商代码（汽车厂不需要填写）
     */
    @Required
    public String vendorCode;

    public String contact;

    @Required
    public String phone;

    @Required
    public String email;
    /**
     * 法人证书
     */
    public Blob legalPersonCert;
    /**
     * 法人证书名称
     */
    public String certName;
    /**
     * 申请证书的状态
     * 0：未审核 1：审核通过 2：审核不通过
     */
    @Required
    public Integer status = 0;
    /**
     * 法人证书服务器保存地址
     */
    public String certPath;

    public String certRemark;

    /**
     * createTime & modifyTime与数据库中的_created & _modified字段重复,这两个字段保留？
     */
    public Date createTime;

    public Date modifyTime;
}
