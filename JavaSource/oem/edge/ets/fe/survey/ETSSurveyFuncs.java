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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author v2sathis
 *
 */
public class ETSSurveyFuncs {


	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";

	/**
	 * @param con
	 * @param sYear
	 * @param sResponseId
	 * @return
	 */
	public static ETSSurveyDetails getSurveyDetails(Connection con, String sYear, String sResponseId) throws SQLException, Exception {
		
		// format the survey here and return the object
		
		/*
		 * 1. Get the questions...
		 * 2. Get the survey data
		 * 2. Get the data from mapping table..
		 *    Get the reference data
		 * 3. Format the questions
		 * 4. Format/figure out answers if necessary.
		 * 5. add them to the object.
		 * 
		 */		
		
		ETSSurveyDetails survey = new ETSSurveyDetails();
		
//		boolean bOther1Used = false;
//		boolean bOther2Used = false;
//		boolean bOther3Used = false;
//		
		try {
			
			ETSSurvey surveyData = ETSSurveyDAO.getSurveyData(con,sYear,sResponseId);		
			Vector questions = ETSSurveyDAO.getSurveyQuestions(con,sYear);
			Vector mapping = ETSSurveyDAO.getSurveyMappingData(con,sYear);
			Vector ref = ETSSurveyDAO.getSurveyReferenceData(con,sYear);
			
			
			String sSampleValue = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.SAMPLE_TYPE_FIELD,surveyData.getSurveyData());
			
			String sSample1Value = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.SAMPLE_TYPE1_FIELD,surveyData.getSurveyData());
			
			// get the division...
			String sDivision = getReferenceData(sYear,ETSSurveyConstants.SAMPLE_DIV,sSampleValue,ref);
			String sDivision1 = getReferenceData(sYear,ETSSurveyConstants.SAMPLE_DIV1,sSample1Value,ref);
			survey.setDivision(sDivision);
			survey.setDivision1(sDivision1);
			survey.setSurveyYear(sYear);
			survey.setResponseID(sResponseId);
			
