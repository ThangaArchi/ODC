package oem.edge.ets_pmo.xml;

import oem.edge.ets_pmo.common.*;
import oem.edge.ets_pmo.mq.*;

import oem.edge.ets_pmo.datastore.Transaction;

import org.w3c.dom.Element;
import org.apache.log4j.Logger;
import oem.edge.ets_pmo.common.mail.PostMan;
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
public class GenerateProjectCreateUpdateAckXML extends GenerateBaseXML {
	
	private static String CLASS_VERSION = "4.5.1";
	static Logger logger = Logger.getLogger(GenerateProjectCreateUpdateAckXML.class);
 public GenerateProjectCreateUpdateAckXML(){
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
        
        main.appendChild(object);
		main.appendChild(super.getDoc().createTextNode("\n\n"));
		return true;
    }
  private void loadProjectCreateUpdateAckData()
    {
     
     	setSource("ETS");
        setDestination("PMOFFICE");
        setVTransactionXML("1.0");
        setTransactionID("3ERTBBAAA7684946A332D6E116125EE3");
        setOperationType("ACK");
        setProject_ID("2B76BBD577684946A332D6E116125EE3");
        setUserid("PMO_SUPERVISOR");
        setApp("PMOFFICE");
        

    }

 public static void main(String args[])
    {
    	if(args.length < 1){
				System.out.println(" Usage : GenerateProjectCreateUpdateAckXML <PropertyFileLocation> " );
				System.exit(0);
			}
			
		
      
	   ETSPMOGlobalInitialize glob = new ETSPMOGlobalInitialize();
    	
      
      GenerateProjectCreateUpdateAckXML gxml  = new GenerateProjectCreateUpdateAckXML();
      gxml.loadProjectCreateUpdateAckData();
      gxml.generate();
      gxml.print();
      System.out.println(gxml.getXmlMsg());
      
    }//end of main
    //
   
     
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
	
	public void loadTransactionData(Transaction trans)
    {
		super.loadTransactionData();
		setOperationType("ACK");
		 		if(super.checkData() == false){
        	logger.error("Problem with the data.One or more of the data elements are null. Hard stop");
        	System.exit(1);
        }
	}
	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
