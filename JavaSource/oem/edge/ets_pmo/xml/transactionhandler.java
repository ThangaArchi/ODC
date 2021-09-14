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
package oem.edge.ets_pmo.xml;

// JDK Classes
import java.util.*;
import java.io.*;

// Xerces Classes
import oem.edge.ets_pmo.domain.TransObject;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.helpers.DefaultHandler;

public class transactionhandler extends DefaultHandler
{
    private CharArrayWriter text = new CharArrayWriter ();
    private Stack path;
    private Map params;
    private DefaultHandler parent;
    private SAXParser parser;
    private TransObject trans = new TransObject();
	private StringBuffer buffer;

    public transactionhandler(Stack path, Map params, Attributes attributes, SAXParser parser, DefaultHandler parent)  throws SAXException
    {
        this.path = path;
        this.params = params;
        this.parent = parent;
        this.parser = parser;
        this.buffer = (StringBuffer)params.get("buffer");
        start(attributes);
    }


    public void start (Attributes attributes)  throws SAXException
    {
         String version = attributes.getValue("version");
         trans.setVersion(version);
         buffer.append("<transaction version=\""+version+"\"");
         
         String tstamp = attributes.getValue("timestamp");
         if (tstamp!= null)
         {
         	trans.setTimestamp(tstamp);
         	buffer.append(" timestamp=\""+tstamp+"\"");
         }
         
         buffer.append(">\n");
    }

    public void end () throws SAXException
    {
    	params.put("transaction",trans);
    	
    	buffer.append("</transaction>\n");
    }

    
    public void startid (Attributes attributes) throws SAXException
    {
      
    }

      
    public void endid () throws SAXException
    {
    	trans.setId(getText());
    	
    	buffer.append("<id>"+getText()+"</id>\n");
    }

      
    
    public void startsource (Attributes attributes) throws SAXException
    {
      
    }

      
    public void endsource () throws SAXException
    {
    	trans.setSource(getText());
    	
    	buffer.append("<source>"+getText()+"</source>\n");
    }

      
    
    public void startdestination (Attributes attributes) throws SAXException
    {
      
    }

      
    public void enddestination () throws SAXException
    {
    	trans.setDestination(getText());
    	
    	buffer.append("<destination>"+getText()+"</destination>\n");
    }

      
    
    public void startapp (Attributes attributes) throws SAXException
    {
      
    }

      
    public void endapp () throws SAXException
    {
    	trans.setApp(getText());
    	
    	buffer.append("<app>"+getText()+"</app>\n");
    }

      
    
    public void startoperation (Attributes attributes) throws SAXException
    {
        String type = attributes.getValue("type");
    }

            
    

    public String getText()
    {
        return text.toString().trim();
    }

    public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName, Attributes attributes) throws SAXException
    {
        text.reset();
        
        if (qName.equals("id")) startid (attributes);
        if (qName.equals("source")) startsource (attributes);
        if (qName.equals("destination")) startdestination (attributes);
        if (qName.equals("app")) startapp (attributes);
                  
        if (qName.equals("operation"))
            {
              startoperation (attributes);
              path.push("operation");
              DefaultHandler handler = new operationhandler(path,params,attributes,parser,this);
              parser.setContentHandler (handler);
            }

                  
    }

    public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName) throws SAXException
    {
        if (qName.equals("transaction"))
        {
            end();
            path.pop();
            parser.setContentHandler (parent);
        }

        if (qName.equals("id")) endid ();
        if (qName.equals("source")) endsource ();      
        if (qName.equals("destination")) enddestination ();
        if (qName.equals("app")) endapp ();            
        
    }

    public void characters(char[] ch, int start, int length)
    {
        text.write (ch,start,length);
    }


	/**
	 * @return
	 */
	public TransObject getTrans() {
		// TODO Auto-generated method stub
		return trans;
	}


}