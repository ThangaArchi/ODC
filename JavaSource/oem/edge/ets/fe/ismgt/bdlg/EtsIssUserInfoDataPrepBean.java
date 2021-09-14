package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.*;
import java.util.*;

import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.dao.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004                                     */
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
 * @author v2phani
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class EtsIssUserInfoDataPrepBean {
	
	public static final String VERSION = "1.11";
	private String userIrId;

	/**
	 * Constructor for EtsIssUserInfoDataPrepBean.
	 */
	public EtsIssUserInfoDataPrepBean(String userIrId) {
		super();
		this.userIrId=userIrId;
	}
	
	
	/**
	 * This will method will call DAO and get user info details
	 */
	
	
	public EtsIssProjectMember createUserInfo() throws SQLException,Exception{
		
		CommonInfoDAO comDao = new CommonInfoDAO();
		
		return comDao.getUserDetailsInfo(userIrId);
		
		
		
	}

}

