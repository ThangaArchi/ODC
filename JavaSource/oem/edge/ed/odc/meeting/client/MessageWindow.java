package oem.edge.ed.odc.meeting.client;

import javax.swing.text.*;
import java.io.*;
import javax.swing.event.*;
import oem.edge.ed.odc.meeting.common.*;
import java.awt.datatransfer.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (3/20/2003 12:42:49 PM)
 * @author: Mike Zarnick
 */
public class MessageWindow extends JFrame implements MessageListener {
	public int ownerID;
	public int toID;
	private boolean canPaste = false;
	private String ownerName;
	private String toName;
	private JTextArea editTarget;
	private DSMPDispatcher dispatcher;
	private JMenuItem ivjClearMI = null;
	private JButton ivjCloseBtn = null;
	private JMenuItem ivjCloseMI = null;
	private JMenuItem ivjCopyMI = null;
	private JMenuItem ivjCutMI = null;
	private JPanel ivjJFrameContentPane = null;
	private JMenu ivjJMenu1 = null;  // @jve:visual-info  decl-index=0 visual-constraint="280,349"
	private JSeparator ivjJSeparator1 = null;
	private JSeparator ivjJSeparator2 = null;
	private JMenu ivjMessageM = null;  // @jve:visual-info  decl-index=0 visual-constraint="34,349"
	private JScrollPane ivjMessageSP = null;
	private JTextArea ivjMessageTA = null;
	private JMenuBar ivjMessageWindowJMenuBar = null;
	private JLabel ivjMsgLbl = null;
	private JMenuItem ivjPasteMI = null;
	private JMenuItem ivjSaveMI = null;
	private JButton ivjSendBtn = null;
	private JScrollPane ivjSendSP = null;
	private JTextArea ivjSendTA = null;
	private JLabel ivjStatusLbl = null;
	private JFileChooser ivjFileChooser = null;  //  @jve:visual-info  decl-index=0 visual-constraint="19,320"
/**
 * MessageWindow constructor comment.
 */
public MessageWindow() {
	super();
	initialize();
}
/**
 * MessageWindow constructor comment.
 * @param title java.lang.String
 */
public MessageWindow(String title) {
	super(title);
}
/**
 * Comment
 */
public void adjustEditMenu(CaretEvent e) {
	// Caret adjusted in one of the text areas. The text area will
	// become the target of the edit menu items.
	editTarget = (JTextArea) e.getSource();

	// Adjust menu items according to text selection. Paste is managed
	// by the cut/copy methods and by window activation. Paste on the
	// Send pop-up is used as the reference.
	boolean enabled = e.getDot() != e.getMark();

	if (editTarget == getMessageTA()) {
		getCutMI().setEnabled(false);
		getCopyMI().setEnabled(enabled);
		getPasteMI().setEnabled(false);
	}
	else {
		getCutMI().setEnabled(enabled);
		getCopyMI().setEnabled(enabled);
		getPasteMI().setEnabled(canPaste);
	}

}
/**
 * Comment
 */
public void adjustPaste() {
	canPaste = false;
	
	try {
		canPaste = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this) != null;
	}
	catch (Exception e) {
		// Couldn't access the clipboard... oh well.
	}

	if (editTarget == getSendTA())
		getPasteMI().setEnabled(canPaste);
}
/**
 * Comment
 */
public void centerWindow() {
	// Center the window over this frame (or the screen if w is frame itself).
	Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension winSize = getSize();

	//System.out.println("Window size: " + winSize.width + "x" + winSize.height);
	//System.out.println("Relative to window size: " + frmSize.width + "x" + frmSize.height + " at: (" + frmPos.x + "," + frmPos.y + ")");
	//System.out.println("Placing window at: " + (frmPos.x + (frmSize.width - winSize.width) / 2) + "," + (frmPos.y + (frmSize.height - winSize.height) / 2));

	int x = (scrSize.width - winSize.width) / 2;
	int y = (scrSize.height - winSize.height) / 2;

	int dx = x + winSize.width - scrSize.width;
	if (dx > 0)
		x -= dx;

	if (x < 0)
		x = 0;

	int dy = y + winSize.height - scrSize.height;
	if (dy > 0)
		y -= dy;

	if (y < 0)
		y = 0;

	setLocation(x,y);
	setVisible(true);
}
/**
 * Comment
 */
