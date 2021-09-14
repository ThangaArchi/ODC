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
package oem.edge.ets_pmo.domain;

import java.util.ArrayList;
import java.util.List;
/**
 * @author shingte
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DocObject extends PmoObject {
	private List docs=null;
	//private String attachment;
	private String attachement;
	private String summary;
	private String revision;
	private String blob_data;
	private String creation_date;
	private String last_checkin;
	private String compressed_size;
	private String document_size;
	/**
	 * @return Returns the docs.
	 */
	public List getDocobjects() {
		return docs;
	}
	/**
	 * @param docs The docs to set.
	 */
	public void setDocobject(DocObject obj) {
		if (docs==null)
			docs=new ArrayList();
		docs.add(obj);
	}
/**
 * @return Returns the attachment.
 */
public String getAttachement() {
	return attachement;
}
/**
 * @param attachment The attachment to set.
 */
public void setAttachement(String attachment) {
	this.attachement = attachment;
}
/**
 * @return Returns the summary.
 */
public String getSummary() {
	return summary;
}
/**
 * @param summary The summary to set.
 */
public void setSummary(String summary) {
	this.summary = summary;
}
/**
 * @return Returns the blob_data.
 */
public String getBlob_data() {
	return blob_data;
}
/**
 * @param blob_data The blob_data to set.
 */
public void setBlob_data(String blob_data) {
	this.blob_data = blob_data;
}
/**
 * @return Returns the compressed_size.
 */
public String getCompressed_size() {
	return compressed_size;
}
/**
 * @param compressed_size The compressed_size to set.
 */
public void setCompressed_size(String str) {
	this.compressed_size = str; //Integer.parseInt(str);
}
/**
 * @return Returns the creation_date.
 */
public String getCreation_date() {
	return creation_date;
}
/**
 * @param creation_date The creation_date to set.
 */
public void setCreation_date(String str) {
	this.creation_date = str; //Helper.toTimestamp(str);
}
/**
 * @return Returns the document_size.
 */
public String getDocument_size() {
	return document_size;
}
/**
 * @param document_size The document_size to set.
 */
public void setDocument_size(String str) {
	this.document_size = str; //Integer.parseInt(str);
}
/**
 * @return Returns the last_checkin.
 */
public String getLast_checkin() {
	return last_checkin;
}
/**
 * @param last_checkin The last_checkin to set.
 */
public void setLast_checkin(String str) {
	this.last_checkin = str; //Helper.toTimestamp(str);
}
/**
 * @return Returns the revision.
 */
public String getRevision() {
	return revision;
}
/**
 * @param revision The revision to set.
 */
public void setRevision(String revision) {
	this.revision = revision;
}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new StringBuffer()
					.append("\n  id="+this.getId())
					.append("\n  root_id="+this.getRoot_id())
					.append("\n  parent_id="+this.getParent_id())
					.append("\n  element_name="+this.getElement_name())
					.append("\n  attachement="+this.attachement)
					.append("\n  blob_data="+this.getBlob_data())
					.append("\n  summary="+this.summary)
					.append("\n  type="+this.getType())
					.append("\n  document_size="+this.document_size)
					.append("\n  compressed_size="+this.compressed_size)
					.append("\n  revision="+this.revision)
					.append("\n  last_checkin="+this.last_checkin)
					.append("\n  creation_date="+this.creation_date)
					.append("\n  docobjects="+this.getDocobjects())
					.toString();
	}
}
