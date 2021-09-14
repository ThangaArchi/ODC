/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
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


package oem.edge.ets.fe;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.common.Global;



public class ETSDocCommon {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.1";


    static {
	if ( ! Global.loaded )
	    Global.Init();
    }
    

	static public Vector getValidCatTree(ETSCat cat,String userid,String projid,String userRole,int priv,boolean traverse) {
		Vector v = new Vector();
		try {
			Vector subcats = ETSDatabaseManager.getSubCats(cat.getId());

			if (subcats.size() > 0) {
				for (int i = 0; i < subcats.size(); i++) {
					System.out.println("i=" + i);
					ETSCat c = (ETSCat) subcats.elementAt(i);
					System.out.println("catname=" + c.getName());
					boolean res = getValidCatSubTree(c.getId(),projid,userid,userRole,priv,traverse);
					if (res) {
						v.addElement(c);
					}
				}
			}
		}
		catch (SQLException se) {
			System.out.println("sql error occurred in getCatTree= " + se);
		}
		catch (Exception e) {
			System.out.println("error occurred in getCatTree= " + e);
		}

		return v;
	}
	
	static public Vector getValidCatTreeIds(ETSCat cat,String userid,String projid,String userRole,int priv,boolean traverse) {
		Vector v = new Vector();
		try {
			Vector subcats = ETSDatabaseManager.getSubCats(cat.getId());

			if (subcats.size() > 0) {
				for (int i = 0; i < subcats.size(); i++) {
					ETSCat c = (ETSCat) subcats.elementAt(i);
					boolean res = getValidCatSubTree(c.getId(),projid,userid,userRole,priv,traverse);
					if (res) {
						v.addElement(new Integer(c.getId()));
					}
				}
			}
		}
		catch (SQLException se) {
			System.out.println("sql error occurred in getCatTree= " + se);
		}
		catch (Exception e) {
			System.out.println("error occurred in getCatTree= " + e);
		}

		return v;
	}

	static public boolean getValidCatSubTree(int catid,String projid,String userid,String userRole,int priv,boolean traverse) {
		boolean editable = true;

		try {
			ETSCat cat = ETSDatabaseManager.getCat(catid);
			if (cat.getCatType() == 0 || ((!cat.getUserId().equals(userid))&& (!ETSDatabaseManager.hasProjectPriv(userid, projid, priv)) && (!userRole.equals(Defines.ETS_ADMIN)))) {
				editable = false;
				return editable;
			}
			/*
			if (cat.IsCPrivate()){
				if(!isAuthorized(cat.getUserId(),cat.getId(),cat.getProjectId(),false,true)){
					editable = false;
					return editable;
				}
			}*/

			if (traverse) {
				Vector subcats = ETSDatabaseManager.getSubCats(catid);
				for (int i = 0; i < subcats.size(); i++) {
					System.out.println("i=" + i);
					ETSCat c = (ETSCat) subcats.elementAt(i);
					System.out.println("catname=" + c.getName());
					editable = getValidCatSubTree(c.getId(),projid,userid,userRole,priv,traverse);
				}

				Vector docs = ETSDatabaseManager.getDocs(catid);
				boolean hasPriv = ETSDatabaseManager.hasProjectPriv(userid, projid, priv);
				
				for (int j = 0; j < docs.size(); j++) {
					ETSDoc d = (ETSDoc) docs.elementAt(j);
					if (((!d.getUserId().equals(userid)) && !hasPriv && !userRole.equals(Defines.ETS_ADMIN)) 
						|| (d.hasExpired() && userRole==Defines.WORKSPACE_MANAGER)) {
						editable = false;
						return editable;
					}
					if (d.IsDPrivate()){
						if(!isAuthorized(d.getUserId(),d.getId(),d.getProjectId(),userRole,userRole.equals(Defines.ETS_ADMIN),userRole.equals(Defines.ETS_EXECUTIVE),false,false,userid)){
							editable = false;
							return editable;
						}
					}
				}
			}
		}
		catch (SQLException se) {
			return false;
		}
		catch (Exception e) {
			System.out.println("error occurred in getCatSubTree= " + e);
			return false;
		}

		System.out.println("allowed=" + editable);
		return editable;
	}

