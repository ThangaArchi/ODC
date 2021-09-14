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

import java.util.ArrayList;
import java.sql.*;


import oem.edge.common.*;
import oem.edge.ets.fe.ETSDBUtils;


import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;

public class DBAccess 
{
	private	Connection	conn				=		null;
	private static Log logger				=		WorkflowLogger.getLogger(DBAccess.class);
	private QueryCaching queryCache			=		QueryCaching.getInstance();
	private ArrayList listData				=		null;
	private PreparedStatement ps			=		null;
	private DBuffer buffer					=		null;
	private String query					=		null;
		
	public DBAccess(){
        logger.info("This is the test logger implemented");
	   try{
		   
			conn 			  = ETSDBUtils.getConnection();	
			conn.setAutoCommit(false);
			listData		  = new ArrayList();
			buffer			  = new DBuffer();
	  
		}catch(Exception sqle){
			logger.error("Unable to create the connection : ",sqle);
		}
	}
	
	public DBAccess(String backend){
        logger.info("This get backend connection");
        Global.Init();
        GenConnect db= null;
	   try{
	   		db= new GenConnect();
	   		db.makeConn();
			conn = db.conn;	
			listData		  = new ArrayList();
			buffer			  = new DBuffer();
	  
		}catch(Exception sqle){
			logger.error("Unable to create the connection : ",sqle);
		}
	}
	 
	public Connection getConnection(){
	return conn;
	}
 

	/**
	 * Returns the number of rows
	 *
	 * @return int number of rows
	 */
	public int getRows(){
		return buffer.getRows();
	}

	/**
	 * Returns the number of columns
	 *
	 * @return int number of columns
	 */
	public int getColumns(){
		return buffer.getColumns();
	}

	/**
	 * Returns the query
	 *
	 * @return String query
	 */
	public String getQuery(){
		return query;
	}

	/**
	 * Prepare the Query
	 *
	 * @param	queryName		String Query Name
	 * @exception	java.sql.SQLException JDBC Error
	 */
	public void prepareQuery(String qryName) throws SQLException{
		query	= queryCache.getQuery(qryName);
		
		if(ps != null){
			logger.warn("Prepared Statement not executed ! Closing it now...");
			try{
				ps.close();
				ps = null;
			}catch(Exception psexception){
				logger.error("Exception while closing prepared statement ",psexception);
				System.out.println("Exception while closing prepared statement "+psexception.toString());
			}
		}
		if (conn != null)
		{
			ps		= conn.prepareStatement(query);
		}
		else{
			conn	= getConnection();
			ps		= conn.prepareStatement(query);
		}
	}


	/**
	 * Prepare the Query with data array
	 *
	 * @param	queryName	String Query Name
	 * @param	data		ArrayList list of data
	 * @exception	java.sql.SQLException JDBC Error
	 */
	public void prepareQuery(String qryName, ArrayList data)
		throws SQLException
	{
		query	= queryCache.getQuery(qryName);
		if(ps != null){
			 logger.warn("Prepared Statement not executed ! Closing it now...");
			try{
				ps.close();
				ps = null;
			}catch(Exception psexception){
				logger.error("Exception while closing prepared statement ",psexception);
			}
		}
		if (conn != null)
		{
			ps		= conn.prepareStatement(query);
		}
		else{
			conn	= getConnection();
			ps		= conn.prepareStatement(query);
		}
		listData.clear();
		setObject(data);
	}



/**
	 * Prepare the Query
	 *
	 * @param	queryName		String Query Name
	 * @exception	java.sql.SQLException JDBC Error
	 */
	public void prepareDirectQuery(String actualQuery) throws SQLException{
		query	= actualQuery.trim();
		if(ps != null){
			  logger.warn("Prepared Statement not executed ! Closing it now...");
			try{
				ps.close();
				ps = null;
			}catch(Exception psexception){
				logger.error("Exception while closing prepared statement ",psexception);
			}
		}

		if (conn != null)
		{
			ps		= conn.prepareStatement(query);
		}
		else{
			conn	= getConnection();
			ps		= conn.prepareStatement(query);
		}
		listData.clear();
	}


/**
	 * Prepare the Query wit data array
	 *
	 * @param	queryName	String Query Name
	 * @param	data		ArrayList list of data
	 * @exception	java.sql.SQLException JDBC Error
	 */
	public void prepareDirectQuery(String actualQuery, ArrayList data)
		throws SQLException
	{
		query	= actualQuery.trim();
		if(ps != null){
				logger.warn("Prepared Statement not executed ! Closing it now...");
			try{
				ps.close();
				ps = null;
			}catch(Exception psexception){
				logger.error("Exception while closing prepared statement",psexception);
			}
		}
		if (conn != null)
		{
			ps		= conn.prepareStatement(query);
		}
		else{
			conn	= getConnection();
			ps		= conn.prepareStatement(query);
		}
		listData.clear();
		setObject(data);
	}

