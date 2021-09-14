package oem.edge.tools;

import java.util.*;
import java.io.*;
import org.apache.commons.cli.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class PropertyFileToolTask extends Task {
	public static final String VERSION = "1.5";

	private PropertyFileTool pft = null;
	
	public PropertyFileToolTask() {
		pft = new PropertyFileTool();
		log("PropertyFileToolTask() v"+VERSION);
	}
	
	public PropertyFileToolTask(String resourceBundle) {
		pft = new PropertyFileTool(resourceBundle);
		log("PropertyFileToolTask("+resourceBundle+") v"+VERSION);
	}
	
	public void log(String text) {
		pft.log("PropertyFileToolTask: "+text);
	}
	
       /* Added JMC 4/15/05 */
	public void addFileset(FileSet fs) {
		pft.log("PropertyFileToolTask: addFileset");
                pft.addFileset(fs);
	}
        
       /* Added JMC 4/15/05 */
	public void setBackupExtension(String s) {
		pft.log("PropertyFileToolTask: setBackupExtension: " + s);
                pft.setBackupExtension(s);
	}
        
       /* Added JMC 4/15/05 */
	public void setAppendSuffix(String s) {
		pft.log("PropertyFileToolTask: setAppendSuffx: " + s);
                pft.setAppendSuffix(s);
	}
        
       /* Added JMC 4/15/05 */
	public void setShowWarnings(boolean v) {
                pft.log("PropertyFileToolTask: setShowWarnings: " + v);
                pft.setShowWarnings(v);
	}
        
       /* Added JMC 4/15/05 */
	public void setSaveOriginals(boolean v) {
		pft.log("PropertyFileToolTask: setSaveOriginals: " + v);
                pft.setSaveOriginals(v);
	}
        
       /* Added JMC 4/15/05 */
	public void setSaveWhenNoChanges(boolean v) {
		pft.log("PropertyFileToolTask: setSaveWhenNoChanges: " + v);
                pft.setSaveWhenNoChanges(v);
	}
        
	public void setResourceBundle(String rbName) {
                log("setResourceBunder("+rbName+")");
		pft.setResourceBundle(rbName);
	}
	
	public void setDirectory(String directory) {
                log("setDirectory("+directory+")");
		pft.setDirectory(directory);
	}

	public void execute() throws BuildException {
		log("execute() classloader="+getClass().getClassLoader()+" system.class.path="+System.getProperty("java.class.path"));
		if (pft.getResourceBundle() == null) {
			log("Error in execute(): Invalid ResourceBundle");
			throw new BuildException("Invalid ResourceBundle");
		}
		try {
                       // JMC ... need the project to get the scanner ... sigh
                        pft.setProject(getProject());
			pft.execute();
		} catch (Exception x) {
			StringWriter sw = new StringWriter();
 			x.printStackTrace(new PrintWriter(sw));
			log("Error in pft.execute(): "+sw);
			throw new BuildException(x);
		}
	}
	
	/**** for MatchingTask ****
	public void setDir(File dir) {
		this.dir = dir;
	}
			
	public void execute() throws BuildException {
		try {
			rb = ResourceBundle.getBundle(rbName);
			DirectoryScanner ds = getDirectoryScanner();
			String files[] = ds.getIncludedFiles();
			for (int i=0; i<files.length; i++) {
				processFile(files[i]);
			}
		} catch (Exception x) {
			throw new BuildException(x);
			//x.printStackTrace();
		}
	}
	***/
		
	public void setResourceBundle(ResourceBundle rb) {
 		log("setResourceBundle("+rb+")");
		pft.setResourceBundle(rb);
	}
	
}


