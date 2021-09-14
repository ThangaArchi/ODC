/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2006                                          */
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

package oem.edge.ets.fe.ismgt.helpers;

import java.util.List;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MessgFormatUtils {

	public static final String VERSION = "1.1";

	/**
	 * 
	 */
	public MessgFormatUtils() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
			 * 
			 * @param oldStr
			 * @return
			 */

	private static String formatMsgString(String oldStr) throws Exception {

		StringBuffer sb = new StringBuffer();

		String sToken = null;
		int iStartIndex = 0;
		List aList = EtsIssFilterUtils.formatMsgStringIntoList(oldStr, "====");

		if (aList != null && !aList.isEmpty()) {

			for (int index = 0; index < aList.size(); index++) {

				Global.println(index + ", " + aList.get(index));

				String tempStr = (String) aList.get(index);

				if (index % 2 != 0) {

					sb.append("====");
				}

				sb.append(formatMsgStringForCmnts(tempStr));

				if (index % 2 != 0) {

					sb.append("====");
				}
				sb.append("<br /><br /><br />");

			}

		}

		Global.println("print breaks ===" + sb.toString());

		return sb.toString();

	}
	
	/**
	 * 
	 * @param oldStr
	 * @return
	 * @throws Exception
	 */

	private static String formatMsgStringForCmnts(String oldStr) throws Exception {

		StringBuffer sb = new StringBuffer();

		String sToken = null;
		int iStartIndex = 0;
		List aList = EtsIssFilterUtils.formatMsgStringIntoList(oldStr, "Comments:");

		Global.println("STRING FROM DB======" + oldStr);

		if (aList != null && !aList.isEmpty()) {

			for (int index = 0; index < aList.size(); index++) {
				System.out.println(index + ", " + aList.get(index));

				if (index % 2 != 0) {

					sb.append("Comments:");
					sb.append("<br />");
				}

				sb.append(aList.get(index));

			}

		}

		Global.println("print breaks ===" + sb.toString());

		return sb.toString();

	}

	/**
	 * 
	 * @param commLog
	 * @return
	 */

	public static String getFormatComments(String commLog) {

		String formatComLogStr = "";

		try {

			formatComLogStr = formatMsgString(commLog);

		} catch (Exception e) {

			e.printStackTrace();

			formatComLogStr = commLog;
		}

		return formatComLogStr;
	}
	
	/**
		 * 
		 * @param commLog
		 * @return
		 */

		public static String getMultiIssueFormatComments(String commLog) {

			String formatComLogStr = "";

			try {

				formatComLogStr = formatMutliIssueMsgString(commLog);

			} catch (Exception e) {

				e.printStackTrace();

				formatComLogStr = commLog;
			}

			return formatComLogStr;
		}
		
	/**
				 * 
				 * @param oldStr
				 * @return
				 */

		private static String formatMutliIssueMsgString(String oldStr) throws Exception {

			StringBuffer sb = new StringBuffer();

			String sToken = null;
			int iStartIndex = 0;
			List aList = EtsIssFilterUtils.formatMsgStringIntoList(oldStr, "====");

			if (aList != null && !aList.isEmpty()) {

				for (int index = 0; index < aList.size(); index++) {

					Global.println(index + ", " + aList.get(index));

					String tempStr = (String) aList.get(index);

					if (index % 2 != 0) {

						sb.append("====");
					}

					sb.append(formatMultiIssueMsgStringForCmnts(tempStr));

					if (index % 2 != 0) {

						sb.append("====");
					}
					sb.append("<br />");

				}

			}

			Global.println("print breaks ===" + sb.toString());

			return sb.toString();

		}
		
	/**
		 * 
		 * @param oldStr
		 * @return
		 * @throws Exception
		 */

		private static String formatMultiIssueMsgStringForCmnts(String oldStr) throws Exception {

			StringBuffer sb = new StringBuffer();

			String sToken = null;
			int iStartIndex = 0;
			List aList = EtsIssFilterUtils.formatMsgStringIntoList(oldStr, "Comments:");

			Global.println("STRING FROM DB======" + oldStr);

			if (aList != null && !aList.isEmpty()) {

				for (int index = 0; index < aList.size(); index++) {
					System.out.println(index + ", " + aList.get(index));

					if (index % 2 != 0) {

						sb.append("<b>");
						sb.append("Comments:");
						sb.append("</b>");
						sb.append("<br />");
					}

					sb.append(aList.get(index));

				}

			}

			Global.println("print breaks ===" + sb.toString());

			return sb.toString();

		}


	

} //end of class
