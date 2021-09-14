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
package oem.edge.authentication;

import java.util.*;
import java.io.*;

/**
 * @author bjr
 */
public class ConfigurationException extends Exception {
	public final static String VERSION = "1.3";
	private Throwable source = null;
	
	public ConfigurationException(String s) {
		super(s);
	}
	
	public ConfigurationException(String s, Throwable t) {
		super(s);
		source = t;
	}
	
	private void printSourceStackTrace(PrintWriter pw) {
		pw.println("Source Exception: ");
		source.printStackTrace(pw);
	}
	
	public void printStackTrace() {
		super.printStackTrace();
		printSourceStackTrace(new PrintWriter(System.err));
	}
	
	public void printStackTrace(PrintStream ps) {
		PrintWriter pw = new PrintWriter(ps);
		super.printStackTrace(pw);
		printSourceStackTrace(pw);
	}
	
	public void printStackTrace(PrintWriter pw) {
		super.printStackTrace(pw);
		printSourceStackTrace(pw);
	}
	
	public String toString() {
		return "ConfigurationException("+this.getMessage()+")";
	}
}
