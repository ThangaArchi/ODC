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

import java.io.*;
public class PipeReader implements Runnable {
   protected InputStream in;
   protected String name = null;
   protected boolean showoutput = true;
   protected OutputStream out = null;
/**
 * Insert the method's description here.
 * Creation date: (6/18/2002 2:01:49 PM)
 * @param in java.io.InputStream
 * @param out java.io.OutputStream
 */
public PipeReader(InputStream in, OutputStream out) {
	this.in = in;
	this.out = out;
}
   public PipeReader(String name, InputStream in) {
      this.name = name;
      this.in = in;
   }
   public PipeReader(String name, InputStream in, boolean debug) {
      this.name = name;
      this.in = in;
      showoutput = true;
   }
public void run() {
	try {
		String line;
		BufferedReader rdr = new BufferedReader(new InputStreamReader(in));

		// System.out.println("Entering read loop.");

		while ((line = rdr.readLine()) != null) {
			if (showoutput) {
				if (name != null) {
					if (out == null) {
						System.out.print(name);
						System.out.print(": ");
					}
					else {
						out.write(name.getBytes());
						out.write(':');
						out.write(' ');
					}
				}

				if (out == null)
					System.out.println(line);
				else {
					out.write(line.getBytes());
					out.write('\n');
				}
			}
		}

		// System.out.println("Exited read loop.");

		in.close();
		if (out != null) out.close();
	}
	catch (Exception e) {
		System.out.println("PipeReader: " + name + " " + e.getMessage());
	}

	// System.out.println("Pipe Reader thread ended.");
}
   public void setShowOutput(boolean v) {
      showoutput = v;
   }
}
