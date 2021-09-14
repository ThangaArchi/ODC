
package oem.edge.ed.odc.webdropbox.server;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

import oem.edge.ed.util.SearchEtc;

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
 * The Bean WebDboxInboxInfo.java is populated with important data which is used
 * for rendering the WebDboxInbox.jsp
 * Webpage Information pertaining to the Sorted/Unsorted Inbox Packages,Paging Indices
 * etc. is stored in this bean
 **/

public class WebDboxInboxInfo {

	

		
		
		int startIndex;
		int endIndex;
		int totRcvdPkgs;
		
		String selectdPkg;
	    private String selectdPkgWithPkgId;
		
		
		
		Hashtable pkgInboxData=new Hashtable(); 
		Enumeration sortedPkgIds;
		Hashtable pkgNameIdMap=new Hashtable();
		Hashtable pkgContents=new Hashtable();
		
		Vector inboxPackages= new Vector();
		Vector sortedPackages= new Vector();
		Vector packageRange = new Vector();
		
		
		Vector inboxFileContents= new Vector();
		String user;
	    String company;
		
	
		public WebDboxInboxInfo(int start, int end, int totalRcvdPkgs) {
		
			
			this.startIndex=start;
			this.endIndex=end;
			this.totRcvdPkgs=totalRcvdPkgs;
		}
		
		public WebDboxInboxInfo(){}
	
	
		public int getEndIndex() {
			return endIndex;
		}
		
		public void setEndIndex(int i) {
				endIndex = i;
		}
		
		public int getStartIndex() {
			return startIndex;
		}
		public void setStartIndex(int i) {
				startIndex = i;
		}

		
		public int getTotRcvdPkgs() {
			return totRcvdPkgs;
		}
		
		public void setTotRcvdPkgs(int i) {
			totRcvdPkgs = i;
		}
	
		
		public Hashtable getPkgInboxData() {
			return pkgInboxData;
		}
		public void setPkgInboxData(Hashtable hashtable) {
			pkgInboxData = hashtable;
		}

		
		public Enumeration getSortedPkgIds() {
			return sortedPkgIds;
		}
		public void setSortedPkgIds(Enumeration enumeration) {
			sortedPkgIds = enumeration;
		}

		
		public Hashtable getPkgNameIdMap() {
			return pkgNameIdMap;
		}

		
		public void setPkgNameIdMap(Hashtable hashtable) {
			pkgNameIdMap = hashtable;
		}

		
		public Hashtable getPkgContents() {
			return pkgContents;
		}

		
		public void setPkgContents(Hashtable hashtable) {
			pkgContents = hashtable;
		}

		
		public String getSelectdPkg() {
			return selectdPkg;
		}

		public String getEncodedSelectdInboxPkg() {
			return SearchEtc.htmlEscape(getSelectdPkg());
		}
		
		public void setSelectdPkg(String string) {
			selectdPkg = string;
		}
		
		
		

		/**
		 * @return
		 */
		public Vector getInboxPackages() {
			return inboxPackages;
		}

		/**
		 * @return
		 */
		public Vector getSortedPackages() {
			return sortedPackages;
		}

		/**
		 * @param vector
		 */
		public void setInboxPackages(Vector vector) {
			inboxPackages = vector;
		}

		/**
		 * @param vector
		 */
		public void setSortedPackages(Vector vector) {
			sortedPackages = vector;
		}
		
		public long getPackageIdForPackageName(String pkgname) {
			long ret = 0;
			 if (sortedPackages.size() > 0) {
				Enumeration enum = sortedPackages.elements();
				while(enum.hasMoreElements()) {
					DboxPackageInfo finfo = (DboxPackageInfo)enum.nextElement();
				   if (finfo.getPackageName().equals(pkgname)) {
					  ret = (long)Long.parseLong(finfo.getPackageId());
					  break;
				   }
				}
			 }
			 return ret;
		  }

	public String getPackageNameForPackageId(String packId) {
				String ret = null;
				 if (sortedPackages.size() > 0) {
					Enumeration enum = sortedPackages.elements();
					while(enum.hasMoreElements()) {
						DboxPackageInfo finfo = (DboxPackageInfo)enum.nextElement();
					   if (finfo.getPackageId().equals(packId)) {
						  ret = finfo.getPackageName();
						  break;
					   }
					}
				 }
				 return ret;
			  }

			


		/**
		 * @return
		 */
		public Vector getInboxFileContents() {
			return inboxFileContents;
		}

		/**
		 * @param vector
		 */
		public void setInboxFileContents(Vector vector) {
			inboxFileContents = vector;
		}

		/**
		 * @return
		 */
		public Vector getPackageRange() {
			return packageRange;
		}

		/**
		 * @param vector
		 */
		public void setPackageRange(Vector vector) {
			packageRange = vector;
		}


		public void setPackageRangeForDisplay(int start, int end ){
		
			packageRange = new Vector((Collection)sortedPackages.subList(start-1,end));
			
		}

		public Vector getFileDescriptorsForPkgId(long pkgId)
		{
			DboxFileInfo finfo=null;
			Vector temp = new Vector();
			String pkgIdStr=new Long(pkgId).toString();
			if (sortedPackages.size() > 0) {
				 Enumeration enum = sortedPackages.elements();
				 while(enum.hasMoreElements()) {
					DboxPackageInfo pinfo = (DboxPackageInfo)enum.nextElement();
					if (pinfo.getPackageId().equals(pkgIdStr)) {
						temp= (Vector)(pinfo.getFileContents());
					   break;
					}
				 }
			 }
		
			return temp;
		}

		/**
		 * @return
		 */
		public String getCompany() {
			return company;
		}

		/**
		 * @return
		 */
		public String getUser() {
			return user;
		}

		/**
		 * @param string
		 */
		public void setCompany(String string) {
			company = string;
		}

		/**
		 * @param string
		 */
		public void setUser(String string) {
			user = string;
		}

		/**
		 * @param string
		 */
		public void setSelectdPkgWithPkgId(String string) {
			
			selectdPkgWithPkgId=string;
		}
	        /**
			 * @return
			 */
			public String getSelectdPkgWithPkgId() {
				return selectdPkgWithPkgId;
			}
			
			

}
