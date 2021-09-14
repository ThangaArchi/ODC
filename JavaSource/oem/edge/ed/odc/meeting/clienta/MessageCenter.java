package oem.edge.ed.odc.meeting.clienta;

import java.awt.datatransfer.*;
import java.util.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import oem.edge.ed.odc.meeting.common.*;

/**
 * Insert the type's description here.
 * Creation date: (9/13/2002 12:30:25 PM)
 * @author: Mike Zarnick
 */
class MessageCenter extends Frame implements MeetingListener, MessageListener {
	private String user = null;
	private String meeting = null;
	private String serverName = "localhost";
	private int serverPort = 5000;
	private boolean leaving = false;
	private DSMPDispatcher dispatcher = null;
	private TextComponent editTarget;
	private BorderPanel ivjBorderPanel = null;
	private Panel ivjContentsPane = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private MessageTextArea ivjMessageTA = null;
	private PresencePanel ivjPresencePnl = null;
	private ScrollPane ivjPresenceSP = null;
	private TipButton ivjSendBtn = null;
	private TipTextField ivjSendTF = null;
	private MenuItem ivjExitMI = null;
	private Menu ivjFileM = null;
	private MenuBar ivjMessageCenterMenuBar = null;
	private Panel ivjContentsPane6 = null;
	private Dialog ivjErrorDlg = null;
	private Button ivjErrorOkBtn = null;
	private TextArea ivjErrorTA = null;
	private MenuItem ivjClearMI = null;
	private MenuItem ivjClearPMI = null;
	private MenuItem ivjCopyMI = null;
	private MenuItem ivjCopyPMI = null;
	private MenuItem ivjCutMI = null;
	private MenuItem ivjCutPMI = null;
	private Menu ivjEditM = null;
	private PopupMenu ivjEditPU = null;
	private MenuItem ivjMenuSeparator11 = null;
	private MenuItem ivjMenuSeparator31 = null;
	private MenuItem ivjPasteMI = null;
	private MenuItem ivjPastePMI = null;
	private FileDialog ivjFileDlg = null;
	private MenuItem ivjMenuSeparator1 = null;
	private MenuItem ivjSaveMI = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ComponentListener, java.awt.event.MouseListener, java.awt.event.WindowListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MessageCenter.this.getExitMI()) 
				connEtoC4(e);
			if (e.getSource() == MessageCenter.this.getErrorOkBtn()) 
				connEtoM1(e);
			if (e.getSource() == MessageCenter.this.getCutMI()) 
				connEtoC6(e);
			if (e.getSource() == MessageCenter.this.getCopyMI()) 
				connEtoC7(e);
			if (e.getSource() == MessageCenter.this.getPasteMI()) 
				connEtoC8(e);
			if (e.getSource() == MessageCenter.this.getClearMI()) 
				connEtoC9(e);
			if (e.getSource() == MessageCenter.this.getCutPMI()) 
				connEtoC10(e);
			if (e.getSource() == MessageCenter.this.getCopyPMI()) 
				connEtoC11(e);
			if (e.getSource() == MessageCenter.this.getPastePMI()) 
				connEtoC12(e);
			if (e.getSource() == MessageCenter.this.getClearPMI()) 
				connEtoC13(e);
			if (e.getSource() == MessageCenter.this.getSaveMI()) 
				connEtoC20(e);
			if (e.getSource() == MessageCenter.this.getSendTF()) 
				connEtoC2(e);
			if (e.getSource() == MessageCenter.this.getSendBtn()) 
				connEtoC3(e);
		};
		public void componentHidden(java.awt.event.ComponentEvent e) {};
		public void componentMoved(java.awt.event.ComponentEvent e) {};
		public void componentResized(java.awt.event.ComponentEvent e) {
			if (e.getSource() == MessageCenter.this.getContentsPane()) 
				connEtoC21(e);
		};
		public void componentShown(java.awt.event.ComponentEvent e) {};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {
			if (e.getSource() == MessageCenter.this.getSendTF()) 
				connEtoC16(e);
			if (e.getSource() == MessageCenter.this.getMessageTA()) 
				connEtoC14(e);
		};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == MessageCenter.this.getSendTF()) 
				connEtoC17(e);
			if (e.getSource() == MessageCenter.this.getMessageTA()) 
				connEtoC15(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == MessageCenter.this.getSendTF()) 
				connEtoC18(e);
		};
		public void windowActivated(java.awt.event.WindowEvent e) {
			if (e.getSource() == MessageCenter.this) 
				connEtoC19(e);
		};
		public void windowClosed(java.awt.event.WindowEvent e) {};
		public void windowClosing(java.awt.event.WindowEvent e) {
			if (e.getSource() == MessageCenter.this) 
				connEtoC1(e);
		};
		public void windowDeactivated(java.awt.event.WindowEvent e) {};
		public void windowDeiconified(java.awt.event.WindowEvent e) {};
		public void windowIconified(java.awt.event.WindowEvent e) {};
		public void windowOpened(java.awt.event.WindowEvent e) {};
	};
/**
 * MessageCenter constructor comment.
 */
public MessageCenter() {
	super();
	initialize();
}
/**
 * MessageCenter constructor comment.
 * @param title java.lang.String
 */
public MessageCenter(String title) {
	super(title);
}
/**
 * Comment
 */
public void adjustEditMenu(MouseEvent e) {
	// Mouse pressed in one of the text areas. It will
	// become the target of the edit menu items.
	if (e.getID() == MouseEvent.MOUSE_PRESSED) {
		editTarget = (TextComponent) e.getSource();
	}

	// Mouse exited the target text area. Adjust menu items
	// according to text selection. Paste is managed by
	// the cut/copy methods and by window activation.
	else {
		boolean selected = editTarget.getSelectionStart() != editTarget.getSelectionEnd();
		getCutMI().setEnabled(selected);
		getCopyMI().setEnabled(selected);
	}

	return;
}
/**
 * Comment
 */
public void adjustPaste() {
	getPasteMI().setEnabled(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this) != null);
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:52:26 PM)
 * @param args java.lang.String[]
 */
public void begin(String[] args) {
	reset();

	// MessageCenter user meeting [host] [port]
	if (args.length < 2) {
		System.out.println("Need user and meeting names.");
		System.out.println("MessageCenter user meeting [host] [port]");
		System.exit(1);
	}

	user = args[0];
	meeting = args[1];

	if (args.length > 2)
		serverName = args[2];

	if (args.length > 3)
		try {
				serverPort = Integer.parseInt(args[3]);
		}
		catch (NumberFormatException e) {
			System.out.println("Port is not a valid integer.");
			System.out.println("MessageCenter user meeting [host] [port]");
			System.exit(1);
		}

	centerWindow(this);
	login();
}
/**
 * Comment
 */
public void centerWindow(Window w) {
	// Center the window over this frame (or the screen if w is frame itself).
	Point frmPos;
	Dimension frmSize;
	Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension winSize = w.getSize();

	if (w == this) {
		frmPos = new Point(0,0);
		frmSize = scrSize;
	}
	else {
		frmPos = getLocation();
		frmSize = getSize();
	}

	//System.out.println("Window size: " + winSize.width + "x" + winSize.height);
	//System.out.println("Relative to window size: " + frmSize.width + "x" + frmSize.height + " at: (" + frmPos.x + "," + frmPos.y + ")");
	//System.out.println("Placing window at: " + (frmPos.x + (frmSize.width - winSize.width) / 2) + "," + (frmPos.y + (frmSize.height - winSize.height) / 2));

	int x = frmPos.x + (frmSize.width - winSize.width) / 2;
	int y = frmPos.y + (frmSize.height - winSize.height) / 2;

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

	w.setLocation(x,y);
	w.setVisible(true);
}
/**
 * connEtoC1:  (MessageCenter.window.windowClosing(java.awt.event.WindowEvent) --> MessageCenter.dispose()V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.exit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (CutPMI.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.doCut()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doCut();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (CopyPMI.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.doCopy()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doCopy();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC12:  (PastePMI.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.doPaste()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doPaste();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  (ClearPMI.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.doClear()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doClear();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC14:  (MessageTA.mouse.mouseExited(java.awt.event.MouseEvent) --> MessageCenter.adjustEditMenu(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.adjustEditMenu(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC15:  (MessageTA.mouse.mousePressed(java.awt.event.MouseEvent) --> MessageCenter.adjustEditMenu(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.adjustEditMenu(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC16:  (SendTF.mouse.mouseExited(java.awt.event.MouseEvent) --> MessageCenter.adjustEditMenu(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.adjustEditMenu(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC17:  (SendTF.mouse.mousePressed(java.awt.event.MouseEvent) --> MessageCenter.adjustEditMenu(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.adjustEditMenu(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC18:  (SendTF.mouse.mouseReleased(java.awt.event.MouseEvent) --> MessageCenter.doMouse(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doMouse(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC19:  (MessageCenter.window.windowActivated(java.awt.event.WindowEvent) --> MessageCenter.adjustPaste()V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.adjustPaste();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (SendTF.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.sendMsg()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.sendMsg();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC20:  (SaveMI.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.doSave()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doSave();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC21:  (ContentsPane.component.componentResized(java.awt.event.ComponentEvent) --> MessageCenter.contentsPaneResized()V)
 * @param arg1 java.awt.event.ComponentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC21(java.awt.event.ComponentEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.contentsPaneResized();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (SendBtn.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.sendMsg()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.sendMsg();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (ExitMI.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.exit()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.exit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (MessageCenter.initialize() --> MessageCenter.setup()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5() {
	try {
		// user code begin {1}
		// user code end
		this.setup();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (CutMI.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.doCut()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doCut();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (CopyMI.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.doCopy()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doCopy();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (PasteMI.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.doPaste()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doPaste();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (ClearMI.action.actionPerformed(java.awt.event.ActionEvent) --> MessageCenter.doClear()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doClear();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (ErrorOkBtn.action.actionPerformed(java.awt.event.ActionEvent) --> ErrorDlg.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getErrorDlg().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void contentsPaneResized() {
	// Get component sizes.
	Dimension cp = getContentsPane().getSize();
	Dimension pb = getPresenceSP().getSize();

	// Determine size of message window.
	int w = Math.max(100,cp.width - pb.width - 15);
	int h = Math.max(100,cp.height - 10);

	// Size the message window.
	getBorderPanel().setSize(w,h);
	getBorderPanel().doLayout();

	// Size and position the presence box.
	getPresenceSP().setLocation(10+w,5);
	getPresenceSP().setSize(pb.width,h);
	getPresenceSP().doLayout();
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
		getPasteMI().setEnabled(true);
		c.setContents(s,s);
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
		getPasteMI().setEnabled(true);
		String text = editTarget.getText();
		text = text.substring(0,editTarget.getSelectionStart()) + text.substring(editTarget.getSelectionEnd());
		editTarget.setText(text);
	}
}
/**
 * Comment
 */
