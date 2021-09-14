package oem.edge.ed.odc.dropbox.client;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import oem.edge.ed.odc.dropbox.common.DropboxGenerator;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dsmp.client.ErrorHandler;
import oem.edge.ed.odc.dsmp.common.DboxException;

import com.ibm.as400.webaccess.common.ConfigFile;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComboBox;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2006                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rights                                */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

public class DropboxOptions extends JDialog {
	public static String REFRESHONACTIVATE = "REFRESHONACTIVATE";
	public static String REFRESHONTIMER = "REFRESHONTIMER";
	public static String REFRESHINTERVAL = "REFRESHINTERVAL";
	public static String SAVEWINDOWSTATE = "SAVEWINDOWSTATE";
	public static String SAVETABLESTATE = "SAVETABLESTATE";
	public static String SAVESPLITSTATE = "SAVESPLITSTATE";
	public static String SAVEINITDIRECTORY = "SAVEINITDIRECTORY";
	public static String INITDIRECTORY = "INITDIRECTORY";
	private int busyCursor = 0;
	private DropboxAccess dboxAccess = null;
	private ConfigFile dboxini = new ConfigFile();
	private OptionListener optionListener = null;
	private ErrorHandler errorHandler = new ErrorHandler(this);
	public boolean filterCompleted = false;
	public boolean filterMarked = false;
	public boolean sendNotification = false;
	public boolean returnReceipt = false;
	public boolean refreshOnActivate = false;
	public boolean refreshOnTimer = false;
	public int refreshTimerInterval = 15;
	private JButton ivjCanBtn = null;
	private JPanel ivjJDialogContentPane = null;
	private JTabbedPane ivjJTabbedPane = null;
	private JPanel ivjNotifyPnl = null;
	private JButton ivjOkBtn = null;
	private JCheckBox ivjReceiveEmailCB = null;
	private JCheckBox nagEmailCB = null;
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
	private JLabel ivjCompStateLbl = null;
	private ButtonGroup ivjButtonGroup = null;  // @jve:visual-info  decl-index=0 visual-constraint="20,422"
	private JRadioButton ivjInitDirRemRB = null;
	private JRadioButton ivjInitDirSpecRB = null;
	private JPanel initDirPnl = null;
	private JPanel compStatePnl = null;
	private JLabel refreshLbl = null;
	private JPanel refreshPnl = null;
	private JCheckBox refreshTabCB = null;
	private JCheckBox refreshIntCB = null;
	private JComboBox refreshIntMinCB = null;
	private JLabel refreshMinLbl = null;
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
				filterCompleted = false;
				filterMarked = false;
				returnReceipt = false;
				sendNotification = false;
				SwingUtilities.invokeLater(new MethodRunner(this,"dispose"));
				errorHandler.addMsg(e.getMessage(),"Options Unavailable");
			} catch(RemoteException e) {
				filterCompleted = false;
				filterMarked = false;
				returnReceipt = false;
				sendNotification = false;
				SwingUtilities.invokeLater(new MethodRunner(this,"dispose"));
				errorHandler.addMsg(e.getMessage(),"Options Unavailable");
			}
			
			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void setOptions() {
		String option = (String) options.get(DropboxAccess.NewPackageEmailNotification);
		getReceiveEmailCB().setSelected(option != null && option.equalsIgnoreCase("TRUE"));
		option = (String) options.get(DropboxAccess.NagNotification);
		getNagEmailCB().setSelected(option != null && option.equalsIgnoreCase("TRUE"));
		option = (String) options.get(DropboxAccess.SendNotificationDefault);
		sendNotification = option != null && option.equalsIgnoreCase("TRUE");
		getSendEmailCB().setSelected(sendNotification);
		option = (String) options.get(DropboxAccess.ReturnReceiptDefault);
		returnReceipt = option != null && option.equalsIgnoreCase("TRUE");
		getReturnReceiptCB().setSelected(returnReceipt);
		option = (String) options.get(DropboxAccess.FilterComplete);
		filterCompleted = option != null && option.equalsIgnoreCase("TRUE");
		getHideCompletedCB().setSelected(filterCompleted);
		option = (String) options.get(DropboxAccess.FilterMarked);
		filterMarked = option != null && option.equalsIgnoreCase("TRUE");
		getHideMarkedCB().setSelected(filterMarked);
	}
}
private GetOptionsHandler getOptionsHandler = new GetOptionsHandler();
/**
 * Handler to set all options.
 */
