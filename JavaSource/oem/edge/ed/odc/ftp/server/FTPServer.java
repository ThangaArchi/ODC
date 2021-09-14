package oem.edge.ed.odc.ftp.server;

import  oem.edge.ed.odc.dsmp.common.AreaContent;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.util.*;
import  oem.edge.common.cipher.*;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.reflect.*;

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

/*
** Note: We are using the HandlerID to be the LoginID for now
**       Also, the ParticipantID is also == to LoginID. This is enough as
**       long as the meetingID is always presented at the same time. It is.
**
** Note1: Add timeout to incoming connections, disconnect if not logged in for
**        5 minutes (some value). If lots of errors ... disconnect, etc.
*/

public class FTPServer extends FTPDispatchBase implements Runnable {

//   java.lang.reflect.Method dataFromToken = null;
   Object                   cipher        = null;
   Object                   edgecipher    = null;
   String                   userpwFile    = null;
   long                userpwLastModified = 0;
   
   static final        int MAX_PROTO_OUT  = 3;
   
   protected boolean        daemonMode    = false;
   
   public boolean getDaemonMode() { return daemonMode; }
   
   
   
   class DownloadOperation extends SendFileOperation 
                           implements ProtoSentListener{
      
      protected int numout           = 0;
      protected long totalconfirmed  = 0;
      
      public DownloadOperation(DSMPBaseHandler handler, int id, 
                               long totToXfer, long ofs, InputStream iis) {
         super(handler, id, totToXfer, ofs, iis);
      }
      public DownloadOperation(DSMPBaseHandler handler, int id, 
                               long totToXfer, long ofs, File f) {
         super(handler, id, totToXfer, ofs, f);
      }
      
      public synchronized void  handleEndError(String reason) {
         if (reason != null) {
            DSMPBaseProto proto = null;
            proto=FTPGenerator.abortDownloadEvent((byte)0, id, 0, reason);
            handler.sendProtocolPacket(proto);
         }
      }
      
      public long getTotalConfirmed() { return totalconfirmed; }
      
      public synchronized void fireSentEvent(DSMPBaseProto p) {
         
         totalconfirmed += p.getNonHeaderSize() - 12;
         dataTransferred();
         
         if (--numout < 0) {
            System.out.println("Zoinks! numout in fireSentEvent < 0!!");
            numout = 0;
         }
         notifyAll();
      }
      
      public void sendData(long tofs, byte arr[], int bofs, int blen) {
         DSMPBaseProto proto = null;
         proto=FTPGenerator.downloadFrameEvent((byte)0, id, 
                                               tofs, arr, bofs, blen);
         proto.addSentListener(this);
         
        // If this is the first packet, just return, don't block, otherwise,
        //  We want to keep from saturating the upload stream, and we get 
        //  callbacks
         synchronized(this) {
            numout++;
            handler.sendProtocolPacket(proto);
            while(numout >= MAX_PROTO_OUT) {
               try {
                  wait(10000);
               } catch(InterruptedException ie) {}
            }
         }
      }
   }

   class UploadOperation extends ReceiveFileOperation {
      
      public UploadOperation(DSMPBaseHandler handler, int id,
                             long totToXfer, long ofs, OutputStream oos) {
         super(handler, id, totToXfer, ofs, oos);
      }
      public UploadOperation(DSMPBaseHandler handler, int id,
                             long totToXfer, long ofs, File f) {
         super(handler, id, totToXfer, ofs, f);
      }
      
      public synchronized void  handleEndError(String reason) {
         if (reason != null) {
            DSMPBaseProto proto = null;
            proto=FTPGenerator.uploadDataError((byte)0, 0, reason, id);
            handler.sendProtocolPacket(proto);
         } else {
            DSMPBaseProto proto = null;
            proto=FTPGenerator.operationCompleteEvent((byte)0, id);
            handler.sendProtocolPacket(proto);
         }
      }
   }
   
   class User {
      String name;
      int    loginid  = 0;
      Vector projects    = new Vector();
      boolean tokenLogin = false;
      
      Vector operations = null;
      String currentArea = null;
      
      public User(String name, int lid) {
         this.name = name;
         loginid = lid;
      }
      public User(String name, Vector projs, int loginid) {
         this.name = name;
         this.loginid = loginid;
         if (projs != null && projs.size() != 0) {
            Enumeration e = projs.elements();
            while(e.hasMoreElements()) {
               projects.addElement(e.nextElement());
            }
         }
      }
      
