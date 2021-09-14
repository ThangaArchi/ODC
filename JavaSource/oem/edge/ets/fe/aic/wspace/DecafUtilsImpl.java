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
package oem.edge.ets.fe.aic.wspace;

import java.util.Vector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.decaf.ws.DecafEntAccessObj;
import oem.edge.decaf.ws.DecafGenericDataObj;
import oem.edge.decaf.ws.DecafProjObj;
import oem.edge.decaf.ws.DecafWsRepObj;
import oem.edge.decaf.ws.genericdata.DecafGenericDataDB;
import oem.edge.decaf.ws.project.DecafCrtModProjDB;
import oem.edge.decaf.ws.useraccess.DecafUserAccessDB;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DecafUtilsImpl {

	private static Log logger = EtsLogger.getLogger(DecafUtilsImpl.class);
	public static final String VERSION = "1.1";

/**
 * 		inserts data in decaf.genericdatatype table
 * 		BPS_OWNER is passed for reference of roles
 * 		checks the roles from decaf.roles & decaf.roles_levels table
 * 		on success the decaf api returns - INSERT_GENERICDATA_SUCCESS
 * @param datatypeName  -- same as project Name for E&TS
 * @return  
 */
	public boolean createDataType(String datatypeName, String sLoginUserId) throws Exception {		
		boolean status = false;
		
		DecafGenericDataObj decafGenDataObj = new DecafGenericDataObj();
		decafGenDataObj.setEntitlement(Defines.BPSOWNER_ENT);
		//datatypeName = ETSProj.getName();
		decafGenDataObj.setDataTypeValue(datatypeName);
		decafGenDataObj.setDataType(Defines.BPSTEAMS_DATATYPE);
		
		String log = "WS"; //Decaf specific logging parameter
		
		DecafGenericDataDB decafGenDataDb = new DecafGenericDataDB();
		
		//return msg object
		DecafWsRepObj wsRepObj = new DecafWsRepObj();
		
		try {
			wsRepObj = decafGenDataDb.genericDataInsert(decafGenDataObj,sLoginUserId,log);
		
			if (logger.isDebugEnabled()) {
				logger.debug("TEAMROOM - data type create request::" + datatypeName);
				logger.debug("DECAF DATATYPE CREATE OBJECT RET CODE==" + wsRepObj.getRetCode());
				logger.debug("DECAF DATATYPE CREATE OBJECT RET CODE MSG==" + wsRepObj.getRetMsg());
				System.out.println("hello u r here");
			}
		} finally {
			
		}
		
		if (wsRepObj.getRetCode().equals("INSERT_GENERICDATA_SUCCESS")) {

			status = true;
		}

		return status;
	}
	
/**
 * insert data in decaf.project & decaf.project_attributes tables
 * API returns success -- CREATED_PROJ_SUCCESS
 * @param datatypeName
 * @param strCompany
 * @param strEntitlement
 * @param sLoginuserId
 * @return
 * @throws Exception
 */
	public synchronized boolean createWSProfile(
		String datatypeName,
		String strCompany,
		String strEntitlement,
		String strProfileName,
		String sLoginUserId) throws Exception 
	{
	
		boolean status = false;

		DecafCrtModProjDB decafCrtModProjDB = new DecafCrtModProjDB();
		DecafProjObj decafProjObj = new DecafProjObj();
        DecafEntAccessObj decafEntAccObj=null;
        
        Vector vtApprovers = new Vector();
        vtApprovers.add(Defines.TEAMROOM_ENTITLEMENT_APPROVER);

        Vector dataTypeList = new Vector();
        dataTypeList.add(datatypeName);    //ASK

        decafProjObj.setName(strProfileName);
        decafProjObj.setDesc(strProfileName);
        decafProjObj.setProjType(Defines.TEAMROOM_PROJECT_TYPE);  
        decafProjObj.setApprArray(vtApprovers);
        decafProjObj.setNoOfAppr(Defines.TEAMROOM_ENT_APPR_LEVEL);
        decafProjObj.setIsRestricted("N");    //ASK   
        decafProjObj.setEndDate(Defines.TEAMROOM_END_DATE);
        
        decafEntAccObj = new DecafEntAccessObj();
        decafEntAccObj.setEntName(strEntitlement);
        decafEntAccObj.setLevel(Integer.parseInt(
        		Defines.TEAMROOM_ENT_APPR_LEVEL));
        
        decafEntAccObj.setDataTypeVal(dataTypeList);
        decafProjObj.setCompany(strCompany); 
        
		//return msg object
		DecafWsRepObj wsRepObj = new DecafWsRepObj();
		
		try {

			if (logger.isDebugEnabled()) {
				logger.debug("TEAMROOM - PROFILE create request Details::");
				logger.debug("Profile Name::" + strProfileName);
				logger.debug("datatype Name::" + datatypeName);
				logger.debug("Entitlement ::" + strEntitlement);
				logger.debug("Company Name::" + strCompany);
			}
			
			wsRepObj =  decafCrtModProjDB.createProfile(decafProjObj,decafEntAccObj,sLoginUserId,"WS",false);

			if (logger.isDebugEnabled()) {
				logger.debug("DECAF PROFILE CREATE OBJECT RET CODE==" + wsRepObj.getRetCode());
				logger.debug("DECAF PROFILE CREATE OBJECT RET CODE MSG==" + wsRepObj.getRetMsg());
			}
		} finally {
			
		}
		
		if (wsRepObj.getRetCode().equals("CREATED_PROJ_SUCCESS")) {
			status = true;
		}      
		
		return status;
	}
	
/**
 * This method request profile for the user
 * @param strRequestForUserId
 * @param strProfileName
 * @param sLoginUserId
 * @param strDataSource
 * @return
 */	
	public synchronized boolean requestProjectForUser(
			String strRequestForUserId,
			String strProfileName,
			String sLoginUserId,
			String strDataSource)
	throws Exception {
		boolean status = false;
		
		Vector projVect = new Vector();
		//String projName = "IBM BPS Owner " + datatype;
		
		projVect.add(strProfileName);
		int skipReq = 0;
		String log = "WS"; // Decaf specific logging
		
		DecafEntAccessObj decafEntAccessObj = new DecafEntAccessObj();
		
		DecafUserAccessDB userAccessDB = new DecafUserAccessDB();
		//DecafCrtModProjDB decafCrtModProjDB = new DecafCrtModProjDB();
		
		//return msg object
		DecafWsRepObj wsRepObj = new DecafWsRepObj();
		try {
		
			if (logger.isDebugEnabled()) {
				logger.debug("TEAMROOM - PROFILE ACCESS REQUEST DETAILS::");
				logger.debug("Profile Name::" + strProfileName);
				logger.debug("User Name   ::" + strRequestForUserId);
			}
			wsRepObj = userAccessDB.reqUserAccess(strRequestForUserId,
												null,
												projVect,
												sLoginUserId,
												skipReq,
												strDataSource,
												log);
		
			if (logger.isDebugEnabled()) {
				logger.debug("Project Acceess Request RET CODE==" + wsRepObj.getRetCode());
				logger.debug("Project Acceess Request RET CODE MSG==" + wsRepObj.getRetMsg());
			}
		} finally {
			
		}
		//loginIrUserId, skipReq, ETS_BE_DATASRC, log);
		if (wsRepObj.getRetCode().equals("PROJ_REQ_SUCCESS")) {
			status = true;
		}      
		
		return status;
		
	}

	public boolean createDatatypeAndProfile(String datatype, String strRequestorUserId , String strCompany, String sLoginUserId)
	throws Exception
	{
		boolean status = false;
		
		//DecafUtilsImpl utils = new DecafUtilsImpl();
		try {
			
			status = createDataType(datatype,sLoginUserId);
			
			if (status) {
				//Create profile for Owner
				String strOwnerProfileName = strCompany + " BPS OWNER " + datatype;
				status = createWSProfile(datatype,
										strCompany,
										Defines.BPSOWNER_ENT,
										strOwnerProfileName,
										sLoginUserId);

				//create profile for author
				if (status) {
					String strAuthorProfileName = strCompany + " BPS AUTHOR " + datatype;
					status = createWSProfile(datatype,
										strCompany,
										Defines.BPSAUTHOR_ENT,
										strAuthorProfileName,
										sLoginUserId);
				}
				//create profile for reader
				String strReaderProfileName = strCompany + " BPS READER " + datatype;
				createWSProfile(datatype,
						strCompany,
						Defines.BPSREADER_ENT,
						strReaderProfileName,
						sLoginUserId);
				
				//Request access for Owner Project
				status = requestProjectForUser(strRequestorUserId,strOwnerProfileName,strRequestorUserId,"amtds");
			}
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("FAILED to CREATE WS PROFILE & DATATYPE");
			}
			status = false;
			e.printStackTrace();
			throw e;
		} finally {
			
		}
		
		return status;
		
	}

    // START FIX FOR CSR IBMCC00010835 - V2SRIKAU
	public boolean createProfile(String datatype, String strRequestorUserId , String strCompany, String sLoginUserId)
	throws Exception
	{
		boolean status = false;
		
		try {
			
			status = true;//createDataType(datatype,sLoginUserId);
			
			if (status) {
				//Create profile for Owner
				String strOwnerProfileName = strCompany + " BPS OWNER " + datatype;
				status = createWSProfile(datatype,
										strCompany,
										Defines.BPSOWNER_ENT,
										strOwnerProfileName,
										sLoginUserId);

				//create profile for author
				if (status) {
					String strAuthorProfileName = strCompany + " BPS AUTHOR " + datatype;
					status = createWSProfile(datatype,
										strCompany,
										Defines.BPSAUTHOR_ENT,
										strAuthorProfileName,
										sLoginUserId);
				}
				//create profile for reader
				String strReaderProfileName = strCompany + " BPS READER " + datatype;
				createWSProfile(datatype,
						strCompany,
						Defines.BPSREADER_ENT,
						strReaderProfileName,
						sLoginUserId);
				
				//Request access for Owner Project
				status = requestProjectForUser(strRequestorUserId,strOwnerProfileName,strRequestorUserId,"amtds");
			}
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("FAILED to CREATE WS PROFILE & DATATYPE");
			}
			status = false;
			e.printStackTrace();
			throw e;
		} finally {
			
		}
		
		return status;
		
	}
    // END FIX FOR CSR IBMCC00010835 - V2SRIKAU
	
	public boolean checkDataTypeExists(String datatypeName, Connection conn)
		throws SQLException {
		
		boolean status = false;
		
		PreparedStatement getDecafDataSt;

		getDecafDataSt = conn.prepareStatement("select count(*) as COUNT  "
				 + "from decaf.genericdatatype where datatype_value = ? " );
		
		getDecafDataSt.setString(1, datatypeName);
		ResultSet rs = getDecafDataSt.executeQuery();
		int iCount =0;

		if (rs.next()) {
			iCount = rs.getInt("COUNT");
		}
		if (iCount > 0) {
			status = true;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("datatype Name for Teamroom::" + datatypeName);
			logger.debug("check datatype Exists count::" + iCount);
		}
		getDecafDataSt.close();

		return status;
	}

}
