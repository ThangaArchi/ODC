package oem.edge.ed.odc.dropbox.common;

import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.util.Nester;

import java.util.*;
import java.io.*;
import java.util.zip.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

public class DropboxDispatchBase extends FTPDispatchBase {

   public byte getReplyForOpcode(byte op) throws InvalidProtocolException {
      byte ret = 0;
      switch(op) {
        // Dropbox
         case DropboxGenerator.OP_SET_PACKAGE_OPTION:
            ret = DropboxGenerator.OP_SET_PACKAGE_OPTION_REPLY; break;
         case DropboxGenerator.OP_CREATE_PACKAGE:
            ret = DropboxGenerator.OP_CREATE_PACKAGE_REPLY; break;
         case DropboxGenerator.OP_DELETE_PACKAGE:
            ret = DropboxGenerator.OP_DELETE_PACKAGE_REPLY; break;
         case DropboxGenerator.OP_COMMIT_PACKAGE:
            ret = DropboxGenerator.OP_COMMIT_PACKAGE_REPLY; break;
         case DropboxGenerator.OP_QUERY_PACKAGES:
            ret = DropboxGenerator.OP_QUERY_PACKAGES_REPLY; break;
         case DropboxGenerator.OP_QUERY_PACKAGE:
            ret = DropboxGenerator.OP_QUERY_PACKAGE_REPLY; break;
         case DropboxGenerator.OP_QUERY_PACKAGE_CONTENTS:
            ret = DropboxGenerator.OP_QUERY_PACKAGE_CONTENTS_REPLY; break;
         case DropboxGenerator.OP_QUERY_PACKAGE_ACLS:
            ret = DropboxGenerator.OP_QUERY_PACKAGE_ACLS_REPLY; break;
         case DropboxGenerator.OP_QUERY_PACKAGE_FILE_ACLS:
            ret = DropboxGenerator.OP_QUERY_PACKAGE_FILE_ACLS_REPLY; break;
         case DropboxGenerator.OP_QUERY_FILES:
            ret = DropboxGenerator.OP_QUERY_FILES_REPLY; break;
         case DropboxGenerator.OP_QUERY_FILE:
            ret = DropboxGenerator.OP_QUERY_FILE_REPLY; break;
         case DropboxGenerator.OP_ADD_ITEM_TO_PACKAGE:
            ret = DropboxGenerator.OP_ADD_ITEM_TO_PACKAGE_REPLY; break;
         case DropboxGenerator.OP_UPLOAD_FILE_TO_PACKAGE:
            ret = DropboxGenerator.OP_UPLOAD_FILE_TO_PACKAGE_REPLY; break;
         case DropboxGenerator.OP_REMOVE_ITEM_FROM_PACKAGE:
            ret = DropboxGenerator.OP_REMOVE_ITEM_FROM_PACKAGE_REPLY; break;
         case DropboxGenerator.OP_DOWNLOAD_PACKAGE_ITEM:
            ret = DropboxGenerator.OP_DOWNLOAD_PACKAGE_ITEM_REPLY; break;
         case DropboxGenerator.OP_DOWNLOAD_PACKAGE:
            ret = DropboxGenerator.OP_DOWNLOAD_PACKAGE_REPLY; break;
         case DropboxGenerator.OP_ADD_PACKAGE_ACL:
            ret = DropboxGenerator.OP_ADD_PACKAGE_ACL_REPLY; break;
         case DropboxGenerator.OP_REMOVE_PACKAGE_ACL:
            ret = DropboxGenerator.OP_REMOVE_PACKAGE_ACL_REPLY; break;
         case DropboxGenerator.OP_CHANGE_PACKAGE_EXPIRATION:
            ret = DropboxGenerator.OP_CHANGE_PACKAGE_EXPIRATION_REPLY; break;
         case DropboxGenerator.OP_GET_PROJECTLIST:
            ret = DropboxGenerator.OP_GET_PROJECTLIST_REPLY; break;
         case DropboxGenerator.OP_MARK_PACKAGE:
            ret = DropboxGenerator.OP_MARK_PACKAGE_REPLY; break;
         case DropboxGenerator.OP_SEND_NEW_FRAME:
            throw new InvalidProtocolException("No reply expected: SendFrame");
         case DropboxGenerator.OP_PROTO_VERSION:
            ret = DropboxGenerator.OP_PROTO_VERSION_REPLY; break;
         case DropboxGenerator.OP_CREATE_GROUP:
            ret = DropboxGenerator.OP_CREATE_GROUP_REPLY; break;
         case DropboxGenerator.OP_DELETE_GROUP:
            ret = DropboxGenerator.OP_DELETE_GROUP_REPLY; break;
         case DropboxGenerator.OP_MODIFY_GROUP_ACL:
            ret = DropboxGenerator.OP_MODIFY_GROUP_ACL_REPLY; break;
         case DropboxGenerator.OP_MODIFY_GROUP_ATTRIBUTES:
            ret = DropboxGenerator.OP_MODIFY_GROUP_ATTRIBUTES_REPLY; break;
         case DropboxGenerator.OP_QUERY_GROUPS:
            ret = DropboxGenerator.OP_QUERY_GROUPS_REPLY; break;
         case DropboxGenerator.OP_MANAGE_OPTIONS:
            ret = DropboxGenerator.OP_MANAGE_OPTIONS_REPLY; break;
         case DropboxGenerator.OP_GET_STORAGEPOOL_INSTANCE:
            ret = DropboxGenerator.OP_GET_STORAGEPOOL_INSTANCE_REPLY; break;
         case DropboxGenerator.OP_QUERY_STORAGEPOOL_INFO:
            ret = DropboxGenerator.OP_QUERY_STORAGEPOOL_INFO_REPLY; break;
         default: 
            ret = super.getReplyForOpcode(op);
      }
      return ret;
   }
   
  /* -------------------------------------------------------*\
  ** Shutdown
  \* -------------------------------------------------------*/
   

  /* -------------------------------------------------------*\
  ** Commands
  \* -------------------------------------------------------*/
   
  /* -=-=-=-=-=-=-=-=-=- Start of DropBox commands -=-=-=-=-=-=-=-=-=-=-=-=- */
   
   public void fireCreatePackageCommand(DSMPBaseHandler h, byte flags, 
                                        byte handle, String packname, 
                                        long poolid,
                                        long expiration, Vector acls,
                                        int optmsk, int optvals) {
      if (printdebug && dodebug) {
         System.out.println("-----fireCreatePackageCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackname[" +packname+"]");
         System.out.println("\texpiration[" +expiration+"]");
         System.out.println("\tpoolid[" +poolid+"]");
         System.out.println("\toptmsk[" +optmsk+"]");
         System.out.println("\toptvals[" +optvals+"]");
         
         Enumeration enum = acls.elements();
         while(enum.hasMoreElements()) {
            AclInfo aclinfo = (AclInfo)enum.nextElement();
            System.out.println("\t\tacltype[" +aclinfo.getAclStatus()+"]");
            System.out.println("\t\taclname[" +aclinfo.getAclName()+"]");
         }
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CREATE_PACKAGE);
   }
    
