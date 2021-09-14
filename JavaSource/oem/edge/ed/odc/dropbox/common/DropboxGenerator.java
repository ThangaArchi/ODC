package oem.edge.ed.odc.dropbox.common;

import  oem.edge.ed.odc.dsmp.common.*;

import java.lang.*;
import java.util.*;
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

public class DropboxGenerator implements CommonGenerator {

  // Error codes
   public final static short ERROR_NOT_SPECIFIED               =   0;
   public final static short ERROR_PACKAGE_DOES_NOT_EXIST      =  20;
   public final static short ERROR_DB_EXCEPTION                =  99;

  // Status values
   public final static byte STATUS_GROUP               = -1;
   public final static byte STATUS_NONE                = 1;
   public final static byte STATUS_PROJECT             = 2;
   public final static byte STATUS_INCOMPLETE          = 3;
   public final static byte STATUS_FAIL                = 4;
   public final static byte STATUS_PARTIAL             = 5;
   public final static byte STATUS_COMPLETE            = 10;
   
  // Group Scope values. NONE is not a valid scope, but indicates that SCOPE 
  //  is not being set in protocol
  /* ... we get this from CommonGenerator
   public final static byte GROUP_SCOPE_NONE            =  0;
   public final static byte GROUP_SCOPE_OWNER           =  1;
   public final static byte GROUP_SCOPE_MEMBER          =  5;
   public final static byte GROUP_SCOPE_ALL             = 10;
  */

   
  //
  // Defined Dropbox Options
  //
   public final static 
   String NewPackageEmailNotification = "NewPackageEmailNotification";
   public final static 
   String FilterComplete              = "FilterComplete";
   public final static 
   String FilterMarked                = "FilterMarked";
   public final static 
   String ReturnReceiptDefault        = "ReturnReceiptDefault";
   public final static 
   String SendNotificationDefault     = "SendNotificationDefault";
   public final static 
   String ShowHidden                  = "ShowHidden";

   public final static
   String NagNotification             = "NagmailNotification";
   public final static
   String ItarCertified               = "ItarCertified";
   public final static
   String ItarSessionCertified        = "ItarSessionCertified";
   public final static 

   String OS                          = "OS";
   public final static 
   String ClientType                  = "ClientType";
   
   public final static 
   long PUBLIC_POOL_ID                = 0;
   
}
