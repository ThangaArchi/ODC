package oem.edge.ed.odc.dropbox.service;


import java.util.Vector;
import java.util.HashMap;

import java.rmi.Remote;
import java.rmi.RemoteException;

import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.GroupInfo;
import oem.edge.ed.odc.dsmp.common.DboxException;
import oem.edge.ed.odc.dsmp.common.CommonGenerator;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006                                         */ 
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

/**
 * This interface defines a method of access to the Dropbox. The Dropbox is 
 *  a service allowing for data distribution based on access lists, akin to email.
 *  The data container is a package, which can contain 1 or more file attachments. 
 *<p>
 *  Once the container is complete (all desired data attached), and addressed (all 
 *  recipients specified), the package is committed, and becomes immutable as to its 
 *  attachments. The access list can be modified, by the package owner, to grant 
 *  and/or retract access to a member at any time.
 *<p>
 * Upon creation, the package is set to expire at some future point in time. Once the
 *  expiration date is reached, the package will be automatically purged from the 
 *  system.
 *
 */

public interface DropboxAccess extends java.rmi.Remote {

  /**
   * Value used to lookup the sessionid in the HashMap returned from 
   *  the createSession or refreshSession methods.
   *  The associated value is a String object.
   */
   public final static String SessionID               = "SessionID";
  /**
   * Value used to lookup the expiration in the HashMap returned from 
   *  the createSession or refreshSession methods. 
   *  The associated value is a Long object.
   */
   public final static String Expiration              = "Expiration";
  /**
   * Value used to lookup the time to live in seconds for the session 
   *  in the HashMap returned from the createSession or 
   *  refreshSession methods. Use this instead of Expiration.
   *  The associated value is a Long object.
   */
   public final static String SessionTTL              = "SessionTTL";
  /**
   * Value used to lookup the username in the HashMap returned from 
   *  the createSession or refreshSession methods.
   *  The associated value is a String object.
   */
   public final static String User                    = "User";
  /**
   * Value used to lookup the company in the HashMap returned from 
   *  the createSession or refreshSession methods.
   *  The associated value is a String object.
   */
   public final static String Company                 = "Company";

  /**
   * The group scope used to specify no change to a listability/visibility
   *  parameter. If a returned GroupInfo object has either of these attributes
   *  set to this value, then that group is NOT listable for the caller. 
   */
   public final static byte GROUP_SCOPE_NONE          = CommonGenerator.GROUP_SCOPE_NONE;
   
  /**
   * The group scope used to specify owner/access status required
   */
   public final static byte GROUP_SCOPE_OWNER         = CommonGenerator.GROUP_SCOPE_OWNER;
   
  /**
   * The group scope used to specify member/owner/access status required
   */
   public final static byte GROUP_SCOPE_MEMBER        = CommonGenerator.GROUP_SCOPE_MEMBER;
   
  /**
   * The group scope used to specify all users (whether they are 
   *  owner/access/member or not)
   */
   public final static byte GROUP_SCOPE_ALL           = CommonGenerator.GROUP_SCOPE_ALL;
   
   
  /**
   * This User option is NON-persistent, and provides information about the calling 
   *  clients OS/platform for reporting purposes only.
   */
   public final static String OS                      = DropboxGenerator.OS;
   
  /**
   * This User option is NON-persistent, and provides information about the calling 
   *  clients identity (web, sftp, GUI, ...).
   */
   public final static String ClientType              = DropboxGenerator.ClientType;
   
  /**
   * This User option specifies the default value for the RETURNRECEIPT option upon 
   *  package creation. 
   *  
   */
   public final static String ReturnReceiptDefault    = DropboxGenerator.ReturnReceiptDefault;
   
  /**
   * This User option specifies the default value for the SENDNOTIFY option upon 
   *  package creation. 
   *  
   */
   public final static String SendNotificationDefault = DropboxGenerator.SendNotificationDefault;
   
  /**
   * This User option specifies whether a User wishes to receive email notifications
   *  when a new package is available for download in his/her inbox.
   */
   public final static String NewPackageEmailNotification = DropboxGenerator.NewPackageEmailNotification;
      
  /**
   * This User option specifies whether a User wishes to receive email notifications
   *  when a package in their inbox which has NOT been fully downloaded/and or marked
   *  is closing in on its expiration date.
   */
   public final static String NagNotification = DropboxGenerator.NagNotification; 
   
  /**
   * This User option contains the user's desire setting for scoping data in his/her
   *  inbox for packages which have been marked. This value is NOT 
   *  automatically applied to the queryPackages call. Instead, it is up to the 
   *  service client to query this value and provide the associated boolean value
   *  on the queryPackages invocation as appropriate.
   *  
   */
   public final static String FilterMarked            = DropboxGenerator.FilterMarked;
   
  /**
   * This User option contains the user's desire setting for scoping data in his/her
   *  inbox for packages which have been fully downloaded. This value is NOT 
   *  automatically applied to the queryPackages call. Instead, it is up to the 
   *  service client to query this value and provide the associated boolean value
   *  on the queryPackages invocation as appropriate.
   *  
   */
   public final static String FilterComplete          = DropboxGenerator.FilterComplete;
   
  /**
   * This User option specifies whether hidden packages in a user's inbox will be
   *  returned by queryPackage(s) methods. This is a session oriented option which
   *  defaults to 'false' for each new session.
   */
   public final static String ShowHidden              = DropboxGenerator.ShowHidden;
   
  /**
   * This User option is readonly, and will convey whether the calling user
   *  has been certified to exchange ITAR data.
   *  
   */
   public final static String ItarCertified           = DropboxGenerator.ItarCertified;
   
  /**
   * This User option should be set to true to allow ITAR data to be exchanged 
   *  during a current dropbox session.  The user must be ItarCertified for this
   *  to be meaningful.
   */
   public final static String ItarSessionCertified    = DropboxGenerator.ItarSessionCertified;
   
