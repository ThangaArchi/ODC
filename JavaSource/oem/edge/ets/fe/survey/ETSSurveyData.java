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
 * Created on Sep 23, 2005
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
public class ETSSurveyData {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2005";
	private static final String CLASS_VERSION = "1.1";

	private static Log logger = EtsLogger.getLogger(ETSSurveyData.class);	
	
	private String Year;
	private String ResponseId;
	private String SurveyKey;
	private String SurveyValue;
	private Timestamp LastTimestamp;
	
	public ETSSurveyData() {
		super();
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
	public String getResponseId() {
		return ResponseId;
	}

	/**
	 * @return
	 */
	public String getSurveyKey() {
		return SurveyKey;
	}

	/**
	 * @return
	 */
	public String getSurveyValue() {
		return SurveyValue;
	}

	/**
	 * @return
	 */
	public String getYear() {
		return Year;
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
	public void setResponseId(String string) {
		ResponseId = string;
	}

	/**
	 * @param string
	 */
	public void setSurveyKey(String string) {
		SurveyKey = string;
	}

	/**
	 * @param string
	 */
	public void setSurveyValue(String string) {
		SurveyValue = string;
	}

	/**
	 * @param string
	 */
	public void setYear(String string) {
		Year = string;
	}

}
