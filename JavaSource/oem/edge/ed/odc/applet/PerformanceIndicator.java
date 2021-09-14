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

import java.awt.*;
import javax.swing.*;
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (8/13/2001 12:02:41 PM)
 * @author: Jeetendra Rao
 */
public class PerformanceIndicator extends JPanel {
	class DataPoint {
		public long in;
		public long out;
		public long comp;
		public long total;

		DataPoint(long in, long out, long comp) {
			this.in = in;
			this.out = out;
			this.comp = comp;
			this.total = in + out;
		}
	}
	class DataPointMgr {
		private DataPoint[] data;
		private int maxSize;
		private int viewNumDP = 0;
		private long viewMax = 0;
		private long lifeMax = 0;
		private int nextDP = 0;

		public DataPointMgr() {
			maxSize = Toolkit.getDefaultToolkit().getScreenSize().width / 4 + 1;
			data = new DataPoint[maxSize];
		}
		synchronized public boolean setViewNumber(int x) {
			boolean adjusted = false;

			// Increasing the number of data points displayed?
			if (x > viewNumDP) {
				// Will some data points be exposed? If so, adjust max.
				if (nextDP > viewNumDP) {
					int fdpnv = Math.max(nextDP - x,0); // First new data point to be viewed.
					int ldpnv = nextDP - viewNumDP - 1; // Last new data point to be viewed.
					for (int j = fdpnv; j < ldpnv; j++) {
						if (data[j].total > viewMax) {
							viewMax = data[j].total;
							adjusted = true;
						}
					}
				}

				// Update the number of data points being displayed.
				viewNumDP = x;
			}

			// Decreasing the number of data points displayed?
			else if (x < viewNumDP) {
				// Some data points will now be hidden?
				if (nextDP > x) {
					// See if viewMax value is leaving.
					int fdph = Math.max(nextDP - viewNumDP,0); // First new data point to be hidden.
					int ldph = nextDP - x - 1; // Last new data point to be hidden.

					for (int j = fdph; j < ldph; j++) {
						if (data[j].total == viewMax)
							adjusted = true;
					}

					// viewMax may have changed?
					if (adjusted) {
						// Determine the new max.
						long newMax = 0;
						for (int j = Math.max(nextDP - x,0); j < nextDP; j++) {
							if (data[j].total > newMax)
								newMax = data[j].total;
						}

						// if new max is different, remember it, otherwise max didn't change.
						if (newMax != viewMax)
							viewMax = newMax;
						else
							adjusted = false;
					}
				}

				// Update the number of data points being displayed.
				viewNumDP = x;
			}

			// Not changing the number of data points displayed.
			//System.out.println("setView - viewMax: " + viewMax + " Number: " + viewNumDP + " " + adjusted);
			return adjusted;
		}
		synchronized public long getLifeMax() {
			return lifeMax;
		}
		synchronized public long getMax() {
			return viewMax;
		}
		synchronized public boolean addDP(long in, long out, long comp) {
			DataPoint d = null;
			long viewOut = -1;

			if (nextDP < maxSize) {
				d = new DataPoint(in,out,comp);
				data[nextDP++] = d;
			}
			else {
				d = data[0];
				viewOut = d.total;
				d.in = in;
				d.out = out;
				d.comp = comp;
				d.total = in + out;
				System.arraycopy(data,1,data,0,maxSize - 1);
				data[maxSize - 1] = d;
			}

			boolean adjusted = true;
			if (d.total > lifeMax)
				lifeMax = viewMax = d.total;
			else if (d.total > viewMax)
				viewMax = d.total;
			else if (nextDP > viewNumDP) {
				int j = nextDP - viewNumDP - 1;
				long newMax = viewMax;

				if (viewOut < data[j].total)
					viewOut = data[j].total;

				//System.out.println("viewOut is " + viewOut);
				if (viewOut == viewMax) {
					// Determine the new max.
					newMax = 0;
					for (j = nextDP - viewNumDP; j < nextDP; j++) {
						if (data[j].total > newMax)
							newMax = data[j].total;
					}
				}

				// if new max is different, remember it, otherwise max didn't change.
				if (newMax != viewMax)
					viewMax = newMax;
				else
					adjusted = false;
			}
			else
				adjusted = false;

			//System.out.println("addDP - viewMax: " + viewMax + " " + adjusted);
			return adjusted;
		}
		synchronized public void clear() {
			viewMax = 0;
			lifeMax = 0;
			nextDP = 0;
		}
	}

