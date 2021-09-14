<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<td nowrap><html:checkbox property="<%=request.getParameter(\"property\")%>"/><strong><%=request.getParameter("label")%></strong></td>
<td>
<jsp:include page="modifier.jsp" flush="true"/>
</td>
<td><html:text property="<%=request.getParameter(\"property\")+\"Value\"%>" size="10" styleClass="iform"/>&nbsp;<strong><%=request.getParameter("units")%></strong></td>
