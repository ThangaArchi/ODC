package oem.edge.ets_pmo.xml;

import oem.edge.ets_pmo.common.*;
import oem.edge.ets_pmo.mq.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Attr;
import org.apache.log4j.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import oem.edge.ets_pmo.common.mail.PostMan;

import java.io.*;
import java.util.*;

import java.net.*;
import oem.edge.ets_pmo.datastore.util.ErrorData;

import oem.edge.ets_pmo.datastore.Transaction;
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
public class GenerateProjectCreateUpdateNackXML extends GenerateBaseXML{
	private static String CLASS_VERSION = "4.5.1";
private Vector vErrors;


static Logger logger = Logger.getLogger(GenerateProjectCreateUpdateNackXML.class);

public GenerateProjectCreateUpdateNackXML(){
 		super();
 	//	loadProjectCreateUpdateNAckData(); 

 		

        
 }
	
 public boolean generateObjectProject(Element main){
		super.generateObjectProject(main);
		Element projId, object;
        object = super.getDoc().createElement("object");
        object.setAttribute("type", "PROJECT");
        object.appendChild(super.getDoc().createTextNode("\n"));
        
        projId = super.getDoc().createElement("id");
		projId.appendChild(super.getDoc().createTextNode(getProject_ID()));
		object.appendChild(projId);
		object.appendChild(super.getDoc().createTextNode("\n\n"));
		
        generateExceptionObject(object);
        
        main.appendChild(object);
		main.appendChild(super.getDoc().createTextNode("\n\n"));
		return true;
    }
 public void generateExceptionObject(Element project){
 /*	
 	Element object, attribute, name, value;
 	
 	name  = super.getDoc().createElement("name");
 	name.appendChild(super.getDoc().createTextNode("SAXException"));
 	
 	value = super.getDoc().createElement("value");
 	value.appendChild(super.getDoc().createTextNode("\n"));
 	value.appendChild(super.getDoc().createTextNode("SAXException Value"));
 	value.appendChild(super.getDoc().createTextNode("\n"));
 	
 	attribute =  super.getDoc().createElement("attribute");
 	attribute.setAttribute("type", "EXCEPTION");
 	attribute.appendChild(super.getDoc().createTextNode("\n"));
 	attribute.appendChild(name);
 	attribute.appendChild(super.getDoc().createTextNode("\n"));
 	attribute.appendChild(value);
 	attribute.appendChild(super.getDoc().createTextNode("\n"));
 		
 	object = super.getDoc().createElement("object");
 	object.setAttribute("type", "ROOT");
 	object.appendChild(super.getDoc().createTextNode("\n"));
 	object.appendChild(attribute);
 	object.appendChild(super.getDoc().createTextNode("\n"));
 	
 	project.appendChild(super.getDoc().createTextNode("\n"));
 	project.appendChild(object);
 	project.appendChild(super.getDoc().createTextNode("\n\n"));
 	*/
 	
 	for(int i = 0 ; i < RetrievePopulationOfErrorData(); i ++)
 			{
 				//Element object
 				Element attribute, name, value;
 				ErrorData err = this.retrieveErrorData(i);
 				
 				name  = super.getDoc().createElement("name");
 				name.appendChild(super.getDoc().createTextNode(err.getName()));
 	
 				value = super.getDoc().createElement("value");
			 	value.appendChild(super.getDoc().createTextNode("\n"));
			 	value.appendChild(super.getDoc().createTextNode(err.getValue()));
			 	value.appendChild(super.getDoc().createTextNode("\n"));
 	
 				attribute =  super.getDoc().createElement("attribute");
			 	attribute.setAttribute("type", err.getAttributeType());
			 	attribute.appendChild(super.getDoc().createTextNode("\n"));
			 	attribute.appendChild(name);
			 	attribute.appendChild(super.getDoc().createTextNode("\n"));
			 	attribute.appendChild(value);
			 	attribute.appendChild(super.getDoc().createTextNode("\n"));
			 	
			 	
 		/* Took off the object as it would send a XML structure that would look as ->
 		 * <object type="ROOT">
			<attribute type="ERROR">
			<name>SAXPARSEEXCEPTION</name>
			<value>
			780802
			</value>
			</attribute>
			</object>
 		 * 
 		 * SysCorp instead wanted no object wrapper around the attribute
 		 * 
 		 * So the structure now is <attribute type="ERROR">
									<name>SAXPARSEEXCEPTION</name>
									<value>
									780802
									</value>
									</attribute>
 		 * 
 		 * I am going to lose object types for other errors too...eeeks..but thats ok..looks like 
 		 * such case will never arise as sys corp is alway going to give me ids
 		 * 
 		 * 
 		 */
			/*	object = super.getDoc().createElement("object");
			 	object.setAttribute("type", err.getObjectType());
			 	object.appendChild(super.getDoc().createTextNode("\n"));
			 	object.appendChild(attribute);
			 	object.appendChild(super.getDoc().createTextNode("\n"));
 	*/
 				project.appendChild(super.getDoc().createTextNode("\n"));
			 	project.appendChild(attribute);
			 	project.appendChild(super.getDoc().createTextNode("\n\n"));
 				
 				
 			}
 	
 	
 }
  public void  registerErrors(String objectType, String attributeType, String name, String value, String id){
  /*
   *    This helps to register each error in this format before sending it to PMOffice
   * 	<object type="RESOURCE" >
		<attribute type="EXCEPTION" >
		<name>Unable to Create resource
		</name>	
		<value>200014</value>
		</attribute>
		<id>BF418A712254466D8F1BFFE6963FE8BB</id>
		</object>
  */
  		if(vErrors ==  null){
  			vErrors = new Vector();
  		}
  		vErrors.add(new ErrorData(objectType, attributeType, name, value, id));
  		
  }
  private void loadProjectCreateUpdateNAckData()
    {
     
     	setSource("ETS");
        setDestination("PMOFFICE");
        setVTransactionXML("1.0");
        setTransactionID("TTRRAANNSSAACCTTIIOONNIIDD");
        setOperationType("NACK");
        setProject_ID("PPRROOJJEECCTTIIDD");
        setUserid("PMO_SUPERVISOR");
        setApp("PMOFFICE");
        registerErrors("ROOT", "EXCEPTION", "SAXEXCEPTION", "780208","");
        registerErrors("ROOT1", "EXCEPTION1", "SAXEXCEPTION1", "780209","");
        

    }
public void loadTransactionData(Transaction trans)
    {
		super.loadTransactionData();
		setOperationType("NACK");
		 		if(super.checkData() == false){
        				logger.error("Problem with the data.One or more of the data elements are null. Hard stop");
        	System.exit(1);
        }
	}
    