  /**
   * This Package option specifies whether return receipt is ON or OFF. If this
   *  option is not explicitly specified for the package, the appropriate default
   *  value is derived from the User option ReturnReceiptDefault.
   *<p>
   * When the RETURNRECEIPT option is enabled, a return receipt email will be 
   *  sent to the package owner when a recipient fully downloads the associated
   *  package.
   */
   public final static int  RETURNRECEIPT             = PackageInfo.RETURNRECEIPT;
   
  /**
   * This Package option specifies whether a notification email is sent to all package
   *  recipients when it becomes ready for download. If this
   *  option is not explicitly specified for the package, the appropriate default
   *  value is derived from the User option SendNotificationDefault.
   *<p>
   * When the SENDNOTIFY option is enabled, a notification email is sent to each
   *  user who has access the to package <b>and</b> who does <b>not</b> have the 
   *  NewPackageEmailNotification option disabled.
   */
   public final static byte SENDNOTIFY                = PackageInfo.SENDNOTIFY;
   
  /**
   * This Package option specifies whether a package is hidden or visible in a 
   *  recipients inbox (the default is that the package is visible).
   *<p>
   * The intent of this package option is to allow sending of packages to a
   *  broad audience, but to have the package hidden from view for <i>normal</i>
   *  client use. A client wishing to see these hidden packages can do so
   *  by setting the Session oriented ShowHidden user option.
   */
   public final static byte HIDDEN                    = PackageInfo.HIDDEN;
      
  /**
   * This Package option specifies whether a package may contain ITAR data.
   */
   public final static byte ITAR                      = PackageInfo.ITAR;
      
  /**
   * This is the ACL type for a USER
   */
   public final static byte STATUS_USER               = DropboxGenerator.STATUS_NONE;
   
  /**
   * This is the ACL type for a PROJECT
   */
   public final static byte STATUS_PROJECT            = DropboxGenerator.STATUS_PROJECT;
   
  /**
   * This is the ACL type for a GROUP
   */
   public final static byte STATUS_GROUP              = DropboxGenerator.STATUS_GROUP;
   
  /**
   * This is the file status when there is a failure
   */
   public final static byte STATUS_INCOMPLETE         = DropboxGenerator.STATUS_INCOMPLETE;
  /**
   * This is the package/file status when they are empty
   */
   public final static byte STATUS_NONE               = DropboxGenerator.STATUS_NONE;
   
  /**
   * This is the package status if contains some failed uploads
   */
   public final static byte STATUS_FAIL               = DropboxGenerator.STATUS_FAIL;
   
  /**
   * This is the package status if contains it contains 0 or more files and
   * none are in error
   */
   public final static byte STATUS_PARTIAL            = DropboxGenerator.STATUS_PARTIAL;
   
  /**
   * This is the package and file status when they are committed
   */
   public final static byte STATUS_COMPLETE           = DropboxGenerator.STATUS_COMPLETE;
   
  /**
   * Value denoting wildcard, or ALL slots
   */
   public final static long ALL_SLOTS                 = -2;
   
  /**
   * Value denoting the CULLED slot
   */
   public final static long CULLED_SLOT               = -1;
   
  /**
   * Public/default storage pool identifier.
   */
   public final static long PUBLIC_POOL_ID            = 0;

  /**
   * Creates a session under which all other dropbox calls will be managed. The
   *  one parameter, <i>token</i> must be obtained by using an appropriate login
   *  service, and will be used to authenticate
   *  and then do authorization checks regarding access to the service for the 
   *  user. 
   * <p>
   * A Map will be returned as a successful conclusion to this method invocation.
   * The Map will contain at least two keys, {@link #SessionID SessionID} and
   * {@link #Expiration Expiration}. The associated SessionID value is a String
   *  which can in turn be used as the session parameter in other method calls 
   *  on this object.  The value for the Expiration key is a Long describing the 
   *  milliseconds since 1970 GMT (Date.getTime()) when the SessionID is no longer
   *  usable. To continue using the Dropbox service in the same session a
   *  refreshed sessionid must be obtained prior to the expiration date/time using
   *  {@link #refreshSession() refreshSession} method.
   *
   * @param  token         Time encrypted token describing the callers identity
   * @return HashMap       Map containing the newly created session identifier and 
   *                       id expiration
   * @throws     DboxException describes failure of routine
   */
   public HashMap  createSession(String token) 
      throws DboxException, RemoteException;
   
  /**
   * Creates a session under which all other dropbox calls will be managed. The
   *  parameters, <i>userid</i> and <i>password</i> will be used to authenticate
   *  and then do authorization checks regarding access to the service for the 
   *  user. 
   * <p>
   * A Map will be returned as a successful conclusion to this method invocation.
   * See {@link #createSession(String) createSession} for more details.
   *
   * @param  userid        User id for user creating session
   * @param  password      Password for user creating session
   * @return HashMap       Map containing the newly created session identifier and 
   *                       id expiration
   * @throws     DboxException describes failure of routine
   */
   public HashMap  createSession(String userid, String password) 
      throws DboxException, RemoteException;
   
  /**
   * Refreshes the session described by the associated object proxy.
   * <p>
   * Once a Dropbox session is created, there is only a limited time in which 
   * the returned identifier remains valid. To allow uninterrupted access to the 
   * Dropbox service object, the session identifier MUST be updated or refreshed
   * prior to the expiration of the sessionid
   * <p>
   * A Map will be returned as a successful conclusion to this method invocation.
   * see {@link #createSession(String) createSession} for more details.
   *
   * @return HashMap       Map containing the newly created session identifier and 
   *                        id expiration
   * @throws     DboxException describes failure of routine
   */
   public HashMap refreshSession() 
      throws DboxException, RemoteException;
   
   
  /**
   * Closes the session with the service.
   * <p>
   * If this method is not called to close the session, it will close automatically 
   * coinciding with the expiration of the last sessionRefresh call.
   *
   * @return void
   * @throws     DboxException describes failure of routine
   */
   public void closeSession() 
      throws DboxException, RemoteException;
   
