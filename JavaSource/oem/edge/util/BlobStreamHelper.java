/*
 * Created on Aug 2, 2005
 *
 *                       Copyright Header Check                            
 *   --------------------------------------------------------------------   *
 *                                                                          *
 *     OCO Source Materials                                                 *
 *                                                                          *
 *     Product(s): PROFIT                                                   *
 *                                                                          *
 *     (C)Copyright IBM Corp. 2001-2004                                     *
 *                                                                          *
 *     All Rights Reserved                                                  *
 *     US Government Users Restricted Rigts                                 *
 *                                                                          *
 *     The source code for this program is not published or otherwise       *
 *     divested of its trade secrets, irrespective of what has been         *
 *     deposited with the US Copyright Office.                              *
 *                                                                          *
 *   --------------------------------------------------------------------   *
 *     Please do not remove any of these commented lines  20 lines          *
 *   --------------------------------------------------------------------   *
 *                       Copyright Footer Check                             *
 *
 */
package oem.edge.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oem.edge.common.DbUtils;
import oem.edge.datasource.ConnectionFailedException;
import oem.edge.datasource.DataSourceManager;

/**
 * @author rapaljeb
 */
public class BlobStreamHelper {
	public static final String VERSION = "1.1";
	
	public BlobStreamHelper() {
		super();
	}

	private static void streamData(BlobFileInputStreamer blobStreamer, OutputStream outputStream) throws IOException {
		int n = 0;
		byte buf[] = new byte[1024];
		while ((n = blobStreamer.read(buf)) != -1) {
		   outputStream.write(buf, 0, n);
		}
	}
	
	/*
	 * stream the blob specified int the SQL
	 * the SQL should return a single row with a blob in the 1st column
	 * i.e. select image from images where imageID = 1
	 */
	public static void streamBlob(OutputStream outputStream, String datasource, String sql) throws ConnectionFailedException, SQLException, IOException {
		BlobFileInputStreamer blobStreamer = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DataSourceManager.getConnection(datasource);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		 	if (rs.next()) {
				Blob blob = rs.getBlob(1);
			 	blobStreamer = new BlobFileInputStreamer(blob);
			 	blobStreamer.setBlobWriterListener(new DbCleanupEventHandler(conn, stmt, rs));
				streamData(blobStreamer, outputStream);
		  	}
		} finally {
			if (blobStreamer == null) {
				DbUtils.close(rs);
				DbUtils.close(stmt);
				DbUtils.close(conn);
			} else {
				// The DbCleanupEventHandler will handle the DB resources asynchronously
				blobStreamer.close();
			}
		}
	}
	
	// the following methods are for testing
	
	// the calling test servlet might have the following service() method:
	// public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
	//   BlobStreamHelper.servletTest(resp);
	// }
	public static void serlvetTest(javax.servlet.http.HttpServletResponse resp) throws IOException, ConnectionFailedException, SQLException {
		String file = "smi.pdf";
		String sql =  "select FILE from TGCM.FILES where FILE_NAME = '"+file+"' for read only";
		resp.setContentType("application/pdf");
		OutputStream outputStream = resp.getOutputStream();
		BlobStreamHelper.streamBlob(outputStream, "tgcmds", sql);
		outputStream.flush();		
	}
	
	public static void main(String args[]) throws Exception {
		String file = "smi.pdf";
		String sql =  "select FILE from TGCM.FILES where FILE_NAME = '"+file+"' for read only";
		FileOutputStream outputStream = new FileOutputStream("/tmp/"+file);
		BlobStreamHelper.streamBlob(outputStream, "tgcmds", sql);
		outputStream.close();
	}
}
