package oem.edge.ed.odc.dropbox.common;

import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.util.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004,2005,2006                           */
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

/**
 * Bean describing the attributes of a package
 */
public class PackageInfo implements java.io.Serializable {
   protected int    numelements   = 0;
   protected byte   packagestatus = DropboxGenerator.STATUS_INCOMPLETE;
   protected String desc          = "";
   protected long   expiration    = 0;
   protected long   creation      = 0;
   protected long   committed     = 0;
   protected long   packagesize   = 0;
   protected String packagename   = "";
   protected long   packageid     = -1;
   protected String packageowner  = "";
   protected String packagecompany= "";
   protected long   poolid        = 0;
   protected byte   flags         = (byte)0;
   
  /** 
   * Package flag mask indicating the package is MARKED for the calling user. 
   *  This mask value cannot be set directly on the package, but is set using
   *  the markPackage routine.
   */
   public static final byte MARKED        = (byte)0x01;
   
  /** 
   * Package flag mask indicating the package is Fully downloaded for the calling user. 
   *  This mask value cannot be set directly on the package, but is set only when
   *  the caller has successfully downloaded all files in the package.
   */
   public static final byte COMPLETED     = (byte)0x02;
   
  /** 
   * Package flag mask indicating that the owner will receive a return receipt email
   *  when a user has fully downloaded the package contents.
   */
   public static final byte RETURNRECEIPT = (byte)0x80;
   
  /** 
   * Package flag mask indicating that a notification email will be generated for all
   *  potential recipients of the package. The email will NOT be sent if the recipient
   *  has declined notifications.
   */
   public static final byte SENDNOTIFY    = (byte)0x40;
   
  /** 
   * Package flag mask indicating that the package is Hidden, and so will not be 
   *  visible is a clients Inbox/Trash view unless they have enabled the ShowHidden
   *  session oriented option.
   */
   public static final byte HIDDEN        = (byte)0x20;
   
  /** 
   * Package flag mask indicating that the package may contain ITAR information, 
   *  and so must be treated with accordingly
   */
   public static final byte ITAR          = (byte)0x10;
   
   
  /**
   * Empty/default constructor.
   */
   public PackageInfo() {}
   
  /**
   * Copy construtor
   * @param in Source PackageInfo to copy 
   */
   public PackageInfo(PackageInfo in) {
      numelements    = in.numelements; 
      packagestatus  = in.packagestatus; 
      desc           = in.desc;
      expiration     = in.expiration; 
      creation       = in.creation;
      committed      = in.committed;
      packagesize    = in.packagesize; 
      packagename    = in.packagename; 
      packageid      = in.packageid; 
      packageowner   = in.packageowner; 
      packagecompany = in.packagecompany; 
      flags          = in.flags; 
      poolid         = in.poolid;
   }
   
  /**
   * Set the number of elements (files) contained in the package
   * @param v number of elements (files) contained in the package
   */
   public void setPackageNumElements(int v)    { numelements   = v; }
   
  /**
   * Set the 
   * @param v 
   */
   public void setPackageStatus(byte v)        { packagestatus = v; }
   
  /**
   * Set the milliseconds since 70 GMT that the package will expire
   * @param v milliseconds since 70 GMT that the package will expire
   */
   public void setPackageExpiration(long v)    { expiration    = v; }
   
  /**
   * Set the package description value. This string contains size limited, arbitrary 
   *  information intended to provide context information about the package. 
   * @param v package discription value
   */
   public void setPackageDescription(String v) { desc          = v; }
   
  /**
   * Set the milliseconds since 70 GMT that the package was created
   * @param v milliseconds since 70 GMT that the package was created
   */
   public void setPackageCreation(long v)      { creation      = v; }
   
  /**
   * Set the milliseconds since 70 GMT that the package was committed
   * @param v milliseconds since 70 GMT that the package was committed
   */
   public void setPackageCommitted(long v)     { committed     = v; }
   
  /**
   * Set the size of the package in bytes
   * @param v size of the package in bytes
   */
   public void setPackageSize(long v)          { packagesize   = v; }
   
  /**
   * Set the name of the package. 
   * @param v package name
   */
   public void setPackageName(String v)        { packagename   = v; }
   
