package oem.edge.ed.odc.ftp.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import oem.edge.ed.odc.dsmp.common.DSMPBaseHandler;
import oem.edge.ed.odc.dsmp.common.DSMPBaseProto;
import oem.edge.ed.odc.dsmp.common.DSMPSocketHandler;
import oem.edge.ed.odc.ftp.common.FTPDispatchBase;
import oem.edge.ed.odc.ftp.common.ReceiveFileOperation;
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
 * Creation date: (10/25/2002 9:24:39 AM)
 * @author: Mike Zarnick
 */
public class FTPDispatcher extends FTPDispatchBase {
	private DSMPSocketHandler handler = null;
	private FTPListener ftpListener = null;
	private FTPStatusListener ftpStatusListener = null;
	public String remoteSeparator = null;
	public String remoteHome = null;

	class UploadOperation extends ClientUploadOperation {
		public UploadOperation(DSMPBaseHandler handler, int id, long totToXfer, long ofs, InputStream iis) {
			super(handler, id, totToXfer, ofs, iis);
		}
		public UploadOperation(DSMPBaseHandler handler, int id, long totToXfer, long ofs, File f) {
			super(handler, id, totToXfer, ofs, f);
		}
		public synchronized boolean endOperation(String reason) {
			boolean ended = super.endOperation(reason);

			if (ended)
				fireFTPStatusEvent(new FTPStatusEvent(FTPStatusEvent.END, (byte) 0, (byte) 0, id, reason));

			return ended;
		}
		public void dataTransferred() {
			fireFTPStatusEvent(new FTPStatusEvent(FTPStatusEvent.INTERIM,(byte)0,(byte)0,id,new Integer(percentDone())));
		}
	}

	class DownloadOperation extends ClientDownloadOperation {
		public DownloadOperation(DSMPBaseHandler handler, int id, long totToXfer, long ofs, OutputStream oos) {
			super(handler, id, totToXfer, ofs, oos);
		}
		public DownloadOperation(DSMPBaseHandler handler, int id, long totToXfer, long ofs, File f) {
			super(handler, id, totToXfer, ofs, f);
		}
		public synchronized boolean endOperation(String reason) {
			boolean ended = super.endOperation(reason);

			if (ended)
				fireFTPStatusEvent(new FTPStatusEvent(FTPStatusEvent.END, (byte) 0, (byte) 0, id, reason));

			return ended;
		}
		public void dataTransferred() {
			fireFTPStatusEvent(new FTPStatusEvent(FTPStatusEvent.INTERIM,(byte)0,(byte)0,id,new Integer(percentDone())));
		}
	}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 9:38:07 AM)
 * @param host java.lang.String
 * @param port int
 */
public FTPDispatcher(String host, int port) throws IOException, java.net.UnknownHostException {
	super();
	handler = new DSMPSocketHandler(host,port,this);
	setDebug(true);
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 9:47:04 AM)
 * @param s java.net.Socket
 */
public FTPDispatcher(Socket s) throws IOException, UnknownHostException {
	super();
	handler = new DSMPSocketHandler(s,this);
	setDebug(false);
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 9:48:41 AM)
 * @param l FTPListener
 */
