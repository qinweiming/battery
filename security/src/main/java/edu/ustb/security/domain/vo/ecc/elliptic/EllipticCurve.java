package edu.ustb.security.domain.vo.ecc.elliptic;

/**
 * Created by sunyichao on 2016/12/19.
 */

import edu.ustb.security.domain.vo.ecc.ECParameters;
import edu.ustb.security.domain.vo.ecc.ECPoint;
import edu.ustb.security.domain.vo.ecc.Key;
import edu.ustb.security.domain.vo.ecc.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

public class EllipticCurve implements Serializable {
    private BigInteger a, b, p, order;
    private ECPoint generator;
    private BigInteger ppodbf;
    private int pointcmpsize;
    private String name;
    public static final BigInteger COEFA = new BigInteger("4");
    public static final BigInteger COEFB = new BigInteger("27");
    public static final int PRIMESECURITY = 500;

    /**
     * Constructs an elliptic curve over the finite field of 'mod' elements.
     * The equation of the curve is on the form : y^2 = x^3 + ax + b.
     *
     * @param a the value of 'a' where y^2 = x^3 + ax + b
     * @param b the value of 'b' where y^2 = x^3 + ax + b
     * @param p The number of elements in the field.
     *          IMPORTANT: Must a prime number!
     * @throws InsecureCurveException if the curve defined by a and b are singular,
     *                                supersingular, trace one/anomalous.
     *                                This ensures well defined operations and security.
     */
    public EllipticCurve(BigInteger a, BigInteger b, BigInteger p) throws InsecureCurveException {

        this.a = a;
        this.b = b;
        this.p = p;
        if (!p.isProbablePrime(PRIMESECURITY)) {
            //System.out.println("THIS CANNOT HAPPEN!!! "+p+" is not a prime!");
            //throw new InsecureCurveException(InsecureCurveException.NONPRIMEMODULUS,this);
        }
        if (isSingular()) throw new InsecureCurveException(InsecureCurveException.SINGULAR, this);

        byte[] pb = p.toByteArray();
        if (pb[0] == 0) pointcmpsize = pb.length;
        else pointcmpsize = pb.length + 1;
        //ppodbf = (p.add(BigInteger.ONE)).shiftRight(2);
        name = "";


        //FIXME compute the order of the group
        //FIXME compute a generator for the group
    }

    public EllipticCurve(ECParameters ecp) throws InsecureCurveException {
        this(ecp.a(), ecp.b(), ecp.p());
        order = ecp.order();
        name = ecp.toString();
        try {
            generator = new ECPoint(this, ecp.generatorX(), ecp.generatorY());
            generator.fastCache();
        } catch (NotOnMotherException e) {
            System.out.println("Error defining EllipticCurve: generator not on mother!");
        }
    }

    public void writeCurve(DataOutputStream output) throws IOException {
        byte[] ab = a.toByteArray();
        output.writeInt(ab.length);
        output.write(ab);
        byte[] bb = b.toByteArray();
        output.writeInt(bb.length);
        output.write(bb);
        byte[] pb = p.toByteArray();
        output.writeInt(pb.length);
        output.write(pb);
        byte[] ob = order.toByteArray();
        output.writeInt(ob.length);
        output.write(ob);
        byte[] gb = generator.compress();
        output.writeInt(gb.length);
        output.write(gb);
        byte[] ppb = getPPODBF().toByteArray();
        output.writeInt(ppb.length);
        output.write(ppb);
        output.writeInt(pointcmpsize);
        output.writeUTF(name);
    }

    public EllipticCurve(DataInputStream input) throws IOException {
        byte[] ab = new byte[input.readInt()];
        input.read(ab);
        a = new BigInteger(ab);
        byte[] bb = new byte[input.readInt()];
        input.read(bb);
        b = new BigInteger(bb);
        byte[] pb = new byte[input.readInt()];
        input.read(pb);
        p = new BigInteger(pb);
        byte[] ob = new byte[input.readInt()];
        input.read(ob);
        order = new BigInteger(ob);
        byte[] gb = new byte[input.readInt()];
        input.read(gb);
        generator = new ECPoint(gb, this);
        byte[] ppb = new byte[input.readInt()];
        input.read(ppb);
        ppodbf = new BigInteger(ppb);
        pointcmpsize = input.readInt();
        name = input.readUTF();
        generator.fastCache();
    }

    public boolean isSingular() {

        BigInteger aa = a.pow(3);
        BigInteger bb = b.pow(2);

        BigInteger result = ((aa.multiply(COEFA)).add(bb.multiply(COEFB))).mod(p);

        if (result.compareTo(BigInteger.ZERO) == 0) return true;
        else return false;

    }

