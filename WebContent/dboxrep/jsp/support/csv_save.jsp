<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld"           prefix="c" %>

<table cellspacing="0" cellpadding="0" border="0">
<tr>
<td halign="center" valign="center"><html:link action="<%=\"/\"+request.getParameter(\"modifier\")+\"CSV\"%>"><img src="//www.ibm.com/i/v14/buttons/download_now_rd.gif" width="21" height="21" alt="" border="0"/></html:link></td>
<td halign="center" valign="center">&nbsp;<b><html:link action="<%=\"/\"+request.getParameter(\"modifier\")+\"CSV\"%>" styleClass="fbox">Download as CSV</html:link></b></td>

<c:if test="${savedreports != null}">
   <td halign="center" valign="center">&nbsp;&nbsp;&nbsp;
      <html:link styleClass="left-nav-overview" action="<%=\"/SaveReportPrep\"+request.getParameter(\"modifier\")%>">
         <img src="//www.ibm.com/i/v14/buttons/arrow_rd.gif" alt="Save Report As" width="21" height="21" border="0" />
      </html:link>
   </td>
   <td halign="center" valign="center">&nbsp;<b><html:link action="<%=\"/SaveReportPrep\"+request.getParameter(\"modifier\")%>" styleClass="fbox">Save report</html:link></b></td>
</c:if>

</tr>
</table>

