package oem.edge.ets.fe.ismgt.dao;

import oem.edge.ets.fe.ismgt.helpers.*;
import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.resources.*;
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
public class FilterDAOFactory implements EtsIssFilterConstants,FilterDAOFactoryIF {
	
	public static final String VERSION = "1.11";

	/**
	 * Constructor for FilterDAOFactory.
	 */
	public FilterDAOFactory() {
		super();
	}

/**
 * to return proper FilterDAO,based on istype
 */	
	
	public FilterDAOAbs createFilterDAO(EtsIssFilterObjectKey issobjkey) throws Exception {

		
		String isType=issobjkey.getProblemType();
		
		
			if(isType.equals("iss")) {
				
				return new EtsIssueFilterDAO(issobjkey);
			}	
			
			if(isType.equals("chg")) {
				
				return new EtsChgFilterDAO(issobjkey);
				
			}

	return null;
	
	}//end of method
	
	/**
	 * to return proper ActionDAO,based on issue class
	 */	

	public FilterDAOAbs createIssueActionDAO(EtsIssObjectKey etsIssObjKey) throws Exception {

		
			String issueClass=etsIssObjKey.getIssueClass();
		
		
		
				if(issueClass.equals(ETSISSUESUBTYPE)) {
				
					return new EtsIssueFilterDAO(etsIssObjKey);
				}	
			
				if(issueClass.equals(ETSCHANGESUBTYPE)) {
				
					return new EtsChgFilterDAO(etsIssObjKey);
				
				}

		return null;
	
		}//end of method
	
}//end of class

