/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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
 * Created on Sep 27, 2005
 * @author v2sathis@us.ibm.com
 */
 
package oem.edge.ets.fe.survey;

import java.sql.Timestamp;

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 */
public class ETSSurveyActionPlan {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2005";
	private static final String CLASS_VERSION = "1.1";

	private static Log logger = EtsLogger.getLogger(ETSSurveyActionPlan.class);			
	
	private String Year;
	private String Company;
	private String SurveyId;
	private String Status;
	private String PlanOwnerId;
	private Timestamp PlanDueDate;
	private String LastUserId;
	private Timestamp LastTimestamp;
	
	
	
	/**
	 * @return
	 */
	public String getCompany() {
		return Company;
	}

	/**
	 * @return
	 */
	public Timestamp getLastTimestamp() {
		return LastTimestamp;
	}

	/**
	 * @return
	 */
	public String getLastUserId() {
		return LastUserId;
	}

	/**
	 * @return
	 */
	public Timestamp getPlanDueDate() {
		return PlanDueDate;
	}

	/**
	 * @return
	 */
	public String getPlanOwnerId() {
		return PlanOwnerId;
	}

	/**
	 * @return
	 */
	public String getStatus() {
		return Status;
	}

	/**
	 * @return
	 */
	public String getSurveyId() {
		return SurveyId;
	}

	/**
	 * @return
	 */
	public String getYear() {
		return Year;
	}

	/**
	 * @param string
	 */
	public void setCompany(String string) {
		Company = string;
	}

	/**
	 * @param timestamp
	 */
	public void setLastTimestamp(Timestamp timestamp) {
		LastTimestamp = timestamp;
	}

	/**
	 * @param string
	 */
	public void setLastUserId(String string) {
		LastUserId = string;
	}

	/**
	 * @param timestamp
	 */
	public void setPlanDueDate(Timestamp timestamp) {
		PlanDueDate = timestamp;
	}

	/**
	 * @param string
	 */
	public void setPlanOwnerId(String string) {
		PlanOwnerId = string;
	}

	/**
	 * @param string
	 */
	public void setStatus(String string) {
		Status = string;
	}

	/**
	 * @param string
	 */
	public void setSurveyId(String string) {
		SurveyId = string;
	}

	/**
	 * @param string
	 */
	public void setYear(String string) {
		Year = string;
	}

}
