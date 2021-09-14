package oem.edge.ets.fe.ismgt.model;

import java.util.*;
import oem.edge.amt.*;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
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

/**
 * @author v2phani
 * This bean represnet the model for dyanmic sub type forms for issue type
 */
public class EtsIssDynSubmFormDetails implements EtsIssueConstants{

	public static final String VERSION = "1.9";

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
	
	//values
	private ArrayList subTypeAValList;
	private ArrayList subTypeBValList;
	private ArrayList subTypeCValList;
	private ArrayList subTypeDValList;
	
	//prev values//
	private ArrayList prevSubTypeAValList;
	private ArrayList prevSubTypeBValList;
	private ArrayList prevSubTypeCValList;
	private ArrayList prevSubTypeDValList;
	
	

	/**
	 * Constructor for EtsIssDynSubmFormDetails.
	 */
	public EtsIssDynSubmFormDetails() {
		super();
	}

	
	
	/**
	 * Returns the prevSubTypeAValList.
	 * @return ArrayList
	 */
	public ArrayList getPrevSubTypeAValList() {
		return prevSubTypeAValList;
	}

	/**
	 * Returns the prevSubTypeBValList.
	 * @return ArrayList
	 */
	public ArrayList getPrevSubTypeBValList() {
		return prevSubTypeBValList;
	}

	/**
	 * Returns the prevSubTypeCValList.
	 * @return ArrayList
	 */
	public ArrayList getPrevSubTypeCValList() {
		return prevSubTypeCValList;
	}

	/**
	 * Returns the prevSubTypeDValList.
	 * @return ArrayList
	 */
	public ArrayList getPrevSubTypeDValList() {
		return prevSubTypeDValList;
	}

	/**
	 * Returns the subTypeADispName.
	 * @return String
	 */
	public String getSubTypeADispName() {
		return subTypeADispName;
	}

	/**
	 * Returns the subTypeARefName.
	 * @return String
	 */
	public String getSubTypeARefName() {
		return subTypeARefName;
	}

	/**
	 * Returns the subTypeAValList.
	 * @return ArrayList
	 */
	public ArrayList getSubTypeAValList() {
		return subTypeAValList;
	}

	/**
	 * Returns the subTypeBDispName.
	 * @return String
	 */
	public String getSubTypeBDispName() {
		return subTypeBDispName;
	}

	/**
	 * Returns the subTypeBRefName.
	 * @return String
	 */
	public String getSubTypeBRefName() {
		return subTypeBRefName;
	}

	/**
	 * Returns the subTypeBValList.
	 * @return ArrayList
	 */
	public ArrayList getSubTypeBValList() {
		return subTypeBValList;
	}

	/**
	 * Returns the subTypeCDispName.
	 * @return String
	 */
	public String getSubTypeCDispName() {
		return subTypeCDispName;
	}

	/**
	 * Returns the subTypeCRefName.
	 * @return String
	 */
	public String getSubTypeCRefName() {
		return subTypeCRefName;
	}

	/**
	 * Returns the subTypeCValList.
	 * @return ArrayList
	 */
	public ArrayList getSubTypeCValList() {
		return subTypeCValList;
	}

	/**
	 * Returns the subTypeDRefName.
	 * @return String
	 */
	public String getSubTypeDRefName() {
		return subTypeDRefName;
	}

	/**
	 * Returns the subTypeDValList.
	 * @return ArrayList
	 */
	public ArrayList getSubTypeDValList() {
		return subTypeDValList;
	}

	

	/**
	 * Sets the prevSubTypeAValList.
	 * @param prevSubTypeAValList The prevSubTypeAValList to set
	 */
	public void setPrevSubTypeAValList(ArrayList prevSubTypeAValList) {
		this.prevSubTypeAValList = prevSubTypeAValList;
	}

	/**
	 * Sets the prevSubTypeBValList.
	 * @param prevSubTypeBValList The prevSubTypeBValList to set
	 */
	public void setPrevSubTypeBValList(ArrayList prevSubTypeBValList) {
		this.prevSubTypeBValList = prevSubTypeBValList;
	}

	/**
	 * Sets the prevSubTypeCValList.
	 * @param prevSubTypeCValList The prevSubTypeCValList to set
	 */
	public void setPrevSubTypeCValList(ArrayList prevSubTypeCValList) {
		this.prevSubTypeCValList = prevSubTypeCValList;
	}