	private DataPointMgr mgr = null;
	private long inTotal = 0;
	private long outTotal = 0;
	private Image offImage = null;
	private Graphics offGraphics = null;
	private Dimension offDimension = null;
	private Rectangle graphSize = null;
	private Graphics graph = null;
	private boolean newDataPoint = false;
	private Color infg = Color.yellow;
	private Color outfg = Color.green;
/**
 * ProgressBar constructor comment.
 */
public PerformanceIndicator() {
	super();

	mgr = new DataPointMgr();
}
/**
 * ProgressBar constructor comment.
 * @param layout java.awt.LayoutManager
 */
public PerformanceIndicator(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:23:53 AM)
 */
public void clear() {
	inTotal = 0;
	outTotal = 0;

	mgr.clear();

	offGraphics = null;
	repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 11:30:47 AM)
 * @return java.awt.Color
 */
public java.awt.Color getInfg() {
	return infg;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 11:31:27 AM)
 * @return java.awt.Color
 */
public java.awt.Color getOutfg() {
	return outfg;
}
/**
 * Insert the method's description here.
 * Creation date: (8/13/2001 1:27:18 PM)
 * @param g java.awt.Graphics
 */
public void paintComponent(java.awt.Graphics g) {
	Dimension d = this.getSize();

	// Don't have an image or it is the wrong size?
	if (offGraphics == null || d.width != offDimension.width || d.height != offDimension.height) {
		// Prepare to build the scale.
		FontMetrics f = g.getFontMetrics();
		int fh = f.getHeight();
		int fd = f.getDescent();
		int fm = fh / 2;

		// Determine how many bars will visible.
		String life = Long.toString(mgr.getLifeMax());
		String limit = Long.toString(mgr.getMax());
		int newScaleSize = f.stringWidth(limit) + 5;
		int scaleSize = 0;
		int bc = 0;

		// While we need more room for the scale numbers...
		while (newScaleSize > scaleSize) {
			scaleSize = newScaleSize;

			// Compute bar count: round up ((graph area width + 2)/bar width)
			bc = (d.width - scaleSize + 1) / 4;

			// Tell data manager number of bars changed,
			// returns true if visible max is changed.
			// We ensure that at least 1 bar is showing.
			if (bc > 0 && mgr.setViewNumber(bc)) {
				// max changed, determine room needed for string.
				limit = Long.toString(mgr.getMax());
				newScaleSize = f.stringWidth(limit) + 5;
			}
		}

		synchronized (mgr) {
			// Create a new image.
			offDimension = d;
			offImage = createImage(d.width,d.height);
			offGraphics = offImage.getGraphics();

			// Render the graph background.
			offGraphics.setColor(getBackground());
			offGraphics.fillRect(0,0,d.width,d.height);

			// Determine the scale positions.
			int sb = d.height - fh - 4;   // Scale bottom
			int sr = d.width - scaleSize; // Scale right edge
			int st = fh + 2 + fm + 1;     // Scale top

			// If graph area is too small, then punt with a fake graph...
			if (bc < 1 || sb - st < 8) {
				graph = null;
				offGraphics.setColor(getForeground());
				/*int w = d.width - 5;
				int h = d.height - 5;
				int c = 2;
				while (w > 0 && h > 0) {
					offGraphics.drawRect(c,c,w,h);
					c += 4;
					w -= 8;
					h -= 8;*
				}*/
				offGraphics.drawString("Graph",(d.width/2) - (f.stringWidth("Graph")/2),(d.height/2) + fm - fd);
			}
			else {
				// Render the scale.
				offGraphics.setColor(getForeground());
				offGraphics.drawLine(1,sb,sr,sb);      // Draw bottom line
				offGraphics.drawLine(sr,sb,sr,st);     // Draw right edge
				offGraphics.drawLine(sr,st,sr - 2,st); // Draw top mark
				int sm = st + ((sb - st) / 2);
				offGraphics.drawLine(sr,sm,sr - 2,sm); // Drop mid mark
				int y = st + ((sm - st) / 2);
				offGraphics.drawLine(sr,y,sr - 2,y);   // Drop 1/4 mark
				y = sm + ((sb - sm) / 2);
				offGraphics.drawLine(sr,y,sr - 2,y);   // Draw 3/4 mark

				// Render the scale labels.
				offGraphics.drawString(life,d.width - f.stringWidth(life) - 2,1 + fh - fd);
				offGraphics.drawString(limit,sr + 2,st + fm - fd);

				// Set size of graph area.
				graphSize = new Rectangle(1,st,sr - 3,sb - st);
				graph = offGraphics.create(graphSize.x,graphSize.y,graphSize.width,graphSize.height);

				// Render as many data bars as possible.
				int x = graphSize.width - 3;
				for (int i = mgr.nextDP - 1; i >= 0 && x > -2; i--) {
					// Render next data point.
					renderBar(x,mgr.data[i]);

					// Next bar will be 5 pixels left.
					x -= 4;
				}

				// Render the I/O rate numbers.
				renderIO(g,(mgr.nextDP > 0) ? mgr. data[mgr.nextDP - 1] : null);
			}
		} // synchronized (mgr)
	}
	else if (newDataPoint) {
		if (graph != null) {
			synchronized(mgr) {
			// Shift the graph area left by 1 bar (7 pixels).
			graph.copyArea(4,0,graphSize.width - 5,graphSize.height - 1,-4,0);

			// Get the data point.
			DataPoint dp = mgr.data[mgr.nextDP - 1];

			// Render the bar's background.
			graph.setColor(getBackground());
			graph.fillRect(graphSize.width - 4,0,4,graphSize.height);

			// Render the bar.
			renderBar(graphSize.width - 3,dp);

			// Render the I/O rate numbers.
			renderIO(g,dp);
			} // synchronized(mgr)
		}

		newDataPoint = false;
	}

	// Render the image.
	g.drawImage(offImage,0,0,this);
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2002 11:39:11 AM)
 * @param x int
 */
private void renderBar(int x, DataPoint dp) {
	int outh;

	// Render the bar's out portion.
	if (dp.out > 0 && mgr.getMax() != 0) {
		long h = (graphSize.height * dp.out) / mgr.getMax();
		if (h > Integer.MAX_VALUE) {
			outh = 0;
		}
		else {
			outh = (int) h;
			graph.setColor(outfg);
			graph.fillRect(x,graphSize.height - 1 - outh,2,outh);
		}
	}
	else
		outh = 0;

	// Render the bar's in portion.
	if (dp.in > 0 && mgr.getMax() != 0) {
		long h = (graphSize.height * dp.in) / mgr.getMax();
		if (h <= Integer.MAX_VALUE) {
			int inh = (int) h;
			graph.setColor(infg);
			graph.fillRect(x,graphSize.height - 1 - inh - outh,2,inh);
		}
	}

	// Render the bar's compression ratio.
	if (dp.comp < dp.total && mgr.getMax() != 0) {
		long h = (graphSize.height * dp.comp) / mgr.getMax();
		if (h <= Integer.MAX_VALUE) {
			int comph = (int) h;
			graph.setColor(getBackground());
			graph.fillRect(x,graphSize.height - 2 - comph,2,2);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 1:05:43 PM)
 */
private void renderIO(Graphics g, DataPoint dp) {
	// Render the I/O rate numbers.
	String item;
	int w;
	FontMetrics f = g.getFontMetrics();
	int fh = f.getHeight();
	int fd = f.getDescent();
	int fm = fh / 2;
	int sb = graphSize.height + graphSize.y - 1;

	// Current interval numbers only if they fit vertically.
	if ((graphSize.height > (2 * fh) + 4) && dp != null) {
		// Inbound interval number.
		item = Long.toString(dp.in);
		w = f.stringWidth(item);
		offGraphics.setColor(getBackground());
		int x = graphSize.x + graphSize.width + 4;
		offGraphics.fillRect(x,sb + fd - fh - fh - 2,offDimension.width - x,fh);
		offGraphics.setColor(infg);
		offGraphics.drawString(item,offDimension.width - w - 2,sb - fh - 2);

		// Outbound interval number.
		item = Long.toString(dp.out);
		w = f.stringWidth(item);
		offGraphics.setColor(getBackground());
		offGraphics.fillRect(x,sb + fd - fh,offDimension.width - x,fh);
		offGraphics.setColor(outfg);
		offGraphics.drawString(item,offDimension.width - w - 2,sb);
	}

	// Clear the I/O total area.
	offGraphics.setColor(getBackground());
	offGraphics.fillRect(1,offDimension.height - 2 - fh,graphSize.width,fh);

	// Inbound session total.
	if (inTotal > 10485760)
		item = Long.toString(inTotal / 1024) + "KB";
	else
		item = Long.toString(inTotal);
	w = f.stringWidth(item);
	offGraphics.setColor(infg);
	offGraphics.drawString(item,1,offDimension.height - 2 - fd);

	// A down arrow.
	int[] arrowX = new int[3];
	int[] arrowY = new int[3];
	w += 2;
	arrowX[0] = w;
	arrowX[1] = 6 + w;
	arrowX[2] = 3 + w;
	arrowY[0] = offDimension.height - 5 - fm;
	arrowY[1] = arrowY[0];
	arrowY[2] = offDimension.height + 1 - fm;
	offGraphics.setColor(getForeground());
	offGraphics.fillPolygon(arrowX,arrowY,3);

	// Outbound session total.
	if (outTotal > 10485760)
		item = Long.toString(outTotal / 1024) + "KB";
	else
		item = Long.toString(outTotal);
	w += 15;
	offGraphics.setColor(outfg);
	offGraphics.drawString(item,w,offDimension.height - 2 - fd);

	// An up arrow.
	w += f.stringWidth(item) + 1;
	arrowX[0] = 3 + w;
	arrowX[1] = 6 + w;
	arrowX[2] = w;
	arrowY[0] = offDimension.height - 5 - fm;
	arrowY[1] = offDimension.height + 1 - fm;
	arrowY[2] = arrowY[1];
	offGraphics.setColor(getForeground());
	offGraphics.fillPolygon(arrowX,arrowY,3);
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 11:30:47 AM)
 * @param newInfg java.awt.Color
 */
public void setInfg(java.awt.Color newInfg) {
	infg = newInfg;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 11:31:27 AM)
 * @param newOutfg java.awt.Color
 */
public void setOutfg(java.awt.Color newOutfg) {
	outfg = newOutfg;
}
/**
 * Insert the method's description here.
 * Creation date: (8/13/2001 3:36:27 PM)
 * @param comp int
 */
public void setRate(long in, long out, long comp) {
	if (mgr.addDP(in,out,comp))
		offGraphics = null;

	inTotal += in;
	outTotal += out;

	newDataPoint = true;
	repaint();
}
}
