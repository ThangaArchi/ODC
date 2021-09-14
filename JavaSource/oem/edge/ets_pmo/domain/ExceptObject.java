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
public class ExceptObject extends CriObject {
	private List docs=null;
	private List rtfs=null;
	private List resources=null;
	private String sourceid; // ACK attribute

	/**
	 * @return Returns the sourceid.
	 */
	public String getSourceid() {
		return sourceid;
	}
	/**
	 * @param sourceid The sourceid to set.
	 */
	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}
	/**
	 * @return Returns the docs.
	 */
	public List getDocobjects() {

		return docs;
	}
	public void setDocobject(DocObject obj)
	{
		if (docs==null)
			docs=new ArrayList();
		docs.add(obj);
	}
	/**
	 * @return Returns the rtfs.
	 */
	public List getRtfobjects() {
		if (rtfs==null)
			rtfs=new ArrayList();
		return rtfs;
	}
	public void setRtfobject(RtfObject obj)
	{
		if (rtfs==null)
			rtfs=new ArrayList();
		rtfs.add(obj);
	}
	/**
	 * @return Returns the resources.
	 */
	public List getResobjects() {
		
		return resources;
	}
	public void setResobject(ResObject obj)
	{
		if (resources==null)
			resources=new ArrayList();
		resources.add(obj);
	}

	


	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new StringBuffer()
				.append("\n  id="+this.getId())
				.append("\n  parent_id="+this.getParent_id())
				.append("\n  root_id="+this.getRoot_id())
				.append("\n  element_name="+this.getElement_name())		
				.append("\n  type="+this.getType())
				.append("\n  reportable="+this.getReportable())
				.append("\n  proposed_by="+this.getProposed_by_name())
				.append("\n  reference_number="+this.getReference_number())
				.append("\n  priority="+this.getPriority())
				.append("\n  proposed_datetime="+this.getProposed_datetime())
				.append("\n  stage_id="+this.getStage_id())
				.append("\n  sourceid="+this.getSourceid())
				.append("\n  criobjects="+this.getCriobjects())
				.append("\n  exceptobjects="+this.getExceptobjects())
				.append("\n  resobjects="+this.getResobjects())
				.append("\n  docobjects="+this.getDocobjects())
				.append("\n  rtfObjects="+this.getRtfobjects()).toString();
	}
}
