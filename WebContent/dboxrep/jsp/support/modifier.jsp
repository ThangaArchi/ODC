<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<html:select property="<%=request.getParameter(\"property\")+\"Modifier\"%>" styleClass="iform">
<html:option value="le">&le;</html:option>
<html:option value="ge">&ge;</html:option>
<logic:equal parameter="between" value="true">
<html:option value="between">between</html:option>

</logic:equal>
</html:select>