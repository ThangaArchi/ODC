package oem.edge.ed.odc.util;

import oem.edge.ed.util.*;
import org.apache.log4j.Logger;

import oem.edge.ed.odc.dsmp.server.DboxAlert;

public class FSAuthentication {

   private static Logger log = Logger.getLogger(FSAuthentication.class.getName());
   
   private String klog     = "/usr/afsws/bin/klog";
   private String gsalogin = "/bin/gsa_login";
   
   private String  fsUserid   = null;
   private String  fsPassword = null;
   private String  fsCell     = null;
   private String  fsPWDir    = null;
   
   private long    fsTimeout  = 12*60*60*1000;
   
   private boolean loud = false;
   
   public FSAuthentication(String cell, String pwDir) {
      fsCell = cell;
      fsPWDir = pwDir;
   }
   
   public FSAuthentication(String cell, String pwDir, long ms) {
      fsCell = cell;
      fsPWDir = pwDir;
      fsTimeout = ms;
   }
   
   public void setLoud(boolean l) { loud = l; }
   
   public void setTimeoutMS(long milliseconds) {
      fsTimeout = milliseconds;
   }
   
   public void setTimeout(long seconds) {
      fsTimeout = seconds * 1000;
   }
   
   public String getCell()         { return fsCell; }
   public void   setCell(String v) { fsCell = v;    }
   
   public String getKlog()         { return klog; }
   public void   setKlog(String v) { klog = v;    }
   
   public String getGSA()         { return gsalogin; }
   public void   setGSA(String v) { gsalogin = v;    }
   
   public String getPWDir()         { return fsPWDir; }
   public void   setPWDir(String v) { fsPWDir = v;    }
   
   public long getTimeoutMS() { return fsTimeout; }
   
   public boolean reauthenticate() {
      boolean ret = false;
      try {
      
        // Reread the password/userid
         String lUserid   = PasswordUtils.getPassword(fsPWDir+"/._afsd435");
         String lPassword = PasswordUtils.getPassword(fsPWDir+"/._afsde7e");
         
         if (lUserid != null) fsUserid = lUserid;
         if (lPassword != null) fsPassword = lPassword;
                            
         if (loud) {
           log.info("Authenticating to " + fsCell + " as " + fsUserid);
         }
         
         String lcell = fsCell;
         
        // Figure out if we are doing gsa or afs. Support explicit typing by adding
        //  :afs or :gsa to end of cellname OR implicit ... if cell ends in letters
        //  gsa, then we go with that, otherwise AFS.
         boolean dogsa = false;
         int cidx = fsCell.lastIndexOf(":");
         if (cidx > 0) {
            lcell = fsCell.substring(0, cidx);
            String ctype = fsCell.substring(cidx).toLowerCase();
            if (ctype.equals(":gsa")) {
               dogsa = true;
            } else if (!ctype.equals(":afs")) {
               throw new Exception("Unknown cell type ... Error authenticating cell " + 
                                   fsCell);
            }
         } else {
            int idx = fsCell.lastIndexOf("gsa");
            if (idx > 0 && idx == fsCell.length()-3) {
               dogsa = true;
            }
         }
         
         String args[];
         
         if (dogsa) {
            args = new String[] {
               gsalogin, "-c", lcell, "-p", fsUserid
            };
         } else {
            args = new String[] {
               klog, fsUserid, "-cell", lcell, "-pipe"
            };
         }
            
         Process p = Runtime.getRuntime().exec(args);
         java.io.OutputStream os = p.getOutputStream();
         os.write((fsPassword+"\n").getBytes());
         os.close();
         int rc = p.waitFor();
         if (rc != 0) {
            throw new Exception("Error code from " + 
                                (dogsa?gsalogin:klog) + 
                                " = " + rc);
         }
         
         ret = true;
         if (loud) {
            log.info("Token/Ticket obtained at " + (new java.util.Date()).toString());
         }
      } catch(Exception ee) {
         
         DboxAlert.alert(1, "Unable to authenticate to FileSystem", 0,
                         "Attempt to authenticate to Filesystem failed"
                         + "\ncell         = " +  fsCell   
                         + "\npwdir        = " +  fsPWDir  
                         + "\nfsUserid     = " +  fsUserid 
                         + "\npasswd null? = " + (fsPassword == null) 
                         + "\npasswd len   = " 
                         + ((fsPassword == null)?0:fsPassword.length()), ee);
      }
      return ret;
   }
   
   
   class FSTimeout extends Timeout {
      long interval;
      long errinterval;
      public FSTimeout(long delta, long error, boolean err) {
         super(err?error:delta, "filesystem_timeout", null);
         interval    = delta;
         errinterval = error;
      }
      
