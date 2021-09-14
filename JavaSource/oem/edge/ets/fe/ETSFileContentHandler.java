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


package oem.edge.ets.fe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.datasource.ConnectionFailedException;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.pmo.ETSPMODoc;
import oem.edge.util.BlobStreamHelper;

public class ETSFileContentHandler {
	public final static String Copyright =
		"(C) Copyright IBM Corp.  2002, 2003";

	//private DbConnect dbConnect = null;
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rset = null;
	private boolean isConnected = false;

	public ETSFileContentHandler() {
		super();

		// may want to read properties file to get relative root path
	}

	
	/**
	 * @param req
	 * @param resp
	 * @param document
	 * @throws ServletException
	 * @throws IOException
	 */
	public void deliverProjectStatus(
		HttpServletRequest req,
		HttpServletResponse resp,
		String strProjectId,
		String strSourceId,
		String strDestId)
		throws ServletException, IOException {

		if (strProjectId == null)
			return;

		OutputStream out = null;
		EdgeAccessCntrl es = null;

		String sDownloadFlag = req.getParameter("download");

		if (sDownloadFlag == null || sDownloadFlag.trim().equals("")) {
			sDownloadFlag = "";
		} else {
			sDownloadFlag = sDownloadFlag.trim();
		}

		String mmtype = "";

		if (sDownloadFlag.trim().equalsIgnoreCase("Y")) {
			resp.setHeader("Cache-Control", null);
			resp.setHeader(
				"Content-Disposition",
				"attachment; filename=".concat("status.html"));
			resp.setHeader("Content-Type", "application/octet-stream");
		} else {
			resp.setHeader("Cache-Control", null);
			mmtype = "text/html";
			resp.setContentType(mmtype);
		}

		out = resp.getOutputStream();

		retrieveProjectStatus(strProjectId, strSourceId, strDestId, out);
		out.close();
	}
	
	/**
	 * @param req
	 * @param resp
	 * @param document
	 * @throws ServletException
	 * @throws IOException
	 */
	public void deliverContent(
		HttpServletRequest req,
		HttpServletResponse resp,
		ETSDoc document)
		throws ServletException, IOException {

		if (document == null)
			return;

		OutputStream out = null;
		EdgeAccessCntrl es = null;

		String sDownloadFlag = req.getParameter("download");

		if (sDownloadFlag == null || sDownloadFlag.trim().equals("")) {
			sDownloadFlag = "";
		} else {
			sDownloadFlag = sDownloadFlag.trim();
		}

		long fileLength = document.getSize();

		String mmtype = "";

		if (sDownloadFlag.trim().equalsIgnoreCase("Y")) {
			resp.setHeader("Cache-Control", null);
			resp.setHeader(
				"Content-Disposition",
				"attachment; filename=".concat(document.getFileName()));
			resp.setHeader("Content-Type", "application/octet-stream");
			System.err.println(
				"************************* mmtype = application/octet-stream");
		} else {
			resp.setHeader("Cache-Control", null);
			mmtype =
				ETSMimeDataList.getMimeTypeByExtension(
					(document.getFileType()).toLowerCase());
			resp.setContentType(mmtype);
			System.err.println("************************* mmtype = " + mmtype);
		}

		resp.setContentLength((int) fileLength);

		out = resp.getOutputStream();

		retrieve(document, out);
		out.close();
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
		ETSDoc udDocument,
		String idDocFile)
		throws ServletException, IOException {

		if (udDocument == null) {
			return;
		}

		OutputStream out = null;
		EdgeAccessCntrl es = null;

		ETSDocFile udDocFile = null;
		try {
			DocumentDAO udDAO = new DocumentDAO();
			udDAO.setConnection(ETSDBUtils.getConnection());
			udDocFile =
				udDAO.getDocFile(
					udDocument.getId(),
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
			resp.setHeader("Cache-Control", null);
			resp.setHeader(
				"Content-Disposition",
				"attachment; filename=".concat(udDocFile.getFileName()));
			resp.setHeader("Content-Type", "application/octet-stream");
			System.err.println(
				"************************* mmtype = application/octet-stream");
		} else {
			resp.setHeader("Cache-Control", null);
			resp.setHeader(
					"Content-Disposition",
					"attachment; filename=".concat(udDocFile.getFileName()));
			mmtype =
				ETSMimeDataList.getMimeTypeByExtension(
					(udDocFile.getType()).toLowerCase());
			resp.setContentType(mmtype);
			System.err.println("************************* mmtype = " + mmtype);
		}

		resp.setContentLength((int) fileLength);

		out = resp.getOutputStream();

		retrieve(udDocument.getId(), udDocFile.getDocfileId(), out);
		out.close();
	}

	/**
	 * @param req
	 * @param resp
	 * @param document
	 * @throws ServletException
	 * @throws IOException
	 */
	public void deliverContent(
		HttpServletRequest req,
		HttpServletResponse resp,
		ETSPMODoc document)
		throws ServletException, IOException {

		if (document == null)
			return;

		OutputStream out = null;
		EdgeAccessCntrl es = null;

		String sDownloadFlag = req.getParameter("download");

		if (sDownloadFlag == null || sDownloadFlag.trim().equals("")) {
			sDownloadFlag = "";
		} else {
			sDownloadFlag = sDownloadFlag.trim();
		}

		long fileLength = document.getCompressedSize();

		String mmtype = "";

		if (sDownloadFlag.trim().equalsIgnoreCase("Y")) {
			resp.setHeader("Cache-Control", null);
			resp.setHeader(
				"Content-Disposition",
				"attachment; filename=".concat(document.getDocDesc()));
			resp.setHeader("Content-Type", "application/octet-stream");
			System.err.println(
				"************************* mmtype = application/octet-stream");
		} else {
			resp.setHeader("Cache-Control", null);
			resp.setHeader(
					"Content-Disposition",
					"attachment; filename=".concat(document.getDocDesc()));
			mmtype =
				ETSMimeDataList.getMimeTypeByExtension(
					(document.getFileType()).toLowerCase());
			resp.setContentType(mmtype);
			System.err.println("************************* mmtype = " + mmtype);
		}

		out = resp.getOutputStream();

		retrieve(document, out);
		out.close();
	}

	/**
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	protected void readFile(InputStream in, OutputStream out)
		throws IOException {
		byte[] buffer = new byte[2 * 1024 * 1024];
		int totalRead = 0;
		int bytesRead = 0;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			if (bytesRead > 0) {
				totalRead += bytesRead;
			}
		}
	}

	/**
	 * @return
	 */
	private boolean connect() {
		if (isConnected == true)
			return true;
		try {
			conn = ETSDBUtils.getConnection();
			isConnected = true;
		} catch (java.sql.SQLException ex) {

		} catch (Exception ex1) {

		}
			return isConnected;
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
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		} catch (SQLException ex) {
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		}

		ETSDBUtils.close(conn);
		isConnected = false;
	}

