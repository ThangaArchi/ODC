package oem.edge.ets.fe.ismgt.model;

import java.util.*;
import oem.edge.amt.*;

import oem.edge.ets.fe.ismgt.resources.*;
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
 * This object represents the model of data for submission data
 */
public class EtsIssStaticSubmFormDetails implements EtsIssueConstants {

	public static final String VERSION = "1.9";

	//display names//
	private String fieldC1DispName;
	private String fieldC2DispName;
	private String fieldC3DispName;
	private String fieldC4DispName;
	private String fieldC5DispName;
	private String fieldC6DispName;
	private String fieldC7DispName;

	//refe names//
	private String fieldC1RefName;
	private String fieldC2RefName;
	private String fieldC3RefName;
	private String fieldC4RefName;
	private String fieldC5RefName;
	private String fieldC6RefName;
	private String fieldC7RefName;

	//existing vals
	private ArrayList fieldC1ValList;
	private ArrayList fieldC2ValList;
	private ArrayList fieldC3ValList;
	private ArrayList fieldC4ValList;
	private ArrayList fieldC5ValList;
	private ArrayList fieldC6ValList;
	private ArrayList fieldC7ValList;
	
	//prev vals//
	private ArrayList prevFieldC1ValList;
	private ArrayList prevFieldC2ValList;
	private ArrayList prevFieldC3ValList;
	private ArrayList prevFieldC4ValList;
	private ArrayList prevFieldC5ValList;
	private ArrayList prevFieldC6ValList;
	private ArrayList prevFieldC7ValList;

	/**
	 * Constructor for EtsIssSubmFormDetails.
	 */
	public EtsIssStaticSubmFormDetails() {
		super();

	}

	/**
	 * Returns the fieldC1DispName.
	 * @return String
	 */
	public String getFieldC1DispName() {
		return fieldC1DispName;
	}

	/**
	 * Returns the fieldC1RefName.
	 * @return String
	 */
	public String getFieldC1RefName() {
		return fieldC1RefName;
	}

	/**
	 * Returns the fieldC1ValList.
	 * @return ArrayList
	 */
	public ArrayList getFieldC1ValList() {
		return fieldC1ValList;
	}

	/**
	 * Returns the fieldC2DispName.
	 * @return String
	 */
	public String getFieldC2DispName() {
		return fieldC2DispName;
	}

	/**
	 * Returns the fieldC2RefName.
	 * @return String
	 */
	public String getFieldC2RefName() {
		return fieldC2RefName;
	}

	/**
	 * Returns the fieldC2ValList.
	 * @return ArrayList
	 */
	public ArrayList getFieldC2ValList() {
		return fieldC2ValList;
	}

	/**
	 * Returns the fieldC3DispName.
	 * @return String
	 */
	public String getFieldC3DispName() {
		return fieldC3DispName;
	}

	/**
	 * Returns the fieldC3RefName.
	 * @return String
	 */
	public String getFieldC3RefName() {
		return fieldC3RefName;
	}

	/**
	 * Returns the fieldC3ValList.
	 * @return ArrayList
	 */
	public ArrayList getFieldC3ValList() {
		return fieldC3ValList;
	}

	/**
	 * Returns the fieldC4DispName.
	 * @return String
	 */
	public String getFieldC4DispName() {
		return fieldC4DispName;
	}

	/**
	 * Returns the fieldC4RefName.
	 * @return String
	 */
	public String getFieldC4RefName() {
		return fieldC4RefName;
	}

	/**
	 * Returns the fieldC4ValList.
	 * @return ArrayList
	 */
	public ArrayList getFieldC4ValList() {
		return fieldC4ValList;
	}

	/**
	 * Returns the fieldC5DispName.
	 * @return String
	 */
	public String getFieldC5DispName() {
		return fieldC5DispName;
	}

	/**
	 * Returns the fieldC5RefName.
	 * @return String
	 */
	public String getFieldC5RefName() {
		return fieldC5RefName;
	}

	/**
	 * Returns the fieldC5ValList.
	 * @return ArrayList
	 */
	public ArrayList getFieldC5ValList() {
		return fieldC5ValList;
	}

	/**
	 * Returns the fieldC6DispName.
	 * @return String
	 */
	public String getFieldC6DispName() {
		return fieldC6DispName;
	}

	/**
	 * Returns the fieldC6RefName.
	 * @return String
	 */
	public String getFieldC6RefName() {
		return fieldC6RefName;
	}

	/**
	 * Returns the fieldC6ValList.
	 * @return ArrayList
	 */
	public ArrayList getFieldC6ValList() {
		return fieldC6ValList;
	}

	/**
	 * Returns the fieldC7DispName.
	 * @return String
	 */
	public String getFieldC7DispName() {
		return fieldC7DispName;
	}

	/**
	 * Returns the fieldC7RefName.
	 * @return String
	 */
	public String getFieldC7RefName() {
		return fieldC7RefName;
	}

	/**
	 * Returns the fieldC7ValList.
	 * @return ArrayList
	 */
	public ArrayList getFieldC7ValList() {
		return fieldC7ValList;
	}