      public void tl_process(Timeout to) {
         
        // Add timeout for specified delta to do status update
         TimeoutManager tmgr = TimeoutManager.getGlobalManager();
         tmgr.removeTimeout("filesystem_timeout");
         
         if (!reauthenticate()) {
            if (loud) {
               log.info("Failed ... Waiting for Error timeout of " + 
                        (errinterval/1000) + " seconds");
            }
            tmgr.addTimeout(new FSTimeout(interval, errinterval, true));
         } else {
            if (loud) {
               log.info("Success ... Add in timeout of " + 
                        (interval/1000) + " seconds");
            }
            tmgr.addTimeout(new FSTimeout(interval, errinterval, false));
         }
      }
   }
   
   public void schedule() {
      schedule(false);
   }
   public void schedule(boolean failed) {
     // Each fsTimeout millisefcs, try to get new token, 
     //  if error, try every 1/10 of full wait
      TimeoutManager tmgr = TimeoutManager.getGlobalManager();
      tmgr.removeTimeout("filesystem_timeout");
      long t = fsTimeout/10;
      if (t < 1000) t = 1000;
      tmgr.addTimeout(new FSTimeout(fsTimeout, t, failed));
   }
   
   public void unschedule() {
     // Add timeout for specified delta to do status update
      TimeoutManager tmgr = TimeoutManager.getGlobalManager();
      tmgr.removeTimeout("filesystem_timeout");
   }
   
   static public void main(String args[]) {
      long fstimeout = 0;
      String cell  = null;
      String pwdir = null;
      
      for(int i=0; i < args.length; i++) {
         if (args[i].equalsIgnoreCase("-fsTimeoutSec")) {
            
            try {
              // Timeout is number of secs
               fstimeout = Long.parseLong(args[++i]) * 1000;
            } catch (NumberFormatException ne) {
               System.err.println("Bad format for fsTimeout ... Ignoring");
            }
         } else if (args[i].equalsIgnoreCase("-fsTimeoutMin")) {
            
            try {
              // Timeout is number of Mins
               fstimeout = Long.parseLong(args[++i]) * 60 * 1000;
            } catch (NumberFormatException ne) {
               System.err.println("Bad format for fsTimeout ... Ignore");
            }
         } else if (args[i].equalsIgnoreCase("-fsTimeout")) {
            
            try {
              // Timeout is number of Hours
               fstimeout = Long.parseLong(args[++i]) * 60 * 60 *1000;
            } catch (NumberFormatException ne) {
               System.err.println("Bad format for fsTimeout ... Ignoring");
            }
         } else if (args[i].equalsIgnoreCase("-fsinfo")) {
            
            cell = args[++i];
            pwdir = args[++i];
            
         }
      }
      
      FSAuthentication auth = new FSAuthentication(cell, pwdir);
      auth.setLoud(true);
      if (fstimeout > 0) auth.setTimeoutMS(fstimeout);
      auth.schedule(!auth.reauthenticate());
      
     // Sleep for 10 min
      try {
         log.info("Sleep for 10 minute so you can 'observe'");
         Thread.sleep(1000*60*10);
      } catch(Exception ee) {};
   }
}
