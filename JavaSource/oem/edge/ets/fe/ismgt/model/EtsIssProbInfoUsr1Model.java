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
 * The purpose of this model to capture all the usr1 info in an object, as when
 * navigates across the pages and store this object into session, to set and retrieve data
 * and submit data to DB.
 * almost identical to CQ.PROBLEM_INFO_USR1 TAB
 */
public class EtsIssProbInfoUsr1Model implements Serializable {

	public static final String VERSION = "1.20.1.22";

	//	core problem info vars
	private String applnId;
	private String edgeProblemId;
	private String cqTrkId;
	private String probState;
	private int seqNo;

	//	problem creator//
	private String probCreator;
	private Timestamp creationDate;

	//submitter profile
	private String custName;
	private String custEmail;
	private String custPhone;
	private String custCompany;
	private String custProject; //Name of project

	//fields to retain tc state, modifier's first name/last name
	private String fieldC12; //tc
	private String fieldC14; //first name
	private String fieldC15; //last name

	//fields for last userid and last updated timestamp
	private String lastUserId;
	private Timestamp lastTimeStamp;

	private String etsProjId; //project id

	private String probClass; //is always Defect from 441 onwards
	private String probTitle;
	private String probDesc;
	private String commFromCust; //comm from cust

	//
	private ArrayList probSevList = new ArrayList();
	private ArrayList prevProbSevList = new ArrayList();

	//	prob type profile
	private ArrayList probTypeList;
	private ArrayList subTypeAList;
	private ArrayList subTypeBList;
	private ArrayList subTypeCList;
	private ArrayList subTypeDList;
	private ArrayList fieldC1List;
	private ArrayList fieldC2List;
	private ArrayList fieldC3List;
	private ArrayList fieldC4List;
	private ArrayList fieldC5List;
	private ArrayList fieldC6List;
	private ArrayList fieldC7List;
	private ArrayList fieldC8List;
	
	//	prev prob type profile
	private ArrayList prevProbTypeList;
	private ArrayList prevSubTypeAList;
	private ArrayList prevSubTypeBList;
	private ArrayList prevSubTypeCList;
	private ArrayList prevSubTypeDList;
	private ArrayList prevFieldC1List;
	private ArrayList prevFieldC2List;
	private ArrayList prevFieldC3List;
	private ArrayList prevFieldC4List;
	private ArrayList prevFieldC5List;
	private ArrayList prevFieldC6List;
	private ArrayList prevFieldC7List;
	private ArrayList prevFieldC8List;

	//display names//
	private String subTypeADispName;
	private String subTypeBDispName;
	private String subTypeCDispName;
	private String subTypeDDispName;

	//refe names//
	private String subTypeARefName;
	private String subTypeBRefName;
	private String subTypeCRefName;
	private String subTypeDRefName;

	//	display names//
	private String fieldC1DispName;
	private String fieldC2DispName;
	private String fieldC3DispName;
	private String fieldC4DispName;
	private String fieldC5DispName;
	private String fieldC6DispName;
	private String fieldC7DispName;
	private String fieldC8DispName;

	//refe names//
	private String fieldC1RefName;
	private String fieldC2RefName;
	private String fieldC3RefName;
	private String fieldC4RefName;
	private String fieldC5RefName;
	private String fieldC6RefName;
	private String fieldC7RefName;
	private String fieldC8RefName;
	
	private String testCase;
	private String etsCcList; //cc list 
	private ArrayList notifyList;
	private ArrayList prevNotifyList;

	private String issueAccess; //issue access
	private String issueSource; //issue source

	private int previousActionState; //previous state
	private int currentActionState; //action state
	private int nextActionState; //next state
	private int cancelActionState; //cancel action state
	private int subtypeActionState; //Sub type action state

	private String subTypeADefnd;
	private String subTypeBDefnd;
	private String subTypeCDefnd;
	private String subTypeDDefnd;

	private String editSubTypeADefnd;
	private String editSubTypeBDefnd;
	private String editSubTypeCDefnd;
	private String editSubTypeDDefnd;

