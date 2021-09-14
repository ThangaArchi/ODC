/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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

package oem.edge.ets.fe.workflow.setmet.prepare;

import java.util.Comparator;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;



/**
 * Class       : IssueComparator
 * Package     : oem.edge.ets.fe.workflow.prepare
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class IssueComparator implements Comparator {
	private static Log logger = WorkflowLogger.getLogger(IssueComparator.class);
	
	private int SETMET = 1;
	private int QBR = 2;
	private int EXEC_VISIT = 3;
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		Row r1 = (Row)o1;
		Row r2 = (Row)o2;
		int i1 = 0;
		int i2 = 0;
		if(r1.getWorkflowType().equals("SETMET"))
			i1 = SETMET;
		if(r1.getWorkflowType().equals("QBR"))
			i1 = QBR;
		if(r1.getWorkflowType().equals("EXEC_VISIT"))
			i1 = EXEC_VISIT;
		
		if(r2.getWorkflowType().equals("SETMET"))
			i2 = SETMET;
		if(r2.getWorkflowType().equals("QBR"))
			i2 = QBR;
		if(r2.getWorkflowType().equals("EXEC_VISIT"))
			i2 = EXEC_VISIT;
		//System.out.println(i1-i2);
		return i1-i2;
	}
	public void setCurrentType(String type)
	{
		if(type.equals("SETMET"))
		{
			SETMET = 100;
			QBR = 2;
			EXEC_VISIT = 3;
			
		}
		if(type.equals("QBR"))
		{
			SETMET = 1;
			QBR = 100;
			EXEC_VISIT = 3;
		}
		if(type.equals("EXEC_VISIT"))
		{
			SETMET = 1;
			QBR = 2;
			EXEC_VISIT = 100;
		}

	}
}
