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

package oem.edge.ets.fe;

import java.util.List;
import java.util.ArrayList;


public class ETSAccessRequestList {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.3";


	private List requests;

	public ETSAccessRequestList() {
		super();
		this.requests = new ArrayList();
	}

	public ETSAccessRequestList(List someRequests) {
		super();
		this.requests = someRequests;
	}

	public void doAcceptReject() {
		// TBD
	}

	// the remainder is a controlled facade for the list.

	public void add(int i, ETSAccessRequest accreq) {
		requests.add(i, accreq);
	}

	public boolean add(ETSAccessRequest accreq) {
		return requests.add(accreq);
	}

	public ETSAccessRequest get(int i) {
		return (ETSAccessRequest) requests.get(i);
	}

	public int size() {
		return requests.size();
	}
}

