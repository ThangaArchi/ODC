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

import java.util.List;

import oem.edge.ets.fe.ismgt.model.ETSIssueAttach;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface IsmgtFileUtilsIF {
		
	public int getIssuesDoc(String strProjectId, String strProblemId);
	public int getIssuesDoc(String strProjectId, String strProblemId,String strUserId);
	public boolean attachIssueFile(String strProjectId, String strStatusFlag, int iDocId, ETSIssueAttach udIssueDetails);
	public List getIssueFiles(String strProjectId, String strProblemId, String strStatusFlag);
	public boolean deleteIssueFile(String strProjectId, String strProblemId, int iDocFileId);
	public boolean updateIssueFileStatus(String strProjectId, String strProblemId, String  strOldStatus, String strNewStatus);
	public boolean updateIssueFileStatus(String strProjectId, String strProblemId,  String strNewStatus);
	public List getIssueFilesWithoutFlag(String strProjectId,String strProblemId,String strStatusFlag);
	public boolean deleteIssueFilesWithoutStatus(String strProjectId,String strProblemId,String strStatus);
	public boolean deleteIssueFilesWithStatus(String strProjectId,String strProblemId,String strStatus);

}
