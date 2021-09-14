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
import java.util.ArrayList;
import java.util.Collection;

import oem.edge.ets.fe.aic.common.vo.ValueObject;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTableVO extends ValueObject {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private int docId = 0;
	private String tableId = "";
	private String tableName = "";
	private String active = "N";
	private String templateId = "";
	private Timestamp tableUpdateDate = null;
	private Collection aICTableRowsDataCollection = null;


	public AICTableVO()
	{
		tableUpdateDate = new Timestamp(System.currentTimeMillis());
		aICTableRowsDataCollection = new ArrayList();
	}
	/**
	 * @return
	 */
	public int getDocId() {
		return docId;
	}

	/**
	 * @return
	 */
	public String getTableId() {
		return tableId;
	}

	/**
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @return
	 */
	public Timestamp getTableUpdateDate() {
		return tableUpdateDate;
	}

	/**
	 * @return
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param i
	 */
	public void setDocId(int i) {
		
		docId = i;
	}

	/**
	 * @param string
	 */
	public void setTableId(String string) {
		
		tableId = string;
	}

	/**
	 * @param string
	 */
	public void setTableName(String string) {
		
		tableName = string;
	}

	/**
	 * @param timestamp
	 */
	public void setTableUpdateDate(Timestamp timestamp) {
		
		tableUpdateDate = timestamp;
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
	public String getActive() {
		return active;
	}

	/**
	 * @return
	 */
	public Collection getAICTableRowsDataCollection() {
		return aICTableRowsDataCollection;
	}

	/**
	 * @param c
	 */
	public void setActive(String c) {
		
		active = c;
	}

	/**
	 * @param collection
	 */
	public void setAICTableRowsDataCollection(Collection collection) {
		
		aICTableRowsDataCollection = collection;
	}

}
