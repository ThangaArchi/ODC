package oem.edge.ed.odc.util;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.net.*;

import oem.edge.ed.odc.tunnel.common.DebugPrint;
import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.util.*;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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

public class ReloadingProperty {

   protected String       reloadPropName      = "edodc.reloadPropertyFile";
   protected long         reloadLastModified  = 0;
   protected ConfigObject reloadProp          = null;
   protected String       origFileName        = null;
   protected String       reloadFileName      = null;
   protected String       lastReloadShort     = null;
   protected String       lastReload          = null;
   protected int          propReloadCnt       = 0;
   protected int          propReloadMax       = 500;
   
   protected String       servingDirectories  = null;
   
   protected ConfigObject AppProp  = null;
   
   public ReloadingProperty(String f) throws IOException { 
      load(f);
   }
   
   public void setReloadMax(int v) { propReloadMax = v;    }
   public int  getReloadMax()      { return propReloadMax; }
   
   public void   setServingDirectories(String v) { servingDirectories = v;    }
   public String getServingDirectories()         { return servingDirectories; }
   
  /* Used to override file lookup by prop and default of Orig file */
   public void   setReloadFile(String v)       { reloadFileName = v;    }
   public String getReloadFile()               { return reloadFileName; }
   
   public ReloadingProperty() {}
   
   public void bulkLoad(ConfigObject b) throws IOException {
      AppProp = b;
      origFileName = null;
   }
   
   public void load(String f) throws IOException {
   
      reloaded = true;
      
      origFileName = f;
      File loadFile = new File(f);
      
      if (!loadFile.exists()) {
         String loadpath = findFileWhereEver(f);
         
        // If null, see if we can find it using getResource
         if (loadpath == null) {
           // Only take this method if its different than a regular file OR failed
           //  with above method.
           //URL uf  = Thread.currentThread().getContextClassLoader().getResource(f);
           URL uf  = this.getClass().getClassLoader().getResource(f);
            if (uf != null) {
               String us = uf.toString();
               if (us.startsWith("file:")) {
                 // Just strip off the file: and let code below handle
                  f = us.substring(5);
                  loadFile = new File(f);
                  if (!loadFile.exists()) throw new FileNotFoundException();
                  
                  String sd = getServingDirectories();
                  if (sd != null) { 
                     sd = sd + ":" + loadFile.getParent();
                  } else {
                     sd = loadFile.getParent();
                  }
                  
               } else {
               
                 // We could NOT find the file using normal means, and we HAVE found the
                 //  file via getResource, but its NOT a plain file
                 
                  URLConnection uc = uf.openConnection();
                  AppProp = new ConfigFile(uc.getInputStream());
                  loadFile = null;
                  
               }
            } else {
               throw new FileNotFoundException("Resource file not found: " + f);
            }
         } else {
            loadFile = new File(loadpath);
            if (!loadFile.exists()) throw new FileNotFoundException();
         }
      }
      
     // Only do this if we did NOT use a Resource stream
      if (loadFile != null) {
         String path = loadFile.getCanonicalPath();
         DebugPrint.printlnd(DebugPrint.INFO2, "loadingProp: loading propfile [" + 
                             path + "]");
         
         AppProp = new ConfigFile(path);
      }
      
      reloadProp = null;
      propReloadCnt = 0;
   }
   
   public void   setReloadPropertyName(String v) { reloadPropName = v;    }
   public String getReloadPropertyName()         { return reloadPropName; }

