package utils;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import play.Play;
import models.Lead;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/8/21 16:52
 */
public class ESUtilsTest {
    String[] companyNames ={"金明","管理","有限公司","服装"};
    String[] provinces ={"河南省","广东省","浙江省","北京市"};
    Map<String,String> citys = ImmutableMap.of("河南省","郑州市","广东省","广州市","浙江省","杭州市","北京市","北京市");
    Map<String,Double> longitudes = ImmutableMap.of("河南省",113.625368,"广东省",113.266531,"浙江省",120.152792,"北京市",116.407526);
    Map<String,Double> latitudes = ImmutableMap.of("河南省",34.746600  ,"广东省",23.132191,"浙江省",30.267447,"北京市",39.904030);
    String[] categorys ={"35-专用设备制造业","39-计算机、通信和其他电子设备制造业","24-文教、工美、体育和娱乐用品制造业","18-纺织服装、服饰业"};

    @Before
    public  void setUp(){
        Play.readConfiguration();
        Play.mode= Play.Mode.DEV;
    }
    //    1. 只有公司名全称
//    2.只有公司名部分
//    3.公司名称+省
//    4.公司名称+省+市
//    5.公司名称+省+市+坐标+100km
//    6.公司名称+省+市+坐标+50km
//    7.公司名称+省+坐标+100km
//    8.公司名称+省+坐标+50km
//    9.公司名称+行业+省+坐标+50km
    //9.坐标+50km
    //9.公司名称+坐标+50km
    @Test(timeout = 15000)
    public void searchLeadGeo() throws Exception {
        //String companyName, String category, String province, String city, Double longitude, Double latitude, String sort, long offset, long limit
        int i= new Random().nextInt(provinces.length);
        List<Lead> leads =ESUtils.searchLead(null,null,null,null,longitudes.get(provinces[i]),latitudes.get(provinces[i]),null,i*10,10);
        assertNotNull(leads);
        assertEquals(leads.size(),10);
        printLeads(leads);
    }
    @Test(timeout = 15000)
    public void searchLeadCompanyName_Geo() throws Exception {
        //String companyName, String category, String province, String city, Double longitude, Double latitude, String sort, long offset, long limit
        int i= new Random().nextInt(provinces.length);
        int j=new Random().nextInt(provinces.length)   ;
        List<Lead> leads =ESUtils.searchLead(companyNames[i],null,null,null,longitudes.get(provinces[j]),latitudes.get(provinces[j]),null,i*10,10);
        assertNotNull(leads);
        assertEquals(leads.size(),10);
    }
    @Test(timeout = 15000)
    public void searchLeadCompanyName() throws Exception {
        //String companyName, String category, String province, String city, Double longitude, Double latitude, String sort, long offset, long limit
        int i= new Random().nextInt(companyNames.length);
        List<Lead> leads =ESUtils.searchLead(companyNames[i],null,null,null,null,null,null,i*10,10);
        assertNotNull(leads);
        assertEquals(leads.size(),10);
    }
    @Test(timeout = 15000)
    public void searchLeadCompanyName_Province() throws Exception {
        //String companyName, String category, String province, String city, Double longitude, Double latitude, String sort, long offset, long limit
        int i= new Random().nextInt(companyNames.length);
        int j=new Random().nextInt(provinces.length)   ;
        List<Lead> leads =ESUtils.searchLead(companyNames[i],null,provinces[j],null,null,null,null,i*10,10);
        assertNotNull(leads);
        printLeads(leads);
        assertEquals(leads.size(),10);
    }
    @Test(timeout = 15000)
    public void searchLeadCompanyName_Province_City() throws Exception {
        //String companyName, String category, String province, String city, Double longitude, Double latitude, String sort, long offset, long limit
        int i= new Random().nextInt(companyNames.length);
        int j=new Random().nextInt(provinces.length)   ;
        List<Lead> leads = ESUtils.searchLead(companyNames[i], null, provinces[j], citys.get(provinces[j]), null, null, null, i * 10, 10);
        assertNotNull(leads);
        printLeads(leads);
        assertEquals(leads.size(),10);
    }
    void printLeads(List<Lead> leads){
        leads.forEach(lead -> System.out.println(lead.toPrettyJson()));
    }
    @Test(timeout = 15000)
    public void searchLeadCompanyName_Province_City_Geo() throws Exception {
        //String companyName, String category, String province, String city, Double longitude, Double latitude, String sort, long offset, long limit
        int i= new Random().nextInt(companyNames.length);
        int j=new Random().nextInt(provinces.length)   ;
        Double longitude ;
        List<Lead> leads = ESUtils.searchLead(companyNames[i], null, provinces[j], citys.get(provinces[j]), longitudes.get(provinces[j]), latitudes.get(provinces[j]), null, i * 10, 10);
        assertNotNull(leads);
        printLeads(leads);
        assertEquals(leads.size(),10);
    }
    @Test(timeout = 15000)
    public void searchLeadCompanyName_Province_Geo() throws Exception {
        //String companyName, String category, String province, String city, Double longitude, Double latitude, String sort, long offset, long limit
        int i= new Random().nextInt(companyNames.length);
        int j=new Random().nextInt(provinces.length)   ;
        Double longitude ;
        List<Lead> leads = ESUtils.searchLead(companyNames[i], null, null, citys.get(provinces[j]), longitudes.get(provinces[j]), latitudes.get(provinces[j]), null, i * 10, 10);
        assertNotNull(leads);
        assertEquals(leads.size(),10);
    }
}