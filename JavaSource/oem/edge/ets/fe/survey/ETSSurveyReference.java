/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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
 * Created on Sep 25, 2005
 * @author v2sathis@us.ibm.com
 */
 
package oem.edge.ets.fe.survey;

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 */
public class ETSSurveyReference {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2005";
	private static final String CLASS_VERSION = "1.1";

	private static Log logger = EtsLogger.getLogger(ETSSurveyReference.class);	
	
	private String Year;
	private String Reference;
	private String Key;
	private String Description;
	
	/**
	 * @return
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * @return
	 */
	public String getKey() {
		return Key;
	}

	/**
	 * @return
	 */
	public String getReference() {
		return Reference;
	}

	/**
	 * @return
	 */
	public String getYear() {
		return Year;
	}

	/**
	 * @param string
	 */
	public void setDescription(String string) {
		Description = string;
	}

	/**
	 * @param string
	 */
	public void setKey(String string) {
		Key = string;
	}

	/**
	 * @param string
	 */
	public void setReference(String string) {
		Reference = string;
	}

	/**
	 * @param string
	 */
	public void setYear(String string) {
		Year = string;
	}

}