private class SetOptionsListener implements ActionListener, Runnable {
	private HashMap options = null;
	public void actionPerformed(ActionEvent e) {
		try {
			filterCompleted = getHideCompletedCB().isSelected();
			filterMarked = getHideMarkedCB().isSelected();
			returnReceipt = getReturnReceiptCB().isSelected();
			sendNotification = getSendEmailCB().isSelected();

			options = new HashMap();
			options.put(DropboxGenerator.NewPackageEmailNotification,getReceiveEmailCB().isSelected() ? "TRUE" : "FALSE");
			options.put(DropboxGenerator.NagNotification,getNagEmailCB().isSelected() ? "TRUE" : "FALSE");
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
			
			refreshOnActivate = getRefreshTabCB().isSelected();
			refreshOnTimer = getRefreshIntCB().isSelected();

			if (refreshOnActivate) {
				dboxini.removeProperty(REFRESHONACTIVATE);
			}
			else {
				dboxini.setBoolProperty(REFRESHONACTIVATE,false);
			}

			if (refreshOnTimer) {
				dboxini.removeProperty(REFRESHONTIMER);
				Integer i = (Integer) getRefreshIntMinCB().getSelectedItem();
				refreshTimerInterval = i.intValue();
				dboxini.setIntProperty(REFRESHINTERVAL,refreshTimerInterval);
			}
			else {
				dboxini.setBoolProperty(REFRESHONTIMER,false);
				dboxini.removeProperty(REFRESHINTERVAL);
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
				errorHandler.addMsg(e.getMessage(),"Set Options");
			}

			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
}
private SetOptionsListener setOptionsListener = new SetOptionsListener();
/**
 * Listener for various GUI components.
 */
private class EventHandler implements ActionListener, ItemListener {
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getCanBtn()) {
			dispose();
		}
		else if (e.getSource() == getInitDirBtn()) {
			changeDirectory();
		}
		else if (e.getSource() == getSplitLocBtn()) {
			fireResetSplit();
		}
		else if (e.getSource() == getTableSizeBtn()) {
			fireResetTables();
		}
		else if (e.getSource() == getWindowSizeBtn()) {
			fireResetWindow();
		}
	}
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == getInitDirRemRB() || e.getSource() == getInitDirSpecRB()) {
			getInitDirTF().setEnabled(getInitDirSpecRB().isSelected());
			getInitDirBtn().setEnabled(getInitDirSpecRB().isSelected());
		}
		else if (e.getSource() == getRefreshIntCB()) {
			getRefreshIntMinCB().setEnabled(getRefreshIntCB().isSelected());
			refreshMinLbl.setEnabled(getRefreshIntCB().isSelected());
		}
	}
	public void fireResetSplit() {
		if (optionListener != null) {
			optionListener.optionAction(new OptionEvent(OptionEvent.RESETSPLIT,(byte) 0,(byte) 0));
		}
	}
	public void fireResetTables() {
		if (optionListener != null) {
			optionListener.optionAction(new OptionEvent(OptionEvent.RESETTABLES,(byte) 0,(byte) 0));
		}
	}
	public void fireResetWindow() {
		if (optionListener != null) {
			optionListener.optionAction(new OptionEvent(OptionEvent.RESETWINDOW,(byte) 0,(byte) 0));
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
		int option = fc.showDialog(DropboxOptions.this,null);

		// if ok from filechooser, set new path.
		if (option == JFileChooser.APPROVE_OPTION) {
			path = fc.getSelectedFile().getPath();
			getInitDirTF().setText(path);
		}
	}
}
private EventHandler eventHandler = new EventHandler();
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
 * Insert the method's description here.
 * Creation date: (3/22/2004 10:40:12 AM)
 * @param parent java.awt.Container
 */
