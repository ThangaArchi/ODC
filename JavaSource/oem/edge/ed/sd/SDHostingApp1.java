package oem.edge.ed.sd;

import java.util.*;
import oem.edge.ed.odc.applet.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import com.ibm.as400.webaccess.common.*;
/**
 * Insert the type's description here.
 * Creation date: (10/4/2001 3:48:50 PM)
 * @author: Mike Zarnick
 */
public class SDHostingApp1 extends Frame implements Runnable, LocationInfo {

	class MsgRouter extends Thread {
		MultiPipeOutputStream out = null;
		MultiPipeInputStream in = null;
		MsgRouter(MultiPipeOutputStream out) {
			this.out = out;
		}
		MsgRouter(MultiPipeInputStream in) {
			this.in = in;
		}
		public void run() {
			try {
				if (in == null)
					in = new MultiPipeInputStream(out);

				BufferedReader rdr = new BufferedReader(new InputStreamReader(in));

				String line;

				while ((line = rdr.readLine()) != null) {
					int i = line.length() + 1;
					String text = getConsoleTA().getText();
					int j = text.length();
					int k = 0;

					if (i + j > 16384) // Account for ...\n
						k = text.indexOf('\n',4096);

					if (k > 0)
						getConsoleTA().replaceRange("...\n",0,k);

					getConsoleTA().append(line);
					getConsoleTA().append("\n");
				}
				rdr.close();
			}
			catch (Exception e) {
				getConsoleTA().append("Error in MsgRouter: " + e.getMessage());
			}
		}
	}
	static private String TOKEN_OPT = "-SD_TOKEN";
	//private ConfigFile cfg = new ConfigFile();
	private File textFile = null;
	private Panel ivjContentsPane = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private ProgressBar ivjProgressBar1 = null;
	private Button ivjCommonLocBtn = null;
	private TextField ivjCommonLocTF = null;
	private oem.edge.ed.sd.Table ivjTable = null;
	private TableBorder ivjTablePnl = null;
	private ScrollPane ivjTableSP = null;
	private SharedTableModel ivjTableData = null;
	private FileDialog ivjFileDialog = null;
	private MenuItem ivjExitMI = null;
	private Menu ivjFileM = null;
	private MenuItem ivjMenuSeparator1 = null;
	private MenuItem ivjSocksMI = null;
	private MenuBar ivjTableTest2MenuBar = null;
	public int runMode = 0;
	public java.lang.Thread downloadfileThread = null;
	public java.lang.Thread showOrdersThread;
	private Panel ivjContentsPane1 = null;
	private Dialog ivjErrorDlg = null;
	private Label ivjErrorLbl = null;
	private Button ivjErrorOkBtn = null;
	public HttpsConnect Connection = null;
	public static boolean debug = false;
	private DownloadFile ivjdownloadfile = null;
	private Button ivjDownload = null;
	private Label ivjDownloadLabel = null;
	private HttpsConnect ivjConnection = null;
	private Panel ivjContentsPane2 = null;
	private Panel ivjContentsPane3 = null;
	private Label ivjLabel1 = null;
	private Dialog ivjProcessDlg = null;
	private Frame ivjConsole = null;
	private Button ivjConsoleOkBtn = null;
	private TextArea ivjConsoleTA = null;
	private MenuItem ivjMenuSeparator2 = null;
	private MenuItem ivjShowConsole = null;
	private Menu ivjEditM = null;
	private Menu ivjViewM = null;
	private Dialog ivjAboutDlg = null;
	private MenuItem ivjAboutM = null;
	private AboutPnl ivjAboutPnl = null;
	private MenuItem ivjAllOrders = null;
	private Panel ivjContentsPane22 = null;
	private Checkbox ivjfolderOption = null;
	private Menu ivjHelpM = null;
	private Label ivjLabel2 = null;
	private MenuItem ivjNewOrders = null;
	private MenuItem ivjSelectAllM = null;
	private MenuItem ivjUnselectAll = null;
	private MenuItem ivjClearMI = null;
	private MenuItem ivjCloseMI = null;
	private MenuBar ivjConsoleMenuBar = null;
	private Menu ivjFileM1 = null;
	private MenuItem ivjMenuSeparator11 = null;
	private MenuItem ivjMenuSeparator21 = null;
	private MenuItem ivjSaveAsMI = null;
	private MenuItem ivjSaveMI = null;
	private Panel ivjContentsPane4 = null;
	private Dialog ivjDownloadDlg = null;
	private Button ivjStopBtn = null;
	private boolean createFolder = true;
	private Button ivjCancelBtn = null;
	private Panel ivjContentsPane11 = null;
	private Button ivjErrorOkBtn1 = null;
	private Dialog ivjFolderDlg = null;
	private Label ivjFolderlbl = null;
	private Panel ivjContentsPane31 = null;
	private Button ivjLicCanBtn = null;
	private Dialog ivjLicDlg = null;
	private Label ivjLicLbl = null;
	private Button ivjLicOkBtn = null;
	private TextArea ivjLicTA = null;
	private Label ivjLicTitleLbl = null;
	private Panel ivjPanel = null;
	public boolean slaAccept = false;
	private Panel ivjContentsPane12 = null;
	private Label ivjErrorLbl1 = null;
	private Button ivjErrorOkBtn2 = null;
	public java.lang.String licenceTxt = "IBM Technology Preview Kit License Agreement\n" +
	"\nImportant - Please read the following agreement carefully." +
	"\n\nIf you agree to all of its terms, click on the \"Submit\" button at the bottom of the page to proceed with the download. " +
	"BY CLICKING THE \"Submit\" BUTTON YOU ARE CONSENTING TO BE BOUND BY AND ARE BECOMING A PARTY TO THIS AGREEMENT. IF YOU DO NOT AGREE TO ALL OF THE TERMS OF THIS AGREEMENT, THE \"Cancel\" BUTTON MUST BE SELECTED. THIS IS A LEGALLY BINDING AGREEMENT (\"AGREEMENT\") BETWEEN YOU (\"YOU\") AND INTERNATIONAL BUSINESS MACHINES CORPORATION, ARMONK, NY (\"IBM\") AND SUPERSEDES ANY PURCHASE ORDER OR OTHER ORAL OR WRITTEN COMMUNICATION FROM YOU.\n"+
	"\nIBM's offer to You of the licensed use of the computer programs, data, documentation, and other materials in the Technology Preview Kit is conditioned upon your acceptance of this Agreement. Downloading the Technology Preview Kit indicates your acceptance of the following terms and conditions of this Agreement. If you do not accept the terms and conditions of this Agreement, you should click the \"Cancel\" button below.\n" + 
	"\nLICENSE" +
	"\n\nThis is a license agreement and not an agreement for sale. IBM retains all right and title to all copies of any Technology Preview Kit provided to You and any copy that You make. Except as expressly set forth in this Agreement, IBM retains all rights to its intellectual property rights in the contents of the Technology Preview Kit, and no other license, immunity, or other right is granted by IBM to You, or to any third party, either directly or by implication, estoppel or otherwise under any patents, copy rights, trade secrets, and other intellectual property rights. No license, immunity, or other right is granted with respect to any combination or use of the Technology Preview Kit with any other product. No license is granted with respect to any IBM tradename or trademark. " +
	"\n\nIBM hereby grants to You a non-exclusive, non-transferrable, revocable, personal, limited license to use, perform, display, and make limited copies of the Technology Preview Kit, solely and exclusively in accordance with this Agreement. " +
	"\n\nUnder this license You may: "+
	"\n\nuse the Technology Preview Kit only for purposes of conducting a technology evaluation or designing semiconductor devices to be manufactured solely by IBM (collectively \"Licensed Use\"); and "+
	"copy or translate the Technology Preview Kit's machine-readable portion into any machine-readable or printed form to provide sufficient copies only to support Your Licensed Use, as well as reasonable storage and backup of the Technology Preview Kit, and copy the printed related materials to support your Licensed Use. "+
	"You may not: "+
	"\nreverse assemble or reverse compile any portion of any machine readable representation of the Technology Preview Kit elements; "+
	"\nmake the Technology Preview Kit available to a network accessible by persons outside of Your organization, or otherwise distribute the Technology Preview Kit to any other persons, including other licensees; "+
	"\nuse, copy, or modify, the Technology Preview Kit, except as expressly provided in this Agreement; or "+
	"\nsublicense, transfer, rent or lease the Technology Preview Kit. "+
	"\n\nYou are expressly prohibited from using this Technology Preview Kit or any portions thereof to design any semiconductor device except for semiconductor devices to be manufactured solely by IBM. This license does not constitute an agreement by IBM to manufacture semiconductor devices for You. "+
	"\n\nDISCLAIMER OF WARRANTIES "+
	"\n\nFor a period of thirty (30) days from the date the Technology Preview Kit was shipped by IBM, IBM warrants that the media in this package upon which any software is recorded will be free from defects which prevent You from loading the software on Your computer. In the event that such media is found by IBM to be defective, and provided that You have notified IBM in writing within such thirty (30) day period, IBM will replace such media without charge to You. "+
	"\n\nIBM does not warrant that the functions contained in any Technology Preview Kit will meet Your specific requirements. "+
	"\n\nTHE FOREGOING WARRANTIES ARE IN LIEU OF ALL OTHER WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR USE, AND NON-INFRINGEMENT. "+
	"\n\nLIMITATION OF LIABILITIES "+
	"\n\nYou agree that in no event shall IBM be liable for any damage or loss, whether direct, indirect, special, or consequential (including lost profits and lost savings) for any cause whatsoever arising or alleged to arise out of, or in any way related to, the Technology Preview Kit, regardless of the form of action, even if IBM or an authorized representative has been advised of the possibility of such damages. You agree that you will not hold IBM liable in any way for any claim by another party. If the foregoing limitation is finally held by a court of competent jurisdiction to be unenforceable, then the maximum aggregate liability of IBM shall not exceed the fair market value of any media per se provided hereunder. "+
	"\n\nMISCELLANEOUS "+
	"\nAny attempt to assign, sublicense, rent or lease, or to transfer any of the rights, duties or obligations hereunder is void. "+
	"\nAny use, performance, display, copying, or translation other than in accordance with this Agreement , or the occurrence of any acts or omissions by You which are prohibited hereunder, shall cause the license to terminate immediately without notice. "+
	"\nYou hereby provide written assurance that You will comply with all United States and foreign laws and regulations relating to the export or re-export of technical data or software as they pertain to the Technology Preview Kit. "+
	"\nThe provisions under the sections entitled \"DISCLAIMER OF WARRANTIES\" \"LIMITATION OF LIABILITIES\" and \"MISCELLANEOUS\" shall survive any termination or expiration of this Agreement. "+
	"\nThis Agreement will be construed under the local laws of the State of New York, without regard to its conflict of laws. The parties hereby expressly waive any right they may have to a jury trial, and hereby consent to venue in courts located in the State of New York.. "+
	"\nThis agreement is the complete and exclusive statement of the parties agreement. This statement of the Agreement supersedes all proposals or other prior agreements, oral or written, and all other communications between the You and IBM relating to the Technology Preview Kit licensed under this Agreement. This Agreement may only be amended in a writing signed by You and IBM. ";
	private Dialog ivjNoConnect = null;
	private ConfigMgr ivjConfigMgr = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.WindowListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SDHostingApp1.this.getExitMI()) 
				connEtoC5(e);
			if (e.getSource() == SDHostingApp1.this.getErrorOkBtn()) 
				connEtoM5(e);
			if (e.getSource() == SDHostingApp1.this.getDownload()) 
				connEtoC17();
			if (e.getSource() == SDHostingApp1.this.getConsoleOkBtn()) 
				connEtoM8(e);
			if (e.getSource() == SDHostingApp1.this.getShowConsole()) 
				connEtoC21(e);
			if (e.getSource() == SDHostingApp1.this.getSelectAllM()) 
				connEtoM10(e);
			if (e.getSource() == SDHostingApp1.this.getUnselectAll()) 
				connEtoM11(e);
			if (e.getSource() == SDHostingApp1.this.getAllOrders()) 
				connEtoC22(e);
			if (e.getSource() == SDHostingApp1.this.getNewOrders()) 
				connEtoC23(e);
			if (e.getSource() == SDHostingApp1.this.getAboutM()) 
				connEtoC18(e);
			if (e.getSource() == SDHostingApp1.this.getSaveMI()) 
				connEtoC2(e);
			if (e.getSource() == SDHostingApp1.this.getSaveAsMI()) 
				connEtoC20(e);
			if (e.getSource() == SDHostingApp1.this.getCloseMI()) 
				connEtoM7(e);
			if (e.getSource() == SDHostingApp1.this.getClearMI()) 
				connEtoC24(e);
			if (e.getSource() == SDHostingApp1.this.getStopBtn()) 
				connEtoC25(e);
			if (e.getSource() == SDHostingApp1.this.getCommonLocBtn()) 
				connEtoC26(e);
			if (e.getSource() == SDHostingApp1.this.getErrorOkBtn1()) 
				connEtoM9(e);
			if (e.getSource() == SDHostingApp1.this.getErrorOkBtn1()) 
				connEtoM12(e);
			if (e.getSource() == SDHostingApp1.this.getCancelBtn()) 
				connEtoM13(e);
			if (e.getSource() == SDHostingApp1.this.getLicOkBtn()) 
				connEtoM16(e);
			if (e.getSource() == SDHostingApp1.this.getLicCanBtn()) 
				connEtoM17(e);
			if (e.getSource() == SDHostingApp1.this.getLicOkBtn()) 
				connEtoM18(e);
			if (e.getSource() == SDHostingApp1.this.getErrorOkBtn2()) 
				connEtoM19(e);
			if (e.getSource() == SDHostingApp1.this.getSocksMI()) 
				connEtoM2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SDHostingApp1.this.getdownloadfile() && (evt.getPropertyName().equals("status"))) 
				connPtoP6SetTarget();
			if (evt.getSource() == SDHostingApp1.this.getdownloadfile() && (evt.getPropertyName().equals("percentDownload"))) 
				connPtoP7SetTarget();
		};
		public void windowActivated(java.awt.event.WindowEvent e) {};
		public void windowClosed(java.awt.event.WindowEvent e) {};
		public void windowClosing(java.awt.event.WindowEvent e) {
			if (e.getSource() == SDHostingApp1.this) 
				connEtoC1(e);
			if (e.getSource() == SDHostingApp1.this.getErrorDlg()) 
				connEtoM6(e);
			if (e.getSource() == SDHostingApp1.this.getAboutDlg()) 
				connEtoM1(e);
			if (e.getSource() == SDHostingApp1.this.getFolderDlg()) 
				connEtoM14(e);
		};
		public void windowDeactivated(java.awt.event.WindowEvent e) {};
		public void windowDeiconified(java.awt.event.WindowEvent e) {};
		public void windowIconified(java.awt.event.WindowEvent e) {};
		public void windowOpened(java.awt.event.WindowEvent e) {
			if (e.getSource() == SDHostingApp1.this) 
				connEtoC3(e);
		};
	};
	public static boolean isFoundry = false;
/**
 * SDHostingApp1 constructor comment.
 */
public SDHostingApp1() {
	super();
	initialize();
}
/**
 * SDHostingApp1 constructor comment.
 * @param title java.lang.String
 */
