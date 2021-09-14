/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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




/*
 * Created on Feb 6, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.workflow.dao;

import java.util.*;
import java.io.*;

public class DBColumn extends ArrayList implements Cloneable,Serializable{
  private String columnName = null;
  private String typeName   = null;
  private int type          = 0;
  

  /** default constructor to set the column name and the value type
   * for the DBColumn
   */
  public DBColumn(String columnName,int type){
	  super();
	  this.columnName   = columnName.toUpperCase();
	  this.type         = type;
  }

  /** default constructor to set the column name, the value type, and
   * the initial capacity of the DBColumn
   *
   */
  public DBColumn(String columnName,int type,int initCapacity){
	  super(initCapacity);
	  this.columnName = new String(columnName.toUpperCase());
	  this.type = type;
  }

  /** return the type of the DBColumn
   *
   * @return int the type of the DBColumn
   * @see java.sql.Types
   */
  public int getType(){
	  return type;
  }

  /** return the column name
   *
   * @return String the column name
   */
  public String getColumnName(){
	  return columnName;
  }
}