/**
 * FileInfo_Deser.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * o0526.04 v62905175048
 */

package oem.edge.ed.odc.dropbox.common;

public class FileInfo_Deser extends com.ibm.ws.webservices.engine.encoding.ser.BeanDeserializer {
    /**
     * Constructor
     */
    public FileInfo_Deser(
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType, 
           com.ibm.ws.webservices.engine.description.TypeDesc _typeDesc) {
        super(_javaType, _xmlType, _typeDesc);
    }
    /**
     * Create instance of java bean
     */
    public void createValue() {
        value = new oem.edge.ed.odc.dropbox.common.FileInfo();
    }
    protected boolean tryElementSetFromString(javax.xml.namespace.QName qName, java.lang.String strValue) {
        if (qName==QName_0_48) {
          ((FileInfo)value).setFileStatus(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parsebyte(strValue));
          return true;}
        else if (qName==QName_0_49) {
          ((FileInfo)value).setFileExpiration(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parselong(strValue));
          return true;}
        else if (qName==QName_0_50) {
          ((FileInfo)value).setFileCreation(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parselong(strValue));
          return true;}
        else if (qName==QName_0_51) {
          ((FileInfo)value).setFileSize(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parselong(strValue));
          return true;}
        else if (qName==QName_0_52) {
          ((FileInfo)value).setFileName(strValue);
          return true;}
        else if (qName==QName_0_53) {
          ((FileInfo)value).setFileMD5(strValue);
          return true;}
        else if (qName==QName_0_54) {
          ((FileInfo)value).setFileId(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parselong(strValue));
          return true;}
        return false;
    }
    protected boolean tryAttributeSetFromString(javax.xml.namespace.QName qName, java.lang.String strValue) {
        return false;
    }
    protected boolean tryElementSetFromObject(javax.xml.namespace.QName qName, java.lang.Object objValue) {
        if (objValue == null) {
          return true;
        }
        return false;
    }
    protected boolean tryElementSetFromList(javax.xml.namespace.QName qName, java.util.List listValue) {
        return false;
    }
    private final static javax.xml.namespace.QName QName_0_51 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "fileSize");
    private final static javax.xml.namespace.QName QName_0_54 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "fileId");
    private final static javax.xml.namespace.QName QName_0_48 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "fileStatus");
    private final static javax.xml.namespace.QName QName_0_53 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "fileMD5");
    private final static javax.xml.namespace.QName QName_0_49 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "fileExpiration");
    private final static javax.xml.namespace.QName QName_0_52 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "fileName");
    private final static javax.xml.namespace.QName QName_0_50 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "fileCreation");
}