  /**
   * Creates a new package in the calling user's draft folder. The specified 
   * packageName MUST be unique in the calling user's drafts folder.
   *
   * Any options which are not specified will receive appropriate default values. 
   * The list of supported options are:<br />
   * <ul>
   *   <li>{@link #RETURNRECEIPT RETURNRECEIPT}</li>
   *   <li>{@link #SENDNOTIFY SENDNOTIFY}</li>
   *   <li>{@link #HIDDEN HIDDEN}</li>
   * </ul>
   *
   * @param  packageName   Name of new package. This may NOT contain any path separators
   * @param  desc          short package description (1024 chars or less)
   * @param  poolid        Storage pool identifier where files should be stored. 
   *                       Attributes such as expiration and charging may be different
   *                       for different pools. PUBLIC_POOL_ID can be specified to use
   *                       the public storage pool.
   * @param  expiration    Describes the future expiration of the package. This parm has 
   *                       two distinct value profiles. If the value is less than
   *                       50000, then its the number of days in the future from the
   *                       current date.  If its greater than or equal to 50000, then its
   *                       Milliseconds since 70 at which the package expires. If you 
   *                       want the package to expire 5 days hence, you could specify
   *                       either  5 or System.currentTimeMillis() + (5*24*60*60*1000).
   *                       If specified
   *                       as 0, then the default number of days associated with the 
   *                       storage pool will be used to calculate the expiration date.
   * @param  acls          Vector containing any AclInfo objects defining package access
   * @param  optmsk        Mask of boolean options being set for new package
   * @param  optvals       Mask of boolean values     to set for new package
   * @return long          Newly created package id
   * @throws     DboxException describes failure of routine
   */
   public long   createPackage(String packageName, String desc, long poolid, 
                               long expiration, Vector acls,
                               int optmsk, int optvals) 
      throws DboxException, RemoteException;      
                               
  /**
   * Creates a package using the default expiration period associated with the
   *  specified poolid.
   *
   * @param  packageName   Name of new package. This MUST be unique in drafts folder
   * @param  desc          short package description (1024 chars or less)
   * @param  poolid        Storage pool identifier where files should be stored. 
   *                       Attributes such as expiration and charging may be different
   *                       for different pools. PUBLIC_POOL_ID can be specified to use
   *                       the public storage pool.
   * @param  acls          Vector containing any AclInfo objects defining package access
   * @param  optmsk        Mask of boolean options being set for new package
   * @param  optvals       Mask of boolean values     to set for new package
   * @return long          Newly created package id
   * @throws     DboxException describes failure of routine
   *
   * Please see {@link #createPackage(String, String, long, long, Vector, int, int) createPackage}
   */
   public long   createPackage(String packageName, String desc, long poolid, 
                               Vector acls,
                               int optmsk, int optvals)
      throws DboxException, RemoteException;                               
                               
  /**
   * Creates a package using the default expiration period associated with the
   *  specified poolid.
   *
   * @param  packageName   Name of new package. This MUST be unique in drafts folder
   * @param  desc          short package description (1024 chars or less)
   * @param  poolid        Storage pool identifier where files should be stored. 
   *                       Attributes such as expiration and charging may be different
   *                       for different pools. PUBLIC_POOL_ID can be specified to use
   *                       the public storage pool.
   * @param  optmsk        Mask of boolean options being set for new package
   * @param  optvals       Mask of boolean values     to set for new package
   * @return long          Newly created package id
   * @throws     DboxException describes failure of routine
   *
   * Please see {@link #createPackage(String, String, long, long, Vector, int, int) createPackage}
   */
   public long   createPackage(String packageName, String desc,  long poolid, 
                               int optmsk, int optvals)
      throws DboxException, RemoteException;
                               
  /**
   * Creates a package using all default values
   *
   * @param  packageName   Name of new package. This MUST be unique in drafts folder
   * @return long          Newly created package id
   * @throws     DboxException describes failure of routine
   *
   * Please see {@link #createPackage(String, String, long, long, Vector, int, int) createPackage}
   */
   public long   createPackage(String packageName)
      throws DboxException, RemoteException;   
   
  /**
   * Returns an login message/banner that might be set at the server.
   *
   * @return String         Banner information
   * @throws     DboxException describes failure of routine
   */
   public String getLoginMessage() 
      throws DboxException, RemoteException;   
   
