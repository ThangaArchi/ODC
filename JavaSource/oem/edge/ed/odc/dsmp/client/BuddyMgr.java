package oem.edge.ed.odc.dsmp.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultListModel;
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
 * Creation date: (2/26/2003 12:03:20 PM)
 * @author: Mike Zarnick
 */
public class BuddyMgr {
	private static String BUDDYLIST = "buddy.list";
	Vector buddyList = new Vector();
	Hashtable groupList = new Hashtable();
	long timeLoaded = 0;
/**
 * BuddyMgr constructor comment.
 */
public BuddyMgr() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 12:26:33 PM)
 */
private void acquireFileLock() {}
/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 3:41:02 PM)
 * @param name java.lang.String
 * @param users java.util.Vector
 */
public void addGroup(String name, Vector users) {
	// Lock the file for the duration.
	acquireFileLock();

	// Reload the buddy list from disk to get the latest one.
	load();

	// Now, add this group.
	groupList.put(name,users);

	// Now, add the group's users to the buddylist.
	for (int i = 0; i < users.size(); i++) {
		String user = (String) users.elementAt(i);
		if (! buddyList.contains(user)) {
			buddyList.addElement(user);
		}
	}

	// Now, save the buddy list.
	save();

	// Unlock the file.
	releaseFileLock();
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 12:23:34 PM)
 * @param name java.lang.String
 * @param list java.util.Vector
 */
private void addNameToList(String name, Vector list) {
	int i = 0;
	Enumeration e = list.elements();

	// Step through the list to find the place to add the name.
	while (e.hasMoreElements()) {
		String bname = (String) e.nextElement();
		int j = bname.compareTo(name);

		// Current name bigger than new name, insert here.
		if (j > 0) {
			list.insertElementAt(name,i);
			return;
		}
		// New name already in list, ignore it.
		else if (j == 0)
			return;
		// Maybe the next spot.
		else
			i++;
	}
	// New name goes at end of list.
	list.addElement(name);
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 10:21:48 AM)
 * @param name java.lang.String
 * @param lm javax.swing.ListModel
 */
public static void addNameToList(String name, DefaultListModel lm) {
	int i = 0;
	Enumeration e = lm.elements();

	// Step through the list to find the place to add the name.
	while (e.hasMoreElements()) {
		String bname = (String) e.nextElement();
		int j = bname.compareTo(name);

		// Current name bigger than new name, insert here.
		if (j > 0) {
			lm.insertElementAt(name,i);
			return;
		}
		// New name already in list, ignore it.
		else if (j == 0)
			return;
		// Maybe the next spot.
		else
			i++;
	}
	// New name goes at end of list.
	lm.addElement(name);
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 12:06:44 PM)
 * @return java.util.Vector
 */
public Vector getBuddyList() {
	// Load the list and return it.
	load();
	return (Vector) buddyList.clone();
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 3:46:04 PM)
 * @return java.util.Vector
 * @param name java.lang.String
 */
public Vector getGroup(String name) {
	Vector users = (Vector) groupList.get(name);

	if (users == null) {
		users = new Vector();
	}

	return users;
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 3:39:59 PM)
 * @return java.util.Vector
 */
public Vector getGroupList() {
	// Load the list.
	load();

	// Get the group names and return them.
	Enumeration keys = groupList.keys();
	Vector groups = new Vector();

	while (keys.hasMoreElements()) {
		addNameToList((String) keys.nextElement(),groups);
	}

	return groups;
}
/**
 * Checks an enumeration of Buddy objects to see if they refer to 2 or more
 * companies separate from the source company.
 * @param myCompany - source company
 * @param e - enumeration of Buddy objects
 * @return true if Buddy objects refer to 2 or more other companies
 */
static public boolean isCrossCompanyBuddyList(String myCompany, Enumeration e) {
	// No different company found.
	String theirCompany = null;

	// For each buddy...
	while (e.hasMoreElements()) {
		Buddy b = (Buddy) e.nextElement();
		
		// If buddy has a company list...
		if (b.companyList != null) {
			// Tokenize it on commas as group/project company lists are separated this way.
			StringTokenizer st = new StringTokenizer(b.companyList,",");
			while (st.hasMoreTokens()) {
				String company = st.nextToken();
				// Not my company?
				if (! myCompany.equals(company)) {
					// Already encountered another company?
					if (theirCompany != null) {
						// This company is not their company?
						if (! theirCompany.equals(company)) {
							return true;
						}
					}
					// First time we encounterd another company.
					else {
						theirCompany = company;
					}
				}
			}
		}
	}

	return false;
}

/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 2:30:28 PM)
 */