	/** 
   * Batch Query insert/update
   * @return void
   * @param qryName query name
   * @param values a array list of array list data
   * @exception SQLException JDBC Error
   * @exception DashboardDBException dbaccess exception
   */
  
  public boolean batchQuery(String qryName, ArrayList data) 
	  throws SQLException, WorkflowDBException
  {
  	query	= queryCache.getQuery(qryName);
  	boolean chkStatus = false;
	String dataType = null;
	String paramsStr = " Data :";
	try {
		int arr[] = null;	 				
		if (data != null && data.size() > 0) {

			if (conn != null)
			{
				ps = conn.prepareStatement(query);
			}
			else{
				conn	= getConnection();
				ps = conn.prepareStatement(query);
			}
						
			for (int i = 0; i < data.size(); i++) {
				ArrayList params = (ArrayList) data.get(i);
				 for (int j = 0; j < params.size(); j++) {
				   	if(params.get(j) == null){
						ps.setNull(j + 1,Types.NULL);
					 }else{
					ps.setObject(j + 1, params.get(j));
					}

					paramsStr = paramsStr + params.get(j) ;
					if ( (j+1) != params.size() ) {
						paramsStr = paramsStr + ", ";
					}
				} 
				System.out.println("....................................paramsStr..."+paramsStr);
				ps.addBatch();
			}
			 arr = ps.executeBatch();
		}
		chkStatus = true;
	}
	catch (BatchUpdateException  e) {
		  System.err.println("SQLException: " + e.getMessage());
	      System.err.println("SQLState: " + e.getSQLState());
	      System.err.println("Vendor: " + e.getErrorCode());
	 }
	catch (SQLException e) {
		  System.err.println("SQLException: " + e.getMessage());
	      System.err.println("SQLState: " + e.getSQLState());
	      System.err.println("Vendor: " + e.getErrorCode());
	}
	finally {
		try {	
			if(ps!=null) {
				ps.close();
			}
		}
		catch(Exception e) {
		}
	}
	return chkStatus;
}
     /**
      * 
      * this method clears the ListData
      * @return
      * @throws SQLException
      */
  public void clearParameters(){
  	listData.clear();
  }

	 /**
	 * Execute the query
	 *
	 * @return int number of rows affected
	 */
	public int execute() throws SQLException{
		ResultSet rs        = null;
		int rowsAffected    = 0; 
		try{
			if(query != null){
				if(query.substring(0,6).toLowerCase().startsWith("select")){
					rs  = ps.executeQuery();
					buffer.addDBColumns(rs);
					rowsAffected = buffer.getRows();
				}else{
					rowsAffected  = ps.executeUpdate();
				}
				logger.debug("Executing the following Query :\n" + query);
				logger.debug("Data :" + listData);
				
								
			}
		}catch(Exception e){
			logger.error("Error in the execution of Prepared Statement: \n" + query + "\nData : " + listData, e);
			  System.out.println("Error in the execution of Prepared Statement:"+e);
		}finally{ 
			try{
				if(rs != null) rs.close();
			}catch(Exception e){}

		}
		return rowsAffected;
	}