  /**
   * @param  packid        Package id for package whose options are being set
   * @param  optmsk        Mask of boolean options being set for new package
   * @param  optvals       Mask of boolean values     to set for new package
   * @return void          Newly created package id
   * @throws     DboxException describes failure of routine
   *
   * Please see {@link #createPackage(String, String, long, Vector, int, int) createPackage} 
   *  for the mask values
   */
   public void   setPackageFlags(long packid,
                                 int optmsk, 
                                 int optvals)
      throws DboxException, RemoteException;
   
   
  /**
   * Allows packages to be deleted from the dropbox. Only the package owner may delete
   *  a package
   *
   * @param  packid        Package identifier for package being deleted
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void   deletePackage(long packid)
      throws DboxException, RemoteException;
   
   
  /**
   * Commits a package which has all attachments completed
   * and makes the package available to recipients.
   *
   * @param  packid        Package identifier for package being committed
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void   commitPackage(long packid)
      throws DboxException, RemoteException;
   
   
  /**
   * Marks a package which is present in a user's inbox. Marking is used to help filter
   *  a user's inbox during a query
   *
   * Please see {@link #queryPackages(String,boolean,boolean,boolean,boolean,boolean) queryPackages}
   *
   * @param  packid        Package identifier for package being marked/cleared
   * @param  v             Boolean value describing whether to mark or clear
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void   markPackage(long packid, boolean v)
      throws DboxException, RemoteException;
   
   
  /**
   * Adds the specified entity to the access list of the package. The valid 
   *  values for <i>type</i> are:
   *<br>
   * <ul>
   *   <li>{@link #STATUS_USER    STATUS_USER}</li>
   *   <li>{@link #STATUS_PROJECT STATUS_PROJECT}</li>
   *   <li>{@link #STATUS_GROUP   STATUS_GROUP}</li>
   * </ul>
   *
   * @param  packid        Package identifier for package whose acls are being modified
   * @param  aclname       entity name being added to specified package
   * @param  acltype       entity type being added to specified package
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void addPackageAcl(long packid, 
                             String aclname, 
                             byte acltype) 
      throws DboxException, RemoteException;                             
      
   
  /**
   * Removes the specified entity from the access list of the package. The valid 
   *  values for <i>type</i> are:
   *<br>
   * <ul>
   *   <li>{@link #STATUS_USER    STATUS_USER}</li>
   *   <li>{@link #STATUS_PROJECT STATUS_PROJECT}</li>
   *   <li>{@link #STATUS_GROUP   STATUS_GROUP}</li>
   * </ul>
   
   * @param  packid        Package identifier for package whose acls are being modified
   * @param  aclname       entity name being added to specified package
   * @param  acltype       entity type being added to specified package
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void removePackageAcl(long packid, 
                                String aclname,
                                byte acltype) 
      throws DboxException, RemoteException;                                
   
  /**
   * Adds the specified user to the access list for the package. If the server is 
   *  configured to apply user correctness checking, then the specified user name
   *  must exist, and the sender must have the necessary permissions to send packages
   *  to said recipient. If either of these requirements are not true, then an error
   *  will be returned.
   *
   * @param  packid        Package identifier for package whose acls are being modified
   * @param  name          User name being added to specified package
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void   addUserAcl(long packid, String name)
      throws DboxException, RemoteException;
   
   
  /**
   * Adds the specified group to the access list for the package. The groups specified
   *  must exist, and the sender must be allowed to send to said group (visibility
   *  of group is examined).
   *
   * @param  packid        Package identifier for package whose acls are being modified
   * @param  name          Group name being added to specified package
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void   addGroupAcl(long packid, String name)
      throws DboxException, RemoteException;
   
   
  /**
   * Adds the specified project to the access list for the package. The user
   *  must be part of the project in order to add it to the package access list.
   *
   * @param  packid        Package identifier for package whose acls are being modified
   * @param  name          Project name being added to specified package
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void   addProjectAcl(long packid, String name)
      throws DboxException, RemoteException;
      
   
  /**
   * Removes the specified user from the access list for the package. 
   *
   * @param  packid        Package identifier for package whose acls are being modified
   * @param  name          User name being removed from specified package
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void   removeUserAcl(long packid, String name)
      throws DboxException, RemoteException;
   
   
  /**
   * Removes the specified group from the access list for the package. 
   *
   * @param  packid        Package identifier for package whose acls are being modified
   * @param  name          Group name being removed from specified package
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void   removeGroupAcl(long packid, String name)
      throws DboxException, RemoteException;
   
   
  /**
   * Removes the specified project from the access list for the package. 
   *
   * @param  packid        Package identifier for package whose acls are being modified
   * @param  name          Project name being removed from specified package
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void   removeProjectAcl(long packid, String name)
      throws DboxException, RemoteException;
   
   
  /**
   * Changes the package expiration from its current setting, to be the specified 
   *  number of days into the future from the current date.
   *
   * @param  packid    Package identifier for package whose expiration is being modified
   * @param  expires   Milliseconds since 70 OR days in the future when this package 
   *                   should expire. See the expiration parameter on 
   *    {@link #createPackage(String, String, long, long, Vector, int, int) createPackage}
   *                   for more info.
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void   changePackageExpiration(long packid, 
                                         long expires)
      throws DboxException, RemoteException;      
   
  /**
   * Obtains the information associated with a storage pool instance
   *
   * @param  poolid     Pool identifier for storage pool information retrieval
   * @return PoolInfo          
   * @throws     DboxException describes failure of routine
   *
   */
   public PoolInfo getStoragePoolInstance(long poolid) 
      throws DboxException, RemoteException;      
      
  /**
   * Obtains all storage pool instances to which the caller is entitled
   *
   * @return Vector  Vector of storage PoolInfo objects
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector queryStoragePoolInformation() 
      throws DboxException, RemoteException;      
      
  /**
   * Sets the package description field to the specified string. The description 
   *  size is limited to 1024 bytes. Any longer description will be silently
   *  truncated.
   *
   * @param  packid    Package identifier for package whose expiration is being modified
   * @param  desc      short package description (1024 chars or less)
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public void   setPackageDescription(long   packid, 
                                       String desc)
      throws DboxException, RemoteException;      
   
  /**
   * Get a map containing all options (key/value) associated with the user's Dropbox
   *
   * @return HashMap   Contains key/value pairs describing currently set options
   * @throws     DboxException describes failure of routine
   *
   */
   public HashMap   getOptions()
      throws DboxException, RemoteException;
   
   
  /**
   * Get a map containing the specified options (key/value) associated with the user's 
   *  Dropbox
   *
   * @param  optnames  Vector containing option names to retrieve
   * @return HashMap   Contains key/value pairs describing currently set options
   * @throws     DboxException describes failure of routine
   *
   */
   public HashMap   getOptions(Vector optnames)
      throws DboxException, RemoteException;
   
   
  /**
   * Get the value for the specified Dropbox option name
   *
   * @param  opt       Name of option to retrieve
   * @return String    String value associated with the specified option
   * @throws     DboxException describes failure of routine
   *
   */
   public String  getOption(String opt)
      throws DboxException, RemoteException;
   
   
  /**
   * Set the option values specified in the Map
   *
   * @param  options   key/values of options being set
   * @return Void
   * @throws     DboxException describes failure of routine
   *
   */
   public void    setOptions(HashMap options)
      throws DboxException, RemoteException;
   
   
  /**
   * Set a single option/value
   *
   * @param  opt      key   of option being set
   * @param  val      value of option being set
   * @return void
   * @throws     DboxException describes failure of routine
   *
   */
   public void    setOption(String opt, String val)
      throws DboxException, RemoteException;
   
   
  /**
   * Get the list of projects to which the caller belongs
   *
   * @return Vector  contains list of projects
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector  getProjectList()
      throws DboxException, RemoteException;
   
   
  /**
   * Get the list of packages and associated information to which the calling
   *  user has access, and which match the specified criteria
   *
   * @param  name            String used for match criteria of package name
   * @param  isRegExp        true if the name parameter is a regular expression
   * @param  ownerOrAccessor true if matching packages must be owned by caller, 
   *                         otherwise, only packages sent to user are returned
   * @param  filterCompleted true if packages fully downloaded by user should be ignored
   * @param  filterMarked    true if packages marked by user should be ignored
   * @param  fullDetail      true if full detail should be added to the PackageInfo 
   *                         objects. Setting this to false will be faster, but will 
   *                         not contain XXXXXXX TODO XXXXXXX
   * @return Vector   contains list of PackageInfo objects for matches
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector queryPackages(String name, boolean isRegExp, 
                               boolean ownerOrAccessor,
                               boolean filterCompleted, 
                               boolean filterMarked,
                               boolean fullDetail)
      throws DboxException, RemoteException;
                               
  /**
   * Get the list of packages and associated information to which the calling
   *  user has access, and which match the specified criteria
   *
   * @param  ownerOrAccessor true if matching packages must be owned by caller, 
   *                         otherwise, only packages sent to user are returned
   * @param  filterCompleted true if packages fully downloaded by user should be ignored
   * @param  filterMarked    true if packages marked by user should be ignored
   * @param  fullDetail      true if full detail should be added to the PackageInfo 
   *                         objects. Setting this to false will be faster, but will 
   *                         not contain XXXXXXX TODO XXXXXXX
   * @return Vector   contains list of PackageInfo objects for matches
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector queryPackages(boolean ownerOrAccessor,
                               boolean filterCompleted, 
                               boolean filterMarked,
                               boolean fullDetail)
      throws DboxException, RemoteException;
                               
  /**
   * Get the PackageInfo information for the specified package (if the caller has
   * access to the package)
   *
   * @param  packid          packageid for the package to query
   * @param  fullDetail      true if full detail should be added to the PackageInfo 
   *                         objects. Setting this to false will be faster, but will 
   *                         not contain XXXXXXX TODO XXXXXXX
   * @return Vector   contains list of PackageInfo objects for matches
   * @throws     DboxException describes failure of routine
   *
   */
   public PackageInfo queryPackage(long packid,
                                   boolean fullDetail)
      throws DboxException, RemoteException;
                        
