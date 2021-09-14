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

import java.sql.Date;
import java.sql.Timestamp;

import oem.edge.ets.fe.aic.common.vo.ValueObject;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTemplateColumnVO extends ValueObject {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private String columnId = "";
	private String columnName = "";
	private String required = "N";
	private String active = "N";
	private int columnOrder = 0;
	private String columnType = "";
	private Timestamp columnUpdateDate = null;
	private String templateId = "";
	private String dataValue = "";
	private String rowId = "";
	private String tableId = "";

	public AICTemplateColumnVO() {
		columnUpdateDate = new Timestamp(System.currentTimeMillis());		
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
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @return
	 */
	public int getColumnOrder() {
		return columnOrder;
	}

	/**
	 * @return
	 */
	public String getColumnType() {
		return columnType;
	}

	/**
	 * @return
	 */
	public Timestamp getColumnUpdateDate() {
		return columnUpdateDate;
	}

	/**
	 * @param i
	 */
	public void setColumnId(String s) {
		
		columnId = s;
	}

	/**
	 * @param string
	 */
	public void setColumnName(String string) {
		
		columnName = string;
	}

	/**
	 * @param i
	 */
	public void setColumnOrder(int i) {
		
		columnOrder = i;
	}

	/**
	 * @param string
	 */
	public void setColumnType(String string) {
		
		columnType = string;
	}

	/**
	 * @param timestamp
	 */
	public void setColumnUpdateDate(Timestamp timestamp) {
		
		columnUpdateDate = timestamp;
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
	public String getRequired() {
		return required;
	}

	/**
	 * @param c
	 */
	public void setActive(String c) {
		
		active = c;
	}

	/**
	 * @param c
	 */
	public void setRequired(String c) {
		
		required = c;
	}

	/**
	 * @return
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param string
	 */
	public void setTemplateId(String string) {
		templateId = string;
	}

	/**
	 * @return
	 */
	public String getDataValue() {
		return dataValue;
	}

	/**
	 * @param string
	 */
	public void setDataValue(String string) {
		dataValue = string;
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
	public String getTableId() {
		return tableId;
	}

	/**
	 * @param string
	 */
	public void setRowId(String string) {
		rowId = string;
	}

	/**
	 * @param string
	 */
	public void setTableId(String string) {
		tableId = string;
	}

}
