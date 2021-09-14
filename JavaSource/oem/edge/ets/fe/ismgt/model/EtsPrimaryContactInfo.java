package oem.edge.ets.fe.ismgt.model;

import java.io.*;
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
 * This class is the bean to represent attributes of Primary contact
 * for the given project id, attributes include Fullname, email and Phone
 */
public class EtsPrimaryContactInfo extends EtsIssUserDetailsAbsBean implements Serializable{
	
	public static final String VERSION = "1.10";
	

	/**
	 * Constructor for EtsPrimaryContactInfo.
	 */
	public EtsPrimaryContactInfo() {
		super();
	}


}

