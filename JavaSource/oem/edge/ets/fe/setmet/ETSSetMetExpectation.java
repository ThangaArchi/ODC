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

import java.sql.Timestamp;

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSetMetExpectation {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.3";
	
	private static Log logger = EtsLogger.getLogger(ETSSetMetExpectation.class);
	
	private String SetMetID = "";
	private String ProjectID = "";
	private int QuestionID = 0;
	private int SeqNo = 0;
	private String ExpectDesc = "";
	private double ExpectRating = 0;
	private int ExpectID = 0;
	private String ExpectAction = "";
	private double FinalRating = 0;
	private String Comments = "";
	private String LastUserID = "";
	private Timestamp LastTimestamp = null; 

	/**
	 * @return
	 */
	public String getComments() {
		return this.Comments;
	}

	/**
	 * @return
	 */
	public String getExpectAction() {
		return this.ExpectAction;
	}

	/**
	 * @return
	 */
	public String getExpectDesc() {
		return this.ExpectDesc;
	}

	/**
	 * @return
	 */
	public int getExpectID() {
		return this.ExpectID;
	}

	/**
	 * @return
	 */
	public double getExpectRating() {
		return this.ExpectRating;
	}

	/**
	 * @return
	 */
	public double getFinalRating() {
		return this.FinalRating;
	}

	/**
	 * @return
	 */
	public Timestamp getLastTimestamp() {
		return this.LastTimestamp;
	}

	/**
	 * @return
	 */
	public String getLastUserID() {
		return this.LastUserID;
	}

	/**
	 * @return
	 */
	public String getProjectID() {
		return this.ProjectID;
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
	public int getSeqNo() {
		return this.SeqNo;
	}

	/**
	 * @return
	 */
	public String getSetMetID() {
		return this.SetMetID;
	}

	/**
	 * @param string
	 */
	public void setComments(String string) {
		this.Comments = string;
	}

	/**
	 * @param string
	 */
	public void setExpectAction(String string) {
		this.ExpectAction = string;
	}

	/**
	 * @param string
	 */
	public void setExpectDesc(String string) {
		this.ExpectDesc = string;
	}

	/**
	 * @param i
	 */
	public void setExpectID(int i) {
		this.ExpectID = i;
	}

	/**
	 * @param i
	 */
	public void setExpectRating(double i) {
		this.ExpectRating = i;
	}

	/**
	 * @param i
	 */
	public void setFinalRating(double i) {
		this.FinalRating = i;
	}

	/**
	 * @param timestamp
	 */
	public void setLastTimestamp(Timestamp timestamp) {
		this.LastTimestamp = timestamp;
	}

	/**
	 * @param string
	 */
	public void setLastUserID(String string) {
		this.LastUserID = string;
	}

	/**
	 * @param string
	 */
	public void setProjectID(String string) {
		this.ProjectID = string;
	}

	/**
	 * @param i
	 */
	public void setQuestionID(int i) {
		this.QuestionID = i;
	}

	/**
	 * @param i
	 */
	public void setSeqNo(int i) {
		this.SeqNo = i;
	}

	/**
	 * @param string
	 */
	public void setSetMetID(String string) {
		this.SetMetID = string;
	}

}
