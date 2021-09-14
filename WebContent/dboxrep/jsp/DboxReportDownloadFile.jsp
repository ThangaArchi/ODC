<!DOCTYPE html SYSTEM "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd">

<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="oem.edge.ed.odc.webdropbox.reports.*"%>
<%@ page import="java.util.*,java.io.*,java.text.*" %>

<%@ taglib uri="/WEB-INF/tlds/struts-html.tld"  prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/odctags.tld"      prefix="odc" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld"  prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<jsp:include page="support/masthead.jsp" flush="true">
<jsp:param name="title" value="IBM Customer Connect DropBox - File Downloads Report"/>
<jsp:param name="h1" value="File Downloads Reporting"/>
<jsp:param name="subtitle" value="Report for File Downloads"/>
</jsp:include>
<br /><b style="color:#ff0000">IBM Confidential</b>

<%!

 /*--------------------------------------------------------------------------*\
     @author        Joe Crichton

     @version 1.0

     Development Environment        :  x
                                       
     Name of the Application        :  DboxReportDownloadFile.jsp

     Creation/Modification History  :

        JMC          30-Nov-2004    - Created

     Overview of Application        :
     
        This JSP is used to show Dropbox Reporting Download File page
        
 \*--------------------------------------------------------------------------*/
%>

<html:errors/>

<odc:form method="post" action="/FileDownloadReport" anchor="headers">
<html:hidden property="sortField"/>
<html:hidden property="sortDirection"/>
<br /><br />

<jsp:include page="support/submit_cancel.jsp" flush="true">
<jsp:param name="resetAction" value="DownloadFile"/>
</jsp:include>

<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Report Interval"/>
</jsp:include>
<jsp:include page="support/interval_controls.jsp" flush="true">
</jsp:include>

The <i>Report Interval</i> is a mandatory date range filter which will help scope
which data is included in the report. All records which were considered to be
active during the specified interval will be included.  For a File Download Report, an
active record equates to file which was downloaded during the interval.

<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Report Type"/>
</jsp:include>
<html:radio property="recordLevel" value="false"/><strong>Summary</strong>&nbsp;&nbsp;
<html:radio property="recordLevel" value="true"/><strong>Record Level</strong>
<br />&nbsp;<br />
The report type determines whether a resultant report output will include any selected
<i>Summary</i> or <i>Record</i> oriented attributes. The report output will
 always contain the attributes selected from the <i>Main</i> section.
<br />&nbsp;<br />
In addition, when a report type of Summary is selected, like output is 
grouped together for all selected attributes, thus providing a summary view. When 
Record Level is selected, no such grouping is done, and each row of data displayed
is at the finest level of granularity. 

<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Main Attributes"/>
</jsp:include>
<script type="text/javascript" language="javascript" src="<html:rewrite page="/js/selection_update.js"/>">
</script>
<input type="checkbox" id="selectAll" onclick="selectAllBoxes(this,'col',8);"/><strong>Select all columns</strong><br /><br />
<table cellspacing="0" cellpadding="0" border="0" width="600">
<tr>
<td><html:checkbox property="user" styleId="col0" onclick="updateSelect('selectAll','col',8);"/><strong>User</strong></td>
<td><html:checkbox property="company" styleId="col1" onclick="updateSelect('selectAll','col',8);"/><strong>Company</strong></td>
<td><html:checkbox property="ownerUser" styleId="col2" onclick="updateSelect('selectAll','col',8);"/><strong>Owning User</strong></td>
<td><html:checkbox property="ownerCompany" styleId="col3" onclick="updateSelect('selectAll','col',8);"/><strong>Owning Company</strong></td>
</tr>
<tr>
<td><html:checkbox property="packageName" styleId="col4" onclick="updateSelect('selectAll','col',8);"/><strong>Package Name</strong></td>
<td><html:checkbox property="fileState" styleId="col5" onclick="updateSelect('selectAll','col',8);"/><strong>Download State</strong></td>
<td><html:checkbox property="fileSize" styleId="col6" onclick="updateSelect('selectAll','col',8);"/><strong>File Size</strong></td>
<td><html:checkbox property="numComponents" styleId="col7" onclick="updateSelect('selectAll','col',8);"/><strong>Num Components</strong></td>
</tr>
</table>

&nbsp;<br />&nbsp;<br />
<strong>Filters:</strong><br />
You can filter the report by selecting the following filters and specifying values for them.<br />
Results that match the specified values will be included in the report. The filters
selected from this section apply to both Summary and Record level reports.
&nbsp;<br />&nbsp;<br />

