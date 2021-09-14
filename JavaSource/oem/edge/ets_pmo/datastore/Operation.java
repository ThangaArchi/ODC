package oem.edge.ets_pmo.datastore;

import oem.edge.ets_pmo.datastore.project.*;
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
public class Operation {
private static String CLASS_VERSION = "4.5.1";
private String OperationType;
private String UserID; // User responsible for the operation
private Project projObject;
private String projObjectType;
/**
 * Returns the operationType.
 * @return String
 */
public String getOperationType() {
	return OperationType;
}

/**
 * Returns the projObject.
 * @return Project
 */
public Project getProjObject() {
	return projObject;
}

/**
 * Returns the userID.
 * @return String
 */
public String getUserID() {
	return UserID;
}

/**
 * Sets the operationType.
 * @param operationType The operationType to set
 */
public void setOperationType(String operationType) {
	OperationType = operationType;
}

/**
 * Sets the projObject.
 * @param projObject The projObject to set
 */
public void setProjObject(Project projObject) {
	this.projObject = projObject;
}

/**
 * Sets the userID.
 * @param userID The userID to set
 */
public void setUserID(String userID) {
	UserID = userID;
}

/**
 * Returns the projObjectType.
 * @return String
 */
public String getProjObjectType() {
	return projObjectType;
}

/**
 * Sets the projObjectType.
 * @param projObjectType The projObjectType to set
 */
public void setProjObjectType(String projObjectType) {
	this.projObjectType = projObjectType;
}

/**
 * @return
 */
public static String getCLASS_VERSION() {
	return CLASS_VERSION;
}

}
