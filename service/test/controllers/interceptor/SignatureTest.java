package controllers.interceptor;

import com.alibaba.fastjson.JSON;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import models.BaseModel;
import models.Customer;
import models.Lead;
import models.Error;
import models.ErrorCode;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.test.FunctionalTest;

/**
 * @author <a href="mailto:wuzhiqiang@novacloud.com">wuzq</a>
 * @version Revision: 1.0
 * @date 15/6/13 上午12:54
 */
public class SignatureTest extends FunctionalTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testValidate() throws Exception {
        Http.Response response = GET("/v2/data/websites/556afa28e2748db26f8fa735?access_key_id=1ojZyGk5rnh5PBhX2VMuUhQ==&limit=2&offset=10&signature_version=1&sort=date:desc&signature=wVc2gzRU7moKOO08c4vZUaxTSUSJgkCAd4A82UQBT6w%3D");
        System.out.println(response.out);
        Error errorAPI = JSON.parseObject(response.out.toString(), Error.class);
        Assert.assertTrue(errorAPI.success());
    }
    @Test
    public void testPostBodyParams() throws Exception {
        Customer customer = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().build().nextObject(Customer.class);
        Http.Response response = POST("/v2/data/customers/556afa28e2748db26f8fa735?access_key_id=1ojZyGk5rnh5PBhX2VMuUhQ==&signature_version=1&signature=wVc2gzRU7moKOO08c4vZUaxTSUSJgkCAd4A82UQBT6w%3D","application/json",customer.toJson());
        //todo: test
        System.out.println(response.out);
        Error errorAPI = JSON.parseObject(response.out.toString(), Error.class);
        Assert.assertTrue(errorAPI.success());
    }

    @Test
    public void testWebsiteValidate() throws Exception {
        //设置为已删除
        Lead lead = new Lead();
        lead.setId(new ObjectId("556afa28e2748db26f8fa735"));

        lead.save();
        Http.Response response = GET("/v2/data/websites/556afa28e2748db26f8fa735?access_key_id=1ojZyGk5rnh5PBhX2VMuUhQ==&limit=2&offset=10&signature_version=1&sort=date:desc&signature=wVc2gzRU7moKOO08c4vZUaxTSUSJgkCAd4A82UQBT6w%3D");
        System.out.println(response.out);
        Error errorAPI = JSON.parseObject(response.out.toString(), Error.class);
        Assert.assertTrue(errorAPI.getCode() == ErrorCode.CLIENT_RESOURCE_NOT_FOUND);

        //设置未删除

        lead.save();
        response = GET("/v2/data/websites/556afa28e2748db26f8fa735?access_key_id=1ojZyGk5rnh5PBhX2VMuUhQ==&limit=2&offset=10&signature_version=1&sort=date:desc&signature=wVc2gzRU7moKOO08c4vZUaxTSUSJgkCAd4A82UQBT6w%3D");
        System.out.println(response.out);
        errorAPI = JSON.parseObject(response.out.toString(), Error.class);
        Assert.assertTrue(errorAPI.success());

        //清除数据
        BaseModel.getCollection(Lead.class).remove(lead.getId()) ;
        response = GET("/v2/data/websites/556afa28e2748db26f8fa735?access_key_id=1ojZyGk5rnh5PBhX2VMuUhQ==&limit=2&offset=10&signature_version=1&sort=date:desc&signature=wVc2gzRU7moKOO08c4vZUaxTSUSJgkCAd4A82UQBT6w%3D");
        System.out.println(response.out);
        errorAPI = JSON.parseObject(response.out.toString(), Error.class);
        Assert.assertTrue(errorAPI.success());
    }
}