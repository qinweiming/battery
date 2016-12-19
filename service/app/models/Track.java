package models;

import models.api.Jsonable;
import play.data.validation.Required;
import play.modules.jongo.BaseModel;

import java.util.Date;

/**
 * Created by xudongmei on 2016/12/13.
 */
@SuppressWarnings("ALL")
public class Track extends BaseModel implements Jsonable {
    @Required
    public Date productTime;
    @Required
    public String productId;
    @Required
    public String srcCompany;
    @Required
    public String desCompany;
    @Required
    public String moduleId;
    @Required
    public String packageId;
    @Required
    public String carId;
    @Required
    public Integer type; // 0:电池厂 1：汽车厂 2：all
}
