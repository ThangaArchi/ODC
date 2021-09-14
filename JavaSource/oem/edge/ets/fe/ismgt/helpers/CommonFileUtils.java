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
package oem.edge.ets.fe.ismgt.helpers;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CommonFileUtils {
	
	public static final String VERSION = "1.2";

	/**
	 * 
	 */
	public CommonFileUtils() {
		super();
		
	}

	/**
				 * 
				 * 
				 * @param filecount
				 * @return
				 */

	public static String getFileAttachMsg(int filecount, String dynProbType, String actionType) {

		StringBuffer sbfile = new StringBuffer();

		if (filecount == 0) {

			sbfile.append("Currently, there are no files attached to this " + dynProbType + " .");
		}

		if (filecount == 1) {

			sbfile.append("Currently, this " + dynProbType + " has <b>" + filecount + " file attachment</b>. You can attach more files or continue with  ");

			if (actionType.equals("submitIssue")) {

				sbfile.append(" the " + dynProbType + " submission process.");

			}

			if (actionType.equals("modifyIssue")) {

				sbfile.append(" modifying the " + dynProbType + ".");

			}

			if (actionType.equals("resolveIssue")) {

				sbfile.append(" resolving  the " + dynProbType + ".");

			}
			if (actionType.equals("rejectIssue")) {

				sbfile.append(" rejecting the " + dynProbType + ".");

			}

			if (actionType.equals("closeIssue")) {

				sbfile.append(" closing the " + dynProbType + ".");

			}

			if (actionType.equals("commentIssue")) {

				sbfile.append(" adding comments to the " + dynProbType + ".");

			}

			if (actionType.equals("submitChange")) {

				sbfile.append(" the " + dynProbType + " submission process.");

			}

			if (actionType.equals("commentChange")) {

				sbfile.append(" updating the " + dynProbType + " .");

			}

		}

		if (filecount > 1) {

			sbfile.append("Currently, this " + dynProbType + " has  <b>" + filecount + " file attachments</b>. You can attach more files or continue with ");

			if (actionType.equals("submitIssue")) {

				sbfile.append(" the " + dynProbType + " submission process.");

			}

			if (actionType.equals("modifyIssue")) {

				sbfile.append(" modifying the " + dynProbType + ".");

			}

			if (actionType.equals("resolveIssue")) {

				sbfile.append(" resolving  the " + dynProbType + ".");

			}
			if (actionType.equals("rejectIssue")) {

				sbfile.append(" rejecting the " + dynProbType + ".");

			}

			if (actionType.equals("closeIssue")) {

				sbfile.append(" closing the " + dynProbType + ".");

			}

			if (actionType.equals("commentIssue")) {

				sbfile.append(" adding comments to the " + dynProbType + ".");

			}

			if (actionType.equals("submitChange")) {

				sbfile.append(" the " + dynProbType + " submission process.");

			}

			if (actionType.equals("commentChange")) {

				sbfile.append(" updating the " + dynProbType + " .");

			}

		}

		sbfile.append("&nbsp;Learn more about &nbsp;<a  href=\"jsp/ismgt/fileattach.jsp\" target=\"new\" \n");
		sbfile.append(" onclick=\"window.open('jsp/ismgt/fileattach.jsp','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" \n");
		sbfile.append(" onkeypress=\"window.open('jsp/ismgt/fileattach.jsp','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">attaching file(s)</a>.\n");
		sbfile.append("&nbsp;To proceed without attaching a file, click <b>Continue</b>.\n");
		return sbfile.toString();

	}

	/**
			 * Insert the method's description here.
			 * Creation date: (6/3/2002 11:23:49 AM)
			 * @return double
			 * @param number java.lang.String
			 */
	public static String printFileSize(String number) {

		double displaySize = 0;
		String displayBytes = "bytes";
		StringBuffer sb = new StringBuffer();

		double size = Double.valueOf(number).doubleValue();

		displaySize = size / 1024;
		displayBytes = " KB";

		/*if (size >= 1024 && size < (1024 * 1024)) {
			displaySize = size / 1024;
			displayBytes = " KB";
		} else if (size >= (1024 * 1024)) {
			displaySize = size / (1024 * 1024);
			displayBytes = " MB";
		} else {
			displaySize = size;
			displayBytes = " bytes";
		}*/

		java.math.BigDecimal bd = new java.math.BigDecimal(displaySize);
		bd = bd.setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
		double dno = bd.doubleValue();

		sb.append(dno + "");
		sb.append("");
		sb.append(displayBytes);

		return sb.toString();
	}

	/**
		 * 
		 * @param filecount
		 * @param dynProbType
		 * @return
		 */

	public static String getViewFileAttachMsg(int filecount, String dynProbType) {

		StringBuffer sbfile = new StringBuffer();

		if (filecount == 0) {

			sbfile.append("Currently, there are no files attached to this " + dynProbType + " .");
		}

		if (filecount == 1) {

			sbfile.append("Currently, this " + dynProbType + " has <b>" + filecount + " file attachment</b>. Files which have been attached to  " + dynProbType + " cannot be deleted, but additional files can be added.");
		}

		if (filecount > 1) {

			sbfile.append("Currently, this " + dynProbType + " has <b>" + filecount + " file attachments</b>. Files which have been attached to  " + dynProbType + " cannot be deleted, but additional files can be added.");

		}

		return sbfile.toString();

	}

	/**
	 * 
	 * @param etsIssObjKey
	 * @return
	 */

	public static String printBasicFileAttachElements(EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbfile = new StringBuffer();

		boolean isITAR = etsIssObjKey.getProj().isITAR();

		sbfile.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n");
		sbfile.append("<tr valign=\"top\"> <td  valign=\"top\" align=\"left\" width=\"25%\">\n");
		sbfile.append("<label for=\"uploadf\"> <b>Select file to attach</b>:</label></td>\n");
		sbfile.append("<td  valign=\"top\" align=\"left\" width=\"75%\">\n");

		if (isITAR) {

			sbfile.append("<input type=\"file\" id=\"uploadf\" name=\"uploadedFile[0]\" size=\"40\" class=\"iform\" style=\"width:320px\" width=\"320px\"/>\n");
		} else {

			sbfile.append("<input type=\"file\" id=\"uploadf\" name=\"upload_file\" size=\"40\" class=\"iform\" style=\"width:320px\" width=\"320px\"/>\n");
		}
		
		sbfile.append("</td>\n");
		sbfile.append("</tr>\n");
		sbfile.append("</table>\n");
		sbfile.append("<br />\n");

		sbfile.append(" <table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n");
		sbfile.append("<tr> <td  valign=\"top\" align=\"left\" width=\"25%\">\n");
		sbfile.append("<label for=\"filed\"><b>File description</b>:</label></td><td  valign=\"top\" align=\"left\" width=\"75%\">\n");
		if (isITAR) {
			sbfile.append("<input align=\"left\" id=\"filed\" class=\"iform\" label=\"file_desc\" maxlength=\"250\" name=\"document.description\" size=\"40\" src=\"\" type=\"text\" value=\"\" style=\"width:313px\" width=\"313px\" />\n");
		} else {
			sbfile.append("<input align=\"left\" id=\"filed\" class=\"iform\" label=\"file_desc\" maxlength=\"250\" name=\"file_desc\" size=\"40\" src=\"\" type=\"text\" value=\"\" style=\"width:313px\" width=\"313px\" />\n");
		}
		sbfile.append("</td> </tr> </table> \n");

		sbfile.append("<br />\n");

		sbfile.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"15%\">\n");
		sbfile.append("<tr><td  align=\"left\"><input type=\"image\"  name=\"attachfilebtn\" src=\"" + Defines.BUTTON_ROOT  + "arrow_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Attach file\" /></a></td>\n");
		sbfile.append("<td  align=\"left\" nowrap=\"nowrap\" >&nbsp;<b>Attach file<b></td></tr>\n");
		sbfile.append("</table>\n");

		sbfile.append("<br />\n");

		sbfile.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"15%\">\n");
		sbfile.append("<tr ><td><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" border=\"0\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>\n");
		sbfile.append("</table>\n");

		return sbfile.toString();

	}
	
	

} //end of class
