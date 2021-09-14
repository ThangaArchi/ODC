package oem.edge.ed.odc.util;

import java.net.*;
import java.util.*;
import java.io.*;

import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.tunnel.common.*;
import oem.edge.ed.util.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004,2005,2006                           */
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


public class MappingFiles {

   private static String odcResMapName = "edesign_edodc_mapping.properties";
   private static String dropResMapName = "edesign_edxfr_mapping.properties";
   private static String gridResMapName = "edesign_edfdr_mapping.properties";
   private static String odcPResMapName = "edesign_edodc_participate_mapping.properties";
   private static String odcIResMapName = "edesign_edodc_invite_mapping.properties";
   private static String dshResName = "edesign_eddsh_hmapping.properties";
   private static String eduResName = "edesign_class_hmapping.properties";

   ReloadingProperty deskprops = null;
   
   public static String emapInitFile     = null;
   public static long   emapLastModified = 0;
   public static ConfigObject eduMapProp = null;
   
   public static String hmapInitFile     = null;
   public static long   hmapLastModified = 0;
   public static ConfigObject dshMapProp = null;
   
   public static String omapInitFile     = null;
   public static long   omapLastModified = 0;
   public static ConfigObject odcMapProp = null;
   
   public static String dmapInitFile      = null;
   public static long   dmapLastModified  = 0;
   public static ConfigObject dropMapProp = null;
   
   public static String gmapInitFile      = null;
   public static long   gmapLastModified  = 0;
   public static ConfigObject gridMapProp = null;
   
   public static String pmapInitFile      = null;
   public static long   pmapLastModified  = 0;
   public static ConfigObject odcPMapProp = null;
   
   public static String imapInitFile     = null;
   public static long   imapLastModified = 0;
   public static ConfigObject odcIMapProp = null;
   
   public MappingFiles(ReloadingProperty props) {
      super();
      
      deskprops = props;
      
     // Initialize mapping file location
      String v ;
      
      if (deskprops != null) {
         v = deskprops.getProperty("eddsh.hmappingFile");
         if (v != null) dshResName     = v;
         v = deskprops.getProperty("ededu.emappingFile");
         if (v != null) eduResName     = v;
         v = deskprops.getProperty("edodc.omappingFile");
         if (v != null) odcResMapName  = v;
         v = deskprops.getProperty("edodc.dmappingFile");
         if (v != null) dropResMapName = v;
         v = deskprops.getProperty("edodc.pmappingFile");
         if (v != null) odcPResMapName = v;
         v = deskprops.getProperty("edodc.imappingFile");
         if (v != null) odcIResMapName = v;
         v = deskprops.getProperty("edodc.gmappingFile");
         if (v != null) gridResMapName = v;
      }
   }
   
   public void overrideMapping(String mappingName, String override) {
      if (override == null) return;
   
      if        (override.equalsIgnoreCase("eddsh.hmappingFile")) {
         dshResName     = override;
      } else if (override.equalsIgnoreCase("ededu.emappingFile")) {
         eduResName     = override;
      } else if (override.equalsIgnoreCase("edodc.omappingFile")) {
         odcResMapName  = override;
      } else if (override.equalsIgnoreCase("edodc.dmappingFile")) {
         dropResMapName = override;
      } else if (override.equalsIgnoreCase("edodc.pmappingFile")) {
         odcPResMapName = override;
      } else if (override.equalsIgnoreCase("edodc.imappingFile")) {
         odcIResMapName = override;
      } else if (override.equalsIgnoreCase("edodc.gmappingFile")) {
         gridResMapName = override;
      }
   }   
   
