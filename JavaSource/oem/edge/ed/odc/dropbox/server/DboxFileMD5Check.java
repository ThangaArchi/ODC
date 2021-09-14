/*
 * Created on Oct 12, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.dropbox.server;


import java.util.*;
import java.sql.*;
import java.io.*;

import  oem.edge.ed.odc.util.*;

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
public class DboxFileMD5Check {
   protected FileManager fileManager = null;
   Vector failed = new Vector();
   
   
   public DboxFileMD5Check(){		
   }
	
   public static void main(String[] args) {
      DboxFileMD5Check check = null;   
      try{
         if (args.length < 4) {
            System.out.println("<<<<  Please provide Database info  >>>>");
            System.exit(1);
         }
         check = new DboxFileMD5Check();
         check.setDBValues(args);
         if (args.length > 4) {
            check.checkAllFilesMD5(Long.parseLong(args[4]));
         } else {
            check.checkAllFilesMD5(-1L);
         }
         
			
      } catch(Exception ex){
         ex.printStackTrace();		
      }
      
      if (check.failed.size() > 0) {
         System.out.println("\n  Summary of failures");
         System.out.println("-------------------------\n");
            Enumeration enum = check.failed.elements();
         
         while(enum.hasMoreElements()) {
            String s = (String)enum.nextElement();
            System.out.println(s);
         }
      }
   }
	
	
   public void checkAllFilesMD5(long fileid){
      System.out.println("Starting the DboxFileMD5Check....");		
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      Connection connection=null;
      StringBuffer sql = null;
		
      try{
         connection=DbConnect.makeConn();
			
         sql = new StringBuffer ("SELECT f.FILEID FROM EDESIGN.FILE f, EDESIGN.PACKAGE p, EDESIGN.FILETOPKG fp WHERE f.FILEID=fp.FILEID AND fp.PKGID=p.PKGID ");

         if (fileid > 0) {
            sql.append(" AND f.fileid = ").append(fileid);
         }
         
         sql.append(" AND p.DELETED is null and f.DELETED is null and fp.DELETED is null GROUP BY f.FILEID WITH UR");
         System.out.println(sql.toString());
         pstmt=connection.prepareStatement(sql.toString());
         rs=DbConnect.executeQuery(pstmt);
			
         while(rs.next()){
            fileid = rs.getLong(1);
           //System.out.println("Checking for fileid="+fileid);
            checkFileMD5(fileid);
         }			
			
      }catch (SQLException e) {
        //handle alert
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      System.out.println("Finished running DboxFileMD5Check....");
   }
	
	
   public void checkFileMD5(long fileid){
		
      String builtMD5 = "";
      String fromfileMD5 = null;
      int i=0;
		
      try{
			
         MessageDigestI digest = new DropboxFileMD5();
			
         System.out.println("\n\n-------------------------------------------------------------------------\n");
         System.out.println("Checking file: " + fileid);
         
         Vector components = getComponents(fileid);
        //System.out.println("Finished getting the components");
         Enumeration enum = components.elements();
         while(enum.hasMoreElements()) {
         
            i++;
            DboxFileComponent comp = (DboxFileComponent)enum.nextElement();				
            System.out.println("Checking Component: " + comp.toString());
            
            InputStream fis = comp.makeInputStream();
           //System.out.println("Inputstream created");
            long clen = comp.getFileSize();
            byte[] buf = new byte[1024*100];

            while (clen > 0) {
               int r = (int)clen;
               if (r > buf.length) r = buf.length;
					
               r = fis.read(buf, 0, r);
               if (r < 0) {
                  break;
               }
               
               if (r > 0) {
                  digest.update(buf, 0, r);
                  clen -= r;
               }
            }
            
            if (clen == 0) {
               fromfileMD5 = digest.hashAsString();
            } else {
               failed.addElement("Component Length != than expected: " + 
                                 fileid + " component: " + i);
               throw new Exception("!!!Component length != expected!");
            }
				
            try{
				
               MessageDigestI builtDigest = comp.getComponentMD5State();					
               builtMD5 = builtDigest.hashAsString();
					
            }catch(Exception ex){
               failed.addElement("Error getting builtMD5 for file: " + fileid + " component: " + i);
               builtMD5="";
               System.out.println("!!!MD5 State at comp "+i+" might be empty or different object for fileid="+fileid);	
               ex.printStackTrace(System.out);
            }
				
            if(fromfileMD5.equals(builtMD5)){
               System.out.println("@@@Dropbox stored state md5 and file md5 at comp "+i+" are matching for fileid="+fileid);
					
            }else{
               System.out.println("!!!Error:Dropbox stored state md5 and file md5 at comp "+i+" are NOT matching for fileid="+fileid);
               
               System.out.println("    fromfile: " + fromfileMD5);
               System.out.println("       built: " + builtMD5);
               
               failed.addElement("No Match for file: " + fileid + " component: " + i);
            }
         }
			
      } catch(Exception ex){
         System.out.println("\n\nError while checking MD5 issues for fileid = " +
                            fileid);
         ex.printStackTrace(System.out);
      }
   }
	
	
   public Vector getComponents(long fileid) throws Exception {
	   
	  
      Connection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;	   
      Vector ret = new Vector();
      DboxFileComponent comp = null;
      try{
			
         connection=DbConnect.makeConn();
         sql = new StringBuffer("SELECT COMPONENTSIZE, COMPINTENDEDSIZE, FILENAME, MD5BLOB, STARTOFS  ");
         sql.append("FROM EDESIGN.FILECOMPONENT fc WHERE fc.FILEID=? AND fc.DELETED is NULL ORDER BY STARTOFS ASC with ur");
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);

         rs=DbConnect.executeQuery (pstmt);
         while (rs.next()){
            comp = new DB2DboxFileComponent (fileManager, fileid, rs.getLong(1), rs.getLong(2), rs.getLong(5), rs.getString(3));
            
            try{
 				
               comp.setMD5blobBytes(rs.getBytes(4));
 				
            }catch(Exception ex){
               
              //blob might be empty
            }				

            comp.threadedCloseWillHelp(comp.getFullPath().startsWith("/afs"));
            ret.addElement(comp); 
         }
      } catch (SQLException e) {
        //handle alert
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      
      return ret;
   }
	
  /*
   * Got this code snippet from DropboxServer.java for db connection
   */
   public void setDBValues(String[] args){
      DBConnection conn = new DBConnLocalPool();
      conn.setDriver     (args[0]);
      conn.setURL        (args[1]);
      conn.setInstance   (args[2]);
      conn.setPasswordDir(args[3]);
            
     // Refactored the code to share Groups code ... use a different
     //  search qualifier to find the same DBConnection object
      DBSource.addDBConnection("dropbox", conn, false);
      DBSource.addDBConnection("GROUPS", conn, false);

   }
}
