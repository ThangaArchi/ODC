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
import java.util.Collection;

import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;

import oem.edge.ets.fe.workflow.util.SelectControl;



/**
 * Class       : NewClientAttendeePreload
 * Package     : oem.edge.ets.fe.workflow.clientattendee
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NewClientAttendeePreload extends WorkflowObject {

	private Collection companies = new ArrayList();
	private Collection titles = new ArrayList();
	/**
	 * @param vo
	 * 
	 */
	public NewClientAttendeePreload(String projectID) {
		
		SelectControl c = null;
		
		DBAccess db = null;
		try {
			db=new DBAccess();
			db.prepareDirectQuery("SELECT company from ets.ets_projects where project_id='"+projectID+"' with ur");
			db.execute();
			String thisCompany = db.getString(0,0); 
			companies.add(new SelectControl(db.getString(0,0),db.getString(0,0)));
			
			db.prepareDirectQuery("SELECT DISTINCT PARENT FROM DECAFOBJ.COMPANY_VIEW with ur");
			System.out.println(".......Waiting for company list from the database");
			int rows=db.execute();
			System.out.println(".......Company list having "+rows+" items recieved from database.");
			
			for(int i = 0 ; i < rows ; i++)
			{
				if(!(thisCompany.equals(db.getString(i,"PARENT")))){
				c = new SelectControl(db.getString(i,"PARENT"),db.getString(i,"PARENT"));
				companies.add(c);}
			}
			
			db.close();
			db=null;
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally
		{
			if(db!=null)
			{
				try {
					db.close();
				} catch (Exception e) {
					e.printStackTrace();
				db=null;
				}
			}
		}
		
		/*Title t = new Title();
		t.setTitleText("Mr.");
		titles.add(t);
		t=new Title();
		t.setTitleText("Ms.");
		titles.add(t);
		t=new Title();
		t.setTitleText("Miss.");
		titles.add(t);
		t=new Title();
		t.setTitleText("Mrs.");
		titles.add(t);
		t=new Title();
		t.setTitleText("Dr.");
		titles.add(t);
		*/
		System.out.println("Preload done.");
	}
	/**
	 * @return Returns the companies.
	 */
	public Collection getCompanies() {
		return companies;
	}
	/**
	 * @param companies The companies to set.
	 */
	public void setCompanies(Collection companies) {
		this.companies = companies;
	}
	/**
	 * @return Returns the titles.
	 */
	public Collection getTitles() {
		return titles;
	}
	/**
	 * @param titles The titles to set.
	 */
	public void setTitles(Collection titles) {
		this.titles = titles;
	}
}