      public boolean getTokenLogin()          { return tokenLogin; }
      public void    setTokenLogin(boolean v) { tokenLogin = v;    }
      
      public Vector  getProjects()   { return projects; } 
      public String  getName()       { return name;     }
      public int     getLoginId()    { return loginid;  }
      
      public void    setArea(String a)  { currentArea = a;    }
      public String  getArea()          { return currentArea; }
      
      public synchronized void      addOperation(Operation op) {
         if (operations == null) {
            operations = new Vector();
         }
         operations.addElement(op);
      }
      
      public Operation getOperation(int id) {
         Operation ret = null;
         if (operations != null) {
            int i = operations.size()-1;
            for( ; i >= 0; i--) {
               Operation op = (Operation)operations.elementAt(i);
               if (op.getId() == id) {
                  ret = op;
                  break;
               }
            }
         }
         return ret;
      }
      public synchronized Operation removeOperation(int id) {
         Operation ret = null;
         if (operations != null) {
            int i = operations.size()-1;
            for( ; i >= 0; i--) {
               Operation op = (Operation)operations.elementAt(i);
               if (op.getId() == id) {
                  ret = op;
                  operations.removeElementAt(i);
                  break;
               }
            }
         }
         return ret;
      }
      
      public Vector  getOperations() {
         return operations;
      }
      
      public void    addProject(String p)  { 
         boolean doit = true;
         synchronized (projects) {
            if (projects.size() != 0) {
               Enumeration e = projects.elements();
               while(e.hasMoreElements()) {
                  if (((String)e.nextElement()).equals(p)) {
                     doit=false;
                     break;
                  }
               }
            }
            if (doit) {
               projects.addElement(p);
            }
         }
      }
   }
   
   
  // User key Vector value (first elem pw, reset projects)
   Hashtable userpw      = new Hashtable();
   
  // String elements have vector of User objects
  // Integer elements have a direct User object
   Hashtable users       = new Hashtable();
   
  // Integer elements have direct Handler objects
   Hashtable handlers    = new Hashtable();
   
  // Integer elements have direct Operation objects
   Hashtable operations  = new Hashtable();
   
   int localport            = 0;
   
  // Server Socket support
   public void run() {
      ServerSocket socket = null;
      for (int i=0; i<100; i++) {
         try {
            socket = new ServerSocket(localport);
            localport = socket.getLocalPort();
            break;
         } catch (IOException ioe) {
            localport++;
         }
      }
   
      System.out.println("Accept thread started. Listening on port " + 
                         localport);
                         
     //httpServer.startServer(8080, localport);
                         
      while (true) {
         try {
            Socket sock = socket.accept();
            if (sock == null) {
               System.out.println("DSMPServer: Got a NULL Socket Done!"); 
               break;
            }	
            
            DSMPBaseHandler handler = new DSMPSocketHandler(sock, this);
            handlers.put(new Integer(handler.getHandlerId()), handler);
                
         } catch (IOException e){			
            e.printStackTrace(System.out);
         }
      }
      
      try {socket.close();} catch(Throwable tt) {}
   }         
   
