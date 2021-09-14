package oem.edge.ed.odc.dropbox.client;

import java.util.*;
import java.net.*;
import java.io.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.dropbox.common.*;

import oem.edge.ed.odc.ftp.client.FTPStatusEvent;
import oem.edge.ed.odc.ftp.client.FTPStatusListener;
import oem.edge.ed.odc.ftp.client.FTPEventMulticaster;
import oem.edge.ed.odc.ftp.client.ClientDownloadOperation;
import oem.edge.ed.odc.ftp.client.ClientUploadOperation;
import oem.edge.ed.odc.ftp.common.ReceiveFileOperation;
import oem.edge.ed.odc.ftp.common.SendFileOperation;
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
 * DBDispatcher manages a connection to the dropbox server. The application instantiates a DBDispatcher,
 * registers a DBListener and possibly an FTPStatusListener. The application then generates the
 * desired protocol using the static methods of DropboxGenerator and passes it to the dispatcher's
 * dispathProtocol() method. The dispatcher sends the protocol to the dropbox server and listens for
 * responses. When responses are received, appropriate events are fired to the registered listeners.
 * <p>
 * The dispatcher uses the applications thread to queue the outbound protocol. This operation is non-
 * blocking. The dispatcher uses 2 separate threads to send and receive protocol. The receive thread is
 * used to fire the events. The application should ensure that long running tasks started by an event
 * are appropriately threaded so as to allow the receive thread to resume operation in a timely manner.
 * <p>
 * The process of sending and receiving data can be complicated. As a result, the downloadFile and uploadFile
 * methods are provided as a convenience. To transfer a file to a package on the server, the application
 * would generate a protocol object using DropboxGenerator.uploadFileToPackage() method and dispatch it.
 * Upon receiving the successful reply (DBEvent.isUploadFile() returns true), the application calls the
 * dispatchers uploadFile method. An internal class, UploadOperation, manages the transfer of data for the
 * application. The application would then receive an operation complete event or a failure if an error
 * was encountered.
 * <p>
 * The download of data is similar. Generate a protocol object using DropboxGenerator.downloadPackageItem()
 * and dispatch it. When the successful reply event (DBEvent.isDownload() returns true) is received, call
 * the dispatchers downloadFile method. An internal class, DownloadOperation, manages the transfer of
 * data. One notable difference, the data is received on the applications event thread as a
 * DBEvent.isDataDown() event. The application then calls the DownloadOperation.frameData method with
 * the data received.
 * <p>
 * The oem.edge.ed.odc.dropbox.client.FileStatusTableModel further encapsulates the data transfer process.
 * It provides convenience methods of uploadFile and downloadFile. These methods handle all protocol 
 * to initiate and manage the full transfer process. When attached to a JTable, the user is able to
 * visualize the various data transfer operation objects.
 *
 * @see DropboxGenerator
 * @see DBListener
 * @see FTPStatusListener
 * @see FileStatusTableModel
 *
 * @author: Mike Zarnick
 */
