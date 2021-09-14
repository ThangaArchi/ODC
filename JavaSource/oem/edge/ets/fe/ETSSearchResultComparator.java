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

import java.util.Comparator;

public class ETSSearchResultComparator implements Comparator {

	public static final String Copyright = "(C) Copyright IBM Corp. 2003, 2004";

	public static final String VERSION_SID = "1.3";
	public static final String LAST_UPDATE = "5/31/05 18:04:19";

	static final byte SORT_BY_RANK = 1;
	// static final byte SORT_BY_TIMESTAMP = 2;
	// static final byte SORT_BY_TITLE = 3;

	private final byte sortOrder;

	ETSSearchResultComparator(byte sortOrder) {
		this.sortOrder = validateSortOrder(sortOrder);
	}

	ETSSearchResultComparator(String sortOrderStr) {
		this.sortOrder = validateSortOrder(sortOrderStr);
	}

	static byte validateSortOrder(byte b) {
		// || b == SORT_BY_TIMESTAMP || b == SORT_BY_TITLE)
		if (b == SORT_BY_RANK)
			return b;
		else
			throw new RuntimeException("Invalid sortOrder: " + b);
	}

	static byte validateSortOrder(String sortOrderStr) {
		return validateSortOrder(Byte.parseByte(sortOrderStr));
	}

	public int compare(Object o1, Object o2) {

		ETSSearchResult result1 = (ETSSearchResult) o1;
		ETSSearchResult result2 = (ETSSearchResult) o2;

		if (sortOrder == SORT_BY_RANK) {
			if (result1.score > result2.score)
				return -1;
			else if (result1.score < result2.score)
				return 1;
			else
				return 0;
		} else {
			throw new RuntimeException("Invalid sortOrder: " + sortOrder);
		}

	}

	public byte getSortOrder() {
		return sortOrder;
	}

	public boolean equals(Object obj) {
		if (obj instanceof ETSSearchResultComparator
			&& sortOrder == ((ETSSearchResultComparator) obj).getSortOrder())
			return true;
		else
			return false;
	}

}
