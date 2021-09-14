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

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 */
public class ETSSurveyQuestion {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2005";
	private static final String CLASS_VERSION = "1.1";

	private static Log logger = EtsLogger.getLogger(ETSSurveyQuestion.class);	
	
	
	private String Year;
	private String QuestionId;
	private String QuestionText;
	
	/**
	 * @return
	 */
	public String getQuestionId() {
		return QuestionId;
	}

	/**
	 * @return
	 */
	public String getQuestionText() {
		return QuestionText;
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
	public void setQuestionId(String string) {
		QuestionId = string;
	}

	/**
	 * @param string
	 */
	public void setQuestionText(String string) {
		QuestionText = string;
	}

	/**
	 * @param string
	 */
	public void setYear(String string) {
		Year = string;
	}

}
