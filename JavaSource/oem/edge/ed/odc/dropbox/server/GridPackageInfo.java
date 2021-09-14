package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.util.*;

/**
 * Insert the type's description here.
 * Creation date: (4/28/2003 9:18:07 AM)
 * @author: Mike Zarnick
 */
import java.util.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004,2005,2006                           */
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

public class GridPackageInfo extends DboxPackageInfo {
	Hashtable files = new Hashtable();
/**
 * GridPackageInfo constructor comment.
 * @param pm oem.edge.ed.odc.dropbox.server.PackageManager
 */
GridPackageInfo(PackageManager pm) {
	super(pm);
}

GridPackageInfo(GridPackageInfo in) {
   super(in);
   files = (Hashtable)in.files.clone();
}

public DboxPackageInfo cloneit() {
   return new GridPackageInfo(this);
}
   

/**
 * addFile method comment.
 */
synchronized void addFile(DboxFileInfo info) throws DboxException {
	Long l = new Long(info.getFileId());
	if (files.get(l) != null) {
		throw new DboxException("GPI.addFileToPackage: Package " + 
								getPackageId() + " already contains item " +
								l.longValue(), 0);
	}

	files.put(l,info);
	((GridFileInfo) info).setPackageInfo(this);
	packagesize += info.getFileSize();
	numelements++;
}

void removeFileAccessRecord(User user, long fileid) {
}

/**
 * addFileAccessRecord method comment.
 */
void addFileAccessRecord(User user, long fileid, 
                         byte status, int xferate) throws DboxException {
}
/**
 * addProjectAcl method comment.
 */
void addProjectAcl(String name) throws DboxException {
}
void addGroupAcl(String name) throws DboxException {
}
/**
 * addUserAcl method comment.
 */
void addUserAcl(String name) throws DboxException {
}
/**
 * getFile method comment.
 */
synchronized DboxFileInfo getFile(long itemid) throws DboxException {
	Long l = new Long(itemid);
	DboxFileInfo info = (DboxFileInfo) files.get(l);

	if (info == null) {
		throw new DboxException("GPI: getFile: File " + itemid + 
								" does not exist in package " + 
								getPackageId(), 0);
	}

	return info;
}
/**
 * getFile method comment.
 */
synchronized DboxFileInfo getFile(String name) throws DboxException {
	Enumeration e = files.elements();
	while (e.hasMoreElements()) {
		DboxFileInfo info = (DboxFileInfo) e.nextElement();
		if (info.getFileName().equals(name)) {
			return info;
		}
	}

	throw new DboxException("GPI.getFile: File " + name +
							" does not exist in package " +
							getPackageId(),0);
}
/**
 * getFileAcls method comment.
 */
java.util.Vector getFileAcls(long fileid) throws DboxException {
	// We don't support acls...
	return new Vector();
}
/**
 * getFiles method comment.
 */
synchronized Vector getFiles() {
	Vector ret = new Vector();

	Enumeration e = files.elements();
	while (e.hasMoreElements()) {
		ret.addElement(e.nextElement());
	}

	return ret;
}
/**
 * getPackageAcls method comment.
 */
java.util.Vector getPackageAcls(boolean staticOnly) throws DboxException {
	// We don't support acls...
	return new Vector();
}
/**
 * getUserAccess method comment.
 */
public oem.edge.ed.odc.dropbox.common.AclInfo getUserAccess(User user, boolean includeOwner) throws DboxException {
	// No acl support here... What to return?
	return null;
}
/**
 * recalculatePackageSize method comment.
 */
synchronized public void recalculatePackageSize() throws DboxException {
	Enumeration e = files.elements();
	packagesize = 0;
	while (e.hasMoreElements()) {
		DboxFileInfo info = (DboxFileInfo) e.nextElement();
		packagesize += info.getFileSize();
	}
}
/**
 * removeFile method comment.
 */
synchronized void removeFile(long itemid) throws DboxException {
	Long l = new Long(itemid);
	DboxFileInfo info = (DboxFileInfo) files.get(l);

	if (info == null) {
		throw new DboxException("GPI.removeFileFromPackage: Package " + 
								getPackageId() + " does not contain item " +
								l.longValue(),0);
	}

	files.remove(l);
	packagesize -= info.getFileSize();
	numelements--;
	info.deleteComponents();
}
/**
 * removeProjectAcl method comment.
 */
void removeProjectAcl(String name) throws DboxException {
}
void removeGroupAcl(String name) throws DboxException {
}
/**
 * removeUserAcl method comment.
 */
void removeUserAcl(String name) throws DboxException {
}
}