	/**
	 * @param strProjectId
	 * @param strSourceId
	 * @param strDestId
	 * @param out
	 */
	public void retrieveProjectStatus(String strProjectId, String strSourceId, String strDestId, OutputStream out) {
		String strQuery =
			"SELECT STATUS_INFO FROM ETS.WS_PROJECT_STATUS WHERE PROJECT_ID = '"
				+ strProjectId
				+ "'";
//				+ " AND SOURCE_ID = '"
//				+ strSourceId
//				+ "' AND DEST_ID = '"
//				+ strDestId
//				+ "' WITH UR";
		try {
			BlobStreamHelper.streamBlob(out, "etsds", strQuery);
		} catch (ConnectionFailedException ex) {
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		} catch (SQLException ex) {
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		}

		ETSDBUtils.close(conn);
		isConnected = false;
	}

	/**
	 * @param doc
	 * @param out
	 */
	public void retrieve(ETSDoc doc, OutputStream out) {
		String strQuery =
			"SELECT DOCFILE FROM ETS.ETS_DOCFILE WHERE DOC_ID = "
				+ doc.getId()
				+ " for READ ONLY";
		try {
			BlobStreamHelper.streamBlob(out, "etsds", strQuery);
		} catch (ConnectionFailedException ex) {
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		} catch (SQLException ex) {
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		}

		ETSDBUtils.close(conn);
		isConnected = false;
	}

	/**
	 * @param doc
	 * @param out
	 */
	public void retrieve(ETSPMODoc doc, OutputStream out) {
		String strQuery =
			"SELECT DOC_BLOB FROM ETS.ETS_PMO_DOC WHERE DOC_ID = '"
				+ doc.getDocId()
				+ "' for READ ONLY";
		try {
			BlobStreamHelper.streamBlob(out, "etsds", strQuery);
		} catch (ConnectionFailedException ex) {
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		} catch (SQLException ex) {
			ex.printStackTrace(System.out);
			System.out.println("sql error = " + ex);

		}

		ETSDBUtils.close(conn);
		isConnected = false;
	}
}
