/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
package oem.edge.ets.fe.acmgt.helpers;

import oem.edge.ets.fe.acmgt.resources.WrkSpcTeamConstantsIF;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WrkSpcTeamGuiUtils implements WrkSpcTeamConstantsIF {
	
	public static final String VERSION = "1.10";
	private static Log logger = EtsLogger.getLogger(WrkSpcTeamGuiUtils.class);

	/**
	 * 
	 */
	public WrkSpcTeamGuiUtils() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param isMandatory
	 * @param isBold
	 * @param sperator
	 * @param labelString
	 * @return
	 */
	private String printLabel(boolean isMandatory, boolean isBold, String sperator, String labelString) {
		return ("<span style=\"" + (isBold ? "font-weight:bold" : "") + "\">" + (isMandatory ? "<span style=\"color:red\">*</span>" : "") + "" + labelString + sperator + "</span>");
	}

	/**
	 * 
	 * @param urlText
	 * @param urlLink
	 * @return
	 */
	private String printHyperLink(String urlText, String urlLink) {
		return ("<a class=\"fbox\" href=\"" + urlLink + "\">" + urlText + "</a>");
	}
	
	/**
	 * 
	 * @return
	 */

	public String printIbmLookUp() {

		
		return printPopUp("Look up e-mail address for IBMers", IBM_EMPLOYEE_LOOKUP_URL,"600","600");

	}
	
	/**
	 * 
	 * @param popupText
	 * @param popupUrl
	 * @return
	 */

	public String printPopUp(String popupText, String popupUrl,String width, String height) {

		StringBuffer sb = new StringBuffer();

		sb.append(
			"<a  href=\""
				+ popupUrl
				+ "\" target=\"new\" onclick=\"window.open('"
				+ popupUrl
				+ "','Services','toolbar=1,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width="+width+",height="+height+",left=387,top=207'); return false;\"  onkeypress=\"window.open('"
				+ popupUrl
				+ "','Services','toolbar=1,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width="+width+",height="+height+",left=387,top=207'); return false;\">"
				+ popupText
				+ "</a>");

		return sb.toString();

	}

} //end of class
