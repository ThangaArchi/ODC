/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;


public class ETSMetricsObj implements ETSMetricsObjInt {

	public final static String Copyright =
		"(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.6";
	protected static SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

	private static Log logger = EtsLogger.getLogger(ETSMetricsObj.class);
	
	protected String projectId;
	protected String projectName;
	protected String projectCompany;
	protected String projectOwnerId;
	protected String projectOwnerName;
	protected String wsType;
	protected String inds;
	protected String geos;
	protected long createDate;

	public ETSMetricsObj() {
		projectId = "";
		projectName = "";
		projectCompany = "";
		projectOwnerId = "";
		projectOwnerName = "";
		wsType = "";
		inds = "";
		geos = "";
		createDate = 0;
	}

	public void setProjectId(String projectid) {
		this.projectId = projectid;
	}
	public String getProjectId() {
		return projectId;
	}

	public void setProjectName(String name) {
		this.projectName = name;
	}
	public String getProjectName() {
		return projectName;
	}

	public void setProjectCompany(String s) {
		this.projectCompany = s;
	}
	public String getProjectCompany() {
		return projectCompany;
	}

	public void setProjectOwnerId(String s) {
		this.projectOwnerId = s;
	}
	public String getProjectOwnerId() {
		return projectOwnerId;
	}
	
	public void setProjectOwnerName(String s) {
		this.projectOwnerName = s;
	}
	public String getProjectOwnerName() {
		return projectOwnerName;
	}
	
	
	
	public String getStringKey(String key) {
		if (key.equals(Defines.SORT_BY_NAME_STR))
			return projectName;
		else
			return "";

	}
	public int getIntKey(String key) {
		return 0;
	}

	public Vector getOutputResults() {
		return new Vector();
	}

	public int getMemberCount() {
		return 0;
	}
	public int getIntMemberCount() {
		return 0;
	}
	public int getExtMemberCount() {
		return 0;
	}
	public int getNewDocCount() {
		return 0;
	}
	public double getDocSizeSum() {
		return 0;
	}
	public int getHitCount() {
		return 0;
	}
	public int getIntHitCount() {
		return 0;
	}
	public int getExtHitCount() {
		return 0;
	}
	public String getTabName() {
		return "";
	}
	public int getIntPostCount() {
		return 0;
	}
	public int getExtPostCount() {
		return 0;
	}
	public int getIBMOnlyPostCount() {
		return 0;
	}

	public int getLowRating() {
		return 0;
	}
	public int getSomeRating() {
		return 0;
	}
	public int getMetRating() {
		return 0;
	}
	public int getExceedRating() {
		return 0;
	}
	public String getRatingStr() {
		return "-";
	}
	public String getRatingDateStr() {
		return "";
	};
	public String getAvgRatingDateStr() {
		return "";
	};
	public String getClientDesignation() {
		return "";
	};
	public int getOpenIssuesCount() {
		return 0;
	}
	public int getIssuesCount() {
		return 0;
	}
	public int getOpenSev1IssuesCount() {
		return 0;
	}
	public int getSev1IssuesCount() {
		return 0;
	}
	public int getOpenSev2IssuesCount() {
		return 0;
	}
	public int getSev2IssuesCount() {
		return 0;
	}
	public int getOpenSev35IssuesCount() {
		return 0;
	}
	public int getSev35IssuesCount() {
		return 0;
	}

	public String getExpCat() {
		return "";
	}

	public String getLastLogonStr(){ return ""; }

	public String getIssueStatus(){ return ""; }
	public String getIssueSeverity(){ return ""; }
	public int getIssueAge(){ return 0; }
	public String getIssueProblemType(){ return ""; }
	public String getOwnerId(){ return ""; }
	public String getOwnerName(){ return ""; }
	public String getIssueTitle(){ return ""; }
	public String getUserType(){ return ""; }
	public String getIssueCreateDateStr(){ return ""; }
	public String getLastActivityStr(){ return ""; }




	public String getTitle(){
		return "";	
	}


	public String getDeliveryTeam() {
		return "Not Defined";
	}

	public void setWorkspaceType(String s) {
		if (s.equals("P")) {
			wsType = "Project";
		}
		else if (s.equals("O")) {
			wsType = "Proposal";
		}
		else if (s.equals("C")) {
			wsType = "Client voice";
		}
		else if (s.equals("M")) {
			wsType = "Metrics";
		}
		else{
			wsType = s;
		}

	}
	public String getWorkspaceType() {
		return wsType;
	}

	public void setInds(String i){
		if (i != null)
			inds = i;	
	}
	public String getInds(){
		return inds;	
	}
	public void setGeos(String i){
		if (i != null)
			geos = i;	
	}
	public String getGeos(){
		return geos;	
	}

	public long getCreateDate(){
		return createDate;
	}
	Timestamp getCreateDateTS(){
		return new Timestamp(createDate);
	}
	void setCreateDate(java.sql.Timestamp d){
		this.createDate = d.getTime();
	}
	public String getCreateDateStr(){
		return df.format(new java.util.Date(createDate));
	}


//start of Blade
  public String getCity(){ return "";}
  public String getCompany(){ return "";}
  public String getCountry(){ return "";}
  public String getDayPhone(){ return "";}
  public String getEmail(){ return "";}
  public String getEvenPhone(){ return "";}
  public String getFax(){ return "";}
  public String getFname(){ return "";}
  public String getRevoked(){ return "";}
  public String getJobTitle(){ return "";}
  public String getLicenseDateStr(){ return "";}
  public String getLname(){ return "";}
  public String getPagmobPhone(){ return "";}
  public String getPostcode(){ return "";}
  public String getSal(){ return "";}
  public String getStAddr1(){ return "";}
  public String getStAddr2(){ return "";}
  public String getStprov(){ return "";}
  public String getSuffix(){ return "";}
  public String getUserid(){ return "";}

//end of Blade

 
	public String getFieldByName(String name) {
		String s  = name;
		try{
			Method m = getClass().getMethod("get"+name,null);
			Object o = m.invoke(this,null);
			s =String.valueOf(o); 
			 
		}
		catch(Exception e){
			System.out.println("ERROR in getting field (main): "+name+e);
			e.printStackTrace();
		}
		return s;
	}

}
