
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
 * The Bean WebDboxSentPkgInfo.java is populated with important data which is used
 * for rendering the WebDboxSentPkg.jsp
 * Webpage Information pertaining to the Sorted/Unsorted Sent Packages,Paging Indices 
 * etc. is stored in this bean
 */
public class WebDboxSentPkgInfo {

	

		
		
		
	int startSentIndex;
		int endSentIndex;
		int totSentPkgs;
		
		String selectdSentPkg;
	    private String selectdPkgWithPkgId;
		
		String sentPkgToLit;
		
		
		
		Hashtable pkgSentData=new Hashtable(); 
		Enumeration sortedSentPkgIds;
		Hashtable pkgSentNameIdMap=new Hashtable();
		Hashtable pkgSentContents=new Hashtable();
		
		Vector sentPackages= new Vector();
		Vector sortedSentPackages= new Vector();
		Vector packageSentRange = new Vector();
		
		
		Vector sentFileContents= new Vector();
		long sentFileContentsLen;
		
	    
		
		public WebDboxSentPkgInfo(int start, int end, int totalRcvdPkgs) {
		
			
			this.startSentIndex=start;
			this.endSentIndex=end;
			this.totSentPkgs=totalRcvdPkgs;
		}
		
		public WebDboxSentPkgInfo(){}
	
	
		public int getEndSentIndex() {
			return endSentIndex;
		}
		
		public void setEndSentIndex(int i) {
			endSentIndex = i;
		}
		
		public int getStartSentIndex() {
			return startSentIndex;
		}
		public void setStartSentIndex(int i) {
			startSentIndex = i;
		}

		
		public int getTotSentPkgs() {
			return totSentPkgs;
		}
		
		public void setTotSentPkgs(int i) {
			totSentPkgs = i;
		}
	
		
		public Hashtable getPkgSentData() {
			return pkgSentData;
		}
		public void setPkgSentData(Hashtable hashtable) {
			pkgSentData = hashtable;
		}

		
		public Enumeration getSortedSentPkgIds() {
			return sortedSentPkgIds;
		}
		public void setSortedSentPkgIds(Enumeration enumeration) {
			sortedSentPkgIds = enumeration;
		}

		
		public Hashtable getPkgSentNameIdMap() {
			return pkgSentNameIdMap;
		}

		
		public void setPkgSentNameIdMap(Hashtable hashtable) {
			pkgSentNameIdMap = hashtable;
		}

		
		public Hashtable getPkgSentContents() {
			return pkgSentContents;
		}

		
		public void setPkgSentContents(Hashtable hashtable) {
			pkgSentContents = hashtable;
		}

		
		public String getSelectdSentPkg() {
			return selectdSentPkg;
		}

		
		public String getEncodedSelectdSentPkg() {
			return SearchEtc.htmlEscape(getSelectdSentPkg());
		}
		
		public void setSelectdSentPkg(String string) {
			selectdSentPkg = string;
		}
		
		
		
		public Vector getSentPackages() {
			return sentPackages;
		}

		
		public Vector getSortedSentPackages() {
			return sortedSentPackages;
		}

		
		public void setSentPackages(Vector vector) {
			sentPackages = vector;
		}

		
		public void setSortedSentPackages(Vector vector) {
			sortedSentPackages = vector;
		}
		
		public long getPackageIdForPackageName(String pkgname) {
			long ret = 0;
			 if (sortedSentPackages.size() > 0) {
				Enumeration enum = sortedSentPackages.elements();
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
					 if (sortedSentPackages.size() > 0) {
						Enumeration enum = sortedSentPackages.elements();
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


	
		public Long getSentFileContentsLength() {
				return new Long(sentFileContents.size());
		}


		
		public Vector getSentFileContents() {
			return sentFileContents;
		}


		
		public void setSentFileContents(Vector vector) {
			sentFileContents = vector;
		}

		public Vector getPackageSentRange() {
			return packageSentRange;
		}

		
		public void setPackageSentRange(Vector vector) {
			packageSentRange = vector;
		}


		public void setPackageRangeForDisplay(int start, int end ){
		
			packageSentRange = new Vector((Collection)sortedSentPackages.subList(start-1,end));
			
		}

		public Vector getFileDescriptorsForPkgId(long pkgId)
		{
			DboxFileInfo finfo=null;
			Vector temp = new Vector();
			String pkgIdStr=new Long(pkgId).toString();
			if (sortedSentPackages.size() > 0) {
				 Enumeration enum = sortedSentPackages.elements();
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

		
		public long getSentFileContentsLen() {
			return sentFileContentsLen;
		}

		
		public void setSentFileContentsLen(long l) {
			sentFileContentsLen = l;
		}

		public String getSentFileContentsLenAsStr()
		{
		 
		 return String.valueOf(getSentFileContentsLen());
		}

		
		public String getSentPkgToLit() {
			return sentPkgToLit;
		}

		
		public void setSentPkgToLit(String string) {
			sentPkgToLit = string;
		}
		
	
		public void setSelectdPkgWithPkgId(String string) {
			
			selectdPkgWithPkgId=string;
		}
		
		
		public String getSelectdPkgWithPkgId() {
					return selectdPkgWithPkgId;
		}
		
		

}
