/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe.documents;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.ismgt.model.ETSIssueAttach;

import org.apache.commons.logging.Log;

/**
 * @author v2srikau
 */
public class IssuesHelper {

	/** Stores the Logger Object */
	private static final Log m_pdLog = EtsLogger.getLogger(IssuesHelper.class);

	/**
	 * @param strProjectId
	 * @param strProblemId
	 * @param iDocFileId
	 * @param strStatus
	 */
	public static void updateIssueFileStatus(
		String strProjectId,
		String strProblemId,
		int iDocFileId,
		String strStatus) {

		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();
			ETSDoc udDoc = udDAO.getDocByProblemId(strProblemId, strProjectId);

			if (udDoc != null) {
				udDAO.updateDocFileStatus(udDoc.getId(), iDocFileId, strStatus);
			}
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
	}

	/**
	 * @param strProjectId
	 * @param strProblemId
	 * @param strOldStatus
	 * @param strStatus
	 * @return
	 */
	public static boolean updateIssueFileStatus(
		String strProjectId,
		String strProblemId,
		String strOldStatus,
		String strStatus) {

		boolean bSuccess = false;
		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();
			ETSDoc udDoc = udDAO.getDocByProblemId(strProblemId, strProjectId);

			if (udDoc != null) {
				bSuccess =
					udDAO.updateDocFileStatus(
						udDoc.getId(),
						strOldStatus,
						strStatus);
			}
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return bSuccess;
	}

	/**
	 * @param strProjectId
	 * @param strProblemId
	 * @param iDocFileId
	 * @return
	 */
	public static boolean deleteIssueFile(
		String strProjectId,
		String strProblemId,
		int iDocFileId) {
		boolean bSuccess = false;
		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();
			ETSDoc udDoc = udDAO.getDocByProblemId(strProblemId, strProjectId);

			if (udDoc != null) {
				bSuccess = udDAO.deleteDocFile(iDocFileId, udDoc.getId());
			}
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return bSuccess;
	}