   public void fireDeletePackageCommand(DSMPBaseHandler h, byte flags, 
                                        byte handle, long packid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDeletePackageCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid[" +packid+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_DELETE_PACKAGE);
   }
    
   public void fireCommitPackageCommand(DSMPBaseHandler h, byte flags, 
                                        byte handle, long packid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireCommitPackageCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid[" +packid+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_COMMIT_PACKAGE);
   }
    
   public void fireQueryPackagesCommand(DSMPBaseHandler h, byte flags, 
                                        byte handle, 
                                        boolean regexpValid,
                                        boolean ownerOrAccessor,
                                        String regexp, 
                                        boolean filterMarked,
                                        boolean filterCompleted) {
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackagesCommand: " +
                            "\n\tflags [" +flags+"] hand["+handle+"]");
         System.out.println("\tregexpV [" +regexpValid+"]");
         System.out.println("\tOwnOrAcc[" +ownerOrAccessor+"]");
         System.out.println("\tregexp  [" +regexp+"]");
         System.out.println("\tfiltmark[" +filterMarked+"]");
         System.out.println("\tfiltcomp[" +filterCompleted+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGES);
   }
    
   public void fireQueryPackageCommand(DSMPBaseHandler h, byte flags, 
                                       byte handle, long packid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid[" +packid+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE);
   }
    
   public void fireQueryPackageContentsCommand(DSMPBaseHandler h, byte flags, 
                                               byte handle, long packid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageContentsCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid[" +packid+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE_CONTENTS);
   }
    
   public void fireQueryPackageAclsCommand(DSMPBaseHandler h, byte flags, 
                                           byte handle, long packid,
                                           boolean staticOnly) {
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageAclsCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid[" +packid+"]");
         System.out.println("\tstaticOnly[" +staticOnly+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE_ACLS);
   }
    
   public void fireQueryPackageFileAclsCommand(DSMPBaseHandler h, byte flags, 
                                               byte handle, long packid, 
                                               long fileid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageFileAclsCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid[" +packid+"]");
         System.out.println("\tfileid[" +fileid+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE_FILE_ACLS);
   }
    
   public void fireQueryFilesCommand(DSMPBaseHandler h, byte flags, 
                                     byte handle,
                                     boolean regexpValid,
                                     boolean ownerOrAccessor,
                                     String regexp) {
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryFilesCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tregexpV [" +regexpValid+"]");
         System.out.println("\tOwnOrAcc[" +ownerOrAccessor+"]");
         System.out.println("\tregexp  [" +regexp+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_FILES);
   }
    
   public void fireQueryFileCommand(DSMPBaseHandler h, byte flags, 
                                    byte handle, long fileid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryFileCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tfileid[" +fileid+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_FILE);
   }
    
   public void fireAddItemToPackageCommand(DSMPBaseHandler h, byte flags, 
                                           byte handle, long packid, 
                                           long itemid) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireAddItemToPackageCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid[" +packid+"]");
         System.out.println("\titemid[" +itemid+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_ADD_ITEM_TO_PACKAGE);
   }
    
   public void fireUploadFileToPackageCommand(DSMPBaseHandler h, byte flags, 
                                              byte handle, boolean tryRestart, 
                                              long packid,
                                              String md5, long md5Size,
                                              long filelen, String file) {
      if (printdebug && dodebug) {
         System.out.println("-----fireUploadFileToPackageCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\trestart[" +tryRestart+"]");
         System.out.println("\tpackid [" +packid+"]");
         System.out.println("\tmd5    [" +md5+"]");
         System.out.println("\tmd5len [" +md5Size+"]");
         System.out.println("\tlen    [" +filelen+"]");
         System.out.println("\tfilenam[" +file+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_UPLOAD_FILE_TO_PACKAGE);
   }
    
   public void fireRemoveItemFromPackageCommand(DSMPBaseHandler h, byte flags, 
                                                byte handle, long packid,
                                                long itemid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireRemoveItemFromPackageCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid[" +packid+"]");
         System.out.println("\titemid[" +itemid+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_REMOVE_ITEM_FROM_PACKAGE);
   }
    
   public void fireDownloadPackageItemCommand(DSMPBaseHandler h, byte flags, 
                                              byte handle, boolean tryRestart, 
                                              boolean trySync,
                                              long packid, String md5, 
                                              long filelen, long fileid) { 
      if (printdebug && dodebug) {
         System.out.println("-----fireDownloadPackageItemCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\trestart[" +tryRestart+"]");
         System.out.println("\tsynched[" +trySync+"]");
         System.out.println("\tpackid [" +packid+"]");
         System.out.println("\tmd5    [" +md5+"]");
         System.out.println("\tlen    [" +filelen+"]");
         System.out.println("\tfileid [" +fileid+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_DOWNLOAD_PACKAGE_ITEM);
   }
   
   public void fireDownloadPackageCommand(DSMPBaseHandler h, byte flags, 
                                          byte handle, boolean trySync,
                                          long packid, String encoding) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDownloadPackageCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tsynched[" +trySync+"]");
         System.out.println("\tpackid [" +packid+"]");
         System.out.println("\tencode [" +encoding+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_DOWNLOAD_PACKAGE);
   }
    
   public void fireAddPackageAclCommand(DSMPBaseHandler h, byte flags, 
                                        byte handle, byte acltype,
                                        long packid, String aclname) {
      if (printdebug && dodebug) {
         System.out.println("-----fireAddPackageAclCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tacltype[" +acltype+"]");
         System.out.println("\tpackid [" +packid+"]");
         System.out.println("\taclname[" +aclname+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_ADD_PACKAGE_ACL);
   }
    
   public void fireRemovePackageAclCommand(DSMPBaseHandler h, byte flags, 
                                           byte handle, byte acltype,
                                           long packid, String aclname) {
      if (printdebug && dodebug) {
         System.out.println("-----fireRemovePackageAclCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tacltype[" +acltype+"]");
         System.out.println("\tpackid [" +packid+"]");
         System.out.println("\taclname[" +aclname+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_REMOVE_PACKAGE_ACL);
   }
    
   public void fireChangePackageExpirationCommand(DSMPBaseHandler h, 
                                                  byte flags, byte handle, 
                                                  long packid, long expire) {
      if (printdebug && dodebug) {
         System.out.println("-----fireChangePackageExpirationCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid [" +packid+"]");
         System.out.println("\texpire [" +expire+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CHANGE_PACKAGE_EXPIRATION);
   }
   
   public void fireGetProjectList(DSMPBaseHandler h, byte flags, byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireGetProjectList: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_GET_PROJECTLIST);
   }
   
   public void fireMarkPackageCommand(DSMPBaseHandler h, byte flags, 
                                      byte handle, long packid, boolean mark) {
      if (printdebug && dodebug) {
         System.out.println("-----fireMarkPackageCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid[" +packid+"]");
         System.out.println("\tmark[" +mark+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_MARK_PACKAGE);
   }
   
   public void fireSendNewFrameCommand(DSMPBaseHandler h, byte flags, 
                                       byte handle, int opid, 
                                       int num) {
      if (printdebug && dodebug) {
         System.out.println("-----fireSendNewFrameCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\topid[" +opid+"]");
         System.out.println("\tnum[" +num+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_SEND_NEW_FRAME);
   }
   
   public void fireNegotiateProtocolVersionCommand(DSMPBaseHandler h, 
                                                   byte flags, byte handle, 
                                                   int ver) {
      if (printdebug && dodebug) {
         System.out.println("-----fireNegotiateProtocolVersionCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tversion[" +ver+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_PROTO_VERSION);
   }
   
   public void fireManageOptionsCommand(DSMPBaseHandler h, 
                                        byte flags, byte handle, 
                                        boolean doGet, 
                                        Hashtable set,
                                        Vector get) {
      if (printdebug && dodebug) {
         System.out.println("-----fireManageOptionsCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tdoGet[" +doGet+"]");
         System.out.println("\tnumToSet[" +(set!=null?set.size():0)+"]");
         System.out.println("\tnumToGet[" +(get!=null?get.size():0)+"]");
         if (set != null && set.size() > 0) {
            Enumeration enum = set.keys();
            while(enum.hasMoreElements()) {
               String k = (String)enum.nextElement();
               String v = (String)set.get(k);
               System.out.println("\t\t" + k + "=" + v);
            }
            
         }
         if (get != null && get.size() > 0) {
            Enumeration enum = get.elements();
            while(enum.hasMoreElements()) {
               String k = (String)enum.nextElement();
               System.out.println("\t\t" + k);
            }
            
         }
      }
      uncaughtProtocol(h, DropboxGenerator.OP_MANAGE_OPTIONS);
   }

   public void fireCreateGroupCommand(DSMPBaseHandler h, byte flags, 
                                      byte handle, String groupname,
                                      byte visibility, 
                                      byte listability) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireCreateGroupCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tgroupname  [" +groupname+"]");
         System.out.println("\tvisibility [" +visibility+"]");
         System.out.println("\tlistability[" +listability+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CREATE_GROUP);
   }
    
   public void fireDeleteGroupCommand(DSMPBaseHandler h, byte flags, 
                                      byte handle, String groupname) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDeleteGroupCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tgroupname[" +groupname+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_DELETE_GROUP);
   }
   
   public void fireModifyGroupAclCommand(DSMPBaseHandler h, byte flags, 
                                         byte handle, 
                                         boolean memberOrAccess,
                                         boolean addOrRemove,
                                         String groupname, String username) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAclCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tgroupname     [" +groupname+"]");
         System.out.println("\tusername      [" +username+"]");
         System.out.println("\tmemberOrAccess[" +memberOrAccess+"]");
         System.out.println("\taddOrRemove   [" +addOrRemove+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_MODIFY_GROUP_ACL);
   }
   
   public void fireModifyGroupAttributeCommand(DSMPBaseHandler h, byte flags, 
                                               byte handle, String groupname,
                                               byte visibility, 
                                               byte listability) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAttributeCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tgroupname  [" +groupname+"]");
         System.out.println("\tvisibility [" +visibility+"]");
         System.out.println("\tlistability[" +listability+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_MODIFY_GROUP_ATTRIBUTES);
   }
   
   public void fireQueryGroupsCommand(DSMPBaseHandler h, byte flags,
                                      byte handle, 
                                      boolean regexSearch,
                                      boolean wantMember,
                                      boolean wantAccess,
                                      String groupname) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryGroupsCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tgroupname  [" +groupname+"]");
         System.out.println("\tregexSearch[" +regexSearch+"]");
         System.out.println("\twantMember [" +wantMember+"]");
         System.out.println("\twantAccess [" +wantAccess+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_GROUPS);
   }
   
   public void fireSetPackageOptionCommand(DSMPBaseHandler h, byte flags,
                                           byte handle, 
                                           long    pkgid,
                                           int     pkgmask,
                                           int     pkgvals) {
                                           
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireSetPackageOptionCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpkgid[" +pkgid+"]");
         System.out.println("\tpkgmsk [" +pkgmask+"]");
         System.out.println("\tpkgvals [" +pkgvals+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_SET_PACKAGE_OPTION);
   }
   
   public void fireGetStoragePoolInstanceCommand(DSMPBaseHandler h, byte flags,
                                                 byte handle, 
                                                 long poolid) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireGetStoragePoolInstanceCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpoolid[" +poolid+"]");
         
      }
      uncaughtProtocol(h, DropboxGenerator.OP_GET_STORAGEPOOL_INSTANCE);
   }
   
   public void fireQueryStoragePoolInfoCommand(DSMPBaseHandler h, byte flags,
                                               byte handle) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryStoragePoolInfoCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_STORAGEPOOL_INFO);
   }
   
   
  /* -------------------------------------------------------*\
  ** Replies
  \* -------------------------------------------------------*/
   
  /* -=-=-=-=-=-=-=-=-=- Start of DropBox Replies -=-=-=-=-=-=-=-=-=-=- */
   public void fireCreatePackageReply(DSMPBaseHandler h, 
                                      byte flags, byte handle, 
                                      long id) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireCreatePackageReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid[" +id+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CREATE_PACKAGE_REPLY);
   }

   public void fireDeletePackageReply(DSMPBaseHandler h, 
                                      byte flags, byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireDeletePackageReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_DELETE_PACKAGE_REPLY);
   }