public SDHostingApp1(String title) {
	super(title);
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public void begin(String[] args) {
	displayAbout();
	getAboutPnl().setCompName("Software Download");	
	// Insert code to start the application here.
	String url = null;
	String token = null;

// Route stdout and stderr to the message textarea.
	MultiPipeOutputStream stdout = new MultiPipeOutputStream();
	PrintStream stdoutStream = new PrintStream(stdout);
	System.setOut(stdoutStream);
	System.setErr(stdoutStream);

	try {
		MultiPipeInputStream stdin = new MultiPipeInputStream(stdout);
		Thread outMsgRouter = new MsgRouter(stdin);
		outMsgRouter.start();
	}
	catch (Exception e) {
		Thread outMsgRouter = new MsgRouter(stdout);
		outMsgRouter.start();
	}

	
	try {
		// System.out.println("Reading program parameters:");

		// Process the parameters...
		BufferedReader rdr = null;

		// We are being invoked as a helper application. Open the data file
		// and read in the first line (which was destined to the launch code).
		if (args.length > 0) {
			FileReader file = new FileReader(args[0]);
			rdr = new BufferedReader(file);
			rdr.readLine();
		}

		// We are being launched by the signed applet. Attach standard input to
		// the reader.
		else {
			InputStreamReader data = new InputStreamReader(System.in);
			rdr = new BufferedReader(data);
		}

		String line = rdr.readLine();
		while (line != null) {
			line = line.trim();
			// System.out.println(line);
			if (line.startsWith("-URL")) {
				String arg = null;
				if (line.length() > 4) arg = line.substring(4).trim();
				if (arg == null || arg.length() == 0) {
					syntax("Parameter -URL requires a valid URL value.");
					return;
				}

				url = arg;
			} else if (line.startsWith(TOKEN_OPT)) {
				String arg = null;
				if (line.length() > TOKEN_OPT.length()) arg = line.substring(TOKEN_OPT.length()).trim();
				if (arg == null || arg.length() == 0) {
					syntax("Parameter " + TOKEN_OPT + " requires a value.");
					return;
				}

				token = arg;
			} else if (line.startsWith("-THE_END")) {
				// We ignore this...
			} else if (line.length() != 0) {
				syntax("Bad parm = '" + line + "'\n");
				return;
			}

			if (line.startsWith("-THE_END"))
				line = null;
			else
				line = rdr.readLine();
		}
	}
	catch (Exception e) {
		 System.out.println("Warning: error occurred while reading parameter file!");
		 System.out.println(e.getMessage());
	}
	
	// We need the ini file. Can't leave home without it...

	// Open the ini file to extract anything else we need:
	try {
		setConfigMgr(new ConfigMgr("edesign.ini",this));
	}
	catch (Exception e) {
		syntax("Unable to read file edesign.ini!");
		return;
	}

	// If the ODC server url was not provided, get it from the ini file.
	if (url == null) {
		syntax("Client Software for Design Solutions server -URL URL not specified!");
		return;
	}

	// Show the frame centered on the screen.
	Dimension screen = Toolkit.getDefaultToolkit ().getScreenSize();
	Rectangle win = getBounds();
	setLocation((screen.width - win.width) / 2, (screen.height - win.height) / 2);
	
	//showConsole_ActionEvents();
	// Ok, what's next?
	System.out.println("This is Foundry and SWD code");
	System.out.println("finished Initial settings");
	System.out.println("Token: " + token);
	System.out.println("URL : " + url);	
	setUrl(url);
	setToken(token);
	setVisible(true);
}
/**
 * Comment
 */
public void clearText() {
	getConsoleTA().setText("");
	return;
}
/**
 * connEtoC1:  (SDHostingApp1.window.windowClosing(java.awt.event.WindowEvent) --> SDHostingApp1.dispose()V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doExit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC17:  (Button3.action. --> SDHostingApp1.download_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17() {
	try {
		// user code begin {1}
		// user code end
		this.StartDownload();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC18:  (AboutM.action.actionPerformed(java.awt.event.ActionEvent) --> SDHostingApp1.displayAboutDlg()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.displayAboutDlg();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC19:  (SDHostingApp1.initialize() --> SDHostingApp1.sDHostingApp1_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19() {
	try {
		// user code begin {1}
		// user code end
		this.sDHostingApp1_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (SaveMI.action.actionPerformed(java.awt.event.ActionEvent) --> SDHostingApp1.saveText(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.saveText(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC20:  (SaveAsMI.action.actionPerformed(java.awt.event.ActionEvent) --> SDHostingApp1.saveText(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.saveText(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC21:  (ShowConsole.action.actionPerformed(java.awt.event.ActionEvent) --> SDHostingApp1.showConsole_ActionEvents()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC21(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showConsole_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC22:  (AllOrders.action.actionPerformed(java.awt.event.ActionEvent) --> SDHostingApp1.ShowAll(Z)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC22(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.ShowAll(true);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC23:  (NewOrders.action.actionPerformed(java.awt.event.ActionEvent) --> SDHostingApp1.ShowAll(Z)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC23(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.ShowAll(false);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC24:  (ClearMI.action.actionPerformed(java.awt.event.ActionEvent) --> SDHostingApp1.clearText()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC24(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.clearText();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC25:  (StopBtn.action.actionPerformed(java.awt.event.ActionEvent) --> SDHostingApp1.stopDownload()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC25(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.stopDownload();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC26:  (CommonLocBtn.action.actionPerformed(java.awt.event.ActionEvent) --> SDHostingApp1.setLocation(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC26(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setLocation(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (SDHostingApp1.window.windowOpened(java.awt.event.WindowEvent) --> SDHostingApp1.populateTable()V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.populateTable();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (ExitMI.action.actionPerformed(java.awt.event.ActionEvent) --> SDHostingApp1.doExit()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.doExit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (SelectAllBtn.action.actionPerformed(java.awt.event.ActionEvent) --> Table.selectAll(Z)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTable().selectAll(true);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (AboutDlg.window.windowClosing(java.awt.event.WindowEvent) --> AboutDlg.dispose()V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getAboutDlg().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM10:  (SelectAllM.action.actionPerformed(java.awt.event.ActionEvent) --> Table.selectAll(Z)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTable().selectAll(true);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM11:  (UnselectAll.action.actionPerformed(java.awt.event.ActionEvent) --> Table.selectAll(Z)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTable().selectAll(false);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM12:  (ErrorOkBtn1.action.actionPerformed(java.awt.event.ActionEvent) --> FolderDlg.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getFolderDlg().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM13:  (CancelBtn.action.actionPerformed(java.awt.event.ActionEvent) --> FolderDlg.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getFolderDlg().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM14:  (FolderDlg.window.windowClosing(java.awt.event.WindowEvent) --> FolderDlg.dispose()V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM14(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getFolderDlg().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM16:  (LicOkBtn.action.actionPerformed(java.awt.event.ActionEvent) --> SDHostingApp1.setslaAccept(Z)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM16(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setslaAccept(true);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM17:  (LicCanBtn.action.actionPerformed(java.awt.event.ActionEvent) --> LicDlg.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM17(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getLicDlg().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM18:  (LicOkBtn.action.actionPerformed(java.awt.event.ActionEvent) --> LicDlg.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM18(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getLicDlg().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM19:  (ErrorOkBtn2.action.actionPerformed(java.awt.event.ActionEvent) --> NoConnect.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM19(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getNoConnect().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (SocksMI.action.actionPerformed(java.awt.event.ActionEvent) --> ConfigMgr.changeConnectivity()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getConfigMgr().changeConnectivity();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM5:  (ErrorOkBtn.action.actionPerformed(java.awt.event.ActionEvent) --> ErrorDlg.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getErrorDlg().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM6:  (ErrorDlg.window.windowClosing(java.awt.event.WindowEvent) --> ErrorDlg.dispose()V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getErrorDlg().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM7:  (CloseMI.action.actionPerformed(java.awt.event.ActionEvent) --> Console.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getConsole().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM8:  (ConsoleOkBtn.action.actionPerformed(java.awt.event.ActionEvent) --> Console.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getConsole().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM9:  (SocksMI.action.actionPerformed(java.awt.event.ActionEvent) --> Console.show()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setCreateFolder(true);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (TableData1.this <--> Table1.tableData)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getTable().setTableData(getTableData());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetTarget:  (SDHostingApp1.this <--> TableData.destination)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getTableData().setDestination(this);
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP6SetTarget:  (downloadfile.status <--> Label1.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetTarget() {
	/* Set the target from the source */
	try {
		getDownloadLabel().setText(getdownloadfile().getStatus());
		// user code begin {1}
		//System.out.println("Download label is " + getDownloadLabel().getText());
		//getDownloadLabel().repaint();
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP7SetTarget:  (downloadfile.percentDownload <--> ProgressBar1.completion)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetTarget() {
	/* Set the target from the source */
	try {
		getProgressBar1().setCompletion(getdownloadfile().getPercentDownload());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP8SetTarget:  (TableData.this <--> Table.tableData)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP8SetTarget() {
	/* Set the target from the source */
	try {
		getTable().setTableData(getTableData());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2001 4:10:00 PM)
 * @param param java.lang.String
 * @param message java.lang.String
 */
public static void Debug(String param, String message) {
	if(debug){
		System.out.println(param + " :: " + message);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (03/07/01 2:22:27 PM)
 */
public void displayAbout() {
	About about = new About(this);
	about.setCompName("Software Download");
	about.display();
	about.invisible(3000);
}
/**
 * Insert the method's description here.
 * Creation date: (03/07/01 2:22:27 PM)
 */
public void displayAboutDlg() {
	// Ensure the AboutPnl is 300x200.
	getAboutDlg().setSize(348,226);

	// Center the window
	Point winPos = getLocation();
	Dimension winSize = getSize();
	Dimension dlgSize = getAboutDlg().getSize();
	getAboutDlg().setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	getAboutDlg().setVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (03/07/01 2:22:27 PM)
 */
public void displayError() {
	// Center the window
	Point winPos = getLocation();
	Dimension winSize = getSize();
	Dimension dlgSize = getErrorDlg().getSize();
	getErrorDlg().setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	getErrorDlg().setVisible(true);
}
/**
 * Comment
 */
public void doExit() {
	// Do something graceful here...

	dispose();
	System.exit(0);

	return;
}
/**
 * Comment
 */
public void downloadAllChanged(ItemEvent e) {
	return;
}
/**
 * Return the AboutDlg property value.
 * @return java.awt.Dialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Dialog getAboutDlg() {
	if (ivjAboutDlg == null) {
		try {
			ivjAboutDlg = new java.awt.Dialog(this);
			ivjAboutDlg.setName("AboutDlg");
			ivjAboutDlg.setLayout(new java.awt.BorderLayout());
			ivjAboutDlg.setBackground(java.awt.SystemColor.window);
			ivjAboutDlg.setBounds(41, 1299, 375, 225);
			ivjAboutDlg.setModal(true);
			ivjAboutDlg.setTitle("CSDS Release Information");
			getAboutDlg().add(getContentsPane22(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAboutDlg;
}
/**
 * Return the AboutM property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getAboutM() {
	if (ivjAboutM == null) {
		try {
			ivjAboutM = new java.awt.MenuItem();
			ivjAboutM.setLabel("About");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAboutM;
}
/**
 * Return the AboutPnl property value.
 * @return oem.edge.ed.odc.applet.AboutPnl
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private oem.edge.ed.odc.applet.AboutPnl getAboutPnl() {
	if (ivjAboutPnl == null) {
		try {
			ivjAboutPnl = new oem.edge.ed.odc.applet.AboutPnl();
			ivjAboutPnl.setName("AboutPnl");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAboutPnl;
}
/**
 * Return the AllOrders property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getAllOrders() {
	if (ivjAllOrders == null) {
		try {
			ivjAllOrders = new java.awt.MenuItem();
			ivjAllOrders.setLabel("All Orders");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAllOrders;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GEEE951ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1543D8DDCD4D57AB8CE3BEE31C5C5AD95DBD6740D0AADAA5A285C564AB65B28580DB63625222222DDAAAA933635F237ABAFA3AAAA7996A556D83E0092022222112222022222202222B24C8C4C48B0B34E8C820A7AFF4EB977B9774E1DBBAF20767B7C7978F867BC67BC67F9B94FB9773CDFE1563791399332E6894204DCC17CDBBDC19034C7A68885BF3D68C41C10B4D6A3487E6683FCA114D5674033G62
	6AD6E6EA8A04790B72A03CG4235CBB2B5DF426F194277ED3376C006DE78246F1EA0E4DD5B1E661BCF519164B311067F5E168F4FE9G4500EBG8887A8905EEFAB9471F688CF93FF931810A58855AF11B4762AC299BED9CCD7GF18BC87C4BA4CD1E2EC80C9F863861G71GA9AF31346360B909CA772B35D5E23A562B2E91B21F294D1ACAF84972C7E8F919518E08CF2D90CF8D11AD949DDE8C4F6A462E4EF5B6E3F7195150E9047F4AEC86FD194EEE3798DDEB57B66B5AAD46E6235335DCC82772DDDB15C7E985
	A19642FD2F905C34B544A665703B8E206D951471792955228CE1DD578871551FE5FB13F164ABCC3614D7A499DB7A1F488ACDA679D76872C5BAAD04DBG187C6DBFFD27A63F5330F6EDD3ED1D4D69B2DBBBB7586D9502A8FB3F8EE5CFCCC6592F96C85E792FC299GD43F0A78CF5B893E99429600E12F22CEBFADC61DDACEC58A635ABB3326FB51E9782BCC17512FCABA45DC52187D95752328234497045700AFC095C0AB40G40F0003954FF75C71640F3C33BEB132D3B5BE685D5B6DA8C0E9DB60351823F572E05
	989137472837B98C0240FC74062AD295F990E2DFF377CF82CC377D827AD4741E9B84176BF0D62E0AEE31FF5F1295A766117C1A24DBDE8A315B3AEAE2377100A7F49781B07CA5945F10A261D9594E223EC342ADEF0C55985EC6BB37CCC3D99AF22E948AAA4CD993D5E4897F5311AC7BBBCC0660F7A4D9180DFF21A3B60E85FC82C09A00706ED80D962066DD34715DC7D67833F13D55E5F4D8F516261EF63B4E615A58E336980C8E51562BF53EEC2D860CEE73206918DD22D6484BA879FE132ADD1223F6E4353E4B6C
	61FAD7328B63FD46AB6F30F7BDFDE39AAF43E393C8D9C53D4B4AF09E47DFC47169B2BCAB5BF0DA37C8387C3D311A2274319A167E2F86024FFB1F255F4A73791462A3E4F8160F204F9743D59F0C5538460C553072556AC9DB99B3965AC800D40071GC599D01FG9AB2300C479DD996E0BDEA1415AF233D6F689CF89A7BDC8EDD3D556952D97546FDBA0719B40B7006E2E99EBECA6C5A6EC677982D5FFA603155E8B5BAF4AE334D8A396B3A0D60CCCE16A66E6897A40D43C8E31B4D5D504E6A3A6D82661BF0F4A6BC
	ADBA27EB2F5D20F3992594ACBE69A87109933AFD10A3E4G3F07E790FB7D1E365196789D1C01365AE1A03894880F63F6E9EA1B814F6D605CC64B5A3535E6BD91C36768C79F2D2DA60E151F896FD400A80002FF437B8EC078F82C2692408E108CD0731F311AC80062F1E3B551G651FC0BAG6153319A93C082C04BE7709E83B00D07B66F3FE3B5A1G09G797F03FCGC681E8BF077285C881E890B2B431G19G0689991AF8004A2C8CCDB8C0B2C0D5F6064685B08AA09320B58770G3539991AE800C2CD06E698
	C0A6C04BC40883B064E5E862GAAA7E5E8A281AAA74393A0FF8A64851084D0178F74G85D3B3B4FDG2C5EFD5002752EF66A75C2695FBBD56BDD4EBD1FE627818D79ADCC4BD0543B4B29BD0B81DF87E082888608820883C883481B1E21A9862883B0818483C481448124816495C0BCC09D0089A098A08AA08EA08DA06F8B08872883B0818483C48144812481644D00F800BAG93C0B0C094C09CC09AC05ECC08872883B0818483C48144812481644D02F800BAG93C0B0C094C09CC09AC05EEC08872883B0818483
	C48144812481644D49907BD1DF2F91F2A43B911F705576913F89240DA43E2206A73EC9FC5297AD71173E2992BD71A71FB2FD9E18CC84C89D1207C93DA1F5827D18782C1B3C13FD73D68229E74407C9DD236F06FC091E54B1F95A5ACCCF39C93D988CBF82249D705073FF2CEEFB148B6898AE33BFE98BC8DDA6F51A544BB8AF7CC9FBC05A9CD22FC91DA56D0BBCBED6ACEFD21739AE935C732A94EDC95A2C416A2886245DA2ED90E9D7C83B251626D264C55A134166CF5AAA52268D06E614CC8E170CE714222CC9BB
	AD8F13761152A87855497C4765474FC9BB1DAC6A9B1D6BDB6EE8317C8A44FA465A02F9EB65ED41D2BF3A1684E813CDDE70E4ECB84F8F6DD10175C9B13C5FCF7A1F86D1D6590AB06983AC845881308B6010A25EB9C87FA163569E006F4570E9D9DC05A2ED2E8A3DF9107C88CCD14111FE758FAA783DG5F4A42E48CDAAF307EDDA7C017979EEB845687BEG508D7015A2FE03783449F01B95E90E0B4F29C350D18DC83FF2BB009EE02517B45345E77F9072B782B48D12E6356CF72F6C77C4C53A590A30CEFC6ED660
	CF49FE7F680777FA0174AB596F43FE524E951FE745A76B277E51C87A7A4550CF2885B0G303E6A95EDDA017661B76A1CC66C356FF2C0075BE810FA2EAC6DE5BAE2F363892355D5EB349B2D867861947B4CD76B088F097D798E1B00FD6DF00AA7633BCD5061F6F27CCD3AAF989E72100F8EB07EF69A2FF3399C66769E17314968A2338E927DBDD496275B5051A96A7AC88751F5GF48C8308643A2EEEA7B644FEF9034D5655E32793B1E27F7E9ABA0FB18252A7707E14FD82691FE6C2B87F4B8C4D8200EA00C6G93
	C090C098C0B440A800A400B1GB905991A22C24C63921A87493392F04DGFDG4381C682A48388DFC15EGF5G9600D000D800242FB00F1B049431BC03C4DE4AF2B7097C55FCE20C8CCFE4A34FE8AFE989B4AA6208AEC4EFE28744AD70C16F8B72C51B2945B1DDDF623ACED729CF584EFC69A5FFF97B05F5E12617348414EDACF907911F153FCF14F5BCD0489558FB483B2E090690010DF7A6093290FDC99BCF5ABD725EA2F5FF0DA88FE933C8FBCC5ADF523613F79BE9A3C95BC25AF852AE11360D34B917892CED
	A46D8CF9B7E07BCE5E03248DA16F621B81AE8738D2E06F270B38DC021148B58E7CCF7BB578A5009DA09CE094C09AC041DC68E3833481848344GA48188C5501F84E884688308G08839883D0B8AFC3D383E0828885988110821077CD06269C2085A088A08AA081E09C0076DB607FAD56830D3EBC0B59CBDEBEE8BB250D55FCA7D040B218A65ADF5E9663BB915F037562937B9A3B45325AAB628FA86C2E934B4DA8169B768D3AD564681515A362AED2C9F7192CACFD69F573B958647F2F40FC679A6ABB44EF64FE11
	AB28576FC967400F78A2760564ED8FE98B30FD4236897B4C246FC65ABB523FA4ED5B8A91CFFC10789F69A3107EB06947133EA8692F137EAD7A203CFF02C07A0024AFA56F1FEED5C9C77A1344273D756D653E7F7F3AACFFEEE0FD07A5FA6FFD1F8A3DFA5FE735DE3D6F73135EF75FE633DE2DEF43629A7444DFED3DD6D9DFE9371E4CB76AEDD62B11F658B8094056F3BEE8A6FA182DE657FE33215368EAB21F0406A79C5A548261E9BA27E9075F433E45B9395E6FF1CEEE4149EB05D1C7B72A2ED7045F264BCA7E16
	3D4747FCAB4D49B1197650B9E72345588D065AE46B313A983E054E170AF8E7B6455DB513783236187645EAF5AEDDC5294BE4F6EA2BFF78E11D16A28101F1A595822B43B305F7F277D13A0DBDAE174DDAD12A235A1731C7235151E1F3F49B8D4547F4A7F4E53ADED799AD3D328DB4FAB379DD22659CCA1DA0135E3541E2A9BED8322FE48572D817FFC0120DAE5815761A2D86DBEF99FBEC32581CD0F8CAA67BE924128923B7181DF61B53D88CEC4A39AEA7D78CD0BEECAA3D41265F3431546CB2F617117F1ADCBA17
	F113C9E76D74D4279E62557968F9D6A2E503B7529FC11465CC0EA5138FD1B91A509C9BDD5673EBD846677E830B5D4BDCD964EEA54EE88E1FAAD316050208E6E4B01295CABA77DFD209764D7A76084FF25C85F55AD3A5DF45E83759FBACE4750020C8E12EC4DEAF6BDA188DED7AAE670E7AF3B21F68B2B61A9761320671C84C5BCFF9EC6EB33B4E938B129561D0D1AE6AB12A3C177228B5BB20844F5DA18DAC1FC03C31824B7C3363BDD406C6072D2F7F5CC53053EC8212E0B92F1BDFAD6B114A735CE5F052EC8212
	E18517A1ECE52BE407A623630451513C255485AF1DB2725FBE1D25471BA44D902FAE89215CE46A24053E0A71A9EDE915F43D90FCD6B3BEA5938ECA7AB45A9C2E734B258A6DB63E6420244F796733867994EFBE69214F39D7CAA73C2CE945D73A090C08B65E65A8477AA5A7BD747D996528C0B9F68F74CBF26C6ABA573748A045106A6EA8535DD40E7FC712149A7B74C6BBC16D526BFB9C8E23C12DB7E05175EFF6B8EC8E62D24BAB396C5BD7351E173E075B269D376E876393EDD9A36BC72DC0732851C24753E253
	990EB2B5293C2B393C31DF33FE9C266AB01357BF3411BD62BB3AC157EE34D4508AD4323C0A531DAD5DFBBE7AF1DB48AEGB77DD6C8367CF471C64AE307515AC35A588A9FDAF6E31A23B29E74653B023F637FD555F4BE5E711BECD6270DD849E46B258CD60B79DF3DFE8B4B9FA2B011F7091DD2AACF19D7214C2B3FE1FDA0680B5199DE67D025F6E7CEC4A45DBB60D5495B21EB7407D97F0478B52DF54B074C2DE103412837182DC6F9592E146A784676C397221E708E9176072EBFD4414A44ADDDGDDD0113E70FB
	5D7968033A33D76B06220F9EB8F89E7A085E2CD2414B7A3A0A030A329EAAAF1AC32D25D3DE504BF91D1B5839CF62C3D28D2914D5F970BAD275E5956B730AED180FCA873DBF078B53511AA6764D7F34F3070A1F107137D9E70131F7311AE11D725051FAD9E248D89A733EBB3F5CF79B0B825136961BDA31AF34E71DA72D5D685013C98D9147B201DECC274368F4EE54B9AA48B812ECDA253E4C5F6F1FCDA88EF87C36C29CDB2EDCF1C89C6352166A1CBC855BC477561E71096ABE6E2D6DBCB76777574C0A7CAF7E76
	207B98FA47F99DC1733E6DD1B33367DEABCFF1BE99D52E4176F562518B795EB848DE9C0CD741BE46EB273177C271DA09E34B1E59FA26D73B2D47B564B2726CDDC1F70EE6890D8CED10510E3BF76C13788D397D62547217A1EF0BA73776B15F531DB01E2FB65F89F91161103F4EEE25584E342FBE49E558F08E1D2321C9310A0F49DE6CE576A616BD87E328B4B52B383D434A0E08BC0CBA4779D2D5CFB2233A92DE3C4D3D3B13759F1ADCB67BB9B41D8A33C2E67217D825542F4A33DC64BEA7F97E189ADD109D6D9F
	79F5AA297F74FDE53B221FB1643102425EE42B30F10B0D4C1A172C64F3064FD5341EAFFEACEFC52D2DDC49787C564E5E971B48AE68F319F0D567427BB6DDD5ECDED2CAB62486C81D0BB82F7357252CCDEDB06B4F697D6E61AFCE0BEE031E0C25E99B3E9A5B5465567D488F4CF78E8CA18305CA2B711DF47162FD67C5A795963CEE7FEA29525706BCDE5AE95B4416025C07CB62DA0BD9FF1E462D9D664E9D1D0ED2BD1DBE93B91ACF18DD7D34512A9477620C69A46BC0855A8C4D8200EAAD6E45C9B410F85C0BA3EE
	B85AE433D85892964405EA497A54FF2BE789521AD3B3609A4473DDAC3E64A7F9FC12C77CDE37784279AC5EGCF967F22EAFC9F0F5F3F4EF77E3FDC67DB3EFB7D443F32CE0D3F94BFF13DDAFC8817AFD8B5BE0247CF2AD10B0F6571172A46477178A99B546213F9FC08EA7C989EBFF523DAFC4EF798FF05EAFCA10F1F3EC9AD3E1447072A46577078AFEA55621BF97C552A71A69E3FF433DA7CG0F0FD60D8F6571BFECD10B0F647177EC716D9FAFEF716DFF1A2D3E7DE779D65F74C7D469C7F079DE77137F14BA5F
	792F2ED3230F67790F28732DFF2AAAFD8A27B7562B45E77278FF567B16EF46B65F716B3629651F77BD667F18EAFC910F2F596EDBBF0B1F78F0BF7123377B16FF4CF6B5794A39FC479BFC535F68A77E6986357CEBF97E5BF62845377078E7D563ADBC3EE127EF7EFDBB556803B87DA72A71E1BC7E64AE5F795F3ECB0DBE0A53AFEED40B9F496323D46393F8FC49EEBF6FAFBF719779093F5BCF7CCBFE62F377780E2F7093FF58CF7C2F3D4633FE42634682F8BA0D47FB0C50B9A633945B0D7DF84E4FB8C391C79774
	78794184A35907E5116D73E578A7BA547726BC5DC14E24F7717C7F52C18E2E1F9073E4FBE9E2F4A4CF3B435C2DF374130D4B629974E81D7C8CFAAA14AFB92F1E33A0C3D38840E8631AA713BAAC9D7D967BFE5B1AA7A11E4C1789085FF700245FE28634D9E731F5A2FE17196C2141F9CC067BB5BDD7AD1F59E378573EA6F9EC62FB03B00F2F17CDE4F8B23495F1E77613340D8E1B5E68F4C24E183651C178613C8343D54EA7F64001A3C94C70F3EA08AEFC0840F599EEA636E4BDE0444DDACE52726EAACF3B116641
	3B185CD77EB1C16EAB658B180D49EFD68659D972B2B00871A1700CGE0E95AF364E9C608E91261B9E68156492DEE6F0C3C05929F966FF40B2FDA28146366FAB5DFB0C13A0105A8471E89EAF204C1FC94CF13592916E69444A772B46B5A545224C33C308853BCEBD4CBD3887125BCCDCD3BDA1ADA08EF61E95E6BD6CBE30778E1GE90B08ED063523ED06C3B8F611E41B610BC9FC13916FE1A8DF4C765957ADE6E906ADE6FB480A0BC93AB99D18AECD0C970A597E7982F8FE43737BA7CF67AAE67905943374F14599
	B23E3F6B44F429009F8FD0D4624EB73D04246BB6E13AD6883BG42CA58FE7D4492F93A8AB3262BDA42629B173827E3FCB3F93A90080F84983D0471CDDAA2CFF777B1CC17A94667AFE54F85CB65F6696C42F48DCB59998283BC7BG4217B23B30F42BAC18AEEAA9335BA8F8A6GE43A253B1C274BAFE579AD00E78D0021D41E2E2F9B538D14327C4260998990A726E3F62933E23A94400F83A87C011DF1F037CB3E8D5335C03C9DA0748726EF428FF2BBA770F463GDFD01621A9AFE3FC6D708C81E0696A97E33A88
	400DD024CB82E0FC4F96E33AF44065ACE369281D61F775B2CC97DB02699A81E7D2248B66695EDD02690683AED616AEF119DC0F1B167272DD464EFA641733273BDDF425182E8E625BG024A45FADFAECF3770871E9F6073ABBC53B13EAF71F4251026D6CC67D99E1717E1BA8324991065B73CC21EEE43B2CC978B78F800748A162EF439BCDDEEB9578370EDGC14BC579164B65FB1A279B87780295E03F95E23B824F6095D83E854B391DD7C869ACDFA96B65E81EAE9662633D266BDB417DC00C4F29D4CB77E3A526
	2B017896003ECAA65F88F8A6D622FCC1ABF9FB85B8E1A5CB976A11DF65AACC27DDA9656719AE0D272B02F40DG76156CCC10FB3A685518AE866263G52D632F3C3452BD83A06D5A4DD8A3FDFE699477D0762BE04176DF59A3C87E3C7017C7E9B0373206A5EDF7AAE94730FDCA56D7DE5F3BE8BE97FE4B46093D761FC4F56CE79592B8DCE27CDEF26733FE21F6C63CE793EE0067B2C1374C7BA4C8E272B5EEAB076E13FEE020948CA6EEF9051E25F6CECDF1960F99F45E60359E5F3605136CD3DD2DFE81F59580BFA
	1F5EA23FD3E76474DDD9D33C681DA66A1B37DA52BB7D1246ECA6C363D635FBCF14B25019B405A00C7E2E25BF85CA5F8C9DA703318E3A37CA1544FBF9C2FE42B22F83D999EE6ABA4485F15C3A75089B41F10FF15C980E2B29C1DCF195629E6738B60E5B3881F1619C778447A5F0DC6DC644652DC15C139CD743F1DBB6A12E0F631E6238980E2B2BC5DC9A47BD4DF15A3508EB5F0C38960EFB0B63C2B94E3085F1F19CF74DD64489BFA24EDA0738AA0E1BDB0FB8BB478D6738A80E6B590638940E7B18638A2B913738
	81F10D9CA76CC0DCB047DD44F123B8AEEBA76246F15C3D9CD77E93624AF7A14E44F12FF2DC84479DE9C4DC92473D4EF1856BF03C73071924AFC86F1142796435EB69AEADC6B3271D50107AD787B46CFE28BF1EA16318A088078344703CFE3BFA21606DCEAA134EE1B45061933DFB063787B1EB3ED261AF9BCE09756447ACDE4EDC0EFB33099C42FA683FG142FC7B91C9533BD73D464F7FD8E490F349FF56BB13F2726D23DA09C8E904373ABDCB95F1BDEB838A292F31DDA36F9CDDF67F2591D52D80A49720FFA14
	A50E4B727BE62ADB8D6886D0DE03E5F86984DE9EB51856FE1A4A8D61F000981E3654447365E9E79C2279EE00FC814AB7E05A93EDBC5F8D1876F1AB4D974261GB1BCEDF6874F17273D5ACC735D88798214EF4434FD6D5C4E9BB16D1ABE1AAF044381E2F85A6F2DBCDF1E36FC924DF793648BD08E406EC53A2B9D6FC52B83DC50A6AC2B9F8EADD2333D7CDEB75920153DD774AB703D92FA72EA216D9A0B57774A08CD6CFD1234C9FA2F8C5C7C20A853GBD3FCD64A82805B193G9BAB3FE5A47B01863B0E477431EC
	A6B2729DDD824A7CFFB3EBC419626F39D228BEDB2EFA97DB707435D9C135CC5628DAC966146D8FEAB237235C6B69DC8B11B5935214EEC639EF27F3ADE44F1038EE58C16CA25BB7A74AB2F603DC1691930F284AE23A63D816E1B313A1E433A4CB71350D5955GCC16691DE843E4C8B3EB8B4AF222F31952067E37620975F1E197191FD2EE42E37C7E4679B5G2FE1DBF0FEE132CDBEFFB09270A9GB9DBD11EB5A63286BF7815B93C576BDA935A3C9072EC5C4A67A2CDF23E41000F86C860FC7FBBE43E4C9637FFC9
	7AEB5222A37A535D5F484BB0CF37DF358CC35ED913153A1515DDFE1D4C1F1A411F1A511F8E1AD137E6C893D20732BFEE9ECABDE0F94EBB0665B49A729BDF4767760B14762A2C07BEFBBD729CF6EC22E4AF51867F1ACF7CC139B51071F9117331C09E11BC1F1F3A88DE3EBF1065255FC87A3BBEB6860A659D5A05B6C906BCE7ED43FC7F53C5665C5866C01C3BEA9B106B54GE9ADG211CC6EB2175525867E2F99F35E05E1110A619277B33856BAF1B9BCA36486706E6ED0731A60089E0E0BB526C5FC77C41A51B67
	E4BC9E64BC42A0EDBCCFBF2F1B7809E20322B8477628DE4EAF270169D18E4F5A86242F6BA67EA8EDDFC4FE37F4A33FD6C89B465367D95DF5925C66F463A1DDA6C0618EE847F6A0CD39D5DD2763D664D18BE987F620CFD997AAFDAA966212F8FEC956F32B7BABED68E3691067021D48B731CF596634C2DC50CE64BB5CF6EEF55F34017A2A778D1222FC1FF0794200F7BC17EF12C3A9DF4EAE98436EC2795A6C67A65FFB5B49F9EC378D0E827611EE36E37F2FFA9776819C0BD1CE976022F7211C4F294A193A8B7B99
	F9453C2F4D730AAA463C4A9B21CEB4E2DEDF762815C9C4A376B702B613F722EC6B22A86F3A92FED7ECA372F8350417BB60966CC69E2F2A72E85D0D72BA1770F14AEE4CEB66D24CAB8AF0A9BC2FB92714F9956E4950D46FC1F9E77DB4C3EC7F5DF7C022EFBE35946BC4A350846F4132ADAB9DFA5BFDE9A94ABA8A729B3787E5F56CD44ADA5E1421E9EEC21E9F151E233F5B497955G77A40A7A6F7C81E5B5019C91CDA8EB55AEB5D9131A30BC324A50EE6900DB500CF4239B5568DA1B116E41B264E787DC94277BE7
	039ADDCAB31663F3C7881DFC93A24B6F58B24CAF9352166E457C5EBA21EC53C8DBEC580B36FEFE5939597A16FAFA9E5EFD2322E85332F214298F7845F019DE5E2614A98D62727721CC57141F1BCC6B1B165076A450FD0CD88746F2F935A0CB4BBE3E66542314B79462E2393C87AA06AAAF233F3C021629EC23A32B7FB195FC6C3B8F7DE64DF274B7E1FF0626EABF625F67F8BB60227673F7DA3B1A6CA91C6E1695FC8CG3852967E3E2D9CFA3DB7D4229D8D105F7096142538C9A9CBA2440D63BC1F2DBC37F2DFD4
	4356235D77A3B2DBCEDE09364CBF003A7FEEA56F87GEE60G4A79DE271A1C311C6E70AA240B831CF09071DF2CC6FC95606C87B13F5D3B1579C5C1DC7CC1543BEF753975AB7E4E7926C01E0507106F19EE3531CC63A164DBDDF5EE76FE3F054C3728EEB294787D5A4B65F35623D2F6FA1D33B69CE2630A3EC34AB96B2996D2F6E1000FBA04F35683A6F2CF874ED96F8156A0851D0B916F84B1939D3B0C7DE2384CCC78527D82825E3B360D5AE7A4641984B086A02F957AB52D486381335FFB32B7777938A77B2A1F
	86F35FF5DDAB5E23B4E35DE068DAB95D2675032133F33A5103229B4669B6578C06AE1C537DF9D0F4511CEE6B0641500D62F4F10322CB64F4759B87C31746691E99945DF8CE37FD53E0688A8EA3DD7C206896F03A9D3503212B62F4FF9D94DD83276B5CBC983AB6CE17B6A8BA9727B3EF998CDDB0273BFE6BE06806F3BAE75DE068E2B81D36FEB0F423B95DAD0322CB62F4A7378D06AE1D53FDB6A8BA61885215B68C06EE96274B5DB1983AE2CEF77120682AB95D441D0321EB64F4310322B3F03A153B86C3574769
	5E9C94DD882753B78E06AE0253258E0AAE16534DEDA7F4F1C7247BA413E53F4748FE9348B91AA1FEABE159D9521FA904B05EA54870EBA83ED806E77D1F474F70FE97448DBF0A7CEB3214F28BED3E6F45E4FC3E4FA1794DEAE3FCA6F0FCA945EBE5F8467F01ECDC13A8053850B664FFF38E611FA963192713FE97093F4B45E72D8E757FF19A492FC52754FFB3451BF4CA7D7FB295756F0338E49D72CF266DC3CD3B4433D9765B843F87E46150F6643FE8AF49AF32DD493F1C62E35B157CC7B4A37F5190D72CC77ED7
	CED05ABFC4AF710B90FF470A4FB8BD72CF256BDA49FAA57F8FA8BEDD2F64FF62B4729F8FF1ED8664DFE1D2729FED9057F16019AE7E960C6CB94B087CAF26EBE545C6A57FD00A2FB2AA79979EC27EF59097EDC47E275A147C8BBAE4F7340A3F2B44E7C38772E77304ED9DCA7E6BA95E5E21647F941F4F8C0238548E641F57C17857F54A6E77177D360B3F07094F70CE642F21F361511DCA7E8594BF32D349BF1C4F7B47C3DC3989791FEED76A9FE692572460B9D27C1DA0BED3CD487FEE3ACEB84E24647F90456719
	157C577771B595089BB0A37FE2AB611FE216F44E147D4EBB46FE97094F72E348BF122EBD569ED3723F07621B0FA9792F1A047C8D9097F78C795F67213FBFE0F40F8D1A0E0DE76E301271CCE9D706268E208D208FA03C8B47B31F1BC93EBE47B3781D97F1A043647905D1A94F08AE77F6B4D191E6F4219EF4E50A70B8859D53633FF4DFE101A5C3D38AD08B5086B08CA082E0348575E9B826369F47FDFC66EDB30EF42735B8166970E7F3F59D1B7C5225DA54F4F49DA7BA0E073842EE28B3G8DGF600900018EE54
	7121E39B7D693881AF2BEC36711FAA0AB2F9E7F78E2DAC3F7720D316E5DC379ADD5290797D53AF3F9485BF71BE4DA6F2BF2BE28F88DEACA42ECF5D50A5DFBB9A877994DAB3B455G0DD61CFB7F0F0D6F3731E2FD9E6E71BE894CC76E74CB97E1D55365EFEBD57633300B18845C4FBEB04DED5F37156DF3CEA2FAF0DDDE277BC1C81EB9368C715BC86FB8087FD5C33885408E90829083908FB046067EB7296B47G6B981BA172F4BCE603B67350ECE775CBD7E0D77557E3C3733B8A8FBA255F95AB78095FB9BC2BAC
	87325F3D8652B65B795E3122BC110F454E7BE8C31473DD3FF2C62A5A655A2E2115C324DF3A11F6B59FFEE92E4ABEB3FA7F8D335B891B52EE641E7DC448AB0D5B6D3A8174DF61B85A4DE8991A9ECE3FF4D5475574E8B22BFEF751633B11688F7FF1DBF3EF04BC7BG0683449C677B7C1A797C7EF15C7794C97B5802835AA400F2876ABBF2087ABE63172E4E21266F633335EA7A123DEC328B08C43F5673FD268E3EFF6B8451A39C42B1GF1DC0FBB3A07264783FE69D2D47538E75A575E74F0AB2F5B9C4A72A2EBD2
	63A04FFCA716D7957F166682A76A79835D0F568AE197C00893756C358ECD4FDF79250BF42A35BF1DFB1575081CA39889E993387C37AD44FA1446657C51B63476275E03CE597EE8DDEAF2DE63D4FB77D6C15A86974AB9259F6DECF0211C1F8FD14E29F6FFF28ED71573E23B1A1C3110B60E4BD963607B03391C1114DFE1CF605F5FE0F4FD0B07265F459EF4CA7DCA9532B059B5CE143D3687E51FDACC729A98346C77948FCD76119EF4CA59C3D5E55F594377EAF359B71550315E20E5FF2BE4E832FF64C12714BDD3
	D576D9A778BA6489143DEF89492B7644E0E57FEA6950E4DF6CC127143D6504DA1D383EDF2DCE58A16D3093D8A7F61CC27D06F37D7E6241CF896AF23E621F6E049ADDE869506CF213875DB70AF03A2ADDFE5C2E34CBCEEF062698203A976D72BB3E273F39976D527C435064EC7720D34A99522B66FBB6FEDEA0124B10D7B63432196317AED9D5064F78D904CCAE43C80F3C823343B39EF4CABFAE6DD313E19EBF07D15B473FA535ECE8B21C7120D34AB020AA43573D78AE8E63B2D4168FCD060D9EF4CAFF086FD373
	5B4DEEE732D37B589E5A027E8C4D02FE745BAF366318253A9F659CB7C4B9B5957EEC656AD713F36BB1E55F061C858C013491DC4EB3BDE84F91DC4E87BC7829419899CFC637FE79506A448E3FF4B9A75568B20648EF428AFFF4C5A755FC6F36F634D565C95EAF771B1717FE39DFBA0B2A4E47AA0766C3FD9EF4CA9F9AF6D24D077A4CCA9F9A8E69C681A41EC49FBA5A04E7E15239DD262FA47C961C9A6C3B795E15C33367A3FE692ACE291569F7A6143D7194DF6FDBB5B4990E7B258BBE25C677656A2171DB6817AE
	6214DA193EBDC9D926648C6E88C89BFF8A4BB4F8B77AFAAA374BABC314739D3FF4C583EAE573C0B73EEFAA87F07E2D44C53FD78E6101811CEBB3F611FD5038272951E6361233E2ACEE06051C9730B94C1DE6AB623E24B83D4DC1BFB8A4FE5B275B7B37108EF42BFF8B69E8377A37100C5D3E3F83595D6D6DDBC96C1B0DD1740CEB8868978540F0A56B90B712638E2EC7DC82473D42F1299C57DA0338F19C77AA47651FC65C118D0853F25C6B9CD749F1ED9B91D747F1EFF0DCAB4735EFC21C1D635E6238E19CE728
	C5DCB8473D4DF1519CF7E6B362C6F15C679C1748F1C1DB901746F177EDC55CF80E3B348EF185E790373A9EF18BB8EE8447D5F15CD55B905740F1B336A32E0D63B6B5A04E45F1C5BB90974CF1B7F25CF00E7BE6A762E2B86ED90E9B4DF18D3B901744F11FF0DCBA471DECC41CF096F11FF15CAC0EDBCE5FF345E759B80561126978249AF00DBC5DF727894E8561900E5BF40660E2A0BC7AAC5F2FF1161CC1C9606175B4CC7ACCAC7C541996BF6DC320C0F8283FD7D24E9B0FEC959C53FDBFBDF31B86B20C673A1690
	D5C599164BFE7FB17BFC4923860CC762856541F4FE101E8B5686C15D88C29F1810CBF08D90EE63382D9A7415E19CE71AC8F0D1909E49F13B2692DC8204D30350069B2691430D61616DB4CC4E7931708BD34F371E04173BAD534FBB8F399FB15DB3E8BB13FF919B870BED94FD8FEB81D7F99162927620ADDBB86EA35A879B00F0A8472D25736411909EF1915FD3361758EEB48F2F22E1F2DE0B059FEDBE1FFA92BE1EFE790F734AC3D99F196E3753F35FA940BF135BA3BD8B6DD6788B445DC357C82AA15C48F163A8
	4EG613EDF20DD5EBDCD6CB40C07B3E9B8020787CE8FC5A712DF6075791AA171D0EB2F3C5767BA5A9F9E8DF2A5F1FBDCC16FBE998F61D93FC4DC798134E5B1476DBEC86BBD045BB86EFA0AF3C1B87817E82B10C344F6E1BCFCAD8D477070B707FC69C9684E45DF980F55BEF984EAB35F363C1A5E9110887226F1FB3CE6C13B658F63FD985A4FAD07F0AD475553337DAD90368C13569C081D7AF8F8B38D13F3C962FC2175FC37D104173B5D7EF95EF9F836035F527B924200F7B43747B10A9B8561C40E0B69C4DB0E
	633867297F967DAAC3D37EAB44FDCD7D379642AD3FC2DB659FA336B370709C9AA6E7FC44B9CD7379561370F23765C3671D0727AD43E8DFAE88F807F1FB3CCC579DC7C0B80163761FC0DB26F2DC9F1DEB4B3BB8C3D3F4B1629E2738F28857DE0C363A3F0F582E11079F216161FCAEE0F35FD074A47985DE1F0F8C0947605A467534CEDAC02EE15C9E17D2DCB80423B9AE410636CC60B8DB9E41E5C2B86F92BE2E2038A28817DF02363AF39231DDB58F4752B0B977444255137C69C9E8076ABF0C47DE3FBC4E25FDE4
	E360FB2D5E475B8FDA55475BA32D6A636DBFD9FD0F371F31FA9BEF4F233ABCBD071E4907AC0DCE3AA9071EFAE7F17FF25133DA4A5DB9BBF55DC6814FA17E41B8C9CC3359E55BD44179CE34FB57F11ADDDD47D9F6FA265B5663509B15FA4C25EB2BACCE5AAFC4B6E891F1D81A85145E25F3F49ABD6C30145233B8EF7415F6EF36E23A8EF4202E0DAEDB63F21E7719635EF57D55F1F5DDAFBB6EDD572B0F7B577506635EF57D3F637EF53D6BB8B90B5EEEE65FF0A517A9B09D9BBA6565A9FD2FF43AC90EA75FA4E3FB
	A55E18C96E01D359BBA7C4DFC26F2760771036C058AE564F30CB58DEA7B657D5E1616BBB17609E09A7699D9225415056823486E39BB05EE3CD5F9B380299AFA67F8BF279F7C8723FD42C325728D1E7B5DA18EC3153F0ADA3B898E5F350737F23A01C84B0064B166AF34FDE5E2F253629C87C4D4AA243615DFFBEF5287B0F60746EBF931D7E7D2740695DFF3EF47A779F2D53F7DD394EAC373774AD2D0347A64B5B0B55B85F99F59667DC4BFF0DF63EE282BD3BG61A00070DF73B3C5C53E6C9C8369C60B76CD7A35
	5C87E6F63971665F957ACB173CDE93D919CC7758306C5339CCF71CA2B2AD38B4C3D38D50FCA95F67B920A61365D246BF68D24F7712F8CF07C54E3B027B652DD3D56EBAEB52BBEC968B71CE816733E7385D619BF6A9DB0F0B03E77225D83FA2BAF97D3A9475F83509562F4B20FE8134DE465F394D3EEC6B3A4CD38F364F3DF09F49AF78B265791779949FF619727C4B1DFB717CCB844465046079628CB7FB2C96703D7C195E17DC2690EFF2C552BDE583A1CA396E2678609025DC2BCE20DCE190B70E4B75DA37DC2E
	AA6EA32F727DBC2D1723ED3737D11DA19C8DB07AF2346DCFBADFBAA4DF6ECD07DB68FE31B117ABF538136247DF2E54E1853F6FAC6F8A98C3DD01BA5CE31557C9C9071B77E25DCB3882F5885ACF4FCDDC19215182D4DD497BE88BFD6950F825240361CAF95DF35864F50FFCD718717E8B5FD354F7A572EEF5915EB1900E83C8613C8B7A83ED7B473971DEE517715EC13EF349F8BF4A6F09498FC55EFB0E53F39C90EE85F00572FE3CA3D05EA1210CB76B677E117208GDCCCA816433D0B5D6429C09F1F628C1447E8
	B79E8B689DB5C900CB63BC5EAEF63377F2FE7620A7D09E635DF89C24F75494DC857EF0957278FA099B0F954863ECEF20BCAA2F127338125EDDD38738964E23F129DB1BDD05E577B73E5F4FF295161D6EA4A93BC8880F84C83C8A4BEE4F29C065C93FCA6EB7B925727AA2FD3373A9B9FE936987323E4E7CED58968BD7CBFD1D422B593ECA71FE1E6D7CBDF7B54A7DFD83FD4FC1B888A07CEA147B1E004F0846DCAD177B329F5C4A259C4B459570995051EE792DAF135BC1EA13055D0166176416DFC6191BFC15185F
	F7811F41CDF74B6F0EE5EE792D447C3E75690742EF2436EA56EF6479F5153B65378A5F49A945DA41573E600DB6873F0765F4962E2D977F864BB90D36ADEDA48C907A9BBE7F325D171CD110EE24A8DF021B1C4BAA6465C23E794DF81FBD067D16D44E7B7BFE423BA88C7A87G8DE1487B17BE5B83C318E4233EB0F9DD0D27774C04GAEA20C5F3B385C2DDD635F43D832428D4F5B09BBFD1ED90B15710E8B136B7D1AFBFE7C7BFE37D43A61D7E19F6903BE651ABF391FBB99729C930675B2460C6515F78D5A6C789E
	E2339A8837G582FC11B991A3C499D8CE9068344DC43649DFD0D5CE60B68FDB2C900CB3B86ED764F15EEB22F667BAD3D0EA54683ED4135991A8557323C2B2E155B667AD5EE7971EF4B6EDC6D06DF437735CDC6DFE93896755EC56F1B9C866161G3157225E177B2C6F7157CA651562A6530CAA391FAE97D047077D6E4FC7483CD6BEDEAAF3113ECD9F8FAF2761FE816B29F183C94B46AF1B2D86F1D3843BA37DB13A3FE0F38E50E6A12E066EAF5884388FB96EB85DCB20078C8FCFC05CD80AEB825CE59C6724FB89
	5E81C55F4FC1DCA6457D8BF08BF39157C377927C9A523C42F19FD35C53106EB10E6B5D08B83D86F163B86E90CF573F89F195BC5D279477BC602E6469CE513D844F816EBF9CB71E625E865CC31C769A3A1760C3403D43F1F368DE02F9A073D7DC5F6BA84E88F93D1A0D38B83A3F60B640BD40535DC277976C07F0B7374BCE3A467789600EF35CF3945783343BB9AE0A6EAF18GF27CCBC0DC89FDEF921BDC45F187697E024F01768E4EF7855DCBF08B6036F3DCAC455D8A389E0EDBC97792100BB9FF4D73FB13625E
	825CB55CD67ADD08134A282091F1DD9CF7954559A07C4AB4444D206B17AB40CE87F230AE5437935F6FB6DAFBB66A9C78BEBCC57664D8F47D369E9776A5A68FE01B15F79D74392EE37EDFF79D1BB7E1FEFEDFAF194369F0683AE1685DE4B76A054C6B886D1A53C85B86E197C098C0ACC062F5586E9CBCC36FC2B43B687DC7556199A04252D3822F336B3EA1EDG0C7EDDE4B2028CB9995E1A65A96B2F781E50A048A7BC1C499A8F4F66C26477179C5286B80CCE73C9F604C69897CEE85F26EB68247E2606E35A42DC
	CF9E4BF211C775EFA5FBB47F161DDDE4FA9F51E09A8B6003GA2GC681247C9673BEBB11685DEDB368ACDC06EBA7E2DFD2389E53F5CC75344F4E8149BE1FD5CED558C76A9FCE5E66D946376C7654E944EE143760FAC927626BD9FF1169742FBD182686704DGAE00B000D8AEEBDEB361F7426CB4934BB21D3E65674492F93A5D4D1EBA5DF4DA52696AD9CA1D24EF87BFB241D37EC47E4D23D4197C63613770954A1F1D0DE9E65D1021A9862883B081845F00F2BDB960D9A649836897D1BC5D74531E72BFFEC612FF
	468E257CE47E127581961ED17681080CA3A16F841E7FD113270EA7CCC817F60324A33C506937DE985DEDE672EEED57693BBA9D369E2B81F5F80476A98AC95A61E80F2D87B03F864035818481C4G0C9A0E79198EFA5A636CC1AC4F941E6E3E031E7618A3C876D81EBBCDD91E4B519ECF9DD25A0368B386724E3991737FDD1B27BD7E4C4FC1975DA8592372C6F686136978EFFE0E318170EDGC1G91G23F85EF32C1EBA7EE045B2CF61695A2C1EBA2E19A069D838DAD966527725D7F52837B9BBBB10CFA667F3D1
	07272E619D3C8D3EC952D5FB935BCB4EF4FD0AFF532B8A708DA48EA084A07AA64CBB4D64296B67A6AC4FF81E2E426C296BC5D9122EA34B1465D9413FB7FCD73B277CFF62F36F49B279B3EFE27DDFC67711DE5DF7A79968FE1C1BB3B405B7232E56933CAD82DCB3008BA08CA076E64C6F4ADECFDDEF6F65ED91CF17546B296BBB59122EEB37ABF5DD41F55DE7D5AFD7AB771FD44E67962BCA3BEBC5BD465DAC5925A0026D85965BDDFE4638987055G2DG83G43A3B06F0B73BCF53DB18FF59D4953BD1767296B7C
	DCC957CFFF72A85765B817974C677262A13F1CDB705DFFE9832969AA6BAE6415A308EBA814369AE81A81ACG6CFE453EC5788D03685B2F941256FD16BDD5703CDFB125F9D75630DB18ED2244A739DFF178A50D59627EA93F670491D2C5FA71CEA6FADF7738DB583C65C40E5FCC71857FA761198D1E25EBC1A4DC8CF1A17F07EB48B3695C5A9888675C0AE314BFCCD34AD4F0AB633DG1ED537FA1BE37DA7DD53E93815714E65780FA83ECD06E7B27571BB20DD9017F4AB5FD3EA42756F6AC844BD47765AC2B8A292
	653C7B2CD24E68C8C936D1115E644C4AA2FC93A315F2CE257834C8251CE193D04E719057F69B5F2FBB8F659C4DF10F50B518D93743FB7AF644CD94689EB188B75E4E77BCF82C41355E4E643543F3586D5EE41FBBC0E4893FDDD96E0BA9BE66F6E5395FFD8A4BBD8E62CA6FC0196E27FD03D088C7F0DCA15F0F9BFD8756117F2DAF96D466EBBC4E5F4B67ED36F563BC4078BBF0BC3C115E15D499856DBB00A18A6DF01B47FDA3F2980872E6075D746E2B50A865DDC107A8BEA2CAF9D750DBF42D0604E3A0EE566F78
	FE68E654396FF7FC7F9839C6956790E43FE376DE78BD37CFDFD09E225F9AD06761DC67BBCFC8FA5EAF7BDDFD62426B3C77E76071264F3507745FF973334768BA14F027524F1E267802BB15FE363B8D7DCC8BF1436EC4BFEB56214D536E64E73F5AE53EA57B3D267D425BE34F4F40A3F22F526679F7C9F6565E654D6619F45D2C72AEE57B7CB945575D25EC1FAF5E0F6DF3AB440D3A0B6FB5DB08B6DF904D4F23503658856160E8AC073F853CE69496ADFF87A750B53628E8A64FA40EFF1562C74B70CC4EF60AA761
	94082B3D9BE5DA5C0FB2C55D0DB2BD61F1FF02B7083DDBAE531F697CF85C5DCA5BBDCF7149F7ABED57F29CEDB786621A6F61E7FE681A6C8888475F03FD303F5949DEB21D233302755352785ADF92277B4E09F4E91C6E47E2F73A5D45FC6C40690E75A0DD5E3DC8F73C441D6E927E0D07D977A25D353DC83700535D3E441DEE649264D7FEAF5A779F812F03565EAB376F5F697AD84B3D4A7660F50AB75D2BEC8FBACFE2FB5087F149DCEE73A9143BA6866FB12F5F067D5486408D0B41F7674594BF924209G69B17C
	7E822FEB9EB97739572D427B64BA5CC0574ACA6FE332CE6578BBA93ED106E7F215D0BC5D6B82F1A977218ECF52393E065FC31F6877083BEC9762AC9C7765EE4475F15C1A9D089B46F12BFA313E06F2DC13116E4307F0666FD16FDFF85EAFFABF53EF96BC0B6FD71CD93C9F6F97ECF5D07D6EE77ACD61FA9F23F83B8C4F747E00568D9A06384C7BD1269F3673739331083B052E97C5C1F8A4473DFD8C7BFD8931A87BB00F7B2312E3193CE36019730037B671BA3A76D47800325FF7AB4517BF206C771574E33F2F8E
	62229F607B69FBD07602879197C75FCDED90F6BD483FDB32CEFEEFBB2BC7EF716FA284F15A475BF919F15CF3740CE58A04B39F443521C57D58A7949EC23FFE042E11D4C13891407490BFBF6EF177D850C3621EB7F8C6810CFC485DCE4C96EF533526040714ED5F980ACFFDC85976756E4136EF9C44350EC079CF5133B023A01C44F15DFC5FFDBA477D0F7AF5759F205D7C83621CF41F10894283FF605F32DA695E9EB571EFA284F33AF5A664994EF147F85FB41A63A25A1167F80EEBE8C61E858F238D3F1B2C34E1
	51434CE665702CFD581B1FBDC14B2465E1258D7FCA71160715B65CEBC29B86C1DC6A43A8D364EE143D6E0F084B273207C3B81A631E6F46716708BFE2BB38FCG5B4151009B7FC77449FF161135AA653DFCF4605140A6A718DF59A8FD65C898EBG98C672F3662771BDB0G385811749B4F27655FEAA0E5724BECF6361D45BF6696CF766B158872780F5C6209FF6FF5237F7E0CBCBED7C64F66911EDD087388631F3DD130BC30AFCBED9E01460F94678F9EE14F1AAB87A6B43EE24DE279BCF7C87E3D07026B57E5CD
	7012CF0BC8EFF8C41A0F90470AD9E877F008CBF8846D5E795DC2343B5E56DDE6EE6FAE6BB951DD26EBF741D0508CE5303347E291CBG67CD5E25F965BD1A215182D4BD0AF5E1FC8E4933C3E7F19A398DA2FA6532D7CDEE767A2D0A46C7C55945E7742881D1767D39A87BF008CBFC945B18B00A4FFF8C7A87G550F21AF8C5320AFB4BF063E70FB0D52971EF4AB6BD40D5297D667496367CCD47A42D5936471F593153E701868AB4CB65B2752EF2DB9FA0CCC27145D28D38848B87AB154E99EED3746C3F856A89853
	0F625FB1EEC21DEAC721CEBFB6A9F5FA5FCD266EA625CE87F26571B7B7A9F53A5B4D3F1FEFD26AE49364BA2DEAF657E97C8454E90048B80AFF23147ACBBA04F39E4F5094BD0EBA15E423CE150F23CE7B3315BA3D61D6CECB8614BAD53815D36B00D2278DD97278AB86FC6B14B1A077E1A66761B338874072B86AF4D3AB5DCB856111G090FE37BF61389ED108E3885FF42742FD0FCAB04DDGA1GEC1E13AE190B3C979DA0EDA07FC8CCD33F53E56C5EE433589C625702C4F93A8F205DA27F04F63B610052EECF38
	7942DF8EA86DC6B659C9711F9FD05A6DC3B73BD59CD45A2D494DEE579EC4DFE0B2FEFE88ED164CEDD0CD3F33B56B8970EB005AA750E69FF4214DDA8197768426DFCA67B563A11C8AB07E897E2D69EE54FF56E8543F3EDB297F07EEFEF32ADB29FF76B4F97C5D5DCA7D97664863DF6FD66A3F41CD7FDA2B5CEF984E58A147B1DDC7F570F1G48BEECB46A7ADF0A9F8961C4007451286B7B1D28EB4E13286BDC136F36EC07C9296BEF736571E7CDCADD9FF173157BCCCADDD7644AF51DE3F62F776F72B978EA105175
	A46A34D8CF7051909E8D107CA47A7CBB13A41F5FF1A2B01F8FBB4147155C8EFFBA213443C3EEBE1BF9C2E90717264B639775FA3495EE6DDFCB2F528EDB5D4A7C11DE34838B7F4A6AD95EA9D634CD691F213F72E75E4E53FB6707C3B896A07E4F3C1D3773B51ABF73F65E6E3B3C3B6DCABD2FF1AB6F1B6DCABD1B5C5BF93BD24FFAB7BDE373D0CF567F3CE208F7308F9FB41D389EC27D2868A965DC4C960A2FFACAB99773ECBB4E45B4C35C082770FE187849E44D2AA88E7AACG0DGA600E00048B83CAF4614CB3E
	516673EE66E61B3D41F842E8119DA7E37D5D9D965E5F0D6377D950B51B5190CE824864786D949F3792462FCFC35BD60C7867AD3E666EB00F4158F034AC3DF8DE5DA70FDCB15DE0F8E4ABFE4F948C96120F05DCCAAC601A9EED6244F96A84FAEE085C83D17CB40E973695D09C048D9C3738184F61BF0DB6CA2738C8880F60387FD0DC8204DDA508BBD30CFA12343E649F61A75E3BCFDF47F9E47BC91FBB487C89ACF8066E13B2570A61B1AAF2621C94C28E509018AD4695BEA31DFF9077D456F9164D2F27CAE5B31A
	2EF3D900AE6899FEF74B84AC0748E778BD07F48FEF8204D3B9EEA63B67E4AF24DB423F7DB4C14D5F880DAF5D937C443B17437D2ABC7C595CDF193917C349BCAC07F1AA32FD8EFFF2BB67BF1B21A1B0D744E91FD516C362D9A23B51CA56588C62BC4CFBE7317DAFFD160F2B0EF916D774B4293C1E21673048DD82B54FE2F95D718D4199A0BC40F12D2778FC4E33D8B67369F8FAA40413G46F07C8F945F379472AC0577D385DF5BBCB5543585B98F5FE599BFE49E59027F760B597419817C6EB73367F3640821D84E
	4BCEE3B967442B57B7F91DAB04B484E60B3CCB6325F2E67A6C725847DBA5665BG4FB6858FD7BC4EE9BEC87B8E41714AF941BFD1FCC43CF2DE304E0A7302A3A02E68AFD86EA379DD88C1FF41F25CCF47EE89BC2CE3612598FE2769FC2EA191BE1E651CF5C16FFAE17BD45EEC66FB70C49946094F423F42780C55EB8316F755DF7D372F8D10266D2FD2F93B7E4A4A0104457B359C5C5EFFC5FBBE5966EE6F67E8B8321417C75B0537377D3C72D037F7977F06FC02E84BD47119739C7A639F69F9BAF2EF51820EDB39
	1F4FA9BE07B6791461A09C42F139AD745D8F61F15CF6A17BFD69C5E8872B1B6BB9E51F69B61FBC0672EE175B0F79E152C2744351AAB2E35B06FE1884E98860FBBE7DB9657B455967795E98CBBE67AB7A69BBFCBDEF9C376F0BAE12A0A7815A8F00CA0046840CFB0B45AD03FEB381405BFF40B813CBEE9FC2C71EC5890333FD0ECAFA097FC5EEBC300D9F2C6D555E791338FDE64F106C73329D6D934CE508BFCEF0E1900E6638FFD0DCC19974C74B90F764F8A0F5ADDA26EF58A0EC45F8748644C3EE1FC07BBB18F6
	2670115D53072A481586E27C0A45B8B6E4F1ED3325B8D331FCBC4C621F1AA3455FD462CE7B6E17D25C8BCB64342CBCBE684772984DED7F4AC95A2E72705BB49C3B8C435DA751C689037445404B616213010E8106824CB67438BB76A786754DD0C94E430326E37D0DCF699AF5EA02F2DDBCE7BB41E7A6A85745C336613AF84E7350CEBC0FED7C15F4EDB57EF979192F3B3663DAF14A73D8E637B410F7C8BA04053FC1BB84D08ED08B5076B7CC93C3538CC03F54C570655057065FE1A49D6F2BAEEBE803A721C7FDC8
	BE425F827703B6153435B2DCBA17F99B65A56FF712B6E5B0ED53D4955C9459EFEC77E78AFF5A6ED9A72B49A7D9445FC767CA3F2FAF12FEBFC22E2D915FB5BBFA71DD932B22A32E832A75F1C9DC5C5F146F1AE3C6CF1996FEAB70F27EAD9D97A682DD5A5F509FBE266BB479AFE4E834AFA0EE7DC46C87D4F21C1D362F43A09C7E825A3B5DC1662CA2A1BC82A081A08DE0BC0F3753784C068C4D2CC4700F65109FBC1B93799DFD3C6DA434287708973C7BC61A0FB804C8B79926BA10075CAFFC794424G7CA4DB748B
	D60E355B319CDD091E72494B102EC9C39A82D80E43931565F86DB64FB169B347F0CDBBAA9167F05B34D2595EC063497DEBA392314CE6533930E488E7F2DCD29F16ED5E5F51C6B7523D8C1590EEGB0F07C6FA8FEG4271ABC006953CCE77AB5FF904B6107AE570112EC10CE3BC6ABCF8845A2E7B9AF7E23933F23B2F874BED380ADC4AB1C78C24997DF7E9AEA1696F4AF2FBDD67D97FFEB9DF60711FD6603BD66CEB36A37FD4957E598A98A7727FDE64197F22471C124B137F6D6473DE22CD17FA6C49AAFC1171AD
	05E78DC0730BEA7BBEAA68F7D0CDAFAA67FE57D17C309715F33FEC0F8389C7C0DC4EBF50776269D9A3930487B8EEFB976E4B086038BB3A50C7E3B9AEEB8F6E4B48C9C2BFFC0F628AA1DC4AF19FD3DCCBA5683E92F12777340869282EC967378F42F8DCCBF9200F8EE5AC622F3D017E58C975F3B011F4DD3A9674EACC427174B08B7AD61F0A3E4A791390C8C3G5B2408A425EF059A896F8E1B43684EFBB135F7B4249F1104F3E517F6FA7A61535FCB756058CA2C87AC0FCE3AB7A7956847F079AFEAC6798BDFD22F
	9BCA79CBDF12642FF9C9121F71F81A6E0DE9GFC4BCB58FE6610ED97E27B39061EB1A777848EFB096F611FC2771CC1F894476570339309AF21FFBDCDF1424B70FEFE99F10994D78E61C66E0707CC72B60D24BDD73F90DED60E11DDEEBC4E079FFA0E117F389B4B26D1C587E53BE900B48430EC7ADED67A56776D1E7E5239C8603FAFDDACF07D2AD46F3072DFBF378C1A0E35F1EF51B958E1B21999BE1D62C3E5F8567675703DD543A1CEF8055F47F0FA307C595E1D47A7627E2558072F93C6DCBCA7EB0A203EF747
	708AAB0BE162B3697201896449565DEEAFA6FD75D808CBG488448CF06BE9FC0D5B22E3B456492994917CF8DE67275DD1D5E54CDCEFB710FA81B0D3D921A9C66E6F9BFCD36DC09779CFC1CCBAA55BE5DB1F1550EE4E5F3E08E5BF4A7F43B5A0F99752EE6DDA7AF1B7BBD761B86D6260F8F1A0E155D917ADD7B06E4E519F6D2FCCB3232CC4770FDDB86080BCB46354CA43A6F3974558CCD5D2BE843EA4DD2EF7A833251E1339B9D2EFE065DC7F7FF0A63425C215920EB50F4CC57BC8DFD273F2A344197946FFAD5E9
	036B797E2FA008CBFD957D7A571A21497D4F006F65F1274B9CB49D536BAA3A272B6EB5253E57D3FC73EBCAFD3F5F037A36C15C6857D05F99DE6FD571ADF75690FD3CE008BE4E769A963F2E5477C10A2FFCDD296FFA6E63B5909779BA6ADB16BDB4395B4FA86972D24479C8F81627F85B9FBB63B01123B6C5B94FD1C4714DA94AF90E48D61C67B0C0DCDC8A7FDE255FEFD32B4B7D7BC153B179F6513EF0699BCAFB37D0FC559BCAFB3F41772855C2DC549BA837634050644EE9D552653FA96EBF05E7651B5E6C9DC3
	7703553D29347743945F7226525E9B0F203DCD90977FA69F4B7B7D0E3217FA917079DCF739EF267B524A5FD25ABB0A622B5FD25A3B4202762E03386837D0EEED40777B384B7D670E2151A58E1A0E69F53C1348DF102A5477A4459725AA754D6EC4FD0BA1AEA495759D6B73BC28F739478CF13F517F06385F68863A2FE80C073E1194BF5EC35F9F7428EF5E5B999A4B5B286FEFCF8C717D6477FB456AF4190326935FCFF4AFDA425B9E6FA70ACFFE5B637DE4C3FD53A02E611F7C7D947039B6F739576B493AD28C64
	B39AA089A01D6759C063D2364058FBD70626F5A70CF376703DD2741CA3C96FAD7F51E2DE01489223C8A76E1522BC7C4D6B84B2EFC7C0B90F447AEDFF1F6C7DFE6364496A779B3FB9D97D3E7334495E6E9DE671E3A8AFE7CF3BDD5A3D063C7EB3196E076E251FBE9347BF7524C84531529D25448ECDB6FD17F3C73D08DB7AB55103DC2C50E404ACF550356477D01C4AA6F7CBEC6EB33BA039902A257315D679DEC361DF999AD2361570E4F7CFFCB28D570F6BG5786908CB01C44EF45B13E2EC0FDAE221F6E9F0B05
	3423G467F0B605651BB893D15C75A3F647730EDDAC368B7BB9CB6C7C33B0561AE51CDC65C2E2E0DAEAB6AD1D2A25723E19B132FB68D77F1EE2273CD441EAD007B1666A550732978476CEB79C190F03C75C6BE0E370A6E8A95EA7FB1A78B675B14F20726B139D35224F39E43BEFEAD17497C4AD94FBBBD461EC53B963E436CDA8E4F5AF758FD484CD687A77832D56BBBF2DB85FFC1F4215783B4DAC5DBA552FB4EEA4101ACB61D01596160BC34037D9DDFF6F831CC7233ACC13E7647644F60770DC409720F02E7A2
	G79468923FB749BF53FF8116E05CA0734635EC1DD1D27FC692AFDD72EEBFD49D419DE8D62DC810B3BF88DE98BE1EC5569B0BA1D9BF50E8A060F7C065695174DCE1C0659226DB45A2272DDDF3678E499794D66FCBA3ABD57CD2E6C76DCB799510DF3C316F7795D94D64FF51311F40F90D927988969925FC5FC9C4557EE877F85DC7EFB085FB968EF3A0CFC57333DCDD460A2DF104F37F78E1A075A5C12320D4636164D9B3D500973C6256F296F5D134F6B55C01A6677583C916DEF3E271CB75ACC57451B457BF5D8
	796671FB22DD6F615E6D1A3D4A3D59A48F626CAC5E31D739779A63457AE340BC235FD37319B16B3D7BD19139F2CD543F4049677BD5741FA083B57D474974E7F2DD25C339F252D15727F53EF57DCC67CDD7D647D37772BB6C52D99DEF062709643F836BED43FE2D8F3F88CA1757DB4D94522EEE32D91DB60B31F98347F71C4E177034D5E7ED7A6A9639FCE1A25F91695826CFDA04BA47277B2A3F6B17CB3F5B4995EBA25F0A1F6899830BE517033EF591FF77CE025FE96CD560AD4BBD5E2D7C4E58E5F6B29757E432
	750AFA086F25C9FD4A77AB69B3653C0F734EB7D6CA726C79093417CDC60BD16F8229F6A0FE6BB61277DE2B93E3C4DDC62D14E89F59C75EB19B5A619533035FF73C1D4C2D973E0FED47E49AAEEF4270555B06327F0C6497C8BA46632EA17188F4FD0F69FEBB5D3360B4599CAEFD0F38FF742DD512EDACC5C416CD96234E417BC9376A27AB4B3222C20C7BB73936D3E8521DB012FE92434DDA16A762B6C0E70B616EB3F864319C7348DE41F85A1CA493668B51BB143EC04A22946C49627F37CBAD3E66FD7E7DEF7EBD
	75E66EBF1AAAC94F98153BAE1F62F7DD063C4F6AF0A4BCC7G10BD610CEE6AC4A26B319E274B5C51AF3E2397CC44FD4F896F63BA4C3E0958E625287800324D4E04B4F99FC8EDD651874AB67B12631E770CEDD8A369F379DAA297ED531B9A514F6BC9DDE007C19AEC7A66ADA27EF0FD3E9C8FED88435FB3C0ECC27B883C2EAEDACB52F658C8DFFB171DFCAB0459BF25DDEE7F628F180D9ABE4072B95C256CE31278B69E7F085B79CC17C89F7E8116DF8F3FCBBD7A83DF6D15E12DE403B70E617A98AB4F517CBB7129
	9F30729C8F4F028FC106FDD81E259675FED7AD5D83D08CE9ABBF44767B43BE2D0F7A557A213C7D6EB8C85A45AD54EE16F6294F757AE95A2E6B2CFA2305F649C57C24F2D2D6D2DF3DC22C87497DCABB92596D9F221D5E645F399F7661E05F31177CC8FE0B6F87BD66B352CBBE0C66CF241BAF766FB652BDED649BEC199F727D5AA7F1DD3AF08C62EA0C583FAB6538FEA32ECB370C41B6F635190C17AD908E8288830862F135B4AE31857A4E00CB8190BE1256914FE5EFB061A38FC709E1A2030573FF731CF8A87BF7
	B866166FF119A93CE576EC13BE655F2957FE44FC388A1E8DG44964CD7DDF45D5FGB87BC7587EBC5A056DCF78C76AFD260902543EC4C31AD19FC96DCF62C74AF56025F43DBA8570699FE15DA92277EEB4187554E3996E37DBA6B19C38B873EF66B74D6B842E275EC2B6F6291E1B545B2CAE07CD7CBE538396744942313E5A005E75526F1ACD929FD327568F1FE6FE066251DA092E2DB9B079FE5F0C72B52A4A07F566134D12FCC737CAFCB6B487A29F73037649688741E3199FC440B39600FCC30F65774C947536
	6CCD3A9E9F8FE91346E2DBB6572475612FC2063CAD3BF39DA9D31CDB106EC25E3D5E3D4DDAAE36D99A33324DA2B24E4A40B6ABFDB75A2DB843D7395ED1A78858B7FAE7C07ABDD6767B5440053FAFAC6C0272E0EB223D7544E6EDE00F81008800D800388CDC9F0DDFCCB6A4EDE8D7DBDF5EEC68A45F82F33AF464AEFC71BB0445BE3EC3D8CCB6B20B73F204CAEF5C27F318C95F4109740F51EF4C345BDCAEDBB7499F71CF96931B38ECF60A943FC3D8CC6FECB5F71ADCAC2908CF20679AAD468E0A9657733325326B
	167D7EB86B421763748B7A5DF026732BCB48EFBE0749162A453854A5240C4DD6334B2C3318CF6AC8EF2A8992DABB19FC53B412BD3E147D0E483D703679434F40633ABDB21DE43FCB76DCF85E9BFF869E77E7C9BA0D147D5E7CB37876118B4AC36C33513BC4483747D3B3309F17C14FFC66E5E6E834197CEE34D63E8FBA937BCF0507C95F3C0D073F23E1978F5F59FA216417F8BEF141F8E473F258EB164A7D086C773F4E297F98188F88677D3B4B1EBEB01716AD790EFA98AF6FEB8E939C790E7A880E7B68B07AC0
	BC377F03C7C8190F6361D1B41C7FEF8C579D89C4FEC2BFF49B698262B15433B00C47ED96295C6F117DDE6D736E027363830DBF830F5ECEC927A0596FC91D971E77F7BF830FDB7412CEF74AFE2F50DFF85E8DBF830F6BEC12CE91325FA5361F61BDFCC1F908E7739A4958E20B59E25C41F18D741BAFE47F20047BD8CBF0E4F726048B25EBA3F5C60B1D6058787CD8C13160731EB937E9CDB646B9E545F3A7657F463B2266CF473D87A640C57C9B6F312BE8AB427C257B8337BA4C060D3ACEF6FD9D4B53BC9D679913
	7E0DF771DCC773AC78B8C3D38CD073B14EF56E58AEF01D66957B3B270825BBD4CCBA1D1BE024C09651593AEE2FD13E2EDB746D0CACDC9BD15A3E65E3F19F6D47523AAEF90A6B07A5B8669B71B1EF6F5D6E539C83781C7F78EE5B59D8A928018C568BA1EDA5C08B009DE09840F000980051GC9GE3G7246E5E80AG4A81EA811A81CCG8363F0ECF55F8FE4A32E373D27E2C1508116C3C7BFB7A66EA579DA40728C053C6246617836DD40F292BE41F28A25E92BA05C88E07A84476C3D9364B6E7F6B9BACFEE17A0
	C89B7609EF3BB0F9665AD11EA8C81F72894A236777B295FE0A72DCC553B6C258849074A97A4D02DD125F6CB995185F041E7254416836EFBB9C720F7E54378EEC5E6A39EEDF73664A35BE7F73665AEC3ACEE43275EE303089D706FF151E51626BC7FC9DED2ACDBEF7913C956CF5BCC3B36AD3BEEF3DD7ADBE71D334B5BB67C07CBC6D53C067E3F11EE8EBA389B17BCDF53B23DC7B99787AE73E6DA77A0EC1CED787B42DFE68185C9B1CFC2F43E7688B47F6CB72DC6B76FD63A8C8B352CF3E4CA7737721CFA6C07A3C
	71E82766FD6813B56351A7FFC15376C1B894A0EABC569173FE39FF31B6736FDD644035224DA453E59B9CC61D281335855B60D163314D1936C82E47B840677FB7907B5474E1DE5A7F22DC9A3A76C55FB37C1B47EF6D40F4AD7FC5DDD7B6A2AE743F7C5B1CEE5FAF0D83FC329FB918BDBF647B0D47C07A627FA10F557CBE4A367F21BDB751BD6961900E81087B9F3F77FCA2C9D74EF53BE4A216FD4A7F866B331753F1BCAB134FE7972A1749963351A26E2979122FB394FE0E324F64733C0D1F63BB7123E5AA653B49
	E454F7355B7AC439B31B10AE78F3346BC9373B47C781BE7173C06C1AFE0C6F8B0474DAA1D3142D328B6D5A82B8E657F6FA67E3A80423GC681B0DBE410EBA54576652FFD01351D797DF25F966DF19275CA023C4571235B37DC8BA6E4EA96G78EF8F2639FDDB3D8AE89A7C503179662EFE7E1DC1C8CF661B7B609982B07CA42EBB3CF792579DA2814F6615779B35BE720E97F9B37AA8A32EA924G1E153BFE8575831DD536CF49F61A2ECB1A758DE4A10D611E7EB11761E4FBA3FA685ABF4111E5D2121835590FB5
	AB6723098E79D919E21B2E57A977CE10F8AD0F3F6D309AFDA509273E739D771DBA4085E5216F9CBB063EB3A28BFDA71C269D83613C6CCC0DB69BFD6740DE49F7EE6C8A6CDE498F3B50CF2AA01F412D7D8D6C13FEFF3ADF625D3C67DB5F3CC9FD96FD6BE137BD68F610E1D8F6A0BE79D033DB5FGE8227D50B1BBDFBA996DBC8A520F4BC6BB7FEDB25A39BC876DACCC2167F4A09C84901E03F6EEBBA0697AE9E7E0F5344A64D9C7FF62FBA4E3F2300EFE64F646A68D7063F3FC6B457A1FE76B3D4F55DFDC6FFF2E7E
	CA7A1DD572892466FE3B1161426A094C360E8E27112F897C363E00636A1D0DB627194CC0337A7EFF7C9B0A85392C3E9743339AA06297785E18AC282FAFFDC77D2B195065E2BBF02C55D7BB901C1BA9DBDFBAF5186FBB2840BD2ACD9EF50E48B19C68183C352DA8EF8C6048F75E6260198C9073CBEC1F9E25F3D0649B0D19DC2E3CA33E64AA524865DA7AA321277B049A2D9676ED450F0E483F2DD82EE17C8C706C53209F84853C17BDD464476896CF9F5A9E7415C324338E1A0E6D2D1FD2C077E1026C6EFB6EE7D1
	FC0C8C4F6A6D0DFC5CBB92623493B345F3001B8F1232988661880051G49GE3G7272B2B5C5G15G4CF72F5ACE46605E46E86C32FDE908A60EC1CA5474EB4CE3B6B764F956CDB6F7F914566D3EBC26874399A8AED44409F52C41FB3D3D2141FF3D0DECD0DFE33B3341F30D6D5E8675B536879A64EBEC8C77F083E917BAE15C5A4253BD46F187B8EEB44D1360761B8DAE9347BFC365A278BAA3E148577328DC3D9457027B7FE53883A26E6586D21FC1E5134D01342FB710395E8E33454242EF51B4E6ABB4C55C9E
	E9D4C633DDA7137BFD0EBB407DD6AB8C4D5F4B06C857B9E8BA710EA1FA27FCF41E321ECC257891F94AFAF2AD1FCF988DF14513B24539749373BC7D7154BC39BFB2B9678F718C4B32A1521986CD4774B7516FCF07CCD25A65B84507CFD25A65BABE2F9189F1F91351AE8327BD6DF251E939DD583B3A56066FEA4BE46CB3E4727D4E1113315D1E64F606B19970E3A67BFEFFB31E8B76229C649B9B8CD7329765F8E38262CE98D44A5160D90EDB5C3E7B15B3A5D3D3B8A590B96E56F94A71FB1DDC8E712C533E211537
	F608F48786CD477C61E3BA9FD0BAC569A71FD3FC5594251F84733906DA080B1A02656AEC7145BFBF5F5BFBE061A24F72DA3248333CB677852A1FB6DF7E3ECF5B91A8DD259B5D5A46C0696AC4BA719BCB3D1E7A5C5C6B294F3F07F866BAFB08E71E3F9E74D9F866878F51790D56FC25FF0CE2E77473157ED12FC17FF0C1DCD2BE7AC748C4B97F6A29191A4629010CE30A743E64B6C09EFD0AFC4435F055333BA1E25A88955E0C6E931FE768E3E5F4F1D36565FE55F64FF23FF63BE7390F757886D8E065A78C718C73
	1AC1533172FD114E3FA4CFD5167B6B941FB6D5D96EA67E0D0ECC08EB190665DEF8D20D7F48E94CF689533C358B7F6B7134E7768939BD4576C32F52FE68656918FC3F69C779D201E751F4FCC73D520F635A6669B82E1DC43F5B978261C80011GEC2C324A9858D856E5140FE599AE429448181F4974B15D73C87A0D09DC4E6B69BB061CB34BAFC0DC72E144598B505E45C3742FD5C324338D1A0E79D1BE7517448225FF4D2678148225FF5D4467FF52A12E718B54577DECFFAC60633E8864BD7E3C4553EFDE34F87A
	57DF1BBD533D506CD92FC3BD3E3996187DEE9AA25DBF86CD476C6B22F3B649DFA86DFE1A62533ED05ADDB30557EB08EB1901F65F68711DBEDA2FE70875FA0637FA1D3747531ED377F85657E74E707A8AF9954DC41F1F416F3FEF1E09753566AC2D2F900E8498B99367216ADA810DE7B4DB832B3B1734FA4EC3DDFA987B3109B3F1BE52520A72654F4A54544EC279E2DBD13E01D9A85F1BB4EDAC046381D2E7E1FB722F430149347008675A4CAF16736F32F97BB6B7CE2A8828C749D1DCA3994FE53B6CA8112DF2F6
	26268140B09B7B7D575135F5722D120159D866DF503B21457E046C772D53AF7C1E06119714879B87B79E74BE57307F207F390636036AF38D9D87BD679A3A8E2A4FB59CBF68B957F0C24C174C3F6C54F573FC878E12361F1DE55821336ABA6939FCF15CC6778284C3590573B2FD2384770BC54E4635G5DA2D27E62FED9BAC54170C5FDD3A5BCCED108E5B1F902D476B3E53F078B975E8F9E7899F8D83F11F46A157D3E51FE61F9BF78B370283779FAF7E44E165A6E3CB972B151A79E5F7416C3519C092EFC0E3CEF
	2CB5E11B7FB447ADB4E11BAF618EBBBD5335BB6569189C6377C9E5123BDF7AFD453E8BEF3BBBAEA88F267325ADD8CFEB67E05DEDE5F763C6E7E842B86EF37E9D65880E5B5D083868B958EEBFA25B677EE7596FDD1A8BEFAF53856541F43EF522E71FA2EA22E7DFEE059E6DBA025B6B6CB602A35FEE4E7B92F14BE8BF308842259C97463F81DC45F1857C9B3D419CD7D9C4F0648E7A24AF517E49DD124D5F167D36F6DDF87BDFF2C1F9087622EB542920EF26E42F13E89B21906D705B003F3B2E28F3C324CB99B41D
	589726633F46C2E59F79E20AEFADD47611271F64FB8EA1AE1E6B7BD9339A7F722F203F885082E079CAED6E62E61DDA1985C15AB0B1FD14789C79156F31CE8244272A72F863309A0FF110B67FEB16DE7B35FA5E15002F1345352A249367C0E53EBED776BB725805777BC7AEA88F6663D3D4461085964F316149A9126E3F147D4E1FF2616D30700272E0BA9F206BF6F67003202F317EBFEA467A9F72B55607840FFB9783536135A152FDF9F6E875BF160EA7133FD6567FC7A8BE6DEBE57D5FF2160F11A12EE5AE6AFB
	65E155B172DCF10CBC57DB3DF1F85C1798183EC1C3247B43206918BD7651FBAFD367AA6DF41462474CD55AA90D0F1947C3DCDB915A296C28AF7E230BBC6DA42ED15031EBD21112FFDE9B152BC8497F2A23FCDF9F44B54F43F90250430092475A3FA9F17F6E5B7DC7C89C5BB7BB2E84774D0E1867A913FCCFAC8165F7966367C97230BA52E643BA12B48F6B4DB63A26D5FDFB0626789B5E4F58474FFAF15CBC7A4D36E1908E87E0725D52D2ABF837E33682A201EE84G6E6B0D7FC612CF3CF36B2840FFFF52A67DAE
	EA17EC32CD0FBC133E71340912E7BA24416FCB10F126702D44136DA739FB9B4ED164FF0B73A4DBAC6EE532DEAF713F0B5FA5DF742DFA194879AB3FEFD1AE634F72DB4C6FF62F7612DF2EACDF4C6FFB9157782D52066D864937361CF45763D7C6C90FA7CEA25FD6AFFC657EE507B484D00FE1B23E62FB662440FF0748FE4FBEF9615FB9E5971487737F99EC6D97740E78966BC4AF4511FBB122B96E053DD8F7C6F1DCE69B2DE3FFG3BEB9177B81DDFDFG61CA0EDB540E7955F05CAF5A11361963DE27B81316792B
	38FE514E4786BCDDBD1DC7CC02F0BA47E59F217B8667E7EA8A66A34EF4847996F15C2B8664DB4EF1FFE05FD600F0AB476551B1099D42C19CB7EAB64A92B69F6B145D6C6E0B93BAA5DF7CBB7F1EEC6AFC7F6D1C728E7DF173153E7870D9497F1E107DEEB8FB61FD51F8C1F9B07B5ECE4BB5673BCC4D2C6F5066D30E204D355F61FB2E7530243BD1767B038BF6E6D63243C4557E7E793543662328331D6B1C5BA669B9CD767B3A2397DE67188B4AC31CD3DF02EB83433F437E4482324DCB2CCB3DB62CCB095FF956A5
	EF5F847ECA4425FD27FC1FECB5CBF9AF5E07F9E72A640D6FAA2C27F95FE7EA88603B2A68FBA9EF56A6D42AFC9B6558B142EF10679568D8CFBC23474FA294FF2F3EFF1A51449016229C2337619DD362BAA7BF3B5076BD5A7808D56251426F0889677197592478CC3E57BFC1D58654B552AE49D032573DFDFC6AB809E3FD14083D38A7BFEF81763D5E5D0BE5D13E40FFBF2496529040FED04B82E5BFE81E8E776D9B96E01B1D24F31769DD07A4532EF614A99452B35CDD6D1847881E47476DC4F7275EE133D85AF50E
	FDE627395DECB13B7AC51F1A4D7BF329DE74107B54B8C8133FD06AC3EA97AAFD2A6DB05BB7EDF6112592A6D76FE1DCA3ABDD08F2FDFD44DD3705AEC9372BE8DBDA83E99B9622EE9AF65F950403F89E89CDE4CDC5E7B1F7DA495DAD1CBE2D897B1451BC6D3774FB469DB62B0B25D9C9FDA881621397E2197EE3B65AA2FF117F772096529040B22DDCA45902792166987AE14DA23EC659A3795CA3075DED50F50244B1D93EBB423F199400ACA110A6E41154BF0C10494276E8379E558A5E5B36A237BD53E5CB646333
	E848EB140A8C1EEDE3945F931848753D22D76071AD7BB0BEFF312FFAF9DF1F40E589F25B8FD48BF4AD0B8359C774065BFEA08B508485C4F758ED8EAF8CE82282227B44ED5F4BC820C970C3A71E39599B583914395CF629BEED1758AF795796DDA0FBD0D85A3E76405626935B835F5732053F8B0A0A83BDCB036F076927A4BD2EB86C795D584B872478978C7EE4E7B4D34EC8B477B485EE1B4F1BE64B5352C395E51BB66FEC5E3C074B93AABB8393BBFBBEBC2D46DEAD2759828DCF71F79B9CE61D653BD2ED857CDB
	DE426BC55E9C34D3332A1D104708C3D23D1862368FA78468A20A832923CDC710D72CCFDE2F503D94E2BFEF09673C6736A51E731EF73B2DB326C37EC2C9C0F54EEDAF6BAC20A976C3A76ED9D0598BB65CEDAF98430DD319377D2F4A5E9F174A5E1F7EE64FF48527BC534DBE65E98F773D3D5520D3E3C076586E5666990026AFA03A29F35CFC82E8A2823263F747BC75D9FC4CD31F16A3EEFB2FA06F3800729FED734C7FE91B0A7D772958FF1FE73A55AA692AD552352B78C7270AFFAC1863192EE40EE7BA5B914FF4
	2EA372F4DF527A342C6A5F13613D793E106B923EE832F538FAF58E2356A05E976F92F26975FEAE21C03C2AD93B4368F46ABA0DCE17B03951E153C348EC6DF489051B1AEA1B34FB0C96234EE954124FBDB83A6985E7AEE19A5EBE8F894B4A4ADC4254CD8E234EE554325BD101EF03D9EF34825906CE0751C83AC748576990E62935FB6B4E34394A9E7DCDF0962868902671A4B4F242EDDF7E32604A95BDD9C25E96C183CD315D242B97F2CF102B589A04A96DF4E38BB9ADE481359A886DB220DDC6E9FFEC90327420
	5BDC892FA7F8965BE457690D10E412C1676822B99C974E88D38E8913BA9C1D6D1401CB48BA3465903025A192BD35C1186CB2118B7AC86F49A5FCA9E55DC84EAA0991BF765945C6ED221D7EB98971A4F71CCB48A11F24GA63B36030D1ADCBA87843EC2839B8D5A727205DADB07362F7FE499090404DDD0D235C6174EECF1D26BCFE6179EE8B7D8C07F7CDA23D3961C8C7FEB59CD5EAEE17ACEF8AB3000F61755528F8C6936AD90025C6DCCF20207F6834BB6175E5FCC1FC642B807FCBFG98B499DD5A861BDE7485
	AA3706DDFF8B2F2C83440F32F65A440869E08BAB88E3BE811DFE1AEB8EB9DD8FDEB01D97F1BDBDB214F2E2455A6C37EE4D4E92F26A858D5B56D4AF64103DC9D05434046B05EC1000DAB5C78E542EA522DDF391B8B657AFD6605849B290C0D8F2158CA9A283A9229CF2098CEA201A8F7802E8DE17CD5BEE54CA4568072FFF99F35D3C678B19C72166824A6D16F2060A6FF92396FCD217285ADD614FDEF101E9B9BDG5D8361A4AF4749EEA9A7792176281F93FEF089337652CFD013D2F5129A52819EB01FC5A1CC42
	AA3156A54C971BC31D5659534EEE5A56C27329B56A74A6AD5DD887E9D8DB898D335129355ADCDAE31F5969AA556A992959355EA5D4CAEC752C9E111F5074E81DC647892323D4EBE72D334B512F35G19C3DBD626DED66019644A9452A8E8B775B89C50A473E6C156FE78B0A0960A3B23480BCA9D6B4351DD42727DE690285F56235511B7852A87B6335820A75C2975F81100F9CB6BB76E50B69B75A6ABB4611D7D5AC607718454D36DF6334BB33DCB1888EFB5C30F5E654DB0CB9A1991FBF5C05FEB022255769A35
	36F6E8E02DD49A07D18F05C69AC50F9A635DE7853734E84ADCD52C60579B51EB9D42D437DE7D13372C7CE77CEB7B1C590226DE780ACCB0B4999D74AED22321D667526D0382861F5C829958604D5484F8E8D41D74116BF475DB607DB92BE30F71F88F345BC643863C65943CE2B6FB2B75384C16E5F5BA2709CE29402BB2472A6B0637428C96ED51D9BB1731ABCF597BEE42069AAAB1DD7C84F9E7D2F9C9AEE5BC1719FF1C3EF07E36549159C25696E1B2E9A0376874AE1B23DE1864B2B90CCE93D40DBE7BFA16D19E
	BD4FC4B6B264C0F6F3397AC4597FEF6B406743409C7D7A07ACCFEFC66FB1C89BE2BDD8A528A2DE87D77164700B071F00D6BD7DF49208C1E7FBAD014BB14E7B324C777874BBCD8D0DD0CF02A260458FD0098F8AB0F81D8572723AE3C0D7FF094A75D40A5ED3108354F28C5A9C81DABC81216A5C57370F1EFEE253911D5BAD12408DF25787D070002396D8DB821BB2EFDF022A6136A340B8FB7F9CAE8E59FD8B66FEB85000505079655343F71FC1ADB120EB96208585CC8A6E23F5421E9BC4B52BEE01FD04507A25E1
	BE2241727C697B376B8FBEFB76725DD338814008849ABF97EEBCC84DBAB4B513FE88CEBD95DA7F8818700EB4B43F5D3081CCEE84131BC064C63048C6304846CD4058ACE8408B5065FF7CG79796B8F2C99268BFBDEEE001DF13672BF68E288D09F600E20E8C3856B56A658BCAAFA9F01E7ABF40F9614861DF186594BFEEAAFA85D45G45AB3642762CCB6D81DD10056BCE5F4BEF9F821BDC8F5F3CBE786E8968FE5FC3BF3FBCC55CF5F6FEAF487C4FB0B5D0C9085CB570DDAC2F1074GGD0CB87887A6660BA2CE0
	GGC8DE81GD0CB818294G94G88G88GEEE951AC7A6660BA2CE0GGC8DE81G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG66E0GGGG
**end of data**/
}
/**
 * Return the CancelBtn property value.
 * @return java.awt.Button
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Button getCancelBtn() {
	if (ivjCancelBtn == null) {
		try {
			ivjCancelBtn = new java.awt.Button();
			ivjCancelBtn.setName("CancelBtn");
			ivjCancelBtn.setLabel("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelBtn;
}
/**
 * Return the ClearMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getClearMI() {
	if (ivjClearMI == null) {
		try {
			ivjClearMI = new java.awt.MenuItem();
			ivjClearMI.setLabel("Clear");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClearMI;
}
/**
 * Return the CloseMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getCloseMI() {
	if (ivjCloseMI == null) {
		try {
			ivjCloseMI = new java.awt.MenuItem();
			ivjCloseMI.setLabel("Close");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCloseMI;
}
/**
 * Return the Button2 property value.
 * @return java.awt.Button
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Button getCommonLocBtn() {
	if (ivjCommonLocBtn == null) {
		try {
			ivjCommonLocBtn = new java.awt.Button();
			ivjCommonLocBtn.setName("CommonLocBtn");
			ivjCommonLocBtn.setEnabled(true);
			ivjCommonLocBtn.setLabel("Set Location");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommonLocBtn;
}
/**
 * Return the TextField1 property value.
 * @return java.awt.TextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.TextField getCommonLocTF() {
	if (ivjCommonLocTF == null) {
		try {
			ivjCommonLocTF = new java.awt.TextField();
			ivjCommonLocTF.setName("CommonLocTF");
			ivjCommonLocTF.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommonLocTF;
}
/**
 * Return the ConfigMgr property value.
 * @return oem.edge.ed.odc.applet.ConfigMgr
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private oem.edge.ed.odc.applet.ConfigMgr getConfigMgr() {
	// user code begin {1}
	// user code end
	return ivjConfigMgr;
}
/**
 * Return the Connection property value.
 * @return oem.edge.ed.sd.HttpsConnect
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private HttpsConnect getConnection() {
	if (ivjConnection == null) {
		try {
			ivjConnection = new oem.edge.ed.sd.HttpsConnect();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConnection;
}
/**
 * Return the Frame1 property value.
 * @return java.awt.Frame
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Frame getConsole() {
	if (ivjConsole == null) {
		try {
			ivjConsole = new java.awt.Frame();
			ivjConsole.setName("Console");
			ivjConsole.setMenuBar(getConsoleMenuBar());
			ivjConsole.setLayout(new java.awt.BorderLayout());
			ivjConsole.setBounds(55, 684, 470, 246);
			ivjConsole.setVisible(true);
			ivjConsole.setTitle("Console Messages");
			getConsole().add(getContentsPane2(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConsole;
}
/**
 * Return the ConsoleMenuBar property value.
 * @return java.awt.MenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuBar getConsoleMenuBar() {
	if (ivjConsoleMenuBar == null) {
		try {
			ivjConsoleMenuBar = new java.awt.MenuBar();
			ivjConsoleMenuBar.add(getFileM1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConsoleMenuBar;
}
/**
 * Return the ConsoleOkBtn property value.
 * @return java.awt.Button
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Button getConsoleOkBtn() {
	if (ivjConsoleOkBtn == null) {
		try {
			ivjConsoleOkBtn = new java.awt.Button();
			ivjConsoleOkBtn.setName("ConsoleOkBtn");
			ivjConsoleOkBtn.setLabel("Ok");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConsoleOkBtn;
}
/**
 * Return the TextArea1 property value.
 * @return java.awt.TextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.TextArea getConsoleTA() {
	if (ivjConsoleTA == null) {
		try {
			ivjConsoleTA = new java.awt.TextArea();
			ivjConsoleTA.setName("ConsoleTA");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConsoleTA;
}
/**
 * Return the ContentsPane property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane() {
	if (ivjContentsPane == null) {
		try {
			ivjContentsPane = new java.awt.Panel();
			ivjContentsPane.setName("ContentsPane");
			ivjContentsPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsTablePnl = new java.awt.GridBagConstraints();
			constraintsTablePnl.gridx = -1; constraintsTablePnl.gridy = 1;
			constraintsTablePnl.gridwidth = 0;
			constraintsTablePnl.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTablePnl.weightx = 1.0;
			constraintsTablePnl.weighty = 1.0;
			constraintsTablePnl.insets = new java.awt.Insets(5, 5, 5, 5);
			getContentsPane().add(getTablePnl(), constraintsTablePnl);

			java.awt.GridBagConstraints constraintsDownload = new java.awt.GridBagConstraints();
			constraintsDownload.gridx = -1; constraintsDownload.gridy = 0;
			constraintsDownload.gridwidth = 0;
			constraintsDownload.anchor = java.awt.GridBagConstraints.EAST;
			constraintsDownload.insets = new java.awt.Insets(5, 5, 0, 5);
			getContentsPane().add(getDownload(), constraintsDownload);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane;
}
/**
 * Return the ContentsPane1 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane1() {
	if (ivjContentsPane1 == null) {
		try {
			ivjContentsPane1 = new java.awt.Panel();
			ivjContentsPane1.setName("ContentsPane1");
			ivjContentsPane1.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane1.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsErrorLbl = new java.awt.GridBagConstraints();
			constraintsErrorLbl.gridx = 0; constraintsErrorLbl.gridy = 0;
			constraintsErrorLbl.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane1().add(getErrorLbl(), constraintsErrorLbl);

			java.awt.GridBagConstraints constraintsErrorOkBtn = new java.awt.GridBagConstraints();
			constraintsErrorOkBtn.gridx = 0; constraintsErrorOkBtn.gridy = 1;
			constraintsErrorOkBtn.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane1().add(getErrorOkBtn(), constraintsErrorOkBtn);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane1;
}
/**
 * Return the ContentsPane11 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane11() {
	if (ivjContentsPane11 == null) {
		try {
			ivjContentsPane11 = new java.awt.Panel();
			ivjContentsPane11.setName("ContentsPane11");
			ivjContentsPane11.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane11.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsFolderlbl = new java.awt.GridBagConstraints();
			constraintsFolderlbl.gridx = 0; constraintsFolderlbl.gridy = 0;
			constraintsFolderlbl.gridwidth = 0;
			constraintsFolderlbl.insets = new java.awt.Insets(5, 5, 5, 5);
			getContentsPane11().add(getFolderlbl(), constraintsFolderlbl);

			java.awt.GridBagConstraints constraintsCancelBtn = new java.awt.GridBagConstraints();
			constraintsCancelBtn.gridx = 1; constraintsCancelBtn.gridy = 1;
			constraintsCancelBtn.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCancelBtn.weightx = 1.0;
			constraintsCancelBtn.insets = new java.awt.Insets(5, 5, 5, 5);
			getContentsPane11().add(getCancelBtn(), constraintsCancelBtn);

			java.awt.GridBagConstraints constraintsErrorOkBtn1 = new java.awt.GridBagConstraints();
			constraintsErrorOkBtn1.gridx = 0; constraintsErrorOkBtn1.gridy = 1;
			constraintsErrorOkBtn1.anchor = java.awt.GridBagConstraints.EAST;
			constraintsErrorOkBtn1.weightx = 1.0;
			constraintsErrorOkBtn1.insets = new java.awt.Insets(5, 5, 5, 5);
			getContentsPane11().add(getErrorOkBtn1(), constraintsErrorOkBtn1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane11;
}
/**
 * Return the ContentsPane12 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane12() {
	if (ivjContentsPane12 == null) {
		try {
			ivjContentsPane12 = new java.awt.Panel();
			ivjContentsPane12.setName("ContentsPane12");
			ivjContentsPane12.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane12.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsErrorLbl1 = new java.awt.GridBagConstraints();
			constraintsErrorLbl1.gridx = 0; constraintsErrorLbl1.gridy = 0;
			constraintsErrorLbl1.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane12().add(getErrorLbl1(), constraintsErrorLbl1);

			java.awt.GridBagConstraints constraintsErrorOkBtn2 = new java.awt.GridBagConstraints();
			constraintsErrorOkBtn2.gridx = 0; constraintsErrorOkBtn2.gridy = 1;
			constraintsErrorOkBtn2.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane12().add(getErrorOkBtn2(), constraintsErrorOkBtn2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane12;
}
/**
 * Return the ContentsPane2 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane2() {
	if (ivjContentsPane2 == null) {
		try {
			ivjContentsPane2 = new java.awt.Panel();
			ivjContentsPane2.setName("ContentsPane2");
			ivjContentsPane2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsConsoleTA = new java.awt.GridBagConstraints();
			constraintsConsoleTA.gridx = 1; constraintsConsoleTA.gridy = 1;
			constraintsConsoleTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsConsoleTA.weightx = 1.0;
			constraintsConsoleTA.weighty = 1.0;
			constraintsConsoleTA.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentsPane2().add(getConsoleTA(), constraintsConsoleTA);

			java.awt.GridBagConstraints constraintsConsoleOkBtn = new java.awt.GridBagConstraints();
			constraintsConsoleOkBtn.gridx = 1; constraintsConsoleOkBtn.gridy = 2;
			constraintsConsoleOkBtn.insets = new java.awt.Insets(0, 10, 10, 10);
			getContentsPane2().add(getConsoleOkBtn(), constraintsConsoleOkBtn);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane2;
}
/**
 * Return the ContentsPane22 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane22() {
	if (ivjContentsPane22 == null) {
		try {
			ivjContentsPane22 = new java.awt.Panel();
			ivjContentsPane22.setName("ContentsPane22");
			ivjContentsPane22.setLayout(new java.awt.BorderLayout());
			ivjContentsPane22.setBackground(java.awt.SystemColor.window);
			getContentsPane22().add(getAboutPnl(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane22;
}
/**
 * Return the ContentsPane3 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane3() {
	if (ivjContentsPane3 == null) {
		try {
			ivjContentsPane3 = new java.awt.Panel();
			ivjContentsPane3.setName("ContentsPane3");
			ivjContentsPane3.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane3.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsLabel1 = new java.awt.GridBagConstraints();
			constraintsLabel1.gridx = 0; constraintsLabel1.gridy = 0;
			constraintsLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane3().add(getLabel1(), constraintsLabel1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane3;
}
/**
 * Return the ContentsPane31 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane31() {
	if (ivjContentsPane31 == null) {
		try {
			ivjContentsPane31 = new java.awt.Panel();
			ivjContentsPane31.setName("ContentsPane31");
			ivjContentsPane31.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane31.setBackground(java.awt.SystemColor.window);
			ivjContentsPane31.setForeground(java.awt.SystemColor.windowText);

			java.awt.GridBagConstraints constraintsPanel = new java.awt.GridBagConstraints();
			constraintsPanel.gridx = 0; constraintsPanel.gridy = 3;
			constraintsPanel.gridwidth = 0;
			constraintsPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPanel.weightx = 1.0;
			constraintsPanel.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentsPane31().add(getPanel(), constraintsPanel);

			java.awt.GridBagConstraints constraintsLicLbl = new java.awt.GridBagConstraints();
			constraintsLicLbl.gridx = 0; constraintsLicLbl.gridy = 1;
			constraintsLicLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLicLbl.insets = new java.awt.Insets(5, 10, 0, 10);
			getContentsPane31().add(getLicLbl(), constraintsLicLbl);

			java.awt.GridBagConstraints constraintsLicTA = new java.awt.GridBagConstraints();
			constraintsLicTA.gridx = 0; constraintsLicTA.gridy = 2;
			constraintsLicTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsLicTA.weightx = 1.0;
			constraintsLicTA.weighty = 1.0;
			constraintsLicTA.insets = new java.awt.Insets(0, 10, 0, 10);
			getContentsPane31().add(getLicTA(), constraintsLicTA);

			java.awt.GridBagConstraints constraintsLicTitleLbl = new java.awt.GridBagConstraints();
			constraintsLicTitleLbl.gridx = 0; constraintsLicTitleLbl.gridy = 0;
			constraintsLicTitleLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLicTitleLbl.insets = new java.awt.Insets(10, 10, 0, 10);
			getContentsPane31().add(getLicTitleLbl(), constraintsLicTitleLbl);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane31;
}
/**
 * Return the ContentsPane4 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane4() {
	if (ivjContentsPane4 == null) {
		try {
			ivjContentsPane4 = new java.awt.Panel();
			ivjContentsPane4.setName("ContentsPane4");
			ivjContentsPane4.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane4.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsDownloadLabel = new java.awt.GridBagConstraints();
			constraintsDownloadLabel.gridx = 0; constraintsDownloadLabel.gridy = 0;
			constraintsDownloadLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDownloadLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getContentsPane4().add(getDownloadLabel(), constraintsDownloadLabel);

			java.awt.GridBagConstraints constraintsProgressBar1 = new java.awt.GridBagConstraints();
			constraintsProgressBar1.gridx = 0; constraintsProgressBar1.gridy = 1;
			constraintsProgressBar1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsProgressBar1.weightx = 1.0;
			constraintsProgressBar1.ipady = 10;
			constraintsProgressBar1.insets = new java.awt.Insets(0, 5, 5, 5);
			getContentsPane4().add(getProgressBar1(), constraintsProgressBar1);

			java.awt.GridBagConstraints constraintsStopBtn = new java.awt.GridBagConstraints();
			constraintsStopBtn.gridx = 0; constraintsStopBtn.gridy = 2;
			constraintsStopBtn.insets = new java.awt.Insets(0, 5, 5, 5);
			getContentsPane4().add(getStopBtn(), constraintsStopBtn);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane4;
}
/**
 * Return the Button3 property value.
 * @return java.awt.Button
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Button getDownload() {
	if (ivjDownload == null) {
		try {
			ivjDownload = new java.awt.Button();
			ivjDownload.setName("Download");
			ivjDownload.setEnabled(false);
			ivjDownload.setLabel("Start");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDownload;
}
/**
 * Return the DownloadDlg property value.
 * @return java.awt.Dialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Dialog getDownloadDlg() {
	if (ivjDownloadDlg == null) {
		try {
			ivjDownloadDlg = new java.awt.Dialog(this);
			ivjDownloadDlg.setName("DownloadDlg");
			ivjDownloadDlg.setLayout(new java.awt.BorderLayout());
			ivjDownloadDlg.setBounds(32, 1793, 530, 119);
			ivjDownloadDlg.setModal(false);
			ivjDownloadDlg.setTitle("Downloading...");
			getDownloadDlg().add(getContentsPane4(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDownloadDlg;
}
/**
 * Return the downloadfile property value.
 * @return oem.edge.ed.sd.DownloadFile
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DownloadFile getdownloadfile() {
	if (ivjdownloadfile == null) {
		try {
			ivjdownloadfile = new oem.edge.ed.sd.DownloadFile();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjdownloadfile;
}
/**
 * Return the Label1 property value.
 * @return java.awt.Label
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Label getDownloadLabel() {
	if (ivjDownloadLabel == null) {
		try {
			ivjDownloadLabel = new java.awt.Label();
			ivjDownloadLabel.setName("DownloadLabel");
			ivjDownloadLabel.setText("Downloaded 00% of xyz.tar");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDownloadLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 2:53:46 PM)
 * @return java.lang.String
 */
public String getDownloadLocation(int row) {
	getFileDialog().setFile((String) getTableData().getValueAt(row,2));

	getFileDialog().setVisible(true);

//	getTable().setLocationValue(row,getFileDialog().getDirectory());
	return getFileDialog().getDirectory();
}
/**
 * Return the EditM property value.
 * @return java.awt.Menu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Menu getEditM() {
	if (ivjEditM == null) {
		try {
			ivjEditM = new java.awt.Menu();
			ivjEditM.setLabel("Edit");
			ivjEditM.add(getSelectAllM());
			ivjEditM.add(getUnselectAll());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditM;
}
/**
 * Return the ErrorDlg property value.
 * @return java.awt.Dialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Dialog getErrorDlg() {
	if (ivjErrorDlg == null) {
		try {
			ivjErrorDlg = new java.awt.Dialog(this);
			ivjErrorDlg.setName("ErrorDlg");
			ivjErrorDlg.setResizable(false);
			ivjErrorDlg.setLayout(new java.awt.BorderLayout());
			ivjErrorDlg.setBounds(479, 1051, 200, 98);
			ivjErrorDlg.setModal(true);
			ivjErrorDlg.setTitle("Error");
			getErrorDlg().add(getContentsPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjErrorDlg;
}
/**
 * Return the ErrorLbl property value.
 * @return java.awt.Label
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Label getErrorLbl() {
	if (ivjErrorLbl == null) {
		try {
			ivjErrorLbl = new java.awt.Label();
			ivjErrorLbl.setName("ErrorLbl");
			ivjErrorLbl.setText("Unable to save file!");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjErrorLbl;
}
/**
 * Return the ErrorLbl1 property value.
 * @return java.awt.Label
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Label getErrorLbl1() {
	if (ivjErrorLbl1 == null) {
		try {
			ivjErrorLbl1 = new java.awt.Label();
			ivjErrorLbl1.setName("ErrorLbl1");
			ivjErrorLbl1.setText("Unable to connect to the server, please try later ..");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjErrorLbl1;
}
/**
 * Return the ErrorOkBtn property value.
 * @return java.awt.Button
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Button getErrorOkBtn() {
	if (ivjErrorOkBtn == null) {
		try {
			ivjErrorOkBtn = new java.awt.Button();
			ivjErrorOkBtn.setName("ErrorOkBtn");
			ivjErrorOkBtn.setLabel("OK");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjErrorOkBtn;
}
/**
 * Return the ErrorOkBtn1 property value.
 * @return java.awt.Button
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Button getErrorOkBtn1() {
	if (ivjErrorOkBtn1 == null) {
		try {
			ivjErrorOkBtn1 = new java.awt.Button();
			ivjErrorOkBtn1.setName("ErrorOkBtn1");
			ivjErrorOkBtn1.setLabel("OK");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjErrorOkBtn1;
}
/**
 * Return the ErrorOkBtn2 property value.
 * @return java.awt.Button
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Button getErrorOkBtn2() {
	if (ivjErrorOkBtn2 == null) {
		try {
			ivjErrorOkBtn2 = new java.awt.Button();
			ivjErrorOkBtn2.setName("ErrorOkBtn2");
			ivjErrorOkBtn2.setLabel("OK");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjErrorOkBtn2;
}
/**
 * Return the ExitMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getExitMI() {
	if (ivjExitMI == null) {
		try {
			ivjExitMI = new java.awt.MenuItem();
			ivjExitMI.setLabel("Exit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExitMI;
}
/**
 * Return the FileDialog property value.
 * @return java.awt.FileDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FileDialog getFileDialog() {
	if (ivjFileDialog == null) {
		try {
			ivjFileDialog = new java.awt.FileDialog(this);
			ivjFileDialog.setName("FileDialog");
			ivjFileDialog.setLayout(null);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileDialog;
}
/**
 * Return the FileM property value.
 * @return java.awt.Menu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Menu getFileM() {
	if (ivjFileM == null) {
		try {
			ivjFileM = new java.awt.Menu();
			ivjFileM.setLabel("File");
			ivjFileM.add(getSocksMI());
			ivjFileM.add(getMenuSeparator1());
			ivjFileM.add(getExitMI());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileM;
}
/**
 * Return the FileM1 property value.
 * @return java.awt.Menu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Menu getFileM1() {
	if (ivjFileM1 == null) {
		try {
			ivjFileM1 = new java.awt.Menu();
			ivjFileM1.setLabel("File");
			ivjFileM1.add(getClearMI());
			ivjFileM1.add(getMenuSeparator11());
			ivjFileM1.add(getSaveMI());
			ivjFileM1.add(getSaveAsMI());
			ivjFileM1.add(getMenuSeparator21());
			ivjFileM1.add(getCloseMI());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileM1;
}
/**
 * Return the FolderDlg property value.
 * @return java.awt.Dialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Dialog getFolderDlg() {
	if (ivjFolderDlg == null) {
		try {
			ivjFolderDlg = new java.awt.Dialog(this);
			ivjFolderDlg.setName("FolderDlg");
			ivjFolderDlg.setResizable(false);
			ivjFolderDlg.setLayout(new java.awt.BorderLayout());
			ivjFolderDlg.setBounds(665, 1612, 222, 98);
			ivjFolderDlg.setModal(true);
			ivjFolderDlg.setTitle("Create Folder");
			getFolderDlg().add(getContentsPane11(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFolderDlg;
}
/**
 * Return the Folderlbl property value.
 * @return java.awt.Label
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Label getFolderlbl() {
	if (ivjFolderlbl == null) {
		try {
			ivjFolderlbl = new java.awt.Label();
			ivjFolderlbl.setName("Folderlbl");
			ivjFolderlbl.setText("Folder does not exist, create it?");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFolderlbl;
}
/**
 * Return the folderOption property value.
 * @return java.awt.Checkbox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Checkbox getfolderOption() {
	if (ivjfolderOption == null) {
		try {
			ivjfolderOption = new java.awt.Checkbox();
			ivjfolderOption.setName("folderOption");
			ivjfolderOption.setLabel("Create a subfolder for each order");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjfolderOption;
}
/**
 * Return the HelpM property value.
 * @return java.awt.Menu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Menu getHelpM() {
	if (ivjHelpM == null) {
		try {
			ivjHelpM = new java.awt.Menu();
			ivjHelpM.setLabel("Help");
			ivjHelpM.add(getAboutM());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHelpM;
}
/**
 * Return the Label1 property value.
 * @return java.awt.Label
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Label getLabel1() {
	if (ivjLabel1 == null) {
		try {
			ivjLabel1 = new java.awt.Label();
			ivjLabel1.setName("Label1");
			ivjLabel1.setText("Please wait while we obtain the records...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabel1;
}
/**
 * Return the Label2 property value.
 * @return java.awt.Label
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Label getLabel2() {
	if (ivjLabel2 == null) {
		try {
			ivjLabel2 = new java.awt.Label();
			ivjLabel2.setName("Label2");
			ivjLabel2.setText("Location:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabel2;
}
/**
 * Return the LicCanBtn property value.
 * @return java.awt.Button
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Button getLicCanBtn() {
	if (ivjLicCanBtn == null) {
		try {
			ivjLicCanBtn = new java.awt.Button();
			ivjLicCanBtn.setName("LicCanBtn");
			ivjLicCanBtn.setBackground(java.awt.SystemColor.control);
			ivjLicCanBtn.setForeground(java.awt.SystemColor.controlText);
			ivjLicCanBtn.setLabel("No");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLicCanBtn;
}
/**
 * Return the LicDlg property value.
 * @return java.awt.Dialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Dialog getLicDlg() {
	if (ivjLicDlg == null) {
		try {
			ivjLicDlg = new java.awt.Dialog(this);
			ivjLicDlg.setName("LicDlg");
			ivjLicDlg.setLayout(new java.awt.BorderLayout());
			ivjLicDlg.setBackground(java.awt.SystemColor.window);
			ivjLicDlg.setBounds(35, 2028, 416, 347);
			ivjLicDlg.setModal(true);
			ivjLicDlg.setTitle("License Agreement");
			getLicDlg().add(getContentsPane31(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLicDlg;
}
/**
 * Return the LicLbl property value.
 * @return java.awt.Label
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Label getLicLbl() {
	if (ivjLicLbl == null) {
		try {
			ivjLicLbl = new java.awt.Label();
			ivjLicLbl.setName("LicLbl");
			ivjLicLbl.setText("Will you agree to the following License Agreement:");
			ivjLicLbl.setBackground(java.awt.SystemColor.window);
			ivjLicLbl.setForeground(java.awt.SystemColor.windowText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLicLbl;
}
/**
 * Return the LicOkBtn property value.
 * @return java.awt.Button
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Button getLicOkBtn() {
	if (ivjLicOkBtn == null) {
		try {
			ivjLicOkBtn = new java.awt.Button();
			ivjLicOkBtn.setName("LicOkBtn");
			ivjLicOkBtn.setBackground(java.awt.SystemColor.control);
			ivjLicOkBtn.setForeground(java.awt.SystemColor.controlText);
			ivjLicOkBtn.setLabel("Yes");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLicOkBtn;
}
/**
 * Return the LicTA property value.
 * @return java.awt.TextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.TextArea getLicTA() {
	if (ivjLicTA == null) {
		try {
			ivjLicTA = new java.awt.TextArea("", 0, 0, java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
			ivjLicTA.setName("LicTA");
			ivjLicTA.setText("IBM Technology Preview Kit License Agreement");
			ivjLicTA.setBackground(java.awt.SystemColor.window);
			ivjLicTA.setForeground(java.awt.SystemColor.windowText);
			ivjLicTA.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLicTA;
}
/**
 * Return the LicTitleLbl property value.
 * @return java.awt.Label
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Label getLicTitleLbl() {
	if (ivjLicTitleLbl == null) {
		try {
			ivjLicTitleLbl = new java.awt.Label();
			ivjLicTitleLbl.setName("LicTitleLbl");
			ivjLicTitleLbl.setText("Product");
			ivjLicTitleLbl.setBackground(java.awt.SystemColor.window);
			ivjLicTitleLbl.setForeground(java.awt.SystemColor.windowText);
			ivjLicTitleLbl.setAlignment(java.awt.Label.CENTER);
			ivjLicTitleLbl.setFont(new java.awt.Font("Arial", 1, 12));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLicTitleLbl;
}
/**
 * Return the MenuSeparator1 property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getMenuSeparator1() {
	if (ivjMenuSeparator1 == null) {
		try {
			ivjMenuSeparator1 = new java.awt.MenuItem();
			ivjMenuSeparator1.setLabel("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenuSeparator1;
}
/**
 * Return the MenuSeparator11 property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getMenuSeparator11() {
	if (ivjMenuSeparator11 == null) {
		try {
			ivjMenuSeparator11 = new java.awt.MenuItem();
			ivjMenuSeparator11.setLabel("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenuSeparator11;
}
/**
 * Return the MenuSeparator2 property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getMenuSeparator2() {
	if (ivjMenuSeparator2 == null) {
		try {
			ivjMenuSeparator2 = new java.awt.MenuItem();
			ivjMenuSeparator2.setLabel("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenuSeparator2;
}
/**
 * Return the MenuSeparator21 property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getMenuSeparator21() {
	if (ivjMenuSeparator21 == null) {
		try {
			ivjMenuSeparator21 = new java.awt.MenuItem();
			ivjMenuSeparator21.setLabel("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenuSeparator21;
}
/**
 * Return the NewOrders property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getNewOrders() {
	if (ivjNewOrders == null) {
		try {
			ivjNewOrders = new java.awt.MenuItem();
			ivjNewOrders.setLabel("New Orders Only");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNewOrders;
}
/**
 * Return the SLAdecline property value.
 * @return java.awt.Dialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Dialog getNoConnect() {
	if (ivjNoConnect == null) {
		try {
			ivjNoConnect = new java.awt.Dialog(this);
			ivjNoConnect.setName("NoConnect");
			ivjNoConnect.setResizable(false);
			ivjNoConnect.setLayout(new java.awt.BorderLayout());
			ivjNoConnect.setBounds(36, 1610, 425, 98);
			ivjNoConnect.setModal(true);
			ivjNoConnect.setTitle("Error");
			getNoConnect().add(getContentsPane12(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNoConnect;
}
/**
 * Return the Panel property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getPanel() {
	if (ivjPanel == null) {
		try {
			ivjPanel = new java.awt.Panel();
			ivjPanel.setName("Panel");
			ivjPanel.setLayout(new java.awt.GridBagLayout());
			ivjPanel.setBackground(java.awt.SystemColor.window);
			ivjPanel.setForeground(java.awt.SystemColor.windowText);

			java.awt.GridBagConstraints constraintsLicOkBtn = new java.awt.GridBagConstraints();
			constraintsLicOkBtn.gridx = 0; constraintsLicOkBtn.gridy = 0;
			constraintsLicOkBtn.insets = new java.awt.Insets(0, 0, 0, 10);
			getPanel().add(getLicOkBtn(), constraintsLicOkBtn);

			java.awt.GridBagConstraints constraintsLicCanBtn = new java.awt.GridBagConstraints();
			constraintsLicCanBtn.gridx = 1; constraintsLicCanBtn.gridy = 0;
			constraintsLicCanBtn.weighty = 1.0;
			getPanel().add(getLicCanBtn(), constraintsLicCanBtn);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPanel;
}
/**
 * Return the ProcessDlg property value.
 * @return java.awt.Dialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Dialog getProcessDlg() {
	if (ivjProcessDlg == null) {
		try {
			ivjProcessDlg = new java.awt.Dialog(this);
			ivjProcessDlg.setName("ProcessDlg");
			ivjProcessDlg.setLayout(new java.awt.BorderLayout());
			ivjProcessDlg.setBounds(55, 1048, 288, 112);
			ivjProcessDlg.setModal(false);
			ivjProcessDlg.setTitle("Processing");
			getProcessDlg().add(getContentsPane3(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjProcessDlg;
}
/**
 * Return the ProgressBar1 property value.
 * @return oem.edge.ed.sd.ProgressBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ProgressBar getProgressBar1() {
	if (ivjProgressBar1 == null) {
		try {
			ivjProgressBar1 = new oem.edge.ed.sd.ProgressBar();
			ivjProgressBar1.setName("ProgressBar1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjProgressBar1;
}
/**
 * Return the SaveAsMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getSaveAsMI() {
	if (ivjSaveAsMI == null) {
		try {
			ivjSaveAsMI = new java.awt.MenuItem();
			ivjSaveAsMI.setLabel("Save As...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSaveAsMI;
}
/**
 * Return the SaveMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getSaveMI() {
	if (ivjSaveMI == null) {
		try {
			ivjSaveMI = new java.awt.MenuItem();
			ivjSaveMI.setLabel("Save");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSaveMI;
}
/**
 * Return the SelectAllM property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getSelectAllM() {
	if (ivjSelectAllM == null) {
		try {
			ivjSelectAllM = new java.awt.MenuItem();
			ivjSelectAllM.setLabel("Select All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectAllM;
}
/**
 * Return the MenuItem1 property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getShowConsole() {
	if (ivjShowConsole == null) {
		try {
			ivjShowConsole = new java.awt.MenuItem();
			ivjShowConsole.setLabel("Details...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShowConsole;
}
/**
 * Return the SocksMI property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getSocksMI() {
	if (ivjSocksMI == null) {
		try {
			ivjSocksMI = new java.awt.MenuItem();
			ivjSocksMI.setLabel("Connectivity...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSocksMI;
}
/**
 * Return the StopBtn property value.
 * @return java.awt.Button
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Button getStopBtn() {
	if (ivjStopBtn == null) {
		try {
			ivjStopBtn = new java.awt.Button();
			ivjStopBtn.setName("StopBtn");
			ivjStopBtn.setLabel("Stop");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStopBtn;
}
/**
 * Return the Table1 property value.
 * @return oem.edge.ed.sd.Table
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Table getTable() {
	if (ivjTable == null) {
		try {
			ivjTable = new oem.edge.ed.sd.Table();
			ivjTable.setName("Table");
			ivjTable.setLocation(0, 0);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTable;
}
/**
 * Return the TableData1 property value.
 * @return oem.edge.ed.sd.TableData
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SharedTableModel getTableData() {
	if (ivjTableData == null) {
		try {
			ivjTableData = new oem.edge.ed.sd.SharedTableModel();
		
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTableData;
}
/**
 * Return the Panel1 property value.
 * @return oem.edge.ed.sd.TableBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TableBorder getTablePnl() {
	if (ivjTablePnl == null) {
		try {
			ivjTablePnl = new oem.edge.ed.sd.TableBorder();
			ivjTablePnl.setName("TablePnl");
			ivjTablePnl.setLayout(new java.awt.GridBagLayout());
			ivjTablePnl.setJustify(0);
			ivjTablePnl.setTitle("Orders to be Downloaded");

			java.awt.GridBagConstraints constraintsTableSP = new java.awt.GridBagConstraints();
			constraintsTableSP.gridx = 0; constraintsTableSP.gridy = 0;
			constraintsTableSP.gridwidth = 0;
			constraintsTableSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTableSP.weightx = 1.0;
			constraintsTableSP.weighty = 1.0;
			constraintsTableSP.insets = new java.awt.Insets(15, 5, 5, 5);
			getTablePnl().add(getTableSP(), constraintsTableSP);

			java.awt.GridBagConstraints constraintsCommonLocTF = new java.awt.GridBagConstraints();
			constraintsCommonLocTF.gridx = 1; constraintsCommonLocTF.gridy = 1;
			constraintsCommonLocTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCommonLocTF.weightx = 1.0;
			constraintsCommonLocTF.insets = new java.awt.Insets(0, 0, 5, 0);
			getTablePnl().add(getCommonLocTF(), constraintsCommonLocTF);

			java.awt.GridBagConstraints constraintsCommonLocBtn = new java.awt.GridBagConstraints();
			constraintsCommonLocBtn.gridx = 2; constraintsCommonLocBtn.gridy = 1;
			constraintsCommonLocBtn.insets = new java.awt.Insets(0, 5, 5, 5);
			getTablePnl().add(getCommonLocBtn(), constraintsCommonLocBtn);

			java.awt.GridBagConstraints constraintsLabel2 = new java.awt.GridBagConstraints();
			constraintsLabel2.gridx = 0; constraintsLabel2.gridy = 1;
			constraintsLabel2.insets = new java.awt.Insets(0, 5, 5, 0);
			getTablePnl().add(getLabel2(), constraintsLabel2);

			java.awt.GridBagConstraints constraintsfolderOption = new java.awt.GridBagConstraints();
			constraintsfolderOption.gridx = 0; constraintsfolderOption.gridy = 2;
			constraintsfolderOption.gridwidth = 0;
			constraintsfolderOption.anchor = java.awt.GridBagConstraints.WEST;
			constraintsfolderOption.insets = new java.awt.Insets(0, 5, 5, 5);
			getTablePnl().add(getfolderOption(), constraintsfolderOption);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTablePnl;
}
/**
 * Return the ScrollPane1 property value.
 * @return java.awt.ScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.ScrollPane getTableSP() {
	if (ivjTableSP == null) {
		try {
			ivjTableSP = new java.awt.ScrollPane();
			ivjTableSP.setName("TableSP");
			getTableSP().add(getTable(), getTable().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTableSP;
}
/**
 * Return the SDHostingApp1MenuBar property value.
 * @return java.awt.MenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuBar getTableTest2MenuBar() {
	if (ivjTableTest2MenuBar == null) {
		try {
			ivjTableTest2MenuBar = new java.awt.MenuBar();
			ivjTableTest2MenuBar.add(getFileM());
			ivjTableTest2MenuBar.add(getEditM());
			ivjTableTest2MenuBar.add(getViewM());
			ivjTableTest2MenuBar.add(getHelpM());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTableTest2MenuBar;
}
/**
 * Return the UnselectAll property value.
 * @return java.awt.MenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.MenuItem getUnselectAll() {
	if (ivjUnselectAll == null) {
		try {
			ivjUnselectAll = new java.awt.MenuItem();
			ivjUnselectAll.setLabel("Deselect All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUnselectAll;
}
/**
 * Return the ViewM property value.
 * @return java.awt.Menu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Menu getViewM() {
	if (ivjViewM == null) {
		try {
			ivjViewM = new java.awt.Menu();
			ivjViewM.setLabel("View");
			ivjViewM.add(getAllOrders());
			ivjViewM.add(getNewOrders());
			ivjViewM.add(getMenuSeparator2());
			ivjViewM.add(getShowConsole());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewM;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addWindowListener(ivjEventHandler);
	getExitMI().addActionListener(ivjEventHandler);
	getErrorOkBtn().addActionListener(ivjEventHandler);
	getErrorDlg().addWindowListener(ivjEventHandler);
	getDownload().addActionListener(ivjEventHandler);
	getdownloadfile().addPropertyChangeListener(ivjEventHandler);
	getConsoleOkBtn().addActionListener(ivjEventHandler);
	getShowConsole().addActionListener(ivjEventHandler);
	getSelectAllM().addActionListener(ivjEventHandler);
	getUnselectAll().addActionListener(ivjEventHandler);
	getAllOrders().addActionListener(ivjEventHandler);
	getNewOrders().addActionListener(ivjEventHandler);
	getAboutM().addActionListener(ivjEventHandler);
	getAboutDlg().addWindowListener(ivjEventHandler);
	getSaveMI().addActionListener(ivjEventHandler);
	getSaveAsMI().addActionListener(ivjEventHandler);
	getCloseMI().addActionListener(ivjEventHandler);
	getClearMI().addActionListener(ivjEventHandler);
	getStopBtn().addActionListener(ivjEventHandler);
	getCommonLocBtn().addActionListener(ivjEventHandler);
	getErrorOkBtn1().addActionListener(ivjEventHandler);
	getCancelBtn().addActionListener(ivjEventHandler);
	getFolderDlg().addWindowListener(ivjEventHandler);
	getLicOkBtn().addActionListener(ivjEventHandler);
	getLicCanBtn().addActionListener(ivjEventHandler);
	getErrorOkBtn2().addActionListener(ivjEventHandler);
	getSocksMI().addActionListener(ivjEventHandler);
	connPtoP2SetTarget();
	connPtoP6SetTarget();
	connPtoP1SetTarget();
	connPtoP7SetTarget();
	connPtoP8SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TableTest2");
		setMenuBar(getTableTest2MenuBar());
		setLayout(new java.awt.BorderLayout());
		setSize(426, 449);
		setTitle("Software download");
		add(getContentsPane(), "Center");
		initConnections();
		connEtoC19();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:29:31 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	// SDHostingApp1.isFoundry= true;
	SDHostingApp1 app = new SDHostingApp1();
		
	//app.setUrl((String)args[1]);
		//	app.setToken((String)args[0]);
		//	app.setVisible(true);
	app.begin(args);
}
/**
 * Comment
 */
public void populateTable() {
		System.out.println("Populating table.");
	runMode = 0;
	//getTableData().setShowAll(true);
	showOrdersThread = new Thread(this);
	showOrdersThread.start();

	// Center the window
	Point winPos = getLocation();
	Dimension winSize = getSize();
	Dimension dlgSize = getProcessDlg().getSize();
	getProcessDlg().setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	getProcessDlg().setVisible(true);

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2001 10:11:01 PM)
 */
public void run() {
	switch(runMode){
		case 0:
		//	getLabel1().setText("Please wait while we obtain the records");
			getProcessDlg().setModal(false);
			while (! getProcessDlg().isShowing())
				try { Thread.currentThread().sleep(200); }
				catch (Exception e) {}
			getProcessDlg().toFront();
			getTableData().setShowAll(false);
			if(! getTableData().populate(getConnection())){
				getProcessDlg().dispose();
				Point winPos = getLocation();
				Dimension winSize = getSize();
				Dimension dlgSize = getNoConnect().getSize();
				getNoConnect().setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
				getNoConnect().setVisible(true);

				return;
			}
			getTable().setTableData(getTableData());
			getTableSP().validate();
			if(getTableData().getRowCount()>0)
			getDownload().setEnabled(true);
			getProcessDlg().dispose();
		break;
		case 1:
		
			while (! getProcessDlg().isShowing())
				try { Thread.currentThread().sleep(200); }
				catch (Exception e) {}
			getProcessDlg().toFront();
			getTableData().setShowAll(true);
			getTableData().populate(getConnection());
			getTable().setTableData(getTableData());
			getTableSP().validate();
			if(getTableData().getRowCount()>0)
				getDownload().setEnabled(true);
			getProcessDlg().dispose();
		break;
		case 2:
				boolean success = true;
			while (! getDownloadDlg().isShowing())
				try { Thread.currentThread().sleep(200); }
				catch (Exception e) {}
			getdownloadfile().stop = false;
			Vector fileIDs = new Vector();
			Vector filenames = new Vector();
			Vector ordernumbers = new Vector();
			System.out.println("stop status " + getdownloadfile().isStopping());
			for(int i =0;i< getTableData().getRowCount() && ! getdownloadfile().isStopping();i++){
				Boolean b = new Boolean(getTableData().getFileNode(i).getAttributes().getNamedItem("selected").getNodeValue().trim());
				// if file is selscted to be downloaded
				if(b.booleanValue()){
					getdownloadfile().setFilenode(getTableData().getFileNode(i));
					String currentFilename=getTableData().getFileNode(i).getAttributes().getNamedItem("name").getNodeValue().trim(); 
				//	System.out.println("Filename "+ i );
					boolean fileExists =false;
					if((filenames.indexOf(currentFilename))!=-1)
						fileExists = true;
					
					System.out.println("file exists " + fileExists);
					// if a file with the file name has already been downloaded
					//boolean success;
					if(fileExists){
						int index = filenames.indexOf(currentFilename);
						if(getConnection().compareFiles(getTableData().getFileNode(i).getFirstChild().getNodeValue().trim(),(String)fileIDs.elementAt(index)))
							success = getdownloadfile().copyFile((String)ordernumbers.elementAt(index),getConnection());
						else
					 			if(! (success = getdownloadfile().download(getConnection())) && ! getdownloadfile().isStopping()){
					 			getDownloadLabel().setText("Cannot Establish Connection,Download stopped");
					 			getStopBtn().setLabel("OK");
					 			return;
					 			}
					}
					else{
						if(! (success = getdownloadfile().download(getConnection())) && !getdownloadfile().isStopping()){
							getDownloadLabel().setText("Cannot Establish Connection,Download stopped");
					 		getStopBtn().setLabel("OK");
							return;
						}
					}

					if (success) {
						fileIDs.addElement(getTableData().getFileNode(i).getFirstChild().getNodeValue().trim());
						filenames.addElement(getTableData().getFileNode(i).getAttributes().getNamedItem("name").getNodeValue().trim());
						ordernumbers.addElement(getTableData().getFileNode(i).getParentNode().getAttributes().getNamedItem("number").getNodeValue().trim());
						//System.out.println("Added to vector" + fileExists);
						getTable().select(i,false);
					}

					
				}
				
			}
			java.io.File temp = new File("Orderlist.txt");
			if (temp.exists())
			temp.delete();
			if(!success)
				getDownloadDlg().dispose();
			else{
				getStopBtn().setLabel("OK");
				getProgressBar1().setCompletion(100);
			}
		break;
	}
}
/**
 * Comment
 */
public void saveText(ActionEvent e) throws Exception {
	if (e.getSource() == getSaveAsMI() || textFile == null) {
		getFileDialog().setVisible(true);
		String file = getFileDialog().getFile();
		if (file != null) {
			String directory = getFileDialog().getDirectory();
			if (directory != null)
				textFile = new File(directory,file);
			else
				textFile = new File(file);
		}
	}

	if (textFile != null) {
		FileWriter f = new FileWriter(textFile);
		PrintWriter p = new PrintWriter(f);
		StringReader s = new StringReader(getConsoleTA().getText());
		BufferedReader r = new BufferedReader(s);
		String line;
		while ((line = r.readLine()) != null)
			p.println(line);
		p.close();
	}

	return;
}
/**
 * Comment
 */
public void sDHostingApp1_Initialize() {
	getTableData().setDestination(this);
	return;
}
/**
 * Set the ConfigMgr to a new value.
 * @param newValue oem.edge.ed.odc.applet.ConfigMgr
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setConfigMgr(oem.edge.ed.odc.applet.ConfigMgr newValue) {
	if (ivjConfigMgr != newValue) {
		try {
			ivjConfigMgr = newValue;
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (11/5/2001 7:33:26 PM)
 * @param b boolean
 */
public void setCreateFolder(boolean b) {
	createFolder = b;
	}
/**
 * Comment
 */
public void setLocation(java.awt.event.ActionEvent actionEvent) {
	getFileDialog().setFile("Example.tar");
	getFileDialog().setVisible(true);
	getCommonLocTF().setText(getFileDialog().getDirectory());
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (01/30/01 4:14:57 PM)
 * @param dlg java.awt.Dialog
 */
private void setLocation2(Dialog dlg) {
	Point aLocScr = getLocationOnScreen();
	Dimension aSize = getSize();
	Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();

	Point dLoc = new Point();
	Dimension dSize = dlg.getSize();

	dLoc.x = aLocScr.x + aSize.width / 2 - dSize.width / 2;
	dLoc.y = aLocScr.y + aSize.height / 2 - dSize.height / 2;

	// if too far right, slide it left...
	int i = sSize.width - (dLoc.x + dSize.width);
	if (i < 0)
		dLoc.x += i;
	// if too far left, slide it right...
	if (dLoc.x < 0)
		dLoc.x = 0;

	// if too far down, slide it up...
	i = sSize.height - (dLoc.y + dSize.height);
	if (i < 0)
		dLoc.y += i;

	// if too far up, slide it down...
	if (dLoc.y < 0)
		dLoc.y = 0;

	dlg.setLocation(dLoc);
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 10:52:34 AM)
 * @param param boolean
 */
public void setslaAccept(boolean param) {
	slaAccept = param;
	}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2001 10:07:59 PM)
 * @param token java.lang.String
 */
public void setToken(String token) {
	getConnection().setToken(token);
	}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2001 10:08:35 PM)
 * @param param java.lang.String
 */
public void setUrl(String param) {
	if(param.indexOf("foundry")>0 || SDHostingApp1.isFoundry){
		System.out.println("THIS IS FOUNDRY");
		SDHostingApp1.isFoundry=true;
	//	getTableData().setupsharedTableModel();
		getConnection().useFoundryUrl=true;
		getTableTest2MenuBar().getMenu(2).remove(0);
		getTableTest2MenuBar().getMenu(2).remove(0);
		getTableTest2MenuBar().getMenu(2).remove(getMenuSeparator2());
		System.out.println("reseting the table model");
		//getTable().setTableData(getTableData());
	}
	getTableData().setupsharedTableModel();
	getConnection().setServerUrl(param.trim());
	}
/**
 * Comment
 */
public void ShowAll(boolean b) {
	System.out.println("show all pressed");
	if (b)
		runMode = 1;
	else
		runMode = 0;
	//getTableData().setShowAll(true);
	showOrdersThread = new Thread(this);
	showOrdersThread.start();

	// Center the window
	Point winPos = getLocation();
	Dimension winSize = getSize();
	Dimension dlgSize = getProcessDlg().getSize();
	getProcessDlg().setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	getProcessDlg().setVisible(true);
	return;
}
/**
 * Comment
 */
public void showConsole_ActionEvents() {
	// Center the window
	Point winPos = getLocation();
	Dimension winSize = getSize();
	Dimension dlgSize = getConsole().getSize();
	getConsole().setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	getConsole().setVisible(true);
	getConsole().toFront();
}
/**
 * Comment
 */
public void StartDownload() {
	//System.out.println("download pressed in Start Download");
	//if(getTableData().getParser()==null)
	//return;
	
	if(getCommonLocTF().getText()==null|| getCommonLocTF().getText().trim().length()==0){
			getErrorLbl().setText("Location not specified!");
			displayError();
			getErrorLbl().setText("Unable to save file!");
			getCommonLocTF().requestFocus();
			return;
	}

	File file = new File(getCommonLocTF().getText().trim());
	if (! file.exists()){
		createFolder = false;
		Point winPos = getLocation();
		Dimension winSize = getSize();
		Dimension dlgSize = getFolderDlg().getSize();
		getFolderDlg().setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
		getFolderDlg().setVisible(true);
		if (!createFolder)
			return;
	}
	
	// handle this case
	getdownloadfile().fileDestination = getCommonLocTF().getText();
	getdownloadfile().createFolder = getfolderOption().getState();
	
	getTableData().getParser().writeDocument();
		//	SDHostingApp.Debug("in case 2", "null");
			//	System.out.println("SLa required ??");
				//System.out.println(getTableData().slaRequired());
				//System.out.println(slaAccept);
			if((getTableData().slaRequired()) && (! slaAccept)){
			//	System.out.println("SLa required");
				getLicOkBtn().requestFocus();
				getLicTitleLbl().setText("Software Licence Agreement for Preview kits");
				setLocation2(getLicDlg());
				getLicTA().setText(licenceTxt);
				getLicDlg().show();

				if (! slaAccept) {
					//getSLAdecline().show();
					return ;
				}
			}

				
					
	runMode =2;
	downloadfileThread = new Thread(this);
	downloadfileThread.start();
	// Center the window
	Point winPos = getLocation();
	Dimension winSize = getSize();
	Dimension dlgSize = getDownloadDlg().getSize();
	getDownloadDlg().setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	getDownloadDlg().setVisible(true);
//	getDownloadDlg().setModal(false);
return;
	
}
/**
 * Comment
 */
public void stopDownload() {
	//System.out.println("label" + getStopBtn().getLabel().trim());
	if(getStopBtn().getLabel().trim().equalsIgnoreCase("ok")){
		System.out.println("MESSAGE LABEL = OK");
		getStopBtn().setLabel("Stop");
		getDownloadDlg().dispose();
		
	}
	else
		getdownloadfile().stop();
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (6/26/2001 8:29:12 AM)
 * @param msg java.lang.String
 */
public void syntax(String msg) {
	if (msg != null)
		System.err.println(msg);

	System.out.println("Command syntax:");
	System.out.println("  java oem.edge.ed.sd.SDHostingApp1 -URL URL " + TOKEN_OPT + " ID\n");
	System.out.println();
	System.out.print("options:\n\n");
	System.out.println("\t-URL URL - url to DSC server");
	System.out.println("\t" + TOKEN_OPT + " ID - Session id token.");

	System.err.println("Unable to start Client Software for Design Solutions.");
}
}
