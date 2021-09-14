/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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
 * Created on Jan 17, 2005
 */

package oem.edge.ets.fe.self;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSUtils;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ModuleConfig;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSEditSelfAssessmentAction extends Action {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.5";

	public ETSEditSelfAssessmentAction() {
		super();
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		String dispatch = mapping.getParameter();
		Connection con = null;
		ActionForward forward = new ActionForward();

		EdgeAccessCntrl es = new EdgeAccessCntrl();


		try {
			
			

			con = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, con)) {
				return new ActionForward("/login.jsp");
			}

			String sOp = ETSUtils.checkNull(request.getParameter("etsop"));

			if (sOp.equalsIgnoreCase("editself")) {
				forward = perfomEditSelfAssessment(con,mapping,form,request,response);
			} else if (sOp.equalsIgnoreCase("editconfirm")) {
				forward = perfomUpdateSelfAssessment(con,mapping,form,request,response,es);
			}

			return forward;

		} catch (Exception e) {
			e.printStackTrace();
			return mapping.findForward("error");
		} finally {
			ETSDBUtils.close(con);
		}


	}

	public ActionErrors validate(ETSSelfAssessmentForm self, HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		String sTitle = self.getTitle();
		if (sTitle == null || sTitle.trim().equalsIgnoreCase("")) {
			errors.add("Title",new ActionError("error.title.empty"));
		}

		String sCalMonth = ETSUtils.checkNull(self.getStartMonth());
		String sCalDay = ETSUtils.checkNull(self.getStartDay());
		String sCalYear = ETSUtils.checkNull(self.getStartYear());

		if (!isDateValid(sCalDay,sCalMonth,sCalYear)) {
			errors.add("Date",new ActionError("error.assessdate.invalid"));
		}

		String sMonth = ETSUtils.checkNull(self.getMemberDueMonth());
		String sDay = ETSUtils.checkNull(self.getMemberDueDay());
		String sYear = ETSUtils.checkNull(self.getMemberDueYear());

		if (!isDateValid(sDay,sMonth,sYear)) {
			errors.add("Date1",new ActionError("error.memberdate.invalid"));
		}

		String sAssessmentOwner = ETSUtils.checkNull(self.getAssessmentOwner());
		if (sAssessmentOwner == null || sAssessmentOwner.trim().equalsIgnoreCase("")) {
			errors.add("AssignmentOwner",new ActionError("error.assessowner.invalid"));
		}

		String sMonth1 = ETSUtils.checkNull(self.getAssessDueMonth());
		String sDay1 = ETSUtils.checkNull(self.getAssessDueDay());
		String sYear1 = ETSUtils.checkNull(self.getAssessDueYear());
		if (!isDateValid(sDay1,sMonth1,sYear1)) {
			errors.add("Date2",new ActionError("error.compiledate.invalid"));
		}

		String sPlanOwner = ETSUtils.checkNull(self.getPlanOwner());
		if (sPlanOwner == null || sPlanOwner.trim().equalsIgnoreCase("")) {
			errors.add("PlanOwner",new ActionError("error.planowner.invalid"));
		}

		String sMonth2 = ETSUtils.checkNull(self.getPlanDueMonth());
		String sDay2 = ETSUtils.checkNull(self.getPlanDueDay());
		String sYear2 = ETSUtils.checkNull(self.getPlanDueYear());
		if (!isDateValid(sDay2,sMonth2,sYear2)) {
			errors.add("Date3",new ActionError("error.actiondate.invalid"));
		}

		long timecreate = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " 00:00:00.000000000").getTime();
		long timememberdue = Timestamp.valueOf(sYear + "-" + sMonth + "-" + sDay + " 00:00:00.000000000").getTime();
		long timecompiledue = Timestamp.valueOf(sYear1 + "-" + sMonth1 + "-" + sDay1 + " 00:00:00.000000000").getTime();
		long timeactiondue = Timestamp.valueOf(sYear2 + "-" + sMonth2 + "-" + sDay2 + " 00:00:00.000000000").getTime();


		if (timecreate > timememberdue) {
			errors.add("Date4",new ActionError("error.memberdate.createdate"));
		}
		
		if (timememberdue > timecompiledue) {
			errors.add("Date5",new ActionError("error.compiledate.memberdate"));
		}
		
		if (timecompiledue > timeactiondue) {
			errors.add("Date6",new ActionError("error.actiondate.compiledate"));
		}

		return errors;


	}


	public ActionForward perfomUpdateSelfAssessment(Connection con,ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, EdgeAccessCntrl es) {


		ETSSelfAssessmentForm self = (ETSSelfAssessmentForm) form;

		try {

			String sProjectID = request.getParameter("proj");
			String sSelfID = request.getParameter("self");

			ActionErrors errors = validate(self,request);

			if (errors.size() > 0) {

				ArrayList listUsers = new ArrayList();

				listUsers = ETSSelfDAO.getInternalNotClientUsersInWorkspace(con,sProjectID);

				ETSSelfAssessment assess = ETSSelfDAO.getSelfAssessment(con,sProjectID,sSelfID);

				ArrayList memberlist = assess.getMembers();

				request.setAttribute("memberlist",memberlist);

				request.setAttribute("internalUsers",listUsers);

				saveErrors(request,errors);

				return mapping.findForward("error");

			} else {

				ActionForward forward = mapping.findForward("confirm");

				StringBuffer sPath = new StringBuffer(forward.getPath());

				// create the self assessment
				boolean created = ETSSelfDAO.updateSelfAssessment(con,sSelfID,sProjectID,self);

				if (created) {

					// setup the due dates
					ETSSelfDAO.updateSelfAssessmentDueDates(con,sSelfID,sProjectID,self);
			
					ETSSelfMail mail = ETSSelfMailFunctions.createEditSelfAssessmentMail(con,sSelfID,sProjectID,es.gIR_USERN);
					
					boolean sent = ETSSelfMailFunctions.sendEmail(mail);

					sPath.append("?success=true");
					return new ActionForward(sPath.toString());

				} else {
					sPath.append("?success=false");
					return new ActionForward(sPath.toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return mapping.findForward("error");
		}

	}

	public ActionForward perfomEditSelfAssessment(Connection con, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		try {




			ETSSelfAssessmentForm self = (ETSSelfAssessmentForm) form;

			String sSelfID = ETSUtils.checkNull(request.getParameter("self"));
			String sProjectId = ETSUtils.checkNull(request.getParameter("proj"));

			ETSSelfAssessment assess = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfID);

			SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");

			String sStartDate = df.format(assess.getStartDate());

			self.setSelfId(assess.getSelfId());

			self.setStartMonth(sStartDate.substring(0,2));
			self.setStartDay(sStartDate.substring(3, 5));
			self.setStartYear(sStartDate.substring(6, 10));

			String MemberAssessmentDate = "";
			String AssessDate = "";
			String PlanDate = "";

			ArrayList duedates = assess.getDueDates();

			for (int i = 0; i < duedates.size(); i++) {

				ETSSelfAssessmentDueDate due = (ETSSelfAssessmentDueDate) duedates.get(i);

				if (due.getStep().equalsIgnoreCase(ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT)) {
					MemberAssessmentDate = df.format(due.getDueDate());
				}
				if (due.getStep().equalsIgnoreCase(ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT)) {
					AssessDate = df.format(due.getDueDate());
				}
				if (due.getStep().equalsIgnoreCase(ETSSelfConstants.SELF_STEP_ACTION_PLAN)) {
					PlanDate = df.format(due.getDueDate());
				}
			}


			self.setMemberDueMonth(MemberAssessmentDate.substring(0, 2));
			self.setMemberDueDay(MemberAssessmentDate.substring(3, 5));
			self.setMemberDueYear(MemberAssessmentDate.substring(6, 10));

			self.setAssessDueMonth(AssessDate.substring(0, 2));
			self.setAssessDueDay(AssessDate.substring(3, 5));
			self.setAssessDueYear(AssessDate.substring(6, 10));

			self.setPlanDueMonth(PlanDate.substring(0, 2));
			self.setPlanDueDay(PlanDate.substring(3, 5));
			self.setPlanDueYear(PlanDate.substring(6, 10));

			self.setTitle(assess.getTitle());
			self.setAssessmentOwner(assess.getAssessmentOwner());

			self.setPlanOwner(assess.getPlanOwner());

			ArrayList listUsers = new ArrayList();

			String sProjectID = request.getParameter("proj");

			listUsers = ETSSelfDAO.getInternalNotClientUsersInWorkspace(con,sProjectID);

			ArrayList memberlist = assess.getMembers();

			request.setAttribute("memberlist",memberlist);

			request.setAttribute("internalUsers",listUsers);
			request.setAttribute("self",assess);


			return mapping.findForward("edit");

		} catch (Exception e) {
			e.printStackTrace();
			return mapping.findForward("error");
		}


	}

	public boolean isDateValid(String sDay, String sMonth, String sYear) {

		int month = Integer.parseInt(sMonth.trim());
		int day = Integer.parseInt(sDay.trim());
		int year = Integer.parseInt(sYear.trim());

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,year);
		cal.set(Calendar.MONTH,month -1);
		int iMaxDaysInMonth =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);

		System.out.println("max  for '"+cal.get(Calendar.MONTH)+"'= "+iMaxDaysInMonth);
		System.out.println("min  for '"+cal.get(Calendar.MONTH)+"'= "+iMinDaysInMonth);

		if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
			return true;
		} else{
			return false;
		}

	}

}
