package oem.edge.common.cipher;

import oem.edge.common.RSA.RSAKeyPair;

import java.util.*;
import java.io.*;

/**
**  Encrypts small amounts of data. To be used for token exchange.
**   The encrypted data is timestamped for expiration. Instances
**   of this interface should be
*/
public interface ODCipherRSA extends ODCipher {

  /**
   * Loads and initializes the cipher instance with an RSAKeyPair from the specified
   *  file.
   *
   * @param  filename      Fullpath to file containing the stored key information in the
   *                       format appropriate for the cipher instance
   * @return void          
   * @throws               IOException describes failure of routine
   */
   public void load(String filename) throws IOException;
   
  /**
   * Stores the current cipher RSAKeyPair to the specified file
   *
   * @param  filename      Fullpath to file to receive the key information
   * @return void          
   * @throws               IOException describes failure of routine
   */
   public void store(String filename) throws IOException;
   
  /**
   * Set the keypair associated with this cipher object. The keypair reference is kept
   *  directly (no copy is made).
   *
   * @param  keypair       Keypair instance to associate with this cipher instance
   * @return void          
   */
   public void setKeyPair(RSAKeyPair keypair);
   
  /**
   * Returns the keypair reference associated with this object
   *
   * @return RSAKeyPair    keypair associated with this cipher instance. Could be null.
   */
   public RSAKeyPair getKeyPair();
   
  /**
   * Creates a new keypair of the specified keylength, and both sets it as the
   *  current keypair for this cipher, and returns it.
   */
   public void initializeKeyPair(int keylen) throws CipherException;
   
}
