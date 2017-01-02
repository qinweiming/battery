package edu.ustb.security.domain.vo.ecc;


/**
 * Created by sunyichao on 2016/12/19.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/** */
public interface Key {
    public Key readKey(InputStream in) throws IOException;
    public void writeKey(OutputStream out) throws IOException;
    public Key getPublic();
    public boolean isPublic();
    public BigInteger getSk();
    public ECPoint getPk();
    public Key getPublicKeyByPoint(ECPoint pointpub);
}
