package oem.edge.ed.odc.meeting.clienta;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import oem.edge.ed.odc.meeting.common.*;

/**
 * Insert the type's description here.
 * Creation date: (7/23/2002 9:28:30 AM)
 * @author: Mike Zarnick
 */
public class PresencePanel extends Panel implements PresenceListener, ActionListener, MessageListener, MeetingListener {
	static public Color present = new Color(0,130,0);
	static public Color absent = Color.black;
	static public Color invalid = new Color(170,0,0);
	static public char[] PROJECT = { 'p','r','o','j','e','c','t',':',' ' };
	static private int iconX = 10;
	static private int projIconX = iconX + 15;
	static private int nameX = iconX + 15;
	static private int projNameX = projIconX + 15;
	public boolean limitProjChat = false;
	private boolean mainUser = false;
	private Image ownerImage = null;
	private Image controlImage = null;
	private MediaTracker mt = null;
	private Vector ul = new Vector();
	private Hashtable ikul = new Hashtable();
	private Hashtable pkul = new Hashtable();
	private int itemHeight;
	private int itemWidth = 0;
	private int panelHeight = 20;
	private FontMetrics fm;
	private MouseHandler mh = new MouseHandler();
	private WindowHandler wh = new WindowHandler();
	private PopupMenu userPU;
	private MenuItem userChatMI;
	private MenuItem userRemoveMI;
	private MenuItem userDropMI;
	private PopupMenu projUserPU;
	private MenuItem projUserChatMI;
	private MenuItem projUserRemoveMI;
	private PopupMenu invitePU;
	private MenuItem inviteMI;
	private PopupMenu dropPU;
	private MenuItem dropMI;
	private PopupMenu chatPU;
	private MenuItem chatMI;
	private User inControl = null;
	private DSMPDispatcher dispatcher = null;
	private Hashtable mw = new Hashtable();

	class Invite {
		char[] name;
		int inviteID;
		int width;
		boolean selected = false;
		boolean isOwner = false;
		boolean isProjUser = false;
		Invite(char[] n, int id) {
			this.name = n;
			this.inviteID = id;
		}
	}

	class User extends Invite {
		boolean present;
		int participantID;
		int nickLength;
		User(char[] u, int inviteID, int partID, boolean p) {
			super(u,inviteID);
			this.participantID = partID;
			this.present = p;
		}
	}

	class Project extends Invite {
		Vector ul = new Vector();
		Project(char[] g, int id) {
			super(g,id);
		}
	}

	class MouseHandler extends MouseAdapter {
		Invite selected = null;
		PresencePanel pp = null;
		public Invite mapToInvite(int x, int y) {
			Invite select = null;
			boolean done = false;
			int itemy = 10 + itemHeight - 1;

			Enumeration ml = ul.elements();
			while (! done && select == null && ml.hasMoreElements()) {
				Invite i = (Invite) ml.nextElement();

				if (y < itemy) {
					if (x < nameX + i.width)
						select = i;
					done = true;
				}

				if (i instanceof Project) {
					Enumeration sl = ((Project) i).ul.elements();
					while (! done && select == null && sl.hasMoreElements()) {
						itemy += itemHeight;
						i = (User) sl.nextElement();

						if (y < itemy) {
							if (x > projNameX - 1 && x < projNameX + i.width)
								select = i;
							done = true;
						}
					}
				}

				itemy += itemHeight;
			}

			return select;
		}
		public void mousePressed(MouseEvent e) {
			if (! isEnabled()) return;

			boolean needPaint = false;
			int x = e.getX();
			int y = e.getY();

			if (selected != null) {
				selected.selected = false;
				selected = null;
				needPaint = true;
			}

			if (x > nameX && x < nameX + itemWidth && y > 9 && y < panelHeight - 10) {
				if ((selected = mapToInvite(x,y)) != null) {
					selected.selected = true;
					needPaint = true;
				}
			}

			if (needPaint)
				repaint();
		}
		public void mouseClicked(MouseEvent e) {
			if (! isEnabled()) return;

			int x = e.getX();
			int y = e.getY();
			boolean showPopup = (e.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0;

			if (! showPopup) {
				if (e.getClickCount() == 2 &&
					selected != null &&
					(! (selected.isOwner && dispatcher.isOwner)) &&
					selected instanceof User &&
					((User) selected).present &&
					(! limitProjChat || mainUser || ! ((User) selected).isProjUser))
					actionPerformed(new ActionEvent(chatMI,0,null));
			}

			else if (selected == null) {
				if (dispatcher.isOwner)
					invitePU.show(pp,x,y);
			}

			else if (selected instanceof User && ((User) selected).present) {
				if (! (selected.isOwner && dispatcher.isOwner)) {
					if (dispatcher.isOwner)
						if (selected.isProjUser)
							projUserPU.show(pp,x,y);
						else
							userPU.show(pp,x,y);
					// Can only chat with user if we are not limiting chat, or this
					// user is a non project user, or the selected user is a non proj user.
					else if (! limitProjChat || mainUser || ! selected.isProjUser)
						chatPU.show(pp,x,y);
				}
			}
			else if (dispatcher.isOwner)
				dropPU.show(pp,x,y);
		}
	}

