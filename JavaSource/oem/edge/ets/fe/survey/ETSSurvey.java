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
 * Created on Aug 26, 2005
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

public class ETSSurvey {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2005";
	private static final String CLASS_VERSION = "1.1";

	private static Log logger = EtsLogger.getLogger(ETSSurvey.class);	
	
	   	private String Year;
		private String ResponseID;
		private String SurveyDate;
		private String FirstName;
		private String LastName;
		private String Title;
		private String Company;
		private String Country;
		private String OverallSatisfaction;
	   	private String Status;
	   	public Vector SurveyData;
   
	   	public ETSSurvey() {
			Year = "";
			ResponseID = "";
			FirstName = "";
			LastName = "";
			Title = "";
			Company = "";
			Country = "";
			Status = "";
			SurveyData = null;
	   	}
	   
	   
		/**
		 * @return
		 */
		public String getCompany() {
			if (SurveyData != null) {
				for (int i = 0; i < SurveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) SurveyData.elementAt(i);
					if (data.getSurveyKey().equalsIgnoreCase(ETSSurveyConstants.COMPANY_FIELD)) {
						return data.getSurveyValue();
					}
				}
			}
			return Company;
		}

		/**
		 * @return
		 */
		public String getCountry() {
			if (SurveyData != null) {
				for (int i = 0; i < SurveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) SurveyData.elementAt(i);
					if (data.getSurveyKey().equalsIgnoreCase(ETSSurveyConstants.COUNTRY_FIELD)) {
						return data.getSurveyValue();
					}
				}
			}

			return Country;
		}

		/**
		 * @return
		 */
		public String getFirstName() {
			if (SurveyData != null) {
				for (int i = 0; i < SurveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) SurveyData.elementAt(i);
					if (data.getSurveyKey().equalsIgnoreCase(ETSSurveyConstants.FNAME_FIELD)) {
						return data.getSurveyValue();
					}
				}
			}
			
			return FirstName;
		}

		/**
		 * @return
		 */
		public String getLastName() {
			if (SurveyData != null) {
				for (int i = 0; i < SurveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) SurveyData.elementAt(i);
					if (data.getSurveyKey().equalsIgnoreCase(ETSSurveyConstants.LNAME_FIELD)) {
						return data.getSurveyValue();
					}
				}
			}

			return LastName;
		}

		/**
		 * @return
		 */
		public String getResponseID() {
			if (SurveyData != null) {
				for (int i = 0; i < SurveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) SurveyData.elementAt(i);
					if (data.getSurveyKey().equalsIgnoreCase(ETSSurveyConstants.RESPONSEID_FIELD)) {
						return data.getSurveyValue();
					}
				}
			}
			
			return ResponseID;
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
		public Vector getSurveyData() {
			return SurveyData;
		}

		/**
		 * @return
		 */
		public String getTitle() {
			if (SurveyData != null) {
				for (int i = 0; i < SurveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) SurveyData.elementAt(i);
					if (data.getSurveyKey().equalsIgnoreCase(ETSSurveyConstants.TITLE_FIELD)) {
						return data.getSurveyValue();
					}
				}
			}
			
			return Title;
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
		 * @param string
		 */
		public void setCountry(String string) {
			Country = string;
		}

		/**
		 * @param string
		 */
		public void setFirstName(String string) {
			FirstName = string;
		}

		/**
		 * @param string
		 */
		public void setLastName(String string) {
			LastName = string;
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
		public void setStatus(String string) {
			Status = string;
		}

		/**
		 * @param vector
		 */
		public void setSurveyData(Vector vector) {
			SurveyData = vector;
		}

		/**
		 * @param string
		 */
		public void setTitle(String string) {
			Title = string;
		}

		/**
		 * @param string
		 */
		public void setYear(String string) {
			Year = string;
		}

		/**
		 * @return
		 */
		public String getOverallSatisfaction() {
			if (SurveyData != null) {
				for (int i = 0; i < SurveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) SurveyData.elementAt(i);
					if (data.getSurveyKey().equalsIgnoreCase(ETSSurveyConstants.OVERALL_SATISFACTION_FIELD)) {
						return data.getSurveyValue();
					}
				}
			}
			
			return OverallSatisfaction;
		}

		/**
		 * @param string
		 */
		public void setOverallSatisfaction(String string) {
			OverallSatisfaction = string;
		}

		/**
		 * @return
		 */
		public String getSurveyDate() {
			if (SurveyData != null) {
				for (int i = 0; i < SurveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) SurveyData.elementAt(i);
					if (data.getSurveyKey().equalsIgnoreCase(ETSSurveyConstants.INTERVIEW_DATE_FIELD)) {
						return data.getSurveyValue();
					}
				}
			}
			
			return SurveyDate;
		}

		/**
		 * @param string
		 */
		public void setSurveyDate(String string) {
			SurveyDate = string;
		}

}

