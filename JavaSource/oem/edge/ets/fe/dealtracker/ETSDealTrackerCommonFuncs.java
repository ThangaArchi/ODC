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

/**
 * Author: Sandra Nava
 * Date: 1/24/2005
 */

package oem.edge.ets.fe.dealtracker;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.AMTException;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;

public class ETSDealTrackerCommonFuncs {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "4.4.1";
	
	static private final String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
	static private final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	static private final SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy  hh:mm a");





	static public Vector getIBMMembers(Vector membs, Connection conn){
		Vector new_members = new Vector();

		for (int i = 0; i<membs.size();i++){
			ETSUser mem = (ETSUser)membs.elementAt(i);
			try{
				String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn,mem.getUserId());
				String decaftype = AccessCntrlFuncs.decafType(edge_userid,conn);
				if (decaftype.equals("I")){
					new_members.addElement(mem);
				}
			}
			catch(AMTException a){
				System.out.println("amt exception in getibmmembers err= "+a);
			}
			catch(SQLException s){
				System.out.println("sql exception in getibmmembers err= "+s);
			}
		}

		return new_members;
	}

	static public String getHashStrValue(Hashtable h, String key)
		{
		String value = (String)h.get(key);

		if (value == null)
			{
			return "";
			}
		else
			{
			return value;
			}
		}
		

	static public String getParameter(HttpServletRequest req, String key){
		String value = req.getParameter(key);
	
		if (value == null){
			return "";
		}
		else{
			return value;
		}
	}
	
	static public Vector getOwnMembers(ETSProj Project, Vector membs, Connection conn,boolean ibmonly,Vector notEditors){
		Vector new_members = new Vector();
	
		for (int i = 0; i<membs.size();i++){
			ETSUser mem = (ETSUser)membs.elementAt(i);
			try{
				String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn,mem.getUserId());
				String decaftype = AccessCntrlFuncs.decafType(edge_userid,conn);
				String sRole = getUserRole(mem.getUserId(),Project.getProjectId(),conn);
				if(ibmonly){
					if (decaftype.equals("I") && !notEditors.contains(sRole)){
						//&& !sRole.equals(Defines.WORKSPACE_CLIENT) 
						//&& !sRole.equals(Defines.ETS_EXECUTIVE) 
						//&& !sRole.equals(Defines.WORKSPACE_VISITOR)) {
						new_members.addElement(mem);
					}
				}
				else{
					System.out.println("sRole="+sRole);
					if (!notEditors.contains(sRole)){
						//!sRole.equals(Defines.WORKSPACE_CLIENT) 
						//&& !sRole.equals(Defines.ETS_EXECUTIVE) 
						//&& !sRole.equals(Defines.WORKSPACE_VISITOR)) {
						new_members.addElement(mem);
					}
				}
			}
			catch(AMTException a){
				System.out.println("amt exception in getownmembers err= "+a);
			}
			catch(SQLException s){
				System.out.println("sql exception in getownmembers err= "+s);
			}
			catch(Exception e){
				System.out.println("exception in getownmembers err= "+e);
			}
		}
	
		return new_members;
	}
	
	
	public static String getUserRole(String ir_userid,String sProjectId,Connection conn) throws Exception {
		Vector userents = new Vector();
		String sRole = Defines.INVALID_USER;

		try {
			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(conn,ir_userid);
			userents = AccessCntrlFuncs.getUserEntitlements(conn,edgeuserid,true, true);

			if (userents.contains(Defines.ETS_ADMIN_ENTITLEMENT)) {
				sRole = Defines.ETS_ADMIN;
			} else if (userents.contains(Defines.ETS_ENTITLEMENT)) {
				if (ETSDatabaseManager.hasProjectPriv(ir_userid,sProjectId,Defines.OWNER)) {
					sRole = Defines.WORKSPACE_OWNER;
				} else if (ETSDatabaseManager.hasProjectPriv(ir_userid,sProjectId,Defines.ADMIN)) {
					sRole = Defines.WORKSPACE_MANAGER;
				} else if (ETSDatabaseManager.hasProjectPriv(ir_userid,sProjectId,Defines.CLIENT)) {
					sRole = Defines.WORKSPACE_CLIENT;
				} else if (ETSDatabaseManager.hasProjectPriv(ir_userid,sProjectId,Defines.USER)) {
					sRole = Defines.WORKSPACE_MEMBER;
				} else if (ETSDatabaseManager.hasProjectPriv(ir_userid,sProjectId,Defines.VISITOR)) {
					sRole = Defines.WORKSPACE_VISITOR;
				} else {
					if (userents.contains(Defines.ETS_EXECUTIVE_ENTITLEMENT)) {
						sRole = Defines.ETS_EXECUTIVE;
					}
				}
			} else if (userents.contains(Defines.ETS_EXECUTIVE_ENTITLEMENT)) {
				sRole = Defines.ETS_EXECUTIVE;
			}

		} catch (Exception e) {
		   throw e;
		}

		return sRole;
	 }

	static public boolean canEdit(EdgeAccessCntrl es,boolean isSuperAdmin,boolean ibmonly,String userRole,Vector notEditors,String creatorid,String ownerid,boolean user_external){
			if (isSuperAdmin)
				return true;
				
			if (ibmonly && (user_external || userRole.equals(Defines.WORKSPACE_CLIENT))){
				return false;
			}
			if (notEditors.contains(userRole)){
				return false;
			}
			if (!(ownerid.equals(es.gIR_USERN) || creatorid.equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER) || userRole.equals(Defines.WORKSPACE_MANAGER)))
				return false;
		
			return true;
		
		}



} // end of class







