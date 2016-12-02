package controllers.v2;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import controllers.BaseController;
import controllers.interceptor.*;
import models.Client;
import models.KeyPair;
import models.Error;
import models.ErrorCode;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.jongo.FindOne;
import org.jongo.MongoCursor;
import play.Logger;
import play.Play;
import play.exceptions.UnexpectedException;
import play.libs.Codec;
import play.libs.Crypto;
import play.mvc.With;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@With({APIRateLimiter.class, RequestLog.class, ExceptionCatcher.class, APIResponseWrapper.class, Compress.class})
public class Auth extends BaseController {
    final static String SEPARATOR = "&";
    final static String VERSION = "1";
    final static int DEFAULT_EXPIRE = 1;// 1 year

    /**
     * 注册client并生成一个keypair
     * @param client_name client名称
     *
     */
    public static void register(String client_name){
        if (client_name.isEmpty()) {
            paramsMiss("client_name");
        }
        long found = Client.getCollection(Client.class).count("{clientName:#}", client_name);
        if(found <=0) {
            KeyPair keyPair = _generateKeyPair(new ObjectId().toString(), null, client_name);
            Client client = new Client();
            client.setClientName(client_name);
            client.setClientId(keyPair.getClientId());
            client.setKeyPairs(Arrays.asList(keyPair));
            client.save();
            renderJSON(keyPair.toJson());
        }else{
            errorJson(ErrorCode.CLIENT_EXIST_ALREADY,"client name has exist already :"+client_name);
        }
    }
    /**
     * 生成secretKey与accessKey对<br>
     * {@literal secretKey rule:  version+hmac( uuid  +  separator  +  accessKey + separator +expiredate  + separator + clientId)}
     * <br>
     * <pre><code> accessKey rule 1.0:  version+aes( clientId+byte(yyMM))} ,长度为25; </code></pre>
     * <pre><code> accessKey rule 2.0:  version+aes( clientId  +  separator  +  expiredate </code></pre>
     * <b>注：java的AES实现中，当明文小于等于15byte时，密文为16byte .其中，ObjectId为12 Byte，将expire date的yyMM转换为short,为2Byte</b>
     * <p>
     * 参考了如下文章中的实现思路：
     * {@literal http://stackoverflow.com/questions/1626575/best-practices-around-generating-oauth-tokens}
     * 与
     * {@literal https://github.com/stepanowon/OAuth_20/blob/master/oauth2provider/src/com/multi/oauth2/provider/util/OAuth2AccessTokenService.java}
     * </p>
     *  @param client_id   对于1.0版本, clientId为member id且必须是objectId
     * @param expire_date 默认为当前日期后1年
     * @param clientName
     */
    public static void generateKeyPair(String client_id, String expire_date, String clientName) {


        if (client_id.isEmpty()) {
            paramsMiss("client_id");
        }
        if (KeyPair.getCollection(KeyPair.class).count("{clientId:#}", client_id) == 2) {
            errorJson(ErrorCode.SERVER_RESOURCE_LIMIT, "exceed max key pair count 2");
        }
        KeyPair keyPair = _generateKeyPair(client_id, expire_date, clientName);
        renderJSON(keyPair.toJson());

    }

