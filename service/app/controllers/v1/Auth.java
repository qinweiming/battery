package controllers.v1;

import com.alibaba.fastjson.JSON;
import controllers.interceptor.APIResponseWrapper;
import controllers.interceptor.RequestLog;
import models.KeyPair;
import models.v1.Response;
import models.v1.ResponseCode;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.jongo.MongoCursor;
import play.Logger;
import play.Play;
import play.exceptions.UnexpectedException;
import play.libs.Codec;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.With;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@With({RequestLog.class, APIResponseWrapper.class})
public class Auth extends Controller {
     final static String SEPARATOR = "&";
     final static String VERSION = "1";
     final static int DEFAULT_EXPIRE = 1;// 1 year

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
     *
     * @param clientId   对于1.0版本, clientId为member id且必须是objectId
     * @param expireDate 默认为当前日期后1年
     */
    public static void generateKeyPair(String clientId, String expireDate) {
        Response response = new Response();
        try {
            response.setCode(ResponseCode.SUCCESS);
            if (clientId.isEmpty()) {
                response.setParameterMiss("clientId");
                renderText(response.toJson());
            }
            if (KeyPair.getCollection(KeyPair.class).count("{clientId:#}", clientId) == 2) {
                response.setCodeMsg(ResponseCode.SERVER_RESOURCE_LIMIT, "exceed max key pair");
                renderText(response.toJson());
            }
            KeyPair keyPair = _generateKeyPair(clientId, expireDate);
            response.setData(keyPair.toJson());
            renderJSON(response.toJson());
        } catch (Exception e) {
            response.setCodeWithDefaultMsg(ResponseCode.SERVER_INTERNAL_ERROR);
            response.setDetailWithExecption(e);
            renderText(response.toJson());
        }
    }

    private static KeyPair _generateKeyPair(String clientId, String expireDate) {
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
     * 生成secretKey与accessKey对<br>
     * 1年后过期
     *
     * @param clientId
     */
    public static void generateKeyPair(String clientId) {
        generateKeyPair(clientId, null);
    }

    /**
     * 获取用户的key pairs <br>
     *
     *
     * @param clientId
     */
    public static void getKeyPairs(String clientId) {
        Response response = new Response();
        try {
            MongoCursor<KeyPair> keyList = KeyPair.getCollection(KeyPair.class).find("{clientId:#}", clientId).projection("{secretKey:0}").
                    as(KeyPair.class);
            if (keyList.hasNext()) {
                response.setCode(ResponseCode.SUCCESS);
                response.setData(JSON.toJSONString(StreamSupport.stream(keyList.spliterator(),false).collect(Collectors.toList())));
            } else {
                response.setCodeWithDefaultMsg(ResponseCode.CLIENT_RESOURCE_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setCodeWithDefaultMsg(ResponseCode.SERVER_INTERNAL_ERROR);
        }
        renderJSON(response.toJson());
    }

    /**
     * 删除clientId与accessKey对
     *
     * @param clientId
     * @param accessKey
     */
    public static void deleteKeyPair(String clientId, String accessKey) {
        Response response = new Response();
        response.setCode(ResponseCode.SUCCESS);
        try {
            long result = KeyPair.getCollection(KeyPair.class).remove("{clientId:#,accessKey:#}",
                    clientId, accessKey).getN();
            if (result > 0) {

                response.setCode(ResponseCode.SUCCESS);
            } else {
                response.setCodeWithDefaultMsg(ResponseCode.CLIENT_RESOURCE_NOT_FOUND);
            }
        } catch (Exception e) {
            response.setCodeWithDefaultMsg(ResponseCode.SERVER_INTERNAL_ERROR);
        }
        renderText(response.toJson());

    }

    /**
     * 禁用clientId与accessKey对
     *
     * @param clientId
     * @param accessKey
     */
    public static void disableKeyPair(String clientId, String accessKey) {
        Response response = new Response();
        response.setCode(ResponseCode.SUCCESS);
        try {
            KeyPair keyPair = KeyPair.getCollection(KeyPair.class).findOne("{clientId:#, accessKey:#}", clientId,
                    accessKey).as(KeyPair.class);
            keyPair.setEnabled(false);
           keyPair.save();
        } catch (Exception e) {
            response.setCodeWithDefaultMsg(ResponseCode.SERVER_INTERNAL_ERROR);
        }
        renderText(response.toJson());

    }

    /**
     * 启用secretKey与accessKey对
     *
     * @param clientId
     * @param accessKey
     */
    public static void enableKeyPair(String clientId, String accessKey) {
        validateAccessKey(clientId, accessKey);
        Response response = new Response();
        response.setCode(ResponseCode.SUCCESS);
        try {
            KeyPair keyPair = KeyPair.getCollection(KeyPair.class).findOne("{clientId:#, accessKey:#}", clientId,
                    accessKey).as(KeyPair.class);
            keyPair.setEnabled(true);
            keyPair.save();
        } catch (Exception e) {
            response.setCodeWithDefaultMsg(ResponseCode.SERVER_INTERNAL_ERROR);
        }
        renderText(response.toJson());
    }

    public static void validateAccessKey(String clientId, String accessKey) {
        Response response = new Response();
        try {
            byte[] text = decryptAES(accessKey.substring(1));
            if (text == null) {
                response.setCodeWithDefaultMsg(ResponseCode.CLIENT_AUTH_ERROR);
                renderText(response.toJson());
            }
            assert text != null;
            String objectId = new ObjectId(ByteBuffer.allocate(12).put(text, 0, 12).array()).toString();
            short expireDate = ByteBuffer.wrap(text).getShort(12);

            response.setCodeWithDefaultMsg(ResponseCode.SUCCESS);
            if (!objectId.equalsIgnoreCase(clientId)) {
                response.setCodeWithDefaultMsg(ResponseCode.CLIENT_AUTH_ERROR);
                renderText(response.toJson());
            }
            if (expireDate < Short.parseShort(DateTime.now().toString("yyMM"))) {
                response.setCodeWithDefaultMsg(ResponseCode.CLIENT_AUTH_TOKEN_EXPIRED);
                renderText(response.toJson());
            }
            renderText(response.toJson());
        } catch (Exception e) {
            Logger.info("validateAccessKey Exception", e);
            response.setCodeWithDefaultMsg(ResponseCode.CLIENT_AUTH_ERROR);
            renderText(response.toJson());
        }
    }

}
