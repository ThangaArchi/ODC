/*
 * Created on Sep 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.dropbox.server;


import java.security.*;
/**
 * @author v2murali
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ApiMessageDigest implements MessageDigestI {
	
   MessageDigest apiMessageDigest = null;
	
	
   public ApiMessageDigest() throws Exception{
      Init();
   }
	
   public void Init() throws Exception {
      try{
					
         apiMessageDigest = MessageDigest.getInstance("MD5");
			
      }catch(NoSuchAlgorithmException ex){
         throw (new Exception("No MD5 Algorithm found"));
      }
   }
	
   public Object clone() throws CloneNotSupportedException{
      ApiMessageDigest o = (ApiMessageDigest)super.clone();
      o.apiMessageDigest = (MessageDigest)apiMessageDigest.clone();
      return o;
   }  
	
   public int digest(byte[] input, int ofs, int len) throws Exception{
	
      return this.apiMessageDigest.digest(input,ofs,len);
   }
	
   public void update(byte[] v, int ofs, int len){
      this.apiMessageDigest.update(v,ofs,len);
   }
	
   public void update(byte[] v){
      this.apiMessageDigest.update(v);
   }
	
   public void update(byte v){
      this.apiMessageDigest.update(v);
   }
	
   public void setMD5State(DropboxMD5State state){
     //do nothing
   }
   public DropboxMD5State getMD5State(){
      return null;
   }
	
   public boolean supportsStoreAndLoad(){
      return false;
   }

   public String getAlgorithm(){
      return this.apiMessageDigest.getAlgorithm();
   }
	
   public void reset(){
      this.apiMessageDigest.reset();
   }
	
   public byte[] digest(){
      return this.apiMessageDigest.digest();
   }
	
   public byte[] digest(byte[] v){
      return this.apiMessageDigest.digest(v);
   }

   public byte[] Final(){
      return digest();
   }
	
   public void stateFromString(String s) throws Exception{
		
      throw new Exception("stateFromString not possible with MessageDigest");
   }
	
   public String stateToString() throws Exception {
      throw new Exception("stateToString not possible with MessageDigest");
   }
	   
   public void stateFromBytes(byte[] buf, int ofs, int len) throws Exception {
      throw new Exception("stateFromBytes not possible with MessageDigest");
   }
	
   public byte[] stateToBytes() throws Exception {
      throw new Exception("stateToBytes not possible with MessageDigest");
   }
										
   public String hashAsString() {
		
      byte arr[] = this.apiMessageDigest.digest();
      StringBuffer ans = new StringBuffer();
      for(int i=0 ; i < arr.length; i++) {
         String v = Integer.toHexString(((int)arr[i]) & 0xff);
         if (v.length() == 1) ans.append("0");
         ans.append(v);
      }
         
      return  ans.toString();
   }
}
