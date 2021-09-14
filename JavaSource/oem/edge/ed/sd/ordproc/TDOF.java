package oem.edge.ed.sd.ordproc;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: ICC/PROFIT                                                    */
/* (C) Copyright IBM Corp. 2002, 2003                                        */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** RCS & COPYRT *************************************/
/************************** EOF : HEADER *************************************/
/////////////////////////////////////////////////////////////////////////////
//
//                            Edge 2.10
//                      Dev: Navneet Gupta
//
//////////////////////////////////////////////////////////////////////////////

import java.util.Properties;
import java.util.StringTokenizer;
import java.io.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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

public class TDOF {

   public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    private Properties message;
    private String outputFile;
    private boolean download;
    private boolean type;
    private MQHandler callingObject;
    private boolean majorOrder;
    private boolean getToolsOrder;
    private String tmpRequestID;
    private String OrderID;
    private String CatchMeUpInd;
    private String CatchMeUpType;
    private boolean majorAnddelta;
    private String Test;

    TDOF(String outputFile, String IPorDK, MQHandler callingObject, boolean majorOrder, boolean getToolsOrder, String tmpRequestID, String Test) {
        this.message = message;
        this.outputFile = outputFile;
        this.callingObject = callingObject;
        if(IPorDK.equalsIgnoreCase("IP"))
            this.type = false;
        else
           this.type = true;
        this.download = true;
	this.majorOrder = majorOrder;
	this.getToolsOrder = getToolsOrder;
	this.tmpRequestID = tmpRequestID;
        this.majorAnddelta = false;
        this.Test = Test;
    }
  
  //new for 3.12.1
  
   TDOF(String outputFile, String IPorDK, MQHandler callingObject, boolean majorOrder, boolean getToolsOrder, String tmpRequestID, boolean majorAnddelta, String Test) {
        this.message = message;
        this.outputFile = outputFile;
        this.callingObject = callingObject;
        if(IPorDK.equalsIgnoreCase("IP"))
            this.type = false;
        else
            this.type = true;
        this.download = true;
	this.majorOrder = majorOrder;
	this.getToolsOrder = getToolsOrder;
	this.tmpRequestID = tmpRequestID;
        this.majorAnddelta = majorAnddelta;
        this.Test = Test;
    }
  //end of new for 3.12.1
  
    String createTDOF() throws IOException {
	if(majorOrder && this.type)
	    return createDK();
        else if (majorOrder && !this.type)
            return createIP();
	else
            return createDR();
        
    }

   
  //new for 5.1.1
    String createIP() throws IOException {

        callingObject.print("Starting TDOF.createIP()...", MQHandler.V_IMP);
        String technology, versionNo, orderNumber, modelTypes="";
        StringBuffer output = new StringBuffer();

        technology = callingObject.getRequiredValue("TECHNOLOGY");
        versionNo = callingObject.getRequiredValue("VERSION_NO");

	if(getToolsOrder) {
           orderNumber = "";
        }
	else {
	    orderNumber = callingObject.getRequiredValue("ORDER_NUMBER");
           
	}

        output.append("ORDER_ID;" + orderNumber + ";" + tmpRequestID + ";\n");
        output.append("TECHNOLOGY;" + technology +";\n");
        output.append("LIBRARY_RELEASE;" + versionNo + ";\n");
        output.append("PRODUCT_DEF_REVISION;" + callingObject.getRequiredValue("REVISION_NO") + ";\n");

        output.append("TEST;"+Test+";\n");
        output.append("CUST;" + callingObject.getRequiredValue("ASIC_CODENAME") 
                      + ";" 
                      + callingObject.getRequiredValue("USERS_COMPANY") 
                      + ";"
                      + callingObject.getRequiredValue("CUSTOMER_PROJNAME")
                      + ";\n");
        // two extra ;'s for blank Project and Location
        
        output.append("PLATFORMS;");
        String[] platformsList;
        platformsList = getStringArray("PLATFORMS_LIST");
        for(int i = 0; i < platformsList.length; i++)
            output.append(getPlatformAlias(platformsList[i]) + ",");
        output.append(";\n");
        
        output.append("IP_VERSIONS;"); 
        if (!callingObject.getRequiredValue("BASE_MODEL_KIT_COUNT").equals("0"))
            output.append(callingObject.getRequiredValue("BASE_MODEL_KIT_LIST"));
        if (!callingObject.getRequiredValue("CORES_COUNT").equals("0"))
            output.append(callingObject.getRequiredValue("CORES_LIST"));
         
        output.append(";\n");
        
        output.append("MODEL_TYPES;");
        if(!getToolsOrder) {
           modelTypes = callingObject.getRequiredValue("MODEL_TYPES_LIST");
        }else
           modelTypes = "ALL";
        output.append(modelTypes);
        output.append(";\n");
        
        
        if (callingObject.getRequiredValue("REVISION_INDICATOR").equalsIgnoreCase("Y"))
           output.append("CATCH_ME_UP;" + "FCS;\n");
        else
           output.append("CATCH_ME_UP;" + "Last_Order;\n");
    
        output.append("PROFILE_ORDER;No;\n");
	output.append("SKIP_CHERRY_PICK;" + callingObject.getRequiredValue("SKIP_CHERRYPICK") + ";\n");

        callingObject.print("Writing TDOF String to file...", MQHandler.NOT_IMP);


        File f = new File(outputFile);

        File targetDir = new File(f.getParent());
        if( ! targetDir.isDirectory())
            if( ! targetDir.mkdirs())
                throw new IOException(outputFile + " (Directory) cannot be created");
        
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(output.toString().getBytes());
        fos.close();

        callingObject.print("Exiting TDOF.createIP()...", MQHandler.V_IMP);

        return output.toString();

    }
  //end of new for 5.1.1

