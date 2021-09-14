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
public class PmoObject {
protected String id;
protected String root_id;
protected String parent_id;
protected String element_name;
protected String type;
protected String reference_number;
//protected String dummy;
protected PmoObject parent;
//protected PmoObject root;


public String getReference_number() {
	return reference_number;
}
public void setReference_number(String reference_number) {
	this.reference_number = reference_number;
}
/**
 * @return Returns the element_name.
 */
public String getElement_name() {
	return element_name;
}
/**
 * @param element_name The element_name to set.
 */
public void setElement_name(String element_name) {
	this.element_name = element_name;
}
/**
 * @return Returns the id.
 */
public String getId() {
	return id;
}
/**
 * @param id The id to set.
 */
public void setId(String id) {
	this.id = id;
}
/**
 * @return Returns the parent_id.
 */
public String getParent_id() {
	return parent_id;
}
/**
 * @param parent_id The parent_id to set.
 */
public void setParent_id(String parent_id) {
	this.parent_id = parent_id;
}
/**
 * @return Returns the parent.
 */
public PmoObject getParent() {
	return parent;
}
/**
 * @param parent The parent to set.
 */
public void setParent(PmoObject parent) {
	this.parent = parent;
}
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
 * @return Returns the root_id.
 */
public String getRoot_id() {
	return root_id;
}
/**
 * @param root_id The root_id to set.
 */
public void setRoot_id(String root_id) {
	this.root_id = root_id;
}

/**
 * @return
 */
public String getReportable() {
	if ("CRIFOLDER".equalsIgnoreCase(getType()))
		return "N";
	return "Y";
}

public boolean isReportable() {
	if ("CRIFOLDER".equalsIgnoreCase(getType()))
		return false;
	return true;
}




}