public void addFTPListener(FTPListener l) {
	if (l == null)
		return;

	ftpListener = FTPEventMulticaster.addFTPListener(ftpListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 9:48:41 AM)
 * @param l FTPStatusListener
 */
public void addFTPStatusListener(FTPStatusListener l) {
	if (l == null)
		return;

	ftpStatusListener = FTPEventMulticaster.addFTPStatusListener(ftpStatusListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 9:56:10 AM)
 * @param p DSMPBaseProto
 */
public void dispatchProtocol(DSMPBaseProto p) {
	handler.sendProtocolPacket(p);
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 2:11:20 PM)
 * @return ReceiveFileOperation
 * @param id int
 * @param len long
 * @param offset long
 * @param file File
 */
public ReceiveFileOperation downloadFile(int id, long totToXfer, long offset, File file) {
	return new DownloadOperation(handler,id,totToXfer,offset,file);
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 2:11:20 PM)
 * @return ReceiveFileOperation
 * @param id int
 * @param len long
 * @param offset long
 * @param file File
 */
public ReceiveFileOperation downloadFile(int id, long totToXfer, long offset, OutputStream os) {
	return new DownloadOperation(handler,id,totToXfer,offset,os);
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireAbortDownloadEvent(DSMPBaseHandler handler, byte flags, byte handle, int id, int errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.DOWNLOAD_ABORTED,flags,handle,id,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param id int
 */
public void fireAbortDownloadReply(DSMPBaseHandler handler, byte flags, byte handle, int id) {
	fireFTPEvent(new FTPEvent(FTPEvent.ABORTDOWN,flags,handle,id));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireAbortDownloadReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.ABORTDOWN_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param id int
 */
public void fireAbortUploadReply(DSMPBaseHandler handler, byte flags, byte handle, int id) {
	fireFTPEvent(new FTPEvent(FTPEvent.ABORTUP,flags,handle,id));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireAbortUploadReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.ABORTUP_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param loginID int
 */
public void fireChangeAreaReply(DSMPBaseHandler handler, byte flags, byte handle) {
	fireFTPEvent(new FTPEvent(FTPEvent.CHANGEAREA,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireChangeAreaReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.CHANGEAREA_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param loginID int
 */
public void fireDeleteFileReply(DSMPBaseHandler handler, byte flags, byte handle) {
	fireFTPEvent(new FTPEvent(FTPEvent.DELETE,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireDeleteFileReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.DELETE_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param id int
 */
public void fireDownloadFrameEvent(DSMPBaseHandler handler, byte flags, byte handle, int id, long ofs, byte[] buf, int bofs, int blen) {
	fireFTPEvent(new FTPEvent(FTPEvent.DATA_DOWN,flags,handle,id,ofs,buf,bofs,blen));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param id int
 */
public void fireDownloadReply(DSMPBaseHandler handler, byte flags, byte handle, int id, long ofs, long sz) {
	fireFTPEvent(new FTPEvent(FTPEvent.DOWNLOAD,flags,handle,id,ofs,sz));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireDownloadReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.DOWNLOAD_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 9:50:46 AM)
 * @param e FTPEvent
 */
public void fireFTPEvent(FTPEvent e) {
	if (ftpListener != null)
		ftpListener.ftpAction(e);
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 9:50:46 AM)
 * @param e FTPStatusEvent
 */
public void fireFTPStatusEvent(FTPStatusEvent e) {
	if (ftpStatusListener != null)
		ftpStatusListener.ftpStatusAction(e);
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param loginID int
 */
public void fireListAreaReply(DSMPBaseHandler handler, byte flags, byte handle, Vector data) {
	fireFTPEvent(new FTPEvent(FTPEvent.LISTAREA,flags,handle,data));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireListAreaReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.LISTAREA_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param loginID int
 */
public void fireLoginReply(DSMPBaseHandler handler, byte flags, byte handle, int loginID, String area, String sep) {
	fireFTPEvent(new FTPEvent(FTPEvent.LOGIN,flags,handle,loginID,area,sep));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireLoginReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.LOGIN_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param loginID int
 */
public void fireLogoutReply(DSMPBaseHandler handler, byte flags, byte handle) {
	fireFTPEvent(new FTPEvent(FTPEvent.LOGOUT,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireLogoutReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.LOGOUT_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param loginID int
 */
public void fireNewFolderReply(DSMPBaseHandler handler, byte flags, byte handle) {
	fireFTPEvent(new FTPEvent(FTPEvent.NEWFOLDER,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireNewFolderReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.NEWFOLDER_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param id int
 */
public void fireOperationCompleteEvent(DSMPBaseHandler handler, byte flags, byte handle, int id, String md5, long len) {
	fireFTPEvent(new FTPEvent(FTPEvent.OPERATION_COMPLETE,flags,handle,id));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 */
public void fireShutdownEvent(DSMPBaseHandler handler) {
	fireFTPEvent(new FTPEvent(FTPEvent.DEATH,(byte) 0,(byte) 0));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireUploadDataError(DSMPBaseHandler handler, byte flags, byte handle, int id, short errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.DATA_UPERROR,flags,handle,id,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param id int
 */
public void fireUploadReply(DSMPBaseHandler handler, byte flags, byte handle, int id, long ofs) {
	fireFTPEvent(new FTPEvent(FTPEvent.UPLOAD,flags,handle,id,ofs));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 2:18:50 PM)
 * @param handler DSMPBaseHandler
 * @param flags byte
 * @param handle byte
 * @param errCode short
 * @param msg String
 */
public void fireUploadReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireFTPEvent(new FTPEvent(FTPEvent.UPLOAD_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 9:48:41 AM)
 * @param l FTPListener
 */
public void removeFTPListener(FTPListener l) {
	if (l == null)
		return;

	ftpListener = FTPEventMulticaster.removeFTPListener(ftpListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2002 9:48:41 AM)
 * @param l FTPStatusListener
 */
public void removeFTPStatusListener(FTPStatusListener l) {
	if (l == null)
		return;

	ftpStatusListener = FTPEventMulticaster.removeFTPStatusListener(ftpStatusListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2003 9:30:55 AM)
 */
public void shutdown() {
	handler.shutdown();
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 2:11:20 PM)
 * @return SendFileOperation
 * @param id int
 * @param len long
 * @param offset long
 * @param file File
 */
public SendFileOperation uploadFile(int id, long totToXfer, long offset, File file) {
	return new UploadOperation(handler,id,totToXfer,offset,file);
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2002 2:11:20 PM)
 * @return SendFileOperation
 * @param id int
 * @param len long
 * @param offset long
 * @param file File
 */
public SendFileOperation uploadFile(int id, long totToXfer, long offset, InputStream is) {
	return new UploadOperation(handler,id,totToXfer,offset,is);
}
}
