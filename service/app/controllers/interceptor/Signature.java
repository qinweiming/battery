/**
 *
 */
package controllers.interceptor;

import controllers.BaseController;
import models.KeyPair;
import models.Error;
import models.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.Logger;
import play.Play;
import play.libs.Codec;
import play.mvc.Before;
import utils.SignatureUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * validate message signature
 *
 * @author <a href="mailto:zhaoxiaoyong@novacloud.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 * @date Aug 5, 2014
 */
public class Signature extends BaseController {
    public static final String DEFAULT_SIGNATURE_VERSION = "1";
    //保留参数
    private static List<String> unSignParams = new ArrayList<>();

    static {
        unSignParams.add("signature");
        unSignParams.add("body");
    }

    @Before(unless = "auth")
    static void validate() {
        Error error = new Error();
        String signature = request.params.get("signature");
        String signature_version = request.params.get("signature_version");
        String access_key_id = request.params.get("access_key_id");
        if (StringUtils.isEmpty(access_key_id)) {
            error.setParameterMiss("access_key_id");
            paramsMiss("access_key_id");
        }

        if (StringUtils.isEmpty(signature)) {
            error.setParameterMiss("signature");
            paramsMiss("signature");
        }

        if (StringUtils.isEmpty(signature_version)) {
            //defaut to 1
            signature_version = DEFAULT_SIGNATURE_VERSION;
        }
        try {
            String clientId = "";
            try {
                byte[] text = decryptAES(access_key_id.substring(1));
                if (text == null) {
                    error.setCodeWithDefaultMsg(ErrorCode.CLIENT_AUTH_ERROR);
                    unauthorized(error);
                }
                clientId = new ObjectId(ByteBuffer.allocate(12).put(text, 0, 12).array()).toString();

                short expireDate = ByteBuffer.wrap(text).getShort(12);

                error.setCodeWithDefaultMsg(ErrorCode.SUCCESS);

                if (expireDate < Short.parseShort(DateTime.now().toString("yyMM"))) {
                    error.setCodeWithDefaultMsg(ErrorCode.CLIENT_AUTH_TOKEN_EXPIRED);
                    unauthorized(error);
                }
            } catch (Exception e) {
                e.printStackTrace();
                error.setCodeMsg(ErrorCode.CLIENT_AUTH_ERROR, "access_key_id错误");
                unauthorized(error);
            }
            KeyPair keyPair = KeyPair.getCollection(KeyPair.class).findOne("{'accessKey':#}", access_key_id).as(KeyPair.class);
            if (keyPair == null) {
                error.setCodeWithDefaultMsg(ErrorCode.CLIENT_AUTH_ERROR);
                unauthorized(error);
            }
            else if (!keyPair.getClientId().equalsIgnoreCase(clientId)) {
                error.setCodeMsg(ErrorCode.CLIENT_AUTH_ERROR, "access key error");
                unauthorized(error);
            }
            // access token is valid
            if (signature_version.equalsIgnoreCase("1")) {
                Map<String, String> toSignMap = new HashMap<>();
                Map<String, String> allParams = request.params.allSimple();
                Map<String, String> routeArgs = request.routeArgs;
                for (Map.Entry<String, String> one : allParams.entrySet()) {
                    String key = one.getKey();
                    //如果参数是属于路由映射的 path,则签名的时候跳过,因为已经在 path 里了
                        if ((routeArgs != null && routeArgs.get(key) != null) || unSignParams.contains(key)) {
                        } else {
                            toSignMap.put(key, one.getValue());
                        }
                }
                String resignature = SignatureUtil.computeSignature(keyPair.getSecretKey(), request.method, request.path, toSignMap);
                Logger.debug("compute signature %s", resignature);
                if (resignature.equalsIgnoreCase(signature)) {
                    params.put("client_id",clientId);

                } else {
                    if(Play.mode.isDev() && Boolean.valueOf(params.get("debug"))){
                        Logger.warn("DEBUG MODE: ignoring signature error!!!");
                        params.put("client_id",clientId);
                    }else {
                        error.setCodeMsg(ErrorCode.CLIENT_AUTH_ERROR, "signature error");
                        unauthorized(error);
                    }
                }
            }else {
                error.setCodeMsg(ErrorCode.CLIENT_AUTH_ERROR, "signature version don't supported.");
                unauthorized(error);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("parse access key error", e);
            error.setCodeWithDefaultMsg(ErrorCode.CLIENT_AUTH_ERROR);
            error.setDetail(e.getMessage());
            unauthorized(error);
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
}