public class DBDispatcher extends DropboxDispatchBase {
	private DSMPSocketHandler handler = null;
	private DBListener dbListener = null;
	private FTPStatusListener ftpStatusListener = null;

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
 * Constructs a DBDispatcher object using the host and port specified. The dispatcher establishes the
 * connection to the dropbox server.
 * 
 * @param s java.net.Socket
 * @throws IOException Error occurred on socket
 * @throws UnknownHostException Hostname specified is not found
 */
public DBDispatcher(String host, int port) throws IOException, java.net.UnknownHostException {
	super();
	handler = new DSMPSocketHandler(host,port,this);
	setDebug(false);
}
/**
 * Constructs a DBDispatcher object using the socket specified. The socket must be connected to the dropbox
 * server already.
 * 
 * @param s java.net.Socket
 * @throws IOException Error occurred on socket
 */
public DBDispatcher(Socket s) throws IOException, UnknownHostException {
	super();
	handler = new DSMPSocketHandler(s,this);
	setDebug(false);
}
/**
 * Registers the specified DBListener as a recipient of DBEvents generated by this dispatcher.
 *
 * @param l DBListener object implementing the DBListener interface
 *
 * @see DBListener
 */
public void addDBListener(DBListener l) {
	if (l == null)
		return;

	dbListener = DBEventMulticaster.addDBListener(dbListener,l);
}
/**
 * Registers the specified FTPStatusListener as a recipient of FTPStatusEvents generated by this dispatcher.
 *
 * @param l FTPStatusListener object implementing the FTPStatusListener interface
 *
 * @see FTPStatusListener
 */
public void addFTPStatusListener(FTPStatusListener l) {
	if (l == null)
		return;

	ftpStatusListener = FTPEventMulticaster.addFTPStatusListener(ftpStatusListener,l);
}
/**
 * Queues the protocol for delivery to the dropbox server. This method returns immediately unless the queue
 * is full. A separate thread transmits the protocol to the dropbox server.
 *
 * @param p DSMPBaseProto protocol object to be queued for deliver to the dropbox server
 *
 * @see DropboxGenerator
 */
public void dispatchProtocol(DSMPBaseProto p) {
	handler.sendProtocolPacket(p);
}
/**
 * Constructs a RecieveFileOperation object to process the data received in 
 * DBEvent.isDataDown events. The application should call the ReceiveFileOperation.frameData
 * method each time a DataDown event is received for this operation.
 *
 * @param id int unique identifier for this Operation object
 * @param totToXfer long total number of bytes to be transferred to this Operation
 * @param offset long starting offset in local file for transferred data
 * @param file File file to which transferred data is written
 *
 * @return ReceiveFileOperation Operation object to be used for download
 */
public ReceiveFileOperation downloadFile(int id, long totToXfer, long offset, File file) {
	return new DownloadOperation(handler,id,totToXfer,offset,file);
}
/**
 * Constructs a RecieveFileOperation object to process the data received in 
 * DBEvent.isDataDown events. The application should call the ReceiveFileOperation.frameData
 * method each time a DataDown event is received for this operation.
 *
 * @param id int unique identifier for this Operation object
 * @param totToXfer long total number of bytes to be transferred to this Operation
 * @param offset long starting offset in local file for transferred data
 * @param os OutputStream stream to which transferred data is written
 *
 * @return ReceiveFileOperation Operation object to be used for download
 */
public ReceiveFileOperation downloadFile(int id, long totToXfer, long offset, OutputStream os) {
	return new DownloadOperation(handler,id,totToXfer,offset,os);
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an AbortDownloadEvent. This is an event caused by a failure
 * while transferring data for a download started by the DropboxGenerator.downloadPackageItem method.
 */
public void fireAbortDownloadEvent(DSMPBaseHandler handler, byte flags, byte handle, int id, int errCode, String msg) {
	fireDBEvent(new DBEvent(DBEvent.DOWNLOAD_ABORTED,flags,handle,id,msg));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an AbortDownload reply. This is a possible response to protocol
 * generated by the DropboxGenerator.abortDownload method.
 */
public void fireAbortDownloadReply(DSMPBaseHandler handler, byte flags, byte handle, int id) {
	fireDBEvent(new DBEvent(DBEvent.ABORTDOWN,flags,handle,id));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an AbortDownload reply. This is a possible response to protocol
 * generated by the DropboxGenerator.abortDownload method.
 */
public void fireAbortDownloadReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireDBEvent(new DBEvent(DBEvent.ABORTDOWN_FAILED,flags,handle,msg));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an AbortUpload reply. This is a possible response to protocol
 * generated by the DropboxGenerator.abortUpload method.
 */
public void fireAbortUploadReply(DSMPBaseHandler handler, byte flags, byte handle, int id) {
	fireDBEvent(new DBEvent(DBEvent.ABORTUP,flags,handle,id));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an AbortUpload reply. This is a possible response to protocol
 * generated by the DropboxGenerator.abortUpload method.
 */
public void fireAbortUploadReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireDBEvent(new DBEvent(DBEvent.ABORTUP_FAILED,flags,handle,msg));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an AddItemToPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.addItemToPackage method.
 */
public void fireAddItemToPackageReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.ADDITEM,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an AddItemToPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.addItemToPackage method.
 */
public void fireAddItemToPackageReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.ADDITEM_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an AddPackageAcl reply. This is a possible response to protocol
 * generated by the DropboxGenerator.addPackageAcl method.
 */
public void fireAddPackageAclReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.ADDACL,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an AddPackageAcl reply. This is a possible response to protocol
 * generated by the DropboxGenerator.addPackageAcl method.
 */
public void fireAddPackageAclReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.ADDACL_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a ChangePackageExpiration reply. This is a possible response to protocol
 * generated by the DropboxGenerator.changePackageExpiration method.
 */
public void fireChangePackageExpirationReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.CHANGEEXPIRATION,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a ChangePackageExpiration reply. This is a possible response to protocol
 * generated by the DropboxGenerator.changePackageExpiration method.
 */
public void fireChangePackageExpirationReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.CHANGEEXPIRATION_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a CommitPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.commitPackage method.
 */
public void fireCommitPackageReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.COMMIT,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a CommitPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.commitPackage method.
 */
public void fireCommitPackageReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.COMMIT_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a CreateGroup reply. This is a possible response to protocol
 * generated by the DropboxGenerator.createGroup method.
 */
public void fireCreateGroupReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.CREATEGROUP,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a CreateGroup reply. This is a possible response to protocol
 * generated by the DropboxGenerator.createGroup method.
 */
public void fireCreateGroupReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.CREATEGROUP_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a CreatePackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.createPackage method.
 */
public void fireCreatePackageReply(DSMPBaseHandler h, byte flags, byte handle, long id) {
	fireDBEvent(new DBEvent(DBEvent.CREATE,flags,handle,id));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a CreatePackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.createPackage method.
 */
public void fireCreatePackageReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.CREATE_FAILED,flags,handle,errorStr));
}
/**
 * Delivers the specified event to all registered listeners.
 *
 * @param e DBEvent dropbox event to deliver
 */
public void fireDBEvent(DBEvent e) {
	if (dbListener != null)
		dbListener.dbAction(e);
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a DeleteGroup reply. This is a possible response to protocol
 * generated by the DropboxGenerator.deleteGroup method.
 */
public void fireDeleteGroupReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.DELETEGROUP,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a DeleteGroup reply. This is a possible response to protocol
 * generated by the DropboxGenerator.deleteGroup method.
 */
public void fireDeleteGroupReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.DELETEGROUP_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a DeletePackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.deletePackage method.
 */
public void fireDeletePackageReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.DELETE,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a DeletePackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.deletePackage method.
 */
public void fireDeletePackageReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.DELETE_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a DownloadFrameEvent. This is an event caused by a successful
 * response to protocol generated by the DropboxGenerator.downloadPackageItem method.
 */
public void fireDownloadFrameEvent(DSMPBaseHandler handler, byte flags, byte handle, int id, long ofs, byte[] buf, int bofs, int blen) {
	fireDBEvent(new DBEvent(DBEvent.DATA_DOWN,flags,handle,id,ofs,buf,bofs,blen));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an DownloadPackageItem reply. This is a possible response to protocol
 * generated by the DropboxGenerator.downloadPackageItem method.
 */
public void fireDownloadPackageItemReply(DSMPBaseHandler h, byte flags, byte handle, int opid, long ofs, long size) {
	fireDBEvent(new DBEvent(DBEvent.DOWNLOAD,flags,handle,opid,ofs,size));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an DownloadPackageItem reply. This is a possible response to protocol
 * generated by the DropboxGenerator.downloadPackageItem method.
 */
public void fireDownloadPackageItemReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.DOWNLOAD_FAILED,flags,handle,errorStr));
}
/**
 * Delivers the specified event to all registered listeners.
 *
 * @param e FTPStatusEvent FTP status event to deliver
 */
public void fireFTPStatusEvent(FTPStatusEvent e) {
	if (ftpStatusListener != null)
		ftpStatusListener.ftpStatusAction(e);
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a GetProjectList reply. This is a possible response to protocol
 * generated by the DropboxGenerator.getProjectList method.
 */
public void fireGetProjectListReply(DSMPBaseHandler h, byte flags, byte handle, String user, String company, Vector vec) {
	fireDBEvent(new DBEvent(DBEvent.GETPROJECTS,flags,handle,user,company,vec));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a GetProjectList reply. This is a possible response to protocol
 * generated by the DropboxGenerator.getProjectList method.
 */
public void fireGetProjectListReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireDBEvent(new DBEvent(DBEvent.GETPROJECTS_FAILED,flags,handle,msg));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a GetStoragePoolInstance reply. This is a possible response to protocol
 * generated by the DropboxGenerator.getStoragePoolInstance method.
 */
public void fireGetStoragePoolInstanceReply(DSMPBaseHandler h, byte flags, byte handle, PoolInfo pool) {
	fireDBEvent(new DBEvent(DBEvent.QUERYSTORAGEPOOL,flags,handle,pool));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a GetStoragePoolInstance reply. This is a possible response to protocol
 * generated by the DropboxGenerator.getStoragePoolInstance method.
 */
public void fireGetStoragePoolInstanceReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.QUERYSTORAGEPOOL_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a Login reply. This is a possible response to protocol
 * generated by the DropboxGenerator.loginToken or DropboxGenerator.loginUserPW methods.
 */
public void fireLoginReply(DSMPBaseHandler handler, byte flags, byte handle, int loginID, String area, String sep) {
	fireDBEvent(new DBEvent(DBEvent.LOGIN,flags,handle,loginID,area,sep));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a Login reply. This is a possible response to protocol
 * generated by the DropboxGenerator.loginToken or DropboxGenerator.loginUserPW methods.
 */
public void fireLoginReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireDBEvent(new DBEvent(DBEvent.LOGIN_FAILED,flags,handle,msg));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a Logout reply. This is a possible response to protocol
 * generated by the DropboxGenerator.logout method.
 */
public void fireLogoutReply(DSMPBaseHandler handler, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.LOGOUT,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a Logout reply. This is a possible response to protocol
 * generated by the DropboxGenerator.logout method.
 */
public void fireLogoutReplyError(DSMPBaseHandler handler, byte flags, byte handle, short errCode, String msg) {
	fireDBEvent(new DBEvent(DBEvent.LOGOUT_FAILED,flags,handle,msg));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a GetOptions reply. This is a possible response to protocol
 * generated by the DropboxGenerator.getOptions method.
 */
public void fireManageOptionsReply(DSMPBaseHandler h, byte flags, byte handle, boolean didget, boolean fullget, Hashtable hash) {
	fireDBEvent(new DBEvent(DBEvent.MANAGEOPTIONS,flags,handle,hash));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a GetOptions reply. This is a possible response to protocol
 * generated by the DropboxGenerator.getOptions method.
 */
public void fireManageOptionsReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.MANAGEOPTIONS_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a MarkPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.markPackage method.
 */
public void fireMarkPackageReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.MARKPACKAGE,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a MarkPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.markPackage method.
 */
public void fireMarkPackageReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.MARKPACKAGE_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a ModifyGroupAcl reply. This is a possible response to protocol
 * generated by the DropboxGenerator methods addGroupAccessAcl, addGroupMemberAcl, removeGroupAccessAcl
 * and removeGroupMemberAcl.
 */
public void fireModifyGroupAclReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.MODIFYGROUPACL,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a ModifyGroupAcl reply. This is a possible response to protocol
 * generated by the DropboxGenerator methods addGroupAccessAcl, addGroupMemberAcl, removeGroupAccessAcl
 * and removeGroupMemberAcl.
 */
public void fireModifyGroupAclReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.MODIFYGROUPACL_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a ModifyGroupAttribute reply. This is a possible response to protocol
 * generated by the DropboxGenerator.modifyGroupAttribute method.
 */
public void fireModifyGroupAttributeReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.MODIFYGROUPATTR,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a ModifyGroupAttribute reply. This is a possible response to protocol
 * generated by the DropboxGenerator.modifyGroupAttribute method.
 */
public void fireModifyGroupAttributeReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.MODIFYGROUPATTR_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a NegotiateProtocolVersion reply. This is a possible response to protocol
 * generated by the DropboxGenerator.negotiateProtocolVersion method.
 */
public void fireNegotiateProtocolVersionReply(DSMPBaseHandler handler, byte flags, byte handle, int version) {
	fireDBEvent(new DBEvent(DBEvent.NEGOTIATEPROTOCOL,flags,handle,version));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a NegotiateProtocolVersion reply. This is a possible response to protocol
 * generated by the DropboxGenerator.negotiateProtocolVersion method.
 */
public void fireNegotiateProtocolVersionReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.NEGOTIATEPROTOCOL_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an OperationCompleteEvent. This is an event caused by a successful
 * completion of a data upload or download. See DropboxGenerator.downloadPackageItem or
 * DropboxGenerator.uploadFileToPackage methods.
 */
public void fireOperationCompleteEvent(DSMPBaseHandler handler, byte flags, byte handle, int id, String md5, long len) {
	fireDBEvent(new DBEvent(DBEvent.OPERATION_COMPLETE,flags,handle,id,md5));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryFile reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryFile method.
 */
public void fireQueryFileReply(DSMPBaseHandler h, byte flags, byte handle, FileInfo info, Vector vec) {
	fireDBEvent(new DBEvent(DBEvent.QUERYFILE,flags,handle,info,vec));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryFile reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryFile method.
 */
public void fireQueryFileReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.QUERYFILE_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryFiles reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryFiles method.
 */
public void fireQueryFilesReply(DSMPBaseHandler h, byte flags, byte handle, boolean ownerAccessor, Vector vec) {
	fireDBEvent(new DBEvent(DBEvent.QUERYFILES,flags,handle,ownerAccessor,vec));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryFiles reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryFiles method.
 */
public void fireQueryFilesReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.QUERYFILES_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryGroups reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryGroups method.
 */
public void fireQueryGroupsReply(DSMPBaseHandler h, byte flags, byte handle, boolean includesMembers, boolean includesAccess, Vector vec) {
	fireDBEvent(new DBEvent(DBEvent.QUERYGROUPS,flags,handle,includesMembers,includesAccess,vec));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryGroups reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryGroups method.
 */
public void fireQueryGroupsReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.QUERYGROUPS_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryPackageAcls reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryPackageAcls method.
 */
public void fireQueryPackageAclsReply(DSMPBaseHandler h, byte flags, byte handle, Vector vec, boolean staticOnly) {
	fireDBEvent(new DBEvent(DBEvent.QUERYPKGACLS,flags,handle,vec,staticOnly));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryPackageAcls reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryPackageAcls method.
 */
public void fireQueryPackageAclsReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.QUERYPKGACLS_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryPackageContents reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryPackageContents method.
 */
public void fireQueryPackageContentsReply(DSMPBaseHandler h, byte flags, byte handle, long pID, Vector vec) {
	fireDBEvent(new DBEvent(DBEvent.QUERYCONTENTS,flags,handle,pID,vec));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryPackageContents reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryPackageContents method.
 */
public void fireQueryPackageContentsReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.QUERYCONTENTS_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryPackageFileAcls reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryPackageFileAcls method.
 */
public void fireQueryPackageFileAclsReply(DSMPBaseHandler h, byte flags, byte handle, Vector vec) {
	fireDBEvent(new DBEvent(DBEvent.QUERYFILEACLS,flags,handle,vec));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryPackageFileAcls reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryPackageFileAcls method.
 */
public void fireQueryPackageFileAclsReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.QUERYFILEACLS_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryPackage method.
 */
public void fireQueryPackageReply(DSMPBaseHandler h, byte flags, byte handle, PackageInfo info) {
	fireDBEvent(new DBEvent(DBEvent.QUERYPKG,flags,handle,info));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryPackage method.
 */
public void fireQueryPackageReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.QUERYPKG_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryPackages reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryPackages method.
 */
public void fireQueryPackagesReply(DSMPBaseHandler h, byte flags, byte handle, boolean ownerOrAccessor, Vector vec) {
	fireDBEvent(new DBEvent(DBEvent.QUERYPKGS,flags,handle,ownerOrAccessor,vec));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryPackages reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryPackages method.
 */
public void fireQueryPackagesReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.QUERYPKGS_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryStoragePools reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryStoragePoolInfo method.
 */
public void fireQueryStoragePoolInfoReply(DSMPBaseHandler h, byte flags, byte handle, Vector v) {
	fireDBEvent(new DBEvent(DBEvent.QUERYSTORAGEPOOLS,flags,handle,v));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a QueryStoragePools reply. This is a possible response to protocol
 * generated by the DropboxGenerator.queryStoragePoolInfo method.
 */
public void fireQueryStoragePoolInfoReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.QUERYSTORAGEPOOLS_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a RemoveItemFromPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.removeItemFromPackage method.
 */
public void fireRemoveItemFromPackageReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.REMOVEITEM,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a RemoveItemFromPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.removeItemFromPackage method.
 */
public void fireRemoveItemFromPackageReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.REMOVEITEM_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a RemovePackageAcl reply. This is a possible response to protocol
 * generated by the DropboxGenerator.removePackageAcl method.
 */
public void fireRemovePackageAclReply(DSMPBaseHandler h, byte flags, byte handle) {
	fireDBEvent(new DBEvent(DBEvent.REMOVEACL,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a RemovePackageAcl reply. This is a possible response to protocol
 * generated by the DropboxGenerator.removePackageAcl method.
 */
public void fireRemovePackageAclReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.REMOVEACL_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a SetPackageOption reply. This is a possible response to protocol
 * generated by the DropboxGenerator.setPackageOption method.
 */
public void fireSetPackageOptionReply(DSMPBaseHandler h, byte flags, byte handle, int pkgFlags) {
	fireDBEvent(new DBEvent(DBEvent.SETPKGOPTION,flags,handle,pkgFlags));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for a SetPackageOption reply. This is a possible response to protocol
 * generated by the DropboxGenerator.setPackageOption method.
 */
public void fireSetPackageOptionReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.SETPKGOPTION_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 */
public void fireShutdownEvent(DSMPBaseHandler handler) {
	fireDBEvent(new DBEvent(DBEvent.DEATH,(byte) 0,(byte) 0));
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
	fireDBEvent(new DBEvent(DBEvent.DATA_UPERROR,flags,handle,id,msg));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an UploadFileToPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.uploadFileToPackage method.
 */
public void fireUploadFileToPackageReply(DSMPBaseHandler h, byte flags, byte handle, long itemid, boolean isRestarted, int opid, long ofs) {
	fireDBEvent(new DBEvent(DBEvent.UPLOAD,flags,handle,itemid,opid,ofs));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a DBEvent for an UploadFileToPackage reply. This is a possible response to protocol
 * generated by the DropboxGenerator.uploadFileToPackage method.
 */
public void fireUploadFileToPackageReplyError(DSMPBaseHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireDBEvent(new DBEvent(DBEvent.UPLOAD_FAILED,flags,handle,errorStr));
}
/**
 * Removes the specified DBListener as a recipient of DBEvents generated by this dispatcher.
 *
 * @param l DBListener object implementing the DBListener interface
 *
 * @see DBListener
 */
public void removeDBListener(DBListener l) {
	if (l == null)
		return;

	dbListener = DBEventMulticaster.removeDBListener(dbListener,l);
}
/**
 * Removes the specified FTPStatusListener as a recipient of FTPStatusEvents generated by this dispatcher.
 *
 * @param l FTPStatusListener object implementing the FTPStatusListener interface
 *
 * @see FTPStatusListener
 */
public void removeFTPStatusListener(FTPStatusListener l) {
	if (l == null)
		return;

	ftpStatusListener = FTPEventMulticaster.removeFTPStatusListener(ftpStatusListener,l);
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 */
public void uncaughtProtocol(DSMPBaseHandler h, byte opcode) {
	System.out.println("Uncaught protocol for client: opcode = " + opcode);
	System.out.println("Closing connection to server.");
	h.shutdown();
}
/**
 * Constructs a SendFileOperation object which transmits the file data to the server 
 * for a data upload operation.
 *
 * @param id int unique identifier for this Operation object
 * @param totToXfer long total number of bytes to be transferred by this Operation
 * @param offset long starting offset in local file for transferred data
 * @param file File file from which transferred data is read
 *
 * @return SendFileOperation Operation object to be used for upload
 */
public SendFileOperation uploadFile(int id, long totToXfer, long offset, File file) {
	return new UploadOperation(handler,id,totToXfer,offset,file);
}
/**
 * Constructs a SendFileOperation object which transmits the file data to the server 
 * for a data upload operation.
 *
 * @param id int unique identifier for this Operation object
 * @param totToXfer long total number of bytes to be transferred by this Operation
 * @param offset long starting offset in local file for transferred data
 * @param is InputStream stream from which transferred data is read
 *
 * @return SendFileOperation Operation object to be used for upload
 */
public SendFileOperation uploadFile(int id, long totToXfer, long offset, InputStream is) {
	return new UploadOperation(handler,id,totToXfer,offset,is);
}
}
