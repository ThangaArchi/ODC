package oem.edge.ed.odc.webdropbox.server;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.struts.action.Action; 
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



import  oem.edge.ed.odc.util.TimeoutManager;
import oem.edge.ed.odc.dropbox.client.DropBoxPnl;
import oem.edge.ed.odc.dropbox.client.sftpDropbox;
import  oem.edge.ed.odc.dropbox.common.*;

// JMC Comment out for Services
//import  oem.edge.ed.odc.ftp.common.*;
//import  oem.edge.ed.odc.ftp.client.*;
import oem.edge.ed.odc.dsmp.client.ErrorRunner;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.tunnel.common.*;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.service.helper.Operation;

import  com.ibm.as400.webaccess.common.*;
import  oem.edge.ed.odc.util.*;

import java.util.*;
import java.io.*;
import java.util.zip.*;
import java.net.*;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpSession;
import javax.swing.SwingUtilities;




import oem.edge.ed.odc.webdropbox.server.DboxPackageInfo;
import oem.edge.ed.odc.webdropbox.server.ETSComparator;

import oem.edge.ed.odc.webdropbox.server.UserDropbox;

import oem.edge.ed.odc.webdropbox.server.DboxFileInfo;
import oem.edge.ed.odc.webdropbox.server.WebDboxAclInfo;
import oem.edge.ed.odc.webdropbox.server.WebDboxCreateDraftPkgInfo;
import oem.edge.ed.odc.webdropbox.server.WebDboxCreateGroupForm;
import oem.edge.ed.odc.webdropbox.server.WebDboxCreateGroupInfo;
import oem.edge.ed.odc.webdropbox.server.WebDboxDraftPkgForm;
import oem.edge.ed.odc.webdropbox.server.WebDboxDraftPkgInfo;
import oem.edge.ed.odc.webdropbox.server.WebDboxOptionsForm;
import oem.edge.ed.odc.webdropbox.server.WebdropboxTimeout;
import oem.edge.ed.odc.webdropbox.server.WebDboxInboxInfo;


import oem.edge.ed.odc.webdropbox.server.WebDboxInboxForm;
import oem.edge.ed.odc.webdropbox.server.WebDboxAddFileForm;
import oem.edge.ed.odc.webdropbox.server.WebDboxCreateDraftPkgForm;
import oem.edge.ed.util.SearchEtc;

import java.text.SimpleDateFormat;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;



/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2006                                     */
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

/*
 * @author george
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/* WebDropboxActions.java is the main file hosting all of the Web Dropbox Actions. */
/* Most of the actions are enlisted here as doXXX().                       */
/*                                                                         */  
/*    Creation/Modification History  :                                     */ 
/*     																	   */ 	
/*    George        15-March-2005   - Created                              */  
/*    George        06-Jan-2006   - CSR - IBMCC00009471                    */  
/*                  - made changes to solve Edit Package Recipients(Sent & */
/*                  - Drafts) prob and Empty Drafts                        */

public class WebDropboxActions extends DispatchAction {
	
	
	//	This will contain SessionID -> UserDropbox mapping. The 
	//  DropboxSessionListener class will ensure that this hash gets cleaned up
	//  upon session expiration
	// static protected Hashtable globalHash = new Hashtable();
	
	//	static public Hashtable getDropboxSessions() {
	//	   return (Hashtable)globalHash.clone();
	//	}
	
	class NotLoggedInException extends Exception {
		public NotLoggedInException(String s) {
			super(s);
		}
		public NotLoggedInException() {
			super();
		}
	}
	
	
	protected String getMethodName(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			String parameter)
	throws Exception {
		
		DebugPrint.printlnd(DebugPrint.INFO5, "In getmethodname: " + parameter);
		
		int idx = parameter.indexOf(":");
		if (idx >= 0) parameter = parameter.substring(0, idx);
		
		DebugPrint.printlnd(DebugPrint.INFO5, "Returning: " + parameter);
		
		return parameter;
	}
	
