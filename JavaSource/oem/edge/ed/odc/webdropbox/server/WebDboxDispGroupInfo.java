
package oem.edge.ed.odc.webdropbox.server;
import java.util.Hashtable;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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

/**
 * The Bean WebDboxDispGroupInfo.java is populated with important data which is used
 * for rendering the GroupInfo.jsp
 * Webpage Information pertaining to the Groups information etc. is stored in this bean
 **/
public class WebDboxDispGroupInfo {
	
	
	
	protected Hashtable groupInfoHash=new Hashtable();
	

	
	public Hashtable getGroupInfoHash() {
		return groupInfoHash;
	}

	
	public void setGroupInfoHash(Hashtable hashtable) {
		groupInfoHash = hashtable;
	}

}
