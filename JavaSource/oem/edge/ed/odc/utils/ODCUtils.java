/*
 * Created on Mar 17, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.utils;

/**
 * @author tkandhas@in.ibm.com
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ODCUtils {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";

	public static String getUniqueId()
	{
		long currTimeMillis = System.currentTimeMillis();
		String uniqueId = currTimeMillis + "";
		return uniqueId;
	}
	
	
	public static String getTimeStamp()
	{
		long currTimeMillis = System.currentTimeMillis();
		String timeStamp = currTimeMillis + "";
		return timeStamp;
	}
	
	
	public static void main(String[] args) {
		System.out.println( getUniqueId() );
		System.out.println( getTimeStamp() );
	}
}
