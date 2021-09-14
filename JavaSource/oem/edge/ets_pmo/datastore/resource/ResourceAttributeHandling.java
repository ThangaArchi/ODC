package oem.edge.ets_pmo.datastore.resource;
import oem.edge.ets_pmo.datastore.AttributeHandling;
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
public class ResourceAttributeHandling extends AttributeHandling{
	private static String CLASS_VERSION = "4.5.1";
/* ************
 * Inherits ELEMENT_NAME 
 * ***********/
	public static String SECURITY_ID = "SECURITY_ID";
	public static boolean IsSECURITY_ID_RANK = true;
	public static String LOGON_NAME = "LOGON_NAME";
	public static boolean IsLOGON_NAME_RANK = false;
	public static String COMPANY_NAME = "COMPANY_NAME";
	public static boolean IsCOMPANY_NAME_RANK = false;
	public static String RESOURCE_NAME = "RESOURCE_NAME";
	public static boolean IsRESOURCE_NAME = false;
	
public final static int ADD_SECURITY_ID= 1050;
public final static int ADD_LOGON_NAME = 1051;
public final static int ADD_COMPANY_NAME = 1052;
public final static int ADD_RESOURCE_NAME = 1053;
	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
