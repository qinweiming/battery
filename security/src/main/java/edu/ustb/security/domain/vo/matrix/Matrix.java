package edu.ustb.security.domain.vo.matrix;

/**
 * Created by sunyichao on 2017/1/14.
 * 矩阵坐标对象：
 *  包含矩阵坐标，及改点的私钥、公钥
 */
public class Matrix {
    private Integer axisX;//矩阵X轴
    private Integer axisY;//矩阵Y轴
    private String publicKeyX;//公钥x坐标32进制
    private String publicKeyY;//公钥y坐标32进制
    private String privateKey;//私钥32进制

    public Matrix() {
    }

    public Matrix(Integer axisX, Integer axisY, String publicKeyX, String publicKeyY, String privateKey) {
        this.axisX = axisX;
        this.axisY = axisY;
        this.publicKeyX = publicKeyX;
        this.publicKeyY = publicKeyY;
        this.privateKey = privateKey;
    }

    public Integer getAxisX() {
        return axisX;
    }

    public void setAxisX(Integer axisX) {
        this.axisX = axisX;
    }

    public Integer getAxisY() {
        return axisY;
    }

    public void setAxisY(Integer axisY) {
        this.axisY = axisY;
    }

    public String getPublicKeyX() {
        return publicKeyX;
    }

    public void setPublicKeyX(String publicKeyX) {
        this.publicKeyX = publicKeyX;
    }

    public String getPublicKeyY() {
        return publicKeyY;
    }

    public void setPublicKeyY(String publicKeyY) {
        this.publicKeyY = publicKeyY;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
