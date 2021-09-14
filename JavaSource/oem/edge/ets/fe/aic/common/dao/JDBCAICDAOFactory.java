/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

package oem.edge.ets.fe.aic.common.dao;

import oem.edge.ets.fe.aic.dyntab.dao.AICTableDAO;
import oem.edge.ets.fe.aic.dyntab.dao.AICTableRowsDataDAO;
import oem.edge.ets.fe.aic.dyntab.dao.AICTemplateColumnDAO;
import oem.edge.ets.fe.aic.dyntab.dao.AICTemplateDAO;
import oem.edge.ets.fe.aic.dyntab.dao.IAICTableDAO;
import oem.edge.ets.fe.aic.dyntab.dao.IAICTableRowsDataDAO;
import oem.edge.ets.fe.aic.dyntab.dao.IAICTemplateColumnDAO;
import oem.edge.ets.fe.aic.dyntab.dao.IAICTemplateDAO;


/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JDBCAICDAOFactory extends AICDAOFactory {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	public IAICTemplateDAO getAICTemplateDAO() {
		return new AICTemplateDAO();
	}
	
	public IAICTemplateColumnDAO getAICTemplateColumnDAO() {
		return new AICTemplateColumnDAO();
		}
		
		
	public IAICTableDAO getAICTableDAO() {
		return new AICTableDAO();
		}
		
		
	public IAICTableRowsDataDAO getAICTableRowsData() {
		return new AICTableRowsDataDAO();
		
		}
}
