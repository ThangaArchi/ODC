package oem.edge.ed.odc.dsmp.common;

import java.lang.*;
import java.io.*;
import java.util.Vector;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 1998,2004,2005,2006		                 */ 
/*                                                                       */ 
/*     All Rights Reserved					         */ 
/*     US Government Users Restricted Rights			         */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

/**
 * Thrown exception for Dropbox and Group object manipulation.
 *
 */
public class DboxException extends Exception {

   public static final int GENERAL_ERROR = 0;
   public static final int SQL_ERROR     = 99;

   private Throwable subthrowable;
   
   int majorCode = 0;
   int minorCode = 0;
   Vector parms;
   
  /**
   * Empty constructor
   */
   public DboxException() {
      super();
   }
   
  /**
   * Initialize with just an exception message
   */
   public DboxException(String msg) {
      super(msg);
   }
     
     
   /*
    * This is needed as having getCause and initCause routines to help 1.3 become
    *  1.4 gives WSDL and WAS fits and starts. This allows it to work in both envs
    *  and no probs:
    *
    * Incompatible parameter types found at Fault class method: 
    *    "oem.edge.ed.odc.dsmp.common.DboxException"
    *     Existing Java parameter type = java.lang.Throwable
    *     WSDL defined parameter type  = java.lang.Object
    */
    private void saveThrowable(Throwable t) {
       if (t != null) {
          try {
            Class carr[] = { Throwable.class };
             java.lang.reflect.Method m = 
                 getClass().getMethod("initCause", carr);
                        
             Object oarr[] = { t };
             m.invoke(this, oarr); 
          } catch(Exception e) {
             subthrowable = t;
          }
       }
    }   
   
   
  /**
   * Initialize with just an exception message and a cause
   */
   public DboxException(String msg, Throwable t) {
      super(msg);
      saveThrowable(t);
   }
   
  /**
   * Initialize with just a cause
   */
   public DboxException(Throwable t) {
      super();
      saveThrowable(t);
   }
   
  /**
   * Initialize with an exception message and a major code
   */
   public DboxException(String msg, int code) {
      super(msg);
      majorCode = code;
   }
   
  /**
   * Initialize with an exception message, a major code and minor code
   */
   public DboxException(String msg, int majcode, int mincode) {
      super(msg);
      majorCode = majcode;
      minorCode = mincode;
   }
   
  /**
   * Initialize with an exception message, a major code, minor code and cause
   */
   public DboxException(String msg, int majcode, int mincode, Throwable t) {
      super(msg);
      majorCode = majcode;
      minorCode = mincode;
      saveThrowable(t);
   }
   
  // Needed for dserproxy
   public DboxException(Vector p, int errcode, int majcode, int mincode) {
      super();
      parms = p;
      majorCode = majcode;
      minorCode = mincode;
   }
   
  // Needed for dserproxy
   public DboxException(int errcode, int majcode, int mincode) {
      super();
      majorCode = majcode;
      minorCode = mincode;
   }
   
  /**
   * Returns any parameter values associated with the major/minor codes. Can be null
   * @return parameter values associated with major/minor codes. Can be null
   */
   public Vector getParameters() { return ((parms != null)?(Vector)parms.clone():null); }
   
  /**
   * Sets the parameter values associated with the major/minor codes
   */
   public void setParameters(Vector v) { 
      if (v != null) parms = (Vector)v.clone();
      else           parms = null;
   }
      
  /**
   * Adds a parameter to the current set of values associated with the major/minor codes
   * @param v parameter to add to the parameter set associated with the major/minor codes
   */
   public void addParameter(String v) { 
      if (parms == null) parms = new Vector();
      parms.add(v);
   }
      
  /**
   * Get the error code. Error code is a synonym for major code
   * @return error code/major code value
   */
   public int getErrorCode() { return getMajorCode(); }
   
  /**
   * Get the major code.
   * @return major code value
   */
   public int getMajorCode() { return majorCode;      }
   
  /**
   * Get the minor code.
   * @return minor code value
   */
   public int getMinorCode() { return minorCode;      }
   
  /**
   * Set the major code.
   * @param v  major code value
   */
   public void setMajorCode(int v) { majorCode = v;    }
   
  /**
   * Set the minor code.
   * @param v minor code value
   */
   public void setMinorCode(int v) { minorCode = v;    }
   
   public String toString() {
      String ret=super.toString();
      
     // For 1.3 env
      if (subthrowable != null) {
         ret += "\n\nNested Exception:\n\n" + subthrowable.toString();
      }
      return ret;
   }
   
   public void printStackTrace(PrintWriter out) {
   
      super.printStackTrace(out);
      
     // For 1.3 env
      if (subthrowable != null) {
          out.print("\nNested Exception:\n\n");
          subthrowable.printStackTrace(out);
      }
   }
   
   public void printStackTrace(PrintStream out) {
   
      super.printStackTrace(out);
      
     // For 1.3 env
      if (subthrowable != null) {
          out.print("\nNested Exception:\n\n");
          subthrowable.printStackTrace(out);
      }
   }
   
   public void printStackTrace() {
      printStackTrace(System.err);
   }
}
