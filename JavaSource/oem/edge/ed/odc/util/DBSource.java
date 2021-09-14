package oem.edge.ed.odc.util;

import java.lang.*;
import java.util.*;
import oem.edge.ed.odc.tunnel.common.*;

import org.apache.log4j.Logger;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 1998,2004,2005,2006		                 */ 
/*                                                                       */ 
/*     All Rights Reserved					         */ 
/*     US Government Users Restricted Rights			         */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

public class DBSource {
   static protected Hashtable connections = new Hashtable();
   static String DEFAULT = "_DEFAULT_CONNECTION_";
   
   static Logger log = Logger.getLogger(DBSource.class);
   
   static public void addDBConnection(String shortname, 
                                  DBConnection c, 
                                  boolean makeDefault) {
      
      if (log.isDebugEnabled()) {
         if (connections.get(shortname) != null) {
            log.debug("Replacing connection " + shortname);
         } else {
            log.debug("Adding connection " + shortname);
         }
      }
      
      connections.put(shortname, c);
      if (makeDefault) {
         connections.put(DEFAULT, c);
      }
   }
   
   static public DBConnection getDBConnection(String shortname) {
      if (shortname == null) shortname = DEFAULT;
      return (DBConnection)connections.get(shortname);
   }
   
   static public DBConnection removeDBConnection(String shortname) {
      if (shortname == null) shortname = DEFAULT;
      
      DBConnection ret = (DBConnection)connections.remove(shortname);
      if (ret != null) {
         if (getDBConnection(null) == ret) {
            connections.remove(DEFAULT);
         }
      }
      return ret;
   }
}
