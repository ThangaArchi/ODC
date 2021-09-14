/**
 * GroupInfo_Deser.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * o0526.04 v62905175048
 */

package oem.edge.ed.odc.dsmp.common;

public class GroupInfo_Deser extends com.ibm.ws.webservices.engine.encoding.ser.BeanDeserializer {
    /**
     * Constructor
     */
    public GroupInfo_Deser(
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType, 
           com.ibm.ws.webservices.engine.description.TypeDesc _typeDesc) {
        super(_javaType, _xmlType, _typeDesc);
    }
    /**
     * Create instance of java bean
     */
    public void createValue() {
        value = new oem.edge.ed.odc.dsmp.common.GroupInfo();
    }
    protected boolean tryElementSetFromString(javax.xml.namespace.QName qName, java.lang.String strValue) {
        if (qName==QName_0_4) {
          ((GroupInfo)value).setGroupName(strValue);
          return true;}
        else if (qName==QName_0_5) {
          ((GroupInfo)value).setGroupOwner(strValue);
          return true;}
        else if (qName==QName_0_6) {
          ((GroupInfo)value).setGroupCompany(strValue);
          return true;}
        else if (qName==QName_0_7) {
          ((GroupInfo)value).setGroupCreated(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parselong(strValue));
          return true;}
        else if (qName==QName_0_8) {
          ((GroupInfo)value).setGroupVisibility(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parsebyte(strValue));
          return true;}
        else if (qName==QName_0_9) {
          ((GroupInfo)value).setGroupListability(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parsebyte(strValue));
          return true;}
        else if (qName==QName_0_10) {
          ((GroupInfo)value).setGroupMembersValid(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parseboolean(strValue));
          return true;}
        else if (qName==QName_0_11) {
          ((GroupInfo)value).setGroupAccessValid(com.ibm.ws.webservices.engine.encoding.ser.SimpleDeserializer.parseboolean(strValue));
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
    private final static javax.xml.namespace.QName QName_0_13 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "groupAccess");
    private final static javax.xml.namespace.QName QName_0_8 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "groupVisibility");
    private final static javax.xml.namespace.QName QName_0_5 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "groupOwner");
    private final static javax.xml.namespace.QName QName_0_9 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "groupListability");
    private final static javax.xml.namespace.QName QName_0_7 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "groupCreated");
    private final static javax.xml.namespace.QName QName_0_12 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "groupMembers");
    private final static javax.xml.namespace.QName QName_0_11 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "groupAccessValid");
    private final static javax.xml.namespace.QName QName_0_10 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "groupMembersValid");
    private final static javax.xml.namespace.QName QName_0_6 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "groupCompany");
    private final static javax.xml.namespace.QName QName_0_4 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "groupName");
}
