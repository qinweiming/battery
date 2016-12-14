package models;

import models.api.Jsonable;
import play.data.validation.Required;
import play.modules.jongo.BaseModel;

/**
 * Created by xudongmei on 2016/12/13.
 */

public class Cert extends BaseModel implements Jsonable {
    @Required
    public Integer companyType; //企业类型 0：电池厂 1：汽车厂
    @Required
    public String companyName; //企业名称
    @Required
    public String creditCode; //社会信用码
    public String contact; //联系人
    @Required
    public String phone; //联系电话
    @Required
    public String email; //联系邮箱
    @Required
    public String uploadCertName; //法人证书的地址---文件读取？
    @Required
    public Integer status = 0; //申请证书的状态 0：未审核 1：审核通过 2：审核不通过

    /**
     * 证书的其他属性
     */
}
