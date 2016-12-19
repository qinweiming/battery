package models;

import models.api.Jsonable;
import play.data.validation.Required;
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
    public File legalPersonCert;
    /**
     * 申请证书的状态
     * 0：未审核 1：审核通过 2：审核不通过
     */
    @Required
    public Integer status = 0;
    public String certAddress;
    public Date modifyTime;
}
