package edu.ustb.security.domain.vo.matrix;

import com.alibaba.fastjson.JSON;

/**
 * Created by sunyichao on 2017/1/14.
 * 矩阵对象
 */
public class Matrixs {
    private int matrixField;//矩阵应用领域
    private int ecType;//矩阵依赖曲线
    private Matrix[] matrices;//矩阵坐标对象

    public Matrixs() {
    }

    public Matrixs(int matrixField, int ecType, Matrix[] matrices) {
        this.matrixField = matrixField;
        this.ecType = ecType;
        this.matrices = matrices;
    }

    public int getMatrixField() {
        return matrixField;
    }

    public void setMatrixField(int matrixField) {
        this.matrixField = matrixField;
    }

    public int getEcType() {
        return ecType;
    }

    public void setEcType(int ecType) {
        this.ecType = ecType;
    }

    public Matrix[] getMatrices() {
        return matrices;
    }

    public void setMatrices(Matrix[] matrices) {
        this.matrices = matrices;
    }

    /**
     * 矩阵对象转化为Json字符串
     *
     * @return matrixsJson
     */
    public String toJson() {
        return JSON.toJSON(this).toString();
    }

    /**
     * 从matrixsJson 反序列化得到matrixs对象
     *
     * @param matrixsJson matrixs对象
     * @return
     */
    public static Matrixs fromJson(String matrixsJson) {
        return JSON.parseObject(matrixsJson, Matrixs.class);
    }
}