	/**
	 * @param strProjectId
	 * @param strProblemId
	 * @param strStatusFlag
	 * @return
	 */
	public static List getIssueFiles(
		String strProjectId,
		String strProblemId,
		String strStatusFlag) {
		List lstFiles = new ArrayList();
		DocumentDAO udDAO = new DocumentDAO();

		try {
			udDAO.prepare();
			ETSDoc udDoc = udDAO.getDocByProblemId(strProblemId, strProjectId);
			udDoc = udDAO.getDocByIdAndProject(udDoc.getId(), strProjectId);

			List lstTmp = udDoc.getDocFiles();
			for (int iCounter = 0; iCounter < lstTmp.size(); iCounter++) {
				ETSDocFile udFile = (ETSDocFile) lstTmp.get(iCounter);
				ETSIssueAttach udAttach = new ETSIssueAttach();
				udAttach.setFileName(udFile.getFileName());
				udAttach.setFileDesc(udFile.getFileDescription());
				udAttach.setFileNo(udFile.getDocfileId());
				udAttach.setFileSize(udFile.getSize());
				udAttach.setFileNewFlag(udFile.getFileStatus()); 
				if (StringUtil.isNullorEmpty(strStatusFlag)) {
					lstFiles.add(udAttach);
				} else if (strStatusFlag.equals(udFile.getFileStatus())) {
					lstFiles.add(udAttach);
				}
			}
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return lstFiles;
	}

	/**
	 * @param strProjectId
	 * @param strProblemId
	 * @param strStatusFlag
	 * @return
	 */
	public static List getIssueFilesWithoutFlag(
		String strProjectId,
		String strProblemId,
		String strStatusFlag) {
		List lstFiles = new ArrayList();
		DocumentDAO udDAO = new DocumentDAO();

		try {
			udDAO.prepare();
			ETSDoc udDoc = udDAO.getDocByProblemId(strProblemId, strProjectId);
			udDoc = udDAO.getDocByIdAndProject(udDoc.getId(), strProjectId);

			List lstTmp = udDoc.getDocFiles();
			for (int iCounter = 0; iCounter < lstTmp.size(); iCounter++) {
				ETSDocFile udFile = (ETSDocFile) lstTmp.get(iCounter);
				if (!strStatusFlag.equals(udFile.getFileStatus())) {
					ETSIssueAttach udAttach = new ETSIssueAttach();
					udAttach.setFileName(udFile.getFileName());
					udAttach.setFileDesc(udFile.getFileDescription());
					udAttach.setFileNo(udFile.getDocfileId());
					udAttach.setFileSize(udFile.getSize());
					udAttach.setFileNewFlag(udFile.getFileStatus()); 
					lstFiles.add(udAttach);
				}
			}
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return lstFiles;
	}

	/**
	 * @param strProjectId
	 * @param strProblemId
	 * @param strStatus
	 * @return
	 */
	public static boolean deleteIssueFilesWithoutStatus(
		String strProjectId,
		String strProblemId,
		String strStatus) {
		boolean bSuccess = false;
		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();
			ETSDoc udDoc = udDAO.getDocByProblemId(strProblemId, strProjectId);

			if (udDoc != null) {
				bSuccess = udDAO.deleteDocFile(udDoc.getId(), strStatus);
			}
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return bSuccess;
	}

	/**
	 * @param pdRequest
	 * @param strProblemId
	 * @param strUserId
	 * @return
	 */
	public static int getIssuesDoc(
		String strProjectId,
		String strProblemId,
		String strUserId) {

		ETSCat udCat = getIssuesCat(strProjectId);

		DocumentDAO udDAO = new DocumentDAO();

		int iDocId = -1;
		try {
			udDAO.prepare();
			ETSDoc udDoc = udDAO.getDocByProblemId(strProblemId, strProjectId);
			if (udDoc == null) {
				udDoc = new ETSDoc();
				udDoc.setName(strProblemId);
				udDoc.setDescription(strProblemId);
				udDoc.setProjectId(strProjectId);
				udDoc.setCatId(udCat.getId());
				udDoc.setUserId(strUserId);
				udDoc.setProblemId(strProblemId);
				udDoc.setDocType(Defines.ISSUES_DOC);
				udDAO.addDocMethod(udDoc, new ArrayList(), 0, false);
			}

			udDoc = udDAO.getDocByProblemId(strProblemId, strProjectId);
			iDocId = udDoc.getId();

		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return iDocId;
	}

	/**
	 * @param pdRequest
	 * @param iDocId
	 * @param udIssueDetails
	 * @return 
	 */
	public static boolean attachIssueFile(
		String strProjectId,
		String strStatusFlag,
		int iDocId,
		ETSIssueAttach udIssueDetails) {
		boolean bSuccess = false;
		ETSCat udCat = getIssuesCat(strProjectId);

		String strProblemId = udIssueDetails.getEdgeProblemId();
		DocumentDAO udDAO = new DocumentDAO();

		try {
			udDAO.prepare();
			String strFileName = udIssueDetails.getFileName();
			String strFileDesc = udIssueDetails.getFileDesc();
			int iFileSize = (int) udIssueDetails.getFileSize();
			byte[] fileData = udIssueDetails.getFileData();
			ByteArrayInputStream pdByteInput =
				new ByteArrayInputStream(fileData);
			bSuccess =
				udDAO.addDocFile(
					iDocId,
					strFileName,
					iFileSize,
					strFileDesc,
					strStatusFlag,
					pdByteInput);
			pdByteInput.close();
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return bSuccess;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	private static ETSCat getIssuesCat(String strProjectId) {

		DocumentDAO udDAO = new DocumentDAO();
		ETSCat udCat = null;
		try {
			udDAO.prepare();
			udCat =
				udDAO.getInvisibleCatByName(
					DocConstants.ISSUES_DOC_FOLDER,
					strProjectId);
			if (udCat == null) {
				// Means the top level folder does not exist. So add it.
				udCat =
					udDAO.getCatByName(
						DocConstants.TOP_DOC_FOLDER,
						strProjectId);

				if (udCat == null) {
					udCat = new ETSCat();
					udCat.setProjectId(strProjectId);
				}
				udCat.setName(DocConstants.ISSUES_DOC_FOLDER);
				udCat.setIbmOnly(DocConstants.ETS_PUBLIC);
				udCat.setUserId(DocConstants.ISSUES_DOC_CREATOR);
				udCat.setParentId(-1);
				udCat.setDisplayFlag(DocConstants.IND_NO);
				udCat.setVisibleFlag(DocConstants.IND_NO);

				String[] strResult = udDAO.addCat(udCat);
				if (strResult != null
					&& strResult.length == 2
					&& !StringUtil.isNullorEmpty(strResult[1])) {
					udCat.setId(Integer.parseInt(strResult[1]));
				}
			}
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return udCat;
	}

	/**
	 * @param strProjectId
	 * @param strProblemId
	 * @param strStatus
	 * @return
	 */
	public static boolean deleteIssueFilesWithStatus(
		String strProjectId,
		String strProblemId,
		String strStatus) {
		boolean bSuccess = false;
		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();
			ETSDoc udDoc = udDAO.getDocByProblemId(strProblemId, strProjectId);

			if (udDoc != null) {
				bSuccess =
					udDAO.deleteDocFileWithStatus(udDoc.getId(), strStatus);
			}
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return bSuccess;
	}

	/**
	 * @param strProjectId
	 * @param strProblemId
	 * @param strOldStatus
	 * @param strStatus
	 * @return
	 */
	public static boolean updateIssueFileStatus(
		String strProjectId,
		String strProblemId,
		String strStatus) {

		boolean bSuccess = false;
		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();
			ETSDoc udDoc = udDAO.getDocByProblemId(strProblemId, strProjectId);

			if (udDoc != null) {
				bSuccess = udDAO.updateDocFileStatus(udDoc.getId(), strStatus);
			}
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return bSuccess;
	}
}