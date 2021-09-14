/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

package oem.edge.ets.fe.acmgt.actions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 * @author Suresh
 *
 */
public class ImportMemberListAction extends BaseAcmgtAction{
	
	/** Stores the Logging object */
	private static final Log logger =
		EtsLogger.getLogger(ImportMemberListAction.class);
	
	private String m_strUserRole = null;
	
	private EdgeAccessCntrl m_udEdgeAccess = null;

	private static final String DELIM = ",";
    
	private static final String USER_NAME_HEADER = "Name";
	private static final String USER_ID_HEADER = "User Id";
	private static final String USER_EMAIL_HEADER = "User Email";
	private static final String USER_ACCESS_HEADER = "Access Level";
	private static final String USER_JOB_HEADER = "Job Responsibility";
	private static final String USER_MSGRID_HEADER = "Messenger Id";
		
	public ImportMemberListAction() {
		super();
	}
	
	/**
	 * @see oem.edge.ets.fe.acmgt.actions.BaseAcmgtAction#executeAction(
	 * org.apache.struts.action.ActionMapping,
	 * org.apache.struts.action.ActionForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	
	protected ActionForward executeAction(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {

		
		String strForward = "";
				
		ActionForward forward = new ActionForward();
		ActionErrors pdErrors = null;

		BaseAddMemberForm udForm = (BaseAddMemberForm) pdForm; 
		
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		Hashtable params = new Hashtable();  //for params
		Connection conn = null;
		
		try {

			if (!Global.loaded) {
				Global.Init();
			}

			//get connection
			conn = ETSDBUtils.getConnection();
			
			if (es.GetProfile(pdResponse, pdRequest)) {
								
				// get linkid, proj, tc 
				String strLinkId = AmtCommonUtils.getTrimStr(pdRequest.getParameter("linkid"));
				String strProjectId = AmtCommonUtils.getTrimStr(pdRequest.getParameter("proj"));
				String strTopCatId = AmtCommonUtils.getTrimStr(pdRequest.getParameter("tc"));
				String from = AmtCommonUtils.getTrimStr(pdRequest.getParameter("from"));
				
				if(StringUtil.isNullorEmpty(strLinkId)) {
					logger.debug("Import Action :: Getting params from FORM GETTER...."); 
					strLinkId = udForm.getLinkid(); 
				}
				if(StringUtil.isNullorEmpty(strProjectId)) strProjectId = udForm.getProj(); 
				if(StringUtil.isNullorEmpty(strTopCatId)) strTopCatId = udForm.getTc();
				if(StringUtil.isNullorEmpty(from)) from = "import";
				
				udForm.setFrom(from);
								
				ETSProj proj = ETSDatabaseManager.getProjectDetails(conn, strProjectId);

				//get params
				params = AmtCommonUtils.getServletParameters(pdRequest);
				
		        String strURL = "displayAddMembrImport.wss?" +
				"from="+ from +"&fwd=importResult"
		        +"&proj="
		        + strProjectId
		        + "&tc="
		        + strTopCatId
		        + "&cc="
		        + strTopCatId
		        + "&linkid="
		        + strLinkId;
		        
		        String strURL2 = "displayAddMembrImport.wss?" +
				"from="+ from +"&fwd=importMain"
		        +"&proj="
		        + strProjectId
		        + "&tc="
		        + strTopCatId
		        + "&cc="
		        + strTopCatId
		        + "&linkid="
		        + strLinkId;
		        
		        logger.debug("Import Action :: URL == "+strURL);
		        
		        String strError = "&error=";
		        
		        boolean fileEmpty = true;			
				boolean invalidHeader = false;
				
				boolean userIdNull = false;
 				boolean userEmailNull = false;
 				boolean userAccessNull = false;
 				
 				boolean userIdNameNull = false;
 				boolean accessNullFound = false;
								
 				List errorDataList = new ArrayList();
 				
				int nameIndex = -1;
				int idIndex = -1;
				int emailIndex = -1;
				int accessIndex = -1;
				int jobIndex = -1;
				int msgrIndex = -1;
				        
		        // get the Form File...
		        FormFile pdImportList = udForm.getImportList();
		        								
				logger.debug("Import Action :: LINKID == "+udForm.getLinkid());
				logger.debug("Import Action :: PROJ == "+udForm.getProj());
				logger.debug("Import Action :: TC == "+udForm.getTc());
				logger.debug("Import Action :: from == "+udForm.getFrom());
				
				
				// reset the Form fields ... 
				resetImportFormFields(udForm);
				
								  
				if (pdImportList == null
						|| pdImportList.getFileName() == null
						|| pdImportList.getFileSize() == 0) {
		            // Means no file has been selected for upload.
		            udForm.setNoFile(1);
		            udForm.setNoValidData(0);
		            udForm.setInvalidHeader(0);
		    		udForm.setDataError(0);
		        }else{
		      
		            // If we reach here means we have a file with data in it.
		            
		            BufferedReader pdInputBuffer = new BufferedReader(
		                    new InputStreamReader(pdImportList.getInputStream()));
		            
		            String strEachLine = null;
		            int iLineNo = 0;
		                		    		    		
		            
		            Vector usersVector = new Vector();
		            		                       
		            while ((strEachLine = pdInputBuffer.readLine()) != null) {
		            	if (iLineNo == 0) {
		                	logger.debug("HEADER in IMPORT Action == " + strEachLine);
		                    if (!checkHeaders(strEachLine)) {
		                        // No point moving forward as headers are wrong.
		                    	logger.debug("IMPORT Action :: INVALID Header...");
		                    	invalidHeader = true;
		                    	udForm.setInvalidHeader(1);
		                    	udForm.setDataError(0);
		                    	udForm.setNoValidData(0);
		                		udForm.setNoFile(0);
		                		break;
		                    }else{
		                    	 nameIndex = getIndex(strEachLine,USER_NAME_HEADER);
		                    	 logger.debug("name Index in IMPORT Action == " + nameIndex);
		                    	 idIndex = getIndex(strEachLine,USER_ID_HEADER);
		                    	 logger.debug("id Index in IMPORT Action == " + idIndex);
		                    	 emailIndex = getIndex(strEachLine,USER_EMAIL_HEADER);
		                    	 logger.debug("email Index in IMPORT Action == " + emailIndex);
		                    	 accessIndex = getIndex(strEachLine,USER_ACCESS_HEADER);
		                    	 logger.debug("access Index in IMPORT Action == " + accessIndex);
		                    	 jobIndex = getIndex(strEachLine,USER_JOB_HEADER);
		                    	 logger.debug("job Index in IMPORT Action == " + jobIndex);
		                    	 msgrIndex = getIndex(strEachLine,USER_MSGRID_HEADER);
		                    	 logger.debug("msgr Index in IMPORT Action == " + msgrIndex);
		                    	 
		                    }
		                }
		                else {
		                    // If we reach here means headers checked out fine.
		                    // Now we parse the data.
		                  if(!invalidHeader){
		                    
		                  	logger.debug("LINE in IMPORT Action == " + strEachLine);
		                  		                  		                  	
		                    String strTokens[] = strEachLine.split(",");
		                    
		                    int len = strTokens.length;
		                    
		                    if(len == 0){
		                    	strEachLine = strEachLine +",000"+",000"+",000"+",000"+",000"+",000";
		                    	strTokens = strEachLine.split(",");
		                    }else if(len == 1){
		                    	strEachLine = strEachLine +",000"+",000"+",000"+",000"+",000";
		                    	strTokens = strEachLine.split(",");
		                    	fileEmpty = false;
		                    }else if(len == 2){
		                    	strEachLine = strEachLine +",000"+",000"+",000"+",000";
		                    	strTokens = strEachLine.split(",");
		                    	fileEmpty = false;
		                    }else if(len == 3){
		                    	strEachLine = strEachLine +",000"+",000"+",000";
		                    	strTokens = strEachLine.split(",");
		                    	fileEmpty = false;
		                    }else if(len == 4){
		                    	strEachLine = strEachLine +",000"+",000";
		                    	strTokens = strEachLine.split(",");
		                    	fileEmpty = false;
		                    }else if(len == 5){
		                    	strEachLine = strEachLine +",000";
		                    	strTokens = strEachLine.split(",");
		                    	fileEmpty = false;
		                    }
		                    
		                    logger.debug("Chgd LINE in IMPORT Action == " + strEachLine);
		                                       	
		                    	userIdNull = false;
		                    	userEmailNull = false;
		                    	userAccessNull = false;
		                    
		                        // Process this data row.
		                        String strName = null;
		                        String strUserId = null;
		                        String strUserEmail = null;
		                        String strAccessLevel = null;
		                        String strJobResp = null;
		                        String strMsgrId = null;
		                       	                        		                        
		                  		if(nameIndex >  -1) strName = strTokens[nameIndex];
		                  			logger.debug("NAME in IMPORT Action == " + strName);
	           			        if(idIndex >  -1) strUserId = strTokens[idIndex];
	           			        	logger.debug("ID in IMPORT Action == " + strUserId);
	               		        if(emailIndex >  -1) strUserEmail = strTokens[emailIndex];
	               		        	logger.debug("EMAIL in IMPORT Action == " + strUserEmail);
	            		        if(accessIndex >  -1) strAccessLevel = strTokens[accessIndex];
	            		        	logger.debug("ACCESS in IMPORT Action == " + strAccessLevel);
		                       if(jobIndex >  -1) strJobResp = strTokens[jobIndex];
	                            	logger.debug("JOB in IMPORT Action == " + strJobResp);
	                           if(msgrIndex >  -1) strMsgrId = strTokens[msgrIndex];
	                           		logger.debug("MSGR ID in IMPORT Action == " + strMsgrId);
			                  
		                                               
		                        String strLoginUserId = es.gIR_USERN;
		                        AddMembrUserDetails user = new AddMembrUserDetails();
		            	                        
		                        if (isNullOrEmpty(strUserId)) {
		                        	userIdNull = true;
		                        }else{
		                        	user.setEnteredId(strUserId);
		                        }
		                       		                        
		                        if(userIdNull){
			                       	if (isNullOrEmpty(strUserEmail)) {
			                               userEmailNull = true;     
			                        }else{
			                        	user.setEnteredId(strUserEmail);
			                        }
		                        }else{
		                        	if (isNullOrEmpty(strUserEmail)) {
			                               userEmailNull = true;     
			                        }else{
			                        	user.setEnteredId2(strUserEmail);
			                        }
		                        }
		                        	                       	
		                       	if((userIdNull) && (userEmailNull)){
		                       		userIdNameNull = true;
		                       		Integer errNo = new Integer(iLineNo);
		                       		String idEmailNull ="    "+ errNo.toString() + "  (User ID and Email not found)";
		                       		errorDataList.add(idEmailNull);
		                       		iLineNo++;
		                       		continue;
		                       		
		                       	}else if(isNullOrEmpty(strAccessLevel)) {
		                       		accessNullFound = true;
		                        	userAccessNull = true;
		                        	Integer errNo = new Integer(iLineNo);
		                        	String accLevNull ="    "+ errNo.toString() + "  (Access Level not found)";
		                        	errorDataList.add(accLevNull);
		                        	iLineNo++;
		                        	continue;
		                        	
		                        }else{
		                        	user.setAccessLevel(strAccessLevel);
		                        }
		                        
		                       	logger.debug("UserId  == " + user.getEnteredId());
		                       	logger.debug("UserEmail  == " + user.getEnteredId2());
		                       	logger.debug("Access Level  == " + user.getAccessLevel());
		                       	
		                        if(!isNullOrEmpty(strName)){
		                        	user.setUserName(strName);
		                        }
		                        
		                        logger.debug("UserName  == " + user.getUserName());
		                        
		                        if(!isNullOrEmpty(strJobResp)){
		                        	user.setJob(strJobResp);
		                        }
		                        
		                        logger.debug("Job  == " + user.getJob());
		                        
		                        if(!isNullOrEmpty(strMsgrId)){
		                        	user.setMsgrID(strMsgrId);
		                        }
		                        
		                        logger.debug("Msgr Id  == " + user.getMsgrID());
		                        
		                        usersVector.add(user);
		                        
		                     } // not Invalid header ...  
		                        
		                    } // else
		                
		                	iLineNo++;
		                	
		                } // while
		            
		                    if(!invalidHeader){
		                    	
		                    	if(fileEmpty){
		                    			udForm.setFileEmpty(1);
			                    		udForm.setNoValidData(0);
			                    		udForm.setInvalidHeader(0);
			                			udForm.setDataError(0);
			                			udForm.setNoFile(0);
		                			
		                       	}else if((usersVector.size() == 0) && (errorDataList.size() > 0)){
		                       			errorDataList = new ArrayList();
		                       			if(userIdNameNull){
		                       				String err1 ="One or more imported user(s) have empty User ID and User email";
				                        	errorDataList.add(err1);
		                       			}
		                       			if(accessNullFound){
		                       				String err2 ="One or more imported user(s) have empty Access level";
				                        	errorDataList.add(err2);
		                       			}
			                    		udForm.setNoValidData(1);
			                    		udForm.setFileEmpty(0);
			                    		udForm.setInvalidHeader(0);
			                			udForm.setDataError(0);
			                			udForm.setNoFile(0);
			                			udForm.setErrorList(errorDataList);
		                		                    		
		                    	}else if((usersVector.size() > 0)&& (errorDataList.size() > 0)){
		                    		    udForm.setDataError(1);
		                    	 		udForm.setFileEmpty(0);
		                    	 		udForm.setNoFile(0);
		                    	 		udForm.setInvalidHeader(0);
		                    	 		udForm.setNoValidData(0);
		                    	 		udForm.setErrorList(errorDataList);
		                    	 		udForm.setImportUsers(usersVector);
		                    	 				                    	 		
		                    	 }else if(usersVector.size() > 0){
		                    	 	    strForward = "success";
		                    	 		udForm.setInvalidHeader(0);
		                    	 		udForm.setFileEmpty(0);
		                    			udForm.setDataError(0);
		                    			udForm.setNoFile(0);
		                    			udForm.setNoValidData(0);
		                    			udForm.setImportUsers(usersVector);
		                       	 }		
		                  }
		         }
						 if(strForward.equals("success")){
						 		pdResponse.sendRedirect(strURL);
						 }else{
						 		pdResponse.sendRedirect(strURL2);
						 }
							
		     } // if (es.GetProfile(pdResponse, pdRequest))
        		
				logger.debug("Import Action :: FORWARD " + strForward);
								
		} catch (SQLException se) {
		 	PrintWriter out = pdResponse.getWriter();
		 	
			if (conn != null) {
				conn.close();
			}
			if (se != null) {
				se.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,se);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(se), "Error occurred on ImportMemberListAction.");

			}
			
		} catch (Exception ex) {
			PrintWriter out = pdResponse.getWriter();
			
			if (ex != null) {
				ex.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,ex);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(ex), "Error occurred on ImportMemberListAction.");

			}
			
		} finally {

			if (conn != null)
				conn.close();
			conn = null;
		}
        
        return null;
    }
	

	/**
     * @param strHeaderLine
     * @return
     */
	