   public boolean tryReload() {
      boolean ret = false;
      
      String reload = reloadFileName;
      if (reload == null) {
         reload = getPropertyNoReload(reloadPropName, origFileName);
      }
      
      if (reload != null) synchronized(this) {
         try {
           // Re-resolve the file only once every 500 times called OR if
           // the base name is different from last time
            String reload2 = lastReloadShort;
            if (lastReloadShort == null         || 
                !lastReloadShort.equals(reload) || 
                lastReload == null              ||
                propReloadCnt > propReloadMax) {
               reload2 = findFileWhereEver(reload);
               DebugPrint.println(DebugPrint.INFO2, 
                                  "Resolved Propfile: " + reload2);
               lastReloadShort = null;
               propReloadCnt   = 0;
               lastReload      = null;
            } else {
               reload2 = lastReload;
               propReloadCnt++;
            }
            
            if (reload2 != null) {
               File reloadFile = new File(reload2);
               if (!reloadFile.exists()) {
                  reload2 = findFileWhereEver(reload);
                  if (reload2 == null) throw new Exception("1111");
                  reloadFile = new File(reload2);
                  if (!reloadFile.exists()) throw new Exception("1111");
                  reloadLastModified = 0L;
                  propReloadCnt = 0;
               }
               
              // Reload it if the stamp is newer, we never had an old load
              //   or the resolved filename changed
               boolean doit = true;
               if (reloadFile.lastModified() > reloadLastModified || 
                  lastReload == null || !lastReload.equals(reload2)) {
                  
                  ret = true;
                  
                  DebugPrint.printlnd(DebugPrint.INFO, "ReloadingProp: getProperty: Reloading propfile [" + reload2 + "]");
                  
                  ConfigObject lastReloadProp = reloadProp;
                  reloadProp = new ConfigFile(reload2);
                  reloadLastModified = reloadFile.lastModified();
                  
                  lastReloadShort = reload;
                  lastReload      = reload2;
                  
                  if (DebugPrint.getLevel() >= DebugPrint.INFO4) {
                     Enumeration enum = reloadProp.getPropertyNames();
                     while(enum != null && enum.hasMoreElements()) {
                        String s = (String)enum.nextElement();
                        String newv = reloadProp.getProperty(s, "");
                        String oldv = null;
                        if (lastReloadProp != null) 
                           oldv = lastReloadProp.getProperty(s);
                        if (oldv == null) {
                           try {
//                              oldv = AppProp.getString(s);
                              oldv = AppProp.getProperty(s);
                           } catch(Exception ex2) {}
                        }
                        
                        if (newv != null && (oldv == null ||
                                             !newv.equals(oldv))) {
                           DebugPrint.println(DebugPrint.INFO3, 
                                              "Replacing key[" + s + 
                                              "] oldv[" +  oldv + "] newv[" + 
                                              newv + "]");
                        }
                     }
                  }
               }
            } else {
               throw new Exception("1111");
            }
         } catch ( Exception e ) {
            if (e.getMessage() == null || !e.getMessage().equals("1111")) {
               DebugPrint.printlnd(DebugPrint.ERROR, "ReloadingProperty: getProperty: Error while loading specified reload file[" 
                                   + reload + "]");
               DebugPrint.println(DebugPrint.ERROR, e);
               reloadProp         = null;
               reloadLastModified = (long)0;
               lastReloadShort    = null;
               propReloadCnt      = 0;
               lastReload         = null;
            }
         }
      }
      reloaded = ret;
      return ret;
   }
   
   boolean reloaded = true;
   public boolean getReloaded() {
      boolean ret = reloaded;
      reloaded = false;
      return ret;
   }
   
   public String getProperty(String in) {
      return getProperty(in, null);
   }
   
   public String getProperty(String in, String def) {
   
      tryReload();
      
      return getPropertyNoReload(in, def);
   }
   
   public synchronized String getPropertyNoReload(String in, String def) {
      String ret = null;
      
      try {
         
         if (reloadProp != null) ret = reloadProp.getProperty(in);
         if (ret == null) {
            if (AppProp != null) {
               ret = AppProp.getProperty(in);
            }
         }
         
         String origret = ret;
         
         ret.trim();
         
        // search and replace any ${var} with that props value
        // This will scan the replaced value as well, so infinite cycles 
        // are possible!!
         int idx = 0;
         while((idx = ret.indexOf("${", idx)) >= 0) {
            int idx2 = ret.indexOf("}", idx);
            if (idx2 > 0) {
               String var = ret.substring(idx+2, idx2);
               String preret  = ret.substring(0, idx);
               String postret = "";
               if (ret.length() > idx2+1) {
                  postret = ret.substring(idx2+1);
               }
               
               String middle = "";
               try {
                  if (reloadProp != null) {
                     middle = reloadProp.getProperty(var, "");
                  }
                  if (middle.equals("")) {
                     middle = AppProp.getProperty(var);
                  }
               } catch(Exception ee) {
               }
               
               String newret = preret + middle + postret;
               if (DebugPrint.getLevel() >= DebugPrint.INFO4) {
                  DebugPrint.println(DebugPrint.INFO4, 
                                     "Converting [" + in + "] from [" + 
                                     ret + "] to [" + newret + "]");
               }
               ret = newret;
               
            } else {
               break;
            }
         }
      } catch(Throwable t) {
      }
      
      if (ret == null) ret = def;
      
      return ret;
   }
   
   public String findFileInOurDirectories(String fname) 
       throws IOException {

      return SearchEtc.findFileInDirectories(fname, servingDirectories);
   }
   
   public String findFileWhereEver(String fname) throws IOException {
      String ret = null;
      String dirs = servingDirectories;
      
      if (dirs != null) {
         ret = SearchEtc.findFileInDirectories(fname, dirs);
      }
      
      if (ret == null) {
         ret = SearchEtc.findFileInClasspath(fname);
      }
      
      return ret;
   }
}
   
