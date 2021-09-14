package oem.edge.ets.fe.acmgt.model;

import java.util.HashMap;

import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ubp.ETSUserDetails;
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
 * @author v2phani
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class WrkSpcTeamObjKey {

	public static final String VERSION = "1.8";
	
	//basic vars reqd for team action
	private String sIRUserId;
	private String reqstrIRUserId;
	private String wrkSpcId;
	private String wrkSpcOwnrId;

	//wrk spc info	
	private ETSProj wrkSpc;

		//USER ID PROFILE for which roles is added/invited/deleted/managed
	private ETSUserDetails etsUserIdDets;

	//REQUESTOR ID PROFILE, who is requesting for another 
	private ETSUserDetails reqUserIdDets;

	//OWNER OF WRKSPC details
	private ETSUserDetails wrkSpcOwnrDets;

	/**
		 * Constructor for EtsIssFilterObjKeyPrep.
		 */
	public WrkSpcTeamObjKey() {
		super();

	}

	/**
	 * @return
	 */
	public ETSUserDetails getEtsUserIdDets() {
		return etsUserIdDets;
	}

	
	/**
	 * @return
	 */
	public ETSUserDetails getReqUserIdDets() {
		return reqUserIdDets;
	}

	/**
	 * @return
	 */
	public ETSProj getWrkSpc() {
		return wrkSpc;
	}

	/**
	 * @return
	 */
	public ETSUserDetails getWrkSpcOwnrDets() {
		return wrkSpcOwnrDets;
	}

	/**
	 * @param details
	 */
	public void setEtsUserIdDets(ETSUserDetails details) {
		etsUserIdDets = details;
	}

	

	/**
	 * @param details
	 */
	public void setReqUserIdDets(ETSUserDetails details) {
		reqUserIdDets = details;
	}

	/**
	 * @param proj
	 */
	public void setWrkSpc(ETSProj proj) {
		wrkSpc = proj;
	}

	/**
	 * @param details
	 */
	public void setWrkSpcOwnrDets(ETSUserDetails details) {
		wrkSpcOwnrDets = details;
	}

	/**
	 * @return
	 */
	public String getReqstrIRUserId() {
		return reqstrIRUserId;
	}

	/**
	 * @return
	 */
	public String getSIRUserId() {
		return sIRUserId;
	}

	/**
	 * @return
	 */
	public String getWrkSpcId() {
		return wrkSpcId;
	}

	/**
	 * @return
	 */
	public String getWrkSpcOwnrId() {
		return wrkSpcOwnrId;
	}

	/**
	 * @param string
	 */
	public void setReqstrIRUserId(String string) {
		reqstrIRUserId = string;
	}

	/**
	 * @param string
	 */
	public void setSIRUserId(String string) {
		sIRUserId = string;
	}

	/**
	 * @param string
	 */
	public void setWrkSpcId(String string) {
		wrkSpcId = string;
	}

	/**
	 * @param string
	 */
	public void setWrkSpcOwnrId(String string) {
		wrkSpcOwnrId = string;
	}

}
