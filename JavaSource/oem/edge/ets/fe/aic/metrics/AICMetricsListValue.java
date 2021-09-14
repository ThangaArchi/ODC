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

// sandra nava   12/21/04

package oem.edge.ets.fe.aic.metrics;

import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.Defines;


public class AICMetricsListValue
{
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";

    private String title;
    private String desc;
    private String link;
	private String reportid;
	//private boolean superAdminRestriction;
	private int restriction;
	private boolean ownerOnly;

    public AICMetricsListValue(String title, String desc, String link,String reportid, int restrict){
		this.desc = desc;
		this.title = title;
		this.link = link;
		this.reportid = reportid;
		//this.superAdminRestriction = restrict;
		this.restriction = restrict;
		this.ownerOnly = false;
    }

	public AICMetricsListValue(String title, String desc, String link,String reportid, int restrict,boolean ownerOnly){
		this.desc = desc;
		this.title = title;
		this.link = link;
		this.reportid = reportid;
		//this.superAdminRestriction = restrict;
		this.restriction = restrict;
		this.ownerOnly = ownerOnly;
	}

    public String getTitle(){
		return title;
    }

    public String getDescription(){
		return desc;
    }


    public String getLink(){
		UnbrandedProperties prop = PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);
		// Added the linkid for the report page links
		return link + "&linkid=" + prop.getLinkID();
    }

	public String getReportId(){
		return reportid;
	}

	/*public boolean isAdminRestricted(){  //admin or exec ?
		return superAdminRestriction;
	}*/

	public int getRestricted(){  //0=owner,admin, or exec 1=admin or exec, 2=admin only
		return restriction;
	}

	public boolean isOwnerOnly(){
		return ownerOnly;
	}
}


