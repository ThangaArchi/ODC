package oem.edge.ed.odc.applet;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (8/23/2001 11:33:32 AM)
 * @author: Mike Zarnick
 */
public class AboutPnl extends JPanel {
	private JLabel ivjJLabel1 = null;
	private JLabel ivjJLabel2 = null;
	private JLabel ivjJLabel3 = null;
	private JLabel ivjJLabel4 = null;
	private JLabel ivjJLabel5 = null;
	private JLabel ivjJLabel6 = null;
	private JLabel ivjCompLbl = null;
/**
 * AboutDSC constructor comment.
 */
public AboutPnl() {
	super();
	initialize();
}
/**
 * AboutDSC constructor comment.
 * @param layout java.awt.LayoutManager
 */
public AboutPnl(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * AboutDSC constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public AboutPnl(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * AboutDSC constructor comment.
 * @param isDoubleBuffered boolean
 */
public AboutPnl(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Return the CompLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getCompLbl() {
	if (ivjCompLbl == null) {
		try {
			ivjCompLbl = new javax.swing.JLabel();
			ivjCompLbl.setName("CompLbl");
			ivjCompLbl.setText("Component Name");
			ivjCompLbl.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCompLbl;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/applet/Java_Compatible.gif")));
			ivjJLabel1.setText("");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Client Software for Design Solutions");
			ivjJLabel2.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("Version 1.5.0");
			ivjJLabel3.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setText("December 31, 2001");
			ivjJLabel4.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setText("Copyright IBM Corp, 2001 - 2006");
			ivjJLabel5.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
}
/**
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel6() {
	if (ivjJLabel6 == null) {
		try {
			ivjJLabel6 = new javax.swing.JLabel();
			ivjJLabel6.setName("JLabel6");
			ivjJLabel6.setText("All Rights Reserved.");
			ivjJLabel6.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel6;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("AboutDSC");
		setLayout(new java.awt.GridBagLayout());
		setSize(328, 200);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
constraintsJLabel1.gridheight = 0;
		constraintsJLabel1.insets = new java.awt.Insets(10, 10, 10, 15);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 1; constraintsJLabel2.gridy = 0;
		constraintsJLabel2.anchor = java.awt.GridBagConstraints.SOUTH;
		constraintsJLabel2.weighty = 1.0;
		constraintsJLabel2.insets = new java.awt.Insets(10, 0, 5, 10);
		add(getJLabel2(), constraintsJLabel2);

		java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
		constraintsJLabel3.gridx = 1; constraintsJLabel3.gridy = 2;
		constraintsJLabel3.insets = new java.awt.Insets(5, 0, 5, 10);
		add(getJLabel3(), constraintsJLabel3);

		java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
		constraintsJLabel4.gridx = 1; constraintsJLabel4.gridy = 3;
		constraintsJLabel4.insets = new java.awt.Insets(5, 0, 5, 10);
		add(getJLabel4(), constraintsJLabel4);

		java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
		constraintsJLabel5.gridx = 1; constraintsJLabel5.gridy = 4;
		constraintsJLabel5.insets = new java.awt.Insets(5, 0, 5, 10);
		add(getJLabel5(), constraintsJLabel5);

		java.awt.GridBagConstraints constraintsJLabel6 = new java.awt.GridBagConstraints();
		constraintsJLabel6.gridx = 1; constraintsJLabel6.gridy = 5;
		constraintsJLabel6.anchor = java.awt.GridBagConstraints.NORTH;
		constraintsJLabel6.weighty = 1.0;
		constraintsJLabel6.insets = new java.awt.Insets(5, 0, 10, 10);
		add(getJLabel6(), constraintsJLabel6);

		java.awt.GridBagConstraints constraintsCompLbl = new java.awt.GridBagConstraints();
		constraintsCompLbl.gridx = 1; constraintsCompLbl.gridy = 1;
		constraintsCompLbl.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getCompLbl(), constraintsCompLbl);

		this.setBorder(javax.swing.BorderFactory.createMatteBorder(4,4,4,4,java.awt.Color.gray));

		getJLabel3().setText("Version " + oem.edge.ed.odc.misc.AboutTime.DSCVER);
		getJLabel4().setText(oem.edge.ed.odc.misc.AboutTime.buildtime);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AboutPnl aAboutDSC;
		aAboutDSC = new AboutPnl();
		frame.setContentPane(aAboutDSC);
		frame.setSize(aAboutDSC.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/7/2001 11:28:54 AM)
 * @param name java.lang.String
 */
public void setCompName(String name) {
	getCompLbl().setText(name);
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
