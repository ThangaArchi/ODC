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
import oem.edge.ets_pmo.domain.OperObject;
import oem.edge.ets_pmo.domain.PmoObject;
import oem.edge.ets_pmo.util.Helper;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.helpers.DefaultHandler;

public class objecthandler extends DefaultHandler
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(objecthandler.class);

    private CharArrayWriter text = new CharArrayWriter ();
    private Stack path;
    private Map params;
    private DefaultHandler parent=null;
    private SAXParser parser;
    private PmoObject pmo=null;

	private StringBuffer buffer;

    public objecthandler(Stack path, Map params, Attributes attributes, SAXParser parser, DefaultHandler parent)  throws SAXException
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
			buffer.append("<object type=\""+type+"\">\n");
			
              String obj_type=Helper.getObjectClass(type);
              pmo=Helper.getPmoObject(obj_type);
              if (pmo!=null)
              {
                Helper.setObjectValue(pmo,"type",type);
                params.put("obj_type",obj_type);
              }
              
     }

    public void end () throws SAXException
    {
    	if (pmo==null)
    		return;
    	String parent_str=parent.getClass().toString();
		// The root objecthandler has parent as operationhandler 
    	// if class name contains "object", means it is not root obj 
    	if (parent_str.indexOf("object")>=0)
    	{
    		PmoObject parent_pmo=((objecthandler) parent).getPmo();
    		
    		String id = pmo.getId();
    		String obj_type=(String) params.remove(id);
    		// set the parent-child relationship
    		////if (obj_type != null)
    			Helper.setObjectValue(parent_pmo,obj_type,pmo); // set pmo in the List of parent_pmo
    		pmo.setParent(parent_pmo); // set parent_pmo in pmo
    		
    	}
    	else // This is root object
    	{
    		OperObject parent_obj=((operationhandler) parent).getOper();
    		parent_obj.setObject(pmo);
    		params.put("object",pmo);
    	}
    	
    	buffer.append("</object>\n");
    }

    
    public void startid (Attributes attributes) throws SAXException
    {
      
    }

      
    public void endid () throws SAXException
    {
    	
    	String id=getText();
		
    	buffer.append("<id>"+id+"</id>\n");
    	
    	if (pmo==null)
    		return;
    	// use the id as key to store obj_type, to be retrived 
    	// as the end of obj parsing, and add to parent obj
    	String obj_type=(String) params.remove("obj_type");
    	params.put(id,obj_type);
    	Helper.setObjectValue(pmo,"id",id);
    	String parent_str=parent.getClass().toString();
    	// The root objecthandler has parent as operationhandler 
    	if (parent_str.indexOf("operation")>=0)
    	{
    		Helper.setObjectValue(pmo,"root_id",id);
    	}
    	else
    	{
    		PmoObject parent_pmo=((objecthandler) parent).getPmo();
    		if (parent_pmo!=null)
    			Helper.setObjectValue(pmo,"root_id",parent_pmo.getRoot_id());
    	}
    		
    }

      
    
    public void startattribute (Attributes attributes) throws SAXException
    {
              //String type = attributes.getValue("type");
              //System.out.println("attr type="+type);
      
    }

            
    
    public void startobject (Attributes attributes) throws SAXException
    {
    //  String type = attributes.getValue("type");
	
      
    }

            
    

    public String getText()
    {
        return text.toString().trim();
    }

    public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName, Attributes attributes) throws SAXException
    {
        text.reset();
        
        if (qName.equals("id")) startid (attributes);
                  
          
        if (qName.equals("attribute"))
            {
              startattribute (attributes);
              path.push("attribute");
              DefaultHandler handler = new attributehandler(path,params,attributes,parser,this);
              parser.setContentHandler (handler);
            }

                  
          
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
        if (qName.equals("object"))
        {
            end();
            path.pop();
            parser.setContentHandler (parent);
        }
        
        if (qName.equals("id")) endid ();             
        
    }

    public void characters(char[] ch, int start, int length)
    {
       text.write (ch,start,length);
    }


	/**
	 * @return Returns the pmo.
	 */
	public PmoObject getPmo() {
		return pmo;
	}
}