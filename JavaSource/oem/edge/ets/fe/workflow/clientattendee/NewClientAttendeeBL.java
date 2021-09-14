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


/*
 * Created on Sep 14, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.clientattendee;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.core.WorkflowObject;

/**
 * @author pradyumna
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NewClientAttendeeBL extends WorkflowObject {

	public boolean saveClientAttendee(NewClientAttendeeVO vo)
	{
			return (new NewClientAttendeeDAO()).saveWorkflowObject(vo);
	}

    /**
     * @param vo
     * @return
     */
    public boolean saveClientAttendees(NewClientAttendeeVO vo) {
    	ArrayList a = new ArrayList();
    	NewClientAttendeeVO vo1 = null;
    	for(int i = 0; i<vo.getAcompany().length; i++)
    	{
    	    vo1 = new NewClientAttendeeVO();
    	    vo1.setCompany(vo.getAcompany()[i]);
    	    vo1.setFname(vo.getAfname()[i]);
    	    vo1.setLname(vo.getAlname()[i]);
    	    vo1.setTitle(vo.getAtitle()[i]);
    	    a.add(vo1);
    	    
    	}
    	
        return (new NewClientAttendeeDAO()).saveWorkflowObjectList(a);
       
    }
	
}

