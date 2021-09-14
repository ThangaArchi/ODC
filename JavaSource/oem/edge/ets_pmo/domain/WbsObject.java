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
public class WbsObject extends PmoObject {
	private List wbs=null;
	private List cris=null;
	private List excepts=null;
	private List docs=null;
	private List rtfs=null;
	private List resources=null;
	private String stage_id;
	
	private String currency_id;
	private String calendar_id;
	private String published;
	private String revision_history;
	private String start_finish_date;
	private String finish_start_date;
	private String start_dt;
	private String finish_dt;
	private String duration;
	private String work_percent;
	private String etc;
	private String percent_complete;
	private String sd;
	private String fd;
	
	private String invalid_entry; // NACK attribute
	
	private String planned_start;
	private String planned_finish;
	private String planned_duration;
	private String planned_effort;
	private String proposed_start;
	private String proposed_finish;
	private String proposed_duration;
	private String proposed_effort;
	private String sched_start;
	private String sched_finish;
	private String sched_duration;
	private String sched_effort;
	private String actual_start;
	private String actual_finish;
	private String  actual_duration;
	private String actual_effort;
	private String forecast_start;
	private String forecast_finish;
	private String  forecast_duration;
	private String baseline1_start;
	private String baseline1_finish;
	private String  baseline1_duration;
	private String baseline2_start;
	private String baseline2_finish;
	private String  baseline2_duration;
	private String baseline3_start;
	private String baseline3_finish;
	private String  baseline3_duration;
	private String nack;
	private String ref_code; // added 11-15-2005
	
	
	public String getRef_code() {
		return ref_code;
	}
	public void setRef_code(String ref_code) {
		this.ref_code = ref_code;
	}
	public String getNack() {
		return nack;
	}
	public void setNack(String nack) {
		this.nack = nack;
	}
	/**
	 * @return Returns the cris.
	 */
	public List getWbsobjects() {
		return wbs;
	}
	/**
	 * @param cris The cris to set.
	 */
	public void setWbsobject(WbsObject obj) {
		if (wbs==null)
			wbs=new ArrayList();
		wbs.add(obj);
	}
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
	public List getExceptobjects() {
		return excepts;
	}
	/**
	 * @param cris The cris to set.
	 */
	public void setExceptobject(ExceptObject obj) {
		if (excepts==null)
			excepts=new ArrayList();
		excepts.add(obj);
	}
	/**
	 * @return Returns the docs.
	 */
	public List getDocobjects() {
		return docs;
	}
	public void setDocobject(DocObject obj) {
		if (docs==null)
			docs=new ArrayList();
		docs.add(obj);
	}
	/**
	 * @return Returns the rtfs.
	 */
	public List getRtfobjects() {
		
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
	public void setResobject(ResObject obj){
		if (resources==null)
			resources=new ArrayList();
		resources.add(obj);
	}
	
	public String getActual_duration() {
		return actual_duration;
	}
	public void setActual_duration(String actual_duration) {
		this.actual_duration = actual_duration;
	}
	public String getActual_effort() {
		return actual_effort;
	}
	public void setActual_effort(String actual_effort) {
		this.actual_effort = actual_effort;
	}
	public String getActual_finish() {
		return actual_finish;
	}
	public void setActual_finish(String actual_finish) {
		this.actual_finish = actual_finish;
	}
	public String getActual_start() {
		return actual_start;
	}
	public void setActual_start(String actual_start) {
		this.actual_start = actual_start;
	}
	public String getBaseline1_duration() {
		return baseline1_duration;
	}
	public void setBaseline1_duration(String baseline1_duration) {
		this.baseline1_duration = baseline1_duration;
	}
	public String getBaseline1_finish() {
		return baseline1_finish;
	}
	public void setBaseline1_finish(String baseline1_finish) {
		this.baseline1_finish = baseline1_finish;
	}
	public String getBaseline1_start() {
		return baseline1_start;
	}
	public void setBaseline1_start(String baseline1_start) {
		this.baseline1_start = baseline1_start;
	}
	public String getBaseline2_duration() {
		return baseline2_duration;
	}
	public void setBaseline2_duration(String baseline2_duration) {
		this.baseline2_duration = baseline2_duration;
	}
	public String getBaseline2_finish() {
		return baseline2_finish;
	}
	public void setBaseline2_finish(String baseline2_finish) {
		this.baseline2_finish = baseline2_finish;
	}
	public String getBaseline2_start() {
		return baseline2_start;
	}
	public void setBaseline2_start(String baseline2_start) {
		this.baseline2_start = baseline2_start;
	}
	public String getBaseline3_duration() {
		return baseline3_duration;
	}
	public void setBaseline3_duration(String baseline3_duration) {
		this.baseline3_duration = baseline3_duration;
	}
	public String getBaseline3_finish() {
		return baseline3_finish;
	}
	public void setBaseline3_finish(String baseline3_finish) {
		this.baseline3_finish = baseline3_finish;
	}
	public String getBaseline3_start() {
		return baseline3_start;
	}
	public void setBaseline3_start(String baseline3_start) {
		this.baseline3_start = baseline3_start;
	}
	public String getCalendar_id() {
		return calendar_id;
	}
	public void setCalendar_id(String calendar_id) {
		this.calendar_id = calendar_id;
	}
	public String getCurrency_id() {
		return currency_id;
	}
	public void setCurrency_id(String currency_id) {
		this.currency_id = currency_id;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getEtc() {
		return etc;
	}
	public void setEtc(String etc) {
		this.etc = etc;
	}
	public String getFd() {
		return fd;
	}
	public void setFd(String fd) {
		this.fd = fd;
	}
	public String getFinish_dt() {
		return finish_dt;
	}
	public void setFinish_dt(String finish_dt) {
		this.finish_dt = finish_dt;
	}
	public String getFinish_start_date() {
		return finish_start_date;
	}
	public void setFinish_start_date(String finish_start_date) {
		this.finish_start_date = finish_start_date;
	}
	public String getForecast_duration() {
		return forecast_duration;
	}
	public void setForecast_duration(String forecast_duration) {
		this.forecast_duration = forecast_duration;
	}
	public String getForecast_finish() {
		return forecast_finish;
	}
	public void setForecast_finish(String forecast_finish) {
		this.forecast_finish = forecast_finish;
	}
	public String getForecast_start() {
		return forecast_start;
	}
	public void setForecast_start(String forecast_start) {
		this.forecast_start = forecast_start;
	}
	public String getInvalid_entry() {
		return invalid_entry;
	}
	public void setInvalid_entry(String invalid_entry) {
		this.invalid_entry = invalid_entry;
	}
	public String getPercent_complete() {
		return percent_complete;
	}
	public void setPercent_complete(String percent_complete) {
		this.percent_complete = percent_complete;
	}
	public String getPlanned_duration() {
		return planned_duration;
	}
	public void setPlanned_duration(String planned_duration) {
		this.planned_duration = planned_duration;
	}
	public String getPlanned_effort() {
		return planned_effort;
	}
	public void setPlanned_effort(String planned_effort) {
		this.planned_effort = planned_effort;
	}
	public String getPlanned_finish() {
		return planned_finish;
	}
	public void setPlanned_finish(String planned_finish) {
		this.planned_finish = planned_finish;
	}
	public String getPlanned_start() {
		return planned_start;
	}
	public void setPlanned_start(String planned_start) {
		this.planned_start = planned_start;
	}
	public String getProposed_duration() {
		return proposed_duration;
	}
	public void setProposed_duration(String proposed_duration) {
		this.proposed_duration = proposed_duration;
	}
	public String getProposed_effort() {
		return proposed_effort;
	}
	public void setProposed_effort(String proposed_effort) {
		this.proposed_effort = proposed_effort;
	}
	public String getProposed_finish() {
		return proposed_finish;
	}
	public void setProposed_finish(String proposed_finish) {
		this.proposed_finish = proposed_finish;
	}
	public String getProposed_start() {
		return proposed_start;
	}
	public void setProposed_start(String proposed_start) {
		this.proposed_start = proposed_start;
	}
	public String getPublished() {
		return published;
	}
	public void setPublished(String published) {
		this.published = published;
	}
	public String getRevision_history() {
		return revision_history;
	}
	public void setRevision_history(String revision_history) {
		this.revision_history = revision_history;
	}
	public String getSched_duration() {
		return sched_duration;
	}
	public void setSched_duration(String sched_duration) {
		this.sched_duration = sched_duration;
	}
	public String getSched_effort() {
		return sched_effort;
	}
	public void setSched_effort(String sched_effort) {
		this.sched_effort = sched_effort;
	}
	public String getSched_finish() {
		return sched_finish;
	}
	public void setSched_finish(String sched_finish) {
		this.sched_finish = sched_finish;
	}
	public String getSched_start() {
		return sched_start;
	}
	public void setSched_start(String sched_start) {
		this.sched_start = sched_start;
	}
	public String getSd() {
		return sd;
	}
	public void setSd(String sd) {
		this.sd = sd;
	}
	public String getStage_id() {
		return stage_id;
	}
	public void setStage_id(String stage_id) {
		this.stage_id = stage_id;
	}
	public String getStart_dt() {
		return start_dt;
	}
	public void setStart_dt(String start_dt) {
		this.start_dt = start_dt;
	}
	public String getStart_finish_date() {
		return start_finish_date;
	}
	public void setStart_finish_date(String start_finish_date) {
		this.start_finish_date = start_finish_date;
	}
	public String getWork_percent() {
		return work_percent;
	}
	public void setWork_percent(String work_percent) {
		this.work_percent = work_percent;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new StringBuffer().append("\n  id="+this.getId())
										.append("\n  root_id="+this.getRoot_id())
										.append("\n  parent_id="+this.getParent_id())
										.append("\n  type="+this.getType())
										.append("\n  reportable="+this.getReportable())
										.append("\n  element_name="+this.getElement_name())
										.append("\n  fd="+this.fd)
										.append("\n  sd="+this.sd)
										//.append("\n  finish_type="+this.getFinish_type())
										.append("\n  reference_number="+this.getReference_number())
										.append("\n  stage_id="+this.getStage_id())
										.append("\n  invalid_entry="+this.getInvalid_entry())
										.append("\n  nack="+this.getNack())
										.append("\n  criobjects="+this.getCriobjects())
										.append("\n  exceptobjects="+this.getExceptobjects())
										.append("\n  rtfobjects="+this.getRtfobjects())
										.append("\n  resobjects="+this.getResobjects())
										.append("\n  docobjects="+this.getDocobjects())
										.toString();
	}
}
