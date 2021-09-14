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
package oem.edge.ets_pmo.util;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

//import org.springframework.beans.factory.access.BeanFactoryLocator;
//import org.springframework.beans.factory.access.BeanFactoryReference;
//import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;

import oem.edge.ets_pmo.common.ETSPMOGlobalInitialize;
import oem.edge.ets_pmo.domain.PmoObject;

/**
 * @author shingte
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Helper {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(Helper.class);

	static Map map=null;
	private static String trail_hh_mm_ss = " 00:00:00.00000000";

	// init the PMO object type mapping
	public static void initObjectClass()
	{
		if (map!=null)
			return;
		map = new HashMap();
		map.put("PROJECT","WbsObject");
		map.put("PROPOSAL","WbsObject");
		map.put("DELIVERABLE","WbsObject");
		map.put("MILESTONE","WbsObject");
		map.put("WORKPRODUCT","WbsObject");
		map.put("TASK","WbsObject");
		map.put("SUMMARYTASK","WbsObject");
		map.put("TASK","WbsObject");
		map.put("ASSIGNEDRESOURCE","ResObject");
		map.put("DOCUMENT","DocObject");
		map.put("DOCUMENTFOLDER","DocObject");
		map.put("NOTEMINUTES","DocObject");
		//map.put("CRIFOLDER","CriObject");
		map.put("CRIFOLDER","ExceptObject");
		map.put("ISSUE","ExceptObject");
		map.put("CHANGEREQUEST","ExceptObject");
		map.put("SERVICEREQUEST","ExceptObject");
		map.put("REQUIREMENT","ExceptObject");
		map.put("DEFECT","ExceptObject");
		map.put("ACTION","ExceptObject");
		map.put("RTF","RtfObject");
		
	}
	
	// based on the XML type, need to have obj_type=getObjectClass(type)
	// create and return the PMO object
	public static PmoObject getPmoObject(String obj_type)
	{
		//the caller function need to get the object type as input
		//String obj_type=getObjectClass(type);
		if(obj_type==null)
			return null;
		
		PmoObject pmo=null;
		Class cls=null;
		try {
			cls=Class.forName("oem.edge.ets_pmo.domain."+obj_type);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
            logger.error("getPmoObject(String)", e);
		}
		try {
			pmo= (PmoObject) cls.newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
            logger.error("getPmoObject(String)", e1);
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
            logger.error("getPmoObject(String)", e1);
		}
		return pmo;
		
	}
	
	// return the PMO object class name from the Type
	public static String getObjectClass(String key)
	{
		initObjectClass();
		Object obj=map.get(key);
	    if (obj==null)
	    {
	    	logger.warn("getObjectClass: object "+key+" is not supported.");
	    	return null;
	    }
		return  (String)obj;
	}
	
	// use reflection to set the properties of the domain objects
	// from the matching parsing name and value
	//
	// This method call setMethodName to modify the method name
	public static void setObjectValue(Object obj, String m_str, Object value) //String v_str)
	{
		if (obj==null)
		 {
		 	logger.warn("setObjectValue: object does not exist to set the parameter "+m_str);
		 	return;
		 }
		Class cls=null;
		String m_name=setMethodName(m_str);
		try {
		 cls= obj.getClass();
		 
		 Method method = 
				getSupportedMethod(cls, m_name , new Class[]{value.getClass()}); //{String.class});
		 Object[] args={value};
		 if (method!=null)
		 	method.invoke(obj,args);

		}
		catch (NoSuchMethodException ex) {
			String clsName=(cls!=null)?  cls.getName() : " none ";
            logger.error("setObjectValue(Object, String, Object)"
                    + clsName + " - No such method: " + m_name, ex);
		}
		catch (Exception ex) {
			String clsName=(cls!=null)?  cls.getName() : " none ";
            logger.error("setObjectValue: "
                    + clsName + " - Exception in setting value for method "
                    + m_name, ex);
		}
	}

	// modify the name to confirm to javabean convention
	// from "method" or "METHOD" -> "setMethod"
	public static String setMethodName(String m_str)
	{
		String m1_str=m_str.substring(0,1).toUpperCase();
		String m2_str=m_str.substring(1).toLowerCase();
		//		 replace space in m_str with '_'
		return ("set"+m1_str+m2_str).replace(' ','_');
		
	}

	/**
	 * @param cls
	 * @param m_str
	 * @param classes
	 * @return
	 */
	private static Method getSupportedMethod(Class cls, String m_str, Class[] types)
	 throws NoSuchMethodException {
		// TODO Auto-generated method stub
		if (cls==null) {
			throw new NoSuchMethodException();
		}
		try {
			return cls.getDeclaredMethod(m_str, types);
		}
		catch (NoSuchMethodException ex) {
			return getSupportedMethod(cls.getSuperclass(),m_str,types);
		}
	}
	
	// String to Timestamp converter
	public static Timestamp toTimestamp(String str) 
	   {
	 		Timestamp ts=null;
			if(str==null || str.trim().equals(""))
				return null;
			try{
				ts = Timestamp.valueOf(str);	
			}
			catch(IllegalArgumentException ie){
				str += trail_hh_mm_ss;
				ts = Timestamp.valueOf(str);
			}    
			return ts;
	   }

	// RTF to TXT converter
	// set decode to true if the input is in base64 encoded form
	public static String rtf2txt(String rtf, boolean decode)
	{
		if (rtf==null || rtf.equals(""))
			return rtf;
		DefaultStyledDocument styledDoc = new DefaultStyledDocument();
		ByteArrayInputStream bis = new ByteArrayInputStream(rtf.getBytes());
		Base64.InputStream b64is = null;
		String txt=null;
		try {
			if (decode)
			{
				b64is= new Base64.InputStream( bis, Base64.DECODE );
				new RTFEditorKit().read(b64is, styledDoc, 0);
			}
			else
				new RTFEditorKit().read(bis, styledDoc, 0);
			txt = styledDoc.getText(0, styledDoc.getLength());
			} catch (IOException e) {
				// TODO Auto-generated catch block
                logger.error("rtf2txt(String, boolean)", e);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
                logger.error("rtf2txt(String, boolean)", e);
			}
		return txt;
	}

	// TXT to RTF converter
	// set encode to true if the output is in base64 encoded form
	public static String txt2rtf(String txt, boolean encode) {
	    String rtf=null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		RTFEditorKit kit = new RTFEditorKit();
		try{
	        Document doc = kit.createDefaultDocument();
            doc.insertString(0, txt, null);
            kit.write(bos, doc, 0, doc.getLength());
		    if (encode)
				rtf = Base64.encodeBytes(bos.toByteArray());
            else
                rtf = bos.toString();
              
		}
		catch(BadLocationException bad){ 
					logger.info("BadLocation exception caused while coverting the following text to rtf : " + txt);
			 } 
		catch(IOException ioe){ 
					logger.info("IOException caused while coverting the following text to rtf : " + txt);
				}
				
		return rtf;
		
	}
	
	public static String getTmpDir()
	{
		ETSPMOGlobalInitialize.Init();
		String dir = ETSPMOGlobalInitialize.getProjectTmpDir();
		if (dir==null)
			dir="";
		return dir;
		
	}
	
	// Spring bootstrapping config reader
	/*
	public static Object getBean(String name)
	{
		BeanFactoryLocator bfLocator = SingletonBeanFactoryLocator.getInstance("oem/edge/ets_pmo/beanRefFactory.xml");
		   BeanFactoryReference bf = bfLocator.useBeanFactory("beanFactory");
		   // now use some bean from factory
		   return bf.getFactory().getBean(name);

	}
	*/
	
	public static void main(String[] args)
	{
	    String test="This is a test string for RTF<->TXT converter and base64 encoding.";
	    System.out.println("Input TXT is - \n"+test);
	    String rtf = txt2rtf(test, true);
	    System.out.println("\nencoded RTF is - \n"+rtf);
	    System.out.println("\ndecoded TXT is - \n"+rtf2txt(rtf,true));
	}
}