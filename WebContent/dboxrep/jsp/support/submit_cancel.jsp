<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld"           prefix="c" %>

<% /*
  This is what we wanted ... but its being URLRewritten, and www does not
  like that ... turns it into the universal 'c'

<html:image src="//www.ibm.com/i/v14/buttons/submit.gif" alt="Submit" border="0"/>

   also, no cancel ... move to back button
         <html:link styleClass="left-nav-overview" action="/mainPage"><img src="//www.ibm.com/i/v14/buttons/cancel.gif" alt="cancel" width="120" height="21" border="0" /></html:link>
   */ 
%>

  
<table cellspacing="0" cellpadding="0" border="0">  
   <tr>
      <td halign="center">
         <input type="image" name="" src="//www.ibm.com/i/v14/buttons/submit.gif" border="0" alt="Submit"> 
      </td>
      <td halign="center" valign="center">
         <a href="javascript:history.back()">
            <img src="//www.ibm.com/i/v14/buttons/arrow_lt.gif" alt="back" width="21" height="21" border="0" />
         </a>
      </td>
      <td halign="center" valign="center" width="100">
         <b>
           <a class="fbox" href="javascript:history.back()" alt="back">
              &nbsp;Back
           </a>
         </b>
      </td>
      
     <c:if test="${not empty param.resetAction}">
      <td halign="center" valign="center">
         <html:link styleClass="left-nav-overview" action="<%=\"/\"+request.getParameter(\"resetAction\")+\"Reset\"%>"><img src="//www.ibm.com/i/v14/buttons/arrow_rd.gif" alt="Reset to default" width="21" height="21" border="0" />
         </html:link>
      </td>
      <td halign="center" valign="center" width="100">
         <b>
            <html:link styleClass="fbox" action="<%=\"/\"+request.getParameter(\"resetAction\")+\"Reset\"%>">
              &nbsp;Reset
            </html:link>
         </b>
      </td>
    </c:if>
   </tr>
</table>
  
