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

package oem.edge.ets.fe.pmo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Vector;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSPMODao {

	public Vector getPMOfficeObjects(Connection con, String sPMOProjectID) throws SQLException, Exception {

		Vector vData = new Vector();
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("SELECT PMO_ID,PMO_PROJECT_ID,PARENT_PMO_ID,NAME,TYPE,EST_START,EST_FINISH,STATE,START,FINISH,PERCENT_COMPLETE,BASE_FINISH,CURR_FINISH,CURR_FINISH_TYPE FROM ETS.ETS_PMO_MAIN WHERE PMO_PROJECT_ID = '" + sPMOProjectID + "' AND IS_REPORTABLE = '" + Defines.PMO_IS_REPORTABLE + "' ORDER BY LAST_TIMESTAMP for READ ONLY");

		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				ETSPMOffice pmOffice = new ETSPMOffice();

				pmOffice.setPMOID(rs.getString("PMO_ID"));
				pmOffice.setPMO_Project_ID(rs.getString("PMO_PROJECT_ID"));
				pmOffice.setPMO_Parent_ID(rs.getString("PARENT_PMO_ID"));
				pmOffice.setName(rs.getString("NAME"));
				pmOffice.setType(rs.getString("TYPE"));
				pmOffice.setEstimatedStartDate(rs.getTimestamp("EST_START"));
				pmOffice.setEstimatedFinishDate(rs.getTimestamp("EST_FINISH"));
				pmOffice.setState(rs.getString("STATE"));
				pmOffice.setStartDate(rs.getTimestamp("START"));
				pmOffice.setFinishDate(rs.getTimestamp("FINISH"));
				pmOffice.setPercentComplete(rs.getString("PERCENT_COMPLETE"));
				pmOffice.setIsReportable(Defines.PMO_IS_REPORTABLE);
				pmOffice.setBaseFinish(rs.getTimestamp("BASE_FINISH"));
				pmOffice.setCurrFinish(rs.getTimestamp("CURR_FINISH"));
				pmOffice.setCurrFinishType(rs.getString("CURR_FINISH_TYPE"));

				vData.addElement(pmOffice);

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return vData;

	}

	public ETSPMOffice getPMOfficeProjectDetails(Connection con, String sPMOProjectID) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		// the pmo_id and pmo_project_id will be the same for the top level element for a pmp project
		StringBuffer sQuery = new StringBuffer("SELECT PMO_ID,PMO_PROJECT_ID,PARENT_PMO_ID,NAME,TYPE,EST_START,EST_FINISH,STATE,START,FINISH,PERCENT_COMPLETE,BASE_FINISH,CURR_FINISH,CURR_FINISH_TYPE FROM ETS.ETS_PMO_MAIN WHERE PMO_PROJECT_ID = '" + sPMOProjectID + "' AND PMO_ID = '" + sPMOProjectID + "' for READ ONLY");
		ETSPMOffice pmOffice = new ETSPMOffice();

		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				pmOffice.setPMOID(rs.getString("PMO_ID"));
				pmOffice.setPMO_Project_ID(rs.getString("PMO_PROJECT_ID"));
				pmOffice.setPMO_Parent_ID(rs.getString("PARENT_PMO_ID"));
				pmOffice.setName(rs.getString("NAME"));
				pmOffice.setType(rs.getString("TYPE"));
				pmOffice.setEstimatedStartDate(rs.getTimestamp("EST_START"));
				pmOffice.setEstimatedFinishDate(rs.getTimestamp("EST_FINISH"));
				pmOffice.setState(rs.getString("STATE"));
				pmOffice.setStartDate(rs.getTimestamp("START"));
				pmOffice.setFinishDate(rs.getTimestamp("FINISH"));
				pmOffice.setPercentComplete(rs.getString("PERCENT_COMPLETE"));
				pmOffice.setBaseFinish(rs.getTimestamp("BASE_FINISH"));
				pmOffice.setCurrFinish(rs.getTimestamp("CURR_FINISH"));
				pmOffice.setCurrFinishType(rs.getString("CURR_FINISH_TYPE"));

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return pmOffice;

	}

	public boolean isStatusRTFAvailable(Connection con, PreparedStatement pstmt) throws SQLException, Exception {

		ResultSet rs = null;
		boolean bAvailable = false;

		try {

			rs = pstmt.executeQuery();

			if (rs.next()) {
				bAvailable = true;
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
		}

		return bAvailable;
	}

	public ETSPMOffice getPMOfficeObjectDetail(Connection con, String sPMOProjectID, String sPMOID) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("SELECT PMO_ID,PMO_PROJECT_ID,PARENT_PMO_ID,NAME,TYPE,EST_START,EST_FINISH,STATE,START,FINISH,PERCENT_COMPLETE,BASE_FINISH,CURR_FINISH,CURR_FINISH_TYPE,LAST_TIMESTAMP FROM ETS.ETS_PMO_MAIN WHERE PMO_PROJECT_ID = '" + sPMOProjectID + "' AND PMO_ID = '" + sPMOID + "' for READ ONLY");
		ETSPMOffice pmOffice = new ETSPMOffice();

		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				pmOffice.setPMOID(rs.getString("PMO_ID"));
				pmOffice.setPMO_Project_ID(rs.getString("PMO_PROJECT_ID"));
				pmOffice.setPMO_Parent_ID(rs.getString("PARENT_PMO_ID"));
				pmOffice.setName(rs.getString("NAME"));
				pmOffice.setType(rs.getString("TYPE"));
				pmOffice.setEstimatedStartDate(rs.getTimestamp("EST_START"));
				pmOffice.setEstimatedFinishDate(rs.getTimestamp("EST_FINISH"));
				pmOffice.setState(rs.getString("STATE"));
				pmOffice.setStartDate(rs.getTimestamp("START"));
				pmOffice.setFinishDate(rs.getTimestamp("FINISH"));
				pmOffice.setPercentComplete(rs.getString("PERCENT_COMPLETE"));

				pmOffice.setBaseFinish(rs.getTimestamp("BASE_FINISH"));
				pmOffice.setCurrFinish(rs.getTimestamp("CURR_FINISH"));
				pmOffice.setCurrFinishType(rs.getString("CURR_FINISH_TYPE"));
				pmOffice.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return pmOffice;

	}

	public String getPMORTF(Connection con, String sPMOID, String sPMOProjectID, int iRTFType) throws SQLException, Exception {

		String sReturn = "";

		Statement stmt = null;
		ResultSet rset = null;

		try {

			String sQuery = "SELECT RTF_BLOB FROM ETS.ETS_PMO_RTF WHERE PARENT_PMO_ID = '" + sPMOID + "' AND PMO_PROJECT_ID = '" + sPMOProjectID + "' AND RTF_ID = " + iRTFType + " for READ ONLY";

			InputStream in = null;

			stmt = con.createStatement();
			rset = stmt.executeQuery(sQuery);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			if (rset.next()) {

				InputStream input = rset.getBinaryStream(1);
				byte buf[] = new byte[512];
				int n = 0;
				int total = 0;
				while ((n = input.read(buf)) > 0) {
					total += n;
					out.write(buf, 0, n);
					out.flush();
				}
				input.close();
			}

			sReturn = out.toString();
			out.close();

		} catch (SQLException e){
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(stmt);
		}

		return sReturn;

	}

	public Vector getPMODocuments(Connection con, String sParentPMOID, String sPMOProjectID) throws SQLException, Exception {
		return getPMODocuments(con,sParentPMOID,sPMOProjectID,Defines.SORT_BY_DATE_STR,Defines.SORT_DES_STR);
	 }

	public Vector getPMODocuments(Connection con, String sParentPMOID, String sPMOProjectID,String sortby, String ad) throws SQLException, Exception {


		String sReturn = "";

		Statement stmt = null;
		ResultSet rset = null;
		Vector vDocs = new Vector();

		try {

			String sb = "doc_name";

			if (sortby.equals(Defines.SORT_BY_DATE_STR)) {
				sb = "update_date " + ad + ",doc_name";
			} else if (sortby.equals(Defines.SORT_BY_TYPE_STR)) {
				sb = "doc_name";
			}
			  //else if (sortby.equals(Defines.SORT_BY_AUTH_STR)){
			  //   sb = "d.user_id "+ad+",d.doc_name";
			  //}


			String sQuery = "SELECT PMO_ID,PMO_PROJECT_ID,DOC_ID,PARENT_PMO_ID,PARENT_TYPE,DOC_NAME,DOC_TYPE,IS_COMPRESSED,DOC_DESC,OWNER_ID,SECURITY_LEVEL,VERSION_INFO,PUBLISH_DATE,UPLOAD_DATE,UPDATE_DATE,COMP_SIZE,UNCOMP_SIZE,LAST_TIMESTAMP FROM ETS.ETS_PMO_DOC WHERE PARENT_PMO_ID = '" + sParentPMOID + "' AND PMO_PROJECT_ID = '" + sPMOProjectID + "' ORDER BY "+sb+" "+ad+" for READ ONLY";

			stmt = con.createStatement();
			rset = stmt.executeQuery(sQuery);

			while (rset.next()) {

				ETSPMODoc doc = new ETSPMODoc();

				String sPMOId = rset.getString("PMO_ID");
				String sPMOProjectId = rset.getString("PMO_PROJECT_ID");
				String sDocId = rset.getString("DOC_ID");
				String sParentID = rset.getString("PARENT_PMO_ID");
				String sParentType = rset.getString("PARENT_TYPE");
				String sDocName = rset.getString("DOC_NAME");
				int iDocType = rset.getInt("DOC_TYPE");
				String isCompressed = rset.getString("IS_COMPRESSED");
				String sDocDesc = rset.getString("DOC_DESC");
				String sOwnerID = rset.getString("OWNER_ID");
				String sSecurityLevel = rset.getString("SECURITY_LEVEL");
				String sVesionInfo = rset.getString("VERSION_INFO");
				Timestamp tPublishDate = rset.getTimestamp("PUBLISH_DATE");
				Timestamp tUploadDate = rset.getTimestamp("UPLOAD_DATE");
				Timestamp tUpdateDate = rset.getTimestamp("UPDATE_DATE");
				int iCompSize = rset.getInt("COMP_SIZE");
				int iUnCompSize = rset.getInt("UNCOMP_SIZE");
				Timestamp tLastTimestamp = rset.getTimestamp("LAST_TIMESTAMP");

				doc.setCompressedSize(iCompSize);
				doc.setDocDesc(ETSUtils.checkNull(sDocDesc));
				doc.setDocId(ETSUtils.checkNull(sDocId));
				doc.setDocName(ETSUtils.checkNull(sDocName));
				doc.setDocType(iDocType);
				doc.setIsCompressed(ETSUtils.checkNull(isCompressed));
				doc.setLastTimestamp(tLastTimestamp);
				doc.setOwnerId(ETSUtils.checkNull(sOwnerID));
				doc.setParentPMOId(ETSUtils.checkNull(sParentID));
				doc.setParentType(ETSUtils.checkNull(sParentType));
				doc.setPMOId(sPMOId);
				doc.setPMOProjectId(sPMOProjectId);
				doc.setPublishDate(tPublishDate);
				doc.setSecurityLevel(ETSUtils.checkNull(sSecurityLevel));
				doc.setUncompressedSize(iUnCompSize);
				doc.setUpdateDate(tUpdateDate);
				doc.setUploadDate(tUploadDate);
				doc.setVersionInfo(ETSUtils.checkNull(sVesionInfo));

				vDocs.addElement(doc);

			}


		} catch (SQLException e){
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(stmt);
		}

		return vDocs;

	}

	public boolean isDocumentsAvailable(Connection con, ETSParams params, ETSPMODao pmoDAO, Vector vDetails, String ID, boolean isAvailable) throws SQLException, Exception {


			try {

				ETSProj proj = params.getETSProj();

				Vector vDocuments = pmoDAO.getPMODocuments(con,ID,proj.getPmo_project_id());
	
				if (vDocuments != null && vDocuments.size() > 0) {
					isAvailable = true;
				} 
				
				if (!isAvailable) {

					for (int i = 0; i < vDetails.size(); i++) {
	
						ETSPMOffice pmo = new ETSPMOffice();
	
						pmo = (ETSPMOffice) vDetails.elementAt(i);
	
						if (pmo.getPMO_Parent_ID().trim().equalsIgnoreCase(ID)) {
	
							if (pmo.getType().trim().equalsIgnoreCase(Defines.DOCUMENT_FOLDER)) {
	
								// display the object level documents first if available...
								Vector vDocs = pmoDAO.getPMODocuments(con,pmo.getPMOID(),proj.getPmo_project_id());
	
								if (vDocs != null && vDocs.size() > 0) {
									isAvailable = true;
								}
	
								if (isAvailable) {
									break;
								}
	
								isAvailable = isDocumentsAvailable(con,params,pmoDAO,vDetails,pmo.getPMOID(),isAvailable);
	
							}
	
						}
	
					}
				}

			} catch (SQLException e) {
				throw e;
			} catch (Exception e){
				throw e;
			}

	    	return isAvailable;

		}

	public Vector getPMOfficeSubCats(Connection con, String sPMOProjectID, String sParentPMOID, String sortby, String ad) throws SQLException, Exception {

		String sb = "name";

		  if (sortby.equals(Defines.SORT_BY_DATE_STR)){
			 sb = "name";
		  }
		  else if (sortby.equals(Defines.SORT_BY_TYPE_STR)){
			 sb = "name";
		  }
		  //else if (sortby.equals(Defines.SORT_BY_AUTH_STR)){
		  //   sb = "user_id "+ad+",cat_name";
		  //}

		   Statement stmt = null;
		   ResultSet rs = null;
		   StringBuffer sQuery = new StringBuffer("SELECT PMO_ID,PMO_PROJECT_ID,PARENT_PMO_ID,NAME,TYPE,EST_START,EST_FINISH,STATE,START,FINISH,PERCENT_COMPLETE,BASE_FINISH,CURR_FINISH,CURR_FINISH_TYPE,LAST_TIMESTAMP FROM ETS.ETS_PMO_MAIN WHERE PMO_PROJECT_ID = '" + sPMOProjectID + "' AND PARENT_PMO_ID = '" + sParentPMOID + "' order by "+sb+" "+ad+" for READ ONLY");
		   Vector v = new Vector();


		  try {

			 stmt = con.createStatement();
			 rs = stmt.executeQuery(sQuery.toString());

			 while (rs.next()) {
				ETSPMOffice pmOffice = new ETSPMOffice();
				pmOffice.setPMOID(rs.getString("PMO_ID"));
				pmOffice.setPMO_Project_ID(rs.getString("PMO_PROJECT_ID"));
				pmOffice.setPMO_Parent_ID(rs.getString("PARENT_PMO_ID"));
				pmOffice.setName(rs.getString("NAME"));
				pmOffice.setType(rs.getString("TYPE"));
				pmOffice.setEstimatedStartDate(rs.getTimestamp("EST_START"));
				pmOffice.setEstimatedFinishDate(rs.getTimestamp("EST_FINISH"));
				pmOffice.setState(rs.getString("STATE"));
				pmOffice.setStartDate(rs.getTimestamp("START"));
				pmOffice.setFinishDate(rs.getTimestamp("FINISH"));
				pmOffice.setPercentComplete(rs.getString("PERCENT_COMPLETE"));

				pmOffice.setBaseFinish(rs.getTimestamp("BASE_FINISH"));
				pmOffice.setCurrFinish(rs.getTimestamp("CURR_FINISH"));
				pmOffice.setCurrFinishType(rs.getString("CURR_FINISH_TYPE"));
				pmOffice.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
				v.addElement(pmOffice);
			 }

		  } catch (SQLException e) {
			 throw e;
		  } catch (Exception e) {
			 throw e;
		  } finally {
			 ETSDBUtils.close(rs);
			 ETSDBUtils.close(stmt);
		  }

		  return v;

	   }

	public ETSPMODoc getPMODocument(Connection con, String sDocID, String sProjectID,boolean pmo_proj_id) throws SQLException, Exception {

		  Statement stmt = null;
		  ResultSet rset = null;
		  ETSPMODoc doc = null;

		  try {


			 String sQuery = "SELECT D.PMO_ID,D.PMO_PROJECT_ID,D.DOC_ID,D.PARENT_PMO_ID,D.PARENT_TYPE,D.DOC_NAME,D.DOC_TYPE,D.IS_COMPRESSED,D.DOC_DESC,D.OWNER_ID,D.SECURITY_LEVEL,D.VERSION_INFO,D.PUBLISH_DATE,D.UPLOAD_DATE,D.UPDATE_DATE,D.COMP_SIZE,D.UNCOMP_SIZE,D.LAST_TIMESTAMP ";
			 if (pmo_proj_id)
				sQuery = sQuery + "FROM ETS.ETS_PMO_DOC D WHERE D.DOC_ID = '" + sDocID + "' AND D.PMO_PROJECT_ID = '" + sProjectID + "' ORDER BY D.UPLOAD_DATE DESC for READ ONLY";
			 else
				sQuery = sQuery + "FROM ETS.ETS_PMO_DOC D, ETS.ETS_Projects P WHERE D.DOC_ID = '" + sDocID + "' AND D.PMO_PROJECT_ID = P.PMO_PROJECT_ID AND P.Project_id='" + sProjectID + "' ORDER BY D.UPLOAD_DATE DESC for READ ONLY";

			 stmt = con.createStatement();
			 rset = stmt.executeQuery(sQuery);

			 if (rset.next()) {
				doc = new ETSPMODoc();

				String sPMOId = rset.getString("PMO_ID");
				String sPMOProjectId = rset.getString("PMO_PROJECT_ID");
				String sDocId = rset.getString("DOC_ID");
				String sParentID = rset.getString("PARENT_PMO_ID");
				String sParentType = rset.getString("PARENT_TYPE");
				String sDocName = rset.getString("DOC_NAME");
				int iDocType = rset.getInt("DOC_TYPE");
				String isCompressed = rset.getString("IS_COMPRESSED");
				String sDocDesc = rset.getString("DOC_DESC");
				String sOwnerID = rset.getString("OWNER_ID");
				String sSecurityLevel = rset.getString("SECURITY_LEVEL");
				String sVesionInfo = rset.getString("VERSION_INFO");
				Timestamp tPublishDate = rset.getTimestamp("PUBLISH_DATE");
				Timestamp tUploadDate = rset.getTimestamp("UPLOAD_DATE");
				Timestamp tUpdateDate = rset.getTimestamp("UPDATE_DATE");
				int iCompSize = rset.getInt("COMP_SIZE");
				int iUnCompSize = rset.getInt("UNCOMP_SIZE");
				Timestamp tLastTimestamp = rset.getTimestamp("LAST_TIMESTAMP");

				doc.setCompressedSize(iCompSize);
				doc.setDocDesc(ETSUtils.checkNull(sDocDesc));
				doc.setDocId(ETSUtils.checkNull(sDocId));
				doc.setDocName(ETSUtils.checkNull(sDocName));
				doc.setDocType(iDocType);
				doc.setIsCompressed(ETSUtils.checkNull(isCompressed));
				doc.setLastTimestamp(tLastTimestamp);
				doc.setOwnerId(ETSUtils.checkNull(sOwnerID));
				doc.setParentPMOId(ETSUtils.checkNull(sParentID));
				doc.setParentType(ETSUtils.checkNull(sParentType));
				doc.setPMOId(sPMOId);
				doc.setPMOProjectId(sPMOProjectId);
				doc.setPublishDate(tPublishDate);
				doc.setSecurityLevel(ETSUtils.checkNull(sSecurityLevel));
				doc.setUncompressedSize(iUnCompSize);
				doc.setUpdateDate(tUpdateDate);
				doc.setUploadDate(tUploadDate);
				doc.setVersionInfo(ETSUtils.checkNull(sVesionInfo));
			 }


		  } catch (SQLException e){
			 throw e;
		  } catch (Exception e) {
			 throw e;
		  } finally {
			 ETSDBUtils.close(rset);
			 ETSDBUtils.close(stmt);
		  }

		  return doc;

	   }

	public ETSPMODoc getPMODocument(Connection con, String sDocID, String sProjectID,boolean pmo_proj_id,String sortby, String ad) throws SQLException, Exception {

      Statement stmt = null;
      ResultSet rset = null;
      ETSPMODoc doc = null;


      try {


         String sQuery = "SELECT D.PMO_ID,D.PMO_PROJECT_ID,D.DOC_ID,D.PARENT_PMO_ID,D.PARENT_TYPE,D.DOC_NAME,D.DOC_TYPE,D.IS_COMPRESSED,D.DOC_DESC,D.OWNER_ID,D.SECURITY_LEVEL,D.VERSION_INFO,D.PUBLISH_DATE,D.UPLOAD_DATE,D.UPDATE_DATE,D.COMP_SIZE,D.UNCOMP_SIZE,D.LAST_TIMESTAMP ";
         if (pmo_proj_id)
            sQuery = sQuery + "FROM ETS.ETS_PMO_DOC D WHERE D.DOC_ID = '" + sDocID + "' AND D.PMO_PROJECT_ID = '" + sProjectID + "' for READ ONLY";
         else
            sQuery = sQuery + "FROM ETS.ETS_PMO_DOC D, ETS.ETS_Projects P WHERE D.DOC_ID = '" + sDocID + "' AND D.PMO_PROJECT_ID = P.PMO_PROJECT_ID AND P.Project_id='" + sProjectID + "' for READ ONLY";

         stmt = con.createStatement();
         rset = stmt.executeQuery(sQuery);

         if (rset.next()) {
            doc = new ETSPMODoc();

            String sPMOId = rset.getString("PMO_ID");
            String sPMOProjectId = rset.getString("PMO_PROJECT_ID");
            String sDocId = rset.getString("DOC_ID");
            String sParentID = rset.getString("PARENT_PMO_ID");
            String sParentType = rset.getString("PARENT_TYPE");
            String sDocName = rset.getString("DOC_NAME");
            int iDocType = rset.getInt("DOC_TYPE");
            String isCompressed = rset.getString("IS_COMPRESSED");
            String sDocDesc = rset.getString("DOC_DESC");
            String sOwnerID = rset.getString("OWNER_ID");
            String sSecurityLevel = rset.getString("SECURITY_LEVEL");
            String sVesionInfo = rset.getString("VERSION_INFO");
            Timestamp tPublishDate = rset.getTimestamp("PUBLISH_DATE");
            Timestamp tUploadDate = rset.getTimestamp("UPLOAD_DATE");
            Timestamp tUpdateDate = rset.getTimestamp("UPDATE_DATE");
            int iCompSize = rset.getInt("COMP_SIZE");
            int iUnCompSize = rset.getInt("UNCOMP_SIZE");
            Timestamp tLastTimestamp = rset.getTimestamp("LAST_TIMESTAMP");

            doc.setCompressedSize(iCompSize);
            doc.setDocDesc(ETSUtils.checkNull(sDocDesc));
            doc.setDocId(ETSUtils.checkNull(sDocId));
            doc.setDocName(ETSUtils.checkNull(sDocName));
            doc.setDocType(iDocType);
            doc.setIsCompressed(ETSUtils.checkNull(isCompressed));
            doc.setLastTimestamp(tLastTimestamp);
            doc.setOwnerId(ETSUtils.checkNull(sOwnerID));
            doc.setParentPMOId(ETSUtils.checkNull(sParentID));
            doc.setParentType(ETSUtils.checkNull(sParentType));
            doc.setPMOId(sPMOId);
            doc.setPMOProjectId(sPMOProjectId);
            doc.setPublishDate(tPublishDate);
            doc.setSecurityLevel(ETSUtils.checkNull(sSecurityLevel));
            doc.setUncompressedSize(iUnCompSize);
            doc.setUpdateDate(tUpdateDate);
            doc.setUploadDate(tUploadDate);
            doc.setVersionInfo(ETSUtils.checkNull(sVesionInfo));
         }


      } catch (SQLException e){
         throw e;
      } catch (Exception e) {
         throw e;
      } finally {
         ETSDBUtils.close(rset);
         ETSDBUtils.close(stmt);
      }

      return doc;

   }


}
