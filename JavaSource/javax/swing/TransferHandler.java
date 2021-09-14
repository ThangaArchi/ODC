package javax.swing;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.io.Serializable;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;

public class TransferHandler implements Serializable {
	static public int NONE = 0;
	static public int COPY = 1;
	static public int MOVE = 2;
	static public int COPY_OR_MOVE = 3;
	
	protected TransferHandler() {
	}
	public TransferHandler(String property) {
	}
	
	static public Action getCutAction() {
		return null;
	}
	static public Action getCopyAction() {
		return null;
	}
	static public Action getPasteAction() {
		return null;
	}
	public boolean canImport(JComponent c, DataFlavor[] f) {
		return false;
	}
	protected Transferable createTransferable(JComponent c) {
		return null;
	}
	public void exportAsDrag(JComponent c, InputEvent e, int action) {
	}
	protected void exportDone(JComponent c, Transferable t, int action) {
	}
	public void exportToClipboard(JComponent c, Clipboard cl, int action) {
	}
	public int getSourceActions(JComponent c) {
		return NONE;
	}
	public Icon getVisualRepresentation(Transferable t) {
		return null;
	}
	public boolean importData(JComponent c, Transferable t) {
		return false;
	}
}
