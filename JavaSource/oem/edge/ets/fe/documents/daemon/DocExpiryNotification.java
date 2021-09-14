/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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
 * Created on Jun 30, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.documents.daemon;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.NotificationMsgHelper;
import oem.edge.ets.fe.documents.data.DocExpiryNotificationDAO;
import oem.edge.ets.fe.documents.data.DocReaderDAO;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;




/**
 * @author amar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class DocExpiryNotification
{
	
	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DocExpiryNotification.class);
	
	public void printArrayList(String arrayListName, ArrayList arrayList)
	{
		System.out.println(arrayListName+"==>"+arrayList);
	}
	
	public ArrayList addArrayList(ArrayList toArrayList, ArrayList addArrayList)
	{
		Iterator editUsersListIterator = addArrayList.iterator();
		/************************Start****************************************/
		while(editUsersListIterator.hasNext())
			{
	
			String userId = (String) editUsersListIterator.next(); 
	
			if(!toArrayList.contains(userId))
				{
				toArrayList.add(userId);
				}
	
	
			}//while loop one
	/*********************************************************************/
		
		return toArrayList;
	}
	
	public void processExpiryDocs() throws SQLException, Exception
	{
		
		DocExpiryNotificationDAO docDAO = new DocExpiryNotificationDAO();
		
		String expiryDaysString = Defines.DOC_EXPIRY_NOTIFICATION_DAYS;
		
		oem.edge.common.Global.Init();
		
		StringTokenizer tokenizer = new StringTokenizer(expiryDaysString, ",");
				
		while(tokenizer.hasMoreElements())
		{
			int expiryDays = Integer.parseInt((String)tokenizer.nextElement());
			//System.out.println(Integer.parseInt((String)tokenizer.nextElement()));
			Vector etsDocVector = docDAO.getExpiryEtsDocs(expiryDays);
			
			System.out.println("etsDocVector.size()===> "+ etsDocVector.size() + 
									", Days to expire the docs==>" + expiryDays);
			m_pdLog.debug("etsDocVector.size()===> "+ etsDocVector.size() + 
					", Days to expire the docs==>" + expiryDays);
			//	DocIds Iterator
			Iterator vectIterator = etsDocVector.iterator();
			
			while(vectIterator.hasNext())
				
				{
				ETSDoc etsDoc = (ETSDoc)vectIterator.next();
				
				processExpiryDoc(etsDoc, expiryDays);
				}//inner while loop
			
		}//outer while loop
	}
		
		
	public void processExpiryDoc(ETSDoc etsDoc, int iExpiryDays) throws SQLException, Exception
	{
	
		DocExpiryNotificationDAO docDAO = new DocExpiryNotificationDAO();
		
		ArrayList groupUsersList = new ArrayList();
	
		/*
		 * if(!etsDoc.IsDPrivate())
		{
			ArrayList managersList = docDAO.getProjManagersList(etsDoc.getProjectId());
			
			addArrayList(groupUsersList,managersList);
			
			System.out.println(managersList);
			m_pdLog.debug(managersList);
			//check the list and validate for the user emails and send a mail
		}*/
		//else
		//{
			ArrayList managersList = docDAO.getProjManagersList(etsDoc.getProjectId());
			
			ArrayList editUsersList = docDAO.getRestrictedEditUsersList(etsDoc.getId(), etsDoc.getProjectId());
			
			ArrayList editGroupsList = docDAO.getRestrictedEditGroupsList(etsDoc.getId(), etsDoc.getProjectId());
			
			Iterator groupsIterator	=	editGroupsList.iterator();
			
			
			while(groupsIterator.hasNext())
			{					
				ArrayList editGroupUsersList = docDAO.getEditGroupUsersList((String)groupsIterator.next(), etsDoc.getProjectId());	
				addArrayList(groupUsersList,editGroupUsersList);
			}
			
			/************Start************Print message method if possible to include****/
			printArrayList("managersList", managersList);
			printArrayList("editUsersList",editUsersList);
			printArrayList("editGroupsList",editGroupsList);
			printArrayList("groupUsersList",groupUsersList);
			
			/****************************************************************************/
			addArrayList(groupUsersList,editUsersList);
			
			addArrayList(groupUsersList,managersList);
			
			
			System.out.println("IBM Only char " + etsDoc.getIBMOnlyStr());
			m_pdLog.debug("IBM Only char  " + etsDoc.getIBMOnlyStr());

			if(etsDoc.getIBMOnlyStr().equals(new Character(Defines.ETS_IBM_ONLY).toString()))
				{
					groupUsersList = docDAO.getIBMMembers(groupUsersList);
					System.out.println("groupUsersList==>"+groupUsersList);
					m_pdLog.debug("groupUsersList==>"+groupUsersList);
				}
			
			
			System.out.println(groupUsersList);
			
			
		// }
		
		String emailString = docDAO.getProjMemberEmails(etsDoc.getProjectId(), groupUsersList);
		
		System.out.println(emailString.substring(0, emailString.length()-1));
		m_pdLog.debug(emailString.substring(0, emailString.length()-1));
		
		String comments = "No comments for this document";
		//send email to the uers of the doc
		/*
		 * sendDocExpirationEMail(etsDoc, emailString, etsDoc.getProjectId(), etsDoc.getUserId(), comments);
		 * 
		 * 
		 */
		
		String sMgrEmail = getWSOwnersEmailString(etsDoc);
		
		sendDocExpirationEMail(etsDoc, iExpiryDays, emailString, etsDoc.getProjectId(), sMgrEmail, comments);
		
		System.out.println("End of the process");
		m_pdLog.debug("End of the process");
		 
	}
	public boolean sendDocExpirationEMail
		(ETSDoc etsDoc, int expiryDays, String sendTo, String project, 
				String sMgrEmail, String comments) 
	{
		int topCategory = 0;
						
		DocReaderDAO docReaderDAO = new DocReaderDAO();
		//UnbrandedProperties unBrandproperties = null;
		String projectType = null;
		String projectName = null;
		String linkId = null;
		
		try
		{
			docReaderDAO.prepare();
			topCategory = docReaderDAO.getTopCatId( etsDoc.getProjectId(), Defines.DOCUMENTS_VT);
			projectType = docReaderDAO.getProjectType(etsDoc.getProjectId());
			projectName = docReaderDAO.getProjectName(etsDoc.getProjectId());
			if(projectType.equals(Defines.AIC_WORKSPACE_TYPE))
				{
				projectType = "IBM Collaboration Center";
				linkId = Defines.AIC_LINKID ;
				}
			else
				{
				projectType = "E&TS Connect";
				linkId = Defines.LINKID;
				}
							
		} 
		catch (SQLException e1)
		{
			e1.printStackTrace();
			
		} catch (Exception e)
		{
			e.printStackTrace();
			
		}
		
		boolean sent = false;
		
		String subject = projectName +" - Document is about to expire:  " + etsDoc.getName();
					
		StringBuffer message = new StringBuffer();
   
		message.append("\n");
		message.append("You are an author or editor of a document in the " +
				"following workspace on IBM Customer Connect that will " +
				"expire in " + expiryDays + " day(s):\n");
		
		message.append(projectName);
		message.append("\n\n");
		message.append("Once a document expires, it will no longer be accessible " +
				"by general workspace members. " +
				"To change the expiration date of the document, " +
				"navigate to the document using the URL" +
				" below and click on the \"update properties\" link.");
		message.append("\n");
		message.append("The details of the document are as follows: ");
		message.append("\n");
		message.append("==============================================================");
		message.append("\n");
		message.append("         Workspace:          " + projectName);
		message.append("\n");
		message.append("         Document name:      " + etsDoc.getName());
		message.append("\n");
		message.append("         Author:             " + etsDoc.getUserId());
		message.append("\n");
		message.append("         Create Date:        " + new SimpleDateFormat("MM/dd/yyyy").format(new Date(etsDoc.getPublishDate())));
		message.append("\n");
		message.append("         Last Updated:       " + new SimpleDateFormat("MM/dd/yyyy").format(new Date(etsDoc.getUpdateDate())));
		message.append("\n");
		message.append("         Expiration Date:    " + new SimpleDateFormat("MM/dd/yyyy").format(new Date(etsDoc.getExpiryDate())));
		message.append("\n");
		message.append("\n");			
		message.append("To view this document, click on the following  URL and log-in: ");
		message.append("\n");
		message.append(oem.edge.common.Global.getUrl("ets/displayDocumentDetails.wss?")+
				"proj="+etsDoc.getProjectId()+"&tc="+topCategory+"&cc="+etsDoc.getCatId()+"&docid="+
				etsDoc.getId()+"&linkid="+linkId+"&hitrequest=true");
		
		message.append("\n");
		message.append(NotificationMsgHelper.getEmailFooter(projectType));
		try
		{
			ETSUserDetails etsUserDetails = new ETSUserDetails();
			
			etsUserDetails.setWebId(etsDoc.getUserId());
			etsUserDetails.extractUserDetails(docReaderDAO.getConnection());
						
			sent = NotificationMsgHelper.sendEMail(etsUserDetails.getEMail(),sendTo,"","",oem.edge.common.Global.mailHost,message.toString(),subject,sMgrEmail);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				docReaderDAO.cleanup();
			} catch (SQLException e2) 
			{
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}

		return sent;
	}
 public String getWSOwnersEmailString(ETSDoc etsDoc)
 {
 	DocExpiryNotificationDAO docDAO = new DocExpiryNotificationDAO();
 	DocReaderDAO docReaderDAO = new DocReaderDAO();
 	
 	
 	String ownersEmailsString = "";
 						
	try
	{
		docReaderDAO.prepare();
		
		Vector ownersVector = docReaderDAO.getUsersByProjectPriv(etsDoc.getProjectId(), Defines.OWNER);
		
		ArrayList ownersIdList = new ArrayList();
		
		Iterator iterator = ownersVector.iterator();
		
		while(iterator.hasNext())
		{
			
			ETSUser user = (ETSUser)iterator.next();
			
			ownersIdList.add(user.getUserId());
		}
		
		ownersEmailsString = docDAO.getProjMemberEmails(etsDoc.getProjectId(), ownersIdList);
		
		
	} 
	catch (SQLException e1)
	{
		
		e1.printStackTrace();
	}
	catch (Exception e1)
	{
		e1.printStackTrace();
	}
	finally
	{
		try
		{
			docReaderDAO.cleanup();
		} 
		catch (SQLException e)
		{
			
			e.printStackTrace();
		}
	}
	return ownersEmailsString;
 }
}