package oem.edge.ed.odc.webdropbox.server;

import  oem.edge.ed.odc.util.TimeoutManager;
import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.ftp.client.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.tunnel.common.*;

import java.util.*;
import java.io.*;
import java.util.zip.*;
import java.net.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



/* WebDropBox.java
 *
 * This is the Main Servlet class that serves the request from Client :
 *
 * For each user logging in to the Dropbox service, the servlet will 
 * instantiate a dropbox instance for that particular user. It'll then serve
 * each user requests by invoking appropriate business logic methods() of that
 * particular user's dropbox instance.
 *
 * There is a static hash which will contain the ref to the UserDropbox object
 * using the SessionID as the key. There is an HttpListener which will ensure
 * that this hash is cleaned up when the session is invalidated.
 * 
 */


public class WebDropbox  extends HttpServlet {
/*

  // This will contain SessionID -> UserDropbox mapping. The 
  //  DropboxSessionListener class will ensure that this hash gets cleaned up
  //  upon session expiration
   static protected Hashtable globalHash = new Hashtable();
   
   static public Hashtable getDropboxSessions() {
      return (Hashtable)globalHash.clone();
   }
   
   class NotLoggedInException extends Exception {
      public NotLoggedInException(String s) {
         super(s);
      }
      public NotLoggedInException() {
         super();
      }
   }

   public void doGet(HttpServletRequest req, HttpServletResponse resp)
     throws ServletException, IOException {
      process(req, resp);
   }
		
   public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
      process(req, resp);
   }
   
   private void setErrorAttributes(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   String errCode, 
                                   String errType, 
                                   String errMess) { 
                
      request.setAttribute( "dropboxErrCode",errCode); 
      request.setAttribute( "severity",errType); 
      request.setAttribute( "dropboxErrMessage",errMess); 
   }   
	
   private void setStandardAttributes(HttpServletRequest request, 
                                      HttpServletResponse response) { 
                
      String context = request.getContextPath();
      if (context == null || context.length() == 0) {
         context="";
      }
      
     // Support URL Rewriting
      String url = response.encodeURL(context + 
                                      "/servlet/oem/edge/ed/odc/webdropbox");

      request.setAttribute("dboxhref", url);
      request.setAttribute("context", context);
      
     // Support URL Rewriting
      String feURL = 
         oem.edge.ed.odc.cntl.DesktopServlet.getDesktopProperty("edodc.feURL");
      String feEdesignServlet = 
         oem.edge.ed.odc.cntl.DesktopServlet.getDesktopProperty("edodc.feEDServlet");
      String restarturl = feURL + "/" + feEdesignServlet +
         "?op=7&sc=webox:op:i";

      request.setAttribute("restarturl", restarturl);
      request.setAttribute("webdropbox", getDropbox(request.getSession()));
      
   }   
        
        
   public String returnSizeInUnits(long value) {
	
     // Break down value to x.yy KB, MB or GB.
      long size = value;
      long divisor = 1024;
      String suffix = " KB";
      long whole = size / divisor;
      int fraction = 0;
	
      if (whole > 999) {
         divisor = 1048576;
         suffix = " MB";
         whole = size / divisor;
         if (whole > 999) {
            divisor = 1073741824;
            suffix = " GB";
            whole = size / divisor;
         }
      }
	
      fraction = (int) (((size - (whole * divisor)) * 100) / divisor);
	
      String myNewSize=null;
      if (fraction == 0) {
         myNewSize = new String(whole + suffix);
      }
      else if (fraction < 10) {
         myNewSize = new String(whole + ".0" + fraction + suffix);
      }
      else {
         myNewSize = new String(whole + "." + fraction + suffix);
      }
	
      return myNewSize;
   }
        
   public void process(HttpServletRequest request,
                       HttpServletResponse response) 
      throws ServletException, IOException {

      try {


         System.out.println("service() :");
            
         setStandardAttributes(request, response);
			
        // Gets the current valid session associated with this request if 
        // create is false 
        // or, if necessary, creates a new session for the request if create 
        // is true.
			
        //Fetch me the value associated with the attribute address from the
        // request object.
         String requestType=request.getParameter("address");
			
         if        (requestType.equals("login")) {
         
            doLogin(request, response); 
            
         } else {
         
            HttpSession session = request.getSession(false);
            UserDropbox dropbox = getValidatedDropbox(session);
         
            if (requestType.equals("inbox")) {
         
               doInbox(request, response); 
            
            } else if (requestType.equals("displayContents")) {
         
               doDisplayContents(request, response); 
            
            } else if (requestType.equals("downloadFile")) {
            
               doDownLoadFile(request,response);
								
            } else if (requestType.equals("downloadPkg")) {
            
               doDownLoadPackage(request,response);
            
            } else if (requestType.equals("draft")) {
              //in case my request is for Drafts Section
            
              // doDrafts(request, response);
			
            } else if (requestType.equals("sort_pkgName")) {
         
               Vector asc = doSortInbox(session, requestType,
                                        ETSComparator.SORT_BY_NAME,
                                        ETSComparator.SORT_BY_PKG_STR);
               getSortedInbox(request,response,asc.elements(),true);
         
            } else if (requestType.equals("sort_pkgSize")) {
         
               Vector asc = doSortInbox(session, requestType,
                                        ETSComparator.SORT_BY_SIZE,
                                        ETSComparator.SORT_BY_PKG_SIZE_STR);
               getSortedInbox(request,response,asc.elements(),true);
			
            } else if (requestType.equals("sort_pkgSent")) {
			
               Vector asc = doSortInbox(session, requestType,
                                        ETSComparator.SORT_BY_OWNER,
                                        ETSComparator.SORT_BY_PKG_OWNER_STR);
               getSortedInbox(request,response,asc.elements(),true);
			
            } else if (requestType.equals("sort_pkgComp")) {
         
               Vector asc = doSortInbox(session, requestType,
                                        ETSComparator.SORT_BY_COMPANY,
                                        ETSComparator.SORT_BY_PKG_COMPANY_STR);
               getSortedInbox(request,response,asc.elements(),true);
            
            } else if (requestType.equals("sort_pkgExp")) {
			
               Vector asc = doSortInbox(session, requestType,
                                        ETSComparator.SORT_BY_DATE,
                                        ETSComparator.SORT_BY_PKG_DATE_STR);
               getSortedInbox(request,response,asc.elements(),true);
            
            } else if(requestType.equals("sort_pkgCommit")) {
         
               Vector asc = doSortInbox(session, requestType,
                                        ETSComparator.SORT_BY_DATE_COMMIT,
                                        ETSComparator.SORT_BY_PKG_DATE_COMMIT_STR);
               getSortedInbox(request,response,asc.elements(),true);
			
            } else if (requestType.equals("sort_fileName")) {
         
               String pkgID=(String)( request.getParameter("pkgID"));
               String currentPkgName =(String)( request.getParameter("pkgName"));
            
               Vector asc = doSortFileContent(session, requestType,
                                              ETSComparator.SORT_BY_NAME);
                                           
               getSortedDisplayPkgContents(request, response, asc.elements(),
                                           true, pkgID, currentPkgName);
            
            } else if (requestType.equals("sort_fileSize")) {
               String pkgID=(String)( request.getParameter("pkgID"));
               String currentPkgName =(String)( request.getParameter("pkgName"));
				
               Vector asc = doSortFileContent(session, requestType,
                                              ETSComparator.SORT_BY_SIZE);
                                           
               getSortedDisplayPkgContents(request, response, asc.elements(),
                                           true, pkgID, currentPkgName);
                                
            } else if (requestType.equals("sort_fileSent")) {
               String pkgID=(String)( request.getParameter("pkgID"));
               String currentPkgName =(String)( request.getParameter("pkgName"));
				
               Vector asc = doSortFileContent(session, requestType,
                                              ETSComparator.SORT_BY_OWNER);
                                           
               getSortedDisplayPkgContents(request, response, asc.elements(),
                                           true, pkgID, currentPkgName);
         
				
            } else if(requestType.equals("sort_fileComp")) {
				
               String pkgID=(String)( request.getParameter("pkgID"));
               String currentPkgName =(String)( request.getParameter("pkgName"));
				
               Vector asc = doSortFileContent(session, requestType,
                                              ETSComparator.SORT_BY_COMPANY);
                                           
               getSortedDisplayPkgContents(request, response, asc.elements(),
                                           true, pkgID, currentPkgName);
                                        
            } else if(requestType.equals("sort_fileExp")) {
				
               String pkgID=(String)( request.getParameter("pkgID"));
               String currentPkgName =(String)( request.getParameter("pkgName"));
				
               Vector asc = doSortFileContent(session, requestType,
                                              ETSComparator.SORT_BY_DATE);
                                           
               getSortedDisplayPkgContents(request, response, asc.elements(),
                                           true, pkgID, currentPkgName);
                                
            } else if (requestType.equals("sort_md5")) {
				
               String pkgID=(String)( request.getParameter("pkgID"));
               String currentPkgName =(String)( request.getParameter("pkgName"));
				
               Vector asc = doSortFileContent(session, requestType,
                                              ETSComparator.SORT_BY_MD5);
                                           
               getSortedDisplayPkgContents(request, response, asc.elements(),
                                           true, pkgID, currentPkgName);
                                
            } else if (requestType.equals("refreshDisplayContents")) {
         
            } else if (requestType.equals("logout")) {
            
              // Remove dropbox object 
               WebDropbox.removeDropbox(session);
            
               try {
                  dropbox.disconnect();
               } catch(Exception ee) {}
            
               getServletContext().getRequestDispatcher("/jsp/Logout.jsp").forward(request, response);
            
            }
         }
      } catch (ServletException se) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Exception occurred in service() " + se);
         DebugPrint.printlnd(DebugPrint.ERROR, se);
         throw se;
      } catch (IOException ioe) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Exception occurred in service() " + ioe);
         DebugPrint.printlnd(DebugPrint.ERROR, ioe);
         throw ioe;
      } catch (NullPointerException npe) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Exception occurred in service() " + npe);
         DebugPrint.printlnd(DebugPrint.ERROR, npe);
         throw npe;
      } catch (NotLoggedInException nlie) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Not logged in during service" + nlie);
         DebugPrint.printlnd(DebugPrint.ERROR, nlie);
         
         getServletContext().getRequestDispatcher("/jsp/LoginError.jsp").forward(request, response);       
      } catch (Exception rege) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Exception occurred in service() " + rege);
         DebugPrint.printlnd(DebugPrint.ERROR, rege);
         try {
            getValidatedDropbox(request.getSession()).disconnect();
         } catch(Exception eee) {
         }
         
         setErrorAttributes(request, response, "1", "ERROR", 
                       "Exception occured while processing your request<p>" +
                       "Please relaunch Web dropbox application and try again." +
                       "If problem persists, please contact the ICC Help desk");
         getServletContext().getRequestDispatcher("/jsp/Error.jsp").forward(request, response);       
      }
   }
      

   private void getSortedDisplayPkgContents(
      HttpServletRequest request,
      HttpServletResponse response,
      Enumeration enumeration,
      boolean b,
      String packID,
      String packName) throws Exception {
	
      HttpSession session=request.getSession();
      try {
         
         UserDropbox dropbox = getValidatedDropbox(session);
      
         request.setAttribute("user",(String)session.getAttribute("user"));
         request.setAttribute("files",dropbox.getDownloadFiles());
         request.setAttribute("sizes",dropbox.getDownloadFileSizes());
				
         request.setAttribute("owner",dropbox.getFileOwnerVector());
         request.setAttribute("company",dropbox.getFileCompanyVector());
         request.setAttribute("expiration",dropbox.getFileExpirationVector());
         request.setAttribute("commitdate",dropbox.getCommitDateVec()); 
				
         request.setAttribute("displayHash",dropbox.getDisplayHash());
				
         request.setAttribute("sortedDisplayenum",(Enumeration)enumeration);
         request.setAttribute( "sortDisplayFlag",Boolean.TRUE);
				
         request.setAttribute( "sortOnFlag",(String)dropbox.getSortOnFlag()); 
				
         Boolean sortOrderVal = new Boolean(dropbox.getadFlag());
         request.setAttribute( "sortOrderFlag",sortOrderVal); 
				
				
				//System.out.println("Value of attribute sortDisplayFlag :"+request.getAttribute("sortDisplayFlag"));
				

         request.setAttribute("pkg",(String)packName);
         request.setAttribute("pkgID",(String)packID);
				
         getServletContext().getRequestDispatcher("/jsp/DisplayPkgContents.jsp").forward(request, response);
	
      } catch(Exception ex) {
         DebugPrint.printlnd(DebugPrint.ERROR, "Error getting sorted contents");
         DebugPrint.printlnd(DebugPrint.ERROR, ex);
         throw ex;
      }
   }

   protected void doInbox(HttpServletRequest request, 
                          HttpServletResponse response) throws Exception {
                          
      HttpSession session = request.getSession(false);
      UserDropbox dropbox = getValidatedDropbox(session);
      
      dropbox.retreiveInbox();
               
      Vector asc = null;
               
      String refreshPageOnColumn = request.getParameter("refresh");
      String refreshPageOnOrder = request.getParameter("order");
      
     // Use sort by newest if doing refresh OR no sorting yet specified
      if ((refreshPageOnColumn != null && refreshPageOnOrder != null && 
           refreshPageOnColumn.equals("pkg_Commit") &&
           refreshPageOnOrder.equals("desc")) ||
          dropbox.getLastInboxSortOperation() == null ||
          dropbox.getLastInboxSortOrder() <= (byte)0) {
          
         dropbox.setLastInboxSortOperation(
            ETSComparator.SORT_BY_PKG_DATE_COMMIT_STR);
         dropbox.setLastInboxSortOrder(ETSComparator.SORT_DESC);
      }	
					
      byte sortOper = 
         ETSComparator.getSortOrder(dropbox.getLastInboxSortOperation());
      byte sortOrder=(byte)(dropbox.getLastInboxSortOrder());
      
      if (sortOper != (byte)0  && sortOrder != (byte)0 ) {
         asc= (Vector)(dropbox.callSortInbox((byte)sortOper,(byte)sortOrder));
         
         Hashtable inboxHash=dropbox.getInboxContents();
         dropbox.setInboxSize(inboxHash.size());
         
         request.setAttribute("sortFlag",Boolean.TRUE);
         
         dropbox.setSortOnFlag(ETSComparator.getSortOperationByString(sortOper));
         
         request.setAttribute("sortOnFlag",(String)dropbox.getSortOnFlag()); 
         
         if ( sortOrder == ETSComparator.SORT_ASC) {
            dropbox.setadFlag(true);
         } else if ( sortOrder == ETSComparator.SORT_DESC ) {
            dropbox.setadFlag(false);
         } else {
            dropbox.setadFlag(false);
            sortOrder = ETSComparator.SORT_DESC; 
         }
         
         getSortedInbox(request,response,asc.elements(),true);
         
         dropbox.setLastSortRequest(dropbox.getLastInboxSortOperation());
         
        // we need to remember this op
         dropbox.setLastInboxSortOperation(dropbox.getLastInboxSortOperation());
         dropbox.setLastInboxSortOrder(sortOrder);
         
      } else {
         DebugPrint.printlnd(DebugPrint.WARN, 
                             "doInbox: NO sortinfo ... so no contents!!");
      }
   }
   
  // Toggles the sort order from the last time
   private Vector doSortInbox(HttpSession session,
                              String sortname, 
                              byte   sortval,
                              String etsSortName) throws Exception {
      
      UserDropbox dropbox = getValidatedDropbox(session);
      
      String lastSortRequest = dropbox.getLastSortRequest();
      if (lastSortRequest == null || !lastSortRequest.equals(sortname)) {
         dropbox.setadFlag(true);
      } 
      
      dropbox.setSortOnFlag(sortname);
      
     // Toggle the sort order
      dropbox.setadFlag(!dropbox.getadFlag());
                        
      byte sortorder = 
         dropbox.getadFlag() ? ETSComparator.SORT_ASC 
         : ETSComparator.SORT_DESC;
      
     //need to remember last order      
      dropbox.setLastInboxSortOrder(sortorder);
      
      Vector asc = (Vector) dropbox.callSortInbox(sortval, sortorder);
      
      Hashtable inboxHash=dropbox.getInboxContents();
      dropbox.setInboxSize(inboxHash.size());
      
      dropbox.setLastSortRequest(sortname);
      dropbox.setLastInboxSortOperation(etsSortName);
      
      return asc;
   }
      
      
   private Vector doSortFileContent(HttpSession session,
                                    String sortname,
                                    byte sortval) throws Exception {
                                    
      UserDropbox dropbox = getValidatedDropbox(session);
      
      String lastSortRequest = dropbox.getLastSortRequest();
      if (lastSortRequest == null || !lastSortRequest.equals(sortname)) {
         dropbox.setadFlag(true);
      } 
      
      dropbox.setSortOnFlag(sortname);
      
      byte sortorder = 
         dropbox.getadFlag() ? ETSComparator.SORT_ASC 
         : ETSComparator.SORT_DESC;
      
      Vector asc = dropbox.callSortDisplayPkgContents(sortval, sortorder);
      
      Hashtable displayHash=dropbox.getDisplayHash();
      dropbox.setDisplayHashSize(displayHash.size());
      
      dropbox.setadFlag(!dropbox.getadFlag());
      dropbox.setLastSortRequest(sortname);
      
      return asc;
   }


   private void getDrafts(HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
      
      HttpSession session=request.getSession();
      
      UserDropbox dropbox = getValidatedDropbox(session);
      
      if ( dropbox.isLoggedIn() == false ) {
         System.out.println("\nERROR !!! : Drafts.. Huh ! ur not logged in...");
         return;
      }
      
     //System.out.println("\nCalling Drafts.jsp now...");
      
      Hashtable draftsHash=dropbox.getDraftsContents();
      dropbox.setDraftsSize(draftsHash.size());
      
      try{
			
         dropbox.retreiveDrafts();
        //System.out.println("draftsHash :"+draftsHash.size());
        //System.out.println(" Size of draftsHash :"+dropbox.getDraftsContents().size());
         request.setAttribute("drafts",dropbox.getDraftsContents());
         request.setAttribute("userid",(String)session.getAttribute("user"));
         request.setAttribute("password",(String)session.getAttribute("pwd"));
			
         getServletContext().getRequestDispatcher("/jsp/Drafts.jsp").forward(request, response);
        //inboxVec.clear();
         dropbox.getDraftsContents().clear();
      }catch(Exception e)
      {
         System.out.println("Error !!! : Exception in Method getInbox() :"+e);
         throw e;
      }
   }



   public void displayContents(HttpServletRequest request, 
                               HttpServletResponse response,
                               UserDropbox dropbox) throws Exception {
		
         HttpSession session=request.getSession();
        //request.setAttribute("pkgID",String.valueOf(packageID));
			
         String pkgID=request.getParameter("pkgID");
			
         if(pkgID==null)
         {
            pkgID=(String)session.getAttribute("pkgID");
         }
        //System.out.println("Inside displayContents() pkgID "+pkgID);
			
			
			
         Enumeration fileStuff=null;
			
         try 
         {
            fileStuff = dropbox.listPackageContents(Long.parseLong(pkgID));
         } 
         catch (Exception e) 
         {
            System.out.println("Error !!! : Getting Package Contents Info :"+e);
            throw e;
         }
			
         dropbox.updateDisplayPkgContainers(fileStuff,pkgID);
			
			
         String packName=request.getParameter("pkg");
			
         if(packName==null)
         {
            packName=(String)session.getAttribute("pkg");
         }
         if(packName==null)
         {
            packName=dropbox.getPkgName();
         }
			
         showPkgContents(request, response,dropbox,packName,pkgID);
      }


   public void showPkgContents(HttpServletRequest request,
                               HttpServletResponse response,
                               UserDropbox dropbox, String packName,
                               String pkgID) throws Exception {
                               
         HttpSession session=request.getSession();
         try
         {
            request.setAttribute("user",(String)session.getAttribute("user"));
            request.setAttribute("files",dropbox.getDownloadFiles());
            request.setAttribute("sizes",dropbox.getDownloadFileSizes());
				
            request.setAttribute("owner",dropbox.getFileOwnerVector());
            request.setAttribute("company",dropbox.getFileCompanyVector());
            request.setAttribute("expiration",dropbox.getFileExpirationVector());
            request.setAttribute("commitdate",dropbox.getCommitDateVec());
            request.setAttribute("md5vect",dropbox.getFileMD5Vector());
				
				

            request.setAttribute("pkg",packName);
            request.setAttribute("pkgID",pkgID);
				
            request.setAttribute("displayHash",dropbox.getDisplayHash());
				
					
				
            request.setAttribute("sortedDisplayenum",null);
            request.setAttribute( "sortDisplayFlag",Boolean.FALSE);
				
            getServletContext().getRequestDispatcher("/jsp/DisplayPkgContents.jsp").forward(request, response);
	
         } catch(Exception ex) {
            System.out.println("Exception download :"+ex);
            throw ex;
         }
      } 

	
   public UserDropbox getDropbox(HttpSession session) {
     //UserDropbox dropbox = (UserDropbox)session.getAttribute("webdropbox");
      UserDropbox dropbox = (UserDropbox)globalHash.get(session.getId());
      
      TimeoutManager mgr = TimeoutManager.getGlobalManager();
      mgr.removeTimeout("webdbox" + session.getId());
      if (dropbox != null) {
         mgr.addTimeout(new WebdropboxTimeout(dropbox.getTimeout(), 
                                              session.getId(), null));
      }
      
      return dropbox;
   }
   
   public UserDropbox getValidatedDropbox(HttpSession session) 
      throws Exception {
      
      if (session == null) {
         throw new NotLoggedInException("No Session Found for user");
      }
      
      UserDropbox dropbox = getDropbox(session);
      if (dropbox == null) {
         throw new NotLoggedInException("Dropbox not found in session");
      }
      if (!dropbox.isConnected()) {
         throw new NotLoggedInException("Dropbox not connected");
      }
      if (!dropbox.isLoggedIn()) {
         throw new NotLoggedInException("Dropbox not logged in");
      }
      return dropbox;
   }
	
  // Add a timeout to handle inactivity cleanup
   static public void manageDropbox(HttpSession session, UserDropbox dropbox) {
      globalHash.put(session.getId(), dropbox);
      TimeoutManager mgr = TimeoutManager.getGlobalManager();
      mgr.removeTimeout("webdbox" + session.getId());
      String webdropboxTimeout = 
         oem.edge.ed.odc.cntl.DesktopServlet.getDesktopProperty(
            "edodc.webdropboxTimeout", "300"); 
            
      long timeout = 300;
      try { timeout = Long.parseLong(webdropboxTimeout); } catch(Exception ee) {}
      timeout *= 1000;
      mgr.addTimeout(new WebdropboxTimeout(timeout, session.getId(), null));
      dropbox.setTimeout(timeout);
   }
   
   static public UserDropbox removeDropboxForId(String id) {
   
      UserDropbox dropbox = (UserDropbox)globalHash.get(id);
      globalHash.remove(id);
      
      TimeoutManager mgr = TimeoutManager.getGlobalManager();
      mgr.removeTimeout("webdbox" + id);
      
      return dropbox;
   }
   
   static public UserDropbox removeDropbox(HttpSession session) {
      return removeDropboxForId(session.getId());
   }
   

   public void getInbox(HttpServletRequest request,
                        HttpServletResponse response) throws Exception {
                        
      HttpSession session=request.getSession();
      try {
         UserDropbox dropbox = getValidatedDropbox(session);
         dropbox.retreiveInbox();
        //System.out.println("inboxHash :"+inboxHash.size());
        //System.out.println("size of inboxHash :"+dropbox.getInboxContents().size());
         request.setAttribute("inbox", dropbox.getInboxContents());
         request.setAttribute("userid",(String)session.getAttribute("user"));
         request.setAttribute("password",(String)session.getAttribute("pwd"));
         
         request.setAttribute("loggedUser", dropbox.provideUser());
         request.setAttribute("loggedCompany",dropbox.provideCompany());
         
         request.setAttribute("sortedenum", null);
         request.setAttribute("sortFlag", Boolean.FALSE);
         
         getServletContext().getRequestDispatcher("/jsp/Inbox.jsp").forward(request, response);
         
         dropbox.getInboxContents().clear();
      } catch(Exception e) {
         System.out.println("Exception in getInbox() :"+e);
         throw e;
      }
   }
		
   public void getSortedInbox(HttpServletRequest request,
                              HttpServletResponse response,
                              Enumeration sortedEnum,
                              boolean sort) throws Exception {
                              
      HttpSession session=request.getSession();
      try {
         
         UserDropbox dropbox = getValidatedDropbox(session);
         dropbox.retreiveInbox();
        //System.out.println("Inside getSortedInbox inboxHash :"+inboxHash.size());
        //System.out.println("Inside getSortedInbox  size of inboxHash :"+dropbox.getInboxContents().size());
         request.setAttribute("inbox",dropbox.getInboxContents());
         request.setAttribute("userid",(String)session.getAttribute("user"));
         request.setAttribute("password",(String)session.getAttribute("pwd"));
         request.setAttribute("sortedenum",(Enumeration)sortedEnum);
         
         request.setAttribute("loggedUser",dropbox.provideUser());
         request.setAttribute("loggedCompany", dropbox.provideCompany());
         
         
         request.setAttribute( "sortFlag",Boolean.TRUE);
         request.setAttribute( "sortOnFlag", dropbox.getSortOnFlag()); 
         
         Boolean sortOrderVal = new Boolean(dropbox.getadFlag());
         request.setAttribute( "sortOrderFlag",sortOrderVal); 
         
         
        //System.out.println("Value of attribute sortFlag :"+request.getAttribute("sortFlag"));
         
         getServletContext().getRequestDispatcher("/jsp/Inbox.jsp").forward(request, response);
         
         
      } catch(Exception e) {
         System.out.println("Exception in getInbox() :"+e);
         throw e;
      }
   }
   
   
   public void doDownLoadFile(HttpServletRequest request, 
                              HttpServletResponse response) throws Exception {
		
      HttpSession session = request.getSession(false);
      UserDropbox dropbox = getValidatedDropbox(session);
                
      String pkgID=request.getParameter("pkgID");
      String filename=request.getParameter("file");
         
      String fileIndex = request.getParameter("fileIndex");
      
      Operation operation = null;
                
      try {
	                    	 	 
         Enumeration enumc = 
            dropbox.listPackageContents(Long.parseLong(pkgID));
		              
         boolean foundfile = false;
         while(enumc.hasMoreElements()) {
		              	
            FileInfo finfo = (FileInfo)enumc.nextElement();
				                 
            if (finfo.getFileName().equals(filename)) {
				                    
               foundfile = true;
				                    	
               operation = dropbox.downloadFile(Long.parseLong(pkgID), 
                                                finfo.getFileId());
                                          
               long totToXfer = operation.getToXfer();
               int sz;
               byte buf[] = new byte[32768];
                  
               String fname = finfo.getFileName();
               
              // Get rid of / and \ from name
               fname = fname.replace('/','-');
               fname = fname.replace('\\','-');
               response.setContentType("application/download");
               response.setHeader("Content-Disposition","attachment;filename=\"" + 
                                  finfo.getFileName() + "\"");
										 
              // Set content length if its 2GIG or less (setContentLen is Int!!)
               if (totToXfer >= 0 && totToXfer <= 0x7fffffff) {
                  response.setContentLength((int)totToXfer);
               } else {
                 // Set size directly ... hope this works
                 //response.setHeader("Content-Length", "" + totToXfer);
                 
                 // No size is better
               }
               
               OutputStream out = response.getOutputStream();
               while((sz=dropbox.readFileData(operation, buf, 
                                              0, buf.length)) > 0) {
                  out.write(buf, 0, sz);
               }
				
               out.flush();
               out.close();
               out=null;  
										
               break;
            }
         }
         
         if (!foundfile) {
            
         }
      } catch (Exception e) {
         DebugPrint.printlnd(DebugPrint.ERROR, "Error doing file download");
         DebugPrint.printlnd(DebugPrint.ERROR, e);
                  
         throw e;
      } finally {
        // Make sure operation is closed
         try {
            if (operation != null) {
               dropbox.closeOperation(operation);
            }
         } catch(Exception ee) {}      
      }
   }
      
      
   public void doDownLoadPackage(HttpServletRequest request, 
                                 HttpServletResponse response) 
      throws Exception {
                                 
      HttpSession session = request.getSession(false);
      UserDropbox dropbox = getValidatedDropbox(session);
                                 
     //in case my request is for Entire Package Download		
				
     //System.out.println("Comes in for Entire Package Download");
            
      if ( dropbox.isLoggedIn() == true ) {
         long pkgID=Long.parseLong( request.getParameter("pkgID"));
         String currentPkgName =(String)( request.getParameter("pkgName"));
               
         String encoding = request.getParameter("fileEncode"); 
               
        // System.out.println("pkgID received from webpage = "+pkgID);	
        // System.out.println("fileEncode received from webpage = "+encoding);						
        // System.out.println("currentPkgName received from webpage = "+currentPkgName);

         Operation operation = null;
         try {
        
            operation = dropbox.downloadPackage((long)pkgID,(String)encoding);
            
            
            String PackageNameExtn = currentPkgName+"."+encoding;
            System.out.println("PackageNameExtn  in userdropbox - download method = "+PackageNameExtn);
            
            long totToXfer = operation.getToXfer();
            
            System.out.println("totToXfer = "+totToXfer); 
            
            int sz;
            byte buf[] = new byte[32768]; 
            
            response.setContentType("application/download");
            response.setHeader("Content-Disposition","attachment;filename=\"" + 
                               PackageNameExtn + "\"");
            
            long bytesDownloaded=0;  					
            OutputStream out = response.getOutputStream();
            while((sz=dropbox.readFileData(operation, buf, 0, buf.length)) > 0) {
               out.write(buf, 0, sz);
               
               bytesDownloaded=bytesDownloaded+sz;
               
            }
         } finally {
           // Make sure operation is closed
            try {
               if (operation != null) {
                  boolean successOperation=dropbox.closeOperation(operation);
               }
            } catch(Exception ee) {}
         }
      } else {
         System.out.println("\nERROR !!! : Downloads.. Not not logged in...");
      }
   }
      
      
   protected void doDisplayContents (HttpServletRequest request, 
                                     HttpServletResponse response) 
      throws Exception {
                                     
      HttpSession session = request.getSession(false);
      UserDropbox dropbox = getValidatedDropbox(session);
      
     // in case my request is for displaying the contents of my 
     // Package listed in Inbox
      try {
					
         String pkgName=null;
         String pkgSize=null;
					
         try {
						
            pkgName=request.getParameter("pkg");
           //System.out.println("displayContents pkgName :"+pkgName);
            pkgSize=request.getParameter("pkgSize");
            session.setAttribute("pkgSize",pkgSize);
						
            session.setAttribute("pkg",pkgName);
            dropbox.setPkgName(pkgName);
						
         } catch(Exception e) {
         }
					
         String pkgID=request.getParameter("pkgID");					
        //System.out.println("displayContents pkgID :"+pkgID);
										
         session.setAttribute("pkgID",pkgID);
         dropbox.setPkgID(Long.parseLong(pkgID));

					
        // System.out.println("\nPKGID IS NOW..."+pkgID);
        // System.out.println("\n and the value of pkgID in session is "+(String)session.getAttribute("pkgID"));

         dropbox.setLastSortRequest(null);

         displayContents(request,response,dropbox);//,Long.parseLong(pkgID ));

      } catch(Exception e) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "WebDropbox: Error doing display contents");
         DebugPrint.printlnd(DebugPrint.ERROR, e);
         throw e;
      }
   }
            
   protected void doLogin(HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
      String user=null;
      String password=null;
      String token = null;
      
      UserDropbox dropbox = null;

      HttpSession session = request.getSession(true);
      
      try {
         
        // Flash ... don't do this. We use TimeoutManager to manage Dropbox 
        //  timeouts now, separate from Session.
        //
        // Time out the session in 5 minutes 
        // session.setMaxInactiveInterval(60*5);
            
        // Get the Token value for login
         token=(String)request.getParameter("compname");
         
         if(token == null) {
         
            setErrorAttributes(request, response, "2", "ERROR", 
                    "Dropbox login failed Login, no token supplied on startup");
                               
           // I did not get Token, display the Errorpage for Login
            getServletContext().getRequestDispatcher("/jsp/Error.jsp").forward(request, response);
         }
      } catch(Exception e) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "WebDropbox: Error during login");
         DebugPrint.printlnd(DebugPrint.ERROR, e);
         throw e;
      }
      
      try {
        // Get any OLD dropbox ... 
         UserDropbox olddropbox = getDropbox(session);
         
        // ... and disconnect it
         if (olddropbox != null) olddropbox.disconnect();
      } catch(Exception ee) {
      }
      
      dropbox = new UserDropbox();
      
      request.setAttribute("webdropbox", dropbox);
      
     // Store someting in session to allow affinity to work (need that)
      session.setAttribute("webdropbox", session.getId());
			
     //provide me a DSMPSocketHandler
      String ports =
         oem.edge.ed.odc.cntl.DesktopServlet.getDesktopProperty("edodc.xfrPort");
      String host = 
         oem.edge.ed.odc.cntl.DesktopServlet.getDesktopProperty("edodc.xfrHost");
 
      int port = -1;

      try {
         port = Integer.parseInt(ports);
      } catch (Exception ee) {}
     
      dropbox.connect(host, port);
				
      boolean loginSuccess = dropbox.login(token);
		
      if (loginSuccess == false) {
         try {
            dropbox.disconnect();
         } catch(Exception ee) {}
         
        //I did not get access, so I display the Error page for Login
         setErrorAttributes(request, response, "3", "ERROR", 
                    "Dropbox login failed");
         getServletContext().getRequestDispatcher("/jsp/Error.jsp").forward(request, response);
					
      } else {

         manageDropbox(session, dropbox);
      
        // Not doing this URI stuff right now ... 
        //String tempURI=(String)session.getAttribute("uri");
         String tempURI=null;
					
        // Support start viewing a package
         PackageInfo pinfo = null;
         String pkgid=(String)( request.getParameter("pkgid"));
         if (pkgid != null) {
            try {
               pinfo = dropbox.queryPackage(Long.parseLong(pkgid));
            } catch(Exception eee) {}
         }
         
        // Support start viewing a package
         if (pinfo != null) {
            
            dropbox.setLastSortRequest("sort_pkgCommit");
            dropbox.setLastInboxSortOperation(ETSComparator.SORT_BY_PKG_DATE_COMMIT_STR);
            dropbox.setLastInboxSortOrder(ETSComparator.SORT_DESC);
            
            dropbox.retreiveInbox();
            
            session.setAttribute("pkgID",   pkgid);
            session.setAttribute("pkg",     pinfo.getPackageName());
            session.setAttribute("pkgSize",
                                 returnSizeInUnits(pinfo.getPackageSize()));
            
            displayContents(request, response, dropbox);
            
         } else if (tempURI != null) {
	
            System.out.println("URI :"+tempURI);
            String temp1=tempURI.substring(tempURI.indexOf('/')+1,
                                           tempURI.length());
            String URI=temp1.substring(temp1.indexOf('/')+1,temp1.length());
            String page=tempURI.substring(tempURI.lastIndexOf('/')+1,
                                          tempURI.length());
						
            System.out.println("page equals :"+page);
            if(page.equals("DisplayPkgContents.jsp")) {
               System.out.println("\nCalling DisplayPkgContents.jsp now...");
              //displayContents(request,response,dropbox);
            } else if(page.equals("DownloadFile.jsp")) {
               System.out.println("\nCalling DownloadFile.jsp now...");
              //displayContents(request,response,dropbox);
            } else if(page.equals("Drafts.jsp")) {
               System.out.println("\nCalling Drafts.jsp now...");
               request.setAttribute("drafts",dropbox.getDraftsContents());
              //userLogin(request,response,dropbox);
              //createPkg(request,response);
            } else if(page.equals("Inbox.jsp")) {
               System.out.println("\nCalling Inbox.jsp now...");
               Hashtable inboxHash=dropbox.getInboxContents();
               dropbox.setInboxSize(inboxHash.size());
               getInbox(request,response);
            } else if (page.equals("displayContents")) {
               System.out.println("\nCalling displayContents method now...");
              //displayContents(request,response,dropbox);
            }
					
         } else {
         
           // For viewing my inbox ... set default sort order and pass the buck
            dropbox.setLastSortRequest("sort_pkgCommit");
            dropbox.setLastInboxSortOperation(ETSComparator.SORT_BY_PKG_DATE_COMMIT_STR);
            dropbox.setLastInboxSortOrder(ETSComparator.SORT_DESC);
           
            doInbox(request, response);
         }
      }
   }
*/
}