	/**
	 * Sets the fieldC1DispName.
	 * @param fieldC1DispName The fieldC1DispName to set
	 */
	public void setFieldC1DispName(String fieldC1DispName) {
		
		if(AmtCommonUtils.isResourceDefined(fieldC1DispName)) {
			
		this.fieldC1DispName = fieldC1DispName;
		
		}
		else {
			
			this.fieldC1DispName=DEFUALTSTDFIELDC1NAME;
		}
			
	}

	/**
	 * Sets the fieldC1RefName.
	 * @param fieldC1RefName The fieldC1RefName to set
	 */
	public void setFieldC1RefName(String fieldC1RefName) {
		this.fieldC1RefName = fieldC1RefName;
	}

	/**
	 * Sets the fieldC1ValList.
	 * @param fieldC1ValList The fieldC1ValList to set
	 */
	public void setFieldC1ValList(ArrayList fieldC1ValList) {
		this.fieldC1ValList = fieldC1ValList;
	}

	/**
	 * Sets the fieldC2DispName.
	 * @param fieldC2DispName The fieldC2DispName to set
	 */
	public void setFieldC2DispName(String fieldC2DispName) {
		
		if(AmtCommonUtils.isResourceDefined(fieldC2DispName)) {
			
		this.fieldC2DispName = fieldC2DispName;
		
		}
		
		else {
			
			this.fieldC2DispName = DEFUALTSTDFIELDC2NAME;
		}
	}

	/**
	 * Sets the fieldC2RefName.
	 * @param fieldC2RefName The fieldC2RefName to set
	 */
	public void setFieldC2RefName(String fieldC2RefName) {
		this.fieldC2RefName = fieldC2RefName;
	}

	/**
	 * Sets the fieldC2ValList.
	 * @param fieldC2ValList The fieldC2ValList to set
	 */
	public void setFieldC2ValList(ArrayList fieldC2ValList) {
		this.fieldC2ValList = fieldC2ValList;
	}

	/**
	 * Sets the fieldC3DispName.
	 * @param fieldC3DispName The fieldC3DispName to set
	 */
	public void setFieldC3DispName(String fieldC3DispName) {
		
		if(AmtCommonUtils.isResourceDefined(fieldC3DispName)) {
			
		this.fieldC3DispName = fieldC3DispName;
		
		}
		
		else {
			
			this.fieldC3DispName = DEFUALTSTDFIELDC3NAME;
			
		}
	}

	/**
	 * Sets the fieldC3RefName.
	 * @param fieldC3RefName The fieldC3RefName to set
	 */
	public void setFieldC3RefName(String fieldC3RefName) {
		this.fieldC3RefName = fieldC3RefName;
	}

	/**
	 * Sets the fieldC3ValList.
	 * @param fieldC3ValList The fieldC3ValList to set
	 */
	public void setFieldC3ValList(ArrayList fieldC3ValList) {
		this.fieldC3ValList = fieldC3ValList;
	}

	/**
	 * Sets the fieldC4DispName.
	 * @param fieldC4DispName The fieldC4DispName to set
	 */
	public void setFieldC4DispName(String fieldC4DispName) {
		
		if(AmtCommonUtils.isResourceDefined(fieldC4DispName)) {
			
		this.fieldC4DispName = fieldC4DispName;
		
		}
		
		else {
			
			this.fieldC4DispName = DEFUALTSTDFIELDC4NAME;
		}
	}

	/**
	 * Sets the fieldC4RefName.
	 * @param fieldC4RefName The fieldC4RefName to set
	 */
	public void setFieldC4RefName(String fieldC4RefName) {
		this.fieldC4RefName = fieldC4RefName;
	}

	/**
	 * Sets the fieldC4ValList.
	 * @param fieldC4ValList The fieldC4ValList to set
	 */
	public void setFieldC4ValList(ArrayList fieldC4ValList) {
		this.fieldC4ValList = fieldC4ValList;
	}

	/**
	 * Sets the fieldC5DispName.
	 * @param fieldC5DispName The fieldC5DispName to set
	 */
	public void setFieldC5DispName(String fieldC5DispName) {
		
		if(AmtCommonUtils.isResourceDefined(fieldC5DispName)) {
			
		this.fieldC5DispName = fieldC5DispName;
		
		}
		
		else {
			
			this.fieldC5DispName = DEFUALTSTDFIELDC5NAME;
		}
	}

	/**
	 * Sets the fieldC5RefName.
	 * @param fieldC5RefName The fieldC5RefName to set
	 */
	public void setFieldC5RefName(String fieldC5RefName) {
		this.fieldC5RefName = fieldC5RefName;
	}

	/**
	 * Sets the fieldC5ValList.
	 * @param fieldC5ValList The fieldC5ValList to set
	 */
	public void setFieldC5ValList(ArrayList fieldC5ValList) {
		this.fieldC5ValList = fieldC5ValList;
	}

	/**
	 * Sets the fieldC6DispName.
	 * @param fieldC6DispName The fieldC6DispName to set
	 */
	public void setFieldC6DispName(String fieldC6DispName) {
		
		if(AmtCommonUtils.isResourceDefined(fieldC6DispName)) {
			
		this.fieldC6DispName = fieldC6DispName;
		
		}
		
		else {
			
			this.fieldC6DispName = DEFUALTSTDFIELDC6NAME;
		}
	}

