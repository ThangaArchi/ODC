package oem.edge.ets_pmo.datastore.util;
import oem.edge.ets_pmo.util.*;
import org.apache.log4j.Logger;
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

/**
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RTFData {
private static String CLASS_VERSION = "4.5.1";
Base64Decoder bd = null;
private String name;
private String value;
private String aliasName;
private byte[] base64Value;
private int rank;

// Not using anywhere other than the place where 
// I am trying to extract the data for testing.
// I am using "private String name" too along with these ones
private String id;
private String BlobData;

static Logger logger = Logger.getLogger(RTFData.class);
public RTFData(String name, String aliasName, String value, int rank){
		this.name = name;
		this.aliasName = aliasName;
		this.value =value;
		this.rank = rank;	
		
}
public RTFData(){}
/**
 * Returns the name.
 * @return String
 */
public String getName() {
	return name;

}
/**
 * Returns the rank.
 * @return String
 */
public int getRank() {
	return rank;
}

/**
 * Returns the value.
 * @return String
 */
public String getValue() {
	return value;
}

/**
 * Sets the name.
 * @param name The name to set
 */
public void setName(String name) {
	this.name = name;
}

/**
 * Sets the rank.
 * @param rank The rank to set
 */
public void setRank(int rank) {
	this.rank = rank;
}

/**
 * Sets the value.
 * @param value The value to set
 */
public void setValue(String value) {
	//this.value = value;
	/*
				 * Communication probs with SystemCorp. 
				 * They are sending values in plain text instead 
				 * of RTFs which was the agreement.
				 * Need to change the code here.
				 * 
				 * After changing the code. They are back to original
				 * RTF handling process
				 */
	if(value == null){
		this.value = null;
		base64Value = null;
		return;	
	}
	/* for SAX parser, decoding is done in parsing
	if (bd==null)
		bd = new Base64Decoder();
	this.value = bd.decode(value);
	*/
	this.value=value;
	base64Value = this.value.getBytes();
	
	
}

/**
 * Returns the base64Value.
 * @return byte[]
 */
public byte[] getBase64Value() {
	return base64Value;
}

/**
 * Returns the blobData.
 * @return String
 */
public String getBlobData() {
	return BlobData;
}

/**
 * Returns the id.
 * @return String
 */
public String getId() {
	return id;
}

/**
 * Sets the blobData.
 * @param blobData The blobData to set
 */
public void setBlobData(String blobData) {
	BlobData = blobData;
}

/**
 * Sets the id.
 * @param id The id to set
 */
public void setId(String id) {
	this.id = id;
}

/**
 * Returns the aliasName.
 * @return String
 */
public String getAliasName() {
	return aliasName;
}

/**
 * Sets the aliasName.
 * @param aliasName The aliasName to set
 */
public void setAliasName(String aliasName) {
	this.aliasName = aliasName;
}

/**
 * @return
 */
public static String getCLASS_VERSION() {
	return CLASS_VERSION;
}

}
