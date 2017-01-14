package edu.ustb.security.service.ecc;

import edu.ustb.security.domain.vo.ecc.ECKey;
import edu.ustb.security.domain.vo.ecc.ECPoint;
import edu.ustb.security.domain.vo.ecc.Key;
import edu.ustb.security.domain.vo.ecc.elliptic.EllipticCurve;
import edu.ustb.security.domain.vo.ecc.elliptic.InsecureCurveException;
import edu.ustb.security.domain.vo.ecc.elliptic.secp256r1;
import edu.ustb.security.domain.vo.matrix.Matrix;
import edu.ustb.security.domain.vo.matrix.Matrixs;

import java.math.BigInteger;

/**
 * Created by sunyichao on 2017/1/14.
 * 生成种子矩阵工厂类
 */
public class CpkMatrixsFactory {
    /**
     * 生成种子矩阵
     *
     * @return 种子矩阵对象
     */
    public static Matrixs generateCpkMatrix() {
        EllipticCurve ellipticCurve = null;
        try {
            ellipticCurve = new EllipticCurve(new secp256r1());
        } catch (InsecureCurveException e) {
            e.printStackTrace();
        }
        return generateCpkMatrix(ellipticCurve);
    }

    /**
     * 生成种子矩阵
     *
     * @param ellipticCurve 指定曲线
     * @return 种子矩阵对象
     */
    public static Matrixs generateCpkMatrix(EllipticCurve ellipticCurve) {
        if (ellipticCurve != null) {
            Matrixs matrixs = new Matrixs();
            Matrix[] matrices = new Matrix[1024];
            int k = 0;
            for (int i = 0; i < 32; i++) {
                for (int j = 0; j < 32; j++) {
                    Key key = new ECKey(ellipticCurve);
                    BigInteger sk = key.getSk();
                    ECPoint pk = key.getPk();
                    Matrix matrix = new Matrix(i, j, pk.getx().toString(32), pk.gety().toString(32), sk.toString(32));
                    matrices[k] = matrix;
                    k++;
                }
            }
            matrixs.setMatrices(matrices);
            return matrixs;
        }
        return null;
    }
}
