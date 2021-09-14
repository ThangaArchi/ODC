/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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


/*
 * Created on Jan 17, 2005
 */
 
package oem.edge.ets.fe;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.PopupHeaderFooter;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSPopUpHeaderFooter {
	
	
	public final static String Copyright = "(C) Copyright IBM Corp.  2002-2005";
	public static final String VERSION = "1.3";


	protected StringBuffer sHeader = new StringBuffer("");
	protected StringBuffer sFooter = new StringBuffer(""); 
	
	public ETSPopUpHeaderFooter() {
		super();
	}
	
	
	public void init(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try {
			
			PopupHeaderFooter header = new PopupHeaderFooter();
			header.setPageTitle("E&TS Connect");

			sHeader.append(header.printPopupHeader());			
			
			sFooter.append(header.printPopupFooter());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	/**
	 * @return
	 */
	public StringBuffer getFooter() {
		return sFooter;
	}

	/**
	 * @return
	 */
	public StringBuffer getHeader() {
		return sHeader;
	}

}
