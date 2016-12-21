package edu.ustb.security.service.ecc;

import edu.ustb.security.domain.vo.ecc.ECPoint;
import edu.ustb.security.domain.vo.ecc.Pair;
import edu.ustb.security.domain.vo.ecc.elliptic.EllipticCurve;

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
     * 生成签名
     *
     * @param sk            标识私钥
     * @param mac           被签名的hash值
     * @param ellipticCurve 依赖曲线
     * @return 签名
     */
    Pair sign(BigInteger sk, BigInteger mac, EllipticCurve ellipticCurve);


    /**
     * 验证签名
     *
     * @param pk            标识公钥
     * @param mac           被签名的hash值
     * @param sign          签名
     * @param ellipticCurve 依赖曲线
     * @return 返回签名验证结果
     */
    boolean verify(ECPoint pk, BigInteger mac, Pair sign, EllipticCurve ellipticCurve);
}
