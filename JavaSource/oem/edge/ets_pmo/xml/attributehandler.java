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

import org.apache.log4j.Logger;

// JDK Classes
import java.util.*;
import java.io.*;

// Xerces Classes
import oem.edge.ets_pmo.domain.PmoObject;
import oem.edge.ets_pmo.domain.RtfObject;
import oem.edge.ets_pmo.util.Base64;
import oem.edge.ets_pmo.util.Helper;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.helpers.DefaultHandler;

public class attributehandler extends DefaultHandler
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(attributehandler.class);

    private CharArrayWriter text = new CharArrayWriter ();
    private Base64.OutputStream b64o = null;
    private BufferedOutputStream out = null;
    private Stack path;
    private Map params;
    private DefaultHandler parent;
    private SAXParser parser;

	private StringBuffer buffer;
  

    public attributehandler(Stack path, Map params, Attributes attributes, SAXParser parser, DefaultHandler parent)  throws SAXException
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
        params.put("type",type);
        
		//if (logger.isDebugEnabled()) {
		//	logger.debug("start(Attributes)" + type);
		//}
      
        	
    }

    public void end () throws SAXException
    {
    	String type=null;
    	String name=null;
    	String value=null;
    	String rank=null;
    	Object obj=params.remove("type");
    	//String id=((objecthandler)parent).getId();
    	//System.out.println(" parent id="+id);
    	PmoObject pmo=((objecthandler) parent).getPmo();
    	
    	if (obj!=null)
    	{
    		type=obj.toString();
			
			//logger.debug("<attribute type=\""+type+"\">");
			buffer.append("<attribute type=\""+type+"\">\n");
    	}
    	
    	obj=params.remove("name");
    	if (obj!=null)
    	{
    		name=obj.toString();
			
			//logger.debug("<name>"+name+"</name>");
			buffer.append("<name>"+name+"</name>\n");
    	}
    	obj=params.remove("value");
    	if (obj!=null)
    	{
    		value=obj.toString();
			
			//logger.debug("<value>"+value+"</value>");
			buffer.append("<value>"+value+"</value>\n");
			
    	}
    	obj=params.remove("rank");
    	if (obj!=null)
    	{
    		rank=obj.toString();
			if (logger.isDebugEnabled()) {
				//logger.debug("end() -   rank=" + rank);
			}
			buffer.append("<rank>"+rank+"</rank>\n");
    	}
    	if(type.equals("RTF"))
    	{
    		// convert RTF value to plain text
    		// value is in base64 encoded form, set decode to true
    		value=Helper.rtf2txt(value, true);
			if (logger.isDebugEnabled()) {
				logger.debug("RTF - converted  value=" + value);
			}
    		// populate RtfObject
    		RtfObject rtf = new RtfObject();
    		rtf.setName(name);
    		rtf.setValue(value);
    		rtf.setRank(rank);
    		// add RtfObject to the parent object
    		Helper.setObjectValue(pmo,"RtfObject",rtf);
    	}
    	else if(type.equals("EXCEPTION")) // this is NACK
    	{
    		Helper.setObjectValue(pmo,"nack",name);
    	}
    	else
    	{
    		Helper.setObjectValue(pmo,name,value);
    	}
    	
    	buffer.append("</attribute>\n");
    }

    
    public void startname (Attributes attributes) throws SAXException
    {
      
    }

      
    public void endname () throws SAXException
    {
    	String str=getText();
    	params.put("name",str);
    	
    }

      
    
    public void startvalue (Attributes attributes) throws SAXException
    {
    	 String type = (String) params.get("type");
         if (type.trim().equals("BINARY"))
         {
         	Object obj=params.get("name");
         	String name=(obj==null)? "" : obj.toString();
         	if (name.equals("BLOB_DATA"))
         	{
         		String filename=Helper.getTmpDir()+"tmp_";//((objecthandler)parent).getId();
         		filename += System.currentTimeMillis();
         		filename += ".zip";
         		
         		try {
 					out = new BufferedOutputStream(new FileOutputStream(filename));
 					b64o = new Base64.OutputStream( out, Base64.DECODE );
 				} catch (IOException e) {
 					// TODO Auto-generated catch block
					logger.error("startvalue(Attributes)", e);
 				}
                params.put("filename",filename);

         	}
         	
         }
    }

      
    public void endvalue () throws SAXException
    {
    	String str=null;
    	Object obj=params.get("filename");
    	if (obj==null)
    		params.put("value",getText());
    	else
    	{
    		params.put("value",params.remove("filename"));
    		try {
    			b64o.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("endvalue()", e);
			}
    	}
	
    }

      
    
    public void startrank (Attributes attributes) throws SAXException
    {
      
    }

      
    public void endrank () throws SAXException
    {
    	String str=getText();
    	params.put("rank",str);
    
    }

    public String getText()
    {
        return text.toString().trim();
    }

    public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName, Attributes attributes) throws SAXException
    {
        text.reset();
    	//text=new StringBuffer();
        
        if (qName.equals("name")) startname (attributes);          
        if (qName.equals("value")) startvalue (attributes);
        if (qName.equals("rank")) startrank (attributes);
       
    }

    public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName) throws SAXException
    {
        if (qName.equals("attribute"))
        {
            end();
            path.pop();
            parser.setContentHandler (parent);
        }
    
        if (qName.equals("name")) endname ();
        if (qName.equals("value")) endvalue ();
        if (qName.equals("rank")) endrank ();

    }

    public void characters(char[] ch, int start, int length)
    {
    	Object obj=params.get("filename");
    	if (obj==null)
    		text.write (ch,start,length);
    		//text.append(ch,start,length);
    	else
    	{
    		try {
			//out.write (ch,start,length);
    		// File content is zip compressed and base64 encoded in xml
    		// decode and save it as a .zip file
    			String str=new String(ch,start,length);	
    			b64o.write(str.getBytes());
    		} catch (IOException e) {
				logger.error("characters(char[], int, int)", e);
    		} catch (Exception e) {
				logger.error("characters(char[], int, int)", e);
        	}
    	}
    	
    		
    }


}