   public ConfigObject getODCMappingFile() {
      try {
         if (omapInitFile == null || DebugPrint.doDebug()) {
            omapLastModified = (long)0;
            omapInitFile = findFileWhereEver(odcResMapName);
            DebugPrint.println(DebugPrint.INFO4, 
                               "ODCMapping file found at path " + 
                               omapInitFile);
         }
         File omapFile = new File(omapInitFile);
         if (!omapFile.exists()) throw new Exception("Does not exist");
         if (omapFile.lastModified() > omapLastModified) {
            ConfigFile cf = new ConfigFile(omapInitFile);
            omapLastModified = omapFile.lastModified();
            
           // Flatten the company sections
            Enumeration enum = cf.getSectionNames();
            while(enum != null && enum.hasMoreElements()) {
               String k = (String)enum.nextElement();
               Vector secvec = cf.getSection(k);
               if (secvec != null) {
                  Enumeration secenum = secvec.elements();
                  while(secenum.hasMoreElements()) {
                     ConfigSection csect=(ConfigSection)secenum.nextElement();
                     Enumeration names = csect.getPropertyNames();
                     while(names.hasMoreElements()) {
                        String n = (String)names.nextElement();
                        String p = csect.getProperty(n);
                        String cbu = k + "!" + n;
                        if (cf.getProperty(cbu) == null) {
                           cf.setProperty(cbu, p);
                        }
                     }
                  }
               }
            }
            odcMapProp = cf;
         }
      } catch ( Exception e ) {
         DebugPrint.printlnd(DebugPrint.ERROR, "getODCMapping: Error while loading omapFile[" 
                             + omapInitFile + "] " + e.toString());
        // DebugPrint.println(DebugPrint.ERROR, e);
         odcMapProp = null;
         omapInitFile = null;
         omapLastModified = (long)0;
      }

      return odcMapProp;
   }
   
   public ConfigObject getDropboxMappingFile() {
      try {
         if (dmapInitFile == null || DebugPrint.doDebug()) {
            dmapLastModified = (long)0;
            dmapInitFile = findFileWhereEver(dropResMapName);
            DebugPrint.println(DebugPrint.INFO4, 
                               "DropboxMapping file found at path " + 
                               dmapInitFile);
         }
         File dmapFile = new File(dmapInitFile);
         if (!dmapFile.exists()) throw new Exception("Does not exist");
         if (dmapFile.lastModified() > dmapLastModified) {
            ConfigFile cf = new ConfigFile(dmapInitFile);
            dmapLastModified = dmapFile.lastModified();
            
           // Flatten the company sections
            Enumeration enum = cf.getSectionNames();
            while(enum != null && enum.hasMoreElements()) {
               String k = (String)enum.nextElement();
               Vector secvec = cf.getSection(k);
               if (secvec != null) {
                  Enumeration secenum = secvec.elements();
                  while(secenum.hasMoreElements()) {
                     ConfigSection csect=(ConfigSection)secenum.nextElement();
                     Enumeration names = csect.getPropertyNames();
                     while(names.hasMoreElements()) {
                        String n = (String)names.nextElement();
                        String p = csect.getProperty(n);
                        String cbu = k + "!" + n;
                        if (cf.getProperty(cbu) == null) {
                           cf.setProperty(cbu, p);
                        }
                     }
                  }
               }
            }
            dropMapProp = cf;
         }
      } catch ( Exception e ) {
         DebugPrint.printlnd(DebugPrint.ERROR, "getDropboxMapping: Error while loading dmapFile[" 
                             + dmapInitFile + "] " + e.toString());
        // DebugPrint.println(DebugPrint.ERROR, e);
         dropMapProp = null;
         dmapLastModified = (long)0;
         dmapInitFile = null;
         
      }

      return dropMapProp;
   }
   
   public ConfigObject getGridMappingFile() {
      try {
         if (gmapInitFile == null || DebugPrint.doDebug()) {
            gmapLastModified = (long)0;
            gmapInitFile = findFileWhereEver(gridResMapName);
            DebugPrint.println(DebugPrint.INFO4, 
                               "GridMapping file found at path " + 
                               gmapInitFile);
         }
         File gmapFile = new File(gmapInitFile);
         if (!gmapFile.exists()) throw new Exception("Does not exist");
         if (gmapFile.lastModified() > gmapLastModified) {
            ConfigFile cf = new ConfigFile(gmapInitFile);
            gmapLastModified = gmapFile.lastModified();
            
           // Flatten the company sections
            Enumeration enum = cf.getSectionNames();
            while(enum != null && enum.hasMoreElements()) {
               String k = (String)enum.nextElement();
               Vector secvec = cf.getSection(k);
               if (secvec != null) {
                  Enumeration secenum = secvec.elements();
                  while(secenum.hasMoreElements()) {
                     ConfigSection csect=(ConfigSection)secenum.nextElement();
                     Enumeration names = csect.getPropertyNames();
                     while(names.hasMoreElements()) {
                        String n = (String)names.nextElement();
                        String p = csect.getProperty(n);
                        String cbu = k + "!" + n;
                        if (cf.getProperty(cbu) == null) {
                           cf.setProperty(cbu, p);
                        }
                     }
                  }
               }
            }
            gridMapProp = cf;
         }
      } catch ( Exception e ) {
         DebugPrint.printlnd(DebugPrint.ERROR, "getGridMapping: Error while loading gmapFile[" 
                             + gmapInitFile + "] " + e.toString());
        // DebugPrint.println(DebugPrint.ERROR, e);
         gridMapProp = null;
         gmapLastModified = (long)0;
         gmapInitFile = null;
         
      }

      return gridMapProp;
   }
   
