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

package oem.edge.ets.fe.aic.dyntab.vo;

import java.sql.Timestamp;

import oem.edge.ets.fe.aic.common.vo.ValueObject;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTableRowsDataVO extends ValueObject {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";		
	private String dataId = "";
	private String rowId = "";
	private String tableId = "";
	private String columnId = "";
	private String dataValue = "";
	private String active = "N";
	private String columnName = "";	
	private Timestamp rowUpdateDate = null;
	private int columnOrder = 0;
	private String columnType = "";
	private String required = "";
	public AICTableRowsDataVO()
	{
		rowUpdateDate = new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * @return
	 */
	public String getActive() {
		return active;
	}

	/**
	 * @return
	 */
	public String getColumnId() {
		return columnId;
	}

	/**
	 * @return
	 */
	public String getDataId() {
		return dataId;
	}

	/**
	 * @return
	 */
	public String getDataValue() {
		return dataValue;
	}

	/**
	 * @return
	 */
	public String getRowId() {
		return rowId;
	}

	/**
	 * @return
	 */
	public Timestamp getRowUpdateDate() {
		return rowUpdateDate;
	}

	/**
	 * @return
	 */
	public String getTableId() {
		return tableId;
	}

	/**
	 * @param c
	 */
	public void setActive(String c) {		
		active = c;
	}

	/**
	 * @param string
	 */
	public void setColumnId(String string) {
		
		columnId = string;
	}

	/**
	 * @param string
	 */
	public void setDataId(String string) {
		
		dataId = string;
	}

	/**
	 * @param string
	 */
	public void setDataValue(String string) {
		
		dataValue = string;
	}

	/**
	 * @param string
	 */
	public void setRowId(String string) {
		
		rowId = string;
	}

	/**
	 * @param timestamp
	 */
	public void setRowUpdateDate(Timestamp timestamp) {
		
		rowUpdateDate = timestamp;
	}

	/**
	 * @param string
	 */
	public void setTableId(String string) {
		
		tableId = string;
	}

	/**
	 * @return
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param string
	 */
	public void setColumnName(String string) {
		columnName = string;
	}

	/**
	 * @return
	 */
	public int getColumnOrder() {
		return columnOrder;
	}

	/**
	 * @param i
	 */
	public void setColumnOrder(int i) {
		columnOrder = i;
	}

	

	/**
	 * @return
	 */
	public String getColumnType() {
		return columnType;
	}

	/**
	 * @param string
	 */
	public void setColumnType(String string) {
		columnType = string;
	}

	/**
	 * @return
	 */
	public String getRequired() {
		return required;
	}

	/**
	 * @param string
	 */
	public void setRequired(String string) {
		required = string;
	}

}
