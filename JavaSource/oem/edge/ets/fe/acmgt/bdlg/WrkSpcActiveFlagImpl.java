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
package oem.edge.ets.fe.acmgt.bdlg;

import java.sql.SQLException;

import oem.edge.amt.AMTException;
import oem.edge.ets.fe.acmgt.dao.WrkSpcInfoDAO;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;


/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WrkSpcActiveFlagImpl {
	
	public static final String VERSION = "1.6";
	private static Log logger = EtsLogger.getLogger(WrkSpcActiveFlagImpl.class);

	/**
	 * 
	 */
	public WrkSpcActiveFlagImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean updateWrkSpcUsers() throws SQLException,AMTException,Exception {
		
		logger.debug("ENTER updateWrkSpcUsers::WrkSpcActiveFlagImpl");
		
		WrkSpcInfoDAO infoDao = new WrkSpcInfoDAO();
		return infoDao.updateWrkSpcUsers();
		
				
	}
}
