<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<tr valign="top">
   <td nowrap>
      <html:checkbox property="<%=request.getParameter(\"property\")%>"/><strong><%=request.getParameter("label")%></strong>
   </td>
  
   <td colspan="2">
   
     <html:hidden property="<%=request.getParameter(\"property\")+\"Modifier\"%>"/>

     <table cellspacing="0" cellpadding="2">
       <tr>
         <td><strong>From:</strong></td>
         <td nowrap>
           <jsp:include page="date_picker.jsp" flush="true">
             <jsp:param name="prefix" value="<%=request.getParameter(\"datePrefix\")+\"Start\"%>"/>
           </jsp:include>
         </td>
         <td>&nbsp;</td>
         <td><Strong>To:<strong></td>
         <td nowrap>
           <jsp:include page="date_picker.jsp" flush="true">
             <jsp:param name="prefix" value="<%=request.getParameter(\"datePrefix\")+\"End\"%>"/>
           </jsp:include>
        </td>
     </table>  
   </td>
</tr>
<tr>
  <td colspan="3"><img src="//www.ibm.com/i/c.gif" width="1" height="4" alt="" /></td>
</tr>
