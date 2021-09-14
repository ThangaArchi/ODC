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

/**
 * Insert the type's description here.
 * Creation date: (5/7/2002 9:48:41 AM)
 * @author: Mike Zarnick
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class ConfigMgrConnDlg extends Dialog {
	private Panel ivjContentsPane = null;
	private Checkbox ivjDirectCB = null;
	private Panel ivjPanel2 = null;
	private Label ivjPortLbl = null;
	private Label ivjPortLbl1 = null;
	private Checkbox ivjProxyCB = null;
	private TextField ivjProxyPortTF = null;
	private TextField ivjProxyServerTF = null;
	private Label ivjServerLbl = null;
	private Label ivjServerLbl1 = null;
	private Button ivjSocksCanBtn = null;
	private Checkbox ivjSocksCB = null;
	private Label ivjSocksIntroLbl = null;
	private Button ivjSocksOkBtn = null;
	private TextField ivjSocksPortTF = null;
	private TextField ivjSocksServerTF = null;
	private Checkbox ivjSocksTmpCB = null;
	private ConfigFile cfg = new ConfigFile();
	private boolean install = false;
	public boolean okPressed = false;
	private CheckboxGroup ivjSocksCBG = null;  // @jve:visual-info  decl-index=0 visual-constraint="431,20"
	private Panel ivjContentsPane1 = null;
	private Dialog ivjErrorDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="20,473"
	private Label ivjErrorLbl = null;
	private Button ivjErrorOkBtn = null;
	private Checkbox ivjAuthCB = null;
	private Panel ivjPanel1 = null;
	private Label ivjUidLbl = null;
	private TextField ivjUidTF = null;
	private TextArea ivjSocksTA = null;
/**
 * ConfigMgr constructor comment.
 * @param owner java.awt.Frame
 */
