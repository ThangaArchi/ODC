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
 * Creation date: (5/13/2002 4:43:51 PM)
 * @author: Mike Zarnick
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class ConfigMgrIcaDlg extends Dialog {
	private Button ivjCitrixBtn = null;
	private Button ivjCitrixCanBtn = null;
	private CheckboxGroup ivjCitrixCBG = null;  // @jve:visual-info  decl-index=0 visual-constraint="464,20"
	private Checkbox ivjCitrixIncCB = null;
	private Label ivjCitrixIntroLbl = null;
	private Label ivjCitrixLbl = null;
	private Checkbox ivjCitrixNatCB = null;
	private Button ivjCitrixOkBtn = null;
	private TextField ivjCitrixTF = null;
	private Panel ivjContentsPane = null;
	private FileDialog ivjFileDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="329,400"
	private Panel ivjPanel = null;
	private ConfigFile cfg = null;
	private Panel ivjContentsPane1 = null;
	private Dialog ivjErrorDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="21,297"
	private Label ivjErrorLbl = null;
	private Button ivjErrorOkBtn = null;
/**
 * ConfigMgrIcaDlg constructor comment.
 * @param owner java.awt.Frame
 */
public ConfigMgrIcaDlg(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * ConfigMgrIcaDlg constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public ConfigMgrIcaDlg(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * ConfigMgrIcaDlg constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public ConfigMgrIcaDlg(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ConfigMgrIcaDlg constructor comment.
 * @param owner java.awt.Frame
 */
public ConfigMgrIcaDlg(java.awt.Frame owner, ConfigFile cfg) {
	super(owner);
	this.cfg = cfg;
	initialize();
}
/**
 * ConfigMgrIcaDlg constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public ConfigMgrIcaDlg(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * Comment
 */
public void citrixTextChg() {
	String icaPath = getCitrixTF().getText();

	boolean enabled = false;

	if (icaPath.trim().length() != 0) {
		File f = new File(icaPath);
		enabled = f.exists();
	}

	getCitrixOkBtn().setEnabled(enabled);

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
 * Return the CitrixBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getCitrixBtn() {
	if (ivjCitrixBtn == null) {
		try {
			ivjCitrixBtn = new java.awt.Button();
			ivjCitrixBtn.setName("CitrixBtn");
			ivjCitrixBtn.setLabel("Browse");
			ivjCitrixBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						locateCitrix(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCitrixBtn;
}
/**
 * Return the CitrixCanBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getCitrixCanBtn() {
	if (ivjCitrixCanBtn == null) {
		try {
			ivjCitrixCanBtn = new java.awt.Button();
			ivjCitrixCanBtn.setName("CitrixCanBtn");
			ivjCitrixCanBtn.setLabel("Cancel");
			ivjCitrixCanBtn.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjCitrixCanBtn;
}
/**
 * Return the CitrixCBG property value.
 * @return java.awt.CheckboxGroup
 */
private java.awt.CheckboxGroup getCitrixCBG() {
	if (ivjCitrixCBG == null) {
		try {
			ivjCitrixCBG = new java.awt.CheckboxGroup();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCitrixCBG;
}
/**
 * Return the CitrixIncCB property value.
 * @return java.awt.Checkbox
 */
private java.awt.Checkbox getCitrixIncCB() {
	if (ivjCitrixIncCB == null) {
		try {
			ivjCitrixIncCB = new java.awt.Checkbox();
			ivjCitrixIncCB.setName("CitrixIncCB");
			ivjCitrixIncCB.setLabel("Use the included Citrix ICA Client");
			ivjCitrixIncCB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						includedChanged(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCitrixIncCB;
}
/**
 * Return the CitrixIntroLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getCitrixIntroLbl() {
	if (ivjCitrixIntroLbl == null) {
		try {
			ivjCitrixIntroLbl = new java.awt.Label();
			ivjCitrixIntroLbl.setName("CitrixIntroLbl");
			ivjCitrixIntroLbl.setText("Please specify your Citrix ICA Client preference:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCitrixIntroLbl;
}
/**
 * Return the CitrixLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getCitrixLbl() {
	if (ivjCitrixLbl == null) {
		try {
			ivjCitrixLbl = new java.awt.Label();
			ivjCitrixLbl.setName("CitrixLbl");
			ivjCitrixLbl.setText("Client Path:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCitrixLbl;
}
/**
 * Return the CitrixNatCB property value.
 * @return java.awt.Checkbox
 */
private java.awt.Checkbox getCitrixNatCB() {
	if (ivjCitrixNatCB == null) {
		try {
			ivjCitrixNatCB = new java.awt.Checkbox();
			ivjCitrixNatCB.setName("CitrixNatCB");
			ivjCitrixNatCB.setLabel("Use the following Citrix ICA Client:");
			ivjCitrixNatCB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						nativeChanged(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCitrixNatCB;
}
/**
 * Return the CitrixOkBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getCitrixOkBtn() {
	if (ivjCitrixOkBtn == null) {
		try {
			ivjCitrixOkBtn = new java.awt.Button();
			ivjCitrixOkBtn.setName("CitrixOkBtn");
			ivjCitrixOkBtn.setEnabled(false);
			ivjCitrixOkBtn.setLabel("Ok");
			ivjCitrixOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						try {
							setCitrix(e);
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
	return ivjCitrixOkBtn;
}
/**
 * Return the CitrixTF property value.
 * @return java.awt.TextField
 */
private java.awt.TextField getCitrixTF() {
	if (ivjCitrixTF == null) {
		try {
			ivjCitrixTF = new java.awt.TextField();
			ivjCitrixTF.setName("CitrixTF");
			ivjCitrixTF.addTextListener(new java.awt.event.TextListener() { 
				public void textValueChanged(java.awt.event.TextEvent e) {    
					try {
						citrixTextChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
			ivjCitrixTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						try {
							setCitrix(e);
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
	return ivjCitrixTF;
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

			java.awt.GridBagConstraints constraintsCitrixIntroLbl = new java.awt.GridBagConstraints();
			constraintsCitrixIntroLbl.gridx = 0; constraintsCitrixIntroLbl.gridy = 0;
			constraintsCitrixIntroLbl.gridwidth = 0;
			constraintsCitrixIntroLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCitrixIntroLbl.insets = new java.awt.Insets(10, 10, 0, 10);
			getContentsPane().add(getCitrixIntroLbl(), constraintsCitrixIntroLbl);

			java.awt.GridBagConstraints constraintsCitrixIncCB = new java.awt.GridBagConstraints();
			constraintsCitrixIncCB.gridx = 0; constraintsCitrixIncCB.gridy = 1;
			constraintsCitrixIncCB.gridwidth = 0;
			constraintsCitrixIncCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCitrixIncCB.insets = new java.awt.Insets(5, 10, 0, 10);
			getContentsPane().add(getCitrixIncCB(), constraintsCitrixIncCB);

			java.awt.GridBagConstraints constraintsCitrixNatCB = new java.awt.GridBagConstraints();
			constraintsCitrixNatCB.gridx = 0; constraintsCitrixNatCB.gridy = 2;
			constraintsCitrixNatCB.gridwidth = 0;
			constraintsCitrixNatCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCitrixNatCB.insets = new java.awt.Insets(0, 10, 0, 10);
			getContentsPane().add(getCitrixNatCB(), constraintsCitrixNatCB);

			java.awt.GridBagConstraints constraintsCitrixLbl = new java.awt.GridBagConstraints();
			constraintsCitrixLbl.gridx = 0; constraintsCitrixLbl.gridy = 3;
			constraintsCitrixLbl.insets = new java.awt.Insets(0, 20, 0, 0);
			getContentsPane().add(getCitrixLbl(), constraintsCitrixLbl);

			java.awt.GridBagConstraints constraintsCitrixTF = new java.awt.GridBagConstraints();
			constraintsCitrixTF.gridx = 1; constraintsCitrixTF.gridy = 3;
			constraintsCitrixTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCitrixTF.weightx = 1.0;
			constraintsCitrixTF.insets = new java.awt.Insets(0, 0, 0, 5);
			getContentsPane().add(getCitrixTF(), constraintsCitrixTF);

			java.awt.GridBagConstraints constraintsCitrixBtn = new java.awt.GridBagConstraints();
			constraintsCitrixBtn.gridx = 2; constraintsCitrixBtn.gridy = 3;
			constraintsCitrixBtn.insets = new java.awt.Insets(0, 0, 0, 10);
			getContentsPane().add(getCitrixBtn(), constraintsCitrixBtn);

			java.awt.GridBagConstraints constraintsPanel = new java.awt.GridBagConstraints();
			constraintsPanel.gridx = 0; constraintsPanel.gridy = 4;
			constraintsPanel.gridwidth = 0;
			constraintsPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPanel.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsPanel.weightx = 1.0;
			constraintsPanel.weighty = 1.0;
			constraintsPanel.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentsPane().add(getPanel(), constraintsPanel);
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
						getErrorDlg().dispose();
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
 * Return the FileDlg property value.
 * @return java.awt.FileDialog
 */
private java.awt.FileDialog getFileDlg() {
	if (ivjFileDlg == null) {
		try {
			ivjFileDlg = new java.awt.FileDialog(new java.awt.Frame());
			ivjFileDlg.setName("FileDlg");
			ivjFileDlg.setLayout(null);
			ivjFileDlg.setMode(java.awt.FileDialog.SAVE);
			ivjFileDlg.setFile("");
			ivjFileDlg.setTitle("Save Text As...");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileDlg;
}
/**
 * Return the Panel property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getPanel() {
	if (ivjPanel == null) {
		try {
			ivjPanel = new java.awt.Panel();
			ivjPanel.setName("Panel");
			ivjPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCitrixOkBtn = new java.awt.GridBagConstraints();
			constraintsCitrixOkBtn.gridx = 0; constraintsCitrixOkBtn.gridy = 0;
			constraintsCitrixOkBtn.insets = new java.awt.Insets(0, 0, 0, 10);
			getPanel().add(getCitrixOkBtn(), constraintsCitrixOkBtn);

			java.awt.GridBagConstraints constraintsCitrixCanBtn = new java.awt.GridBagConstraints();
			constraintsCitrixCanBtn.gridx = 1; constraintsCitrixCanBtn.gridy = 0;
			constraintsCitrixCanBtn.weighty = 1.0;
			getPanel().add(getCitrixCanBtn(), constraintsCitrixCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPanel;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Comment
 */
public void includedChanged(ItemEvent e) {
	if (e.getStateChange() == ItemEvent.SELECTED) {
		getCitrixLbl().setEnabled(false);
		getCitrixTF().setEnabled(false);
		getCitrixBtn().setEnabled(false);

		getCitrixOkBtn().setEnabled(true);
	}

	return;
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ConfigMgrIcaDlg");
		setLayout(new java.awt.BorderLayout());
		setSize(406, 240);
		setTitle("Select Citrix ICA Client");
		add(getContentsPane(), "Center");

		getCitrixIncCB().setCheckboxGroup(getCitrixCBG());
		getCitrixNatCB().setCheckboxGroup(getCitrixCBG());

		// Set up the citrix dialog.
		String icaPath = cfg.getProperty("ICAPATH",null);

		boolean useNative = (icaPath != null);
		getCitrixNatCB().setState(useNative);
		getCitrixIncCB().setState(! useNative);
		getCitrixLbl().setEnabled(useNative);
		getCitrixTF().setEnabled(useNative);
		getCitrixBtn().setEnabled(useNative);
		getCitrixTF().setText(useNative ? icaPath : "");

		if (useNative)
			citrixTextChg();
		else
			getCitrixOkBtn().setEnabled(true);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void locateCitrix(ActionEvent e) throws Exception {
	getFileDlg().setTitle("Select Citrix ICA Client");
	getFileDlg().setMode(FileDialog.LOAD);
	getFileDlg().setVisible(true);
	String file = getFileDlg().getFile();
	if (file != null) {
		String directory = getFileDlg().getDirectory();
		if (directory != null)
			getCitrixTF().setText(directory + file);
		else
			getCitrixTF().setText(file);
	}

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		ConfigMgrIcaDlg aConfigMgrIcaDlg = new oem.edge.ed.odc.applet.ConfigMgrIcaDlg(new java.awt.Frame());
		aConfigMgrIcaDlg.setModal(true);
		aConfigMgrIcaDlg.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aConfigMgrIcaDlg.show();
		java.awt.Insets insets = aConfigMgrIcaDlg.getInsets();
		aConfigMgrIcaDlg.setSize(aConfigMgrIcaDlg.getWidth() + insets.left + insets.right, aConfigMgrIcaDlg.getHeight() + insets.top + insets.bottom);
		aConfigMgrIcaDlg.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Dialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void nativeChanged(ItemEvent e) {
	if (e.getStateChange() == ItemEvent.SELECTED) {
		getCitrixLbl().setEnabled(true);
		getCitrixTF().setEnabled(true);
		getCitrixBtn().setEnabled(true);

		getCitrixTF().requestFocus();
		citrixTextChg();
	}

	return;
}
/**
 * Comment
 */
public void setCitrix(ActionEvent e) throws Exception {
	// If ok button pushed, or text field enter pushed && ok button enabled,
	// then change the settings and save them. Otherwise, ignore the
	// text field enter.
	if (e.getSource() == getCitrixOkBtn() || getCitrixOkBtn().isEnabled()) {
		if (getCitrixIncCB().getState())
			cfg.removeProperty("ICAPATH");
		else
			// Get the native ICA path
			cfg.setProperty("ICAPATH",getCitrixTF().getText());

		// Save the ini file if requested.
		FileOutputStream f = new FileOutputStream("edesign.ini");
		cfg.save(f);
		f.close();

		dispose();
	}

	return;
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
		ivjErrorDlg.setSize(259, 140);
		ivjErrorDlg.setName("ErrorDlg");
		ivjErrorDlg.setModal(true);
		ivjErrorDlg.setResizable(false);
		ivjErrorDlg.setTitle("Error");
	}
	return ivjErrorDlg;
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