	private String fieldC1Defnd;
	private String fieldC2Defnd;
	private String fieldC3Defnd;
	private String fieldC4Defnd;
	private String fieldC5Defnd;
	private String fieldC6Defnd;
	private String fieldC7Defnd;
	private String fieldC8Defnd;
	private String testCaseDefnd;

	//
	private String errMsg;

	//
	private String chkIssTypeIbmOnly;

	//
	private int usr_seq_no;
	private int cq_seq_no;
	private ArrayList ownerList;
	private ArrayList ownerNameList;

	//
	private String lastAction;
	private String commentLogStr;

	//
	private String issueType;
	private ArrayList prevIssueTypeList;

	private String submissionDate;
	
	//
	private ArrayList histList;
	
	///
	private String infoSrcFlag;
	private String txnStatusFlag;
	
	//
	private HashMap rtfMap;
	
	//ISSUE category
	private String etsIssuesType;
	
	//
	private ArrayList extUserList;
	private ArrayList prevExtUserList;
	
	//
	private String userType;
	
	//
	private boolean usrIssueSubscribe;
	private boolean usrIssTypSubscribe;
	
	//
	private String issueTypeId;
	
	//
	private boolean issueTypIdActive; 
	
	//
	private String refNo;
	private boolean issueSrcPMO;

	/**
	 * Constructor for EtsIssProbInfoUsr1Model.
	 */

	public EtsIssProbInfoUsr1Model() {
		super();
	}

	/**
	 * @return
	 */
	public String getApplnId() {
		return applnId;
	}

	/**
	 * @return
	 */
	public String getCommFromCust() {
		return commFromCust;
	}

	/**
	 * @return
	 */
	public String getCqTrkId() {
		return cqTrkId;
	}

	/**
	 * @return
	 */
	public Timestamp getCreationDate() {
		return creationDate;
	}

	/**
	 * @return
	 */
	public String getCustCompany() {
		return custCompany;
	}

	/**
	 * @return
	 */
	public String getCustEmail() {
		return custEmail;
	}

	/**
	 * @return
	 */
	public String getCustName() {
		return custName;
	}

	/**
	 * @return
	 */
	public String getCustPhone() {
		return custPhone;
	}

	/**
	 * @return
	 */
	public String getCustProject() {
		return custProject;
	}

	/**
	 * @return
	 */
	public String getEdgeProblemId() {
		return edgeProblemId;
	}

	/**
	 * @return
	 */
	public String getEtsCcList() {
		return etsCcList;
	}

	/**
	 * @return
	 */
	public String getEtsProjId() {
		return etsProjId;
	}

	/**
	 * @return
	 */
	public String getFieldC12() {
		return fieldC12;
	}

	/**
	 * @return
	 */
	public String getFieldC14() {
		return fieldC14;
	}

	/**
	 * @return
	 */
	public String getFieldC15() {
		return fieldC15;
	}

	/**
	 * @return
	 */
	public String getFieldC1DispName() {
		return fieldC1DispName;
	}

	/**
	 * @return
	 */
	public ArrayList getFieldC1List() {
		return fieldC1List;
	}

	/**
	 * @return
	 */
	public String getFieldC1RefName() {
		return fieldC1RefName;
	}

	/**
	 * @return
	 */
	public String getFieldC2DispName() {
		return fieldC2DispName;
	}

	/**
	 * @return
	 */
	public ArrayList getFieldC2List() {
		return fieldC2List;
	}

	/**
	 * @return
	 */
	public String getFieldC2RefName() {
		return fieldC2RefName;
	}

	/**
	 * @return
	 */
	public String getFieldC3DispName() {
		return fieldC3DispName;
	}

	/**
	 * @return
	 */
	public ArrayList getFieldC3List() {
		return fieldC3List;
	}

	/**
	 * @return
	 */
	public String getFieldC3RefName() {
		return fieldC3RefName;
	}

	/**
	 * @return
	 */
	public String getFieldC4DispName() {
		return fieldC4DispName;
	}

	/**
	 * @return
	 */
	public ArrayList getFieldC4List() {
		return fieldC4List;
	}

	/**
	 * @return
	 */
	public String getFieldC4RefName() {
		return fieldC4RefName;
	}

