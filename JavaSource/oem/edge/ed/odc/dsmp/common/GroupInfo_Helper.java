/**
 * GroupInfo_Helper.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * o0526.04 v62905175048
 */

package oem.edge.ed.odc.dsmp.common;

public class GroupInfo_Helper {
    // Type metadata
    private static final com.ibm.ws.webservices.engine.description.TypeDesc typeDesc =
        new com.ibm.ws.webservices.engine.description.TypeDesc(GroupInfo.class);

    static {
        typeDesc.setOption("buildNum","o0526.04");
        com.ibm.ws.webservices.engine.description.FieldDesc field = new com.ibm.ws.webservices.engine.description.ElementDesc();
        field.setFieldName("groupName");
        field.setXmlName(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "groupName"));
        field.setXmlType(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(field);
        field = new com.ibm.ws.webservices.engine.description.ElementDesc();
        field.setFieldName("groupOwner");
        field.setXmlName(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "groupOwner"));
        field.setXmlType(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(field);
        field = new com.ibm.ws.webservices.engine.description.ElementDesc();
        field.setFieldName("groupCompany");
        field.setXmlName(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "groupCompany"));
        field.setXmlType(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(field);
        field = new com.ibm.ws.webservices.engine.description.ElementDesc();
        field.setFieldName("groupCreated");
        field.setXmlName(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "groupCreated"));
        field.setXmlType(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"));
        typeDesc.addFieldDesc(field);
        field = new com.ibm.ws.webservices.engine.description.ElementDesc();
        field.setFieldName("groupVisibility");
        field.setXmlName(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "groupVisibility"));
        field.setXmlType(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"));
        typeDesc.addFieldDesc(field);
        field = new com.ibm.ws.webservices.engine.description.ElementDesc();
        field.setFieldName("groupListability");
        field.setXmlName(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "groupListability"));
        field.setXmlType(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"));
        typeDesc.addFieldDesc(field);
        field = new com.ibm.ws.webservices.engine.description.ElementDesc();
        field.setFieldName("groupMembersValid");
        field.setXmlName(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "groupMembersValid"));
        field.setXmlType(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(field);
        field = new com.ibm.ws.webservices.engine.description.ElementDesc();
        field.setFieldName("groupAccessValid");
        field.setXmlName(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "groupAccessValid"));
        field.setXmlType(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(field);
        field = new com.ibm.ws.webservices.engine.description.ElementDesc();
        field.setFieldName("groupMembers");
        field.setXmlName(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "groupMembers"));
        field.setXmlType(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"));
        typeDesc.addFieldDesc(field);
        field = new com.ibm.ws.webservices.engine.description.ElementDesc();
        field.setFieldName("groupAccess");
        field.setXmlName(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "groupAccess"));
        field.setXmlType(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"));
        typeDesc.addFieldDesc(field);
    };

    /**
     * Return type metadata object
     */
    public static com.ibm.ws.webservices.engine.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static com.ibm.ws.webservices.engine.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class javaType,  
           javax.xml.namespace.QName xmlType) {
        return 
          new GroupInfo_Ser(
            javaType, xmlType, typeDesc);
    };

    /**
     * Get Custom Deserializer
     */
    public static com.ibm.ws.webservices.engine.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class javaType,  
           javax.xml.namespace.QName xmlType) {
        return 
          new GroupInfo_Deser(
            javaType, xmlType, typeDesc);
    };

}
