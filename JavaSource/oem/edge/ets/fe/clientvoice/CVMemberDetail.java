/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2006                                     */
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


/*
 * Created on Mar 9, 2006
 * Created by v2sathis
 * 
 * @author v2sathis
 */
package oem.edge.ets.fe.clientvoice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author v2sathis
 *
 */
public class CVMemberDetail {
	
	public static final String VERSION = "1.1";
	
	static Log log = LogFactory.getLog(CVMemberDetail.class);
	
	private String name = null;
	private String type = null;
	private String step = null;
	
	public CVMemberDetail(String cvName, String cvType, String cvStep) {
		this.name = cvName;
		this.type = cvType;
		this.step = cvStep;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return Returns the step.
	 */
	public String getStep() {
		return step;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
}
