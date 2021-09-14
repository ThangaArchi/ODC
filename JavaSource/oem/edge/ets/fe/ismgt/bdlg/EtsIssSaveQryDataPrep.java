/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.dao.EtsIssSaveQryDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterDetailsBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssSaveQryModel;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssSaveQryDataPrep {
	
	private EtsIssFilterObjectKey issFilterObjKey;

	/**
	 * 
	 */
	public EtsIssSaveQryDataPrep(EtsIssFilterObjectKey issFilterObjKey) {
		super();
		this.issFilterObjKey=issFilterObjKey;
		// TODO Auto-generated constructor stub
	}
	
	
	/**
			 * To derive Save Qry Model from EtsIssFilter Model
			 * @param etsFilterBean
			 * @return
			 */

		public EtsIssSaveQryModel deriveSaveQryModel(EtsIssFilterDetailsBean etsFilterBean, String queryView) {

			EtsIssSaveQryModel saveQryModel = new EtsIssSaveQryModel();

			StringBuffer sb = new StringBuffer();

			ArrayList prevIssueTypeList = etsFilterBean.getPrevIssueTypeList();
			ArrayList prevSeverityList = etsFilterBean.getPrevSeverityTypeList();
			ArrayList prevStatusList = etsFilterBean.getPrevStatusTypeList();
			ArrayList prevSubmitterList = etsFilterBean.getPrevIssueSubmitterList();
			ArrayList prevCurOwnerList = etsFilterBean.getPrevIssueOwnerList();

			//////////////////////////
			String issueDateAllStr = AmtCommonUtils.getTrimStr(etsFilterBean.getPrevIssueDateAll());
			String issueStartDateStr = AmtCommonUtils.getTrimStr(etsFilterBean.getPrevIssueStartDate());
			String issueEndDateStr = AmtCommonUtils.getTrimStr(etsFilterBean.getPrevIssueEndDate());

			/////////////////////////add issue type
			if (EtsIssFilterUtils.isArrayListDefnd(prevIssueTypeList)) {

				sb.append("ISSUETYPE");
				sb.append("#%#");
				sb.append(EtsIssFilterUtils.getCommSepStrFromStrList(prevIssueTypeList));
				sb.append("#%#");
				sb.append("~$~");
			}

			//		///////////////////////add severity
			if (EtsIssFilterUtils.isArrayListDefnd(prevSeverityList)) {

				sb.append("SEVERITY");
				sb.append("#%#");
				sb.append(EtsIssFilterUtils.getCommSepStrFromStrList(prevSeverityList));
				sb.append("#%#");
				sb.append("~$~");
			}

			/////////////////////add status
			if (EtsIssFilterUtils.isArrayListDefnd(prevStatusList)) {

				sb.append("STATUS");
				sb.append("#%#");
				sb.append(EtsIssFilterUtils.getCommSepStrFromStrList(prevStatusList));
				sb.append("#%#");
				sb.append("~$~");
			}

			///////////////////add submitter list
			if (EtsIssFilterUtils.isArrayListDefnd(prevSubmitterList)) {

				sb.append("SUBMITTER");
				sb.append("#%#");
				sb.append(EtsIssFilterUtils.getCommSepStrFromStrList(prevSubmitterList));
				sb.append("#%#");
				sb.append("~$~");
			}

			/////////////////add owner list
			if (EtsIssFilterUtils.isArrayListDefnd(prevCurOwnerList)) {

				sb.append("OWNER");
				sb.append("#%#");
				sb.append(EtsIssFilterUtils.getCommSepStrFromStrList(prevCurOwnerList));
				sb.append("#%#");
				sb.append("~$~");
			}

			////date all

			sb.append("DATESALL");
			sb.append("#%#");
			sb.append(issueDateAllStr);
			sb.append("#%#");
			sb.append("~$~");

			sb.append("STARTDT");
			sb.append("#%#");
			sb.append(issueStartDateStr);
			sb.append("#%#");
			sb.append("~$~");

			sb.append("ENDDT");
			sb.append("#%#");
			sb.append(issueEndDateStr);
			sb.append("#%#");
			sb.append("~$~");

			Global.println("FINAL USER SAVE QRY STR====" + sb.toString());

			saveQryModel.setEdgeUserId(issFilterObjKey.getEs().gUSERN);
			saveQryModel.setProjectId(issFilterObjKey.getProjectId());
			saveQryModel.setQueryView(queryView);
			saveQryModel.setQueryName("");
			saveQryModel.setQueryComment("");
			saveQryModel.setQuerySql(sb.toString());
			saveQryModel.setLastUserId(issFilterObjKey.getEs().gUSERN);

			return saveQryModel;
		}

		/**
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

		public EtsIssFilterDetailsBean getFiltDetFromSaveQry(String queryView) throws SQLException, Exception {

			EtsIssSaveQryDAO saveQryDao = new EtsIssSaveQryDAO();

			///
			EtsIssSaveQryModel saveQryModel = new EtsIssSaveQryModel();
			saveQryModel.setEdgeUserId(issFilterObjKey.getEs().gUSERN);
			saveQryModel.setProjectId(issFilterObjKey.getProjectId());
			saveQryModel.setQueryView(queryView);

			String userSaveQry = saveQryDao.getUserSaveQry(saveQryModel);

			//get the map for each filtercondition and corresponding values
			HashMap saveQryMap = getSaveQryMap(userSaveQry);

			//get all val lists from map

			ArrayList prevIssueTypeList = (ArrayList) saveQryMap.get("ISSUETYPE");
			ArrayList prevSeverityList = (ArrayList) saveQryMap.get("SEVERITY");
			ArrayList prevStatusList = (ArrayList) saveQryMap.get("STATUS");
			ArrayList submitterList = (ArrayList) saveQryMap.get("SUBMITTER");
			ArrayList ownerList = (ArrayList) saveQryMap.get("OWNER");
			ArrayList datesAllList = (ArrayList) saveQryMap.get("DATESALL");
			ArrayList startDateList = (ArrayList) saveQryMap.get("STARTDT");
			ArrayList endDateList = (ArrayList) saveQryMap.get("ENDDT");

			///strs
			String datesAll = "";
			String startDt = "";
			String endDt = "";

			//get dates all  str
			if (EtsIssFilterUtils.isArrayListDefnd(datesAllList)) {

				datesAll = (String) datesAllList.get(0);
			}

			//get start dt  str
			if (EtsIssFilterUtils.isArrayListDefnd(startDateList)) {

				startDt = (String) startDateList.get(0);
			}

			//get end dt  str
			if (EtsIssFilterUtils.isArrayListDefnd(endDateList)) {

				endDt = (String) endDateList.get(0);
			}

			//set the params list
			EtsIssFilterDetailsBean filDetBean = new EtsIssFilterDetailsBean();
			filDetBean.setPrevIssueTypeList(prevIssueTypeList);
			filDetBean.setPrevSeverityTypeList(prevSeverityList);
			filDetBean.setPrevStatusTypeList(prevStatusList);
			filDetBean.setPrevIssueSubmitterList(submitterList);
			filDetBean.setPrevIssueOwnerList(ownerList);
			filDetBean.setIssueDateAll(datesAll);
			filDetBean.setIssueStartDate(startDt);
			filDetBean.setIssueEndDate(endDt);

			return filDetBean;

		}

		/**
		 * 
		 * @param userSaveQry
		 * @return
		 */
		public HashMap getSaveQryMap(String userSaveQry) {

			//	get first the str, delimited by ~$~

			HashMap saveQryMap = new HashMap();

			Global.println("USER SAVE QRY==" + userSaveQry);

			if (AmtCommonUtils.isResourceDefined(userSaveQry)) {

				ArrayList delStrList = AmtCommonUtils.getArrayListFromStringTok(userSaveQry, "~$~", 3);

				if (EtsIssFilterUtils.isArrayListDefnd(delStrList)) {

					String tempTok = "";

					for (int i = 0; i < delStrList.size(); i++) {

						tempTok = (String) delStrList.get(i);

						Global.println("TEMP TOK==" + tempTok);

						ArrayList tmpStrList = AmtCommonUtils.getArrayListFromStringTok(tempTok, "#%#", 3);

						String tempKey = "";
						String tempVal = "";

						if (EtsIssFilterUtils.isArrayListDefnd(tmpStrList)) {

							for (int j = 0; j < tmpStrList.size(); j = j + 2) {

								tempKey = (String) tmpStrList.get(j);
								tempVal = (String) tmpStrList.get(j + 1);

								Global.println("tempKey==" + tempKey);
								Global.println("tempVal==" + tempVal);

								ArrayList finalValList = AmtCommonUtils.getArrayListFromStringTok(tempVal, ",");

								saveQryMap.put(tempKey, finalValList);

							} //end of for=j+2

						} //if tmpStrList defined

					} //end of for=i++

				} //if delStrList exists

			} //if userSaveQry exists

			//get all val lists from map
			return saveQryMap;
		}

	
				/**
			 * 
			 * @return
			 */

		public boolean isDefaultUsrSaveQryExists(String queryView) throws SQLException, Exception {

			EtsIssSaveQryModel saveQryModel = new EtsIssSaveQryModel();
			saveQryModel.setEdgeUserId(issFilterObjKey.getEs().gUSERN);
			saveQryModel.setProjectId(issFilterObjKey.getProjectId());
			saveQryModel.setQueryView(queryView);

			EtsIssSaveQryDAO saveQryDao = new EtsIssSaveQryDAO();

			return saveQryDao.isUserSaveQryExists(saveQryModel);
		}


}
