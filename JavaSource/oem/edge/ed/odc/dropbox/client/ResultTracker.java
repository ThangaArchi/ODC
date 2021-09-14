package oem.edge.ed.odc.dropbox.client;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.SwingUtilities;

import oem.edge.ed.odc.dsmp.client.ErrorRunner;
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

/**
 * @author zarnick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ResultTracker {
	private class State {
		Component parent;
		String errorMsg;
		String title;
		Hashtable items = new Hashtable();
		Vector failed = new Vector();
	}
	private Hashtable results = new Hashtable();

	public void clear() {
		results.clear();
	}
	public void clear(String method) {
		results.remove(method);
	}
	public void init(String method,Component parent,String title,String errMsg) {
		State s = (State) results.get(method);
		if (s == null) {
			s = new State();
			results.put(method,s);
		}
		s.parent = parent;
		s.title = title;
		s.errorMsg = errMsg;
		s.items.clear();
		s.failed.removeAllElements();
	}
	public void track(String method,byte key,String request) {
		State s = (State) results.get(method);
		if (s != null) {
			s.items.put(new Byte(key),request);
		}
	}
	public void result(String method,byte key,String error) {
		State s = (State) results.get(method);
		if (s != null) {
			String r = (String) s.items.remove(new Byte(key));
			if (error != null) {
				s.failed.addElement(r);
			}
			if (s.items.isEmpty() && ! s.failed.isEmpty()) {
				String[] messages = new String[s.failed.size()+1];
				messages[0] = s.errorMsg;
				Enumeration e = s.failed.elements();
				int i = 1;
				while (e.hasMoreElements()) {
					messages[i++] = "  " + (String) e.nextElement();
				}
				ErrorRunner er = new ErrorRunner(s.parent,messages,s.title,true);
				SwingUtilities.invokeLater(er);
			}
		}
	}
}
