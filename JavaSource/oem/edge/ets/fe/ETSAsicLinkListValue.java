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

package oem.edge.ets.fe;


public class ETSAsicLinkListValue
{
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";
 
    private String title;
    private String desc;
    private String link;
	private boolean restricted;
	private String linkid;
	private boolean ibmonly;
	
    public ETSAsicLinkListValue(String title, String desc, String link,boolean restricted,String linkid,boolean ibmonly){
		this.title = title;
		this.desc = desc;
		this.link = link;
		this.restricted = restricted;
		this.linkid = linkid;
		this.ibmonly = ibmonly;
    }
    
    public String getTitle(){
		return title;
    }
    
    public String getDescription(){
		return desc;
    }
    

	public String getLink(){
		return link;
	}
    
	public boolean isRestriced(){
		return restricted;
	}

	public String getLinkId(){
		return linkid;
	}

	public boolean isIbmOnly(){
		return ibmonly;
	}


}


