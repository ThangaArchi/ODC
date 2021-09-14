package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.util.*;

import java.io.*;
import java.util.*;

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

/**
 * Insert the type's description here.
 * Creation date: (4/28/2003 9:17:46 AM)
 * @author: Mike Zarnick
 */
public class GridFileInfo extends DboxFileInfo {
	private GridPackageInfo packageInfo = null;
	private Vector components = new Vector();
/**
 * GridFileInfo constructor comment.
 * @param i oem.edge.ed.odc.dropbox.server.DboxFileInfo
 */
public GridFileInfo(DboxFileInfo i) {
	super(i);
}
/**
 * GridFileInfo constructor comment.
 * @param fm oem.edge.ed.odc.dropbox.server.FileManager
 */
public GridFileInfo(FileManager fm) {
	super(fm);
}
/**
 * GridFileInfo constructor comment.
 * @param fm oem.edge.ed.odc.dropbox.server.FileManager
 * @param n java.lang.String
 * @param sz long
 * @param stat byte
 * @param isz long
 */
public GridFileInfo(FileManager fm, String n, long sz, byte stat, long isz) {
	super(fm, n, sz, stat, isz);
}
/**
 * createComponent method comment.
 */
public DboxFileComponent createComponent() throws DboxException {
	lazyCreateComponent();

	DboxFileComponent c = (DboxFileComponent) components.elementAt(0);

	return c;
}
/**
 * getComponent method comment.
 */
   public DboxFileComponent getComponent(int idx) throws DboxException {
      if (idx != 1) {
         throw new DboxException("GFI.getComponent: invalid component index!",0);
      }
      
      lazyCreateComponent();
      
      return (DboxFileComponent) components.elementAt(0);
   }
   
   public void cullSlots()  throws DboxException {
      throw new DboxException("cullSlots");
   }
   
   public Vector getFileSlots() throws DboxException {
      throw new DboxException("file slots not implemented in GRID yet!");
   }
   
   public void truncate(long filelen)  throws DboxException {
      throw new DboxException("Truncate not yet implemented");
   }
   
   public DboxFileSlot allocateFileSlot(User user)  throws DboxException {
      throw new DboxException("allocateFileSlot not yet implemented");
   }

   public void releaseFileSlot(User user, long slotid)  throws DboxException {
      throw new DboxException("releaseFileSlot not yet implemented");
   }
   
   public void removeFileSlot(User user, long slotid)  throws DboxException {
      throw new DboxException("removeFileSlot not yet implemented");
   }

   public DboxFileSlot getFileSlot(long slotid) throws DboxException {
      throw new DboxException("getFileSlot not yet implemented");
   }
   

/**
 * getComponents method comment.
 */
public Vector getComponents() {
	lazyCreateComponent();

	return components;
}

public DboxFileComponent getComponent(long idx) 
   throws DboxException {
   
   lazyCreateComponent();
   if (components.size() > 0) {
      return (DboxFileComponent)components.elementAt(0);
   } 
   
   return null;
}

/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 11:26:15 AM)
 * @return GridPackageInfo
 */
public GridPackageInfo getPackageInfo() {
	return packageInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 1:36:24 PM)
 */
private void lazyCreateComponent() {
	synchronized (components) {
		if (components.size() == 0) {
			String gridDir = ((GridPackageManager) fileManager.packageManager).getGridDirectory();
			File f = new File(gridDir + File.separator + packageInfo.getPackageOwner() + File.separator + packageInfo.getPackageName() + File.separator + filename);
			GridFileComponent c;
			if (f.exists() && f.isFile()) {
				c = new GridFileComponent(fileManager,fileid,f.length(),intendedSize,0, f.getPath());
			}
			else {
				c = new GridFileComponent(fileManager,fileid,0,intendedSize,0,f.getPath());
			}
			components.addElement(c);
		}
		else {
			GridFileComponent c = (GridFileComponent) components.elementAt(0);
			c.setFileIntendedSize(intendedSize);
		}
	}
}
/**
 * recalculateFileSize method comment.
 */
public void recalculateFileSize() {
	lazyCreateComponent();

	filesize = 0;

	if (components.size() > 0) {
		GridFileComponent c = (GridFileComponent) components.elementAt(0);
		filesize = c.recalculateFileSize();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 11:25:59 AM)
 * @param id GridPackageInfo
 */
public void setPackageInfo(GridPackageInfo info) {
	packageInfo = info;
}

/**
 * Insert the method's description here.
 * Creation date: (5/5/2003 1:42:10 PM)
 * @param s byte
 */
public void setFileStatus(byte s) {
	super.setFileStatus(s);

	if (packageInfo != null) {
		String gridDir = ((GridPackageManager) fileManager.packageManager).getGridDirectory();
		File f = new File(gridDir + File.separator + packageInfo.getPackageOwner() + File.separator + packageInfo.getPackageName() + File.separator + filename);
		if (! f.exists()) {
			try {
				FileOutputStream fo = new FileOutputStream(f);
				fo.close();
			}
			catch (IOException e) {
				System.err.println("GFI.setFileStatus: unable to create empty file: " + f.toString());
			}
		}
	}
}
}
