<!DOCTYPE html SYSTEM "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd">

<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="oem.edge.ed.odc.webdropbox.reports.*"%>
<%@ page import="java.util.*,java.io.*,java.text.*" %>

<%@ taglib uri="/WEB-INF/tlds/struts-html.tld"  prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld"  prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<jsp:include page="support/masthead.jsp" flush="true">
<jsp:param name="title" value="IBM Customer Connect DropBox - Package Information"/>
<jsp:param name="h1" value="Package Details"/>
<jsp:param name="subtitle" value="Attribute, access and file information"/>
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
                                       
     Name of the Application        :  DboxReportPackageInfo.jsp

     Creation/Modification History  :

        JMC          30-Nov-2004    - Created

     Overview of Application        :
     
        This JSP is used to show a specific Dropbox Package for reporting purposes
        
 \*--------------------------------------------------------------------------*/
%>

<jsp:useBean type="oem.edge.ed.odc.webdropbox.reports.DboxReportPackageInfo" id="packageinfo" scope="request"/>
<%=packageinfo.isLimited()?"<br />(Limited Access)":""%>
<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Package Attributes"/>
</jsp:include>
<table cellspacing="0" cellpadding="0" border="0" width="400">
<tr>
<td>Package Name:</td>
<td><%=packageinfo.getPackageName()%></td>
</tr>
<tr>
<td>Package ID:</td>
<td><%=packageinfo.getPackageId()%></td>
</tr>
<tr>
<td>Owner:</td>
<td><%=packageinfo.getPackageOwner()%></td>
</tr>
<tr>
<td>Company:</td>
<td><%=packageinfo.getPackageCompany()%></td>
</tr>
<tr>
<td>Size:</td>
<td><%=packageinfo.getPackageSize()%></td>
</tr>
<tr>
<td>Num Files:</td>
<td><%=packageinfo.getPackageNumFiles()%></td>
</tr>                  
<tr>                  
<td>Status:</td>
<td><%=packageinfo.getPackageState()%></td>
</tr>
<tr>                  
<td>Upload Transfer Rate:</td>
<td><%=packageinfo.getPackageTransferRate()%></td>
</tr>
<tr>               
<td>Created:</td>
<td><%=packageinfo.getPackageCreated()%></td>
</tr>
<tr>
<td>Committed:</td>
<td><%=packageinfo.getPackageCommitted()%></td>
</tr>
<tr>
<td>Deleted:</td>
<td><%=packageinfo.getPackageDeleted()%></td>
</tr>
</table>
<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Package Access Information"/>
</jsp:include>
<table cellspacing="0" cellpadding="2" border="0" width="600">
<tr>
<td width="260" class="v14-header-2">Access List</td>
<td width="10">&nbsp;</td>
<td width="330" class="v14-header-2">Package Accessed By</td>
</tr>
<tr>
<td><table cellspacing="0" cellpadding="2" border="0" width="260" class="data-table-2">
<tr>
<th>Access Name</th>
<th>Access Type</th>
<th>Created</th>
</tr>
<logic:iterate 
	name="packageinfo"
	property="aclInfo"
	id="ainfo"
	type="oem.edge.ed.odc.webdropbox.reports.DboxReportAclInfo">
<tr>
<td><%=ainfo.getAclName()%></td>
<td><%=ainfo.getAclType()%></td>
<td><%=ainfo.getAclCreated()%></td>
</tr>
</logic:iterate>
</table></td>
<td>&nbsp;</td>
<td valign="top"><table cellspacing="0" cellpadding="2" border="0" width="330" class="data-table-2">
<tr>
<th>User name</th>
<th>Download Status</th>
<th>Transfer Rate</th>
<th>Last Download</th>
</tr>                              
<logic:iterate 
	name="packageinfo"
	property="accessInfo"
	id="ainfo"
	type="oem.edge.ed.odc.webdropbox.reports.DboxReportPackageAccess">
<tr>
<td><%=ainfo.getAccessName()%></td>
<td><%=ainfo.getAccessState()%></td>
<td style="text-align:right;"><%=ainfo.getAccessTransferRate()%></td>
<td><%=ainfo.getAccessLast()%></td>
</tr>
</logic:iterate>
</table>
</tr></table>
<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Files"/>
</jsp:include>
<table border="0" cellpadding="2" cellspacing="0" width="600" class="data-table-2">
<tr>
<th>Details</th>
<th>File name</th>
<th>Size</th>
<th>Status</th>
<th>Transfer rate</th>
<th>Created</th>
<th>Deleted</th>
<th>MD5</th>
</tr>
<logic:iterate name="packageinfo" property="files" id="finfo" type="oem.edge.ed.odc.webdropbox.reports.DboxReportFileInfo">                             
<tr valign="top">
<td style="padding-top:4px;padding-bottom:4px;text-align:center"><html:link action="/FileInfo" paramId="<%=FileForm.FILEID%>" paramName="finfo" paramProperty="fileId"><img src="//www.ibm.com/i/v14/buttons/view_rd.gif" width="21" height="21" alt="" border="0" /></html:link></td>
<td><%=finfo.getFileName()%></td>
<td style="text-align:right;"><%=finfo.getFileSize()%></td>
<td><%=finfo.getFileState()%></td>
<td style="text-align:right;"><%=finfo.getFileTransferRate()%></td>
<td><%=finfo.getFileCreated()%></td>
<td><%=finfo.getFileDeleted()%></td>
<td><%=finfo.getFileMD5()%></td>
</tr>
</logic:iterate>
</table>
<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="File Access Status"/>
</jsp:include>
<table border="0" cellpadding="2" cellspacing="0" width="600" class="data-table-2">
<tr>
<th>File name</th>
<th>Accessed By</th>
<th>Status</th>
<th>Transfer Rate</th>
<th>When Accessed</th>
</tr>
<logic:iterate name="packageinfo" property="files" id="finfo" type="oem.edge.ed.odc.webdropbox.reports.DboxReportFileInfo">                             
<logic:iterate name="finfo" property="access" id="fainfo" type="oem.edge.ed.odc.webdropbox.reports.DboxReportFileAccess">
<tr>
<td><%=fainfo.getAccessFileName()%></td>
<td><%=fainfo.getAccessName()%></td>
<td><%=fainfo.getAccessState()%></td>
<td style="text-align:right;"><%=fainfo.getAccessTransferRate()%></td>
<td><%=fainfo.getAccessCreated()%></td>
</tr>
</logic:iterate>
</logic:iterate>
</table>   
<br /><br />

<jsp:include page="support/footer.jsp" flush="true"/>
