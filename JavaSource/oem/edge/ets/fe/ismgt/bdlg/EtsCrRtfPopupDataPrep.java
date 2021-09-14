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

package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.SQLException;

import oem.edge.ets.fe.ismgt.dao.EtsCrPmoDAO;
import oem.edge.ets.fe.ismgt.model.EtsCrRtfModel;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrRtfPopupDataPrep {

	public static final String VERSION = "1.28";

	/**
	 * 
	 */
	public EtsCrRtfPopupDataPrep() {
		super();

	}

	public EtsCrRtfModel getCrRtf(String pmoId) throws SQLException, Exception {

		EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();

		EtsCrRtfModel crRtfModel = crPmoDao.getCrRTF(pmoId);

		return crRtfModel;

	}

} //end of class
