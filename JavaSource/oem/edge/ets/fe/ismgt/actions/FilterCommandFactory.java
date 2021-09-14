package oem.edge.ets.fe.ismgt.actions;

import oem.edge.common.*;
import oem.edge.amt.*;

import javax.servlet.*;
import javax.servlet.http.*;

import oem.edge.ets.fe.ismgt.resources.*;
import oem.edge.ets.fe.ismgt.model.*;
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
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FilterCommandFactory implements FilterCommandFactoryIF, EtsIssFilterConstants{

	/**
	 * Constructor for FilterCommandFactory.
	 */
	public FilterCommandFactory() {
		super();
	}
	
	public FilterCommandAbsBean createFilterCommand(HttpServletRequest request,HttpServletResponse response,EtsIssFilterObjectKey issobjkey) throws Exception {
		
		//get the state
		int state=issobjkey.getState();
		
		switch (state) {
			
			case ETSISSUEINITIAL:
			
			return new EtsIssFilterWelcomeCmd(request,response,issobjkey);
			
			case ETSISSTYPESWELCOME :
			
			return new EtsIssTypesWelcomeCmd(request,response,issobjkey);

			case ETSCUSTOMFIELDSWELCOME :
				
				return new EtsCustFieldsWelcomeCmd(request,response,issobjkey);

			case ETSISSRPTWALL :
			
			return new ReportAllWorkingIssuesCmd(request,response,issobjkey);
			
			case ETSISSRPTWALLFC:
			
			return new ShowFilterCondsWrkAllCmd(request,response,issobjkey);
			
			case ETSISSRPTWALLFCGO:
			
			return new ShowFilterCondsWrkAllGoCmd(request,response,issobjkey);
			
			case ETSISSRPTISUB:
			
			return new ReportISubIssuesCmd(request,response,issobjkey);
			
			case ETSISSRPTISUBFC:
			
			return new ShowFilterCondsISubCmd(request,response,issobjkey);
			
			case ETSISSRPTISUBFCGO:
			
			return new ShowFilterCondsISubGoCmd(request,response,issobjkey);
			
			case ETSISSRPTASGND:
			
			return new ReportAsgnMeIssuesCmd(request,response,issobjkey);
			
			case ETSISSRPTASGNDFC:
			
			return new ShowFilterCondsAsgnMeCmd(request,response,issobjkey);
			
			case ETSISSRPTASGNDFCGO:
			
			return new ShowFilterCondsAsgnMeGoCmd(request,response,issobjkey);
			
			case ETSISSUESHOWLOG:
			
			return new ShowIssueCommLogsCmd(request,response,issobjkey);
			
			case ETSSHOWUSERINFO:
			
			return new ShowUserDetailsInfoCmd(request,response,issobjkey);
			
		}
		
		
		return null;
		
	}
	
	

}//end of class

