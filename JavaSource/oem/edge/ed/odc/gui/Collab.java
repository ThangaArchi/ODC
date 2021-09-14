package oem.edge.ed.odc.gui;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.odc.*;
import oem.edge.ed.odc.tunnel.common.*;
import com.ibm.as400.webaccess.common.*;

class XmedWatcher implements Runnable {
   private Collab collab;
   private Thread t;
   private Process p;
   private PrintWriter writer = null;
   private boolean inOut;
   public XmedWatcher(Collab c, boolean inOutV) {
      collab = c;
      inOut = inOutV;
      t = new Thread(this);
      t.start();
   }
   
   public void sendMeetingData(Hashtable hash) {
      
      if (writer == null) {
         writer = new PrintWriter(p.getOutputStream(), false);
      }
      
      StringBuffer sb = new StringBuffer();
      sb.append("\n{\n");
      for (Enumeration e = hash.keys() ; e.hasMoreElements() ;) {
         String k = (String)e.nextElement();
         sb.append(k + "=" + hash.get(k)).append("\n");
      }
      sb.append("}\n");
      DebugPrint.println("SendingMeetingData=> " + sb);
      writer.println(sb);
      writer.flush();
   }
   
   public void run() {
      try {
         String cmdarr[] = null;
         int idx = 0;
         String cmd = "xmed";
         int num = 0;
         String firstname, lastname, emailaddr;
         
        
        // firstname = collab.getFirstName();
        // lastname  = collab.getLastName();
         emailaddr = collab.getEmailAddr();
         
        // if (firstname != null) num+=2;
        // if (lastname  != null) num+=2;
         if (emailaddr != null) num+=2;
         
         if (inOut) {
            num += 13;
            cmdarr = new String[num];
            cmdarr[idx++] = "xmed";
            cmdarr[idx++] = "-security";
            cmdarr[idx++] = "-display";
            
            cmd += " -security -display ";
         } else {
            num += 12;
            cmdarr = new String[num];
            cmdarr[idx++] = "xmed";
            cmdarr[idx++] = "-mdisplay";
            cmd += " -mdisplay ";
         }
         
         cmdarr[idx++] = collab.getXMXDisplay();
         cmd += collab.getXMXDisplay();
        
         cmdarr[idx++] = "-user";
         cmdarr[idx++] = collab.getWhoIam();
         cmd += " -user '" + 
            collab.getWhoIam().replace('\'', '_');
                
         cmdarr[idx++] = "-compname";
         cmdarr[idx++] = collab.getCompany();
         cmd += "' -compname '" + 
                collab.getCompany().replace('\'', '_');
                
         cmdarr[idx++] = "-country";
         cmdarr[idx++] = collab.getCountry();
         cmd += "' -country '" + 
                collab.getCountry().replace('\'', '_');
                
         if (emailaddr != null) {
            cmdarr[idx++] = "-emailaddr";
            cmdarr[idx++] = emailaddr;
            cmd += "' -emailaddr '" + emailaddr.replace('\'', '_');
         }
         cmd += "'";
                       
         System.out.println("XmedWatcher: Running => " + cmd);
        //p = Runtime.getRuntime().exec(cmd);
         p = Runtime.getRuntime().exec(cmdarr);
      } catch (IOException e) {
         System.out.println("Error running xmed!");
         e.printStackTrace();
         Hashtable hash = new Hashtable();
         hash.put("COMMAND", "END");
         collab.receiveMeetingData(hash, this);
         //t.stop();
         return;
      }
      
      InputStream inp = p.getInputStream();
      BufferedReader rdr = new BufferedReader(new InputStreamReader(inp));
      int state = 0;
      Hashtable hash = null;
      int index;
      String line;
      try {
         DebugPrint.println("XmedWatcher: Top of loop before While");
         while((line = rdr.readLine()) != null) {
            line = line.trim();
            DebugPrint.println("Xmed: Read => " + line);
            if (state == 0) {
               if (line.equals("{")) {
                  state = 1;
                  hash = new Hashtable();
               } else {
                  System.out.println(
                     "XmedWatcher, throwing away garbage line: " + line);
               }
            } else if ((index = line.indexOf("=")) > 0) {
                  String key = line.substring(0, index).trim().toUpperCase();
                  String value = line.substring(index+1).trim();
                  hash.put(key, value);
            } else if (line.equals("}")) {
               state = 0;
               hash = collab.receiveMeetingData(hash, this);
               
              // If we get an answer ... send it back
               if (hash != null) {
                  sendMeetingData(hash);
               }
               
               hash = null;
            } else {
               System.out.println(
                  "XmedWatcher, throwing away garbage line: " + line);
            }
         }
      } catch(IOException e) {
         System.out.println("XmedWatcher: IOError");
      }
      
      DebugPrint.println("XmedWatcher: Returning");
      if (inOut) {
         collab.setMeetingStopped();
         DebugPrint.println("XmedWatcher: setRunningMeeting false");
      } else {
         DebugPrint.println("XmedWatcher: send END");
         hash = new Hashtable();
         hash.put("COMMAND", "END");
         collab.receiveMeetingData(hash, this);
      }
      //t.stop();
   }
}


