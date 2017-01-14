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
     * 设置默认曲线
     * 如果不设置则默认为spec256
     *
     * @param defaultEllipticCurve
     */
    void setDefaultEllipticCurve(EllipticCurve defaultEllipticCurve);

    /**
     * 获取曲线信息
     *
     * @return 曲线名字
     */
    String getDefaultEllipticCurve();

    /**
     * 使用默认曲线生成种子公钥和种子私钥
     *
     * @param skm 私钥矩阵
     * @param pkm 公钥矩阵
     * @return 是否生成成功
     */
    boolean generateCpkMatrix(BigInteger[][] skm, ECPoint pkm[][]);

    /**
     * 指定曲线，生成种子私钥和种子公钥
     *
     * @param skm           私钥矩阵
     * @param pkm           公钥矩阵
     * @param ellipticCurve 指定曲线
     * @return 是否生成成功
     */
    boolean generateCpkMatrix(BigInteger[][] skm, ECPoint pkm[][], EllipticCurve ellipticCurve);

    /**
     * 生成标识公钥
     *
     * @param Id  用户标识
     * @param pkm 种子公钥
     * @return 由标识及指定矩阵生成的公钥
     */
    ECPoint generatePkById(String Id, ECPoint pkm[][]);

    /**
     * 使用默认曲线，生成标识私钥
     *
     * @param Id  用户标识
     * @param skm 种子私钥
     * @return 由标识及指定矩阵生成的私钥，null默认曲线不存在
     */
    // FIXME: 私钥生成,仅对私钥管理中心开放
    BigInteger generateSkById(String Id, BigInteger skm[][]);

    /**
     * 生成标识私钥
     *
     * @param Id    用户标识
     * @param skm   种子私钥
     * @param order 依赖曲线的阶
     * @return 由标识及指定矩阵生成的私钥
     */
    // FIXME: 私钥生成,仅对私钥管理中心开放
    BigInteger generateSkById(String Id, BigInteger skm[][], BigInteger order);

    /**
     * 使用默认曲线，生成签名
     *
     * @param sk        标识私钥
     * @param hashBytes 被签名的hash值
     * @return 签名
     */
    Pair sign(BigInteger sk, byte[] hashBytes);

    /**
     * 生成签名
     *
     * @param sk            标识私钥
     * @param hashBytes     被签名的hash值
     * @param ellipticCurve 依赖曲线
     * @return 签名
     */
    Pair sign(BigInteger sk, byte[] hashBytes, EllipticCurve ellipticCurve);

    /**
     * 使用默认曲线 验证签名
     *
     * @param pk        标识公钥
     * @param hashBytes 被签名的hash值
     * @param sign      签名
     * @return 返回签名验证结果，
     */
    boolean verify(ECPoint pk, byte[] hashBytes, Pair sign);

    /**
     * 验证签名
     *
     * @param pk            标识公钥
     * @param hashBytes     被签名的hash值
     * @param sign          签名
     * @param ellipticCurve 依赖曲线
     * @return 返回签名验证结果
     */
    boolean verify(ECPoint pk, byte[] hashBytes, Pair sign, EllipticCurve ellipticCurve);

    /**
     * 将源字符串，签名生成二维码并输出到指定输出流
     *
     * @param outputStream 二维码图片指定输出流
     * @param src          源字符串，即电池编号，被签名信息
     * @param pair         签名信息
     */
    void generateQRcode(OutputStream outputStream, String src, Pair pair);

}
