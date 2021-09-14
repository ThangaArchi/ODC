package oem.edge.ed.odc.model;

import oem.edge.ed.odc.tunnel.common.DebugPrint;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.odc.view.*;
import java.util.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 1998,2004,2005,2006		                 */ 
/*                                                                       */ 
/*     All Rights Reserved					         */ 
/*     US Government Users Restricted Rights			         */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

/**
 * Insert the type's description here.
 * Creation date: (08/25/00 14:33:14)
 * @author: Administrator
 */
public class LocalDataManager implements DataManager {
	private static java.util.Hashtable DesktopTable = new Hashtable();
/**
 * LocalDataManager constructor comment.
 */
public LocalDataManager() {
	super();
}
/**
 * addDesktop method comment.
 */
public boolean addDesktop(DesktopView dv) {
	
	// block until there's no unbound desktop
	
	Desktop dt = new Desktop(dv);
        String desktopId = dv.getKey();
	dt.setStartTime(new java.sql.Timestamp(new Date().getTime()));
	if ( DesktopTable.get(desktopId) != null ) {
		DebugPrint.println(DebugPrint.WARN,
                                   "LDM: addDesktop: Desktop id: " + 
                                   desktopId + " already exists!");
	}

	DesktopTable.put(desktopId, dt);

	return true;
}

/**
 * bindDesktop method comment.
 */
public DesktopView bindDesktop(String id, String owner, String xDisplay, 
                               String xCookie, String alias,
                               String bh, String bp) {
	Desktop dt = (Desktop) DesktopTable.get(id);
	if ( dt != null ) {
        
           if (owner    != null) dt.setOwner(owner);
           if (xDisplay != null) dt.setXDisplay(xDisplay);
           if (xCookie  != null) dt.setXCookie(xCookie);
           if (alias    != null) dt.setXAlias(alias);
           if (bh != null) {
              DebugPrint.println(DebugPrint.INFO4, 
                                 "BIND: Bump host, port = " + bh + " " + bp);
              dt.setBumpHost(bh);
              try {
                 int p = Integer.parseInt(bp);
                 dt.setBumpPort(p);
              } catch (NumberFormatException e) {
                 DebugPrint.println(DebugPrint.WARN, 
                                    "Setting of bumpport BAD:" + bp);
              }
           }
	} else {
           return null;
        }
	
	return makeDesktopView(dt);
}

public DesktopView makeDesktopView(Desktop dt) {
   if ( dt != null ) {
      DesktopView v = new DesktopView(dt);
     /*
      v.setKey(dt.getKey());
      v.setCompany(dt.getCompany());
      v.setCountry(dt.getCountry());
      v.setEdgeId(dt.getEdgeId());
      v.setOwner(dt.getOwner());
      v.setRemoteHost(dt.getRemoteHost());
      v.setRemotePort(dt.getRemotePort());
      v.setStartTime(dt.getStartTime());
      v.setXAlias(dt.getXAlias());
      v.setXDisplay(dt.getXDisplay());
      v.setXCookie(dt.getXCookie());
      v.setLocalHost(dt.getLocalHost());
      v.setLocalPort(dt.getLocalPort());
      v.setBumpHost(dt.getBumpHost());
      v.setBumpPort(dt.getBumpPort());
     */
      return v;
   }
   return null;
}

public DesktopView getDesktop(String id) {
	Desktop dt = (Desktop) DesktopTable.get(id);
	if ( dt != null ) {
           return makeDesktopView(dt);
	}
	return null;
}
/**
 * getDesktop method comment.
 */
public Vector getDesktopByUser(String uid) {
   Desktop dt;
   DesktopView v;
   Vector vector = new Vector();
   Enumeration set = DesktopTable.elements();
   while ( set.hasMoreElements() ) {
      dt = (Desktop) set.nextElement();
      if ( dt.getEdgeId()!=null && dt.getEdgeId().equals(uid) ) {
         v= makeDesktopView(dt);
         vector.addElement(v);
      }
   }
   return (vector.size() > 0) ? vector : null;
}

/**
 * load method comment.
 */
public boolean init(ReloadingProperty p) {

	return true;
}
/**
 * removeDesktop method comment.
 */
public DesktopView removeDesktop(String id) {
   Desktop dt = (Desktop) DesktopTable.remove(id);
   
   if ( dt != null ) {
      return makeDesktopView(dt);
   }
   return null;
}
/**
 * getDesktop method comment.
 */
public DesktopView setRemotePort(String id, String rhost, int port) {
   Desktop dt = (Desktop) DesktopTable.get(id);
   if ( dt != null ) {
      dt.setRemotePort(port);
      dt.setRemoteHost(rhost);
      return makeDesktopView(dt);
   }
   return null;
}
}
