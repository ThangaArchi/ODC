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
package oem.edge.ets.fe.ismgt.helpers;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.ismgt.model.ETSIssueAttach;
import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssFileAttachUtils implements EtsIssueConstants {
	public static final String VERSION = "1.23.1.23";
	private DocFileUtils docFileUtils;
	private PmoFileUtils pmoFileUtils;

	/**
	 * 
	 */
	public EtsIssFileAttachUtils() {
		super();
		docFileUtils = new DocFileUtils();
		pmoFileUtils = new PmoFileUtils();

	}

	/**
		 * 
		 * 
		 * @param etsIssObjKey
		 * @param edgeProblemId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public String printFileAttachPage(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, String actionType) throws SQLException, Exception {

		boolean isITAR = etsIssObjKey.getProj().isITAR();

		if (isITAR) {

			return docFileUtils.printFileAttachPageForITAR(etsIssObjKey, usr1InfoModel, actionType);
		} else {

			return docFileUtils.printFileAttachPageForNonITAR(etsIssObjKey, usr1InfoModel, actionType);
		}

	}

	

	/**
			 * 
			 * 
			 * @param filecount
			 * @return
			 */

	public String getIndepFileAttachMsg(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, String actionType) {

		return docFileUtils.getIndepFileAttachMsg(etsIssObjKey, usr1InfoModel, actionType);
	}

	/**
				 * 
				 * 
				 * @param filecount
				 * @return
				 */

	public String getIndepViewFileAttachMsg(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, String actionType) {

		return docFileUtils.getIndepViewFileAttachMsg(etsIssObjKey, usr1InfoModel, actionType);
	}

	/**
	  * 
	  * 
	  * @param filecount
	  * @return
	  */

	public String getIndepViewFileAttachMsgForCR(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel, String actionType) {

		return pmoFileUtils.getIndepViewFileAttachMsgForCR(etsIssObjKey, crInfoModel, actionType);

	}

	/**
		 * to attach files for a given issue/change
		 * returns an array of code/messg for success/failure
		 * 
		 */

	public String[] doAttach(HttpServletRequest request) {

		return docFileUtils.doAttach(request);

	}

	/**
			 * To display attached files
			 * 
			 */

	public String printAttachedFilesList(String projectId, String edgeProblemId, String issueSource) throws SQLException, Exception {

		if (!issueSource.equals(ETSPMOSOURCE)) {

			return docFileUtils.printAttachedFilesList(projectId, edgeProblemId);
		} else {

			return pmoFileUtils.printCRAttachedFilesList(edgeProblemId);
		}

	}

	/**
				 * To display attached files
				 * 
				 */

	public String viewAttachedFilesList(String projectId, String edgeProblemId, String issueSource) throws SQLException, Exception {

		String chkIssueSource = AmtCommonUtils.getTrimStr(issueSource);

		if (!chkIssueSource.equals(ETSPMOSOURCE)) {

			return docFileUtils.viewAttachedFilesList(projectId, edgeProblemId);

		} else {

			return pmoFileUtils.printAttachedFilesListForViewCR(edgeProblemId);

		}

	}

	/**
					 * To display attached files
					 * 
					 */

	public String viewAttachedFilesListWithNoLinks(String projectId, String edgeProblemId, String issueSource) throws SQLException, Exception {

		String chkIssueSource = AmtCommonUtils.getTrimStr(issueSource);

		if (!chkIssueSource.equals(ETSPMOSOURCE)) {

			return docFileUtils.viewAttachedFilesListWithNoLinks(projectId, edgeProblemId);

		} else {

			return pmoFileUtils.viewCRAttachedFilesListWithNoLinks(edgeProblemId);
		}

	}

	/**
			 * To display attached files for Modify problem
			 * 
			 */

	public String printAttachedFilesListForModify(String projectId, String edgeProblemId) throws SQLException, Exception {

		return docFileUtils.printAttachedFilesListForModify(projectId, edgeProblemId);

	}

	/**
				 * To display attached files for Modify problem
				 * 
				 */

	public String printPrevAttachedFilesList(String projectId, String edgeProblemId) throws SQLException, Exception {

		return docFileUtils.printPrevAttachedFilesList(projectId, edgeProblemId);

	}

	/**
					 * To display attached files for Modify problem
					 * 
					 */

	public String printPrevAttachedFilesListForCR(String etsId) throws SQLException, Exception {

		return pmoFileUtils.printPrevAttachedFilesListForCR(etsId);

	}

	/**
				 * To display attached files for Modify problem
				 * 
				 */

	public String printCurAttachedFilesList(String projectId, String edgeProblemId) throws SQLException, Exception {

		return docFileUtils.printCurAttachedFilesList(projectId, edgeProblemId);

	}

	/**
		 * 
		 * 
		 * @param etsIssObjKey
		 * @param edgeProblemId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public String printFileAttachPageForCr(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel, String actionType) throws SQLException, Exception {

		return pmoFileUtils.printFileAttachPageForCr(etsIssObjKey, crInfoModel, actionType);

	}

	/**
			 * to attach files for a given issue/change
			 * returns an array of code/messg for success/failure
			 * 
			 */

	public String[] doAttachForCr(HttpServletRequest request, EtsCrProbInfoModel crInfoModel) {

		return pmoFileUtils.doAttachForCr(request, crInfoModel);

	}

	/**
			 * To display attached files
			 * 
			 */

	public String printCRAttachedFilesList(String etsId) throws SQLException, Exception {

		return pmoFileUtils.printCRAttachedFilesList(etsId);

	}

	/**
				 * 
				 * 
				 * @param filecount
				 * @return
				 */

	public String getIndepFileAttachMsgForCR(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel, String actionType) {

		return pmoFileUtils.getIndepFileAttachMsgForCR(etsIssObjKey, crInfoModel, actionType);
	}

	/**
				 * To display attached files
				 * 
				 */

	public String viewCRAttachedFilesList(String etsId) {

		return pmoFileUtils.viewCRAttachedFilesList(etsId);

	}

	/**
					 * To display attached files
					 * 
					 */

	public String viewCRAttachedFilesListWithNoLinks(String etsId) {

		return pmoFileUtils.viewCRAttachedFilesListWithNoLinks(etsId);

	}

	/**
					 * To display attached files for Modify problem
					 * 
					 */

	public String printCurAttachedFilesListForCR(String etsId) throws SQLException, Exception {

		return pmoFileUtils.printCurAttachedFilesListForCR(etsId);

	}

	/**
						 * To display attached files for Modify problem
						 * 
						 */

	public String printCurAttachedFilesListForTYStates(String etsId) throws SQLException, Exception {

		return pmoFileUtils.printCurAttachedFilesListForTYStates(etsId);

	}

	/**
						 * To display attached files for Modify problem
						 * 
						 */

	public String printCurAttachedFilesListForPMOIssuesScr6(String etsId) throws SQLException, Exception {

		return pmoFileUtils.printCurAttachedFilesListForPMOIssuesScr6(etsId);

	}

	/**
						 * To display attached files for Modify problem
						 * 
						 */

	public String printCurAttachedFilesListForViewCR(String etsId) throws SQLException, Exception {

		return pmoFileUtils.printCurAttachedFilesListForViewCR(etsId);

	}

	/**
				 * To display attached files for Modify problem
				 * 
				 */

	public String printAttachedFilesListForCRUpdate(String etsId) throws SQLException, Exception {

		return pmoFileUtils.printAttachedFilesListForCRUpdate(etsId);

	}

	/**
					 * To display attached files for Modify problem
					 * 
					 */

	public String printAttachedFilesListForPMOIssuesResolveScr6(String etsId) throws SQLException, Exception {

		return pmoFileUtils.printAttachedFilesListForPMOIssuesResolveScr6(etsId);

	}

	/**
						 * To display attached files for Modify problem
						 * 
						 */

	public String printAttachedFilesListForPMOIssuesResolveScr3(String etsId) throws SQLException, Exception {

		return pmoFileUtils.printAttachedFilesListForPMOIssuesResolveScr3(etsId);

	}

	/**
					 * To display attached files for Modify problem
					 * 
					 */

	public String printAttachedFilesListForViewCR(String etsId) throws SQLException, Exception {

		return pmoFileUtils.printAttachedFilesListForViewCR(etsId);

	}

	///

	
	//	/get the file_no/file number flag for the files attached under issues attachments
	/**
	 * @param
	 */

	public int getIssuesDoc(String strProjectId, String strProblemId) {

		return docFileUtils.getIssuesDoc(strProjectId, strProblemId);
	}

	//	/get the file_no/file number flag for the files attached under issues attachments
	/**
	 * @param
	 */

	public int getIssuesDoc(String strProjectId, String strProblemId, String strUserId) {

		return docFileUtils.getIssuesDoc(strProjectId, strProblemId, strUserId);
	}

	//	attach file using docs repositroy
	/**
	 * 
	 */

	public boolean attachIssueFile(String strProjectId, String strStatusFlag, int iDocId, ETSIssueAttach udIssueDetails) {

		return docFileUtils.attachIssueFile(strProjectId, strStatusFlag, iDocId, udIssueDetails);
	}

	/**
		 * delete a file for a given problem id
		 */

	public boolean deleteIssueFile(String strProjectId, String strProblemId, int iDocFileId) {

		return docFileUtils.deleteIssueFile(strProjectId, strProblemId, iDocFileId);
	}

	/**
		 * update issue file status for a given problem id
		 * something like this is reqduired 
		 * ETSIssuesManager.updateFileFlagInUsr2(usrSessnModel.getEdgeProblemId(), getEtsIssObjKey().getEs().gUSERN, "T", "E");
		 * ETSIssuesManager.updateFileFlagInUsr2(edgeProblemId, getEtsIssObjKey().getEs().gUSERN, "E", "Y");
		 */

	public boolean updateIssueFileStatus(String strProjectId, String strProblemId, String strOldStatus, String strNewStatus) {

		return docFileUtils.updateIssueFileStatus(strProjectId, strProblemId, strOldStatus, strNewStatus);
	}

	/**
		 * update issue file status for a given problem id
		 * something like this is reqduired 
		 * ETSIssuesManager.updateFileFlagInUsr2(usrSessnModel.getEdgeProblemId(), getEtsIssObjKey().getEs().gUSERN, "T", "E");
		 * ETSIssuesManager.updateFileFlagInUsr2(edgeProblemId, getEtsIssObjKey().getEs().gUSERN, "E", "Y");
		 */

	public boolean updateIssueFileStatus(String strProjectId, String strProblemId, String strNewStatus) {

		return docFileUtils.updateIssueFileStatus(strProjectId, strProblemId, strNewStatus);
	}

	/**
		 * 
		 */

	public boolean deleteIssueFilesWithoutStatus(String strProjectId, String strProblemId, String strStatus) {

		return docFileUtils.deleteIssueFilesWithoutStatus(strProjectId, strProblemId, strStatus);
	}

	/**
		 * 
		 */

	public boolean deleteIssueFilesWithStatus(String strProjectId, String strProblemId, String strStatus) {

		return docFileUtils.deleteIssueFilesWithStatus(strProjectId, strProblemId, strStatus);
	}
	
	

} //class end