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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import oem.edge.ed.odc.applet.ConfigFile;

/**
 * Insert the type's description here.
 * Creation date: (5/28/2002 7:42:21 PM)
 * @author: Mike Zarnick
 */
public class ConfigMgrAuthDlg extends JDialog {
	private ConfigFile cfg = null;
	private JButton ivjCanBtn = null;
	private JPanel ivjContentsPane = null;
	private JLabel ivjLabel1 = null;
	private JLabel ivjLabel2 = null;
	private JLabel ivjLabel3 = null;
	private JButton ivjOkBtn = null;
	private JPanel ivjPanel1 = null;
	private JPasswordField ivjPwdTF = null;
	private JTextField ivjUidTF = null;
/**
 * ConfigMgrAuthDlg constructor comment.
 * @param owner java.awt.Frame
 */
public ConfigMgrAuthDlg(Frame owner) {
	super(owner);
	initialize();
}
/**
 * ConfigMgrAuthDlg constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public ConfigMgrAuthDlg(Frame owner, String title) {
	super(owner, title);
}
/**
 * ConfigMgrAuthDlg constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public ConfigMgrAuthDlg(Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ConfigMgrAuthDlg constructor comment.
 * @param owner java.awt.Frame
 */
public ConfigMgrAuthDlg(Frame owner, ConfigFile cfg) {
	super(owner);
	this.cfg = cfg;
	setModal(true);
	initialize();
}
/**
 * ConfigMgrAuthDlg constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public ConfigMgrAuthDlg(Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2002 8:48:12 PM)
 * @param e java.awt.event.ActionEvent
 */
public void canAuth() {
	Properties p = System.getProperties();
	p.remove("proxyAuth");
	System.setProperties(p);

	dispose();

	return;
}
/**
 * Return the CanBtn property value.
 * @return java.awt.Button
 */
private JButton getCanBtn() {
	if (ivjCanBtn == null) {
		try {
			ivjCanBtn = new JButton();
			ivjCanBtn.setName("CanBtn");
			ivjCanBtn.setText("Cancel");
			ivjCanBtn.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjCanBtn;
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
			ivjContentsPane.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsLabel1 = new java.awt.GridBagConstraints();
			constraintsLabel1.gridx = 0; constraintsLabel1.gridy = 0;
			constraintsLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLabel1.insets = new java.awt.Insets(10, 10, 5, 0);
			getContentsPane().add(getLabel1(), constraintsLabel1);

			java.awt.GridBagConstraints constraintsLabel2 = new java.awt.GridBagConstraints();
			constraintsLabel2.gridx = 0; constraintsLabel2.gridy = 1;
			constraintsLabel2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLabel2.insets = new java.awt.Insets(0, 10, 10, 0);
			getContentsPane().add(getLabel2(), constraintsLabel2);

			java.awt.GridBagConstraints constraintsUidTF = new java.awt.GridBagConstraints();
			constraintsUidTF.gridx = 1; constraintsUidTF.gridy = 0;
			constraintsUidTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUidTF.weightx = 1.0;
			constraintsUidTF.insets = new java.awt.Insets(10, 0, 5, 10);
			getContentsPane().add(getUidTF(), constraintsUidTF);

			java.awt.GridBagConstraints constraintsPwdTF = new java.awt.GridBagConstraints();
			constraintsPwdTF.gridx = 1; constraintsPwdTF.gridy = 1;
			constraintsPwdTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPwdTF.weightx = 1.0;
			constraintsPwdTF.insets = new java.awt.Insets(0, 0, 10, 10);
			getContentsPane().add(getPwdTF(), constraintsPwdTF);

			java.awt.GridBagConstraints constraintsLabel3 = new java.awt.GridBagConstraints();
			constraintsLabel3.gridx = 0; constraintsLabel3.gridy = 2;
			constraintsLabel3.gridwidth = 0;
			constraintsLabel3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLabel3.insets = new java.awt.Insets(0, 20, 0, 0);
			getContentsPane().add(getLabel3(), constraintsLabel3);

			java.awt.GridBagConstraints constraintsPanel1 = new java.awt.GridBagConstraints();
			constraintsPanel1.gridx = 0; constraintsPanel1.gridy = 3;
			constraintsPanel1.gridwidth = 0;
			constraintsPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPanel1.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsPanel1.weighty = 1.0;
			constraintsPanel1.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentsPane().add(getPanel1(), constraintsPanel1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContentsPane;
}
/**
 * Return the Label1 property value.
 * @return java.awt.Label
 */
private JLabel getLabel1() {
	if (ivjLabel1 == null) {
		try {
			ivjLabel1 = new JLabel();
			ivjLabel1.setName("Label1");
			ivjLabel1.setText("User ID:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLabel1;
}
/**
 * Return the Label2 property value.
 * @return java.awt.Label
 */
private JLabel getLabel2() {
	if (ivjLabel2 == null) {
		try {
			ivjLabel2 = new JLabel();
			ivjLabel2.setName("Label2");
			ivjLabel2.setText("Password:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLabel2;
}
/**
 * Return the Label3 property value.
 * @return java.awt.Label
 */
private JLabel getLabel3() {
	if (ivjLabel3 == null) {
		try {
			ivjLabel3 = new JLabel();
			ivjLabel3.setName("Label3");
			ivjLabel3.setText("Select Cancel to skip authentication.");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLabel3;
}
/**
 * Return the OkBtn property value.
 * @return java.awt.Button
 */
private JButton getOkBtn() {
	if (ivjOkBtn == null) {
		try {
			ivjOkBtn = new JButton();
			ivjOkBtn.setName("OkBtn");
			ivjOkBtn.setEnabled(false);
			ivjOkBtn.setText("OK");
			ivjOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setAuth(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOkBtn;
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
			ivjPanel1.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsOkBtn = new java.awt.GridBagConstraints();
			constraintsOkBtn.gridx = 0; constraintsOkBtn.gridy = 0;
			constraintsOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getPanel1().add(getOkBtn(), constraintsOkBtn);

			java.awt.GridBagConstraints constraintsCanBtn = new java.awt.GridBagConstraints();
			constraintsCanBtn.gridx = 1; constraintsCanBtn.gridy = 0;
			constraintsCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getPanel1().add(getCanBtn(), constraintsCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPanel1;
}
/**
 * Return the PwdTF property value.
 * @return java.awt.TextField
 */
private JPasswordField getPwdTF() {
	if (ivjPwdTF == null) {
		try {
			ivjPwdTF = new JPasswordField();
			ivjPwdTF.setName("PwdTF");
			ivjPwdTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setAuth(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
			ivjPwdTF.getDocument().addDocumentListener(new DocumentListener() { 
				public void changedUpdate(DocumentEvent e) {    
					try {
						textChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
				public void insertUpdate(DocumentEvent e) {    
					try {
						textChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
				public void removeUpdate(DocumentEvent e) {    
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
	return ivjPwdTF;
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
			ivjUidTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getPwdTF().requestFocus();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
			ivjUidTF.getDocument().addDocumentListener(new DocumentListener() { 
				public void changedUpdate(DocumentEvent e) {    
					try {
						textChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
				public void insertUpdate(DocumentEvent e) {    
					try {
						textChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
				public void removeUpdate(DocumentEvent e) {    
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
		setName("ConfigMgrAuthDlg");
		setLayout(new java.awt.BorderLayout());
		setSize(260, 196);
		setModal(true);
		setTitle("Http Proxy Authentication");
		add(getContentsPane(), "Center");
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowOpened(java.awt.event.WindowEvent e) {    
				try {
					if (getUidTF().getText().trim().length() != 0) {
						getPwdTF().requestFocus();
					}
					else {
						getUidTF().requestFocus();
					}
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
		});

		String proxyId = cfg.getProperty("ODCPROXYID",null);

		if (proxyId != null) {
			getUidTF().setText(proxyId);
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void promptAuth() {
	// Center the window
	Point winPos = getParent().getLocation();
	Dimension winSize = getParent().getSize();
	Dimension dlgSize = getSize();
	setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	setVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2002 8:48:12 PM)
 * @param e java.awt.event.ActionEvent
 */
public void setAuth(ActionEvent e) {
	// If ok button pushed, or text field enter pushed && ok button enabled,
	// then change encode the ID and PWD, and store in the system property
	// proxyAuth. Otherwise, ignore the text field enter.
	if (e.getSource() == getOkBtn() || getOkBtn().isEnabled()) {
		// Get the system properties.
		Properties p = System.getProperties();

		// Set the ID/PWD.
		String pwd = new String(getPwdTF().getPassword());
		String idpwd = getUidTF().getText() + ":" + pwd;
		String base64 = oem.edge.ed.util.Base64.encode(idpwd.getBytes());
		p.put("proxyAuth",base64);

		// Store the system properties.
		System.setProperties(p);
		dispose();
		cfg.setProperty("ODCPROXYID",getUidTF().getText());
	}

	return;
}
/**
 * Comment
 */
public void textChg() {
	String pwd = new String(getPwdTF().getPassword());

	if (getUidTF().getText().trim().length() != 0 &&
		pwd.trim().length() != 0)
		getOkBtn().setEnabled(true);
	else
		getOkBtn().setEnabled(false);

	return;
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
