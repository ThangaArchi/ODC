package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2006                                     */
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

public class User extends UserInfo {

  // values of session bits. Refed in PackageManager
   static public final  int SessionFlag_ShowHidden           = 0x01;
   static public final  int SessionFlag_ItarCertified        = 0x02;
   static public final  int SessionFlag_ItarSessionCertified = 0x04;

   boolean doProjectSend = true;
   public boolean getDoProjectSend()           { return doProjectSend; }
   public void    setDoProjectSend(boolean v ) { doProjectSend = v;    }

   int    loginid  = 0;
   boolean tokenLogin = false;
   String clienttype = "NA";
   String OS         = "NA";
   
   Vector operations = null;
   
  // All flags always default to false 
   int sessionflags  = 0;
   
   long sessionid = -1;
   public long getSessionId()       { return sessionid; }
   public void setSessionId(long v) { sessionid = v;    }
   
   public int  getSessionFlags()      { return sessionflags; }
   public void setSessionFlags(int v) { sessionflags = v;    }
   
  // This flag gets set in session by the user
   public boolean isSessionItarCertified() {
      return (sessionflags & SessionFlag_ItarSessionCertified) != 0;
   }
   
  // This flag gets set in session at login time
   public boolean isUserItarCertified() {
      return (sessionflags & SessionFlag_ItarCertified) != 0;
   }
   
  // This sets it in USER object only!
   public void setUserItarCertified(boolean v) {
      if (v) sessionflags |= SessionFlag_ItarCertified;
      else   sessionflags &= ~SessionFlag_ItarCertified;
   }
   
   public User() {
      super();
   }
      
   public User(String name, int lid) {
      super(name);
      loginid = lid;
   }
   public User(String name, Vector projs, int loginid) {
      super(name, projs);
      this.loginid = loginid;
   }
      
   public boolean getTokenLogin()          { return tokenLogin; }
   public void    setTokenLogin(boolean v) { tokenLogin = v;    }
      
   public int     getLoginId()      { return loginid;  }
   public void    setLoginId(int v) { loginid = v;     }
   
   public String  getOS()                 { return OS;                       }
   public void    setOS(String v)         { OS = (v==null)?"NA":v;           }
   
   public String  getClientType()         { return clienttype;               }
   public void    setClientType(String v) { clienttype = (v==null)?"NA":v;   }
      
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
      
   public String toString() {
      String ret =  "User: Sessid: " + sessionid + 
                    " clienttype: " + clienttype + 
                    " OS: " + OS +
                    " loginid = " + getLoginId() +
                    "\n" + super.toString();
            
      return ret;
   }
}
