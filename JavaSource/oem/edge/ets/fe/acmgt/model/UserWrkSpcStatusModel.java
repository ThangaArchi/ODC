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

package oem.edge.ets.fe.acmgt.model;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UserWrkSpcStatusModel {
	
	public static final String VERSION = "1.8";
	
		private int wrkspcuidcount;
		private int wrkspcemailcount;
		
		private boolean addMembrToWrkSpc;
		

	/**
	 * 
	 */
	public UserWrkSpcStatusModel() {
		super();
		// TODO Auto-generated constructor stub
	}

		/**
		 * @return
		 */
		public int getWrkspcuidcount() {
			return wrkspcuidcount;
		}

		/**
		 * @param i
		 */
		public void setWrkspcuidcount(int i) {
			wrkspcuidcount = i;
		}

		/**
		 * @return
		 */
		public int getWrkspcemailcount() {
			return wrkspcemailcount;
		}

		/**
		 * @param i
		 */
		public void setWrkspcemailcount(int i) {
			wrkspcemailcount = i;
		}

		/**
		 * @return
		 */
		public boolean isAddMembrToWrkSpc() {
			return addMembrToWrkSpc;
		}

		/**
		 * @param b
		 */
		public void setAddMembrToWrkSpc(boolean b) {
			addMembrToWrkSpc = b;
		}

}
