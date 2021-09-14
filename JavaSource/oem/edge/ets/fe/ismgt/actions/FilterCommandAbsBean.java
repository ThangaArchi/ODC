package oem.edge.ets.fe.ismgt.actions;

import java.sql.*;
import java.util.*;
import java.io.*;

import oem.edge.common.*;
import oem.edge.amt.*;

import javax.servlet.*;
import javax.servlet.http.*;

import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.bdlg.*;
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
 * An abstract class for different states of Filter control process
 */
public abstract class FilterCommandAbsBean {
	
	public static final String VERSION = "1.10";
	private HttpServletRequest request;
	private HttpServletResponse response;
	private EtsIssFilterObjectKey issobjkey;
	

	/**
	 * Constructor for FilterCommandAbsBean.
	 */
	public FilterCommandAbsBean(HttpServletRequest request,HttpServletResponse response,EtsIssFilterObjectKey issobjkey) {
		super();
		this.request=request;
		this.response=response;
		this.issobjkey=issobjkey;
	}
	
	/**
	 * takes the action on issue/change, based on subaction type and action value
	 * 
	 */

	public abstract int processRequest() throws SQLException,Exception;
	
	/***
	 * to get suitable busniess delegate  for the business objects
	 * 
	 */
	
	public FilterDetailsDataPrepAbsBean getFilterBldgBean() throws Exception{
		
		
		FilterDetailsPrepFactory detFac = new FilterDetailsPrepFactory();
		
		FilterDetailsDataPrepAbsBean detObj = detFac.createFilterDetailsPrepBean(request,response,issobjkey);
		
		return detObj;
		
	}
	
	

	/**
	 * Returns the request.
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Returns the response.
	 * @return HttpServletResponse
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * Sets the request.
	 * @param request The request to set
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Sets the response.
	 * @param response The response to set
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}


	

	/**
	 * Returns the issobjkey.
	 * @return EtsIssFilterObjectKey
	 */
	public EtsIssFilterObjectKey getIssobjkey() {
		return issobjkey;
	}

	/**
	 * Sets the issobjkey.
	 * @param issobjkey The issobjkey to set
	 */
	public void setIssobjkey(EtsIssFilterObjectKey issobjkey) {
		this.issobjkey = issobjkey;
	}

}

