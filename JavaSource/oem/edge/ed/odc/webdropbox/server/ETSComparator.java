package oem.edge.ed.odc.webdropbox.server;


import java.util.Comparator;
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

/**
 * ETSComparator.java hosts web dropbox sorting related information
 *  
 **/
public class ETSComparator implements Comparator {

	
	public static final byte SORT_BY_NAME = 1;
	public static final byte SORT_BY_SIZE = 2; 
	public static final byte SORT_BY_OWNER = 3;
	public static final byte SORT_BY_COMPANY = 4;
	public static final byte	SORT_BY_DATE = 5;
	public static final byte	SORT_BY_MD5 = 6;
	public static final byte	SORT_BY_DATE_COMMIT = 7;
	
	
	public static final String SORT_BY_PKG_STR = "1";
	public static final String SORT_BY_PKG_SIZE_STR = "2";
	public static final String SORT_BY_PKG_OWNER_STR = "3";
	public static final String SORT_BY_PKG_COMPANY_STR = "4";
	public static final String SORT_BY_PKG_DATE_STR = "5";
	public static final String SORT_BY_PKG_MD5_STR = "6";
	public static final String SORT_BY_PKG_DATE_COMMIT_STR = "7";
	
	public static final String SORT_ASC_STR = "asc";
	public static final String SORT_DESC_STR = "desc";

	public static final byte SORT_ASC = 1;
	public static final byte SORT_DESC = 2;
	
	
	public static final byte FILE_NAME_HEADER = 9;
	public static final byte SIZE_NAME_HEADER = 9; 
	public static final byte OWNER_NAME_HEADER = 7;
	public static final byte COMPANY_NAME_HEADER = 7;
	public static final byte	DATE_NAME_HEADER = 10;
	

	private static final String[] SORT_PARAM_KEY =
		{ null, SORT_BY_PKG_STR, SORT_BY_PKG_SIZE_STR,SORT_BY_PKG_OWNER_STR, SORT_BY_PKG_COMPANY_STR, SORT_BY_PKG_DATE_STR,SORT_BY_PKG_MD5_STR,SORT_BY_PKG_DATE_COMMIT_STR}; 

	private static final String[] SORT_KEY =
		{ null, SORT_ASC_STR, SORT_DESC_STR};

	private final byte sortOrder;
	private final byte sortBy;

	public ETSComparator(byte sortOrder, byte sortBy) {

		this.sortOrder = validateSortOrder(sortOrder);
		this.sortBy = validateSortBy(sortBy);

	}

	public static byte validateSortOrder(byte b) {
		if (b == SORT_BY_NAME || b == SORT_BY_SIZE || b == SORT_BY_OWNER || b == SORT_BY_COMPANY || b == SORT_BY_DATE || b == SORT_BY_MD5 || b == SORT_BY_DATE_COMMIT )
			return b;
		else
			throw new RuntimeException("Invalid sortOrder: " + b);
	}
	public static byte validateSortBy(byte b) {
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
	
	public static String getSortOperationByString(byte sortOperByte ) {
		String temp = new String();
		
		switch(sortOperByte)
		{
			case 1 : temp="sort_pkgName" ; break;	
			case 2 : temp="sort_pkgSize" ; break;
			case 3 : temp="sort_pkgSent" ; break;
			case 4 : temp="sort_pkgComp" ; break;
			case 5 : temp="sort_pkgExp" ; break;
			case 7 : temp="sort_pkgCommit" ; break;
		
		
		}
		return temp;
		
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


	
		if (sortOrder == SORT_BY_NAME || sortOrder == SORT_BY_OWNER || sortOrder == SORT_BY_COMPANY || sortOrder == SORT_BY_MD5 ) 
		{
			String str1 = o1.toString(); 
			String str2 = o2.toString(); 
			

			String str3 = str1.substring(0,str1.lastIndexOf(':'));
			String str4 = str2.substring(0,str2.lastIndexOf(':'));
			
			
			if (sortBy == SORT_ASC)
				return str3.compareToIgnoreCase(str4);
			else
				return str4.compareToIgnoreCase(str3);
		}
		else if (sortOrder == SORT_BY_SIZE  ) 
		{
			String str1 = o1.toString(); 
			String str2 = o2.toString(); 
			
			
			int i = Integer.parseInt(str1.substring(0,str1.lastIndexOf(':')));
			int j = Integer.parseInt(str2.substring(0,str2.lastIndexOf(':')));
		
		
    	
			if (sortBy == SORT_ASC)
				return (i < j ? -1 : (i == j ? 0 : 1));
			else
			{   
				return (j < i ? -1 : (j == i ? 0 : 1));
			}

			

		}else if ( sortOrder == SORT_BY_DATE || sortOrder == SORT_BY_DATE_COMMIT) 
		{
			String str1 = o1.toString(); 
			String str2 = o2.toString(); 
			
			
			long i = Long.parseLong(str1.substring(0,str1.lastIndexOf(':')));
			long j = Long.parseLong(str2.substring(0,str2.lastIndexOf(':')));
		
    
			if (sortBy == SORT_ASC)
				return (i < j ? -1 : (i == j ? 0 : 1));
			else
			{   
				return (j < i ? -1 : (j == i ? 0 : 1));
			}
			
			
		}		 
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
