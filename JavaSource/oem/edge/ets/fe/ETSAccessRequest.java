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

package oem.edge.ets.fe;

import java.sql.Timestamp;

import oem.edge.ets.fe.ubp.ETSUserDetails;

public class ETSAccessRequest {    // is a synthesized bean

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";

	private int id=0;
	private String userId="";
	private String status="PENDING";
	private String projName="";
	private String mgrEMail="";
	private String requestedBy="";
	private String userMiddleName="";
	private Timestamp dateRequested;
	private ETSUserDetails userDetails = null;


	public ETSAccessRequest() {
		status = "UNDEFINED";
		userDetails = new ETSUserDetails();
	}

	ETSAccessRequest(int key, Timestamp theDateRequested,
						String aUserid,
						String aProjName, String aMgrEMail,
						String aRequestor, String aStatus,
						ETSUserDetails theUserDetails)
	{
		super();
		setId(key);
		setUserId(aUserid);
		setProjName(aProjName);
		setMgrEMail(aMgrEMail);
		setRequestedBy(aRequestor);
		setDateRequested(theDateRequested);
		setStatus(aStatus);
		setUserDetails(theUserDetails);
	}

	public void Accept() {
	}
	public void Reject() {
	}

// Bean sets/gets

	// id (squential PK)
	public void setId (int id) {
		this.id = id;
	}
	public int getId() {
		return this.id;
	}

	// userID
	public void setUserId(String aUserId) {
		this.userId = aUserId;
	}
	public String getUserId() {
		return this.userId;
	}

	public void setStatus(String theStatus) {
		this.status = theStatus;
	}
	public String getStatus() {
		return this.status;
	}

	public void setProjName(String projName) {
		this.projName = projName;
	}
	public String getProjName() {
		return this.projName;
	}

	public void setDateRequested(Timestamp dateReq) {
		this.dateRequested = dateReq;
	}
	public Timestamp getDateRequested() {
		return dateRequested;
	}

	public void setMgrEMail(String mgrEMail) {
		this.mgrEMail = mgrEMail;
	}
	public String getMgrEMail() {
		return this.mgrEMail;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}
	public String getRequestedBy() {
		return this.requestedBy;
	}

	public void setUserDetails(ETSUserDetails uDetails) {
		this.userDetails = uDetails;
	}
	public ETSUserDetails getUserDetails() {
		return this.userDetails;
	}

	// convenience accessors
	public String getWebId() { return userDetails.getWebId(); }
    public String getFirstName() {  return userDetails.getFirstName();   }
    public String getLastName() {return userDetails.getLastName();   }
    public String getEMail() {  return userDetails.getEMail();   }
    public String getStreetAddress() {  return userDetails.getStreetAddress();   }
    public String getCompany() {return userDetails.getCompany();}
    public int getUserType() {  return userDetails.getUserType();    }
    public boolean isUser() { return userDetails.isUserExists();  }
    public String getDecafId() {  return userDetails.getDecafId();    }
    public String getEdgeId() {  return userDetails.getEdgeId();    }
    public String getPocEmail() { return userDetails.getPocEmail();}
    public String getPocId() { return userDetails.getPocId(); }
}

