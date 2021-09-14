package oem.edge.ed.odc.meeting.client;

import oem.edge.ed.odc.dsmp.client.*;
import oem.edge.ed.odc.meeting.common.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (3/3/2003 11:23:24 AM)
 * @author: Mike Zarnick
 */
public class PresencePanel extends JPanel implements DocumentListener, MeetingListener, MessageListener, PresenceListener {

	class MouseHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
				JTree tree = (JTree) e.getSource();
				TreePath tp = tree.getPathForLocation(e.getX(),e.getY());
				if (tp != null) {
					Invite i = (Invite) tp.getLastPathComponent();
					if (i instanceof UserInvite) {
						UserInvite ui = (UserInvite) i;
						if (ui.present &&
							ui.participantID != dispatcher.particpantID &&
							(dispatcher.isOwner || ui.isOwner)) {
							sendMsg();
						}
					}
				}
			}
			else if (SwingUtilities.isRightMouseButton(e)) {
				JTree tree = (JTree) e.getSource();
				TreePath tp = tree.getPathForLocation(e.getX(),e.getY());
				if (tp != null) {
					tree.setSelectionPath(tp);
					Invite i = (Invite) tp.getLastPathComponent();
					if (i instanceof UserInvite && ((UserInvite) i).present) {
						UserInvite ui = (UserInvite) i;
						if (ui.participantID != dispatcher.particpantID) {
							if (dispatcher.isOwner) {
								if (ui.isProjUser) {
									getProjUserPU().removeAll();
									getProjUserPU().add(getProjUserChatMI());
									getProjUserPU().add(getProjUserRemoveMI());
									getProjUserPU().add(ui.isOwner ? getProjUserRemoveOwnerMI() : getProjUserGrantOwnerMI());
									getProjUserPU().add(ui.isModerator ? getProjUserGrabModMI() : getProjUserGrantModMI());
									getProjUserPU().show(tree,e.getX(),e.getY());
								}
								else {
									getUserPU().removeAll();
									getUserPU().add(getUserChatMI());
									getUserPU().add(getUserRemoveMI());
									getUserPU().add(getUserDropMI());
									getUserPU().add(ui.isOwner ? getUserRemoveOwnerMI() : getUserGrantOwnerMI());
									getUserPU().add(ui.isModerator ? getUserGrabModMI() : getUserGrantModMI());
									getUserPU().show(tree,e.getX(),e.getY());
								}
							}
							else {
								getChatPU().show(tree,e.getX(),e.getY());
							}
						}
						else if (dispatcher.isOwner && ! ui.isModerator) {
							getUserPU().removeAll();
							getUserPU().add(getUserGrabModMI());
							getUserPU().show(tree,e.getX(),e.getY());
						}
					}
					else if (dispatcher.isOwner) {
						getDropPU().show(tree,e.getX(),e.getY());
					}
				}
				else {
					tree.clearSelection();
					if (dispatcher.isOwner) {
						getGrabModMI().setEnabled(! dispatcher.isModerator);
						getInvitePU().show(tree,e.getX(),e.getY());
					}
				}
			}
		}
	}

	class WindowHandler extends WindowAdapter {
		public void windowClosed(WindowEvent e) {
			MessageWindow w = (MessageWindow) e.getSource();
			mw.remove(new Integer(w.toID));
		}
	}
	private boolean limitChatter = false;
	private PresenceCellRenderer renderer = null;
	private MenuChangeListener listener = null;
	private DSMPDispatcher dispatcher = null;
	private Hashtable mw = new Hashtable();
	private WindowHandler wh = new WindowHandler();
	private JButton ivjInviteBtn = null;
	private JTextField ivjInviteTF = null;
	private JScrollPane ivjPresenceSP = null;
	private JTree ivjPresenceTree = null;
	private PresenceTM ivjPresenceTM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="190,115"
	private JMenuItem ivjChatMI = null;
	private JPopupMenu ivjChatPU = null;  //  @jve:visual-info  decl-index=0 visual-constraint="215,366"
	private JMenuItem ivjDropMI = null;
	private JPopupMenu ivjDropPU = null;  //  @jve:visual-info  decl-index=0 visual-constraint="213,301"
	private JMenuItem ivjInviteMI = null;
	private JPopupMenu ivjInvitePU = null;  //  @jve:visual-info  decl-index=0 visual-constraint="218,429"
	private JMenuItem ivjProjUserChatMI = null;
	private JPopupMenu ivjProjUserPU = null;  //  @jve:visual-info  decl-index=0 visual-constraint="207,238"
	private JMenuItem ivjProjUserRemoveMI = null;
	private JMenuItem ivjUserChatMI = null;
	private JMenuItem ivjUserDropMI = null;
	private JPopupMenu ivjUserPU = null;  //  @jve:visual-info  decl-index=0 visual-constraint="208,175"
	private JMenuItem ivjUserRemoveMI = null;
	private BorderFactory ivjBorderFactory = null;  // @jve:visual-info  decl-index=0 visual-constraint="59,488"
	private boolean ivjConnPtoP2Aligning = false;
	private TreeSelectionModel ivjTreeSelectionModel = null;  // @jve:visual-info  decl-index=0 visual-constraint="215,305"
	private JMenuItem ivjMainChatMI = null;
	private JMenuItem ivjMainDropMI = null;
	private JMenuItem ivjMainRemoveMI = null;
	private JMenu ivjMainUserM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="29,478"
	private JMenuItem ivjGrabModMI = null;
	private JMenuItem ivjMainGrabModMI = null;
	private JMenuItem ivjMainGrantModMI = null;
	private JMenuItem ivjMainGrantOwnerMI = null;
	private JMenuItem ivjMainRemoveOwnerMI = null;
	private JMenuItem ivjProjUserGrabModMI = null;
	private JMenuItem ivjProjUserGrantModMI = null;
	private JMenuItem ivjProjUserGrantOwnerMI = null;
	private JMenuItem ivjProjUserRemoveOwnerMI = null;
	private JMenuItem ivjUserGrabModMI = null;
	private JMenuItem ivjUserGrantModMI = null;
	private JMenuItem ivjUserGrantOwnerMI = null;
	private JMenuItem ivjUserRemoveOwnerMI = null;
	private TreeSelectionListener ivjConnEtoC12SourceListener;
