package oem.edge.ed.odc.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
// import com.ibm.extend.awt.*;
import oem.edge.ed.odc.util.*;
import com.ibm.as400.webaccess.common.*;

public class InvitationDialog extends Dialog implements ActionListener {
	Frame f;
	private DesktopCommon common = null;	
	private Invitation invitation;
	private CollaborationManager CollaborationManager;
	protected Button dialogOKButton;
	protected Button dialogDeclineButton;
	protected Panel buttonPanel;
	//protected TextField newMeetingTextField;
	//protected String newMeetingName;
//	protected TextField newMeetingUsageTextField;
	protected Label newInvitationLabel;
	//protected Label newMeetingUsageLabel;
	//MeetingPanel mp;
/**
 * NewMeetingDialog constructor comment.
 * @param meetingIcon java.awt.Image
 * @param participantIcon java.awt.Image
 */
public InvitationDialog(Frame f, CollaborationManager cm, Invitation inv) {
	super(f, "Incoming Invitation Dialog");
	this.f = f;
	this.invitation = inv;
	this.CollaborationManager = cm;
	common = new DesktopCommon();
	System.out.println("In New Meeting Dialog");
	setSize(400, 100);
	setBackground(Color.lightGray);
	setLocation(CollaborationManager.getXLOC(), CollaborationManager.getYLOC());
	newInvitationLabel = new Label("Incoming Invitation from " + invitation.getMeetingHost());
	dialogOKButton = new Button("Accept");
	dialogOKButton.addActionListener(this);
	dialogDeclineButton = new Button("Decline");
	dialogDeclineButton.addActionListener(this);
	buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 2, 0, 0));
	buttonPanel.add(dialogOKButton);
	buttonPanel.add(dialogDeclineButton);
	add(newInvitationLabel, "North");
	add(buttonPanel, "South");
	show();
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/00 8:55:59 PM)
 */
public void actionPerformed(ActionEvent e) {
	String s = null;
	if ((e.getSource() == dialogOKButton)) {
		System.out.println("AAAAAAA       " + invitation.getName());
		s = "Invitation from " + invitation.getName() + " accepted";
		CollaborationManager.appendAcceptText(s);
		CollaborationManager.getLauncher().addSession(CollaborationManager.getHost(), CollaborationManager.getWhoIam(),CollaborationManager.getDisplayPort() ,CollaborationManager.getICACookie(),invitation.getCookie(), invitation.getDisplay());
		doAccept();
		dispose();
	}
	if ((e.getSource() == dialogDeclineButton)) {
		s = "Invitation from " + invitation.getName() + " declined";
		CollaborationManager.appendDeclineText(s);
		doReject();
		dispose();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/00 11:27:04 PM)
 */
public void doAccept() {
	System.out.println("accept response sent to servlet");
	try {
		URL url = new URL(CollaborationManager.getURL() + common.ACCEPT_INVITATION_CMD);
		HttpMessage msg = new HttpMessage(url);
		Properties props = new Properties();
		props.put(common.DESKTOP_ID, CollaborationManager.getDesktopID()); // DesktopID
		props.put(common.ID, invitation.getId()); // ID Name
		msg.sendGetMessage(props);
		//msg.sendGetMess(props);
//		CollaborationManager.appendAcceptText("Invitation from " + invitation.getName() + " Accepted");
		System.out.println("URL" + url);
	} catch (Exception e) {
	} // catch (MalformedURLException e) {
	//	System.out.println("MalformedURLException thrown");
	//} catch (IOException e) {
	//	System.out.println("IOException thrown");
	//}

	//addSession

}
/**
 * Insert the method's description here.
 * Creation date: (8/24/00 11:55:09 PM)
 */
public void doReject() {
	System.out.println("reject response sent to servlet");
	try {
		URL url = new URL(CollaborationManager.getURL() + common.REJECT_INVITATION_CMD);
		HttpMessage msg = new HttpMessage(url);
		Properties props = new Properties();
		props.put(common.DESKTOP_ID, CollaborationManager.getDesktopID()); // DesktopID
		props.put(common.ID, invitation.getId()); // ID Name
		msg.sendGetMessage(props);
		//msg.sendGetMess(props);
//		CollaborationManager.appendDeclineText("Invitation from " + invitation.getName() + " Declined"); 
		System.out.println("URL" + url);
	} catch (Exception e) {
	} // catch (MalformedURLException e) {
	//	System.out.println("MalformedURLException thrown");
	//} catch (IOException e) {
	//	System.out.println("IOException thrown");
	//}


}
}
