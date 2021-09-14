package oem.edge.ed.sd.util;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: ICC/PROFIT                                                    */
/* (C) Copyright IBM Corp. 2002, 2003                                        */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** RCS & COPYRT *************************************/
/************************** EOF : HEADER *************************************/
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.String;
import java.util.ResourceBundle;
import java.util.Calendar;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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

public class MessageDisplay {
    
    
   public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003"; 
  short displayLevel = 0, 
    logLevel = 0,
    type;
  String logFileName = new String();
  BufferedWriter out;
    
  public static final short LEVEL1 = 1; /* really important messages */
  public static final short LEVEL2 = 2;
  public static final short LEVEL3 = 3; /* print anything and everything*/

  public static final short LOG             = 1;
  public static final short DISPLAY         = 2;
  public static final short LOG_AND_DISPLAY = 3;
    

  public MessageDisplay (String rbFile, String rbTag) {
    this(rbFile, rbTag, DISPLAY, new String());
  }
    
  public MessageDisplay (String rbFile, String rbTag, 
			 short type, String logTag) {
    try {
      ResourceBundle prop = ResourceBundle.getBundle(rbFile);
	    
      init(prop, rbTag, type, logTag);
	    
    } catch (java.util.MissingResourceException mre) {
      System.out.println("Error reading resource file " + 
			 rbFile);
    }
  }

    
  public MessageDisplay (ResourceBundle prop, String rbTag) {
    this(prop, rbTag, DISPLAY, new String());
  }


  public MessageDisplay (ResourceBundle prop, String rbTag, 
			 short type, String logTag) {
    init(prop, rbTag, type, logTag);
  }


  void init (ResourceBundle prop, String rbTag, 
	     short type, String logTag) {
    this.type = type;
    try {
      String el;
      if (type == DISPLAY || type == LOG_AND_DISPLAY) {
	el = prop.getString(rbTag + "_display_level").trim();
	displayLevel = (short) Integer.parseInt(el);
      }	    
      if (type == LOG || type == LOG_AND_DISPLAY) {
	el = prop.getString(rbTag + "_log_level").trim();
	logLevel = (short) Integer.parseInt(el);
	logFileName = prop.getString("edesign_home").trim();

        if ( ! logFileName.endsWith("/") )
            logFileName += "/";

	logFileName += prop.getString(logTag + "_log_file").trim();
      }
    } catch (java.lang.NumberFormatException nfe) {
      System.out.println("Error reading message level for " + 
			 rbTag);
    }	


    /* log file name */
    Calendar cal = Calendar.getInstance();
    logFileName += "." + (cal.get(Calendar.MONTH)+1) + "-" + 
      cal.get(Calendar.DATE) + "-" + cal.get(Calendar.YEAR);

  }


  public void displayMessage (String error, int level) {
    try {
      if (type == LOG || type == LOG_AND_DISPLAY) {
	if (level <= logLevel) {
	  out = new BufferedWriter(new FileWriter(
						  logFileName, true));
	  out.write(error + "\n");
	  out.close();
	}
      }
    } catch (java.io.IOException ioe) {
      System.out.println("Could not open log file: " + logFileName);
    }

    if (level <= displayLevel) {
      System.out.println(error);
    }
  }

    
  public void displayMessage (String error, Exception ex, int level) {
    try {
      if (type == LOG || type == LOG_AND_DISPLAY) {
	if (level <= logLevel) {
	  StringWriter sw = new StringWriter();
	  PrintWriter pw = new PrintWriter(sw);
	  ex.printStackTrace(pw);

	  out = new BufferedWriter(new FileWriter(logFileName, true));
	  out.write(error + "\n");
	  out.write(sw.toString() + "\n");
	  out.close();
	}
      }
    } catch (java.io.IOException ioe) {
      System.out.println("Could not open log file: " + logFileName);
    }

    if (level <= displayLevel) {
      System.out.println(error);
      ex.printStackTrace(System.out);
    }

  }


  public static void displayMessage (String error) {
    System.out.println(error);
  }

}
