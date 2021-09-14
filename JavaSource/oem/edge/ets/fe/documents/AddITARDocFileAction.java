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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.common.cipher.ODCipherData;
import oem.edge.common.cipher.ODCipherRSA;
import oem.edge.common.cipher.ODCipherRSAFactory;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.ITARDocumentDAO;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.ibm.as400.webaccess.common.ConfigObject;

/**
 * @author v2srikau
 */
public class AddITARDocFileAction extends Action {

	private static ODCipherRSA m_pdEdgeCipher = null;
	private static ODCipherRSA m_pdCipher = null;

	private static final long MAX_FILE_SIZE = 100000000;

	public static final String MSG_USER_ERROR = "documents.messages";

	public static final String RESOURCE_BUNDLE = "oem.edge.ets.fe.ets-itar";

	public static final String BLD_SERVER = "ets.doc.bld.server";

	private static final String ERR_ADD_DOC = "doc.fileadd.error";
	private static final String ERR_FILE_EMPTY = "doc.file.empty.error";
	private static final String ERR_FILE_SIZE = "doc.file.size.error";
	private static final String ERR_FILE_ADD_INVALID_FILE = "doc.file.addInvalidFile.error";
	private static final String ERR_FILE_EXISTS = "doc.file.exists.error";
	private static final String ERR_DOC_NAME = "doc.name.error";
	private static final String ERR_DOC_FILE = "doc.file.empty.error";
	private static final String ERR_DOC_KEYWORDS = "doc.keywords.error";

	private static final String EMPTY_STRING = "";
	private static final String CTX_ISSUE_MGMT = "ISSUES";
	private static final String CTX_MEETINGS = "MEETINGS";

