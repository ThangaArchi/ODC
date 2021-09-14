package oem.edge.ets_pmo.common.mail;

/************************** EOF : HEADER *************************************/
/////////////////////////////////////////////////////////////////////////////
//
//                            Edge 2.10
//                      Dev: Navneet Gupta
//
//////////////////////////////////////////////////////////////////////////////

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.*;

import org.apache.log4j.Logger;

import java.util.Properties;
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

public class Mailer {
	private static String CLASS_VERSION = "4.5.1";
	static Logger logger = Logger.getLogger(Mailer.class);
	public static synchronized void sendMail(
		String mailhost,
		String from,
		String to,
		String cc,
		String bcc,
		String replyTo,
		String subject,
		String body, String filename) {
		
		MimeMessage msg = null;
		Session session = null;
		try {

			Properties props = new Properties();

			if (mailhost != null)
				props.put("mail.smtp.host", mailhost);
			else
				props.put("mail.smtp.host", "us.ibm.com");

			session = Session.getDefaultInstance(props, null);

			msg = new MimeMessage(session);

			if (from != null && from.trim().length() != 0)
				msg.setFrom(new InternetAddress(from.trim()));
			else
				msg.setFrom();

			msg.setRecipients(
				Message.RecipientType.TO,
				InternetAddress.parse(to, false));

			if (cc != null && cc.trim().length() != 0)
				msg.setRecipients(
					Message.RecipientType.CC,
					InternetAddress.parse(cc, false));

			if (bcc != null && bcc.trim().length() != 0)
				msg.setRecipients(
					Message.RecipientType.BCC,
					InternetAddress.parse(bcc, false));

			if (replyTo != null && replyTo.trim().length() != 0)
				msg.setReplyTo(InternetAddress.parse(replyTo, false));

			msg.setSubject(subject);
			//msg.setText(body);
			/* subu adding files */
			BodyPart bp = new MimeBodyPart();
			bp.setText(body);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(bp);
			if(filename != null){
				bp = new MimeBodyPart();
				DataSource source = new FileDataSource(filename);
				bp.setDataHandler(new DataHandler(source));
				bp.setFileName(filename);
				multipart.addBodyPart(bp);
			}
			msg.setContent(multipart);
			
			Transport transport = session.getTransport("smtp");
			transport.connect();
			transport.send(msg);
			transport.close();

		} catch (MessagingException e) {

			logger.error("Couldnt connect to SMTPServer. Trial 1 failed. Trial 2 started.");
				try{
						Thread.sleep(5000);
						Transport transport = session.getTransport("smtp");
						transport.connect();
						Transport.send(msg);
						transport.close();
					}
					catch(MessagingException e1){
					
						logger.error("Couldnt connect to SMTPServer. Trial 2 failed. Trial 3 started.");
							try{
									Thread.sleep(5000);
									Transport transport = session.getTransport("smtp");
									transport.connect();
									Transport.send(msg);
									transport.close();
								}
								catch(MessagingException e2){
									
									logger.error("Couldnt connect to SMTPServer. Trial 3 failed. Trial 4 started.");
										try{
												Thread.sleep(5000);
												Transport transport = session.getTransport("smtp");
												transport.connect();
												Transport.send(msg);
												transport.close();
											}
											catch(MessagingException e3){
												
												logger.error("Couldnt connect to SMTPServer. Trial 4 failed. Trial 5 started.");
													try{
															Thread.sleep(5000);
															Transport transport = session.getTransport("smtp");
															transport.connect();
															Transport.send(msg);
															transport.close();	
														}
														catch(MessagingException e4){
															logger.error("FAILED 5 trials. SMTP Server is down...");
															logger.error(getStackTrace(e4));
														}
														catch(InterruptedException ie){
															logger.error(getStackTrace(ie));
														}
											}
											catch(InterruptedException ie){
												logger.error(getStackTrace(ie));
											}
								}
								catch(InterruptedException ie){
									logger.error(getStackTrace(ie));
								}
					}
					catch(InterruptedException ie){
						logger.error(getStackTrace(ie));
					}
			} 
		
	}
	static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}
	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}
}
