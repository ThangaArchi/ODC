package oem.edge.ed.odc.dropbox.client;
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

import java.awt.Frame;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import oem.edge.ed.odc.applet.ConfigFile;

/**
 * Insert the type's description here.
 * Creation date: (5/13/2002 3:07:38 PM)
 * @author: Mike Zarnick
 */
public class ConfigMgr {
	private ConfigFile cfg = new ConfigFile();
	private Frame parent = null;
	protected transient java.awt.event.ActionListener aActionListener = null;
/**
 * ConfigMgr constructor comment.
 */
public ConfigMgr(String fileName, Frame parent) throws IOException{
	super();

	if (parent != null)
		this.parent = parent;
	else
		this.parent = new Frame();

	if (fileName != null) {
		FileInputStream f = new FileInputStream(fileName);
		cfg.load(f);
		f.close();
	}

	//  Socks server address & port.
	int connType = cfg.getIntProperty("ODCCONNTYPE",-1);
	String socksServerHost = cfg.getProperty("ODCSOCKSSERVER",null);
	String socksServerPort = cfg.getProperty("ODCSOCKSPORT",null);
	String proxyServerHost = cfg.getProperty("ODCPROXYSERVER",null);
	String proxyServerPort = cfg.getProperty("ODCPROXYPORT",null);
	boolean proxyAuth = cfg.getBoolProperty("ODCPROXYAUTH",false);
	String proxyId = cfg.getProperty("ODCPROXYID",null);
	if ((connType == 2 || connType == -1) && socksServerHost != null && socksServerPort != null) {
		System.out.println("Using socks server: " + socksServerHost + ":" + socksServerPort);
		Properties p = System.getProperties();
		p.put("socksProxySet","true");
		p.put("socksProxyHost",socksServerHost);
		p.put("socksProxyPort",socksServerPort);
		p.remove("proxySet");
		p.remove("proxyHost");
		p.remove("proxyPort");
		p.remove("http.proxySet");
		p.remove("http.proxyHost");
		p.remove("http.proxyPort");
		p.remove("https.proxySet");
		p.remove("https.proxyHost");
		p.remove("https.proxyPort");
		System.setProperties(p);
		if (connType != 2) cfg.setIntProperty("ODCCONNTYPE",2);
	}
	else if ((connType == 1 || connType == -1) && proxyServerHost != null && proxyServerPort != null) {
		System.out.println("Using proxy server: " + proxyServerHost + ":" + proxyServerPort);
		Properties p = System.getProperties();
		p.put("proxySet","true");
		p.put("proxyHost",proxyServerHost);
		p.put("proxyPort",proxyServerPort);
		p.put("http.proxySet","true");
		p.put("http.proxyHost",proxyServerHost);
		p.put("http.proxyPort",proxyServerPort);
		p.put("https.proxySet","true");
		p.put("https.proxyHost",proxyServerHost);
		p.put("https.proxyPort",proxyServerPort);
		p.remove("socksProxySet");
		p.remove("socksProxyHost");
		p.remove("socksProxyPort");
		System.setProperties(p);
		if (connType != 1) cfg.setIntProperty("ODCCONNTYPE",1);
		if (proxyAuth) {
			ConfigMgrAuthDlg auth = new ConfigMgrAuthDlg(parent,cfg);
			auth.promptAuth();
		}
	}
	else {
		System.out.println("Using direct connection.");
		Properties p = System.getProperties();
		p.remove("proxySet");
		p.remove("proxyHost");
		p.remove("proxyPort");
		p.remove("http.proxySet");
		p.remove("http.proxyHost");
		p.remove("http.proxyPort");
		p.remove("https.proxySet");
		p.remove("https.proxyHost");
		p.remove("https.proxyPort");
		p.remove("socksProxySet");
		p.remove("socksProxyHost");
		p.remove("socksProxyPort");
		System.setProperties(p);
		if (connType != 0) cfg.setIntProperty("ODCCONNTYPE",0);
	}
}
/**
 * ConfigMgr constructor comment.
 */
public ConfigMgr(ConfigFile cfg) {
	super();

	this.parent = new Frame();

	this.cfg = cfg;
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2002 3:32:29 PM)
 * @return com.ibm.as400.webaccess.common.ConfigFile
 */
public ConfigFile getCfg() {
	return cfg;
}
public void changeConnectivity() {
	ConfigMgrConnDlg dlg = new ConfigMgrConnDlg(parent,cfg);
	dlg.setLocationRelativeTo(parent);
	dlg.show();
}
}
