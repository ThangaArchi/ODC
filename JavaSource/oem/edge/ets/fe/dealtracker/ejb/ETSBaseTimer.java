/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe.dealtracker.ejb;

import java.util.Collection;

import javax.ejb.Timer;
import javax.ejb.TimerHandle;


/**
 * @author v2srikau
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ETSBaseTimer extends javax.ejb.EJBObject {
    public TimerHandle createTimer(String handle, long duration) throws java.rmi.RemoteException;
    public Collection getTimers() throws java.rmi.RemoteException;
    public Timer getTimer(String timerInfo) throws java.rmi.RemoteException;
    public void cancelTimer(String handle) throws java.rmi.RemoteException;
}
