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

/*
 * The ServiceLocatorException class is an exception that is
 * thrown whenever a user requests an EJB or database connection from the
 * ServiceLocator and the ServiceLocator can not find the item the user is
 * loogkinh for.
 *
 */
public class AICServiceLocatorException extends AICDataAccessException{
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
  public AICServiceLocatorException(String pExceptionMsg){
    super(pExceptionMsg);
  } 

  public AICServiceLocatorException(String pExceptionMsg, Throwable pException){
    super(pExceptionMsg, pException);
  } 
}
