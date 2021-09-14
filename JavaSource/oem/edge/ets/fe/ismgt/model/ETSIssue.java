package oem.edge.ets.fe.ismgt.model;
import java.util.*;
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

public class ETSIssue {

	// from problem_info_cq1
	public String application_id; // key
	public String edge_problem_id; // key
	public String cq_trk_id; // key
	public String problem_state;
	public int seq_no;
	public String problem_class;
	public String title;
	public String severity;
	public String problem_type;
	public String problem_desc;

	// from problem_info_usr1
	public String problem_creator;
	public java.sql.Date creation_date;
	public String submitDateStr;
	public String cust_name;
	public String cust_email;
	public String cust_phone;
	public String cust_company;
	public String cust_project;
	public String comm_from_cust;
	public String last_userid;
	public String subTypeA;
	public String subTypeB;
	public String subTypeC;
	public String subTypeD;
	public String field_C1;
	public String field_C2;
	public String field_C3;
	public String field_C4;
	public String field_C5;
	public String field_C6;
	public String field_C7;
	public String field_C8;
	////////////////fxpk1////
	public String field_C14; //first name
	public String field_C15; //last name
	public String field_C12; //tc
	////////////////////
	public String ets_cclist;
	public String ets_project_id;
	public String issue_access;
	public String issue_source;
	public String test_case;

	// from problem_info_cq2
	public String rootcause;
	public String field_r1;
	
	//from CQ.ETS_OWNER_CQ//
	public ArrayList probOwnerList;
	
	//for PMO ISSUE
	public ArrayList ownerIdList;
	public ArrayList ownerNameList;
	public String infoSrcFlag;
	public String txnStatusFlag;
	
	//for RTF MAP
	public HashMap rtfmap;
	
	//
	public String userLastAction;
	
	
	public String etsIssuesType;
	
	public String issueTypeId;
	
	//521 fix pk
	public int refNo;

	public ETSIssue() {
	}

}

