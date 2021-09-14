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
package oem.edge.ets.fe;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: IBM Customer Connect                                          */
/* (C) Copyright IBM Corp. 2002,2003                                      */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** EOF : HEADER *************************************/
/*																			 */
/*	File Name 	: 	EdesignErrorCodes.java									 */
/*	Release		:	2.9														 */
/*	Description	:	To generate unique Error Codes for exceptions.			 */
/*	Created By	: 	Sathish													 */
/*	Date		:	06/06/2001												 */
/*****************************************************************************/
/*  Change Log 	: 	Please Enter Changed on, Changed by and Desc			 */
/*****************************************************************************/
/*	Changed On  : 	06/24/2001												 */
/*	Changed By  : 	Sathish													 */
/*	Change Desc : 	Added Log4j logging									 	 */
/*****************************************************************************/

/**
 * @author: Sathish
 */


public class ETSErrorCodes {
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";

	private static String CLASS_VERSION = "1.6";

	// java.lang exceptions
	private static final String ARITHMATIC 					= "ETS-LANG-ARITHMATIC-EX-001";
	private static final String ARRAY_INDEX_OUT_OF_BOUNDS 	= "ETS-LANG-ARRAYINDEX-EX-001";
	private static final String ARRAY_STORE 				= "ETS-LANG-ARRAYSTORE-EX-001";
	private static final String CLASS_CAST 					= "ETS-LANG-CLASSCAST-EX-001";
	private static final String CLASS_NOT_FOUND 			= "ETS-LANG-CLASS-EX-001";
	private static final String CLONE_NOT_SUPPORRTED 		= "ETS-LANG-CLONE-EX-001";
	private static final String EXCEPTION 					= "ETS-LANG-EXCEPTION-EX-001";
	private static final String ILLEGAL_ACCESS 				= "ETS-LANG-ACCESS-EX-001";
	private static final String ILLEGAL_ARGUMENT 			= "ETS-LANG-ARGUMENT-EX-001";
	private static final String ILLEGAL_MONITOR_STATE 		= "ETS-LANG-MONITOR-EX-001";
	private static final String ILLEGAL_STATE 				= "ETS-LANG-STATE-EX-001";
	private static final String ILLEGAL_THREAD_STATE 		= "ETS-LANG-THREAD-EX-001";
	private static final String INDEX_OUT_OF_BOUNDS 		= "ETS-LANG-INDEX-EX-001";
	private static final String INSTANTIATION 				= "ETS-LANG-INSTANCE-EX-001";
	private static final String INTERRUPTED 				= "ETS-LANG-INTERRUPTED-EX-001";
	private static final String NEGATIVE_ARRAY_SIZE 		= "ETS-LANG-NEGATIVE-EX-001";
	private static final String NO_SUCH_FIELD 				= "ETS-LANG-FIELD-EX-001";
	private static final String NO_SUCH_METHOD 				= "ETS-LANG-METHOD-EX-001";
	private static final String NULL_POINTER 				= "ETS-LANG-NULL-EX-001";
	private static final String NUMBER_FORMAT 				= "ETS-LANG-NUMBER-EX-001";
	private static final String RUNTIME 					= "ETS-LANG-RUNTIME-EX-001";
	private static final String SECURITY 					= "ETS-LANG-SECURITY-EX-001";
	private static final String STRING_INDEX_OUT_OF_BOUNDS 	= "ETS-LANG-OUTOFBOUNDS-EX-001";
	private static final String UNSUPPORTED_OPERATION 		= "ETS-LANG-UNSUPPORTED-EX-001";

	// java.sql exceptions	
	private static final String BATCH_UPDATE 				= "ETS-SQL-BATCH-EX-001";
	private static final String DATA_TRUNCATION 			= "ETS-SQL-TRUNCATE-EX-001";
	private static final String SQL_EXCEPTION 				= "ETS-SQL-SQL-EX-001";
	private static final String SQL_WARNING 				= "ETS-SQL-WARNING-EX-001";

