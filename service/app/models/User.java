package models;

import com.alibaba.fastjson.annotation.JSONField;
import models.api.Jsonable;
import play.data.validation.Required;
import play.modules.jongo.BaseModel;

import java.util.Date;

/**
 * @author <a href="mailto:fivesmallq@gmail.com">fivesmallq</a>
 * @version Revision: 1.0
 * @date 16/11/24 下午11:46
 */
public class User extends BaseModel implements Jsonable {
    @Required
    public String userName;
    @JSONField(serialize = false)
    @Required
    public String password;
    public Date createDate;
    public Integer state;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
