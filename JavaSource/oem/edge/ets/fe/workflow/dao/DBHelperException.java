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

/*
 * Created on Feb 6, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.workflow.dao;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DBHelperException extends Exception{

	/**
	 * Constructs a <code>DBHelperException</code> with no detail message.
	 */
	public DBHelperException () {
		super();
	}

   /**
	 * Constructs a <code>DBHelperException</code> with the 
	 * specified detail message. 
	 *
	 * @param   s   the detail message.
	 */
	public DBHelperException (String s) {
		super(s);
	}


   /**
	 * Constructs a <code>DBHelperException</code> with the 
	 * specified detail message. 
	 *
	 * @param   s   the detail message.
	 */
	public DBHelperException (Exception e) {
		super(e.toString());
	}

}
