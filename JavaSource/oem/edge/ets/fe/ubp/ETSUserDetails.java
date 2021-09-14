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
 * Date: Feb 19, 2004
 * File: ETSUserDetails.java
 *
 */
import java.util.*;
import java.sql.*;

import oem.edge.amt.*;

public class ETSUserDetails {

    public final int USER_TYPE_INTERNAL=1;
    public final int USER_TYPE_INTERNAL_PENDING_VALIDATION=2;
    public final int USER_TYPE_EXTERNAL=3;
    public final int USER_TYPE_INVALID=4;

    public ETSUserDetails(){
        webId=""; firstName="";  lastName="";
        eMail=""; streetAddress="";   company="";
        userType=USER_TYPE_INVALID;
        userExists = false;  decafId = ""; edgeId = "";
        pocId = ""; pocEmail="";countryCode="";
    }

    public void setWebId(String webId) {    this.webId = webId.toLowerCase(); }

    public String getWebId() { return webId; }
    public String getFirstName() {  return firstName;   }
    public String getLastName() {return lastName;   }
    public String getEMail() {  return eMail;   }
    public String getStreetAddress() {  return streetAddress;   }
    public String getCompany() {return company;}
    public int getUserType() {  return userType;    }
    public boolean isUserExists() { return userExists;  }
    public String getDecafId() {  return decafId;    }
    public String getEdgeId() {  return edgeId;    }
    public String getPocEmail() { return pocEmail;}
    public String getPocId() { return pocId; }
    public String getCountryCode() { return countryCode; }
    
    public void extractUserDetails(Connection conn) {
        try {
            String qry =
            "with u "
            +" (fname, lname, email, address, company, utype, decafid"
            +", userid, pocid, pocemail, countrycode) as"
            +" (select a.first_name, a.last_name "
            +", case a.user_type when 'I' then a.email_id else c.user_email end as email"
            +", rtrim(c.company_addr1)||' '||rtrim(c.company_addr2)||' '||rtrim(c.company_city)||' '||rtrim(c.company_statecode)||' '||rtrim(c.company_postcode)||' '||rtrim(c.company_cntry_code) as address"
            +", a.assoc_company, a.user_type, a.decaf_id"
            +", a.userid, a.poc_id, a.poc_mail ,a.country_code"
            +" from  decaf.users a, amt.users c"
            +" where a.userid = c.edge_userid"
            +" and   c.ir_userid='"+this.getWebId()+"' "
            +" ) select u.fname, u.lname, u.email, u.address, u.company"
            +", u.utype, u.decafid, u.userid, u.pocid, u.pocemail,u.countrycode"
            +" from u  "
//            +" from u left outer join decaf.user_details b "
//            +" on u.decafid = b.decaf_id "
            +" with ur "; // with ur to avoid lock on amt

            Vector vect = EntitledStatic.getVQueryResult(conn, qry,11);
            if (vect.size()==0){
                // no user found
                this.setUserType(this.USER_TYPE_INVALID);
                this.setUserExists(false);
            } else {
                String[] values = new String[11];
                values  = (String[])vect.get(0);
                this.setFirstName(values[0]);
                this.setLastName(values[1]);
                this.setEMail(values[2]);
                this.setStreetAddress(values[3]);
                this.setCompany(values[4]);
                this.setUserType(getUserType(values[5]));
                if (getUserType()==this.USER_TYPE_INTERNAL)
                    {this.setCompany("IBM");}
                this.setDecafId(values[6]);
                this.setEdgeId(values[7]);
                this.setPocId(values[8]);
                this.setPocEmail(values[9]);
                this.setCountryCode(values[10]);
                this.setUserExists(true);
            }
        } catch (SQLException sqlEx){
        } catch (Exception eX){
        }
    }

    // private variables
    private String webId;
    private String firstName;
    private String lastName;
    private String eMail;
    private String streetAddress;
    private String company;
    private int userType;
    private boolean userExists;
    private String pocId;
    private String pocEmail;
    private String decafId;
    private String edgeId;
    private String countryCode;

    private void setFirstName(String firstName) {this.firstName = firstName; }
    private void setLastName(String lastName) { this.lastName = lastName;   }
    private void setEMail(String eMail) {this.eMail = eMail; }
    private void setStreetAddress(String streetAddress)
        {this.streetAddress = streetAddress;    }
    private void setCompany(String company) { this.company = company; }
    private void setUserType(int userType) { this.userType = userType;   }
    private void setUserExists(boolean userExists)
        {this.userExists = userExists;}
    private void setDecafId( String decafId) { this.decafId = decafId; }
    private void setEdgeId( String edgeId) { this.edgeId = edgeId; }
    private void setPocEmail(String pocEmail) { this.pocEmail = pocEmail; }
    private void setPocId(String pocId) { this.pocId = pocId; }
    private void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    private int getUserType(String str){
        int usrType=this.USER_TYPE_INVALID; // invalid by default:
                                            // to avoid usertype changes
        if (str.equals("I")) usrType = this.USER_TYPE_INTERNAL;
        if (str.equals("E")) usrType = this.USER_TYPE_EXTERNAL;
        if (str.equals("P")||str.equals("T"))
        usrType = this.USER_TYPE_INTERNAL_PENDING_VALIDATION;
        return usrType;
    }
}
