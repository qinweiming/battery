package edu.ustb.security.service.ecc.impl;

import edu.ustb.security.common.utils.TypeTransUtils;
import edu.ustb.security.domain.vo.ecc.ECPoint;
import edu.ustb.security.domain.vo.ecc.Pair;
import edu.ustb.security.domain.vo.ecc.elliptic.EllipticCurve;
import edu.ustb.security.domain.vo.ecc.elliptic.secp256r1;
import edu.ustb.security.domain.vo.matrix.Matrixs;
import edu.ustb.security.service.ecc.CpkMatrixsFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private String src = "sunyichao";
    BigInteger sk = null;
    Pair sign = null;
    ECPoint pk = null;


    @Before
    public void setUp() throws Exception {
        //生成种子矩阵
        Matrixs matrixs = CpkMatrixsFactory.generateCpkMatrix();
        //实例化cpkCores核心类
        cpkCores = new CpkCoresImpl(matrixs);

    }


    @Test
    public void BGenerateSk() {
        //生成私钥
        sk = cpkCores.generateSkById(id);
        //生成签名
        sign = cpkCores.sign(sk, src);
        //生成公钥
        pk = cpkCores.generatePkById(id);
        //验证签名
        boolean verify = cpkCores.verify(pk, src, sign);
        //生成二维码
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File("qr.png"));
            //生成二维码接口
            cpkCores.generateQRcode(fos, id, sign);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(verify);

    }

    @Test
    public void CGenerateQR() {

    }
}