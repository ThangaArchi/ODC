/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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
package oem.edge.ets.fe.ubp;

/**
 * @author Ravi K. Ravipati
 * Date: Feb 17, 2004
 * File: ETSSyncUser.java
 *
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import oem.edge.amt.AMTException;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtNewUserInfo;
import oem.edge.amt.EntitledStatic;
import oem.edge.amt.UserObject;
import oem.edge.common.Global;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

public class ETSSyncUser {
	
	public static final String VERSION = "1.5";
	
	private static Log logger = EtsLogger.getLogger(ETSSyncUser.class);

	public ETSSyncUser() {webId = "";}

	public String getWebId() {	return webId;}
	public void setWebId(String webId) {this.webId = webId;}

    public ETSStatus syncUser(Connection conn) {

        ETSStatus etsStatus  = new ETSStatus();
        UserObject userObject = new UserObject();
        AmtNewUserInfo decafNewUser = new AmtNewUserInfo();
        try {
            userObject = AccessCntrlFuncs.refreshUserDetails(conn,this.getWebId(),true,false);
            logger.debug("IR USERN IN SYNC USER==="+userObject.gIR_USERN);
			logger.debug("EDGE USERN IN SYNC USER==="+userObject.gUSERN);
			logger.debug("IR USER name IN SYNC USER==="+userObject.gUSER_FULLNAME);
            if ((userObject.gUSERN == null)|| (userObject.gUSERN.equals(""))){
                etsStatus.setErrCode(1);
                etsStatus.setErrText("Information: User not found in web identity");
            } else {
                if (!isIdInDecaf(conn,userObject.gUSERN)){
                    // do something
                    Hashtable decafUserSet = new Hashtable();
                    decafUserSet.put("COMPANY", Global.getQString(userObject.gUSER_COMPANY));
                    decafUserSet.put("EMAIL", userObject.gEMAIL);
                    decafUserSet.put("FIRST_NAME", Global.getQString(userObject.gFIRST_NAME));
                    decafUserSet.put("LAST_NAME", Global.getQString(userObject.gLAST_NAME));
                    decafUserSet.put("USERNAME", userObject.gUSERN);
                    decafUserSet.put("COUNTRY_CODE", EntitledStatic.getValue(conn,"select country_code from decaf.country where iso_code = '"+userObject.gUSER_CNTRY+"' for read only"));
                    decafUserSet.put("USER_TYPE","E");
                    decafUserSet.put("POC_ID","MPOC1000");

                    decafNewUser.sendNewUsersDetails(conn,decafUserSet);
                }
                // update user details
                updateDecafUserDetail(conn, userObject);
                
				etsStatus.setErrCode(0);
				etsStatus.setErrText("Sync user is success");
            }
        } catch (AMTException amtEx) {
            etsStatus.setErrCode(-1);
            etsStatus.setErrText("Error: AMT error in pulling user into AMT from web identity");
    	} catch (SQLException sqlEx){
            etsStatus.setErrCode(-2);
            etsStatus.setErrText("Error: SQL error in pulling user into AMT from web identity");
        } catch (Exception eX){
            etsStatus.setErrCode(-3);
            etsStatus.setErrText("Error: General error in pulling user into AMT from web identity");
        }
        return (etsStatus);
    }


	// private methods
	private String webId;
    public boolean isIdInDecaf(Connection conn, String edgeId) {
        boolean inDecaf = true; // true: to avoid accidental
                                // insert into decaf
        try {
        String qryStr = "select count(*) from decaf.users where userid = '"+edgeId+"' for read only";
        String cnt = EntitledStatic.getValue(conn, qryStr);
        inDecaf = (cnt.equals("0"))?false:true;
        } catch (SQLException sqlEx){
        } catch (AMTException amtEx){
        }
        return (inDecaf);
    }
    private void updateDecafUserDetail(Connection conn, UserObject userObject) throws SQLException, Exception{
        String str = "select count(*) from decaf.user_details a, decaf.users b where a.decaf_id = b.decaf_id and b.userid = '"+userObject.gUSERN+"' ";
        String isInDetail = EntitledStatic.getValue(conn, str);

        StringBuffer userAddress = new StringBuffer();
        userAddress.append(Global.getQString(userObject.gCOMPANY_ADDR1.trim()))
                   .append(" ")
                   .append(Global.getQString(userObject.gCOMPANY_ADDR2.trim()))
                   .append(" ")
                   .append(Global.getQString(userObject.gCOMPANY_CITY.trim()))
                   .append(" ")
                   .append(Global.getQString(userObject.gCOMPANY_STATECODE.trim()))
                   .append(" ")
                   .append(Global.getQString(userObject.gCOMPANY_POSTCODE.trim()));

        String qry="";
        String decafId = EntitledStatic.getValue(conn,"select decaf_id from decaf.users where userid = '"+userObject.gUSERN+"' for read only");

        if (isInDetail.equals("0")){
            // insert
            qry="insert into decaf.user_details values ('"+decafId+"','"+userAddress.toString()+"','E&TS','NONE',' ',' ','etsapi',(current timestamp))";
            EntitledStatic.safeInsert(conn,qry);
        } else {
            // update
            qry = "update decaf.user_details set address='"+userAddress.toString()+"', last_timestamp=(current timestamp) where decaf_id = '"+decafId+"'";
            EntitledStatic.fireUpdate(conn,qry);
        }

    }
}
