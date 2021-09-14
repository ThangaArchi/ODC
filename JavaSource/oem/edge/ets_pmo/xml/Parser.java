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
import oem.edge.ets_pmo.domain.*;

import org.xml.sax.*;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xerces.parsers.*;
import org.xml.sax.helpers.DefaultHandler;

public class Parser extends DefaultHandler
{
    SAXParser parser = null;
    Stack path = null;
    Map params = null;
    StringBuffer buffer = null; 
    
    public Parser()
    {
    	parser = new SAXParser();
    	path = new Stack();
    	params = new HashMap();
    	buffer = new StringBuffer();
    	params.put("buffer",buffer);
    	
    }
    
    public void startDocument() throws SAXException
    {
    }

    public void endDocument() throws SAXException
    {
    }

    public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName, Attributes attributes) throws SAXException
    {
        if (qName.equals("pmoffice"))
        {
            DefaultHandler handler = new pmofficehandler(path,params,attributes,parser,this);
            path.push ("pmoffice");
            parser.setContentHandler (handler);
        }

    }


    public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName) throws SAXException
    {
    }

    public Map parse (InputStream is) throws SAXException, IOException
    {
        parser.setContentHandler (this);
        parser.parse( new InputSource (is));

        return params;
    }

    public static void main (String[] args) throws Exception
    {
    	PropertyConfigurator.configure(args[0]);
    	System.out.println("Memory before parsing -> Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory());
        Parser parser = new Parser();
        FileInputStream fis = new FileInputStream (args[1]);
        Map params = parser.parse (new BufferedInputStream(fis)); //(fis);
        
        StringBuffer buf = (StringBuffer)params.remove("buffer");
        System.out.println(buf.toString());
        /*
        FileOutputStream out = null;
		try {
			out = new FileOutputStream("output.xml");
			out.write(buf.toString().getBytes());
			out.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		*/
        System.out.println("Memory after parsing -> Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory());
        TransObject trans=(TransObject)params.remove("transaction");
        System.out.println(trans.toString());
        /*
        OperObject oper=(OperObject)params.remove("operation");
        System.out.println(oper.toString());
        WbsObject obj = (WbsObject)params.remove("object");
        System.out.println(obj.toString());
        */
        System.out.println("Memory after printing -> Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory());
    }

}