	// java.io exceptions
	private static final String CHAR_CONVERSION 			= "ETS-IO-CHAR-EX-001";
	private static final String EOF 						= "ETS-IO-EOF-EX-001";
	private static final String FILE_NOT_FOUND 				= "ETS-IO-FILENOTFOUND-EX-001";
	private static final String INTERRUPT 					= "ETS-IO-INTERRUPT-EX-001";
	private static final String INVALID_CLASS  				= "ETS-IO-INVALIDCLASS-EX-001";
	private static final String INVALID_OBJECT 				= "ETS-IO-INVALIDOBJ-EX-001";
	private static final String IO_EXCEPTION 				= "ETS-IO-EX-001";
	private static final String NOT_ACTIVE 					= "ETS-IO-NOTACTIVE-EX-001";
	private static final String NOT_SERIALIZABLE 			= "ETS-IO-SERIALIZE-EX-001";
	private static final String OBJECT_STREAM 				= "ETS-IO-OBJECT-EX-001";
	private static final String OPTIONAL_DATA 				= "ETS-IO-OPTIONAL-EX-001";
	private static final String STREAM_CORRUPTED 			= "ETS-IO-STREAM-EX-001";
	private static final String SYNC_FAILED 				= "ETS-IO-SYNC-EX-001";
	private static final String UNSUPPORTED_ENCODING 		= "ETS-IO-UNSUPPORTED-EX-001";
	private static final String UTF_DATA_FORMAT 			= "ETS-IO-UTF-EX-001";
	private static final String WRITE_ABORTED 				= "ETS-IO-WRITEABORT-EX-001";

	// java.net exceptions
	private static final String CONNECT 					= "ETS-NET-CONNECT-EX-001";
	private static final String MALFORMED_URL 				= "ETS-NET-MALFORMEDURL-EX-001";
	private static final String NO_ROUTE_TO_HOST 			= "ETS-NET-ROUTE-EX-001";
	private static final String PROTOCOL 					= "ETS-NET-PROTOCOL-EX-001";
	private static final String UNKNOWN_HOST 				= "ETS-NET-UNKNOWNHOST-EX-001";
	private static final String UNKNOWN_SERVICE 			= "ETS-NET-UNKNOWNSERVICE-EX-001";

	// java.util exceptions	
	private static final String CONCURRENT_MODIFICATION 	= "ETS-UTIL-CONCURRENT-EX-001";
	private static final String EMPTY_STACK 				= "ETS-UTIL-EMPTYSTACK-EX-001";
	private static final String MISSING_RESOURCE 			= "ETS-UTIL-MISSINGRESOURCE-EX-001";
	private static final String NO_SUCH_ELEMENT 			= "ETS-UTIL-NOELEMENT-EX-001";
	private static final String TOO_MANY_LISTENERS 			= "ETS-UTIL-LISTENER-EX-001";

	// com.ibm.mq exceptions
	private static final String MQ_EXCEPTION 				= "ETS-MQ-MESSAGEQUEUE-EX-001";