	/**
	 * Sets the fieldC6RefName.
	 * @param fieldC6RefName The fieldC6RefName to set
	 */
	public void setFieldC6RefName(String fieldC6RefName) {
		this.fieldC6RefName = fieldC6RefName;
	}

	/**
	 * Sets the fieldC6ValList.
	 * @param fieldC6ValList The fieldC6ValList to set
	 */
	public void setFieldC6ValList(ArrayList fieldC6ValList) {
		this.fieldC6ValList = fieldC6ValList;
	}

	/**
	 * Sets the fieldC7DispName.
	 * @param fieldC7DispName The fieldC7DispName to set
	 */
	public void setFieldC7DispName(String fieldC7DispName) {
		
		if(AmtCommonUtils.isResourceDefined(fieldC7DispName)) {
			
		this.fieldC7DispName = fieldC7DispName;
		
		}
		
		else {
		
		this.fieldC7DispName = DEFUALTSTDFIELDC7NAME;	
			
		}
	}

	/**
	 * Sets the fieldC7RefName.
	 * @param fieldC7RefName The fieldC7RefName to set
	 */
	public void setFieldC7RefName(String fieldC7RefName) {
		this.fieldC7RefName = fieldC7RefName;
	}

	/**
	 * Sets the fieldC7ValList.
	 * @param fieldC7ValList The fieldC7ValList to set
	 */
	public void setFieldC7ValList(ArrayList fieldC7ValList) {
		this.fieldC7ValList = fieldC7ValList;
	}

	/**
	 * Returns the prevFieldC1ValList.
	 * @return ArrayList
	 */
	public ArrayList getPrevFieldC1ValList() {
		return prevFieldC1ValList;
	}

	/**
	 * Returns the prevFieldC2ValList.
	 * @return ArrayList
	 */
	public ArrayList getPrevFieldC2ValList() {
		return prevFieldC2ValList;
	}

	/**
	 * Returns the prevFieldC3ValList.
	 * @return ArrayList
	 */
	public ArrayList getPrevFieldC3ValList() {
		return prevFieldC3ValList;
	}

	/**
	 * Returns the prevFieldC4ValList.
	 * @return ArrayList
	 */
	public ArrayList getPrevFieldC4ValList() {
		return prevFieldC4ValList;
	}

	/**
	 * Returns the prevFieldC5ValList.
	 * @return ArrayList
	 */
	public ArrayList getPrevFieldC5ValList() {
		return prevFieldC5ValList;
	}

	/**
	 * Returns the prevFieldC6ValList.
	 * @return ArrayList
	 */
	public ArrayList getPrevFieldC6ValList() {
		return prevFieldC6ValList;
	}

	/**
	 * Returns the prevFieldC7ValList.
	 * @return ArrayList
	 */
	public ArrayList getPrevFieldC7ValList() {
		return prevFieldC7ValList;
	}

	/**
	 * Sets the prevFieldC1ValList.
	 * @param prevFieldC1ValList The prevFieldC1ValList to set
	 */
	public void setPrevFieldC1ValList(ArrayList prevFieldC1ValList) {
		this.prevFieldC1ValList = prevFieldC1ValList;
	}

	/**
	 * Sets the prevFieldC2ValList.
	 * @param prevFieldC2ValList The prevFieldC2ValList to set
	 */
	public void setPrevFieldC2ValList(ArrayList prevFieldC2ValList) {
		this.prevFieldC2ValList = prevFieldC2ValList;
	}

	/**
	 * Sets the prevFieldC3ValList.
	 * @param prevFieldC3ValList The prevFieldC3ValList to set
	 */
	public void setPrevFieldC3ValList(ArrayList prevFieldC3ValList) {
		this.prevFieldC3ValList = prevFieldC3ValList;
	}

	/**
	 * Sets the prevFieldC4ValList.
	 * @param prevFieldC4ValList The prevFieldC4ValList to set
	 */
	public void setPrevFieldC4ValList(ArrayList prevFieldC4ValList) {
		this.prevFieldC4ValList = prevFieldC4ValList;
	}

	/**
	 * Sets the prevFieldC5ValList.
	 * @param prevFieldC5ValList The prevFieldC5ValList to set
	 */
	public void setPrevFieldC5ValList(ArrayList prevFieldC5ValList) {
		this.prevFieldC5ValList = prevFieldC5ValList;
	}

	/**
	 * Sets the prevFieldC6ValList.
	 * @param prevFieldC6ValList The prevFieldC6ValList to set
	 */
	public void setPrevFieldC6ValList(ArrayList prevFieldC6ValList) {
		this.prevFieldC6ValList = prevFieldC6ValList;
	}

	/**
	 * Sets the prevFieldC7ValList.
	 * @param prevFieldC7ValList The prevFieldC7ValList to set
	 */
	public void setPrevFieldC7ValList(ArrayList prevFieldC7ValList) {
		this.prevFieldC7ValList = prevFieldC7ValList;
	}

} //end of class

