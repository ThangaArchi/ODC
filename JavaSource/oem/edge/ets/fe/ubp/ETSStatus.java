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
 * File: ETSStatus.java
 *
 */
public class ETSStatus {

    public ETSStatus(){
        errCode = -1; // no error - success
        errText = "Operation Not Successful";
    }
    public int getErrCode() { return errCode; }
    public void setErrCode(int errCode) { this.errCode = errCode;}

    public String getErrText() { return errText;}
    public void setErrText(String errText) { this.errText = errText;}

    private int errCode;
    private String errText;


}
