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
public abstract class AICDAOFactory {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	public static final int JDBC = 1;
	public static final int ORM = 2;
	public static final int SPRING = 3;
	  
	  
	public static AICDAOFactory getDAOFactory(int whichFactory) {
  
		switch (whichFactory) {
			case JDBC: 
					  return new JDBCAICDAOFactory();
			/*
			case ORM    : 
					  return new ORMAICDAOFactory();
			case SPRING    : 
					 return new SpringAICDAOFactory();
					 
					 */
			default           : 
					return new JDBCAICDAOFactory(); 
		}		
	 }
	 
	public abstract IAICTemplateDAO getAICTemplateDAO();

	public abstract IAICTemplateColumnDAO getAICTemplateColumnDAO();

	public abstract IAICTableDAO getAICTableDAO();

	public abstract IAICTableRowsDataDAO getAICTableRowsData();

}
