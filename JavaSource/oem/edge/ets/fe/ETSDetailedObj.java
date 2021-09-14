/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe;

/**
 * @author v2srikau
 */
public abstract class ETSDetailedObj {

	protected String m_strUserName;

	/**
	 * @param strUserName
	 */
	public void setUserName(String strUserName) {
		m_strUserName = strUserName;
	}
	
	/**
	 * @return
	 */
	public String getUserName() {
		return m_strUserName;		
	}
	
	/**
	 * @return
	 */
	public abstract String getUserId();
}
