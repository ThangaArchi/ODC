package oem.edge.ed.odc.dsmp.client;

import javax.swing.JDialog;

import javax.swing.JPanel;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import oem.edge.ed.util.SearchEtc;
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

public class ErrorHandler extends JDialog {
	static private SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.US);

	private Vector messages = new Vector();
	private int currentMsg = -1;

	private JPanel errorHandlerCP = null;
	private JLabel errorLbl = null;
	private JLabel errorCurLbl = null;
	private JLabel errorOfLbl = null;
	private JLabel errorMaxLbl = null;
	private JButton prevBtn = null;
	private JButton nextBtn = null;
	private JButton closeBtn = null;
	private JScrollPane errorSP = null;
	private JEditorPane errorTA = null;
	
	private class Message {
		public String title;
		public Date date;
		public boolean isError;
		public String message;
		public String detail;
		public boolean viewed;
	}
	
	private class CloseHandler extends WindowAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			close();
		}
		public void windowClosing(WindowEvent e) {
			close();
		}
		private void close() {
			synchronized(ErrorHandler.this) {
				boolean unviewed = false;
				Enumeration e = messages.elements();
				while (e.hasMoreElements() && ! unviewed) {
					Message m = (Message) e.nextElement();
					if (! m.viewed) unviewed = true;
				}
				
				if (unviewed) {
					int opt = JOptionPane.showConfirmDialog(ErrorHandler.this,"There are other messages to view. Discard them?","Messages To View",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
					if (opt != JOptionPane.YES_OPTION) {
						return;
					}
				}
				
				messages.removeAllElements();
				currentMsg = -1;
				ErrorHandler.this.setVisible(false);
			}
		}
	}
	private CloseHandler closeHandler = new CloseHandler();
	private class PostNewMsg implements Runnable {
		public void run() {
			// Update the max count.
			errorMaxLbl.setText(Integer.toString(messages.size()));
			
			if (! isShowing()) {
				currentMsg = 0;
				showCurrentMsg();
				setLocationRelativeTo(getParent());
				setVisible(true);
			}
			else {
				updateButtons();
			}
		}
	}
	private PostNewMsg postNewMsg = new PostNewMsg();
	/**
	 * This method initializes 
	 * 
	 */
	public ErrorHandler(Frame owner) {
		super(owner);
		initialize();
	}
	/**
	 * This method initializes 
	 * 
	 */
	public ErrorHandler(Dialog owner) {
		super(owner);
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        this.setContentPane(getErrorHandlerCP());
        this.setTitle("Messages");
        this.setSize(350, 200);
        this.setModal(true);
        this.addWindowListener(closeHandler);			
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getErrorHandlerCP() {
		if (errorHandlerCP == null) {
			errorMaxLbl = new JLabel();
			errorOfLbl = new JLabel();
			errorCurLbl = new JLabel();
			errorLbl = new JLabel();
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			errorHandlerCP = new JPanel();
			errorHandlerCP.setLayout(new GridBagLayout());
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			errorLbl.setText("Error ");
			gridBagConstraints1.insets = new java.awt.Insets(5,5,0,0);
			gridBagConstraints2.insets = new java.awt.Insets(5,0,0,0);
			gridBagConstraints3.insets = new java.awt.Insets(5,0,0,0);
			gridBagConstraints4.insets = new java.awt.Insets(5,0,0,5);
			gridBagConstraints6.insets = new java.awt.Insets(5,2,0,5);
			gridBagConstraints5.insets = new java.awt.Insets(5,5,0,2);
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints5.weightx = 1.0D;
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 2;
			gridBagConstraints7.insets = new java.awt.Insets(5,5,5,5);
			gridBagConstraints7.gridwidth = 0;
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.weighty = 1.0;
			gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints8.insets = new java.awt.Insets(2,5,0,5);
			gridBagConstraints8.gridwidth = 0;
			errorHandlerCP.add(errorLbl, gridBagConstraints1);
			errorHandlerCP.add(errorCurLbl, gridBagConstraints2);
			errorHandlerCP.add(errorOfLbl, gridBagConstraints3);
			errorHandlerCP.add(errorMaxLbl, gridBagConstraints4);
			errorHandlerCP.add(getPrevBtn(), gridBagConstraints5);
			errorHandlerCP.add(getNextBtn(), gridBagConstraints6);
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			errorCurLbl.setText("1");
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.gridy = 0;
			errorOfLbl.setText(" of ");
			gridBagConstraints4.gridx = 3;
			gridBagConstraints4.gridy = 0;
			errorMaxLbl.setText("N");
			gridBagConstraints5.gridx = 4;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints6.gridx = 5;
			gridBagConstraints6.gridy = 0;
			errorHandlerCP.add(getCloseBtn(), gridBagConstraints7);
			errorHandlerCP.add(getErrorSP(), gridBagConstraints8);
		}
		return errorHandlerCP;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getPrevBtn() {
		if (prevBtn == null) {
			prevBtn = new JButton();
			prevBtn.setIcon(new ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/download.gif")));
			prevBtn.setMargin(new java.awt.Insets(0,0,0,0));
			prevBtn.setToolTipText("View previous error");
			prevBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					currentMsg--;
					showCurrentMsg();
				}
			});
		}
		return prevBtn;
	}
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getNextBtn() {
		if (nextBtn == null) {
			nextBtn = new JButton();
			nextBtn.setIcon(new ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/upload.gif")));
			nextBtn.setMargin(new java.awt.Insets(0,0,0,0));
			nextBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					currentMsg++;
					showCurrentMsg();
				}
			});
		}
		return nextBtn;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getCloseBtn() {
		if (closeBtn == null) {
			closeBtn = new JButton();
			closeBtn.setText("Close");
			closeBtn.setToolTipText("Close this window");
			closeBtn.addActionListener(closeHandler);
		}
		return closeBtn;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getErrorSP() {
		if (errorSP == null) {
			errorSP = new JScrollPane();
			errorSP.setViewportView(getErrorTA());
		}
		return errorSP;
	}
	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JEditorPane getErrorTA() {
		if (errorTA == null) {
			errorTA = new JEditorPane();
			errorTA.setEditable(false);
			errorTA.setBackground(getErrorHandlerCP().getBackground());
			errorTA.setContentType("text/html");
		}
		return errorTA;
	}
	/**
	 * Add a new message to the message window.
	 * @param parent
	 * @param message
	 * @param title
	 * @param isError
	 */
	private void addMsgInternal(String message, String title, boolean isError) {
		Message msg = new Message();
		msg.date = new Date();
		msg.isError = isError;
		msg.title = title;
		msg.viewed = false;
		
		int i = message.indexOf("<@-@>");
		msg.message = (i == -1) ? message : message.substring(0,i);
		msg.detail = (i == -1) ? null : message.substring(i+5);
		
		synchronized(this) {
			messages.addElement(msg);
		}
	}
	public void addMsg(String message, String title) {
		addMsg(message,title,true);
	}
	public void addMsg(String message, String title, boolean isError) {
		addMsgInternal(message,title,isError);
		
		// post the update.
		SwingUtilities.invokeLater(postNewMsg);
	}
	public void addMsg(String messages[], String title, boolean isError) {
		for (int i = 0; i < messages.length; i++) {
			addMsgInternal(messages[i],title,isError);
		}
		
		// post the update.
		SwingUtilities.invokeLater(postNewMsg);
	}
	private synchronized void showCurrentMsg() {
		// Get the message and mark it viewed.
		Message msg = (Message) messages.elementAt(currentMsg);
		msg.viewed = true;

		errorLbl.setText((msg.isError) ? "Error " : "Message ");
		String text = "<html><body><p><b>" + format.format(msg.date) + " - ";
		text += msg.title + "</b></p><p>" + SearchEtc.htmlEscape(msg.message);
		if (msg.detail != null) {
			text += "</p><p><b>Details:</b></p><p>" + SearchEtc.htmlEscape(msg.detail);
		}
		text += "</p></body></html>";

		// Update the GUI elements.
		errorCurLbl.setText(Integer.toString(currentMsg+1));
		getErrorTA().setText(text);
		updateButtons();
		getErrorTA().setCaretPosition(0);
	}
	private void updateButtons() {
		getNextBtn().setEnabled(currentMsg+1 < messages.size());
		getPrevBtn().setEnabled(currentMsg > 0);
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