	/**
	 * @return
	 */
	public String getFieldC5DispName() {
		return fieldC5DispName;
	}

	/**
	 * @return
	 */
	public ArrayList getFieldC5List() {
		return fieldC5List;
	}

	/**
	 * @return
	 */
	public String getFieldC5RefName() {
		return fieldC5RefName;
	}

	/**
	 * @return
	 */
	public String getFieldC6DispName() {
		return fieldC6DispName;
	}

	/**
	 * @return
	 */
	public ArrayList getFieldC6List() {
		return fieldC6List;
	}

	/**
	 * @return
	 */
	public String getFieldC6RefName() {
		return fieldC6RefName;
	}

	/**
	 * @return
	 */
	public String getFieldC7DispName() {
		return fieldC7DispName;
	}

	/**
	 * @return
	 */
	public ArrayList getFieldC7List() {
		return fieldC7List;
	}

	/**
	 * @return
	 */
	public String getFieldC7RefName() {
		return fieldC7RefName;
	}

	/**
	 * @return
	 */
	public String getIssueAccess() {
		return issueAccess;
	}

	/**
	 * @return
	 */
	public String getIssueSource() {
		return issueSource;
	}

	/**
	 * @return
	 */
	public Timestamp getLastTimeStamp() {
		return lastTimeStamp;
	}

