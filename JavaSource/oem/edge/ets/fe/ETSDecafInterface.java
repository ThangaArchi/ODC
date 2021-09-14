package oem.edge.ets.fe;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: IBM Customer Connect                                          */
/* (C) Copyright IBM Corp. 2000 - 2001                                       */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** EOF : HEADER *************************************/
/* File Name   : ETSDecafInterface.java                                      */
/* Release     : 3.12.1                                                       */
/* Description : Implements DecafInterface to get decaf datatype values      */
/*               for ETS_PROJECTS entitlement.                               */
/* Created By  : Navneet Gupta                                               */
/* Date        : 10/22/2003                                                  */
/*****************************************************************************/

import java.util.Vector;
import java.sql.*;

public class ETSDecafInterface implements oem.edge.amt.DecafInterface {

    private static final String DATATYPE_VALUE = "IBM";

    private String ENTITLEMENT = "";
    private String DATATYPE = ""; 
    private static final String CLASS_VERSION = "3.12.1";


    public Vector getDataTypeList(Connection conn) throws Exception {
        Vector dataTypeList = new Vector();
        dataTypeList.add(DATATYPE_VALUE);
        return dataTypeList;
    }

    public Vector getDataTypeList(Connection conn, String sPrevValStr) throws Exception {
        sPrevValStr = sPrevValStr.trim();
        Vector dataTypeList = new Vector();
        if( ! sPrevValStr.equals(DATATYPE_VALUE) )
            dataTypeList.add(DATATYPE_VALUE);
        return dataTypeList;
    }


    /*
    // old version of functions used before 3.12
    public Vector getDataTypeList(Connection conn) throws Exception {
	String query = "select distinct DECAF_PROJECT_NAME from ETS.ETS_PROJECTS order by DECAF_PROJECT_NAME for read only";
	return getETSDataTypeList(conn, query);
    }

    public Vector getDataTypeList(Connection conn, String excludeDataTypes) throws Exception {
	String query = "select distinct DECAF_PROJECT_NAME from ETS.ETS_PROJECTS where DECAF_PROJECT_NAME not in (" + excludeDataTypes + ") order by DECAF_PROJECT_NAME for read only";
	return getETSDataTypeList(conn, query);
    }

    private Vector getETSDataTypeList(Connection conn, String query) throws Exception {
	Vector dataTypeList = new Vector();
	Statement stmt = conn.createStatement();
	ResultSet rs = stmt.executeQuery(query);
	while(rs.next()) {
	    String dataType = rs.getString("DECAF_PROJECT_NAME").trim();
	    dataTypeList.addElement(dataType);
	}
	rs.close();
	stmt.close();
	return dataTypeList;
    }
    */
 

    public static String getChangeLog() {
        return "<ul><li>10/22/2003: Navneet: changes for 3.12</li>"
             +     "<li>09/24/2003: Navneet: changes for 3.9</li></ul>";
    }

    public static String getClassVersion() {
        return CLASS_VERSION;
    }
 
    public String[] getDataTypeID(Connection conn, String[] value) throws Exception {
        String[] retValue = new String[value.length];
        for(int i = 0; i < value.length; i++) {
            retValue[i] = getDataTypeID(conn, value[i]);
        }
        return retValue;
    }
 
    public String getDataTypeID(Connection conn, String value) throws Exception {
        return value;
    }
 
    public String[] getDataTypeValue(Connection conn, String[] id) throws Exception {
        String[] retValue = new String[id.length];
        for (int i = 0; i < id.length; i++) {
            retValue[i] = getDataTypeValue(conn, id[i]);
        }
        return retValue;
    }
 
    public String getDataTypeValue(Connection conn, String id) throws Exception {
        return id;
    }
 
    public void SetDatatypeEntitlement(String entitlement, String datatype, boolean b) {
        ENTITLEMENT = entitlement;
        DATATYPE = datatype; 
    }

}

