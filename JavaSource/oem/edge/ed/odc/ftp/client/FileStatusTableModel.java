package oem.edge.ed.odc.ftp.client;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import oem.edge.ed.odc.dsmp.common.DSMPBaseProto;
import oem.edge.ed.odc.ftp.common.FTPGenerator;
import oem.edge.ed.odc.ftp.common.Operation;
import oem.edge.ed.odc.ftp.common.SendFileOperation;
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
 * Creation date: (10/21/2002 9:56:37 AM)
 * @author: Mike Zarnick
 */
public class FileStatusTableModel extends AbstractTableModel implements FTPListener, FTPStatusListener {
	//static public String[] headings = { "", "File", "Source", "Destination", "Size", "Status", "Speed", "Time" };
	static public String[] headings = { "", "File", "Source", "Destination", "Size", "Status" };
	static private int SIZE = 4;
	static private int STATUS = 5;
	static private int MAXACTIVE = 3;
	static private String INIT = "Initializing...";
	static private String QUEUED = "Queued";
	static private String CANCEL = "Canceled";
	static private String FAIL = "Failed";
	static private String COMPLETE = "Completed";
	private byte handle = (byte) 0;
	private int active = 0;
	private Vector queue = new Vector();
	private Vector stats = new Vector();
	private Hashtable lookup = new Hashtable();
	private FTPDispatcher dispatcher = null;

	private class Status {
		Boolean upload = Boolean.FALSE;
		String name = null;
		String source = null;
		String dest = null;
		String size = null;
		Object status = INIT;
		String speed = "";
		String time = "";
		String detail = null;
		Object op = null;

		Status(Boolean upload, String name, String src, String dest, String size) {
			this.upload = upload;
			this.name = name;
			source = src;
			this.dest = dest;
			this.size = size;
		}
	}
/**
 * FileListModel constructor comment.
 */
public FileStatusTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 12:14:09 PM)
 */
