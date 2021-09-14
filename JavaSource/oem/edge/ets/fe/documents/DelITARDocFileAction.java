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
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.common.cipher.ODCipherData;
import oem.edge.common.cipher.ODCipherRSA;
import oem.edge.common.cipher.ODCipherRSAFactory;
import oem.edge.ets.fe.documents.data.ITARDocumentDAO;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ibm.as400.webaccess.common.ConfigObject;

/**
 * @author v2srikau
 */
public class DelITARDocFileAction extends Action {

	private static ODCipherRSA m_pdEdgeCipher = null;
	private static ODCipherRSA m_pdCipher = null;

	private static final long MAX_FILE_SIZE = 100000000;

	public static final String RESOURCE_BUNDLE = "oem.edge.ets.fe.ets-itar";

	public static final String BLD_SERVER = "ets.doc.bld.server";

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
				+ "displayDocumentDetails.wss?"
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

		if (!isNullorEmpty(strContext) && strContext.equals(CTX_ISSUE_MGMT)) {
			System.out.println(
				"DelITARDocFileAction: Performing in Issues Context");
			bIsIssueMgmt = true;
		}
		if (!isNullorEmpty(strContext) && strContext.equals(CTX_MEETINGS)) {
			System.out.println(
				"DelITARDocFileAction: Performing in Meetings Context");
			bIsMeetings = true;
		}
		String strFileDescription = udForm.getDocument().getDescription();
		String strFileStatus = udForm.getDocument().getItarStatus();
		if (isNullorEmpty(strFileDescription)) {
			strFileDescription = "";
		}
		if (isNullorEmpty(strFileStatus)) {
			strFileStatus = "";
		}

		String strDocFileIds = pdRequest.getParameter("delDocFileIds");
		StringBuffer strDocIdsURL = new StringBuffer();
		Enumeration enumDelDocIds = pdRequest.getParameterNames();
		ITARDocumentDAO udDAO = new ITARDocumentDAO();
		try {
			System.out.println("DelITARDocFileAction: Checking Encoded String");
			// First Check for Encoded Token
			String strEncodedString = udForm.getEncodedToken();
			loadCiphers();
			ODCipherData pdCipherData = getCipherData(strEncodedString);
			if (pdCipherData == null) {
				System.out.println("strErrorURL : " + strErrorURL);
				pdResponse.sendRedirect(strErrorURL);
			} else {
				// Check if token is current
				System.out.println("DelITARDocFileAction: Checking Token");
				if (pdCipherData.isCurrent()) {
					ConfigObject pdConfigObject = new ConfigObject();
					String strOld = pdCipherData.getString();
					pdConfigObject.fromString(strOld);
					System.out.println("DelITARDocFileAction: Getting Values");
					strProjectId = pdConfigObject.getProperty("PROJID");
					strDocId = pdConfigObject.getProperty("DOCID");
					strTopCatId = pdConfigObject.getProperty("TOPCATID");
					strCurrCatId = pdConfigObject.getProperty("CURCATID");
					String strEdgeId = pdConfigObject.getProperty("EDGEID");

					// ERROR: If any of the properties are missing
					if (isNullorEmpty(strProjectId)
						|| isNullorEmpty(strDocId)
						|| isNullorEmpty(strCurrCatId)
						|| isNullorEmpty(strTopCatId)
						|| isNullorEmpty(strEdgeId)) {
						System.out.println(
							"DelITARDocFileAction: Invalid Values");
						System.out.println("strErrorURL : " + strErrorURL);
						pdResponse.sendRedirect(strErrorURL);
						return null;
					} else {
						strErrorURL =
							getResource(BLD_SERVER)
								+ "displayDocumentDetails.wss?"
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
						"DelITARDocFileAction: Preparing Connection");
					udDAO.prepare();

					ActionErrors pdErrors = null;
					if (bIsMeetings) {
						boolean bDeleteDocFlag = false;
						int iDocCount=0;
		            	while(enumDelDocIds.hasMoreElements()) {
		            		String delDocIds = enumDelDocIds.nextElement().toString();
		            		if(delDocIds.startsWith("delDocIds")) {
		            			bDeleteDocFlag = udDAO.delDocFile( Integer.parseInt(pdRequest.getParameter(delDocIds)) );
			            		if(bDeleteDocFlag) {
			            			strDocIdsURL.append("&delDocIds"+ iDocCount +"=" +pdRequest.getParameter(delDocIds));
			            			iDocCount++;
			            		}
		            		}
		            	}
					} else {
						int iDocId = Integer.parseInt(strDocId);
						int iDocFileId = -1;
						StringTokenizer strTokens = new StringTokenizer(strDocFileIds, ",");
						while (strTokens.hasMoreTokens()) {
						    String strDocFileId = strTokens.nextToken();
						    if (!isNullorEmpty(strDocFileId)) {
						        try {
						            iDocFileId = Integer.parseInt(strDocFileId);
					            	udDAO.delDocFile(iDocId, iDocFileId);
						        }
						        catch(NumberFormatException e) {
						            // DO NOTHING
						        }
						    }
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

		System.out.println("DelITARDocFileAction: Returning to BOULDER");
		if (bIsIssueMgmt || bIsMeetings) {
			String strReturnURL = getResource(BLD_SERVER) + udForm.getDocAction();
			System.out.println("strReturnURL : " + strReturnURL);
			pdResponse.sendRedirect(
				getResource(BLD_SERVER) + udForm.getDocAction() + strDocIdsURL);
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
					+ udForm.getDocAction()
					+ "&docFileIds=" 
					+ strDocFileIds;

			if (!isNullorEmpty(udForm.getAttachmentNotifyFlag())) {
			    strReturnURL = strReturnURL
					+ "&attachmentNotifyFlag="
					+ udForm.getAttachmentNotifyFlag();
			}
			if (!isNullorEmpty(udForm.getDelDocAttachmentNames())) {
			    strReturnURL = strReturnURL
					+ "&delDocAttachmentNames="
					+ udForm.getDelDocAttachmentNames();
			}

			System.out.println("REDIRECT URL IS : " + strReturnURL);
			pdResponse.sendRedirect(strReturnURL);
		}
		return null;
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

	private boolean isNullorEmpty(String strInput) {
		boolean bIsNullOrEmpty =
			((strInput == null)
				|| (strInput.length() == 0)
				|| strInput.equalsIgnoreCase("NULL"));
		return bIsNullOrEmpty;
	}
}
