package oem.edge.ed.odc.ftp.client;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import oem.edge.ed.odc.dsmp.client.FileCellRenderer;
import oem.edge.ed.odc.dsmp.client.FileHeaderRenderer;
import oem.edge.ed.odc.dsmp.client.FileStatusDirectionRenderer;
import oem.edge.ed.odc.dsmp.client.FileStatusRenderer;
import oem.edge.ed.odc.dsmp.client.FileTableModel;
import oem.edge.ed.odc.dsmp.client.FileTableSorter;
import oem.edge.ed.odc.dsmp.common.DSMPBaseProto;
import oem.edge.ed.odc.ftp.common.FTPGenerator;
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

/**
 * Insert the type's description here.
 * Creation date: (10/3/2002 1:29:26 PM)
 * @author: Mike Zarnick
 */
public class FileTransfer extends JFrame implements DocumentListener, FTPListener {
	private boolean isWin = false;
	private boolean isTunnel = false;
	private boolean isExit = false;
	private FTPDispatcher dispatcher = null;
	private String serverName = "localhost";
	private int serverPort = 5050;
	private Socket serverSocket = null;
	public static JFrame initDlg;
	public static JLabel initLbl;
	private JMenuBar ivjFileTransferJMenuBar = null;
	private JPanel ivjJFrameContentPane = null;
	private JLabel ivjLocalLbl = null;
	private JLabel ivjLocalPathLbl = null;
	private JTextField ivjLocalPathTF = null;
	private JLabel ivjRemoteLbl = null;
	private JLabel ivjRemotePathLbl = null;
	private JTextField ivjRemotePathTF = null;
	private JScrollPane ivjLocalSP = null;
	private JScrollPane ivjRemoteSP = null;
	private JPanel ivjLocalPnl = null;
	private JTable ivjLocalTbl = null;
	private JPanel ivjRemotePnl = null;
	private JTable ivjRemoteTbl = null;
	private FileTableModel ivjLocalTM = null;  // @jve:visual-info  decl-index=0 visual-constraint="827,402"
	private FileTableModel ivjRemoteTM = null;  // @jve:visual-info  decl-index=0 visual-constraint="825,239"
	private JSplitPane ivjHostSplit = null;
	private JScrollPane ivjStatusSP = null;
	private JSplitPane ivjStatusSplit = null;
	private JTable ivjStatusTbl = null;
	private FileStatusTableModel ivjFileStatusTM = null;  // @jve:visual-info  decl-index=0 visual-constraint="712,537"
	private JComboBox ivjLocalDrivesCB = null;
	private JLabel ivjLocalDrivesLbl = null;
	private FileTableSorter ivjLocalSortTM = null;  // @jve:visual-info  decl-index=0 visual-constraint="724,402"
	private FileTableSorter ivjRemoteSortTM = null;  // @jve:visual-info  decl-index=0 visual-constraint="730,239"
	private JButton ivjDownloadBtn = null;
	private JMenuItem ivjExitMI = null;
	private JPanel ivjJPanel1 = null;
	private JPanel ivjLocalBtnPnl = null;
	private JButton ivjLocalDeleteBtn = null;
	private JButton ivjLocalHomeBtn = null;
	private JButton ivjLocalNewDirBtn = null;
	private JButton ivjLocalRefreshBtn = null;
	private JButton ivjLocalUpBtn = null;
	private JMenuItem ivjLogoutMI = null;
	private JButton ivjUploadBtn = null;
	private JMenu ivjFileM = null;  // @jve:visual-info  decl-index=0 visual-constraint="690,20"
	private JSeparator ivjFileSep = null;
	private JButton ivjCopyTB = null;
	private JButton ivjLoginTB = null;
	private JButton ivjLogoutTB = null;
	private JButton ivjPasteTB = null;
	private JButton ivjStopTB = null;
	private JMenuItem ivjLoginMI = null;
	private JToolBar ivjToolBar = null;
	private JButton ivjRemoteDeleteBtn = null;
	private JButton ivjRemoteHomeBtn = null;
	private JButton ivjRemoteNewDirBtn = null;
	private JButton ivjRemoteRefreshBtn = null;
	private JButton ivjRemoteUpBtn = null;
	private JPanel ivjJDialogContentPane = null;
	private JPanel ivjJPanel2 = null;
	private JDialog ivjLoginDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="44,913"
	private JLabel ivjUserLbl = null;
	private JLabel ivjPwdLbl = null;
	private JPasswordField ivjPwdTF = null;
	private JTextField ivjUserTF = null;
	private JButton ivjLoginCanBtn = null;
	private JButton ivjLoginOkBtn = null;
	private JButton ivjFolderCanBtn = null;
	private JDialog ivjFolderDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="473,915"
	private JLabel ivjFolderLbl = null;
	private JButton ivjFolderOkBtn = null;
	private JTextField ivjFolderTF = null;
	private JPanel ivjJDialogContentPane1 = null;
	private JPanel ivjJPanel = null;
	private JMenuItem ivjCanXferMI = null;
	private JMenuItem ivjDetailsMI = null;
	private JSeparator ivjJSeparator1 = null;
	private JSeparator ivjJSeparator2 = null;
	private JMenuItem ivjRemoveAllMI = null;
	private JMenuItem ivjRemoveMI = null;
	private JPopupMenu ivjStatusPU = null;  // @jve:visual-info  decl-index=0 visual-constraint="717,720"
	private JFrame ivjInitDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="47,1178"
	private JLabel ivjInitLbl = null;
	private JPanel ivjJFrameContentPane1 = null;
	private JCheckBoxMenuItem ivjDebugMI = null;
	private JSeparator ivjJSeparator3 = null;
/**
 * FileTransfer constructor comment.
 */
public FileTransfer() {
	super();
	initialize();
}
/**
 * FileTransfer constructor comment.
 * @param title java.lang.String
 */
public FileTransfer(String title) {
	super(title);
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:52:26 PM)
 * @param args java.lang.String[]
 */
public void begin(String[] args) {
	// Determine our platform.
	isWin = (System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1);
	String token = null;

	if (args.length == 0) {
		try {
			BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
			serverName = rdr.readLine();

			serverPort = Integer.parseInt(rdr.readLine());

			token = rdr.readLine();
		}
		catch (IOException e) {
			System.out.println("Need address and port of server.");
			System.out.println("FileTransfer host port token");
			System.exit(1);
		}
	}
	else {
		if (args.length < 2) {
			System.out.println("Need address and port of server.");
			System.out.println("FileTransfer host port");
			System.exit(1);
		}

		serverName = args[0];

		try {
			serverPort = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e) {
			System.out.println("Port is not a valid integer.");
			System.out.println("FileTransfer host port");
			System.exit(1);
		}
	}

	if (token != null) {
		isTunnel = true;
		getFileM().remove(getLoginMI());
		getFileM().remove(getLogoutMI());
		getFileM().remove(getFileSep());
		getToolBar().remove(getLoginTB());
		getToolBar().remove(getLogoutTB());
	}

	resetToLoggedOut();

	// Prepare the local components. they are always
	// active regardless of whether we are logging in or
	// not.
	String dir = System.getProperty("user.home");
	getLocalPathTF().setText(dir);
	getLocalTM().setDirectory(dir);
	getLocalTM().populateLocal();

	// Setup the drives combo box.
	if (isWin) {
		for (char c = 'C'; c <= 'Z'; c++) {
			File f = new File(c + ":\\");
			if (f.exists())
				getLocalDrivesCB().addItem(f);
		}

		File f = new File("A:\\");
		getLocalDrivesCB().insertItemAt(f,0);
	}
	else {
		getLocalDrivesCB().setEnabled(false);
		getLocalDrivesLbl().setEnabled(false);
	}

	// Hide the initializing window...
	initDlg.setVisible(false);

	// Center this frame on the screen.
	Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension winSize = getSize();

	int x = (scrSize.width - winSize.width) / 2;
	int y = (scrSize.height - winSize.height) / 2;

	int dx = x + winSize.width - scrSize.width;
	if (dx > 0)
		x -= dx;

	if (x < 0)
		x = 0;

	int dy = y + winSize.height - scrSize.height;
	if (dy > 0)
		y -= dy;

	if (y < 0)
		y = 0;

	setLocation(x,y);
	setVisible(true);
	getHostSplit().setDividerLocation(300);

	if (token != null) {
		try {
			dispatcher = new FTPDispatcher(serverName,serverPort);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to connect to socket provided.");
			JOptionPane.showMessageDialog(this,"Bad server information.","No connection",JOptionPane.ERROR_MESSAGE);
			return;
		}

		dispatcher.addFTPListener(this);

		DSMPBaseProto p = FTPGenerator.loginToken((byte) 0,token);
		dispatcher.dispatchProtocol(p);
		setTitle("File Transfer - Connecting...");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:52:26 PM)
 * @param args java.lang.String[]
 */
public void begin(Socket s) {
	// Determine our platform.
	isWin = (System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1);

	isTunnel = true;
	serverSocket = s;

	resetToLoggedOut();

	// Prepare the local components. they are always
	// active regardless of whether we are logging in or
	// not.
	String dir = System.getProperty("user.home");
	getLocalPathTF().setText(dir);
	getLocalTM().setDirectory(dir);
	getLocalTM().populateLocal();

	// Setup the drives combo box.
	if (isWin) {
		for (char c = 'C'; c <= 'Z'; c++) {
			File f = new File(c + ":\\");
			if (f.exists())
				getLocalDrivesCB().addItem(f);
		}

		File f = new File("A:\\");
		getLocalDrivesCB().insertItemAt(f,0);
	}
	else {
		getLocalDrivesCB().setEnabled(false);
		getLocalDrivesLbl().setEnabled(false);
	}

	// Hide the initializing window...
	initDlg.setVisible(false);

	// Center this frame on the screen.
	Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension winSize = getSize();

	int x = (scrSize.width - winSize.width) / 2;
	int y = (scrSize.height - winSize.height) / 2;

	int dx = x + winSize.width - scrSize.width;
	if (dx > 0)
		x -= dx;

	if (x < 0)
		x = 0;

	int dy = y + winSize.height - scrSize.height;
	if (dy > 0)
		y -= dy;

	if (y < 0)
		y = 0;

	setLocation(x,y);
	setVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 1:53:12 PM)
 * @param on boolean
 */
public void busyCursor(boolean on) {
	if (on) {
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
public void cancelXfer() {
	busyCursor(true);
	getFileStatusTM().cancel(getStatusTbl().getSelectedRows());
	getStopTB().setEnabled(false);
	busyCursor(false);
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2002 12:01:40 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void changedUpdate(DocumentEvent e) {
	if (e.getDocument() == getUserTF().getDocument())
		textChgLogin();
	if (e.getDocument() == getPwdTF().getDocument())
		textChgLogin();
}
/**
 * Comment
 */
public void chgDirToHome(ActionEvent e) {
	busyCursor(true);
	if (e.getSource() == getLocalHomeBtn()) {
		getLocalTbl().clearSelection();
		String dir = System.getProperty("user.home");
		getLocalPathTF().setText(dir);
		getLocalTM().setDirectory(dir);
		getLocalTM().populateLocal();
		busyCursor(false);
	}
	else {
		getRemoteTbl().clearSelection();
		getRemotePathTF().setText(dispatcher.remoteHome);
		getRemoteTM().setDirectory(dispatcher.remoteHome);
		DSMPBaseProto p = FTPGenerator.changeArea((byte) 0,dispatcher.remoteHome);
		dispatcher.dispatchProtocol(p);
	}
}
/**
 * Comment
 */
public void chgDirToParent(ActionEvent e) {
	busyCursor(true);
	if (e.getSource() == getLocalUpBtn()) {
		File dir = new File(getLocalTM().getDirectory());
		File parent = new File(dir.getParent());
		if (parent != null) {
			getLocalTbl().clearSelection();
			getLocalTM().setDirectory(parent.getPath());
			getLocalPathTF().setText(parent.getPath());
			getLocalTM().populateLocal();
		}
		busyCursor(false);
	}
	else {
		String dir = getRemoteTM().getDirectory();
		int i = dir.lastIndexOf(dispatcher.remoteSeparator);
		if (i >= 0) {
			getRemoteTbl().clearSelection();
			String parent;
			if (i > 0)
				parent = dir.substring(0,i);
			else
				parent = dir.substring(0,1);
			getRemoteTM().setDirectory(parent);
			getRemotePathTF().setText(parent);
			DSMPBaseProto p = FTPGenerator.changeArea((byte) 0,parent);
			dispatcher.dispatchProtocol(p);
		}
		else
			busyCursor(false);
	}
}
/**
 * Comment
 */
public void chgLocalDir() {
	String dir = getLocalPathTF().getText();

	if (dir != null && (dir = dir.trim()).length() > 0) {
		File f = new File(dir);
		if (f.exists()) {
			if (f.isDirectory()) {
				busyCursor(true);
				getLocalTbl().clearSelection();
				getLocalTM().setDirectory(dir);
				getLocalTM().populateLocal();
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
	File f = (File) getLocalDrivesCB().getSelectedItem();

	if (f.exists()) {
		busyCursor(true);
		getLocalTbl().clearSelection();
		getLocalPathTF().setText(f.toString());
		getLocalTM().setDirectory(f.toString());
		getLocalTM().populateLocal();
		busyCursor(false);
	}
	else
		JOptionPane.showMessageDialog(this,f.getPath(),"Drive Not Found",JOptionPane.ERROR_MESSAGE);
}
/**
 * Comment
 */
public void chgRemoteDir() {
	String dir = getRemotePathTF().getText();

	if (dir != null && (dir = dir.trim()).length() > 0) {
		busyCursor(true);
		getRemoteTbl().clearSelection();
		getRemoteTM().setDirectory(dir);
		DSMPBaseProto p = FTPGenerator.changeArea((byte) 0,dir);
		dispatcher.dispatchProtocol(p);
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

	if (getFolderDlg().getTitle().equals("Create Local Folder")) {
		// Create the folder
		File f = new File(getLocalTM().getDirectory(),getFolderTF().getText().trim());
		if (! f.mkdir()) {
			busyCursor(false);
			JOptionPane.showMessageDialog(this,"Unable to create directory","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update data model and make directory selected.
		getLocalTM().populateLocal();
		int i = getLocalTM().getFileIndex(f.getName());
		if (i != -1)
			i = getLocalSortTM().getSortedIndex(i);
		if (i != -1) {
			getLocalTbl().setRowSelectionInterval(i,i);
			getLocalTbl().scrollRectToVisible(getLocalTbl().getCellRect(i,0,false));
		}
		busyCursor(false);
	}
	else {
		// Send the protocol to create the folder.
		DSMPBaseProto p = FTPGenerator.newFolder((byte) 0,getFolderTF().getText().trim());
		dispatcher.dispatchProtocol(p);
	}
}
/**
 * Comment
 */
public void doCreate(ActionEvent e) {
	if (e.getSource() == getLocalNewDirBtn())
		getFolderDlg().setTitle("Create Local Folder");
	else
		getFolderDlg().setTitle("Create Remote Folder");
	getFolderDlg().setLocationRelativeTo(this);
	getFolderDlg().setVisible(true);
}
/**
 * Comment
 */
public void doDebug() {
	if (dispatcher != null) {
		dispatcher.setDebug(getDebugMI().isSelected());
	}
}
/**
 * Comment
 */
public void doDelete(ActionEvent e) {
	int result;
	if (e.getSource() == getLocalDeleteBtn())
		result = JOptionPane.showConfirmDialog(this,"Delete local files?","Confirm Delete",JOptionPane.YES_NO_OPTION);
	else
		result = JOptionPane.showConfirmDialog(this,"Delete remote files?","Confirm Delete",JOptionPane.YES_NO_OPTION);

	if (result == JOptionPane.YES_OPTION) {
		busyCursor(true);
		if (e.getSource() == getLocalDeleteBtn()) {
			// Delete the file(s)
			int[] s = getLocalTbl().getSelectedRows();
			for (int i = 0; i < s.length; i++) {
				getLocalTbl().removeRowSelectionInterval(s[i],s[i]);
				String name = (String) getLocalSortTM().getValueAt(s[i],1);
				File f = new File(getLocalTM().getDirectory(),name);
				if (! f.delete()) {
					busyCursor(false);
					JOptionPane.showMessageDialog(this,"Unable to delete " + name,"Error",JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			// Update data model and make directory selected.
			getLocalTM().populateLocal();
			busyCursor(false);
		}
		else {
			// Delete the file(s)
			int[] s = getRemoteTbl().getSelectedRows();
			for (int i = 0; i < s.length; i++) {
				getRemoteTbl().removeRowSelectionInterval(s[i],s[i]);
				String name = (String) getRemoteSortTM().getValueAt(s[i],1);
				DSMPBaseProto p = FTPGenerator.deleteFile((byte) 0, name);
				dispatcher.dispatchProtocol(p);
			}
			// Send the protocol to repopulate the list.
			DSMPBaseProto p = FTPGenerator.listArea((byte) 0);
			dispatcher.dispatchProtocol(p);
		}
	}
}
/**
 * Comment
 */
public void doExit() {
	isExit = true;

	if (getLogoutMI().isEnabled()) {
		if (JOptionPane.showConfirmDialog(this,"Logoff remote system and exit?","Close Connection",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
			return;

		doLogout();
	}
	else
		exit();
}
/**
 * Comment
 */
public void doLogin() {
	getLoginOkBtn().setEnabled(false);
	getPwdTF().setText("");
	getLoginDlg().setLocationRelativeTo(this);
	getLoginDlg().setVisible(true);
}
/**
 * Comment
 */
public void doLoginFocus() {
	if (getUserTF().getText() != null && getUserTF().getText().length() > 0)
		getPwdTF().requestFocus();
	else
		getUserTF().requestFocus();
}
/**
 * Comment
 */
public void doLogout() {
	busyCursor(true);
	getLogoutMI().setEnabled(false);
	getLogoutTB().setEnabled(false);

	dispatcher.dispatchProtocol(FTPGenerator.logout((byte) 0));
}
/**
 * Comment
 */
public void exit() {
	setVisible(false);

	// Reset the dispatcher.
	dispatcher.shutdown();
	dispatcher = null;
	getFileStatusTM().setDispatcher(null);

	if (! isTunnel)
		System.exit(0);
}
/**
 * Comment
 */
public void fileListSelectionChg(ListSelectionEvent e) {
	if (e.getSource() == getLocalTbl().getSelectionModel()) {
		int[] s = getLocalTbl().getSelectedRows();
		if (s.length == 0) {
			getLocalDeleteBtn().setEnabled(false);
			getUploadBtn().setEnabled(false);
		}
		else {
			boolean dir = false;
			for (int i = 0; i < s.length; i++) {
				Boolean isDir = (Boolean) getLocalSortTM().getValueAt(s[i],0);
				dir |= isDir.booleanValue();
			}

			getLocalDeleteBtn().setEnabled(! dir);
			if (dispatcher != null)
				getUploadBtn().setEnabled(! dir);
			else
				getUploadBtn().setEnabled(false);
		}
	}
	else {
		int[] s = getRemoteTbl().getSelectedRows();
		if (s.length == 0) {
			getRemoteDeleteBtn().setEnabled(false);
			getDownloadBtn().setEnabled(false);
		}
		else {
			boolean dir = false;
			for (int i = 0; i < s.length; i++) {
				Boolean isDir = (Boolean) getRemoteSortTM().getValueAt(s[i],0);
				dir |= isDir.booleanValue();
			}

			getRemoteDeleteBtn().setEnabled(! dir);
			getDownloadBtn().setEnabled(! dir);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 1:04:39 PM)
 * @param e FTPEvent
 */
public void ftpAction(FTPEvent e) {
	if (e.isLogin()) {
		resetToLoggedIn();
		getRemotePathTF().setText(e.area);
		getRemoteTM().setDirectory(e.area);
		dispatcher.remoteSeparator = e.separator;
		dispatcher.remoteHome = e.area;
		DSMPBaseProto p = FTPGenerator.listArea((byte) 0);
		dispatcher.dispatchProtocol(p);
	}
	else if (e.isLoginFailed()) {
		busyCursor(false);
		JOptionPane.showMessageDialog(this,e.message,"Login Failed",JOptionPane.ERROR_MESSAGE);
		resetToLoggedOut();
	}
	else if (e.isListArea()) {
		getRemoteTM().populateRemote(e.listdata);

		if (e.handle != 0) {
			int i = getRemoteTM().getFileIndex(getFolderTF().getText().trim());
			if (i != -1)
				i = getRemoteSortTM().getSortedIndex(i);
			if (i != -1) {
				getRemoteTbl().setRowSelectionInterval(i,i);
				getRemoteTbl().scrollRectToVisible(getRemoteTbl().getCellRect(i,0,false));
			}
		}

		busyCursor(false);
	}
	else if (e.isListAreaFailed()) {
		busyCursor(false);
		JOptionPane.showMessageDialog(this,e.message,"List Failed",JOptionPane.ERROR_MESSAGE);
	}
	else if (e.isChangeArea()) {
		DSMPBaseProto p = FTPGenerator.listArea((byte) 0);
		dispatcher.dispatchProtocol(p);
	}
	else if (e.isChangeAreaFailed()) {
		busyCursor(false);
		JOptionPane.showMessageDialog(this,e.message,"Change Directory Failed",JOptionPane.ERROR_MESSAGE);
	}
	else if (e.isNewFolder()) {
		// Get updated list. Set handle to indicate to listArea processing that
		// it should set the new folder selected.
		DSMPBaseProto p = FTPGenerator.listArea((byte) 1);
		dispatcher.dispatchProtocol(p);
	}
	else if (e.isNewFolderFailed()) {
		busyCursor(false);
		JOptionPane.showMessageDialog(this,e.message,"Create Folder Failed",JOptionPane.ERROR_MESSAGE);
	}
	else if (e.isDelete()) {
		// Nothing to do for successful remote deletes.
		// Delete code will request 1 list refresh after all deletes.
	}
	else if (e.isDeleteFailed()) {
		// No cursor reset here, remote deletes are followed by listarea.
		JOptionPane.showMessageDialog(this,e.message,"Error",JOptionPane.ERROR_MESSAGE);
	}
	else if (e.isLogout()) {
		resetToLoggedOut();
		busyCursor(false);
		if (isTunnel || isExit) {
			exit();
		}
	}
	else if (e.isLogoutFailed()) {
		busyCursor(false);
		JOptionPane.showMessageDialog(this,e.message,"Logout Failed",JOptionPane.ERROR_MESSAGE);
	}
	else if (e.isDeath()) {
		busyCursor(false);
		// If we were logged in and the connection shutdown, that is bad.
		// If we are logging out, we'll get connection death, but the logout
		// menu item will be enabled (meaning we logged out).
		if (getLogoutMI().isEnabled())
			JOptionPane.showMessageDialog(this,"Connection to remote host ended.","No Connection",JOptionPane.ERROR_MESSAGE);
		resetToLoggedOut();

		if (isTunnel)
			exit();
	}
}
/**
 * Return the CanXferMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getCanXferMI() {
	if (ivjCanXferMI == null) {
		try {
			ivjCanXferMI = new javax.swing.JMenuItem();
			ivjCanXferMI.setName("CanXferMI");
			ivjCanXferMI.setText("Cancel Transfer");
			ivjCanXferMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							cancelXfer();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCanXferMI;
}
/**
 * Return the DefaultToolBarButton2 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getCopyTB() {
	if (ivjCopyTB == null) {
		try {
			ivjCopyTB = new javax.swing.JButton();
			ivjCopyTB.setName("CopyTB");
			ivjCopyTB.setToolTipText("Copy");
			ivjCopyTB.setText("");
			ivjCopyTB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjCopyTB.setDisabledIcon(null);
			ivjCopyTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjCopyTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/copy.gif")));
			ivjCopyTB.setMargin(new java.awt.Insets(0, 0, 0, 0));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCopyTB;
}
/**
 * Return the DebugMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getDebugMI() {
	if (ivjDebugMI == null) {
		try {
			ivjDebugMI = new javax.swing.JCheckBoxMenuItem();
			ivjDebugMI.setName("DebugMI");
			ivjDebugMI.setText("Debug");
			ivjDebugMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					try {
							doDebug();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDebugMI;
}
/**
 * Return the DetailsMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getDetailsMI() {
	if (ivjDetailsMI == null) {
		try {
			ivjDetailsMI = new javax.swing.JMenuItem();
			ivjDetailsMI.setName("DetailsMI");
			ivjDetailsMI.setText("Show Failed Details");
			ivjDetailsMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							showDetails();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDetailsMI;
}
/**
 * Return the DownloadBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDownloadBtn() {
	if (ivjDownloadBtn == null) {
		try {
			ivjDownloadBtn = new javax.swing.JButton();
			ivjDownloadBtn.setName("DownloadBtn");
			ivjDownloadBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/download.gif")));
			ivjDownloadBtn.setToolTipText("Download");
			ivjDownloadBtn.setText("");
			ivjDownloadBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjDownloadBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							transfer(e);
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownloadBtn;
}
/**
 * Return the ExitMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getExitMI() {
	if (ivjExitMI == null) {
		try {
			ivjExitMI = new javax.swing.JMenuItem();
			ivjExitMI.setName("ExitMI");
			ivjExitMI.setText("Exit");
			ivjExitMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							doExit();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjExitMI;
}
/**
 * Return the JMenu1 property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getFileM() {
	if (ivjFileM == null) {
		try {
			ivjFileM = new javax.swing.JMenu();
			ivjFileM.setName("FileM");
			ivjFileM.setText("File");
			ivjFileM.add(getLoginMI());
			ivjFileM.add(getLogoutMI());
			ivjFileM.add(getFileSep());
			ivjFileM.add(getDebugMI());
			ivjFileM.add(getJSeparator3());
			ivjFileM.add(getExitMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileM;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getFileSep() {
	if (ivjFileSep == null) {
		try {
			ivjFileSep = new javax.swing.JSeparator();
			ivjFileSep.setName("FileSep");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileSep;
}
/**
 * Return the FileStatusTM property value.
 * @return oem.edge.ed.odc.applet.FileStatusTableModel
 */
private FileStatusTableModel getFileStatusTM() {
	if (ivjFileStatusTM == null) {
		try {
			ivjFileStatusTM = new oem.edge.ed.odc.ftp.client.FileStatusTableModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileStatusTM;
}
/**
 * Return the FileTransferJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
private javax.swing.JMenuBar getFileTransferJMenuBar() {
	if (ivjFileTransferJMenuBar == null) {
		try {
			ivjFileTransferJMenuBar = new javax.swing.JMenuBar();
			ivjFileTransferJMenuBar.setName("FileTransferJMenuBar");
			ivjFileTransferJMenuBar.add(getFileM());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileTransferJMenuBar;
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
			ivjFolderDlg.setBounds(473, 915, 251, 131);
			ivjFolderDlg.setModal(true);
			ivjFolderDlg.setTitle("Create Folder");
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
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
private javax.swing.JSplitPane getHostSplit() {
	if (ivjHostSplit == null) {
		try {
			ivjHostSplit = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
			ivjHostSplit.setName("HostSplit");
			ivjHostSplit.setContinuousLayout(true);
			getHostSplit().add(getLocalPnl(), "left");
			getHostSplit().add(getRemotePnl(), "right");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjHostSplit;
}
/**
 * Return the InitDlg property value.
 * @return javax.swing.JFrame
 */
private javax.swing.JFrame getInitDlg() {
	if (ivjInitDlg == null) {
		try {
			ivjInitDlg = new javax.swing.JFrame();
			ivjInitDlg.setName("InitDlg");
			ivjInitDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
			ivjInitDlg.setTitle("File Transfer");
			ivjInitDlg.setBounds(47, 1178, 214, 108);
			ivjInitDlg.setResizable(false);
			getInitDlg().setContentPane(getJFrameContentPane1());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInitDlg;
}
/**
 * Return the InitLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getInitLbl() {
	if (ivjInitLbl == null) {
		try {
			ivjInitLbl = new javax.swing.JLabel();
			ivjInitLbl.setName("InitLbl");
			ivjInitLbl.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjInitLbl.setText("Initializing...");
			ivjInitLbl.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjInitLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInitLbl;
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

			java.awt.GridBagConstraints constraintsUserLbl = new java.awt.GridBagConstraints();
			constraintsUserLbl.gridx = 0; constraintsUserLbl.gridy = 0;
			constraintsUserLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUserLbl.insets = new java.awt.Insets(10, 10, 0, 0);
			getJDialogContentPane().add(getUserLbl(), constraintsUserLbl);

			java.awt.GridBagConstraints constraintsPwdLbl = new java.awt.GridBagConstraints();
			constraintsPwdLbl.gridx = 0; constraintsPwdLbl.gridy = 1;
			constraintsPwdLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPwdLbl.insets = new java.awt.Insets(10, 10, 0, 0);
			getJDialogContentPane().add(getPwdLbl(), constraintsPwdLbl);

			java.awt.GridBagConstraints constraintsUserTF = new java.awt.GridBagConstraints();
			constraintsUserTF.gridx = 1; constraintsUserTF.gridy = 0;
			constraintsUserTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUserTF.weightx = 1.0;
			constraintsUserTF.insets = new java.awt.Insets(10, 5, 0, 10);
			getJDialogContentPane().add(getUserTF(), constraintsUserTF);

			java.awt.GridBagConstraints constraintsPwdTF = new java.awt.GridBagConstraints();
			constraintsPwdTF.gridx = 1; constraintsPwdTF.gridy = 1;
			constraintsPwdTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPwdTF.weightx = 1.0;
			constraintsPwdTF.insets = new java.awt.Insets(10, 5, 0, 10);
			getJDialogContentPane().add(getPwdTF(), constraintsPwdTF);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 2;
			constraintsJPanel2.gridwidth = 0;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel2.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsJPanel2.weightx = 1.0;
			constraintsJPanel2.weighty = 1.0;
			constraintsJPanel2.insets = new java.awt.Insets(10, 10, 10, 10);
			getJDialogContentPane().add(getJPanel2(), constraintsJPanel2);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
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
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsStatusSplit = new java.awt.GridBagConstraints();
			constraintsStatusSplit.gridx = 0; constraintsStatusSplit.gridy = 1;
			constraintsStatusSplit.fill = java.awt.GridBagConstraints.BOTH;
			constraintsStatusSplit.weightx = 1.0;
			constraintsStatusSplit.weighty = 1.0;
			getJFrameContentPane().add(getStatusSplit(), constraintsStatusSplit);

			java.awt.GridBagConstraints constraintsToolBar = new java.awt.GridBagConstraints();
			constraintsToolBar.gridx = 0; constraintsToolBar.gridy = 0;
			constraintsToolBar.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsToolBar.weightx = 1.0;
			getJFrameContentPane().add(getToolBar(), constraintsToolBar);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Return the JFrameContentPane1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJFrameContentPane1() {
	if (ivjJFrameContentPane1 == null) {
		try {
			ivjJFrameContentPane1 = new javax.swing.JPanel();
			ivjJFrameContentPane1.setName("JFrameContentPane1");
			ivjJFrameContentPane1.setLayout(new java.awt.BorderLayout());
			getJFrameContentPane1().add(getInitLbl(), "Center");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane1;
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
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRemoteHomeBtn = new java.awt.GridBagConstraints();
			constraintsRemoteHomeBtn.gridx = 0; constraintsRemoteHomeBtn.gridy = 0;
			constraintsRemoteHomeBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsRemoteHomeBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getJPanel1().add(getRemoteHomeBtn(), constraintsRemoteHomeBtn);

			java.awt.GridBagConstraints constraintsRemoteUpBtn = new java.awt.GridBagConstraints();
			constraintsRemoteUpBtn.gridx = 1; constraintsRemoteUpBtn.gridy = 0;
			constraintsRemoteUpBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsRemoteUpBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getJPanel1().add(getRemoteUpBtn(), constraintsRemoteUpBtn);

			java.awt.GridBagConstraints constraintsRemoteRefreshBtn = new java.awt.GridBagConstraints();
			constraintsRemoteRefreshBtn.gridx = 2; constraintsRemoteRefreshBtn.gridy = 0;
			constraintsRemoteRefreshBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsRemoteRefreshBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanel1().add(getRemoteRefreshBtn(), constraintsRemoteRefreshBtn);

			java.awt.GridBagConstraints constraintsRemoteNewDirBtn = new java.awt.GridBagConstraints();
			constraintsRemoteNewDirBtn.gridx = 3; constraintsRemoteNewDirBtn.gridy = 0;
			constraintsRemoteNewDirBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsRemoteNewDirBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getJPanel1().add(getRemoteNewDirBtn(), constraintsRemoteNewDirBtn);

			java.awt.GridBagConstraints constraintsRemoteDeleteBtn = new java.awt.GridBagConstraints();
			constraintsRemoteDeleteBtn.gridx = 4; constraintsRemoteDeleteBtn.gridy = 0;
			constraintsRemoteDeleteBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsRemoteDeleteBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanel1().add(getRemoteDeleteBtn(), constraintsRemoteDeleteBtn);

			java.awt.GridBagConstraints constraintsDownloadBtn = new java.awt.GridBagConstraints();
			constraintsDownloadBtn.gridx = 5; constraintsDownloadBtn.gridy = 0;
			constraintsDownloadBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsDownloadBtn.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDownloadBtn.weightx = 1.0;
			constraintsDownloadBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getJPanel1().add(getDownloadBtn(), constraintsDownloadBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLoginOkBtn = new java.awt.GridBagConstraints();
			constraintsLoginOkBtn.gridx = 0; constraintsLoginOkBtn.gridy = 0;
			constraintsLoginOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanel2().add(getLoginOkBtn(), constraintsLoginOkBtn);

			java.awt.GridBagConstraints constraintsLoginCanBtn = new java.awt.GridBagConstraints();
			constraintsLoginCanBtn.gridx = 1; constraintsLoginCanBtn.gridy = 0;
			constraintsLoginCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getJPanel2().add(getLoginCanBtn(), constraintsLoginCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}
/**
 * Return the JSeparator2 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator2() {
	if (ivjJSeparator2 == null) {
		try {
			ivjJSeparator2 = new javax.swing.JSeparator();
			ivjJSeparator2.setName("JSeparator2");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator2;
}
/**
 * Return the JSeparator3 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator3() {
	if (ivjJSeparator3 == null) {
		try {
			ivjJSeparator3 = new javax.swing.JSeparator();
			ivjJSeparator3.setName("JSeparator3");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator3;
}
/**
 * Return the LocalBtnPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getLocalBtnPnl() {
	if (ivjLocalBtnPnl == null) {
		try {
			ivjLocalBtnPnl = new javax.swing.JPanel();
			ivjLocalBtnPnl.setName("LocalBtnPnl");
			ivjLocalBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLocalHomeBtn = new java.awt.GridBagConstraints();
			constraintsLocalHomeBtn.gridx = 0; constraintsLocalHomeBtn.gridy = 0;
			constraintsLocalHomeBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsLocalHomeBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getLocalBtnPnl().add(getLocalHomeBtn(), constraintsLocalHomeBtn);

			java.awt.GridBagConstraints constraintsLocalUpBtn = new java.awt.GridBagConstraints();
			constraintsLocalUpBtn.gridx = 1; constraintsLocalUpBtn.gridy = 0;
			constraintsLocalUpBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsLocalUpBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getLocalBtnPnl().add(getLocalUpBtn(), constraintsLocalUpBtn);

			java.awt.GridBagConstraints constraintsLocalRefreshBtn = new java.awt.GridBagConstraints();
			constraintsLocalRefreshBtn.gridx = 2; constraintsLocalRefreshBtn.gridy = 0;
			constraintsLocalRefreshBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsLocalRefreshBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getLocalBtnPnl().add(getLocalRefreshBtn(), constraintsLocalRefreshBtn);

			java.awt.GridBagConstraints constraintsLocalNewDirBtn = new java.awt.GridBagConstraints();
			constraintsLocalNewDirBtn.gridx = 3; constraintsLocalNewDirBtn.gridy = 0;
			constraintsLocalNewDirBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsLocalNewDirBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getLocalBtnPnl().add(getLocalNewDirBtn(), constraintsLocalNewDirBtn);

			java.awt.GridBagConstraints constraintsLocalDeleteBtn = new java.awt.GridBagConstraints();
			constraintsLocalDeleteBtn.gridx = 4; constraintsLocalDeleteBtn.gridy = 0;
			constraintsLocalDeleteBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsLocalDeleteBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getLocalBtnPnl().add(getLocalDeleteBtn(), constraintsLocalDeleteBtn);

			java.awt.GridBagConstraints constraintsLocalDrivesLbl = new java.awt.GridBagConstraints();
			constraintsLocalDrivesLbl.gridx = 6; constraintsLocalDrivesLbl.gridy = 0;
			constraintsLocalDrivesLbl.insets = new java.awt.Insets(0, 10, 0, 2);
			getLocalBtnPnl().add(getLocalDrivesLbl(), constraintsLocalDrivesLbl);

			java.awt.GridBagConstraints constraintsLocalDrivesCB = new java.awt.GridBagConstraints();
			constraintsLocalDrivesCB.gridx = 7; constraintsLocalDrivesCB.gridy = 0;
			constraintsLocalDrivesCB.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLocalDrivesCB.weightx = 1.0;
			getLocalBtnPnl().add(getLocalDrivesCB(), constraintsLocalDrivesCB);

			java.awt.GridBagConstraints constraintsUploadBtn = new java.awt.GridBagConstraints();
			constraintsUploadBtn.gridx = 5; constraintsUploadBtn.gridy = 0;
			constraintsUploadBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsUploadBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getLocalBtnPnl().add(getUploadBtn(), constraintsUploadBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalBtnPnl;
}
/**
 * Return the LocalDeleteBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLocalDeleteBtn() {
	if (ivjLocalDeleteBtn == null) {
		try {
			ivjLocalDeleteBtn = new javax.swing.JButton();
			ivjLocalDeleteBtn.setName("LocalDeleteBtn");
			ivjLocalDeleteBtn.setToolTipText("Delete Local");
			ivjLocalDeleteBtn.setText("");
			ivjLocalDeleteBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjLocalDeleteBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjLocalDeleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/trash.gif")));
			ivjLocalDeleteBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjLocalDeleteBtn.setEnabled(false);
			ivjLocalDeleteBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							doDelete(e);
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalDeleteBtn;
}
/**
 * Return the LocalDrivesCB property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getLocalDrivesCB() {
	if (ivjLocalDrivesCB == null) {
		try {
			ivjLocalDrivesCB = new javax.swing.JComboBox();
			ivjLocalDrivesCB.setName("LocalDrivesCB");
			ivjLocalDrivesCB.setToolTipText("Switch local drive");
			ivjLocalDrivesCB.setPreferredSize(new java.awt.Dimension(100, 23));
			ivjLocalDrivesCB.setMinimumSize(new java.awt.Dimension(75, 23));
			ivjLocalDrivesCB.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjLocalDrivesCB;
}
/**
 * Return the LocalDrivesLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLocalDrivesLbl() {
	if (ivjLocalDrivesLbl == null) {
		try {
			ivjLocalDrivesLbl = new javax.swing.JLabel();
			ivjLocalDrivesLbl.setName("LocalDrivesLbl");
			ivjLocalDrivesLbl.setIconTextGap(20);
			ivjLocalDrivesLbl.setText("Drives:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalDrivesLbl;
}
/**
 * Return the LocalHomeBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLocalHomeBtn() {
	if (ivjLocalHomeBtn == null) {
		try {
			ivjLocalHomeBtn = new javax.swing.JButton();
			ivjLocalHomeBtn.setName("LocalHomeBtn");
			ivjLocalHomeBtn.setToolTipText("Local Home");
			ivjLocalHomeBtn.setText("");
			ivjLocalHomeBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjLocalHomeBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjLocalHomeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/home.gif")));
			ivjLocalHomeBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjLocalHomeBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							chgDirToHome(e);
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalHomeBtn;
}
/**
 * Return the JLabel1 property value.
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
 * Return the LocalNewDirBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLocalNewDirBtn() {
	if (ivjLocalNewDirBtn == null) {
		try {
			ivjLocalNewDirBtn = new javax.swing.JButton();
			ivjLocalNewDirBtn.setName("LocalNewDirBtn");
			ivjLocalNewDirBtn.setToolTipText("New Local Folder");
			ivjLocalNewDirBtn.setText("");
			ivjLocalNewDirBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjLocalNewDirBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjLocalNewDirBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/newfold.gif")));
			ivjLocalNewDirBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjLocalNewDirBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							doCreate(e);
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}   
					}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalNewDirBtn;
}
/**
 * Return the LocalPathLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLocalPathLbl() {
	if (ivjLocalPathLbl == null) {
		try {
			ivjLocalPathLbl = new javax.swing.JLabel();
			ivjLocalPathLbl.setName("LocalPathLbl");
			ivjLocalPathLbl.setText("Path:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalPathLbl;
}
/**
 * Return the LocalPathTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getLocalPathTF() {
	if (ivjLocalPathTF == null) {
		try {
			ivjLocalPathTF = new javax.swing.JTextField();
			ivjLocalPathTF.setName("LocalPathTF");
			ivjLocalPathTF.setToolTipText("Enter a local directory");
			ivjLocalPathTF.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjLocalPathTF;
}
/**
 * Return the LocalPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getLocalPnl() {
	if (ivjLocalPnl == null) {
		try {
			ivjLocalPnl = new javax.swing.JPanel();
			ivjLocalPnl.setName("LocalPnl");
			ivjLocalPnl.setPreferredSize(new java.awt.Dimension(250, 400));
			ivjLocalPnl.setLayout(new java.awt.GridBagLayout());
			ivjLocalPnl.setMinimumSize(new java.awt.Dimension(0, 0));

			java.awt.GridBagConstraints constraintsLocalLbl = new java.awt.GridBagConstraints();
			constraintsLocalLbl.gridx = 0; constraintsLocalLbl.gridy = 0;
			constraintsLocalLbl.gridwidth = 0;
			constraintsLocalLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLocalLbl.insets = new java.awt.Insets(5, 5, 5, 5);
			getLocalPnl().add(getLocalLbl(), constraintsLocalLbl);

			java.awt.GridBagConstraints constraintsLocalPathLbl = new java.awt.GridBagConstraints();
			constraintsLocalPathLbl.gridx = 0; constraintsLocalPathLbl.gridy = 2;
			constraintsLocalPathLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLocalPathLbl.insets = new java.awt.Insets(0, 5, 0, 4);
			getLocalPnl().add(getLocalPathLbl(), constraintsLocalPathLbl);

			java.awt.GridBagConstraints constraintsLocalPathTF = new java.awt.GridBagConstraints();
			constraintsLocalPathTF.gridx = 1; constraintsLocalPathTF.gridy = 2;
			constraintsLocalPathTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLocalPathTF.weightx = 1.0;
			constraintsLocalPathTF.insets = new java.awt.Insets(0, 0, 0, 5);
			getLocalPnl().add(getLocalPathTF(), constraintsLocalPathTF);

			java.awt.GridBagConstraints constraintsLocalSP = new java.awt.GridBagConstraints();
			constraintsLocalSP.gridx = 0; constraintsLocalSP.gridy = 3;
			constraintsLocalSP.gridwidth = 0;
			constraintsLocalSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsLocalSP.weightx = 1.0;
			constraintsLocalSP.weighty = 1.0;
			constraintsLocalSP.insets = new java.awt.Insets(2, 5, 5, 5);
			getLocalPnl().add(getLocalSP(), constraintsLocalSP);

			java.awt.GridBagConstraints constraintsLocalBtnPnl = new java.awt.GridBagConstraints();
			constraintsLocalBtnPnl.gridx = 0; constraintsLocalBtnPnl.gridy = 1;
			constraintsLocalBtnPnl.gridwidth = 0;
			constraintsLocalBtnPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLocalBtnPnl.weightx = 1.0;
			constraintsLocalBtnPnl.insets = new java.awt.Insets(0, 5, 2, 5);
			getLocalPnl().add(getLocalBtnPnl(), constraintsLocalBtnPnl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalPnl;
}
/**
 * Return the LocalRefreshBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLocalRefreshBtn() {
	if (ivjLocalRefreshBtn == null) {
		try {
			ivjLocalRefreshBtn = new javax.swing.JButton();
			ivjLocalRefreshBtn.setName("LocalRefreshBtn");
			ivjLocalRefreshBtn.setToolTipText("Refresh Local");
			ivjLocalRefreshBtn.setText("");
			ivjLocalRefreshBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjLocalRefreshBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjLocalRefreshBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/refresh.gif")));
			ivjLocalRefreshBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjLocalRefreshBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							refresh(e);
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalRefreshBtn;
}
/**
 * Return the LocalSortTM property value.
 * @return FileTableSorter
 */
private FileTableSorter getLocalSortTM() {
	if (ivjLocalSortTM == null) {
		try {
			ivjLocalSortTM = new FileTableSorter();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalSortTM;
}
/**
 * Return the LocalSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getLocalSP() {
	if (ivjLocalSP == null) {
		try {
			ivjLocalSP = new javax.swing.JScrollPane();
			ivjLocalSP.setName("LocalSP");
			ivjLocalSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjLocalSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getLocalSP().setViewportView(getLocalTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalSP;
}
/**
 * Return the LocalTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getLocalTbl() {
	if (ivjLocalTbl == null) {
		try {
			ivjLocalTbl = new javax.swing.JTable();
			ivjLocalTbl.setName("LocalTbl");
			getLocalSP().setColumnHeaderView(ivjLocalTbl.getTableHeader());
			getLocalSP().getViewport().setBackingStoreEnabled(true);
			ivjLocalTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjLocalTbl.setBounds(0, 0, 200, 200);
			ivjLocalTbl.setShowVerticalLines(false);
			ivjLocalTbl.setShowHorizontalLines(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalTbl;
}
/**
 * Return the LocalTM property value.
 * @return oem.edge.ed.odc.applet.FileTableModel
 */
private FileTableModel getLocalTM() {
	if (ivjLocalTM == null) {
		try {
			ivjLocalTM = new FileTableModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalTM;
}
/**
 * Return the LocalUpBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLocalUpBtn() {
	if (ivjLocalUpBtn == null) {
		try {
			ivjLocalUpBtn = new javax.swing.JButton();
			ivjLocalUpBtn.setName("LocalUpBtn");
			ivjLocalUpBtn.setToolTipText("Up");
			ivjLocalUpBtn.setText("");
			ivjLocalUpBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjLocalUpBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjLocalUpBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/upfolder.gif")));
			ivjLocalUpBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjLocalUpBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							chgDirToParent(e);
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}   
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalUpBtn;
}
/**
 * Return the LogCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLoginCanBtn() {
	if (ivjLoginCanBtn == null) {
		try {
			ivjLoginCanBtn = new javax.swing.JButton();
			ivjLoginCanBtn.setName("LoginCanBtn");
			ivjLoginCanBtn.setText("Cancel");
			ivjLoginCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							getLoginDlg().dispose();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
					}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoginCanBtn;
}
/**
 * Return the LoginDlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getLoginDlg() {
	if (ivjLoginDlg == null) {
		try {
			ivjLoginDlg = new javax.swing.JDialog();
			ivjLoginDlg.setName("LoginDlg");
			ivjLoginDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjLoginDlg.setBounds(44, 913, 217, 133);
			ivjLoginDlg.setModal(true);
			ivjLoginDlg.setTitle("Login");
			getLoginDlg().setContentPane(getJDialogContentPane());
			ivjLoginDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowOpened(java.awt.event.WindowEvent e) {
					try {
							doLoginFocus();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
					}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoginDlg;
}
/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getLoginMI() {
	if (ivjLoginMI == null) {
		try {
			ivjLoginMI = new javax.swing.JMenuItem();
			ivjLoginMI.setName("LoginMI");
			ivjLoginMI.setText("Login...");
			ivjLoginMI.setActionCommand("Login");
			ivjLoginMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							doLogin();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoginMI;
}
/**
 * Return the LogOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLoginOkBtn() {
	if (ivjLoginOkBtn == null) {
		try {
			ivjLoginOkBtn = new javax.swing.JButton();
			ivjLoginOkBtn.setName("LoginOkBtn");
			ivjLoginOkBtn.setText("Ok");
			ivjLoginOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							login();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}   
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoginOkBtn;
}
/**
 * Return the DefaultToolBarButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLoginTB() {
	if (ivjLoginTB == null) {
		try {
			ivjLoginTB = new javax.swing.JButton();
			ivjLoginTB.setName("LoginTB");
			ivjLoginTB.setToolTipText("Login");
			ivjLoginTB.setText("");
			ivjLoginTB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjLoginTB.setDisabledIcon(null);
			ivjLoginTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjLoginTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/logon.gif")));
			ivjLoginTB.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjLoginTB.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							doLogin();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoginTB;
}
/**
 * Return the LogoutMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getLogoutMI() {
	if (ivjLogoutMI == null) {
		try {
			ivjLogoutMI = new javax.swing.JMenuItem();
			ivjLogoutMI.setName("LogoutMI");
			ivjLogoutMI.setText("Logout");
			ivjLogoutMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							doLogout();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLogoutMI;
}
/**
 * Return the DefaultToolBarButton1 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLogoutTB() {
	if (ivjLogoutTB == null) {
		try {
			ivjLogoutTB = new javax.swing.JButton();
			ivjLogoutTB.setName("LogoutTB");
			ivjLogoutTB.setToolTipText("Logout");
			ivjLogoutTB.setText("");
			ivjLogoutTB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjLogoutTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjLogoutTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/logoff.gif")));
			ivjLogoutTB.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjLogoutTB.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							doLogout();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLogoutTB;
}
/**
 * Return the DefaultToolBarButton3 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getPasteTB() {
	if (ivjPasteTB == null) {
		try {
			ivjPasteTB = new javax.swing.JButton();
			ivjPasteTB.setName("PasteTB");
			ivjPasteTB.setToolTipText("Paste");
			ivjPasteTB.setText("");
			ivjPasteTB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjPasteTB.setDisabledIcon(null);
			ivjPasteTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjPasteTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/paste.gif")));
			ivjPasteTB.setMargin(new java.awt.Insets(0, 0, 0, 0));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPasteTB;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPwdLbl() {
	if (ivjPwdLbl == null) {
		try {
			ivjPwdLbl = new javax.swing.JLabel();
			ivjPwdLbl.setName("PwdLbl");
			ivjPwdLbl.setText("Password:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPwdLbl;
}
/**
 * Return the JPasswordField1 property value.
 * @return javax.swing.JPasswordField
 */
private javax.swing.JPasswordField getPwdTF() {
	if (ivjPwdTF == null) {
		try {
			ivjPwdTF = new javax.swing.JPasswordField();
			ivjPwdTF.setName("PwdTF");
			ivjPwdTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							login();
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
 * Return the RemoteDeleteTB property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getRemoteDeleteBtn() {
	if (ivjRemoteDeleteBtn == null) {
		try {
			ivjRemoteDeleteBtn = new javax.swing.JButton();
			ivjRemoteDeleteBtn.setName("RemoteDeleteBtn");
			ivjRemoteDeleteBtn.setToolTipText("Delete Remote");
			ivjRemoteDeleteBtn.setText("");
			ivjRemoteDeleteBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjRemoteDeleteBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjRemoteDeleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/trash.gif")));
			ivjRemoteDeleteBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjRemoteDeleteBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							doDelete(e);
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteDeleteBtn;
}
/**
 * Return the RemoteFolderTB property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getRemoteHomeBtn() {
	if (ivjRemoteHomeBtn == null) {
		try {
			ivjRemoteHomeBtn = new javax.swing.JButton();
			ivjRemoteHomeBtn.setName("RemoteHomeBtn");
			ivjRemoteHomeBtn.setToolTipText("Remote Home");
			ivjRemoteHomeBtn.setText("");
			ivjRemoteHomeBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjRemoteHomeBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjRemoteHomeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/home.gif")));
			ivjRemoteHomeBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjRemoteHomeBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							chgDirToHome(e);
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}	    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteHomeBtn;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRemoteLbl() {
	if (ivjRemoteLbl == null) {
		try {
			ivjRemoteLbl = new javax.swing.JLabel();
			ivjRemoteLbl.setName("RemoteLbl");
			ivjRemoteLbl.setText("Host Files");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteLbl;
}
/**
 * Return the RemoteNewDirTB property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getRemoteNewDirBtn() {
	if (ivjRemoteNewDirBtn == null) {
		try {
			ivjRemoteNewDirBtn = new javax.swing.JButton();
			ivjRemoteNewDirBtn.setName("RemoteNewDirBtn");
			ivjRemoteNewDirBtn.setToolTipText("New Remote Folder");
			ivjRemoteNewDirBtn.setText("");
			ivjRemoteNewDirBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjRemoteNewDirBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjRemoteNewDirBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/newfold.gif")));
			ivjRemoteNewDirBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjRemoteNewDirBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							doCreate(e);
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteNewDirBtn;
}
/**
 * Return the RemotePathLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRemotePathLbl() {
	if (ivjRemotePathLbl == null) {
		try {
			ivjRemotePathLbl = new javax.swing.JLabel();
			ivjRemotePathLbl.setName("RemotePathLbl");
			ivjRemotePathLbl.setText("Path:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemotePathLbl;
}
/**
 * Return the RemotePathTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getRemotePathTF() {
	if (ivjRemotePathTF == null) {
		try {
			ivjRemotePathTF = new javax.swing.JTextField();
			ivjRemotePathTF.setName("RemotePathTF");
			ivjRemotePathTF.setToolTipText("Enter a remote directory");
			ivjRemotePathTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							chgRemoteDir();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemotePathTF;
}
/**
 * Return the RemotePnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getRemotePnl() {
	if (ivjRemotePnl == null) {
		try {
			ivjRemotePnl = new javax.swing.JPanel();
			ivjRemotePnl.setName("RemotePnl");
			ivjRemotePnl.setPreferredSize(new java.awt.Dimension(200, 400));
			ivjRemotePnl.setLayout(new java.awt.GridBagLayout());
			ivjRemotePnl.setMinimumSize(new java.awt.Dimension(0, 0));

			java.awt.GridBagConstraints constraintsRemoteLbl = new java.awt.GridBagConstraints();
			constraintsRemoteLbl.gridx = 0; constraintsRemoteLbl.gridy = 0;
			constraintsRemoteLbl.gridwidth = 2;
			constraintsRemoteLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRemoteLbl.insets = new java.awt.Insets(5, 5, 5, 5);
			getRemotePnl().add(getRemoteLbl(), constraintsRemoteLbl);

			java.awt.GridBagConstraints constraintsRemotePathLbl = new java.awt.GridBagConstraints();
			constraintsRemotePathLbl.gridx = 0; constraintsRemotePathLbl.gridy = 2;
			constraintsRemotePathLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRemotePathLbl.insets = new java.awt.Insets(0, 5, 0, 4);
			getRemotePnl().add(getRemotePathLbl(), constraintsRemotePathLbl);

			java.awt.GridBagConstraints constraintsRemotePathTF = new java.awt.GridBagConstraints();
			constraintsRemotePathTF.gridx = 1; constraintsRemotePathTF.gridy = 2;
			constraintsRemotePathTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRemotePathTF.weightx = 1.0;
			constraintsRemotePathTF.insets = new java.awt.Insets(0, 0, 0, 5);
			getRemotePnl().add(getRemotePathTF(), constraintsRemotePathTF);

			java.awt.GridBagConstraints constraintsRemoteSP = new java.awt.GridBagConstraints();
			constraintsRemoteSP.gridx = 0; constraintsRemoteSP.gridy = 3;
			constraintsRemoteSP.gridwidth = 0;
			constraintsRemoteSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRemoteSP.weightx = 1.0;
			constraintsRemoteSP.weighty = 1.0;
			constraintsRemoteSP.insets = new java.awt.Insets(2, 5, 5, 5);
			getRemotePnl().add(getRemoteSP(), constraintsRemoteSP);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
			constraintsJPanel1.gridwidth = 0;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(0, 5, 2, 5);
			getRemotePnl().add(getJPanel1(), constraintsJPanel1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemotePnl;
}
/**
 * Return the RemoteRefreshTB property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getRemoteRefreshBtn() {
	if (ivjRemoteRefreshBtn == null) {
		try {
			ivjRemoteRefreshBtn = new javax.swing.JButton();
			ivjRemoteRefreshBtn.setName("RemoteRefreshBtn");
			ivjRemoteRefreshBtn.setToolTipText("Remote Refresh");
			ivjRemoteRefreshBtn.setText("");
			ivjRemoteRefreshBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjRemoteRefreshBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjRemoteRefreshBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/refresh.gif")));
			ivjRemoteRefreshBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjRemoteRefreshBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							refresh(e);
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteRefreshBtn;
}
/**
 * Return the RemoteSortTM property value.
 * @return FileTableSorter
 */
private FileTableSorter getRemoteSortTM() {
	if (ivjRemoteSortTM == null) {
		try {
			ivjRemoteSortTM = new FileTableSorter();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteSortTM;
}
/**
 * Return the RemoteSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getRemoteSP() {
	if (ivjRemoteSP == null) {
		try {
			ivjRemoteSP = new javax.swing.JScrollPane();
			ivjRemoteSP.setName("RemoteSP");
			ivjRemoteSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjRemoteSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getRemoteSP().setViewportView(getRemoteTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteSP;
}
/**
 * Return the RemoteTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getRemoteTbl() {
	if (ivjRemoteTbl == null) {
		try {
			ivjRemoteTbl = new javax.swing.JTable();
			ivjRemoteTbl.setName("RemoteTbl");
			getRemoteSP().setColumnHeaderView(ivjRemoteTbl.getTableHeader());
			getRemoteSP().getViewport().setBackingStoreEnabled(true);
			ivjRemoteTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjRemoteTbl.setBackground(java.awt.Color.white);
			ivjRemoteTbl.setBounds(0, 0, 200, 200);
			ivjRemoteTbl.setShowVerticalLines(false);
			ivjRemoteTbl.setShowHorizontalLines(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteTbl;
}
/**
 * Return the RemoteTM property value.
 * @return oem.edge.ed.odc.applet.FileTableModel
 */
private FileTableModel getRemoteTM() {
	if (ivjRemoteTM == null) {
		try {
			ivjRemoteTM = new FileTableModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteTM;
}
/**
 * Return the RemoteUpTB property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getRemoteUpBtn() {
	if (ivjRemoteUpBtn == null) {
		try {
			ivjRemoteUpBtn = new javax.swing.JButton();
			ivjRemoteUpBtn.setName("RemoteUpBtn");
			ivjRemoteUpBtn.setToolTipText("Up");
			ivjRemoteUpBtn.setText("");
			ivjRemoteUpBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjRemoteUpBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjRemoteUpBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/upfolder.gif")));
			ivjRemoteUpBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjRemoteUpBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							chgDirToParent(e);
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteUpBtn;
}
/**
 * Return the RemoveAllMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getRemoveAllMI() {
	if (ivjRemoveAllMI == null) {
		try {
			ivjRemoveAllMI = new javax.swing.JMenuItem();
			ivjRemoveAllMI.setName("RemoveAllMI");
			ivjRemoveAllMI.setText("Remove All Completed");
			ivjRemoveAllMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							removeAll();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoveAllMI;
}
/**
 * Return the RemoveMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getRemoveMI() {
	if (ivjRemoveMI == null) {
		try {
			ivjRemoveMI = new javax.swing.JMenuItem();
			ivjRemoveMI.setName("RemoveMI");
			ivjRemoveMI.setText("Remove Selected");
			ivjRemoveMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							removeSelected();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoveMI;
}
/**
 * Return the StatusPU property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getStatusPU() {
	if (ivjStatusPU == null) {
		try {
			ivjStatusPU = new javax.swing.JPopupMenu();
			ivjStatusPU.setName("StatusPU");
			ivjStatusPU.setLabel("");
			ivjStatusPU.add(getCanXferMI());
			ivjStatusPU.add(getJSeparator1());
			ivjStatusPU.add(getRemoveMI());
			ivjStatusPU.add(getRemoveAllMI());
			ivjStatusPU.add(getJSeparator2());
			ivjStatusPU.add(getDetailsMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStatusPU;
}
/**
 * Return the StatusSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getStatusSP() {
	if (ivjStatusSP == null) {
		try {
			ivjStatusSP = new javax.swing.JScrollPane();
			ivjStatusSP.setName("StatusSP");
			ivjStatusSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjStatusSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjStatusSP.setPreferredSize(new java.awt.Dimension(400, 50));
			getStatusSP().setViewportView(getStatusTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStatusSP;
}
/**
 * Return the StatusSplit property value.
 * @return javax.swing.JSplitPane
 */
private javax.swing.JSplitPane getStatusSplit() {
	if (ivjStatusSplit == null) {
		try {
			ivjStatusSplit = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjStatusSplit.setName("StatusSplit");
			ivjStatusSplit.setContinuousLayout(true);
			getStatusSplit().add(getHostSplit(), "top");
			getStatusSplit().add(getStatusSP(), "bottom");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStatusSplit;
}
/**
 * Return the StatusTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getStatusTbl() {
	if (ivjStatusTbl == null) {
		try {
			ivjStatusTbl = new javax.swing.JTable();
			ivjStatusTbl.setName("StatusTbl");
			getStatusSP().setColumnHeaderView(ivjStatusTbl.getTableHeader());
			getStatusSP().getViewport().setBackingStoreEnabled(true);
			ivjStatusTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjStatusTbl.setBounds(0, 0, 200, 200);
			ivjStatusTbl.setPreferredScrollableViewportSize(new java.awt.Dimension(450, 50));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStatusTbl;
}
/**
 * Return the StopTB property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getStopTB() {
	if (ivjStopTB == null) {
		try {
			ivjStopTB = new javax.swing.JButton();
			ivjStopTB.setName("StopTB");
			ivjStopTB.setToolTipText("Stop selected transfers");
			ivjStopTB.setText("");
			ivjStopTB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjStopTB.setDisabledIcon(null);
			ivjStopTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjStopTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/stop.gif")));
			ivjStopTB.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjStopTB.setEnabled(false);
			ivjStopTB.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							cancelXfer();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStopTB;
}
/**
 * Return the JToolBar1 property value.
 * @return javax.swing.JToolBar
 */
private javax.swing.JToolBar getToolBar() {
	if (ivjToolBar == null) {
		try {
			ivjToolBar = new javax.swing.JToolBar();
			ivjToolBar.setName("ToolBar");
			ivjToolBar.add(getLoginTB());
			getToolBar().add(getLogoutTB(), getLogoutTB().getName());
			ivjToolBar.addSeparator();
			getToolBar().add(getCopyTB(), getCopyTB().getName());
			getToolBar().add(getPasteTB(), getPasteTB().getName());
			ivjToolBar.addSeparator();
			getToolBar().add(getStopTB(), getStopTB().getName());
			ivjToolBar.addSeparator();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjToolBar;
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
			ivjUploadBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/upload.gif")));
			ivjUploadBtn.setToolTipText("Upload");
			ivjUploadBtn.setText("");
			ivjUploadBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjUploadBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							transfer(e);
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
 * Return the UserLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getUserLbl() {
	if (ivjUserLbl == null) {
		try {
			ivjUserLbl = new javax.swing.JLabel();
			ivjUserLbl.setName("UserLbl");
			ivjUserLbl.setText("User ID:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserLbl;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getUserTF() {
	if (ivjUserTF == null) {
		try {
			ivjUserTF = new javax.swing.JTextField();
			ivjUserTF.setName("UserTF");
			ivjUserTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
							getPwdTF().requestFocus();
						} catch (java.lang.Throwable ivjExc) {
							handleException(ivjExc);
						}    
					}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserTF;
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
		setName("FileTransfer");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("File Transfer");
		setSize(632, 553);
		setJMenuBar(getFileTransferJMenuBar());
		setContentPane(getJFrameContentPane());
		this.addWindowListener(new java.awt.event.WindowAdapter() {   
			public void windowClosing(java.awt.event.WindowEvent e) {    
				try {
					doExit();
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}    
			} 
			public void windowOpened(java.awt.event.WindowEvent e) {    
				try {
					split();
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
		});

		getLocalTbl().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				fileListSelectionChg(e);
			}
		});
		getRemoteTbl().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				fileListSelectionChg(e);
			}
		});
		getStatusTbl().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				statusListSelectionChanged();
			}
		});

		getStatusTbl().setModel(getFileStatusTM());
		getStatusTbl().createDefaultColumnsFromModel();

		getLocalSortTM().setModel(getLocalTM());
		getLocalTbl().setModel(getLocalSortTM());
		getLocalTbl().createDefaultColumnsFromModel();

		getRemoteSortTM().setModel(getRemoteTM());
		getRemoteTbl().setModel(getRemoteSortTM());
		getRemoteTbl().createDefaultColumnsFromModel();

		// Setup the local table.
		getLocalTbl().getTableHeader().setReorderingAllowed(false);
		TableColumnModel tm = getLocalTbl().getColumnModel();
		FileHeaderRenderer hr = new FileHeaderRenderer();
		hr.setHorizontalAlignment(JLabel.CENTER);
		FileCellRenderer cr = new FileCellRenderer();
		TableColumn tc = tm.getColumn(0);
		tc.setCellRenderer(cr);
		tc.setHeaderRenderer(hr);
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

		// Setup the remote table.
		getRemoteTbl().getTableHeader().setReorderingAllowed(false);
		tm = getRemoteTbl().getColumnModel();
		tc = tm.getColumn(0);
		tc.setCellRenderer(cr);
		tc.setHeaderRenderer(hr);
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

		// Setup the status table.
		getStatusTbl().getTableHeader().setReorderingAllowed(false);
		tm = getStatusTbl().getColumnModel();
		tm.getColumn(0).setPreferredWidth(14);
		tm.getColumn(0).setCellRenderer(new FileStatusDirectionRenderer());
		tm.getColumn(1).setPreferredWidth(200);
		tm.getColumn(2).setPreferredWidth(100);
		tm.getColumn(3).setPreferredWidth(100);
		tm.getColumn(4).setPreferredWidth(75);
		tm.getColumn(5).setPreferredWidth(108);
		tm.getColumn(5).setCellRenderer(new FileStatusRenderer());
		//tm.getColumn(6).setPreferredWidth(75);
		//tm.getColumn(7).setPreferredWidth(75);

		getLocalSortTM().addMouseListenerToHeaderInTable(getLocalTbl());
		getRemoteSortTM().addMouseListenerToHeaderInTable(getRemoteTbl());

		// Setup a MouseAdapter to handle double clicks on the file tables.
		MouseAdapter ma = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
					tableAction((JTable) e.getSource());
				}
			}
		};
		getLocalTbl().addMouseListener(ma);
		getRemoteTbl().addMouseListener(ma);

		// Setup a MouseAdapter to handle right presses on the status table.
		ma = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if ((e.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
					statusListSelectionChanged();
					getStatusPU().show(getStatusTbl(),e.getX(),e.getY());
				}
			}
		};
		getStatusTbl().addMouseListener(ma);

		// Prepare to listen for changes on the login dialog.
		getUserTF().getDocument().addDocumentListener(this);
		getPwdTF().getDocument().addDocumentListener(this);

		// Fix the background colors of the viewports to match the tables.
		getRemoteSP().getViewport().setBackground(getRemoteTbl().getBackground());
		getLocalSP().getViewport().setBackground(getLocalTbl().getBackground());
		getStatusSP().getViewport().setBackground(getStatusTbl().getBackground());

		// Set up the glass pane to handle the busy cursor.
		getGlassPane().addMouseListener(new MouseAdapter() {});
		getGlassPane().addKeyListener(new KeyAdapter() {});
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
	if (e.getDocument() == getUserTF().getDocument())
		textChgLogin();
	if (e.getDocument() == getPwdTF().getDocument())
		textChgLogin();
}
/**
 * Comment
 */
public void login() {
	if (! getLoginOkBtn().isEnabled())
		return;

	busyCursor(true);

	if (dispatcher == null) {
		try {
			if (serverSocket == null)
				dispatcher = new FTPDispatcher(serverName,serverPort);
			else
				dispatcher = new FTPDispatcher(serverSocket);
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Unknown host: " + serverName);
			System.out.println("FileTransfer host port");
			busyCursor(false);
			JOptionPane.showMessageDialog(this,"Unknown host: " + serverName,"No connection",JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			busyCursor(false);
			if (serverSocket == null) {
				System.out.println("Unable to connect to server " + serverName + " at " + serverPort + ".");
				System.out.println("FileTransfer host port");
				JOptionPane.showMessageDialog(this,"Unable to connect to server " + serverName + " at " + serverPort + ".","No connection",JOptionPane.ERROR_MESSAGE);
			}
			else {
				System.out.println("Unable to connect to socket provided.");
				JOptionPane.showMessageDialog(this,"Bad server information.","No connection",JOptionPane.ERROR_MESSAGE);
			}
			return;
		}

		dispatcher.setDebug(getDebugMI().isSelected());
		dispatcher.addFTPListener(this);
		getFileStatusTM().setDispatcher(dispatcher);
	}

	//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	setTitle("File Transfer - Connecting..");
	getLoginDlg().setVisible(false);
	getLoginMI().setEnabled(false);
	getLoginTB().setEnabled(false);

	DSMPBaseProto p = FTPGenerator.loginUserPW((byte) 0,getUserTF().getText(),new String(getPwdTF().getPassword()));
	dispatcher.dispatchProtocol(p);
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		initDlg = new JFrame("File Transfer");
		initDlg.getContentPane().setLayout(new BorderLayout());
		initLbl = new JLabel("Initializing...");
		initLbl.setHorizontalAlignment(JLabel.CENTER);
		initLbl.setHorizontalTextPosition(JLabel.CENTER);
		initDlg.getContentPane().add(initLbl,"Center");
		Dimension s = new Dimension(200,100);
		initDlg.pack();
		initDlg.setSize(s);

		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();

		int x = (scrSize.width - s.width) / 2;
		int y = (scrSize.height - s.height) / 2;

		int dx = x + s.width - scrSize.width;
		if (dx > 0)
			x -= dx;

		if (x < 0)
			x = 0;

		int dy = y + s.height - scrSize.height;
		if (dy > 0)
			y -= dy;

		if (y < 0)
			y = 0;

		initDlg.setLocation(x,y);
		initDlg.setVisible(true);

		FileTransfer aFileTransfer = new FileTransfer();
		aFileTransfer.begin(args);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void refresh(ActionEvent e) {
	busyCursor(true);
	if (e.getSource() == getLocalRefreshBtn()) {
		// Update the path field and cause the list to go blank.
		getLocalTbl().clearSelection();
		String dir = getLocalTM().getDirectory();
		getLocalPathTF().setText(dir);
		getLocalTM().setDirectory(dir);
		getLocalTM().populateLocal();
		busyCursor(false);
	}
	else {
		// Update the path field and cause the list to go blank.
		getRemoteTbl().clearSelection();
		String dir = getRemoteTM().getDirectory();
		getRemotePathTF().setText(dir);
		getRemoteTM().setDirectory(dir);

		// Request a new list of files.
		DSMPBaseProto p = FTPGenerator.listArea((byte) 0);
		dispatcher.dispatchProtocol(p);
	}
}
/**
 * Comment
 */
public void removeAll() {
	busyCursor(true);
	getFileStatusTM().removeAllCompleted();
	busyCursor(false);
}
/**
 * Comment
 */
public void removeSelected() {
	busyCursor(true);
	getFileStatusTM().remove(getStatusTbl().getSelectedRows());
	busyCursor(false);
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2002 12:01:40 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void removeUpdate(DocumentEvent e) {
	if (e.getDocument() == getUserTF().getDocument())
		textChgLogin();
	if (e.getDocument() == getPwdTF().getDocument())
		textChgLogin();
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 1:02:30 PM)
 */
private void resetToLoggedIn() {
	// Disable menu features
	getLoginMI().setEnabled(false);
	getLogoutMI().setEnabled(true);
	//getEditM().setEnabled(false);

	// Disable window features
	getStatusTbl().setEnabled(true);
	getRemoteTbl().setEnabled(true);
	getUploadBtn().setEnabled(false);
	getRemoteHomeBtn().setEnabled(true);
	getRemoteUpBtn().setEnabled(true);
	getRemoteRefreshBtn().setEnabled(true);
	getRemoteNewDirBtn().setEnabled(true);
	getRemoteDeleteBtn().setEnabled(false);
	getRemotePathLbl().setEnabled(true);
	getRemotePathTF().setEnabled(true);
	getDownloadBtn().setEnabled(false);

	// Set the right toolbar features enabled.
	getLoginTB().setEnabled(false);
	getLogoutTB().setEnabled(true);
	getCopyTB().setEnabled(false);
	getPasteTB().setEnabled(false);
	getStopTB().setEnabled(false);

	// Set the data models properly.
	getRemotePathTF().setText("");
	getRemoteTM().setDirectory(null);

	isExit = false;

	setTitle("File Transfer - Connected");
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 1:02:30 PM)
 */
private void resetToLoggedOut() {
	// Disable menu features
	getLoginMI().setEnabled(true);
	getLogoutMI().setEnabled(false);
	//getEditM().setEnabled(false);

	// Disable window features
	getStatusTbl().setEnabled(false);
	getRemoteTbl().setEnabled(false);
	getUploadBtn().setEnabled(false);
	getRemoteHomeBtn().setEnabled(false);
	getRemoteUpBtn().setEnabled(false);
	getRemoteRefreshBtn().setEnabled(false);
	getRemoteNewDirBtn().setEnabled(false);
	getRemoteDeleteBtn().setEnabled(false);
	getRemotePathLbl().setEnabled(false);
	getRemotePathTF().setEnabled(false);
	getDownloadBtn().setEnabled(false);

	// Set the right toolbar features enabled.
	getLoginTB().setEnabled(true);
	getLogoutTB().setEnabled(false);
	getCopyTB().setEnabled(false);
	getPasteTB().setEnabled(false);
	getStopTB().setEnabled(false);

	// Set the data models properly.
	getRemoteTM().setDirectory(null);
	getRemotePathTF().setText("");
	getFileStatusTM().clear();

	setTitle("File Transfer - Disconnected");
}
/**
 * Comment
 */
public void showDetails() {
	getFileStatusTM().showDetail(this,getStatusTbl().getSelectedRows());
}
/**
 * Comment
 */
public void split() {
	Dimension s = getStatusSplit().getSize();
	int dl = (s.height - getStatusSplit().getDividerSize()) / 5 * 4;
	getStatusSplit().setDividerLocation(dl);
	dl = (s.width - getHostSplit().getDividerSize()) / 2;
	getHostSplit().setDividerLocation(dl);
	doLogin();
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:52:26 PM)
 * @param args java.lang.String[]
 */
public static void start(Socket socket) throws Exception {
	if (socket == null)
		throw new Exception("socket not provided.");

	initDlg = new JFrame("File Transfer");
	initDlg.getContentPane().setLayout(new BorderLayout());
	initLbl = new JLabel("Initializing...");
	initLbl.setHorizontalAlignment(JLabel.CENTER);
	initLbl.setHorizontalTextPosition(JLabel.CENTER);
	initDlg.getContentPane().add(initLbl,"Center");
	Dimension s = new Dimension(200,100);
	initDlg.pack();
	initDlg.setSize(s);

	Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();

	int x = (scrSize.width - s.width) / 2;
	int y = (scrSize.height - s.height) / 2;

	int dx = x + s.width - scrSize.width;
	if (dx > 0)
		x -= dx;

	if (x < 0)
		x = 0;

	int dy = y + s.height - scrSize.height;
	if (dy > 0)
		y -= dy;

	if (y < 0)
		y = 0;

	initDlg.setLocation(x,y);
	initDlg.setVisible(true);

	FileTransfer aFileTransfer = new FileTransfer();
	aFileTransfer.begin(socket);
}
/**
 * Comment
 */
public void statusListSelectionChanged() {
	boolean showCancel = true;
	boolean showRemove = true;
	boolean showRemoveAll = true;
	boolean showFailed = true;

	int[] s = getStatusTbl().getSelectedRows();

	if (s.length == 0) {
		showCancel = false;
		showRemove = false;
		showFailed = false;
	}
	else {
		for (int i = 0; i < s.length; i++) {
			if (getFileStatusTM().isActive(s[i])) {
				showFailed = false;
				showRemove = false;
			}
			else if (getFileStatusTM().isFailed(s[i])) {
				showCancel = false;
			}
			else {
				showCancel = false;
				showFailed = false;
			}
		}
	}

	getCanXferMI().setEnabled(showCancel);
	getRemoveMI().setEnabled(showRemove);
	getRemoveAllMI().setEnabled(showRemoveAll);
	getDetailsMI().setEnabled(showFailed);

	getStopTB().setEnabled(showCancel);
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2002 3:25:43 PM)
 * @param table javax.swing.JTable
 */
public void tableAction(JTable table) {
	int i = table.getSelectedRowCount();
	if (i == 1) {
		busyCursor(true);
		i = table.getSelectedRow();
		String name = (String) table.getModel().getValueAt(i,1);
		Boolean isDir = (Boolean) table.getModel().getValueAt(i,0);

		if (isDir.booleanValue()) {
			if (table == getLocalTbl()) {
				getLocalTbl().clearSelection();
				String dir;
				if (getLocalTM().getDirectory().endsWith(File.separator))
					dir = getLocalTM().getDirectory() + name;
				else
					dir = getLocalTM().getDirectory() + File.separator + name;
				getLocalPathTF().setText(dir);
				getLocalTM().setDirectory(dir);
				getLocalTM().populateLocal();
				busyCursor(false);
			}
			else {
				getRemoteTbl().clearSelection();
				String dir;
				if (getRemoteTM().getDirectory().endsWith(dispatcher.remoteSeparator))
					dir = getRemoteTM().getDirectory() + name;
				else
					dir = getRemoteTM().getDirectory() + dispatcher.remoteSeparator + name;
				getRemotePathTF().setText(dir);
				getRemoteTM().setDirectory(dir);
				DSMPBaseProto p = FTPGenerator.changeArea((byte) 0,dir);
				dispatcher.dispatchProtocol(p);
			}
		}
		else if (table == getLocalTbl()) {
			File f = new File(getLocalTM().getDirectory(),name);
			if (f.exists()) {
				// Get the file length from the remote list to see if restart is possible.
				long len = getRemoteTM().getFileLength(name);
				String error = getFileStatusTM().uploadFile(name,getLocalTM().getDirectory(),getRemoteTM().getDirectory(),len);
				busyCursor(false);
				if (error != null) {
					JOptionPane.showMessageDialog(this,name + ": " + error,"Upload File",JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				busyCursor(false);
				JOptionPane.showMessageDialog(this,name + "not found.","Upload File",JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			Long len = (Long) table.getModel().getValueAt(i,2);
			String error = getFileStatusTM().downloadFile(name,getRemoteTM().getDirectory(),getLocalTM().getDirectory(),len);
			busyCursor(false);
			if (error != null) {
				JOptionPane.showMessageDialog(this,name + ": " + error,"Download File",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	else {
		System.out.println("User doubled clicked table and " + i + " items were selected as a result");
	}
}
/**
 * Comment
 */
public void textChgLogin() {
	String user = getUserTF().getText();
	String pwd = new String(getPwdTF().getPassword());

	getLoginOkBtn().setEnabled(user != null && user.length() > 0 &&
		pwd != null && pwd.length() > 0);
}
/**
 * Comment
 */
public void transfer(ActionEvent e) {
	busyCursor(true);
	if (e.getSource() == getUploadBtn()) {
		int[] s = getLocalTbl().getSelectedRows();
		for (int i = 0; i < s.length; i++) {
			String name = (String) getLocalSortTM().getValueAt(s[i],1);

			// Get the file length from the remote list to see if restart is possible.
			long len = getRemoteTM().getFileLength(name);

			String error = getFileStatusTM().uploadFile(name,getLocalTM().getDirectory(),getRemoteTM().getDirectory(),len);
			if (error != null) {
				JOptionPane.showMessageDialog(this,name + ": " + error,"Upload File",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	else {
		int[] s = getRemoteTbl().getSelectedRows();
		for (int i = 0; i < s.length; i++) {
			String name = (String) getRemoteSortTM().getValueAt(s[i],1);
			Long len = (Long) getRemoteSortTM().getValueAt(s[i],2);
			String error = getFileStatusTM().downloadFile(name,getRemoteTM().getDirectory(),getLocalTM().getDirectory(),len);
			if (error != null) {
				JOptionPane.showMessageDialog(this,name + ": " + error,"Download File",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	busyCursor(false);
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
