package oem.edge.ed.sd.ordproc;

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
/////////////////////////////////////////////////////////////////////////////
//
//                            Edge 2.10
//                      Dev: Navneet Gupta
//
//////////////////////////////////////////////////////////////////////////////

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;
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

public class Mailer {

   public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    public static synchronized void sendMail(String mailhost, String from, String to, String cc, String bcc, String replyTo, String subject, String body) throws MessagingException {

        try {

            Properties props = new Properties();

	    if (mailhost != null)
                props.put("mail.smtp.host", mailhost);
            else
                props.put("mail.smtp.host", "us.ibm.com");

            Session session = Session.getDefaultInstance(props, null);

	    MimeMessage msg = new MimeMessage(session);

	    if(from != null && from.trim().length() != 0)
                msg.setFrom(new InternetAddress(from.trim()));
	    else
                msg.setFrom();


	    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));


	    if(cc != null && cc.trim().length() != 0)
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));

	    if(bcc != null && bcc.trim().length() != 0)
                msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));

            if(replyTo != null && replyTo.trim().length() != 0)
                msg.setReplyTo(InternetAddress.parse(replyTo, false));
            

	    msg.setSubject(subject);
            msg.setText(body);

            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.send(msg);
            transport.close();

        }
        catch(MessagingException e) {
            try {
                Thread.sleep(5000);
            }
            catch(InterruptedException ie) { }
            
            throw e;
        }
    }
}
