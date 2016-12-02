package controllers.v2;

import com.alibaba.fastjson.JSON;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import models.Customer;
import models.Lead;
import models.Recommend;
import org.bson.types.ObjectId;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import play.Logger;
import play.mvc.Http;
import play.test.FunctionalTest;
import utils.SignatureUtil;
import utils.Utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/4/30 22:23
 */
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
//todo: Compress annotation changed response.out to gzip binary stream
public class DataTest  extends FunctionalTest {

    public static final String SECRET_KEY = "146bcbd5448a9bc31a88ebd1da4037f11268912c5";
    public static final String ACCESS_KEY_ID = "1oKIHohJJtA/6NxWHesCHOw==";
    private String leadId= "5724440cd53a97ad197c340c";
    public static final String CLIENT_ID = "53ea055e0cf2921b57a24e2c";
    public static final String RECOMMEND_ID = "53ea055e0cf2921b57a24e2c";

    //@Before
    public void setUp() throws Exception {

        EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().build();
        Lead lead = enhancedRandom.nextObject(Lead.class);
        lead.setId(new ObjectId(leadId));
        lead.save();


        Recommend recommend = enhancedRandom.nextObject(Recommend.class);
        recommend.setClientId(CLIENT_ID);
        recommend.setLeadId(leadId);
        recommend.setId(RECOMMEND_ID);
        recommend.setLead(lead);
        recommend.save();
        List<Recommend> recommends = enhancedRandom.nextObjects(Recommend.class, 99);
        recommends.forEach(recommend1 -> {
            recommend1.setClientId(CLIENT_ID);
            ObjectId leadId = ObjectId.get();
            recommend1.setLeadId(leadId.toHexString());
            recommend1.setId(new ObjectId());
            Lead lead1 = enhancedRandom.nextObject(Lead.class);
            lead.setId(leadId);
            recommend1.setLead(lead1);
            recommend1.save();
        });
    }

    //@After
    public void tearDown() throws Exception {
        Lead.getCollection(Lead.class).remove(new ObjectId(leadId));
        Recommend.getCollection(Recommend.class).remove(new ObjectId(RECOMMEND_ID));
    }
    @Test
    public void recommend() throws Exception {

        String path = "/v2/leads/" + CLIENT_ID + "/recommends";
        Map<String ,String  > params=new HashMap<>();
//        params.put("debug","true");
        params.put("access_key_id", ACCESS_KEY_ID);
        params.put("signature_version","1");
        String  signature=SignatureUtil.computeSignature(SECRET_KEY,"GET",path,params);
        params.put("signature",signature);
        String queryString = Utility.map2QueryString(params);
        Http.Response response = GET(path+"?"+queryString);
        assertIsOk(response);
        System.err.printf("response:%s \n", response.out);
        assertContentType("application/json; charset=utf-8",response);
        String json = response.out.toString("UTF-8");
        List<Recommend> recommends = JSON.parseArray(json, Recommend.class);
        assertNotNull(recommends);
        assertTrue(recommends.get(0).getRating() >= recommends.get(1).getRating());
        assertEquals(20,recommends.size());

    }

    @Test
    public void category() throws Exception {
        //todo: unit test
        String path = "/v2/leads/dict/category";
        Map<String ,String  > params=new HashMap<>();
        params.put("debug","true");
        params.put("access_key_id", ACCESS_KEY_ID);
        params.put("signature_version","1");
        String  signature=SignatureUtil.computeSignature(SECRET_KEY,"GET",path,params);
        params.put("signature",signature);
        String queryString = Utility.map2QueryString(params);
        Http.Response response = GET(path+"?"+queryString);
        System.out.println(response.out.toString("UTF-8"));
        assertIsOk(response);

    }

    @Test
    public void recommendBootstrap() throws Exception {

        String path = "/v2/leads/" + CLIENT_ID + "/recommends";
        Map<String ,String  > params=new HashMap<>();
      params.put("category","其他");
        params.put("province","河南省");
       params.put("sort_by","near");
        params.put("longitude","103.8924564599");
        params.put("latitude","36.05");
        params.put("access_key_id", ACCESS_KEY_ID);
        params.put("signature_version","1");
        String  signature=SignatureUtil.computeSignature(SECRET_KEY,"GET",path,params);
        params.put("signature",signature);
        String queryString = Utility.map2QueryString(params);
        Http.Response response = GET(path+"?"+queryString);

        assertIsOk(response);
        System.err.printf("response:%s \n", response.out);
        assertContentType("application/json; charset=utf-8",response);
        String json = response.out.toString("UTF-8");
        List<Recommend> recommends = JSON.parseArray(json, Recommend.class);
        assertNotNull(recommends);

        assertEquals(20,recommends.size());

    }
    @Test
    public void omnibox() throws Exception {

        String path = "/v2/leads/" + CLIENT_ID + "/omnibox";
        Map<String ,String  > params=new HashMap<>();
        params.put("keyword","软件");
        params.put("longitude","103.8924564599");
        params.put("latitude","36.05");
        params.put("access_key_id", ACCESS_KEY_ID);
        params.put("signature_version","1");
        String  signature=SignatureUtil.computeSignature(SECRET_KEY,"GET",path,params);
        params.put("signature",signature);
        String queryString = Utility.map2QueryString(params);
        Http.Response response = GET(path+"?"+queryString);

        assertIsOk(response);
        System.err.printf("response:%s \n", response.out);
        assertContentType("application/json; charset=utf-8",response);
        String json = response.out.toString("UTF-8");
        List<Recommend> recommends = JSON.parseArray(json, Recommend.class);
        assertNotNull(recommends);

        assertEquals(20,recommends.size());

    }
    @Test
    public void omniboxMissing() throws Exception {

        String path = "/v2/leads/" + CLIENT_ID + "/omnibox";
        Map<String ,String  > params=new HashMap<>();
        params.put("keyword","五金交电软件");
        params.put("longitude","103.8924564599");
        params.put("latitude","36.05");
        params.put("access_key_id", ACCESS_KEY_ID);
        params.put("signature_version","1");
        String  signature=SignatureUtil.computeSignature(SECRET_KEY,"GET",path,params);
        params.put("signature",signature);
        String queryString = Utility.map2QueryString(params);
        Http.Response response = GET(path+"?"+queryString);

        assertIsNotFound(response);
        System.err.printf("response:%s \n", response.out);


    }
    @Test

