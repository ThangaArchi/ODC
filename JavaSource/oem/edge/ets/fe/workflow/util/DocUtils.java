/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                     */
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

package oem.edge.ets.fe.workflow.util;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Random;
import java.util.Vector;

import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.setmet.validate.ValidateDocumentStageDAO;

/**
 * Class : DocUtils
 * Package : oem.edge.ets.fe.workflow.util
 * Description :
 * Date : Mar 29, 2007
 * 
 * @author : Pradyumna Achar
 * @since 7.1.1
 */
public class DocUtils {

	private static final int MAX_DOCS_PER_PROJECT = 99999;

	public void addDoc(String projectID, String workflowID, String title, 
			String fileName, int size, InputStream is, String loggedUser) {

		Random rand = new Random();

		
		
		DocumentDAO dao = new DocumentDAO();
		try{
		dao.prepare();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		Vector v = null;
		try {
			v = dao.getAllDocs(projectID, "4", "DESC", true, loggedUser);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int docID = rand.nextInt(MAX_DOCS_PER_PROJECT);
		boolean isPresent = false;
		int nloops = 0;
		if (v != null && v.size() != 0) {
			nloops++;
			do {
				for (int x = 0; x < v.size(); x++) {
					ETSDoc doc = (ETSDoc) v.get(x);
					if (docID == doc.getId()) {
						docID = rand.nextInt(MAX_DOCS_PER_PROJECT);
						isPresent = true;
						break;
					}
				}
			} while (isPresent && nloops < MAX_DOCS_PER_PROJECT);
		}
		System.out.println("Looped "+nloops+" time(s) to find a doc ID");
		
		DBAccess db = null;
		try{
			db = new DBAccess();
			String query = "insert into ets.ets_doc (" +
					"doc_id," + //1
					"PROJECT_ID," + //2
					"CAT_ID," + //3
					"USER_ID," + //4
					"DOC_NAME," + //5
					"DOC_UPLOAD_DATE," + //6
					"DOC_PUBLISH_DATE," + //7
					"DOC_UPDATE_DATE," + //8
					"LATEST_VERSION," + //9
					"HAS_PREV_VERSION," + //10
					"LOCK_FINAL_FLAG," + //11
					"LOCKED_BY," + //12
					"DELETE_FLAG," + //13
					"DELETION_DATE," + //14
					"DELETED_BY," + //15
					"UPDATED_BY," + //16
					"DOC_TYPE," + //17
					"MEETING_ID," + //18
					"IBM_ONLY," + //19
					"PMO," + //20
					"DOCUMENT_STATUS," + //21
					"APPROVAL_COMMENTS," + //22
					"APPROVE_DATE," + //23
					"EXPIRY_DATE," + //24
					"APPROVER_ID," + //25
					"SELF_ID," + //26 
					"ISPRIVATE," + //27
					"IBM_CONF," + //28
					"ITAR_UPLOAD_STATUS," + //29
					"issue_id," + //30
					"doc_description," + //31
					"doc_keywords," + //32
					"doc_size" + //33
					")" +
					"values(" ;
					for(int i=0; i<32;i++)query += "?,";
					query+="?)";
			db.prepareDirectQuery(query);
			db.setInt(1,docID);
			db.setString(2,projectID);
			db.setInt(3,Integer.parseInt(MiscUtils.getTc(projectID,MiscUtils.TC_ASSESSMENT)));
			db.setString(4,loggedUser);
			db.setString(5,"[Report] "+title);
			db.setDateTime(6,new java.sql.Date(System.currentTimeMillis()));
			db.setDateTime(7,new java.sql.Date(System.currentTimeMillis()));
			db.setDateTime(8,new java.sql.Date(System.currentTimeMillis()));
			db.setString(9,"1");
			db.setString(10,"0");
			db.setString(11,"x");
			db.setNull(12,Types.VARCHAR);
			db.setString(13,"N");
			db.setNull(14,Types.TIMESTAMP);
			db.setNull(15,Types.VARCHAR);
			db.setNull(16,Types.VARCHAR);
			db.setInt(17,0);
			db.setNull(18,Types.VARCHAR);
			db.setString(19,"0"); 
			db.setNull(20,Types.CHAR);
			db.setNull(21,Types.CHAR);
			db.setNull(22,Types.LONGVARCHAR);
			db.setNull(23,Types.VARCHAR);
			db.setNull(24,Types.VARCHAR);
			db.setNull(25,Types.VARCHAR);
			db.setNull(26,Types.VARCHAR);
			db.setString(27,"0");
			db.setString(28,"Y"); //TODO: Check if this is OK.
			db.setString(29,"C");
			db.setNull(30,Types.VARCHAR);
			db.setNull(31,Types.CLOB);
			db.setNull(32,Types.VARCHAR);
			db.setInt(33,size);
			db.execute();
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		
		
		try {
			dao.addDocFile(docID, fileName, size, is);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ValidateDocumentStageDAO.saveAttachment(projectID,workflowID,docID,loggedUser);
	}
}