public ConfigMgrConnDlg(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * ConfigMgr constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public ConfigMgrConnDlg(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * ConfigMgr constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public ConfigMgrConnDlg(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ConfigMgr constructor comment.
 * @param owner java.awt.Frame
 */
public ConfigMgrConnDlg(java.awt.Frame owner, ConfigFile cfg) {
	super(owner);
	this.cfg = cfg;
	initialize();
}
/**
 * ConfigMgr constructor comment.
 * @param owner java.awt.Frame
 */
public ConfigMgrConnDlg(java.awt.Frame owner, ConfigFile cfg, boolean install) {
	super(owner);
	this.cfg = cfg;
	this.install = install;
	initialize();
}
/**
 * ConfigMgr constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public ConfigMgrConnDlg(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * Comment
 */
public void authChanged(ItemEvent e) {
	boolean b = (e.getStateChange() == ItemEvent.SELECTED);
	
	getUidLbl().setEnabled(b);
	getUidTF().setEnabled(b);

	getUidTF().requestFocus();
	textChg();

	return;
}
/**
 * Comment
 */
public void directChanged(ItemEvent e) {
	if (e.getStateChange() == ItemEvent.SELECTED) {
		getServerLbl1().setEnabled(false);
		getProxyServerTF().setEnabled(false);
		getPortLbl1().setEnabled(false);
		getProxyPortTF().setEnabled(false);
		getAuthCB().setEnabled(false);
		getUidLbl().setEnabled(false);
		getUidTF().setEnabled(false);
		getServerLbl().setEnabled(false);
		getSocksServerTF().setEnabled(false);
		getPortLbl().setEnabled(false);
		getSocksPortTF().setEnabled(false);

		getSocksOkBtn().setEnabled(true);
	}

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (03/07/01 2:22:27 PM)
 */
public void displayError() {
	// Center the window
	Point winPos = getLocation();
	Dimension winSize = getSize();

	Dimension dlgSize = getErrorDlg().getSize();
	getErrorDlg().setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	getErrorDlg().setVisible(true);
}
/**
 * Return the AuthCB property value.
 * @return java.awt.Checkbox
 */
private java.awt.Checkbox getAuthCB() {
	if (ivjAuthCB == null) {
		try {
			ivjAuthCB = new java.awt.Checkbox();
			ivjAuthCB.setName("AuthCB");
			ivjAuthCB.setLabel("Use authentication");
			ivjAuthCB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						authChanged(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAuthCB;
}
/**
 * Return the ContentsPane property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getContentsPane() {
	if (ivjContentsPane == null) {
		try {
			ivjContentsPane = new java.awt.Panel();
			ivjContentsPane.setName("ContentsPane");
			ivjContentsPane.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsSocksIntroLbl = new java.awt.GridBagConstraints();
			constraintsSocksIntroLbl.gridx = 0; constraintsSocksIntroLbl.gridy = 0;
			constraintsSocksIntroLbl.gridwidth = 0;
			constraintsSocksIntroLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSocksIntroLbl.insets = new java.awt.Insets(10, 10, 0, 10);
			getContentsPane().add(getSocksIntroLbl(), constraintsSocksIntroLbl);

			java.awt.GridBagConstraints constraintsDirectCB = new java.awt.GridBagConstraints();
			constraintsDirectCB.gridx = 0; constraintsDirectCB.gridy = 1;
			constraintsDirectCB.gridwidth = 0;
			constraintsDirectCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDirectCB.insets = new java.awt.Insets(5, 10, 0, 10);
			getContentsPane().add(getDirectCB(), constraintsDirectCB);

			java.awt.GridBagConstraints constraintsProxyCB = new java.awt.GridBagConstraints();
			constraintsProxyCB.gridx = 0; constraintsProxyCB.gridy = 2;
			constraintsProxyCB.gridwidth = 0;
			constraintsProxyCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsProxyCB.insets = new java.awt.Insets(0, 10, 0, 10);
			getContentsPane().add(getProxyCB(), constraintsProxyCB);

			java.awt.GridBagConstraints constraintsServerLbl1 = new java.awt.GridBagConstraints();
			constraintsServerLbl1.gridx = 0; constraintsServerLbl1.gridy = 3;
			constraintsServerLbl1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsServerLbl1.insets = new java.awt.Insets(0, 20, 0, 0);
			getContentsPane().add(getServerLbl1(), constraintsServerLbl1);

			java.awt.GridBagConstraints constraintsProxyServerTF = new java.awt.GridBagConstraints();
			constraintsProxyServerTF.gridx = 1; constraintsProxyServerTF.gridy = 3;
			constraintsProxyServerTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsProxyServerTF.weightx = 1.0;
			constraintsProxyServerTF.insets = new java.awt.Insets(0, 0, 0, 5);
			getContentsPane().add(getProxyServerTF(), constraintsProxyServerTF);

			java.awt.GridBagConstraints constraintsPortLbl1 = new java.awt.GridBagConstraints();
			constraintsPortLbl1.gridx = 2; constraintsPortLbl1.gridy = 3;
			constraintsPortLbl1.anchor = java.awt.GridBagConstraints.WEST;
			getContentsPane().add(getPortLbl1(), constraintsPortLbl1);

			java.awt.GridBagConstraints constraintsProxyPortTF = new java.awt.GridBagConstraints();
			constraintsProxyPortTF.gridx = 3; constraintsProxyPortTF.gridy = 3;
			constraintsProxyPortTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsProxyPortTF.insets = new java.awt.Insets(0, 0, 0, 10);
			getContentsPane().add(getProxyPortTF(), constraintsProxyPortTF);

			java.awt.GridBagConstraints constraintsSocksCB = new java.awt.GridBagConstraints();
			constraintsSocksCB.gridx = 0; constraintsSocksCB.gridy = 5;
			constraintsSocksCB.gridwidth = 0;
			constraintsSocksCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSocksCB.insets = new java.awt.Insets(0, 10, 0, 10);
			getContentsPane().add(getSocksCB(), constraintsSocksCB);

			java.awt.GridBagConstraints constraintsServerLbl = new java.awt.GridBagConstraints();
			constraintsServerLbl.gridx = 0; constraintsServerLbl.gridy = 6;
			constraintsServerLbl.insets = new java.awt.Insets(0, 20, 0, 0);
			getContentsPane().add(getServerLbl(), constraintsServerLbl);

			java.awt.GridBagConstraints constraintsSocksServerTF = new java.awt.GridBagConstraints();
			constraintsSocksServerTF.gridx = 1; constraintsSocksServerTF.gridy = 6;
			constraintsSocksServerTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSocksServerTF.weightx = 1.0;
			constraintsSocksServerTF.insets = new java.awt.Insets(0, 0, 0, 5);
			getContentsPane().add(getSocksServerTF(), constraintsSocksServerTF);

			java.awt.GridBagConstraints constraintsPortLbl = new java.awt.GridBagConstraints();
			constraintsPortLbl.gridx = 2; constraintsPortLbl.gridy = 6;
			getContentsPane().add(getPortLbl(), constraintsPortLbl);

			java.awt.GridBagConstraints constraintsSocksPortTF = new java.awt.GridBagConstraints();
			constraintsSocksPortTF.gridx = 3; constraintsSocksPortTF.gridy = 6;
			constraintsSocksPortTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSocksPortTF.insets = new java.awt.Insets(0, 0, 0, 10);
			getContentsPane().add(getSocksPortTF(), constraintsSocksPortTF);

			java.awt.GridBagConstraints constraintsSocksTmpCB = new java.awt.GridBagConstraints();
			constraintsSocksTmpCB.gridx = 0; constraintsSocksTmpCB.gridy = 8;
			constraintsSocksTmpCB.gridwidth = 0;
			constraintsSocksTmpCB.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSocksTmpCB.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsSocksTmpCB.insets = new java.awt.Insets(10, 10, 0, 10);
			getContentsPane().add(getSocksTmpCB(), constraintsSocksTmpCB);

			java.awt.GridBagConstraints constraintsPanel2 = new java.awt.GridBagConstraints();
			constraintsPanel2.gridx = 0; constraintsPanel2.gridy = 9;
			constraintsPanel2.gridwidth = 0;
			constraintsPanel2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPanel2.weightx = 1.0;
			constraintsPanel2.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentsPane().add(getPanel2(), constraintsPanel2);

			java.awt.GridBagConstraints constraintsPanel1 = new java.awt.GridBagConstraints();
			constraintsPanel1.gridx = 0; constraintsPanel1.gridy = 4;
			constraintsPanel1.gridwidth = 0;
			constraintsPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPanel1.insets = new java.awt.Insets(0, 20, 0, 10);
			getContentsPane().add(getPanel1(), constraintsPanel1);

			java.awt.GridBagConstraints constraintsSocksTA = new java.awt.GridBagConstraints();
			constraintsSocksTA.gridx = 0; constraintsSocksTA.gridy = 7;
			constraintsSocksTA.gridwidth = 0;
			constraintsSocksTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSocksTA.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsSocksTA.weighty = 1.0;
			constraintsSocksTA.insets = new java.awt.Insets(10, 10, 0, 10);
			getContentsPane().add(getSocksTA(), constraintsSocksTA);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContentsPane;
}
/**
 * Return the ContentsPane1 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getContentsPane1() {
	if (ivjContentsPane1 == null) {
		try {
			ivjContentsPane1 = new java.awt.Panel();
			ivjContentsPane1.setName("ContentsPane1");
			ivjContentsPane1.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane1.setBackground(java.awt.SystemColor.window);
			java.awt.GridBagConstraints constraintsErrorLbl = new java.awt.GridBagConstraints();
			constraintsErrorLbl.gridx = 0; constraintsErrorLbl.gridy = 0;
			constraintsErrorLbl.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane1().add(getErrorLbl(), constraintsErrorLbl);

			java.awt.GridBagConstraints constraintsErrorOkBtn = new java.awt.GridBagConstraints();
			constraintsErrorOkBtn.gridx = 0; constraintsErrorOkBtn.gridy = 1;
			constraintsErrorOkBtn.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane1().add(getErrorOkBtn(), constraintsErrorOkBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContentsPane1;
}
/**
 * Return the DirectCB property value.
 * @return java.awt.Checkbox
 */
private java.awt.Checkbox getDirectCB() {
	if (ivjDirectCB == null) {
		try {
			ivjDirectCB = new java.awt.Checkbox();
			ivjDirectCB.setName("DirectCB");
			ivjDirectCB.setLabel("Direct Connection to the Internet");
			ivjDirectCB.setState(false);
			ivjDirectCB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						directChanged(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDirectCB;
}
/**
 * This method initializes dialog
 * 
 * @return java.awt.Dialog
 */
private java.awt.Dialog getErrorDlg() {
	if (ivjErrorDlg == null) {
		ivjErrorDlg = new java.awt.Dialog((Frame) getParent());
		ivjErrorDlg.add(getContentsPane1(), java.awt.BorderLayout.CENTER);
		ivjErrorDlg.setSize(221, 132);
		ivjErrorDlg.setName("ErrorDlg");
		ivjErrorDlg.setTitle("Error");
		ivjErrorDlg.setResizable(false);
		ivjErrorDlg.setModal(true);
	}
	return ivjErrorDlg;
}
/**
 * Return the ErrorLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getErrorLbl() {
	if (ivjErrorLbl == null) {
		try {
			ivjErrorLbl = new java.awt.Label();
			ivjErrorLbl.setName("ErrorLbl");
			ivjErrorLbl.setText("Unable to save file!");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjErrorLbl;
}
/**
 * Return the ErrorOkBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getErrorOkBtn() {
	if (ivjErrorOkBtn == null) {
		try {
			ivjErrorOkBtn = new java.awt.Button();
			ivjErrorOkBtn.setName("ErrorOkBtn");
			ivjErrorOkBtn.setLabel("OK");
			ivjErrorOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getErrorDlg().setVisible(false);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjErrorOkBtn;
}
/**
 * Return the Panel1 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getPanel1() {
	if (ivjPanel1 == null) {
		try {
			ivjPanel1 = new java.awt.Panel();
			ivjPanel1.setName("Panel1");
			ivjPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsAuthCB = new java.awt.GridBagConstraints();
			constraintsAuthCB.gridx = 0; constraintsAuthCB.gridy = 0;
			constraintsAuthCB.insets = new java.awt.Insets(0, 0, 0, 10);
			getPanel1().add(getAuthCB(), constraintsAuthCB);

			java.awt.GridBagConstraints constraintsUidLbl = new java.awt.GridBagConstraints();
			constraintsUidLbl.gridx = 1; constraintsUidLbl.gridy = 0;
			getPanel1().add(getUidLbl(), constraintsUidLbl);

			java.awt.GridBagConstraints constraintsUidTF = new java.awt.GridBagConstraints();
			constraintsUidTF.gridx = 2; constraintsUidTF.gridy = 0;
			constraintsUidTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUidTF.weightx = 1.0;
			getPanel1().add(getUidTF(), constraintsUidTF);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPanel1;
}
/**
 * Return the Panel2 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getPanel2() {
	if (ivjPanel2 == null) {
		try {
			ivjPanel2 = new java.awt.Panel();
			ivjPanel2.setName("Panel2");
			ivjPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSocksOkBtn = new java.awt.GridBagConstraints();
			constraintsSocksOkBtn.gridx = 0; constraintsSocksOkBtn.gridy = 0;
			constraintsSocksOkBtn.insets = new java.awt.Insets(0, 0, 0, 10);
			getPanel2().add(getSocksOkBtn(), constraintsSocksOkBtn);

			java.awt.GridBagConstraints constraintsSocksCanBtn = new java.awt.GridBagConstraints();
			constraintsSocksCanBtn.gridx = 1; constraintsSocksCanBtn.gridy = 0;
			constraintsSocksCanBtn.weighty = 1.0;
			getPanel2().add(getSocksCanBtn(), constraintsSocksCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPanel2;
}
/**
 * Return the PortLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getPortLbl() {
	if (ivjPortLbl == null) {
		try {
			ivjPortLbl = new java.awt.Label();
			ivjPortLbl.setName("PortLbl");
			ivjPortLbl.setText("Port:");
			ivjPortLbl.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPortLbl;
}
/**
 * Return the PortLbl1 property value.
 * @return java.awt.Label
 */
private java.awt.Label getPortLbl1() {
	if (ivjPortLbl1 == null) {
		try {
			ivjPortLbl1 = new java.awt.Label();
			ivjPortLbl1.setName("PortLbl1");
			ivjPortLbl1.setText("Port:");
			ivjPortLbl1.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPortLbl1;
}
/**
 * Return the ProxyCB property value.
 * @return java.awt.Checkbox
 */
private java.awt.Checkbox getProxyCB() {
	if (ivjProxyCB == null) {
		try {
			ivjProxyCB = new java.awt.Checkbox();
			ivjProxyCB.setName("ProxyCB");
			ivjProxyCB.setLabel("Proxy Connection to the Internet");
			ivjProxyCB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						proxyChanged(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProxyCB;
}
/**
 * Return the ProxyPortTF property value.
 * @return java.awt.TextField
 */
private java.awt.TextField getProxyPortTF() {
	if (ivjProxyPortTF == null) {
		try {
			ivjProxyPortTF = new java.awt.TextField();
			ivjProxyPortTF.setName("ProxyPortTF");
			ivjProxyPortTF.setEnabled(false);
			ivjProxyPortTF.setColumns(4);
			ivjProxyPortTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						try {
							setSocks(e);
						} catch (Exception exc) {
							displayError();
						}
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
			ivjProxyPortTF.addTextListener(new java.awt.event.TextListener() { 
				public void textValueChanged(java.awt.event.TextEvent e) {    
					try {
						textChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProxyPortTF;
}
/**
 * Return the ProxyServerTF property value.
 * @return java.awt.TextField
 */
private java.awt.TextField getProxyServerTF() {
	if (ivjProxyServerTF == null) {
		try {
			ivjProxyServerTF = new java.awt.TextField();
			ivjProxyServerTF.setName("ProxyServerTF");
			ivjProxyServerTF.setEnabled(false);
			ivjProxyServerTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getProxyPortTF().requestFocus();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
			ivjProxyServerTF.addTextListener(new java.awt.event.TextListener() { 
				public void textValueChanged(java.awt.event.TextEvent e) {    
					try {
						textChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProxyServerTF;
}
/**
 * Return the ServerLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getServerLbl() {
	if (ivjServerLbl == null) {
		try {
			ivjServerLbl = new java.awt.Label();
			ivjServerLbl.setName("ServerLbl");
			ivjServerLbl.setText("Server:");
			ivjServerLbl.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjServerLbl;
}
/**
 * Return the ServerLbl1 property value.
 * @return java.awt.Label
 */
private java.awt.Label getServerLbl1() {
	if (ivjServerLbl1 == null) {
		try {
			ivjServerLbl1 = new java.awt.Label();
			ivjServerLbl1.setName("ServerLbl1");
			ivjServerLbl1.setText("Server:");
			ivjServerLbl1.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjServerLbl1;
}
/**
 * Return the SocksCanBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getSocksCanBtn() {
	if (ivjSocksCanBtn == null) {
		try {
			ivjSocksCanBtn = new java.awt.Button();
			ivjSocksCanBtn.setName("SocksCanBtn");
			ivjSocksCanBtn.setLabel("Cancel");
			ivjSocksCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSocksCanBtn;
}
/**
 * Return the SocksCB property value.
 * @return java.awt.Checkbox
 */
private java.awt.Checkbox getSocksCB() {
	if (ivjSocksCB == null) {
		try {
			ivjSocksCB = new java.awt.Checkbox();
			ivjSocksCB.setName("SocksCB");
			ivjSocksCB.setLabel("Socks Connection to the Internet");
			ivjSocksCB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						socksChanged(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSocksCB;
}
/**
 * Return the SocksCBG property value.
 * @return java.awt.CheckboxGroup
 */
private java.awt.CheckboxGroup getSocksCBG() {
	if (ivjSocksCBG == null) {
		try {
			ivjSocksCBG = new java.awt.CheckboxGroup();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSocksCBG;
}
/**
 * Return the SocksIntroLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getSocksIntroLbl() {
	if (ivjSocksIntroLbl == null) {
		try {
			ivjSocksIntroLbl = new java.awt.Label();
			ivjSocksIntroLbl.setName("SocksIntroLbl");
			ivjSocksIntroLbl.setText("Please specify your Internet connectivity:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSocksIntroLbl;
}
/**
 * Return the SocksOkBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getSocksOkBtn() {
	if (ivjSocksOkBtn == null) {
		try {
			ivjSocksOkBtn = new java.awt.Button();
			ivjSocksOkBtn.setName("SocksOkBtn");
			ivjSocksOkBtn.setEnabled(false);
			ivjSocksOkBtn.setLabel("Ok");
			ivjSocksOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						try {
							setSocks(e);
						} catch (Exception exc) {
							displayError();
						}
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSocksOkBtn;
}
/**
 * Return the SocksPortTF property value.
 * @return java.awt.TextField
 */
private java.awt.TextField getSocksPortTF() {
	if (ivjSocksPortTF == null) {
		try {
			ivjSocksPortTF = new java.awt.TextField();
			ivjSocksPortTF.setName("SocksPortTF");
			ivjSocksPortTF.setEnabled(false);
			ivjSocksPortTF.setColumns(4);
			ivjSocksPortTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						try {
							setSocks(e);
						} catch (Exception exc) {
							displayError();
						}
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
			ivjSocksPortTF.addTextListener(new java.awt.event.TextListener() { 
				public void textValueChanged(java.awt.event.TextEvent e) {    
					try {
						textChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSocksPortTF;
}
/**
 * Return the SocksServerTF property value.
 * @return java.awt.TextField
 */
private java.awt.TextField getSocksServerTF() {
	if (ivjSocksServerTF == null) {
		try {
			ivjSocksServerTF = new java.awt.TextField();
			ivjSocksServerTF.setName("SocksServerTF");
			ivjSocksServerTF.setEnabled(false);
			ivjSocksServerTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getSocksPortTF().requestFocus();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
			ivjSocksServerTF.addTextListener(new java.awt.event.TextListener() { 
				public void textValueChanged(java.awt.event.TextEvent e) {    
					try {
						textChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSocksServerTF;
}
/**
 * Return the SocksTA property value.
 * @return java.awt.TextArea
 */
private java.awt.TextArea getSocksTA() {
	if (ivjSocksTA == null) {
		try {
			ivjSocksTA = new java.awt.TextArea("", 0, 0, java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
			ivjSocksTA.setName("SocksTA");
			ivjSocksTA.setText("Many companies use firewalls to protect their networks from the Internet. A Socks server enables you to securely access the Internet over the firewall. If your company uses a Socks server, select the Socks Connection button and enter the server address and port in the areas provided. A Proxy server also enables you to securely access the Internet over the firewall. If your company uses a Proxy server, select the Proxy Connection button and enter the server address and port in the areas provided. You may need to contact your network administrator for assistance. If no firewall is used, select the Direct Connection button.");
			ivjSocksTA.setBackground(java.awt.SystemColor.window);
			ivjSocksTA.setForeground(java.awt.SystemColor.windowText);
			ivjSocksTA.setEditable(false);
			ivjSocksTA.setEnabled(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSocksTA;
}
/**
 * Return the SocksTmpCB property value.
 * @return java.awt.Checkbox
 */
private java.awt.Checkbox getSocksTmpCB() {
	if (ivjSocksTmpCB == null) {
		try {
			ivjSocksTmpCB = new java.awt.Checkbox();
			ivjSocksTmpCB.setName("SocksTmpCB");
			ivjSocksTmpCB.setLabel("Change settings for this session only.");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSocksTmpCB;
}
/**
 * Return the UidLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getUidLbl() {
	if (ivjUidLbl == null) {
		try {
			ivjUidLbl = new java.awt.Label();
			ivjUidLbl.setName("UidLbl");
			ivjUidLbl.setText("User ID:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUidLbl;
}
/**
 * Return the UidTF property value.
 * @return java.awt.TextField
 */
private java.awt.TextField getUidTF() {
	if (ivjUidTF == null) {
		try {
			ivjUidTF = new java.awt.TextField();
			ivjUidTF.setName("UidTF");
			ivjUidTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						try {
							setSocks(e);
						} catch (Exception exc) {
							displayError();
						}
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
			ivjUidTF.addTextListener(new java.awt.event.TextListener() { 
				public void textValueChanged(java.awt.event.TextEvent e) {    
					try {
						textChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUidTF;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ConfigMgr");
		setLayout(new java.awt.BorderLayout());
		setSize(373, 389);
		setModal(true);
		setTitle("Connectivity");
		add(getContentsPane(), "Center");

		getDirectCB().setCheckboxGroup(getSocksCBG());
		getProxyCB().setCheckboxGroup(getSocksCBG());
		getSocksCB().setCheckboxGroup(getSocksCBG());

		// Adjust the GUI depending on whether we are using this for the install...
		if (install) {
			getSocksTmpCB().setVisible(false);
		}
		else {
			getSocksTA().setVisible(false);
			GridBagLayout lm = (GridBagLayout) getContentsPane().getLayout();
			GridBagConstraints c = lm.getConstraints(getSocksTmpCB());
			c.weighty = 1.0;
			lm.setConstraints(getSocksTmpCB(),c);
		}

		// Set up the socks dialog.
		getSocksTmpCB().setState(false);
		int connType = cfg.getIntProperty("ODCCONNTYPE",0);
		String socksServerHost = cfg.getProperty("ODCSOCKSSERVER",null);
		String socksServerPort = cfg.getProperty("ODCSOCKSPORT",null);
		String proxyServerHost = cfg.getProperty("ODCPROXYSERVER",null);
		String proxyServerPort = cfg.getProperty("ODCPROXYPORT",null);
		boolean proxyAuth = cfg.getBoolProperty("ODCPROXYAUTH",false);
		String proxyId = cfg.getProperty("ODCPROXYID",null);

		if (socksServerHost != null && socksServerPort != null) {
			getSocksServerTF().setText(socksServerHost);
			getSocksPortTF().setText(socksServerPort);
		}
		if (proxyServerHost != null && proxyServerPort != null) {
			getProxyServerTF().setText(proxyServerHost);
			getProxyPortTF().setText(proxyServerPort);
		}
		getAuthCB().setState(proxyAuth);
		if (proxyId != null)
			getUidTF().setText(proxyId);

		if (connType == 2) {
			getDirectCB().setState(false);
			getProxyCB().setState(false);
			getSocksCB().setState(true);
			getServerLbl1().setEnabled(false);
			getProxyServerTF().setEnabled(false);
			getPortLbl1().setEnabled(false);
			getProxyPortTF().setEnabled(false);
			getAuthCB().setEnabled(false);
			getUidLbl().setEnabled(false);
			getUidTF().setEnabled(false);
			getServerLbl().setEnabled(true);
			getSocksServerTF().setEnabled(true);
			getPortLbl().setEnabled(true);
			getSocksPortTF().setEnabled(true);

			textChg();
		}
		else if (connType == 1) {
			getSocksCB().setState(false);
			getDirectCB().setState(false);
			getProxyCB().setState(true);
			getServerLbl1().setEnabled(true);
			getProxyServerTF().setEnabled(true);
			getPortLbl1().setEnabled(true);
			getProxyPortTF().setEnabled(true);
			getAuthCB().setEnabled(true);
			getUidLbl().setEnabled(proxyAuth);
			getUidTF().setEnabled(proxyAuth);
			getServerLbl().setEnabled(false);
			getSocksServerTF().setEnabled(false);
			getPortLbl().setEnabled(false);
			getSocksPortTF().setEnabled(false);

			textChg();
		}
		else {
			getSocksCB().setState(false);
			getProxyCB().setState(false);
			getDirectCB().setState(true);
			getServerLbl1().setEnabled(false);
			getProxyServerTF().setEnabled(false);
			getPortLbl1().setEnabled(false);
			getProxyPortTF().setEnabled(false);
			getAuthCB().setEnabled(false);
			getUidLbl().setEnabled(false);
			getUidTF().setEnabled(false);
			getServerLbl().setEnabled(false);
			getSocksServerTF().setEnabled(false);
			getPortLbl().setEnabled(false);
			getSocksPortTF().setEnabled(false);

			getSocksOkBtn().setEnabled(true);
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		ConfigMgrConnDlg aConfigMgrConnDlg = new oem.edge.ed.odc.applet.ConfigMgrConnDlg(new java.awt.Frame());
		aConfigMgrConnDlg.setModal(true);
		aConfigMgrConnDlg.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aConfigMgrConnDlg.show();
		java.awt.Insets insets = aConfigMgrConnDlg.getInsets();
		aConfigMgrConnDlg.setSize(aConfigMgrConnDlg.getWidth() + insets.left + insets.right, aConfigMgrConnDlg.getHeight() + insets.top + insets.bottom);
		aConfigMgrConnDlg.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Dialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void proxyChanged(ItemEvent e) {
	if (e.getStateChange() == ItemEvent.SELECTED) {
		getServerLbl1().setEnabled(true);
		getProxyServerTF().setEnabled(true);
		getPortLbl1().setEnabled(true);
		getProxyPortTF().setEnabled(true);
		getAuthCB().setEnabled(true);
		getServerLbl().setEnabled(false);
		getSocksServerTF().setEnabled(false);
		getPortLbl().setEnabled(false);
		getSocksPortTF().setEnabled(false);

		if (getAuthCB().getState()) {
			getUidLbl().setEnabled(true);
			getUidTF().setEnabled(true);
		}

		getProxyServerTF().requestFocus();
		textChg();
	}

	return;
}
/**
 * Comment
 */
public void setSocks(ActionEvent e) throws Exception {
	// If ok button pushed, or text field enter pushed && ok button enabled,
	// then change the settings and possibly save them. Otherwise, ignore the
	// text field enter.
	if (e.getSource() == getSocksOkBtn() || getSocksOkBtn().isEnabled()) {
		// Hide the dialog.
		okPressed = true;

		// Save any dialog option settings in the config object..
		cfg.setProperty("ODCPROXYSERVER",getProxyServerTF().getText());
		cfg.setProperty("ODCPROXYPORT",getProxyPortTF().getText());
		cfg.setProperty("ODCSOCKSSERVER",getSocksServerTF().getText());
		cfg.setProperty("ODCSOCKSPORT",getSocksPortTF().getText());
		cfg.setBoolProperty("ODCPROXYAUTH",getAuthCB().getState());
		String uid = getUidTF().getText().trim();

		if (uid.length() != 0)
			cfg.setProperty("ODCPROXYID",uid);
		else
			cfg.removeProperty("ODCPROXYID");

		if (getDirectCB().getState())
			cfg.setIntProperty("ODCCONNTYPE",0);
		else if (getProxyCB().getState())
			cfg.setIntProperty("ODCCONNTYPE",1);
		else
			cfg.setIntProperty("ODCCONNTYPE",2);

		// Stuff to do if not installing.
		if (! install) {
			// Get the system properties.
			Properties p = System.getProperties();

			// Set up the system properties correctly.
			if (getDirectCB().getState()) {
				p.remove("socksProxySet");
				p.remove("socksProxyHost");
				p.remove("socksProxyPort");
				p.remove("proxySet");
				p.remove("proxyHost");
				p.remove("proxyPort");
				p.remove("proxyAuth");
			}
			else if (getProxyCB().getState()) {
				p.remove("socksProxySet");
				p.remove("socksProxyHost");
				p.remove("socksProxyPort");
				p.put("proxySet","true");
				p.put("proxyHost",getProxyServerTF().getText());
				p.put("proxyPort",getProxyPortTF().getText());
			}
			else {
				p.put("socksProxySet","true");
				p.put("socksProxyHost",getSocksServerTF().getText());
				p.put("socksProxyPort",getSocksPortTF().getText());
				p.remove("proxySet");
				p.remove("proxyHost");
				p.remove("proxyPort");
				p.remove("proxyAuth");
			}

			// Store the system properties.
			System.setProperties(p);

			// Get the password, if needed.
			if (getProxyCB().getState() && getAuthCB().getState()) {
				ConfigMgrAuthDlg auth = new ConfigMgrAuthDlg((Frame) getParent(),cfg);
				auth.promptAuth();
			}

			// Save the ini file if requested.
			if (! getSocksTmpCB().getState()) {
				FileOutputStream f = new FileOutputStream("edesign.ini");
				cfg.save(f);
				f.close();
			}
		}

		dispose();
	}

	return;
}
/**
 * Comment
 */
public void socksChanged(ItemEvent e) {
	if (e.getStateChange() == ItemEvent.SELECTED) {
		getServerLbl1().setEnabled(false);
		getProxyServerTF().setEnabled(false);
		getPortLbl1().setEnabled(false);
		getProxyPortTF().setEnabled(false);
		getAuthCB().setEnabled(false);
		getUidLbl().setEnabled(false);
		getUidTF().setEnabled(false);
		getServerLbl().setEnabled(true);
		getPortLbl().setEnabled(true);
		getSocksServerTF().setEnabled(true);
		getSocksPortTF().setEnabled(true);

		getSocksServerTF().requestFocus();
		textChg();
	}

	return;
}
/**
 * Comment
 */
public void textChg() {
	String server;
	String port;

	if (getDirectCB().getState()) {
		return;
	}
	else if (getProxyCB().getState()) {
		server = getProxyServerTF().getText();
		port = getProxyPortTF().getText();
	}
	else {
		server = getSocksServerTF().getText();
		port = getSocksPortTF().getText();
	}

	if (server.trim().length() != 0 &&
		port.trim().length() != 0 &&
		Integer.parseInt(port) > 0)
		getSocksOkBtn().setEnabled(true);
	else
		getSocksOkBtn().setEnabled(false);

	return;
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
