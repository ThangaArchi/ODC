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

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICSystemException extends RuntimeException {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	Throwable exceptionCause = null;
	private String errorCode = "";
	public AICSystemException(String pExceptionMsg) {
		super(pExceptionMsg);
	}

	public AICSystemException(String pExceptionMsg, Throwable pException) {
		super(pExceptionMsg);
		this.exceptionCause = pException;
	}
	/**
	 * @param errorCode error code
	 * @param message text which needs to be propagated
	 */
	public AICSystemException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
	
	/**
	 * @param errorCode error code
	 * @param message text which needs to be propagated
	 * @param cause The original exception
	 */
	public AICSystemException(String errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}
	
	/**
		 * @return Returns the errorCode.
		 * Returns error code if not null. If it is null, it returns error code of wrapped exception.
		 */
		public String getErrorCode() {
			if( errorCode != null)
			{
				return errorCode;
			}

				// check if wrapped exception has a non-null error code
				Throwable origException = getOriginalException();

				if( origException != null)
				{
					if( origException instanceof AICApplicationException )
					{
						AICApplicationException faBiz = (AICApplicationException)origException;
						return faBiz.getErrorCode();
					}

					if( origException instanceof AICSystemException)
					{
						AICSystemException faSys = (AICSystemException)origException;
						return faSys.getErrorCode();
					}
				}

				return null;
//			}
		}
		
	   /**
		 * @param errorCode The errorCode to set.
		 */
		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}

		/*
		 * This is a convinient method which returns root cause exception.
		 */
		public Throwable getOriginalException()
		{
			return getCause();
		}
	public void printStackTrace() {
		if (exceptionCause != null) {
			System.err.println("An exception has been caused by: ");
			exceptionCause.printStackTrace();
		}

	}
}
