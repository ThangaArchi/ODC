package oem.edge.ed.odc.dsmp.client;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JDialog;
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
 * Creation date: (6/15/2004 2:37:48 PM)
 * @author: 
 */
public class MessagePane extends javax.swing.JOptionPane {
/**
 * MessagePane constructor comment.
 */
public MessagePane() {
	super();
}
/**
 * MessagePane constructor comment.
 * @param message java.lang.Object
 */
public MessagePane(Object message) {
	super(message);
}
/**
 * MessagePane constructor comment.
 * @param message java.lang.Object
 * @param messageType int
 */
public MessagePane(Object message, int messageType) {
	super(message, messageType);
}
/**
 * MessagePane constructor comment.
 * @param message java.lang.Object
 * @param messageType int
 * @param optionType int
 */
public MessagePane(Object message, int messageType, int optionType) {
	super(message, messageType, optionType);
}
/**
 * MessagePane constructor comment.
 * @param message java.lang.Object
 * @param messageType int
 * @param optionType int
 * @param icon javax.swing.Icon
 */
public MessagePane(Object message, int messageType, int optionType, javax.swing.Icon icon) {
	super(message, messageType, optionType, icon);
}
/**
 * MessagePane constructor comment.
 * @param message java.lang.Object
 * @param messageType int
 * @param optionType int
 * @param icon javax.swing.Icon
 * @param options java.lang.Object[]
 */
public MessagePane(Object message, int messageType, int optionType, javax.swing.Icon icon, java.lang.Object[] options) {
	super(message, messageType, optionType, icon, options);
}
/**
 * MessagePane constructor comment.
 * @param message java.lang.Object
 * @param messageType int
 * @param optionType int
 * @param icon javax.swing.Icon
 * @param options java.lang.Object[]
 * @param initialValue java.lang.Object
 */
public MessagePane(Object message, int messageType, int optionType, javax.swing.Icon icon, java.lang.Object[] options, Object initialValue) {
	super(message, messageType, optionType, icon, options, initialValue);
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 1:50:37 PM)
 */
public void adjustPreferredSize() {
	Dimension prefSize = getPreferredSize();
	prefSize.width+=5;
	prefSize.height+=2;
	setPreferredSize(prefSize);
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 12:55:48 PM)
 * @return int
 */
public int getMaxCharactersPerLineCount() {
	return 50;
}
/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 8:55:15 AM)
 * @return int
 * @param parent com.ms.wfc.core.Component
 * @param message java.lang.Object
 */
public static int showConfirmDialog(Component parent, Object message) {
	return showConfirmDialog(parent, message, "Select an Option",
								 YES_NO_CANCEL_OPTION);
}
/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 8:55:15 AM)
 * @return int
 * @param parent com.ms.wfc.core.Component
 * @param message java.lang.Object
 */
public static int showConfirmDialog(Component parent, Object message, String title, int optionType) {
	return showConfirmDialog(parent,message,title,optionType,QUESTION_MESSAGE);
}
/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 8:55:15 AM)
 * @return int
 * @param parent com.ms.wfc.core.Component
 * @param message java.lang.Object
 */
public static int showConfirmDialog(Component parent, Object message, String title, int optionType, int messageType) {
	return showConfirmDialog(parent,message,title,optionType,messageType,null);
}
/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 8:55:15 AM)
 * @return int
 * @param parent com.ms.wfc.core.Component
 * @param message java.lang.Object
 */
public static int showConfirmDialog(Component parent, Object message, String title, int optionType, int messageType, Icon icon) {
	return showOptionDialog(parent,message,title,optionType,messageType,icon,null,null);
}
/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 8:55:15 AM)
 * @return int
 * @param parent com.ms.wfc.core.Component
 * @param message java.lang.Object
 */
public static void showMessageDialog(Component parent, Object message) {
	showMessageDialog(parent,message,"Message",INFORMATION_MESSAGE);
}
/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 8:55:15 AM)
 * @return int
 * @param parent com.ms.wfc.core.Component
 * @param message java.lang.Object
 */
public static void showMessageDialog(Component parent, Object message, String title, int messageType) {
	showMessageDialog(parent,message,title,messageType,null);
}
/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 8:55:15 AM)
 * @return int
 * @param parent com.ms.wfc.core.Component
 * @param message java.lang.Object
 */
public static void showMessageDialog(Component parent, Object message, String title, int messageType, Icon icon) {
	showOptionDialog(parent,message,title,DEFAULT_OPTION,messageType,icon,null,null);
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 2:40:57 PM)
 * @return int
 * @param parent java.awt.Component
 * @param message java.lang.Object
 * @param title java.lang.String
 * @param messageType int
 */
public static int showOptionDialog(Component parent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue) {
	MessagePane pane = new MessagePane(message,messageType,optionType,icon,options,initialValue);

	pane.adjustPreferredSize();
	pane.setInitialValue(initialValue);

	JDialog dialog = pane.createDialog(parent,title);

	pane.selectInitialValue();
	dialog.show();

	Object selectedValue = pane.getValue();

	if (selectedValue == null)
		return CLOSED_OPTION;

	if (options == null) {
		if(selectedValue instanceof Integer)
			return ((Integer) selectedValue).intValue();

		return CLOSED_OPTION;
	}

	for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
		if (options[counter].equals(selectedValue))
			return counter;
	}

	return CLOSED_OPTION;
}
}