	/**
	 * Sets the prevSubTypeDValList.
	 * @param prevSubTypeDValList The prevSubTypeDValList to set
	 */
	public void setPrevSubTypeDValList(ArrayList prevSubTypeDValList) {
		this.prevSubTypeDValList = prevSubTypeDValList;
	}

	/**
	 * Sets the subTypeADispName.
	 * @param subTypeADispName The subTypeADispName to set
	 */
	public void setSubTypeADispName(String subTypeADispName) {
		
		if(AmtCommonUtils.isResourceDefined(subTypeADispName)) {
			
		this.subTypeADispName = subTypeADispName;
		
		}
		
		else {
			
			this.subTypeADispName = DEFUALTSTDSUBTYPE_A;
		}
	}

	/**
	 * Sets the subTypeARefName.
	 * @param subTypeARefName The subTypeARefName to set
	 */
	public void setSubTypeARefName(String subTypeARefName) {
		this.subTypeARefName = subTypeARefName;
	}

	/**
	 * Sets the subTypeAValList.
	 * @param subTypeAValList The subTypeAValList to set
	 */
	public void setSubTypeAValList(ArrayList subTypeAValList) {
		this.subTypeAValList = subTypeAValList;
	}

	/**
	 * Sets the subTypeBDispName.
	 * @param subTypeBDispName The subTypeBDispName to set
	 */
	public void setSubTypeBDispName(String subTypeBDispName) {
		
		if(AmtCommonUtils.isResourceDefined(subTypeBDispName)) {
			
		this.subTypeBDispName = subTypeBDispName;
		
		}
		
		else {
			
			this.subTypeBDispName = DEFUALTSTDSUBTYPE_B;
		}
	}

	/**
	 * Sets the subTypeBRefName.
	 * @param subTypeBRefName The subTypeBRefName to set
	 */
	public void setSubTypeBRefName(String subTypeBRefName) {
		this.subTypeBRefName = subTypeBRefName;
	}

	/**
	 * Sets the subTypeBValList.
	 * @param subTypeBValList The subTypeBValList to set
	 */
	public void setSubTypeBValList(ArrayList subTypeBValList) {
		this.subTypeBValList = subTypeBValList;
	}

	/**
	 * Sets the subTypeCDispName.
	 * @param subTypeCDispName The subTypeCDispName to set
	 */
	public void setSubTypeCDispName(String subTypeCDispName) {
		
		if(AmtCommonUtils.isResourceDefined(subTypeCDispName)) {
			
		this.subTypeCDispName = subTypeCDispName;
		
		}
		
		else {
		
		this.subTypeCDispName = DEFUALTSTDSUBTYPE_C;	
			
		}
	}

	/**
	 * Sets the subTypeCRefName.
	 * @param subTypeCRefName The subTypeCRefName to set
	 */
	public void setSubTypeCRefName(String subTypeCRefName) {
		this.subTypeCRefName = subTypeCRefName;
	}

	/**
	 * Sets the subTypeCValList.
	 * @param subTypeCValList The subTypeCValList to set
	 */
	public void setSubTypeCValList(ArrayList subTypeCValList) {
		this.subTypeCValList = subTypeCValList;
	}

	/**
	 * Sets the subTypeDRefName.
	 * @param subTypeDRefName The subTypeDRefName to set
	 */
	public void setSubTypeDRefName(String subTypeDRefName) {
		this.subTypeDRefName = subTypeDRefName;
	}

	/**
	 * Sets the subTypeDValList.
	 * @param subTypeDValList The subTypeDValList to set
	 */
	public void setSubTypeDValList(ArrayList subTypeDValList) {
		this.subTypeDValList = subTypeDValList;
	}

	

	/**
	 * Returns the subTypeDDispName.
	 * @return String
	 */
	public String getSubTypeDDispName() {
		return subTypeDDispName;
	}

	/**
	 * Sets the subTypeDDispName.
	 * @param subTypeDDispName The subTypeDDispName to set
	 */
	public void setSubTypeDDispName(String subTypeDDispName) {
		
		if(AmtCommonUtils.isResourceDefined(subTypeDDispName)) {
			
		this.subTypeDDispName = subTypeDDispName;
		
		}
		
		else {
			
		this.subTypeDDispName = DEFUALTSTDSUBTYPE_D;
			
		}
	}

}

