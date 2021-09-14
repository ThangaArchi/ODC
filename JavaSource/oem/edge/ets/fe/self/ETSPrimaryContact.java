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


/*
 * Created on Jan 20, 2005
 */

package oem.edge.ets.fe.self;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.setmet.ETSSetMetDAO;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSPrimaryContact {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";

	String ID = "";
	String Name = "";
	String Email = "";
	String Phone = "";
	boolean isPhotoAvailable = false;

	public ETSPrimaryContact() {
		super();
		Name = "";
		Email = "";
		Phone = "";
		ID = "";
	}

	public void init(String sProjectId) throws SQLException, Exception {

		Connection con = null;

		try {

			con = ETSDBUtils.getConnection();

			String sPrimary = ETSSetMetDAO.getPrimaryContact(con,sProjectId);

			isPhotoAvailable = ETSUtils.isUserPhotoAvailable(con,sPrimary);

			Name = ETSUtils.getUsersName(con,sPrimary);

			Phone = ETSUtils.getUserPhone(con,sPrimary);

			ID = sPrimary;

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(con);
		}
	}
	/**
	 * @return
	 */
	public String getEmail() {
		return Email;
	}

	/**
	 * @return
	 */
	public boolean isPhotoAvailable() {
		return isPhotoAvailable;
	}

	/**
	 * @return
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @return
	 */
	public String getPhone() {
		return Phone;
	}

	/**
	 * @param string
	 */
	public void setEmail(String string) {
		Email = string;
	}

	/**
	 * @param b
	 */
	public void setPhotoAvailable(boolean b) {
		isPhotoAvailable = b;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		Name = string;
	}

	/**
	 * @param string
	 */
	public void setPhone(String string) {
		Phone = string;
	}

	/**
	 * @return
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @param string
	 */
	public void setID(String string) {
		ID = string;
	}

}
