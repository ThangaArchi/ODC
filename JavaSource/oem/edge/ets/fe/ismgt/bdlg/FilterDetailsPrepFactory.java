package oem.edge.ets.fe.ismgt.bdlg;

import oem.edge.amt.*;
import oem.edge.ets.fe.ismgt.resources.*;
import oem.edge.ets.fe.ismgt.model.*;

import javax.servlet.*;
import javax.servlet.http.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004                                     */
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
public class FilterDetailsPrepFactory implements EtsIssFilterConstants,FilterDetailsPrepFactoryIF{

	/**
	 * Constructor for FilterDetailsPrepFactory.
	 */
	public FilterDetailsPrepFactory() {
		super();
	}
	
	
	public FilterDetailsDataPrepAbsBean createFilterDetailsPrepBean(HttpServletRequest request,HttpServletResponse response,EtsIssFilterObjectKey issobjkey) throws Exception {
		
		int state= issobjkey.getState();
		
		switch (state) {

			case ETSISSRPTWALL:
			
			return new WorkAllIssuesDataPrepRep(request,response,issobjkey);
			
			case ETSISSRPTWALLFC:
			
			return new ShowFilterCondsWrkAllDataPrep(request,response,issobjkey);
			
			case ETSISSRPTWALLFCGO:
			
			return new ShowFilterCondsWrkAllGoDataPrep(request,response,issobjkey);
			
			case ETSISSRPTISUB:
			
			return new IssuesISubDataPrepRep(request,response,issobjkey);
			
			case ETSISSRPTISUBFC:
			
			return new ShowFilterCondsISubDataPrep(request,response,issobjkey);
			
			case ETSISSRPTISUBFCGO:
			
			return new ShowFilterCondsISubGoDataPrep(request,response,issobjkey);
			
			case ETSISSRPTASGND:
			
			return new IssuesAsgndMeDataPrepRep(request,response,issobjkey);
			
			case ETSISSRPTASGNDFC:
			
			return new ShowFilterCondsAsgndMeDataPrep(request,response,issobjkey);
			
			case ETSISSRPTASGNDFCGO:
			
			return new ShowFilterCondsAsgndMeGoDataPrep(request,response,issobjkey);
			
			case ETSLISTISSTYPESINFO:
			
			return new EtsListIssTypesInfoBdlg(request,response,issobjkey);
			
		}
		
		
		return null;
		
	}

}//end of class