public class Collab implements Runnable {
   private DatagramSocket dgramSock = null;
   private String ProgPath;
   private Thread t;
   private String remotehost;
   private String localport;
   private boolean writelocalport = true;
   private String host;
   final boolean DEBUG = true; // DEBUG
   private DesktopCommon common = null;
   private ProcessLauncher launcher = null;
   private boolean xmxStarted  = false;
   private boolean xmxStarting = false;
   private String xmxCookie;
   private String icaCookie;
   private String xmxPort;
   private String tempURL; 
   private String DisplayPort;
   private String ICAPort;   
   private String DesktopID;
   private String JDesktopID;
   private String aliasName;
   private String meetingTitle;
   private String inviteeName;
   private String newMeetingName = null;
   private String invitationName;
   private String invitationOwner;
   private String invitationID;
   private String invitationStart;
   private String invitationURLString;
   private String invitationBaseURL;
   private int invitationState;
   private String invitationSentTo;
   private String invitationCookie;
   private String invitationAlias;
   private String invitationDisplay;
   private String invitationCompany;
   private String invitationCountry;
   private String viewURLString;
   private String viewBaseURL;
   private String whoIam;
   private String localuser;
   private String company;
   private String mycountry;
   private String emailaddr;
   private String firstname;
   private String lastname;
   
   private String token = "-invalid-";
   
   private XmedWatcher inxmed = null;
   private XmedWatcher outxmed = null;
   private Hashtable invitationsHash = new Hashtable();
   
   private int    inviteID = 0;
        
  // JMC 11/15/00 
   private final java.lang.String ResourceName = "edesign_edodc_desktop";
   private java.util.PropertyResourceBundle AppProp = null;
        
   Vector invitations;

   public String getXMXDisplay() {
      return xmxPort;
   }
   
   public String getLastInvitee() { 
      return inviteeName;
   }
   
   public void timestamp(String s) {
      Date d = new Date();
      if (s != null) {
         System.out.println(d.toString() +": " + s);
      } else {
         System.out.println(d.toString());
      }
   }
   
   public void timestamp() {
      Date d = new Date();
      System.out.println(d.toString());
   }
   
   public Collab(String displayport, String icaport,
   
                 String desktopid, String host, 
                 String tempurl, String progpath) {
   
      timestamp("Starting");
      
     // Get application properties
      try {
         AppProp = (PropertyResourceBundle)
            PropertyResourceBundle.getBundle(ResourceName);
      } catch ( Exception e ) {
         System.out.println("Error reading ResourceBundle for " + 
                            ResourceName);
         e.printStackTrace();
      }
        
      this.ICAPort = icaport;
      this.DisplayPort = displayport;
            
      StringTokenizer st = new StringTokenizer(desktopid, "!");
      if (st.hasMoreTokens()) JDesktopID=DesktopID = st.nextToken();
      if (st.hasMoreTokens()) token = st.nextToken();
      if (st.hasMoreTokens()) {
         String lurl = st.nextToken();
         int idx = tempurl.indexOf("://");
         if (idx > 0 && lurl.indexOf("://") > 0) {
            idx = tempurl.indexOf("/", idx+3);
            if (idx >= 0) {
               System.out.print("Replace URL [" + tempurl + "] with [");
               tempurl = lurl + tempurl.substring(idx);
               System.out.println(tempurl + "]");
            }
         }
      }
      
      if (token == null) token = "-invalid-";

      this.host = host;
      this.tempURL = tempurl;
      this.ProgPath = progpath;
        
      t = new Thread(this);
      
      timestamp("Doing WHOAMI");
        
      try {
         URL url = 
            new URL(DebugPrint.urlRewrite(tempURL + common.WHOAMI_CMD, 
                                          JDesktopID));
         HttpMessage msg = new HttpMessage(url);
         Properties props = new Properties();
         props.put(common.DESKTOP_ID, DesktopID); // DesktopID
         props.put(common.TOKENID, token);
      
         InputStream in = msg.sendGetMessage(props);
         ConfigObject cf = getINI(in);
      
         localuser = System.getProperty("user.name");
         if (cf == null) {
            System.out.println("ConfigFile is null");
            logout();
         } else {
            whoIam  = cf.getProperty(common.NAME);
            company = cf.getProperty(common.COMPANY);
            mycountry = cf.getProperty(common.COUNTRY);
            emailaddr = cf.getProperty(common.EMAILADDR);
            firstname = cf.getProperty(common.FIRST);
            lastname  = cf.getProperty(common.LAST);
            timestamp("******  WhoamI response"
                               + "\n\tEdgeId  = " + whoIam
                               + "\n\tCountry = " + mycountry
                               + "\n\tCompany = " + company 
                               + "\n\temail   = " + emailaddr
                               + "\n\tFirst   = " + firstname
                               + "\n\tLast    = " + lastname);
            if (whoIam == null) {
               whoIam = localuser;
            }
            if (company == null) {
               company = "unknown";
            }
            if (mycountry == null) {
               mycountry = "unknown";
            }
         }
      } catch(Exception e) {}
        
      makeAlias();
        
     // The DesktopCommon class contains all of the constants 
     //   used in the ini file and comman names used to call the servlet. 
      common = new DesktopCommon();
     // The ProcessLauncher class contains the interfaces for the 
     // native afs calls.
      launcher = new ProcessLauncher();

     // Set the ProgPath
      if (ProgPath != null) {
         launcher.setProgPath(ProgPath);
         DebugPrint.println("Setting the ProgPath");
      }

     // get ica Cookie///////////////////////////////////////////
      int displayCtr = 0;
      displayCtr = 0;
      timestamp("before ica Cookie loop");
      while (icaCookie == null && displayCtr < 3) {
         System.out.println("in ica Cookie loop");
         icaCookie = launcher.getCookie(host, whoIam, getDisplayPort());
         if (icaCookie == null) {
            displayCtr++;
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ie) {
            }
         } else {
            System.out.println("displayCnt   " + displayCtr);
            System.out.println("icaCookie   " + icaCookie);
            break;
         }
      }
      