    private boolean checkHeaders(String strHeaderLine) {
       
    	StringTokenizer strHeaderTokens = new StringTokenizer(strHeaderLine,DELIM);
        boolean idHeaderExists = false;
        boolean emailHeaderExists = false;
        boolean accessHeaderExists = false;
        boolean bIsHeaderOK = false;
        int openBracePos = 0;
                
            while (strHeaderTokens.hasMoreTokens()) {
                
            	String strToken = strHeaderTokens.nextToken();
                openBracePos = strToken.indexOf('(');
                if(openBracePos != -1) strToken = strToken.substring(0,openBracePos).trim();
                         
             if(!idHeaderExists){
                if (strToken.equalsIgnoreCase(USER_ID_HEADER)) {
                	idHeaderExists = true;
                }	
             }else if(!emailHeaderExists){
               	if (strToken.equalsIgnoreCase(USER_EMAIL_HEADER)) {
               		emailHeaderExists = true;
               	}	
             }else if(!accessHeaderExists){
             	if (strToken.equalsIgnoreCase(USER_ACCESS_HEADER)) {
             		accessHeaderExists = true;
             	}
             }
              
            } // while
            
            if((idHeaderExists) || (emailHeaderExists))
            	if(accessHeaderExists) bIsHeaderOK = true;
            
            return bIsHeaderOK;
    }

    
    private void resetImportFormFields(BaseAddMemberForm udForm){
    	
    	udForm.setDataError(0);
 		udForm.setNoFile(0);
 		udForm.setFileEmpty(0);
 		udForm.setInvalidHeader(0);
 		udForm.setNoValidData(0);
 		udForm.setErrorList(null);
 		udForm.setImportUsers(null);
    	
    }
    
    private int getIndex(String strHeaderLine,String HEADER) {
    	
        StringTokenizer strHeaderTokens = new StringTokenizer(strHeaderLine,DELIM);
        int index = -1;
        int iCounter = 0;
        int openBracePos = 0;
        
        
            while (strHeaderTokens.hasMoreTokens()) {
                String strToken = strHeaderTokens.nextToken();
                openBracePos = strToken.indexOf('(');
                if(openBracePos != -1) strToken = strToken.substring(0,openBracePos).trim();
                if (strToken.equalsIgnoreCase(HEADER)) {
                	index = iCounter;
                    break;
                }
                iCounter++;
            }
            
            return index;
    }
    
    /**
     * @param str
     * @return
     */
    private boolean isNullOrEmpty(String str) {
        boolean bIsNullorEmpty = false;
        bIsNullorEmpty = (str == null) || str.trim().equals("") || str.trim().equals("000");
        return bIsNullorEmpty;
    }
}
