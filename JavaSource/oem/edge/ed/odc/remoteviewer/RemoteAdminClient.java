package oem.edge.ed.odc.remoteviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import oem.edge.ed.odc.util.KeystoreInfo;

public class RemoteAdminClient {
	protected String serverName = null;
	protected int port = 9999;

	private KeystoreInfo ksi = null;
	private KeystoreInfo tsi = null;

	/**
	 * Create a RemoteAdminClient instance which connects to the specified host
	 * on the default port 9999.
	 * @param serverName name of remote host running the remote admin server
	 */
	public RemoteAdminClient(String serverName) {
		super();
		this.serverName = serverName;
	}
	/**
	 * Create a RemoteAdminClient instance which connects to the specified host
	 * on the specified port.
	 * @param serverName name of remote host running the remote admin server
	 * @param port port number of the remote admin server
	 */
	public RemoteAdminClient(String serverName,int port) {
		this(serverName);
		this.port = port;
	}
	/**
	 * Create a RemoteAdminClient instance which connects to the specified host
	 * on the specified port. The connection is strongly authenticated using the
	 * supplied key and trust stores.
	 * @param serverName name of remote host running the remote admin server
	 * @param port port number of the remote admin server
	 * @param keyStore store containing the client's key pair
	 * @param keyStorePW password required to open key store.
	 * @param trustStore store containing the trusted CA for the server's cert
	 * @param trustStorePW password required to open trust store.
	 */
	public RemoteAdminClient(String serverName,int port,String keyStore,String keyStorePW,String trustStore,String trustStorePW) {
		this(serverName,port);
		this.ksi = new KeystoreInfo(keyStore,keyStorePW);
		this.tsi = new KeystoreInfo(trustStore,trustStorePW);
	}

