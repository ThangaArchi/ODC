<!DOCTYPE html SYSTEM "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd">

<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="oem.edge.ed.odc.webdropbox.reports.*"%>
<%@ page import="java.util.*,java.io.*,java.text.*" %>

<%@ taglib uri="/WEB-INF/tlds/struts-html.tld"  prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld"  prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>

<jsp:include page="support/masthead.jsp" flush="true">
<jsp:param name="title" value="IBM Customer Connect DropBox - Reporting"/>
<jsp:param name="h1" value="Dropbox reports"/>
</jsp:include>

<%

 /*--------------------------------------------------------------------------*\
     @author        Joe Crichton

     @version 1.0

     Development Environment        :  x
                                       
     Name of the Application        :  DboxReportMain.jsp

     Creation/Modification History  :

        JMC          30-Nov-2004    - Created

     Overview of Application        :
     
        This JSP is used to show Dropbox Reporting top page
        
 \*--------------------------------------------------------------------------*/
    
%>
<html:errors/>

<table cellspacing="0" cellpadding="0" border="0">
<tr>
<td><html:link action="/sessionQuery"><img src="//www.ibm.com/i/v14/icons/fw.gif" width="16" height="16" alt="" border="0" /></html:link></td>
<td><html:link action="/sessionQuery" styleClass="fbox">Session Reports</html:link></td>
<td>
&nbsp;&nbsp;detailing individual dropbox sessions (client accesses)
</td>
</tr>
<tr>
<td><html:link action="/packageQuery"><img src="//www.ibm.com/i/v14/icons/fw.gif" width="16" height="16" alt="" border="0" /></html:link></td>
<td><html:link action="/packageQuery" styleClass="fbox">Package Reports</html:link></td>
<td>
&nbsp;&nbsp;detailing dropbox packages sent to and by a user
</td>
</tr>
<tr>
<td><html:link action="/fileQuery"><img src="//www.ibm.com/i/v14/icons/fw.gif" width="16" height="16" alt="" border="0" /></html:link></td>
<td><html:link action="/fileQuery" styleClass="fbox">File Reports</html:link></td>
<td>
&nbsp;&nbsp;detailing files which exist in the Dropbox
</td>
</tr>
<tr>
<td><html:link action="/fileDownloadQuery"><img src="//www.ibm.com/i/v14/icons/fw.gif" width="16" height="16" alt="" border="0" /></html:link></td>
<td><html:link action="/fileDownloadQuery" styleClass="fbox">File Download Reports</html:link></td>
<td>
&nbsp;&nbsp;detailing file downloads by a user and by others on files owned by a user
</td>
</tr>
<tr>
<td colspan="3">
   <br/>
   <br/>
   All Dropbox reports will be scoped to include only data which is accessible to the
   IBM Customer Connect ID used to generate them.
</td>
</tr>
</table>

<c:if test="${savedreports != null}">
   <br />
   <hr />
   <br />
   
   <h2>Saved Reports</h2>

   <br />
   
   Click on a report name (below) to launch that report as it was last saved.
   Alternatively,
   you can click on the <i>launch</i> link to override the date management scheme for 
   that particular invocation. The <i>edit</i> link will allow you change the
   report name as well as the saved date management scheme. 
   The <i>delete</i> link will delete the associated saved report.
   Finally, to create a new saved report, click on the <i>Save Report</i> button which 
   is displayed after a report is generated.
   
   <br /><br />
   
   <table cellspacing="0" cellpadding="0" border="0">
   <tr>
      <th>&nbsp;&nbsp;</th><th></th><th>Report Name</th>
      <th>&nbsp;&nbsp;&nbsp;</th><th>Report Type</th>
      <th>&nbsp;&nbsp;&nbsp;</th><th>Date Scheme</th>
      <th>&nbsp;&nbsp;&nbsp;</th><th>Action</th>
   </tr>
   
   <tr>
     <td>&nbsp;</td>
   </tr>
   
   <c:forEach var="report" items="${savedreports}">
      
      <tr>
        <td></td>
        <td halign="left" valign="top">
          <html:link action="/LaunchReport"
                     anchor="headers"
                     paramId="requestReportId" paramName="report"
                     paramProperty="reportId">
             <img src="//www.ibm.com/i/v14/icons/fw.gif"
                  width="16" height="16" alt="" border="0" />
          </html:link>
        </td>
        <td halign="left" valign="top">
          <html:link action="/LaunchReport"
                     anchor="headers"
                     paramId="requestReportId" paramName="report"
                     paramProperty="reportId">
             <c:out value="${report.reportName}"/><br />
          </html:link>
        </td>
        <td></td>
        <td halign="left" valign="top">
          <c:out value="${report.reportTypeLabel}"/><br />
        </td>
        <td></td>
        <td halign="left" valign="top">
           <c:set var="rep" value="${report.dateManagement}" scope="request"/>
           <bean:message key="<%=\"datemgmt.\" + request.getAttribute(\"rep\")%>"/>
           <br />
        </td>
        <td></td>
        <td halign="left" valign="top">
          <html:link action="/LaunchReportPrep"
                     paramId="requestReportId" paramName="report"
                     paramProperty="reportId">
             launch
          </html:link>
          &nbsp;|&nbsp;
          <html:link action="/UpdateReportPrep"
                     paramId="requestReportId" paramName="report"
                     paramProperty="reportId">
           edit
          </html:link>
          
          &nbsp;|&nbsp;
          
          <html:link action="/DeleteReportQ"
                     paramId="requestReportId" paramName="report"
                     paramProperty="reportId">
           delete
          </html:link>
        </td>
   </c:forEach>
   </table>

</c:if>
