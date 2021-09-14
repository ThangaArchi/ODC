/**
 * DboxException_DeserProxy.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * o0526.04 v62905175048
 */

package oem.edge.ed.odc.dsmp.common;

public class DboxException_DeserProxy  extends java.lang.Exception  {
    private int errorCode;
    private int majorCode;
    private int minorCode;

    public DboxException_DeserProxy() {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(int majorCode) {
        this.majorCode = majorCode;
    }

    public int getMinorCode() {
        return minorCode;
    }

    public void setMinorCode(int minorCode) {
        this.minorCode = minorCode;
    }

    public java.lang.Object convert() {
      return new oem.edge.ed.odc.dsmp.common.DboxException(
        getErrorCode(),
        getMajorCode(),
        getMinorCode());
    }
}
