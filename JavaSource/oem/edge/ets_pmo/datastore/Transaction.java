package oem.edge.ets_pmo.datastore;
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
public class Transaction {
private static String CLASS_VERSION = "4.5.1";	
private String transactionVersion;
private String transactionID;
private String source;
private String destination;
private String repositoryApp;

private Operation operation = null;

public Transaction(){
	this.transactionVersion = null;
	this.transactionID	= null;
	this.source		= null;
	this.destination	= null;
	this.repositoryApp	= null;
	this.operation		= null;
}

public Transaction(String transactionV, String transactionID, String source, String destination, String repApp, Operation operation){
	this.transactionVersion = transactionV;
	this.transactionID	= transactionID;
	this.source		= source;
	this.destination	= destination;
	this.repositoryApp	= repApp;
	this.operation		= operation;
}
/**
 * Returns the destination
 * @return String
 */
public String getDestination() {
	return destination;
}

/**
 * Returns the operation.
 * @return Operation
 */
public Operation getOperation() {
	return operation;
}

/**
 * Returns the repositoryApp.
 * @return String
 */
public String getRepositoryApp() {
	return repositoryApp;
}

/**
 * Returns the source.
 * @return String
 */
public String getSource() {
	return source;
}

/**
 * Returns the transactionID.
 * @return String
 */
public String getTransactionID() {
	return transactionID;
}

/**
 * Sets the destination.
 * @param destination The destination to set
 */
public void setDestination(String destination) {
	this.destination = destination;
}

/**
 * Sets the operation.
 * @param operation The operation to set
 */
public void setOperation(Operation operation) {
	this.operation = operation;
}

/**
 * Sets the repositoryApp.
 * @param repositoryApp The repositoryApp to set
 */
public void setRepositoryApp(String repositoryApp) {
	this.repositoryApp = repositoryApp;
}

/**
 * Sets the source.
 * @param source The source to set
 */
public void setSource(String source) {
	this.source = source;
}

/**
 * Sets the transactionID.
 * @param transactionID The transactionID to set
 */
public void setTransactionID(String transactionID) {
	this.transactionID = transactionID;
}

/**
 * Returns the transactionVersion.
 * @return String
 */
public String getTransactionVersion() {
	return transactionVersion;
}

/**
 * Sets the transactionVersion.
 * @param transactionVersion The transactionVersion to set
 */
public void setTransactionVersion(String transactionVersion) {
	this.transactionVersion = transactionVersion;
}

/**
 * @return
 */
public static String getCLASS_VERSION() {
	return CLASS_VERSION;
}

}
