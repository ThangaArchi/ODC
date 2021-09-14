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

package oem.edge.ets.fe.aic.dyntab.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;

import org.apache.commons.logging.Log;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ValidateHelper {
	 
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.4";
	private static Log logger =
					EtsLogger.getLogger(ValidateHelper.class);
	
	public static String validateForRequired(String strRequired,String strColumnName,String strDataValue)
	{
		if (logger.isInfoEnabled()) {
			logger.info("-> validateForRequired");
		}
		String strError = "";
		if(strRequired.equals("Y"))
		{
			if ((strDataValue == null) || (strDataValue.trim().length() == 0)) {
				strError = "errors.required";
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- validateForRequired");
		}
		return strError;
	}
	
	
	public static String validateForDataType(String strColumnType,String strColumnName,String strDataValue)
	{
		if (logger.isInfoEnabled()) {
			logger.info("-> validateForDataType");
		}
		String strError = "";
		
		if(strColumnType.equals("Integer"))
		{
			
			try{
			if(!StringUtil.isNullorEmpty(strDataValue))
			{	
					Integer.parseInt(strDataValue.trim());
				
			}
			}catch(NumberFormatException nfe)
			{
				strError = "errors.integer";
				
			}
		}
		else if(strColumnType.equals("String"))
		{
			
		}
		else if(strColumnType.equals("Decimal"))
		{
			try{
			if(!StringUtil.isNullorEmpty(strDataValue))
			{	
			 	
					Double.parseDouble(strDataValue.trim());
				
			}
			}catch(NumberFormatException nfe)
			{
				strError = "errors.decimal";
			}
		}
		else if(strColumnType.equals("Date"))
		{
			if(!StringUtil.isNullorEmpty(strDataValue))
			{
				
				try{
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		   		formatter.setLenient(false);
		   		Date date = formatter.parse(strDataValue.trim());
				if ("MM/DD/YYYY".length() != strDataValue.trim().length()) {
					strError = "errors.date";
				}
				}catch(ParseException pe)
				{
					//strError = strColumnName + "shoule be " +  strColumnType;
					strError = "errors.date";
				}
				
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- validateForDataType");
		}
		return strError;
	}
	
	public static String validateForMaxLength(String strDataValue)
	{
		if (logger.isInfoEnabled()) {
			logger.info("-> validateForMaxLength");
		}
		String strError = "";
		
		if (strDataValue != null)
		{
			if(strDataValue.trim().length() > 128)
			{			
				strError = "errors.maxlength";
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- validateForMaxLength");
		}
		return strError;
	}

	public static void main(String args[])
	{
		/*
		String str = validateForRequired("Y","",null);
		System.out.println("str="+str);
		*/
		/*
		String str = validateForMaxLength("");
		System.out.println("str="+str);
		*/
		/*
		String str = validateForDataType("Integer","","0");
		System.out.println("str="+str);
		*/
		/*
		String str = validateForDataType("String","","0");
		System.out.println("str="+str);
		*/
		/*
		String str = validateForDataType("Decimal","","1231.sd");
		System.out.println("str="+str);
		*/
		/*
		String str = validateForDataType("Date","","Nov 09, 2005");
		System.out.println("str="+str);
		*/
		String str = validateForDataType("Date",""," 12/12/2005"); 
		System.out.println("str="+str);
		
		
	}
}