 public static void main(String args[])
    {
    	if(args.length < 1){
				System.out.println(" Usage : GenerateProjectCreateUpdateNackXML <PropertyFileLocation> " );
				System.exit(0);
			}
			
		ETSPMOGlobalInitialize glob = new ETSPMOGlobalInitialize();


      GenerateProjectCreateUpdateNackXML gxml  = new GenerateProjectCreateUpdateNackXML();
      gxml.loadProjectCreateUpdateNAckData();
      gxml.generate();
      gxml.print();
      System.out.println(gxml.getXmlMsg());
      
    }//end of main
   
   public ErrorData retrieveErrorData(int index) throws IndexOutOfBoundsException{
	ErrorData pro = null;
	if(this.vErrors != null &&
		!this.vErrors.isEmpty()){
			if(index >= vErrors.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vErrors");
			}
	pro = (ErrorData)vErrors.get(index);
	}
	return pro;
		
}

	public void populateVErrorData(ErrorData pro) {
		if(this.vErrors == null){
			vErrors = new Vector();
		}
		vErrors.add(pro);
	}
	
public int RetrievePopulationOfErrorData() {
		if(vErrors == null)
			return -1;
		return vErrors.size();
	}

public void SendToPMOMQ(boolean ack){

    	PMOMQSend one = new PMOMQSend();
    	String ackOrnack = "nack";
    	try{
    		
    		if(ack == true)
    			ackOrnack = "ack";
    	logger.info("Sending MQMessage with Messagetype: " + ackOrnack +
    													"\n CorrID: " + this.getCorrId() +
            											"\n MessageID: " + this.getMessId() + 
            											"\n Message: " + this.getXmlMsg());
	    	one.sendMQMessage(this.getCorrId(), this.getMessId(), this.getXmlMsg().toString(), true, true, true);
	    	
	    	 
			PostMan p = new PostMan();
	    	p.sendACKorNACKInfoFromETS(ack, this.getXmlMsg().toString());
    	}
    	catch (Throwable e) {
        	//e.printStackTrace();
            logger.error("Error sending the MQMEssage with MessageType: " +  ackOrnack +
    													"\n CorrID: " + this.getCorrId() +
            											"\nMessageID: " + this.getMessId() + 
            											"\nMessage: " + this.getXmlMsg());
        }
	}
	
	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}