private void load() {
	// Determine if list needs to be loaded.
	File bl = new File(BUDDYLIST);
	if (bl.exists() && bl.lastModified() > timeLoaded) {
		// Lock the file.
		acquireFileLock();

		// Load the buddy list.
		try {
			// Start a new buddy and group list
			Vector nbl = new Vector();
			Hashtable ngl = new Hashtable();

			// Not currently processing a group.
			Vector group = null;
			String groupName = null;

			// Load the list.
			BufferedReader r = new BufferedReader(new FileReader(bl));
			String name = r.readLine();
			while (name != null) {
				int len = name.length();

				// Is this line a group indicator?
				if (len > 1 && name.charAt(0) == '<' && name.charAt(len-1) == '>') {
					// End of group indicator?
					if (name.charAt(1) == '/') {
						// We have a current group and '/' is not the only character between < and >?
						if (group != null && len > 3) {
							// Get the group name.
							String gname = name.substring(2,len-1);

							// Ended current group?
							if (gname.equals(groupName)) {
								ngl.put(groupName,group);
								groupName = null;
								group = null;
							}

							// No, treat this as a member of the group.
							else {
								addNameToList(name,group);
							}
						}

						// Trying to end current group with </>?
						else if (group != null) {
							addNameToList(name,group);
						}

						else {
							addNameToList(name,nbl);
						}
					}

					// Starting a new group. Was working on a group, and never ended it?
					else if (group != null) {
						// Treat this entry as a name in the previous group.
						addNameToList(name,group);
					}

					// Starting a new group.
					else {
						groupName = name.substring(1,len-1);
						group = new Vector();
					}
				}

				// No, just a normal name. Is it part of a group?
				else if (group != null) {
					addNameToList(name,group);
				}

				// No, just a saved name.
				else {
					addNameToList(name,nbl);
				}

				// Next name.
				name = r.readLine();
			}
			r.close();

			// Unfinished group? Add all to buddy list.
			if (group != null) {
				addNameToList(groupName,nbl);
				for (int i = 0; i < group.size(); i++) {
					addNameToList((String) group.elementAt(i),nbl);
				}
			}

			// Replace the lists.
			buddyList = nbl;
			groupList = ngl;
			timeLoaded = System.currentTimeMillis();
		}
		catch (Exception e) {
			System.out.println("Error loading buddy list: " + bl.getAbsolutePath());
			e.printStackTrace();
		}

		// Unlock the file.
		releaseFileLock();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 12:26:49 PM)
 */
private void releaseFileLock() {}
/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 3:45:12 PM)
 * @param name java.lang.String
 */
public void removeGroup(String name) {
	// Lock the file for the duration.
	acquireFileLock();

	// Reload the buddy list from disk to get the latest one.
	load();

	// Now, remove this group.
	groupList.remove(name);

	// Now, save the buddy list.
	save();

	// Unlock the file.
	releaseFileLock();
}
/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 2:30:28 PM)
 */
private void save() {
	// Lock the file.
	acquireFileLock();

	// Now, write out the buddy list.
	File bl = new File(BUDDYLIST);
	try {
		PrintWriter f = new PrintWriter(new FileWriter(bl));

		// Write out the saved buddies list.
		Enumeration e = buddyList.elements();
		while (e.hasMoreElements()) {
			f.println((String) e.nextElement());
		}

		// Now, write out the groups.
		e = groupList.keys();
		while (e.hasMoreElements()) {
			String groupName = (String) e.nextElement();
			f.println("<" + groupName + ">");

			Vector users = (Vector) groupList.get(groupName);
			for (int i = 0; i < users.size(); i++) {
				f.println((String) users.elementAt(i));
			}

			f.println("</" + groupName + ">");
		}

		// Close the buddy list file.
		f.close();
	}
	catch (Exception ex) {
		System.out.println("Error saving buddy list: " + bl.getAbsolutePath());
		ex.printStackTrace();
	}

	// Unlock the file.
	releaseFileLock();
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 12:09:35 PM)
 * @param newlist java.util.Vector
 */
public Vector setBuddyList(Vector newlist) {
	// Lock the file for the duration.
	acquireFileLock();

	// Determine deletes and adds.
	Vector dels = new Vector();

	// Run through all the names we handed out.
	Enumeration e = buddyList.elements();
	while (e.hasMoreElements()) {
		String name = (String) e.nextElement();
		// Name is still in new list?
		if (newlist.contains(name)) {
			// No change, not an add or del.
			newlist.removeElement(name);
		}
		// Name is not in new list, a del.
		else {
			dels.addElement(name);
		}
	}

	// Ok, dels contains the deletions, and newlist
	// contains the additions. Reload the buddy list
	// from disk to get the latest one.
	load();

	// Now, remove the deletions.
	e = dels.elements();
	while (e.hasMoreElements()) {
		buddyList.removeElement(e.nextElement());
	}

	// Now, add the additions.
	e = newlist.elements();
	while (e.hasMoreElements()) {
		buddyList.addElement(e.nextElement());
	}

	// Now, save the buddy list.
	save();

	// Unlock the file.
	releaseFileLock();
	
	return getBuddyList();
}
}
