
package oem.edge.ed.odc.webdropbox.server;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

import oem.edge.ed.util.SearchEtc;

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
 * The Bean WebDboxDraftPkgInfo.java is populated with important data which is used
 * for rendering the WebDboxDraftPkg.jsp
 * Webpage Information pertaining to the Sorted/Unsorted Draft Packages,Paging Indices 
 * etc. is stored in this bean
 */
public class WebDboxDraftPkgInfo {

	

		
		
		int startDraftIndex;
		int endDraftIndex;
		int totDraftdPkgs;
		
		String selectdDraftPkg;
 private String selectdPkgWithPkgId;
		
		String draftPkgToLit;
		
		
		
		Hashtable pkgDraftData=new Hashtable(); 
		Enumeration sortedDraftPkgIds;
		Hashtable pkgDraftNameIdMap=new Hashtable();
		Hashtable pkgDraftContents=new Hashtable();
		
		Vector draftPackages= new Vector();
		Vector sortedDraftPackages= new Vector();
		Vector packageDraftRange = new Vector();
		
		
		Vector draftFileContents= new Vector();
		long draftFileContentsLen;
		
	    
		
	   	public WebDboxDraftPkgInfo(int start, int end, int totalRcvdPkgs) {
		
			
			this.startDraftIndex=start;
			this.endDraftIndex=end;
			this.totDraftdPkgs=totalRcvdPkgs;
		}
		
		public WebDboxDraftPkgInfo(){}
	
	
		public int getEndDraftIndex() {
			return endDraftIndex;
		}
		
		public void setEndDraftIndex(int i) {
			endDraftIndex = i;
		}
		
		public int getStartDraftIndex() {
			return startDraftIndex;
		}
		public void setStartDraftIndex(int i) {
			startDraftIndex = i;
		}

		
		public int getTotDraftdPkgs() {
			return totDraftdPkgs;
		}
		
		public void setTotDraftdPkgs(int i) {
			totDraftdPkgs = i;
		}
	
		
		public Hashtable getPkgDraftData() {
			return pkgDraftData;
		}
		public void setPkgDraftData(Hashtable hashtable) {
			pkgDraftData = hashtable;
		}

		
		public Enumeration getSortedDraftPkgIds() {
			return sortedDraftPkgIds;
		}
		public void setSortedDraftPkgIds(Enumeration enumeration) {
			sortedDraftPkgIds = enumeration;
		}

		
		public Hashtable getPkgDraftNameIdMap() {
			return pkgDraftNameIdMap;
		}

		
		public void setPkgDraftNameIdMap(Hashtable hashtable) {
			pkgDraftNameIdMap = hashtable;
		}

		
		public Hashtable getPkgDraftContents() {
			return pkgDraftContents;
		}

		
		public void setPkgDraftContents(Hashtable hashtable) {
			pkgDraftContents = hashtable;
		}

		
		public String getSelectdDraftPkg() {
			return selectdDraftPkg;
		}

		public String getEncodedSelectdDraftPkg() {
			return SearchEtc.htmlEscape(getSelectdDraftPkg());
		}
		
		public void setSelectdDraftPkg(String string) {
			selectdDraftPkg = string;
		}
		
		
		

		public Vector getDraftPackages() {
			return draftPackages;
		}

		
		public Vector getSortedDraftPackages() {
			return sortedDraftPackages;
		}

		
		public void setDraftPackages(Vector vector) {
			draftPackages = vector;
		}

		
		public void setSortedDraftPackages(Vector vector) {
			sortedDraftPackages = vector;
		}
		
		public long getPackageIdForPackageName(String pkgname) {
			long ret = 0;
			 if (sortedDraftPackages.size() > 0) {
				Enumeration enum = sortedDraftPackages.elements();
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

			
	
			public Long getDraftFileContentsLength() {
				return new Long(draftFileContents.size());
			}


		
		public Vector getDraftFileContents() {
			return draftFileContents;
		}


		
		public void setDraftFileContents(Vector vector) {
			draftFileContents = vector;
		}

		
		public Vector getPackageDraftRange() {
			return packageDraftRange;
		}

		
		public void setPackageDraftRange(Vector vector) {
			packageDraftRange = vector;
		}


		public void setPackageRangeForDisplay(int start, int end ){
		
			packageDraftRange = new Vector((Collection)sortedDraftPackages.subList(start-1,end));
			
		}

		public Vector getFileDescriptorsForPkgId(long pkgId)
		{
			DboxFileInfo finfo=null;
			Vector temp = new Vector();
			String pkgIdStr=new Long(pkgId).toString();
			if (sortedDraftPackages.size() > 0) {
				 Enumeration enum = sortedDraftPackages.elements();
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

		
		public long getDraftFileContentsLen() {
			return draftFileContentsLen;
		}

		
		public void setDraftFileContentsLen(long l) {
			draftFileContentsLen = l;
		}

		public String getDraftFileContentsLenAsStr()
		{
		 
		 return String.valueOf(getDraftFileContentsLen());
		}

		
		public String getDraftPkgToLit() {
			return draftPkgToLit;
		}

		
		public void setDraftPkgToLit(String string) {
			draftPkgToLit = string;
		}

		
		

	public void setSelectdPkgWithPkgId(String string) {
			
			selectdPkgWithPkgId=string;
		}
		
		
		public String getSelectdPkgWithPkgId() {
					return selectdPkgWithPkgId;
		}
		
	   public String getPackageNameForPackageId(String packId) {
						String ret = null;
						 if (sortedDraftPackages.size() > 0) {
							Enumeration enum = sortedDraftPackages.elements();
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
	   
	   
	   

}
