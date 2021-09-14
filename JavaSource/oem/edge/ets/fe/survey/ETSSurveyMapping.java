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
public class ETSSurveyMapping {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2005";
	private static final String CLASS_VERSION = "1.1";

	private static Log logger = EtsLogger.getLogger(ETSSurveyMapping.class);		
	
	private String Year;
	private int SeqNo;
	private String QuestionId;
	private String QuestionGroup;
	private String ResponseType;
	private String ResponseOther;
	private String MappingQuery;
	
	/**
	 * @return
	 */
	public String getMappingQuery() {
		return MappingQuery;
	}

	/**
	 * @return
	 */
	public String getQuestionGroup() {
		return QuestionGroup;
	}

	/**
	 * @return
	 */
	public String getQuestionId() {
		return QuestionId;
	}

	/**
	 * @return
	 */
	public String getResponseOther() {
		return ResponseOther;
	}

	/**
	 * @return
	 */
	public String getResponseType() {
		return ResponseType;
	}

	/**
	 * @return
	 */
	public int getSeqNo() {
		return SeqNo;
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
	public void setMappingQuery(String string) {
		MappingQuery = string;
	}

	/**
	 * @param string
	 */
	public void setQuestionGroup(String string) {
		QuestionGroup = string;
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
	public void setResponseOther(String string) {
		ResponseOther = string;
	}

	/**
	 * @param string
	 */
	public void setResponseType(String string) {
		ResponseType = string;
	}

	/**
	 * @param i
	 */
	public void setSeqNo(int i) {
		SeqNo = i;
	}

	/**
	 * @param string
	 */
	public void setYear(String string) {
		Year = string;
	}

}
