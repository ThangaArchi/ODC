/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
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


package oem.edge.ets.fe;

import java.util.Date;


public class ETSDocComment extends ETSDetailedObj {
	public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";
	private static final String CLASS_VERSION = "1.2";
	
	protected int id;
	protected String projectId;
	protected String userid;
	protected String comment;
	protected long commentDate;

	//Need to keep doc version as well as now we show all commments
	private long lDocVersion;

	public ETSDocComment() {
		id = 0;
		projectId = "";
		userid = "";
		commentDate = 0;
		comment = "";
		
	}


	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}


	public void setProjectId(String projectid) {
		this.projectId = projectid;
	}
	public String getProjectId() {
		return projectId;
	}


	public void setUserId(String userid) {
		this.userid = userid;
	}
	public String getUserId() {
		return userid;
	}


	public void setComment(String comm) {
		this.comment = comm;
	}
	public String getComment() {
		return comment;
	}


	public long getCommentDate() {
		return commentDate;
	}
	public void setCommentDate() {
		this.commentDate = new Date().getTime();
	}
	public void setCommentDate(long commdate) {
		this.commentDate = commdate;
	}
	public void setCommentDate(java.sql.Timestamp d) {
		this.commentDate = d.getTime();
	}


	public String formatComment(){
		String s = comment;
		if (s.length() > 147){
			s = s.substring(0,147);
			s = s+"...";	
		}
		
		return s;	
	}

    /**
     * @return Returns the lDocID.
     */
    public long getDocVersion() {
        return lDocVersion;
    }
    /**
     * @param docID The lDocID to set.
     */
    public void setDocVersion(long docVersion) {
        lDocVersion = docVersion;
    }
}
