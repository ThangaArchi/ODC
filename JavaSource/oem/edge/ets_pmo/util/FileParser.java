package oem.edge.ets_pmo.util;

import java.io.*;
import oem.edge.ets_pmo.common.*;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.PropertyConfigurator;
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

/**
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FileParser {
	private static String CLASS_VERSION = "4.5.1";
	static Logger logger = Logger.getLogger(FileParser.class);
	
	private  String xmlStr;

	private String appStartTag			;
	private  String appFinishTag		;
	
	private  String priorToprojectidStartTag	;
	private  String projectidStartTag		;
	private  String projectidFinishTag		;
	
	private  String useridStartTag		;
	private  String useridFinishTag	;
	
	private String transactionidStartTag ;
	private String transactionidFinishTag;
	
	private String transactionVersionStartTag;
	private String transactionVersionFinishTag;
	
	private String sourceStartTag;
	private String sourceFinishTag;
	
	private String destinationStartTag;
	private String destinationFinishTag;
	
	public String ExtractTransactionID(){
		int transactionidStartTagPos	= xmlStr.indexOf(transactionidStartTag);
		transactionidStartTagPos		= transactionidStartTagPos + transactionidStartTag.length();
		/*
		 * Get the value after <transactionid>. transactionidValue
		 */
		String transactionidValue = null;
		transactionidValue = xmlStr.substring(transactionidStartTagPos, xmlStr.indexOf(transactionidFinishTag, transactionidStartTagPos));
		logger.debug(transactionidValue);
		return transactionidValue;				
		
	}
	
	public String ExtractTransactionVersion(){
		int transactionVersionStartTagPos	= xmlStr.indexOf(transactionVersionStartTag);
		transactionVersionStartTagPos		= transactionVersionStartTagPos + transactionVersionStartTag.length();
		/*
		 * Get the value after <transactionVersion>. transactionVersionValue
		 */
		String transactionVersionValue = null;
		transactionVersionValue = xmlStr.substring(transactionVersionStartTagPos, xmlStr.indexOf(transactionVersionFinishTag, transactionVersionStartTagPos));
		logger.debug(transactionVersionValue);
		return transactionVersionValue;				
		
	}
	
	public String ExtractSource(){
		int sourceStartTagPos	= xmlStr.indexOf(sourceStartTag);
		sourceStartTagPos		= sourceStartTagPos + sourceStartTag.length();
		/*
		 * Get the value after <source>. sourceValue
		 */
		String sourceValue = null;
		sourceValue = xmlStr.substring(sourceStartTagPos, xmlStr.indexOf(sourceFinishTag, sourceStartTagPos));
		logger.debug(sourceValue);
		return sourceValue;				
		
	}
	
	public String ExtractDestination(){
		int destinationStartTagPos	= xmlStr.indexOf(destinationStartTag);
		destinationStartTagPos		= destinationStartTagPos + destinationStartTag.length();
		/*
		 * Get the value after <destination>. destinationValue
		 */
		String destinationValue = null;
		destinationValue = xmlStr.substring(destinationStartTagPos, xmlStr.indexOf(destinationFinishTag, destinationStartTagPos));
		logger.debug(destinationValue);
		return destinationValue;				
		
	}
	
	public String ExtractUserID(){
		int useridStartTagPos	= xmlStr.indexOf(useridStartTag);
		useridStartTagPos		= useridStartTagPos + useridStartTag.length();
		/*
		 * Get the value after <userid>. useridValue
		 */
		String useridValue = null;
		useridValue = xmlStr.substring(useridStartTagPos, xmlStr.indexOf(useridFinishTag, useridStartTagPos));
		logger.debug(useridValue);
		return useridValue;				
		
	}
	public String ExtractAppName(){
		int appStartTagPos = xmlStr.indexOf(appStartTag);
		appStartTagPos =  appStartTagPos + appStartTag.length();
		
		/*
		 * Get the value after <app>. appValue
		 */
		String appValue = null;
		appValue = xmlStr.substring(appStartTagPos, xmlStr.indexOf(appFinishTag, appStartTagPos));
		logger.debug(appValue);
		return appValue;		
	
	}
	public String ExtractProjectID(){
		int projectidStartTagPos = xmlStr.indexOf(projectidStartTag, xmlStr.indexOf(priorToprojectidStartTag));
		projectidStartTagPos =  projectidStartTagPos + projectidStartTag.length();
		
		/*
		 * Get the value after <projectid>. projectidValue
		 */
		String projectidValue = null;
		projectidValue = xmlStr.substring(projectidStartTagPos, xmlStr.indexOf(projectidFinishTag, projectidStartTagPos));
		logger.debug(projectidValue);
		return projectidValue;	
		
	}
	
	public FileParser(String s){
		
		this.appStartTag 					= ETSPMOGlobalInitialize.getAppStartTag().toLowerCase();
		this.appFinishTag					= ETSPMOGlobalInitialize.getAppFinishTag().toLowerCase();
		this.priorToprojectidStartTag 		= ETSPMOGlobalInitialize.getPriortoProjectidStartTag().toLowerCase();
		this.projectidStartTag				= ETSPMOGlobalInitialize.getProjectidStartTag().toLowerCase();
		this.projectidFinishTag			= ETSPMOGlobalInitialize.getProjectidFinishTag().toLowerCase();
		this.useridStartTag				= ETSPMOGlobalInitialize.getUseridStartTag().toLowerCase();
		this.useridFinishTag				= ETSPMOGlobalInitialize.getUseridFinishTag().toLowerCase();
		this.transactionidStartTag 		= ETSPMOGlobalInitialize.getTransactionidStartTag().toLowerCase();
		this.transactionidFinishTag		= ETSPMOGlobalInitialize.getTransactionidFinishTag().toLowerCase();
		this.transactionVersionStartTag	= ETSPMOGlobalInitialize.getTransactionVersionStartTag().toLowerCase();
		this.transactionVersionFinishTag	= ETSPMOGlobalInitialize.getTransactionVersionFinishTag().toLowerCase();
		this.sourceStartTag				= ETSPMOGlobalInitialize.getSourceStartTag().toLowerCase();
		this.sourceFinishTag				= ETSPMOGlobalInitialize.getSourceFinishTag().toLowerCase();
		this.destinationStartTag			= ETSPMOGlobalInitialize.getDestinationStartTag().toLowerCase();
		this.destinationFinishTag			= ETSPMOGlobalInitialize.getDestinationFinishTag().toLowerCase();
		
		File file  = new File(s);
		StringBuffer strbuf = new StringBuffer();  
		try{
				FileInputStream fin = new FileInputStream(file);
			//	fin.read(
				String ls_str ;

				BufferedReader ls_in
          					= new BufferedReader(new InputStreamReader(fin));

					    try {
								while ((ls_str = ls_in.readLine()) != null) {
									strbuf.append(ls_str + "\n");
							    	
							}
	   				 	} catch (IOException e) {
						logger.error("IOException in reading XML file from this path: " + s);
				    }
  			  } catch (IOException e1) {
				    System.err.println(e1);
				    System.exit(1);
			}
		xmlStr = strbuf.toString();
		xmlStr = xmlStr.toLowerCase();
		
	}
	
	public static void main(String[] args) {
		if(args.length < 1){
			System.out.println(" Usage : XMLProcessor <log4jPropertyFileLocation>" );
		}
		
		
	   
	   /* 
	    * Tried PropertyConfigurator.configure("oem.edge.ets_pmo.ets_pmo_log4j");
	    * But looks like configure() needs absolute path. 
	    */
	   
	    PropertyConfigurator.configure(args[0]);
	    
	    ETSPMOGlobalInitialize Global = new ETSPMOGlobalInitialize();
		if(Global.getProp() == null){
				Global.Init();	
		}
			String fName = "C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\ETS-PMOIntegration\\StoredXMLs\\Project\\ProjectCreateUpdate\\V7-01-04\\RTFChanges.xml";
			FileParser fp = new FileParser(fName);
			fp.ExtractTransactionID();
			fp.ExtractTransactionVersion();
			fp.ExtractSource();
			fp.ExtractDestination();
		
			fp.ExtractAppName();
			fp.ExtractUserID();
			fp.ExtractProjectID();
		
	}
	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