  /**
   * Set the unique id for the package
   * @param v unique package id
   */
   public void setPackageId(long v)            { packageid     = v; }
   
  /**
   * Set the user id of the package owner
   * @param v user id of the package owner
   */
   public void setPackageOwner(String v)       { packageowner  = v; }
   
  /**
   * Set the company affiliation of the package owner
   * @param v company of the package owner
   */
   public void setPackageCompany(String v)     { packagecompany= v; }
   
  /**
   * Set the poolid assoicated with the package
   * @param v package pool id value
   */
   public void setPackagePoolId(long v)        { poolid        = v; }
   
  /**
   * Set/reset the MARKED mask value into the package flags
   * @param v True to set MARKED, false to reset
   */
   public void setPackageMarked(boolean v)  { 
      if (v) flags |=  MARKED; 
      else   flags &= ~MARKED;
   }
   
  /**
   * Set/reset the COMPLETED mask value into the package flags
   * @param v True to set COMPLETED, false to reset
   */
   public void setPackageCompleted(boolean v)  { 
      if (v) flags |=  COMPLETED; 
      else   flags &= ~COMPLETED;
   }
   
  /**
   * Set/reset the HIDDENmask value into the package flags
   * @param v True to set HIDDEN, false to reset
   */
   public void setPackageHidden(boolean v)  { 
      if (v) flags |=  HIDDEN; 
      else   flags &= ~HIDDEN;
   }
   
  /**
   * Set/reset the RETURNRECEIPT mask value into the package flags
   * @param v True to set RETURNRECEIPT, false to reset
   */
   public void setPackageReturnReceipt(boolean v)  { 
      if (v) flags |=  RETURNRECEIPT; 
      else   flags &= ~RETURNRECEIPT;
   }
   
  /**
   * Set/reset the SENDNOTIFY mask value into the package flags
   * @param v True to set SENDNOTIFY, false to reset
   */
   public void setPackageSendNotification(boolean v)  { 
      if (v) flags |=  SENDNOTIFY; 
      else   flags &= ~SENDNOTIFY;
   }
   
  /**
   * Set/reset the ITAR mask value into the package flags
   * @param v True to set ITAR, false to reset
   */
   public void setPackageItar(boolean v)  { 
      if (v) flags |=  ITAR; 
      else   flags &= ~ITAR;
   }
   
  /**
   * Set the package flags
   * @param flgs flags to set into package
   */
   public void setPackageFlags(byte flgs)   { flags = flgs;      }
   
  /**
   * Get the number of contained files for the package
   * @return number of contain files in the package
   */
   public int     getPackageNumElements() { return numelements;   }
   
  /**
   * Get the status value for the package
   * @return status value for the package
   */
   public byte    getPackageStatus()      { return packagestatus; }
   
  /**
   * Get the package description value (can be null)
   * @return package description value
   */
   public String  getPackageDescription() { return desc;   }
   
  /**
   * Get the milliseconds since 70 GMT which the package will expire
   * @return milliseconds since 70 GMT which the package will expire
   */
   public long    getPackageExpiration()  { return expiration;    }
   
  /**
   * Get the milliseconds since 70 GMT which the package was created
   * @return milliseconds since 70 GMT which the package was created
   */
   public long    getPackageCreation()    { return creation;      }
   
  /**
   * Get the milliseconds since 70 GMT which the package was committed (may be 0)
   * @return milliseconds since 70 GMT which the packag was committed
   */
   public long    getPackageCommitted()   { return committed;     }
   
  /**
   * Get the size of the package (sum of all contained files)
   * @return size of the package
   */
   public long    getPackageSize()        { return packagesize;   }
   
  /**
   * Get the name of the package
   * @return name of the package
   */
   public String  getPackageName()        { return packagename;   }
   
  /**
   * Get the unique id for the package
   * @return package id
   */
   public long    getPackageId()          { return packageid;     }
   
  /**
   * Get the user id for the owner of the package
   * @return user id for the owner of the package
   */
   public String  getPackageOwner()       { return packageowner;  }
   
  /**
   * Get the company name of the package owner
   * @return company name of the package owner
   */
   public String  getPackageCompany()     { return packagecompany;}
   