	static public Vector getCatSubTreeOwners(Vector v,int catid,String projid,String userid,String userRole,int priv,boolean traverse) {
		try {
			ETSCat cat = ETSDatabaseManager.getCat(catid);
			if (cat.getCatType() == 0 || ((!cat.getUserId().equals(userid))&& (!ETSDatabaseManager.hasProjectPriv(userid, projid, priv)) && (!userRole.equals(Defines.ETS_ADMIN)))) {
				if (!v.contains(cat.getUserId()))
					v.addElement(cat.getUserId());
			}
			if (traverse) {
				Vector subcats = ETSDatabaseManager.getSubCats(catid);
				for (int i = 0; i < subcats.size(); i++) {
					System.out.println("i=" + i);
					ETSCat c = (ETSCat) subcats.elementAt(i);
					System.out.println("catname=" + c.getName());
					v = getCatSubTreeOwners(v,c.getId(),projid,userid,userRole,priv,traverse);
				}

				Vector docs = ETSDatabaseManager.getDocs(catid);
				boolean hasPriv = ETSDatabaseManager.hasProjectPriv(userid, projid, priv);
				
				for (int j = 0; j < docs.size(); j++) {
					ETSDoc d = (ETSDoc) docs.elementAt(j);
					if (((!d.getUserId().equals(userid)) && !hasPriv && !userRole.equals(Defines.ETS_ADMIN)) 
						|| (d.hasExpired() && userRole==Defines.WORKSPACE_MANAGER)) {
							if (!v.contains(d.getUserId()))
								v.addElement(d.getUserId());
					}
				}
			}
		}
		catch (SQLException se) {
			return v;
		}
		catch (Exception e) {
			System.out.println("error occurred in getCatSubTreeOwners= " + e);
			return v;
		}

		return v;
	}

	
	static public boolean isAuthorized(String ownerid,String userRole,boolean isSuperAdmin, boolean isExecutive,Vector users,boolean isViewOnly,String userid){
		
		
		if (isViewOnly && (userRole.equals(Defines.WORKSPACE_OWNER) || isSuperAdmin || isExecutive || ownerid.equals(userid)))
			return true;
		else if ((!isViewOnly) && (userRole.equals(Defines.WORKSPACE_OWNER) || isSuperAdmin || ownerid.equals(userid)))
			return true;
		else{
			for (int u=0;u<users.size();u++){
				if (userid.equals(((ETSUser)users.elementAt(u)).getUserId())){
					return true;
				}	
			}
		}
		return false;
	}

	
	
	static public boolean isAuthorized(String ownerid,int id,String projectid,String userRole,boolean isSuperAdmin, boolean isExecutive,boolean isViewOnly,boolean isCat,String userid){
		
		if (isViewOnly && (userRole.equals(Defines.WORKSPACE_OWNER) || isSuperAdmin || isExecutive || ownerid.equals(userid)))
			return true;
		else if ((!isViewOnly) && (userRole.equals(Defines.WORKSPACE_OWNER) || isSuperAdmin || ownerid.equals(userid)))
			return true;
		else{
			try{
				Vector users = ETSDatabaseManager.getRestrictedProjMemberIds(projectid,id,isCat);
				for (int u=0;u<users.size();u++){
					if (userid.equals(users.elementAt(u))){
						if (isViewOnly || (userRole.equals(Defines.WORKSPACE_MANAGER)))
							return true;
					}	
				}
			}
			catch(Exception e){
				e.printStackTrace();
				return false;	
			}
		}
		return false;
	}
	
	static public boolean isAuthorized(String ownerid,int id,String projectid,String userRole,boolean isSuperAdmin, boolean isExecutive,boolean isViewOnly,boolean isCat,boolean allMems, String userid){
		
			if (isViewOnly && (userRole.equals(Defines.WORKSPACE_OWNER) || isSuperAdmin || isExecutive || ownerid.equals(userid)))
				return true;
			else if ((!isViewOnly) && (userRole.equals(Defines.WORKSPACE_OWNER) || isSuperAdmin || ownerid.equals(userid)))
				return true;
			else{
				try{
					Vector users = ETSDatabaseManager.getRestrictedProjMemberIds(projectid,id,isCat);
					for (int u=0;u<users.size();u++){
						if (userid.equals(users.elementAt(u))){
							if (isViewOnly)
								return true;
							else if(allMems && ((!isExecutive) && (!userRole.equals(Defines.WORKSPACE_VISITOR)) && (!userRole.equals(Defines.WORKSPACE_CLIENT)))){
								return true;
							}
							else if (userRole.equals(Defines.WORKSPACE_MANAGER))
								return true;
						}	
					}
				}
				catch(Exception e){
					e.printStackTrace();
					return false;	
				}
			}
			return false;
		}

	static public String getSessionString(String key, HttpServletRequest req){
		
		String s = (String)req.getSession(true).getAttribute(key);
		
		/*if (s == null){
			s = "";	
		}*/
		
		return s;
	}
	static public char getSessionChar(String key, HttpServletRequest req){
		char c;
		
		String s = (String)req.getSession(true).getAttribute(key);
	
		if (s != null && s.length()>0){
			c = s.charAt(0);
		}
		else{
			c = ' ';	
		}
		
		return c;
	}
	static public Vector getSessionVector(String key, HttpServletRequest req){
		
		Vector v = (Vector)req.getSession(true).getAttribute(key);
		if (v == null){
			v = new Vector();	
		}
	
		return v;
	}
	
	static public void removeSessionVar(String key, HttpServletRequest req){
		req.getSession(true).removeAttribute(key);
	}


}