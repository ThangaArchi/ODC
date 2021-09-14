package oem.edge.ets_pmo.datastore.document;
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
public class DocAttributeHandling {
	private static String CLASS_VERSION = "4.5.1";
public static int DOCUMENTFOLDER = 1;
public static int DOCUMENT = 2;

public static String ATTACHMENT = "ATTACHMENT";
public static String ATTACHEMENT = "ATTACHEMENT";
public static String SUMMARY = "SUMMARY";
public static String BLOB_DATA = "BLOB_DATA";
public static String REVISION = "REVISION";
public static String CREATION_DATE = "CREATION_DATE";
public static String LAST_CHECKIN = "LAST_CHECKIN"; 
public static String COMPRESSED_SIZE = "COMPRESSED_SIZE";
public static String DOCUMENT_SIZE = "DOCUMENT_SIZE";
public static String ELEMENT_NAME	= "ELEMENT_NAME";

public final static int ADD_ATTACHMENT = 32;
public final static int ADD_SUMMARY = 33;
public final static int ADD_BLOB_DATA = 34;
public final static int ADD_REVISION = 35;
public final static int ADD_CREATION_DATE = 36;
public final static int ADD_LAST_CHECKIN = 37;
public final static int ADD_COMPRESSED_SIZE = 38;
public final static int ADD_DOCUMENT_SIZE = 39;
public final static int ADD_ELEMENT_NAME = 40;


	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}