      if (icaCookie == null) {
         System.out.println("Exiting because there is no ica Cookie");
         logout();
      }
        
      timestamp("Got all cookies ... start outside Xmed");
      outxmed = new XmedWatcher(this, false);
        
      timestamp("Create Initial Meeting");
      createInitialMeeting();
      
      timestamp("Start main processing");
      t.start();
   }

   public void makeAlias() {
     //Construct the aliasName
      long mills = System.currentTimeMillis();
      Long amills = new Long(mills);
      String x = amills.toString(mills);
      aliasName = "meeting" + whoIam + "_" + x;
      newMeetingName = aliasName;
   }
   
   
   public void doAcceptReject(boolean v, Invitation invitation) {
      DebugPrint.println("reject response sent to servlet");
      try {
         URL url = 
            new URL(DebugPrint.urlRewrite(getURL() + 
                                          (v?common.ACCEPT_INVITATION_CMD:common.REJECT_INVITATION_CMD), 
                                          getJDesktopID()));
         HttpMessage msg = new HttpMessage(url);
         Properties props = new Properties();
         props.put(common.DESKTOP_ID, getDesktopID()); 
         props.put(common.ID, invitation.getId());
         props.put(common.TOKENID, token);
         msg.sendGetMessage(props);
         DebugPrint.println("URL" + url);
      } catch (Exception e) {
      }
   }
   
   public void unbindMeeting() {
      try {
         URL url = 
            new URL(DebugPrint.urlRewrite(getURL() + common.END_XMX_CMD,
                                          getJDesktopID()));
         HttpMessage msg = new HttpMessage(url);
         Properties props = new Properties();
         props.put(common.DESKTOP_ID, getDesktopID()); 
         props.put(common.TOKENID, token);
         msg.sendGetMessage(props);
         DebugPrint.println("URL" + url);
      } catch (Exception e) {
      }
   }
   
   public void setMeetingStopped() {
      unbindMeeting();
      xmxStarted = false;
      inxmed = null;
   }
   
