/*
 * Created on May 6, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.ismgt.middleware;

/**
 * @author jetendra
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSMWOwnerRecord {

	public static final String VERSION = "1.0";
	private String owner_email;
	private String owner_id;
	
	//Added by Prasad
	private String backupOwner_email;
	private String backupOwner_id;
	
	/**
	 * @return
	 */
	public String getOwner_email() {
		return owner_email;
	}

	/**
	 * @return
	 */
	public String getOwner_id() {
		return owner_id;
	}

	/**
	 * @param string
	 */
	public void setOwner_email(String string) {
		owner_email = string;
	}

	/**
	 * @param string
	 */
	public void setOwner_id(String string) {
		owner_id = string;
	}

	/**
	 * @return Returns the backupOwner_email.
	 */
	public String getBackupOwner_email() {
		return backupOwner_email;
	}
	/**
	 * @param backupOwner_email The backupOwner_email to set.
	 */
	public void setBackupOwner_email(String backupOwner_email) {
		this.backupOwner_email = backupOwner_email;
	}
	/**
	 * @return Returns the backupOwner_id.
	 */
	public String getBackupOwner_id() {
		return backupOwner_id;
	}
	/**
	 * @param backupOwner_id The backupOwner_id to set.
	 */
	public void setBackupOwner_id(String backupOwner_id) {
		this.backupOwner_id = backupOwner_id;
	}
}