	/**
	 * @see org.apache.struts.action.Action#execute(
	 * org.apache.struts.action.ActionMapping,
	 * org.apache.struts.action.ActionForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {

		String strDocId = EMPTY_STRING;
		String strProjectId = EMPTY_STRING;
		String strTopCatId = EMPTY_STRING;
		String strCurrCatId = EMPTY_STRING;
		BaseDocumentForm udForm = (BaseDocumentForm) pdForm;

		String strErrorURL =
			getResource(BLD_SERVER)
				+ "displayAddDocFilesITAR.wss?"
				+ "docid="
				+ strDocId
				+ "&proj="
				+ strProjectId
				+ "&tc="
				+ strTopCatId
				+ "&cc="
				+ strCurrCatId;

		boolean bIsIssueMgmt = false;
		boolean bIsMeetings = false;
		String strContext = udForm.getFormContext();
		String strDocAction = udForm.getDocAction();

		if (!StringUtil.isNullorEmpty(strContext) && strContext.equals(CTX_ISSUE_MGMT)) {
			System.out.println(
				"AddITARDocFileAction: Performing in Issues Context");
			bIsIssueMgmt = true;
		}
		if (!StringUtil.isNullorEmpty(strContext) && strContext.equals(CTX_MEETINGS)) {
			System.out.println(
				"AddITARDocFileAction: Performing in Meetings Context");
			bIsMeetings = true;
		}
		String strFileDescription = udForm.getDocument().getDescription();
		String strFileStatus = udForm.getDocument().getItarStatus();
		if (StringUtil.isNullorEmpty(strFileDescription)) {
			strFileDescription = "";
		}
		if (StringUtil.isNullorEmpty(strFileStatus)) {
			strFileStatus = "";
		}

		// Following 3 are used ONLY by Meetings tab
		String strDocName = udForm.getDocument().getName();
		String strDocKeywords = udForm.getDocument().getKeywords();
		String strDocDescription = udForm.getDocument().getDescription();
		if (StringUtil.isNullorEmpty(strDocName)) {
		    strDocName = "";
		}
		if (StringUtil.isNullorEmpty(strDocKeywords)) {
		    strDocKeywords = "";
		}
		if (StringUtil.isNullorEmpty(strDocDescription)) {
		    strDocDescription = "";
		}

		ITARDocumentDAO udDAO = new ITARDocumentDAO();
		try {
			System.out.println("AddITARDocFileAction: Checking Encoded String");
			// First Check for Encoded Token
			String strEncodedString = udForm.getEncodedToken();
			loadCiphers();
			ODCipherData pdCipherData = getCipherData(strEncodedString);
			if (pdCipherData == null) {
				System.out.println("strErrorURL : " + strErrorURL);
				pdResponse.sendRedirect(strErrorURL);
			} else {
				// Check if token is current
				System.out.println("AddITARDocFileAction: Checking Token");
				if (pdCipherData.isCurrent()) {
					ConfigObject pdConfigObject = new ConfigObject();
					String strOld = pdCipherData.getString();
					pdConfigObject.fromString(strOld);
					System.out.println("AddITARDocFileAction: Getting Values");
					strProjectId = pdConfigObject.getProperty("PROJID");
					strDocId = pdConfigObject.getProperty("DOCID");
					strTopCatId = pdConfigObject.getProperty("TOPCATID");
					strCurrCatId = pdConfigObject.getProperty("CURCATID");
					String strEdgeId = pdConfigObject.getProperty("EDGEID");

					// ERROR: If any of the properties are missing
					if (StringUtil.isNullorEmpty(strProjectId)
						|| StringUtil.isNullorEmpty(strDocId)
						|| StringUtil.isNullorEmpty(strCurrCatId)
						|| StringUtil.isNullorEmpty(strTopCatId)
						|| StringUtil.isNullorEmpty(strEdgeId)) {
						System.out.println(
							"AddITARDocFileAction: Invalid Values");
						System.out.println("strErrorURL : " + strErrorURL);
						pdResponse.sendRedirect(strErrorURL);
						return null;
					} else {
						strErrorURL =
							getResource(BLD_SERVER)
								+ "displayAddDocFilesITAR.wss?"
								+ "docid="
								+ strDocId
								+ "&proj="
								+ strProjectId
								+ "&tc="
								+ strTopCatId
								+ "&cc="
								+ strCurrCatId;
					}

					System.out.println(
						"AddITARDocFileAction: Preparing Connection");
					udDAO.prepare();

					System.out.println("AddITARDocFileAction: Validating ITAR");
					int iDocId = Integer.parseInt(strDocId);
					String strError =
						validateForITARStep(udDAO, iDocId, strProjectId, strDocName, udForm.getUploadedFiles(), bIsIssueMgmt, bIsMeetings, strDocKeywords);

					if (!StringUtil.isNullorEmpty(strError)) {
						System.out.println(
							"AddITARDocFileAction: Found Errors");
						ETSDoc udDoc = udForm.getDocument();
						udDoc.setId(iDocId);
						udForm.setDocument(udDoc);

						if (bIsIssueMgmt || bIsMeetings) {
							strErrorURL =
							    getResource(BLD_SERVER) + strDocAction;
							if (bIsMeetings) {
							    // Use the Error URL in the request param
							    strErrorURL = 
							        getResource(BLD_SERVER) + pdRequest.getParameter("errorURL");
							}
							System.out.println(
									"strErrorURL : "
										+ strErrorURL
										+ "&error="
										+ strError);
								pdResponse.sendRedirect(
									strErrorURL
										+ "&error="
										+ strError);
								return null;
						}
						else {
						System.out.println(
							"strErrorURL : "
								+ strErrorURL
								+ "&error="
										+ strError);
						pdResponse.sendRedirect(
							strErrorURL
								+ "&error="
										+ strError);
						return null;
					}
					}

					List lstFiles = udForm.getUploadedFiles();
					StringBuffer strFiles = new StringBuffer();
					StringBuffer strSizes = new StringBuffer();

					if (lstFiles != null && lstFiles.size() > 0) {
						for (int iCounter = 0;
							iCounter < lstFiles.size();
							iCounter++) {
							FormFile pdFormFile =
								(FormFile) lstFiles.get(iCounter);
							if (!StringUtil.isNullorEmpty(pdFormFile.getFileName())) {
								System.out.println(
									"AddITARDocFileAction: Adding docfile");
								boolean bSuccess = false;
								if (!bIsIssueMgmt) {
									bSuccess =
									udDAO.addDocFile(
										iDocId,
										pdFormFile.getFileName(),
										pdFormFile.getFileSize(),
										pdFormFile.getInputStream());
								} else {
									bSuccess =
										udDAO.addDocFile(
											iDocId,
											pdFormFile.getFileName(),
											pdFormFile.getFileSize(),
											pdFormFile.getInputStream(),
											strFileDescription,
											strFileStatus);
								}
								if (!bSuccess) {
									System.err.println("error in add doc file");
									throw new DocumentException(ERR_ADD_DOC);
								} else {
									strFiles.append(pdFormFile.getFileName());
									strSizes.append(pdFormFile.getFileSize());
									if (iCounter < (lstFiles.size()-1)) {
										strFiles.append(":");
										strSizes.append(":");
									}
								}
							}
						}
					}

					MQHelper udMQHelper = new MQHelper();
					if (bIsIssueMgmt) {
						udMQHelper.sendMQMessage(
							strDocId,
							strDocId,
							strProjectId,
							strFiles.toString(),
							strSizes.toString(),
							strFileDescription,
							strFileStatus,
							strEdgeId,
							null);
					}
					else if (bIsMeetings) {
						udMQHelper.sendMQMessage(
								strDocId,
								strProjectId,
								strFiles.toString(),
								strSizes.toString(),
								strEdgeId,
								strDocName,
								strDocKeywords,
								strDocDescription);
					}
					else {
					    if (!StringUtil.isNullorEmpty(strDocAction)) {
					udMQHelper.sendMQMessage(
						strDocId,
						strDocId,
						strProjectId,
						strFiles.toString(),
							strSizes.toString(),
									strDocAction,
									"",
									strEdgeId,
									udForm.getAttachmentNotifyFlag());
					}
					    else {
							udMQHelper.sendMQMessage(
									strDocId,
									strDocId,
									strProjectId,
									strFiles.toString(),
									strSizes.toString(),
									strEdgeId,
									udForm.getAttachmentNotifyFlag());
					    }
					}

				} else {
					System.out.println("TOKEN HAS EXPIRED!!");
					System.out.println("strErrorURL : " + strErrorURL);
					pdResponse.sendRedirect(strErrorURL);
					return null;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw e;
		} finally {
			udDAO.cleanup();
		}

		System.out.println("AddITARDocFileAction: Returning to BOULDER");
		if (bIsIssueMgmt || bIsMeetings) {
			String strReturnURL = getResource(BLD_SERVER) + strDocAction;
			System.out.println("strReturnURL : " + strReturnURL);
		pdResponse.sendRedirect(
				getResource(BLD_SERVER) + strDocAction);
		} else {
			String strReturnURL =
			getResource(BLD_SERVER)
				+ "updateDocStatus.wss?"
				+ "docid="
				+ strDocId
				+ "&proj="
				+ strProjectId
				+ "&tc="
				+ strTopCatId
				+ "&cc="
				+ strCurrCatId
				+ "&docAction="
					+ udForm.getDocAction();

			System.out.println("REDIRECT URL IS : " + strReturnURL);
			pdResponse.sendRedirect(strReturnURL);
		}
		return null;
	}

	/**
	 * @param lstUploadedFiles
	 * @return
	 */
	private String validateForITARStep(ITARDocumentDAO udDAO, int iDocId, String strProjectId, String strDocName, List lstUploadedFiles, boolean bIsIssues, boolean bIsMeetings, String strKeywords) throws SQLException {
		ActionErrors pdErrors = new ActionErrors();

		String strError = null;
	    ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocId, strProjectId);
		
