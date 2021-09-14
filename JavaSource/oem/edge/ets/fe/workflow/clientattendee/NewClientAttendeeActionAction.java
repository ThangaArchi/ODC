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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.newissue.NewIssueVO;
import oem.edge.ets.fe.workflow.util.CharUtils;
 
/**
 * Class       : NewClientAttendeeActionAction
 * Package     : oem.edge.ets.fe.workflow.clientattendee
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NewClientAttendeeActionAction extends WorkflowAction

{

    public ActionForward executeWorkflow(ActionMapping mapping,
            WorkflowForm form, HttpServletRequest request,
            HttpServletResponse response) {

        ActionForward forward = new ActionForward();
        NewClientAttendeeFormBean newClientAttendeeFormBean = (NewClientAttendeeFormBean) form;
        NewClientAttendeeVO vo = new NewClientAttendeeVO();

        String name = "";
        String company = "";
        String title = "";

        try {

            vo = (NewClientAttendeeVO) newClientAttendeeFormBean
                    .getWorkflowObject();
            NewClientAttendeeBL bl = new NewClientAttendeeBL();

            if (vo.getAcompany() == null) {
               
            	ArrayList errs = getValidationErrors(vo);
				if (errs != null && errs.size() != 0) {
					request.getSession().setAttribute("errorMessages", errs);
				}else{
                    if (!bl.saveClientAttendee(vo)) {
                        setError(request, "NewClientAttendee.saveFailed");
                        System.out.println("........Could not save new client attendee");
                    } else {
                        request.setAttribute("poison", " ");
                    }
				}

            } else {
            	ArrayList errs = getValidationErrors(vo);
				if (errs != null && errs.size() != 0) {
					request.getSession().setAttribute("errorMessages", errs);
					
				}else{
                if (!bl.saveClientAttendees(vo)) {
                    
                } else {
                    request.setAttribute("poison", " ");
                }
				}
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        /*
         * System.out.println("...................In New Client Attendee
         * Action"); System.out.println("................name = "+name);
         * System.out.println("................title = "+title);
         * System.out.println("................company = "+company);
         */

        forward = mapping.findForward("success");
        return (forward);

    }
	private ArrayList getValidationErrors(NewClientAttendeeVO vo) {
		ArrayList errs = new ArrayList();
		 if (vo.getAcompany() == null) {
		 	if(vo.getFname()==null || vo.getFname().trim().length()==0)
            	errs.add("First name must be filled in");
		 	if(vo.getFname()!=null && CharUtils.isAlNum(vo.getFname())==false)
            	errs.add("First name can only contain alphabets, digits and space characters");
		 	if(vo.getFname()!=null && vo.getFname().trim().length()>40)
		 		errs.add("We're sorry, but a first name larger than 40 characters is inadmissible in this system");

		 	if(vo.getLname()==null || vo.getLname().trim().length()==0)
            	errs.add("Last name must be filled in");
		 	if(vo.getLname()!=null && CharUtils.isAlNum(vo.getLname())==false)
            	errs.add("Last name can only contain alphabets, digits and space characters");
		 	if(vo.getLname()!=null && vo.getLname().trim().length()>40)
		 		errs.add("We're sorry, but a Last name larger than 40 characters is inadmissible in this system");

		 	if(vo.getTitle()==null || vo.getTitle().trim().length()==0)
            	errs.add("Title must be filled in");
		 	if(vo.getTitle()!=null && CharUtils.isAlNum(vo.getTitle())==false)
            	errs.add("Title can only contain alphabets, digits and space characters");
		 	if(vo.getTitle()!=null && vo.getTitle().trim().length()>40)
		 		errs.add("We're sorry, but a Title larger than 40 characters is inadmissible in this system");

		 	if(vo.getCompany()==null || vo.getCompany().trim().length()==0)
            	errs.add("Please select a Company");
		  
		 }else
		 {
		 	ArrayList temperrs= null;
		 	for(int i =0; i<vo.getAcompany().length; i++)
		 	{
		 		System.out.println("Checking attendee entry #"+i);
		 		System.out.println(vo.getAfname());
		 		System.out.println(vo.getAlname());
		 		System.out.println(vo.getAcompany());
		 		System.out.println(vo.getAtitle());
		 		temperrs = new ArrayList();
		 		if(vo.getAfname()[i]==null || vo.getAfname()[i].trim().length()==0)
	            	temperrs.add("First name must be filled in.");
			 	if(vo.getAfname()[i]!=null && CharUtils.isAlNum(vo.getAfname()[i])==false)
	            	temperrs.add("First name can only contain alphabets, digits and space characters.");
			 	if(vo.getAfname()[i]!=null && vo.getAfname()[i].trim().length()>40)
			 		temperrs.add("We're sorry, but a first name larger than 40 characters is inadmissible in this system.");

			 	if(vo.getAlname()[i]==null || vo.getAlname()[i].trim().length()==0)
	            	temperrs.add("Last name must be filled in.");
			 	if(vo.getAlname()[i]!=null && CharUtils.isAlNum(vo.getAlname()[i])==false)
	            	temperrs.add("Last name can only contain alphabets, digits and space characters");
			 	if(vo.getAlname()[i]!=null && vo.getAlname()[i].trim().length()>40)
			 		temperrs.add("We're sorry, but a last name larger than 40 characters is inadmissible in this system");

			 	if(vo.getAtitle()[i]==null || vo.getAtitle()[i].trim().length()==0)
	            	errs.add("Title must be filled in");
			 	if(vo.getAtitle()[i]!=null && CharUtils.isAlNum(vo.getAtitle()[i])==false)
	            	errs.add("Title can only contain alphabets, digits and space characters");
			 	if(vo.getAtitle()[i]!=null && vo.getAtitle()[i].trim().length()>40)
			 		errs.add("We're sorry, but a Title larger than 40 characters is inadmissible in this system");

			 	
			 	if(vo.getAcompany()[i]==null || vo.getAcompany()[i].trim().length()==0)
	            	temperrs.add("Please select a Company for client");
			 	System.out.println("Error count for this attendee is "+temperrs.size());
			 	if(temperrs.size()!=0)
			 	{
			 		errs.add(" - - Errors for client attendee "+(i+1)+" - - ");
					errs.addAll(temperrs);
			 	}
		 	}
		 	if(errs.size()>0)
		 	{
		 	String e = "The list submitted was:\n";
		 	for(int i =0; i<vo.getAcompany().length; i++)
		 		e += vo.getAfname()[i]+" "+vo.getAlname()[i]+" - "+vo.getAtitle()[i]+" from "+vo.getAcompany()[i]+", "; 
		 	errs.add(e);
		 	}
            
         }
		if(errs.size()==0)
		{
			if(vo.getAcompany() == null)
			{
				vo.setFname(CharUtils.SQLize(vo.getFname()));
				vo.setLname(CharUtils.SQLize(vo.getLname()));
				vo.setTitle(CharUtils.SQLize(vo.getTitle()));
				vo.setCompany(CharUtils.SQLize(vo.getCompany()));
			}
			else
			{
				String[] tempCompany = vo.getAcompany();
				String[] tempFname = vo.getAfname();
				String[] tempLname = vo.getAlname();
				String[] tempTitle = vo.getAtitle();
				
				for(int i =0; i<vo.getAcompany().length; i++)
			 	{
					tempCompany[i] = CharUtils.SQLize(tempCompany[i]);
					tempFname[i] = CharUtils.SQLize(tempFname[i]);
					tempLname[i] = CharUtils.SQLize(tempLname[i]);
					tempTitle[i] = CharUtils.SQLize(tempTitle[i]);
			 	}
				vo.setAcompany(tempCompany);
				vo.setAfname(tempFname);
				vo.setAlname(tempLname);
				vo.setAtitle(tempTitle);
			}
		}
		return errs;
	}
}
