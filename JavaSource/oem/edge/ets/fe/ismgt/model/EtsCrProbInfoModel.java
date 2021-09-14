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
public class EtsCrProbInfoModel {

	public static final String VERSION = "1.19.1.19";

	//	core problem info vars
	private String applnId;
	private String etsId;
	private String pmoId;
	private String pmoProjectId;
	private String parentPmoId;

	//key params
	private int refNo;
	private String infoSrcFlag;
	private String crType;

	//	problem creator//
	private String probCreator;
	private Timestamp creationDate;
	private String creationDateStr;

	//submitter profile
	private String custName;
	private String custEmail;
	private String custPhone;
	private String custCompany;

	//
	private String stateAction;

	//
	private String probClass; //is always Defect from 441 onwards
	private String probTitle;
	private String probDesc;
	private String commFromCust; //comm from cust

	//
	private ArrayList probSevList = new ArrayList();
	private ArrayList prevProbSevList = new ArrayList();

	//
	private String ownerIrId;
	private String ownerName;

	//	fields for last userid and last updated timestamp
	private String lastUserId;
	private Timestamp lastTimeStamp;

	private int previousActionState; //previous state
	private int currentActionState; //action state
	private int nextActionState; //next state
	private int cancelActionState; //cancel action state

	//
	private String errMsg;
	
	//
	private ArrayList rtfList;
	private HashMap rtfMap;
	private String statusFlag;
	
	//
	private String probState;
	
	
	/**
	 * 
	 */
	public EtsCrProbInfoModel() {
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
	public Timestamp getCreationDate() {
		return creationDate;
	}

	/**
	 * @return
	 */
	public String getCrType() {
		return crType;
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
	public String getEtsId() {
		return etsId;
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
	public String getOwnerIrId() {
		return ownerIrId;
	}

	/**
	 * @return
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @return
	 */
	public String getParentPmoId() {
		return parentPmoId;
	}

	/**
	 * @return
	 */
	public String getPmoId() {
		return pmoId;
	}

	/**
	 * @return
	 */
	public String getPmoProjectId() {
		return pmoProjectId;
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
	public String getProbTitle() {
		return probTitle;
	}

	/**
	 * @return
	 */
	public int getRefNo() {
		return refNo;
	}

	/**
	 * @return
	 */
	public String getStateAction() {
		return stateAction;
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
	 * @param timestamp
	 */
	public void setCreationDate(Timestamp timestamp) {
		creationDate = timestamp;
	}

	/**
	 * @param string
	 */
	public void setCrType(String string) {
		crType = string;
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
	public void setEtsId(String string) {
		etsId = string;
	}

	/**
	 * @param string
	 */
	public void setInfoSrcFlag(String string) {
		infoSrcFlag = string;
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
	 * @param string
	 */
	public void setOwnerIrId(String string) {
		ownerIrId = string;
	}

	/**
	 * @param string
	 */
	public void setOwnerName(String string) {
		ownerName = string;
	}

	/**
	 * @param string
	 */
	public void setParentPmoId(String string) {
		parentPmoId = string;
	}

	/**
	 * @param string
	 */
	public void setPmoId(String string) {
		pmoId = string;
	}

	/**
	 * @param string
	 */
	public void setPmoProjectId(String string) {
		pmoProjectId = string;
	}

	/**
	 * @param list
	 */
	public void setPrevProbSevList(ArrayList list) {
		prevProbSevList = list;
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
	public void setProbTitle(String string) {
		probTitle = string;
	}

	/**
	 * @param i
	 */
	public void setRefNo(int i) {
		refNo = i;
	}

	/**
	 * @param string
	 */
	public void setStateAction(String string) {
		stateAction = string;
	}

	/**
	 * @return
	 */
	public int getCancelActionState() {
		return cancelActionState;
	}

	/**
	 * @return
	 */
	public int getCurrentActionState() {
		return currentActionState;
	}

	/**
	 * @return
	 */
	public int getNextActionState() {
		return nextActionState;
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
	public void setCancelActionState(int i) {
		cancelActionState = i;
	}

	/**
	 * @param i
	 */
	public void setCurrentActionState(int i) {
		currentActionState = i;
	}

	/**
	 * @param i
	 */
	public void setNextActionState(int i) {
		nextActionState = i;
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
	public ArrayList getRtfList() {
		return rtfList;
	}

	/**
	 * @param list
	 */
	public void setRtfList(ArrayList list) {
		rtfList = list;
	}

	/**
	 * @return
	 */
	public String getStatusFlag() {
		return statusFlag;
	}

	/**
	 * @param string
	 */
	public void setStatusFlag(String string) {
		statusFlag = string;
	}

	/**
	 * @return
	 */
	public String getCreationDateStr() {
		return creationDateStr;
	}

	/**
	 * @param string
	 */
	public void setCreationDateStr(String string) {
		creationDateStr = string;
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
	public String getProbState() {
		return probState;
	}

	/**
	 * @param string
	 */
	public void setProbState(String string) {
		probState = string;
	}

}