  /**
   * Get the pool id of the storage pool associated with the package
   * @return pool id of the storage pool associated with the package
   */
   public long    getPackagePoolId()      { return poolid;        }   
   
  /**
   * Returns TRUE if the package is MARKED, false otherwise
   * @return TRUE if the package is MARKED, false otherwise
   */
   public boolean getPackageMarked()      { return (flags & MARKED)    != 0; }
   
  /**
   * Returns TRUE if the package is COMPLETED, false otherwise
   * @return TRUE if the package is COMPLETED, false otherwise
   */
   public boolean getPackageCompleted()   { return (flags & COMPLETED) != 0; }
   
  /**
   * Returns TRUE if the package is HIDDEN, false otherwise
   * @return TRUE if the package is HIDDEN, false otherwise
   */
   public boolean getPackageHidden()      { return (flags & HIDDEN)    != 0; }
  
  /**
   * Returns TRUE if the package is ITAR, false otherwise
   * @return TRUE if the package is ITAR, false otherwise
   */
   public boolean getPackageItar()        { return (flags & ITAR)      != 0; } 
   
  /**
   * Returns TRUE if the package is RETURNRECEIPT, false otherwise
   * @return TRUE if the package is RETURNRECEIPT, false otherwise
   */
   public boolean getPackageReturnReceipt() { 
      return (flags & RETURNRECEIPT) != 0; 
   }
   
  /**
   * Returns TRUE if the package is SENDNOTIFY, false otherwise
   * @return TRUE if the package is SENDNOTIFY, false otherwise
   */
   public boolean getPackageSendNotification() { 
      return (flags & SENDNOTIFY) != 0; 
   }
   
   
  /**
   * Returns TRUE if the package is MARKED, false otherwise
   * @return TRUE if the package is MARKED, false otherwise
   */
   public boolean isPackageMarked()      { return (flags & MARKED)    != 0; }
   
  /**
   * Returns TRUE if the package is COMPLETED, false otherwise
   * @return TRUE if the package is COMPLETED, false otherwise
   */
   public boolean isPackageCompleted()   { return (flags & COMPLETED) != 0; }
   
  /**
   * Returns TRUE if the package is HIDDEN, false otherwise
   * @return TRUE if the package is HIDDEN, false otherwise
   */
   public boolean isPackageHidden()      { return (flags & HIDDEN)    != 0; }
   
  /**
   * Returns TRUE if the package is ITAR, false otherwise
   * @return TRUE if the package is ITAR, false otherwise
   */
   public boolean isPackageItar()        { return (flags & ITAR)      != 0; } 
   
   
  /**
   * Returns TRUE if the package is RETURNRECEIPT, false otherwise
   * @return TRUE if the package is RETURNRECEIPT, false otherwise
   */
   public boolean isPackageReturnReceipt() { 
      return (flags & RETURNRECEIPT) != 0; 
   }
   
  /**
   * Returns TRUE if the package is SENDNOTIFY, false otherwise
   * @return TRUE if the package is SENDNOTIFY, false otherwise
   */
   public boolean isPackageSendNotification() { 
      return (flags & SENDNOTIFY) != 0; 
   }
   
   
  /**
   * Get the package flags for the package
   * @return package flags for the package
   */
   public byte    getPackageFlags()       { return flags;         }
   
   public String toString() {
      return "PackageInfo" +
         Nester.nest("\npackname   = " + packagename +
                     "\npackowner  = " + packageowner+
                     "\npackcompany= " + packagecompany+
                     "\nnumelements= " + numelements +
                     "\ndescription= " + desc  +
                     "\nexpiration = " + expiration  +
                     "\npoolid     = " + poolid  +
                     "\ncreation   = " + creation    +
                     "\npacksize   = " + packagesize +
                     "\npackid     = " + packageid   +
                     "\npackflags  = " + flags       +
                     "\npackstatus = " + packagestatus);
   }
   
   public int hashcode()           { return (int)(packageid ^ (packageid >> 32)); }
   
  /**
   * Compare objects to test for a match. 
   * Compare packageid to determine a match
   */
   public boolean equals(Object o) { 
      if (o instanceof PackageInfo) {
         return packageid == ((PackageInfo)o).getPackageId();
      }
      return false;
   }
}
