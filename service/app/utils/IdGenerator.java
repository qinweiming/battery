package utils;

import org.bson.types.ObjectId;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class IdGenerator {
    static SecureRandom secureRandom;
    static {
        try {
            secureRandom=SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    public static  int generateIntId(){

        return secureRandom.nextInt();
    }


    public static int getClientIdAsInt(String clientId){
        return new ObjectId(clientId).getCounter();
    }
}
