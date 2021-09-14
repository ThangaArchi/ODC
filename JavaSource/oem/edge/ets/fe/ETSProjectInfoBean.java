/*   ------------------------------------------------------------------          */
/*   IBM                                                                                     */
/*                                                                                               */  
/*   OCO Source Materials                                                          */
/*                                                                                               */
/*   Product(s): ICC/PROFIT                                                       */
/*                                                                                               */
/*   (C)Copyright IBM Corp. 2002,2003 		              */ 
/*                                                                                               */  
/*   The source code for this program is not published or otherwise */
/*   divested of its trade secrets, irrespective of what has been        */
/*   deposited with the US Copyright Office.                                  */
/*   ------------------------------------------------------------------           */

package oem.edge.ets.fe;

import java.io.Serializable;
import java.util.Vector;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 * CHANGE HISTORY >>>> 01/12/2004
 * CHANGES BY PHANI
 * 1. the constructor was made public to be accessible by other sub-packages in ETS
 * 2. a final static var was added to get dynamic CMVC extracted version 
 */
public class ETSProjectInfoBean implements Serializable {
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";

   //prefeered variable,so that CMVC will put extracted version dynamically//
   //added by phani
   public static final String VERSION = "1.7";

	Vector vInfo = new Vector();
	boolean bIsLoaded = false;	
	String sMonth = "";
	String sYear = "";

    public ETSProjectInfoBean() {
        bIsLoaded = false;
    }
	
	public boolean isLoaded() {
		return bIsLoaded;
	}
    
    public void setInfo(Vector vInInfo) {
        vInfo = vInInfo;
    }
    
    public void setLoaded(boolean bLoaded) {
        bIsLoaded = bLoaded;
    }

	public boolean isImageAvailable(String sProjectId, int iModuleId) {

		boolean bAvailable = false;

		if (vInfo != null) {
			
			for (int i = 0; i < vInfo.size(); i++) {
				
				String sDet[] = (String[]) vInfo.elementAt(i);
			
				String sProjId = sDet[0];
				String sInfoType = sDet[1];
				int iMod = Integer.parseInt(sDet[2]);
				int iImageLength = Integer.parseInt(sDet[3]);
				String sImageAltText = sDet[4];
				String sInfoDesc = sDet[5];
				String sInfoLink = sDet[6];
				
				if (sProjId.trim().equalsIgnoreCase(sProjectId) && iMod == iModuleId) {
					if (iImageLength > 0) {
						bAvailable = true;
					} else {
						bAvailable = false;
					}
					
					break;
				}
								
			}
			
		} 
		
		return bAvailable;
	}	
	
	public String getImageAltText(String sProjectId, int iModuleId) {

		String sText = "";

		if (vInfo != null) {
			
			for (int i = 0; i < vInfo.size(); i++) {
				
				String sDet[] = (String[]) vInfo.elementAt(i);
			
				String sProjId = sDet[0];
				String sInfoType = sDet[1];
				int iMod = Integer.parseInt(sDet[2]);
				int iImageLength = Integer.parseInt(sDet[3]);
				String sImageAltText = sDet[4];
				String sInfoDesc = sDet[5];
				String sInfoLink = sDet[6];
				
				if (sProjId.trim().equalsIgnoreCase(sProjectId) && iMod == iModuleId) {
					sText = sImageAltText;
					break;
				}
								
			}
			
		} 
		
		return sText;
	}	

	public String[] getInfoDescAndLink(String sProjectId, int iModuleId) {

		String sText[] = new String[]{"",""};

		if (vInfo != null) {
			
			for (int i = 0; i < vInfo.size(); i++) {
				
				String sDet[] = (String[]) vInfo.elementAt(i);
			
				String sProjId = sDet[0];
				String sInfoType = sDet[1];
				int iMod = Integer.parseInt(sDet[2]);
				int iImageLength = Integer.parseInt(sDet[3]);
				String sImageAltText = sDet[4];
				String sInfoDesc = sDet[5];
				String sInfoLink = sDet[6];
				
				if (sProjId.trim().equalsIgnoreCase(sProjectId) && iMod == iModuleId) {
					sText = new String[]{sInfoLink,sInfoDesc};
					break;
				}
								
			}
			
		} 
		
		return sText;
	}	
	
	public String getInfoDescription(String sProjectId, int iModuleId) {

		String sText = "";

		if (vInfo != null) {
			
			for (int i = 0; i < vInfo.size(); i++) {
				
				String sDet[] = (String[]) vInfo.elementAt(i);
			
				String sProjId = sDet[0];
				String sInfoType = sDet[1];
				int iMod = Integer.parseInt(sDet[2]);
				int iImageLength = Integer.parseInt(sDet[3]);
				String sImageAltText = sDet[4];
				String sInfoDesc = sDet[5];
				String sInfoLink = sDet[6];
				
				if (sProjId.trim().equalsIgnoreCase(sProjectId) && iMod == iModuleId) {
					sText = sInfoDesc;
					break;
				}
								
			}
			
		} 
		
		return sText;
	}		

	/**
	 * Returns the sMonth.
	 * @return String
	 */
	public String getSMonth() {
		return sMonth;
	}

	/**
	 * Returns the sYear.
	 * @return String
	 */
	public String getSYear() {
		return sYear;
	}

	/**
	 * Sets the sMonth.
	 * @param sMonth The sMonth to set
	 */
	public void setSMonth(String sMonth) {
		this.sMonth = sMonth;
	}

	/**
	 * Sets the sYear.
	 * @param sYear The sYear to set
	 */
	public void setSYear(String sYear) {
		this.sYear = sYear;
	}

}


