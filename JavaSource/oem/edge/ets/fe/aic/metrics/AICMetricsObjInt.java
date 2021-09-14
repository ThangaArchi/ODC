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


package oem.edge.ets.fe.aic.metrics;

import java.util.*;
import oem.edge.ets.fe.ETSObj;
//import com.ibm.as400.webaccess.common.*;

public interface AICMetricsObjInt extends ETSObj{

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	public static final String CLASS_VERSION = "1.3";

    public void setProjectId(String projectid);
    public String getProjectId();
    public void setProjectName(String name);
    public String getProjectName();
    public void setProjectCompany(String s);
	public String getProjectCompany();
	public void setProjectOwnerId(String s);
	public String getProjectOwnerId();
	public String getProjectOwnerName();
	public String getStringKey(String key);
	public int getIntKey(String key);

	public int getMemberCount();
	public int getIntMemberCount();
	public int getExtMemberCount();
	public int getNewDocCount();
	public double getDocSizeSum();
	public String getWorkspaceType();
	public String getDeliveryTeam();
	public int getHitCount();
	public int getIntHitCount();
	public int getExtHitCount();
	public int getIntPostCount();
	public int getExtPostCount();
	public int getIBMOnlyPostCount();
	public String getTabName();
	public String getRatingStr();
	public String getRatingDateStr();
	public String getClientDesignation();
	public String getExpCat();
	public int getLowRating();
	public int getSomeRating();
	public int getMetRating();
	public int getExceedRating();
	public int getOpenIssuesCount();
	public int getIssuesCount();
	public int getOpenSev1IssuesCount();
	public int getSev1IssuesCount();
	public int getOpenSev2IssuesCount();
	public int getSev2IssuesCount();
	public int getOpenSev35IssuesCount();
	public int getSev35IssuesCount();

	public String getLastLogonStr();

	public String getIssueStatus();
	public String getIssueSeverity();
	public int getIssueAge();
	public String getIssueProblemType();
	public String getOwnerId();
	public String getOwnerName();
	public String getIssueTitle();
	public String getUserType();
	public String getIssueCreateDateStr();
	public String getLastActivityStr();

	public String getTitle();


	public String getCity();
	public String getCompany();
	public String getCountry();
	public String getDayPhone();
	public String getEmail();
	public String getEvenPhone();
	public String getFax();
	public String getFname();
	public String getRevoked();
	public String getJobTitle();
	public String getLicenseDateStr();
	public String getLname();
	public String getPagmobPhone();
	public String getPostcode();
	public String getSal();
	public String getStAddr1();
	public String getStAddr2();
	public String getStprov();
	public String getSuffix();
	public String getUserid();

	public String getInds();
	public String getGeos();
	public String getAvgRatingDateStr();
	public String getBrand();
	public String getProcess();
	public String getSector();
	public String getSceSector();
}


