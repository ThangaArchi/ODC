/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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

import java.util.ArrayList;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsEjbIssueModel implements java.io.Serializable {



		private java.lang.String application_Id;
		private java.lang.String comm_From_Cust;
		private java.lang.String cq_Trk_Id;
		private java.lang.Object creation_Date;
		private java.lang.String cust_Company;
		private java.lang.String cust_Email;
		private java.lang.String cust_Name;
		private java.lang.String cust_Phone;
		private java.lang.String cust_Project;
		private java.lang.String edge_Problem_Id;
		private java.lang.String ets_Cclist;
		private java.lang.String ets_Project_Id;
		private java.lang.String etsIssuesType;
		private java.lang.String field_C1;
		private java.lang.String field_C12;
		private java.lang.String field_C14;
		private java.lang.String field_C15;
		private java.lang.String field_C2;
		private java.lang.String field_C3;
		private java.lang.String field_C4;
		private java.lang.String field_C5;
		private java.lang.String field_C6;
		private java.lang.String field_C7;
		private java.lang.String field_R1;
		private java.lang.String infoSrcFlag;
		private java.lang.String issue_Access;
		private java.lang.String issue_Source;
		private java.lang.String issueTypeId;
		private java.lang.String last_Userid;
		private ArrayList ownerIdList;
		private ArrayList ownerNameList;
		private java.lang.String problem_Class;
		private java.lang.String problem_Creator;
		private java.lang.String problem_Desc;
		private java.lang.String problem_State;
		private java.lang.String problem_Type;
		private ArrayList probOwnerList;
		private java.lang.String rootcause;
		private java.util.HashMap rtfmap;
		private int seq_No;
		private java.lang.String severity;
		private java.lang.String submitDateStr;
		private java.lang.String subTypeA;
		private java.lang.String subTypeB;
		private java.lang.String subTypeC;
		private java.lang.String subTypeD;
		private java.lang.String test_Case;
		private java.lang.String title;
		private java.lang.String txnStatusFlag;
		private java.lang.String userLastAction;




	/**
	 *
	 */
	public EtsEjbIssueModel() {
		super();
		// TODO Auto-generated constructor stub
	}

		/**
		 * @return
		 */
		public java.lang.String getApplication_Id() {
			return application_Id;
		}

		/**
		 * @return
		 */
		public java.lang.String getComm_From_Cust() {
			return comm_From_Cust;
		}

		/**
		 * @return
		 */
		public java.lang.String getCq_Trk_Id() {
			return cq_Trk_Id;
		}

		/**
		 * @return
		 */
		public java.lang.Object getCreation_Date() {
			return creation_Date;
		}

		/**
		 * @return
		 */
		public java.lang.String getCust_Company() {
			return cust_Company;
		}

		/**
		 * @return
		 */
		public java.lang.String getCust_Email() {
			return cust_Email;
		}

		/**
		 * @return
		 */
		public java.lang.String getCust_Name() {
			return cust_Name;
		}

		/**
		 * @return
		 */
		public java.lang.String getCust_Phone() {
			return cust_Phone;
		}

		/**
		 * @return
		 */
		public java.lang.String getCust_Project() {
			return cust_Project;
		}

		/**
		 * @return
		 */
		public java.lang.String getEdge_Problem_Id() {
			return edge_Problem_Id;
		}

		/**
		 * @return
		 */
		public java.lang.String getEts_Cclist() {
			return ets_Cclist;
		}

		/**
		 * @return
		 */
		public java.lang.String getEts_Project_Id() {
			return ets_Project_Id;
		}

		/**
		 * @return
		 */
		public java.lang.String getEtsIssuesType() {
			return etsIssuesType;
		}

		/**
		 * @return
		 */
		public java.lang.String getField_C1() {
			return field_C1;
		}

		/**
		 * @return
		 */
		public java.lang.String getField_C12() {
			return field_C12;
		}

		/**
		 * @return
		 */
		public java.lang.String getField_C14() {
			return field_C14;
		}

		/**
		 * @return
		 */
		public java.lang.String getField_C15() {
			return field_C15;
		}

		/**
		 * @return
		 */
		public java.lang.String getField_C2() {
			return field_C2;
		}

		/**
		 * @return
		 */
		public java.lang.String getField_C3() {
			return field_C3;
		}

		/**
		 * @return
		 */
		public java.lang.String getField_C4() {
			return field_C4;
		}

		/**
		 * @return
		 */
		public java.lang.String getField_C5() {
			return field_C5;
		}

		/**
		 * @return
		 */
		public java.lang.String getField_C6() {
			return field_C6;
		}

		/**
		 * @return
		 */
		public java.lang.String getField_C7() {
			return field_C7;
		}

		/**
		 * @return
		 */
		public java.lang.String getField_R1() {
			return field_R1;
		}

		/**
		 * @return
		 */
		public java.lang.String getInfoSrcFlag() {
			return infoSrcFlag;
		}

		/**
		 * @return
		 */
		public java.lang.String getIssue_Access() {
			return issue_Access;
		}

		/**
		 * @return
		 */
		public java.lang.String getIssue_Source() {
			return issue_Source;
		}

		/**
		 * @return
		 */
		public java.lang.String getIssueTypeId() {
			return issueTypeId;
		}

		/**
		 * @return
		 */
		public java.lang.String getLast_Userid() {
			return last_Userid;
		}

		/**
		 * @return
		 */
		public ArrayList getOwnerIdList() {
			return ownerIdList;
		}

		/**
		 * @return
		 */
		public ArrayList getOwnerNameList() {
			return ownerNameList;
		}

		/**
		 * @return
		 */
		public java.lang.String getProblem_Class() {
			return problem_Class;
		}

		/**
		 * @return
		 */
		public java.lang.String getProblem_Creator() {
			return problem_Creator;
		}

		/**
		 * @return
		 */
		public java.lang.String getProblem_Desc() {
			return problem_Desc;
		}

		/**
		 * @return
		 */
		public java.lang.String getProblem_State() {
			return problem_State;
		}

		/**
		 * @return
		 */
		public java.lang.String getProblem_Type() {
			return problem_Type;
		}

		/**
		 * @return
		 */
		public ArrayList getProbOwnerList() {
			return probOwnerList;
		}

		/**
		 * @return
		 */
		public java.lang.String getRootcause() {
			return rootcause;
		}

		/**
		 * @return
		 */
		public java.util.HashMap getRtfmap() {
			return rtfmap;
		}

		/**
		 * @return
		 */
		public int getSeq_No() {
			return seq_No;
		}

		/**
		 * @return
		 */
		public java.lang.String getSeverity() {
			return severity;
		}

		/**
		 * @return
		 */
		public java.lang.String getSubmitDateStr() {
			return submitDateStr;
		}

		/**
		 * @return
		 */
		public java.lang.String getSubTypeA() {
			return subTypeA;
		}

		/**
		 * @return
		 */
		public java.lang.String getSubTypeB() {
			return subTypeB;
		}

		/**
		 * @return
		 */
		public java.lang.String getSubTypeC() {
			return subTypeC;
		}

		/**
		 * @return
		 */
		public java.lang.String getSubTypeD() {
			return subTypeD;
		}

		/**
		 * @return
		 */
		public java.lang.String getTest_Case() {
			return test_Case;
		}

		/**
		 * @return
		 */
		public java.lang.String getTitle() {
			return title;
		}

		/**
		 * @return
		 */
		public java.lang.String getTxnStatusFlag() {
			return txnStatusFlag;
		}

		/**
		 * @return
		 */
		public java.lang.String getUserLastAction() {
			return userLastAction;
		}

		/**
		 * @param string
		 */
		public void setApplication_Id(java.lang.String string) {
			application_Id = string;
		}

		/**
		 * @param string
		 */
		public void setComm_From_Cust(java.lang.String string) {
			comm_From_Cust = string;
		}

		/**
		 * @param string
		 */
		public void setCq_Trk_Id(java.lang.String string) {
			cq_Trk_Id = string;
		}

		/**
		 * @param object
		 */
		public void setCreation_Date(java.lang.Object object) {
			creation_Date = object;
		}

		/**
		 * @param string
		 */
		public void setCust_Company(java.lang.String string) {
			cust_Company = string;
		}

		/**
		 * @param string
		 */
		public void setCust_Email(java.lang.String string) {
			cust_Email = string;
		}

		/**
		 * @param string
		 */
		public void setCust_Name(java.lang.String string) {
			cust_Name = string;
		}

		/**
		 * @param string
		 */
		public void setCust_Phone(java.lang.String string) {
			cust_Phone = string;
		}

		/**
		 * @param string
		 */
		public void setCust_Project(java.lang.String string) {
			cust_Project = string;
		}

		/**
		 * @param string
		 */
		public void setEdge_Problem_Id(java.lang.String string) {
			edge_Problem_Id = string;
		}

		/**
		 * @param string
		 */
		public void setEts_Cclist(java.lang.String string) {
			ets_Cclist = string;
		}

		/**
		 * @param string
		 */
		public void setEts_Project_Id(java.lang.String string) {
			ets_Project_Id = string;
		}

		/**
		 * @param string
		 */
		public void setEtsIssuesType(java.lang.String string) {
			etsIssuesType = string;
		}

		/**
		 * @param string
		 */
		public void setField_C1(java.lang.String string) {
			field_C1 = string;
		}

		/**
		 * @param string
		 */
		public void setField_C12(java.lang.String string) {
			field_C12 = string;
		}

		/**
		 * @param string
		 */
		public void setField_C14(java.lang.String string) {
			field_C14 = string;
		}

		/**
		 * @param string
		 */
		public void setField_C15(java.lang.String string) {
			field_C15 = string;
		}

		/**
		 * @param string
		 */
		public void setField_C2(java.lang.String string) {
			field_C2 = string;
		}

		/**
		 * @param string
		 */
		public void setField_C3(java.lang.String string) {
			field_C3 = string;
		}

		/**
		 * @param string
		 */
		public void setField_C4(java.lang.String string) {
			field_C4 = string;
		}

		/**
		 * @param string
		 */
		public void setField_C5(java.lang.String string) {
			field_C5 = string;
		}

		/**
		 * @param string
		 */
		public void setField_C6(java.lang.String string) {
			field_C6 = string;
		}

		/**
		 * @param string
		 */
		public void setField_C7(java.lang.String string) {
			field_C7 = string;
		}

		/**
		 * @param string
		 */
		public void setField_R1(java.lang.String string) {
			field_R1 = string;
		}

		/**
		 * @param string
		 */
		public void setInfoSrcFlag(java.lang.String string) {
			infoSrcFlag = string;
		}

		/**
		 * @param string
		 */
		public void setIssue_Access(java.lang.String string) {
			issue_Access = string;
		}

		/**
		 * @param string
		 */
		public void setIssue_Source(java.lang.String string) {
			issue_Source = string;
		}

		/**
		 * @param string
		 */
		public void setIssueTypeId(java.lang.String string) {
			issueTypeId = string;
		}

		/**
		 * @param string
		 */
		public void setLast_Userid(java.lang.String string) {
			last_Userid = string;
		}

		/**
		 * @param list
		 */
		public void setOwnerIdList(ArrayList list) {
			ownerIdList = list;
		}

		/**
		 * @param list
		 */
		public void setOwnerNameList(ArrayList list) {
			ownerNameList = list;
		}

		/**
		 * @param string
		 */
		public void setProblem_Class(java.lang.String string) {
			problem_Class = string;
		}

		/**
		 * @param string
		 */
		public void setProblem_Creator(java.lang.String string) {
			problem_Creator = string;
		}

		/**
		 * @param string
		 */
		public void setProblem_Desc(java.lang.String string) {
			problem_Desc = string;
		}

		/**
		 * @param string
		 */
		public void setProblem_State(java.lang.String string) {
			problem_State = string;
		}

		/**
		 * @param string
		 */
		public void setProblem_Type(java.lang.String string) {
			problem_Type = string;
		}

		/**
		 * @param list
		 */
		public void setProbOwnerList(ArrayList list) {
			probOwnerList = list;
		}

		/**
		 * @param string
		 */
		public void setRootcause(java.lang.String string) {
			rootcause = string;
		}

		/**
		 * @param map
		 */
		public void setRtfmap(java.util.HashMap map) {
			rtfmap = map;
		}

		/**
		 * @param i
		 */
		public void setSeq_No(int i) {
			seq_No = i;
		}

		/**
		 * @param string
		 */
		public void setSeverity(java.lang.String string) {
			severity = string;
		}

		/**
		 * @param string
		 */
		public void setSubmitDateStr(java.lang.String string) {
			submitDateStr = string;
		}

		/**
		 * @param string
		 */
		public void setSubTypeA(java.lang.String string) {
			subTypeA = string;
		}

		/**
		 * @param string
		 */
		public void setSubTypeB(java.lang.String string) {
			subTypeB = string;
		}

		/**
		 * @param string
		 */
		public void setSubTypeC(java.lang.String string) {
			subTypeC = string;
		}

		/**
		 * @param string
		 */
		public void setSubTypeD(java.lang.String string) {
			subTypeD = string;
		}

		/**
		 * @param string
		 */
		public void setTest_Case(java.lang.String string) {
			test_Case = string;
		}

		/**
		 * @param string
		 */
		public void setTitle(java.lang.String string) {
			title = string;
		}

		/**
		 * @param string
		 */
		public void setTxnStatusFlag(java.lang.String string) {
			txnStatusFlag = string;
		}

		/**
		 * @param string
		 */
		public void setUserLastAction(java.lang.String string) {
			userLastAction = string;
		}

}
