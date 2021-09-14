/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
package oem.edge.ets.fe.acmgt.helpers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.model.UserIccStatusModel;
import oem.edge.ets.fe.acmgt.resources.WrkSpcAddMemberContantsIF;
import oem.edge.ets.fe.acmgt.actions.AddMembrUserDetails;
import oem.edge.ets.fe.acmgt.actions.BaseAddMemberForm;


import org.apache.commons.logging.Log;

/**
 * @author Suresh
 *
 */
public class AddMemberGuiUtils implements WrkSpcAddMemberContantsIF{

	private static Log logger = EtsLogger.getLogger(AddMemberGuiUtils.class);
	public static final String VERSION = "1.9";

	int index;
	boolean hasMoreMembersToProcess = false;
	Vector UserList = null;
	
	WrkSpcTeamUtils teamUtils = new WrkSpcTeamUtils();
	AddMembrToWrkSpcDAO addWrkSpcDao = new AddMembrToWrkSpcDAO();
	WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();
	
	
	public AddMemberGuiUtils() {
		super();
   	}
	
	/**
	 * @param pdRequest
	 * @return
	 */
	public static EdgeAccessCntrl getEdgeAccess(HttpServletRequest pdRequest) {
		
		EdgeAccessCntrl pdAccess =
			(EdgeAccessCntrl) pdRequest.getAttribute(REQ_ATTR_EDGEACCESS);

		return pdAccess;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getUserRole(HttpServletRequest pdRequest) {
		String strUserRole =
			(String) pdRequest.getAttribute(REQ_ATTR_USERROLE);

		return strUserRole;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static boolean isInternal(HttpServletRequest pdRequest) {
		boolean bIsInternal = false;

		EdgeAccessCntrl pdAccess =
			(EdgeAccessCntrl) pdRequest.getAttribute(REQ_ATTR_EDGEACCESS);
		if (pdAccess
			.gDECAFTYPE
			.trim()
			.equals(DECAFTYPE_INTERNAL)) {
			bIsInternal = true;
		}
		return bIsInternal;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getFormContext(HttpServletRequest pdRequest) {
		return (
			(BaseAddMemberForm) pdRequest.getAttribute(ADD_MEMBR_FORM))
			.getFormContext();
	}
	
	/**
	 * @param pdRequest
	 * @return
	 */
	public static BaseAddMemberForm getAddMembrForm(ServletRequest pdRequest) {
		return (
			(BaseAddMemberForm) pdRequest.getAttribute(ADD_MEMBR_FORM));
	}
	
	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getProjectID(HttpServletRequest pdRequest) {
		String strProjectID = null;
		strProjectID = pdRequest.getParameter(PARAM_PROJECTID);

		if (StringUtil.isNullorEmpty(strProjectID)) {
			BaseAddMemberForm udForm = getAddMembrForm(pdRequest);
			if (udForm != null) {
				strProjectID = udForm.getProj();
			}
		}

		return strProjectID;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getLinkID(HttpServletRequest pdRequest) {
		String strLinkID = null;
		strLinkID = pdRequest.getParameter(PARAM_LINKID);

		if (StringUtil.isNullorEmpty(strLinkID)) {
			BaseAddMemberForm udForm = getAddMembrForm(pdRequest);
			if (udForm != null) {
				strLinkID = udForm.getLinkid();
			}
		}

		return strLinkID;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getTopCatID(HttpServletRequest pdRequest) {
		String strTopCatID = null;
		strTopCatID = pdRequest.getParameter(PARAM_TOPCATEGORY);

		if (StringUtil.isNullorEmpty(strTopCatID)) {
			BaseAddMemberForm udForm = getAddMembrForm(pdRequest);
			if (udForm != null) {
				strTopCatID = udForm.getTc();
			}
		}

		return strTopCatID;
	}
	

	/**
	 * @param pdRequest
	 * @param iTabType
	 * @return
	 */
	public static int getTopCatForTab(
		HttpServletRequest pdRequest,
		int iTabType) {

		AddMembrToWrkSpcDAO udDAO = new AddMembrToWrkSpcDAO();
		int iTopCatId = -1;
		try {
			int iTopCatID =
				udDAO.getTopCatId(getProjectID(pdRequest), iTabType);
		} catch (SQLException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		return iTopCatId;
	}
	
	
	/**
	 * @param pdRequest
	 * @param strKey
	 * @return
	 */
	public static String getParameter(
		HttpServletRequest pdRequest,
		String strKey) {
		String strValue = pdRequest.getParameter(strKey);

		if (StringUtil.isNullorEmpty(strValue)) {
			return StringUtil.EMPTY_STRING;
		} else {
			return strValue;
		}
	}
		
	public String verifyUser(ETSProj proj,String id){
	
		String userStatus = "";
		Vector userIdsinWS = new Vector();
		Vector inviteUserIds = new Vector();
		Vector idsTobeConfmd = new Vector();
		Vector idsTobeAdded = new Vector();
		Vector usrsWthMltiEmailId = new Vector();
			
		try {
		
			if (teamUtils.isUserDefndInWrkSpc(proj.getProjectId(), id)) {
				
				userStatus = "userIdsinWS";
				
			}else { // id not in WS... Check AMT-ICC
				
				if(teamUtils.isUserDefndinICC(id)){
						
					logger.debug("USERID::" + id + " defined in ICC");

					UserIccStatusModel iccStatModel = addWrkSpcDao.getUserIdStatusInICC(id);

					int uidcount = iccStatModel.getAmtuidcount();
					int emailcount = iccStatModel.getAmtemailcount();
					int decafidcount = iccStatModel.getDecafidcount();

					logger.debug("IR USERID COUNT:" + id + " defined in ICC::" + uidcount);
					logger.debug("USERID EMAIL COUNT:" + id + " defined in ICC::" + emailcount);
					logger.debug("DECAF USERID COUNT:" + id + " defined in DECAF::" + decafidcount);
						
					if (uidcount == 1) {

						if (decafidcount == 0) {

							logger.debug("USERID::" + id + " DEFINED IN ICC/DECAF ID COUNT==0.Once again updating profile into ICC");
							addWrkSpcDao.syncUser(id);
						}
						//add member to wrkspc
						userStatus = "idsTobeAdded";
						
					} else if (emailcount == 1) {
						if (decafidcount == 0) {

							logger.debug("USERID::" + id + " DEFINED IN ICC/DECAF ID COUNT==0.Once again updating profile into ICC");
							addWrkSpcDao.syncUser(teamUtils.getIRUserIdFromEmail(id));

						}
						//add member to wrkspc
						userStatus = "idsTobeAdded";

					} else if (emailcount > 1) {
						String wrkSpcOwnerId = WrkSpcTeamUtils.getOwnerIdForProject(proj.getProjectId());
						mailHandler.sendMailToWOOnMultipleIds(id, wrkSpcOwnerId, proj.getProjectId());
						userStatus = "usrsWthMltiEmailId";
					}
					
			  }else {
			  		//  when the user is not defined in AMT-ICC
						logger.debug("USERID::" + id + " NOT defined in ICC. checking in UD");
						//if id exists in IR
						if (teamUtils.isUserIdDefndInUD(id)) {
							logger.debug("USERID::" + id + " DEFINED IN UD.Loading into ICC");
							addWrkSpcDao.syncUser(id);
							logger.debug("USERID::" + id + " DEFINED IN UD.Loaded into ICC and adding to wrkspace");
							//add member to wrkspc
							userStatus = "idsTobeAdded";
	
						} else { //if userid not defined in UD
	
							logger.debug("USERID::" + id + " NOT DEFINED IN UD.");
							if((id.indexOf("ibm.com") > 0) || (id.indexOf("IBM.COM") > 0)|| (id.indexOf("/ibm") > 0) || (id.indexOf("/IBM") > 0)){
							
								userStatus = "idsTobeConfmd";
	
							}else{
								userStatus = "inviteUserIds";
							}
	
						}   //if userid not defined in UD
						
			  	}   //  when the user is not defined in AMT-ICC
			
	    } // id not in WS	
		} catch (Exception ex) {

			if (ex != null) {
				logger.error("Exception in AddMemberGuiUtils", ex);
				ex.printStackTrace();

			}
		}
		logger.debug("USER STATUS::" + id + " == "+ userStatus);
		return userStatus.trim();
	}
	
	
	public AddMembrUserDetails verifyIdsTobeAdded(Connection conn,AddMembrUserDetails user) throws SQLException,Exception {
						
				boolean ctryNull = false;
				boolean cmpnyNull = false;
								
				ETSUserDetails defndUser = (ETSUserDetails) addWrkSpcDao.getUserDetails(user.getEmailId());
				user.setUserName(defndUser.getFirstName()+" "+defndUser.getLastName());
				user.setWebId(defndUser.getWebId());
				user.setEmailId(defndUser.getEMail());
				
						if ((defndUser.getUserType() == defndUser.USER_TYPE_INTERNAL)){
									user.setUserType("I");
						}else if((defndUser.getUserType() == defndUser.USER_TYPE_EXTERNAL)){
								
								user.setUserType("E");
								user.setAddress(defndUser.getStreetAddress());
								user.setCompany(defndUser.getCompany());
								user.setCountryCode(defndUser.getCountryCode());
								
								if(StringUtil.isNullorEmpty(user.getCompany())){
									  cmpnyNull = true;
								}
								
								if(StringUtil.isNullorEmpty(user.getCountryCode())){
									   ctryNull = true;
								}else{
									user.setCountry(addWrkSpcDao.getCountryName(conn,defndUser.getCountryCode()));
									
									if(StringUtil.isNullorEmpty(user.getCountry())){
										ctryNull = true;
									}
								}
								
								if((cmpnyNull) && (ctryNull)){
									  	user.setCtryCmpEmpty("CTRY_CMPNY");								
								}else if(cmpnyNull){
										user.setCtryCmpEmpty("CMPNY");  
								}else if(ctryNull){
									user.setCtryCmpEmpty("CTRY");
								}else{
									user.setCtryCmpEmpty("");
								}
								
								
						}else if((defndUser.getUserType() == defndUser.USER_TYPE_INTERNAL_PENDING_VALIDATION)){
							
								user.setUserType("P");
								user.setAddress(defndUser.getStreetAddress());
								user.setCompany(defndUser.getCompany());
								user.setCountryCode(defndUser.getCountryCode());
															
								if(!StringUtil.isNullorEmpty(user.getCountryCode())){
						
									user.setCountry(addWrkSpcDao.getCountryName(conn,defndUser.getCountryCode()));
															
									
								}
								
						}else{
								user.setUserType("INVALID");
						}
			
		return user;
	}
		
} //end of class
