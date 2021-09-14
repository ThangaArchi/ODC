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
 * Created on Sep 14, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.core;


import java.net.URL;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

import oem.edge.ets.fe.workflow.dao.QueryCaching;
import oem.edge.ets.fe.workflow.util.pdfutils.ReportConfig;
import oem.edge.ets.fe.workflow.common.Validator;




/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WorkflowConfig implements PlugIn {
	
	private String queryFile = null;
	private String fieldList = null;
	
/**
 * @return Returns the fieldList.
 */
public String getFieldList() {
	return fieldList;
}
/**
 * @param fieldList The fieldList to set.
 */
public void setFieldList(String fieldList) {
	this.fieldList = fieldList;
}

	
public WorkflowConfig(){
  	 System.out.println("the plug in calss is initialized ******************************");
  }

/**
 * @return Returns the queryFile.
 */
public String getQueryFile() {
	return queryFile;
}
/**
 * @param queryFile The queryFile to set.
 */
public void setQueryFile(String queryFile) {
	this.queryFile = queryFile;
}

public void init(ActionServlet servlet,ModuleConfig config){
	  try{
	  	  initializeQueryFile(servlet,config);
	  	  initializeWorkflowFields(servlet,config);
	  	  initializeReports(servlet);
	  }catch(Exception ex){
	  	
	  }
	
}
  public void initializeWorkflowFields(ActionServlet servlet,ModuleConfig config){
	  String fileName= "";
  	  try{
  	  	  fileName = servlet.getServletContext().getRealPath(fieldList);
  	  	  Validator qc = Validator.getInstance(fileName);
  	  }catch(Exception ex){
  	  	
  	  }  
  }
  public void initializeQueryFile(ActionServlet servlet,ModuleConfig config){
  	  String fileName= "";
  	  try{
  	  	  fileName = servlet.getServletContext().getRealPath(queryFile);
  	  	  
  	  	  System.out.println("the query file is &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+fileName+queryFile);
  	  	  QueryCaching qc = QueryCaching.getInstance(fileName);
  	  }catch(Exception ex){
  	  	
  	  }
  }
  
  public void initializeReports(ActionServlet servlet)
  {
  	System.out.println("Set report paths for Workflow");
      	ReportConfig.qbrReportFile = servlet.getServletContext().getRealPath("/WEB-INF/config/qbrReport.xml");
	  	ReportConfig.msaReportFile = servlet.getServletContext().getRealPath("/WEB-INF/config/msaReport.xml");
	  	ReportConfig.xslFile = servlet.getServletContext().getRealPath("/WEB-INF/config/xhtml-to-xslfo.xsl");
	  	ReportConfig.msaOverallTemplateFile = servlet.getServletContext().getRealPath("/WEB-INF/config/overallTemplate.xml");
	  	ReportConfig.msaScoringTemplateFile = servlet.getServletContext().getRealPath("/WEB-INF/config/scoringTemplate.xml");

	  	System.out.println(ReportConfig.qbrReportFile);
	  	System.out.println(ReportConfig.msaReportFile);
	  	System.out.println(ReportConfig.xslFile);
  }
  public void destroy(){
	  queryFile = null;
	  fieldList = null;
  }
}
