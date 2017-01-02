package edu.ustb.security.domain.vo.ecc;

import java.math.BigInteger;

/**
 * Created by sunyichao on 2016/12/19.
 * Cpk签名
 */
public class Pair {
    public BigInteger r;
    public BigInteger s;

    public Pair() {
    }

    public Pair(BigInteger r, BigInteger s) {
        this.r = r;
        this.s = s;
    }

}