<table cellspacing="0" cellpadding="0" border="0" width="550">
<tr>
<jsp:include page="support/simple_filter.jsp" flush="true">
<jsp:param name="property" value="userFilter"/>
<jsp:param name="label" value="User"/>
<jsp:param name="colspan" value="2"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/simple_filter.jsp" flush="true">
<jsp:param name="property" value="companyFilter"/>
<jsp:param name="label" value="Company"/>
<jsp:param name="colspan" value="2"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/simple_filter.jsp" flush="true">
<jsp:param name="property" value="ownerUserFilter"/>
<jsp:param name="label" value="Owning User"/>
<jsp:param name="colspan" value="2"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/simple_filter.jsp" flush="true">
<jsp:param name="property" value="ownerCompanyFilter"/>
<jsp:param name="label" value="Owning Company"/>
<jsp:param name="colspan" value="2"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/simple_filter.jsp" flush="true">
<jsp:param name="property" value="packageNameFilter"/>
<jsp:param name="label" value="Package Name"/>
<jsp:param name="colspan" value="2"/>
</jsp:include>
</tr>
<tr>
<td nowrap><html:checkbox property="fileStateFilter"/><strong>Download State</strong></td>
<td colspan="2"><html:radio property="fileStateFilterModifier" value="Failed"/><strong>Failed</strong>
<html:radio property="fileStateFilterModifier" value="Completed"/><strong>Completed</strong></td>
</tr>
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="fileSizeFilter"/>
<jsp:param name="label" value="File Size"/>
<jsp:param name="units" value="KB"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="numComponentsFilter"/>
<jsp:param name="label" value="Num Components"/>
<jsp:param name="units" value=""/>
</jsp:include>
</tr>

<tr><td>&nbsp;</td></tr>
<tr>
<jsp:include page="support/simple_filter.jsp" flush="true">
<jsp:param name="property" value="fileNameFilter"/>
<jsp:param name="label" value="File Name"/>
<jsp:param name="colspan" value="2"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="xferRateFilter"/>
<jsp:param name="label" value="Transfer Rate"/>
<jsp:param name="units" value="KB/sec"/>
</jsp:include>
</tr>
<jsp:include page="support/complex_date_filter.jsp" flush="true">
<jsp:param name="label" value="Date Created"/>
<jsp:param name="property" value="createTimeFilter"/>
<jsp:param name="datePrefix" value="createFilter"/>
</jsp:include>
<jsp:include page="support/complex_date_filter.jsp" flush="true">
<jsp:param name="label" value="Date Deleted"/>
<jsp:param name="property" value="deleteTimeFilter"/>
<jsp:param name="datePrefix" value="deleteFilter"/>
</jsp:include>

<tr>
<td></td>
<td colspan=2>
<br/>
<i>
Selecting the arrow 
(<img src="//www.ibm.com/i/v14/icons/dn.gif" alt="" border="0" />) 
next to each date filter will import the associated report interval
value from the top of the page
</i>

</td>
</tr>


</table>
<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Summary Attributes"/>
</jsp:include>
<table cellspacing="0" cellpadding="0" border="0" width="600">
<tr>
<td><html:checkbox property="avgXferRate"/><strong>Avg Transfer Rate</strong></td>
<td><html:checkbox property="numFiles"/><strong>Number of Files</strong></td>
<td><html:checkbox property="avgNumComponents"/><strong>Avg Number of Components</strong></td>
<td><html:checkbox property="avgFileSize"/><strong>Avg File Size</strong></td>
</tr>
</table>

&nbsp;<br />
<strong>Filters:</strong><br />
You can filter the report by selecting the following filters and specifying values for them.<br />
Results that match the specified values will be included in the report. The filters
selected from this section apply ONLY to Summary reports.
<br />&nbsp;<br />

<table cellspacing="0" cellpadding="0" border="0" width="400">
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="avgXferRateFilter"/>
<jsp:param name="label" value="Avg Transfer Rate"/>
<jsp:param name="units" value="KB/sec"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="numFilesFilter"/>
<jsp:param name="label" value="Number of Files"/>
<jsp:param name="units" value=""/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="avgNumComponentsFilter"/>
<jsp:param name="label" value="Avg Number of Components"/>
<jsp:param name="units" value=""/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="avgFileSizeFilter"/>
<jsp:param name="label" value="Avg File Size"/>
<jsp:param name="units" value="KB"/>
</jsp:include>
</tr>
</table>
<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Record Level Attributes"/>
</jsp:include>
<table cellspacing="0" cellpadding="0" border="0" width="600">
<tr>
<td><html:checkbox property="fileName"/><strong>File Name</strong></td>
<td><html:checkbox property="xferRate"/><strong>Transfer Rate</strong></td>
<td><html:checkbox property="downloadTime"/><strong>Download Time</strong></td>
<td><html:checkbox property="createTime"/><strong>Creation Time</strong></td>
</tr>
<tr>
<td><html:checkbox property="deleteTime"/><strong>Deletion Time</strong></td>
</tr>
</table>
<br />

