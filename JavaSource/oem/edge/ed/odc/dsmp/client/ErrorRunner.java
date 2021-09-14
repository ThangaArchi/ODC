package oem.edge.ed.odc.dsmp.client;

import java.awt.Component;
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

public class ErrorRunner implements Runnable {
	private Component parent;
	private String message;
	private String[] messages;
	private String title;
	private boolean isError;
	private String details;
/**
 * ErrorRunner constructor comment.
 */
public ErrorRunner(Component parent, String message, String title) {
	this(parent,message,title,true);
}
/**
 * ErrorRunner constructor comment.
 */
public ErrorRunner(Component parent, String message, String title, boolean isError) {
	parseMessage(message);

	this.parent = parent;
	this.title = title;
	this.isError = isError;
}
/**
 * ErrorRunner constructor comment.
 */
public ErrorRunner(Component parent, String[] messages, String title, boolean isError) {
	if (messages.length == 1) {
		parseMessage(messages[0]);
	}
	else {
		this.messages = messages;
		this.message = null;
		this.details = null;
	}
	
	this.parent = parent;
	this.title = title;
	this.isError = isError;
}
private void parseMessage(String message) {
	int i = message.indexOf("<@-@>");

	if (i != -1) {
		this.message = message.substring(0,i);
		this.details = message.substring(i+5);
	}
	else {
		this.message = message;
		this.details = null;
	}
}
public void run() {
	int msgType = isError ? MessagePane.ERROR_MESSAGE : MessagePane.INFORMATION_MESSAGE;

	if (message != null) {
		if (details == null) {
			MessagePane.showMessageDialog(parent,message,title,msgType);
		}
		else {
			String[] options = { "Ok", "Details >>" };
			int selectedValue = MessagePane.showOptionDialog(parent,message,title,0,msgType,
																null, options, options[0]);
	
			if (selectedValue == 1) {
				String[] detailMsg = new String[4];
				detailMsg[0] = message;
				detailMsg[1] = " ";
				detailMsg[2] = "Details:";
				detailMsg[3] = details;
	
				MessagePane.showMessageDialog(parent,detailMsg,title,msgType);
			}
		}
	}
	else {
		String[] options = { "Ok" };
		MessagePane.showOptionDialog(parent,messages,title,0,msgType,null,options,options[0]);
	}
}
}