	/**
	 * @return
	 */
	public String getLastUserId() {
		return lastUserId;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevFieldC1List() {
		return prevFieldC1List;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevFieldC2List() {
		return prevFieldC2List;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevFieldC3List() {
		return prevFieldC3List;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevFieldC4List() {
		return prevFieldC4List;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevFieldC5List() {
		return prevFieldC5List;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevFieldC6List() {
		return prevFieldC6List;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevFieldC7List() {
		return prevFieldC7List;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList getPrevFieldC8List() {
		return prevFieldC8List;
	}
	
	/**
	 * @return
	 */
	public ArrayList getPrevProbSevList() {
		return prevProbSevList;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevProbTypeList() {
		return prevProbTypeList;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevSubTypeAList() {
		return prevSubTypeAList;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevSubTypeBList() {
		return prevSubTypeBList;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevSubTypeCList() {
		return prevSubTypeCList;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevSubTypeDList() {
		return prevSubTypeDList;
	}

	/**
	 * @return
	 */
	public String getProbClass() {
		return probClass;
	}

	/**
	 * @return
	 */
	public String getProbCreator() {
		return probCreator;
	}

	/**
	 * @return
	 */
	public String getProbDesc() {
		return probDesc;
	}

	/**
	 * @return
	 */
	public ArrayList getProbSevList() {
		return probSevList;
	}

	/**
	 * @return
	 */
	public String getProbState() {
		return probState;
	}

	/**
	 * @return
	 */
	public String getProbTitle() {
		return probTitle;
	}

	/**
	 * @return
	 */
	public ArrayList getProbTypeList() {
		return probTypeList;
	}

	/**
	 * @return
	 */
	public int getSeqNo() {
		return seqNo;
	}

	/**
	 * @return
	 */
	public String getSubTypeADispName() {
		return subTypeADispName;
	}

	/**
	 * @return
	 */
	public ArrayList getSubTypeAList() {
		return subTypeAList;
	}

	/**
	 * @return
	 */
	public String getSubTypeARefName() {
		return subTypeARefName;
	}

	/**
	 * @return
	 */
	public String getSubTypeBDispName() {
		return subTypeBDispName;
	}

	/**
	 * @return
	 */
	public ArrayList getSubTypeBList() {
		return subTypeBList;
	}

	/**
	 * @return
	 */
	public String getSubTypeBRefName() {
		return subTypeBRefName;
	}

	/**
	 * @return
	 */
	public String getSubTypeCDispName() {
		return subTypeCDispName;
	}

	/**
	 * @return
	 */
	public ArrayList getSubTypeCList() {
		return subTypeCList;
	}

	/**
	 * @return
	 */
	public String getSubTypeCRefName() {
		return subTypeCRefName;
	}

	/**
	 * @return
	 */
	public String getSubTypeDDispName() {
		return subTypeDDispName;
	}

	/**
	 * @return
	 */
	public ArrayList getSubTypeDList() {
		return subTypeDList;
	}

	/**
	 * @return
	 */
	public String getSubTypeDRefName() {
		return subTypeDRefName;
	}

	/**
	 * @return
	 */
	public String getTestCase() {
		return testCase;
	}

	/**
	 * @param string
	 */
	public void setApplnId(String string) {
		applnId = string;
	}

	/**
	 * @param string
	 */
	public void setCommFromCust(String string) {
		commFromCust = string;
	}

	/**
	 * @param string
	 */
	public void setCqTrkId(String string) {
		cqTrkId = string;
	}

	/**
	 * @param timestamp
	 */
	public void setCreationDate(Timestamp timestamp) {
		creationDate = timestamp;
	}

	/**
	 * @param string
	 */
	public void setCustCompany(String string) {
		custCompany = string;
	}

	/**
	 * @param string
	 */
	public void setCustEmail(String string) {
		custEmail = string;
	}

	/**
	 * @param string
	 */
	public void setCustName(String string) {
		custName = string;
	}

	/**
	 * @param string
	 */
	public void setCustPhone(String string) {
		custPhone = string;
	}

	/**
	 * @param string
	 */
	public void setCustProject(String string) {
		custProject = string;
	}

	/**
	 * @param string
	 */
	public void setEdgeProblemId(String string) {
		edgeProblemId = string;
	}

	/**
	 * @param string
	 */
	public void setEtsCcList(String string) {
		etsCcList = string;
	}

	/**
	 * @param string
	 */
	public void setEtsProjId(String string) {
		etsProjId = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC12(String string) {
		fieldC12 = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC14(String string) {
		fieldC14 = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC15(String string) {
		fieldC15 = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC1DispName(String string) {
		fieldC1DispName = string;
	}

	/**
	 * @param list
	 */
	public void setFieldC1List(ArrayList list) {
		fieldC1List = list;
	}

	/**
	 * @param string
	 */
	public void setFieldC1RefName(String string) {
		fieldC1RefName = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC2DispName(String string) {
		fieldC2DispName = string;
	}

	/**
	 * @param list
	 */
	public void setFieldC2List(ArrayList list) {
		fieldC2List = list;
	}

	/**
	 * @param string
	 */
	public void setFieldC2RefName(String string) {
		fieldC2RefName = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC3DispName(String string) {
		fieldC3DispName = string;
	}

	/**
	 * @param list
	 */
	public void setFieldC3List(ArrayList list) {
		fieldC3List = list;
	}

	/**
	 * @param string
	 */
	public void setFieldC3RefName(String string) {
		fieldC3RefName = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC4DispName(String string) {
		fieldC4DispName = string;
	}

	/**
	 * @param list
	 */
	public void setFieldC4List(ArrayList list) {
		fieldC4List = list;
	}

	/**
	 * @param string
	 */
	public void setFieldC4RefName(String string) {
		fieldC4RefName = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC5DispName(String string) {
		fieldC5DispName = string;
	}

	/**
	 * @param list
	 */
	public void setFieldC5List(ArrayList list) {
		fieldC5List = list;
	}

	/**
	 * @param string
	 */
	public void setFieldC5RefName(String string) {
		fieldC5RefName = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC6DispName(String string) {
		fieldC6DispName = string;
	}

	/**
	 * @param list
	 */
	public void setFieldC6List(ArrayList list) {
		fieldC6List = list;
	}

	/**
	 * @param string
	 */
	public void setFieldC6RefName(String string) {
		fieldC6RefName = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC7DispName(String string) {
		fieldC7DispName = string;
	}

	/**
	 * @param list
	 */
	public void setFieldC7List(ArrayList list) {
		fieldC7List = list;
	}

	/**
	 * @param string
	 */
	public void setFieldC7RefName(String string) {
		fieldC7RefName = string;
	}

	/**
	 * @param string
	 */
	public void setIssueAccess(String string) {
		issueAccess = string;
	}

	/**
	 * @param string
	 */
	public void setIssueSource(String string) {
		issueSource = string;
	}

	/**
	 * @param timestamp
	 */
	public void setLastTimeStamp(Timestamp timestamp) {
		lastTimeStamp = timestamp;
	}

	/**
	 * @param string
	 */
	public void setLastUserId(String string) {
		lastUserId = string;
	}

	/**
	 * @param list
	 */
	public void setPrevFieldC1List(ArrayList list) {
		prevFieldC1List = list;
	}

	/**
	 * @param list
	 */
	public void setPrevFieldC2List(ArrayList list) {
		prevFieldC2List = list;
	}

	/**
	 * @param list
	 */
	public void setPrevFieldC3List(ArrayList list) {
		prevFieldC3List = list;
	}

	/**
	 * @param list
	 */
	public void setPrevFieldC4List(ArrayList list) {
		prevFieldC4List = list;
	}

	/**
	 * @param list
	 */
	public void setPrevFieldC5List(ArrayList list) {
		prevFieldC5List = list;
	}

	/**
	 * @param list
	 */
	public void setPrevFieldC6List(ArrayList list) {
		prevFieldC6List = list;
	}

	/**
	 * @param list
	 */
	public void setPrevFieldC7List(ArrayList list) {
		prevFieldC7List = list;
	}
	
	/**
	 * @param list
	 */
	public void setPrevFieldC8List(ArrayList list) {
		prevFieldC8List = list;
	}	
	/**
	 * @param list
	 */
	public void setPrevProbSevList(ArrayList list) {
		prevProbSevList = list;
	}

	/**
	 * @param list
	 */
	public void setPrevProbTypeList(ArrayList list) {
		prevProbTypeList = list;
	}

	/**
	 * @param list
	 */
	public void setPrevSubTypeAList(ArrayList list) {
		prevSubTypeAList = list;
	}

	/**
	 * @param list
	 */
	public void setPrevSubTypeBList(ArrayList list) {
		prevSubTypeBList = list;
	}

	/**
	 * @param list
	 */
	public void setPrevSubTypeCList(ArrayList list) {
		prevSubTypeCList = list;
	}

	/**
	 * @param list
	 */
	public void setPrevSubTypeDList(ArrayList list) {
		prevSubTypeDList = list;
	}

	/**
	 * @param string
	 */
	public void setProbClass(String string) {
		probClass = string;
	}

	/**
	 * @param string
	 */
	public void setProbCreator(String string) {
		probCreator = string;
	}

	/**
	 * @param string
	 */
	public void setProbDesc(String string) {
		probDesc = string;
	}

	/**
	 * @param list
	 */
	public void setProbSevList(ArrayList list) {
		probSevList = list;
	}

	/**
	 * @param string
	 */
	public void setProbState(String string) {
		probState = string;
	}

	/**
	 * @param string
	 */
	public void setProbTitle(String string) {
		probTitle = string;
	}

	/**
	 * @param list
	 */
	public void setProbTypeList(ArrayList list) {
		probTypeList = list;
	}

	/**
	 * @param i
	 */
	public void setSeqNo(int i) {
		seqNo = i;
	}

	/**
	 * @param string
	 */
	public void setSubTypeADispName(String string) {
		subTypeADispName = string;
	}

	/**
	 * @param list
	 */
	public void setSubTypeAList(ArrayList list) {
		subTypeAList = list;
	}

	/**
	 * @param string
	 */
	public void setSubTypeARefName(String string) {
		subTypeARefName = string;
	}

	/**
	 * @param string
	 */
	public void setSubTypeBDispName(String string) {
		subTypeBDispName = string;
	}

	/**
	 * @param list
	 */
	public void setSubTypeBList(ArrayList list) {
		subTypeBList = list;
	}

	/**
	 * @param string
	 */
	public void setSubTypeBRefName(String string) {
		subTypeBRefName = string;
	}

	/**
	 * @param string
	 */
	public void setSubTypeCDispName(String string) {
		subTypeCDispName = string;
	}

	/**
	 * @param list
	 */
	public void setSubTypeCList(ArrayList list) {
		subTypeCList = list;
	}

	/**
	 * @param string
	 */
	public void setSubTypeCRefName(String string) {
		subTypeCRefName = string;
	}

	/**
	 * @param string
	 */
	public void setSubTypeDDispName(String string) {
		subTypeDDispName = string;
	}

	/**
	 * @param list
	 */
	public void setSubTypeDList(ArrayList list) {
		subTypeDList = list;
	}

	/**
	 * @param string
	 */
	public void setSubTypeDRefName(String string) {
		subTypeDRefName = string;
	}

	/**
	 * @param string
	 */
	public void setTestCase(String string) {
		testCase = string;
	}

	/**
	 * @return
	 */
	public int getCurrentActionState() {
		return currentActionState;
	}

	/**
	 * @param i
	 */
	public void setCurrentActionState(int i) {
		currentActionState = i;
	}

	/**
	 * @return
	 */
	public String getFieldC1Defnd() {
		return fieldC1Defnd;
	}

	/**
	 * @return
	 */
	public String getFieldC2Defnd() {
		return fieldC2Defnd;
	}

	/**
	 * @return
	 */
	public String getFieldC3Defnd() {
		return fieldC3Defnd;
	}

	/**
	 * @return
	 */
	public String getFieldC4Defnd() {
		return fieldC4Defnd;
	}

	/**
	 * @return
	 */
	public String getFieldC5Defnd() {
		return fieldC5Defnd;
	}

	/**
	 * @return
	 */
	public String getFieldC6Defnd() {
		return fieldC6Defnd;
	}

	/**
	 * @return
	 */
	public String getFieldC7Defnd() {
		return fieldC7Defnd;
	}

	/**
	 * @return
	 */
	public String getSubTypeADefnd() {
		return subTypeADefnd;
	}

	/**
	 * @return
	 */
	public String getSubTypeBDefnd() {
		return subTypeBDefnd;
	}

	/**
	 * @return
	 */
	public String getSubTypeCDefnd() {
		return subTypeCDefnd;
	}

	/**
	 * @return
	 */
	public String getSubTypeDDefnd() {
		return subTypeDDefnd;
	}

	/**
	 * @return
	 */
	public String getTestCaseDefnd() {
		return testCaseDefnd;
	}

	/**
	 * @param string
	 */
	public void setFieldC1Defnd(String string) {
		fieldC1Defnd = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC2Defnd(String string) {
		fieldC2Defnd = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC3Defnd(String string) {
		fieldC3Defnd = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC4Defnd(String string) {
		fieldC4Defnd = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC5Defnd(String string) {
		fieldC5Defnd = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC6Defnd(String string) {
		fieldC6Defnd = string;
	}

	/**
	 * @param string
	 */
	public void setFieldC7Defnd(String string) {
		fieldC7Defnd = string;
	}

	/**
	 * @param string
	 */
	public void setSubTypeADefnd(String string) {
		subTypeADefnd = string;
	}

	/**
	 * @param string
	 */
	public void setSubTypeBDefnd(String string) {
		subTypeBDefnd = string;
	}

	/**
	 * @param string
	 */
	public void setSubTypeCDefnd(String string) {
		subTypeCDefnd = string;
	}

	/**
	 * @param string
	 */
	public void setSubTypeDDefnd(String string) {
		subTypeDDefnd = string;
	}

	/**
	 * @param string
	 */
	public void setTestCaseDefnd(String string) {
		testCaseDefnd = string;
	}

	/**
	 * @return
	 */
	public String getErrMsg() {
		return errMsg;
	}

	/**
	 * @param string
	 */
	public void setErrMsg(String string) {
		errMsg = string;
	}

	/**
	 * @return
	 */
	public int getNextActionState() {
		return nextActionState;
	}

	/**
	 * @param i
	 */
	public void setNextActionState(int i) {
		nextActionState = i;
	}

	/**
	 * @return
	 */
	public String getEditSubTypeADefnd() {
		return editSubTypeADefnd;
	}

	/**
	 * @return
	 */
	public String getEditSubTypeBDefnd() {
		return editSubTypeBDefnd;
	}

	/**
	 * @return
	 */
	public String getEditSubTypeCDefnd() {
		return editSubTypeCDefnd;
	}

	/**
	 * @return
	 */
	public String getEditSubTypeDDefnd() {
		return editSubTypeDDefnd;
	}

	/**
	 * @param string
	 */
	public void setEditSubTypeADefnd(String string) {
		editSubTypeADefnd = string;
	}

	/**
	 * @param string
	 */
	public void setEditSubTypeBDefnd(String string) {
		editSubTypeBDefnd = string;
	}

	/**
	 * @param string
	 */
	public void setEditSubTypeCDefnd(String string) {
		editSubTypeCDefnd = string;
	}

	/**
	 * @param string
	 */
	public void setEditSubTypeDDefnd(String string) {
		editSubTypeDDefnd = string;
	}

	/**
	 * @return
	 */
	public int getPreviousActionState() {
		return previousActionState;
	}

	/**
	 * @param i
	 */
	public void setPreviousActionState(int i) {
		previousActionState = i;
	}

	/**
	 * @return
	 */
	public int getCancelActionState() {
		return cancelActionState;
	}

	/**
	 * @param i
	 */
	public void setCancelActionState(int i) {
		cancelActionState = i;
	}

	/**
	 * @return
	 */
	public int getSubtypeActionState() {
		return subtypeActionState;
	}

	/**
	 * @param i
	 */
	public void setSubtypeActionState(int i) {
		subtypeActionState = i;
	}

	/**
	 * @return
	 */
	public String getChkIssTypeIbmOnly() {
		return chkIssTypeIbmOnly;
	}

	/**
	 * @param string
	 */
	public void setChkIssTypeIbmOnly(String string) {
		chkIssTypeIbmOnly = string;
	}

	/**
	 * @return
	 */
	public ArrayList getNotifyList() {
		return notifyList;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevNotifyList() {
		return prevNotifyList;
	}

	/**
	 * @param list
	 */
	public void setNotifyList(ArrayList list) {
		notifyList = list;
	}

	/**
	 * @param list
	 */
	public void setPrevNotifyList(ArrayList list) {
		prevNotifyList = list;
	}

	/**
	 * @return
	 */
	public int getCq_seq_no() {
		return cq_seq_no;
	}

	/**
	 * @return
	 */
	public int getUsr_seq_no() {
		return usr_seq_no;
	}

	/**
	 * @param i
	 */
	public void setCq_seq_no(int i) {
		cq_seq_no = i;
	}

	/**
	 * @param i
	 */
	public void setUsr_seq_no(int i) {
		usr_seq_no = i;
	}

	/**
	 * @return
	 */
	public ArrayList getOwnerList() {
		return ownerList;
	}

	/**
	 * @param list
	 */
	public void setOwnerList(ArrayList list) {
		ownerList = list;
	}

	/**
	 * @return
	 */
	public String getCommentLogStr() {
		return commentLogStr;
	}

	/**
	 * @return
	 */
	public String getLastAction() {
		return lastAction;
	}

	/**
	 * @param string
	 */
	public void setCommentLogStr(String string) {
		commentLogStr = string;
	}

	/**
	 * @param string
	 */
	public void setLastAction(String string) {
		lastAction = string;
	}

	/**
	 * @return
	 */
	public String getIssueType() {
		return issueType;
	}

	/**
	 * @param string
	 */
	public void setIssueType(String string) {
		issueType = string;
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
	public String getSubmissionDate() {
		return submissionDate;
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
	public void setSubmissionDate(String string) {
		submissionDate = string;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevIssueTypeList() {
		return prevIssueTypeList;
	}

	/**
	 * @param list
	 */
	public void setPrevIssueTypeList(ArrayList list) {
		prevIssueTypeList = list;
	}

	/**
	 * @return
	 */
	public ArrayList getHistList() {
		return histList;
	}

	/**
	 * @param list
	 */
	public void setHistList(ArrayList list) {
		histList = list;
	}

	/**
	 * @return
	 */
	public String getInfoSrcFlag() {
		return infoSrcFlag;
	}

	/**
	 * @return
	 */
	public String getTxnStatusFlag() {
		return txnStatusFlag;
	}

	/**
	 * @param string
	 */
	public void setInfoSrcFlag(String string) {
		infoSrcFlag = string;
	}

	/**
	 * @param string
	 */
	public void setTxnStatusFlag(String string) {
		txnStatusFlag = string;
	}

	/**
	 * @return
	 */
	public HashMap getRtfMap() {
		return rtfMap;
	}

	/**
	 * @param map
	 */
	public void setRtfMap(HashMap map) {
		rtfMap = map;
	}

	/**
	 * @return
	 */
	public String getEtsIssuesType() {
		return etsIssuesType;
	}

	/**
	 * @param string
	 */
	public void setEtsIssuesType(String string) {
		etsIssuesType = string;
	}

	/**
	 * @return
	 */
	public ArrayList getExtUserList() {
		return extUserList;
	}

	/**
	 * @param list
	 */
	public void setExtUserList(ArrayList list) {
		extUserList = list;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevExtUserList() {
		return prevExtUserList;
	}

	/**
	 * @param list
	 */
	public void setPrevExtUserList(ArrayList list) {
		prevExtUserList = list;
	}

	/**
	 * @return
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param string
	 */
	public void setUserType(String string) {
		userType = string;
	}

	

	/**
	 * @return
	 */
	public boolean isUsrIssueSubscribe() {
		return usrIssueSubscribe;
	}

	/**
	 * @param b
	 */
	public void setUsrIssueSubscribe(boolean b) {
		usrIssueSubscribe = b;
	}

	/**
	 * @return
	 */
	public boolean isUsrIssTypSubscribe() {
		return usrIssTypSubscribe;
	}

	/**
	 * @param b
	 */
	public void setUsrIssTypSubscribe(boolean b) {
		usrIssTypSubscribe = b;
	}

	/**
	 * @return
	 */
	public String getIssueTypeId() {
		return issueTypeId;
	}

	/**
	 * @param string
	 */
	public void setIssueTypeId(String string) {
		issueTypeId = string;
	}

	/**
	 * @return
	 */
	public boolean isIssueTypIdActive() {
		return issueTypIdActive;
	}

	/**
	 * @param b
	 */
	public void setIssueTypIdActive(boolean b) {
		issueTypIdActive = b;
	}

	

	/**
	 * @return
	 */
	public String getRefNo() {
		return refNo;
	}

	/**
	 * @param string
	 */
	public void setRefNo(String string) {
		refNo = string;
	}

	/**
	 * @return
	 */
	public boolean isIssueSrcPMO() {
		return issueSrcPMO;
	}

	/**
	 * @param b
	 */
	public void setIssueSrcPMO(boolean b) {
		issueSrcPMO = b;
	}

	
	
	
	
	/**
	 * @return Returns the fieldC8Defnd.
	 */
	public String getFieldC8Defnd() {
		return fieldC8Defnd;
	}
	/**
	 * @param fieldC8Defnd The fieldC8Defnd to set.
	 */
	public void setFieldC8Defnd(String fieldC8Defnd) {
		this.fieldC8Defnd = fieldC8Defnd;
	}
	/**
	 * @return Returns the fieldC8DispName.
	 */
	public String getFieldC8DispName() {
		return fieldC8DispName;
	}
	/**
	 * @param fieldC8DispName The fieldC8DispName to set.
	 */
	public void setFieldC8DispName(String fieldC8DispName) {
		this.fieldC8DispName = fieldC8DispName;
	}
	/**
	 * @return Returns the fieldC8List.
	 */
	public ArrayList getFieldC8List() {
		return fieldC8List;
	}
	/**
	 * @param fieldC8List The fieldC8List to set.
	 */
	public void setFieldC8List(ArrayList fieldC8List) {
		this.fieldC8List = fieldC8List;
	}
	/**
	 * @return Returns the fieldC8RefName.
	 */
	public String getFieldC8RefName() {
		return fieldC8RefName;
	}
	/**
	 * @param fieldC8RefName The fieldC8RefName to set.
	 */
	public void setFieldC8RefName(String fieldC8RefName) {
		this.fieldC8RefName = fieldC8RefName;
	}
} //end of class
