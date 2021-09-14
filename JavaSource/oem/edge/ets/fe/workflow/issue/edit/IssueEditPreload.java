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


package oem.edge.ets.fe.workflow.issue.edit;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.util.MiscUtils;
import oem.edge.ets.fe.workflow.util.SelectControl;
import oem.edge.ets.fe.workflow.util.UserUtils;

//private static Log logger = WorkflowLogger.getLogger(IssueEditPreload.class);
/**
 * Class       : IssueEditPreload
 * Package     : oem.edge.ets.fe.workflow.issue.edit
 * Description : 
 * Date		   : Oct 10, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class IssueEditPreload extends WorkflowObject {

	private String name = null;
	private String title=null;
	private String status=null;
	private SelectControl focalPt=new SelectControl();
	private String type=null;
	private String category = null;
	private String month=null;
	private String year=null;
	private String day=null;
	private ArrayList owners=new ArrayList();
	private String desc =null;
	private ArrayList months = new ArrayList();
	private ArrayList years = new ArrayList();
	private ArrayList days = new ArrayList();
	private ArrayList persons = new ArrayList();
	private ArrayList types = new ArrayList();
	private ArrayList categories = new ArrayList();
	
	
	/**
	 * @param issue_id
	 */
	public IssueEditPreload(String issue_id, String project_id, EditIssueFormBean f, HttpServletRequest request) {
	
		types.addAll(MiscUtils.getIssueTypes());
		categories.addAll(MiscUtils.getIssueCategories());
		name = issue_id;
		
		boolean shouldPreset = true;
		if(request.getAttribute("errorMessages")!=null)
			shouldPreset = false;
		
		//Months
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
		
		///////////////////////
		DBAccess db = null;
		try {
			db = new DBAccess();

			/*db.prepareDirectQuery("UPDATE ETS.WF_ISSUE SET ISSUE_TITLE= 'Price title 6', ISSUE_DESC= 'Price is too low 6' ,ISSUE_CONTACT='v2srikau@us.ibm.com' ,ISSUE_TYPE= 'Ebiz', TARGET_DATE= '2010-4-1'  WHERE ISSUE_ID='1161599714395-6686'");
			db.execute();
			*/
			/////////
			persons = UserUtils.getUserByRoleName("NO_VISITOR", project_id,db);
			//ArrayList temp = UserUtils.getUserByRoleName("WS_OWNER",project_id);
			//for(int i = 0; i < temp.size(); i++)
			//	persons.add(temp.get(i));
			//temp = UserUtils.getUserByRoleName("WS_MGR",project_id);
			//for(int i = 0; i < temp.size(); i++)
			//	persons.add(temp.get(i));
			
			MiscUtils.removeDuplicates(persons);
			for(int i = 0; i < persons.size(); i++)
			{
				ETSUserDetails u = new ETSUserDetails();
				u.setWebId((String)persons.get(i));
				u.extractUserDetails(db.getConnection());
				
				persons.set(i,new SelectControl(u.getFirstName()+" "+u.getLastName(),
						(String)persons.get(i)));
			}
			
			///////////
			db.doCommit();
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
		//////////////////////
		///////////////////////
		db = null;
		try {
			db = new DBAccess();
			//db.prepareDirectQuery("SELECT * FROM ETS.WF_CLIENT");
			db.prepareDirectQuery("select issue_desc, issue_title, issue_contact, status, issue_type,issue_category,target_date,issue_id_display from ets.wf_issue where issue_id='"+issue_id+"' with ur");
			int rows = db.execute();
			desc = db.getString(0,0);
			title = db.getString(0,1);

			if(shouldPreset)
			{
			((EditIssueVO)(f.getWorkflowObject())).setDesc(desc);
			((EditIssueVO)(f.getWorkflowObject())).setTitle(title);
			}
			ETSUserDetails u = new ETSUserDetails();
			u.setWebId(db.getString(0,2));
			u.extractUserDetails(db.getConnection());
			
			focalPt = new SelectControl(u.getFirstName()+" "+u.getLastName(),db.getString(0,2));
		System.out.println("focalPt is "+focalPt);
		if(shouldPreset)
		{
		((EditIssueVO)(f.getWorkflowObject())).setFocalPt(new String[]{focalPt.getValue()});
		}
			status = db.getString(0,3);
			type= db.getString(0,4);
			if(shouldPreset)
			{
			((EditIssueVO)(f.getWorkflowObject())).setType(new String[]{type});
			}
			category=db.getString(0,5);
			System.out.println("category is "+category);
			if(shouldPreset)
			{
			((EditIssueVO)(f.getWorkflowObject())).setCategory(new String[]{category});
			}
			
			String d = db.getString(0,6);
			if(d!=null)
			{
				year = d.substring(0,4);
				month = getMonthString(Integer.parseInt(d.substring(5,7))-1);
				day = d.substring(8,10);
				
				String[] temp = new String[1];
				temp[0] = Integer.toString(Integer.parseInt(d.substring(5,7)));
				if(shouldPreset){
				((EditIssueVO)(f.getWorkflowObject())).setMonth(temp);
				}
				temp = new String[1];
				temp[0] = Integer.toString(Integer.parseInt(d.substring(8,10)));
				if(shouldPreset)
				{
				((EditIssueVO)(f.getWorkflowObject())).setDay(temp);
				}
				System.out.println(((EditIssueVO)(f.getWorkflowObject())).getDay());
				
				temp = new String[1];
				temp[0] = d.substring(0,4);
				if(shouldPreset){
				((EditIssueVO)(f.getWorkflowObject())).setYear(temp);
				}
				System.out.println(year);
				System.out.println(month);
				System.out.println(day);
			}
			else
			{
				request.removeAttribute("perm_accept_reject");	
			}
			
			String issue_id_display=db.getString(0,7);
			if(issue_id_display!=null)
				name=issue_id_display;
			else
				name=issue_id;
			
			db.doCommit();
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
		//////////////////////

		/////////////////////
		db = null;
		try {
			db = new DBAccess();
			//db.prepareDirectQuery("SELECT * FROM ETS.WF_CLIENT");
			db.prepareDirectQuery("select owner_id, ownership_state from ets.wf_issue_owner where issue_id='"+issue_id+"' with ur");
			int rows = db.execute();
			System.out.println(".......Person list having " + rows
					+ " items recieved from database.");
			//logger.info("Person list having " + rows+ " items recieved from database.");
			
			
			
			for (int i = 0; i < rows; i++) {
				
				ETSUserDetails u = new ETSUserDetails();
				u.setWebId(db.getString(i,"OWNER_ID"));
				u.extractUserDetails(db.getConnection());
				String ownershipState = null;
				if(db.getString(i,"OWNERSHIP_STATE")!=null && db.getString(i,"OWNERSHIP_STATE")!="null")
					ownershipState = db.getString(i,"OWNERSHIP_STATE");
				else
					ownershipState = "ASSIGNED";
				if(shouldPreset)
				{
				owners.add(new Owner(u.getFirstName()+" "+u.getLastName(),db.getString(i,"OWNER_ID"),ownershipState));
				}
				for(int j=0; j<persons.size(); j++)
				{
					if(db.getString(i,"OWNER_ID").equals(((SelectControl)persons.get(j)).getValue()))
					{
						if(db.getString(i,"OWNERSHIP_STATE")!=null && db.getString(i,"OWNERSHIP_STATE")!="null")
							persons.set(j,new SelectControl(u.getFirstName()+" "+u.getLastName()+":"+db.getString(i,"OWNERSHIP_STATE"),db.getString(i,"OWNER_ID")));
					}
				}
			}
			if(!shouldPreset)
			{
				ETSUserDetails u = new ETSUserDetails();
				
				for(int i=0; i< ((EditIssueVO)(f.getWorkflowObject())).getOwners().length; i++)
				{
					u.setWebId(((EditIssueVO)(f.getWorkflowObject())).getOwners()[i]);
					u.extractUserDetails(db.getConnection());
					String qualifiedName = u.getFirstName()+" "+u.getLastName();

					db.prepareDirectQuery("select ownership_state from ets.wf_issue_owner where issue_id='"+issue_id+"' and owner_id='"+((EditIssueVO)(f.getWorkflowObject())).getOwners()[i]+"' with ur");
					if(db.execute()==1)
						qualifiedName += db.getObject(0,0)==null ? "" : ":"+db.getString(0,0);
					
					owners.add(new Owner(qualifiedName,((EditIssueVO)(f.getWorkflowObject())).getOwners()[i]));
					
				}
			}
			db.doCommit();
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
		//////////////////////

		
		
	}
	
	private String getMonthString(int monthNum)
	{
		String ans = null;
		ans = monthNum==0?"Jan":ans;
		ans = monthNum==1?"Feb":ans;
		ans = monthNum==2?"Mar":ans;
		ans = monthNum==3?"Apr":ans;
		ans = monthNum==4?"May":ans;
		ans = monthNum==5?"Jun":ans;
		ans = monthNum==6?"Jul":ans;
		ans = monthNum==7?"Aug":ans;
		ans = monthNum==8?"Sep":ans;
		ans = monthNum==9?"Oct":ans;
		ans = monthNum==10?"Nov":ans;
		ans = monthNum==11?"Dec":ans;
		return ans;
	}

	/**
	 * @return Returns the day.
	 */
	public String getDay() {
		return day;
	}
	/**
	 * @param day The day to set.
	 */
	public void setDay(String day) {
		this.day = day;
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
	 * @return Returns the desc.
	 */
	public String getDesc() {
		return desc;
	}
	/**
	 * @param desc The desc to set.
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	/**
	 * @return Returns the focalPt.
	 */
	public SelectControl getFocalPt() {
		return focalPt;
	}
	/**
	 * @param focalPt The focalPt to set.
	 */
	public void setFocalPt(SelectControl focalPt) {
		this.focalPt = focalPt;
	}
	/**
	 * @return Returns the month.
	 */
	public String getMonth() {
		return month;
	}
	/**
	 * @param month The month to set.
	 */
	public void setMonth(String month) {
		this.month = month;
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
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the owners.
	 */
	public ArrayList getOwners() {
		return owners;
	}
	/**
	 * @param owners The owners to set.
	 */
	public void setOwners(ArrayList owners) {
		this.owners = owners;
	}
	/**
	 * @return Returns the persons.
	 */
	public ArrayList getPersons() {
		return persons;
	}
	/**
	 * @param persons The persons to set.
	 */
	public void setPersons(ArrayList persons) {
		this.persons = persons;
	}
	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return Returns the year.
	 */
	public String getYear() {
		return year;
	}
	/**
	 * @param year The year to set.
	 */
	public void setYear(String year) {
		this.year = year;
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
	/**
	 * @return Returns the types.
	 */
	public ArrayList getTypes() {
		return types;
	}
	/**
	 * @param types The types to set.
	 */
	public void setTypes(ArrayList types) {
		this.types = types;
	}
	/**
	 * @return Returns the category.
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category The category to set.
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return Returns the categories.
	 */
	public ArrayList getCategories() {
		return categories;
	}
	/**
	 * @param categories The categories to set.
	 */
	public void setCategories(ArrayList categories) {
		this.categories = categories;
	}
}

