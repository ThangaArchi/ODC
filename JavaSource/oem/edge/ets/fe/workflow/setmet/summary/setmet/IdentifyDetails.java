/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.setmet.summary.setmet;

import java.util.ArrayList;

import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.setmet.SetMetDAO;
import oem.edge.ets.fe.workflow.setmet.SetMetIdentifyStageObject;

import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC
//
/**
 * Class       : IdentifyDetails
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.setmet
 * Description : 
 * Date		   : Nov 20, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class IdentifyDetails{
	private static Log logger = WorkflowLogger.getLogger(IdentifyDetails.class);
	private String quarterNum = null;
	private String yearYY = null;
	private String IBMAttendeesCommaSep = null;
	private ArrayList IBMAttendeesID  = new ArrayList();
	private String clientRepFP = null;
	private String clientRepFPID = null;
	private String nextMeetingDate = null;
	private String setmetDate = null;
	private String execSponsor = null;
	private String execSponsorID = null;
	private String clientName = null;
	private String clientAttendeesCommaSep = null;
	/**
	 * @param db
	 */
	public IdentifyDetails(DBAccess db, String projectID, String workflowID) {
		SetMetDAO identifyStgDAO = new SetMetDAO();
		SetMetIdentifyStageObject obj = (SetMetIdentifyStageObject)identifyStgDAO.getWorkflowObject(workflowID);
		yearYY = obj.getYear().substring(2);
		quarterNum = obj.getQuarter();
		clientName = obj.getClientName();
		String[] temp = obj.getIbmlist();
		for(int i=0; i<temp.length; i++)
		{
			IBMAttendeesID.add(temp[i]);
			if(i==0)
				IBMAttendeesCommaSep =getNameFromWebID(temp[i],db);
			else
				IBMAttendeesCommaSep +=", "+getNameFromWebID(temp[i],db);
		}
		String[] temp1 = identifyStgDAO.clientAttendees_list(workflowID, db);
		if(temp1!=null && temp1.length>=0)
		{
			clientAttendeesCommaSep=  identifyStgDAO.getClientnameOnclientID(temp1,db);
			clientRepFP = identifyStgDAO.getClientnameOnclientID(new String[]{temp1[0]},db);
		}
		execSponsorID = obj.getSponsor();
		execSponsor = getNameFromWebID(execSponsorID,db);
		
		setmetDate = obj.getPlMon()+"/"+obj.getPlDate()+"/"+obj.getPlYear();
		/*for(int i=0;i<temp1.length;i++)
		{
			if(i==0)
				clientAttendeesCommaSep =temp1[i];
			else
				clientAttendeesCommaSep +=", "+temp1[i];
		}*/
	}
	private String getNameFromWebID(String webID, DBAccess db)
	{
		ETSUserDetails u = new ETSUserDetails();
		u.setWebId(webID);
		u.extractUserDetails(db.getConnection());
		return u.getFirstName() + " "+ u.getLastName();
	}
	/**
	 * @return Returns the clientRepFP.
	 */
	public String getClientRepFP() {
		return clientRepFP;
	}
	/**
	 * @param clientRepFP The clientRepFP to set.
	 */
	public void setClientRepFP(String clientRepFP) {
		this.clientRepFP = clientRepFP;
	}
	/**
	 * @return Returns the clientRepFPID.
	 */
	public String getClientRepFPID() {
		return clientRepFPID;
	}
	/**
	 * @param clientRepFPID The clientRepFPID to set.
	 */
	public void setClientRepFPID(String clientRepFPID) {
		this.clientRepFPID = clientRepFPID;
	}
	/**
	 * @return Returns the iBMAttendeesCommaSep.
	 */
	public String getIBMAttendeesCommaSep() {
		return IBMAttendeesCommaSep;
	}
	/**
	 * @param attendeesCommaSep The iBMAttendeesCommaSep to set.
	 */
	public void setIBMAttendeesCommaSep(String attendeesCommaSep) {
		IBMAttendeesCommaSep = attendeesCommaSep;
	}
	/**
	 * @return Returns the iBMAttendeesID.
	 */
	public ArrayList getIBMAttendeesID() {
		return IBMAttendeesID;
	}
	/**
	 * @param attendeesID The iBMAttendeesID to set.
	 */
	public void setIBMAttendeesID(ArrayList attendeesID) {
		IBMAttendeesID = attendeesID;
	}
	/**
	 * @return Returns the nextMeetingDate.
	 */
	public String getNextMeetingDate() {
		return nextMeetingDate;
	}
	/**
	 * @param nextMeetingDate The nextMeetingDate to set.
	 */
	public void setNextMeetingDate(String nextMeetingDate) {
		this.nextMeetingDate = nextMeetingDate;
	}
	/**
	 * @return Returns the quarterNum.
	 */
	public String getQuarterNum() {
		return quarterNum;
	}
	/**
	 * @param quarterNum The quarterNum to set.
	 */
	public void setQuarterNum(String quarterNum) {
		this.quarterNum = quarterNum;
	}
	/**
	 * @return Returns the setmetDate.
	 */
	public String getSetmetDate() {
		return setmetDate;
	}
	/**
	 * @param setmetDate The setmetDate to set.
	 */
	public void setSetmetDate(String setmetDate) {
		this.setmetDate = setmetDate;
	}
	/**
	 * @return Returns the yearYY.
	 */
	public String getYearYY() {
		return yearYY;
	}
	/**
	 * @param yearYY The yearYY to set.
	 */
	public void setYearYY(String yearYY) {
		this.yearYY = yearYY;
	}
	/**
	 * @return Returns the execSponsor.
	 */
	public String getExecSponsor() {
		return execSponsor;
	}
	/**
	 * @param execSponsor The execSponsor to set.
	 */
	public void setExecSponsor(String execSponsor) {
		this.execSponsor = execSponsor;
	}
	/**
	 * @return Returns the execSponsorID.
	 */
	public String getExecSponsorID() {
		return execSponsorID;
	}
	/**
	 * @param execSponsorID The execSponsorID to set.
	 */
	public void setExecSponsorID(String execSponsorID) {
		this.execSponsorID = execSponsorID;
	}
	/**
	 * @return Returns the clientName.
	 */
	public String getClientName() {
		return clientName;
	}
	/**
	 * @param clientName The clientName to set.
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	/**
	 * @return Returns the clientAttendeesCommaSep.
	 */
	public String getClientAttendeesCommaSep() {
		return clientAttendeesCommaSep;
	}
	/**
	 * @param clientAttendeesCommaSep The clientAttendeesCommaSep to set.
	 */
	public void setClientAttendeesCommaSep(String clientAttendeesCommaSep) {
		this.clientAttendeesCommaSep = clientAttendeesCommaSep;
	}
}