public void doClear() {
	if (editTarget != null)
		editTarget.setText("");
}
/**
 * Comment
 */
public void doCopy() {
	if (editTarget != null) {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection s = new StringSelection(editTarget.getSelectedText());
		c.setContents(s,s);
		canPaste = true;
		getPasteMI().setEnabled(editTarget == getSendTA() ? true : false);
	}
}
/**
 * Comment
 */
public void doCut() {
	if (editTarget != null) {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection s = new StringSelection(editTarget.getSelectedText());
		c.setContents(s,s);
		canPaste = true;
		getPasteMI().setEnabled(editTarget == getSendTA() ? true : false);
		editTarget.replaceRange("",editTarget.getSelectionStart(),editTarget.getSelectionEnd());
	}
}
/**
 * Comment
 */
public void doPaste() {
	if (editTarget != null) {
		Transferable t = null;
		
		try {
			t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
		}
		catch (Exception e) {
			// Couldn't access the clipboard... oh well.
		}

		if (t != null) {
			try {
				String text = (String) t.getTransferData(DataFlavor.stringFlavor);
				editTarget.replaceRange(text,editTarget.getSelectionStart(),editTarget.getSelectionEnd());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
}
/**
 * Comment
 */
public void doSave() {
	if (getFileChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
		File textFile = getFileChooser().getSelectedFile();

		try {
			FileWriter f = new FileWriter(textFile);
			getMessageTA().write(f);
			f.close();
		}
		catch (IOException e) {
			dispatcher.fireMeetingEvent(new MeetingEvent(MeetingEvent.CHAT_FAILED,(byte) 0,(byte) 0,e.getMessage()));
		}
	}
}
/**
 * Comment
 */
public void doSend() {
	String msg = getSendTA().getText();

	if (msg != null) {
		DSMPProto p = DSMPGenerator.chatMessage((byte) 0,dispatcher.meetingID,toID,msg);
		dispatcher.dispatchProtocol(p);
		getSendTA().setText("");
		postMessage(ownerName + ": " + msg);

		if (! getMessageSP().isVisible()) {
			GridBagLayout l = (GridBagLayout) getJFrameContentPane().getLayout();
			GridBagConstraints c = l.getConstraints(getSendSP());
			c.weighty = 0.0;
			l.setConstraints(getSendSP(),c);
			setTitle("Chat with " + toName);
			getMsgLbl().setText("Type your message:");
			getMessageSP().setVisible(true);
			setSize(380,270);
			validate();
		}
	}
}
/**
 * Return the ClearMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getClearMI() {
	if (ivjClearMI == null) {
		try {
			ivjClearMI = new javax.swing.JMenuItem();
			ivjClearMI.setName("ClearMI");
			ivjClearMI.setText("Clear");
			ivjClearMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doClear();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjClearMI;
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
			ivjCloseBtn.setText("Close");
			ivjCloseBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCloseBtn;
}
/**
 * Return the CloseMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getCloseMI() {
	if (ivjCloseMI == null) {
		try {
			ivjCloseMI = new javax.swing.JMenuItem();
			ivjCloseMI.setName("CloseMI");
			ivjCloseMI.setText("Close");
			ivjCloseMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCloseMI;
}
/**
 * Return the CopyMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getCopyMI() {
	if (ivjCopyMI == null) {
		try {
			ivjCopyMI = new javax.swing.JMenuItem();
			ivjCopyMI.setName("CopyMI");
			ivjCopyMI.setText("Copy");
			ivjCopyMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doCopy();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCopyMI;
}
/**
 * Return the CutMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getCutMI() {
	if (ivjCutMI == null) {
		try {
			ivjCutMI = new javax.swing.JMenuItem();
			ivjCutMI.setName("CutMI");
			ivjCutMI.setText("Cut");
			ivjCutMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doCut();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCutMI;
}
/**
 * Return the FileChooser property value.
 * @return javax.swing.JFileChooser
 */
private javax.swing.JFileChooser getFileChooser() {
	if (ivjFileChooser == null) {
		try {
			ivjFileChooser = new javax.swing.JFileChooser();
			ivjFileChooser.setName("FileChooser");
			ivjFileChooser.setBounds(44, 626, 500, 300);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileChooser;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsMessageSP = new java.awt.GridBagConstraints();
			constraintsMessageSP.gridx = 0; constraintsMessageSP.gridy = 0;
			constraintsMessageSP.gridwidth = 0;
			constraintsMessageSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsMessageSP.weightx = 1.0;
			constraintsMessageSP.weighty = 1.0;
			constraintsMessageSP.insets = new java.awt.Insets(5, 5, 5, 5);
			getJFrameContentPane().add(getMessageSP(), constraintsMessageSP);

			java.awt.GridBagConstraints constraintsMsgLbl = new java.awt.GridBagConstraints();
			constraintsMsgLbl.gridx = 0; constraintsMsgLbl.gridy = 1;
			constraintsMsgLbl.gridwidth = 0;
			constraintsMsgLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMsgLbl.insets = new java.awt.Insets(5, 5, 5, 5);
			getJFrameContentPane().add(getMsgLbl(), constraintsMsgLbl);

			java.awt.GridBagConstraints constraintsStatusLbl = new java.awt.GridBagConstraints();
			constraintsStatusLbl.gridx = 0; constraintsStatusLbl.gridy = 3;
			constraintsStatusLbl.anchor = java.awt.GridBagConstraints.SOUTHWEST;
			constraintsStatusLbl.weightx = 1.0;
			constraintsStatusLbl.insets = new java.awt.Insets(5, 5, 5, 5);
			getJFrameContentPane().add(getStatusLbl(), constraintsStatusLbl);

			java.awt.GridBagConstraints constraintsSendBtn = new java.awt.GridBagConstraints();
			constraintsSendBtn.gridx = 1; constraintsSendBtn.gridy = 3;
			constraintsSendBtn.insets = new java.awt.Insets(5, 0, 5, 5);
			getJFrameContentPane().add(getSendBtn(), constraintsSendBtn);

			java.awt.GridBagConstraints constraintsCloseBtn = new java.awt.GridBagConstraints();
			constraintsCloseBtn.gridx = 2; constraintsCloseBtn.gridy = 3;
			constraintsCloseBtn.insets = new java.awt.Insets(4, 4, 4, 4);
			getJFrameContentPane().add(getCloseBtn(), constraintsCloseBtn);

			java.awt.GridBagConstraints constraintsSendSP = new java.awt.GridBagConstraints();
			constraintsSendSP.gridx = 0; constraintsSendSP.gridy = 2;
			constraintsSendSP.gridwidth = 0;
			constraintsSendSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSendSP.weightx = 1.0;
			constraintsSendSP.insets = new java.awt.Insets(0, 5, 0, 5);
			getJFrameContentPane().add(getSendSP(), constraintsSendSP);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Return the JMenu1 property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getJMenu1() {
	if (ivjJMenu1 == null) {
		try {
			ivjJMenu1 = new javax.swing.JMenu();
			ivjJMenu1.setName("JMenu1");
			ivjJMenu1.setText("Edit");
			ivjJMenu1.setActionCommand("EditM");
			ivjJMenu1.add(getCutMI());
			ivjJMenu1.add(getCopyMI());
			ivjJMenu1.add(getPasteMI());
			ivjJMenu1.add(getJSeparator2());
			ivjJMenu1.add(getClearMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenu1;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}
/**
 * Return the JSeparator2 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator2() {
	if (ivjJSeparator2 == null) {
		try {
			ivjJSeparator2 = new javax.swing.JSeparator();
			ivjJSeparator2.setName("JSeparator2");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator2;
}
/**
 * Return the MessageM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getMessageM() {
	if (ivjMessageM == null) {
		try {
			ivjMessageM = new javax.swing.JMenu();
			ivjMessageM.setName("MessageM");
			ivjMessageM.setText("Message");
			ivjMessageM.add(getSaveMI());
			ivjMessageM.add(getJSeparator1());
			ivjMessageM.add(getCloseMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMessageM;
}
/**
 * Return the MessageSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getMessageSP() {
	if (ivjMessageSP == null) {
		try {
			ivjMessageSP = new javax.swing.JScrollPane();
			ivjMessageSP.setName("MessageSP");
			getMessageSP().setViewportView(getMessageTA());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMessageSP;
}
/**
 * Return the MessageTA property value.
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getMessageTA() {
	if (ivjMessageTA == null) {
		try {
			ivjMessageTA = new javax.swing.JTextArea();
			ivjMessageTA.setName("MessageTA");
			ivjMessageTA.setLineWrap(true);
			ivjMessageTA.setWrapStyleWord(true);
			ivjMessageTA.setBounds(0, 0, 160, 120);
			ivjMessageTA.setEditable(false);
			ivjMessageTA.addCaretListener(new javax.swing.event.CaretListener() { 
				public void caretUpdate(javax.swing.event.CaretEvent e) {    
					try {
						adjustEditMenu(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMessageTA;
}
/**
 * Return the MessageWindowJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
private javax.swing.JMenuBar getMessageWindowJMenuBar() {
	if (ivjMessageWindowJMenuBar == null) {
		try {
			ivjMessageWindowJMenuBar = new javax.swing.JMenuBar();
			ivjMessageWindowJMenuBar.setName("MessageWindowJMenuBar");
			ivjMessageWindowJMenuBar.add(getMessageM());
			ivjMessageWindowJMenuBar.add(getJMenu1());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMessageWindowJMenuBar;
}
/**
 * Return the MsgLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getMsgLbl() {
	if (ivjMsgLbl == null) {
		try {
			ivjMsgLbl = new javax.swing.JLabel();
			ivjMsgLbl.setName("MsgLbl");
			ivjMsgLbl.setText("Type your message:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMsgLbl;
}
/**
 * Return the PasteMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPasteMI() {
	if (ivjPasteMI == null) {
		try {
			ivjPasteMI = new javax.swing.JMenuItem();
			ivjPasteMI.setName("PasteMI");
			ivjPasteMI.setText("Paste");
			ivjPasteMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doPaste();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPasteMI;
}
/**
 * Return the SaveMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getSaveMI() {
	if (ivjSaveMI == null) {
		try {
			ivjSaveMI = new javax.swing.JMenuItem();
			ivjSaveMI.setName("SaveMI");
			ivjSaveMI.setText("Save As...");
			ivjSaveMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doSave();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSaveMI;
}
/**
 * Return the SendBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSendBtn() {
	if (ivjSendBtn == null) {
		try {
			ivjSendBtn = new javax.swing.JButton();
			ivjSendBtn.setName("SendBtn");
			ivjSendBtn.setText("Send");
			ivjSendBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doSend();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendBtn;
}
/**
 * Return the SendSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getSendSP() {
	if (ivjSendSP == null) {
		try {
			ivjSendSP = new javax.swing.JScrollPane();
			ivjSendSP.setName("SendSP");
			ivjSendSP.setPreferredSize(new java.awt.Dimension(20, 50));
			ivjSendSP.setMinimumSize(new java.awt.Dimension(20, 50));
			getSendSP().setViewportView(getSendTA());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendSP;
}
/**
 * Return the SendTA property value.
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getSendTA() {
	if (ivjSendTA == null) {
		try {
			ivjSendTA = new javax.swing.JTextArea();
			ivjSendTA.setName("SendTA");
			ivjSendTA.setLineWrap(true);
			ivjSendTA.setWrapStyleWord(true);
			ivjSendTA.setBounds(0, 0, 160, 120);
			ivjSendTA.addCaretListener(new javax.swing.event.CaretListener() { 
				public void caretUpdate(javax.swing.event.CaretEvent e) {    
					try {
						adjustEditMenu(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSendTA;
}
/**
 * Return the StatusLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getStatusLbl() {
	if (ivjStatusLbl == null) {
		try {
			ivjStatusLbl = new javax.swing.JLabel();
			ivjStatusLbl.setName("StatusLbl");
			ivjStatusLbl.setText("Messaging partner is on-line");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStatusLbl;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("MessageWindow");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(380, 270);
		setJMenuBar(getMessageWindowJMenuBar());
		setContentPane(getJFrameContentPane());
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowActivated(java.awt.event.WindowEvent e) {    
				try {
					adjustPaste();
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
		});

		// Set the initial edit target.
		editTarget = getSendTA();

		// Update the keymap for the send textarea:

		// Get the keymap.
		Keymap km = getSendTA().getKeymap();

		// Use plain enter's action for ctrl-enter, alt-enter and shift-enter.
		KeyStroke ceks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,KeyEvent.CTRL_MASK,false);
		KeyStroke aeks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,KeyEvent.ALT_MASK,false);
		KeyStroke seks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,KeyEvent.SHIFT_MASK,false);
		km.removeKeyStrokeBinding(ceks);
		km.removeKeyStrokeBinding(aeks);
		km.removeKeyStrokeBinding(seks);
		Action ba = new DefaultEditorKit.InsertBreakAction();
		km.addActionForKeyStroke(ceks,ba);
		km.addActionForKeyStroke(aeks,ba);
		km.addActionForKeyStroke(seks,ba);

		// Get the KeyStroke for plain enter and get the action for plain enter.
		KeyStroke eks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0,false);

		// Remove the action binding for plain enter.
		km.removeKeyStrokeBinding(eks);

		// Create an action which calls our doSend() method and invoke this action for plain enter.
		Action a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				doSend();
			}
		};
		km.addActionForKeyStroke(eks,a);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 11:41:33 AM)
 * @param e MessageEvent
 */
public void message(MessageEvent e) {
	// Unicast messages from our partner (toID) are ours,
	if (e.isUnicast() && e.fromID == toID) {
		postMessage(toName + ": " + e.message);
		this.toFront();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2002 9:03:07 AM)
 * @param online boolean
 */
public void partnerStatus(boolean online) {
	if (online) {
		getStatusLbl().setText("Messaging partner is on-line.");
		getStatusLbl().setForeground(getMsgLbl().getForeground());
	}
	else {
		getStatusLbl().setText("Messaging partner is off-line.");
		getStatusLbl().setForeground(Color.red);
	}

	getMsgLbl().setEnabled(online);
	getSendBtn().setEnabled(online);
	getSendTA().setEnabled(online);
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 11:20:21 AM)
 * @param msg java.lang.String
 */
public void postMessage(String msg) {
	synchronized(getMessageTA()) {
		int caret = getMessageTA().getText().length();

		if (caret > 0) {
			getMessageTA().append("\n");
			caret++;
		}

		getMessageTA().append(msg);
		getMessageTA().setCaretPosition(caret);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/30/2002 1:08:43 PM)
 * @param from int
 * @param to int
 * @param pp PresencePanel
 */
static public MessageWindow startChat(int fID, int tID, String fName, String tName, DSMPDispatcher d, String msg) {
	MessageWindow mw = new MessageWindow();
	mw.ownerID = fID;
	mw.toID = tID;
	mw.ownerName = fName;
	mw.toName = tName;
	mw.dispatcher = d;

	if (msg != null) {
		mw.postMessage(tName + ": " + msg);
		mw.setTitle("Chat with " + tName);
	}
	else {
		GridBagLayout l = (GridBagLayout) mw.getJFrameContentPane().getLayout();
		GridBagConstraints c = l.getConstraints(mw.getSendSP());
		c.weighty = 1.0;
		l.setConstraints(mw.getSendSP(),c);
		mw.getMessageSP().setVisible(false);
		mw.getMsgLbl().setText("To: " + mw.toName);
		mw.setTitle("Send Message");
		mw.setSize(380,180);
		mw.validate();
	}

	mw.centerWindow();
	mw.getSendTA().requestFocus();
	d.addMessageListener(mw);

	return mw;
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
