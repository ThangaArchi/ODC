/**
 * PackageInfo_Deser.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * o0526.04 v62905175048
 */

package oem.edge.ed.odc.dropbox.common;

public class PackageInfo_Deser extends com.ibm.ws.webservices.engine.encoding.ser.BeanDeserializer {
    /**
     * Constructor
     */
    public PackageInfo_Deser(
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType, 
           com.ibm.ws.webservices.engine.description.TypeDesc _typeDesc) {
        super(_javaType, _xmlType, _typeDesc);
    }
    /**
     * Create instance of java bean
     */
    public void createValue() {
        value = new oem.edge.ed.odc.dropbox.common.PackageInfo();
    }
    protected boolean tryElementSetFromString(javax.xml.namespace.QName qName, java.lang.String strValue) {
        if (qName==QName_0_30) {
          ((PackageInfo)value).setPackageNumElements(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parseint(strValue));
          return true;}
        else if (qName==QName_0_31) {
          ((PackageInfo)value).setPackageStatus(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parsebyte(strValue));
          return true;}
        else if (qName==QName_0_32) {
          ((PackageInfo)value).setPackageDescription(strValue);
          return true;}
        else if (qName==QName_0_33) {
          ((PackageInfo)value).setPackageExpiration(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parselong(strValue));
          return true;}
        else if (qName==QName_0_34) {
          ((PackageInfo)value).setPackageCreation(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parselong(strValue));
          return true;}
        else if (qName==QName_0_35) {
          ((PackageInfo)value).setPackageCommitted(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parselong(strValue));
          return true;}
        else if (qName==QName_0_36) {
          ((PackageInfo)value).setPackageSize(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parselong(strValue));
          return true;}
        else if (qName==QName_0_37) {
          ((PackageInfo)value).setPackageName(strValue);
          return true;}
        else if (qName==QName_0_38) {
          ((PackageInfo)value).setPackageId(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parselong(strValue));
          return true;}
        else if (qName==QName_0_39) {
          ((PackageInfo)value).setPackageOwner(strValue);
          return true;}
        else if (qName==QName_0_40) {
          ((PackageInfo)value).setPackageCompany(strValue);
          return true;}
        else if (qName==QName_0_41) {
          ((PackageInfo)value).setPackagePoolId(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parselong(strValue));
          return true;}
        else if (qName==QName_0_42) {
          ((PackageInfo)value).setPackageMarked(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parseboolean(strValue));
          return true;}
        else if (qName==QName_0_43) {
          ((PackageInfo)value).setPackageCompleted(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parseboolean(strValue));
          return true;}
        else if (qName==QName_0_44) {
          ((PackageInfo)value).setPackageHidden(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parseboolean(strValue));
          return true;}
        else if (qName==QName_0_45) {
          ((PackageInfo)value).setPackageReturnReceipt(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parseboolean(strValue));
          return true;}
        else if (qName==QName_0_46) {
          ((PackageInfo)value).setPackageSendNotification(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parseboolean(strValue));
          return true;}
        else if (qName==QName_0_47) {
          ((PackageInfo)value).setPackageFlags(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parsebyte(strValue));
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
    private final static javax.xml.namespace.QName QName_0_42 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageMarked");
    private final static javax.xml.namespace.QName QName_0_46 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageSendNotification");
    private final static javax.xml.namespace.QName QName_0_31 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageStatus");
    private final static javax.xml.namespace.QName QName_0_36 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageSize");
    private final static javax.xml.namespace.QName QName_0_44 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageHidden");
    private final static javax.xml.namespace.QName QName_0_39 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageOwner");
    private final static javax.xml.namespace.QName QName_0_35 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageCommitted");
    private final static javax.xml.namespace.QName QName_0_43 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageCompleted");
    private final static javax.xml.namespace.QName QName_0_47 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageFlags");
    private final static javax.xml.namespace.QName QName_0_37 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageName");
    private final static javax.xml.namespace.QName QName_0_45 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageReturnReceipt");
    private final static javax.xml.namespace.QName QName_0_32 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageDescription");
    private final static javax.xml.namespace.QName QName_0_41 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packagePoolId");
    private final static javax.xml.namespace.QName QName_0_34 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageCreation");
    private final static javax.xml.namespace.QName QName_0_30 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageNumElements");
    private final static javax.xml.namespace.QName QName_0_38 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageId");
    private final static javax.xml.namespace.QName QName_0_33 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageExpiration");
    private final static javax.xml.namespace.QName QName_0_40 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageCompany");
}
