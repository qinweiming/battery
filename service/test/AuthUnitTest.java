import controllers.v2.Auth;
import org.bson.types.ObjectId;
import org.junit.Test;
import play.Play;
import play.exceptions.UnexpectedException;
import play.libs.Codec;
import play.test.UnitTest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;

public class AuthUnitTest extends UnitTest {

    private static String encryptAES(String text) {
           try {
               byte[] ex =  Play.configuration.getProperty("application.secret").substring(0, 16).getBytes();
               SecretKeySpec skeySpec = new SecretKeySpec(ex, "AES");
               Cipher cipher = Cipher.getInstance("AES");
               cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
               return Codec.encodeBASE64(cipher.doFinal(text.getBytes()));
           } catch (Exception var5) {
               throw new UnexpectedException(var5);
           }
       }
    private static String encryptAES(byte[] text) {
               try {
                   byte[] ex =  Play.configuration.getProperty("application.secret").substring(0, 16).getBytes();
                   SecretKeySpec skeySpec = new SecretKeySpec(ex, "AES");
                   Cipher cipher = Cipher.getInstance("AES");
                   cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
                   byte[] value = cipher.doFinal(text);
                   System.err.println("cipher length:"+value.length);
                   System.err.println("cipher byteToHexString length:"+Codec.byteToHexString(value).length());
                   return Codec.encodeBASE64(value);
               } catch (Exception var5) {
                   throw new UnexpectedException(var5);
               }
           }

    public static byte[] decryptAES(String value) {
             try {
                 byte[] ex = Play.configuration.getProperty("application.secret").substring(0, 16).getBytes();
                 SecretKeySpec skeySpec = new SecretKeySpec(ex, "AES");
                 Cipher cipher = Cipher.getInstance("AES");
                 cipher.init(2, skeySpec);

                 return cipher.doFinal(Codec.decodeBASE64(value));
             } catch (Exception var5) {
                 throw new UnexpectedException(var5);
             }
         }
    public static String decryptObjectId(String value) {
          try {
              byte[] ex = Play.configuration.getProperty("application.secret").substring(0, 16).getBytes();
              SecretKeySpec skeySpec = new SecretKeySpec(ex, "AES");
              Cipher cipher = Cipher.getInstance("AES");
              cipher.init(2, skeySpec);

              return new ObjectId(cipher.doFinal(Codec.decodeBASE64(value))).toString();
          } catch (Exception var5) {
              throw new UnexpectedException(var5);
          }
      }
    private static String encryptObjectId(ObjectId objectId) {
           try {
               byte[] ex =  Play.configuration.getProperty("application.secret").substring(0, 16).getBytes();
               SecretKeySpec skeySpec = new SecretKeySpec(ex, "AES");
               Cipher cipher = Cipher.getInstance("AES");
               cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
               return Codec.encodeBASE64(cipher.doFinal(objectId.toByteArray()));
           } catch (Exception var5) {
               throw new UnexpectedException(var5);
           }
       }
    @Test
    public void testGetKeyPairs() {
        Auth.getKeyPairs("5407fd5e96e633b127b0797d");
    }
    @Test
    public void testEncryptObjectId() {
        String base64 = encryptObjectId(new ObjectId("5407fd5e96e633b127b0797d"));
        System.err.printf("%d,base64:%s",base64.length(),base64);
        assertEquals(24,base64.length());
    }
     @Test
    public void testAES(){

               try {

                   String base64 =encryptAES("5407fd5e96e633b127b0797d"+"&"+"20050101");
                   System.err.printf("%d,base64:%s",base64.length(),base64);
               } catch (Exception var5) {
                   throw new UnexpectedException(var5);
               }
    }
    @Test
    public void validateAccessKey() {
//        DateTime.now().toDateTime().toInstant().getMillis()
        byte[] objectIdByteArray = new ObjectId("5407fd5e96e633b127b0797d").toByteArray();
        System.err.println(objectIdByteArray.length);
        ByteBuffer byteBuffer = ByteBuffer.allocate(15).put(objectIdByteArray)
                .put(objectIdByteArray.length, (byte) '&')
//                .putInt(objectIdByteArray.length , Integer.parseInt("20150201"))
                .putShort(objectIdByteArray.length, Short.parseShort("1602"));
        System.err.println(byteBuffer.array().length);
        String encryptAES = encryptAES(byteBuffer.array());
        System.err.println(encryptAES+"----"+encryptAES.length());

        byte[] decrypt= decryptAES(encryptAES);
        System.err.println("ObjectId:" + new ObjectId(ByteBuffer.allocate(12).put(decrypt, 0, 12).array()).toString());
        short expireDate =ByteBuffer.wrap(decrypt).getShort(12);
//        short expireDate = ByteBuffer.allocate(2).put(decrypt, 12, 2).getShort(0);
        System.err.println("expireDate:"+expireDate);

//        String clientId =  "5407fd5e96e633b127b0797d";
//        String accessKey = "1"+encryptObjectId(new ObjectId("5407fd5e96e633b127b0797d"));
//        System.err.println(accessKey+"----"+accessKey.length());
//        String decryptObjectId = decryptObjectId((accessKey.substring(1)));
//        System.err.println(decryptObjectId);
//        boolean ret = decryptObjectId .split("&")[0]
//    				.equalsIgnoreCase(clientId);
//    	    assertTrue(ret);
    	}
}