	    if (bIsMeetings) {
		    if (StringUtil.isNullorEmpty(strDocName) || (strDocName.length() > 128)) {
		        strError = ERR_DOC_NAME;
		    }
			if (!StringUtil.isNullorEmpty(strKeywords)
					&& (strKeywords.length() > 500)) {
		        strError = ERR_DOC_KEYWORDS;
			}
		}
		if (!StringUtil.isNullorEmpty(strError)) {
		    return strError;
		}

			for (int iCounter = 0;
				iCounter < lstUploadedFiles.size();
				iCounter++) {
				FormFile udFormFile = (FormFile) lstUploadedFiles.get(iCounter);

			if (!StringUtil.isNullorEmpty(udFormFile.getFileName())) {
					int iFileSize = udFormFile.getFileSize();
				if (!bIsIssues && isExistingFile(udFormFile, udDoc)) {
				    strError = ERR_FILE_EXISTS;
				    break;
				}
				else if (iFileSize > MAX_FILE_SIZE) {
					strError = ERR_FILE_SIZE;
				    break;
					}
					else if (iFileSize == 0) {
				    strError = ERR_FILE_ADD_INVALID_FILE;
				    break;
					}
				}
			else if (bIsMeetings) {
		        strError = ERR_DOC_FILE;
		        break;
			}
			}

