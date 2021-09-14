package oem.edge.common.RSA;
import java.math.BigInteger;
import java.io.*;

import java.security.*;
import javax.crypto.*;

public interface RSAKeyPair {
   public void initializeKeyPair(int len) throws Exception;
   public void setPublicKey(RSAPublicKey p);
   public void setPrivateKey(RSAPrivateKey p);
   public RSAPrivateKey getPrivateKey();
   public RSAPublicKey getPublicKey();
   public void load(String file) throws IOException;
   public void store(String file) throws IOException;
}
