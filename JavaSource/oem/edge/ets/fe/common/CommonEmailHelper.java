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

package oem.edge.ets.fe.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oem.edge.ets.fe.ETSDBUtils;

import org.apache.log4j.Logger;

/**
 * @author v2srikau
 */
public class CommonEmailHelper {
    
    public static final String IBM = "IBM ";
	private static final Logger m_pdLog = Logger.getLogger(CommonEmailHelper.class);

    /**
     * @param strAppName
     * @return
     */
    public static String getEmailFooter(String strAppName) {
	    StringBuffer strFooter = new StringBuffer();
	    String strCustConnect = "";
	    if ("Collaboration Center".equalsIgnoreCase(strAppName)) {
	        strCustConnect = "Customer Connect ";
	    }
	    strFooter.append("\n\n==============================================================");
	    strFooter.append("\nThis is a system generated email delivered by IBM " + strCustConnect + strAppName + ".");
	    strFooter.append("\n ");
	    strFooter.append("\nFor Help and Support, please contact the");
	    strFooter.append(" IBM Customer Connect Support Desk, 24 hours a day,");
	    strFooter.append(" 7 days a week.");
	    strFooter.append("\n ");
	    strFooter.append("\nNorth America: 1-888-220-3343");
	    strFooter.append("\nInternational: 1-802-769-3353");
	    strFooter.append("\ne-mail: econnect@us.ibm.com");
	    strFooter.append("\n ");
	    strFooter.append("\non-line: http://www.ibm.com/technologyconnect/");
	    strFooter.append("\n(log-on and click on \"Help and Support\" at the top");
	    strFooter.append(" of the left-hand navigation bar)");
	    strFooter.append("\n===================================================================================\n\n");

		return strFooter.toString();
	}

	/**
	 * This method wraps the String and pads that string with spaces. Creation
	 * date: (10/28/01 4:35:24 PM)
	 * 
	 * @return java.lang.String
	 * @param sInString
	 *            java.lang.String
	 * @exception java.lang.Exception
	 *                The exception description.
	 */
	public static String formatEmailSubject(String sInString) throws java.lang.Exception {

	    StringBuffer sOut = new StringBuffer("");

	    try {
	        if (sInString.length() > 80) {
	        	sOut.append(sInString.substring(0,76) + " ...");
	        } else {
	        	sOut.append(sInString);
	        }
	    } catch (Exception e) {
	        sOut.setLength(0);
	        sOut.append(sInString);
	    } finally {
	      
	    }
	    return sOut.toString();

	}

	/**
	 * Method getUserEmail.
	 * @param conn
	 * @param string
	 * @return String
	 */
	public static String getUserEmail(Connection conn, String sIRId) throws SQLException, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		String sUserEmail = "";

		try {

			sQuery.append("SELECT USER_EMAIL FROM AMT.USERS WHERE IR_USERID = ? with ur");

			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("ETSUtils::getUsersName()::QUERY : " + sQuery.toString());
			}

			stmt = conn.prepareStatement(sQuery.toString());
			stmt.setString(1,sIRId);

			rs = stmt.executeQuery();

			if (rs.next()) {
				sUserEmail = checkNull(rs.getString(1));
			} else {
				sUserEmail = "";
			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return sUserEmail;
	}

	/**
	 * Checks to see if the variable is null or not.
	 */
	public static String checkNull(String sInString) {

		String sOutString = "";

		if (sInString == null || sInString.trim().equals("")) {
			sOutString = "";
		} else {
			sOutString = sInString.trim();
		}

		return sOutString;

	}
}
