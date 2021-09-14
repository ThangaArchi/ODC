package oem.edge.ed.odc.dsmp.client;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
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
 * @author zarnick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AboutWindow extends JDialog {
	private JPanel aboutCP = null;  //  @jve:decl-index=0:visual-constraint="29,32"
	private JEditorPane aboutEditorPnl = null;
	private JScrollPane aboutSP = null;
	private JButton backBtn = null;
	private JButton fwdBtn = null;
	private JLabel statusLbl = null;
	private class HistoryMgr {
		private Vector history = new Vector();
		private int index = -1;
		
		public URL forwards() {
			// No where to go?
			if (index + 1 == history.size()) 
				return null;
			
			// Return next URL.
			index++;
			return (URL) history.elementAt(index);
		}
		public URL backwards() {
			// No where to go?
			if (index <= 0) 
				return null;
			
			// Return previous URL.
			index--;
			return (URL) history.elementAt(index);
		}
		public boolean canGoFowards() {
			// More URLs ahead?
			return (index != -1 && index < history.size() - 1);
		}
		public boolean canGoBackwards() {
			// More URLs behind?
			return (index > 0);
		}
		public void wentTo(URL url) {
			// Truncate the history and add the new URL.
			history.setSize(++index);
			history.addElement(url);
			System.out.println("History depth: " + history.size() + " Index: " + index);
		}
		public void clearHistory() {
			// No URLs in history.
			history.removeAllElements();
			index = -1;
		}
	}
	private HistoryMgr historyMgr = new HistoryMgr();
	private class LinkListener implements HyperlinkListener {
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
				statusLbl.setText(e.getURL().toString());
			}
			else {
				statusLbl.setText("");
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						gotoPage(e.getURL());
					} catch (IOException io) {
						System.out.println("AboutWindow: Hyperlink IO error.");
						io.printStackTrace();
					}
				}
			}
		}
	}
	private LinkListener linkListener = new LinkListener();
	private class BtnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getFwdBtn()) {
				AboutWindow.this.pageForward();
			}
			else if (e.getSource() == getBackBtn()) {
				AboutWindow.this.pageBackward();
			}
		}
	}
	private BtnListener btnListener = new BtnListener();

	/**
	 * 
	 */
	public AboutWindow() {
		super();
		setContentPane(getAboutCP());
		setTitle("About Drop Box");
		setSize(525,425);
	}
	/**
	 * @param owner
	 */
	public AboutWindow(Frame owner) {
		super(owner);
		setContentPane(getAboutCP());
		setModal(true);
		setTitle("About Drop Box");
		setSize(525,425);
	}
	/**
	 * This method initializes aboutCP	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getAboutCP() {
		if (aboutCP == null) {
			aboutCP = new JPanel();
			statusLbl = new JLabel();
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			aboutCP.setLayout(new GridBagLayout());
			aboutCP.setSize(350, 300);
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.ipady = 215;
			gridBagConstraints1.insets = new java.awt.Insets(5,5,5,5);
			gridBagConstraints1.gridwidth = 0;
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.insets = new java.awt.Insets(0,0,5,5);
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.weightx = 0.0D;
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.insets = new java.awt.Insets(0,5,5,2);
			gridBagConstraints4.weighty = 0.0D;
			gridBagConstraints4.ipady = 0;
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 1;
			gridBagConstraints11.insets = new java.awt.Insets(0,5,5,5);
			gridBagConstraints11.weightx = 1.0D;
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			statusLbl.setText("");
			statusLbl.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
			aboutCP.add(getBackBtn(), gridBagConstraints4);
			aboutCP.add(getFwdBtn(), gridBagConstraints3);
			aboutCP.add(getAboutSP(), gridBagConstraints1);
			aboutCP.add(statusLbl, gridBagConstraints11);
		}
		return aboutCP;
	}
	/**
	 * This method initializes aboutSP	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getAboutSP() {
		if (aboutSP == null) {
			aboutSP = new JScrollPane();
			aboutSP.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
			aboutSP.setViewportView(getAboutEditorPnl());
		}
		return aboutSP;
	}
	/**
	 * This method initializes aboutEditorPnl	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */    
	private JEditorPane getAboutEditorPnl() {
		if (aboutEditorPnl == null) {
			aboutEditorPnl = new JEditorPane();
			aboutEditorPnl.setEditable(false);
			aboutEditorPnl.addHyperlinkListener(linkListener);
		}
		return aboutEditorPnl;
	}
	/**
	 * This method initializes backBtn	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getBackBtn() {
		if (backBtn == null) {
			backBtn = new JButton();
			backBtn.setIcon(new ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/download.gif")));
			backBtn.setToolTipText("Previous page");
			backBtn.setMargin(new java.awt.Insets(0,0,0,0));
			backBtn.addActionListener(btnListener);
		}
		return backBtn;
	}
	/**
	 * This method initializes fwdBtn	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getFwdBtn() {
		if (fwdBtn == null) {
			fwdBtn = new JButton();
			fwdBtn.setIcon(new ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/upload.gif")));
			fwdBtn.setToolTipText("Next page");
			fwdBtn.setMargin(new java.awt.Insets(0,0,0,0));
			fwdBtn.addActionListener(btnListener);
		}
		return fwdBtn;
	}

	public void setPage(URL pageUrl) throws IOException {
		historyMgr.clearHistory();
		gotoPage(pageUrl);
	}
	public void gotoPage(URL pageUrl) throws IOException {
		System.out.println(pageUrl);
		historyMgr.wentTo(pageUrl);
		getAboutEditorPnl().setPage(pageUrl);
		setButtons();
	}
	public void pageForward() {
		if (! historyMgr.canGoFowards())
			return;
		
		try {
			getAboutEditorPnl().setPage(historyMgr.forwards());
		} catch (IOException e) {
			getAboutEditorPnl().setText("<HTML><BODY><P>Page not found!</P></BODY></HTML>");
		}
		setButtons();
	}
	public void pageBackward() {
		if (! historyMgr.canGoBackwards())
			return;
		
		try {
			getAboutEditorPnl().setPage(historyMgr.backwards());
		} catch (IOException e) {
			getAboutEditorPnl().setText("<HTML><BODY><P>Page not found!</P></BODY></HTML>");
		}
		setButtons();
	}
	private void setButtons() {
		getBackBtn().setEnabled(historyMgr.canGoBackwards());
		getFwdBtn().setEnabled(historyMgr.canGoFowards());
	}

	public void showAbout() {
		setSize(525,425);
		setVisible(true);
	}
	public void hideAbout() {
		setVisible(false);
	}
}
