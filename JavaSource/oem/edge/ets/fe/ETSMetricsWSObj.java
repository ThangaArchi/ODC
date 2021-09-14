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


//import com.ibm.as400.webaccess.common.*;

public class ETSMetricsWSObj extends ETSMetricsObj{

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";
	protected static SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	
	private static Log logger = EtsLogger.getLogger(ETSMetricsWSObj.class);
	
	protected int memberCount;
	protected int intMemberCount;
	protected int extMemberCount;
	protected int newDocCount;
	protected int hitCount;
	protected int intHitCount;
	protected int extHitCount;
	protected int intPostCount;
	protected int extPostCount;
	protected int ibmOnlyPostCount;
	protected double docSizeSum;
	protected String tabName;
	protected Vector outputResults;
	protected String deliveryTeam;
	protected int openIssuesCount;
	protected int issuesCount;
	protected int changesCount;
	protected int feedbacksCount;
	protected int openSev1IssuesCount;
	protected int sev1IssuesCount;
	protected int openSev2IssuesCount;
	protected int sev2IssuesCount;
	protected int openSev35IssuesCount;
	protected int sev35IssuesCount;
	
	protected String userName;
	protected String userId;
	protected String userEmail;
	protected long lastLogon;
	protected int logonCount;
	protected String roleName;

	protected String issueStatus;
	protected String issueSeverity;
	protected int issueAge;
	protected long issueCreateDate;
	protected long lastActivity;
	protected String issueProblemType;
	protected String ownerId;
	protected String ownerName;
	protected String issueTitle;
	protected String userType;
	
	protected String title;
	
	protected String issueNumber = "";
	
    public ETSMetricsWSObj(){
		super();
		deliveryTeam = "";
    }

	public void setMemberCount(int i){
		memberCount = i;	
	}
	public int getMemberCount(){
		return memberCount;	
	}
		
	public void setIntMemberCount(int i){
		intMemberCount = i;	
	}
	public int getIntMemberCount(){
		return intMemberCount;	
	}
		
	public void setExtMemberCount(int i){
		extMemberCount = i;	
	}
	public int getExtMemberCount(){
		return extMemberCount;	
	}
		
		
	public void setNewDocCount(int i){
		newDocCount = i;	
	}
	public int getNewDocCount(){
		return newDocCount;	
	}
	
	public void setHitCount(int i){
		hitCount = i;	
	}
	public int getHitCount(){
		return hitCount;	
	}
	
	public void setIntHitCount(int i){
		intHitCount = i;	
	}
	public int getIntHitCount(){
		return intHitCount;	
	}
	
	public void setExtHitCount(int i){
		extHitCount = i;	
	}
	public int getExtHitCount(){
		return extHitCount;	
	}
	
	public void setIntPostCount(int i){
		intPostCount = i;	
	}
	public int getIntPostCount(){
		return intPostCount;	
	}
		
	public void setExtPostCount(int i){
		extPostCount = i;	
	}
	public int getExtPostCount(){
		return extPostCount;	
	}
	
	public void setIBMOnlyPostCount(int i){
		ibmOnlyPostCount = i;	
	}
	public int getIBMOnlyPostCount(){
		return ibmOnlyPostCount;	
	}	
	
	public void setDocSizeSum(double i){
		docSizeSum = i/(1000.000);	
	}
	public double getDocSizeSum(){
		return docSizeSum;	
	}
	
	public void setTabName(String s){
		tabName = s;	
	}
	public String getTabName(){
		return tabName;	
	}
	
	public void setOutputResults(String[] s){
		outputResults = new Vector();
		for (int i=0;i<s.length;i++){
			outputResults.addElement(s[i]);
		}
	}
	public Vector getOutputResults(){
		return outputResults;	
	}

	public void setDeliveryTeam(String s){
		deliveryTeam = s;	
	}
	public String getDeliveryTeam(){
		return deliveryTeam;	
	}

	public void setOpenIssuesCount(int i){
		openIssuesCount = i;	
	}
	public int getOpenIssuesCount(){
		return openIssuesCount;	
	}
	
	public void setIssuesCount(int i){
		issuesCount = i;	
	}
	public int getIssuesCount(){
		return issuesCount;	
	}

	public void setChangesCount(int i){
		changesCount = i;	
	}
	public int getChangesCount(){
		return changesCount;	
	}
	public void setFeedbacksCount(int i){
		feedbacksCount = i;	
	}
	public int getFeedbacksCount(){
		return feedbacksCount;	
	}

