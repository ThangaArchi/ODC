package oem.edge.ed.odc.webdropbox.server;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

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
 * WebDboxCreateDraftPkgForm.java is the Action Form associated with important
 * elements on the Createpackage.jsp.Also provides validations for form elements. * 
 **/
public class WebDboxCreateDraftPkgForm extends ActionForm {
	

	private String packNameField;

	protected String packageName;
	
	protected String iccid;
	
	protected String aclList;
	protected String aclListRcpt;
	
	protected String savedList;
	protected String sendtoList;
	
	protected String expire;
	protected String poolReselectSubmit="no";

	boolean cbx1;
	boolean cbx2;
	boolean containsItarCbx;
	String validItarSession;
	String itarSelect;
	
	String editMode;
	String calEndDate;

	protected String addFiles;
	
	protected String addIccIdUser;
	protected String addIccIdGroup;
	protected String typeOfId;
	
	protected String expirationsOptions;
	
	protected String pkgDesc="";
	
	
	
	public String getPackageName() { return packageName; }
	public void setPackageName(String string) {	packageName = string;}

	public String getIccid() {	return iccid; }
	public void setIccid(String string) {iccid = string; }
	
	public String getSavedList() { return savedList; }
	public void setSavedList(String string) { savedList = string;}

	public String getSendtoList() {	return sendtoList;	}
	public void setSendtoList(String string) { sendtoList = string;	}
	

	public boolean getCbx1() { return cbx1; }
	public void setCbx1(boolean b) { cbx1 = b; }

	public boolean getCbx2() { return cbx2; }
	public void setCbx2(boolean b) { cbx2 = b; }
	
	public boolean isContainsItarCbx() { return containsItarCbx;	}
	public void setContainsItarCbx(boolean containsItar) {	this.containsItarCbx = containsItar;	}
	
	/**
	 * @return Returns the validItarSession.
	 */
	public String getValidItarSession() {
		return validItarSession;
	}
	/**
	 * @return Returns the itarSelect.
	 */
	public String getItarSelect() {
		return itarSelect;
	}
	/**
	 * @param itarSelect The itarSelect to set.
	 */
	public void setItarSelect(String itarSelect) {
		this.itarSelect = itarSelect;
	}
	/**
	 * @param validItarSession The validItarSession to set.
	 */
	public void setValidItarSession(String validItarSession) {
		this.validItarSession = validItarSession;
	}
	public String getExpire() {	return expire;	}
	public void setExpire(String string) {expire = string; 	}

	public String getAclList() { return aclList;}
	public void setAclList(String string) {	aclList = string; }

	public String getAddFiles() { return addFiles;	}
	public void setAddFiles(String string) { addFiles = string;	}

	public String getAddIccIdUser() { return addIccIdUser; }
	public void setAddIccIdUser(String string) { addIccIdUser = string;	}
	
	
	public String getAddIccIdGroup() { return addIccIdGroup; }
	public void setAddIccIdGroup(String string) { addIccIdGroup = string;}
	
	public String getTypeOfId() { return typeOfId; }
	public void setTypeOfId(String string) { typeOfId = string;	}

	public String getAclListRcpt() { return aclListRcpt; }
	public void setAclListRcpt(String string) {	aclListRcpt = string;}

	public String getPackNameField() { return packNameField; }
	public void setPackNameField(String string) { packNameField = string;}
	

	public String getExpirationsOptions() {	return expirationsOptions; }
	public void setExpirationsOptions(String string) {	expirationsOptions = string; }
	
	public String getPkgDesc() {return pkgDesc;}
	public void setPkgDesc(String pkgDesc) { this.pkgDesc = pkgDesc;}
	
//	 JGSDC Storage Pools
	   String poolName;
	   public void setPoolName(String v) { poolName = v;    }
	   public String getPoolName()       { return poolName; }
	   
	   
	/**
	 * @return Returns the poolReselect.
	 */
	public String getPoolReselect() {
		return poolReselect;
	}
	/**
	 * @param poolReselect The poolReselect to set.
	 */
	public void setPoolReselect(String poolReselect) {
		this.poolReselect = poolReselect;
	}
	
