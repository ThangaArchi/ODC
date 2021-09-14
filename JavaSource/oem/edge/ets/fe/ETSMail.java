/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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


/*
 * Created on Jan 26, 2005
 */
 
package oem.edge.ets.fe;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSMail {
	/**
	 * 
	 */
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.1";
	
	private String From = "";
	private String To = "";
	private String Cc = "";
	private String Bcc = "";
	private String Subject = "";
	private String Message = "";
	private String ReplyTo = "";

	public ETSMail() {
		super();
	}
	
	
	/**
	 * @return
	 */
	public String getBcc() {
		return Bcc;
	}

	/**
	 * @return
	 */
	public String getCc() {
		return Cc;
	}

	/**
	 * @return
	 */
	public String getFrom() {
		return From;
	}

	/**
	 * @return
	 */
	public String getMessage() {
		return Message;
	}

	/**
	 * @return
	 */
	public String getSubject() {
		return Subject;
	}

	/**
	 * @return
	 */
	public String getTo() {
		return To;
	}

	/**
	 * @param string
	 */
	public void setBcc(String string) {
		Bcc = string;
	}

	/**
	 * @param string
	 */
	public void setCc(String string) {
		Cc = string;
	}

	/**
	 * @param string
	 */
	public void setFrom(String string) {
		From = string;
	}

	/**
	 * @param string
	 */
	public void setMessage(String string) {
		Message = string;
	}

	/**
	 * @param string
	 */
	public void setSubject(String string) {
		Subject = string;
	}

	/**
	 * @param string
	 */
	public void setTo(String string) {
		To = string;
	}

	/**
	 * @return
	 */
	public String getReplyTo() {
		return ReplyTo;
	}

	/**
	 * @param string
	 */
	public void setReplyTo(String string) {
		ReplyTo = string;
	}

}
