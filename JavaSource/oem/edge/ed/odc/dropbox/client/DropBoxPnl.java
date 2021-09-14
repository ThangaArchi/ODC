package oem.edge.ed.odc.dropbox.client;

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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import oem.edge.ed.odc.applet.CodeUpdaterGui;
import oem.edge.ed.odc.applet.LaunchApp;
import oem.edge.ed.odc.dropbox.common.AclInfo;
import oem.edge.ed.odc.dropbox.common.DropboxGenerator;
import oem.edge.ed.odc.dropbox.common.FileInfo;
import oem.edge.ed.odc.dropbox.common.PackageInfo;
import oem.edge.ed.odc.dropbox.common.PoolInfo;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.service.helper.ConnectionFactory;
import oem.edge.ed.odc.dropbox.service.helper.HessianConnectFactory;
import oem.edge.ed.odc.dsmp.client.AboutWindow;
import oem.edge.ed.odc.dsmp.client.Buddy;
import oem.edge.ed.odc.dsmp.client.BuddyListCellRenderer;
import oem.edge.ed.odc.dsmp.client.BuddyMgr;
import oem.edge.ed.odc.dsmp.client.ErrorHandler;
import oem.edge.ed.odc.dsmp.client.ErrorRunner;
import oem.edge.ed.odc.dsmp.client.FeedbackFlavor;
import oem.edge.ed.odc.dsmp.client.FeedbackFlavorHandler;
import oem.edge.ed.odc.dsmp.client.FileHeaderRenderer;
import oem.edge.ed.odc.dsmp.client.FileListFlavor;
import oem.edge.ed.odc.dsmp.client.FileStatusEvent;
import oem.edge.ed.odc.dsmp.client.FileStatusListener;
import oem.edge.ed.odc.dsmp.client.JCalendarChooser;
import oem.edge.ed.odc.dsmp.client.LinkResolver;
import oem.edge.ed.odc.dsmp.client.LocalFilePnl;
import oem.edge.ed.odc.dsmp.client.LocalFilePnlListener;
import oem.edge.ed.odc.dsmp.client.MessagePane;
import oem.edge.ed.odc.dsmp.client.TableSorter;
import oem.edge.ed.odc.dsmp.common.DboxException;
import oem.edge.ed.odc.dsmp.common.GroupInfo;
import oem.edge.ed.odc.util.ProxyDebugInterface;

import com.ibm.as400.webaccess.common.ConfigFile;
import com.ibm.as400.webaccess.common.ConfigSection;

/**
 * Insert the type's description here.
 * Creation date: (10/3/2002 1:29:26 PM)
 * @author: Mike Zarnick
 */
public class DropBoxPnl extends JPanel implements FileStatusListener, OptionListener {
	static public int WINDOW_OPEN = 0;
	static public int WINDOW_CLOSING = 1;
	static public int DESC_MAX = 1024;
	static private String NOUPDATE_OPT = "-CH_NOUPDATE";
	static private String TOKEN_OPT = "-CH_TOKEN";
	private boolean isWin = false;
	private boolean isExit = false;
	private boolean isFile = false;
	private ErrorHandler errorHandler = null;
	private DropboxAccess dboxAccess = null;
	private ConnectionFactory dboxFactory = null;
	private HashMap session = null;
	private String serviceUrl = "http://edesign5.fishkill.ibm.com/technologyconnect/dev";
	private BuddyMgr buddyMgr = null;
	private SourceMgr sourceMgr = null;
	private ConfigMgr cfg = null;
	private ConfigFile dboxini = null;
	private MsgHandler msgHandler = null;  //  @jve:visual-info  decl-index=0 visual-constraint="32,611"
	private DropBoxPnlListener listeners = null;
	private Frame ownerContainer = new JFrame();
	private JRootPane ownerRootPane = null;
	private int busyCursor = 0;
	private String saveFile = null;
	private Object btnPressed = null;
	private Vector users = new Vector();
	private Vector projects = new Vector();
	private Vector pools = null;
	private JCalendarChooser expirationCC = null;
	private JSplitPane ivjHostSplit = null;
	private JScrollPane ivjStatusSP = null;
	private JSplitPane ivjStatusSplit = null;
	private JTable ivjStatusTbl = null;
	private FileStatusTableModel ivjFileStatusTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="594,920"
	private JMenuItem ivjExitMI = null;
	private JMenuItem ivjLogoutMI = null;
	private JMenu ivjFileM = null;  // @jve:visual-info  decl-index=0 visual-constraint="984,41"
	private JSeparator ivjFileSep = null;
	private JButton ivjLoginTB = null;
	private JButton ivjLogoutTB = null;
	private JButton ivjStopTB = null;
	private JMenuItem ivjLoginMI = null;
	private JPanel ivjJPanel2 = null;
	private JLabel ivjUserLbl = null;
	private JLabel ivjPwdLbl = null;
	private JPasswordField ivjPwdTF = null;
	private JTextField ivjUserTF = null;
	private JButton ivjLoginCanBtn = null;
	private JButton ivjLoginOkBtn = null;
	private JMenuItem ivjCanXferMI = null;
	private JMenuItem ivjDetailsMI = null;
	private JSeparator ivjJSeparator1 = null;
	private JSeparator ivjJSeparator2 = null;
	private JMenuItem ivjRemoveAllMI = null;
	private JMenuItem ivjRemoveMI = null;
	private JPopupMenu ivjStatusPU = null;  //  @jve:visual-info  decl-index=0 visual-constraint="603,981"
	private JCheckBoxMenuItem ivjDebugMI = null;
	private JSeparator ivjJSeparator3 = null;
	private JLabel ivjInboxFileLbl = null;
	private JScrollPane ivjInboxFileSP = null;
	private JTable ivjInboxFileTbl = null;
	private JLabel ivjInboxPkgLbl = null;
	private JScrollPane ivjInboxPkgSP = null;
	private JTable ivjInboxPkgTbl = null;
	private JPanel ivjInboxPnl = null;
	private JButton ivjInboxRefreshBtn = null;
	private JButton ivjInboxTransferBtn = null;
	private JButton ivjOutboxDeleteBtn = null;
	private JLabel ivjOutboxFileLbl = null;
	private JScrollPane ivjOutboxFileSP = null;
	private JTable ivjOutboxFileTbl = null;
	private JLabel ivjOutboxPkgLbl = null;
	private JScrollPane ivjOutboxPkgSP = null;
	private JTable ivjOutboxPkgTbl = null;
	private JPanel ivjOutboxPnl = null;
	private JButton ivjOutboxRefreshBtn = null;
	private JTabbedPane ivjRemoteTP = null;
	private JButton ivjSendCommitBtn = null;
	private JButton ivjSendFileDeleteBtn = null;
	private JScrollPane ivjSendFileSP = null;
	private JTable ivjSendFileTbl = null;
	private JButton ivjSendPkgCreateBtn = null;
	private JButton ivjSendPkgDeleteBtn = null;
	private JButton ivjSendPkgEditBtn = null;
	private JLabel ivjSendPkgLbl = null;
	private JScrollPane ivjSendPkgSP = null;
	private JTable ivjSendPkgTbl = null;
	private JPanel ivjSendPnl = null;
	private InboxFileTM ivjInboxFileTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="590,621"
	private InboxPkgTM ivjInboxPkgTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="589,678"
	private OutboxFileTM ivjOutboxFileTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="587,729"
	private OutboxPkgTM ivjOutboxPkgTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="587,777"
	private SendFileTM ivjSendFileTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="596,825"
	private SendPkgTM ivjSendPkgTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="595,873"
	private JLabel ivjJLabel2 = null;
	private JLabel ivjJLabel5 = null;
	private JPanel ivjJPanel4 = null;
	private BorderFactory ivjPkgBF = null;  // @jve:visual-info  decl-index=0 visual-constraint="254,2893"
	private JButton ivjPkgCanBtn = null;
	private JButton ivjPkgOkBtn = null;
	private JMenuBar ivjDropBoxJMenuBar = null;  //  @jve:visual-info  decl-index=0 visual-constraint="854,632"
	private JButton ivjInboxFileDownBtn = null;
	private JButton ivjInboxPkgDownBtn = null;
	private JButton ivjOutboxFileDownBtn = null;
	private JButton ivjOutboxPkgDownBtn = null;
	private JButton ivjOutboxTransferBtn = null;
	private JCheckBox ivjExpressCB = null;
	private JTextField ivjToUserTF = null;
	private JButton ivjUserDelBtn = null;
	private JTextField ivjPkgNameTF = null;
	private javax.swing.JScrollPane pkgDescSP = null;
	private javax.swing.JTextArea pkgDescTA = null;
	private JButton ivjFileAclViewBtn = null;
	private JLabel ivjFileAclViewLbl = null;
	private JDialog ivjFileAclViewDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="26,1354"
	private JScrollPane ivjFileAclViewSP = null;
	private JTable ivjFileAclViewTbl = null;
	private FileAclsTM ivjFileAclViewTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="412,1417"
	private JLabel ivjFileAclViewNameLbl = null;
	private JButton ivjSendFileRestartBtn = null;
	private JSeparator ivjJSeparator4 = null;
	private JButton ivjOutboxFileViewBtn = null;
	private JButton ivjOutboxPkgEditBtn = null;
	private JButton ivjOutboxPkgViewBtn = null;
	private JButton ivjPkgAclViewBtn = null;
	private JLabel ivjPkgAclViewLbl = null;
	private JLabel ivjPkgAclViewNameLbl = null;
	private JScrollPane ivjPkgAclViewSP = null;
	private JTable ivjPkgAclViewTbl = null;
	private PkgAclsTM ivjPkgAclViewTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="408,1694"
	private JPanel ivjInboxFilePnl = null;
	private JPanel ivjInboxPkgPnl = null;
	private JPanel ivjOutboxFilePnl = null;
	private JPanel ivjOutboxPkgPnl = null;
	private JLabel ivjSendFileLbl = null;
	private JPanel ivjSendFilePnl = null;
	private JPanel ivjSendPkgPnl = null;
	private JPanel ivjToolBarPnl = null;
	private LocalFilePnl ivjLocalFilePnl = null;
	private JLabel ivjJLabel6 = null;
	private TableSorter ivjInboxFileSortTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="693,622"
	private TableSorter ivjInboxPkgSortTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="692,674"
	private TableSorter ivjOutboxFileSortTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="691,725"
	private TableSorter ivjOutboxPkgSortTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="692,775"
	private TableSorter ivjSendFileSortTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="699,821"
	private TableSorter ivjSendPkgSortTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="701,867"
	private TableSorter ivjFileAclViewSortTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="407,1471"
	private TableSorter ivjPkgAclViewSortTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="402,1743"
	private JLabel ivjJLabel7 = null;
	private JMenuItem ivjFilesDeleteMI = null;
	private JMenuItem ivjFilesDownMI = null;
	private JMenuItem ivjFilesFwdMI = null;
	private JMenu ivjFilesM = null;
	private JMenuItem ivjFilesViewMI = null;
	private JSeparator ivjJSeparator5 = null;
	private JMenu ivjLocalM = null;  // @jve:visual-info  decl-index=0 visual-constraint="823,20"
	private JMenuItem ivjPkgCreateMI = null;
	private JMenuItem ivjPkgDeleteMI = null;
	private JMenuItem ivjPkgDeliverMI = null;
	private JMenuItem ivjPkgDownMI = null;
	private JMenuItem ivjPkgEditMI = null;
	private JMenu ivjPkgM = null;
	private JMenuItem ivjPkgViewMI = null;
	private JMenuItem ivjRefreshMI = null;
	private JMenu ivjRemoteM = null;  // @jve:visual-info  decl-index=0 visual-constraint="120,875"
	private JMenuItem ivjStatCanMI = null;
	private JMenuItem ivjStatDetailsMI = null;
	private JMenuItem ivjStatRemAllMI = null;
	private JMenuItem ivjStatRemSelMI = null;
	private JMenu ivjStatusM = null;  // @jve:visual-info  decl-index=0 visual-constraint="403,875"
	private JMenuItem ivjFilesRestartMI = null;
	private JSeparator ivjJSeparator6 = null;
	private JSeparator ivjJSeparator7 = null;
	private JSeparator ivjJSeparator8 = null;
	private JSeparator ivjJSeparator9 = null;
	private JMenu helpM = null;
	private JMenuItem aboutMI = null;
	private JButton ivjSendPkgRefreshBtn = null;
	private JMenuItem ivjOptionsMI = null;
	private JLabel ivjJLabel = null;
	private JButton ivjUserNewAddBtn = null;
	private JButton ivjDownConfCanBtn = null;
	private JCheckBox ivjDownConfCB = null;
	private JButton ivjDownConfOkBtn = null;
	private JPanel ivjRecPnl = null;
	private JPanel recToPnl = null;
	private JLabel storagePoolLbl = null;
	private JComboBox storagePoolCB = null;
	private JLabel expirationLbl = null;
	private JButton expirationBtn = null;
	private JPanel ivjExpirePnl = null;
	private JLabel ivjPkgNameLbl = null;
	private JPanel ivjConfirmBtnPnl = null;
	private JButton ivjConfirmCanBtn = null;
	private JButton ivjConfirmNewBtn = null;
	private JButton ivjConfirmOkBtn = null;
	private JScrollPane ivjConfirmPkgSP = null;
	private JTable ivjConfirmPkgTbl = null;
	private JLabel ivjJLabel12 = null;
	private JMenuItem ivjGroupMI = null;
	private JButton ivjGrpQueryBtn = null;
	private JLabel ivjGrpQueryCompanyLbl = null;
	private JList ivjGrpQueryLB = null;
	private JLabel ivjGrpQueryLbl1 = null;
	private JLabel ivjGrpQueryLbl2 = null;
	private JLabel ivjGrpQueryLbl3 = null;
	private DefaultListModel ivjGrpQueryLM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="407,2893"
	private JLabel ivjGrpQueryNameLbl = null;
	private JButton ivjGrpQueryOkBtn = null;
	private JLabel ivjGrpQueryOwnerLbl = null;
	private JScrollPane ivjGrpQuerySP = null;
	private JPanel ivjGrpQueryCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="622,1990"
	private JDialog ivjGrpQueryDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="32,2724"
	private JDialog ivjDownConfirmDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="30,2166"
	private JDialog ivjPkgEditDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="34,3094"
	private JDialog ivjPkgAclViewDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="27,1627"
	private JDialog ivjLoginDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="182,610"
	private JDialog ivjConfirmDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="25,776"
	private ManageGroups ivjManageGroups = null;  //  @jve:visual-info  decl-index=0 visual-constraint="33,3667"
	private JPanel ivjConfirmCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="782,894"
	private JPanel ivjFileAclViewCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="73,1619"
	private JPanel ivjLoginCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="77,1377"
	private JPanel ivjPkgAclViewCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="624,1616"
	private JPanel ivjPkgEditCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="343,2793"
	private DropboxOptions ivjDropboxOptions = null;  //  @jve:visual-info  decl-index=0 visual-constraint="27,1043"
	private JCheckBox ivjSendEmailCB = null;
	private JCheckBox pkgItarCB = null;
	private JLabel ivjJLabel1 = null;
	private JPanel ivjJPanel1 = null;
	private JPanel ivjStatusPnl = null;
	private JCheckBox ivjReturnReceiptCB = null;
	private JButton ivjInboxAddSenderBtn = null;
	private JCheckBox ivjPkgAddFilesInfoCB = null;
	private JPanel ivjPkgAddFilesInfoCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="74,2002"
	private JButton ivjPkgAddFilesInfoOkBtn = null;
	private JTextArea ivjPkgAddFilesInfoTA = null;
	private JMenuItem ivjPkgAddSenderMI = null;
	private JButton ivjPkgAddFilesBtn = null;
	private JDialog ivjPkgAddFilesInfoDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="28,1896"
	private JLabel ivjFilterLbl = null;
	private JPanel ivjJPanel5 = null;
	private JLabel ivjDownConfLbl = null;
	private JTextField ivjDownConfTF = null;
	private JButton ivjDownConfChgBtn = null;
	private JPanel ivjDownConfirmCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="57,2434"
	private JButton ivjEditAddBtn = null;
	private JPanel ivjEditBtnPnl = null;
	private JList ivjEditFromLB = null;
	private DefaultListModel ivjEditFromLM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="632,3308"
	private JScrollPane ivjEditFromSP = null;
	private JButton ivjEditRemBtn = null;
	private JList ivjEditToLB = null;
	private DefaultListModel ivjEditToLM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="638,3257"
	private JScrollPane ivjEditToSP = null;
	private JLabel ivjJLabel3 = null;
	private JLabel ivjJLabel4 = null;
	private JLabel ivjJLabel9 = null;
	private JPanel ivjJPanel3 = null;
	private JPanel ivjJPanel6 = null;
	private JLabel ivjToUserLbl = null;
	private JButton ivjInboxPkgTransferBtn = null;
	private JMenuItem ivjPkgFwdMI = null;
	private JCheckBoxMenuItem ivjCompressMI = null;
	private JPanel ivjDownInfoCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="618,2445"
	private JButton ivjDownInfoOkBtn = null;
	private JTextArea ivjDownInfoTA = null;
	private JLabel ivjDownConfWarnLbl = null;
	private JButton ivjDownConfInfoBtn = null;
	private JDialog ivjDownInfoDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="31,2396"
	private javax.swing.JMenuItem createIconMI = null;
	private javax.swing.JMenuItem msgHandlerMI = null;
	private javax.swing.JButton inboxPkgViewBtn = null;
	private javax.swing.JMenuItem pkgViewDescMI = null;  //  @jve:visual-info  decl-index=0 visual-constraint="957,690"
	private javax.swing.JPanel viewDescCP = null;
	private javax.swing.JDialog viewDescDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="507,1864"
	private javax.swing.JScrollPane viewDescSP = null;
	private javax.swing.JTextArea viewDescTA = null;
	private AboutWindow aboutWindow = null;
	private JMenuItem itarMI = null;
	private JPanel pkgDescPnl = null;
	private JLabel pkgDescLbl = null;
	private JLabel pkgDescCharLbl = null;
	private JLabel pkgDescLbl2 = null;
	private JLabel itarLbl = null;
	private JMenuItem connectivityMI = null;
	private JLabel crossCompanyLbl = null;
/**
 * Generic button listener. Sets DropBox.btnPressed to the source object of the action and
 * disposes the JDialog, if defined.
 */
private class BtnPressListener implements ActionListener {
	private JDialog dlg = null;
	BtnPressListener(JDialog dlg) {
		this.dlg = dlg;
	}
	public void actionPerformed(ActionEvent e) {
		try {
			btnPressed = e.getSource();
			
			if (dlg != null) {
				dlg.dispose();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
}
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
 * Generic runnable to turn on the busy cursor. Use busyCursorOn as the Runnable.
 * eg. SwingUtilities.invokeLater(busyCursorOn);
 */
private class BusyCursorOn implements Runnable {
	public void run() {
		busyCursor(true);
	}
}
private BusyCursorOn busyCursorOn = new BusyCursorOn();
/**
 * Timer for the busy cursor activity. Don't want it to stay on too long.
 **/
private class BusyCursorTimer extends Thread {
	public void run() {
		try {
			Thread.sleep(30000);
			if (Thread.currentThread() == busyCursorTimer) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (! msgHandler.isShowing()) {
							String[] msgs = { "The current operation is taking a long time.", "The messages window has been displayed for your awareness." };
							getMsgHandlerMI().doClick();
							JOptionPane.showMessageDialog(msgHandler,msgs,
									"Possible Problem",JOptionPane.WARNING_MESSAGE);
						}
					}
				});
			}
		}
		catch(InterruptedException e) {
		}
	}
}
private Thread busyCursorTimer = null;
/**
 * Handles committing a package.
 */
private class CommitHandler extends RefreshHandler implements Runnable {
	public PackageInfo[] pkgs = null;
	public void run() {
		try {
			// Commit the selected package(s).
			for (int i = 0; i < pkgs.length; i++) {
				try {
					dboxAccess.commitPackage(pkgs[i].getPackageId());
					SwingUtilities.invokeLater(new ErrorRunner(DropBoxPnl.this,"\"" + pkgs[i].getPackageName() + "\" is sent.","Package Sent",false));
				} catch (DboxException e) {
					errorHandler.addMsg("\"" + pkgs[i].getPackageName() + "\": " + e.getMessage(),"Send Package Failed");
				} catch (RemoteException e) {
					errorHandler.addMsg("\"" + pkgs[i].getPackageName() + "\": " + e.getMessage(),"Send Package Failed");
				}
			}

			// Refresh the package tables.
			busyCursor(true);
			inbox = false;
			all = false;
			super.run();

			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}    
	}
}
private class CommitListener extends CommitHandler implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		try {
			busyCursor(true);

			// Get the package(s) to commit.
			int[] s = getSendPkgTbl().getSelectedRows();
			pkgs = new PackageInfo[s.length];

			for (int i = 0; i < s.length; i++) {
				int u = getSendPkgSortTM().getUnsortedIndex(s[i]);
				pkgs[i] = getSendPkgTM().getPackageInfo(u);
			}

			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}    
	}
}
private CommitListener commitListener = new CommitListener();
/**
 * Handles creation of a new package.
 */
private class CreatePkgActionListener implements ActionListener, Runnable {
	protected PackageInfo pkgInfo = null;
	protected Vector pkgAcls = null;
	protected Vector pkgItems = null;
	protected List pkgDropItems = null;
	protected boolean pkgAutoCommit = false;
	protected Vector groups = new Vector();
	public void actionPerformed(ActionEvent e) {
		try {
			busyCursor(true);

			// No package, items or acls.
			pkgInfo = null;
			pkgItems = null;
			pkgDropItems = null;
			pkgAcls = null;

			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void autoCommitCompleted() {
		// If the created package is in no longer in the drafts table,
		// then switch to the outbox tab.
		if (getSendPkgTM().getPackageRow(pkgInfo.getPackageId()) == -1) {
			getRemoteTP().setSelectedComponent(getOutboxPnl());
		}
	}
	public void createPkg() {
		try {
			// Load the latest buddy list.
			users = buddyMgr.getBuddyList();
	
			// Clear selections and fields.
			getEditFromLB().clearSelection();
			getEditToLB().clearSelection();
			getToUserTF().setText("");
			setNameAndDescription();
	
			// Populate the from listbox.
			getEditFromLM().clear();
			getEditToLM().clear();
			Enumeration e = users.elements();
			while (e.hasMoreElements()) {
				Buddy buddy = new Buddy((String) e.nextElement(),Buddy.USER);
				addNameToList(buddy,getEditFromLM());
			}
			e = groups.elements();
			while (e.hasMoreElements()) {
				Buddy buddy = new Buddy((String) e.nextElement(),Buddy.GROUP);
				addNameToList(buddy,getEditFromLM());
			}
			e = projects.elements();
			while (e.hasMoreElements()) {
				Buddy buddy = new Buddy((String) e.nextElement(),Buddy.PROJECT);
				addNameToList(buddy,getEditFromLM());
			}
	
			// Prepare the storage pools, which will set up the GUI.
			expirationListener.isAdjusting = true;
			getStoragePoolCB().removeAllItems();
			if (pools != null && pools.size() > 0) {
				for (int i = 0; i < pools.size(); i++) {
					PoolInfo p = (PoolInfo) pools.elementAt(i);
					getStoragePoolCB().addItem(p.getPoolName());
				}
				getStoragePoolCB().setEnabled(true);
				getStoragePoolCB().setSelectedIndex(0);
				
				PoolInfo p = (PoolInfo) pools.elementAt(0);
				expirationListener.prepareExpirationCC(p.getPoolMaxDays(),p.getPoolDefaultDays(),0);
			}
			else {
				getStoragePoolCB().setEnabled(false);
				expirationListener.prepareExpirationCC(14,0,0);
			}
			expirationListener.isAdjusting = false;

			// Reset push buttons.
			getUserNewAddBtn().setEnabled(false);
			getUserDelBtn().setEnabled(false);
			getEditAddBtn().setEnabled(false);
			getEditRemBtn().setEnabled(false);
			getGrpQueryBtn().setEnabled(false);
			getExpressCB().setSelected(false);
			getSendEmailCB().setSelected(getDropboxOptions().sendNotification);
			getReturnReceiptCB().setSelected(getDropboxOptions().returnReceipt);
			getPkgItarCB().setSelected(false);
	
			// No items to add to package? Express is not an option.
			if (pkgItems == null && pkgDropItems == null) {
				getExpressCB().setEnabled(false);
			}
		
			// If user declined tip, or we're an express package, no add files button.
			if (dboxini.getBoolProperty("NOADDFILETIP",false) || pkgItems != null) {
				getPkgAddFilesBtn().setVisible(false);
			}
			else {
				getPkgAddFilesBtn().setVisible(true);
				getPkgAddFilesBtn().setEnabled(false);
			}
			getPkgOkBtn().setEnabled(false);

			// No cross company condition is possible, no recipients yet.
			crossCompanyLbl.setVisible(false);

			// Turn off the busy cursor from the create button click.
			busyCursor(false);
	
			// Show the edit pkg dialog and wait.
			btnPressed = null;
			getPkgEditDlg().setLocationRelativeTo(DropBoxPnl.this);
			getPkgEditDlg().show();
			getExpressCB().setEnabled(true);
	
			if (btnPressed == getPkgOkBtn() || btnPressed == getPkgAddFilesBtn()) {
				busyCursor(true);
	
				// Save the ACLs to editPkgAcls.
				pkgAcls = new Vector();
				for (int i = 0; i < getEditToLM().size(); i++) {
					Buddy buddy = (Buddy) getEditToLM().elementAt(i);
					pkgAcls.addElement(buddy);
				}
	
				// Determine the package expiration timestamp.
				long expire = expirationListener.getExpiration();
	
				// Save the package info to editPkg.
				pkgInfo = new PackageInfo();
				pkgInfo.setPackageName(getPkgNameTF().getText());
				pkgInfo.setPackageDescription(getPkgDescTA().getText());
				pkgInfo.setPackageExpiration(expire);
				pkgInfo.setPackageReturnReceipt(getReturnReceiptCB().isSelected());
				pkgInfo.setPackageSendNotification(getSendEmailCB().isSelected());
				pkgInfo.setPackageItar(getPkgItarCB().isSelected());

				if (getStoragePoolCB().isEnabled()) {
					int i = getStoragePoolCB().getSelectedIndex();
					PoolInfo p = (PoolInfo) pools.elementAt(i);
					pkgInfo.setPackagePoolId(p.getPoolId());
				}
				else {
					pkgInfo.setPackagePoolId(DropboxAccess.PUBLIC_POOL_ID);
				}

				// Auto commit this package?
				pkgAutoCommit = getExpressCB().isSelected();
			}

			// TODO: Not needed anymore.
			/*
			// Move any entries in the To list back to the From list.
			while (getEditToLM().size() > 0) {
				Buddy buddy = (Buddy) getEditToLM().remove(0);
				addNameToList(buddy,getEditFromLM());
			}
			*/
	
			// Finally, save the buddy list.
			users = buddyMgr.setBuddyList(users);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void run() {
		try {
			// Send/Recieve notification default options
			try {
				Vector args = new Vector();
				args.addElement(DropboxAccess.SendNotificationDefault);
				args.addElement(DropboxAccess.ReturnReceiptDefault);
				Map options = dboxAccess.getOptions(args);
				String option = (String) options.get(DropboxAccess.SendNotificationDefault);
				getDropboxOptions().sendNotification = options != null && option.equalsIgnoreCase("TRUE");
				option = (String) options.get(DropboxAccess.ReturnReceiptDefault);
				getDropboxOptions().returnReceipt = option != null && option.equalsIgnoreCase("TRUE");
			} catch (DboxException e) {
				getDropboxOptions().sendNotification = false;
				getDropboxOptions().returnReceipt = false;
				errorHandler.addMsg(e.getMessage(),"Query Options Failed");
			} catch (RemoteException e) {
				getDropboxOptions().sendNotification = false;
				getDropboxOptions().returnReceipt = false;
				errorHandler.addMsg(e.getMessage(),"Query Options Failed");
			}
	
			// Groups
			try {
				// Need to query user's accessible groups first.
				groups.removeAllElements();
				Map grps = dboxAccess.queryGroups(false,false);
				Iterator names = grps.keySet().iterator();
				while (names.hasNext()) {
					groups.addElement(names.next());
				}
			} catch (DboxException e) {
				errorHandler.addMsg(e.getMessage(),"Query Groups Failed");
			} catch (RemoteException e) {
				errorHandler.addMsg(e.getMessage(),"Query Groups Failed");
			}

			// Storage Pools
			try {
				// Need to query user's accessible storage pools.
				pools = dboxAccess.queryStoragePoolInformation();
			} catch (DboxException e) {
				errorHandler.addMsg(e.getMessage(),"Query Storage Failed");
				pools = null;
			} catch (RemoteException e) {
				errorHandler.addMsg(e.getMessage(),"Query Storage Failed");
				pools = null;
			}

			// Invoke createPkg on the GUI event thread.
			SwingUtilities.invokeAndWait(new MethodRunner(this,"createPkg"));

			// User didn't cancel, and is ready to create a package?
			if (pkgInfo != null) {
				// Create the package now.
				int pkgVal = 0;
				int pkgMsk = PackageInfo.SENDNOTIFY;
				if (pkgInfo.getPackageSendNotification()) {
					pkgVal |= PackageInfo.SENDNOTIFY;
				}
				pkgMsk |= PackageInfo.RETURNRECEIPT;
				if (pkgInfo.getPackageReturnReceipt()) {
					pkgVal |= PackageInfo.RETURNRECEIPT;
				}
				pkgMsk |= PackageInfo.ITAR;
				if (pkgInfo.getPackageItar()) {
					pkgVal |= PackageInfo.ITAR;
				}
				pkgMsk &= 0xFF;
				pkgVal &= 0xFF;
	
				try {
					long pkgID = dboxAccess.createPackage(pkgInfo.getPackageName(),
									pkgInfo.getPackageDescription(),
									pkgInfo.getPackagePoolId(),
									pkgInfo.getPackageExpiration(),
									null,pkgMsk,pkgVal);
					pkgInfo.setPackageId(pkgID);
				} catch (DboxException e) {
					// Failed to create the package, nothing else to do...
					SwingUtilities.invokeLater(busyCursorOff);
					errorHandler.addMsg(e.getMessage(),"Create Package Failed");
					return;
				} catch (RemoteException e) {
					// Failed to create the package, nothing else to do...
					SwingUtilities.invokeLater(busyCursorOff);
					errorHandler.addMsg(e.getMessage(),"Create Package Failed");
					return;
				}
	
				// Show the new package in the send table.
				SwingUtilities.invokeLater(new MethodRunner(this,"showPackage"));
	
				// Send the acls.
				boolean updatedUserList = false;
				for (byte i = 0; i < pkgAcls.size(); i++) {
					Buddy buddy = (Buddy) pkgAcls.elementAt(i);
		
					try {
						if (buddy.type == Buddy.PROJECT) {
							dboxAccess.addProjectAcl(pkgInfo.getPackageId(),buddy.name);
						}
						else if (buddy.type == Buddy.GROUP) {
							dboxAccess.addGroupAcl(pkgInfo.getPackageId(),buddy.name);
						}
						else {
							dboxAccess.addUserAcl(pkgInfo.getPackageId(),buddy.name);
						}
					}
					catch (DboxException e) {
						// Failed to add an Acl, if a user, remove from buddy list.
						if (e.getMessage() != null && buddy.type == Buddy.USER) {
							users.removeElement(buddy.name);
							updatedUserList = true;
						}
						pkgAutoCommit = false;
						errorHandler.addMsg(e.getMessage(),"Add Recipient Failed");
					}
					catch (RemoteException e) {
						pkgAutoCommit = false;
						errorHandler.addMsg(e.getMessage(),"Add Recipient Failed");
					}
				}

				// Failed acl adds caused user deletes? Save buddy list.
				if (updatedUserList) {
					users = buddyMgr.setBuddyList(users);
				}

				// Query the package (to update package table info).
				try {
					pkgInfo = dboxAccess.queryPackage(pkgInfo.getPackageId(),true);
	
					SwingUtilities.invokeLater(new MethodRunner(this,"updatePackage"));
				} catch (DboxException e) {
					errorHandler.addMsg(e.getMessage(),"Query Package Failed");
				} catch (RemoteException e) {
					errorHandler.addMsg(e.getMessage(),"Query Package Failed");
				}
		
				// Any initial items to be added to the package (aka express package)?
				if (pkgItems != null || pkgDropItems != null) {
					// Drop a bunch of files to upload.
					if (pkgDropItems != null) {
						// Prepare for a vector of FileBundle objects
						Vector fbs = new Vector();

						// For each dropped item:
						for (int i = 0; i < pkgDropItems.size(); i++) {
							// A new FileBundle
							FileBundle fb = new FileBundle();
							fb.fileNames = new Vector();
							fb.files = new Vector();

							// Base directory is directory of dropped item.
							File f = (File) pkgDropItems.get(i);
							fb.baseDirectory = f.getParent();

							// Scan dropped item and then add FileBundle to vector.
							scanDirectory(fb.fileNames,fb.files,f.getName(),f.getPath());
							fbs.addElement(fb);
						}

						// Queue the uploads (also handles auto-commit).
						getFileStatusTM().uploadFiles(pkgAutoCommit,pkgInfo.getPackageId(),fbs,pkgInfo.getPackageName());
					}
					// String types indicate file uploads.
					else if (pkgItems.elementAt(0) instanceof String) {
						// Prepare the vector of one FileBundle
						Enumeration items = pkgItems.elements();

						Vector fbs = new Vector();
						FileBundle fb = new FileBundle();
						fbs.addElement(fb);
						fb.fileNames = new Vector();
						fb.files = new Vector();

						// First element is the base directory.
						fb.baseDirectory = (String) items.nextElement();

						// All other elements are the files/dirs.
						while (items.hasMoreElements()) {
							String name = (String) items.nextElement();
							String absName = fb.baseDirectory + File.separator + name;
							scanDirectory(fb.fileNames,fb.files,name,absName);
						}
						// Queue the uploads (also handles auto-commit).
						getFileStatusTM().uploadFiles(pkgAutoCommit,pkgInfo.getPackageId(),fbs,pkgInfo.getPackageName());
					}
					// Otherwise, Long types indicate file and package forwarding.
					else {
						Enumeration items = pkgItems.elements();
						while (items.hasMoreElements()) {
							try {
								Long item = (Long) items.nextElement();
								dboxAccess.addItemToPackage(pkgInfo.getPackageId(),item.longValue());
							} catch (DboxException e) {
								errorHandler.addMsg(e.getMessage(),"Add File Failed");
								pkgAutoCommit = false;
							} catch (RemoteException e) {
								errorHandler.addMsg(e.getMessage(),"Add File Failed");
								pkgAutoCommit = false;
							}
						}
	
						// Commit the package, if expected.
						if (pkgAutoCommit) {
							// Commit the package.
							busyCursor(true);
							commitListener.pkgs = new PackageInfo[1];
							commitListener.pkgs[0] = pkgInfo;
							commitListener.run();
	
							// Switch over to the sent tab if commit worked.
							SwingUtilities.invokeLater(new MethodRunner(this,"autoCommitCompleted"));
						}
						// No package commit, so must refresh the package.
						else {
							QueryPkgHandler h = new QueryPkgHandler();
							h.isUpload = true;
							h.pkgId = pkgInfo.getPackageId();
							h.run();
						}
					}
				}
				// Just a plain old package create.
				else {
					// Handle displaying of the add file tip.
					SwingUtilities.invokeLater(new MethodRunner(this,"showTip"));
				}
		
				SwingUtilities.invokeLater(busyCursorOff);
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	protected void setNameAndDescription() {
		getPkgEditDlg().setTitle("Create New Package");
		getPkgNameTF().setText("");
		getPkgDescTA().setText("");
	}
	public void showTip() {
		if (! dboxini.getBoolProperty("NOADDFILETIP",false) && btnPressed == getPkgAddFilesBtn()) {
			getPkgAddFilesInfoDlg().setLocationRelativeTo(DropBoxPnl.this);
			getPkgAddFilesInfoDlg().setVisible(true);
		}
	}
	public void showPackage() {
		// Empty out the file table model as the selection of the
		// new package may not fire an event if it replaces the currently
		// selected package's index (based on sorting). 
		emptySendFile();
		
		// Add the package and select it.
		getSendPkgTM().addPackage(pkgInfo);
		int r = getSendPkgTM().getPackageRow(pkgInfo.getPackageId());
		r = getSendPkgSortTM().getSortedIndex(r);
		getSendPkgTbl().getSelectionModel().setSelectionInterval(r,r);
	}
	public void updatePackage() {
		// Update the package information.
		getSendPkgTM().updatePackage(pkgInfo);
	}
}
private CreatePkgActionListener createPkgActionListener = new CreatePkgActionListener();
/**
 * Handles delete of packages and files.
 */
private class DeleteListener implements ActionListener, Runnable {
	private PackageInfo pkg = null;
	private long[] itemIDs = null;
	private Vector files = null;
	public void actionPerformed(ActionEvent e) {
		try {
			String[] msg;
	
			if (e.getSource() == getOutboxDeleteBtn() ||
				(e.getSource() == getPkgDeleteMI() && getRemoteTP().getSelectedComponent() == getOutboxPnl())) {
				int[] s = getOutboxPkgTbl().getSelectedRows();
				itemIDs = new long[s.length];
				msg = new String[s.length+1];
	
				if (s.length == 1)
					msg[0] = "Delete the following sent package?";
				else
					msg[0] = "Delete the following sent packages?";
	
				for (int i = 0; i < s.length; i++) {
					int u = getOutboxPkgSortTM().getUnsortedIndex(s[i]);
					itemIDs[i] = getOutboxPkgTM().getPackageID(u);
					msg[i+1] = "   " + getOutboxPkgTM().getPackageName(u);
				}
	
				pkg = null;
			}
			else if (e.getSource() == getSendPkgDeleteBtn() ||
					(e.getSource() == getPkgDeleteMI() && getRemoteTP().getSelectedComponent() == getSendPnl())) {
				int[] s = getSendPkgTbl().getSelectedRows();
				itemIDs = new long[s.length];
				msg = new String[s.length+1];
	
				if (s.length == 1)
					msg[0] = "Delete the following unsent package?";
				else
					msg[0] = "Delete the following unsent packages?";
	
				for (int i = 0; i < s.length; i++) {
					int u = getSendPkgSortTM().getUnsortedIndex(s[i]);
					itemIDs[i] = getSendPkgTM().getPackageID(u);
					msg[i+1] = "   " + getSendPkgTM().getPackageName(u);
				}
	
				pkg = null;
			}
			else {
				int[] s = getSendFileTbl().getSelectedRows();
				itemIDs = new long[s.length];
				msg = new String[s.length+1];
	
				pkg = getSendPkgTM().getPackageInfo(getSendPkgSortTM().getUnsortedIndex(getSendPkgTbl().getSelectedRow()));
	
				if (s.length == 1)
					msg[0] = "Delete the following file from package \"" + pkg.getPackageName() + "\"?";
				else
					msg[0] = "Delete the following files from package \"" + pkg.getPackageName() + "\"?";
	
				for (int i = 0; i < s.length; i++) {
					int u = getSendFileSortTM().getUnsortedIndex(s[i]);
					itemIDs[i] = getSendFileTM().getFileID(u);
					msg[i+1] = "   " + getSendFileTM().getFileName(u);
				}
			}
	
			int result = MessagePane.showConfirmDialog(DropBoxPnl.this,msg,"Confirm Delete",MessagePane.YES_NO_OPTION);
	
			if (result == MessagePane.YES_OPTION) {
				// We're busy now...
				busyCursor(true);
	
				WorkerThread t = new WorkerThread(this);
				t.start();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}    
	}
	public void refreshFilesInSendTbl() {
		emptySendFile();
		getSendFileTM().addFiles(pkg.getPackageId(),files);
	}
	public void run() {
		try {
			// Packages to delete?
			if (pkg == null) {
				for (int i = 0; i < itemIDs.length; i++) {
					try {
						dboxAccess.deletePackage(itemIDs[i]);
					} catch (DboxException e) {
						errorHandler.addMsg(e.getMessage(),"Delete Package Failed");
					} catch (RemoteException e) {
						errorHandler.addMsg(e.getMessage(),"Delete Package Failed");
					}
				}
	
				// Refresh the package tables.
				busyCursor(true);
				outboxRefreshListener.inbox = false;
				outboxRefreshListener.all = false;
				outboxRefreshListener.run();
			}
			// Files from the current draft package.
			else {
				for (int i = 0; i < itemIDs.length; i++) {
					try {
						dboxAccess.removeItemFromPackage(pkg.getPackageId(),itemIDs[i]);
					} catch (DboxException e) {
						errorHandler.addMsg(e.getMessage(),"Delete File Failed");
					} catch (RemoteException e) {
						errorHandler.addMsg(e.getMessage(),"Delete File Failed");
					}
				}
	
				// Refresh the package content.
				try {
					files = dboxAccess.queryPackageContents(pkg.getPackageId());
					SwingUtilities.invokeLater(new MethodRunner(this,"refreshFilesInSendTbl"));
				} catch(DboxException e) {
					errorHandler.addMsg(e.getMessage(),"Query Contents Failed");
				} catch(RemoteException e) {
					errorHandler.addMsg(e.getMessage(),"Query Contents Failed");
				}


				// Refresh the package information.
				try {
					pkg = dboxAccess.queryPackage(pkg.getPackageId(),true);
	
					SwingUtilities.invokeLater(new MethodRunner(this,"updatePackage"));
				} catch (DboxException e) {
					errorHandler.addMsg(e.getMessage(),"Query Package Failed");
				} catch (RemoteException e) {
					errorHandler.addMsg(e.getMessage(),"Query Package Failed");
				}
			}
	
			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}    
	}
	public void updatePackage() {
		// Update the package information.
		if (getRemoteTP().getSelectedComponent() == getSendPnl()) {
			getSendPkgTM().updatePackage(pkg);
		}
		else {
			getOutboxPkgTM().updatePackage(pkg);
		}
	}
}
private DeleteListener deleteListener = new DeleteListener();
/**
 * Handles download of files.
 */
private class DownloadFileHandler extends MouseAdapter implements ActionListener, FeedbackFlavorHandler, Runnable {
	private PackageInfo pkg = null;
	private FileInfo[] files = null;
	private boolean noOverWrite = false;
	private String destination = null;
	public void mouseClicked(MouseEvent e) {
		try {
			if (e.getClickCount() == 2 && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
				int i;
				boolean isInbox;
				
				if (e.getSource() == getInboxFileTbl()) {
					isInbox = true;
					JTable table = (JTable) e.getSource();
					i = table.getSelectedRowCount();
				}
				else if (e.getSource() == getOutboxFileTbl()) {
					isInbox = false;
					JTable table = (JTable) e.getSource();
					i = table.getSelectedRowCount();
				}
				else {
					// Unsupported source.
					System.out.println("DownloadFileHandler.mouseClicked: unknown event source, ignored.");
					return;
				}

				if (i == 1) {
					downloadFiles(isInbox,getLocalFilePnl().getLocalTM().getDirectory());
				}
				else {
					System.out.println("User doubled clicked table and " + i + " items were selected as a result");
				}
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void dropOnTarget(Object data,Object source) {
		// No target destination supplied? No good.
		if (data == null) {
			System.out.println("DownloadFileHandler.dropOnTarget: missing destination, drop ignored.");
			return;
		}

		// Determine the data type. We only like String and File.
		File f;
		if (data instanceof String) {
			f = new File((String) data);
		}
		else if (data instanceof File) {
			f = (File) data;
		}
		else {
			// Unknown target destination type supplied.
			System.out.println("DownloadFileHandler.dropOnTarget: unrecognized data type, drop ignored.");
			return;
		}

		// We want the target directory.
		String directory;
		if (f.isDirectory()) {
			directory = f.getPath();
		}
		else {
			directory = f.getParent();
		}

		if (source == getInboxFileTbl()) {
			downloadFiles(true,directory);
		}
		else if (source == getOutboxFileTbl()) {
			downloadFiles(false,directory);
		}
		else {
			// Unsupported source.
			System.out.println("DownloadFileHandler.dropOnTarget: unknown source, drop ignored.");
			return;
		}
	}
	public void actionPerformed(ActionEvent e) {
		boolean isInbox = e.getSource() == getInboxFileDownBtn() ||
			(e.getSource() == getFilesDownMI() && getRemoteTP().getSelectedComponent() == getInboxPnl());
		boolean isOutbox = e.getSource() == getOutboxFileDownBtn() ||
			(e.getSource() == getFilesDownMI() && getRemoteTP().getSelectedComponent() == getOutboxPnl());
		
		if (isInbox || isOutbox) {
			downloadFiles(isInbox,getLocalFilePnl().getLocalTM().getDirectory());
		}
		else {
			// Unsupported source.
			System.out.println("DownloadFileHandler.actionPerformed: unknown event source, ignored.");
			return;
		}
	}
	private void downloadFiles(boolean isInbox, String directory) {
		try {
			int[] s;

			if (isInbox) {
				int u = getInboxPkgSortTM().getUnsortedIndex(getInboxPkgTbl().getSelectedRow());
				pkg = getInboxPkgTM().getPackageInfo(u);

				s = getInboxFileTbl().getSelectedRows();
			}
			else {
				int u = getOutboxPkgSortTM().getUnsortedIndex(getOutboxPkgTbl().getSelectedRow());
				pkg = getOutboxPkgTM().getPackageInfo(u);

				s = getOutboxFileTbl().getSelectedRows();
			}

			if (s.length == 1) {
				// Set up prompting for a single file.
				String DownConfFilePrompt = "Press Ok to download file as:";
				String DownConfFileMsg = "The file will be downloaded as specified." +
					" You may rename the file or change its location by keying a new path in the field above." +
					" You may also change the directory by pressing the Change Directory button and" +
					" using the file chooser to select a new directory." +
					" If the destination file exists, an attempt to resume the download will be made." +
					" If resumption is not possible, the file will be overwritten." + 
					" Check the box below to prevent the over-write of an existing file.";
				getDownConfLbl().setText(DownConfFilePrompt);
				getDownInfoTA().setText(DownConfFileMsg);
	
				// Set up saveFile, used by confirmation dialog to preserve relative path
				// information associated with the file (see DropBox.changeDirectory).
				if (isInbox) {
					FileInfo fi = getInboxFileTM().getFileInfo(getInboxFileSortTM().getUnsortedIndex(getInboxFileTbl().getSelectedRow()));
					saveFile = fi.getFileName().replace('/',File.separatorChar);
				}
				else {
					FileInfo fi = getOutboxFileTM().getFileInfo(getOutboxFileSortTM().getUnsortedIndex(getOutboxFileTbl().getSelectedRow()));
					saveFile = fi.getFileName().replace('/',File.separatorChar);
				}
	
				isFile = true; // Controls behaviour in textChgDownConf();

				getDownConfTF().setText(directory + File.separatorChar + saveFile);
			}
			else {
				// Set up prompting for multiple files.
				String DownConfFilesPrompt = "Press Ok to download files to this directory:";
				String DownConfFilesMsg = "Files will be downloaded to the specified directory." +
					" Relative paths will be maintained." +
					" You may change the directory by keying a new path in the field above or" +
					" by pressing the Change Directory button and using the file chooser to select a new directory." +
					" For existing files, an attempt to resume the download will be made." +
					" If resumption is not possible, the file will be overwritten." + 
					" Check the box below to prevent the over-write of an existing file.";
				getDownConfLbl().setText(DownConfFilesPrompt);
				getDownInfoTA().setText(DownConfFilesMsg);
				isFile = false; // Controls behaviour in textChgDownConf();
				saveFile = null; // Not used.
				getDownConfTF().setText(directory);
			}
	
			// Ask user for download destination (file or dir, depending on number of files selected)?
			btnPressed = null;
			getDownConfirmDlg().setLocationRelativeTo(DropBoxPnl.this);
			getDownConfirmDlg().setVisible(true);
			if (btnPressed != getDownConfOkBtn()) {
				// Cancelled.
				return;
			}

			// If we are downloading from an ITAR package, certify.
			if (pkg.getPackageItar() &&
				! itarListener.certifySession(ownerContainer)) {
				return;
			}

			busyCursor(true);

			noOverWrite = getDownConfCB().isSelected();
			files = new FileInfo[s.length];
			destination = getDownConfTF().getText().trim();
			
			// TODO: If destination is a windows link, resolve to real file/directory.
	
			if (isInbox) {
				for (int i = 0; i < s.length; i++) {
					int u = getInboxFileSortTM().getUnsortedIndex(s[i]);
					files[i] = getInboxFileTM().getFileInfo(u);
				}
			}
			else {
				for (int i = 0; i < s.length; i++) {
					int u = getOutboxFileSortTM().getUnsortedIndex(s[i]);
					files[i] = getOutboxFileTM().getFileInfo(u);
				}
			}
	
			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void run() {
		try {
			if (files.length == 1) {
				File path = new File(destination);
				String error = getFileStatusTM().downloadFile(pkg,files[0],path.getName(),path.getParent(),noOverWrite);
				if (error != null) {
					JOptionPane.showMessageDialog(DropBoxPnl.this,files[0].getFileName() + ": " + error,"Download File",JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				for (int i = 0; i < files.length; i++) {
					Long len = new Long(files[i].getFileSize());
					String error = getFileStatusTM().downloadFile(pkg,files[i],files[i].getFileName(),destination,noOverWrite);
					if (error != null) {
						JOptionPane.showMessageDialog(DropBoxPnl.this,files[i].getFileName() + ": " + error,"Download File",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
	
			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
}
private DownloadFileHandler downloadFileHandler = new DownloadFileHandler();
/**
 * Handles download of packages.
 */
private class DownloadPkgHandler extends MouseAdapter implements ActionListener, FeedbackFlavorHandler, Runnable {
	private PackageInfo[] pkgs = null;
	private boolean noOverWrite = false;
	private String directoryName = null;
	public void mouseClicked(MouseEvent e) {
		try {
			if (e.getClickCount() == 2 && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
				JTable table = (JTable) e.getSource();
				int i = table.getSelectedRowCount();
				if (i == 1) {
					if (table == getInboxPkgTbl()) {
						downloadPackages(true,getLocalFilePnl().getLocalTM().getDirectory());
					}
					else if (table == getOutboxPkgTbl()) {
						downloadPackages(false,getLocalFilePnl().getLocalTM().getDirectory());
					}
					else {
						// Unsupported source.
						System.out.println("DownloadPkgHandler.mouseClicked: unknown event source, ignored.");
					}
				}
				else {
					System.out.println("User doubled clicked table and " + i + " items were selected as a result");
				}
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void dropOnTarget(Object data,Object source) {
		// No target destination supplied? No good.
		if (data == null) {
			System.out.println("DownloadPkgHandler.dropOnTarget: missing destination, drop ignored.");
			return;
		}

		// Determine the data type. We only like String and File.
		File f;
		if (data instanceof String) {
			f = new File((String) data);
		}
		else if (data instanceof File) {
			f = (File) data;
		}
		else {
			// Unknown target destination type supplied.
			System.out.println("DownloadPkgHandler.dropOnTarget: unrecognized data type, drop ignored.");
			return;
		}

		// We want the target directory.
		String directory;
		if (f.isDirectory()) {
			directory = f.getPath();
		}
		else {
			directory = f.getParent();
		}

		if (source == getInboxPkgTbl()) {
			downloadPackages(true,directory);
		}
		else if (source == getOutboxPkgTbl()) {
			downloadPackages(false,directory);
		}
		else {
			// Unsupported source.
			System.out.println("DownloadPkgHandler.dropOnTarget: unknown source, drop ignored.");
			return;
		}
	}
	public void actionPerformed(ActionEvent e) {
		boolean isInbox = e.getSource() == getInboxPkgDownBtn() ||
			(e.getSource() == getPkgDownMI() && getRemoteTP().getSelectedComponent() == getInboxPnl());
		boolean isOutbox = e.getSource() == getOutboxPkgDownBtn() ||
			(e.getSource() == getPkgDownMI() && getRemoteTP().getSelectedComponent() == getOutboxPnl());

		if (isInbox || isOutbox) {
			downloadPackages(isInbox,getLocalFilePnl().getLocalTM().getDirectory());
		}
		else {
			// Unsupported source.
			System.out.println("DownloadPkgHandler.actionPerformed: unknown event source, ignored.");
			return;
		}
	}
	private void downloadPackages(boolean isInbox,String directory) {
		try {
			busyCursor(true);

			int i;

			String DownConfFilesPrompt = "Press Ok to download files to this directory:";
			String DownConfFilesMsg = "Files will be downloaded to the specified directory." +
				" Relative paths will be maintained." +
				" You may change the directory by keying a new path in the field above or" +
				" by pressing the Change Directory button and using the file chooser to select a new directory." +
				" For existing files, an attempt to resume the download will be made." +
				" If resumption is not possible, the file will be overwritten." + 
				" Check the box below to prevent the over-write of an existing file.";
			isFile = false;

			getDownConfLbl().setText(DownConfFilesPrompt);
			getDownInfoTA().setText(DownConfFilesMsg);
			saveFile = null;
			getDownConfTF().setText(directory);

			// Ask user if that is ok?
			btnPressed = null;
			getDownConfirmDlg().setLocationRelativeTo(DropBoxPnl.this);
			getDownConfirmDlg().setVisible(true);
			if (btnPressed != getDownConfOkBtn()) {
				busyCursor(false);
				return;
			}

			noOverWrite = getDownConfCB().isSelected();
			directoryName = getDownConfTF().getText().trim();
			boolean isItar = false;

			if (isInbox) {
				int[] s = getInboxPkgTbl().getSelectedRows();
				pkgs = new PackageInfo[s.length];

				for (i = 0; i < s.length; i++) {
					int u = getInboxPkgSortTM().getUnsortedIndex(s[i]);
					pkgs[i] = getInboxPkgTM().getPackageInfo(u);
					isItar |= pkgs[i].getPackageItar();
				}
			}
			else {
				int[] s = getOutboxPkgTbl().getSelectedRows();
				pkgs = new PackageInfo[s.length];

				for (i = 0; i < s.length; i++) {
					int u = getOutboxPkgSortTM().getUnsortedIndex(s[i]);
					pkgs[i] = getOutboxPkgTM().getPackageInfo(u);
					isItar |= pkgs[i].getPackageItar();
				}
			}

			// Downloading ITAR packages, but not certified?
			if (isItar && ! itarListener.certifySession(ownerContainer)) {
				busyCursor(false);
				return;
			}

			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void run() {
		try {
			for (int i = 0; i < pkgs.length; i++) {
				try {
					Vector files = dboxAccess.queryPackageContents(pkgs[i].getPackageId());

					Enumeration fe = files.elements();
					while (fe.hasMoreElements()) {
						FileInfo f = (FileInfo) fe.nextElement();
						String error = getFileStatusTM().downloadFile(pkgs[i],f,f.getFileName(),directoryName,noOverWrite);
						if (error != null) {
							errorHandler.addMsg(f.getFileName() + ": " + error,"Download File");
						}
					}
				} catch(DboxException e) {
					errorHandler.addMsg(e.getMessage(),"Query Contents Failed");
				} catch(RemoteException e) {
					errorHandler.addMsg(e.getMessage(),"Query Contents Failed");
				}
			}

			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
}
private DownloadPkgHandler downloadPkgHandler = new DownloadPkgHandler();
/**
 * Provides drag source support for the Inbox and Outbox Tables.
 */
private class DragHandler extends TransferHandler {
	// We don't do imports.
	public boolean canImport(JComponent c, DataFlavor[] f) {
		return false;
	}
	// Defines types of drag sources we supply: Copies only.
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}
	// Create the transferable that holds the source data.
	public Transferable createTransferable(JComponent c) {
		if (c == null)
			return null;
		
		try {
			return new DragTransferable(c);
		} catch (ClassNotFoundException e) {
			System.out.println("DropBoxPnl.DragHandler.createTransferable: " + e.getMessage());
		}

		return null;
	}
}
public class DragTransferable implements Transferable {
	// Create a flavor to transfer to other objects in our application only.
	private DataFlavor[] flavors = null;
	private JComponent dragSource;
	public DragTransferable(JComponent c) throws ClassNotFoundException {
		dragSource = c;
		flavors = new DataFlavor[1];
		flavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=oem.edge.ed.odc.dsmp.client.FeedbackFlavor");
	}
	public boolean isDataFlavorSupported(DataFlavor f) {
		return f.equals(flavors[0]);
	}
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}
	public Object getTransferData(DataFlavor f) {
		if (f.equals(flavors[0])) {
			// Build a FeedbackFlavor object for inbox and outbox tables,
			// and give it the corresponding download handler.
			if (dragSource == getInboxPkgTbl() || dragSource == getOutboxPkgTbl())
				return new FeedbackFlavor(dragSource,downloadPkgHandler);
			else if (dragSource == getInboxFileTbl() || dragSource == getOutboxFileTbl())
				return new FeedbackFlavor(dragSource,downloadFileHandler);
		}

		return null;
	}
}
private DragHandler dragHandler = null;
/**
 * Handles editing an existing package's metadata.
 */
private class EditPkgActionListener implements ActionListener, Runnable {
	private PackageInfo pkgInfo = null;
	private PackageInfo pkgInfoEdited = null;
	private Vector pkgAcls = null;
	private Vector pkgAclsEdited = null;
	private Vector groups = new Vector();
	private PoolInfo poolInfo = null;
	private boolean canEditDescription = false;
	public void actionPerformed(ActionEvent e) {
		try {
			busyCursor(true);

			// Prepare for this interation.
			pkgInfoEdited = null;
			pkgAclsEdited = null;

			// Determine which package we'll be editing.
			if (e.getSource() == getSendPkgEditBtn() ||
				(e.getSource() == getPkgEditMI() && getRemoteTP().getSelectedComponent() == getSendPnl())) {
				int s = getSendPkgTbl().getSelectedRow();
				int u = getSendPkgSortTM().getUnsortedIndex(s);
	
				pkgInfo = getSendPkgTM().getPackageInfo(u);
				canEditDescription = true;
			}
			else {
				int s = getOutboxPkgTbl().getSelectedRow();
				int u = getOutboxPkgSortTM().getUnsortedIndex(s);
	
				pkgInfo = getOutboxPkgTM().getPackageInfo(u);
				canEditDescription = false;
			}
			poolInfo = null;

			// ITAR Package? Need to have a certified session.
			if (pkgInfo.getPackageItar() && ! itarListener.certifySession(ownerContainer)) {
				busyCursor(false);
				return;
			}

			// Background the server queries.
			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void editPkg() {
		try {
			// Load the latest buddy list.
			users = buddyMgr.getBuddyList();
	
			// Clear selections and fields.
			getEditFromLB().clearSelection();
			getEditToLB().clearSelection();
			getToUserTF().setText("");
			getPkgNameTF().setEditable(false);
			getPkgDescTA().setEditable(canEditDescription);

			// Populate the from listbox.
			getEditFromLM().clear();
			getEditToLM().clear();
			Enumeration e = users.elements();
			while (e.hasMoreElements()) {
				Buddy buddy = new Buddy((String) e.nextElement(),Buddy.USER);
				addNameToList(buddy,getEditFromLM());
			}
			e = groups.elements();
			while (e.hasMoreElements()) {
				Buddy buddy = new Buddy((String) e.nextElement(),Buddy.GROUP);
				addNameToList(buddy,getEditFromLM());
			}
			e = projects.elements();
			while (e.hasMoreElements()) {
				Buddy buddy = new Buddy((String) e.nextElement(),Buddy.PROJECT);
				addNameToList(buddy,getEditFromLM());
			}
	
			// Load up the current recipients
			e = pkgAcls.elements();
			while (e.hasMoreElements()) {
				Buddy buddy = (Buddy) e.nextElement();
				addNameToList(buddy,getEditToLM());
				getEditFromLM().removeElement(buddy);
			}
	
			// Set up the dialog.
			getPkgNameTF().setText(pkgInfo.getPackageName());
			getPkgDescTA().setText(pkgInfo.getPackageDescription());
			getPkgItarCB().setEnabled(false);
			getPkgItarCB().setSelected(pkgInfo.getPackageItar());
			getSendEmailCB().setSelected(pkgInfo.getPackageSendNotification());
			getReturnReceiptCB().setSelected(pkgInfo.getPackageReturnReceipt());

			// Prepare the storage pool.
			expirationListener.isAdjusting = true;
			getStoragePoolCB().removeAllItems();
			if (poolInfo != null) {
				getStoragePoolCB().addItem(poolInfo.getPoolName());
				getStoragePoolCB().setSelectedIndex(0);
			}
			getStoragePoolCB().setEnabled(false);
			expirationListener.isAdjusting = false;

			// Prepare the expiration date GUI based on the package's pool.
			int maxDays = 14;
			if (poolInfo != null) {
				maxDays = poolInfo.getPoolMaxDays();
			}

			expirationListener.prepareExpirationCC(maxDays,0,pkgInfo.getPackageExpiration());

			// Reset push buttons.
			getUserNewAddBtn().setEnabled(false);
			getUserDelBtn().setEnabled(false);
			getEditAddBtn().setEnabled(false);
			getEditRemBtn().setEnabled(false);
			getGrpQueryBtn().setEnabled(false);
			getExpressCB().setEnabled(false);
			getExpressCB().setSelected(false);
			getPkgAddFilesBtn().setVisible(false);
			getPkgOkBtn().setEnabled(true);
	
			// Check for a cross company condition.
			String myCompany = (String) session.get(DropboxAccess.Company);
			crossCompanyLbl.setVisible(BuddyMgr.isCrossCompanyBuddyList(myCompany,getEditToLM().elements()));

			// Monitor any option changes (protocol version 5 and higher)
			boolean oldSendEmail = getSendEmailCB().isSelected();
			boolean oldReturnReceipt = getReturnReceiptCB().isSelected();
			Date oldExpireDate = expirationListener.getExpirationDate();
	
			// Turn off the busy cursor from the edit button click.
			busyCursor(false);
	
			// Show the edit pkg dialog and wait.
			btnPressed = null;
			getPkgEditDlg().setTitle("Change Package Recipients");
			getPkgEditDlg().setLocationRelativeTo(DropBoxPnl.this);
			getPkgEditDlg().show();
	
			busyCursor(true);
	
			if (btnPressed == getPkgOkBtn()) {
				busyCursor(true);
	
				// Save the ACLs.
				pkgAclsEdited = new Vector();
				for (int i = 0; i < getEditToLM().size(); i++) {
					Buddy buddy = (Buddy) getEditToLM().elementAt(i);
					pkgAclsEdited.addElement(buddy);
				}
	
				// Dup the package info
				pkgInfoEdited = new PackageInfo(pkgInfo);
	
				// Determine the package expiration timestamp.
				if (oldExpireDate.compareTo(expirationListener.getExpirationDate()) != 0) {
					pkgInfoEdited.setPackageExpiration(expirationListener.getExpiration());
				}
	
				// Note the options.
				pkgInfoEdited.setPackageSendNotification(getSendEmailCB().isSelected());
				pkgInfoEdited.setPackageReturnReceipt(getReturnReceiptCB().isSelected());
				
				// Note the description.
				pkgInfoEdited.setPackageDescription(getPkgDescTA().getText());
			}
	
			// Reset some gui elements for next use
			getPkgNameTF().setEditable(true);
			getPkgDescTA().setEditable(true);
			getPkgItarCB().setEnabled(true);
			getExpressCB().setEnabled(true);

			// TODO: Not needed anymore.
			/*
			// Move any entries in the To list back to the From list.
			while (getEditToLM().size() > 0) {
				Buddy buddy = (Buddy) getEditToLM().remove(0);
				addNameToList(buddy,getEditFromLM());
			}
			*/
	
			// Finally, save the buddy list.
			users = buddyMgr.setBuddyList(users);
	
			busyCursor(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void run() {
		try {
			try {
				// Need to query user's accessible groups.
				groups.removeAllElements();
				Map grps = dboxAccess.queryGroups(false,false);
				Iterator names = grps.keySet().iterator();
				while (names.hasNext()) {
					groups.addElement(names.next());
				}
			}
			catch (DboxException e) {
				errorHandler.addMsg(e.getMessage(),"Query Groups Failed");
			}
			catch (RemoteException e) {
				errorHandler.addMsg(e.getMessage(),"Query Groups Failed");
			}

			// Storage Pools
			try {
				// Need to query the package's storage pool.
				pools = null;
				poolInfo = dboxAccess.getStoragePoolInstance(pkgInfo.getPackagePoolId());
			} catch (DboxException e) {
				errorHandler.addMsg(e.getMessage(),"Query Storage Failed");
			} catch (RemoteException e) {
				errorHandler.addMsg(e.getMessage(),"Query Storage Failed");
			}

			// Query the package acls.
			try {
				Vector acls = dboxAccess.queryPackageAcls(pkgInfo.getPackageId(),true);
	
				// Convert the AclInfo objects into Buddy objects.
				Enumeration e = acls.elements();
				pkgAcls = new Vector();
				Buddy buddy;

				while (e.hasMoreElements()) {
					AclInfo a = (AclInfo) e.nextElement();
					if (a.getAclStatus() == DropboxGenerator.STATUS_PROJECT) {
						buddy = new Buddy(a.getAclName(),Buddy.PROJECT);
					}
					else if (a.getAclStatus() == DropboxGenerator.STATUS_GROUP) {
						buddy = new Buddy(a.getAclName(),Buddy.GROUP);
					}
					else {
						buddy = new Buddy(a.getAclName(),Buddy.USER);
					}
					buddy.companyList = a.getAclCompany();
					pkgAcls.addElement(buddy);
				}
			} catch (DboxException e) {
				pkgInfo = null;
				SwingUtilities.invokeLater(busyCursorOff);
				errorHandler.addMsg(e.getMessage(),"Query Package Acls Failed");
				return;
			} catch (RemoteException e) {
				pkgInfo = null;
				SwingUtilities.invokeLater(busyCursorOff);
				errorHandler.addMsg(e.getMessage(),"Query Package Acls Failed");
				return;
			}

			// Invoke createPkg on the GUI event thread.
			SwingUtilities.invokeAndWait(new MethodRunner(this,"editPkg"));

			// User didn't cancel, and is edited the package?
			if (pkgInfoEdited != null) {
				// Process both pkgAcls and pkgAclsEdited to separate into acls removed
				// (held in remAcls) and added (left in pkgAclsEdited). Unchanged acls
				// are discarded.
				int i;
		
				Enumeration acls = pkgAcls.elements();
				Vector remAcls = new Vector();
				while (acls.hasMoreElements()) {
					Buddy buddy = (Buddy) acls.nextElement();
		
					if ((i = pkgAclsEdited.indexOf(buddy)) != -1) {
						pkgAclsEdited.removeElementAt(i);
					}
					else {
						remAcls.addElement(buddy);
					}
				}
		
				// Process the deleted acls.
				acls = remAcls.elements();
				while (acls.hasMoreElements()) {
					Buddy buddy = (Buddy) acls.nextElement();
					try {
						if (buddy.type == Buddy.PROJECT) {
							dboxAccess.removeProjectAcl(pkgInfo.getPackageId(),buddy.name);
						}
						else if (buddy.type == Buddy.GROUP) {
							dboxAccess.removeGroupAcl(pkgInfo.getPackageId(),buddy.name);
						}
						else {
							dboxAccess.removeUserAcl(pkgInfo.getPackageId(),buddy.name);
						}
					}
					catch (DboxException e) {
						errorHandler.addMsg(e.getMessage(),"Remove Recipient Failed");
					}
					catch (RemoteException e) {
						errorHandler.addMsg(e.getMessage(),"Remove Recipient Failed");
					}
				}

				// Process the added acls.
				boolean updatedUserList = false;
				acls = pkgAclsEdited.elements();
				while (acls.hasMoreElements()) {
					Buddy buddy = (Buddy) acls.nextElement();
					try {
						if (buddy.type == Buddy.PROJECT) {
							dboxAccess.addProjectAcl(pkgInfo.getPackageId(),buddy.name);
						}
						else if (buddy.type == Buddy.GROUP) {
							dboxAccess.addGroupAcl(pkgInfo.getPackageId(),buddy.name);
						}
						else {
							dboxAccess.addUserAcl(pkgInfo.getPackageId(),buddy.name);
						}
					}
					catch (DboxException e) {
						// Failed to add an Acl, if a user, remove from buddy list.
						if (e.getMessage() != null && buddy.type == Buddy.USER) {
							users.removeElement(buddy.name);
							updatedUserList = true;
						}

						errorHandler.addMsg(e.getMessage(),"Add Recipient Failed");
					}
					catch (RemoteException e) {
						errorHandler.addMsg(e.getMessage(),"Add Recipient Failed");
					}
				}

				// Failed acl adds caused user deletes? Save buddy list.
				if (updatedUserList) {
					users = buddyMgr.setBuddyList(users);
				}

				// Done with the acls.
				pkgAcls = null;
				pkgAclsEdited = null;
		
				boolean pChanged = false;
		
				// Updated the expiration date?
				if (pkgInfo.getPackageExpiration() != pkgInfoEdited.getPackageExpiration()) {
					try {
						dboxAccess.changePackageExpiration(pkgInfo.getPackageId(),pkgInfoEdited.getPackageExpiration());
					} catch (DboxException e) {
						errorHandler.addMsg(e.getMessage(),"Change Expiration Failed");
					} catch (RemoteException e) {
						errorHandler.addMsg(e.getMessage(),"Change Expiration Failed");
					}
					pChanged = true;
				}

				// Updated the description?
				String oldDescription = pkgInfo.getPackageDescription();
				if (oldDescription == null) oldDescription = "";
				String newDescription = pkgInfoEdited.getPackageDescription();
				if (newDescription == null) newDescription = "";
				if (! oldDescription.equals(newDescription)) {
					try {
						dboxAccess.setPackageDescription(pkgInfo.getPackageId(),pkgInfoEdited.getPackageDescription());
					} catch (DboxException e) {
						errorHandler.addMsg(e.getMessage(),"Change Description Failed");
					} catch (RemoteException e) {
						errorHandler.addMsg(e.getMessage(),"Change Description Failed");
					}
					pChanged = true;
				}

				// Changed any options?
				int pkgMsk = 0;
				int pkgVal = 0;
		
				if (pkgInfo.getPackageSendNotification() != pkgInfoEdited.getPackageSendNotification()) {
					pkgMsk |= PackageInfo.SENDNOTIFY;
					if (pkgInfoEdited.getPackageSendNotification()) {
						pkgVal |= PackageInfo.SENDNOTIFY;
					}
				}
		
				if (pkgInfo.getPackageReturnReceipt() != pkgInfoEdited.getPackageReturnReceipt()) {
					pkgMsk |= PackageInfo.RETURNRECEIPT;
					if (pkgInfoEdited.getPackageReturnReceipt()) {
						pkgVal |= PackageInfo.RETURNRECEIPT;
					}
				}
		
				pkgMsk &= 0xFF;
				pkgVal &= 0xFF;
		
				if (pkgMsk != 0) {
					try {
						dboxAccess.setPackageFlags(pkgInfo.getPackageId(),pkgMsk,pkgVal);
					} catch (DboxException e) {
						errorHandler.addMsg(e.getMessage(),"Set Package Options Failed");
					} catch (RemoteException e) {
						errorHandler.addMsg(e.getMessage(),"Set Package Options Failed");
					}
					pChanged = true;
				}
		
				// Changed any package settings?
				if (pChanged) {
					// Query the package (to update package table info).
					try {
						pkgInfo = dboxAccess.queryPackage(pkgInfo.getPackageId(),true);
		
						SwingUtilities.invokeLater(new MethodRunner(this,"updatePackage"));
					} catch (DboxException e) {
						errorHandler.addMsg(e.getMessage(),"Query Package Failed");
					} catch (RemoteException e) {
						errorHandler.addMsg(e.getMessage(),"Query Package Failed");
					}
				}
				
				SwingUtilities.invokeLater(busyCursorOff);
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void updatePackage() {
		// Update the package information.
		if (getRemoteTP().getSelectedComponent() == getSendPnl()) {
			getSendPkgTM().updatePackage(pkgInfo);
		}
		else {
			getOutboxPkgTM().updatePackage(pkgInfo);
		}
	}
}
private EditPkgActionListener editPkgActionListener = new EditPkgActionListener();
/**
 * Handles exiting application.
 */
private class ExitListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		isExit = true;

		// If the logout menu is enabled, we're logged in, so need to deal with that...
		if (getLogoutMI().isEnabled()) {
			// and we are still showing (for applet support,
			// if the user closes the applet window, we'll be called to shut stuff down, but
			// the owning container will already be closed).
			if (ownerContainer.isVisible()) {
				if (JOptionPane.showConfirmDialog(DropBoxPnl.this,"Logoff Drop Box and exit?","Close Connection",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			}

			getLogoutMI().doClick();
		}
		else {
			exit();
		}
	}
}
private ExitListener exitListener = new ExitListener();
/**
 * Handles package expiration administrivia and action events for
 * the StoragePool ComboBox and the JCalendarChooser.
 */
private class ExpirationListener implements ActionListener, ItemListener {
	private Date expirationDate = null;
	private DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
	public boolean isAdjusting = false;

	public void actionPerformed(ActionEvent e) {
		// Pushed the calendar button?
		if (e.getSource() == getExpirationBtn()) {
			expirationCC.popupRelativeTo(getExpirationBtn());
		}

		// Let the current expiration owner know about the
		// change in expiration.
		else {
			setExpiration((Date) e.getSource());
		}
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public long getExpiration() {
		// Create a calendar.
		GregorianCalendar gc = new GregorianCalendar();
		
		// Normalize the expirate date (day) to midnight.
		gc.setTime(expirationDate);
		gc.set(GregorianCalendar.HOUR_OF_DAY,0);
		gc.set(GregorianCalendar.MINUTE,0);
		gc.set(GregorianCalendar.SECOND,0);
		gc.set(GregorianCalendar.MILLISECOND,0);

		// Get expirate date midnight in millis.
		long expire = gc.getTimeInMillis();
		
		// Get midnight today.
		gc.setTimeInMillis(System.currentTimeMillis());
		gc.set(GregorianCalendar.HOUR_OF_DAY,0);
		gc.set(GregorianCalendar.MINUTE,0);
		gc.set(GregorianCalendar.SECOND,0);
		gc.set(GregorianCalendar.MILLISECOND,0);

		// Get number of millis into future for expiration (future midnight - midnight).
		expire -= gc.getTimeInMillis();
		
		// Expirate is now + millis into future.
		expire += System.currentTimeMillis();

		return expire;
	}
	public void itemStateChanged(java.awt.event.ItemEvent e) {
		if (isAdjusting)
			return;
			
		int i = getStoragePoolCB().getSelectedIndex();
		PoolInfo p = (PoolInfo) pools.elementAt(i);
		prepareExpirationCC(p.getPoolMaxDays(),p.getPoolDefaultDays(),0);
	}
	public void prepareExpirationCC(int maxDays, int defDays, long expiration) {
		// Get the current time.
		Date now = new Date(System.currentTimeMillis());
	
		// Create a calendar.
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(now);
	
		// Earliest expiration will be today.
		expirationCC.setEarliestDate(now);
	
		// Latest expiration will be maxDays from now.
		gc.add(Calendar.DAY_OF_MONTH,maxDays);
		expirationCC.setLatestDate(gc.getTime());
	
		// Finally, the default expiration will be either the actual expiration
		// (if not 0) or defDays from now.
		if (expiration > 0) {
			setExpiration(expiration);
		}
		else {
			gc.setTime(now);
			gc.add(Calendar.DAY_OF_MONTH,defDays);
			setExpiration(gc.getTime());
		}

		expirationCC.setDate(expirationDate);
	}
	public void setExpiration(long expiration) {
		setExpiration(new Date(expiration));
	}
	public void setExpiration(Date expiration) {
		expirationDate = expiration;
		getExpirationLbl().setText(df.format(expiration));
	}
}
private ExpirationListener expirationListener = new ExpirationListener();
/**
 * Handles query of file access information.
 */
private class FileAclListener implements ActionListener, Runnable {
	private PackageInfo pkg = null;
	private FileInfo file = null;
	private Vector acls = null;
	public void actionPerformed(ActionEvent e) {
		try {
			busyCursor(true);
	
			int u = getOutboxPkgSortTM().getUnsortedIndex(getOutboxPkgTbl().getSelectedRow());
			pkg = getOutboxPkgTM().getPackageInfo(u);
			u = getOutboxFileSortTM().getUnsortedIndex(getOutboxFileTbl().getSelectedRow());
			file = getOutboxFileTM().getFileInfo(u);
			
			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}    
	}
	public void run() {
		try {
			try {
				acls = dboxAccess.queryPackageFileAcls(pkg.getPackageId(),file.getFileId());
	
				SwingUtilities.invokeLater(new MethodRunner(this,"displayResults"));
			} catch(DboxException e) {
				errorHandler.addMsg(e.getMessage(),"Query Acls Failed");
			} catch(RemoteException e) {
				errorHandler.addMsg(e.getMessage(),"Query Acls Failed");
			}
	
			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}    
	}
	public void displayResults() {
		// Prep the dialog.
		getFileAclViewNameLbl().setText(file.getFileName());
		getFileAclViewTbl().clearSelection();
		getFileAclViewTM().addAcls(acls);

		// Display the dialog.
		getFileAclViewDlg().setLocationRelativeTo(DropBoxPnl.this);
		getFileAclViewDlg().show();
	}
}
private FileAclListener fileAclListener = new FileAclListener();
/**
 * Handles forwarding of files to other packages.
 */
private class ForwardFilesActionListener extends CreatePkgActionListener {
	private boolean createPackage = false;
	public void actionPerformed(ActionEvent e) {
		try {
			busyCursor(true);
	
			// Remember which pane the forward came from and show the Send New Packages pane.
			Component tpSel = getRemoteTP().getSelectedComponent();
			getRemoteTP().setSelectedComponent(getSendPnl());
	
			// Note the items to be forwarded...
			Vector items = new Vector();
			if (e.getSource() == getInboxTransferBtn() ||
				(e.getSource() == getFilesFwdMI() && tpSel == getInboxPnl())) {
				int[] s = getInboxFileTbl().getSelectedRows();
				for (int i = 0; i < s.length; i++) {
					int u = getInboxFileSortTM().getUnsortedIndex(s[i]);
					Long item = new Long(getInboxFileTM().getFileID(u));
					items.addElement(item);
				}
			}
			else {
				int[] s = getOutboxFileTbl().getSelectedRows();
				for (int i = 0; i < s.length; i++) {
					int u = getOutboxFileSortTM().getUnsortedIndex(s[i]);
					Long item = new Long(getOutboxFileTM().getFileID(u));
					items.addElement(item);
				}
			}
	
			// No packages defined?
			if (getSendPkgTM().getRowCount() == 0) {
				// See if we can create one.
				if (JOptionPane.showConfirmDialog(DropBoxPnl.this,"Create a new package for selected files?","No New Package Defined!",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
					// Cancel forward, flip back to original pane.
					getRemoteTP().setSelectedComponent(tpSel);
					busyCursor(false);
					return;
				}
	
				// Define a new package.
				pkgInfo = null;
			}
	
			// Query for an existing package.
			else {
				// Set up confirm dialog.
				int i = getSendPkgTbl().getSelectedRowCount();
				if (i == 1) {
					int j = getSendPkgTbl().getSelectedRow();
					getConfirmPkgTbl().getSelectionModel().setSelectionInterval(j,j);
					getConfirmOkBtn().setEnabled(true);
				}
				else {
					getConfirmPkgTbl().getSelectionModel().clearSelection();
					getConfirmOkBtn().setEnabled(false);
				}
				btnPressed = null;
				getConfirmDlg().setLocationRelativeTo(DropBoxPnl.this);
				getConfirmDlg().setVisible(true);
	
				// Confirmed an existing package.
				if (btnPressed == getConfirmOkBtn()) {
					int row = getConfirmPkgTbl().getSelectedRow();
					int pkg = getSendPkgSortTM().getUnsortedIndex(row);
					pkgInfo = getSendPkgTM().getPackageInfo(pkg);
				}
				// Define a new package.
				else if (btnPressed == getConfirmNewBtn()) {
					pkgInfo = null;
				}
				// Cancelled.
				else {
					// Flip back to original pane.
					getRemoteTP().setSelectedComponent(tpSel);
					busyCursor(false);
					return;
				}
			}

			// We have items, but no acls.
			pkgDropItems = null;
			pkgItems = items;
			pkgAcls = null;

			WorkerThread t = new WorkerThread(this);
			createPackage = pkgInfo == null;
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void deployResults() {
		// Update the package information.
		getSendPkgTM().updatePackage(pkgInfo);

		// Select the package row to update the package content.
		int row = getSendPkgTM().getPackageRow(pkgInfo.getPackageId());
		row = getSendPkgSortTM().getSortedIndex(row);
		getSendPkgTbl().getSelectionModel().clearSelection();
		getSendPkgTbl().getSelectionModel().setSelectionInterval(row,row);
	}
	public void run() {
		try {
			// Create a new package? Use the inherited run() from CreatePkgActionListener
			if (createPackage) {
				super.run();
			}
			// Forward into an existing package.
			else {
				// Forward the files.
				Enumeration items = pkgItems.elements();
				while (items.hasMoreElements()) {
					try {
						Long item = (Long) items.nextElement();
						dboxAccess.addItemToPackage(pkgInfo.getPackageId(),item.longValue());
					} catch (DboxException e) {
						errorHandler.addMsg(e.getMessage(),"Add File Failed");
					} catch (RemoteException e) {
						errorHandler.addMsg(e.getMessage(),"Add File Failed");
					}
				}
	
				// Query the package.
				try {
					pkgInfo = dboxAccess.queryPackage(pkgInfo.getPackageId(),true);
	
					// Update the table model and select the package to show its new content.
					SwingUtilities.invokeLater(new MethodRunner(this,"deployResults"));
				} catch (DboxException e) {
					errorHandler.addMsg(e.getMessage(),"Query Package Failed");
				} catch (RemoteException e) {
					errorHandler.addMsg(e.getMessage(),"Query Package Failed");
				}
	
				SwingUtilities.invokeLater(busyCursorOff);
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	protected void setNameAndDescription() {
		super.setNameAndDescription();
		getPkgEditDlg().setTitle("Forward Files as a New Package");
	}
}
private ForwardFilesActionListener forwardFilesActionListener = new ForwardFilesActionListener();
/**
 * Handles forwarding of package to new recipients.
 */
private class ForwardPkgActionListener extends CreatePkgActionListener {
	private PackageInfo inboxPkg = null;
	public void actionPerformed(ActionEvent e) {
		try {
			busyCursor(true);

			// No package, or acls.
			pkgInfo = null;
			pkgAcls = null;

			// Set inboxPkg to pre-define create dialog fields.
			int i = getInboxPkgTbl().getSelectedRow();
			int u = getInboxPkgSortTM().getUnsortedIndex(i);
			inboxPkg = getInboxPkgTM().getPackageInfo(u);

			// Flip tabbed pane to Send New Packges panel.
			getRemoteTP().setSelectedComponent(getSendPnl());

			// Note all the files to be forwsrded with the new package.
			pkgItems = new Vector();
			for (i = 0; i < getInboxFileTM().getRowCount(); i++) {
				Long fileID = new Long(getInboxFileTM().getFileID(i));
				pkgItems.addElement(fileID);
			}

			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	protected void setNameAndDescription() {
		super.setNameAndDescription();
		getPkgEditDlg().setTitle("Forward a Package");
		getPkgNameTF().setText(inboxPkg.getPackageName());
		getPkgDescTA().setText(inboxPkg.getPackageDescription());
	}
}
private ForwardPkgActionListener forwardPkgActionListener = new ForwardPkgActionListener();
/**
 * Provides drop target support for the Send Tables.
 */
private class SendDropHandler extends TransferHandler {
	private DataFlavor fileListFlavor = null;
	private DataFlavor flavors[] = null;
	private ArrayList flavorList = new ArrayList();

	// Prepare this handler to accept dsmp FileListFlavor and native file lists.
	// FileListFlavor is an internal class to allow the various GUI elements to
	// exchange file lists, but prevents them from interacting with other apps.
	public SendDropHandler() {
		try {
			fileListFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=oem.edge.ed.odc.dsmp.client.FileListFlavor");
			flavorList.add(fileListFlavor);
		} catch (ClassNotFoundException e) {
			System.out.println("DropBoxPnl.SendDropHandler: " + e.getMessage());
		}
		flavorList.add(DataFlavor.javaFileListFlavor);

		flavors = new DataFlavor[flavorList.size()];
		for (int i = 0; i < flavors.length; i++) {
			flavors[i] = (DataFlavor) flavorList.get(i);
		}
	}
	// Informs drag sources of the data formats we can accept.
	public boolean canImport(JComponent c, DataFlavor[] f) {
		// If we aren't dropping onto the package table itself, then there must be
		// only 1 or no package selected. For more than one selected, we wouldn't know
		// the destination of the drop.
		if (c != getInboxPkgTbl() && getInboxPkgTbl().getSelectedRowCount() > 1) {
			return false;
		}

		// We only accept file lists.
		for (int i = 0; i < f.length; i++) {
			if (flavorList.contains(f[i])) {
				return true;
			}
		}
		return false;
	}
	// Defines types of drag sources we supply: None.
	public int getSourceActions(JComponent c) {
		return TransferHandler.NONE;
	}
	// Imports the dropped data.
	public boolean importData(JComponent c, Transferable t) {
		try {
			List l;

			// We get a list of files.
			if (fileListFlavor != null && t.isDataFlavorSupported(fileListFlavor)) {
				FileListFlavor flf = (FileListFlavor) t.getTransferData(fileListFlavor);
				l = flf.files;
			}
			else {
				l = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
			}

			// Where did we drop? (Package or new package)
			// PkgTable? Add to package.
			// PkgTableSP? Create new package.
			// FileTable? Add to package.
			// FileTableSP? Selected Package? Add to package or new package.
			PackageInfo pkgInfo = null;
			// Dropped on a package row, or the file table. Either implies a single package.
			if (c == getSendPkgTbl() || c == getSendFileTbl()) {
				// Should never happen, but...
				if (getSendPkgTbl().getSelectedRowCount() != 1) {
					System.out.println("SendDropHandler: Dropped onto table, but not 1 package selected!");
					return false;
				}

				int r = getSendPkgTbl().getSelectedRow();
				pkgInfo = getSendPkgTM().getPackageInfo(getSendPkgSortTM().getUnsortedIndex(r));
			}
			// Dropped on file scroll pane and only 1 package selected?
			else if (c == getSendFileSP() && getSendPkgTbl().getSelectedRowCount() == 1) {
				int r = getSendPkgTbl().getSelectedRow();
				pkgInfo = getSendPkgTM().getPackageInfo(getSendPkgSortTM().getUnsortedIndex(r));
			}
			// All others imply new package.

			// Let the upload handler know this drop occurred.
			uploadListener.dropFileList(l,pkgInfo);

			return true;
		}
		catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassCastException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
private SendDropHandler sendDropHandler = null;
/**
 * Handles Inbox File table list selections
 */
private class InboxFileListSelectionListener implements ListSelectionListener {
	public void valueChanged(ListSelectionEvent e) {
		try {
			if (e.getValueIsAdjusting())
				return;

			boolean enable = getInboxFileTbl().getSelectedRowCount() > 0;

			getInboxFileDownBtn().setEnabled(enable);
			getInboxTransferBtn().setEnabled(enable);
			getFilesDownMI().setEnabled(enable);
			getFilesFwdMI().setEnabled(enable);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
}
private InboxFileListSelectionListener inboxFileListSelectionListener = new InboxFileListSelectionListener();
/**
 * Handles Inbox package table list selections
 */
private class InboxPkgListSelectionListener implements ListSelectionListener, Runnable {
	private PackageInfo pkg = null;
	private Vector files = null;
	public void valueChanged(ListSelectionEvent e) {
		try {
			if (e.getValueIsAdjusting())
				return;

			emptyInboxFile();

			int i = getInboxPkgTbl().getSelectedRowCount();

			getInboxPkgDownBtn().setEnabled(i > 0);
			getInboxPkgViewBtn().setEnabled(i == 1);
			getInboxAddSenderBtn().setEnabled(i > 0);
			getInboxPkgTransferBtn().setEnabled(i == 1);
			getPkgDownMI().setEnabled(i > 0);
			getPkgViewDescMI().setEnabled(i == 1);
			getPkgAddSenderMI().setEnabled(i > 0);
			getPkgFwdMI().setEnabled(i ==1);

			if (i == 1) {
				busyCursor(true);
	
				int u = getInboxPkgSortTM().getUnsortedIndex(getInboxPkgTbl().getSelectedRow());
				pkg = getInboxPkgTM().getPackageInfo(u);
	
				WorkerThread t = new WorkerThread(this);
				t.start();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void addFilesToInboxTbl() {
		getInboxFileTM().addFiles(files);
	}
	public void run() {
		try {
			try {
				files = dboxAccess.queryPackageContents(pkg.getPackageId());
				SwingUtilities.invokeLater(new MethodRunner(this,"addFilesToInboxTbl"));
			} catch(DboxException e) {
				errorHandler.addMsg(e.getMessage(),"Query Contents Failed");
			} catch(RemoteException e) {
				errorHandler.addMsg(e.getMessage(),"Query Contents Failed");
			}
			
			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
}
private InboxPkgListSelectionListener inboxPkgListSelectionListener = new InboxPkgListSelectionListener();
/**
 * Handles ITAR certification for session.
 */
private class ItarListener implements ActionListener, ItemListener, Runnable {
	private String ITARMSG =
		"In order to utilize ITAR data, you must certify your session. By certifying " +
		"your session, you confirm that you are currently located on United States " +
		"property, and therefore, are able to meet the security requirements imposed on the " +
		"transmission and receipt of ITAR data.";
	private boolean sessionCertified = false;
	private boolean sessionCertifyNewState = false;
	private JCheckBox checkBox = null;
	private JPanel itarPnl = null;
	private JLabel itarMsg = null;
	public void actionPerformed(ActionEvent e) {
		certify(ownerContainer);
	}
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == getPkgItarCB()) {
			if (getPkgItarCB().isSelected()) {
				checkBox = getPkgItarCB();
				if (! certifySession(checkBox)) {
					checkBox = null;
					getPkgItarCB().setSelected(false);
				}
			}
		}
	}
	public boolean certifySession(Component parent) {
		return sessionCertified || certify(parent);
	}
	private boolean certify(Component parent) {
		if (sessionCertified) {
			getMsgLbl().setText("This session is certified!");
		}
		else {
			getMsgLbl().setText("This session is NOT certified!");
		}

		String[] options = { "Certify", "Decertify", "Cancel" };
		JOptionPane p = new JOptionPane(getItarPnl(),JOptionPane.QUESTION_MESSAGE,0,null,options,options[0]);
		JDialog d = p.createDialog(parent,"ITAR Session Certification");

		d.setSize(300,320);
		d.setResizable(true);
		d.setLocationRelativeTo(parent);
		d.show();

		Object o = p.getValue();

		if (o == null || o.equals(options[2])) {
			return sessionCertified;
		}

		sessionCertifyNewState = o.equals(options[0]);

		// Only need to involve the server if we are changing states.
		if (sessionCertifyNewState != sessionCertified) {
			WorkerThread t = new WorkerThread(this);
			t.start();
		}

		return sessionCertifyNewState;
	}
	public void resetCheckBox() {
		if (checkBox != null) {
			checkBox.setSelected(sessionCertified);
			checkBox = null;
		}
	}
	public void displayItarLbl() {
		itarLbl.setVisible(sessionCertified);
	}
	public void sessionEnded() {
		sessionCertified = false;
		displayItarLbl();
	}
	public void run() {
		try {
			dboxAccess.setOption(DropboxAccess.ItarSessionCertified,
								sessionCertifyNewState ? "TRUE" : "FALSE");
			sessionCertified = sessionCertifyNewState;
			checkBox = null;
			SwingUtilities.invokeLater(new MethodRunner(this,"displayItarLbl"));
		} catch(DboxException e) {
			SwingUtilities.invokeLater(new MethodRunner(this,"resetCheckBox"));
			errorHandler.addMsg(e.getMessage(),"Set Option Failed");
		} catch(RemoteException e) {
			SwingUtilities.invokeLater(new MethodRunner(this,"resetCheckBox"));
			errorHandler.addMsg(e.getMessage(),"Set Option Failed");
		}
	}
	private JPanel getItarPnl() {
		if (itarPnl == null) {
			itarPnl = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0; gbc.gridy = 0;
			gbc.insets = new Insets(5,5,5,5);
			itarPnl.add(getMsgLbl(),gbc);
			gbc.gridy = 1;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			JTextArea ta = new JTextArea(ITARMSG);
			ta.setEditable(false);
			ta.setWrapStyleWord(true);
			ta.setLineWrap(true);
			JScrollPane sp = new JScrollPane(ta);
			itarPnl.add(sp,gbc);
		}
		return itarPnl;
	}
	private JLabel getMsgLbl() {
		if (itarMsg == null) {
			itarMsg = new JLabel();
		}
		return itarMsg;
	}
}
private ItarListener itarListener= new ItarListener();
/**
 * Handles loging into the server.
 */
private class LoginListener implements ActionListener, Runnable {
	private String user = null;
	private String pw = null;
	private String token = null;
	public void actionPerformed(ActionEvent e) {
		try {
			if (token == null) {
				btnPressed = null;
				getLoginOkBtn().setEnabled(false);
				getPwdTF().setText("");
				getLoginDlg().setLocationRelativeTo(DropBoxPnl.this);
				getLoginDlg().setVisible(true);
	
				if (btnPressed != getLoginOkBtn()) {
					return;
				}
			}

			busyCursor(true);

			fireTitleChange("Drop Box - Connecting...");
			getLoginDlg().setVisible(false);
			getLoginMI().setEnabled(false);
			getLoginTB().setEnabled(false);

			if (token == null) {
				user = getUserTF().getText();
				pw = new String(getPwdTF().getPassword());
			}

			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void run() {
		try {
			// Establish a dropbox session with the service provider.
			try {
				if (token == null) {
					session = dboxAccess.createSession(user,pw);
					user = null;
					pw = null;
				}
				else {
					session = dboxAccess.createSession(token);
					token = null;
				}
				String sessionId = (String) session.get(DropboxAccess.SessionID);
				dboxFactory.setSessionId(dboxAccess,session);
				sessionHandler = new SessionHandler();
				sessionHandler.start();
				SwingUtilities.invokeLater(new MethodRunner(DropBoxPnl.this,"resetToLoggedIn"));
			} catch (DboxException e) {
				// Ditch any token we tried to use.
				token = null;
				user = null;
				pw = null;
				errorHandler.addMsg(e.getMessage(),"Login Failed");
				SwingUtilities.invokeLater(new MethodRunner(DropBoxPnl.this,"resetToLoggedOut"));
				SwingUtilities.invokeLater(busyCursorOff);
				return;
			} catch (RemoteException e) {
				errorHandler.addMsg(e.getMessage(),"Login Failed");
				SwingUtilities.invokeLater(new MethodRunner(DropBoxPnl.this,"resetToLoggedOut"));
				SwingUtilities.invokeLater(busyCursorOff);
				return;
			}

			// Set the window title on the GUI thread.
			SwingUtilities.invokeLater(new MethodRunner(this,"setWindowTitle"));

			// Have the source mgr validate the locally managed files.
			sourceMgr.verifyAllFiles();

			// Get our project information.
			try {
				projects = dboxAccess.getProjectList();
			} catch(DboxException e) {
				errorHandler.addMsg(e.getMessage(),"Get Project List Failed");
			} catch(RemoteException e) {
				errorHandler.addMsg(e.getMessage(),"Get Project List Failed");
			}

			// Query the options so we can properly query for packages
			boolean filterCompleted = false;
			boolean filterMarked = false;
			boolean itarCertified = false;

			try {
				Map options = dboxAccess.getOptions();

				if (options != null) {
					String option = (String) options.get(DropboxGenerator.FilterComplete);
					filterCompleted = option != null && option.equalsIgnoreCase("TRUE");
					option = (String) options.get(DropboxGenerator.FilterMarked);
					filterMarked = option != null && option.equalsIgnoreCase("TRUE");
					option = (String) options.get(DropboxGenerator.ItarCertified);
					itarCertified = option != null && option.equalsIgnoreCase("TRUE");
				}
			} catch(DboxException e) {
				errorHandler.addMsg(e.getMessage(),"Get Options Failed");
			} catch(RemoteException e) {
				errorHandler.addMsg(e.getMessage(),"Get Options Failed");
			}

			getDropboxOptions().filterCompleted = filterCompleted;
			getDropboxOptions().filterMarked = filterMarked;
			getPkgItarCB().setEnabled(itarCertified);
			getItarMI().setEnabled(itarCertified);

			// Populate the inbox, outbox and drafts.
			inboxRefreshListener.refreshAll();

			// Set up for timed refresh intervals. RefreshTimerInterval is in minutes.
			if (getDropboxOptions().refreshOnTimer) {
				inboxRefreshListener.setInterval(getDropboxOptions().refreshTimerInterval);
				inboxRefreshListener.startTimer();
			}

			// Turn off the busy cursor.
			SwingUtilities.invokeLater(busyCursorOff);

			// Server wants to know about us.
			// We don't care about a response, so no busyCursor call.
			try {
				HashMap h = new HashMap();
				h.put(DropboxGenerator.OS,
					System.getProperty("os.name") + " " +
					System.getProperty("os.arch") + " " +
					System.getProperty("os.version"));
				h.put(DropboxGenerator.ClientType, "GUI");
				dboxAccess.setOptions(h);
			} catch(DboxException e) {
			} catch(RemoteException e) {
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void setToken(String newToken) {
		token = newToken;
	}
	public void setWindowTitle() {
		String company = (String) session.get(DropboxAccess.Company);
		user = (String) session.get(DropboxAccess.User);

		if (company != null && company.length() > 0)
			fireTitleChange("Drop Box - Connected as " + user + " from " + company);
		else
			fireTitleChange("Drop Box - Connected as " + user);
	}
}
private LoginListener loginListener = new LoginListener();
/**
 * Handles logging out of the server.
 */
private class LogoutListener implements ActionListener, Runnable {
	public void actionPerformed(ActionEvent e) {
		try {
			busyCursor(true);
			getLogoutMI().setEnabled(false);
			getLogoutTB().setEnabled(false);

			savePreferences();

			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}    
	}
	public void run() {
		try {
			try {
				// Disable any timed interval refreshes.
				inboxRefreshListener.stopTimer();
				
				dboxAccess.closeSession();

				synchronized(sessionHandler) {
					sessionHandler.stop = true;
					sessionHandler.notify();
				}

				if (isExit) {
					exit();
				}

				SwingUtilities.invokeLater(new MethodRunner(DropBoxPnl.this,"resetToLoggedOut"));
			} catch(DboxException e){
				errorHandler.addMsg(e.getMessage(),"Logout Failed");
			} catch(RemoteException e){
				errorHandler.addMsg(e.getMessage(),"Logout Failed");
			}

			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}    
	}
}
private LogoutListener logoutListener = new LogoutListener();
/**
 * Handles list selections on the Outbox file table.
 */
private class OutboxFileListSelectionListener implements ListSelectionListener {
	public void valueChanged(ListSelectionEvent e) {
		try {
			if (e.getValueIsAdjusting())
				return;
	
			int i = getOutboxFileTbl().getSelectedRowCount();
	
			getOutboxFileDownBtn().setEnabled(i > 0);
			getOutboxTransferBtn().setEnabled(i > 0);
			getOutboxFileViewBtn().setEnabled(i == 1);
			getFilesDownMI().setEnabled(i > 0);
			getFilesFwdMI().setEnabled(i > 0);
			getFilesViewMI().setEnabled(i == 1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
}
private OutboxFileListSelectionListener outboxFileListSelectionListener = new OutboxFileListSelectionListener();
/**
 * Handles list selections on the Outbox package table.
 */
private class OutboxPkgListSelectionListener implements ListSelectionListener, Runnable {
	private PackageInfo pkg = null;
	private Vector files = null;
	public void valueChanged(ListSelectionEvent e) {
		try {
			if (e.getValueIsAdjusting())
				return;
	
			emptyOutboxFile();
	
			int i = getOutboxPkgTbl().getSelectedRowCount();
	
			getOutboxPkgEditBtn().setEnabled(i == 1);
			getOutboxPkgViewBtn().setEnabled(i == 1);
			getOutboxPkgDownBtn().setEnabled(i > 0);
			getOutboxDeleteBtn().setEnabled(i > 0);
			getPkgDeleteMI().setEnabled(i > 0);
			getPkgDownMI().setEnabled(i > 0);
			getPkgEditMI().setEnabled(i == 1);
			getPkgViewMI().setEnabled(i == 1);
	
			if (i == 1) {
				busyCursor(true);
	
				int u = getOutboxPkgSortTM().getUnsortedIndex(getOutboxPkgTbl().getSelectedRow());
				pkg = getOutboxPkgTM().getPackageInfo(u);
	
				WorkerThread t = new WorkerThread(this);
				t.start();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void addFilesToOutboxTbl() {
		getOutboxFileTM().addFiles(files);
	}
	public void run() {
		try {
			try {
				files = dboxAccess.queryPackageContents(pkg.getPackageId());
				SwingUtilities.invokeLater(new MethodRunner(this,"addFilesToOutboxTbl"));
			} catch(DboxException e) {
				errorHandler.addMsg(e.getMessage(),"Query Contents Failed");
			} catch(RemoteException e) {
				errorHandler.addMsg(e.getMessage(),"Query Contents Failed");
			}
			
			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
}
private OutboxPkgListSelectionListener outboxPkgListSelectionListener = new OutboxPkgListSelectionListener();
/**
 * Handles query of package access information.
 */
private class PkgAclsListener implements ActionListener, Runnable {
	private PackageInfo pkg = null;
	private Vector acls = null;
	public void actionPerformed(ActionEvent e) {
		try {
			busyCursor(true);

			int i = getOutboxPkgTbl().getSelectedRow();
			int u = getOutboxPkgSortTM().getUnsortedIndex(i);
			pkg = getOutboxPkgTM().getPackageInfo(u);
			
			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void run() {
		try {
			try {
				Vector allAcls = dboxAccess.queryPackageAcls(pkg.getPackageId(),false);

				acls = new Vector();
				Enumeration eAllAcls = allAcls.elements();
				while (eAllAcls.hasMoreElements()) {
					AclInfo aclInfo = (AclInfo) eAllAcls.nextElement();
					if (aclInfo.getAclStatus() != DropboxGenerator.STATUS_GROUP && aclInfo.getAclStatus() != DropboxGenerator.STATUS_PROJECT) {
						acls.addElement(aclInfo);
					}
				}

				SwingUtilities.invokeLater(new MethodRunner(this,"showResults"));
			} catch (DboxException e) {
				errorHandler.addMsg(e.getMessage(),"Query Package Acls Failed");
			} catch (RemoteException e) {
				errorHandler.addMsg(e.getMessage(),"Query Package Acls Failed");
			}

			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void showResults() {
		getPkgAclViewNameLbl().setText(pkg.getPackageName());
		getPkgAclViewTbl().clearSelection();
		getPkgAclViewTM().addAcls(acls);
		getPkgAclViewDlg().setLocationRelativeTo(DropBoxPnl.this);
		getPkgAclViewDlg().show();
	}
}
private PkgAclsListener pkgAclsListener = new PkgAclsListener();
/**
 * Handles query of group membership.
 */
private class QueryGrpListener implements ActionListener, Runnable {
	private GroupInfo group = null;
	public void actionPerformed(ActionEvent e) {
		Buddy buddy = (Buddy) getEditFromLB().getSelectedValue();

		if (buddy == null) {
			buddy = (Buddy) getEditToLB().getSelectedValue();
		}

		if (buddy != null && buddy.type == Buddy.GROUP) {
			// Set the group to be queried.
			group = new GroupInfo();
			group.setGroupName(buddy.name);

			// Show the dialog to indicate we're working on it...
			getGrpQueryNameLbl().setText("");
			getGrpQueryOwnerLbl().setText("");
			getGrpQueryCompanyLbl().setText("");
			getGrpQueryLM().clear();
			getGrpQueryDlg().setTitle("Querying group, please wait...");
			getGrpQueryDlg().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			getGrpQueryDlg().getGlassPane().setVisible(true);
			getGrpQueryDlg().setLocationRelativeTo(getPkgEditDlg());
			getGrpQueryDlg().setVisible(true);

			WorkerThread t = new WorkerThread(this);
			t.start();
		}
	}
	public void displayResults() {
		if (group != null) {
			getGrpQueryNameLbl().setText(group.getGroupName());
			getGrpQueryOwnerLbl().setText(group.getGroupOwner());
			getGrpQueryCompanyLbl().setText(group.getGroupCompany());

			if (group.getGroupMembersValid()) {
				Enumeration members = group.getGroupMembers().elements();
				while (members.hasMoreElements()) {
					BuddyMgr.addNameToList((String) members.nextElement(),getGrpQueryLM());
				}
			}
		}
		else {
			getGrpQueryNameLbl().setText("No information available");
			getGrpQueryOwnerLbl().setText("No information available");
			getGrpQueryCompanyLbl().setText("No information available");
		}

		getGrpQueryDlg().setTitle("Group Information");
		getGrpQueryDlg().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		getGrpQueryDlg().getGlassPane().setVisible(false);
	}
	public void run() {
		try {
			Map groups = dboxAccess.queryGroups(group.getGroupName(),false,true,false);
			group = (GroupInfo) groups.get(group.getGroupName());
		} catch(DboxException e) {
			group = null;
		} catch(RemoteException e) {
			group = null;
		}
		SwingUtilities.invokeLater(new MethodRunner(this,"displayResults"));
	}
}
private QueryGrpListener queryGrpListener = new QueryGrpListener();
/**
 * Query package handler.
 */
private class QueryPkgHandler implements Runnable {
	public boolean isUpload = false;
	public long pkgId;
	private PackageInfo pkg = null;
	public void displayResults() {
		if (isUpload) {
			// File finished uploading...
			getSendPkgTM().updatePackage(pkg);
			sendPkgSelectAdjust();
		}
		else {
			// File finished downloading.
			getOutboxPkgTM().updatePackage(pkg);
			getInboxPkgTM().updatePackage(pkg);
		}
	}
	public void run() {
		SwingUtilities.invokeLater(busyCursorOn);

		try {
			pkg = dboxAccess.queryPackage(pkgId,true);
			SwingUtilities.invokeLater(new MethodRunner(this,"displayResults"));
		} catch(DboxException e) {
		} catch(RemoteException e) {
		}
		
		SwingUtilities.invokeLater(busyCursorOff);
	}
}
/**
 * Handles refreshing of the remote tables (inbox, outbox, draft).
 */
private class RefreshHandler implements Runnable {
	public boolean inbox = false;
	public boolean all = false;
	private Vector inboxPkgs = null;
	private Vector pkgs = null;
	protected WorkerThread refreshThread = null;

	public void deployResults() {
		// Refreshing inbox or all?
		if (inbox || all) {
			// In case the filtering changed.
			getFilterLbl().setVisible(getDropboxOptions().filterCompleted || getDropboxOptions().filterMarked);
			getInboxPnl().doLayout();

			// Empty the inbox and re-populate it.
			emptyInboxPkg();
			getInboxPkgTM().addPackages(inboxPkgs);
		}

		// Refreshing sent or all?
		if (! inbox || all) {
			// Empty both outbox and drafts.
			emptyOutboxPkg();
			emptySendPkg();
		
			// Packages we own. Split them into 2 set (commit vs non-commit)
			for (int i = 0; i < pkgs.size(); i++) {
				PackageInfo p = (PackageInfo) pkgs.elementAt(i);
				if (p.getPackageStatus() == DropboxGenerator.STATUS_COMPLETE) {
					getOutboxPkgTM().addPackage(p);
				}
				else {
					getSendPkgTM().addPackage(p);
				}
			}
		}

		refreshThread = null;
	}
	public void run() {
		try {
			try {
				// Refreshing inbox only or all?
				if (inbox || all) {
					// Request a new list of inbox packages.
					inboxPkgs = dboxAccess.queryPackages(false,
									getDropboxOptions().filterCompleted,
									getDropboxOptions().filterMarked,
									true);
				}

				// Refreshing sent only or all?
				if (! inbox || all) {
					// Request a new list of sent packages.
					pkgs = dboxAccess.queryPackages(true,false,false,true);
				}

				SwingUtilities.invokeLater(new MethodRunner(this,"deployResults"));
			} catch (DboxException e) {
				refreshThread = null;
				errorHandler.addMsg(e.getMessage(),"Query Packages Failed");
			} catch (RemoteException e) {
				refreshThread = null;
				errorHandler.addMsg(e.getMessage(),"Query Packages Failed");
			}

			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			refreshThread = null;
			handleException(ivjExc);
		}    
	}
}
private class RefreshListener extends RefreshHandler implements ActionListener, ChangeListener, OptionListener {
	// Interval Timer handles timed refresh events.
	private Timer intervalTimer = null;
	public RefreshListener() {
		// Create the interval timer, but don't start it.
		intervalTimer = new Timer(30*60*1000,this);
	}
	public void actionPerformed(ActionEvent e) {
		try {
			// Already have an refresh running?
			if (refreshThread != null)
				return;

			busyCursor(true);

			all = false;
			if (e.getSource() == intervalTimer) {
				inbox = true;
			}
			else if (e.getSource() == getInboxRefreshBtn() ||
				(e.getSource() == getRefreshMI() && getRemoteTP().getSelectedComponent() == getInboxPnl())) {
				inbox = true;
				
				// We are about to refresh the inbox, the timed interval can
				// be reset.
				if (intervalTimer.isRunning()) {
					intervalTimer.restart();
				}
			}
			else {
				inbox = false;
			}

			refreshThread = new WorkerThread(this);
			refreshThread.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}    
	}
	public void stateChanged(ChangeEvent e) {
		if (getRemoteTP().getSelectedComponent() == getInboxPnl()) {
			// Refresh the inbox, unless a refresh is already in progress or not logged in.
			if (getDropboxOptions().refreshOnActivate &&
				getLogoutMI().isEnabled() &&
				refreshThread == null) {
				busyCursor(true);

				// We are about to refresh the inbox, the timed interval can
				// be reset.
				if (intervalTimer.isRunning()) {
					intervalTimer.restart();
				}

				inbox = true;
				all = false;
				refreshThread = new WorkerThread(this);
				refreshThread.start();
			}
		}
	}
	public void optionAction(OptionEvent e) {
		// Changed filtering options.
		if (e.isFilterChange()) {
			SwingUtilities.invokeLater(new MethodRunner(this,"optionChange"));
		}
	}
	public void optionChange() {
		// Turn on the busy cursor.
		busyCursor(true);

		// Set the timed interval refresh option state.
		if (getDropboxOptions().refreshOnTimer) {
			setInterval(getDropboxOptions().refreshTimerInterval);
			if (intervalTimer.isRunning()) {
				intervalTimer.restart();
			}
			else {
				intervalTimer.start();
			}
		}
		else {
			if (intervalTimer.isRunning()) {
				intervalTimer.stop();
			}
		}

		if (refreshThread == null) {
			inbox = true;
			all	 = false;
			refreshThread = new WorkerThread(this);
			refreshThread.start();
		}
		else {
			busyCursor(false);
		}
	}
	// Refresh all of the package tables.
	public void refreshAll() {
		busyCursor(true);
		all = true;
		inbox = false;
		refreshThread = new WorkerThread(this);
		refreshThread.start();
	}
	// Set the timed interval delay value in minutes.
	public void setInterval(int delayInMinutes) {
		int msec = delayInMinutes * 60000;
		intervalTimer.setInitialDelay(msec);
		intervalTimer.setDelay(msec);

		if (intervalTimer.isRunning()) {
			intervalTimer.restart();
		}
	}
	// Start the timed interval refresh.
	public void startTimer() {
		intervalTimer.start();
	}
	// Stop the timed interval refresh.
	public void stopTimer() {
		intervalTimer.stop();
		// TODO: What about synchronizing a running thread with Logout?
	}
}
private RefreshListener inboxRefreshListener = new RefreshListener();
private RefreshListener outboxRefreshListener = new RefreshListener();
/**
 * Handles restart of cancelled or failed file uploads.
 */
private class RestartListener implements ActionListener, Runnable {
	private PackageInfo pkg = null;
	private long[] fileIDs = null;
	private String[] fileNames = null;
	public void actionPerformed(ActionEvent e) {
		try {
			busyCursor(true);

			int s[] = getSendFileTbl().getSelectedRows();
			fileIDs = new long[s.length];
			fileNames = new String[s.length];

			for (int i = 0; i < s.length; i++) {
				int u = getSendFileSortTM().getUnsortedIndex(s[i]);
				fileIDs[i] = getSendFileTM().getFileID(u);
				fileNames[i] = getSendFileTM().getFileName(u);
			}

			int r = getSendPkgTbl().getSelectedRow();
			int u = getSendPkgSortTM().getUnsortedIndex(r);
			pkg = getSendPkgTM().getPackageInfo(u);

			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void run() {
		try {
			for (int i = 0; i < fileIDs.length; i++) {
				String source = sourceMgr.getSource(fileIDs[i]);
				String error = getFileStatusTM().uploadFile(pkg.getPackageId(),fileNames[i],source,pkg.getPackageName());
				if (error != null) {
					errorHandler.addMsg(fileNames[i] + ": " + error,"Restart File Upload");
				}
			}

			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
}
private RestartListener restartListener = new RestartListener();
/**
 * Handles list selections on the Drafts file table.
 */
private class SendFileListSelectionListener implements ListSelectionListener, TableModelListener {
	public void tableChanged(TableModelEvent e) {
		valueChanged(new ListSelectionEvent(getSendFileTbl().getSelectionModel(),0,0,false));
	}
	public void valueChanged(ListSelectionEvent e) {
		try {
			if (e.getValueIsAdjusting())
				return;

			boolean enable = (getSendFileTbl().getSelectedRowCount() > 0);

			getSendFileDeleteBtn().setEnabled(enable);
			getFilesDeleteMI().setEnabled(enable);

			int[] s = getSendFileTbl().getSelectedRows();
			for (int i = 0; i < s.length; i++) {
				int u = getSendFileSortTM().getUnsortedIndex(s[i]);
				if (! getSendFileTM().isFailed(u)) {
					enable = false;
				}
			}

			getSendFileRestartBtn().setEnabled(enable);
			getFilesRestartMI().setEnabled(enable);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
}
private SendFileListSelectionListener sendFileListSelectionListener = new SendFileListSelectionListener();
/**
 * Handles list selections on the Drafts package table.
 * 
 * Design Note: Normally, package selections invoke the busy cursor. This sequences the
 * selections. With the introduction of the drop target, this selection listener can have
 * many queries outstanding as the mouse passes over potential packages for the drop.
 * currentThread is introduced so that the various query threads can determine if their
 * particular result is still relevant and, therefore, should be posted.
 */
private class SendPkgListSelectionListener implements ListSelectionListener, Runnable {
	private PackageInfo pkg = null;
	private Vector files = null;
	private Thread currentThread = null;
	public void valueChanged(ListSelectionEvent e) {
		try {
			// An interim selection, ignore it.
			if (e.getValueIsAdjusting())
				return;

			int i = getSendPkgTbl().getSelectedRowCount();

			// A single row selection, dispatch a thread to query the package's files.
			if (i == 1) {
				busyCursor(true);

				int u = getSendPkgSortTM().getUnsortedIndex(getSendPkgTbl().getSelectedRow());
				pkg = getSendPkgTM().getPackageInfo(u);

				synchronized(this) {
					currentThread = new WorkerThread(this);
					currentThread.start();
				}
			}
			// Empty or multiple selection, no files to show. Just invalidate any
			// outstanding results.
			else {
				synchronized(this) {
					currentThread = null;

					SwingUtilities.invokeLater(new MethodRunner(DropBoxPnl.this,"emptySendFile"));
					SwingUtilities.invokeLater(new MethodRunner(DropBoxPnl.this,"sendPkgSelectAdjust"));
				}
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void addFilesToSendTbl() {
		getSendFileTM().addFiles(pkg.getPackageId(),files);
	}
	public void run() {
		try {
			try {
				synchronized (this) {
					if (currentThread == Thread.currentThread()) {
						SwingUtilities.invokeLater(new MethodRunner(DropBoxPnl.this,"emptySendFile"));
						SwingUtilities.invokeLater(new MethodRunner(DropBoxPnl.this,"sendPkgSelectAdjust"));
					}
				}

				files = dboxAccess.queryPackageContents(pkg.getPackageId());

				synchronized (this) {
					if (currentThread == Thread.currentThread()) {
						SwingUtilities.invokeLater(new MethodRunner(this,"addFilesToSendTbl"));
					}
				}
			} catch(DboxException e) {
				synchronized (this) {
					if (currentThread == Thread.currentThread()) {
						errorHandler.addMsg(e.getMessage(),"Query Contents Failed");
					}
				}
			} catch(RemoteException e) {
				synchronized (this) {
					if (currentThread == Thread.currentThread()) {
						errorHandler.addMsg(e.getMessage(),"Query Contents Failed");
					}
				}
			}

			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
}
private SendPkgListSelectionListener sendPkgListSelectionListener = new SendPkgListSelectionListener();
/**
 * Abstract document listener.
 */
private abstract class DropboxDocumentListener implements DocumentListener {
	public void changedUpdate(DocumentEvent e) {
		update(e);
	}
	public void insertUpdate(DocumentEvent e) {
		update(e);
	}
	public void removeUpdate(DocumentEvent e) {
		update(e);
	}
	public abstract void update(DocumentEvent e);
}
/**
 * Download confirmation document listener
 */
private class DownConfListener extends DropboxDocumentListener implements ItemListener, ActionListener {
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getDownConfChgBtn()) {
			changeDirectory();
		}
		else if (e.getSource() == getDownConfCanBtn()) {
			getDownConfirmDlg().dispose();
		}
	}
	public void itemStateChanged(ItemEvent e) {
		update();
	}
	public void update(DocumentEvent e) {
		update();
	}
	public void changeDirectory() {
		File baseDir = null;
		String fileName = null;
		String fileRelPath = null;

		// Are we changing directory for a single file?
		if (saveFile != null) {
			// Single file. Get any relative path from source file.
			int i = saveFile.lastIndexOf(File.separatorChar);
			fileRelPath = i == -1 ? null : saveFile.substring(0,i);

			// Get directory and name from text field.
			File filePath = new File(getDownConfTF().getText().trim());
			fileName = filePath.getName();
			String parent = filePath.getParent();
		
			// Determine base directory. See if directory includes the relative path.
			if (fileRelPath != null) {
				if (parent.endsWith(fileRelPath)) {
					baseDir = new File(parent.substring(0,parent.lastIndexOf(fileRelPath)));
				}
				else {
					fileRelPath = null;
					baseDir = new File(parent);
				}
			}
			else {
				baseDir = new File(parent);
			}
		}
		// A directory.
		else {
			baseDir = new File(getDownConfTF().getText().trim());
		}

		// show filechooser and start in base directory.
		JFileChooser fc = new JFileChooser();
		fc.setApproveButtonText("Change");
		fc.setApproveButtonToolTipText("Change directory location for download");
		fc.setDialogTitle("Change Directory for Download");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setCurrentDirectory(baseDir);
		int option = fc.showDialog(DropBoxPnl.this,null);

		// if ok from filechooser, set new path.
		if (option == JFileChooser.APPROVE_OPTION) {
			String path = fc.getSelectedFile().getPath();

			if (fileRelPath != null) path += File.separator + fileRelPath;
			if (fileName != null) path += File.separator + fileName;

			getDownConfTF().setText(path);
		}
	}
	public void update() {
		String path = getDownConfTF().getText();
	
		if (path == null || path.trim().length() == 0) {
			if (isFile) {
				getDownConfWarnLbl().setText("Please enter a file path!");
			}
			else {
				getDownConfWarnLbl().setText("Please enter a directory path!");
			}
			getDownConfWarnLbl().setForeground(Color.red);
			getDownConfOkBtn().setEnabled(false);
			return;
		}
	
		File PATH;
		
		try {
			PATH = new File(path.trim());
		} catch (Exception ioe) {
			if (isFile) {
				getDownConfWarnLbl().setText("Please enter a file path!");
			}
			else {
				getDownConfWarnLbl().setText("Please enter a directory path!");
			}
			getDownConfWarnLbl().setForeground(Color.red);
			getDownConfOkBtn().setEnabled(false);
			return;
		}
	
		if (isFile) {
			if (PATH.exists()) {
				if (PATH.isDirectory()) {
					getDownConfWarnLbl().setText("Can not overwrite a directory!");
					getDownConfWarnLbl().setForeground(Color.red);
					getDownConfOkBtn().setEnabled(false);
				}
				else if (getDownConfCB().isSelected()) {
					getDownConfWarnLbl().setText("File exists, enable over-write to replace.");
					getDownConfWarnLbl().setForeground(Color.red);
					getDownConfOkBtn().setEnabled(false);
				}
				else {
					getDownConfWarnLbl().setText("");
					getDownConfOkBtn().setEnabled(true);
				}
			}
			else {
				try {
					File DIR = new File(PATH.getParent());
	
					if (DIR.exists()) {
						getDownConfWarnLbl().setText("");
					}
					else {
						getDownConfWarnLbl().setText("Directory will be created");
					}
					getDownConfWarnLbl().setForeground(Color.blue)	;
					getDownConfOkBtn().setEnabled(true);
				} catch (Exception ioe) {
					getDownConfWarnLbl().setText("");
					getDownConfOkBtn().setEnabled(true);
				}
			}
		}
		else {
			if (PATH.exists()) {
				if (PATH.isFile()) {
					getDownConfWarnLbl().setText("File exists, please enter a directory!");
					getDownConfWarnLbl().setForeground(Color.red);
					getDownConfOkBtn().setEnabled(false);
				}
				else {
					getDownConfWarnLbl().setText("");
					getDownConfOkBtn().setEnabled(true);
				}
			}
			else {
				getDownConfWarnLbl().setText("Directory will be created");
				getDownConfWarnLbl().setForeground(Color.blue)	;
				getDownConfOkBtn().setEnabled(true);
			}
		}
	}
}
private DownConfListener downConfListener = new DownConfListener();
/**
 * Login document listener.
 */
private class LoginDocumentListener extends DropboxDocumentListener {
	public void update(DocumentEvent e) {
		String user = getUserTF().getText();
		String pwd = new String(getPwdTF().getPassword());

	getLoginOkBtn().setEnabled(user != null && user.length	() > 0 &&
			pwd != null && pwd.length() > 0);
	}
}
private LoginDocumentListener loginDocumentListener = new LoginDocumentListener();
/**
 * Package editor document listener.
 */
private class PkgEditListener extends DropboxDocumentListener implements ActionListener, MouseListener, ListSelectionListener, Runnable {
	// Company query list of buddies.
	private Vector queryBuddies = new Vector();
	/**
	 * Handle mouse clicked on the from or to list box.
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			if (e.getSource() == getEditFromLB())
				addUser();
			else
				removeUser();
		}
	}
	/**
	 * Handle button press on user management buttons.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getToUserTF() || e.getSource() == getUserNewAddBtn()) {
			addNewUser();
		}
		else if (e.getSource() == getEditAddBtn()) {
			addUser();
		}
		else if (e.getSource() == getUserDelBtn()) {
			deleteUser();
		}
		else if (e.getSource() == getEditRemBtn()) {
			removeUser();
		}
	}
	/**
	 * Handle selection change on the from or to list box.
	 */
	public void valueChanged(ListSelectionEvent e) {
		// What is selected?
		Object[] to = getEditToLB().getSelectedValues();
		Object[] from = getEditFromLB().getSelectedValues();

		// Check if a single group is selected, enable query group button.
		if (to.length + from.length == 1) {
			Buddy buddy;
			if (to.length == 1) {
				buddy = (Buddy) to[0];
			}
			else {
				buddy = (Buddy) from[0];
			}
			getGrpQueryBtn().setEnabled(buddy.type == Buddy.GROUP);
		}
		else {
			getGrpQueryBtn().setEnabled(false);
		}

		// Changed selections in to list box? Adjust remove user button.
		if (e.getSource() == getEditToLB()) {
			getEditRemBtn().setEnabled(to.length > 0);
		}
		// Changed selections in from list box?
		else if (e.getSource() == getEditFromLB()) {
			// Adjust add user button.
			getEditAddBtn().setEnabled(from.length > 0);

			// If all users, enable delete user button.
			boolean allUsers = true;
			for (int i = 0; (i < from.length) && allUsers; i++) {
				Buddy buddy = (Buddy) from[i];
				if (buddy.type != Buddy.USER) {
					allUsers = false;
				}
			}

			getUserDelBtn().setEnabled(allUsers);
		}
	}
	/**
	 * Handle adding users from buddy list (left to right move).
	 */
	public void addUser() {
		// For every selected buddy...
		Object[] objs = getEditFromLB().getSelectedValues();
		for (int i = 0; i < objs.length; i++) {
			// Move into the To list.
			Buddy buddy = (Buddy) objs[i];
			getEditFromLM().removeElement(buddy);
			addNameToList(buddy,getEditToLM());

			// If buddy is missing company info, we'll need to get it.
			if (buddy.companyList == null) {
				queryBuddies.add(buddy);
			}
		}
		
		// Removed all selected buddies in from list, so buttons get disabled.
		getUserDelBtn().setEnabled(false);
		getEditAddBtn().setEnabled(false);

		// Update the over-all panel, may be able to submit the package.
		nameDescOrToListUpdate();
		
		// If company query list is not empty, we need to get company info.
		if (queryBuddies.size() > 0) {
			getPkgEditDlg().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			getPkgEditDlg().getGlassPane().setVisible(true);
			Thread t = new WorkerThread(this);
			t.start();
		}
		// Got all the companies, look for a cross company condition.
		else {
			checkCrossCompany();
		}
	}
	/**
	 * Handle adding new (or keyed in) users.
	 */
	public void addNewUser() {
		// For every whitespace delimited name...
		String names = getToUserTF().getText().trim();
		StringTokenizer list = new StringTokenizer(names," ,\t\r\n\f");

		while (list.hasMoreTokens()) {
			// Get the user name keyed.
			String user = list.nextToken();

			// Not in our saved buddy list? Add it.
			if (! users.contains(user)) {
				users.addElement(user);
			}

			// Create a new buddy object.
			Buddy buddy = new Buddy(user);
			buddy.type = Buddy.USER;

			// If from list contained same name, use it (it may have company info).
			if (getEditFromLM().contains(buddy)) {
				int i = getEditFromLM().indexOf(buddy);
				buddy = (Buddy) getEditFromLM().get(i);
				getEditFromLM().removeElement(buddy);
			}

			// Put the buddy in the To list.
			addNameToList(buddy,getEditToLM());
			
			// If buddy is missing company info, we'll need to get it.
			if (buddy.companyList == null) {
				queryBuddies.add(buddy);
			}
		}

		// Clear the text field, and update the over-all panel,
		// may be able to submit the package.
		getToUserTF().setText("");
		userUpdate();
		nameDescOrToListUpdate();
		
		// If company query list is not empty, we need to get company info.
		if (queryBuddies.size() > 0) {
			getPkgEditDlg().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			getPkgEditDlg().getGlassPane().setVisible(true);
			Thread t = new WorkerThread(this);
			t.start();
		}
		// Got all the companies, look for a cross company condition.
		else {
			checkCrossCompany();
		}
	}
	/**
	 * Handle deleting saved buddies in the From list.
	 */
	public void deleteUser() {
		Object[] objs = getEditFromLB().getSelectedValues();

		for (int i = 0; i < objs.length; i++) {
			Buddy b = (Buddy) objs[i];
			getEditFromLM().removeElement(b);
			users.removeElement(b.name);
		}

		getEditFromLB().clearSelection();

		// Update gui elements
		getUserDelBtn().setEnabled(false);
	}
	/**
	 * Handle moving buddies in the To list to the From list. 
	 *
	 */
	public void removeUser() {
		Object[] objs = getEditToLB().getSelectedValues();
		for (int i = 0; i < objs.length; i++) {
			getEditToLM().removeElement(objs[i]);
			addNameToList((Buddy) objs[i],getEditFromLM());
		}
		getEditRemBtn().setEnabled(false);
		nameDescOrToListUpdate();
		checkCrossCompany();
	}
	public void update(DocumentEvent e) {
		if (e.getDocument() == getPkgNameTF().getDocument()) {
			nameDescOrToListUpdate();
		}
		else if (e.getDocument() == getToUserTF().getDocument()) {
			userUpdate();
		}
		else if (e.getDocument() == getPkgDescTA().getDocument()) {
			int i = e.getDocument().getLength();
			getPkgDescCharLbl().setText(Integer.toString(i));
			Color c = i > DESC_MAX ? Color.red : Color.black;
			getPkgDescCharLbl().setForeground(c);
			getPkgDescLbl2().setForeground(c);
			nameDescOrToListUpdate();
		}
	}
	public void nameDescOrToListUpdate() {
		String name = getPkgNameTF().getText();
		int descCnt = getPkgDescTA().getDocument().getLength();
		int toCnt = getEditToLM().getSize();
		
		boolean enable = name != null && name.length() > 0 && toCnt > 0 && descCnt <= DESC_MAX;
		
		getPkgOkBtn().setEnabled(enable);
		getPkgAddFilesBtn().setEnabled(enable);
	}
	public void userUpdate() {
		String user = getToUserTF().getText();
		getUserNewAddBtn().setEnabled(user != null && user.trim().length() > 0);
	}
	public void updateCompanyInfo() {
		// Notify the listbox of the changes in the data model. Kind of awkward,
		// but we are using the default list model.
		Enumeration e = queryBuddies.elements();
		while (e.hasMoreElements()) {
			Buddy b = (Buddy) e.nextElement();
			int j = getEditToLM().indexOf(b);
			getEditToLM().set(j,b);
		}
		
		// Done with the query of these buddy elements
		queryBuddies.clear();
		
		// Post any cross company issue.
		checkCrossCompany();
		
		// Turn off the hour-glass.
		getPkgEditDlg().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		getPkgEditDlg().getGlassPane().setVisible(false);

	}
	public void checkCrossCompany() {
		// Get the company the sender represents.
		String myCompany = (String) session.get(DropboxAccess.Company);

		// Enable the label?
		crossCompanyLbl.setVisible(BuddyMgr.isCrossCompanyBuddyList(myCompany,getEditToLM().elements()));
	}
	public void run() {
		// Query the server for each buddy for whom we need a company list.
		Vector sendAcls = new Vector();
		Enumeration e = queryBuddies.elements();
		while (e.hasMoreElements()) {
			// Build an acl for the buddy.
			Buddy b = (Buddy) e.nextElement();
			
			AclInfo a = new AclInfo();
			a.setAclName(b.name);
			if (b.type == Buddy.USER)
				a.setAclStatus(DropboxAccess.STATUS_USER);
			else if (b.type == Buddy.GROUP) 
				a.setAclStatus(DropboxAccess.STATUS_GROUP);
			else
				a.setAclStatus(DropboxAccess.STATUS_PROJECT);

			sendAcls.clear();
			sendAcls.add(a);

			// Query the company list for the acl and store it in the buddy.
			try {
				Vector companies = dboxAccess.queryRepresentedCompanies(sendAcls,false);
				Enumeration cList = companies.elements();
				while (cList.hasMoreElements()) {
					String company = (String) cList.nextElement();
					b.companyList = (b.companyList == null)
										? company
										: b.companyList + "," + company;
				}
			}
			catch (DboxException exc) {
				errorHandler.addMsg(exc.getMessage(),"Query ACL Company Info Failed");
			}
			catch (RemoteException exc) {
				errorHandler.addMsg(exc.getMessage(),"Query ACL Company Info Failed");
			}
		}

		// Have the GUI thread post the results and check for a cross company condition.
		SwingUtilities.invokeLater(new MethodRunner(this,"updateCompanyInfo"));
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}
private PkgEditListener pkgEditListener = new PkgEditListener();
/**
 * Handles upload of files.
 */
private class UploadListener extends CreatePkgActionListener implements LocalFilePnlListener {
	public void uploadBtn_actionPerformed(EventObject e) {
		try {
			busyCursor(true);
			
			// Remember which pane the forward came from and show the Send New Packages pane.
			Component tpSel = getRemoteTP().getSelectedComponent();
			getRemoteTP().setSelectedComponent(getSendPnl());
	
			// Note the items to be forwarded...
			Vector items = new Vector();
			int[] s = getLocalFilePnl().getLocalTbl().getSelectedRows();
			items.addElement(getLocalFilePnl().getLocalTM().getDirectory());
			for (int i = 0; i < s.length; i++) {
				items.addElement(getLocalFilePnl().getLocalSortTM().getValueAt(s[i],1));
			}
	
			// No packages defined?
			if (getSendPkgTM().getRowCount() == 0) {
				// See if we can create one.
				if (JOptionPane.showConfirmDialog(DropBoxPnl.this,"Create a new package for selected files?","No New Package Defined!",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
					// Cancel forward, flip back to original pane.
					getRemoteTP().setSelectedComponent(tpSel);
					busyCursor(false);
					return;
				}
	
				// Define a new package.
				pkgInfo = null;
			}
	
			// Query for an existing package.
			else {
				// Set up confirm dialog.
				int i = getSendPkgTbl().getSelectedRowCount();
				if (i == 1) {
					int j = getSendPkgTbl().getSelectedRow();
					getConfirmPkgTbl().getSelectionModel().setSelectionInterval(j,j);
					getConfirmOkBtn().setEnabled(true);
				}
				else {
					getConfirmPkgTbl().getSelectionModel().clearSelection();
					getConfirmOkBtn().setEnabled(false);
				}
				btnPressed = null;
				getConfirmDlg().setLocationRelativeTo(DropBoxPnl.this);
				getConfirmDlg().setVisible(true);
	
				// Confirmed an existing package.
				if (btnPressed == getConfirmOkBtn()) {
					int row = getConfirmPkgTbl().getSelectedRow();
					int pkg = getSendPkgSortTM().getUnsortedIndex(row);
					pkgInfo = getSendPkgTM().getPackageInfo(pkg);
				}
				// Define a new package.
				else if (btnPressed == getConfirmNewBtn()) {
					pkgInfo = null;
				}
				// Cancelled.
				else {
					// Flip back to original pane.
					getRemoteTP().setSelectedComponent(tpSel);
					busyCursor(false);
					return;
				}
			}
	
			// We have items, but no acls.
			pkgDropItems = null;
			pkgItems = items;
			pkgAcls = null;

			// If we are adding to an ITAR package, certify.
			if (pkgInfo != null &&
				pkgInfo.getPackageItar() &&
				! itarListener.certifySession(ownerContainer)) {
				busyCursor(false);
				return;
			}

			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void dropFileList(List list, PackageInfo p) {
		try {
			pkgInfo = p;
			pkgDropItems = list;
			pkgItems = null;

			// If we are adding to an ITAR package, certify.
			if (pkgInfo != null &&
				pkgInfo.getPackageItar() &&
				! itarListener.certifySession(ownerContainer)) {
				return;
			}
	
			busyCursor(true);

			WorkerThread t = new WorkerThread(this);
			t.start();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
	public void run() {
		try {
			// Create a new package? Use the inherited run() from CreatePkgActionListener
			if (pkgInfo == null) {
				super.run();
			}
			// Dropped files into an existing package?
			else if (pkgDropItems != null) {
				// Prepare for a vector of FileBundle objects
				Vector fbs = new Vector();

				// For each dropped item:
				for (int i = 0; i < pkgDropItems.size(); i++) {
					// A new FileBundle
					FileBundle fb = new FileBundle();
					fb.fileNames = new Vector();
					fb.files = new Vector();

					// Base directory is directory of dropped item.
					File f = (File) pkgDropItems.get(i);
					fb.baseDirectory = f.getParent();

					// Scan dropped item and then add FileBundle to vector.
					scanDirectory(fb.fileNames,fb.files,f.getName(),f.getPath());
					fbs.addElement(fb);
				}

				// Queue the uploads (no auto-commit).
				getFileStatusTM().uploadFiles(false,pkgInfo.getPackageId(),fbs,pkgInfo.getPackageName());

				SwingUtilities.invokeLater(busyCursorOff);
			}
			// Upload files into an existing package.
			else {
				// Prepare the vector of one FileBundle
				Enumeration items = pkgItems.elements();

				Vector fbs = new Vector();
				FileBundle fb = new FileBundle();
				fbs.addElement(fb);
				fb.fileNames = new Vector();
				fb.files = new Vector();

				// First element is the base directory.
				fb.baseDirectory = (String) items.nextElement();

				// All other elements are the files/dirs.
				while (items.hasMoreElements()) {
					String name = (String) items.nextElement();
					String absName = fb.baseDirectory + File.separator + name;
					scanDirectory(fb.fileNames,fb.files,name,absName);
				}
				// Queue the uploads (no auto-commit).
				getFileStatusTM().uploadFiles(false,pkgInfo.getPackageId(),fbs,pkgInfo.getPackageName());
		
				SwingUtilities.invokeLater(busyCursorOff);
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}	    
	}
}
private UploadListener uploadListener = new UploadListener();
/**
 * Handle viewing of package description for inbox package table.
 */
private class ViewDescriptionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		int r = getInboxPkgTbl().getSelectedRow();
		
		PackageInfo p = getInboxPkgTM().getPackageInfo(getInboxPkgSortTM().getUnsortedIndex(r));

		getViewDescTA().setText(p.getPackageDescription());
		getViewDescTA().setCaretPosition(0);
		
		getViewDescDlg().setLocationRelativeTo(ownerContainer);
		getViewDescDlg().setVisible(true);
	}
}
private ViewDescriptionListener viewDescriptionListener = new ViewDescriptionListener();
/**
 * Session expiration handler: maintains the session credentials to 
 * prevent them from expiring.
 */
private class SessionHandler extends WorkerThread {
	public boolean stop = false;
	private String error = null;
	public void run() {
		// Run until we don't have a session, or are told to stop.
		synchronized(this) {
			while (! stop && session != null) {
				// Wait until we are half-way through the session time to live.
				// TTL is in seconds, so we multiple by 500 to get half time in millis.
				// We'll also get notified if the user logs our and we can stop.
				Long ttl = (Long) session.get(DropboxAccess.SessionTTL);
				long time = ttl.longValue()*500;
				long start = System.currentTimeMillis();
				while (time > 1000 && ! stop) {
					try {
						wait(time);
					} catch(InterruptedException e) {
					}
					
					// Woke up, see how much time we have left to half of TTL.
					long now = System.currentTimeMillis();
					time -= now - start;
					start = now;
				}
	
				if (! stop) {
					try {
						HashMap newSession = dboxAccess.refreshSession();
						session = newSession;
						dboxFactory.setSessionId(dboxAccess,newSession);
					} catch(DboxException e) {
						error = e.getMessage();
						stop = true;
						SwingUtilities.invokeLater(new MethodRunner(this,"handleError"));
					} catch(RemoteException e) {
						error = e.getMessage();
						stop = true;
						SwingUtilities.invokeLater(new MethodRunner(this,"handleError"));
					}
				}
			}
		}
	}
	public void handleError() {
		String[] options = { "Retry", "Cancel" };
		String[] message = { "Could not renew the session credentials!",
							 error,
							 "Try again or cancel?" };
		int selectedValue = MessagePane.showOptionDialog(DropBoxPnl.this,message,
									"Session Renewal Failed",0,MessagePane.ERROR_MESSAGE,
									null, options, options[0]);

		if (selectedValue == 0) {
			sessionHandler = new SessionHandler();
			sessionHandler.start();
		}
	}
}
private SessionHandler sessionHandler = null;
/**
 * FileTransfer constructor comment.
 */
public DropBoxPnl() {
	this(new JFrame());
}
public DropBoxPnl(JFrame owner) {
	this(owner,owner.getRootPane());
}
public DropBoxPnl(Frame owner,JRootPane rootPane) {
	super();
	this.ownerContainer = owner;
	this.ownerRootPane = rootPane;
	initialize();
}
public void addDropBoxPnlListener(DropBoxPnlListener l) {
	if (l == null) return;
	listeners = DBEventMulticaster.addDropBoxPnlListener(listeners,l);
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 10:21:48 AM)
 * @param name java.lang.String
 * @param lm javax.swing.ListModel
 */
public void addNameToList(Buddy buddy, DefaultListModel lm) {
	int i = 0;
	int j = 0;
	Enumeration e = lm.elements();

	// Step through the list to find the place to add the name.
	while (e.hasMoreElements()) {
		Buddy lBuddy = (Buddy) e.nextElement();
		if (lBuddy.type != buddy.type) {
			j = (lBuddy.type < buddy.type) ? - 1: 1;
		}
		else {
			j = lBuddy.name.compareTo(buddy.name);
		}

		// Current name bigger than new name, insert here.
		if (j > 0) {
			lm.insertElementAt(buddy,i);
			return;
		}
		// Name already in list, check type it.
		else if (j == 0) {
			// Current type is bigger than new type, insert here.
			if (lBuddy.type > buddy.type) {
				lm.insertElementAt(buddy,i);
				return;
			}
			// Name of this type already in list, ignore it.
			else if (lBuddy.type == buddy.type) {
				return;
			}
			// Maybe the next spot.
			else {
				i++;
			}
		}
		// Maybe the next spot.
		else {
			i++;
		}
	}
	// New name goes at end of list.
	lm.addElement(buddy);
}
/**
 * Comment
 */
public void addSender() {
	int[] s = getInboxPkgTbl().getSelectedRows();
	String[] msg = new String[s.length+1];

	if (s.length == 1)
		msg[0] = "Add the following ID to your saved user list?";
	else
		msg[0] = "Add the following IDs to your saved user list?";

	for (int i = 0; i < s.length; i++) {
		msg[i+1] = "   " + getInboxPkgTM().getPackageOwner(getInboxPkgSortTM().getUnsortedIndex(s[i]));
	}

	if (MessagePane.showConfirmDialog(this,msg,"Save User ID",MessagePane.YES_NO_OPTION) == MessagePane.YES_OPTION) {
		users = buddyMgr.getBuddyList();
		boolean added = false;

		for (int i = 0; i < s.length; i++) {
			int u = getInboxPkgSortTM().getUnsortedIndex(s[i]);
			String owner = getInboxPkgTM().getPackageOwner(u);
			if (! users.contains(owner)) {
				users.addElement(owner);
				added = true;
			}
		}

		if (added) {
			buddyMgr.setBuddyList(users);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:52:26 PM)
 * @param args java.lang.String[]
 */
public void begin(String[] args) {
	// Determine our platform.
	isWin = (System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1);

	boolean doUpdates = true;
	//boolean autoLogin = false;
	String line = null;
	BufferedReader rdr = null;
	int i = 0;

	try {
		if (args.length == 0) {
			rdr = new BufferedReader(new InputStreamReader(System.in));
			line = rdr.readLine();
		}
		else if (args.length == 1 && args[0].charAt(0) != '-') {
			FileReader file = new FileReader(args[0]);
			rdr = new BufferedReader(file);
			line = rdr.readLine();
		}
		else {
			line = args[i++];
		}
	
		while (line != null) {
			line = line.trim();
			System.out.println(line);
			String LINE = line.toUpperCase();
			if (LINE.startsWith(TOKEN_OPT)) {
				String arg = null;
				if (rdr != null) {
					if (line.length() > 10) arg = line.substring(10).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-CH_TOKEN requires an value.");
					return;
				}
				else {
					loginListener.setToken(arg);
					//autoLogin = true;
				}
			} else if (LINE.startsWith("-URL")) {
				String arg = null;
				if (rdr != null) {
					if (line.length() > 5) arg = line.substring(5).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-URL requires an value.");
					return;
				}
				else {
					serviceUrl = arg;
				}
			} else if (line.equals(NOUPDATE_OPT)) {
				doUpdates = false;
			} else if (line.equalsIgnoreCase("-the_end")) {
				// caboose, ignore it...
			} else {
				syntax("Bad parm = '" + line + "'");
				return;
			}
	
			if (line.equalsIgnoreCase("-THE_END")) {
				line = null;
			}
			else if (rdr != null) {
				line = rdr.readLine();
			}
			else if (i < args.length) {
				line = args[i++];
			}
			else {
				line = null;
			}
		}
	}
	catch (Exception e) {
		System.out.println("Warning: error occurred while reading parameters!");
		handleException(e);
	}

	// Create the command-line startup files.
	try {
		if (isWin) {
			LaunchApp.createDboxBat();
			LaunchApp.createCmdlineBat();
			LaunchApp.createCmdlineScript();
		}
		else {
			LaunchApp.createDboxShell();
			LaunchApp.createCmdlineShell();
		}
	}
	catch (Exception e) {
		System.out.println("Unable to create Dropbox CMD line shell!");
		handleException(e);
	}

	// Only Create Icon on Windows.
	if (! isWin) {
		getFileM().remove(getCreateIconMI());
		getFileM().validate();
	}

	resetToLoggedOut();

	try	{
		File temp = new File("dropbox.ini");
		if (temp.exists()) {
			dboxini.load("dropbox.ini");
		}
	}
	catch (Exception e) {
		System.out.println("Exception in loading dropbox.ini");
		handleException(e);
	}

	getDropboxOptions().setIniConfig(dboxini);
	getDropboxOptions().addOptionListener(this);
	getDropboxOptions().addOptionListener(inboxRefreshListener);

	// Prepare the local components. they are always
	// active regardless of whether we are logging in or
	// not.
	getLocalFilePnl().initGui(isWin);

	// Load the user's preferences.
	loadPreferences();

	// Build an service accessor from the factory.
	try {
		dboxFactory = new HessianConnectFactory();
		dboxFactory.setTopURL(new URL(serviceUrl));
		dboxAccess = dboxFactory.getProxy();
		((ProxyDebugInterface) dboxAccess).enableDebug(ProxyDebugInterface.NAMES);

		// Give them the dboxAccess object.
		getInboxPkgTM().setDropboxAccess(dboxAccess);
		getInboxPkgTM().setErrorHandler(errorHandler);
		getManageGroups().setDropboxAccess(dboxAccess);
		getDropboxOptions().setDropboxAccess(dboxAccess);
		getFileStatusTM().setDropboxAccess(dboxAccess);
		sourceMgr.setDropboxAccess(dboxAccess);
	} catch (Exception e) {
		errorHandler.addMsg("Failed to create service accessor!","Fatal Error!");
		handleException(e);
	}

	// Hide the splash.
	fireHideSplash();

	// Check for updates and possibly restart?
	if (doUpdates) {
		CodeUpdaterGui c = new CodeUpdaterGui(ownerContainer,serviceUrl);
		int result = c.update();
		if (result != CodeUpdaterGui.GOOD) {
			System.exit(result == CodeUpdaterGui.UPDATE ? 200 : 1);
		}
	}

	// Show the window.
	fireShowMe();

	// Restore the split positions.
	SwingUtilities.invokeLater(new MethodRunner(this,"loadSplit"));
	
	// Setup networking information.
	try {
		SwingUtilities.invokeAndWait(new MethodRunner(this,"configNetwork"));
	}
	catch (Exception e) {
		System.out.println("Exception while waiting for proxy authentication!");
		handleException(e);
	}

	// If we have a token, then log in automatically for the user.
	// Auto login anyway everytime we start, token (web launch) or not (desktop).
	//if (autoLogin) {
		SwingUtilities.invokeLater(new MethodRunner(getLoginMI(),"doClick"));
	//}
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

	//System.out.println("busyCursor: " + on + " " + busyCursor);

	if (busyCursor > 0) {
		ownerRootPane.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ownerRootPane.getGlassPane().setVisible(true);
		if (busyCursorTimer == null) {
			busyCursorTimer = new BusyCursorTimer();
			busyCursorTimer.start();
		}
	}
	else {
		ownerRootPane.getGlassPane().setVisible(false);
		ownerRootPane.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		busyCursorTimer.interrupt();
		busyCursorTimer = null;
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
 * Comment
 */
public void closeAddFilesTip() {
	getPkgAddFilesInfoDlg().dispose();

	if (getPkgAddFilesInfoCB().isSelected()) {
		try {
			dboxini.setBoolProperty("NOADDFILETIP",true);
			dboxini.store("dropbox.ini");
		}
		catch (IOException e) {
			System.out.println("Unable to save dropbox.ini");
		}
	}
}
/**
 * Comment
 */
public void configNetwork() {
	// Open the edesign.ini file to extract network configs we need:
	try {
		cfg = new ConfigMgr("edesign.ini",ownerContainer);
	}
	catch (Exception e) {
		syntax("Unable to read file edesign.ini!");
		e.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void confirmPkgSelect(ListSelectionEvent e) {
	if (e.getValueIsAdjusting())
		return;

	int i = getConfirmPkgTbl().getSelectedRowCount();
	getConfirmOkBtn().setEnabled(i > 0);
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 9:44:26 AM)
 */
public void emptyInboxFile() {
	getInboxFileTbl().clearSelection();
	getInboxFileTM().clear();
	getInboxFileDownBtn().setEnabled(false);
	getInboxTransferBtn().setEnabled(false);
	getFilesDownMI().setEnabled(false);
	getFilesDeleteMI().setEnabled(false);
	getFilesFwdMI().setEnabled(false);
	getFilesRestartMI().setEnabled(false);
	getFilesViewMI().setEnabled(false);
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 9:44:26 AM)
 */
public void emptyInboxPkg() {
	getInboxPkgTbl().clearSelection();
	getInboxPkgTM().clear();
	getInboxPkgViewBtn().setEnabled(false);
	getInboxPkgDownBtn().setEnabled(false);
	getInboxPkgTransferBtn().setEnabled(false);
	getInboxAddSenderBtn().setEnabled(false);
	getPkgAddSenderMI().setEnabled(false);
	getPkgCreateMI().setEnabled(false);
	getPkgDeleteMI().setEnabled(false);
	getPkgDeliverMI().setEnabled(false);
	getPkgDownMI().setEnabled(false);
	getPkgFwdMI().setEnabled(false);
	getPkgEditMI().setEnabled(false);
	getPkgViewMI().setEnabled(false);
	getPkgViewDescMI().setEnabled(false);

	emptyInboxFile();
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 9:44:26 AM)
 */
public void emptyOutboxFile() {
	getOutboxFileTbl().clearSelection();
	getOutboxFileTM().clear();
	getOutboxFileDownBtn().setEnabled(false);
	getOutboxFileViewBtn().setEnabled(false);
	getOutboxTransferBtn().setEnabled(false);
	getFilesDownMI().setEnabled(false);
	getFilesDeleteMI().setEnabled(false);
	getFilesFwdMI().setEnabled(false);
	getFilesRestartMI().setEnabled(false);
	getFilesViewMI().setEnabled(false);
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 9:44:26 AM)
 */
public void emptyOutboxPkg() {
	getOutboxPkgTbl().clearSelection();
	getOutboxPkgTM().clear();
	getOutboxPkgDownBtn().setEnabled(false);
	getOutboxPkgViewBtn().setEnabled(false);
	getOutboxPkgEditBtn().setEnabled(false);
	getOutboxDeleteBtn().setEnabled(false);
	getPkgAddSenderMI().setEnabled(false);
	getPkgCreateMI().setEnabled(false);
	getPkgDeleteMI().setEnabled(false);
	getPkgDeliverMI().setEnabled(false);
	getPkgDownMI().setEnabled(false);
	getPkgEditMI().setEnabled(false);
	getPkgViewMI().setEnabled(false);
	getPkgViewDescMI().setEnabled(false);

	emptyOutboxFile();
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 9:44:26 AM)
 */
public void emptySendFile() {
	getSendFileTbl().clearSelection();
	getSendFileTM().clear();
	getSendFileDeleteBtn().setEnabled(false);
	getSendFileRestartBtn().setEnabled(false);
	getFilesDownMI().setEnabled(false);
	getFilesDeleteMI().setEnabled(false);
	getFilesFwdMI().setEnabled(false);
	getFilesRestartMI().setEnabled(false);
	getFilesViewMI().setEnabled(false);
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 9:44:26 AM)
 */
public void emptySendPkg() {
	getSendPkgTbl().clearSelection();
	getSendPkgTM().clear();
	getSendPkgDeleteBtn().setEnabled(false);
	getSendPkgEditBtn().setEnabled(false);
	getPkgAddSenderMI().setEnabled(false);
	getPkgCreateMI().setEnabled(true);
	getPkgDeleteMI().setEnabled(false);
	getPkgDeliverMI().setEnabled(false);
	getPkgDownMI().setEnabled(false);
	getPkgEditMI().setEnabled(false);
	getPkgViewMI().setEnabled(false);
	getPkgViewDescMI().setEnabled(false);

	emptySendFile();
}
/**
 * Comment
 */
public void exit() {
	fireExit();
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2003 10:32:26 AM)
 * @param e oem.edge.ed.odc.dropbox.client.FileStatusEvent
 */
public void fileStatusAction(FileStatusEvent e) {
	// For completed packages, commit them.
	if (e.isPackageComplete()) {
		// Turn on the busy cursor.
		SwingUtilities.invokeLater(busyCursorOn);

		// Get a commit handler to commit this package.
		CommitHandler ch = new CommitHandler();
		ch.pkgs = new PackageInfo[1];
		ch.pkgs[0] = getSendPkgTM().getPackageInfo(getSendPkgTM().getPackageRow(e.packageID));
		WorkerThread t = new WorkerThread(ch);
		t.start();
	}

	// For completed transfers, refresh the package info.
	if (e.isFileEnded()) {
		QueryPkgHandler qph = new QueryPkgHandler();
		qph.isUpload = e.isUpload;
		qph.pkgId = e.packageID;
		WorkerThread t = new WorkerThread(qph);
		t.start();
	}

	// For any upload.
	if ((e.isFileBegun() || e.isFileEnded()) && e.isUpload) {
		// Adjust the drafts package table toolbar.
		SwingUtilities.invokeLater(new MethodRunner(this,"sendPkgSelectAdjust"));
	}
}
public void fireExit() {
	if (listeners != null) {
		listeners.dropBoxPnlUpdate(new DropBoxPnlEvent(DropBoxPnlEvent.EXIT));
	}
}
public void fireHideSplash() {
	if (listeners != null) {
		listeners.dropBoxPnlUpdate(new DropBoxPnlEvent(DropBoxPnlEvent.HIDE));
	}
}
public void fireSavePreferences() {
	if (listeners != null) {
		listeners.dropBoxPnlUpdate(new DropBoxPnlEvent(DropBoxPnlEvent.SAVE_PREFERENCES));
	}
}
public void fireShowMe() {
	if (listeners != null) {
		listeners.dropBoxPnlUpdate(new DropBoxPnlEvent(DropBoxPnlEvent.SHOW));
	}
}
public void fireTitleChange(String title) {
	if (listeners != null) {
		listeners.dropBoxPnlUpdate(new DropBoxPnlEvent(title));
	}
}
/**
 * This method initializes aboutMI	
 * 	
 * @return javax.swing.JMenuItem	
 */    
private JMenuItem getAboutMI() {
	if (aboutMI == null) {
		aboutMI = new JMenuItem();
		aboutMI.setText("About");
		aboutMI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					aboutWindow.setPage(DropBoxPnl.this.getClass().getResource("/about.html"));
					aboutWindow.setLocationRelativeTo(ownerContainer);
					aboutWindow.showAbout();
				} catch (IOException io) {
					System.out.println("IO Error: " + io.getMessage());
				}
			}
		});
	}
	return aboutMI;
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
			ivjCanXferMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/stop.gif")));
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
 * Return the ConfirmBtnPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getConfirmBtnPnl() {
	if (ivjConfirmBtnPnl == null) {
		try {
			ivjConfirmBtnPnl = new javax.swing.JPanel();
			ivjConfirmBtnPnl.setName("ConfirmBtnPnl");
			ivjConfirmBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsConfirmOkBtn = new java.awt.GridBagConstraints();
			constraintsConfirmOkBtn.gridx = 0; constraintsConfirmOkBtn.gridy = 0;
			constraintsConfirmOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getConfirmBtnPnl().add(getConfirmOkBtn(), constraintsConfirmOkBtn);

			java.awt.GridBagConstraints constraintsConfirmCanBtn = new java.awt.GridBagConstraints();
			constraintsConfirmCanBtn.gridx = 1; constraintsConfirmCanBtn.gridy = 0;
			constraintsConfirmCanBtn.insets = new java.awt.Insets(0, 5, 0, 5);
			getConfirmBtnPnl().add(getConfirmCanBtn(), constraintsConfirmCanBtn);

			java.awt.GridBagConstraints constraintsConfirmNewBtn = new java.awt.GridBagConstraints();
			constraintsConfirmNewBtn.gridx = 2; constraintsConfirmNewBtn.gridy = 0;
			constraintsConfirmNewBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getConfirmBtnPnl().add(getConfirmNewBtn(), constraintsConfirmNewBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjConfirmBtnPnl;
}
/**
 * Return the ConfirmCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getConfirmCanBtn() {
	if (ivjConfirmCanBtn == null) {
		try {
			ivjConfirmCanBtn = new javax.swing.JButton();
			ivjConfirmCanBtn.setName("ConfirmCanBtn");
			ivjConfirmCanBtn.setText("Cancel");
			ivjConfirmCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getConfirmDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}	    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjConfirmCanBtn;
}
/**
 * Return the JDialogContentPane6 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getConfirmCP() {
	if (ivjConfirmCP == null) {
		try {
			ivjConfirmCP = new javax.swing.JPanel();
			ivjConfirmCP.setName("ConfirmCP");
			ivjConfirmCP.setLayout(new java.awt.GridBagLayout());
			ivjConfirmCP.setBounds(782, 894, 448, 240);

			java.awt.GridBagConstraints constraintsJLabel12 = new java.awt.GridBagConstraints();
			constraintsJLabel12.gridx = 0; constraintsJLabel12.gridy = 0;
			constraintsJLabel12.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel12.insets = new java.awt.Insets(5, 5, 5, 5);
			getConfirmCP().add(getJLabel12(), constraintsJLabel12);

			java.awt.GridBagConstraints constraintsConfirmPkgSP = new java.awt.GridBagConstraints();
			constraintsConfirmPkgSP.gridx = 0; constraintsConfirmPkgSP.gridy = 1;
			constraintsConfirmPkgSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsConfirmPkgSP.weightx = 1.0;
			constraintsConfirmPkgSP.weighty = 1.0;
			constraintsConfirmPkgSP.insets = new java.awt.Insets(0, 5, 0, 5);
			getConfirmCP().add(getConfirmPkgSP(), constraintsConfirmPkgSP);

			java.awt.GridBagConstraints constraintsConfirmBtnPnl = new java.awt.GridBagConstraints();
			constraintsConfirmBtnPnl.gridx = 0; constraintsConfirmBtnPnl.gridy = 2;
			constraintsConfirmBtnPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsConfirmBtnPnl.weightx = 1.0;
			constraintsConfirmBtnPnl.insets = new java.awt.Insets(5, 5, 5, 5);
			getConfirmCP().add(getConfirmBtnPnl(), constraintsConfirmBtnPnl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjConfirmCP;
}
/**
 * Return the ConfirmDlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getConfirmDlg() {
	if (ivjConfirmDlg == null) {
		try {
			ivjConfirmDlg = new javax.swing.JDialog(ownerContainer);
			ivjConfirmDlg.setName("ConfirmDlg2");
			ivjConfirmDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjConfirmDlg.setBounds(1064, 1443, 448, 240);
			ivjConfirmDlg.setModal(true);
			ivjConfirmDlg.setTitle("Select Package");
			ivjConfirmDlg.setContentPane(getConfirmCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjConfirmDlg;
}
/**
 * Return the ConfirmNewBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getConfirmNewBtn() {
	if (ivjConfirmNewBtn == null) {
		try {
			ivjConfirmNewBtn = new javax.swing.JButton();
			ivjConfirmNewBtn.setName("ConfirmNewBtn");
			ivjConfirmNewBtn.setText("Create New Package");
			ivjConfirmNewBtn.addActionListener(new BtnPressListener(getConfirmDlg()));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjConfirmNewBtn;
}
/**
 * Return the ConfirmOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getConfirmOkBtn() {
	if (ivjConfirmOkBtn == null) {
		try {
			ivjConfirmOkBtn = new javax.swing.JButton();
			ivjConfirmOkBtn.setName("ConfirmOkBtn");
			ivjConfirmOkBtn.setText("Add to selected");
			ivjConfirmOkBtn.addActionListener(new BtnPressListener(getConfirmDlg()));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjConfirmOkBtn;
}
/**
 * Return the ConfirmPkgSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getConfirmPkgSP() {
	if (ivjConfirmPkgSP == null) {
		try {
			ivjConfirmPkgSP = new javax.swing.JScrollPane();
			ivjConfirmPkgSP.setName("ConfirmPkgSP");
			ivjConfirmPkgSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjConfirmPkgSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getConfirmPkgSP().setViewportView(getConfirmPkgTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjConfirmPkgSP;
}
/**
 * Return the ConfirmPkgTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getConfirmPkgTbl() {
	if (ivjConfirmPkgTbl == null) {
		try {
			ivjConfirmPkgTbl = new javax.swing.JTable();
			ivjConfirmPkgTbl.setName("ConfirmPkgTbl");
			getConfirmPkgSP().setColumnHeaderView(ivjConfirmPkgTbl.getTableHeader());
			ivjConfirmPkgTbl.setBounds(0, 0, 200, 200);
			ivjConfirmPkgTbl.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjConfirmPkgTbl;
}
/**
 * This method initializes connectivityMI	
 * 	
 * @return javax.swing.JMenuItem	
 */    
private JMenuItem getConnectivityMI() {
	if (connectivityMI == null) {
		connectivityMI = new JMenuItem();
		connectivityMI.setText("Connectivity...");
		connectivityMI.addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try {
					cfg.changeConnectivity();
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}    
			}
		});
	}
	return connectivityMI;
}
/**
 * This method initializes createIconMI
 * 
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getCreateIconMI() {
	if(createIconMI == null) {
		createIconMI = new javax.swing.JMenuItem();
		createIconMI.setText("Create Icon...");
		createIconMI.setToolTipText("Create a Windows Desktop Icon");
		createIconMI.addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(DropBoxPnl.this,
									"Create a Dropbox icon on your desktop?",
									"Create Icon",
									JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE);

				if (response == JOptionPane.YES_OPTION) {
					// Get the start script to be used by the icon.
					File script = new File("startds.js");
					
					if (script.exists()) {
						// Current directory is the absolute path of the script
						// trimmed of the ending "\startds.js";
						String cd = script.getAbsolutePath();
						cd = cd.substring(0,cd.length()-11);

						try {
							// Create the JScript file to execute.
							FileWriter js = new FileWriter("dboxicon.js");
							js.write("try {\n");
							js.write("  var WSO = new ActiveXObject(\"WScript.shell\");\n");
							js.write("  var tenv = WSO.Environment(\"Process\");\n");
							js.write("  var desktoppath = WSO.SpecialFolders.Item(\"Desktop\");\n");
							js.write("  var link = WSO.CreateShortcut(desktoppath + \"\\\\ICC Dropbox.lnk\");\n");
							js.write("  link.WorkingDirectory = \"");

							// Need working directory to have \\ separaters.
							StringTokenizer t = new StringTokenizer(cd,"\\");
							js.write(t.nextToken());
							while (t.hasMoreTokens()) {
								js.write("\\\\");
								js.write(t.nextToken());
							}

							js.write("\";\n");
							js.write("  link.TargetPath = \"wscript.exe\";\n");
							js.write("  link.Arguments = \"startds.js -dropbox\";\n");
							js.write("  link.Description = \"Launch IBM Customer Connect Dropbox\";\n");
							js.write("  link.IconLocation = tenv(\"SystemRoot\") + \"\\\\system32\\\\SHELL32.dll, 12\";\n");
							js.write("  link.WindowStyle = \"1\";\n");
							js.write("  link.Save();\n");
							js.write("} catch (e) {\n");
							js.write("  WScript.Echo(\"Error encountered while creating desktop shortcut.\\nUnexpected error: \" + e.message);\n");
							js.write("  WScript.QUIT(10);\n");
							js.write("}\n");
							js.write("WScript.Echo(\"Dropbox icon added to your desktop!\");\n");
							js.write("WScript.QUIT(0);\n");
							js.close();
	
							// Create the icon.
							Process p = Runtime.getRuntime().exec("wscript dboxicon.js");
						}
						catch (IOException e1) {
							System.out.println(e1.getMessage());
							JOptionPane.showMessageDialog(DropBoxPnl.this,
												"An error occurred while preparing to create icon.",
												"Error",
												JOptionPane.ERROR_MESSAGE);
						}
					}
					else {
						JOptionPane.showMessageDialog(DropBoxPnl.this,
											"A required script, startds.js, is missing.",
											"Error",
											JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}
	return createIconMI;
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
			ivjDebugMI.setMnemonic('d');
			ivjDebugMI.setText("Debug");
			ivjDebugMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					try {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							((ProxyDebugInterface) dboxAccess).enableDebug(ProxyDebugInterface.FULL);
						}
						else {
							((ProxyDebugInterface) dboxAccess).enableDebug(ProxyDebugInterface.NAMES);
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
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDownConfCanBtn() {
	if (ivjDownConfCanBtn == null) {
		try {
			ivjDownConfCanBtn = new javax.swing.JButton();
			ivjDownConfCanBtn.setName("DownConfCanBtn");
			ivjDownConfCanBtn.setText("Cancel");
			ivjDownConfCanBtn.addActionListener(downConfListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownConfCanBtn;
}
/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getDownConfCB() {
	if (ivjDownConfCB == null) {
		try {
			ivjDownConfCB = new javax.swing.JCheckBox();
			ivjDownConfCB.setName("DownConfCB");
			ivjDownConfCB.setText("Do not over-write existing files");
			ivjDownConfCB.addItemListener(downConfListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownConfCB;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDownConfChgBtn() {
	if (ivjDownConfChgBtn == null) {
		try {
			ivjDownConfChgBtn = new javax.swing.JButton();
			ivjDownConfChgBtn.setName("DownConfChgBtn");
			ivjDownConfChgBtn.setText("Browse");
			ivjDownConfChgBtn.addActionListener(downConfListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownConfChgBtn;
}
/**
 * Return the DownConfInfoBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDownConfInfoBtn() {
	if (ivjDownConfInfoBtn == null) {
		try {
			ivjDownConfInfoBtn = new javax.swing.JButton();
			ivjDownConfInfoBtn.setName("DownConfInfoBtn");
			ivjDownConfInfoBtn.setText("More Info");
			ivjDownConfInfoBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						moreInfo();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}	    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownConfInfoBtn;
}
/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getDownConfirmCP() {
	if (ivjDownConfirmCP == null) {
		try {
			ivjDownConfirmCP = new javax.swing.JPanel();
			ivjDownConfirmCP.setName("DownConfirmCP");
			ivjDownConfirmCP.setLayout(new java.awt.GridBagLayout());
			ivjDownConfirmCP.setBounds(57, 2434, 390, 187);

			java.awt.GridBagConstraints constraintsDownConfTF = new java.awt.GridBagConstraints();
			constraintsDownConfTF.gridx = 0; constraintsDownConfTF.gridy = 1;
			constraintsDownConfTF.gridwidth = 0;
			constraintsDownConfTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDownConfTF.weightx = 1.0;
			constraintsDownConfTF.insets = new java.awt.Insets(2, 5, 5, 5);
			getDownConfirmCP().add(getDownConfTF(), constraintsDownConfTF);

			java.awt.GridBagConstraints constraintsDownConfLbl = new java.awt.GridBagConstraints();
			constraintsDownConfLbl.gridx = 0; constraintsDownConfLbl.gridy = 0;
			constraintsDownConfLbl.anchor = java.awt.GridBagConstraints.SOUTHWEST;
			constraintsDownConfLbl.weightx = 1.0;
			constraintsDownConfLbl.insets = new java.awt.Insets(5, 5, 0, 5);
			getDownConfirmCP().add(getDownConfLbl(), constraintsDownConfLbl);

			java.awt.GridBagConstraints constraintsDownConfChgBtn = new java.awt.GridBagConstraints();
			constraintsDownConfChgBtn.gridx = 1; constraintsDownConfChgBtn.gridy = 0;
			constraintsDownConfChgBtn.insets = new java.awt.Insets(5, 0, 0, 5);
			getDownConfirmCP().add(getDownConfChgBtn(), constraintsDownConfChgBtn);

			java.awt.GridBagConstraints constraintsJPanel5 = new java.awt.GridBagConstraints();
			constraintsJPanel5.gridx = 0; constraintsJPanel5.gridy = 4;
			constraintsJPanel5.gridwidth = 0;
			constraintsJPanel5.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel5.insets = new java.awt.Insets(0, 5, 5, 5);
			getDownConfirmCP().add(getJPanel5(), constraintsJPanel5);

			java.awt.GridBagConstraints constraintsDownConfCB = new java.awt.GridBagConstraints();
			constraintsDownConfCB.gridx = 0; constraintsDownConfCB.gridy = 3;
			constraintsDownConfCB.gridwidth = 0;
			constraintsDownConfCB.anchor = java.awt.GridBagConstraints.SOUTHWEST;
			constraintsDownConfCB.weighty = 1.0;
			constraintsDownConfCB.insets = new java.awt.Insets(0, 5, 5, 5);
			getDownConfirmCP().add(getDownConfCB(), constraintsDownConfCB);

			java.awt.GridBagConstraints constraintsDownConfWarnLbl = new java.awt.GridBagConstraints();
			constraintsDownConfWarnLbl.gridx = 0; constraintsDownConfWarnLbl.gridy = 2;
			constraintsDownConfWarnLbl.gridwidth = 0;
			constraintsDownConfWarnLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDownConfWarnLbl.insets = new java.awt.Insets(5, 5, 5, 5);
			getDownConfirmCP().add(getDownConfWarnLbl(), constraintsDownConfWarnLbl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownConfirmCP;
}
/**
 * Return the DownConfirmDlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getDownConfirmDlg() {
	if (ivjDownConfirmDlg == null) {
		try {
			ivjDownConfirmDlg = new javax.swing.JDialog(ownerContainer);
			ivjDownConfirmDlg.setName("DownConfirmDlg2");
			ivjDownConfirmDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjDownConfirmDlg.setBounds(652, 1973, 450, 200);
			ivjDownConfirmDlg.setModal(true);
			ivjDownConfirmDlg.setTitle("Confirm download");
			ivjDownConfirmDlg.setContentPane(getDownConfirmCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownConfirmDlg;
}
/**
 * Return the JLabel10 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getDownConfLbl() {
	if (ivjDownConfLbl == null) {
		try {
			ivjDownConfLbl = new javax.swing.JLabel();
			ivjDownConfLbl.setName("DownConfLbl");
			ivjDownConfLbl.setText("Prompt label?");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownConfLbl;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDownConfOkBtn() {
	if (ivjDownConfOkBtn == null) {
		try {
			ivjDownConfOkBtn = new javax.swing.JButton();
			ivjDownConfOkBtn.setName("DownConfOkBtn");
			ivjDownConfOkBtn.setText("Ok");
			ivjDownConfOkBtn.addActionListener(new BtnPressListener(getDownConfirmDlg()));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownConfOkBtn;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getDownConfTF() {
	if (ivjDownConfTF == null) {
		try {
			ivjDownConfTF = new javax.swing.JTextField();
			ivjDownConfTF.setName("DownConfTF");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownConfTF;
}
/**
 * Return the JLabel10 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getDownConfWarnLbl() {
	if (ivjDownConfWarnLbl == null) {
		try {
			ivjDownConfWarnLbl = new javax.swing.JLabel();
			ivjDownConfWarnLbl.setName("DownConfWarnLbl");
			ivjDownConfWarnLbl.setText("JLabel10");
			ivjDownConfWarnLbl.setForeground(new java.awt.Color(255,0,0));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownConfWarnLbl;
}
/**
 * Return the DownInfoCP property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getDownInfoCP() {
	if (ivjDownInfoCP == null) {
		try {
			ivjDownInfoCP = new javax.swing.JPanel();
			ivjDownInfoCP.setName("DownInfoCP");
			ivjDownInfoCP.setLayout(new java.awt.GridBagLayout());
			ivjDownInfoCP.setBounds(618, 2445, 303, 181);

			java.awt.GridBagConstraints constraintsDownInfoOkBtn = new java.awt.GridBagConstraints();
			constraintsDownInfoOkBtn.gridx = 0; constraintsDownInfoOkBtn.gridy = 1;
			constraintsDownInfoOkBtn.insets = new java.awt.Insets(5, 5, 5, 5);
			getDownInfoCP().add(getDownInfoOkBtn(), constraintsDownInfoOkBtn);

			java.awt.GridBagConstraints constraintsDownInfoTA = new java.awt.GridBagConstraints();
			constraintsDownInfoTA.gridx = 0; constraintsDownInfoTA.gridy = 0;
			constraintsDownInfoTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDownInfoTA.weightx = 1.0;
			constraintsDownInfoTA.weighty = 1.0;
			constraintsDownInfoTA.insets = new java.awt.Insets(5, 5, 0, 5);
			getDownInfoCP().add(getDownInfoTA(), constraintsDownInfoTA);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownInfoCP;
}
/**
 * Return the DownInfoDlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getDownInfoDlg() {
	if (ivjDownInfoDlg == null) {
		try {
			ivjDownInfoDlg = new javax.swing.JDialog(getDownConfirmDlg());
			ivjDownInfoDlg.setName("DownInfo1Dlg");
			ivjDownInfoDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjDownInfoDlg.setBounds(781, 2537, 300, 300);
			ivjDownInfoDlg.setModal(true);
			ivjDownInfoDlg.setTitle("Download Information");
			getDownInfoDlg().setContentPane(getDownInfoCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownInfoDlg;
}
/**
 * Return the DownInfoOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDownInfoOkBtn() {
	if (ivjDownInfoOkBtn == null) {
		try {
			ivjDownInfoOkBtn = new javax.swing.JButton();
			ivjDownInfoOkBtn.setName("DownInfoOkBtn");
			ivjDownInfoOkBtn.setText("Close");
			ivjDownInfoOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getDownInfoDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownInfoOkBtn;
}
/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getDownInfoTA() {
	if (ivjDownInfoTA == null) {
		try {
			ivjDownInfoTA = new javax.swing.JTextArea();
			ivjDownInfoTA.setName("DownInfoTA");
			ivjDownInfoTA.setLineWrap(true);
			ivjDownInfoTA.setWrapStyleWord(true);
			ivjDownInfoTA.setText("Files will be downloaded to the current directory. Relative paths will be maintained. For existing files, an attempt to resume the download will be made. If resumption is not possible, the file will be over-written. Check the box below to prevent the over-write of an existing file.");
			ivjDownInfoTA.setBackground(new java.awt.Color(204,204,204));
			ivjDownInfoTA.setEditable(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDownInfoTA;
}
/**
 * Return the DropBoxJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
public javax.swing.JMenuBar getDropBoxJMenuBar() {
	if (ivjDropBoxJMenuBar == null) {
		try {
			ivjDropBoxJMenuBar = new javax.swing.JMenuBar();
			ivjDropBoxJMenuBar.setName("DropBoxJMenuBar");
			ivjDropBoxJMenuBar.add(getFileM());
			ivjDropBoxJMenuBar.add(getLocalM());
			ivjDropBoxJMenuBar.add(getRemoteM());
			ivjDropBoxJMenuBar.add(getStatusM());
			ivjDropBoxJMenuBar.add(getHelpM());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDropBoxJMenuBar;
}
/**
 * Return the DropboxOptions property value.
 * @return oem.edge.ed.odc.dropbox.client.DropboxOptions
 */
private DropboxOptions getDropboxOptions() {
	if (ivjDropboxOptions == null) {
		try {
			ivjDropboxOptions = new DropboxOptions(ownerContainer);
			ivjDropboxOptions.setName("DropboxOptions");
			ivjDropboxOptions.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjDropboxOptions.setTitle("");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDropboxOptions;
}
/**
 * Return the JButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getEditAddBtn() {
	if (ivjEditAddBtn == null) {
		try {
			ivjEditAddBtn = new javax.swing.JButton();
			ivjEditAddBtn.setName("EditAddBtn");
			ivjEditAddBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/upload.gif")));
			ivjEditAddBtn.setToolTipText("Add selected user to \'To users\' list");
			ivjEditAddBtn.setText("");
			ivjEditAddBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjEditAddBtn.addActionListener(pkgEditListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditAddBtn;
}
/**
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getEditBtnPnl() {
	if (ivjEditBtnPnl == null) {
		try {
			ivjEditBtnPnl = new javax.swing.JPanel();
			ivjEditBtnPnl.setName("EditBtnPnl");
			ivjEditBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsUserDelBtn = new java.awt.GridBagConstraints();
			constraintsUserDelBtn.gridx = 0; constraintsUserDelBtn.gridy = 2;
			constraintsUserDelBtn.insets = new java.awt.Insets(5, 0, 2, 0);
			getEditBtnPnl().add(getUserDelBtn(), constraintsUserDelBtn);

			java.awt.GridBagConstraints constraintsEditAddBtn = new java.awt.GridBagConstraints();
			constraintsEditAddBtn.gridx = 0; constraintsEditAddBtn.gridy = 0;
			constraintsEditAddBtn.insets = new java.awt.Insets(0, 0, 2, 0);
			getEditBtnPnl().add(getEditAddBtn(), constraintsEditAddBtn);

			java.awt.GridBagConstraints constraintsEditRemBtn = new java.awt.GridBagConstraints();
			constraintsEditRemBtn.gridx = 0; constraintsEditRemBtn.gridy = 1;
			constraintsEditRemBtn.insets = new java.awt.Insets(2, 0, 5, 0);
			getEditBtnPnl().add(getEditRemBtn(), constraintsEditRemBtn);

			java.awt.GridBagConstraints constraintsGrpQueryBtn = new java.awt.GridBagConstraints();
			constraintsGrpQueryBtn.gridx = 0; constraintsGrpQueryBtn.gridy = 3;
			constraintsGrpQueryBtn.insets = new java.awt.Insets(2, 0, 0, 0);
			getEditBtnPnl().add(getGrpQueryBtn(), constraintsGrpQueryBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditBtnPnl;
}
/**
 * Return the UserFromLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getEditFromLB() {
	if (ivjEditFromLB == null) {
		try {
			ivjEditFromLB = new javax.swing.JList();
			ivjEditFromLB.setName("EditFromLB");
			ivjEditFromLB.setToolTipText("List of saved users");
			ivjEditFromLB.setBounds(0, 0, 160, 120);
			ivjEditFromLB.addListSelectionListener(pkgEditListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditFromLB;
}
/**
 * Return the UserFromLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getEditFromLM() {
	if (ivjEditFromLM == null) {
		try {
			ivjEditFromLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditFromLM;
}
/**
 * Return the UserFromSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getEditFromSP() {
	if (ivjEditFromSP == null) {
		try {
			ivjEditFromSP = new javax.swing.JScrollPane();
			ivjEditFromSP.setName("EditFromSP");
			ivjEditFromSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getEditFromSP().setViewportView(getEditFromLB());
			ivjEditFromSP.setPreferredSize(new java.awt.Dimension(104,131));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditFromSP;
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getEditRemBtn() {
	if (ivjEditRemBtn == null) {
		try {
			ivjEditRemBtn = new javax.swing.JButton();
			ivjEditRemBtn.setName("EditRemBtn");
			ivjEditRemBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/download.gif")));
			ivjEditRemBtn.setToolTipText("Remove user from \'To users\' list");
			ivjEditRemBtn.setText("");
			ivjEditRemBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjEditRemBtn.addActionListener(pkgEditListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditRemBtn;
}
/**
 * Return the UserToLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getEditToLB() {
	if (ivjEditToLB == null) {
		try {
			ivjEditToLB = new javax.swing.JList();
			ivjEditToLB.setName("EditToLB");
			ivjEditToLB.setBounds(0, 0, 160, 120);
			ivjEditToLB.addListSelectionListener(pkgEditListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditToLB;
}
/**
 * Return the UserToLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getEditToLM() {
	if (ivjEditToLM == null) {
		try {
			ivjEditToLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditToLM;
}
/**
 * Return the UserToSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getEditToSP() {
	if (ivjEditToSP == null) {
		try {
			ivjEditToSP = new javax.swing.JScrollPane();
			ivjEditToSP.setName("EditToSP");
			ivjEditToSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getEditToSP().setViewportView(getEditToLB());
			ivjEditToSP.setPreferredSize(new java.awt.Dimension(104,131));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditToSP;
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
			ivjExitMI.setMnemonic('e');
			ivjExitMI.setText("Exit");
			ivjExitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,InputEvent.CTRL_MASK,false));
			ivjExitMI.addActionListener(exitListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjExitMI;
}
/**
 * This method initializes pkgDescLbl
 * 
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getExpirationLbl() {
	if(expirationLbl == null) {
		expirationLbl = new javax.swing.JLabel();
		expirationLbl.setText("JLabel");
	}
	return expirationLbl;
}
/**
 * This method initializes jButton
 * 
 * @return javax.swing.JButton
 */
private javax.swing.JButton getExpirationBtn() {
	if(expirationBtn == null) {
		expirationBtn = new javax.swing.JButton();
		expirationBtn.setText("");
		expirationBtn.setMargin(new java.awt.Insets(0,0,0,0));
		expirationBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/calendar.gif")));
		expirationBtn.setToolTipText("Calendar");
		expirationBtn.setMinimumSize(new java.awt.Dimension(20,20));
		expirationBtn.setPreferredSize(new java.awt.Dimension(20,20));
		expirationBtn.setMaximumSize(new java.awt.Dimension(20,20));
		expirationBtn.addActionListener(expirationListener);
	}
	return expirationBtn;
}
/**
 * Return the JPanel6 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getExpirePnl() {
	if (ivjExpirePnl == null) {
		try {
			ivjExpirePnl = new javax.swing.JPanel();
			ivjExpirePnl.setName("ExpirePnl");
			ivjExpirePnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel7 = new java.awt.GridBagConstraints();
			constraintsJLabel7.gridx = 2; constraintsJLabel7.gridy = 0;
 			java.awt.GridBagConstraints consGridBagConstraints9 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints10 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints12 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints21 = new java.awt.GridBagConstraints();
			consGridBagConstraints12.gridy = 0;
			consGridBagConstraints12.gridx = 3;
			consGridBagConstraints12.insets = new java.awt.Insets(0,5,0,3);
			consGridBagConstraints21.gridy = 0;
			consGridBagConstraints21.gridx = 4;
			consGridBagConstraints21.weightx = 1.0D;
			consGridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints10.weightx = 0.0D;
			consGridBagConstraints10.gridy = 0;
			consGridBagConstraints10.gridx = 6;
			consGridBagConstraints10.insets = new java.awt.Insets(0,5,0,0);
			consGridBagConstraints9.gridy = 0;
			consGridBagConstraints9.gridx = 5;
 			ivjExpirePnl.add(getJLabel7(), constraintsJLabel7);
			ivjExpirePnl.add(getStoragePoolLbl(), consGridBagConstraints9);
			ivjExpirePnl.add(getStoragePoolCB(), consGridBagConstraints10);
			ivjExpirePnl.add(getExpirationLbl(), consGridBagConstraints12);
			ivjExpirePnl.add(getExpirationBtn(), consGridBagConstraints21);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjExpirePnl;
}
/**
 * Return the ExpressCB property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getExpressCB() {
	if (ivjExpressCB == null) {
		try {
			ivjExpressCB = new javax.swing.JCheckBox();
			ivjExpressCB.setName("ExpressCB");
			ivjExpressCB.setToolTipText("Simplifies delivery of single files to recipients");
			ivjExpressCB.setText("Automatically deliver package when upload completes");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjExpressCB;
}
/**
 * Return the FileAclViewBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getFileAclViewBtn() {
	if (ivjFileAclViewBtn == null) {
		try {
			ivjFileAclViewBtn = new javax.swing.JButton();
			ivjFileAclViewBtn.setName("FileAclViewBtn");
			ivjFileAclViewBtn.setToolTipText("Close this window");
			ivjFileAclViewBtn.setText("Close");
			ivjFileAclViewBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getFileAclViewDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}   
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileAclViewBtn;
}
/**
 * Return the JDialogContentPane3 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getFileAclViewCP() {
	if (ivjFileAclViewCP == null) {
		try {
			ivjFileAclViewCP = new javax.swing.JPanel();
			ivjFileAclViewCP.setName("FileAclViewCP");
			ivjFileAclViewCP.setLayout(new java.awt.GridBagLayout());
			ivjFileAclViewCP.setBounds(73, 1619, 350, 238);

			java.awt.GridBagConstraints constraintsFileAclViewBtn = new java.awt.GridBagConstraints();
			constraintsFileAclViewBtn.gridx = 0; constraintsFileAclViewBtn.gridy = 2;
			constraintsFileAclViewBtn.gridwidth = 0;
			constraintsFileAclViewBtn.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileAclViewCP().add(getFileAclViewBtn(), constraintsFileAclViewBtn);

			java.awt.GridBagConstraints constraintsFileAclViewSP = new java.awt.GridBagConstraints();
			constraintsFileAclViewSP.gridx = 0; constraintsFileAclViewSP.gridy = 1;
			constraintsFileAclViewSP.gridwidth = 0;
			constraintsFileAclViewSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsFileAclViewSP.weightx = 1.0;
			constraintsFileAclViewSP.weighty = 1.0;
			constraintsFileAclViewSP.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileAclViewCP().add(getFileAclViewSP(), constraintsFileAclViewSP);

			java.awt.GridBagConstraints constraintsFileAclViewLbl = new java.awt.GridBagConstraints();
			constraintsFileAclViewLbl.gridx = 0; constraintsFileAclViewLbl.gridy = 0;
			constraintsFileAclViewLbl.insets = new java.awt.Insets(5, 5, 0, 4);
			getFileAclViewCP().add(getFileAclViewLbl(), constraintsFileAclViewLbl);

			java.awt.GridBagConstraints constraintsFileAclViewNameLbl = new java.awt.GridBagConstraints();
			constraintsFileAclViewNameLbl.gridx = 1; constraintsFileAclViewNameLbl.gridy = 0;
			constraintsFileAclViewNameLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFileAclViewNameLbl.insets = new java.awt.Insets(5, 0, 0, 5);
			getFileAclViewCP().add(getFileAclViewNameLbl(), constraintsFileAclViewNameLbl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileAclViewCP;
}
/**
 * Return the FileAclViewDlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getFileAclViewDlg() {
	if (ivjFileAclViewDlg == null) {
		try {
			ivjFileAclViewDlg = new javax.swing.JDialog(ownerContainer);
			ivjFileAclViewDlg.setName("FileAclViewDlg2");
			ivjFileAclViewDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjFileAclViewDlg.setBounds(1051, 1338, 350, 238);
			ivjFileAclViewDlg.setModal(false);
			ivjFileAclViewDlg.setTitle("File Access Information");
			ivjFileAclViewDlg.setContentPane(getFileAclViewCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileAclViewDlg;
}
/**
 * Return the FileAclViewLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getFileAclViewLbl() {
	if (ivjFileAclViewLbl == null) {
		try {
			ivjFileAclViewLbl = new javax.swing.JLabel();
			ivjFileAclViewLbl.setName("FileAclViewLbl");
			ivjFileAclViewLbl.setText("Access for:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileAclViewLbl;
}
/**
 * Return the FileAclViewNameLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getFileAclViewNameLbl() {
	if (ivjFileAclViewNameLbl == null) {
		try {
			ivjFileAclViewNameLbl = new javax.swing.JLabel();
			ivjFileAclViewNameLbl.setName("FileAclViewNameLbl");
			ivjFileAclViewNameLbl.setText("JLabel6");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileAclViewNameLbl;
}
/**
 * Return the FileAclViewSortTM property value.
 * @return TableSorter
 */
private TableSorter getFileAclViewSortTM() {
	if (ivjFileAclViewSortTM == null) {
		try {
			ivjFileAclViewSortTM = new TableSorter();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileAclViewSortTM;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getFileAclViewSP() {
	if (ivjFileAclViewSP == null) {
		try {
			ivjFileAclViewSP = new javax.swing.JScrollPane();
			ivjFileAclViewSP.setName("FileAclViewSP");
			ivjFileAclViewSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjFileAclViewSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getFileAclViewSP().setViewportView(getFileAclViewTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileAclViewSP;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getFileAclViewTbl() {
	if (ivjFileAclViewTbl == null) {
		try {
			ivjFileAclViewTbl = new javax.swing.JTable();
			ivjFileAclViewTbl.setName("FileAclViewTbl");
			getFileAclViewSP().setColumnHeaderView(ivjFileAclViewTbl.getTableHeader());
			ivjFileAclViewTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjFileAclViewTbl.setBounds(0, 0, 200, 200);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileAclViewTbl;
}
/**
 * Return the FileAclViewTM property value.
 * @return oem.edge.ed.odc.dropbox.client.FileAclsTM
 */
private FileAclsTM getFileAclViewTM() {
	if (ivjFileAclViewTM == null) {
		try {
			ivjFileAclViewTM = new FileAclsTM();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileAclViewTM;
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
			ivjFileM.add(getConnectivityMI());
			ivjFileM.add(new JSeparator());
			ivjFileM.add(getOptionsMI());
			ivjFileM.add(getGroupMI());
			ivjFileM.add(getItarMI());
			ivjFileM.add(getCreateIconMI());
			ivjFileM.add(new JSeparator());
			ivjFileM.add(getMsgHandlerMI());
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
 * Return the FilesDeleteMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getFilesDeleteMI() {
	if (ivjFilesDeleteMI == null) {
		try {
			ivjFilesDeleteMI = new javax.swing.JMenuItem();
			ivjFilesDeleteMI.setName("FilesDeleteMI");
			ivjFilesDeleteMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/trash.gif")));
			ivjFilesDeleteMI.setMnemonic('l');
			ivjFilesDeleteMI.setText("Delete");
			ivjFilesDeleteMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,InputEvent.ALT_MASK,false));
			ivjFilesDeleteMI.addActionListener(deleteListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFilesDeleteMI;
}
/**
 * Return the FilesDownMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getFilesDownMI() {
	if (ivjFilesDownMI == null) {
		try {
			ivjFilesDownMI = new javax.swing.JMenuItem();
			ivjFilesDownMI.setName("FilesDownMI");
			ivjFilesDownMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/download.gif")));
			ivjFilesDownMI.setMnemonic('d');
			ivjFilesDownMI.setText("Download");
			ivjFilesDownMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.ALT_MASK,false));
			ivjFilesDownMI.addActionListener(downloadFileHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFilesDownMI;
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
 * Return the FilesFwdMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getFilesFwdMI() {
	if (ivjFilesFwdMI == null) {
		try {
			ivjFilesFwdMI = new javax.swing.JMenuItem();
			ivjFilesFwdMI.setName("FilesFwdMI");
			ivjFilesFwdMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/fwdfile.gif")));
			ivjFilesFwdMI.setMnemonic('f');
			ivjFilesFwdMI.setText("Forward");
			ivjFilesFwdMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.ALT_MASK,false));
			ivjFilesFwdMI.addActionListener(forwardFilesActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFilesFwdMI;
}
/**
 * Return the FilesM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getFilesM() {
	if (ivjFilesM == null) {
		try {
			ivjFilesM = new javax.swing.JMenu();
			ivjFilesM.setName("FilesM");
			ivjFilesM.setMnemonic('f');
			ivjFilesM.setText("Files");
			ivjFilesM.add(getFilesDownMI());
			ivjFilesM.add(getFilesFwdMI());
			ivjFilesM.add(getJSeparator6());
			ivjFilesM.add(getFilesViewMI());
			ivjFilesM.add(getJSeparator7());
			ivjFilesM.add(getFilesRestartMI());
			ivjFilesM.add(getFilesDeleteMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFilesM;
}
/**
 * Return the FilesRestartMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getFilesRestartMI() {
	if (ivjFilesRestartMI == null) {
		try {
			ivjFilesRestartMI = new javax.swing.JMenuItem();
			ivjFilesRestartMI.setName("FilesRestartMI");
			ivjFilesRestartMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/uprestrt.gif")));
			ivjFilesRestartMI.setMnemonic('r');
			ivjFilesRestartMI.setText("Restart");
			ivjFilesRestartMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,InputEvent.ALT_MASK,false));
			ivjFilesRestartMI.addActionListener(restartListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFilesRestartMI;
}
/**
 * Return the FileStatusTM property value.
 * @return oem.edge.ed.odc.dropbox.client.FileStatusTableModel
 */
private FileStatusTableModel getFileStatusTM() {
	if (ivjFileStatusTM == null) {
		try {
			ivjFileStatusTM = new FileStatusTableModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileStatusTM;
}
/**
 * Return the FilesViewMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getFilesViewMI() {
	if (ivjFilesViewMI == null) {
		try {
			ivjFilesViewMI = new javax.swing.JMenuItem();
			ivjFilesViewMI.setName("FilesViewMI");
			ivjFilesViewMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/propview.gif")));
			ivjFilesViewMI.setMnemonic('v');
			ivjFilesViewMI.setText("View recipients");
			ivjFilesViewMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.ALT_MASK,false));
			ivjFilesViewMI.addActionListener(fileAclListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFilesViewMI;
}
/**
 * Return the FilterLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getFilterLbl() {
	if (ivjFilterLbl == null) {
		try {
			ivjFilterLbl = new javax.swing.JLabel();
			ivjFilterLbl.setName("FilterLbl");
			ivjFilterLbl.setToolTipText("Marked and completed packages are not shown");
			ivjFilterLbl.setText("(filtered)");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFilterLbl;
}
/**
 * Return the JMenuItem2 property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getGroupMI() {
	if (ivjGroupMI == null) {
		try {
			ivjGroupMI = new javax.swing.JMenuItem();
			ivjGroupMI.setName("GroupMI");
			ivjGroupMI.setText("Groups...");
			ivjGroupMI.setActionCommand("Groups");
			ivjGroupMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getManageGroups().doGroups(ownerContainer);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGroupMI;
}
/**
 * Return the GrpQueryBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getGrpQueryBtn() {
	if (ivjGrpQueryBtn == null) {
		try {
			ivjGrpQueryBtn = new javax.swing.JButton();
			ivjGrpQueryBtn.setName("GrpQueryBtn");
			ivjGrpQueryBtn.setToolTipText("View information about selected group");
			ivjGrpQueryBtn.setText("");
			ivjGrpQueryBtn.setForeground(java.awt.Color.black);
			ivjGrpQueryBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/query.gif")));
			ivjGrpQueryBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjGrpQueryBtn.addActionListener(queryGrpListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryBtn;
}
/**
 * Return the GrpQueryCompanyLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryCompanyLbl() {
	if (ivjGrpQueryCompanyLbl == null) {
		try {
			ivjGrpQueryCompanyLbl = new javax.swing.JLabel();
			ivjGrpQueryCompanyLbl.setName("GrpQueryCompanyLbl");
			ivjGrpQueryCompanyLbl.setText("JLabel15");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryCompanyLbl;
}
/**
 * Return the JDialogContentPane5 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getGrpQueryCP() {
	if (ivjGrpQueryCP == null) {
		try {
			ivjGrpQueryCP = new javax.swing.JPanel();
			ivjGrpQueryCP.setName("GrpQueryCP");
			ivjGrpQueryCP.setLayout(new java.awt.GridBagLayout());
			ivjGrpQueryCP.setBounds(622, 1990, 353, 347);

			java.awt.GridBagConstraints constraintsGrpQueryLbl1 = new java.awt.GridBagConstraints();
			constraintsGrpQueryLbl1.gridx = 0; constraintsGrpQueryLbl1.gridy = 0;
			constraintsGrpQueryLbl1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryLbl1.insets = new java.awt.Insets(5, 5, 0, 4);
			getGrpQueryCP().add(getGrpQueryLbl1(), constraintsGrpQueryLbl1);

			java.awt.GridBagConstraints constraintsGrpQueryNameLbl = new java.awt.GridBagConstraints();
			constraintsGrpQueryNameLbl.gridx = 1; constraintsGrpQueryNameLbl.gridy = 0;
			constraintsGrpQueryNameLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryNameLbl.insets = new java.awt.Insets(5, 0, 0, 5);
			getGrpQueryCP().add(getGrpQueryNameLbl(), constraintsGrpQueryNameLbl);

			java.awt.GridBagConstraints constraintsGrpQueryLbl2 = new java.awt.GridBagConstraints();
			constraintsGrpQueryLbl2.gridx = 0; constraintsGrpQueryLbl2.gridy = 1;
			constraintsGrpQueryLbl2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryLbl2.insets = new java.awt.Insets(5, 5, 0, 4);
			getGrpQueryCP().add(getGrpQueryLbl2(), constraintsGrpQueryLbl2);

			java.awt.GridBagConstraints constraintsGrpQueryOwnerLbl = new java.awt.GridBagConstraints();
			constraintsGrpQueryOwnerLbl.gridx = 1; constraintsGrpQueryOwnerLbl.gridy = 1;
			constraintsGrpQueryOwnerLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryOwnerLbl.insets = new java.awt.Insets(5, 0, 0, 5);
			getGrpQueryCP().add(getGrpQueryOwnerLbl(), constraintsGrpQueryOwnerLbl);

			java.awt.GridBagConstraints constraintsGrpQueryLbl3 = new java.awt.GridBagConstraints();
			constraintsGrpQueryLbl3.gridx = 0; constraintsGrpQueryLbl3.gridy = 2;
			constraintsGrpQueryLbl3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryLbl3.insets = new java.awt.Insets(5, 5, 0, 4);
			getGrpQueryCP().add(getGrpQueryLbl3(), constraintsGrpQueryLbl3);

			java.awt.GridBagConstraints constraintsGrpQueryCompanyLbl = new java.awt.GridBagConstraints();
			constraintsGrpQueryCompanyLbl.gridx = 1; constraintsGrpQueryCompanyLbl.gridy = 2;
			constraintsGrpQueryCompanyLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryCompanyLbl.insets = new java.awt.Insets(5, 0, 0, 5);
			getGrpQueryCP().add(getGrpQueryCompanyLbl(), constraintsGrpQueryCompanyLbl);

			java.awt.GridBagConstraints constraintsGrpQuerySP = new java.awt.GridBagConstraints();
			constraintsGrpQuerySP.gridx = 0; constraintsGrpQuerySP.gridy = 3;
			constraintsGrpQuerySP.gridwidth = 0;
			constraintsGrpQuerySP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsGrpQuerySP.weightx = 1.0;
			constraintsGrpQuerySP.weighty = 1.0;
			constraintsGrpQuerySP.insets = new java.awt.Insets(5, 5, 5, 5);
			getGrpQueryCP().add(getGrpQuerySP(), constraintsGrpQuerySP);

			java.awt.GridBagConstraints constraintsGrpQueryOkBtn = new java.awt.GridBagConstraints();
			constraintsGrpQueryOkBtn.gridx = 0; constraintsGrpQueryOkBtn.gridy = 4;
			constraintsGrpQueryOkBtn.gridwidth = 0;
			constraintsGrpQueryOkBtn.insets = new java.awt.Insets(0, 5, 5, 5);
			getGrpQueryCP().add(getGrpQueryOkBtn(), constraintsGrpQueryOkBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryCP;
}
/**
 * Return the GrpQueryDlg3 property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getGrpQueryDlg() {
	if (ivjGrpQueryDlg == null) {
		try {
			ivjGrpQueryDlg = new javax.swing.JDialog(getPkgEditDlg());
			ivjGrpQueryDlg.setName("GrpQueryDlg2");
			ivjGrpQueryDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjGrpQueryDlg.setBounds(977, 1918, 353, 347);
			ivjGrpQueryDlg.setTitle("Group Member List");
			ivjGrpQueryDlg.setContentPane(getGrpQueryCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryDlg;
}
/**
 * Return the GrpQueryLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getGrpQueryLB() {
	if (ivjGrpQueryLB == null) {
		try {
			ivjGrpQueryLB = new javax.swing.JList();
			ivjGrpQueryLB.setName("GrpQueryLB");
			ivjGrpQueryLB.setBounds(0, 0, 160, 120);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryLB;
}
/**
 * Return the GrpQueryLbl1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryLbl1() {
	if (ivjGrpQueryLbl1 == null) {
		try {
			ivjGrpQueryLbl1 = new javax.swing.JLabel();
			ivjGrpQueryLbl1.setName("GrpQueryLbl1");
			ivjGrpQueryLbl1.setText("Group:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryLbl1;
}
/**
 * Return the GrpQueryLbl2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryLbl2() {
	if (ivjGrpQueryLbl2 == null) {
		try {
			ivjGrpQueryLbl2 = new javax.swing.JLabel();
			ivjGrpQueryLbl2.setName("GrpQueryLbl2");
			ivjGrpQueryLbl2.setText("Owner:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryLbl2;
}
/**
 * Return the GrpQueryLbl3 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryLbl3() {
	if (ivjGrpQueryLbl3 == null) {
		try {
			ivjGrpQueryLbl3 = new javax.swing.JLabel();
			ivjGrpQueryLbl3.setName("GrpQueryLbl3");
			ivjGrpQueryLbl3.setText("Company:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryLbl3;
}
/**
 * Return the GrpQueryLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getGrpQueryLM() {
	if (ivjGrpQueryLM == null) {
		try {
			ivjGrpQueryLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryLM;
}
/**
 * Return the GrpQueryNameLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryNameLbl() {
	if (ivjGrpQueryNameLbl == null) {
		try {
			ivjGrpQueryNameLbl = new javax.swing.JLabel();
			ivjGrpQueryNameLbl.setName("GrpQueryNameLbl");
			ivjGrpQueryNameLbl.setText("JLabel10");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryNameLbl;
}
/**
 * Return the GrpQueryOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getGrpQueryOkBtn() {
	if (ivjGrpQueryOkBtn == null) {
		try {
			ivjGrpQueryOkBtn = new javax.swing.JButton();
			ivjGrpQueryOkBtn.setName("GrpQueryOkBtn");
			ivjGrpQueryOkBtn.setText("Close");
			ivjGrpQueryOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getGrpQueryDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryOkBtn;
}
/**
 * Return the GrpQueryOwnerLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryOwnerLbl() {
	if (ivjGrpQueryOwnerLbl == null) {
		try {
			ivjGrpQueryOwnerLbl = new javax.swing.JLabel();
			ivjGrpQueryOwnerLbl.setName("GrpQueryOwnerLbl");
			ivjGrpQueryOwnerLbl.setText("JLabel13");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQueryOwnerLbl;
}
/**
 * Return the GrpQuerySP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getGrpQuerySP() {
	if (ivjGrpQuerySP == null) {
		try {
			ivjGrpQuerySP = new javax.swing.JScrollPane();
			ivjGrpQuerySP.setName("GrpQuerySP");
			getGrpQuerySP().setViewportView(getGrpQueryLB());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrpQuerySP;
}
/**
 * This method initializes helpM	
 * 	
 * @return javax.swing.JMenu	
 */    
private JMenu getHelpM() {
	if (helpM == null) {
		helpM = new JMenu();
		helpM.setText("Help");
		helpM.add(getAboutMI());
	}
	return helpM;
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
			ivjHostSplit.setLastDividerLocation(274);
			ivjHostSplit.setDividerLocation(274);
			ivjHostSplit.setOneTouchExpandable(true);
			ivjHostSplit.setContinuousLayout(true);
			getHostSplit().add(getRemoteTP(), "right");
			getHostSplit().add(getLocalFilePnl(), "left");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjHostSplit;
}
/**
 * Return the InboxAddSenderBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInboxAddSenderBtn() {
	if (ivjInboxAddSenderBtn == null) {
		try {
			ivjInboxAddSenderBtn = new javax.swing.JButton();
			ivjInboxAddSenderBtn.setName("InboxAddSenderBtn");
			ivjInboxAddSenderBtn.setToolTipText("Add sender of package to your saved user list");
			ivjInboxAddSenderBtn.setText("");
			ivjInboxAddSenderBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjInboxAddSenderBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjInboxAddSenderBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/buddy.gif")));
			ivjInboxAddSenderBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjInboxAddSenderBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						addSender();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxAddSenderBtn;
}
/**
 * Return the InboxDownloadBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInboxFileDownBtn() {
	if (ivjInboxFileDownBtn == null) {
		try {
			ivjInboxFileDownBtn = new javax.swing.JButton();
			ivjInboxFileDownBtn.setName("InboxFileDownBtn");
			ivjInboxFileDownBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/download.gif")));
			ivjInboxFileDownBtn.setToolTipText("Receive the selected files");
			ivjInboxFileDownBtn.setText("");
			ivjInboxFileDownBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjInboxFileDownBtn.addActionListener(downloadFileHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxFileDownBtn;
}
/**
 * Return the InboxFileLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getInboxFileLbl() {
	if (ivjInboxFileLbl == null) {
		try {
			ivjInboxFileLbl = new javax.swing.JLabel();
			ivjInboxFileLbl.setName("InboxFileLbl");
			ivjInboxFileLbl.setText("Files in the selected package:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxFileLbl;
}
/**
 * Return the InboxFilePnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getInboxFilePnl() {
	if (ivjInboxFilePnl == null) {
		try {
			ivjInboxFilePnl = new javax.swing.JPanel();
			ivjInboxFilePnl.setName("InboxFilePnl");
			ivjInboxFilePnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsInboxFileLbl = new java.awt.GridBagConstraints();
			constraintsInboxFileLbl.gridx = 0; constraintsInboxFileLbl.gridy = 0;
			constraintsInboxFileLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsInboxFileLbl.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsInboxFileLbl.weightx = 1.0;
			getInboxFilePnl().add(getInboxFileLbl(), constraintsInboxFileLbl);

			java.awt.GridBagConstraints constraintsInboxFileDownBtn = new java.awt.GridBagConstraints();
			constraintsInboxFileDownBtn.gridx = 1; constraintsInboxFileDownBtn.gridy = 0;
			constraintsInboxFileDownBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsInboxFileDownBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getInboxFilePnl().add(getInboxFileDownBtn(), constraintsInboxFileDownBtn);

			java.awt.GridBagConstraints constraintsInboxTransferBtn = new java.awt.GridBagConstraints();
			constraintsInboxTransferBtn.gridx = 2; constraintsInboxTransferBtn.gridy = 0;
			constraintsInboxTransferBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			getInboxFilePnl().add(getInboxTransferBtn(), constraintsInboxTransferBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxFilePnl;
}
/**
 * Return the InboxFileSortTM property value.
 * @return TableSorter
 */
private TableSorter getInboxFileSortTM() {
	if (ivjInboxFileSortTM == null) {
		try {
			ivjInboxFileSortTM = new TableSorter();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxFileSortTM;
}
/**
 * Return the InboxFileSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getInboxFileSP() {
	if (ivjInboxFileSP == null) {
		try {
			ivjInboxFileSP = new javax.swing.JScrollPane();
			ivjInboxFileSP.setName("InboxFileSP");
			ivjInboxFileSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjInboxFileSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getInboxFileSP().setViewportView(getInboxFileTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxFileSP;
}
/**
 * Return the InboxFileTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getInboxFileTbl() {
	if (ivjInboxFileTbl == null) {
		try {
			ivjInboxFileTbl = new javax.swing.JTable();
			ivjInboxFileTbl.setName("InboxFileTbl");
			getInboxFileSP().setColumnHeaderView(ivjInboxFileTbl.getTableHeader());
			ivjInboxFileTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjInboxFileTbl.setBounds(0, 0, 200, 200);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxFileTbl;
}
/**
 * Return the InboxFileTM property value.
 * @return InboxFileTM
 */
private InboxFileTM getInboxFileTM() {
	if (ivjInboxFileTM == null) {
		try {
			ivjInboxFileTM = new InboxFileTM();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxFileTM;
}
/**
 * Return the JButton3 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInboxPkgDownBtn() {
	if (ivjInboxPkgDownBtn == null) {
		try {
			ivjInboxPkgDownBtn = new javax.swing.JButton();
			ivjInboxPkgDownBtn.setName("InboxPkgDownBtn");
			ivjInboxPkgDownBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/download.gif")));
			ivjInboxPkgDownBtn.setToolTipText("Receive all files of the selected package");
			ivjInboxPkgDownBtn.setText("");
			ivjInboxPkgDownBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjInboxPkgDownBtn.addActionListener(downloadPkgHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxPkgDownBtn;
}
/**
 * Return the InboxPkgLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getInboxPkgLbl() {
	if (ivjInboxPkgLbl == null) {
		try {
			ivjInboxPkgLbl = new javax.swing.JLabel();
			ivjInboxPkgLbl.setName("InboxPkgLbl");
			ivjInboxPkgLbl.setText("Packages sent to you to download:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxPkgLbl;
}
/**
 * Return the InboxPkgPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getInboxPkgPnl() {
	if (ivjInboxPkgPnl == null) {
		try {
			ivjInboxPkgPnl = new javax.swing.JPanel();
			ivjInboxPkgPnl.setName("InboxPkgPnl");
			ivjInboxPkgPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsInboxPkgLbl = new java.awt.GridBagConstraints();
			constraintsInboxPkgLbl.gridx = 0; constraintsInboxPkgLbl.gridy = 0;
			constraintsInboxPkgLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsInboxPkgLbl.anchor = java.awt.GridBagConstraints.SOUTH;
			getInboxPkgPnl().add(getInboxPkgLbl(), constraintsInboxPkgLbl);

			java.awt.GridBagConstraints constraintsInboxRefreshBtn = new java.awt.GridBagConstraints();
			constraintsInboxRefreshBtn.gridx = 2; constraintsInboxRefreshBtn.gridy = 0;
			constraintsInboxRefreshBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsInboxRefreshBtn.anchor = java.awt.GridBagConstraints.EAST;
			constraintsInboxRefreshBtn.weightx = 1.0;
			constraintsInboxRefreshBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getInboxPkgPnl().add(getInboxRefreshBtn(), constraintsInboxRefreshBtn);

			java.awt.GridBagConstraints constraintsInboxPkgDownBtn = new java.awt.GridBagConstraints();
			constraintsInboxPkgDownBtn.gridx = 5; constraintsInboxPkgDownBtn.gridy = 0;
			constraintsInboxPkgDownBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsInboxPkgDownBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			java.awt.GridBagConstraints constraintsInboxAddSenderBtn = new java.awt.GridBagConstraints();
			constraintsInboxAddSenderBtn.gridx = 4; constraintsInboxAddSenderBtn.gridy = 0;
			constraintsInboxAddSenderBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsInboxAddSenderBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			java.awt.GridBagConstraints constraintsFilterLbl = new java.awt.GridBagConstraints();
			constraintsFilterLbl.gridx = 1; constraintsFilterLbl.gridy = 0;
			constraintsFilterLbl.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsFilterLbl.insets = new java.awt.Insets(0, 3, 0, 0);
			getInboxPkgPnl().add(getFilterLbl(), constraintsFilterLbl);

			java.awt.GridBagConstraints constraintsInboxPkgTransferBtn = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints11 = new java.awt.GridBagConstraints();
			consGridBagConstraints11.gridy = 0;
			consGridBagConstraints11.gridx = 3;
			consGridBagConstraints11.insets = new java.awt.Insets(0,0,0,2);
			constraintsInboxPkgTransferBtn.gridx = 6; constraintsInboxPkgTransferBtn.gridy = 0;
			constraintsInboxPkgTransferBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			ivjInboxPkgPnl.add(getInboxPkgDownBtn(), constraintsInboxPkgDownBtn);
			ivjInboxPkgPnl.add(getInboxAddSenderBtn(), constraintsInboxAddSenderBtn);
			ivjInboxPkgPnl.add(getInboxPkgTransferBtn(), constraintsInboxPkgTransferBtn);
			ivjInboxPkgPnl.add(getInboxPkgViewBtn(), consGridBagConstraints11);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxPkgPnl;
}
/**
 * Return the InboxPkgSortTM property value.
 * @return TableSorter
 */
private TableSorter getInboxPkgSortTM() {
	if (ivjInboxPkgSortTM == null) {
		try {
			ivjInboxPkgSortTM = new TableSorter();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxPkgSortTM;
}
/**
 * Return the InboxPkgSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getInboxPkgSP() {
	if (ivjInboxPkgSP == null) {
		try {
			ivjInboxPkgSP = new javax.swing.JScrollPane();
			ivjInboxPkgSP.setName("InboxPkgSP");
			ivjInboxPkgSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjInboxPkgSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getInboxPkgSP().setViewportView(getInboxPkgTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxPkgSP;
}
/**
 * Return the InboxPkgTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getInboxPkgTbl() {
	if (ivjInboxPkgTbl == null) {
		try {
			ivjInboxPkgTbl = new javax.swing.JTable();
			ivjInboxPkgTbl.setName("InboxPkgTbl");
			getInboxPkgSP().setColumnHeaderView(ivjInboxPkgTbl.getTableHeader());
			ivjInboxPkgTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjInboxPkgTbl.setBounds(0, 0, 200, 200);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxPkgTbl;
}
/**
 * Return the InboxPkgTM property value.
 * @return InboxPkgTM
 */
private InboxPkgTM getInboxPkgTM() {
	if (ivjInboxPkgTM == null) {
		try {
			ivjInboxPkgTM = new InboxPkgTM();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxPkgTM;
}
/**
 * Return the InboxPkgTransferBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInboxPkgTransferBtn() {
	if (ivjInboxPkgTransferBtn == null) {
		try {
			ivjInboxPkgTransferBtn = new javax.swing.JButton();
			ivjInboxPkgTransferBtn.setName("InboxPkgTransferBtn");
			ivjInboxPkgTransferBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/fwdpkg.gif")));
			ivjInboxPkgTransferBtn.setToolTipText("Forward package to other users");
			ivjInboxPkgTransferBtn.setText("");
			ivjInboxPkgTransferBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjInboxPkgTransferBtn.addActionListener(forwardPkgActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxPkgTransferBtn;
}
/**
 * This method initializes jButton
 * 
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInboxPkgViewBtn() {
	if(inboxPkgViewBtn == null) {
		inboxPkgViewBtn = new javax.swing.JButton();
		inboxPkgViewBtn.setPreferredSize(new java.awt.Dimension(22,22));
		inboxPkgViewBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/new_mail.gif")));
		inboxPkgViewBtn.setToolTipText("View Package Description");
		inboxPkgViewBtn.setMargin(new java.awt.Insets(0,0,0,0));
		inboxPkgViewBtn.setName("InboxViewDescBtn");
		inboxPkgViewBtn.addActionListener(viewDescriptionListener);
	}
	return inboxPkgViewBtn;
}
/**
 * Return the InboxPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getInboxPnl() {
	if (ivjInboxPnl == null) {
		try {
			ivjInboxPnl = new javax.swing.JPanel();
			ivjInboxPnl.setName("InboxPnl");
			ivjInboxPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsInboxPkgPnl = new java.awt.GridBagConstraints();
			constraintsInboxPkgPnl.gridx = 0; constraintsInboxPkgPnl.gridy = 0;
			constraintsInboxPkgPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsInboxPkgPnl.insets = new java.awt.Insets(5, 5, 0, 5);
			getInboxPnl().add(getInboxPkgPnl(), constraintsInboxPkgPnl);

			java.awt.GridBagConstraints constraintsInboxPkgSP = new java.awt.GridBagConstraints();
			constraintsInboxPkgSP.gridx = 0; constraintsInboxPkgSP.gridy = 1;
			constraintsInboxPkgSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsInboxPkgSP.weightx = 1.0;
			constraintsInboxPkgSP.weighty = 1.0;
			constraintsInboxPkgSP.insets = new java.awt.Insets(0, 5, 0, 5);
			getInboxPnl().add(getInboxPkgSP(), constraintsInboxPkgSP);

			java.awt.GridBagConstraints constraintsInboxFilePnl = new java.awt.GridBagConstraints();
			constraintsInboxFilePnl.gridx = 0; constraintsInboxFilePnl.gridy = 2;
			constraintsInboxFilePnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsInboxFilePnl.insets = new java.awt.Insets(10, 5, 0, 5);
			getInboxPnl().add(getInboxFilePnl(), constraintsInboxFilePnl);

			java.awt.GridBagConstraints constraintsInboxFileSP = new java.awt.GridBagConstraints();
			constraintsInboxFileSP.gridx = 0; constraintsInboxFileSP.gridy = 3;
			constraintsInboxFileSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsInboxFileSP.weightx = 1.0;
			constraintsInboxFileSP.weighty = 1.0;
			constraintsInboxFileSP.insets = new java.awt.Insets(0, 5, 5, 5);
			getInboxPnl().add(getInboxFileSP(), constraintsInboxFileSP);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxPnl;
}
/**
 * Return the InboxRefreshBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInboxRefreshBtn() {
	if (ivjInboxRefreshBtn == null) {
		try {
			ivjInboxRefreshBtn = new javax.swing.JButton();
			ivjInboxRefreshBtn.setName("InboxRefreshBtn");
			ivjInboxRefreshBtn.setToolTipText("Refresh Inbox");
			ivjInboxRefreshBtn.setText("");
			ivjInboxRefreshBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjInboxRefreshBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjInboxRefreshBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/refresh.gif")));
			ivjInboxRefreshBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjInboxRefreshBtn.addActionListener(inboxRefreshListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxRefreshBtn;
}
/**
 * Return the InboxTransferBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInboxTransferBtn() {
	if (ivjInboxTransferBtn == null) {
		try {
			ivjInboxTransferBtn = new javax.swing.JButton();
			ivjInboxTransferBtn.setName("InboxTransferBtn");
			ivjInboxTransferBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/fwdfile.gif")));
			ivjInboxTransferBtn.setToolTipText("Forward the selected files");
			ivjInboxTransferBtn.setText("");
			ivjInboxTransferBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjInboxTransferBtn.addActionListener(forwardFilesActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInboxTransferBtn;
}
/**
 * Return the itarLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getItarLbl() {
	if (itarLbl == null) {
		try {
			itarLbl = new JLabel();
			itarLbl.setName("ItarLbl");
			itarLbl.setText("Session is ITAR certified");
			itarLbl.setForeground(Color.red);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return itarLbl;
}
/**
 * This method initializes itarMI	
 * 	
 * @return javax.swing.JMenuItem	
 */    
private JMenuItem getItarMI() {
	if (itarMI == null) {
		itarMI = new JMenuItem();
		itarMI.setText("ITAR Certification...");
		itarMI.setToolTipText("Manage session's ITAR certification");
		itarMI.addActionListener(itarListener);
	}
	return itarMI;
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel() {
	if (ivjJLabel == null) {
		try {
			ivjJLabel = new javax.swing.JLabel();
			ivjJLabel.setName("JLabel");
			ivjJLabel.setText("Or select a ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel;
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
			ivjJLabel1.setText("File Transfer Information");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabel12 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel12() {
	if (ivjJLabel12 == null) {
		try {
			ivjJLabel12 = new javax.swing.JLabel();
			ivjJLabel12.setName("JLabel12");
			ivjJLabel12.setText("Select the package to which the files should be added:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel12;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Send to:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/buddy.gif")));
			ivjJLabel3.setText("user, ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/group.gif")));
			ivjJLabel4.setText("group,");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/project.gif")));
			ivjJLabel5.setText("project:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
}
/**
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel6() {
	if (ivjJLabel6 == null) {
		try {
			ivjJLabel6 = new javax.swing.JLabel();
			ivjJLabel6.setName("JLabel6");
			ivjJLabel6.setText("All transfers are recorded");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel6;
}
/**
 * Return the JLabel7 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel7() {
	if (ivjJLabel7 == null) {
		try {
			ivjJLabel7 = new javax.swing.JLabel();
			ivjJLabel7.setName("JLabel7");
			ivjJLabel7.setText("Expires on:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel7;
}
/**
 * Return the JLabel9 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel9() {
	if (ivjJLabel9 == null) {
		try {
			ivjJLabel9 = new javax.swing.JLabel();
			ivjJLabel9.setName("JLabel9");
			ivjJLabel9.setText("or ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel9;
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

			java.awt.GridBagConstraints constraintsStopTB = new java.awt.GridBagConstraints();
			constraintsStopTB.gridx = 1; constraintsStopTB.gridy = 0;
			getJPanel1().add(getStopTB(), constraintsStopTB);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel1.weightx = 1.0;
			constraintsJLabel1.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanel1().add(getJLabel1(), constraintsJLabel1);
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
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel = new java.awt.GridBagConstraints();
			constraintsJLabel.gridx = 0; constraintsJLabel.gridy = 0;
			getJPanel3().add(getJLabel(), constraintsJLabel);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 1; constraintsJLabel3.gridy = 0;
			getJPanel3().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
			constraintsJLabel4.gridx = 2; constraintsJLabel4.gridy = 0;
			constraintsJLabel4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel4.weightx = 1.0;
			getJPanel3().add(getJLabel4(), constraintsJLabel4);
			ivjJPanel3.setPreferredSize(new java.awt.Dimension(103,16));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}
/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel4() {
	if (ivjJPanel4 == null) {
		try {
			ivjJPanel4 = new javax.swing.JPanel();
			ivjJPanel4.setName("JPanel4");
			ivjJPanel4.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsPkgOkBtn = new java.awt.GridBagConstraints();
			constraintsPkgOkBtn.gridx = 0; constraintsPkgOkBtn.gridy = 0;
			constraintsPkgOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanel4().add(getPkgOkBtn(), constraintsPkgOkBtn);

			java.awt.GridBagConstraints constraintsPkgCanBtn = new java.awt.GridBagConstraints();
			constraintsPkgCanBtn.gridx = 2; constraintsPkgCanBtn.gridy = 0;
			constraintsPkgCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getJPanel4().add(getPkgCanBtn(), constraintsPkgCanBtn);

			java.awt.GridBagConstraints constraintsPkgAddFilesBtn = new java.awt.GridBagConstraints();
			constraintsPkgAddFilesBtn.gridx = 1; constraintsPkgAddFilesBtn.gridy = 0;
			constraintsPkgAddFilesBtn.insets = new java.awt.Insets(0, 5, 0, 5);
			getJPanel4().add(getPkgAddFilesBtn(), constraintsPkgAddFilesBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel4;
}
/**
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel5() {
	if (ivjJPanel5 == null) {
		try {
			ivjJPanel5 = new javax.swing.JPanel();
			ivjJPanel5.setName("JPanel5");
			ivjJPanel5.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDownConfOkBtn = new java.awt.GridBagConstraints();
			constraintsDownConfOkBtn.gridx = 0; constraintsDownConfOkBtn.gridy = 0;
			constraintsDownConfOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanel5().add(getDownConfOkBtn(), constraintsDownConfOkBtn);

			java.awt.GridBagConstraints constraintsDownConfCanBtn = new java.awt.GridBagConstraints();
			constraintsDownConfCanBtn.gridx = 1; constraintsDownConfCanBtn.gridy = 0;
			constraintsDownConfCanBtn.insets = new java.awt.Insets(0, 5, 0, 5);
			getJPanel5().add(getDownConfCanBtn(), constraintsDownConfCanBtn);

			java.awt.GridBagConstraints constraintsDownConfInfoBtn = new java.awt.GridBagConstraints();
			constraintsDownConfInfoBtn.gridx = 2; constraintsDownConfInfoBtn.gridy = 0;
			constraintsDownConfInfoBtn.insets = new java.awt.Insets(0, 5, 0, 5);
			getJPanel5().add(getDownConfInfoBtn(), constraintsDownConfInfoBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel5;
}
/**
 * Return the JPanel6 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel6() {
	if (ivjJPanel6 == null) {
		try {
			ivjJPanel6 = new javax.swing.JPanel();
			ivjJPanel6.setName("JPanel6");
			ivjJPanel6.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel9 = new java.awt.GridBagConstraints();
			constraintsJLabel9.gridx = 0; constraintsJLabel9.gridy = 0;
			getJPanel6().add(getJLabel9(), constraintsJLabel9);

			java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
			constraintsJLabel5.gridx = 1; constraintsJLabel5.gridy = 0;
			constraintsJLabel5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel5.weightx = 1.0;
			getJPanel6().add(getJLabel5(), constraintsJLabel5);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel6;
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
 * Return the JSeparator4 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator4() {
	if (ivjJSeparator4 == null) {
		try {
			ivjJSeparator4 = new javax.swing.JSeparator();
			ivjJSeparator4.setName("JSeparator4");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator4;
}
/**
 * Return the JSeparator5 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator5() {
	if (ivjJSeparator5 == null) {
		try {
			ivjJSeparator5 = new javax.swing.JSeparator();
			ivjJSeparator5.setName("JSeparator5");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator5;
}
/**
 * Return the JSeparator6 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator6() {
	if (ivjJSeparator6 == null) {
		try {
			ivjJSeparator6 = new javax.swing.JSeparator();
			ivjJSeparator6.setName("JSeparator6");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator6;
}
/**
 * Return the JSeparator7 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator7() {
	if (ivjJSeparator7 == null) {
		try {
			ivjJSeparator7 = new javax.swing.JSeparator();
			ivjJSeparator7.setName("JSeparator7");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator7;
}
/**
 * Return the JSeparator8 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator8() {
	if (ivjJSeparator8 == null) {
		try {
			ivjJSeparator8 = new javax.swing.JSeparator();
			ivjJSeparator8.setName("JSeparator8");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator8;
}
/**
 * Return the JSeparator9 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator9() {
	if (ivjJSeparator9 == null) {
		try {
			ivjJSeparator9 = new javax.swing.JSeparator();
			ivjJSeparator9.setName("JSeparator9");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator9;
}
/**
 * Return the LocalFilePnl property value.
 * @return LocalFilePnl
 */
private LocalFilePnl getLocalFilePnl() {
	if (ivjLocalFilePnl == null) {
		try {
			ivjLocalFilePnl = new LocalFilePnl();
			ivjLocalFilePnl.setName("LocalFilePnl");
			ivjLocalFilePnl.setUploadBtnToolTipText("Add selected files to the new package");
			ivjLocalFilePnl.addLocalFilePnlListener(uploadListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalFilePnl;
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLocalM;
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
			ivjLoginCanBtn.setToolTipText("Press to cancel login");
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
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getLoginCP() {
	if (ivjLoginCP == null) {
		try {
			ivjLoginCP = new javax.swing.JPanel();
			ivjLoginCP.setName("LoginCP");
			ivjLoginCP.setLayout(new java.awt.GridBagLayout());
			ivjLoginCP.setBounds(77, 1377, 217, 133);

			java.awt.GridBagConstraints constraintsUserLbl = new java.awt.GridBagConstraints();
			constraintsUserLbl.gridx = 0; constraintsUserLbl.gridy = 0;
			constraintsUserLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUserLbl.insets = new java.awt.Insets(10, 10, 0, 0);
			getLoginCP().add(getUserLbl(), constraintsUserLbl);

			java.awt.GridBagConstraints constraintsPwdLbl = new java.awt.GridBagConstraints();
			constraintsPwdLbl.gridx = 0; constraintsPwdLbl.gridy = 1;
			constraintsPwdLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPwdLbl.insets = new java.awt.Insets(5, 10, 0, 0);
			getLoginCP().add(getPwdLbl(), constraintsPwdLbl);

			java.awt.GridBagConstraints constraintsUserTF = new java.awt.GridBagConstraints();
			constraintsUserTF.gridx = 1; constraintsUserTF.gridy = 0;
			constraintsUserTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUserTF.weightx = 1.0;
			constraintsUserTF.insets = new java.awt.Insets(10, 5, 0, 10);
			getLoginCP().add(getUserTF(), constraintsUserTF);

			java.awt.GridBagConstraints constraintsPwdTF = new java.awt.GridBagConstraints();
			constraintsPwdTF.gridx = 1; constraintsPwdTF.gridy = 1;
			constraintsPwdTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPwdTF.weightx = 1.0;
			constraintsPwdTF.insets = new java.awt.Insets(5, 5, 0, 10);
			getLoginCP().add(getPwdTF(), constraintsPwdTF);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 2;
			constraintsJPanel2.gridwidth = 0;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel2.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsJPanel2.weightx = 1.0;
			constraintsJPanel2.weighty = 1.0;
			constraintsJPanel2.insets = new java.awt.Insets(10, 10, 10, 10);
			getLoginCP().add(getJPanel2(), constraintsJPanel2);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoginCP;
}
/**
 * Return the LoginDlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getLoginDlg() {
	if (ivjLoginDlg == null) {
		try {
			ivjLoginDlg = new javax.swing.JDialog(ownerContainer);
			ivjLoginDlg.setName("LoginDlg2");
			ivjLoginDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjLoginDlg.setBounds(1115, 1529, 217, 133);
			ivjLoginDlg.setModal(true);
			ivjLoginDlg.setTitle("Login");
			ivjLoginDlg.setContentPane(getLoginCP());
			ivjLoginDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowOpened(java.awt.event.WindowEvent e) {    
					try {
						if (getUserTF().getText() != null && getUserTF().getText().length() > 0)
							getPwdTF().requestFocus();
						else
							getUserTF().requestFocus();
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
			ivjLoginMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/logon.gif")));
			ivjLoginMI.setMnemonic('i');
			ivjLoginMI.setText("Login...");
			ivjLoginMI.setActionCommand("Login");
			ivjLoginMI.addActionListener(loginListener);
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
			ivjLoginOkBtn.setToolTipText("Press to login");
			ivjLoginOkBtn.setText("Ok");
			ivjLoginOkBtn.addActionListener(new BtnPressListener(getLoginDlg()));
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
			ivjLoginTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjLoginTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/logon.gif")));
			ivjLoginTB.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjLoginTB.addActionListener(loginListener);
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
			ivjLogoutMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/logoff.gif")));
			ivjLogoutMI.setMnemonic('o');
			ivjLogoutMI.setText("Logout");
			ivjLogoutMI.addActionListener(logoutListener);
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
			ivjLogoutTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/logoff.gif")));
			ivjLogoutTB.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjLogoutTB.addActionListener(logoutListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLogoutTB;
}
/**
 * This method initializes msgHandlerMI
 * 
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getMsgHandlerMI() {
	if(msgHandlerMI == null) {
		msgHandlerMI = new javax.swing.JMenuItem();
		msgHandlerMI.setText("Show messages");
		msgHandlerMI.addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {
				// Can't use Window's setLocationRelativeTo(Component) becuase
				// we still support 1.3. So we have to grow our own.

				// Get related information.
				Point dbw = ownerContainer.getLocation();
				Dimension dbd = ownerContainer.getSize();
				Dimension md = msgHandler.getSize();

				// Determine point to locate msg window so center over dropbox window.
				Point mw = new Point();
				mw.x = dbw.x + dbd.width/2 - md.width/2;
				mw.y = dbw.y + dbd.height/2 - md.height/2;

				// Ensure msg window is still on screen.
				Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
				if (mw.x + md.width > s.width) mw.x = s.width - md.width;
				if (mw.x < 0) mw.x = 0;
				if (mw.y + md.height > s.height) mw.y = s.height - md.height;
				if (mw.y < 0) mw.y = 0;

				// Position it and show it.
				msgHandler.setLocation(mw);
				msgHandler.show();
				msgHandler.toFront();
			}
		});
	}
	return msgHandlerMI;
}
/**
 * Return the ManageGroups1 property value.
 * @return oem.edge.ed.odc.dropbox.client.ManageGroups
 */
private ManageGroups getManageGroups() {
	if (ivjManageGroups == null) {
		try {
			ivjManageGroups = new ManageGroups(ownerContainer);
			ivjManageGroups.setSize(453, 546);
			ivjManageGroups.setName("ManageGroups");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjManageGroups;
}
/**
 * Return the OptionsMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getOptionsMI() {
	if (ivjOptionsMI == null) {
		try {
			ivjOptionsMI = new javax.swing.JMenuItem();
			ivjOptionsMI.setName("OptionsMI");
			ivjOptionsMI.setText("Options...");
			ivjOptionsMI.setEnabled(true);
			ivjOptionsMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getDropboxOptions().doOptions(ownerContainer);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOptionsMI;
}
/**
 * Return the OutboxDeleteBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOutboxDeleteBtn() {
	if (ivjOutboxDeleteBtn == null) {
		try {
			ivjOutboxDeleteBtn = new javax.swing.JButton();
			ivjOutboxDeleteBtn.setName("OutboxDeleteBtn");
			ivjOutboxDeleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/trash.gif")));
			ivjOutboxDeleteBtn.setToolTipText("Remove selected packages from server");
			ivjOutboxDeleteBtn.setText("");
			ivjOutboxDeleteBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjOutboxDeleteBtn.addActionListener(deleteListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxDeleteBtn;
}
/**
 * Return the OutboxFileDownBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOutboxFileDownBtn() {
	if (ivjOutboxFileDownBtn == null) {
		try {
			ivjOutboxFileDownBtn = new javax.swing.JButton();
			ivjOutboxFileDownBtn.setName("OutboxFileDownBtn");
			ivjOutboxFileDownBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/download.gif")));
			ivjOutboxFileDownBtn.setToolTipText("Receive the selected files");
			ivjOutboxFileDownBtn.setText("");
			ivjOutboxFileDownBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjOutboxFileDownBtn.addActionListener(downloadFileHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxFileDownBtn;
}
/**
 * Return the OutboxFileLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getOutboxFileLbl() {
	if (ivjOutboxFileLbl == null) {
		try {
			ivjOutboxFileLbl = new javax.swing.JLabel();
			ivjOutboxFileLbl.setName("OutboxFileLbl");
			ivjOutboxFileLbl.setText("Files in the selected package:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxFileLbl;
}
/**
 * Return the OutboxFilePnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getOutboxFilePnl() {
	if (ivjOutboxFilePnl == null) {
		try {
			ivjOutboxFilePnl = new javax.swing.JPanel();
			ivjOutboxFilePnl.setName("OutboxFilePnl");
			ivjOutboxFilePnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOutboxFileLbl = new java.awt.GridBagConstraints();
			constraintsOutboxFileLbl.gridx = 0; constraintsOutboxFileLbl.gridy = 0;
			constraintsOutboxFileLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOutboxFileLbl.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsOutboxFileLbl.weightx = 1.0;
			getOutboxFilePnl().add(getOutboxFileLbl(), constraintsOutboxFileLbl);

			java.awt.GridBagConstraints constraintsOutboxFileDownBtn = new java.awt.GridBagConstraints();
			constraintsOutboxFileDownBtn.gridx = 1; constraintsOutboxFileDownBtn.gridy = 0;
			constraintsOutboxFileDownBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsOutboxFileDownBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getOutboxFilePnl().add(getOutboxFileDownBtn(), constraintsOutboxFileDownBtn);

			java.awt.GridBagConstraints constraintsOutboxFileViewBtn = new java.awt.GridBagConstraints();
			constraintsOutboxFileViewBtn.gridx = 2; constraintsOutboxFileViewBtn.gridy = 0;
			constraintsOutboxFileViewBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsOutboxFileViewBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getOutboxFilePnl().add(getOutboxFileViewBtn(), constraintsOutboxFileViewBtn);

			java.awt.GridBagConstraints constraintsOutboxTransferBtn = new java.awt.GridBagConstraints();
			constraintsOutboxTransferBtn.gridx = 3; constraintsOutboxTransferBtn.gridy = 0;
			constraintsOutboxTransferBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			getOutboxFilePnl().add(getOutboxTransferBtn(), constraintsOutboxTransferBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxFilePnl;
}
/**
 * Return the OutboxFileSortTM property value.
 * @return TableSorter
 */
private TableSorter getOutboxFileSortTM() {
	if (ivjOutboxFileSortTM == null) {
		try {
			ivjOutboxFileSortTM = new TableSorter();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxFileSortTM;
}
/**
 * Return the OutboxFileSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getOutboxFileSP() {
	if (ivjOutboxFileSP == null) {
		try {
			ivjOutboxFileSP = new javax.swing.JScrollPane();
			ivjOutboxFileSP.setName("OutboxFileSP");
			ivjOutboxFileSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjOutboxFileSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getOutboxFileSP().setViewportView(getOutboxFileTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxFileSP;
}
/**
 * Return the OutboxFileTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getOutboxFileTbl() {
	if (ivjOutboxFileTbl == null) {
		try {
			ivjOutboxFileTbl = new javax.swing.JTable();
			ivjOutboxFileTbl.setName("OutboxFileTbl");
			getOutboxFileSP().setColumnHeaderView(ivjOutboxFileTbl.getTableHeader());
			ivjOutboxFileTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjOutboxFileTbl.setBounds(0, 0, 200, 200);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxFileTbl;
}
/**
 * Return the OutboxFileTM property value.
 * @return oem.edge.ed.odc.dropbox.client.OutboxFileTM
 */
private OutboxFileTM getOutboxFileTM() {
	if (ivjOutboxFileTM == null) {
		try {
			ivjOutboxFileTM = new OutboxFileTM();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxFileTM;
}
/**
 * Return the OutboxFileRecBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOutboxFileViewBtn() {
	if (ivjOutboxFileViewBtn == null) {
		try {
			ivjOutboxFileViewBtn = new javax.swing.JButton();
			ivjOutboxFileViewBtn.setName("OutboxFileViewBtn");
			ivjOutboxFileViewBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/propview.gif")));
			ivjOutboxFileViewBtn.setToolTipText("View file recipients");
			ivjOutboxFileViewBtn.setText("");
			ivjOutboxFileViewBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjOutboxFileViewBtn.addActionListener(fileAclListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxFileViewBtn;
}
/**
 * Return the OutboxPkgDownBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOutboxPkgDownBtn() {
	if (ivjOutboxPkgDownBtn == null) {
		try {
			ivjOutboxPkgDownBtn = new javax.swing.JButton();
			ivjOutboxPkgDownBtn.setName("OutboxPkgDownBtn");
			ivjOutboxPkgDownBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/download.gif")));
			ivjOutboxPkgDownBtn.setToolTipText("Receive all files of the selected packages");
			ivjOutboxPkgDownBtn.setText("");
			ivjOutboxPkgDownBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjOutboxPkgDownBtn.addActionListener(downloadPkgHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxPkgDownBtn;
}
/**
 * Return the OutboxPkgRecBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOutboxPkgEditBtn() {
	if (ivjOutboxPkgEditBtn == null) {
		try {
			ivjOutboxPkgEditBtn = new javax.swing.JButton();
			ivjOutboxPkgEditBtn.setName("OutboxPkgEditBtn");
			ivjOutboxPkgEditBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/property.gif")));
			ivjOutboxPkgEditBtn.setToolTipText("Edit package recipients");
			ivjOutboxPkgEditBtn.setText("");
			ivjOutboxPkgEditBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjOutboxPkgEditBtn.addActionListener(editPkgActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxPkgEditBtn;
}
/**
 * Return the OutboxPkgLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getOutboxPkgLbl() {
	if (ivjOutboxPkgLbl == null) {
		try {
			ivjOutboxPkgLbl = new javax.swing.JLabel();
			ivjOutboxPkgLbl.setName("OutboxPkgLbl");
			ivjOutboxPkgLbl.setText("Packages you have sent:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxPkgLbl;
}
/**
 * Return the OutboxPkgPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getOutboxPkgPnl() {
	if (ivjOutboxPkgPnl == null) {
		try {
			ivjOutboxPkgPnl = new javax.swing.JPanel();
			ivjOutboxPkgPnl.setName("OutboxPkgPnl");
			ivjOutboxPkgPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOutboxPkgLbl = new java.awt.GridBagConstraints();
			constraintsOutboxPkgLbl.gridx = 0; constraintsOutboxPkgLbl.gridy = 0;
			constraintsOutboxPkgLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOutboxPkgLbl.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsOutboxPkgLbl.weightx = 1.0;
			getOutboxPkgPnl().add(getOutboxPkgLbl(), constraintsOutboxPkgLbl);

			java.awt.GridBagConstraints constraintsOutboxRefreshBtn = new java.awt.GridBagConstraints();
			constraintsOutboxRefreshBtn.gridx = 1; constraintsOutboxRefreshBtn.gridy = 0;
			constraintsOutboxRefreshBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsOutboxRefreshBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getOutboxPkgPnl().add(getOutboxRefreshBtn(), constraintsOutboxRefreshBtn);

			java.awt.GridBagConstraints constraintsOutboxPkgViewBtn = new java.awt.GridBagConstraints();
			constraintsOutboxPkgViewBtn.gridx = 2; constraintsOutboxPkgViewBtn.gridy = 0;
			constraintsOutboxPkgViewBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsOutboxPkgViewBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getOutboxPkgPnl().add(getOutboxPkgViewBtn(), constraintsOutboxPkgViewBtn);

			java.awt.GridBagConstraints constraintsOutboxPkgEditBtn = new java.awt.GridBagConstraints();
			constraintsOutboxPkgEditBtn.gridx = 3; constraintsOutboxPkgEditBtn.gridy = 0;
			constraintsOutboxPkgEditBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsOutboxPkgEditBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getOutboxPkgPnl().add(getOutboxPkgEditBtn(), constraintsOutboxPkgEditBtn);

			java.awt.GridBagConstraints constraintsOutboxDeleteBtn = new java.awt.GridBagConstraints();
			constraintsOutboxDeleteBtn.gridx = 4; constraintsOutboxDeleteBtn.gridy = 0;
			constraintsOutboxDeleteBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsOutboxDeleteBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getOutboxPkgPnl().add(getOutboxDeleteBtn(), constraintsOutboxDeleteBtn);

			java.awt.GridBagConstraints constraintsOutboxPkgDownBtn = new java.awt.GridBagConstraints();
			constraintsOutboxPkgDownBtn.gridx = 5; constraintsOutboxPkgDownBtn.gridy = 0;
			constraintsOutboxPkgDownBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			getOutboxPkgPnl().add(getOutboxPkgDownBtn(), constraintsOutboxPkgDownBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxPkgPnl;
}
/**
 * Return the OutboxPkgSortTM property value.
 * @return TableSorter
 */
private TableSorter getOutboxPkgSortTM() {
	if (ivjOutboxPkgSortTM == null) {
		try {
			ivjOutboxPkgSortTM = new TableSorter();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxPkgSortTM;
}
/**
 * Return the OutboxPkgSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getOutboxPkgSP() {
	if (ivjOutboxPkgSP == null) {
		try {
			ivjOutboxPkgSP = new javax.swing.JScrollPane();
			ivjOutboxPkgSP.setName("OutboxPkgSP");
			ivjOutboxPkgSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjOutboxPkgSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getOutboxPkgSP().setViewportView(getOutboxPkgTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxPkgSP;
}
/**
 * Return the OutboxPkgTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getOutboxPkgTbl() {
	if (ivjOutboxPkgTbl == null) {
		try {
			ivjOutboxPkgTbl = new javax.swing.JTable();
			ivjOutboxPkgTbl.setName("OutboxPkgTbl");
			getOutboxPkgSP().setColumnHeaderView(ivjOutboxPkgTbl.getTableHeader());
			ivjOutboxPkgTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjOutboxPkgTbl.setBounds(0, 0, 200, 200);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxPkgTbl;
}
/**
 * Return the OutboxPkgTM property value.
 * @return OutboxPkgTM
 */
private OutboxPkgTM getOutboxPkgTM() {
	if (ivjOutboxPkgTM == null) {
		try {
			ivjOutboxPkgTM = new OutboxPkgTM();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxPkgTM;
}
/**
 * Return the OutboxPkgViewBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOutboxPkgViewBtn() {
	if (ivjOutboxPkgViewBtn == null) {
		try {
			ivjOutboxPkgViewBtn = new javax.swing.JButton();
			ivjOutboxPkgViewBtn.setName("OutboxPkgViewBtn");
			ivjOutboxPkgViewBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/propview.gif")));
			ivjOutboxPkgViewBtn.setToolTipText("View package recipients");
			ivjOutboxPkgViewBtn.setText("");
			ivjOutboxPkgViewBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjOutboxPkgViewBtn.addActionListener(pkgAclsListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxPkgViewBtn;
}
/**
 * Return the OutboxPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getOutboxPnl() {
	if (ivjOutboxPnl == null) {
		try {
			ivjOutboxPnl = new javax.swing.JPanel();
			ivjOutboxPnl.setName("OutboxPnl");
			ivjOutboxPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOutboxPkgPnl = new java.awt.GridBagConstraints();
			constraintsOutboxPkgPnl.gridx = 0; constraintsOutboxPkgPnl.gridy = 0;
			constraintsOutboxPkgPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOutboxPkgPnl.insets = new java.awt.Insets(5, 5, 0, 5);
			getOutboxPnl().add(getOutboxPkgPnl(), constraintsOutboxPkgPnl);

			java.awt.GridBagConstraints constraintsOutboxPkgSP = new java.awt.GridBagConstraints();
			constraintsOutboxPkgSP.gridx = 0; constraintsOutboxPkgSP.gridy = 1;
			constraintsOutboxPkgSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOutboxPkgSP.weightx = 1.0;
			constraintsOutboxPkgSP.weighty = 1.0;
			constraintsOutboxPkgSP.insets = new java.awt.Insets(0, 5, 0, 5);
			getOutboxPnl().add(getOutboxPkgSP(), constraintsOutboxPkgSP);

			java.awt.GridBagConstraints constraintsOutboxFilePnl = new java.awt.GridBagConstraints();
			constraintsOutboxFilePnl.gridx = 0; constraintsOutboxFilePnl.gridy = 2;
			constraintsOutboxFilePnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOutboxFilePnl.insets = new java.awt.Insets(10, 5, 0, 5);
			getOutboxPnl().add(getOutboxFilePnl(), constraintsOutboxFilePnl);

			java.awt.GridBagConstraints constraintsOutboxFileSP = new java.awt.GridBagConstraints();
			constraintsOutboxFileSP.gridx = 0; constraintsOutboxFileSP.gridy = 3;
			constraintsOutboxFileSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOutboxFileSP.weightx = 1.0;
			constraintsOutboxFileSP.weighty = 1.0;
			constraintsOutboxFileSP.insets = new java.awt.Insets(0, 5, 5, 5);
			getOutboxPnl().add(getOutboxFileSP(), constraintsOutboxFileSP);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxPnl;
}
/**
 * Return the OutboxRefreshBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOutboxRefreshBtn() {
	if (ivjOutboxRefreshBtn == null) {
		try {
			ivjOutboxRefreshBtn = new javax.swing.JButton();
			ivjOutboxRefreshBtn.setName("OutboxRefreshBtn");
			ivjOutboxRefreshBtn.setToolTipText("Refresh Sent Package List");
			ivjOutboxRefreshBtn.setText("");
			ivjOutboxRefreshBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjOutboxRefreshBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjOutboxRefreshBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/refresh.gif")));
			ivjOutboxRefreshBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjOutboxRefreshBtn.addActionListener(outboxRefreshListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxRefreshBtn;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOutboxTransferBtn() {
	if (ivjOutboxTransferBtn == null) {
		try {
			ivjOutboxTransferBtn = new javax.swing.JButton();
			ivjOutboxTransferBtn.setName("OutboxTransferBtn");
			ivjOutboxTransferBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/fwdfile.gif")));
			ivjOutboxTransferBtn.setToolTipText("Forward the selected files");
			ivjOutboxTransferBtn.setText("");
			ivjOutboxTransferBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjOutboxTransferBtn.addActionListener(forwardFilesActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutboxTransferBtn;
}
/**
 * Return the PkgAclViewBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getPkgAclViewBtn() {
	if (ivjPkgAclViewBtn == null) {
		try {
			ivjPkgAclViewBtn = new javax.swing.JButton();
			ivjPkgAclViewBtn.setName("PkgAclViewBtn");
			ivjPkgAclViewBtn.setToolTipText("Close this window");
			ivjPkgAclViewBtn.setText("Close");
			ivjPkgAclViewBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getPkgAclViewDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAclViewBtn;
}
/**
 * Return the JDialogContentPane31 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getPkgAclViewCP() {
	if (ivjPkgAclViewCP == null) {
		try {
			ivjPkgAclViewCP = new javax.swing.JPanel();
			ivjPkgAclViewCP.setName("PkgAclViewCP");
			ivjPkgAclViewCP.setLayout(new java.awt.GridBagLayout());
			ivjPkgAclViewCP.setBounds(624, 1616, 349, 238);

			java.awt.GridBagConstraints constraintsPkgAclViewBtn = new java.awt.GridBagConstraints();
			constraintsPkgAclViewBtn.gridx = 0; constraintsPkgAclViewBtn.gridy = 2;
			constraintsPkgAclViewBtn.gridwidth = 0;
			constraintsPkgAclViewBtn.insets = new java.awt.Insets(5, 5, 5, 5);
			getPkgAclViewCP().add(getPkgAclViewBtn(), constraintsPkgAclViewBtn);

			java.awt.GridBagConstraints constraintsPkgAclViewSP = new java.awt.GridBagConstraints();
			constraintsPkgAclViewSP.gridx = 0; constraintsPkgAclViewSP.gridy = 1;
			constraintsPkgAclViewSP.gridwidth = 0;
			constraintsPkgAclViewSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPkgAclViewSP.weightx = 1.0;
			constraintsPkgAclViewSP.weighty = 1.0;
			constraintsPkgAclViewSP.insets = new java.awt.Insets(5, 5, 5, 5);
			getPkgAclViewCP().add(getPkgAclViewSP(), constraintsPkgAclViewSP);

			java.awt.GridBagConstraints constraintsPkgAclViewLbl = new java.awt.GridBagConstraints();
			constraintsPkgAclViewLbl.gridx = 0; constraintsPkgAclViewLbl.gridy = 0;
			constraintsPkgAclViewLbl.insets = new java.awt.Insets(5, 5, 0, 4);
			getPkgAclViewCP().add(getPkgAclViewLbl(), constraintsPkgAclViewLbl);

			java.awt.GridBagConstraints constraintsPkgAclViewNameLbl = new java.awt.GridBagConstraints();
			constraintsPkgAclViewNameLbl.gridx = 1; constraintsPkgAclViewNameLbl.gridy = 0;
			constraintsPkgAclViewNameLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPkgAclViewNameLbl.insets = new java.awt.Insets(5, 0, 0, 5);
			getPkgAclViewCP().add(getPkgAclViewNameLbl(), constraintsPkgAclViewNameLbl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAclViewCP;
}
/**
 * Return the PkgAclViewDlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getPkgAclViewDlg() {
	if (ivjPkgAclViewDlg == null) {
		try {
			ivjPkgAclViewDlg = new javax.swing.JDialog(ownerContainer);
			ivjPkgAclViewDlg.setName("PkgAclViewDlg2");
			ivjPkgAclViewDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjPkgAclViewDlg.setBounds(1049, 2167, 349, 238);
			ivjPkgAclViewDlg.setModal(false);
			ivjPkgAclViewDlg.setTitle("Package Access Information");
			ivjPkgAclViewDlg.setContentPane(getPkgAclViewCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAclViewDlg;
}
/**
 * Return the PkgAclViewLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPkgAclViewLbl() {
	if (ivjPkgAclViewLbl == null) {
		try {
			ivjPkgAclViewLbl = new javax.swing.JLabel();
			ivjPkgAclViewLbl.setName("PkgAclViewLbl");
			ivjPkgAclViewLbl.setText("Access for:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAclViewLbl;
}
/**
 * Return the PkgAclViewNameLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPkgAclViewNameLbl() {
	if (ivjPkgAclViewNameLbl == null) {
		try {
			ivjPkgAclViewNameLbl = new javax.swing.JLabel();
			ivjPkgAclViewNameLbl.setName("PkgAclViewNameLbl");
			ivjPkgAclViewNameLbl.setText("JLabel6");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAclViewNameLbl;
}
/**
 * Return the PkgAclViewSortTM property value.
 * @return TableSorter
 */
private TableSorter getPkgAclViewSortTM() {
	if (ivjPkgAclViewSortTM == null) {
		try {
			ivjPkgAclViewSortTM = new TableSorter();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAclViewSortTM;
}
/**
 * Return the PkgAclViewSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getPkgAclViewSP() {
	if (ivjPkgAclViewSP == null) {
		try {
			ivjPkgAclViewSP = new javax.swing.JScrollPane();
			ivjPkgAclViewSP.setName("PkgAclViewSP");
			ivjPkgAclViewSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjPkgAclViewSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getPkgAclViewSP().setViewportView(getPkgAclViewTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAclViewSP;
}
/**
 * Return the PkgAclViewTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getPkgAclViewTbl() {
	if (ivjPkgAclViewTbl == null) {
		try {
			ivjPkgAclViewTbl = new javax.swing.JTable();
			ivjPkgAclViewTbl.setName("PkgAclViewTbl");
			getPkgAclViewSP().setColumnHeaderView(ivjPkgAclViewTbl.getTableHeader());
			ivjPkgAclViewTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjPkgAclViewTbl.setBounds(0, 0, 200, 200);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAclViewTbl;
}
/**
 * Return the PkgAclsTM property value.
 * @return oem.edge.ed.odc.dropbox.client.PkgAclsTM
 */
private PkgAclsTM getPkgAclViewTM() {
	if (ivjPkgAclViewTM == null) {
		try {
			ivjPkgAclViewTM = new PkgAclsTM();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAclViewTM;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getPkgAddFilesBtn() {
	if (ivjPkgAddFilesBtn == null) {
		try {
			ivjPkgAddFilesBtn = new javax.swing.JButton();
			ivjPkgAddFilesBtn.setName("PkgAddFilesBtn");
			ivjPkgAddFilesBtn.setText("Add Files");
			ivjPkgAddFilesBtn.addActionListener(new BtnPressListener(getPkgEditDlg()));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAddFilesBtn;
}
/**
 * Return the PkgAddFilesInfoCB property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getPkgAddFilesInfoCB() {
	if (ivjPkgAddFilesInfoCB == null) {
		try {
			ivjPkgAddFilesInfoCB = new javax.swing.JCheckBox();
			ivjPkgAddFilesInfoCB.setName("PkgAddFilesInfoCB");
			ivjPkgAddFilesInfoCB.setText("Do not show me this tip again");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAddFilesInfoCB;
}
/**
 * Return the PkgAddFilesInfoCP property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getPkgAddFilesInfoCP() {
	if (ivjPkgAddFilesInfoCP == null) {
		try {
			ivjPkgAddFilesInfoCP = new javax.swing.JPanel();
			ivjPkgAddFilesInfoCP.setName("PkgAddFilesInfoCP");
			ivjPkgAddFilesInfoCP.setLayout(new java.awt.GridBagLayout());
			ivjPkgAddFilesInfoCP.setBounds(74, 2002, 355, 240);

			java.awt.GridBagConstraints constraintsPkgAddFilesInfoTA = new java.awt.GridBagConstraints();
			constraintsPkgAddFilesInfoTA.gridx = 0; constraintsPkgAddFilesInfoTA.gridy = 0;
			constraintsPkgAddFilesInfoTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPkgAddFilesInfoTA.weightx = 1.0;
			constraintsPkgAddFilesInfoTA.weighty = 1.0;
			constraintsPkgAddFilesInfoTA.insets = new java.awt.Insets(5, 5, 5, 5);
			getPkgAddFilesInfoCP().add(getPkgAddFilesInfoTA(), constraintsPkgAddFilesInfoTA);

			java.awt.GridBagConstraints constraintsPkgAddFilesInfoCB = new java.awt.GridBagConstraints();
			constraintsPkgAddFilesInfoCB.gridx = 0; constraintsPkgAddFilesInfoCB.gridy = 1;
			constraintsPkgAddFilesInfoCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPkgAddFilesInfoCB.insets = new java.awt.Insets(5, 5, 5, 5);
			getPkgAddFilesInfoCP().add(getPkgAddFilesInfoCB(), constraintsPkgAddFilesInfoCB);

			java.awt.GridBagConstraints constraintsPkgAddFilesInfoOkBtn = new java.awt.GridBagConstraints();
			constraintsPkgAddFilesInfoOkBtn.gridx = 0; constraintsPkgAddFilesInfoOkBtn.gridy = 2;
			constraintsPkgAddFilesInfoOkBtn.insets = new java.awt.Insets(0, 5, 5, 5);
			getPkgAddFilesInfoCP().add(getPkgAddFilesInfoOkBtn(), constraintsPkgAddFilesInfoOkBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAddFilesInfoCP;
}
/**
 * Return the PkgAddFilesInfoDlg1 property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getPkgAddFilesInfoDlg() {
	if (ivjPkgAddFilesInfoDlg == null) {
		try {
			ivjPkgAddFilesInfoDlg = new javax.swing.JDialog(ownerContainer);
			ivjPkgAddFilesInfoDlg.setName("PkgAddFilesInfoDlg");
			ivjPkgAddFilesInfoDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjPkgAddFilesInfoDlg.setBounds(639, 2035, 355, 240);
			ivjPkgAddFilesInfoDlg.setModal(true);
			ivjPkgAddFilesInfoDlg.setTitle("Adding Files");
			getPkgAddFilesInfoDlg().setContentPane(getPkgAddFilesInfoCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAddFilesInfoDlg;
}
/**
 * Return the PkgAddFilesInfoOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getPkgAddFilesInfoOkBtn() {
	if (ivjPkgAddFilesInfoOkBtn == null) {
		try {
			ivjPkgAddFilesInfoOkBtn = new javax.swing.JButton();
			ivjPkgAddFilesInfoOkBtn.setName("PkgAddFilesInfoOkBtn");
			ivjPkgAddFilesInfoOkBtn.setText("Close");
			ivjPkgAddFilesInfoOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						closeAddFilesTip();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}  
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAddFilesInfoOkBtn;
}
/**
 * Return the PkgAddFilesInfoTA property value.
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getPkgAddFilesInfoTA() {
	if (ivjPkgAddFilesInfoTA == null) {
		try {
			ivjPkgAddFilesInfoTA = new javax.swing.JTextArea();
			ivjPkgAddFilesInfoTA.setName("PkgAddFilesInfoTA");
			ivjPkgAddFilesInfoTA.setLineWrap(true);
			ivjPkgAddFilesInfoTA.setWrapStyleWord(true);
			ivjPkgAddFilesInfoTA.setText("You may add files to this new package by selecting files or directories from the local file panel and then pressing the add files to package button (right arrow). You may also transfer files found in received packages and previously sent packages by selecting those files and then pressing the add files to package button (right arrow).");
			ivjPkgAddFilesInfoTA.setBackground(new java.awt.Color(204,204,204));
			ivjPkgAddFilesInfoTA.setEditable(false);
			ivjPkgAddFilesInfoTA.setEnabled(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAddFilesInfoTA;
}
/**
 * Return the PkgAddSenderMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPkgAddSenderMI() {
	if (ivjPkgAddSenderMI == null) {
		try {
			ivjPkgAddSenderMI = new javax.swing.JMenuItem();
			ivjPkgAddSenderMI.setName("PkgAddSenderMI");
			ivjPkgAddSenderMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/buddy.gif")));
			ivjPkgAddSenderMI.setToolTipText("Add sender of package to your saved user list");
			ivjPkgAddSenderMI.setText("Add sender");
			ivjPkgAddSenderMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						addSender();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgAddSenderMI;
}
/**
 * Return the PkgBF property value.
 * @return javax.swing.BorderFactory
 */
private javax.swing.BorderFactory getPkgBF() {
	return ivjPkgBF;
}
/**
 * Return the PkgCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getPkgCanBtn() {
	if (ivjPkgCanBtn == null) {
		try {
			ivjPkgCanBtn = new javax.swing.JButton();
			ivjPkgCanBtn.setName("PkgCanBtn");
			ivjPkgCanBtn.setText("Cancel");
			ivjPkgCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getPkgEditDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgCanBtn;
}
/**
 * Return the PkgCreateMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPkgCreateMI() {
	if (ivjPkgCreateMI == null) {
		try {
			ivjPkgCreateMI = new javax.swing.JMenuItem();
			ivjPkgCreateMI.setName("PkgCreateMI");
			ivjPkgCreateMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/newfold.gif")));
			ivjPkgCreateMI.setMnemonic('c');
			ivjPkgCreateMI.setText("Create new");
			ivjPkgCreateMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT,0,false));
			ivjPkgCreateMI.addActionListener(createPkgActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgCreateMI;
}
/**
 * Return the PkgDeleteMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPkgDeleteMI() {
	if (ivjPkgDeleteMI == null) {
		try {
			ivjPkgDeleteMI = new javax.swing.JMenuItem();
			ivjPkgDeleteMI.setName("PkgDeleteMI");
			ivjPkgDeleteMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/trash.gif")));
			ivjPkgDeleteMI.setMnemonic('l');
			ivjPkgDeleteMI.setText("Delete");
			ivjPkgDeleteMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0,false));
			ivjPkgDeleteMI.addActionListener(deleteListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgDeleteMI;
}
/**
 * Return the PkgDeliverMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPkgDeliverMI() {
	if (ivjPkgDeliverMI == null) {
		try {
			ivjPkgDeliverMI = new javax.swing.JMenuItem();
			ivjPkgDeliverMI.setName("PkgDeliverMI");
			ivjPkgDeliverMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/sendmail.gif")));
			ivjPkgDeliverMI.setMnemonic('s');
			ivjPkgDeliverMI.setText("Send");
			ivjPkgDeliverMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK,false));
			ivjPkgDeliverMI.addActionListener(commitListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgDeliverMI;
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPkgDescLbl() {
	if (pkgDescLbl == null) {
		try {
			pkgDescLbl = new javax.swing.JLabel();
			pkgDescLbl.setName("pkgDescLbl");
			pkgDescLbl.setText("Description: (" + DESC_MAX + " chars max)");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return pkgDescLbl;
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPkgDescCharLbl() {
	if (pkgDescCharLbl == null) {
		try {
			pkgDescCharLbl = new javax.swing.JLabel();
			pkgDescCharLbl.setName("pkgDescCharLbl");
			pkgDescCharLbl.setText("0");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return pkgDescCharLbl;
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPkgDescLbl2() {
	if (pkgDescLbl2 == null) {
		try {
			pkgDescLbl2 = new javax.swing.JLabel();
			pkgDescLbl2.setName("pkgDescLbl2");
			pkgDescLbl2.setText("chars used");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return pkgDescLbl2;
}
/**
 * This method initializes pkgDescPnl	
 * 	
 * @return javax.swing.JPanel	
 */    
private JPanel getPkgDescPnl() {
	if (pkgDescPnl == null) {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		pkgDescPnl = new JPanel();
		pkgDescPnl.setLayout(new GridBagLayout());
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.gridwidth = 0;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.weighty = 1.0;
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.insets = new java.awt.Insets(1,2,2,2);
		pkgDescPnl.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		pkgDescPnl.setMinimumSize(new java.awt.Dimension(161,80));
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints3.insets = new java.awt.Insets(1,2,0,2);
		gridBagConstraints4.gridx = 2;
		gridBagConstraints4.gridy = 0;
		gridBagConstraints4.insets = new java.awt.Insets(1,0,0,2);
		gridBagConstraints5.gridx = 1;
		gridBagConstraints5.gridy = 0;
		gridBagConstraints5.weightx = 1.0D;
		gridBagConstraints5.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints5.insets = new java.awt.Insets(1,0,0,2);
		pkgDescPnl.add(getPkgDescSP(), gridBagConstraints2);
		pkgDescPnl.add(getPkgDescLbl(), gridBagConstraints3);
		pkgDescPnl.add(getPkgDescLbl2(), gridBagConstraints4);
		pkgDescPnl.add(getPkgDescCharLbl(), gridBagConstraints5);
	}
	return pkgDescPnl;
}
/**
 * This method initializes jScrollPane
 * 
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getPkgDescSP() {
	if(pkgDescSP == null) {
		pkgDescSP = new javax.swing.JScrollPane();
		pkgDescSP.setViewportView(getPkgDescTA());
		pkgDescSP.setPreferredSize(new java.awt.Dimension(33,60));
	}
	return pkgDescSP;
}
/**
 * This method initializes jTextArea
 * 
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getPkgDescTA() {
	if(pkgDescTA == null) {
		pkgDescTA = new javax.swing.JTextArea();
		pkgDescTA.setLineWrap(true);
		pkgDescTA.setWrapStyleWord(true);
	}
	return pkgDescTA;
}
/**
 * Return the PkgDownMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPkgDownMI() {
	if (ivjPkgDownMI == null) {
		try {
			ivjPkgDownMI = new javax.swing.JMenuItem();
			ivjPkgDownMI.setName("PkgDownMI");
			ivjPkgDownMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/download.gif")));
			ivjPkgDownMI.setMnemonic('d');
			ivjPkgDownMI.setText("Download");
			ivjPkgDownMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.CTRL_MASK,false));
			ivjPkgDownMI.addActionListener(downloadPkgHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgDownMI;
}
/**
 * Return the JDialogContentPane2 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getPkgEditCP() {
	if (ivjPkgEditCP == null) {
		try {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			ivjPkgEditCP = new javax.swing.JPanel();
			ivjPkgEditCP.setName("PkgEditCP");
			ivjPkgEditCP.setLayout(new java.awt.GridBagLayout());
			ivjPkgEditCP.setBounds(343, 2793, 424, 581);

			java.awt.GridBagConstraints constraintsPkgNameLbl = new java.awt.GridBagConstraints();
			constraintsPkgNameLbl.gridx = 0; constraintsPkgNameLbl.gridy = 0;
			constraintsPkgNameLbl.insets = new java.awt.Insets(5, 5, 5, 5);
			java.awt.GridBagConstraints constraintsPkgNameTF = new java.awt.GridBagConstraints();
			constraintsPkgNameTF.gridx = 1; constraintsPkgNameTF.gridy = 0;
			constraintsPkgNameTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPkgNameTF.weightx = 1.0;
			constraintsPkgNameTF.insets = new java.awt.Insets(5, 0, 5, 5);
			java.awt.GridBagConstraints constraintsRecPnl = new java.awt.GridBagConstraints();
			constraintsRecPnl.gridx = 0; constraintsRecPnl.gridy = 4;
			constraintsRecPnl.gridwidth = 0;
			constraintsRecPnl.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRecPnl.weightx = 1.0;
			constraintsRecPnl.weighty = 1.0;
			constraintsRecPnl.insets = new java.awt.Insets(0, 5, 5, 5);
			java.awt.GridBagConstraints constraintsExpirePnl = new java.awt.GridBagConstraints();
			constraintsExpirePnl.gridx = 0; constraintsExpirePnl.gridy = 5;
			constraintsExpirePnl.gridwidth = 0;
			constraintsExpirePnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsExpirePnl.insets = new java.awt.Insets(0, 5, 5, 5);
			java.awt.GridBagConstraints constraintsSendEmailCB = new java.awt.GridBagConstraints();
			constraintsSendEmailCB.gridx = 0; constraintsSendEmailCB.gridy = 7;
			constraintsSendEmailCB.gridwidth = 0;
			constraintsSendEmailCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSendEmailCB.insets = new java.awt.Insets(0, 5, 3, 5);
			java.awt.GridBagConstraints constraintsReturnReceiptCB = new java.awt.GridBagConstraints();
			constraintsReturnReceiptCB.gridx = 0; constraintsReturnReceiptCB.gridy = 8;
			constraintsReturnReceiptCB.gridwidth = 0;
			constraintsReturnReceiptCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsReturnReceiptCB.insets = new java.awt.Insets(0, 5, 3, 5);
			java.awt.GridBagConstraints constraintsExpressCB = new java.awt.GridBagConstraints();
			constraintsExpressCB.gridx = 0; constraintsExpressCB.gridy = 9;
			constraintsExpressCB.gridwidth = 0;
			constraintsExpressCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsExpressCB.insets = new java.awt.Insets(0, 5, 5, 5);
			java.awt.GridBagConstraints constraintsJPanel4 = new java.awt.GridBagConstraints();
			constraintsJPanel4.gridx = 0; constraintsJPanel4.gridy = 10;
			constraintsJPanel4.gridwidth = 0;
			constraintsJPanel4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel4.weightx = 1.0;
			constraintsJPanel4.insets = new java.awt.Insets(0, 5, 5, 5);
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.gridwidth = 0;
			gridBagConstraints1.insets = new java.awt.Insets(0,7,0,5);
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 2;
			gridBagConstraints11.insets = new java.awt.Insets(0,5,5,5);
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridwidth = 0;
			gridBagConstraints11.weighty = 0.0D;
			gridBagConstraints11.ipady = 0;
			ivjPkgEditCP.add(getPkgNameLbl(), constraintsPkgNameLbl);
			ivjPkgEditCP.add(getPkgNameTF(), constraintsPkgNameTF);
			ivjPkgEditCP.add(getRecPnl(), constraintsRecPnl);
			ivjPkgEditCP.add(getExpirePnl(), constraintsExpirePnl);
			ivjPkgEditCP.add(getSendEmailCB(), constraintsSendEmailCB);
			ivjPkgEditCP.add(getReturnReceiptCB(), constraintsReturnReceiptCB);
			ivjPkgEditCP.add(getExpressCB(), constraintsExpressCB);
			ivjPkgEditCP.add(getJPanel4(), constraintsJPanel4);
			ivjPkgEditCP.add(getPkgItarCB(), gridBagConstraints1);
			ivjPkgEditCP.add(getPkgDescPnl(), gridBagConstraints11);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgEditCP;
}
/**
 * Return the JDialog3 property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getPkgEditDlg() {
	if (ivjPkgEditDlg == null) {
		try {
			ivjPkgEditDlg = new javax.swing.JDialog(ownerContainer);
			ivjPkgEditDlg.setName("PkgEditDlg2");
			ivjPkgEditDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjPkgEditDlg.setBounds(1063, 1360, 509, 650);
			ivjPkgEditDlg.setModal(true);
			ivjPkgEditDlg.setTitle("Update Package Info");
			ivjPkgEditDlg.setContentPane(getPkgEditCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgEditDlg;
}
/**
 * Return the PkgEditMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPkgEditMI() {
	if (ivjPkgEditMI == null) {
		try {
			ivjPkgEditMI = new javax.swing.JMenuItem();
			ivjPkgEditMI.setName("PkgEditMI");
			ivjPkgEditMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/property.gif")));
			ivjPkgEditMI.setMnemonic('e');
			ivjPkgEditMI.setText("Edit recipients");
			ivjPkgEditMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_MASK,false));
			ivjPkgEditMI.addActionListener(editPkgActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgEditMI;
}
/**
 * Return the PkgFwdMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPkgFwdMI() {
	if (ivjPkgFwdMI == null) {
		try {
			ivjPkgFwdMI = new javax.swing.JMenuItem();
			ivjPkgFwdMI.setName("PkgFwdMI");
			ivjPkgFwdMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/fwdpkg.gif")));
			ivjPkgFwdMI.setMnemonic('f');
			ivjPkgFwdMI.setText("Forward");
			ivjPkgFwdMI.addActionListener(forwardPkgActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgFwdMI;
}
/**
 * This method initializes pkgItarCB	
 * 	
 * @return javax.swing.JCheckBox	
 */    
private JCheckBox getPkgItarCB() {
	if (pkgItarCB == null) {
		pkgItarCB = new JCheckBox();
		pkgItarCB.setText("Contains ITAR sensitive data");
		pkgItarCB.addItemListener(itarListener);
	}
	return pkgItarCB;
}
/**
 * Return the PkgM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getPkgM() {
	if (ivjPkgM == null) {
		try {
			ivjPkgM = new javax.swing.JMenu();
			ivjPkgM.add(getPkgViewDescMI());
			ivjPkgM.setName("PkgM");
			ivjPkgM.setMnemonic('p');
			ivjPkgM.setText("Packages");
			ivjPkgM.add(getPkgAddSenderMI());
			ivjPkgM.add(getPkgDownMI());
			ivjPkgM.add(getPkgFwdMI());
			ivjPkgM.add(getPkgDeleteMI());
			ivjPkgM.add(getJSeparator8());
			ivjPkgM.add(getPkgViewMI());
			ivjPkgM.add(getPkgEditMI());
			ivjPkgM.add(getJSeparator9());
			ivjPkgM.add(getPkgCreateMI());
			ivjPkgM.add(getPkgDeliverMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgM;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPkgNameLbl() {
	if (ivjPkgNameLbl == null) {
		try {
			ivjPkgNameLbl = new javax.swing.JLabel();
			ivjPkgNameLbl.setName("PkgNameLbl");
			ivjPkgNameLbl.setText("Package Name:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgNameLbl;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getPkgNameTF() {
	if (ivjPkgNameTF == null) {
		try {
			ivjPkgNameTF = new javax.swing.JTextField();
			ivjPkgNameTF.setName("PkgNameTF");
			ivjPkgNameTF.setToolTipText("Enter package name");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgNameTF;
}
/**
 * Return the PkgOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getPkgOkBtn() {
	if (ivjPkgOkBtn == null) {
		try {
			ivjPkgOkBtn = new javax.swing.JButton();
			ivjPkgOkBtn.setName("PkgOkBtn");
			ivjPkgOkBtn.setText("Save & Close");
			ivjPkgOkBtn.addActionListener(new BtnPressListener(getPkgEditDlg()));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgOkBtn;
}
/**
 * This method initializes pkgViewDescMI
 * 
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPkgViewDescMI() {
	if(pkgViewDescMI == null) {
		pkgViewDescMI = new javax.swing.JMenuItem();
		pkgViewDescMI.setText("View Description");
		pkgViewDescMI.setName("");
		pkgViewDescMI.setToolTipText("View Package Description");
		pkgViewDescMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/new_mail.gif")));
		pkgViewDescMI.addActionListener(viewDescriptionListener);
	}
	return pkgViewDescMI;
}
/**
 * Return the PkgViewMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPkgViewMI() {
	if (ivjPkgViewMI == null) {
		try {
			ivjPkgViewMI = new javax.swing.JMenuItem();
			ivjPkgViewMI.setName("PkgViewMI");
			ivjPkgViewMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/propview.gif")));
			ivjPkgViewMI.setMnemonic('v');
			ivjPkgViewMI.setText("View recipients");
			ivjPkgViewMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK,false));
			ivjPkgViewMI.addActionListener(pkgAclsListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPkgViewMI;
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
			ivjPwdTF.setToolTipText("Enter your password");
			ivjPwdTF.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getLoginOkBtn().doClick();
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPwdTF;
}
/**
 * Return the JPanel7 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getRecPnl() {
	if (ivjRecPnl == null) {
		try {
			crossCompanyLbl = new JLabel();
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			ivjRecPnl = new javax.swing.JPanel();
			ivjRecPnl.setName("RecPnl");
			ivjRecPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsToUserLbl = new java.awt.GridBagConstraints();
			constraintsToUserLbl.gridx = 0; constraintsToUserLbl.gridy = 1;
			constraintsToUserLbl.gridwidth = 2;
			constraintsToUserLbl.anchor = java.awt.GridBagConstraints.WEST;
      			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 2; constraintsJLabel2.gridy = 1;
			constraintsJLabel2.anchor = java.awt.GridBagConstraints.WEST;
			java.awt.GridBagConstraints constraintsEditToSP = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints2 = new java.awt.GridBagConstraints();
			consGridBagConstraints2.gridy = 2;
			consGridBagConstraints2.gridx = 0;
			consGridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints2.weighty = 1.0D;
			consGridBagConstraints2.insets = new java.awt.Insets(0,0,0,5);
			consGridBagConstraints2.weightx = 1.0D;
			constraintsEditToSP.gridx = 2; constraintsEditToSP.gridy = 2;
constraintsEditToSP.gridheight = 0;
			constraintsEditToSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsEditToSP.weightx = 1.0;
			constraintsEditToSP.weighty = 1.0;
			ivjRecPnl.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), "Recipients", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.gridy = 0;
			gridBagConstraints12.gridwidth = 0;
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints12.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
			crossCompanyLbl.setText("<html><p>Please note: this package's \"Send to:\" list contains multiple companies different from your own.</p></html>");
			ivjRecPnl.add(getToUserLbl(), constraintsToUserLbl);
			ivjRecPnl.add(getJLabel2(), constraintsJLabel2);
			ivjRecPnl.add(getEditToSP(), constraintsEditToSP);
			ivjRecPnl.add(getRecToPnl(), consGridBagConstraints2);
			ivjRecPnl.add(crossCompanyLbl, gridBagConstraints12);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRecPnl;
}
/**
 * This method initializes pkgDescPnl
 * 
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getRecToPnl() {
	if(recToPnl == null) {
		recToPnl = new javax.swing.JPanel();
		java.awt.GridBagConstraints consGridBagConstraints31 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints5 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints6 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints7 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints4 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints8 = new java.awt.GridBagConstraints();
		consGridBagConstraints31.insets = new java.awt.Insets(2, 0, 4, 0);
		consGridBagConstraints31.fill = java.awt.GridBagConstraints.HORIZONTAL;
		consGridBagConstraints31.gridy = 0;
		consGridBagConstraints31.gridx = 0;
		consGridBagConstraints5.insets = new java.awt.Insets(5, 0, 0, 5);
		consGridBagConstraints5.fill = java.awt.GridBagConstraints.BOTH;
		consGridBagConstraints5.gridwidth = 0;
		consGridBagConstraints5.gridy = 1;
		consGridBagConstraints5.gridx = 0;
		consGridBagConstraints8.insets = new java.awt.Insets(0,2,0,0);
		consGridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
		consGridBagConstraints8.gridy = 3;
		consGridBagConstraints8.gridx = 1;
		consGridBagConstraints6.insets = new java.awt.Insets(0, 0, 2, 5);
		consGridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
		consGridBagConstraints6.gridwidth = 0;
		consGridBagConstraints6.gridy = 2;
		consGridBagConstraints6.gridx = 0;
		consGridBagConstraints7.fill = java.awt.GridBagConstraints.BOTH;
		consGridBagConstraints7.weighty = 1.0;
		consGridBagConstraints7.weightx = 1.0;
		consGridBagConstraints7.gridy = 3;
		consGridBagConstraints7.gridx = 0;
		consGridBagConstraints4.insets = new java.awt.Insets(0, 2, 0, 5);
		consGridBagConstraints4.gridy = 0;
		consGridBagConstraints4.gridx = 1;
		recToPnl.setLayout(new java.awt.GridBagLayout());
		recToPnl.add(getToUserTF(), consGridBagConstraints31);
		recToPnl.add(getUserNewAddBtn(), consGridBagConstraints4);
		recToPnl.add(getJPanel3(), consGridBagConstraints5);
		recToPnl.add(getJPanel6(), consGridBagConstraints6);
		recToPnl.add(getEditFromSP(), consGridBagConstraints7);
		recToPnl.add(getEditBtnPnl(), consGridBagConstraints8);
		recToPnl.setMinimumSize(new java.awt.Dimension(100,150));
		recToPnl.setPreferredSize(new java.awt.Dimension(100,195));
	}
	return recToPnl;
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
			ivjRefreshMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/refresh.gif")));
			ivjRefreshMI.setMnemonic('r');
			ivjRefreshMI.setText("Refresh");
			ivjRefreshMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0,false));
			ivjRefreshMI.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == getRefreshMI() && getRemoteTP().getSelectedComponent() == getInboxPnl()) {
						inboxRefreshListener.actionPerformed(e);
					}
					else {
						outboxRefreshListener.actionPerformed(e);
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
 * Return the RemoteM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getRemoteM() {
	if (ivjRemoteM == null) {
		try {
			ivjRemoteM = new javax.swing.JMenu();
			ivjRemoteM.setName("RemoteM");
			ivjRemoteM.setText("Remote");
			ivjRemoteM.add(getRefreshMI());
			ivjRemoteM.add(getPkgM());
			ivjRemoteM.add(getFilesM());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteM;
}
/**
 * Return the RemoteTP property value.
 * @return javax.swing.JTabbedPane
 */
private javax.swing.JTabbedPane getRemoteTP() {
	if (ivjRemoteTP == null) {
		try {
			ivjRemoteTP = new javax.swing.JTabbedPane();
			ivjRemoteTP.setName("RemoteTP");
			ivjRemoteTP.setToolTipText("");
			ivjRemoteTP.insertTab("Packages Inbox", null, getInboxPnl(), "View packages sent to you", 0);
			ivjRemoteTP.insertTab("Sent Packages", null, getOutboxPnl(), "View packages sent by you", 1);
			ivjRemoteTP.insertTab("Send New Packages", null, getSendPnl(), "Send new packages to recipients", 2);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRemoteTP;
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
 * Return the ReturnReceiptCB property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getReturnReceiptCB() {
	if (ivjReturnReceiptCB == null) {
		try {
			ivjReturnReceiptCB = new javax.swing.JCheckBox();
			ivjReturnReceiptCB.setName("ReturnReceiptCB");
			ivjReturnReceiptCB.setText("Send me an e-mail as each recipient downloads this package");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjReturnReceiptCB;
}
/**
 * Return the SendCommitBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSendCommitBtn() {
	if (ivjSendCommitBtn == null) {
		try {
			ivjSendCommitBtn = new javax.swing.JButton();
			ivjSendCommitBtn.setName("SendCommitBtn");
			ivjSendCommitBtn.setToolTipText("Send the selected packages");
			ivjSendCommitBtn.setText("Send");
			ivjSendCommitBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/sendmail.gif")));
			ivjSendCommitBtn.setMargin(new java.awt.Insets(0, 0, 0, 2));
			ivjSendCommitBtn.addActionListener(commitListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendCommitBtn;
}
/**
 * Return the SendEmailCB property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getSendEmailCB() {
	if (ivjSendEmailCB == null) {
		try {
			ivjSendEmailCB = new javax.swing.JCheckBox();
			ivjSendEmailCB.setName("SendEmailCB");
			ivjSendEmailCB.setText("Send an e-mail to each recipient when a package is sent");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendEmailCB;
}
/**
 * Return the SendFileDeleteBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSendFileDeleteBtn() {
	if (ivjSendFileDeleteBtn == null) {
		try {
			ivjSendFileDeleteBtn = new javax.swing.JButton();
			ivjSendFileDeleteBtn.setName("SendFileDeleteBtn");
			ivjSendFileDeleteBtn.setToolTipText("Remove selected files from package");
			ivjSendFileDeleteBtn.setText("");
			ivjSendFileDeleteBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjSendFileDeleteBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjSendFileDeleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/trash.gif")));
			ivjSendFileDeleteBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjSendFileDeleteBtn.setEnabled(true);
			ivjSendFileDeleteBtn.addActionListener(deleteListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendFileDeleteBtn;
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getSendFileLbl() {
	if (ivjSendFileLbl == null) {
		try {
			ivjSendFileLbl = new javax.swing.JLabel();
			ivjSendFileLbl.setName("SendFileLbl");
			ivjSendFileLbl.setText("Files in the selected package:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendFileLbl;
}
/**
 * Return the JPanel6 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getSendFilePnl() {
	if (ivjSendFilePnl == null) {
		try {
			ivjSendFilePnl = new javax.swing.JPanel();
			ivjSendFilePnl.setName("SendFilePnl");
			ivjSendFilePnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSendFileLbl = new java.awt.GridBagConstraints();
			constraintsSendFileLbl.gridx = 0; constraintsSendFileLbl.gridy = 0;
			constraintsSendFileLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSendFileLbl.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsSendFileLbl.weightx = 1.0;
			getSendFilePnl().add(getSendFileLbl(), constraintsSendFileLbl);

			java.awt.GridBagConstraints constraintsSendFileRestartBtn = new java.awt.GridBagConstraints();
			constraintsSendFileRestartBtn.gridx = 1; constraintsSendFileRestartBtn.gridy = 0;
			constraintsSendFileRestartBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsSendFileRestartBtn.anchor = java.awt.GridBagConstraints.EAST;
			constraintsSendFileRestartBtn.weightx = 1.0;
			constraintsSendFileRestartBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getSendFilePnl().add(getSendFileRestartBtn(), constraintsSendFileRestartBtn);

			java.awt.GridBagConstraints constraintsSendFileDeleteBtn = new java.awt.GridBagConstraints();
			constraintsSendFileDeleteBtn.gridx = 2; constraintsSendFileDeleteBtn.gridy = 0;
			constraintsSendFileDeleteBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			getSendFilePnl().add(getSendFileDeleteBtn(), constraintsSendFileDeleteBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendFilePnl;
}
/**
 * Return the SendFileRestartBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSendFileRestartBtn() {
	if (ivjSendFileRestartBtn == null) {
		try {
			ivjSendFileRestartBtn = new javax.swing.JButton();
			ivjSendFileRestartBtn.setName("SendFileRestartBtn");
			ivjSendFileRestartBtn.setToolTipText("Restart addition of stopped files");
			ivjSendFileRestartBtn.setText("");
			ivjSendFileRestartBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjSendFileRestartBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjSendFileRestartBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/uprestrt.gif")));
			ivjSendFileRestartBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjSendFileRestartBtn.setEnabled(true);
			ivjSendFileRestartBtn.addActionListener(restartListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendFileRestartBtn;
}
/**
 * Return the SendFileSortTM property value.
 * @return TableSorter
 */
private TableSorter getSendFileSortTM() {
	if (ivjSendFileSortTM == null) {
		try {
			ivjSendFileSortTM = new TableSorter();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendFileSortTM;
}
/**
 * Return the SendFileSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getSendFileSP() {
	if (ivjSendFileSP == null) {
		try {
			ivjSendFileSP = new javax.swing.JScrollPane();
			ivjSendFileSP.setName("SendFileSP");
			ivjSendFileSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjSendFileSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getSendFileSP().setViewportView(getSendFileTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendFileSP;
}
/**
 * Return the SendFileTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getSendFileTbl() {
	if (ivjSendFileTbl == null) {
		try {
			ivjSendFileTbl = new javax.swing.JTable();
			ivjSendFileTbl.setName("SendFileTbl");
			getSendFileSP().setColumnHeaderView(ivjSendFileTbl.getTableHeader());
			ivjSendFileTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjSendFileTbl.setBounds(0, 0, 200, 200);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendFileTbl;
}
/**
 * Return the SendFileTM property value.
 * @return SendFileTM
 */
private SendFileTM getSendFileTM() {
	if (ivjSendFileTM == null) {
		try {
			ivjSendFileTM = new SendFileTM();
			ivjSendFileTM.addTableModelListener(sendFileListSelectionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendFileTM;
}
/**
 * Return the SendPkgCreateBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSendPkgCreateBtn() {
	if (ivjSendPkgCreateBtn == null) {
		try {
			ivjSendPkgCreateBtn = new javax.swing.JButton();
			ivjSendPkgCreateBtn.setName("SendPkgCreateBtn");
			ivjSendPkgCreateBtn.setToolTipText("Create new package");
			ivjSendPkgCreateBtn.setText("");
			ivjSendPkgCreateBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjSendPkgCreateBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjSendPkgCreateBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/newfold.gif")));
			ivjSendPkgCreateBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjSendPkgCreateBtn.addActionListener(createPkgActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendPkgCreateBtn;
}
/**
 * Return the SendPkgDeleteBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSendPkgDeleteBtn() {
	if (ivjSendPkgDeleteBtn == null) {
		try {
			ivjSendPkgDeleteBtn = new javax.swing.JButton();
			ivjSendPkgDeleteBtn.setName("SendPkgDeleteBtn");
			ivjSendPkgDeleteBtn.setToolTipText("Delete selected package");
			ivjSendPkgDeleteBtn.setText("");
			ivjSendPkgDeleteBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjSendPkgDeleteBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjSendPkgDeleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/trash.gif")));
			ivjSendPkgDeleteBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjSendPkgDeleteBtn.setEnabled(true);
			ivjSendPkgDeleteBtn.addActionListener(deleteListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendPkgDeleteBtn;
}
/**
 * Return the SendPkgEditBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSendPkgEditBtn() {
	if (ivjSendPkgEditBtn == null) {
		try {
			ivjSendPkgEditBtn = new javax.swing.JButton();
			ivjSendPkgEditBtn.setName("SendPkgEditBtn");
			ivjSendPkgEditBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/property.gif")));
			ivjSendPkgEditBtn.setToolTipText("Edit package recipients");
			ivjSendPkgEditBtn.setText("");
			ivjSendPkgEditBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjSendPkgEditBtn.addActionListener(editPkgActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendPkgEditBtn;
}
/**
 * Return the SendPkgLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getSendPkgLbl() {
	if (ivjSendPkgLbl == null) {
		try {
			ivjSendPkgLbl = new javax.swing.JLabel();
			ivjSendPkgLbl.setName("SendPkgLbl");
			ivjSendPkgLbl.setText("Packages being prepared for delivery:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendPkgLbl;
}
/**
 * Return the SendPkgPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getSendPkgPnl() {
	if (ivjSendPkgPnl == null) {
		try {
			ivjSendPkgPnl = new javax.swing.JPanel();
			ivjSendPkgPnl.setName("SendPkgPnl");
			ivjSendPkgPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSendPkgLbl = new java.awt.GridBagConstraints();
			constraintsSendPkgLbl.gridx = 0; constraintsSendPkgLbl.gridy = 0;
			constraintsSendPkgLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSendPkgLbl.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsSendPkgLbl.weightx = 1.0;
			getSendPkgPnl().add(getSendPkgLbl(), constraintsSendPkgLbl);

			java.awt.GridBagConstraints constraintsSendPkgRefreshBtn = new java.awt.GridBagConstraints();
			constraintsSendPkgRefreshBtn.gridx = 1; constraintsSendPkgRefreshBtn.gridy = 0;
			constraintsSendPkgRefreshBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsSendPkgRefreshBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getSendPkgPnl().add(getSendPkgRefreshBtn(), constraintsSendPkgRefreshBtn);

			java.awt.GridBagConstraints constraintsSendPkgCreateBtn = new java.awt.GridBagConstraints();
			constraintsSendPkgCreateBtn.gridx = 2; constraintsSendPkgCreateBtn.gridy = 0;
			constraintsSendPkgCreateBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsSendPkgCreateBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getSendPkgPnl().add(getSendPkgCreateBtn(), constraintsSendPkgCreateBtn);

			java.awt.GridBagConstraints constraintsSendPkgDeleteBtn = new java.awt.GridBagConstraints();
			constraintsSendPkgDeleteBtn.gridx = 3; constraintsSendPkgDeleteBtn.gridy = 0;
			constraintsSendPkgDeleteBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsSendPkgDeleteBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getSendPkgPnl().add(getSendPkgDeleteBtn(), constraintsSendPkgDeleteBtn);

			java.awt.GridBagConstraints constraintsSendPkgEditBtn = new java.awt.GridBagConstraints();
			constraintsSendPkgEditBtn.gridx = 4; constraintsSendPkgEditBtn.gridy = 0;
			constraintsSendPkgEditBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsSendPkgEditBtn.insets = new java.awt.Insets(0, 0, 0, 2);
			getSendPkgPnl().add(getSendPkgEditBtn(), constraintsSendPkgEditBtn);

			java.awt.GridBagConstraints constraintsSendCommitBtn = new java.awt.GridBagConstraints();
			constraintsSendCommitBtn.gridx = 5; constraintsSendCommitBtn.gridy = 0;
			constraintsSendCommitBtn.fill = java.awt.GridBagConstraints.VERTICAL;
			getSendPkgPnl().add(getSendCommitBtn(), constraintsSendCommitBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendPkgPnl;
}
/**
 * Return the SendPkgRefreshBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSendPkgRefreshBtn() {
	if (ivjSendPkgRefreshBtn == null) {
		try {
			ivjSendPkgRefreshBtn = new javax.swing.JButton();
			ivjSendPkgRefreshBtn.setName("SendPkgRefreshBtn");
			ivjSendPkgRefreshBtn.setToolTipText("Refresh new packages list");
			ivjSendPkgRefreshBtn.setText("");
			ivjSendPkgRefreshBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjSendPkgRefreshBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjSendPkgRefreshBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/refresh.gif")));
			ivjSendPkgRefreshBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjSendPkgRefreshBtn.addActionListener(outboxRefreshListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendPkgRefreshBtn;
}
/**
 * Return the SendPkgSortTM property value.
 * @return TableSorter
 */
private TableSorter getSendPkgSortTM() {
	if (ivjSendPkgSortTM == null) {
		try {
			ivjSendPkgSortTM = new TableSorter();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendPkgSortTM;
}
/**
 * Return the SendPkgSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getSendPkgSP() {
	if (ivjSendPkgSP == null) {
		try {
			ivjSendPkgSP = new javax.swing.JScrollPane();
			ivjSendPkgSP.setName("SendPkgSP");
			ivjSendPkgSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjSendPkgSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getSendPkgSP().setViewportView(getSendPkgTbl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendPkgSP;
}
/**
 * Return the SendPkgTbl property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getSendPkgTbl() {
	if (ivjSendPkgTbl == null) {
		try {
			ivjSendPkgTbl = new javax.swing.JTable();
			ivjSendPkgTbl.setName("SendPkgTbl");
			getSendPkgSP().setColumnHeaderView(ivjSendPkgTbl.getTableHeader());
			ivjSendPkgTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjSendPkgTbl.setBounds(0, 0, 200, 200);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendPkgTbl;
}
/**
 * Return the SendPkgTM property value.
 * @return SendPkgTM
 */
private SendPkgTM getSendPkgTM() {
	if (ivjSendPkgTM == null) {
		try {
			ivjSendPkgTM = new SendPkgTM();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendPkgTM;
}
/**
 * Return the SendPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getSendPnl() {
	if (ivjSendPnl == null) {
		try {
			ivjSendPnl = new javax.swing.JPanel();
			ivjSendPnl.setName("SendPnl");
			ivjSendPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSendPkgPnl = new java.awt.GridBagConstraints();
			constraintsSendPkgPnl.gridx = 0; constraintsSendPkgPnl.gridy = 0;
			constraintsSendPkgPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSendPkgPnl.insets = new java.awt.Insets(5, 5, 0, 5);
			getSendPnl().add(getSendPkgPnl(), constraintsSendPkgPnl);

			java.awt.GridBagConstraints constraintsSendPkgSP = new java.awt.GridBagConstraints();
			constraintsSendPkgSP.gridx = 0; constraintsSendPkgSP.gridy = 1;
			constraintsSendPkgSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSendPkgSP.weightx = 1.0;
			constraintsSendPkgSP.weighty = 1.0;
			constraintsSendPkgSP.insets = new java.awt.Insets(0, 5, 0, 5);
			getSendPnl().add(getSendPkgSP(), constraintsSendPkgSP);

			java.awt.GridBagConstraints constraintsSendFilePnl = new java.awt.GridBagConstraints();
			constraintsSendFilePnl.gridx = 0; constraintsSendFilePnl.gridy = 2;
			constraintsSendFilePnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSendFilePnl.insets = new java.awt.Insets(10, 5, 0, 5);
			getSendPnl().add(getSendFilePnl(), constraintsSendFilePnl);

			java.awt.GridBagConstraints constraintsSendFileSP = new java.awt.GridBagConstraints();
			constraintsSendFileSP.gridx = 0; constraintsSendFileSP.gridy = 3;
			constraintsSendFileSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSendFileSP.weightx = 1.0;
			constraintsSendFileSP.weighty = 1.0;
			constraintsSendFileSP.insets = new java.awt.Insets(0, 5, 5, 5);
			getSendPnl().add(getSendFileSP(), constraintsSendFileSP);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendPnl;
}
/**
 * Return the StatCanMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getStatCanMI() {
	if (ivjStatCanMI == null) {
		try {
			ivjStatCanMI = new javax.swing.JMenuItem();
			ivjStatCanMI.setName("StatCanMI");
			ivjStatCanMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/stop.gif")));
			ivjStatCanMI.setMnemonic('c');
			ivjStatCanMI.setText("Cancel Transfer");
			ivjStatCanMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK,false));
			ivjStatCanMI.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjStatCanMI;
}
/**
 * Return the StatDetailsMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getStatDetailsMI() {
	if (ivjStatDetailsMI == null) {
		try {
			ivjStatDetailsMI = new javax.swing.JMenuItem();
			ivjStatDetailsMI.setName("StatDetailsMI");
			ivjStatDetailsMI.setMnemonic('s');
			ivjStatDetailsMI.setText("Show Failed Details");
			ivjStatDetailsMI.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjStatDetailsMI;
}
/**
 * Return the StatRemAllMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getStatRemAllMI() {
	if (ivjStatRemAllMI == null) {
		try {
			ivjStatRemAllMI = new javax.swing.JMenuItem();
			ivjStatRemAllMI.setName("StatRemAllMI");
			ivjStatRemAllMI.setMnemonic('a');
			ivjStatRemAllMI.setText("Remove All Completed");
			ivjStatRemAllMI.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjStatRemAllMI;
}
/**
 * Return the StatRemSelMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getStatRemSelMI() {
	if (ivjStatRemSelMI == null) {
		try {
			ivjStatRemSelMI = new javax.swing.JMenuItem();
			ivjStatRemSelMI.setName("StatRemSelMI");
			ivjStatRemSelMI.setMnemonic('r');
			ivjStatRemSelMI.setText("Remove Selected");
			ivjStatRemSelMI.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjStatRemSelMI;
}
/**
 * Return the StatusM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getStatusM() {
	if (ivjStatusM == null) {
		try {
			ivjStatusM = new javax.swing.JMenu();
			ivjStatusM.setName("StatusM");
			ivjStatusM.setText("Status");
			ivjStatusM.add(getStatCanMI());
			ivjStatusM.add(getJSeparator5());
			ivjStatusM.add(getStatRemSelMI());
			ivjStatusM.add(getStatRemAllMI());
			ivjStatusM.add(getJSeparator4());
			ivjStatusM.add(getStatDetailsMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStatusM;
}
/**
 * Return the StatusPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getStatusPnl() {
	if (ivjStatusPnl == null) {
		try {
			ivjStatusPnl = new javax.swing.JPanel();
			ivjStatusPnl.setName("StatusPnl");
			ivjStatusPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.insets = new java.awt.Insets(5, 5, 0, 5);
			getStatusPnl().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsStatusSP = new java.awt.GridBagConstraints();
			constraintsStatusSP.gridx = 0; constraintsStatusSP.gridy = 1;
			constraintsStatusSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsStatusSP.weightx = 1.0;
			constraintsStatusSP.weighty = 1.0;
			constraintsStatusSP.insets = new java.awt.Insets(0, 5, 5, 5);
			getStatusPnl().add(getStatusSP(), constraintsStatusSP);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStatusPnl;
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
			ivjStatusSplit.setDividerLocation(350);
			ivjStatusSplit.setOneTouchExpandable(true);
			ivjStatusSplit.setContinuousLayout(true);
			getStatusSplit().add(getHostSplit(), "top");
			getStatusSplit().add(getStatusPnl(), "bottom");
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
			ivjStatusTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjStatusTbl.setBounds(0, 0, 200, 200);
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
			ivjStopTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjStopTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/stop.gif")));
			ivjStopTB.setMargin(new java.awt.Insets(0, 0, 0, 0));
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
 * This method initializes pkgDescLbl
 * 
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getStoragePoolLbl() {
	if(storagePoolLbl == null) {
		storagePoolLbl = new javax.swing.JLabel();
		storagePoolLbl.setText("Storage area:");
	}
	return storagePoolLbl;
}
/**
 * This method initializes jComboBox
 * 
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getStoragePoolCB() {
	if(storagePoolCB == null) {
		storagePoolCB = new javax.swing.JComboBox();
		storagePoolCB.setMinimumSize(new java.awt.Dimension(125,24));
		storagePoolCB.setPreferredSize(new java.awt.Dimension(125,24));
		storagePoolCB.setToolTipText("Areas available to you to store files. Each areas has its own expiration rules.");
		storagePoolCB.addItemListener(expirationListener);
	}
	return storagePoolCB;
}
/**
 * Return the ToolBarPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getToolBarPnl() {
	if (ivjToolBarPnl == null) {
		try {
			ivjToolBarPnl = new javax.swing.JPanel();
			ivjToolBarPnl.setName("ToolBarPnl");
			ivjToolBarPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLoginTB = new java.awt.GridBagConstraints();
			constraintsLoginTB.gridx = 0; constraintsLoginTB.gridy = 0;
			constraintsLoginTB.insets = new java.awt.Insets(0, 0, 0, 2);
			getToolBarPnl().add(getLoginTB(), constraintsLoginTB);

			java.awt.GridBagConstraints constraintsLogoutTB = new java.awt.GridBagConstraints();
			constraintsLogoutTB.gridx = 1; constraintsLogoutTB.gridy = 0;
			constraintsLogoutTB.insets = new java.awt.Insets(0, 0, 0, 5);
			getToolBarPnl().add(getLogoutTB(), constraintsLogoutTB);

			GridBagConstraints constraintsItarLbl = new GridBagConstraints();
			constraintsItarLbl.gridx = 2; constraintsItarLbl.gridy = 0;
			constraintsItarLbl.insets = new java.awt.Insets(0,0,0,5);
			ivjToolBarPnl.add(getItarLbl(), constraintsItarLbl);

			java.awt.GridBagConstraints constraintsJLabel6 = new java.awt.GridBagConstraints();
			constraintsJLabel6.gridx = 3; constraintsJLabel6.gridy = 0;
			constraintsJLabel6.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel6.weightx = 1.0;
			ivjToolBarPnl.add(getJLabel6(), constraintsJLabel6);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjToolBarPnl;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getToUserLbl() {
	if (ivjToUserLbl == null) {
		try {
			ivjToUserLbl = new javax.swing.JLabel();
			ivjToUserLbl.setName("ToUserLbl");
			ivjToUserLbl.setText("Enter a Customer Connect ID:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjToUserLbl;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getToUserTF() {
	if (ivjToUserTF == null) {
		try {
			ivjToUserTF = new javax.swing.JTextField();
			ivjToUserTF.setName("ToUserTF");
			ivjToUserTF.setToolTipText("Enter Customer Connect ID of new recipient");
			ivjToUserTF.setPreferredSize(new java.awt.Dimension(4,19));
			ivjToUserTF.setMinimumSize(new java.awt.Dimension(4,19));
			ivjToUserTF.addActionListener(pkgEditListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjToUserTF;
}
/**
 * Return the UserDelBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getUserDelBtn() {
	if (ivjUserDelBtn == null) {
		try {
			ivjUserDelBtn = new javax.swing.JButton();
			ivjUserDelBtn.setName("UserDelBtn");
			ivjUserDelBtn.setToolTipText("Drop selected user from my saved users");
			ivjUserDelBtn.setText("");
			ivjUserDelBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjUserDelBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjUserDelBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/logoff.gif")));
			ivjUserDelBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjUserDelBtn.addActionListener(pkgEditListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserDelBtn;
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
 * Return the UserNewAddBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getUserNewAddBtn() {
	if (ivjUserNewAddBtn == null) {
		try {
			ivjUserNewAddBtn = new javax.swing.JButton();
			ivjUserNewAddBtn.setName("UserNewAddBtn");
			ivjUserNewAddBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/upload.gif")));
			ivjUserNewAddBtn.setToolTipText("Add new user to \'Send to\' list");
			ivjUserNewAddBtn.setText("");
			ivjUserNewAddBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjUserNewAddBtn.addActionListener(pkgEditListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserNewAddBtn;
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
			ivjUserTF.setToolTipText("Enter your user ID");
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
 * This method initializes aboutCP
 * 
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getViewDescCP() {
	if(viewDescCP == null) {
		viewDescCP = new javax.swing.JPanel();
		java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
		consGridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
		consGridBagConstraints3.weighty = 1.0;
		consGridBagConstraints3.weightx = 1.0;
		consGridBagConstraints3.gridy = 0;
		consGridBagConstraints3.gridx = 0;
		consGridBagConstraints3.insets = new java.awt.Insets(5,5,5,5);
		viewDescCP.setLayout(new java.awt.GridBagLayout());
		viewDescCP.add(getViewDescSP(), consGridBagConstraints3);
	}
	return viewDescCP;
}
/**
 * This method initializes jDialog
 * 
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getViewDescDlg() {
	if(viewDescDlg == null) {
		viewDescDlg = new javax.swing.JDialog();
		viewDescDlg.setContentPane(getViewDescCP());
		viewDescDlg.setSize(394, 281);
		viewDescDlg.setModal(true);
		viewDescDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		viewDescDlg.setTitle("View Package Description");
	}
	return viewDescDlg;
}
/**
 * This method initializes jScrollPane
 * 
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getViewDescSP() {
	if(viewDescSP == null) {
		viewDescSP = new javax.swing.JScrollPane();
		viewDescSP.setViewportView(getViewDescTA());
	}
	return viewDescSP;
}
/**
 * This method initializes jTextArea
 * 
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getViewDescTA() {
	if(viewDescTA == null) {
		viewDescTA = new javax.swing.JTextArea();
		viewDescTA.setEditable(false);
	}
	return viewDescTA;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			if (! msgHandler.isShowing()) {
				String[] msgs = { "An unexpected error has occurred.", "The messages window has been displayed for your awareness." };
				getMsgHandlerMI().doClick();
				JOptionPane.showMessageDialog(msgHandler,msgs,
						"Problem Found",JOptionPane.ERROR_MESSAGE);
			}
		}
	});
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		boolean enableDND = false;
		try {
			Class.forName("javax.swing.TransferHandler");
			enableDND = true;
		} catch (ClassNotFoundException e) {
			System.out.println("Drag and drop not available. Java 1.4 or higher required.");
		}

		setName("DropBoxPnl");
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints constraintsToolBarPnl = new java.awt.GridBagConstraints();
		constraintsToolBarPnl.gridx = 0; constraintsToolBarPnl.gridy = 0;
		constraintsToolBarPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsToolBarPnl.insets = new java.awt.Insets(2,2,2,2);
		add(getToolBarPnl(), constraintsToolBarPnl);
		java.awt.GridBagConstraints constraintsStatusSplit = new java.awt.GridBagConstraints();
		constraintsStatusSplit.gridx = 0; constraintsStatusSplit.gridy = 1;
		constraintsStatusSplit.gridwidth = 0;
		constraintsStatusSplit.fill = java.awt.GridBagConstraints.BOTH;
		constraintsStatusSplit.weightx = 1.0;
		constraintsStatusSplit.weighty = 1.0;
		add(getStatusSplit(), constraintsStatusSplit);

		// We need these processing aids.
		buddyMgr = new BuddyMgr();
		sourceMgr = new SourceMgr();
		dboxini = new ConfigFile();
		msgHandler = new MsgHandler();
		errorHandler = new ErrorHandler(ownerContainer);

		getStatusTbl().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				try {
					statusListSelectionChanged();
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
		});
		getInboxPkgTbl().getSelectionModel().addListSelectionListener(inboxPkgListSelectionListener);
		getInboxFileTbl().getSelectionModel().addListSelectionListener(inboxFileListSelectionListener);
		getOutboxPkgTbl().getSelectionModel().addListSelectionListener(outboxPkgListSelectionListener);
		getOutboxFileTbl().getSelectionModel().addListSelectionListener(outboxFileListSelectionListener);
		getSendPkgTbl().getSelectionModel().addListSelectionListener(sendPkgListSelectionListener);
		getSendFileTbl().getSelectionModel().addListSelectionListener(sendFileListSelectionListener);

		getEditFromLB().setModel(getEditFromLM());
		getEditToLB().setModel(getEditToLM());

		getStatusTbl().setModel(getFileStatusTM());
		getStatusTbl().createDefaultColumnsFromModel();

		getInboxPkgSortTM().setModel(getInboxPkgTM());
		getInboxPkgTbl().setModel(getInboxPkgSortTM());
		getInboxPkgTbl().createDefaultColumnsFromModel();

		getInboxFileSortTM().setModel(getInboxFileTM());
		getInboxFileTbl().setModel(getInboxFileSortTM());
		getInboxFileTbl().createDefaultColumnsFromModel();

		getSendFileSortTM().setModel(getSendFileTM());
		getSendFileTbl().setModel(getSendFileSortTM());
		getSendFileTbl().createDefaultColumnsFromModel();

		getSendPkgSortTM().setModel(getSendPkgTM());
		getSendPkgTbl().setModel(getSendPkgSortTM());
		getSendPkgTbl().createDefaultColumnsFromModel();

		getOutboxFileSortTM().setModel(getOutboxFileTM());
		getOutboxFileTbl().setModel(getOutboxFileSortTM());
		getOutboxFileTbl().createDefaultColumnsFromModel();

		getOutboxPkgSortTM().setModel(getOutboxPkgTM());
		getOutboxPkgTbl().setModel(getOutboxPkgSortTM());
		getOutboxPkgTbl().createDefaultColumnsFromModel();

		getFileAclViewSortTM().setModel(getFileAclViewTM());
		getFileAclViewTbl().setModel(getFileAclViewSortTM());
		getFileAclViewTbl().createDefaultColumnsFromModel();

		getPkgAclViewSortTM().setModel(getPkgAclViewTM());
		getPkgAclViewTbl().setModel(getPkgAclViewSortTM());
		getPkgAclViewTbl().createDefaultColumnsFromModel();

		getRemoteTP().getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				try {
					remoteStateChanged();
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
		});
		getRemoteTP().getModel().addChangeListener(inboxRefreshListener);

		getLocalFilePnl().setLocalMenu(getLocalM());

		getConfirmPkgTbl().setModel(getSendPkgSortTM());
		getConfirmPkgTbl().createDefaultColumnsFromModel();

		getConfirmPkgTbl().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				try {
					confirmPkgSelect(e);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
		});

		getGrpQueryLB().setModel(getGrpQueryLM());

		getLocalFilePnl().setAllowDirs(true);

		// Setup the remote tables.
		FileHeaderRenderer hr = new FileHeaderRenderer();
		hr.setToolTipText("Click to sort");
		hr.setHorizontalAlignment(JLabel.CENTER);

		// LocalFilePnl and SourceMgr needs to be notified of FileStatus events.
		getFileStatusTM().addFileStatusListener(sourceMgr);
		getFileStatusTM().addFileStatusListener(getLocalFilePnl());

		// Inbox Pkg table.
		setupTable(getInboxPkgTbl(),getInboxPkgTM(),hr);
		getInboxPkgSortTM().addMouseListenerToHeaderInTable(getInboxPkgTbl());
		getInboxPkgSortTM().sortByColumn(1,true);

		// Inbox File table.
		setupTable(getInboxFileTbl(),getInboxFileTM(),hr);
		getInboxFileSortTM().addMouseListenerToHeaderInTable(getInboxFileTbl());
		getInboxFileSortTM().sortByColumn(0,true);

		// Outbox Pkg table.
		setupTable(getOutboxPkgTbl(),getOutboxPkgTM(),hr);
		getOutboxPkgSortTM().addMouseListenerToHeaderInTable(getOutboxPkgTbl());
		getOutboxPkgSortTM().sortByColumn(0,true);

		// Outbox File table.
		setupTable(getOutboxFileTbl(),getOutboxFileTM(),hr);
		getOutboxFileSortTM().addMouseListenerToHeaderInTable(getOutboxFileTbl());
		getOutboxFileSortTM().sortByColumn(0,true);

		// Send Pkg table.
		setupTable(getSendPkgTbl(),getSendPkgTM(),hr);
		getSendPkgSortTM().addMouseListenerToHeaderInTable(getSendPkgTbl());
		getSendPkgSortTM().sortByColumn(0,true);
		getFileStatusTM().addFileStatusListener(getSendPkgTM());

		// Send File table: need to have SourceMgr, notification of FileStatusTM events.
		setupTable(getSendFileTbl(),getSendFileTM(),hr);
		getSendFileSortTM().addMouseListenerToHeaderInTable(getSendFileTbl());
		getSendFileSortTM().sortByColumn(0,true);
		getSendFileTM().setSourceMgr(sourceMgr);
		getFileStatusTM().addFileStatusListener(getSendFileTM());

		// File Acls table.
		getFileAclViewTbl().getTableHeader().setReorderingAllowed(false);
		TableColumnModel tm = getFileAclViewTbl().getColumnModel();
		for (int i = 0; i < getFileAclViewTM().getColumnCount(); i++) {
			tm.getColumn(i).setPreferredWidth(getFileAclViewTM().getColumnWidth(i));
			tm.getColumn(i).setHeaderRenderer(hr);
			if (getFileAclViewTM().getColumnRenderer(i) != null) {
				tm.getColumn(i).setCellRenderer(getFileAclViewTM().getColumnRenderer(i));
			}
		}
		getFileAclViewSortTM().addMouseListenerToHeaderInTable(getFileAclViewTbl());
		getFileAclViewSortTM().sortByColumn(0,true);

		// Pkg Acls table.
		getPkgAclViewTbl().getTableHeader().setReorderingAllowed(false);
		tm = getPkgAclViewTbl().getColumnModel();
		for (int i = 0; i < getPkgAclViewTM().getColumnCount(); i++) {
			tm.getColumn(i).setPreferredWidth(getPkgAclViewTM().getColumnWidth(i));
			tm.getColumn(i).setHeaderRenderer(hr);
			if (getPkgAclViewTM().getColumnRenderer(i) != null) {
				tm.getColumn(i).setCellRenderer(getPkgAclViewTM().getColumnRenderer(i));
			}
		}
		getPkgAclViewSortTM().addMouseListenerToHeaderInTable(getPkgAclViewTbl());
		getPkgAclViewSortTM().sortByColumn(0,true);

		// Setup the status table.
		getStatusTbl().getTableHeader().setReorderingAllowed(false);
		tm = getStatusTbl().getColumnModel();
		for (int i = 0; i < getFileStatusTM().getColumnCount(); i++) {
			tm.getColumn(i).setPreferredWidth(getFileStatusTM().getColumnWidth(i));
			if (getFileStatusTM().getColumnRenderer(i) != null) {
				tm.getColumn(i).setCellRenderer(getFileStatusTM().getColumnRenderer(i));
			}
		}

		// Confirm Pkg table.
		getConfirmPkgTbl().getTableHeader().setReorderingAllowed(false);
		tm = getConfirmPkgTbl().getColumnModel();
		for (int i = 0; i < getSendPkgTM().getColumnCount(); i++) {
			tm.getColumn(i).setPreferredWidth(getSendPkgTM().getColumnWidth(i));
			tm.getColumn(i).setHeaderRenderer(hr);
			if (getSendPkgTM().getColumnRenderer(i) != null) {
				tm.getColumn(i).setCellRenderer(getSendPkgTM().getColumnRenderer(i));
			}
		}
		getSendPkgSortTM().addMouseListenerToHeaderInTable(getConfirmPkgTbl());

		// Setup a MouseAdapter to handle double clicks on the Inbox/Outbox tables.
		getInboxPkgTbl().addMouseListener(downloadPkgHandler);
		getInboxFileTbl().addMouseListener(downloadFileHandler);
		getOutboxPkgTbl().addMouseListener(downloadPkgHandler);
		getOutboxFileTbl().addMouseListener(downloadFileHandler);

		// Setup a MouseAdapter to handle double clicks on the edit package lists.
		getEditFromLB().addMouseListener(pkgEditListener);
		getEditToLB().addMouseListener(pkgEditListener);

		// Setup the cell renderer for the edit package lists.
		BuddyListCellRenderer br = new BuddyListCellRenderer();
		this.setSize(840, 561);
		getEditFromLB().setCellRenderer(br);
		getEditToLB().setCellRenderer(br);

		// Setup a MouseAdapter to handle right presses on the status table.
		MouseAdapter ma = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if ((e.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
					statusListSelectionChanged();
					getStatusPU().show(getStatusTbl(),e.getX(),e.getY());
				}
			}
		};
		getStatusTbl().addMouseListener(ma);

		// Prepare to listen for changes on the login dialog.
		getUserTF().getDocument().addDocumentListener(loginDocumentListener);
		getPwdTF().getDocument().addDocumentListener(loginDocumentListener);

		// Prepare to listen for changes on the package edit dialog.
		getToUserTF().getDocument().addDocumentListener(pkgEditListener);
		getPkgNameTF().getDocument().addDocumentListener(pkgEditListener);
		getPkgDescTA().getDocument().addDocumentListener(pkgEditListener);

		// Prepare to listen for changes on the download confirmation dialog.
		getDownConfTF().getDocument().addDocumentListener(downConfListener);

		// Fix the background colors of the viewports to match the tables.
		getInboxPkgSP().getViewport().setBackground(getInboxPkgTbl().getBackground());
		getInboxFileSP().getViewport().setBackground(getInboxFileTbl().getBackground());
		getOutboxPkgSP().getViewport().setBackground(getOutboxPkgTbl().getBackground());
		getOutboxFileSP().getViewport().setBackground(getOutboxFileTbl().getBackground());
		getSendPkgSP().getViewport().setBackground(getSendPkgTbl().getBackground());
		getSendFileSP().getViewport().setBackground(getSendFileTbl().getBackground());
		getStatusSP().getViewport().setBackground(getStatusTbl().getBackground());

		// Set up the glass pane to handle the busy cursor.
		ownerRootPane.getGlassPane().addMouseListener(new MouseAdapter() {});
		ownerRootPane.getGlassPane().addKeyListener(new KeyAdapter() {});

		// Set up the glass pane to handle the busy cursor on Package Edit panel.
		getPkgEditDlg().getGlassPane().addMouseListener(new MouseAdapter() {});
		getPkgEditDlg().getGlassPane().addKeyListener(new KeyAdapter() {});

		// Set up the glass pane to handle the busy cursor on Query Group panel.
		getGrpQueryDlg().getGlassPane().addMouseListener(new MouseAdapter() {});
		getGrpQueryDlg().getGlassPane().addKeyListener(new KeyAdapter() {});

		// We need to be the last FileStatus listener.
		getFileStatusTM().addFileStatusListener(this);

		// Share our buddyMgr with the ManageGroups object.
		getManageGroups().setBuddyMgr(buddyMgr);
		
		// Create the expiration calendar chooser.
		expirationCC = new JCalendarChooser();
		expirationCC.addActionListener(expirationListener);
		
		// Create an about window.
		aboutWindow = new AboutWindow(ownerContainer);
		ownerContainer.setTitle("ownerContainer");
		
		// Enable drag and drop support.
		if (enableDND) {
			dragHandler = new DragHandler();
			sendDropHandler = new SendDropHandler();
			Class[] parms = { TransferHandler.class };
			Object[] args = { dragHandler };
			// getInboxFileTbl().setTransferHandler(dragHandler);
			Method meth = getInboxFileTbl().getClass().getMethod("setTransferHandler",parms);
			meth.invoke(getInboxFileTbl(),args);
			// getInboxPkgTbl().setTransferHandler(dragHandler);
			meth = getInboxPkgTbl().getClass().getMethod("setTransferHandler",parms);
			meth.invoke(getInboxPkgTbl(),args);
			// getOutboxFileTbl().setTransferHandler(dragHandler);
			meth = getOutboxFileTbl().getClass().getMethod("setTransferHandler",parms);
			meth.invoke(getOutboxFileTbl(),args);
			// getOutboxPkgTbl().setTransferHandler(dragHandler);
			meth = getOutboxPkgTbl().getClass().getMethod("setTransferHandler",parms);
			meth.invoke(getOutboxPkgTbl(),args);
			// getSendFileSP().setTransferHandler(sendDropHandler);
			args[0] = sendDropHandler;
			meth = getSendFileSP().getClass().getMethod("setTransferHandler",parms);
			meth.invoke(getSendFileSP(),args);
			// getSendFileTbl().setTransferHandler(sendDropHandler);
			meth = getSendFileTbl().getClass().getMethod("setTransferHandler",parms);
			meth.invoke(getSendFileTbl(),args);
			// getSendPkgSP().setTransferHandler(sendDropHandler);
			meth = getSendPkgSP().getClass().getMethod("setTransferHandler",parms);
			meth.invoke(getSendPkgSP(),args);
			// getSendPkgTbl().setTransferHandler(sendDropHandler);
			meth = getSendPkgTbl().getClass().getMethod("setTransferHandler",parms);
			meth.invoke(getSendPkgTbl(),args);
			// getInboxFileTbl().setDragEnabled(true);
			parms[0] = Boolean.TYPE;
			args[0] = Boolean.TRUE;
			meth = getInboxFileTbl().getClass().getMethod("setDragEnabled",parms);
			meth.invoke(getInboxFileTbl(),args);
			// getInboxPkgTbl().setDragEnabled(true);
			meth = getInboxPkgTbl().getClass().getMethod("setDragEnabled",parms);
			meth.invoke(getInboxPkgTbl(),args);
			// getOutboxFileTbl().setDragEnabled(true);
			meth = getOutboxFileTbl().getClass().getMethod("setDragEnabled",parms);
			meth.invoke(getOutboxFileTbl(),args);
			// getOutboxPkgTbl().setDragEnabled(true);
			meth = getOutboxPkgTbl().getClass().getMethod("setDragEnabled",parms);
			meth.invoke(getOutboxPkgTbl(),args);
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 10:01:03 AM)
 */
public void loadPreferences() {
	// Restore current directory
	String dir = dboxini.getProperty(DropboxOptions.INITDIRECTORY,null);
	try {
		getLocalFilePnl().setDirectory(dir);
	}
	catch (IOException e) {
		System.out.println("Could not restore previous local directory.");
		System.out.println(e.getMessage());
	}

	// Load table changes
	if (dboxini.getBoolProperty(DropboxOptions.SAVETABLESTATE,true)) {
		loadTablePreferences("InboxPkgTbl",getInboxPkgTbl(),getInboxPkgTM());
		loadTablePreferences("InboxFileTbl",getInboxFileTbl(),getInboxFileTM());
		loadTablePreferences("OutboxPkgTbl",getOutboxPkgTbl(),getOutboxPkgTM());
		loadTablePreferences("OutboxFileTbl",getOutboxFileTbl(),getOutboxFileTM());
		loadTablePreferences("SendPkgTbl",getSendPkgTbl(),getSendPkgTM());
		loadTablePreferences("SendFileTbl",getSendFileTbl(),getSendFileTM());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 10:01:03 AM)
 */
public void loadSplit() {
	// Restore divider locations
	Vector v = dboxini.getSection("SplitState");
	if (v != null && v.size() == 1) {
		ConfigSection dl = (ConfigSection) v.elementAt(0);
		int i = dl.getIntProperty("host",-1);
		if (i > -1) {
			getHostSplit().setDividerLocation(i);
		}
		i = dl.getIntProperty("status",-1);
		if (i > -1) {
			getStatusSplit().setDividerLocation(i);
		}
	}
	else {
		setDefaultSplit();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:31:37 PM)
 */
public void loadTablePreferences(String section, JTable table, DropboxTableModel model) {
//	System.out.println("Processing changes for table " + section);

	// Get the section in the ini file for this table.
	Vector v = dboxini.getSection(section);
	if (v != null && v.size() == 1) {
		ConfigSection ts = (ConfigSection) v.elementAt(0);

		// Get the column model and prepare to apply changes.
		TableColumnModel tcm = table.getColumnModel();

		// Keep track of logical columns physical location.
		TableColumn slots[] = new TableColumn[model.getColumnCount()];
		for (int i = slots.length - 1; i >= 0; i--) {
			slots[i] = tcm.getColumn(i);
//			System.out.println("Slot " + i + " holds model index " + slots[i].getModelIndex());
			tcm.removeColumn(slots[i]);
		}

		// For every logical column in the data model...
		for (int i = 0; i < model.getColumnCount(); i++) {
			String name = model.getLogicalColumnName(i);

//			System.out.println("Checking for adjustments to column " + name);

			// See if the logical column has been changed.
			v = ts.getSection(name);
			if (v != null && v.size() == 1) {
				ConfigSection cs = (ConfigSection) v.elementAt(0);

				// Find the column's current position.
				int ci = -1;
				for (int j = 0; j < slots.length && ci < 0; j++) {
					if (slots[j].getModelIndex() == i) {
						ci = j;
					}
				}

				// Logical column has changes, get them.
				int ti = cs.getIntProperty("index",-1);
				int w = cs.getIntProperty("width",-1);

				// Wants to alter the column width?
				if (w > -1) {
//					System.out.println("Resize width of column " + ci + " to " + w);
					slots[ci].setWidth(w);
					slots[ci].setPreferredWidth(w);
				}

				// Logical column wants to swap with another and it is not already in position?
				if (ti != -1 && ti != ci) {
//					System.out.println("Move column from position " + ci + " to " + ti);

					// Pull the column from its old position.
//					System.out.println("Saving column in slot " + ci);
					TableColumn h = slots[ci];

					// Shift everyone to the left?
					if (ti > ci) {
//						System.out.println("Shifting slots to the left.");
						for (int j = ci; j < ti; j++) {
//							System.out.println((j+1) + " to " + j);
							slots[j] = slots[j+1];
						}
					}
					// Shift everyone to the right.
					else {
//						System.out.println("Shifting slots to the right.");
						for (int j = ci; j > ti; j--) {
//							System.out.println((j-1) + " to " + j);
							slots[j] = slots[j-1];
						}
					}

					// Put the column in its new position.
//					System.out.println("Storing column in slot " + ti);
					slots[ti] = h;
				}
			}
//			else {
//				System.out.println("No adjustments for column " + name);
//			}
		}

		// Put all the columns back into the table column model.
		for (int i = 0; i < slots.length; i++) {
			tcm.addColumn(slots[i]);
		}
	}
//	else {
//		System.out.println("No changes for " + section);
//	}
}
/**
 * Comment
 */
public void moreInfo() {
	getDownInfoDlg().setLocationRelativeTo(getDownConfirmDlg());
	getDownInfoDlg().setVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 1:04:39 PM)
 * @param e OptionEvent
 */
public void optionAction(OptionEvent e) {
	// Reset window to default size and position.
	if (e.isResetWindow()) {
		SwingUtilities.invokeLater(new MethodRunner(this,"setPositionAndSize"));
	}
	// Reset tables to default column order and sizes.
	else if (e.isResetTables()) {
		SwingUtilities.invokeLater(new MethodRunner(this,"resetTables"));
	}
	// Reset split panes to default positions.
	else if (e.isResetSplit()) {
		SwingUtilities.invokeLater(new MethodRunner(this,"setDefaultSplit"));
	}
}
/**
 * Owner event notification
 */
public void ownerEvent(int event) {
	if (event == WINDOW_OPEN) {
		// Will always auto login in begin()
		//getLoginMI().doClick();
	}
	else if (event == WINDOW_CLOSING){
		getExitMI().doClick();
	}
}
/**
 * Comment
 */
public void remoteStateChanged() {
	// Inbox pane now showing.
	if (getRemoteTP().getSelectedComponent() == getInboxPnl()) {
		// Enable needed Files menu items.
		boolean enable = getInboxFileTbl().getSelectedRowCount() > 0;
		getFilesDeleteMI().setEnabled(false);
		getFilesRestartMI().setEnabled(false);
		getFilesViewMI().setEnabled(false);
		getFilesDownMI().setEnabled(enable);
		getFilesFwdMI().setEnabled(enable);

		// Enable needed Pkg menu items.
		int i = getInboxPkgTbl().getSelectedRowCount();
		enable = i > 0;
		getPkgAddSenderMI().setEnabled(enable);
		getPkgCreateMI().setEnabled(false);
		getPkgDeleteMI().setEnabled(false);
		getPkgDeliverMI().setEnabled(false);
		getPkgEditMI().setEnabled(false);
		getPkgViewMI().setEnabled(false);
		getPkgDownMI().setEnabled(enable);
		getPkgViewDescMI().setEnabled(i == 1);
		getPkgFwdMI().setEnabled(i == 1);
	}
	// Outbox pane now showing.
	else if (getRemoteTP().getSelectedComponent() == getOutboxPnl()) {
		// Enable needed Files menu items.
		int i = getOutboxFileTbl().getSelectedRowCount();
		getFilesDeleteMI().setEnabled(false);
		getFilesRestartMI().setEnabled(false);
		getFilesDownMI().setEnabled(i > 0);
		getFilesFwdMI().setEnabled(i > 0);
		getFilesViewMI().setEnabled(i == 1);

		// Enable needed Pkg menu items.
		i = getOutboxPkgTbl().getSelectedRowCount();
		getPkgAddSenderMI().setEnabled(false);
		getPkgCreateMI().setEnabled(false);
		getPkgDeliverMI().setEnabled(false);
		getPkgDeleteMI().setEnabled(i > 0);
		getPkgDownMI().setEnabled(i > 0);
		getPkgFwdMI().setEnabled(false);
		getPkgEditMI().setEnabled(i == 1);
		getPkgViewDescMI().setEnabled(false);
		getPkgViewMI().setEnabled(i == 1);
	}
	// Sendbox pane now showing.
	else {
		// Enable needed Files menu items.
		boolean enable = (getSendFileTbl().getSelectedRowCount() > 0);

		getFilesDownMI().setEnabled(false);
		getFilesFwdMI().setEnabled(false);
		getFilesViewMI().setEnabled(false);
		getFilesDeleteMI().setEnabled(enable);

		int[] s = getSendFileTbl().getSelectedRows();
		for (int i = 0; i < s.length && enable; i++) {
			int u = getSendFileSortTM().getUnsortedIndex(s[i]);
			if (! getSendFileTM().isFailed(u)) {
				enable = false;
			}
		}

		getFilesRestartMI().setEnabled(enable);

		// Enable needed Pkg menu items.
		int i = getSendPkgTbl().getSelectedRowCount();

		getPkgAddSenderMI().setEnabled(false);
		getPkgCreateMI().setEnabled(true);
		getPkgDownMI().setEnabled(false);
		getPkgFwdMI().setEnabled(false);
		getPkgViewDescMI().setEnabled(false);
		getPkgViewMI().setEnabled(false);
		getPkgDeleteMI().setEnabled(i > 0);
		getPkgDeliverMI().setEnabled(i > 0);
		getPkgEditMI().setEnabled(i == 1);
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
public void removeDropBoxPnlListener(DropBoxPnlListener l) {
	if (l == null) return;
	listeners = DBEventMulticaster.removeDropBoxPnlListener(listeners,l);
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
 * Creation date: (8/5/2004 10:44:56 AM)
 */
public void resetTables() {
	TableCellRenderer hr = getInboxPkgTbl().getColumnModel().getColumn(0).getHeaderRenderer();

	// Inbox Pkg table.
	getInboxPkgTbl().createDefaultColumnsFromModel();
	getInboxPkgSortTM().sortByColumn(1,true);
	setupTable(getInboxPkgTbl(),getInboxPkgTM(),hr);

	// Inbox File table.
	getInboxFileTbl().createDefaultColumnsFromModel();
	getInboxFileSortTM().sortByColumn(0,true);
	setupTable(getInboxFileTbl(),getInboxFileTM(),hr);

	// Outbox Pkg table.
	getOutboxPkgTbl().createDefaultColumnsFromModel();
	getOutboxPkgSortTM().sortByColumn(0,true);
	setupTable(getOutboxPkgTbl(),getOutboxPkgTM(),hr);

	// Outbox File table.
	getOutboxFileTbl().createDefaultColumnsFromModel();
	getOutboxFileSortTM().sortByColumn(0,true);
	setupTable(getOutboxFileTbl(),getOutboxFileTM(),hr);

	// Send Pkg table.
	getSendPkgTbl().createDefaultColumnsFromModel();
	getSendPkgSortTM().sortByColumn(0,true);
	setupTable(getSendPkgTbl(),getSendPkgTM(),hr);

	// Send File table: need to have SourceMgr, notification of FileStatusTM events.
	getSendFileTbl().createDefaultColumnsFromModel();
	getSendFileSortTM().sortByColumn(0,true);
	setupTable(getSendFileTbl(),getSendFileTM(),hr);
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 1:02:30 PM)
 */
public void resetToLoggedIn() {
	// Disable menu features
	getLoginMI().setEnabled(false);
	getLogoutMI().setEnabled(true);
	getGroupMI().setEnabled(true);
	getOptionsMI().setEnabled(true);
	getRefreshMI().setEnabled(true);
	remoteStateChanged();
	statusListSelectionChanged();

	// Disable window features
	getStatusTbl().setEnabled(true);
	getLocalFilePnl().setLoggedIn(true);
	getInboxPkgTbl().setEnabled(true);
	getInboxFileTbl().setEnabled(true);
	getInboxRefreshBtn().setEnabled(true);
	getOutboxPkgTbl().setEnabled(true);
	getOutboxFileTbl().setEnabled(true);
	getOutboxRefreshBtn().setEnabled(true);
	getSendPkgTbl().setEnabled(true);
	getSendFileTbl().setEnabled(true);
	getSendPkgCreateBtn().setEnabled(true);
	getSendCommitBtn().setEnabled(false);

	// Set the right toolbar features enabled.
	getLoginTB().setEnabled(false);
	getLogoutTB().setEnabled(true);
	getStopTB().setEnabled(false);

	isExit = false;
	fireTitleChange("Drop Box - Connected");
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 1:02:30 PM)
 */
public void resetToLoggedOut() {
	// Clear the data models properly.
	getStatusTbl().clearSelection();
	getFileStatusTM().clear();
	emptyInboxPkg();
	emptyOutboxPkg();
	emptySendPkg();

	// Disable menu features
	getLoginMI().setEnabled(true);
	getLogoutMI().setEnabled(false);
	getGroupMI().setEnabled(false);
	getOptionsMI().setEnabled(false);
	getItarMI().setEnabled(false);
	getRefreshMI().setEnabled(false);
	getFilesDownMI().setEnabled(false);
	getFilesDeleteMI().setEnabled(false);
	getFilesFwdMI().setEnabled(false);
	getFilesRestartMI().setEnabled(false);
	getFilesViewMI().setEnabled(false);
	getPkgAddSenderMI().setEnabled(false);
	getPkgCreateMI().setEnabled(false);
	getPkgDeleteMI().setEnabled(false);
	getPkgDeliverMI().setEnabled(false);
	getPkgDownMI().setEnabled(false);
	getPkgEditMI().setEnabled(false);
	getPkgViewDescMI().setEnabled(false);
	getPkgViewMI().setEnabled(false);
	getStatCanMI().setEnabled(false);
	getStatDetailsMI().setEnabled(false);
	getStatRemAllMI().setEnabled(false);
	getStatRemSelMI().setEnabled(false);

	// Disable window features
	getStatusTbl().setEnabled(false);
	getLocalFilePnl().setLoggedIn(false);
	getInboxPkgTbl().setEnabled(false);
	getInboxFileTbl().setEnabled(false);
	getInboxRefreshBtn().setEnabled(false);
	getOutboxPkgTbl().setEnabled(false);
	getOutboxFileTbl().setEnabled(false);
	getOutboxRefreshBtn().setEnabled(false);
	getSendPkgTbl().setEnabled(false);
	getSendFileTbl().setEnabled(false);
	getSendPkgCreateBtn().setEnabled(false);
	getSendCommitBtn().setEnabled(false);

	// Set the right toolbar features enabled.
	getLoginTB().setEnabled(true);
	getLogoutTB().setEnabled(false);
	getStopTB().setEnabled(false);

	// ITAR Session Certification
	itarListener.sessionEnded();

	fireTitleChange("Drop Box - Disconnected");
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:31:37 PM)
 */
public void savePreferences() {
	// Will not be saving window size?
	if (! dboxini.getBoolProperty(DropboxOptions.SAVEWINDOWSTATE,true)) {
		dboxini.removeSection("WindowState");
	}

	// Save divider locations
	if (dboxini.getBoolProperty(DropboxOptions.SAVESPLITSTATE,true)) {
		ConfigSection dl = new ConfigSection("SplitState");
		dl.setIntProperty("host",getHostSplit().getDividerLocation());
		dl.setIntProperty("status",getStatusSplit().getDividerLocation());
		dboxini.removeSection(dl.getName());
		dboxini.addSection(dl);
	}
	else {
		dboxini.removeSection("SplitState");
	}

	// Save current directory
	if (dboxini.getBoolProperty(DropboxOptions.SAVEINITDIRECTORY,true)) {
		dboxini.setProperty(DropboxOptions.INITDIRECTORY,getLocalFilePnl().getLocalTM().getDirectory());
	}

	if (dboxini.getBoolProperty(DropboxOptions.SAVETABLESTATE,true)) {
		// Save InboxPkg table changes
		ConfigSection ts = new ConfigSection("InboxPkgTbl");
		dboxini.removeSection(ts.getName());
		if (saveTablePreferences(ts,getInboxPkgTbl(),getInboxPkgTM())) {
			dboxini.addSection(ts);
		}

		// Save InboxFile table changes
		ts = new ConfigSection("InboxFileTbl");
		dboxini.removeSection(ts.getName());
		if (saveTablePreferences(ts,getInboxFileTbl(),getInboxFileTM())) {
			dboxini.addSection(ts);
		}

		// Save OutboxPkg table changes
		ts = new ConfigSection("OutboxPkgTbl");
		dboxini.removeSection(ts.getName());
		if (saveTablePreferences(ts,getOutboxPkgTbl(),getOutboxPkgTM())) {
			dboxini.addSection(ts);
		}

		// Save OutboxFile table changes
		ts = new ConfigSection("OutboxFileTbl");
		dboxini.removeSection(ts.getName());
		if (saveTablePreferences(ts,getOutboxFileTbl(),getOutboxFileTM())) {
			dboxini.addSection(ts);
		}

		// Save SendPkg table changes
		ts = new ConfigSection("SendPkgTbl");
		dboxini.removeSection(ts.getName());
		if (saveTablePreferences(ts,getSendPkgTbl(),getSendPkgTM())) {
			dboxini.addSection(ts);
		}

		// Save SendFile table changes
		ts = new ConfigSection("SendFileTbl");
		dboxini.removeSection(ts.getName());
		if (saveTablePreferences(ts,getSendFileTbl(),getSendFileTM())) {
			dboxini.addSection(ts);
		}
	}
	else {
		dboxini.removeSection("InboxPkgTbl");
		dboxini.removeSection("InboxFileTbl");
		dboxini.removeSection("OutboxPkgTbl");
		dboxini.removeSection("OutboxFileTbl");
		dboxini.removeSection("SendPkgTbl");
		dboxini.removeSection("SendFileTbl");
	}

	// Save ini file to disk.
	try {
		dboxini.store("dropbox.ini");
	}
	catch (IOException e) {
		System.out.println("Unable to save preferences as dropbox.ini");
	}

	// Ok, Now get the owning container to save window size, if needed.
	if (dboxini.getBoolProperty(DropboxOptions.SAVEWINDOWSTATE,true)) {
		fireSavePreferences();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:31:37 PM)
 */
public boolean saveTablePreferences(ConfigSection section, JTable table, DropboxTableModel model) {
	// Get the column model and prepare to report section changes.
	TableColumnModel tcm = table.getColumnModel();
	boolean changed = false;

	// Check each column.
	for (int i = 0; i < tcm.getColumnCount(); i++) {
		// Get the column, its model index and create a section for it.
		TableColumn tc = tcm.getColumn(i);
		int mi = tc.getModelIndex();
		ConfigSection tcs = new ConfigSection(model.getLogicalColumnName(mi));

		boolean differs = false;

		// User moved this column? save its new model index.
		if (mi != i) {
			tcs.setIntProperty("index",i);
			differs = true;
		}

		// User resized this column? save its new width.
		if (tc.getWidth() != model.getColumnWidth(mi)) {
			tcs.setIntProperty("width",tc.getWidth());
			differs = true;
		}

		// Column was moved or sized? Save its changes to the table section.
		if (differs) {
			section.addSection(tcs);
			changed = true;
		}
	}

	// Return true if changes were stored in the table section.
	return changed;
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2003 1:22:41 PM)
 * @param names java.util.Vector
 * @param name java.lang.String
 * @param baseDir java.lang.String
 */
public void scanDirectory(Vector names, Vector files, String name, String absName) {
	// Resolve any links.
	String resolvedAbsName = LinkResolver.resolveLink(absName);
	String resolvedName = name;
	if (! resolvedAbsName.equals(absName)) {
		resolvedName = LinkResolver.trimLink(name);
	}

	// Check to see if name is a file or directory.
	File f = new File(resolvedAbsName);

	// If file disappeared, just return.
	if (! f.exists())
		return;

	// For a directory, call ourself with each entry.
	if (f.isDirectory()) {
		String[] entries = f.list();
		for (int i = 0; i < entries.length; i++) {
			String entry = resolvedName + '/' + entries[i];
			String realEntry = resolvedAbsName + File.separator + entries[i];
			scanDirectory(names,files,entry,realEntry);
		}
	}
	// For a file, just add it to the names vector.
	else {
		names.addElement(resolvedName);
		files.addElement(f);
	}
}
/**
 * Adjust the Send Pkg Table GUI elements.
 */
public void sendPkgSelectAdjust() {
	int[] r = getSendPkgTbl().getSelectedRows();

	boolean busy = false;
	boolean ready = true;

	if (r.length > 0) {
		for (int i = 0; i < r.length; i++) {
			int c = getSendPkgSortTM().getUnsortedIndex(r[i]);
			if (! busy) busy = getSendPkgTM().isBusy(c);
			if (ready) ready = getSendPkgTM().isReady(c);
		}
	}
	else {
		busy = true;
	}

	int i = r.length;
	getSendPkgEditBtn().setEnabled(i == 1);
	getSendPkgDeleteBtn().setEnabled(! busy);
	getSendCommitBtn().setEnabled((! busy) && ready);
	getPkgDeleteMI().setEnabled(! busy);
	getPkgDeliverMI().setEnabled((! busy) && ready);
	getPkgEditMI().setEnabled(i == 1);
}
/**
 * Set the ConfirmDlg to a new value.
 * @param newValue javax.swing.JDialog
 */
private void setConfirmDlg(javax.swing.JDialog newValue) {
	if (ivjConfirmDlg != newValue) {
		try {
			ivjConfirmDlg = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Comment
 */
public void setDefaultSplit() {
	Dimension s = getStatusSplit().getSize();
	int dl = (s.height - getStatusSplit().getDividerSize()) / 4 * 3;
	getStatusSplit().setDividerLocation(dl);
	dl = (s.width - getHostSplit().getDividerSize()) / 3;
	getHostSplit().setDividerLocation(dl);
}
/**
 * Set the DownConfirmDlg to a new value.
 * @param newValue javax.swing.JDialog
 */
private void setDownConfirmDlg(javax.swing.JDialog newValue) {
	if (ivjDownConfirmDlg != newValue) {
		try {
			ivjDownConfirmDlg = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the DownInfoDlg to a new value.
 * @param newValue javax.swing.JDialog
 */
private void setDownInfoDlg(javax.swing.JDialog newValue) {
	if (ivjDownInfoDlg != newValue) {
		try {
			ivjDownInfoDlg = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the DropboxOptions to a new value.
 * @param newValue oem.edge.ed.odc.dropbox.client.DropboxOptions
 */
private void setDropboxOptions(DropboxOptions newValue) {
	if (ivjDropboxOptions != newValue) {
		try {
			ivjDropboxOptions = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the FileAclViewDlg to a new value.
 * @param newValue javax.swing.JDialog
 */
private void setFileAclViewDlg(javax.swing.JDialog newValue) {
	if (ivjFileAclViewDlg != newValue) {
		try {
			ivjFileAclViewDlg = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the GrpQueryDlg to a new value.
 * @param newValue javax.swing.JDialog
 */
private void setGrpQueryDlg(javax.swing.JDialog newValue) {
	if (ivjGrpQueryDlg != newValue) {
		try {
			ivjGrpQueryDlg = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the ManageGroups1 to a new value.
 * @param newValue oem.edge.ed.odc.dropbox.client.ManageGroups
 */
private void setManageGroups(ManageGroups newValue) {
	if (ivjManageGroups != newValue) {
		try {
			ivjManageGroups = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the PkgAclViewDlg to a new value.
 * @param newValue javax.swing.JDialog
 */
private void setPkgAclViewDlg(javax.swing.JDialog newValue) {
	if (ivjPkgAclViewDlg != newValue) {
		try {
			ivjPkgAclViewDlg = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the PkgAddFilesInfoDlg1 to a new value.
 * @param newValue javax.swing.JDialog
 */
private void setPkgAddFilesInfoDlg(javax.swing.JDialog newValue) {
	if (ivjPkgAddFilesInfoDlg != newValue) {
		try {
			ivjPkgAddFilesInfoDlg = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the JDialog3 to a new value.
 * @param newValue javax.swing.JDialog
 */
private void setPkgEditDlg(javax.swing.JDialog newValue) {
	if (ivjPkgEditDlg != newValue) {
		try {
			ivjPkgEditDlg = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Comment
 */
public void setupTable(JTable table, DropboxTableModel model, TableCellRenderer hr) {
	TableColumnModel tm = table.getColumnModel();
	for (int i = 0; i < model.getColumnCount(); i++) {
		tm.getColumn(i).setPreferredWidth(model.getColumnWidth(i));
		tm.getColumn(i).setHeaderRenderer(hr);
		if (model.getColumnRenderer(i) != null) {
			tm.getColumn(i).setCellRenderer(model.getColumnRenderer(i));
		}
	}
}
/**
 * Comment
 */
public void showDetails() {
	getFileStatusTM().showDetail(errorHandler,getStatusTbl().getSelectedRows());
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
	getStatCanMI().setEnabled(showCancel);
	getStatDetailsMI().setEnabled(showFailed);
	getStatRemAllMI().setEnabled(showRemoveAll);
	getStatRemSelMI().setEnabled(showRemove);

	getStopTB().setEnabled(showCancel);
}
/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 1:13:25 PM)
 * @param msg java.lang.String
 */
public void syntax(String msg) {
	System.out.println(msg);
	System.out.println("DropBox -CH_TOKEN token -URL url");
}
  }  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
