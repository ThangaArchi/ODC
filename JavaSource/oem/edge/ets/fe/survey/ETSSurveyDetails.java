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
 * Created on Sep 25, 2005
 * @author v2sathis@us.ibm.com
 */
 
package oem.edge.ets.fe.survey;

import java.util.Vector;

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 */
public class ETSSurveyDetails {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2005";
	private static final String CLASS_VERSION = "1.1";

	private static Log logger = EtsLogger.getLogger(ETSSurveyDetails.class);	

	private String SurveyYear;
	private String ResponseID;
	private String Division;
	private String Division1;
	private String Provider1;
	private String Provider2;
	private String Provider3;
	
	private Vector Data;

	/**
	 * @return
	 */
	public Vector getData() {
		return Data;
	}

	/**
	 * @return
	 */
	public String getDivision() {
		return Division;
	}

	/**
	 * @return
	 */
	public String getDivision1() {
		return Division1;
	}

	/**
	 * @return
	 */
	public String getProvider1() {
		return Provider1;
	}

	/**
	 * @return
	 */
	public String getProvider2() {
		return Provider2;
	}

	/**
	 * @return
	 */
	public String getProvider3() {
		return Provider3;
	}

	/**
	 * @return
	 */
	public String getResponseID() {
		return ResponseID;
	}

	/**
	 * @return
	 */
	public String getSurveyYear() {
		return SurveyYear;
	}

	/**
	 * @param vector
	 */
	public void setData(Vector vector) {
		Data = vector;
	}

	/**
	 * @param string
	 */
	public void setDivision(String string) {
		Division = string;
	}

	/**
	 * @param string
	 */
	public void setDivision1(String string) {
		Division1 = string;
	}

	/**
	 * @param string
	 */
	public void setProvider1(String string) {
		Provider1 = string;
	}

	/**
	 * @param string
	 */
	public void setProvider2(String string) {
		Provider2 = string;
	}

	/**
	 * @param string
	 */
	public void setProvider3(String string) {
		Provider3 = string;
	}

	/**
	 * @param string
	 */
	public void setResponseID(String string) {
		ResponseID = string;
	}

	/**
	 * @param string
	 */
	public void setSurveyYear(String string) {
		SurveyYear = string;
	}

}
