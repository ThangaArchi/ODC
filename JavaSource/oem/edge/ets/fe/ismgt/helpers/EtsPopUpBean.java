package oem.edge.ets.fe.ismgt.helpers;

import java.io.*;
import java.sql.*;
import java.util.*;

import oem.edge.amt.*;
import oem.edge.common.*;

import javax.servlet.*;
import javax.servlet.http.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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
 * A wrapper class to set the Popup header params
 * and get the Popup Header, Footer
 *
 */
public class EtsPopUpBean {

	public static final String VERSION = "1.11";

	private PopupHeaderFooter pHead;

	/**
	 * Constructor for EtsPopUpBean.
	 */
	public EtsPopUpBean() {
		super();
	}

	/**
	 * method to initialize the params
	 * takes pageTitle >> HTML title
	 * takes pageHeader >> page header
	 */

	public void init(String pageTitle, String pageHeader) {

		pHead = new PopupHeaderFooter();
		pHead.setPageTitle(pageTitle);
		pHead.setHeader(pageHeader);

	}

	/**
	 * get Popup Header
	 */

	public String getPopupHeader() {

		return pHead.printPopupHeader();

	}

	/**
	 * get Popup subheader
	 */

	public String getPopopSubHeader() {

		return pHead.printSubHeader();
	}

	/**
	 * get Popup Footer
	 */

	public String getPopupFooter() {

		return pHead.printPopupFooter();

	}

	/**
	 * get Popup Simple Footer
	 */

	public String getSimpleFooter() {

		return pHead.printSimpleFooter();

	}
}

