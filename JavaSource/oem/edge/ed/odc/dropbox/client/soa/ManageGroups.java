package oem.edge.ed.odc.dropbox.client.soa;

import java.awt.event.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import oem.edge.ed.odc.dsmp.client.BuddyMgr;
import oem.edge.ed.odc.dsmp.client.ErrorRunner;
import oem.edge.ed.odc.dsmp.common.DboxException;
import oem.edge.ed.odc.dsmp.common.GroupInfo;
import oem.edge.ed.odc.dsmp.common.DSMPBaseProto;
import oem.edge.ed.odc.dropbox.client.ResultTracker;
import oem.edge.ed.odc.dropbox.common.DropboxGenerator;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
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
 * Creation date: (1/5/2004 10:38:57 AM)
 * @author: 
 */
public class ManageGroups extends JDialog {
	private String groupName = null;
	private Vector groupMembers = null;
	private Vector groupEditors = null;
	private byte groupUse = 0;
	private byte groupList = 0;
	private BuddyMgr buddyMgr = new BuddyMgr();
	private DropboxAccess dboxAccess = null;
	private boolean isAdjusting = false;
	private boolean isAdjustingShare = false;
	private int busyCursor = 0;
	private JMenu ivjGroupM = null;  // @jve:visual-info  decl-index=0 visual-constraint="663,173"
	private JPanel ivjJDialogContentPane = null;
	private JMenuBar ivjManageGroupsJMenuBar = null;
	private BorderFactory ivjPkgBF = null;  // @jve:visual-info  decl-index=0 visual-constraint="758,108"
	private JPanel ivjBtnPnl = null;
	private JButton ivjCanBtn = null;
	private JButton ivjDelBtn = null;
	private JButton ivjEditorAddBtn = null;
	private JPanel ivjEditorBtnPnl = null;
	private JList ivjEditorFromLB = null;
	private JLabel ivjEditorFromLbl = null;
	private JScrollPane ivjEditorFromSP = null;
	private JButton ivjEditorNewAddBtn = null;
	private JPanel ivjEditorPnl = null;
	private JButton ivjEditorRemBtn = null;
	private JTextField ivjEditorTF = null;
	private JList ivjEditorToLB = null;
	private JLabel ivjEditorToLbl = null;
	private JScrollPane ivjEditorToSP = null;
	private JLabel ivjGroupLbl = null;
	private JPanel ivjGroupPnl = null;
	private JComboBox ivjNameCB = null;
	private JLabel ivjNameLbl = null;
	private JPanel ivjNamePnl = null;
	private JTextField ivjNameTF = null;
	private JLabel ivjNewEditorLbl = null;
	private JLabel ivjNewUserLbl = null;
	private JButton ivjOkBtn = null;
	private JButton ivjUserAddBtn = null;
	private JPanel ivjUserBtnPnl = null;
	private JList ivjUserFromLB = null;
	private JLabel ivjUserFromLbl = null;
	private JScrollPane ivjUserFromSP = null;
	private JButton ivjUserNewAddBtn = null;
	private JPanel ivjUserPnl = null;
	private JButton ivjUserRemBtn = null;
	private JTextField ivjUserTF = null;
	private JList ivjUserToLB = null;
	private JLabel ivjUserToLbl = null;
	private JScrollPane ivjUserToSP = null;
	private DefaultListModel ivjUserFromLM = null;  // @jve:visual-info  decl-index=0 visual-constraint="694,304"
	private DefaultListModel ivjUserToLM = null;  // @jve:visual-info  decl-index=0 visual-constraint="694,253"
	private DefaultListModel ivjEditorFromLM = null;  // @jve:visual-info  decl-index=0 visual-constraint="687,476"
	private DefaultListModel ivjEditorToLM = null;  // @jve:visual-info  decl-index=0 visual-constraint="686,427"
	private JRadioButton ivjListMemberRB = null;
	private JRadioButton ivjListOwnerRB = null;
	private JRadioButton ivjUseMemberRB = null;
	private JRadioButton ivjUseOwnerRB = null;
	private JPanel ivjJDialogContentPane7 = null;
	private JTextArea ivjMigateTA = null;
	private JButton ivjMigrateCanBtn = null;
	private JDialog ivjMigrateDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="33,663"
	private JList ivjMigrateLB = null;
	private DefaultListModel ivjMigrateLM = null;  // @jve:visual-info  decl-index=0 visual-constraint="469,848"
	private JButton ivjMigrateOkBtn = null;
	private JScrollPane ivjMigrateSP = null;
	private JMenuItem ivjLoadMI = null;
	private JButton ivjMigrateCloseBtn = null;
	private JPanel ivjSharePnl = null;
	private JMenuItem ivjCloseMI = null;
	private JButton ivjCloseBtn = null;
	private JLabel ivjListLbl = null;
	private JLabel ivjUseLbl = null;
/**
 * Generic runnable to turn off the busy cursor. Use busyCursorOff as the Runnable.
 * eg. SwingUtilities.invokeLater(busyCursorOff);
 */
private class BusyCursorOff implements Runnable {
	public void run() {
		busyCursor(false);
	}
}
private BusyCursorOff busyCursorOff = new BusyCursorOff();
/**
 * Delete group listener.
 */
private class DeleteGroupListener implements ActionListener, Runnable {
	public void actionPerformed(ActionEvent e) {
		// Ask if ok to delete group.
		if (JOptionPane.showConfirmDialog(ManageGroups.this,"Ok to delete selected group?","Delete Group",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
			return;
		}

		busyCursor(true);

		WorkerThread t = new WorkerThread(this);
		t.start();
	}
	public void removeGroupFromList() {
		// Must do in event thread, kicks off lots of activity
		// as a new group will be selected afterwards.
		int i = getNameCB().getSelectedIndex();
		getNameCB().removeItemAt(i);
	}
	public void run() {
		try {
			dboxAccess.deleteGroup(groupName);
			String gName = groupName;
			SwingUtilities.invokeLater(new MethodRunner(this,"removeGroupFromList"));
			SwingUtilities.invokeLater(new ErrorRunner(ManageGroups.this,"Group '" + gName + "' deleted.","Deleted Group",false));
		} catch(DboxException e) {
			SwingUtilities.invokeLater(new ErrorRunner(ManageGroups.this,e.getMessage(),"Delete Group Failed"));
		} catch(RemoteException re) {
			SwingUtilities.invokeLater(new ErrorRunner(ManageGroups.this,re.getMessage(),"Delete Group Failed"));
		}
		
		SwingUtilities.invokeLater(busyCursorOff);
	}
}
private DeleteGroupListener deleteGroupListener = new DeleteGroupListener();
/**
 * Edit event handler.
 */
private class EditHandler extends MouseAdapter implements ActionListener, ItemListener, ListSelectionListener, DocumentListener {
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == getEditorAddBtn()) {
				addEditor();		}
			else if (e.getSource() == getEditorRemBtn()) {
				removeEditor();		}
			else if (e.getSource() == getUserAddBtn()) {
				addUser();		}
			else if (e.getSource() == getUserRemBtn()) {
				removeUser();
			}
			else if (e.getSource() == getEditorNewAddBtn() || e.getSource() == getEditorTF()) {
				addNewEditor();
			}
			else if (e.getSource() == getUserNewAddBtn() || e.getSource() == getUserTF()) {
				addNewUser();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}    
	}
	public void changedUpdate(DocumentEvent e) {
		textUpdate(e);
	}
	public void insertUpdate(DocumentEvent e) {
		textUpdate(e);
	}
	public void itemStateChanged(ItemEvent e) {
		if (isAdjustingShare) {
			return;
		}

		// No need to manage listability is visibility is only owner.
		boolean enableList = getUseMemberRB().isSelected();
		getListLbl().setEnabled(enableList);
		getListOwnerRB().setEnabled(enableList);
		getListMemberRB().setEnabled(enableList);

		adjustGUI();
	}
	public void mouseClicked(MouseEvent e) {
		try {
			if (e.getClickCount() == 2 && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
				if (e.getSource() == getUserFromLB())
					addUser();
				else if (e.getSource() == getUserToLB())
					removeUser();
				else if (e.getSource() == getEditorFromLB())
					addEditor();
				else if (e.getSource() == getEditorToLB())
					removeEditor();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}    
	}
	public void removeUpdate(DocumentEvent e) {
		textUpdate(e);
	}
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == getUserToLB()) {
			getUserRemBtn().setEnabled(getUserToLB().getSelectedIndex() != -1);
		}
		else if (e.getSource() == getUserFromLB()) {
			getUserAddBtn().setEnabled(getUserFromLB().getSelectedIndex() != -1);
		}
		else if (e.getSource() == getEditorToLB()) {
			getEditorRemBtn().setEnabled(getEditorToLB().getSelectedIndex() != -1);
		}
		else if (e.getSource() == getEditorFromLB()) {
			getEditorAddBtn().setEnabled(getEditorFromLB().getSelectedIndex() != -1);
		}
	}
	public void addEditor() {
		Object[] objs = getEditorFromLB().getSelectedValues();
		for (int i = 0; i < objs.length; i++) {
			getEditorFromLM().removeElement(objs[i]);
			BuddyMgr.addNameToList((String) objs[i],getEditorToLM());
		}
		getEditorAddBtn().setEnabled(false);
		adjustGUI();
	}
	public void addNewEditor() {
		String editor = getEditorTF().getText().trim();

		BuddyMgr.addNameToList(editor,getEditorToLM());
		getEditorFromLM().removeElement(editor);
		getEditorTF().setText("");
		getEditorNewAddBtn().setEnabled(false);

		// Ensure user LMs have this new person.
		if (! getUserToLM().contains(editor)) {
			BuddyMgr.addNameToList(editor,getUserFromLM());
		}

		adjustGUI();
	}
	public void removeEditor() {
		Object[] objs = getEditorToLB().getSelectedValues();
		for (int i = 0; i < objs.length; i++) {
			getEditorToLM().removeElement(objs[i]);
			BuddyMgr.addNameToList((String) objs[i],getEditorFromLM());
		}
		getEditorRemBtn().setEnabled(false);
		adjustGUI();
	}
	public void addUser() {
		Object[] objs = getUserFromLB().getSelectedValues();
		for (int i = 0; i < objs.length; i++) {
			getUserFromLM().removeElement(objs[i]);
			BuddyMgr.addNameToList((String) objs[i],getUserToLM());
		}
		getUserAddBtn().setEnabled(false);
		adjustGUI();
	}
	public void addNewUser() {
		String user = getUserTF().getText().trim();

		BuddyMgr.addNameToList(user,getUserToLM());
		getUserFromLM().removeElement(user);
		getUserTF().setText("");
		getUserNewAddBtn().setEnabled(false);

		// Ensure editor LMs have this new person.
		if (! getEditorToLM().contains(user)) {
			BuddyMgr.addNameToList(user,getEditorFromLM());
		}

		adjustGUI();
	}
	public void removeUser() {
		Object[] objs = getUserToLB().getSelectedValues();
		for (int i = 0; i < objs.length; i++) {
			getUserToLM().removeElement(objs[i]);
			BuddyMgr.addNameToList((String) objs[i],getUserFromLM());
		}
		getUserRemBtn().setEnabled(false);
		adjustGUI();
	}
	public void textUpdate(DocumentEvent e) {
		if (e.getDocument() == getUserTF().getDocument())
			textChgUser();
		else if (e.getDocument() == getEditorTF().getDocument())
			textChgEditor();
		else if (e.getDocument() == getNameTF().getDocument())
			adjustGUI();
	}
	public void textChgEditor() {
		String editor = getEditorTF().getText();

		getEditorNewAddBtn().setEnabled(editor != null && editor.trim().length() > 0);
	}
	public void textChgUser() {
		String user = getUserTF().getText();

		getUserNewAddBtn().setEnabled(user != null && user.trim().length() > 0);
	}
}
private EditHandler editHandler = new EditHandler();
/**
 * Base class to handle group selections. Query's the selected
 * group and sets up the globals and gui.
 */
