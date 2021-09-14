/*
 * Created on Mar 10, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.dropbox.client.soa;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import javax.swing.event.MouseInputAdapter;

/**
 * @author zarnick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JCalendarChooser extends JPanel {
	static private String[] MONTHS = {
		"January", "February", "March", "April", "May", "June",
		"July", "August", "September", "October", "November", "December"
	};
	static private int WIDTH = 172;
	static private int HEIGHT = 187;

	private GregorianCalendar gc = new GregorianCalendar();
	private int[] WEEKDAY = null;
	private Date hilightDate = null;
	private Date earliestDate = null;
	private Date latestDate = null;
	private Date selectedDate = null;
	private Component oldGlassPane = null;
	private JPanel glassPane = null;
	private ActionListener listenerList = null;

	private JPanel monthPnl = null;
	private JButton monthPrevBtn = null;
	private JButton monthNextBtn = null;
	private JLabel monthLbl = null;
	private JLabel sunLbl = null;
	private JLabel monLbl = null;
	private JLabel tueLbl = null;
	private JLabel wedLbl = null;
	private JLabel thuLbl = null;
	private JLabel friLbl = null;
	private JLabel satLbl = null;
	private JButton[][] days = null;

	private class DayBtnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			peelOffCalendar();
			fireActionPerformed(e);
		}
	}
	private DayBtnListener dayBtnListener = new DayBtnListener();
	private class GlassPaneMouseHandler extends MouseInputAdapter {
		public void mousePressed(MouseEvent e) {
			peelOffCalendar();
			// TODO: Redispatch mouse event.
		}
	}
	private GlassPaneMouseHandler glassPaneMouseHandler = new GlassPaneMouseHandler();

	/**
	 * 
	 */
	public JCalendarChooser() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        java.awt.GridBagConstraints consGridBagConstraints1 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints5 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints6 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints7 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints9 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints10 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints8 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints11 = new java.awt.GridBagConstraints();
        consGridBagConstraints6.gridy = 1;
        consGridBagConstraints6.gridx = 1;
        consGridBagConstraints7.gridy = 1;
        consGridBagConstraints7.gridx = 2;
        consGridBagConstraints1.gridy = 0;
        consGridBagConstraints1.gridx = 0;
        consGridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints1.weightx = 1.0D;
        consGridBagConstraints1.weighty = 0.0D;
        consGridBagConstraints1.insets = new java.awt.Insets(2,2,4,2);
        consGridBagConstraints1.gridwidth = 0;
        consGridBagConstraints9.gridy = 1;
        consGridBagConstraints9.gridx = 4;
        consGridBagConstraints5.gridy = 1;
        consGridBagConstraints5.gridx = 0;
        consGridBagConstraints5.weightx = 1.0D;
        consGridBagConstraints11.gridy = 1;
        consGridBagConstraints11.gridx = 6;
        consGridBagConstraints11.weightx = 1.0D;
        consGridBagConstraints6.weightx = 1.0D;
        consGridBagConstraints8.gridy = 1;
        consGridBagConstraints8.gridx = 3;
        consGridBagConstraints8.weightx = 1.0D;
        consGridBagConstraints10.gridy = 1;
        consGridBagConstraints10.gridx = 5;
        consGridBagConstraints10.weightx = 1.0D;
        consGridBagConstraints7.weightx = 1.0D;
        consGridBagConstraints9.weightx = 1.0D;
        this.setLayout(new java.awt.GridBagLayout());
        this.add(getMonthPnl(), consGridBagConstraints1);
        this.add(getSunLbl(), consGridBagConstraints5);
        this.add(getMonLbl(), consGridBagConstraints6);
        this.add(getTueLbl(), consGridBagConstraints7);
        this.add(getWedLbl(), consGridBagConstraints8);
        this.add(getThuLbl(), consGridBagConstraints9);
        this.add(getFriLbl(), consGridBagConstraints10);
        this.add(getSatLbl(), consGridBagConstraints11);

		this.days = new JButton[6][7];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				days[i][j] = new JButton();
				days[i][j].setPreferredSize(new java.awt.Dimension(22,22));
				days[i][j].setMinimumSize(new java.awt.Dimension(22,22));
				days[i][j].setMaximumSize(new java.awt.Dimension(22,22));
				days[i][j].setMargin(new java.awt.Insets(0,0,0,0));
				days[i][j].setText(Integer.toString(i*7+j+1));
				days[i][j].addActionListener(dayBtnListener);
				
				GridBagConstraints daysGBC = new GridBagConstraints();
				daysGBC.insets = new Insets(1,1,1,1);
				daysGBC.gridy = i + 2;
				daysGBC.gridx = j;
				daysGBC.anchor = GridBagConstraints.NORTH;
				this.add(days[i][j],daysGBC);
			}
		}

        this.setSize(WIDTH, HEIGHT);
        this.setPreferredSize(new java.awt.Dimension(WIDTH,HEIGHT));
        this.setMinimumSize(new java.awt.Dimension(WIDTH,HEIGHT));
		this.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.SoftBevelBorder.RAISED));
        
        WEEKDAY = new int[gc.getMaximum(Calendar.DAY_OF_WEEK)+1];
        WEEKDAY[Calendar.SUNDAY] = 0;
		WEEKDAY[Calendar.MONDAY] = 1;
		WEEKDAY[Calendar.TUESDAY] = 2;
		WEEKDAY[Calendar.WEDNESDAY] = 3;
		WEEKDAY[Calendar.THURSDAY] = 4;
		WEEKDAY[Calendar.FRIDAY] = 5;
		WEEKDAY[Calendar.SATURDAY] = 6;
		
		glassPane = new JPanel();
		glassPane.setOpaque(false);
		glassPane.addMouseListener(glassPaneMouseHandler);
		glassPane.setLayout(null);
		glassPane.add(this);
	}
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getMonthPnl() {
		if(monthPnl == null) {
			monthPnl = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints2 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints4 = new java.awt.GridBagConstraints();
			consGridBagConstraints4.gridy = 0;
			consGridBagConstraints4.gridx = 1;
			consGridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints4.weightx = 1.0D;
			consGridBagConstraints4.insets = new java.awt.Insets(0,2,0,2);
			consGridBagConstraints3.gridy = 0;
			consGridBagConstraints3.gridx = 2;
			consGridBagConstraints2.gridy = 0;
			consGridBagConstraints2.gridx = 0;
			monthPnl.setLayout(new java.awt.GridBagLayout());
			monthPnl.add(getMonthPrevBtn(), consGridBagConstraints2);
			monthPnl.add(getMonthNextBtn(), consGridBagConstraints3);
			monthPnl.add(getMonthLbl(), consGridBagConstraints4);
		}
		return monthPnl;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getMonthPrevBtn() {
		if(monthPrevBtn == null) {
			monthPrevBtn = new javax.swing.JButton();
			monthPrevBtn.setText("<<");
			monthPrevBtn.setMargin(new java.awt.Insets(0,0,0,0));
			monthPrevBtn.setToolTipText("");
			monthPrevBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gc.add(Calendar.MONTH,-1);
					populateCalendar();
				}
			});
		}
		return monthPrevBtn;
	}
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getMonthNextBtn() {
		if(monthNextBtn == null) {
			monthNextBtn = new javax.swing.JButton();
			monthNextBtn.setText(">>");
			monthNextBtn.setMargin(new java.awt.Insets(0,0,0,0));
			monthNextBtn.setToolTipText("");
			monthNextBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gc.add(Calendar.MONTH,1);
					populateCalendar();
				}
			});
		}
		return monthNextBtn;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getMonthLbl() {
		if(monthLbl == null) {
			monthLbl = new javax.swing.JLabel();
			monthLbl.setText("January 2005");
			monthLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			monthLbl.setToolTipText("Current month");
		}
		return monthLbl;
	}
	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getSunLbl() {
		if(sunLbl == null) {
			sunLbl = new javax.swing.JLabel();
			sunLbl.setText("Su");
		}
		return sunLbl;
	}
	/**
	 * This method initializes jLabel2
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getMonLbl() {
		if(monLbl == null) {
			monLbl = new javax.swing.JLabel();
			monLbl.setText("M");
		}
		return monLbl;
	}
	/**
	 * This method initializes jLabel3
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getTueLbl() {
		if(tueLbl == null) {
			tueLbl = new javax.swing.JLabel();
			tueLbl.setText("Tu");
		}
		return tueLbl;
	}
	/**
	 * This method initializes jLabel4
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getWedLbl() {
		if(wedLbl == null) {
			wedLbl = new javax.swing.JLabel();
			wedLbl.setText("W");
		}
		return wedLbl;
	}
	/**
	 * This method initializes jLabel5
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getThuLbl() {
		if(thuLbl == null) {
			thuLbl = new javax.swing.JLabel();
			thuLbl.setText("Th");
		}
		return thuLbl;
	}
	/**
	 * This method initializes jLabel6
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getFriLbl() {
		if(friLbl == null) {
			friLbl = new javax.swing.JLabel();
			friLbl.setText("F");
		}
		return friLbl;
	}
	/**
	 * This method initializes jLabel7
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getSatLbl() {
		if(satLbl == null) {
			satLbl = new javax.swing.JLabel();
			satLbl.setText("Sa");
		}
		return satLbl;
	}
	
	private void populateCalendar() {
		// Work items needed.
		GregorianCalendar gcClone = new GregorianCalendar();
		Date gcDate = gc.getTime();
		gcClone.setTime(gcDate);
		Date firstDayOfMonth = (earliestDate == null ? null : gcDate);
		int maxDay = gcClone.getActualMaximum(Calendar.DAY_OF_MONTH);
		gcClone.set(Calendar.DAY_OF_MONTH,maxDay);
		Date lastDayOfMonth = (latestDate == null ? null : new Date(gcClone.getTime().getTime()));
		gcClone.setTime(gcDate);

		// Set the month label.
		int month = gc.get(Calendar.MONTH);
		int year = gc.get(Calendar.YEAR);
		getMonthLbl().setText(MONTHS[month] + " - " + year);
		getMonthPrevBtn().setEnabled(earliestDate == null || earliestDate.before(firstDayOfMonth));
		getMonthNextBtn().setEnabled(latestDate == null || latestDate.after(lastDayOfMonth));

		// Determine the day button for 1st day.
		int dayOne = WEEKDAY[gc.get(Calendar.DAY_OF_WEEK)];

		// Determine signed day for 1st day button.
		int dayCnt = 1 - dayOne;

		// Set up for any unavailble days.
		Date date;
		int earliest = -1;
		if (earliestDate != null);

		// Set up the day buttons.
		GridBagLayout l = (GridBagLayout) getLayout();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				// Outside this month's actual days?
				if (dayCnt < 1 || dayCnt > maxDay) {
					// Hide this particular day button.
					days[i][j].setVisible(false);
				}
				// A date we need to show.
				else {
					// Set the day button text and show it.
					days[i][j].setText(Integer.toString(dayCnt));
					days[i][j].setVisible(true);
					
					// Update the date to this day. Enable the day button if it is between
					// the earliest and latest dates, inclusive. Draw the date button
					// in black, unless it is the set date.
					date = gcClone.getTime();
					days[i][j].setForeground(date.compareTo(hilightDate) == 0 ? Color.blue : Color.black);
					days[i][j].setEnabled((earliestDate == null || ! date.before(earliestDate)) &&
										(latestDate == null || ! date.after(latestDate)));
										
					// Reset this day buttons constraints. Last used button pushes all buttons north.
					GridBagConstraints c = l.getConstraints(days[i][j]);
					c.weighty = (dayCnt == maxDay) ? 1.0D : 0.0D;
					l.setConstraints(days[i][j],c);
					
					// Move the close calendar to the next day.
					gcClone.add(Calendar.DAY_OF_MONTH,1);
				}
				dayCnt++;
			}
		}
	}

	public void setEarliestDate(Date d) {
		earliestDate = d;
	}
	public void setLatestDate(Date d) {
		latestDate = d;
	}
	public void setDate(Date d) {
		// Remeber the date, so we can hilight it.
		hilightDate = d;
		
		// Set the calendar to be the 1st day of the month.
		gc.setTime(d);
		gc.set(Calendar.DAY_OF_MONTH,1);
	}

	public void popupRelativeTo(JComponent c) {
		// Update the GUI.
		populateCalendar();
		
		// Swap out the original glassPane for ours.
		JRootPane root = c.getRootPane();
		oldGlassPane = root.getGlassPane();
		root.setGlassPane(glassPane);

		// Determine the location of the upper/left corner
		// relative to the rootpane coordinate space.
		// The rootpane and our glasspane will share the same
		// coordinate space.		
		Point cp = c.getLocation();
		Container p = c.getParent();
		while (p != null && p != root) {
			Point pp = p.getLocation();
			cp.translate(pp.x,pp.y);
			p = p.getParent();
		}

		// Calendar fits below the component?
		if (cp.y + c.getHeight() + HEIGHT < root.getHeight()) {
			cp.y += c.getHeight();
		}

		// No, how about above?
		else if (cp.y - HEIGHT >= 0) {
			cp.y -= HEIGHT;
		}
		
		// No, we need to obscure ourself vertically.
		else {
			cp.y = root.getHeight() - HEIGHT;
		}
		
		// Calendar fits left of the component?
		if (cp.x + c.getWidth() - WIDTH >= 0) {
			cp.x += c.getWidth() - WIDTH;
		}

		// No, if not right of the component, then obscure.
		else if (cp.x + WIDTH > root.getWidth()) {
			cp.x = 0;
		}

		// Set the final location and show it.		
		setLocation(cp);
		glassPane.validate();
		glassPane.setVisible(true);
	}
	private void peelOffCalendar() {
		glassPane.setVisible(false);
	}

	public void addActionListener(ActionListener l) {
		listenerList = AWTEventMulticaster.add(listenerList,l);
	}
	public void removeActionListener(ActionListener oldl) {
		listenerList = AWTEventMulticaster.remove(listenerList,oldl);
	}
	private void fireActionPerformed(ActionEvent e) {
		Date gcDate = gc.getTime();
		GregorianCalendar gcClone = new GregorianCalendar();
		gcClone.setTime(gcDate);
		JButton s = (JButton) e.getSource();
		int day = Integer.parseInt(s.getText());
		gcClone.set(Calendar.DAY_OF_MONTH,day);
		Date d = gcClone.getTime();
		ActionEvent e1 = new ActionEvent(d,e.getID(),e.getActionCommand());
		listenerList.actionPerformed(e1);
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
