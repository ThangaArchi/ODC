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
package oem.edge.ets_pmo.domain;

import java.util.ArrayList;
import java.util.List;
/**
 * @author shingte
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CriObject extends PmoObject{
	private List cris;
	private List excepts;
	private String stage_id;
	private String priority;
	private String proposed_by;
	private String proposed_by_name;
	private String proposed_datetime;


	/**
	 * @return Returns the cris.
	 */
	public List getCriobjects() {
		return cris;
	}
	/**
	 * @param cris The cris to set.
	 */
	public void setCriobject(CriObject obj) {
		if (cris==null)
			cris=new ArrayList();
		cris.add(obj);
	}
	/**
	 * @return Returns the excepts.
	 */
	public List getExceptobjects() {

		return excepts;
	}
	public void setExceptobject(ExceptObject obj)
	{
		if (excepts==null)
			excepts=new ArrayList();
		excepts.add(obj);
	}
	/**
	 * @return Returns the priority.
	 */
	public String getPriority() {
		return priority;
	}
	/**
	 * @param priority The priority to set.
	 */
	public void setPriority (String str) {
		this.priority = str; //Integer.parseInt(str);
	}
	/**
	 * @return Returns the proposed_by.
	 */
	public String getProposed_by() {
		return proposed_by;
	}
	/**
	 * @param proposed_by The proposed_by to set.
	 */
	public void setProposed_by(String proposed_by) {
		this.proposed_by = proposed_by;
	}
	/**
	 * @return Returns the proposed_by.
	 */
	public String getProposed_by_name() {
		return proposed_by_name;
	}
	/**
	 * @param proposed_by The proposed_by to set.
	 */
	public void setProposed_by_name(String proposed_by) {
		this.proposed_by_name = proposed_by;
	}
	/**
	 * @return Returns the proposed_datetime.
	 */
	public String getProposed_datetime() {
		return proposed_datetime;
	}
	/**
	 * @param proposed_datetime The proposed_datetime to set.
	 */
	public void setProposed_datetime(String str) {
		this.proposed_datetime = str; //Helper.toTimestamp(str);
	}
	/**
	 * @return Returns the stage_id.
	 */
	public String getStage_id() {
		return stage_id;
	}
	/**
	 * @param stage_id The stage_id to set.
	 */
	public void setStage_id(String stage_id) {
		this.stage_id = stage_id;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new StringBuffer()
				.append("\n  id="+ this.getId())
				.append("\n  root_id=" + this.getRoot_id())
				.append("\n  parent_id="+this.getParent_id())
				.append("\n  element_name="+this.getElement_name())
				.append("\n  type="+this.getType())		
				.append("\n  reportable="+this.getReportable())
				.append("\n  proposed_by_name="+this.proposed_by_name)
				.append("\n  reference_number="+this.reference_number)
				.append("\n  priority="+this.priority)
				.append("\n  proposed_datetime="+this.proposed_datetime)
				.append("\n  stage_id="+this.stage_id)
				.append("\n  criobjects="+this.getCriobjects())
				.append("\n  exceptobjects="+this.getExceptobjects())
				.toString();
	}
}
