/**
 * @author $Author$
 * @version $Id$
 * Usage: java PropertyFileTool -p PRF -d DIR
 * where PRF is the name of the password resource bundle (password properties file)
 * and DIR is the directory structure to scan
 */
package oem.edge.tools;

import java.util.*;
import java.io.*;
import java.text.*;
import org.apache.commons.cli.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class PropertyFileTool {
	public final static String VERSION = "1.9";
	private String rbName = null;
	private ResourceBundle rb = null;
	private String directory = ".";
	private String extension = "-pft";
	private boolean cleanInstall = true;
	
       // JMC 02/16/06 	
	private boolean directReplace = false;	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss-zzz");
       /* Added JMC 4/15/05 */
        private Vector filesets           = new Vector();
       /* Added JMC 4/15/05 */
        private String backupExt          = "-orig";
       /* Added JMC 4/15/05 */
        private String appendSuffix       = "";
       /* Added JMC 4/15/05 */
        private boolean saveOrig          = true;
       /* Added JMC 4/15/05 */
        private boolean saveWhenNoChanges = true;
       /* Added JMC 4/15/05 */
        private boolean showwarnings      = true;
        
	
	public PropertyFileTool() {
		log("PropertyFileTool() v"+VERSION+" classloader="+getClass().getClassLoader()+" system.class.path="+System.getProperty("java.class.path"));
	}
	
	public PropertyFileTool(String resourceBundle) {
		log("PropertyFileTool("+resourceBundle+") v"+VERSION+" classloader="+getClass().getClassLoader()+" system.class.path="+System.getProperty("java.class.path"));	
		setResourceBundle(resourceBundle);	
	}
	
	public PropertyFileTool(ResourceBundle rb) {
		log("PropertyFileTool(ResourceBundle) v"+VERSION+" classloader="+getClass().getClassLoader()+" system.class.path="+System.getProperty("java.class.path"));
		setResourceBundle(rb);
	}
	
	public void log(String text) {
		System.out.println(text);
	}
                
       /* Added JMC 4/15/05 */
	public void addFileset(FileSet fs) {
           filesets.addElement(fs);
	}
        
       /* Added JMC 4/15/05 */
	public void setBackupExtension(String s) {
           backupExt = s;
	}
        
       /* Added JMC 4/15/05 */
	public void setAppendSuffix(String s) {
           appendSuffix = s;
	}
        
       /* Added JMC 4/15/05 */
	public void setShowWarnings(boolean v) {
           showwarnings = v;
	}
        
       /* Added JMC 4/15/05 */
	public void setSaveOriginals(boolean v) {
           saveOrig = v;
        }
        
       /* Added JMC 4/15/05 */
	public void setSaveWhenNoChanges(boolean v) {
           saveWhenNoChanges = v;
        }
        

	private String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}
	
	public void setCleanInstall(boolean b) {
		log("setCleanInstall("+b+")");
		cleanInstall = b;
	}
	
	public void setDirectReplace(boolean b) {
		log("directReplace("+b+")");
		directReplace = b;
	}
		
	public void setResourceBundle(String rbName) {
		log("setResourceBundle("+rbName+")");
		this.rbName = rbName;
		try {
			rb = ResourceBundle.getBundle(rbName);
		} catch (Exception x) {
			log(getStackTrace(x));
		}
	}
        
	
	public void setDirectory(String directory) {
		log("setDirectory("+directory+")");
		this.directory = directory;
	}

	public void setResourceBundle(ResourceBundle rb) {
		log("setResourceBundle(ResourceBundle)");
		this.rb = rb;
	}
	
	public ResourceBundle getResourceBundle() {
		return rb;
	}
	
       // JMC 4/15/05 - Changed to re-scan the line until no changes, and returns 
       //                number of changes were found
	public int processFile(File inputFile, File outputFile) 
           throws FileNotFoundException, IOException {
            
           log("processing ["+inputFile.getPath()+"] ...");
           int n = 0;
           BufferedReader reader = new BufferedReader(new FileReader(inputFile));
           PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
           String line = null;
           
           while ((line = reader.readLine()) != null) {		
           
             // Loop until all replacements are made
              int start = 0;
              int loopchk = 100;
              while(start >= 0 && loopchk > 0) {
                 start = line.indexOf("${", start);
                 if (start >= 0) {
                    int end = line.indexOf("}", start);
                    if (end >= 0) {
                       try {
                          String property = line.substring(start+2, end);
                          String propertyValue = rb.getString(property); 
                          
                          String newLine = line.substring(0, start) +
                             propertyValue + line.substring(end+1);
                          line = newLine;
                          
                         // JMC Start scan at same start point
                          end=start;
                          n++;				
                       } catch (MissingResourceException x) {
                         // JMC 4/15/05 avoid these warnings
                          if (showwarnings) {
                             log("Warning: "+x);
                          }
                          
                         // JMC Scan again skipping ${, as it COULD be some
                         //     funky regular expression which would cause us
                         //     to miss a REAL substitution
                          start+=2;
                       }
                    }
                   // JMC Scan from end pos
                    start=end;   
                 }
                 
                // JMC Don't loop forever if a cycle is created
                 loopchk--;
              }
              
              writer.println(line);	
           }
           writer.close();
           reader.close();
           log("modified "+n+" lines.");
           return n;
	}
   
   
        Project project = null;
        public void setProject(Project inP) {
           project = inP;
        }
   
       // JMC 4/15/05. Process all the filesets
	public void processFilesets() throws Exception {
           Enumeration enum = filesets.elements();
           while(enum.hasMoreElements()) {
              FileSet fs = (FileSet)enum.nextElement();
              DirectoryScanner ds = fs.getDirectoryScanner(project);
              String files[] = ds.getIncludedFiles();
                 
              String dirprefix = ds.getBasedir().getCanonicalPath() + 
                 File.separatorChar;
              
              for(int i=0; files != null && i < files.length; i++) {
                 String thisfile = dirprefix + files[i];
                 String origfile = thisfile + appendSuffix + backupExt;
                 
                 File f    = new File(thisfile);
                 File orig = new File(origfile);
                 
                // If we are not using appendSuffix, just rename 
                 if (appendSuffix.equals("")) {
                    if (!f.renameTo(orig)) {
                       log("Error renaming [" + thisfile + "] to [" + origfile + "]");
                       throw new Exception("Rename Exception");
                    }
                 } else {
                   // ... otherwise, copy it
                    try {
                       FileInputStream is = new FileInputStream(f);
                       FileOutputStream os = new FileOutputStream(orig);
                       byte buf[] = new byte[1024];
                       int r;
                       while((r=is.read(buf)) >= 0) {
                          os.write(buf, 0, r);
                       }
                       is.close();
                       os.close();
                    } catch(IOException ce) {
                       log("Error copying [" + thisfile + "] to [" + origfile + "]");
                       throw new Exception("Copy Exception");
                    }
                 }
                 
                // Now add on the append suffix
                 f    = new File(thisfile + appendSuffix);
                 
                 int num = processFile(orig, f);
                 if ((!saveOrig || (num == 0 && !saveWhenNoChanges))) {
                    orig.delete();
                 }
              }
           }
        }
   
        // JMC 4/15/05. Not touching this ... works the way it always did
	// JMC 2/16/06  -R does !cleanInstall by overwriting non-orig files
	public void processDirectoryTree(int depth, File file) throws Exception {
		String backupExtension = extension+dateFormat.format(new Date());
		String findExtension = ".properties";
		if (!cleanInstall || directReplace) {
			findExtension = backupExt;
		} else {
			backupExtension = backupExt;
		}
		if (file.isDirectory()) {
			File files[] = file.listFiles();
			for (int i=0; i<files.length; i++) {
				if (files[i].isFile() && files[i].getName().endsWith(findExtension)) {
					String name = files[i].getPath();
					if (directReplace) {
					   processFile(new File(name), new File(name.substring(0, name.length()-backupExt.length())));					   
					} else {
					   files[i].renameTo(new File(name+backupExtension));
					   processFile(new File(name+backupExtension), new File(name));					   
					}
				} else if (files[i].isDirectory()) {
					processDirectoryTree(depth+1, files[i]);
				}
			}
		}
	}
	
	public void execute() throws Exception {
         try {
                  /*
                  ** JMC 4/15/05 - added filesets processing. If a fileset is 
                  **               specified, no directory recursion is done
                  */
                   if (filesets.size() > 0) {
                      processFilesets();
                   } else {
                      processDirectoryTree(0, new File(directory));
                   }
		} catch (Exception x) {
			log(getStackTrace(x));
			throw x;
		}
	}
	
	/* */
	public static void main(String args[]) {
		int exitCode = 1;
		Options options = new Options();
		options.addOption('p', "passwordResourceBundle", true, "The resource bundle that contains the passwords.");
		options.addOption('d', "directory", true, "The directory to traverse to find properties files.");
		options.addOption('r', "redo", false, "Run against the original properties files.  Useful when the tool has to be rerun.");
		
		// JMC 02/16/06 
		options.addOption('R', "Redo", false, "Like -r, but truly replaces old file (no backup). eg. a.properties-orig will produce a.properties");		
		try {
			CommandLine cmdLine = options.parse(args);
			String resourceBundle = cmdLine.getOptionValue('p');
			String directory = cmdLine.getOptionValue('d');
			boolean redo = cmdLine.getOptionValue('r') != null;
			
		// JMC 02/16/06 Added Redo option check and mutual exclusion chk in if 			
			boolean Redo = cmdLine.getOptionValue('R') != null;			
			if (resourceBundle == null || directory == null || (redo && Redo)) {
				(new HelpFormatter()).printHelp(" ", options);
				exitCode = 1;
			} else {
				//PropertyFileTool pft = new PropertyFileTool(resourceBundle);
				PropertyFileTool pft = new PropertyFileTool();
				pft.setResourceBundle(resourceBundle);
				pft.setDirectory(directory);
				pft.setCleanInstall(!redo);
				
			// JMC 02/16/06 				
				pft.setDirectReplace(Redo);				
				pft.execute();
				//pft.processDirectoryTree(0, new File(directory));
				exitCode = 0;
			}
		} catch (Exception x) {
			x.printStackTrace();
			exitCode = 2;
		}
		System.exit(exitCode);
	}
	/* */
}
