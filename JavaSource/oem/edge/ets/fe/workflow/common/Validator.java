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
 * Created on Sep 21, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.common;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oem.edge.ets.fe.workflow.core.WorkflowStage;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.setmet.document.ScorecardDAO;
import oem.edge.ets.fe.workflow.setmet.validate.ValidateDocumentStageDAO;

import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Validator {
		 
		 
		 private HashMap workflowTypes = new HashMap();
		 private HashMap workflowStages= null;
		 ArrayList methods		 		 		   = null;
		 
		 private static Log logger		 		   		   = WorkflowLogger.getLogger(Validator.class);
		 
		 HashMap map = new HashMap();
		 
		 private static Validator validator = null;
		 
		 private Validator(){
		 		 
		 }
		 private Validator(InputStream is){
		 		 init(is);
		 }
		 private void init(InputStream is){
		 		 try{
		 		 		 		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		 		 		 		 factory.setIgnoringElementContentWhitespace(true);
		 		 		     DocumentBuilder builder = factory.newDocumentBuilder();
		 		 		     Document doc = builder.parse(is);
		 		 		     
		 		 		     NodeList list = doc.getElementsByTagName("WORKFLOWTYPE");
		             if(list != null){
		                 for(int i=0; i < list.getLength(); i++){
		                     Element node = (Element)list.item(i);
		 		 		 		 		 		 String nodeType = node.getAttribute("type");
		 		 		 		 		 		 workflowStages = new HashMap();
		 		 		 		 		 		     NodeList subchild = node.getElementsByTagName("WORKFLOWSTAGE");		 		 		 		 		 		 
		 		 		                 for(int j=0; j < subchild.getLength(); j++){
		 		 		 		 		 		 		 		 Element childNode =(Element)subchild.item(j);		 		 		 		 		 		 		 		 
		 		 		 		 		 		 		 		 String state = childNode.getAttribute("stage"); 
		                              NodeList fields = childNode.getElementsByTagName("WORKFLOWFIELD");		 
		                               methods 		   = new ArrayList();
		 		 		 		 		 		          for(int k=0; k < fields.getLength(); k++){
		 		 		 		 		 		 		 		 		   Element fieldNode = (Element) fields.item(k);
		 		 		 		 		 		 		 		 		   methods.add("get"+fieldNode.getFirstChild().getNodeValue());
		 		 		 		 		 		 		 		   }		 
		 		 		 		 		 		          System.out.println("the state name is"+nodeType+"*********"+state+"KKK"+methods);
		 		 		 		 		 		          
		 		 		 		 		 		 		 		    workflowStages.put(state,methods);
		 		 		 		 		 		 		 }
		 		 		                 workflowTypes.put(nodeType,workflowStages);
		                 }
		             }
		 		 		 
		 		 }catch(Exception ex){
		 		 		   logger.error("Error in parsing the fields xml",ex);
		 		 }
		 		 
		 		 
		 		 
		 }
		 public static Validator getInstance(){
		 		 if(validator==null)
		 		 		  validator= new Validator();
		 		 
		 		 return validator;
		 }
		 
    public static Validator getInstance(String fileName){
    		 System.out.println("The name of he file is &&&&&&&&&&&&&&&&&&&&"+fileName);
    		 try{
    		   if(validator==null)
    		   		     validator = new Validator(new FileInputStream(fileName));
    		 }catch(Exception ex){
    		 		 logger.error("Exception is reading the file in validator getInstance",ex);
    		 }
    		   return validator;
    }
    public void validateStage(WorkflowStage stageObj,ArrayList method){
    		
    		map.put("IBMERS","IBM Attendees");
    	    map.put("MeetingLocn","Meeting Location");

    		 boolean rtnValue=false;
    		 String stage = null;
    		 String wfType = null;
    		 
    		  try{

    		        //Class name to be loaded will be passed from the action class. eg.TestClass will be passed from the action class.

    		        Class c = Class.forName(stageObj.getClassName());
    		 		    stage = stageObj.getStageName();
    		 		    wfType= stageObj.getWorkflowType();
    		 		    stageObj.setWorkflowStatus("true");
    
    		 		    HashMap temp = (HashMap)workflowTypes.get(wfType);
    		 		    ArrayList al = (ArrayList)temp.get(stage.toUpperCase());
    		 		    Method m[]=c.getDeclaredMethods();
    		 		    for(int cnt = 0;cnt<m.length;cnt++){
    		           if(!al.contains(m[cnt].getName())){
    		 		 		 		   String mName = m[cnt].getName();    		 		 		 		   
    		 		 		 		    if(!mName.substring(0,3).equalsIgnoreCase("set") && !mName.substring(0,2).equalsIgnoreCase("is")){
    		 		 		 		      String obj = (String)m[cnt].invoke(stageObj,null);
    		                     if(obj==null ||obj.trim().length()==0 ){
    		                     	System.out.println("MNAME:---"+mName.substring(3));
    		                     	method.add(map.get(mName.substring(3)));
    		                     	stageObj.setWorkflowStatus("false");		 
    		                     }
    		 		 		 		    }
    		           }
    		 		    }

    		    }catch(Exception ex){

    		 		    System.out.println("the exception is"+ex);
    		 		    rtnValue=false;
    		    }
    }
    
    public boolean getWorkflowCurrentStage(WorkflowStage stageObj,String action){
    		 String stageName = "";
    		 String objStage = "";
    		 boolean canUpdate = false;
    		 stageName=ScorecardDAO.getWorkflowCurrentStage(stageObj.getProjectID(),stageObj.getWorkflowID());
    		 objStage = stageObj.getStageName();
    		 
    		 if(stageName.equalsIgnoreCase(objStage))
    		 		   canUpdate = true;
    		 else{
    		 		  canUpdate = false;
    		 }
    		 return canUpdate;
    }
    
    public int validateDocumentStage(String projectID,String workflowID,String stageName,String loggedUser){
    		 int canPass = 0;  		 
    		 if("VALIDATE".equalsIgnoreCase(stageName)){
    		 		 		 canPass= ValidateDocumentStageDAO.isStageCompleted(projectID,workflowID);
    		 		 		 if(canPass==3)
    		 		 		 		 ValidateDocumentStageDAO.updateWorkflowStage(projectID,workflowID,stageName,loggedUser);
    		 }else{
    		 		 		 
    		 		     ValidateDocumentStageDAO.updateWorkflowStage(projectID,workflowID,stageName,loggedUser);
    		 }
    		 return canPass;
    }
    
    private String getFieldName(String key){  
    	
        String value =(String)map.get(key);
        return value;
}

}