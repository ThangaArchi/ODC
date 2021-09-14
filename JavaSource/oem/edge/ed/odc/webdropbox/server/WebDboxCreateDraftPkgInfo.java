
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
 * The Bean WebDboxCreateDraftPkgInfo.java is populated with important data which is used
 * for rendering the Createpackage.jsp
 * Webpage Information pertaining to the Package name,acls,expiration,files 
 * etc. is stored in this bean
 */
public class WebDboxCreateDraftPkgInfo {

	
		Vector recipientsList; 
		Enumeration rcptEnum;
		
		Vector sendList; 
		Enumeration sendEnum;
		
		boolean disablePkgNameField;
		boolean editPackageRcpt;	
		boolean containsItarDataCbx;
		String valSessItar="false";
		String itarSel="unselect";
		
		
		Vector expirationValues;
		String selectedExpireValue;
		
		String pkgExpireDate;
		String calendarEndDate;
		
		
//		 JGSDC
		   Vector storagePools;
		   PoolInfo selectedPool;
		   Vector crossRcptList;
		   String crossRcptShow;
		  
		  int crossRcptListLen;
		
//		 JGSDC Storage Pools
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
		
		public WebDboxCreateDraftPkgInfo(){}
		
	
		public Vector getRecipientsList() {	return recipientsList;	}
		public void setRecipientsList(Vector vector) {	recipientsList = vector; }

		
		public Enumeration getRcptEnum() {	return rcptEnum; }
		public void setRcptEnum(Enumeration enumeration) {	rcptEnum = enumeration;	}

		public Vector getSendList() { return sendList;	}
		public void setSendList(Vector vector) { sendList = vector;	}

		public Enumeration getSendEnum() {	return sendEnum; }
		public void setSendEnum(Enumeration enumeration) {sendEnum = enumeration;	}

		public boolean isDisablePkgNameField() { return disablePkgNameField; }
		public void setDisablePkgNameField(boolean b) {	disablePkgNameField = b; }

		public boolean isEditPackageRcpt() { return editPackageRcpt; }
		public void setEditPackageRcpt(boolean b) {	editPackageRcpt = b; }

		public void generateExpirationVals(){
			expirationValues=new Vector();
			for(int i=1;i<=14;i++){
				expirationValues.add(String.valueOf(i));				
			}
		}
		
		public String getSelectedExpireValue() { return selectedExpireValue; }
		public void setSelectedExpireValue(String string) {	selectedExpireValue = string; }

		public Vector getExpirationValues() { return expirationValues;	}
		public void setExpirationValues(Vector vector) {expirationValues = vector;	}


		/**
		 * @param b
		 */
		public void setContainsItarDataCbx(boolean b) {
			containsItarDataCbx=b;
			
		}

		/**
		 * @return Returns the containsItarDataCbx.
		 */
		public boolean isContainsItarDataCbx() {
			return containsItarDataCbx;
		}
		/**
		 * @return Returns the pkgExpireDate.
		 */
		public String getPkgExpireDate() {
			return pkgExpireDate;
		}
		/**
		 * @param pkgExpireDate The pkgExpireDate to set.
		 */
		public void setPkgExpireDate(String pkgExpireDate) {
			this.pkgExpireDate = pkgExpireDate;
		}
		/**
		 * @return Returns the itarSel.
		 */
		public String getItarSel() {
			return itarSel;
		}
		/**
		 * @param itarSel The itarSel to set.
		 */
		public void setItarSel(String itarSel) {
			this.itarSel = itarSel;
		}
		/**
		 * @return Returns the valSessItar.
		 */
		public String getValSessItar() {
			return valSessItar;
		}
		/**
		 * @param valSessItar The valSessItar to set.
		 */
		public void setValSessItar(String valSessItar) {
			this.valSessItar = valSessItar;
		}
		/**
		 * @return Returns the calendarEndDate.
		 */
		public String getCalendarEndDate() {
			return calendarEndDate;
		}
		/**
		 * @param calendarEndDate The calendarEndDate to set.
		 */
		public void setCalendarEndDate(String calendarEndDate) {
			this.calendarEndDate = calendarEndDate;
		}
		/**
		 * @param rcptCompanies
		 */
		public void setCrossRcptList(Vector rcptCompanies) {
			// TODO Auto-generated method stub
			this.crossRcptList=rcptCompanies;
		}
		/**
		 * @return Returns the crossRcptList.
		 */
		public Vector getCrossRcptList() {
			return crossRcptList;
		}
		/**
		 * @return Returns the crossRcptShow.
		 */
		public String getCrossRcptShow() {
			return crossRcptShow;
		}
		/**
		 * @param crossRcptShow The crossRcptShow to set.
		 */
		public void setCrossRcptShow(String crossRcptShow) {
			this.crossRcptShow = crossRcptShow;
		}
		/**
		 * @param i
		 */
		public void setCrossRcptListLen(int i) {
			
			crossRcptListLen=i;
			
		}
		/**
		 * @return Returns the crossRcptListLen.
		 */
		public int getCrossRcptListLen() {
			return crossRcptListLen;
		}
}
