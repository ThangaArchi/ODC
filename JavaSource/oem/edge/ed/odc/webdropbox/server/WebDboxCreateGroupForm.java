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
 * WebDboxCreateGroupForm.java is the Action Form associated with important
 * elements on the CreateGroups.jsp.Also provides validations for form elements. *
 *  
 **/

public class WebDboxCreateGroupForm extends ActionForm {
	

	private String groupNameField;

	protected String groupName;
	
	protected String iccid;
	
	protected String aclList;
	protected String aclListRcpt;
	
	protected String savedList;
	protected String sendtoList;
	
	
	protected String iccidEditor;
	
	protected String aclListEditor;
	protected String aclListRcptEditor;

	protected String savedListEditor;
	protected String sendtoListEditor;
	
	protected String expire;
	
	boolean cbx1;
	boolean cbx2;

	protected String addFiles;
	
	protected String addIccIdUser;
	protected String addIccIdGroup;
	protected String typeOfId;
	
	protected String groupCreateOrEditOptions;
	
	protected boolean groupOwnVis;
	protected boolean groupMemVis;
	protected boolean groupOwnLis;
	protected boolean groupMemLis;
	
	protected String visibility;
	protected String listablility;
	
	protected String editvisibility;
	protected String editlistablility;
	
	
	
	public String getGroupName() { return groupName; }
	public void setGroupName(String string) {	groupName = string;}

	public String getIccid() {	return iccid; }
	public void setIccid(String string) {iccid = string; }
	
	public String getSavedList() { return savedList; }
	public void setSavedList(String string) { savedList = string;}

	public String getSendtoList() {	return sendtoList;	}
	public void setSendtoList(String string) { sendtoList = string;	}
	
	
	public String getIccidEditor() {	return iccidEditor; }
	public void setIccidEditor(String string) {iccidEditor = string; }
	
	
	public String getAclListEditor() { return aclListEditor;}
	public void setAclListEditor(String string) {	aclListEditor = string; }
	
	public String getAclListRcptEditor() { return aclListRcptEditor;}
	public void setAclListRcptEditor(String string) {	aclListRcptEditor = string; }

	public String getSavedListEditor() { return savedListEditor; }
	public void setSavedListEditor(String string) { savedListEditor = string;}

	public String getSendtoListEditor() {	return sendtoListEditor;	}
	public void setSendtoListEditor(String string) { sendtoListEditor = string;	}
	

	public boolean getCbx1() { return cbx1; }
	public void setCbx1(boolean b) { cbx1 = b; }

	public boolean getCbx2() { return cbx2; }
	public void setCbx2(boolean b) { cbx2 = b; }

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

	public String getGroupNameField() { return groupNameField; }
	public void setGroupNameField(String string) { groupNameField = string;}
	
	public String getGroupCreateOrEditOptions() { return groupCreateOrEditOptions; }

	public void setGroupCreateOrEditOptions(String string) { groupCreateOrEditOptions = string;	}
	
	
	public boolean isGroupMemLis() {return groupMemLis; }
	public void setGroupMemLis(boolean b) {	groupMemLis = b;}

	
	public boolean isGroupMemVis() {return groupMemVis;	}
	public void setGroupMemVis(boolean b) {	groupMemVis = b;}


	public boolean isGroupOwnLis() {return groupOwnLis;	}
	public void setGroupOwnLis(boolean b) {	groupOwnLis = b;	}

	public boolean isGroupOwnVis() {return groupOwnVis;	}
	public void setGroupOwnVis(boolean b) {	groupOwnVis = b;	}


	
	public String getListablility() { return listablility; }
	public void setListablility(String string) { listablility = string;	}
	
	public String getVisibility() {	return visibility;	}
	public void setVisibility(String string) {	visibility = string; }

	
	public String getEditlistablility() { return editlistablility;	}
	public void setEditlistablility(String string) { editlistablility = string;	}
	
	public String getEditvisibility() {	return editvisibility;	}	
	public void setEditvisibility(String string) {	editvisibility = string; }

	
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// Reset field values here.		
		//setUploadFile("NoFileSelected");
		//setFile1name("TestValue");
		//setPackageName("NoPackageName");
		setTypeOfId("Create new group...");
	}
 
	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {
			
			
			ActionErrors errors = super.validate(mapping, request);
		    if (errors == null) errors = new ActionErrors();
		    
		   
			String s = getGroupName();
			if (  s == null || s.trim().length() == 0 ) {
				errors.add("error.groupName.value", 
				new ActionError("error.groupName.value"));
			}
		
			if (s.indexOf("/") >= 0 
					|| s.indexOf("\\") >= 0 
					)
				{
			
				        errors.add("error.groupNameInvalidChar.value", 
						new ActionError("error.groupNameInvalidChar.value"));
				}
			
			/*if (s.indexOf("/") >= 0 
				|| s.indexOf("\\") >= 0 
				|| s.indexOf(",")  >= 0 
				|| s.indexOf("\"") >= 0 
				|| s.indexOf(";")  >= 0
				|| s.indexOf("@")  >= 0
				|| s.indexOf("=")  >= 0
				|| s.indexOf("+")  >= 0
			    || s.indexOf("$")  >= 0
			    || s.indexOf("&")  >= 0	
			    || s.indexOf("#")  >= 0
			    || s.indexOf("*")  >= 0
			    || s.indexOf(":")  >= 0	
				|| s.indexOf(">")  >= 0
			    || s.indexOf("<")  >= 0
			    || s.indexOf("`")  >= 0
			    || s.indexOf("~")  >= 0
			    || s.indexOf("!")  >= 0
			    || s.indexOf("%")  >= 0	    			
				)
			{

				errors.add("error.groupNameInvalidChar.value", 
				new ActionError("error.groupNameInvalidChar.value"));
			}*/

			if (s.length()  > 128) {
				errors.add("error.groupNameLong.value", 
								new ActionError("error.groupNameLong.value"));					
			}
			
		
			s = getAclList();
			
/*			
			if ( ( s == null || s.trim().length() == 0 ) && getTypeOfId().equalsIgnoreCase(getGroupCreateOrEditOptions()) ) {					
				errors.add("error.groupmemberlist.value", 
				new ActionError("error.groupmemberlist.value"));
			}
			
           
			
			if (!getTypeOfId().equalsIgnoreCase(getGroupCreateOrEditOptions()))
			{
				if ( getAclList() == null || getAclList().trim().length() == 0 )
				{					 	
					errors.add("error.groupmemberlist.value", new ActionError("error.groupmemberlist.value"));
				}
		    }
*/		
		  
		// Validate the fields in your form, adding
		// adding each error to this.errors as found, e.g.

		// if ((field == null) || (field.length() == 0)) {
		//   errors.add("field", new org.apache.struts.action.ActionError("error.field.required"));
		// }
		return errors;

	}
	
	
	

	

	


}
