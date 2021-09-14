package oem.edge.ed.odc.applet;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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

public class CodeUpdaterGui extends CodeUpdater {
	private Frame parent = null;

	private JDialog updateDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="46,40"
	private JPanel updateCP = null;
	private JLabel updateMsgLbl = null;
	private JProgressBar updatePB = null;
	private JPanel errorCP = null;
	private JDialog errorDlg = null;  //  @jve:decl-index=0:visual-constraint="49,214"
	private JButton errorOkBtn = null;
	private JScrollPane errorSP = null;
	private JTextArea errorTA = null;

	public CodeUpdaterGui(Frame parent,String url) {
		super(url);
		this.parent = parent;
		getUpdateDlg(); // Make sure GUI is constructed so public methods are ready.
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getUpdateCP() {
		if(updateCP == null) {
			updateCP = new JPanel();
			GridBagConstraints consGridBagConstraints1 = new GridBagConstraints();
			GridBagConstraints consGridBagConstraints2 = new GridBagConstraints();
			consGridBagConstraints1.gridy = 0;
			consGridBagConstraints1.gridx = 0;
			consGridBagConstraints1.anchor = GridBagConstraints.WEST;
			consGridBagConstraints1.insets = new Insets(5,5,0,5);
			consGridBagConstraints1.gridwidth = 0;
			consGridBagConstraints2.fill = GridBagConstraints.BOTH;
			consGridBagConstraints2.weightx = 1.0;
			consGridBagConstraints2.gridy = 1;
			consGridBagConstraints2.gridx = 0;
			consGridBagConstraints2.insets = new Insets(5,5,5,5);
			consGridBagConstraints2.gridwidth = 0;
			consGridBagConstraints2.anchor = GridBagConstraints.NORTH;
			consGridBagConstraints2.weighty = 0.0D;
			consGridBagConstraints2.ipady = 5;
			updateCP.setLayout(new GridBagLayout());
			updateCP.add(getUpdateMsgLbl(), consGridBagConstraints1);
			updateCP.add(getUpdatePB(), consGridBagConstraints2);
		}
		return updateCP;
	}
	/**
	 * This method initializes jDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	private JDialog getUpdateDlg() {
		if(updateDlg == null) {
			updateDlg = new JDialog(parent,true);
			updateDlg.setContentPane(getUpdateCP());
			updateDlg.setSize(368, 127);
			updateDlg.setTitle("Software Updates");
			updateDlg.setModal(true);
			updateDlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		}
		return updateDlg;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private JLabel getUpdateMsgLbl() {
		if(updateMsgLbl == null) {
			updateMsgLbl = new JLabel();
			updateMsgLbl.setText("Initializing...");
		}
		return updateMsgLbl;
	}
	/**
	 * This method initializes jProgressBar
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getUpdatePB() {
		if(updatePB == null) {
			updatePB = new JProgressBar();
		}
		return updatePB;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.applet.CodeUpdater#emitPercent(int)
	 */
	public void emitPercent(int pct) {
		getUpdatePB().setValue(pct);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.applet.CodeUpdater#emitStatusText(java.lang.String)
	 */
	public void emitStatusText(String msg) {
		getUpdateMsgLbl().setText(msg);
	}
	
	public void emitFailureText(String title, String msg) {
		// Hide the progress dialog and show the error.
		getErrorTA().setText("**** " + title + " ****\n\n" + msg);
		getUpdateDlg().setVisible(false);
		getErrorDlg().setLocationRelativeTo(parent);
		getErrorDlg().setVisible(true);
		
		// Once returned (blocking modal dialog), re-show the update dialog.
		// Show the updater dialog.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getUpdateDlg().setLocationRelativeTo(parent);
				getUpdateDlg().setVisible(true);
			}
		});

		// Wait for it to appear.
		while (! getUpdateDlg().isShowing()) {
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}
		}
	}

	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.applet.CodeUpdater#update()
	 */
	public int update() {
		// Show the updater dialog.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getUpdateDlg().setLocationRelativeTo(parent);
				getUpdateDlg().setVisible(true);
			}
		});

		// Wait for it to appear.
		while (! getUpdateDlg().isShowing()) {
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}
		}

		// Do the updates.
		int result = super.update();

		// Updates were available?
		if (result == UPDATE) {
			getUpdateMsgLbl().setText("Updates downloaded, restarting...");
		}
		else if (result == FAIL) {
			getUpdateMsgLbl().setText("Exiting...");
		}

		try {
			Thread.sleep(1500);
		}
		catch (InterruptedException e) {
		}

		getUpdateDlg().setVisible(false);

		return result;
	}
	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getErrorCP() {
		if (errorCP == null) {
			errorCP = new JPanel();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			errorCP.setLayout(new GridBagLayout());
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.insets = new java.awt.Insets(0,5,5,5);
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints2.insets = new java.awt.Insets(5,5,5,5);
			errorCP.add(getErrorOkBtn(), gridBagConstraints1);
			errorCP.add(getErrorSP(), gridBagConstraints2);
		}
		return errorCP;
	}
	/**
	 * This method initializes jDialog	
	 * 	
	 * @return javax.swing.JDialog	
	 */    
	private JDialog getErrorDlg() {
		if (errorDlg == null) {
			errorDlg = new JDialog(parent,true);
			errorDlg.setContentPane(getErrorCP());
			errorDlg.setTitle("Automatic Update Error");
			errorDlg.setSize(366, 306);
			errorDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		}
		return errorDlg;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getErrorOkBtn() {
		if (errorOkBtn == null) {
			errorOkBtn = new JButton();
			errorOkBtn.setText("Close");
			errorOkBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getErrorDlg().dispose();
				}
			});
		}
		return errorOkBtn;
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
	private JTextArea getErrorTA() {
		if (errorTA == null) {
			errorTA = new JTextArea();
			errorTA.setLineWrap(true);
			errorTA.setWrapStyleWord(true);
		}
		return errorTA;
	}
     }
