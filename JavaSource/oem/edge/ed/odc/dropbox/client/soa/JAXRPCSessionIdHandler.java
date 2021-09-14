package oem.edge.ed.odc.dropbox.client.soa;

import javax.xml.rpc.handler.*;
import javax.xml.rpc.handler.soap.*;
import javax.xml.namespace.*;
import javax.xml.soap.*;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;

public class JAXRPCSessionIdHandler extends javax.xml.rpc.handler.GenericHandler {

   HandlerInfo hinfo;

   public JAXRPCSessionIdHandler() {}

  // We simply want to embed the dboxsessionid in the header
   public boolean handleRequest(MessageContext context) {
   
      try {
      
         // get SOAP message context
         SOAPMessageContext smc = (SOAPMessageContext) context;
         // get SOAP envelope from SOAP message
         SOAPEnvelope se = smc.getMessage().getSOAPPart().getEnvelope();
         // create instance of SOAP factory
         SOAPFactory sFactory = SOAPFactory.newInstance();
         
         // get SOAPHeader instance for SOAP envelope
         SOAPHeader sh = se.getHeader();
         if (sh == null) {
            sh = se.addHeader();
         }
         
        // Create a name for env header
         Name headerName = sFactory.createName("dboxsessionid", 
                                               "DropboxAccess", 
                                               "http://dboxsessionid/");
         SOAPHeaderElement headerElement = sh.addHeaderElement(headerName);
         headerElement.setActor("http://DropboxAccess/dboxsessionid");
         
         // attach value to id element
         headerElement.addTextNode((String)(hinfo.getHandlerConfig().get("SessionID")));

        //System.out.println("Envelope=" + se.toString());
                          
      }   catch (Exception ex) {
         ex.printStackTrace();
         return false;
      }     
      return true;
   }
   
   public boolean handleResponse(MessageContext context) {
      try {
         // get SOAP message context
         SOAPMessageContext smc = (SOAPMessageContext) context;
        // get SOAP envelope from SOAP message
         SOAPEnvelope se = smc.getMessage().getSOAPPart().getEnvelope();
        //System.out.println("Envelope=" + se.toString());
      }   catch (Exception ex) {
         ex.printStackTrace();
         return false;
      }     
   
      return true;
   }
   
   public boolean handleFault(MessageContext context) {
      try {
         // get SOAP message context
         SOAPMessageContext smc = (SOAPMessageContext) context;
         // get SOAP envelope from SOAP message
         SOAPEnvelope se = smc.getMessage().getSOAPPart().getEnvelope();
        // System.out.println("Envelope=" + se.toString());
      }   catch (Exception ex) {
         ex.printStackTrace();
         return false;
      }     
   
      return true;
   }
   
   public void init(HandlerInfo config) {
      hinfo = config;
   }
   
   public void destroy() {
   }
   
   public QName[] getHeaders() {
      return null;
   }
}
