/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004                                     */
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

import java.util.*;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssDynFieldDataModel {
	
	public static final String VERSION = "1.35";

		//display names//
		private String fieldDispName;
	private String fieldRefName;
	private ArrayList fieldValList;
	private ArrayList prevFieldValList;

	/**
	 * 
	 */
	public EtsIssDynFieldDataModel() {
		super();
		// TODO Auto-generated constructor stub
	}

		/**
		 * @return
		 */
		public String getFieldDispName() {
			return fieldDispName;
		}

	/**
	 * @return
	 */
	public String getFieldRefName() {
		return fieldRefName;
	}

	/**
	 * @return
	 */
	public ArrayList getFieldValList() {
		return fieldValList;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevFieldValList() {
		return prevFieldValList;
	}

		/**
		 * @param string
		 */
		public void setFieldDispName(String string) {
			fieldDispName = string;
		}

	/**
	 * @param string
	 */
	public void setFieldRefName(String string) {
		fieldRefName = string;
	}

	/**
	 * @param list
	 */
	public void setFieldValList(ArrayList list) {
		fieldValList = list;
	}

	/**
	 * @param list
	 */
	public void setPrevFieldValList(ArrayList list) {
		prevFieldValList = list;
	}

}
