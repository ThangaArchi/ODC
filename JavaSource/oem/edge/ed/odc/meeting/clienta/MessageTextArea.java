package oem.edge.ed.odc.meeting.clienta;

import java.awt.datatransfer.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Insert the type's description here.
 * Creation date: (7/29/2002 9:04:39 AM)
 * @author: Mike Zarnick
 */
class MessageTextArea extends TextArea implements ActionListener {
	private PopupMenu editPU;
	private MenuItem copyMI;
	private MenuItem clearMI;
	private MouseHandler mh = new MouseHandler();

	class MouseHandler extends MouseAdapter {
		MessageTextArea ta;
		public void mouseReleased(MouseEvent e) {
			//System.out.println("Mouse Event");
			boolean showPopup = ((e.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0);

			if (showPopup) {
				boolean enable = getSelectionStart() != getSelectionEnd();
				copyMI.setEnabled(enable);

				editPU.show(ta,e.getX(),e.getY());

				e.consume();
			}
		}
	}
/**
 * MessageTextArea constructor comment.
 */
public MessageTextArea() {
	super();
	setup();
}
/**
 * MessageTextArea constructor comment.
 * @param rows int
 * @param columns int
 */
public MessageTextArea(int rows, int columns) {
	super(rows, columns);
	setup();
}
/**
 * MessageTextArea constructor comment.
 * @param text java.lang.String
 */
public MessageTextArea(String text) {
	super(text);
	setup();
}
/**
 * MessageTextArea constructor comment.
 * @param text java.lang.String
 * @param rows int
 * @param columns int
 */
public MessageTextArea(String text, int rows, int columns) {
	super(text, rows, columns);
	setup();
}
/**
 * MessageTextArea constructor comment.
 * @param text java.lang.String
 * @param rows int
 * @param columns int
 * @param scrollbars int
 */
public MessageTextArea(String text, int rows, int columns, int scrollbars) {
	super(text, rows, columns, scrollbars);
	setup();
}
/**
 * Insert the method's description here.
 * Creation date: (7/30/2002 12:20:57 PM)
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(ActionEvent e) {
	if (e.getSource() == copyMI) {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection s = new StringSelection(getSelectedText());
		c.setContents(s,s);
	}
	else
		setText("");
}
/**
 * Insert the method's description here.
 * Creation date: (7/30/2002 12:00:17 PM)
 */
public void createPopup() {
	editPU = new PopupMenu("Edit");
	copyMI = new MenuItem("Copy");
	clearMI = new MenuItem("Clear");

	editPU.add(copyMI);
	editPU.add(new MenuItem("-"));
	editPU.add(clearMI);

	add(editPU);

	copyMI.addActionListener(this);
	clearMI.addActionListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 11:20:21 AM)
 * @param msg java.lang.String
 */
synchronized public void postMessage(String msg) {
	if (getText().length() > 0)
		append("\n");

	append(msg);
}
/**
 * Insert the method's description here.
 * Creation date: (7/30/2002 12:00:17 PM)
 */
public void setup() {
	mh.ta = this;
	addMouseListener(mh);
	createPopup();
}
}
