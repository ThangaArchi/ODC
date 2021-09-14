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


package oem.edge.ets.fe.workflow.sa.initialize;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import oem.edge.ets.fe.workflow.setmet.SetMetDAO;
import oem.edge.ets.fe.workflow.util.HistoryUtils;
import oem.edge.ets.fe.workflow.util.SelectControl;
//private static Log logger = WorkflowLogger.getLogger(InitializeBL.class);
/**
 * Class       : InitializeBL
 * Package     : oem.edge.ets.fe.workflow.sa.initialize
 * Description : 
 * Date		   : Feb 2, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class InitializeBL {
	public void fillVOOptions(InitializeVO vo)
	{
		SetMetDAO dao = new SetMetDAO();
		ArrayList clientPeople = dao.getClient_Attendees(vo.getCompany());
		ArrayList ibmPeople = dao.getIBM_Attendees(vo.getProjectID());
		ArrayList accountContactPeople = null;
		try{accountContactPeople=dao.getAccntcontByroles(vo.getProjectID());}catch(Exception e){}
		
		vo.setAllAttendees(clientPeople);
		vo.setAllBackups(accountContactPeople);
		vo.setAllContacts(accountContactPeople);
		vo.setAllIBMers(ibmPeople);
		//vo.setAllSponsors(ibmPeople);
		vo.setAllSelectedAttendees(new ArrayList());
		
		vo.setAllDays(getDays());
		vo.setAllMonths(getMonths());
		vo.setAllYears(getYears());
		vo.setAllNsiRatings(getNSIs());
		vo.setAllQbrYears(getYears());
		vo.setAllQuarters(getMonths());
		//vo.setAllBiweeklyStatuses(getBiweeklyStatuses());
		
	}
	public void fillVO(InitializeVO vo)
	{
		fillVOOptions(vo);
		
		//vo.setBiweeklyFlag(new String[]{"Y"});
		
		vo.setPlannedDay(getCurrentDay());
		vo.setPlannedMonth(getCurrentMonth());
		vo.setPlannedYear(getCurrentYear());
		
		vo.setQbrYear(getCurrentYear());
		vo.setQbrQuarter(getCurrentMonth());
		/*vo.setBiweeklyDay(getCurrentDay());
		vo.setBiweeklyMonth(getCurrentMonth());
		vo.setBiweeklyYear(getCurrentYear());
		
		vo.setRatingFromDay(getCurrentDay());
		vo.setRatingFromMonth(getCurrentMonth());
		vo.setRatingFromYear(getCurrentYear());

		vo.setRatingToDay(getCurrentDay());
		vo.setRatingToMonth(getCurrentMonth());
		vo.setRatingToYear(getCurrentYear());*/

	}
	
	/**
	 * @param vo
	 * @param workflowID
	 */
	public void fillVO(InitializeVO vo, String workflowID) {
		InitializeDAO dao = new InitializeDAO();
		
		InitializeVO vo_old = (InitializeVO)dao.getWorkflowObject(workflowID,vo.getCompany());

		fillVO(vo);

		vo.setAllAttendees(vo_old.getAllAttendees());
		vo.setAllSelectedAttendees(vo_old.getAllSelectedAttendees());
		
		vo.setAccountContact(vo_old.getAccountContact());
		vo.setBackupContact(vo_old.getBackupContact());
		//vo.setExecSponsor(vo_old.getExecSponsor());
		vo.setIbmAttendees(vo_old.getIbmAttendees());
		vo.setMeetingLocation(vo_old.getMeetingLocation());
		vo.setNsiRating(vo_old.getNsiRating());
		vo.setPlannedDay(vo_old.getPlannedDay());
		vo.setPlannedMonth(vo_old.getPlannedMonth());
		vo.setPlannedYear(vo_old.getPlannedYear());
		vo.setQbrQuarter(vo_old.getQbrQuarter());
		vo.setQbrYear(vo_old.getQbrYear());
		/*vo.setBiweeklyDay(vo_old.getBiweeklyDay());
		vo.setBiweeklyFlag(vo_old.getBiweeklyFlag());
		vo.setBiweeklyMonth(vo_old.getBiweeklyMonth());
		vo.setBiweeklyStatus(vo_old.getBiweeklyStatus());
		vo.setBiweeklyYear(vo_old.getBiweeklyYear());
		vo.setRatingFromDay(vo_old.getRatingFromDay());
		vo.setRatingFromMonth(vo_old.getRatingFromMonth());
		vo.setRatingFromYear(vo_old.getRatingFromYear());
		vo.setRatingToDay(vo_old.getRatingToDay());
		vo.setRatingToMonth(vo_old.getRatingToMonth());
		vo.setRatingToYear(vo_old.getRatingToYear());*/
		
		alterVOIfNoBiweekly(vo);
		
	}
	
	public void retainClientAttendeeSelection(InitializeVO vo)
	{
		ArrayList clientPeople = vo.getAllAttendees();
		String[] currentSelection = vo.getAttendees();

		if(clientPeople==null || currentSelection==null)
			return;

		ArrayList nonAttendees=new ArrayList(); //left side box in screen
		ArrayList selectedAttendees=new ArrayList(); //right side box in screen
		for(int j=0; j<clientPeople.size(); j++)
		{
			boolean isAttendee = false;
			for(int i=0; i<currentSelection.length; i++)	
				if(((SelectControl)clientPeople.get(j)).getValue().equals(currentSelection[i]))isAttendee=true;
			if(!isAttendee)
				nonAttendees.add(clientPeople.get(j));
			else
				selectedAttendees.add(clientPeople.get(j));
		}
		vo.setAllAttendees(nonAttendees);
		vo.setAllSelectedAttendees(selectedAttendees);
	}
	
	public void createNewQBR(InitializeVO vo) {
		InitializeDAO dao = new InitializeDAO();
		alterVOIfNoBiweekly(vo);
		dao.saveWorkflowObject(vo);
		HistoryUtils.setHistory(vo,dao.getIdentifyStageID());
	}


	public void updateQBR(InitializeVO vo) {
		InitializeDAO dao = new InitializeDAO();
		alterVOIfNoBiweekly(vo);
	
		InitializeVO oldVO = new InitializeVO();
		try {
			PropertyUtils.copyProperties(oldVO,vo);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		fillVO(oldVO,vo.getWorkflowID());

		//Make a complete selection of the previously selected attendees
		if(oldVO.getAllSelectedAttendees()!=null && oldVO.getAllSelectedAttendees().size()>0)
		{
			String[] attendees = new String[oldVO.getAllSelectedAttendees().size()];
			for(int i=0;i<oldVO.getAllSelectedAttendees().size();i++)
			{
				if(oldVO.getAllSelectedAttendees().get(i)!=null)
					attendees[i] = ((SelectControl)oldVO.getAllSelectedAttendees().get(i)).getValue().trim();
				else
					attendees[i]=null;
			}
			oldVO.setAttendees(attendees);
		}
		
		dao.updateWorkflowObject(vo);
		HistoryUtils.setHistory(oldVO,vo,dao.getIdentifyStageID());
	}
	
	private ArrayList getDays()
	{
		ArrayList days = new ArrayList();
		for(int i = 1; i<32; i++)
			days.add(new SelectControl(Integer.toString(i),Integer.toString(i)));
		return days;
	}
	private ArrayList getMonths()
	{
		ArrayList months = new ArrayList();
		months.add(new SelectControl("January","1"));
		months.add(new SelectControl("February","2"));
		months.add(new SelectControl("March","3"));
		months.add(new SelectControl("April","4"));
		months.add(new SelectControl("May","5"));
		months.add(new SelectControl("June","6"));
		months.add(new SelectControl("July","7"));
		months.add(new SelectControl("August","8"));
		months.add(new SelectControl("September","9"));
		months.add(new SelectControl("October","10"));
		months.add(new SelectControl("November","11"));
		months.add(new SelectControl("December","12"));
		return months;
	}
	private ArrayList getYears()
	{
		ArrayList years = new ArrayList();
		for(int i = 2006; i<2013; i++)
			years.add(new SelectControl(Integer.toString(i),Integer.toString(i)));
		return years;
	}
	private ArrayList getNSIs()
	{
		ArrayList nsi = new ArrayList();
		for(int i = 0; i<100; i++)
			nsi.add(new SelectControl(Integer.toString(i),Integer.toString(i)));
		return nsi;
	}
	private ArrayList getQuarters()
	{
		ArrayList quarters = new ArrayList();
		quarters.add(new SelectControl("First Quarter","01"));
		quarters.add(new SelectControl("Second Quarter","02"));
		quarters.add(new SelectControl("Third Quarter","03"));
		quarters.add(new SelectControl("Fourth Quarter","04"));
		return quarters;
	}
	private ArrayList getBiweeklyStatuses()
	{
		ArrayList biweeklyStatuses = new ArrayList();
		biweeklyStatuses.add(new SelectControl("Complete","Complete"));
		biweeklyStatuses.add(new SelectControl("Cancelled","Cancelled"));
		biweeklyStatuses.add(new SelectControl("Reviewed","Reviewed"));
		biweeklyStatuses.add(new SelectControl("Skipped","Skipped"));
		return biweeklyStatuses;
	}
	private String[] getCurrentYear() {
		java.util.Date d = new Date();
		return new String[]{Integer.toString(d.getYear()+1900)};
	}
	private String[] getCurrentMonth() {
		java.util.Date d = new Date();
		return new String[]{Integer.toString(d.getMonth()+1)};
	}
	private String[] getCurrentDay() {
		java.util.Date d = new Date();
		return new String[]{Integer.toString(d.getDate())};
	}
	private void alterVOIfNoBiweekly(InitializeVO vo)
	{
	/*	if(vo.getBiweeklyFlag()[0].equals("N"))
		{
			vo.setBiweeklyDay(getCurrentDay());
			vo.setBiweeklyMonth(getCurrentMonth());
			vo.setBiweeklyYear(getCurrentYear());
			vo.setBiweeklyStatus(new String[]{"Complete"});
		}*/
	}
	/**
	 * @param projectID
	 * @return
	 */
	public String[] getPreviousWorkflow(String projectID) {
		InitializeDAO dao = new InitializeDAO();
		return dao.getPreviousWorkflow(projectID);
	}
}

