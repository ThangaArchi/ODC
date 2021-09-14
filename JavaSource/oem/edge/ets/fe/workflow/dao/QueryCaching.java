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
 * Created on Feb 6, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.workflow.dao;
import oem.edge.ets.fe.workflow.log.*;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.w3c.dom.*;
import javax.xml.parsers.*;


/**
 * Query Cache Service searches for  the query.xml file
 * and load it in cache.
 */

 public class QueryCaching{
   
	 private HashMap query	= null;
	 
	 /**
	  * Query Cache is a Singleton
	  * One instance serves all the request
	  */
	 private QueryCaching(){
		 query = new HashMap();
	 }
	 
	 
	 private QueryCaching(InputStream is){	 	
	 	  init(is);
	 }

	 /**
	  * Methods returns QueryCache
	  *
	  * @return	QueryCache Returns the QueryCache
	  */
	 public static QueryCaching getInstance(){
	 	
		  if(queryCache == null){
		  	
		 	queryCache = new QueryCaching();
		}
			logger.debug("Instance returned....."+queryCache);
		
		 return queryCache;
	 }

     /**
      * Retrieves the query corresponds to the key
      *
      * @param  key String The key for the query
      * @return String the query
      */
	 public String getQuery(String key){
		 if(query != null){
			 return (String)query.get(key);
		 }
		return null;
	 }
	 
	 public static QueryCaching getInstance(String file){
	 	try{
	 	  if(queryCache==null){
	 	  	 queryCache = new QueryCaching(new FileInputStream(file));
	 	  }
	 	}catch(Exception ex){
	 		logger.error("the exception ^^^^^^^^^^^^^^^^^^^^^^^^^^^ in getInstance method",ex);
	 	}
	 	  return queryCache;
	 }
    
	 private void init(InputStream is){
		logger.debug("The Inside init()");
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			NodeList list = doc.getElementsByTagName("sql");
            if(list != null){
                for(int i=0; i < list.getLength(); i++){
                    Element node = (Element)list.item(i);
                    if(query == null) query = new HashMap();
                    String quee = node.getFirstChild().getNodeValue().trim();
                    query.put(node.getAttribute("name").toLowerCase(),
                           quee );
                    
                    logger.debug("the query is+++++++++++++++"+quee);
                }
            }
		}catch(Exception e){
			logger.error("QueryCache.init() failed \n " + e);
		}
	 }

    
  

	 private static QueryCaching queryCache	= null;
	private static Log logger = WorkflowLogger.getLogger(QueryCaching.class);
	 
	 
 }
