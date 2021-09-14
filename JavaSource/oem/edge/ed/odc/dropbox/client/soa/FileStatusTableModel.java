package oem.edge.ed.odc.dropbox.client.soa;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.rmi.RemoteException;
import java.text.*;
import oem.edge.ed.odc.dropbox.client.SizeCellRenderer;
import oem.edge.ed.odc.dropbox.client.TimeCellRenderer;
import oem.edge.ed.odc.dropbox.common.DropboxGenerator;
import oem.edge.ed.odc.dropbox.common.FileInfo;
import oem.edge.ed.odc.dropbox.common.PackageInfo;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dsmp.client.FileStatusDirectionRenderer;
import oem.edge.ed.odc.dsmp.client.FileStatusEvent;
import oem.edge.ed.odc.dsmp.client.FileStatusListener;
import oem.edge.ed.odc.dsmp.client.FileStatusRenderer;
import oem.edge.ed.odc.dsmp.common.DboxException;

import javax.swing.table.TableCellRenderer;
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
 * Creation date: (10/21/2002 9:56:37 AM)
 * @author: Mike Zarnick
 */
public class FileStatusTableModel extends AbstractTableModel implements OperationListener {
	//static public String[] headings = { "", "File", "Source", "Destination", "Size", "Status" };
	private String[] headings = { " ", "File", "Source", "Destination", "Size", "Status", "Speed", "Time" };
	private int[] columnWidths = { 14, 200, 100, 100, 85, 108, 80, 80 };
	private Class[] columnClasses = { Boolean.class, String.class, String.class, String.class, Long.class, String.class, String.class, Long.class };
	private TableCellRenderer[] columnRenderers = { new FileStatusDirectionRenderer(), null, null, null, new SizeCellRenderer(), new FileStatusRenderer(), null, new TimeCellRenderer() };
	static private int SIZE = 4;
	static private int STATUS = 5;
	static private int RATE = 6;
	static private int TIME = 7;
	static private int MAXACTIVE = 3;
	static private String INIT = "Initializing...";
	static private String MD5 = "Checking MD5...";
	static private String QUEUED = "Queued";
	static private String CANCEL = "Canceled";
	static private String FAIL = "Failed";
	static private String COMPLETE = "Completed";
	private byte handle = (byte) 0;
	private int active = 0;
	private Vector queue = new Vector();
	private Vector stats = new Vector();
	private Hashtable lookup = new Hashtable();
	private Hashtable uploads = new Hashtable();
	private DropboxAccess dboxAccess = null;
	private ConnectionFactory dboxFactory = null;
	private FileStatusListener fileStatusListener = null;

	private class Package {
		Boolean isExpress = Boolean.FALSE;
		int fileCount = 0;

		Package(Boolean isExpress, int count) {
			this.isExpress = isExpress;
			this.fileCount = count;
		}
	}

	private class Status {
		Boolean upload = Boolean.FALSE;
		long id = -1;
		String name = null;
		String fname = null;
		String lclDir = null;
		String pkg = null;
		String md5 = null;
		Long pkgID = null;
		Long size = null;
		Object status = INIT;
		long lastUpdate = 0;
		String speed = "";
		Long time = null;
		String detail = null;
		boolean noOverwrite = false;
		Operation op = null;

		Status(Boolean upload, long id, String name, String fname, String lclDir, String pkg, Long pkgID, Long size) {
			this.upload = upload;
			this.id = id;
			this.name = name;
			this.fname = fname;
			this.lclDir = lclDir;
			this.pkg = pkg;
			this.pkgID = pkgID;
			this.size = size;
		}
	}

	private class TableUpdate implements Runnable {
		int row;
		int col;
		boolean insert;
		public TableUpdate(boolean insert, int r, int c) {
			this.insert = insert;
			this.row = r;
			this.col = c;
		}
		public void run() {
			if (col == -1) {
				if (insert) {
					fireTableRowsInserted(row,row);
				}
				else {
					fireTableRowsUpdated(row,row);
				}
			}
			else {
				fireTableCellUpdated(row,col);
			}
		}
	}

	private class Abort extends Thread {
		private Operation o;

		public Abort(Operation o) {
			this.o = o;
			setPriority(Thread.NORM_PRIORITY);
		}
		public void run() {
			o.abort();
		}
	}