   public void fireCommitPackageReply(DSMPBaseHandler h, 
                                      byte flags, byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireCommitPackageReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_COMMIT_PACKAGE_REPLY);
   }

   public void fireQueryPackagesReply(DSMPBaseHandler h, 
                                      byte flags, byte handle, 
                                      boolean ownerOrAccessor, 
                                      Vector vec) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackagesReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\townOrAcc[" +ownerOrAccessor+"]");
         if (vec != null) {
            Enumeration enum = vec.elements();
            while(enum.hasMoreElements()) {
               PackageInfo info = (PackageInfo)enum.nextElement();
               System.out.println("\t\tpackname[" +info.getPackageName()+"]");
               System.out.println("\t\tpackown [" +info.getPackageOwner()+"]");
               System.out.println("\t\tpackcomp[" +info.getPackageCompany()+"]");
               System.out.println("\t\tpackid  [" +info.getPackageId()+"]");
               System.out.println("\t\tpack#elm[" +info.getPackageNumElements()+"]");
               System.out.println("\t\tpackstat[" +info.getPackageStatus()+"]");
               System.out.println("\t\tpackexpr[" +info.getPackageExpiration()+"]");
               System.out.println("\t\tpaccreat[" +info.getPackageCreation()+"]");
               System.out.println("\t\tpaccomit[" +info.getPackageCommitted()+"]");
               System.out.println("\t\tpackpool[" +info.getPackagePoolId()+"]");
               System.out.println("\t\tpacksize[" +info.getPackageSize()+"]");
               System.out.println("\t\tpackflgs[" +info.getPackageFlags()+"]");
               System.out.println("\t-----");
            }
         }
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGES_REPLY);
   }

   public void fireQueryPackageReply(DSMPBaseHandler h, 
                                     byte flags, byte handle, 
                                     PackageInfo info) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\t\tpackname[" +info.getPackageName()+"]");
         System.out.println("\t\tpackown [" +info.getPackageOwner()+"]");
         System.out.println("\t\tpackcomp[" +info.getPackageCompany()+"]");
         System.out.println("\t\tpackid  [" +info.getPackageId()+"]");
         System.out.println("\t\tpack#elm[" +info.getPackageNumElements()+"]");
         System.out.println("\t\tpackstat[" +info.getPackageStatus()+"]");
         System.out.println("\t\tpackexpr[" +info.getPackageExpiration()+"]");
         System.out.println("\t\tpaccreat[" +info.getPackageCreation()+"]");
         System.out.println("\t\tpaccomit[" +info.getPackageCommitted()+"]");
         System.out.println("\t\tpackpool[" +info.getPackagePoolId()+"]");
         System.out.println("\t\tpacksize[" +info.getPackageSize()+"]");
         System.out.println("\t\tpackflgs[" +info.getPackageFlags()+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE_REPLY);
   }

   public void fireQueryPackageContentsReply(DSMPBaseHandler h, 
                                             byte flags, byte handle, 
                                             long packid,
                                             Vector vec) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageContentsReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tpackid[" +packid+"]");
         if (vec != null) {
            Enumeration enum = vec.elements();
            while(enum.hasMoreElements()) {
               FileInfo info = (FileInfo)enum.nextElement();
               System.out.println("\t\tfilename[" +info.getFileName()+"]");
               System.out.println("\t\tfilestat[" +info.getFileStatus()+"]");
               System.out.println("\t\tfileid  [" +info.getFileId()+"]");
               System.out.println("\t\tfilesize[" +info.getFileSize()+"]");
               System.out.println("\t\tfilemd5 [" +info.getFileMD5()+"]");
               System.out.println("\t\tfilcreat[" +info.getFileCreation()+"]");
               System.out.println("\t-----");
            }
         }
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE_CONTENTS_REPLY);
   }

   public void fireQueryPackageAclsReply(DSMPBaseHandler h, 
                                         byte flags, byte handle, 
                                         Vector vec, boolean staticOnly) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageAclsReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tstaticOnly[" +staticOnly+"]");
         if (vec != null) {
            Enumeration enum = vec.elements();
            while(enum.hasMoreElements()) {
               AclInfo info = (AclInfo)enum.nextElement();
               System.out.println("\t\taclstat[" +info.getAclStatus()+"]");
               System.out.println("\t\talcuser[" +info.getAclName()+"]");
               System.out.println("\t\taclproj[" +info.getAclProjectName()+"]");
               System.out.println("\t\tcompany[" +info.getAclCompany()+"]");
               System.out.println("\t-----");
            }
         }
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE_ACLS_REPLY);
   }

   public void fireQueryPackageFileAclsReply(DSMPBaseHandler h, 
                                             byte flags, byte handle, 
                                             Vector vec) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageFileAclsReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         if (vec != null) {
            Enumeration enum = vec.elements();
            while(enum.hasMoreElements()) {
               AclInfo info = (AclInfo)enum.nextElement();
               System.out.println("\t\taclstat[" +info.getAclStatus()+"]");
               System.out.println("\t\talcuser[" +info.getAclName()+"]");
               System.out.println("\t\tcompany[" +info.getAclCompany()+"]");
               System.out.println("\t-----");
            }
         }
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE_FILE_ACLS_REPLY);
   }

   public void fireQueryFilesReply(DSMPBaseHandler h, 
                                   byte flags, byte handle,
                                   boolean ownerAccessor, Vector vec) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryFilesReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\townAccess[" +ownerAccessor+"]");
         
         if (vec != null) {
            Enumeration enum = vec.elements();
            while(enum.hasMoreElements()) {
               FileInfo info = (FileInfo)enum.nextElement();
               System.out.println("\t\tfilename[" +info.getFileName()+"]");
               System.out.println("\t\tfilestat[" +info.getFileStatus()+"]");
               System.out.println("\t\tfileid  [" +info.getFileId()+"]");
               System.out.println("\t\tfilesize[" +info.getFileSize()+"]");
               System.out.println("\t\tfileMD5 [" +info.getFileMD5()+"]");
               System.out.println("\t\tfilcreat[" +info.getFileCreation()+"]");
               System.out.println("\t-----");
            }
         }
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_FILES_REPLY);
   }

   public void fireQueryFileReply(DSMPBaseHandler h, 
                                  byte flags, byte handle, 
                                  FileInfo info, Vector vec) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryFileReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tfilename[" +info.getFileName()+"]");
         System.out.println("\tfileid  [" +info.getFileId()+"]");
         System.out.println("\tfilestat[" +info.getFileStatus()+"]");
         System.out.println("\tfilesize[" +info.getFileSize()+"]");
         System.out.println("\tfileMD5 [" +info.getFileMD5()+"]");
         
         if (vec != null) {
            Enumeration enum = vec.elements();
            while(enum.hasMoreElements()) {
               Long l = (Long)enum.nextElement();
               System.out.println("\t\tpackid[" +l.longValue()+"]");
            }
            System.out.println("\t-----");
         }
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_FILE_REPLY);
   }

   public void fireAddItemToPackageReply(DSMPBaseHandler h, 
                                         byte flags, byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireAddItemToPackageReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_ADD_ITEM_TO_PACKAGE_REPLY);
   }

   public void fireUploadFileToPackageReply(DSMPBaseHandler h, 
                                            byte flags, byte handle, 
                                            long itemid, boolean isRestarted,
                                            int opid, long ofs) {
      if (printdebug && dodebug) {
         System.out.println("-----fireUploadFileToPackageReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\titemid   [" +itemid+ "]");
         System.out.println("\tisRestart[" +isRestarted+ "]");
         System.out.println("\topid     [" +opid+ "]");
         System.out.println("\tofs      [" +ofs+ "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_UPLOAD_FILE_TO_PACKAGE_REPLY);
   }

   public void fireRemoveItemFromPackageReply(DSMPBaseHandler h, 
                                              byte flags, byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireRemoveItemFromPackageReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_REMOVE_ITEM_FROM_PACKAGE_REPLY);
   }

   public void fireDownloadPackageItemReply(DSMPBaseHandler h, 
                                            byte flags, byte handle, 
                                            int opid, long ofs, long size) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireDownloadPackageItemReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\topid[" +opid+ "]");
         System.out.println("\tofs [" +ofs+ "]");
         System.out.println("\tsize[" +size+ "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_DOWNLOAD_PACKAGE_ITEM_REPLY);
   }
   
   public void fireDownloadPackageReply(DSMPBaseHandler h, 
                                        byte flags, byte handle, 
                                        int opid, String encoding) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireDownloadPackageReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\topid  [" +opid+ "]");
         System.out.println("\tencode[" +encoding+ "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_DOWNLOAD_PACKAGE_REPLY);
   }

   public void fireAddPackageAclReply(DSMPBaseHandler h, 
                                      byte flags, byte handle) { 
      
      if (printdebug && dodebug) {
         System.out.println("-----fireAddPackageAclReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_ADD_PACKAGE_ACL_REPLY);
   }

   public void fireRemovePackageAclReply(DSMPBaseHandler h, 
                                         byte flags, byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireRemovePackageAclReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_REMOVE_PACKAGE_ACL_REPLY);
   }

   public void fireChangePackageExpirationReply(DSMPBaseHandler h, 
                                                byte flags, byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireChangePackageExpirationReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CHANGE_PACKAGE_EXPIRATION_REPLY);
   }
   
   public void fireGetProjectListReply(DSMPBaseHandler h, 
                                       byte flags, byte handle, 
                                       String user, String company,
                                       Vector vec) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireGetProjectListReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tuser      [" +user+ "]");
         System.out.println("\tcompany   [" +company+ "]");
         
         if (vec != null) {
            Enumeration enum = vec.elements();
            while(enum.hasMoreElements()) {
               String s = (String)enum.nextElement();
               System.out.println("\t\t[" +s+"]");
            }
            System.out.println("\t-----");
         }
      }
      uncaughtProtocol(h, DropboxGenerator.OP_GET_PROJECTLIST_REPLY);
   }
   
   public void fireMarkPackageReply(DSMPBaseHandler h, 
                                    byte flags, byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireMarkPackageReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_MARK_PACKAGE_REPLY);
   }

   public void fireNegotiateProtocolVersionReply(DSMPBaseHandler h, 
                                                byte flags, 
                                                byte handle, 
                                                int ver) {
      if (printdebug && dodebug) {
         System.out.println("-----fireNegotiateProtocolVersionReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tversion[" +ver+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_PROTO_VERSION_REPLY);
   }
   
   public void fireManageOptionsReply(DSMPBaseHandler h, 
                                      byte flags, byte handle, 
                                      boolean didGet, boolean fullGet,
                                      Hashtable get) {
      if (printdebug && dodebug) {
         System.out.println("-----fireManageOptionsReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tdidGet[" +didGet+"]");
         System.out.println("\tfullGet[" +fullGet+"]");
         System.out.println("\tnumGotten[" +(get!=null?get.size():0)+"]");
         if (get != null && get.size() > 0) {
            Enumeration enum = get.keys();
            while(enum.hasMoreElements()) {
               String k = (String)enum.nextElement();
               String v = (String)get.get(k);
               System.out.println("\t\t" + k + "=" + v);
            }
            
         }
      }
      uncaughtProtocol(h, DropboxGenerator.OP_MANAGE_OPTIONS_REPLY);
   }
   
   
   public void fireCreateGroupReply(DSMPBaseHandler h, byte flags,
                                    byte handle) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireCreateGroupReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CREATE_GROUP_REPLY);
   }
    
   public void fireDeleteGroupReply(DSMPBaseHandler h, byte flags, 
                                    byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDeleteGroupReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_DELETE_GROUP_REPLY);
   }
   
   public void fireModifyGroupAclReply(DSMPBaseHandler h, byte flags, 
                                       byte handle) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAclReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_MODIFY_GROUP_ACL_REPLY);
   }
   
   public void fireModifyGroupAttributeReply(DSMPBaseHandler h, byte flags, 
                                             byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAttributeReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_MODIFY_GROUP_ATTRIBUTES_REPLY);
   }
   
   public void fireQueryGroupsReply(DSMPBaseHandler h, byte flags,
                                    byte handle, 
                                    boolean includesMember,
                                    boolean includesAccess,
                                    Vector groups) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryGroupsReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tincludesMember[" +includesMember+"]");
         System.out.println("\tincludesAccess[" +includesAccess+"]");
         if (groups != null) {
            System.out.println("\tNumGroups[" +groups.size()+"]");
            Enumeration enum = groups.elements();
            while(enum.hasMoreElements()) {
               GroupInfo gi = (GroupInfo)enum.nextElement();
               System.out.println("\t --------------------------------");
               System.out.println("\t  groupname [" +gi.getGroupName()+"]");
               System.out.println("\t  groupowner[" +gi.getGroupOwner()+"]");
               System.out.println("\t  grpcompany[" +gi.getGroupCompany()+"]");
               System.out.println("\t  created   [" +gi.getGroupCreated()+"]");
               System.out.println("\t  visible   [" +
                                  gi.getGroupVisibility()+"]");
               System.out.println("\t  listable  ["+
                                  gi.getGroupListability()+"]");
               System.out.println("\t  membersValid["+
                                  gi.getGroupMembersValid()+"]");
               System.out.println("\t  accessValid["+
                                  gi.getGroupAccessValid()+"]");
               Vector members = gi.getGroupMembers();
               
               System.out.println("\t  members   [" +
                                  ((members!=null)?members.size():0)+"]");
               if (members != null) {
                  Enumeration venum = members.elements();
                  while(venum.hasMoreElements()) {
                     String v = (String)venum.nextElement();
                     System.out.println("\t     " + v);
                  }
               }
               Vector access = gi.getGroupAccess();
               System.out.println("\t  access    [" +
                                  ((access!=null)?access.size():0)+"]");
               if (access != null) {
                  Enumeration venum = access.elements();
                  while(venum.hasMoreElements()) {
                     String v = (String)venum.nextElement();
                     System.out.println("\t     " + v);
                  }
               }
            }
         }
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_GROUPS_REPLY);
   }
   
   public void fireSetPackageOptionReply(DSMPBaseHandler h, byte flags, 
                                         byte handle, int newpkgflags) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireSetPackageOptionReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tnewpkgflags[" +newpkgflags+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_SET_PACKAGE_OPTION_REPLY);
   }
      
   public void fireGetStoragePoolInstanceReply(DSMPBaseHandler h, byte flags,
                                               byte handle, 
                                               PoolInfo pool) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireGetStoragePoolInstanceReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println(Nester.nest(pool.toString()));
         
      }
      uncaughtProtocol(h, DropboxGenerator.OP_GET_STORAGEPOOL_INSTANCE_REPLY);
   }
   
   public void fireQueryStoragePoolInfoReply(DSMPBaseHandler h, byte flags,
                                             byte handle, 
                                             Vector v) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryStoragePoolInfoReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      
      System.out.println("\tNum Pools[" +v.size()+"]");
      
      Iterator it = v.iterator();
      int i=0;
      while(it.hasNext()) {
         PoolInfo pool = (PoolInfo)it.next();
         System.out.println(""+i + Nester.nest(pool.toString()));
      }
      
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_STORAGEPOOL_INFO_REPLY);
   }
   
  /* -------------------------------------------------------*\
  ** Reply Errors
  \* -------------------------------------------------------*/
  
   
  /* -=-=-=-=-=-=-=-=-=- Start of DropBox reply errors -=-=-=-=-=-=-=-=-=- */
   
   public void fireCreatePackageReplyError(DSMPBaseHandler h, 
                                           byte flags, byte handle, 
                                           short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireCreatePackageReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CREATE_PACKAGE_REPLY);
   }

   public void fireDeletePackageReplyError(DSMPBaseHandler h, 
                                           byte flags, byte handle, 
                                           short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireDeletePackageReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_DELETE_PACKAGE_REPLY);
   }

   public void fireCommitPackageReplyError(DSMPBaseHandler h, 
                                           byte flags, byte handle, 
                                           short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireCommitPackageReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_COMMIT_PACKAGE_REPLY);
   }

   public void fireQueryPackagesReplyError(DSMPBaseHandler h, 
                                           byte flags, byte handle, 
                                           short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackagesReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGES_REPLY);
   }

   public void fireQueryPackageReplyError(DSMPBaseHandler h, 
                                          byte flags, byte handle, 
                                          short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE_REPLY);
   }

   public void fireQueryPackageContentsReplyError(DSMPBaseHandler h, 
                                                  byte flags, byte handle, 
                                                  short errorcode, 
                                                  String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageContentsReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE_CONTENTS_REPLY);
   }

   public void fireQueryPackageAclsReplyError(DSMPBaseHandler h, 
                                              byte flags, byte handle, 
                                              short errorcode, 
                                              String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageAclsReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE_ACLS_REPLY);
   }

   public void fireQueryPackageFileAclsReplyError(DSMPBaseHandler h, 
                                                  byte flags, byte handle, 
                                                  short errorcode, 
                                                  String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryPackageFileAclsReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_PACKAGE_FILE_ACLS_REPLY);
   }

   public void fireQueryFilesReplyError(DSMPBaseHandler h, 
                                        byte flags, byte handle, 
                                        short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryFilesReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_FILES_REPLY);
   }

   public void fireQueryFileReplyError(DSMPBaseHandler h, 
                                       byte flags, byte handle, 
                                       short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryFileReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_FILE_REPLY);
   }

   public void fireAddItemToPackageReplyError(DSMPBaseHandler h, 
                                              byte flags, byte handle, 
                                              short errorcode, 
                                              String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireAddItemToPackageReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_ADD_ITEM_TO_PACKAGE_REPLY);
   }

   public void fireUploadFileToPackageReplyError(DSMPBaseHandler h, 
                                                 byte flags, byte handle, 
                                                 short errorcode, 
                                                 String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireUploadFileToPackageReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_UPLOAD_FILE_TO_PACKAGE_REPLY);
   }

   public void fireRemoveItemFromPackageReplyError(DSMPBaseHandler h, 
                                                   byte flags, byte handle, 
                                                   short errorcode, 
                                                   String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireRemoveItemFromPackageReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_REMOVE_ITEM_FROM_PACKAGE_REPLY);
   }

   public void fireDownloadPackageItemReplyError(DSMPBaseHandler h, 
                                                 byte flags, byte handle, 
                                                 short errorcode, 
                                                 String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireDownloadPackageItemReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_DOWNLOAD_PACKAGE_ITEM_REPLY);
   }
   
   public void fireDownloadPackageReplyError(DSMPBaseHandler h, 
                                             byte flags, byte handle, 
                                             short errorcode, 
                                             String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireDownloadPackageReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_DOWNLOAD_PACKAGE_REPLY);
   }

   public void fireAddPackageAclReplyError(DSMPBaseHandler h, 
                                           byte flags, byte handle, 
                                           short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireAddPackageAclReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_ADD_PACKAGE_ACL_REPLY);
   }

   public void fireRemovePackageAclReplyError(DSMPBaseHandler h, 
                                              byte flags, byte handle, 
                                              short errorcode, 
                                              String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireRemovePackageAclReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_REMOVE_PACKAGE_ACL_REPLY);
   }
   public void fireChangePackageExpirationReplyError(DSMPBaseHandler h, 
                                                     byte flags, byte handle, 
                                                     short errorcode, 
                                                     String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireChangePackageExpirationReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CHANGE_PACKAGE_EXPIRATION_REPLY);
   }
   public void fireGetProjectListReplyError(DSMPBaseHandler h, 
                                            byte flags, byte handle, 
                                            short errorcode, 
                                            String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireGetProjectListReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_GET_PROJECTLIST_REPLY);
   }
   
   public void fireMarkPackageReplyError(DSMPBaseHandler h, 
                                         byte flags, byte handle, 
                                         short errorcode, 
                                         String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireMarkPackageReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_MARK_PACKAGE_REPLY);
   }
   public void fireNegotiateProtocolVersionReplyError(DSMPBaseHandler h, 
                                                      byte flags, byte handle, 
                                                      short errorcode, 
                                                      String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireNegotiateProtocolVersionReplyError: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_PROTO_VERSION_REPLY);
   }
       
   public void fireManageOptionsReplyError(DSMPBaseHandler h, 
                                           byte flags, byte handle, 
                                           short errorcode, 
                                           String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireManageOptionsReplyError: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_MANAGE_OPTIONS_REPLY);
   }
     
   public void fireCreateGroupReplyError(DSMPBaseHandler h, 
                                         byte flags, byte handle,
                                         short errorcode,
                                         String errorStr) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireCreateGroupReplyError: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CREATE_GROUP_REPLY);
   }
   public void fireDeleteGroupReplyError(DSMPBaseHandler h, 
                                         byte flags, byte handle,
                                         short errorcode,
                                         String errorStr) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireDeleteGroupReplyError: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CREATE_GROUP_REPLY);
   }
   public void fireModifyGroupAclReplyError(DSMPBaseHandler h, 
                                            byte flags, byte handle,
                                            short errorcode,
                                            String errorStr) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAclReplyError: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CREATE_GROUP_REPLY);
   }
   public void fireModifyGroupAttributeReplyError(DSMPBaseHandler h, 
                                                  byte flags, byte handle,
                                                  short errorcode,
                                                  String errorStr) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAttributeReplyError: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CREATE_GROUP_REPLY);
   }
   public void fireQueryGroupsReplyError(DSMPBaseHandler h, 
                                         byte flags, byte handle,
                                         short errorcode,
                                         String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryGroupsReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_CREATE_GROUP_REPLY);
   }
      
   public void fireSetPackageOptionReplyError(DSMPBaseHandler h, byte flags, 
                                              byte handle, 
                                              short errorcode,
                                              String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireSetPackageOptionReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_SET_PACKAGE_OPTION_REPLY);
   }
      
   public void fireGetStoragePoolInstanceReplyError(DSMPBaseHandler h, byte flags, 
                                                    byte handle, 
                                                    short errorcode,
                                                    String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireGetStoragePoolInstanceReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_GET_STORAGEPOOL_INSTANCE_REPLY);
   }
      
   public void fireQueryStoragePoolInfoReplyError(DSMPBaseHandler h, byte flags, 
                                                  byte handle, 
                                                  short errorcode,
                                                  String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryStoragePoolInfoReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_QUERY_STORAGEPOOL_INFO_REPLY);
   }
      
  /* -------------------------------------------------------*\
  ** Events
  \* -------------------------------------------------------*/

   
  /* -------------------------------------------------------*\
  ** Errors
  \* -------------------------------------------------------*/
   
   
  /* -------------------------------------------------------*\
  ** Checking and Dispatching
  \* -------------------------------------------------------*/
  
   public void dispatchProtocolI(DSMPBaseProto proto, 
                                 DSMPBaseHandler handler, 
                                 boolean doDispatch) 
                                            throws InvalidProtocolException {
                                            
      byte opcode = proto.getOpcode();
      byte flags  = proto.getFlags();
      byte handle = proto.getHandle();
      proto.resetCursor();
      
     // If the caller had originally asked to be synchronized, lets manage that
     //  ... but not if we are printing debug info
      boolean syncproto = redispatch && ((flags & 0x40) != 0);
      SynchArguments sargs = null;
      
      if (!DropboxGenerator.isValid(opcode)) {
         throw new InvalidProtocolException("Invalid opcode: " + opcode);
      }
      
      if (printdebug && dodebug && 
          redispatch && !FTPGenerator.isValid(opcode)) {
          
         try {
            if (pdbg == null) {
               
               pdbg = 
                  (DSMPDispatchBase)this.getClass().getSuperclass().newInstance();
               pdbg.setDebug(true);
               pdbg.setRedispatch(false);
            }
            
            if (bigdebug) {
               System.out.println("==> " + proto.toString());
            }
            pdbg.dispatchProtocolI(proto, null, true);
            proto.resetCursor();
         } catch(Throwable tttt) {
            System.out.println("Error dispatching Debug info = " + tttt.toString());
            tttt.printStackTrace(System.out);
         }
         proto.resetCursor();
      }
      
      if (DropboxGenerator.isReply(opcode)) {
      
         boolean success = (flags & (byte)0x01) != 0;
         if (!success) {
            short    errorcode = proto.getShort();
            String errorString = proto.getString8();
            
            proto.verifyCursorDone();
            if (doDispatch) {
               
               if (syncproto) {
                  sargs = new SynchArguments(handler, proto);
                  sargs.addElement(new Short(errorcode));
                  sargs.addElement(errorString);
               }
               
               switch(opcode) {
                  case DropboxGenerator.OP_CREATE_PACKAGE_REPLY: {
                     if (syncproto) {
                        handleSynchProto(sargs);
                     } else {
                        fireCreatePackageReplyError(handler, flags, handle,
                                                    errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_DELETE_PACKAGE_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireDeletePackageReplyError(handler, flags, handle,
                                                    errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_COMMIT_PACKAGE_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireCommitPackageReplyError(handler, flags, handle,
                                                    errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_QUERY_PACKAGES_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireQueryPackagesReplyError(handler, flags, handle,
                                                    errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_QUERY_PACKAGE_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireQueryPackageReplyError(handler, flags, handle,
                                                   errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_QUERY_PACKAGE_CONTENTS_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireQueryPackageContentsReplyError(handler, flags,
                                                           handle, errorcode, 
                                                           errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_QUERY_PACKAGE_ACLS_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireQueryPackageAclsReplyError(handler, flags, handle,
                                                       errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_QUERY_PACKAGE_FILE_ACLS_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireQueryPackageFileAclsReplyError(handler, flags, 
                                                           handle, errorcode, 
                                                           errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_QUERY_FILES_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireQueryFilesReplyError(handler, flags, handle,
                                                 errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_QUERY_FILE_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireQueryFileReplyError(handler, flags, handle,
                                                errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_ADD_ITEM_TO_PACKAGE_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireAddItemToPackageReplyError(handler, flags, handle,
                                                       errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_UPLOAD_FILE_TO_PACKAGE_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireUploadFileToPackageReplyError(handler, flags,
                                                          handle, errorcode, 
                                                          errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_REMOVE_ITEM_FROM_PACKAGE_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireRemoveItemFromPackageReplyError(handler, flags, 
                                                            handle, errorcode, 
                                                            errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_DOWNLOAD_PACKAGE_ITEM_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireDownloadPackageItemReplyError(handler, flags,
                                                          handle, errorcode, 
                                                          errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_DOWNLOAD_PACKAGE_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireDownloadPackageReplyError(handler, flags,
                                                      handle, errorcode, 
                                                      errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_ADD_PACKAGE_ACL_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireAddPackageAclReplyError(handler, flags, handle,
                                                    errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_REMOVE_PACKAGE_ACL_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireRemovePackageAclReplyError(handler, flags, handle,
                                                       errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_CHANGE_PACKAGE_EXPIRATION_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireChangePackageExpirationReplyError(handler, flags, 
                                                              handle, 
                                                              errorcode, 
                                                              errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_GET_PROJECTLIST_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireGetProjectListReplyError(handler, flags, handle,
                                                     errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_MARK_PACKAGE_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireMarkPackageReplyError(handler, flags, handle,
                                                  errorcode, errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_PROTO_VERSION_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireNegotiateProtocolVersionReplyError(handler, flags, 
                                                               handle, 
                                                               errorcode, 
                                                               errorString);
                     }
                                                            
                     break;
                  }
                  case DropboxGenerator.OP_MANAGE_OPTIONS_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireManageOptionsReplyError(handler, flags, 
                                                    handle, errorcode, 
                                                    errorString);
                     }
                     break;
                  }
                  
                  case DropboxGenerator.OP_CREATE_GROUP_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireCreateGroupReplyError(handler, flags, 
                                                  handle, errorcode, 
                                                  errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_DELETE_GROUP_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireDeleteGroupReplyError(handler, flags, 
                                                  handle, errorcode, 
                                                  errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_MODIFY_GROUP_ACL_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireModifyGroupAclReplyError(handler, flags, 
                                                     handle, errorcode, 
                                                     errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_MODIFY_GROUP_ATTRIBUTES_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireModifyGroupAttributeReplyError(handler, flags, 
                                                           handle, errorcode, 
                                                           errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_QUERY_GROUPS_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else {
                        fireQueryGroupsReplyError(handler, flags, 
                                                  handle, errorcode, 
                                                  errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_SET_PACKAGE_OPTION_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else { 
                        fireSetPackageOptionReplyError(handler, flags, 
                                                       handle, errorcode, 
                                                       errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_GET_STORAGEPOOL_INSTANCE_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else { 
                        fireGetStoragePoolInstanceReplyError(handler, flags, 
                                                             handle, errorcode, 
                                                             errorString);
                     }
                     break;
                  }
                  case DropboxGenerator.OP_QUERY_STORAGEPOOL_INFO_REPLY: {
                     if (syncproto) { 
                        handleSynchProto(sargs);
                     } else { 
                        fireQueryStoragePoolInfoReplyError(handler, flags, 
                                                           handle, errorcode, 
                                                           errorString);
                     }
                     break;
                  }
                  
                  default:
                     super.dispatchProtocolI(proto, handler, doDispatch);
                     break;
               }
            }
         } else {
            switch(opcode) {
               case DropboxGenerator.OP_CREATE_PACKAGE_REPLY: {
                  long id = proto.getLong();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Long(id));
                        handleSynchProto(sargs);
                     } else {
                        fireCreatePackageReply(handler, flags, handle, id);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_DELETE_PACKAGE_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireDeletePackageReply(handler, flags, handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_COMMIT_PACKAGE_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireCommitPackageReply(handler, flags, handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_QUERY_PACKAGES_REPLY: {
                  int num = proto.getInteger();
                  Vector vec = new Vector();
                  for(int i=0 ; i < num; i++) {
                     PackageInfo info = new PackageInfo();
                     info.setPackageId(proto.getLong());
                     info.setPackageNumElements(proto.getInteger());
                     info.setPackageStatus(proto.getByte());
                     info.setPackageExpiration(proto.getLong());
                     if (DropboxGenerator.getProtocolVersion() >= 2) {
                        info.setPackageCreation(proto.getLong());
                     }
                     if (DropboxGenerator.getProtocolVersion() >= 7) {
                        info.setPackageCommitted(proto.getLong());
                     }
                     if (DropboxGenerator.getProtocolVersion() >= 8) {
                        info.setPackagePoolId(proto.getLong());
                     }
                     info.setPackageSize(proto.getLong());
                     info.setPackageName(proto.getString16());
                     info.setPackageOwner(proto.getString16());
                     info.setPackageCompany(proto.getString16());
                     if ((flags & (byte)0x4) != 0) {
                        info.setPackageFlags(proto.getByte());
                     }
                     vec.addElement(info);
                  }
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     boolean callerOwned = (flags & (byte)0x2) != 0;                  
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Boolean(callerOwned));
                        sargs.addElement(vec);
                        handleSynchProto(sargs);
                     } else {
                        fireQueryPackagesReply(handler, flags, handle,
                                               callerOwned, vec);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_QUERY_PACKAGE_REPLY: {
                  PackageInfo info = new PackageInfo();
                  info.setPackageId(proto.getLong());
                  info.setPackageNumElements(proto.getInteger());
                  info.setPackageStatus(proto.getByte());
                  info.setPackageExpiration(proto.getLong());
                  if (DropboxGenerator.getProtocolVersion() >= 2) {
                     info.setPackageCreation(proto.getLong());
                  }
                  if (DropboxGenerator.getProtocolVersion() >= 7) {
                     info.setPackageCommitted(proto.getLong());
                  }
                  if (DropboxGenerator.getProtocolVersion() >= 8) {
                     info.setPackagePoolId(proto.getLong());
                  }
                  info.setPackageSize(proto.getLong());
                  info.setPackageName(proto.getString16());
                  info.setPackageOwner(proto.getString16());
                  info.setPackageCompany(proto.getString16());
                  if ((flags & (byte)0x2) != 0) {
                     info.setPackageFlags(proto.getByte());
                  }
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(info);
                        handleSynchProto(sargs);
                     } else {
                        fireQueryPackageReply(handler, flags, handle, info);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_QUERY_PACKAGE_CONTENTS_REPLY: {
                  long packid  = proto.getLong();
                  int  num     = proto.getInteger();
                  Vector vec = new Vector();
                  for(int i=0 ; i < num; i++) {
                     FileInfo info = new FileInfo();
                     info.setFileName(proto.getString16());
                     info.setFileStatus(proto.getByte());
                     info.setFileId(proto.getLong());
                     info.setFileSize(proto.getLong());
                     info.setFileMD5(proto.getString8());
                     if (DropboxGenerator.getProtocolVersion() >= 2) {
                        info.setFileCreation(proto.getLong());
                     }
                     vec.addElement(info);
                  }
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Long(packid));
                        sargs.addElement(vec);
                        handleSynchProto(sargs);
                     } else {
                        fireQueryPackageContentsReply(handler, flags, handle, 
                                                      packid, vec);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_QUERY_PACKAGE_ACLS_REPLY: {
                  int  num   = proto.getInteger();
                  Vector vec = new Vector();
                  for(int i=0 ; i < num; i++) {
                     AclInfo info = new AclInfo();
                     info.setAclStatus(proto.getByte());
                     info.setAclName(proto.getString16());
                     info.setAclProjectName(proto.getString16());
                     info.setAclCompany(proto.getString16());
                     vec.addElement(info);
                  }
                  proto.verifyCursorDone();
                  
                  boolean staticAcls = (flags & 2) != 0; 
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(vec);
                        sargs.addElement(new Boolean(staticAcls));
                        handleSynchProto(sargs);
                     } else {
                        fireQueryPackageAclsReply(handler, flags, handle, 
                                                  vec, staticAcls);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_QUERY_PACKAGE_FILE_ACLS_REPLY: {
                  int  num   = proto.getInteger();
                  Vector vec = new Vector();
                  for(int i=0 ; i < num; i++) {
                     AclInfo info = new AclInfo();
                     info.setAclStatus(proto.getByte());
                     info.setAclName(proto.getString16());
                     info.setAclCompany(proto.getString16());
                     vec.addElement(info);
                  }
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(vec);
                        handleSynchProto(sargs);
                     } else {
                        fireQueryPackageFileAclsReply(handler, flags, handle, 
                                                      vec);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_QUERY_FILES_REPLY: {
                  int  num     = proto.getInteger();
                  Vector vec = new Vector();
                  for(int i=0 ; i < num; i++) {
                     FileInfo info = new FileInfo();
                     info.setFileName(proto.getString16());
                     info.setFileStatus(proto.getByte());
                     info.setFileId(proto.getLong());
                     info.setFileSize(proto.getLong());
                     info.setFileMD5(proto.getString8());
                     if (DropboxGenerator.getProtocolVersion() >= 2) {
                        info.setFileCreation(proto.getLong());
                     }
                     vec.addElement(info);
                  }
                  proto.verifyCursorDone();
                  
                  boolean callerOwned = (flags & (byte)2) != 0; 
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Boolean(callerOwned));
                        sargs.addElement(vec);
                        handleSynchProto(sargs);
                     } else {
                        fireQueryFilesReply(handler, flags, handle, 
                                            callerOwned, vec);
                     }
                  }                                         
                  break;
               }
               case DropboxGenerator.OP_QUERY_FILE_REPLY: {
                  
                  FileInfo info = new FileInfo();
                  info.setFileName(proto.getString16());
                  info.setFileStatus(proto.getByte());
                  
                  if (DropboxGenerator.getProtocolVersion() >= 7) {
                     info.setFileId(proto.getLong());
                  }
                  
                  info.setFileSize(proto.getLong());
                  info.setFileMD5(proto.getString8());
                  if (DropboxGenerator.getProtocolVersion() >= 2) {
                     info.setFileCreation(proto.getLong());
                  }
                  
                  int    num    = proto.getInteger();
                  
                  Vector vec = new Vector();
                  for(int i=0 ; i < num; i++) {
                     vec.addElement(new Long(proto.getLong()));
                  }
                  
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(info);
                        sargs.addElement(vec);
                        handleSynchProto(sargs);
                     } else {
                        fireQueryFileReply(handler, flags, handle, info, vec);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_ADD_ITEM_TO_PACKAGE_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireAddItemToPackageReply(handler, flags, handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_UPLOAD_FILE_TO_PACKAGE_REPLY: {
                  long itemid  = proto.getLong();
                  int  opid    = proto.getInteger();
                  long ofs     = proto.getLong();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     boolean restartable = (flags & (byte)2) != 0;                  
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Long(itemid));
                        sargs.addElement(new Boolean(restartable));
                        sargs.addElement(new Integer(opid));
                        sargs.addElement(new Long(ofs));
                        handleSynchProto(sargs);
                     } else {
                        fireUploadFileToPackageReply(handler, flags, handle, 
                                                     itemid, restartable,
                                                     opid, ofs);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_REMOVE_ITEM_FROM_PACKAGE_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireRemoveItemFromPackageReply(handler, flags, handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_DOWNLOAD_PACKAGE_ITEM_REPLY: {
                  int id   = proto.getInteger();
                  long ofs = proto.getLong();
                  long sz  = proto.getLong();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Integer(id));
                        sargs.addElement(new Long(ofs));
                        sargs.addElement(new Long(sz));
                        handleSynchProto(sargs);
                     } else {
                        fireDownloadPackageItemReply(handler, flags, handle, 
                                                     id, ofs, sz);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_DOWNLOAD_PACKAGE_REPLY: {
                  int id          = proto.getInteger();
                  String encoding = proto.getString16();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Integer(id));
                        sargs.addElement(encoding);
                        handleSynchProto(sargs);
                     } else {
                        fireDownloadPackageReply(handler, flags, handle, 
                                                 id, encoding);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_ADD_PACKAGE_ACL_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireAddPackageAclReply(handler, flags, handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_REMOVE_PACKAGE_ACL_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireRemovePackageAclReply(handler, flags, handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_CHANGE_PACKAGE_EXPIRATION_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireChangePackageExpirationReply(handler, flags, 
                                                         handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_GET_PROJECTLIST_REPLY: {
                  String user    = proto.getString16();
                  String company = proto.getString16();
                  int    num = proto.getInteger();
                  Vector vec = new Vector();
                  for(int i=0 ; i < num; i++) {
                     vec.addElement(proto.getString8());
                  }
               
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(user);
                        sargs.addElement(company);
                        sargs.addElement(vec);
                        handleSynchProto(sargs);
                     } else {
                        fireGetProjectListReply(handler, flags, handle, 
                                                user, company, vec);
                     }
                  }
                  break;
               }
               
               case DropboxGenerator.OP_MARK_PACKAGE_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireMarkPackageReply(handler, flags, handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_PROTO_VERSION_REPLY: {
                  int ver = proto.getInteger();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Integer(ver));
                        handleSynchProto(sargs);
                     } else {
                        fireNegotiateProtocolVersionReply(handler, flags, 
                                                          handle, ver);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_MANAGE_OPTIONS_REPLY: {
                  boolean didGet  = (flags & (byte)2) != 0;
                  boolean fullGet = (flags & (byte)4) != 0;
                  int numGet = proto.get3ByteInteger();
                  Hashtable get = new Hashtable();
                  while(numGet-- > 0) {
                     String k = proto.getString8();
                     String v = proto.getString16();
                     get.put(k, v);
                  }
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Boolean(didGet));
                        sargs.addElement(new Boolean(fullGet));
                        sargs.addElement(get);
                        handleSynchProto(sargs);
                     } else {
                        fireManageOptionsReply(handler, flags, handle, 
                                               didGet, fullGet, get);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_CREATE_GROUP_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireCreateGroupReply(handler, flags, handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_DELETE_GROUP_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireDeleteGroupReply(handler, flags, handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_MODIFY_GROUP_ACL_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireModifyGroupAclReply(handler, flags, handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_MODIFY_GROUP_ATTRIBUTES_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireModifyGroupAttributeReply(handler, flags, handle);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_QUERY_GROUPS_REPLY: {
                  int numgrps = proto.get3ByteInteger();
                  Vector vec = new Vector();
                  while(numgrps-- > 0) {
                     GroupInfo gi = new GroupInfo();
                     gi.setGroupName(proto.getString16());
                     gi.setGroupOwner(proto.getString16());
                     gi.setGroupCompany(proto.getString16());
                     gi.setGroupCreated(proto.getLong());
                     gi.setGroupVisibility(proto.getByte());
                     gi.setGroupListability(proto.getByte());
                     byte lflags = proto.getByte();
                     
                     int nummem = proto.get3ByteInteger();
                     Vector members = gi.getGroupMembers();
                     while(nummem-- > 0) {
                        members.addElement(proto.getString16());
                     }
                     gi.setGroupMembers(members);
                     gi.setGroupMembersValid((lflags & (byte)1) != (byte)0);
                     
                     int numacc = proto.get3ByteInteger();
                     Vector access = gi.getGroupAccess();
                     while(numacc-- > 0) {
                        access.addElement(proto.getString16());
                     }
                     gi.setGroupAccess(access);
                     gi.setGroupAccessValid((lflags & (byte)2) != (byte)0);
                     
                     vec.addElement(gi);
                  }
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     boolean memberIncluded = (flags & (byte)1) != (byte)0;
                     boolean accessIncluded = (flags & (byte)2) != (byte)0;
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Boolean(memberIncluded));
                        sargs.addElement(new Boolean(accessIncluded));
                        sargs.addElement(vec);
                        handleSynchProto(sargs);
                     } else {
                        fireQueryGroupsReply(handler, flags, handle, 
                                             memberIncluded, accessIncluded,
                                             vec);
                     }
                  }
                  break;
               }
               
               case DropboxGenerator.OP_SET_PACKAGE_OPTION_REPLY: {
                  int newflags = proto.getInteger();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Integer(newflags));
                        handleSynchProto(sargs);
                     } else {
                        fireSetPackageOptionReply(handler, flags, handle,
                                                  newflags);
                     }
                  }
                  break;
               }
               
               case DropboxGenerator.OP_GET_STORAGEPOOL_INSTANCE_REPLY: {
                  PoolInfo pool = new PoolInfo();
                  pool.setPoolName(proto.getString16());
                  pool.setPoolDescription(proto.getString16());
                  pool.setPoolId(proto.getLong());
                  pool.setPoolMaxDays(proto.getInteger());
                  pool.setPoolDefaultDays(proto.getInteger());
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(pool);
                        handleSynchProto(sargs);
                     } else { 
                        fireGetStoragePoolInstanceReply(handler, flags, handle, pool);
                     }
                  }
                  break;
               }
               case DropboxGenerator.OP_QUERY_STORAGEPOOL_INFO_REPLY: {
                  int num = proto.get3ByteInteger();
                  Vector v = new Vector();
                  while(num > 0) {
                     PoolInfo pool = new PoolInfo();
                     pool.setPoolName(proto.getString16());
                     pool.setPoolDescription(proto.getString16());
                     pool.setPoolId(proto.getLong());
                     pool.setPoolMaxDays(proto.getInteger());
                     pool.setPoolDefaultDays(proto.getInteger());
                     v.add(pool);
                     num--;
                  }
                  proto.verifyCursorDone();
                  
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(v);
                        handleSynchProto(sargs);
                     } else { 
                        fireQueryStoragePoolInfoReply(handler, flags, handle, v);
                     }
                  }
                  break;
               }
               
               default:
                  super.dispatchProtocolI(proto, handler, doDispatch);
               
            }
         }
      } else if (DropboxGenerator.isError(opcode)) {
//         int extra          = proto.getExtraInt();
//         short errorcode    = proto.getShort();
//         String errorString = proto.getString8();
//         proto.verifyCursorDone();
         super.dispatchProtocolI(proto, handler, doDispatch);
      } else if (DropboxGenerator.isEvent(opcode)) {
         super.dispatchProtocolI(proto, handler, doDispatch);
      } else if (DropboxGenerator.isCommand(opcode)) {
      
         switch(opcode) {
            case DropboxGenerator.OP_CREATE_PACKAGE: {
               String packname   = proto.getString16();
               long   expiration = proto.getLong();
               
              // New for V5
               int    optmsk     = 0;
               int    optvals    = 0;
               
              // New for V8
               long   poolid     = DropboxGenerator.PUBLIC_POOL_ID;
               
               if (DropboxGenerator.getProtocolVersion() >= 5) {
                  optmsk  = proto.getInteger();
                  optvals = proto.getInteger();
               }
               
               if (DropboxGenerator.getProtocolVersion() >= 8) {
                  poolid = proto.getLong();
               }
               
               int  num          = proto.getInteger();
               Vector vec = new Vector();
               for(int i=0 ; i < num; i++) {
                  AclInfo info = new AclInfo();
                  info.setAclStatus(proto.getByte());
                  String aclname = proto.getString16();
                  
                 // Project ACLS are to NOT set toLowerCase
                  if (info.getAclStatus() == DropboxGenerator.STATUS_PROJECT) {
                     info.setAclName(aclname);
                  } else {
                     if (info.getAclStatus() != DropboxGenerator.STATUS_GROUP) {
                        info.setAclStatus(DropboxGenerator.STATUS_NONE);
                     }
                     info.setAclName(aclname.toLowerCase());
                  }
                  
                  vec.addElement(info);
               }
               
               proto.verifyCursorDone();
               
               if (doDispatch) {
                  fireCreatePackageCommand(handler, flags, handle, 
                                           packname, poolid, expiration, 
                                           vec, optmsk, optvals);
               }
               break;
            }
            case DropboxGenerator.OP_DELETE_PACKAGE: {
               long packid = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireDeletePackageCommand(handler, flags, handle, packid);
               }
               break;
            }
            case DropboxGenerator.OP_COMMIT_PACKAGE: {
               long packid = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireCommitPackageCommand(handler, flags, handle, packid);
               }
               break;
            }
            case DropboxGenerator.OP_QUERY_PACKAGES: {
               String regexp = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireQueryPackagesCommand(handler, flags, handle, 
                                           (flags & (byte)1) != 0,  
                                           (flags & (byte)2) != 0,  
                                           regexp, 
                                           (flags & (byte)4) != 0,  
                                           (flags & (byte)8) != 0); 
               }
               break;
            }
            case DropboxGenerator.OP_QUERY_PACKAGE: {
               long packid = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireQueryPackageCommand(handler, flags, handle, packid);
               }
               break;
            }
            case DropboxGenerator.OP_QUERY_PACKAGE_CONTENTS: {
               long packid = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireQueryPackageContentsCommand(handler, flags, handle, 
                                                  packid);
               }
               break;
            }
            case DropboxGenerator.OP_QUERY_PACKAGE_ACLS: {
               long packid = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireQueryPackageAclsCommand(handler, flags, handle, 
                                              packid, (flags & 1) != 0);
               }
               break;
            }
            case DropboxGenerator.OP_QUERY_PACKAGE_FILE_ACLS: {
               long packid = proto.getLong();
               long fileid = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireQueryPackageFileAclsCommand(handler, flags, handle, 
                                                  packid, fileid);
               }
               break;
            }
            case DropboxGenerator.OP_QUERY_FILES: {
               String regexp = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireQueryFilesCommand(handler, flags, handle, 
                                        (flags & (byte)1) != 0,  
                                        (flags & (byte)2) != 0,  
                                        regexp);
               }
               break;
            }
            case DropboxGenerator.OP_QUERY_FILE: {
               long fileid = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireQueryFileCommand(handler, flags, handle, fileid);
               }
               break;
            }
            case DropboxGenerator.OP_ADD_ITEM_TO_PACKAGE: {
               long packid = proto.getLong();
               long fileid = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireAddItemToPackageCommand(handler, flags, handle, 
                                              packid, fileid);
               }
               break;
            }
            case DropboxGenerator.OP_UPLOAD_FILE_TO_PACKAGE: {
               long packid  = proto.getLong();
               String md5   = proto.getString8();
               long md5sz   = proto.getLong();
               long filelen = proto.getLong();
               String file  = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireUploadFileToPackageCommand(handler, flags, handle, 
                                                 (flags & (byte)1) != 0, 
                                                 packid, md5, md5sz,
                                                 filelen, file);
               }
               break;
            }
            case DropboxGenerator.OP_REMOVE_ITEM_FROM_PACKAGE: {
               long packid = proto.getLong();
               long fileid = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireRemoveItemFromPackageCommand(handler, flags, handle, 
                                                   packid, fileid);
               }
               break;
            }
            case DropboxGenerator.OP_DOWNLOAD_PACKAGE_ITEM: {
               long packid  = proto.getLong();
               String md5   = proto.getString8();
               long filelen = proto.getLong();
               long fileid  = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireDownloadPackageItemCommand(handler, flags, handle, 
                                                 (flags & (byte)1) != 0, 
                                                 (flags & (byte)2) != 0, 
                                                 packid, md5, filelen, fileid);
               }
               break;
            }
            case DropboxGenerator.OP_DOWNLOAD_PACKAGE: {
               long packid     = proto.getLong();
               String encoding = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireDownloadPackageCommand(handler, flags, handle, 
                                             (flags & (byte)1) != 0, 
                                             packid, encoding);
               }
               break;
            }
            case DropboxGenerator.OP_ADD_PACKAGE_ACL: {
               long packid    = proto.getLong();
               String aclname = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  byte acltype = DropboxGenerator.STATUS_NONE;
                  
                 // Project acls are NOT set toLowerCase
                  if ((flags & (byte)1) != 0) {
                     acltype = DropboxGenerator.STATUS_PROJECT;
                  } else if ((flags & (byte)2) != 0) {
                     acltype = DropboxGenerator.STATUS_GROUP;
                     aclname = aclname.toLowerCase();
                  } else {
                     aclname = aclname.toLowerCase();
                  }
                                    
                  fireAddPackageAclCommand(handler, flags, handle, 
                                           acltype, packid, aclname);
               }
               break;
            }
            case DropboxGenerator.OP_REMOVE_PACKAGE_ACL: {
               long packid    = proto.getLong();
               String aclname = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  byte acltype = DropboxGenerator.STATUS_NONE;
                  
                 // Project acls are NOT set toLowerCase
                  if ((flags & (byte)1) != 0) {
                     acltype = DropboxGenerator.STATUS_PROJECT;
                  } else if ((flags & (byte)2) != 0) {
                     acltype = DropboxGenerator.STATUS_GROUP;
                     aclname = aclname.toLowerCase();
                  } else {
                     aclname = aclname.toLowerCase();
                  }
               
                  fireRemovePackageAclCommand(handler, flags, handle, 
                                              acltype, packid, aclname);
               }
               break;
            }
            case DropboxGenerator.OP_CHANGE_PACKAGE_EXPIRATION: {
               long packid    = proto.getLong();
               long expire    = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireChangePackageExpirationCommand(handler, flags, handle, 
                                                     packid, expire);
               }
               break;
            }
            case DropboxGenerator.OP_GET_PROJECTLIST: {
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireGetProjectList(handler, flags, handle);
               }
               break;
            }
            
            case DropboxGenerator.OP_MARK_PACKAGE: {
               long packid = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireMarkPackageCommand(handler, flags, handle, packid,
                                           ((int)(flags & 1)) != 0);
               }
               break;
            }
            case DropboxGenerator.OP_SEND_NEW_FRAME: {
               int opid = proto.getInteger();
               int num  = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireSendNewFrameCommand(handler, flags, handle, opid, num);
               }
               break;
            }
            case DropboxGenerator.OP_PROTO_VERSION: {
               int ver = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireNegotiateProtocolVersionCommand(handler, flags, handle, 
                                                      ver);
               }
               break;
            }
            case DropboxGenerator.OP_MANAGE_OPTIONS: {
               boolean doGet  = (flags & (byte)1) != 0;
               
               Hashtable set = new Hashtable();
               int numSet = proto.get3ByteInteger();
               while(numSet-- > 0) {
                  String k = proto.getString8();
                  String v = proto.getString16();
                  set.put(k, v);
               }
               
               int numGet = proto.get3ByteInteger();
               Vector get = new Vector();
               while(numGet-- > 0) {
                  String k = proto.getString8();
                  get.addElement(k);
               }
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireManageOptionsCommand(handler, flags, handle, 
                                           doGet, set, get);
               }
               break;
            }
            case DropboxGenerator.OP_CREATE_GROUP: {
               String group = proto.getString16().toLowerCase();
               byte   visibility  = proto.getByte();
               byte   listability = proto.getByte();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireCreateGroupCommand(handler, flags, handle, 
                                         group, visibility, listability);
               }
               break;
            }
            case DropboxGenerator.OP_DELETE_GROUP: {
               String group = proto.getString16().toLowerCase();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireDeleteGroupCommand(handler, flags, handle, 
                                         group);
               }
               break;
            }
            case DropboxGenerator.OP_MODIFY_GROUP_ACL: {
               String group = proto.getString16().toLowerCase();
               String usern = proto.getString16().toLowerCase();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireModifyGroupAclCommand(handler, flags, handle, 
                                            (flags & (byte)1) != (byte)0,
                                            (flags & (byte)2) != (byte)0,
                                            group, usern);
               }
               break;
            }
            case DropboxGenerator.OP_MODIFY_GROUP_ATTRIBUTES: {
               String group = proto.getString16().toLowerCase();
               byte   visibility  = proto.getByte();
               byte   listability = proto.getByte();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireModifyGroupAttributeCommand(handler, flags, handle, 
                                                  group, visibility,
                                                  listability);
               }
               break;
            }
            case DropboxGenerator.OP_QUERY_GROUPS: {
               String group = proto.getString16().toLowerCase();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireQueryGroupsCommand(handler, flags, handle, 
                                         (flags & (byte)1) != (byte)0,
                                         (flags & (byte)2) != (byte)0,
                                         (flags & (byte)4) != (byte)0,
                                         group);
               }
               break;
            }
            
            case DropboxGenerator.OP_SET_PACKAGE_OPTION: {
               long pkgid   = proto.getLong();
               int msk  = proto.getInteger();
               int vals = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireSetPackageOptionCommand(handler, flags, handle, 
                                              pkgid, msk, vals);
               }
               break;
            }
            
            case DropboxGenerator.OP_GET_STORAGEPOOL_INSTANCE: {
               long poolid = proto.getLong();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireGetStoragePoolInstanceCommand(handler, flags, handle, poolid);
               }
               break;
            }
            case DropboxGenerator.OP_QUERY_STORAGEPOOL_INFO: {
               proto.verifyCursorDone();
               
               if (doDispatch) {
                  fireQueryStoragePoolInfoCommand(handler, flags, handle);
               }
               break;
            }
               
               
            default:
               super.dispatchProtocolI(proto, handler, doDispatch);
               break;
         }
      }
   }
}