   public ConfigObject getODCParticipateMappingFile() {
      try {
         if (pmapInitFile == null || DebugPrint.doDebug()) {
            pmapLastModified = (long)0;
            pmapInitFile = findFileWhereEver(odcPResMapName);
            DebugPrint.println(DebugPrint.INFO4, 
                               "ODCParticipateMapping file found at path " + 
                               pmapInitFile);
         }
         File opmapFile = new File(pmapInitFile);
         if (!opmapFile.exists()) throw new Exception("Does not exist");
         if (opmapFile.lastModified() > pmapLastModified) {
            odcPMapProp = new ConfigFile(pmapInitFile);
            pmapLastModified = opmapFile.lastModified();
         }
      } catch ( Exception e ) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "getODCParticipateMapping: Error while loading mapFile[" 
                             + pmapInitFile + "] " + e.toString());
         odcPMapProp = null;
         pmapLastModified = (long)0;         
         pmapInitFile = null;
      }

      return odcPMapProp;
   }
   
   public ConfigObject getODCInviteMappingFile() {
      try {
         if (imapInitFile == null || DebugPrint.doDebug()) {
            imapLastModified = (long)0;
            imapInitFile = findFileWhereEver(odcIResMapName);
            DebugPrint.println(DebugPrint.INFO4, 
                               "ODCInviteMapping file found at path " + 
                               imapInitFile);
         }
         File imapFile = new File(imapInitFile);
         if (!imapFile.exists()) throw new Exception("Does not exist");
         if (imapFile.lastModified() > imapLastModified) {
            odcIMapProp = new ConfigFile(imapInitFile);
            imapLastModified = imapFile.lastModified();
         }
      } catch ( Exception e ) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "getODCInviteMapping: Error while loading mapFile[" 
                             + imapInitFile + "] " + e.toString());
         odcIMapProp      = null;
         imapLastModified = (long)0;
         imapInitFile     = null;
      }

      return odcIMapProp;
   }
   
   public ConfigObject getHostMappingFile() {
      try {
         if (hmapInitFile == null || DebugPrint.doDebug()) {
            hmapInitFile = findFileWhereEver(dshResName);
            hmapLastModified = (long)0;
            DebugPrint.println(DebugPrint.INFO4, 
                               "Hosting file found at path " + hmapInitFile);
         }
         File hmapFile = new File(hmapInitFile);
         if (!hmapFile.exists()) throw new Exception("Does not exist");
         if (hmapFile.lastModified() > hmapLastModified) {
            dshMapProp = new ConfigFile(hmapInitFile);
            hmapLastModified = hmapFile.lastModified();
         }
      } catch ( Exception e ) {
         DebugPrint.printlnd(DebugPrint.ERROR, "getDSHostMapping: Error while loading hmapFile[" 
                             + hmapInitFile + "]");
         DebugPrint.println(DebugPrint.ERROR, e);
         dshMapProp = null;
         hmapLastModified = (long)0;
         hmapInitFile = null;
      }

      return dshMapProp;
   }
   
   public ConfigObject getClassroomMappingFile() {
      try {
         if (emapInitFile == null || DebugPrint.doDebug()) {
            emapLastModified = (long)0;
            emapInitFile = findFileWhereEver(eduResName);
            DebugPrint.println(DebugPrint.INFO4, 
                               "Classroom file found at path " + emapInitFile);
         }
         File emapFile = new File(emapInitFile);
         if (!emapFile.exists()) throw new Exception("Does not exist");
         if (emapFile.lastModified() > emapLastModified) {
            eduMapProp = new ConfigFile(emapInitFile);
            emapLastModified = emapFile.lastModified();
         }
      } catch ( Exception e ) {
         DebugPrint.printlnd(DebugPrint.ERROR, "getClassroomMapping: Error while loading emapFile[" 
                             + emapInitFile + "]");
         DebugPrint.println(DebugPrint.ERROR, e);
         eduMapProp = null;
         emapLastModified = (long)0;
         emapInitFile     = null;
      }

      return eduMapProp;
   }
   
   private boolean addHEElement(int iteration, Vector retvec,
                                String val, Vector scopev, String defmap) { 
      boolean ret = false;
      if (val == null) return false;
      if (iteration > 20) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "DesctopServlet:addHEElement - Iteration=" +
                             iteration +  " Must have a cycle!");
         return false;
      }
      
      if (val.equals("*") || val.length() == 0) {
         val = defmap;
         if (val == null) val = "*";
      }
      
      if (val.startsWith("$")) {
         for (int i = scopev.size()-1; i >= 0 && !ret; i--) {
            ConfigObject co = (ConfigObject)scopev.elementAt(i);
            String ts = co.getProperty(val);
            ret |= addHEElement(iteration+1, retvec, ts, scopev, defmap);
            ret |= addHEMachineSection(iteration+1, retvec,
                                       co.getSection(val), scopev,
                                       defmap);
         }
      } else {
         retvec.addElement(val);
         ret = true;
      }
      return ret;
   }
   
   private boolean addHEMachineSection(int iteration, Vector retvec, 
                                       Vector vals, Vector scopev, 
                                       String defmap) { 
      boolean ret = false;
      if (vals.size() == 0) return false;
      if (iteration > 20) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "DesctopServlet:addHEElement - Iteration=" +
                             iteration +  " Must have a cycle!");
         return false;
      }
      
      Enumeration e = vals.elements();
      while(e.hasMoreElements()) {
         ConfigObject co = (ConfigObject)e.nextElement();
         scopev.addElement(co);
         String ts; 
         for(int i=1; (ts=co.getProperty(""+i)) != null; i++) {
            ret |= addHEElement(1, retvec, ts, scopev, defmap);
         }
         scopev.removeElementAt(scopev.size()-1);
      }
      
      return ret;
   }
   
   public Vector getHostingMachines(String uname, String company) {
      ConfigObject rco = getHostMappingFile();
      Vector ret = new Vector();
      String defmap = "$DEFAULT_MAPPING";
      if (rco != null) {
         Vector scopev = new Vector();
         scopev.addElement(rco);
         
        // Add company!user and company in the root CO
         String ts = rco.getProperty(company + "!" + uname);
         addHEElement(1, ret, ts, scopev, defmap);
         ts = rco.getProperty(company);
         addHEElement(1, ret, ts, scopev, defmap);
         
        // Wildcard
         ts = rco.getProperty("*");
         addHEElement(1, ret, ts, scopev, defmap);
         
        // Add any sections for company!user
         addHEMachineSection(1, ret, rco.getSection(company + "!" + uname), 
                             scopev, defmap);
         
        // Traverse Company Section if exists
         Vector cco = rco.getSection(company);
         if (cco.size() > 0) {
            Enumeration e = cco.elements();
            while(e.hasMoreElements()) {
               ConfigObject yco = (ConfigObject)e.nextElement();
               scopev.addElement(yco);
               ts = yco.getProperty(uname);
               addHEElement(1, ret, ts, scopev, defmap);
               
               ts = yco.getProperty("*");
               addHEElement(1, ret, ts, scopev, defmap);
               
              // Add any sections for user
               addHEMachineSection(1, ret, yco.getSection(uname), 
                                   scopev, defmap);
               scopev.removeElementAt(scopev.size()-1);
               
              // Add any sections for "*"
               addHEMachineSection(1, ret, yco.getSection("*"), 
                                   scopev, defmap);
               scopev.removeElementAt(scopev.size()-1);
            }
         }
         
        // Traverse * Section if exists
         cco = rco.getSection("*");
         if (cco.size() > 0) {
            Enumeration e = cco.elements();
            while(e.hasMoreElements()) {
               ConfigObject yco = (ConfigObject)e.nextElement();
               scopev.addElement(yco);
               ts = yco.getProperty(uname);
               addHEElement(1, ret, ts, scopev, defmap);
               
               ts = yco.getProperty("*");
               addHEElement(1, ret, ts, scopev, defmap);
               
              // Add any sections for user
               addHEMachineSection(1, ret, yco.getSection(uname), 
                                   scopev, defmap);
               scopev.removeElementAt(scopev.size()-1);
               
              // Add any sections for "*"
               addHEMachineSection(1, ret, yco.getSection("*"), 
                                   scopev, defmap);
               scopev.removeElementAt(scopev.size()-1);
            }
         }
      }
      if (ret != null) {
         SearchEtc.sortVector(ret, true);
      }
      return ret;
   }

   public String getEducationMachine(String classroom, String uname, 
                                     String company) {
      ConfigObject rco = getClassroomMappingFile();
      
      if (company == null) company = "null"; 
      company = company.trim();
      
     // genret used for general matches. User specific matches go in ret
     // If there are NO ret entries, then genret will be returned.
      Vector ret    = new Vector();
      Vector genret = new Vector();
      String defmap = "$" + classroom + "_MACHINE";
      if (rco != null) {
      
         Vector scopev = new Vector();
         scopev.addElement(rco);
         
        // Add class!company!user in the root CO
         String ts = rco.getProperty(classroom + "!" + company + "!" + uname);
         addHEElement(1, ret, ts, scopev, defmap);
         
        // Add class!company in the root CO
         ts = rco.getProperty(classroom + "!" + company);
         addHEElement(1, genret, ts, scopev, defmap);
         
        // Add class!company in the root CO
         ts = rco.getProperty(classroom + "!" + company + "!*");
         addHEElement(1, genret, ts, scopev, defmap);
                  
        // Add class in the root CO
         ts = rco.getProperty(classroom);
         addHEElement(1, genret, ts, scopev, defmap);
                  
        // Add class in the root CO
         ts = rco.getProperty(classroom+"!*!*");
         addHEElement(1, genret, ts, scopev, defmap);
                  
        // Traverse classroom!company Section if exists
         Vector vec = rco.getSection(classroom + "!" + company);
         if (vec.size() > 0) {
            Enumeration e = vec.elements();
            while(e.hasMoreElements()) {
               ConfigObject yco = (ConfigObject)e.nextElement();
               scopev.addElement(yco);
               
               ts = yco.getProperty(uname);
               addHEElement(1, ret, ts, scopev, defmap);
               
               ts = yco.getProperty("*");
               addHEElement(1, genret, ts, scopev, defmap);
               
              // Add any section for user
               addHEMachineSection(1, ret, yco.getSection(uname), 
                                   scopev, defmap);
                                   
              // Add any section for "*"
               addHEMachineSection(1, genret, yco.getSection("*"), 
                                   scopev, defmap);
                                   
               scopev.removeElementAt(scopev.size()-1);
            }
         }
         
        // Traverse Class Section if exists
         vec = rco.getSection(classroom);
         if (vec.size() > 0) {
            Enumeration e = vec.elements();
            while(e.hasMoreElements()) {
               ConfigObject yco = (ConfigObject)e.nextElement();
               scopev.addElement(yco);
               
              // Add company!uname and company
               ts = yco.getProperty(company + "!" + uname);
               addHEElement(1, ret, ts, scopev, defmap);
               ts = yco.getProperty(company);
               addHEElement(1, genret, ts, scopev, defmap);
               ts = yco.getProperty("*");
               addHEElement(1, genret, ts, scopev, defmap);
               ts = yco.getProperty("*!*");
               addHEElement(1, genret, ts, scopev, defmap);
               
              // Add any section for user
               addHEMachineSection(1, ret, yco.getSection(company+"!"+uname), 
                                   scopev, defmap);
                                   
              // Add any section for company!*
               addHEMachineSection(1, genret, yco.getSection(company+"!*"), 
                                   scopev, defmap);
                                   
              // Add any section for *!*
               addHEMachineSection(1, genret, yco.getSection("*!*"), 
                                   scopev, defmap);
                                   
              // Traverse company Section if exists
               vec = yco.getSection(company);
               if (vec.size() > 0) {
                  Enumeration et = vec.elements();
                  while(et.hasMoreElements()) {
                     ConfigObject tyco = (ConfigObject)et.nextElement();
                     scopev.addElement(tyco);
                     
                    // Add user mapping
                     ts = tyco.getProperty(uname);
                     addHEElement(1, ret, ts, scopev, defmap);
                     
                    // Add any section for user
                     addHEMachineSection(1, ret, tyco.getSection(uname), 
                                         scopev, defmap);
                     scopev.removeElementAt(scopev.size()-1);
                  }
               }
               
              // Traverse "*" Section if exists
               vec = yco.getSection("*");
               if (vec.size() > 0) {
                  Enumeration et = vec.elements();
                  while(et.hasMoreElements()) {
                     ConfigObject tyco = (ConfigObject)et.nextElement();
                     scopev.addElement(tyco);
                     
                    // Add user mapping
                     ts = tyco.getProperty(uname);
                     addHEElement(1, genret, ts, scopev, defmap);
                     
                     ts = tyco.getProperty("*");
                     addHEElement(1, genret, ts, scopev, defmap);
                     
                    // Add any section for user
                     addHEMachineSection(1, genret, tyco.getSection(uname), 
                                         scopev, defmap);
                                         
                     addHEMachineSection(1, genret, tyco.getSection("*"), 
                                         scopev, defmap);
                                         
                     scopev.removeElementAt(scopev.size()-1);
                  }
               }
               
               scopev.removeElementAt(scopev.size()-1);
            }
         }
      }
      
      if (ret.size() == 0) ret = genret;
      
      String strret = null;
      if (ret.size() > 0) {
         SearchEtc.sortVector(ret, true);
         strret = (String)ret.elementAt(0);
         if (ret.size() > 1) {
            DebugPrint.printlnd(DebugPrint.WARN, 
                                "Multiple Class mappings exist for " +
                                classroom + "!" + company + "!" + uname);
            for(int j=0; j < ret.size(); j++) {
               DebugPrint.println("\t" + (String)ret.elementAt(j));
            }
         }
      }
      return strret;
   }
   
   public boolean enabledForODC(String uname, String company) {
      ConfigObject rco = getODCMappingFile();
      
      if (rco != null) {
      
         if (company == null) company = ""; 
         company = company.trim();
         
        /* Look for higher precedence first */
         String ts;
         
         ts = rco.getProperty(company + "!" + uname);
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty("*!" + uname);
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty(company + "!*");
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty(company);
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty("*!*");
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty("*");
         if (ts != null) return ts.equalsIgnoreCase("true");
      }
      
      return false;
   }
   
   public boolean enabledForDropbox(String uname, String company) {
      ConfigObject rco = getDropboxMappingFile();
      
      if (rco != null) {
      
         if (company == null) company = ""; 
         company = company.trim();
         
        /* Look for higher precedence first */
         String ts;
         
         ts = rco.getProperty(company + "!" + uname);
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty("*!" + uname);
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty(company + "!*");
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty(company);
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty("*!*");
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty("*");
         if (ts != null) return ts.equalsIgnoreCase("true");
      }
      
      return false;
   }
   
   public boolean enabledForGrid(String uname, String company) {
      ConfigObject rco = getGridMappingFile();
      
      if (rco != null) {
      
         if (company == null) company = ""; 
         company = company.trim();
         
        /* Look for higher precedence first */
         String ts;
         
         ts = rco.getProperty(company + "!" + uname);
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty("*!" + uname);
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty(company + "!*");
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty(company);
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty("*!*");
         if (ts != null) return ts.equalsIgnoreCase("true");
         
         ts = rco.getProperty("*");
         if (ts != null) return ts.equalsIgnoreCase("true");
      }
      
      return false;
   }
   
   
  // Can srccomp user give participate access to dstcomp user
   public boolean allowParticipateAccess(String srccomp, String dstcomp) {
      ConfigObject rco = getODCParticipateMappingFile();
      
      DebugPrint.println(DebugPrint.INFO3, "AllowPartAccess: [" + 
                                           srccomp + "] [" + dstcomp + "]");
      
      int rule = 1;
      String rulestr = "1";
      
      if (srccomp == null) srccomp = ""; 
      if (dstcomp == null) dstcomp = ""; 
      srccomp = srccomp.trim();
      dstcomp = dstcomp.trim();
      
      if (rco != null) {
         rulestr = rco.getProperty(srccomp);
         if (rulestr == null && !srccomp.equals("")) {
            rulestr = rco.getProperty("*");
         }
         
         DebugPrint.println(DebugPrint.INFO3, "Rulestr = " + rulestr);
         if (rulestr != null) {
            try { 
               rule = Integer.parseInt(rulestr);
               if (rule < 0 || rule > 4) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                      "allowParticpateAccess: Failure for [" +
                                      srccomp + 
                                      "] specified int rule was " + rule);
                  rule = 0;
               }
            } catch (NumberFormatException ne) {
               if        (rulestr.equalsIgnoreCase("NEVER")) {
                  rule = 0;
               } else if (rulestr.equalsIgnoreCase("COMPANYONLY")) {
                  rule = 1;
               } else if (rulestr.equalsIgnoreCase("IBMONLY")) {
                  rule = 2;
               } else if (rulestr.equalsIgnoreCase("BOTH")) {
                  rule = 3;
               } else if (rulestr.equalsIgnoreCase("ALWAYS")) {
                  rule = 4;
               } else if (rulestr.indexOf(':') == 0) { 
                  rule = 5;
               } else {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                      "allowParticpateAccess: Failure for [" +
                                      srccomp + 
                                      "] specified str rule was " + 
                                      rulestr);
                  rule = 0;
               }
            }
         }
      }
      
     // Rule == 0 :  NEVER allow participate with anyone
     // Rule == 1 :  ALLOW participate for same Company only
     // Rule == 2 :  ALLOW participate for IBM only
     // Rule == 3 :  ALLOW participate for same company && IBM
     // Rule == 4 :  ALWAYS ALLOW participate ... Dangerous ... just in case
     // Rule == 5 :  Explicit company mapping
      if (rule < 0 || rule > 5) rule = 0;
      
      boolean ret = false;
      switch(rule) {
      
         case 0:
            break;
            
         case 1:
            if (srccomp.equalsIgnoreCase(dstcomp) && 
                !srccomp.equals("")) {
               ret = true;
            }
            break;
            
         case 2:
            if (dstcomp.equalsIgnoreCase("IBM")) {
               ret = true;
            }
            break;
            
         case 3:
            if ((srccomp.equalsIgnoreCase(dstcomp) && 
                 !srccomp.equals("")) ||
                dstcomp.equalsIgnoreCase("IBM")) {
               ret = true;
            }
            break;
            
         case 4:
            ret = true;
            break;
            
         case 5: {
            StringTokenizer st = new StringTokenizer(rulestr, ":");
            if (st.hasMoreTokens()) st.nextToken(); // Eat first "" token
            while(st.hasMoreTokens()) {
               String p1 = st.nextToken();
               if (p1.equals("*") || 
               
                   (dstcomp.length() > 0 && 
                    ((p1.equals("@") && srccomp.equalsIgnoreCase(dstcomp)) ||
                     p1.equalsIgnoreCase(dstcomp)))) {
                    
                  ret = true;
                  break;
               }
            }
            break;
         }
            
         default:
            break;
      }
         
      DebugPrint.println(DebugPrint.INFO3, "Returning " + ret + 
                                           " Rule = " + rule);
      return ret;
   }
   
  // Can srccomp user invite dstcomp user to meeting
  //
  //  File fmt    company[!user] = company[!user]:...
  //
  //    If user not specified, considered to match all users from that company
  //
   public boolean allowCompanyInvite(String srccomp, String srcuser, 
                                     String dstcomp, String dstuser) {
                                            
      try {
         if (srccomp == null) srccomp = "";
         if (dstcomp == null) dstcomp = "";
         srccomp = srccomp.trim();
         dstcomp = dstcomp.trim();
         srcuser = srcuser.trim();
         dstuser = dstuser.trim();
      } catch(NullPointerException npe) {
         return false;
      }
      
      ConfigObject rco = getODCInviteMappingFile();
      
      DebugPrint.println(DebugPrint.INFO3, 
                         "AllowCompanyInvite: [" + srccomp + "!" + srcuser + 
                         "] [" + dstcomp + "!" + dstuser + "]");
      
     // If no RCO file, then assume default rules for invite
      if (rco == null) {
         if (srccomp.equalsIgnoreCase("IBM") || 
             dstcomp.equalsIgnoreCase("IBM") ||
             (srccomp.equalsIgnoreCase(dstcomp) && dstcomp.length() > 0)) {
             
            DebugPrint.println(DebugPrint.INFO3, 
                               "No invite file ... passes default rules");
            return true;
         } else {
            DebugPrint.println(DebugPrint.INFO3, 
                               "No invite file ... violates default rules");
            return false;
         }
         
      } else {
         
        /*
        ** Possible matches in file:  Precedence from top to bottom
        ** 
        **    company!user           - explicit
        **    *!user                 - just user
        **    company && company!*   - just company (company has precedence)
        **    * and *!*              - default rule (* has precedence)
        **
        ** What an entry can contain ... if any match, its allowed
        ** 
        **    * && *!*               - Anyone can be invited
        **    company!user           - explicit
        **    company && company!*   - dstcompany has to match
        **    *!user                 - only user has to match
        **    @!user                 - src/dst companies match && user check
        **    @                      - src/dst companies have to match
        **
        */
        
        // If this user has a record, use it
         String p = rco.getProperty(srccomp+"!"+srcuser);
         if (p == null) p = rco.getProperty("*!" + srcuser);
         if (p == null) p = rco.getProperty(srccomp);
         if (p == null) p = rco.getProperty(srccomp+"!*");
         if (p == null) p = rco.getProperty("*");
         if (p == null) p = rco.getProperty("*!*");
         
         if (p != null) {
            StringTokenizer st = new StringTokenizer(p.trim(), ":");
            while(st.hasMoreTokens()) {
               String p1 = st.nextToken().trim();
               
               if (p1.equals("*") || p1.equals("*!*")         ||
                   
                  (dstcomp.length() > 0 && (
                     p1.equalsIgnoreCase(dstcomp+"!"+dstuser) ||
                     p1.equalsIgnoreCase(dstcomp)             ||
                     p1.equalsIgnoreCase(dstcomp+"!*")        ||

                     ((p1.equals("@") || p1.equals("@!*")) && 
                      srccomp.equalsIgnoreCase(dstcomp))      || 

                     (p1.equalsIgnoreCase("@!" + dstuser) && 
                      srccomp.equalsIgnoreCase(dstcomp))))    ||

                   p1.equalsIgnoreCase("*!"+dstuser)) {
                  
                  DebugPrint.println(DebugPrint.INFO3, 
                                     "Match explicit rule = " + p);
                  return true;
               }
            }
         } else if (srccomp.equalsIgnoreCase("IBM") || 
                    dstcomp.equalsIgnoreCase("IBM") ||
                    (srccomp.equalsIgnoreCase(dstcomp) && dstcomp.length()>0)){
            DebugPrint.println(DebugPrint.INFO3, 
                               "No explicit rule, but passes default rule");
            return true;
         }
      }
      DebugPrint.println(DebugPrint.INFO3, 
                         "No appropriate rules found ... return false");
      return false;
   }
   
  // Test pgm for class and hosting files
   public static void main(String args[]) {
      boolean doClass = args.length > 1 && args[0].equalsIgnoreCase("class");
      boolean doPart  = args.length > 1 && args[0].equalsIgnoreCase("participate");
      boolean doOdc   = args.length > 1 && args[0].equalsIgnoreCase("odc");
      
      ReloadingProperty props = new ReloadingProperty();
      MappingFiles mytt = new MappingFiles(props);
      
      for(int i=1; i < args.length; i++) {
         if (doClass) {
            String classname = args[i++];
            String company   = args[i++];
            String user      = args[i];
            System.out.println("Looking up class[" + classname + "]" +
                               " company["         + company   + "]" +
                               " user["            + user      + "]");
            String mach = mytt.getEducationMachine(classname, user, company);
            System.out.println("Answer is " + mach);
         } else if (doPart) {
            String c1 = args[i++];
            String c2 = args[i++];
            System.out.println("Participate access for Meeting Comp[" + 
                               c1 + "] Invite Comp[" + c2 + "] = " + 
                               mytt.allowParticipateAccess(c1, c2));
         } else if (doOdc) {
            String u = args[i++];
            String c = args[i++];
            System.out.println("Check ODC access for User[" + 
                               u + "] Company[" + c + "] = " + 
                               mytt.enabledForODC(u, c));
         } else {
            String company   = args[i++];
            String user      = args[i];
            System.out.println("Looking up company[" + company + "]" +
                               " user["              + user    + "]");
            Vector v = mytt.getHostingMachines(user, company);
            if (v == null || v.size() == 0) {
               System.out.println("No matching machines!");
            } else {
               Enumeration e = v.elements();
               while(e.hasMoreElements()) {
                  String mach = (String)e.nextElement();
                  System.out.println("\t" +  mach);
               }
            }
         }
      }
   }
   
   
   public String findFileWhereEver(String fname) 
      throws IOException {
      
      String dirpath = null;
      if (deskprops != null) dirpath = deskprops.getServingDirectories();
      
      return SearchEtc.findFileInDirectoriesThenClasspath(fname, dirpath);
   }
}
