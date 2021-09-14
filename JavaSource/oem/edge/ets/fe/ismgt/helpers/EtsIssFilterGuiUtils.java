package oem.edge.ets.fe.ismgt.helpers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.AmtErrorHandler;
import oem.edge.amt.AmtHfConstants;
import oem.edge.amt.EntitledStatic;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.dao.CommonInfoDAO;
import oem.edge.ets.fe.ismgt.model.EtsFilterCondsViewParamsBean;
import oem.edge.ets.fe.ismgt.model.EtsFilterRepViewParamsBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterDetailsBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterRepTabBean;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;
import oem.edge.ets.fe.ismgt.model.EtsPrimaryContactInfo;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

import org.apache.commons.logging.Log;
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
 * Helper class to formulate common gui utils that appear across commonly in ets issue 
 * pages,like contact info
 *
 */
public class EtsIssFilterGuiUtils implements EtsIssFilterConstants {

	public static final String VERSION = "1.41.1.31";
	private EtsIssCommonGuiUtils comGuiUtils;
	private static Log logger = EtsLogger.getLogger(EtsIssFilterGuiUtils.class);

	/**
	 * Constructor for EtsIssFilterGuiUtils.
	 */
	public EtsIssFilterGuiUtils() {
		super();
		this.comGuiUtils = new EtsIssCommonGuiUtils();

		if (!Global.loaded) {

			Global.Init();
		}
	}

	/**
	 * This method will return the common contact module string for ets pages
	 * takes EtsPrimaryContactInfo as input and gives the html formatted output string
	 */

	public String getPrimaryContactModule(EtsPrimaryContactInfo primCont, String sProjectId) {

		StringBuffer buf = new StringBuffer();
		ETSProj proj = getProjectDetails(sProjectId);
		UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());
		
		String primContName = "Name not available";
		String primContMail = "E-mail not available";
		String primContPhone = "Phone not available";

		if (primCont != null) {

			primContName = primCont.getUserFullName();
			primContMail = primCont.getUserEmail();
			primContPhone = primCont.getUserContPhone();

		}
		primContName = ETSUtils.checkNull(primContName);
		
		
		if(primContName.length() == 0){
			primContName = "Name not available";
		
		}

		buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"150\">");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td></tr>");
		buf.append("<tr>");
		buf.append("<td class=\"tblue\" height=\"18\" width=\"150\"><b>&nbsp;&nbsp;Your contact</b></td>");
		buf.append("</tr>");
		buf.append("</table>");

		buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"150\">");
		buf.append("<tr>");
		buf.append("<td class=\"tgreen\" valign=\"top\" width=\"150\">");
		buf.append("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\"> ");
		buf.append("<tr valign=\"middle\">");

		buf.append("<td style=\"background-color: #ffffff; color: #000000; font-weight: normal; word-wrap:break-word;width:125px\">"); //class=\"fbox\">");
		buf.append("<b>" + primContName + "</b>");
		buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		buf.append("<tr valign=\"top\">");
		buf.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT  + "em.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
		buf.append("<td>");

		if (AmtCommonUtils.isResourceDefined(primContMail)) {

			buf.append("<a href=\"mailto:" + primContMail + "\" class=\"fbox\">E-mail me</a>");

		} else {

			buf.append("Email not available");

		}

		buf.append("</td></tr></table>");

		buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		buf.append("<tr valign=\"top\">");
		buf.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT  + "ph.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
		buf.append("<td>");

		if (AmtCommonUtils.isResourceDefined(primContPhone)) {

			buf.append("<b>" + primContPhone + "</b>");

		} else {

			buf.append("");

		}

		buf.append("</td></tr>");

		ETSDatabaseManager dbManager = new ETSDatabaseManager();

		int iIssueTopCat = 0;

		try {

			iIssueTopCat = dbManager.getTopCatId(sProjectId, 2); // 2 view type is for issues/changes
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("EXCEPTION OCCURED IN getPrimaryContactModule()");
			iIssueTopCat = 0;
		}

		if (iIssueTopCat > 0) {

			buf.append("<tr>");
			buf.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"7\" alt=\"\" /></td>");
			buf.append("</tr>");
			buf.append("<tr>");
			buf.append("<td colspan=\"2\"style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			buf.append("</tr>");
			buf.append("<tr>");
			buf.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			buf.append("</tr>");

			buf.append("<tr>");
			buf.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
			buf.append("<td><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + sProjectId + "&tc=" + String.valueOf(iIssueTopCat) + "&actionType=feedback&linkid=" + prop.getLinkID() + "\" class=\"fbox\" >We're listening</a></td>");
			buf.append("</tr>");
		}

		buf.append("</table>");

		buf.append("</td>");
		buf.append("</tr>");
		buf.append("</table>");
		buf.append("</td>");
		buf.append("</tr>");
		buf.append("</table>");

		return buf.toString();
	}

	/**
	 * To print the options in ListBox from the 2 sets of lists, with value==text
	 * 
	 */

	public String printSelectOptions(ArrayList issueTypeList, ArrayList prevIssueTypeList) {

		StringBuffer sb = new StringBuffer();

		String issueType = "";

		if (issueTypeList != null && !issueTypeList.isEmpty()) {

			int isize = issueTypeList.size();

			for (int i = 0; i < isize; i++) {

				issueType = (String) issueTypeList.get(i);

				if (prevIssueTypeList != null && !prevIssueTypeList.isEmpty() && prevIssueTypeList.contains(issueType)) {

					sb.append("<option value=\"" + issueType + "\" selected=\"selected\">" + issueType + "</option>\n");

				} else {

					sb.append("<option value=\"" + issueType + "\" >" + issueType + "</option>\n");

				}
			}

		}

		return sb.toString();

	}

	/**
	 * To print the options in ListBox from the 2 sets of lists, with value!=text
	 * 
	 */

	public String printSelectOptionsWithValue(ArrayList issueTypeList, ArrayList prevIssueTypeList) {

		StringBuffer sb = new StringBuffer();

		String issueTypeVal = "";
		String issueTypeTxt = "";

		if (issueTypeList != null && !issueTypeList.isEmpty()) {

			int isize = issueTypeList.size();

			for (int i = 0; i < isize; i += 2) {

				issueTypeVal = (String) issueTypeList.get(i);
				issueTypeTxt = (String) issueTypeList.get(i + 1);

				if (prevIssueTypeList != null && !prevIssueTypeList.isEmpty() && prevIssueTypeList.contains(issueTypeVal)) {

					sb.append("<option value=\"" + issueTypeVal + "\" selected=\"selected\">" + issueTypeTxt + "</option>\n");

				} else {

					sb.append("<option value=\"" + issueTypeVal + "\" >" + issueTypeTxt + "</option>\n");

				}
			}

		}

		return sb.toString();

	}

	/**
		 * To print the options in ListBox from the 2 sets of lists, with value!=text
		 * 
		 */

	public String printSelectOptionsWithValue(ArrayList issueTypeList, ArrayList prevIssueTypeList, boolean compText) {

		StringBuffer sb = new StringBuffer();

		String issueTypeVal = "";
		String issueTypeTxt = "";

		if (issueTypeList != null && !issueTypeList.isEmpty()) {

			int isize = issueTypeList.size();

			for (int i = 0; i < isize; i += 2) {

				issueTypeVal = (String) issueTypeList.get(i);
				issueTypeTxt = (String) issueTypeList.get(i + 1);

				if (!compText) {

					if (prevIssueTypeList != null && !prevIssueTypeList.isEmpty() && prevIssueTypeList.contains(issueTypeVal)) {

						sb.append("<option value=\"" + issueTypeVal + "\" selected=\"selected\">" + issueTypeTxt + "</option>\n");

					} else {

						sb.append("<option value=\"" + issueTypeVal + "\" >" + issueTypeTxt + "</option>\n");

					}

				} //comp text = false
				else {

					if (prevIssueTypeList != null && !prevIssueTypeList.isEmpty() && prevIssueTypeList.contains(issueTypeTxt)) {

						sb.append("<option value=\"" + issueTypeVal + "\" selected=\"selected\">" + issueTypeTxt + "</option>\n");

					} else {

						sb.append("<option value=\"" + issueTypeVal + "\" >" + issueTypeTxt + "</option>\n");

					}

				}
			}

		}

		return sb.toString();

	}

	/**
	 * To print the options in ListBox from the 2 sets of lists, with value==text
	 * 
	 */

	public String printSelectOptionsOnTop(ArrayList issueTypeList, ArrayList prevIssueTypeList) {

		StringBuffer sb = new StringBuffer();

		String issueType = "";

		int isize = issueTypeList.size();

		/////////new ///////////////////////////////
		//copy all elements to tempList
		ArrayList tempList = new ArrayList();

		for (int i = 0; i < isize; i++) {

			tempList.add(issueTypeList.get(i));

		}

		String tempElem = "";
		//delete all selected elements
		for (int j = 0; j < tempList.size(); j++) {

			tempElem = (String) tempList.get(j);

			if (prevIssueTypeList.contains(tempElem)) {

				tempList.remove(tempElem);
			}

		}

		//remove all//
		tempList.remove("All");

		///create a new with selected ones as top
		ArrayList topList = new ArrayList();
		topList.add("All");

		for (int k = 0; k < prevIssueTypeList.size(); k++) {

			if (!prevIssueTypeList.get(k).equals("All")) {

				topList.add(prevIssueTypeList.get(k));

			}

		}

		//add the remaing elements in Temp List to Top List

		for (int j = 0; j < tempList.size(); j++) {

			topList.add(tempList.get(j));

		}

		//finally display////

		int fsize = topList.size();

		for (int f = 0; f < fsize; f++) {

			issueType = (String) topList.get(f);

			//System.out.println("top selected string 11 ==" + issueType);

			if (prevIssueTypeList.contains(issueType)) {

				sb.append("<option value=\"" + issueType + "\" selected=\"selected\">" + issueType + "</option>\n");

			} else {

				sb.append("<option value=\"" + issueType + "\" >" + issueType + "</option>\n");

			}
		}
		////////////////////////

		return sb.toString();

	}

	/**
	 * To print the options in ListBox from the 2 sets of lists, with value!=text
	 * 
	 */

	public String printSelectOptionsWithValueOnTop(ArrayList issueTypeList, ArrayList prevIssueTypeList) {

		StringBuffer sb = new StringBuffer();

		String issueTypeVal = "";
		String issueTypeTxt = "";

		int isize = issueTypeList.size();

		HashMap tempMap = new HashMap();

		/////
		//copy all elements to tempList
		ArrayList tempList = new ArrayList();

		for (int i = 0; i < isize; i += 2) {

			issueTypeVal = (String) issueTypeList.get(i);
			issueTypeTxt = (String) issueTypeList.get(i + 1);

			tempList.add(issueTypeVal);
			tempList.add(issueTypeTxt);

			tempMap.put(issueTypeVal, issueTypeTxt);

		}

		String tempElem = "";
		//delete all selected elements
		for (int j = 0; j < tempList.size(); j += 2) {

			tempElem = (String) tempList.get(j);

			if (prevIssueTypeList.contains(tempElem)) {

				tempList.remove(tempMap.get(tempElem));
				tempList.remove(tempElem);

			}

		}

		//remove all//
		tempList.remove("All");
		tempList.remove("All");

		///create a new with selected ones as top
		ArrayList topList = new ArrayList();
		topList.add("All");
		topList.add("All");

		for (int k = 0; k < prevIssueTypeList.size(); k++) {

			if (!prevIssueTypeList.get(k).equals("All")) {

				topList.add(prevIssueTypeList.get(k));
				topList.add(tempMap.get(prevIssueTypeList.get(k)));

			}

		}

		//add the remaing elements in Temp List to Top List

		for (int j = 0; j < tempList.size(); j += 2) {

			topList.add(tempList.get(j));
			topList.add(tempList.get(j + 1));

		}

		//finally display////

		int fsize = topList.size();

		String fnVal = "";
		String fnTxt = "";

		for (int f = 0; f < fsize; f += 2) {

			fnVal = (String) topList.get(f);
			fnTxt = (String) topList.get(f + 1);

			if (prevIssueTypeList.contains(fnVal)) {

				sb.append("<option value=\"" + fnVal + "\" selected=\"selected\">" + fnTxt + "</option>\n");

			} else {

				sb.append("<option value=\"" + fnVal + "\" >" + fnTxt + "</option>\n");

			}

		}

		return sb.toString();

	}

	/**
	 * To print the calendar utils for the filter conditions
	 * based on CQ showDate method in EdCQFilterCommand.java
	 * the format of input date is shDate
	 * m,d,y are the actual values
	 * mname,dname,yname are the names of the list boxes
	 * of month,date and year respectively
	 * 
	 */

	public String showDate(String shDate, String mname, String dname, String yname) {

		StringBuffer sb = new StringBuffer();

		if (!AmtCommonUtils.isResourceDefined(shDate) || AmtCommonUtils.getTrimStr(shDate).equals("--")) {

			shDate = EtsIssFilterUtils.getCurDate();
		}

		System.out.println("Cuurent date==" + shDate);

		String m = shDate.substring(0, 2);

		String d = shDate.substring(3, 5);

		String yy = shDate.substring(6);

		int iyy = Integer.parseInt(yy);

		String mon[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

		Calendar cal = Calendar.getInstance();
		int y = cal.get(Calendar.YEAR);

		sb.append("<table summary=\"\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td > \n");

		sb.append(AmtErrorHandler.printImgLabel("m1", "month"));
		sb.append("<select id=\"m1\" class=\"iform\" name=\"" + mname + "\"> \n");
		for (int k = 1; k < 13; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			sb.append("<option value=" + qq);
			if (m.equals(qq))
				sb.append(" selected=\"selected\"  \n");
			sb.append(">" + mon[Integer.parseInt(qq) - 1] + "</option> \n");
		}

		sb.append("</select></td><td > ");

		sb.append(AmtErrorHandler.printImgLabel("d1", "date"));
		sb.append("<select id=\"d1\" class=\"iform\" name=\"" + dname + "\"> \n");
		for (int k = 1; k < 32; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			sb.append("<option value=" + qq);
			if (d.equals(qq))
				sb.append(" selected=\"selected\"  \n");
			sb.append(">" + qq + "</option> \n");
		}
		sb.append("</select></td><td > ");

		sb.append(AmtErrorHandler.printImgLabel("y1", "year"));
		sb.append("<select id=\"y1\" class=\"iform\" name=\"" + yname + "\"> \n");
		for (int k = iyy - 10; k < iyy + 11; k++) {
			//String qq = "" + (y + k - 2);
			String qq = "" + k;
			sb.append("<option value=" + qq);
			if (yy.equals(qq))
				sb.append(" selected=\"selected\"  \n");
			sb.append(">" + qq + "</option> \n");
		}
		sb.append("</select> \n");
		sb.append("</td></tr></table> \n");

		return sb.toString();
	}

	/***
	 * 
	 * prints common back across issue pages
	 */

	public String printCommonBack(EtsIssFilterObjectKey issobjkey) {

		StringBuffer sb = new StringBuffer();
		HashMap propMap = issobjkey.getPropMap();

		sb.append("<a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&tc=" + issobjkey.getTc() + "\">");
		sb.append("<img src=\"" + Defines.ICON_ROOT  + "bk.gif\" alt=\"" + (String) propMap.get("filter.reppg.bklnk.txt") + "\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a>");
		sb.append(" <a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&tc=" + issobjkey.getTc() + "\">" + (String) propMap.get("filter.reppg.bklnk.txt") + "</a>");

		return sb.toString();
	}

	public String printReturnToMainPageonErr(EtsIssFilterObjectKey issobjkey) {

		StringBuffer sbsub = new StringBuffer();
		HashMap propMap = issobjkey.getPropMap();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");
		sbsub.append("<td width=\"18\">\n");
		sbsub.append("<a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&tc=" + issobjkey.getTc() + "\">");
		sbsub.append("<img src=\"" + Defines.ICON_ROOT  + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&tc=" + issobjkey.getTc() + "\">" + (String) propMap.get("filter.reppg.bklnk.txt") + "</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

	public String printReturnToMainPageonErr(EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbsub = new StringBuffer();
		HashMap propMap = etsIssObjKey.getPropMap();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");
		sbsub.append("<td width=\"18\">\n");
		sbsub.append("<a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + etsIssObjKey.getProj().getProjectId() + "&linkid=" + etsIssObjKey.getSLink() + "&tc=" + etsIssObjKey.getTopCatId() + "\">");
		sbsub.append("<img src=\"" + Defines.ICON_ROOT  + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + etsIssObjKey.getProj().getProjectId() + "&linkid=" + etsIssObjKey.getSLink() + "&tc=" + etsIssObjKey.getTopCatId() + "\">" + (String) propMap.get("filter.reppg.bklnk.txt") + "</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

	/***
	 * 
	 * prints common action link across issue pages
	 */

	public String printCommonAction(EtsIssFilterObjectKey issobjkey, String actionName, String opn) {

		StringBuffer sb = new StringBuffer();

		sb.append("<a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + issobjkey.getProblemType() + "&opn=" + opn + "&proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&tc=" + issobjkey.getTc() + "\">");
		sb.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\" alt=\"" + actionName + "\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a>");
		sb.append(" <a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + issobjkey.getProblemType() + "&opn=" + opn + "&proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&tc=" + issobjkey.getTc() + "\">" + actionName + "</a>");

		return sb.toString();
	}

	/**
	  * To display the window popup for log commentary
	  *
	  */

	public String printUserInfoPopup(String userIrId, String userName, String utype) {

		StringBuffer sb = new StringBuffer();

		if (AmtCommonUtils.isResourceDefined(userIrId)) {

			sb.append("<a href=\"#\" onclick=\"window.open('EtsIssLogActionServlet.wss?opn=120&utype=" + utype + "&irid=" + java.net.URLEncoder.encode(userIrId) + "',");
			sb.append(" 'history','toolbar=no,scrollbars=yes,location=0,statusbar=0,menubar=0,resizable=yes,left=60,top=100,width=300,height=250')\"  ");
			sb.append(" onkeypress=\"window.open('EtsIssLogActionServlet.wss?opn=120&utype=" + utype + "&irid=" + java.net.URLEncoder.encode(userIrId) + "',");
			sb.append(" 'history','toolbar=no,scrollbars=yes,location=0,statusbar=0,menubar=0,resizable=yes,left=60,top=100,width=300,height=250')\" >" + userName + "</a> ");

		} else {

			sb.append(userName);
		}

		return sb.toString();
	}

	/**
	 * To print help blurb
	 */

	public String printHelpBlurb(String helpId) {

		StringBuffer sb = new StringBuffer();

		sb.append(EntitledStatic.addBlurb(helpId));

		return sb.toString();
	}

	/***
	 * 
	 * to print error msg
	 */

	public String printErrorMsg(ArrayList errMsgList) {

		StringBuffer sb = new StringBuffer();

		int errsize = errMsgList.size();
		String errmsg = "";

		if (errsize > 0) { //if errmsg

			//sb.append("<%-- table 3 for error msg module start --%> ");

			sb.append("<table summary=\"error msg info\" width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
			sb.append("<tbody>");

			for (int ii = 0; ii < errsize; ii++) { //start of error msg

				errmsg = (String) errMsgList.get(ii);

				sb.append("<tr>");
				sb.append("<td><span style=\"color: #ff3333\">" + errmsg + "</span></td>");
				sb.append("</tr>");

			} //end of list of error msg for loop

			sb.append("<tr>");
			sb.append("<td>&nbsp;</td>");
			sb.append("</tr>");
			sb.append("</tbody>");
			sb.append("</table>");

			//sb.append("<%-- table 3 for error msg module end --%>");

		}

		return sb.toString();

	}

	/**
	 * to print date func from date and to date
	 * 
	 */

	public String printStartEndDate(EtsIssFilterDetailsBean etsIssFilDetails) {

		StringBuffer sb = new StringBuffer();

		String prevIssueDateAll = etsIssFilDetails.getPrevIssueDateAll();
		String prevIssueStartDate = etsIssFilDetails.getPrevIssueStartDate();
		String prevIssueEndDate = etsIssFilDetails.getPrevIssueEndDate();

		//sb.append("<%-- table 111 for date filter conditions details start  --%> ");

		sb.append("<table summary=\"datesall\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"200\"> ");
		sb.append("<tbody> ");
		sb.append("<tr > ");
		sb.append("<td valign=\"top\" align=\"left\">");
		sb.append("<label for=\"ad\"><img src=\"//www.ibm.com/i/c.gif\" border=\"0\" width=\"1\" height=\"1\" alt=\"checkbox for all dates\" /></label> ");

		if (prevIssueDateAll.equals("All")) {

			sb.append(AmtErrorHandler.printImgLabel("lblchkidalldates", "All dates"));
			sb.append("<input id=\"lblchkidalldates\" align=\"left\" name=\"issuedateall\" type=\"checkbox\" value=\"All\" class=\"iform\" checked=\"checked\" />  ");

		} else {

			sb.append(AmtErrorHandler.printImgLabel("lblchkidalldates", "All dates"));
			sb.append("<input id=\"lblchkidalldates\" align=\"left\" name=\"issuedateall\" type=\"checkbox\" value=\"All\" class=\"iform\" />  ");

		}

		sb.append("</td> ");
		sb.append("<td valign=\"top\" align=\"left\"><b>All dates</b>&nbsp;&nbsp;&nbsp; - or -</td> ");
		sb.append("</tr> ");
		sb.append("</tbody> ");
		sb.append("</table> ");

		//sb.append("<%-- table 111 for date filter conditions details start  --%> ");

		sb.append("<table summary=\"datesall\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"200\"> ");
		sb.append("<tbody>   ");
		sb.append("<tr> ");
		sb.append("<td valign=\"top\" align=\"left\" width=\"200\"> ");

		//sb.append("<%-- table 112 for date filter conditions details start  --%> ");

		sb.append("<table border=\"0\" summary=\"alldates\" cellspacing=\"0\" cellpadding=\"0\" width=\"200\"> ");
		sb.append("<tbody> ");
		sb.append("<tr> ");
		sb.append("<td><b>From</b>: " + showDate(prevIssueStartDate, "IssueStartMonth", "IssueStartDay", "IssueStartYear") + "</td> ");
		sb.append("</tr> ");
		sb.append("<tr> ");
		sb.append("<td height=\"14\" width=\"200\"><b>To</b>:</td> ");
		sb.append("</tr> ");
		sb.append("<tr> ");
		sb.append("<td height=\"14\" width=\"200\">" + showDate(prevIssueEndDate, "IssueEndMonth", "IssueEndDay", "IssueEndYear") + " ");
		sb.append("</td> ");
		sb.append("</tr> ");
		sb.append("</tbody> ");
		sb.append("</table> ");

		//sb.append("<%-- table 112 for date filter conditions details end  --%> ");

		sb.append("</td>     ");
		sb.append("</tr> ");
		sb.append("</tbody> ");
		sb.append("</table> ");

		//sb.append("<%-- table 111 for date filter conditions details end  --%> ");

		return sb.toString();
	}

	/**
	 * print common cancel button
	 */

	public String printCommonCancel(EtsIssFilterObjectKey issobjkey, String opn) {

		StringBuffer sb = new StringBuffer();

		sb.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" >\n");
		sb.append("<tbody>\n");
		sb.append("<tr>\n");
		sb.append("<td ><a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + issobjkey.getProblemType() + "&opn=" + opn + "&proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&tc=" + issobjkey.getTc() + "\"><img src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
		sb.append("<td >&nbsp;<a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + issobjkey.getProblemType() + "&opn=" + opn + "&proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&tc=" + issobjkey.getTc() + "\">Cancel</a></td>");
		sb.append("</tr>\n");
		sb.append("</tbody>\n");
		sb.append("</table>\n");

		return sb.toString();

	}

	/**
	 * print common assign current owner msg
	 */

	public String printCommonAssignMsg(EtsIssFilterObjectKey issobjkey) {

		StringBuffer sb = new StringBuffer();
		String localIssSubType = issobjkey.getIssueSubType();

		sb.append("<table summary=\"more info\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		if (localIssSubType.equals(ETSISSUESUBTYPE)) {
			sb.append("<td valign=\"top\" align=\"left\" width=\"600\"><span class=\"small\">" + issobjkey.getPropMap().get("filter.assign.iss.msg") + "</span></td>");
		} else if (localIssSubType.equals(ETSCHANGESUBTYPE)) {

			sb.append("<td valign=\"top\" align=\"left\" width=\"600\"><span class=\"small\">" + issobjkey.getPropMap().get("filter.assign.chg.msg") + "</span></td>");
		} else {

			sb.append("<td valign=\"top\" align=\"left\" width=\"600\"><span class=\"small\">" + issobjkey.getPropMap().get("filter.assign.iss.msg") + "</span></td>");
		}
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		return sb.toString();

	}

	/**
	 * print common View Problem link
	 * 
	 */

	public String printViewIssueLink(EtsIssFilterObjectKey issobjkey, String issueProblemId, String issueTitle, String issueSource) {

		StringBuffer sb = new StringBuffer();
		String localIssSubType = issobjkey.getIssueSubType();
		String filopn = issobjkey.getOpn();

		if (localIssSubType.equals(ETSISSUESUBTYPE)) {

			sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + issobjkey.getProjectId() + "&tc=" + issobjkey.getTc() + "&sc=0&istyp=iss&linkid=" + issobjkey.getLinkid() + "&actionType=viewIssue&op=60&edge_problem_id=" + issueProblemId + "&flop=" + filopn + "&src=" + issueSource + "\">" + issueTitle + "</a>");

		} else if (localIssSubType.equals(ETSCHANGESUBTYPE)) {

			sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + issobjkey.getProjectId() + "&tc=" + issobjkey.getTc() + "&sc=0&istyp=chg&linkid=" + issobjkey.getLinkid() + "&actionType=viewChange&op=60&etsId=" + issueProblemId + "&flop=" + filopn + "&src=" + issueSource + "\">" + issueTitle + "</a>");

		} else {

			sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + issobjkey.getProjectId() + "&tc=" + issobjkey.getTc() + "&sc=0&istyp=iss&linkid=" + issobjkey.getLinkid() + "&actionType=viewIssue&op=60&edge_problem_id=" + issueProblemId + "&flop=" + filopn + "&src=" + issueSource + "\">" + issueTitle + "</a>");

		}

		return sb.toString();
	}

	/**
		 * print common View Problem link
		 * 
		 */

	public String printViewIssueLink(EtsIssFilterObjectKey issobjkey, String issueProblemId, String issueTitle) {

		StringBuffer sb = new StringBuffer();
		String localIssSubType = AmtCommonUtils.getTrimStr(issobjkey.getIssueSubType());
		String filopn = issobjkey.getOpn();
		int srt = issobjkey.getSortState();

		if (localIssSubType.equals(ETSISSUESUBTYPE)) {

			sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + issobjkey.getProjectId() + "&tc=" + issobjkey.getTc() + "&sc=0&istyp=iss&linkid=" + issobjkey.getLinkid() + "&actionType=viewIssue&op=60&edge_problem_id=" + issueProblemId + "&srt=" + srt + "&flop=" + filopn + "\">" + issueTitle + "</a>");

		} else if (localIssSubType.equals(ETSCHANGESUBTYPE)) {

			sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + issobjkey.getProjectId() + "&tc=" + issobjkey.getTc() + "&sc=0&istyp=chg&linkid=" + issobjkey.getLinkid() + "&actionType=viewChange&op=60&etsId=" + issueProblemId + "&srt=" + srt + "&flop=" + filopn + "\">" + issueTitle + "</a>");

		} else {

			sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + issobjkey.getProjectId() + "&tc=" + issobjkey.getTc() + "&sc=0&istyp=iss&linkid=" + issobjkey.getLinkid() + "&actionType=viewIssue&op=60&edge_problem_id=" + issueProblemId + "&srt=" + srt + "&flop=" + filopn + "\">" + issueTitle + "</a>");

		}

		return sb.toString();
	}

	/***
	 * print no recs msg
	 */

	public String printNoRecsMsg(EtsIssFilterObjectKey issobjkey, boolean srchflg) {

		StringBuffer sb = new StringBuffer();

		String istype = issobjkey.getProblemType();
		int state = issobjkey.getState();

		//sb.append("<%-- table 7 for no issues info details info starts --%>");

		sb.append("<table summary=\"No Issues info details\" cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
		sb.append("<tbody>");

		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"5\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
		sb.append("</tr>");

		sb.append("<tr>");

		if (istype.equals("iss")) {

			if (!srchflg) {

				///
				switch (state) {

					case ETSISSRPTWALL :

						sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("filter.norecs.iss.def.msg") + "</span></td>");

						break;

					case ETSISSRPTISUB :

						sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("filter.norecs.iss.isub.def.msg") + "</span></td>");

						break;

					case ETSISSRPTASGND :

						sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("filter.norecs.iss.asgn.def.msg") + "</span></td>");

						break;

					default :

						sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("filter.norecs.iss.def.msg") + "</span></td>");

						break;

				}

				///

			} else {

				sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("filter.norecs.iss.srch.msg") + "</span></td>");

			}

		}

		if (istype.equals("chg")) {

			if (!srchflg) {

				///
				switch (state) {

					case ETSISSRPTWALL :

						sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("filter.norecs.chg.def.msg") + "</span></td>");

						break;

					case ETSISSRPTISUB :

						sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("filter.norecs.chg.isub.def.msg") + "</span></td>");

						break;

					case ETSISSRPTASGND :

						sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("filter.norecs.chg.asgn.def.msg") + "</span></td>");

						break;

					default :

						sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("filter.norecs.chg.def.msg") + "</span></td>");

						break;

				}

				///

			} else {

				sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("filter.norecs.chg.srch.msg") + "</span></td>");

			}

		}

		sb.append("</tr>");

		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"5\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"443\" height=\"1\" /></td>");
		sb.append("</tr>");

		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 7 for no issues info details info end --%>");

		return sb.toString();
	}

	/***
	 * 
	 * to print common actions(filter conds, and back ) on no recs pages
	 * 
	 */

	public String printActionsOnRepPage(EtsIssFilterObjectKey issfilterkey) {

		String issopn = issfilterkey.getOpn();
		String opnqual = issopn.substring(0, 1);
		HashMap propMap = issfilterkey.getPropMap();

		StringBuffer sb = new StringBuffer();

		sb.append("<table summary=\"backinfo\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td valign=\"top\" align=\"left\">" + printCommonAction(issfilterkey, (String) propMap.get("filter.reppg.fclnk.txt"), opnqual + "1") + "</td>");
		sb.append("<td valign=\"top\" align=\"left\">" + printCommonBack(issfilterkey) + "</td> ");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		return sb.toString();

	}

	/***
		 * 
		 * to print common actions(filter conds, and back ) on no recs pages
		 * 
		 */

	public String printPrinterActionsOnRepPage(EtsIssFilterObjectKey issfilterkey) {

		String issopn = issfilterkey.getOpn();
		String opnqual = issopn.substring(0, 1);
		HashMap propMap = issfilterkey.getPropMap();

		StringBuffer sb = new StringBuffer();

		sb.append("<table summary=\"backinfo\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"650\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td valign=\"top\" align=\"left\">" + printCommonAction(issfilterkey, (String) propMap.get("filter.reppg.fclnk.txt"), opnqual + "1") + "</td>");
		sb.append("<td valign=\"top\" align=\"left\">" + printCommonBack(issfilterkey) + "</td> ");
		sb.append("<td valign=\"top\" align=\"left\">" + printPrinterFriendly(issfilterkey) + "</td> ");
		sb.append("<td valign=\"top\" align=\"left\">" + printDownLoadPage(issfilterkey) + "</td> ");
		sb.append("<td valign=\"top\" align=\"left\">&nbsp;</td> ");
		sb.append("<td valign=\"top\" align=\"left\">" + printMultipleIssues() + "</td> ");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		sb.append("</form>");

		return sb.toString();

	}

	/***
	 * print common actions on filter conds page
	 * 
	 */

	public String printActionsOnFilCondPage(EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();
		String issopn = issfilterkey.getOpn();
		String opnqual = issopn.substring(0, 1);

		sb.append("<table summary=\"backinfo\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td valign=\"top\" align=\"left\">");
		sb.append("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT  + "search_t.gif\" width=\"120\" height=\"21\" border=\"0\" alt=\"Search\" name=\"Search\" />");
		sb.append("</td> ");
		sb.append("<td valign=\"top\" align=\"left\">" + printCommonCancel(issfilterkey, opnqual + "0") + "");
		sb.append("</td>  ");
		sb.append("<td valign=\"top\" align=\"left\">" + printCommonBack(issfilterkey) + "</td>");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		sb.append("<input type=\"hidden\" name=\"srchon\" value=\"Y\" />");
		sb.append("</form>");

		return sb.toString();
	}

	/***
	 * print common filter conds across pages
	 * 
	 */

	public String printCommonFilConds(EtsIssFilterDetailsBean etsfilterdets, EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();

		//get common params//
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		String frmopn = opnqual + "2";

		EtsFilterCondsViewParamsBean issviewfc = issfilterkey.getFcViewBean();

		boolean srchflg = false;

		if (etsfilterdets.getSrchOn().equals("Y")) {

			srchflg = true;

		}

		//val
		ArrayList issueTypeList = etsfilterdets.getIssueTypeList();
		ArrayList severityTypeList = etsfilterdets.getSeverityTypeList();
		ArrayList statusTypeList = etsfilterdets.getStatusTypeList();

		//prev values
		ArrayList prevIssueTypeList = etsfilterdets.getPrevIssueTypeList();
		ArrayList prevSeverityTypeList = etsfilterdets.getPrevSeverityTypeList();
		ArrayList prevStatusTypeList = etsfilterdets.getPrevStatusTypeList();

		//sb.append("<%-- table 8 for filter conditions  start --%>");

		sb.append("<form name=\"filteriss\" method=\"post\" action=\"EtsIssFilterCntrlServlet.wss?linkid=" + linkid + "&tc=" + tc + "&proj=" + etsprojid + "&istyp=" + istype + "&opn=" + frmopn + "\" ");

		sb.append("<table summary=\"Filter Conditions\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td height=\"18\" width=\"443\" class=\"tblue\"><b>&nbsp;" + issviewfc.getFcHeaderName() + "</b></td>");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 8 for filter conditions  end --%>");

		sb.append("<br />");

		//sb.append("<%-- table 9 for filter conditions  start --%>");

		sb.append("<table summary=\"filter conditions details\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">");
		sb.append("<tbody>");
		sb.append("<tr bgcolor=\"#cccccc\" height=\"12\">");
		sb.append("<th valign=\"top\" align=\"left\" id=\"fcid1\"><b><label for=\"ff2\">" + issviewfc.getFcIssTypeName() + "");
		sb.append("</label></b>:</th>");
		sb.append("<th valign=\"top\" align=\"left\" id=\"fcid2\">&nbsp;</th>");
		sb.append("<th valign=\"top\" align=\"left\" id=\"fcid3\"><b><label for=\"ff3\">" + issviewfc.getFcSeverityName() + "</label></b>:</th>");
		sb.append("<th valign=\"top\" align=\"left\" id=\"fcid4\">&nbsp;</th>");
		sb.append("<th valign=\"top\" align=\"left\" id=\"fcid5\"><b><label for=\"ff4\">" + issviewfc.getFcStatusName() + "</label></b>:</th>");

		sb.append("</tr>");

		sb.append("<tr>");

		sb.append("<td headers=\"fcid1\" valign=\"top\" align=\"left\">");
		sb.append("<select id=\"ff2\" multiple=\"multiple\" size=\"5\" name=\"issuetype\" class=\"iform\" style=\"width:185px\" width=\"185px\" >");
		sb.append("" + printSelectOptions(issueTypeList, prevIssueTypeList) + "");
		sb.append("</select></td>");

		sb.append("<td headers=\"fcid2\" valign=\"top\" align=\"left\">&nbsp;</td>");

		sb.append("<td headers=\"fcid3\" valign=\"top\" align=\"left\">");
		sb.append("<select id=\"ff3\" multiple=\"multiple\" size=\"5\" name=\"issueseverity\" align=\"left\" class=\"iform\" style=\"width:185px\" width=\"185px\" >");
		sb.append("" + printSelectOptionsWithValue(severityTypeList, prevSeverityTypeList) + "");
		sb.append("</select></td>");

		sb.append("<td headers=\"fcid4\" valign=\"top\" align=\"left\">&nbsp;</td>");

		sb.append("<td valign=\"top\" align=\"left\" headers=\"fcid5\">");
		sb.append("<select id=\"ff4\" multiple=\"multiple\" size=\"5\" name=\"issuestatus\"  align=\"left\" class=\"iform\" style=\"width:185px\" width=\"185px\" >");

		if (!srchflg) {

			sb.append("" + printSelectOptionsOnTop(statusTypeList, prevStatusTypeList) + "");

		} else {

			sb.append("" + printSelectOptions(statusTypeList, prevStatusTypeList) + "");

		}

		sb.append(" </select></td>");

		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 9 for filter conditions  end --%> ");

		return sb.toString();

	}

	/***
	 * 
	 * To print Tab index, print Contact Mod and messg
	 */

	public String printCommonIssHeader(EtsAmtHfBean etsamthf, EtsIssFilterObjectKey issfilterkey, String headMsg) {

		StringBuffer sb = new StringBuffer();

		//print secure header
		sb.append(comGuiUtils.printSecureContentHeader(issfilterkey));

		sb.append("<table summary=\"tab info\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
		sb.append("<tbody>");
		sb.append("<tr valign=\"top\">");
		//sb.append("<%-- print tabs --%>");

		sb.append("<td width=\"443\" valign=\"top\">" + etsamthf.getTabIndex() + etsamthf.getIssueBreadCrumb(issfilterkey) + "");

		sb.append("<br />");

		//sb.append("<%-- table 2 for general info contact module start --%>");
		

		sb.append("<table summary=\"general info\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		
		sb.append("<td height=\"18\" width=\"443\">" + headMsg + "</td>");
		
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("<br />");
		sb.append("<br />");
		//v2sagar
		sb.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"><tr><td ><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"443\" height=\"1\" /></td></tr></table>");

		//sb.append("<%-- table 2 for general info contact module end --%>");
		sb.append("</td>");

		//sb.append("<%-- 7 pixel  --%>");

		sb.append("<td width=\"7\"><img alt=\"\" src=\"//www.ibm.com/i/c.gif\" width=\"7\" height=\"1\" /></td>");

		//sb.append("<%-- 150 right hand navg  --%>");

		sb.append("<td width=\"150\" valign=\"top\">" + etsamthf.getPrimaryContactModule() + "</td>");

		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 1 for tabs/right hand contact module end  --%>");

		return sb.toString();

	}

	/***
		* 
		* To print Tab index, print Contact Mod and messg
		*/

	public String printCommonIssHeaderWithoutBread(EtsAmtHfBean etsamthf, EtsIssFilterObjectKey issfilterkey, String headMsg) {

		StringBuffer sb = new StringBuffer();

		//print secure header
		sb.append(comGuiUtils.printSecureContentHeader(issfilterkey));

		sb.append("<table summary=\"tab info\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
		sb.append("<tbody>");
		sb.append("<tr valign=\"top\">");
		//sb.append("<%-- print tabs --%>");

		sb.append("<td width=\"443\" valign=\"top\">" + etsamthf.getTabIndex() + "");

		sb.append("<br />");

		//sb.append("<%-- table 2 for general info contact module start --%>");

		sb.append("<table summary=\"general info\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td height=\"18\" width=\"443\">" + headMsg + "</td>");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 2 for general info contact module end --%>");
		sb.append("</td>");

		//sb.append("<%-- 7 pixel  --%>");

		sb.append("<td width=\"7\"><img alt=\"\" src=\"//www.ibm.com/i/c.gif\" width=\"7\" height=\"1\" /></td>");

		//sb.append("<%-- 150 right hand navg  --%>");

		sb.append("<td width=\"150\" valign=\"top\">" + etsamthf.getPrimaryContactModule() + "</td>");

		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 1 for tabs/right hand contact module end  --%>");

		return sb.toString();

	}

	/***
			* 
			* To print Tab index, print Contact Mod and messg
			*/

	public String printCommonHeaderForWelcome(EtsAmtHfBean etsamthf, EtsIssFilterObjectKey issfilterkey, String headMsg) {

		StringBuffer sb = new StringBuffer();

		HashMap propMap = issfilterkey.getPropMap();

		String startUrl = (String) propMap.get("iss.getting.started.url");

		//print secure header
		sb.append(comGuiUtils.printSecureContentHeader(issfilterkey));

		sb.append("<table summary=\"tab info\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
		sb.append("<tbody>");
		sb.append("<tr valign=\"top\">");
		//sb.append("<%-- print tabs --%>");

		sb.append("<td width=\"443\" valign=\"top\">" + etsamthf.getTabIndex() + "");

		sb.append("<br />");

		//sb.append("<%-- table 2 for general info contact module start --%>");

		sb.append("<table summary=\"general info\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td height=\"18\" width=\"443\">" + headMsg + "");
		sb.append("</td>");

		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 2 for general info contact module end --%>");
		sb.append("</td>");

		//sb.append("<%-- 7 pixel  --%>");

		sb.append("<td width=\"7\"><img alt=\"\" src=\"//www.ibm.com/i/c.gif\" width=\"7\" height=\"1\" /></td>");

		//sb.append("<%-- 150 right hand navg  --%>");

		sb.append("<td width=\"150\" valign=\"top\">" + etsamthf.getPrimaryContactModule() + "</td>");

		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 1 for tabs/right hand contact module end  --%>");

		return sb.toString();

	}

	public String printGettingStartLink(String startUrl) {

		StringBuffer sb = new StringBuffer();

		sb.append("<a  href=\"" + startUrl + "\" target=\"new\" \n");
		sb.append(" onclick=\"window.open('" + startUrl + "','Services','toolbar=1,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=625,height=600,left=387,top=207'); return false;\" \n");
		sb.append(" onkeypress=\"window.open('" + startUrl + "','Services','toolbar=1,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=625,height=600,left=387,top=207'); return false;\"> \n");
		sb.append("Getting started\n");
		sb.append("</a>");

		return sb.toString();
	}

	/***
			* 
			* To print Tab index, print Contact Mod and messg
			*/

	public String printCommonIssHeaderForErrs(EtsAmtHfBean etsamthf, EtsIssFilterObjectKey issfilterkey, String headMsg) {

		StringBuffer sb = new StringBuffer();

		//print secure header
		sb.append(comGuiUtils.printSecureContentHeader(issfilterkey));

		sb.append("<table summary=\"tab info\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
		sb.append("<tbody>");
		sb.append("<tr valign=\"top\">");
		//sb.append("<%-- print tabs --%>");

		sb.append("<td width=\"443\" valign=\"top\">" + etsamthf.getTabIndex() + "");
		

		sb.append("<br />");

		//sb.append("<%-- table 2 for general info contact module start --%>");

		sb.append("<table summary=\"general info\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		
		sb.append("<td height=\"18\" width=\"443\"><span style=\"color:#ff3333\">" + headMsg + "</span></td>");
		
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 2 for general info contact module end --%>");
		sb.append("</td>");

		//sb.append("<%-- 7 pixel  --%>");

		sb.append("<td width=\"7\"><img alt=\"\" src=\"//www.ibm.com/i/c.gif\" width=\"7\" height=\"1\" /></td>");

		//sb.append("<%-- 150 right hand navg  --%>");

		sb.append("<td width=\"150\" valign=\"top\">" + etsamthf.getPrimaryContactModule() + "</td>");

		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 1 for tabs/right hand contact module end  --%>");

		return sb.toString();

	}

	/***
		 * 
		 * to print common actions(filter conds, and back ) on no recs pages
		 * 
		 */

	public String printPrinterFriendly(EtsIssFilterObjectKey issfilterkey) {

		String opn = issfilterkey.getOpn();
		//String opnqual = opn.substring(0, 1);
		HashMap propMap = issfilterkey.getPropMap();

		String viewUrl = "EtsIssFilterCntrlServlet.wss?istyp=" + issfilterkey.getProblemType() + "&opn=" + opn + "&proj=" + issfilterkey.getProjectId() + "&linkid=" + issfilterkey.getLinkid() + "&tc=" + issfilterkey.getTc() + "&prnt=Y";

		return comGuiUtils.printPrinterFriendly(viewUrl);

	}

	/***
			 * 
			 * to print common actions(filter conds, and back ) on no recs pages
			 * 
			 */

	public String printDownLoadPage(EtsIssFilterObjectKey issfilterkey) {

		String opn = issfilterkey.getOpn();
		//String opnqual = opn.substring(0, 1);
		HashMap propMap = issfilterkey.getPropMap();

		String viewUrl = "downLoadCsv.wss?istyp=" + issfilterkey.getProblemType() + "&opn=" + opn + "&proj=" + issfilterkey.getProjectId() + "&linkid=" + issfilterkey.getLinkid() + "&tc=" + issfilterkey.getTc() + "&dwnld=Y";

		return comGuiUtils.printDownLoadLink(viewUrl);

	}

	/***
		 * print common filter conds across pages
		 * 
		 */

	public String printCommonFilCondsForCr(EtsIssFilterDetailsBean etsfilterdets, EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();

		//get common params//
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		String frmopn = opnqual + "2";

		EtsFilterCondsViewParamsBean issviewfc = issfilterkey.getFcViewBean();

		boolean srchflg = false;

		if (etsfilterdets.getSrchOn().equals("Y")) {

			srchflg = true;

		}

		//val
		ArrayList issueTypeList = etsfilterdets.getIssueTypeList();
		ArrayList severityTypeList = etsfilterdets.getSeverityTypeList();
		ArrayList statusTypeList = etsfilterdets.getStatusTypeList();

		//prev values
		ArrayList prevIssueTypeList = etsfilterdets.getPrevIssueTypeList();
		ArrayList prevSeverityTypeList = etsfilterdets.getPrevSeverityTypeList();
		ArrayList prevStatusTypeList = etsfilterdets.getPrevStatusTypeList();

		//sb.append("<%-- table 8 for filter conditions  start --%>");

		sb.append("<form name=\"filteriss\" method=\"post\" action=\"EtsIssFilterCntrlServlet.wss?linkid=" + linkid + "&tc=" + tc + "&proj=" + etsprojid + "&istyp=" + istype + "&opn=" + frmopn + "\" ");

		sb.append("<table summary=\"Filter Conditions\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td height=\"18\" width=\"600\" class=\"tblue\"><b>&nbsp;" + issviewfc.getFcHeaderName() + "</b></td>");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 8 for filter conditions  end --%>");

		sb.append("<br />");

		//sb.append("<%-- table 9 for filter conditions  start --%>");

		sb.append("<table summary=\"filter conditions details\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">");
		sb.append("<tbody>");
		sb.append("<tr bgcolor=\"#cccccc\" height=\"12\">");
		sb.append("<th valign=\"top\" align=\"left\" id=\"fcid3\"><b><label for=\"ff3\">" + issviewfc.getFcSeverityName() + "</label></b>:</th>");
		sb.append("<th valign=\"top\" align=\"left\" id=\"fcid4\">&nbsp;</th>");
		sb.append("<th valign=\"top\" align=\"left\" id=\"fcid5\"><b><label for=\"ff4\">" + issviewfc.getFcStatusName() + "</label></b>:</th>");

		sb.append("</tr>");

		sb.append("<tr>");

		sb.append("<td headers=\"fcid3\" valign=\"top\" align=\"left\">");
		sb.append("<select id=\"ff3\" multiple=\"multiple\" size=\"5\" name=\"issueseverity\" align=\"left\" class=\"iform\" style=\"width:185px\" width=\"185px\" >");
		sb.append("" + printSelectOptionsWithValue(severityTypeList, prevSeverityTypeList) + "");
		sb.append("</select></td>");

		sb.append("<td headers=\"fcid4\" valign=\"top\" align=\"left\">&nbsp;</td>");

		sb.append("<td valign=\"top\" align=\"left\" headers=\"fcid5\">");
		sb.append("<select id=\"ff4\" multiple=\"multiple\" size=\"5\" name=\"issuestatus\"  align=\"left\" class=\"iform\" style=\"width:185px\" width=\"185px\" >");

		if (!srchflg) {

			sb.append("" + printSelectOptionsOnTop(statusTypeList, prevStatusTypeList) + "");

		} else {

			sb.append("" + printSelectOptions(statusTypeList, prevStatusTypeList) + "");

		}

		sb.append(" </select></td>");

		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 9 for filter conditions  end --%> ");

		return sb.toString();

	}

	/**
	 * prints printer friednly header
	 * @param sPageTitle
	 * @return
	 */

	public String printPrinterFriendlyHeader(String sPageTitle) {

		return comGuiUtils.printPrinterFriendlyHeader(sPageTitle);
	}

	/**
		 * prints printer friednly footer
		 * @param sPageTitle
		 * @return
		 */

	public String printPrinterFriendlyFooter() {

		return comGuiUtils.printPrinterFriendlyFooter();
	}

	/**
	 * 
	 * prints sort header
	 * @param issviewrep
	 * @param etsIssObjKey
	 * @return
	 */

	public String printIssFilterSortHeader(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();
		int sortstate = 0;

		//////

		//	get common params//
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		String frmopn = opnqual + "2";

		EtsFilterCondsViewParamsBean issviewfc = issfilterkey.getFcViewBean();

		/////////get state///
		sortstate = issfilterkey.getSortState();

		///
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();

		sb.append("<form name=\"filteriss\" method=\"post\" action=\"EtsIssFilterCntrlServlet.wss?linkid=" + linkid + "&tc=" + tc + "&proj=" + etsprojid + "&istyp=" + istype + "&opn=" + issopn + "\" ");
		sb.append("<tr>\n");

		//for check box
		sb.append("<th id=\"issid-1\" align=\"left\" valign=\"top\" width=\"25\">&nbsp;</th>\n");
		sb.append("<th id=\"issid-11\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		//for sort by cq trk id//
		sb.append("<th id=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"> \n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_trkid", sortstate, SORTTRKID_A, SORTTRKID_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTrkId() + "</span></th>\n");
		sb.append("<th id=\"issid01\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		////
		sb.append("<th id=\"issid1\" align=\"left\" valign=\"top\" width=\"170\"> \n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_title", sortstate, SORTISSUETITLE_A, SORTISSUETITLE_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTitleName() + "</span></th>\n");
		sb.append("<th id=\"issid2\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid3\" align=\"left\" valign=\"top\" width=\"65\"> \n");
		//	sort by title
		sb.append(printSortColumns("issue_sort_isstype", sortstate, SORTISSUETYPE_A, SORTISSUETYPE_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepIssueTypeName() + "</span></th>\n");

		sb.append("<th id=\"issid4\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid5\" align=\"left\" valign=\"top\" width=\"98\"> \n");
		//sort by submitter
		sb.append(printSortColumns("issue_sort_submitter", sortstate, SORTSUBMITTER_A, SORTSUBMITTER_D));

		sb.append("<span class=\"small\">" + issviewrep.getRepSubName() + "</span></th>\n");

		sb.append("<th id=\"issid6\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

		//		donot show submitter and owner details if proj is of blade type and customer is externals

		//show issue owner

		if (usrRolesModel.isShowOwnerName()) {

			sb.append("<th id=\"issid7\" align=\"left\" valign=\"top\" width=\"100\"> \n");

			//sort by owner
			sb.append(printSortColumns("issue_sort_owner", sortstate, SORTOWNER_A, SORTOWNER_D));

			sb.append("<span class=\"small\">" + issviewrep.getRepCownerName() + "</span></th>\n");
			sb.append("<th id=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

		} //show issue owner

		sb.append("<th id=\"issid9\" align=\"left\" valign=\"top\" width=\"75\" nowrap=\"nowrap\"> \n");

		//SORT BY svereity
		sb.append(printSortColumns("issue_sort_severity", sortstate, SORTSEVERITY_A, SORTSEVERITY_D));

		sb.append("<span class=\"small\">" + issviewrep.getRepSeverityName() + "</span></th>\n");
		sb.append("<th id=\"issid10\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		sb.append("<th id=\"issid11\" align=\"left\" valign=\"top\" width=\"60\" nowrap=\"nowrap\"> " + "\n");

		//sort by status
		sb.append(printSortColumns("issue_sort_status", sortstate, SORTSTATUS_A, SORTSTATUS_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepStatusName() + "</span></th>\n");
		sb.append("</tr> \n");

		sb.append("</form>\n");

		return sb.toString();

	}

	public String printSortColumns(String sortColumnName, int sortstate, int sortasc, int sortdesc) {

		StringBuffer sb = new StringBuffer();

		//		title//

		if (sortstate == 0) {

			sb.append("	               <input type=\"image\" name=\"" + sortColumnName + "_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_updown.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Click To Sort\" /> \n");

		} else if (sortstate == sortasc) {

			sb.append("	               <input type=\"image\" name=\"" + sortColumnName + "_D\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_up.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Sorted in ascending order\" /> \n");

		} else if (sortstate == sortdesc) {

			sb.append("	               <input type=\"image\" name=\"" + sortColumnName + "_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_down.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Sorted in descending order\" /> \n");

		} else {

			sb.append("	               <input type=\"image\" name=\"" + sortColumnName + "_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_updown.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Click To Sort\" /> \n");
		}

		return sb.toString();

	}

	/**
	 * 
	 * This is for printing sort header for REP ALL for External only
	 * @param issviewrep
	 * @param issfilterkey
	 * @return
	 */

	public String printIssFilterSortOrderForWAllExt(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();
		int sortstate = 0;

		//////

		//	get common params//
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		String frmopn = opnqual + "2";

		EtsFilterCondsViewParamsBean issviewfc = issfilterkey.getFcViewBean();

		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();

		/////////get state///

		sortstate = issfilterkey.getSortState();

		sb.append("<form name=\"filteriss\" method=\"post\" action=\"EtsIssFilterCntrlServlet.wss?linkid=" + linkid + "&tc=" + tc + "&proj=" + etsprojid + "&istyp=" + istype + "&opn=" + issopn + "\" ");
		sb.append("<tr>\n");

		//for check box
		sb.append(printThForChkBox());

		//		for sort by cq trk id//
		sb.append("<th id=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"> \n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_trkid", sortstate, SORTTRKID_A, SORTTRKID_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTrkId() + "</span></th>\n");
		sb.append("<th id=\"issid01\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		sb.append("<th id=\"issid1\" align=\"left\" valign=\"top\" width=\"120\">\n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_title", sortstate, SORTISSUETITLE_A, SORTISSUETITLE_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTitleName() + "</span></th>\n");
		sb.append("<th id=\"issid2\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid3\" align=\"left\" valign=\"top\" width=\"113\"> \n");
		//	sort by title//
		sb.append(printSortColumns("issue_sort_isstype", sortstate, SORTISSUETYPE_A, SORTISSUETYPE_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepIssueTypeName() + "</span></th>\n");

		sb.append("<th id=\"issid4\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		//blade proj logic
		//show submitter only to internals

		if (!issfilterkey.isProjBladeType()) {

			sb.append("<th id=\"issid5\" align=\"left\" valign=\"top\" width=\"140\"> \n");
			//sort by submitter
			sb.append(printSortColumns("issue_sort_submitter", sortstate, SORTSUBMITTER_A, SORTSUBMITTER_D));

			sb.append("<span class=\"small\">" + issviewrep.getRepSubName() + "</span></th>\n");

			sb.append("<th id=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				sb.append("<th id=\"issid5\" align=\"left\" valign=\"top\" width=\"140\"> \n");
				//sort by submitter
				sb.append(printSortColumns("issue_sort_submitter", sortstate, SORTSUBMITTER_A, SORTSUBMITTER_D));

				sb.append("<span class=\"small\">" + issviewrep.getRepSubName() + "</span></th>\n");

				sb.append("<th id=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

			}

		}

		sb.append("<th id=\"issid9\" align=\"left\" valign=\"top\" width=\"80\" nowrap=\"nowrap\"> \n");

		//SORT BY svereity
		sb.append(printSortColumns("issue_sort_severity", sortstate, SORTSEVERITY_A, SORTSEVERITY_D));

		sb.append("<span class=\"small\">" + issviewrep.getRepSeverityName() + "</span></th>\n");
		sb.append("<th id=\"issid10\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		sb.append("<th id=\"issid11\" align=\"left\" valign=\"top\" width=\"60\" nowrap=\"nowrap\"> " + "\n");

		//sort by status
		sb.append(printSortColumns("issue_sort_status", sortstate, SORTSTATUS_A, SORTSTATUS_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepStatusName() + "</span></th>\n");
		sb.append("</tr> \n");

		sb.append("</form>\n");

		return sb.toString();

	}

	/**
		 * 
		 * This is for printing sort header for REP ALL for External only
		 * @param issviewrep
		 * @param issfilterkey
		 * @return
		 */

	public String printIssFilterSortOrderForISub(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();
		int sortstate = 0;

		//////

		//	get common params//
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		String frmopn = opnqual + "2";

		EtsFilterCondsViewParamsBean issviewfc = issfilterkey.getFcViewBean();

		/////////get state///

		sortstate = issfilterkey.getSortState();

		///
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();

		sb.append("<form name=\"filteriss\" method=\"post\" action=\"EtsIssFilterCntrlServlet.wss?linkid=" + linkid + "&tc=" + tc + "&proj=" + etsprojid + "&istyp=" + istype + "&opn=" + issopn + "\" ");
		sb.append("<tr>\n");

		//for check box
		sb.append(printThForChkBox());

		//for sort by cq trk id//
		sb.append("<th id=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"> \n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_trkid", sortstate, SORTTRKID_A, SORTTRKID_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTrkId() + "</span></th>\n");
		sb.append("<th id=\"issid01\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		sb.append("<th id=\"issid1\" align=\"left\" valign=\"top\" width=\"130\">\n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_title", sortstate, SORTISSUETITLE_A, SORTISSUETITLE_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTitleName() + "</span></th>\n");
		sb.append("<th id=\"issid2\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid3\" align=\"left\" valign=\"top\" width=\"123\"> \n");
		//	sort by title//
		sb.append(printSortColumns("issue_sort_isstype", sortstate, SORTISSUETYPE_A, SORTISSUETYPE_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepIssueTypeName() + "</span></th>\n");

		sb.append("<th id=\"issid4\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		//show issue owner

		if (usrRolesModel.isShowOwnerName()) {

			sb.append("<th id=\"issid5\" align=\"left\" valign=\"top\" width=\"120\"> \n");

			//sort by owner
			sb.append(printSortColumns("issue_sort_owner", sortstate, SORTOWNER_A, SORTOWNER_D));

			sb.append("<span class=\"small\">" + issviewrep.getRepCownerName() + "</span></th>\n");

			sb.append("<th id=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

		} //show issue owner flag

		sb.append("<th id=\"issid9\" align=\"left\" valign=\"top\" width=\"80\" nowrap=\"nowrap\"> \n");

		//SORT BY svereity
		sb.append(printSortColumns("issue_sort_severity", sortstate, SORTSEVERITY_A, SORTSEVERITY_D));

		sb.append("<span class=\"small\">" + issviewrep.getRepSeverityName() + "</span></th>\n");
		sb.append("<th id=\"issid10\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		sb.append("<th id=\"issid11\" align=\"left\" valign=\"top\" width=\"60\" nowrap=\"nowrap\"> " + "\n");

		//sort by status
		sb.append(printSortColumns("issue_sort_status", sortstate, SORTSTATUS_A, SORTSTATUS_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepStatusName() + "</span></th>\n");
		sb.append("</tr> \n");

		sb.append("</form>\n");

		return sb.toString();

	}

	/**
			 * 
			 * This is for printing sort header for REP ALL for External only
			 * @param issviewrep
			 * @param issfilterkey
			 * @return
			 */

	public String printIssFilterSortOrderForISubExt(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();
		int sortstate = 0;

		//////

		//	get common params//
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		String frmopn = opnqual + "2";

		EtsFilterCondsViewParamsBean issviewfc = issfilterkey.getFcViewBean();

		/////////get state///

		sortstate = issfilterkey.getSortState();

		sb.append("<form name=\"filteriss\" method=\"post\" action=\"EtsIssFilterCntrlServlet.wss?linkid=" + linkid + "&tc=" + tc + "&proj=" + etsprojid + "&istyp=" + istype + "&opn=" + issopn + "\" ");
		sb.append("<tr>\n");

		//for check box
		sb.append(printThForChkBox());

		//for sort by cq trk id//
		sb.append("<th id=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"> \n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_trkid", sortstate, SORTTRKID_A, SORTTRKID_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTrkId() + "</span></th>\n");
		sb.append("<th id=\"issid01\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		sb.append("<th id=\"issid1\" align=\"left\" valign=\"top\" width=\"165\">\n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_title", sortstate, SORTISSUETITLE_A, SORTISSUETITLE_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTitleName() + "</span></th>\n");
		sb.append("<th id=\"issid2\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid3\" align=\"left\" valign=\"top\" width=\"168\"> \n");
		//	sort by title//
		sb.append(printSortColumns("issue_sort_isstype", sortstate, SORTISSUETYPE_A, SORTISSUETYPE_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepIssueTypeName() + "</span></th>\n");

		sb.append("<th id=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");
		sb.append("<th id=\"issid9\" align=\"left\" valign=\"top\" width=\"80\" nowrap=\"nowrap\"> \n");

		//SORT BY svereity
		sb.append(printSortColumns("issue_sort_severity", sortstate, SORTSEVERITY_A, SORTSEVERITY_D));

		sb.append("<span class=\"small\">" + issviewrep.getRepSeverityName() + "</span></th>\n");
		sb.append("<th id=\"issid10\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		sb.append("<th id=\"issid11\" align=\"left\" valign=\"top\" width=\"70\" nowrap=\"nowrap\"> " + "\n");

		//sort by status
		sb.append(printSortColumns("issue_sort_status", sortstate, SORTSTATUS_A, SORTSTATUS_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepStatusName() + "</span></th>\n");
		sb.append("</tr> \n");

		sb.append("</form>\n");

		return sb.toString();

	}

	/**
				 * 
				 * This is for printing sort header for REP ALL for External only
				 * @param issviewrep
				 * @param issfilterkey
				 * @return
				 */

	public String printIssFilterSortOrderForAsgndMe(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();
		int sortstate = 0;
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();

		//////

		//	get common params//
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		String frmopn = opnqual + "2";

		EtsFilterCondsViewParamsBean issviewfc = issfilterkey.getFcViewBean();

		/////////get state///

		sortstate = issfilterkey.getSortState();

		sb.append("<form name=\"filteriss\" method=\"post\" action=\"EtsIssFilterCntrlServlet.wss?linkid=" + linkid + "&tc=" + tc + "&proj=" + etsprojid + "&istyp=" + istype + "&opn=" + issopn + "\" ");
		sb.append("<tr>\n");

		//for check box
		sb.append(printThForChkBox());

		//		for sort by cq trk id//
		sb.append("<th id=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"> \n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_trkid", sortstate, SORTTRKID_A, SORTTRKID_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTrkId() + "</span></th>\n");
		sb.append("<th id=\"issid01\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid1\" align=\"left\" valign=\"top\" width=\"120\">\n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_title", sortstate, SORTISSUETITLE_A, SORTISSUETITLE_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTitleName() + "</span></th>\n");
		sb.append("<th id=\"issid2\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid3\" align=\"left\" valign=\"top\" width=\"110\"> \n");
		//	sort by title//
		sb.append(printSortColumns("issue_sort_isstype", sortstate, SORTISSUETYPE_A, SORTISSUETYPE_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepIssueTypeName() + "</span></th>\n");

		sb.append("<th id=\"issid4\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		if (!issfilterkey.isProjBladeType()) {

			sb.append("<th id=\"issid5\" align=\"left\" valign=\"top\" width=\"123\"> \n");

			//sort by submitter
			sb.append(printSortColumns("issue_sort_submitter", sortstate, SORTSUBMITTER_A, SORTSUBMITTER_D));
			sb.append("<span class=\"small\">" + issviewrep.getRepSubName() + "</span></th>\n");

			sb.append("<th id=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				sb.append("<th id=\"issid5\" align=\"left\" valign=\"top\" width=\"123\"> \n");

				//sort by submitter
				sb.append(printSortColumns("issue_sort_submitter", sortstate, SORTSUBMITTER_A, SORTSUBMITTER_D));
				sb.append("<span class=\"small\">" + issviewrep.getRepSubName() + "</span></th>\n");

				sb.append("<th id=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

			}

		}

		sb.append("<th id=\"issid9\" align=\"left\" valign=\"top\" width=\"90\" nowrap=\"nowrap\"> \n");

		//SORT BY svereity
		sb.append(printSortColumns("issue_sort_severity", sortstate, SORTSEVERITY_A, SORTSEVERITY_D));

		sb.append("<span class=\"small\">" + issviewrep.getRepSeverityName() + "</span></th>\n");
		sb.append("<th id=\"issid10\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		sb.append("<th id=\"issid11\" align=\"left\" valign=\"top\" width=\"60\" nowrap=\"nowrap\"> " + "\n");

		//sort by status
		sb.append(printSortColumns("issue_sort_status", sortstate, SORTSTATUS_A, SORTSTATUS_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepStatusName() + "</span></th>\n");
		sb.append("</tr> \n");

		sb.append("</form>\n");

		return sb.toString();

	}

	/**
		 * 
		 * This is for printing sort header for REP ALL for External only
		 * @param issviewrep
		 * @param issfilterkey
		 * @return
		 */

	public String printCrFilterSortOrderWAll(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();
		int sortstate = 0;

		//////

		//	get common params//
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		String frmopn = opnqual + "2";

		EtsFilterCondsViewParamsBean issviewfc = issfilterkey.getFcViewBean();

		/////////get state///

		sortstate = issfilterkey.getSortState();

		sb.append("<form name=\"filteriss\" method=\"post\" action=\"EtsIssFilterCntrlServlet.wss?linkid=" + linkid + "&tc=" + tc + "&proj=" + etsprojid + "&istyp=" + istype + "&opn=" + issopn + "\" ");
		sb.append("<tr>\n");

		//for sort by cq trk id//
		sb.append("<th id=\"issid00\" align=\"left\" valign=\"top\" width=\"80\"> \n");

		//sort by title//
		sb.append(printSortColumns("issue_sort_trkid", sortstate, SORTTRKID_A, SORTTRKID_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTrkId() + "</span></th>\n");
		sb.append("<th id=\"issid01\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid1\" align=\"left\" valign=\"top\" width=\"150\">\n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_title", sortstate, SORTISSUETITLE_A, SORTISSUETITLE_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTitleName() + "</span></th>\n");
		sb.append("<th id=\"issid2\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid5\" align=\"left\" valign=\"top\" width=\"180\"> \n");
		//sort by submitter
		sb.append(printSortColumns("issue_sort_submitter", sortstate, SORTSUBMITTER_A, SORTSUBMITTER_D));

		sb.append("<span class=\"small\">" + issviewrep.getRepSubName() + "</span></th>\n");

		sb.append("<th id=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");
		sb.append("<th id=\"issid9\" align=\"left\" valign=\"top\" width=\"80\" nowrap=\"nowrap\"> \n");

		//SORT BY svereity
		sb.append(printSortColumns("issue_sort_severity", sortstate, SORTSEVERITY_A, SORTSEVERITY_D));

		sb.append("<span class=\"small\">" + issviewrep.getRepSeverityName() + "</span></th>\n");
		sb.append("<th id=\"issid10\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		sb.append("<th id=\"issid11\" align=\"left\" valign=\"top\" width=\"80\" nowrap=\"nowrap\"> " + "\n");

		//sort by status
		sb.append(printSortColumns("issue_sort_status", sortstate, SORTSTATUS_A, SORTSTATUS_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepStatusName() + "</span></th>\n");
		sb.append("</tr> \n");

		sb.append("</form>\n");

		return sb.toString();

	}

	/**
			 * 
			 * This is for printing sort header for REP ALL for External only
			 * @param issviewrep
			 * @param issfilterkey
			 * @return
			 */

	public String printCrFilterSortOrderISub(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();
		int sortstate = 0;

		//////

		//	get common params//
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		String frmopn = opnqual + "2";

		EtsFilterCondsViewParamsBean issviewfc = issfilterkey.getFcViewBean();

		/////////get state///

		sortstate = issfilterkey.getSortState();

		sb.append("<form name=\"filteriss\" method=\"post\" action=\"EtsIssFilterCntrlServlet.wss?linkid=" + linkid + "&tc=" + tc + "&proj=" + etsprojid + "&istyp=" + istype + "&opn=" + issopn + "\" ");
		sb.append("<tr>\n");

		//		for sort by cq trk id//
		sb.append("<th id=\"issid00\" align=\"left\" valign=\"top\" width=\"80\"> \n");

		//sort by title//
		sb.append(printSortColumns("issue_sort_trkid", sortstate, SORTTRKID_A, SORTTRKID_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTrkId() + "</span></th>\n");
		sb.append("<th id=\"issid01\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid1\" align=\"left\" valign=\"top\" width=\"230\">\n");

		// sort by title//
		sb.append(printSortColumns("issue_sort_title", sortstate, SORTISSUETITLE_A, SORTISSUETITLE_D));
		/////
		sb.append("<span class=\"small\">" + issviewrep.getRepTitleName() + "</span></th>\n");
		sb.append("<th id=\"issid2\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid9\" align=\"left\" valign=\"top\" width=\"80\" nowrap=\"nowrap\"> \n");

		//SORT BY svereity
		sb.append(printSortColumns("issue_sort_severity", sortstate, SORTSEVERITY_A, SORTSEVERITY_D));

		sb.append("<span class=\"small\">" + issviewrep.getRepSeverityName() + "</span></th>\n");
		sb.append("<th id=\"issid10\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		sb.append("<th id=\"issid11\" align=\"left\" valign=\"top\" width=\"80\" nowrap=\"nowrap\"> " + "\n");

		//sort by status
		sb.append(printSortColumns("issue_sort_status", sortstate, SORTSTATUS_A, SORTSTATUS_D));
		sb.append("<span class=\"small\">" + issviewrep.getRepStatusName() + "</span></th>\n");
		sb.append("</tr> \n");

		sb.append("</form>\n");

		return sb.toString();

	}

	/***
				 * 
				 * to print common actions(filter conds, and back ) on no recs pages
				 * 
				 */

	public String printPrinterFriendly(EtsIssFilterObjectKey issfilterkey, String issueProblemId) {

		String filopn = issfilterkey.getOpn();

		String viewUrl = "ETSProjectsServlet.wss?proj=" + issfilterkey.getProjectId() + "&tc=" + issfilterkey.getTc() + "&sc=0&istyp=iss&linkid=" + issfilterkey.getLinkid() + "&actionType=viewIssue&op=60&edge_problem_id=" + issueProblemId + "&flop=" + filopn + "&prnt=Y";

		return comGuiUtils.printPrinterFriendly(viewUrl);

	}

	/**
	 * 
	 * @return
	 */

	public String printIssRepWorkAll(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey, ArrayList issueRepTabList) {

		StringBuffer sb = new StringBuffer();

		try {

			String etsprojid = issfilterkey.getProjectId();
			String istype = issfilterkey.getProblemType();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
			String issopn = issfilterkey.getOpn();
			HashMap propMap = issfilterkey.getPropMap();
			String opnqual = issopn.substring(0, 1);
			EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
			int srt = issfilterkey.getSortState();

			int repsize = 0;

			if (issueRepTabList != null && !issueRepTabList.isEmpty()) {

				repsize = issueRepTabList.size();

			}

			String issueProblemId = "";
			String issueCqTrkId = "";
			String issueTitle = "";
			String issueType = "";
			String issueSeverity = "";
			String issueStatus = "";
			String issueSubmitter = "";
			String issueSubmitterName = "";
			String issueLastTime = "";
			String issueCurOwnerName = "";
			String issueCurOwnerId = "";
			String refId = "";

			String bgcolor = "background-color:#eeeeee";

			String tmpIssueProblemId = "";
			boolean chgcolor = true;
			String lastTmpRowColor = "";
			int count = 0;

			List selIssIdsList = getPrevSelectedIssuesList(issfilterkey);

			sb.append("<table summary=\"Issues info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"680\" border=\"0\">\n");
			sb.append("<tbody>\n");

			sb.append(printIssFilterSortHeader(issviewrep, issfilterkey));
			sb.append(printMultiIssueForm(issfilterkey));

			for (int r = 0; r < repsize; r++) {

				EtsIssFilterRepTabBean etsreptab = (EtsIssFilterRepTabBean) issueRepTabList.get(r);

				issueProblemId = etsreptab.getIssueProblemId();
				issueCqTrkId = etsreptab.getIssueCqTrkId();
				refId = etsreptab.getRefId();
				issueTitle = etsreptab.getIssueTitle();
				issueType = etsreptab.getIssueType();
				issueSeverity = etsreptab.getIssueSeverity();
				issueStatus = etsreptab.getIssueStatus();
				issueSubmitter = etsreptab.getIssueSubmitter();
				issueSubmitterName = etsreptab.getIssueSubmitterName();
				issueLastTime = etsreptab.getIssueLastTime();
				issueCurOwnerName = etsreptab.getCurrentOwnerName();
				issueCurOwnerId = etsreptab.getCurrentOwnerId();

				if (tmpIssueProblemId.equals(issueProblemId)) {

					chgcolor = false;

				} else {

					chgcolor = true;

				}

				if (!chgcolor) {

					bgcolor = lastTmpRowColor;

				}

				if (bgcolor.equals("background-color:#eeeeee")) {

					sb.append("<tr style=" + bgcolor + ">");

				} else {

					sb.append("<tr>\n");

				}

				sb.append(printCheckBox(selIssIdsList, issueProblemId, issueTitle, r));

				if (!chgcolor) {

					sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"50\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				} else {

					sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"50\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + printViewIssueLink(issfilterkey, issueProblemId, refId) + "</span></td>");

				}

				sb.append("<td headers=\"issid01\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
                //surya value of width changed from width=\"85\" 
				sb.append("<td headers=\"issid1\" align=\"left\" valign=\"top\" width=\"170\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueTitle + "</span></td>");

				sb.append("<td headers=\"issid2\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid3\" align=\"left\" valign=\"top\" width=\"65\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueType + "</span></td>");
				sb.append("<td headers=\"issid4\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				
				//donot show submitter and owner details if proj is of blade type and customer is external

				sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"98\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + printUserInfoPopup(issueSubmitter, issueSubmitterName, "sub") + "</span></td>");
				sb.append("<td headers=\"issid6\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				//donot show submitter and owner details if proj is of blade type and customer is external

				if (usrRolesModel.isShowOwnerName()) {

					if (issueCurOwnerName.equals("No owner")) {

						sb.append("<td headers=\"issid7\" align=\"left\" valign=\"top\" width=\"100\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueCurOwnerName + "</span></td>");

					} else {

						sb.append("<td headers=\"issid7\" align=\"left\" valign=\"top\" width=\"100\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + printUserInfoPopup(issueCurOwnerId, issueCurOwnerName, "cown") + "</span></td>");

					}

				}

				sb.append("<td headers=\"issid8\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid9\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"75\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueSeverity + "</span></td>");
				sb.append("<td headers=\"issid10\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid11\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"60\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueStatus + "</span></td>");

				sb.append("</tr>");

				lastTmpRowColor = bgcolor;

				if (bgcolor.equals("background-color:#eeeeee")) {

					bgcolor = "";

				} else {

					bgcolor = "background-color:#eeeeee";

				}

				tmpIssueProblemId = issueProblemId;
				count++;

			}

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"13\" width=\"600\"><img src=\"//www.ibm.com/i/c.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
			sb.append("</tr>");

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"13\" width=\"600\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
			sb.append("</tr>");

			sb.append("</tbody>");
			sb.append("</table>");
			sb.append("<input type=\"hidden\" name=\"repcount\" value=\"" + count + "\" />");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssFilterGuiUtils", ETSLSTUSR);

			if (ex != null) {
				logger.error("Exception in printIssRepWorkAll", ex);
				ex.printStackTrace();

			}

		} finally {

		}

		return sb.toString();

	}

	/***
		 * 
		 * prints common action link across issue pages
		 */

	public String printModifySearchLink(EtsIssFilterObjectKey issobjkey, String actionName, String opn) {

		StringBuffer sb = new StringBuffer();

		sb.append(" <a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + issobjkey.getProblemType() + "&opn=" + opn + "&proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&tc=" + issobjkey.getTc() + "\">" + actionName + "</a>");

		return sb.toString();
	}

	/**
	 * to print check box to save qry
	 * @param issobjkey
	 * @return
	 */

	public String printChkSaveQry(EtsIssFilterObjectKey issobjkey, EtsIssFilterDetailsBean etsIssFilDetails) {

		StringBuffer sb = new StringBuffer();

		String chkSaveQry = AmtCommonUtils.getTrimStr(etsIssFilDetails.getChkSaveQry());

		sb.append("<table summary=\"save_qry\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"200\"> ");
		sb.append("<tbody> ");
		sb.append("<tr > ");
		sb.append("<td valign=\"top\" align=\"left\">");
		sb.append("<label for=\"ad\"><img src=\"//www.ibm.com/i/c.gif\" border=\"0\" width=\"1\" height=\"1\" alt=\"checkbox to save search criteria\" /></label> ");

		if (chkSaveQry.equals("Y")) {

			sb.append(AmtErrorHandler.printImgLabel("lblchkidsaveqry", "Save search criteria"));
			sb.append("<input id=\"lblchkidsaveqry\" align=\"left\" name=\"chk_save_qry\" type=\"checkbox\" value=\"Y\" class=\"iform\" checked=\"checked\" />  ");

		} else {

			sb.append(AmtErrorHandler.printImgLabel("lblchkidsaveqry", "Save search criteria"));
			sb.append("<input id=\"lblchkidsaveqry\" align=\"left\" name=\"chk_save_qry\" type=\"checkbox\" value=\"Y\" class=\"iform\" />  ");

		}

		sb.append("</td> ");
		sb.append("<td valign=\"top\" align=\"left\"><b>Save search criteria</b></td> ");
		sb.append("</tr> ");
		sb.append("</tbody> ");
		sb.append("</table> ");

		return sb.toString();

	}

	/**
		 * to print check box to save qry
		 * @param issobjkey
		 * @return
		 */

	public String printChkSaveQryTab(EtsIssFilterObjectKey issfilterkey, EtsIssFilterDetailsBean etsIssFilDetails) {

		StringBuffer sb = new StringBuffer();

		sb.append("<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n ");
		sb.append("<tbody> ");
		sb.append("<tr bgcolor=\"#cccccc\" height=\"12\"> ");
		sb.append("<th valign=\"top\" align=\"left\" id=\"fcid9\" width=\"320\">Save filter conditions</th> ");
		sb.append("</tr> ");

		sb.append("<tr><td>");
		sb.append("Please click the check box to save the selected filter conditions as default search criteria.");
		sb.append("</td></tr> ");

		sb.append("<tr> ");
		sb.append("<td  valign=\"top\" align=\"left\" width=\"280\">\n");
		sb.append(printChkSaveQry(issfilterkey, etsIssFilDetails));
		sb.append("</td> ");
		sb.append("</tr> ");

		sb.append("</tbody> ");
		sb.append("</table> ");

		return sb.toString();

	}

	/**
			 * print grey line
			 */

	public String printGreyLine() {

		return comGuiUtils.printGreyLine();

	}

	/***
		 * print no recs msg
		 */

	public String printNoRecsMsgForSrchByNum(EtsIssFilterObjectKey issobjkey) {

		StringBuffer sb = new StringBuffer();

		String istype = issobjkey.getProblemType();
		int state = issobjkey.getState();

		//sb.append("<%-- table 7 for no issues info details info starts --%>");

		sb.append("<table summary=\"No Issues info details\" cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
		sb.append("<tbody>");

		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"5\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
		sb.append("</tr>");

		sb.append("<tr>");

		sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("filter.norecs.iss.srchbynum.msg") + "</span></td>");
		sb.append("</tr>");

		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"5\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
		sb.append("</tr>");

		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 7 for no issues info details info end --%>");

		return sb.toString();

	}

	/**
	 * 
	 * @param issviewrep
	 * @return
	 */

	public String printRepHeader(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issobjkey) {

		StringBuffer sb = new StringBuffer();
		String srchByNum = AmtCommonUtils.getTrimStr((String) issobjkey.getParams().get("isssrchnum"));
		
		sb.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		sb.append("<tr>");
		//v2sagar
		//sb.append("<td class=\"small\"><b>" + issviewrep.getRepHeaderName() + "</b></td>");
		sb.append("</tr>");

		if (AmtCommonUtils.isResourceDefined(srchByNum)) {

			sb.append("<tr>");
			sb.append("<td>&nbsp; </td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td class=\"small\">Search results matching with <b> ID : '" + srchByNum + "'</b></td>");
			sb.append("</tr>");
		}

		sb.append("<tr>");
		sb.append("<td height=\"21\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "gray_dotted_line.gif\" ");
		sb.append("height=\"1\" width=\"600\" alt=\"\" /></td>");
		sb.append("</tr>");
		sb.append("</table>");

		return sb.toString();

	}

	/**
	 * 
	 * @return
	 */

	public String printMultipleIssues() {

		StringBuffer sb = new StringBuffer();

		sb.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"15%\">\n");
		sb.append("<tr><td  align=\"left\"><input type=\"image\"  name=\"printmulti\" src=\"" + Defines.BUTTON_ROOT + "view_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Print Multiple issues\" /></a></td>\n");
		sb.append("<td  align=\"left\" nowrap=\"nowrap\" >&nbsp;<span class=\"small\">Print selected issues</span></td></tr>\n");
		sb.append("</table>\n");

		return sb.toString();

	}

	/***
			 * print no recs msg
			 */

	public String printNoRecsMsgForMultiIssues(EtsIssFilterObjectKey issobjkey) {

		StringBuffer sb = new StringBuffer();

		String istype = issobjkey.getProblemType();
		int state = issobjkey.getState();

		//sb.append("<%-- table 7 for no issues info details info starts --%>");

		sb.append("<table summary=\"No Issues info details\" cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
		sb.append("<tbody>");

		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"5\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
		sb.append("</tr>");

		sb.append("<tr>");

		sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("print.multiissues.norecs") + "</span></td>");
		sb.append("</tr>");

		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"5\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
		sb.append("</tr>");

		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 7 for no issues info details info end --%>");

		return sb.toString();

	}

	public String printMultiIssueForm(EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();

		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
		int srt = issfilterkey.getSortState();
		int flop = issfilterkey.getFlopstate();

		String reqUrl = "EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=" + istype + "&opn=" + issopn + "&tc=" + tc + "&err=Y&flop=" + issopn + "&srt=" + srt + "";

		logger.debug("req url===" + reqUrl);

		sb.append("<form name=\"printrep\" method=\"post\" action=\"printMultiIssues.wss?linkid=" + linkid + "&opn=" + issopn + "&tc=" + tc + "&actionType=viewIssue&proj=" + etsprojid + "&istyp=" + istype + "\" >");
		sb.append("<input type=\"hidden\" name=\"srt\" value=\"" + srt + "\" />");
		sb.append("<input type=\"hidden\" name=\"bkUrl\" value=\"" + reqUrl + "\" />");
		sb.append("<input type=\"hidden\" name=\"flop\" value=\"" + issopn + "\" />");

		return sb.toString();

	}

	/**
	 * 
	 * @param issviewrep
	 * @param issfilterkey
	 * @param issueRepTabList
	 * @return
	 */

	public String printIssRepWorkAllExt(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey, ArrayList issueRepTabList) {

		StringBuffer sb = new StringBuffer();

		try {

			String etsprojid = issfilterkey.getProjectId();
			String istype = issfilterkey.getProblemType();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
			String issopn = issfilterkey.getOpn();
			HashMap propMap = issfilterkey.getPropMap();
			String opnqual = issopn.substring(0, 1);
			EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
			int srt = issfilterkey.getSortState();

			int repsize = 0;

			if (issueRepTabList != null && !issueRepTabList.isEmpty()) {

				repsize = issueRepTabList.size();

			}

			String issueProblemId = "";
			String issueCqTrkId = "";
			String issueTitle = "";
			String issueType = "";
			String issueSeverity = "";
			String issueStatus = "";
			String issueSubmitter = "";
			String issueSubmitterName = "";
			String issueLastTime = "";
			String issueCurOwnerName = "";
			String issueCurOwnerId = "";
			String refId = "";

			String bgcolor = "background-color:#eeeeee";

			List selIssIdsList = getPrevSelectedIssuesList(issfilterkey);

			sb.append("<table summary=\"Issues info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
			sb.append("<tbody>");

			sb.append(printIssFilterSortOrderForWAllExt(issviewrep, issfilterkey));
			sb.append(printMultiIssueForm(issfilterkey));

			for (int r = 0; r < repsize; r++) {

				EtsIssFilterRepTabBean etsreptab = (EtsIssFilterRepTabBean) issueRepTabList.get(r);

				issueProblemId = etsreptab.getIssueProblemId();
				issueCqTrkId = etsreptab.getIssueCqTrkId();
				issueTitle = etsreptab.getIssueTitle();
				issueType = etsreptab.getIssueType();
				issueSeverity = etsreptab.getIssueSeverity();
				issueStatus = etsreptab.getIssueStatus();
				issueSubmitter = etsreptab.getIssueSubmitter();
				issueSubmitterName = etsreptab.getIssueSubmitterName();
				issueLastTime = etsreptab.getIssueLastTime();
				issueCurOwnerName = etsreptab.getCurrentOwnerName();
				issueCurOwnerId = etsreptab.getCurrentOwnerId();
				refId = etsreptab.getRefId();

				if (bgcolor.equals("background-color:#eeeeee")) {

					sb.append("<tr style=\"" + bgcolor + "\" >");

				} else {

					sb.append("<tr>");

				}

				sb.append(printCheckBox(selIssIdsList, issueProblemId, issueTitle, r));
				sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"50\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + printViewIssueLink(issfilterkey, issueProblemId, refId) + "</span></td>");
				sb.append("<td headers=\"issid01\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid1\" align=\"left\" valign=\"top\" width=\"120\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueTitle + "</span></td>");
				sb.append("<td headers=\"issid2\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid3\" align=\"left\" valign=\"top\" width=\"113\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueType + "</span></td>");
				sb.append("<td headers=\"issid4\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				if (!issfilterkey.isProjBladeType()) {

					sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"140\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + printUserInfoPopup(issueSubmitter, issueSubmitterName, "sub") + "</span></td>");
					sb.append("<td headers=\"issid8\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				} else {

					if (usrRolesModel.isBladeUsrInt()) {

						sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"140\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + printUserInfoPopup(issueSubmitter, issueSubmitterName, "sub") + "</span></td>");
						sb.append("<td headers=\"issid8\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					}

				}

				sb.append("<td headers=\"issid9\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"80\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueSeverity + "</span></td>");
				sb.append("<td headers=\"issid10\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid11\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"60\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueStatus + "</span></td>");

				sb.append("</tr>");

				if (bgcolor.equals("background-color:#eeeeee")) {

					bgcolor = "";

				} else {

					bgcolor = "background-color:#eeeeee";

				}

			}

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"11\" width=\"600\"><img src=\"//www.ibm.com/i/c.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
			sb.append("</tr>");

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"11\" width=\"600\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
			sb.append("</tr>");

			sb.append("</tbody>");
			sb.append("</table>");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssFilterGuiUtils", ETSLSTUSR);

			if (ex != null) {
				logger.error("Exception in printIssRepWorkAllExt", ex);
				ex.printStackTrace();

			}

		} finally {

		}

		return sb.toString();

	}

	/**
	 * 
	 * @param issfilterkey
	 * @return
	 */

	public String printErrMsgOnMultiIssues(EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();

		String showErr = AmtCommonUtils.getTrimStr((String) issfilterkey.getParams().get("err"));

		if (showErr.equals("Y")) {

			sb.append(comGuiUtils.printStdErrMsg("Please select atleast one issue to print."));
			sb.append("<br />");

		}

		return sb.toString();

	}
	/**
	 * 
	 * @param issfilterkey
	 * @return
	 */

	public List getPrevSelectedIssuesList(EtsIssFilterObjectKey issfilterkey) {

		List selIssIdsList = new ArrayList();

		String chkissid = AmtCommonUtils.getTrimStr((String) issfilterkey.getParams().get("chkissid"));

		String tempTokIdStr = AmtCommonUtils.getTrimStr((String) issfilterkey.getParams().get("selectedIssues"));

		if (AmtCommonUtils.isResourceDefined(chkissid)) {

			selIssIdsList = EtsIssFilterUtils.getArrayListFromStringTok(tempTokIdStr, ",");

		}

		return selIssIdsList;
	}

	/**
	 * 
	 * @param selIssIdsList
	 * @param issueProblemId
	 * @param issueTitle
	 * @param r
	 * @return
	 */

	public String printCheckBox(List selIssIdsList, String issueProblemId, String issueTitle, int r) {

		StringBuffer sb = new StringBuffer();

		sb.append("<td headers=\"issid-1\" align=\"left\" valign=\"top\" width=\"15\" style=\"height:21\" height=\"21\" ><span class=\"small\">");
		//sb.append("<label for=\"lblprint" + r + "\"><img src=\"//www.ibm.com/i/c.gif\" border=\"0\" width=\"1\" height=\"1\" alt=\"Please click checkbox to print issue title " + issueTitle + "\" /></label> ");

		if (selIssIdsList.contains(issueProblemId)) {

			sb.append("<input type=\"checkbox\" name=\"issueList\" value=\"" + issueProblemId + "\"  class=\"iform\"  checked=\"checked\" id=\"lblprint" + r + "\" />");
		} else {

			sb.append("<input type=\"checkbox\" name=\"issueList\" value=\"" + issueProblemId + "\"  class=\"iform\"  id=\"lblprint" + r + "\" />");

		}

		sb.append("</span></td>");
		sb.append("<td headers=\"issid-11\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

		return sb.toString();

	}

	/**
	 * 
	 * @return
	 */

	public String printThForChkBox() {

		StringBuffer sb = new StringBuffer();

		//	for check box
		sb.append("<th id=\"issid-1\" align=\"left\" valign=\"top\" width=\"15\">&nbsp;</th>\n");
		sb.append("<th id=\"issid-11\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		return sb.toString();

	}

	/**
		 * 
		 * @param issviewrep
		 * @param issfilterkey
		 * @param issueRepTabList
		 * @return
		 */

	public String printIssRepISub(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey, ArrayList issueRepTabList) {

		StringBuffer sb = new StringBuffer();

		try {

			String etsprojid = issfilterkey.getProjectId();
			String istype = issfilterkey.getProblemType();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
			String issopn = issfilterkey.getOpn();
			HashMap propMap = issfilterkey.getPropMap();
			String opnqual = issopn.substring(0, 1);
			EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
			int srt = issfilterkey.getSortState();

			int repsize = 0;

			if (issueRepTabList != null && !issueRepTabList.isEmpty()) {

				repsize = issueRepTabList.size();

			}

			String issueProblemId = "";
			String issueCqTrkId = "";
			String issueTitle = "";
			String issueType = "";
			String issueSeverity = "";
			String issueStatus = "";
			String issueSubmitter = "";
			String issueSubmitterName = "";
			String issueLastTime = "";
			String issueCurOwnerName = "";
			String issueCurOwnerId = "";
			String refId = "";

			String tmpIssueProblemId = "";
			boolean chgcolor = true;
			String lastTmpRowColor = "";

			String bgcolor = "background-color:#eeeeee";

			List selIssIdsList = getPrevSelectedIssuesList(issfilterkey);

			sb.append("<table summary=\"Issues info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			sb.append("<tbody>");

			sb.append(printIssFilterSortOrderForISub(issviewrep, issfilterkey));
			sb.append(printMultiIssueForm(issfilterkey));

			for (int r = 0; r < repsize; r++) {

				EtsIssFilterRepTabBean etsreptab = (EtsIssFilterRepTabBean) issueRepTabList.get(r);

				issueProblemId = etsreptab.getIssueProblemId();
				issueCqTrkId = etsreptab.getIssueCqTrkId();
				issueTitle = etsreptab.getIssueTitle();
				issueType = etsreptab.getIssueType();
				issueSeverity = etsreptab.getIssueSeverity();
				issueStatus = etsreptab.getIssueStatus();
				issueSubmitter = etsreptab.getIssueSubmitter();
				issueSubmitterName = etsreptab.getIssueSubmitterName();
				issueLastTime = etsreptab.getIssueLastTime();
				issueCurOwnerName = etsreptab.getCurrentOwnerName();
				issueCurOwnerId = etsreptab.getCurrentOwnerId();
				refId = etsreptab.getRefId();

				if (tmpIssueProblemId.equals(issueProblemId)) {

					chgcolor = false;

				} else {

					chgcolor = true;

				}

				if (!chgcolor) {

					bgcolor = lastTmpRowColor;

				}

				if (bgcolor.equals("background-color:#eeeeee")) {

					sb.append("<tr style=\"" + bgcolor + "\">");

				} else {

					sb.append("<tr>");

				}

				sb.append(printCheckBox(selIssIdsList, issueProblemId, issueTitle, r));

				if (!chgcolor) {

					sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				} else {
					sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"  style=\"height:21\" height=\"21\" >");
					
					sb.append("<span class=\"small\">" + printViewIssueLink(issfilterkey, issueProblemId, refId) + "</span>");
					
					sb.append("</td>");

				}

				sb.append("<td headers=\"issid01\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid1\" align=\"left\" valign=\"top\" width=\"130\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueTitle + "</span></td>");
				sb.append("<td headers=\"issid2\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid3\" align=\"left\" valign=\"top\" width=\"123\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueType + "</span></td>");
				sb.append("<td headers=\"issid4\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				if (usrRolesModel.isShowOwnerName()) {

					if (issueCurOwnerName.equals("No owner")) {

						sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"120\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueCurOwnerName + "</span></td>");

					} else {

						sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"120\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + printUserInfoPopup(issueCurOwnerId, issueCurOwnerName, "cown") + "</span></td>");

					}

					sb.append("<td headers=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				}

				sb.append("<td headers=\"issid9\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"80\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueSeverity + "</span></td>");
				sb.append("<td headers=\"issid10\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid11\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"60\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueStatus + "</span></td>");

				sb.append("</tr>");

				lastTmpRowColor = bgcolor;

				if (bgcolor.equals("background-color:#eeeeee")) {

					bgcolor = "";

				} else {

					bgcolor = "background-color:#eeeeee";

				}

				tmpIssueProblemId = issueProblemId;

			}

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"11\" width=\"600\"><img src=\"//www.ibm.com/i/c.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
			sb.append("</tr>");

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"11\" width=\"600\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
			sb.append("</tr>");

			sb.append("</tbody>");
			sb.append("</table>");
		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssFilterGuiUtils", ETSLSTUSR);

			if (ex != null) {
				logger.error("Exception in printIssRepISub", ex);
				ex.printStackTrace();

			}

		} finally {

		}

		return sb.toString();

	}
	/**
	 * 
	 * @param issviewrep
	 * @param issfilterkey
	 * @param issueRepTabList
	 * @return
	 */

	public String printIssRepISubExt(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey, ArrayList issueRepTabList) {

		StringBuffer sb = new StringBuffer();

		try {

			String etsprojid = issfilterkey.getProjectId();
			String istype = issfilterkey.getProblemType();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
			String issopn = issfilterkey.getOpn();
			HashMap propMap = issfilterkey.getPropMap();
			String opnqual = issopn.substring(0, 1);
			EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
			int srt = issfilterkey.getSortState();

			int repsize = 0;

			if (issueRepTabList != null && !issueRepTabList.isEmpty()) {

				repsize = issueRepTabList.size();

			}

			String issueProblemId = "";
			String issueCqTrkId = "";
			String issueTitle = "";
			String issueType = "";
			String issueSeverity = "";
			String issueStatus = "";
			String issueSubmitter = "";
			String issueSubmitterName = "";
			String issueLastTime = "";
			String issueCurOwnerName = "";
			String issueCurOwnerId = "";
			String refId = "";

			String tmpIssueProblemId = "";
			boolean chgcolor = true;
			String lastTmpRowColor = "";

			String bgcolor = "background-color:#eeeeee";

			List selIssIdsList = getPrevSelectedIssuesList(issfilterkey);

			sb.append("<table summary=\"Issues info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			sb.append("<tbody>");

			sb.append(printIssFilterSortOrderForISubExt(issviewrep, issfilterkey));
			sb.append(printMultiIssueForm(issfilterkey));

			for (int r = 0; r < repsize; r++) {

				EtsIssFilterRepTabBean etsreptab = (EtsIssFilterRepTabBean) issueRepTabList.get(r);

				issueProblemId = etsreptab.getIssueProblemId();
				issueCqTrkId = etsreptab.getIssueCqTrkId();
				issueTitle = etsreptab.getIssueTitle();
				issueType = etsreptab.getIssueType();
				issueSeverity = etsreptab.getIssueSeverity();
				issueStatus = etsreptab.getIssueStatus();
				issueSubmitter = etsreptab.getIssueSubmitter();
				issueSubmitterName = etsreptab.getIssueSubmitterName();
				issueLastTime = etsreptab.getIssueLastTime();
				issueCurOwnerName = etsreptab.getCurrentOwnerName();
				issueCurOwnerId = etsreptab.getCurrentOwnerId();
				refId = etsreptab.getRefId();

				if (bgcolor.equals("background-color:#eeeeee")) {

					sb.append("<tr style=\"" + bgcolor + "\" >");

				} else {

					sb.append("<tr>");

				}

				sb.append(printCheckBox(selIssIdsList, issueProblemId, issueTitle, r));
				sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + printViewIssueLink(issfilterkey, issueProblemId, refId) + "</span></td>");
				sb.append("<td headers=\"issid01\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid1\" align=\"left\" valign=\"top\" width=\"165\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueTitle + "</span></td>");
				sb.append("<td headers=\"issid2\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid3\" align=\"left\" valign=\"top\" width=\"168\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueType + "</span></td>");
				sb.append("<td headers=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid9\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"80\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueSeverity + "</span></td>");
				sb.append("<td headers=\"issid10\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid11\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"70\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueStatus + "</span></td>");

				sb.append("</tr>");

				if (bgcolor.equals("background-color:#eeeeee")) {

					bgcolor = "";

				} else {

					bgcolor = "background-color:#eeeeee";

				}

			}

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"9\" width=\"600\"><img src=\"//www.ibm.com/i/c.gif\" alt=\"\" width=\"600\" height=\"1\"/></td>");
			sb.append("</tr>");

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"9\" width=\"600\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
			sb.append("</tr>");

			sb.append("</tbody>");
			sb.append("</table>");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssFilterGuiUtils", ETSLSTUSR);

			if (ex != null) {
				logger.error("Exception in printIssRepISubEXt", ex);
				ex.printStackTrace();

			}

		} finally {

		}

		return sb.toString();

	}

	/**
		 * 
		 * @param issviewrep
		 * @param issfilterkey
		 * @param issueRepTabList
		 * @return
		 */

	public String printIssRepAsgndMe(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey, ArrayList issueRepTabList) {

		StringBuffer sb = new StringBuffer();

		try {

			String etsprojid = issfilterkey.getProjectId();
			String istype = issfilterkey.getProblemType();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
			String issopn = issfilterkey.getOpn();
			HashMap propMap = issfilterkey.getPropMap();
			String opnqual = issopn.substring(0, 1);
			EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
			int srt = issfilterkey.getSortState();

			int repsize = 0;

			if (issueRepTabList != null && !issueRepTabList.isEmpty()) {

				repsize = issueRepTabList.size();

			}

			String issueProblemId = "";
			String issueCqTrkId = "";
			String issueTitle = "";
			String issueType = "";
			String issueSeverity = "";
			String issueStatus = "";
			String issueSubmitter = "";
			String issueSubmitterName = "";
			String issueLastTime = "";
			String issueCurOwnerName = "";
			String issueCurOwnerId = "";
			String refId = "";

			String tmpIssueProblemId = "";
			boolean chgcolor = true;
			String lastTmpRowColor = "";

			String bgcolor = "background-color:#eeeeee";

			List selIssIdsList = getPrevSelectedIssuesList(issfilterkey);

			sb.append("<table summary=\"Issues info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			sb.append("<tbody>");

			sb.append(printIssFilterSortOrderForAsgndMe(issviewrep, issfilterkey));
			sb.append(printMultiIssueForm(issfilterkey));

			for (int r = 0; r < repsize; r++) {

				EtsIssFilterRepTabBean etsreptab = (EtsIssFilterRepTabBean) issueRepTabList.get(r);

				issueProblemId = etsreptab.getIssueProblemId();
				issueCqTrkId = etsreptab.getIssueCqTrkId();
				issueTitle = etsreptab.getIssueTitle();
				issueType = etsreptab.getIssueType();
				issueSeverity = etsreptab.getIssueSeverity();
				issueStatus = etsreptab.getIssueStatus();
				issueSubmitter = etsreptab.getIssueSubmitter();
				issueSubmitterName = etsreptab.getIssueSubmitterName();
				issueLastTime = etsreptab.getIssueLastTime();
				issueCurOwnerName = etsreptab.getCurrentOwnerName();
				issueCurOwnerId = etsreptab.getCurrentOwnerId();
				refId = etsreptab.getRefId();

				if (bgcolor.equals("background-color:#eeeeee")) {

					sb.append("<tr style=\"" + bgcolor + "\">");

				} else {

					sb.append("<tr>");

				}

				sb.append(printCheckBox(selIssIdsList, issueProblemId, issueTitle, r));
				sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + printViewIssueLink(issfilterkey, issueProblemId, refId) + "</span></td>");
				sb.append("<td headers=\"issid01\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid1\" align=\"left\" valign=\"top\" width=\"120\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueTitle + "</span></td>");
				sb.append("<td headers=\"issid2\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid3\" align=\"left\" valign=\"top\" width=\"110\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueType + "</span></td>");
				sb.append("<td headers=\"issid4\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				if (!issfilterkey.isProjBladeType()) {

					sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"123\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + printUserInfoPopup(issueSubmitter, issueSubmitterName, "sub") + "</span></td>");
					sb.append("<td headers=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				} else {

					if (usrRolesModel.isBladeUsrInt()) {

						sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"123\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + printUserInfoPopup(issueSubmitter, issueSubmitterName, "sub") + "</span></td>");
						sb.append("<td headers=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					}

				}

				sb.append("<td headers=\"issid9\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"90\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueSeverity + "</span></td>");
				sb.append("<td headers=\"issid10\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid11\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"60\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueStatus + "</span></td>");

				sb.append("</tr>");

				if (bgcolor.equals("background-color:#eeeeee")) {

					bgcolor = "";

				} else {

					bgcolor = "background-color:#eeeeee";

				}

			}

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"11\" width=\"600\"><img src=\"//www.ibm.com/i/c.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
			sb.append("</tr>");

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"11\" width=\"600\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
			sb.append("</tr>");

			sb.append("</tbody>");
			sb.append("</table>");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssFilterGuiUtils", ETSLSTUSR);

			if (ex != null) {
				logger.error("Exception in printIssRepISubEXt", ex);
				ex.printStackTrace();

			}

		} finally {

		}

		return sb.toString();

	}
	
	/**
			 * 
			 * @param issviewrep
			 * @param issfilterkey
			 * @param issueRepTabList
			 * @return
			 */

		public String printIssRepISubTest(EtsFilterRepViewParamsBean issviewrep, EtsIssFilterObjectKey issfilterkey, ArrayList issueRepTabList) {

			StringBuffer sb = new StringBuffer();

			try {

				String etsprojid = issfilterkey.getProjectId();
				String istype = issfilterkey.getProblemType();
				String tc = issfilterkey.getTc();
				String linkid = issfilterkey.getLinkid();
				String issopn = issfilterkey.getOpn();
				HashMap propMap = issfilterkey.getPropMap();
				String opnqual = issopn.substring(0, 1);
				EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
				int srt = issfilterkey.getSortState();

				int repsize = 0;

				if (issueRepTabList != null && !issueRepTabList.isEmpty()) {

					repsize = issueRepTabList.size();

				}

				String issueProblemId = "";
				String issueCqTrkId = "";
				String issueTitle = "";
				String issueType = "";
				String issueSeverity = "";
				String issueStatus = "";
				String issueSubmitter = "";
				String issueSubmitterName = "";
				String issueLastTime = "";
				String issueCurOwnerName = "";
				String issueCurOwnerId = "";
				String refId = "";

				String tmpIssueProblemId = "";
				boolean chgcolor = true;
				String lastTmpRowColor = "";

				String bgcolor = "background-color:#eeeeee";

				List selIssIdsList = getPrevSelectedIssuesList(issfilterkey);

				sb.append("<table summary=\"Issues info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"1\">");
				sb.append("<tbody>");

				sb.append(printIssFilterSortOrderForISub(issviewrep, issfilterkey));
				sb.append(printMultiIssueForm(issfilterkey));

				for (int r = 0; r < repsize; r++) {

					EtsIssFilterRepTabBean etsreptab = (EtsIssFilterRepTabBean) issueRepTabList.get(r);

					issueProblemId = etsreptab.getIssueProblemId();
					issueCqTrkId = etsreptab.getIssueCqTrkId();
					issueTitle = etsreptab.getIssueTitle();
					issueType = etsreptab.getIssueType();
					issueSeverity = etsreptab.getIssueSeverity();
					issueStatus = etsreptab.getIssueStatus();
					issueSubmitter = etsreptab.getIssueSubmitter();
					issueSubmitterName = etsreptab.getIssueSubmitterName();
					issueLastTime = etsreptab.getIssueLastTime();
					issueCurOwnerName = etsreptab.getCurrentOwnerName();
					issueCurOwnerId = etsreptab.getCurrentOwnerId();
					refId = etsreptab.getRefId();

					if (tmpIssueProblemId.equals(issueProblemId)) {

						chgcolor = false;

					} else {

						chgcolor = true;

					}

					if (!chgcolor) {

						bgcolor = lastTmpRowColor;

					}

					if (bgcolor.equals("background-color:#eeeeee")) {

						sb.append("<tr style=\"" + bgcolor + "\">");

					} else {

						sb.append("<tr>");

					}

					//ssb.append(printCheckBox(selIssIdsList, issueProblemId, issueTitle, r));

					if (!chgcolor) {

						sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					} else {
						sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"  style=\"height:21\" height=\"21\" >");

						sb.append("<table summary=\"chk box details\" cellpadding=\"0\" cellspacing=\"0\" width=\"50\" border=\"1\">");
						sb.append("<tr  >");

						sb.append("<td width=\"15\"  align=\"right\" valign=\"top\"  >");
						//sb.append("<label for=\"lblprint" + r + "\"><img src=\"//www.ibm.com/i/c.gif\" border=\"0\" width=\"1\" height=\"1\" alt=\"Please click checkbox to print issue title " + issueTitle + "\" /></label> ");

						if (selIssIdsList.contains(issueProblemId)) {

							sb.append("<input type=\"checkbox\" name=\"issueList\" value=\"" + issueProblemId + "\"  class=\"iform\"  checked=\"checked\" id=\"lblprint" + r + "\" />");
						} else {

							sb.append("<input type=\"checkbox\" name=\"issueList\" value=\"" + issueProblemId + "\"  class=\"iform\"  id=\"lblprint" + r + "\" />");

						}

						sb.append("</td>");

						sb.append("<td align=\"left\" valign=\"top\"   width=\"35\"   >");

						sb.append("<span class=\"small\">" + printViewIssueLink(issfilterkey, issueProblemId, refId) + "</span>");
						sb.append("</td>");
						sb.append("</tr>");
						sb.append("</table>");
						sb.append("</td>");

					}

					sb.append("<td headers=\"issid01\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
					sb.append("<td headers=\"issid1\" align=\"left\" valign=\"top\" width=\"130\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueTitle + "</span></td>");
					sb.append("<td headers=\"issid2\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
					sb.append("<td headers=\"issid3\" align=\"left\" valign=\"top\" width=\"123\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueType + "</span></td>");
					sb.append("<td headers=\"issid4\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					if (usrRolesModel.isShowOwnerName()) {

						if (issueCurOwnerName.equals("No owner")) {

							sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"120\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueCurOwnerName + "</span></td>");

						} else {

							sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"120\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + printUserInfoPopup(issueCurOwnerId, issueCurOwnerName, "cown") + "</span></td>");

						}

						sb.append("<td headers=\"issid8\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					}

					sb.append("<td headers=\"issid9\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"80\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueSeverity + "</span></td>");
					sb.append("<td headers=\"issid10\" align=\"left\" valign=\"top\" width=\"2\"  style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
					sb.append("<td headers=\"issid11\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"60\"  style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueStatus + "</span></td>");

					sb.append("</tr>");

					lastTmpRowColor = bgcolor;

					if (bgcolor.equals("background-color:#eeeeee")) {

						bgcolor = "";

					} else {

						bgcolor = "background-color:#eeeeee";

					}

					tmpIssueProblemId = issueProblemId;

				}

				sb.append("<tr valign=\"top\">");
				sb.append("<td colspan=\"11\" width=\"600\"><img src=\"//www.ibm.com/i/c.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
				sb.append("</tr>");

				sb.append("<tr valign=\"top\">");
				sb.append("<td colspan=\"11\" width=\"600\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
				sb.append("</tr>");

				sb.append("</tbody>");
				sb.append("</table>");
			} catch (Exception ex) {

				AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssFilterGuiUtils", ETSLSTUSR);

				if (ex != null) {
					logger.error("Exception in printIssRepISub", ex);
					ex.printStackTrace();

				}

			} finally {

			}

			return sb.toString();

		}
		
	/***
	 * 	To get the Project Details 
	 */
	
	public ETSProj getProjectDetails(String sProjectId){
		
		CommonInfoDAO infoDAO = new CommonInfoDAO();
		ETSProj proj = new ETSProj();
		
		try{
			
			proj = infoDAO.getProjectDetails(sProjectId);
			
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return proj;
	}

} //end of class