	   String poolReselect;
	   
	   String expireDate;

	   private String crossRcptWarning="noshowrcpts";
	   /**
		 * @return Returns the pkgExpireDate.
		 */
		public String getExpireDate() {
			return expireDate;
		}
		/**
		 * @param pkgExpireDate The pkgExpireDate to set.
		 */
		public void setExpireDate(String ExpireDate) {
			this.expireDate = ExpireDate;
		}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// Reset field values here.
		//setUploadFile("NoFileSelected");
		//setFile1name("TestValue");
		//setPackageName("NoPackageName");
		
		setPackageName("");
		setPackNameField("");
		setAclList("");
		setAclListRcpt("");
		//setPkgDesc("");
		setPoolReselectSubmit("no");
		setCrossRcptWarning("noshowrcpts");
		setCbx1(false);
		setCbx2(false);
	}
 
	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {
			
			
			ActionErrors errors = super.validate(mapping, request);
		    if (errors == null) errors = new ActionErrors();
		    
		   
			String s = getPackageName();
			if (  s == null || s.trim().length() == 0 ) {
				errors.add("packageName", 
				new ActionError("error.packageName.value"));
			}
		   
			if (s.indexOf("/") >= 0 
				|| s.indexOf("\\") >= 0 
				)
			{
			
				errors.add("error.packageNameInvalidChar.value", 
				new ActionError("error.packageNameInvalidChar.value"));
			}
			if (s.length()  > 128) {
				errors.add("error.packageNamelong.value", 
								new ActionError("error.packageNamelong.value"));					
			}
			
			s = getPkgDesc();
			if (s.length() > 1024) {
				errors.add("error.pkgDescriptionLen.value", 
								new ActionError("error.pkgDescriptionLen.value"));			
			}
			
			s = getIccid();
			if (s.length() > 128) {
				errors.add("error.aclListlong.value", 
								new ActionError("error.aclListlong.value"));			
			}	
			
			s = getAclList();
			if (  s == null || s.trim().length() == 0 ) {
				errors.add("aclList", 
				new ActionError("error.aclList.value"));
			}
	
		  
		// Validate the fields in your form, adding
		// adding each error to this.errors as found, e.g.

		// if ((field == null) || (field.length() == 0)) {
		//   errors.add("field", new org.apache.struts.action.ActionError("error.field.required"));
		// }
		return errors;

	}
	
	
	

	

	


	


	
	/**
	 * @return Returns the calEndDate.
	 */
	public String getCalEndDate() {
		return calEndDate;
	}
	/**
	 * @param calEndDate The calEndDate to set.
	 */
	public void setCalEndDate(String calEndDate) {
		this.calEndDate = calEndDate;
	}
	/**
	 * @return Returns the editMode.
	 */
	public String getEditMode() {
		return editMode;
	}
	/**
	 * @param editMode The editMode to set.
	 */
	public void setEditMode(String editMode) {
		this.editMode = editMode;
	}
	/**
	 * @return Returns the poolReselectSubmit.
	 */
	public String getPoolReselectSubmit() {
		return poolReselectSubmit;
	}
	/**
	 * @param poolReselectSubmit The poolReselectSubmit to set.
	 */
	public void setPoolReselectSubmit(String poolReselectSubmit) {
		this.poolReselectSubmit = poolReselectSubmit;
	}
	/**
	 * @param string
	 */
	public void setCrossRcptWarning(String string) {
		// TODO Auto-generated method stub
		this.crossRcptWarning=string;
		
	}
	/**
	 * @return Returns the crossRcptWarning.
	 */
	public String getCrossRcptWarning() {
		return crossRcptWarning;
	}
}
