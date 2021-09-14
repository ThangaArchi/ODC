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

package oem.edge.ets.fe.workflow.clientattendee;


import java.util.ArrayList;

import oem.edge.ets.fe.ETSCalendar;
import oem.edge.ets.fe.workflow.core.AbstractDAO;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;


/**
 * Class       : NewClientAttendeeDAO
 * Package     : oem.edge.ets.fe.workflow.clientattendee
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NewClientAttendeeDAO extends AbstractDAO{


	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#saveWorkflowObject(oem.edge.ets.fe.workflow.core.WorkflowObject)
	 */
	public boolean saveWorkflowObject(WorkflowObject workflowObject) {
		NewClientAttendeeVO vo = (NewClientAttendeeVO)workflowObject;
		
		/* REQUIRED		IMPLEMENTED ?
		 * ---------	-------------
		 * CLIENT_ID		Y
		 * COMPANY			Y
		 * FNAME			Y
		 * LNAME			N
		 * TITLE			Y
		 * LAST_USERID		N
		 * LAST_TIMESTAMP	N
		 */		
		
	
		DBAccess db = null;
		try {
			System.out.println("..........Creating new DBAccess object");
			db=new DBAccess();
			System.out.println("..........Created DBAccess object.");
			/*
			db.prepareDirectQuery("select max(CAST(CLIENT_ID AS INT)) from ets.wf_client");
			
			System.out.println(".......prepareDirectQuery done.");
			System.out.println(".....Waiting for response from database.");
			int rows=db.execute();
			System.out.println(".....Recieved response from database\n" +
									"Number of rows returned = "+rows);
			String lastPerson = db.getString(0,0);
			System.out.println(".......Largest client ID is "+ lastPerson);
			int client_id = Integer.parseInt(lastPerson)+1;
			*/
			String client_id = ETSCalendar.getNewCalendarId();
			String q ="INSERT INTO ETS.WF_CLIENT (CLIENT_ID, COMPANY, FNAME, LNAME, TITLE) VALUES('"+client_id+"','"+vo.getCompany()+"','"+vo.getFname()+"','"+vo.getLname()+"','"+vo.getTitle()+"')";
			
			db.prepareDirectQuery(q);
			
			System.out.println(q);
			/*db.setString(1,Integer.toString(client_id));
			db.setString(2,vo.getCompany()[0]);
			db.setString(3,vo.getName());
			db.setString(4,vo.getTitle()[0]);*/
			System.out.println("....insert query ready");
			System.out.println(".....Waiting for database to insert the new attendee.");
			db.execute();
			System.out.println("....Database finished inserting.");
			db.doCommit();
			db.close();
			db=null;
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
					
				}catch(Exception ex){
					
				}
			}
		}
		System.out.println(".....Reurning from DAO");
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#getWorkflowObject(java.lang.String)
	 */
	public WorkflowObject getWorkflowObject(String ID) {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#getWorkflowObjectList(java.lang.String)
	 */
	public ArrayList getWorkflowObjectList(String ID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#saveWorkflowObjectList(java.util.ArrayList)
	 */
	public boolean saveWorkflowObjectList(ArrayList object) {
		// TODO Auto-generated method stub
	    ArrayList vo_a=(ArrayList)object;
	    for(int i=0; i<vo_a.size(); i++)
	    {
	        if(!saveWorkflowObject((NewClientAttendeeVO)vo_a.get(i)))
	        {
	            return false;
	        }
	    }
		return true;
	}
}

