package oem.edge.ed.sd;


import com.ibm.xml.parsers.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
/**
 * Insert the type's description here.
 * Creation date: (9/16/2001 4:16:26 PM)
 * @author: Administrator
 */
public class OrderlistParser  {
	public com.ibm.xml.parsers.DOMParser parser = null;
	public java.io.File xmlFile = null;
	public org.w3c.dom.NodeList fileList = null;
	public org.w3c.dom.Document orderDocument = null;
/**
 * OrderlistParser constructor comment.
 */
public OrderlistParser() {
		super();
		//SDHostingApp.Debug("In the constructor","OrderListparser");
	 try{
		// SDHostingApp.Debug("In the try block of constructor","OrderListparser"); 
		parser = new DOMParser();
			// SDHostingApp.Debug("In the try block of constructor after creation","OrderListparser");
	 }
	 catch(Exception e){
		//SDHostingApp.Debug("In the creation of parser",e.getMessage());
		e.printStackTrace();
	 } 
	// SDHostingApp.Debug("In the constructor","OrderListparser created successfully");
	    
}
/**
 * Insert the method's description here.
 * Creation date: (9/30/2001 8:03:23 PM)
 * @return org.w3c.dom.NodeList
 */
public NodeList getFiles() {
	

	return fileList;
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 11:20:30 AM)
 * @return int
 */
public int getNumberofFiles() {
	if(fileList!=null)
	return fileList.getLength();
	else
	return 0;
}
/**
 * Insert the method's description here.b
 * Creation date: (9/16/2001 4:17:06 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
OrderlistParser testparser = new OrderlistParser();
testparser.parseFile("C:\\orderList.txt");
	
    File testfile = new File("C:\\orderList.txt");
    if (testfile.exists())
        System.out.println("FileFound");
    FileInputStream test = null;
    try {
        test = new FileInputStream(testfile);
    } catch (IOException ioe) {
        ioe.printStackTrace();
        System.out.println("exceptionfile");
        System.out.println(ioe.getMessage());
    }
    System.out.println("hello1");
    InputSource source = new InputSource(test);
    DOMParser parser = new DOMParser();
    try {
        parser.parse(source);
    } catch (SAXException se) {
        se.printStackTrace();
        System.out.println("exception1");
        System.out.println(se.getMessage());
    } catch (IOException ioe) {
        ioe.printStackTrace();
        System.out.println("exception2");
        System.out.println(ioe.getMessage());
    }
	
    Document doc = parser.getDocument();
    NodeList temp = doc.getElementsByTagName("file");
    System.out.println(temp.getLength());
    int i = 0;
    while (i < temp.getLength()) {
        NamedNodeMap OrderAttributes = temp.item(i).getAttributes();
        System.out.println(OrderAttributes.getNamedItem("downloaded").getNodeValue());
        OrderAttributes.getNamedItem("downloaded").setNodeValue("yes");
        i++;
    }
	
    NamedNodeMap testattrib =temp.item(0).getParentNode().getAttributes();
    System.out.println(testattrib.getNamedItem("number").getNodeValue());

    DOMWriter writer;
    try {
        writer = new DOMWriter(true, "C:\\XMLout.txt");
        writer.print(doc);

    } catch (Exception e) {
        e.printStackTrace();
    }

    System.out.println(parser.getDocument().toString());

}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 10:58:05 AM)
 */
public void parseFile() {
	
	// File testfile = new File("hello");
    //if (xmlFile.exists())
    //    System.out.println("FileFound");
    FileInputStream test = null;
    try {
        test = new FileInputStream(xmlFile);
    } catch (IOException ioe) {
        ioe.printStackTrace();
        System.out.println("exceptionfile");
        System.out.println(ioe.getMessage());
    }
    parseSource(test);}
/**
 * Insert the method's description here.
 * Creation date: (9/26/2001 6:34:21 PM)
 * @param filename java.lang.String
 */
public void parseFile(String filename) {
	
	File testfile = new File(filename);
    //if (testfile.exists())
    //    System.out.println("FileFound");
    FileInputStream test = null;
    try {
        test = new FileInputStream(testfile);
    } catch (IOException ioe) {
        ioe.printStackTrace();
        System.out.println("exceptionfile");
        System.out.println(ioe.getMessage());
    }
 
     parseSource(test); 
    }
/**
 * Insert the method's description here.
 * Creation date: (9/26/2001 6:32:36 PM)
 * @param input java.io.InputStream
 */
public void parseSource(InputStream input) {
	// setting the input source to the parser
	//SDHostingApp.Debug("inside parse source","data");
	InputSource source = new InputSource(input);
if(parser==null)
parser= new DOMParser();
    try {
         parser.parse(source);
    } catch (SAXException se) {
        se.printStackTrace();
        // System.out.println("exception1");
        // System.out.println(se.getMessage());
    } catch (IOException ioe) {
        ioe.printStackTrace();
        // System.out.println("exception2");
        // System.out.println(ioe.getMessage());
    }

    orderDocument = parser.getDocument();
    fileList =  orderDocument.getElementsByTagName("file");
    System.out.println("filelist" + fileList.getLength());

}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 7:43:21 PM)
 * @param localParser oem.edge.ed.sd.OrderlistParser
 */
public void synchronize(OrderlistParser localParser) {
// System.out.println("in SYNCHRONIZE METHOD");
try{	
	for(int i =0;i<getFiles().getLength();i++)
		for(int j = 0;j<localParser.getFiles().getLength();j++){
			//// System.out.println(getFiles().getLength() + ":" + localParser.getFiles().getLength());
			String id1 = getFiles().item(i).getFirstChild().getNodeValue().trim();
			String id2 = localParser.getFiles().item(j).getFirstChild().getNodeValue().trim();
		
			if( (new Long(id1)).equals ((new Long(id2))) ){
				// System.out.println(i +" : " + j);
					org.w3c.dom.Element temp =(org.w3c.dom.Element) (getFiles().item(i));
				String selection = localParser.getFiles().item(j).getAttributes().getNamedItem("selected").getNodeValue().trim();
				// System.out.println(selection);
				String destination = localParser.getFiles().item(j).getAttributes().getNamedItem("destination").getNodeValue().trim();
				// System.out.println(destination);
				temp.setAttribute("selected",selection);
				temp.setAttribute("destination",destination);
				
			}
		}
}
catch(Exception e){
// System.out.println("Exception in Synchronize" + e.getMessage());
e.printStackTrace();
}

}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 10:54:09 AM)
 */
public void writeDocument() {
	
	DOMWriter writer;
    try {
        writer =new DOMWriter(true, "orderlist.txt");
        writer.print(orderDocument);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 11:11:51 AM)
 * @return boolean
 */
public boolean getSLArequired() {
	String value = orderDocument.getElementsByTagName("edsd_download_client").item(0).getAttributes().getNamedItem("slaRequired").getNodeValue().trim();
	
	System.out.println("SLA" + value);
	if(value.equalsIgnoreCase("yes"))
		return true;
	else
	return false;
		
	
}
}
