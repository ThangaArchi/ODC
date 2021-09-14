package oem.edge.ed.odc.tunnel.applet;

/**
 * Insert the type's description here.
 * Creation date: (01/24/01 9:33:37 AM)
 * @author: Michael Zarnick
 */
public class ICAThread extends Thread {
	String host = null;
	int port = 0;
	String initpgm = null;
/**
 * ICAThread constructor comment.
 */
public ICAThread() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (01/24/01 9:36:35 AM)
 * @param address java.lang.String
 * @param host java.lang.String
 * @param id java.lang.String
 */
public ICAThread(String host, int port, String initpgm) {
	super();
	this.host = host;
	this.port = port;
	this.initpgm = initpgm;
}
/**
 * Insert the method's description here.
 * Creation date: (01/24/01 9:38:30 AM)
 */
public void run() {
	String[] args = new String[11];
	args[0]  = "-InitialProgram:" + initpgm;
	args[1]  = "-address:" + host;
	args[2]  = "-ICAPortNumber:" + port;
	args[3]  = "-Border:off";
	args[4]  = "-Start:auto";
	args[5]  = "-End:manual";
	args[6]  = "-user.wfclient.keyboardlayout:US";
	args[7]  = "-EndSessionTimeout:0";
	args[8]  = "-width:1100";
	args[9]  = "-height:900";
	args[10] = "-title:Online_Design_Collab";
	com.citrix.JICA.main(args);
}
}