   public boolean parseArgs(String args[]) {
      for(int i=0; i < args.length; i++) {
         if (args[i].equalsIgnoreCase("-port")) {
            try {
               localport = Integer.parseInt(args[++i]);
            } catch(Throwable tt) {
               System.out.println("-port take an integer value");
               return false;
            }
         } else if (args[i].equalsIgnoreCase("-debug")) {
            setDebug(true);
            
         } else if (args[i].equalsIgnoreCase("-daemonStartup")) {
            InputStream is = System.in;
            OutputStream os = System.out;
            
            try {
               String outname = "/dev/null";
               System.setIn(new ByteArrayInputStream(new byte[1]));
//               System.setOut(new PrintStream(new FileOutputStream(outname)));
               System.setOut(System.err);
            } catch(Exception eee) {
               System.err.println("Error setting up stream info");
               eee.printStackTrace(System.err);
            }
            
            String username = null;
            String homedir  = null;
            byte   handle   = (byte)0;
            try {
               username = args[++i];
               homedir  = args[++i];
               handle   = Byte.parseByte(args[++i]);
               
            } catch(Throwable tt) {
               System.out.println("-daemonStartup takes a username homedir handle");
               return false;
            }
            
            DSMPBaseHandler hand = new DSMPBaseHandler(this);
            hand.setInputOutput(is, os);
            
            User userObj=new User(username, hand.getHandlerId());
            addUser(userObj);
            userObj.setArea(homedir);
            DSMPBaseProto proto = FTPGenerator.loginReply(handle, 
                                                          userObj.getLoginId(),
                                                          userObj.getArea(),
                                                          File.separator);
            hand.addMaskToFlags(0x01);
            daemonMode = true;
            
            hand.setIdentifier(username);
            
            hand.sendProtocolPacket(proto);
           //if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
            
         } else if (args[i].equalsIgnoreCase("-tokencipher")) {
         
           // Supporting token login ... get reflection setup
            try {
            
               String cipherFile     = args[++i];
               String edgecipherFile = args[++i];
               
              /*
               Class dsrvclass   = Class.forName("oem.edge.ed.util.SearchEtc");
               Class hashclass   = Class.forName("java.util.Hashtable");
               Class stringclass = Class.forName("java.lang.String");
               Class cipherclass = Class.forName("oem.edge.common.cipher.ODCipherRSA");
               
               Class classparms[] = new Class[1];
               classparms[0] = stringclass;
               java.lang.reflect.Method loadmeth = 
                  dsrvclass.getMethod("loadCipherFile", classparms);
                  
               String parmarr[] = new String[1];
               parmarr[0] = cipherFile;
               cipher = loadmeth.invoke(null, parmarr);
               
               parmarr[0] = edgecipherFile;
               edgecipher = loadmeth.invoke(null, parmarr);
              */
              
               cipher     = SearchEtc.loadCipherFile(cipherFile);
               edgecipher = SearchEtc.loadCipherFile(cipherFile);
               
               if (cipher != null && edgecipher != null) {
                 /*
                  classparms    = new Class[2];
                  classparms[0] = cipherclass;
                  classparms[1] = stringclass;
                  dataFromToken = dsrvclass.getMethod("dataFromToken", 
                                                      classparms);
                  if (dataFromToken == null) {
                     System.out.println(
                        "-tokencipher failed! Error finding dataFromToken");
                     cipher = null;
                  }
                 */
               } else {
                  cipher = null;
                  System.out.println(
                     "-tokencipher failed! Error loading cipherfiles: " + 
                     cipherFile + ", " + edgecipherFile);
               }
            } catch(Exception ee) {
               System.out.println("-tokencipher failed! " + ee.toString());
            }
         } else if (args[i].equalsIgnoreCase("-userpw")) {
            userpwFile = args[++i];
            if (!refreshUserPW()) {
            
               System.out.println("Error processing -userpw option");
               return false;
            }
         } else {
            System.out.println(args[i] + ": unknown option\n\nUsage:\n" +
                               "FTPServer [-port portno] [-userpw file]\n" +
                               "           [-tokencipher cipher1 cipher2]");
            return false;
         }
      }
      return true;
   }
   
   synchronized boolean refreshUserPW() {
      
      if (userpwFile == null) return false;
      
      try {
         
         File file = new File(userpwFile);
         if (file.lastModified() == userpwLastModified) {
            return false;
         }
         
         userpwLastModified = file.lastModified(); 
         System.out.println("Reloading userpw: " + (new Date()).toString());
         
         BufferedReader in = 
            new BufferedReader(new FileReader(userpwFile));
         String s;
         while((s=in.readLine()) != null) {
            s = s.trim();
            if (s.length() == 0 || s.startsWith("#")) {
               ;
            } else {
               StringTokenizer tokenizer = new StringTokenizer(s, ":");
               String user=tokenizer.nextToken();
               Vector v    = new Vector();
               Vector oldv = (Vector)userpw.get("user");
               
               
               int ii=0;
               int cnt = 0;
               
               if (oldv == null) {
                  System.out.println("User = " + user);
                  cnt=1;
               }
               
               while(tokenizer.hasMoreTokens()) {
                  String tn=tokenizer.nextToken();
                  boolean doit = true;
                  if (ii > 0 && oldv != null) {
                     Enumeration e = oldv.elements();
                     e.nextElement();
                     while(e.hasMoreElements()) {
                        String p = (String)e.nextElement();
                        if (p.equals(tn)) {
                           doit=false;
                           break;
                        }
                     }
                  }
                  v.addElement(tn);
                  if (doit) {
                     if (++cnt == 1) System.out.println("User = " + user);
                     if (ii++ !=  0) System.out.println("   " + tn);
                  }
               }
               
               userpw.put(user, v);
            }
         }
      } catch( Exception ee) { return false; } 
      return true;
   }
   
      
   public static void main(String args[]) {
   
      FTPServer srv = new FTPServer();
      if (!srv.parseArgs(args)) return;
      Thread thread  = new Thread(srv);
      
      if (!srv.getDaemonMode()) {
         thread.setName("FTPServer");
         thread.start();
      }
   }
   
   
  /* -------------------------------------------------------*\
  ** Work Routines
  \* -------------------------------------------------------*/
  