	public ActionForward execute(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception {
		
		// Get the parameter setting from mapping ... if none, let parent handle
		String parameter = mapping.getParameter();
		if (parameter == null) {
			return super.execute(mapping, form, request, response);
		}
		
		String name = getMethodName(mapping, form, request, response, parameter);
		
		// Invoke the named method, and return the result
		return dispatchMethod(mapping,form,request,response,name);
	}
	
	
	// JMC log and logerror method steps toward logging
	static void log(String s) {
		DebugPrint.println(DebugPrint.INFO, s);
	}
	
	static void logerror(String s) {
		DebugPrint.println(DebugPrint.ERROR, s);
	}
	
	static void logerror(Exception e) {
		DebugPrint.println(DebugPrint.ERROR, e);
	}
	
	// JMC
	// Errors come from Dropbox in the form mainmsg<@-@>submsg (for DboxException)
	//  so we create a message based on that formatting if possible
	static protected void createErrorFromException(String action, Exception e, 
			ActionErrors errors) {
		
		if (e != null) {
			if (errors != null) {
				
				// Set l1 as general high level message. l2 has more details
				String l1 = e.getMessage();
				String l2 = null;
				int sepidx = l1.indexOf("<@-@>");
				if (sepidx >= 0) {
					l2 = l1.substring(sepidx+5).trim();
					l1 = l1.substring(0, sepidx);
					if (l2.length() == 0) l2 = null;
				}
				
				// l1 will always have a value ... l2 only if we have a valid submessage
				if (l1 != null) l1 = l1.trim();
				if (l1 == null || l1.length() == 0) {
					l1 = e.getClass().getName();
				}
				
				if (action != null) {
					if (l2 != null) {
						errors.add(ActionErrors.GLOBAL_ERROR,
								new ActionError("error.generic.localplus2", 
										action, l1, l2));
					} else {
						errors.add(ActionErrors.GLOBAL_ERROR,
								new ActionError("error.generic.localplus1", 
										action, l1));
					}
				} else {
					if (l2 != null) {
						errors.add(ActionErrors.GLOBAL_ERROR,
								new ActionError("error.generic.nolocalplus2", 
										l1, l2));
					} else {
						errors.add(ActionErrors.GLOBAL_ERROR,
								new ActionError("error.generic.nolocalplus1",
										l1));
					}
				}
			} else {
				logerror("No ActionErrors passed in to generate error message!");
				logerror(e);
			}
		} else {
			logerror("No Exception passed in to generate error message!");
			e = new Exception("No Exception provided ... here is stack trace");
			e.fillInStackTrace();
			logerror(e);
		}
	}
	
	
	private void setErrorAttributes(HttpServletRequest request, 
			HttpServletResponse response, 
			String errCode, 
			String errType, 
			String errMess) { 
		
		request.setAttribute( "dropboxErrCode",errCode); 
		request.setAttribute( "severity",errType); 
		request.setAttribute( "dropboxErrMessage",errMess); 
	}   
	
	
	private void setStandardAttributes(HttpServletRequest request, 
			HttpServletResponse response) { 
		
		String context = request.getContextPath();
		if (context == null || context.length() == 0) {
			context="";
		}
		
		// Support URL Rewriting
		//		 String url = response.encodeURL(context + 
		//										 "/servlet/oem/edge/ed/odc/webdropbox");
		
		String url = response.encodeURL(context+"/WebDropboxActions.do");  //TBDel
		request.setAttribute("dboxhref", url);
		request.setAttribute("context", context);
		
		// Support URL Rewriting
		//		 String feURL = 
		//			oem.edge.ed.odc.cntl.DesktopServlet.getDesktopProperty("edodc.feURL");
		//		 String feEdesignServlet = 
		//			oem.edge.ed.odc.cntl.DesktopServlet.getDesktopProperty("edodc.feEDServlet");
		//		 String restarturl = feURL + "/" + feEdesignServlet +
		//			"?op=7&sc=webox:op:i";
		//
		String restarturl = url+"?action=doLogin"; //TBDel
		
		request.setAttribute("restarturl", restarturl);
		// JMC Don't need this for Services code
		//request.setAttribute("webdropbox", getDropbox(request.getSession()));
		
	}   
	
	
	
	public String returnSizeInUnits(long value) {
		
		// Break down value to x.yy KB, MB or GB.
		long size = value;
		long divisor = 1024;
		String suffix = " KB";
		long whole = size / divisor;
		int fraction = 0;
		
		if (whole > 999) {
			divisor = 1048576;
			suffix = " MB";
			whole = size / divisor;
			if (whole > 999) {
				divisor = 1073741824;
				suffix = " GB";
				whole = size / divisor;
			}
		}
		
		fraction = (int) (((size - (whole * divisor)) * 100) / divisor);
		
		String myNewSize=null;
		if (fraction == 0) {
			myNewSize = new String(whole + suffix);
		}
		else if (fraction < 10) {
			myNewSize = new String(whole + ".0" + fraction + suffix);
		}
		else {
			myNewSize = new String(whole + "." + fraction + suffix);
		}
		
		return myNewSize;
	}
	
	
	/**
	 * Method getDropbox fetches the dropbox instance from the hashtable for a 
	 * logged on user.
	 *
	 * @param user
	 * @return UserDropbox
	 */
	public UserDropbox getDropbox(HttpSession session) {
		UserDropbox dropbox = (UserDropbox)session.getAttribute("webdropbox");
		//			 UserDropbox dropbox = (UserDropbox)globalHash.get(session.getId());
		
		/* JMC Services ... comment out
		 TimeoutManager mgr = TimeoutManager.getGlobalManager();
		 mgr.removeTimeout("webdbox" + session.getId());
		 if (dropbox != null) {
		 mgr.addTimeout(new oem.edge.ed.odc.webdropbox.server.WebdropboxTimeout(dropbox.getTimeout(), 
		 session.getId(), null));
		 }
		 */
		
		return dropbox;
	}
	
	public UserDropbox getValidatedDropbox(HttpSession session) 
	throws Exception {
		
		if (session == null) {
			throw new NotLoggedInException("No Session Found for user");
		}
		
		UserDropbox dropbox = getDropbox(session);
		if (dropbox == null) {
			throw new NotLoggedInException("Dropbox not found in session");
		}
		if (!dropbox.isConnected()) {
			throw new NotLoggedInException("Dropbox not connected");
		}
		if (!dropbox.isLoggedIn()) {
			throw new NotLoggedInException("Dropbox not logged in");
		}
		return dropbox;
	}
	
	
	// Add a timeout to handle inactivity cleanup
	static public void manageDropbox(HttpSession session, UserDropbox dropbox) {
		// globalHash.put(session.getId(), dropbox);
		session.setAttribute("webdropbox", dropbox);
		/* JMC Services comment out
		 TimeoutManager mgr = TimeoutManager.getGlobalManager();
		 mgr.removeTimeout("webdbox" + session.getId());
		 
		 String webdropboxTimeout =
		 getDesktopProperty("edodc.webdropboxTimeout", 
		 "600"); 
		 
		 long timeout = 600;
		 try { timeout = Long.parseLong(webdropboxTimeout); } catch(Exception ee) {}
		 timeout *= 1000;
		 mgr.addTimeout(new oem.edge.ed.odc.webdropbox.server.WebdropboxTimeout(timeout, session.getId(), null));
		 dropbox.setTimeout(timeout);
		 */
	}
	static public UserDropbox removeDropboxForId(String id) {
		/*   
		 UserDropbox dropbox = (UserDropbox)globalHash.get(id);
		 globalHash.remove(id);
		 
		 // JMC Services comment out
		  // TimeoutManager mgr = TimeoutManager.getGlobalManager();
		   // mgr.removeTimeout("webdbox" + id);
		    
		    */ 
		return null;
	}
	static public UserDropbox removeDropbox(HttpSession session) {
		//return removeDropboxForId(session.getId());
		UserDropbox ret = (UserDropbox)session.getAttribute("webdropbox");
		session.removeAttribute("webdropbox");
		return ret;
	}
	
	
	public void refreshDropboxTimeout(HttpSession session) {
		/* JMC Services comment out 
		 UserDropbox dropbox = (UserDropbox)globalHash.get(session.getId());
		 
		 TimeoutManager mgr = TimeoutManager.getGlobalManager();
		 mgr.removeTimeout("webdbox" + session.getId());
		 if (dropbox != null) {
		 mgr.addTimeout(new oem.edge.ed.odc.webdropbox.server.WebdropboxTimeout(dropbox.getTimeout(), session.getId(), null));
		 }
		 */
	}
	
	
	static ReloadingProperty getDesktopProperties() {
		// Setup for Dropbox Reports
		ReloadingProperty prop = new ReloadingProperty();
		
		// Get ODC properties
		try {
			PropertyResourceBundle AppProp = (PropertyResourceBundle)
			PropertyResourceBundle.getBundle("edesign_edodc_desktop");
			
			ConfigObject cfgobj = new ConfigObject();
			Enumeration keys = AppProp.getKeys();
			while(keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				cfgobj.setProperty(key, AppProp.getString(key));
			}
			prop.bulkLoad(cfgobj);
		} catch ( Exception e ) {
			logerror("Property file edesign_edodc_desktop not found");
		}
		return prop;
	}
	
	static protected ReloadingProperty dtprops = null;
	static String getDesktopProperty(String key) {
		return getDesktopProperty(key, null);
	}
	static String getDesktopProperty(String key, String defval) {
		String ret = defval;
		if (dtprops == null) {
			dtprops = getDesktopProperties();
		}
		
		if (dtprops != null) {
			ret = dtprops.getProperty(key, defval);
		}
		
		return ret;
	}
	
	
	class Pair {
		public Pair(Vector vv, long ee) {
			v = vv; expireval = ee;
		}
		public Vector v;
		public long expireval;
	}
	
	// JMC Utility to setup the crtInf with correct storage pool info
	Pair setupCRTInfo(UserDropbox dropbox, PoolInfo pi, 
			long expireval) throws Exception { 
		// Check trigger for default value usage
		if (pi == null) pi = dropbox.getSelectedPoolInfo();
		if (expireval == -999) {
			expireval = pi.getPoolDefaultDays();
		} else if (expireval < 0) {
			expireval = 0;
		} else if (expireval > pi.getPoolMaxDays()) {
			expireval = pi.getPoolMaxDays();
		}
		
		Vector v = new Vector();
		
		int maxday = pi.getPoolMaxDays();
		
		// Add in the days, 0-n, which make the most sense for the number of
		//  entries needed (maxdays+1 ... includes 0). expireval needs to be 
		//  represented
		
		// We allow 24 slots. Use 16 (0-15), then start jumping 
		int maxslots = 24;
		int lastv    = -1;
		int slot;
		for(slot=0; slot <= 15 && slot <= maxday; slot++) {
			v.add(String.valueOf(slot));
			lastv = slot;
		}
		
		// start adding distance as we move up in slots
		for(; slot < maxslots && lastv < maxday; slot++) {
			
			// Only use 80% of avail slot size ... will give log type effect
			int dist = ((maxday-lastv)*80)/((maxslots-slot)*100);
			
			if      (dist <= 2)  dist = 1;
			else if (dist <= 7)  dist = 5;
			else if (dist <= 12) dist = 10;
			else if (dist <= 17) dist = 15;
			else if (dist <= 22) dist = 20;
			else if (dist <= 27) dist = 25;
			else if (dist <= 32) dist = 30;
			else if (dist <= 42) dist = 40;
			else if (dist <= 52) dist = 50;
			else                 dist = 60;
			
			int cv = lastv + dist;
			
			// If dist is 1 ... take what we are given
			// ... otherwise, make it 0 5 or 10 ending
			// Exception will be to include requested expiration and max
			if (dist > 1) {
				int mod5 = cv % 5;
				if (mod5 != 0) cv += 5-mod5;
			}
			
			if (cv > maxday) cv = maxday;
			
			// Add in required expire value as a choice
			if (lastv < expireval && cv > expireval) {
				v.add(String.valueOf(expireval));
			}
			
			if (cv <= maxday) {
				v.add(String.valueOf(cv));
				lastv = cv;
				
				if (cv == maxday) break;
			} else {
				break;
			}
		}
		
		// If maxday did not make it in ... shove it in there
		if (lastv < maxday) {
			// Add in required expire value as a choice
			if (lastv < expireval && maxday > expireval) {
				v.add(String.valueOf(expireval));
			}
			v.add(String.valueOf(maxday));
		}
		
		return new Pair(v, expireval);
	}
	
	PoolInfo findStoragePool(UserDropbox dropbox, long poolid) {
		if (poolid < 0) return null;
		try {
			return dropbox.getStoragePoolInstance(poolid);
		} catch(Exception e) {
			logerror("Failed to find storage pool instance: " + poolid);
			logerror(e);
		}
		return null;
	}
	
	// JMC Utility to setup the crtInf with correct storage pool info
	void setupCRTInfo(UserDropbox dropbox,
			WebDboxCreateDraftPkgInfo crtInf, 
			long poolid, 
			long expireval) throws Exception { 
		
		Pair pair = setupCRTInfo(dropbox, findStoragePool(dropbox, poolid), expireval);
		
		crtInf.setSelectedExpireValue(String.valueOf(pair.expireval)); 
		crtInf.setExpirationValues(pair.v);
	}
	// JMC Utility to setup the crtInf with correct storage pool info
	void setupCRTInfo(UserDropbox dropbox,
			WebDboxCreateSentPkgInfo crtInf, 
			long poolid, 
			long expireval) throws Exception { 
		
		Pair pair = setupCRTInfo(dropbox, findStoragePool(dropbox, poolid), expireval);
		
		crtInf.setSelectedExpireValue(String.valueOf(pair.expireval)); 
		crtInf.setExpirationValues(pair.v);
	}
	
	/** 
	 * Processes one String, returns it as an array of fields 
	 * 
	 */
	public static String[] process(String line,String DELIM) {
		//String DELIM=",";
		
		// A Vector is an array-like container that grows dynamically;
		// see the chapter on Java Collections.
		Vector results = new Vector();
		
		// Unless you ask StringTokenizer to give you the tokens,
		// it silently discards multiple null tokens.
		StringTokenizer st = new StringTokenizer(line, DELIM, true);
		
		int i = 0;
		// Stuff each token into the Vector
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s.equals(DELIM)) {
				// Just ignore the "tokens" that consist of DELIM
				// But do insert something for consecutive delimiters
				if (results.size() < ++i)
					results.add(null);
				continue;
			}
			results.addElement(s);
		}
		// Make an Array just the right size, ask the
		// Vector to copy its data into it, and return it.
		String[] sResults = new String[results.size()];
		results.toArray(sResults);
		return sResults;
	}
	
	
	/**
	 * checkIfPackageNameValid util method verifies existence of Package on the dropbox server
	 */
	private boolean checkIfPackageNameValid(UserDropbox dropbox, String packNameStr) {
		try {
			Enumeration draftEnum=dropbox.listInOutSandBox(4);
			
			while(draftEnum.hasMoreElements()) {
				PackageInfo pinfo = (PackageInfo)draftEnum.nextElement();
				if (pinfo.getPackageName().equalsIgnoreCase(packNameStr)) {
					return true;
					
				}
			}
		} catch (Exception e) {
			
			logerror(e);
		}
		return false;
	}
	
	
	/**
	 * checkIfGroupNameValid util method verifies existence of Group on the dropbox server
	 */
	private boolean checkIfGroupNameValid(UserDropbox dropbox, String groupNameStr) {
		try {
			Hashtable hash=dropbox.listGroups();
			
			Enumeration groupEnum=hash.keys(); 
			
			while(groupEnum.hasMoreElements()) {
				String groupOnServer = (String)(groupEnum.nextElement());
				if (groupOnServer.equals(groupNameStr)) {
					return true;
					
				}
			}
		} catch (Exception e) {
			
			logerror(e);
		}
		return false;
	}
	
	
	/***
	 * This method provides the index for the Inbox package in context and returns a string containing
	 * the packagename and index as "packagenamesttart#201end*"    
	 */
	
	String getIndexForPkgIdFromSortedInboxVector(String sortedPkgID,WebDboxInboxInfo inbxInfo)
	{
		int idx=0;
		String pkgname_idx=null;
		Vector pkgVect=inbxInfo.getSortedPackages();
		
		if (pkgVect.size() > 0) {
			Enumeration enum = pkgVect.elements();
			while(enum.hasMoreElements()) {
				idx++;
				DboxPackageInfo pinfo = (DboxPackageInfo)enum.nextElement();				
				if (pinfo.getPackageId().equals(sortedPkgID)) {
					pkgname_idx = pinfo.getPackageName()+"start#"+idx+"end*";					
					break;
				}
			}
		}
		
		return pkgname_idx;
	}
	
	/***
	 * This method provides the index for the Inbox package in context and returns an index of 
	 * the pkg within the vector.
	 */
	
	int getIndexForPkgIdFromInbox(String sortedPkgID,WebDboxInboxInfo inbxInfo)
	{
		int idx=0;
		int index=0;
		Vector pkgVect=inbxInfo.getSortedPackages();
		
		if (pkgVect.size() > 0) {
			Enumeration enum = pkgVect.elements();
			while(enum.hasMoreElements()) {
				idx++;
				DboxPackageInfo pinfo = (DboxPackageInfo)enum.nextElement();				
				if (pinfo.getPackageId().equals(sortedPkgID)) {
					index = idx;					
					break;
				}
			}
		}
		
		return index;
	}
	
	
	/**
	 * This method provides the index for the Draft package in context 
	 */
	int getIndexForPkgIdFromSortedDraftVector(String sortedPkgID,WebDboxDraftPkgInfo dftInfo)
	{
		int idx=0;
		int pkgname_idx=0;
		Vector pkgVect=dftInfo.getSortedDraftPackages();
		
		if (pkgVect.size() > 0) {
			Enumeration enum = pkgVect.elements();
			while(enum.hasMoreElements()) {
				idx++;
				DboxPackageInfo pinfo = (DboxPackageInfo)enum.nextElement();				
				if (pinfo.getPackageId().equals(sortedPkgID)) {
					pkgname_idx = idx;					
					break;
				}
			}
		}
		
		return pkgname_idx;
	}
	
	
	/**
	 * This method provides the index for the Draft package in context 
	 */
	int getIndexForPkgIdFromSortedSentVector(String sortedPkgID,WebDboxSentPkgInfo sntInfo)
	{
		int idx=0;
		int pkgname_idx=0;
		Vector pkgVect=sntInfo.getSortedSentPackages();
		
		if (pkgVect.size() > 0) {
			Enumeration enum = pkgVect.elements();
			while(enum.hasMoreElements()) {
				idx++;
				DboxPackageInfo pinfo = (DboxPackageInfo)enum.nextElement();				
				if (pinfo.getPackageId().equals(sortedPkgID)) {
					pkgname_idx = idx;					
					break;
				}
			}
		}
		
		return pkgname_idx;
	}			
	
	
	/**
	 * daysDifference util method finds the difference between two dates as long
	 * 
	 */
	public static long daysDifference(Date a, Date b) {
		
		// a=new Date(a.getYear()-1900, a.getMonth()-1, a.getDate()); //timeanddate.com
		//b=new Date(b.getYear()-1900, b.getMonth()-1, b.getDate());
		
		a=new Date(a.getYear(), a.getMonth(), a.getDate()); //timeanddate.com
		b=new Date(b.getYear(), b.getMonth(), b.getDate());
		
		
		Calendar sca = Calendar.getInstance();
		sca.clear();
		sca.setTime(a);
		
		
		
		Calendar scb = Calendar.getInstance();
		scb.clear();
		scb.setTime(b);
		
		long from = sca.getTime().getTime();
		
		long to = scb.getTime().getTime();
		double difference = to - from; 
		long days = Math.round((difference/(1000*60*60*24))); 
		
		
		
		
		
		return days;
		
	}  
	
	
	/**
	 * checkIfFileAlreadyUploaded util method checks for duplicate files added to Package on the dropbox server
	 * 
	 */
	private boolean checkIfFileAlreadyUploaded(UserDropbox dropbox,long packid,String fname) {
		
		boolean stat=false;
		try {
			Enumeration enumc = dropbox.listPackageContents(packid);
			
			while(enumc.hasMoreElements()) {
				FileInfo fi = (FileInfo)enumc.nextElement();
				if (fi.getFileName().trim().equals(fname.trim())) {
					if (fi.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
						
						stat=true;				
					}if ( ( fi.getFileStatus() == DropboxGenerator.STATUS_INCOMPLETE) || ( fi.getFileStatus() == DropboxGenerator.STATUS_FAIL) || ( fi.getFileStatus() == DropboxGenerator.STATUS_PARTIAL)) {
						
						dropbox.deleteFileFromPackage(packid, fi.getFileId());
						stat=false;
					}
				}
			}	
		} catch (Exception e) {
			
			//e.printStackTrace();
			
		}
		
		
		return stat;
	}
	
	private void setItarAttributes(HttpServletRequest request, 
			HttpServletResponse response,UserDropbox dropbox) { 
		
		if ( dropbox.isItarEntitled())
			request.setAttribute("itarEntitled","true");
		else
			request.setAttribute("itarEntitled","false");
		
		if ( dropbox.isItarSessionCertified())
			request.setAttribute("itarSessionCert","true");
		else
			request.setAttribute("itarSessionCert","false");
		
	
	}
	
	
	/**
	 * 
	 * The doLogin Action method creates a new session, disconnects any old dropbox instances and establishes
	 * the new connection to the dropbox server at hostname:port. The dropbox instances are maintained in a 
	 * global hash using the session ids as keys for uniqueness.This Action also handles Pre-populating of 
	 * webInboxInfo Bean which is important when  a user clicks on the link in an email message, to download 
	 * the package.This would cause the Inbox WebPage to load showing the highlighted Package and its contents.	 
	 *   
	 */
	public ActionForward doLogin(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception {
		
		String user=null;
		String password=null;
		String token = null;
		
		UserDropbox dropbox = null;
		
		HttpSession session = request.getSession(true);
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward();
		
		
		WebDboxInboxForm testData = (WebDboxInboxForm) form;
		try {
			
			setStandardAttributes(request,response);
			
			
			// Get the Token value for login
			token=(String)request.getParameter("compname");
			
			
			if(token == null) {
				
				
				errors.add("error.login.loginfailed",new ActionError("error.login.loginfailed"));
			}
			
			
		} catch (Exception e) {
			
			// Report the error using the appropriate name and ID.
			errors.add("error.login.loginfailed",new ActionError("error.login.loginfailed"));
			
			logerror("WebDropbox: Error during login");
			logerror(e);
			createErrorFromException("Error during login process", e, errors);
			throw e;
		}
		
		
		try {
			
			// Get any OLD dropbox ... 
			UserDropbox olddropbox = getDropbox(session);
			
			// ... and disconnect it
			if (olddropbox != null) olddropbox.disconnect();
			
			
		} catch (Exception e){
			// Report the error using the appropriate name and ID.
			errors.add("error.login.loginfailed",new ActionError("error.login.loginfailed"));
			logerror(e);
		}
		
		dropbox = new UserDropbox();
		
		
		
		request.setAttribute("webdropbox", dropbox);
		
		
		//provide me a DSMPSocketHandler
		String ports = getDesktopProperty("edodc.xfrPort");
		String host = getDesktopProperty("edodc.xfrHost");
		
		
		int port = -1;
		
		try {
			
			port = Integer.parseInt(ports);
			
		} catch (Exception ee) {
			
		}
		
		// JMC Change to use Services
		//dropbox.connect(host, port);
		
		// Using NULL will cause DIRECT ... but package
		//  download does not work that way :-(
		// dropbox.connect(null);
		//
		//  BIIiing ... now Direct supports package 
		//    download as well
		//
		// Pass the top URL for this server.
		//  TODO should read from prop file
		//
		String topurl = request.getScheme() + "://" + 
		request.getServerName() +
		':' + request.getServerPort() +
		request.getContextPath();
		dropbox.connect(null /*topurl*/);
		
		log("\n\n TOPURL = " +topurl+"\n\n");
		
		boolean loginSuccess = dropbox.login(token);
		
		
		
		
		if (loginSuccess == false) {
			try {
				dropbox.disconnect();
			} catch(Exception ee) {}
			
			
			
			errors.add("error.login.loginfailed",new ActionError("error.login.loginfailed"));		
		} else {
			
			manageDropbox(session, dropbox);
			
			
			setDefaultUserOptions(dropbox,request,response);	
			
			
			
			WebDboxDraftPkgInfo dfinf = new WebDboxDraftPkgInfo();	
			request.getSession(false).setAttribute("webDraftPkgInfo",dfinf);
			
			
			
			// Support start viewing a particular package
			PackageInfo pinfo = null;
			String pkgid=(String)( request.getParameter("pkgid"));
			
			
			if (pkgid != null) {
				try {
					pinfo = dropbox.queryPackage(Long.parseLong(pkgid));
				} catch(Exception eee) {							  
					//errors.add("error.inbox.severe",new ActionError("error.inbox.severe"));	
					createErrorFromException("Error while searching for specified package", eee, errors); 
					logerror(eee);
				}
			}
			
			
			if (pinfo != null) {
				
				int totalRcvdPkgs=0;
				int start=0;
				int end=0;	
				int pkgIndex=0;
				String pkgName=null;
				String pkgName_index=null;							
				
				
				
				WebDboxInboxInfo inbxInfo = new WebDboxInboxInfo();
				totalRcvdPkgs=dropbox.retreiveInboxContents(inbxInfo);
				dropbox.sortPkgInfo(ETSComparator.getSortOrder(ETSComparator.SORT_BY_PKG_DATE_COMMIT_STR),ETSComparator.SORT_DESC,inbxInfo);
				
				inbxInfo.setUser((String)(dropbox.getTokenUserCompany(true)));
				inbxInfo.setCompany((String)(dropbox.getTokenUserCompany(false)));
				
				
				pkgName=getInboxPkgNameForId(pkgid,dropbox);
				pkgIndex=getIndexForPkgIdFromInbox(pkgid,inbxInfo);
				
				
				
				int view=0;
				int remainder = pkgIndex%6;
				if (remainder==0) {
					view=pkgIndex/6 ;
				}else{
					view=(pkgIndex/6)+1;
				}
				
				
				start=(view*6-6)+1;
				end=(view*6 <= totalRcvdPkgs ? view*6 : totalRcvdPkgs);
				
				inbxInfo.setStartIndex(start);
				inbxInfo.setEndIndex(end);
				inbxInfo.setTotRcvdPkgs(totalRcvdPkgs);
				
				inbxInfo.setPackageRangeForDisplay(start,end);
				inbxInfo.setSelectdPkg(pkgName);
				
				inbxInfo.setSelectdPkgWithPkgId(pkgid);
				testData.setHighlitPkg(pkgName);
				testData.setInboxPkgToLit(pkgid);
				
				
				Vector 	fileData = new Vector();
				
				
				
				dropbox.setInboxInfoBean(inbxInfo);
				dropbox.setFirstTime(false);
				
				fileData = (Vector)(dropbox.getInboxInfoBean().getFileDescriptorsForPkgId(Long.parseLong(pkgid)));
				dropbox.getInboxInfoBean().setInboxFileContents(fileData);
				
				//store in webpage as attribute besides other attributes
				session.setAttribute("webInboxInfo",inbxInfo);
				request.setAttribute("webInboxInfo",inbxInfo);
				
				
				
				return mapping.findForward("InboxSuccess");
			}
			
			
			
			
			doInbox(mapping,form,request, response);
			
			
		}	
		
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			forward = mapping.findForward("InboxFailure");
			
			
		} else {
			
			forward = mapping.findForward("InboxSuccess");
		}
		
		// Finish with
		return (forward); 
		
		
		
	}
	
	
	
	
	/**
	 * @param dropbox
	 */
	private void setDefaultUserOptions(UserDropbox dropbox,HttpServletRequest request,HttpServletResponse response) {
		
		
		
		
		boolean itarCertified= false;
		boolean SendNotificationDefault=false;
		boolean ReturnReceiptDefault=false;
		boolean NewPackageEmailNotification=false;
		boolean NagNotification=false;
		
		
		try {
			Map options = dropbox.getOptions();
			
			if (options != null) {
				String option = (String) options.get(DropboxGenerator.ItarCertified);
				itarCertified = option != null && option.equalsIgnoreCase("TRUE");
				
				option = (String) options.get(DropboxGenerator.SendNotificationDefault);
				SendNotificationDefault = option != null && option.equalsIgnoreCase("TRUE");
				
				option = (String) options.get(DropboxGenerator.ReturnReceiptDefault);
				ReturnReceiptDefault = option != null && option.equalsIgnoreCase("TRUE");
				
				option = (String) options.get(DropboxGenerator.NewPackageEmailNotification);
				NewPackageEmailNotification = option != null && option.equalsIgnoreCase("TRUE");
				
				option = (String) options.get(DropboxGenerator.NagNotification);
				NagNotification = option != null && option.equalsIgnoreCase("TRUE");
				
			}
			
			dropbox.setItarEntitled(itarCertified);
			dropbox.setSendNotificationDefault(SendNotificationDefault);
			dropbox.setReturnReceiptDefault(ReturnReceiptDefault);
			dropbox.setNewPackageEmailNotification(NewPackageEmailNotification);
			dropbox.setNagNotification(NagNotification);
			
			setItarAttributes(request,response,dropbox);
			
		} catch(Exception e) {
			
			logerror(e);
		}
		
	}
	
	/**
	 * The doLogout Action Method disconnects the dropbox instance from the dropbox server.It invalidates
	 * the stored dropbox instance in the Global Hash.
	 *
	 */
	
	public ActionForward doLogout(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception {
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		
		
		
		try {
			
			
			setStandardAttributes(request,response);
			
			// Remove dropbox object 
			UserDropbox dropbox = WebDropboxActions.removeDropbox(request.getSession());
			
			// Send Shutdown event
			dropbox.disconnect();
			
			
		}
		catch(Exception ee) {
			
			
			errors.add("error.logout",new ActionError("error.logout"));		
			logerror(ee);
			
		}
		
		
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			forward = mapping.findForward("LogoutFailure");
			
			
		} else {
			
			forward = mapping.findForward("LogoutSuccess");
		}
		
		
		
		return (forward);
		
	}
	
	
	
	
	
	/**
	 * 
	 * The doInbox Action sends a request to the dropbox server to fetch the Inbox Contents.
	 * Population of webInboxInfo Bean is also done.
	 *  	
	 */
	
	public ActionForward doInbox(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		
		WebDboxInboxForm inbxFormData = (WebDboxInboxForm) form;
		
		
		String fieldname  = inbxFormData.getModifyFieldName();
		String fieldvalue = inbxFormData.getModifyFieldValue();
		
		
		
		
		
		
		boolean loggedInException=false;
		
		try {
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			
			setItarAttributes(request,response,dropbox);
			
			//fieldvalue stores the id..do a lookup and fetch the draft pkgname
			String inboxPkgName=getInboxPkgNameForId(fieldvalue,dropbox); //do a lookup for the pkgname
			
			
			if ( ! fieldvalue.equals(null) )
			{
				inbxFormData.setHighlitPkg(inboxPkgName);
				inbxFormData.setInboxPkgToLit(fieldvalue);
			}   
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && s.indexOf(":refresh:") >= 0) {
					dropbox.setFirstTime(true);
					inbxFormData.setHighlitPkg("NoPackageSelected");
					inbxFormData.setInboxPkgToLit("NoPackageSelected");
				}
			}
			
			
			
			
			if ( dropbox.isFirstTime())
			{ 							
				
				int totalRcvdPkgs=0;
				int start=0;
				int end=0;				
				
				WebDboxInboxInfo inbxInfo = new WebDboxInboxInfo();
				totalRcvdPkgs=dropbox.retreiveInboxContents(inbxInfo);
				dropbox.sortPkgInfo(ETSComparator.getSortOrder(ETSComparator.SORT_BY_PKG_DATE_COMMIT_STR),ETSComparator.SORT_DESC,inbxInfo);
				
				inbxInfo.setUser((String)(dropbox.getTokenUserCompany(true)));
				inbxInfo.setCompany((String)(dropbox.getTokenUserCompany(false)));
				
				WebDboxDraftPkgInfo dfinf = new WebDboxDraftPkgInfo();	
				request.getSession(false).setAttribute("webDraftPkgInfo",dfinf);
				
				if ( totalRcvdPkgs > 6 )
				{   
					//set indices for more than 6 pkgs
					start=1;
					end=6;
					
				}else { 
					//set indices for less than 6 pkgs
					start=1;	
					end=totalRcvdPkgs;								 
				}
				inbxInfo.setStartIndex(start);
				inbxInfo.setEndIndex(end);
				inbxInfo.setTotRcvdPkgs(totalRcvdPkgs);
				
				inbxInfo.setPackageRangeForDisplay(start,end);
				dropbox.setInboxInfoBean(inbxInfo); //inbxInfo now has indices,sortedpkgs,unsortedpkgs
				
				
				String selectPkg=inbxFormData.getHighlitPkg().trim();
				
				if ( selectPkg.equals("NoPackageSelected"))
				{
					
					dropbox.getInboxInfoBean().setSelectdPkg(selectPkg);	
					session.setAttribute("selPkgInfo","NoPackageSelected"); 
					request.setAttribute("selPkgInfo","NoPackageSelected");  	
					
				}
				
				//store in webpage as attribute besides other attributes
				session.setAttribute("webInboxInfo",inbxInfo);
				request.setAttribute("webInboxInfo",inbxInfo);
				dropbox.setFirstTime(false);
				
			}else
			{
				
				
				
				
				//This done if a Pkg was selected...we need the show Contents of Package then
				if (inbxFormData.getHighlitPkg() != null )	
				{	
					String selectPkg=inbxFormData.getHighlitPkg().trim();
					
					
					if ( selectPkg.equals("NoPackageSelected"))
					{
						dropbox.getInboxInfoBean().setSelectdPkg("NoPackageSelected");	
						dropbox.getInboxInfoBean().setSelectdPkgWithPkgId("NoPackageSelected");	
						session.setAttribute("selPkgInfo","NoPackageSelected"); 
						request.setAttribute("selPkgInfo","NoPackageSelected");	
						
						
						String selectFile=inbxFormData.getUploadFile().trim();
						
						
					}else
					{
						
						String selectFile=inbxFormData.getUploadFile().trim();									
						
						long PkgId = Long.parseLong(fieldvalue);
						
						Vector fileData = (Vector)(dropbox.getInboxInfoBean().getFileDescriptorsForPkgId(PkgId));
						dropbox.getInboxInfoBean().setInboxFileContents(fileData);
						
						dropbox.getInboxInfoBean().setSelectdPkgWithPkgId(fieldvalue);									
						dropbox.getInboxInfoBean().setSelectdPkg(inboxPkgName);									
						
						session.setAttribute("selPkgInfo",selectPkg); 
						request.setAttribute("selPkgInfo",selectPkg);  	
					}
					
				}
				
				
				
			}
			
			dropbox.setDraftFirstTime(true); //Added for CSRFIX(IBMCC00009471) drafts empty Prob..
			dropbox.setTrashFirstTime(true); 
			
			
			
			
		}catch( NotLoggedInException nle)
		{							
			loggedInException=true;						
			
			
		}	
		catch (Exception e)
		{
			
			createErrorFromException("Error while handling Inbox Panel", e, errors); 
			logerror(e);
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("InboxFailure");
			
			
		} else {
			
			forward = mapping.findForward("InboxSuccess");
		}
		
		
		return (forward);
		
		
	}
	
	
	
	
	
	private String getInboxPkgNameForId(String packId, UserDropbox dropbox) {
		String ret = "NoPackageSelected";
		Enumeration inboxEnum=null;
		
		
		if ( ! packId.equals(ret)) {
			try {
				inboxEnum=dropbox.listInOutSandBox(2); //query inbox
				while(inboxEnum.hasMoreElements()) {
					
				   PackageInfo pkginfo = (PackageInfo)(inboxEnum.nextElement());
					if (Long.toString(pkginfo.getPackageId()).equals(packId)) {
						ret = pkginfo.getPackageName();
						break;
					}
				}
			} catch (Exception e) {
				
				//e.printStackTrace();
			}
		}
		return ret;
	}
	
	public String getDraftPkgNameForId(String packId,UserDropbox dropbox) {
		String ret = "NoPackageSelected";
		Enumeration draftsEnum=null;
		
		
		if ( ! packId.equals(ret)) {
			try {
				draftsEnum=dropbox.listInOutSandBox(4); //query drafts
				while(draftsEnum.hasMoreElements()) {
					
					PackageInfo pkginfo = (PackageInfo)(draftsEnum.nextElement());
					if (Long.toString(pkginfo.getPackageId()).equals(packId)) {
						ret = pkginfo.getPackageName();
						break;
					}
				}
			} catch (Exception e) {
				
				//e.printStackTrace();
			}
		}
		return ret;
	}	
	
	
	
	
	
	/**
	 * 
	 * The doDrafts Action sends a request to the dropbox server to fetch the draft Contents.
	 * Population of webDraftPkgInfo Bean is also done.
	 *  	
	 */
	
	public ActionForward doDrafts(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		
		WebDboxDraftPkgForm dftFormData = (WebDboxDraftPkgForm) form;
		boolean loggedInException = false;
		
		String fileDeleteOperation=(String)(dftFormData.getFileDelOper().trim()); // delete a file from draft
		
		String fieldname  = dftFormData.getModifyFieldName().trim(); // get String "Package Name"
		String fieldvalue = dftFormData.getModifyFieldValue().trim();   // get "pkgid"				
		
		
		
		try 
		{
			
			
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			//fieldvalue stores the id..do a lookup and fetch the draft pkgname
			String draftPkgName=getDraftPkgNameForId(fieldvalue,dropbox); //do a lookup for the pkgname
			
			
			
			if ( ! fieldvalue.equals("NoPackageSelected") )
			{
				dftFormData.setHighlitDraftPkg(draftPkgName); //will store the pkgname
				dftFormData.setDraftPkgToLit(fieldvalue); //will store the pkgid ..used for highlighting in JScript
			}else
			{
				
				dftFormData.setHighlitDraftPkg("NoPackageSelected");
				dftFormData.setDraftPkgToLit("NoPackageSelected"); 
			}    	
			
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && s.indexOf(":refresh:") >= 0) { //Section called by action mthd "WebDboxDraftPkgRefresh"
					
					dropbox.setDraftFirstTime(true);
					dftFormData.setHighlitDraftPkg("NoPackageSelected");
					dftFormData.setDraftPkgToLit("NoPackageSelected");              
					
					//Intialisations for the Create Package Page.
					WebDboxCreateDraftPkgInfo crInf= new WebDboxCreateDraftPkgInfo();
					crInf.setRecipientsList(new Vector());
					crInf.setSendList(new Vector());
					crInf.generateExpirationVals();
					
					dropbox.setCreatDftInfoBean(crInf);		
					session.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
					request.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
					
					//Intialisations for the Create Groups Page.
					WebDboxCreateGroupInfo crGrpInf= new WebDboxCreateGroupInfo();
					crGrpInf.setRecipientsList(new Vector());
					crGrpInf.setSendList(new Vector());
					
					dropbox.setCreateGroupInfoBean(crGrpInf);		
					session.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
					request.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
					
					
				}							
				
				
			}
			
			if ( dropbox.isCreateDraftFirstTime())
			{ 
				//Intialisations for the Create Package Page.
				WebDboxCreateDraftPkgInfo crInf= new WebDboxCreateDraftPkgInfo();
				crInf.setRecipientsList(new Vector());
				crInf.setSendList(new Vector());
				crInf.generateExpirationVals();
				
				crInf.setDisablePkgNameField(false); //TBDEL
				
				
				dropbox.setCreatDftInfoBean(crInf);		
				session.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
				request.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
				
				//Intialisations for the Create Group Page.						
				WebDboxCreateGroupInfo crGrpInf= new WebDboxCreateGroupInfo();
				crGrpInf.setRecipientsList(new Vector());
				crGrpInf.setSendList(new Vector());
				
				
				dropbox.setCreateGroupInfoBean(crGrpInf);		
				session.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
				request.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
				
				dropbox.setCreateDraftFirstTime(false);
			}
			
			if ( dropbox.isDraftFirstTime())
			{ 							
				
				
				int totalDraftPkgs=0;
				int start=0;
				int end=0;	
				
				//set up the draft bean
				WebDboxDraftPkgInfo dftInfo = new WebDboxDraftPkgInfo();
				totalDraftPkgs=dropbox.retreiveDraftContents(dftInfo);
				
				dropbox.sortDraftPkgInfo(ETSComparator.getSortOrder(ETSComparator.SORT_BY_PKG_DATE_COMMIT_STR),ETSComparator.SORT_DESC,dftInfo);
				
				if ( totalDraftPkgs > 6 )
				{   
					//set indices for more than 6 pkgs
					start=1;
					end=6;
					
				}else { 
					//set indices for less than 6 pkgs
					start=1;	
					end=totalDraftPkgs;								 
				}
				dftInfo.setStartDraftIndex(start);
				dftInfo.setEndDraftIndex(end);
				dftInfo.setTotDraftdPkgs(totalDraftPkgs);
				
				dftInfo.setPackageRangeForDisplay(start,end);
				dropbox.setDraftInfoBean(dftInfo); //dftInfo now has indices,sortedpkgs,unsortedpkgs
				
				
				
				String selectPkg=dftFormData.getHighlitDraftPkg().trim();
				
				if ( selectPkg.equals("NoPackageSelected"))
				{
					
					dropbox.getDraftInfoBean().setSelectdDraftPkg(selectPkg);
					
					dropbox.getDraftInfoBean().setSelectdPkgWithPkgId(fieldvalue); 
					dropbox.getDraftInfoBean().setSelectdDraftPkg(draftPkgName);
					
					
				}
				
				
				
				//store in webpage as attribute besides other attributes
				session.setAttribute("webDraftPkgInfo",dftInfo);
				request.setAttribute("webDraftPkgInfo",dftInfo);
				dropbox.setDraftFirstTime(false);
				
			}else
			{
				
				
				
				//This done if a Pkg was selected...we need the show Contents of Package then
				if (dftFormData.getHighlitDraftPkg() != null )	
				{	
					String selectPkg=dftFormData.getHighlitDraftPkg().trim();
					
					if ( selectPkg.equals("NoPackageSelected"))
					{
						dropbox.getDraftInfoBean().setSelectdDraftPkg("NoPackageSelected");
						dropbox.getDraftInfoBean().setSelectdPkgWithPkgId("NoPackageSelected");
						
					}else
					{
						
						long PkgId = Long.parseLong(fieldvalue);
						
						Vector fileData = (Vector)(dropbox.getDraftInfoBean().getFileDescriptorsForPkgId(PkgId));
						dropbox.getDraftInfoBean().setDraftFileContents(fileData);
						dropbox.getDraftInfoBean().setDraftFileContentsLen(fileData.size());
						
						dropbox.getDraftInfoBean().setSelectdPkgWithPkgId(fieldvalue); //save the pkgids for further 
						dropbox.getDraftInfoBean().setSelectdDraftPkg(draftPkgName);
						
					}
					
				}
				
				
				
				
			}
			
			
			
		}catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			
			createErrorFromException("Error while handling Drafts panel", e, errors); 
			logerror(e);
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftsFailure");
			
			
		} else {
			
			forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward);
		
		
	}
	
	
	
	/**
	 * The Action method doUpdateDraftPkgView remembers the last draft operation done and builds the
	 * appropriate draft webpage in context.
	 * 
	 */
	public ActionForward doUpdateDraftPkgView(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		
		WebDboxDraftPkgForm draftFormData = (WebDboxDraftPkgForm) form;
		boolean loggedInException = false;
		int totalDraftPkgs=0;
		
		
		
		try {	
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String currentDraftPkgInContext=dropbox.getDraftInfoBean().getSelectdPkgWithPkgId();													
			
			String draftPkgName=dropbox.getDraftInfoBean().getSelectdDraftPkg();                      
			
			WebDboxDraftPkgInfo dftInfo = new WebDboxDraftPkgInfo();
			
			dftInfo.setSelectdDraftPkg(draftPkgName);
			dftInfo.setSelectdPkgWithPkgId(currentDraftPkgInContext);
			
			
			if (currentDraftPkgInContext == null ) {														
				errors.add("error.drafts.severe",new ActionError("error.drafts.severe"));
				saveErrors(request, errors);
				
				return mapping.findForward("DraftsFailure");
			}
			
			
			totalDraftPkgs=dropbox.retreiveDraftContents(dftInfo);
			
			
			dftInfo.setTotDraftdPkgs(totalDraftPkgs);
			dropbox.sortDraftPkgInfo(ETSComparator.getSortOrder(ETSComparator.SORT_BY_PKG_DATE_COMMIT_STR),ETSComparator.SORT_DESC,dftInfo);
			
			
			
			long PkgId=Long.parseLong(currentDraftPkgInContext);
			
			draftFormData.setDraftPkgToLit(currentDraftPkgInContext);//replaces alls spaces in long names with _
			
			dftInfo.setSelectdDraftPkg(draftPkgName);
			
			
			
			Vector fileData = (Vector)(dftInfo.getFileDescriptorsForPkgId(PkgId));
			dftInfo.setDraftFileContents(fileData);
			dftInfo.setDraftFileContentsLen(fileData.size());												
			
			
			String pkgName_index=null;
			String pkgName=null;
			int pkgIndex=0;
			
			
			
			pkgIndex= getIndexForPkgIdFromSortedDraftVector(String.valueOf(PkgId),dftInfo);					
			
			
			int view=0;
			int start=0;
			int end=0;
			
			int remainder = pkgIndex%6;
			if (remainder==0) {
				view=pkgIndex/6 ;
			}else{
				view=(pkgIndex/6)+1;
			}
			
			
			start=(view*6-6)+1;
			end=(view*6 <= totalDraftPkgs ? view*6 : totalDraftPkgs);
			
			
			dftInfo.setStartDraftIndex(start);
			dftInfo.setEndDraftIndex(end);
			dftInfo.setTotDraftdPkgs(totalDraftPkgs);
			
			dftInfo.setPackageRangeForDisplay(start,end);
			
			
			
			
			
			dropbox.setDraftInfoBean(dftInfo); //dftInfo now has indices,sortedpkgs,unsortedpkgs,filecontents												
			
			
			session.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
			request.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
			dropbox.setDraftFirstTime(false);
			
			dropbox.setCreateDraftFirstTime(true); //TBDEL
			
			
			
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			
			
			logerror(e);
			createErrorFromException("Error updating draft panel", e, errors);
			
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}			
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftsFailure");
			
			
		} else {
			
			forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	
	
	
	
	/**
	 * The Action method doDownLoadFile performs the file download action for Inbox/drafts webpage.
	 * 
	 */
	
	public ActionForward doDownLoadFile(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		boolean loggedInException = false;
		String errorpage=null;
		
		OutputStream out = null;
		Operation operation = null;
		setStandardAttributes(request,response);	
		HttpSession session = request.getSession(false);
		UserDropbox dropbox = getValidatedDropbox(session);  
		
		
		try {
			
			
			long pkgID=0;
			String filename=null;
			
			
			
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && s.indexOf(":draftfile:") >= 0) {
					pkgID = (long)(dropbox.getDraftInfoBean().getPackageIdForPackageName(dropbox.getDraftInfoBean().getSelectdDraftPkg()));
					filename=request.getParameter(WebDboxDraftPkgForm.FILENAME);
					if (checkIfSessionCertified(dropbox,pkgID,4))
					{	
						errorpage="draft";
						throw new Exception("Can't access ITAR package data. Please certify your session in Options");
					}  
					
					
				}else if (s != null && s.indexOf(":sentfile:") >= 0) {
					
					
					pkgID=Long.parseLong(dropbox.getSentInfoBean().getSelectdPkgWithPkgId());
					filename=request.getParameter(WebDboxSentPkgForm.FILENAME);  
					if (checkIfSessionCertified(dropbox,pkgID,3))
					{	
						errorpage="sent";
						throw new Exception("Can't access ITAR package data. Please certify your session in Options");
					}
					
				}else if (s != null && s.indexOf(":trashfile:") >= 0) {
					
			  	    pkgID=Long.parseLong(dropbox.getTrashInfoBean().getSelectdPkgWithPkgId());
					filename=request.getParameter(WebDboxTrashForm.FILENAME);
					if (checkIfSessionCertified(dropbox,pkgID,1))
					{
						errorpage="trash";
						throw new Exception("Can't access ITAR package data. Please certify your session in Options");
					}	       	
				}
				else{
					
					pkgID= Long.parseLong(dropbox.getInboxInfoBean().getSelectdPkgWithPkgId());
					filename=request.getParameter(WebDboxInboxForm.FILENAME);	
					if (checkIfSessionCertified(dropbox,pkgID,2))
					{ 
						errorpage="inbox";
						throw new Exception("Can't access ITAR package data. Please certify your session in Options");
					}
					
				}
			}	
			
			
			
			try {
				out =  response.getOutputStream();
				Enumeration enumc = 
					dropbox.listPackageContents(pkgID);
				
				boolean foundfile = false;
				while(enumc.hasMoreElements()) {
					
					FileInfo finfo = (FileInfo)enumc.nextElement();
					
					if (finfo.getFileName().equals(filename)) {
						
						foundfile = true;
						
						// stream the whole file to the browser via an operation object
						operation = dropbox.downloadFile(pkgID, 
						finfo.getFileId());
						
						
						//determine how big this file object is.. 
						long totToXfer = operation.getToXfer();
						int sz;
						byte buf[] = new byte[32768];
						
						
						//Let the browser know Content-type being streamed 
						response.setContentType("application/download");
						
						//Let the browser know it's receiving an attachment & what the default name should be.. 
						response.setHeader("Content-Disposition","attachment;filename=\"" + 
								finfo.getFileName() + "\"");
						
						
						
						// Set content length if its 2GIG or less (setContentLen is Int!!)
						if (totToXfer >= 0 && totToXfer <= 0x7fffffff) {
							
							//Let the browser know how big this file object being streamed is 
							response.setContentLength((int)totToXfer);
						} else {
							// Set size directly ... hope this works
							//response.setHeader("Content-Length", "" + totToXfer);
							
							// No size is better
						}
						
						long to = System.currentTimeMillis() + (dropbox.getTimeout()/2);  //added for refresh timeout
						
						
						// OutputStream out = response.getOutputStream();
						while((sz=dropbox.readFileData(operation, buf, 
								0, buf.length)) > 0) {
							// Do check for 1/2 way to timeout ... if so, refresh ..//added for refresh timeout
							if (System.currentTimeMillis() > to) {
								refreshDropboxTimeout(session);
								to = System.currentTimeMillis() + (dropbox.getTimeout()/2);
							}
							
							//pass every byte to servlet output stream   
							out.write(buf, 0, sz);
						}
						
						
						out.flush();
						out.close();
						out=null;  
						
						break;
					}
				}
				
				if (!foundfile) {
					
				}
			} catch (IOException ioe) {									
				
				//DebugPrint.printlnd(DebugPrint.ERROR, "Broken IO detected...");
				//DebugPrint.printlnd(DebugPrint.ERROR, ioe);		
				//	Make sure operation is closed
				try {
					if (operation != null) {			
						
						boolean success=dropbox.closeOperation(operation);
						
						out.flush();
						out.close();
						out=null; 
						//return mapping.findForward("SaveFileSuccess");
						
					}
				} catch(Exception ee) 
				{	
					//logerror(ee);					
				}      
				
				
				
			}
			
			catch (Exception e) {
				logerror("Error doing file download");
				logerror(e);
				
				throw e;
			} 
			finally {
				
				
				// Make sure operation is closed
				try {
					
					if (out != null)
					{
						out.flush();
						out.close();
						out=null;
						
					}
					
					if (operation != null) {				   	
						boolean success=dropbox.closeOperation(operation);
						
					}
				} catch(Exception ee) 
				{	
					//ee.printStackTrace();
				}      
			}
			
			
		}catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		} catch (Exception e)
		{
			logerror(e);
			createErrorFromException("Error doing file download",e,errors);
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {								
			saveErrors(request, errors);
			
			
			if ( errorpage.equalsIgnoreCase("inbox"))
				forward = mapping.findForward("InboxfileFailure");
			else if ( errorpage.equalsIgnoreCase("draft"))
				forward = mapping.findForward("DraftsfileFailure");
			else if ( errorpage.equalsIgnoreCase("sent"))
				forward = mapping.findForward("SentsfileFailure");
			else  if ( errorpage.equalsIgnoreCase("trash"))
				forward = mapping.findForward("TrashfileFailure");
			
			
		} else {
			
			forward = mapping.findForward("success");
		}
		
		
		
		return (forward); 
	}
	
	
	/**
	 * The Action method doDownLoadPackage performs the Package download action for Inbox/drafts webpage.
	 * 
	 */
	
	
	public ActionForward doDownLoadPackage(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		String encoding = null;
		boolean loggedInException = false;
		OutputStream out = null;
		
		try {
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && (( s.indexOf(":tar:") >= 0) || ( s.indexOf(":zip:") >= 0)) || ( s.indexOf(":tgz:") >= 0) ) {
					int idx = s.indexOf(":");
					if (idx >= 0) 
						encoding =  (s.substring(idx+1, idx+4)).trim();
					
				}
			}
			
			
			long pkgID=Long.parseLong(dropbox.getInboxInfoBean().getSelectdPkgWithPkgId());			
			String currentPkgName=getInboxPkgNameForId(String.valueOf(pkgID),dropbox);
			
			if (checkIfSessionCertified(dropbox,currentPkgName,2))
				throw new Exception("Can't access ITAR package data. Please certify your session in Options");
			
			
			Operation operation = null;
			try {
				
				operation = dropbox.downloadPackage((long)pkgID,(String)encoding);
				
				
				String PackageNameExtn = currentPkgName+"."+encoding;
				
				
				
				long totToXfer = operation.getToXfer();
				
				
				int sz;
				byte buf[] = new byte[32768]; 
				
				response.setContentType("application/download");
				response.setHeader("Content-Disposition","attachment;filename=\"" + 
						PackageNameExtn + "\"");
				
				
				long bytesDownloaded=0;  					
				
				long to = System.currentTimeMillis() + (dropbox.getTimeout()/2);  //added for refresh timeout
				
				out = response.getOutputStream();
				
				while((sz=dropbox.readFileData(operation, buf, 0, buf.length)) > 0) {
					
					// Do check for 1/2 way to timeout ... if so, refresh ..//added for refresh timeout
					if (System.currentTimeMillis() > to) {
						refreshDropboxTimeout(session);
						to = System.currentTimeMillis() + (dropbox.getTimeout()/2);
					}	
					
					out.write(buf, 0, sz);
					
					bytesDownloaded=bytesDownloaded+sz;
					
				}
			} finally {
				// Make sure operation is closed
				try {
					if (operation != null) {
						boolean successOperation=dropbox.closeOperation(operation);
						
					}
					if ( out != null)
					{
						response.flushBuffer();
						out.flush();
						out.close();
						out=null;
					}
				} catch(Exception ee) {}
			}
			
			
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			
			logerror(e);
			createErrorFromException("Error while downloading package ", e, errors);
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("InboxFailure");
			
			
		} else {
			
			forward = mapping.findForward("InboxSuccess");
		}
		
		
		return (forward); 
	}
	
	
	/**
	 * The Action method doFwdCreatePackage performs Action Chaining/pre-seeding for the doCreatePackage Action.
	 * 
	 */
	
	public ActionForward doFwdCreatePackage(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		WebDboxCreateDraftPkgForm dftFormData = (WebDboxCreateDraftPkgForm) form;
		boolean doEditRecipt = false;
		boolean loggedInException = false;
		
		
		
		
		
		try {
			
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			setItarAttributes(request,response,dropbox);
			
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && s.indexOf(":editRcpt:") >= 0) {
					doEditRecipt=true;
				}
			}		
			
			
			if (doEditRecipt == true){
				dftFormData.setPackageName("TestPackage");	
				
			}
			
			WebDboxCreateDraftPkgInfo crtInf = dropbox.getCreatDftInfoBean();
			
			crtInf.setValSessItar(String.valueOf(dropbox.isItarSessionCertified())); 
			
			Hashtable hash = new Hashtable();
			hash = dropbox.listGroups();
			
			
			
			
			Enumeration enum = hash.keys();
			while(enum.hasMoreElements()) {
				WebDboxAclInfo acinf = new WebDboxAclInfo();
				String groupname=(String)(enum.nextElement());
				
				acinf.setAclName(SearchEtc.htmlEscape(groupname)); // groupencode
				acinf.setAclType("[GROUP]");
				if (!(crtInf.getRecipientsList().contains(acinf)))
					crtInf.getRecipientsList().add(acinf);		
			}
			
			
			if ( doEditRecipt == false )
			{	
				crtInf.setSendList(new Vector());
				crtInf.setDisablePkgNameField(false); //enable text editing for Package name
				crtInf.setEditPackageRcpt(false);    //disable edit recipients mode for use in doCreatePkg
				crtInf.setCrossRcptShow("noshowrcpts");				
				
				// JMC storage pools
				//crtInf.setSelectedExpireValue("14");
				setupCRTInfo(dropbox, crtInf, -1, -999);
			}  
			
			
			if ( dropbox.isItarEntitled() == true && doEditRecipt == false ) //for enabling& disabling itar data checkbx
			{	crtInf.setContainsItarDataCbx(true);
			dftFormData.setContainsItarCbx(false);
			}
			else
			{
				crtInf.setContainsItarDataCbx(false);
				dftFormData.setContainsItarCbx(false);
			}

			if (  doEditRecipt == false ) //for enabling notifications sn & rr in create mode
			{	
			   dftFormData.setCbx1(dropbox.isSendNotificationDefault());
			   dftFormData.setCbx2(dropbox.isReturnReceiptDefault());
			   String pkgDescp="";
			    request.setAttribute("pkgDesLen",String.valueOf(pkgDescp.length()));
				int leftChar=1024-pkgDescp.length();
				request.setAttribute("pkgDesLeftChar",String.valueOf(leftChar));
			}
			
			dropbox.setCreatDftInfoBean(crtInf);
			
			WebDboxDispGroupInfo grpInfo = new WebDboxDispGroupInfo();					
			grpInfo.setGroupInfoHash(dropbox.listGroups());
			
			// The pool name is actually the pool id 
			String poolname = dftFormData.getPoolName();
			Vector pools = dropbox.queryStoragePoolInformation();
			dropbox.getCreatDftInfoBean().setStoragePools(pools);
			
			
			PoolInfo pi =dropbox.getStoragePoolInstance(0);
			dropbox.setSelectedPoolInfo(pi);
			dropbox.getCreatDftInfoBean().setSelectedStoragePool(pi);
			
			String expire=dropbox.prepareExpiration(pi.getPoolMaxDays(),pi.getPoolDefaultDays());
			dropbox.getCreatDftInfoBean().setPkgExpireDate("Expires on :"+expire);
			
			
		
			
			dftFormData.setPkgDesc("");
			dftFormData.setCrossRcptWarning("noshowrcpts");
			
			dropbox.setGroupInfoBean(grpInfo); 												
			
			//store in webpage as attribute besides other attributes
			session.setAttribute("webGrpDispInfo",dropbox.getGroupInfoBean());
			request.setAttribute("webGrpDispInfo",dropbox.getGroupInfoBean());
			
			
			
			
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			logerror(e);
			createErrorFromException(doEditRecipt?"Error while editing package recipients":"Error while creating package", e, errors);
			
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftsFailure");
			
			
		} else {
			
			if ( doEditRecipt == true )
				forward = mapping.findForward("RcptSuccess");
			else
				forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward); 
	}
	
	/**
	 * The Action method doRefreshCrtPackage performs Action of creating a new package.
	 * besides updating acl stuff
	 * 
	 */
	
	public ActionForward doRefreshCrtPackage(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		WebDboxCreateDraftPkgForm dftFormData = (WebDboxCreateDraftPkgForm) form;
		String encoding = null;
		String addFiles = null;
		boolean loggedInException = false;
		
		try {
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String poolreselected = ((String)(dftFormData.getPoolReselect())).trim();
			String itarSessionCert= ((String)(dftFormData.getValidItarSession())).trim();
			
			String itarSel=((String)(dftFormData.getItarSelect())).trim();
			
			
			if (poolreselected.equalsIgnoreCase("poolreselected") || ( itarSel.equalsIgnoreCase("selected") && (itarSessionCert.equalsIgnoreCase("true") || itarSessionCert.equalsIgnoreCase("false"))))
			{
				refreshCreatePkgPage(mapping,form,request,response,errors,forward,dftFormData,dropbox,session,itarSessionCert);
				return mapping.findForward("RefreshFailure");
			}
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true; 
			
		}	
		catch (Exception e)
		{
			
			
			logerror(e);
			
			errors.add("error.drafts.severe",new ActionError("error.drafts.severe"));				
			createErrorFromException("Error while handling Draft Panel action", e, errors);
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("RefreshFailure");
			
			
		} else {
			
			forward = mapping.findForward("RefreshSuccess");
		}
		return (forward); 
	}
	
	
	
	/**
	 * The Action method doCreatePackage performs Action of creating a new package.
	 * besides updating acl stuff
	 * 
	 */
	
	public ActionForward doCreatePackage(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		WebDboxCreateDraftPkgForm dftFormData = (WebDboxCreateDraftPkgForm) form;
		String encoding = null;
		String addFiles = null;
		boolean loggedInException = false;
		boolean showCrossCompWarning=false;
		
		try {
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String poolreselected = ((String)(dftFormData.getPoolReselect())).trim();
			String itarSessionCert= ((String)(dftFormData.getValidItarSession())).trim();
			String itarSel=((String)(dftFormData.getItarSelect())).trim();
			
			
			
			String packNameStr=((String)(dftFormData.getPackageName())).trim();
			
			
			boolean itarpackage=dftFormData.isContainsItarCbx();
			
			//for creating itar pkgs only
			if (itarpackage && !( dropbox.getCreatDftInfoBean().isEditPackageRcpt())){
				packNameStr="ITAR_"+packNameStr;									    	
			}
			
			//need to set the highlighted pkg context..case where create and add files is done
			//getSelectdDraftPkg will have a value of the sel pkg.
			dropbox.getDraftInfoBean().setSelectdDraftPkg(packNameStr);
			session.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
			request.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
			
			
			String iccIdStr=((String)(dftFormData.getIccid()));
			
			String accList=((String)(dftFormData.getAclList()));
			
			
			String accListRcpt=((String)(dftFormData.getAclListRcpt()));
			
			if(accList.equalsIgnoreCase("empty") || accListRcpt.equalsIgnoreCase("empty") )
			{
				//if user was editing recipients and deleted em all and clickd submit..
				//options.js::updateaclfield sets fields to str empty..need to check if
				//the bean still supplies the sendtolist with acl info..
				
				WebDboxCreateDraftPkgInfo crtInf = (WebDboxCreateDraftPkgInfo)(dropbox.getCreatDftInfoBean());											
				
				crtInf.setSendList(new Vector());
				dropbox.setCreatDftInfoBean(crtInf);
				
				
				//store in webpage as attribute besides other attributes
				session.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
				request.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
				
				
				
				errors.add("aclList", new ActionError("error.aclList.value"));
				saveErrors(request, errors);
				return mapping.findForward("DraftsFailure"); 
				
			}
			
			/////////////////validate groups //////////////////
			String[] strArrUsrGrp = process(accListRcpt,","); // we use the "  ,_U_u1,_G_g1.."
			
			String usrGrpRcpt=null;
			Hashtable grouphash = new Hashtable();
			grouphash = dropbox.listGroups();
			
			
			
			//first is a empty str. All ACL's are added from   ,_U_u1,_G_g1..list
			for(int j=1;j<strArrUsrGrp.length;j++)
			{
				
				if ( strArrUsrGrp[j].trim().indexOf("_U_") == 0 )
				{
					usrGrpRcpt=strArrUsrGrp[j].substring(3); 
				}else
				{
					boolean groupexists=false;
					usrGrpRcpt=strArrUsrGrp[j];
					
					// if acl being added is a group verify if it exists on Srv..
					Enumeration groupsenum = grouphash.keys();
					while(groupsenum.hasMoreElements()) {
						
						String groupname=(String)(groupsenum.nextElement());
						
						
						if (groupname.trim().equals(usrGrpRcpt.trim().substring(3,usrGrpRcpt.length())))
						{
							groupexists=true;
							break;   
						}
						
					}
					if (!groupexists) {
						
						errors.add("error.grouporuser.unknown",new ActionError("error.grouporuser.unknown","test","Group or User name provided does not exist or not visible.Please Retry creating a new group or user with that name"));
						saveErrors(request, errors);
						
						return mapping.findForward("DraftsFailure");
					}
					
					
					
				}
				
				
				
			}
			
			
			
			
			
			
			
			addFiles=((String)(dftFormData.getAddFiles()));
			
			
			long expireval= daysDifference(new Date(),new Date(dftFormData.getExpireDate().trim()));
			
			
			//										 Finally, the default expiration will be either the actual expiration
			// (if not 0) or defDays from now.
			if (expireval <= 0) {
				expireval= dropbox.getSelectedPoolInfo().getPoolDefaultDays();
			}
			
			
			long pkid=0;	
			long poolid=0;
			
			
			
			if ( dropbox.getCreatDftInfoBean().isEditPackageRcpt() == false )
			{	
				
				//In case the package is being created anew...
				if (checkIfPackageNameValid(dropbox,packNameStr)){
					
					errors.add("error.draft.draftexists",new ActionError("error.draft.draftexists"));
					
					saveErrors(request, errors);
					return mapping.findForward("DraftsFailure"); 
					
				}else {
					if (!dropbox.isItarSessionCertified())
						dropbox.setOption(DropboxAccess.ItarSessionCertified,"FALSE");
					else
						dropbox.setOption(DropboxAccess.ItarSessionCertified,"TRUE");
					dropbox.createPackage(((String)(dftFormData.getPackageName())).trim(),dftFormData.getPkgDesc().trim(),dropbox.getSelectedPoolInfo().getPoolId(),itarpackage?PackageInfo.ITAR:0,
							itarpackage?PackageInfo.ITAR:0);
				}
				
				
				
				Enumeration draftEnum=dropbox.listInOutSandBox(4);
				
				while(draftEnum.hasMoreElements()) {
					PackageInfo finfo = (PackageInfo)draftEnum.nextElement();
					
					if (finfo.getPackageName().equals(packNameStr)) {
						
						pkid = finfo.getPackageId();
						poolid = finfo.getPackagePoolId();
						dropbox.getDraftInfoBean().setSelectdPkgWithPkgId(Long.toString(pkid));
						break;
					}
				}
				
			}
			else
			{
				//In case the package is being edited
				
				dropbox.getCreatDftInfoBean().setEditPackageRcpt(false);
				dropbox.getCreatDftInfoBean().setDisablePkgNameField(false);
				
				Enumeration draftEnum=dropbox.listInOutSandBox(4);
				
				while(draftEnum.hasMoreElements()) {
					PackageInfo finfo = (PackageInfo)draftEnum.nextElement();
					if (finfo.getPackageName().equals(packNameStr)) {
						pkid = finfo.getPackageId();	
						poolid = finfo.getPackagePoolId();
						
						break;
					}
				}
				
				dropbox.deleteAclsForPackage(pkid);
			}
			
			
			
			//,_U_ user@in.ibm.com,_U_ u2	
			
			
			String[] strArr = process(accListRcpt,","); // we use the "  ,_U_u1,_G_g1.."
			
			
			
			String newrecipient=null;
			
			//first is a empty str. All ACL's are added from   ,_U_u1,_G_g1..list
			for(int j=1;j<strArr.length;j++)
			{
				
				if ( strArr[j].trim().indexOf("_U_") == 0 )
				{
					newrecipient=strArr[j].substring(3); 
				}else
				{
					newrecipient=strArr[j];
				}
				
				
				
				
				try {
					
					
					dropbox.addAcl(pkid,newrecipient.trim());
                                        Vector usrsVect=dropbox.lookupUser(newrecipient,true);           
				} catch (Exception e1) {																							
					
					//added as these 2 lines were re-enabling the edit modes and packname field..
					dropbox.getCreatDftInfoBean().setEditPackageRcpt(true);
					dropbox.getCreatDftInfoBean().setDisablePkgNameField(true);
					dftFormData.setAclList("");
					dftFormData.setAclListRcpt("");
					
					//errors.add("error.grouporuser.unknown",new ActionError("error.grouporuser.unknown"));
					//saveErrors(request, errors);
				    logerror(e1);
					createErrorFromException("", e1, errors);
					//return mapping.findForward("DraftsFailure");
					
					errors.add("error.grouporuser.unknown",new ActionError("error.grouporuser.unknown"));
					saveErrors(request, errors);
					return mapping.findForward("DraftsFailure"); 
				}
				
			}
			
			
			
			
			//stuff the bean with userinfo and groupinfo for the "Available Individual/Groups"
			WebDboxCreateDraftPkgInfo crtInf = (WebDboxCreateDraftPkgInfo)(dropbox.getCreatDftInfoBean());
			
			String[] sendTo = process(accList,","); // we use the " ,[USER] u1,[GROUP] g1.."
			
			
			for(int j=1;j<sendTo.length;j++)
			{
				WebDboxAclInfo acinf = new WebDboxAclInfo();
				newrecipient=(String)(sendTo[j]);
				
				if ( newrecipient.indexOf("[USER]") == 0 )
				{
					
					acinf.setAclName(newrecipient.substring(7));
					acinf.setAclType("[USER]");
					
				}
				
				if (!(crtInf.getRecipientsList().contains(acinf)))
					crtInf.getRecipientsList().add(acinf);																					
				
				
			}
			
			
			
			
			// need to check for duplicates
			//need to query groups visible to dropbox user. These groups are appended to sendto List for JSP
			
			//for the "Available Individual/Groups"
			Hashtable hash = new Hashtable();
			hash = dropbox.listGroups(); // lists groups on server
			
			
			
			Enumeration enum = hash.keys();
			while(enum.hasMoreElements()) {
				WebDboxAclInfo acinf = new WebDboxAclInfo();
				String groupname=(String)(enum.nextElement());
				
				acinf.setAclName(SearchEtc.htmlEscape(groupname)); // groupencode
				acinf.setAclType("[GROUP]");
				if (!(crtInf.getRecipientsList().contains(acinf)))
					crtInf.getRecipientsList().add(acinf);		
			}
			// JMC Storage Pool
			setupCRTInfo(dropbox, crtInf, poolid, expireval);
			// crtInf.setSelectedExpireValue(String.valueOf(expireval));
			
			
			//crtInf.setRcptEnum(crtInf.getRecipientsList().elements());
			//dropbox.setCreatDftInfoBean(crtInf);
			
			
			//store in webpage as attribute besides other attributes
			//session.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
			//request.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
			
			
			dropbox.setPackageExpiration(pkid,expireval);
			dropbox.setPackageDescription(pkid,dftFormData.getPkgDesc().trim());
			
			
			PackageInfo pinfo = dropbox.queryPackage(pkid);
			boolean sendNotification=pinfo.getPackageSendNotification();
			
			//if ( sendNotification != dropbox.isSendNotificationDefault())
			//	sendNotification=dropbox.isSendNotificationDefault();
			
			boolean requestReturnReceipt=pinfo.getPackageReturnReceipt();
			
			//if ( requestReturnReceipt != dropbox.isReturnReceiptDefault())
			//	requestReturnReceipt=dropbox.isReturnReceiptDefault();
			
			
			int pkgMsk = 0;
			int pkgVal = 0;
			
			if (sendNotification != dftFormData.getCbx1()) {								
				
				pkgMsk |= PackageInfo.SENDNOTIFY;
				if (dftFormData.getCbx1()) {
					pkgVal |= PackageInfo.SENDNOTIFY;
				}
				
			}								
			
			if (requestReturnReceipt != dftFormData.getCbx2()) {								
				
				pkgMsk |= PackageInfo.RETURNRECEIPT;
				if (dftFormData.getCbx2()) {
					pkgVal |= PackageInfo.RETURNRECEIPT;
				}
				
			}
			
			
			pkgMsk &= 0xFF;
			pkgVal &= 0xFF;
			
			
			if (pkgMsk != 0) 
				dropbox.setPackageFlags(pkid,pkgMsk,pkgVal);
			
			dropbox.setDraftFirstTime(true); 
			//	///////////////////////// cross recipients check /////////////////////////
			
			
			
			Vector rcptCompanies=dropbox.queryPackageAclCompanies(pkid);
			String company = dropbox.getTokenUserCompany(false);
			
			 
			 
			 if (rcptCompanies != null)
			 {	
			 	Collections.sort(rcptCompanies);
			 }
			 
			 Iterator it = rcptCompanies.iterator();
		      String repc = "";
		      int num = 0;
		      int rnum = 0;
		      while(it.hasNext()) {
		         String lcomp = (String)it.next();
		         
		        // If this is NOT the first one add a comma. Used to do it by
		        //  length, but if there is an empty string company <in test 
		        //  there is> then it does not have any indication in the list
		         if (rnum++ > 0)  repc += "," + lcomp;
		         else   repc  = lcomp;
		         if (!lcomp.equals(company)) num++;
		      }
		      
		      if (num > 1) {
		      	//System.out.println( "Note: this package has " + num + " companies represented in the ACL list besides your own:");
		      //	System.out.println("      Companies: (" + repc + ")");
		      	
		      	crtInf.setCrossRcptList(rcptCompanies); //SEED IN DATA FOR THE CROSSCOMP.JSP PAGE
		      	crtInf.setCrossRcptListLen(num);
		      	showCrossCompWarning=true;
		      	crtInf.setCrossRcptShow("showrcpts");   /// UPDATEACLFIELD TO BE CALLED CORRESPODNINGLY
		      	dftFormData.setCrossRcptWarning("showrcpts");  /// 
		      }
			
			
			crtInf.setRcptEnum(crtInf.getRecipientsList().elements());
			dropbox.setCreatDftInfoBean(crtInf);
			
			
			//store in webpage as attribute besides other attributes
			session.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
			request.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
			
			
			
			
			/////////////////////////////////////////////////////////////////////////////
			
			
			
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true; 
			
		}	
		catch (Exception e)
		{
			
			
			logerror(e);
			
			createErrorFromException("Error while handling Draft Panel action", e, errors);
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftsFailure");
			
			
		} 		
		else{
			
			if (addFiles.equals("true"))
			{
				
				forward = mapping.findForward("AddFilesSuccess");	
			} else
				if  ( showCrossCompWarning == true)
			{
				return mapping.findForward("ShowCrossComp");
			}
			
			
			else
				forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	
	
	
	
	
	private long getDaysBetween (java.util.Date d1, java.util.Date d2) {
		
		
		
		Calendar c1=new GregorianCalendar(d1.getYear(),d1.getMonth(),d1.getDate());
		Calendar c2=new GregorianCalendar(d2.getYear(),d2.getMonth(),d2.getDate());;
		
		if (c1.after(c2)) {  // swap dates so that c1 is start and c2 is end
			java.util.Calendar swap = c1;
			c1 = c2;
			c2 = swap;
		}
		long days = c2.get(java.util.Calendar.DAY_OF_YEAR) -
		c1.get(java.util.Calendar.DAY_OF_YEAR);
		int y2 = c2.get(java.util.Calendar.YEAR);
		if (c1.get(java.util.Calendar.YEAR) != y2) {
			c1 = (java.util.Calendar) c1.clone();
			do {
				days += c1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
				c1.add(java.util.Calendar.YEAR, 1);
			} while (c1.get(java.util.Calendar.YEAR) != y2);
		}
		return days;
	} 
	
	
	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param errors
	 * @param forward
	 * @param dftFormData
	 * @param dropbox
	 */
	private void refreshCreatePkgPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, ActionErrors errors, ActionForward forward, WebDboxCreateDraftPkgForm dftFormData, UserDropbox dropbox,HttpSession session,String itarsess) throws Exception{
		
		
		if (!dftFormData.getPoolReselect().equalsIgnoreCase("poolreselected"))
		{	
			
			if (itarsess.equalsIgnoreCase("true")) 
			{	 
				
				dftFormData.setValidItarSession("true");
				dropbox.getCreatDftInfoBean().setValSessItar("true");
				
				dropbox.setItarSessionCertified(true);
				dftFormData.setContainsItarCbx(true); // fixing a Windoz bug for cancel "X" on windows dialog..sigh
				
				
				dftFormData.setItarSelect("unselect");
				dropbox.getCreatDftInfoBean().setItarSel("unselect");
				
				
			}
			if (itarsess.equalsIgnoreCase("false")) 
			{	
				dftFormData.setValidItarSession("false");
				dropbox.getCreatDftInfoBean().setValSessItar("false");
				
				dropbox.setItarSessionCertified(false);
				dftFormData.setContainsItarCbx(false);
				
				dftFormData.setItarSelect("unselect");
				dropbox.getCreatDftInfoBean().setItarSel("unselect");
			}
		}else 
		{
			dftFormData.setItarSelect("unselect");
		}
		
		    setItarAttributes(request,response,dropbox);
		    
            String pkgDescp="";
		    request.setAttribute("pkgDesLen",String.valueOf(pkgDescp.length()));
			int leftChar=1024-pkgDescp.length();
			request.setAttribute("pkgDesLeftChar",String.valueOf(leftChar));

		dftFormData.setPoolReselect("none");
		
		// JMC StoragePools
		// The pool name is actually the pool id
		String poolname = dftFormData.getPoolName();
		Vector pools = dropbox.queryStoragePoolInformation();
		dropbox.getCreatDftInfoBean().setStoragePools(pools);
		PoolInfo pi = dropbox.getSelectedPoolInfo();
		if (poolname != null) {
			try {
				long poolid = Long.parseLong(poolname);
				
				if (pi.getPoolId() != poolid) {
					// If selected name different than cur pool
					Iterator it = pools.iterator();
					while(it.hasNext()) {
						PoolInfo lpi = (PoolInfo)it.next();
						if (poolid == lpi.getPoolId()) {
							pi = lpi;
						}
					}
				}
			} catch(Exception ee) {
				logerror("Error setting storagepool");
				logerror(ee);
			}
		}
		dropbox.setSelectedPoolInfo(pi);
		dropbox.getCreatDftInfoBean().setSelectedStoragePool(pi);
		String expire=dropbox.prepareExpiration(pi.getPoolMaxDays(),pi.getPoolDefaultDays());
		dropbox.getCreatDftInfoBean().setPkgExpireDate("Expires on :"+expire);
		
		
		//       stuff the bean with userinfo and groupinfo	
		WebDboxCreateDraftPkgInfo crtInf = (WebDboxCreateDraftPkgInfo)(dropbox.getCreatDftInfoBean());
		String accListRcpt=((String)(dftFormData.getAclListRcpt()));
		String newrecipient=null;
		
		String[] sendTo = process(accListRcpt,","); // we use the " ,[USER] u1,[GROUP] g1.."
		
		
		for(int j=1;j<sendTo.length;j++)
		{
			WebDboxAclInfo acinf = new WebDboxAclInfo();
			newrecipient=(String)(sendTo[j]);
			
			if ( newrecipient.indexOf("_U_") == 0 )
			{
				
				acinf.setAclName(newrecipient.substring(3,newrecipient.length()));
				acinf.setAclType("[USER]");
				
			}else if ( newrecipient.indexOf("_G_") == 0 )
			{
				acinf.setAclName(SearchEtc.htmlEscape(newrecipient.substring(3,newrecipient.length()))); // groupencode
				acinf.setAclType("[GROUP]");
				
				
			}else if ( newrecipient.indexOf("_P_") == 0)
			{
				acinf.setAclName(newrecipient.substring(3,newrecipient.length()));
				acinf.setAclType("[PROJECT]");
				
				
			}
			
			if (!(crtInf.getSendList().contains(acinf)))
				crtInf.getSendList().add(acinf);																					
			
			
		}
		
		
		
		crtInf.setRcptEnum(crtInf.getRecipientsList().elements());
		dropbox.setCreatDftInfoBean(crtInf);
		
		
		//store in webpage as attribute besides other attributes
		session.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
		request.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
		
		
		
		
	}
	
	
	boolean checkIfSessionCertified(UserDropbox dbx,String pkgname,int which)
	{
		boolean isCert=false;
		
		try {
			Enumeration enum=dbx.listInOutSandBox(which);
			while(enum.hasMoreElements()) {
				PackageInfo pinfo = (PackageInfo)enum.nextElement();
				if (pinfo.getPackageName().equals(pkgname)) {
					boolean isPkgItar=pinfo.isPackageItar();
					
					if (isPkgItar)
					{
						if ( !dbx.isItarSessionCertified())
						{
							isCert=true;
						}
						
					}
					
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return isCert;
		
	}
	
	
	boolean checkIfSessionCertified(UserDropbox dbx,long pkgid,int which)
	{
		boolean isCert=false;
		
		try {
			Enumeration enum=dbx.listInOutSandBox(which);
			while(enum.hasMoreElements()) {
				PackageInfo pinfo = (PackageInfo)enum.nextElement();
				if (pinfo.getPackageId() == pkgid ) {
					boolean isPkgItar=pinfo.isPackageItar();
					
					if (isPkgItar)
					{
						if ( !dbx.isItarSessionCertified())
						{
							isCert=true;
						}
						
					}
					
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return isCert;
		
	}
	
	
	
	/**
	 * doEditRecipientsCrtPkg Action method edits an existing Package on the dropbox server
	 * 
	 */
	
	public ActionForward doEditRecipientsCrtPkg(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		WebDboxCreateDraftPkgForm dftFormData = (WebDboxCreateDraftPkgForm) form;
		String newrecipient=null;
		boolean loggedInException = false;
		
		
		try {
			
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String packNameStr=dropbox.getDraftInfoBean().getSelectdDraftPkg();
			
			//pre-populate form data
			dftFormData.setPackageName(packNameStr);  //store package name
			dftFormData.setPackNameField("disable");
			
			boolean sendNotification=false;
			boolean requestReturnReceipt=false;
			boolean isPkgItar=false;
			String pkgDescp="";
			
			//String expireValue=null; JMC
			long expireval = -999; // JMC
			String expire=null;
			
			
			Enumeration editdraftEnum=dropbox.listInOutSandBox(4);
			long pkid=0;
			long poolid=0;
			while(editdraftEnum.hasMoreElements()) {
				PackageInfo pinfo = (PackageInfo)editdraftEnum.nextElement();
				if (pinfo.getPackageName().equals(packNameStr)) {
					pkid = pinfo.getPackageId();
					poolid = pinfo.getPackagePoolId();
					sendNotification=pinfo.getPackageSendNotification();
					requestReturnReceipt=pinfo.getPackageReturnReceipt();
					isPkgItar=pinfo.isPackageItar();
					
					if (isPkgItar)
					{
						if ( !dropbox.isItarSessionCertified())
						{
							throw new Exception("Webdropbox: Editing Package: Can't access ITAR package data. Please certify your session in Options");
						}
						
					}
					
					pkgDescp=pinfo.getPackageDescription();
					
					////////////////////pool /////////////////////
					try {
						
						
						Vector pools = dropbox.queryStoragePoolInformation();
						dropbox.getCreatDftInfoBean().setStoragePools(pools);
						
						PoolInfo pi = dropbox.getSelectedPoolInfo();
						
						
						Iterator it = pools.iterator();
						while(it.hasNext()) {
							PoolInfo lpi = (PoolInfo)it.next();
							if (poolid == lpi.getPoolId()) {
								pi = lpi;
							}
						}
						
						// The pool name is actually the pool id
						String poolname = pi.getPoolName();
						dropbox.setSelectedPoolInfo(pi);
						dropbox.getCreatDftInfoBean().setSelectedStoragePool(pi);
						
						
						
						
					} catch(Exception ee) {
						logerror("Error setting storagepool");
						logerror(ee);
					}
					
					
					expire=new Date(pinfo.getPackageExpiration()).toGMTString();
					
					
					break;
				}
			}
			
			// store package acls in sendto List
			if ( pkid != 0  && dropbox.isCrossCompEdit() == false)
			{
				Enumeration enumofacls=dropbox.queryAcls(pkid); //query existing package acls
				
				//stuff the bean with userinfo and groupinfo	
				WebDboxCreateDraftPkgInfo crtInf = (WebDboxCreateDraftPkgInfo)(dropbox.getCreatDftInfoBean());
				if ( crtInf.getSendList() != null )
					crtInf.getSendList().clear();
				
				StringBuffer sbufAccList = new StringBuffer();
				sbufAccList.append(" ");
				StringBuffer sbufAccListRcpt = new StringBuffer();
				sbufAccListRcpt.append(" ");
				
				while(enumofacls.hasMoreElements())
				{
					
					
					WebDboxAclInfo acinf = new WebDboxAclInfo();
					String recipient=(String)(enumofacls.nextElement());
					
					
					
					
					if ( recipient.indexOf("_G_") == 0 )
					{
						acinf.setAclName(SearchEtc.htmlEscape(recipient.substring(3,recipient.length()))); // groupencode
						acinf.setAclType("[GROUP]");
						
						sbufAccListRcpt.append(","+recipient);
						
						
					}else if ( recipient.indexOf("_P_") == 0)
					{
						acinf.setAclName(recipient.substring(3,recipient.length()));
						acinf.setAclType("[PROJECT]");
						
						sbufAccListRcpt.append(","+recipient);
						
						
					}else 
					{
						acinf.setAclName(recipient);
						acinf.setAclType("[USER]");
						
						sbufAccListRcpt.append(","+"_U_"+recipient);
						
					}
					
					sbufAccList.append(","+acinf.getAclType()+" "+acinf.getAclName());
					
					
					
					if (!(crtInf.getSendList().contains(acinf)))
						crtInf.getSendList().add(acinf);	
					
					
				} 
				
				
				dftFormData.setContainsItarCbx(isPkgItar);
				dftFormData.setPkgDesc(pkgDescp);

                request.setAttribute("pkgDesLen",String.valueOf(pkgDescp.length()));
				int leftChar=1024-pkgDescp.length();
				request.setAttribute("pkgDesLeftChar",String.valueOf(leftChar));

				dftFormData.setAclList((String)(sbufAccList.toString()));
				dftFormData.setAclListRcpt((String)(sbufAccListRcpt.toString()));
				
				dftFormData.setCbx1(sendNotification);
				dftFormData.setCbx2(requestReturnReceipt);
				
				dftFormData.setExpireDate("Expires on :"+dropbox.mmddyyyy(expire));
				
				crtInf.setPkgExpireDate("Expires on :"+dropbox.mmddyyyy(expire));
				crtInf.setSelectedStoragePool(dropbox.getSelectedPoolInfo());
				
				
				crtInf.setDisablePkgNameField(true); //disable text editing for Package name
				crtInf.setEditPackageRcpt(true);    //enable edit recipients mode for use in doCreatePkg
				
				
				
				String calendarEnddate=dropbox.prepareExpiration(dropbox.getCreatDftInfoBean().getSelectedStoragePool().getPoolMaxDays(),dropbox.getCreatDftInfoBean().getSelectedStoragePool().getPoolDefaultDays());
				dropbox.getCreatDftInfoBean().setCalendarEndDate("Expires on :"+calendarEnddate);
				
				crtInf.setSendEnum(crtInf.getSendList().elements()); //store the acls in send to list
				dropbox.setCreatDftInfoBean(crtInf);
				
				
				//store in webpage as attribute besides other attributes
				session.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
				request.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
				
				
				
				
				WebDboxDispGroupInfo grpInfo = new WebDboxDispGroupInfo();					
				grpInfo.setGroupInfoHash(dropbox.listGroups());
				
				dropbox.setGroupInfoBean(grpInfo); 												
				
				//store in webpage as attribute besides other attributes
				session.setAttribute("webGrpDispInfo",dropbox.getGroupInfoBean());
				request.setAttribute("webGrpDispInfo",dropbox.getGroupInfoBean());
				
				
				
			}  
             //	store flattened package acls in sendto List
			if ( pkid != 0  && dropbox.isCrossCompEdit() == true)
			{
				
				
		      	
			//	System.out.println("--------------   pkid != 0  && dropbox.isCrossCompEdit() is true ------------ ");
				dropbox.setCrossCompEdit(false);
				
				
				//stuff the bean with userinfo and groupinfo	
				WebDboxCreateDraftPkgInfo crtInf = (WebDboxCreateDraftPkgInfo)(dropbox.getCreatDftInfoBean());
				if ( crtInf.getSendList() != null )
					crtInf.getSendList().clear();
				
				 ProcessSendToListForDraft(dropbox,pkid,crtInf,isPkgItar);
				 crtInf.setCrossRcptShow("showrcpts");   /// to strip the **[...]** in updateaclfield of javascripts..
			     dftFormData.setCrossRcptWarning("showrcpts"); /// to strip the **[...]** in updateaclfield of javascripts..
				
				
				
				
				dftFormData.setContainsItarCbx(isPkgItar);
				dftFormData.setPkgDesc(pkgDescp);

                request.setAttribute("pkgDesLen",String.valueOf(pkgDescp.length()));
				int leftChar=1024-pkgDescp.length();
				request.setAttribute("pkgDesLeftChar",String.valueOf(leftChar));

				
				dftFormData.setCbx1(sendNotification);
				dftFormData.setCbx2(requestReturnReceipt);
				
				dftFormData.setExpireDate("Expires on :"+dropbox.mmddyyyy(expire));
				
				crtInf.setPkgExpireDate("Expires on :"+dropbox.mmddyyyy(expire));
				crtInf.setSelectedStoragePool(dropbox.getSelectedPoolInfo());
				
				
				crtInf.setDisablePkgNameField(true); //disable text editing for Package name
				crtInf.setEditPackageRcpt(true);    //enable edit recipients mode for use in doCreatePkg
				
				
				
				String calendarEnddate=dropbox.prepareExpiration(dropbox.getCreatDftInfoBean().getSelectedStoragePool().getPoolMaxDays(),dropbox.getCreatDftInfoBean().getSelectedStoragePool().getPoolDefaultDays());
				dropbox.getCreatDftInfoBean().setCalendarEndDate("Expires on :"+calendarEnddate);
				
				crtInf.setSendEnum(crtInf.getSendList().elements()); //store the acls in send to list
				dropbox.setCreatDftInfoBean(crtInf);
				
				
				//store in webpage as attribute besides other attributes
				session.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
				request.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
				
			
			
			
				WebDboxDispGroupInfo grpInfo = new WebDboxDispGroupInfo();					
				grpInfo.setGroupInfoHash(dropbox.listGroups());
				
				dropbox.setGroupInfoBean(grpInfo); 												
				
				//store in webpage as attribute besides other attributes
				session.setAttribute("webGrpDispInfo",dropbox.getGroupInfoBean());
				request.setAttribute("webGrpDispInfo",dropbox.getGroupInfoBean());
				
					
				//System.out.println("---pkid != 0  && dropbox.isCrossCompEdit() ==true ACLS ARE ...."+crtInf.getSendList().toString());
				
			}  
			
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			
			logerror(e);
			createErrorFromException("Error while editing package recipients", e, errors);
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftsFailure");
			
			
		} else {
			
			forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	
	
	
	
	
	
	
	
	private void ProcessSendToListForDraft(UserDropbox dropbox, long pkid, WebDboxCreateDraftPkgInfo crtInf,boolean isPkgItar ) throws DboxException,Exception {
		
		
		//	System.out.println("<<<<<<<<<<<<<<<<   ProcessSendToListForDraft invoked >>>>>>>>>>>>>>>>>"); 
			Enumeration enumofacls = dropbox.queryAcls(pkid);
			
			
			
			Vector ret = null;
			while(enumofacls.hasMoreElements()) {
			  	
			  	String recipient=(String)(enumofacls.nextElement());
			  	if ( recipient.indexOf("_G_") == 0 )
					{
			  		    AclInfo ainfogrp=new AclInfo(); 
			  		    WebDboxAclInfo webacinfgrp = new WebDboxAclInfo();
						String groupName=recipient.substring(3,recipient.length());
						ainfogrp.setAclName(groupName);
						ainfogrp.setAclType(DropboxGenerator.STATUS_GROUP);
						String compForGroup=getAssociatedCompanies(dropbox,isPkgItar,ainfogrp);	
						String compForGroupStr="**"+compForGroup+"**";
						
						webacinfgrp.setAclName(SearchEtc.htmlEscape(recipient.substring(3,recipient.length())+" "+compForGroupStr)); // groupencode
						webacinfgrp.setAclType("[GROUP]");
						
						crtInf.getSendList().add(webacinfgrp);  //ADD TO SEND TO LIST
					//	System.out.println("<<<   ProcessSendToListForDraft invoked >>>>[GROUP] AclName ="+webacinfgrp.getAclName()); 
					}else if ( recipient.indexOf("_P_") == 0)
						{
						WebDboxAclInfo webacinfprj = new WebDboxAclInfo();
						webacinfprj.setAclName(recipient.substring(3,recipient.length()));
						webacinfprj.setAclType("[PROJECT]");
						
						crtInf.getSendList().add(webacinfprj);  //ADD TO SEND TO LIST
						
					}else 
					{
						AclInfo ainfousr=new AclInfo();
						 WebDboxAclInfo webacinfusr = new WebDboxAclInfo();
						ainfousr.setAclName(recipient);
						ainfousr.setAclType(DropboxGenerator.STATUS_NONE);
						String compForUsr=getAssociatedCompanies(dropbox,isPkgItar,ainfousr);	
						String compForUsrStr="**"+compForUsr+"**";
						webacinfusr.setAclName(SearchEtc.htmlEscape(recipient+" "+compForUsrStr)); // usrencode
						//webacinf.setAclName(recipient);
						webacinfusr.setAclType("[USER]");
						
						crtInf.getSendList().add(webacinfusr);  //ADD TO SEND TO LIST
					//	System.out.println("----  ProcessSendToListForDraft invoked >>>>[USER] AclName ="+webacinfusr.getAclName());
					
						
					}
						
			  			//if (!(crtInf.getSendList().contains(webacinf)))
			  				//crtInf.getSendList().add(webacinf);	
			     
			}
		
	}
	
	
	
	
	
	
	
	private void ProcessSendToListForSent(UserDropbox dropbox, long pkid, WebDboxCreateSentPkgInfo crtInf,boolean isPkgItar ) throws DboxException,Exception {
		
		
		//	System.out.println("<<<<<<<<<<<<<<<<   ProcessSendToListForSent invoked >>>>>>>>>>>>>>>>>"); 
			Enumeration enumofacls = dropbox.queryAcls(pkid);
			
			
			
			Vector ret = null;
			while(enumofacls.hasMoreElements()) {
			  	
			  	String recipient=(String)(enumofacls.nextElement());
			  	if ( recipient.indexOf("_G_") == 0 )
					{
			  		    AclInfo ainfogrp=new AclInfo(); 
			  		    WebDboxAclInfo webacinfgrp = new WebDboxAclInfo();
						String groupName=recipient.substring(3,recipient.length());
						ainfogrp.setAclName(groupName);
						ainfogrp.setAclType(DropboxGenerator.STATUS_GROUP);
						String compForGroup=getAssociatedCompanies(dropbox,isPkgItar,ainfogrp);	
						String compForGroupStr="**"+compForGroup+"**";
						
						webacinfgrp.setAclName(SearchEtc.htmlEscape(recipient.substring(3,recipient.length())+" "+compForGroupStr)); // groupencode
						webacinfgrp.setAclType("[GROUP]");
						
						crtInf.getSendList().add(webacinfgrp);  //ADD TO SEND TO LIST
					//	System.out.println("<<<   ProcessSendToListForSent invoked >>>>[GROUP] AclName ="+webacinfgrp.getAclName()); 
					}else if ( recipient.indexOf("_P_") == 0)
						{
						WebDboxAclInfo webacinfprj = new WebDboxAclInfo();
						webacinfprj.setAclName(recipient.substring(3,recipient.length()));
						webacinfprj.setAclType("[PROJECT]");
						
						crtInf.getSendList().add(webacinfprj);  //ADD TO SEND TO LIST
						
					}else 
					{
						AclInfo ainfousr=new AclInfo();
						 WebDboxAclInfo webacinfusr = new WebDboxAclInfo();
						ainfousr.setAclName(recipient);
						ainfousr.setAclType(DropboxGenerator.STATUS_NONE);
						String compForUsr=getAssociatedCompanies(dropbox,isPkgItar,ainfousr);	
						String compForUsrStr="**"+compForUsr+"**";
						webacinfusr.setAclName(SearchEtc.htmlEscape(recipient+" "+compForUsrStr)); // usrencode
						//webacinf.setAclName(recipient);
						webacinfusr.setAclType("[USER]");
						
						crtInf.getSendList().add(webacinfusr);  //ADD TO SEND TO LIST
					//	System.out.println("----  ProcessSendToListForSent invoked >>>>[USER] AclName ="+webacinfusr.getAclName());
					
						
					}
						
			  			//if (!(crtInf.getSendList().contains(webacinf)))
			  				//crtInf.getSendList().add(webacinf);	
			     
			}
		
	}
		
		
		
		
		
		
		

	/**
	 * @param groupName
	 * @param dropbox
	 * @return
	 */
	private String getAssociatedCompanies(UserDropbox dropbox,boolean itar,AclInfo inf) {
		String associateComps=null;
		
		Vector companies = new Vector();
		companies.addElement(inf);
		try {
			associateComps=(dropbox.queryRepresentedCompanies(companies,itar)).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return associateComps;
	}

	public ActionForward doFwdAddFilesPage(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		WebDboxDraftPkgForm dftFormData = (WebDboxDraftPkgForm) form;
		String encoding = null;
		boolean loggedInException = false;
		
		
		try {
			
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			//------Added for CSRFIX(IBMCC00009471) Editing & Adding files Drafts Prob ---------		
			
			
			String currentpkg=dropbox.getDraftInfoBean().getSelectdDraftPkg();
			
			Enumeration draftEnum=dropbox.listInOutSandBox(4);
			long pkid=0;
			while(draftEnum.hasMoreElements()) {
				PackageInfo finfo = (PackageInfo)draftEnum.nextElement();
				if (finfo.getPackageName().equals(currentpkg)) {
					pkid = finfo.getPackageId();
					
					break;
				}
			}
			
			Vector fileData = (Vector)(dropbox.getDraftInfoBean().getFileDescriptorsForPkgId(pkid));
			dropbox.getDraftInfoBean().setDraftFileContents(fileData);
			dropbox.getDraftInfoBean().setDraftFileContentsLen(fileData.size());	
			
			
			dropbox.setDraftInfoBean(dropbox.getDraftInfoBean()); //dftInfo now has indices,sortedpkgs,unsortedpkgs,filecontents												
			
		    //store in webpage as attribute besides other attributes
			session.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
			request.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());					 
			
			
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			
			
			logerror(e);
			createErrorFromException("Error while adding files to package", e, errors);
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftsFailure");
			
			
		} else {
			
			forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	/**
	 * doAddFilesToPackage Action method adds files to a package on the dropbox server.
	 * 
	 */				
	
	public ActionForward doAddFilesToPackage(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception {
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		
		WebDboxAddFileForm addFileFormData = (WebDboxAddFileForm) form;
		String encoding = null;
		boolean loggedInException = false;
		
		// JMC
		//
		// By the time we make it here, we are already done uploading the file
		//  and jamming into the dropbox. Has to be that way using struts upload
		//
		// If there was an error with the upload that was not fatal to us getting
		//  the action callback, then check if there are any action errors in the
		//  request ... if so, add the errors and return failire. Otherwise, we
		//  setup to return the next page
		//						
		try {
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			// If there were errors, let them stand
			ActionErrors errl = (ActionErrors)request.getAttribute("UPLOADER-ERRORS");
			if (errl != null && !errl.isEmpty()) {
				saveErrors(request, errl);
				return mapping.findForward("DraftsFailure"); 
			}
			
			FormFile content = addFileFormData.getContent();
			String fname=content.getFileName();
			
			
			if (content.getFileSize() == 0 )
			{  
				dropbox.setDraftFirstTime(true);
				errors.add("error.fileinvalid.value",new ActionError("error.fileinvalid.value")); 
				saveErrors(request,errors);
				return mapping.findForward("DraftsFailure"); 
				
			}
			
			
			String currentpkg=dropbox.getDraftInfoBean().getSelectdDraftPkg();
			
			Enumeration draftEnum=dropbox.listInOutSandBox(4);
			long pkid=0;
			while(draftEnum.hasMoreElements()) {
				PackageInfo finfo = (PackageInfo)draftEnum.nextElement();
				if (finfo.getPackageName().equals(currentpkg)) {
					pkid = finfo.getPackageId();
					
					break;
				}
			}
			
			
			WebDboxDraftPkgInfo dftInfo = new WebDboxDraftPkgInfo();
			dftInfo.setSelectdDraftPkg(currentpkg);
			
			dftInfo.setSelectdPkgWithPkgId(dropbox.getDraftInfoBean().getSelectdPkgWithPkgId()); 
			int totalDraftPkgs=0;
			totalDraftPkgs=dropbox.retreiveDraftContents(dftInfo);
			
			dftInfo.setTotDraftdPkgs(totalDraftPkgs);
			dropbox.sortDraftPkgInfo(ETSComparator.getSortOrder(ETSComparator.SORT_BY_PKG_DATE_COMMIT_STR),ETSComparator.SORT_DESC,dftInfo);
			
			
			Vector fileData = (Vector)(dftInfo.getFileDescriptorsForPkgId(pkid));
			dftInfo.setDraftFileContents(fileData);
			dftInfo.setDraftFileContentsLen(fileData.size());	
			
			
			dropbox.setDraftInfoBean(dftInfo); 
			
			session.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
			request.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
			dropbox.setDraftFirstTime(true);
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true; 
		}	
		catch (Exception e)
		{
			
			
			logerror(e);
		 	createErrorFromException("Error while adding files to package", e, errors);
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftsFailure");
			
			
		} else {
			
			forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward); 
		
	}
	
	
	/**
	 * 
	 * doDraftPkgDelete Action method deletes a Package on the dropbox server
	 * 
	 */    
	
	
	public ActionForward doDraftPkgDelete(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		
		
		
		String encoding = null;
		boolean loggedInException = false;
		
		
		try {	
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String currentpkg=dropbox.getDraftInfoBean().getSelectdDraftPkg();
			
			Enumeration draftEnum=dropbox.listInOutSandBox(4);
			long pkid=0;
			while(draftEnum.hasMoreElements()) {
				PackageInfo finfo = (PackageInfo)draftEnum.nextElement();
				if (finfo.getPackageName().equals(currentpkg)) {
					pkid = finfo.getPackageId();
					
					break;
				}
			}
			
			dropbox.deletePackage(pkid);
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true; 
			
		}	
		catch (Exception e)
		{
			
			logerror(e);
			createErrorFromException("Error while deleting Draft package", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftsFailure");
			
			
		} else {
			
			forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	/**
	 * 
	 * doDraftPkgDelFiles Action method deletes files from a Package on the dropbox server
	 * 
	 */
	
	public ActionForward doDraftPkgDelFiles(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		
		WebDboxDraftPkgForm draftFormData = (WebDboxDraftPkgForm) form;
		boolean loggedInException = false;
		
		
		
		try {	
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String[] strArr = process(draftFormData.getFilesToDelete().trim(),",");
			
			if(strArr[0].trim().equals("nofilestodelete")){
				dropbox.setDraftFirstTime(true);
				errors.add("error.draft.draftfilenodelete",new ActionError("error.draft.draftfilenodelete"));												
				saveErrors(request, errors);
				return mapping.findForward("DraftsFailure"); 
				
			}
			
			
			String currentpkg=dropbox.getDraftInfoBean().getSelectdDraftPkg();
			draftFormData.setHighlitDraftPkg(currentpkg);
			
			
			
			Vector fileNamesForDeletion = dropbox.getDraftInfoBean().getDraftFileContents();
			
			
			Enumeration draftEnum=dropbox.listInOutSandBox(4);
			long pkid=0;
			while(draftEnum.hasMoreElements()) {
				PackageInfo finfo = (PackageInfo)draftEnum.nextElement();
				if (finfo.getPackageName().equals(currentpkg)) {
					pkid = finfo.getPackageId();
					
					break;
				}
			}												
			
			if(strArr[0].trim().equals("filestodelete")){
				
				for(int i=1;i<strArr.length;i++)
				{
					int idx=Integer.parseInt(strArr[i].trim());
					DboxFileInfo finf=(DboxFileInfo)fileNamesForDeletion.elementAt(idx);
					dropbox.deleteFileFromPackage(pkid,Long.parseLong(finf.getFileId().trim()));    
					
				}			
			}
			
			
			//store in webpage as attribute besides other attributes
			session.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
			request.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());			
			session.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
			request.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean()); 
			dropbox.setDraftFirstTime(false);
			
			draftFormData.setFileDelOper("false");
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			
			
			logerror(e);
			createErrorFromException("Error while deleting file(s) from package", e, errors);
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}			
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftsFailure");
			
			
		} else {
			
			forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward); 
	}
	
	/**
	 * 
	 * doDraftPkgSend Action method sends  a Package on the dropbox server destined to the Package recipients
	 * 
	 */							  
	
	public ActionForward doDraftPkgSend(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		
		WebDboxDraftPkgForm draftFormData = (WebDboxDraftPkgForm) form;
		String encoding = null;
		boolean loggedInException = false;
		
		
		try {	
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String currentpkg=dropbox.getDraftInfoBean().getSelectdDraftPkg();
			
			
			
			Enumeration draftEnum=dropbox.listInOutSandBox(4);
			long pkid=0;
			while(draftEnum.hasMoreElements()) {
				PackageInfo finfo = (PackageInfo)draftEnum.nextElement();
				if (finfo.getPackageName().equals(currentpkg)) {
					pkid = finfo.getPackageId();
					
					break;
				}
			}
			
			
			
			dropbox.getDraftInfoBean().setSelectdPkgWithPkgId(String.valueOf(pkid)); //save the pkgids for further 
			dropbox.getDraftInfoBean().setSelectdDraftPkg(currentpkg);
			draftFormData.setDraftPkgToLit(dropbox.getDraftInfoBean().getSelectdPkgWithPkgId());  
			
			
			DboxPackageInfo pinfo=new DboxPackageInfo();
			Vector listOfPackages=dropbox.queryDraftPackageContentsForpkgId(pkid,pinfo);
			String pkgStat=pinfo.getPackageState();
			
			
			if(pkgStat.trim().equals("Empty")){
				WebDboxDraftPkgInfo dftInfo = dropbox.getDraftInfoBean(); //get existing bean..
				dftInfo.setSelectdDraftPkg(currentpkg);
				draftFormData.setDraftPkgToLit(dftInfo.getSelectdPkgWithPkgId());
				
				dropbox.setDraftInfoBean(dftInfo);
				
				session.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
				request.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
				
				
				
				errors.add("error.draft.draftemptypkgsenderr",new ActionError("error.draft.draftemptypkgsenderr"));
				
				saveErrors(request, errors);
				return mapping.findForward("DraftsFailure"); 
				
			}			
			
			
			session.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
			request.setAttribute("webDraftPkgInfo",dropbox.getDraftInfoBean());
			dropbox.setDraftFirstTime(false);
			
			
			
			
			dropbox.commitPackage(pkid);
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			
			
			logerror(e);
			createErrorFromException("Error while sending package", e, errors); 
			
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftsFailure");
			
			
		} else {
			
			forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	
	/**
	 * 
	 * doDraftPackagesDelete Action method deletes Package(s) from the sent folder on the dropbox server
	 * 
	 */    
	
	
	public ActionForward doDraftPackagesDelete(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		
		
		String encoding = null;
		boolean loggedInException = false;
		
		WebDboxDraftPkgForm dftFormData = (WebDboxDraftPkgForm) form;
		
		
		try {	
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String currentpkg=dropbox.getDraftInfoBean().getSelectdDraftPkg();
			
			String[] strArr = process(dftFormData.getPackToTrash().trim(),",");
			
			if(strArr[0].trim().equals("nopkgstodelete")){
				
				errors.add("error.draft.draftpackgnodelete",new ActionError("error.draft.draftpackgnodelete"));												
				saveErrors(request, errors);
				return mapping.findForward("DraftsFailure"); 
				
			}
			
			if(strArr[0].trim().equals("pkgstodelete")){
				
				for(int i=1;i<strArr.length;i++)
				{
					String selectPkg=strArr[i].trim();
					
					dropbox.deletePackage(Long.parseLong(selectPkg));
					
				}			
			}
			
			dftFormData.setPackTrashOper("false");
			
			dropbox.setDraftFirstTime(true);
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true; 
			
		}	
		catch (Exception e)
		{
			
			
			logerror(e);
			createErrorFromException("Error while deleting package(s)", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftsFailure");
			
			
		} else {
			
			forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	
	
	/**
	 * 
	 * doGenInboxIndex Action method generates the prev&next indices for paging  
	 * 
	 */		
	
	
	public ActionForward doGenInboxIndex(	
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		WebDboxInboxForm model = (WebDboxInboxForm) form;
		boolean loggedInException = false;
		
		
		try {
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && s.indexOf(":prev:") >= 0) {
					
					
					WebDboxInboxInfo inf = (WebDboxInboxInfo)session.getAttribute("webInboxInfo");
					
					inf.setEndIndex((inf.getStartIndex()-1));
					inf.setStartIndex((inf.getStartIndex()>= 1 ) ? inf.getStartIndex()-6 : 1 );
					
					inf.setPackageRangeForDisplay(inf.getStartIndex(),inf.getEndIndex());
					
					request.setAttribute("webInboxInfo",inf);
					session.setAttribute("webInboxInfo",inf);													
					
					dropbox.setInboxInfoBean(inf);
					
					
					
					
					
				}else
					if (s != null && s.indexOf(":next:") >= 0) {
						
						WebDboxInboxInfo inf = (WebDboxInboxInfo)session.getAttribute("webInboxInfo");
						
						
						inf.setStartIndex( ( inf.getEndIndex() < inf.getTotRcvdPkgs()) ? inf.getEndIndex()+1:inf.getEndIndex());
						inf.setEndIndex((inf.getEndIndex()+6) >= inf.getTotRcvdPkgs() ? inf.getTotRcvdPkgs():inf.getEndIndex()+6);				
						
						
						inf.setPackageRangeForDisplay(inf.getStartIndex(),inf.getEndIndex());
						
						request.setAttribute("webInboxInfo",inf);
						session.setAttribute("webInboxInfo",inf);
						dropbox.setInboxInfoBean(inf);										
						
					}
					
			}
			
			
			doInbox(mapping,form,request, response);
			
			
		}catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}
		catch (Exception e)
		{
			
			
			logerror(e);
			createErrorFromException("Error while handling Inbox multipage indices", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("InboxFailure");
			
			
		} else {
			
			forward = mapping.findForward("InboxSuccess");
		}
		
		
		return (forward); 
	}
	
	
	/**
	 * 
	 * doGenDraftIndex Action method generates the prev&next indices for paging  
	 * 
	 */								
	public ActionForward doGenDraftIndex(	
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		WebDboxDraftPkgForm model = (WebDboxDraftPkgForm) form;
		boolean loggedInException = false;
		
		
		
		try {
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && s.indexOf(":prev:") >= 0) {
					
					
					WebDboxDraftPkgInfo inf = (WebDboxDraftPkgInfo)session.getAttribute("webDraftPkgInfo");
					
					inf.setEndDraftIndex((inf.getStartDraftIndex()-1));
					inf.setStartDraftIndex((inf.getStartDraftIndex()>= 1 ) ? inf.getStartDraftIndex()-6 : 1 );
					
					inf.setPackageRangeForDisplay(inf.getStartDraftIndex(),inf.getEndDraftIndex());
					
					request.setAttribute("webDraftPkgInfo",inf);
					session.setAttribute("webDraftPkgInfo",inf);													
					
					dropbox.setDraftInfoBean(inf);
					
					
					
					
					
				}else
					if (s != null && s.indexOf(":next:") >= 0) {
						
						WebDboxDraftPkgInfo inf = (WebDboxDraftPkgInfo)session.getAttribute("webDraftPkgInfo");
						
						
						inf.setStartDraftIndex( ( inf.getEndDraftIndex() < inf.getTotDraftdPkgs()) ? inf.getEndDraftIndex()+1:inf.getEndDraftIndex());
						inf.setEndDraftIndex((inf.getEndDraftIndex()+6) >= inf.getTotDraftdPkgs() ? inf.getTotDraftdPkgs():inf.getEndDraftIndex()+6);				
						
						
						inf.setPackageRangeForDisplay(inf.getStartDraftIndex(),inf.getEndDraftIndex());
						
						request.setAttribute("webDraftPkgInfo",inf);
						session.setAttribute("webDraftPkgInfo",inf);
						dropbox.setDraftInfoBean(inf);										
						
					}
					
			}
			
			
			doDrafts(mapping,form,request, response);
			
			
		}catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}
		catch (Exception e)
		{
			
			logerror(e);
			createErrorFromException("Error while handling Draft multipage indices", e, errors); 
			
		}
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DraftPkgFailure");
			
			
		} else {
			
			forward = mapping.findForward("DraftsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	/**
	 * 
	 * doGenSentIndex Action method generates the prev&next indices for paging  
	 * 
	 */								
	public ActionForward doGenSentIndex(	
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		WebDboxSentPkgForm model = (WebDboxSentPkgForm) form;
		boolean loggedInException = false;
		
		
		
		try {
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && s.indexOf(":prev:") >= 0) {
					model.setHighlitSentPkg("NoPackageSelected");
					model.setSentPkgToLit("NoPackageSelected");  
					
					
					WebDboxSentPkgInfo inf = (WebDboxSentPkgInfo)session.getAttribute("webSentPkgInfo");
					
					inf.setEndSentIndex((inf.getStartSentIndex()-1));
					inf.setStartSentIndex((inf.getStartSentIndex()>= 1 ) ? inf.getStartSentIndex()-6 : 1 );
					
					inf.setPackageRangeForDisplay(inf.getStartSentIndex(),inf.getEndSentIndex());
					
					request.setAttribute("webSentPkgInfo",inf);
					session.setAttribute("webSentPkgInfo",inf);													
					
					dropbox.setSentInfoBean(inf);
					
					
					
					
					
				}else
					if (s != null && s.indexOf(":next:") >= 0) {
						
						model.setHighlitSentPkg("NoPackageSelected");
						model.setSentPkgToLit("NoPackageSelected"); 
						
						WebDboxSentPkgInfo inf = (WebDboxSentPkgInfo)session.getAttribute("webSentPkgInfo");
						
						
						inf.setStartSentIndex( ( inf.getEndSentIndex() < inf.getTotSentPkgs()) ? inf.getEndSentIndex()+1:inf.getEndSentIndex());
						inf.setEndSentIndex((inf.getEndSentIndex()+6) >= inf.getTotSentPkgs() ? inf.getTotSentPkgs():inf.getEndSentIndex()+6);				
						
						
						inf.setPackageRangeForDisplay(inf.getStartSentIndex(),inf.getEndSentIndex());
						
						request.setAttribute("webSentPkgInfo",inf);
						session.setAttribute("webSentPkgInfo",inf);
						dropbox.setSentInfoBean(inf);										
						
					}
					
			}
			
			
			doSent(mapping,form,request, response);
			
			
		}catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}
		catch (Exception e)
		{
			
			logerror(e);
			createErrorFromException("Error while handling Sent multipage indices", e, errors); 
			
		}
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("SentPkgFailure");
			
			
		} else {
			
			forward = mapping.findForward("SentsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	/**
	 * 
	 * doOptions Action method pre-seeds data for the options page  
	 * 
	 */							
	
	public ActionForward doOptions(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception {
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		WebDboxOptionsForm optForm = (WebDboxOptionsForm) form;
		boolean loggedInException = false;
		
		
		
		try {
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			
			if (optForm.getValidItarSession().trim().equalsIgnoreCase("true") && optForm.getOptItarCertInfo().trim().equalsIgnoreCase("optItarCertInfo"))
			{
				dropbox.setItarSessionCertified(Boolean.valueOf(optForm.getValidItarSession().trim()).booleanValue());	
				
				
			}
			if (optForm.getValidItarSession().trim().equalsIgnoreCase("false") && optForm.getOptItarCertInfo().trim().equalsIgnoreCase("optItarCertInfo"))
				dropbox.setItarSessionCertified(Boolean.valueOf(optForm.getValidItarSession().trim()).booleanValue());						     
			
			
			if (!dropbox.isItarSessionCertified())
				dropbox.setOption(DropboxAccess.ItarSessionCertified,"FALSE");
			else
				dropbox.setOption(DropboxAccess.ItarSessionCertified,"TRUE");
			
			if ( dropbox.isCreateGroupsFirstTime())
			{ 
				WebDboxCreateGroupInfo crGrpInf= new WebDboxCreateGroupInfo();
				crGrpInf.setRecipientsList(new Vector());
				crGrpInf.setSendList(new Vector());
				
				
				dropbox.setCreateGroupInfoBean(crGrpInf);		
				session.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
				request.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
				
				dropbox.setCreateGroupsFirstTime(false);
			}
			
			// JMC StoragePools
			// The pool name is actually the pool id
			String poolname = optForm.getPoolName();
			Vector pools = dropbox.queryStoragePoolInformation();
			dropbox.getCreateGroupInfoBean().setStoragePools(pools);
			PoolInfo pi = dropbox.getSelectedPoolInfo();
			if (poolname != null) {
				try {
					long poolid = Long.parseLong(poolname);
					
					if (pi.getPoolId() != poolid) {
						// If selected name different than cur pool
						Iterator it = pools.iterator();
						while(it.hasNext()) {
							PoolInfo lpi = (PoolInfo)it.next();
							if (poolid == lpi.getPoolId()) {
								pi = lpi;
							}
						}
					}
				} catch(Exception ee) {
					logerror("Error setting storagepool");
					logerror(ee);
				}
			}
			dropbox.setSelectedPoolInfo(pi);
			dropbox.getCreateGroupInfoBean().setSelectedStoragePool(pi);
			dropbox.getCreateGroupInfoBean().setItarCertification(dropbox.isItarEntitled());
			
			String option = (String) dropbox.getOption(DropboxGenerator.SendNotificationDefault);
			boolean SendNotificationDefault = option != null && option.equalsIgnoreCase("TRUE");
			optForm.setSendNotificationDefault(SendNotificationDefault);
			
			option = (String) dropbox.getOption(DropboxGenerator.ReturnReceiptDefault);
			boolean ReturnReceiptDefault = option != null && option.equalsIgnoreCase("TRUE");
			optForm.setReturnReceiptDefault(ReturnReceiptDefault);
			
			option = (String) dropbox.getOption(DropboxGenerator.NewPackageEmailNotification);
			boolean NewPackageEmailNotification = option != null && option.equalsIgnoreCase("TRUE");
			optForm.setNewPackageEmailNotification(NewPackageEmailNotification);
			
			option = (String) dropbox.getOption(DropboxGenerator.NagNotification);
			boolean NagNotification = option != null && option.equalsIgnoreCase("TRUE");
			optForm.setNagNotification(NagNotification);
			
			
			setDefaultUserOptions(dropbox,request,response);
			
		}catch( NotLoggedInException nle)
		{							
			loggedInException = true;					
			
			
		}
		catch(Exception ee) {
			
			logerror(ee);
			createErrorFromException("Error while managing options", ee, errors); 
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			forward = mapping.findForward("OptionsFailure");
			
			
		} else {
			
			forward = mapping.findForward("OptionsSuccess");
		}
		
		return (forward);
		
	}
	
	
	
	/**
	 * The Action method doFwdCreateGroupPage performs Action Chaining/pre-seeding for the doCreateGroups Action.
	 * 
	 */
	
	public ActionForward doFwdCreateGroupPage(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception {
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		
		WebDboxCreateGroupForm grpForm = (WebDboxCreateGroupForm) form;
		
		
		boolean loggedInException = false;
		
		
		
		try {
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			String grpToEdit=(String)(grpForm.getTypeOfId());
			WebDboxCreateGroupInfo crtGrpInf = dropbox.getCreateGroupInfoBean();
			
			
			crtGrpInf.setGroupEditListability("");
			crtGrpInf.setGroupEditVisibility("");
			
			Hashtable hash = new Hashtable();
			hash = dropbox.listGroups();
			
			
			Vector memForGrp= new Vector();
			Vector sentMemForGrp= new Vector();
			Vector listOfUsers= new Vector();
			Vector listOfGrpNames= new Vector();
			Vector listOfEditors= new Vector();
			crtGrpInf.setRecipientsList(new Vector());
			crtGrpInf.setSendList(new Vector());
			
			crtGrpInf.setRecipientsListEditor(new Vector());
			crtGrpInf.setSendListEditor(new Vector());
			
			
			
			
			
			crtGrpInf.setListOfGroupNames(new Vector()); //added for groupedit
			
			
			//fetches all the users present in different groups on server n populates bean
			Enumeration enum = hash.keys();
			while(enum.hasMoreElements()) {
				
				String groupname=(String)(enum.nextElement());
				GroupInfo ginfo = (GroupInfo)hash.get(groupname);
				
				if (ginfo.getGroupListability() != DropboxGenerator.GROUP_SCOPE_NONE )
					listOfGrpNames.add(SearchEtc.htmlEscape(groupname)); //added for groupedit
				
				
				
				memForGrp=(Vector)(ginfo.getGroupMembers());
				
				Enumeration members=(Enumeration)(memForGrp.elements());
				while(members.hasMoreElements()){
					WebDboxAclInfo acinf = new WebDboxAclInfo();
					acinf.setAclName(((String)(members.nextElement())).trim());
					acinf.setAclType("[USER]");
					
					if (!(crtGrpInf.getRecipientsList().contains((WebDboxAclInfo)acinf)))
						crtGrpInf.getRecipientsList().add((WebDboxAclInfo)acinf);		
					if (!(crtGrpInf.getRecipientsListEditor().contains((WebDboxAclInfo)acinf)))
						crtGrpInf.getRecipientsListEditor().add((WebDboxAclInfo)acinf);	
					
					
				}
				
				
			}
			
			Collections.sort(listOfGrpNames); 
			listOfGrpNames.insertElementAt(new String("Create new group..."),0);
			
			
			
			
			if (!grpToEdit.equals("Create new group..."))
			{
				
				
				GroupInfo ginfo = (GroupInfo)hash.get(grpToEdit);
				sentMemForGrp=(Vector)(ginfo.getGroupMembers());
				Enumeration members=(Enumeration)(sentMemForGrp.elements());
				
				listOfEditors=(Vector)(ginfo.getGroupAccess());
				Enumeration editors=(Enumeration)(listOfEditors.elements());
				
				
				while(members.hasMoreElements()){
					WebDboxAclInfo acinf = new WebDboxAclInfo();
					acinf.setAclName(((String)(members.nextElement())).trim());
					acinf.setAclType("[USER]");
					
					if (!(crtGrpInf.getSendList().contains((WebDboxAclInfo)acinf)))
						crtGrpInf.getSendList().add((WebDboxAclInfo)acinf);		
					
					
				}			
				
				while(editors.hasMoreElements()){
					WebDboxAclInfo acinf = new WebDboxAclInfo();
					acinf.setAclName(((String)(editors.nextElement())).trim());
					acinf.setAclType("[USER]");
					
					if (!(crtGrpInf.getSendListEditor().contains((WebDboxAclInfo)acinf)))
						crtGrpInf.getSendListEditor().add((WebDboxAclInfo)acinf);		
					
					
				}
				byte use=(byte)(ginfo.getGroupVisibility());
				
				
				
				if (use == DropboxGenerator.GROUP_SCOPE_OWNER)
				{  
					
					crtGrpInf.setGroupEditVisibility("EDIT_VIS_GROUP_SCOPE_OWNER");
					crtGrpInf.setVisibility("VIS_GROUP_SCOPE_OWNER");	
					
				} 
				if (use == DropboxGenerator.GROUP_SCOPE_MEMBER)
				{  
					
					crtGrpInf.setGroupEditVisibility("EDIT_VIS_GROUP_SCOPE_MEMBER");
					crtGrpInf.setVisibility("VIS_GROUP_SCOPE_MEMBER");	
				} 
				
				byte list=(byte)(ginfo.getGroupListability());
				
				
				if (list == DropboxGenerator.GROUP_SCOPE_OWNER)
				{  
					
					crtGrpInf.setGroupEditListability("EDIT_LIS_GROUP_SCOPE_OWNER");
					crtGrpInf.setListability("LIS_GROUP_SCOPE_OWNER");
				} 
				if (list == DropboxGenerator.GROUP_SCOPE_MEMBER)
				{ 
					
					crtGrpInf.setGroupEditListability("EDIT_LIS_GROUP_SCOPE_MEMBER");
					crtGrpInf.setListability("LIS_GROUP_SCOPE_MEMBER");
				}   
				
				
				grpForm.setGroupName(grpToEdit);
				crtGrpInf.setDisableGroupNameField(true);
				crtGrpInf.setEditGroupMode(true);    
			}else {
				grpForm.setGroupName("");
				crtGrpInf.setDisableGroupNameField(false);
				crtGrpInf.setEditGroupMode(false);  
				crtGrpInf.setGroupEditVisibility("TEST_VIS_GROUP_SCOPE_OWNER");  
				crtGrpInf.setGroupEditListability("TEST_LIS_GROUP_SCOPE_OWNER");									
				
				crtGrpInf.setVisibility("VIS_GROUP_SCOPE_OWNER");	
				crtGrpInf.setListability("LIS_GROUP_SCOPE_OWNER");									
			}
			
			
			crtGrpInf.setListOfGroupNames(listOfGrpNames); //added for groupedit
			crtGrpInf.setSelectedGroupOptionValue(SearchEtc.htmlEscape(grpToEdit));
			
			
			dropbox.setCreateGroupInfoBean(crtGrpInf);
			
			
			session.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
			request.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());		
			
			
		}catch( NotLoggedInException nle)
		{							
			loggedInException = true;					
			
			
		}
		catch(Exception ee) {
			
			logerror(ee);
			createErrorFromException("Error while managing options", ee, errors); 
			
		}
		
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}	
		
		
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			forward = mapping.findForward("OptionsFailure");
			
			
		} else {
			
			forward = mapping.findForward("OptionsSuccess");
		}
		
		
		
		return (forward);
		
	}
	
	
	
	/**
	 * The Action method doCreateGroups creates the new Group of users on the server
	 * 
	 */	
	public ActionForward doCreateGroups(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception {
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		WebDboxCreateGroupForm grpForm = (WebDboxCreateGroupForm) form;
		boolean loggedInException = false;
		byte use=0;
		byte list=0;							
		
		
		
		
		try {
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String groupNameStr=((String)(grpForm.getGroupName())).trim().toLowerCase();							
			
			Hashtable groupHash = new Hashtable();
			groupHash = dropbox.listGroups();
			
			
			session.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
			request.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
			
			String iccIdStr=((String)(grpForm.getIccid())).trim().toLowerCase();
			
			String accList=((String)(grpForm.getAclList()));
			
			//Get the visibility and listability bytes.								
			
			if (grpForm.getVisibility().trim().equals("VIS_GROUP_SCOPE_OWNER"))
				use=DropboxGenerator.GROUP_SCOPE_OWNER;
			else if (grpForm.getVisibility().trim().equals("VIS_GROUP_SCOPE_MEMBER"))
				use=DropboxGenerator.GROUP_SCOPE_MEMBER;
			if (grpForm.getListablility().trim().equals("LIS_GROUP_SCOPE_OWNER"))
				list=DropboxGenerator.GROUP_SCOPE_OWNER;
			else if (grpForm.getListablility().trim().equals("LIS_GROUP_SCOPE_MEMBER"))
				list=DropboxGenerator.GROUP_SCOPE_MEMBER;
			
			
			
			
			
			
			String accListRcpt=((String)(grpForm.getAclListRcpt()));
			String accListRcptEditors=((String)(grpForm.getAclListRcptEditor()));
			
			
			
			
			
			if ( dropbox.getCreateGroupInfoBean().isEditGroupMode() == false )
			{	
				
				
				
				if (checkIfGroupNameValid(dropbox,groupNameStr)){
					
					errors.add("error.group.groupexists",new ActionError("error.group.groupexists"));
					
					saveErrors(request, errors);
					return mapping.findForward("OptionsFailure"); 
					
				}else {
					dropbox.createGroup(groupNameStr);
				}
				
			}   
			
			String[] strArr = process(accListRcpt,","); // we use the "  ,_U_u1,_G_g1.."
			String[] strArrEditors = process(accListRcptEditors,","); // we use the "  ,_U_u1,_G_g1.."
			String[] arrEditorsDel = process(accListRcptEditors,",");		
			
			
			String newrecipient=null;
			
			dropbox.deleteAclsForGroup(groupNameStr);
			
			//first is a empty str. All ACL's are added from   ,_U_u1,_G_g1..list
			for(int j=1;j<strArr.length;j++)
			{
				
				if ( strArr[j].trim().indexOf("_U_") == 0 )
				{
					newrecipient=strArr[j].substring(3); 
				}else
				{
					newrecipient=strArr[j];
				}
				
				
				dropbox.addGroupMemberAccess(groupNameStr,newrecipient.trim().toLowerCase(),true); //true=currently all members have access.
				
			}
			
			String currentLoggedUser=dropbox.getTokenUserCompany(true);
			
			Vector toDelete=dropbox.deleteEditorsForGroup(groupNameStr,currentLoggedUser,arrEditorsDel);
			
			//first is a empty str. All ACL's are added from   ,_U_u1,_G_g1..list
			for(int j=1;j<strArrEditors.length;j++)
			{
				
				if ( strArrEditors[j].trim().indexOf("_U_") == 0 )
				{
					newrecipient=strArrEditors[j].substring(3); 
				}else
				{
					newrecipient=strArrEditors[j];
				}
				
				
				
				if (!(newrecipient.trim().toLowerCase().equalsIgnoreCase(currentLoggedUser)))
					dropbox.addGroupMemberAccess(groupNameStr,newrecipient.trim().toLowerCase(),false); //false=currently access list add.
				else
				{
					if ( ! (queryIfGrpMemberExistsInGroup(dropbox,groupNameStr,newrecipient.trim())))
						dropbox.addGroupMemberAccess(groupNameStr,newrecipient.trim().toLowerCase(),false); //false=currently access list add.
				}
				
			}
			
			
			//Get the visibility and listability bytes.
			dropbox.setGroupAttributes(groupNameStr,use,true);
			dropbox.setGroupAttributes(groupNameStr,list,false);
			
			Enumeration delEnum=toDelete.elements();
			while(delEnum.hasMoreElements())
			{
				
				
				String userForDel=(String)delEnum.nextElement();											
				
				if (userForDel.equalsIgnoreCase(currentLoggedUser))										 	
					dropbox.removeGroupMemberAccess(groupNameStr,currentLoggedUser,false);
			}
			
			
			
			
		}
		catch( NotLoggedInException nle)
		{							
			loggedInException = true;						
			
			
		}
		catch(Exception ee) {
			
			logerror(ee);
			createErrorFromException("Error while managing options", ee, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}		
		
	
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			forward = mapping.findForward("OptionsFailure");
			
			
		} else {
			
			forward = mapping.findForward("OptionsSuccess");
		}
		
		
		
		return (forward);
		
	}
	
	
	
	
	
	
	private boolean queryIfGrpMemberExistsInGroup(UserDropbox dbox,String groupName,String groupmember )
	{
		boolean exists=false;
		
		try 
		{
			
			GroupInfo ginfo = dbox.listGroup(groupName);
			Vector memForGrp=(Vector)(ginfo.getGroupAccess());
			if (memForGrp.contains(groupmember))
			{   	
				exists=true; 
			}
			else
			{
				
				exists=false; 
			} 					
			
		} catch (Exception e) {
			
			logerror(e);
		}
		
		
		return exists;
		
	}
	
	
	
	
	
	
	
	/**
	 * The Action method doDeleteGroup deletes the Group in context.
	 * 
	 */	
	public ActionForward doDeleteGroup(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception {
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		WebDboxCreateGroupForm grpForm = (WebDboxCreateGroupForm) form;
		boolean loggedInException = false;
		
		
		
		try {
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String groupNameStr=((String)(grpForm.getGroupName())).trim().toLowerCase();
			
			
			session.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
			request.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
			
			dropbox.deleteGroup(groupNameStr);
			
			
			
		}catch( NotLoggedInException nle)
		{							
			loggedInException = true;						
			
			
		}
		catch(Exception ee) {
			
			logerror(ee);
			createErrorFromException(null, ee, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}		
		
		
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			forward = mapping.findForward("OptionsFailure");
			
			
		} else {
			
			forward = mapping.findForward("OptionsSuccess");
		}
		
		
		// Finish with
		return (forward);
		
	}
	
	
	
	/**
	 * 
	 * The Action method doSent performs a forward to the sent packages page
	 * 
	 */			
	
	public ActionForward doSent(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception {
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		
		WebDboxSentPkgForm sentFormData = (WebDboxSentPkgForm) form;
		
		
		String fieldname  = sentFormData.getModifyFieldName();
		String fieldvalue = sentFormData.getModifyFieldValue();
		
		
		
		boolean loggedInException = false;
		
		try {
			
			
			setStandardAttributes(request,response);
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			//fieldvalue stores the id..do a lookup and fetch the sent pkgname
			String sentPkgName=getSentPkgNameForId(fieldvalue,dropbox); //do a lookup for the pkgname
			if ( ! fieldvalue.equals(null) )
			{
				sentFormData.setHighlitSentPkg(sentPkgName);
				sentFormData.setSentPkgToLit(fieldvalue);
			}     
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && s.indexOf(":refresh:") >= 0) {
					
					dropbox.setSentFirstTime(true);
					sentFormData.setHighlitSentPkg("NoPackageSelected");
					sentFormData.setSentPkgToLit("NoPackageSelected");                  
					
					WebDboxDraftPkgInfo dfinf = new WebDboxDraftPkgInfo();	
					request.getSession(false).setAttribute("webDraftPkgInfo",dfinf);
					
					// init some create pkg info stuff
					dropbox.getCreatSntInfoBean().setDisablePkgNameField(false);
					WebDboxCreateSentPkgInfo crInf= new WebDboxCreateSentPkgInfo();
					crInf.setRecipientsList(new Vector());
					crInf.setSendList(new Vector());
					crInf.generateExpirationVals();
					
					
					dropbox.setCreatSntInfoBean(crInf);		
					session.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
					request.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
					
					
				}
			}
			
			if ( dropbox.isCreateSentFirstTime())
			{ 
				
				WebDboxCreateSentPkgInfo crInf= new WebDboxCreateSentPkgInfo();
				crInf.setRecipientsList(new Vector());
				crInf.setSendList(new Vector());
				crInf.generateExpirationVals();
				
				crInf.setDisablePkgNameField(false); 
				
				
				dropbox.setCreatSntInfoBean(crInf);		
				session.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
				request.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
				
				
				WebDboxCreateGroupInfo crGrpInf= new WebDboxCreateGroupInfo();
				crGrpInf.setRecipientsList(new Vector());
				crGrpInf.setSendList(new Vector());
				
				
				dropbox.setCreateGroupInfoBean(crGrpInf);		
				session.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
				request.setAttribute("webCrtGrpInfo",dropbox.getCreateGroupInfoBean());
				
				
				dropbox.setCreateSentFirstTime(false);
			}
			
			
			if ( dropbox.isSentFirstTime())
			{ 							
				
				int totalSentPkgs=0;
				int start=0;
				int end=0;				
				
				WebDboxSentPkgInfo sentInfo = new WebDboxSentPkgInfo();
				totalSentPkgs=dropbox.retreiveSentContents(sentInfo);
				
				WebDboxDraftPkgInfo dfinf = new WebDboxDraftPkgInfo();	
				request.getSession(false).setAttribute("webDraftPkgInfo",dfinf);
				
				
				dropbox.sortSentPkgInfo(ETSComparator.getSortOrder(ETSComparator.SORT_BY_PKG_STR),ETSComparator.SORT_DESC,sentInfo);
				
				if ( totalSentPkgs > 6 )
				{   
					//set indices for more than 6 pkgs
					start=1;
					end=6;
					
				}else { 
					//set indices for less than 6 pkgs
					start=1;	
					end=totalSentPkgs;								 
				}
				sentInfo.setStartSentIndex(start);
				sentInfo.setEndSentIndex(end);
				sentInfo.setTotSentPkgs(totalSentPkgs);
				
				sentInfo.setPackageRangeForDisplay(start,end);
				
				
				dropbox.setSentInfoBean(sentInfo); //sentInfo now has indices,sortedpkgs,unsortedpkgs
				
				
				
				String selectPkg=sentFormData.getHighlitSentPkg().trim();
				
				
				
				
				if ( selectPkg.equals("NoPackageSelected"))
				{
					dropbox.getSentInfoBean().setSelectdSentPkg(selectPkg);								  
					
					session.setAttribute("selPkgInfo","NoPackageSelected"); 
					request.setAttribute("selPkgInfo","NoPackageSelected");  	
					
					
				}
				
				
				session.setAttribute("webSentPkgInfo",sentInfo);
				request.setAttribute("webSentPkgInfo",sentInfo);
				dropbox.setSentFirstTime(false);
				
			}else
			{
				
				//This done if a Pkg was selected...we need to show Contents of Package then
				if (sentFormData.getHighlitSentPkg() != null )	
				{	
					String selectPkg=sentFormData.getHighlitSentPkg().trim();
					
					
					if ( selectPkg.equals("NoPackageSelected"))
					{
						dropbox.getSentInfoBean().setSelectdSentPkg("NoPackageSelected");											   
						dropbox.getSentInfoBean().setSelectdPkgWithPkgId("NoPackageSelected");
						session.setAttribute("selPkgInfo","NoPackageSelected"); 
						request.setAttribute("selPkgInfo","NoPackageSelected");  	
						
						
						String selectFile=sentFormData.getUploadFile().trim();
						
						
					}else
					{
						
						long PkgId = Long.parseLong(fieldvalue);
						
						Vector fileData = (Vector)(dropbox.getSentInfoBean().getFileDescriptorsForPkgId(PkgId));
						dropbox.getSentInfoBean().setSentFileContents(fileData);
						dropbox.getSentInfoBean().setSentFileContentsLen(fileData.size());
						
						dropbox.getSentInfoBean().setSelectdPkgWithPkgId(fieldvalue);							
						dropbox.getSentInfoBean().setSelectdSentPkg(sentPkgName);
						
						session.setAttribute("selPkgInfo",selectPkg); 
						request.setAttribute("selPkgInfo",selectPkg); 
						
						
						
						
						
						
					}
					
				}
				
				
				
				
				
			}
			
			dropbox.setDraftFirstTime(true);
			
		}catch( NotLoggedInException nle)
		{							
			loggedInException = true;						
			
			
		}
		catch(Exception ee) {
			
			logerror(ee);
			createErrorFromException("Error while handing Sent Package panel", ee, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			forward = mapping.findForward("SentsFailure");
			
			
		} else {
			
			forward = mapping.findForward("SentsSuccess");
		}
		
		
		
		return (forward);
		
	}
	
	
	
	private String getSentPkgNameForId(String packId, UserDropbox dropbox) {
		String ret = "NoPackageSelected";
		Enumeration sentEnum=null;
		
		
		if ( ! packId.equals(ret)) {
			try {
				sentEnum=dropbox.listInOutSandBox(3); //query sent
				while(sentEnum.hasMoreElements()) {
					
					PackageInfo pkginfo = (PackageInfo)(sentEnum.nextElement());
					if (Long.toString(pkginfo.getPackageId()).equals(packId)) {
						ret = pkginfo.getPackageName();
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	
	/**
	 * 
	 * doSentPkgDelete Action method deletes a Package on the dropbox server
	 * 
	 */    
	
	
	public ActionForward doSentPkgDelete(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		
		
		String encoding = null;
		boolean loggedInException = false;
		
		
		try {	
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String currentpkg=dropbox.getSentInfoBean().getSelectdSentPkg();
			
			Enumeration sentEnum=dropbox.listInOutSandBox(3);
			
			
			
			String selectPkg=dropbox.getSentInfoBean().getSelectdPkgWithPkgId();							                
			long pkid=Long.parseLong(selectPkg.substring(selectPkg.indexOf("start#")+6,selectPkg.indexOf("end*")));
			
			
			dropbox.deletePackage(pkid);
			dropbox.setDraftFirstTime(true); 
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true; 
			
		}	
		catch (Exception e)
		{
			
			logerror(e);
			createErrorFromException("Error while deleting package(s) from Sent Panel", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("SentsFailure");
			
			
		} else {
			
			forward = mapping.findForward("SentsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	/**
	 * 
	 * doSentPackagesDelete Action method deletes Package(s) from the sent folder on the dropbox server
	 * 
	 */    
	
	
	public ActionForward doSentPackagesDelete(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		
		
		String encoding = null;
		boolean loggedInException = false;
		
		WebDboxSentPkgForm sentFormData = (WebDboxSentPkgForm) form;
		
		
		try {	
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String currentpkg=dropbox.getSentInfoBean().getSelectdSentPkg();
			
			String[] strArr = process(sentFormData.getPackToTrash().trim(),",");
			
			if(strArr[0].trim().equals("nopkgstodelete")){
				
				errors.add("error.sent.sentpackgnodelete",new ActionError("error.sent.sentpackgnodelete"));												
				saveErrors(request, errors);
				return mapping.findForward("SentsFailure"); 
				
			}
			
			if(strArr[0].trim().equals("pkgstodelete")){
				
				for(int i=1;i<strArr.length;i++)
				{
					String selectPkg=strArr[i].trim();
															
					dropbox.deletePackage(Long.parseLong(selectPkg));
					
				}			
			}
			
			sentFormData.setPackTrashOper("false");
			
			dropbox.setDraftFirstTime(true);
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true; 
			
		}	
		catch (Exception e)
		{
			
			
			logerror(e);
			createErrorFromException("Error while deleting Package", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("SentsFailure");
			
			
		} else {
			
			forward = mapping.findForward("SentsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	
	
	
	/**
	 * The Action method doFwdCreateSentPackage performs Action Chaining/pre-seeding for the doCreatePackage Action.
	 * 
	 */
	
	public ActionForward doFwdCreateSentPackage(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		WebDboxCreateSentPkgForm sntFormData = (WebDboxCreateSentPkgForm) form;
		boolean doEditRecipt = false;
		boolean loggedInException = false;
		
		
		
		
		try {
			
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && s.indexOf(":editRcpt:") >= 0) {
					doEditRecipt=true;
				}
			}		
			
			
			if (doEditRecipt == true){
				sntFormData.setPackageName("TestPackage");	
				
			}
			
			
			
			
			WebDboxCreateSentPkgInfo crtInf = dropbox.getCreatSntInfoBean();
			
			
			
			Hashtable hash = new Hashtable();
			hash = dropbox.listGroups();
			
			
			
			
			Enumeration enum = hash.keys();
			while(enum.hasMoreElements()) {
				WebDboxAclInfo acinf = new WebDboxAclInfo();
				String groupname=(String)(enum.nextElement());
				
				acinf.setAclName(SearchEtc.htmlEscape(groupname));  // groupencode
				acinf.setAclType("[GROUP]");
				if (!(crtInf.getRecipientsList().contains(acinf)))
					crtInf.getRecipientsList().add(acinf);		
			}
			
			
			
			if ( doEditRecipt == false ) {
				// JMC Storage Pool
				setupCRTInfo(dropbox, crtInf, -1, -999);
				
			}
			dropbox.setCreatSntInfoBean(crtInf);
			
			
			WebDboxDispGroupInfo grpInfo = new WebDboxDispGroupInfo();					
			grpInfo.setGroupInfoHash(dropbox.listGroups());
			
			dropbox.setGroupInfoBean(grpInfo); 												
			
			//store in webpage as attribute besides other attributes
			session.setAttribute("webGrpDispInfo",dropbox.getGroupInfoBean());
			request.setAttribute("webGrpDispInfo",dropbox.getGroupInfoBean());	
			
			
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			
		createErrorFromException("Error while handling Sent package panel", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("SentsFailure");
			
			
			
		} else {
			
			if ( doEditRecipt == true ){
				forward = mapping.findForward("RcptSuccess");	
				
			}
			
			else
			{
				forward = mapping.findForward("SentsSuccess");
				
			} 
		}
		
		
		return (forward); 
	}
	
	/**
	 * The Action method doCreatePackage performs Action of creating a new package.
	 * besides updating acl stuff
	 * 
	 */
	
	public ActionForward doCreateSentPackage(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		WebDboxCreateSentPkgForm sntFormData = (WebDboxCreateSentPkgForm) form;
		String encoding = null;
		String addFiles = null;
		boolean loggedInException = false;
		boolean showCrossCompWarning=false;
		
		try {
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			//										
			String packNameStr=((String)(sntFormData.getPackageName()));
			boolean itarpackage=sntFormData.isContainsItarCbx();
			
			//for creating itar pkgs only
			if (itarpackage && !( dropbox.getCreatSntInfoBean().isEditPackageRcpt())){
				packNameStr="ITAR_"+packNameStr;
				
			}
			
			//need to set the highlighted pkg context..case where create and add files is done
			dropbox.getSentInfoBean().setSelectdSentPkg(packNameStr);
			session.setAttribute("webSentPkgInfo",dropbox.getSentInfoBean());
			request.setAttribute("webSentPkgInfo",dropbox.getSentInfoBean());
			
			
			String iccIdStr=((String)(sntFormData.getIccid()));
			
			String accList=((String)(sntFormData.getAclList()));
			
			
			String accListRcpt=((String)(sntFormData.getAclListRcpt()));
			
			if(accList.equalsIgnoreCase("empty") || accListRcpt.equalsIgnoreCase("empty") )
			{
				//if user was editing recipients and deleted em all and clickd submit..
				//options.js::updateaclfield sets fields to str empty..need to check if
				//the bean still supplies the sendtolist with acl info..
				
				WebDboxCreateSentPkgInfo crtInf = (WebDboxCreateSentPkgInfo)(dropbox.getCreatSntInfoBean());											
				crtInf.setSendList(new Vector());
				dropbox.setCreatSntInfoBean(crtInf);
				
				
				//store in webpage as attribute besides other attributes
				session.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
				request.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
				
				
				
				errors.add("aclList", new ActionError("error.aclList.value"));
				saveErrors(request, errors);
				return mapping.findForward("SentsFailure"); 
				
			}
			
			/////////////////validate groups //////////////////
			String[] strArrUsrGrp = process(accListRcpt,","); // we use the "  ,_U_u1,_G_g1.."
			
			
			
			String usrGrpRcpt=null;
			Hashtable grouphash = new Hashtable();
			grouphash = dropbox.listGroups();
			
			
			
			//first is a empty str. All ACL's are added from   ,_U_u1,_G_g1..list
			for(int j=1;j<strArrUsrGrp.length;j++)
			{
				
				if ( strArrUsrGrp[j].trim().indexOf("_U_") == 0 )
				{
					usrGrpRcpt=strArrUsrGrp[j].substring(3); 
				}else
				{
					boolean groupexists=false;
					usrGrpRcpt=strArrUsrGrp[j];
					
					
					Enumeration groupsenum = grouphash.keys();
					while(groupsenum.hasMoreElements()) {
						
						String groupname=(String)(groupsenum.nextElement());
						
						
						if (groupname.trim().equals(usrGrpRcpt.trim().substring(3,usrGrpRcpt.length())))
						{
							groupexists=true;
							break;   
						}
						
					}
					if (!groupexists) {
						
						errors.add("error.grouporuser.unknown",new ActionError("error.grouporuser.unknown","test","Group or User name provided does not exist or not visibile.Please Retry creating a new group or user with that name"));
						saveErrors(request, errors);
						
						return mapping.findForward("SentsFailure");
					}
					
					
					
				}
				
				
				
			}
			
			
			
			addFiles=((String)(sntFormData.getAddFiles()));
			
			long expireval= daysDifference(new Date(),new Date(sntFormData.getExpireDate().trim()));   
			//Finally, the default expiration will be either the actual expiration
			// (if not 0) or defDays from now.
			if (expireval <= 0) {
				expireval= dropbox.getSelectedPoolInfo().getPoolDefaultDays();
			}
			
			long pkid=0;	
			long poolid=0;
			
			
			
			if ( dropbox.getCreatSntInfoBean().isEditPackageRcpt() == false )
			{	
				
				//In case the package is being created anew...
				if (checkIfPackageNameValid(dropbox,packNameStr)){
					
					errors.add("error.draft.draftexists",new ActionError("error.draft.draftexists"));
					
					saveErrors(request, errors);
					return mapping.findForward("SentsFailure"); 
					
				}else {
					if (!dropbox.isItarSessionCertified())
						dropbox.setOption(DropboxAccess.ItarSessionCertified,"FALSE");
					else
						dropbox.setOption(DropboxAccess.ItarSessionCertified,"TRUE");
					dropbox.createPackage(((String)(sntFormData.getPackageName())).trim(),sntFormData.getPkgDesc().trim(),dropbox.getSelectedPoolInfo().getPoolId(),itarpackage?PackageInfo.ITAR:0, itarpackage?PackageInfo.ITAR:0);     
					
					
				}
				
				
				
				Enumeration sentEnum=dropbox.listInOutSandBox(3);
				
				while(sentEnum.hasMoreElements()) {
					PackageInfo finfo = (PackageInfo)sentEnum.nextElement();
					if (finfo.getPackageName().equals(packNameStr)) {
						pkid = finfo.getPackageId();
						poolid = finfo.getPackagePoolId();
						
						break;
					}
				}
				
			}
			else
			{
				//In case the package is being edited
				
				dropbox.getCreatSntInfoBean().setEditPackageRcpt(false);
				dropbox.getCreatSntInfoBean().setDisablePkgNameField(false);
				
				Enumeration sentEnum=dropbox.listInOutSandBox(3);
				
				while(sentEnum.hasMoreElements()) {
					PackageInfo finfo = (PackageInfo)sentEnum.nextElement();
					if (finfo.getPackageName().equals(packNameStr)) {
						pkid = finfo.getPackageId();
						poolid = finfo.getPackagePoolId();
						
						break;
					}
				}
				
				dropbox.deleteAclsForPackage(pkid);
			}
			
			
			
			//,_U_ user@in.ibm.com,_U_ u2	
			
			
			String[] strArr = process(accListRcpt,","); // we use the "  ,_U_u1,_G_g1.."
			
			
			
			String newrecipient=null;
			
			//first is a empty str. All ACL's are added from   ,_U_u1,_G_g1..list
			for(int j=1;j<strArr.length;j++)
			{
				
				if ( strArr[j].trim().indexOf("_U_") == 0 )
				{
					newrecipient=strArr[j].substring(3); 
				}else
				{
					newrecipient=strArr[j];
				}
				
				
				
				
				try {
					
					
					dropbox.addAcl(pkid,newrecipient.trim());
                    Vector usrsVect=dropbox.lookupUser(newrecipient,true);
                    
				} catch (Exception e1) {																							
					
					//added as these 2 lines were re-enabling the edit modes and packname field..
					dropbox.getCreatSntInfoBean().setEditPackageRcpt(true);
					dropbox.getCreatSntInfoBean().setDisablePkgNameField(true);
					sntFormData.setAclList("");
					sntFormData.setAclListRcpt("");
					
					
					//errors.add("error.grouporuser.unknown",new ActionError("error.grouporuser.unknown"));
					//saveErrors(request, errors);
                                        logerror(e1);
                                        createErrorFromException("", e1, errors);
					//return mapping.findForward("SentsFailure"); 
				}
				
			}
			
			
			
			
			//stuff the bean with userinfo and groupinfo	
			WebDboxCreateSentPkgInfo crtInf = (WebDboxCreateSentPkgInfo)(dropbox.getCreatSntInfoBean());
			
			String[] sendTo = process(accList,","); // we use the " ,[USER] u1,[GROUP] g1.."
			
			
			for(int j=1;j<sendTo.length;j++)
			{
				WebDboxAclInfo acinf = new WebDboxAclInfo();
				newrecipient=(String)(sendTo[j]);
				
				if ( newrecipient.indexOf("[USER]") == 0 )
				{
					
					acinf.setAclName(newrecipient.substring(7));
					acinf.setAclType("[USER]");
					
				}
				
				if (!(crtInf.getRecipientsList().contains(acinf)))
					crtInf.getRecipientsList().add(acinf);																					
				
				
			}
			
			
			
			
			// need to check for duplicates
			//need to query groups visible to dropbox user. These groups are appended to sendto List for JSP
			
			//for the "Available Individual/Groups"
			Hashtable hash = new Hashtable();
			hash = dropbox.listGroups(); // lists groups on server
			
			
			
			Enumeration enum = hash.keys();
			while(enum.hasMoreElements()) {
				WebDboxAclInfo acinf = new WebDboxAclInfo();
				String groupname=(String)(enum.nextElement());
				
				acinf.setAclName(SearchEtc.htmlEscape(groupname)); // groupencode
				acinf.setAclType("[GROUP]");
				if (!(crtInf.getRecipientsList().contains(acinf)))
					crtInf.getRecipientsList().add(acinf);		
			}
			
			// JMC Storage Pool
			setupCRTInfo(dropbox, crtInf, poolid, expireval);
			crtInf.setRcptEnum(crtInf.getRecipientsList().elements());
			dropbox.setCreatSntInfoBean(crtInf);
			
			
			
			session.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
			request.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
			
			dropbox.setPackageExpiration(pkid,expireval);
			
			
			PackageInfo pinfo = dropbox.queryPackage(pkid);
			boolean sendNotification=pinfo.getPackageSendNotification();
			boolean requestReturnReceipt=pinfo.getPackageReturnReceipt();
			
			
			int pkgMsk = 0;
			int pkgVal = 0;
			
			if (sendNotification != sntFormData.getCbx1()) {								
				
				pkgMsk |= PackageInfo.SENDNOTIFY;
				if (sntFormData.getCbx1()) {
					pkgVal |= PackageInfo.SENDNOTIFY;
				}
				
			}								
			
			if (requestReturnReceipt != sntFormData.getCbx2()) {								
				
				pkgMsk |= PackageInfo.RETURNRECEIPT;
				if (sntFormData.getCbx2()) {
					pkgVal |= PackageInfo.RETURNRECEIPT;
				}
				
			}
			
			
			pkgMsk &= 0xFF;
			pkgVal &= 0xFF;
			
			
			if (pkgMsk != 0) 
				dropbox.setPackageFlags(pkid,pkgMsk,pkgVal);
			
			
			//	///////////////////////// cross recipients check /////////////////////////
			
			
			
			Vector rcptCompanies=dropbox.queryPackageAclCompanies(pkid);
			String company = dropbox.getTokenUserCompany(false);
			
			 
				 
			 if (rcptCompanies != null)
			 {	
			 	Collections.sort(rcptCompanies);			 
			 }
			 
			 Iterator it = rcptCompanies.iterator();
		      String repc = "";
		      int num = 0;
		      int rnum = 0;
		      while(it.hasNext()) {
		         String lcomp = (String)it.next();
		         
		        // If this is NOT the first one add a comma. Used to do it by
		        //  length, but if there is an empty string company <in test 
		        //  there is> then it does not have any indication in the list
		         if (rnum++ > 0)  repc += "," + lcomp;
		         else   repc  = lcomp;
		         if (!lcomp.equals(company)) num++;
		      }
		      
		      if (num > 1) {
		      	//System.out.println( "Note: this package has " + num + " companies represented in the ACL list besides your own:");
		      	//System.out.println("      Companies: (" + repc + ")");
		      	
		      	crtInf.setCrossRcptList(rcptCompanies); //SEED IN DATA FOR THE SENTCROSSCOMP.JSP PAGE
		      	crtInf.setCrossRcptListLen(num);
		      	showCrossCompWarning=true;
		      	crtInf.setCrossRcptShow("showrcpts");   /// UPDATEACLFIELD TO BE CALLED CORRESPODNINGLY
		      	sntFormData.setCrossRcptWarning("showrcpts");  /// 
		      }
			
			
			crtInf.setRcptEnum(crtInf.getRecipientsList().elements());
			dropbox.setCreatSntInfoBean(crtInf);
			
			
			//store in webpage as attribute besides other attributes
			session.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
			request.setAttribute("webCrtDraftPkgInfo",dropbox.getCreatDftInfoBean());
			
			
			
			
			/////////////////////////////////////////////////////////////////////////////
			
			
			
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true; 
			
		}	
		catch (Exception e)
		{
			
			logerror(e);
			
			//errors.add("error.sents.severe",new ActionError("error.sents.severe"));				
			createErrorFromException("Error while creating package", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("SentsFailure");
			
			
		} else {
			
			if (addFiles.equals("true"))
			{
				
				forward = mapping.findForward("AddFilesSuccess");	
			} else
				if  ( showCrossCompWarning == true)
			{
				return mapping.findForward("ShowCrossComp");
			}
			
			
			else
				forward = mapping.findForward("SentsSuccess");
		}
		
		// Finish with
		return (forward); 
	}
	
	
	
	
	
	
	
	/**
	 * doEditRecipientsCrtPkg Action method edits an existing Package on the dropbox server
	 * 
	 */
	
	public ActionForward doEditRecipientsCrtSentPkg(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		WebDboxCreateSentPkgForm sntFormData = (WebDboxCreateSentPkgForm) form;
		String newrecipient=null;
		boolean loggedInException = false;
		
		
		try {
			
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			setItarAttributes(request,response,dropbox);
			
			String packNameStr=dropbox.getSentInfoBean().getSelectdSentPkg();
			
			//pre-populate form data
			sntFormData.setPackageName(packNameStr);  //store package name
			sntFormData.setPackNameField("disable");
			
			boolean sendNotification=false;
			boolean requestReturnReceipt=false;
			boolean isPkgItar=false;
			String pkgDescp="";
		
			long expireval = -999; // JMC
			String expire=null;
			
			
			Enumeration editsentEnum=dropbox.listInOutSandBox(3);
			long pkid=0;
			long poolid=0;
			while(editsentEnum.hasMoreElements()) {
				PackageInfo pinfo = (PackageInfo)editsentEnum.nextElement();
				if (pinfo.getPackageName().equals(packNameStr)) {
					pkid = pinfo.getPackageId();
					poolid = pinfo.getPackagePoolId();
					sendNotification=pinfo.getPackageSendNotification();
					requestReturnReceipt=pinfo.getPackageReturnReceipt();
					isPkgItar=pinfo.isPackageItar();
					
					if (isPkgItar)
					{
						if ( !dropbox.isItarSessionCertified())
						{
							throw new Exception("Webdropbox: Editing Package: Can't access ITAR package data. Please certify your session in Options");
						}
						
					}
					
					pkgDescp=pinfo.getPackageDescription();
					
					try {
						
						
						Vector pools = dropbox.queryStoragePoolInformation();
						dropbox.getCreatSntInfoBean().setStoragePools(pools);
						
						PoolInfo pi = dropbox.getSelectedPoolInfo();
						
						
						Iterator it = pools.iterator();
						while(it.hasNext()) {
							PoolInfo lpi = (PoolInfo)it.next();
							if (poolid == lpi.getPoolId()) {
								pi = lpi;
							}
						}
						
						// The pool name is actually the pool id
						String poolname = pi.getPoolName();
						dropbox.setSelectedPoolInfo(pi);
						dropbox.getCreatSntInfoBean().setSelectedStoragePool(pi);
						
						
						
						
					} catch(Exception ee) {
						logerror("Error setting storagepool");
						logerror(ee);
					}
					expire=new Date(pinfo.getPackageExpiration()).toGMTString();  
					break;
				}
			}
			
			// store package acls in sendto List
			if ( pkid != 0 && dropbox.isCrossCompEdit() == false )
			{
				Enumeration enumofacls=dropbox.queryAcls(pkid); //query existing package acls
				
				//stuff the bean with userinfo and groupinfo	
				WebDboxCreateSentPkgInfo crtInf = (WebDboxCreateSentPkgInfo)(dropbox.getCreatSntInfoBean());
				if ( crtInf.getSendList() != null )
					crtInf.getSendList().clear();
				
				StringBuffer sbufAccList = new StringBuffer();
				sbufAccList.append(" ");
				StringBuffer sbufAccListRcpt = new StringBuffer();
				sbufAccListRcpt.append(" ");
				
				while(enumofacls.hasMoreElements())
				{
					
					
					WebDboxAclInfo acinf = new WebDboxAclInfo();
					String recipient=(String)(enumofacls.nextElement());
					
					
					
					
					if ( recipient.indexOf("_G_") == 0 )
					{
						acinf.setAclName(SearchEtc.htmlEscape(recipient.substring(3,recipient.length()))); // groupencode
						acinf.setAclType("[GROUP]");
						
						sbufAccListRcpt.append(","+recipient);
						
						
					}else if ( recipient.indexOf("_P_") == 0)
					{
						acinf.setAclName(recipient.substring(3,recipient.length()));
						acinf.setAclType("[PROJECT]");
						
						sbufAccListRcpt.append(","+recipient);
						
						
					}else 
					{
						acinf.setAclName(recipient);
						acinf.setAclType("[USER]");
						
						sbufAccListRcpt.append(","+"_U_"+recipient);
						
					}
					
					sbufAccList.append(","+acinf.getAclType()+" "+acinf.getAclName());
					
					
					
					if (!(crtInf.getSendList().contains(acinf)))
						crtInf.getSendList().add(acinf);	
					
					
				} 
				
				
				sntFormData.setContainsItarCbx(isPkgItar);
				sntFormData.setPkgDesc(pkgDescp);
                
				request.setAttribute("sntPkgDesLen",String.valueOf(pkgDescp.length()));
				int leftChar=1024-pkgDescp.length();
				request.setAttribute("sntPkgDesLeftChar",String.valueOf(leftChar));

				sntFormData.setAclList((String)(sbufAccList.toString()));
				sntFormData.setAclListRcpt((String)(sbufAccListRcpt.toString()));
				
				sntFormData.setCbx1(sendNotification);
				sntFormData.setCbx2(requestReturnReceipt);
				
				
				
				crtInf.setPkgExpireDate("Expires on :"+dropbox.mmddyyyy(expire));
				crtInf.setSelectedStoragePool(dropbox.getSelectedPoolInfo());
				
				
				crtInf.setDisablePkgNameField(true); //disable text editing for Package name
				crtInf.setEditPackageRcpt(true);    //enable edit recipients mode for use in doCreatePkg
				
				String calendarEnddate=dropbox.prepareExpiration(dropbox.getCreatSntInfoBean().getSelectedStoragePool().getPoolMaxDays(),dropbox.getCreatSntInfoBean().getSelectedStoragePool().getPoolDefaultDays());
				dropbox.getCreatSntInfoBean().setCalendarEndDate("Expires on :"+calendarEnddate);
				
				crtInf.setSendEnum(crtInf.getSendList().elements()); //store the acls in send to list
				dropbox.setCreatSntInfoBean(crtInf);
				
				
				
				
				//	store in webpage as attribute besides other attributes
				session.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
				request.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
				
				dropbox.setDraftFirstTime(true); //Added for CSRFIX(IBMCC00009471) Editing Sent Recipients Prob..
				
			}  
// store package acls in sendto List
			if ( pkid != 0 && dropbox.isCrossCompEdit() == true )
			{
				Enumeration enumofacls=dropbox.queryAcls(pkid); //query existing package acls
				
				//stuff the bean with userinfo and groupinfo	
				WebDboxCreateSentPkgInfo crtInf = (WebDboxCreateSentPkgInfo)(dropbox.getCreatSntInfoBean());
				if ( crtInf.getSendList() != null )
					crtInf.getSendList().clear();

                             ProcessSendToListForSent(dropbox,pkid,crtInf,isPkgItar);
			     crtInf.setCrossRcptShow("showrcpts");   /// to strip the **[...]** in updateaclfield of javascripts..
			     sntFormData.setCrossRcptWarning("showrcpts"); /// to strip the **[...]** in updateaclfield of javascripts..
				
			
				
				
				sntFormData.setContainsItarCbx(isPkgItar);
				sntFormData.setPkgDesc(pkgDescp);
                
				request.setAttribute("sntPkgDesLen",String.valueOf(pkgDescp.length()));
				int leftChar=1024-pkgDescp.length();
				request.setAttribute("sntPkgDesLeftChar",String.valueOf(leftChar));

				//sntFormData.setAclList((String)(sbufAccList.toString()));
				//sntFormData.setAclListRcpt((String)(sbufAccListRcpt.toString()));
				
				sntFormData.setCbx1(sendNotification);
				sntFormData.setCbx2(requestReturnReceipt);
				
				
				
				crtInf.setPkgExpireDate("Expires on :"+dropbox.mmddyyyy(expire));
				crtInf.setSelectedStoragePool(dropbox.getSelectedPoolInfo());
				
				
				crtInf.setDisablePkgNameField(true); //disable text editing for Package name
				crtInf.setEditPackageRcpt(true);    //enable edit recipients mode for use in doCreatePkg
				
				String calendarEnddate=dropbox.prepareExpiration(dropbox.getCreatSntInfoBean().getSelectedStoragePool().getPoolMaxDays(),dropbox.getCreatSntInfoBean().getSelectedStoragePool().getPoolDefaultDays());
				dropbox.getCreatSntInfoBean().setCalendarEndDate("Expires on :"+calendarEnddate);
				
				crtInf.setSendEnum(crtInf.getSendList().elements()); //store the acls in send to list
				dropbox.setCreatSntInfoBean(crtInf);
				
				
				
				
				//	store in webpage as attribute besides other attributes
				session.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
				request.setAttribute("webCrtSentPkgInfo",dropbox.getCreatSntInfoBean());
				
				dropbox.setDraftFirstTime(true); //Added for CSRFIX(IBMCC00009471) Editing Sent Recipients Prob..
				
			}  
			
			
			
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			
			logerror(e);
			
			createErrorFromException("Error while editing package recipients", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("SentsFailure");
			
			
			
		} else {
			
			forward = mapping.findForward("SentsSuccess");
		}
		
		return (forward); 
	}
	
	/**
	 * The Action method doUpdateSentPkgView remembers the last sent operation done and builds the
	 * appropriate sent webpage in context.
	 * 
	 */
	public ActionForward doUpdateSentPkgView(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		
		WebDboxSentPkgForm sentFormData = (WebDboxSentPkgForm) form;
		boolean loggedInException = false;
		int totalSentPkgs=0;
		
		try {	
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String currentSentPkgInContext=dropbox.getSentInfoBean().getSelectdPkgWithPkgId();
			
			String sentPkgName=dropbox.getSentInfoBean().getSelectdSentPkg(); //Added for CSRFIX(IBMCC00009471) Editing Sent Recipients Prob..	
			
			
			WebDboxSentPkgInfo sntInfo = new WebDboxSentPkgInfo();//dropbox.getSentInfoBean(); //get existing bean..
			
			sntInfo.setSelectdSentPkg(sentPkgName);//Added for CSRFIX(IBMCC00009471) Editing Sent Recipients Prob..
			sntInfo.setSelectdPkgWithPkgId(currentSentPkgInContext);//Added for CSRFIX(IBMCC00009471) Editing Sent Recipients Prob..	
			
			if (currentSentPkgInContext == null ) {														
				errors.add("error.sents.severe",new ActionError("error.sents.severe"));
				saveErrors(request, errors);																										
				return mapping.findForward("SentsFailure");
				
			}
			
			
			totalSentPkgs=dropbox.retreiveSentContents(sntInfo);
			
			
			sntInfo.setTotSentPkgs(totalSentPkgs);
			dropbox.sortSentPkgInfo(ETSComparator.getSortOrder(ETSComparator.SORT_BY_PKG_STR),ETSComparator.SORT_DESC,sntInfo);
			
			long PkgId=Long.parseLong(currentSentPkgInContext);												
			
			
			sentFormData.setSentPkgToLit(currentSentPkgInContext);//replaces alls spaces in long names with _
			
			sntInfo.setSelectdSentPkg(sentPkgName);
			
			
			
			
			
			
			
			Vector fileData = (Vector)(sntInfo.getFileDescriptorsForPkgId(PkgId));
			sntInfo.setSentFileContents(fileData);
			sntInfo.setSentFileContentsLen(fileData.size());												
			
			
			String pkgName_index=null;
			String pkgName=null;
			int pkgIndex=0;
			
			
			
			pkgIndex= getIndexForPkgIdFromSortedSentVector(String.valueOf(PkgId),sntInfo);					
			
			
			int view=0;
			int start=0;
			int end=0;
			
			int remainder = pkgIndex%6;
			if (remainder==0) {
				view=pkgIndex/6 ;
			}else{
				view=(pkgIndex/6)+1;
			}
			
			
			start=(view*6-6)+1;
			end=(view*6 <= totalSentPkgs ? view*6 : totalSentPkgs);
			
			
			sntInfo.setStartSentIndex(start);
			sntInfo.setEndSentIndex(end);
			sntInfo.setTotSentPkgs(totalSentPkgs);
			
			sntInfo.setPackageRangeForDisplay(start,end);
			
			
			
			
			
			dropbox.setSentInfoBean(sntInfo); 
			
			
			session.setAttribute("webSentPkgInfo",dropbox.getSentInfoBean());
			request.setAttribute("webSentPkgInfo",dropbox.getSentInfoBean());
			dropbox.setSentFirstTime(false);
			
			dropbox.setCreateSentFirstTime(true); //TBDEL
			
			
			
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;												   
		}	
		catch (Exception e)
		{
			
			logerror(e);
			
			createErrorFromException("Error handling Sent Package panel", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}			
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("SentsFailure");
			
			
		} else {
			
			forward = mapping.findForward("SentsSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	
	/**
	 * The Action method doDispGroupInfo displays the visbility & listablity for selected group.
	 
	 */
	public ActionForward doDispGroupInfo(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		boolean loggedInException = false;
		
		try {
			WebDboxDispGroupInfoForm disGrpFormData = (WebDboxDispGroupInfoForm) form;
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			setItarAttributes(request,response,dropbox);
			
			WebDboxDispGroupInfo grpInfo = new WebDboxDispGroupInfo();														
			
			dropbox.setGroupInfoBean(grpInfo); 												
			
			session.setAttribute("webGrpDispInfo",dropbox.getGroupInfoBean());
			request.setAttribute("webGrpDispInfo",dropbox.getGroupInfoBean());
			
		} catch (Exception e) {
			
			loggedInException = true;
		
		}
		if (loggedInException == true) 
		{
			return mapping.findForward("sessionExpired");
		}			
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("DispGroupInfoFailure");
			
			
		} else {
			
			forward = mapping.findForward("DispGroupInfoSuccess");
		}
		
		
		
		return (forward);
		
	}	
	
	
	
	
	
	/** 
	 * doMovePkgToTrash Action method hides Packages from the Inbox folder view and moves items
	 * to the Trash folder view on the dropbox server
	 * 
	 */
	
	public ActionForward doMovePkgToTrash(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		
		
		WebDboxInboxForm ibxFormData = (WebDboxInboxForm) form;
		boolean loggedInException = false;
		
		
		
		try {	 
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			setItarAttributes(request,response,dropbox);
			
			String[] strArr = process(ibxFormData.getPackToTrash().trim(),",");
			
			if(strArr[0].trim().equals("nopkgstodelete")){
				
				errors.add("error.inbox.inboxpackgnodelete",new ActionError("error.inbox.inboxpackgnodelete"));												
				saveErrors(request, errors);
				return mapping.findForward("InboxFailure"); 
				
			}
			
			
			//pkgstodelete,packagenamestart#201end*,packagenamestart#203end*,..
			if(strArr[0].trim().equals("pkgstodelete")){
				
				for(int i=1;i<strArr.length;i++)
				{
					String selectPkg=strArr[i].trim();
					
					//long pkgID=Long.parseLong(selectPkg.substring(selectPkg.indexOf("start#")+6,selectPkg.indexOf("end*")));								
					long pkgID=Long.parseLong(selectPkg);
					dropbox.markPackage(pkgID,true);
					
				}			
			}
			
			ibxFormData.setPackTrashOper("false");
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			
			logerror(e);						
			createErrorFromException("Error while moving packages to trash", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}			
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("InboxFailure");
			
			
		} else {
			
			forward = mapping.findForward("InboxSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	/**
	 * 
	 * The doTrash Action sends a request to the dropbox server to fetch the Trash Contents.
	 * Population of webTrashInfo Bean is also done.
	 *  	
	 */
	
	public ActionForward doTrash(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		
		WebDboxTrashForm tshFormData = (WebDboxTrashForm) form;
		
		
		String fieldname  = tshFormData.getModifyFieldName();
		String fieldvalue = tshFormData.getModifyFieldValue();
		
		
		
		
		boolean loggedInException=false;
		
		try {
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			//fieldvalue stores the id..do a lookup and fetch the trashd pkgname
			String trashPkgName=getTrashPkgNameForId(fieldvalue,dropbox); //do a lookup for the pkgname
			
			
			if ( ! fieldvalue.equals(null) )
			{
				tshFormData.setHighlitPkg(trashPkgName);
				tshFormData.setTrashPkgToLit(fieldvalue);
			}  
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && s.indexOf(":refresh:") >= 0) {
					dropbox.setTrashFirstTime(true);
					tshFormData.setHighlitPkg("NoPackageSelected");
					tshFormData.setTrashPkgToLit("NoPackageSelected");
				}
			}
			
			
			
			
			if ( dropbox.isTrashFirstTime())
			{ 							
				
				int totalRcvdPkgs=0;
				int start=0;
				int end=0;				
				
				WebDboxTrashInfo tshInfo = new WebDboxTrashInfo();
				totalRcvdPkgs=dropbox.retreiveTrashContents(tshInfo);
				dropbox.sortTrashPkgInfo(ETSComparator.getSortOrder(ETSComparator.SORT_BY_PKG_DATE_COMMIT_STR),ETSComparator.SORT_DESC,tshInfo);
				
				
				WebDboxDraftPkgInfo dfinf = new WebDboxDraftPkgInfo();	
				request.getSession(false).setAttribute("webDraftPkgInfo",dfinf);
				
				if ( totalRcvdPkgs > 6 )
				{   
					//set indices for more than 6 pkgs
					start=1;
					end=6;
					
				}else { 
					//set indices for less than 6 pkgs
					start=1;	
					end=totalRcvdPkgs;								 
				}
				tshInfo.setStartIndex(start);
				tshInfo.setEndIndex(end);
				tshInfo.setTotRcvdPkgs(totalRcvdPkgs);
				
				tshInfo.setPackageRangeForDisplay(start,end);
				dropbox.setTrashInfoBean(tshInfo); 
				
				
				String selectPkg=tshFormData.getHighlitPkg().trim();
				
				if ( selectPkg.equals("NoPackageSelected"))
				{
					dropbox.getTrashInfoBean().setSelectdPkg(selectPkg);	
					session.setAttribute("selPkgInfo","NoPackageSelected"); 
					request.setAttribute("selPkgInfo","NoPackageSelected");  	
					
				}
				
				//store in webpage as attribute besides other attributes
				session.setAttribute("webTrashInfo",tshInfo);
				request.setAttribute("webTrashInfo",tshInfo);
				dropbox.setTrashFirstTime(false);
				
			}else
			{
				
				
				
				
				//This done if a Pkg was selected...we need the show Contents of Package then
				if (tshFormData.getHighlitPkg() != null )	
				{	
					String selectPkg=tshFormData.getHighlitPkg().trim();
					
					
					if ( selectPkg.equals("NoPackageSelected"))
					{
						dropbox.getTrashInfoBean().setSelectdPkg("NoPackageSelected");	
						dropbox.getTrashInfoBean().setSelectdPkgWithPkgId("NoPackageSelected");
						
						session.setAttribute("selPkgInfo","NoPackageSelected"); 
						request.setAttribute("selPkgInfo","NoPackageSelected");	
						
						
						String selectFile=tshFormData.getUploadFile().trim();
						
						
					}else
					{
						
						String selectFile=tshFormData.getUploadFile().trim();									
						
						long PkgId = Long.parseLong(fieldvalue);
						
						Vector fileData = (Vector)(dropbox.getTrashInfoBean().getFileDescriptorsForPkgId(PkgId));
						dropbox.getTrashInfoBean().setTrashFileContents(fileData);
						
						
						dropbox.getTrashInfoBean().setSelectdPkgWithPkgId(fieldvalue);							
						dropbox.getTrashInfoBean().setSelectdPkg(trashPkgName);
						
						
						session.setAttribute("selPkgInfo",selectPkg); 
						request.setAttribute("selPkgInfo",selectPkg);  	
					}
					
				}
				
				
				
				
				
			}
			
			dropbox.setDraftFirstTime(true);
			dropbox.setFirstTime(true);
			
		}catch( NotLoggedInException nle)
		{							
			loggedInException=true;
			
		}	
		catch (Exception e)
		{
			logerror(e);
			createErrorFromException("Error while handing Trash panel", e, errors); 
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("TrashFailure");
			
			
		} else {
			
			forward = mapping.findForward("TrashSuccess");
		}
		
		
		return (forward);
		
		
	}
	
	
	
	
	private String getTrashPkgNameForId(String packId, UserDropbox dropbox) {
		String ret = "NoPackageSelected";
		Enumeration trashEnum=null;
		
		
		if ( ! packId.equals(ret)) {
			try {
				trashEnum=dropbox.listInOutSandBox(1); //query trash
				while(trashEnum.hasMoreElements()) {
					PackageInfo pkginfo = (PackageInfo)(trashEnum.nextElement());
					if (Long.toString(pkginfo.getPackageId()).equals(packId)) {
						ret = pkginfo.getPackageName();
						break;
					}
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * doGenTrashIndex Action method generates the prev&next indices for paging  
	 * 
	 */		
	
	
	public ActionForward doGenTrashIndex(	
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		WebDboxTrashForm model = (WebDboxTrashForm) form;
		boolean loggedInException = false;
		
		
		try {
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			if (mapping != null) {
				String s = mapping.getParameter();
				if (s != null && s.indexOf(":prev:") >= 0) {
					
					
					WebDboxTrashInfo inf = (WebDboxTrashInfo)session.getAttribute("webTrashInfo");
					
					inf.setEndIndex((inf.getStartIndex()-1));
					inf.setStartIndex((inf.getStartIndex()>= 1 ) ? inf.getStartIndex()-6 : 1 );
					
					inf.setPackageRangeForDisplay(inf.getStartIndex(),inf.getEndIndex());
					
					request.setAttribute("webTrashInfo",inf);
					session.setAttribute("webTrashInfo",inf);													
					
					dropbox.setTrashInfoBean(inf);
					
					
					
					
					
				}else
					if (s != null && s.indexOf(":next:") >= 0) {
						
						WebDboxTrashInfo inf = (WebDboxTrashInfo)session.getAttribute("webTrashInfo");
						
						
						inf.setStartIndex( ( inf.getEndIndex() < inf.getTotRcvdPkgs()) ? inf.getEndIndex()+1:inf.getEndIndex());
						inf.setEndIndex((inf.getEndIndex()+6) >= inf.getTotRcvdPkgs() ? inf.getTotRcvdPkgs():inf.getEndIndex()+6);				
						
						
						inf.setPackageRangeForDisplay(inf.getStartIndex(),inf.getEndIndex());
						
						request.setAttribute("webTrashInfo",inf);
						session.setAttribute("webTrashInfo",inf);
						dropbox.setTrashInfoBean(inf);										
						
					}
					
			}
			
			
			doTrash(mapping,form,request, response);
			
			
		}catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}
		catch (Exception e)
		{
			
			logerror(e);
			createErrorFromException("Error while handing Trash panel index creation", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("TrashFailure");
			
			
		} else {
			
			forward = mapping.findForward("TrashSuccess");
		}
		
		
		return (forward); 
	}
	
	
	
	
	/** 
	 * doMovePkgToInbox Action method hides Packages from the Inbox folder view and moves items
	 * to the Trash folder view on the dropbox server
	 * 
	 */
	
	public ActionForward doMovePkgToInbox(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		
		
		WebDboxTrashForm tshFormData = (WebDboxTrashForm) form;
		boolean loggedInException = false;
		
		
		
		try {	 
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			String[] strArr = process(tshFormData.getPackToTrash().trim(),",");
			
			if(strArr[0].trim().equals("nopkgstodelete")){
				
				errors.add("error.trash.trashpackgnodelete",new ActionError("error.trash.trashpackgnodelete"));												
				saveErrors(request, errors);
				return mapping.findForward("TrashFailure"); 
				
			}
			
			
			if(strArr[0].trim().equals("pkgstodelete")){
				
				for(int i=1;i<strArr.length;i++)
				{
					String selectPkg=strArr[i];																
					long pkgID=Long.parseLong(selectPkg);
					String currentPkgName = dropbox.getTrashInfoBean().getPackageNameForPackageId(String.valueOf(pkgID));																
					dropbox.markPackage(pkgID,false);
					
				}			
			}
			
			tshFormData.setPackTrashOper("false");
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;
			
		}	
		catch (Exception e)
		{
			
			logerror(e);
			createErrorFromException("Error while moving package from Trash to Inbox", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}			
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("TrashFailure");
			
			
		} else {
			
			forward = mapping.findForward("TrashSuccess");
		}
		
		
		return (forward); 
	}
	
	
	public ActionForward doOpenCalendar(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		
		
		
		boolean result = false;
		final String SOURCE_OBJ = "sourceObj";
		final String VIEW_MONTH = "viewMonth";
		final String VIEW_YEAR = "viewYear";
		final String VALID_DATE_START = "validStart";
		final String VALID_DATE_END = "validEnd";
		final String ALLOW_PAST_DATES = "allowPast";
		
		request.setAttribute("sourceObj", SOURCE_OBJ);
		request.setAttribute("viewMonth", VIEW_MONTH);
		request.setAttribute("viewYear", VIEW_YEAR);
		request.setAttribute("validStart", VALID_DATE_START);
		request.setAttribute("validEnd", VALID_DATE_END);
		request.setAttribute("allowPastDates", request.getParameter("allowPast"));
		String reqMonthView = request.getParameter("viewMonth");
		String sourceObject = request.getParameter("sourceObj");
		
		if (sourceObject == null || sourceObject.trim().length() <= 0) {
			try {
				throw new Exception("Missing input parameter: " + SOURCE_OBJ);
			} catch (Exception e1) {
			
				e1.printStackTrace();
			}
		}
		
		String reqYearView = request.getParameter("viewYear");
		String reqValidStart = request.getParameter("validStart");
		String reqValidEnd = request.getParameter("validEnd");
		
		String reqAllowPastDates = request.getParameter("allowPast");
		SimpleDateFormat sdfFullNumeric = new SimpleDateFormat("MM/dd/yyyy");
		String dateHighlight = request.getParameter("highlight");
		Date dHighlight = null;
		try {
			dHighlight = sdfFullNumeric.parse(dateHighlight);
			
		} catch (NullPointerException e) {
			//Ignore any null pointer exceptions that may occur
			//dValidStart is still null, so a start date won't be used
		} catch (java.text.ParseException e) {
			//Ignore any parsing exceptions that may occur
			//dValidStart is still null, so a start date won't be used
		}
		
		SimpleDateFormat sdfMonthName = new SimpleDateFormat("MMM");
		
		Calendar cView = Calendar.getInstance();
		
		//Get the date parts of the current date from the Calendar object
		int monthCurrent = cView.get(Calendar.MONTH);
		int dayCurrent = cView.get(Calendar.DATE);
		int yearCurrent = cView.get(Calendar.YEAR);
		
		cView.clear();
		cView.set(yearCurrent, monthCurrent, dayCurrent);
		Date dCurrent = cView.getTime();
		
		//Try parsing the start date passed in on the request.
		Date dValidStart = null;
		try {
			
			dValidStart = sdfFullNumeric.parse(reqValidStart);
			
		} catch (NullPointerException e) {
			//Ignore any null pointer exceptions that may occur
			//dValidStart is still null, so a start date won't be used
		} catch (java.text.ParseException e) {
			//Ignore any parsing exceptions that may occur
			//dValidStart is still null, so a start date won't be used
		}
		
		Date dValidEnd = null;
		try {
			dValidEnd = sdfFullNumeric.parse(reqValidEnd);
			
		} catch (NullPointerException e) {
			
			//Ignore any null pointer exceptions that may occur
			//dValidStart is still null, so a start date won't be used
			
		} catch (java.text.ParseException e) {
			
			//Ignore any parsing exceptions that may occur
			//dValidEnd is still null, so an end date won't be used
		}
		
		boolean validRangeExists = false;
		boolean validStartExists = false;
		boolean validEndExists = false;
		boolean allowPastDates = false;
		
		if (dValidStart != null
				&& dValidEnd != null
				&& dValidEnd.compareTo(dValidStart) >= 0) {
			//A valid range exists if both start and end dates have a value and 
			//the end date is the same as or after the start date.
			validRangeExists = true;
			request.setAttribute(
					"validRangeExist",
					String.valueOf(validRangeExists));
			
		} else if (dValidStart != null && dValidEnd == null) {
			//If a valid start date exists and a valid end date does not, we will use
			//a start date only
			validStartExists = true;
			request.setAttribute(
					"validStartExist",
					String.valueOf(validStartExists));
		} else if (dValidEnd != null && dValidStart == null) {
			//If a valid end date exists and a valid start date does not, we will use
			//an end date only
			validEndExists = true;
			request.setAttribute(
					"validEndExists",
					String.valueOf(validEndExists));
		}
		if (reqAllowPastDates != null
				&& reqAllowPastDates.equalsIgnoreCase("true")) {
			//Set allowPastDates boolean based on whether it was sent in the request
			allowPastDates = true;
			request.setAttribute(
					" allowPastDates",
					String.valueOf(allowPastDates));
		}
		Calendar cTemp = Calendar.getInstance();
		
		int monthView, yearView;
		if (reqMonthView != null && reqMonthView.trim().length() > 0) {
			//If monthView was passed in the request, use that value
			monthView = Integer.parseInt(reqMonthView);
			request.setAttribute("monthViw", String.valueOf(monthView));
		} else if (
				(validRangeExists || validStartExists)
				&& dValidStart.after(dCurrent)) {
			//If a full range or start-date only range exists and the start date is 
			//after the current date, use the month of the start date
			cTemp.setTime(dValidStart);
			monthView = cTemp.get(Calendar.MONTH);
			request.setAttribute("monthViw", String.valueOf(monthView));
		} else {
			//Otherwise, use the month of the current date
			cTemp.setTime(dCurrent);
			monthView = cTemp.get(Calendar.MONTH);
			request.setAttribute("monthViw", String.valueOf(monthView));
		}
		
		//Set the year to display
		if (reqYearView != null && reqYearView.trim().length() > 0) {
			//If yearView was passed in the request, use that value
			yearView = Integer.parseInt(reqYearView);
			request.setAttribute("yearViw", String.valueOf(yearView));
		} else if (
				(validRangeExists || validStartExists)
				&& dValidStart.after(dCurrent)) {
			////If a full range or start-date only range exists and the start date is 
			//after the current date, use the year of the start date
			cTemp.setTime(dValidStart);
			yearView = cTemp.get(Calendar.YEAR);
			request.setAttribute("yearViw", String.valueOf(yearView));
		} else {
			//Otherwise, use the year of the current date
			cTemp.setTime(dCurrent);
			yearView = cTemp.get(Calendar.YEAR);
			request.setAttribute("yearViw", String.valueOf(yearView));
		}
		
		
		cView.clear();
		cView.set(yearView, monthView, 1);
		Date dView = cView.getTime();
		cView.get(Calendar.MONTH);
		
		//Format the month name and put into a variable
		String monthName = sdfMonthName.format(dView);
		
		//Determine the correct values for the previous and next month and year
		int monthPrev =
			monthView == Calendar.JANUARY ? Calendar.DECEMBER : monthView - 1;
		int monthNext =
			monthView == Calendar.DECEMBER ? Calendar.JANUARY : monthView + 1;
		int yearPrev = monthView == Calendar.JANUARY ? yearView - 1 : yearView;
		int yearNext = monthView == Calendar.DECEMBER ? yearView + 1 : yearView;
		
		request.setAttribute("monthPv", URLEncoder.encode(String.valueOf(monthPrev)));
		request.setAttribute("monthNx",URLEncoder.encode(String.valueOf(monthNext)));
		request.setAttribute("yearPv", URLEncoder.encode(String.valueOf(yearPrev)));
		request.setAttribute("yearNx", URLEncoder.encode(String.valueOf(yearNext)));
		
		request.setAttribute("srcEncode", URLEncoder.encode(sourceObject));
		String sourceEncode = URLEncoder.encode(sourceObject);
		
		if (validRangeExists) {
			request.setAttribute(
					"frmdValidStart",
					URLEncoder.encode(sdfFullNumeric.format(dValidStart)));
			
			request.setAttribute(
					"frmdValidEnd",
					URLEncoder.encode(sdfFullNumeric.format(dValidEnd)));
			
		} else if (validStartExists) {
			request.setAttribute(
					"frmdValidStart",
					URLEncoder.encode(sdfFullNumeric.format(dValidStart)));
		} else if (validEndExists) {
			request.setAttribute(
					"frmdValidEnd",
					URLEncoder.encode(sdfFullNumeric.format(dValidEnd)));
		}
		String mname="";
		if(monthNext==0)
			mname="12";
		else
			mname=String.valueOf(monthNext);
		
		
		if(mname.length()==1)
			mname=0+mname;
		request.setAttribute("unfmtMonthName", mname);
		request.setAttribute("frmMonthName", sdfMonthName.format(dView));
		
		while (cView.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			cView.add(Calendar.DATE, -1);
			
		}
		
		boolean beginWek = false;
		boolean endWek = false;
		boolean showBlak = false;
		boolean showLik = false;
		int inc = 0;
		ArrayList count = new ArrayList();
		ArrayList beginWeek = new ArrayList();
		ArrayList showBlank = new ArrayList();
		ArrayList showLink = new ArrayList();
		
		ArrayList endWeek = new ArrayList();
		ArrayList fmtdView = new ArrayList();
		ArrayList cdate = new ArrayList();
		ArrayList dateHilight = new ArrayList();
		while (cView.get(Calendar.MONTH) != monthNext) {
			//Set the date object to the Calendar's date
			dView = cView.getTime();
			
			//Set a boolean to start a new week if the date is a Sunday
			beginWek =
				cView.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
				? true
						: false;
			
			beginWeek.add(String.valueOf(beginWek));
			//Set a boolean to end the week if the date is a Saturday
			endWek =
				cView.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				? true
						: false;
			
			endWeek.add(String.valueOf(endWek));
			//Set a boolean to show a blank cell if the date's month is not the month being displayed
			showBlak = cView.get(Calendar.MONTH) != monthView ? true : false;
			
			showBlank.add(String.valueOf(showBlak));
			//Set a boolean to determine whether the date should be displayed as a link
			//or deactivated
			if (!allowPastDates && dView.compareTo(dCurrent) < 0) {
				//If past dates are not allowed and the date to display is
				//prior to today, do not show the date as a link
				showLik = false;
				showLink.add(String.valueOf(showLik));
			} else {
				//Otherwise, do some more date range checking
				
				if (validRangeExists) {
					//If a full date range exists, show the date as a link if
					//it is after or the same as the start date, and if it is
					//before or the same as the end date
					if (dView.compareTo(dValidStart) >= 0
							&& dView.compareTo(dValidEnd) <= 0) {
						showLik = true;
						showLink.add(String.valueOf(showLik));
					} else {
						showLik = false;
						showLink.add(String.valueOf(showLik));
					}
				} else if (validStartExists) {
					//If a start date only range exists, show the date as a link if
					//it is after or the same as the start date
					if (dView.compareTo(dValidStart) >= 0) {
						showLik = true;
						showLink.add(String.valueOf(showLik));
					} else {
						showLik = false;
						showLink.add(String.valueOf(showLik));
					}
				} else if (validEndExists) {
					//If an end date only range exists, show the date as a link if
					//it is before or the same as the end date
					if (dView.compareTo(dValidEnd) <= 0) {
						showLik = true;
						showLink.add(String.valueOf(showLik));
					} else {
						showLik = false;
						showLink.add(String.valueOf(showLik));
					}
					
				} else {
					//Otherwise there is no range at all
					showLik = true;
					showLink.add(String.valueOf(showLik));
				}
			}
			
			fmtdView.add(sdfFullNumeric.format(dView));
			cdate.add(String.valueOf(cView.get(Calendar.DATE)));
			
			cView.add(Calendar.DATE, 1);
			inc = inc + 1;
			count.add(String.valueOf(inc));
			int comp=1;
			try
			{
				comp=dView.compareTo(dHighlight);
			}
			catch(Exception e)
			{
			}
			if(comp==0)
			{
				dateHilight.add("true");
			}
			else {
				dateHilight.add("false");
			}
			
			
			
			
		} /* END WHILE LOOP THROUGH CURRENT MONTH */
		
		request.setAttribute("count", count);
		//calForm.setCount(count);
		request.setAttribute("showLink", showLink);
		request.setAttribute("showBlank", showBlank);
		request.setAttribute("endWeek", endWeek);
		request.setAttribute("beginWeek", beginWeek);
		request.setAttribute("fmtdView", fmtdView);
		request.setAttribute("cDate", cdate);
		request.setAttribute("dtHighlight",dateHilight);
		
		request.setAttribute("highligt",dateHighlight);
		//Build remaining empty table cells needed to complete the calendar
		ArrayList eedWeek = new ArrayList();
		ArrayList count2 = new ArrayList();
		boolean eendWeek = false;
		int inc2 = 0;
		while (cView.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			eendWeek =
				cView.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				? true
						: false;
			cView.add(Calendar.DATE, 1);
			eedWeek.add(String.valueOf(eendWeek));
			inc2 = inc2 + 1;
			count2.add(String.valueOf(inc2));
		}
		request.setAttribute("eendWeek", eedWeek);
		request.setAttribute("count2", count2);
		//calForm.setCount2(count2);
		
		request.setAttribute("formatdValidStart",request.getParameter("validStart"));
		
		request.setAttribute("formatdValidEnd",request.getParameter("validEnd"));
		String fdate = String.valueOf(cdate.get(0));
		String sdate = String.valueOf(cdate.get(1));
		if (Integer.parseInt(fdate) > 7) {
			cdate.set(0, "");
			
		}
		
		return (mapping.findForward("calendarSuccess"));
		
	}
	
	
	
	/**
	 * 
	 * doManageNotifications Action method manages notifications.  
	 * 
	 */							
	
	public ActionForward doManageNotifications(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception {
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		WebDboxOptionsForm optForm = (WebDboxOptionsForm) form;
		boolean loggedInException = false;
		
		
		
		try {
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			/*System.out.println("doManageNotifications : optForm.isSendNotificationDefault = "+optForm.isSendNotificationDefault());
			 System.out.println("doManageNotifications : optForm.isReturnReceiptDefault() = "+optForm.isReturnReceiptDefault());
			 System.out.println("doManageNotifications : optForm.isNewPackageEmailNotification() = "+optForm.isNewPackageEmailNotification());
			 System.out.println("doManageNotifications : optForm.isNagNotification() = "+optForm.isNagNotification());*/
			
			
			if (optForm.isSendNotificationDefault()) 
				dropbox.setOption(DropboxAccess.SendNotificationDefault,"TRUE");
			else 
				dropbox.setOption(DropboxAccess.SendNotificationDefault,"FALSE");
			if (optForm.isReturnReceiptDefault()) 
				dropbox.setOption(DropboxAccess.ReturnReceiptDefault,"TRUE");
			else 
				dropbox.setOption(DropboxAccess.ReturnReceiptDefault,"FALSE");
			if (optForm.isNewPackageEmailNotification()) 
				dropbox.setOption(DropboxAccess.NewPackageEmailNotification,"TRUE");
			else 
				dropbox.setOption(DropboxAccess.NewPackageEmailNotification,"FALSE");
			if (optForm.isNagNotification()) 
				dropbox.setOption(DropboxAccess.NagNotification,"TRUE");
			else 
				dropbox.setOption(DropboxAccess.NagNotification,"FALSE");
			
			
			String option = (String) dropbox.getOption(DropboxGenerator.SendNotificationDefault);
			boolean SendNotificationDefault = option != null && option.equalsIgnoreCase("TRUE");
			optForm.setSendNotificationDefault(SendNotificationDefault);
			
			option = (String) dropbox.getOption(DropboxGenerator.ReturnReceiptDefault);
			boolean ReturnReceiptDefault = option != null && option.equalsIgnoreCase("TRUE");
			optForm.setReturnReceiptDefault(ReturnReceiptDefault);
			
			option = (String) dropbox.getOption(DropboxGenerator.NewPackageEmailNotification);
			boolean NewPackageEmailNotification = option != null && option.equalsIgnoreCase("TRUE");
			optForm.setNewPackageEmailNotification(NewPackageEmailNotification);
			
			option = (String) dropbox.getOption(DropboxGenerator.NagNotification);
			boolean NagNotification = option != null && option.equalsIgnoreCase("TRUE");
			optForm.setNagNotification(NagNotification);
			
			
			setDefaultUserOptions(dropbox,request,response);

		}catch( NotLoggedInException nle)
		{							
			loggedInException = true;					
			
			
		}
		catch(Exception ee) {
			
			logerror(ee);
			//errors.add("error.options",new ActionError("error.options")); 
			createErrorFromException("Error while managing options", ee, errors); 
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}
		
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			forward = mapping.findForward("OptionsFailure");
			
			
		} else {
			
			forward = mapping.findForward("OptionsSuccess");
		}
		
		
		// Finish with
		return (forward);
		
	}

    

	
	
	
	public ActionForward doCrossCompPkgEdit(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		boolean loggedInException = false;
		
		try {
			
		//	System.out.println("******************   doCrossCompPkgEdit invoked *******************");
			WebDboxCrossCompInfoForm disCrossFormData = (WebDboxCrossCompInfoForm) form;
			
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			setItarAttributes(request,response,dropbox);
			
			dropbox.setCrossCompEdit(true);
			
			
			
		} catch (Exception e) {
			
			loggedInException = true;
		
		}
		if (loggedInException == true) 
		{
			return mapping.findForward("sessionExpired");
		}			
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("CrossRcptFailure");
			
			
		} else {
			
			forward = mapping.findForward("CrossRcptSuccess");
		}
		
		
		
		return (forward);
		
	}	

    public ActionForward doUpdateInboxPkgView(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		
		WebDboxInboxForm inboxFormData = (WebDboxInboxForm) form;
		boolean loggedInException = false;
		int totalInboxPkgs=0;
		
		try {	
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			
			dropbox.setFirstTime(true);
          	} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;												   
		}	
		catch (Exception e)
		{
			
			logerror(e);
			
			createErrorFromException("Error handling Inbox Package panel", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}			
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("InboxFailure");
			
			
		} else {
			
			forward = mapping.findForward("InboxSuccess");
		}
		
		
		return (forward); 
	}




	public ActionForward doUpdateDraftSentPkgView(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		
		
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		
		
		WebDboxSentPkgForm sntFormData = (WebDboxSentPkgForm) form;
		boolean loggedInException = false;
		
		
		try {	
			
			setStandardAttributes(request,response);	
			HttpSession session = request.getSession(false);
			UserDropbox dropbox = getValidatedDropbox(session);
			
			setItarAttributes(request,response,dropbox);
			
			
			dropbox.setSentFirstTime(true);
			
		} 
		catch( NotLoggedInException nle)
		{
			loggedInException = true;												   
		}	
		catch (Exception e)
		{
			
			logerror(e);
			
			createErrorFromException("Error handling Sent Package panel", e, errors); 
			
		}
		
		if (loggedInException == true) {
			return mapping.findForward("sessionExpired");
		}			
		
		
		if (!errors.isEmpty()) {
			
			saveErrors(request, errors);
			forward = mapping.findForward("SentsFailure");
			
			
		} else {
			
			forward = mapping.findForward("SentsSuccess");
		}
		
		
		return (forward); 
	}
	
}

