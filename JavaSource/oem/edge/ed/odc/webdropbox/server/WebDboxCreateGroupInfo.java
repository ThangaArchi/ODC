
package oem.edge.ed.odc.webdropbox.server;

import java.util.*;

import oem.edge.ed.odc.dropbox.common.PoolInfo;

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
 * The Bean WebDboxCreateGroupInfo.java is populated with important data which is used
 * for rendering the CreateGroups.jsp
 * Webpage Information pertaining to the Group members,owners etc. is stored in this bean
 */
public class WebDboxCreateGroupInfo {

	
		Vector recipientsList; 
	    Vector recipientsListEditor;
		Enumeration rcptEnum;
		
		Vector sendList; 
	    Vector sendListEditor;
		Enumeration sendEnum;
		
		boolean disableGroupNameField;
		boolean editGroupRcpt;
	    boolean editGroupMode;	
		
	    Vector listOfGroupNames;
	    
  // JMC
   Vector storagePools;
   PoolInfo selectedPool;
	    
	    String selectedGroupOptionValue;
	    
	    public String groupEditVisibility;
	    public String groupEditListability;
	    
	    public String visibility;
		public String listability;
		
		boolean itarCertification;
		
		public WebDboxCreateGroupInfo(){}
	
	
  // JMC Storage Pools
   public void setStoragePools(Vector v)            { storagePools = v;    }
   public void setSelectedStoragePool(PoolInfo pi)  { selectedPool = pi;   }
   public Vector getStoragePools()                  { return storagePools; }
   public PoolInfo getSelectedStoragePool()         { return selectedPool; }
   public Vector getStoragePoolsString()            { 
      Vector ret = new Vector();
      if (storagePools != null) {
         Iterator it = storagePools.iterator();
         while(it.hasNext()) {
            ret.add(((PoolInfo)it.next()).getPoolName());
         }
      }
      return ret;
   }
   public String getSelectedStoragePoolString()   { 
      return (selectedPool != null) ? selectedPool.getPoolName() : "";
   }
	
		
		public Vector getRecipientsList() {	return recipientsList;	}		
		public void setRecipientsList(Vector vector) {	recipientsList = vector; }

        
		public Vector getRecipientsListEditor() { return recipientsListEditor;	}
		public void setRecipientsListEditor(Vector vector) { recipientsListEditor = vector;	}
		

		public Enumeration getRcptEnum() {	return rcptEnum;	}
		public void setRcptEnum(Enumeration enumeration) {	rcptEnum = enumeration;		}

		public Vector getSendList() { return sendList;	}
		public void setSendList(Vector vector) {	sendList = vector;	}


		public Vector getSendListEditor() {	return sendListEditor;	}
		public void setSendListEditor(Vector vector) {	sendListEditor = vector; }

		
		public Enumeration getSendEnum() {	return sendEnum;	}		
		public void setSendEnum(Enumeration enumeration) {	sendEnum = enumeration;	}

		
		public boolean isDisableGroupNameField() { return disableGroupNameField; }
		public void setDisableGroupNameField(boolean b) { disableGroupNameField = b; }

		
		public boolean isEditPackageRcpt() { return editGroupRcpt;	}
		public void setEditPackageRcpt(boolean b) {	editGroupRcpt = b;	}

		
		public Vector getListOfGroupNames() {	return listOfGroupNames;	}
		public void setListOfGroupNames(Vector vector) { listOfGroupNames = vector;	}
		
	
		public String getSelectedGroupOptionValue() {	return selectedGroupOptionValue; }		
		public void setSelectedGroupOptionValue(String string) { selectedGroupOptionValue = string;	}


		public void setEditGroupMode(boolean b) { editGroupMode=b;	}
		public boolean isEditGroupMode() {	return editGroupMode;	}

		
		public String getGroupEditListability() {	return groupEditListability;	}
	    public void setGroupEditListability(String string) { groupEditListability = string;	}
	    		
		public String getGroupEditVisibility() {	return groupEditVisibility;		}
		public void setGroupEditVisibility(String string) {	groupEditVisibility = string;	}

		
		public String getListability() { return listability; }
	    public void setListability(String string) {	listability = string;	}

		public String getVisibility() {	return visibility;	}
		public void setVisibility(String string) {	visibility = string; }


		/**
		 * @param b
		 */
		public void setItarCertification(boolean b) {
			// TODO Auto-generated method stub
			itarCertification=b;
		}

		/**
		 * @return Returns the itarCertification.
		 */
		public boolean isItarCertification() {
			return itarCertification;
		}
}