   boolean sendProtoTo(DSMPBaseProto proto, int loginid) {
      DSMPBaseHandler handler = 
         (DSMPBaseHandler)handlers.get(new Integer(loginid));
      if (handler != null) {
         handler.sendProtocolPacket(proto);
         return true;
      }
      return false;
   }
  
   void addOperation(Operation op) {
      int id = op.getId();
      operations.put(new Integer(id), op);
   }
   
   Operation endOperation(int id) {
      Operation op   = (Operation)operations.remove(new Integer(id));
      if (op != null) {
         op.endOperation("Operation Aborted");
      }
      return op;
   }
   Operation getOperation(int id) {
      Operation op   = (Operation)operations.get(new Integer(id));
      return op;
   }
   
  // Called by Operation.endOperation
   public void operationComplete(Operation op) {
      operations.remove(new Integer(op.getId()));
      User user = getUser(op.getHandler().getHandlerId());
      if (user != null) {
         user.removeOperation(op.getId());
      }
   }

   Vector getUser(String u) {
      return (Vector)users.get(u);
   }
   User getUser(int u) {
      return (User)users.get(new Integer(u));
   }
   
   void addUser(User user) {
      synchronized(users) {
         users.put(new Integer(user.getLoginId()), user);
         Vector v = (Vector)users.get(user.getName());
         if (v == null) {
            v = new Vector();
            users.put(user.getName(), v);
         }
         v.addElement(user);
      }
   }
   
   boolean removeUser(int loginid) {
      boolean ret = false;
      
      synchronized (users) {
         User user = (User)users.remove(new Integer(loginid));
         if (user != null) {
            Vector v = user.getOperations();
            if (v != null) {
               Enumeration enum = v.elements();
               while(enum.hasMoreElements()) {
                  Operation op = (Operation)enum.nextElement();
                  endOperation(op.getId());
               }
            }
         
            v = (Vector)users.get(user.getName());
            if (v != null) {
               int idx = 0;
               Enumeration e = v.elements();
               while(e.hasMoreElements()) {
                  User tu = (User)e.nextElement();
                  if (tu.getLoginId() == loginid) {
                     v.removeElementAt(idx);
                  }
                  idx++;
               }
               if (v.size() == 0) {
                  users.remove(user.getName());
               }
            }
            
           // If we are in Token mode, remove userpw entry as well
            if (user.getTokenLogin()) {
               userpw.remove(user.getName());
            }
         }
      }
      
      return ret;
   }
   
   
   private void invalidProtocol(DSMPBaseHandler h, byte opcode) {
      System.out.println("Invalid protocol for server: Opcode = " + opcode);
      System.out.println("Closing handler for ==> " + h.getHandlerId());
      h.shutdown();
   }
   
   private boolean validateLoggedIn(DSMPBaseHandler h, byte handle, 
                                    byte opcode) {
      boolean ret = true;
      if (!h.bitsSetInFlags(0x01)) {
         DSMPBaseProto proto;
         proto = FTPGenerator.genericReplyError(opcode, handle, 0, 
                                                 "Not Logged In");
         h.sendProtocolPacket(proto);
         ret = false;
      }
      return ret;
   }
   
  /* -------------------------------------------------------*\
  ** Shutdown, uncaughtProtocol callback
  \* -------------------------------------------------------*/
   public void fireShutdownEvent(DSMPBaseHandler hand) {
      System.out.println("GotFireShutdownEvent for connection = " + hand);
      removeUser(hand.getHandlerId());
      handlers.remove(new Integer(hand.getHandlerId()));
      if (daemonMode) {
         System.exit(2);
      }
   }
   
