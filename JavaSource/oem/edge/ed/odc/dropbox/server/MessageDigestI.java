/*
 * Created on Sep 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.dropbox.server;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2005-2006                                    */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

/**
 * @author v2murali
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface MessageDigestI {
  /* Interesting methods from java.security.MessageDigest */
   public byte[] digest();
   public byte[] digest(byte[] input);
   public int digest(byte[] input, int ofs, int len) throws Exception;
   public String getAlgorithm();
   public void reset();
   public void update(byte v);
   public void update(byte v[]);
   public void update(byte v[], int ofs, int len);
   public Object clone() throws Exception;       /* Not sure if it will let us do this */


  /* Our extensions */
   public void setMD5State(DropboxMD5State state);
   public DropboxMD5State getMD5State();
   public boolean supportsStoreAndLoad();
  /* Rest of your important methods */
   public void Init () throws Exception;
   public void stateFromString(String s) throws Exception;
   public String stateToString() throws Exception;
   public void stateFromBytes(byte[] buf, int ofs, int len) throws Exception;
   public byte[] stateToBytes() throws Exception;
   public byte[] Final();
   public String hashAsString();
	
}
