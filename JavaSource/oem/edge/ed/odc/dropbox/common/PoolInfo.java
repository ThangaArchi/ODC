package oem.edge.ed.odc.dropbox.common;

import  oem.edge.ed.odc.util.Nester;

import java.lang.*;
import java.util.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2006                                          */
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
 * This class is a bean describing a Storage Pool for the Dropbox. Storage
 *  pools provide a bounded (size) storage area that can be associated with a
 *  package at package creation time. All files uploaded to that package will
 *  store its data in the areas hooked to the storage pool.
 *
 * The primary reason for caring about storage pools:
 * <ol>
 *   <li>The space is reserved for the storagepool in question. This means an
 *       organization can ensure no outages (shortage of dasd) by owning and 
 *       managing their own group.
 *   </li>
 *   <li>A storage pool has an associated expiration policy</li>
 * </ol>
 * The expiration policy connected to the storage pool is the big deal here,
 *  since the default/public pool is intended to have a short shelf life.
 * If an organization wishes to reserve space for their exclusive use, and
 *  have a longer shelf life, that is possible.
 */
public class PoolInfo implements java.io.Serializable {
   protected String name          = "";
   protected String desc          = "";
   protected long   poolid        = 0;
   protected int    maxdays       = 0;
   protected int    defaultdays   = 0;
   
  /**
   * Empty/default constructor.
   */
   public PoolInfo() { }
   
  /**
   * Copy construtor
   * @param p Source PoolInfo to copy 
   */
   public PoolInfo(PoolInfo p) {
      name        = p.name;
      desc        = p.desc;
      poolid      = p.poolid;
      maxdays     = p.maxdays;
      defaultdays = p.defaultdays;
   }
   
  /**
   * Set the pool id associated with the storage pool
   * @param v pool id to set into object
   */
   public void   setPoolId(long v)            { poolid = v;         }
   
  /**
   * Get the pool id associated with the storage pool
   * @return long pool id associated with the storage pool
   */
   public long   getPoolId()                  { return poolid;      }
   
  /**
   * Set the pool name associated with the storage pool
   * @param v pool name to set into object
   */
   public void   setPoolName(String v)        { name = v;           }
  /**
   * Get the pool name associated with the storage pool
   * @return String pool name associated with storage pool
   */
   public String getPoolName()                { return name;        }
   
  /**
   * Set the maxium retention days associated with the storage pool
   * @param v maximum retention days to set into object
   */
   public void   setPoolMaxDays(int v)        { maxdays = v;        }
  /**
   * Get the maxium retention days associated with the storage pool
   * @return int maximum retention days associated with storage pool
   */
   public int    getPoolMaxDays()             { return maxdays;     }
   
  /**
   * Set the default retention days associated with the storage pool. When
   *  package is created, the default days will be selected for its
   *  expiration unless overridden by the call.
   * @param v default retention days to set into object
   */
   public void   setPoolDefaultDays(int v)    { defaultdays = v;    }
  /**
   * Get the default retention days associated with the storage pool
   * @return int default retention days associated with storage pool
   */
   public int    getPoolDefaultDays()         { return defaultdays; }
   
  /**
   * Set the pool description associated with the storage pool
   * @param v pool description to set into object
   */
   public void   setPoolDescription(String v) { desc = v;           }
  /**
   * Get the pool description associated with the storage pool
   * @return String pool description associated with the storage pool
   */
   public String getPoolDescription()         { return desc;        }
   
   public String toString() {
      return "PoolInfo" +
         Nester.nest("\npoolname   = " + name  +
                     "\npoolid     = " + poolid +
                     "\ndescription= " + desc +
                     "\nmaxdays    = " + maxdays +
                     "\ndefaultdays= " + defaultdays);
   }
   
   public int hashCode() {
      return (int)poolid;
   }
   
  /**
   * Compare objects to test for a match. 
   * Company poolid and pool name are used to determin a match.
   */
   public boolean equals(Object o) {
      if (o instanceof PoolInfo) {
         PoolInfo to = (PoolInfo)o;
         if (poolid == to.poolid) {
            if (name != null && to.name != null) {
               if (!name.equals(to.name)) return false;
            } else if (name != to.name)   return false;
            
           // Thats close enough for me
            return true;
         } 
      } 
      return false;
   }
}
