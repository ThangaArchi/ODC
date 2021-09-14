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
 * Created on Jun 14, 2004
 */
 
package oem.edge.ets.fe.setmet;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSetMetQuestion {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";
	
	private int QuestionID = 0;
	private String QuestionType = "";
	private String QuestionDesc = "";

	/**
	 * @return
	 */
	public String getQuestionDesc() {
		return this.QuestionDesc;
	}

	/**
	 * @return
	 */
	public int getQuestionID() {
		return this.QuestionID;
	}

	/**
	 * @return
	 */
	public String getQuestionType() {
		return this.QuestionType;
	}

	/**
	 * @param string
	 */
	public void setQuestionDesc(String string) {
		this.QuestionDesc = string;
	}

	/**
	 * @param i
	 */
	public void setQuestionID(int i) {
		this.QuestionID = i;
	}

	/**
	 * @param string
	 */
	public void setQuestionType(String string) {
		this.QuestionType = string;
	}

}
