/**
 * DropboxAccessSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * a0425.05 v62404125333
 */

package oem.edge.ed.odc.dropbox.server;

import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;
import javax.xml.namespace.*;
import javax.xml.soap.*;
import java.util.Iterator;
import java.util.Vector;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006-2006                                    */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
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
** Service proxy to access DropboxAccessSvc via jaxrpc/soap.
**
** Implement ServiceLifecycle to get MessageContext, so we can get the sessionid (if there is one)
**  and register it with the dropbox instance for the length of the call.
**
*/
public class DropboxAccessWebSvcSoapBindingImpl implements oem.edge.ed.odc.dropbox.service.DropboxAccess, ServiceLifecycle {

   static private Boolean lock = new Boolean(true);
   static private oem.edge.ed.odc.dropbox.server.DropboxAccessSrv dropbox = null;
   
   org.apache.log4j.Logger log = 
   org.apache.log4j.Logger.getLogger(DropboxAccessWebSvcSoapBindingImpl.class);
   
   
  // ---- LifeCycle stuff
   private ServletEndpointContext servletcontext;
   public void init(Object ctx){
   
     // Initialize message context for the service use 
      servletcontext = (ServletEndpointContext)ctx;
   }
   
   public void destroy(){
   }
   
   public MessageContext getContext() {
      if (servletcontext != null) return servletcontext.getMessageContext();
      return null;
   }
   
  // register/precall
   private void doreg() {
      boolean done = false;
      MessageContext mc = getContext();
      
      if (mc != null) try {
        // get SOAP message context
         SOAPMessageContext smc = (SOAPMessageContext) mc;
        // get SOAP envelope from SOAP message
         SOAPEnvelope se = smc.getMessage().getSOAPPart().getEnvelope();
        // get SOAPHeader instance for SOAP envelope
         SOAPHeader sh = se.getHeader();
         
         if (log.isDebugEnabled()) {
            log.debug(se.toString());
         }
         
         if (sh != null) {
         
            Iterator it = 
               sh.examineHeaderElements("http://DropboxAccess/dboxsessionid");
            
         
            while (it.hasNext()) {
               SOAPHeaderElement she = (SOAPHeaderElement)it.next();
               String sesid = she.getValue();
               if (sesid != null) {
                  dropbox.setThreadSessionID(sesid);
                  done = true;
                  break;
               }
            }
         }
      } catch(Exception e) { 
         e.printStackTrace(System.out);
      }
      
      if (!done) {
         log.warn("No or bad message context while setting SessionId for JAXRCP!");
      }
   }
   
  // deregister/postcall
   private void dodereg() {
      dropbox.setThreadSessionID(null);
   }
   
   
   public DropboxAccessWebSvcSoapBindingImpl() {
     // Create the singleton if its not yet created
      synchronized(lock) {
         if (dropbox == null) {
            dropbox = new oem.edge.ed.odc.dropbox.server.DropboxAccessSrv();
         }
      }
   }
    