	 /**
	 * Executes the query directly without prepared query
	 * 
	 * @param   query   String - the sql query
	 */
	public int executeQuery(String actualQuery) throws SQLException{
		Statement stmt      = null;
		ResultSet rs        = null;
		int rowsAffected    = 0;
		if(actualQuery == null) return -1;
		query = actualQuery.trim();
		if(ps != null){
			  logger.warn("DBAccess is already associated with prepared statement");
			  logger.warn("PreparedStatement will be closed and cleared");
			try{
				ps.close();
				ps = null;
			}catch(Exception e){}
			buffer				= new DBuffer();
			listData			= new ArrayList();
		}
		try{
			stmt = conn.createStatement();
			logger.debug("Executing the following Query :\n" + query);
			if(query.substring(0,6).toLowerCase().startsWith("select")){
				rs  = stmt.executeQuery(query);
				buffer.addDBColumns(rs);
				rowsAffected = buffer.getRows();
			}else{
				rowsAffected  = stmt.executeUpdate(query);
			}
		}catch(Exception e){
			//logger.error("Error in the execution of Prepared Statement: " + e);
		}finally{
			try{
				if(rs != null) rs.close();
			}catch(Exception e){}

			try{
				if(stmt != null) stmt.close();
			}catch(Exception e){}
		}
		return rowsAffected;
	}

	/**
	 * Set the object in the prepared statement
	 *
	 * @param	paramIndex	int parameter index
	 * @param	data		Object the data need to be set
	 */
	public void setObject(int paramIndex, Object data) throws SQLException{
		if(data instanceof java.math.BigDecimal)
			ps.setObject(paramIndex,data);
		else if (data instanceof Boolean)
			ps.setObject(paramIndex,data);
		else if (data instanceof Integer)
			ps.setObject(paramIndex,data);
		else if (data instanceof Long)
			ps.setObject(paramIndex,data);
		else if (data instanceof Double)
			ps.setObject(paramIndex,data);
		else if (data instanceof Float)
			ps.setObject(paramIndex,data);
		else if (data instanceof java.sql.Date){
			if(data!=null)
				ps.setObject(paramIndex,data);
			else
				setNull(paramIndex,Types.NULL);
		}
		else if (data instanceof java.sql.Time)
		{
			if(data!=null)
				ps.setObject(paramIndex,data);
			else
				setNull(paramIndex,Types.NULL);
		}
		else if (data instanceof java.sql.Timestamp)
		{
			if(data!=null)
				ps.setObject(paramIndex,data);
			else
				setNull(paramIndex,Types.NULL);
		}
		else if(data instanceof String){
			if(((String)data).length() < 1)
				setNull(paramIndex,Types.VARCHAR);
			else
				setString(paramIndex,(String)data);
		}
		listData.add(String.valueOf(data));
	}


	/**
	 * Sets the data in the prepared statement
	 *
	 * @return int number of rows
	 */
	public void setObject(ArrayList dataList) throws SQLException{
		Object obj = null;
		if(dataList != null){
			for(int i=0; i < dataList.size(); i++){
				setObject(i+1, dataList.get(i));
			}
		}
	}

	/** To set the boolean value for the parameter used in the sqlstatement
	* @return void
	* @param paramIndex the parameter index
	* @param x the boolean value
	* @exception SQLException JDBC Error
	*/
	public void setBoolean(int paramIndex,boolean x) throws SQLException{
		ps.setBoolean(paramIndex,x);
		listData.add(String.valueOf(x));
	}

	/** To set the <code>String</code> value for the parameter used 
	*  in the sqlstatement
	*
	* @return void
	* @param paramIndex the parameter index
	* @param x the String value
	* @exception SQLException JDBC Error
	*/
	public void setString (int paramIndex,String x) throws SQLException{
		if(x == null){
			setNull(paramIndex, Types.VARCHAR);
		}else{
			ps.setString(paramIndex,x);
		}
		listData.add(String.valueOf(x));
	}

	/** To set the <code>java.sql.Date</code> value for the parameter used 
	* in the sqlstatement
	* @return void
	* @param paramIndex the parameter index
	* @param x the java.sql.Date value
	* @exception SQLException JDBC Error
	*/
	public void setDate(int paramIndex, java.sql.Date x) throws SQLException{
		if(x == null){
			setNull(paramIndex, Types.DATE);
		}else{
			ps.setDate(paramIndex,x);
		}
		listData.add(String.valueOf(x));
	}

	/** To set the <code>java.sql.Date with time</code> value for the parameter used 
	* in the sqlstatement
	* @return void
	* @param paramIndex the parameter index
	* @param x the java.sql.Date value
	* @exception SQLException JDBC Error
	*/
	public void setDateTime(int paramIndex, java.sql.Date x) throws SQLException{
		if(x == null){
			setNull(paramIndex, Types.DATE);
		}else{
			Timestamp ts    = new Timestamp(x.getTime());
			ps.setTimestamp(paramIndex,ts);
		}
		listData.add(String.valueOf(x));
	}

  
	/** To set the <code>Time</code> value for the parameter used 
	* in the sqlstatement
	* @return void
	* @param paramIndex the parameter index
	* @param x the java.sql.Time value
	* @exception SQLException JDBC Error
	*/
	public void setTime(int paramIndex,Time x) throws SQLException{
		if(x == null){
			setNull(paramIndex, Types.TIME);
		}else{
			ps.setTime(paramIndex,x);
		}
		listData.add(String.valueOf(x));
	}


