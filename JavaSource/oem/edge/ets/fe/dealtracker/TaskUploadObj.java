/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe.dealtracker;

/**
 * @author v2srikau
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TaskUploadObj {
    
    public static final String ERR_TOKENS = "tokens";
    public static final String ERR_OTHERS = "others";
    
    private String strError;  
    private String strData;
    private String strMsg;
    
    public TaskUploadObj(String strData, String strError) {
        this.strData = strData;
        this.strError = strError;
    }
    
    public TaskUploadObj(String strData, String strError, String strMsg) {
        this.strData = strData;
        this.strError = strError;
        this.strMsg = strMsg;
    }

    /**
     * @return Returns the strData.
     */
    public String getData() {
        return strData;
    }
    /**
     * @param strData The strData to set.
     */
    public void setData(String strData) {
        this.strData = strData;
    }
    /**
     * @return Returns the strError.
     */
    public String getError() {
        return strError;
    }
    /**
     * @param strError The strError to set.
     */
    public void setError(String strError) {
        this.strError = strError;
    }
    /**
     * @return Returns the strMsg.
     */
    public String getMsg() {
        return strMsg;
    }
    /**
     * @param strMsg The strMsg to set.
     */
    public void setMsg(String strMsg) {
        this.strMsg = strMsg;
    }
}