	private static final String FUNC_SEQUENCE 				= "ETS-DB2-FUNCSEQUENCE-EX-001";
	// undefined
	private static final String UNDEFINED 					= "ETS-GEN-UNDEFINED-EX-001";
	
	
/**
 * EdesignExceptionCode constructor comment.
 */
public ETSErrorCodes() {
	super();
}
	/**
	 * Insert the method's description here.
	 * Creation date: (03/09/01 11:21:25 AM)
	 * @return double
	 */
	public static String getClassVersion() {
		return CLASS_VERSION;
	}
/**
 * Insert the method's description here.
 * Creation date: (06/06/01 10:02:13 AM)
 * @return java.lang.String
 * @param ex java.lang.Exception
 */
public static String getErrorCode(Exception ex) {

	String sErrorCode = "";
	String sErrorName = "";

	sErrorName = ex.getClass().getName();

	
	// java.lang Exceptions
	
	if (sErrorName.equalsIgnoreCase("java.lang.ArithmeticException")) {
		sErrorCode = ARITHMATIC;
	} else if (sErrorName.equalsIgnoreCase("java.lang.ArrayIndexOutOfBoundsException")) {
		sErrorCode = ARRAY_INDEX_OUT_OF_BOUNDS;
	} else if (sErrorName.equalsIgnoreCase("java.lang.ArrayStoreException")) {
		sErrorCode = ARRAY_STORE;
	} else if (sErrorName.equalsIgnoreCase("java.lang.ClassCastException")) {
		sErrorCode = CLASS_CAST;
	} else if (sErrorName.equalsIgnoreCase("java.lang.ClassNotFoundException")) {
		sErrorCode = CLASS_NOT_FOUND;
	} else if (sErrorName.equalsIgnoreCase("java.lang.CloneNotSupportedException")) {
		sErrorCode = CLONE_NOT_SUPPORRTED;
	} else if (sErrorName.equalsIgnoreCase("java.lang.Exception")) {
		sErrorCode = EXCEPTION;
	} else if (sErrorName.equalsIgnoreCase("java.lang.IllegalAccessException")) {
		sErrorCode = ILLEGAL_ACCESS;
	} else if (sErrorName.equalsIgnoreCase("java.lang.IllegalArgumentException")) {
		sErrorCode = ILLEGAL_ARGUMENT;
	} else if (sErrorName.equalsIgnoreCase("java.lang.IllegalMonitorStateException")) {
		sErrorCode = ILLEGAL_MONITOR_STATE;
	} else if (sErrorName.equalsIgnoreCase("java.lang.IllegalStateException")) {
		sErrorCode = ILLEGAL_STATE;
	} else if (sErrorName.equalsIgnoreCase("java.lang.IllegalThreadStateException")) {
		sErrorCode = ILLEGAL_THREAD_STATE;
	} else if (sErrorName.equalsIgnoreCase("java.lang.IndexOutOfBoundsException")) {
		sErrorCode = INDEX_OUT_OF_BOUNDS;
	} else if (sErrorName.equalsIgnoreCase("java.lang.InstantiationException")) {
		sErrorCode = INSTANTIATION;
	} else if (sErrorName.equalsIgnoreCase("java.lang.InterruptedException")) {
		sErrorCode = INTERRUPTED;
	} else if (sErrorName.equalsIgnoreCase("java.lang.NegativeArraySizeException")) {
		sErrorCode = NEGATIVE_ARRAY_SIZE;
	} else if (sErrorName.equalsIgnoreCase("java.lang.NoSuchFieldException")) {
		sErrorCode = NO_SUCH_FIELD;
	} else if (sErrorName.equalsIgnoreCase("java.lang.NoSuchMethodException")) {
		sErrorCode = NO_SUCH_METHOD;
	} else if (sErrorName.equalsIgnoreCase("java.lang.NullPointerException")) {
		sErrorCode = NULL_POINTER;
	} else if (sErrorName.equalsIgnoreCase("java.lang.NumberFormatException")) {
		sErrorCode = NUMBER_FORMAT;
	} else if (sErrorName.equalsIgnoreCase("java.lang.RuntimeException")) {
		sErrorCode = RUNTIME;
	} else if (sErrorName.equalsIgnoreCase("java.lang.SecurityException")) {
		sErrorCode = SECURITY;
	} else if (sErrorName.equalsIgnoreCase("java.lang.StringIndexOutOfBoundsException")) {
		sErrorCode = STRING_INDEX_OUT_OF_BOUNDS;
	} else if (sErrorName.equalsIgnoreCase("java.lang.UnsupportedOperationException")) {
		sErrorCode = UNSUPPORTED_OPERATION;

	// java.sql Exceptions
		
	} else if (sErrorName.equalsIgnoreCase("java.sql.BatchUpdateException")) {
		sErrorCode = BATCH_UPDATE;
	} else if (sErrorName.equalsIgnoreCase("java.sql.DataTruncation")) {
		sErrorCode = DATA_TRUNCATION;
	} else if (sErrorName.equalsIgnoreCase("java.sql.SQLException")) {
		sErrorCode = SQL_EXCEPTION;
	} else if (sErrorName.equalsIgnoreCase("java.sql.SQLWarning")) {
		sErrorCode = SQL_WARNING;

	// java.io Exceptions
	
	} else if (sErrorName.equalsIgnoreCase("java.io.CharConversionException")) {
		sErrorCode = CHAR_CONVERSION;
	} else if (sErrorName.equalsIgnoreCase("java.io.EOFException")) {
		sErrorCode = EOF;
	} else if (sErrorName.equalsIgnoreCase("java.io.FileNotFoundException")) {
		sErrorCode = FILE_NOT_FOUND;
	} else if (sErrorName.equalsIgnoreCase("java.io.InterruptedIOException")) {
		sErrorCode = INTERRUPT;
	} else if (sErrorName.equalsIgnoreCase("java.io.InvalidClassException")) {
		sErrorCode = INVALID_CLASS;
	} else if (sErrorName.equalsIgnoreCase("java.io.InvalidObjectException")) {
		sErrorCode = INVALID_OBJECT;
	} else if (sErrorName.equalsIgnoreCase("java.io.IOException")) {
		sErrorCode = IO_EXCEPTION;
	} else if (sErrorName.equalsIgnoreCase("java.io.NotActiveException")) {
		sErrorCode = NOT_ACTIVE;
	} else if (sErrorName.equalsIgnoreCase("java.io.NotSerializableException")) {
		sErrorCode = NOT_SERIALIZABLE;
	} else if (sErrorName.equalsIgnoreCase("java.io.ObjectStreamException")) {
		sErrorCode = OBJECT_STREAM;
	} else if (sErrorName.equalsIgnoreCase("java.io.OptionalDataException")) {
		sErrorCode = OPTIONAL_DATA;
	} else if (sErrorName.equalsIgnoreCase("java.io.StreamCorruptedException")) {
		sErrorCode = STREAM_CORRUPTED;
	} else if (sErrorName.equalsIgnoreCase("java.io.SyncFailedException")) {
		sErrorCode = SYNC_FAILED;
	} else if (sErrorName.equalsIgnoreCase("java.io.UnsupportedEncodingException")) {
		sErrorCode = UNSUPPORTED_ENCODING;
	} else if (sErrorName.equalsIgnoreCase("java.io.UTFDataFormatException")) {
		sErrorCode = UTF_DATA_FORMAT;
	} else if (sErrorName.equalsIgnoreCase("java.io.WriteAbortedException")) {
		sErrorCode = WRITE_ABORTED;

	// java.net Exceptions

	
	} else if (sErrorName.equalsIgnoreCase("java.net.ConnectException")) {
		sErrorCode = CONNECT;
	} else if (sErrorName.equalsIgnoreCase("java.net.MalformedURLException")) {
		sErrorCode = MALFORMED_URL;
	} else if (sErrorName.equalsIgnoreCase("java.net.NoRouteToHostException")) {
		sErrorCode = NO_ROUTE_TO_HOST;
	} else if (sErrorName.equalsIgnoreCase("java.net.ProtocolException")) {
		sErrorCode = PROTOCOL;
	} else if (sErrorName.equalsIgnoreCase("java.net.UnknownHostException")) {
		sErrorCode = UNKNOWN_HOST;
	} else if (sErrorName.equalsIgnoreCase("java.net.UnknownServiceException")) {
		sErrorCode = UNKNOWN_SERVICE;

	// java.util Exceptions
	
	} else if (sErrorName.equalsIgnoreCase("java.util.ConcurrentModificationException")) {
		sErrorCode = CONCURRENT_MODIFICATION;
	} else if (sErrorName.equalsIgnoreCase("java.util.EmptyStackException")) {
		sErrorCode = EMPTY_STACK;
	} else if (sErrorName.equalsIgnoreCase("java.util.MissingResourceException")) {
		sErrorCode = MISSING_RESOURCE;
	} else if (sErrorName.equalsIgnoreCase("java.util.NoSuchElementException")) {
		sErrorCode = NO_SUCH_ELEMENT;
	} else if (sErrorName.equalsIgnoreCase("java.util.TooManyListenersException")) {
		sErrorCode = TOO_MANY_LISTENERS;

	// com.ibm.mq Exceptions
		
	} else if (sErrorName.equalsIgnoreCase("com.ibm.mq.MQException")) {
		sErrorCode = MQ_EXCEPTION;

	// COM.ibm.db2.jdbc.DB2Exception
	
	} else if (sErrorName.equalsIgnoreCase("COM.ibm.db2.jdbc.DB2Exception")) {
		sErrorCode = FUNC_SEQUENCE;
		
	// UnTrapped Exception
	
	} else {
		sErrorCode = UNDEFINED;
	}

	return sErrorCode;
}
}
