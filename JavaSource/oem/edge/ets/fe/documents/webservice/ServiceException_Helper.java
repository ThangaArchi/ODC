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
public class ServiceException_Helper {

	// Type metadata
	private static com.ibm.ws.webservices.engine.description.TypeDesc typeDesc =
		new com.ibm.ws.webservices.engine.description.TypeDesc(
			ServiceException.class);

	static {
		com.ibm.ws.webservices.engine.description.FieldDesc field =
			new com.ibm.ws.webservices.engine.description.ElementDesc();
		field.setFieldName("message");
		field.setXmlName(
			com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
				"http://webservice.documents.fe.ets.edge.oem",
				"message"));
		field.setXmlType(
			com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
				"http://www.w3.org/2001/XMLSchema",
				"string"));
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
		return new ServiceException_Ser(javaType, xmlType, typeDesc);
	};

	/**
	 * Get Custom Deserializer
	 */
	public static com.ibm.ws.webservices.engine.encoding.Deserializer getDeserializer(
			java.lang.String mechType,
			java.lang.Class javaType,
			javax.xml.namespace.QName xmlType) {
		return new ServiceException_Deser(javaType, xmlType, typeDesc);
	};

	/**
	 * @return
	 */
	public static java.lang.Object createProxy() {
		return 
			new oem.edge.ets.fe.documents.webservice.ServiceException_DeserProxy();
	}
}