public void doOptions(Container parent) {
	// Clear out the GUI elements.
	getReceiveEmailCB().setSelected(false);
	getNagEmailCB().setSelected(false);
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

	getRefreshTabCB().setSelected(dboxini.getBoolProperty(REFRESHONACTIVATE,true));
	getRefreshIntCB().setSelected(dboxini.getBoolProperty(REFRESHONTIMER,true));
	Integer i = new Integer(dboxini.getIntProperty(REFRESHINTERVAL,15));

	getRefreshIntMinCB().setSelectedItem(i);

	// Query the server for the options.
	WorkerThread t = new WorkerThread(getOptionsHandler);
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
			ivjCanBtn.addActionListener(eventHandler);
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
			refreshLbl = new JLabel();
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			ivjDefaultPnl = new javax.swing.JPanel();
			ivjDefaultPnl.setName("DefaultPnl");
			ivjDefaultPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsInitDirLbl = new java.awt.GridBagConstraints();
			constraintsInitDirLbl.gridx = 0; constraintsInitDirLbl.gridy = 0;
			constraintsInitDirLbl.gridwidth = 0;
			constraintsInitDirLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsInitDirLbl.insets = new java.awt.Insets(5, 5, 0, 5);
			getDefaultPnl().add(getInitDirLbl(), constraintsInitDirLbl);

          			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 2;
			constraintsJLabel1.gridwidth = 0;
			constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel1.insets = new java.awt.Insets(5,5,0,5);
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 1;
			gridBagConstraints11.weightx = 1.0D;
			gridBagConstraints11.weighty = 0.0D;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.insets = new java.awt.Insets(2,10,0,5);
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.weighty = 0.0D;
			gridBagConstraints6.insets = new java.awt.Insets(2,10,0,5);
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.gridy = 4;
			gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints13.insets = new java.awt.Insets(5,5,0,5);
			refreshLbl.setText("Auto-Refresh:");
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.gridy = 5;
			gridBagConstraints14.weightx = 1.0D;
			gridBagConstraints14.weighty = 1.0D;
			gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.insets = new java.awt.Insets(2,10,5,5);
			gridBagConstraints14.anchor = java.awt.GridBagConstraints.NORTH;
			ivjDefaultPnl.add(getCompStateLbl(), constraintsJLabel1);
			ivjDefaultPnl.add(getInitDirPnl(), gridBagConstraints11);
			ivjDefaultPnl.add(getCompStatePnl(), gridBagConstraints6);
			ivjDefaultPnl.add(refreshLbl, gridBagConstraints13);
			ivjDefaultPnl.add(getRefreshPnl(), gridBagConstraints14);
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
			ivjInitDirBtn.addActionListener(eventHandler);
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
			ivjInitDirRemRB.addItemListener(eventHandler);
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
			ivjInitDirSpecRB.addItemListener(eventHandler);
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
 * Return the CompStateLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getCompStateLbl() {
	if (ivjCompStateLbl == null) {
		try {
			ivjCompStateLbl = new javax.swing.JLabel();
			ivjCompStateLbl.setName("CompStateLbl");
			ivjCompStateLbl.setText("Component States:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCompStateLbl;
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
 * This method initializes nagEmailCB	
 * 	
 * @return javax.swing.JCheckBox	
 */    
private JCheckBox getNagEmailCB() {
	if (nagEmailCB == null) {
		nagEmailCB = new JCheckBox();
		nagEmailCB.setText("Notify me by e-mail when a received package is about to expire");
		nagEmailCB.setToolTipText("Receiver must elect to be notified when packages are about to expire.");
	}
	return nagEmailCB;
}
/**
 * Return the NotifyPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getNotifyPnl() {
	if (ivjNotifyPnl == null) {
		try {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			ivjNotifyPnl = new javax.swing.JPanel();
			ivjNotifyPnl.setName("NotifyPnl");
			ivjNotifyPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsReceiveEmailCB = new java.awt.GridBagConstraints();
			constraintsReceiveEmailCB.gridx = 0; constraintsReceiveEmailCB.gridy = 0;
			constraintsReceiveEmailCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsReceiveEmailCB.insets = new java.awt.Insets(5, 5, 5, 5);
			getNotifyPnl().add(getReceiveEmailCB(), constraintsReceiveEmailCB);

			java.awt.GridBagConstraints constraintsSendEmailCB = new java.awt.GridBagConstraints();
			constraintsSendEmailCB.gridx = 0; constraintsSendEmailCB.gridy = 2;
			constraintsSendEmailCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSendEmailCB.insets = new java.awt.Insets(0, 5, 5, 5);
			java.awt.GridBagConstraints constraintsReturnReceiptCB = new java.awt.GridBagConstraints();
			constraintsReturnReceiptCB.gridx = 0; constraintsReturnReceiptCB.gridy = 3;
			constraintsReturnReceiptCB.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsReturnReceiptCB.weightx = 1.0;
			constraintsReturnReceiptCB.weighty = 1.0;
			constraintsReturnReceiptCB.insets = new java.awt.Insets(0, 5, 5, 5);
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.insets = new java.awt.Insets(0,5,5,5);
			ivjNotifyPnl.add(getSendEmailCB(), constraintsSendEmailCB);
			ivjNotifyPnl.add(getReturnReceiptCB(), constraintsReturnReceiptCB);
			ivjNotifyPnl.add(getNagEmailCB(), gridBagConstraints1);
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
			ivjOkBtn.addActionListener(setOptionsListener);
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
			ivjSplitLocBtn.addActionListener(eventHandler);
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
			ivjTableSizeBtn.addActionListener(eventHandler);
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
			ivjWindowSizeBtn.addActionListener(eventHandler);
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
		setSize(459, 372);
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
	
	if (dboxini != null) {
		refreshOnActivate = dboxini.getBoolProperty(REFRESHONACTIVATE,true);
		refreshOnTimer = dboxini.getBoolProperty(REFRESHONTIMER,true);
		refreshTimerInterval = dboxini.getIntProperty(REFRESHINTERVAL,15);
	}
}
	/**
	 * This method initializes initDirPnl	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getInitDirPnl() {
		if (initDirPnl == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			initDirPnl = new JPanel();
			initDirPnl.setLayout(new GridBagLayout());
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.gridwidth = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = 2;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.insets = new java.awt.Insets(0,5,0,0);
			initDirPnl.add(getInitDirRemRB(), gridBagConstraints2);
			initDirPnl.add(getInitDirSpecRB(), gridBagConstraints3);
			initDirPnl.add(getInitDirTF(), gridBagConstraints4);
			initDirPnl.add(getInitDirBtn(), gridBagConstraints5);
		}
		return initDirPnl;
	}
	/**
	 * This method initializes compStatePnl	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getCompStatePnl() {
		if (compStatePnl == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints111 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			compStatePnl = new JPanel();
			compStatePnl.setLayout(new GridBagLayout());
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.gridwidth = 1;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.gridwidth = 1;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.insets = new java.awt.Insets(2,0,0,0);
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridy = 2;
			gridBagConstraints9.gridwidth = 1;
			gridBagConstraints9.weighty = 0.0D;
			gridBagConstraints9.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints9.insets = new java.awt.Insets(2,0,0,0);
			gridBagConstraints9.weightx = 1.0D;
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.gridy = 0;
			gridBagConstraints10.gridwidth = 0;
			gridBagConstraints10.anchor = GridBagConstraints.EAST;
			gridBagConstraints10.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints111.gridx = 1;
			gridBagConstraints111.gridy = 1;
			gridBagConstraints111.gridwidth = 0;
			gridBagConstraints111.anchor = GridBagConstraints.EAST;
			gridBagConstraints111.insets = new java.awt.Insets(2,0,0,0);
			gridBagConstraints12.gridx = 1;
			gridBagConstraints12.gridy = 2;
			gridBagConstraints12.gridwidth = 0;
			gridBagConstraints12.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints12.insets = new java.awt.Insets(2,0,0,0);
			compStatePnl.add(getWindowSizeCB(), gridBagConstraints7);
			compStatePnl.add(getTableSizeCB(), gridBagConstraints8);
			compStatePnl.add(getSplitLocCB(), gridBagConstraints9);
			compStatePnl.add(getWindowSizeBtn(), gridBagConstraints10);
			compStatePnl.add(getTableSizeBtn(), gridBagConstraints111);
			compStatePnl.add(getSplitLocBtn(), gridBagConstraints12);
		}
		return compStatePnl;
	}
	/**
	 * This method initializes refreshPnl	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getRefreshPnl() {
		if (refreshPnl == null) {
			refreshMinLbl = new JLabel();
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			refreshPnl = new JPanel();
			refreshPnl.setLayout(new GridBagLayout());
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 0;
			gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints15.gridwidth = 0;
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.gridy = 1;
			gridBagConstraints16.insets = new java.awt.Insets(2,0,0,0);
			gridBagConstraints16.weighty = 0.0D;
			gridBagConstraints16.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.gridy = 1;
			gridBagConstraints17.weightx = 0.0D;
			gridBagConstraints17.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints17.insets = new java.awt.Insets(2,0,0,0);
			gridBagConstraints17.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gridBagConstraints18.gridx = 2;
			gridBagConstraints18.gridy = 1;
			gridBagConstraints18.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints18.insets = new java.awt.Insets(2,2,0,0);
			gridBagConstraints18.weightx = 1.0D;
			gridBagConstraints18.weighty = 0.0D;
			refreshMinLbl.setText("minutes");
			refreshPnl.add(getRefreshTabCB(), gridBagConstraints15);
			refreshPnl.add(getRefreshIntCB(), gridBagConstraints16);
			refreshPnl.add(getRefreshIntMinCB(), gridBagConstraints17);
			refreshPnl.add(refreshMinLbl, gridBagConstraints18);
		}
		return refreshPnl;
	}
	/**
	 * This method initializes refreshTabCB	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getRefreshTabCB() {
		if (refreshTabCB == null) {
			refreshTabCB = new JCheckBox();
			refreshTabCB.setText("Refresh Inbox when activated (tab selected)");
		}
		return refreshTabCB;
	}
	/**
	 * This method initializes refreshIntCB	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getRefreshIntCB() {
		if (refreshIntCB == null) {
			refreshIntCB = new JCheckBox();
			refreshIntCB.setText("Refresh Inbox on timed interval:");
			refreshIntCB.addItemListener(eventHandler);
		}
		return refreshIntCB;
	}
	/**
	 * This method initializes refreshIntMinCB	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getRefreshIntMinCB() {
		if (refreshIntMinCB == null) {
			refreshIntMinCB = new JComboBox();
			refreshIntMinCB.setPreferredSize(new java.awt.Dimension(50,24));
			refreshIntMinCB.setEditable(false);
			refreshIntMinCB.addItem(new Integer(5));
			refreshIntMinCB.addItem(new Integer(10));
			refreshIntMinCB.addItem(new Integer(15));
			refreshIntMinCB.addItem(new Integer(20));
			refreshIntMinCB.addItem(new Integer(25));
			refreshIntMinCB.addItem(new Integer(30));
			refreshIntMinCB.addItem(new Integer(45));
			refreshIntMinCB.addItem(new Integer(60));
		}
		return refreshIntMinCB;
	}
       }  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
