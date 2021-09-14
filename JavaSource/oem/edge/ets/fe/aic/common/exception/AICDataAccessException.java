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


package oem.edge.ets.fe.aic.common.exception;

public class AICDataAccessException extends AICApplicationException{
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	
  Throwable exceptionCause = null;

  public AICDataAccessException(String pExceptionMsg){
    super(pExceptionMsg);
  }

  public AICDataAccessException(String pExceptionMsg, Throwable pException){
    super(pExceptionMsg);
    this.exceptionCause=pException;
  }

  /**
	   * @param errorCode
	   * @param message
	   */
	  public AICDataAccessException(String errorCode, String message) {
		  super(errorCode, message);
       
	 }

  /**
	   * @param errorCode
	   * @param message
	   * @param cause
	   */
	  public AICDataAccessException(String errorCode, String message, Throwable cause) {
		  super(errorCode, message, cause);
       
   }


}
