package models;

import models.api.Jsonable;
import play.data.validation.Required;
import play.modules.jongo.BaseModel;

/**
 * 证书的数据模型
 * Created by xudongmei on 2016/12/13.
 */

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
     * 营业执照证书图片的Base64格式
     */
    public String  licensePicture;

    /**
     * 申请证书的状态
     * 0：未审核 1：审核通过 2：审核不通过
     */
    @Required
    public Integer status = 0;
    /**
     * 交易用私钥证书
     */
    public String tradeSK;
    /**
     * 产品二维码用私钥证书
     */
    public String productSK;


    public String certRemark;

    public Integer getCompanyType() {
        return companyType;
    }

    public void setCompanyType(Integer companyType) {
        this.companyType = companyType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicensePicture() {
        return licensePicture;
    }

    public void setLicensePicture(String licensePicture) {
        this.licensePicture = licensePicture;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



    public String getCertRemark() {
        return certRemark;
    }

    public void setCertRemark(String certRemark) {
        this.certRemark = certRemark;
    }

    public String getTradeSK() {
        return tradeSK;
    }

    public void setTradeSK(String tradeSK) {
        this.tradeSK = tradeSK;
    }

    public String getProductSK() {
        return productSK;
    }

    public void setProductSK(String productSK) {
        this.productSK = productSK;
    }
}
