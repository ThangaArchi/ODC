package oem.edge.ed.odc.dsmp.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
public class LinkResolver {
	private static String LNKEXT = ".lnk";
	private static boolean initialized = false;
	private static boolean isWindows;

	private static void initialize() {
		if (! initialized) {
			isWindows = System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1;
			initialized = true;
		}
	}

	/**
	 * Trims the .lnk extension off of a windows file. Should only be used on files which
	 * resolve to a different path when resolveLink is called.
	 * @param link
	 * @return String link less the .lnk ending
	 */
	public static String trimLink(String link) {
		initialize();
		String ret = link;

		if (isWindows && ret.endsWith(LNKEXT)) {
			ret = link.substring(0,link.length()-LNKEXT.length());
		}

		return ret;
	}

	/**
	 * Resolves a .lnk file on Windows(R) to the target file.
	 * @param link
	 * @return String of target file, or link if not a valid windows .lnk file
	 */
	public static String resolveLink(String link) {
		initialize();
		String ret = link;

		if (isWindows && ret.endsWith(LNKEXT)) {
			File file = new File(ret);
			if (file.exists()) {
				try {
					// Create temporary files for script and results.
					File script = File.createTempFile("lnkres",".js");
					File output = File.createTempFile("lnkres",".out");

					// Create the windows host script (JScript).
					FileWriter writer = new FileWriter(script);
					writer.write("// Script to resolve shortcuts to actual file or directory.\n");
					writer.write("var args = WScript.Arguments;\n");
					writer.write("var FSO = new ActiveXObject(\"Scripting.FileSystemObject\");\n");
					writer.write("var SHELL = WScript.CreateObject(\"WScript.Shell\");\n");
					writer.write("var output = args(0);\n");
					writer.write("var myFile = FSO.CreateTextFile(output,true);\n");
					writer.write("for (i = 1; i < args.length; i++) {\n");
					writer.write("  if (args(i).lastIndexOf(\"" + LNKEXT + "\") == (args(i).length - " + LNKEXT.length() + ")) {\n");
					writer.write("    if (FSO.FileExists(args(i))) {\n");
					writer.write("      var link = SHELL.CreateShortcut(args(i));\n");
					writer.write("      myFile.WriteLine(link.TargetPath);\n");
					writer.write("    }\n");
					writer.write("    else {\n");
					writer.write("      myFile.WriteLine(args(i));\n");
					writer.write("    }\n");
					writer.write("  }\n");
					writer.write("  else {\n");
					writer.write("    myFile.WriteLine(args(i));\n");
					writer.write("  }\n");
					writer.write("}\n");
					writer.close();
	
					// Keep following the link until we get to an end file or it does not exist.
					while (ret.endsWith(LNKEXT) && file.exists()) {
						Process p = Runtime.getRuntime().exec("wscript \"" + script.getPath() + "\" \"" + output.getPath() + "\" \"" + ret + "\"");
						p.waitFor();
						FileReader reader = new FileReader(output);
						BufferedReader br = new BufferedReader(reader);
						ret = br.readLine();
						br.close();
						file = new File(ret);
					}
					
					// Remove temporary files.
					script.delete();
					output.delete();
				} catch (IOException e) {
				} catch (InterruptedException e) {
				}
			}
		}

		return ret;
	}
}
