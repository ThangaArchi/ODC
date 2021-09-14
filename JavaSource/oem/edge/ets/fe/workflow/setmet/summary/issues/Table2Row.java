/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.setmet.summary.issues;

import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC
//private static Log logger = WorkflowLogger.getLogger(Table2Row.class);
/**
 * Class       : Table2Row
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.issues
 * Description : 
 * Date		   : Dec 7, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class Table2Row {
	private String type  =null;
	private String focalPt= null;
	private String owners = null;
	private String targetDate = null;
	private String actualDate = null;
	private String status = null;
	
	/**
	 * @return Returns the actualDate.
	 */
	public String getActualDate() {
		return actualDate;
	}
	/**
	 * @param actualDate The actualDate to set.
	 */
	public void setActualDate(String actualDate) {
		this.actualDate = actualDate;
	}
	/**
	 * @return Returns the focalPt.
	 */
	public String getFocalPt() {
		return focalPt;
	}
	/**
	 * @param focalPt The focalPt to set.
	 */
	public void setFocalPt(String focalPt) {
		this.focalPt = focalPt;
	}
	/**
	 * @return Returns the owners.
	 */
	public String getOwners() {
		return owners;
	}
	/**
	 * @param owners The owners to set.
	 */
	public void setOwners(String owners) {
		this.owners = owners;
	}
	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return Returns the targetDate.
	 */
	public String getTargetDate() {
		return targetDate;
	}
	/**
	 * @param targetDate The targetDate to set.
	 */
	public void setTargetDate(String targetDate) {
		this.targetDate = targetDate;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
}