    String createDK() throws IOException {

        callingObject.print("Starting TDOF.createDK()...", MQHandler.V_IMP);
        String technology, versionNo, orderNumber, modelTypes, toolkit, mediaType;
        StringBuffer output = new StringBuffer();
	int shiptoLocCount;

        technology = callingObject.getRequiredValue("TECHNOLOGY");
        versionNo = callingObject.getRequiredValue("VERSION_NO");

	if(getToolsOrder) {
	    orderNumber = "";
	    toolkit = "Y";
	    shiptoLocCount = 0;

	    mediaType = "";
	    modelTypes = "ALL";
	}
	else {
	    orderNumber = callingObject.getRequiredValue("ORDER_NUMBER");
	    toolkit = callingObject.getRequiredValue("TOOLKIT");
	    shiptoLocCount = Integer.parseInt(callingObject.getRequiredValue("SHIPTO_LOC_COUNT"));

            if(download)
                mediaType = "DOWNLOAD";
            else
                mediaType = "CD";

	    if(callingObject.getRequiredValue("MODEL_TYPES_COUNT").equals("0")) {
	        modelTypes = "ALL";
	    }
	    else {
	        modelTypes = callingObject.getRequiredValue("MODEL_TYPES_LIST");
	    }
	}



        output.append("HEAD;" + orderNumber + ";" + versionNo + ";" + tmpRequestID + ";\n");

        output.append("TEST;"+Test+";\n");
        output.append("CUST;" + callingObject.getRequiredValue("ASIC_CODENAME") 
                      + ";" 
                      + callingObject.getRequiredValue("USERS_COMPANY") 
                      + ";;;\n");
        // two extra ;'s for blank Project and Location
        
        output.append("PRIMARY_CI;" 
                      + callingObject.getValue("FSE_NAME_1") 
                      + ";" 
                      + callingObject.getValue("FSE_LOC_1") 
                      + ";" 
                      + callingObject.getValue("FSE_PHONE_1") 
                      + ";" 
                      + callingObject.getValue("FSE_FAX_1") 
                      + ";" 
                      + callingObject.getValue("FSE_EMAIL_1") 
                      + ";\n");

        output.append("BACKUP_CI;;;;;;\n"); 

// output.append("COMMENTS;;\n");


        boolean toolkitOnly = true;


        output.append("NSTD;");
        if( ! callingObject.getRequiredValue("NONSTAND_DELIVERS_COUNT").equals("0")) {
	    output.append(callingObject.getRequiredValue("NONSTAND_DELIVERS_LIST"));
            toolkitOnly = false;
        }
        output.append(";\n");


        output.append("CORE;");
        if( ! callingObject.getRequiredValue("CORES_COUNT").equals("0")) {
	    output.append(callingObject.getRequiredValue("CORES_LIST"));
            toolkitOnly = false;
        }
        output.append(";\n");


        output.append("BASE_ORD;");
        if( ! callingObject.getRequiredValue("BASE_ORD_COUNT").equals("0")) {
	    output.append(callingObject.getRequiredValue("BASE_ORD_LIST"));
            toolkitOnly = false;
        }
        output.append(";\n");


	output.append("MODEL_TYPES;");
	output.append(modelTypes);
        output.append(";\n");


        output.append("BASE_MODEL_KIT;");
        if(callingObject.getRequiredValue("BASE_MODEL_KIT").equals("N"))
            output.append("No;\n");
        else {
            output.append("Yes;\n");
            toolkitOnly = false;
        }


        output.append("TOOL_KIT_ONLY;");
        if(toolkitOnly)
            output.append("Yes;\n");
        else
            output.append("No;\n");



        output.append("SHIP;customer,,;");
        // extra , for blank relationship


        if(download)
            output.append(callingObject.getValue("FIRST_NAME") 
                          + " " + callingObject.getValue("LAST_NAME") 
                          + "," 
                          + callingObject.getValue("ADDRESS") 
                          + ",,"
                          // extra , for ADDRESS_2 field
                          + callingObject.getValue("CITY") 
                          + "," 
                          + callingObject.getValue("STATE") 
                          + "," 
                          + callingObject.getValue("ZIP") 
                          + "," 
                          + callingObject.getValue("PHONE") 
                          + "," 
                          + callingObject.getValue("FAX") 
                          + "," 
                          + callingObject.getRequiredValue("E_MAIL") 
                          + ",;");
        else
            output.append(callingObject.getRequiredValue("FIRST_NAME") 
                          + " " + callingObject.getRequiredValue("LAST_NAME") 
                          + "," 
                          + callingObject.getRequiredValue("ADDRESS") 
                          + ",,"
                          // extra , for ADDRESS_2 field
                          + callingObject.getRequiredValue("CITY") 
                          + "," 
                          + callingObject.getRequiredValue("STATE") 
                          + "," 
                          + callingObject.getRequiredValue("ZIP") 
                          + "," 
                          + callingObject.getValue("PHONE") 
                          + "," 
                          + callingObject.getValue("FAX") 
                          + "," 
                          + callingObject.getRequiredValue("E_MAIL") 
                          + ",;");


        String[] platformsList;
        String toolkitOrdered = "No";
        int platformsCount = Integer.parseInt(callingObject.getRequiredValue("PLATFORMS_COUNT"));

	if(toolkit.equals("Y"))
	    toolkitOrdered = "Yes";

        output.append(toolkitOrdered + ",");
        platformsList = getStringArray("PLATFORMS_LIST");
        for(int i = 0; i < platformsList.length; i++)
            output.append(getPlatformAlias(platformsList[i]) + "%");
        output.append(",;");


        output.append(mediaType);
	output.append(",,;\n");



        boolean mediaIsFile, mediaIsCD;

        for(int i = 1; i <= shiptoLocCount; i++) {

            if(callingObject.getRequiredValue("FL_" + i).equals("1")) {	
                mediaIsFile = true;
		mediaIsCD = false;
	    }
            else if(callingObject.getRequiredValue("CD_" + i).equals("1")) {
                mediaIsFile = false;
		mediaIsCD = true;
	    }
	    else {
                mediaIsFile = false;
		mediaIsCD = false;
	    }

            output.append("SHIP;" + getAlias(callingObject.getRequiredValue("SHIPTO_TYPE_" + i)) + "," + callingObject.getValue("RELATIONSHIP_" + i) + ",;");

            output.append(callingObject.getRequiredValue("CONTACT_NAME_" + i) 
                          + "," 
                          + callingObject.getRequiredValue("ADDRESS_1_" + i) 
                          + "," 
                          + callingObject.getValue("ADDRESS_2_" + i) 
                          + "," 
                          + callingObject.getRequiredValue("CITY_" + i) 
                          + "," 
                          + callingObject.getRequiredValue("STATE_" + i) 
                          + "," 
                          + callingObject.getRequiredValue("ZIP_" + i) 
                          + "," 
                          + callingObject.getValue("PHONE_" + i) 
                          + "," 
                          + callingObject.getValue("FAX_" + i) 
                          + "," 
                          + callingObject.getRequiredValue("EMAIL_" + i) + ",;");


            platformsCount = Integer.parseInt(callingObject.getRequiredValue("PLATFORM_LIST_COUNT_" + i));
            if(platformsCount <= 0) 
                throw new RuntimeException("PLATFORM_LIST_COUNT_" + i + " cannot be zero");

            output.append(toolkitOrdered + ",");
            platformsList = getStringArray("PLATFORM_LIST_" + i);
            for(int j = 0; j < platformsList.length; j++)
                output.append(getPlatformAlias(platformsList[j]) + "%");
            output.append(",;");


            if(mediaIsFile)
                output.append("FILE," + callingObject.getRequiredValue("AFS_USR_ID_" + i) + ",;\n");
            else if(mediaIsCD)
                output.append("CD,,;\n");
	    else
                output.append("DOWNLOAD,,;\n");

        }


        callingObject.print("Writing TDOF String to file...", MQHandler.NOT_IMP);


        File f = new File(outputFile);

        File targetDir = new File(f.getParent());
        if( ! targetDir.isDirectory())
            if( ! targetDir.mkdirs())
                throw new IOException(outputFile + " (Directory) cannot be created");
        
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(output.toString().getBytes());
        fos.close();

        callingObject.print("Exiting TDOF.createDK()...", MQHandler.V_IMP);

        return output.toString();

    }




/*
HEAD;RequestID;Major Release Version;TmpRequestID; 
TEST;yes or no;
CUST;ASIC_Codename;Company;Project;Location; 
PRIMARY_CI;Name;Location;Phone;Fax;Email; 
BACKUP_CI;Name;Location;Phone;Fax;Email; 
CATCH_ME_UP;CatchMeUpIndicator;CatchMeUpType;
DELTAS;DeltaNameList; 
MODEL_TYPES;ModelTypeList  
SHIP;ShipTo;ContactData;MK_Media;PlatformList; 
PREV_NSTD;ToolList; 
PREV_CORE;CoreNameList;  
PREV_BASE_ORD;BaseOrderableList; 
MAJOR_RELEASE_ORDER;MajorReleaseRequestID;
*/
    String createDR() throws IOException {

        callingObject.print("Starting TDOF.createDR()...", MQHandler.V_IMP);
        String technology, versionNo, orderNumber, modelTypes;
        StringBuffer output = new StringBuffer();
	int shiptoLocCount;

        technology = callingObject.getRequiredValue("TECHNOLOGY");
        versionNo = callingObject.getRequiredValue("VERSION_NO");

	if(getToolsOrder) {
	    orderNumber = "";
	    shiptoLocCount = 0;

	    modelTypes = "ALL";
	}
	else {
	    orderNumber = callingObject.getRequiredValue("ORDER_NUMBER");
	    shiptoLocCount = 0;
// shiptoLocCount = Integer.parseInt(callingObject.getRequiredValue("SHIPTO_LOC_COUNT"));

	    if(callingObject.getRequiredValue("MODEL_TYPES_COUNT").equals("0")) {
	        modelTypes = "ALL";
	    }
	    else {
	        modelTypes = callingObject.getRequiredValue("MODEL_TYPES_LIST");
	    }
	}



        output.append("HEAD;" + orderNumber + ";" + versionNo + ";" + tmpRequestID + ";\n");
        output.append("TEST;"+Test+";\n");
        output.append("CUST;" + callingObject.getRequiredValue("ASIC_CODENAME") 
                      + ";" 
                      + callingObject.getRequiredValue("USERS_COMPANY") 
                      + ";;;\n");
        // two extra ;'s for blank Project and Location
        
        output.append("PRIMARY_CI;" 
                      + callingObject.getValue("FSE_NAME_1") 
                      + ";" 
                      + callingObject.getValue("FSE_LOC_1") 
                      + ";" 
                      + callingObject.getValue("FSE_PHONE_1") 
                      + ";" 
                      + callingObject.getValue("FSE_FAX_1") 
                      + ";" 
                      + callingObject.getValue("FSE_EMAIL_1") 
                      + ";\n");

        output.append("BACKUP_CI;;;;;;\n"); 
        //new for 5.1.1
        output.append("PROFILE_ORDER;");
        if (callingObject.getValue("ORDER_BY").equalsIgnoreCase("SYSTEM"))
                output.append("Yes;\n");
        else 
                output.append("No;\n");
                
        //end of new for 5.1.1
        
       //new for 3.12.1
        if (majorAnddelta){
           if (callingObject.getRequiredValue("DELTA_INDICATOR").equalsIgnoreCase("Y"))
              output.append("CATCH_ME_UP;"
                      + "Yes"
                      + ";"
                      + "FCS"
                      + ";\n");
           else
              output.append("CATCH_ME_UP;"
                      + "No"
                      + ";"
                      + ""
                      + ";\n");
        }
        else{
           String catchmeup_ind = callingObject.getRequiredValue("CatchMeUp_Ind");
           String ind, type;
           if (catchmeup_ind.equalsIgnoreCase("Y")){
              ind="Yes";
              type=callingObject.getRequiredValue("CatchMeUp_Type");
              if (type.equalsIgnoreCase("LAST_ORDER"))
                 type="Last_Order";
           }
           else{
              ind="No";
              type="";
           }
                  
               
           output.append("CATCH_ME_UP;"
                      + ind
                      + ";"
                      + type
                      + ";\n");
        }
                      
       //end of new for 3.12.1


	output.append("DELTAS;");
	output.append(callingObject.getRequiredValue("PATCH_LIST"));
        output.append(";\n");


	output.append("MODEL_TYPES;");
	output.append(modelTypes);
        output.append(";\n");


        output.append("SHIP;customer,,;");
        // extra , for blank relationship


        output.append(callingObject.getValue("FIRST_NAME") 
                          + " " + callingObject.getValue("LAST_NAME") 
                          + "," 
                          + callingObject.getValue("ADDRESS") 
                          + ",,"
                          // extra , for ADDRESS_2 field
                          + callingObject.getValue("CITY") 
                          + "," 
                          + callingObject.getValue("STATE") 
                          + "," 
                          + callingObject.getValue("ZIP") 
                          + "," 
                          + callingObject.getValue("PHONE") 
                          + "," 
                          + callingObject.getValue("FAX") 
                          + "," 
                          + callingObject.getRequiredValue("E_MAIL") 
                          + ",;");


        output.append("DOWNLOAD,,;");


        String[] platformsList = getStringArray("PLATFORMS_LIST");
        for(int i = 0; i < platformsList.length; i++)
            output.append(getPlatformAlias(platformsList[i]) + ",");
        output.append(";\n");



        output.append("PREV_NSTD;");
        if( ! callingObject.getRequiredValue("PREV_NSTD_COUNT").equals("0")) {
	    output.append(callingObject.getRequiredValue("PREV_NSTD_LIST"));
        }
        output.append(";\n");


        output.append("PREV_CORE;");
        if( ! callingObject.getRequiredValue("PREV_CORE_COUNT").equals("0")) {
	    output.append(callingObject.getRequiredValue("PREV_CORE_LIST"));
        }
        output.append(";\n");


        output.append("PREV_BASE_ORD;");
        if( ! callingObject.getRequiredValue("PREV_BASE_ORD_COUNT").equals("0")) {
	    output.append(callingObject.getRequiredValue("PREV_BASE_ORD_LIST"));
        }
        output.append(";\n");

       //new for 3.12.1
       
        output.append("MAJOR_RELEASE_ORDER;");
        if (majorAnddelta && getToolsOrder)
           output.append(callingObject.getRequiredValue("MESSAGE_ID"));
        else if (majorAnddelta && !getToolsOrder)
           output.append(callingObject.getRequiredValue("ORDER_NUMBER"));
        output.append(";\n");
        
       //end of new for 3.12.1

        callingObject.print("Writing TDOF String to file...", MQHandler.NOT_IMP);


        File f = new File(outputFile);

        File targetDir = new File(f.getParent());
        if( ! targetDir.isDirectory())
            if( ! targetDir.mkdirs())
                throw new IOException(outputFile + " (Directory) cannot be created");
        
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(output.toString().getBytes());
        fos.close();

        callingObject.print("Exiting TDOF.createDR()...", MQHandler.V_IMP);

        return output.toString();

    }





