package oem.edge.ed.odc.dsmp.client;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2006                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rights                                */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */
public class Buddy {
	static public int USER = 0;
	static public int GROUP = 1;
	static public int PROJECT = 2;
	public String name;
	public String companyList;
	public int type = USER;
/**
 * Buddy constructor comment.
 */
public Buddy(String name) {
	this(name,USER);
}
/**
 * Buddy constructor comment.
 */
public Buddy(String name, int type) {
	super();
	this.name = name;
	this.companyList = null;
	this.type = type;
}
/**
 * Insert the method's description here.
 * Creation date: (7/14/2004 4:15:58 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof Buddy) {
		Buddy buddy = (Buddy) obj;
		return (name.equals(buddy.name) && type == buddy.type);
	}

	return super.equals(obj);
}
}
