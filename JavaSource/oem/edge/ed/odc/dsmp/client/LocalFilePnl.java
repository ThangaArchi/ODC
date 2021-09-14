package oem.edge.ed.odc.dsmp.client;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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
 * Creation date: (3/7/2003 8:25:15 AM)
 * @author: Mike Zarnick
 */
public class LocalFilePnl extends JPanel implements FileStatusListener {
	private boolean loggedIn = false;
	private boolean allowDirs = false;
	private boolean isWin = false;
	private JMenu fieldLocalMenu = null;
	private JLabel ivjLocalLbl = null;
	private JButton ivjUploadBtn = null;
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
	protected transient LocalFilePnlListener fieldLocalFilePnlListenerEventMulticaster = null;
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
	private JLabel filterLbl = null;
	private JTextField filterTF = null;
	private JButton pathOkBtn = null;
	private JButton filterOkBtn = null;
	/**
	 * Provides Drag and Drop support for the file table.
	 */
	private class DragDropHandler extends TransferHandler {
		private DataFlavor fileListFlavor = null;
		private DataFlavor feedbackFlavor = null;
		private boolean nativeFileListOk = false;
		private DataFlavor flavors[] = null;
		private ArrayList flavorList = new ArrayList();

		// Prepare this handler to accept dsmp FileListFlavor and FeedbackFlavor.
		// FileListFlavor is an internal class to allow the various GUI elements to
		// exchange file lists, but prevents them from interacting with other apps.
		// FeedbackFlavor is an internal class which is used to provide the
		// target directory back to the drag source so that they can complete the
		// drag-n-drop operation.
		public DragDropHandler() {
			/* We don't really want to allow us to accept files lists... The inbox and sent
			 * panels will provide feedback flavors, and interaction with externals, if 
			 * allowed, will use regular file lists. Accepting FileListFlavor only allows
			 * us to do DND with ourselves.
			try {
				fileListFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=oem.edge.ed.odc.dsmp.client.FileListFlavor");
				flavorList.add(fileListFlavor);
			} catch (ClassNotFoundException e) {
				System.out.println("LocalFilePnl.DragDropHandler: " + e.getMessage());
			}
			*/
			try {
				feedbackFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=oem.edge.ed.odc.dsmp.client.FeedbackFlavor");
				flavorList.add(feedbackFlavor);
			} catch (ClassNotFoundException e) {
				System.out.println("LocalFilePnl.DragDropHandler: " + e.getMessage());
			}

			buildArray();
		}
		// Build the flavors array from flavorList.
		private void buildArray() {
			flavors = new DataFlavor[flavorList.size()];
			for (int i = 0; i < flavors.length; i++) {
				flavors[i] = (DataFlavor) flavorList.get(i);
			}
		}
		// Add support for native file lists. Usually for Unix Platforms.
		public void addNativeFileListSupport() {
			nativeFileListOk = true;
			flavorList.add(DataFlavor.javaFileListFlavor);
			buildArray();
		}
		// Decide if we can accept one of the available flavors.
		public boolean canImport(JComponent c, DataFlavor[] f) {
			for (int i = 0; i < f.length; i++) {
				if (flavorList.contains(f[i])) {
					return true;
				}
			}
			return false;
		}
		public Transferable createTransferable(JComponent c) {
			return new DragTransferable(nativeFileListOk);
		}
		public void exportDone(JComponent c, Transferable t, int action) {
			// May need to delete the source selections and refresh list.
			if (action == TransferHandler.MOVE) {
				System.out.println("exportDone: Move!");
				((DragTransferable) t).completeMove();
			}
			else {
				System.out.println("exportDone: " + action);
			}
		}
		// We can source copy and move operations.
		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY_OR_MOVE;
		}
		// We can accept either a file list or a FeedbackFlavor loop.
		public boolean importData(JComponent c, Transferable t) {
			FeedbackFlavor fbf = null;
			List flf = null;

			for (int i = 0; i < flavors.length && fbf == null && flf == null; i++) {
				if (t.isDataFlavorSupported(flavors[i])) {
					if (flavors[i] == fileListFlavor) {
						try {
							FileListFlavor l = (FileListFlavor) t.getTransferData(fileListFlavor);
							flf = l.files;
						}
						catch (UnsupportedFlavorException e) {
							// Doesn't have a file list, no biggy.
						}
						catch (IOException e) {
							System.out.println("LocalFilePnl.DragDropHandler.importData: FileListFlavor IOException!");
							e.printStackTrace();
							return false;
						}
						catch (ClassCastException e) {
							System.out.println("LocalFilePnl.DragDropHandler.importData: data not class FileListFlavor!");
							e.printStackTrace();
							return false;
						}
					}
					else if (flavors[i] == feedbackFlavor) {
						try {
							fbf = (FeedbackFlavor) t.getTransferData(feedbackFlavor);
						}
						catch (UnsupportedFlavorException e) {
							// Doesn't have a feedback flavor, no biggy.
						}
						catch (IOException e) {
							System.out.println("LocalFilePnl.DragDropHandler.importData: FeedbackFlavor IOException!");
							e.printStackTrace();
							return false;
						}
						catch (ClassCastException e) {
							System.out.println("LocalFilePnl.DragDropHandler.importData: data not class FeedbackFlavor!");
							e.printStackTrace();
							return false;
						}
					}
					else {
						try {
							flf = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
						}
						catch (UnsupportedFlavorException e) {
							// Doesn't have a file list, no biggy.
						}
						catch (IOException e) {
							System.out.println("LocalFilePnl.DragDropHandler.importData: DataFlavor.javaFileListFlavor IOException!");
							e.printStackTrace();
							return false;
						}
						catch (ClassCastException e) {
							System.out.println("LocalFilePnl.DragDropHandler.importData: data not class List!");
							e.printStackTrace();
							return false;
						}
					}
				}
			}

			if (flf != null) {
				JLabel lbl = new JLabel("Receiving files:");
				JDialog dlg = new JDialog(JOptionPane.getFrameForComponent(LocalFilePnl.this));
				JOptionPane pane = new JOptionPane(lbl);
				dlg.setContentPane(pane);
				dlg.setSize(250,100);
				dlg.setLocationRelativeTo(LocalFilePnl.this);
				dlg.setVisible(true);
				try { Thread.sleep(2000); } catch (Exception e) {}
				String dir = getFileTM().getDirectory();
				
				for (int i = 0; i < flf.size(); i++) {
					// copy the files to here...
					File f = (File) flf.get(i);
					lbl.setText(f.getName());
					File fc = new File(dir,f.getName());
					/*
					if (fc.exists()) {
						int opt = JOptionPane.showConfirmDialog(LocalFilePnl.this,"File exists, ok to replace?");
						if (opt == JOptionPane.NO_OPTION) continue;
						if (opt == JOptionPane.CANCEL_OPTION) break;
					}
					*/
					try {
						FileInputStream fi = new FileInputStream(f);
						FileOutputStream fo = new FileOutputStream(fc);
						byte[] b = new byte[1024];
						int len;
						while ((len = fi.read(b)) != -1) fo.write(b,0,len);
						fi.close();
						fo.close();
					}
					catch (Exception e) {
						System.out.println("DragDropHandler.importData: " + e.getMessage());
						System.out.println("DragDropHandler.importData: import stopped.");
						JOptionPane.showMessageDialog(LocalFilePnl.this,"Error processing drop of " + f.getName(),"Drop Aborted",JOptionPane.ERROR_MESSAGE);
						refresh();
						dlg.setVisible(false);
						return false;
					}
					try { Thread.sleep(2000); } catch (Exception e) {}
				}
				dlg.setVisible(false);
				refresh();
				return true;
			}

			if (fbf != null) {
				String directory = getLocalTM().getDirectory();
				if (getLocalTbl().getSelectedRowCount() == 1) {
					int i = getLocalTbl().getSelectedRow();
					String name = getLocalTM().getFileName(getLocalSortTM().getUnsortedIndex(i));
					File file = new File(directory,name);
					fbf.sendBack(file);
				}
				else {
					fbf.sendBack(directory);
				}

				return true;
			}

			return false;
		}
	}
	private class DragTransferable implements Transferable {
		private DataFlavor fileListFlavor = null;
		private DataFlavor[] flavors = null;
		private ArrayList flavorArray = new ArrayList();
		private Vector filesTransferred = null;
		// We need to provide only a list of files.
		public DragTransferable(boolean nativeFileListOk) {
			try {
				fileListFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=oem.edge.ed.odc.dsmp.client.FileListFlavor");
				flavorArray.add(fileListFlavor);
			} catch (ClassNotFoundException e) {
				System.out.println("LocalFilePnl.DragDropHandler: " + e.getMessage());
			}
			if (nativeFileListOk) {
				flavorArray.add(DataFlavor.javaFileListFlavor);
			}
			flavors = new DataFlavor[flavorArray.size()];
			for (int i = 0; i < flavors.length; i++) {
				flavors[i] = (DataFlavor) flavorArray.get(i);
			}
		}
		public boolean isDataFlavorSupported(DataFlavor f) {
			return flavorArray.contains(f);
		}
		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}
		public void completeMove() {
			if (filesTransferred != null) {
				Enumeration e = filesTransferred.elements();
				while (e.hasMoreElements()) {
					File file = (File) e.nextElement();
					if (file.exists())
						System.out.println("DragTransferable.completeMove: Should delete " + file.getPath());
					else
						System.out.println("DragTransferable.completeMove: File missing " + file.getPath());
				}
			}
		}
		public Object getTransferData(DataFlavor f) {
			if (isDataFlavorSupported(f)) {
				filesTransferred = new Vector();
				int s[] = getLocalTbl().getSelectedRows();
				String directory = getLocalTM().getDirectory();
				for (int i = 0; i < s.length; i++) {
					String name = getLocalTM().getFileName(getLocalSortTM().getUnsortedIndex(s[i]));
					File file = new File(directory,name);
					filesTransferred.addElement(file);
				}
				
				if (fileListFlavor != null && f.equals(fileListFlavor)) {
					FileListFlavor flf = new FileListFlavor();
					flf.files = filesTransferred;
					return flf;
				}
				else {
					return filesTransferred;
				}
			}

			return null;
		}
	}
	private DragDropHandler dragDropHandler = null;
	
	/**
	 * Provides event support for most toolbar and menu items.
	 */
	private class ToolBarMenuHandler implements ActionListener, DocumentListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getDeleteBtn() || e.getSource() == getDeleteMI()) {
				doDelete();
			}
			else if (e.getSource() == getHomeBtn() || e.getSource() == getHomeMI()) {
				chgDirToHome();
			}
			else if (e.getSource() == getUpBtn() || e.getSource() == getUpMI()) {
				chgDirToParent();
			}
			else if (e.getSource() == getRefreshBtn() || e.getSource() == getRefreshMI()) {
				refresh();
			}
			else if (e.getSource() == getDrivesCB()) {
				chgLocalDrive();
			}
			else if (e.getSource() == getPathTF() || e.getSource() == getPathOkBtn()) {
				chgLocalDir();
			}
			else if (e.getSource() == getFilterTF() || e.getSource() == getFilterOkBtn()) {
				getLocalTM().setLocalFilter(getFilterTF().getText(),true,isWin);
			}
		}
		public void changedUpdate(DocumentEvent e) {
			filterChanged();
		}
		public void insertUpdate(DocumentEvent e) {
			filterChanged();
		}
		public void removeUpdate(DocumentEvent e) {
			filterChanged();
		}
		private void filterChanged() {
			int l = getFilterTF().getDocument().getLength();
			if (l == 0 || (l == 1 && getFilterTF().getText().equals("*"))) {
				getLocalTM().setLocalFilter(null,true,isWin);
			}
			
		}
		public void chgDirToHome() {
			busyCursor(true);

			String dir = System.getProperty("user.home");
			updateDirectory(dir);

			busyCursor(false);
		}
		public void chgDirToParent() {
			busyCursor(true);

			File dir = new File(getFileTM().getDirectory());
			String parent = dir.getParent();
			if (parent != null) {
				File parentDir = new File(parent);
				updateDirectory(parentDir.getPath());
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
						updateDirectory(dir);
						busyCursor(false);
					}
					else
						JOptionPane.showMessageDialog(LocalFilePnl.this,f.getName() + "is not a directory!","Error",JOptionPane.ERROR_MESSAGE);
				}
				else {
					busyCursor(true);
					updateDirectory(dir);
					busyCursor(false);
					JOptionPane.showMessageDialog(LocalFilePnl.this,f.getPath(),"Path Not Found",JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		/**
		 * Comment
		 */
		public void chgLocalDrive() {
			File f = (File) getDrivesCB().getSelectedItem();

			if (f.exists()) {
				busyCursor(true);
				updateDirectory(f.toString());
				busyCursor(false);
			}
			else
				JOptionPane.showMessageDialog(LocalFilePnl.this,f.getPath(),"Drive Not Found",JOptionPane.ERROR_MESSAGE);
		}
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

			int result = JOptionPane.showConfirmDialog(LocalFilePnl.this,msg,"Confirm Delete",JOptionPane.YES_NO_OPTION);

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
						JOptionPane.showMessageDialog(LocalFilePnl.this,"Unable to delete " + name,"Error",JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				// Update data model and make directory selected.
				getFileTM().populateLocal();
				busyCursor(false);
			}
		}
	}
	private ToolBarMenuHandler toolBarMenuHandler = new ToolBarMenuHandler();

	private class NewFolderHandler extends WindowAdapter implements ActionListener, DocumentListener {
		private JButton folderCanBtn = null;
		private JDialog folderDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="412,343"
		private JLabel folderLbl = null;
		private JButton folderOkBtn = null;
		private JTextField folderTF = null;
		private JPanel folderCP = null;
		private JPanel folderBtnPnl = null;

		/**
		 * ActionEvent handler.
		 */
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == getNewDirBtn() || e.getSource() == getNewDirMI()) {
					Component c = LocalFilePnl.this;
	
					while (c != null && ! (c instanceof JFrame || c instanceof JDialog)) {
						c = c.getParent();
					}
	
					textChgFolder();
					getFolderDlg().setLocationRelativeTo(c);
					getFolderDlg().setVisible(true);
				}
				else if (e.getSource() == getFolderOkBtn() || e.getSource() == getFolderTF()) {
					// Create the folder.

					// Hit enter on an empty text field? Do nothing.
					if (! getFolderOkBtn().isEnabled())
						return;

					// Ditch the dialog and make the folder.
					getFolderDlg().dispose();
					busyCursor(true);

					// Create the folder
					File f = new File(getFileTM().getDirectory(),getFolderTF().getText().trim());
					if (! f.mkdir()) {
						busyCursor(false);
						JOptionPane.showMessageDialog(LocalFilePnl.this,"Unable to create directory","Error",JOptionPane.ERROR_MESSAGE);
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
				else if (e.getSource() == getFolderCanBtn()) {
					getFolderDlg().dispose();
				}
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}    
		}

		/**
		 * Document event handlers.
		 */
		public void changedUpdate(DocumentEvent e) {
			textChgFolder();
		}
		public void insertUpdate(DocumentEvent e) {
			textChgFolder();
		}
		public void removeUpdate(DocumentEvent e) {
			textChgFolder();
		}
		public void textChgFolder() {
			String folder = getFolderTF().getText();
			getFolderOkBtn().setEnabled(folder != null && folder.length() > 0);
		}

		/**
		 * Window event handlers.
		 */
		public void windowOpened(java.awt.event.WindowEvent e) {    
			try {
				getFolderTF().requestFocus();
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}

		/**
		 * New folder dialog objects.
		 */
		private JDialog getFolderDlg() {
			if (folderDlg == null) {
				try {
					folderDlg = new javax.swing.JDialog();
					folderDlg.setName("FolderDlg");
					folderDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
					folderDlg.setBounds(19, 543, 251, 131);
					folderDlg.setModal(true);
					folderDlg.setTitle("Create Local Folder");
					folderDlg.setContentPane(getFolderCP());
					folderDlg.addWindowListener(this);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
			return folderDlg;
		}
		private JPanel getFolderCP() {
			if (folderCP == null) {
				try {
					folderCP = new javax.swing.JPanel();
					folderCP.setName("JDialogContentPane1");
					folderCP.setLayout(new java.awt.GridBagLayout());

					java.awt.GridBagConstraints constraintsFolderLbl = new java.awt.GridBagConstraints();
					constraintsFolderLbl.gridx = 0; constraintsFolderLbl.gridy = 0;
					constraintsFolderLbl.insets = new java.awt.Insets(10, 10, 0, 5);
					folderCP.add(getFolderLbl(), constraintsFolderLbl);

					java.awt.GridBagConstraints constraintsFolderTF = new java.awt.GridBagConstraints();
					constraintsFolderTF.gridx = 1; constraintsFolderTF.gridy = 0;
					constraintsFolderTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
					constraintsFolderTF.weightx = 1.0;
					constraintsFolderTF.insets = new java.awt.Insets(10, 0, 0, 10);
					folderCP.add(getFolderTF(), constraintsFolderTF);

					java.awt.GridBagConstraints constraintsJPanel = new java.awt.GridBagConstraints();
					constraintsJPanel.gridx = 0; constraintsJPanel.gridy = 1;
					constraintsJPanel.gridwidth = 0;
					constraintsJPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
					constraintsJPanel.anchor = java.awt.GridBagConstraints.SOUTH;
					constraintsJPanel.weightx = 1.0;
					constraintsJPanel.weighty = 1.0;
					constraintsJPanel.insets = new java.awt.Insets(10, 10, 10, 10);
					folderCP.add(getFolderBtnPnl(), constraintsJPanel);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
			return folderCP;
		}
		private JLabel getFolderLbl() {
			if (folderLbl == null) {
				try {
					folderLbl = new javax.swing.JLabel();
					folderLbl.setName("FolderLbl");
					folderLbl.setText("Folder Name:");
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
			return folderLbl;
		}
		private JTextField getFolderTF() {
			if (folderTF == null) {
				try {
					folderTF = new javax.swing.JTextField();
					folderTF.setName("FolderTF");
					folderTF.addActionListener(this);
					folderTF.getDocument().addDocumentListener(this);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
			return folderTF;
		}
		private JPanel getFolderBtnPnl() {
			if (folderBtnPnl == null) {
				try {
					folderBtnPnl = new javax.swing.JPanel();
					folderBtnPnl.setName("JPanel");
					folderBtnPnl.setLayout(new java.awt.GridBagLayout());

					java.awt.GridBagConstraints constraintsFolderOkBtn = new java.awt.GridBagConstraints();
					constraintsFolderOkBtn.gridx = 0; constraintsFolderOkBtn.gridy = 0;
					constraintsFolderOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
					folderBtnPnl.add(getFolderOkBtn(), constraintsFolderOkBtn);

					java.awt.GridBagConstraints constraintsFolderCanBtn = new java.awt.GridBagConstraints();
					constraintsFolderCanBtn.gridx = 1; constraintsFolderCanBtn.gridy = 0;
					constraintsFolderCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
					folderBtnPnl.add(getFolderCanBtn(), constraintsFolderCanBtn);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
			return folderBtnPnl;
		}
		private JButton getFolderOkBtn() {
			if (folderOkBtn == null) {
				try {
					folderOkBtn = new javax.swing.JButton();
					folderOkBtn.setName("FolderOkBtn");
					folderOkBtn.setText("Ok");
					folderOkBtn.addActionListener(this);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
			return folderOkBtn;
		}
		private JButton getFolderCanBtn() {
			if (folderCanBtn == null) {
				try {
					folderCanBtn = new javax.swing.JButton();
					folderCanBtn.setName("FolderCanBtn");
					folderCanBtn.setText("Cancel");
					folderCanBtn.addActionListener(this);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
			return folderCanBtn;
		}
	}
	private NewFolderHandler newFolderHandler = new NewFolderHandler();

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
 * @param newListener LocalFilePnlListener
 */
public void addLocalFilePnlListener(LocalFilePnlListener newListener) {
	fieldLocalFilePnlListenerEventMulticaster = LocalFilePnlListenerEventMulticaster.add(fieldLocalFilePnlListenerEventMulticaster, newListener);
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

	if (e.isFileEnded()) {
		if (e.source.equals(getFileTM().getDirectory())) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					refresh();
				}
			});
		}
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
			ivjDeleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/trash.gif")));
			ivjDeleteBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjDeleteBtn.setEnabled(false);
			ivjDeleteBtn.addActionListener(toolBarMenuHandler);
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
			ivjDeleteMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/trash.gif")));
			ivjDeleteMI.setMnemonic('d');
			ivjDeleteMI.setText("Delete files");
			ivjDeleteMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,InputEvent.CTRL_MASK,false));
			ivjDeleteMI.addActionListener(toolBarMenuHandler);
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
			ivjDrivesCB.addActionListener(toolBarMenuHandler);
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
 * @return FileTableSorter
 */
private FileTableSorter getFileSortTM() {
	if (ivjFileSortTM == null) {
		try {
			ivjFileSortTM = new FileTableSorter();
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
 * @return FileTableModel
 */
private FileTableModel getFileTM() {
	if (ivjFileTM == null) {
		try {
			ivjFileTM = new FileTableModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileTM;
}
/**
 * This method initializes filterOkBtn	
 * 	
 * @return javax.swing.JButton	
 */    
private JButton getFilterOkBtn() {
	if (filterOkBtn == null) {
		filterOkBtn = new JButton();
		filterOkBtn.setText("Search");
		filterOkBtn.setMargin(new java.awt.Insets(0,2,0,2));
		filterOkBtn.setToolTipText("Apply file name pattern to list");
		filterOkBtn.addActionListener(toolBarMenuHandler);
	}
	return filterOkBtn;
}
/**
 * This method initializes filterTF	
 * 	
 * @return javax.swing.JTextField	
 */    
private JTextField getFilterTF() {
	if (filterTF == null) {
		filterTF = new JTextField();
		filterTF.setToolTipText("<html><p>Enter a pattern to limit the file list. You may use an * to match any number of characters, or a ? to match a single character.</p></html>");
		filterTF.setText("*");
		filterTF.getDocument().addDocumentListener(toolBarMenuHandler);
		filterTF.addActionListener(toolBarMenuHandler);
	}
	return filterTF;
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
			ivjHomeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/home.gif")));
			ivjHomeBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjHomeBtn.addActionListener(toolBarMenuHandler);
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
			ivjHomeMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/home.gif")));
			ivjHomeMI.setMnemonic('h');
			ivjHomeMI.setText("Home");
			ivjHomeMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,InputEvent.CTRL_MASK,false));
			ivjHomeMI.addActionListener(toolBarMenuHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjHomeMI;
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
 * @return FileTableSorter
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
 * @return FileTableModel
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
			ivjNewDirBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/newfold.gif")));
			ivjNewDirBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjNewDirBtn.addActionListener(newFolderHandler);
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
			ivjNewDirMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/newfold.gif")));
			ivjNewDirMI.setMnemonic('n');
			ivjNewDirMI.setText("New folder");
			ivjNewDirMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT,InputEvent.CTRL_MASK,false));
			ivjNewDirMI.addActionListener(newFolderHandler);
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
 * This method initializes pathOkBtn	
 * 	
 * @return javax.swing.JButton	
 */    
private JButton getPathOkBtn() {
	if (pathOkBtn == null) {
		pathOkBtn = new JButton();
		pathOkBtn.setText("Apply");
		pathOkBtn.setMargin(new java.awt.Insets(0,2,0,2));
		pathOkBtn.setToolTipText("Change to path specified");
		pathOkBtn.addActionListener(toolBarMenuHandler);
	}
	return pathOkBtn;
}
/**
 * Return the LocalPathPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getPathPnl() {
	if (ivjPathPnl == null) {
		try {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			filterLbl = new JLabel();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			ivjPathPnl = new javax.swing.JPanel();
			ivjPathPnl.setName("PathPnl");
			ivjPathPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsPathLbl = new java.awt.GridBagConstraints();
			constraintsPathLbl.gridx = 0; constraintsPathLbl.gridy = 0;
			constraintsPathLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPathLbl.insets = new java.awt.Insets(0, 0, 0, 5);
			java.awt.GridBagConstraints constraintsPathTF = new java.awt.GridBagConstraints();
			constraintsPathTF.gridx = 1; constraintsPathTF.gridy = 0;
			constraintsPathTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPathTF.weightx = 1.0;
			constraintsPathTF.gridwidth = 1;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.insets = new java.awt.Insets(3,0,0,5);
			filterLbl.setText("File search:");
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.insets = new java.awt.Insets(3,0,0,0);
			gridBagConstraints11.gridx = 2;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints21.gridx = 2;
			gridBagConstraints21.gridy = 1;
			gridBagConstraints21.insets = new java.awt.Insets(3,0,0,0);
			ivjPathPnl.add(getPathLbl(), constraintsPathLbl);
			ivjPathPnl.add(getPathTF(), constraintsPathTF);
			ivjPathPnl.add(filterLbl, gridBagConstraints1);
			ivjPathPnl.add(getFilterTF(), gridBagConstraints2);
			ivjPathPnl.add(getPathOkBtn(), gridBagConstraints11);
			ivjPathPnl.add(getFilterOkBtn(), gridBagConstraints21);
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
			ivjPathTF.addActionListener(toolBarMenuHandler);
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
			ivjRefreshBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/refresh.gif")));
			ivjRefreshBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjRefreshBtn.addActionListener(toolBarMenuHandler);
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
			ivjRefreshMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/refresh.gif")));
			ivjRefreshMI.setMnemonic('r');
			ivjRefreshMI.setText("Refresh");
			ivjRefreshMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,InputEvent.CTRL_MASK,false));
			ivjRefreshMI.addActionListener(toolBarMenuHandler);
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
			ivjUpBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/upfolder.gif")));
			ivjUpBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjUpBtn.addActionListener(toolBarMenuHandler);
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
			ivjUploadBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/upload.gif")));
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
			ivjUploadMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/upload.gif")));
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
			ivjUpMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/upfolder.gif")));
			ivjUpMI.setMnemonic('p');
			ivjUpMI.setText("Parent directory");
			ivjUpMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,InputEvent.CTRL_MASK,false));
			ivjUpMI.addActionListener(toolBarMenuHandler);
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
	updateDirectory(dir);
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

		// Fix the background color of the viewport to match the table.
		getFileSP().getViewport().setBackground(getFileTbl().getBackground());
		
		// Determine our platform.
		isWin = (System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1);

		// Initialize filter
		getLocalTM().setLocalFilter(null,true,isWin);

		// Enable drag and drop support.
		if (enableDND) {
			dragDropHandler = new DragDropHandler();
			Class[] parms = { TransferHandler.class };
			Object[] args = { dragDropHandler };
			// getFileSP().setTransferHandler(dragDropHandler);
			Method meth = getFileSP().getClass().getMethod("setTransferHandler",parms);
			meth.invoke(getFileSP(),args);
			// getFileTbl().setTransferHandler(dragDropHandler);
			meth = getFileTbl().getClass().getMethod("setTransferHandler",parms);
			meth.invoke(getFileTbl(),args);
			// getFileTbl().setDragEnabled(true);
			parms[0] = Boolean.TYPE;
			args[0] = Boolean.TRUE;
			meth = getFileTbl().getClass().getMethod("setDragEnabled",parms);
			meth.invoke(getFileTbl(),args);
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
	String dir = getFileTM().getDirectory();
	updateDirectory(dir);

	busyCursor(false);
}
/**
 * 
 * @param newListener LocalFilePnlListener
 */
public void removeLocalFilePnlListener(LocalFilePnlListener newListener) {
	fieldLocalFilePnlListenerEventMulticaster = LocalFilePnlListenerEventMulticaster.remove(fieldLocalFilePnlListenerEventMulticaster, newListener);
	return;
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
				updateDirectory(dir);
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
			String dir;
			if (getFileTM().getDirectory().endsWith(File.separator))
				dir = getFileTM().getDirectory() + name;
			else
				dir = getFileTM().getDirectory() + File.separator + name;
			updateDirectory(dir);
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
private void updateDirectory(String dir) {
	getFileTbl().clearSelection();
	getFileTM().setDirectory(dir);
	getPathTF().setText(getFileTM().getDirectory());
	getFileTM().populateLocal();
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
