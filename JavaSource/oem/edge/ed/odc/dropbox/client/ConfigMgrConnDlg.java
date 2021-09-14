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

/**
 * Insert the type's description here.
 * Creation date: (5/7/2002 9:48:41 AM)
 * @author: Mike Zarnick
 */

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import oem.edge.ed.odc.applet.ConfigFile;

public class ConfigMgrConnDlg extends JDialog {
	private JPanel ivjContentsPane = null;
	private JRadioButton ivjDirectCB = null;
	private JPanel ivjPanel2 = null;
	private JLabel ivjPortLbl = null;
	private JLabel ivjPortLbl1 = null;
	private JRadioButton ivjProxyCB = null;
	private JTextField ivjProxyPortTF = null;
	private JTextField ivjProxyServerTF = null;
	private JLabel ivjServerLbl = null;
	private JLabel ivjServerLbl1 = null;
	private JButton ivjSocksCanBtn = null;
	private JRadioButton ivjSocksCB = null;
	private JLabel ivjSocksIntroLbl = null;
	private JButton ivjSocksOkBtn = null;
	private JTextField ivjSocksPortTF = null;
	private JTextField ivjSocksServerTF = null;
	private JCheckBox ivjSocksTmpCB = null;
	private ConfigFile cfg = new ConfigFile();
	private ButtonGroup ivjSocksCBG = null;  // @jve:visual-info  decl-index=0 visual-constraint="431,20"
	private JPanel ivjContentsPane1 = null;
	private JDialog ivjErrorDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="20,473"
	private JLabel ivjErrorLbl = null;
	private JButton ivjErrorOkBtn = null;
	private JCheckBox ivjAuthCB = null;
	private JPanel ivjPanel1 = null;
	private JLabel ivjUidLbl = null;
	private JTextField ivjUidTF = null;
	
/**
 * Event Listener
 */
private class EventsListener implements ActionListener, ItemListener, DocumentListener {
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == getProxyPortTF()) {
				setSocks(e);
			}
			else if (e.getSource() == getProxyServerTF()) {
				getProxyPortTF().requestFocus();
			}
			else if (e.getSource() == getSocksPortTF()) {
				setSocks(e);
			}
			else if (e.getSource() == getSocksServerTF()) {
				getSocksPortTF().requestFocus();
			}
			else if (e.getSource() == getUidTF()) {
				setSocks(e);
			}
			else if (e.getSource() == getSocksOkBtn()) {
				setSocks(e);
			}
			else if (e.getSource() == getSocksCanBtn()) {
				dispose();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void itemStateChanged(ItemEvent e) {
		try {
			if (e.getSource() == getDirectCB()) {
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
			}
			else if (e.getSource() == getProxyCB()) {
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
	
					if (getAuthCB().isSelected()) {
						getUidLbl().setEnabled(true);
						getUidTF().setEnabled(true);
					}
	
					getProxyServerTF().requestFocus();
					textChg();
				}
			}
			else if (e.getSource() == getSocksCB()) {
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
			}
			else if (e.getSource() == getAuthCB()) {
				boolean b = (e.getStateChange() == ItemEvent.SELECTED);
				
				getUidLbl().setEnabled(b);
				getUidTF().setEnabled(b);

				getUidTF().requestFocus();
				textChg();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void changedUpdate(DocumentEvent e) {
		textChg();
	}
	public void insertUpdate(DocumentEvent e) {
		textChg();
	}
	public void removeUpdate(DocumentEvent e) {
		textChg();
	}
	public void textChg() {
		try {
			String server;
			String port;
	
			if (getDirectCB().isSelected()) {
				return;
			}
			else if (getProxyCB().isSelected()) {
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
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
			try {
				dispose();

				// Save any dialog option settings in the config object..
				cfg.setProperty("ODCPROXYSERVER",getProxyServerTF().getText());
				cfg.setProperty("ODCPROXYPORT",getProxyPortTF().getText());
				cfg.setProperty("ODCSOCKSSERVER",getSocksServerTF().getText());
				cfg.setProperty("ODCSOCKSPORT",getSocksPortTF().getText());
				cfg.setBoolProperty("ODCPROXYAUTH",getAuthCB().isSelected());
				String uid = getUidTF().getText().trim();
	
				if (uid.length() != 0)
					cfg.setProperty("ODCPROXYID",uid);
				else
					cfg.removeProperty("ODCPROXYID");
	
				if (getDirectCB().isSelected())
					cfg.setIntProperty("ODCCONNTYPE",0);
				else if (getProxyCB().isSelected())
					cfg.setIntProperty("ODCCONNTYPE",1);
				else
					cfg.setIntProperty("ODCCONNTYPE",2);
	
				// Get the system properties.
				Properties p = System.getProperties();
	
				// Set up the system properties correctly.
				if (getDirectCB().isSelected()) {
					System.out.println("Using direct connection.");
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
				}
				else if (getProxyCB().isSelected()) {
					String proxyServerHost = getProxyServerTF().getText();
					String proxyServerPort = getProxyPortTF().getText();
					System.out.println("Using proxy server: " + proxyServerHost + ":" + proxyServerPort);
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
				}
				else {
					String socksServerHost = getSocksServerTF().getText();
					String socksServerPort = getSocksPortTF().getText();
					System.out.println("Using socks server: " + socksServerHost + ":" + socksServerPort);
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
				}

				// Store the system properties.
				System.setProperties(p);
	
				// Get the password, if needed.
				if (getProxyCB().isSelected() && getAuthCB().isSelected()) {
					ConfigMgrAuthDlg auth = new ConfigMgrAuthDlg((Frame) getParent(),cfg);
					auth.promptAuth();
				}
	
				// Save the ini file if requested.
				if (! getSocksTmpCB().isSelected()) {
					FileOutputStream f = new FileOutputStream("edesign.ini");
					cfg.save(f);
					f.close();
				}
			} catch (Exception e1) {
				displayError();
			}
		}

		return;
	}
}
private EventsListener eventsListener = new EventsListener();
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
private JCheckBox getAuthCB() {
	if (ivjAuthCB == null) {
		try {
			ivjAuthCB = new JCheckBox();
			ivjAuthCB.setName("AuthCB");
			ivjAuthCB.setText("Use authentication");
			ivjAuthCB.addItemListener(eventsListener);
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
private JPanel getContentsPane() {
	if (ivjContentsPane == null) {
		try {
			ivjContentsPane = new JPanel();
			ivjContentsPane.setName("ContentsPane");
			ivjContentsPane.setLayout(new java.awt.GridBagLayout());
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
			constraintsSocksTmpCB.weighty = 1.0D;
			ivjContentsPane.add(getSocksTmpCB(), constraintsSocksTmpCB);
			getContentsPane().add(getPanel1(), constraintsPanel1);
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
private JPanel getContentsPane1() {
	if (ivjContentsPane1 == null) {
		try {
			ivjContentsPane1 = new JPanel();
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
private JRadioButton getDirectCB() {
	if (ivjDirectCB == null) {
		try {
			ivjDirectCB = new JRadioButton();
			ivjDirectCB.setName("DirectCB");
			ivjDirectCB.setText("Direct Connection to the Internet");
			ivjDirectCB.setSelected(false);
			ivjDirectCB.addItemListener(eventsListener);
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
private JDialog getErrorDlg() {
	if (ivjErrorDlg == null) {
		ivjErrorDlg = new JDialog(this);
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
private JLabel getErrorLbl() {
	if (ivjErrorLbl == null) {
		try {
			ivjErrorLbl = new JLabel();
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
private JButton getErrorOkBtn() {
	if (ivjErrorOkBtn == null) {
		try {
			ivjErrorOkBtn = new JButton();
			ivjErrorOkBtn.setName("ErrorOkBtn");
			ivjErrorOkBtn.setText("OK");
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
private JPanel getPanel1() {
	if (ivjPanel1 == null) {
		try {
			ivjPanel1 = new JPanel();
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
private JPanel getPanel2() {
	if (ivjPanel2 == null) {
		try {
			ivjPanel2 = new JPanel();
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
private JLabel getPortLbl() {
	if (ivjPortLbl == null) {
		try {
			ivjPortLbl = new JLabel();
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
private JLabel getPortLbl1() {
	if (ivjPortLbl1 == null) {
		try {
			ivjPortLbl1 = new JLabel();
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
private JRadioButton getProxyCB() {
	if (ivjProxyCB == null) {
		try {
			ivjProxyCB = new JRadioButton();
			ivjProxyCB.setName("ProxyCB");
			ivjProxyCB.setText("Proxy Connection to the Internet");
			ivjProxyCB.addItemListener(eventsListener);
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
private JTextField getProxyPortTF() {
	if (ivjProxyPortTF == null) {
		try {
			ivjProxyPortTF = new JTextField();
			ivjProxyPortTF.setName("ProxyPortTF");
			ivjProxyPortTF.setEnabled(false);
			ivjProxyPortTF.setColumns(4);
			ivjProxyPortTF.addActionListener(eventsListener);
			ivjProxyPortTF.getDocument().addDocumentListener(eventsListener);
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
private JTextField getProxyServerTF() {
	if (ivjProxyServerTF == null) {
		try {
			ivjProxyServerTF = new JTextField();
			ivjProxyServerTF.setName("ProxyServerTF");
			ivjProxyServerTF.setEnabled(false);
			ivjProxyServerTF.addActionListener(eventsListener);
			ivjProxyServerTF.getDocument().addDocumentListener(eventsListener);
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
private JLabel getServerLbl() {
	if (ivjServerLbl == null) {
		try {
			ivjServerLbl = new JLabel();
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
private JLabel getServerLbl1() {
	if (ivjServerLbl1 == null) {
		try {
			ivjServerLbl1 = new JLabel();
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
private JButton getSocksCanBtn() {
	if (ivjSocksCanBtn == null) {
		try {
			ivjSocksCanBtn = new JButton();
			ivjSocksCanBtn.setName("SocksCanBtn");
			ivjSocksCanBtn.setText("Cancel");
			ivjSocksCanBtn.addActionListener(eventsListener);
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
private JRadioButton getSocksCB() {
	if (ivjSocksCB == null) {
		try {
			ivjSocksCB = new JRadioButton();
			ivjSocksCB.setName("SocksCB");
			ivjSocksCB.setText("Socks Connection to the Internet");
			ivjSocksCB.addItemListener(eventsListener);
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
private ButtonGroup getSocksCBG() {
	if (ivjSocksCBG == null) {
		try {
			ivjSocksCBG = new ButtonGroup();
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
private JLabel getSocksIntroLbl() {
	if (ivjSocksIntroLbl == null) {
		try {
			ivjSocksIntroLbl = new JLabel();
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
private JButton getSocksOkBtn() {
	if (ivjSocksOkBtn == null) {
		try {
			ivjSocksOkBtn = new JButton();
			ivjSocksOkBtn.setName("SocksOkBtn");
			ivjSocksOkBtn.setEnabled(false);
			ivjSocksOkBtn.setText("Ok");
			ivjSocksOkBtn.addActionListener(eventsListener);
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
private JTextField getSocksPortTF() {
	if (ivjSocksPortTF == null) {
		try {
			ivjSocksPortTF = new JTextField();
			ivjSocksPortTF.setName("SocksPortTF");
			ivjSocksPortTF.setEnabled(false);
			ivjSocksPortTF.setColumns(4);
			ivjSocksPortTF.addActionListener(eventsListener);
			ivjSocksPortTF.getDocument().addDocumentListener(eventsListener);
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
private JTextField getSocksServerTF() {
	if (ivjSocksServerTF == null) {
		try {
			ivjSocksServerTF = new JTextField();
			ivjSocksServerTF.setName("SocksServerTF");
			ivjSocksServerTF.setEnabled(false);
			ivjSocksServerTF.addActionListener(eventsListener);
			ivjSocksServerTF.getDocument().addDocumentListener(eventsListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSocksServerTF;
}
/**
 * Return the SocksTmpCB property value.
 * @return java.awt.Checkbox
 */
private JCheckBox getSocksTmpCB() {
	if (ivjSocksTmpCB == null) {
		try {
			ivjSocksTmpCB = new JCheckBox();
			ivjSocksTmpCB.setName("SocksTmpCB");
			ivjSocksTmpCB.setText("Change settings for this session only.");
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
private JLabel getUidLbl() {
	if (ivjUidLbl == null) {
		try {
			ivjUidLbl = new JLabel();
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
private JTextField getUidTF() {
	if (ivjUidTF == null) {
		try {
			ivjUidTF = new JTextField();
			ivjUidTF.setName("UidTF");
			ivjUidTF.addActionListener(eventsListener);
			ivjUidTF.getDocument().addDocumentListener(eventsListener);
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
		setSize(373, 389);
		setModal(true);
		setTitle("Connectivity");
		setContentPane(getContentsPane());

		getSocksCBG().add(getDirectCB());
		getSocksCBG().add(getProxyCB());
		getSocksCBG().add(getSocksCB());

		// Set up the socks dialog.
		getSocksTmpCB().setSelected(false);
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
		getAuthCB().setSelected(proxyAuth);
		if (proxyId != null) {
			getUidTF().setText(proxyId);
		}

		if (connType == 2) {
			getDirectCB().setSelected(false);
			getProxyCB().setSelected(false);
			getSocksCB().setSelected(true);
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

			//textChg();
		}
		else if (connType == 1) {
			getSocksCB().setSelected(false);
			getDirectCB().setSelected(false);
			getProxyCB().setSelected(true);
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

			//textChg();
		}
		else {
			getSocksCB().setSelected(false);
			getProxyCB().setSelected(false);
			getDirectCB().setSelected(true);
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
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
