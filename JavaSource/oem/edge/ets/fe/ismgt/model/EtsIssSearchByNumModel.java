/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2005                                     */
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

package oem.edge.ets.fe.ismgt.model;

import java.util.ArrayList;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssSearchByNumModel {
	
	public static final String VERSION = "1.2";
	private int srchcount;
	private String srchByNum;
	private String edgeProblemId;
	private ArrayList srchList;

	/**
	 * 
	 */
	public EtsIssSearchByNumModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	public int getSrchcount() {
		return srchcount;
	}

	/**
	 * @return
	 */
	public ArrayList getSrchList() {
		return srchList;
	}

	/**
	 * @param i
	 */
	public void setSrchcount(int i) {
		srchcount = i;
	}

	/**
	 * @param list
	 */
	public void setSrchList(ArrayList list) {
		srchList = list;
	}

	/**
	 * @return
	 */
	public String getEdgeProblemId() {
		return edgeProblemId;
	}

	/**
	 * @return
	 */
	public String getSrchByNum() {
		return srchByNum;
	}

	/**
	 * @param string
	 */
	public void setEdgeProblemId(String string) {
		edgeProblemId = string;
	}

	/**
	 * @param string
	 */
	public void setSrchByNum(String string) {
		srchByNum = string;
	}

}
