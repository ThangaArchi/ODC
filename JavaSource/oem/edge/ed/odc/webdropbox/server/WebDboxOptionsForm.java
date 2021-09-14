package oem.edge.ed.odc.webdropbox.server;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

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
 * WebDboxOptionsForm.java is the Action Form associated with important
 * elements on the WebDboxOptions.jsp.Also provides validations for form elements. * 
 *
 **/
public class WebDboxOptionsForm extends ActionForm {
	
  // JMC Storage Pools
   String poolName;
   String validItarSession;
   String optItarCertInfo="NotoptItarCertInfo";
   
   boolean sendNotificationDefault=false;
   boolean returnReceiptDefault=false;
   boolean newPackageEmailNotification=false;
   boolean nagNotification=false;
   
   
   public void setPoolName(String v) { poolName = v;    }
   public String getPoolName()       { return poolName; }
   public void setValidItarSession(String validItarSession) { this.validItarSession = validItarSession;}
   public String getValidItarSession() { return validItarSession;}
    



/**
 * @return Returns the nagNotification.
 */
public boolean isNagNotification() {
	return nagNotification;
}
/**
 * @param nagNotification The nagNotification to set.
 */
public void setNagNotification(boolean nagNotification) {
	this.nagNotification = nagNotification;
}
/**
 * @return Returns the newPackageEmailNotification.
 */
public boolean isNewPackageEmailNotification() {
	return newPackageEmailNotification;
}
/**
 * @param newPackageEmailNotification The newPackageEmailNotification to set.
 */
public void setNewPackageEmailNotification(boolean newPackageEmailNotification) {
	this.newPackageEmailNotification = newPackageEmailNotification;
}
/**
 * @return Returns the returnReceiptDefault.
 */
public boolean isReturnReceiptDefault() {
	return returnReceiptDefault;
}
/**
 * @param returnReceiptDefault The returnReceiptDefault to set.
 */
public void setReturnReceiptDefault(boolean returnReceiptDefault) {
	this.returnReceiptDefault = returnReceiptDefault;
}
/**
 * @return Returns the sendNotificationDefault.
 */
public boolean isSendNotificationDefault() {
	return sendNotificationDefault;
}
/**
 * @param sendNotificationDefault The sendNotificationDefault to set.
 */
public void setSendNotificationDefault(boolean sendNotificationDefault) {
	this.sendNotificationDefault = sendNotificationDefault;
}
/**
 * @return Returns the optItarCertInfo.
 */
public String getOptItarCertInfo() {
	return optItarCertInfo;
}
/**
 * @param optItarCertInfo The optItarCertInfo to set.
 */
public void setOptItarCertInfo(String optItarCertInfo) {
	this.optItarCertInfo = optItarCertInfo;
}
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		// Reset field values here.
        setValidItarSession("false");
        setNagNotification(false);
        setNewPackageEmailNotification(false);
        setReturnReceiptDefault(false);
        setSendNotificationDefault(false);
        setOptItarCertInfo("NotoptItarCertInfo");
	
	}

	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {
			
			
			
			ActionErrors errors = super.validate(mapping, request);
		    if (errors == null) errors = new ActionErrors();
		    
		

		return errors;

	}
	
	
	

	

}
