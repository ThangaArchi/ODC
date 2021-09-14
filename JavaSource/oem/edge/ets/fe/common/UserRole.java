/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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
 * Created on Feb 21, 2006
 * @author v2sathis@us.ibm.com
 */

package oem.edge.ets.fe.common;

import java.sql.Connection;
import java.util.Vector;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.AICUserDecafRole;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author v2sathis
 *
 */
public class UserRole {

	public static final String VERSION = "1.1";
	private static Log logger = LogFactory.getLog(UserRole.class);

	public String getUserRole(Connection con, String irUserId, String projectId, String projectType, boolean isITAR, String projectPrivate) throws Exception {

		String role = Defines.INVALID_USER;

		Vector userents = new Vector();
		UserRoleDAO roleDAO = new UserRoleDAO();

		try {

			if (projectType.equalsIgnoreCase(Defines.ETS_WORKSPACE_TYPE)) {

				// ets project type

				if (isITAR) {

					// itar project.. check for additional entitlement.
					// added by sathish for 5.4.1

					String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,irUserId);
					userents = AccessCntrlFuncs.getUserEntitlements(con,edgeuserid,true, true);

					boolean bITAR = false;

					if (userents.contains(Defines.ITAR_ENTITLEMENT)) {
						bITAR = true;
					}

					if (userents.contains(Defines.ETS_ADMIN_ENTITLEMENT) && bITAR) {
						role = Defines.ETS_ADMIN;
					} else if (userents.contains(Defines.ITAR_ENTITLEMENT)) {
						if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.OWNER,con)) {
							role = Defines.WORKSPACE_OWNER;
						} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.ADMIN,con)) {
							role = Defines.WORKSPACE_MANAGER;
						} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.CLIENT,con)) {
							role = Defines.WORKSPACE_CLIENT;
						} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.USER,con)) {
							role = Defines.WORKSPACE_MEMBER;
						} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.VISITOR,con)) {
							role = Defines.WORKSPACE_VISITOR;
						} else {
							if (userents.contains(Defines.ETS_EXECUTIVE_ENTITLEMENT) && bITAR) {
								role = Defines.ETS_EXECUTIVE;
							}
						}
					} else if (userents.contains(Defines.ETS_EXECUTIVE_ENTITLEMENT) && bITAR) {
						role = Defines.ETS_EXECUTIVE;
					}

				} else {

					String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,irUserId);
					userents = AccessCntrlFuncs.getUserEntitlements(con,edgeuserid,true, true);

					if (userents.contains(Defines.ETS_ADMIN_ENTITLEMENT)) {
						role = Defines.ETS_ADMIN;
					} else if (userents.contains(Defines.ETS_ENTITLEMENT)) {
						if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.OWNER,con)) {
							role = Defines.WORKSPACE_OWNER;
						} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.ADMIN,con)) {
							role = Defines.WORKSPACE_MANAGER;
						} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.CLIENT,con)) {
							role = Defines.WORKSPACE_CLIENT;
						} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.USER,con)) {
							role = Defines.WORKSPACE_MEMBER;
						} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.VISITOR,con)) {
							role = Defines.WORKSPACE_VISITOR;
						} else {
							if (userents.contains(Defines.ETS_EXECUTIVE_ENTITLEMENT)) {
								role = Defines.ETS_EXECUTIVE;
							}
						}
					} else if (userents.contains(Defines.ETS_EXECUTIVE_ENTITLEMENT)) {
						role = Defines.ETS_EXECUTIVE;
					}

				}
			} else {
				// non ets workspace.. AIC
				String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,irUserId);
				userents = AccessCntrlFuncs.getUserEntitlements(con,edgeuserid,true, true);
				String decaftype = AccessCntrlFuncs.decafType(edgeuserid, con);
				boolean isibmer = true;
				if(!decaftype.equalsIgnoreCase("I")){
					 isibmer = false;
				}

				if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
					role = Defines.ETS_ADMIN;
				} else if(userents.contains(Defines.WORKFLOW_ADMIN)){
					role = Defines.WORKFLOW_ADMIN;
			    }else if (projectPrivate.equalsIgnoreCase(Defines.AIC_IS_PRIVATE_PUBLIC) ||
						projectPrivate.equalsIgnoreCase(Defines.AIC_IS_PRIVATE_RESTRICTED) ||
						projectPrivate.equalsIgnoreCase(Defines.AIC_IS_PRIVATE_PRIVATE)) 
				{   
					//	check for external user - External User must have SALES_COLLAB entitlement
					if (( isibmer == false ) && (!userents.contains(Defines.COLLAB_CENTER_ENTITLEMENT))){
						role = Defines.INVALID_USER;
					} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.OWNER,con)) {
						role = Defines.WORKSPACE_OWNER;
					} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.ADMIN,con)) {
						role = Defines.WORKSPACE_MANAGER;
					} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.CLIENT,con)) {
						role = Defines.WORKSPACE_CLIENT;
					} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.USER,con)) {
						role = Defines.WORKSPACE_MEMBER;
					} else if (roleDAO.hasProjectPriv(irUserId,projectId,Defines.VISITOR,con)) {
						role = Defines.WORKSPACE_VISITOR;
					} else {
						if (projectPrivate.equalsIgnoreCase(Defines.AIC_IS_PRIVATE_PUBLIC)) {
							if (userents.contains(Defines.COLLAB_CENTER_EXECUTIVE_ENTITLEMENT) || userents.contains(Defines.COLLAB_CENTER_NON_SALES_ENTITLEMENT) || userents.contains(Defines.COLLAB_CENTER_SALES_ENTITLEMENT)) {
								role = Defines.WORKSPACE_MEMBER;								
							} else {
								//workflow admin entitlementent check 
								if(userents.contains(Defines.WORKFLOW_ADMIN))
									role = Defines.WORKFLOW_ADMIN;
							}
						} else {
							role = Defines.INVALID_USER;
						}
					} // end of AIC roles for Restricted/public/private workspace
				} else if (projectPrivate.equalsIgnoreCase(Defines.AIC_IS_PRIVATE_TEAMROOM) || projectPrivate.equalsIgnoreCase(Defines.AIC_IS_RESTRICTED_TEAMROOM)) {
					//Check if user have BPS_OWNER/BPS_READER entitlements
					if (userents.contains(Defines.BPSAUTHOR_ENT) || userents.contains(Defines.BPSOWNER_ENT) || userents.contains(Defines.BPSREADER_ENT)){
						role = checkProfileForUserRole(con,projectId,irUserId,edgeuserid);
					} else {
						role = Defines.INVALID_USER;
					}
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
		   	throw e;
		} finally {
			roleDAO = null;
			userents = null;
		}

		return role;

	}

	public String checkProfileForUserRole(Connection conn, String strProjId, String iruserid, String edgeUserId)
	throws Exception{
		String userRole = Defines.INVALID_USER;
		
		UserRoleDAO userRoleDao = new UserRoleDAO();
		//get User entitlement & profile from projectId
		AICUserDecafRole userDecafRole = userRoleDao.extractUserDetails(conn,strProjId,iruserid);
		
		//check if the user is having the entitlement & project scoped
		EdgeAccessCntrl  es = new EdgeAccessCntrl();
		Vector userEnts = es.getEntitlementsForUser(conn,edgeUserId, userDecafRole.getEntitlementName(),"BPSTeams",userDecafRole.getDatatypeName());
		
		if (userEnts.size() > 0) {
			Vector priv = userRoleDao.getUserPrivs(userDecafRole.getRoleId(),conn);
			if (priv.contains(new Integer(Defines.OWNER))) {
				userRole = Defines.WORKSPACE_OWNER;
			} else if (priv.contains(new Integer(Defines.ADMIN))) {
				userRole = Defines.WORKSPACE_MANAGER;
			} else if (priv.contains(new Integer(Defines.USER))) {
				userRole = Defines.WORKSPACE_MEMBER;
			} else if (priv.contains(new Integer(Defines.VISITOR))) {
				userRole = Defines.WORKSPACE_VISITOR;
			} else {
				userRole = Defines.INVALID_USER;
			}
		}
		System.out.println("User Role userid::" + iruserid + "::role::" + userRole);
		return userRole;
	}


}
