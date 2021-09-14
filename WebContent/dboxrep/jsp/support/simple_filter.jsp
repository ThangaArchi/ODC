<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<td nowrap><html:checkbox property="<%=request.getParameter(\"property\")%>"/><strong><%=request.getParameter("label")%></strong></td>
<td<%=request.getParameter("colspan")==null?"":" colspan=\""+request.getParameter("colspan")+"\""%>><html:text property="<%=request.getParameter(\"property\")+\"Value\"%>" size="30" styleClass="iform"/></td>
