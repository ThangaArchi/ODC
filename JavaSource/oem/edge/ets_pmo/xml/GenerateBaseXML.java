package oem.edge.ets_pmo.xml;


import oem.edge.ets_pmo.common.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.log4j.Logger;

import oem.edge.ets_pmo.datastore.Transaction;
import oem.edge.ets_pmo.mq.XMLProcessor;

import java.io.*;
import java.util.*;
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

/**
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GenerateBaseXML {
	private static String CLASS_VERSION = "4.5.1";
	private static Hashtable ht=null; //store the txnId
    private Document doc;
    private String vTransactionXML = null;
    private String TransactionID = null;
    private String source = null;
    private String destination = null;
    private String app = null;
    private String operationType = null;
    private String userid = null;
    private String Project_ID = null;
	static Logger logger = Logger.getLogger(GenerateBaseXML.class);
    private String xmlMsg;
    
    private String corrId;
    private String MessId;
    private String Message;
    
	private Transaction trans = null;
     protected boolean checkData(){
    	boolean success = true;
    	if(getSource() == null){
    		print("source element: NULL");
    		success = false;
    	}
    	if(getDestination() == null){
    		print("destination element: NULL");
    		success = false;
    	}
    	if(getVTransactionXML() == null){
    		print("transaction version: NULL");
    		success = false;
    	}
    	if(getTransactionID() == null){
    		print("transaction id: NULL");
    		success = false;
    	}
    	if(getOperationType() == null){
    		print("transaction operation type: NULL");
    		success = false;
    	}
    /*	if(getProject_ID() == null){
   		print(" Project id : NULL", LogMsg.DEBUG_LEVEL1);
    		success = false;
    	}
    	if(getUserid() == null){
    		print(" User id is null" , LogMsg.DEBUG_LEVEL1);
    	}*/
    	if(getApp() == null){
    		print(" App is null" );
    		success = false;
    	}
    	return success;
     }
     
	public GenerateBaseXML(){
	xmlMsg = "";
	}
	public void loadTransactionData()
    {
     	// Inverse the source and destination
     	setSource(trans.getDestination());
        setDestination(trans.getSource());
        
        setVTransactionXML(trans.getTransactionVersion());
        setTransactionID(trans.getTransactionID());
        
        String proj_id = null;
        if(trans.getOperation().getProjObject() != null){
	      proj_id  = trans.getOperation().getProjObject().getProjectId();
        }
        setProject_ID(proj_id);
        setUserid(trans.getOperation().getUserID());
        
        //if(trans.getRepositoryApp() != null){
	        setApp(trans.getRepositoryApp());
       /* }
        else setApp(app);*/
        

    }
    public void registerTransactionData(Transaction trans){
    	
    	this.trans = trans;	
    }
	public void generate(){
		if(trans != null){
				loadTransactionData();
		}
			
    	try
        {
            //Create Document
            DocumentBuilderFactory dbf =DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.newDocument();
        }
        catch(Exception e)
        {
         
         logger.error("Error " + e);
            
        }
        Element pmoffice;
        pmoffice = doc.createElement("pmoffice");
        pmoffice.appendChild(doc.createTextNode("\n"));

        generateTransaction(pmoffice);

        doc.appendChild(pmoffice);
        
    	
    }
       public boolean generateTransaction(Element main){
       	boolean b = true;
    	Element id, source, destination, app, transaction;
        transaction = doc.createElement("transaction");
        transaction.setAttribute("version", getVTransactionXML());
        transaction.appendChild(doc.createTextNode("\n"));
        main.appendChild(transaction);
        main.appendChild(doc.createTextNode("\n"));
        
        id = doc.createElement("id");
        id.appendChild(doc.createTextNode(getTransactionID()));
        transaction.appendChild(id);
        transaction.appendChild(doc.createTextNode("\n"));
        
        source = doc.createElement("source");
        source.appendChild(doc.createTextNode(getSource()));
        transaction.appendChild(source);
        transaction.appendChild(doc.createTextNode("\n"));
        
        destination = doc.createElement("destination");
        destination.appendChild(doc.createTextNode(getDestination()));
        transaction.appendChild(destination);
        transaction.appendChild(doc.createTextNode("\n"));
        
        app = doc.createElement("app");
        app.appendChild(doc.createTextNode(getApp()));
        transaction.appendChild(app);
        transaction.appendChild(doc.createTextNode("\n\n"));
        
        
        
        b = generateOperation(transaction);
        return b;
       
    }
    
    public boolean generateOperation(Element main){
        boolean b = true;    	
    	Element operation, userid;
        
        operation = doc.createElement("operation");
        operation.setAttribute("type", getOperationType());
        operation.appendChild(doc.createTextNode("\n"));
        
        userid = doc.createElement("userid");
        userid.appendChild(doc.createTextNode(getUserid()));

        operation.appendChild(userid);
        operation.appendChild(doc.createTextNode("\n"));
        
        main.appendChild(operation);
        main.appendChild(doc.createTextNode("\n\n"));
     
		b = generateObjectProject(operation);
		return b;
    	
    }
    
    public boolean generateObjectProject(Element main){
    	
        return true;
    }

	/**
	 * Returns the app.
	 * @return String
	 */
	public String getApp() {
		return app;
	}

	/**
	 * Returns the destination.
	 * @return String
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * Returns the doc.
	 * @return Document
	 */
	public Document getDoc() {
		return doc;
	}

	/**
	 * Returns the operationType.
	 * @return String
	 */
	public String getOperationType() {
		return operationType;
	}

	/**
	 * Returns the project_ID.
	 * @return String
	 */
	public String getProject_ID() {
		return Project_ID;
	}

	/**
	 * Returns the source.
	 * @return String
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Returns the transactionID.
	 * @return String
	 */
	public String getTransactionID() {
		return TransactionID;
	}

	/**
	 * Returns the userid.
	 * @return String
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * Returns the vTransactionXML.
	 * @return String
	 */
	public String getVTransactionXML() {
		return vTransactionXML;
	}

	/**
	 * Sets the app.
	 * @param app The app to set
	 */
	public void setApp(String app) {
		this.app = app;
	}

	/**
	 * Sets the destination.
	 * @param destination The destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * Sets the doc.
	 * @param doc The doc to set
	 */
	public void setDoc(Document doc) {
		this.doc = doc;
	}

	/**
	 * Sets the operationType.
	 * @param operationType The operationType to set
	 */
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	/**
	 * Sets the project_ID.
	 * @param project_ID The project_ID to set
	 */
	public void setProject_ID(String project_ID) {
		Project_ID = project_ID;
	}

	/**
	 * Sets the source.
	 * @param source The source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Sets the transactionID.
	 * @param transactionID The transactionID to set
	 */
	public void setTransactionID(String transactionID) {
		TransactionID = transactionID;
	}

	/**
	 * Sets the userid.
	 * @param userid The userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * Sets the vTransactionXML.
	 * @param vTransactionXML The vTransactionXML to set
	 */
	public void setVTransactionXML(String vTransactionXML) {
		this.vTransactionXML = vTransactionXML;
	}
	
	 private void walk(Node node)
    {
    	
        int type = node.getNodeType();
        switch(type)
        {
            case Node.DOCUMENT_NODE:
            {
          	xmlMsg+="<?xml version=\"1.0\" encoding=\""+
                                "UTF-8" + "\"?>" ;
              break;                  
            }//end of document
            case Node.ELEMENT_NODE:
            {
          
                xmlMsg+='<' + node.getNodeName();
                NamedNodeMap nnm = node.getAttributes();
                if(nnm != null )
                {
                    int len = nnm.getLength() ;
                    Attr attr;
                    for ( int i = 0; i < len; i++ )
                    {
                        attr = (Attr)nnm.item(i);

                        xmlMsg+=' ' 
                             + attr.getNodeName()
                             + "=\""
                             + attr.getNodeValue()
                             +  '"' ;
                    }
                }
         
                xmlMsg+='>';
                
                break;
                
            }//end of element
            case Node.ENTITY_REFERENCE_NODE:
            {
               
     
               xmlMsg+='&' + node.getNodeName() + ';' ;
               break;
                
            }//end of entity
            case Node.CDATA_SECTION_NODE:
            {
         
                    xmlMsg+="<![CDATA[" 
                            + node.getNodeValue()
                            + "]]>" ;
                     break;       
                
            }
            case Node.TEXT_NODE:
            {
          
                xmlMsg+=node.getNodeValue();
                break;
            }
            case Node.PROCESSING_INSTRUCTION_NODE:
            {
        
                xmlMsg+="<?" 
                    + node.getNodeName()  ;
                String data = node.getNodeValue();
                if ( data != null && data.length() > 0 ) {
            
                    xmlMsg+="' '";
             
                    xmlMsg+=data;
                }
            
                xmlMsg+="?>";
                break;

             }
        }//end of switch
        
              
        //recurse
        for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
        {
            walk(child);
        }
        
        //without this the ending tags will miss
        if ( type == Node.ELEMENT_NODE )
        {
      
            xmlMsg+="</" + node.getNodeName() + ">";
        }
        

        
    }//end of walk
    public void print()
    {
    	
        walk(doc);
    }
    	
	public void print(String str){
		logger.debug(str);
	}



	/**
	 * Returns the xmlMsg.
	 * @return StringBuffer
	 */
	public String getXmlMsg() {
		return xmlMsg;
	}



	/**
	 * Returns the corrId.
	 * @return String
	 */
	public String getCorrId() {
		return corrId;
	}

	/**
	 * Returns the message.
	 * @return String
	 */
	public String getMessage() {
		return Message;
	}

	/**
	 * Returns the messId.
	 * @return String
	 */
	public String getMessId() {
		return MessId;
	}

	/**
	 * Returns the trans.
	 * @return Transaction
	 */
	public Transaction getTrans() {
		return trans;
	}

	/**
	 * Sets the corrId.
	 * @param corrId The corrId to set
	 */
	public void setCorrId(String corrId) {
		this.corrId = corrId;
	}

	/**
	 * Sets the message.
	 * @param message The message to set
	 */
	public void setMessage(String message) {
		Message = message;
	}

	/**
	 * Sets the messId.
	 * @param messId The messId to set
	 */
	public void setMessId(String messId) {
		MessId = messId;
	}

	public String handleSpecialChars(String str){
			StringBuffer retstr = new StringBuffer();	
			for(int i = 0; i < str.length() ; i++)
    			{
			      char c = str.charAt(i);
			      if( c == '&' )
			        retstr.append("&amp;");
			      else if (c == '<')
			        retstr.append("&lt;");
			      else if (c =='>')
			        retstr.append("&gt;");
			      else if (c == '\"')
			        retstr.append("&quot;");
			      else if (c == '\'')
			        retstr.append("&apos;");
			      else if (c < 127)
			        retstr.append(c);
			      else		
			      {
			          retstr.append("&#");
			          String code = Integer.toString(c);
			          retstr.append(code);
			          retstr.append(';');
			      }
			    }
	
			  return retstr.toString();
					
		}	
	protected File writeXMLFileToDisk(String TxnId, String type){
			String fileN = ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir()  + TxnId;	
			File f = new File(fileN);
			  try {
						FileOutputStream out = new FileOutputStream(f);   
						out.write(this.getXmlMsg().getBytes());
						out.close();
					}
					catch(Throwable t) {
						logger.warn(
										   "\n\n\n******************** !!! writeXMLFileToDisk ERROR !!! ********************\n"
										   + "WARNING!: Error writing " + type + " to " + fileN + " at " 
										   + new Date() + "\n"
										   + "Stack Trace:\n" 
										   + XMLProcessor.getStackTrace(t) + "\n"
										   + "While writing xml data\n" 
										   + "\n"
										   + "*****************************************************************\n\n\n"
										   );
					  
					}
			 return f;
		
		}
	public static boolean deleteXMLFileFromDisk(String TxnId){
		boolean rslt =false;
			String fileN = ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir()  + TxnId;	
			String fileNTrials = ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir()  + TxnId + "_NumberTrials";
			File f = new File(fileN);
			try{
				logger.debug(ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir() + "  deleting the Issue/CR file : " + f.getAbsolutePath());
				rslt = f.delete();
			}
		catch(Throwable t) {
								logger.warn(
												   "\n\n\n******************** !!! writeXMLFileToDisk ERROR !!! ********************\n"
												   + "WARNING!: Error deleting " + fileN + " at " 
												   + new Date() + "\n"
												   + "Stack Trace:\n" 
												   + XMLProcessor.getStackTrace(t) + "\n"
												   + "*****************************************************************\n\n\n"
												   );
							   
							f= null;          
		}
		f = new File(fileNTrials);
		try{
			logger.debug("deleting the Issue/CR file : " + f.getAbsolutePath());
			rslt = f.delete();
		}
		catch(Throwable t) {
							logger.warn(
											   "\n\n\n******************** !!! writeXMLFileToDisk ERROR !!! ********************\n"
											   + "WARNING!: Error deleting noofTrial File : " + fileNTrials + " at " 
											   + new Date() + "\n"
											   + "Stack Trace:\n" 
											   + XMLProcessor.getStackTrace(t) + "\n"
											   + "*****************************************************************\n\n\n"
											   );
						   
							f = null;          
		}
		return rslt;
	}
	protected static File writeReSendRetrialNumberToDisk(String TxnId, int TrialNo){
				String fileN = ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir()  + TxnId + "_NumberTrials";	
				File f = new File(fileN);
				Integer i = new Integer(TrialNo);
				String str = i.toString();
				  try {
							FileOutputStream out = new FileOutputStream(f);   
							out.write(str.getBytes());
							out.close();
						}
						catch(Throwable t) {
							logger.warn(
											   "\n\n\n******************** !!! writeReSendRetrialNumberToDisk ERROR !!! ********************\n"
											   + "WARNING!: Error writing  to " + fileN + " at " 
											   + new Date() + "\n"
											   + "Stack Trace:\n" 
											   + XMLProcessor.getStackTrace(t) + "\n"
											   + "While writing xml retrial number\n" 
											   + "\n"
											   + "*****************************************************************\n\n\n"
											   );
						        
						}
				 return f;
		
			}
	protected static String retrieveXMLFileFromDisk(String TxnId){
		String fileN = ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir()  + TxnId;
	//String fileN = "C:\\temp\\pmotest\\projectCreateUpdateDir\\"  + TxnId;
		File f = new File(fileN);
		String retStr = null;
		
		if (ht==null)
			ht=new Hashtable();
		if (ht.containsKey(TxnId))
			return retStr;
		try {
					  			
								FileInputStream in = new FileInputStream(f);
								
								int offset = 20;
								int len = 0;   
								
								//len = in.read(b,offset,b.length);
								byte b[] = new byte[in.available()];
								in.read(b);
								
								retStr = new String(b);
								logger.debug("The message  is : " + retStr);
								
							}
							catch(IOException e){
								retStr=null;
								if (ht.containsKey(TxnId)==false)
								{
									
								ht.put(TxnId,"1");
								logger.warn(
												   "\n\n\n******************** !!! retrieveXMLFileFromDisk ERROR !!! ********************\n"
												   + "WARNING!: Error to open " + fileN + " at " 
												   + new Date() + "\n"
												   + "Stack Trace:\n" 
												   + XMLProcessor.getStackTrace(e) + "\n"
												   + "While reading xml data\n" 
												   + "\n"
												   + "*****************************************************************\n\n\n"
												   );
								}
								
				
							}
							catch(Throwable t) {
								retStr=null;
								if (ht.containsKey(TxnId)==false)
								{
	
								ht.put(TxnId,"1");
								logger.warn(
												   "\n\n\n******************** !!! retrieveXMLFileFromDisk ERROR !!! ********************\n"
												   + "WARNING!: Error to read " + fileN + " at " 
												   + new Date() + "\n"
												   + "Stack Trace:\n" 
												   + XMLProcessor.getStackTrace(t) + "\n"
												   + "While reading xml data\n" 
												   + "\n"
												   + "*****************************************************************\n\n\n"
																			   );
								}
							}
					 return retStr;
	}
	protected static String retrieveRetrialNumberFromDisk(String TxnId){
			String fileN = ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir()  + TxnId + "_NumberTrials";
		//String fileN = "C:\\temp\\pmotest\\projectCreateUpdateDir\\"  + TxnId + "_NumberTrials";
			
			
			if (ht==null)
				ht=new Hashtable();
			if (ht.containsKey(TxnId))
				return null;
			File f = null;
			String retStr = null;
						  try {
						  			f = new File(fileN);
									FileInputStream in = new FileInputStream(f);
								
								
						//		if (logger.isDebugEnabled()) {
						//			logger.debug("retrieveRetrialNumberFromDisk(String)"
						//							+ in.available());
						//		}
									//len = in.read(b,offset,b.length);
									byte b[] = new byte[in.available()];
									in.read(b);
								
									retStr = new String(b);
									if (logger.isDebugEnabled()) {
										logger.debug("retrieveRetrialNumberFromDisk(String) - The message  is"
														+ retStr);
									}
								
								}
								catch(IOException e){
									retStr=null;
									if (ht.containsKey(TxnId)==false)
									{
		
									ht.put(TxnId,"1");
									logger.warn(
													   "\n\n\n******************** !!! retrieveRetrialNumberFromDisk ERROR !!! ********************\n"
													   + "WARNING!: Error to open " + fileN + " at " 
													   + new Date() + "\n"
													   + "Stack Trace:\n" 
													   + XMLProcessor.getStackTrace(e) + "\n"
													   + "While reading xml data\n" 
													   + "\n"
													   + "*****************************************************************\n\n\n"
													   );
									}
								   
								}
								catch(Throwable t) {
									retStr=null;
									if (ht.containsKey(TxnId)==false)
									{
		
									ht.put(TxnId,"1");
									logger.warn(
													   "\n\n\n******************** !!! retrieveRetrialNumberFromDisk ERROR !!! ********************\n"
													   + "WARNING!: Error to read " + fileN + " at " 
													   + new Date() + "\n"
													   + "Stack Trace:\n" 
													   + XMLProcessor.getStackTrace(t) + "\n"
													   + "While reading xml data\n" 
													   + "\n"
													   + "*****************************************************************\n\n\n"
													   );
								       
									}
								}
						 return retStr;
		}
	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}
public static void main(String[] args){
	//retrieveXMLFileFromDisk("v2sat1-1107814509338", "ISSUE");
	//retrieveRetrialNumberFromDisk("v2sat1-1107814509338");
	if (logger.isDebugEnabled()) {
		logger.debug("main(String[])"
				+ deleteXMLFileFromDisk("v2sat1-1107814509338"));
	}
}
}