<a name="headers" />

<jsp:include page="support/submit_cancel.jsp" flush="true">
<jsp:param name="resetAction" value="DownloadFile"/>
</jsp:include>

</odc:form>
<br /><br />
<img alt="" height="1" src="//www.ibm.com/i/v14/rules/blue_rule.gif" width="600"/><br /><br />
<bean:define id="cursortfield" name="DownloadFileForm" property="sortField"/>
<bean:define id="cursortdir"   name="DownloadFileForm" property="sortDirection"/>

<%
   Results results = (Results)request.getAttribute("results");
   if (results != null) {
  
      if (results != null) {
%>        

<jsp:include page="support/csv_save.jsp" flush="true">
<jsp:param name="modifier" value="DownloadFile"/>
</jsp:include>

<br /><br />
<table cellspacing="1" cellpadding="2" border="0">
         
<%      
                
        // Print column names
        java.util.HashMap sortparms = new java.util.HashMap();
        pageContext.setAttribute("sortparms", sortparms);
        
       /* If Dive Down is possible, add a col for it */
        if (results.getHeader(FileForm.FILEID) != null) {
%>        
           <th style="background-color:#b5b5b5"><strong>Details</strong></th>
<%
        }
        
        Enumeration enum = results.getOrderedHeaders();
        while(enum.hasMoreElements()) {
           String fn = (String)enum.nextElement();
           String header = results.getHeader(fn);
           
           String sd = "ASC";
           String img= "edesign_updown.gif";
           sortparms.put("sortField", fn);
           if (((String)cursortfield).equals(fn)) {
               if (((String)cursortdir).equalsIgnoreCase(sd)) {
                   sd = "DESC";
                   img = "edesign_up.gif";
               }
               else img="edesign_down.gif";
           }
           sortparms.put("sortDirection", sd);
%>           
<th style="background-color:#b5b5b5"><table cellspacing="0" cellpadding="0" border="0"><tr>
<td><html:link action="/FileDownloadSort" anchor="headers" name="sortparms"><html:img page="<%=\"/images/\"+img%>" border="0"/></html:link></td>
<td><html:link action="/FileDownloadSort" anchor="headers" name="sortparms" style="color:#000000"><%=header%></html:link></td>
</tr></table></th>
<%           
        }
        out.println("</tr>");
        
        java.util.HashMap fieldparms = new java.util.HashMap();
        pageContext.setAttribute("fieldparms", fieldparms);
        
        int resultCount=0;
        while(results.hasMoreElements()) {
           results.nextRow();
           %>
           <tr<%=((resultCount++)%2==0)?"":" style=\"background-color:#eeeeee\""%>>
           <%
           
          /* If a dive down link is needed, do it. Note, this should be an image */
           String fileid = results.getValue(FileForm.FILEID);
           if (fileid != null) {
              java.util.HashMap packinfoparms = new java.util.HashMap();
              packinfoparms.put(FileForm.FILEID, fileid);
              
              pageContext.setAttribute("packinfoparms", packinfoparms);
           
%>           
<td style="padding-top:4px;padding-bottom:4px;text-align:center"><html:link action="/FileInfo" name="packinfoparms"><img src="//www.ibm.com/i/v14/buttons/view_rd.gif" width="21" height="21" alt="" border="0" /></html:link></td>
<%
           }   
           
           enum = results.getOrderedHeaders();
           while(enum.hasMoreElements()) {
              String fn = (String)enum.nextElement();
              String fv = (String)results.getValue(fn);
              String align = results.getHeaderAlignment(fn);
              
              if (DownloadFileForm.fieldHasSimpleFilter(fn)) {
                 fieldparms.put("modifyFieldName",fn);
                 fieldparms.put("modifyFieldValue",fv);
%>           
                 <td style="padding-top:4px;padding-bottom:4px">
                    <html:link action="/FileDownloadModifyField" anchor="headers" name="fieldparms">
                       <%=fv%>
                    </html:link>
                 </td>
<%           
              } else {
%>              
                <td style="padding-top:4px;padding-bottom:4px;text-align:<%=align%>"><%=fv%></td>
<%
              }
           }
           out.println("</tr>");
        }       
           
        out.println("</table><br /><br />");
      } 
   }    
%>

<jsp:include page="support/footer.jsp" flush="true"/>
