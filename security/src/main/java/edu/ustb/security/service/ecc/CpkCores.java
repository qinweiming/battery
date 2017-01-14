package edu.ustb.security.service.ecc;

import edu.ustb.security.domain.vo.ecc.ECPoint;
import edu.ustb.security.domain.vo.ecc.Pair;
import edu.ustb.security.domain.vo.ecc.elliptic.EllipticCurve;

import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Created by sunyichao on 2016/12/19.
 * Cpk算法核心：
 * 1、包括种子矩阵生成
 * 2、标识私钥／公钥生成
 * 3、CPK签名／验签
 */
public interface CpkCores {

    /**
     * 生成标识公钥
     *
     * @param Id 用户标识
     * @return 由标识及指定矩阵生成的公钥
     */
    ECPoint generatePkById(String Id);

    /**
     * 使用默认曲线，生成标识私钥
     *
     * @param Id 用户标识
     * @return 由标识及指定矩阵生成的私钥，null默认曲线不存在
     */
    // FIXME: 私钥生成,仅对私钥管理中心开放
    BigInteger generateSkById(String Id);


    /**
     * 生成签名
     *
     * @param sk  私钥
     * @param src 要签名的字符串
     * @return 1. 生成签名二维码
     * 电池编号中的厂商代码所生成的私钥
     * 电池编号 流水号
     * <p>
     * 2. 交易过程签名
     * 厂商社会信用码所生成的私钥
     * 交易信息
     */
    abstract Pair sign(BigInteger sk, String src);


    /**
     * 验证签名
     *
     * @param pk   标识公钥
     * @param src  要验证签名的字符串
     * @param sign 签名
     * @return 返回签名验证结果，
     */
    boolean verify(ECPoint pk, String src, Pair sign);

    /**
     * 将源字符串，签名生成二维码并输出到指定输出流
     *
     * @param outputStream 二维码图片指定输出流
     * @param src          源字符串，即电池编号，被签名信息
     * @param pair         签名信息
     */
    void generateQRcode(OutputStream outputStream, String src, Pair pair);
}
