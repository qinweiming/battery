package edu.ustb.security.service.ecc.impl;

import edu.ustb.security.domain.vo.ecc.ECPoint;
import edu.ustb.security.domain.vo.ecc.Pair;
import edu.ustb.security.domain.vo.ecc.elliptic.EllipticCurve;
import edu.ustb.security.domain.vo.ecc.elliptic.secp256r1;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * Created by sunyichao on 2016/12/20.
 * cpk 算法核心测试类
 * 指定测试顺序测试 @FixMethodOrder
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CpkCoresImplTest {
    private CpkCoresImpl cpkCores;
    private EllipticCurve ellipticCurve;
    private BigInteger skm[][] = new BigInteger[32][32];
    private ECPoint pkm[][] = new ECPoint[32][32];
    private String id = "sunyichao";
    private BigInteger mac = new BigInteger("1231231");

    @Before
    public void setUp() throws Exception {
        cpkCores = new CpkCoresImpl();
        ellipticCurve = new EllipticCurve(new secp256r1());
        cpkCores.generateCpkMatrix(skm, pkm, ellipticCurve);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void AGenerateCpkMatrix() throws Exception {
        BigInteger[][] skm = new BigInteger[32][32];
        ECPoint[][] pkm = new ECPoint[32][32];
        cpkCores.generateCpkMatrix(skm, pkm, ellipticCurve);
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                ECPoint temp = ellipticCurve.getGenerator().multiply(skm[i][j]);
                Assert.assertArrayEquals(pkm[i][j].getx().toByteArray(), temp.getx().toByteArray());
            }
        }
    }

    @Test
    public void SignAndVerify() throws Exception {
        BigInteger sk = cpkCores.generateSkById(id, skm, ellipticCurve.getOrder());
        Pair sign = cpkCores.sign(sk, mac, ellipticCurve);
        ECPoint pk = cpkCores.generatePkById(id, pkm);
        boolean verify = cpkCores.verify(pk, mac, sign, ellipticCurve);
        Assert.assertTrue(verify);
    }

}