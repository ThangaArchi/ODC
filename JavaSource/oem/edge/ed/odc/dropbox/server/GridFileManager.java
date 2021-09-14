package oem.edge.ed.odc.dropbox.server;

/**
 * Insert the type's description here.
 * Creation date: (4/28/2003 9:19:21 AM)
 * @author: Mike Zarnick
 */
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2003-2006                                    */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

public class GridFileManager extends LFileManager {
/**
 * GridFileManager constructor comment.
 * @param pm oem.edge.ed.odc.dropbox.server.PackageManager
 * @param fa oem.edge.ed.odc.dropbox.server.DboxFileAllocator
 */
public GridFileManager(PackageManager pm, DboxFileAllocator fa) {
	super(pm, fa);
}
/**
 * cleanUnreferencedFiles method comment.
 */
public int cleanUnreferencedFiles(int tot) throws DboxException {
	return 0;
}
/**
 * createFile method comment.
 */
public DboxFileInfo createFile(String file, long len, long poolid) throws DboxException {
	if (len == -1) {
		throw new DboxException("GFM.createFile: Bad length specified [" +
								file + "] len = " + len, 0);
	}

	DboxFileInfo info = new GridFileInfo(this, file, 0,
											DropboxGenerator.STATUS_NONE, len);

	info.setFileId(IDGenerator.getId());
        info.setPoolId(poolid);

	addFile(info);
	return info;
}
}
