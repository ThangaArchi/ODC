/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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
 * Created on Sep 5, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet.prepare;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetMetPrepareDAO {
	
	public ArrayList getpreviousIssueList(String projectID){
		
		ArrayList list 	  = new ArrayList();

		SetMetPrepareValueObject vo = null;
		try{
						
			vo = new SetMetPrepareValueObject();
			
			vo.setIssueTitle("issue1");
			vo.setIssueOwner("Oracle");
			vo.setIssueStatus("Opened");
			vo.setDateOpened("3Q06");
			vo.setWorkflowStatus("Incomplete");
			
			list.add(vo);
			
			// Second Array 
			vo = new SetMetPrepareValueObject();
			vo.setIssueTitle("issue2");
			vo.setIssueOwner("Oracle");
			vo.setIssueStatus("Opened");
		    vo.setDateOpened("2Q06");
			vo.setWorkflowStatus("In-complete");
			
			list.add(vo);
					

			vo = new SetMetPrepareValueObject();
			vo.setIssueTitle("issue3");
			vo.setIssueOwner("Oracle");
			vo.setIssueStatus("Opened");
		    vo.setDateOpened("2Q04");
			vo.setWorkflowStatus("In-complete");
			
			list.add(vo);
			vo = new SetMetPrepareValueObject();
			vo.setIssueTitle("issue4");
			vo.setIssueOwner("Oracle");
			vo.setIssueStatus("Opened");
		    vo.setDateOpened("2Q05");
			vo.setWorkflowStatus("In-complete");
			
			list.add(vo);
		  
		}catch(Exception e){
			
		}
		return list;
		
	}

}
