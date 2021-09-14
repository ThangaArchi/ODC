/*
 * Created on Feb 17, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.remoteviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import oem.edge.ed.odc.util.KeystoreInfo;
import oem.edge.ed.odc.util.ShuttleService;
import oem.edge.ed.odc.util.SocketPair;

/**
 * @author zarnick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RemoteAdminServer {
	private String gridToolsDir = null;

	private KeystoreInfo ksi = null;
	private KeystoreInfo tsi = null;

	private static PrintStream stdout;

	/**
	 * Constructor
	 * @param gridToolsDir String path to grid management tools
	 */
	public RemoteAdminServer(String[] args) throws Exception {
		if (args.length != 1 && args.length != 5) {
			throw new Exception("RemoteExecServer gridDir [ keystore password truststore password ]");
		}

		this.gridToolsDir = args[0];
		
		if (args.length == 5) {
			ksi = new KeystoreInfo(args[1],args[2]);
			tsi = new KeystoreInfo(args[3],args[4]);
		}
	}

	/**
	 * Launches grid management tools for the web server.
	 * @param args String path to grid management tools
	 */
	public static void main(String[] args) {
		// Protect stdout from code generated messages, route to stderr.
		stdout = System.out;
		System.setOut(System.err);

		try {
			RemoteAdminServer res = new RemoteAdminServer(args);
			res.process();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	private void process() {
		String kshCmd = null;

		try {
			System.err.println("Creating socket on top of stdin/stdout");

			// Build a socket out of standard in and out.
			SocketPair sp = new SocketPair();
			System.err.println("Got new socket pair");
			Socket s1 = sp.getServerAllocatedSocket();
			System.err.println("Got inetd end of socket pair");
			Socket sock = sp.getConnectingSocket();
			System.err.println("Got server end of socket pair");
			ShuttleService shuttleIn = new ShuttleService(System.in,s1);
			shuttleIn.start();
			System.err.println("Started shuttle service for stdin");
			ShuttleService shuttleOut = new ShuttleService(s1, stdout);
			shuttleOut.start();
			System.err.println("Started shuttle service for stdout");
	
			// If this socket is to remain after we end, we need to decouple it
			// from the SocketPair object.
			// sp.decouple();
	
			// Use strong-authentication SSL on the socket?
			if (ksi != null && tsi != null) {
				System.err.println("Doing SSL on socket");

				// Get the trust managers.
				String alg = TrustManagerFactory.getDefaultAlgorithm();
				TrustManagerFactory tmc = TrustManagerFactory.getInstance(alg);
				KeyStore ts = KeyStore.getInstance("jks");
				ts.load(tsi.getKeystoreStream(),tsi.getKeystorePassword().toCharArray());
				tmc.init(ts);
				TrustManager[] tsm = tmc.getTrustManagers();

				// Get the key managers.
				alg = KeyManagerFactory.getDefaultAlgorithm();
				KeyManagerFactory kmc = KeyManagerFactory.getInstance(alg);
				KeyStore ks = KeyStore.getInstance("jks");
				ks.load(ksi.getKeystoreStream(),ksi.getKeystorePassword().toCharArray());
				kmc.init(ks,ksi.getKeystorePassword().toCharArray());
				KeyManager[] ksm = kmc.getKeyManagers();

				// Get the SSL Context.
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(ksm,tsm,null);
				
				// Attach an SSL socket to the base socket and do the handshake.
				SSLSocketFactory sf = sc.getSocketFactory();
				//SSLSocket s = (SSLSocket) sf.createSocket(sock,sock.getLocalAddress().getHostName(),sock.getLocalPort(),true);
				SSLSocket s = (SSLSocket) sf.createSocket(sock,"Unknown",9999,true);
				s.setNeedClientAuth(true);
				s.setUseClientMode(false);
				s.startHandshake();
				
				sock = s;
				System.err.println("Successful SSL handshake.");
			}
			
			// Read the request from the socket
			ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
			String request = in.readUTF();
			System.err.println("Request: " + request);
	
			// Read the request specific parameters.
			if (request.equals("createApplication")) {
				kshCmd = buildCreateAppCmd(in);
			}
			else if (request.equals("deleteApplication")) {
				kshCmd = buildDeleteAppCmd(in);
			}
			else if (request.equals("createProject")) {
				kshCmd = buildCreateProjCmd(in);
			}
			else if (request.equals("deleteProject")) {
				kshCmd = buildDeleteProjCmd(in);
			}
			else if (request.equals("createUser")) {
				kshCmd = buildCreateUserCmd(in);
			}
			else if (request.equals("deleteUser")) {
				kshCmd = buildDeleteUserCmd(in);
			}
			else if (request.equals("grantProject")) {
				kshCmd = buildGrantProjCmd(in);
			}
			else if (request.equals("revokeProject")) {
				kshCmd = buildRevokeProjCmd(in);
			}
			// Unknown request, kill it all.
			else {
				System.err.println("Unsupported request from client: " + request + ", exiting.");
				sock.close();
				return;
			}

			System.err.println("Running command.");

			// Ok, run the command and read in the result.
			String result = runCmd(kshCmd + " 2>&1");
			System.err.println("Result: " + result);
			
			// Ok, return the result.
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
			out.writeUTF(result);
			out.flush();

			System.err.println("Wrote results, closing socket");

			out.close();
		}
		catch (IOException e) {
			System.err.println("General I/O error on socket while processing");
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
		catch (Exception e) {
			System.err.println("Error while establishing SSL connection");
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}
	private String buildCreateAppCmd(ObjectInputStream in) throws IOException {
		String appName = in.readUTF();
		System.err.println("appName: " + appName);
		String appPath = in.readUTF();
		System.err.println("appPath: " + appPath);
		int numOfUsers = in.readInt();
		System.err.println("numOfUsers: " + numOfUsers);
		String userIDPrefix = in.readUTF();
		System.err.println("userIDPrefix: " + userIDPrefix);
		String grid = in.readUTF();
		System.err.println("grid: " + grid);

		StringBuffer cmd = new StringBuffer();
		cmd.append(gridToolsDir);
		cmd.append(File.separatorChar);
		cmd.append("AppSetup.sh \"");
		cmd.append(appName);
		cmd.append("\" \"");
		cmd.append(grid);
		cmd.append("\" \"");
		cmd.append(userIDPrefix);
		cmd.append("\" \"");
		cmd.append(numOfUsers);
		cmd.append("\" \"");
		cmd.append(appPath);
		cmd.append('"');
		System.err.println("cmd: " + cmd);
		
		return cmd.toString();
	}
	private String buildDeleteAppCmd(ObjectInputStream in) throws IOException {
		String appName = in.readUTF();
		System.err.println("appName: " + appName);

		StringBuffer cmd = new StringBuffer();
		cmd.append(gridToolsDir);
		cmd.append(File.separatorChar);
		cmd.append("AppDel.sh \"");
		cmd.append(appName);
		cmd.append('"');
		System.err.println("cmd: " + cmd);
		
		return cmd.toString();
	}
	private String buildCreateProjCmd(ObjectInputStream in) throws IOException {
		String project = in.readUTF();
		System.err.println("project: " + project);
		String grid = in.readUTF();
		System.err.println("grid: " + grid);

		StringBuffer cmd = new StringBuffer();
		cmd.append(gridToolsDir);
		cmd.append(File.separatorChar);
		cmd.append("GridAdmin.sh -cmd createproj -path \"");
		cmd.append(grid);
		cmd.append("\" -proj \"");
		cmd.append(project);
		cmd.append('"');
		System.err.println("cmd: " + cmd);
		
		return cmd.toString();
	}
	private String buildDeleteProjCmd(ObjectInputStream in) throws IOException {
		String project = in.readUTF();
		System.err.println("project: " + project);
		String grid = in.readUTF();
		System.err.println("grid: " + grid);

		StringBuffer cmd = new StringBuffer();
		cmd.append(gridToolsDir);
		cmd.append(File.separatorChar);
		cmd.append("GridAdmin.sh -cmd delproj -path \"");
		cmd.append(grid);
		cmd.append("\" -proj \"");
		cmd.append(project);
		cmd.append('"');
		System.err.println("cmd: " + cmd);
		
		return cmd.toString();
	}
	private String buildCreateUserCmd(ObjectInputStream in) throws IOException {
		String user = in.readUTF();
		System.err.println("user: " + user);
		String grid = in.readUTF();
		System.err.println("grid: " + grid);

		StringBuffer cmd = new StringBuffer();
		cmd.append(gridToolsDir);
		cmd.append(File.separatorChar);
		cmd.append("GridAdmin.sh -cmd createuser -path \"");
		cmd.append(grid);
		cmd.append("\" -user \"");
		cmd.append(user);
		cmd.append('"');
		System.err.println("cmd: " + cmd);
		
		return cmd.toString();
	}
	private String buildDeleteUserCmd(ObjectInputStream in) throws IOException {
		String user = in.readUTF();
		System.err.println("user: " + user);
		String grid = in.readUTF();
		System.err.println("grid: " + grid);

		StringBuffer cmd = new StringBuffer();
		cmd.append(gridToolsDir);
		cmd.append(File.separatorChar);
		cmd.append("GridAdmin.sh -cmd deluser -path \"");
		cmd.append(grid);
		cmd.append("\" -user \"");
		cmd.append(user);
		cmd.append('"');
		System.err.println("cmd: " + cmd);
		
		return cmd.toString();
	}
	private String buildGrantProjCmd(ObjectInputStream in) throws IOException {
		String project = in.readUTF();
		System.err.println("project: " + project);
		String user = in.readUTF();
		System.err.println("user: " + user);
		String grid = in.readUTF();
		System.err.println("grid: " + grid);

		StringBuffer cmd = new StringBuffer();
		cmd.append(gridToolsDir);
		cmd.append(File.separatorChar);
		cmd.append("GridAdmin.sh -cmd addprojtouser -path \"");
		cmd.append(grid);
		cmd.append("\" -user \"");
		cmd.append(user);
		cmd.append("\" -proj \"");
		cmd.append(project);
		cmd.append('"');
		System.err.println("cmd: " + cmd);
		
		return cmd.toString();
	}
	private String buildRevokeProjCmd(ObjectInputStream in) throws IOException {
		String project = in.readUTF();
		System.err.println("project: " + project);
		String user = in.readUTF();
		System.err.println("user: " + user);
		String grid = in.readUTF();
		System.err.println("grid: " + grid);

		StringBuffer cmd = new StringBuffer();
		cmd.append(gridToolsDir);
		cmd.append(File.separatorChar);
		cmd.append("GridAdmin.sh -cmd delprojtouser -path \"");
		cmd.append(grid);
		cmd.append("\" -user \"");
		cmd.append(user);
		cmd.append("\" -proj \"");
		cmd.append(project);
		cmd.append('"');
		System.err.println("cmd: " + cmd);
		
		return cmd.toString();
	}
	private String runCmd(String cmd) throws IOException {
		String[] args = new String[3];
		args[0] = "/bin/ksh";
		args[1] = "-c";
		args[2] = cmd;
		Process p = Runtime.getRuntime().exec(args);
		p.getOutputStream().close();
		System.err.println("Command running, stdin of command is closed.");

		BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringBuffer cmdOutput = new StringBuffer();
		String line = null;
		while ((line = output.readLine()) != null) {
			System.err.println("Command running, read result line: " + line);
			cmdOutput.append(line);
			cmdOutput.append("\n");
		}
		output.close();
		System.err.println("Command done, results read.");
		
		if (cmdOutput.length() > 0) {
			return cmdOutput.toString();
		}
		else {
			return null;
		}
	}
}