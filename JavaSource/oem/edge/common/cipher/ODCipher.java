package oem.edge.common.cipher;

/**
 * Interface supporting the encoding and decoding of data
 */
public interface ODCipher {
  /**
   * Encrypt the provided string and have it tagged to expire in advanceSecs from now
   *
   * @param  advanceSecs   Number of seconds from the current time which the token
   *                       should be considered to be expired
   * @param  toencode      String containing data to be encoded. The string will be
   *                        converted to a byte array prior to encoding
   * @return ODCipherData  Encoded data
   * @throws               CipherException describes failure of routine
   */
   public ODCipherData encode(int advanceSecs, String toencode) throws CipherException;
   
  /**
   * Encrypt the provided byte array and have it tagged to expire in advanceSecs from now
   *
   * @param  advanceSecs   Number of seconds from the current time which the token
   *                       should be considered to be expired
   * @param  toencode      Byte array containing data to be encoded
   * @return ODCipherData  Encoded data
   */
   public ODCipherData encode(int advanceSecs, byte toencode[]) throws CipherException;
   
  /**
   * Encrypt the provided byte array and have it tagged to expire in advanceSecs from now
   *
   * @param  advanceSecs   Number of seconds from the current time which the token
   *                       should be considered to be expired
   * @param  toencode      Byte array containing data to be encoded
   * @param  ofs           Index into toencode to start encoding
   * @param  len           Number of bytes to encode from array
   * @return ODCipherData  Encoded data
   */
   public ODCipherData encode(int advanceSecs, byte toencode[], 
                              int ofs, int len) throws CipherException;
   
  /**
   * Decrypt the provided byte array
   *
   * @param  todecode      Array containing data to decode
   * @return ODCipherData  Decoded data
   * @throws               DecodeException describes failure of routine
   */
   public ODCipherData decode(byte todecode[]) throws DecodeException;
   
  /**
   * Decrypt the provided byte array, starting from ofs for len bytes
   *
   * @param  todecode      Array containing data to decode
   * @param  ofs           Index into toencode to start encoding
   * @param  len           Number of bytes to encode from array
   * @return ODCipherData  Decoded data
   * @throws               DecodeException describes failure of routine
   */
   public ODCipherData decode(byte todecode[], int ofs, int len) throws DecodeException;
   
  /**
   * Decrypt the provided String, which is assumed to be a series of hex digits. The
   *  string is converted to a byte array matching the hex codes.
   *
   * @param  todecode      String containing hexcodes describing data to decode
   * @return ODCipherData  Decoded data
   * @throws               DecodeException describes failure of routine
   */
   public ODCipherData decode(String todecode) throws DecodeException;
}
