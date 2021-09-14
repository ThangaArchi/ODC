/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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



import java.util.HashMap;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.w3c.dom.*;
import javax.xml.parsers.*;


/**
 * Query Cache Service searches for  the query.xml file
 * and load it in cache.
 */

 public class QueryCaching1{
   
	 private HashMap query	= null;
	 
	 /**
	  * Query Cache is a Singleton
	  * One instance serves all the request
	  */
	 private QueryCaching1(){
		 query = new HashMap();
	      init();
	     
	 }
	 

	 /**
	  * Methods returns QueryCache
	  *
	  * @return	QueryCache Returns the QueryCache
	  */
	 public static QueryCaching1 getInstance(){
	 	
		  if(queryCache == null){
		  	
		 	queryCache = new QueryCaching1();
		}
			//logger.info("Instance returned....."+queryCache);
		
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

    
	 private void init(){
		System.out.println("The Inside init()");
		try{		
			  URL url = new URL("http://localhost:9080/DashboardWeb/query.xml");	             		
	     	  addToCache(url);            
		}catch(Exception e){
			 logger.error("QueryCache.init() failed " + e);
		System.out.println("the exception at query caching is"+e);
		}
	 }

    
    /**
     * Adds the information into cache
     *
     * @param   file    File
     */
    private void addToCache(URL file){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file.openStream());
			NodeList list = doc.getElementsByTagName("sql");
				
            if(list != null){
                for(int i=0; i < list.getLength(); i++){
					Element node = (Element)list.item(i);					
					query.put(node.getAttribute("name"),node.getFirstChild().getNodeValue().trim());
                }
            }
		}catch(Exception e){
			logger.error("QueryCache.init() failed \n " + e);
		}
    }

	 private static QueryCaching1 queryCache	= null;
	 private static Log logger = WorkflowLogger.getLogger(QueryCaching1.class);
	 
	 
 }
