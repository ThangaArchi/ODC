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


package oem.edge.ets.fe.ismgt.model;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssSortObjModel {
	
	private String sortColumn;
	private String sortOrder;

	/**
	 * 
	 */
	public EtsIssSortObjModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	public String getSortColumn() {
		return sortColumn;
	}

	/**
	 * @return
	 */
	public String getSortOrder() {
		return sortOrder;
	}

	/**
	 * @param string
	 */
	public void setSortColumn(String string) {
		sortColumn = string;
	}

	/**
	 * @param string
	 */
	public void setSortOrder(String string) {
		sortOrder = string;
	}

}
