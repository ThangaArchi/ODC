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

package oem.edge.ets.fe.workflow.newissue;

import java.util.ArrayList;

import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.MiscUtils;
import oem.edge.ets.fe.workflow.util.SelectControl;
import oem.edge.ets.fe.workflow.util.UserUtils;

import org.apache.commons.logging.Log;

/**
 * Class       : NewIssuePreload
 * Package     : oem.edge.ets.fe.workflow.newissue
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NewIssuePreload {

	private static Log logger = WorkflowLogger.getLogger(NewIssuePreload.class);
	
	private ArrayList issueTypes = null;
	private ArrayList issueCategories=null;

	private ArrayList rolePlayingPeople = null;
	private ArrayList people = null;
	private ArrayList months = new ArrayList();
	private ArrayList years = new ArrayList();
	private ArrayList days = new ArrayList();

	public NewIssuePreload(String project_id) {
		issueTypes = new ArrayList();
		
		issueCategories=new ArrayList();
		
		people =new ArrayList();
		
		//		Months
		months.add(new SelectControl("Jan","1"));
		months.add(new SelectControl("Feb","2"));
		months.add(new SelectControl("Mar","3"));
		months.add(new SelectControl("Apr","4"));
		months.add(new SelectControl("May","5"));
		months.add(new SelectControl("Jun","6"));
		months.add(new SelectControl("Jul","7"));
		months.add(new SelectControl("Aug","8"));
		months.add(new SelectControl("Sep","9"));
		months.add(new SelectControl("Oct","10"));
		months.add(new SelectControl("Nov","11"));
		months.add(new SelectControl("Dec","12"));
		
		for(int i = 1; i<32; i++)
			days.add(new SelectControl(Integer.toString(i),Integer.toString(i)));
		
		for(int i = 2006; i<2013; i++)
			years.add(new SelectControl(Integer.toString(i),Integer.toString(i)));
		
			
		//-------------------------------------------------
		
		DBAccess db = null;
		
		try {
			db = new DBAccess();
			db.prepareDirectQuery("SELECT ETS_USERS.USER_ID "+
									"FROM ETS.ETS_USERS AS ETS_USERS "+
									"WHERE ETS_USERS.USER_PROJECT_ID = '"+project_id+"' AND ETS_USERS.ACTIVE_FLAG = 'A' with ur");
			System.out.println(".......Waiting for person list from the database");
			int rows = db.execute();
			System.out.println(".......Person list having " + rows
					+ " items recieved from database.");
			//logger.info("Person list having " + rows+ " items recieved from database.");
			
			
			
			for (int i = 0; i < rows; i++) {
				
				ETSUserDetails u = new ETSUserDetails();
				u.setWebId(db.getString(i,"USER_ID"));
				u.extractUserDetails(db.getConnection());
				
				people.add(new SelectControl(u.getFirstName()+" "+u.getLastName(),
						db.getString(i,"USER_ID")));
			}
			///////////
			rolePlayingPeople = UserUtils.getUserByRoleName("NO_VISITOR", project_id);
			
			MiscUtils.removeDuplicates(rolePlayingPeople);
			for(int i = 0; i < rolePlayingPeople.size(); i++)
			{
				ETSUserDetails u = new ETSUserDetails();
				u.setWebId((String)rolePlayingPeople.get(i));
				u.extractUserDetails(db.getConnection());
				
				rolePlayingPeople.set(i,new SelectControl(u.getFirstName()+" "+u.getLastName(),
						(String)rolePlayingPeople.get(i)));
			}
			
			///////////
			
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
		
		//-------------------------------------------------
		
		
		issueTypes.addAll(MiscUtils.getIssueTypes());
       		issueCategories.addAll(MiscUtils.getIssueCategories());
			
		
	}


	/**
	 * @param issueTypes The issueTypes to set.
	 */
	public void setIssueTypes(ArrayList issueType) {
		this.issueTypes = issueType;
	}
	
	public void setIssueCategories(ArrayList issueCategories) {
		 this.issueCategories= issueCategories;
	}
	
	public ArrayList getIssueCategories() {
		 return issueCategories;
	}
	
	
	/**
	 * @return Returns the issueTypes.
	 */
	public ArrayList getIssueTypes() {
		return issueTypes;
	}

	
	/**
	 * @param people The people to set.
	 */
	public void setPeople(ArrayList people) {
		this.people = people;
	}


	/**
	 * @return Returns the people.
	 */
	public ArrayList getPeople() {
		return people;
	}
	
	
	/**
	 * @return Returns the rolePlayingPeople.
	 */
	public ArrayList getRolePlayingPeople() {
		return rolePlayingPeople;
	}
	/**
	 * @param rolePlayingPeople The rolePlayingPeople to set.
	 */
	public void setRolePlayingPeople(ArrayList rolePlayingPeople) {
		this.rolePlayingPeople = rolePlayingPeople;
	}
	/**
	 * @return Returns the days.
	 */
	public ArrayList getDays() {
		return days;
	}
	/**
	 * @param days The days to set.
	 */
	public void setDays(ArrayList days) {
		this.days = days;
	}
	/**
	 * @return Returns the months.
	 */
	public ArrayList getMonths() {
		return months;
	}
	/**
	 * @param months The months to set.
	 */
	public void setMonths(ArrayList months) {
		this.months = months;
	}
	/**
	 * @return Returns the years.
	 */
	public ArrayList getYears() {
		return years;
	}
	/**
	 * @param years The years to set.
	 */
	public void setYears(ArrayList years) {
		this.years = years;
	}
}