    String createPK() throws IOException {

        callingObject.print("Starting TDOF.createPK()...", MQHandler.V_IMP);

        String technology = "", versionNo = "", orderNumber = "";
        StringBuffer output = new StringBuffer();

        technology = callingObject.getRequiredValue("TECHNOLOGY");
        versionNo = callingObject.getRequiredValue("VERSION_NO");
        orderNumber = callingObject.getRequiredValue("ORDER_NUMBER");

        output.append("HEAD;" + orderNumber + ";" + versionNo + ";\n");

        output.append("CUST;" 
                      + ";" 
                      + ";;;\n");
        // two extra ;'s for blank Project and Location
        
        output.append("PRIMARY_CI;" 
                      + callingObject.getValue("FSE_NAME_1") 
                      + ";" 
                      + callingObject.getValue("FSE_LOC_1") 
                      + ";" 
                      + callingObject.getValue("FSE_PHONE_1") 
                      + ";" 
                      + callingObject.getValue("FSE_FAX_1") 
                      + ";" 
                      + callingObject.getValue("FSE_EMAIL_1") 
                      + ";\n");

        output.append("BACKUP_CI;;;;;;\n"); 

// output.append("COMMENTS;;\n");


        boolean toolkitOnly = true;


        output.append("SHIP;customer,,;");
        // extra , for blank relationship


        if(download)
            output.append(callingObject.getValue("FIRST_NAME") 
                          + " " + callingObject.getValue("LAST_NAME") 
                          + "," 
                          + callingObject.getValue("ADDRESS") 
                          + ",,"
                          // extra , for ADDRESS_2 field
                          + callingObject.getValue("CITY") 
                          + "," 
                          + callingObject.getValue("STATE") 
                          + "," 
                          + callingObject.getValue("ZIP") 
                          + "," 
                          + callingObject.getValue("PHONE") 
                          + "," 
                          + callingObject.getValue("FAX") 
                          + "," 
                          + callingObject.getRequiredValue("E_MAIL") 
                          + ",;");
        else
            output.append(callingObject.getRequiredValue("FIRST_NAME") 
                          + " " + callingObject.getRequiredValue("LAST_NAME") 
                          + "," 
                          + callingObject.getRequiredValue("ADDRESS") 
                          + ",,"
                          // extra , for ADDRESS_2 field
                          + callingObject.getRequiredValue("CITY") 
                          + "," 
                          + callingObject.getRequiredValue("STATE") 
                          + "," 
                          + callingObject.getRequiredValue("ZIP") 
                          + "," 
                          + callingObject.getValue("PHONE") 
                          + "," 
                          + callingObject.getValue("FAX") 
                          + "," 
                          + callingObject.getRequiredValue("E_MAIL") 
                          + ",;");


        if(download)
            output.append("DOWNLOAD,");
        else
            output.append("CD,");

        output.append(",;\n");


        callingObject.print("Writing TDOF String to file...", MQHandler.NOT_IMP);


        File f = new File(outputFile);

        File targetDir = new File(f.getParent());
        if( ! targetDir.isDirectory())
            if( ! targetDir.mkdirs())
                throw new IOException(outputFile + " (Directory) cannot be created");
        
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(output.toString().getBytes());
        fos.close();

        callingObject.print("Exiting TDOF.createPK()...", MQHandler.V_IMP);

        return output.toString();

    }




    private String getPlatformAlias(String platformName) {
        if(platformName.equals("aix"))
            return "RS6K";
        else if(platformName.equals("aix64"))
            return "RS6K64";
        else if(platformName.equals("aix5_64"))
            return "RS6K64_51";
       //new for 4.2.1 fixpack
        else if(platformName.equals("linux_amd64"))
           return "LIN64OPT";
        else if (platformName.equals("linux32"))
           return "LINUX32";
       //end of new for 4.2.1 fixpack
        else
            return platformName.toUpperCase();
    }




    private String[] getStringArray(String key) {
        String s = callingObject.getRequiredValue(key);
        StringTokenizer st = new StringTokenizer(s, ",");
        int numTokens = st.countTokens();
        String[] retArray = new String[numTokens];
        int i = 0;
        while(st.hasMoreTokens())
        retArray[i++] = st.nextToken().trim();
        return retArray;
    } 


    private String getAlias(String relation) {
        if(relation.equals("oth"))
            return "other";
        else
            return relation;
    }

}