   public void uncaughtProtocol(DSMPBaseHandler h, byte opcode) {
      invalidProtocol(h, opcode);
   }
   
   
  /* -------------------------------------------------------*\
  ** Commands 
  \* -------------------------------------------------------*/
   public void fireLoginCommandToken(DSMPBaseHandler hand, byte flags, 
                                     byte handle, String token) {
                                     
      byte opcode = FTPGenerator.OP_LOGIN_REPLY;
      
      refreshUserPW();
      
      DSMPBaseProto proto = null;
      if (hand.bitsSetInFlags(0x01)) {
         proto = FTPGenerator.genericReplyError(opcode, handle, 0,
                                    "Already logged in on this connection=" + 
                                                hand.getHandlerId());
         hand.sendProtocolPacket(proto);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
         return;
      }      
      
      if (token.trim().equals("")) {
         proto = FTPGenerator.genericReplyError(opcode, handle, 0,
                                                "Bad token format");
      } else {
         
         Vector projs = null;
         String error = null;
         String user  = null;
         if (cipher != null) {
            try {
              /*
               Object parms[] = new Object[2];
               parms[0] = cipher;
               parms[1] = token;
               Hashtable hash = (Hashtable)dataFromToken.invoke(null, parms);
              */
              
               Hashtable hash = SearchEtc.dataFromToken((ODCipherRSA)cipher, 
                                                        token);
               if (hash != null) {
                  error = (String)hash.get("ERROR");
                  
                 /*
                  if (error != null && hash.get("INVALID") != null) {
                     parms[0] = edgecipher;
                     parms[1] = token;
                     hash = (Hashtable)dataFromToken.invoke(null, parms);
                     error = (String)hash.get("ERROR");
                  }
                 */
                 
                  if (error == null) {
                     projs = new Vector();
                     user = (String)hash.get("EDGEID");
                     if (user == null) {
                        error = "userid not in token";
                     } else {
                        hand.setIdentifier(user);
                     
                       // PW == "" means can't login with Userid/PW
                        projs.addElement(""); 
                        
                        int cnt=1;
                        String v = null;
                        while((v=(String)hash.get("P"+cnt)) != null) {
                           projs.addElement(v);
                           cnt++;
                        }
                     }
                  } 
               } else {
                  error = "Error parsing token - null hash";
               }
            } catch(Exception ee) {
               error = "Error parsing token";
            }
         } else {
            error = "Not configured for token login";
         }
         
         if (error == null) {
            synchronized (users) {
              /* ... Uncomment this to go back to only one login per user
               User u = (User)users.get(user);
               if (u != null) {
                  proto = FTPGenerator.genericReplyError(opcode, handle, 0,
                                                "Already logged in as ID = " + 
                                                          u.getLoginId());
               } else {
              */
               {
                  
                  User userObj=new User(user, projs, hand.getHandlerId());
                  userObj.setTokenLogin(true);
                  addUser(userObj);
                  projs.removeElementAt(0);  // get rid of PW
                  userObj.setArea("/tmp");
                  proto = FTPGenerator.loginReply(handle, 
                                                  userObj.getLoginId(),
                                                  userObj.getArea(), 
                                                  File.separator);
                  hand.addMaskToFlags(0x01);
               }
            }
         } else {
            proto = FTPGenerator.genericReplyError(opcode, handle, 0,
                                                   "LoginError = " + 
                                                   error);
         }
      }
      
      hand.sendProtocolPacket(proto);
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
   }
   public void fireLoginCommandUserPW(DSMPBaseHandler hand, byte flags, 
                                      byte handle,
                                      String user, String pw) {
                                      
      byte opcode = FTPGenerator.OP_LOGIN_REPLY;
      
      refreshUserPW();
      
      DSMPBaseProto proto=null;
      if (hand.bitsSetInFlags(0x01)) {
         proto = FTPGenerator.genericReplyError(opcode,
                                                handle, 0,
                                                "Already logged in = " + 
                                                hand.getHandlerId());
         System.out.println("ERROR");
         hand.sendProtocolPacket(proto);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
         return;
      }
      
      User userObj = null;
      Vector v = (Vector)userpw.get(user);
      if (v != null) {
         if (((String)v.firstElement()).equals(pw) && 
             pw.trim().length() != 0) {
            Vector projv = new Vector();
            if (v.size() > 1) {
               Enumeration e = v.elements();
               e.nextElement();
               while(e.hasMoreElements()) {
                  projv.addElement(e.nextElement());
               }
            }
            
            synchronized (users) {
              /* ... Uncomment this to go back to only one login per user
               User u = (User)users.get(user);
               if (u != null) {
                  proto = FTPGenerator.genericReplyError(opcode, handle, 
                                                          0, 
                                                       "Already logged in = " +
                                                          u.getLoginId());
               } else {
              */
               {
                  hand.setIdentifier(user);
                  userObj=new User(user, projv, hand.getHandlerId());
                  addUser(userObj);
                  userObj.setArea("/tmp");
                  proto = FTPGenerator.loginReply(handle, 
                                                  userObj.getLoginId(),
                                                  userObj.getArea(),
                                                  File.separator);
                  hand.addMaskToFlags(0x01);
               }
            }
         }
      }
      if (proto == null) {
         System.out.println("ERROR");
         proto = FTPGenerator.genericReplyError(FTPGenerator.OP_LOGIN_REPLY,
                                                handle, 0, "Bad UserID/PW");
      }
      hand.sendProtocolPacket(proto);
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
   }
   public void fireLogoutCommand(DSMPBaseHandler hand, byte flags, 
                                 byte handle) {
      byte opcode = FTPGenerator.OP_LOGOUT_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         
         if (removeUser(hand.getHandlerId())) {
            System.out.println("Logout:Huh? no User record for id=" + 
                               hand.getHandlerId());
         }
         DSMPBaseProto proto;
         proto=FTPGenerator.logoutReply(handle);
         hand.removeMaskFromFlags(0x01);
         hand.sendProtocolPacket(proto);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
         try {
            Thread.currentThread().sleep(2000);
         } catch(Throwable tt) {}
         hand.shutdown();
      }
   }
   
   public void fireChangeAreaCommand(DSMPBaseHandler hand, byte flags, 
                                     byte handle, String area) {
      
      byte opcode = FTPGenerator.OP_CHANGEAREA_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         
         DSMPBaseProto proto = null;
         User user = getUser(hand.getHandlerId());
         File f = new File(area);
         if (f.exists() && f.isDirectory() && f.canRead()) {
            user.setArea(area);
            proto = FTPGenerator.changeAreaReply(handle);
         } else {
            String err = "Specified area [" + area + "] is not valid";
            proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
         }
         hand.sendProtocolPacket(proto);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
      }
   }
   public void fireListAreaCommand(DSMPBaseHandler hand, byte flags, 
                                   byte handle) {
      byte opcode = FTPGenerator.OP_LISTAREA_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         
         DSMPBaseProto proto = null;
         User user = getUser(hand.getHandlerId());
         String area = user.getArea();
         File f = new File(area);
         if (f.exists() && f.isDirectory() && f.canRead()) {
            
            Vector v = new Vector();
            String arr[] = f.list();
            for(int i=0; arr != null && i < arr.length; i++) {
               f = new File(area + File.separator + arr[i]);
               byte type = AreaContent.TYPE_UNKNOWN;
               if (f.isFile()) {
                  type = AreaContent.TYPE_FILE;
               } else if (f.isDirectory()) {
                  type = AreaContent.TYPE_DIRECTORY;
               }
               
               AreaContent ac = new AreaContent(area, arr[i], type, 
                                                f.length(), f.lastModified());
               v.addElement(ac);
            }
            proto = FTPGenerator.listAreaReply(handle, v);
         } else {
            String err = "Current area [" + area + "] is no longer valid";
            proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
         }
         hand.sendProtocolPacket(proto);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
      }      
   }
   
   public void fireDeleteFileCommand(DSMPBaseHandler hand, byte flags, 
                                     byte handle, String file) {
      byte opcode = FTPGenerator.OP_DELETEFILE_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         
         DSMPBaseProto proto = null;
         User user = getUser(hand.getHandlerId());
         String area = user.getArea();
         File f = new File(area);
         if (f.exists() && f.isDirectory()) {
            if (file.indexOf(File.separator) >= 0) {
               String err = "File to delete CANNOT contain separator char [" +
                            file + "]";
               proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
            } else {
               f = new File(area + File.separator + file);
               if (f.isDirectory()) {
                  String err = "Can't delete directory [" + file + "]";
                  proto = FTPGenerator.genericReplyError(opcode, handle, 
                                                         0, err);
               } else if (f.delete()) {
                  proto = FTPGenerator.deleteFileReply(handle);
               } else {
                  String err = "Error deleting [" + file + "]";
                  proto = FTPGenerator.genericReplyError(opcode, handle, 
                                                         0, err);
               }
            }
         } else {
            String err = "Current area [" + area + "] is no longer valid";
            proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
         }
         hand.sendProtocolPacket(proto);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
      }
   }
   
   public void fireNewFolderCommand(DSMPBaseHandler hand, byte flags, 
                                    byte handle, String folder) {
      byte opcode = FTPGenerator.OP_NEWFOLDER_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         
         DSMPBaseProto proto = null;
         User user = getUser(hand.getHandlerId());
         String area = user.getArea();
         File f = new File(area);
         if (f.exists() && f.isDirectory()) {
            if (folder.indexOf(File.separator) >= 0) {
               String err = "Folder to create CANNOT contain separator char ["
                            + folder + "]";
               proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
            } else {
               f = new File(area + File.separator + folder);
               if (f.mkdir()) {
                  proto = FTPGenerator.newFolderReply(handle);
               } else {
                  String err = "Error creating new Folder [" + folder + "]";
                  proto = FTPGenerator.genericReplyError(opcode, handle, 
                                                         0, err);
               }
            }
         } else {
            String err = "Current area [" + area + "] is no longer valid";
            proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
         }
         hand.sendProtocolPacket(proto);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
      }
   }
   
   public void fireUploadCommand(DSMPBaseHandler hand, byte flags, 
                                 byte handle, boolean tryRestart, 
                                 int crc, long crcsz,
                                 long filelen, String file) {
      
      byte opcode = FTPGenerator.OP_UPLOAD_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         
         DSMPBaseProto proto = null;
         User user = getUser(hand.getHandlerId());
         String area = user.getArea();
         File f = new File(area);
         if (f.exists() && f.isDirectory()) {
            if (file.indexOf(File.separator) >= 0) {
               String err = "File to upload CANNOT contain separator char [" +
                            file + "]";
               proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
            } else {
               
               long ofs = 0;
               f = new File(area + File.separator + file);
               if (f.exists()) {
                  if (f.isFile()) {
                     if (tryRestart) {
                        if (f.length() == crcsz) {
                           try {
                              int lcrc = calculateCRC(f);
                              if (lcrc == crc) {
                                 ofs = f.length();
                              }
                           } catch(IOException io) {}
                        }
                     }
                     if (ofs == 0) f.delete();
                  } else {
                     String err = "Can't Upload over directory[" + file + "]";
                     proto = FTPGenerator.genericReplyError(opcode, handle, 
                                                            0, err);
                  }
               }
               
               if (proto == null) {
                  Operation op;
                  op = new UploadOperation(hand, IDGenerator.getId(), 
                                           filelen-ofs, ofs, f);
                  addOperation(op);       // trace by Dispatcher
                  user.addOperation(op);  // Track by user
                  proto = FTPGenerator.uploadReply(handle, op.getId(), ofs);
                  if (filelen == ofs) op.endOperation(null);
               }
            }
         } else {
            String err = "Current area [" + area + "] is no longer valid";
            proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
         }
         hand.sendProtocolPacket(proto);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
      }
   }
   
   public void fireAbortUploadCommand(DSMPBaseHandler hand, byte flags, 
                                      byte handle, int id) {
      
      byte opcode = FTPGenerator.OP_ABORTUPLOAD_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         
         DSMPBaseProto proto = null;
         User user = getUser(hand.getHandlerId());
         Operation op = user.getOperation(id);
         if (op != null) {
            op.endOperation("User Abort Action");
            proto = FTPGenerator.abortUploadReply(handle, id);
         } else {
            String err = "Operation ID to abort not found[" + id + "]";
            proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
         }
         hand.sendProtocolPacket(proto);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
      }
   }
   
   public void fireUploadDataCommand(DSMPBaseHandler hand, byte flags, 
                                     byte handle, int id, long ofs, 
                                     CompressInfo ci) { 
                                     
      byte opcode = FTPGenerator.OP_UPLOADDATA;
      
      if (!hand.bitsSetInFlags(0x01)) {
         DSMPBaseProto proto;
         proto = FTPGenerator.uploadDataError(handle, 0, 
                                              "Not Logged In", 
                                              id);
         hand.sendProtocolPacket(proto);
      } else {
         User user = getUser(hand.getHandlerId());
        // TODO CATCH THIS CAST ERROR
         UploadOperation op = (UploadOperation)user.getOperation(id);
         if (op != null) {
            op.frameData(ofs, ci.buf, ci.ofs, ci.len);
         } else {
            String err = "Operation ID to for UploadData not found[" + id + "]";
            DSMPBaseProto proto = null;
            proto = FTPGenerator.uploadDataError(handle, 0, err, id);
            hand.sendProtocolPacket(proto);
         }
      }
   }
   public void fireDownloadCommand(DSMPBaseHandler hand, byte flags, 
                                   byte handle, boolean tryRestart, 
                                   int crc, long filelen, String file) {
                                   
      byte opcode = FTPGenerator.OP_DOWNLOAD_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         
         DSMPBaseProto proto = null;
         User user = getUser(hand.getHandlerId());
         String area = user.getArea();
         File f = new File(area);
         long ofs = 0;
         if (f.exists() && f.isDirectory()) {
            if (file.indexOf(File.separator) >= 0) {
               String err = "File to download CANNOT contain separator char ["
                            + file + "]";
               proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
            } else {
               
               f = new File(area + File.separator + file);
               if (f.exists()) {
                  if (f.isFile()) {
                     if (tryRestart) {
                        if (f.length() >= filelen) {
                           try {
                              int lcrc = calculateCRC(f, filelen);
                              if (lcrc == crc) {
                                 ofs = filelen;
                              }
                           } catch(IOException io) {}
                        }
                     }
                  } else {
                     String err = "Can't Download a directory[" + 
                                  file + "]";
                     proto = FTPGenerator.genericReplyError(opcode, handle, 
                                                            0, err);
                  }
               } else {
                  String err = "File to download does not exist[" + file + "]";
                  proto = FTPGenerator.genericReplyError(opcode, handle, 
                                                         0, err);
               }
            }
               
            if (proto == null) {
               DownloadOperation op;
               op = new DownloadOperation(hand, IDGenerator.getId(), 
                                          f.length()-ofs, ofs, f);
               addOperation(op);       // trace by Dispatcher
               user.addOperation(op);  // Track by user
               proto = FTPGenerator.downloadReply(handle, op.getId(), 
                                                  ofs, f.length());
               
               hand.sendProtocolPacket(proto);
               if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
               proto = null;
         
               new Thread(op, "DownloadOp").start();
            }
         } else {
            String err = "Current area [" + area + "] is no longer valid";
            proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
         }
         
         if (proto != null) {
            hand.sendProtocolPacket(proto);
            if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
         }
      }
   }
                         
   public void fireAbortDownloadCommand(DSMPBaseHandler hand, byte flags, 
                                        byte handle, int id) {
      byte opcode = FTPGenerator.OP_ABORTDOWNLOAD_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         
         DSMPBaseProto proto = null;
         User user = getUser(hand.getHandlerId());
         Operation op = user.getOperation(id);
         if (op != null) {
            op.endOperation("User Abort Action");
            proto = FTPGenerator.abortDownloadReply(handle, id);
         } else {
            String err = "Operation ID to abort not found[" + id + "]";
            proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
         }
         hand.sendProtocolPacket(proto);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag 
      }
   }
   
   public void fireOperationCompleteCommand(DSMPBaseHandler hand, byte flags, 
                                            byte handle, int id, String md5) {
      byte opcode = FTPGenerator.OP_OPERATION_COMPLETE;
      if (validateLoggedIn(hand, handle, opcode)) {
         
         DSMPBaseProto proto = null;
         User user = getUser(hand.getHandlerId());
         Operation op = user.getOperation(id);
         if (op != null) {
            op.endOperation(null);
         } else {
            ;
           //   String err = "Operation ID to abort not found[" + id + "]";
           // proto = FTPGenerator.genericReplyError(opcode, handle, 0, err);
         }
        //hand.sendProtocolPacket(proto);
      }
   }
   
  /* -------------------------------------------------------*\
  ** Replies
  \* -------------------------------------------------------*/
   
  /* -------------------------------------------------------*\
  ** Reply Errors
  \* -------------------------------------------------------*/
  
   
  /* -------------------------------------------------------*\
  ** Events
  \* -------------------------------------------------------*/
   
  /* -------------------------------------------------------*\
  ** Errors
  \* -------------------------------------------------------*/
}
