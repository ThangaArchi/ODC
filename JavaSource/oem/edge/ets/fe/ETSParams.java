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


package oem.edge.ets.fe;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.brand.UnbrandedProperties;


/**
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ETSParams {

  public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";
  private static final String CLASS_VERSION = "1.9";


    public Connection con;
    public HttpServletRequest request;
    public HttpServletResponse response;
    public PrintWriter out;
    public ETSProj proj;
    public EdgeAccessCntrl es;
    public int topCat;
    public String linkId;
    public ETSProjectInfoBean beanInfo;
    public String currentTabName;
    public ETSProperties theProperties = null;
    public boolean isSuperAdmin = false;
    public boolean isExecutive = false;
    public UnbrandedProperties prop;

    public void setConnection(Connection conn) {
	this.con = conn;
    }

    public Connection getConnection() {
	return this.con;
    }

    public void setRequest(HttpServletRequest req) {
	this.request = req;
    }

    public HttpServletRequest getRequest() {
	return this.request;
    }

    public void setResponse(HttpServletResponse res) {
	this.response = res;
    }

    public HttpServletResponse getResponse() {
	return this.response;
    }

    public void setWriter(PrintWriter writer) {
	this.out = writer;
    }

    public PrintWriter getWriter() {
	return this.out;
    }

    public void setETSProj(ETSProj project) {
	this.proj = project;
    }

    public ETSProj getETSProj() {
	return this.proj;
    }

    public void setEdgeAccessCntrl(EdgeAccessCntrl edge) {
	this.es = edge;
    }

    public EdgeAccessCntrl getEdgeAccessCntrl() {
	return this.es;
    }

    public void setTopCat(int iTopCat) {
	this.topCat = iTopCat;
    }

    public int getTopCat() {
	return this.topCat;
    }

    public void setLinkId(String sLinkId) {
		this.linkId = sLinkId;
    }

    public String getLinkId() {
		return this.linkId;
    }

	/**
	 * Returns the beanInfo.
	 * @return ETSProjectInfoBean
	 */
	public ETSProjectInfoBean getProjBeanInfo() {
		return beanInfo;
	}

	/**
	 * Sets the beanInfo.
	 * @param beanInfo The beanInfo to set
	 */
	public void setProjBeanInfo(ETSProjectInfoBean beanInfo) {
		this.beanInfo = beanInfo;
	}

	/**
	 * Returns the currentTabName.
	 * @return String
	 */
	public String getCurrentTabName() {
		return currentTabName;
	}

	/**
	 * Sets the currentTabName.
	 * @param currentTabName The currentTabName to set
	 */
	public void setCurrentTabName(String currentTabName) {
		this.currentTabName = currentTabName;
	}

	public ETSProperties getProperties() {
		if (theProperties == null) {
			theProperties = new ETSProperties();
			theProperties.loadProperties();
		}
		return theProperties;
	}

	/**
	 * @return
	 */
	public boolean isExecutive() {
		return this.isExecutive;
	}

	/**
	 * @return
	 */
	public boolean isSuperAdmin() {
		return this.isSuperAdmin;
	}

	/**
	 * @param b
	 */
	public void setExecutive(boolean b) {
		this.isExecutive = b;
	}

	/**
	 * @param b
	 */
	public void setSuperAdmin(boolean b) {
		this.isSuperAdmin = b;
	}

	/**
	 * @return
	 */
	public UnbrandedProperties getUnbrandedProperties() {
		return this.prop;
	}

	/**
	 * @param properties
	 */
	public void setUnbrandedProperties(UnbrandedProperties properties) {
		this.prop = properties;
	}

}
