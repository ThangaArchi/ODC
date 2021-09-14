package oem.edge.ed.odc.dropbox.client.soa;

import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import com.ibm.as400.webaccess.common.ConfigFile;

import oem.edge.ed.odc.dropbox.client.OptionEvent;
import oem.edge.ed.odc.dropbox.client.OptionListener;
import oem.edge.ed.odc.dropbox.common.DropboxGenerator;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dsmp.client.ErrorRunner;
import oem.edge.ed.odc.dsmp.common.DboxException;

/**
 * Insert the type's description here.
 * Creation date: (3/22/2004 10:12:21 AM)
 * @author: 
 */
public class DropboxOptions extends JDialog {
	public static String SAVEWINDOWSTATE = "SAVEWINDOWSTATE";
	public static String SAVETABLESTATE = "SAVETABLESTATE";
	public static String SAVESPLITSTATE = "SAVESPLITSTATE";
	public static String SAVEINITDIRECTORY = "SAVEINITDIRECTORY";
	public static String INITDIRECTORY = "INITDIRECTORY";
	private int busyCursor = 0;
	private DropboxAccess dboxAccess = null;
	private ConfigFile dboxini = new ConfigFile();
	private OptionListener optionListener = null;
	public boolean filterCompleted = false;
	public boolean filterMarked = false;
	public boolean sendNotification = false;
	public boolean returnReceipt = false;
	private JButton ivjCanBtn = null;
	private JPanel ivjJDialogContentPane = null;
	private JTabbedPane ivjJTabbedPane = null;
	private JPanel ivjNotifyPnl = null;
	private JButton ivjOkBtn = null;
	private JCheckBox ivjReceiveEmailCB = null;
	private JCheckBox ivjSendEmailCB = null;
	private JPanel ivjFilterPnl = null;
	private JCheckBox ivjHideCompletedCB = null;
	private JCheckBox ivjHideMarkedCB = null;
	private JCheckBox ivjReturnReceiptCB = null;
	private JPanel ivjDefaultPnl = null;
	private JButton ivjInitDirBtn = null;
	private JLabel ivjInitDirLbl = null;
	private JTextField ivjInitDirTF = null;
	private JButton ivjSplitLocBtn = null;
	private JCheckBox ivjSplitLocCB = null;
	private JButton ivjTableSizeBtn = null;
	private JCheckBox ivjTableSizeCB = null;
	private JButton ivjWindowSizeBtn = null;
	private JCheckBox ivjWindowSizeCB = null;
	private JLabel ivjJLabel1 = null;
	private ButtonGroup ivjButtonGroup = null;  // @jve:visual-info  decl-index=0 visual-constraint="20,422"
	private JRadioButton ivjInitDirRemRB = null;
	private JRadioButton ivjInitDirSpecRB = null;
/**
 * Generic runnable to turn off the busy cursor. Use busyCursorOff as the Runnable.
 * eg. SwingUtilities.invokeLater(busyCursorOff);
 */
private class BusyCursorOff implements Runnable {
	public void run() {
		busyCursor(false);
	}
}
private BusyCursorOff busyCursorOff = new BusyCursorOff();
/**
 * Handler to query all dropbox options.
 */
private class GetOptionsHandler implements Runnable {
	private Map options = null;
	public void run() {
		try {
			try {
				options = dboxAccess.getOptions();

				if (options != null) {
					SwingUtilities.invokeLater(new MethodRunner(this,"setOptions"));
				}
			} catch(DboxException e) {
				SwingUtilities.invokeLater(new MethodRunner(this,"dispose"));
				SwingUtilities.invokeLater(new ErrorRunner(DropboxOptions.this,e.getMessage(),"Options Unavailable",true));
			}
			
			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void setOptions() {
		String option = (String) options.get(DropboxAccess.NewPackageEmailNotification);
		getReceiveEmailCB().setSelected(option != null && option.equalsIgnoreCase("TRUE"));
		option = (String) options.get(DropboxAccess.SendNotificationDefault);
		getSendEmailCB().setSelected(option != null && option.equalsIgnoreCase("TRUE"));
		option = (String) options.get(DropboxAccess.ReturnReceiptDefault);
		getReturnReceiptCB().setSelected(option != null && option.equalsIgnoreCase("TRUE"));
		option = (String) options.get(DropboxAccess.FilterComplete);
		getHideCompletedCB().setSelected(option != null && option.equalsIgnoreCase("TRUE"));
		option = (String) options.get(DropboxAccess.FilterMarked);
		getHideMarkedCB().setSelected(option != null && option.equalsIgnoreCase("TRUE"));
	}
}
/**
 * Handler to set all options.
 */
private class SetOptionsListener implements ActionListener, Runnable {
	private HashMap options = null;
	public void actionPerformed(ActionEvent e) {
		try {
			filterCompleted = getHideCompletedCB().isSelected();
			filterMarked = getHideMarkedCB().isSelected();

			options = new HashMap();
			options.put(DropboxGenerator.NewPackageEmailNotification,getReceiveEmailCB().isSelected() ? "TRUE" : "FALSE");
			options.put(DropboxGenerator.SendNotificationDefault,getSendEmailCB().isSelected() ? "TRUE" : "FALSE");
			options.put(DropboxGenerator.ReturnReceiptDefault,getReturnReceiptCB().isSelected() ? "TRUE" : "FALSE");
			options.put(DropboxGenerator.FilterComplete,getHideCompletedCB().isSelected() ? "TRUE" : "FALSE");
			options.put(DropboxGenerator.FilterMarked,getHideMarkedCB().isSelected() ? "TRUE" : "FALSE");

			busyCursor(true);
			
			WorkerThread t = new WorkerThread(this);
			t.start();

			if (getWindowSizeCB().isSelected()) {
				dboxini.removeProperty(SAVEWINDOWSTATE);
			}
			else {
				dboxini.setBoolProperty(SAVEWINDOWSTATE,false);
			}

			if (getSplitLocCB().isSelected()) {
				dboxini.removeProperty(SAVESPLITSTATE);
			}
			else {
				dboxini.setBoolProperty(SAVESPLITSTATE,false);
			}

			if (getTableSizeCB().isSelected()) {
				dboxini.removeProperty(SAVETABLESTATE);
			}
			else {
				dboxini.setBoolProperty(SAVETABLESTATE,false);
			}

			if (getInitDirRemRB().isSelected()) {
				dboxini.removeProperty(SAVEINITDIRECTORY);
				dboxini.removeProperty(INITDIRECTORY);
			}
			else {
				dboxini.setBoolProperty(SAVEINITDIRECTORY,false);

				String dir = getInitDirTF().getText();
				if (dir != null && dir.trim().length() > 0) {
					dboxini.setProperty(INITDIRECTORY,dir.trim());
				}
				else {
					dboxini.removeProperty(INITDIRECTORY);
				}
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void run() {
		try {
			try {
				dboxAccess.setOptions(options);

				fireFilterChange();

				SwingUtilities.invokeLater(new MethodRunner(DropboxOptions.this,"dispose"));
			} catch(DboxException e) {
				SwingUtilities.invokeLater(new ErrorRunner(DropboxOptions.this,e.getMessage(),"Set Options",true));
			}

			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
}
/**
 * DropboxOptions constructor comment.
 */
public DropboxOptions() {
	super();
	initialize();
}
/**
 * DropboxOptions constructor comment.
 * @param owner java.awt.Frame
 */
public DropboxOptions(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * Registers the specified OptionListener as a recipient of OptionEvents generated by this dispatcher.
 *
 * @param l OptionListener object implementing the OptionListener interface
 *
 * @see OptionListener
 */
public void addOptionListener(OptionListener l) {
	if (l == null)
		return;

	optionListener = DBEventMulticaster.addOptionListener(optionListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 1:53:12 PM)
 * @param on boolean
 */
public void busyCursor(boolean on) {
	if (on)
		busyCursor++;
	else
		busyCursor--;

	//System.out.println("busyCursor2: " + on + " " + busyCursor);

	if (busyCursor > 0) {
		getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		getGlassPane().setVisible(true);
	}
	else {
		getGlassPane().setVisible(false);
		getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
/**
 * Comment
 */
public void changeDirectory() {
	// Use the current initial directory, if possible.
	String path = getInitDirTF().getText();
	File baseDir = null;

	if (path != null) {
		baseDir = new File(path);
		if (! baseDir.exists()) {
			baseDir = null;
		}
	}
	
	// show filechooser and start in base directory.
	JFileChooser fc = new JFileChooser();
	fc.setApproveButtonText("Change");
	fc.setApproveButtonToolTipText("Change initial directory location");
	fc.setDialogTitle("Change Initial Directory");
	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	fc.setMultiSelectionEnabled(false);
	if (baseDir != null) fc.setCurrentDirectory(baseDir);
	int option = fc.showDialog(this,null);

	// if ok from filechooser, set new path.
	if (option == JFileChooser.APPROVE_OPTION) {
		path = fc.getSelectedFile().getPath();
		getInitDirTF().setText(path);
	}
}
/**
 * Comment
 */
public void dirStateChanged() {
	getInitDirTF().setEnabled(getInitDirSpecRB().isSelected());
	getInitDirBtn().setEnabled(getInitDirSpecRB().isSelected());
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2004 10:40:12 AM)
 * @param parent java.awt.Container
 */
public void doOptions(Container parent) {
	// Clear out the GUI elements.
	getReceiveEmailCB().setSelected(false);
	getSendEmailCB().setSelected(false);
	getReturnReceiptCB().setSelected(false);
	getHideCompletedCB().setSelected(false);
	getHideMarkedCB().setSelected(false);

	// Set up the default GUI elements
	getWindowSizeCB().setSelected(dboxini.getBoolProperty(SAVEWINDOWSTATE,true));
	getSplitLocCB().setSelected(dboxini.getBoolProperty(SAVESPLITSTATE,true));
	getTableSizeCB().setSelected(dboxini.getBoolProperty(SAVETABLESTATE,true));

	boolean saveDir = dboxini.getBoolProperty(SAVEINITDIRECTORY,true);
	getInitDirRemRB().setSelected(saveDir);
	getInitDirSpecRB().setSelected(! saveDir);
	getInitDirBtn().setEnabled(! saveDir);
	getInitDirTF().setEnabled(! saveDir);

	String dir = dboxini.getProperty(INITDIRECTORY,null);
	getInitDirTF().setText(dir);

	// Query the server for the options.
	GetOptionsHandler h = new GetOptionsHandler();
	WorkerThread t = new WorkerThread(h);
	t.start();

	busyCursor(true);

	// Show the dialog.
	setLocationRelativeTo(parent);
	setVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (8/5/2004 10:16:10 AM)
 */
public void fireFilterChange() {
	if (optionListener != null) {
		optionListener.optionAction(new OptionEvent(OptionEvent.FILTERCHANGE,(byte) 0,(byte) 0));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/5/2004 10:16:10 AM)
 */
public void fireResetSplit() {
	if (optionListener != null) {
		optionListener.optionAction(new OptionEvent(OptionEvent.RESETSPLIT,(byte) 0,(byte) 0));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/5/2004 10:16:10 AM)
 */
public void fireResetTables() {
	if (optionListener != null) {
		optionListener.optionAction(new OptionEvent(OptionEvent.RESETTABLES,(byte) 0,(byte) 0));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/5/2004 10:16:10 AM)
 */
public void fireResetWindow() {
	if (optionListener != null) {
		optionListener.optionAction(new OptionEvent(OptionEvent.RESETWINDOW,(byte) 0,(byte) 0));
	}
}
/**
 * Return the ButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
private javax.swing.ButtonGroup getButtonGroup() {
	if (ivjButtonGroup == null) {
		try {
			ivjButtonGroup = new javax.swing.ButtonGroup();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjButtonGroup;
}
/**
 * Return the CanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getCanBtn() {
	if (ivjCanBtn == null) {
		try {
			ivjCanBtn = new javax.swing.JButton();
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
 * Return the DefaultPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getDefaultPnl() {
	if (ivjDefaultPnl == null) {
		try {
			ivjDefaultPnl = new javax.swing.JPanel();
			ivjDefaultPnl.setName("DefaultPnl");
			ivjDefaultPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsInitDirLbl = new java.awt.GridBagConstraints();
			constraintsInitDirLbl.gridx = 0; constraintsInitDirLbl.gridy = 0;
			constraintsInitDirLbl.gridwidth = 0;
			constraintsInitDirLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsInitDirLbl.insets = new java.awt.Insets(5, 5, 0, 5);
			getDefaultPnl().add(getInitDirLbl(), constraintsInitDirLbl);

			java.awt.GridBagConstraints constraintsInitDirTF = new java.awt.GridBagConstraints();
			constraintsInitDirTF.gridx = 1; constraintsInitDirTF.gridy = 2;
			constraintsInitDirTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsInitDirTF.weightx = 1.0;
			getDefaultPnl().add(getInitDirTF(), constraintsInitDirTF);

			java.awt.GridBagConstraints constraintsInitDirBtn = new java.awt.GridBagConstraints();
			constraintsInitDirBtn.gridx = 2; constraintsInitDirBtn.gridy = 2;
			constraintsInitDirBtn.insets = new java.awt.Insets(0, 5, 0, 5);
			getDefaultPnl().add(getInitDirBtn(), constraintsInitDirBtn);

			java.awt.GridBagConstraints constraintsWindowSizeCB = new java.awt.GridBagConstraints();
			constraintsWindowSizeCB.gridx = 0; constraintsWindowSizeCB.gridy = 4;
			constraintsWindowSizeCB.gridwidth = 0;
			constraintsWindowSizeCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsWindowSizeCB.insets = new java.awt.Insets(2, 10, 0, 0);
			getDefaultPnl().add(getWindowSizeCB(), constraintsWindowSizeCB);

			java.awt.GridBagConstraints constraintsTableSizeCB = new java.awt.GridBagConstraints();
			constraintsTableSizeCB.gridx = 0; constraintsTableSizeCB.gridy = 5;
			constraintsTableSizeCB.gridwidth = 0;
			constraintsTableSizeCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTableSizeCB.insets = new java.awt.Insets(2, 10, 0, 0);
			getDefaultPnl().add(getTableSizeCB(), constraintsTableSizeCB);

			java.awt.GridBagConstraints constraintsSplitLocCB = new java.awt.GridBagConstraints();
			constraintsSplitLocCB.gridx = 0; constraintsSplitLocCB.gridy = 6;
			constraintsSplitLocCB.gridwidth = 0;
			constraintsSplitLocCB.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsSplitLocCB.weighty = 1.0;
			constraintsSplitLocCB.insets = new java.awt.Insets(2, 10, 0, 0);
			getDefaultPnl().add(getSplitLocCB(), constraintsSplitLocCB);

			java.awt.GridBagConstraints constraintsWindowSizeBtn = new java.awt.GridBagConstraints();
			constraintsWindowSizeBtn.gridx = 1; constraintsWindowSizeBtn.gridy = 4;
			constraintsWindowSizeBtn.gridwidth = 0;
			constraintsWindowSizeBtn.anchor = java.awt.GridBagConstraints.EAST;
			constraintsWindowSizeBtn.insets = new java.awt.Insets(2, 0, 0, 5);
			getDefaultPnl().add(getWindowSizeBtn(), constraintsWindowSizeBtn);

			java.awt.GridBagConstraints constraintsTableSizeBtn = new java.awt.GridBagConstraints();
			constraintsTableSizeBtn.gridx = 1; constraintsTableSizeBtn.gridy = 5;
			constraintsTableSizeBtn.gridwidth = 0;
			constraintsTableSizeBtn.anchor = java.awt.GridBagConstraints.EAST;
			constraintsTableSizeBtn.insets = new java.awt.Insets(2, 0, 0, 5);
			getDefaultPnl().add(getTableSizeBtn(), constraintsTableSizeBtn);

			java.awt.GridBagConstraints constraintsSplitLocBtn = new java.awt.GridBagConstraints();
			constraintsSplitLocBtn.gridx = 1; constraintsSplitLocBtn.gridy = 6;
			constraintsSplitLocBtn.gridwidth = 0;
			constraintsSplitLocBtn.anchor = java.awt.GridBagConstraints.NORTHEAST;
			constraintsSplitLocBtn.insets = new java.awt.Insets(2, 0, 0, 5);
			getDefaultPnl().add(getSplitLocBtn(), constraintsSplitLocBtn);

			java.awt.GridBagConstraints constraintsInitDirRemRB = new java.awt.GridBagConstraints();
			constraintsInitDirRemRB.gridx = 0; constraintsInitDirRemRB.gridy = 1;
			constraintsInitDirRemRB.gridwidth = 0;
			constraintsInitDirRemRB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsInitDirRemRB.insets = new java.awt.Insets(0, 10, 0, 0);
			getDefaultPnl().add(getInitDirRemRB(), constraintsInitDirRemRB);

			java.awt.GridBagConstraints constraintsInitDirSpecRB = new java.awt.GridBagConstraints();
			constraintsInitDirSpecRB.gridx = 0; constraintsInitDirSpecRB.gridy = 2;
			constraintsInitDirSpecRB.insets = new java.awt.Insets(0, 10, 0, 0);
			getDefaultPnl().add(getInitDirSpecRB(), constraintsInitDirSpecRB);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 3;
			constraintsJLabel1.gridwidth = 0;
			constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel1.insets = new java.awt.Insets(10, 5, 0, 5);
			getDefaultPnl().add(getJLabel1(), constraintsJLabel1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDefaultPnl;
}
/**
 * Return the FilterPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getFilterPnl() {
	if (ivjFilterPnl == null) {
		try {
			ivjFilterPnl = new javax.swing.JPanel();
			ivjFilterPnl.setName("FilterPnl");
			ivjFilterPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsHideCompletedCB = new java.awt.GridBagConstraints();
			constraintsHideCompletedCB.gridx = 0; constraintsHideCompletedCB.gridy = 0;
			constraintsHideCompletedCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsHideCompletedCB.insets = new java.awt.Insets(5, 5, 5, 5);
			getFilterPnl().add(getHideCompletedCB(), constraintsHideCompletedCB);

			java.awt.GridBagConstraints constraintsHideMarkedCB = new java.awt.GridBagConstraints();
			constraintsHideMarkedCB.gridx = 0; constraintsHideMarkedCB.gridy = 1;
			constraintsHideMarkedCB.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsHideMarkedCB.weightx = 1.0;
			constraintsHideMarkedCB.weighty = 1.0;
			constraintsHideMarkedCB.insets = new java.awt.Insets(0, 5, 5, 5);
			getFilterPnl().add(getHideMarkedCB(), constraintsHideMarkedCB);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFilterPnl;
}
/**
 * Return the HideCompletedCB property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getHideCompletedCB() {
	if (ivjHideCompletedCB == null) {
		try {
			ivjHideCompletedCB = new javax.swing.JCheckBox();
			ivjHideCompletedCB.setName("HideCompletedCB");
			ivjHideCompletedCB.setText("Do not show packages which I have fully downloaded");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjHideCompletedCB;
}
/**
 * Return the HideMarkedCB property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getHideMarkedCB() {
	if (ivjHideMarkedCB == null) {
		try {
			ivjHideMarkedCB = new javax.swing.JCheckBox();
			ivjHideMarkedCB.setName("HideMarkedCB");
			ivjHideMarkedCB.setText("Do not show packages which I have marked");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjHideMarkedCB;
}
/**
 * Return the InitDirBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInitDirBtn() {
	if (ivjInitDirBtn == null) {
		try {
			ivjInitDirBtn = new javax.swing.JButton();
			ivjInitDirBtn.setName("InitDirBtn");
			ivjInitDirBtn.setText("Change");
			ivjInitDirBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						changeDirectory();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInitDirBtn;
}
/**
 * Return the InitDirLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getInitDirLbl() {
	if (ivjInitDirLbl == null) {
		try {
			ivjInitDirLbl = new javax.swing.JLabel();
			ivjInitDirLbl.setName("InitDirLbl");
			ivjInitDirLbl.setText("Initial directory:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInitDirLbl;
}
/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getInitDirRemRB() {
	if (ivjInitDirRemRB == null) {
		try {
			ivjInitDirRemRB = new javax.swing.JRadioButton();
			ivjInitDirRemRB.setName("InitDirRemRB");
			ivjInitDirRemRB.setText("Remember last directory as initial directory");
			ivjInitDirRemRB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					try {
						dirStateChanged();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInitDirRemRB;
}
/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getInitDirSpecRB() {
	if (ivjInitDirSpecRB == null) {
		try {
			ivjInitDirSpecRB = new javax.swing.JRadioButton();
			ivjInitDirSpecRB.setName("InitDirSpecRB");
			ivjInitDirSpecRB.setText("Specifiy:");
			ivjInitDirSpecRB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					try {
						dirStateChanged();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}	    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInitDirSpecRB;
}
/**
 * Return the InitDirTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getInitDirTF() {
	if (ivjInitDirTF == null) {
		try {
			ivjInitDirTF = new javax.swing.JTextField();
			ivjInitDirTF.setName("InitDirTF");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInitDirTF;
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJTabbedPane = new java.awt.GridBagConstraints();
			constraintsJTabbedPane.gridx = 0; constraintsJTabbedPane.gridy = 0;
			constraintsJTabbedPane.gridwidth = 0;
			constraintsJTabbedPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJTabbedPane.weightx = 1.0;
			constraintsJTabbedPane.weighty = 1.0;
			constraintsJTabbedPane.insets = new java.awt.Insets(5, 5, 5, 5);
			getJDialogContentPane().add(getJTabbedPane(), constraintsJTabbedPane);

			java.awt.GridBagConstraints constraintsOkBtn = new java.awt.GridBagConstraints();
			constraintsOkBtn.gridx = 0; constraintsOkBtn.gridy = 1;
			constraintsOkBtn.anchor = java.awt.GridBagConstraints.EAST;
			constraintsOkBtn.weightx = 1.0;
			constraintsOkBtn.insets = new java.awt.Insets(0, 5, 5, 5);
			getJDialogContentPane().add(getOkBtn(), constraintsOkBtn);

			java.awt.GridBagConstraints constraintsCanBtn = new java.awt.GridBagConstraints();
			constraintsCanBtn.gridx = 1; constraintsCanBtn.gridy = 1;
			constraintsCanBtn.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCanBtn.weightx = 1.0;
			constraintsCanBtn.insets = new java.awt.Insets(0, 5, 5, 5);
			getJDialogContentPane().add(getCanBtn(), constraintsCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Component States:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
private javax.swing.JTabbedPane getJTabbedPane() {
	if (ivjJTabbedPane == null) {
		try {
			ivjJTabbedPane = new javax.swing.JTabbedPane();
			ivjJTabbedPane.setName("JTabbedPane");
			ivjJTabbedPane.insertTab("Defaults", null, getDefaultPnl(), null, 0);
			ivjJTabbedPane.insertTab("Notification", null, getNotifyPnl(), null, 1);
			ivjJTabbedPane.insertTab("Inbox Filters", null, getFilterPnl(), null, 2);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane;
}
/**
 * Return the NotifyPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getNotifyPnl() {
	if (ivjNotifyPnl == null) {
		try {
			ivjNotifyPnl = new javax.swing.JPanel();
			ivjNotifyPnl.setName("NotifyPnl");
			ivjNotifyPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsReceiveEmailCB = new java.awt.GridBagConstraints();
			constraintsReceiveEmailCB.gridx = 0; constraintsReceiveEmailCB.gridy = 0;
			constraintsReceiveEmailCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsReceiveEmailCB.insets = new java.awt.Insets(5, 5, 5, 5);
			getNotifyPnl().add(getReceiveEmailCB(), constraintsReceiveEmailCB);

			java.awt.GridBagConstraints constraintsSendEmailCB = new java.awt.GridBagConstraints();
			constraintsSendEmailCB.gridx = 0; constraintsSendEmailCB.gridy = 1;
			constraintsSendEmailCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSendEmailCB.insets = new java.awt.Insets(0, 5, 5, 5);
			getNotifyPnl().add(getSendEmailCB(), constraintsSendEmailCB);

			java.awt.GridBagConstraints constraintsReturnReceiptCB = new java.awt.GridBagConstraints();
			constraintsReturnReceiptCB.gridx = 0; constraintsReturnReceiptCB.gridy = 2;
			constraintsReturnReceiptCB.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsReturnReceiptCB.weightx = 1.0;
			constraintsReturnReceiptCB.weighty = 1.0;
			constraintsReturnReceiptCB.insets = new java.awt.Insets(0, 5, 5, 5);
			getNotifyPnl().add(getReturnReceiptCB(), constraintsReturnReceiptCB);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNotifyPnl;
}
/**
 * Return the OkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOkBtn() {
	if (ivjOkBtn == null) {
		try {
			ivjOkBtn = new javax.swing.JButton();
			ivjOkBtn.setName("OkBtn");
			ivjOkBtn.setText("Ok");
			ivjOkBtn.addActionListener(new SetOptionsListener());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOkBtn;
}
/**
 * Return the NoEmailCB property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getReceiveEmailCB() {
	if (ivjReceiveEmailCB == null) {
		try {
			ivjReceiveEmailCB = new javax.swing.JCheckBox();
			ivjReceiveEmailCB.setName("ReceiveEmailCB");
			ivjReceiveEmailCB.setToolTipText("Sender must elect to send e-mails");
			ivjReceiveEmailCB.setText("Notify me by e-mail when new packages arrive");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjReceiveEmailCB;
}
/**
 * Return the ReturnReceiptCB property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getReturnReceiptCB() {
	if (ivjReturnReceiptCB == null) {
		try {
			ivjReturnReceiptCB = new javax.swing.JCheckBox();
			ivjReturnReceiptCB.setName("ReturnReceiptCB");
			ivjReturnReceiptCB.setText("Notify me when each recipient receives a package I sent");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjReturnReceiptCB;
}
/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getSendEmailCB() {
	if (ivjSendEmailCB == null) {
		try {
			ivjSendEmailCB = new javax.swing.JCheckBox();
			ivjSendEmailCB.setName("SendEmailCB");
			ivjSendEmailCB.setText("Send an e-mail to each recipient when a package is sent");
			ivjSendEmailCB.setActionCommand("SendEmailCB");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendEmailCB;
}
/**
 * Return the SplitLocBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSplitLocBtn() {
	if (ivjSplitLocBtn == null) {
		try {
			ivjSplitLocBtn = new javax.swing.JButton();
			ivjSplitLocBtn.setName("SplitLocBtn");
			ivjSplitLocBtn.setText("Restore Default");
			ivjSplitLocBtn.setEnabled(true);
			ivjSplitLocBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						fireResetSplit();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}   
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSplitLocBtn;
}
/**
 * Return the SplitLocCB property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getSplitLocCB() {
	if (ivjSplitLocCB == null) {
		try {
			ivjSplitLocCB = new javax.swing.JCheckBox();
			ivjSplitLocCB.setName("SplitLocCB");
			ivjSplitLocCB.setText("Remember split divider locations");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSplitLocCB;
}
/**
 * Return the TableSizeBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTableSizeBtn() {
	if (ivjTableSizeBtn == null) {
		try {
			ivjTableSizeBtn = new javax.swing.JButton();
			ivjTableSizeBtn.setName("TableSizeBtn");
			ivjTableSizeBtn.setText("Restore Default");
			ivjTableSizeBtn.setEnabled(true);
			ivjTableSizeBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						fireResetTables();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}	    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTableSizeBtn;
}
/**
 * Return the TableSizeCB property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getTableSizeCB() {
	if (ivjTableSizeCB == null) {
		try {
			ivjTableSizeCB = new javax.swing.JCheckBox();
			ivjTableSizeCB.setName("TableSizeCB");
			ivjTableSizeCB.setText("Remember table column sizes and order");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTableSizeCB;
}
/**
 * Return the WindowSizeBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getWindowSizeBtn() {
	if (ivjWindowSizeBtn == null) {
		try {
			ivjWindowSizeBtn = new javax.swing.JButton();
			ivjWindowSizeBtn.setName("WindowSizeBtn");
			ivjWindowSizeBtn.setText("Restore Default");
			ivjWindowSizeBtn.setEnabled(true);
			ivjWindowSizeBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						fireResetWindow();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}	    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjWindowSizeBtn;
}
/**
 * Return the WindowSizeCB property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getWindowSizeCB() {
	if (ivjWindowSizeCB == null) {
		try {
			ivjWindowSizeCB = new javax.swing.JCheckBox();
			ivjWindowSizeCB.setName("WindowSizeCB");
			ivjWindowSizeCB.setText("Remember window size and location");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjWindowSizeCB;
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
		setName("DropboxOptions");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(459, 338);
		setModal(true);
		setTitle("Dropbox Options");
		setContentPane(getJDialogContentPane());

		getButtonGroup().add(getInitDirRemRB());
		getButtonGroup().add(getInitDirSpecRB());
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
		DropboxOptions aDropboxOptions;
		aDropboxOptions = new DropboxOptions();
		aDropboxOptions.setModal(true);
		aDropboxOptions.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aDropboxOptions.show();
		java.awt.Insets insets = aDropboxOptions.getInsets();
		aDropboxOptions.setSize(aDropboxOptions.getWidth() + insets.left + insets.right, aDropboxOptions.getHeight() + insets.top + insets.bottom);
		aDropboxOptions.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * Removes the specified OptionListener as a recipient of OptionEvents generated by this dispatcher.
 *
 * @param l OptionListener object implementing the OptionListener interface
 *
 * @see OptionListener
 */
public void removeOptionListener(OptionListener l) {
	if (l == null)
		return;

	optionListener = DBEventMulticaster.removeOptionListener(optionListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2004 2:34:02 PM)
 * @param newDispatcher oem.edge.ed.odc.dropbox.client.DBDispatcher
 */
public void setDropboxAccess(DropboxAccess newDboxAccess) {
	if (newDboxAccess != null) {
		// Nothing to do.
	}

	dboxAccess = newDboxAccess;

	if (dboxAccess != null) {
		// Nothing to do.
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2004 1:16:53 PM)
 * @param ini com.ibm.as400.webaccess.common.ConfigFile
 */
public void setIniConfig(ConfigFile ini) {
	dboxini = ini;
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