private class GroupSelectionHandler implements Runnable {
	public void nameSelected() {
		int i = getNameCB().getSelectedIndex();

		if (isAdjusting) {
			return;
		}

		// New group?
		if (i == 0) {
			// Remember this is a new group.
			groupName = "Enter new group name";
			groupMembers = new Vector();
			groupEditors = new Vector();
			groupUse = DropboxGenerator.GROUP_SCOPE_OWNER;
			groupList = DropboxGenerator.GROUP_SCOPE_OWNER;

			// Set up the GUI.
			reset();

			// Can't delete a new group, can edit name.
			getDelBtn().setEnabled(false);
			getNameTF().setEditable(true);
		}
		// Edit an existing group.
		else {
			// Remember the group's name and initial members.
			groupName = (String) getNameCB().getItemAt(i);

			// Query the group on the server.
			WorkerThread t = new WorkerThread(this);
			t.start();

			busyCursor(true);
		}
	}
	public void missingGroup() {
		int i = getNameCB().getSelectedIndex();
		getNameCB().removeItemAt(i);
	}
	public void reset() {
		// Prepare the common gui parts.
		getUserAddBtn().setEnabled(false);
		getUserRemBtn().setEnabled(false);
		getUserTF().setText("");
		getUserNewAddBtn().setEnabled(false);
		getEditorAddBtn().setEnabled(false);
		getEditorRemBtn().setEnabled(false);
		getEditorTF().setText("");
		getEditorNewAddBtn().setEnabled(false);

		// Prepare the saved users list.
		getUserFromLM().removeAllElements();
		getEditorFromLM().removeAllElements();
		Vector users = buddyMgr.getBuddyList();
		for (int j = 0; j < users.size(); j++) {
			String user = (String) users.elementAt(j);
			getUserFromLM().addElement(user);
			getEditorFromLM().addElement(user);
		}
		getUserFromLB().clearSelection();
		getEditorFromLB().clearSelection();

		// Prepare the member list.
		getUserToLM().removeAllElements();
		for (int j = 0; j < groupMembers.size(); j++) {
			String member = (String) groupMembers.elementAt(j);
			BuddyMgr.addNameToList(member,getUserToLM());
			getUserFromLM().removeElement(member);
		}
		getUserToLB().clearSelection();

		// Prepare the editor list.
		getEditorToLM().removeAllElements();
		for (int j = 0; j < groupEditors.size(); j++) {
			String editor = (String) groupEditors.elementAt(j);
			BuddyMgr.addNameToList(editor,getEditorToLM());
			getEditorFromLM().removeElement(editor);
		}
		getEditorToLB().clearSelection();

		// Prepare the share radio buttons.
		isAdjustingShare = true;
		getUseOwnerRB().setSelected(groupUse == DropboxGenerator.GROUP_SCOPE_OWNER);
		getUseMemberRB().setSelected(groupUse == DropboxGenerator.GROUP_SCOPE_MEMBER);
		getListOwnerRB().setSelected(groupList == DropboxGenerator.GROUP_SCOPE_OWNER);
		getListMemberRB().setSelected(groupList == DropboxGenerator.GROUP_SCOPE_MEMBER);

		// No need to manage listability is visibility is only owner.
		boolean enableList = getUseMemberRB().isSelected();
		getListLbl().setEnabled(enableList);
		getListOwnerRB().setEnabled(enableList);
		getListMemberRB().setEnabled(enableList);
		isAdjustingShare = false;

		// Prepare the rest of the gui.
		if (getNameCB().getSelectedIndex() == 0) {
			getOkBtn().setToolTipText("Save new group");
			getCanBtn().setToolTipText("Discard choices and start over");
			getNameTF().setToolTipText("Enter name of new group");
		}
		else {
			getOkBtn().setToolTipText("Save group changes");
			getCanBtn().setToolTipText("Discard group changes");
			getNameTF().setToolTipText("Name of current group (can not be changed)");
		}

		getNameTF().setText(groupName);

		getNameCB().setEnabled(true);
		getCloseBtn().setEnabled(true);
		getOkBtn().setEnabled(false);
		getCanBtn().setEnabled(false);
	}
	public void run() {
		try {
			try {
				Map groups = dboxAccess.queryGroups(groupName,false,true,true);

				GroupInfo group = (GroupInfo) groups.get(groupName);

				// Queried a specific group, but we no longer have access to it...
				if (group == null) {
					String gName = groupName;
					SwingUtilities.invokeLater(new MethodRunner(this,"missingGroup"));
					SwingUtilities.invokeLater(new ErrorRunner(ManageGroups.this,"Group '" + gName + "' is not editable by you.","Edit Group",false));
				}

				// Got the group details.
				else {
					// Remember the group's name and initial members.
					groupName = group.getGroupName();
					groupMembers = group.getGroupMembersValid() ? group.getGroupMembers() : new Vector();
					groupEditors = group.getGroupAccessValid() ? group.getGroupAccess() : new Vector();
					groupUse = group.getGroupVisibility();
					groupList = group.getGroupListability();

					SwingUtilities.invokeLater(new MethodRunner(this,"showGroup"));
				}
			} catch(DboxException e) {
				SwingUtilities.invokeLater(new ErrorRunner(ManageGroups.this,e.getMessage(),"Query Group Error",false));
			}
			SwingUtilities.invokeLater(busyCursorOff);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void showGroup() {
		// Prepare the group GUI area.
		reset();

		// Can delete existing groups, but can't edit name.
		getDelBtn().setEnabled(true);
		getNameTF().setEditable(false);
	}
}
/**
 * Handler to get all groups.
 */
private class GetGroupsHandler extends GroupSelectionHandler implements Runnable {
	private boolean gettingGroups = true;
	private Map groups = null;
	public void displayResults() {
		// Set up the group name combo box.
		isAdjusting = true;
		getNameCB().setEnabled(true);
		getNameCB().removeAllItems();
		getNameCB().addItem("Create new group...");

		if (groups != null) {
			Iterator names = groups.keySet().iterator();
			while (names.hasNext()) {
				GroupInfo gInfo = (GroupInfo) groups.get(names.next());
				if (gInfo.getGroupListability() != DropboxAccess.GROUP_SCOPE_NONE) {
					getNameCB().addItem(gInfo.getGroupName());
				}
			}
		}

		isAdjusting = false;

		getNameCB().setSelectedIndex(0);
		nameSelected();
	}
	public void run() {
		try {
			// Getting our full group list?
			if (gettingGroups) {
				try {
					groups = dboxAccess.queryGroups(false,false);
				} catch(DboxException e) {
					groups = null;
				}
	
				SwingUtilities.invokeLater(new MethodRunner(this,"displayResults"));
				SwingUtilities.invokeLater(busyCursorOff);
			}
			// No, super class is querying a specific group.
			else {
				super.run();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
}
/**
 * Group selection listener.
 */
private class GroupSelectionListener extends GroupSelectionHandler implements ActionListener, ItemListener {
	private GroupInfo group = null;
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getMigrateOkBtn()) {
			// Get selected groups.
			String group = (String) getMigrateLB().getSelectedValue();
	
			// Load the local group into the group window..
			getNameCB().setSelectedIndex(0);
			nameSelected();
	
			getNameTF().setText(group);
			Vector members = buddyMgr.getGroup(group);
			Enumeration names = members.elements();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				BuddyMgr.addNameToList(name,getUserToLM());
				getUserFromLM().removeElement(name);
			}
	
			// Adjust the GUI so the "new" group is edited.
			adjustGUI();
	
			// Close the local group window.
			getMigrateDlg().dispose();
		}
	}
	public void itemStateChanged(ItemEvent e) {
		try {
			if (e.getStateChange() != ItemEvent.DESELECTED) {
				nameSelected();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
}
private GroupSelectionListener groupSelectionListener = new GroupSelectionListener();
/**
 * Handler to save a new or modified group.
 */
private class GroupActionListener extends GroupSelectionHandler implements ActionListener, Runnable {
	private boolean savingGroup;
	private boolean newGroup;
	private byte use;
	private byte list;
	private String name;
	private Vector members;
	private Vector editors;
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == getOkBtn()) {
				save();
			}
			else if (e.getSource() == getCanBtn()) {
				reset();
			}
			else if (e.getSource() == getCloseBtn() || e.getSource() == getCloseMI()) {
				dispose();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void save() {
		savingGroup = true;

		// Is this a new group?
		newGroup = getNameCB().getSelectedIndex() == 0;

		// Ask if ok to save changes.
		int okToSave = newGroup ? JOptionPane.YES_OPTION : JOptionPane.NO_OPTION;

		if (okToSave == JOptionPane.NO_OPTION)
			okToSave = JOptionPane.showConfirmDialog(ManageGroups.this,"Save changes to group?","Save Changes",JOptionPane.YES_NO_OPTION);

		// Not Ok? Out of here.
		if (okToSave == JOptionPane.NO_OPTION) {
			return;
		}

		busyCursor(true);

		// Get the visibility and listability bytes.
		if (getUseOwnerRB().isSelected())
			use = DropboxGenerator.GROUP_SCOPE_OWNER;
		else
			use = DropboxGenerator.GROUP_SCOPE_MEMBER;

		if (getListOwnerRB().isSelected())
			list = DropboxGenerator.GROUP_SCOPE_OWNER;
		else
			list = DropboxGenerator.GROUP_SCOPE_MEMBER;

		name = getNameTF().getText();

		members = new Vector();
		Enumeration users = getUserToLM().elements();
		while (users.hasMoreElements()) {
			members.addElement(users.nextElement());
		}

		editors = new Vector();
		users = getEditorToLM().elements();
		while (users.hasMoreElements()) {
			editors.addElement(users.nextElement());
		}

		WorkerThread t = new WorkerThread(this);
		t.start();
	}
	public void finishCreateGroup() {
		// Insert group name into combo box.
		boolean done = false;
		int i;
		for (i = 1; (i < getNameCB().getItemCount()) && (! done); i++) {
			String gname = (String) getNameCB().getItemAt(i);
			if (gname.compareTo(name) > 0) {
				done = true;
				i--;
			}
		}

		// Prevent the selection from being processed. Will do it
		// later by calling nameSelected directly.
		isAdjusting = true;
		getNameCB().insertItemAt(name,i);
		getNameCB().setSelectedIndex(i);
		isAdjusting = false;
	}
	public void run() {
		try {
			if (savingGroup) {
				// Save the buddy list.
				saveBuddyList();
	
				// Is this a new group?
				if (newGroup) {
					try {
						// Create the group on the server.
						dboxAccess.createGroup(name,use,list);
					} catch(DboxException e) {
						SwingUtilities.invokeLater(new ErrorRunner(ManageGroups.this,e.getMessage(),"Create Group Failed"));
						SwingUtilities.invokeLater(busyCursorOff);
						return;
					}
				}
				// Not a new group, edited use or list?
				// See if the attributes changed.
				else if (groupUse != use || groupList != list) {
					try {
						dboxAccess.modifyGroupAttributes(groupName,use,list);
					} catch(DboxException e) {
						SwingUtilities.invokeLater(new ErrorRunner(ManageGroups.this,e.getMessage(),"Edit Attributes Failed"));
					}
				}

				// Prepare to track the member and editor acl changes.
				ResultTracker tracker = new ResultTracker();
				tracker.clear();
				tracker.init("addMemberAcl",ManageGroups.this,"Add Member Failed","These members could not be added:");
				tracker.init("removeMemberAcl",ManageGroups.this,"Remove Member Failed","These members could not be removed:");
				tracker.init("addEditorAcl",ManageGroups.this,"Add Editor Failed","These editors could not be added:");
				tracker.init("removeEditorAcl",ManageGroups.this,"Remove Editor Failed","These editors could not be removed:");

				// Process member changes.

				// Sort the members into 2 groups: adds (newMembers) and deletes (groupMembers)
				// For every member (in members), see if they were in the original member list
				// (groupMembers). If so, drop them from the original list. If not, add them
				// to the new members list (newMembers).
				Enumeration users = members.elements();
				byte j = 0;
				Vector newMembers = new Vector();

				while (users.hasMoreElements()) {
					String user = (String) users.nextElement();
					if (groupMembers.contains(user)) {
						groupMembers.removeElement(user);
					}
					else {
						newMembers.addElement(user);
						tracker.track("addMemberAcl",j++,user);
					}
				}

				users = groupMembers.elements();
				j = 0;
				while (users.hasMoreElements()) {
					String user = (String) users.nextElement();
					tracker.track("removeMemberAcl",j++,user);
				}

				// Do the member additions.
				users = newMembers.elements();
				j = 0;
				while (users.hasMoreElements()) {
					String user = (String) users.nextElement();
					try {
						dboxAccess.addGroupAcl(name,user,true);
						tracker.result("addMemberAcl",j++,null);
					} catch(DboxException e) {
						tracker.result("addMemberAcl",j,e.getMessage());
					}
				}

				// Do the member deletions.
				users = groupMembers.elements();
				j = 0;
				while (users.hasMoreElements()) {
					String user = (String) users.nextElement();
					try {
						dboxAccess.removeGroupAcl(name,user,true);
						tracker.result("removeMemberAcl",j++,null);
					} catch(DboxException e) {
						tracker.result("removeMemberAcl",j,e.getMessage());
					}
				}

				// Process editor changes.
	

				// Sort the editors into 2 groups: adds (newEditors) and deletes (groupEditors)
				// For every editor (in editors), see if they were in the original editor list
				// (groupEditors). If so, drop them from the original list. If not, add them
				// to the new editors list (newEditors).
				users = editors.elements();
				j = 0;
				Vector newEditors = new Vector();

				while (users.hasMoreElements()) {
					String user = (String) users.nextElement();
					if (groupEditors.contains(user)) {
						groupEditors.removeElement(user);
					}
					else {
						newEditors.addElement(user);
						tracker.track("addEditorAcl",j++,user);
					}
				}

				users = groupEditors.elements();
				j = 0;
				while (users.hasMoreElements()) {
					String user = (String) users.nextElement();
					tracker.track("removeEditorAcl",j++,user);
				}

				// Do the editor additions.
				users = newEditors.elements();
				j = 0;
				while (users.hasMoreElements()) {
					String user = (String) users.nextElement();
					try {
						dboxAccess.addGroupAcl(name,user,false);
						tracker.result("addEditorAcl",j++,null);
					} catch(DboxException e) {
						tracker.result("addEditorAcl",j,e.getMessage());
					}
				}

				// Do the editor deletions.
				users = groupEditors.elements();
				j = 0;
				while (users.hasMoreElements()) {
					String user = (String) users.nextElement();
					try {
						dboxAccess.removeGroupAcl(name,user,false);
						tracker.result("removeEditorAcl",j++,null);
					} catch(DboxException e) {
						tracker.result("removeEditorAcl",j,e.getMessage());
					}
				}

				// Ok, done saving group changes.
				savingGroup = false;
				if (newGroup) {
					SwingUtilities.invokeLater(new ErrorRunner(ManageGroups.this,"Created group '" + name + "'.","Group Created",false));
					SwingUtilities.invokeLater(new MethodRunner(this,"finishCreateGroup"));
				}
				else {
					SwingUtilities.invokeLater(new ErrorRunner(ManageGroups.this,"Group '" + groupName + "' saved.","Group Saved",false));
				}

				// Reload the group from the server by saying it was selected again.
				SwingUtilities.invokeLater(new MethodRunner(this,"nameSelected"));
				SwingUtilities.invokeLater(busyCursorOff);
			}
			// Not saving a group, so must need the super class's run.
			else {
				super.run();
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
}
private GroupActionListener groupActionListener = new GroupActionListener();
/**
 * ManageGroups constructor comment.
 */
public ManageGroups() {
	super();
	initialize();
}
/**
 * ManageGroups constructor comment.
 * @param owner java.awt.Dialog
 */
public ManageGroups(java.awt.Dialog owner) {
	super(owner);
}
/**
 * ManageGroups constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public ManageGroups(java.awt.Dialog owner, String title) {
	super(owner, title);
}
/**
 * ManageGroups constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public ManageGroups(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ManageGroups constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public ManageGroups(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
}
/**
 * ManageGroups constructor comment.
 * @param owner java.awt.Frame
 */
public ManageGroups(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * ManageGroups constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public ManageGroups(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * ManageGroups constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public ManageGroups(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ManageGroups constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public ManageGroups(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 10:21:48 AM)
 * @param name java.lang.String
 * @param lm javax.swing.ListModel
 */
public void addNameToList(String name, Vector lm) {
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
		else if (j == 0) {
			return;
		}
		// Maybe the next spot.
		else
			i++;
	}
	// New name goes at end of list.
	lm.addElement(name);
}
/**
 * Comment
 */
public void adjustGUI() {
	// See if the name was changed (new groups only).
	boolean edited = ! groupName.equals(getNameTF().getText());

	// See if the group members were changed.
	if (! edited)
		edited = groupMembers.size() != getUserToLM().size();

	for (int i = 0; ! edited && i < groupMembers.size(); i++) {
		edited = ! groupMembers.elementAt(i).equals(getUserToLM().elementAt(i));
	}

	// See if the group editors were changed.
	if (! edited)
		edited = groupEditors.size() != getEditorToLM().size();

	for (int i = 0; ! edited && i < groupEditors.size(); i++) {
		edited = ! groupEditors.elementAt(i).equals(getEditorToLM().elementAt(i));
	}

	// See if the sharing changed.
	if (! edited) {
		switch (groupUse) {
			case DropboxGenerator.GROUP_SCOPE_MEMBER:
				edited = ! getUseMemberRB().isSelected();
				break;
			default:
				edited = ! getUseOwnerRB().isSelected();
		}
	}

	if (! edited) {
		switch (groupList) {
			case DropboxGenerator.GROUP_SCOPE_MEMBER:
				edited = ! getListMemberRB().isSelected();
				break;
			default:
				edited = ! getListOwnerRB().isSelected();
		}
	}

	// Enable or disable the group combo box and push button as appropriate.
	getLoadMI().setEnabled(! edited && getMigrateLM().size() > 0);
	getNameCB().setEnabled(! edited);
	getCloseBtn().setEnabled(! edited);
	getOkBtn().setEnabled(edited);
	getCanBtn().setEnabled(edited);
}
/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 1:53:12 PM)
 * @param on boolean
 */
public void busyCursor(boolean on) {
	if (on)
		busyCursor++;
	else
		busyCursor--;

	//System.out.println("busyCursor2: " + on + " " + busyCursor);

	if (busyCursor > 0) {
		getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		getGlassPane().setVisible(true);
	}
	else {
		getGlassPane().setVisible(false);
		getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
/**
 * Comment
 */
public void deleteLocalGroups() {
	// Get selected groups.
	Object[] groups = getMigrateLB().getSelectedValues();

	// For each group, delete it.
	for (int i = 0; i < groups.length; i++) {
		String group = (String) groups[i];
		buddyMgr.removeGroup(group);
		getMigrateLM().removeElement(group);
	}

	// If no groups are left, then disable load menu item and
	// close the local groups window.
	if (getMigrateLM().size() == 0) {
		getLoadMI().setEnabled(false);
		getMigrateDlg().dispose();
	}
}
/**
 * Comment
 */
public void doGroups(Container parent) {
	// Load up the local groups.
	loadLocalGroups();

	// Set up the gui elements.
	getOkBtn().setEnabled(false);
	getCanBtn().setEnabled(false);
	getDelBtn().setEnabled(false);
	getNameCB().setEnabled(true);

	// Query the server for the groups.
	GetGroupsHandler h = new GetGroupsHandler();
	h.gettingGroups = true;
	WorkerThread t = new WorkerThread(h);
	t.start();

	busyCursor(true);

	// Show the dialog.
	setLocationRelativeTo(parent);
	setVisible(true);
}
/**
 * Comment
 */
public void doLocalGroup() {
	getMigrateLB().clearSelection();
	getMigrateDlg().setLocationRelativeTo(this);
	getMigrateDlg().setVisible(true);
}
/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getBtnPnl() {
	if (ivjBtnPnl == null) {
		try {
			ivjBtnPnl = new javax.swing.JPanel();
			ivjBtnPnl.setName("BtnPnl");
			ivjBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOkBtn = new java.awt.GridBagConstraints();
			constraintsOkBtn.gridx = 0; constraintsOkBtn.gridy = 0;
			constraintsOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getBtnPnl().add(getOkBtn(), constraintsOkBtn);

			java.awt.GridBagConstraints constraintsCanBtn = new java.awt.GridBagConstraints();
			constraintsCanBtn.gridx = 1; constraintsCanBtn.gridy = 0;
			constraintsCanBtn.insets = new java.awt.Insets(0, 5, 0, 5);
			getBtnPnl().add(getCanBtn(), constraintsCanBtn);

			java.awt.GridBagConstraints constraintsDelBtn = new java.awt.GridBagConstraints();
			constraintsDelBtn.gridx = 2; constraintsDelBtn.gridy = 0;
			constraintsDelBtn.insets = new java.awt.Insets(0, 5, 0, 5);
			getBtnPnl().add(getDelBtn(), constraintsDelBtn);

			java.awt.GridBagConstraints constraintsCloseBtn = new java.awt.GridBagConstraints();
			constraintsCloseBtn.gridx = 3; constraintsCloseBtn.gridy = 0;
			constraintsCloseBtn.anchor = java.awt.GridBagConstraints.EAST;
			constraintsCloseBtn.weightx = 1.0;
			constraintsCloseBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getBtnPnl().add(getCloseBtn(), constraintsCloseBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjBtnPnl;
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2004 11:17:44 AM)
 * @return oem.edge.ed.odc.dsmp.common.BuddyMgr
 */
public BuddyMgr getBuddyMgr() {
	return buddyMgr;
}
/**
 * Return the GrpCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getCanBtn() {
	if (ivjCanBtn == null) {
		try {
			ivjCanBtn = new javax.swing.JButton();
			ivjCanBtn.setName("CanBtn");
			ivjCanBtn.setToolTipText("Discard changes");
			ivjCanBtn.setText("Reset");
			ivjCanBtn.addActionListener(groupActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCanBtn;
}
/**
 * Return the CloseBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getCloseBtn() {
	if (ivjCloseBtn == null) {
		try {
			ivjCloseBtn = new javax.swing.JButton();
			ivjCloseBtn.setName("CloseBtn");
			ivjCloseBtn.setToolTipText("Close this window");
			ivjCloseBtn.setText("Close");
			ivjCloseBtn.addActionListener(groupActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCloseBtn;
}
/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getCloseMI() {
	if (ivjCloseMI == null) {
		try {
			ivjCloseMI = new javax.swing.JMenuItem();
			ivjCloseMI.setName("CloseMI");
			ivjCloseMI.setText("Close");
			ivjCloseMI.setActionCommand("CloseMI");
			ivjCloseMI.addActionListener(groupActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCloseMI;
}
/**
 * Return the GrpDelBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDelBtn() {
	if (ivjDelBtn == null) {
		try {
			ivjDelBtn = new javax.swing.JButton();
			ivjDelBtn.setName("DelBtn");
			ivjDelBtn.setToolTipText("Delete selected group");
			ivjDelBtn.setText("Delete");
			ivjDelBtn.addActionListener(deleteGroupListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDelBtn;
}
/**
 * Return the EditorAddBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getEditorAddBtn() {
	if (ivjEditorAddBtn == null) {
		try {
			ivjEditorAddBtn = new javax.swing.JButton();
			ivjEditorAddBtn.setName("EditorAddBtn");
			ivjEditorAddBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/upload.gif")));
			ivjEditorAddBtn.setToolTipText("Add selected user to \'To users\' list");
			ivjEditorAddBtn.setText("");
			ivjEditorAddBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjEditorAddBtn.addActionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorAddBtn;
}
/**
 * Return the EditorBtnPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getEditorBtnPnl() {
	if (ivjEditorBtnPnl == null) {
		try {
			ivjEditorBtnPnl = new javax.swing.JPanel();
			ivjEditorBtnPnl.setName("EditorBtnPnl");
			ivjEditorBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsEditorAddBtn = new java.awt.GridBagConstraints();
			constraintsEditorAddBtn.gridx = 0; constraintsEditorAddBtn.gridy = 0;
			constraintsEditorAddBtn.insets = new java.awt.Insets(2, 0, 2, 0);
			getEditorBtnPnl().add(getEditorAddBtn(), constraintsEditorAddBtn);

			java.awt.GridBagConstraints constraintsEditorRemBtn = new java.awt.GridBagConstraints();
			constraintsEditorRemBtn.gridx = 0; constraintsEditorRemBtn.gridy = 1;
			constraintsEditorRemBtn.insets = new java.awt.Insets(2, 0, 0, 0);
			getEditorBtnPnl().add(getEditorRemBtn(), constraintsEditorRemBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorBtnPnl;
}
/**
 * Return the EditorFromLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getEditorFromLB() {
	if (ivjEditorFromLB == null) {
		try {
			ivjEditorFromLB = new javax.swing.JList();
			ivjEditorFromLB.setName("EditorFromLB");
			ivjEditorFromLB.setToolTipText("List of saved users");
			ivjEditorFromLB.setBounds(0, 0, 160, 120);
			ivjEditorFromLB.addListSelectionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorFromLB;
}
/**
 * Return the EditorFromLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getEditorFromLbl() {
	if (ivjEditorFromLbl == null) {
		try {
			ivjEditorFromLbl = new javax.swing.JLabel();
			ivjEditorFromLbl.setName("EditorFromLbl");
			ivjEditorFromLbl.setText("Select a saved Customer Connect ID:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorFromLbl;
}
/**
 * Return the EditorFromLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getEditorFromLM() {
	if (ivjEditorFromLM == null) {
		try {
			ivjEditorFromLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorFromLM;
}
/**
 * Return the EditorFromSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getEditorFromSP() {
	if (ivjEditorFromSP == null) {
		try {
			ivjEditorFromSP = new javax.swing.JScrollPane();
			ivjEditorFromSP.setName("EditorFromSP");
			ivjEditorFromSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getEditorFromSP().setViewportView(getEditorFromLB());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorFromSP;
}
/**
 * Return the EditorNewAddBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getEditorNewAddBtn() {
	if (ivjEditorNewAddBtn == null) {
		try {
			ivjEditorNewAddBtn = new javax.swing.JButton();
			ivjEditorNewAddBtn.setName("EditorNewAddBtn");
			ivjEditorNewAddBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/upload.gif")));
			ivjEditorNewAddBtn.setToolTipText("Add new user to \'To users\' list");
			ivjEditorNewAddBtn.setText("");
			ivjEditorNewAddBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjEditorNewAddBtn.addActionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorNewAddBtn;
}
/**
 * Return the EditorPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getEditorPnl() {
	if (ivjEditorPnl == null) {
		try {
			ivjEditorPnl = new javax.swing.JPanel();
			ivjEditorPnl.setName("EditorPnl");
			ivjEditorPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNewEditorLbl = new java.awt.GridBagConstraints();
			constraintsNewEditorLbl.gridx = 0; constraintsNewEditorLbl.gridy = 0;
			constraintsNewEditorLbl.anchor = java.awt.GridBagConstraints.WEST;
			getEditorPnl().add(getNewEditorLbl(), constraintsNewEditorLbl);

			java.awt.GridBagConstraints constraintsEditorToLbl = new java.awt.GridBagConstraints();
			constraintsEditorToLbl.gridx = 2; constraintsEditorToLbl.gridy = 0;
			constraintsEditorToLbl.anchor = java.awt.GridBagConstraints.WEST;
			getEditorPnl().add(getEditorToLbl(), constraintsEditorToLbl);

			java.awt.GridBagConstraints constraintsEditorTF = new java.awt.GridBagConstraints();
			constraintsEditorTF.gridx = 0; constraintsEditorTF.gridy = 1;
			constraintsEditorTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsEditorTF.weightx = 1.0;
			constraintsEditorTF.insets = new java.awt.Insets(0, 0, 3, 0);
			getEditorPnl().add(getEditorTF(), constraintsEditorTF);

			java.awt.GridBagConstraints constraintsEditorNewAddBtn = new java.awt.GridBagConstraints();
			constraintsEditorNewAddBtn.gridx = 1; constraintsEditorNewAddBtn.gridy = 1;
			constraintsEditorNewAddBtn.insets = new java.awt.Insets(0, 2, 3, 5);
			getEditorPnl().add(getEditorNewAddBtn(), constraintsEditorNewAddBtn);

			java.awt.GridBagConstraints constraintsEditorToSP = new java.awt.GridBagConstraints();
			constraintsEditorToSP.gridx = 2; constraintsEditorToSP.gridy = 1;
constraintsEditorToSP.gridheight = 0;
			constraintsEditorToSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsEditorToSP.weightx = 1.0;
			constraintsEditorToSP.weighty = 1.0;
			getEditorPnl().add(getEditorToSP(), constraintsEditorToSP);

			java.awt.GridBagConstraints constraintsEditorFromLbl = new java.awt.GridBagConstraints();
			constraintsEditorFromLbl.gridx = 0; constraintsEditorFromLbl.gridy = 2;
			constraintsEditorFromLbl.gridwidth = 2;
			constraintsEditorFromLbl.anchor = java.awt.GridBagConstraints.WEST;
			getEditorPnl().add(getEditorFromLbl(), constraintsEditorFromLbl);

			java.awt.GridBagConstraints constraintsEditorFromSP = new java.awt.GridBagConstraints();
			constraintsEditorFromSP.gridx = 0; constraintsEditorFromSP.gridy = 3;
			constraintsEditorFromSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsEditorFromSP.weightx = 1.0;
			constraintsEditorFromSP.weighty = 1.0;
			getEditorPnl().add(getEditorFromSP(), constraintsEditorFromSP);

			java.awt.GridBagConstraints constraintsEditorBtnPnl = new java.awt.GridBagConstraints();
			constraintsEditorBtnPnl.gridx = 1; constraintsEditorBtnPnl.gridy = 3;
			constraintsEditorBtnPnl.insets = new java.awt.Insets(0, 2, 0, 5);
			getEditorPnl().add(getEditorBtnPnl(), constraintsEditorBtnPnl);
			ivjEditorPnl.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), "Editors", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorPnl;
}
/**
 * Return the EditorRemBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getEditorRemBtn() {
	if (ivjEditorRemBtn == null) {
		try {
			ivjEditorRemBtn = new javax.swing.JButton();
			ivjEditorRemBtn.setName("EditorRemBtn");
			ivjEditorRemBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/download.gif")));
			ivjEditorRemBtn.setToolTipText("Remove user from \'To users\' list");
			ivjEditorRemBtn.setText("");
			ivjEditorRemBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjEditorRemBtn.addActionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorRemBtn;
}
/**
 * Return the EditorTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getEditorTF() {
	if (ivjEditorTF == null) {
		try {
			ivjEditorTF = new javax.swing.JTextField();
			ivjEditorTF.setName("EditorTF");
			ivjEditorTF.setToolTipText("Enter Customer Connect ID of new group editor");
			ivjEditorTF.addActionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorTF;
}
/**
 * Return the EditorToLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getEditorToLB() {
	if (ivjEditorToLB == null) {
		try {
			ivjEditorToLB = new javax.swing.JList();
			ivjEditorToLB.setName("EditorToLB");
			ivjEditorToLB.setToolTipText("List of users in group");
			ivjEditorToLB.setBounds(0, 0, 160, 120);
			ivjEditorToLB.addListSelectionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorToLB;
}
/**
 * Return the EditorToLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getEditorToLbl() {
	if (ivjEditorToLbl == null) {
		try {
			ivjEditorToLbl = new javax.swing.JLabel();
			ivjEditorToLbl.setName("EditorToLbl");
			ivjEditorToLbl.setText("Editors of this group:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorToLbl;
}
/**
 * Return the EditorToLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getEditorToLM() {
	if (ivjEditorToLM == null) {
		try {
			ivjEditorToLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorToLM;
}
/**
 * Return the EditorToSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getEditorToSP() {
	if (ivjEditorToSP == null) {
		try {
			ivjEditorToSP = new javax.swing.JScrollPane();
			ivjEditorToSP.setName("EditorToSP");
			ivjEditorToSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getEditorToSP().setViewportView(getEditorToLB());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEditorToSP;
}
/**
 * Return the JLabel10 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGroupLbl() {
	if (ivjGroupLbl == null) {
		try {
			ivjGroupLbl = new javax.swing.JLabel();
			ivjGroupLbl.setName("GroupLbl");
			ivjGroupLbl.setText("Group:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGroupLbl;
}
/**
 * Return the GroupM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getGroupM() {
	if (ivjGroupM == null) {
		try {
			ivjGroupM = new javax.swing.JMenu();
			ivjGroupM.setName("GroupM");
			ivjGroupM.setText("Groups");
			ivjGroupM.add(getLoadMI());
			ivjGroupM.add(getCloseMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGroupM;
}
/**
 * Return the JPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getGroupPnl() {
	if (ivjGroupPnl == null) {
		try {
			ivjGroupPnl = new javax.swing.JPanel();
			ivjGroupPnl.setName("GroupPnl");
			ivjGroupPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNamePnl = new java.awt.GridBagConstraints();
			constraintsNamePnl.gridx = 0; constraintsNamePnl.gridy = 0;
			constraintsNamePnl.fill = java.awt.GridBagConstraints.BOTH;
			constraintsNamePnl.weightx = 1.0;
			constraintsNamePnl.insets = new java.awt.Insets(2, 2, 5, 2);
			getGroupPnl().add(getNamePnl(), constraintsNamePnl);

			java.awt.GridBagConstraints constraintsBtnPnl = new java.awt.GridBagConstraints();
			constraintsBtnPnl.gridx = 0; constraintsBtnPnl.gridy = 4;
			constraintsBtnPnl.fill = java.awt.GridBagConstraints.BOTH;
			constraintsBtnPnl.weightx = 1.0;
			constraintsBtnPnl.insets = new java.awt.Insets(5, 2, 2, 2);
			getGroupPnl().add(getBtnPnl(), constraintsBtnPnl);

			java.awt.GridBagConstraints constraintsUserPnl = new java.awt.GridBagConstraints();
			constraintsUserPnl.gridx = 0; constraintsUserPnl.gridy = 1;
			constraintsUserPnl.fill = java.awt.GridBagConstraints.BOTH;
			constraintsUserPnl.weightx = 1.0;
			constraintsUserPnl.weighty = 1.0;
			constraintsUserPnl.insets = new java.awt.Insets(0, 2, 0, 2);
			getGroupPnl().add(getUserPnl(), constraintsUserPnl);

			java.awt.GridBagConstraints constraintsEditorPnl = new java.awt.GridBagConstraints();
			constraintsEditorPnl.gridx = 0; constraintsEditorPnl.gridy = 2;
			constraintsEditorPnl.fill = java.awt.GridBagConstraints.BOTH;
			constraintsEditorPnl.weightx = 1.0;
			constraintsEditorPnl.weighty = 1.0;
			constraintsEditorPnl.insets = new java.awt.Insets(5, 2, 0, 2);
			getGroupPnl().add(getEditorPnl(), constraintsEditorPnl);

			java.awt.GridBagConstraints constraintsSharePnl = new java.awt.GridBagConstraints();
			constraintsSharePnl.gridx = 0; constraintsSharePnl.gridy = 3;
			constraintsSharePnl.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSharePnl.insets = new java.awt.Insets(5, 2, 0, 2);
			getGroupPnl().add(getSharePnl(), constraintsSharePnl);
			ivjGroupPnl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102,102,153),1));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGroupPnl;
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameCB = new java.awt.GridBagConstraints();
			constraintsNameCB.gridx = 1; constraintsNameCB.gridy = 0;
			constraintsNameCB.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameCB.weightx = 1.0;
			constraintsNameCB.insets = new java.awt.Insets(5, 0, 5, 5);
			getJDialogContentPane().add(getNameCB(), constraintsNameCB);

			java.awt.GridBagConstraints constraintsGroupLbl = new java.awt.GridBagConstraints();
			constraintsGroupLbl.gridx = 0; constraintsGroupLbl.gridy = 0;
			constraintsGroupLbl.insets = new java.awt.Insets(5, 5, 5, 5);
			getJDialogContentPane().add(getGroupLbl(), constraintsGroupLbl);

			java.awt.GridBagConstraints constraintsGroupPnl = new java.awt.GridBagConstraints();
			constraintsGroupPnl.gridx = 0; constraintsGroupPnl.gridy = 1;
			constraintsGroupPnl.gridwidth = 0;
			constraintsGroupPnl.fill = java.awt.GridBagConstraints.BOTH;
			constraintsGroupPnl.weightx = 1.0;
			constraintsGroupPnl.weighty = 1.0;
			constraintsGroupPnl.insets = new java.awt.Insets(5, 5, 5, 5);
			getJDialogContentPane().add(getGroupPnl(), constraintsGroupPnl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Return the JDialogContentPane7 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJDialogContentPane7() {
	if (ivjJDialogContentPane7 == null) {
		try {
			ivjJDialogContentPane7 = new javax.swing.JPanel();
			ivjJDialogContentPane7.setName("JDialogContentPane7");
			ivjJDialogContentPane7.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsMigateTA = new java.awt.GridBagConstraints();
			constraintsMigateTA.gridx = 0; constraintsMigateTA.gridy = 0;
			constraintsMigateTA.gridwidth = 0;
			constraintsMigateTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsMigateTA.insets = new java.awt.Insets(10, 10, 5, 10);
			getJDialogContentPane7().add(getMigateTA(), constraintsMigateTA);

			java.awt.GridBagConstraints constraintsMigrateSP = new java.awt.GridBagConstraints();
			constraintsMigrateSP.gridx = 0; constraintsMigrateSP.gridy = 1;
			constraintsMigrateSP.gridwidth = 0;
			constraintsMigrateSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsMigrateSP.weightx = 1.0;
			constraintsMigrateSP.weighty = 1.0;
			constraintsMigrateSP.insets = new java.awt.Insets(5, 10, 10, 10);
			getJDialogContentPane7().add(getMigrateSP(), constraintsMigrateSP);

			java.awt.GridBagConstraints constraintsMigrateOkBtn = new java.awt.GridBagConstraints();
			constraintsMigrateOkBtn.gridx = 0; constraintsMigrateOkBtn.gridy = 2;
			constraintsMigrateOkBtn.anchor = java.awt.GridBagConstraints.EAST;
			constraintsMigrateOkBtn.weightx = 1.0;
			constraintsMigrateOkBtn.insets = new java.awt.Insets(0, 10, 10, 5);
			getJDialogContentPane7().add(getMigrateOkBtn(), constraintsMigrateOkBtn);

			java.awt.GridBagConstraints constraintsMigrateCanBtn = new java.awt.GridBagConstraints();
			constraintsMigrateCanBtn.gridx = 1; constraintsMigrateCanBtn.gridy = 2;
			constraintsMigrateCanBtn.insets = new java.awt.Insets(0, 5, 10, 5);
			getJDialogContentPane7().add(getMigrateCanBtn(), constraintsMigrateCanBtn);

			java.awt.GridBagConstraints constraintsMigrateCloseBtn = new java.awt.GridBagConstraints();
			constraintsMigrateCloseBtn.gridx = 2; constraintsMigrateCloseBtn.gridy = 2;
			constraintsMigrateCloseBtn.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMigrateCloseBtn.weightx = 1.0;
			constraintsMigrateCloseBtn.insets = new java.awt.Insets(0, 5, 10, 10);
			getJDialogContentPane7().add(getMigrateCloseBtn(), constraintsMigrateCloseBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane7;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getListLbl() {
	if (ivjListLbl == null) {
		try {
			ivjListLbl = new javax.swing.JLabel();
			ivjListLbl.setName("ListLbl");
			ivjListLbl.setText("Who may see member list?");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjListLbl;
}
/**
 * Return the ListMemberRB property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getListMemberRB() {
	if (ivjListMemberRB == null) {
		try {
			ivjListMemberRB = new javax.swing.JRadioButton();
			ivjListMemberRB.setName("ListMemberRB");
			ivjListMemberRB.setToolTipText("Owner and members may see member list");
			ivjListMemberRB.setText("Members");
			ivjListMemberRB.addItemListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjListMemberRB;
}
/**
 * Return the ListOwnerRB property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getListOwnerRB() {
	if (ivjListOwnerRB == null) {
		try {
			ivjListOwnerRB = new javax.swing.JRadioButton();
			ivjListOwnerRB.setName("ListOwnerRB");
			ivjListOwnerRB.setToolTipText("Only owner may see member list");
			ivjListOwnerRB.setText("Owner");
			ivjListOwnerRB.addItemListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjListOwnerRB;
}
/**
 * Return the LoadMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getLoadMI() {
	if (ivjLoadMI == null) {
		try {
			ivjLoadMI = new javax.swing.JMenuItem();
			ivjLoadMI.setName("LoadMI");
			ivjLoadMI.setToolTipText("Migrate old groups to the server");
			ivjLoadMI.setText("Load local group");
			ivjLoadMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						doLocalGroup();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoadMI;
}
/**
 * Return the ManageGroupsJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
private javax.swing.JMenuBar getManageGroupsJMenuBar() {
	if (ivjManageGroupsJMenuBar == null) {
		try {
			ivjManageGroupsJMenuBar = new javax.swing.JMenuBar();
			ivjManageGroupsJMenuBar.setName("ManageGroupsJMenuBar");
			ivjManageGroupsJMenuBar.add(getGroupM());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjManageGroupsJMenuBar;
}
/**
 * Return the MigateTA property value.
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getMigateTA() {
	if (ivjMigateTA == null) {
		try {
			ivjMigateTA = new javax.swing.JTextArea();
			ivjMigateTA.setName("MigateTA");
			ivjMigateTA.setLineWrap(true);
			ivjMigateTA.setWrapStyleWord(true);
			ivjMigateTA.setText("Group definitions are now stored on our servers. You have the following groups defined locally. Select a group and press the Open button to load the group onto the manage groups window. You may then save the group to the server. Press the Delete button to remove the selected groups.");
			ivjMigateTA.setBackground(new java.awt.Color(204,204,204));
			ivjMigateTA.setForeground(new java.awt.Color(102,102,153));
			ivjMigateTA.setFont(new java.awt.Font("Arial", 1, 12));
			ivjMigateTA.setEditable(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMigateTA;
}
/**
 * Return the MigrateCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getMigrateCanBtn() {
	if (ivjMigrateCanBtn == null) {
		try {
			ivjMigrateCanBtn = new javax.swing.JButton();
			ivjMigrateCanBtn.setName("MigrateCanBtn");
			ivjMigrateCanBtn.setToolTipText("Delete the selected groups");
			ivjMigrateCanBtn.setText("Delete");
			ivjMigrateCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						deleteLocalGroups();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMigrateCanBtn;
}
/**
 * Return the MigrateCloseBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getMigrateCloseBtn() {
	if (ivjMigrateCloseBtn == null) {
		try {
			ivjMigrateCloseBtn = new javax.swing.JButton();
			ivjMigrateCloseBtn.setName("MigrateCloseBtn");
			ivjMigrateCloseBtn.setToolTipText("Close this window");
			ivjMigrateCloseBtn.setText("Close");
			ivjMigrateCloseBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getMigrateDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMigrateCloseBtn;
}
/**
 * Return the MigrateDlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getMigrateDlg() {
	if (ivjMigrateDlg == null) {
		try {
			ivjMigrateDlg = new javax.swing.JDialog();
			ivjMigrateDlg.setName("MigrateDlg");
			ivjMigrateDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjMigrateDlg.setBounds(33, 663, 362, 291);
			ivjMigrateDlg.setModal(true);
			ivjMigrateDlg.setTitle("Local Groups");
			getMigrateDlg().setContentPane(getJDialogContentPane7());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMigrateDlg;
}
/**
 * Return the MigrateLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getMigrateLB() {
	if (ivjMigrateLB == null) {
		try {
			ivjMigrateLB = new javax.swing.JList();
			ivjMigrateLB.setName("MigrateLB");
			ivjMigrateLB.setBounds(0, 0, 160, 120);
			ivjMigrateLB.addListSelectionListener(new javax.swing.event.ListSelectionListener() { 
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {
					try {
						migrateListChanged();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}    
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMigrateLB;
}
/**
 * Return the MigrateLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getMigrateLM() {
	if (ivjMigrateLM == null) {
		try {
			ivjMigrateLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMigrateLM;
}
/**
 * Return the MigrateOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getMigrateOkBtn() {
	if (ivjMigrateOkBtn == null) {
		try {
			ivjMigrateOkBtn = new javax.swing.JButton();
			ivjMigrateOkBtn.setName("MigrateOkBtn");
			ivjMigrateOkBtn.setToolTipText("Use selected group as basis for a new group");
			ivjMigrateOkBtn.setText("Open");
			ivjMigrateOkBtn.addActionListener(groupSelectionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMigrateOkBtn;
}
/**
 * Return the MigrateSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getMigrateSP() {
	if (ivjMigrateSP == null) {
		try {
			ivjMigrateSP = new javax.swing.JScrollPane();
			ivjMigrateSP.setName("MigrateSP");
			getMigrateSP().setViewportView(getMigrateLB());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMigrateSP;
}
/**
 * Return the JComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getNameCB() {
	if (ivjNameCB == null) {
		try {
			ivjNameCB = new javax.swing.JComboBox();
			ivjNameCB.setName("NameCB");
			ivjNameCB.setToolTipText("Select group to edit");
			ivjNameCB.setEditable(false);
			ivjNameCB.addItemListener(groupSelectionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameCB;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getNameLbl() {
	if (ivjNameLbl == null) {
		try {
			ivjNameLbl = new javax.swing.JLabel();
			ivjNameLbl.setName("NameLbl");
			ivjNameLbl.setText("Group Name:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameLbl;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getNamePnl() {
	if (ivjNamePnl == null) {
		try {
			ivjNamePnl = new javax.swing.JPanel();
			ivjNamePnl.setName("NamePnl");
			ivjNamePnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameLbl = new java.awt.GridBagConstraints();
			constraintsNameLbl.gridx = 0; constraintsNameLbl.gridy = 0;
			constraintsNameLbl.insets = new java.awt.Insets(0, 0, 0, 5);
			getNamePnl().add(getNameLbl(), constraintsNameLbl);

			java.awt.GridBagConstraints constraintsNameTF = new java.awt.GridBagConstraints();
			constraintsNameTF.gridx = 1; constraintsNameTF.gridy = 0;
			constraintsNameTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTF.weightx = 1.0;
			getNamePnl().add(getNameTF(), constraintsNameTF);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNamePnl;
}
/**
 * Return the GrpNameTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getNameTF() {
	if (ivjNameTF == null) {
		try {
			ivjNameTF = new javax.swing.JTextField();
			ivjNameTF.setName("NameTF");
			ivjNameTF.setToolTipText("Current group name, edit to change");
			ivjNameTF.setText("");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameTF;
}
/**
 * Return the NewEditorLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getNewEditorLbl() {
	if (ivjNewEditorLbl == null) {
		try {
			ivjNewEditorLbl = new javax.swing.JLabel();
			ivjNewEditorLbl.setName("NewEditorLbl");
			ivjNewEditorLbl.setText("Enter a Customer Connect ID:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNewEditorLbl;
}
/**
 * Return the JLabel51 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getNewUserLbl() {
	if (ivjNewUserLbl == null) {
		try {
			ivjNewUserLbl = new javax.swing.JLabel();
			ivjNewUserLbl.setName("NewUserLbl");
			ivjNewUserLbl.setText("Enter a Customer Connect ID:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNewUserLbl;
}
/**
 * Return the GrpOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOkBtn() {
	if (ivjOkBtn == null) {
		try {
			ivjOkBtn = new javax.swing.JButton();
			ivjOkBtn.setName("OkBtn");
			ivjOkBtn.setToolTipText("Save");
			ivjOkBtn.setText("Save");
			ivjOkBtn.addActionListener(groupActionListener);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOkBtn;
}
/**
 * Return the PkgBF property value.
 * @return javax.swing.BorderFactory
 */
private javax.swing.BorderFactory getPkgBF() {
	return ivjPkgBF;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getSharePnl() {
	if (ivjSharePnl == null) {
		try {
			ivjSharePnl = new javax.swing.JPanel();
			ivjSharePnl.setName("SharePnl");
			ivjSharePnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsUseLbl = new java.awt.GridBagConstraints();
			constraintsUseLbl.gridx = 0; constraintsUseLbl.gridy = 0;
			constraintsUseLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUseLbl.insets = new java.awt.Insets(0, 0, 0, 5);
			getSharePnl().add(getUseLbl(), constraintsUseLbl);

			java.awt.GridBagConstraints constraintsListLbl = new java.awt.GridBagConstraints();
			constraintsListLbl.gridx = 0; constraintsListLbl.gridy = 1;
			constraintsListLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsListLbl.insets = new java.awt.Insets(0, 0, 0, 5);
			getSharePnl().add(getListLbl(), constraintsListLbl);

			java.awt.GridBagConstraints constraintsUseOwnerRB = new java.awt.GridBagConstraints();
			constraintsUseOwnerRB.gridx = 1; constraintsUseOwnerRB.gridy = 0;
			getSharePnl().add(getUseOwnerRB(), constraintsUseOwnerRB);

			java.awt.GridBagConstraints constraintsListOwnerRB = new java.awt.GridBagConstraints();
			constraintsListOwnerRB.gridx = 1; constraintsListOwnerRB.gridy = 1;
			getSharePnl().add(getListOwnerRB(), constraintsListOwnerRB);

			java.awt.GridBagConstraints constraintsUseMemberRB = new java.awt.GridBagConstraints();
			constraintsUseMemberRB.gridx = 2; constraintsUseMemberRB.gridy = 0;
			constraintsUseMemberRB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUseMemberRB.weightx = 1.0;
			getSharePnl().add(getUseMemberRB(), constraintsUseMemberRB);

			java.awt.GridBagConstraints constraintsListMemberRB = new java.awt.GridBagConstraints();
			constraintsListMemberRB.gridx = 2; constraintsListMemberRB.gridy = 1;
			constraintsListMemberRB.anchor = java.awt.GridBagConstraints.WEST;
			getSharePnl().add(getListMemberRB(), constraintsListMemberRB);
			ivjSharePnl.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), "Group Sharing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSharePnl;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getUseLbl() {
	if (ivjUseLbl == null) {
		try {
			ivjUseLbl = new javax.swing.JLabel();
			ivjUseLbl.setName("UseLbl");
			ivjUseLbl.setText("Who may use this group?");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUseLbl;
}
/**
 * Return the UseMemberRB property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getUseMemberRB() {
	if (ivjUseMemberRB == null) {
		try {
			ivjUseMemberRB = new javax.swing.JRadioButton();
			ivjUseMemberRB.setName("UseMemberRB");
			ivjUseMemberRB.setToolTipText("Owner and members may use group");
			ivjUseMemberRB.setText("Members");
			ivjUseMemberRB.addItemListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUseMemberRB;
}
/**
 * Return the UseOwnerRB property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getUseOwnerRB() {
	if (ivjUseOwnerRB == null) {
		try {
			ivjUseOwnerRB = new javax.swing.JRadioButton();
			ivjUseOwnerRB.setName("UseOwnerRB");
			ivjUseOwnerRB.setToolTipText("Only owner may use group");
			ivjUseOwnerRB.setText("Owner");
			ivjUseOwnerRB.addItemListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUseOwnerRB;
}
/**
 * Return the GrpUserAddBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getUserAddBtn() {
	if (ivjUserAddBtn == null) {
		try {
			ivjUserAddBtn = new javax.swing.JButton();
			ivjUserAddBtn.setName("UserAddBtn");
			ivjUserAddBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/upload.gif")));
			ivjUserAddBtn.setToolTipText("Add selected user to \'To users\' list");
			ivjUserAddBtn.setText("");
			ivjUserAddBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjUserAddBtn.addActionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserAddBtn;
}
/**
 * Return the JPanel51 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getUserBtnPnl() {
	if (ivjUserBtnPnl == null) {
		try {
			ivjUserBtnPnl = new javax.swing.JPanel();
			ivjUserBtnPnl.setName("UserBtnPnl");
			ivjUserBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsUserAddBtn = new java.awt.GridBagConstraints();
			constraintsUserAddBtn.gridx = 0; constraintsUserAddBtn.gridy = 0;
			constraintsUserAddBtn.insets = new java.awt.Insets(2, 0, 2, 0);
			getUserBtnPnl().add(getUserAddBtn(), constraintsUserAddBtn);

			java.awt.GridBagConstraints constraintsUserRemBtn = new java.awt.GridBagConstraints();
			constraintsUserRemBtn.gridx = 0; constraintsUserRemBtn.gridy = 1;
			constraintsUserRemBtn.insets = new java.awt.Insets(2, 0, 0, 0);
			getUserBtnPnl().add(getUserRemBtn(), constraintsUserRemBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserBtnPnl;
}
/**
 * Return the GrpUserFromLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getUserFromLB() {
	if (ivjUserFromLB == null) {
		try {
			ivjUserFromLB = new javax.swing.JList();
			ivjUserFromLB.setName("UserFromLB");
			ivjUserFromLB.setToolTipText("List of saved users");
			ivjUserFromLB.setBounds(0, 0, 160, 120);
			ivjUserFromLB.addListSelectionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserFromLB;
}
/**
 * Return the JLabel11 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getUserFromLbl() {
	if (ivjUserFromLbl == null) {
		try {
			ivjUserFromLbl = new javax.swing.JLabel();
			ivjUserFromLbl.setName("UserFromLbl");
			ivjUserFromLbl.setText("Select a saved Customer Connect ID:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserFromLbl;
}
/**
 * Return the GrpUserFromLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getUserFromLM() {
	if (ivjUserFromLM == null) {
		try {
			ivjUserFromLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserFromLM;
}
/**
 * Return the grpUserFromSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getUserFromSP() {
	if (ivjUserFromSP == null) {
		try {
			ivjUserFromSP = new javax.swing.JScrollPane();
			ivjUserFromSP.setName("UserFromSP");
			ivjUserFromSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getUserFromSP().setViewportView(getUserFromLB());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserFromSP;
}
/**
 * Return the GrpUserNewAddBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getUserNewAddBtn() {
	if (ivjUserNewAddBtn == null) {
		try {
			ivjUserNewAddBtn = new javax.swing.JButton();
			ivjUserNewAddBtn.setName("UserNewAddBtn");
			ivjUserNewAddBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/upload.gif")));
			ivjUserNewAddBtn.setToolTipText("Add new user to \'To users\' list");
			ivjUserNewAddBtn.setText("");
			ivjUserNewAddBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjUserNewAddBtn.addActionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserNewAddBtn;
}
/**
 * Return the UserPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getUserPnl() {
	if (ivjUserPnl == null) {
		try {
			ivjUserPnl = new javax.swing.JPanel();
			ivjUserPnl.setName("UserPnl");
			ivjUserPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNewUserLbl = new java.awt.GridBagConstraints();
			constraintsNewUserLbl.gridx = 0; constraintsNewUserLbl.gridy = 0;
			constraintsNewUserLbl.anchor = java.awt.GridBagConstraints.WEST;
			getUserPnl().add(getNewUserLbl(), constraintsNewUserLbl);

			java.awt.GridBagConstraints constraintsUserTF = new java.awt.GridBagConstraints();
			constraintsUserTF.gridx = 0; constraintsUserTF.gridy = 1;
			constraintsUserTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUserTF.weightx = 1.0;
			constraintsUserTF.insets = new java.awt.Insets(0, 0, 3, 0);
			getUserPnl().add(getUserTF(), constraintsUserTF);

			java.awt.GridBagConstraints constraintsUserNewAddBtn = new java.awt.GridBagConstraints();
			constraintsUserNewAddBtn.gridx = 1; constraintsUserNewAddBtn.gridy = 1;
			constraintsUserNewAddBtn.insets = new java.awt.Insets(0, 2, 3, 5);
			getUserPnl().add(getUserNewAddBtn(), constraintsUserNewAddBtn);

			java.awt.GridBagConstraints constraintsUserToLbl = new java.awt.GridBagConstraints();
			constraintsUserToLbl.gridx = 2; constraintsUserToLbl.gridy = 0;
			constraintsUserToLbl.anchor = java.awt.GridBagConstraints.WEST;
			getUserPnl().add(getUserToLbl(), constraintsUserToLbl);

			java.awt.GridBagConstraints constraintsUserToSP = new java.awt.GridBagConstraints();
			constraintsUserToSP.gridx = 2; constraintsUserToSP.gridy = 1;
constraintsUserToSP.gridheight = 0;
			constraintsUserToSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsUserToSP.weightx = 1.0;
			constraintsUserToSP.weighty = 1.0;
			getUserPnl().add(getUserToSP(), constraintsUserToSP);

			java.awt.GridBagConstraints constraintsUserFromLbl = new java.awt.GridBagConstraints();
			constraintsUserFromLbl.gridx = 0; constraintsUserFromLbl.gridy = 2;
			constraintsUserFromLbl.gridwidth = 2;
			constraintsUserFromLbl.anchor = java.awt.GridBagConstraints.WEST;
			getUserPnl().add(getUserFromLbl(), constraintsUserFromLbl);

			java.awt.GridBagConstraints constraintsUserFromSP = new java.awt.GridBagConstraints();
			constraintsUserFromSP.gridx = 0; constraintsUserFromSP.gridy = 3;
			constraintsUserFromSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsUserFromSP.weightx = 1.0;
			constraintsUserFromSP.weighty = 1.0;
			getUserPnl().add(getUserFromSP(), constraintsUserFromSP);

			java.awt.GridBagConstraints constraintsUserBtnPnl = new java.awt.GridBagConstraints();
			constraintsUserBtnPnl.gridx = 1; constraintsUserBtnPnl.gridy = 3;
			constraintsUserBtnPnl.insets = new java.awt.Insets(0, 2, 0, 5);
			getUserPnl().add(getUserBtnPnl(), constraintsUserBtnPnl);
			ivjUserPnl.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), "Members", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserPnl;
}
/**
 * Return the GrpUserRemBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getUserRemBtn() {
	if (ivjUserRemBtn == null) {
		try {
			ivjUserRemBtn = new javax.swing.JButton();
			ivjUserRemBtn.setName("UserRemBtn");
			ivjUserRemBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/ftp/client/download.gif")));
			ivjUserRemBtn.setToolTipText("Remove user from \'To users\' list");
			ivjUserRemBtn.setText("");
			ivjUserRemBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjUserRemBtn.addActionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserRemBtn;
}
/**
 * Return the GrpToUserTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getUserTF() {
	if (ivjUserTF == null) {
		try {
			ivjUserTF = new javax.swing.JTextField();
			ivjUserTF.setName("UserTF");
			ivjUserTF.setToolTipText("Enter Customer Connect ID of new group member");
			ivjUserTF.addActionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserTF;
}
/**
 * Return the GrpUserToLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getUserToLB() {
	if (ivjUserToLB == null) {
		try {
			ivjUserToLB = new javax.swing.JList();
			ivjUserToLB.setName("UserToLB");
			ivjUserToLB.setToolTipText("List of users in group");
			ivjUserToLB.setBounds(0, 0, 160, 120);
			ivjUserToLB.addListSelectionListener(editHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserToLB;
}
/**
 * Return the JLabel21 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getUserToLbl() {
	if (ivjUserToLbl == null) {
		try {
			ivjUserToLbl = new javax.swing.JLabel();
			ivjUserToLbl.setName("UserToLbl");
			ivjUserToLbl.setText("Members of this group:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserToLbl;
}
/**
 * Return the GrpUserToLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getUserToLM() {
	if (ivjUserToLM == null) {
		try {
			ivjUserToLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserToLM;
}
/**
 * Return the GrpUserToSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getUserToSP() {
	if (ivjUserToSP == null) {
		try {
			ivjUserToSP = new javax.swing.JScrollPane();
			ivjUserToSP.setName("UserToSP");
			ivjUserToSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getUserToSP().setViewportView(getUserToLB());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserToSP;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ManageGroups");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setJMenuBar(getManageGroupsJMenuBar());
		setSize(449, 544);
		setModal(true);
		setTitle("Manage Groups");
		setContentPane(getJDialogContentPane());

		getUserFromLB().setModel(getUserFromLM());
		getUserToLB().setModel(getUserToLM());
		getEditorFromLB().setModel(getEditorFromLM());
		getEditorToLB().setModel(getEditorToLM());
		getMigrateLB().setModel(getMigrateLM());

		// Setup a MouseAdapter to handle double clicks on the edit group lists.
		getUserFromLB().addMouseListener(editHandler);
		getUserToLB().addMouseListener(editHandler);
		getEditorFromLB().addMouseListener(editHandler);
		getEditorToLB().addMouseListener(editHandler);

		// Prepare to listen for changes on the group edit dialog.
		getUserTF().getDocument().addDocumentListener(editHandler);
		getNameTF().getDocument().addDocumentListener(editHandler);
		getEditorTF().getDocument().addDocumentListener(editHandler);

		// Set up the glass pane to handle the busy cursor.
		getGlassPane().addMouseListener(new MouseAdapter() {});
		getGlassPane().addKeyListener(new KeyAdapter() {});

		// Group radio buttons.
		ButtonGroup bg = new ButtonGroup();
		bg.add(getListOwnerRB());
		bg.add(getListMemberRB());
		bg = new ButtonGroup();
		bg.add(getUseOwnerRB());
		bg.add(getUseMemberRB());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2004 9:40:33 AM)
 */
public void loadLocalGroups() {
	Vector groups = buddyMgr.getGroupList();
	getMigrateLM().clear();
	Enumeration e = groups.elements();
	while (e.hasMoreElements()) {
		BuddyMgr.addNameToList((String) e.nextElement(),getMigrateLM());
	}
}
/**
 * Comment
 */
public void migrateListChanged() {
	int i = getMigrateLB().getSelectedIndices().length;
	getMigrateOkBtn().setEnabled(i == 1);
	getMigrateCanBtn().setEnabled(i > 0);
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 10:01:03 AM)
 */
public void saveBuddyList() {
	// Merge the editors and members together.
	Vector bl = new Vector();
	Enumeration e = getUserFromLM().elements();
	while (e.hasMoreElements()) {
		addNameToList((String) e.nextElement(),bl);
	}
	e = getUserToLM().elements();
	while (e.hasMoreElements()) {
		addNameToList((String) e.nextElement(),bl);
	}
	e = getEditorFromLM().elements();
	while (e.hasMoreElements()) {
		addNameToList((String) e.nextElement(),bl);
	}
	e = getEditorToLM().elements();
	while (e.hasMoreElements()) {
		addNameToList((String) e.nextElement(),bl);
	}
	buddyMgr.setBuddyList(bl);
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2004 11:17:44 AM)
 * @param newBuddyMgr oem.edge.ed.odc.dsmp.common.BuddyMgr
 */
public void setBuddyMgr(BuddyMgr newBuddyMgr) {
	buddyMgr = newBuddyMgr;
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2004 2:34:02 PM)
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
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
