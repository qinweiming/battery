package edu.ustb.security.common.utils;

import edu.ustb.security.common.constants.Constants;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sunyichao on 16/6/7.
 */
public class TypeTransUtils {
    /**
     * 根据摘要算法 生成标识
     *
     * @param strSrc 需要生成摘要的信息
     * @param MD     采取的摘要算法
     * @return 生成的标识(16进制)
     */
    public static String Digest(String strSrc, String MD) {
        MessageDigest md = null;
        String strDes = null;

        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance(MD);
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    public static byte[] Digest(byte[] srcBytes) {
        byte[] hashBytes = null;
        try {
            hashBytes = Digest(srcBytes, Constants.MD);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashBytes;
    }

    public static byte[] Digest(byte[] srcBytes, String MD) throws NoSuchAlgorithmException {
        MessageDigest md = null;
        byte[] hashBytes = null;
        md = MessageDigest.getInstance(MD);
        md.update(srcBytes);
        hashBytes = md.digest();
        return hashBytes;
    }

    /**
     * 使用默认hash算法 得到输入流hash值
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static byte[] Digest(InputStream inputStream) throws Exception {
        return Digest(inputStream, Constants.MD);
    }

    /**
     * 指定hash算法 得到输入流hash值
     *
     * @param inputStream 输入流
     * @param MD          指定Hash算法
     * @return hash值
     * @throws Exception Hash算法指定错误&IOException
     */
    public static byte[] Digest(InputStream inputStream, String MD) throws Exception {
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance(MD); //如果想使用SHA-1或SHA-256，则传入SHA-1,SHA-256
        int numRead;
        do {
            numRead = inputStream.read(buffer);    //从文件读到buffer，最多装满buffer
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);  //用读到的字节进行MD5的计算，第二个参数是偏移量
            }
        } while (numRead != -1);
        inputStream.close();
        return complete.digest();
    }


    public static byte[] intToBytes(int value) {
        byte[] byte_src = new byte[4];
        byte_src[3] = (byte) ((value & 0xFF000000) >> 24);
        byte_src[2] = (byte) ((value & 0x00FF0000) >> 16);
        byte_src[1] = (byte) ((value & 0x0000FF00) >> 8);
        byte_src[0] = (byte) ((value & 0x000000FF));
        return byte_src;
    }

    public static int bytesToInt(byte[] ary, int offset) {
        int value;
        value = (int) ((ary[offset] & 0xFF)
                | ((ary[offset + 1] << 8) & 0xFF00)
                | ((ary[offset + 2] << 16) & 0xFF0000)
                | ((ary[offset + 3] << 24) & 0xFF000000));
        return value;
    }

    public static byte[] StringToBytes(String value) {
        byte[] bytes = null;
        try {
            bytes = value.getBytes(Constants.CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static String BytesToString(byte[] bytes) {
        String values = null;
        try {
            values = new String(bytes, Constants.CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return values;
    }

    /**
     * 将byte[]转为各种进制的字符串
     *
     * @param bytes byte[]
     * @param radix 基数可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    /**
     * byte转为16进制
     *
     * @param bts byte数组
     * @return 十六进制字符串
     */
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    /**
     * 16进制转化为2进制
     *
     * @param hexString 十六进制字符串
     * @return 二进制字符串
     */
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString
                    .substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }
}
