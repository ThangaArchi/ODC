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
 * Created on Nov 21, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;
 
import java.util.ArrayList;
import java.util.Date;
 
import oem.edge.ets.fe.workflow.core.AbstractDAO;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.util.SelectControl;
 
/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WorkflowMatrixDAO extends AbstractDAO{
 
 /* (non-Javadoc)
  * @see oem.edge.ets.fe.workflow.core.AbstractDAO#saveWorkflowObject(oem.edge.ets.fe.workflow.core.WorkflowObject)
  */
 public boolean saveWorkflowObject(WorkflowObject workflowObject) {
  return false;
 }
 
 /* (non-Javadoc)
  * @see oem.edge.ets.fe.workflow.core.AbstractDAO#getWorkflowObject(java.lang.String)
  */
 public WorkflowObject getWorkflowObject(String ID) {
  return null;
 }
 
 /* (non-Javadoc)
  * @see oem.edge.ets.fe.workflow.core.AbstractDAO#saveWorkflowObjectList(java.util.ArrayList)
  */
 public boolean saveWorkflowObjectList(ArrayList object) {
  return false;
 }
 
 /* (non-Javadoc)
  * @see oem.edge.ets.fe.workflow.core.AbstractDAO#getWorkflowObjectList(java.lang.String)
  */
 public ArrayList getWorkflowObjectList(String ID) {
  return null;
 }
 
 /**
  * 
  * @return
  */
 public ArrayList getAllWorkflowList(){
  ArrayList workspacelist = new ArrayList();
  DBAccess db = null;
  SelectControl selCon = null;
 
  try {
   db = new DBAccess();
   db.prepareDirectQuery("SELECT PROJECT_ID,PROJECT_NAME FROM ETS.ETS_PROJECTS WHERE PROJECT_STATUS  != 'A' " +
    "and project_status != 'D' and project_or_proposal!='M' and project_type='AIC' order by project_name,project_id" );
   
   int workspacelistrow = db.execute();
   workspacelist.add(new SelectControl("All Values","All Values"));
   for (int i = 0; i < workspacelistrow; i++) {
 
    selCon = new SelectControl();
    String PROJECT_ID = db.getString(i, "PROJECT_ID");
    String PROJECT_NAME = db.getString(i, "PROJECT_NAME");
 
    selCon.setLable(PROJECT_NAME);
    selCon.setValue(PROJECT_ID);
 
    workspacelist.add(selCon);
   }
   db.close();
   db = null;
  } catch (Exception e) {
   e.getMessage();
  } finally {
   if (db != null) {
    try {
     db.close();
     db = null;
    } catch (Exception e) {
    }
   }
  }
  return workspacelist;
 }
 
 /**
  * 
  * @return
  */
 public ArrayList getAllBrand(){
  ArrayList brandlist = new ArrayList();
  DBAccess db = null;
  SelectControl selCon = null;
  try {
   db = new DBAccess();
   db.prepareDirectQuery("select distinct brand from ETS.ETS_projects where project_type='AIC' and process='Workflow'" );
   
   int brandlistrow = db.execute();
   brandlist.add(new SelectControl("All Values","All Values"));
   for (int i = 0; i < brandlistrow; i++) {
 
    selCon = new SelectControl();
    String brand = db.getString(i, "brand");
 
    selCon.setLable(brand);
    selCon.setValue(brand);
 
    brandlist.add(selCon);
   }
   db.close();
   db = null;
  } catch (Exception e) {
   e.getMessage();
  } finally {
   if (db != null) {
    try {
     db.close();
     db = null;
    } catch (Exception e) {
    }
   }
  }
  return brandlist;
 }
 /**
  * 
  * @return
  */
 public ArrayList getAllProcess(){
  ArrayList processlist = new ArrayList();
  DBAccess db = null;
  SelectControl selCon = null;
  try {
   db = new DBAccess();
   db.prepareDirectQuery("select distinct process from ETS.ETS_projects where project_type='AIC'" );
   
   int processrow = db.execute();
   processlist.add(new SelectControl("All Values","All Values"));
   for (int i = 0; i < processrow; i++) {
 
    selCon = new SelectControl();
    String process = db.getString(i, "process");
 
    selCon.setLable(process);
    selCon.setValue(process);
 
    processlist.add(selCon);
   }
   db.close();
   db = null;
  } catch (Exception e) {
   e.getMessage();
  } finally {
   if (db != null) {
    try {
     db.close();
     db = null;
    } catch (Exception e) {
    }
   }
  }
  return processlist;
 }
 /**
  * 
  * @return
  */
 public ArrayList getAllBusinessSectorList(){
  ArrayList bSectorlist = new ArrayList();
  DBAccess db = null;
  SelectControl selCon = null;
  try {
   db = new DBAccess();
   db.prepareDirectQuery("select distinct sector from ETS.ETS_projects where project_type='AIC'" );
   
   int bSectorlistrow = db.execute();
   bSectorlist.add(new SelectControl("All Values","All Values"));
   for (int i = 0; i < bSectorlistrow; i++) {
 
    selCon = new SelectControl();
    String sector = db.getString(i, "sector");
 
    selCon.setLable(sector);
    selCon.setValue(sector);
 
    bSectorlist.add(selCon);
   }
   db.close();
   db = null;
  } catch (Exception e) {
   e.getMessage();
  } finally {
   if (db != null) {
    try {
     db.close();
     db = null;
    } catch (Exception e) {
    }
   }
  }
  return bSectorlist;
 }
 /**
  * 
  * @return
  */
 public ArrayList getAllSCESectorList(){
  ArrayList sceSectorlist = new ArrayList();
  DBAccess db = null;
  SelectControl selCon = null;
  try {
   db = new DBAccess();
   db.prepareDirectQuery("select distinct sce_sector from ETS.ETS_projects where project_type='AIC'" );
   
   int sceSectorlistrow = db.execute();
   sceSectorlist.add(new SelectControl("All Values","All Values"));
   for (int i = 0; i < sceSectorlistrow; i++) {
 
    selCon = new SelectControl();
    String sce_sector = db.getString(i, "sce_sector");
 
    selCon.setLable(sce_sector);
    selCon.setValue(sce_sector);
 
    sceSectorlist.add(selCon);
   }
   db.close();
   db = null;
  } catch (Exception e) {
   e.getMessage();
  } finally {
   if (db != null) {
    try {
     db.close();
     db = null;
    } catch (Exception e) {
    }
   }
  }
  return sceSectorlist;
 }
 
 public ArrayList GenerateFinalReport(String query,ArrayList selectedfields){
  DBAccess db = null;
  ArrayList reportlist = new ArrayList();
  try{
   db = new DBAccess();
   db.prepareDirectQuery(query);
   int row = db.execute();
   for (int i = 0; i < row; i++) {
    
    WorkflowMatrixReportObject repobj = new WorkflowMatrixReportObject();
    //common report starts here
    repobj.setWorkspace(db.getString(i, "PROJECT_NAME"));
    repobj.setProcess(db.getString(i, "PROCESS"));
    repobj.setScesector(db.getString(i, "SCE_SECTOR"));
    repobj.setBrand(db.getString(i, "BRAND"));
    repobj.setBusinesssector(db.getString(i, "SECTOR"));
    repobj.setProjectID(db.getString(i, "PROJECT_ID"));
    repobj.setProjectTC("6415");
    repobj.setWorkflowID(db.getString(i, "WF_ID"));
    if(selectedfields!=null || selectedfields.size() > 0){
     if(selectedfields.contains("COMPANY")){
      repobj.setClientname(checkNull(db.getString(i, "COMPANY")));
     }if(selectedfields.contains("WF_NAME")){
      repobj.setAccountcontact(checkNull(db.getString(i, "WF_NAME")));
     }if(selectedfields.contains("WF_CURR_STAGE_NAME")){
        repobj.setCurrStageName(checkNull(db.getString(i, "WF_CURR_STAGE_NAME")));
     }if(selectedfields.contains("SCHEDULE_DATE")){
      java.util.Date dt = (Date) db.getObject(i, "SCHEDULE_DATE");
      repobj.setMeetingdate(dt.toString());
     }if(selectedfields.contains("OVERAL_SCORE")){
      repobj.setOverallscorerating(checkNull(db.getString(i, "OVERAL_SCORE")));
     }if(selectedfields.contains("NSI_RATING")){  //upto forst report
      repobj.setNsirating(checkNull(db.getString(i, "NSI_RATING")));
     }if(selectedfields.contains("ISSUE_TITLE")){
      repobj.setIssuetitle(checkNull(db.getString(i, "ISSUE_TITLE")));
     }if(selectedfields.contains("ISSUE_ID")){
      repobj.setIssueID(checkNull(db.getString(i, "ISSUE_ID")));
     }if(selectedfields.contains("ISSUE_ID")){
      repobj.setIssueitemno(checkNull(db.getString(i, "ISSUE_ID_DISPLAY")));
     }if(selectedfields.contains("OWNER_ID")){
      repobj.setIssueowner(checkNull(db.getString(i, "OWNER_ID")));
     }if(selectedfields.contains("TARGET_DATE")){
      repobj.setIssuereviseddate(db.getString(i, "TARGET_DATE"));
     }if(selectedfields.contains("STATUS")){
      repobj.setIssuestatus(checkNull(db.getString(i, "STATUS")));
     }if(selectedfields.contains("COMMENT")){// upto second report
      repobj.setIssueitemtaskcomments(checkNull(db.getString(i, "COMMENT")));
     }if(selectedfields.contains("EXEC_SPONSOR")){
      repobj.setExecutivesponsor(checkNull(db.getString(i, "EXEC_SPONSOR")));
     }if(selectedfields.contains("ISSUE_DESC")){
      repobj.setTopissuefromcssurvey(checkNull(db.getString(i, "ISSUE_DESC")));
     }if(selectedfields.contains("XDR56TFC6")){
     	String workflowID =repobj.getWorkflowID(); 
     	repobj.setTopissuefromcssurvey(checkNull(getTopCSIssues(workflowID)));
     }
    }//if
    reportlist.add(repobj);
    repobj = null;
   }//for
   db.close();
   db = null;
  }catch (Exception e){
   e.getMessage();
  }finally{
   if (db != null) {
    try {
     db.close();
     db = null;
    } catch (Exception e) {
    }
   }
  }
  return reportlist;
 }//method
 
 
 /**
 * @since 7.1
 * @author KP
 */
private String getTopCSIssues(String workflowID) {
	String returnString = "";
	DBAccess db = null;
	try{
		db = new DBAccess();
		db.prepareDirectQuery(
				"select b.issue_title from ets.wf_issue_wf_map a, ets.wf_issue b where a.issue_id = b.issue_id  and a.wf_id in "+
				"(select distinct  a.wf_id "+
				"from ets.wf_def a, ets.wf_def b where "+ 
				"( "+
				"(a.creation_date < b.creation_date and a.quarter=b.quarter and a.year=b.year) or "+
				"(cast(a.year as int) = cast(b.year as int) - 1 and a.quarter >= b.quarter) or "+
				"(a.year = b.year and a.quarter < b.quarter) "+
				") "+
				"and b.wf_id='"+workflowID+"' and a.project_id=b.project_id) "+
				"with ur "
		);
		int nrows = db.execute();
		if(nrows>0)
		{
			for(int i=0; i<nrows-1; i++)
			{
				returnString+=db.getString(i,0)+", ";
			}
			returnString+=db.getString(nrows-1,0);
		}
		db.doCommit();
		db.close();
		db=null;
	}catch(Exception e)
	{
		db.doRollback();
		e.printStackTrace();
		try{db.close();}catch(Exception ex){}
		db = null;
	}
	if("".equals(returnString))return "--NONE--";
	return returnString;
}

public ArrayList stringToArrayList(String[] inputString){
  ArrayList al = new ArrayList(inputString.length);
  for (int j=0;j<inputString.length;j++) {
      al.add(inputString[j]);
  }
  return al;
 }
 public static String getWhereCondition(ArrayList list,String reportid){
  String whereCondition = "";
  StringBuffer sb = new StringBuffer();
  
  if(reportid.equalsIgnoreCase("WF002")){
   System.out.println("inside WF002----------");
   ArrayList item = removeIssueItem(list,reportid);
   ArrayList newitem = removeItem(list);
   System.out.println(item + "----------------item--------------");
   if(item!=null && item.size()>0 ){
    if(newitem.size()> 0){
     sb.append(" ETS.ETS_PROJECTS.PROJECT_ID =  ETS.WF_ISSUE_WF_MAP.PROJECT_ID AND ets.wf_issue_wf_map.wf_id = ets.wf_def.wf_id and ");
    }
    for(int condition=0;condition<item.size();condition++){
      String tname = (String)item.get(condition);
      if(!"ETS_PROJECTS".equalsIgnoreCase(tname)){
          sb.append(" ETS.ETS_PROJECTS.PROJECT_ID = ETS."+tname+".PROJECT_ID");
          sb.append(" AND ");
      }
       }
   }
   
   System.out.println(newitem + "----------------newitem --------------");
   if(newitem!=null && newitem.size()>0 ){ 
    for(int condition=0;condition<newitem.size();condition++){
      String tname = (String)newitem.get(condition);
          sb.append(" ETS.WF_ISSUE_WF_MAP.ISSUE_ID = ETS."+tname+".ISSUE_ID");
          sb.append(" AND ");
       }
   }
  }else if(reportid.equalsIgnoreCase("WF003")){
   ArrayList item = removeIssueItem(list,reportid);
   if(item!=null && item.size()>0 ){ 
    for(int condition=0;condition<item.size();condition++){
      String tname = (String)item.get(condition);
      if(!"ETS_PROJECTS".equalsIgnoreCase(tname)){
          sb.append(" ETS.ETS_PROJECTS.PROJECT_ID = ETS."+tname+".PROJECT_ID");
          sb.append(" AND ");
      }
       }
   }
   if(list.contains("WF_ISSUE")){
      sb.append(" ETS.WF_PREPARE_PREVIOUS_ISSUES.ISSUE_ID = ETS.WF_ISSUE.ISSUE_ID");
     }
  }else {
   if(list!=null && list.size()>0 ){ 
    for(int condition=0;condition<list.size();condition++){
      String tname = (String)list.get(condition);
      if(!"ETS_PROJECTS".equalsIgnoreCase(tname)){
          sb.append(" ETS.ETS_PROJECTS.PROJECT_ID = ETS."+tname+".PROJECT_ID");
          sb.append(" AND ");
      }
       }
   }
  }
    
  if(sb.toString().length()>0)
     whereCondition=sb.toString();
  
  return whereCondition;
 }
 public static String viewAllExists(String[] list){
     String exist = "true";
         if(list!=null && list.length>0){
            for(int i = 0;i< list.length;i++){
               if("All Values".equalsIgnoreCase(list[i])){
                   exist = "true";
                   break;
               }else{
                 if(exist!=null && exist.trim().length()==0 || "true".equalsIgnoreCase(exist))
                  exist = "'"+list[i]+"'";
                 else
                  exist = exist +",'" + list[i]+ "'";
                 
               }
            }
         }
   return exist;
   }
 /**
  * 
  * @param alist
  * @return
  */
 public static ArrayList removeIssueItem(ArrayList alist,String reportid){
   ArrayList list = new ArrayList();
   if(reportid.equalsIgnoreCase("WF002")){
    for(int i=0;i<alist.size();i++){
       String ss = ((String)alist.get(i)).trim();
       if(ss.equalsIgnoreCase("ETS_PROJECTS") && !list.contains("ETS_PROJECTS")){
        list.add("ETS_PROJECTS");
       }if(ss.equalsIgnoreCase("WF_DEF") && !list.contains("WF_DEF")){
        list.add("WF_DEF");
       }if(ss.equalsIgnoreCase("WF_HISTORY") && !list.contains("WF_HISTORY")){
        list.add("WF_HISTORY");
       }
      }
   }if(reportid.equalsIgnoreCase("WF003")){
    for(int i=0;i<alist.size();i++){
       String ss = ((String)alist.get(i)).trim();
       if(ss.equalsIgnoreCase("ETS_PROJECTS") && !list.contains("ETS_PROJECTS")){
        list.add("ETS_PROJECTS");
       }if(ss.equalsIgnoreCase("WF_DEF") && !list.contains("WF_DEF")){
        list.add("WF_DEF");
       }if(ss.equalsIgnoreCase("ETS_CALENDAR") && !list.contains("ETS_CALENDAR")){
        list.add("ETS_CALENDAR");
       }if(ss.equalsIgnoreCase("WF_STAGE_IDENTIFY_SETMET") && !list.contains("WF_STAGE_IDENTIFY_SETMET")){
        list.add("WF_STAGE_IDENTIFY_SETMET");
       }if(ss.equalsIgnoreCase("WF_STAGE_DOCUMENT_SETMET") && !list.contains("WF_STAGE_DOCUMENT_SETMET")){
        list.add("WF_STAGE_DOCUMENT_SETMET");
       }if(ss.equalsIgnoreCase("WF_ISSUE") && !list.contains("WF_PREPARE_PREVIOUS_ISSUES")){
        list.add("WF_PREPARE_PREVIOUS_ISSUES");
       }
      }
   }
   
   return list;
  }
 /**
  * 
  * @param alist
  * @return
  */
  public static ArrayList removeItem(ArrayList alist){
   ArrayList list = new ArrayList();
   for(int i=0;i<alist.size();i++){
    String ss = ((String)alist.get(i)).trim();
    if(ss.equalsIgnoreCase("WF_ISSUE") && !list.contains("WF_ISSUE")){
     list.add("WF_ISSUE");
    }if(ss.equalsIgnoreCase("WF_ISSUE_OWNER") && !list.contains("WF_ISSUE_OWNER")){
     list.add("WF_ISSUE_OWNER");
    }
   }
  return list;
  }
  //Download
  public StringBuffer downloadReportPage(ArrayList columnsToShow,ArrayList reportResults,String reportid) {
 StringBuffer b = new StringBuffer();
 b = printSearchResults(columnsToShow, reportResults ,reportid , true);
 return b;
}
  public static StringBuffer printSearchResults(ArrayList columnsToShow,ArrayList reportResults,String reportid, boolean download){
 StringBuffer buf = new StringBuffer();
 if (download){
  buf.append(getCSVResults(columnsToShow,reportResults,reportid,buf));
 }
 return buf;
}
 
   public static StringBuffer getCSVResults(ArrayList columnsToShow,ArrayList reportResults,String reportid, StringBuffer b){
 StringBuffer buf = new StringBuffer();
  if(reportResults.size() > 0){
   for(int i=0;i<columnsToShow.size();i++){
    buf.append((String)columnsToShow.get(i));
    buf.append(",");
   }
   buf.append("\n");
   
   java.util.Iterator it = reportResults.iterator();
       while(it.hasNext()){
        WorkflowMatrixReportObject obj = new WorkflowMatrixReportObject();
        obj = (oem.edge.ets.fe.workflow.setmet.WorkflowMatrixReportObject)it.next();
        buf.append(obj.getWorkspace());
        buf.append(",");
        buf.append(obj.getBrand());
        buf.append(",");
        buf.append(obj.getProcess());
        buf.append(",");
        buf.append(obj.getBusinesssector());
        buf.append(",");
        buf.append(obj.getScesector());
        buf.append(",");
        if(reportid.equalsIgnoreCase("WF001")){
         if(columnsToShow.contains("COMPANY")){
          buf.append(obj.getClientname());
          buf.append(",");
         }if(columnsToShow.contains("WF_NAME")){
          buf.append(obj.getAccountcontact());
          buf.append(",");
         }if(columnsToShow.contains("WF_CURR_STAGE_NAME")){
            buf.append(obj.getCurrStageName());
            buf.append(",");
         }if(columnsToShow.contains("SCHEDULE_DATE")){
          buf.append(obj.getMeetingdate());
          buf.append(",");
         }if(columnsToShow.contains("OVERAL_SCORE")){
          buf.append(obj.getOverallscorerating());
          buf.append(",");
         }if(columnsToShow.contains("NSI_RATING")){
          buf.append(obj.getNsirating());
          buf.append(",");
         }
        }else if(reportid.equalsIgnoreCase("WF002")){
         if(columnsToShow.contains("COMPANY")){
          buf.append(obj.getClientname());
          buf.append(",");
         }if(columnsToShow.contains("WF_NAME")){
          buf.append(obj.getAccountcontact());
          buf.append(",");
         }if(columnsToShow.contains("ISSUE_TITLE")){
          buf.append(obj.getIssuetitle());
          buf.append(",");
         }if(columnsToShow.contains("ISSUE_ID")){
          buf.append(obj.getIssueitemno());
          buf.append(",");
         }if(columnsToShow.contains("OWNER_ID")){
          buf.append(obj.getIssueowner());
          buf.append(",");
         }if(columnsToShow.contains("TARGET_DATE")){
          buf.append(obj.getIssuereviseddate());
          buf.append(",");
         }if(columnsToShow.contains("STATUS")){
          buf.append(obj.getIssuestatus());
          buf.append(",");
         }if(columnsToShow.contains("COMMENT")){
          buf.append(obj.getIssueitemtaskcomments());
          buf.append(",");
         }
        }else if(reportid.equalsIgnoreCase("WF003")){
         if(columnsToShow.contains("COMPANY")){
          buf.append(obj.getClientname());
          buf.append(",");
         }if(columnsToShow.contains("WF_NAME")){
          buf.append(obj.getAccountcontact());
          buf.append(",");
         }if(columnsToShow.contains("SCHEDULE_DATE")){
          buf.append(obj.getMeetingdate());
          buf.append(",");
         }if(columnsToShow.contains("EXEC_SPONSOR")){
          buf.append(obj.getExecutivesponsor());
          buf.append(",");
         }if(columnsToShow.contains("NSI_RATING")){
          buf.append(obj.getNsirating());
          buf.append(",");
         }if(columnsToShow.contains("OVERAL_SCORE")){
          buf.append(obj.getOverallscorerating());
          buf.append(",");
         }
        }
        buf.append("\n");
       }
  }else{
   buf.append("No results matched\n");
  }
 return buf;
   }
   public String getQueryResult(String[] values,WorkflowMatrixObject matrix,String reportid){
           System.out.println("===============================================================================");
     ArrayList tNames = new ArrayList();   
   StringBuffer query = new StringBuffer("SELECT DISTINCT ");
   String tablenames = "";
   String fieldnames = "";
   boolean contains = false;
   ArrayList selectedfields = new ArrayList();
   
   tNames.add("ETS_PROJECTS");
   
   if(values!=null && values.length > 0){
    for(int i=0;i< values.length; i++){
     String tabNames = values[i].substring(0,values[i].indexOf(".")).trim();
     selectedfields.add(values[i].substring(values[i].indexOf(".")+1).trim());
     if(!tNames.contains(tabNames)&&!"WF_ZAQ12WSX".equals(tabNames)){
      tNames.add(tabNames);
     }
    }
   }
   for(int j=0;j<tNames.size();j++){
    tablenames = tablenames + "ETS."+tNames.get(j) + ",";
   }
   tablenames = tablenames.substring(0, (tablenames.length() - 1)) + tablenames.substring(tablenames.length());
   if(values!=null && values.length > 0){
    for(int i=0;i< values.length; i++){
    	if(!(values[i].trim().equals("WF_ZAQ12WSX.XDR56TFC6")))
    		fieldnames = fieldnames + "ETS."+(values[i].trim())+ ",";
    	else
    	{
    		//fieldnames += "previssues.issue_title,";
    	}
    }
   }
   if(values!=null && values.length > 0){
    fieldnames = fieldnames.substring(0, (fieldnames.length() - 1)) + fieldnames.substring(fieldnames.length());
   }
   //Query Formation 
   query.append("ETS.ETS_PROJECTS.PROJECT_NAME,ETS.ETS_PROJECTS.PROCESS,ETS.ETS_PROJECTS.SCE_SECTOR," +
      "ETS.ETS_PROJECTS.BRAND,ETS.ETS_PROJECTS.SECTOR,ETS.ETS_PROJECTS.PROJECT_ID");
   
   if(fieldnames!=null && fieldnames.length() > 0){
    query.append(","+fieldnames);
   }
   
   query.append("  FROM  ");
   
   if(reportid.equalsIgnoreCase("WF001")){
    query.append(" ETS.ETS_PROJECTS,ETS.WF_DEF,ETS.WF_STAGE_DOCUMENT_SETMET," +
        "ETS.ETS_CALENDAR,ETS.WF_STAGE_IDENTIFY_SETMET");
   }if(reportid.equalsIgnoreCase("WF002")){
    query.append( " ETS.ETS_PROJECTS,ETS.WF_DEF,ETS.WF_ISSUE,ETS.WF_ISSUE_OWNER," +
        "ETS.WF_ISSUE_WF_MAP ");
   }if(reportid.equalsIgnoreCase("WF003")){
   	query.append(" ETS.ETS_PROJECTS,ETS.WF_DEF," +
	"ETS.ETS_CALENDAR,ETS.WF_STAGE_IDENTIFY_SETMET");
   }
   
   query.append(" WHERE ");
   
   String conditionValues=viewAllExists(matrix.getWorkspaceslist());
   
   if(!conditionValues.equalsIgnoreCase("true")){
    query.append(" ETS.ETS_PROJECTS.PROJECT_ID IN ("+conditionValues+")");
    query.append(" AND ");
    contains = true;
   }
   
   conditionValues=viewAllExists(matrix.getBrandlist());
   
   if(!conditionValues.equalsIgnoreCase("true")){
    query.append(" ETS.ETS_PROJECTS.BRAND IN ("+conditionValues+")");
    query.append(" AND ");
    contains = true;
   }
   
   conditionValues=viewAllExists(matrix.getBusinesssectorlist());
   
   if(!conditionValues.equalsIgnoreCase("true")){
    query.append(" ETS.ETS_PROJECTS.SECTOR IN ("+conditionValues+")");
    query.append(" AND ");
    contains = true;
   }
   
   conditionValues=viewAllExists(matrix.getProcesslist());
   
   if(!conditionValues.equalsIgnoreCase("true")){
    query.append(" ETS.ETS_PROJECTS.PROCESS IN ("+conditionValues+")");
    query.append(" AND ");
    contains = true;
   }
   
   conditionValues=viewAllExists(matrix.getScesectorlist());
   
   if(!conditionValues.equalsIgnoreCase("true")){
    query.append(" ETS.ETS_PROJECTS.SCE_SECTOR IN ("+conditionValues+")");
    query.append(" AND ");
    contains = true;
   }
   
   String qryString = query.toString();
   /*if(contains){
    if(qryString.trim().substring((qryString.length()-3)).equalsIgnoreCase("AND"))
    {qryString = qryString.substring(0,(qryString.length()-3));}
   }*/
   if(reportid.equalsIgnoreCase("WF001")){
    qryString = qryString + "ETS.ETS_PROJECTS.PROJECT_ID = ETS.WF_DEF.PROJECT_ID AND  " +
          "ETS.wf_def.wf_id = ETS.WF_STAGE_IDENTIFY_SETMET.wf_ID AND " +
          "ETS.WF_DEF.MEETING_ID = ETS.ETS_CALENDAR.CALENDAR_ID AND " +
          "ETS.WF_STAGE_IDENTIFY_SETMET.wf_ID = ETS.WF_STAGE_DOCUMENT_SETMET.wf_id " +
          "ORDER BY ETS.ETS_PROJECTS.PROJECT_NAME with ur";
   }else if(reportid.equalsIgnoreCase("WF002")){
    qryString = qryString + "ETS.ETS_PROJECTS.PROJECT_ID = ETS.WF_DEF.PROJECT_ID and " +
          " ets.wf_def.wf_id = ets.wf_issue_wf_map.wf_id and  " +
          " ets.wf_issue_wf_map.issue_id = ets.wf_issue.Issue_id and " +
          " ETS.WF_ISSUE_WF_MAP.ISSUE_ID = ETS.WF_ISSUE_OWNER.ISSUE_ID " +
          " ORDER BY ETS.ETS_PROJECTS.PROJECT_NAME with ur" ;
   }else if(reportid.equalsIgnoreCase("WF003")){
   	qryString = qryString + "ETS.ETS_PROJECTS.PROJECT_ID = ETS.WF_DEF.PROJECT_ID AND  " +
		//" ets.wf_def.wf_id = ets.wf_issue_wf_map.wf_id AND  " +
		"ETS.wf_def.wf_id = ETS.WF_STAGE_IDENTIFY_SETMET.wf_ID AND " +
		"ETS.WF_DEF.MEETING_ID = ETS.ETS_CALENDAR.CALENDAR_ID " +
		//"ETS.WF_STAGE_IDENTIFY_SETMET.wf_ID = ETS.WF_STAGE_DOCUMENT_SETMET.wf_id AND" +
		//" ETS.WF_ISSUE_WF_MAP.ISSUE_ID = ETS.WF_issue.Issue_id " +
		"ORDER BY ETS.ETS_PROJECTS.PROJECT_NAME with ur";
   }
   qryString = qryString.trim();
   System.out.println("===============================================================================");
   return qryString;
  }
  public String getQueryResultOld(String[] values,WorkflowMatrixObject matrix,String reportid){
   
     ArrayList tNames = new ArrayList();   
   StringBuffer query = new StringBuffer("SELECT DISTINCT ");
   String tablenames = "";
   String fieldnames = "";
   boolean contains = false;
   ArrayList selectedfields = new ArrayList();
   
   tNames.add("ETS_PROJECTS");
   
   if(values!=null && values.length > 0){
    for(int i=0;i< values.length; i++){
     String tabNames = values[i].substring(0,values[i].indexOf(".")).trim();
     selectedfields.add(values[i].substring(values[i].indexOf(".")+1).trim());
     if(!tNames.contains(tabNames)){
      tNames.add(tabNames);
     }
    }
   }
   for(int j=0;j<tNames.size();j++){
    tablenames = tablenames + "ETS."+tNames.get(j) + ",";
   }
   tablenames = tablenames.substring(0, (tablenames.length() - 1)) + tablenames.substring(tablenames.length());
   if(values!=null && values.length > 0){
    for(int i=0;i< values.length; i++){
     fieldnames = fieldnames + "ETS."+(values[i].trim())+ ",";
    }
   }
   if(values!=null && values.length > 0){
    fieldnames = fieldnames.substring(0, (fieldnames.length() - 1)) + fieldnames.substring(fieldnames.length());
   }
   //Query Formation 
   query.append("ETS.ETS_PROJECTS.PROJECT_NAME,ETS.ETS_PROJECTS.PROCESS,ETS.ETS_PROJECTS.SCE_SECTOR," +
      "ETS.ETS_PROJECTS.BRAND,ETS.ETS_PROJECTS.SECTOR,ETS.ETS_PROJECTS.PROJECT_ID");
   
   if(fieldnames!=null && fieldnames.length() > 0){
    query.append(","+fieldnames);
   }
   
   query.append("  FROM  ");
   query.append(tablenames);
   if(reportid.equalsIgnoreCase("WF002") && (tNames.contains("WF_ISSUE_OWNER") || tNames.contains("WF_ISSUE"))){
    query.append(",ETS.WF_ISSUE_WF_MAP ");
   }if(reportid.equalsIgnoreCase("WF003") && tNames.contains("WF_ISSUE")){
    query.append(",ETS.WF_PREPARE_PREVIOUS_ISSUES");
   }
   query.append(" WHERE");
   
   String conditionValues=viewAllExists(matrix.getWorkspaceslist());
   
   if(!conditionValues.equalsIgnoreCase("true")){
    query.append(" ETS.ETS_PROJECTS.PROJECT_ID IN ("+conditionValues+")");
    query.append(" AND");
   }
   
   conditionValues=viewAllExists(matrix.getBrandlist());
   
   if(!conditionValues.equalsIgnoreCase("true")){
    query.append(" ETS.ETS_PROJECTS.BRAND IN ("+conditionValues+")");
    query.append(" AND");
   }
   
   conditionValues=viewAllExists(matrix.getBusinesssectorlist());
   
   if(!conditionValues.equalsIgnoreCase("true")){
    query.append(" ETS.ETS_PROJECTS.SECTOR IN ("+conditionValues+")");
    query.append(" AND");
   }
   
   conditionValues=viewAllExists(matrix.getProcesslist());
   
   if(!conditionValues.equalsIgnoreCase("true")){
    query.append(" ETS.ETS_PROJECTS.PROCESS IN ("+conditionValues+")");
    query.append(" AND");
   }
   
   conditionValues=viewAllExists(matrix.getScesectorlist());
   
   if(!conditionValues.equalsIgnoreCase("true")){
    query.append(" ETS.ETS_PROJECTS.SCE_SECTOR IN ("+conditionValues+")");
    query.append(" AND");
   }
   
   String qryString = query.toString();
   
   qryString = qryString+" " +(getWhereCondition(tNames,reportid)).trim() ;
   
   qryString = qryString.trim();
   
   if(qryString.trim().substring((qryString.length()-3)).equalsIgnoreCase("AND"))
    {qryString = qryString.substring(0,(qryString.length()-3));}
   if(qryString.trim().substring((qryString.length()-5)).equalsIgnoreCase("WHERE"))
    {qryString = qryString.substring(0,(qryString.length()-5));}
   
   qryString = qryString +" ORDER BY ETS.ETS_PROJECTS.PROJECT_NAME";
   
   return qryString;
  }
  /**
   * 
   * @param sInString
   * @return
   */
  public static String checkNull(String sInString) {
 String sOutString = "";
 if (sInString == null || sInString.trim().equals("")) {
  sOutString = "";
 } else {
  sOutString = sInString.trim();
 }
 return sOutString;
 
}
}
