/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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
package oem.edge.ets_pmo.domain;
/**
 * @author shingte
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OperObject {
 String type;
 String userid;
 PmoObject object; // root object of this operation
 

/**
 * @return Returns the type.
 */
public String getType() {
	return type;
}
/**
 * @param type The type to set.
 */
public void setType(String type) {
	this.type = type;
}
/**
 * @return Returns the userid.
 */
public String getUserid() {
	return userid;
}
/**
 * @param userid The userid to set.
 */
public void setUserid(String userid) {
	this.userid = userid;
}
/**
 * @return Returns the root.
 */
public PmoObject getObject() {
	return object;
}
/**
 * @param pmo
 */
public void setObject(PmoObject pmo) {
	// TODO Auto-generated method stub
	this.object = pmo;
}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new StringBuffer()
			.append("\n  type="+this.type)
			.append("\n  userid="+this.userid)
			.append("\n  object="+this.object)
			.toString();
	}
}
