/**
 * PackageInfo_Ser.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * o0526.04 v62905175048
 */

package oem.edge.ed.odc.dropbox.common;

public class PackageInfo_Ser extends com.ibm.ws.webservices.engine.encoding.ser.BeanSerializer {
    /**
     * Constructor
     */
    public PackageInfo_Ser(
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType, 
           com.ibm.ws.webservices.engine.description.TypeDesc _typeDesc) {
        super(_javaType, _xmlType, _typeDesc);
    }
    public void serialize(
        javax.xml.namespace.QName name,
        org.xml.sax.Attributes attributes,
        java.lang.Object value,
        com.ibm.ws.webservices.engine.encoding.SerializationContext context)
        throws java.io.IOException
    {
        context.startElement(name, addAttributes(attributes, value, context));
        addElements(value, context);
        context.endElement();
    }
    protected org.xml.sax.Attributes addAttributes(
        org.xml.sax.Attributes attributes,
        java.lang.Object value,
        com.ibm.ws.webservices.engine.encoding.SerializationContext context)
        throws java.io.IOException
    {
        return attributes;
    }
    protected void addElements(
        java.lang.Object value,
        com.ibm.ws.webservices.engine.encoding.SerializationContext context)
        throws java.io.IOException
    {
        PackageInfo bean = (PackageInfo) value;
        java.lang.Object propValue;
        javax.xml.namespace.QName propQName;
        {
          propQName = QName_0_30;
          propValue = new java.lang.Integer(bean.getPackageNumElements());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_3,
              true,null,context);
          propQName = QName_0_31;
          propValue = new java.lang.Byte(bean.getPackageStatus());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_16,
              true,null,context);
          propQName = QName_0_32;
          propValue = bean.getPackageDescription();
          if (propValue != null && !context.shouldSendXSIType()) {
            context.simpleElement(propQName, null, propValue.toString()); 
          } else {
            serializeChild(propQName, null, 
              propValue, 
              QName_1_14,
              true,null,context);
          }
          propQName = QName_0_33;
          propValue = new java.lang.Long(bean.getPackageExpiration());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_15,
              true,null,context);
          propQName = QName_0_34;
          propValue = new java.lang.Long(bean.getPackageCreation());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_15,
              true,null,context);
          propQName = QName_0_35;
          propValue = new java.lang.Long(bean.getPackageCommitted());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_15,
              true,null,context);
          propQName = QName_0_36;
          propValue = new java.lang.Long(bean.getPackageSize());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_15,
              true,null,context);
          propQName = QName_0_37;
          propValue = bean.getPackageName();
          if (propValue != null && !context.shouldSendXSIType()) {
            context.simpleElement(propQName, null, propValue.toString()); 
          } else {
            serializeChild(propQName, null, 
              propValue, 
              QName_1_14,
              true,null,context);
          }
          propQName = QName_0_38;
          propValue = new java.lang.Long(bean.getPackageId());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_15,
              true,null,context);
          propQName = QName_0_39;
          propValue = bean.getPackageOwner();
          if (propValue != null && !context.shouldSendXSIType()) {
            context.simpleElement(propQName, null, propValue.toString()); 
          } else {
            serializeChild(propQName, null, 
              propValue, 
              QName_1_14,
              true,null,context);
          }
          propQName = QName_0_40;
          propValue = bean.getPackageCompany();
          if (propValue != null && !context.shouldSendXSIType()) {
            context.simpleElement(propQName, null, propValue.toString()); 
          } else {
            serializeChild(propQName, null, 
              propValue, 
              QName_1_14,
              true,null,context);
          }
          propQName = QName_0_41;
          propValue = new java.lang.Long(bean.getPackagePoolId());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_15,
              true,null,context);
          propQName = QName_0_42;
          propValue = new java.lang.Boolean(bean.getPackageMarked());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_17,
              true,null,context);
          propQName = QName_0_43;
          propValue = new java.lang.Boolean(bean.getPackageCompleted());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_17,
              true,null,context);
          propQName = QName_0_44;
          propValue = new java.lang.Boolean(bean.getPackageHidden());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_17,
              true,null,context);
          propQName = QName_0_45;
          propValue = new java.lang.Boolean(bean.getPackageReturnReceipt());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_17,
              true,null,context);
          propQName = QName_0_46;
          propValue = new java.lang.Boolean(bean.getPackageSendNotification());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_17,
              true,null,context);
          propQName = QName_0_47;
          propValue = new java.lang.Byte(bean.getPackageFlags());
          serializeChild(propQName, null, 
              propValue, 
              QName_1_16,
              true,null,context);
        }
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
    private final static javax.xml.namespace.QName QName_1_15 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "http://www.w3.org/2001/XMLSchema",
                  "long");
    private final static javax.xml.namespace.QName QName_1_16 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "http://www.w3.org/2001/XMLSchema",
                  "byte");
    private final static javax.xml.namespace.QName QName_0_35 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageCommitted");
    private final static javax.xml.namespace.QName QName_0_43 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "",
                  "packageCompleted");
    private final static javax.xml.namespace.QName QName_1_3 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "http://www.w3.org/2001/XMLSchema",
                  "int");
    private final static javax.xml.namespace.QName QName_1_14 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "http://www.w3.org/2001/XMLSchema",
                  "string");
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
    private final static javax.xml.namespace.QName QName_1_17 = 
           com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                  "http://www.w3.org/2001/XMLSchema",
                  "boolean");
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
