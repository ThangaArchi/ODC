package oem.edge.ed.odc.ftp.client;

import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import oem.edge.ed.odc.dropbox.client.FileStatusListener;
import oem.edge.ed.odc.dropbox.client.FileStatusEvent;

/**
 * Insert the type's description here.
 * Creation date: (3/7/2003 8:25:15 AM)
 * @author: Mike Zarnick
 */
public class LocalFilePnl extends JPanel implements DocumentListener, oem.edge.ed.odc.dropbox.client.FileStatusListener {
	private boolean loggedIn = false;
	private boolean allowDirs = false;
	private JMenu fieldLocalMenu = null;
	private JLabel ivjLocalLbl = null;
	private JButton ivjUploadBtn = null;
	private JButton ivjFolderCanBtn = null;
	private JDialog ivjFolderDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="412,343"
	private JLabel ivjFolderLbl = null;
	private JButton ivjFolderOkBtn = null;
	private JTextField ivjFolderTF = null;
	private JPanel ivjJDialogContentPane1 = null;
	private JPanel ivjJPanel = null;
	private FileTableSorter ivjFileSortTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="420,51"
	private FileTableModel ivjFileTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="514,52"
	private JPanel ivjBtnPnl = null;
	private JButton ivjDeleteBtn = null;
	private JComboBox ivjDrivesCB = null;
	private JLabel ivjDrivesLbl = null;
	private JScrollPane ivjFileSP = null;
	private JTable ivjFileTbl = null;
	private JButton ivjHomeBtn = null;
	private JButton ivjNewDirBtn = null;
	private JLabel ivjPathLbl = null;
	private JPanel ivjPathPnl = null;
	private JTextField ivjPathTF = null;
	private JButton ivjRefreshBtn = null;
	private JButton ivjUpBtn = null;
	protected transient oem.edge.ed.odc.ftp.client.LocalFilePnlListener fieldLocalFilePnlListenerEventMulticaster = null;
	private JMenuItem ivjDeleteMI = null;
	private JMenuItem ivjHomeMI = null;
	private JMenu ivjLocalM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="421,124"
	private JMenuItem ivjRefreshMI = null;
	private JMenuItem ivjUploadMI = null;
	private JMenuItem ivjUpMI = null;
	private JMenuItem ivjNewDirMI = null;
	private JSeparator ivjSeparator1 = null;
	private JSeparator ivjSeparator2 = null;
	private boolean fieldUploadBtnVisible = false;
/**
 * LocalFilePnl constructor comment.
 */
public LocalFilePnl() {
	super();
	initialize();
}
/**
 * LocalFilePnl constructor comment.
 * @param layout java.awt.LayoutManager
 */
public LocalFilePnl(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * LocalFilePnl constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public LocalFilePnl(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * LocalFilePnl constructor comment.
 * @param isDoubleBuffered boolean
 */
public LocalFilePnl(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * 
 * @param newListener oem.edge.ed.odc.ftp.client.LocalFilePnlListener
 */
public void addLocalFilePnlListener(oem.edge.ed.odc.ftp.client.LocalFilePnlListener newListener) {
	fieldLocalFilePnlListenerEventMulticaster = oem.edge.ed.odc.ftp.client.LocalFilePnlListenerEventMulticaster.add(fieldLocalFilePnlListenerEventMulticaster, newListener);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 1:53:12 PM)
 * @param on boolean
 */
public void busyCursor(boolean on) {
	Component glassPane = null;

	Component p = this;
	while (p != null && glassPane == null) {
		if (p instanceof JFrame)
			glassPane = ((JFrame) p).getGlassPane();
		else if (p instanceof JDialog)
			glassPane = ((JDialog) p).getGlassPane();
		else
			p = p.getParent();
	}

	if (glassPane != null) {
		if (on) {
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
		}
		else {
			glassPane.setVisible(false);
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2002 12:01:40 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void changedUpdate(DocumentEvent e) {
	if (e.getDocument() == getFolderTF().getDocument())
		textChgFolder();
}
/**
 * Comment
 */
public void chgDirToHome() {
	busyCursor(true);

	getFileTbl().clearSelection();
	String dir = System.getProperty("user.home");
	getPathTF().setText(dir);
	getFileTM().setDirectory(dir);
	getFileTM().populateLocal();

	busyCursor(false);
}
/**
 * Comment
 */
public void chgDirToParent() {
	busyCursor(true);

	File dir = new File(getFileTM().getDirectory());
	String parent = dir.getParent();
	if (parent != null) {
		File parentDir = new File(parent);
		getFileTbl().clearSelection();
		getFileTM().setDirectory(parentDir.getPath());
		getPathTF().setText(parentDir.getPath());
		getFileTM().populateLocal();
	}

	busyCursor(false);
}
/**
 * Comment
 */
public void chgLocalDir() {
	String dir = getPathTF().getText();

	if (dir != null && (dir = dir.trim()).length() > 0) {
		File f = new File(dir);
		if (f.exists()) {
			if (f.isDirectory()) {
				busyCursor(true);
				getFileTbl().clearSelection();
				getFileTM().setDirectory(dir);
				getFileTM().populateLocal();
				busyCursor(false);
			}
			else
				JOptionPane.showMessageDialog(this,f.getName() + "is not a directory!","Error",JOptionPane.ERROR_MESSAGE);
		}
		else
			JOptionPane.showMessageDialog(this,f.getName(),"File Not Found",JOptionPane.ERROR_MESSAGE);
	}
}
/**
 * Comment
 */
public void chgLocalDrive() {
	File f = (File) getDrivesCB().getSelectedItem();

	if (f.exists()) {
		busyCursor(true);
		getFileTbl().clearSelection();
		getPathTF().setText(f.toString());
		getFileTM().setDirectory(f.toString());
		getFileTM().populateLocal();
		busyCursor(false);
	}
	else
		JOptionPane.showMessageDialog(this,f.getPath(),"Drive Not Found",JOptionPane.ERROR_MESSAGE);
}
/**
 * connPtoP1SetTarget:  (LocalTM.this <--> LocalSortTM.model)
 */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getFileSortTM().setModel(getFileTM());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetTarget:  (LocalSortTM.this <--> LocalTbl.model)
 */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getFileTbl().setModel(getFileSortTM());
		getFileTbl().createDefaultColumnsFromModel();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void createFolder() {
	if (! getFolderOkBtn().isEnabled())
		return;

	getFolderDlg().dispose();
	busyCursor(true);

	// Create the folder
	File f = new File(getFileTM().getDirectory(),getFolderTF().getText().trim());
	if (! f.mkdir()) {
		busyCursor(false);
		JOptionPane.showMessageDialog(this,"Unable to create directory","Error",JOptionPane.ERROR_MESSAGE);
		return;
	}

	// Update data model and make directory selected.
	getFileTM().populateLocal();
	int i = getFileTM().getFileIndex(f.getName());
	if (i != -1)
		i = getFileSortTM().getSortedIndex(i);
	if (i != -1) {
		getFileTbl().setRowSelectionInterval(i,i);
		getFileTbl().scrollRectToVisible(getFileTbl().getCellRect(i,0,false));
	}

	busyCursor(false);
}
/**
 * Comment
 */
public void doCreate() {
	Component c = this;

	while (c != null && ! (c instanceof JFrame || c instanceof JDialog)) {
		c = c.getParent();
	}

	textChgFolder();
	getFolderDlg().setLocationRelativeTo(c);
	getFolderDlg().setVisible(true);
}
/**
 * Comment
 */
public void doDelete() {
	int[] s = getFileTbl().getSelectedRows();
	String[] msg = new String[s.length+1];

	if (s.length == 1)
		msg[0] = "Delete the following local file?";
	else
		msg[0] = "Delete the following local files?";

	for (int i = 0; i < s.length; i++) {
		msg[i+1] = "  " + getFileTM().getFileName(getFileSortTM().getUnsortedIndex(s[i]));
	}

	int result = JOptionPane.showConfirmDialog(this,msg,"Confirm Delete",JOptionPane.YES_NO_OPTION);

	if (result == JOptionPane.YES_OPTION) {
		// We're busy now...
		busyCursor(true);

		// Delete the file(s)
		for (int i = 0; i < s.length; i++) {
			getFileTbl().removeRowSelectionInterval(s[i],s[i]);
			String name = getFileTM().getFileName(getFileSortTM().getUnsortedIndex(s[i]));
			File f = new File(getFileTM().getDirectory(),name);
			if (! f.delete()) {
				busyCursor(false);
				JOptionPane.showMessageDialog(this,"Unable to delete " + name,"Error",JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// Update data model and make directory selected.
		getFileTM().populateLocal();
		busyCursor(false);
	}
}
/**
 * Comment
 */
public void fileListSelectionChg() {
	int[] s = getFileTbl().getSelectedRows();
	if (s.length == 0) {
		getDeleteBtn().setEnabled(false);
		getUploadBtn().setEnabled(false);
		getDeleteMI().setEnabled(false);
		getUploadMI().setEnabled(false);
	}
	else {
		boolean dir = false;
		for (int i = 0; i < s.length; i++) {
			Boolean isDir = (Boolean) getFileSortTM().getValueAt(s[i],0);
			dir |= isDir.booleanValue();
		}

		getDeleteBtn().setEnabled(! dir);
		getDeleteMI().setEnabled(! dir);

		boolean enabled = isLoggedIn() && (isAllowDirs() || ! dir);
		getUploadBtn().setEnabled(enabled);
		getUploadMI().setEnabled(enabled);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/9/2003 11:13:26 AM)
 * @param e oem.edge.ed.odc.dropbox.client.FileStatusEvent
 */
public void fileStatusAction(FileStatusEvent e) {
	if (e.isUpload)
		return;

	if (e.isFileEnded() && e.sourceDir.equals(getFileTM().getDirectory())) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refresh();
			}
		});
	}
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireUploadBtn_actionPerformed(java.util.EventObject newEvent) {
	if (fieldLocalFilePnlListenerEventMulticaster == null) {
		return;
	};
	fieldLocalFilePnlListenerEventMulticaster.uploadBtn_actionPerformed(newEvent);
}
/**
 * Return the LocalBtnPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getBtnPnl() {
	if (ivjBtnPnl == null) {
		try {
			ivjBtnPnl = new javax.swing.JPanel();
			ivjBtnPnl.setName("BtnPnl");
			ivjBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsHomeBtn = new java.awt.GridBagConstraints();
			constraintsHomeBtn.gridx = 0; constraintsHomeBtn.gridy = 0;
			constraintsHomeBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsHomeBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getBtnPnl().add(getHomeBtn(), constraintsHomeBtn);

			java.awt.GridBagConstraints constraintsUpBtn = new java.awt.GridBagConstraints();
			constraintsUpBtn.gridx = 1; constraintsUpBtn.gridy = 0;
			constraintsUpBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsUpBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getBtnPnl().add(getUpBtn(), constraintsUpBtn);

			java.awt.GridBagConstraints constraintsRefreshBtn = new java.awt.GridBagConstraints();
			constraintsRefreshBtn.gridx = 2; constraintsRefreshBtn.gridy = 0;
			constraintsRefreshBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsRefreshBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getBtnPnl().add(getRefreshBtn(), constraintsRefreshBtn);

			java.awt.GridBagConstraints constraintsNewDirBtn = new java.awt.GridBagConstraints();
			constraintsNewDirBtn.gridx = 3; constraintsNewDirBtn.gridy = 0;
			constraintsNewDirBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsNewDirBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getBtnPnl().add(getNewDirBtn(), constraintsNewDirBtn);

			java.awt.GridBagConstraints constraintsDeleteBtn = new java.awt.GridBagConstraints();
			constraintsDeleteBtn.gridx = 4; constraintsDeleteBtn.gridy = 0;
			constraintsDeleteBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsDeleteBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getBtnPnl().add(getDeleteBtn(), constraintsDeleteBtn);

			java.awt.GridBagConstraints constraintsDrivesLbl = new java.awt.GridBagConstraints();
			constraintsDrivesLbl.gridx = 6; constraintsDrivesLbl.gridy = 0;
			constraintsDrivesLbl.insets = new java.awt.Insets(0, 10, 0, 2);
			getBtnPnl().add(getDrivesLbl(), constraintsDrivesLbl);

			java.awt.GridBagConstraints constraintsDrivesCB = new java.awt.GridBagConstraints();
			constraintsDrivesCB.gridx = 7; constraintsDrivesCB.gridy = 0;
			constraintsDrivesCB.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDrivesCB.weightx = 1.0;
			getBtnPnl().add(getDrivesCB(), constraintsDrivesCB);

			java.awt.GridBagConstraints constraintsUploadBtn = new java.awt.GridBagConstraints();
			constraintsUploadBtn.gridx = 5; constraintsUploadBtn.gridy = 0;
			constraintsUploadBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsUploadBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getBtnPnl().add(getUploadBtn(), constraintsUploadBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjBtnPnl;
}
/**
 * Return the LocalDeleteBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDeleteBtn() {
	if (ivjDeleteBtn == null) {
		try {
			ivjDeleteBtn = new javax.swing.JButton();
			ivjDeleteBtn.setName("DeleteBtn");
			ivjDeleteBtn.setToolTipText("Delete Local");
			ivjDeleteBtn.setText("");
			ivjDeleteBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjDeleteBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjDeleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/trash.gif")));
			ivjDeleteBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjDeleteBtn.setEnabled(false);
			ivjDeleteBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						doDelete();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDeleteBtn;
}
/**
 * Return the DeleteMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getDeleteMI() {
	if (ivjDeleteMI == null) {
		try {
			ivjDeleteMI = new javax.swing.JMenuItem();
			ivjDeleteMI.setName("DeleteMI");
			ivjDeleteMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/trash.gif")));
			ivjDeleteMI.setMnemonic('d');
			ivjDeleteMI.setText("Delete files");
			ivjDeleteMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,InputEvent.CTRL_MASK,false));
			ivjDeleteMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						doDelete();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDeleteMI;
}
/**
 * Return the LocalDrivesCB property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getDrivesCB() {
	if (ivjDrivesCB == null) {
		try {
			ivjDrivesCB = new javax.swing.JComboBox();
			ivjDrivesCB.setName("DrivesCB");
			ivjDrivesCB.setToolTipText("Switch local drive");
			ivjDrivesCB.setPreferredSize(new java.awt.Dimension(100, 23));
			ivjDrivesCB.setMinimumSize(new java.awt.Dimension(75, 23));
			ivjDrivesCB.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						chgLocalDrive();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDrivesCB;
}
/**
 * Return the LocalDrivesLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getDrivesLbl() {
	if (ivjDrivesLbl == null) {
		try {
			ivjDrivesLbl = new javax.swing.JLabel();
			ivjDrivesLbl.setName("DrivesLbl");
			ivjDrivesLbl.setIconTextGap(20);
			ivjDrivesLbl.setText("Drives:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDrivesLbl;
}
/**
 * Return the LocalSortTM property value.
 * @return oem.edge.ed.odc.ftp.client.FileTableSorter
 */
private FileTableSorter getFileSortTM() {
	if (ivjFileSortTM == null) {
		try {
			ivjFileSortTM = new oem.edge.ed.odc.ftp.client.FileTableSorter();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileSortTM;
}
/**
 * Return the LocalSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getFileSP() {
	if (ivjFileSP == null) {
		try {
			ivjFileSP = new javax.swing.JScrollPane();
			ivjFileSP.setName("FileSP");
			ivjFileSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjFileSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjFileSP.setRequestFocusEnabled(false);
			getFileSP().setViewportView(getFileTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileSP;
}
/**
 * Return the LocalTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getFileTbl() {
	if (ivjFileTbl == null) {
		try {
			ivjFileTbl = new javax.swing.JTable();
			ivjFileTbl.setName("FileTbl");
			getFileSP().setColumnHeaderView(ivjFileTbl.getTableHeader());
			getFileSP().getViewport().setBackingStoreEnabled(true);
			ivjFileTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjFileTbl.setBounds(0, 0, 200, 200);
			ivjFileTbl.setShowVerticalLines(false);
			ivjFileTbl.setShowHorizontalLines(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileTbl;
}
/**
 * Return the LocalTM property value.
 * @return oem.edge.ed.odc.ftp.client.FileTableModel
 */
private FileTableModel getFileTM() {
	if (ivjFileTM == null) {
		try {
			ivjFileTM = new oem.edge.ed.odc.ftp.client.FileTableModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileTM;
}
/**
 * Return the FolderCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getFolderCanBtn() {
	if (ivjFolderCanBtn == null) {
		try {
			ivjFolderCanBtn = new javax.swing.JButton();
			ivjFolderCanBtn.setName("FolderCanBtn");
			ivjFolderCanBtn.setText("Cancel");
			ivjFolderCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getFolderDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFolderCanBtn;
}
/**
 * Return the FolderDlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getFolderDlg() {
	if (ivjFolderDlg == null) {
		try {
			ivjFolderDlg = new javax.swing.JDialog();
			ivjFolderDlg.setName("FolderDlg");
			ivjFolderDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjFolderDlg.setBounds(19, 543, 251, 131);
			ivjFolderDlg.setModal(true);
			ivjFolderDlg.setTitle("Create Local Folder");
			getFolderDlg().setContentPane(getJDialogContentPane1());
			ivjFolderDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowOpened(java.awt.event.WindowEvent e) {    
					try {
						getFolderTF().requestFocus();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFolderDlg;
}
/**
 * Return the FolderLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getFolderLbl() {
	if (ivjFolderLbl == null) {
		try {
			ivjFolderLbl = new javax.swing.JLabel();
			ivjFolderLbl.setName("FolderLbl");
			ivjFolderLbl.setText("Folder Name:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFolderLbl;
}
/**
 * Return the FolderOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getFolderOkBtn() {
	if (ivjFolderOkBtn == null) {
		try {
			ivjFolderOkBtn = new javax.swing.JButton();
			ivjFolderOkBtn.setName("FolderOkBtn");
			ivjFolderOkBtn.setText("Ok");
			ivjFolderOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						createFolder();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFolderOkBtn;
}
/**
 * Return the FolderTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getFolderTF() {
	if (ivjFolderTF == null) {
		try {
			ivjFolderTF = new javax.swing.JTextField();
			ivjFolderTF.setName("FolderTF");
			ivjFolderTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						createFolder();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFolderTF;
}
/**
 * Return the LocalHomeBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getHomeBtn() {
	if (ivjHomeBtn == null) {
		try {
			ivjHomeBtn = new javax.swing.JButton();
			ivjHomeBtn.setName("HomeBtn");
			ivjHomeBtn.setToolTipText("Local Home");
			ivjHomeBtn.setText("");
			ivjHomeBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjHomeBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjHomeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/home.gif")));
			ivjHomeBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjHomeBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						chgDirToHome();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjHomeBtn;
}
/**
 * Return the HomeMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getHomeMI() {
	if (ivjHomeMI == null) {
		try {
			ivjHomeMI = new javax.swing.JMenuItem();
			ivjHomeMI.setName("HomeMI");
			ivjHomeMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/home.gif")));
			ivjHomeMI.setMnemonic('h');
			ivjHomeMI.setText("Home");
			ivjHomeMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,InputEvent.CTRL_MASK,false));
			ivjHomeMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						chgDirToHome();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjHomeMI;
}
/**
 * Return the JDialogContentPane1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJDialogContentPane1() {
	if (ivjJDialogContentPane1 == null) {
		try {
			ivjJDialogContentPane1 = new javax.swing.JPanel();
			ivjJDialogContentPane1.setName("JDialogContentPane1");
			ivjJDialogContentPane1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFolderLbl = new java.awt.GridBagConstraints();
			constraintsFolderLbl.gridx = 0; constraintsFolderLbl.gridy = 0;
			constraintsFolderLbl.insets = new java.awt.Insets(10, 10, 0, 5);
			getJDialogContentPane1().add(getFolderLbl(), constraintsFolderLbl);

			java.awt.GridBagConstraints constraintsFolderTF = new java.awt.GridBagConstraints();
			constraintsFolderTF.gridx = 1; constraintsFolderTF.gridy = 0;
			constraintsFolderTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFolderTF.weightx = 1.0;
			constraintsFolderTF.insets = new java.awt.Insets(10, 0, 0, 10);
			getJDialogContentPane1().add(getFolderTF(), constraintsFolderTF);

			java.awt.GridBagConstraints constraintsJPanel = new java.awt.GridBagConstraints();
			constraintsJPanel.gridx = 0; constraintsJPanel.gridy = 1;
			constraintsJPanel.gridwidth = 0;
			constraintsJPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsJPanel.weightx = 1.0;
			constraintsJPanel.weighty = 1.0;
			constraintsJPanel.insets = new java.awt.Insets(10, 10, 10, 10);
			getJDialogContentPane1().add(getJPanel(), constraintsJPanel);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane1;
}
/**
 * Return the JPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel() {
	if (ivjJPanel == null) {
		try {
			ivjJPanel = new javax.swing.JPanel();
			ivjJPanel.setName("JPanel");
			ivjJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFolderOkBtn = new java.awt.GridBagConstraints();
			constraintsFolderOkBtn.gridx = 0; constraintsFolderOkBtn.gridy = 0;
			constraintsFolderOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanel().add(getFolderOkBtn(), constraintsFolderOkBtn);

			java.awt.GridBagConstraints constraintsFolderCanBtn = new java.awt.GridBagConstraints();
			constraintsFolderCanBtn.gridx = 1; constraintsFolderCanBtn.gridy = 0;
			constraintsFolderCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getJPanel().add(getFolderCanBtn(), constraintsFolderCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel;
}
/**
 * Return the LocalLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLocalLbl() {
	if (ivjLocalLbl == null) {
		try {
			ivjLocalLbl = new javax.swing.JLabel();
			ivjLocalLbl.setName("LocalLbl");
			ivjLocalLbl.setText("Local Files");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalLbl;
}
/**
 * Return the LocalM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getLocalM() {
	if (ivjLocalM == null) {
		try {
			ivjLocalM = new javax.swing.JMenu();
			ivjLocalM.setName("LocalM");
			ivjLocalM.setText("Local");
			ivjLocalM.add(getHomeMI());
			ivjLocalM.add(getUpMI());
			ivjLocalM.add(getRefreshMI());
			ivjLocalM.add(getSeparator1());
			ivjLocalM.add(getNewDirMI());
			ivjLocalM.add(getDeleteMI());
			ivjLocalM.add(getSeparator2());
			ivjLocalM.add(getUploadMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalM;
}
/**
 * Gets the localMenu property (javax.swing.JMenu) value.
 * @return The localMenu property value.
 * @see #setLocalMenu
 */
public javax.swing.JMenu getLocalMenu() {
	return fieldLocalMenu;
}
/**
 * Method generated to support the promotion of the localSortTM attribute.
 * @return oem.edge.ed.odc.ftp.client.FileTableSorter
 */
public FileTableSorter getLocalSortTM() {
	return getFileSortTM();
}
/**
 * Method generated to support the promotion of the localTbl attribute.
 * @return javax.swing.JTable
 */
public javax.swing.JTable getLocalTbl() {
	return getFileTbl();
}
/**
 * Method generated to support the promotion of the localTM attribute.
 * @return oem.edge.ed.odc.ftp.client.FileTableModel
 */
public FileTableModel getLocalTM() {
	return getFileTM();
}
/**
 * Return the LocalNewDirBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getNewDirBtn() {
	if (ivjNewDirBtn == null) {
		try {
			ivjNewDirBtn = new javax.swing.JButton();
			ivjNewDirBtn.setName("NewDirBtn");
			ivjNewDirBtn.setToolTipText("New Local Folder");
			ivjNewDirBtn.setText("");
			ivjNewDirBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjNewDirBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjNewDirBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/newfold.gif")));
			ivjNewDirBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjNewDirBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						doCreate();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNewDirBtn;
}
/**
 * Return the CreateMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getNewDirMI() {
	if (ivjNewDirMI == null) {
		try {
			ivjNewDirMI = new javax.swing.JMenuItem();
			ivjNewDirMI.setName("NewDirMI");
			ivjNewDirMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/newfold.gif")));
			ivjNewDirMI.setMnemonic('n');
			ivjNewDirMI.setText("New folder");
			ivjNewDirMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT,InputEvent.CTRL_MASK,false));
			ivjNewDirMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						doCreate();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNewDirMI;
}
/**
 * Return the LocalPathLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPathLbl() {
	if (ivjPathLbl == null) {
		try {
			ivjPathLbl = new javax.swing.JLabel();
			ivjPathLbl.setName("PathLbl");
			ivjPathLbl.setText("Path:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPathLbl;
}
/**
 * Return the LocalPathPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getPathPnl() {
	if (ivjPathPnl == null) {
		try {
			ivjPathPnl = new javax.swing.JPanel();
			ivjPathPnl.setName("PathPnl");
			ivjPathPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsPathLbl = new java.awt.GridBagConstraints();
			constraintsPathLbl.gridx = 0; constraintsPathLbl.gridy = 0;
			constraintsPathLbl.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsPathLbl.insets = new java.awt.Insets(0, 0, 0, 5);
			getPathPnl().add(getPathLbl(), constraintsPathLbl);

			java.awt.GridBagConstraints constraintsPathTF = new java.awt.GridBagConstraints();
			constraintsPathTF.gridx = 1; constraintsPathTF.gridy = 0;
			constraintsPathTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPathTF.weightx = 1.0;
			getPathPnl().add(getPathTF(), constraintsPathTF);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPathPnl;
}
/**
 * Return the LocalPathTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getPathTF() {
	if (ivjPathTF == null) {
		try {
			ivjPathTF = new javax.swing.JTextField();
			ivjPathTF.setName("PathTF");
			ivjPathTF.setToolTipText("Enter a local directory");
			ivjPathTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						chgLocalDir();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}   
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPathTF;
}
/**
 * Return the LocalRefreshBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getRefreshBtn() {
	if (ivjRefreshBtn == null) {
		try {
			ivjRefreshBtn = new javax.swing.JButton();
			ivjRefreshBtn.setName("RefreshBtn");
			ivjRefreshBtn.setToolTipText("Refresh Local");
			ivjRefreshBtn.setText("");
			ivjRefreshBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjRefreshBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjRefreshBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/refresh.gif")));
			ivjRefreshBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjRefreshBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						refresh();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}   
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRefreshBtn;
}
/**
 * Return the RefreshMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getRefreshMI() {
	if (ivjRefreshMI == null) {
		try {
			ivjRefreshMI = new javax.swing.JMenuItem();
			ivjRefreshMI.setName("RefreshMI");
			ivjRefreshMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/refresh.gif")));
			ivjRefreshMI.setMnemonic('r');
			ivjRefreshMI.setText("Refresh");
			ivjRefreshMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,InputEvent.CTRL_MASK,false));
			ivjRefreshMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						refresh();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRefreshMI;
}
/**
 * Return the Separator1 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getSeparator1() {
	if (ivjSeparator1 == null) {
		try {
			ivjSeparator1 = new javax.swing.JSeparator();
			ivjSeparator1.setName("Separator1");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSeparator1;
}
/**
 * Return the Separator2 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getSeparator2() {
	if (ivjSeparator2 == null) {
		try {
			ivjSeparator2 = new javax.swing.JSeparator();
			ivjSeparator2.setName("Separator2");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSeparator2;
}
/**
 * Return the LocalUpBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getUpBtn() {
	if (ivjUpBtn == null) {
		try {
			ivjUpBtn = new javax.swing.JButton();
			ivjUpBtn.setName("UpBtn");
			ivjUpBtn.setToolTipText("Change to parent directory");
			ivjUpBtn.setText("");
			ivjUpBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjUpBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjUpBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/upfolder.gif")));
			ivjUpBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjUpBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						chgDirToParent();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUpBtn;
}
/**
 * Return the UploadBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getUploadBtn() {
	if (ivjUploadBtn == null) {
		try {
			ivjUploadBtn = new javax.swing.JButton();
			ivjUploadBtn.setName("UploadBtn");
			ivjUploadBtn.setToolTipText("Add selected files to current package");
			ivjUploadBtn.setText("");
			ivjUploadBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/upload.gif")));
			ivjUploadBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjUploadBtn.setEnabled(false);
			ivjUploadBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						fireUploadBtn_actionPerformed(new java.util.EventObject(this));
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUploadBtn;
}
/**
 * Method generated to support the promotion of the uploadBtnToolTipText attribute.
 * @return java.lang.String
 */
public java.lang.String getUploadBtnToolTipText() {
	return getUploadBtn().getToolTipText();
}
/**
 * Gets the uploadBtnVisible property (boolean) value.
 * @return The uploadBtnVisible property value.
 * @see #setUploadBtnVisible
 */
public boolean getUploadBtnVisible() {
	return getUploadBtn().isVisible();
}
/**
 * Return the UploadMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getUploadMI() {
	if (ivjUploadMI == null) {
		try {
			ivjUploadMI = new javax.swing.JMenuItem();
			ivjUploadMI.setName("UploadMI");
			ivjUploadMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/upload.gif")));
			ivjUploadMI.setMnemonic('a');
			ivjUploadMI.setText("Add files to package");
			ivjUploadMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_MASK,false));
			ivjUploadMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						fireUploadBtn_actionPerformed(new java.util.EventObject(this));
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUploadMI;
}
/**
 * Return the UpMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getUpMI() {
	if (ivjUpMI == null) {
		try {
			ivjUpMI = new javax.swing.JMenuItem();
			ivjUpMI.setName("UpMI");
			ivjUpMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/upfolder.gif")));
			ivjUpMI.setMnemonic('p');
			ivjUpMI.setText("Parent directory");
			ivjUpMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,InputEvent.CTRL_MASK,false));
			ivjUpMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						chgDirToParent();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUpMI;
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
 * Insert the method's description here.
 * Creation date: (3/7/2003 10:52:47 AM)
 * @param isWin boolean
 */
public void initGui(boolean isWin) {
	// Prepare the local components. they are always
	// active regardless of whether we are logging in or
	// not.

	// Setup the drives combo box.
	if (isWin) {
		for (char c = 'C'; c <= 'Z'; c++) {
			File f = new File(c + ":\\");
			if (f.exists())
				getDrivesCB().addItem(f);
		}

		File f = new File("A:\\");
		getDrivesCB().insertItemAt(f,0);
	}
	else {
		getDrivesCB().setEnabled(false);
		getDrivesLbl().setEnabled(false);
	}

	// Setup the home directory as the default.
	String dir = System.getProperty("user.home");
	getPathTF().setText(dir);
	getFileTM().setDirectory(dir);
	getFileTM().populateLocal();
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("LocalFilePnl");
		setLayout(new java.awt.GridBagLayout());
		setSize(361, 457);

		java.awt.GridBagConstraints constraintsLocalLbl = new java.awt.GridBagConstraints();
		constraintsLocalLbl.gridx = 0; constraintsLocalLbl.gridy = 0;
		constraintsLocalLbl.anchor = java.awt.GridBagConstraints.WEST;
		constraintsLocalLbl.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getLocalLbl(), constraintsLocalLbl);

		java.awt.GridBagConstraints constraintsBtnPnl = new java.awt.GridBagConstraints();
		constraintsBtnPnl.gridx = 0; constraintsBtnPnl.gridy = 1;
		constraintsBtnPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsBtnPnl.weightx = 1.0;
		constraintsBtnPnl.insets = new java.awt.Insets(0, 5, 2, 5);
		add(getBtnPnl(), constraintsBtnPnl);

		java.awt.GridBagConstraints constraintsPathPnl = new java.awt.GridBagConstraints();
		constraintsPathPnl.gridx = 0; constraintsPathPnl.gridy = 2;
		constraintsPathPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPathPnl.insets = new java.awt.Insets(0, 5, 0, 5);
		add(getPathPnl(), constraintsPathPnl);

		java.awt.GridBagConstraints constraintsFileSP = new java.awt.GridBagConstraints();
		constraintsFileSP.gridx = 0; constraintsFileSP.gridy = 3;
		constraintsFileSP.fill = java.awt.GridBagConstraints.BOTH;
		constraintsFileSP.weightx = 1.0;
		constraintsFileSP.weighty = 1.0;
		constraintsFileSP.insets = new java.awt.Insets(2, 5, 5, 5);
		add(getFileSP(), constraintsFileSP);

		getFileTbl().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				try {
					fileListSelectionChg();
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
		});

		getFileSortTM().setModel(getFileTM());
		getFileTbl().setModel(getFileSortTM());
		getFileTbl().createDefaultColumnsFromModel();

		// Setup the local table.
		getFileTbl().getTableHeader().setReorderingAllowed(false);
		TableColumnModel tm = getFileTbl().getColumnModel();
		FileHeaderRenderer hr = new FileHeaderRenderer();
		hr.setToolTipText("Click to sort");
		hr.setHorizontalAlignment(JLabel.CENTER);
		FileCellRenderer cr = new FileCellRenderer();
		TableColumn tc = tm.getColumn(0);
		tc.setCellRenderer(cr);
		//tc.setHeaderRenderer(hr);
		tc.setPreferredWidth(20);
		tc = tm.getColumn(1);
		tc.setCellRenderer(cr);
		tc.setHeaderRenderer(hr);
		tc.setPreferredWidth(200);
		tc = tm.getColumn(2);
		tc.setCellRenderer(cr);
		tc.setHeaderRenderer(hr);
		tc.setPreferredWidth(75);
		tc = tm.getColumn(3);
		tc.setCellRenderer(cr);
		tc.setHeaderRenderer(hr);
		tc.setPreferredWidth(125);

		getFileSortTM().addMouseListenerToHeaderInTable(getFileTbl());

		// Setup a MouseAdapter to handle double clicks on the file tables.
		MouseAdapter ma = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
					tableAction();
				}
			}
		};
		getFileTbl().addMouseListener(ma);

		// Prepare to listen for changes on the folder dialog.
		getFolderTF().getDocument().addDocumentListener(this);

		// Fix the background color of the viewport to match the table.
		getFileSP().getViewport().setBackground(getFileTbl().getBackground());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2002 12:01:40 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void insertUpdate(DocumentEvent e) {
	if (e.getDocument() == getFolderTF().getDocument())
		textChgFolder();
}
/**
 * Insert the method's description here.
 * Creation date: (3/7/2003 8:56:52 AM)
 * @return boolean
 */
public boolean isAllowDirs() {
	return allowDirs;
}
/**
 * Insert the method's description here.
 * Creation date: (3/7/2003 8:56:52 AM)
 * @return boolean
 */
public boolean isLoggedIn() {
	return loggedIn;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LocalFilePnl aLocalFilePnl;
		aLocalFilePnl = new LocalFilePnl();
		frame.setContentPane(aLocalFilePnl);
		frame.setSize(aLocalFilePnl.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void refresh() {
	busyCursor(true);

	// Update the path field and cause the list to go blank.
	getFileTbl().clearSelection();
	String dir = getFileTM().getDirectory();
	getPathTF().setText(dir);
	getFileTM().setDirectory(dir);
	getFileTM().populateLocal();

	busyCursor(false);
}
/**
 * 
 * @param newListener oem.edge.ed.odc.ftp.client.LocalFilePnlListener
 */
public void removeLocalFilePnlListener(oem.edge.ed.odc.ftp.client.LocalFilePnlListener newListener) {
	fieldLocalFilePnlListenerEventMulticaster = oem.edge.ed.odc.ftp.client.LocalFilePnlListenerEventMulticaster.remove(fieldLocalFilePnlListenerEventMulticaster, newListener);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2002 12:01:40 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void removeUpdate(DocumentEvent e) {
	if (e.getDocument() == getFolderTF().getDocument())
		textChgFolder();
}
/**
 * Insert the method's description here.
 * Creation date: (3/7/2003 8:56:52 AM)
 * @param newLoggedIn boolean
 */
public void setAllowDirs(boolean newAllowDirs) {
	allowDirs = newAllowDirs;
	fileListSelectionChg();
}
/**
 * Insert the method's description here.
 * Creation date: (3/7/2003 8:56:52 AM)
 * @param newLoggedIn boolean
 */
public void setDirectory(String dir) throws IOException {
	if (dir != null && (dir = dir.trim()).length() > 0) {
		File f = new File(dir);
		if (f.exists()) {
			if (f.isDirectory()) {
				busyCursor(true);
				getFileTbl().clearSelection();
				getFileTM().setDirectory(dir);
				getFileTM().populateLocal();
				getPathTF().setText(dir);
				busyCursor(false);
			}
			else
				throw new IOException(getName() + " is not a directory!");
		}
		else
			throw new IOException(f.getName() + "does not exist");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2003 10:09:15 AM)
 * @param menu javax.swing.JMenu
 */
public void setLocalMenu(JMenu menu) {
	if (fieldLocalMenu != null) {
		fieldLocalMenu.removeAll();
		getLocalM().add(getHomeMI());
		getLocalM().add(getUpMI());
		getLocalM().add(getRefreshMI());
		getLocalM().add(getSeparator1());
		getLocalM().add(getNewDirMI());
		getLocalM().add(getDeleteMI());
		getLocalM().add(getSeparator2());
		getLocalM().add(getUploadMI());
	}

	fieldLocalMenu = menu;

	if (fieldLocalMenu != null) {
		getLocalM().removeAll();
		fieldLocalMenu.add(getHomeMI());
		fieldLocalMenu.add(getUpMI());
		fieldLocalMenu.add(getRefreshMI());
		fieldLocalMenu.add(getSeparator1());
		fieldLocalMenu.add(getNewDirMI());
		fieldLocalMenu.add(getDeleteMI());
		fieldLocalMenu.add(getSeparator2());
		fieldLocalMenu.add(getUploadMI());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/7/2003 8:56:52 AM)
 * @param newLoggedIn boolean
 */
public void setLoggedIn(boolean newLoggedIn) {
	loggedIn = newLoggedIn;
	fileListSelectionChg();
}
/**
 * Method generated to support the promotion of the uploadBtnToolTipText attribute.
 * @param arg1 java.lang.String
 */
public void setUploadBtnToolTipText(java.lang.String arg1) {
	getUploadBtn().setToolTipText(arg1);
}
/**
 * Sets the uploadBtnVisible property (boolean) value.
 * @param uploadBtnVisible The new value for the property.
 * @see #getUploadBtnVisible
 */
public void setUploadBtnVisible(boolean uploadBtnVisible) {
	getUploadBtn().setVisible(uploadBtnVisible);
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2002 3:25:43 PM)
 * @param table javax.swing.JTable
 */
public void tableAction() {
	int i = getFileTbl().getSelectedRowCount();
	if (i == 1) {
		busyCursor(true);
		i = getFileTbl().getSelectedRow();
		String name = (String) getFileTbl().getModel().getValueAt(i,1);
		Boolean isDir = (Boolean) getFileTbl().getModel().getValueAt(i,0);

		if (isDir.booleanValue()) {
			getFileTbl().clearSelection();
			String dir;
			if (getFileTM().getDirectory().endsWith(File.separator))
				dir = getFileTM().getDirectory() + name;
			else
				dir = getFileTM().getDirectory() + File.separator + name;
			getPathTF().setText(dir);
			getFileTM().setDirectory(dir);
			getFileTM().populateLocal();
		}
		else if (isLoggedIn()) {
			fireUploadBtn_actionPerformed(new ActionEvent(this,0,null));
		}
		busyCursor(false);
	}
	else {
		System.out.println("User doubled clicked table and " + i + " items were selected as a result");
	}
}
/**
 * Comment
 */
public void textChgFolder() {
	String folder = getFolderTF().getText();

	getFolderOkBtn().setEnabled(folder != null && folder.length() > 0);
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