    //FIXME!!!!!!!!!!
    public BigInteger calculateOrder() {
        return null;
    }

    //FIXME!!!!!!!!
    public ECPoint calculateGenerator() {
        return null;
    }

    public boolean onCurve(ECPoint q) {

        if (q.isZero()) return true;
        BigInteger y_square = (q.gety()).modPow(new BigInteger("2"), p);
        BigInteger x_cube = (q.getx()).modPow(new BigInteger("3"), p);
        BigInteger x = q.getx();

        BigInteger dum = ((x_cube.add(a.multiply(x))).add(b)).mod(p);

        if (y_square.compareTo(dum) == 0) return true;
        else return false;

    }

    /**
     * Returns the order of the group
     */
    public BigInteger getOrder() {
        return order;
    }

    public ECPoint getZero() {
        return new ECPoint(this);
    }

    public BigInteger geta() {
        return a;
    }

    public BigInteger getb() {
        return b;
    }

    public BigInteger getp() {
        return p;
    }

    public int getPCS() {
        return pointcmpsize;
    }

    /**
     * Returns a generator for this EllipticCurve.
     */
    public ECPoint getGenerator() {
        return generator;
    }

    public String toString() {
        if (name == null) return "y^2 = x^3 + " + a + "x + " + b + " ( mod " + p + " )";
        else if (name.equals("")) return "y^2 = x^3 + " + a + "x + " + b + " ( mod " + p + " )";
        else return name;
    }

    public BigInteger getPPODBF() {
        if (ppodbf == null) {
            ppodbf = p.add(BigInteger.ONE).shiftRight(2);
        }
        return ppodbf;
    }

    /**
     * 通过Key(包含公 私钥)进行签名
     * 这里需要注意签名过程需要随机数 BigInteger k
     *
     * @param sk  包含公私钥的Key-
     * @param mac 消息验证码
     * @return
     */
    public Pair sign2(Key sk, BigInteger mac) {
        BigInteger k = BigInteger.ZERO;
        Pair sig = new Pair();
        ECPoint g = new ECPoint(generator);
        ECPoint gk = null;
        do {
            k = randomBigInteger(order.subtract(BigInteger.ONE));//内部生成的随机值用来签名
//			k=new BigInteger("84663323743887588986451456640319025113528807989434676950116330380477121824104");
            System.out.println("k:" + k);
            gk = g.multiply(k);
            sig.r = (gk.getx()).mod(order);
            if (!(sig.r.compareTo(BigInteger.ZERO) == 0)) {
                if (k.gcd(order).compareTo(BigInteger.ONE) == 0) {
                    BigInteger temp = k.modInverse(order);
                    sig.s = (temp.multiply((sk.getSk().multiply(sig.r)).add(mac))).mod(order);
                }
            }
        } while ((sig.r.compareTo(BigInteger.ZERO) == 0) || (sig.s.compareTo(BigInteger.ZERO) == 0));

        return sig;
    }

    /**
     * 通过Key(仅含 私钥)进行验签
     *
     * @param pk  仅含公钥的Key
     * @param mac 消息摘要码
     * @param sig 签名
     * @return
     */
    public boolean verify2(Key pk, BigInteger mac, Pair sig) {
        ECPoint g = new ECPoint(generator);
        BigInteger r = sig.r;
        BigInteger s = sig.s;
        BigInteger w, u1, u2;
        if ((r.compareTo(BigInteger.ONE) >= 0) &&
                (r.compareTo(order.subtract(BigInteger.ONE)) <= 0) &&
                (s.compareTo(BigInteger.ONE) >= 0) &&
                (s.compareTo(order.subtract(BigInteger.ONE)) <= 0)) {
            w = s.modInverse(order);
            u1 = (mac.multiply(w)).mod(order);
            u2 = (r.multiply(w)).mod(order);
            ECPoint g1 = g.multiply(u1);
            ECPoint g2 = pk.getPk().multiply(u2);
            try {
                ECPoint temp = g1.add(g2);
                if (temp.getx().mod(order).compareTo(r.mod(order)) == 0) {
                    return true;
                } else {

                    return false;
                }
            } catch (NoCommonMotherException e1) {
                e1.printStackTrace();
            }
        } else {
            return false;
        }
        return false;
    }

    //BigInteger random generator in closed set [1, n]
    public BigInteger randomBigInteger(BigInteger n) {
        Random rnd = new Random();
        int maxNumBitLength = n.bitLength();
        BigInteger aRandomBigInt;
        do {
            aRandomBigInt = new BigInteger(maxNumBitLength, rnd);
            // compare random number lessthan ginven number
        } while (aRandomBigInt.compareTo(n) > 0);
        return aRandomBigInt;
    }
}