    private static KeyPair _generateKeyPair(String clientId, String expireDate, String clientName) {
        //todo: validate expireDate date format ,must be yyMM
        if (expireDate == null || expireDate.length() < 1) {
            expireDate = DateTime.now().plusYears(DEFAULT_EXPIRE)
                    .toString("yyMM");
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(15).put(new ObjectId(clientId).toByteArray()).putShort(12, Short.parseShort(expireDate));
        String accessKey = VERSION
                + encryptAES(byteBuffer.array());
        String secretKey = VERSION
                + Crypto.sign(Codec.UUID() + SEPARATOR + accessKey
                + SEPARATOR + expireDate + SEPARATOR + clientId);

        KeyPair keyPair = new KeyPair();
        keyPair.setAccessKey(accessKey);
        keyPair.setClientId(clientId);
        keyPair.setClientName(clientName);
        keyPair.setExpireDate(new Date(DateTime.parse(expireDate).getMillis()));
        keyPair.setSecretKey(secretKey);
        keyPair.save();
        return keyPair;
    }

    /**
     * encrypt aes
     *
     * @param text
     * @return base64 encoded cipher
     */
    private static String encryptAES(byte[] text) {
        try {
            byte[] ex = Play.configuration.getProperty("application.secret").substring(0, 16).getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(ex, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] value = cipher.doFinal(text);
            return Codec.encodeBASE64(value);
        } catch (Exception var5) {
            throw new UnexpectedException(var5);
        }
    }

    /**
     * decryptAES
     *
     * @param value base64 encoded cipher
     * @return
     */
    public static byte[] decryptAES(String value) {
        try {
            byte[] ex = Play.configuration.getProperty("application.secret").substring(0, 16).getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(ex, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, skeySpec);
            return cipher.doFinal(Codec.decodeBASE64(value));
        } catch (Exception e) {
            Logger.error("decrypt AES exception", e);
            return null;
        }
    }

    /**
     * 解密objectId
     *
     * @param value
     * @return
     */
    private static String decryptObjectId(String value) {
        try {
            byte[] ex = Play.configuration.getProperty("application.secret").substring(0, 16).getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(ex, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, skeySpec);

            return new ObjectId(cipher.doFinal(Codec.decodeBASE64(value))).toString();
        } catch (Exception var5) {
            throw new UnexpectedException(var5);
        }
    }

    /**
     * 使用AES加密objectId,并将结果进行Base64编码
     *
     * @param objectId
     * @return
     */
    private static String encryptObjectId(String objectId) {
        try {
            byte[] ex = Play.configuration.getProperty("application.secret").substring(0, 16).getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(ex, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(1, skeySpec);
            return Codec.encodeBASE64(cipher.doFinal(new ObjectId(objectId).toByteArray()));
        } catch (Exception var5) {
            throw new UnexpectedException(var5);
        }
    }



    /**
     * 获取用户的key pairs <br>
     *
     * @param client_id
     */
    public static void getKeyPairs(String client_id) {

        if (!ObjectId.isValid(client_id))
            paramsError("client_id");


        MongoCursor<KeyPair> keyList = KeyPair.getCollection(KeyPair.class).find("{clientId:#}", client_id).projection("{secretKey:0}").
                as(KeyPair.class);

        if (keyList.hasNext()) {
            renderJSON(JSON.toJSONString(StreamSupport.stream(keyList.spliterator(), false).collect(Collectors.toList())));
        } else {
            notFoundEx(client_id);
        }


    }

    /**
     * 删除clientId与accessKey对
     *
     * @param client_id
     * @param access_key
     */
    public static void deleteKeyPair(String client_id, String access_key) {
        if (!ObjectId.isValid(client_id)) paramsError(client_id);
        long result = KeyPair.getCollection(KeyPair.class).remove("{clientId:#,accessKey:#}",
                client_id, access_key).getN();
        if (result > 0) {
            ok();
        } else {
            notFoundEx(client_id, access_key);
        }

    }

    /**
     * 禁用clientId与accessKey对
     *
     * @param client_id
     * @param access_key
     */
    public static void disableKeyPair(String client_id, String access_key) {

        KeyPair keyPair = KeyPair.getCollection(KeyPair.class).findOne("{clientId:#, accessKey:#}", client_id,
                access_key).as(KeyPair.class);
        if (keyPair == null) {
            notFoundEx(client_id, access_key);
        } else {
            keyPair.setEnabled(false);
            keyPair.save();
            ok();
        }

    }

    /**
     * 启用secretKey与accessKey对
     *
     * @param client_id
     * @param access_key
     */
    public static void enableKeyPair(String client_id, String access_key) {
        validateAccessKey(client_id, access_key);

        KeyPair keyPair = KeyPair.getCollection(KeyPair.class).findOne("{clientId:#, accessKey:#}", client_id,
                access_key).as(KeyPair.class);
        if (keyPair == null) {
            notFoundEx(client_id, access_key);
        } else {
            keyPair.setEnabled(true);
            keyPair.save();
            ok();
        }

    }

    public static void validateAccessKey(String client_id, String access_key) {
        Error error = new Error();
        try {
            byte[] text = decryptAES(access_key.substring(1));
            if (text == null) {
                error.setCodeWithDefaultMsg(ErrorCode.CLIENT_AUTH_ERROR);
                unauthorized(error);
            }
            assert text != null;
            String objectId = new ObjectId(ByteBuffer.allocate(12).put(text, 0, 12).array()).toString();
            short expireDate = ByteBuffer.wrap(text).getShort(12);

            error.setCodeWithDefaultMsg(ErrorCode.SUCCESS);
            if (!objectId.equalsIgnoreCase(client_id)) {
                error.setCodeWithDefaultMsg(ErrorCode.CLIENT_AUTH_ERROR);
                unauthorized(error);
            }
            if (expireDate < Short.parseShort(DateTime.now().toString("yyMM"))) {
                error.setCodeWithDefaultMsg(ErrorCode.CLIENT_AUTH_TOKEN_EXPIRED);
                unauthorized(error);
            }
            renderJSON(error.toJson());
        } catch (Exception e) {
            Logger.info("validateAccessKey Exception", e);
            error.setCodeWithDefaultMsg(ErrorCode.CLIENT_AUTH_ERROR);
            unauthorized(error);
        }
    }

}
