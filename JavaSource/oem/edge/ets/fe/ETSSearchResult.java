package oem.edge.ets.fe;

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

/**
 * @author  Navneet Gupta (navneet@us.ibm.com)
 * @since   custcont.4.2.1
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ETSSearchResult implements Comparable, Serializable {

	public static final String Copyright = "(C) Copyright IBM Corp. 2003, 2004";

	public static final String VERSION_SID = "1.15";
	public static final String LAST_UPDATE = "10/11/05 16:35:11";

	final String id;
	final byte type;
	final String projectId;
	final String projectType;
	final String linkid;
	float score;
	String docfileId;
	String cqTrkId;
	boolean isPrivate;
	boolean hasExpired;
	char ibmOnly = Defines.ETS_PUBLIC;

	transient String title;
	transient String link;
	transient boolean isPopup;
	transient String altTitle;
	transient String altLink;
	transient String irUserId;
	transient String userIdRole;
	transient String location;
	transient String description;
	transient String projectName;
	transient int topCatId;

	static final String TYPE_DOC_STR = "DOC";
	static final String TYPE_MAIN_STR = "MAIN";
	static final String TYPE_ISSUE_STR = "ISSUE";
	static final String TYPE_SETMET_STR = "SETMET";
	static final String TYPE_SELF_ASSESSMENT_STR = "SELF";
	static final String TYPE_MEETING_STR = "MEETING";
	static final String TYPE_ALERT_STR = "ALERT";
	static final String TYPE_EVENT_STR = "EVENT";
	static final String TYPE_PROJECT_PLAN_STR = "PLAN";
	static final String TYPE_CAT_STR = "CAT";
	static final String TYPE_CHANGE_REQ_STR = "CHANGE";

	static final byte TYPE_DOC_NUM = 1;
	static final byte TYPE_MAIN_NUM = 2;
	static final byte TYPE_ISSUE_NUM = 3;
	static final byte TYPE_SETMET_NUM = 4;
	static final byte TYPE_SELF_ASSESSMENT_NUM = 5;
	static final byte TYPE_MEETING_NUM = 6;
	static final byte TYPE_ALERT_NUM = 7;
	static final byte TYPE_EVENT_NUM = 8;
	static final byte TYPE_PROJECT_PLAN_NUM = 9;
	static final byte TYPE_CAT_NUM = 10;
	static final byte TYPE_CHANGE_REQ_NUM = 11;

	private static Map types = new HashMap();

	static {
		types.put(TYPE_DOC_STR, new Byte(TYPE_DOC_NUM));
		types.put(TYPE_MAIN_STR, new Byte(TYPE_MAIN_NUM));
		types.put(TYPE_ISSUE_STR, new Byte(TYPE_ISSUE_NUM));
		types.put(TYPE_SETMET_STR, new Byte(TYPE_SETMET_NUM));
		types.put(TYPE_SELF_ASSESSMENT_STR, new Byte(TYPE_SELF_ASSESSMENT_NUM));
		types.put(TYPE_MEETING_STR, new Byte(TYPE_MEETING_NUM));
		types.put(TYPE_ALERT_STR, new Byte(TYPE_ALERT_NUM));
		types.put(TYPE_EVENT_STR, new Byte(TYPE_EVENT_NUM));
		types.put(TYPE_PROJECT_PLAN_STR, new Byte(TYPE_PROJECT_PLAN_NUM));
		types.put(TYPE_CAT_STR, new Byte(TYPE_CAT_NUM));
		types.put(TYPE_CHANGE_REQ_STR, new Byte(TYPE_CHANGE_REQ_NUM));
	}

	ETSSearchResult(
		String id,
		byte type,
		String projectId,
		String projectType,
		String linkid,
		float score) {

		this.id = id;
		this.type = type;
		this.projectId = projectId;
		this.projectType = projectType;
		this.score = score;

		if (linkid != null) {
			this.linkid = linkid;
		} else {
			this.linkid = Defines.LINKID;
		}
	}

	public boolean equals(Object o) {
		return equals((ETSSearchResult) o);
	}

	public boolean equals(ETSSearchResult result) {
		return type == result.type && id.equals(result.id);
	}

	public int compareTo(Object o) {
		return compareTo((ETSSearchResult) o);
	}

	public int compareTo(ETSSearchResult result) {
		return (
			type < result.type
				? -1
				: (type > result.type ? 1 : id.compareTo(result.id)));
	}

	static byte parseType(String typeStr) {
		Byte b = (Byte) types.get(typeStr);
		if (b != null)
			return b.byteValue();
		else
			throw new RuntimeException("Invalid typeStr: " + typeStr);
	}

}
