package oem.edge.ed.odc.dropbox.client;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import oem.edge.ed.odc.applet.MultiPipeInputStream;
import oem.edge.ed.odc.applet.MultiPipeOutputStream;

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
public class MsgHandler extends JFrame implements Runnable {
	private MultiPipeInputStream stdin;
	private PrintStream realStdout = null;
	private PrintStream realStderr = null;

	private JPanel msgCP = null;
	private JMenuBar msgMenuBar = null;
	private JMenu windowM = null;
	private JMenuItem clearMI = null;
	private JMenuItem closeMI = null;
	private JScrollPane msgSP = null;
	private JTextArea msgTA = null;
	private JCheckBoxMenuItem logMI = null;
	public MsgHandler() {
		this(false);
	}
	public MsgHandler(boolean echo) {
		super();
		initialize();
		getLogMI().setSelected(echo);
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        this.setContentPane(getMsgCP());
        this.setJMenuBar(getMsgMenuBar());
        this.setSize(509, 391);
        this.setTitle("Dropbox Messages");
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			
		// Route stdout and stderr to the message textarea.
		MultiPipeOutputStream stdout = new MultiPipeOutputStream();
		PrintStream stdoutStream = new PrintStream(stdout);
		MultiPipeOutputStream stderr = new MultiPipeOutputStream();
		PrintStream stderrStream = new PrintStream(stderr);
		stdin = new MultiPipeInputStream();

		try {
			stdout.connect(stdin);
			realStdout = System.out;
			System.setOut(stdoutStream);
		} catch (IOException e) {
			System.err.println("MsgHandler: Could not re-route stdout.");
		}

		try {
			stderr.connect(stdin);
			realStderr = System.err;
			System.setErr(stderrStream);
		} catch (IOException e) {
			System.err.println("MsgHandler: Could not re-route stderr.");
		}
		
		WorkerThread t = new WorkerThread(this);
		t.start();
	}
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getMsgCP() {
		if(msgCP == null) {
			msgCP = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints1 = new java.awt.GridBagConstraints();
			consGridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints1.weighty = 1.0;
			consGridBagConstraints1.weightx = 1.0;
			consGridBagConstraints1.gridy = 0;
			consGridBagConstraints1.gridx = 0;
			consGridBagConstraints1.insets = new java.awt.Insets(5,5,5,5);
			msgCP.setLayout(new java.awt.GridBagLayout());
			msgCP.add(getMsgSP(), consGridBagConstraints1);
		}
		return msgCP;
	}
	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private javax.swing.JMenuBar getMsgMenuBar() {
		if(msgMenuBar == null) {
			msgMenuBar = new javax.swing.JMenuBar();
			msgMenuBar.add(getWindowM());
		}
		return msgMenuBar;
	}
	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private javax.swing.JMenu getWindowM() {
		if(windowM == null) {
			windowM = new javax.swing.JMenu();
			windowM.add(getLogMI());
			windowM.add(getClearMI());
			windowM.add(getCloseMI());
			windowM.setText("Window");
		}
		return windowM;
	}
	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getClearMI() {
		if(clearMI == null) {
			clearMI = new javax.swing.JMenuItem();
			clearMI.setText("Clear");
			clearMI.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {  
					getMsgTA().setText(null);
				}
			});
		}
		return clearMI;
	}
	/**
	 * This method initializes jMenuItem1
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getCloseMI() {
		if(closeMI == null) {
			closeMI = new javax.swing.JMenuItem();
			closeMI.setText("Close");
			closeMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					hide();
				}
			});
		}
		return closeMI;
	}
	/**
	 * This method initializes logMI	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBoxMenuItem getLogMI() {
		if (logMI == null) {
			logMI = new JCheckBoxMenuItem();
			logMI.setText("Log to File");
			logMI.setToolTipText("View dropbox.log in install directory");
		}
		return logMI;
	}
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getMsgSP() {
		if(msgSP == null) {
			msgSP = new javax.swing.JScrollPane();
			msgSP.setViewportView(getMsgTA());
		}
		return msgSP;
	}
	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private javax.swing.JTextArea getMsgTA() {
		if(msgTA == null) {
			msgTA = new javax.swing.JTextArea();
		}
		return msgTA;
	}
	public void run() {
		int i = 0;
		int j = 0;
		FileOutputStream out = null;

		try {
			BufferedReader rdr = new BufferedReader(new InputStreamReader(stdin));
			String line;

			while ((line = rdr.readLine()) != null) {
				try {
					j = getMsgTA().getDocument().getLength();

					if (j > 1048576) {
						int k = getMsgTA().getLineOfOffset(524288);
						k = getMsgTA().getLineEndOffset(k);
						getMsgTA().replaceRange("...\n",0,k);
						j = getMsgTA().getDocument().getLength();
					}

					i = getMsgTA().getCaretPosition();

					getMsgTA().append(line);
					getMsgTA().append("\n");

					if (i == j) {
						j += line.length() + 1;
						getMsgTA().setCaretPosition(j);
					}
				}
				catch (Exception e) {
					getMsgTA().setText("...error pruning text...\n");
				}
				
				if (getLogMI().isSelected()) {
					if (out == null) {
						out = new FileOutputStream("dropbox.log",true);
					}
					if (out != null) {
						out.write(line.getBytes());
					}
				}
				else if (out != null) {
					out.close();
					out = null;
				}
			}
			rdr.close();
		}
		catch (Exception e) {
			getMsgTA().append("Fatal error in MsgHandler.run(): " + e.getMessage());
		}
	}
 }  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
