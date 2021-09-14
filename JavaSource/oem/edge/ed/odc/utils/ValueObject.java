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

package oem.edge.ed.odc.utils;

import java.io.Serializable;

/*
 * The ValueObject is the abstract class that all ValueObjects in our
 * data persistence framework descend off of.  It provides a number of methods,
 * including get/set methods for the VO status flags and the rowVersion method.
 *
 */
public abstract class ValueObject implements Serializable{
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
  /*Below are the VO status flags*/
  private boolean insertFlag = false;
  private boolean updateFlag = false;
  private boolean deleteFlag = false;
  private long    rowVersion = 0;

  /*
   * Retrieves the insertFlag.  The insertFlag is used to tell the DAO that data in the
   * the ValueObject should be inserted into the database.
   *
   */
  public boolean getInsertFlag(){
    return insertFlag;
  }

  /*
   * Retrieves the deleteFlag.  The deleteFlag is used to tell the DAO that the data
   * in the ValueObject should be deleted from the database.
   *
   */
  public boolean getDeleteFlag(){
    return deleteFlag;
  }

  /*
   * Retrieves the updateFlag.  The updateFlag is to tell the DAO that the data in the
   * ValueObject should be deleted from the database.
   *
   */
  public boolean getUpdateFlag(){
    return updateFlag;
  }

  /*
   * Retrieves the rowVersion.  The rowVersion holds the current rowVersion of the record
   * and is used by the DAO.update() method to enforce optimistic locking exceptions.
   *
   */
  public long getRowVersion(){
    return rowVersion;
  }

  /*Sets the insertFlag and sets all other status flags to false*/
  public void setInsertFlag(boolean pFlag){
    insertFlag = pFlag;
    deleteFlag = false;
    updateFlag = false;
  }

  /*Sets the updateFlag and sets all other status flags to false*/
  public void setUpdateFlag(boolean pFlag){
    insertFlag = false;
    deleteFlag = false;
    updateFlag = pFlag;

  }

  /*Sets the deleteFlag and sets all other status flags to false*/
  public void setDeleteFlag(boolean pFlag){
    insertFlag = false;
    deleteFlag = pFlag;
    updateFlag = false;
  }

  /*Sets the rowVersion property*/
  public void setRowVersion(long pRowVersion){
    rowVersion = pRowVersion;
  }

  /*Resets all VO status flags to false*/
  public void resetFlags(){
    insertFlag = false;
    deleteFlag = false;
    updateFlag = false;
  }

}
