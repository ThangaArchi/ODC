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


package oem.edge.ets.fe;

/**
 * @author Ravi K. Ravipati
 * Date: Jan 22, 2004
 * File: ETSMessgae.java
 *
 */
public class ETSMessgae {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.3";


	public ETSMessgae() {
		_errType = this.TYPE_SUCCESS;
		_errCode = new StringBuffer("");
		_errMsg = new StringBuffer("");
		_errTxt = new StringBuffer("");
        _returnURL = "";
	}
	public final int TYPE_ERROR = 0;
	public final int TYPE_INFO = 1;
	public final int TYPE_WARN = 2;
	public final int TYPE_SUCCESS = 3;
	public final int TYPE_FAIL = 4;

	public void setErrType(int errType) {_errType = errType;}
	public boolean isError() {	return (_errType == 0) ? true : false;	}
	public boolean isInfo() {return (_errType == 1) ? true : false;}
	public boolean isWarn() {return (_errType == 2) ? true : false;}
	public boolean isSuccess() {return (_errType == 3) ? true : false;}
	public boolean isFail() {return (_errType == 4) ? true : false;}

	public void setErrorCode(String errCode) {	_errCode.append(errCode);}
	public void appendErrorCode(String errCode) {	_errCode.append(errCode);}
	public void setErrMsg(String errMsg) {	_errMsg.append(errMsg);}
	public void appendErrMsg(String errMsg) {	_errMsg.append(errMsg);}

	public void setErrTxt(String errTxt) {	_errTxt.append(errTxt);}
	public void appendErrTxt(String errTxt) {	_errTxt.append(errTxt);	}

    public void setReturnURL(String returnURL){ _returnURL = returnURL;}

	public String getMessageWindow() {
		StringBuffer msg = new StringBuffer();
        msg.append("<table summary=\" \" border=\"0\" cellspacing=\"1\" cellpading=\"1\">");
        msg.append("<tr><td width=\"443\" align=\"center\">");
        msg.append("<table summary=\" \" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
        msg.append("<tr><td class=\"tdblue\">");
        switch (this.getErrType()){
            case TYPE_ERROR: msg.append("Error"); break;
            case TYPE_INFO: msg.append("Information"); break;
            case TYPE_WARN: msg.append("Warning"); break;
            case TYPE_SUCCESS: msg.append("Success"); break;
            case TYPE_FAIL: msg.append("Failure"); break;
        }
        msg.append("</td></tr>");
        msg.append("<tr><td>");
        msg.append("<table summary=\" \" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
        msg.append("<tr style=\"background-color: #eeeeee\"><td>"+this.getErrCode()+":"+this.getErrMsg()+"</td></tr>");
        msg.append("<tr style=\"background-color: #eeeeee\"><td><span class=\"small\">"+this.getErrTxt()+"</span></td></tr>");
        msg.append("<tr><td align=\"center\"><a href=\""+this.getReturnURL()+"\"><img src=\""+ Defines.BUTTON_ROOT+ "arrow_rd.gif\" width=\"21\" height=\"21\" border=\"0\" alt=\"Ok\" />Ok</a></td></tr>");
        msg.append("</table>");
        msg.append("</td></tr></table>");
        msg.append("</td></tr></table>");

        return msg.toString();
    }
		//  private variables
	private int _errType;
	private StringBuffer _errCode;
	private StringBuffer _errMsg;
	private StringBuffer _errTxt;
    private String _returnURL;

	private int getErrType() {return _errType;}
    private String getErrCode() {return _errCode.toString();}
    private String getErrMsg() {return _errMsg.toString();}
    private String getErrTxt() {return _errTxt.toString();}
    private String getReturnURL () {return _returnURL;}
}
