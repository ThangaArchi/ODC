<!DOCTYPE html SYSTEM "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd">

<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="oem.edge.ed.odc.webdropbox.reports.*"%>
<%@ page import="java.util.*,java.io.*,java.text.*" %>

<%@ taglib uri="/WEB-INF/tlds/struts-html.tld"  prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld"  prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld"            prefix="c" %>
<jsp:include page="support/masthead.jsp" flush="true">
<jsp:param name="title" value="IBM Customer Connect DropBox - Delete Report"/>
<jsp:param name="h1" value="Delete Report"/>
<jsp:param name="subtitle" value="Allows deletion of a saved report configuration"/>
</jsp:include>

<%

 /*--------------------------------------------------------------------------*\
     @author        Joe Crichton

     @version 1.0

     Development Environment        :  x
                                       
     Name of the Application        :  DboxReportDelete.jsp

     Creation/Modification History  :

        JMC          22-Aug-2005    - Created

     Overview of Application        :
     
        This JSP is used to delete a Dropbox Report for user
        
 \*--------------------------------------------------------------------------*/
    
%>
<html:errors/>
<html:form method="POST" action="/DeleteReport">

   <html:hidden property="reportType"/>
   <html:hidden property="reportId"/>
   <html:hidden property="reportName"/>

   <br />
   <br />
   
   Are you sure you want to delete the following saved Dropbox report 
   configuration?  
   
   <br />
   <br />

   <table cellspacing="3" cellpadding="3" border="1">
     <tr>
        <th>Report Name</th> 
        <th>Report Type</th>
     </tr>
     <tr>
        <td>
           <c:out value="${SavedReportForm.reportName}"/>
        </td>           
        <td>
           <c:out value="${SavedReportForm.reportTypeLabel}"/>
        </td>
     </tr>
   </table>
     
   <br />&nbsp;<br />   
   
   Select <i>Submit</i> to continue with deletion process, or <i>Back</i> to cancel out.
   
   
   <br />&nbsp;<br />   
   
   <jsp:include page="support/submit_cancel.jsp" flush="true"/>
   
</html:form>

<br /><br />

<jsp:include page="support/footer.jsp" flush="true"/>
