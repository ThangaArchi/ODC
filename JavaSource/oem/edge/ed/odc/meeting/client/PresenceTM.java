package oem.edge.ed.odc.meeting.client;

import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (3/3/2003 11:36:44 AM)
 * @author: Mike Zarnick
 */
public class PresenceTM implements TreeModel {
	private static String PROJECT = "project: ";
	private static String GROUP = "group: ";
	private TeamInvite root = new TeamInvite("Invitees",-1);
	private Hashtable ikul = new Hashtable();
	private Hashtable pkul = new Hashtable();
	protected EventListenerList listenerList = new EventListenerList();
	private UserInvite inControl = null;
/**
 * PresenceTM constructor comment.
 */
public PresenceTM() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2002 1:40:35 PM)
 * @param group java.lang.String
 */
public void addGroup(String g, int inviteID) {
	// Look up this group name.
	GroupInvite group = new GroupInvite(g,inviteID);
	int pIndex = search(root.ul,group);

	// Did not find it?
	if (pIndex < 0) {
		// Add the invitation to the 1st level list and the invitation index.
		root.ul.insertElementAt(group,-pIndex-1);
		ikul.put(new Integer(inviteID),group);

		// Added a new tree element.
		Object path[] = new Object[1];
		int indices[] = new int[1];
		Object children[] = new Object[1];
		path[0] = root;
		indices[0] = -pIndex-1;
		children[0] = group;

		fireTreeNodesInserted(this,path,indices,children);
	}

	// Found the group.
	else {
		// We allow this addition only if the existing group
		// was previously uninvited (owner changed mind).
		group = (GroupInvite) root.ul.elementAt(pIndex);
		if (group.inviteID == -1) {
			// Has a valid invitation ID again.
			group.inviteID = inviteID;

			// Updated a tree element.
			Object path[] = new Object[1];
			int indices[] = new int[1];
			Object children[] = new Object[1];
			path[0] = root;
			indices[0] = pIndex;
			children[0] = group;

			fireTreeNodesChanged(this,path,indices,children);
		}
		else {
			System.out.println("PresenceTM.addGroup: received duplicate group " + g);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2002 1:40:35 PM)
 * @param group java.lang.String
 */
public void addProject(String p, int inviteID) {
	// Look up this project name.
	ProjectInvite project = new ProjectInvite(p,inviteID);
	int pIndex = search(root.ul,project);

	// Did not find it?
	if (pIndex < 0) {
		// Add the invitation to the 1st level list and the invitation index.
		root.ul.insertElementAt(project,-pIndex-1);
		ikul.put(new Integer(inviteID),project);

		// Added a new tree element.
		Object path[] = new Object[1];
		int indices[] = new int[1];
		Object children[] = new Object[1];
		path[0] = root;
		indices[0] = -pIndex-1;
		children[0] = project;

		fireTreeNodesInserted(this,path,indices,children);
	}

	// Found the project.
	else {
		// We allow this addition only if the existing project
		// was previously uninvited (owner changed mind).
		project = (ProjectInvite) root.ul.elementAt(pIndex);
		if (project.inviteID == -1) {
			// Has a valid invitation ID again.
			project.inviteID = inviteID;

			// Updated a tree element.
			Object path[] = new Object[1];
			int indices[] = new int[1];
			Object children[] = new Object[1];
			path[0] = root;
			indices[0] = pIndex;
			children[0] = project;

			fireTreeNodesChanged(this,path,indices,children);
		}
		else {
			System.out.println("PresenceTM.addProject: received duplicate project " + p);
		}
	}
}
/**
 * Adds a listener for the TreeModelEvent posted after the tree changes.
 *
 * @see     #removeTreeModelListener
 * @param   l       the listener to add
 */
public void addTreeModelListener(TreeModelListener l) {
	listenerList.add(TreeModelListener.class, l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2002 1:40:03 PM)
 * @param user java.lang.String
 * @param group java.lang.String
 * @param presence boolean
 */
public void addUser(String name, int inviteID) {
	// Look up this user name.
	UserInvite user = new UserInvite(name,inviteID,-1,false);
	int uIndex = search(root.ul,user);

	// Did not find it?
	if (uIndex < 0) {
		// Add the invitation to the 1st level list and the invitation index.
		root.ul.insertElementAt(user,-uIndex-1);
		ikul.put(new Integer(inviteID),user);

		// Added an element to the tree.
		Object path[] = new Object[1];
		int indices[] = new int[1];
		Object children[] = new Object[1];
		path[0] = root;
		indices[0] = -uIndex-1;
		children[0] = user;

		fireTreeNodesInserted(this,path,indices,children);
	}
	else {
		System.out.println("PresenceTM.addUser: received duplicate user " + name);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2003 10:11:01 AM)
 */
public void clear() {
	// Clear out all data.
	root.ul.removeAllElements();
	ikul.clear();
	pkul.clear();
	inControl = null;

	// Cleared entire tree structure.
	Object path[] = new Object[1];
	path[0] = root;
	fireTreeStructureChanged(this,path,null,null);
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2003 10:54:54 AM)
 * @param partID int
 */
public void controlPassed(int partID) {
	// Someone already in control?
	if (inControl != null) {
		// Control passed to same person who is already in control?
		if (inControl.participantID == partID)
			return;

		// Current user loses control.
		inControl.inControl = false;

		// Find the path to the inControl user.
		Object path[] = null;
		int indices[] = new int[1];
		Object children[] = new Object[1];
		indices[0] = -1;
		children[0] = inControl;
		for (int i = 0; i < root.ul.size() && indices[0] == -1; i++) {
			Invite invite = (Invite) root.ul.elementAt(i);
			if (invite == inControl) {
				indices[0] = i;
				path = new Object[1];
				path[0] = root;
			}
			else if (invite instanceof TeamInvite) {
				TeamInvite team = (TeamInvite) invite;
				indices[0] = team.ul.indexOf(inControl);
				if (indices[0] != -1) {
					path = new Object[2];
					path[0] = root;
					path[1] = team;
				}
			}
		}

		// If we found inControl in tree (should have), we have
		// updated a tree element.
		if (indices[0] != -1) {
			fireTreeNodesChanged(this,path,indices,children);
		}
		else {
			System.out.println("Could not find old invitee in control.");
		}

		// No one in control now.
		inControl = null;
	}

	// Get the user who is now in control.
	inControl = (UserInvite) pkul.get(new Integer(partID));

	// Found the user by participant ID?
	if (inControl != null) {
		// Mark invitation in control.
		inControl.inControl = true;

		// Find the path to the inControl user.
		Object path[] = null;
		int indices[] = new int[1];
		Object children[] = new Object[1];
		indices[0] = -1;
		children[0] = inControl;
		for (int i = 0; i < root.ul.size() && indices[0] == -1; i++) {
			Invite invite = (Invite) root.ul.elementAt(i);
			if (invite == inControl) {
				indices[0] = i;
				path = new Object[1];
				path[0] = root;
			}
			else if (invite instanceof TeamInvite) {
				TeamInvite team = (TeamInvite) invite;
				indices[0] = team.ul.indexOf(inControl);
				if (indices[0] != -1) {
					path = new Object[2];
					path[0] = root;
					path[1] = team;
				}
			}
		}

		// If we found inControl in tree (should have), we have
		// updated a tree element.
		if (indices[0] != -1) {
			fireTreeNodesChanged(this,path,indices,children);
		}
		else {
			System.out.println("Could not find new invitee in control.");
		}
	}
}
/*
 * Notify all listeners that have registered interest for
 * notification on this event type.  The event instance 
 * is lazily created using the parameters passed into 
 * the fire method.
 * @see EventListenerList
 */
protected void fireTreeNodesChanged(Object source, Object[] path, 
									int[] childIndices, 
									Object[] children) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	TreeModelEvent e = null;
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
		if (listeners[i]==TreeModelListener.class) {
			// Lazily create the event:
			if (e == null)
				e = new TreeModelEvent(source, path, 
									   childIndices, children);
			((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
		}          
	}
}
/*
 * Notify all listeners that have registered interest for
 * notification on this event type.  The event instance 
 * is lazily created using the parameters passed into 
 * the fire method.
 * @see EventListenerList
 */
protected void fireTreeNodesInserted(Object source, Object[] path, 
									int[] childIndices, 
									Object[] children) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	TreeModelEvent e = null;
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
		if (listeners[i]==TreeModelListener.class) {
			// Lazily create the event:
			if (e == null)
				e = new TreeModelEvent(source, path, 
									   childIndices, children);
			((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
		}          
	}
}
/*
 * Notify all listeners that have registered interest for
 * notification on this event type.  The event instance 
 * is lazily created using the parameters passed into 
 * the fire method.
 * @see EventListenerList
 */
protected void fireTreeNodesRemoved(Object source, Object[] path, 
									int[] childIndices, 
									Object[] children) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	TreeModelEvent e = null;
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
		if (listeners[i]==TreeModelListener.class) {
			// Lazily create the event:
			if (e == null)
				e = new TreeModelEvent(source, path, 
									   childIndices, children);
			((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
		}          
	}
}
/*
 * Notify all listeners that have registered interest for
 * notification on this event type.  The event instance 
 * is lazily created using the parameters passed into 
 * the fire method.
 * @see EventListenerList
 */
protected void fireTreeStructureChanged(Object source, Object[] path, 
									int[] childIndices, 
									Object[] children) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	TreeModelEvent e = null;
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
		if (listeners[i]==TreeModelListener.class) {
			// Lazily create the event:
			if (e == null)
				e = new TreeModelEvent(source, path, 
									   childIndices, children);
			((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
		}          
	}
}
/**
 * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
 * child array.  <I>parent</I> must be a node previously obtained from
 * this data source. This should not return null if <i>index</i>
 * is a valid index for <i>parent</i> (that is <i>index</i> >= 0 &&
 * <i>index</i> < getChildCount(<i>parent</i>)).
 *
 * @param   parent  a node in the tree, obtained from this data source
 * @return  the child of <I>parent</I> at index <I>index</I>
 */
public Object getChild(Object parent, int index) {
	if (parent instanceof TeamInvite) {
		TeamInvite team = (TeamInvite) parent;
		return team.ul.elementAt(index);
	}

	return null;
}
/**
 * Returns the number of children of <I>parent</I>.  Returns 0 if the node
 * is a leaf or if it has no children.  <I>parent</I> must be a node
 * previously obtained from this data source.
 *
 * @param   parent  a node in the tree, obtained from this data source
 * @return  the number of children of the node <I>parent</I>
 */
public int getChildCount(Object parent) {
	if (parent instanceof TeamInvite) {
		TeamInvite team = (TeamInvite) parent;
		return team.ul.size();
	}

	return 0;
}
/**
 * Returns the index of child in parent.
 */
public int getIndexOfChild(Object parent, Object child) {
	if (parent instanceof TeamInvite) {
		TeamInvite team = (TeamInvite) parent;
		return team.ul.indexOf(child);
	}

	return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 10:32:25 AM)
 * @return java.lang.String
 * @param partID int
 */
public String getNickName(int partID) {
	UserInvite user = (UserInvite) pkul.get(new Integer(partID));

	if (user == null)
		return null;

	return user.name.substring(0,user.nickLength);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 10:32:25 AM)
 * @return java.lang.String
 * @param partID int
 */
public Enumeration getPresentUserKeys() {
	return pkul.keys();
}
/**
 * Returns the root of the tree.  Returns null only if the tree has
 * no nodes.
 *
 * @return  the root of the tree
 */
public Object getRoot() {
	return root;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 10:32:25 AM)
 * @return java.lang.String
 * @param partID int
 */
public String getUserName(int partID) {
	UserInvite user = (UserInvite) pkul.get(new Integer(partID));

	if (user == null)
		return null;

	return user.name;
}
/**
 * Returns true if <I>node</I> is a leaf.  It is possible for this method
 * to return false even if <I>node</I> has no children.  A directory in a
 * filesystem, for example, may contain no files; the node representing
 * the directory is not a leaf, but it also has no children.
 *
 * @param   node    a node in the tree, obtained from this data source
 * @return  true if <I>node</I> is a leaf
 */
public boolean isLeaf(Object node) {
	return (node instanceof UserInvite);
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2003 10:54:54 AM)
 * @param partID int
 */
public void moderatorPassed(int fromPID, int toPID) {
	// Get the user who was the moderator.
	UserInvite moderator = (UserInvite) pkul.get(new Integer(fromPID));

	// Found the user by participant ID?
	if (moderator != null) {
		// Current user loses moderator.
		moderator.isModerator = false;

		// Find the path to the user.
		Object path[] = null;
		int indices[] = new int[1];
		Object children[] = new Object[1];
		indices[0] = -1;
		children[0] = moderator;
		for (int i = 0; i < root.ul.size() && indices[0] == -1; i++) {
			Invite invite = (Invite) root.ul.elementAt(i);
			if (invite == moderator) {
				indices[0] = i;
				path = new Object[1];
				path[0] = root;
			}
			else if (invite instanceof TeamInvite) {
				TeamInvite team = (TeamInvite) invite;
				indices[0] = team.ul.indexOf(moderator);
				if (indices[0] != -1) {
					path = new Object[2];
					path[0] = root;
					path[1] = team;
				}
			}
		}

		// If we found user in tree (should have), we have
		// updated a tree element.
		if (indices[0] != -1) {
			fireTreeNodesChanged(this,path,indices,children);
		}
		else {
			System.out.println("Could not find old moderator in tree.");
		}
	}

	// Get the user who is now in control.
	moderator = (UserInvite) pkul.get(new Integer(toPID));

	// Found the user by participant ID?
	if (moderator != null) {
		// Mark invitation in control.
		moderator.isModerator = true;

		// Find the path to the inControl user.
		Object path[] = null;
		int indices[] = new int[1];
		Object children[] = new Object[1];
		indices[0] = -1;
		children[0] = moderator;
		for (int i = 0; i < root.ul.size() && indices[0] == -1; i++) {
			Invite invite = (Invite) root.ul.elementAt(i);
			if (invite == moderator) {
				indices[0] = i;
				path = new Object[1];
				path[0] = root;
			}
			else if (invite instanceof TeamInvite) {
				TeamInvite team = (TeamInvite) invite;
				indices[0] = team.ul.indexOf(moderator);
				if (indices[0] != -1) {
					path = new Object[2];
					path[0] = root;
					path[1] = team;
				}
			}
		}

		// If we found user in tree (should have), we have
		// updated a tree element.
		if (indices[0] != -1) {
			fireTreeNodesChanged(this,path,indices,children);
		}
		else {
			System.out.println("Could not find new moderator in tree.");
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2002 12:33:37 PM)
 * @return boolean
 */
private boolean nickCollision(UserInvite u) {
	Enumeration ml = root.ul.elements();
	while (ml.hasMoreElements()) {
		Invite i = (Invite) ml.nextElement();

		if (i instanceof UserInvite) {
			UserInvite u1 = (UserInvite) i;
			if (u1 != u && u.nickLength == u1.nickLength && u.name.regionMatches(0,u1.name,0,u.nickLength))
				return true;
		}
		else {
			Enumeration sl = ((TeamInvite) i).ul.elements();
			while (sl.hasMoreElements()) {
				UserInvite u1 = (UserInvite) sl.nextElement();
				if (u1 != u && u.nickLength == u1.nickLength && u.name.regionMatches(0,u1.name,0,u.nickLength))
					return true;
			}
		}
	}

	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2003 10:54:54 AM)
 * @param partID int
 */
public void ownerChange(int partID, boolean addOrRemove) {
	// Get the user whose ownership setting is changing.
	UserInvite owner = (UserInvite) pkul.get(new Integer(partID));

	// Found the user by participant ID?
	if (owner != null) {
		// Update owner status in invitation.
		owner.isOwner = addOrRemove;

		// Find the path to the user.
		Object path[] = null;
		int indices[] = new int[1];
		Object children[] = new Object[1];
		indices[0] = -1;
		children[0] = owner;
		for (int i = 0; i < root.ul.size() && indices[0] == -1; i++) {
			Invite invite = (Invite) root.ul.elementAt(i);
			if (invite == owner) {
				indices[0] = i;
				path = new Object[1];
				path[0] = root;
			}
			else if (invite instanceof TeamInvite) {
				TeamInvite team = (TeamInvite) invite;
				indices[0] = team.ul.indexOf(owner);
				if (indices[0] != -1) {
					path = new Object[2];
					path[0] = root;
					path[1] = team;
				}
			}
		}

		// If we found the user in tree (should have), we have
		// updated a tree element.
		if (indices[0] != -1) {
			fireTreeNodesChanged(this,path,indices,children);
		}
		else {
			System.out.println("Could not find user for owner change.");
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2002 1:40:35 PM)
 * @param group java.lang.String
 */
public void removeInvite(int inviteID) {
	// Locate the invitation.
	Invite invite = (Invite) ikul.remove(new Integer(inviteID));

	// Missing? Huh, that shouldn't happen.
	if (invite == null) {
		System.out.println("PresenceTM.removeInvite: invitation ID " + inviteID + " not found.");
		return;
	}

	if (invite instanceof UserInvite ||
		((TeamInvite) invite).ul.size() == 0) {
		int i = search(root.ul,invite);
		root.ul.removeElementAt(i);

		if (invite instanceof UserInvite) {
			UserInvite user = (UserInvite) invite;
			if (user.present)
				pkul.remove(new Integer(user.participantID));
		}

		// Removed a tree element.
		Object path[] = new Object[1];
		int indices[] = new int[1];
		Object children[] = new Object[1];
		path[0] = root;
		indices[0] = i;
		children[0] = invite;

		fireTreeNodesRemoved(this,path,indices,children);
	}

	// Invitation is a team invitation with invited users present.
	// Just mark the invitation invalid.
	else {
		invite.inviteID = -1;

		// Updated a tree element.
		Object path[] = new Object[1];
		int indices[] = new int[1];
		Object children[] = new Object[1];
		path[0] = root;
		indices[0] = root.ul.indexOf(invite);
		children[0] = invite;

		fireTreeNodesChanged(this,path,indices,children);
	}
}
/**
 * Removes a listener previously added with <B>addTreeModelListener()</B>.
 *
 * @see     #addTreeModelListener
 * @param   l       the listener to remove
 */
public void removeTreeModelListener(TreeModelListener l) {
	listenerList.remove(TreeModelListener.class, l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2002 1:40:03 PM)
 * @param user java.lang.String
 * @param group java.lang.String
 * @param presence boolean
 */
private int search(Vector il, Invite invite) {
	if (il == null)
		return -1;

	int l = 0;
	int h = il.size() - 1;

	while (l <= h) {
		int t = (l + h)/2;
		Invite i = (Invite) il.elementAt(t);

		int c = i.name.compareTo(invite.name);

		// i name is before invite name?
		if (c < 0)
			l = t + 1;
		// i name is after invite name?
		else if (c > 0)
			h = t - 1;
		// names are the same. sort by invitation type.
		else {
			if (i instanceof UserInvite) {
				// Types are the same?
				if (invite instanceof UserInvite)
					return t;
				// Group and Project types are after User type.
				else
					h = t - 1;
			}
			else if (i instanceof GroupInvite) {
				// User type is before Group type.
				if (invite instanceof UserInvite)
					h = t - 1;
				// Both are Group type.
				else if (invite instanceof GroupInvite)
					return t;
				// Project type is after Group type.
				else
					l = t + 1;
			}
			else {
				// Types are the same?
				if (invite instanceof ProjectInvite)
					return t;
				// User and Group types are before Project type.
				else
					l = t + 1;
			}
		}
	}

	return -l - 1;
}
/**
 * Returns true if arrival was added to tree, false if user is already arrived in tree.
 */
public boolean userArrived(int partID, int inviteID, String name) {
	return userArrived(partID,inviteID,name,false,false);
}
/**
 * Returns true if arrival was added to tree, false if user is already arrived in tree.
 */
public boolean userArrived(int partID, int inviteID, String name, boolean isOwner) {
	return userArrived(partID,inviteID,name,isOwner,false);
}
/**
 * Returns true if arrival was added to tree, false if user is already arrived in tree.
 */
public boolean userArrived(int partID, int inviteID, String name, boolean isOwner, boolean isModerator) {
	// Locate the invitation.
	Invite invite = (Invite) ikul.get(new Integer(inviteID));

	// If the invite is a team (group or project), then the participant is a User under
	// the team.
	if (invite instanceof TeamInvite) {
		TeamInvite team = (TeamInvite) invite;
		UserInvite user = new UserInvite(name,inviteID,partID,true);
		int uIndex = search(team.ul,user);

		// Should never find this user in the team list. Should only ever
		// receive 1 event for a user's arrival (in a team) as departing users
		// are deleted.
		if (uIndex < 0) {
			user.isModerator = isModerator;
			user.isOwner = isOwner;
			user.isProjUser = true;
			team.ul.insertElementAt(user,-uIndex-1);
			Integer PartID = new Integer(partID);
			pkul.put(PartID,user);

			user.nickLength = name.length();
			if (user.nickLength > 8) {
				user.nickLength = 8;
				while (nickCollision(user))
					user.nickLength++;
			}

			// Added a tree element.
			Object path[] = new Object[2];
			int indices[] = new int[1];
			Object children[] = new Object[1];
			path[0] = root;
			path[1] = team;
			indices[0] = -uIndex-1;
			children[0] = user;

			fireTreeNodesInserted(this,path,indices,children);
		}
		else {
			user = (UserInvite) team.ul.elementAt(uIndex);
			if (user.present) {
				user.isModerator = isModerator;
				user.isOwner = isOwner;
				System.out.println("PresenceTM.userArrived: received duplicate for team user " + user.participantID);
				return false;
			}
			else {
				System.out.println("PresenceTM.userArrived: team user " + user.participantID + " was not present.");
				user.isModerator= isModerator;
				user.isOwner = isOwner;
				user.present = true;
				user.participantID = partID;
				Integer PartID = new Integer(partID);
				pkul.put(PartID,user);

				user.nickLength = name.length();
				if (user.nickLength > 8) {
					user.nickLength = 8;
					while (nickCollision(user))
						user.nickLength++;
				}

				// Updated a tree element.
				Object path[] = new Object[2];
				int indices[] = new int[1];
				Object children[] = new Object[1];
				path[0] = root;
				path[1] = team;
				indices[0] = uIndex;
				children[0] = user;

				fireTreeNodesChanged(this,path,indices,children);
			}
		}
	}
	// invite references a user block.
	else {
		UserInvite user = (UserInvite) invite;
		if (user.present) {
			user.isOwner= isOwner;
			user.isModerator = isModerator;
			System.out.println("PresenceTM.userArrived: received duplicate for user " + user.participantID);
			return false;
		}
		else {
			user.present = true;
			user.participantID = partID;
			user.isOwner = isOwner;
			user.isModerator = isModerator;

			Integer PartID = new Integer(partID);
			pkul.put(PartID,user);

			user.nickLength = name.length();
			if (user.nickLength > 8) {
				user.nickLength = 8;
				while (nickCollision(user))
					user.nickLength++;
			}

			// Updated a tree element.
			Object path[] = new Object[1];
			int indices[] = new int[1];
			Object children[] = new Object[1];
			path[0] = root;
			indices[0] = root.ul.indexOf(user);
			children[0] = user;

			fireTreeNodesChanged(this,path,indices,children);
		}
	}

	return true;
}
/**
 */
public boolean userDeparted(int partID, int inviteID) {
	// Locate the participant and the invitation.
	Integer PartID = new Integer(partID);
	Integer InviteID = new Integer(inviteID);
	UserInvite user = (UserInvite) pkul.get(PartID);
	Invite invite = (Invite) ikul.get(InviteID);

	// If the invitation is a team, then the participant is a User
	// under the team.
	if (invite != null && invite instanceof TeamInvite) {
		userDepartedTeam(user,PartID,(TeamInvite) invite,InviteID);
	}

	// Found user, lost invite. Team was dropped.
	else if (user != null && invite == null) {
		// Find the team that has this user.
		boolean done = false;
		for (int i = 0; i < root.ul.size() && ! done; i++) {
			invite = (Invite) root.ul.elementAt(i);
			if (invite instanceof TeamInvite && ((TeamInvite) invite).ul.contains(user)) {
				userDepartedTeam(user,PartID,(TeamInvite) invite,InviteID);
				done = true;
			}
		}
		if (! done) {
			System.out.println("PresenceTM.userDeparted: team user is lost.");
			return false;
		}
	}

	// participant and invite reference the same User object.
	else if (user == invite) {
		user.present = false;
		user.isOwner = false;
		user.isModerator = false;
		user.participantID = -1;
		pkul.remove(PartID);

		// Updated a tree element.
		Object path[] = new Object[1];
		int indices[] = new int[1];
		Object children[] = new Object[1];
		path[0] = root;
		indices[0] = root.ul.indexOf(user);
		children[0] = user;

		fireTreeNodesChanged(this,path,indices,children);
	}

	// Yuk! invite and participant IDs reference 2 different user objects.
	else {
		System.out.println("PresenceTM.userDeparted: invite and participant IDs mismatch.");
		return false;
	}

	return true;
}
/**
 */
private void userDepartedTeam(UserInvite user, Integer PartID, TeamInvite team, Integer InviteID) {
	// Locate the user under the team.
	int uIndex = search(team.ul,user);

	// Found the user?
	if (uIndex >= 0) {
		// And it matches the one index by participant ID?
		if (team.ul.elementAt(uIndex) == user) {
			// Remove the user from the team.
			team.ul.removeElementAt(uIndex);
			pkul.remove(PartID);

			// Removed a tree element.
			Object path[] = new Object[2];
			int indices[] = new int[1];
			Object children[] = new Object[1];
			path[0] = root;
			path[1] = team;
			indices[0] = uIndex;
			children[0] = user;

			fireTreeNodesRemoved(this,path,indices,children);

			// Is this the last user in an uninvited team?
			if (team.ul.size() == 0 && team.inviteID == -1) {
				// Remove the dead invitation too.
				int pIndex = root.ul.indexOf(team);
				root.ul.removeElementAt(pIndex);
				ikul.remove(InviteID);

				// Removed a tree element;
				path = new Object[1];
				path[0] = root;
				indices[0] = pIndex;
				children[0] = team;

				fireTreeNodesRemoved(this,path,indices,children);
			}

		}
		else {
			System.out.println("PresenceTM.userDepartedProject: project invite and participant IDs crossed.");
		}
	}
	else {
		System.out.println("PresenceTM.userDepartedProject: project invite and participant IDs mismatch.");
	}
}
/**
  * Messaged when the user has altered the value for the item identified
  * by <I>path</I> to <I>newValue</I>.  If <I>newValue</I> signifies
  * a truly new value the model should post a treeNodesChanged
  * event.
  *
  * @param path path to the node that the user has altered.
  * @param newValue the new value from the TreeCellEditor.
  */
public void valueForPathChanged(TreePath path, Object newValue) {}
}
