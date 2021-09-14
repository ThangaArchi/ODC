<!DOCTYPE html SYSTEM "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd">

<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="oem.edge.ed.odc.webdropbox.reports.*"%>
<%@ page import="java.util.*,java.io.*,java.text.*" %>

<%@ taglib uri="/WEB-INF/tlds/struts-html.tld"  prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld"  prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<jsp:include page="support/masthead.jsp" flush="true">
<jsp:param name="title" value="IBM Customer Connect DropBox - File Information"/>
<jsp:param name="h1" value="File Details"/>
<jsp:param name="subtitle" value="Attribute and package information"/>
</jsp:include>
<br /><b style="color:#ff0000">IBM Confidential</b>

<br />
<br />
<table cellspacing="0" cellpadding="0" border="0">  
   <tr>
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
   </tr>
</table>

<%!

 /*--------------------------------------------------------------------------*\
     @author        Joe Crichton

     @version 1.0

     Development Environment        :  x
                                       
     Name of the Application        :  DboxReportFileInfo.jsp

     Creation/Modification History  :

        JMC          30-Nov-2004    - Created

     Overview of Application        :
     
        This JSP is used to show a specific Dropbox File for reporting purposes
        
 \*--------------------------------------------------------------------------*/
%>

<jsp:useBean id="fileinfo" type="oem.edge.ed.odc.webdropbox.reports.DboxReportFileInfo" scope="request"/>
<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="File Attributes"/>
</jsp:include>
<table cellspacing="0" cellpadding="0" border="0" width="400">
<tr>
<td><strong>File Name:</strong></td>
<td><%=fileinfo.getFileName()%></td>
</tr>
<tr>
<td><strong>File ID:</strong></td>
<td><%=fileinfo.getFileId()%></td>
</tr>
<tr>
<td><strong>Size:</strong></td>
<td><%=fileinfo.getFileSize()%></td>
</tr>
<tr>
<td><strong>Num Components:</strong></td>
<td><%=fileinfo.getFileNumComponents()%></td>
</tr>
<tr>
<td><strong>Status:</strong></td>
<td><%=fileinfo.getFileState()%></td>
</tr>
<tr>
<td><strong>Created:</strong></td>
<td><%=fileinfo.getFileCreated()%></td>
</tr>
<tr>
<td><strong>Deleted:</strong></td>
<td><%=fileinfo.getFileDeleted()%></td>
</tr>
</table>
<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Referencing Packages"/>
</jsp:include>
<table cellspacing="0" cellpadding="0" border="0" width="600" class="data-table-2">
<tr>
<th>Details</th>
<th>Package Id</th>
<th>Package Name</th>
<th>Owner</th>
<th>Company</th>
</tr>
<logic:iterate name="fileinfo" property="packages" id="pinfo" type="oem.edge.ed.odc.webdropbox.reports.DboxReportPackageInfo">                             
<tr>
<td style="padding-top:4px;padding-bottom:4px;text-align:center"><html:link action="/PackageInfo" paramId="<%=PackageForm.PKGID%>" paramName="pinfo" paramProperty="packageId"><img src="//www.ibm.com/i/v14/buttons/view_rd.gif" width="21" height="21" alt="" border="0" /></html:link></td>
<td><%=pinfo.getPackageId()%></td>
<td><%=pinfo.getPackageName()%></td>
<td><%=pinfo.getPackageOwner()%></td>
<td><%=pinfo.getPackageCompany()%></td>
</tr>
</logic:iterate>
</table><br /><br />
<jsp:include page="support/footer.jsp" flush="true"/>
 