	/** To set the <code>long</code> value for the parameter used 
	* in the sqlstatement
	* @return void
	* @param paramIndex the parameter index
	* @param x the long value
	* @exception SQLException JDBC Error
	*/
	public void setLong(int paramIndex,long x) throws SQLException{
		if(x == -1){
			setNull(paramIndex, Types.BIGINT);
		}else{
			ps.setLong(paramIndex,x);
		}
		listData.add(String.valueOf(x));
	}

	/** To set the <code>null</code> value for the parameter used 
	* in the sqlstatement
	* @return void
	* @param paramIndex the parameter index
	* @param type the java.sql.Type value
	* @exception SQLException JDBC Error
	*/
	public void setNull(int paramIndex,int type) throws SQLException{
		ps.setNull(paramIndex,type);
	}

	/** To set the <code>float</code> value for the parameter used 
	* in the sqlstatement
	* @return void
	* @param paramIndex the parameter index
	* @param x the float value
	* @exception SQLException JDBC Error
	*/
	public void setFloat(int paramIndex, float x) throws SQLException{
		if(x == -1){
			setNull(paramIndex, Types.FLOAT);
		}else{
			ps.setFloat(paramIndex,x);
		}
		listData.add(String.valueOf(x));
	}

	/** To set the <code>double</code> value for the parameter used 
	* in the sqlstatement
	* @return void
	* @param paramIndex the parameter index
	* @param x the double value
	* @exception SQLException JDBC Error
	*/
	public void setDouble(int paramIndex,double x) throws SQLException{
		if(x == -1){
			setNull(paramIndex, Types.DOUBLE);
		}else{
			ps.setDouble(paramIndex,x);
		}
		listData.add(String.valueOf(x));
	}

	/** To set the <code>int</code> value for the parameter used 
	* in the sqlstatement
	* @return void
	* @param paramIndex the parameter index
	* @param x the int value
	* @exception SQLException JDBC Error
	*/
	public void setInt(int paramIndex,int x) throws SQLException{
		if(x == -1){
			setNull(paramIndex, Types.INTEGER);
		}else{
			ps.setInt(paramIndex,x);
		}
		listData.add(String.valueOf(x));
	}

	/** To set the <code>short</code> value for the parameter used 
	* in the sqlstatement.
	* @return void
	* @param paramIndex the parameter index
	* @param x the short value
	* @exception SQLException JDBC Error
	*/
	public void setShort(int paramIndex,short x) throws SQLException{
		if(x == -1){
			setNull(paramIndex, Types.SMALLINT);
		}else{
			ps.setShort(paramIndex,x);
		}
		listData.add(String.valueOf(x));
	}


