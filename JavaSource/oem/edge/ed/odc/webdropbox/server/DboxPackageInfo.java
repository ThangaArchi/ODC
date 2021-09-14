package oem.edge.ed.odc.webdropbox.server;

import oem.edge.ed.odc.dropbox.common.DropboxGenerator;
import oem.edge.ed.util.SearchEtc;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.net.URLEncoder;
import java.text.*;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

/**
 * DboxPackageInfo.java hosts dropbox package related information
 *  
 **/
public class DboxPackageInfo {
	
	
	   String expires;
	   String packageId = "";
	   String packageName = "";
	   String owner = "";
	   String company = "";
	   String size = "";
	   String state = "";
	   String numfiles = "";
	   String xferrate = "";
	   String created = "";
	   String committed = "";
	   String deleted = "";
	   String pkgDesc = "";
	   
	   protected byte   packagestatus = DropboxGenerator.STATUS_INCOMPLETE;
	   
	protected byte   flags         = (byte)0;
   
	 // These flags are per user per package, and so are calculated
	  public static final byte MARKED        = (byte)0x01;
	  public static final byte COMPLETED     = (byte)0x02;
   
	 // These flags are stored as part of the package
	  public static final byte RETURNRECEIPT = (byte)0x80;
	  public static final byte SENDNOTIFY    = (byte)0x40;
	  public static final byte HIDDEN        = (byte)0x20;
	   
	   
	   
   
	   boolean islimited = false;
   
	   Vector files   = new Vector();
	   Vector aclinfo = new Vector();
	   Vector accinfo = new Vector();

	   public DboxPackageInfo() {
	   }
   
	   public void addFile(DboxFileInfo file) {
		  files.addElement(file);		 
	   }
   
	   public DboxFileInfo getFileById(String fileid) {
		DboxFileInfo ret = null;
		  if (files.size() > 0) {
			 Enumeration enum = files.elements();
			 while(enum.hasMoreElements()) {
				DboxFileInfo finfo = (DboxFileInfo)enum.nextElement();
				if (finfo.getFileId().equals(fileid)) {
				   ret = finfo;
				   break;
				}
			 }
		  }
		  return ret;
	   }
   
	   public DboxFileInfo getFileByName(String filename) {
		DboxFileInfo ret = null;
		  if (files.size() > 0) {
			 Enumeration enum = files.elements();
			 while(enum.hasMoreElements()) {
				DboxFileInfo finfo = (DboxFileInfo)enum.nextElement();
				if (finfo.getFileName().equals(filename)) {
				   ret = finfo;
				   break;
				}
			 }
		  }
		  return ret;
	   }
   
		public Enumeration getPkgIds() {
			Enumeration enum = null;
			
			
		
		
			return enum;
		}
   	   	
   
	
	   public Enumeration getFiles()      { return files.elements(); }
	   public Enumeration getAclInfo()    { return aclinfo.elements(); }
	   public Enumeration getAccessInfo() { return accinfo.elements(); }
   
	   public boolean isLimited()           { return islimited; }
	   public void    setLimited(boolean v) { islimited = v;    }
   
	   public String getPackageId()         { return packageId; }
	   public void   setPackageId(String v) { packageId = v;    }
   
	   public String getPackageName()         { return SearchEtc.htmlEscape(packageName); }
	   public void   setPackageName(String v) { packageName = v;    }
   
	   public String getPackageOwner()         { return owner; }
	   public void   setPackageOwner(String v) { owner = v;    }
   
	   public String getPackageCompany()         { return company; }
	   public void   setPackageCompany(String v) { company = v;    }
   
	   public String getPackageSize()         { return size; }
	   public void   setPackageSize(String v) { size = v;    }
   
	   public String getPackageState()         { return state; }
	   public void   setPackageState(String v) { state = v;    }
   
	   public String getPackageNumFiles()         { return numfiles; }
	   public void   setPackageNumFiles(String v) { numfiles = v;    }
   
	   public String getPackageTransferRate()         { return xferrate; }
	   public void   setPackageTransferRate(String v) { xferrate = v;    }
   
	   public String getPackageCreated()         { return created; }
	   public void   setPackageCreated(String v) { created = v;    }
   
	   public String getPackageCommitted()         { return committed; }
	   public void   setPackageCommitted(String v) { committed = v;    }
   
	   public String getPackageDeleted()         { return deleted; }
	   public void   setPackageDeleted(String v) { deleted = v;    }

	   public String getPackageExpiration()         { return expires; }
	   public void   setPackageExpiration(String v) { expires = v;    }

			
		public Vector getFileContents() {	return files;	}
			
		public void setFileContents(Vector vector) { files = vector; }

	
		public byte getPackagestatus() {
			return packagestatus;
		}
	
		public void setPackagestatus(byte b) {
			packagestatus = b;
		}


	public void setPackageMarked(boolean v)  { 
		  if (v) flags |=  MARKED; 
		  else   flags &= ~MARKED;
	   }
	   public void setPackageCompleted(boolean v)  { 
		  if (v) flags |=  COMPLETED; 
		  else   flags &= ~COMPLETED;
	   }
	   public void setPackageHidden(boolean v)  { 
		  if (v) flags |=  HIDDEN; 
		  else   flags &= ~HIDDEN;
	   }
	   public void setPackageReturnReceipt(boolean v)  { 
		  if (v) flags |=  RETURNRECEIPT; 
		  else   flags &= ~RETURNRECEIPT;
	   }
	   public void setPackageSendNotification(boolean v)  { 
		  if (v) flags |=  SENDNOTIFY; 
		  else   flags &= ~SENDNOTIFY;
	   }
	   public void setPackageFlags(byte flgs)   { flags = flgs;      }
	   
	   
	   
	   public boolean getPackageMarked()      { return (flags & MARKED)    != 0; }
	   public boolean getPackageCompleted()   { return (flags & COMPLETED) != 0; }
	   public boolean getPackageHidden()      { return (flags & HIDDEN)    != 0; }
	   public boolean getPackageReturnReceipt() { 
		  return (flags & RETURNRECEIPT) != 0; 
	   }
	   public boolean getPackageSendNotification() { 
		  return (flags & SENDNOTIFY) != 0; 
	   }
	   public byte    getPackageFlags()       { return flags;         }

	   public String useAlteredPackageNameForInbox(){
	   	
	   	String str=getPackageName();
	   	String newStr=getPackageId();
	   	return newStr;	   	
	   
	   
	   }
	   
	public String useAlteredPackageNameForTrash(){
	   	
			String str=getPackageName();	   
			String newStr=getPackageId();
			return newStr;	   	
	   
	   
		   }
	   
	   
	    public String useAlteredPackageNameForDrafts(){
	   	
		   String str=getPackageName();	   
		   String newStr=getPackageId();
		   return newStr;
	   	
	   
	   
		  }
		  
	public String useAlteredPackageNameForSent(){
	   	
			   String str=getPackageName();	   
			   String newStr=getPackageId();
			   return newStr;
	   	
	   
	   
			  }


	/**
	 * @return Returns the pkgDesc.
	 */ 
	public String getPkgDesc() {
		return SearchEtc.htmlEscape(pkgDesc);
	}
	/**
	 * @param pkgDesc The pkgDesc to set.
	 */
	public void setPkgDesc(String pkgDesc) {
		this.pkgDesc = pkgDesc;
	}
	
	
	

	
}
