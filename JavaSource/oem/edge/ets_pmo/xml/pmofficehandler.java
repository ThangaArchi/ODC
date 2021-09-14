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
import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.helpers.DefaultHandler;

import oem.edge.ets_pmo.xml.transactionhandler;

public class pmofficehandler extends DefaultHandler
{
    private CharArrayWriter text = new CharArrayWriter ();
    private Stack path;
    private Map params;
    private DefaultHandler parent;
    private SAXParser parser;
    private StringBuffer buffer;

    public pmofficehandler(Stack path, Map params, Attributes attributes, SAXParser parser, DefaultHandler parent)  throws SAXException
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
    	buffer.append("<?xml version='1.0' encoding='UTF-8'?>\n");
    	buffer.append("<pmoffice>\n");
    }

    public void end () throws SAXException
    {
    	buffer.append("</pmoffice>\n");
    }

    
    public void starttransaction (Attributes attributes) throws SAXException
    {
             // String version = attributes.getValue("version");

      
    }

            
    

    public String getText()
    {
        return text.toString().trim();
    }

    public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName, Attributes attributes) throws SAXException
    {
        text.reset();
        
          
            if (qName.equals("transaction"))
            {
              starttransaction (attributes);
              path.push("transaction");
              DefaultHandler handler = new transactionhandler(path,params,attributes,parser,this);
              parser.setContentHandler (handler);
            }

                  
    }

    public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName) throws SAXException
    {
        if (qName.equals("pmoffice"))
        {
            end();
            path.pop();
            parser.setContentHandler (parent);
        }

        
                    
        
    }

    public void characters(char[] ch, int start, int length)
    {
        text.write (ch,start,length);
    }


}