public void cancel(int[] sel) {
	synchronized (queue) {
		for (int i = 0; i < sel.length; i++) {
			Status s = (Status) stats.elementAt(sel[i]);
			if (s != null && s.op != null) {
				queue.removeElement(s.op);
				if (s.op instanceof Operation) {
					((Operation) s.op).endOperation(CANCEL);
				}
			}
		}
	}
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
public String downloadFile(String name, String source, String destination, Long len) {
	// See if the file name at destination is already the target of an active operation.
	for (int i = 0; i < stats.size(); i++) {
		Status s = (Status) stats.elementAt(i);
		if (s.upload == Boolean.FALSE && s.name.equals(name) && s.dest.equals(destination) && isActive(i)) {
			return "download already in progress.";
		}
	}

	// Get a unused handle for this file.
	Status s = new Status(Boolean.FALSE,name,source,destination,len.toString());
	stats.insertElementAt(s,0);
	fireTableRowsInserted(0,0);

	// Get a unused handle for this file.
	Byte h = null;

	synchronized (this) {
		h = new Byte(handle);
		while (lookup.get(h) != null) {
			handle++;
			h = new Byte(handle);
		}
		lookup.put(h,s);
	}

	// Try to generate the file's crc so we can restart, if the file
	// doesn't exist, we will start the transfer from the beginning.
	File f = new File(destination,name);
	boolean restart = false;
	int crc = 0;
	long ofs = 0;

	try {
		crc = dispatcher.calculateCRC(f);
		ofs = f.length();
		restart = true;
	}
	catch (IOException e) {
	}

	// Send download request (restarts if possible).
	DSMPBaseProto p = FTPGenerator.download(h.byteValue(),restart,crc,ofs,name);
	s.op = p;

	if (! jobStarted(p)) {
		s.status = QUEUED;
		fireTableRowsUpdated(0,0);
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 12:43:31 PM)
 * @param e FTPEvent
 */
public void ftpAction(FTPEvent e) {
	if (e.isDownload()) {
		// Get the stats object for this transaction...
		Status s = (Status) lookup.remove(new Byte(e.handle));
		int row = stats.indexOf(s);

		// Update the data model size.
		s.size = Long.toString(e.filelen);
		fireTableCellUpdated(row,SIZE);

		// Construct the download operation, DataDown events will follow...
		File f = new File(s.dest,s.name);
		Operation op = dispatcher.downloadFile(e.id,e.filelen-e.fileofs,e.fileofs,f);
		s.op = op;
		lookup.put(new Integer(e.id),s);

		// If we have the whole file already, the operation is complete...
		if (e.filelen == e.fileofs) {
			op.endOperation(null);
		}
	}
	else if (e.isDownloadFailed()) {
		// Get the stats object for this transaction...
		Status s = (Status) lookup.remove(new Byte(e.handle));
		int row = stats.indexOf(s);

		// Update the data model.
		s.status = FAIL;
		s.detail = e.message;
		fireTableCellUpdated(row,STATUS);
		jobEnded();
	}
	else if (e.isDataDown()) {
		// Receive data for a download in progress...
		Status s = (Status) lookup.get(new Integer(e.id));
		ClientDownloadOperation op = (ClientDownloadOperation) s.op;
		op.frameData(e.fileofs,e.filedata,e.filedataofs,e.filedatalen);
	}
	else if (e.isDownloadAborted()) {
		// Server aborts a download in progress...
		Status s = (Status) lookup.get(new Integer(e.id));
		((Operation) s.op).endOperation(e.message);
	}
	else if (e.isAbortDown()) {
		// ignored
	}
	else if (e.isAbortDownFailed()) {
		// ignored
	}
	else if (e.isUpload()) {
		// Get the stats object for this transaction...
		Status s = (Status) lookup.remove(new Byte(e.handle));
		int row = stats.indexOf(s);

		// Construct the upload operation and begin...
		File f = new File(s.source,s.name);
		SendFileOperation op = dispatcher.uploadFile(e.id,f.length()-e.fileofs,e.fileofs,f);
		s.op = op;
		lookup.put(new Integer(e.id),s);
		Thread t = new Thread(op);
		t.start();
	}
	else if (e.isUploadFailed()) {
		// Get the stats object for this transaction...
		Status s = (Status) lookup.remove(new Byte(e.handle));
		int row = stats.indexOf(s);

		// Update the data model.
		s.status = FAIL;
		s.detail = e.message;
		fireTableCellUpdated(row,STATUS);
		jobEnded();
	}
	else if (e.isDataUpError()) {
		// Server aborts an upload in progress...
		Status s = (Status) lookup.get(new Integer(e.id));
		((Operation) s.op).endOperation(e.message);
	}
	else if (e.isAbortUp()) {
		// ignored
	}
	else if (e.isAbortUpFailed()) {
		// ignored
	}
	else if (e.isOperationComplete()) {
		// Receive data for a download in progress...
		Status s = (Status) lookup.get(new Integer(e.id));
		((Operation) s.op).endOperation(null);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 12:43:31 PM)
 * @param e FTPStatusEvent
 */
public void ftpStatusAction(FTPStatusEvent e) {
	Status s = (Status) lookup.get(new Integer(e.id));
	int row = stats.indexOf(s);

	if (e.isInterim()) {
		if (s.status != CANCEL)
			s.status = e.status;
	}
	else if (e.status != null) {
		if (e.status == CANCEL)
			s.status = (String) e.status;
		else {
			s.detail = (String) e.status;
			s.status = FAIL;
		}

		jobEnded();
	}
	else {
		s.status = COMPLETE;
		jobEnded();
	}

	fireTableCellUpdated(row,STATUS);
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
 * Insert the method's description here.
 * Creation date: (10/28/2002 11:22:45 AM)
 * @return FTPDispatcher
 */
public FTPDispatcher getDispatcher() {
	return dispatcher;
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
			case 2: return stat.source;
			case 3: return stat.dest;
			case 4: return stat.size;
			case 5: return stat.status;
			//case 6: return stat.speed;
			//case 7: return stat.time;
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
			active = stat != null && (stat.equals(INIT) || stat.equals(QUEUED));
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
 * @param row int
 */
public boolean isFailed(int row) {
	boolean failed = false;

	if (row < stats.size()) {
		Status s = (Status) stats.elementAt(row);
		failed = s.status instanceof String && s.status != null && s.status.equals(FAIL);
	}

	return failed;
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2003 9:01:14 AM)
 */
private void jobEnded() {
	synchronized (queue) {
		if (queue.size() > 0) {
			DSMPBaseProto p = (DSMPBaseProto) queue.elementAt(0);
			queue.removeElementAt(0);
			dispatcher.dispatchProtocol(p);
		}
		else {
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
private boolean jobStarted(DSMPBaseProto p) {
	boolean started = true;

	synchronized(queue) {
		if (active < MAXACTIVE) {
			active++;
			dispatcher.dispatchProtocol(p);
		}
		else {
			queue.addElement(p);
			started = false;
		}
	}

	return started;
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 12:14:09 PM)
 */
public void remove(int[] sel) {
	for (int i = sel.length - 1; i >= 0; i--) {
		int j = sel[i];
		Status s = (Status) stats.elementAt(j);
		if (s != null && (s.status == FAIL || s.status == CANCEL || s.status == COMPLETE)) {
			stats.removeElementAt(j);
			fireTableRowsDeleted(j,j);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2002 12:14:09 PM)
 */
public void removeAllCompleted() {
	for (int i = stats.size() - 1; i >= 0; i--) {
		Status s = (Status) stats.elementAt(i);
		if (s.status == FAIL || s.status == CANCEL || s.status == COMPLETE) {
			stats.removeElementAt(i);
			fireTableRowsDeleted(i,i);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 11:22:45 AM)
 * @param newDispatcher FTPDispatcher
 */
public void setDispatcher(FTPDispatcher newDispatcher) {
	if (dispatcher != null) {
		dispatcher.removeFTPListener(this);
		dispatcher.removeFTPStatusListener(this);
	}

	dispatcher = newDispatcher;

	if (dispatcher != null) {
		dispatcher.addFTPListener(this);
		dispatcher.addFTPStatusListener(this);
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
public String uploadFile(String name, String source, String destination, long crclen) {
	// See if the file name at destination is already the target of an active operation.
	for (int i = 0; i < stats.size(); i++) {
		Status s = (Status) stats.elementAt(i);
		if (s.upload == Boolean.TRUE && s.name.equals(name) && s.dest.equals(destination) && isActive(i)) {
			return "upload already in progress.";
		}
	}

	// Get the file's length
	File f = new File(source,name);
	long len = f.length();

	// Add an entry to the status table.
	Status s = new Status(Boolean.TRUE,name,source,destination,Long.toString(len));
	stats.insertElementAt(s,0);
	fireTableRowsInserted(0,0);

	// Get a unused handle for this file.
	Byte h = null;

	synchronized (this) {
		h = new Byte(handle);
		while (lookup.get(h) != null) {
			handle++;
			h = new Byte(handle);
		}
		lookup.put(h,s);
	}

	// Try to generate the file's crc so we can restart, if the file
	// doesn't exist, we will start the transfer from the beginning.
	boolean restart = false;
	int crc = 0;
	long ofs = 0;

	try {
		crc = dispatcher.calculateCRC(f,crclen);
		ofs = crclen;
		restart = true;
	}
	catch (IOException e) {
	}

	// Send upload request (restarts if possible).
	DSMPBaseProto p = FTPGenerator.upload(h.byteValue(),restart,crc,ofs,len,name);
	s.op = p;

	if (! jobStarted(p)) {
		s.status = QUEUED;
		fireTableRowsUpdated(0,0);
	}

	return null;
}
}
