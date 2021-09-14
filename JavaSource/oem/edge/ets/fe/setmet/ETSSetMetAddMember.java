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

package oem.edge.ets.fe.setmet;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Vector;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSetMetAddMember implements Serializable {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.1";
	
	private String SetMetTitle = "";
	private String SetMetMonth = "";
	private String SetMetDay = "";
	private String SetMetYear = "";
	
	private String SetMetClient = "";
	
	private String SetMetTeam[] = new String[]{""};
	
	private String SetMetPrincipal = "";
	private String PrincipalReviewDay = "";
	private String PrincipalReviewMonth = "";
	private String PrincipalReviewYear = "";
	
	private String SetMetPM = "";
	
	private String ActionPlanCreationDay = "";
	private String ActionPlanCreationMonth = "";
	private String ActionPlanCreationYear = "";

	private String ActionPlanImplementDay = "";
	private String ActionPlanImplementMonth = "";
	private String ActionPlanImplementYear = "";

	private String AfterActionDay = "";
	private String AfterActionMonth = "";
	private String AfterActionYear = "";

	/**
	 * @return
	 */
	public String getSetMetClient() {
		return this.SetMetClient;
	}

	/**
	 * @return
	 */
	public String getSetMetDay() {
		return this.SetMetDay;
	}

	/**
	 * @return
	 */
	public String getSetMetMonth() {
		return this.SetMetMonth;
	}

	/**
	 * @return
	 */
	public String getSetMetPM() {
		return this.SetMetPM;
	}

	/**
	 * @return
	 */
	public String getSetMetPrincipal() {
		return this.SetMetPrincipal;
	}

	/**
	 * @return
	 */
	public String[] getSetMetTeam() {
		return this.SetMetTeam;
	}

	/**
	 * @return
	 */
	public String getSetMetTitle() {
		return this.SetMetTitle;
	}

	/**
	 * @return
	 */
	public String getSetMetYear() {
		return this.SetMetYear;
	}

	/**
	 * @param string
	 */
	public void setSetMetClient(String string) {
		this.SetMetClient = string;
	}

	/**
	 * @param string
	 */
	public void setSetMetDay(String string) {
		this.SetMetDay = string;
	}

	/**
	 * @param string
	 */
	public void setSetMetMonth(String string) {
		this.SetMetMonth = string;
	}

	/**
	 * @param string
	 */
	public void setSetMetPM(String string) {
		this.SetMetPM = string;
	}

	/**
	 * @param string
	 */
	public void setSetMetPrincipal(String string) {
		this.SetMetPrincipal = string;
	}

	/**
	 * @param strings
	 */
	public void setSetMetTeam(String[] strings) {
		this.SetMetTeam = strings;
	}

	/**
	 * @param string
	 */
	public void setSetMetTitle(String string) {
		this.SetMetTitle = string;
	}

	/**
	 * @param string
	 */
	public void setSetMetYear(String string) {
		this.SetMetYear = string;
	}

	/**
	 * @return
	 */
	public String getActionPlanCreationDay() {
		return this.ActionPlanCreationDay;
	}

	/**
	 * @return
	 */
	public String getActionPlanCreationMonth() {
		return this.ActionPlanCreationMonth;
	}

	/**
	 * @return
	 */
	public String getActionPlanCreationYear() {
		return this.ActionPlanCreationYear;
	}

	/**
	 * @return
	 */
	public String getActionPlanImplementDay() {
		return this.ActionPlanImplementDay;
	}

	/**
	 * @return
	 */
	public String getActionPlanImplementMonth() {
		return this.ActionPlanImplementMonth;
	}

	/**
	 * @return
	 */
	public String getActionPlanImplementYear() {
		return this.ActionPlanImplementYear;
	}

	/**
	 * @return
	 */
	public String getAfterActionDay() {
		return this.AfterActionDay;
	}

	/**
	 * @return
	 */
	public String getAfterActionMonth() {
		return this.AfterActionMonth;
	}

	/**
	 * @return
	 */
	public String getAfterActionYear() {
		return this.AfterActionYear;
	}

	/**
	 * @return
	 */
	public String getPrincipalReviewDay() {
		return this.PrincipalReviewDay;
	}

	/**
	 * @return
	 */
	public String getPrincipalReviewMonth() {
		return this.PrincipalReviewMonth;
	}

	/**
	 * @return
	 */
	public String getPrincipalReviewYear() {
		return this.PrincipalReviewYear;
	}

	/**
	 * @param string
	 */
	public void setActionPlanCreationDay(String string) {
		this.ActionPlanCreationDay = string;
	}

	/**
	 * @param string
	 */
	public void setActionPlanCreationMonth(String string) {
		this.ActionPlanCreationMonth = string;
	}

	/**
	 * @param string
	 */
	public void setActionPlanCreationYear(String string) {
		this.ActionPlanCreationYear = string;
	}

	/**
	 * @param string
	 */
	public void setActionPlanImplementDay(String string) {
		this.ActionPlanImplementDay = string;
	}

	/**
	 * @param string
	 */
	public void setActionPlanImplementMonth(String string) {
		this.ActionPlanImplementMonth = string;
	}

	/**
	 * @param string
	 */
	public void setActionPlanImplementYear(String string) {
		this.ActionPlanImplementYear = string;
	}

	/**
	 * @param string
	 */
	public void setAfterActionDay(String string) {
		this.AfterActionDay = string;
	}

	/**
	 * @param string
	 */
	public void setAfterActionMonth(String string) {
		this.AfterActionMonth = string;
	}

	/**
	 * @param string
	 */
	public void setAfterActionYear(String string) {
		this.AfterActionYear = string;
	}

	/**
	 * @param string
	 */
	public void setPrincipalReviewDay(String string) {
		this.PrincipalReviewDay = string;
	}

	/**
	 * @param string
	 */
	public void setPrincipalReviewMonth(String string) {
		this.PrincipalReviewMonth = string;
	}

	/**
	 * @param string
	 */
	public void setPrincipalReviewYear(String string) {
		this.PrincipalReviewYear = string;
	}

}