  /**
   * Returns a list of FileInfo objects which describe the attachments for
   *  the specified package.
   *
   * @param  packid        Package identifier for package being listed
   * @return void          
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector queryPackageContents(long packid)
      throws DboxException, RemoteException;
   
                        
  /**
   * Returns a list of FileInfo objects which match the specified criteria
   *
   * @param  name            String used for match criteria of file name
   * @param  isRegExp        true if the name parameter is a regular expression
   * @param  ownerOrAccessor true if matching files must be owned by caller, 
   *                         otherwise, only files sent to the user will be returned
   * @return Vector of FileInfo
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector queryFiles(String name, boolean isRegExp,
                            boolean ownerOrAccessor)
      throws DboxException, RemoteException;
                            
  /**
   * Returns a list of FileInfo objects which match the specified criteria
   *
   * @param  ownerOrAccessor true if matching files must be owned by caller, 
   *                         otherwise, only files sent to the user will be returned
   * @return void          
   * @return Vector of FileInfo
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector queryFiles(boolean ownerOrAccessor)
      throws DboxException, RemoteException;
                        
                        
  /**
   * Returns a FileInfo object describing file specified by the provided fileid
   *
   * @param  fileid    Fileid for the file being queried
   * @return FileInfo  Describes the specified file
   * @throws     DboxException describes failure of routine
   *
   */
   public FileInfo queryFile(long fileid)
      throws DboxException, RemoteException;
                        
                        
  /**
   * Returns a Vector of AclInfo objects describing the access entries for the package.
   *  Anyone with access to a package can call this method, but the data visible will
   *  be scoped for the caller (owner sees ALL, others see info pertinent to them alone)
   *
   * @param  packid     The package identifier for which the acls are being queried
   * @param  staticonly If true, then only the acls which are set on the package are
   *                    returned. Otherwise, the static acls are returned along with
   *                    AclInfos for folks in groups and/or projects which have accessed
   *                    (downloaded) any files in the package
   * @return Vector List of AclInfo objects
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector queryPackageAcls(long packid, boolean staticonly)
      throws DboxException, RemoteException;
                        
  /**
   * Returns a Vector of company name strings, one for each of the companies who
   *  are represented in the Acls for the specified package.
   *
   * @param  packid     The package identifier for which the acls are being queried
   * @return Vector List of company name strings
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector queryPackageAclCompanies(long packid)
      throws DboxException, RemoteException;
                        
  /**
   * Returns a Vector of company name strings, one for each of the companies who
   *  are represented in the passed in acls vector containing AclInfo objects. If
   *  the acl list is for an ITAR package, set needitar to true, otherwise false.
   * <p>
   * If any of the AclInfo objects in the acls vector are invalid (they don't exist
   *  or the caller does not have authority to reference them), then an 
   *  DboxException will be thrown. If needitar is set to true AND the caller is 
   *  not ITAR certified, then an exception will be thrown.
   *
   * @param  acls      Vector containing AclInfo objects to check
   * @param  needitar  true if query is to build ITAR package
   * @return Vector    List of company name strings
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector queryRepresentedCompanies(Vector acls, boolean needitar)
      throws DboxException, RemoteException;
                        
  /**
   * Returns a Vector of company name strings, one for each valid user which matches 
   *  the specified username
   *
   * @param  username String for which to search
   * @param  isregex  If true, then username may contain * and ? characters which
   *                   will match 0 or more chars and any single char as wilds. If the
   *                   use of wilds is not supported by the server implementation,
   *                   a DropboxException will be thrown
   * @return Vector   Vector of matches
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector lookupUser(String username, boolean isregex) 
      throws DboxException, RemoteException;
                        
                        
  /**
   * Returns a list of AclInfo objects describing download status for a file.
   *  Anyone with access to the package can call this method, but the data visible will
   *  be scoped for the caller (owner sees ALL, others see info pertinent to them alone)
   *
   * @param  packid     The package identifier for which the access is being queried
   * @param  fileid     The file    identifier for which the access is being queried
   * @return Vector List of AclInfo objects
   * @throws     DboxException describes failure of routine
   *
   */
   public Vector queryPackageFileAcls(long packid, long fileid)
      throws DboxException, RemoteException;
                        
                        
  /**
   * Adds a file which already exists in the Dropbox to a package. The caller must
   *  be the owner of the package in question, and have access to the file being added.
   *  The file must be in the complete state, while the package must NOT be commited.
   *
   * @param  packid     The package identifier for the package being modified
   * @param  fileid     The file    identifier for the file being added
   * @return void
   * @throws     DboxException describes failure of routine
   * 
   */
   public void addItemToPackage(long packid, long fileid)
      throws DboxException, RemoteException;
   
