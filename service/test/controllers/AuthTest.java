package controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import models.Jsonable;
import models.KeyPair;
import models.Error;
import models.ErrorCode;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import play.Logger;
import play.mvc.Http;
import play.test.FunctionalTest;

import java.util.List;

/**
 * @author <a href="mailto:wuzhiqiang@novacloud.com">wuzq</a>
 * @version Revision: 1.0
 * @date 15/4/27 下午1:13
 */
public class AuthTest extends FunctionalTest {
    private static final String clientId = "5407fd5e96e633b127b0797d";
    public static final String validAccessKey = "1UF9lzD7t/N2I1Mj2EyqKNA==";
    public static final String expiredAccessKey = "119bLa8H7rckUSZbk4/vZCA==";
    public static final String illegalAccessKey = "12p/1o/fRGIj5KpSXW1FmIw==";
    public static final String secretKey = "1cf2dab382cb130613fb8eaef37d67fd22f3693ca";

    @Before
    public void setUp() throws Exception {
        System.err.println("Before");
        KeyPair.getCollection(KeyPair.class).remove("{clientId:#}", clientId);

    }

    @After
    public void tearDown() throws Exception {
        System.err.println("tearDown");
    }

//    @Test
//    public void testGetAccessKey400() {
//        Http.Error response = POST("/v2/auth/" + clientId);
//        Error ret = Error.fromJSON(response.out.toString());
//        Assert.assertEquals(Integer.valueOf(CLIENT_ACCESS_DENIED), ret.getCode());
//        Logger.info("response:%s", response.status);
//        Logger.info("response:%s", response.out);
//    }

    @Test
    public void testGenerateKeyPair() {
        Http.Response response = POSTJSON("/v2/auth/" + clientId + "?expireDate=2505");
        assertIsOk(response);
        Logger.info("response:%s", response.status);
        Logger.info("response:%s", response.out);
        String out = response.out.toString();
        KeyPair keyPair = JSON.parseObject(out, models.KeyPair.class);
        assertNotNull(keyPair);
        assertEquals(keyPair.getClientId(), clientId);

        response = POSTJSON("/v2/auth/" + 1 + "?expireDate=1405");
        assertStatus(500,response);
        Logger.info("response:%s", response.out);
        Error error = Jsonable.fromJson(response.out.toString(), Error.class);
        assertEquals(ErrorCode.SERVER_INTERNAL_ERROR, error.getCode().intValue());
    }

    // Requests
    public static Http.Response GETJSON(Object url) {
        Http.Request request = newRequest();
        request.format = "json";
        return GET(request, url);
    }

    public static Http.Response POSTJSON(Object url) {
        Http.Request request = newRequest();
        request.format = "json";
        return POST(request, url);
    }
    public static Http.Response DELETEJSON(Object url) {
        Http.Request request = newRequest();
        request.format = "json";
        return DELETE(request, url);
    }

    @Test
    public void testGetKeyPairs() {
        //generate new keypair
        testGenerateKeyPair();
        //test success
        Http.Response response = GETJSON("/v2/auth/" + clientId);
        Logger.info("response:%s", response.status);
        Logger.info("response:%s", response.out);
        assertIsOk(response);
        String results = response.out.toString();
        List<models.KeyPair> keyPairs = JSON.parseObject(results, new TypeReference<List<models.KeyPair>>() {
        });
        assertNotNull(keyPairs);
        models.KeyPair keyPair = keyPairs.get(0);
        String accessKey = keyPair.getAccessKey();
        assertNotNull(accessKey);

        //test error client id
        String errorId="575c3f95445e3007b5ab3326";
        response = GETJSON("/v2/auth/" + errorId);
        Logger.info("response:%s", response.status);
        Logger.info("response:%s", response.out);
        assertIsNotFound(response);
        Error error = Jsonable.fromJson(response.out.toString(), Error.class);
        assertEquals(ErrorCode.CLIENT_RESOURCE_NOT_FOUND, error.getCode().intValue());

        //test error format client id
        errorId="error_param";
        response = GETJSON("/v2/auth/" + errorId);
        Logger.info("response:%s", response.status);
        Logger.info("response:%s", response.out);
        assertStatus(400,response);
        error = Jsonable.fromJson(response.out.toString(), Error.class);
        assertEquals(ErrorCode.CLIENT_FORMAT_ERROR, error.getCode().intValue());
    }

    @Test
    public void testGetDeleteAccessKey() {
        //generate new keypair
        testGenerateKeyPair();

        String jsonRet = GETJSON("/v2/auth/" + clientId).out.toString();
        Logger.info(jsonRet);
        String results =jsonRet;
        List<KeyPair> keyPairs = JSON.parseObject(results, new TypeReference<List<KeyPair>>() {
        });
        KeyPair keyPair = keyPairs.get(0);
        String accessKey = keyPair.getAccessKey();

        //test delete success
        Http.Response response = DELETEJSON("/v2/auth/" + clientId + "/" + accessKey);
        Logger.info("response:%s", response.status);
        Logger.info("response:%s", response.out);
        assertIsOk(response);

        //test delete null
        response = DELETEJSON("/v2/auth/" + clientId + "/" + accessKey);
        Logger.info("response:%s", response.status);
        Logger.info("response:%s", response.out);
        assertIsNotFound(response);
        Error error = Jsonable.fromJson(response.out.toString(), Error.class);
        assertEquals(ErrorCode.CLIENT_RESOURCE_NOT_FOUND, error.getCode().intValue());
    }

    @Test
    @Ignore
    public void testUpdateAccessKey() {
        String jsonRet = (POST("/v2/auth/" + clientId + "?auth=novadata").out.toString());
        Logger.info(jsonRet);
        KeyPair keyPair = JSON.parseObject(jsonRet, KeyPair.class);
        String accessKey = keyPair.getAccessKey();
        Http.Response response = POST("/v2/auth/" + clientId + "/disable?accessKey=" + accessKey + "&auth=novadata");
        Logger.info("response:%s", response.status);
        Logger.info("response:%s", response.out);
        Error error1 = Jsonable.fromJson(response.out.toString(), Error.class);
    }

    @Test
    @Ignore
    public void testGetAccessKeys() {
        String jsonRet = (GET("/v2/auth/" + clientId + "?auth=novadata").out.toString());
        Logger.info(jsonRet);
    }

    @Test
    @Ignore
    public void testValidateAccessKey() {
        String jsonRet = GET("/v2/auth/" + clientId + "/validate?accessKey=" + validAccessKey).out.toString();
        Logger.info(jsonRet);
        Error error = Jsonable.fromJson(jsonRet, Error.class);
        assertEquals(ErrorCode.SUCCESS, error.getCode().intValue());

        jsonRet = GET("/v2/auth/" + clientId + "/validate?accessKey=" + expiredAccessKey).out.toString();
        Logger.info(jsonRet);
        error = Jsonable.fromJson(jsonRet, Error.class);
        assertEquals(ErrorCode.CLIENT_AUTH_TOKEN_EXPIRED, error.getCode().intValue());
        String jsonRetIllegalAccessKey = GET("/v2/auth/" + clientId + "/validate?accessKey=" + illegalAccessKey).out.toString();
        Logger.info("jsonRetIllegalAccessKey:%s",jsonRetIllegalAccessKey);
        error = Jsonable.fromJson(jsonRetIllegalAccessKey, Error.class);
        assertEquals(ErrorCode.CLIENT_AUTH_ERROR, error.getCode().intValue());
    }
}