    public void tags() throws Exception {

        Map<String ,String  > params=new HashMap<>();
//        params.put("debug","true");
        params.put("access_key_id", ACCESS_KEY_ID);
        params.put("signature_version","1");
        params.put("clientId", CLIENT_ID);
        params.put("tag","useful");
        String path = "/v2/leads/" + leadId + "/tags";
        String  signature=SignatureUtil.computeSignature(SECRET_KEY,"POST",path,params);
        params.put("signature",signature);
        Http.Response response = POST(path,params);
        assertIsOk(response);
        System.err.printf("response:%s \n", response.out);
        assertContentType("application/json; charset=utf-8",response);
        String json = response.out.toString("UTF-8");
        //XXX no test for error
        //Error error1 = JSON.parseObject(json, Error.class);
        //assertEquals(ErrorCode.SUCCESS, error1.getCode().intValue());
    }

    @Test

    public void detail() throws Exception {
        Map<String ,String  > params=new HashMap<>();
        params.put("access_key_id", ACCESS_KEY_ID);
        params.put("signature_version","1");

        String path = "/v2/leads/" + leadId;
        String  signature=SignatureUtil.computeSignature(SECRET_KEY,"GET",path,params);
        params.put("signature",signature);
        String queryString = Utility.map2QueryString(params);
        Http.Response response = GET(path+"?"+queryString);
        assertIsOk(response);
        System.err.printf("response:%s \n", response.out);
        assertContentType("application/json; charset=utf-8",response);
        String json = response.out.toString("UTF-8");
        Lead lead = JSON.parseObject(json, Lead.class);
        assertEquals(leadId,lead.getIdAsStr());
        path = "/v2/leads/" + new ObjectId().toHexString();
        params.remove("signature");
        signature = SignatureUtil.computeSignature(SECRET_KEY, "GET", path, params);
        params.put("signature", signature);
        queryString = Utility.map2QueryString(params);
        response = GET(path + "?" + queryString);
        assertIsNotFound(response);

    }
    @Test
    public void customer() throws Exception {
        Map<String ,String  > params=new HashMap<>();
        params.put("access_key_id", ACCESS_KEY_ID);
        params.put("signature_version","1");
        params.put("debug",String .valueOf(true));
        params.put("clientId", CLIENT_ID);
        String customerId=Customer.getCollection(Customer.class).findOne("{clientId:#}",CLIENT_ID).as(Customer.class).getCustomerId();
        String path = "/v2/customers/" + customerId;
        String  signature=SignatureUtil.computeSignature(SECRET_KEY,"PUT",path,params);
        params.put("signature",signature);
        String queryString = Utility.map2QueryString(params);
        EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().build();
        Customer customer =enhancedRandom.nextObject(Customer.class);
        customer.setClientId(CLIENT_ID);
        customer.setCustomerId(customerId);
        Http.Response response = PUT(path+"?"+queryString, "application/json",customer.toJson());
        assertIsOk(response);
        System.err.printf("response:%s \n", response.out);
        assertContentType("application/json; charset=utf-8",response);
        String json = response.out.toString("UTF-8");
        //XXX no test for error
        //Error error1 = JSON.parseObject(json, Error.class);
        //assertEquals(ErrorCode.SUCCESS, error1.getCode().intValue());

    }
    @Test
    public void customers() throws Exception {
        Map<String ,String  > params=new HashMap<>();
        params.put("access_key_id", ACCESS_KEY_ID);
        params.put("signature_version","1");
        params.put("debug",String .valueOf(true));
        params.put("clientId", CLIENT_ID);


        String path = "/v2/customers";
        String  signature=SignatureUtil.computeSignature(SECRET_KEY,"POST",path,params);
        params.put("signature",signature);
        String queryString = Utility.map2QueryString(params);
        EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().build();
        List<Customer> customers =enhancedRandom.nextObjects(Customer.class,2);
        customers.forEach(customer -> customer.setClientId(CLIENT_ID));
        Logger.info(JSON.toJSONString(customers));
        Http.Response response = POST(path+"?"+queryString, "application/json",JSON.toJSONString(customers));
        assertIsOk(response);
        System.err.printf("response:%s \n", response.out);
        assertContentType("application/json; charset=utf-8",response);
        String json = response.out.toString("UTF-8");
        //XXX no test for error
        //Error error1 = JSON.parseObject(json, Error.class);
        //assertEquals(ErrorCode.SUCCESS, error1.getCode().intValue());

    }
}
