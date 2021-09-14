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

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DatesArithmatic;
import oem.edge.common.Global;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.UserRole;
import oem.edge.ets.fe.aic.AICProj;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author v2sathis
 *
 */
public class ETSUtils {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.26.2.16";

	private static Log logger = LogFactory.getLog(ETSUtils.class);
	
	private static boolean serverResponse;




	/**
	 * This method wraps the String and pads that string with spaces.
	 * Creation date: (10/28/01 4:35:24 PM)
	 * @return java.lang.String
	 * @param sInString java.lang.String
	 * @exception java.lang.Exception The exception description.
	 */
	public static String formatEmailStr(String sInString) throws java.lang.Exception {

	    StringBuffer sOut = new StringBuffer();
	    boolean bBreakFlag = false;
	    boolean bNewLine = false;

	    try {

	    	byte sTemp[] = sInString.getBytes();

	    	for (int i = 0; i < sTemp.length; i++) {
	    		if (sTemp[i] == (byte) '\n' || sTemp[i] == (byte) '\r') {
	    			sTemp[i] = (byte) ' ';
	    		}
	    	}

	    	sInString = new String(sTemp);

            if (sInString.length() > 48) {
                for (int i = 0; i < sInString.length(); i++) {
                    if (i % 40 == 0) {
                        if (i > 39) {
                            bBreakFlag = true;
                        }
                    }
                    if (bBreakFlag) {
                        if (sInString.substring(i, i + 1).equals(",") || sInString.substring(i, i + 1).equals(" ") || sInString.substring(i, i + 1).equals(";")) {
                            sOut.append(sInString.substring(i, i + 1));
                            sOut.append("\n                  ");
                            bBreakFlag = false;
                            bNewLine = true;
                        } else {
                       		sOut.append(sInString.substring(i, i + 1));
                        }
                    } else {
                    	if (bNewLine) {
                            sOut.append(sInString.substring(i, i + 1).trim());
                            bNewLine = false;
                    	} else {
                    		sOut.append(sInString.substring(i, i + 1));
                    	}
                    }
                }
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


	public static String getParameter(HttpServletRequest req, String key) {

		String value = req.getParameter(key);

		if (value == null) {
			return "";
		} else {
			return value;
		}
	}

	public static String getUsersName(Connection con, String sIRId) throws SQLException, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		String sUserName = "";

		try {

			sQuery.append("SELECT LTRIM(RTRIM(USER_FNAME)) || ' ' || LTRIM(RTRIM(USER_LNAME)) FROM AMT.USERS WHERE IR_USERID = ? with ur");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSUtils::getUsersName()::QUERY : " + sQuery.toString());
			}

			stmt = con.prepareStatement(sQuery.toString());
			stmt.setString(1,sIRId);

			rs = stmt.executeQuery();

			if (rs.next()) {
				sUserName = ETSUtils.checkNull(rs.getString(1));
			} else {
				sUserName = sIRId;
			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return sUserName;

	}

	public static Hashtable getServletParameters(javax.servlet.http.HttpServletRequest req) throws java.lang.Exception {
		Hashtable hs = new Hashtable();
		Enumeration e = req.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			hs.put(name, req.getParameter(name));
		}
		return hs;
	}


	public static String getIRProfileURL(Connection con) throws Exception {

		String sLink = "";
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {


			sb.append("SELECT LINK_URL FROM AMT.LEFTNAV WHERE LINK_ID='DS0001' with ur");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sb.toString());

			while (rs.next()) {
				sLink = rs.getString("LINK_URL");
				if (sLink == null) {
					sLink = "";
				} else {
					sLink = sLink.trim();
				}
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return sLink;
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

			if (logger.isDebugEnabled()) {
				logger.debug("ETSUtils::getUsersName()::QUERY : " + sQuery.toString());
			}

			stmt = conn.prepareStatement(sQuery.toString());
			stmt.setString(1,sIRId);

			rs = stmt.executeQuery();

			if (rs.next()) {
				sUserEmail = ETSUtils.checkNull(rs.getString(1));
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
	 * Method getUserEmail.
	 * @param conn
	 * @param string
	 * @return String
	 */
	public static String getUserPhone(Connection conn, String sIRId) throws SQLException, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		String sUserPhone = "";

		try {

			sQuery.append("SELECT USER_PHONE FROM AMT.USERS WHERE IR_USERID = ? with ur");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSUtils::getUserPhone()::QUERY : " + sQuery.toString());
			}

			stmt = conn.prepareStatement(sQuery.toString());
			stmt.setString(1,sIRId);

			rs = stmt.executeQuery();

			if (rs.next()) {
				sUserPhone = ETSUtils.checkNull(rs.getString(1));
			} else {
				sUserPhone = "";
			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return sUserPhone;
	}

	/**
	 * Send mail...
	 */
	public static boolean sendEMail(String from, String to, String sCC, String host, String sMessage, String Subject, String reply) throws Exception {
		
		
		boolean debug = false;
		long sleepTime = 1000 * 5; // sleep for ...sleepTime
		boolean mailSent = false;

		// create some properties and get the default Session
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		//javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);
		javax.mail.Session session = javax.mail.Session.getInstance(props, null);
		session.setDebug(debug);

		try {

			InternetAddress[] tolist = InternetAddress.parse(to, false);
			InternetAddress[] cclist = InternetAddress.parse(sCC, false);

			// create a message
			javax.mail.Message msg = new javax.mail.internet.MimeMessage(session);
			//msg.addHeader("X-Priority", "1");
			//msg.addHeader("Importance", "High");
			msg.setFrom(new javax.mail.internet.InternetAddress(from));

			//javax.mail.internet.InternetAddress[] address = {new javax.mail.internet.InternetAddress(to)};
			msg.setRecipients(javax.mail.Message.RecipientType.TO, tolist);

			//javax.mail.internet.InternetAddress[] ccaddress = {new javax.mail.internet.InternetAddress(cc)};
			msg.setRecipients(javax.mail.Message.RecipientType.CC, cclist);

			if (reply != null && !reply.trim().equals("")) {
				javax.mail.internet.InternetAddress[] replyto = {new javax.mail.internet.InternetAddress(reply)};
				msg.setReplyTo(replyto);
			}

			msg.setSubject(Subject);

			InetAddress addr = InetAddress.getLocalHost();
			String hostName = addr.getHostName();
			msg.setText(sMessage);

			for (int i = 0; i < 5; i++) {
				try {
					javax.mail.Transport.send(msg);
					mailSent = true;
					break;
				} catch (Exception ex) {

					String str = "Thrown while trying to send e-mail (attempt# " + (i + 1) + ") as follows:\n" + sMessage + "\n\n" + "StackTrace:\n" + ex.getMessage() + "\n\n" + "Will Re-try " + (5 - i) + " times\n\n" + "This error was thrown at: " + new java.util.Date();
					if (logger.isErrorEnabled()) {
						logger.error(str);
					}
				}

				try {
					Thread.sleep(sleepTime);
				} catch (Exception e) {
					String str = "Thrown while WAITING to re-send e-mail\n" + "StackTrace:\n" + e.getMessage() + "\n\n" + "This error was thrown at: " + new java.util.Date();
					if (logger.isErrorEnabled()) {
						logger.error(str);
					}
				}
			}

			if (!mailSent) {
				String str = "***ERROR***: The following e-mail could NOT be sent despite " + (5 + 1) + " attempts:\n" + sMessage;
				if (logger.isErrorEnabled()) {
					logger.error(str);
				}
			}

		} catch (Exception ex) {
			
			throw ex;
		}

		return mailSent;
		
	}

	/**
	 * Send mail...
	 */

	public static boolean sendEMail(String from, String to, String sCC, String sBcc, String host, String sMessage, String Subject, String reply) throws Exception {
		
		

		boolean debug = false;
		long sleepTime = 1000 * 5; // sleep for ...sleepTime

		boolean mailSent = false;
		boolean noResponse=false;

		// create some properties and get the default Session
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		//javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);
		javax.mail.Session session = javax.mail.Session.getInstance(props, null);
		session.setDebug(debug);

		try {

			InternetAddress[] tolist = InternetAddress.parse(to, false);
			InternetAddress[] cclist = InternetAddress.parse(sCC, false);
			InternetAddress[] bcclist = InternetAddress.parse(sBcc, false);

			// create a message
			javax.mail.Message msg = new javax.mail.internet.MimeMessage(session);
			//msg.addHeader("X-Priority", "1");
			//msg.addHeader("Importance", "High");
			msg.setFrom(new javax.mail.internet.InternetAddress(from));

			msg.setRecipients(javax.mail.Message.RecipientType.TO, tolist);
			msg.setRecipients(javax.mail.Message.RecipientType.CC, cclist);
			msg.setRecipients(javax.mail.Message.RecipientType.BCC, bcclist);

			if (reply != null && !reply.trim().equals("")) {
				javax.mail.internet.InternetAddress[] replyto = {new javax.mail.internet.InternetAddress(reply)};
				msg.setReplyTo(replyto);
			}

			msg.setSubject(Subject);

			InetAddress addr = InetAddress.getLocalHost();
			String hostName = addr.getHostName();
			msg.setText(sMessage);

			for (int i = 0; i < 5; i++) {
				try {
					javax.mail.Transport.send(msg);
					mailSent = true;
					noResponse=true;
					setServerResponse(true);
					break;
				} catch (Exception ex) {
					noResponse=false;
					String str = "Thrown while trying to send e-mail (attempt# " + (i + 1) + ") as follows:\n" + sMessage + "\n\n" + "StackTrace:\n" + ex.getMessage() + "\n\n" + "Will Re-try " + (5 - i) + " times\n\n" + "This error was thrown at: " + new java.util.Date();
					if (logger.isErrorEnabled()) {
						logger.error(str);
					}
				}

				try {
					Thread.sleep(sleepTime);
				} catch (Exception e) {
					String str = "Thrown while WAITING to re-send e-mail\n" + "StackTrace:\n" + e.getMessage() + "\n\n" + "This error was thrown at: " + new java.util.Date();
					if (logger.isErrorEnabled()) {
						logger.error(str);
					}
				}
			}

			if (!mailSent) {
				
				String str = "***ERROR***: The following e-mail could NOT be sent despite " + (5 + 1) + " attempts:\n" + sMessage;
				if (logger.isErrorEnabled()) {
					logger.error(str);
				}
				
				if(!noResponse)//v2sagar
				{
					System.out.println("Mail server is down..pl try again later..");
					setServerResponse(false);					
				}				
			}

		} catch (Exception ex) {
			throw ex;
		}

		return mailSent;
	}
	
	
	
	
	public static boolean sendEmail(ETSMail mail) throws Exception {

		boolean sent = false;

		try {

			sent = ETSUtils.sendEMail(mail.getFrom(),mail.getTo(),mail.getCc(), mail.getBcc(), Global.mailHost,mail.getMessage(),mail.getSubject(),mail.getReplyTo());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return sent;
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
	    return CommonEmailHelper.formatEmailSubject(sInString);
	}

	/**
     * NOTE: Please use this method to escape string only if you are using
     * prepared statement to insert into database. This will not work for
     * normal statement. The escape string for single quote is handled differently
     * if it is just a normal insert statement and not prepared statement.
     */

    public static String escapeString(String str) {
        return escapeString(str, 0xf423f);
	}
	/**
     * NOTE: Please use this method to escape string only if you are using
     * prepared statement to insert into database. This will not work for
     * normal statement. The escape string for single quote is handled differently
     * if it is just a normal insert statement and not prepared statement.
	 * Creation date: (5/16/2002 12:45:13 AM)
	 * @return java.lang.String
	 * @param str java.lang.String
	 * @param length int
	 */

	public static String escapeString(String str, int length) {
        if (str == null)
        	return "";
        if (str.length() > length)
        	str = str.substring(0, length);
        StringTokenizer st = new StringTokenizer(str, "'");
        StringBuffer buffer = null;
        for (; st.hasMoreTokens(); buffer.append(st.nextToken()))
        	if (buffer == null)
        		buffer = new StringBuffer(str.length() + 20);
        	else
        		buffer.append("\'");

        if (buffer == null)
        	return str;
        else
        	return buffer.toString();
	}


	/**
	 * Method insertEmailLot.
	 * @param conn
	 * @param sMailType
	 * @param sKey1
	 * @param sKey2
	 * @param sKey3
	 * @param sProjectId
	 * @param sEmailSubject
	 * @param sToList
	 * @param sCC
	 */
	public static int insertEmailLog(Connection conn, String sMailType, String sKey1, String sKey2, String sKey3, String sProjectId, String sEmailSubject, String sToList, String sCC) throws SQLException, Exception {

		PreparedStatement pstmt = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			sQuery.append("INSERT INTO ETS.ETS_EMAIL_LOG (TIMESTAMP,MAIL_TYPE,KEY1,KEY2,KEY3,PROJECT_ID,SUBJECT,TO,CC) VALUES (");
			sQuery.append("?,?,?,?,?,?,?,?,?) ");

			pstmt = conn.prepareStatement(sQuery.toString());

			pstmt.setTimestamp(1,new Timestamp(System.currentTimeMillis()));
			pstmt.setString(2,sMailType);
			pstmt.setString(3,sKey1);
			pstmt.setString(4,sKey2);
			pstmt.setString(5,sKey3);
			pstmt.setString(6,sProjectId);
			pstmt.setString(7,sEmailSubject);
			pstmt.setString(8,sToList);
			pstmt.setString(9,sCC);

			iCount = pstmt.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}

		return iCount;
	}

	public static String getTopCatId(Connection con, String sProjectId, int iViewType) throws SQLException, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		String sTopCat = "";

		try {

			sQuery.append("SELECT CAT_ID FROM ETS.ETS_CAT WHERE VIEW_TYPE= ? AND PROJECT_ID = ? AND PARENT_ID=? with ur");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSUtils::getTopCatId()::QUERY : " + sQuery.toString());
			}

			stmt = con.prepareStatement(sQuery.toString());
			stmt.setInt(1,iViewType);
			stmt.setString(2,sProjectId);
			stmt.setInt(3,0);			// Set Parent ID to Zero for the tabs..

			rs = stmt.executeQuery();

			if (rs.next()) {
				sTopCat = String.valueOf(rs.getInt(1));
			} else {
				sTopCat = "0";
			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return sTopCat;
	}


    public static void popupHeaderLeft(String header, String subheader, PrintWriter writer){
		writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\"><tr>");
		writer.println("<td headers=\"\" width=\"12\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" width=\"12\" height=\"1\" alt=\"\" /></td>");

		writer.println("<td headers=\"\" align=\"left\">");
		writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
		writer.println("<tr><td headers=\"\" width=\"100%\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"8\" alt=\"\" /></td></tr>");
		writer.println("<tr><td headers=\"\" class=\"boldtitle\" width=\"100%\" align=\"left\">"+header+"</td></tr>"); //header
		writer.println("<tr><td headers=\"\" class=\"subtitle\" width=\"100%\" align=\"left\">"+subheader+"</td></tr>"); //sub header
		writer.println("<tr><td headers=\"\" width=\"100%\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
		writer.println("<tr><td headers=\"\" width=\"100%\" align=\"left\">");
    }
    public static void popupHeaderRight(PrintWriter writer){
		writer.println("</td>");
		writer.println("<td headers=\"\"><img src=\"//www.ibm.com/i/c.gif\" width=\"12\" height=\"1\" alt=\"\" /></td>");
		writer.println("</tr></table>");
		writer.println("</td></tr></table>");
    }

	public static void displayError(PrintWriter out, String ecode, String sname) {
		out.println("<script language=\"JavaScript\" type=\"text/javascript\" >");
		out.println("location.replace('" + Defines.SERVLET_PATH + "ETSErrorServlet.wss?ecode=" + ecode + "&sname=" + URLEncoder.encode(sname) + "') ");
		out.println("</script>");
	}

    public static String formatDateTime(Timestamp ts) throws Exception {

        try {

            String sTempDate = ts.toString();

            String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
            String sTime = sTempDate.substring(11, 16);

            String sHour = sTempDate.substring(11, 13);
            String sMin = sTempDate.substring(14, 16);
            String sAMPM = "AM";

            if (Integer.parseInt(sHour) == 12) {
                sHour = String.valueOf(Integer.parseInt(sHour));
                if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
                sAMPM = "PM";
            } else if (Integer.parseInt(sHour) > 12) {
                sHour = String.valueOf(Integer.parseInt(sHour) - 12);
                if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
                sAMPM = "PM";
            }

            return sDate + " " + sHour + ":" + sMin + sAMPM.toLowerCase() ;

        } catch (Exception e) {
            throw e;
        }

    }
    
    //v2sagar
    public static String formatDateTime(String ts) throws Exception {

        try {

            String sTempDate = ts;
            String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
            String sTime = sTempDate.substring(11, 16);

            String sHour = sTempDate.substring(11, 13);
            String sMin = sTempDate.substring(14, 16);
            String sAMPM = "AM";
            
            sAMPM=sTempDate.substring(sTempDate.length() -2,sTempDate.length());

            return sDate + " " + sHour + ":" + sMin + sAMPM.toLowerCase() +" ("+"GMT"+")" ;

        } catch (Exception e) {
            throw e;
        }

    }


	/**
	 * Method getUsersNameFromEdgeId.
	 * @param con
	 * @param sEdgeId
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
    public static String getUsersNameFromEdgeId(Connection con, String sEdgeId) throws SQLException, Exception {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        String sUserName = "";

        try {

            sQuery.append("SELECT LTRIM(RTRIM(USER_FNAME)) || ' ' || LTRIM(RTRIM(USER_LNAME)) FROM AMT.USERS WHERE EDGE_USERID = ? with ur");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSUtils::getUsersNameFromEdgeId()::QUERY : " + sQuery.toString());
			}

            stmt = con.prepareStatement(sQuery.toString());
            stmt.setString(1,sEdgeId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                sUserName = ETSUtils.checkNull(rs.getString(1));
            } else {
                sUserName = sEdgeId;
            }

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(rs);
            ETSDBUtils.close(stmt);
        }

        return sUserName;

    }

    public static ETSProjectInfoBean getProjInfoBean(Connection con) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        Vector vInfo = new Vector();

        ETSProjectInfoBean projBean = new ETSProjectInfoBean();

        try {

            sQuery.append("SELECT PROJECT_ID,INFO_TYPE,INFO_MODULE,LENGTH(IMAGE),IMAGE_ALT_TEXT,INFO_DESC,INFO_LINK FROM ETS.ETS_PROJECT_INFO ORDER BY PROJECT_ID,INFO_MODULE with ur");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSProjectInfo::loadDetails::QUERY : " + sQuery.toString());
			}

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            while (rs.next()) {

                String sProjectId = ETSUtils.checkNull(rs.getString("PROJECT_ID"));
                String sInfoType = ETSUtils.checkNull(rs.getString("INFO_TYPE"));
                int iInfoModule = rs.getInt("INFO_MODULE");
                int iImageLength = rs.getInt(3);
                String sImageAltText = ETSUtils.checkNull(rs.getString("IMAGE_ALT_TEXT"));
                String sInfoDesc = ETSUtils.checkNull(rs.getString("INFO_DESC"));
                String sInfoLink = ETSUtils.checkNull(rs.getString("INFO_LINK"));

                String[] sDetails = new String[]{sProjectId,sInfoType,String.valueOf(iInfoModule),String.valueOf(iImageLength),sImageAltText,sInfoDesc,sInfoLink};

                vInfo.addElement(sDetails);

            }

            projBean.setInfo(vInfo);
            projBean.setLoaded(true);

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(rs);
            ETSDBUtils.close(stmt);
        }

        return projBean;
    }

	public static String formatDate(Timestamp ts) throws Exception {

		try {

			if (ts == null || ts.toString().trim().equals("")) {
				return "-";
			} else {

				String sTempDate = ts.toString();

				String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);

				return sDate;
			}

		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * @param conn
	 * @param sUserID
	 * @param sProjectId
	 * @param sTabName
	 * @param sAction
	 * @return int
	 * @throws SQLException
	 * @throws Exception
	 */
	public static int insertTabHits(Connection conn, String sUserID, String sProjectId, String sTabName, String sAction) throws SQLException, Exception {

		Statement stmt = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			sQuery.append("INSERT INTO ETS.ETS_METRICS (USER_ID,PROJECT_ID,TAB_NAME,ACTION,LAST_TIMESTAMP) VALUES (");
			sQuery.append("'" + sUserID + "',");
			sQuery.append("'" + sProjectId + "',");
			sQuery.append("'" + sTabName + "',");
			sQuery.append("'" + sAction + "',");
			sQuery.append("current timestamp)");

			stmt = conn.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}

		return iCount;
	}

	//	sandra added this for 4.4.1
	public static boolean isIBMer(String sIrId, Connection con) throws Exception {

		  boolean isibmer = true;

		try {
		   String edge_userid = AccessCntrlFuncs.getEdgeUserId(con, sIrId);
		   String decaftype = AccessCntrlFuncs.decafType(edge_userid, con);
		   if(!decaftype.equalsIgnoreCase("I")){
			  isibmer = false;
		   }
		}
		catch (Exception e) {
		   isibmer = false;
		   throw e;
		}
		finally {
		}

		return isibmer;
	 }

	 public static String getTitleString(String sCaption) {

	 	ETSProperties prop = new ETSProperties();
		String sBookmark = "";

		sBookmark = "<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"443\" height=\"10\" valign=\"top\" align=\"left\" >" + sCaption + "</td></tr></table><br />";
		return sBookmark;

	 }

	 public static String getBookMarkString(String sCaption, String sAnchor, boolean printbm) {

	 	ETSProperties prop = new ETSProperties();
		String sBookmark = "";
		StringBuffer sBookMark = new StringBuffer("");

	 	if (printbm){

			sBookMark.append("<script language=\"JavaScript\" type=\"text/javascript\">");
			sBookMark.append("if (navigator.appName==\"Microsoft Internet Explorer\" && parseInt(navigator.appVersion) >= 4) {");
			if (prop.displayHelp() == true) {
				sBookMark.append("document.write ('<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\"><a href=\"javascript: window.external.AddFavorite(location.href, document.title);\">Bookmark this page</a>&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?proj=project\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?proj=project','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?proj=project','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">Help</a></td></tr></table><br />')");
			} else {
				sBookMark.append("document.write ('<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\"><a href=\"javascript: window.external.AddFavorite(location.href, document.title);\">Bookmark this page</a></td></tr></table><br />')");
			}
			sBookMark.append("} else if (parseInt(navigator.appVersion)>3) {");
			if (prop.displayHelp() == true) {
				sBookMark.append("document.write ('<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\">To bookmark this page, press <b>Ctrl+D</b>.&nbsp;&nbsp;<a href=\"" + prop.getHelpURL() + "\">Help</a></td></tr></table><br />')");
			} else {
				sBookMark.append("document.write ('<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\">To bookmark this page, press <b>Ctrl+D</b></td></tr></table><br />')");
			}
			sBookMark.append("}");
			sBookMark.append("</script>");

			sBookMark.append("<noscript>");
			//sBookMark.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\">Press <b>Ctrl+D</b> to bookmark this page.<a href=\"" + prop.getHelpURL() + "\">Help</a></td></tr></table><br />");
			if (prop.displayHelp() == true) {
				 sBookMark.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\">&nbsp;<a href=\"" + prop.getHelpURL() + "\">Help</a></td></tr></table><br />");
			} else {
				 sBookMark.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\"></td></tr></table><br />");
			}
			sBookMark.append("</noscript>");

	 	} else{
			sBookMark.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"443\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td></tr></table><br />");
	 	}

		return sBookMark.toString();

	 }

	public static String getBookMarkString(String sCaption, String sAnchor) {

	   ETSProperties prop = new ETSProperties();

	   StringBuffer sBookMark = new StringBuffer("");

	   sBookMark.append("<script language=\"JavaScript\" type=\"text/javascript\">");
	   sBookMark.append("if (navigator.appName==\"Microsoft Internet Explorer\" && parseInt(navigator.appVersion) >= 4) {");
	   if (prop.displayHelp() == true) {
		   sBookMark.append("document.write ('<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\"><a href=\"javascript: window.external.AddFavorite(location.href, document.title);\">Bookmark this page</a>&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?proj=project\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?proj=project','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?proj=project','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">Help</a></td></tr></table><br />')");
	   } else {
		   sBookMark.append("document.write ('<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\"><a href=\"javascript: window.external.AddFavorite(location.href, document.title);\">Bookmark this page</a></td></tr></table><br />')");
	   }
	   sBookMark.append("} else if (parseInt(navigator.appVersion)>3) {");
	   if (prop.displayHelp() == true) {
	   		sBookMark.append("document.write ('<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\">To bookmark this page, press <b>Ctrl+D</b>.&nbsp;&nbsp;<a href=\"" + prop.getHelpURL() + "\">Help</a></td></tr></table><br />')");
	   } else {
			sBookMark.append("document.write ('<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\">To bookmark this page, press <b>Ctrl+D</b></td></tr></table><br />')");
	   }

	   sBookMark.append("}");
	   sBookMark.append("</script>");

	   sBookMark.append("<noscript>");
	   if (prop.displayHelp() == true) {
			sBookMark.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\">&nbsp;<a href=\"" + prop.getHelpURL() + "\">Help</a></td></tr></table><br />");
	   } else {
			sBookMark.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr valign=\"top\"><td headers=\"\" width=\"243\" height=\"10\" valign=\"top\" align=\"left\" class=\"subtitle\">" + sCaption + "</td><td headers=\"\" width=\"200\" height=\"10\" valign=\"top\" align=\"right\" class=\"small\"></td></tr></table><br />");
	   }

	   sBookMark.append("</noscript>");


	   return sBookMark.toString();

	}

	public static String makeFirstLetterUpperCase(String sInString) {

		String sOutString = "";

		try {

			String sTemp = sInString.toLowerCase();
			sOutString = sTemp.substring(0,1).toUpperCase() + sTemp.substring(1,sTemp.length());
		} catch (Exception e) {
			e.printStackTrace();
			sOutString = sInString;
		}

		return sOutString;


	}

	public static String checkUserRole(EdgeAccessCntrl es,String sProjectId) throws Exception {

		String sRole = Defines.INVALID_USER;
		Connection con = null;

		try {

			con = ETSDBUtils.getConnection();

			sRole = getUserRole(es.gIR_USERN,sProjectId,con);

//			if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT, "tg_member=MD")) {
//				sRole = Defines.ETS_ADMIN;
//			} else if (es.Qualify(Defines.ETS_ENTITLEMENT, "tg_member=MD")) {
//				if (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,sProjectId,Defines.OWNER)) {
//					sRole = Defines.WORKSPACE_OWNER;
//				} else if (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,sProjectId,Defines.ADMIN)) {
//					sRole = Defines.WORKSPACE_MANAGER;
//				} else if (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,sProjectId,Defines.CLIENT)) {
//					sRole = Defines.WORKSPACE_CLIENT;
//				} else if (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,sProjectId,Defines.USER)) {
//					sRole = Defines.WORKSPACE_MEMBER;
//				} else if (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,sProjectId,Defines.VISITOR)) {
//					sRole = Defines.WORKSPACE_VISITOR;
//				} else {
//					if (es.Qualify(Defines.ETS_EXECUTIVE_ENTITLEMENT, "tg_member=MD")) {
//						sRole = Defines.ETS_EXECUTIVE;
//					}
//				}
//			} else if (es.Qualify(Defines.ETS_EXECUTIVE_ENTITLEMENT, "tg_member=MD")) {
//				sRole = Defines.ETS_EXECUTIVE;
//			}

		} catch (Exception e) {
		   throw e;
		} finally {
			ETSDBUtils.close(con);
		}

		return sRole;
	 }

	public static String checkUserRole(EdgeAccessCntrl es,String sProjectId, Connection conn) throws Exception {

		String sRole = Defines.INVALID_USER;

			sRole = getUserRole(es.gIR_USERN,sProjectId,conn);

//			if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT, "tg_member=MD")) {
//				sRole = Defines.ETS_ADMIN;
//			} else if (es.Qualify(Defines.ETS_ENTITLEMENT, "tg_member=MD")) {
//				if (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,sProjectId,Defines.OWNER)) {
//					sRole = Defines.WORKSPACE_OWNER;
//				} else if (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,sProjectId,Defines.ADMIN)) {
//					sRole = Defines.WORKSPACE_MANAGER;
//				} else if (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,sProjectId,Defines.CLIENT)) {
//					sRole = Defines.WORKSPACE_CLIENT;
//				} else if (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,sProjectId,Defines.USER)) {
//					sRole = Defines.WORKSPACE_MEMBER;
//				} else if (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,sProjectId,Defines.VISITOR)) {
//					sRole = Defines.WORKSPACE_VISITOR;
//				} else {
//					if (es.Qualify(Defines.ETS_EXECUTIVE_ENTITLEMENT, "tg_member=MD")) {
//						sRole = Defines.ETS_EXECUTIVE;
//					}
//				}
//			} else if (es.Qualify(Defines.ETS_EXECUTIVE_ENTITLEMENT, "tg_member=MD")) {
//				sRole = Defines.ETS_EXECUTIVE;
//			}

		return sRole;
	 }

	public static String getUserRole(String ir_userid,String sProjectId,Connection conn) throws Exception {

		/**
		 * If the person is a super admin, then all other roles are invalid.
		 * If the person is an executive, then the persons role in the workspace is valid and executive is ignored
		 * If the person is not admin and not executive, then the actual role is returned.
		 */

		/*
		 * Added checks for ITAR in release 5.4.1 by sathish.
		 * First determine if the project is ITAR project, if not, continue the existing code.
		 * If ITAR Project, then check for ITAR entitlement additionally...
		 */

		ETSProj proj = new ETSProj();
		UserRole role = new UserRole();
		
		try {
			
			proj = getProjectDetails(conn,sProjectId);
			role = new UserRole();
			
			return role.getUserRole(conn,ir_userid,sProjectId,proj.getProjectType(),proj.isITAR(),proj.getIsPrivate());
			
		} finally {
			proj = null;
			role = null;
		}

	 }

	public static boolean isUserPhotoAvailable(Connection con, String sUserIRID) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");

		Vector vQuestions = new Vector();
		long lSize = 0;
		boolean bIsAvailable = false;

		try {

			sQuery.append("SELECT USER_PHOTO FROM ETS.ETS_USER_INFO WHERE USER_ID = '" + sUserIRID + "' with ur");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {
				Blob photo = rs.getBlob(1);
				if (photo == null) {
					bIsAvailable = false;
				} else {
					lSize = photo.length();
				}

			}

			if (lSize > 0) {
				bIsAvailable = true;
			} else {
				bIsAvailable = false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return bIsAvailable;

	}

	public static int dateDiff(Timestamp ts1, Timestamp ts2) throws Exception {

	   int iJulDate1=0, iJulDate2=0;

	   try {

			String sDate1 = ts1.toString().substring(5, 7) + "/" + ts1.toString().substring(8, 10) + "/" + ts1.toString().substring(0, 4);;

			String sMonth = sDate1.substring(0, 2);
			String sDay = sDate1.substring(3, 5);
			String sYear = sDate1.substring(6, 10);

			sDate1 = sYear+sMonth+sDay;

			String sDate2 = ts2.toString().substring(5, 7) + "/" + ts2.toString().substring(8, 10) + "/" + ts2.toString().substring(0, 4);;;

			String sMonth2 = sDate2.substring(0, 2);
			String sDay2 = sDate2.substring(3, 5);
			String sYear2 = sDate2.substring(6, 10);

			sDate2 = sYear2+sMonth2+sDay2;

		   iJulDate1 = DatesArithmatic.GetJulianDate(sDate1);
		   iJulDate2 = DatesArithmatic.GetJulianDate(sDate2);

		} catch (Exception ex) {

		   throw ex;
		} finally {
		  
		}
		 return (iJulDate2-iJulDate1);
	}

	public static boolean isUserCVAvailable(Connection con, String sUserIRID) throws SQLException, Exception {

		  Statement stmt = null;
		  ResultSet rs = null;

		  StringBuffer sQuery = new StringBuffer("");

		  Vector vQuestions = new Vector();
		  long lSize = 0;
		  boolean bIsAvailable = false;

		  try {

			 sQuery.append("SELECT USER_CV FROM ETS.ETS_USER_INFO WHERE USER_ID = '" + sUserIRID + "' with ur");

			 stmt = con.createStatement();
			 rs = stmt.executeQuery(sQuery.toString());

			 if (rs.next()) {
				Blob skills = rs.getBlob(1);
				if (skills == null)
				   bIsAvailable = false;
				else
				   lSize = skills.length();
			 }

			 if (lSize > 0) {
				bIsAvailable = true;
			 } else {
				bIsAvailable = false;
			 }

		  } catch (SQLException e) {
			 e.printStackTrace();
			 throw e;
		  } catch (Exception e) {
			 e.printStackTrace();
			 throw e;
		  } finally {
			 ETSDBUtils.close(rs);
			 ETSDBUtils.close(stmt);
		  }

		  return bIsAvailable;

	   }


	   /**
		*
   		* This checks if the project belongs to Blade project
   		* @param etsProjId
   		* @return
   		* @throws SQLException
   		* @throws Exception
   		*/

   		public static boolean isEtsProjBladeProject(String etsProjId) throws SQLException, Exception {
   			Connection conn = null;
   			boolean flag = false;
   			try {
   				conn = ETSDBUtils.getConnection();
   				flag = isEtsProjBladeProject(etsProjId, conn);
   			} finally {
   				ETSDBUtils.close(conn);
   			}
   			return flag;
   		}


   		/**
		 * This will check if the project belongs to
   		 * @param etsProjId
   		 * @param conn
   		 * @return
   		 * @throws SQLException
   		 * @throws Exception
   		*/
   		public static boolean isEtsProjBladeProject(String etsProjId, Connection conn) throws SQLException, Exception {
   			StringBuffer sb = new StringBuffer();
   			int count = 0;
   			sb.append("select count(ETS_PROJECT_ID) from BLADE.BLADE_PROJECTS");
   			sb.append(" WHERE");
   			sb.append(" ETS_PROJECT_ID = '" + etsProjId + "' ");
   			sb.append(" with ur");
   			count = AmtCommonUtils.getRecCount(conn, sb.toString());
   			if (count > 0) {
   				return true;
   			}
   			return false;
		}


		/**
		 * This method wraps the String and pads that string with spaces.
		 * Creation date: (10/28/01 4:35:24 PM)
		 * @return java.lang.String
		 * @param sInString java.lang.String
		 * @exception java.lang.Exception The exception description.
		 */
		public static String formatDescStr(String sInString) throws java.lang.Exception {

			StringBuffer sOut = new StringBuffer();
			boolean bBreakFlag = false;

			int iLength = 55;

			try {

				byte sTemp[] = sInString.getBytes();

				sInString = new String(sTemp);

				int iCounter = 1;

				if (sInString.length() > iLength) {

					for (int i = 0; i < sInString.length(); i++) {

						if (sTemp[i] == (byte) '\n') {
							iCounter = 1;
						}

						if (iCounter % iLength == 0) {
							bBreakFlag = true;
						}

						if (bBreakFlag) {
							iCounter = iCounter + 1;
							if (sInString.substring(i, i + 1).equals(",") || sInString.substring(i, i + 1).equals(" ") || sInString.substring(i, i + 1).equals(";")) {
								if (!sInString.substring(i, i + 1).equals(" ")) {
									sOut.append(sInString.substring(i, i + 1));
								}
								sOut.append("\n");
								bBreakFlag = false;
								iCounter = 1;
							} else {
								sOut.append(sInString.substring(i, i + 1));
							}
						} else {
							iCounter = iCounter + 1;
							sOut.append(sInString.substring(i, i + 1));
						}
					}
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
		 * Method getManagerNameFromDecafTable.
		 * @param con
		 * @param sEdgeId
		 * @return String
		 * @throws SQLException
		 * @throws Exception
		 */
		public static String getManagerNameFromDecafTable(Connection con, String sEdgeId) throws SQLException, Exception {

		    PreparedStatement stmt = null;
		    ResultSet rs = null;
		    StringBuffer sQuery = new StringBuffer("");
		    String sUserName = "";

		    try {

		        sQuery.append("SELECT LTRIM(RTRIM(MGR_FIRST_NAME)) || ' ' || LTRIM(RTRIM(MGR_LAST_NAME)) FROM DECAF.USERS_BLUEPAGES_INFO WHERE EDGE_USER_ID = ? with ur");

				if (logger.isDebugEnabled()) {
					logger.debug("ETSUtils::getManagerNameFromDecafTable()::QUERY : " + sQuery.toString());
				}

		        stmt = con.prepareStatement(sQuery.toString());
		        stmt.setString(1,sEdgeId);

		        rs = stmt.executeQuery();

		        if (rs.next()) {
		            sUserName = ETSUtils.checkNull(rs.getString(1));
		        } else {
		            sUserName = "";
		        }

		    } catch (SQLException e) {
		        throw e;
		    } catch (Exception e) {
		        throw e;
		    } finally {
		        ETSDBUtils.close(rs);
		        ETSDBUtils.close(stmt);
		    }

		    return sUserName;

		}

		/**
		 * Method getManagersEMailFromDecafTable.
		 * @param con
		 * @param sEdgeId
		 * @return String
		 * @throws SQLException
		 * @throws Exception
		 */
		public static String getManagersEMailFromDecafTable(Connection con, String sEdgeId) throws SQLException, Exception {

		    PreparedStatement stmt = null;
		    ResultSet rs = null;
		    StringBuffer sQuery = new StringBuffer("");
		    String sUserName = "";

		    try {

		        sQuery.append("SELECT LTRIM(RTRIM(MGR_EMAIL_ID)) FROM DECAF.USERS_BLUEPAGES_INFO WHERE EDGE_USER_ID = ? with ur");

				if (logger.isDebugEnabled()) {
					logger.debug("ETSUtils::getManagersEMailFromDecafTable()::QUERY : " + sQuery.toString());
				}

		        stmt = con.prepareStatement(sQuery.toString());
		        stmt.setString(1,sEdgeId);

		        rs = stmt.executeQuery();

		        if (rs.next()) {
		            sUserName = ETSUtils.checkNull(rs.getString(1));
		        } else {
		            sUserName = "";
		        }

		    } catch (SQLException e) {
		        throw e;
		    } catch (Exception e) {
		        throw e;
		    } finally {
		        ETSDBUtils.close(rs);
		        ETSDBUtils.close(stmt);
		    }

		    return sUserName;

		}

		/**
		 * Method getUserEdgeIdFromAMT.
		 * @param conn
		 * @param string
		 * @return String
		 */
		public static String getUserEdgeIdFromAMT(Connection conn, String sIRId) throws SQLException, Exception {

			PreparedStatement stmt = null;
			ResultSet rs = null;
			StringBuffer sQuery = new StringBuffer("");
			String sUserEdgeId = "";

			try {

				sQuery.append("SELECT EDGE_USERID FROM AMT.USERS WHERE IR_USERID = ? with ur");

				if (logger.isDebugEnabled()) {
					logger.debug("ETSUtils::getUserEdgeIdFromAMT()::QUERY : " + sQuery.toString());
				}

				stmt = conn.prepareStatement(sQuery.toString());
				stmt.setString(1,sIRId);

				rs = stmt.executeQuery();

				if (rs.next()) {
					sUserEdgeId = ETSUtils.checkNull(rs.getString(1));
				} else {
					sUserEdgeId = "";
				}


			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			} finally {
				ETSDBUtils.close(rs);
				ETSDBUtils.close(stmt);
			}

			return sUserEdgeId;
		}

		public static ETSProjectInfoBean getProjInfoBean() throws SQLException, Exception {

				Connection conn = null;
				ETSProjectInfoBean projBean = new ETSProjectInfoBean();

				try {

					conn = ETSDBUtils.getConnection();

					projBean = getProjInfoBean(conn);

				} finally {

					ETSDBUtils.close(conn);
				}

				return projBean;
	}
	
	
	/**
	 * Method getProjectDetails.
	 * @param projectidStr
	 * @return ETSProj
	 */
	public static ETSProj getProjectDetails(Connection con, String projectidStr) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		ETSProj projDetails = new ETSProj();

		try {

			sQuery.append("SELECT PROJECT_ID,PROJECT_DESCRIPTION,PROJECT_NAME,PROJECT_START," + "PROJECT_END,DECAF_PROJECT_NAME,PROJECT_OR_PROPOSAL,LOTUS_PROJECT_ID,RELATED_ID,PARENT_ID,COMPANY,PMO_PROJECT_ID,SHOW_ISSUE_OWNER,PROJECT_STATUS,DELIVERY_TEAM,GEOGRAPHY,INDUSTRY,IS_ITAR,PROJECT_TYPE,IS_PRIVATE FROM ETS.ETS_PROJECTS " + "WHERE PROJECT_ID = '" + projectidStr + "' with ur");

			logger.debug("ETSUtils::getProjectDetails::QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				String sProjId = ETSUtils.checkNull(rs.getString("PROJECT_ID"));
				String sProjDesc = ETSUtils.checkNull(rs.getString("PROJECT_DESCRIPTION"));

				String sProjName = ETSUtils.checkNull(rs.getString("PROJECT_NAME"));
				Timestamp tProjStart = rs.getTimestamp("PROJECT_START");
				Timestamp tProjEnd = rs.getTimestamp("PROJECT_END");
				String sDecafProjName = ETSUtils.checkNull(rs.getString("DECAF_PROJECT_NAME"));
				String sProjOrProposal = ETSUtils.checkNull(rs.getString("PROJECT_OR_PROPOSAL"));
				String sLotusProjID = ETSUtils.checkNull(rs.getString("LOTUS_PROJECT_ID"));
				String sRelatedId = ETSUtils.checkNull(rs.getString("RELATED_ID"));
				String sParentId = ETSUtils.checkNull(rs.getString("PARENT_ID"));
				String sCompany = ETSUtils.checkNull(rs.getString("COMPANY"));
				String sPmoProjectId = ETSUtils.checkNull(rs.getString("PMO_PROJECT_ID"));
				String sShowIssueOwner = ETSUtils.checkNull(rs.getString("SHOW_ISSUE_OWNER"));
				String sProjectStatus = ETSUtils.checkNull(rs.getString("PROJECT_STATUS"));
				String sDelivery = ETSUtils.checkNull(rs.getString("DELIVERY_TEAM"));
				String sGeo = ETSUtils.checkNull(rs.getString("GEOGRAPHY"));
				String sIndustry = ETSUtils.checkNull(rs.getString("INDUSTRY"));
				String sIsITAR = ETSUtils.checkNull(rs.getString("IS_ITAR"));
				String sProjectType = ETSUtils.checkNull(rs.getString("PROJECT_TYPE"));
				String sIsPrivate = ETSUtils.checkNull(rs.getString("IS_PRIVATE"));

				projDetails.setProjectId(sProjId);
				projDetails.setDescription(sProjDesc);
				projDetails.setName(sProjName);
				projDetails.setStartDate(tProjStart);
				projDetails.setEndDate(tProjEnd);
				projDetails.setDecafProject(sDecafProjName);
				projDetails.setProjectOrProposal(sProjOrProposal);
				projDetails.setLotusProject(sLotusProjID);
				projDetails.setRelatedProjectId(sRelatedId);
				projDetails.setParent_id(sParentId);
				projDetails.setCompany(sCompany);
				projDetails.setPmo_project_id(sPmoProjectId);
				projDetails.setShow_issue_owner(sShowIssueOwner);
				projDetails.setProject_status(sProjectStatus);
				projDetails.setProjBladeType(ETSUtils.isEtsProjBladeProject(projectidStr,con));
				

				projDetails.setDeliveryTeam(sDelivery);
				projDetails.setGeography(sGeo);
				projDetails.setIndustry(sIndustry);

				if (sIsITAR.equalsIgnoreCase("Y")) {
					projDetails.setITAR(true);
				} else {
					projDetails.setITAR(false);
				}

				if (sProjectType.equalsIgnoreCase(Defines.ETS_WORKSPACE_TYPE) || sProjectType.equalsIgnoreCase("")) {
					projDetails.setProjectType(Defines.ETS_WORKSPACE_TYPE);				
				} else {
					projDetails.setProjectType(Defines.AIC_WORKSPACE_TYPE);
				}
				
				projDetails.setIsPrivate(sIsPrivate);
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return projDetails;
	}

	/**
	 * Method getManagerNameFromDecafTable.
	 * @param con
	 * @param sEdgeId
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public static Vector getDecafProjectsForUserEdgeId(Connection con, String sEdgeId) throws SQLException, Exception {
	
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    StringBuffer sQuery = new StringBuffer("");
	    Vector decafproj = new Vector();
	
	    try {
	
	        sQuery.append("SELECT DISTINCT PROJECT FROM AMT.S_USER_PROJECT_VIEW WHERE USERID=? with ur");
	
			if (logger.isDebugEnabled()) {
				logger.debug("ETSUtils::getDecafProjectsForUserEdgeId()::QUERY : " + sQuery.toString());
			}
	
	        stmt = con.prepareStatement(sQuery.toString());
	        stmt.setString(1,sEdgeId);
	
	        rs = stmt.executeQuery();
	
	        while (rs.next()) {
	        	decafproj.addElement(checkNull(rs.getString(1)));
	        }
	
	    } catch (SQLException e) {
	        throw e;
	    } catch (Exception e) {
	        throw e;
	    } finally {
	        ETSDBUtils.close(rs);
	        ETSDBUtils.close(stmt);
	    }
	
	    return decafproj;
	
	}

	/**
	 * Method getUserEdgeIdFromAMT.
	 * @param conn
	 * @param string
	 * @return String
	 */
	public static String getsRPMProjectCode(Connection conn, String sPMOProjectId) throws SQLException, Exception {
	
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		String sRPMProjectCode = "";
	
		try {
	
			sQuery.append("SELECT DISTINCT RPM_PROJECT_CODE FROM ETS.ETS_PMO_MAIN WHERE PMO_PROJECT_ID = ? AND PMO_ID = ? with ur");
	
			if (logger.isDebugEnabled()) {
				logger.debug("ETSUtils::getPMOID()::QUERY : " + sQuery.toString());
			}
	
			stmt = conn.prepareStatement(sQuery.toString());
			stmt.setString(1,sPMOProjectId);
			stmt.setString(2,sPMOProjectId);
	
			rs = stmt.executeQuery();
	
			if (rs.next()) {
				sRPMProjectCode = ETSUtils.checkNull(rs.getString(1));
			} else {
				sRPMProjectCode = "";
			}
	
	
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}
	
		return sRPMProjectCode;
	}	

	/**
	 * @return Returns the serverResponse.
	 */
	public static boolean isServerResponse() {
		return serverResponse;
	}
	/**
	 * @param serverResponse The serverResponse to set.
	 */
	public static void setServerResponse(boolean serverResponse) {
		ETSUtils.serverResponse = serverResponse;
	}

	/**
	 *  Method to return the user role for AIC teamroom wrkspces
	 * @param conn
	 * @param projId
	 * @param irUserId
	 * @return
	 * @throws Exception
	 */
	
	public static AICUserDecafRole extractUserDetails(Connection conn, String projId, String irUserId) 
	throws Exception{
		AICUserDecafRole userDets = new AICUserDecafRole();
		PreparedStatement getUserDataSt = null;
		ResultSet rs = null;
		
		try {
			getUserDataSt = conn.prepareStatement("select map.project_id, "
				+ " map.datatype_name, map.entitlement_name, map.role_id," 
				+ " map.profile_name , map.profile_id, users.user_id " 
				+ " from ets.ws_decaf_mapping map, ets.ets_users users "
				+ " where map.project_id = ? "
				+ " and map.project_id = users.user_project_id "
				+ " and map.role_id = users.user_role_id "
				+ " and users.user_id = ?  " 
				+ " and users.active_flag = 'A' with ur" );
		
			getUserDataSt.setString(1, projId);
			getUserDataSt.setString(2, irUserId);
			rs = getUserDataSt.executeQuery();

			if (rs.next()) {
				String sProjId = ETSUtils.checkNull(rs.getString("PROJECT_ID"));
				String sDatatypeName = ETSUtils.checkNull(rs.getString("DATATYPE_NAME"));
				String sEntitlementName = ETSUtils.checkNull(rs.getString("ENTITLEMENT_NAME"));
				int iRoleId = rs.getInt("ROLE_ID");
				String sProfileName = ETSUtils.checkNull(rs.getString("PROFILE_NAME"));
				String sProfileId = ETSUtils.checkNull(rs.getString("PROFILE_ID"));
				String sUserId = ETSUtils.checkNull(rs.getString("USER_ID"));

				userDets.setProjectId(sProjId);
				userDets.setDatatypeName(sDatatypeName);
				userDets.setEntitlementName(sEntitlementName);
				userDets.setRoleId(iRoleId);
				userDets.setDecafProfileName(sProfileName);
				userDets.setDecafProfileId(sProfileId);
				userDets.setUserId(sUserId);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(getUserDataSt);
		}

	return userDets;

	}

	
	/**
	 * Method getAicEtsProjectDetails.
	 * @param projectidStr
	 * @return AICProj
	 * This includes all the attributes of a project
	 */
	public static AICProj getAicEtsProjectDetails(Connection con, String projectidStr) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		AICProj projDetails = new AICProj();

		try {

			sQuery.append("SELECT PROJECT_ID,PROJECT_DESCRIPTION,PROJECT_NAME,PROJECT_START," + "PROJECT_END,DECAF_PROJECT_NAME,PROJECT_OR_PROPOSAL,LOTUS_PROJECT_ID,RELATED_ID,PARENT_ID,COMPANY,PMO_PROJECT_ID,SHOW_ISSUE_OWNER,PROJECT_STATUS,DELIVERY_TEAM,GEOGRAPHY,INDUSTRY,IS_ITAR,PROJECT_TYPE,IS_PRIVATE,BRAND,SECTOR,SCE_SECTOR,SUB_SECTOR,PROCESS,IBM_ONLY FROM ETS.ETS_PROJECTS " + "WHERE PROJECT_ID = '" + projectidStr + "' with ur");

			logger.debug("ETSUtils::getProjectDetails::QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				String sProjId = ETSUtils.checkNull(rs.getString("PROJECT_ID"));
				String sProjDesc = ETSUtils.checkNull(rs.getString("PROJECT_DESCRIPTION"));

				String sProjName = ETSUtils.checkNull(rs.getString("PROJECT_NAME"));
				Timestamp tProjStart = rs.getTimestamp("PROJECT_START");
				Timestamp tProjEnd = rs.getTimestamp("PROJECT_END");
				String sDecafProjName = ETSUtils.checkNull(rs.getString("DECAF_PROJECT_NAME"));
				String sProjOrProposal = ETSUtils.checkNull(rs.getString("PROJECT_OR_PROPOSAL"));
				String sLotusProjID = ETSUtils.checkNull(rs.getString("LOTUS_PROJECT_ID"));
				String sRelatedId = ETSUtils.checkNull(rs.getString("RELATED_ID"));
				String sParentId = ETSUtils.checkNull(rs.getString("PARENT_ID"));
				String sCompany = ETSUtils.checkNull(rs.getString("COMPANY"));
				String sPmoProjectId = ETSUtils.checkNull(rs.getString("PMO_PROJECT_ID"));
				String sShowIssueOwner = ETSUtils.checkNull(rs.getString("SHOW_ISSUE_OWNER"));
				String sProjectStatus = ETSUtils.checkNull(rs.getString("PROJECT_STATUS"));
				String sDelivery = ETSUtils.checkNull(rs.getString("DELIVERY_TEAM"));
				String sGeo = ETSUtils.checkNull(rs.getString("GEOGRAPHY"));
				String sIndustry = ETSUtils.checkNull(rs.getString("INDUSTRY"));
				String sIsITAR = ETSUtils.checkNull(rs.getString("IS_ITAR"));
				String sProjectType = ETSUtils.checkNull(rs.getString("PROJECT_TYPE"));
				String sIsPrivate = ETSUtils.checkNull(rs.getString("IS_PRIVATE"));
				String sSector = ETSUtils.checkNull(rs.getString("SECTOR"));
				String sSubSector = ETSUtils.checkNull(rs.getString("SUB_SECTOR"));
				String sBrand = ETSUtils.checkNull(rs.getString("BRAND"));
				String sProcess = ETSUtils.checkNull(rs.getString("PROCESS"));
				String sSceSector = ETSUtils.checkNull(rs.getString("SCE_SECTOR"));
				String sIbmOnly = ETSUtils.checkNull(rs.getString("IBM_ONLY"));

				projDetails.setProjectId(sProjId);
				projDetails.setDescription(sProjDesc);
				projDetails.setName(sProjName);
				projDetails.setStartDate(tProjStart);
				projDetails.setEndDate(tProjEnd);
				projDetails.setDecafProject(sDecafProjName);
				projDetails.setProjectOrProposal(sProjOrProposal);
				projDetails.setLotusProject(sLotusProjID);
				projDetails.setRelatedProjectId(sRelatedId);
				projDetails.setParent_id(sParentId);
				projDetails.setCompany(sCompany);
				projDetails.setPmo_project_id(sPmoProjectId);
				projDetails.setShow_issue_owner(sShowIssueOwner);
				projDetails.setProject_status(sProjectStatus);
				projDetails.setProjBladeType(ETSUtils.isEtsProjBladeProject(projectidStr,con));
				

				projDetails.setDeliveryTeam(sDelivery);
				projDetails.setGeography(sGeo);
				projDetails.setIndustry(sIndustry);

				if (sIsITAR.equalsIgnoreCase("Y")) {
					projDetails.setITAR(true);
				} else {
					projDetails.setITAR(false);
				}

				if (sProjectType.equalsIgnoreCase(Defines.ETS_WORKSPACE_TYPE) || sProjectType.equalsIgnoreCase("")) {
					projDetails.setProjectType(Defines.ETS_WORKSPACE_TYPE);				
				} else {
					projDetails.setProjectType(Defines.AIC_WORKSPACE_TYPE);
				}
				
				projDetails.setIsPrivate(sIsPrivate);
				
				projDetails.setBrand(sBrand);
				projDetails.setSector(sSector);
				projDetails.setSce_sector(sSceSector);
				projDetails.setSub_sector(sSubSector);
				projDetails.setProcess(sProcess);
				if (sIbmOnly.equalsIgnoreCase("N") || sIbmOnly.equalsIgnoreCase("")) {
					projDetails.setIbmOnly(false);
				} else {
					projDetails.setIbmOnly(true);
				}
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return projDetails;
	}

	
}