	/** 
	 * to check if the cell value is null by giving the 
	 * rowindex and column name.
	 *
	 * @return boolean 
	 * @param rowIndex the row index
	 * @param colName the column name
	 */
	public boolean isNull(int rowIndex, String colName) throws WorkflowDBException,DBHelperException{
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
	public boolean isNull(int rowIndex, int colIndex) throws WorkflowDBException,DBHelperException{
		String x = getString(rowIndex, colIndex);
		return (x == null || x.length() == 0);
	}

	/** 
	 * to get a array of String values from the DBuffer for the column
	 * indicated by the column name
	 *
	 * @return String[] a array of string 
	 * @param colName the column name
	 */
	public String[] getString(String colName) throws WorkflowDBException,DBHelperException{
		return buffer.getString(colName);
	}

	/**
	 * to get a String value from the DBuffer by rowindex and column name
	 *
	 * @return String a string representation of value store in the DBuffer
	 * @param rowIndex the row index
	 * @param colName the column name
	 */
	public String getString(int rowIndex,String colName) throws WorkflowDBException,DBHelperException{
		return buffer.getString(rowIndex, colName);
	}

	/**
	 * to get a String value from the DBuffer by rowindex and column index
	 *
	 * @return String a string representation of value store in the DBuffer
	 * @param rowIndex the row index
	 * @param colIndex the column index
	 * @exception WorkflowDBException index error
	 */
	public String getString(int rowIndex,int colIndex) throws WorkflowDBException,DBHelperException	{
		return buffer.getString(rowIndex, colIndex);
	}

	/** 
	 * to get a array of String values from the DBuffer for the column
	 * indicated by the column index
	 *
	 * @return String[] a array of string 
	 * @param Index the column index
	 */
	public String[] getString(int index) throws WorkflowDBException,DBHelperException{
		return buffer.getString(index);
	}

	/** 
	 * to get a array of String values from the DBuffer for the column names
	 *
	 * @return String[] a array of string, null if DBuffer is empty. 
	 * 
	 */
	public String[] getColNames(){
		return buffer.getColNames();
	}


	/** 
	 * to get a integer value from the DBuffer by given the row index 
	 * and the column names
	 *
	 * @return int integer value 
	 * 
	 */
	public int getInt(int rowIndex, String colName) throws WorkflowDBException,DBHelperException{
		return buffer.getInt(rowIndex, colName);
	}

	  
	/** 
	 * to get a integer value from the DBuffer by given the row index 
	 * and the column index
	 *
	 * @return int integer value 
	 */
	public int getInt(int rowIndex, int colIndex) throws WorkflowDBException,DBHelperException{
		return buffer.getInt(rowIndex, colIndex);
	}

	/** 
	* to get a short value from the DBuffer by given the row index 
	* and the column names
	*
	* @return short a short integer value
	*/
	public short getShort(int rowIndex, String colName) throws WorkflowDBException,DBHelperException{
		return buffer.getShort(rowIndex, colName);
	}

  
	/** 
	* to get a short value from the DBuffer by given the row index 
	* and the column index
	*
	* @return short a short integer value
	*/
	public short getShort(int rowIndex, int colIndex) throws WorkflowDBException,DBHelperException{
		return buffer.getShort(rowIndex, colIndex);
	}


	/** to get a long value from the DBuffer by given the row index 
	* and the column names
	*
	* @return long a long integer value
	* 
	*/
	public long getLong(int rowIndex, String colName) throws WorkflowDBException,DBHelperException{
		return buffer.getLong(rowIndex, colName);
	}


	/**
	* to get a long value from the DBuffer by given the row index 
	* and the column index
	*
	* @return long a long integer value
	*/
	public long getLong(int rowIndex, int colIndex) throws WorkflowDBException,DBHelperException{
		return buffer.getLong(rowIndex, colIndex);
	}

	/**
	* to get a double value from the DBuffer by given the row index 
	* and the column names
	*
	* @return double a double value
	*/
	public double getDouble(int rowIndex, String colName) throws WorkflowDBException,DBHelperException{
		return buffer.getDouble(rowIndex, colName);
	}

	/** 
	* to get a double value from the DBuffer by given the row index 
	* and the column index
	*
	* @return double a double value
	*/
	public double getDouble(int rowIndex, int colIndex) throws WorkflowDBException,DBHelperException{
		return buffer.getDouble(rowIndex, colIndex);
	}

	/** to get a float value from the DBuffer by given the row index 
	* and the column names
	*
	* @return float a float value
	* 
	*/
	public float getFloat(int rowIndex, String colName) throws WorkflowDBException,DBHelperException{
		return buffer.getFloat(rowIndex, colName);
	}

	/** to get a double value from the DBuffer by given the row index 
	* and the column index
	*
	* @return double a double value
	* 
	*/
	public float getFloat(int rowIndex, int colIndex) throws WorkflowDBException,DBHelperException{
		return buffer.getFloat(rowIndex, colIndex);
	}

	/** to get a object from the DBuffer by given the row index 
	* and the column index
	*
	* @return Object an deserialized object or the object type defined java.lang.* 
	*/
	public Object getObject(int rowIndex, int colIndex) throws WorkflowDBException,DBHelperException{
	  return buffer.getObject(rowIndex, colIndex);
	}


	/** to get a object from the DBuffer by given the row index 
	* and the column names.
	*
	* @return Object a deserialized object
	*/
	public Object getObject(int rowIndex, String colName) throws WorkflowDBException,DBHelperException{
	  return buffer.getObject(rowIndex, colName);
	}


	/** to get a <code>java.sql.Date</code> value from the DBuffer by given the row index 
	* and the column names
	*
	* @return Date a java.sql.Date value
	* 
	*/
	public java.sql.Date getDate(int rowIndex, String colName) throws WorkflowDBException,DBHelperException{
	  return buffer.getDate(rowIndex, colName);
	}

	/** to get a <code>java.sql.Date</code> value from the 
	* DBuffer by given the row index 
	* and the column index
	*
	* @return Date a java.sql.Date value
	* 
	*/
	public java.sql.Date getDate(int rowIndex, int colIndex) throws WorkflowDBException,DBHelperException{
	  return buffer.getDate(rowIndex, colIndex);
	}

	/** to get a <code>java.sql.Date</code> value from the DBuffer by given the row index 
	* and the column names
	*
	* @return Date a java.sql.Date value
	* 
	*/
	public java.sql.Date getDateTime(int rowIndex, String colName) throws WorkflowDBException,DBHelperException{
	  return buffer.getDateTime(rowIndex, colName);
	}

	/** to get a <code>java.sql.Date</code> value from the 
	* DBuffer by given the row index 
	* and the column index
	*
	* @return Date a java.sql.Date value
	* 
	*/
	public java.sql.Date getDateTime(int rowIndex, int colIndex) throws WorkflowDBException{
	  return buffer.getDateTime(rowIndex, colIndex);
	}


	 /** to get the next value of a sequence
	*
	* @return long id
	* 
	*/
	public long getId(String sequenceName){
		Statement stmt      = null;
		ResultSet rs        = null;
		long id             = -1;
		try{
			stmt = conn.createStatement();
				//logger.debug("Getting the next sequence for " + sequenceName);
			String seqQuery = "select " + sequenceName + ".nextval from dual";
				//logger.debug("Executing the query:" + seqQuery);
			rs  = stmt.executeQuery(seqQuery);
			if(rs != null && rs.next()){
				id = rs.getLong(1);
			}
		}catch(Exception exception){
			id = -1;
			//logger.error("Error in the execution of getId(): ",exception);
		}finally{
			try{
				if(rs != null) rs.close();
			}catch(Exception e){}

			try{
				if(stmt != null) stmt.close();
			}catch(Exception e){}
		}
		return id;
	}


	/** 
   * finalize method used to close the connection
   * 
   */
  public void finalize() 
	  throws SQLException, WorkflowDBException
  {
		try{
			if(ps != null) {
				//logger.warn("prepared statement is not closed through the program. now closing..");
				ps.close();
			}
		}catch(Exception e){}

		try{
			if(conn != null){
				conn.close();
				conn=null;
				//logger.warn("connection is not closed through the program. now closing..");
			}
		}catch(Exception e){}
  }

   /** 
   * close method used to close the connection
   * 
   */
  public void close() 
	  throws SQLException, WorkflowDBException
  {
		try{
			if(ps != null) ps.close();
		}catch(Exception exception){
			logger.error("Exception in closing prepared statement ",exception);
		}

		try{
			if(conn != null) {
				conn.close();
				conn=null;
			}
		}catch(Exception exception){
			logger.error("Exception in closing connection ",exception);
		}
  }

 /** 
   * clearListData method used to clear the arrayList Listdata
   * 
   */
  public void clearListData() 
     
  {
		try{
			if(listData != null) listData.clear();
		}catch(Exception exception){
			logger.error("Exception in clearing listData",exception);
		}
	
  }

  /** 
   * setConnectionStatus method used to clear the arrayList Listdata
   * 
   */
  public void setConnectionStatus(boolean status) 
     
  {
		try{
			if(conn != null) conn.setAutoCommit(status);
		}catch(Exception exception){
			logger.error("Exception in setConnectionStatus",exception);
		}
	
  }

  /** 
   * doCommit method used set execute a commit
   * 
   */
  public void doCommit() 
     
  {
		try{
			if(conn != null) conn.commit();
		}catch(Exception exception){
			logger.error("Exception in doCommit",exception);
		}
	
  }

  /** 
   * doCommit method used set execute a rollback
   * 
   */
  public void doRollback() 
     
  {
		try{
			if(conn != null) conn.rollback();
		}catch(Exception exception){
			logger.error("Exception in doRollback",exception);
		}
	
  }

}