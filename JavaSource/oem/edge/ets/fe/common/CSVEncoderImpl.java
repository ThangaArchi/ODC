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

package oem.edge.ets.fe.common;

import java.util.ArrayList;

/**
 * Author : V2SANDY
 * @Modified by V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CSVEncoderImpl implements CSVEncoder {

	/**
	 * 
	 */
	public CSVEncoderImpl() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This method will take ArrayList of ArrayList of values as input and 
	 * convert them into CSV buffer
	 * @param inputlist
	 * @return 
	 */

	public StringBuffer encode(ArrayList inputlist) {
		
		StringBuffer sbftmp = new StringBuffer();
		StringBuffer sbfcsv = new StringBuffer();
		
		if (inputlist == null || inputlist.size() == 0) {
			sbfcsv.append("InValid Data ");
			return sbfcsv;
		}
		
		for (int ii = 0; ii < inputlist.size(); ii++) {
			
			ArrayList insidelist = (ArrayList) inputlist.get(ii);
			
			for (int jj = 0; jj < insidelist.size(); jj++) {
				
				String value = (String) insidelist.get(jj);
					
				value = value.replaceAll("\"", "\"\"\"");				
								
				value = value.replaceAll("\n", "");
				value = value.replaceAll("\r", "");

				if (value.indexOf(",") != -1) {
				
					value = "\"" + value + "\"";
					
				}

				sbfcsv.append(value);
				
				if (jj != (insidelist.size() - 1)) {
				
					sbfcsv.append(",");
					
				}
				
			} //end of jj
			
			sbfcsv.append("\n");

		}//end of ii
		
		return sbfcsv;
	}

}
