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

package oem.edge.ets.fe.documents.webservice;

/**
 * @author v2srikau
 */
public class Category_Ser
	extends com.ibm.ws.webservices.engine.encoding.ser.BeanSerializer {

	public final static javax.xml.namespace.QName QName_2_2 =
		com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
			"http://webservice.documents.fe.ets.edge.oem",
			"parentId");

	public final static javax.xml.namespace.QName QName_1_4 =
		com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
			"http://www.w3.org/2001/XMLSchema",
			"int");

	public final static javax.xml.namespace.QName QName_2_0 =
		com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
			"http://webservice.documents.fe.ets.edge.oem",
			"cat");

	public final static javax.xml.namespace.QName QName_2_1 =
		com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
			"http://webservice.documents.fe.ets.edge.oem",
			"catId");

	public final static javax.xml.namespace.QName QName_1_3 =
		com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
			"http://www.w3.org/2001/XMLSchema",
			"string");

	/**
	 * Constructor
	 */
	public Category_Ser(
		java.lang.Class _javaType,
		javax.xml.namespace.QName _xmlType,
		com.ibm.ws.webservices.engine.description.TypeDesc _typeDesc) {
		super(_javaType, _xmlType, _typeDesc);
	}

	/**
	 * @see com.ibm.ws.webservices.engine.encoding.Serializer#serialize(
	 * javax.xml.namespace.QName, 
	 * org.xml.sax.Attributes, 
	 * java.lang.Object, 
	 * com.ibm.ws.webservices.engine.encoding.SerializationContext)
	 */
	public void serialize(
		javax.xml.namespace.QName name,
		org.xml.sax.Attributes attributes,
		java.lang.Object value,
		com.ibm.ws.webservices.engine.encoding.SerializationContext context)
		throws java.io.IOException {
		context.startElement(name, addAttributes(attributes, value, context));
		addElements(value, context);
		context.endElement();
	}
	
	/**
	 * @param attributes
	 * @param value
	 * @param context
	 * @return
	 * @throws java.io.IOException
	 */
	protected org.xml.sax.Attributes addAttributes(
		org.xml.sax.Attributes attributes,
		java.lang.Object value,
		com.ibm.ws.webservices.engine.encoding.SerializationContext context)
		throws java.io.IOException {
		return attributes;
	}
	
	/**
	 * @param value
	 * @param context
	 * @throws java.io.IOException
	 */
	protected void addElements(
		java.lang.Object value,
		com.ibm.ws.webservices.engine.encoding.SerializationContext context)
		throws java.io.IOException {
		Category bean = (Category) value;
		java.lang.Object propValue;
		javax.xml.namespace.QName propQName;
		{
			propQName = QName_2_0;
			propValue = bean.getCat();
			if (propValue != null && !context.shouldSendXSIType()) {
				context.simpleElement(propQName, null, propValue.toString());
			} else {
				context.serialize(
					propQName,
					null,
					propValue,
					QName_1_3,
					true,
					null);
			}
			propQName = QName_2_1;
			propValue = new Integer(bean.getCatId());
			context.serialize(
				propQName,
				null,
				propValue,
				QName_1_4,
				true,
				null);
			propQName = QName_2_2;
			propValue = new Integer(bean.getParentId());
			context.serialize(
				propQName,
				null,
				propValue,
				QName_1_4,
				true,
				null);
		}
	}
}
