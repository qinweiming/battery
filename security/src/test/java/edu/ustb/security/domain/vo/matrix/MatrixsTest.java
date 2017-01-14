package edu.ustb.security.domain.vo.matrix;

import edu.ustb.security.domain.vo.ecc.ECPoint;
import edu.ustb.security.domain.vo.ecc.elliptic.EllipticCurve;
import edu.ustb.security.domain.vo.ecc.elliptic.secp256r1;
import edu.ustb.security.service.ecc.impl.CpkCoresImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * Created by sunyichao on 2017/1/14.
 */
public class MatrixsTest {
    private CpkCoresImpl cpkCores;
    private Matrixs matrixs;
    private BigInteger skm[][] = new BigInteger[32][32];
    private ECPoint pkm[][] = new ECPoint[32][32];
    private String id = "sunyichao";
    private byte[] mac = "sunyichao".getBytes();


    @Before
    public void setUp() throws Exception {
        cpkCores = new CpkCoresImpl();
        //种子矩阵生成API
        matrixs = cpkCores.generateCpkMatrix();
    }

    /**
     * json fromJson
     * @throws Exception
     */
    @Test
    public void toJson() throws Exception {
        Matrixs matrixs = Matrixs.fromJson(this.matrixs.toJson());
        Assert.assertEquals(this.matrixs.getMatrices()[512].getAxisX(),matrixs.getMatrices()[512].getAxisX());
    }
}