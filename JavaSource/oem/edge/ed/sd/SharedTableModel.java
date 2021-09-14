package oem.edge.ed.sd;

import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (9/18/2001 2:20:23 PM)
 * @author: Administrator
 */
public class SharedTableModel {
		public java.util.Vector fileRows = null;
	private OrderlistParser Parser;
	public java.lang.String xmlFilename = "orderList.txt";
	public java.util.Vector ColumnNames = null;
	Boolean dl[] = { Boolean.TRUE, Boolean.FALSE, Boolean.FALSE };

	public LocationInfo destination;
	public boolean showAll = false;
	public boolean isFoundry = false;
/**
 * CustomTableModel constructor comment.
 */
public SharedTableModel() {
	super();
	fileRows = new java.util.Vector();
	ColumnNames = new java.util.Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2001 10:53:01 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column) {
	if (column ==0)
	return Boolean.class;
	else
	return String.class;
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	//System.out.println("Colomns" + fileRows.size());
	return ColumnNames.size();
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/2001 10:28:20 PM)
 * @return java.lang.String
 * @param column int
 */
public String getColumnName(int column) {
	return (String)ColumnNames.elementAt(column);
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2001 4:38:21 PM)
 * @return oem.edge.ed.sd.LocationInfo
 */
public LocationInfo getDestination() {
	return destination;
}
/**
 * Insert the method's description here.
 * Creation date: (10/27/2001 10:01:25 PM)
 * @return org.w3c.dom.Node
 * @param rowNumber int
 */
public org.w3c.dom.Node getFileNode(int rowNumber) {
	return (org.w3c.dom.Node)fileRows.elementAt(rowNumber);
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/2001 5:00:47 PM)
 * @return oem.edge.ed.sd.OrderlistParser
 */
public OrderlistParser getParser() {
	return Parser;
}
/**
 * getRowCount method comment.
 */
public int getRowCount() {
	//System.out.println("ROWS" + fileRows.size());
	return fileRows.size();
	
	//return 2;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int arg1, int arg2) {
//	return "hello";
String value ="";
//SDHostingApp.Debug("Getting value at","row,col");
	if(SDHostingApp1.isFoundry){
	switch ( arg2) {
		
		
			case 0:
				String testselection =getFileNode(arg1).getAttributes().getNamedItem("selected").getNodeValue(); 	
				System.out.println(testselection);
				return new Boolean(testselection);
		
			case 1: // 
				value=getFileNode(arg1).getAttributes().getNamedItem("name").getNodeValue();
				break;
			case 2:
				value= getFileNode(arg1).getAttributes().getNamedItem("desc").getNodeValue();
				break;
			case 3:
				value= getFileNode(arg1).getAttributes().getNamedItem("size").getNodeValue();	
			break;
			case 4:
				value= getFileNode(arg1).getAttributes().getNamedItem("destination").getNodeValue();
				break;
			case 5:
				value = "Browse";
			break;
			default:
			value= null;
		}
	}
	else {
		switch ( arg2) {
			case 0:
				String testselection =getFileNode(arg1).getAttributes().getNamedItem("selected").getNodeValue(); 	
				System.out.println(testselection);
				return new Boolean(testselection);
		
			case 1: // order number
				value=  getFileNode(arg1).getParentNode().getAttributes().getNamedItem("number").getNodeValue();
				break;
			case 2: // 
				value=getFileNode(arg1).getAttributes().getNamedItem("name").getNodeValue();
				break;
			case 3:
				Long temp = new Long(getFileNode(arg1).getParentNode().getAttributes().getNamedItem("expiration").getNodeValue().trim());
				long now  = System.currentTimeMillis();
				if(now>temp.longValue())
					value = "Expired";
				else{
					Date tempdate= new Date(temp.longValue());
					String tempstring = tempdate.toString();
					value=tempstring.substring(0,tempstring.lastIndexOf(":"));
				}
			break;
			case 4:
				value= getFileNode(arg1).getAttributes().getNamedItem("desc").getNodeValue();
				break;
			case 5:
				value= getFileNode(arg1).getAttributes().getNamedItem("size").getNodeValue();	
			break;
			case 6:
				value= getFileNode(arg1).getAttributes().getNamedItem("destination").getNodeValue();
				break;
			case 7:
				value = "Browse";
			break;
			default:
			value= null;
		}	
	}
	//System.out.println(value);
return value;
}
/**
 * Insert the method's description here.
 * Creation date: (10/8/2001 9:46:08 PM)
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column) {
//	System.out.println(" ROW " + row + "COL" + column );
	return (column ==0 || column == 6 || column == 7);
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/2001 9:05:47 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {}
/**
 * Insert the method's description here.
 * Creation date: (9/30/2001 8:28:24 PM)
 */
public boolean populate(HttpsConnect connection) {
boolean returnValue = true;
try{
	// System.out.println("In populate function in Shared Table Model"+xmlFilename);
	java.io.File xmlFile = new java.io.File(xmlFilename);
	// System.out.println("Attempting to Create the Parser"+xmlFilename);
	Parser =  new OrderlistParser();
	// System.out.println("Created the Parser"+xmlFilename);
	//if(connection==null)
	// System.out.println("connection is null");
	if(connection.getOrderlist()){
	 System.out.println("Established Connection to get order list  Parsing input Stream");
	Parser.parseSource(connection.getData());
	//connection.getData().close();
	}
	else{
		// System.out.println("Failed to Establish connection with server to get order list: FAILED");
		returnValue =false;
		return returnValue;

	}

	
	// adding the parameters selected and destination
	for(int i =0; i < Parser.getNumberofFiles();i++){
	org.w3c.dom.Element selected = (org.w3c.dom.Element)Parser.getFiles().item(i);
	selected.setAttribute("selected","false");
	selected.setAttribute("destination","C:\\jars\\");
	}
	// checking for local existence of xml file and synchronizing
	if(xmlFile.exists()){
//	SDHostingApp.Debug("Synchronizing Parsers","xml file exists");
	
	OrderlistParser fileParser = new OrderlistParser();
	// System.out.println("PARSING LOCAL xml FILE");
	fileParser.parseFile(xmlFilename);
	// System.out.println("PARSED LOCAL xml FILE");
	Parser.synchronize(fileParser);
	// System.out.println("SYNCHRONIZED PARSERS");
	}

	setFilerows();
	
	//SDHostingApp.Debug("Finished Populating","xml file exists");
	}
catch(Exception  e){
	// System.out.println("Exception in populate method"+e.getMessage());
	e.printStackTrace();
	returnValue = false;
}
return returnValue;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2001 11:09:20 PM)
 * @param value java.lang.Object
 * @param row int
 * @param column int
 */
protected String queryForDestination(int row) {
	String downloadDestination = null;

	// System.out.println("INSIDE UPDATE ROW TABLE");
	//if (destination == null)
		// System.out.println("destination is null");
	//else
		downloadDestination = destination.getDownloadLocation(row);

	// System.out.println(downloadDestination);
	return downloadDestination;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2001 4:37:35 PM)
 * @param location oem.edge.ed.sd.LocationInfo
 */
public void setDestination(LocationInfo location) {
	destination= location;}
/**
 * Insert the method's description here.
 * Creation date: (10/27/2001 10:09:53 PM)
 */
public void setFilerows() {
	fileRows.removeAllElements();
	if(showAll){
		for(int i =0; i < Parser.getNumberofFiles();i++){
			// System.out.println(Parser.getFiles().item(i).getAttributes().getNamedItem("downloaded").getNodeValue().trim());
			// System.out.println(i);
			fileRows.addElement(Parser.getFiles().item(i));
		}
	}
	else{
		for(int i =0; i < Parser.getNumberofFiles();i++){
			// System.out.println(Parser.getFiles().item(i).getAttributes().getNamedItem("downloaded").getNodeValue().trim());
			if(Parser.getFiles().item(i).getAttributes().getNamedItem("downloaded").getNodeValue().trim().equalsIgnoreCase("no")){
				// System.out.println(i);
				fileRows.addElement(Parser.getFiles().item(i));
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/7/2002 5:26:11 PM)
 * @param foundry boolean
 */
public void setFoundry(boolean foundry) {
	isFoundry = foundry;
	}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:36:49 AM)
 * @param show boolean
 */
public void setShowAll(boolean show) {
	showAll = show;}
/**
 * Insert the method's description here.
 * Creation date: (11/8/2002 4:27:31 PM)
 */
public void setupsharedTableModel() {
	if(SDHostingApp1.isFoundry){
		System.out.println("IN FOUNDRY SOLUTIONS");
	
	ColumnNames.addElement(" ");
	//ColumnNames.addElement("Order Number");
	ColumnNames.addElement("Filename");
	//ColumnNames.addElement("Order Expiration");
	ColumnNames.addElement("File Description");
	ColumnNames.addElement("Size (bytes)");
	}
	else{
		System.out.println("NOT IN FOUNDRY SOLUTIONS");
	
	ColumnNames.addElement(" ");
	ColumnNames.addElement("Order Number");
	ColumnNames.addElement("Filename");
	ColumnNames.addElement("Order Expiration");
	ColumnNames.addElement("File Description");
	ColumnNames.addElement("Size (bytes)");
	}
		
	//ColumnNames.addElement("Destination");
	//ColumnNames.addElement(" ");
	

	
	}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2001 3:40:48 PM)
 * @param value java.lang.Object
 * @param row int
 * @param column int
 */
public void setValueAt(Object value, int row, int column) {
	if (column == 0) {
		Boolean temp = (Boolean)value;
		//System.out.println("ROW " + row + "COL " +column + "VAL " + temp);
		if(temp.booleanValue())
			((org.w3c.dom.Element)getFileNode(row)).setAttribute("selected","true");
		else
			((org.w3c.dom.Element)getFileNode(row)).setAttribute("selected","false");
	}
	// Column 6 or 7.
	else {
		if (column == 7)
			value = queryForDestination(row);

		((org.w3c.dom.Element)getFileNode(row)).setAttribute("destination",((String)value));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 11:08:51 AM)
 * @return boolean
 */
public boolean slaRequired() {
	return Parser.getSLArequired();
}
}