private boolean startMeeting() {   
   timestamp("About to launch meeting");
   
   synchronized (this) {
      if (xmxStarted || xmxStarting) {
         System.out.println("Zoinks, asked to start meeting, and its already going/Starting!");
         return false;
      }
   
      xmxStarting = true;
   }
   
   makeAlias();
        
  // Start a meeting
   launcher.startMeeting(host, whoIam, getDisplayPort(), aliasName);
   
   timestamp("Meeting launched. Get Display");
   
  // get the Display///////////////////////////////////////////
      int displayCtr = 0;
      xmxPort=null;
      while (xmxPort == null && displayCtr < 45) {
         xmxPort = launcher.getDisplay(host, whoIam, aliasName);
         timestamp("getDisplay try number " + displayCtr);
         if (xmxPort == null) {
            displayCtr++;
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ie) {
            }
         } else {
            System.out.println("displayCnt   " + displayCtr);
            System.out.println("xmxPort   " + xmxPort);
            break;
         }
      }

      if (xmxPort == null) {
         System.out.println("Error starting XMX!");
         xmxStarting = false;
         return false;
      }
        
     // get xmx Cookie///////////////////////////////////////////
      displayCtr = 0;
      timestamp("before xmx Cookie loop");
      xmxCookie = null;
      while (xmxCookie == null && displayCtr < 10) {
         System.out.println("in xmx Cookie loop");
         xmxCookie = launcher.getCookie(host, whoIam, xmxPort);
         if (xmxCookie == null) {
            displayCtr++;
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ie) {
            }
         } else {
            System.out.println("displayCnt   " + displayCtr);
            System.out.println("xmxCookie   " + xmxCookie);
            break;
         }
      }
      if (xmxCookie == null) {
         System.out.println("Couldn't get XMX cookie!");
         xmxStarting = false;
         return false;
      }
      
      timestamp("Waiting for XMX start to register ...");
      int trycnt = 30;
      int idx = xmxPort.indexOf(":");
      String hostS = xmxPort.substring(0, idx);
      String portS = xmxPort.substring(idx+1);
      int xmxport=-1;
      try {
         xmxport = Integer.parseInt(portS);
      } catch(NumberFormatException e) {
      }
      xmxport += 6000;
      while(--trycnt > 0) {
         System.out.println("Testing for host " + 
                            hostS + " port " + xmxport + " Date "
                            + (new Date()).toString());
         try {
            Socket s = new Socket(host, xmxport);
            System.out.println("Connection worked!");
            break;
         } catch (IOException e) {
            ;
         } catch (Throwable e) {
            break;
         }
         
         try {
            Thread.sleep(1000);
         } catch (InterruptedException ie) {
         }
      }      
      
      inxmed  = new XmedWatcher(this, true);
      
      xmxStarted = true;
      xmxStarting = false;
      return true;
   }

  // Called by XmedWatcher when a full correspondence arrives
   public Hashtable receiveMeetingData(Hashtable hash, XmedWatcher www) {
      String cmd = (String)hash.get("COMMAND");
      Hashtable rethash = null;
      if (cmd == null) {
         System.out.println("Huh? Go no command from Xmed??");
      } else if (cmd.equalsIgnoreCase("END")) {
         DebugPrint.println("Got END command!");
         logout();
      } else if (cmd.equalsIgnoreCase("FOROTHER")) {
         if (www != null) {
            if (www == inxmed)       www = outxmed;
            else if (www == outxmed) www = inxmed;
            else                     www = null;
            if (www != null) {
               www.sendMeetingData(hash);
            }
         }
      } else if (cmd.equalsIgnoreCase("STARTMEETING")) {
         boolean ret = startMeeting();
         rethash = new Hashtable();
         if (!ret) {
            if (xmxStarted || xmxStarting) {
               rethash.put("COMMAND" , "ERRORMSG");
               rethash.put("MESSAGE" , "Meeting is already running/started");
            } else {
               rethash.put("COMMAND" , "ERRORMSG");
               rethash.put("MESSAGE" , "Sorry, error starting meeting software!");
            }
         } else {
            rethash.put("COMMAND" , "HOOKUP");
            rethash.put("MDISPLAY", getXMXDisplay());
            createMeeting();
            
            Hashtable ht = new Hashtable();
            ht.put("COMMAND", "SETCLASSTITLE");
            ht.put("CLASSIFICATION", hash.get("CLASSIFICATION"));
            ht.put("MEETINGNAME", hash.get("MEETINGNAME"));
            sendMeetingData(ht);
         }
         
      } else if (cmd.equalsIgnoreCase("SET_MEETINGNAME")) {
        // If in or out sets new title, send it back (to both)
         String meet = (String)hash.get("MEETINGNAME");
         if (meet != null) {
            sendMeetingData(hash);
         }
      } else if (cmd.equalsIgnoreCase(common.QUERY_IBM_CMD) ||
                 cmd.equalsIgnoreCase(common.QUERY_SAME_COMP_CMD)) {
         try {
            URL url = 
               new URL(DebugPrint.urlRewrite(getURL() + cmd, getJDesktopID()));
            HttpMessage msg = new HttpMessage(url);
            Properties props = new Properties();
            String qid   = (String)hash.remove("QUESTIONID");
            String udata = (String)hash.remove("UDATA");
            props.put(common.DESKTOP_ID, getDesktopID()); 
            props.put(common.NAME,       (String)hash.get("NAME"));
            props.put(common.TOKENID, token);
            InputStream in = msg.sendGetMessage(props);
            ConfigObject cf = getINI(in);
            
            if (cf == null) {
               System.out.println("ConfigFile is null");
               logout();
            } else {
               String ans   = cf.getProperty("ANSWER");
               String ibmer = cf.getProperty("IBMER");
               String country = cf.getProperty("COUNTRY");
               String allowpart = cf.getProperty("ALLOWPARTICIPATE");
               String allowinvite = cf.getProperty("ALLOWINVITE");
               rethash = new Hashtable();
               rethash.put("COMMAND",    "ANSWER");
               rethash.put("ANSWER",     (ans   != null)? ans  :"INVALID");
               rethash.put("IBMER",      (ibmer != null)? ibmer:"INVALID");
               rethash.put("QUESTIONID", (qid   != null)? qid  :"INVALID");
               rethash.put("IMANIBMER",  (company.equalsIgnoreCase("ibm")?"TRUE":"FALSE"));
               rethash.put("UDATA",      (udata  != null)? udata :"INVALID");
               rethash.put("COUNTRY",    (country != null)? country :"INVALID");
               rethash.put("ALLOWPARTICIPATE", (allowpart != null)? allowpart :"FALSE");
               rethash.put("ALLOWINVITE", (allowinvite != null)? allowinvite :"FALSE");
               rethash.put("COUNTRY",    (country != null)? country :"INVALID");
            }
         } catch (Exception e) {
         }
      } else if (cmd.equalsIgnoreCase("INVITEE_DROPPED")) {
         inviteeName = (String)hash.get("INVITEE");
         sendInviteeDropped(inviteeName);
      } else if (cmd.equalsIgnoreCase("NEW_INVITATION")) {
         inviteeName = (String)hash.get("INVITEE");
         String ts = (String)hash.get("MEETINGNAME");
         if (ts != null) {
            newMeetingName = ts;
         }
         sendInvitation();
      } else if (cmd.equalsIgnoreCase("ACCEPTED_INVITATION") ||
                 cmd.equalsIgnoreCase("REJECTED_INVITATION")) {
         String id = (String)hash.get("INVITATIONID");
         if (id == null) {
            System.out.println("No ID for accepted/decline invite!!\n");
         } else {
            Invitation invitation = (Invitation)invitationsHash.get(id);
            if (invitation != null) {
               invitationsHash.remove(id);
               if (cmd.equalsIgnoreCase("ACCEPTED_INVITATION")) {
                  launcher.addSession(getHost(), getWhoIam(), getDisplayPort(),
                                      getICACookie(), invitation.getCookie(), 
                                      invitation.getDisplay());
                  doAcceptReject(true, invitation);
               } else {
                  doAcceptReject(false, invitation);
               }
            } else {
               System.out.println("No invitation for ID from accept/decline: \n" + id);
            }
         }
      }
      return rethash;
   }

   public String getCompany()   { return company;   }
   public String getCountry()   { return mycountry; }
   public String getFirstName() { return firstname; }
   public String getLastName()  { return lastname;  }
   public String getEmailAddr() { return emailaddr; }
   
  // Called to send commands to xmed
   public void sendMeetingData(Hashtable hash) {
      if (inxmed != null) inxmed.sendMeetingData(hash);
      outxmed.sendMeetingData(hash);
   }
   
   public void logout() {
      
      IOException ex = new IOException("Logging out");
      ex.fillInStackTrace();
      System.out.print("Collab:Logout: Calltrace = ");
      ex.printStackTrace(System.out);
   
      if (xmxStarted) {
         System.out.println("Ending Meeting ");
         launcher.endMeeting(host, whoIam, xmxPort + " " + aliasName);
      }
      try {
         URL url = 
            new URL(DebugPrint.urlRewrite(tempURL + common.END_MEETING_CMD,
                                          JDesktopID));
         HttpMessage msg = new HttpMessage(url);
         Properties props = new Properties();
         props.put(common.DESKTOP_ID, DesktopID); // DesktopID
         props.put(common.TOKENID, token);
         msg.sendGetMessage(props);
      } catch (MalformedURLException ee) {
         System.out.println(
            "MalformedURLException thrown in actionPerformed:" + 
            tempURL.toString());
         ee.printStackTrace();
      } catch (IOException eee) {
         System.out.println("IOException thrown while sending END_MEETING");
         eee.printStackTrace(System.out);
      }
     // t.stop();
      System.exit(0);
   }
   
   public void addUserRequest(String meetID, String userID) {
      DebugPrint.println("send an invitation to a meeting");
     //String temp = meetingBaseURL + "/" + INVITE;
      try {
         URL url = new URL(DebugPrint.urlRewrite(tempURL, getJDesktopID()));
         HttpMessage msg = new HttpMessage(url);
         Properties props = new Properties();
         props.put("meeting", meetID);
         props.put("user", userID);
         props.put(common.TOKENID, token);
         msg.sendGetMessage(props);
      } catch (Exception e) {
      };
   }

   public void createInitialMeeting() {

      DebugPrint.println("initial call to servlet when a meeting is created");
      try {
        // Used by DesktopServlet to bump us on the head.
        // Set timeout to 5 seconds as a fallback (polling still), incase the
        // dgram is lost. We will need to build retry functionality to get
        // rid of this entirely ... well, change that to once a minute.
        // TODO! Have servlet engine resend bonk until answer is forthcoming
         dgramSock = new DatagramSocket();
         dgramSock.setSoTimeout(60000);
         
         URL url = 
            new URL(DebugPrint.urlRewrite(tempURL + common.BIND_CMD,
                                          JDesktopID));
         HttpMessage msg = new HttpMessage(url);
         Properties props = new Properties();
         props.put(common.DESKTOP_ID, DesktopID); // DesktopID
         props.put(common.OWNER,     localuser); // User Name
         props.put(common.EDGEOWNER, whoIam);    // Edge User Name
         props.put(common.COOKIE, "none");
         props.put(common.DISPLAY, "none:99");
         props.put(common.ALIAS, aliasName);
         props.put(common.TOKENID, token);
         if (dgramSock != null) {
            InetAddress inet1 = dgramSock.getLocalAddress();
            InetAddress inet2 = InetAddress.getLocalHost();
            System.out.println("DGramBump Addr = " + inet1.toString());
            System.out.println("LocalHost Addr = " + inet2.toString());
            props.put(common.BUMPHOST, inet2.getHostAddress());
            props.put(common.BUMPPORT, ""+dgramSock.getLocalPort());
         }
         
         InputStream in = msg.sendGetMessage(props);
         ConfigObject cf = getINI(in);
         
         if (cf == null) {
            System.out.println("ConfigFile is null");
            logout();
         } else {
            DebugPrint.println("Reading DesktopID");
            DesktopID = cf.getProperty(common.DESKTOP_ID);
            tempURL = cf.getProperty(common.URL);
            DebugPrint.println("******  New URL = " + tempURL);
            DebugPrint.println("******  NEW DESKTOP_ID = " + DesktopID);
         }
         
         DebugPrint.println("URL in create meeting" + url);
      } catch (MalformedURLException e) {
         System.out.println("MalformedURLException thrown in create Meeting:" + tempURL.toString());
      } catch (IOException e) {
         System.out.println("IOException thrown in create Meeting");
      }
   }

   public void createMeeting() {

      DebugPrint.println("create a meeting");
      try {
         
         URL url = 
            new URL(DebugPrint.urlRewrite(tempURL + common.START_XMX_CMD,
                                          JDesktopID));
         HttpMessage msg = new HttpMessage(url);
         Properties props = new Properties();
         props.put(common.DESKTOP_ID, DesktopID); // DesktopID
         props.put(common.COOKIE, xmxCookie!=null?xmxCookie:"none");
         props.put(common.DISPLAY, xmxPort);
         props.put(common.ALIAS, aliasName);
         props.put(common.TOKENID, token);
         
         InputStream in = msg.sendGetMessage(props);
         ConfigObject cf = getINI(in);
         
         if (cf == null) {
            System.out.println("ConfigFile is null");
            logout();
         }
         
         DebugPrint.println("URL in create meeting" + url);
      } catch (MalformedURLException e) {
         System.out.println("MalformedURLException thrown in create Meeting:" + tempURL.toString());
      } catch (IOException e) {
         System.out.println("IOException thrown in create Meeting");
      }
   }
   
   public void createInvitation(String name, String owner, String start, 
                                String ID, int state, String sentto, 
                                String cookie, String alias, 
                                String display, String comp, String country) {
      Invitation invitation = new Invitation(name, owner, ID, start, 
                                             state, sentto, cookie, 
                                             alias, display);
      
      invitations.addElement(invitation);
      Hashtable hash = new Hashtable();
      if (state == DesktopCommon.PENDING) {
         invitationsHash.put("" + ID, invitation);
         
         hash.put("COMMAND", "NEW_INVITATION");
         hash.put("OWNER",   invitation.getMeetingHost());
         hash.put("INVITATIONID", ID);
         hash.put("MEETINGNAME", name);
         if (comp != null) {
            hash.put("COMPANY",     comp);
         }
         if (country != null) {
            hash.put("COUNTRY", country);
         }
         outxmed.sendMeetingData(hash);
         return;
      }
      if (state == DesktopCommon.ACCEPTED) {
         hash.put("COMMAND", "ACCEPTED_INVITATION");
         hash.put("OWNER",    invitation.getMeetingHost());
         hash.put("INVITED",  invitation.getSentTo());
      } else if (state == DesktopCommon.REJECTED) {
         hash.put("COMMAND", "REJECTED_INVITATION");
         hash.put("OWNER",    invitation.getMeetingHost());
         hash.put("INVITED",  invitation.getSentTo());
      } else if (state == DesktopCommon.OFFLINE) {
         hash.put("COMMAND", "OFFLINE_INVITATION");
         hash.put("OWNER",    invitation.getMeetingHost());
         hash.put("INVITED",  invitation.getSentTo());
      }
      
      sendMeetingData(hash);
   }


   public void createURL(String urlString) {
      try {
         DebugPrint.println("IN Create URL");
         DebugPrint.println("URL String " + urlString);
      } catch (Exception e) {
      }
   }

   public void shutdown() {
      logout();
      System.exit(1);
   }
   
   public InputStream doPoll() {
      InputStream in = null;
      try {
         URL url = 
            new URL(DebugPrint.urlRewrite(tempURL + common.REFRESH_CMD, 
                                          JDesktopID));
         HttpMessage msg = new HttpMessage(url);
         Properties props = new Properties();
         System.out.println("doPoll - ID = " + DesktopID + " token = " + token);
         props.put(common.DESKTOP_ID, DesktopID); // DesktopID
         props.put(common.TOKENID, token);
		
         in = msg.sendGetMessage(props);
      } catch (MalformedURLException e) {
         System.out.println("MalformedURLException thrown in doPoll :" + tempURL.toString());
      } catch (IOException e) {
         System.out.println("IOException thrown in doPoll");
      }
      return in;
   }

   public String getDesktopID() {
      return DesktopID;
   }
   public String getJDesktopID() {
      return JDesktopID;
   }

   public String getDisplayPort() {
      return DisplayPort;
   }

   public String getHost() {
      return host;
   }
        
   public String getICACookie() {
      return icaCookie;
   }

   public String getICAPort() {
      return ICAPort;
   }

   
   public ConfigObject getINI(InputStream in) {
      ConfigFile ret = null;
      try {
         ret = new ConfigFile(in);
         if (ret != null) {
           // Always check for Token refresh
            String ltoken = ret.getProperty(common.NEWTOKEN);
            if (ltoken != null) token = ltoken;
         }
      } catch(Throwable t) {}
      return ret;
   }
            
   
  // This code assumes REFRESH cmd!
   public void handleRefresh(InputStream in) {
      String sectionName;
      Vector v;
      Vector vv;
      Vector w;
      Vector ww;
      Vector x;
      Vector xx;
      invitations = new Vector();
      try {
         ConfigObject cfile = getINI(in);
         if (cfile == null) {
            System.out.println("ConfigObject is null");
            shutdown();
         }
         
         localport = cfile.getProperty(common.DISPLAY);
         remotehost = cfile.getProperty(common.R_HOST);
         
         String refreshError = cfile.getProperty("REFRESH_ERROR");
         if (refreshError != null) {
            System.out.println("Refresh error received from host: " + 
                               refreshError + " ... Shutdown!");
            shutdown();
         }
         
         String exok = cfile.getProperty("EXECUTION");
         if (exok == null || !exok.equalsIgnoreCase("ok")) {
            System.out.println("ExecutionOK not correct ... shutdown!");
            shutdown();
         }
         
         if (remotehost == null) {
            remotehost = "localhost";
         }
                
         String ltoken = cfile.getProperty(common.NEWTOKEN);
         if (ltoken != null) {
            token = token;
         }
                
         if (localport == null) writelocalport = false;
         else writelocalport = true;
         if (writelocalport) {
            writelocalport = false;
            Hashtable h = new Hashtable();
            h.put("COMMAND", "BROWSERPORT");
            h.put("BROWSERPORT", localport);
            h.put("BROWSERHOST", remotehost);
            sendMeetingData(h);
         }
         
         int j = 0;
         x = cfile.getSection(common.INVITATIONS);
         if (x != null) {
            for (int i = 0; i < x.size(); i++) {
               ConfigSection section = (ConfigSection) x.elementAt(i);
               invitationURLString = section.getProperty("url");
               DebugPrint.println("Invitation URL: " + invitationURLString);
               xx = section.getSection(common.INVITATION);
               for (j = 0; j < xx.size(); j++) {
                  ConfigSection sec = (ConfigSection) xx.elementAt(j);
                  invitationName = sec.getProperty(common.NAME);
                  invitationOwner = sec.getProperty(common.EDGEOWNER);
                  if (invitationOwner == null) {
                     invitationOwner = sec.getProperty(common.OWNER);
                  }
                  invitationID = sec.getProperty(common.ID);
                  invitationStart = sec.getProperty(common.START);
                  invitationState = sec.getIntProperty(common.STATUS, 
                                                       common.PENDING);
                  invitationSentTo = sec.getProperty(common.INVITEE);
                  invitationCookie = sec.getProperty(common.COOKIE);
                  invitationAlias = sec.getProperty(common.ALIAS);
                  invitationDisplay = sec.getProperty(common.DISPLAY);
                  invitationCompany = sec.getProperty(common.COMPANY);
                  invitationCountry = sec.getProperty(common.COUNTRY);
                  
                  if (DebugPrint.getLevel() != 0) {
                     DebugPrint.println("xx.size " + xx.size());
                     DebugPrint.println("sec:  " + sec);
                     DebugPrint.println("Invite Name:  " + invitationName);
                     DebugPrint.println("Invite Owner:  " + invitationOwner);
                     DebugPrint.println("Invite ID:  " + invitationID);
                     DebugPrint.println("Invite Start:  " + invitationStart);
                     DebugPrint.println("Invite State:  " + invitationState);
                     DebugPrint.println("Invite Send To:  " + invitationSentTo);
                     DebugPrint.println("Invite Cookie:  " + invitationCookie);
                     DebugPrint.println("Invite Alias:  " + invitationAlias);
                     DebugPrint.println("Invite Display:  " + invitationDisplay);
                     DebugPrint.println("Invite Company:  " + invitationCompany);
                     DebugPrint.println("Invite Country:  " + invitationCountry);
                  }
                  
                  createInvitation(invitationName, invitationOwner, invitationStart, invitationID, invitationState, invitationSentTo, invitationCookie, invitationAlias, invitationDisplay, invitationCompany, invitationCountry);
               }
            }
         }
      } catch (Exception e) {
      }
   }
   public ProcessLauncher getLauncher() {
      return launcher;
   }
   
   public String getURL() {
      return tempURL; 
   }
   public String getWhoIam() {
      return whoIam;
   }
   public static void main(String[] args) {
      String DisplayPort = null;
      String ICAPort = null;
      String DesktopID = null;
      String Host = null;
      String TempURL = null;
      String ProgPath = null;
        
     // Levelset Log4j
      DebugPrint.setLevel(DebugPrint.INFO);
         
      try {
         com.ibm.net.www.https.SecureGlue.setKeyRing("EDesignKeyring", "0x120163");
      } catch(ClassNotFoundException e) {
         System.out.println("CollaborationMgr: Yikes ... Keyring not found!");
      }
        
      if (args.length % 2 != 0) {
         System.out.println("Exiting due to incorrect parameter specification");
         System.exit(1);
      }
      for (int i = 0; i < args.length; i++) {
         if (args[i].equals("+DISPLAYPORT")) {
            DisplayPort = args[i + 1];
            if (DisplayPort.substring(0, 1).equals("+")) {
               System.out.println("Exiting because there are two adjacent parameters");
               System.exit(1);
            }
         }
         if (args[i].equals("+ICAPORT")) {
            ICAPort = args[i + 1];
            if (ICAPort.substring(0, 1).equals("+")) {
               System.out.println("Exiting because there are two adjacent parameters");
               System.exit(1);
            }
         }
         if (args[i].equals("+DESKTOPID")) {
            DesktopID = args[i + 1];
            if (DesktopID.substring(0, 1).equals("+")) {
               System.out.println("Exiting because there are two adjacent parameters");
               System.exit(1);
            }
         }
         if (args[i].equals("+HOST")) {
            Host = args[i + 1];
            if (Host.substring(0, 1).equals("+")) {
               System.out.println("Exiting because there are two adjacent parameters");
               System.exit(1);
            }
         }
         
         if (args[i].equalsIgnoreCase("+DEBUG")) {
            try {
               String s = args[i+1];
               int v = Integer.parseInt(s);
               DebugPrint.setLevel(v);
            } catch(NumberFormatException e) {
              System.out.println("Debug option has invalid value " + args[i+1]);
            }
         }
         if (args[i].equals("+URL")) {
            TempURL = args[i + 1];
            if (TempURL.substring(0, 1).equals("+")) {
               System.out.println("Exiting because there are two adjacent parameters");
               System.exit(1);
            }
         }
         if (args[i].equals("+PROGPATH")) {
            ProgPath = args[i + 1];
            if (ProgPath.substring(0, 1).equals("+")) {
               System.out.println("Exiting because there are two adjacent parameters");
               System.exit(1);
            }
         }
      }
      System.out.println("DisplayPort  " + DisplayPort);
      System.out.println("ICAPort " + ICAPort);
      System.out.println("DesktopID  " + DesktopID);
      System.out.println("Host  " + Host);
      System.out.println("TempURL  " + TempURL);
      System.out.println("ProgPath  " + ProgPath);
      if (DisplayPort == null) {
         System.out.println("Exiting because the Display Port is null");
         System.exit(1);
      }
      if (ICAPort == null) {
         System.out.println("Exiting because the ICA Port is null");
         System.exit(1);
      }
      if (DesktopID == null) {
         System.out.println("Exiting because the DesktopID is null");
         System.exit(1);
      }
      if (Host == null) {
         System.out.println("Exiting because the Host is null");
         System.exit(1);
      }
      if (TempURL == null) {
         System.out.println("Exiting because the TempURL is null");
         System.exit(1);
      }
      if (ProgPath == null) {
         System.out.println("Program Path not specified use the default");
      }
      new Collab(DisplayPort, ICAPort, DesktopID, Host, TempURL, ProgPath);
   }

   public void run() {
      DatagramPacket dp = new DatagramPacket(new byte[128], 128);
      while (true) {
      
         InputStream in = doPoll();
      
         handleRefresh(in);
      
        /* JMC 11/10/00 - Go back to true polling! Doing the wait
           in the servlet was just too costly resource
           wise (looks like 50 concurrent servlets was
           the max), so just check for new stuff 
           every so often
                       
           Also, we were not correctly cleaning up 
           the servlet when the Meeting Manager went
           down. Just punt.
        */
        
        /* JMC 05/07/01 - Well, polling stinks ... try using
                          datagrams to have WS bump us on the head to 
                          wake us up ... telling us to connect. The 
                          timeout time on the dgramSock will be the
                          polling interval, which is Infinite (YEAH)
        */
      
         try {
            if (dgramSock != null) {
               dgramSock.receive(dp);
            } else {
               Thread.sleep(5000);
            }
         } catch (Exception e ) {
            ;
         }
      
      }
   }

   public String getNonIBMPrefix() {
      String ret = null;
      try {
         ret = AppProp.getString("edodc.nonIBMPrefix");
      } catch(Throwable t) {
      }
      return ret;
   }
   
   public String getDenyCustToCust() {
      String ret = null;
      try {
         ret = AppProp.getString("edodc.denyTwoNonIBM");
      } catch(Throwable t) {
      }
      return ret;
   }
   
   public void sendInvitation() {
        
      String s = getNonIBMPrefix();
      if (s == null) {
         System.out.println("Error accessing nonIBMPrefix, just complete invite");
      }
      sendInvitationI();
   }

   public void sendInvitationI() {
        
   
      DebugPrint.println("Invitation Sent TO: " + inviteeName + "\n");
   
      try {
         URL url = 
            new URL(DebugPrint.urlRewrite(tempURL+common.SEND_INVITATION_CMD, 
                                          JDesktopID));
         HttpMessage msg = new HttpMessage(url);
         Properties props = new Properties();
         props.put(common.DESKTOP_ID, DesktopID); // DesktopID
         props.put(common.NAME, newMeetingName); // Meeting Title
         props.put(common.INVITEE, inviteeName); // Person being Invited
         props.put(common.TOKENID, token);
         msg.sendGetMessage(props);
         DebugPrint.println("URL" + url);
      } catch (MalformedURLException e) {
         System.out.println("MalformedURLException thrown");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public void sendInviteeDropped(String invitee) {
        
   
      DebugPrint.println("Invitee DROP: " + inviteeName + "\n");
   
      try {
         URL url = new URL(
               DebugPrint.urlRewrite(tempURL+common.DROPPED_PARTICIPANT_CMD,
                                     JDesktopID));
         HttpMessage msg = new HttpMessage(url);
         Properties props = new Properties();
         props.put(common.DESKTOP_ID, DesktopID); // DesktopID
         props.put(common.INVITEE, inviteeName);  // Person being dropped
         props.put(common.TOKENID, token);
         msg.sendGetMessage(props);
         DebugPrint.println("URL" + url);
      } catch (MalformedURLException e) {
         System.out.println("MalformedURLException thrown");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
}
