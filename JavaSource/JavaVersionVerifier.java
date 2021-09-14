import java.lang.reflect.Method;

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

public class JavaVersionVerifier {
	public JavaVersionVerifier() {
	}
	public static void main(String args[]) {
		String s = "1.0";

		try {
			Class c = Class.forName("java.io.File");
			Method m = c.getMethod("deleteOnExit", null);
			if (m != null) {
				s = "1.2";
			}
		} catch(NoSuchMethodException e) {
			s = "1.1";
		} catch(ClassNotFoundException e) {
		}

		if (s.equals("1.2")) {
			try {
				Class c = Class.forName("java.util.Timer");
				if (c != null) {
					s = "1.3";
				}
			} catch(ClassNotFoundException e) {
			}
		}

		if (s.equals("1.3")) {
			try {
				Class c = Class.forName("java.net.SocketAddress");
				if (c != null) {
					s = "1.4";
				}
			} catch(ClassNotFoundException e) {
			}
		}

		if (s.equals("1.4")) {
			try {
				Class c = Class.forName("java.net.CacheRequest");
				if (c != null) {
					s = "1.5";
				}
			} catch(ClassNotFoundException e) {
			}
		}

		if (s.equals("1.5")) {
			try {
				Class c = Class.forName("javax.swing.TableRowSorter");
				if (c != null) {
					s = "1.6";
				}
			} catch(ClassNotFoundException e) {
			}
		}

		System.out.println(s);
		System.exit(0);
	}
}
