/*   ------------------------------------------------------------------          */
/*   IBM                                                                                    */
/*                                                                                               */  
/*   OCO Source Materials                                                          */
/*                                                                                               */
/*   Product(s): ICC/PROFIT                                                       */
/*                                                                                               */
/*   (C)Copyright IBM Corp. 2002,2003 		              */ 
/*                                                                                               */  
/*   The source code for this program is not published or otherwise */
/*   divested of its trade secrets, irrespective of what has been        */
/*   deposited with the US Copyright Office.                                  */
/*   ------------------------------------------------------------------           */

package oem.edge.ets.fe;

import java.util.*;
import java.io.*;

import javax.mail.*;
import javax.activation.*;
import javax.mail.internet.*;
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

public class SMTPMail
{
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";

	private String mailhost;
	private String to;
	private String from;
	private String mailText;
	private String cc;
	private String bcc;
	private String subject;
	private String mailer;

	public SMTPMail()
	{
		mailer = "eDesignMailer";
	}

	public void setOriginator(String from)
	{
		this.from = from;
	}

	public String getOriginator()
	{
		return from;
	}

	public void setMailHost(String hostname)
	{
		this.mailhost = hostname;
	}

	public String getMailHost()
	{
		return mailhost;
	}

	public void setRecipients(String recipientlist)
	{
		this.to = recipientlist;
	}

	public String getRecipients()
	{
		return to;
	}

	public void setCCs(String ccList)
	{
		this.cc = ccList;
	}

	public String getCCs()
	{
		return cc;
	}

	public void setBCCs(String bccList)
	{
		this.bcc = bccList;
	}

	public String getBCCs()
	{
		return bcc;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setMailText(String text)
	{
		this.mailText = text;
	}

	public String getMailText()
	{
		return mailText;
	}

	public void send()
		throws MessagingException
	{
	    System.out.println("SMTP MSG: new smtp");
	    /* sps
	    Properties props = System.getProperties();
	    // XXX - could use Session.getTransport() and Transport.connect()
	    // XXX - assume we're using SMTP
	    if (mailhost != null)
		{
			props.put("mail.smtp.host", mailhost);
		}

	    // Get a Session object
	    Session session = Session.getDefaultInstance(props, null);
	    //if (debug)
		//session.setDebug(true);
	     sps */

	    //sps 2-13-03
	    try {
		Session session = oem.edge.common.MailSessionFactory.createSession();
		// construct the message
		Message msg = new MimeMessage(session);
		if (from != null)
		    {
			msg.setFrom(new InternetAddress(from));
		    }
		else
		    {
			msg.setFrom();
		    }

		msg.setRecipients(Message.RecipientType.TO,
				  InternetAddress.parse(to, false));

		if (cc != null)
		    {
			msg.setRecipients(Message.RecipientType.CC,
					  InternetAddress.parse(cc, false));
		    }
		if (bcc != null)
		    {
			msg.setRecipients(Message.RecipientType.BCC,
					  InternetAddress.parse(bcc, false));
		    }

		msg.setSubject(subject);
		msg.setText(mailText);

		msg.setHeader("X-Mailer", mailer);
		msg.setSentDate(new Date());

		// send the thing off
		Transport.send(msg);
       	    }
	    catch(MessagingException me){
		System.out.println("SMTPMail.send()  MessagingEXCEPTION - trying to get/send mailsession factory");
		System.out.println("error="+me);
		me.printStackTrace();
		throw me;
	    }
	    catch(Exception e){
		System.out.println("SMTPMail.send()  EXCEPTION - trying to get mailsession factory");
		System.out.println("error="+e);
		e.printStackTrace();
	    }
	}
}
