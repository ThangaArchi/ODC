package oem.edge.ed.odc.model;

import oem.edge.ed.odc.view.*;
import oem.edge.ed.odc.util.ReloadingProperty;

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
 * Creation date: (7/25/00 10:25:18 AM)
 * @author: Administrator
 */
public interface DataManager {
/**
 * Insert the method's description here.
 * Creation date: (07/31/00 17:22:52)
 * @return boolean
 * @param desktopId String
 * @param lHost String
 * @param rHost String
 */
   boolean addDesktop(DesktopView dv);
   
/**
 * Insert the method's description here.
 * Creation date: (07/31/00 17:22:52)
 * @return boolean
 * @param desktop Desktop
 */
DesktopView bindDesktop(String id, String owner, String xDisplay, 
                        String xCookie, String alias, String bh, String bp);
/**
 * Insert the method's description here.
 * Creation date: (07/31/00 17:31:57)
 * @return com.ibm.edesign.collaboration.model.Desktop
 * @param Id java.lang.String
 */
public DesktopView getDesktop(String Id);
/**
 * Insert the method's description here.
 * Creation date: (07/31/00 17:31:57)
 * @return com.ibm.edesign.collaboration.model.Desktop
 * @param Id java.lang.String
 * @param boundOnly boolean
 */
public Vector getDesktopByUser(String Id);
/**
 * Insert the method's description here.
 * Creation date: (07/25/00 14:28:02)
 * @return boolean
 */
boolean init(ReloadingProperty p);
/**
 * Insert the method's description here.
 * Creation date: (07/31/00 17:23:36)
 * @return Desktop
 * @param id java.lang.String
 */
DesktopView removeDesktop(String id);

DesktopView setRemotePort(String desktopId, String rhost, int port);
}
