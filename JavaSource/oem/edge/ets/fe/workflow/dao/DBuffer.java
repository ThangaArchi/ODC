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

import java.sql.*;
import java.util.*;
import java.io.*;

import oem.edge.ets.fe.workflow.util.OrderedMap;


public class DBuffer implements Cloneable, Serializable{
	private int rows				= 0;
	private int cols				= 0;
	private OrderedMap buffer	= null;

	/**
	 * Default constructor for the DBuffer
	 */
	public DBuffer(){
		buffer = new OrderedMap();
	}

	/**
	 * Returns the number of rows
	 *
	 * @return int number of rows
	 */
	public int getRows(){
		return rows;
	}

	/**
	 * Returns the number of columns
	 *
	 * @return int number of columns
	 */
	public int getColumns(){
		return cols;
	}

	/**
	 * Check if DBffer is empty
	 *
	 * @return boolean empty or not
     */
	public boolean isEmpty(){
		return (rows == 0);
	}

	/**
	 * Add a column to the buffer
	 * 
	 * @param	dbColumn	DBColumn
	 */
	public synchronized void addDBColumn(DBColumn dbColumn){
		buffer.put(dbColumn.getColumnName(),dbColumn);
		cols++;
		int size = dbColumn.size();
		if(rows == 0 || rows > size){
			rows = size;
		}
	}

	/**
	 * To clean up the array
     */
	public void clear(){
		this.rows = 0;
		this.cols = 0;
		buffer.clear();
    }

	/**
	 * to store the query result into the DBuffer
	 *
	 * @param rs the JDBC ResultSet
	 * @exception SQLException JDBC Error
	 */
	public synchronized void addDBColumns(ResultSet rs) throws SQLException{
		try{
			clear();
			ResultSetMetaData meta = rs.getMetaData();
			int colCount = meta.getColumnCount();

			DBColumn[] dbColumn = new DBColumn[colCount];
			for( int i = 1 ; i <= colCount; i++){
				dbColumn[i - 1] = new DBColumn(meta.getColumnName(i).toUpperCase(),meta.getColumnType(i));
			}

			while(rs.next()){
				rows++;
				for( int i = 1; i <= colCount; i++){
					int type = meta.getColumnType(i);
					if(type == 93){
						dbColumn[i-1].add(rs.getTimestamp(i));
					}else{
						dbColumn[i-1].add(rs.getString(i));
                    }
				}
			}

			for( int i = 0; i < colCount ; i++){
				addDBColumn(dbColumn[i]);
			}
		}catch(SQLException e){
			throw e;
		}
	}

	public DBColumn getDBColumn(String colName){
		return ((DBColumn)buffer.get(colName.toUpperCase()));
    }

	public DBColumn getDBColumn(int index){
		return ((DBColumn)buffer.get(index));
    }

	 
	/** 
	 * to check if the cell value is null by giving the 
	 * rowindex and column name.
	 *
	 * @return boolean 
	 * @param rowIndex the row index
	 * @param colName the column name
	 */
	public boolean isNull(int rowIndex, String colName) throws DBHelperException{
		String x = getString(rowIndex,colName);
		return (x == null || x.length() == 0);
	}

	/** 
	 * to check if the cell value is null by given the rowindex and colindex
	 *
	 * @return boolean 
	 * @param rowIndex the row index
	 * @param colIndex the column index
	 */
	public boolean isNull(int rowIndex, int colIndex) throws DBHelperException{
		String x = getString(rowIndex, colIndex);
		return (x == null || x.length() == 0);
	}

	/**
	 * to get a String value from the DBuffer by rowindex and column name
	 *
	 * @return String a string representation of value store in the DBuffer
	 * @param rowIndex the row index
	 * @param colName the column name
 	 */
	public String getString(int rowIndex,String colName) throws DBHelperException{
		DBColumn column = getDBColumn(colName.toUpperCase());
		if( column != null && rowIndex < column.size())
			return ((String) column.get(rowIndex));
		else
			return null;
	}

	/**
	 * to get a String value from the DBuffer by rowindex and column index
	 *
	 * @return String a string representation of value store in the DBuffer
	 * @param rowIndex the row index
	 * @param colIndex the column index
	 * @exception DashboardHelperException index error
	 */
	public String getString(int rowIndex,int colIndex) throws DBHelperException	{
		DBColumn column = getDBColumn(colIndex);
		if( column != null && rowIndex < column.size())
			return ((String) column.get(rowIndex));
		else
			return null;
	}

