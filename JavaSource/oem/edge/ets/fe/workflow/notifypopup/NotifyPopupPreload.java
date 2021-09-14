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

package oem.edge.ets.fe.workflow.notifypopup;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.SelectControl;
import org.apache.commons.logging.Log;



/**
 * Class       : NotifyPopupPreload
 * Package     : oem.edge.ets.fe.workflow.notifypopup
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NotifyPopupPreload extends WorkflowObject {

	private Collection people = new ArrayList();

	private String date = null;

	private String displayEmailID = null;

	
	
	private Log logger	=	WorkflowLogger.getLogger(DBAccess.class);

	/**
	 *  
	 */
	public NotifyPopupPreload(String project_id, String userid) {

		
		displayEmailID = userid ; //This is supposed to be the userid itself.. displayEmailID is a misleading name. We don't need the Email ID in that variable
		System.out.println("displayEmailID = "+userid);
		
		Format f = new SimpleDateFormat("E, dd MMMM, yyyy");
		date = (f.format(new Date()));

		DBAccess db = null;
		
		try {
			db = new DBAccess();
			//db.prepareDirectQuery("SELECT * FROM ETS.WF_CLIENT");
			db.prepareDirectQuery("SELECT ETS_USERS.USER_ID "+
									"FROM ETS.ETS_USERS AS ETS_USERS "+
									"WHERE ETS_USERS.USER_PROJECT_ID = '"+project_id+"' AND ETS_USERS.ACTIVE_FLAG = 'A'");
			System.out.println(".......Waiting for person list from the database");
			int rows = db.execute();
			System.out.println(".......Person list having " + rows
					+ " items recieved from database.");
			logger.info("Person list having " + rows+ " items recieved from database.");
			
			
			
			for (int i = 0; i < rows; i++) {
				
				ETSUserDetails u = new ETSUserDetails();
				u.setWebId(db.getString(i,"USER_ID"));
				u.extractUserDetails(db.getConnection());
				
				people.add(new SelectControl(u.getFirstName()+" "+u.getLastName(),u.getEMail()));
			}
			db.close();
			db = null;
		} catch (Exception e) {

			e.printStackTrace();
		}finally{
			if(db!=null)
			{
				try {
					db.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				db=null;
			}
		}
		System.out.println("Exit preload constructor.");
	}
	/**
	 * @return Returns the date.
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date The date to set.
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return Returns the displayEmailID.
	 */
	public String getDisplayEmailID() {
		return displayEmailID;
	}
	/**
	 * @param displayEmailID The displayEmailID to set.
	 */
	public void setDisplayEmailID(String displayEmailID) {
		this.displayEmailID = displayEmailID;
	}

	/**
	 * @return Returns the people.
	 */
	public Collection getPeople() {
		return people;
	}
	/**
	 * @param people The people to set.
	 */
	public void setPeople(Collection people) {
		this.people = people;
	}
}