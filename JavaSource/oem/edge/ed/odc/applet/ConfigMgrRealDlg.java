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
 * Creation date: (5/29/2002 10:55:56 AM)
 * @author: Mike Zarnick
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class ConfigMgrRealDlg extends Dialog {
	private ConfigFile cfg = null;
	private Button ivjCanBtn = null;
	private Panel ivjContentsPane = null;
	private Panel ivjContentsPane1 = null;
	private Dialog ivjErrorDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="18,220"
	private Label ivjErrorLbl = null;
	private Button ivjErrorOkBtn = null;
	private FileDialog ivjFileDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="271,311"
	private Button ivjOkBtn = null;
	private Panel ivjPanel = null;
	private Button ivjRealBtn = null;
	private Label ivjRealIntroLbl = null;
	private TextField ivjRealTF = null;
/**
 * ConfigMgrRealDlg constructor comment.
 * @param owner java.awt.Frame
 */
public ConfigMgrRealDlg(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * ConfigMgrRealDlg constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public ConfigMgrRealDlg(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * ConfigMgrRealDlg constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public ConfigMgrRealDlg(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ConfigMgrRealDlg constructor comment.
 * @param owner java.awt.Frame
 */
public ConfigMgrRealDlg(java.awt.Frame owner, ConfigFile cfg) {
	super(owner);
	this.cfg = cfg;
	initialize();
}
/**
 * ConfigMgrRealDlg constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public ConfigMgrRealDlg(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
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
 * Return the CanBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getCanBtn() {
	if (ivjCanBtn == null) {
		try {
			ivjCanBtn = new java.awt.Button();
			ivjCanBtn.setName("CanBtn");
			ivjCanBtn.setLabel("Cancel");
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
private java.awt.Panel getContentsPane() {
	if (ivjContentsPane == null) {
		try {
			ivjContentsPane = new java.awt.Panel();
			ivjContentsPane.setName("ContentsPane");
			ivjContentsPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRealIntroLbl = new java.awt.GridBagConstraints();
			constraintsRealIntroLbl.gridx = -1; constraintsRealIntroLbl.gridy = 0;
			constraintsRealIntroLbl.gridwidth = 0;
			constraintsRealIntroLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRealIntroLbl.insets = new java.awt.Insets(10, 10, 0, 10);
			getContentsPane().add(getRealIntroLbl(), constraintsRealIntroLbl);

			java.awt.GridBagConstraints constraintsRealTF = new java.awt.GridBagConstraints();
			constraintsRealTF.gridx = 0; constraintsRealTF.gridy = 1;
			constraintsRealTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRealTF.weightx = 1.0;
			constraintsRealTF.insets = new java.awt.Insets(0, 20, 0, 0);
			getContentsPane().add(getRealTF(), constraintsRealTF);

			java.awt.GridBagConstraints constraintsRealBtn = new java.awt.GridBagConstraints();
			constraintsRealBtn.gridx = 1; constraintsRealBtn.gridy = 1;
			constraintsRealBtn.insets = new java.awt.Insets(0, 5, 0, 10);
			getContentsPane().add(getRealBtn(), constraintsRealBtn);

			java.awt.GridBagConstraints constraintsPanel = new java.awt.GridBagConstraints();
			constraintsPanel.gridx = -1; constraintsPanel.gridy = 2;
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
 * This method initializes dialog
 * 
 * @return java.awt.Dialog
 */
private java.awt.Dialog getErrorDlg() {
	if (ivjErrorDlg == null) {
		ivjErrorDlg = new java.awt.Dialog((Frame) getParent());
		ivjErrorDlg.add(getContentsPane1(), java.awt.BorderLayout.CENTER);
		ivjErrorDlg.setSize(228, 131);
		ivjErrorDlg.setResizable(false);
		ivjErrorDlg.setModal(true);
		ivjErrorDlg.setTitle("Error");
		ivjErrorDlg.setName("ErrorDlg");
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
 * Return the OkBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getOkBtn() {
	if (ivjOkBtn == null) {
		try {
			ivjOkBtn = new java.awt.Button();
			ivjOkBtn.setName("OkBtn");
			ivjOkBtn.setEnabled(false);
			ivjOkBtn.setLabel("Ok");
			ivjOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						try {
							setReal(e);
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
	return ivjOkBtn;
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

			java.awt.GridBagConstraints constraintsOkBtn = new java.awt.GridBagConstraints();
			constraintsOkBtn.gridx = 0; constraintsOkBtn.gridy = 0;
			constraintsOkBtn.insets = new java.awt.Insets(0, 0, 0, 10);
			getPanel().add(getOkBtn(), constraintsOkBtn);

			java.awt.GridBagConstraints constraintsCanBtn = new java.awt.GridBagConstraints();
			constraintsCanBtn.gridx = 1; constraintsCanBtn.gridy = 0;
			constraintsCanBtn.weighty = 1.0;
			getPanel().add(getCanBtn(), constraintsCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPanel;
}
/**
 * Return the RealBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getRealBtn() {
	if (ivjRealBtn == null) {
		try {
			ivjRealBtn = new java.awt.Button();
			ivjRealBtn.setName("RealBtn");
			ivjRealBtn.setLabel("Browse");
			ivjRealBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						locateReal(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRealBtn;
}
/**
 * Return the RealIntroLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getRealIntroLbl() {
	if (ivjRealIntroLbl == null) {
		try {
			ivjRealIntroLbl = new java.awt.Label();
			ivjRealIntroLbl.setName("RealIntroLbl");
			ivjRealIntroLbl.setText("Please specify the program to use for Real Media files:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRealIntroLbl;
}
/**
 * Return the RealTF property value.
 * @return java.awt.TextField
 */
private java.awt.TextField getRealTF() {
	if (ivjRealTF == null) {
		try {
			ivjRealTF = new java.awt.TextField();
			ivjRealTF.setName("RealTF");
			ivjRealTF.addTextListener(new java.awt.event.TextListener() { 
				public void textValueChanged(java.awt.event.TextEvent e) {    
					try {
						textChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
			ivjRealTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						try {
							setReal(e);
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
	return ivjRealTF;
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
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ConfigMgrRealDlg");
		setLayout(new java.awt.BorderLayout());
		setBackground(java.awt.SystemColor.window);
		setSize(397, 171);
		setModal(true);
		setTitle("Specify Real Player");
		add(getContentsPane(), "Center");

		// Set up the citrix dialog.
		String realPath = cfg.getProperty("REALPATH",null);

		if (realPath != null) {
			getRealTF().setText(realPath);
		}

		textChg();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void locateReal(ActionEvent e) throws Exception {
	getFileDlg().setTitle("Select Real Media Player Client");
	getFileDlg().setMode(FileDialog.LOAD);
	getFileDlg().setVisible(true);
	String file = getFileDlg().getFile();
	if (file != null) {
		String directory = getFileDlg().getDirectory();
		if (directory != null)
			getRealTF().setText(directory + file);
		else
			getRealTF().setText(file);
	}

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		ConfigMgrRealDlg aConfigMgrRealDlg = new oem.edge.ed.odc.applet.ConfigMgrRealDlg(new java.awt.Frame());
		aConfigMgrRealDlg.setModal(true);
		aConfigMgrRealDlg.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aConfigMgrRealDlg.show();
		java.awt.Insets insets = aConfigMgrRealDlg.getInsets();
		aConfigMgrRealDlg.setSize(aConfigMgrRealDlg.getWidth() + insets.left + insets.right, aConfigMgrRealDlg.getHeight() + insets.top + insets.bottom);
		aConfigMgrRealDlg.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Dialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void setReal(ActionEvent e) throws Exception {
	// If ok button pushed, or text field enter pushed && ok button enabled,
	// then change the settings and save them. Otherwise, ignore the
	// text field enter.
	if (e.getSource() == getOkBtn() || getOkBtn().isEnabled()) {
		cfg.setProperty("REALPATH",getRealTF().getText());

		// Save the ini file if requested.
		FileOutputStream f = new FileOutputStream("edesign.ini");
		cfg.save(f);
		f.close();

		dispose();
	}

	return;
}
/**
 * Comment
 */
public void textChg() {
	String realPath = getRealTF().getText();

	boolean enabled = false;

	if (realPath.trim().length() != 0) {
		File f = new File(realPath);
		enabled = f.exists();
	}

	getOkBtn().setEnabled(enabled);

	return;
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
