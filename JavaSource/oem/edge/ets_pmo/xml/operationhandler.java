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
import oem.edge.ets_pmo.domain.OperObject;
import oem.edge.ets_pmo.domain.TransObject;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.helpers.DefaultHandler;

public class operationhandler extends DefaultHandler
{
    private CharArrayWriter text = new CharArrayWriter ();
    private Stack path;
    private Map params;
    private DefaultHandler parent;
    private SAXParser parser;
    private OperObject oper= new OperObject();
	private StringBuffer buffer;

    public operationhandler(Stack path, Map params, Attributes attributes, SAXParser parser, DefaultHandler parent)  throws SAXException
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
              String type = attributes.getValue("type");
              oper.setType(type);
              
              buffer.append("<operation type=\""+type+"\">\n");
    }

    public void end () throws SAXException
    {
    	params.put("operation",oper);
    	TransObject parent_obj=((transactionhandler) parent).getTrans();
    	parent_obj.setOperation(oper);
    	
    	buffer.append("</operation>\n");
    }

    
    public void startuserid (Attributes attributes) throws SAXException
    {
      
    }

      
    public void enduserid () throws SAXException
    {
    	oper.setUserid(getText());
    }

      
    
    public void startobject (Attributes attributes) throws SAXException
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
        
        if (qName.equals("userid")) startuserid (attributes);  
            if (qName.equals("object"))
            {
              startobject (attributes);
              path.push("object");
              DefaultHandler handler = new objecthandler(path,params,attributes,parser,this);
              parser.setContentHandler (handler);
            }  
    }

    public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName) throws SAXException
    {
        if (qName.equals("operation"))
        {
            end();
            path.pop();
            parser.setContentHandler (parent);
        }

        if (qName.equals("userid")) enduserid ();            
        
    }

    public void characters(char[] ch, int start, int length)
    {
        text.write (ch,start,length);
    }


	/**
	 * @return
	 */
	public OperObject getOper() {
		// TODO Auto-generated method stub
		return oper;
	}


}