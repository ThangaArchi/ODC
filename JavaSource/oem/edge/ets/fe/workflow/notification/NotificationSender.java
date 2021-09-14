/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.notification;

import java.util.Collection;

import oem.edge.ets.fe.workflow.dao.DBAccess;



/**
 * Class       : NotificationSender
 * Package     : oem.edge.ets.fe.workflow.notification
 * Description : 
 * Date		   : Nov 15, 2006
 * 
 * @author     : Pradyumna Achar
 */
public interface NotificationSender {
	
	public boolean init(NotificationParams params, DBAccess db); //init once with issue_id, wf_id etc; costly call with DB things 
	public boolean send(int recipient_type, Collection recipient_userid, boolean bccMail, DBAccess db); //call this as many times as reqd; once for owners, for issue contact, once more for general etc
	public boolean send(int recipient_type, String recipient_userid, boolean bccMail, DBAccess db); //Reciepient ids can be a String of comma-separated userids
}