  /**
   * Removes a file which exists in a Dropbox package. The caller must be the
   *  owner of the package, and the package must NOT be commited.
   *
   * @param  packid     The package identifier for the package being modified
   * @param  fileid     The file    identifier for the file being removed
   * @return void
   * @throws     DboxException describes failure of routine
   *
   */
   public void removeItemFromPackage(long packid,
                                     long fileid)
      throws DboxException, RemoteException;
   
  /**
   * Initiate the creation of the file object in a package. An exception will be thrown
   *  if a file of the same name already exists in the package.
   *
   * @param  packid        The identifier of the package being modified
   * @param  nameInPackage Name of new file in the package. This MAY contain file 
   *                       separators, which will be rendered as directories by some
   *                       clients
   * @return long          file id of newly created file object 
   * @throws               DboxException describes failure of routine
   *
   */
   public long uploadFileToPackage(long packid, 
                                   String nameInPackage,
                                   long totalIntendedSize)
      throws DboxException, RemoteException;
   
  /**
   * Obtains a FileSlot to be used for file upload to the package/file in question.
   * Its up to the server's discretion as to the size/location of the data. The 
   * totalthreads value along with the totalIntendedSize for the file upload will
   * be used to determine an appropriate slot size.
   *
   * @param  packid        The identifier of the package for slot allocation
   * @param  fileid        The identifier of the file    for slot allocation
   * @param  totalThreads  The number of simultaneous uploads (slots) that will
   *                       will be attempted. The number <i>1</i> would be specified
   *                       if only a single thread will be used to accomplish the 
   *                       upload. 
   * @return FileSlot      Describes the location and size for target upload
   * @throws               DboxException describes failure of routine
   *
   */
   public FileSlot allocateUploadFileSlot(long packid, 
                                          long fileid,
                                          int  totalThreads)
      throws DboxException, RemoteException;
                                          
  /**
   * Obtains a FileSlot to be used for file upload to the package/file in question.
   * Its up to the server's discretion as to the size/location of the data. The 
   * totalthreads value along with the totalIntendedSize for the file upload will
   * be used as hints to determine an appropriate slot size.
   *
   * @param  packid        The identifier of the package for upload
   * @param  fileid        The identifier of the file    for upload
   * @param  slotid        The slotid (FileSlot->getSlotId()) for upload
   * @param  is            The InputStream from where the data will be obtained for
   *                       upload
   * @param  getNextSlot   If true, the next appropriate FileSlot for upload will be
   *                       returned. Otherwise, a null is returned.
   * @return FileSlot      Describes the location and size for next upload slot
   * @throws               DboxException describes failure of routine
   *
   */
   public FileSlot uploadFileSlotToPackage(long packid, 
                                           long fileid,
                                           long slotid,
                                           boolean getNextSlot,
                                           byte is[])
      throws DboxException, RemoteException;
   
  /**
   * Queries the currently define fileslots for upload
   *
   * @param  packid        The identifier of the package for slot query
   * @param  fileid        The identifier of the file    for slot query
   * @return Vector        Contains currently defined fileslots in question
   * @throws               DboxException describes failure of routine
   *
   */
   public Vector queryFileSlots(long packid, long fileid)
      throws DboxException, RemoteException;
                                               
  /**
   * Removes the specified slot from the package/file. 
   *<p>
   * If the provided slotid is ALL_SLOTS, then ALL slots will be removed.
   *
   * @param  packid        The identifier of the package for slot removal
   * @param  fileid        The identifier of the file    for slot removal
   * @param  slotid        The identifier of the slot    for removal
   * @return void
   * @throws               DboxException describes failure of routine
   *
   */
   public void removeFileSlot(long packid, 
                              long fileid,
                              long slotid)
      throws DboxException, RemoteException;
                                               
  /**
   * Releases ownership of the specified slot for the package/file. If the provided 
   * slotid is ALL_SLOTS, then ALL slots will be released. When a FileSlot is allocated, 
   * it is tagged as being reserved for a specific user session as a rudemental
   * locking system.
   *
   * @param  packid        The identifier of the package for slot removal
   * @param  fileid        The identifier of the file    for slot removal
   * @param  slotid        The identifier of the slot    for removal
   * @return void
   * @throws               DboxException describes failure of routine
   *
   */
   public void releaseFileSlot(long packid, 
                               long fileid,
                               long slotid)
      throws DboxException, RemoteException;
                                               
  /**
   * This method simply registers transfer-rate audit information from the 
   * client-side perspective. The Dropbox Reporting feature can be used to 
   * probe these values to gain insight into performance issues.
   * <p>
   * If this method is NOT called, then the transfer rate will simply be an 
   * unknown. The intent is for the client to invoke this method after a file
   * operation is complete.
   *
   * @param  packid        The identifier of the package for audit update
   * @param  fileid        The identifier of the file    for upload update
   * @param  length        The total length achieved for transfer
   * @param  timems        The total time (ms) for the transfer operation
   * @param  upOrDown      Is this an Upload or Download operation (true for upload)
   * @return void
   * @throws               DboxException describes failure of routine
   *
   */
   public void registerAuditInformation(long packid, 
                                        long fileid,
                                        long length,
                                        long timems,
                                        boolean upOrDown)
      throws DboxException, RemoteException;
   
