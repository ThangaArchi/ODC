package oem.edge.ed.odc.dropbox.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import oem.edge.ed.odc.dropbox.common.DropboxGenerator;
import oem.edge.ed.odc.dropbox.common.FileInfo;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dsmp.client.FileStatusEvent;
import oem.edge.ed.odc.dsmp.client.FileStatusListener;
import oem.edge.ed.odc.dsmp.common.DboxException;
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
 * Creation date: (2/27/2003 8:23:05 AM)
 * @author: Mike Zarnick
 */

public class SourceMgr implements FileStatusListener {
	private static String SRCDIR = "dropbox";
	private static String FILEXT = ".dat";
	private DropboxAccess dboxAccess = null;
/**
 * Verify locally managed files handler.
 */
private class VerifyFilesHandler implements Runnable {
	public void run() {
		File s = new File(SRCDIR);

		if (! s.exists())
			return;

		if (! s.isDirectory())
			return;

		String fl[] = s.list();
		if (fl == null || fl.length == 0)
			return;

		// Set the time of the query to now - 1 hour. Any files created
		// within the last hour will be immune to cleanup to allow for
		// any last minute creations. We assume the server will respond
		// positively to a new upload request within 59 minutes.
		long queryTime = System.currentTimeMillis() - 3600000;

		Vector files = null;

		try {
			// Get the owned files on the server.
			files = dboxAccess.queryFiles(true);
		} catch(DboxException e) {
			System.out.println("SourceMgr: Query files failed: " + e.getMessage());
			return;
		} catch(RemoteException re) {
			System.out.println("SourceMgr: Query files failed: " + re.getMessage());
			return;
		}

		// Get our locally managed files into a vector.
		Vector fileList = new Vector(fl.length);
		for (int i = 0; i < fl.length; i++) {
			fileList.addElement(fl[i]);
		}

		// For each file on the server that is not completed, remove
		// that file from out locally managed list.
		Enumeration e = files.elements();
		while (e.hasMoreElements()) {
			FileInfo f = (FileInfo) e.nextElement();
			if (f.getFileStatus() == DropboxGenerator.STATUS_NONE ||
				f.getFileStatus() == DropboxGenerator.STATUS_INCOMPLETE) {
				String name = f.getFileId() + FILEXT;
				fileList.removeElement(name);
			}
		}

		// Remaining locally managed files should not be needed. Remove
		// remove any files that were created before queryTime.
		e = fileList.elements();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			try {
				File f = new File(SRCDIR,name);
				if (f.lastModified() < queryTime) {
					if (! f.delete()) {
						System.out.println("SourceMgr: Could not delete " + name);
					}
				}
			} catch(NullPointerException npe) {
				System.out.println("SourceMgr: Null pointer exception: " + name);
			} catch(SecurityException se) {
				System.out.println("SourceMgr: Security exception: " + name);
			}
		}
	}
}
/**
 * SourceMgr constructor comment.
 */
public SourceMgr() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2003 8:41:47 AM)
 * @param e oem.edge.ed.odc.dropbox.client.FileStatusEvent
 */
public void fileStatusAction(FileStatusEvent e) {
	// We only want FILE_STATUS events.
	if (e.reason != FileStatusEvent.FILE_STATUS)
		return;

	// We only want uploads
	if (! e.isUpload)
		return;

	// If event has a package ID, it's a new file.
	if (e.isFileBegun()) {
		setSource(e.source,e.info.getFileId());
	}

	// If the file is now complete, no need for source anymore.
	else if (e.isFileComplete()) {
		removeSource(e.info.getFileId());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2003 8:29:43 AM)
 * @return java.lang.String
 * @param fileID long
 */
public String getSource(long fileID) {
	String source = null;

	File s = new File(SRCDIR + File.separator + fileID + FILEXT);
	if (s.exists()) {
		try {
			BufferedReader i = new BufferedReader(new FileReader(s));
			source = i.readLine();
			i.close();
		}
		catch (Exception e) {
			System.out.println("SourceMgr.getSourceDir I/O error: " + s.getAbsolutePath());
			e.printStackTrace();
		}
	}

	return source;
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2003 8:31:39 AM)
 * @param fileID long
 */
public void removeSource(long fileID) {
	File s = new File(SRCDIR + File.separator + fileID + FILEXT);
	if (s.exists()) {
		s.delete();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2003 8:26:23 AM)
 * @param newDispatcher oem.edge.ed.odc.dropbox.client.DBDispatcher
 */
public void setDropboxAccess(DropboxAccess newDboxAccess) {
	if (dboxAccess != null) {
		// Nothing to do.
	}

	dboxAccess = newDboxAccess;

	if (dboxAccess != null) {
		// Nothing to do.
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2003 8:30:10 AM)
 * @param dir java.lang.String
 * @param fileID long
 */
public void setSource(String src, long fileID) {
	File s = new File(SRCDIR);
	if (! s.exists() && ! s.mkdir()) {
		System.out.println("SourceMgr.setSourceDir error: could not create directory " + s.getAbsolutePath());
	}

	s = new File(SRCDIR + File.separator + fileID + FILEXT);

	try {
		FileOutputStream o = new FileOutputStream(s);
		o.write(src.getBytes());
		o.close();
	}
	catch (Exception e) {
		System.out.println("SourceMgr.setSourceDir I/O error: " + s.getAbsolutePath());
		e.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2003 9:04:15 AM)
 */
public void verifyAllFiles() {
	VerifyFilesHandler h = new VerifyFilesHandler();
	WorkerThread t = new WorkerThread(h);
	t.start();
}
}
