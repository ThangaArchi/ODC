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
 * @author  Navneet Gupta
 * @modified Sandra Nava
 */

import java.util.Comparator;
import java.sql.*;

public class ETSComparator implements Comparator {

	public static final String Copyright = "(C) Copyright IBM Corp. 2003, 2004";

	public static final String version = "v040604.1: ";

	public static final byte SORT_BY_TYPE = 3;
	public static final byte SORT_BY_AUTHOR = 4; 
	public static final byte SORT_BY_COMP = 5;
	public static final byte SORT_BY_DT_OWNER = 10;

	public static final String SORT_BY_TYPE_STR = Defines.SORT_BY_TYPE_STR;
	public static final String SORT_BY_AUTHOR_STR = Defines.SORT_BY_AUTH_STR;
	public static final String SORT_BY_COMP_STR = Defines.SORT_BY_COMP_STR;
	public static final String SORT_BY_DT_OWNER_STR = Defines.SORT_BY_DT_OWNER_STR;
	
	public static final String SORT_ASC_STR = Defines.SORT_ASC_STR;
	public static final String SORT_DESC_STR = Defines.SORT_DES_STR;

	public static final byte SORT_ASC = 1;
	public static final byte SORT_DESC = 2;

	private static final String[] SORT_PARAM_KEY =
		{ null, null, null,SORT_BY_TYPE_STR, SORT_BY_AUTHOR_STR, SORT_BY_COMP_STR,null,null,null,null,SORT_BY_DT_OWNER_STR}; 

	private static final String[] SORT_KEY =
		{ null, SORT_ASC_STR, SORT_DESC_STR};

	private final byte sortOrder;
	private final byte sortBy;

	public ETSComparator(byte sortOrder, byte sortBy) {
		this.sortOrder = validateSortOrder(sortOrder);
		this.sortBy = validateSortBy(sortBy);
	}

	static byte validateSortOrder(byte b) {
		if (b == SORT_BY_TYPE || b == SORT_BY_AUTHOR || b == SORT_BY_DT_OWNER || b == SORT_BY_COMP)
			return b;
		else
			throw new RuntimeException("Invalid sortOrder: " + b);
	}
	static byte validateSortBy(byte b) {
		if (b == SORT_ASC || b == SORT_DESC)
			return b;
		else
			throw new RuntimeException("Invalid sortBy: " + b);
	}

	public static byte getSortOrder(String sort_by_param) {
		byte so = -1;
		if (sort_by_param != null) {
			for (byte i = 1; i < SORT_PARAM_KEY.length; i++) {
				if (sort_by_param.equals(SORT_PARAM_KEY[i])) {
					so = i;
					break;
				}
			}
		}
		return validateSortOrder(so);
	}
	public static byte getSortBy(String sort_param) {
		byte so = -1;
		if (sort_param != null) {
			for (byte i = 1; i < SORT_KEY.length; i++) {
				if (sort_param.equals(SORT_KEY[i])) {
					so = i;
					break;
				}
			}
		}
		return validateSortBy(so);
	}
	
	
	public int compare(Object o1, Object o2) {

		ETSObj obj1 = (ETSObj) o1;
		ETSObj obj2 = (ETSObj) o2;

		if (sortOrder == SORT_BY_TYPE || sortOrder == SORT_BY_COMP) {
			String str1 = (String) obj1.getStringKey(SORT_PARAM_KEY[sortOrder]);
			String str2 = (String) obj2.getStringKey(SORT_PARAM_KEY[sortOrder]);
			if (sortBy == SORT_ASC)
				return str1.compareToIgnoreCase(str2);
			else
				return str2.compareToIgnoreCase(str1);
		} 
		else if (sortOrder == SORT_BY_AUTHOR || sortOrder == SORT_BY_DT_OWNER) {
			Connection con = null;
			String str1 = "";
			String str2 = "";
			try{
				con = ETSDBUtils.getConnection();
				str1 = ETSUtils.getUsersName(con,(String) obj1.getStringKey(SORT_PARAM_KEY[sortOrder]));
				str2 = ETSUtils.getUsersName(con,(String) obj2.getStringKey(SORT_PARAM_KEY[sortOrder]));
			}
			catch(Exception e){
				System.out.println("error getting connection in etsComparator");	
				str1 = (String) obj1.getStringKey(SORT_PARAM_KEY[1]);
				str2 = (String) obj2.getStringKey(SORT_PARAM_KEY[1]);
			}
			finally{
				if (con != null){
					ETSDBUtils.close(con);				
				}
			}
			if (sortBy == SORT_ASC)
				return str1.compareTo(str2);
			else
				return str2.compareTo(str1);
		} /*
		else if (sortOrder == SORT_BY_ACCLEV) {
			Connection con = null;
			String str1 = "";
			String str2 = "";
			try{
				con = ETSDBUtils.getConnection();
				str1 = ETSDatabaseManager.getRoleName(con,obj1.getIntKey(SORT_PARAM_KEY[sortOrder]));
				str2 = ETSDatabaseManager.getRoleName(con,obj2.getIntKey(SORT_PARAM_KEY[sortOrder]));
			}
			catch(Exception e){
				System.out.println("error getting connection in etsComparator");	
				str1 = obj1.getStringKey(SORT_PARAM_KEY[1]);
				str2 = obj2.getStringKey(SORT_PARAM_KEY[1]);
			}
			finally{
				if (con != null){
					ETSDBUtils.close(con);				
				}
			}
			if (sortBy == SORT_ASC)
				return str1.compareTo(str2);
			else
				return str2.compareTo(str1);
		} 	*/	
		/*else if (sortOrder == SORT_BY_DATE) {
			Long t1 = (Long) obj1.getLongKey(SORT_PARAM_KEY[sortOrder]);
			Long t2 = (Long) obj2.getLongKey(SORT_PARAM_KEY[sortOrder]);
			if (sortBy == SORT_ASC)
				return t1.compareTo(t2);
			else
				return t2.compareTo(t1);
		}*/
		else {
			throw new RuntimeException("Invalid sortOrder: " + sortOrder);
		}

	}

	public byte getSortOrder() {
		return sortOrder;
	}

	public boolean equals(Object obj) {
		if (obj instanceof ETSComparator
			&& sortOrder == ((ETSComparator) obj).getSortOrder())
			return true;
		else
			return false;
	}

}