			// get providers...
			String sProvider1_Field = getReferenceData(sYear,ETSSurveyConstants.REFERENCE_PROV_FIELD,"PROVIDER1",ref);
			// get data from survey data for this field...
			String sProvider1Value = getSurveyFieldData(sYear,sResponseId,sProvider1_Field,surveyData.getSurveyData());
			String sProvider1Desc  = "";
			if (sProvider1Value.trim().equalsIgnoreCase("") || sProvider1Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9995)) {
				// none or no provider selected.
				sProvider1Desc = "";
			} else{
				if (sProvider1Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9991)) {
					sProvider1Desc = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.REFERENCE_OTHER1,surveyData.getSurveyData());
				} else if (sProvider1Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9992)) {
					sProvider1Desc = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.REFERENCE_OTHER2,surveyData.getSurveyData());
				} else if (sProvider1Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9993)) {
					sProvider1Desc = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.REFERENCE_OTHER3,surveyData.getSurveyData());
				} else if (sProvider1Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9994)) {
					sProvider1Desc = ETSSurveyConstants.PROVIDER_INTERNAL_VALUE;
				} else {
					// since this is provider.. have to again find out the value from reference table..
					sProvider1Desc = getReferenceData(sYear,ETSSurveyConstants.REFERENCE_PROVIDER,sProvider1Value,ref);
				}
			}
			
			// get providers...
			String sProvider2_Field = getReferenceData(sYear,ETSSurveyConstants.REFERENCE_PROV_FIELD,"PROVIDER2",ref);
			// get data from survey data for this field...
			String sProvider2Value = getSurveyFieldData(sYear,sResponseId,sProvider2_Field,surveyData.getSurveyData());
			String sProvider2Desc  = "";
			if (sProvider2Value.trim().equalsIgnoreCase("") || sProvider2Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9995)) {
				// none or no provider selected.
				sProvider2Desc = "";
			} else{
				if (sProvider2Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9991)) {
					sProvider2Desc = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.REFERENCE_OTHER1,surveyData.getSurveyData());
				} else if (sProvider2Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9992)) {
					sProvider2Desc = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.REFERENCE_OTHER2,surveyData.getSurveyData());
				} else if (sProvider2Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9993)) {
					sProvider2Desc = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.REFERENCE_OTHER3,surveyData.getSurveyData());
				} else if (sProvider2Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9994)) {
					sProvider2Desc = ETSSurveyConstants.PROVIDER_INTERNAL_VALUE;
				} else {
					// since this is provider.. have to again find out the value from reference table..
					sProvider2Desc = getReferenceData(sYear,ETSSurveyConstants.REFERENCE_PROVIDER,sProvider2Value,ref);
				}
			}
			
			// get providers...
			String sProvider3_Field = getReferenceData(sYear,ETSSurveyConstants.REFERENCE_PROV_FIELD,"PROVIDER3",ref);
			
			// get data from survey data for this field...
			String sProvider3Value = getSurveyFieldData(sYear,sResponseId,sProvider3_Field,surveyData.getSurveyData());
			
			String sProvider3Desc  = "";
			if (sProvider3Value.trim().equalsIgnoreCase("") || sProvider3Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9995)) {
				// none or no provider selected.
				sProvider3Desc = "";
			} else{
				if (sProvider3Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9991)) {
					sProvider3Desc = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.REFERENCE_OTHER1,surveyData.getSurveyData());
				} else if (sProvider3Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9992)) {
					sProvider3Desc = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.REFERENCE_OTHER2,surveyData.getSurveyData());
				} else if (sProvider3Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9993)) {
					sProvider3Desc = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.REFERENCE_OTHER3,surveyData.getSurveyData());
				} else if (sProvider3Value.trim().equalsIgnoreCase(ETSSurveyConstants.PROVIDER_9994)) {
					sProvider3Desc = ETSSurveyConstants.PROVIDER_INTERNAL_VALUE;
				} else {
					// since this is provider.. have to again find out the value from reference table..
					sProvider3Desc = getReferenceData(sYear,ETSSurveyConstants.REFERENCE_PROVIDER,sProvider3Value,ref);
				}
			}
			
			survey.setProvider1(sProvider1Desc);
			survey.setProvider2(sProvider2Desc);
			survey.setProvider3(sProvider3Desc);
			
		
			Vector details = new Vector();
		
			// now format the questions and figure out the survey answers...
			// loop through mapping and get the questions by sequence...
			
			for (int i = 0; i < mapping.size(); i++) {
				
				ETSSurveyQAData data = new ETSSurveyQAData();
				ETSSurveyMapping map =(ETSSurveyMapping) mapping.elementAt(i);
				
				data.setQuestionNo(map.getQuestionId());
				data.setQuestionSeq(map.getSeqNo());
				
				String sQuestionText = getQuestionText(sYear,map.getQuestionId(),questions);
				
				
				// check to see if the question has dynamic values to be replaced...
				if (sQuestionText.indexOf(ETSSurveyConstants.PARAMETER_DIVIDER) >=0) {
					// this question contains dynamic paramters.. have to replace them with 
					// the actual values before displaying...
					sQuestionText = formatQuestion(sYear, sResponseId,sQuestionText,ref,surveyData.getSurveyData(),mapping, survey);
				} 
				
				data.setQuestionText(sQuestionText);
								
				if (map.getResponseType().equalsIgnoreCase(ETSSurveyConstants.RESPONSE_RATING)) {
					
					data.setAnswerType(ETSSurveyConstants.DISPLAY_RATING);
					
					String sQuestionGroup = map.getQuestionGroup();
					
					StringTokenizer st = new StringTokenizer(sQuestionGroup,",");
					int iCount = 1;
					
					while (st.hasMoreTokens()) {
						String sToken = st.nextToken();
						
						if (iCount == 1) {
							String sTemp = getSurveyFieldData(sYear,sResponseId,sToken.trim(),surveyData.getSurveyData());
							if (sTemp.equalsIgnoreCase(ETSSurveyConstants.RATING_NA)) {
								data.setAnswer1(ETSSurveyConstants.RATING_NA_REPLACE);
							} else {
								data.setAnswer1(sTemp);
							}
						}
						if (iCount == 2) {
							String sTemp = getSurveyFieldData(sYear,sResponseId,sToken.trim(),surveyData.getSurveyData());
							if (sTemp.equalsIgnoreCase(ETSSurveyConstants.RATING_NA)) {
								data.setAnswer2(ETSSurveyConstants.RATING_NA_REPLACE);
							} else {
								data.setAnswer2(sTemp);
							}
						}
						if (iCount == 3) {
							String sTemp = getSurveyFieldData(sYear,sResponseId,sToken.trim(),surveyData.getSurveyData());
							if (sTemp.equalsIgnoreCase(ETSSurveyConstants.RATING_NA)) {
								data.setAnswer3(ETSSurveyConstants.RATING_NA_REPLACE);
							} else {
								data.setAnswer3(sTemp);
							}
						}
						if (iCount == 4) {
							String sTemp = getSurveyFieldData(sYear,sResponseId,sToken.trim(),surveyData.getSurveyData());
							if (sTemp.equalsIgnoreCase(ETSSurveyConstants.RATING_NA)) {
								data.setAnswer4(ETSSurveyConstants.RATING_NA_REPLACE);
							} else {
								data.setAnswer4(sTemp);
							}
						}
						iCount = iCount + 1;
					}
					
				} else if (map.getResponseType().equalsIgnoreCase(ETSSurveyConstants.RESPONSE_TEXT)) {
					data.setAnswerType(ETSSurveyConstants.DISPLAY_TEXT);
					data.setAnswer1(getSurveyFieldData(sYear,sResponseId,map.getQuestionGroup(),surveyData.getSurveyData()));
				} else if (map.getResponseType().equalsIgnoreCase(ETSSurveyConstants.RESPONSE_MAPPING)) {
					data.setAnswerType(ETSSurveyConstants.DISPLAY_TEXT);
					
					String sQuestionGroup = map.getQuestionGroup();
					
					boolean bAnswer1 = false;
					boolean bAnswerOther = false;
					
					StringTokenizer st = new StringTokenizer(sQuestionGroup,",");
					while (st.hasMoreTokens()) {
						String sToken = st.nextToken().trim();

						String sFieldValue = getSurveyFieldData(sYear,sResponseId,sToken.trim(),surveyData.getSurveyData());
						
						if (bAnswerOther) {
							String sValue = getSurveyFieldData(sYear,sResponseId,sToken,surveyData.getSurveyData());
							data.setAnswer1(sValue);
						} else {						
							if (sFieldValue.equalsIgnoreCase(map.getResponseOther())) {
								bAnswerOther = true;						
							} else {
								bAnswer1 = true;
								// get survey field data and then execute the sql defined.
								String sFieldData = getSurveyFieldData(sYear,sResponseId,sToken.trim(),surveyData.getSurveyData());
		
								String sMappingSQL = map.getMappingQuery();
									 
								String sSQL = replaceMappingStr(sMappingSQL,sYear,map.getQuestionId(),sFieldData);
								
								String sFinalValue = ETSSurveyDAO.executeSQL(con,sSQL);
									
								data.setAnswer1(sFinalValue);
								break;
							}
						}						
					}
					
				}
				
				details.addElement(data);
			}
			
			survey.setData(details);
		
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		
		
		return survey;
	}
	


	private static String formatQuestion(String sYear, String sResponseId, String sQuestionText, Vector ref, Vector surveyData, Vector mapping, ETSSurveyDetails survey) {
		
		String sFormattedQuestion = sQuestionText;
		
		//  check to see if the question has $parameter..
		while (sFormattedQuestion.indexOf(ETSSurveyConstants.PARAMETER_DIVIDER) >= 0) {
			
			int i1Pos = sFormattedQuestion.indexOf(ETSSurveyConstants.PARAMETER_DIVIDER);
			int i2Pos = sFormattedQuestion.indexOf(ETSSurveyConstants.PARAMETER_DIVIDER,i1Pos + 1);
			
			String sParameter = sFormattedQuestion.substring(i1Pos,i2Pos +1);
			String sValue = "";
			
			if (sParameter.startsWith("$DIVISION")) {
//				String sKey = sParameter.substring(1,sParameter.length()-1);
//				for (int i = 0; i < ref.size(); i++) {
//					ETSSurveyReference refdata = (ETSSurveyReference) ref.elementAt(i);
//					if (refdata.getYear().equalsIgnoreCase(sYear) && refdata.getReference().equalsIgnoreCase(ETSSurveyConstants.REFERENCE_DIV) && refdata.getKey().equalsIgnoreCase(sKey)) {
//						sValue = refdata.getDescription();
//						break;
//					}
//				}

				// for product, get the value defined in sampptype field.
				// based on that, then get the value from reference table for SAMP_PROD reference 
				// have to code for division 1 - sathish
				String sKey = sParameter.substring(1,sParameter.length()-1);
				
				String sSampleValue = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.SAMPLE_TYPE_FIELD,surveyData);
				
				//String sKey = sParameter.substring(1,sParameter.length()-1);
				for (int i = 0; i < ref.size(); i++) {
					ETSSurveyReference refdata = (ETSSurveyReference) ref.elementAt(i);
					if (refdata.getYear().equalsIgnoreCase(sYear) && refdata.getReference().equalsIgnoreCase(ETSSurveyConstants.SAMPLE_DIV) && refdata.getKey().equalsIgnoreCase(sSampleValue)) {
						sValue = refdata.getDescription();
						break;
					}
				}

			} else if (sParameter.startsWith("$PROVIDER")) {
				String sProvField = "";
				String sKey = sParameter.substring(1,sParameter.length()-1);
				for (int i = 0; i < ref.size(); i++) {
					ETSSurveyReference refdata = (ETSSurveyReference) ref.elementAt(i);
					if (refdata.getYear().equalsIgnoreCase(sYear) && refdata.getReference().equalsIgnoreCase(ETSSurveyConstants.REFERENCE_PROV_FIELD) && refdata.getKey().equalsIgnoreCase(sKey)) {
						sProvField = refdata.getDescription();
						break;
					}
				}
				// have gotten the provider field.. now get the value from the survey data for this field...
				String sSurveyDataValue = "";
				for (int i = 0; i < surveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) surveyData.elementAt(i);
					if (data.getYear().equalsIgnoreCase(sYear) && data.getResponseId().equalsIgnoreCase(sResponseId) && data.getSurveyKey().equalsIgnoreCase(sProvField)) {
						sSurveyDataValue = data.getSurveyValue();
						break;
					}
				}
				// with this value.. not go to reference table and get the master data value..
				for (int i = 0; i < ref.size(); i++) {
					ETSSurveyReference refdata = (ETSSurveyReference) ref.elementAt(i);
					if (refdata.getYear().equalsIgnoreCase(sYear) && refdata.getReference().equalsIgnoreCase(ETSSurveyConstants.REFERENCE_PROVIDER) && refdata.getKey().equalsIgnoreCase(sSurveyDataValue)) {
						sValue = refdata.getDescription();
						break;
					}
				}
			} else if (sParameter.startsWith("$RATING")) {
				
				String sQuestionId = sParameter.substring(7,sParameter.length()-1);
				
				String sRatingFieldName = "";
				
				for (int i = 0; i < mapping.size(); i++) {
					ETSSurveyMapping mapdata = (ETSSurveyMapping) mapping.elementAt(i);
					if (mapdata.getYear().equalsIgnoreCase(sYear) && mapdata.getQuestionId().equalsIgnoreCase(sQuestionId) && mapdata.getResponseType().equalsIgnoreCase(ETSSurveyConstants.RESPONSE_RATING)) {
						String sTemp = mapdata.getQuestionGroup();
						if (sTemp.indexOf(",") >= 0) {
							// get the first field in the group...
							sRatingFieldName = sTemp.substring(0,sTemp.indexOf(","));
						} else {
							sRatingFieldName = sTemp;
						}
						break;
					}
				}
				
				// get the actual rating from the survey data for the rating field...
				for (int i = 0; i < surveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) surveyData.elementAt(i);
					if (data.getYear().equalsIgnoreCase(sYear) && data.getResponseId().equalsIgnoreCase(sResponseId) && data.getSurveyKey().equalsIgnoreCase(sRatingFieldName)) {
						sValue = data.getSurveyValue();
						break;
					}
				}
				
			} else if (sParameter.startsWith("$PRODUCT")) {
				
				// for product, get the value defined in sampptype field.
				// based on that, then get the value from reference table for SAMP_PROD reference 
				
				String sSampleValue = getSurveyFieldData(sYear,sResponseId,ETSSurveyConstants.SAMPLE_TYPE_FIELD,surveyData);
				
				//String sKey = sParameter.substring(1,sParameter.length()-1);
				for (int i = 0; i < ref.size(); i++) {
					ETSSurveyReference refdata = (ETSSurveyReference) ref.elementAt(i);
					if (refdata.getYear().equalsIgnoreCase(sYear) && refdata.getReference().equalsIgnoreCase(ETSSurveyConstants.SAMPLE_PROD) && refdata.getKey().equalsIgnoreCase(sSampleValue)) {
						sValue = refdata.getDescription();
						break;
					}
				}

			} else if (sParameter.startsWith("$AREA")) {
				
				String sAreaField = "";
				String sKey = sParameter.substring(1,sParameter.length()-1);
				for (int i = 0; i < ref.size(); i++) {
					ETSSurveyReference refdata = (ETSSurveyReference) ref.elementAt(i);
					if (refdata.getYear().equalsIgnoreCase(sYear) && refdata.getReference().equalsIgnoreCase(ETSSurveyConstants.REFERENCE_AREA_FIELD) && refdata.getKey().equalsIgnoreCase(sKey)) {
						sAreaField = refdata.getDescription();
						break;
					}
				}
				// have gotten the provider field.. now get the value from the survey data for this field...
				String sSurveyDataValue = "";
				for (int i = 0; i < surveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) surveyData.elementAt(i);
					if (data.getYear().equalsIgnoreCase(sYear) && data.getResponseId().equalsIgnoreCase(sResponseId) && data.getSurveyKey().equalsIgnoreCase(sAreaField)) {
						sSurveyDataValue = data.getSurveyValue();
						break;
					}
				}
				// with this value.. not go to reference table and get the master data value..
				for (int i = 0; i < ref.size(); i++) {
					ETSSurveyReference refdata = (ETSSurveyReference) ref.elementAt(i);
					if (refdata.getYear().equalsIgnoreCase(sYear) && refdata.getReference().equalsIgnoreCase(ETSSurveyConstants.REFERENCE_AREA) && refdata.getKey().equalsIgnoreCase(sSurveyDataValue)) {
						sValue = refdata.getDescription();
						break;
					}
				}
			} else if (sParameter.startsWith("$COMPETITOR")) {
				
				String sKey = sParameter.substring(1,sParameter.length()-1);
				
				// get the field name where this is defined
				String sFieldName = "";
				for (int i = 0; i < ref.size(); i++) {
					ETSSurveyReference refdata = (ETSSurveyReference) ref.elementAt(i);
					if (refdata.getYear().equalsIgnoreCase(sYear) && refdata.getReference().equalsIgnoreCase(ETSSurveyConstants.REFERENCE_COMPETITOR) && refdata.getKey().equalsIgnoreCase(sKey)) {
						sFieldName = refdata.getDescription();
						break;
					}
				}
				
				// now get the field value
				String sFieldValue = "";
				for (int i = 0; i < surveyData.size(); i++) {
					ETSSurveyData data = (ETSSurveyData) surveyData.elementAt(i);
					if (data.getYear().equalsIgnoreCase(sYear) && data.getResponseId().equalsIgnoreCase(sResponseId) && data.getSurveyKey().equalsIgnoreCase(sFieldName)) {
						sFieldValue = data.getSurveyValue();
						break;
					}
				}
				
				if (sFieldValue.trim().equalsIgnoreCase("1")) {
					sValue = survey.getProvider1();
				} else if (sFieldValue.trim().equalsIgnoreCase("2")) {
					sValue = survey.getProvider2();
				} else if (sFieldValue.trim().equalsIgnoreCase("3")) {
					sValue = survey.getProvider3();
				}
			}
			
			sFormattedQuestion = sFormattedQuestion.substring(0,i1Pos) + "<i>" + sValue + "</i>" + sFormattedQuestion.substring(i2Pos +1);
			
		}
		
		return sFormattedQuestion;
	}

	private static String getReferenceData(String sYear, String sRef, String sKey, Vector vRefData) {
		
		String sValue = "";
				
		for (int i = 0; i < vRefData.size(); i++) {
			ETSSurveyReference ref = (ETSSurveyReference) vRefData.elementAt(i);
			if (sYear.equalsIgnoreCase(ref.getYear()) && sRef.equalsIgnoreCase(ref.getReference()) && sKey.equalsIgnoreCase(ref.getKey())) {
				sValue = ref.getDescription();
			}
		}
		
		return sValue;
		
	}
	
	private static String getSurveyFieldData(String sYear, String sResponseId, String sSurveyKey, Vector vSurveyData) {
		
		String sValue = "";
				
		for (int i = 0; i < vSurveyData.size(); i++) {
			ETSSurveyData data = (ETSSurveyData) vSurveyData.elementAt(i);
			if (sYear.equalsIgnoreCase(data.getYear()) && sResponseId.equalsIgnoreCase(data.getResponseId()) && sSurveyKey.equalsIgnoreCase(data.getSurveyKey())) {
				sValue = data.getSurveyValue();
			}
		}
		
		return sValue;
		
	}
	
	private static String getQuestionText(String sYear, String sQuestionId, Vector vQuestions) {
		
		String sValue = "";
				
		for (int i = 0; i < vQuestions.size(); i++) {
			ETSSurveyQuestion question = (ETSSurveyQuestion) vQuestions.elementAt(i);
			if (sYear.equalsIgnoreCase(question.getYear().trim()) && sQuestionId.equalsIgnoreCase(question.getQuestionId().trim())) {
				sValue = question.getQuestionText().trim();
			}
		}
		
		return sValue;
		
	}
	
	public static String replaceMappingStr(String sInStr, String sYear, String sQuestionId, String sValue) {
		
		while (sInStr.indexOf("$") >= 0) {
			
			int iStartPos = sInStr.indexOf("$");
			int iEndPos = sInStr.indexOf("$", iStartPos +1);
		
			String sStrToReplace = sInStr.substring(iStartPos,iEndPos +1);
			String sNewStr = "";
			
			if (sStrToReplace.equalsIgnoreCase("$YEAR$")) {
				sNewStr = "'" + sYear + "'";
			} else if (sStrToReplace.equalsIgnoreCase("$QUESTION_ID$")) {
				sNewStr = "'" + sQuestionId + "'";
			} else if (sStrToReplace.equalsIgnoreCase("$VALUE$")) {
				sNewStr = "'" + sValue + "'";
			}
			
			sInStr = sInStr.substring(0,iStartPos) + sNewStr + sInStr.substring(iEndPos +1);
			
		}				
		
		return sInStr;
	}
}
