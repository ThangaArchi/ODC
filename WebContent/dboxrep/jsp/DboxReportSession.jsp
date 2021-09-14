<!DOCTYPE html SYSTEM "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd">

<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="oem.edge.ed.odc.webdropbox.reports.*"%>
<%@ page import="java.util.*,java.io.*,java.text.*" %>

<%@ taglib uri="/WEB-INF/tlds/struts-html.tld"  prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/odctags.tld"      prefix="odc" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld"  prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<jsp:include page="support/masthead.jsp" flush="true">
<jsp:param name="title" value="IBM Customer Connect DropBox - Session Report"/>
<jsp:param name="h1" value="Session reports"/>
<jsp:param name="subtitle" value="Report for Dropbox Sessions"/>
</jsp:include>
<br /><b style="color:#ff0000">IBM Confidential</b>
<%!

 /*--------------------------------------------------------------------------*\
     @author        Joe Crichton

     @version 1.0

     Development Environment        :  x
                                       
     Name of the Application        :  DboxReportSession.jsp

     Creation/Modification History  :

        JMC          30-Nov-2004    - Created

     Overview of Application        :
     
        This JSP is used to show Dropbox Reporting Session page
        
 \*--------------------------------------------------------------------------*/
%>


<html:errors/>

<odc:form method="post" action="/SessionReport" anchor="headers">
<html:hidden property="sortField"/>
<html:hidden property="sortDirection"/>
<br /><br />
<jsp:include page="support/submit_cancel.jsp" flush="true">
<jsp:param name="resetAction" value="Session"/>
</jsp:include>


<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Report Interval"/>
</jsp:include>
<jsp:include page="support/interval_controls.jsp" flush="true">
</jsp:include>

The <i>Report Interval</i> is a mandatory date range filter which will help scope
which data is included in the report. All records which were considered to be
active during the specified interval will be included.  For a Session Report, an
active record equates to a session that is/was running for some portion of the
interval.

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
<input type="checkbox" id="selectAll" onclick="selectAllBoxes(this,'col',9);"/><strong>Select all columns</strong><br /><br />
<table cellspacing="0" cellpadding="0" border="0" width="600">
<tr>
<td><html:checkbox property="user" styleId="col0" onclick="updateSelect('selectAll','col',9);"/><strong>User</strong></td>
<td><html:checkbox property="company" styleId="col1" onclick="updateSelect('selectAll','col',9);"/><strong>Company</strong></td>
<td><html:checkbox property="clientType" styleId="col2" onclick="updateSelect('selectAll','col',9);"/><strong>Client Type</strong></td>
<td><html:checkbox property="os" styleId="col3" onclick="updateSelect('selectAll','col',9);"/><strong>OS</strong></td>
</tr>
<tr>
<td><html:checkbox property="sessionState" styleId="col4" onclick="updateSelect('selectAll','col',9);"/><strong>Session State</strong></td>
<td><html:checkbox property="duration" styleId="col5" onclick="updateSelect('selectAll','col',9);"/><strong>Duration</strong></td>
<td><html:checkbox property="bytes" styleId="col6" onclick="updateSelect('selectAll','col',9);"/><strong>Bytes</strong></td>
<td><html:checkbox property="bytesUp" styleId="col7" onclick="updateSelect('selectAll','col',9);"/><strong>Bytes Up</strong></td>
</tr>
<tr>
<td><html:checkbox property="bytesDown" styleId="col8" onclick="updateSelect('selectAll','col',9);"/><strong>Bytes Down</strong></td>
</tr>
</table>

&nbsp;<br />
<strong>Filters:</strong><br />
You can filter the report by selecting the following filters and specifying values for them.<br />
Results that match the specified values will be included in the report. The filters
selected from this section apply to both Summary and Record level reports.
&nbsp;<br />&nbsp;<br />

<table cellspacing="0" cellpadding="0" border="0" width="600">
<tr>
<jsp:include page="support/simple_filter.jsp" flush="true">
<jsp:param name="property" value="userFilter"/>
<jsp:param name="label" value="User"/>
</jsp:include>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="durationFilter"/>
<jsp:param name="label" value="Duration"/>
<jsp:param name="units" value="hr:min:sec"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/simple_filter.jsp" flush="true">
<jsp:param name="property" value="companyFilter"/>
<jsp:param name="label" value="Company"/>
</jsp:include>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="bytesFilter"/>
<jsp:param name="label" value="Bytes"/>
<jsp:param name="units" value="KB"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/simple_filter.jsp" flush="true">
<jsp:param name="property" value="clientTypeFilter"/>
<jsp:param name="label" value="Client Type"/>
</jsp:include>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="bytesUpFilter"/>
<jsp:param name="label" value="Bytes Up"/>
<jsp:param name="units" value="KB"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/simple_filter.jsp" flush="true">
<jsp:param name="property" value="osFilter"/>
<jsp:param name="label" value="OS"/>
</jsp:include>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="bytesDownFilter"/>
<jsp:param name="label" value="Bytes Down"/>
<jsp:param name="units" value="KB"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/simple_filter.jsp" flush="true">
<jsp:param name="property" value="sessionStateFilter"/>
<jsp:param name="label" value="Session State"/>
</jsp:include>
</tr>
</table>

