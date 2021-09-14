/*
 * Created on May 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.ismgt.middleware;

import java.util.Vector;

import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;


/**
 * @author jetendra
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */


public class ETSMWIssue extends ETSIssue {

	public static final String VERSION = "1.0";
	public String nextState;
	public String comm_log="";
	public Vector ownerRecords=null;
	public EtsIssOwnerInfo ownerInfo;

}
