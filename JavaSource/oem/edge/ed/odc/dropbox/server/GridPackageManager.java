package oem.edge.ed.odc.dropbox.server;

import java.io.*;
import java.util.*;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

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
 * Creation date: (4/28/2003 9:16:08 AM)
 * @author: Mike Zarnick
 */
public class GridPackageManager extends PackageManager {
	private String gridDirectory = null;
	private Vector gridPackageMap = new Vector();
	private long gridPackageMapLoaded = 0;
	private Vector gridPackages = new Vector();
	private User owner = null;
/**
 * GridPackageManager constructor comment.
 * @param fa oem.edge.ed.odc.dropbox.server.DboxFileAllocator
 */
public GridPackageManager(DboxFileAllocator fa, String gridDirectory) {
	super(fa);

	this.fileMgr = new GridFileManager(this,fa);
	this.gridDirectory = gridDirectory;
	gridPackages.addElement(null);
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 1:54:50 PM)
 * @param user oem.edge.ed.odc.dropbox.server.User
 * @param pkgID long
 * @param itemID long
 * @exception oem.edge.ed.odc.dropbox.server.DboxException The exception description.
 */
public void addItemToPackage(User user, long pkgID, long itemID) throws DboxException {
	throw new DboxException("GPM.addItemToPackage: can not copy files from one package to another.",0);
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 1:57:40 PM)
 * @param user oem.edge.ed.odc.dropbox.server.User
 * @param pkgID long
 * @param isProj boolean
 * @param user4 java.lang.String
 * @exception oem.edge.ed.odc.dropbox.server.DboxException The exception description.
 */
public void addPackageAcl(User user, long pkgID, boolean isProj, String name) throws DboxException {
	throw new DboxException("GPM.addPackageAcl: package access can not be changed.",0);
}
/**
 * changePackageExpiration method comment.
 */
public void changePackageExpiration(User owner, long packid, long expire) throws DboxException {
	throw new DboxException("GPM.changePackageExpiration: packages do not expire",0);
}
/**
 * changePackageExpiration method comment.
 */
public void setPackageDescription(User owner, long packid, String desc) throws DboxException {
	throw new DboxException("GPM.setPackageDescription: no desc stored",0);
}
/**
 * cleanExpiredPackages method comment.
 */
public int cleanExpiredPackages(int tot) throws DboxException {
	return 0;
}
/**
 * cleanPackage method comment.
 */
public void cleanPackage(long packid) throws DboxException {
	synchronized (gridPackages) {
		// Make sure id is valid.
		if (packid < 0 || packid >= gridPackages.size()) {
			throw new DboxException("GPM.delete: package id " + packid + " is not valid!",0);
		}

		int id = (int) packid;

		// Get the package.
		GridPackageInfo pInfo = (GridPackageInfo) gridPackages.elementAt(id);

		if (pInfo == null) {
			throw new DboxException("GPM.delete: package id " + packid + " is not found!",0);
		}

		// Remove any files.
		File pkgPath = new File(gridDirectory + File.separator + pInfo.getPackageOwner() + File.separator + pInfo.getPackageName());

		if (pkgPath.exists() && pkgPath.isDirectory()) {
			String[] entries = pkgPath.list();
			for (int i = 0; i < entries.length; i++) {
				File f = new File(pkgPath,entries[i]);
				if (! f.isDirectory()) {
					f.delete();
				}
			}
		}
		else {
			throw new DboxException("GPM.delete: directory " + pInfo.getPackageName() + " is not found!",0);
		}

		// See if directory is now empty. Should contain only . and .. directories.
		String[] entries = pkgPath.list();
		boolean empty = entries.length <= 2;

		for (int i = 0; i < entries.length && empty; i++) {
			if (! entries[i].equals(".") && ! entries[i].equals("..")) {
				empty = false;
			}
		}

		// If empty, delete directory and remove it from our list.
		if (empty) {
			if (pkgPath.delete()) {
				gridPackages.setElementAt(null,id);
			}
			else {
				throw new DboxException("GPM.delete: could not remove package directory!",0);
			}
		}
		else {
			throw new DboxException("GPM.delete: package path part of other packages, can not remove!",0);
		}
	}
}
/**
 * commitPackage method comment.
 */
public void commitPackage(User owner, long id) throws DboxException {
	throw new DboxException("GridPackageManager: commit of packages not permitted",0);
}
/**
 * createPackage method comment.
 */
public DboxPackageInfo createPackage(User owner, String packname, 
                                     String desc, long poolid, long expires, 
                                     java.util.Vector acls) throws DboxException {
	GridPackageInfo pInfo = null;

	synchronized (gridPackages) {
		// Need a valid package name.
		if (packname == null) {
			throw new DboxException("GPM.create: package name is null!",0);
		}

		// Verify package doesn't exist.
		File pkgPath = new File(gridDirectory + File.separator + owner.getName() + File.separator + packname);
		if (pkgPath.exists()) {
			throw new DboxException("GPM.create: package directory already exists!",0);
		}

		// Verify map allows package to be created.
		boolean foundMapMatch = false;

		synchronized (gridPackageMap) {
			for (int j = 0; j < gridPackageMap.size() && ! foundMapMatch; j++) {
				Object mapEntry = gridPackageMap.elementAt(j);

				if (mapEntry instanceof org.apache.regexp.RE) {
					foundMapMatch = ((org.apache.regexp.RE) mapEntry).match(packname);
				}
				else {
					foundMapMatch = ((String) mapEntry).equals(packname);
				}
			}
		}

		if (! foundMapMatch) {
			throw new DboxException("GPM.create: gridMap denied package creation!",0);
		}

		// Create directory
		if (! pkgPath.mkdirs()) {
			throw new DboxException("GPM.create: directory structure could not be created!",0);
		}

		// Create package.
		System.err.println("Create new package: " + packname);
		pInfo = new GridPackageInfo(this);
		pInfo.setPackageId(gridPackages.size());
		pInfo.setPackagePoolId(poolid);
		pInfo.setPackageName(packname);
		pInfo.setPackageOwner(owner.getName());
		pInfo.setPackageExpiration(pkgPath.lastModified());
                pInfo.setPackageDescription(desc);
		pInfo.setPackageCreation(pkgPath.lastModified());
		gridPackages.addElement(pInfo);
	}

	return pInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 1:50:30 PM)
 * @return java.lang.String
 */
public String getGridDirectory() {
	return gridDirectory;
}
/**
 * getPackagesContainingFile method comment.
 */
public java.util.Vector getPackagesContainingFile(User user, 
                                                  DboxFileInfo info) 
   throws DboxException {

	Vector ret = new Vector();

	synchronized (gridPackages) {
		try {
			refreshAll();
		}
		catch (Exception e) {
		}
		Enumeration e = gridPackages.elements();
		while (e.hasMoreElements()) {
			DboxPackageInfo pkg = (DboxPackageInfo) e. nextElement();
			if (pkg != null && pkg.includesFile(info.getFileId())) {
				if (user == null || pkg.canAccessPackage(user,true)) {
                                   pkg = pkg.cloneit();
                                   updatePackageStatus(pkg, user);
                                   ret.addElement(pkg);
				}
			}
		}
	}

	return ret;
}
/**
 * lookupPackage method comment.
 */
public DboxPackageInfo lookupPackage(long packid) throws DboxException {
	GridPackageInfo pkg = null;

	if (packid > Integer.MAX_VALUE) {
		throw new DboxException("GPM.lookupPackage: " + packid + " is invalid!",0);
	}

	refreshAll();

	synchronized (gridPackages) {
		if (packid > gridPackages.size()) {
			throw new DboxException("GPM.lookupPackage: " + packid + " not found!",0);
		}

		pkg = (GridPackageInfo) gridPackages.elementAt((int) packid);

		if (pkg == null) {
			throw new DboxException("GPM.lookupPackage: " + packid + " not found!",0);
		}
	}

	return pkg;
}
/**
 * packagesMatchingExpr method comment.
 */
public java.util.Vector packagesMatchingExpr(String exp, boolean isReg, boolean filterMarked, boolean filterCompleted) throws DboxException {
	Vector ret = new Vector();

	org.apache.regexp.RE re = null;

	if (isReg && exp != null) {
		try {
			re = new org.apache.regexp.RE(exp);
		}
		catch(org.apache.regexp.RESyntaxException syne) {
			throw new DboxException("GPM.packagesMatchingExpr: Invalid regexp " + exp + " " + syne.getMessage(), 0);
		}
	}

	refreshAll();

	synchronized(gridPackages) {
		Enumeration e = gridPackages.elements();
		while (e.hasMoreElements()) {
			GridPackageInfo pkg = (GridPackageInfo) e.nextElement();
			if (pkg != null &&
				(exp == null ||
				(isReg  && re.match(pkg.getPackageName())) ||
				(!isReg && exp.equals(pkg.getPackageName())))) {
                                 
				pkg = (GridPackageInfo)pkg.cloneit();
				updatePackageStatus(pkg);
				ret.addElement(pkg);
			}
		}
	}

	return ret;
}
/**
 * packagesMatchingExprWithAccess method comment.
 */
public java.util.Vector packagesMatchingExprWithAccess(User user, boolean ownerOrAccessor, String exp, boolean isReg, boolean filterMarked, boolean filterCompleted) throws DboxException {
	Vector ret = new Vector();

	if (! ownerOrAccessor) {
		return ret;
	}

	org.apache.regexp.RE re = null;

	if (isReg && exp != null) {
		try {
			re = new org.apache.regexp.RE(exp);
		}
		catch(org.apache.regexp.RESyntaxException syne) {
			throw new DboxException("GPM.packagesMatchingExpr: Invalid regexp " + exp + " " + syne.getMessage(), 0);
		}
	}

	reloadPackageMap();
	updateOwner(user);
	updatePackageFilesAll();

	synchronized(gridPackages) {
		Enumeration e = gridPackages.elements();
		while (e.hasMoreElements()) {
			GridPackageInfo pkg = (GridPackageInfo) e.nextElement();
			if (pkg != null &&
				(exp == null ||
				(isReg  && re.match(pkg.getPackageName())) ||
				(!isReg && exp.equals(pkg.getPackageName())))) {
				if (pkg.getPackageOwner().equalsIgnoreCase(user.getName())) {
					pkg = (GridPackageInfo)pkg.cloneit();
					updatePackageStatus(pkg, user);  
					ret.addElement(pkg);
				}
			}
		}
	}

	return ret;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 11:03:22 AM)
 */
private void refreshAll() throws DboxException {
	reloadPackageMap();
	updatePackages();
	updatePackageFilesAll();
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 9:05:34 AM)
 */
private void reloadPackageMap() {
	// GridPackageMap holds the defined packages. Its index in the vector is
	// its ID. GridPackages holds the accessible packages. Their positions map 1 to 1
	// to the defined package position in GridPackageMap. New defined packages have
	// slots allocated in GridPackages. A call to queryPackagesWithAccess will create
	// the real package in GridPackages iff the user has access.
	File gridMapFile = new File(gridDirectory,"gridPackageMap");

	if (gridMapFile.exists()) {
		if (gridMapFile.lastModified() > gridPackageMapLoaded) {
			synchronized (gridPackageMap) {
				System.err.println("Reloading gridPackageMap file.");
				gridPackageMapLoaded = gridMapFile.lastModified();

				try {
					gridPackageMap.removeAllElements();

					BufferedReader in = new BufferedReader(new FileReader(gridMapFile));

					String pkg = in.readLine();
					while (pkg != null) {
						org.apache.regexp.RE re = null;

						try {
							re = new org.apache.regexp.RE(pkg);
						}
						catch(org.apache.regexp.RESyntaxException syne) {
							System.err.println("RE Exception for: " + pkg);
							System.err.println(syne.getMessage());
						}

						if (re == null) {
							gridPackageMap.addElement(pkg);
						}
						else {
							gridPackageMap.addElement(re);
						}
						System.err.println("Added entry: " + gridPackageMap.size() + " " + pkg);
						pkg = in.readLine();
					}

					in.close();
				}
				catch (Exception e) {
					e.printStackTrace(System.err);
					System.err.println("GridPackageManager: " + gridMapFile.toString() + " could not be read!");
				}
			}
		}
	}
	else {
		System.err.println("GridPackageManager: " + gridMapFile.toString() + " not found!");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 11:32:01 AM)
 * @param pkg oem.edge.ed.odc.dropbox.server.GridPackageInfo
 */
private void removeFiles(GridPackageInfo pkg) throws DboxException {
	// Get known files from package.
	Vector files = pkg.getFiles();

	// Remove every file from the FileManager.
	for (int i = 0; i < files.size(); i++) {
		DboxFileInfo fInfo = (DboxFileInfo) files.elementAt(i);
		fileMgr.removeFile(fInfo.getFileId());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 10:57:00 AM)
 * @param user oem.edge.ed.odc.dropbox.server.User
 */
private void updateOwner(User user) {
	// If we have an owner already?
	if (owner != null) {
		// If we are switching to no owner, or another owner?
		if (user == null || ! user.getName().equals(owner.getName())) {
			// Switch to no owner for now, and update packages (removes
			// all packages).
			owner = null;
			updatePackages();
		}
	}

	// Have no current owner and have a new owner?
	if (owner == null && user != null) {
		// Switch to new owner and update packages.
		owner = user;
		updatePackages();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 12:20:07 PM)
 * @param pInfo oem.edge.ed.odc.dropbox.server.GridPackageInfo
 */
private void updatePackageFiles(GridPackageInfo pkg) throws DboxException {
	String pkgPath = pkg.getPackageOwner() + File.separator + pkg.getPackageName();
	File pkgDir = new File(gridDirectory,pkgPath);

	// Package directory still exists?
	if (pkgDir.exists() && pkgDir.isDirectory()) {
		System.err.println("Update package files: " + pkg.getPackageName());
		// Get known files from package.
		Vector files = pkg.getFiles();

		// Plan that all files in package are no longer valid.
		Boolean[] valid = new Boolean[files.size()+1];
		for (int i = 0; i < valid.length-1; i++) {
			valid[i] = Boolean.FALSE;
		}

		// Scan all entries in package directory.
		String[] members = pkgDir.list();

		for (int i = 0; i < members.length; i++) {
			File member = new File(pkgDir,members[i]);

			if (member.isFile()) {
				// Find this file in the known files vector.
				boolean found = false;
				for (int j = 0; j < files.size(); j++) {
					GridFileInfo file = (GridFileInfo) files.elementAt(j);
					if (members[i].equals(file.getFileName())) {
						System.err.println("existing file: " + members[i]);
						// Update known file's date and size, and mark it valid.
						file.setFileExpiration(member.lastModified());
						long len = member.length();
						if (len != file.getFileSize()) {
							file.setFileIntendedSize(len);
							file.setFileSize(len);
						}
						valid[j] = Boolean.TRUE;
						found = true;
						break;
					}
				}

				// Didn't find this file in the known files vector?
				if (! found) {
					System.err.println("new file: " + members[i]);
					// Create it and add it.
					long len = member.length();
					GridFileInfo file = (GridFileInfo) fileMgr.createFile(members[i],len, 0);
					file.setFileExpiration(member.lastModified());
					file.setFileCreation(member.lastModified());
					file.setFileSize(len);
					file.setFileStatus(DropboxGenerator.STATUS_COMPLETE);
					pkg.addFile(file);
				}
			}
		}

		for (int i = 0; i < valid.length-1; i++) {
			if (valid[i] == Boolean.FALSE) {
				DboxFileInfo f = (DboxFileInfo) files.elementAt(i);
				if (f != null) {
					System.err.println("old file: " + f.getFileName());
					pkg.removeFile(f.getFileId());
					fileMgr.removeFile(f.getFileId());
				}
			}
		}
	}
	// No more package...
	else {
		throw new DboxException("GridPkgMgr.updatePackageFiles: " + pkgDir.toString() + " not found!",0);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 11:03:22 AM)
 */
private void updatePackageFilesAll() throws DboxException {
	synchronized (gridPackages) {
		for (int i = 0; i < gridPackages.size(); i++) {
			GridPackageInfo pkg = (GridPackageInfo) gridPackages.elementAt(i);
			if (pkg != null) {
				updatePackageFiles(pkg);
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 10:27:57 AM)
 * @exception oem.edge.ed.odc.dropbox.server.DboxException The exception description.
 */
private void updatePackages() {
	System.err.println("Update packages.");
	// Have no owner?
	if (owner == null) {
		// Then we have no packages.
		synchronized (gridPackages) {
			System.err.println("Remove all " + gridPackageMap.size() + " packages.");
			for (int i = 0; i < gridPackages.size(); i++) {
				gridPackages.setElementAt(null,i);
			}
		}
	}

	// Have an owner.
	else {
		synchronized (gridPackages) {
			updatePackages(null,null,null);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 10:27:57 AM)
 * @exception oem.edge.ed.odc.dropbox.server.DboxException The exception description.
 */
private void updatePackages(File baseDir,Boolean[] checked,String subDir) {
	// DO NOT CALL THIS METHOD DIRECTLY! Call updatePackages() instead.
	if (baseDir == null) {
		File userPath = new File(gridDirectory + File.separator + owner.getName());

		Boolean tested[] = new Boolean[gridPackages.size()];
		for (int i = 0; i < gridPackages.size(); i++) {
			tested[i] = Boolean.FALSE;
		}

		if (userPath.exists()) {
			updatePackages(userPath,tested,null);
		}

		if (tested != null) {
			for (int i = 0; i < tested.length; i++) {
				if (tested[i] == Boolean.FALSE) {
					gridPackages.setElementAt(null,i);
				}
			}
		}
	}

	// Recursive call.
	else {
		// Get the directory entries.
		String[] entries;
		if (subDir == null) {
			entries = baseDir.list();
		}
		else {
			File dir = new File(baseDir,subDir);
			entries = dir.list();
		}

		// For each entry...
		for (int i = 0; i < entries.length; i++) {
			// Build the package name...
			String entry;
			if (subDir == null) {
				entry = entries[i];
			}
			else {
				entry = subDir + File.separator + entries[i];
			}

			File dir = new File(baseDir,entry);

			if (dir.isDirectory()) {
				// Prepare the search.
				boolean foundMapMatch = false;
				int foundPackageMatch = -1;

				// Scan map vector for matching pattern.
				synchronized (gridPackageMap) {
					for (int j = 0; j < gridPackageMap.size() && ! foundMapMatch; j++) {
						Object mapEntry = gridPackageMap.elementAt(j);

						if (mapEntry instanceof org.apache.regexp.RE) {
							foundMapMatch = ((org.apache.regexp.RE) mapEntry).match(entry);
						}
						else {
							foundMapMatch = ((String) mapEntry).equals(entry);
						}
					}
				}

				// Scan packages vector for matching package.
				for (int j = 0; j < gridPackages.size() && foundPackageMatch == -1; j++) {
					GridPackageInfo pInfo = (GridPackageInfo) gridPackages.elementAt(j);
					if (pInfo != null && pInfo.getPackageName().equals(entry)) {
						foundPackageMatch = j;
						checked[j] = Boolean.TRUE;
					}
				}

				if (foundMapMatch) {
					if (foundPackageMatch == -1) {
						// Add new package to package vector.
						System.err.println("Add new package: " + entry);
						GridPackageInfo pInfo = new GridPackageInfo(this);
						pInfo.setPackageId(gridPackages.size());
						pInfo.setPackageName(entry);
						pInfo.setPackageOwner(owner.getName());
						pInfo.setPackageExpiration(dir.lastModified());
						pInfo.setPackageCreation(dir.lastModified());
						gridPackages.addElement(pInfo);
					}
				}
				else {
					if (foundPackageMatch != -1) {
						// Remove package from package vector. We set it to null to
						// ensure its index is never used again.
						gridPackages.setElementAt(null,foundPackageMatch);
					}
				}

				updatePackages(baseDir,checked,entry);
			}
		}
	}
}

   public GroupInfo getGroup(String group,
                             boolean returnMembers, 
                             boolean returnAccess) 
                             throws DboxException {
      throw new DboxException("getGroup: Grid does not support groups: " + 
                              group, 0);
   }

   public GroupInfo getGroupWithAccess(User user, String group,
                                       boolean returnMembers, 
                                       boolean returnAccess)
      throws DboxException {
      
      throw new DboxException("getGroup: Grid does not support groups: " + 
                              group, 0);
   }
   public Hashtable getMatchingGroups(User user,
                                      String  group,
                                      boolean regexSearch,
                                      boolean owner,
                                      boolean modify,
                                      boolean member,
                                      boolean visible,
                                      boolean listable,
                                      
                                      boolean returnGI,
                                      boolean returnMembers, 
                                      boolean returnAccess)
      throws DboxException {
   
      throw new DboxException("getMatchingGroups: Grid does not support groups: " + 
                              user.getName(), 0);
   }   
   public void createGroup(User owner, String group) throws DboxException {
      throw new DboxException("createGroup: Grid does not support groups: " + 
                              group, 0);
   }
   public void deleteGroup(User user, String group) throws DboxException {
      throw new DboxException("deleteGroup: Grid does not support groups: " + 
                              group, 0);
   }
   public void modifyGroupAttributes(User owner, 
                                     String group,
                                     byte visibility,
                                     byte listability) 
      throws DboxException {
      
      throw new DboxException("modifyGroupAttr: Grid does not support groups: " + 
                              group, 0);
   }
   public void addGroupAcl(User owner, 
                           String group,
                           String name,
                           boolean memberOrAccess)
      throws DboxException {
      throw new DboxException("addGroupAcl: Grid does not support groups: " + 
                              group, 0);
   }
   public void removeGroupAcl(User owner, 
                              String group,
                              String name,
                              boolean memberOrAccess) 
      throws DboxException {
      throw new DboxException("remGroupAcl: Grid does not support groups: " + 
                              group, 0);
   }
   
  // We support no options 
   public Hashtable getValidOptionNames(String u) {
      Hashtable h = new Hashtable();
      return h;
   }
      
   public void setUserOptions(User user, Hashtable h) throws DboxException {
   
      assertUserOptionNames(user.getName(), h);
   }
   
   public Hashtable getUserOptions(String u, Vector v) throws DboxException {
      assertUserOptionNames(u, v);
      return new Hashtable();
   }
   public String getUserOption(String u, String k) throws DboxException {
      Vector v = new Vector();
      v.addElement(k);
      assertUserOptionNames(u, v);
      return "";
   }
   
   public Hashtable getUserOptions(User user, Vector v) throws DboxException {
      assertUserOptionNames(user.getName(), v);
      return new Hashtable();
   }
   
   public String getUserOption(User user, String k) throws DboxException {
      Vector v = new Vector();
      v.addElement(k);
      assertUserOptionNames(user.getName(), v);
      return "";
   }
   
   public int  setPackageOption(User user, long pkgid, int msk, int vals)
      throws DboxException {
      return 0;
   }      
}
