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
public class ETSSurveyQAData {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2005";
	private static final String CLASS_VERSION = "1.1";

	private static Log logger = EtsLogger.getLogger(ETSSurveyQAData.class);		
	
	private int QuestionSeq = 0;
	private String QuestionNo = "";
	private String QuestionText = "";
	private String AnswerType = "";
	private String Answer1 = "";
	private String Answer2 = "";
	private String Answer3 = "";
	private String Answer4 = "";
	
	/**
	 * @return
	 */
	public String getAnswer1() {
		return Answer1;
	}

	/**
	 * @return
	 */
	public String getAnswer2() {
		return Answer2;
	}

	/**
	 * @return
	 */
	public String getAnswer3() {
		return Answer3;
	}

	/**
	 * @return
	 */
	public String getAnswer4() {
		return Answer4;
	}

	/**
	 * @return
	 */
	public String getAnswerType() {
		return AnswerType;
	}

	/**
	 * @return
	 */
	public String getQuestionNo() {
		return QuestionNo;
	}

	/**
	 * @return
	 */
	public int getQuestionSeq() {
		return QuestionSeq;
	}

	/**
	 * @return
	 */
	public String getQuestionText() {
		return QuestionText;
	}

	/**
	 * @param string
	 */
	public void setAnswer1(String string) {
		Answer1 = string;
	}

	/**
	 * @param string
	 */
	public void setAnswer2(String string) {
		Answer2 = string;
	}

	/**
	 * @param string
	 */
	public void setAnswer3(String string) {
		Answer3 = string;
	}

	/**
	 * @param string
	 */
	public void setAnswer4(String string) {
		Answer4 = string;
	}

	/**
	 * @param string
	 */
	public void setAnswerType(String string) {
		AnswerType = string;
	}

	/**
	 * @param string
	 */
	public void setQuestionNo(String string) {
		QuestionNo = string;
	}

	/**
	 * @param i
	 */
	public void setQuestionSeq(int i) {
		QuestionSeq = i;
	}

	/**
	 * @param string
	 */
	public void setQuestionText(String string) {
		QuestionText = string;
	}

}
