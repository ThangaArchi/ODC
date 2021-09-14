package oem.edge.ed.odc.meeting.clienta;

import java.awt.*;

/**
 * Insert the type's description here.
 * Creation date: (8/23/2002 1:12:47 PM)
 * @author: Mike Zarnick
 */
class TipTextField extends TextField implements TipOwner {
	private String tipText = null;
/**
 * TipTextField constructor comment.
 */
public TipTextField() {
	super();
}
/**
 * TipTextField constructor comment.
 * @param columns int
 */
public TipTextField(int columns) {
	super(columns);
}
/**
 * TipTextField constructor comment.
 * @param text java.lang.String
 */
public TipTextField(String text) {
	super(text);
}
/**
 * TipTextField constructor comment.
 * @param text java.lang.String
 * @param columns int
 */
public TipTextField(String text, int columns) {
	super(text, columns);
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2002 1:10:11 PM)
 * @return java.lang.String
 */
public String getTipText() {
	return tipText;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2002 1:10:36 PM)
 * @param t java.lang.String
 */
public void setTipText(String t) {
	if (tipText != null)
		TipManager.getTipManager().remove((TipOwner) this);

	tipText = t;

	if (tipText != null)
		TipManager.getTipManager().add(this);
}
}