	/** 
	 * to get a array of String values from the DBuffer for the column
	 * indicated by the column name
	 *
	 * @return String[] a array of string 
	 * @param colName the column name
	 */
	public String[] getString(String colName) throws DBHelperException{
		DBColumn column = getDBColumn(colName);
		if( column != null){
			String[] a = new String[column.size()];
			for( int i = 0 ; i < a.length ; i ++)
				a[i] = new String((String)column.get(i));
			return a;
		}
		return null;
	}

	/** 
	 * to get a array of String values from the DBuffer for the column
	 * indicated by the column index
	 *
	 * @return String[] a array of string 
	 * @param Index the column index
	 */
	public String[] getString(int index) throws DBHelperException{
		DBColumn column = getDBColumn(index);
		if( column != null){
			String[] a = new String[column.size()];
			for( int i = 0 ; i < a.length; i ++){
				if(column.get(i) == null)
					a[i] = null;
				else
					a[i] = new String((String)column.get(i));
			}
			return a;
		}
		return null;
	}

	/** 
	 * to get a array of String values from the DBuffer for the column names
	 *
	 * @return String[] a array of string, null if DBuffer is empty. 
	 * 
	 */
	public String[] getColNames(){
		String[] a = new String[buffer.size()];
        buffer.toArray(a);
		return a;
	}


	/** 
	 * to get a integer value from the DBuffer by given the row index 
	 * and the column names
	 *
	 * @return int integer value 
	 * 
	 */
	public int getInt(int rowIndex, String colName) throws DBHelperException{
		DBColumn column = getDBColumn(colName);
        if(column == null) return 0;
		return Integer.parseInt((column.get(rowIndex)!=null)?(String)column.get(rowIndex):"0");
	}

	  
	/** 
	 * to get a integer value from the DBuffer by given the row index 
	 * and the column index
	 *
	 * @return int integer value 
	 */
	public int getInt(int rowIndex, int colIndex) throws DBHelperException{
		DBColumn column = getDBColumn(colIndex);
        if(column == null) return 0;
		return Integer.parseInt((column.get(rowIndex)!=null)?(String)column.get(rowIndex):"0");
	}

  /** 
   * to get a short value from the DBuffer by given the row index 
   * and the column names
   *
   * @return short a short integer value
   */
  public short getShort(int rowIndex, String colName) throws DBHelperException{
      DBColumn column = getDBColumn(colName);
      if(column == null) return 0;
      return Short.parseShort((column.get(rowIndex)!=null)?(String)column.get(rowIndex):"0");
  }

  
  /** 
   * to get a short value from the DBuffer by given the row index 
   * and the column index
   *
   * @return short a short integer value
   */
  public short getShort(int rowIndex, int colIndex) throws DBHelperException{
      DBColumn column = getDBColumn(colIndex);
      if(column == null) return 0;
      return Short.parseShort((column.get(rowIndex)!=null)?(String)column.get(rowIndex):"0");
  }


  /** to get a long value from the DBuffer by given the row index 
   * and the column names
   *
   * @return long a long integer value
   * 
   */
  public long getLong(int rowIndex, String colName) throws DBHelperException{
      DBColumn column = getDBColumn(colName);
      if(column == null) return 0;
      return Long.parseLong((column.get(rowIndex)!=null)?(String)column.get(rowIndex):"0");
  }

  
  /**
   * to get a long value from the DBuffer by given the row index 
   * and the column index
   *
   * @return long a long integer value
   */
  public long getLong(int rowIndex, int colIndex) throws DBHelperException{
      DBColumn column = getDBColumn(colIndex);
      if(column == null) return 0;
      return Long.parseLong((column.get(rowIndex)!=null)?(String)column.get(rowIndex):"0");
  }

	
  /**
   * to get a double value from the DBuffer by given the row index 
   * and the column names
   *
   * @return double a double value
   */
  public double getDouble(int rowIndex, String colName) throws DBHelperException{
      DBColumn column = getDBColumn(colName);
      if(column == null) return 0;
      return Double.parseDouble((column.get(rowIndex)!=null)?(String)column.get(rowIndex):"0");
  }
  
