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



/*
 * Created on Feb 21, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.wspace;

/**
 * @author vishal
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import oem.edge.ets.fe.aic.AICProj;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcMailHandler;
import oem.edge.ets.fe.event.ETSWorkspaceEvent;
import oem.edge.webservice.eventservice.EventDispatcher;

public class ETSSendWorkspaceEvent {

	public boolean sendWrkspaceEvent(Connection conn, String projectId, String eventType) {
		boolean status = false;
		
		
		WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();
		
		try {
			AICProj proj = ETSUtils.getAicEtsProjectDetails(conn,projectId);
			
			ETSWorkspaceEvent projEvent = new ETSWorkspaceEvent();
			
			projEvent.setIdentity(eventType);
			projEvent.setProjectid(proj.getProjectId());
			projEvent.setProjectname(proj.getName());
			projEvent.setDeliveryteam(proj.getDeliveryTeam());
			projEvent.setBrand(proj.getBrand());
			projEvent.setCompany(proj.getCompany());
			projEvent.setGeography(proj.getGeography());
			projEvent.setEnddate(new GregorianCalendar());
			if (proj.isITAR()) {
				projEvent.setIsitar("Yes");
			} else {
				projEvent.setIsitar("No");
			}
			
			if (proj.isIbmOnly()) {
				projEvent.setIbmonly("Yes");
			} else {
				projEvent.setIbmonly("No");
			}
			
			projEvent.setIndustry(proj.getIndustry());
			
			if (proj.getIsPrivate().equalsIgnoreCase("A")) {
				projEvent.setIsprivate("Public Workspace");
			} else if (proj.getIsPrivate().equalsIgnoreCase("R")) {
				projEvent.setIsprivate("Restricted Workspace");
			} else if (proj.getIsPrivate().equalsIgnoreCase("P")) {
				projEvent.setIsprivate("Private Workspace");
			} else if (proj.getIsPrivate().equalsIgnoreCase("1")) {
				projEvent.setIsprivate("Restricted Teamroom Workspace");
			} else 			if (proj.getIsPrivate().equalsIgnoreCase("2")) {
				projEvent.setIsprivate("Private Teamroom Workspace");
			} else {
				projEvent.setIsprivate(proj.getIsPrivate());
			} 
 
			projEvent.setProcess(proj.getProcess());
			projEvent.setProjectdescription(proj.getDescription());
			
			if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
				projEvent.setProjectorproposal("Project");
			} else if (proj.getProjectOrProposal().equalsIgnoreCase("O")) {
				projEvent.setProjectorproposal("Proposal");
			} else if (proj.getProjectOrProposal().equalsIgnoreCase("C")) {
				projEvent.setProjectorproposal("Client Voice");
			} else {
				projEvent.setProjectorproposal(proj.getProjectOrProposal());
			}

			if (proj.getProject_status().equalsIgnoreCase("N")) {
				projEvent.setProjectstatus("Active Workspace");
			} else if (proj.getProject_status().equalsIgnoreCase("D")) {
				projEvent.setProjectstatus("Deleted Workspace");
			} else if (proj.getProject_status().equalsIgnoreCase("A")) {
				projEvent.setProjectstatus("Archived Workspace");				
			}
			
			
			projEvent.setProjecttype(proj.getProjectType());
			projEvent.setScesector(proj.getSce_sector());
			projEvent.setSector(proj.getSector());
			projEvent.setStartdate(new GregorianCalendar());
			projEvent.setSubsector(proj.getSub_sector());
			
			
			EventDispatcher.getInstance().dispatchEvent(projEvent);
			System.out.println("SUCCESS:::Sending the event");
			
			status =  true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FAILED TO SEND MQEVENT for project::"  + projectId);
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN SEND MQ EVENT", "Exception in sending MQ event for wrkspc" + projectId);
			
		}
		
		return status;
	}
	
	public boolean sendWrkspaceEvent(String projectId, String eventType) {
		Connection conn = null;
		boolean status = false;
		
		WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();
		
		try {
			conn = ETSDBUtils.getConnection();
			status = sendWrkspaceEvent(conn,projectId,eventType);
			
			ETSDBUtils.close(conn);
			
		} catch (SQLException e) {
			e.printStackTrace();
			mailHandler.sendMailOnErrorToSupp("FATAL SQL Exception IN SEND MQ EVENT", " SQL Exception in sending MQ event for wrkspc" + projectId);
		} catch (Exception e) {
			e.printStackTrace();
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN SEND MQ EVENT", "Exception in sending MQ event for wrkspc" + projectId);
		}
		return status;
	}
}
