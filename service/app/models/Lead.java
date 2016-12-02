package models;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Vector;
import utils.LabeledPointBuilder;

import java.util.Date;
import java.util.List;

/**
 * 销售线索
 *
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/4/21 16:42
 */
public class Lead extends BaseModel {
    /**
     * 线索企业名称
     */
    String companyName;
    /**
     * 线索企业工商注册号
     */
    String regNo;

    /**
     * 联系人姓名
     */
    String contact;
    /**
     * 联系人职位
     */
    String title;
    /**
     * 联系人电话
     */
    String phone;
    /**
     * 行业
     */
    String category;
    /**
     * 经营范围
     */
    String opscope;
    String province;
    String city;
    /**
     * 地址
     */
    String dom;
    /**
     * 企业运营开始日期
     */
    Date opbegin;
    Date registerDate;
    /**
     * 注册资本(万元)
     */
    double regcap;
    /**
     * 地理位置
     */
    double longitude;
    double latitude;
    /**
     * 距离,Unit: meter
     */
    double dis;
    /**
     * 工商登记状态
     */
    String regStatus;
    /**
     * 企业网站
     */
    String website;
    /**
     * 企业法人
     */
    String legalPerson;
    /**
     * 企业电话
     */
    String entTel;
    /**
     * MongoDB use this for coordinate
     */
    Geo geo;
    /**
     * ES use this for coordinate
     */
    List<Double> location;
    public Vector toVector() {
        return new DenseVector(new double[]{getRegcap(), LabeledPointBuilder.Nominal(getCategory())
                , getOpmonths()});
    }

    @JSONField(serialize = false)
    public int getOpmonths() {
        if (opbegin == null) return 0;
        long l = 2592000000L;//1000*60*60*24*30;
        return (int) (Math.abs(new Date().getTime() - opbegin.getTime()) / l);
    }


    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(String regStatus) {
        this.regStatus = regStatus;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    public String getEntTel() {
        return entTel;
    }

    public void setEntTel(String entTel) {
        this.entTel = entTel;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOpscope() {
        return opscope;
    }

    public void setOpscope(String opscope) {
        this.opscope = opscope;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDom() {
        return dom;
    }

    public void setDom(String dom) {
        this.dom = dom;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getDis() {
        return dis;
    }

    public void setDis(double dis) {
        this.dis = dis;
    }

    public Date getOpbegin() {
        return opbegin;
    }

    public void setOpbegin(Date opbegin) {
        this.opbegin = opbegin;
    }

    public double getRegcap() {
        return regcap;
    }

    public void setRegcap(double regcap) {
        this.regcap = regcap;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    /**
     * 地理位置
     *
     * @author <a href="mailto:fivesmallq@gmail.com">fivesmallq</a>
     * @version Revision: 1.0
     * @date 16/8/7 下午9:19
     */
    public static class Geo {
        List<Double> coordinates;

        public List<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Double> coordinates) {
            this.coordinates = coordinates;
        }
    }
}