  /** 
   * to get a double value from the DBuffer by given the row index 
   * and the column index
   *
   * @return double a double value
   */
  public double getDouble(int rowIndex, int colIndex) throws DBHelperException{
      DBColumn column = getDBColumn(colIndex);
      if(column == null) return 0;
      return Double.parseDouble((column.get(rowIndex)!=null)?(String)column.get(rowIndex):"0");
  }

  /** to get a float value from the DBuffer by given the row index 
   * and the column names
   *
   * @return float a float value
   * 
   */
  public float getFloat(int rowIndex, String colName) throws DBHelperException{
      DBColumn column = getDBColumn(colName);
      if(column == null) return 0;
      return Float.parseFloat((column.get(rowIndex)!=null)?(String)column.get(rowIndex):"0");
  }
  
  /** to get a double value from the DBuffer by given the row index 
   * and the column index
   *
   * @return double a double value
   * 
   */
  public float getFloat(int rowIndex, int colIndex) throws DBHelperException{
      DBColumn column = getDBColumn(colIndex);
      if(column == null) return 0;
      return Float.parseFloat((column.get(rowIndex)!=null)?(String)column.get(rowIndex):"0");
  }

  /** to get a object from the DBuffer by given the row index 
   * and the column index
   *
   * @return Object an deserialized object or the object type defined java.lang.* 
   */
  public Object getObject(int rowIndex, int colIndex) throws DBHelperException{
      DBColumn column = getDBColumn(colIndex);
      if(column == null) return null;
      return column.get(rowIndex);
  }


  /** to get a object from the DBuffer by given the row index 
   * and the column names.
   *
   * @return Object a deserialized object
   */
  public Object getObject(int rowIndex, String colName) throws DBHelperException{
      DBColumn column = getDBColumn(colName);
      if(column == null) return null;
      return column.get(rowIndex);
  }

  /** to get a <code>java.sql.Date</code> value from the DBuffer by given the row index 
   * and the column names
   *
   * @return Date a java.sql.Date value
   * 
   */
  public java.sql.Date getDate(int rowIndex, String colName) throws DBHelperException{
      DBColumn column = getDBColumn(colName);
      if(column == null) return null;
      Timestamp ts  = (java.sql.Timestamp)column.get(rowIndex);
	  if(ts == null) return null;
     java.sql.Date date = new java.sql.Date(ts.getTime());
      return date;
  }
  
  /** to get a <code>java.sql.Date</code> value from the 
   * DBuffer by given the row index 
   * and the column index
   *
   * @return Date a java.sql.Date value
   * 
   */
  public java.sql.Date getDate(int rowIndex, int colIndex) throws DBHelperException{
      DBColumn column = getDBColumn(colIndex);
      if(column == null) return null;
      Timestamp ts  = (java.sql.Timestamp)column.get(rowIndex);
	  if(ts == null) return null;
      java.sql.Date date = new java.sql.Date(ts.getTime());
      return date;
  }


  /** to get a <code>java.sql.Date</code> value from the DBuffer by given the row index 
   * and the column names
   *
   * @return Date a java.sql.Date value
   * 
   */
  public java.sql.Date getDateTime(int rowIndex, String colName) throws WorkflowDBException{
      DBColumn column = getDBColumn(colName);
      if(column == null) return null;
      Timestamp ts  = (java.sql.Timestamp)column.get(rowIndex);
	  if(ts == null) return null;
      java.sql.Date date = new java.sql.Date(ts.getTime());
      return date;
  }
  
  /** to get a <code>java.sql.Date</code> value from the 
   * DBuffer by given the row index 
   * and the column index
   *
   * @return Date a java.sql.Date value
   * 
   */
  public java.sql.Date getDateTime(int rowIndex, int colIndex) throws WorkflowDBException{
      DBColumn column = getDBColumn(colIndex);
      if(column == null) return null;
      Timestamp ts  = (java.sql.Timestamp)column.get(rowIndex);
	  if(ts == null) return null;
      java.sql.Date date = new java.sql.Date(ts.getTime());
      return date;
  }

  private java.sql.Date getDate(Timestamp ts){
      Calendar cal = Calendar.getInstance();
      cal.setTime((java.util.Date)ts);
      cal.set(Calendar.HOUR, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      java.util.Date date = cal.getTime();
      return new java.sql.Date(date.getTime());
  }
}