	private class ProcessOperation extends Thread {
		private Status s;
		public ProcessOperation(Status s) {
			this.s = s;
			setPriority(Thread.NORM_PRIORITY);
		}
		public void run() {
			try {
				s.op.process();
			} catch (DboxException e) {
				int r = stats.indexOf(s);
				s.status = FAIL;
				s.detail = e.getMessage();
				SwingUtilities.invokeLater(new TableUpdate(false,r,STATUS));

				// Fire a FileStatusEvent with FileInfo object (STATUS_NONE) and src directory
				FileInfo fi = new FileInfo();
				fi.setFileName(s.name);
				fi.setFileStatus(DropboxGenerator.STATUS_INCOMPLETE);
				fireFileStatusEvent(new FileStatusEvent(fi,s.pkgID.longValue(),s.lclDir,s.detail,false,s.upload));
			}
		}
	}

	private class Upload extends Thread {
		private Status s;
		private File f;

		public Upload(Status s,File f) {
			this.s = s;
			this.f = f;
			setPriority(Thread.NORM_PRIORITY);
		}
		public void run() {
			String error;

			try {
				long pkgID = s.pkgID.longValue();
	
				s.id = dboxAccess.uploadFileToPackage(pkgID,s.fname,s.size.longValue());

				s.op = new UploadOperation(dboxAccess,dboxFactory,f,pkgID,s.id);
				s.op.addOperationListener(FileStatusTableModel.this);
				lookup.put(new Long(s.id),s);

				if (! jobStarted(s)) {
					int r = stats.indexOf(s);
					s.status = QUEUED;
					SwingUtilities.invokeLater(new TableUpdate(false,r,STATUS));
				}
				
				// Fire a FileStatusEvent with FileInfo object (STATUS_NONE) and src directory
				FileInfo fi = new FileInfo();
				fi.setFileId(s.id);
				fi.setFileName(s.name);
				fi.setFileSize(f.length());
				fi.setFileStatus(s.status == CANCEL ? DropboxGenerator.STATUS_INCOMPLETE : DropboxGenerator.STATUS_NONE);
				fireFileStatusEvent(new FileStatusEvent(fi,s.pkgID.longValue(),s.lclDir,null,true,s.upload));

				return;
			} catch (DboxException e) {
				error = e.getMessage();
			} catch (RemoteException e) {
				error = e.getMessage();
			}

			int r = stats.indexOf(s);
			s.status = FAIL;
			s.detail = error;
			SwingUtilities.invokeLater(new TableUpdate(false,r,STATUS));

			// Fire a FileStatusEvent with FileInfo object (STATUS_NONE) and src directory
			FileInfo fi = new FileInfo();
			fi.setFileName(s.name);
			fi.setFileStatus(DropboxGenerator.STATUS_INCOMPLETE);
			fireFileStatusEvent(new FileStatusEvent(fi,s.pkgID.longValue(),s.lclDir,s.detail,false,s.upload));
		}
	}

/**
 * FileListModel constructor comment.
 */
public FileStatusTableModel() {
	super();
	DefaultTableCellRenderer r = new DefaultTableCellRenderer();
	r.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
	columnRenderers[RATE] = r;
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 3:41:31 PM)
 * @param l oem.edge.ed.odc.dropbox.client.FileStatusListener
 */
public void addFileStatusListener(FileStatusListener l) {
	if (l == null)
		return;

	fileStatusListener = DBEventMulticaster.addFileStatusListener(fileStatusListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 12:14:09 PM)
 */
public void cancel(int[] sel) {
	synchronized (queue) {
		synchronized (stats) {
			for (int i = 0; i < sel.length; i++) {
				Status s = (Status) stats.elementAt(sel[i]);
				if (s != null && s.op != null) {
					// Is the job queued?
					boolean queued = queue.contains(s.op);

					// If so, then remove it from the queue.
					if (queued) {
						queue.removeElement(s.op);

						s.status = CANCEL;
						fireTableRowsUpdated(sel[i],sel[i]);
					}

					// Job is actually running.
					else {
						Abort a = new Abort(s.op);
						a.start();
					}
				}
			}
		}
	}
}
public void cancelExpress(long pkgID) {
	// remove is synchronized
	uploads.remove(new Long(pkgID));
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 12:14:09 PM)
 */
public void clear() {
	stats.removeAllElements();
	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 2:21:13 PM)
 * @param name java.lang.String
 * @param source java.lang.String
 * @param destination java.lang.String
 */
public String downloadFile(PackageInfo pkg, FileInfo f, String name, String lclDir) {
	return downloadFile(pkg,f,name,lclDir,false);
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 2:21:13 PM)
 * @param name java.lang.String
 * @param source java.lang.String
 * @param destination java.lang.String
 */
public String downloadFile(PackageInfo pkg, FileInfo fInfo, String name, String lclDir, boolean noOverWrite) {
	// Lock-up the stats vector until we determine this file is new and we add it.
	synchronized (stats) {
		Status s;

		// See if the file name at lclDir is already the target of an active operation.
		for (int i = 0; i < stats.size(); i++) {
			s = (Status) stats.elementAt(i);
			if (s.upload == Boolean.FALSE && s.name.equals(name) && s.lclDir == lclDir && isActive(i)) {
				return "download already in progress.";
			}
		}

		// A new Status object is added to the table.
		String fname = name;
		if (File.separatorChar != '/') {
			fname = fname.replace('/',File.separatorChar);
		}

		s = new Status(Boolean.FALSE,fInfo.getFileId(),name,fname,lclDir,pkg.getPackageName(),new Long(pkg.getPackageId()),new Long(fInfo.getFileSize()));
		s.noOverwrite = noOverWrite;
		stats.insertElementAt(s,0);
		SwingUtilities.invokeLater(new TableUpdate(true,0,-1));

		// Prepare for the download.
		File f = new File(s.lclDir,s.fname);
		File p = new File(f.getParent());

		// If the file exists and no overwrite is set, we are not allowed
		// to download the file, so we will fail the operation.
		if (f.exists() && noOverWrite) {
			s.status = FAIL;
			s.detail = "Over-write not allowed.";
			SwingUtilities.invokeLater(new TableUpdate(false,0,-1));
			return null;
		}

		// Create directory for file, if necessary.
		if (! p.exists()) {
			boolean created = p.mkdirs();

			// Failed to create directory.
			if (! created && ! p.exists()) {
				s.detail = "Could not create directory needed for file.";
				s.status = FAIL;
				SwingUtilities.invokeLater(new TableUpdate(false,0,-1));
				return null;
			}
		}

		// Set up the download operation.
		s.op = new DownloadOperation(dboxAccess,dboxFactory,f,pkg.getPackageId(),fInfo);
		s.op.addOperationListener(this);
		lookup.put(new Long(fInfo.getFileId()),s);

		// Attempt to start the download, may be queued.
		if (! jobStarted(s)) {
			s.status = QUEUED;
			SwingUtilities.invokeLater(new TableUpdate(false,0,-1));
		}
	}
	
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 3:43:53 PM)
 * @param e oem.edge.ed.odc.dropbox.client.FileStatusEvent
 */
private void fireFileStatusEvent(FileStatusEvent e) {
	if (fileStatusListener != null)
		fileStatusListener.fileStatusAction(e);
}
/**
 * getColumnClass method comment.
 */
public Class getColumnClass(int c) {
	return columnClasses[c];
}
/**
 * getSize method comment.
 */
public int getColumnCount() {
	return headings.length;
}
/**
 * getSize method comment.
 */
public String getColumnName(int column) {
	return headings[column];
}
/**
 * getColumnWidth method comment.
 */
public TableCellRenderer getColumnRenderer(int c) {
	return columnRenderers[c];
}
/**
 * getColumnWidth method comment.
 */
public int getColumnWidth(int c) {
	return columnWidths[c];
}
/**
 * getSize method comment.
 */
public int getRowCount() {
	return stats.size();
}
/**
 * getElementAt method comment.
 */
public Object getValueAt(int row,int column) {
	if (stats != null && row < stats.size()) {
		Status stat = (Status) stats.elementAt(row);
		switch (column) {
			case 0: return stat.upload;
			case 1: return stat.name;
			case 2: if (stat.upload.booleanValue())
						return stat.lclDir;
					else
						return stat.pkg;
			case 3: if (stat.upload.booleanValue())
						return stat.pkg;
					else
						return stat.lclDir;
			case 4: return stat.size;
			case 5: return stat.status;
			case 6: return stat.speed;
			case 7: return stat.time;
			default: return null;
		}
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 10:09:58 AM)
 * @return boolean
 * @param row int
 */
public boolean isActive(int row) {
	boolean active = false;

	if (row < stats.size()) {
		Status s = (Status) stats.elementAt(row);
		if (s.status instanceof String) {
			String stat = (String) s.status;
			active = stat != null && stat != CANCEL && stat != COMPLETE && stat != FAIL;
		}
		else
			active = true;
	}

	return active;
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 10:09:58 AM)
 * @return boolean
 * @param s Status
 */
private boolean isComplete(Status s) {
	return s != null && (s.status == FAIL || s.status == CANCEL || s.status == COMPLETE);
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 10:09:58 AM)
 * @return boolean
 * @param row int
 */
public boolean isFailed(int row) {
	return isFailed((Status) stats.elementAt(row));
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 10:09:58 AM)
 * @return boolean
 * @param s Status
 */
private boolean isFailed(Status s) {
	return s != null && s.status == FAIL;
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2003 9:01:14 AM)
 */
private void jobEnded() {
	synchronized (stats) {
		synchronized (queue) {
			// Over capacity?
			if (active > MAXACTIVE)
				active--;
			// Queue is not empty?
			else if (queue.size() > 0) {
				// Grab a job.
				Status s = (Status) queue.elementAt(0);
				queue.removeElementAt(0);


				// Start the operation.
				ProcessOperation po = new ProcessOperation(s);
				po.start();
			}
			// No other jobs to start, one less now.
			else
				active--;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2003 9:08:02 AM)
 * @return boolean
 * @param p oem.edge.ed.odc.dsmp.common.DSMPBaseProto
 */
private boolean jobStarted(Status s) {
	boolean started = true;

	synchronized(queue) {
		// Room for more jobs?
		if (active < MAXACTIVE) {
			// One more job running.
			active++;

			// Start the operation.
			ProcessOperation po = new ProcessOperation(s);
			po.start();
		}
		else {
			// No room, queue it.
			queue.addElement(s);
			started = false;
		}
	}

	return started;
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 12:43:31 PM)
 * @param e FTPStatusEvent
 */
public void operationUpdate(OperationEvent e) {
	Status s = (Status) lookup.get(new Long(e.getSource().getFileId()));
	int row = stats.indexOf(s);

	if (e.isMD5()) {
		if (s.status != CANCEL && s.status != FAIL) {
			s.status = MD5;
			SwingUtilities.invokeLater(new TableUpdate(false,row,STATUS));
		}
	}
	else if (e.isData()) {
		if (s.status != CANCEL && s.status != FAIL) {
			Integer pct = new Integer(e.getSource().percentDone());
			if (! s.status.equals(pct)) {
				s.status = pct;
				SwingUtilities.invokeLater(new TableUpdate(false,row,STATUS));
			}
			long ct = System.currentTimeMillis();
			if (ct - s.lastUpdate > 1000) {
				s.lastUpdate = ct;
				s.speed = (s.op.getXferRate() / 1024) + "KB/s";
				s.time = new Long(ct - s.op.getStartTime());
				SwingUtilities.invokeLater(new TableUpdate(false,row,RATE));
				SwingUtilities.invokeLater(new TableUpdate(false,row,TIME));
			}
		}
	}
	else {
		if (e.getSource().getStatus() != Operation.STATUS_FINISHED) {
			if (e.getSource().getStatus() == Operation.STATUS_ABORTED)
				s.status = CANCEL;
			else {
				// TODO: Need a failure reason.
				s.detail = "Unknown";
				s.status = FAIL;
			}

			// Any express package is now useless.
			uploads.remove(s.pkgID);
		}
		else {
			s.status = COMPLETE;
		}

		s.speed = (s.op.getXferRate() / 1024) + "KB/s";
		s.time = new Long(s.op.getEndTime() - s.op.getStartTime());

		SwingUtilities.invokeLater(new TableUpdate(false,row,-1));

		jobEnded();

		// Fire a FileStatusEvent with FileInfo object (STATUS_COMPLETE)
		FileInfo fi = new FileInfo();
		fi.setFileId(s.id);
		if (s.status == COMPLETE) {
			FileInfo sfi = s.upload.booleanValue() ? ((UploadOperation) s.op).getFileInfo() : null;
			if (sfi != null) {
				fi = sfi;
			}
			else {
				File f = new File(s.lclDir,s.fname);
				fi.setFileSize(f.length());
				fi.setFileStatus(DropboxGenerator.STATUS_COMPLETE);
			}
		}
		else {
			fi.setFileSize(0);
			fi.setFileStatus(DropboxGenerator.STATUS_INCOMPLETE);
		}
		fireFileStatusEvent(new FileStatusEvent(fi,s.pkgID.longValue(),s.lclDir,s.detail,false,s.upload));

		if (s.status == COMPLETE) {
			synchronized(uploads) {
				Package p = (Package) uploads.get(s.pkgID);
				if (p != null) {
					// Still other downloads to finish?
					if (p.fileCount > 1) {
						// Decrement count.
						p.fileCount--;
					}
					// Last file of package uploaded successfully.
					else {
						// Fire status event of package complete.
						uploads.remove(s.pkgID);
						if (p.isExpress.booleanValue()) {
							fireFileStatusEvent(new FileStatusEvent(s.pkgID.longValue()));
						}
					}
				}
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 12:14:09 PM)
 */
public void remove(int[] sel) {
	// Lock the data model.
	synchronized (stats) {
		// For each selected row, starting at the bottom, if it is
		// completed, remove it.
		for (int i = sel.length - 1; i >= 0; i--) {
			int j = sel[i];
			Status s = (Status) stats.elementAt(j);
			if (isComplete(s)) {
				stats.removeElementAt(j);
				fireTableRowsDeleted(j,j);
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 12:14:09 PM)
 */
public void removeAllCompleted() {
	// Lock the data model.
	synchronized (stats) {
		// For each row, starting at the bottom, if it is
		// completed, remove it.
		for (int i = stats.size() - 1; i >= 0; i--) {
			Status s = (Status) stats.elementAt(i);
			if (isComplete(s)) {
				stats.removeElementAt(i);
				fireTableRowsDeleted(i,i);
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 3:41:31 PM)
 * @param l oem.edge.ed.odc.dropbox.client.FileStatusListener
 */
public void removeFileStatusListener(FileStatusListener l) {
	if (l == null)
		return;

	fileStatusListener = DBEventMulticaster.removeFileStatusListener(fileStatusListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 11:22:45 AM)
 * @param newDispatcher DBDispatcher
 */
public void setDropboxAccess(DropboxAccess newDboxAccess, ConnectionFactory newDboxFactory) {
	if (dboxAccess != null) {
	}

	dboxAccess = newDboxAccess;
	dboxFactory = newDboxFactory;

	if (dboxAccess != null) {
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 12:33:39 PM)
 * @param parent javax.swing.JComponent
 * @param rows int[]
 */
public void showDetail(Component parent, int[] rows) {
	for (int i = 0; i < rows.length; i++) {
		Status s = (Status) stats.elementAt(rows[i]);
		if (s.detail != null)
			JOptionPane.showMessageDialog(parent,s.detail,s.name,JOptionPane.INFORMATION_MESSAGE);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 2:21:13 PM)
 * @param name java.lang.String
 * @param source java.lang.String
 * @param destination java.lang.String
 */
public String uploadFile(long pkgID, String name, String lclDir, String pkg) {
	// Lock-up the stats vector until we determine this file is new and we add it.
	synchronized (stats) {
		Status s;

		// See if the file name at destination is already the target of an active operation.
		for (int i = 0; i < stats.size(); i++) {
			s = (Status) stats.elementAt(i);
			if (s.upload == Boolean.TRUE && s.name.equals(name) && s.pkgID.longValue() == pkgID && isActive(i)) {
				return "upload already in progress.";
			}
		}

		Long pID = new Long(pkgID);
		synchronized (uploads) {
			Package p = (Package) uploads.get(pID);
			if (p == null) {
				p = new Package(Boolean.FALSE,1);
				uploads.put(pID,p);
			}
			else {
				p.fileCount++;
			}
		}

		// Get the file's length
		String fname = name;
		if (File.separatorChar != '/') {
			fname = fname.replace('/',File.separatorChar);
		}

		File f = new File(lclDir,fname);
		long len = f.length();

		// Add an entry to the status table.
		s = new Status(Boolean.TRUE,-1,name,fname,lclDir,pkg,pID,new Long(len));
		stats.insertElementAt(s,0);
		SwingUtilities.invokeLater(new TableUpdate(true,0,-1));

		// Start an upload thread to register the file w/ the package.
		Upload u = new Upload(s,f);
		u.start();
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 2:21:13 PM)
 * @param name java.lang.String
 * @param source java.lang.String
 * @param destination java.lang.String
 */
public void uploadFile(long pkgID, Vector names, String lclDir, String pkg) {
	Enumeration eNames = names.elements();

	while (eNames.hasMoreElements()) {
		String name = (String) eNames.nextElement();
		uploadFile(pkgID,name,lclDir,pkg);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 2:21:13 PM)
 * @param name java.lang.String
 * @param source java.lang.String
 * @param destination java.lang.String
 */
public void uploadFiles(boolean expressUp, long pkgID, File files[], String pkg) {
	// Add a Package object to the uploads hash to remember how many
	// files are being uploaded for this package. A FileStatusEvent
	// will be fired when all files have been successfully uploaded.
	Long pID = new Long(pkgID);
	synchronized (uploads) {
		Package p = (Package) uploads.get(pID);
		if (p == null) {
			p = new Package(expressUp ? Boolean.TRUE : Boolean.FALSE,files.length);
			uploads.put(pID,p);
		}
		else {
			p.fileCount += files.length;
		}
	}

	// Now upload each individual file.
	for (int i = 0; i < files.length; i++) {
		// Get the file's length
		long len = files[i].length();
		String dir = files[i].getParent();
		String name = files[i].getName();

		Status s;

		// Lock the data model.
		synchronized (stats) {
			// Add an entry to the status table.
			s = new Status(Boolean.TRUE,-1,name,name,dir,pkg,pID,new Long(len));
			stats.insertElementAt(s,0);
			SwingUtilities.invokeLater(new TableUpdate(true,0,-1));

			// Start an upload thread to register the file w/ the package.
			Upload u = new Upload(s,files[i]);
			u.start();
		}
	}
}
/**
 * This method should be called to upload files to a new package only. It assumes that
 * restarts are not possible.
 * Creation date: (10/28/2002 2:21:13 PM)
 * @param name java.lang.String
 * @param source java.lang.String
 * @param destination java.lang.String
 */
public void uploadFiles(boolean expressUp, long pkgID, String name[], String lclDir, String pkg) {
	// Add a Package object to the uploads hash to remember how many
	// files are being uploaded for this package. A FileStatusEvent
	// will be fired when all files have been successfully uploaded.
	Long pID = new Long(pkgID);
	synchronized (uploads) {
		Package p = (Package) uploads.get(pID);
		if (p == null) {
			p = new Package(expressUp ? Boolean.TRUE : Boolean.FALSE,name.length);
			uploads.put(pID,p);
		}
		else {
			p.fileCount += name.length;
		}
	}

	// Now upload each individual file.
	for (int i = 0; i < name.length; i++) {
		// Get the file's length
		String fname = name[i];
		if (File.separatorChar != '/') {
			fname = fname.replace('/',File.separatorChar);
		}

		File f = new File(lclDir,fname);
		long len = f.length();

		Status s;

		// Lock the data model.
		synchronized (stats) {
			// Add an entry to the status table.
			s = new Status(Boolean.TRUE,-1,name[i],fname,lclDir,pkg,pID,new Long(len));
			stats.insertElementAt(s,0);
			SwingUtilities.invokeLater(new TableUpdate(true,0,-1));

			// Start an upload thread to register the file w/ the package.
			Upload u = new Upload(s,f);
			u.start();
		}
	}
}
/**
 * This method should be called to upload files to a new package only. It assumes that
 * restarts are not possible.
 * Creation date: (10/28/2002 2:21:13 PM)
 * @param name java.lang.String
 * @param source java.lang.String
 * @param destination java.lang.String
 */
public void uploadFiles(boolean expressUp, long pkgID, Vector names, String lclDir, String pkg) {
	// Add a Package object to the uploads hash to remember how many
	// files are being uploaded for this package. A FileStatusEvent
	// will be fired when all files have been successfully uploaded.
	Long pID = new Long(pkgID);
	synchronized (uploads) {
		Package p = (Package) uploads.get(pID);
		if (p == null) {
			p = new Package(expressUp ? Boolean.TRUE : Boolean.FALSE,names.size());
			uploads.put(pID,p);
		}
		else {
			p.fileCount += names.size();
		}
	}

	// Now upload each individual file.
	Enumeration eNames = names.elements();
	while (eNames.hasMoreElements()) {
		// Get the file's length
		String name = (String) eNames.nextElement();
		String fname = name;
		if (File.separatorChar != '/') {
			fname = fname.replace('/',File.separatorChar);
		}

		File f = new File(lclDir,fname);
		long len = f.length();

		Status s;

		// Lock the data model.
		synchronized (stats) {
			// Add an entry to the status table.
			s = new Status(Boolean.TRUE,-1,name,fname,lclDir,pkg,pID,new Long(len));
			stats.insertElementAt(s,0);
			SwingUtilities.invokeLater(new TableUpdate(true,0,-1));

			// Start an upload thread to register the file w/ the package.
			Upload u = new Upload(s,f);
			u.start();
		}
	}
}
}
