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


/*
 * Created on Jan 24, 2005
 */

package oem.edge.ets.fe.self;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSelfExpectation {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";

	private int ExpectId = 0;
	private String ExpectCode = "";
	private String ExpectDesc = "";

	public ETSSelfExpectation() {
		super();
	}
	/**
	 * @return
	 */
	public String getExpectDesc() {
		return ExpectDesc;
	}

	/**
	 * @return
	 */
	public int getExpectId() {
		return ExpectId;
	}

	/**
	 * @param string
	 */
	public void setExpectDesc(String string) {
		ExpectDesc = string;
	}

	/**
	 * @param string
	 */
	public void setExpectId(int iExpectId) {
		ExpectId = iExpectId;
	}

	/**
	 * @return
	 */
	public String getExpectCode() {
		return ExpectCode;
	}

	/**
	 * @param string
	 */
	public void setExpectCode(String string) {
		ExpectCode = string;
	}

}