   public java.util.HashMap createSession(java.lang.String arg_0_0) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.createSession(arg_0_0);
      } finally {
         dodereg();
      }
   }

   public java.util.HashMap createSession(java.lang.String arg_0_1,
                                          java.lang.String arg_1_1) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.createSession(arg_0_1,
                                      arg_1_1);
      } finally {
         dodereg();
      }
   }

   public java.util.HashMap refreshSession() throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.refreshSession();
      } finally {
         dodereg();
      }
   }

   public void closeSession() throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.closeSession();
      } finally {
         dodereg();
      }
   }

   public long createPackage(java.lang.String arg_0_4,
                             java.lang.String desc,
                             long poolid,
                             long arg_1_4,
                             java.util.Vector arg_2_4,
                             int arg_3_4,
                             int arg_4_4) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.createPackage(arg_0_4,
                                      desc, 
                                      poolid,
                                      arg_1_4,
                                      arg_2_4,
                                      arg_3_4,
                                      arg_4_4);
      } finally {
         dodereg();
      }
   }

   public long createPackage(java.lang.String arg_0_5,
                             java.lang.String desc,
                             long poolid, 
                             java.util.Vector arg_1_5,
                             int arg_2_5,
                             int arg_3_5) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.createPackage(arg_0_5,
                                      desc,
                                      poolid,
                                      arg_1_5,
                                      arg_2_5,
                                      arg_3_5);
      } finally {
         dodereg();
      }
   }

   public long createPackage(java.lang.String arg_0_6,
                             java.lang.String desc,
                             long poolid, 
                             int arg_1_6,
                             int arg_2_6) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.createPackage(arg_0_6,
                                      desc,
                                      poolid,
                                      arg_1_6,
                                      arg_2_6);
      } finally {
         dodereg();
      }
   }

   public long createPackage(java.lang.String arg_0_7) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.createPackage(arg_0_7);
      } finally {
         dodereg();
      }
   }

   public java.lang.String getLoginMessage() throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.getLoginMessage();
      } finally {
         dodereg();
      }
   }

   public void setPackageFlags(long arg_0_9,
                               int arg_1_9,
                               int arg_2_9) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.setPackageFlags(arg_0_9,
                                 arg_1_9,
                                 arg_2_9);
      } finally {
         dodereg();
      }
   }

   public void deletePackage(long arg_0_10) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.deletePackage(arg_0_10);
      } finally {
         dodereg();
      }
   }

   public void commitPackage(long arg_0_11) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.commitPackage(arg_0_11);
      } finally {
         dodereg();
      }
   }

   public void markPackage(long arg_0_12,
                           boolean arg_1_12) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.markPackage(arg_0_12,
                             arg_1_12);
      } finally {
         dodereg();
      }
   }

   public void addPackageAcl(long packid, oem.edge.ed.odc.dropbox.common.AclInfo acl) 
      throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {      
      try {
         doreg();
         dropbox.addPackageAcl(packid, acl);
      } finally {
         dodereg();
      }
   }                                             
   
   public void addPackageAcl(long arg_0_13,
                             java.lang.String arg_1_13,
                             byte arg_2_13) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.addPackageAcl(arg_0_13,
                               arg_1_13,
                               arg_2_13);
      } finally {
         dodereg();
      }
   }

   public void removePackageAcl(long arg_0_14,
                                java.lang.String arg_1_14,
                                byte arg_2_14) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.removePackageAcl(arg_0_14,
                                  arg_1_14,
                                  arg_2_14);
      } finally {
         dodereg();
      }
   }

   public void addUserAcl(long arg_0_15,
                          java.lang.String arg_1_15) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.addUserAcl(arg_0_15,
                            arg_1_15);
      } finally {
         dodereg();
      }
   }

   public void addGroupAcl(long arg_0_16,
                           java.lang.String arg_1_16) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.addGroupAcl(arg_0_16,
                             arg_1_16);
      } finally {
         dodereg();
      }
   }

   public void addGroupAcl(java.lang.String arg_0_17,
                           java.lang.String arg_1_17,
                           boolean arg_2_17) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.addGroupAcl(arg_0_17,
                             arg_1_17,
                             arg_2_17);
      } finally {
         dodereg();
      }
   }

   public void addProjectAcl(long arg_0_18,
                             java.lang.String arg_1_18) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.addProjectAcl(arg_0_18,
                               arg_1_18);
      } finally {
         dodereg();
      }
   }

   public void removeUserAcl(long arg_0_19,
                             java.lang.String arg_1_19) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.removeUserAcl(arg_0_19,
                               arg_1_19);
      } finally {
         dodereg();
      }
   }

   public void removeGroupAcl(long arg_0_20,
                              java.lang.String arg_1_20) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.removeGroupAcl(arg_0_20,
                                arg_1_20);
      } finally {
         dodereg();
      }
   }

   public void removeGroupAcl(java.lang.String arg_0_21,
                              java.lang.String arg_1_21,
                              boolean arg_2_21) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.removeGroupAcl(arg_0_21,
                                arg_1_21,
                                arg_2_21);
      } finally {
         dodereg();
      }
   }

   public void removeProjectAcl(long arg_0_22,
                                java.lang.String arg_1_22) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.removeProjectAcl(arg_0_22,
                                  arg_1_22);
      } finally {
         dodereg();
      }
   }

   public java.util.Vector queryStoragePoolInformation() throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryStoragePoolInformation();
      } finally {
         dodereg();
      }
   }
   
   public oem.edge.ed.odc.dropbox.common.PoolInfo 
   getStoragePoolInstance(long poolid) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.getStoragePoolInstance(poolid);
      } finally {
         dodereg();
      }
   }
   
   public void changePackageExpiration(long arg_0_23,
                                       long arg_1_23) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.changePackageExpiration(arg_0_23,
                                         arg_1_23);
      } finally {
         dodereg();
      }
   }

   public void setPackageDescription(long arg_0_23,
                                     String desc) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.setPackageDescription(arg_0_23,
                                       desc);
      } finally {
         dodereg();
      }
   }
   
   public java.util.HashMap getOptions() throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.getOptions();
      } finally {
         dodereg();
      }
   }

   public java.util.HashMap getOptions(java.util.Vector arg_0_25) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.getOptions(arg_0_25);
      } finally {
         dodereg();
      }
   }

   public java.lang.String getOption(java.lang.String arg_0_26) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.getOption(arg_0_26);
      } finally {
         dodereg();
      }
   }

   public void setOptions(java.util.HashMap arg_0_27) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.setOptions(arg_0_27);
      } finally {
         dodereg();
      }
   }

   public void setOption(java.lang.String arg_0_28,
                         java.lang.String arg_1_28) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.setOption(arg_0_28,
                           arg_1_28);
      } finally {
         dodereg();
      }
   }

   public java.util.Vector getProjectList() throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.getProjectList();
      } finally {
         dodereg();
      }
   }

   public java.util.Vector queryPackages(java.lang.String arg_0_30,
                                         boolean arg_1_30,
                                         boolean arg_2_30,
                                         boolean arg_3_30,
                                         boolean arg_4_30,
                                         boolean arg_5_30) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryPackages(arg_0_30,
                                      arg_1_30,
                                      arg_2_30,
                                      arg_3_30,
                                      arg_4_30,
                                      arg_5_30);
      } finally {
         dodereg();
      }
   }

   public java.util.Vector queryPackages(boolean arg_0_31,
                                         boolean arg_1_31,
                                         boolean arg_2_31,
                                         boolean arg_3_31) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryPackages(arg_0_31,
                                      arg_1_31,
                                      arg_2_31,
                                      arg_3_31);
      } finally {
         dodereg();
      }
   }

   public oem.edge.ed.odc.dropbox.common.PackageInfo queryPackage(long arg_0_32,
                                                                  boolean arg_1_32) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryPackage(arg_0_32,
                                     arg_1_32);
      } finally {
         dodereg();
      }
   }

   public java.util.Vector queryPackageContents(long arg_0_33) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryPackageContents(arg_0_33);
      } finally {
         dodereg();
      }
   }

   public java.util.Vector queryFiles(java.lang.String arg_0_34,
                                      boolean arg_1_34,
                                      boolean arg_2_34) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryFiles(arg_0_34,
                                   arg_1_34,
                                   arg_2_34);
      } finally {
         dodereg();
      }
   }

   public java.util.Vector queryFiles(boolean arg_0_35) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryFiles(arg_0_35);
      } finally {
         dodereg();
      }
   }

   public oem.edge.ed.odc.dropbox.common.FileInfo queryFile(long arg_0_36) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryFile(arg_0_36);
      } finally {
         dodereg();
      }
   }

   public java.util.Vector queryPackageAcls(long arg_0_37,
                                            boolean arg_1_37) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryPackageAcls(arg_0_37,
                                         arg_1_37);
      } finally {
         dodereg();
      }
   }
   
    public java.util.Vector queryPackageAclCompanies(long arg_0_42) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryPackageAclCompanies(arg_0_42);
      } finally {
         dodereg();
      }
    }

    public java.util.Vector queryRepresentedCompanies(java.util.Vector arg_0_43, boolean arg_1_43) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryRepresentedCompanies(arg_0_43, arg_1_43);
      } finally {
         dodereg();
      }
    }

    public java.util.Vector lookupUser(java.lang.String arg_0_44, boolean arg_1_44) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.lookupUser(arg_0_44, arg_1_44);
      } finally {
         dodereg();
      }
    }
   

   public java.util.Vector queryPackageFileAcls(long arg_0_38,
                                                long arg_1_38) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryPackageFileAcls(arg_0_38,
                                             arg_1_38);
      } finally {
         dodereg();
      }
   }

   public void addItemToPackage(long arg_0_39,
                                long arg_1_39) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.addItemToPackage(arg_0_39,
                                  arg_1_39);
      } finally {
         dodereg();
      }
   }

   public void removeItemFromPackage(long arg_0_40,
                                     long arg_1_40) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.removeItemFromPackage(arg_0_40,
                                       arg_1_40);
      } finally {
         dodereg();
      }
   }

   public long uploadFileToPackage(long arg_0_41,
                                   java.lang.String arg_1_41,
                                   long arg_2_41) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.uploadFileToPackage(arg_0_41,
                                            arg_1_41,
                                            arg_2_41);
      } finally {
         dodereg();
      }
   }

   public oem.edge.ed.odc.dropbox.common.FileSlot allocateUploadFileSlot(long arg_0_42,
                                                                         long arg_1_42,
                                                                         int arg_2_42) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.allocateUploadFileSlot(arg_0_42,
                                               arg_1_42,
                                               arg_2_42);
      } finally {
         dodereg();
      }
   }

   public oem.edge.ed.odc.dropbox.common.FileSlot uploadFileSlotToPackage(long arg_0_43,
                                                                          long arg_1_43,
                                                                          long arg_2_43,
                                                                          boolean arg_3_43,
                                                                          byte[] arg_4_43) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.uploadFileSlotToPackage(arg_0_43,
                                                arg_1_43,
                                                arg_2_43,
                                                arg_3_43,
                                                arg_4_43);
      } finally {
         dodereg();
      }
   }

   public java.util.Vector queryFileSlots(long arg_0_44,
                                          long arg_1_44) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryFileSlots(arg_0_44,
                                       arg_1_44);
      } finally {
         dodereg();
      }
   }

   public void removeFileSlot(long arg_0_45,
                              long arg_1_45,
                              long arg_2_45) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.removeFileSlot(arg_0_45,
                                arg_1_45,
                                arg_2_45);
      } finally {
         dodereg();
      }
   }

   public void releaseFileSlot(long arg_0_46,
                               long arg_1_46,
                               long arg_2_46) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.releaseFileSlot(arg_0_46,
                                 arg_1_46,
                                 arg_2_46);
      } finally {
         dodereg();
      }
   }

   public void registerAuditInformation(long arg_0_51, long arg_1_51, long arg_2_51, 
                                        long arg_3_51, boolean arg_4_51) throws java.rmi.RemoteException, oem.edge .ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.registerAuditInformation(arg_0_51,
                                          arg_1_51,
                                          arg_2_51,
                                          arg_3_51,
                                          arg_4_51);
      } finally {
         dodereg();
      }
   }
   
   public void commitUploadedFile(long arg_0_47,
                                  long arg_1_47,
                                  long arg_2_47,
                                  java.lang.String arg_3_47) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.commitUploadedFile(arg_0_47,
                                    arg_1_47,
                                    arg_2_47,
                                    arg_3_47);
      } finally {
         dodereg();
      }
   }

   public byte[] downloadPackage(long arg_0_48,
                                 java.lang.String arg_1_48) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.downloadPackage(arg_0_48,
                                        arg_1_48);
      } finally {
         dodereg();
      }
   }

   public byte[] downloadPackageItem(long arg_0_49,
                                     long arg_1_49) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.downloadPackageItem(arg_0_49,
                                            arg_1_49);
      } finally {
         dodereg();
      }
   }

   public byte[] downloadPackageItem(long arg_0_50,
                                     long arg_1_50,
                                     long arg_2_50,
                                     long arg_3_50) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.downloadPackageItem(arg_0_50,
                                            arg_1_50,
                                            arg_2_50,
                                            arg_3_50);
      } finally {
         dodereg();
      }
   }

   public java.lang.String[] getPackageItemMD5(long arg_0_51,
                                               long arg_1_51,
                                               long arg_2_51) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.getPackageItemMD5(arg_0_51,
                                          arg_1_51,
                                          arg_2_51);
      } finally {
         dodereg();
      }
   }

   public void createGroup(java.lang.String arg_0_52,
                           byte arg_1_52,
                           byte arg_2_52) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.createGroup(arg_0_52,
                             arg_1_52,
                             arg_2_52);
      } finally {
         dodereg();
      }
   }

   public void createGroup(java.lang.String arg_0_53) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.createGroup(arg_0_53);
      } finally {
         dodereg();
      }
   }

   public void deleteGroup(java.lang.String arg_0_54) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.deleteGroup(arg_0_54);
      } finally {
         dodereg();
      }
   }

   public void modifyGroupAttributes(java.lang.String arg_0_55,
                                     byte arg_1_55,
                                     byte arg_2_55) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         dropbox.modifyGroupAttributes(arg_0_55,
                                       arg_1_55,
                                       arg_2_55);
      } finally {
         dodereg();
      }
   }

   public oem.edge.ed.odc.dsmp.common.GroupInfo queryGroup(String groupname) 
      throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {      
      try {
         doreg();
         return dropbox.queryGroup(groupname);
      } finally {
         dodereg();
      }
   }                                             
   
   public java.util.HashMap queryGroups(java.lang.String arg_0_56,
                                        boolean arg_1_56,
                                        boolean arg_2_56,
                                        boolean arg_3_56) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryGroups(arg_0_56,
                                    arg_1_56,
                                    arg_2_56,
                                    arg_3_56);
      } finally {
         dodereg();
      }
   }

   public java.util.HashMap queryGroups(boolean arg_0_57,
                                        boolean arg_1_57) throws java.rmi.RemoteException,
      oem.edge.ed.odc.dsmp.common.DboxException {
      try {
         doreg();
         return dropbox.queryGroups(arg_0_57,
                                    arg_1_57);
      } finally {
         dodereg();
      }
   }
}
