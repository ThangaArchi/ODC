package oem.edge.ed.odc.dropbox.server;

import java.io.Serializable;
/**
 * @author v2murali
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
class DropboxMD5State implements Serializable {
  /**
   * 128-bit state 
   */
  int	state[];
  
  /**
   * 64-bit character count
   */
  long count;
  
  /**
   * 64-byte buffer (512 bits) for storing to-be-hashed characters
   */
  byte	buffer[];

  public DropboxMD5State() {
    buffer = new byte[64];
    count = 0;
    state = new int[4];
    
    state[0] = 0x67452301;
    state[1] = 0xefcdab89;
    state[2] = 0x98badcfe;
    state[3] = 0x10325476;

  }

  public DropboxMD5State (DropboxMD5State from) {
    this();
    
    int i;
    
    System.arraycopy(from.buffer, 0, this.buffer, 0, this.buffer.length);
    System.arraycopy(from.state, 0, this.state, 0, this.state.length);

    this.count = from.count;
  }
};