  /**
   * This method will truncate (if necessary) and validate the MD5 for the package/file
   * in question. The file length will be set to the specified length, and the
   * resultant server side md5 will be validated against the value specified in the 
   * method invocation.
   * <p>
   * If the md5 is non-null, and does not match the files MD5 or the specified length 
   * is greater than the actual file length OR the actual file contains holes 
   * inclusive of the specified length, then this method will fail, and return an
   * exception.
   * <p>
   * This method need not be called to complete an upload. Instead, its required to 
   * complete an upload whose initial size was specified as a larger size when the 
   * actual file size was smaller. This would be the case for access methods where the
   * actual file size was not known (sftp)
   *
   * @param  packid        The identifier of the package for upload commit
   * @param  fileid        The identifier of the file    for upload commit
   * @param  length        The total length for the file
   * @param  md5           The MD5 value for the total file length. If specified 
   *                       as null, then no md5 check will occur.
   * @return void
   * @throws               DboxException describes failure of routine
   *
   */
   public void commitUploadedFile(long packid, 
                                  long fileid,
                                  long length,
                                  String md5)
      throws DboxException, RemoteException;
   
  /**
   * Downloads the specified package using the provided encoding type. This download
   *  method is NOT restartable, nor is it threadable (sequential only). The returned
   *  input stream should be used to sequentially access all the data in the package,
   *  which will be encoded as specified.  Supported encodings are:
   *<p>
   *&nbsp;&nbsp;&nbsp;&nbsp;tar  -  Tar file containing all files in package
   *&nbsp;&nbsp;&nbsp;&nbsp;tgz  -  Tar file containing all files in package, and gziped
   *&nbsp;&nbsp;&nbsp;&nbsp;zip  -  Zip file containing all files in package
   *<p>  
   * NOTE: The end size of the package is not known while the package is being 
   *  streamed to the caller. If this API is being accessed using a transport which
   *  has a max size per invocation (such as 2GIG for http), then its possible that
   *  the data will not be fully delivered.
   *
   * @param  packid        The identifier of the package for download
   * @param  encoding      The type of encoding to apply to files
   * @return InputStream   Used to read downloaded bytes
   * @throws               DboxException describes failure of routine
   */
   public byte[] downloadPackage(long packid, 
                                String encoding)
      throws DboxException, RemoteException;
   
  /**
   * Downloads the specified file from the specified package. This download
   *  method is NOT restartable, nor is it threadable (sequential only).
   *<p>  
   * NOTE:  If this API is being accessed using a transport which
   *  has a max size per invocation (such as 2GIG for http), then its possible that
   *  the data will not be fully delivered.  
   *
   * @param  packid        The identifier of the package containing file for download
   * @param  fileid        The identifier of the file for download
   * @return InputStream   Used to read downloaded bytes
   * @throws               DboxException describes failure of routine
   *
   */
   public byte[] downloadPackageItem(long packid, 
                                     long fileid)
      throws DboxException, RemoteException;
                                     
  /**
   * Downloads a section of the specified file from the specified package. The file
   *  download process is mostly driven by the client. The one major caveat is that
   *  the final 10k of data in a file must be downloaded in a single invocation (can
   *  be more than 10k, but must be at least 10k, or entire file).  This caveat exists
   *  to help ensure non-repudiation for downloads. When a user of the API asks for,
   *  and is delivered, from the Servers standpoint, the final 10k of a file, then
   *  the the file access record for that user is marked completed.
   *
   * It is the server's perogative to determine how much data to return for the 
   *  invocation, but it will NOT be greater than len bytes (could be <= len, however).
   *
   * @param  packid        The identifier of the package containing file for download
   * @param  fileid        The identifier of the file for download
   * @param  ofs           Byte offset into the file to begin the data transfer
   * @param  len           Max number of bytes to download
   * @return byte[]        bytes being downloaded
   * @throws               DboxException describes failure of routine
   *
   */
   public byte[] downloadPackageItem(long packid, 
                                     long fileid,
                                     long ofs,
                                     long len)
      throws DboxException, RemoteException;

  /**
   * Returns the MD5 value of the specified package/file for the first <i>len</i>
   *  bytes. The intent of this routine is to help facilitate resumption of a previously
   *  interrupted download. The client can check the "goodness" of any local file
   *  data with that stored on the server, thus be assured that the selected resumption 
   *  point is valid.
   *<p>
   * Note, because this service API allows multi-threaded access, its possible that 
   *  a file resulting from an interrupted download may contain 'holes'. To help 
   *  compensate for restarting when holes are present, this routine actually returns
   *  an Array of MD5 values. The first value is always for the specified offset. For
   *  each additional element of the array, the MD5 value is for the offset exactly 
   *  1 MB (1024*1024) less than preceding the element. This method will return a max of
   *  5 MD5 elements per invocation, and will stop prior to 5 if the target offset is
   *  less than 1 MB (again, 1024*1024). 
   *<p>
   * Example 1, if you asked to restart a file at len = 999999, the array would have
   *  only one entry, because 999999 - (1024*1024) is < 0, which is an invalid offset.
   *<p>
   * Example 2, if you asked to restart a file at len = (2*1024*1024)-1, the array would 
   *  still have only one entry, because the second entry would be < 1MB, and so would
   *  be excluded.
   *<p>
   * Example 3, if you asked to restart a file at len = (6*1024*1024), the array would 
   *  have 5 entries, MD5 for 6, 5, 4, 3 and 2 MB offsets.
   *
   * @param  packid        The identifier of the package containing file for MD5 generation
   * @param  fileid        The identifier of the file for md5 generation
   * @param  len           Number of bytes to include in MD5 generation.
   * @return String[]      Calculated MD5 value for the specified offset, and up to 
   *                       four other MD5 values for offsets decending by a megabyte.
   * @throws               DboxException describes failure of routine
   *
   */
   public String[] getPackageItemMD5(long packid, 
                                     long fileid,
                                     long len)
      throws DboxException, RemoteException;
                                     
