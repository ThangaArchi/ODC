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


/*
 * Created on Sep 6, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.core.WorkflowObject;

/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetMetValueObject extends WorkflowObject{
	
	
	private ArrayList clientAttendee = null;
	private ArrayList IbmAttendee = null;
	private ArrayList Delegate_Backup = null;
	private ArrayList AnnualNSIRating = null;
	private ArrayList AccountContact = null;
	private ArrayList ExecutiveSponser = null;
	private ArrayList MeetingLocation = null;
	private ArrayList BiweeklyStatus = null;
	private ArrayList NotificatioList = null;
/*	
	private String IssueTitle = null;
	private String IssueOwner = null;
	private String IssueStatus = null;
	private String DateOpened = null;
	private String WorkflowStatus = null;
	*/
	

	/**
	 * @return Returns the accountContact.
	 */
	public ArrayList getAccountContact() {
		return AccountContact;
	}
	/**
	 * @param accountContact The accountContact to set.
	 */
	public void setAccountContact(ArrayList accountContact) {
		AccountContact = accountContact;
	}
	/**
	 * @return Returns the annualNSIRating.
	 */
	public ArrayList getAnnualNSIRating() {
		return AnnualNSIRating;
	}
	/**
	 * @param annualNSIRating The annualNSIRating to set.
	 */
	public void setAnnualNSIRating(ArrayList annualNSIRating) {
		AnnualNSIRating = annualNSIRating;
	}
	/**
	 * @return Returns the biweeklyStatus.
	 */
	public ArrayList getBiweeklyStatus() {
		return BiweeklyStatus;
	}
	/**
	 * @param biweeklyStatus The biweeklyStatus to set.
	 */
	public void setBiweeklyStatus(ArrayList biweeklyStatus) {
		BiweeklyStatus = biweeklyStatus;
	}
	/**
	 * @return Returns the clientAttendee.
	 */
	public ArrayList getClientAttendee() {
		return clientAttendee;
	}
	/**
	 * @param clientAttendee The clientAttendee to set.
	 */
	public void setClientAttendee(ArrayList clientAttendee) {
		this.clientAttendee = clientAttendee;
	}
	/**
	 * @return Returns the delegate_Backup.
	 */
	public ArrayList getDelegate_Backup() {
		return Delegate_Backup;
	}
	/**
	 * @param delegate_Backup The delegate_Backup to set.
	 */
	public void setDelegate_Backup(ArrayList delegate_Backup) {
		Delegate_Backup = delegate_Backup;
	}
	/**
	 * @return Returns the executiveSponser.
	 */
	public ArrayList getExecutiveSponser() {
		return ExecutiveSponser;
	}
	/**
	 * @param executiveSponser The executiveSponser to set.
	 */
	public void setExecutiveSponser(ArrayList executiveSponser) {
		ExecutiveSponser = executiveSponser;
	}
	/**
	 * @return Returns the ibmAttendee.
	 */
	public ArrayList getIbmAttendee() {
		return IbmAttendee;
	}
	/**
	 * @param ibmAttendee The ibmAttendee to set.
	 */
	public void setIbmAttendee(ArrayList ibmAttendee) {
		IbmAttendee = ibmAttendee;
	}
	/**
	 * @return Returns the meetingLocation.
	 */
	public ArrayList getMeetingLocation() {
		return MeetingLocation;
	}
	/**
	 * @param meetingLocation The meetingLocation to set.
	 */
	public void setMeetingLocation(ArrayList meetingLocation) {
		MeetingLocation = meetingLocation;
	}
	/**
	 * @return Returns the notificatioList.
	 */
	public ArrayList getNotificatioList() {
		return NotificatioList;
	}
	/**
	 * @param notificatioList The notificatioList to set.
	 */
	public void setNotificatioList(ArrayList notificatioList) {
		NotificatioList = notificatioList;
	}
	
		
}