		return strError;
		}

	/**
	 * @param formFile
	 * @param udDoc
	 * @return
	 */
	private boolean isExistingFile(FormFile formFile, ETSDoc udDoc)
	{
		boolean isFileExisting = false;
		
		List lstExistingFiles = udDoc.getDocFiles();
		
		Iterator iterFiles = lstExistingFiles.iterator();
		
		while(iterFiles.hasNext())
		{
			ETSDocFile etsDocFile = (ETSDocFile)iterFiles.next();
			
			if(etsDocFile.getFileName().equals(formFile.getFileName()))
			{  
				isFileExisting = true;
				break;
			}
		}
		return isFileExisting;
	}

	/**
	 * @param strResourceID
	 * @return
	 */
	private String getResource(String strResourceID) {
		String strResource = null;

		try {
			ResourceBundle pdResources =
			ResourceBundle.getBundle(RESOURCE_BUNDLE);

			String strKeyPath = null;
			if (pdResources != null) {
				strResource = pdResources.getString(strResourceID);
			}
		} catch (Exception e) {

		}

		return strResource;
	}

	/**
	 *
	 */
	private static void loadCiphers() {

		if (m_pdEdgeCipher == null || m_pdCipher == null) {

			ResourceBundle pdResources =
				ResourceBundle.getBundle("oem.edge.ets.fe.ets-itar");

			String strKeyPath = null;
			if (pdResources != null) {
				strKeyPath = pdResources.getString("ets.doc.cipher.path");
			} else {
				strKeyPath =
					"/web/ibm/DesignSolutions/odc-cipher/Ciphers_Int/cipher.key";
			}

			if (strKeyPath == null || strKeyPath.trim().equals("")) {
				strKeyPath =
					"/web/ibm/DesignSolutions/odc-cipher/Ciphers_Int/cipher.key";
			}

			ODCipherRSAFactory fac = ODCipherRSAFactory.newFactoryInstance();
			try {
				m_pdEdgeCipher = fac.newInstance(strKeyPath);
			} catch(Throwable t) {
				System.out.println("Error loading CipherFile! [" + strKeyPath + "]");
			}
/*
			try {
				RSAKeyPair pdKeyPair = RSAKeyPair.load(strKeyPath);
				m_pdEdgeCipher = new ODCipherRSA(pdKeyPair);
			} catch (Throwable t) {
				System.err.println(
					"Error loading CipherFile! [" + strKeyPath + "]");
				m_pdEdgeCipher = new ODCipherRSA();
			}
*/
		}
	}

	/**
	 * @param strToken
	 * @return
	 */
	private static ODCipherData getCipherData(String strToken) {
		ODCipherData pdCipherData = null;
		try {
			pdCipherData = m_pdEdgeCipher.decode(strToken);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return pdCipherData;
	}
}
