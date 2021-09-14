
package oem.edge.ed.odc.webdropbox.server;

import java.io.*;
import java.lang.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.Calendar;

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
 * 
 * The Tab Library WebDbxUtils.java hosts various utilities for 
 * displaying messages to the Webpages.  
 * 
 **/

public class WebDbxUtils extends TagSupport {

	protected String opstr;
	protected int value1;
	protected int value2;
	protected int value3;
	protected String utilstr1;
	protected String utilstr2;
	protected String utilstr3;
	
	final protected static int COL_WIDTH=25; 
	
	protected int retVal;
		
  
	  public int doStartTag() {
		try {
		  JspWriter out = pageContext.getOut();
			
		  if (opstr.equalsIgnoreCase("sizeunits"))
		  {
		 	out.print(returnSizeInUnits(getUtilstr1()));
			this.retVal=SKIP_BODY;
		  }	
		  if (opstr.equalsIgnoreCase("mmddyyyy"))
		   {
			 out.print(mmddyyyy(getUtilstr1()));
			 this.retVal=SKIP_BODY;
		   }	
		   if (opstr.equalsIgnoreCase("filterchar"))
					  {
						out.print(filterchar(getUtilstr1()));
						this.retVal=SKIP_BODY;
					  }
		  if (opstr.equalsIgnoreCase("resizestr"))
		  {
			out.print((String)returnModString(getUtilstr1()));
			this.retVal=SKIP_BODY;
		  }	

		  
		} catch(IOException ioe) {
		  System.out.println("WebDbxUtils : Error generating tag output: " + ioe);
		} catch(Exception e) {
		
	  }
		
		return(this.retVal);
	  }

	
	


	/**
	 * returnReSizedString injects spaces into the string at 20 char len intervals..
	 * @param returnReSizedString
	 * @return
	 */
	private String returnModString(String str) {
		
		/*if (str != null )
		{
			   int length = str.length();
			   int idx=WebDbxUtils.COL_WIDTH;	
			   	   		    	
		       if (length <= idx )  return str;
		       
			   StringBuffer sbuf=new StringBuffer(str.trim());
			   
		       while( length > WebDbxUtils.COL_WIDTH )	
		       {
		       	 sbuf.insert(idx,' ');
				 idx=idx+WebDbxUtils.COL_WIDTH;
		       	 length=length-WebDbxUtils.COL_WIDTH;
		       }
		       
		       str=sbuf.toString();
		
		  
		}*/
		
		//System.out.println("................returnModString "+str);
		return str;
	

	}



	private String filterchar(String string) {
		String newstr;		
		newstr=string.trim().replace('+',' ');
		return newstr;
	}

	public int doEndTag() {
		
		return(EVAL_PAGE); // Continue with rest of JSP page
	}


	public String returnSizeInUnits(String valuestr)
		{
			
			// Break down value to x.yy KB, MB or GB.
			long value = Long.parseLong(valuestr);
			long size = value;
			long divisor = 1024;
			String suffix = " KB";
			long whole = size / divisor;
			int fraction = 0;
	
			if (whole > 999) {
				divisor = 1048576;
				suffix = " MB";
				whole = size / divisor;
				if (whole > 999) {
					divisor = 1073741824;
					suffix = " GB";
					whole = size / divisor;
				}
			}
	
			fraction = (int) (((size - (whole * divisor)) * 100) / divisor);
	
			String myNewSize=null;
			if (fraction == 0) {
			  myNewSize = new String(whole + suffix);
			}
			else if (fraction < 10) {
			 myNewSize = new String(whole + ".0" + fraction + suffix);
			}
			else {
			 myNewSize = new String(whole + "." + fraction + suffix);
			}
		
		return myNewSize;
		}	



	  public String mmddyyyy(String dateString) {
	  	
	  		 
	  			
		     Date d = new java.util.Date(dateString);
			 Calendar sc = Calendar.getInstance();
			 sc.setTime(d);
			 int yr  = sc.get(Calendar.YEAR);
			 int mon = sc.get(Calendar.MONTH)+1;
			 int day = sc.get(Calendar.DAY_OF_MONTH);
			 int hr  = sc.get(Calendar.HOUR_OF_DAY);
			 int min = sc.get(Calendar.MINUTE);
			 int sec = sc.get(Calendar.SECOND);
      
			 String scS1=((mon < 10)?"0":"")+ mon + ((day < 10)?"/0":"/")+day + "/"+yr;
		
		 
		 	
			 return scS1;
		  }

	
	
	/**
	 * @return
	 */
	public String getOpstr() {
		return opstr;
	}

	/**
	 * @return
	 */
	public String getUtilstr1() {
		return utilstr1;
	}

	/**
	 * @return
	 */
	public String getUtilstr2() {
		return utilstr2;
	}

	/**
	 * @return
	 */
	public String getUtilstr3() {
		return utilstr3;
	}

	/**
	 * @return
	 */
	public int getValue1() {
		return value1;
	}

	/**
	 * @return
	 */
	public int getValue2() {
		return value2;
	}

	/**
	 * @return
	 */
	public int getValue3() {
		return value3;
	}

	/**
	 * @param string
	 */
	public void setOpstr(String string) {
		this.opstr = string;
	}

	/**
	 * @param string
	 */
	public void setUtilstr1(String string) {
		this.utilstr1 = string;
	}

	/**
	 * @param string
	 */
	public void setUtilstr2(String string) {
		this.utilstr2 = string;
	}

	/**
	 * @param string
	 */
	public void setUtilstr3(String string) {
		this.utilstr3 = string;
	}

	/**
	 * @param i
	 */
	public void setValue1(int i) {
		value1 = i;
	}

	/**
	 * @param i
	 */
	public void setValue2(int i) {
		value2 = i;
	}

	/**
	 * @param i
	 */
	public void setValue3(int i) {
		value3 = i;
	}

}