	class WindowHandler extends WindowAdapter {
		public void windowClosed(WindowEvent e) {
			MessageWindow w = (MessageWindow) e.getSource();
			mw.remove(new Integer(w.toID));
		}
	}
/**
 * PresencePanel constructor comment.
 */
public PresencePanel() {
	super();

	mt = new MediaTracker(this);
	ownerImage = loadImage("owner.gif");
	controlImage = loadImage("baton.gif");

	mh.pp = this;
	addMouseListener(mh);
	createMenus();
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e ActionEvent
 */
public void actionPerformed(ActionEvent e) {
	if (e.getSource() == chatMI || e.getSource() == userChatMI || e.getSource() == projUserChatMI) {
		User u = (User) mh.selected;
		Integer pID = new Integer(u.participantID);
		MessageWindow w = (MessageWindow) mw.get(pID);

		// We are not currently chatting with this user.
		if (w == null) {
			w = MessageWindow.startChat(dispatcher.particpantID,
										u.participantID,
										getUserName(dispatcher.particpantID),
										new String(u.name),
										dispatcher,
										null);
			mw.put(pID,w);
			w.addWindowListener(wh);
		}

		// Bring new or existing chat window to front.
		w.toFront();
	}
	else if (e.getSource() == dropMI || e.getSource() == userDropMI) {
		DSMPProto p;
		if (mh.selected instanceof Project) {
			p = DSMPGenerator.dropInvitee((byte) 0,dispatcher.meetingID,mh.selected.inviteID,-1,true,false);
		}
		else {
			User u = (User) mh.selected;
			p = DSMPGenerator.dropInvitee((byte) 0,dispatcher.meetingID,u.inviteID,u.participantID,true,u.present);
		}
		dispatcher.dispatchProtocol(p);
	}
	else if (e.getSource() == userRemoveMI || e.getSource() == projUserRemoveMI) {
		User u = (User) mh.selected;
		DSMPProto p = DSMPGenerator.dropInvitee((byte) 0,dispatcher.meetingID,u.inviteID,u.participantID,false,true);
		dispatcher.dispatchProtocol(p);
	}
	else if (e.getSource() == inviteMI) {
		dispatcher.fireMeetingEvent(new MeetingEvent(MeetingEvent.INVITE_ACTION,(byte) 0,(byte) 0));
	}
	else
		System.out.println("Unknown action event source: " + e.getSource());
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2002 1:40:35 PM)
 * @param group java.lang.String
 */
public void addProject(char[] p, int inviteID) {
	// Look up this project name.
	char[] project = new char[PROJECT.length + p.length];
	System.arraycopy(PROJECT,0,project,0,PROJECT.length);
	System.arraycopy(p,0,project,PROJECT.length,p.length);
	int pIndex = search(ul,project);

	// Did not find it?
	if (pIndex < 0) {
		// Create the invitation and add it to the 1st level
		// list and the invitation index.
		Project np = new Project(project,inviteID);
		ul.insertElementAt(np,-pIndex-1);
		ikul.put(new Integer(inviteID),np);

		// Update the container width and height.
		if ((np.width = getTextWidth(project)) > itemWidth)
			itemWidth = np.width;
		panelHeight += itemHeight;
		setPanelSize();

		repaint();
	}

	// Found the project.
	else {
		// We allow this addition only if the existing project
		// was previously uninvited (owner changed mind).
		Project np = (Project) ul.elementAt(pIndex);
		if (np.inviteID == -1) {
			np.inviteID = inviteID;
			repaint();
		}
		else
			System.out.println("PresencePanel.addProject: received duplicate project " + p);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2002 1:40:03 PM)
 * @param user java.lang.String
 * @param group java.lang.String
 * @param presence boolean
 */
public void addUser(char[] name, int inviteID) {
	// Look up this user name.
	int uIndex = search(ul,name);

	// Did not find it?
	if (uIndex < 0) {
		// Create the invitation and add it to the 1st level
		// list and the invitation index.
		User user = new User(name,inviteID,-1,false);
		ul.insertElementAt(user,-uIndex-1);
		ikul.put(new Integer(inviteID),user);

		// Update the container width and height.
		if ((user.width = getTextWidth(name)) > itemWidth)
			itemWidth = user.width;
		panelHeight += itemHeight;
		setPanelSize();

		repaint();
	}
	else
		System.out.println("PresencePanel.addUser: received duplicate user " + name);
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2002 1:15:43 PM)
 * @return int
 * @param a char[]
 * @param b char[]
 */
private int compare(char[] a, char[] b) {
	int c = 0;
	int n = Math.min(a.length, b.length);
	int j = 0;

	while (n-- != 0) {
	    if (a[j] != b[j])
			c = a[j] - b[j];
	    else
	    	j++;
	}

	if (c == 0)
		c = a.length - b.length;

	return c;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2002 1:15:43 PM)
 * @return int
 * @param a char[]
 * @param b char[]
 */
private int compare(char[] a, char[] b, int lenA, int lenB) {
	int c = 0;
	int n = Math.min(lenA, lenB);
	int j = 0;

	while (n-- != 0) {
	    if (a[j] != b[j])
			c = a[j] - b[j];
	    else
	    	j++;
	}

	if (c == 0)
		c = lenA - lenB;

	return c;
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2002 3:28:21 PM)
 */
public void createMenus() {
	// Item pop-up for meeting participant.
	chatPU = new PopupMenu();
	chatMI = new MenuItem("Send message");
	chatPU.add(chatMI);
	chatMI.addActionListener(this);

	// Non-item pop-up for meeting owner.
	invitePU = new PopupMenu();
	inviteMI = new MenuItem("Invite...");
	invitePU.add(inviteMI);
	inviteMI.addActionListener(this);

	// Project item pop-up for meeting owner.
	dropPU = new PopupMenu();
	dropMI = new MenuItem("Drop invite");
	dropPU.add(dropMI);
	dropMI.addActionListener(this);

	// User item pop-up for meeting owner.
	userPU = new PopupMenu();
	userChatMI = new MenuItem("Send message");
	userRemoveMI = new MenuItem("Remove");
	userDropMI = new MenuItem("Drop invite");
	userPU.add(userChatMI);
	userPU.add(userRemoveMI);
	userPU.add(userDropMI);
	userChatMI.addActionListener(this);
	userRemoveMI.addActionListener(this);
	userDropMI.addActionListener(this);

	// Project user item pop-up for meeting owner.
	projUserPU = new PopupMenu();
	projUserChatMI = new MenuItem("Send message");
	projUserRemoveMI = new MenuItem("Remove");
	projUserPU.add(projUserChatMI);
	projUserPU.add(projUserRemoveMI);
	projUserChatMI.addActionListener(this);
	projUserRemoveMI.addActionListener(this);

	add(chatPU);
	add(invitePU);
	add(dropPU);
	add(userPU);
	add(projUserPU);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 10:32:25 AM)
 * @return java.lang.String
 * @param partID int
 */
public String getNickName(int partID) {
	User user = (User) pkul.get(new Integer(partID));

	if (user == null)
		return null;

	return new String(user.name,0,user.nickLength);
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
 * Insert the method's description here.
 * Creation date: (7/27/2002 10:50:07 AM)
 * @return int
 * @param text java.lang.String
 */
public int getTextWidth(char[] text) {
	if (fm == null) {
		if (getFont() == null) {
			System.out.println("getFont() returned null");
			return 0;
		}
		else
			fm = getFontMetrics(getFont());

		if (fm == null) {
			System.out.println("getFontMetrics() returned null");
			return 0;
		}
		else
			itemHeight = fm.getHeight() + 2;
	}

	return fm.charsWidth(text,0,text.length);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 10:32:25 AM)
 * @return java.lang.String
 * @param partID int
 */
public String getUserName(int partID) {
	User user = (User) pkul.get(new Integer(partID));

	if (user == null)
		return null;

	return new String(user.name);
}
/**
 * Insert the method's description here.
 * Creation date: (8/12/2002 2:12:44 PM)
 * @param image java.io.File
 */
private Image loadImage(String file) {
	Image i = null;

	try {
		InputStream is = getClass().getResourceAsStream(file);
		byte[] buffer = new byte[2048];
		int len = 0;
		int n;
		while ((n = is.read(buffer,len,2048-len)) != -1)
			len += n;
		i = Toolkit.getDefaultToolkit().createImage(buffer,0,len);
		mt.addImage(i,0);
		mt.waitForAll();
	}
	catch (Exception e) {
		System.out.println("Image load of " + file + " failed.");
	}

	return i;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:56:37 AM)
 * @param e MeetingEvent
 */
public void meetingAction(MeetingEvent e) {
	if (e.isControl()) {
		inControl = (User) pkul.get(new Integer(e.participantID));
		repaint();
	}
	else if (limitProjChat && e.isJoin()) {
		Enumeration invites = ul.elements();
		while (invites.hasMoreElements()) {
			Invite i = (Invite) invites.nextElement();
			if (i instanceof User) {
				User u = (User) i;
				if (u.participantID == e.participantID)
					mainUser = true;
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:56:37 AM)
 * @param e MessageEvent
 */
public void message(MessageEvent e) {
	if (e.isUnicast()) {
		Integer fID = new Integer(e.fromID);
		MessageWindow w = (MessageWindow) mw.get(fID);

		if (w == null) {
			w = MessageWindow.startChat(dispatcher.particpantID,
										e.fromID,
										getUserName(dispatcher.particpantID),
										getUserName(e.fromID),
										dispatcher,
										e.message);
			mw.put(fID,w);
			w.addWindowListener(wh);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2002 12:33:37 PM)
 * @return boolean
 */
private boolean nickCollision(User u) {
	Enumeration ml = ul.elements();
	while (ml.hasMoreElements()) {
		Invite i = (Invite) ml.nextElement();

		if (i instanceof User) {
			User u1 = (User) i;
			if (u1 != u && compare(u.name, u1.name, u.nickLength, u1.nickLength) == 0)
				return true;
		}
		else {
			Enumeration sl = ((Project) i).ul.elements();
			while (sl.hasMoreElements()) {
				User u1 = (User) sl.nextElement();
				if (u1 != u && compare(u.name, u1.name, u.nickLength, u1.nickLength) == 0)
					return true;
			}
		}
	}

	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/2002 12:48:36 PM)
 * @param g java.awt.Graphics
 */
public void paint(Graphics g) {
	Dimension d = getSize();
	g.clearRect(0,0,d.width,d.height);

	FontMetrics fm = g.getFontMetrics();

	int fh = fm.getHeight();
	int fd = fm.getDescent();
	int y = 10 + fh - fd;
	fh += 2;

	Enumeration ml = ul.elements();
	while (ml.hasMoreElements()) {
		Invite i = (Invite) ml.nextElement();

		if (i instanceof User) {
			if (i.isOwner) {
				if (mt.checkAll())
					g.drawImage(ownerImage,iconX,y+fd-fh+4,null);
				else {
					g.setColor(Color.black);
					g.fillRect(iconX,y+fd-fh+4,10,10);
				}
			}
			else if (i == inControl) {
				if (mt.checkAll())
					g.drawImage(controlImage,iconX,y+fd-fh+4,null);
				else {
					g.setColor(Color.green);
					g.fillRect(iconX,y+fd-fh+4,10,10);
				}
			}
		}

		if (i instanceof User && ((User) i).present)
			g.setColor(present);
		else if (i.inviteID == -1)
			g.setColor(invalid);
		else
			g.setColor(absent);

		if (i.selected) {
			g.fillRect(nameX-1,y+fd-fh,i.width+2,fh);
			g.setColor(getBackground());
		}

		g.drawChars(i.name,0,i.name.length,nameX,y);

		y += fh;

		if (i instanceof Project) {
			Enumeration sl = ((Project) i).ul.elements();
			while (sl.hasMoreElements()) {
				User u = (User) sl.nextElement();

				if (u == inControl) {
					if (mt.checkAll())
						g.drawImage(controlImage,projIconX,y+fd-fh+4,null);
					else {
						g.setColor(Color.green);
						g.fillRect(projIconX,y+fd-fh+4,10,10);
					}
				}

				if (u.present)
					if (limitProjChat && ! mainUser)
						g.setColor(invalid);
					else
						g.setColor(present);
				else
					g.setColor(absent);

				if (u.selected) {
					g.fillRect(projNameX-1,y-fh+fd,u.width-13,fh);
					g.setColor(getBackground());
				}

				g.drawChars(u.name,0,u.name.length,projNameX,y);

				y += fh;
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2002 3:28:21 PM)
 * @return java.awt.Dimension
 */
public Dimension preferredSize() {
	return new Dimension(itemWidth + 20,panelHeight);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e PresenceEvent
 */
public void presenceChanged(PresenceEvent e) {
	//System.out.println(e);
	if (e.isProjectInvite())
		addProject(e.name,e.inviteID);
	else if (e.isUserInvite())
		addUser(e.name,e.inviteID);
	else if (e.isArrival())
		userArrived(e.participantID,e.inviteID,e.name,e.isOwner());
	else if (e.isInviteDeparture())
		removeInvite(e.inviteID);
	else if (e.isUserDeparture())
		userDeparted(e.participantID,e.inviteID);
	else {
		System.out.println("PresencePanel.presenceChanged: drop event is neither user or invite");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/2002 4:42:35 PM)
 */
public void recomputeWidth() {
	itemWidth = 0;
	Enumeration ml = ul.elements();
	while (ml.hasMoreElements()) {
		Invite i = (Invite) ml.nextElement();

		if (i.width > itemWidth) itemWidth = i.width;

		if (i instanceof Project) {
			Enumeration sl = ((Project) i).ul.elements();
			while (sl.hasMoreElements()) {
				User u = (User) sl.nextElement();
				if (u.width > itemWidth) itemWidth = u.width;
			}
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
		System.out.println("PresencePanel.removeInvite: invitation ID " + inviteID + " not found.");
		return;
	}

	if (invite instanceof User || ((Project) invite).ul.size() == 0) {
		int i = search(ul,invite.name);
		ul.removeElementAt(i);

		if (invite instanceof User) {
			User user = (User) invite;
			if (user.present)
				pkul.remove(new Integer(user.participantID));
		}

		if (invite.width == itemWidth)
			recomputeWidth();

		panelHeight -= itemHeight;

		setPanelSize();
	}

	// Invitation is a project invitation with invited users present.
	// Just mark the invitation invalid.
	else
		invite.inviteID = -1;

	repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2002 1:40:03 PM)
 * @param user java.lang.String
 * @param group java.lang.String
 * @param presence boolean
 */
private int search(Vector il, char[] name) {
	if (il == null)
		return -1;

	int l = 0;
	int h = il.size() - 1;

	while (l <= h) {
		int t = (l + h)/2;
		Invite i = (Invite) il.elementAt(t);

		int c = compare(i.name,name);

		if (c < 0)
			l = t + 1;
		else if (c > 0)
			h = t - 1;
		else
			return t;
	}

	return -l - 1;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 * @param d DSMPDispatcher
 */
public void setDispatcher(DSMPDispatcher d) {
	if (dispatcher == d)
		return;

	if (dispatcher != null) {
		dispatcher.removePresenceListener(this);
		dispatcher.removeMessageListener(this);
		dispatcher.removeMeetingListener(this);
		ul.removeAllElements();
		ikul.clear();
		pkul.clear();
		itemWidth = 0;
		panelHeight = 20;
		inControl = null;
		Enumeration e = mw.elements();
		while (e.hasMoreElements()) {
			MessageWindow w = (MessageWindow) e.nextElement();
			w.removeWindowListener(wh);
			w.dispose();
		}
		mw.clear();
		repaint();
	}

	dispatcher = d;

	if (dispatcher != null) {
		dispatcher.addPresenceListener(this);
		dispatcher.addMessageListener(this);
		dispatcher.addMeetingListener(this);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/29/2002 9:34:46 AM)
 * @param width int
 * @param height int
 */
public void setPanelSize() {
	Dimension d;

	Container p = getParent();
	while (p != null && ! (p instanceof ScrollPane))
		p = p.getParent();

	if (p != null)
		d = p.getSize();
	else
		d = getSize();

	// Width is increased by 40 to include 10 pixel border and moderator/baton icon.
	super.setSize(Math.max(itemWidth+40,d.width),Math.max(panelHeight,d.height));
	if (p != null) p.doLayout();
}
/**
 * Insert the method's description here.
 * Creation date: (7/29/2002 9:34:46 AM)
 * @param width int
 * @param height int
 */
public void setSize(int width, int height) {
	super.setSize(Math.max(width,itemWidth + 20),Math.max(height,panelHeight));
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2002 9:06:54 AM)
 * @param partID java.lang.Integer
 * @param online boolean
 */
public void updateMsgWndws(Integer partID, boolean online) {
	MessageWindow w = (MessageWindow) mw.get(partID);
	if (w != null)
		w.partnerStatus(online);
}
/**
 */
public void userArrived(int partID, int inviteID, char[] name, boolean isOwner) {
	// Locate the invitation.
	Invite invite = (Invite) ikul.get(new Integer(inviteID));

	// If the invite is a project, then the participant is a User under
	// the project.
	if (invite instanceof Project) {
		Project p = (Project) invite;
		int uIndex = search(p.ul,name);

		// Should never find this user in the project list. Should only ever
		// receive 1 event for a user's arrival (in a project) as departing users
		// are deleted.
		if (uIndex < 0) {
			User user = new User(name,inviteID,partID,true);
			user.isProjUser = true;
			p.ul.insertElementAt(user,-uIndex-1);
			Integer PartID = new Integer(partID);
			pkul.put(PartID,user);

			// Update container height and width.
			if ((user.width = getTextWidth(name) + 15) > itemWidth)
				itemWidth = user.width;
			panelHeight += itemHeight;
			setPanelSize();

			user.nickLength = name.length;
			if (name.length > 8) {
				user.nickLength = 8;
				while (nickCollision(user))
					user.nickLength++;
			}

			updateMsgWndws(PartID,true);

			repaint();
		}
		else {
			User user = (User) p.ul.elementAt(uIndex);
			if (user.present) {
				System.out.println("PresencePanel.userArrived: received duplicate for project user " + user.participantID);
				return;
			}
			else {
				System.out.println("PresencePanel.userArrived: project user " + user.participantID + " was not present.");
				user.present = true;
				user.participantID = partID;
				Integer PartID = new Integer(partID);
				pkul.put(PartID,user);
				if (partID == dispatcher.particpantID)
					mainUser = true;
				updateMsgWndws(PartID,true);

				user.nickLength = name.length;
				if (name.length > 8) {
					user.nickLength = 8;
					while (nickCollision(user))
						user.nickLength++;
				}

				repaint();
			}
		}
	}
	// invite references a user block.
	else {
		User user = (User) invite;
		if (user.present) {
			System.out.println("PresencePanel.userArrived: received duplicate for user " + user.participantID);
			return;
		}
		else {
			user.present = true;
			user.participantID = partID;
			user.isOwner = isOwner;

			Integer PartID = new Integer(partID);
			pkul.put(PartID,user);
			updateMsgWndws(PartID,true);

			user.nickLength = name.length;
			if (name.length > 8) {
				user.nickLength = 8;
				while (nickCollision(user))
					user.nickLength++;
			}

			repaint();
		}
	}

	dispatcher.fireArriveMessage(new String(name));
	dispatcher.fireImageEvent(new ImageEvent(ImageEvent.FULL_IMAGE,(byte) 0,(byte) 0));
}
/**
 */
public void userDeparted(int partID, int inviteID) {
	// Locate the participant and the invitation.
	Integer PartID = new Integer(partID);
	Integer InviteID = new Integer(inviteID);
	User user = (User) pkul.get(PartID);
	Invite invite = (Invite) ikul.get(InviteID);

	// If the invitation is a Project, then the participant is a User
	// under the project.
	if (invite != null && invite instanceof Project) {
		userDepartedProject(user,PartID,(Project) invite,InviteID);
	}

	// Found user, lost invite. Project was dropped.
	else if (user != null && invite == null) {
		// Find the project that has this user.
		boolean done = false;
		for (int i = 0; i < ul.size() && ! done; i++) {
			invite = (Invite) ul.elementAt(i);
			if (invite instanceof Project && ((Project) invite).ul.contains(user)) {
				userDepartedProject(user,PartID,(Project) invite,InviteID);
				done = true;
			}
		}
		if (! done) {
			System.out.println("PresencePanel.userDeparted: project user is lost.");
			return;
		}
	}

	// participant and invite reference the same User object.
	else if (user == invite) {
		user.present = false;
		user.participantID = -1;
		pkul.remove(PartID);
		updateMsgWndws(PartID,false);
		repaint();
	}

	// Yuk! invite and participant IDs reference 2 different user objects.
	else {
		System.out.println("PresencePanel.userDeparted: invite and participant IDs mismatch.");
		return;
	}

	if (partID == dispatcher.particpantID)
		dispatcher.fireForcedLeaveMeeting();
	else
		dispatcher.fireDepartMessage(new String(user.name));
}
/**
 */
public void userDepartedProject(User user, Integer PartID, Project p, Integer InviteID) {
	// Locate the user under the project.
	int uIndex = search(p.ul,user.name);

	// Found the user?
	if (uIndex >= 0)
		// And it matches the one index by participant ID?
		if (p.ul.elementAt(uIndex) == user) {
			// Remove the user from the project.
			p.ul.removeElementAt(uIndex);
			pkul.remove(PartID);
			panelHeight -= itemHeight;

			// Does this require a new width?
			boolean redoWidth = false;
			if (user.width == itemWidth) redoWidth = true;

			// Is this the last user in an uninvited project?
			if (p.ul.size() == 0 && p.inviteID == -1) {
				// Remove the dead invitation too.
				int pIndex = search(ul,p.name);
				if (pIndex >= 0)
					ul.removeElementAt(pIndex);
				ikul.remove(InviteID);
				if (p.width == itemWidth) redoWidth = true;
				panelHeight -= itemHeight;
			}

			// Recompute the correct size and repaint.
			if (redoWidth) recomputeWidth();
			setPanelSize();

			updateMsgWndws(PartID,false);

			repaint();
		}
		else
			System.out.println("PresencePanel.userDepartedProject: project invite and participant IDs crossed.");
	else
		System.out.println("PresencePanel.userDepartedProject: project invite and participant IDs mismatch.");
}
}
