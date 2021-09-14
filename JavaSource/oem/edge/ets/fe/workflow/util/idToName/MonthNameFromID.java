/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.util.idToName;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.util.SelectControl;

/**
 * Class       : MonthNameFromID
 * Package     : oem.edge.ets.fe.workflow.util.idToName
 * Description : 
 * Date		   : Feb 27, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class MonthNameFromID implements IDToName {
	java.util.HashMap months  = null;
	public MonthNameFromID()
	{
		months = new java.util.HashMap();
		months.put(new Integer(1),"January");
		months.put(new Integer(2),"February");
		months.put(new Integer(3),"March");
		months.put(new Integer(4),"April");
		months.put(new Integer(5),"May");
		months.put(new Integer(6),"June");
		months.put(new Integer(7),"July");
		months.put(new Integer(8),"August");
		months.put(new Integer(9),"September");
		months.put(new Integer(10),"October");
		months.put(new Integer(11),"November");
		months.put(new Integer(12),"December");
	}
	
	public String convert(String ID, DBAccess db) throws Exception {
		
		return (String)months.get(new Integer(Integer.parseInt(ID)));
	}
	public String convert(String ID) {
		String value = null;
		try{
			value = convert(ID, null);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return value;
	}
}

