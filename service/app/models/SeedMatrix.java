package models;

import play.data.validation.Required;
import play.modules.jongo.BaseModel;

import java.util.Date;

/**
 * Created by xudongmei on 2017/1/14.
 */
public class SeedMatrix extends BaseModel {
    @Required
    public String matrix; //种子矩阵
    @Required
    public Date enableTime; //启动时间
    @Required
    public Date expireTime; //到期时间

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public Date getEnableTime() {
        return enableTime;
    }

    public void setEnableTime(Date enableTime) {
        this.enableTime = enableTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
