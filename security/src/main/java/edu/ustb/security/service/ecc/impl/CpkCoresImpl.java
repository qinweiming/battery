package edu.ustb.security.service.ecc.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import edu.ustb.security.common.constants.Constants;
import edu.ustb.security.common.utils.DESUtils;
import edu.ustb.security.common.utils.TypeTransUtils;
import edu.ustb.security.domain.vo.ecc.ECKey;
import edu.ustb.security.domain.vo.ecc.ECPoint;
import edu.ustb.security.domain.vo.ecc.Key;
import edu.ustb.security.domain.vo.ecc.Pair;
import edu.ustb.security.domain.vo.ecc.elliptic.*;
import edu.ustb.security.domain.vo.matrix.Matrix;
import edu.ustb.security.domain.vo.matrix.Matrixs;
import edu.ustb.security.service.ecc.CpkCores;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sunyichao on 2016/12/20.
 * cpk算法核心实现
 */
public class CpkCoresImpl implements CpkCores {
    private EllipticCurve defaultEllipticCurve;
    private BigInteger[][] skm = new BigInteger[32][32];
    private ECPoint[][] pkm = new ECPoint[32][32];

    public CpkCoresImpl(Matrixs matrixs) {
        try {
            defaultEllipticCurve = new EllipticCurve(new secp256r1());
        } catch (InsecureCurveException e) {
            e.printStackTrace();
            defaultEllipticCurve = null;
        }
        int k = 0;
        Matrix[] matrices = matrixs.getMatrices();
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                skm[i][j] = new BigInteger(matrices[k].getPrivateKey(), 32);
                try {
                    pkm[i][j] = new ECPoint(defaultEllipticCurve, new BigInteger(matrices[k].getPublicKeyX(), 32), new BigInteger(matrices[k].getPublicKeyY(), 32));
                } catch (NotOnMotherException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * @see CpkCores#generatePkById(String,)
     */
    @Override
    public ECPoint generatePkById(String Id) {
        ECPoint pk = null;
        try {
            if (pkm != null) {
                int[] YS = IdToYs(Id.getBytes(Constants.CHARSET), Constants.MD);
                BigInteger[] a = IdToA2(Id, Constants.MD);
                try {
                    pk = generatePk(YS, a, pkm);
                } catch (NoCommonMotherException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pk;
    }

    @Override
    public BigInteger generateSkById(String Id) {
        return generateSkById(Id, skm, defaultEllipticCurve.getOrder());
    }

    private BigInteger generateSkById(String Id, BigInteger[][] skm, BigInteger order) {
        BigInteger sk = null;
        try {
            if (skm != null && order != null) {
                int[] YS = IdToYs(Id.getBytes(Constants.CHARSET), Constants.MD);
                BigInteger[] a = IdToA2(Id, Constants.MD);
                sk = generateSk(YS, a, skm, order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sk;
    }

    @Override
    public Pair sign(BigInteger sk, String src) {
        byte[] hashBytes = null;
        try {
            hashBytes = src.getBytes(Constants.CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        BigInteger mac = new BigInteger(hashBytes);
        Pair sig = new Pair();
        ECPoint g = new ECPoint(defaultEllipticCurve.getGenerator());
        BigInteger order = defaultEllipticCurve.getOrder();
        do {
            //为增加安全度，签名随机数位数限制
            BigInteger k = defaultEllipticCurve.randomBigInteger(order.subtract(BigInteger.ONE));
            ECPoint gk = g.multiply(k);
            sig.r = (gk.getx()).mod(order);
            if (!(sig.r.compareTo(BigInteger.ZERO) == 0)) {
                if (k.gcd(order).compareTo(BigInteger.ONE) == 0) {
                    BigInteger temp = k.modInverse(order);
                    sig.s = (temp.multiply((sk.multiply(sig.r)).add(mac))).mod(order);
                }
            }
        } while ((sig.r.compareTo(BigInteger.ZERO) == 0) || (sig.s.compareTo(BigInteger.ZERO) == 0));
        return sig;
    }

    @Override
    public boolean verify(ECPoint pk, String src, Pair sig) {
        byte[] hashBytes = null;
        try {
            hashBytes = src.getBytes(Constants.CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        BigInteger mac = new BigInteger(hashBytes);
        ECPoint g = new ECPoint(defaultEllipticCurve.getGenerator());
        BigInteger r = sig.r;
        BigInteger s = sig.s;
        BigInteger w, u1, u2;
        BigInteger order = defaultEllipticCurve.getOrder();
        if ((r.compareTo(BigInteger.ONE) >= 0) &&
                (r.compareTo(order.subtract(BigInteger.ONE)) <= 0) &&
                (s.compareTo(BigInteger.ONE) >= 0) &&
                (s.compareTo(order.subtract(BigInteger.ONE)) <= 0)) {
            w = s.modInverse(order);
            u1 = (mac.multiply(w)).mod(order);
            u2 = (r.multiply(w)).mod(order);
            ECPoint g1 = g.multiply(u1);
            ECPoint g2 = pk.multiply(u2);
            try {
                ECPoint temp = g1.add(g2);
                if (temp.getx().mod(order).compareTo(r.mod(order)) == 0) {
                    System.out.println("验证成功!");
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

    @Override
    public void generateQRcode(OutputStream outputStream, String src, Pair pair) {
        String x = pair.r.toString(32);
        String y = pair.s.toString(32);
        String QRString = src + "/" + x + "/" + y;
        generateQRcode(outputStream, QRString);
    }

    /**
     * 生成私钥
     *
     * @param YS  YS序列，种子矩阵挑选坐标
     * @param a   系数序列，点加运算系数
     * @param skm 种子私钥
     * @param n   椭圆曲线阶
     * @return 私钥
     */
    private BigInteger generateSk(int[] YS, BigInteger[] a, BigInteger[][] skm, BigInteger n) {
        BigInteger[] skAdd = new BigInteger[32];
        for (int i = 0; i < 32; i++) {
            skAdd[i] = skm[YS[i]][i].multiply(a[i]);
        }
        BigInteger skTotal = skAdd[0];
        for (int i = 1; i < 32; i++) {
            skTotal = skTotal.add(skAdd[i]).mod(n);
        }
        return skTotal;
    }

    /**
     * 生成公钥
     *
     * @param YS  YS序列，种子矩阵挑选坐标
     * @param a   系数序列，点加运算系数
     * @param PKM 种子公钥
     * @return 公钥
     * @throws NoCommonMotherException 不在同一条曲线上
     */
    private ECPoint generatePk(int[] YS, BigInteger[] a, ECPoint[][] PKM) throws NoCommonMotherException {
        ECPoint[] pubkeyAdd = new ECPoint[32];
        for (int i = 0; i < 32; i++) {
            pubkeyAdd[i] = PKM[YS[i]][i].multiply(a[i]);
        }
        ECPoint pubkeyTotal = pubkeyAdd[0];
        for (int i = 1; i < 32; i++) {
            pubkeyTotal = pubkeyTotal.add(pubkeyAdd[i]);
        }
        return pubkeyTotal;
    }

    /**
     * 标识ID生成YS序列，做CPK算法中种子矩阵挑选坐标
     *
     * @param strSrc 标识ID
     * @param MD     hash算法
     * @return YS系数数组
     */
    private int[] IdToYs(String strSrc, String MD) {
        byte[] bytes = null;
        int[] Ys = null;
        try {
            bytes = strSrc.getBytes(Constants.CHARSET);
            String strDes = TypeTransUtils.Digest(strSrc, MD);
            String binaryId = TypeTransUtils.hexString2binaryString(strDes);
            Ys = part32by5(binaryId);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Ys;
    }

    private int[] IdToYs(byte[] srcBytes, String MD) throws NoSuchAlgorithmException {
        byte[] hashBytes = TypeTransUtils.Digest(srcBytes, MD);
        String binary = TypeTransUtils.binary(hashBytes, 2);
        return part32by5(binary);

    }

    /**
     * 二进制标识每8bit分割，前5bit作为坐标
     * 使其数值范围匹配0～31
     *
     * @param binaryId 标识二进制表示
     * @return 坐标数组
     */
    private int[] part32by5(String binaryId) {
        int n = binaryId.length() / 8;
        String[] binary32 = new String[n];
        int[] int32 = new int[n];
        char[] binary5 = new char[5];
        for (int i = 3, j = 0; i < binaryId.length(); j++) {
            binary5[0] = binaryId.charAt(i++);
            binary5[1] = binaryId.charAt(i++);
            binary5[2] = binaryId.charAt(i++);
            binary5[3] = binaryId.charAt(i++);
            binary5[4] = binaryId.charAt(i++);
            i = i + 3;
            binary32[j] = String.valueOf(binary5);
            BigInteger bir = new BigInteger(binary32[j], 2);//转换为BigInteger类型
            int32[j] = bir.intValue();
        }
        return int32;
    }

    /**
     * 标识ID生成A2序列，做CPK算法中点加运算的系数
     *
     * @param strSrc 标识ID
     * @param MD     摘要算法
     * @return 系数数组
     */
    private BigInteger[] IdToA2(String strSrc, String MD) {
        String strDes = TypeTransUtils.Digest(strSrc, MD);
        BigInteger[] bigIntegers32 = new BigInteger[32];
        for (int i = 0; i < 32; i++) {
            try {
                strDes = DESUtils.encrypt(strDes).substring(0, 64);
                String binaryId = TypeTransUtils.hexString2binaryString(strDes);
                BigInteger bigInteger = new BigInteger(binaryId, 2);
                bigIntegers32[i] = bigInteger;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bigIntegers32;
    }


    /**
     * 输入要生成二维码的字符串，输出到指定输出流
     *
     * @param outputStream
     * @param encodeString
     */
    private void generateQRcode(OutputStream outputStream, String encodeString) {
        BitMatrix bitMatrix = null;

        try {
            bitMatrix = new MultiFormatWriter().encode(encodeString, BarcodeFormat.QR_CODE, 300, 300);
            MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