/**
 * PresencePanel2 constructor comment.
 */
public PresencePanel() {
	super();
	initialize();
}
/**
 * PresencePanel2 constructor comment.
 * @param layout java.awt.LayoutManager
 */
public PresencePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * PresencePanel2 constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public PresencePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * PresencePanel2 constructor comment.
 * @param isDoubleBuffered boolean
 */
public PresencePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:57:22 AM)
 * @param l MenuChangeListener
 */
public synchronized void addMenuChangeListener(MenuChangeListener l) {
	if (l == null)
		return;

	listener = DSMPEventMulticaster.addMenuChangeListener(listener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2003 3:56:28 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void changedUpdate(DocumentEvent e) {
	textChgInvite();
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e ActionEvent
 */
public void dropInvite() {
	Object selected = getPresenceTree().getSelectionPath().getLastPathComponent();

	DSMPProto p;
	if (selected instanceof ProjectInvite) {
		ProjectInvite proj = (ProjectInvite) selected;
		p = DSMPGenerator.dropInvitee((byte) 0,dispatcher.meetingID,proj.inviteID,-1,true,false);
	}
	else if (selected instanceof GroupInvite) {
		GroupInvite group = (GroupInvite) selected;
		p = DSMPGenerator.dropInvitee((byte) 0,dispatcher.meetingID,group.inviteID,-1,true,false);
	}
	else {
		UserInvite u = (UserInvite) selected;
		p = DSMPGenerator.dropInvitee((byte) 0,dispatcher.meetingID,u.inviteID,u.participantID,true,u.present);
	}
	dispatcher.dispatchProtocol(p);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e ActionEvent
 */
public void fireInvite() {
	dispatcher.fireMeetingEvent(new MeetingEvent(MeetingEvent.INVITE_ACTION,(byte) 0,(byte) 0));
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e MenuChangeEvent
 */
public void fireMenuChangeEvent(MenuChangeEvent e) {
	if (listener != null)
		listener.menuChange(e);
}
/**
 * Return the BorderFactory property value.
 * @return javax.swing.BorderFactory
 */
private javax.swing.BorderFactory getBorderFactory() {
	return ivjBorderFactory;
}
/**
 * Return the ChatMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getChatMI() {
	if (ivjChatMI == null) {
		try {
			ivjChatMI = new javax.swing.JMenuItem();
			ivjChatMI.setName("ChatMI");
			ivjChatMI.setText("Send message");
			ivjChatMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						sendMsg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjChatMI;
}
/**
 * Return the ChatPU property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getChatPU() {
	if (ivjChatPU == null) {
		try {
			ivjChatPU = new javax.swing.JPopupMenu();
			ivjChatPU.setName("ChatPU");
			ivjChatPU.add(getChatMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjChatPU;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2003 9:04:09 AM)
 * @return oem.edge.ed.odc.meeting.client2.PresenceTM
 */
public PresenceTM getDataModel() {
	return getPresenceTM();
}
/**
 * Return the DropMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getDropMI() {
	if (ivjDropMI == null) {
		try {
			ivjDropMI = new javax.swing.JMenuItem();
			ivjDropMI.setName("DropMI");
			ivjDropMI.setText("Drop invite");
			ivjDropMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						dropInvite();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDropMI;
}
/**
 * Return the DropPU property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getDropPU() {
	if (ivjDropPU == null) {
		try {
			ivjDropPU = new javax.swing.JPopupMenu();
			ivjDropPU.setName("DropPU");
			ivjDropPU.add(getDropMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDropPU;
}
/**
 * Return the GrabModMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getGrabModMI() {
	if (ivjGrabModMI == null) {
		try {
			ivjGrabModMI = new javax.swing.JMenuItem();
			ivjGrabModMI.setName("GrabModMI");
			ivjGrabModMI.setText("Grab moderator");
			ivjGrabModMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						grabModerator();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrabModMI;
}
/**
 * Return the InviteBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInviteBtn() {
	if (ivjInviteBtn == null) {
		try {
			ivjInviteBtn = new javax.swing.JButton();
			ivjInviteBtn.setName("InviteBtn");
			ivjInviteBtn.setText("Invite");
			ivjInviteBtn.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjInviteBtn.setEnabled(true);
			ivjInviteBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						sendInvite();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInviteBtn;
}
/**
 * Return the InviteMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getInviteMI() {
	if (ivjInviteMI == null) {
		try {
			ivjInviteMI = new javax.swing.JMenuItem();
			ivjInviteMI.setName("InviteMI");
			ivjInviteMI.setText("Invite...");
			ivjInviteMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						fireInvite();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInviteMI;
}
/**
 * Return the InvitePU property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getInvitePU() {
	if (ivjInvitePU == null) {
		try {
			ivjInvitePU = new javax.swing.JPopupMenu();
			ivjInvitePU.setName("InvitePU");
			ivjInvitePU.add(getInviteMI());
			ivjInvitePU.add(getGrabModMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInvitePU;
}
/**
 * Return the InviteTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getInviteTF() {
	if (ivjInviteTF == null) {
		try {
			ivjInviteTF = new javax.swing.JTextField();
			ivjInviteTF.setName("InviteTF");
			ivjInviteTF.setEnabled(true);
			ivjInviteTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						sendInvite();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInviteTF;
}
/**
 * Return the MainChatMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getMainChatMI() {
	if (ivjMainChatMI == null) {
		try {
			ivjMainChatMI = new javax.swing.JMenuItem();
			ivjMainChatMI.setName("MainChatMI");
			ivjMainChatMI.setText("Send message");
			ivjMainChatMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						sendMsg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMainChatMI;
}
/**
 * Return the MainDropMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getMainDropMI() {
	if (ivjMainDropMI == null) {
		try {
			ivjMainDropMI = new javax.swing.JMenuItem();
			ivjMainDropMI.setName("MainDropMI");
			ivjMainDropMI.setText("Drop invite");
			ivjMainDropMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						dropInvite();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMainDropMI;
}
/**
 * Return the MainGrabModMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getMainGrabModMI() {
	if (ivjMainGrabModMI == null) {
		try {
			ivjMainGrabModMI = new javax.swing.JMenuItem();
			ivjMainGrabModMI.setName("MainGrabModMI");
			ivjMainGrabModMI.setText("Grab moderator");
			ivjMainGrabModMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						grabModerator();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMainGrabModMI;
}
/**
 * Return the MainGrantModMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getMainGrantModMI() {
	if (ivjMainGrantModMI == null) {
		try {
			ivjMainGrantModMI = new javax.swing.JMenuItem();
			ivjMainGrantModMI.setName("MainGrantModMI");
			ivjMainGrantModMI.setText("Grant moderator");
			ivjMainGrantModMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						grantModerator();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMainGrantModMI;
}
/**
 * Return the MainGrantOwnerMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getMainGrantOwnerMI() {
	if (ivjMainGrantOwnerMI == null) {
		try {
			ivjMainGrantOwnerMI = new javax.swing.JMenuItem();
			ivjMainGrantOwnerMI.setName("MainGrantOwnerMI");
			ivjMainGrantOwnerMI.setText("Grant ownership");
			ivjMainGrantOwnerMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						grantOwner();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMainGrantOwnerMI;
}
/**
 * Return the MainRemoveMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getMainRemoveMI() {
	if (ivjMainRemoveMI == null) {
		try {
			ivjMainRemoveMI = new javax.swing.JMenuItem();
			ivjMainRemoveMI.setName("MainRemoveMI");
			ivjMainRemoveMI.setToolTipText("Remove user from meeting");
			ivjMainRemoveMI.setText("Remove");
			ivjMainRemoveMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						removeUser();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMainRemoveMI;
}
/**
 * Return the MainRemoveOwnerMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getMainRemoveOwnerMI() {
	if (ivjMainRemoveOwnerMI == null) {
		try {
			ivjMainRemoveOwnerMI = new javax.swing.JMenuItem();
			ivjMainRemoveOwnerMI.setName("MainRemoveOwnerMI");
			ivjMainRemoveOwnerMI.setText("Remove ownership");
			ivjMainRemoveOwnerMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						removeOwner();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMainRemoveOwnerMI;
}
/**
 * Return the MainUserM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getMainUserM() {
	if (ivjMainUserM == null) {
		try {
			ivjMainUserM = new javax.swing.JMenu();
			ivjMainUserM.setName("MainUserM");
			ivjMainUserM.setText("Selected");
			ivjMainUserM.add(getMainChatMI());
			ivjMainUserM.add(getMainRemoveMI());
			ivjMainUserM.add(getMainDropMI());
			ivjMainUserM.add(getMainGrantOwnerMI());
			ivjMainUserM.add(getMainRemoveOwnerMI());
			ivjMainUserM.add(getMainGrantModMI());
			ivjMainUserM.add(getMainGrabModMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMainUserM;
}
/**
 * Return the PresenceSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getPresenceSP() {
	if (ivjPresenceSP == null) {
		try {
			ivjPresenceSP = new javax.swing.JScrollPane();
			ivjPresenceSP.setName("PresenceSP");
			getPresenceSP().setViewportView(getPresenceTree());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPresenceSP;
}
/**
 * Return the PresenceTM property value.
 * @return oem.edge.ed.odc.meeting.client2.PresenceTM
 */
private PresenceTM getPresenceTM() {
	if (ivjPresenceTM == null) {
		try {
			ivjPresenceTM = new oem.edge.ed.odc.meeting.client.PresenceTM();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPresenceTM;
}
/**
 * Return the PresenceTree property value.
 * @return javax.swing.JTree
 */
private javax.swing.JTree getPresenceTree() {
	if (ivjPresenceTree == null) {
		try {
			ivjPresenceTree = new javax.swing.JTree();
			ivjPresenceTree.setName("PresenceTree");
			ivjPresenceTree.setShowsRootHandles(true);
			ivjPresenceTree.setBounds(0, 0, 78, 72);
			ivjPresenceTree.setRootVisible(false);
			ivjPresenceTree.setEnabled(true);
			ivjPresenceTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() { 
				public void valueChanged(javax.swing.event.TreeSelectionEvent e) {    
					try {
						selectionChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPresenceTree;
}
/**
 * Return the ProjUserChatMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getProjUserChatMI() {
	if (ivjProjUserChatMI == null) {
		try {
			ivjProjUserChatMI = new javax.swing.JMenuItem();
			ivjProjUserChatMI.setName("ProjUserChatMI");
			ivjProjUserChatMI.setText("Send message");
			ivjProjUserChatMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						sendMsg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProjUserChatMI;
}
/**
 * Return the ProjUserGrabModMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getProjUserGrabModMI() {
	if (ivjProjUserGrabModMI == null) {
		try {
			ivjProjUserGrabModMI = new javax.swing.JMenuItem();
			ivjProjUserGrabModMI.setName("ProjUserGrabModMI");
			ivjProjUserGrabModMI.setText("Grab moderator");
			ivjProjUserGrabModMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						grabModerator();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProjUserGrabModMI;
}
/**
 * Return the ProjUserGrantModMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getProjUserGrantModMI() {
	if (ivjProjUserGrantModMI == null) {
		try {
			ivjProjUserGrantModMI = new javax.swing.JMenuItem();
			ivjProjUserGrantModMI.setName("ProjUserGrantModMI");
			ivjProjUserGrantModMI.setText("Grant moderator");
			ivjProjUserGrantModMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						grantModerator();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProjUserGrantModMI;
}
/**
 * Return the ProjUserGrantOwnerMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getProjUserGrantOwnerMI() {
	if (ivjProjUserGrantOwnerMI == null) {
		try {
			ivjProjUserGrantOwnerMI = new javax.swing.JMenuItem();
			ivjProjUserGrantOwnerMI.setName("ProjUserGrantOwnerMI");
			ivjProjUserGrantOwnerMI.setText("Grant ownership");
			ivjProjUserGrantOwnerMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						grantOwner();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProjUserGrantOwnerMI;
}
/**
 * Return the ProjUserPU property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getProjUserPU() {
	if (ivjProjUserPU == null) {
		try {
			ivjProjUserPU = new javax.swing.JPopupMenu();
			ivjProjUserPU.setName("ProjUserPU");
			ivjProjUserPU.add(getProjUserChatMI());
			ivjProjUserPU.add(getProjUserRemoveMI());
			ivjProjUserPU.add(getProjUserGrantOwnerMI());
			ivjProjUserPU.add(getProjUserRemoveOwnerMI());
			ivjProjUserPU.add(getProjUserGrantModMI());
			ivjProjUserPU.add(getProjUserGrabModMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProjUserPU;
}
/**
 * Return the ProjUserRemoveMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getProjUserRemoveMI() {
	if (ivjProjUserRemoveMI == null) {
		try {
			ivjProjUserRemoveMI = new javax.swing.JMenuItem();
			ivjProjUserRemoveMI.setName("ProjUserRemoveMI");
			ivjProjUserRemoveMI.setText("Remove");
			ivjProjUserRemoveMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						removeUser();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProjUserRemoveMI;
}
/**
 * Return the ProjUserRemoveOwnerMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getProjUserRemoveOwnerMI() {
	if (ivjProjUserRemoveOwnerMI == null) {
		try {
			ivjProjUserRemoveOwnerMI = new javax.swing.JMenuItem();
			ivjProjUserRemoveOwnerMI.setName("ProjUserRemoveOwnerMI");
			ivjProjUserRemoveOwnerMI.setText("Remove ownership");
			ivjProjUserRemoveOwnerMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						removeOwner();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProjUserRemoveOwnerMI;
}
/**
 * Return the UserChatMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getUserChatMI() {
	if (ivjUserChatMI == null) {
		try {
			ivjUserChatMI = new javax.swing.JMenuItem();
			ivjUserChatMI.setName("UserChatMI");
			ivjUserChatMI.setText("Send message");
			ivjUserChatMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						sendMsg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserChatMI;
}
/**
 * Return the UserDropMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getUserDropMI() {
	if (ivjUserDropMI == null) {
		try {
			ivjUserDropMI = new javax.swing.JMenuItem();
			ivjUserDropMI.setName("UserDropMI");
			ivjUserDropMI.setText("Drop invite");
			ivjUserDropMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						dropInvite();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserDropMI;
}
/**
 * Return the UserGrabModMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getUserGrabModMI() {
	if (ivjUserGrabModMI == null) {
		try {
			ivjUserGrabModMI = new javax.swing.JMenuItem();
			ivjUserGrabModMI.setName("UserGrabModMI");
			ivjUserGrabModMI.setText("Grab moderator");
			ivjUserGrabModMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						grabModerator();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserGrabModMI;
}
/**
 * Return the UserGrantModMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getUserGrantModMI() {
	if (ivjUserGrantModMI == null) {
		try {
			ivjUserGrantModMI = new javax.swing.JMenuItem();
			ivjUserGrantModMI.setName("UserGrantModMI");
			ivjUserGrantModMI.setText("Grant moderator");
			ivjUserGrantModMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						grantModerator();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserGrantModMI;
}
/**
 * Return the UserGrantOwnerMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getUserGrantOwnerMI() {
	if (ivjUserGrantOwnerMI == null) {
		try {
			ivjUserGrantOwnerMI = new javax.swing.JMenuItem();
			ivjUserGrantOwnerMI.setName("UserGrantOwnerMI");
			ivjUserGrantOwnerMI.setText("Grant ownership");
			ivjUserGrantOwnerMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						grantOwner();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserGrantOwnerMI;
}
/**
 * Return the UserPU property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getUserPU() {
	if (ivjUserPU == null) {
		try {
			ivjUserPU = new javax.swing.JPopupMenu();
			ivjUserPU.setName("UserPU");
			ivjUserPU.add(getUserChatMI());
			ivjUserPU.add(getUserRemoveMI());
			ivjUserPU.add(getUserDropMI());
			ivjUserPU.add(getUserGrantOwnerMI());
			ivjUserPU.add(getUserRemoveOwnerMI());
			ivjUserPU.add(getUserGrantModMI());
			ivjUserPU.add(getUserGrabModMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserPU;
}
/**
 * Return the UserRemoveMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getUserRemoveMI() {
	if (ivjUserRemoveMI == null) {
		try {
			ivjUserRemoveMI = new javax.swing.JMenuItem();
			ivjUserRemoveMI.setName("UserRemoveMI");
			ivjUserRemoveMI.setText("Remove");
			ivjUserRemoveMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						removeUser();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserRemoveMI;
}
/**
 * Return the UserRemoveOwnerMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getUserRemoveOwnerMI() {
	if (ivjUserRemoveOwnerMI == null) {
		try {
			ivjUserRemoveOwnerMI = new javax.swing.JMenuItem();
			ivjUserRemoveOwnerMI.setName("UserRemoveOwnerMI");
			ivjUserRemoveOwnerMI.setText("Remove ownership");
			ivjUserRemoveOwnerMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						removeOwner();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserRemoveOwnerMI;
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e ActionEvent
 */
public void grabModerator() {
	DSMPProto p = DSMPGenerator.transferModerator((byte) 0,dispatcher.meetingID,dispatcher.particpantID);
	dispatcher.dispatchProtocol(p);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e ActionEvent
 */
public void grantModerator() {
	Object selected = getPresenceTree().getSelectionPath().getLastPathComponent();

	UserInvite u = (UserInvite) selected;
	DSMPProto p = DSMPGenerator.transferModerator((byte) 0,dispatcher.meetingID,u.participantID);
	dispatcher.dispatchProtocol(p);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e ActionEvent
 */
public void grantOwner() {
	Object selected = getPresenceTree().getSelectionPath().getLastPathComponent();

	UserInvite u = (UserInvite) selected;
	DSMPProto p = DSMPGenerator.assignOwnership((byte) 0,dispatcher.meetingID,u.participantID,true);
	dispatcher.dispatchProtocol(p);
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
		setName("PresencePanel2");
		setLayout(new java.awt.GridBagLayout());
		setSize(157, 421);

		java.awt.GridBagConstraints constraintsInviteTF = new java.awt.GridBagConstraints();
		constraintsInviteTF.gridx = 0; constraintsInviteTF.gridy = 0;
		constraintsInviteTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsInviteTF.weightx = 1.0;
		constraintsInviteTF.insets = new java.awt.Insets(0, 0, 2, 5);
		add(getInviteTF(), constraintsInviteTF);

		java.awt.GridBagConstraints constraintsInviteBtn = new java.awt.GridBagConstraints();
		constraintsInviteBtn.gridx = 1; constraintsInviteBtn.gridy = 0;
		constraintsInviteBtn.insets = new java.awt.Insets(0, 0, 2, 0);
		add(getInviteBtn(), constraintsInviteBtn);

		java.awt.GridBagConstraints constraintsPresenceSP = new java.awt.GridBagConstraints();
		constraintsPresenceSP.gridx = 0; constraintsPresenceSP.gridy = 1;
		constraintsPresenceSP.gridwidth = 0;
		constraintsPresenceSP.fill = java.awt.GridBagConstraints.BOTH;
		constraintsPresenceSP.weightx = 1.0;
		constraintsPresenceSP.weighty = 1.0;
		add(getPresenceSP(), constraintsPresenceSP);

		// Listen for right-mouse clicks on the tree cells.
		MouseHandler mh = new MouseHandler();
		getPresenceTree().addMouseListener(mh);

		// Use a customer renderer for the tree cells.
		renderer = new PresenceCellRenderer();
		renderer.setSize(83, 78);
		renderer.setLimitChatter(limitChatter);
		getPresenceTree().setCellRenderer(renderer);

		// Set the tree data model.
		getPresenceTree().setModel(getPresenceTM());

		// Add the pop-up menus to the tree.
		getPresenceTree().add(getChatPU());
		getPresenceTree().add(getInvitePU());
		getPresenceTree().add(getDropPU());
		getPresenceTree().add(getUserPU());
		getPresenceTree().add(getProjUserPU());

		// Prepare to listen for changes on the invite field.
		getInviteTF().getDocument().addDocumentListener(this);

		// Initialize enablement state.
		setEnablement(0);

		// Prepare the main menu for the containing app.
		getMainUserM().setEnabled(false);

		// Set a border.
		setBorder(javax.swing.BorderFactory.createTitledBorder("Participants"));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2003 3:56:09 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void insertUpdate(DocumentEvent e) {
	textChgInvite();
}
/**
 * Insert the method's description here.
 * Creation date: (7/1/2004 2:18:06 PM)
 * @return boolean
 */
public boolean isLimitChatter() {
	return limitChatter;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:56:37 AM)
 * @param e MeetingEvent
 */
public void meetingAction(MeetingEvent e) {
	if (e.isControlEvent()) {
		getPresenceTM().controlPassed(e.participantID);
	}
	else if (e.isModeratorEvent()) {
		getPresenceTM().moderatorPassed(e.participantID,e.toPID);
	}
	else if (e.isOwnerEvent()) {
		getPresenceTM().ownerChange(e.participantID,e.addOrRemove);
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
										getPresenceTM().getUserName(dispatcher.particpantID),
										getPresenceTM().getUserName(e.fromID),
										dispatcher,
										e.message);
			mw.put(fID,w);
			w.addWindowListener(wh);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e PresenceEvent
 */
public void presenceChanged(PresenceEvent e) {
	//System.out.println(e);
	if (e.isProjectInvite()) {
		getPresenceTM().addProject(e.name,e.inviteID);
	}
	else if (e.isGroupInvite()) {
		getPresenceTM().addGroup(e.name,e.inviteID);
	}
	else if (e.isUserInvite()) {
		getPresenceTM().addUser(e.name,e.inviteID);
	}
	else if (e.isArrival()) {
		if (getPresenceTM().userArrived(e.participantID,e.inviteID,e.name)) {
			updateMsgWndws(e.participantID,true);
			dispatcher.fireArriveMessage(e.name);
			dispatcher.fireImageEvent(new ImageEvent(ImageEvent.FULL_IMAGE,(byte) 0,(byte) 0));
		}
	}
	else if (e.isInviteDeparture()) {
		getPresenceTM().removeInvite(e.inviteID);
	}
	else if (e.isUserDeparture()) {
		String name = getPresenceTM().getUserName(e.participantID);
		if (getPresenceTM().userDeparted(e.participantID,e.inviteID)) {
			updateMsgWndws(e.participantID,false);
			if (e.participantID == dispatcher.particpantID)
				dispatcher.fireForcedLeaveMeeting();
			else
				dispatcher.fireDepartMessage(name);
		}
	}
	else {
		System.out.println("PresencePanel.presenceChanged: drop event is neither user or invite");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:55:36 AM)
 * @param l MenuChangeListener
 */
public synchronized void removeMenuChangeListener(MenuChangeListener l) {
	if (l == null)
		return;

	listener = DSMPEventMulticaster.removeMenuChangeListener(listener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e ActionEvent
 */
public void removeOwner() {
	Object selected = getPresenceTree().getSelectionPath().getLastPathComponent();

	UserInvite u = (UserInvite) selected;
	DSMPProto p = DSMPGenerator.assignOwnership((byte) 0,dispatcher.meetingID,u.participantID,false);
	dispatcher.dispatchProtocol(p);
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2003 3:55:49 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void removeUpdate(DocumentEvent e) {
	textChgInvite();
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e ActionEvent
 */
public void removeUser() {
	Object selected = getPresenceTree().getSelectionPath().getLastPathComponent();

	UserInvite u = (UserInvite) selected;
	DSMPProto p = DSMPGenerator.dropInvitee((byte) 0,dispatcher.meetingID,u.inviteID,u.participantID,false,true);
	dispatcher.dispatchProtocol(p);
}
/**
 * Comment
 */
public void selectionChg() {
	if (getPresenceTree().getSelectionCount() == 0) {
		if (dispatcher.isOwner && ! dispatcher.isModerator) {
			getMainUserM().removeAll();
			getMainUserM().add(getMainGrabModMI());
			getMainUserM().setEnabled(true);
		}
		else {
			getMainUserM().setEnabled(false);
		}
	}
	else {
		TreePath tp = getPresenceTree().getSelectionPath();
		Invite i = (Invite) tp.getLastPathComponent();
		if (i instanceof UserInvite && ((UserInvite) i).present) {
			UserInvite ui = (UserInvite) i;
			if (ui.participantID != dispatcher.particpantID) {
				if (dispatcher.isOwner)
					if (ui.isProjUser) {
						getMainUserM().removeAll();
						getMainUserM().add(getMainChatMI());
						getMainUserM().add(getMainRemoveMI());
						getMainUserM().add(ui.isOwner ? getMainRemoveOwnerMI() : getMainGrantOwnerMI());
						getMainUserM().add(ui.isModerator ? getMainGrabModMI() : getMainGrantModMI());
						getMainUserM().setEnabled(true);
					}
					else {
						getMainUserM().removeAll();
						getMainUserM().add(getMainChatMI());
						getMainUserM().add(getMainRemoveMI());
						getMainUserM().add(getMainDropMI());
						getMainUserM().add(ui.isOwner ? getMainRemoveOwnerMI() : getMainGrantOwnerMI());
						getMainUserM().add(ui.isModerator ? getMainGrabModMI() : getMainGrantModMI());
						getMainUserM().setEnabled(true);
					}
				else {
					getMainUserM().removeAll();
					getMainUserM().add(getMainChatMI());
					getMainUserM().setEnabled(true);
				}
			}
			else if (dispatcher.isOwner && ! ui.isModerator) {
				getMainUserM().removeAll();
				getMainUserM().add(getMainGrabModMI());
				getMainUserM().setEnabled(true);
			}
			else {
				getMainUserM().setEnabled(false);
			}
		}
		else if (dispatcher.isOwner) {
			getMainUserM().removeAll();
			getMainUserM().add(getMainDropMI());
			getMainUserM().setEnabled(true);
		}
		else {
			getMainUserM().setEnabled(false);
		}
	}
}
/**
 * Comment
 */
public void sendInvite() {
	if (! getInviteBtn().isEnabled())
		return;
	/* trim() metod added by Aswath on 12/22/03
	*/
	String text = getInviteTF().getText().trim();
	StringTokenizer list = new StringTokenizer(text," ,\t\r\n\f");

	while (list.hasMoreTokens()) {
		String name = list.nextToken();

		DSMPProto p = DSMPGenerator.createInvitation((byte) 0,dispatcher.meetingID,DSMPGenerator.STATUS_NONE,false,name);
		dispatcher.dispatchProtocol(p);
	}

	getInviteTF().setText("");
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:54:57 AM)
 * @param e ActionEvent
 */
public void sendMsg() {
	Object selected = getPresenceTree().getSelectionPath().getLastPathComponent();

	UserInvite u = (UserInvite) selected;
	Integer pID = new Integer(u.participantID);
	MessageWindow w = (MessageWindow) mw.get(pID);

	// We are not currently chatting with this user.
	if (w == null) {
		w = MessageWindow.startChat(dispatcher.particpantID,
									u.participantID,
									getPresenceTM().getUserName(dispatcher.particpantID),
									new String(u.name),
									dispatcher,
									null);
		mw.put(pID,w);
		w.addWindowListener(wh);
	}

	// Bring new or existing chat window to front.
	w.toFront();
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
		// Stop listening to events on current dispatcher.
		dispatcher.removePresenceListener(this);
		dispatcher.removeMessageListener(this);
		dispatcher.removeMeetingListener(this);

		// Clear the data model.
		getPresenceTM().clear();

		// Close any open chat windows.
		Enumeration e = mw.elements();
		while (e.hasMoreElements()) {
			MessageWindow w = (MessageWindow) e.nextElement();
			w.removeWindowListener(wh);
			w.dispose();
		}
		mw.clear();

		// Remove our Selected user menu from the containing app.
		fireMenuChangeEvent(new MenuChangeEvent(MenuChangeEvent.REMOVEMENU,getMainUserM()));
	}

	dispatcher = d;
	setEnablement(0);

	if (renderer != null) {
		renderer.setDispatcher(d);
	}

	if (dispatcher != null) {
		// Start listening for events.
		dispatcher.addPresenceListener(this);
		dispatcher.addMessageListener(this);
		dispatcher.addMeetingListener(this);

		// Add our Selected user menu to the containing app.
		fireMenuChangeEvent(new MenuChangeEvent(MenuChangeEvent.ADDMENU,getMainUserM()));
	}
}
/**
 * Adjusts GUI elements for calling container. Mode is:
 *  0 - All components are visible, but disabled.
 *  1 - All components are visible and enabled (meeting owner).
 *  2 - Only the tree is visible and enabled (participant).
 */
public void setEnablement(int mode) {
	switch (mode) {
		case 0: {
			getPresenceTree().setEnabled(false);
			getInviteTF().setEnabled(false);
			getInviteBtn().setEnabled(false);
			if (! getInviteTF().isVisible()) {
				getInviteTF().setVisible(true);
				getInviteBtn().setVisible(true);
				doLayout();
			}
			break;
		}
		case 1: {
			getPresenceTree().setEnabled(true);
			getInviteTF().setEnabled(true);
			getInviteTF().setText(null);
			getInviteBtn().setEnabled(false);
			if (! getInviteTF().isVisible()) {
				getInviteTF().setVisible(true);
				getInviteBtn().setVisible(true);
				doLayout();
			}
			break;
		}
		case 2: {
			getPresenceTree().setEnabled(true);
			getInviteTF().setEnabled(false);
			getInviteBtn().setEnabled(false);
			if (getInviteTF().isVisible()) {
				getInviteTF().setVisible(false);
				getInviteBtn().setVisible(false);
				doLayout();
			}
			break;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/1/2004 2:18:06 PM)
 * @param newLimitProjectChatter boolean
 */
public void setLimitChatter(boolean newLimitChatter) {
	limitChatter = newLimitChatter;
	if (renderer != null)
		renderer.setLimitChatter(newLimitChatter);
}
/**
 * Comment
 */
public void textChgInvite() {
	/***
		trim() method added by Aswath, in solving the error :
		Enability of Invite button for empty values of Invite field.
	*/
	String user = getInviteTF().getText();

	getInviteBtn().setEnabled(user != null && user.trim().length() > 0);

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2002 9:06:54 AM)
 * @param partID java.lang.Integer
 * @param online boolean
 */
public void updateMsgWndws(int partID, boolean online) {
	MessageWindow w = (MessageWindow) mw.get(new Integer(partID));
	if (w != null)
		w.partnerStatus(online);
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