	/**
	 * Connects to the configured server at the configured port.
	 * @return Socket if connection successful, null otherwise.
	 */
	private Socket connect() {
		try {
			// Create a socket.
			Socket s = new Socket(serverName,port);

			// Use strong-authentication SSL on the socket?
			if (ksi != null && tsi != null) {
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
				SSLSocket s1 = (SSLSocket) sf.createSocket(s,serverName,port,true);
				s1.setUseClientMode(true);
				s1.startHandshake();
				
				s = s1;
			}

			return s;
		} catch(UnknownHostException e) {
			System.out.println("RemoteExecClient.connect: Unknown host " + serverName);
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch(IOException e) {
			System.out.println("RemoteExecClient.connect: IO exception connecting to" + serverName);
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch(Exception e) {
			System.out.println("RemoteExecClient.connect: SSL error connecting to" + serverName);
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return null;
	}
	/**
	 * Handles transmission and receipt of String arguments and a String result with the server
	 * @param parms Array of String arguments, first being the remote method name
	 * @return String null if successful, error message otherwise.
	 */
	private String invoke(String[] parms) {
		// Connect to the server...
		Socket s = connect();
		String result;

		// Got the socket? Send the parms, and read back the response.
		if (s != null) {
			try {
				ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
				for (int i = 0; i < parms.length; i++) {
					o.writeUTF(parms[i]);
				}
				o.flush();
	
				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
				result = in.readUTF();
				
				return result;
			} catch(IOException e) {
				System.out.println("RemoteExecClient." + parms[0] + ": IO Exception");
				e.printStackTrace();
				result = "IO Exception while communicating with server";
			} finally {
				try {
					s.close();
				} catch(IOException e) {
				}
			}
		}
		// No connection.
		else {
			result = "Unable to connect to server";
		}

		return result;
	}

	/**
	 * Creates an application on the designated server.
	 * 
	 * @param name Name of application to create
	 * @param appPath Path to application executable
	 * @param numOfUsers Number of users in the pool for this application
	 * @param userIDPrefix Prefix to be used for the pool of user IDs
	 * @param gridPath Path to the grid directories
	 * @return String null if successful, error message otherwise.
	 */
	public String createApplication(String name, String appPath, int numOfUsers, String userIDPrefix, String gridPath) {
		// Validate the arguments...
		if (name == null) {
			return "Application name is not specified";
		}
		if (appPath == null) {
			return "Application path is not specified";
		}
		if (userIDPrefix == null) {
			return "User ID prefix is not specified";
		}
		if (gridPath == null) {
			return "Grid path is not specified";
		}

		// Connect to the server...
		Socket s = connect();
		String result;

		// Got the socket? Send the parms, and read back the response.
		if (s != null) {
			try {
				ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
				o.writeUTF("createApplication");
				o.writeUTF(name);
				o.writeUTF(appPath);
				o.writeInt(numOfUsers);
				o.writeUTF(userIDPrefix);
				o.writeUTF(gridPath);
				o.flush();
	
				ObjectInputStream i = new ObjectInputStream(s.getInputStream());
				result = i.readUTF();
			} catch(IOException e) {
				System.out.println("RemoteExecClient.createApplication: IO Exception");
				e.printStackTrace();
				result = "IO Exception while communicating with server";
			} finally {
				try {
					s.close();
				} catch(IOException e) {
				}
			}
		}
		// No connection.
		else {
			result = "Unable to connect to server";
		}

		return result;
	}
	/**
	 * Deletes an application on the designated server.
	 * 
	 * @param name name of the application to delete.
	 * @param gridPath path to the grid directories.
	 * @return String null if successful, error message otherwise.
	 */
	public String deleteApplication(String name) {
		// Validate the arguments...
		if (name == null) {
			return "Application name is not specified";
		}

		// Make the call using the invoke routine.
		String[] parms = { "deleteApplication", name };
		return invoke(parms);
	}
	/**
	 * Creates a project on the designated server.
	 * 
	 * @param name name of the project to create.
	 * @param gridPath path to the grid directories.
	 * @return String null if successful, error message otherwise.
	 */
	public String createProject(String name, String gridPath) {
		// Validate the arguments...
		if (name == null) {
			return "Project name is not specified";
		}
		if (gridPath == null) {
			return "Grid path is not specified";
		}

		// Make the call using the invoke routine.
		String[] parms = { "createProject", name, gridPath };
		return invoke(parms);
	}
	/**
	 * Deletes a project on the designated server.
	 * 
	 * @param name name of the project to delete.
	 * @param gridPath path to the grid directories.
	 * @return String null if successful, error message otherwise.
	 */
	public String deleteProject(String name, String gridPath) {
		// Validate the arguments...
		if (name == null) {
			return "Project name is not specified";
		}
		if (gridPath == null) {
			return "Grid path is not specified";
		}

		// Make the call using the invoke routine.
		String[] parms = { "deleteProject", name, gridPath };
		return invoke(parms);
	}
	/**
	 * Creates a user on the designated server.
	 * 
	 * @param name name of the user to create.
	 * @param gridPath path to the grid directories.
	 * @return String null if successful, error message otherwise.
	 */
	public String createUser(String name, String gridPath) {
		// Validate the arguments...
		if (name == null) {
			return "User name is not specified";
		}
		if (gridPath == null) {
			return "Grid path is not specified";
		}

		// Make the call using the invoke routine.
		String[] parms = { "createUser", name, gridPath };
		return invoke(parms);
	}
	/**
	 * Deletes a user on the designated server.
	 * 
	 * @param name name of the user to delete.
	 * @param gridPath path to the grid directories.
	 * @return String null if successful, error message otherwise.
	 */
	public String deleteUser(String name, String gridPath) {
		// Validate the arguments...
		if (name == null) {
			return "User name is not specified";
		}
		if (gridPath == null) {
			return "Grid path is not specified";
		}

		// Make the call using the invoke routine.
		String[] parms = { "deleteUser", name, gridPath };
		return invoke(parms);
	}
	/**
	 * Grants a user access to a project on the designated server.
	 * 
	 * @param projName name of the project to be granted.
	 * @param userName name of the user to be granted.
	 * @param gridPath path to the grid directories.
	 * @return String null if successful, error message otherwise.
	 */
	public String grantProject(String projName, String userName, String gridPath) {
		// Validate the arguments...
		if (projName == null) {
			return "Project name is not specified";
		}
		if (userName == null) {
			return "User name is not specified";
		}
		if (gridPath == null) {
			return "Grid path is not specified";
		}

		// Make the call using the invoke routine.
		String[] parms = { "grantProject", projName, userName, gridPath };
		return invoke(parms);
	}
	/**
	 * Revokes a user's access to a project on the designated server.
	 * 
	 * @param projName name of the project to be revoked.
	 * @param userName name of the user to be revoked.
	 * @param gridPath path to the grid directories.
	 * @return String null if successful, error message otherwise.
	 */
	public String revokeProject(String projName, String userName, String gridPath) {
		// Validate the arguments...
		if (projName == null) {
			return "Project name is not specified";
		}
		if (userName == null) {
			return "User name is not specified";
		}
		if (gridPath == null) {
			return "Grid path is not specified";
		}

		// Make the call using the invoke routine.
		String[] parms = { "revokeProject", projName, userName, gridPath };
		return invoke(parms);
	}
}
