<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<table cellspacing="2" cellpadding="2" border="0">
<tr>
<td><strong>Start date:</strong></td>
<td><jsp:include page="date_picker.jsp" flush="true">
<jsp:param name="prefix" value="start"/>
</jsp:include></td>
<td><strong>End date:</strong></td>
<td><jsp:include page="date_picker.jsp" flush="true">
<jsp:param name="prefix" value="end"/>
</jsp:include></td>
</tr>
</table>
