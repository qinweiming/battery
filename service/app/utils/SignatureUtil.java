package utils;



import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * NovaData API signature demo.
 * 代码参考 https://github.com/fit2cloud/qingcloud-api-java-wrapper/blob/master/src/main/java/com/fit2cloud/qingcloud/wsclient/QingCloudWSClient.java
 */
public class SignatureUtil {
    private static final String ENCODING = "UTF-8";

    /**
     * 计算签名
     *
     * @param secretKey
     * @param httpMethod
     * @param path
     * @param parameters
     * @return
     */
    public static String computeSignature(String secretKey, String httpMethod, String path,
                                          Map<String, String> parameters) {
        String[] sortedKeys = parameters.keySet().toArray(new String[]{});
        Arrays.sort(sortedKeys);
        final String SEPARATOR = "&";

        StringBuilder sbStringToSign = new StringBuilder();
        sbStringToSign.append(httpMethod).append("\n").append(path).append("\n");

        String signature = "";
        try {
            int count = 0;

            for (String key : sortedKeys) {
                if (count != 0) {
                    sbStringToSign.append(SEPARATOR);
                }
                sbStringToSign.append(percentEncode(key)).append("=")
                        .append(percentEncode(parameters.get(key)));
                count++;
            }

            String strToSign = sbStringToSign.toString();
            signature = calculateSignature(secretKey, strToSign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    private static String calculateSignature(String key, String stringToSign) {
        final String ALGORITHM = "HmacSHA256";
        byte[] signData = new byte[]{};
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(key.getBytes(ENCODING), ALGORITHM));
            signData = mac.doFinal(stringToSign.getBytes(ENCODING));
        } catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | IllegalStateException e) {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(signData));
    }

    private static String percentEncode(String value)
            throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, ENCODING)
                .replace("+", "%20").replace("*", "%2A").replace("%7E", "~")
                : null;
    }

    //测试 URL
    //http://localhost:8010/v2/data/websites/556afa28e2748db26f8fa735?access_key_id=1ojZyGk5rnh5PBhX2VMuUhQ==&fields=data.*&limit=2&offset=10&signature_version=1&sort=date:desc&signature=FvyGTjeamJUwV1gQz8G%2Bel1miLSEiUt4auayHhqhGb0%3D

    public static void main(String[] args) throws UnsupportedEncodingException {
        String accessKeyId = "1ojZyGk5rnh5PBhX2VMuUhQ==";
        String secretKey = "198826f5e1f52e6087c5d711668300dcc9b5633c3";
        String method = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("access_key_id", accessKeyId);
        params.put("limit", "2");
        params.put("offset", "10");
        params.put("signature_version", "1");
        params.put("sort", "date:desc");
        String path = "/v2/data/websites/556afa28e2748db26f8fa735";
        String signature = computeSignature(secretKey, method, path, params);
        System.out.println(signature);
        //作为 URL 参数附带的时候，需要转码
        System.out.println(percentEncode(signature));
    }
}