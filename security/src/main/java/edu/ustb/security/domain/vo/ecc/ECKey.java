package edu.ustb.security.domain.vo.ecc;

import edu.ustb.security.domain.vo.ecc.elliptic.EllipticCurve;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by sunyichao on 2016/12/20.
 * CPK密钥对 包括公钥 私钥
 */
public class ECKey implements Key {
    public boolean secret;
    private BigInteger sk;
    private BigInteger pk;
    public ECPoint beta;
    protected EllipticCurve mother;

    @Override
    public BigInteger getSk() {
        return sk;
    }

    @Override
    public ECPoint getPk() {
        return beta;
    }

    /**
     * ECKey generates a random secret key (contains also the public key)
     */
    public ECKey(EllipticCurve ec) {
        mother = ec;
        secret = true;
        BigInteger temp;
        boolean range;
        boolean odd;
        do {
            sk = new BigInteger(ec.getp().bitLength() + 17, new SecureRandom());//随机生成私钥
            //sk=new BigInteger("27695309294341089017390108141584159587215765888091911316068293386721553528579");
            if (mother.getOrder() != null)
                sk = sk.mod(mother.getOrder());
            temp = sk.multiply(new BigInteger("32"));
            range = (temp.compareTo(mother.getOrder())) == 1;
            odd = ((sk.mod(new BigInteger("2"))).compareTo(BigInteger.ZERO)) == 1;
        } while ((!range) || (!odd));

        beta = (mother.getGenerator()).multiply(sk);
        beta.fastCache();
    }


    public ECKey(EllipticCurve ec, BigInteger skin) {
        mother = ec;
        secret = true;
        //sk=new BigInteger(ec.getp().bitLength() + 17,Rand.om);//随机生成私钥
        //sk=new BigInteger("27695309294341089017390108141584159587215765888091911316068293386721553528579");
        sk = skin;
        if (mother.getOrder() != null)
            sk = sk.mod(mother.getOrder());
        beta = (mother.getGenerator()).multiply(sk);
        beta.fastCache();
    }


    public ECKey ManualECKey(EllipticCurve ec, BigInteger skin) {
        mother = ec;
        secret = true;
        sk = new BigInteger(ec.getp().bitLength() + 17, new SecureRandom());//随机生成私钥
        //sk=new BigInteger("27695309294341089017390108141584159587215765888091911316068293386721553528579");
        if (mother.getOrder() != null)
            sk = sk.mod(mother.getOrder());
        beta = (mother.getGenerator()).multiply(sk);
        beta.fastCache();
        return null;
    }


    public String toString() {
        if (secret) return ("Secret key: " + sk + " " + beta + " " + mother);
        else return ("Public key:" + beta + " " + mother);
    }

    public boolean isPublic() {
        return (!secret);
    }

    public void writeKey(OutputStream out) throws IOException {
        DataOutputStream output = new DataOutputStream(out);
        mother.writeCurve(output);
        output.writeBoolean(secret);
        if (secret) {
            byte[] skb = sk.toByteArray();
            output.writeInt(skb.length);
            output.write(skb);
        }
        byte[] betab = beta.compress();
        output.writeInt(betab.length);
        output.write(betab);
    }

    public Key readKey(InputStream in) throws IOException {
        DataInputStream input = new DataInputStream(in);
        ECKey k = new ECKey(new EllipticCurve(input));
        k.secret = input.readBoolean();
        if (k.secret) {
            byte[] skb = new byte[input.readInt()];
            input.read(skb);
            k.sk = new BigInteger(skb);
        }
        byte[] betab = new byte[input.readInt()];
        input.read(betab);
        k.beta = new ECPoint(betab, k.mother);
        return k;
    }

    /**
     * Turns this key into a public key (does nothing if this key is public)
     */
    public Key getPublic() {
        Key temp = new ECKey(mother);
        ((ECKey) temp).beta = beta;
        ((ECKey) temp).sk = BigInteger.ZERO;
        ((ECKey) temp).secret = false;
        System.gc();
        return temp;
    }

    /**
     * Turns this key into a public key (does nothing if this key is public)
     */
    public Key getManualPublic() {
        Key temp = new ECKey(mother);
        ((ECKey) temp).beta = beta;
        ((ECKey) temp).sk = BigInteger.ZERO;
        ((ECKey) temp).secret = false;
        System.gc();
        return temp;
    }

    /**
     * Turns this key into a public key (does nothing if this key is public)
     */
    public Key getPublicKeyByPoint(ECPoint pointpub) {
        Key temp = new ECKey(mother);
        ((ECKey) temp).beta = pointpub;
        ((ECKey) temp).sk = BigInteger.ZERO;
        ((ECKey) temp).secret = false;
        System.gc();
        return temp;
    }

}