	public void setOpenSev1IssuesCount(int i){
		openSev1IssuesCount = i;	
	}
	public int getOpenSev1IssuesCount(){
		return openSev1IssuesCount;	
	}
	public void setSev1IssuesCount(int i){
		sev1IssuesCount = i;	
	}
	public int getSev1IssuesCount(){
		return sev1IssuesCount;	
	}


	public void setOpenSev2IssuesCount(int i){
		openSev2IssuesCount = i;	
	}
	public int getOpenSev2IssuesCount(){
		return openSev2IssuesCount;	
	}
	public void setSev2IssuesCount(int i){
		sev2IssuesCount = i;	
	}
	public int getSev2IssuesCount(){
		return sev2IssuesCount;	
	}

	public void setOpenSev35IssuesCount(int i){
		openSev35IssuesCount = i;	
	}
	public int getOpenSev35IssuesCount(){
		return openSev35IssuesCount;	
	}
	public void setSev35IssuesCount(int i){
		sev35IssuesCount = i;	
	}
	public int getSev35IssuesCount(){
		return sev35IssuesCount;	
	}

	public void setUserName(String lastname,String firstname){
		userName = lastname+", "+firstname;	
	}
	public String getUserName(){
		return userName;	
	}
	
	public void setUserId(String id){
		userId = id;	
	}
	public String getUserId(){
		return userId;	
	}
	
	public void setUserEmail(String email){
		userEmail = email;	
	}
	public String getUserEmail(){
		return userEmail;	
	}

	public void setRoleName(String role){
		roleName = role;	
	}
	public String getRoleName(){
		return roleName;	
	}

	public long getLastLogon(){
		return lastLogon;
	}
	Timestamp getLastLogonTS(){
		return new Timestamp(lastLogon);
	}
	void setLastLogon(java.sql.Timestamp d){
		this.lastLogon = d.getTime();
	}
	public String getLastLogonStr(){
		return df.format(new java.util.Date(lastLogon));
	}
	
	
	public void setLogonCount(int i){
		logonCount = i;	
	}
	public int getLogonCount(){
		return logonCount;	
	}


	public void setIssueStatus(String s){
		issueStatus = s;	
	}
	public String getIssueStatus(){
		return issueStatus;	
	}


	public void setIssueSeverity(String s){
		if (s == null)
			issueSeverity = "X";
		else if (s.length()<1)
			issueSeverity = "X";
		else
			issueSeverity = s.substring(0,1);
	}
	public String getIssueSeverity(){
		return issueSeverity;	
	}
	
	public void setIssueAge(int i){
		issueAge = i;
	}
	public int getIssueAge(){
		return issueAge;	
	}
	
	public void setIssueProblemtype(String s){
		issueProblemType = s;
	}
	public String getIssueProblemType(){
		return issueProblemType;	
	}
	
	public void setOwnerId(String s){
		ownerId = s;
	}
	public String getOwnerId(){
		return ownerId;	
	}
	
	public void setOwnerName(String s){
		ownerName = s;
	}
	public String getOwnerName(){
		return ownerName;	
	}
	
	public void setIssueTitle(String s){
		issueTitle = s;
	}
	public String getIssueTitle(){
		return issueTitle;	
	}
	
	
	public void setUserType(String s){
		if (s.equals("I"))
			userType = "internal";
		else
			userType = "external";
	}
	public String getUserType(){
		return userType;	
	}



	public void setTitle(String s){
		title = s;
	}
	public String getTitle(){
		return title;	
	}


	public long getIssueCreateDate(){
		return issueCreateDate;
	}
	public Timestamp getIssueCreateDateTS(){
		return new Timestamp(issueCreateDate);
	}
	public void setIssueCreateDate(java.sql.Timestamp d){
		this.issueCreateDate = d.getTime();
	}
	public String getIssueCreateDateStr(){
		return df.format(new java.util.Date(issueCreateDate));
	}


	public long getLastActivity(){
		return lastActivity;
	}
	public Timestamp getLastActivityTS(){
		return new Timestamp(lastActivity);
	}
	public void setLastActivity(java.sql.Timestamp d){
		this.lastActivity = d.getTime();
	}
	public String getLastActivityStr(){
		return df.format(new java.util.Date(lastActivity));
	}
	
	public String getFieldByName(String name) {
		String s  = "not found";
		
		try{
			Method m = getClass().getMethod("get"+name,null);
			Object o = m.invoke(this,null);
		 	s =String.valueOf(o); 
			 
		}
		catch(Exception e){
			System.out.println("ERROR in getting field"+e);
			e.printStackTrace();
		}
		
		return s;
	}




	/**
	 * @return
	 */
	public String getIssueNumber() {
		return issueNumber;
	}

	/**
	 * @param string
	 */
	public void setIssueNumber(String string) {
		issueNumber = string;
	}

}


