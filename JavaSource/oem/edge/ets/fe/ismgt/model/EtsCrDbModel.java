/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

package oem.edge.ets.fe.ismgt.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrDbModel {

	public static final String VERSION = "1.16.1.16";

	//key params
	public String etsId;
	public String pmoId;
	public String pmoProjectId;
	public String parentPmoId;

	//key params
	public int refNo;
	public String infoSrcFlag;
	public String CRType;

	//	problem creator//
	public String probCreator;
	public Timestamp creationDate;
	public String creationDateStr;

	//submitter profile
	public String custName;
	public String custEmail;
	public String custPhone;
	public String custCompany;

	//
	public String stateAction;

	//
	public String probClass; //is always Defect from 441 onwards
	public String probTitle;
	public String probDesc;
	public String probSeverity;
	public String commFromCust; //comm from cust

	//

	//
	public String ownerIrId;
	public String ownerName;

	//	fields for last userid and last updated timestamp
	public String lastUserId;
	
	//
	public String statusFlag; //C,U,N
	public ArrayList rtfList; //List of RTFs
	
	//
	public String issueSource;
	public String issueAccess;
	public String etsCCList;
		
	public String probState;
	
	public String issueTypeId;

	/**
	 * 
	 */
	public EtsCrDbModel() {
		super();
		
	}

	
}//end of class
