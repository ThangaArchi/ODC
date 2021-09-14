<!DOCTYPE html SYSTEM "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd">

<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="oem.edge.ed.odc.webdropbox.reports.*"%>
<%@ page import="java.util.*,java.io.*,java.text.*" %>

<%@ taglib uri="/WEB-INF/tlds/struts-html.tld"  prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld"  prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld"            prefix="c" %>
<jsp:include page="support/masthead.jsp" flush="true">
<jsp:param name="title" value="IBM Customer Connect DropBox - Save Report"/>
<jsp:param name="h1" value="Save Report"/>
<jsp:param name="subtitle" value="Allows saving of a Dropbox report configuration"/>
</jsp:include>

<%

 /*--------------------------------------------------------------------------*\
     @author        Joe Crichton

     @version 1.0

     Development Environment        :  x
                                       
     Name of the Application        :  DboxReportSave.jsp

     Creation/Modification History  :

        JMC          11-Aug-2005    - Created

     Overview of Application        :
     
        This JSP is used to save Dropbox Report for user
        
 \*--------------------------------------------------------------------------*/
    
%>
<html:errors/>
<html:form method="POST" action="/SaveReport">

   <html:hidden property="reportType"/>

   <jsp:include page="support/subheader_bar.jsp" flush="true">
   <jsp:param name="name" value="Report Name"/>
   </jsp:include>
   
   <br />
   <html:text   property="reportName" size="50" maxlength="128" />
   
   <br />&nbsp;<br />   
   
   Enter a descriptive name which will be used to identify the report for later recall.
   
   <jsp:include page="support/subheader_bar.jsp" flush="true">
   <jsp:param name="name" value="Date Management"/>
   </jsp:include>
   
   <br />
   
   <html:select property="dateManagement">
      <html:option value="1">  <bean:message key="datemgmt.1"/> </html:option>
      <html:option value="2">  <bean:message key="datemgmt.2"/> </html:option>
      <html:option value="3">  <bean:message key="datemgmt.3"/> </html:option>
      <html:option value="4">  <bean:message key="datemgmt.4"/> </html:option>
      <html:option value="5">  <bean:message key="datemgmt.5"/> </html:option>
      <html:option value="6">  <bean:message key="datemgmt.6"/> </html:option>
      <html:option value="7">  <bean:message key="datemgmt.7"/> </html:option>
      <html:option value="8">  <bean:message key="datemgmt.8"/> </html:option>
      <html:option value="9">  <bean:message key="datemgmt.9"/> </html:option>
      <html:option value="10"> <bean:message key="datemgmt.10"/> </html:option>
   </html:select>
   
   <br />&nbsp;<br />   
   
   Select a date management scheme to drive how any dates associated with the saved 
   report (such as the report interval) will be interpreted when the report is run.
   
   <p />
   Description of all date management schemes affect on saved report dates:
   <p />
   <p />
   
   <table cellspacing="1" cellpadding="1" border="0">
      <tr>
         <td valign="top" nowrap>&nbsp;&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.1"/></td>
         <td valign="top" nowrap>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.desc.1"/></td>
      </tr>
      <tr>
         <td valign="top" nowrap>&nbsp;&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.2"/></td>
         <td valign="top" nowrap>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.desc.2"/></td>
      </tr>
      <tr>
         <td valign="top" nowrap>&nbsp;&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.3"/></td>
         <td valign="top" nowrap>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.desc.3"/></td>
      </tr>
      <tr>
         <td valign="top" nowrap>&nbsp;&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.4"/></td>
         <td valign="top" nowrap>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.desc.4"/></td>
      </tr>
      <tr>
         <td valign="top" nowrap>&nbsp;&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.5"/></td>
         <td valign="top" nowrap>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.desc.5"/></td>
      </tr>
      <tr>
         <td valign="top" nowrap>&nbsp;&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.6"/></td>
         <td valign="top" nowrap>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.desc.6"/></td>
      </tr>
      <tr>
         <td valign="top" nowrap>&nbsp;&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.7"/></td>
         <td valign="top" nowrap>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.desc.7"/></td>
      </tr>
      <tr>
         <td valign="top" nowrap>&nbsp;&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.8"/></td>
         <td valign="top" nowrap>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.desc.8"/></td>
      </tr>
      <tr>
         <td valign="top" nowrap>&nbsp;&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.9"/></td>
         <td valign="top" nowrap>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.desc.9"/></td>
      </tr>
      <tr>
         <td valign="top" nowrap>&nbsp;&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.10"/></td>
         <td valign="top" nowrap>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
         <td valign="top"><bean:message key="datemgmt.desc.10"/></td>
      </tr>
   </table>
   
   <br />&nbsp;<br />   
   <br />&nbsp;<br />   
   
   <jsp:include page="support/submit_cancel.jsp" flush="true"/>
   
</html:form>

<br /><br />

<jsp:include page="support/footer.jsp" flush="true"/>
