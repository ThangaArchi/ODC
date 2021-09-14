package oem.edge.ed.odc.applet;
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

import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.io.*;
import java.awt.*;

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
		System.setProperties(p);
		if (connType != 2) cfg.setIntProperty("ODCCONNTYPE",2);
	}
	else if ((connType == 1 || connType == -1) && proxyServerHost != null && proxyServerPort != null) {
		System.out.println("Using proxy server: " + proxyServerHost + ":" + proxyServerPort);
		Properties p = System.getProperties();
		p.put("proxySet","true");
		p.put("proxyHost",proxyServerHost);
		p.put("proxyPort",proxyServerPort);
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
	else if (connType != 0) cfg.setIntProperty("ODCCONNTYPE",0);
}
/**
 * ConfigMgr constructor comment.
 */
public ConfigMgr(ConfigFile cfg) {
	super();

	this.parent = new Frame();

	this.cfg = cfg;
}
public void addActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}
/**
 * Comment
 */
public void changeCitrix() {
	ConfigMgrIcaDlg dlg = new ConfigMgrIcaDlg(parent,cfg);

	// Center the window
	Point winPos = parent.getLocation();
	Dimension winSize = parent.getSize();
	Dimension dlgSize = dlg.getSize();
	dlg.setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	dlg.setVisible(true);
}
/**
 * Comment
 */
public void changeConnectivity() {
	ConfigMgrConnDlg dlg = new ConfigMgrConnDlg(parent,cfg);

	// Center the window
	Point winPos = parent.getLocation();
	Dimension winSize = parent.getSize();
	Dimension dlgSize = dlg.getSize();
	dlg.setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	dlg.setVisible(true);

	if (dlg.okPressed) {
		fireActionPerformed(new ActionEvent(this,0,"ConnectivityChanged"));
	}
}
/**
 * Comment
 */
public boolean changeConnectivityWithResponse(Applet applet) {
	ConfigMgrConnDlg dlg = new ConfigMgrConnDlg(parent,cfg,true);

	// Center the window
	Point aLocScr = applet.getLocationOnScreen();
	Dimension aSize = applet.getSize();
	Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();

	Point dLoc = new Point();
	Dimension dSize = dlg.getSize();

	dLoc.x = aLocScr.x + aSize.width / 2 - dSize.width / 2;
	dLoc.y = aLocScr.y + aSize.height / 2 - dSize.height / 2;

	// if too far right, slide it left...
	int i = sSize.width - (dLoc.x + dSize.width);
	if (i < 0)
		dLoc.x += i;
	// if too far left, slide it right...
	if (dLoc.x < 0)
		dLoc.x = 0;

	// if too far down, slide it up...
	i = sSize.height - (dLoc.y + dSize.height);
	if (i < 0)
		dLoc.y += i;

	// if too far up, slide it down...
	if (dLoc.y < 0)
		dLoc.y = 0;

	dlg.setLocation(dLoc);
	dlg.setVisible(true);

	return dlg.okPressed;
}
/**
 * Comment
 */
public void changeReal() {
	ConfigMgrRealDlg dlg = new ConfigMgrRealDlg(parent,cfg);

	// Center the window
	Point winPos = parent.getLocation();
	Dimension winSize = parent.getSize();
	Dimension dlgSize = dlg.getSize();
	dlg.setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	dlg.setVisible(true);
}
/**
 * Method to support listener events.
 */
protected void fireActionPerformed(java.awt.event.ActionEvent e) {
	if (aActionListener == null) {
		return;
	};
	aActionListener.actionPerformed(e);
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2002 3:32:29 PM)
 * @return com.ibm.as400.webaccess.common.ConfigFile
 */
public ConfigFile getCfg() {
	return cfg;
}
public void removeActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}
}
