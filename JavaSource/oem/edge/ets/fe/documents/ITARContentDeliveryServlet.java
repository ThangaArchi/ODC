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

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.common.RSA.RSAKeyPair;
import oem.edge.common.cipher.ODCipherData;
import oem.edge.common.cipher.ODCipherRSA;
import oem.edge.common.cipher.ODCipherRSAFactory;
import oem.edge.datasource.ConnectionFailedException;
import oem.edge.ets.fe.ETSMimeDataList;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.data.ITARDocumentDAO;
import oem.edge.util.BlobStreamHelper;

import com.ibm.as400.webaccess.common.ConfigObject;

/**
 * @author v2srikau
 */
public class ITARContentDeliveryServlet extends HttpServlet {

	public final static String Copyright =
		"(C)Copyright IBM Corp.  2001 - 2004";

	private static final String CLASS_VERSION = "1.10";

	private static ODCipherRSA m_pdEdgeCipher = null;
	private static ODCipherRSA m_pdCipher = null;

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws ServletException, IOException {
		handleRequest(pdRequest, pdResponse);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws ServletException, IOException {
		handleRequest(pdRequest, pdResponse);
	}

	/**
	 * @param pdRequest
	 * @param pdResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public void handleRequest(
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws ServletException, IOException {
		Connection conn = null;

		String strEncodedToken = pdRequest.getParameter("encodedToken");

		loadCiphers();
		ODCipherData pdCipherData = getCipherData(strEncodedToken);
		String strDocFileID = pdRequest.getParameter("docfileid");
		String strDocId = pdRequest.getParameter("docid");
		String strProjId = pdRequest.getParameter("projid");
		if (pdCipherData == null) {
			//DO NOTHING
		} else {
			// Check if token is current
			if (pdCipherData.isCurrent()) {
				ConfigObject pdConfigObject = new ConfigObject();
				String strOld = pdCipherData.getString();
				pdConfigObject.fromString(strOld);
				strProjId = pdConfigObject.getProperty("PROJID");
				strDocId = pdConfigObject.getProperty("DOCID");
				strDocFileID = pdConfigObject.getProperty("DOCFILEID");

			}
		}

		ITARDocumentDAO udDAO = new ITARDocumentDAO();
		try {
			conn = ITARDocumentDAO.getDBConnection();
			udDAO.setConnection(conn);

			if (strDocFileID == null || strDocFileID.equals("")) {
				strDocFileID = "1";
			}

//			ETSDoc udDoc =
//				udDAO.getDocByIdAndProject(
//					Integer.parseInt(strDocId),
//					strProjId);

			if (strDocId != null && strDocFileID != null) {
				// start delivery
				try {
//					if (udDoc != null) {
						deliverContent(
							pdRequest,
							pdResponse,
							Integer.parseInt(strDocId),
							strDocFileID);
//					}
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
					return;
				} finally {

				}

			}
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			return;
		} finally {
			try {
				udDAO.cleanup();
			}
			catch(SQLException e) {
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		try {
			super.init(config);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @param req
	 * @param resp
	 * @param udDocument
	 * @param idDocFile
	 * @throws ServletException
	 * @throws IOException
	 */
	public void deliverContent(
		HttpServletRequest req,
		HttpServletResponse resp,
		int iDocId,
		String idDocFile)
		throws ServletException, IOException {

		OutputStream out = null;

		ETSDocFile udDocFile = null;
		try {
			ITARDocumentDAO udDAO = new ITARDocumentDAO();
			udDAO.setConnection(ITARDocumentDAO.getDBConnection());
			udDocFile =
				udDAO.getDocFile(
					iDocId,
					Integer.parseInt(idDocFile));
			udDAO.cleanup();
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		if (udDocFile == null) {
			return;
		}

		String sDownloadFlag = req.getParameter("download");

		if (sDownloadFlag == null || sDownloadFlag.trim().equals("")) {
			sDownloadFlag = "";
		} else {
			sDownloadFlag = sDownloadFlag.trim();
		}

		long fileLength = udDocFile.getSize();

		String mmtype = "";

		if (sDownloadFlag.trim().equalsIgnoreCase("Y")) {
			resp.setHeader(
				"Content-Disposition",
				"attachment; filename=".concat(udDocFile.getFileName()));
			resp.setHeader("Content-Type", "application/octet-stream");
		} else {
			mmtype =
				ETSMimeDataList.getMimeTypeByExtension(
					(udDocFile.getType()).toLowerCase());
			resp.setContentType(mmtype);
			resp.setHeader(
				"Content-Disposition",
				"attachment; filename=".concat(udDocFile.getFileName()));
		}

		resp.setContentLength((int) fileLength);

		out = resp.getOutputStream();

		retrieve(iDocId, udDocFile.getDocfileId(), out);
		out.close();
	}

	/**
	 * @param iDocId
	 * @param iDocFileID
	 * @param out
	 */
	public void retrieve(int iDocId, int iDocFileID, OutputStream out) {
		String strQuery =
			"SELECT DOCFILE FROM ETS.ETS_DOCFILE WHERE DOC_ID = "
				+ iDocId
				+ " AND DOCFILE_ID = "
				+ iDocFileID
				+ " for READ ONLY";
		try {
			BlobStreamHelper.streamBlob(out, "etsds", strQuery);
		} catch (ConnectionFailedException ex) {
			ex.printStackTrace(System.err);

		} catch (IOException ex) {
			ex.printStackTrace(System.err);

		} catch (SQLException ex) {
			ex.printStackTrace(System.err);
		}

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
				t.printStackTrace(System.err);
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
