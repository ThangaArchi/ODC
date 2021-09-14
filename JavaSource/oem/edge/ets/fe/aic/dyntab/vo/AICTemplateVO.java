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

package oem.edge.ets.fe.aic.dyntab.vo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import oem.edge.ets.fe.aic.common.vo.ValueObject;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTemplateVO extends ValueObject {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private String templateId = "";
	private String templateName = "";
	private Timestamp templateUpdatedate = null;
	private String active = "N";
	private Collection templateColVOCollection = null;

	public AICTemplateVO() {
		templateColVOCollection = new ArrayList();		
		templateUpdatedate = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * @return
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @return
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @return
	 */
	public Timestamp getTemplateUpdatedate() {
		return templateUpdatedate;
	}

	/**
	 * @param i
	 */
	public void setTemplateId(String s) {
		
		templateId = s;
	}

	/**
	 * @param string
	 */
	public void setTemplateName(String string) {
		
		templateName = string;
	}

	/**
	 * @param timestamp
	 */
	public void setTemplateUpdatedate(Timestamp timestamp) {
		
		templateUpdatedate = timestamp;
	}

	
	/**
	 * @return
	 */
	public String getActive() { 
		return active;
	}

	/**
	 * @param c
	 */
	public void setActive(String c) {
		
		active = c;
	}

	/**
	 * @return
	 */
	public Collection getTemplateColVOCollection() {
		return templateColVOCollection;
	}

	/**
	 * @param collection
	 */
	public void setTemplateColVOCollection(Collection collection) {
		templateColVOCollection = collection;
	}

}