  /**
   * Creates a Dropbox Group of the specified name. The group will have no
   * members, and will be setup with the listability and visibility specified.
   * The group names MUST be
   * unique for all users. Its suggested that user prepend their userid to all
   * their groups (or use some less verbose, semi-unique prefix).
   * <p>
   * Possible listability and visibility values are GROUP_SCOPE_XXX where XXX is
   * <p>
   * &nbsp;&nbsp;OWNER  - private group<br />
   * &nbsp;&nbsp;MEMBER - For members only<br />
   * &nbsp;&nbsp;ALL    - public group<br />
   * <p>
   * A group has 2 distinct acl lists, one for Members and one for Access users. 
   * The former are users who are the target for sends to the group. The latter 
   * (Access) are user who can manipulate (administer) the group. The owner is 
   * implicitly in the Access list.
   *
   * @param  groupname    Name of group to be created
   * @param  visibility   set who can see/use the group to send packages
   * @param  listability  set who can list the members of the group
   * @return void      
   * @throws     DboxException
   *
   */
   public void createGroup(String groupname, 
                           byte visibility, 
                           byte listability)
      throws DboxException, RemoteException;
                                             
  /**
   * Creates a Dropbox Group of the specified name. The group will have no
   * members, and will be setup as a private group.  The group names MUST be
   * unique for all users. Its suggested that user prepend their userid to all
   * their groups (or use some less verbose, semi-unique prefix).
   *
   * @param  groupname   Name of group to be created
   * @return void
   * @throws     DboxException
   *
   */
   public void createGroup(String groupname)
      throws DboxException, RemoteException;
                                             
                                             
  /**
   * Deletes the specified group name. If the group is not owned by the caller, then
   *  the delete will fail.
   *
   * @param  groupname   Name of group to be deleted
   * @return void
   * @throws     DboxException
   *
   */
   public void deleteGroup(String groupname)
      throws DboxException, RemoteException;
                                             
                                             
  /**
   * Add a member to either the USER portion or ACCESS portion of the group. Only 
   *  the owner or users in the ACCESS list can modify the group acls.
   *
   * @param  groupname       Name of group to modify
   * @param  user            Id of the user to add
   * @param  memberOrAccess  If true, then its a normal user add. Otherwise, the
   *                          add is to the ACCESS list of the group
   * @return void
   * @throws     DboxException
   *
   */
   public void addGroupAcl(String  groupname, 
                           String  user,
                           boolean memberOrAccess)
      throws DboxException, RemoteException;
                           
                                             
  /**
   * Remove a member from either the USER portion or ACCESS portion of the group. Only 
   *  the owner or users in the ACCESS list can modify the group acls.
   *
   * @param  groupname       Name of group to modify
   * @param  user            Id of the user to remove
   * @param  memberOrAccess  If true, then its a normal user remove. Otherwise, the
   *                          remove is from the ACCESS list of the group
   * @return void
   * @throws     DboxException
   *
   */
   public void removeGroupAcl(String  groupname, 
                              String  user,
                              boolean memberOrAccess)
      throws DboxException, RemoteException;
                                             
                                             
  /**
   * Allows modification of the visibility/listability attributes associated with
   * a group. Possible values are GROUP_SCOPE_XXX where XXX is
   * <p>
   * &nbsp;&nbsp;NONE   - Don't change current value<br />
   * &nbsp;&nbsp;OWNER  - private group (owner and access users)<br />
   * &nbsp;&nbsp;MEMBER - For members only<br />
   * &nbsp;&nbsp;ALL    - public group<br />
   * <p>
   *
   * @param  groupname    Name of group to be modified. Only Owner and Access 
   *                      participants may modify the attributes.
   * @param  visibility   set who can see/use the group to send packages
   * @param  listability  set who can list the members of the group
   * @return void      
   * @throws     DboxException
   *
   */
   public void modifyGroupAttributes(String groupname, 
                                     byte visibility, 
                                     byte listability)
      throws DboxException, RemoteException;
                                             
  /**
   * Return a Map including the groupname as a key and GroupInfo object as the value,
   *  which describes the group in question. If includeMembers/includeAccess are true,
   *  then those lists will be included in the GroupInfo object, but only if its 
   *  allowed based on visibility/listability.  Only members of the Access list may
   *  see the members of the access list.
   *
   * @param  groupname       String used for match criteria of group name
   * @param  isRegExp        true if the groupname parameter is a regular expression
   * @param  includeMembers  true if the Member acls should be included
   * @param  includeAccess   true if the Access acls should be included
   * @return HashMap   contains list of PackageInfo objects for matches
   * @throws     DboxException describes failure of routine
   *
   */
   public HashMap  queryGroups(String groupname,
                               boolean isRegExp,
                               boolean includeMembers,
                               boolean includeAccess)
      throws DboxException, RemoteException;
                                             
  /**
   * Return a Map including the groupname as a key and GroupInfo object as the value,
   *  which describes the group in question. All groups to which the user has 
   *  visibility will be returned. If includeMembers/includeAccess are true,
   *  then those lists will be included in the GroupInfo object, but only if its 
   *  allowed based on visibility/listability.  Only members of the Access list may
   *  see the members of the access list.   
   *
   * @param  includeMembers  true if the Member acls should be included
   * @param  includeAccess   true if the Access acls should be included
   * @return HashMap   contains list of PackageInfo objects for matches
   * @throws     DboxException describes failure of routine
   *
   */
   public HashMap  queryGroups(boolean includeMembers,
                               boolean includeAccess)
      throws DboxException, RemoteException;
                                             
                                             
  /* Added cause WAS was not loading the WAS serializer w/out a real ref ... sigh */
   public GroupInfo  queryGroup(String groupname)
      throws DboxException, RemoteException;
                                             
  /* Added cause WAS was not loading the WAS serializer w/out a real ref ... sigh */
   public void addPackageAcl(long packid, AclInfo aclinfo) 
      throws DboxException, RemoteException;                             
}