public void doMouse(MouseEvent e) {
	boolean showPopup = ((e.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0);

	if (showPopup) {
		boolean enable = getSendTF().getSelectionStart() != getSendTF().getSelectionEnd();
		getCopyPMI().setEnabled(enable);
		getCutPMI().setEnabled(enable);

		enable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this) != null;

		getPastePMI().setEnabled(enable);

		getEditPU().show(getSendTF(),e.getX(),e.getY());

		e.consume();
	}
}
/**
 * Comment
 */
public void doPaste() {
	if (editTarget != null) {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
		if (t != null) {
			try {
				String text = (String) t.getTransferData(DataFlavor.stringFlavor);
				String text2 = editTarget.getText();
				text2 = text2.substring(0,editTarget.getSelectionStart()) + text + text2.substring(editTarget.getSelectionEnd());
				editTarget.setText(text2);
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
	getFileDlg().setVisible(true);
	String file = getFileDlg().getFile();
	if (file != null) {
		String directory = getFileDlg().getDirectory();
		File textFile;
		if (directory != null)
			textFile = new File(directory,file);
		else
			textFile = new File(file);

		try {
			FileWriter f = new FileWriter(textFile);
			PrintWriter p = new PrintWriter(f);
			StringReader s = new StringReader(getMessageTA().getText());
			BufferedReader r = new BufferedReader(s);
			String line;
			while ((line = r.readLine()) != null)
				p.println(line);
			p.close();
		}
		catch (IOException e) {
			postError("Unable to save",e.getMessage());
		}
	}
}
/**
 * Comment
 */
public void exit() {
	// Do cleanup...

	// Logout necessary if loginID != -1?

	// dispatcher.shutdown();

	System.exit(0);
}
/**
 * Return the BorderPanel property value.
 * @return BorderPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BorderPanel getBorderPanel() {
	if (ivjBorderPanel == null) {
		try {
			ivjBorderPanel = new oem.edge.ed.odc.meeting.clienta.BorderPanel();
			ivjBorderPanel.setName("BorderPanel");
			ivjBorderPanel.setLayout(new java.awt.GridBagLayout());
			ivjBorderPanel.setBounds(5, 5, 480, 410);
			ivjBorderPanel.setTitle("Meeting Messages");

			java.awt.GridBagConstraints constraintsSendTF = new java.awt.GridBagConstraints();
			constraintsSendTF.gridx = 0; constraintsSendTF.gridy = 1;
			constraintsSendTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSendTF.weightx = 1.0;
			constraintsSendTF.insets = new java.awt.Insets(5, 7, 7, 0);
			getBorderPanel().add(getSendTF(), constraintsSendTF);

			java.awt.GridBagConstraints constraintsSendBtn = new java.awt.GridBagConstraints();
			constraintsSendBtn.gridx = 1; constraintsSendBtn.gridy = 1;
			constraintsSendBtn.insets = new java.awt.Insets(5, 5, 7, 5);
			getBorderPanel().add(getSendBtn(), constraintsSendBtn);

			java.awt.GridBagConstraints constraintsMessageTA = new java.awt.GridBagConstraints();
			constraintsMessageTA.gridx = 0; constraintsMessageTA.gridy = 0;
			constraintsMessageTA.gridwidth = 2;
			constraintsMessageTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsMessageTA.weightx = 1.0;
			constraintsMessageTA.weighty = 1.0;
			constraintsMessageTA.insets = new java.awt.Insets(20, 7, 0, 7);
			getBorderPanel().add(getMessageTA(), constraintsMessageTA);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBorderPanel;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G1AE603AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165FD8DDCD4D59AB715ED36399B35DAD6D4D464D2D231656ED20DC5E5C5C5C5E5456EDACB45EE34D1CB3BD4DA2417E178D0D1D1D1B1D0D1B1D181712B9031C481A531A69991130C1199E1E4E046194B070A7ABE671C7B1C39F3678E8C247B3E3F5F4B6F77F06F791F73FC1C67FC5C73F56F88594B82D313355902101CAAC8FFDFA58BC26FFD02706944C37508E558B5DAC176379CE82EF007DE87579C088B
	DEB1DE1BA5B4AE4F00F09104CD4B46EB97417DFC612765C577400D97BDBA088B3BA9F1CD7FFAE6FD0EFA52E9B83AAA932E3301CA81B3G8582E58989D5B9925E8B615952BD21E410DAF1022469B81DA343E325F4D990D7G146FA0E9E6D667C97143G8BG1A8414F002254D00B45329F5171617C9691A2E9AA5A4BE3DC2B70B681249C7EAF3B05E9152B5DF38B305B8A2C538F3DBBEDCAB746D2DDFD7D4D557D49E28B3FCEDB6994B4B77191DCEE32BF91739D3B4BB1228DD3FBB1DC1F984E1820407BB292DFD44
	97BB603E91486EC45BDE18D5A1699E59BECA4853D8533C599669E4B6A5B8DD3609CF17CD8F94097C4D551992DFA9045B00185DFE691B50EE1B392344ECEAB5433F921B29252443EC96AD1D2DA5ADD68B982F4C0A407252D70DF90995B1AF7BCB082E3CAE0DF6B3D0DD9762E34E905C8461BE2011DD18476D2BB10F0D27030449C72BF5F33C64B1240B65EDE217AB0F1337BE1D3E1856276BF2C91D486A863DC0FB002CC0A300C200F2E9BDB6FC3E962E15C745DD360E8EDBE7255D3E53EAF26C33194CD638AFAF07
	98895BEFEE31B9CC02406A6A6853C5AA76A0CD68F62F2704D85E8E8AD837C2765FA00862B6DD2ACA5E463DDC200B16E444F73B729656C37C16FB1678AD936E895F8FC08C2F23F8128CE7E5BD1756A5928E6E85DF1CC6BFB74EC6DB6AF4D78AD99BF66B52D5EC997DE805AE7C8C33A17A0C4B9666E3679262E35DD9E8E3C0E5C0FAA0BB107FD97471CD1FEF9A484735248AF59A2D75DDC76DC60738334BE2B5C11DE23EAE2F6E4F57EA4478768C1A0F79A5F8133C0CA65DD7236A17C9416BF4A1E719BFA61CF57905
	693E482B6E58331EF5E3B6AF43A7E8DB19FC161561F40E3FC4716CF3AE1C15EDA345C9389462C68231742F533AB0D9CADF4C65FCC671A6BF974E64045036C8426190D7F431C64B4AF76EB252E70E3EC4239D879489948B249B86658D34F9981671BB1F2F77319D69E5654BF8EF4DDD88D7F32F68B056F6BAC5E3E70B39416830980FDA4DCEC1CAB3B6177875289BDFA8ED5FAD20E33739536CB00A96DBA7C8B7F61821B2B9D91A7BF36712B48EB30DB5D8BA4C202043AE205CC7F2674355EAF40A876CA623E8F625
	E071CF65925FF69B9BC0A288007BE143083F6E21FD36816E6D4350D7DF6793AC8242B15CAFD5F973603A97AA3759DADEDEE5E9A1E6989DA7310EAEBF9B8857C4C85FFC29C69B81D474ABA8C720A4A053E550D781E58D57E8FB01E20076DC2E510682657CDA239D8614G54FB85BCDFC7C098A863B7503F82A581E57D961EFFC0E5C0F5C04DC0A210BFD090D098D0A4D08CD0A2D05615109E288C288E288DA86ACA6CAF5EEF44F6D1B56BFA216865EA55F6E1A83626697DD99DEF73D7360B7DB43F7E000782C581A5
	GE5DD2551168269016CC07EC021C0D1C089C0D9D7C3BC109E488E648F948A948514G1475BB0887B2G09C0230142002201A683650C04BC81B581758185GE900A681A581650D52E8AB001A007AC661F8E4D11F1FF6F07D87A9037E626B243240B0A9F7D2665E52077D36FFF9241EA8B1D2DECA2C209FB985AA6989117A2506E7C969C9DDC40C54D1F2CDD07018AEF38F778E7714C96AF364G79C4A2ED244D0B3D243D10FACF5A06074DAA72C97D76175A84E9B372B852B6062914A5ED07D73036A54A5ACD1997
	1B5428C8D6E6433C78D8CE2CAEBEB0683A38EA0078ADC01BE46194C91737745F8CA0EF268A76038A36219F9935DE70A5DE705D52F5118C1BABDD17AB524ED104972848A37DF8250F7EA563366F3D4411B179BA01BDE314F16BD530FDC05B003E82DA23085B8E34DA0507BC37D68A6C193D95E897D0B550DE9F6DA7B4DDF63F4C07746CB997321C0C9582201F8C830A9E054F3AF3F979829D836CB4BA4D78547F5E818FEC33497564E3E97B161072B0F7435C214AECB7F71A6046A9BDF3AFD9C26C12468347EC82BE
	2BAF27B8999F6E0287361363D7AE194BF010A19FDDE07C35B45EA80A8E4B51AE51DCEF9649BC4645FFB3354569B674F4CAF93DFB8549EB8264B1639AF80EDC03F9ADDCCA7C0F4F75BA1B2D3D4BEEA0A396E9FCB01A4E030AA0FD55B578BC09AC4902EB9B0407810D868A61F1FF21F1A4EDB8D0ACD0463598F72424D172BF6173D6B6A1D2DC1BCA1A517D70B19DBDDCC72A8F75A5450B1C7EF5441EA5BA1225B011C57A8C52D666882C5D921F12F639C82AEB6B253ACF5A84362765D23DA77D5B92295EEE60BAD24E
	919DC560BB83D09FD090D0A4D0A2D05EE8A88FA0AB50C8A08DD0ACD066F5B01685EA829A869482B491A8892860FA988B810957E3F95CFB26B959FD5C63EBFEF2E5B64B73A14FA376917D75137FBF9073752C334457727A166A233F174BFCAA77333C8C30CFAB7EFFA03F7FB70975E9394B3C7749AB16297749054B547B6412E57D7739DFAED36BF3D95C76E5240CEDBD1D32BEFC67B2B20FEA31F5F61A6903043388EC5D6AFD8349072553A29E3418DA4DE23D65149966655753B9B51D2B8540FD6875B81769D909
	F31902D3578861BF672B2E430C3E7D9BDD468D2C6F4C3B41B517E1B66D4AA74F8633555C810E5AE56B6A9499DECB6701926ECC21586CEFFEA63C05EEEBE6C5BD16CE132D2704DDF6D9EDCE70D161F1E337314458A3165012A8B9C8A32B497D1A7C62622F735DC518ACCE3B4DE9AEDC533026D4E03ADEB5B4DD98DD66DE0BC895B1BD4F1FBDC875D4833C2F3648C88B2604DD74E647B11B2343ECD22A282451832A58A465656842C3CC0743E1F3FC5F3ED36C9C2A9EAA214A5AAA7758E6C1107434D79C26FA6A215A
	9BEA4ECFDE1CA0EB1F3315A85A0079B95C5244757C024CF8D7341969F9F061D615BAC05A0745E80536D128AEC76C3293A9DB38DFB24BD8996F6ABAEFC5EC3201B02265CBC90733061571AE1B7D64F9D4825208162FF8DE220D2C0C75C627E8BEFF0A28B822298C6B2C156959E5B59B9D67B1C3C49C5133D55273C67DA1AC9B7DF9AF1C8D255CEF9F179D6665F3BE95C98534E103246727D3BF394A67BCAA628534E1A34FD3DC7ECF2EB2BA1F1942C25A38096BBADBF9CA5E968D15C59D36AE2739047EA71D27271E
	FDB44A1B9A2369F817D32CB6C137EB6E6CAA2C734A3C0D9832996D081DF95A3B9DFA87209754108DDBA4BB4A2BFB64FD6D7F52999B304FF9F3D52F979B7E870E78CA6A23D7B83C58309F868F46C19BE1325178013417E1FDB851FCEC20F182296F5DC615B299E024407C61EAF8DBD167EFFFA4632C9A0B553C0F370F536B52604AC691082D5BC052D10772F42316900CBE09B8E6E9A53C6C490158ED95E4FEA0757292766B69E43CC98A275E8CC3CE23E8F370FE27FCA7C9AF755692762CA1CD522FBF00F21FBC13
	46746BDD3A427625CB7A751C37541E017A81E4582CA59E7AB7217ECF37E620FE1E7E562CF449CFB0E411527D3E07682FB7F61BDD7AEF281C6D11AF0C7B3A70C7D63FA847796A6108B45A4115221E0D8BC56979GC9E0442BB7F61A0BDAEC9DF6DBA711486F761B1DB0727050388B633DAAED1189C6A144028DEC0D660D15E49CDE8163F3BDD05B8D38C673F8BE0947B59AE9A1EA174DEAE5D388080B39014C8F6EBC1BAD9B737B5D404E9EC44215452FBB2796DF980071A12A7195BC3ED810476B83D87CEC2E7F10
	20466F4F797F2A9A9F44638FA72B45077178C92A7111BC7EA72DDAFC8C0FFFDDB5BE11477F1C22961FF1A3464728466771F823CEAD3E14473F299ADF4563DBD255629BF9FC2CEA3C1547F74FD50B7763719F2B460F64713D73546203F97C9455F88D0F3FF43EDAFC94443333ADE959EEE7DBEE147CF7937868A61431F321DC461E1BD81AE1B7210C0CC5EAB2C6C3FC8817714DE2B59993F8FC6A67EA7151BC7E995578F89EFFD52EBCDE38D9598E46672A71D75D0CF938A4CFAD8F0D90EF3D99F5BC30D4558FBC5E
	EF19DA7CE89EDF132F969F4263B317771F073265EA7C611CFF2ADB7CC409DF97087143D62971677078EF3F68DFFF7797EAF5290873FF2C9A3FA3907B4CFB1713F5E82779C417393345CC9634771ACF62DE7A72F90A388623354B35C77F48F2329EE7152D05B3FC5C8A75F51287D61073DF6DDC7EC3AB48732DDB124956F5C20DC4265DE169B0BACE1245FD693C57BD55723AE0G7B4959B0912E438371ACD3E69FD99BF4BF25044FACFDAFF966621A0077E73E7BF31869DE36DE2EBBD052BD812E5101D8D717F449
	53A4C8E932EF016746AD186672D372B4BBEEE1E95A605A8714F48BC9D3FB8E4F2C0D3C056DA504C86962EEE1654E52E58A18EE4F2D9AEDB3105F2D6E69743711F4D1BCDDB844C783CD3E156DB9145DA617B7B29953855C46640C132E11EE693E53E23A9840A783658431F40DC172F473D2B01D9570E1C12EF4C1EE691E6069B400CF1425CBF4CBF7CA0769B2EF57E88BEEF7252B3ADD1E2EA49553B581AE5E6E4D3EB7F93A1110A664F6EF76DD130669A2A1CD6C6D5E6C7BB69D536946C0DFBD461BFDD3F93A0AB1
	2CFC8DE3D83A11E364699E4D40F42100C7G45C9698AFFAFCF772B6918EE87600DC0FD3F176A27DB3A3A9918AE8EF0A1D8234D8FE6691A03656932E6E23A5EE0164EDFCA9761166EC51ECE3803254B39C3AD5D28CCCCE71052D925F441F74853B54E42F413GCF844A3ED34D3E3C59DC9E44DB6F7426777ED318EE0414CE190F20BBC93A5778D94C759CCB2458BA6888DAD3710C583EAC7999D11333CCF57D187A77CE490EBB15E767962EA2F27346C27FBC967B5F321572FD45CA275356E221C717243E746D1572
	35E506452F1C8157E39607D32C6DB419FB31BF7E4FCAE2ABB95BA441D21FBAF69D59D3D61E55A27D1E4D015B363B585C4EE05BE5B31977C0173E0BAF3DB7D84CE47E4E4EFB3EF1967D35E3AC2E2D1F29111FD1554CA9564D74621B5E314CA723473AFC33397C69F4A67B68B9149D8D718CFB0BE3D9A1584F6BF72B1DAFD44D0FA297CC263F00B27704201E39490879F16CEA0E05F1EC3E9631840E0D64D861DD08ADC8C12C19E357F0ECB4479669900B6258E80EE55C0D5862D46EF70EDD4F31DE0E6526A1964A31
	3B66A296473112F908655F0358BD9CEB64D869FC447CB92661D844BDD8A6DFE6103DB7FA0E9866FFA516239DA5DD5D9DA54623B06B0302A10707BB3B9AAC4EAE2315BE5531BEFD1D1DA155D3A1944F89DF39000C9F4AA02C87EA8BC59D2F27978F2C23D6B4F72868B9391017EFA866E347A2549D46F50F48217B7F904E78837411FF40345D0B113F0CE3951FA3E66718B6973160BF205D4E13DF76EFB71F4A2BE50049CD64F2E3382E8773507E1CFB517E742544FEBD045B007C6E45349FAC65F50FE3FFDE466B9E
	474E64733AF79FE236653C6E5D07F9FAE7610A815BE0ED8B19F4B399DBD69099E4EC554B65BE380A57D50E5D75852F2B9CABD94D6B6AB854FFBF1DC3D11F6272C2F9F98DB98BAA35179A42C37A2746F1681B0F9A08EF7CA19C8C944EE5B9B7AC7014D5E5B1DAED2D3C7E9CDCCF6411FEAD1A4B3B7FB8111775C7F8A6836D78A34A5B1D1963A9CFEF33F759C979B2F9873AF8DB7BA34AFB270B5A8761E020F0AEAFF46BE2153C12A12A3B0D0F1CC29FC7F3197F25E741337E84B6826DGE267FF9D67B2253A538818
	BF909B7F7E659C5987986C5EA133F34BC622DB5A51131E1B374E5FA1757D11F7DFA9D41C1B2DFA36FF781C9CDD441F24B305FFF2BD9BF66C55CCB7GB11B3788E8F3451FE1EC77E71C637C21DEBE478981BC8AA8714F1827G016C838CFEB98C4FCACF21FAE91B88839F06215E2FBB65FAFB818FG8A8FC33D4D49C3554B7CB9E691F94E3B36AFF1ACF257D239CFB30CDBD4FDBA621DC53A09E152D8A4CC66D383785400BE9D1F0CBE2D3A9F460E77236D85DA024B778F19CD171710BA069B07CCC60F96E50C837E
	787B797AC10332DC8A9E50E877BC00BADE577EB27F5C5DCDEC419DACE64B3694342589740CFC002FD5285A9279G36314DA726B3D9EC13CA40327FB7179783E9F39E64EB9B875564699FC4F96FD5533263FBD1A86FB69D4AB3C15AGAE6F129F5564CD647262CB18BC5CEFC2F987D3D1DE9C244D57203CB15DCAF9640CD923865B7F4CB4643382964479CE742871CD62FC8FF33E84408A47A35FD73DEAFC4D63114F190EFCA2E0A11C6F3113EAFC311CEFF58672CD862C74A12CBB3F4D58286C27FC5A3B403A132B
	A37A5C37AD50276FF21DED20AF70A134557C03DABF934DED5ABF7D1755671EA52C8ECAAB6E3C8E5EB39D6D0987DD858F63FA425803727504260759FC328F2E7E8F23CD8BE710F381C3DE1C97503691D98B299C3505F93433EB865A998C7AE39EC63F556E147BADA79C6AA1D0DFB85AB8E946AF735BBE678C5AA761218834E76BCC34479FF4C5F0FD01B3493BBA035906E4FA4414A2DEE7FC5B7AE3F6FC4A6D909E51E84B9EC1BB0EE6963B5BB170D6A42F938FE522CCBB488B66B2D74DAAF117694356A27A715A1C
	D5347E79302B486D38E8966F9F41061CC7314CFFF4FBA66B9FE5E5BE62D134B3EE56AFFBA6D74FC63D01A0B31A6B3DE80DDCEF56E3CC2F61B16CCF5EBC455BB6419E43B96C410D7279FDF8CC115779FD74E352FBFA0FA967775F52F1EA46840DB6EF824E6FBB8A4839B81C5F6F073928313355EA9678993CD5449F6D6613D2F859AA22172E098AF8A6721FE78773DEDB6984FCBF696AC2620BAA88B7837582798305F25B462C9A70BD3D6A5EFE5E53EBBDB7983B22A660F95A38C17145F23ED142E078A6F33E0549
	03614BFC9C792E9D94DFBE674B518E06EFB3673BEED0FCFBB85F67A90361EB62FCB78C0A4F4E7972F403619B4679EE9C945FE84E37B4F5B0FCA11C6F66C17105F33E8DF38743B711735DBBA83EB84E37E95EE07812B85F38C171E5BD01FCDB668F062F0073BDB2A83EB24E37AB3BBF3E3AA7DC6FAAB4CB776C5D551405242F961FE06F2EA6F3BC1362FEB21C756D239660BB6EA3A0AE016BBF3B50D73B8DCFB27D0C2FFAD1FFFCEDCF3A6C6EFBD2EE3730184E631FD45ABD1D6281CFAA6D3EBA876D8E06384C8834
	7B74E2DF6D36C7486D2E7C5CD73EE1EEFC69393E720DF6631B62B3DF089BDFF8DEFFFC6191AE7FCE0C107B77B4DD630A0BD07AF7984593A3147E1D35947DABBC25511A1EC27FFE3454D73BA31E125B2DD966AB5FA4B73E0B73FD65CBF06393177B4A27FBDA4ED73EC2491773344B27C5327B9DEEFC612BFCEEAFEEFC77FEB1187E216DE95E7F2D9E8CDF9F67DBDAC3787CE57988126E43609AA94311D8BDB9FE1054071827D9BDC96138C87184994E6ACFD2832E7F884FC07DF98675B72DD75A9D718C53B9892E89
	523DAE12DDF3A271DD7D62F6A22FA8D2297FAB0AD7C4AA75BFFC9C7557C1DCE8A46A7F314BD73FE5BFABEFBFBFF593F9054FB2BDB3B95ECE71BDB2DCDA83216B12A45C86F1114F227EA7CEA97517BE87F35067545FBDEBFC8E735FCA3FBDE0FDCE59FE6D946FFDCE59FE2774E17B9D86F1F14F217EDE0FF1E45D736E3A5B94E14677AB8F3E92C5F804020F0D27635610BD45A0088B870A86CA844A0952E8B783B5C661387A43D5EA7BED6E63EAEF1BED2EF799192D5BBC463C3E65315603CF19475EA8F7BE693D1A
	FA72AE15E22D9937A524739097FFAFDFF77087B921C093016222F04DBE2E9E57370597303C268DB8FED74F4B2C814761E5AF28656545F215F57D1DDD22C84F82323CF436AB57D0485988BD48B33D00F97954097BACFDAF605C6A67F524AE0C03F094D09C106EC50D3690288A286DC52C8BB5DFEC7331AE30D92EE7CDE0BEF84C43873E550397BC781475E0780BEAFCE7BC66A23E693B4203CF29AFD0210F15C31FC12DCE05C15A489779FEDB837F56500BD827AABD66B03E59793D871F524E3C175C79247D3D03E8
	43661750068F07E8C30A47FCC8E903D5550688EE43B0EE439D9E32FC33614F835A20D13561019F5106095C061FBC66DB3E59506E412734A1F3221A8D2FF5238D8593510639E9C333E119871F5206A6D59B02FB5086115B707890EDF8E9C09BC2D4ED686C655F9363B61CCC9F1A8D17F970A9EDC8D235611313E8C3764BE8C3F14650EC5866412734216EE5B53E8F0628EF5A007ACCAF2B65B960874CF39F4F739F278F4D0647BC7814B60CD358401EF1FFA9D45987A74B73D6697C4CC15C930EFC9977042FAEA763
	A95DAB9AED9E5066D778FCF34650EC3F5C034F233CDED133BD27FC23606B37678C963B415CAB56D84CD693BEB3EA37491F9926D7587937A038063D024F8C3F1D187FA81E4FEFE68EAD1F7BBC7816AA42992F2A71A5E78ECD5F9C8FBE253EFCD5FD9306286F45817515296A3BEC5650740D7260D3569B7D2BEA7566364D6B87D3EF24919F2B8FD755E3FDE8FB956B43E60FF90CEF766F7460D37AABD0557E02D5DBFC369F5F5D033ADF69B09B856CFB4256607916B01E0F38A16663238173112048879B835FDCC477
	BB22E18E940D635DDB3FA0E730F13FC3EF33F412B3D8AC6E7B55643C354DE1E935F4A2E620D80B4DC1DF7EE75FD2790C1EB1AC8419D5DC6E75055E3FD5F0CB217A378AFEDF287E2D023B8A7B7FFE4C9F8B3DFD4B0059F7769C5A57884430E99C33F2ACC4C04C0FE34549080D64585D9C8B6658FAADE29A0E05F2AC0AE39BD2900B65583D9C1B4C31CDBA44B27F0258B80E65F3ECCBAAE21BB976A70E6D61581E3908B5F16CA90E59B9766DBC4406F16C990E0D66D85DFC44C2B8964D31F00E7DC84F8BCE04F0BC47
	96ADA0D856DFB55A023FE2DB18CE362B257ABDCFF6FF43827C6E4D05A0E9DD7E026AE0F99E359073DC7AD774C353A274CD85479667E03AE66E1B3445AEFF4C115D0F4E39703E794385554172FC75E24C33157B2171F344FAB936B49731E19C4B4AC34C1FE32F6621DF83B8B6F289268B67FE3DE8294B173F165D4FDFFA617DDAF4C1F530BC4FDA06F90E64FEF809E3519C3BAE9F31D80EDD319C31840E7DC04F04A6C1B86FEF1C37GFD3D03E38D741C2889423D9CFBF5954A9B4131273E607D8A47581EBC6DD7B8
	36051E8D8D7F9BDBE3E0D86DE12A9730B81E6E4AA602E5CE02FEE592E2E187503E32C9D86E7603643D26A69EBEC543E4AC4A425A03672BEC088E75724FBDEFBA543E7545726E24E7E0C530E1B877477D25848B0170840E3D5A06BE0A6658E48BADEB88E73D06D865F10295C0386CB53E9750CEFCD745435BE9181C2B146656473D6513701C1F36743AD79DEA3E99DADBBAC14F7FB6034DA24FEB8BBD3367876111C041C09A20E89EFF02460B527BCA64DD247857583BC652BAD4175C6E11922F46873F78C97A8676
	4D6CEE398EB21EA57B825387100F3ECB7749B72F51F34C192F433864F52CABA5FD842B023001E327A95686613E5751879DE784EEEB376CFEDA5F056F03F34EDCC89DCB288F1FDAC84641F6984E1A1DF461384AA89A253837C5325E6D714D2C6F0C9DE6815FEF3C675CF4A9CD35E85BC53EB9A73DBB34467B78FA4A9A75717534B5E42C6F34F5B9DA4C4AB1F45A5A9C9E67DA53A61B88449C16E64EDA42AF9A9D2DE60FB178C24A4F623C71AFDB6BED1C4E726A9064F95DAC607C6B0D79641DAB15BD8EE1526B24DE
	3554603A42F088873E4E5A4504575979CAB67F4A517335473F633ACA081E2C2BB4C1D8849A71F72C136F557B3ACF9774F7260B597FE5324C7EFD242C186EB16D7C3D952E7B328E3AF789610C98E8B7B128DB907B53DD9663EA73F552BD732DE51D777AE0DF27DE9F3A57F92F8FE7568DDC9FFE354EFBFD784D3A016B43B56B7A2F8F1F487D394B754D2BF1DAB97EA57773B883FF07AB867D3C67G7173E8880782C5F0BFD7B678DA46B1B172B26ECD116BA65F2E124EE29C74D5DE221B3CA21DDCDE9917772C4F72
	B25E104B0BF11337154BFB78C7DF656539499B19BAC3A60FFCF40A49FB335BD7F925EE72762439495B00726E6A71D5DE151B3CA4F7F99BD11E3357D7F90DEE72C2535D64ED624F23133E4A333A49B3E53849635F59A91CAE47B7F23F5E72032FFA7C5C74443B4B63FE0D7328C7A3A53EE038EA5E70F666A2ED9BE9B7D1EF303D7DD40E4F25F82C8CE7FB7E234B711C6EE408EBFA934F8D5FBBC3DE9FB78A5827BFD0CF7AC75F5654F65A4827E168A23900ED7D22ED38AE987EA63675CBAB098D13A11C7516C6DB78
	963675CBF7AAFD3063AD16EF83DC5B5E727481333DFB26DB59FD09F8D9269B7E95621F3A637CBBCE8F4D127BE01300E5F30D47F326CFE60B3F6CBE68ADF9D9371FF2533385650DDF632D6E0481FFA4D00CA4A751CD5E3253EE72B6E39F9ED4C44A183C174C4241B4FC12172119FE5B087571551DA6E971103D131DCD5757F6C142AE8131D194BB8458479CDBC8575C4E01607F6A90FB0CE2DFC1B817E3D5F44D6D9B603DAD853147A9F685E037F3F9B5F44D6D8D2DA0BC48F91F2458BB007D03E3FB681A5B3B004D
	C9C56CA90A7D83306D9C7B162E399D524965BDC3B123CEAE2FAE95B1173CE7A97633CEAE6FE73A66F69104E7E9910B615863490899691A5BC9409E61695E6458AC6E03963A66E6842C03277BB745EC001DCC46327AC38F99779FF398BB20E9555B4DADC2D3AC1D5770EF57E44432BA11A7DDABE2318D17AC206F9D990F52769794CBFEABCAA455D83A3F168C55A4F9CE7E9D1BB8C9CE924A7D07C64B52BF3045459B77256B3E384CF52F5F6A3AF7163B643F4F3FB1D3718FA6D7AFDD6DDC7EAD5BDC3CD3ABDC774B
	3FF65D7F780D6B7E4C8E0157E9DBB37906CA2B43E8EF3334B82B4C4E960745AE5AE0E09372CF223FED85516F8F3A46814582857E5355CE190D2185E8E322941771B63B963E0D6F075D33C96ED366670ED5249D6337CCA4DFD58A3CEE47507512CB20CC9F67754EC857CB4CA0EC018E65545590B99DB0C05EE9F4C8D87361B42A6F242D8B5AA89BB7779CC6BB7700DD4D120D435FE663E646B72609748DC70DAD6D2D8EDBD727C990225FA63C8F513516C0388F81E279FE7EG4A4B0B53E8CB0174C0F6A07FB81477
	B5BD5BA97F9219747D37751E765D423F63929A6732AFA20E1D79E1FCD1256A764551F50E98C89B9F07763D5F0672325F016783509E20B620616F203CB947C939F458CCC6D2AECC56974779BB62BCDDA3CDA7DAC47AEE88AB2B57F68A3CAC4B364CD26473CB9EB73C4B33BD89D5AE5E7F4DF76C587BB71F393D8798768EFB1FAF1E67B37114E7394C643F65A67CCB234D7997AB173AFF31EF62B33E6ABE9D7323197970F09F7A5074AFCCF3F51F278F5B48CFA4C8E30AABEA709C61F060897F97361DDF57118D8215
	79A0296E829B8BC4D05E49401375AE14753B785ED06F97786D0B10B1D78A058F297FBEDB0C21D83763DD16AF13F4A56F8D45EEFDBA1DBDEB1768FD9D27853EABBF4B197F8331EB5C3BEC7C144E71A20AC74AF06603FB699C1504E3A1EECFBC2E256868DCA784426171B84E995B2E34A9B25E55B74544FB9B6BFDCA672209714A31DEDAA741B35ED30E75FE5301E33DBC0873FB0F6F6B08380E984B3129FB893679FD307DFD3473FE034F67155F177BCEC367F2ED6FAB4F41BECD71616FAB4F41FEF7G4F4186C3DC
	663F795E82BD17E6A7E10EADBD0818BF47E2B9964031D7FEC4AC18E3D3683926F1900E6058049E44A6F26C573D0845F0EC61A94462B996C64F29CC06F0E6825F2F243664C03808E3E78F51768F61B60E6DAE47B268CBC0FFE75231F2B00443B93608E264DD3BE80E5DD47ECB5743D007DA1CF4FE6D976BC8913C7F8606F4DEF81B32FEA549EC4A7A405D3E028F701C33EEA729AFE59FA85BE69645759FA85B66951558B65BA0AE7283AC03GFA062574BF709C7D8FE25ADD342CA05C4631AB0AF9D9F16CAEFA5EA1
	9242B19C1BEF60FB289C1BCE4F93D47C971E317FC5ECA2451AA0ACF22C3681F907F36CA0CD978D61F80E5DC2B1A111754DEC0EF5AD5D3BA5F94E873CB1110E3933654F8B32FEF3290E1D83E1719F3B4553E7294062D97F7B4CAA6CFF131E39D1307EF133575F6E8CCC14567124EB786F8EEB75153199CC4E7307656FFFE6DD1F27CB76A2A7C9624F7850550F337CDD378873D771A1666F49C54A7C1DF64B5F14C54A7C7DBD1945B31959CBD02648E56EDC523FCCF109D2E6FB8A0BE74F49CBD610F5E27E122D6BCB
	A2E4CAAA8C7F084838E3C50140FED3E339646B69122E9030238C5290990DF0E53F8FFB20A0942E3BEBEB4A369A21068B77CDA3DE7573A37F60929E9EBE6D53044040A0BFBF3FE9A4EA9AC1689D7FDBBE0578FC86BDA4AE9099BBF4BFA5C4540EAD1FF94F2A0BF7C77CA03CF86C265517AF303EFB6F71954F0BBB8B0E0F19393E69F8C0CA72444535BA3FA047F559F929D1411B57AEEF3CA73A7D3B521C04D3B519CFACEEDE1C19FCE4F62F30DCF70BBD21517E57475E569C797E814354F9B3668A5B18BF2EA1D915
	6A536199D81FC6CC9AA9E487A526F87D3D550FD879A7FC642A87064B562531F2FB25G4B2D70E3ACB75DAAE539FD10A6AF37AFD7A94B6DB529DCD97FF073E19C3FB4034C519F6378651FA5E4BE2DBC67CE97B96A58C00B0DE1A6D17EA8600B8392A6637C666DBD822F9BF79DA03F1352E96E496775E3174DEAF394EEACADAD12E80D642B409EF91BA91B31516BBB53B913190F0A707A78E1E99D7D438368A753E47453CA37EFF5903BECA9F2BF9DE8D07A113C976F0A3F4CAD1E587A9B3776F1FF03524F484F7C7C
	54FA74F3G58B4F1B22E87251592BCF30AC6DBGD4B1857DB772DB177F1EED4B15372D7A13B0186E209E143ED2016D5A0279B6CC616D5A224C779B49EE6D5A224CF72EDB7D99E3D166FB011B5F22AD4AFC9F96647D427663E40CADBA3A4C2CDE9D4FAA44FA657DFBDBFCDA407235191F839EB1855F395FF49A7DB9813024A9D8EFDBD72C6ADF7EF7DDD62BD403F19CFE1A4A5A7C09C6DB87E47A84477E0E3E9CC135CFAB292FEC28E64F712BEA86731E196B79FF73207958F33EE2B731356F9365B8F8B7457DBFD1
	0E031F2B41F1F09044E5FC0A6F1ADC3C17644D8A6161C001C0E1C051C0091F627938FB2A49F71B7AFD4F44E03357193B4DD65911B7762C5ECC7732B212B45A3CA49C3BFDC1678F05902EGEA6478C60A136FD1DA1358F7C7999E26EF14522B7983E58CF6EC66DE8EAFD09D5E7660D3050177177BA3D60FF28D1EF369BC72C9FAE9EF7A51F6BC83B1A28947BA959DB8A68A65FECA6EA458A488A7F2AC13E2649B2C991FA1763B8EEF6357C4053FA68D41FFCCC708D79D4AF16FD04EB7B4FDC6776745AAA9DC781927
	1D78BEA3D28524A134C00A53FF66FA670F154302BA4FF2687E4ED58ED1861CB785F1DFBEFA0074EF21901EG948D948F14B1956323E8BC79E6AD799EAD7956EC5ED476ADD996EFB800FEA2BC4ABC042A64CB0D22E569A6C8B6384A22036BD07B7DCCDF7DAF2F6744CF725F691BAF3C2DD2072FA3BFDDA95DEB742E7B0FFEF05D97548BDCC6D60F270C5D86D75A6387DC77AFB6386EBFBC6812717C8F1EB2F28E396297B6FA2E8595B772755829ECAD48G5736296CECBF639BF5C8FD0D6EAEBAFF750334A300A227
	926CC8F9FEBFFE8C134AD97ACEEA81A993796E10644FBA220FFC1C07FC873BDE0F6F9F3C38C67E7EC176B4E6E385DC755330EF38114E0B5BA05C47313C6D58DF84F2ECD6A541A6C0B8FA9A564799F41E988B6149C01942F87C1E89454937F449F7F24937ED2567DB2537F6CE7849154870660B58E93E1C7F39532B0ED4D93A7E7A58017B607B686FF3086C0D8F295E6CB86C2AB70BF729177F86BAA7AE00BC168231FE6956E26C1774AAF9D776CBA6C8538B34C04A4F8838F76F17A654FB5AC6FF62CB6A87579AD0
	5FE895FDA98A6A1569A3BAC2BC74652F71ECC3D77D6472453BEB54FD11321678A28C64CD603EE8EEC05B923C788247A3C436AEF93CB6A71959C642C549CA5BBA699C1F8C5548475F195ECB576038368C52CBEB4DEB48F99BE70B43E6359EB5BA9AACCE4BD10B55A21E146AD4C03F5F87D1A3B67FF8A49D67EA639E38D6887BD5E20A376F8E57A5337CDA25EB63550769150D2F6EBAC25AF1G440581CD82CA844A500E576683317155475544C67236AEE437D2940DADED740B4CB838A21F97F641E4C30C490EA2BF
	83083FF3D8C3463B8D46632ADFCB7D96462E5F9FBDEEEE918D46D65E5EEE6A779BA45E5B6962A50365E37378A7D7784135077D1DF22C20FF2BCFDEC77EEA5345AA7A17FE7AC17A17FEFA6546837017FE7A7469D56C3AFAC103F195DC9FDCBEF7F89739768EAF245755FBAEDE10FEFAF54F2A552BA17EG7DCB6FBEC67F529D34BC05553F1774C6035F33042495B8DF598CE1328E912EE5654756A8C62D142FD124882EF50A3C14715A3694D79DE775E559AC79FC557ADC06EE3E177AB2DC628D132E818107253178
	DAA329AB7100A781E56946EB8B01AA009AF5D8D7AE29A17349CA7B317DE6F660E91F315358CA3F608DA5EDBFD65B81A179D620D4E6FFAB98DAD95FFEF8E8FC778D1A0F4DDF74CD6479A36A46AB3E5FF00862FEB2DC7A669ADF379881F189C04C170623242FB0240E575AD351F7C5356B3C3591G758E1B5D6C90CFB2147E9A051417CF8E8C4D874FFBFC4343B73E57864D47FCF40A2EA17827AAFDF7894583D2153E4BACC55F85C3DCE69A0E8196DB06E677D61F4F9D5953464B462DDBCF8FCD5F6EC173B1FF7C1B
	4E730725A97DB419627EE9CABFDD44579882A0CEC8C7BF6D5CCD5F9D00F0AF4776D1ACCA7A46AA43A6568C340FC078FD4D03C911D6BA87BA208EEF7B90EA249CCB3176F345DE6F67B62F5E2BFEEEF374DE757764EE5A5B7FEF7A0E596B7DB7FD6F5ACB72023F694B4681FB751EE319D49831E07CE0FF6F4C7DF764587EF369FE657EB3A96791D09EECFE71AE7F5EC7A0E0E1C0B1C009C0649D83B6E7286F486F476F199963E5E71D2A671171176B57EA912FDF1D0E382192FF2B27CEEEDBFE8653D985D736F67633
	0867FA9B81DBC66588746C927EB11F95126DED691976C6A60E0172467A8BD52FA6262C94541FE97E125DB152152CD75B9FFFAB0D493C71182B9C7EB73F3B47FCF230D1D9DE24CD66CC9FAF2D9D9EE844F6DD8418E9BA7A6A46C69CE786G4656861F21E9A7C2B89EC8B7E33C34BE1B52664ADBC539AF6B5BD27DAA27F916FEC8C87A9DE9335C5FF94B92D266FA71F75E8C6667BDB3DC7E46B61EA25CB6CB651B6E75F408CC261E826E7F4CD80B6B29FDB3B01F075651BDFF88C782454E40F17638ED1E7369E778BB
	72C9B3D8FD4F1E89E396A032D74B4E31EC5A0EE9F6GEEG9201C6820D1B09328BAABD4FD634DBDCFE5D14CF5BAE7DE5CB44C4EB3A04110641303147268B7CB71A8C15DC562463AED9179413BA61DEBEA5BDED96D17A367FCB4558CEA64ED4EBA7895FF8EFBB535A856E5B438614131909F5253843157626CE17CDDFB778D2EF24F74C9BD0EED5262F76A5FC43F4BEE2F3698C20F33FE3C62B13781C0D192E32935F74015C51C09A20A82058CC9CBF3D7413743B8CAAF314EAD3ABB9FB6F940D64DCA5CB7F27A35E
	1F97630F10F3B1527302F035189B0C8E8B196C715FGFB62881DFF5AC45156C164A39EF9044E13EDF68AB26C05A3246C9D1656B611A51570D70F90BFD84D47A84C66A18BD77A494A4E4F4F4F4FF56FC242699F146A34CFBFCDC8A03F4D9598989444E2C97CB45996A339F3251F2658FCD47FE3FDD063CA6C0304D96335E5C02CAFB07761FC227450EF04526B7E6BF57EE91F457AG7FD92E79844B67FDABF7905B26B27B06C26700174D1722D7E2DF2A81BD246EC4CB7AD85DF96F8419AFA5G261B8DF3FB20A220
	AA20A6A0AB109FD040EC2CCB5FB7137D9F362130DBBA41D8DE3E2B354A5CEDE9B1135F9812AA8C1D50B01ECD64482A50495F49784AA87F1EB143AA0C244EB82449D12D89711DC6D2E7CCD6ABFDE309E13B0DE47F555269B4F7BAAD22251BA617623EB3E639475189819BEBB550B599F67F336CBE6E7005FF8FAC3969C26AE0F9BFF4145C7331991B8AC9F14DC7338559EFB7501F132A07041D2D4CBE3FD217BFAE175D4FAB3D703ED9F7C1F53035D47DC9BC8395B69B477C9F1EF665F32A6C7E325397BE4F0197D4
	87FBD6287D4E327BEFAA33E7D36BFE9CCFCC180DE32C79D66C5F12G5BBCE73C34BF3B2119FC63CEB1C6596D3018F69ADB59362C749C3562D84CBA8747A817D319C19056GCD1C036388FB07FCCC45743C5C2E724DC17E459369F7016A70199ABF874B546476EEDBC196744DD97D4F51D8BFD86FA46B20F510368DE884D090D098D094D0AC50E4204C39633585C095C0FAA093D0AF50F020G20503958CF761A49FA0F37B58B49E174416BB0326F2130BC7D36111745DC4C536146214D33ADC3643BF550FCEC9E7E
	4DA1D23EC9F3157373BD944F18271C1F3F404771599057BB8F5700BE907361BA894213017266C37D835A83548CA4820D180FFE5E5DD6580F1F593E0D4B4D5277AD5676173F60791E75056563D9BA46564C9F2F78C66B2B940F1461ACFFBFD31C04A7C15C0EECECDB7F5D06798E82ECD2B64EDF36D5E01B49DE00ED46D2C15256C158845437G539E5A0EF5FFA4E06CAC47D55B15E7C536381D75F8EC3B722C88991B3862BF5C2EBCAB72089B7FFBBB14E7C5101F79B53AD86E57C830ABE6C17FED0F71B53B1D55CD
	841E0C053E70DD6376FEE59E70148E40476A4CF5F66F635BC07B40635BBB6CEC0F83A6ABA76D521866EEBB7D0E5131E3CEB39FC75FE74F62D82DD3EFA38392DBA7337D484A2135CD6B2079D81B2A215F8928827FB48275828D878ADC086D287284E9F31E63BA7A2B11E6135EE8B50B22B48E7F4FCA12AF7C43F1B32B5BB985D83743C0F68217BF658429AFBD961358464BE16A896233B6B3997CA31662A069E094A9CB1761A0754C59E26C34DABA4DFA23C942E7BB082E23A7C533D3EFF654C19C43339DE43CE9B2
	0AC6964EF1905F59D996A45EA58E1287A865CE277451A7CB2F594A628A94F1ADA777991D6DECAE96C1FE76924BE23F2FE5D138C83EEE3A58BA3432DFBDC43E66C17331FE6DB37A3C2ED8246C4F5368597CBD0B147D7915FC4CE0003830C5787C3A372EBF7DF9B92EB6DBAA5DCB5FF220EBDFD5B9CC8F437CA856A4E12C3CDA1D5E5BF327F360765C63D41F2F1EF1FA4ED7AFF62A4FD707BB6573D5697DF4A7294B2D702CEF64692E6658A10EDDCBE5926C20DB7B38115AC570BDEEED64B6EAD78F459A713DC999F6
	8857DB1D24FDC1165B480612745B5ECE52B60ED92CD2DD7F93CDC366CBA277470354C60B5DA833BB1CE307F83D7A1D4F63045E9CF97D2F2667BD587DF7327BD79BAF7C38BB61026AE03ECA923D5747142E016BE3E617FAFD1C5765D99F97F5295747A5DD1E75F105A45741BFG427042AEB21FE6EB04ECDB52415B5B97EB71DC4A089CEC4FAF2DA3F5A81407DF2361889EB62F7B25BE8E4D717E9CE7BA4E7EE29D7D1DBBE1BA26F83C6361DB9DCB2F989A5F0EC173317E78B5BAC60C4FD176537F2478649CE5BFED
	2B40FEDA379866BB0B315C0EECF79B3F819E33387F71942BA37FAE766C27930B657DB443FA8C1E697A8C1E696EEA70CCF7EF03E73F7FDDB79DBF98BBCDD6DE176B3B493B35E4CC305FEC272733C4765359D27C61EED2D7492F5431704F5DE4ECE135595A3B6C86528A25F4E67A3B87B6677E5DBB2BBBC507456CE4BEDABA447D7822C17331FCDFC2676609D08E991FE3B9255075E8697C286CFE147542771FF7DFD09D52B75B6A304F4906BCB3AC1EBE77CD1F33B58336C6E07C0214113426C59F1D8C4F6B1D6542
	7151A9493E33113F17873258DE4776353857618FD890508420D8A0DDAE2ED1687612FA02677D741DD6693C5F6CA279F93F82C8CF5EF5B34135B7974B6B2D8AD799454B6E3BAAAEFCF97D76026AE0BE6D5A0E6D154C9799F6EABB36D7B287E5BE3CFE3F67DE6F3B64538F52B93918BABC0B661F6B7DAC9A1E138B02B4E1392E33E811394A33E8A9DD5E4E18B15D33CF915DECBE5D3296675E5139FCBE3DF60079745A816653EB15736931EE712B57A96753AFCB72D9BDBE301EFCG41376F17701AC94F2832BC5594
	61DAD2D69E2EA75CDCCC476E90EE866A4D437504397BF0BD41BFEF307BD39BE4EB27ED7D2E2DF82F27FB0B7A630B487336A6B33F0464E7D21E725917C77138BC65336F76E2FC76A5C2DC6392FEFEF61FDC3F867028A53EBC7B0E96717711392C45A5AEFF2C105D0FAD3E706DFD428555A1FD2FF81F6773F963BE797319752BA7F95F1831F43CB68F2882288928EFA956BB763DE8D26F8216E23BFB35C8596E5C5F651ADE2CECF749EE715B0A156D4E1D3F23D8596E101F79B0482DAECE843B62167AD2979EACC23F
	2C61D8F8917A05E0EC7D647A6FC8BF92721E10AA8ABA32BC8F97F2E205DCCC6412322B0B8448498AD1C82507A960CA8FC97816E686B9B013DF69ACA9A99105B4FA62E4B9671CCFA3256DF6A7CB65F488335DB73113EFDFF4C956151B3A34C2C60D10F64CE8353799EB05D43AEBD7A74CBCCA279CE4ED55AA8AD974F7E157836FFA4A3B2DCE5036B8C5A1570537901C45565B0DADE6C8B25DE4F434D389A70433425C434274E30E5623D401A8E88F579C8635B4C422675789B344B6B37D58C5AFE4E211CB341E2C6C
	CA915BFA6D243DCD0B08083053BF48D8365A1BF7DF1FED96CB9E7A5DF0683EFA09FCDC51CAE7EC3CC4D249AF5D1B87F0376FE5B1172C986643C44B115F418AC09496501F784D97ED7964670ADD380E1C1210EA88E4E38E4FC6AD1D2D47F4EFAACFF96B36942D202B955258EC31D65011A99F1493AD1EDAA185E60B54A5B364A4D585A74F384BDFD0187B5833055790D9B278135CCA3055D20BC917FC176A0626A2292961A985190CECD1C8AEC6F2CBA1371E6D332FB9A7BF37BB795279DE5FB30DDF415A2E3052F5
	66062D098652EFA0D4G5E389257A993746477173D1D472E6FB0DA2D22515ADE59E9A2E733F71235C7816796377FC064BBB10D944962C2FEA0F69F17717C9FD0CB8788BD1B04F1B5B5GG2033GGD0CB818294G94G88G88G1AE603AEBD1B04F1B5B5GG2033GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGEFB5GGGG
**end of data**/
}
/**
 * Return the ClearMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getClearMI() {
	if (ivjClearMI == null) {
		try {
			ivjClearMI = new java.awt.MenuItem();
			ivjClearMI.setLabel("Clear");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClearMI;
}
/**
 * Return the ClearPMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getClearPMI() {
	if (ivjClearPMI == null) {
		try {
			ivjClearPMI = new java.awt.MenuItem();
			ivjClearPMI.setLabel("Clear");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClearPMI;
}
/**
 * Return the ContentsPane property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane() {
	if (ivjContentsPane == null) {
		try {
			ivjContentsPane = new java.awt.Panel();
			ivjContentsPane.setName("ContentsPane");
			ivjContentsPane.setLayout(null);
			ivjContentsPane.setBackground(new java.awt.Color(200,200,200));
			getContentsPane().add(getBorderPanel(), getBorderPanel().getName());
			getContentsPane().add(getPresenceSP(), getPresenceSP().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane;
}
/**
 * Return the ContentsPane6 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane6() {
	if (ivjContentsPane6 == null) {
		try {
			ivjContentsPane6 = new java.awt.Panel();
			ivjContentsPane6.setName("ContentsPane6");
			ivjContentsPane6.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsErrorOkBtn = new java.awt.GridBagConstraints();
			constraintsErrorOkBtn.gridx = 0; constraintsErrorOkBtn.gridy = 1;
			constraintsErrorOkBtn.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentsPane6().add(getErrorOkBtn(), constraintsErrorOkBtn);

			java.awt.GridBagConstraints constraintsErrorTA = new java.awt.GridBagConstraints();
			constraintsErrorTA.gridx = 0; constraintsErrorTA.gridy = 0;
			constraintsErrorTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsErrorTA.weightx = 1.0;
			constraintsErrorTA.weighty = 1.0;
			constraintsErrorTA.insets = new java.awt.Insets(10, 10, 0, 10);
			getContentsPane6().add(getErrorTA(), constraintsErrorTA);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane6;
}
/**
 * Return the CopyMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getCopyMI() {
	if (ivjCopyMI == null) {
		try {
			ivjCopyMI = new java.awt.MenuItem();
			ivjCopyMI.setLabel("Copy");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCopyMI;
}
/**
 * Return the CopyPMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getCopyPMI() {
	if (ivjCopyPMI == null) {
		try {
			ivjCopyPMI = new java.awt.MenuItem();
			ivjCopyPMI.setLabel("Copy");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCopyPMI;
}
/**
 * Return the CutMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getCutMI() {
	if (ivjCutMI == null) {
		try {
			ivjCutMI = new java.awt.MenuItem();
			ivjCutMI.setLabel("Cut");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCutMI;
}
/**
 * Return the CutPMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getCutPMI() {
	if (ivjCutPMI == null) {
		try {
			ivjCutPMI = new java.awt.MenuItem();
			ivjCutPMI.setLabel("Cut");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCutPMI;
}
/**
 * Return the EditM property value.
 * @return java.awt.Menu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Menu getEditM() {
	if (ivjEditM == null) {
		try {
			ivjEditM = new java.awt.Menu();
			ivjEditM.setLabel("Edit");
			ivjEditM.add(getCutMI());
			ivjEditM.add(getCopyMI());
			ivjEditM.add(getPasteMI());
			ivjEditM.add(getMenuSeparator11());
			ivjEditM.add(getClearMI());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditM;
}
/**
 * Return the EditPU property value.
 * @return java.awt.PopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.PopupMenu getEditPU() {
	if (ivjEditPU == null) {
		try {
			ivjEditPU = new java.awt.PopupMenu();
			ivjEditPU.setLabel("Edit");
			ivjEditPU.add(getCutPMI());
			ivjEditPU.add(getCopyPMI());
			ivjEditPU.add(getPastePMI());
			ivjEditPU.add(getMenuSeparator31());
			ivjEditPU.add(getClearPMI());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditPU;
}
/**
 * Return the ErrorDlg property value.
 * @return java.awt.Dialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Dialog getErrorDlg() {
	if (ivjErrorDlg == null) {
		try {
			ivjErrorDlg = new java.awt.Dialog(this);
			ivjErrorDlg.setName("ErrorDlg");
			ivjErrorDlg.setLayout(new java.awt.BorderLayout());
			ivjErrorDlg.setBackground(java.awt.SystemColor.window);
			ivjErrorDlg.setBounds(45, 781, 394, 240);
			ivjErrorDlg.setModal(true);
			ivjErrorDlg.setTitle("Error!");
			getErrorDlg().add(getContentsPane6(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjErrorDlg;
}
/**
 * Return the ErrorOkBtn property value.
 * @return java.awt.Button
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Button getErrorOkBtn() {
	if (ivjErrorOkBtn == null) {
		try {
			ivjErrorOkBtn = new java.awt.Button();
			ivjErrorOkBtn.setName("ErrorOkBtn");
			ivjErrorOkBtn.setLabel("Close");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjErrorOkBtn;
}
/**
 * Return the ErrorTA property value.
 * @return java.awt.TextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.TextArea getErrorTA() {
	if (ivjErrorTA == null) {
		try {
			ivjErrorTA = new java.awt.TextArea("", 0, 0, java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
			ivjErrorTA.setName("ErrorTA");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjErrorTA;
}
/**
 * Return the ExitMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getExitMI() {
	if (ivjExitMI == null) {
		try {
			ivjExitMI = new java.awt.MenuItem();
			ivjExitMI.setLabel("Exit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExitMI;
}
/**
 * Return the FileDlg property value.
 * @return java.awt.FileDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FileDialog getFileDlg() {
	if (ivjFileDlg == null) {
		try {
			ivjFileDlg = new java.awt.FileDialog(this);
			ivjFileDlg.setName("FileDlg");
			ivjFileDlg.setLayout(null);
			ivjFileDlg.setMode(java.awt.FileDialog.SAVE);
			ivjFileDlg.setTitle("Save Messages As...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileDlg;
}
/**
 * Return the FileM property value.
 * @return java.awt.Menu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Menu getFileM() {
	if (ivjFileM == null) {
		try {
			ivjFileM = new java.awt.Menu();
			ivjFileM.setLabel("File");
			ivjFileM.add(getSaveMI());
			ivjFileM.add(getMenuSeparator1());
			ivjFileM.add(getExitMI());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileM;
}
/**
 * Return the MenuSeparator1 property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getMenuSeparator1() {
	if (ivjMenuSeparator1 == null) {
		try {
			ivjMenuSeparator1 = new java.awt.MenuItem();
			ivjMenuSeparator1.setLabel("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenuSeparator1;
}
/**
 * Return the MenuSeparator11 property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getMenuSeparator11() {
	if (ivjMenuSeparator11 == null) {
		try {
			ivjMenuSeparator11 = new java.awt.MenuItem();
			ivjMenuSeparator11.setLabel("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenuSeparator11;
}
/**
 * Return the MenuSeparator31 property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getMenuSeparator31() {
	if (ivjMenuSeparator31 == null) {
		try {
			ivjMenuSeparator31 = new java.awt.MenuItem();
			ivjMenuSeparator31.setLabel("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenuSeparator31;
}
/**
 * Return the MessageCenterMenuBar property value.
 * @return java.awt.MenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuBar getMessageCenterMenuBar() {
	if (ivjMessageCenterMenuBar == null) {
		try {
			ivjMessageCenterMenuBar = new java.awt.MenuBar();
			ivjMessageCenterMenuBar.add(getFileM());
			ivjMessageCenterMenuBar.add(getEditM());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMessageCenterMenuBar;
}
/**
 * Return the MessageTA property value.
 * @return MessageTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MessageTextArea getMessageTA() {
	if (ivjMessageTA == null) {
		try {
			ivjMessageTA = new oem.edge.ed.odc.meeting.clienta.MessageTextArea("", 0, 0, 1);
			ivjMessageTA.setName("MessageTA");
			ivjMessageTA.setBackground(new java.awt.Color(200,200,200));
			ivjMessageTA.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMessageTA;
}
/**
 * Return the PasteMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getPasteMI() {
	if (ivjPasteMI == null) {
		try {
			ivjPasteMI = new java.awt.MenuItem();
			ivjPasteMI.setLabel("Paste");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasteMI;
}
/**
 * Return the PastePMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getPastePMI() {
	if (ivjPastePMI == null) {
		try {
			ivjPastePMI = new java.awt.MenuItem();
			ivjPastePMI.setLabel("Paste");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPastePMI;
}
/**
 * Return the PresencePnl property value.
 * @return PresencePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private PresencePanel getPresencePnl() {
	if (ivjPresencePnl == null) {
		try {
			ivjPresencePnl = new oem.edge.ed.odc.meeting.clienta.PresencePanel();
			ivjPresencePnl.setName("PresencePnl");
			ivjPresencePnl.setBounds(0, 0, 20, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPresencePnl;
}
/**
 * Return the PresenceSP property value.
 * @return java.awt.ScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.ScrollPane getPresenceSP() {
	if (ivjPresenceSP == null) {
		try {
			ivjPresenceSP = new java.awt.ScrollPane();
			ivjPresenceSP.setName("PresenceSP");
			ivjPresenceSP.setBounds(490, 5, 85, 410);
			getPresenceSP().add(getPresencePnl(), getPresencePnl().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPresenceSP;
}
/**
 * Return the SaveMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getSaveMI() {
	if (ivjSaveMI == null) {
		try {
			ivjSaveMI = new java.awt.MenuItem();
			ivjSaveMI.setLabel("Save As...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSaveMI;
}
/**
 * Return the SendBtn property value.
 * @return TipButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TipButton getSendBtn() {
	if (ivjSendBtn == null) {
		try {
			ivjSendBtn = new oem.edge.ed.odc.meeting.clienta.TipButton();
			ivjSendBtn.setName("SendBtn");
			ivjSendBtn.setTipText("Press to send message");
			ivjSendBtn.setLabel("Send");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSendBtn;
}
/**
 * Return the SendTF property value.
 * @return TipTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TipTextField getSendTF() {
	if (ivjSendTF == null) {
		try {
			ivjSendTF = new oem.edge.ed.odc.meeting.clienta.TipTextField();
			ivjSendTF.setName("SendTF");
			ivjSendTF.setTipText("Type your message");
			ivjSendTF.setBackground(java.awt.Color.white);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSendTF;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addWindowListener(ivjEventHandler);
	getExitMI().addActionListener(ivjEventHandler);
	getErrorOkBtn().addActionListener(ivjEventHandler);
	getCutMI().addActionListener(ivjEventHandler);
	getCopyMI().addActionListener(ivjEventHandler);
	getPasteMI().addActionListener(ivjEventHandler);
	getClearMI().addActionListener(ivjEventHandler);
	getCutPMI().addActionListener(ivjEventHandler);
	getCopyPMI().addActionListener(ivjEventHandler);
	getPastePMI().addActionListener(ivjEventHandler);
	getClearPMI().addActionListener(ivjEventHandler);
	getSaveMI().addActionListener(ivjEventHandler);
	getContentsPane().addComponentListener(ivjEventHandler);
	getSendTF().addActionListener(ivjEventHandler);
	getSendTF().addMouseListener(ivjEventHandler);
	getSendBtn().addActionListener(ivjEventHandler);
	getMessageTA().addMouseListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MessageCenter");
		setMenuBar(getMessageCenterMenuBar());
		setLayout(new java.awt.BorderLayout());
		setBackground(new java.awt.Color(200,200,200));
		setSize(582, 439);
		add(getContentsPane(), "Center");
		initConnections();
		connEtoC5();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void login() {
	try {
		dispatcher = new DSMPDispatcher(serverName,serverPort);
	}
	catch (UnknownHostException e) {
		e.printStackTrace();
		System.out.println("Unknown host: " + serverName);
		System.out.println("MeetingViewer host port [display]");
		postError("No connection","Unknown host: " + serverName);
		return;
	}
	catch (IOException ioe) {
		ioe.printStackTrace();
		System.out.println("Unable to connect to server " + serverName + " at " + serverPort + ".");
		System.out.println("MeetingViewer host port [display]");
		postError("No connection","Unable to connect to server " + serverName + " at " + serverPort + ".");
		return;
	}

	dispatcher.addMeetingListener(this);

	DSMPProto p = DSMPGenerator.loginUserPW((byte) 0,user,"pw");
	dispatcher.dispatchProtocol(p);
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		MessageCenter aMessageCenter = new MessageCenter();
		aMessageCenter.getPresencePnl().limitProjChat = true;
		aMessageCenter.begin(args);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Frame");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:47:50 PM)
 * @param e MeetingEvent
 */
public void meetingAction(MeetingEvent e) {
	if (e.isLogin()) {
		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		dispatcher.loginID = e.loginID;

		dispatcher.dispatchProtocol(DSMPGenerator.getAllMeetings((byte) 0));
	}
	else if (e.isLoginFailed()) {
		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		postError("Login failed",e.message);
	}
	else if (e.isLogout()) {
		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	else if (e.isLogoutFailed()) {
		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		postError("Logout failed",e.message);
	}
	else if (e.isGet()) {
		Vector invites = e.getInvites();

		if (invites != null && invites.size() > 0) {
			Enumeration p = invites.elements();
			while (p.hasMoreElements()) {
				DSMPMeeting m = (DSMPMeeting) p.nextElement();
				if (m.getTitle().equals(meeting)) {
					getPresencePnl().setDispatcher(dispatcher);
					dispatcher.addMessageListener(this);

					dispatcher.meetingID = m.getMeetingId();
					dispatcher.inviteID = m.getInviteId();
					dispatcher.dispatchProtocol(DSMPGenerator.joinMeeting((byte) 0,dispatcher.meetingID));
					setTitle(getTitle() + " - " + meeting);
					return;
				}
			}
		}

		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		//getMeetingM().setEnabled(true);
		//getFileM().setEnabled(true);
		postError("No invitation","You have not been invited to meeting: " + meeting);
	}
	else if (e.isGetFailed()) {
		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		//getMeetingM().setEnabled(true);
		//getFileM().setEnabled(true);
		postError("Get invites failed",e.message);
	}
	else if (e.isJoin()) {
		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		dispatcher.particpantID = e.participantID;
		dispatcher.isOwner = false;

		// Set window features.
		getMessageTA().setEnabled(true);
		getSendTF().setEnabled(true);
		getSendBtn().setEnabled(true);
		getPresencePnl().setEnabled(true);
	}
	else if (e.isJoinFailed()) {
		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		postError("Join meeting failed",e.message);
	}
	else if (e.isLeave() || e.isForcedLeave()) {
		if (e.isForcedLeave() && leaving)
			return;

		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		reset();

		if (e.isForcedLeave())
			postError("Meeting closed","The moderator has removed you from the meeting.");

		leaving = false;
	}
	else if (e.isLeaveFailed()) {
		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		postError("Leave meeting failed",e.message);
		leaving = false;
	}
	else if (e.isEnd()) {
		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		reset();
	}
	else if (e.isInviteFailed()) {
		postError("Invite failed",e.message);
	}
	else if (e.isChatFailed()) {
		postError("Chat failed",e.message);
	}
	else if (e.isDeath()) {
		reset();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 3:59:29 PM)
 * @param e MessageEvent
 */
public void message(MessageEvent e) {
	// Only broadcast messages are ours.
	if (! e.isUnicast()) {
		String msg = getPresencePnl().getUserName(e.fromID) + ": " + e.message;
		getMessageTA().postMessage(msg);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2002 8:40:36 PM)
 */
public void postError(String title, String msg) {
	getErrorDlg().setTitle(title);
	getErrorTA().setText(msg);
	AwtQueueRunner.invokeLater(new Runnable() {
		public void run() {
			centerWindow(getErrorDlg());
		}
	});
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/2002 11:39:39 AM)
 */
private void reset() {
	// Reset components if meeting just ended.
	if (dispatcher != null) {
		getPresencePnl().setDispatcher(null);
		dispatcher.removeMessageListener(this);

		dispatcher.meetingID = -1;
		dispatcher.inviteID = -1;
		dispatcher.particpantID = -1;
	}

	// Set the window features.
	getMessageTA().setText("");
	getMessageTA().setEnabled(false);
	getSendTF().setEnabled(false);
	getSendBtn().setEnabled(false);
	getPresencePnl().setEnabled(false);

	setTitle("Message Center");
}
/**
 * Comment
 */
public void sendMsg() {
	String msg = getSendTF().getText();

	if (msg == null || msg.length() == 0)
		return;

	DSMPProto p = DSMPGenerator.chatBroadcast((byte) 0,dispatcher.meetingID,msg);
	dispatcher.dispatchProtocol(p);
	getSendTF().setText("");
	getMessageTA().postMessage(getPresencePnl().getUserName(dispatcher.particpantID) + ": " + msg);
}
/**
 * Comment
 */
public void setup() {
	getSendTF().add(getEditPU());
	editTarget = getSendTF();
}
}