<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Summary Attributes"/>
</jsp:include>

<table cellspacing="0" cellpadding="0" border="0" width="600">
<tr>
<td><html:checkbox property="avgBytesUp"/><strong>Avg Bytes Up</strong></td>
<td><html:checkbox property="avgBytesDown"/><strong>Avg Bytes Down</strong></td>
<td><html:checkbox property="avgBytes"/><strong>Avg Bytes</strong></td>
<td><html:checkbox property="avgDuration"/><strong>Avg Duration</strong></td>
</tr>
<tr><td><html:checkbox property="numSessions"/><strong>Number Sessions</strong></td>
</tr>
</table>

&nbsp;<br />
<strong>Filters:</strong><br />
You can filter the report by selecting the following filters and specifying values for them.<br />
Results that match the specified values will be included in the report. The filters
selected from this section apply ONLY to Record level reports.
<br />&nbsp;<br />

<table cellspacing="0" cellpadding="0" border="0" width="450">
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="avgBytesUpFilter"/>
<jsp:param name="label" value="Avg Bytes Up"/>
<jsp:param name="units" value="KB"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="avgBytesDownFilter"/>
<jsp:param name="label" value="Avg Bytes Down"/>
<jsp:param name="units" value="KB"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="avgBytesFilter"/>
<jsp:param name="label" value="Avg Bytes"/>
<jsp:param name="units" value="KB"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="avgDurationFilter"/>
<jsp:param name="label" value="Avg Duration"/>
<jsp:param name="units" value="hr:min:sec"/>
</jsp:include>
</tr>
<tr>
<jsp:include page="support/complex_filter.jsp" flush="true">
<jsp:param name="property" value="numSessionsFilter"/>
<jsp:param name="label" value="Num Session"/>
<jsp:param name="units" value=""/>
</jsp:include>
</tr>
</table>
<jsp:include page="support/subheader_bar.jsp" flush="true">
<jsp:param name="name" value="Record Level Attributes"/>
</jsp:include>
<html:checkbox property="startTime"/><strong>Start Time</strong><br />
<html:checkbox property="endTime"/><strong>End Time</strong><br />
<br />

<a name="headers" />

<jsp:include page="support/submit_cancel.jsp" flush="true">
<jsp:param name="resetAction" value="Session"/>
</jsp:include>

</odc:form>
<br /><br />
<img alt="" height="1" src="//www.ibm.com/i/v14/rules/blue_rule.gif" width="600"/><br /><br />
<bean:define id="cursortfield" name="SessionForm" property="sortField"/>
<bean:define id="cursortdir"   name="SessionForm" property="sortDirection"/>

<%
   Results results = (Results)request.getAttribute("results");
   if (results != null) {
  
      if (results != null) {
      
%>        

<jsp:include page="support/csv_save.jsp" flush="true">
<jsp:param name="modifier" value="Session"/>
</jsp:include>

<table cellspacing="1" cellpadding="2" border="0">
<%      
      
        
        
        // Print column names
        java.util.HashMap sortparms = new java.util.HashMap();
        pageContext.setAttribute("sortparms", sortparms);
        
        
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
<td><html:link action="/SessionSort" anchor="headers" name="sortparms"><html:img page="<%=\"/images/\"+img%>" border="0"/></html:link></td>
<td><html:link action="/SessionSort" anchor="headers" name="sortparms" style="color:#000000"><%=header%></html:link></td>
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
           enum = results.getOrderedHeaders();
           while(enum.hasMoreElements()) {
              String fn = (String)enum.nextElement();
              String fv = (String)results.getValue(fn);
              String align = results.getHeaderAlignment(fn);
              if (SessionForm.fieldHasSimpleFilter(fn)) {
                 fieldparms.put("modifyFieldName",fn);
                 fieldparms.put("modifyFieldValue",fv);
%>           
                 <td style="padding-top:4px;padding-bottom:4px">
                    <html:link anchor="header" action="/SessionModifyField" name="fieldparms">
                       <%=fv%>
                    </html:link>
                 </td>
<%           
              } else {
%>              
                <td style="padding-top:4px;padding-bottom:4px;text-align:<%=align%>;"><%=fv%></td>
<%
              }
           }
           out.println("</tr>");
        }       
%>           
        </table><br /><br />
<%      } 
   }    
%>

<jsp:include page="support/footer.jsp" flush="true"/>
