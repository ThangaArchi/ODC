<!DOCTYPE html SYSTEM "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd">
<%--                     Copyright Header Check                           --%>
<%-- -------------------------------------------------------------------- --%>
<%--                                                                      --%>
<%--   OCO Source Materials                                               --%>
<%--                                                                      --%>
<%--   Product(s): PROFIT                                                 --%>
<%--                                                                      --%>
<%--   (C)Copyright IBM Corp. 2003-2005                                   --%>
<%--                                                                      --%>
<%--   All Rights Reserved                                                --%>
<%--   US Government Users Restricted Rigts                               --%>
<%--                                                                      --%>
<%--   The source code for this program is not published or otherwise     --%>
<%--   divested of its trade secrets, irrespective of what has been       --%>
<%--   deposited with the US Copyright Office.                            --%>
<%--                                                                      --%>
<%-- -------------------------------------------------------------------- --%>
<%--   Please do not remove any of these commented lines  20 lines        --%>
<%-- -------------------------------------------------------------------- --%>
<%--                     Copyright Footer Check                           --%>

<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta http-equiv="PICS-Label" content='(PICS-1.1 "http://www.icra.org/ratingsv02.html" | gen true r (cz 1 lz 1 nz 1 oz 1 vz 1) "http://www.rsac.org/ratingsv01.html"  | gen true  r (n 0 s 0 v 0 l 0) "http://www.classify.org/safesurf/" | gen true r (SS~~000 1))' />
<meta name="Source" content="v14 Template Generator, Template 14.4" />
<meta name="Description" content="The IBM Customer Connect Partner Extranet, list single user to application" />
<meta name="Abstract" content="IBM Corporation, Technology Group, list single user to application" />
<meta name="Keywords" content="IBM Microelectronics storage SAN OEM Distributors Resellers hard disk drives Networking" />
<meta name="IBM.Country" content="US" />
<meta name = "Robots" content="index,follow" />
<meta name="DC.Date" scheme="iso8601" content="2004-09-25" />
<meta name="DC.Language" scheme="rfc1766" content="en-US" />
<meta name="Security" content="public" />
<meta name="DC.Rights" content="Copyright (c) 2004 by IBM Corporation" />
<meta name="Owner" content="eConnect@us.ibm.com" />
<meta name="Charset" content="ISO-8859-1" />

<meta name="Title" content="IBM Customer Connect" />
<meta name="Country" content="US" />
<meta name="Author" content="eConnect@us.ibm.com" />
<meta name="DocumentCountryCode" content="US" />
<meta name="DocumentLanguageCode" content="en" />
<meta name="Review" content="20000808" />
<meta name="LastUpdate" content="Mon Sept 24 14:41:19 2004" />
<meta name="Format" content="text/xhtml" />
<meta name="DC.Type" scheme="IBM_ContentClassTaxonomy" content="TS100" />
<meta name="DC.Subject" scheme="IBM_SubjectTaxonomy" content="1018529, 56982" />
<meta name="DC.Publisher" content="IBM Corporation" />
<meta name="IBM.Effective" scheme="W3CDTF" content="2004-09-27" />
<meta name="IBM.Industry" scheme="IBM_IndustryTaxonomy" content="K, Y, BA" />
</head>

<body> 
<table bgcolor="#000">
<tr valign="top">
<td class="bbg" width="110"><a href="http://www.ibm.com/us/"><img alt="IBM?" src="//www.ibm.com/i/v14/t/ibm-logo.gif" border="0" height="52" width="110"></a></td>
<td class="mbbg" align="right" width="650">
<table align="right" border="0" cellpadding="0" cellspacing="0">
<tbody><tr class="cty-tou">
<td class="upper-masthead-corner" rowspan="2" width="17"><a href="#main"><img src="//www.ibm.com/i/c.gif" alt="Skip to main content" border="0" height="1" width="1"></a></td>
<td align="left">
<table align="left" border="0" cellpadding="0" cellspacing="0" bgcolor="#5c81a7">
<tbody><tr>
<td><span class="spacer">&nbsp;&nbsp;&nbsp;&nbsp;</span><b class="country">United States</b><span class="spacer">&nbsp;[</span><a href="http://www.ibm.com/planetwide/select/selector.html" class="ur-link">change</a><span class="spacer">]</span></td>
<td class="upper-masthead-divider" width="29">&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td align="left"><a href="http://www.ibm.com/legal/us/" class="ur-link">Terms of use</a></td>

</tr>
</tbody></table>
</td>
<td width="40">&nbsp;</td>
</tr>
<tr>
<td colspan="2" class="cty-tou-border" height="1"><img alt="" src="//www.ibm.com/i/c.gif" height="1" width="1"></td>
</tr>
<tr>
<td colspan="3"><img src="//www.ibm.com/i/c.gif" alt="" height="8" width="1"></td>
</tr>
<tr>
<td>&nbsp;</td>
<td colspan="2" align="center">
<form title="Search form" name="search-form" method="get" action="http://www.ibm.com/Search/">
<table border="0" cellpadding="0" cellspacing="0">
<tbody><tr>

<td width="1"><label for="q"><img alt="Search for:" src="//www.ibm.com/i/c.gif" height="1" width="1"></label></td>
<td align="right"><input value="" size="15" name="q" maxlength="100" id="q" class="input" type="text"></td>
<td width="7">&nbsp;<input name="v" value="14" type="hidden"><input name="lang" value="en" type="hidden"><input name="cc" value="us" type="hidden"><input name="en" value="utf" type="hidden"></td>
<td><input value="Search" src="//www.ibm.com/i/v14/t/us/en/search.gif" name="Search" alt="Search" type="image"></td>
<td width="20">&nbsp;</td>
</tr>
</tbody></table>
</form>
</td>
</tr>
</tbody></table>
</td>
</tr>
</table>


<table> 
   <tr>
      <td headers="manage1" class="tblue">
         <font color="#5c81a7"> Manage Application to user </font> -> 
<logic:notEqual name="UserApplicationForm" property="listPage" value="false">
         <bean:write name="UserApplicationForm" property="message"/> 
</logic:notEqual>
<logic:equal name="UserApplicationForm" property="listPage" value="false">
         List user by Application
</logic:equal>
      </td>
   </tr>
</table>
 
<!-- Gray dotted line -->
<table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tbody>
      <tr>
         <td headers="col10" width="1"><img src="//www.ibm.com/i/c.gif" alt=""
            height="1" width="1"></td>
         <td headers="col11"
            background="//www.ibm.com/i/v14/gray_dotted_line.gif" width="431"><img
            alt="" src="//www.ibm.com/i/c.gif" border="0" height="1"
            width="100%"></td>
         <td headers="col12" width="1"><img src="//www.ibm.com/i/c.gif" alt=""
            height="1" width="1"></td>
      </tr>
   </tbody>
</table>
<!-- End Gray dotted line -->
 
 
<logic:equal name="UserApplicationForm" property="moduleLabel" value="add">
   <html:form action="/AddUserApplication">
      <input type="hidden" name="userName" value="<bean:write name="UserApplicationForm" property="userName" />">
      <table summary="" border="0" cellpadding="3" cellspacing="1" width="70%" >
         <tr bgcolor="#cccccc" valign="middle">
            <td> <font color="#5c81a7"> <u> <center> Details of user : <bean:write name="UserApplicationForm" property="userName" /> </center> </u> </td>
         </tr>
      </table>
      <table summary="" border="0" cellpadding="3" cellspacing="0" width="70%" >
          <tr>
            <td width="183"> <font color="#5c81a7"> User [<bean:write name="UserApplicationForm" property="userName" />] have the following application : </font> </td>
            <td>
               <html:select property="userName" multiple="false" size="10">
                  <logic:iterate id="lineItem" indexId="index" name="UserApplicationForm" property="userApplicationList" type="oem.edge.ed.odc.remoteviewer.vo.UserApplicationVO">
                     <option value="<bean:write name="lineItem" property="userName" />" disabled="disabled">
                        <bean:write name="lineItem" property="applicationName" /> -> 
                        <bean:write name="lineItem" property="serverName" /> -> 
                        <bean:write name="lineItem" property="applicationPath" />
                     </option>
                  </logic:iterate>
               </html:select>
            </td>
         </tr>
 
<logic:notEqual name="UserApplicationForm" property="listPage" value="false">
         <tr>
            <td width="183"> <font color="#5c81a7"> Select the appliction to <bean:write name="UserApplicationForm" property="moduleLabel"/> : </font> </td>
            <td>
               <html:select property="applicationId" size="10">
						<logic:iterate id="lineItem" indexId="index"
							name="UserApplicationForm" property="userNonApplicationList"
							type="oem.edge.ed.odc.remoteviewer.vo.UserApplicationVO">
							<option
								value="<bean:write name="lineItem" property="applicationId" />@<bean:write name="lineItem" property="applicationName" />@<bean:write name="lineItem" property="serverName" />@<bean:write name="lineItem" property="applicationPath" />">
							<bean:write name="lineItem" property="applicationName" /> -> <bean:write
								name="lineItem" property="serverName" /> -> <bean:write
								name="lineItem" property="applicationPath" /></option>
						</logic:iterate>
					</html:select>
            </td>
         </tr>
         <tr bgcolor="#cccccc" valign="middle">
            <td align="center" width="183">
               <input name="submit" src="//www.ibm.com/i/v14/buttons/submit.gif" title="Add Application to user" alt="Add Application to user" type="image" align="middle">
            </td>
            <td> <CENTER>
               <html:link action="RemoteAdministrator">
                  <img src="//www.ibm.com/i/v14/buttons/arrow_lt.gif" alt="Back to Admin main" border="0" height="21" width="21"><font color="#5c81a7"> Back to Admin main </font>
               </html:link>
               </CENTER>
            </td> <td>
               <html:link action="LoadAllUser">
                  <img src="//www.ibm.com/i/v14/buttons/arrow_lt.gif" alt="Back" border="0" height="21" width="21"><font color="#5c81a7"> Back </font>
               </html:link>
            </td>
         </tr>
      </table>
</logic:notEqual>
   </html:form>
</logic:equal>
 
 
 
<logic:equal name="UserApplicationForm" property="moduleLabel" value="delete">
   <html:form action="/DeleteUserApplication"> <!-- /AddUserApplication" -->
      <input type="hidden" name="userName" value="<bean:write name="UserApplicationForm" property="userName" />">
      <table summary="" border="0" cellpadding="3" cellspacing="1" width="70%" >
         <tr bgcolor="#cccccc" valign="middle">
            <td> <font color="#5c81a7"> <u> <center> Details of user : <bean:write name="UserApplicationForm" property="userName" /> </center> </u> </td>
         </tr>
      </table>
      <table summary="" border="0" cellpadding="3" cellspacing="0" width="70%" >
          <tr>
            <td width="183"> <font color="#5c81a7"> User [<bean:write name="UserApplicationForm" property="userName" />] have the following application : </font> </td>
            <td>
               <html:select property="applicationId" size="10">
					<logic:iterate id="lineItem" indexId="index"
						name="UserApplicationForm" property="userApplicationList"
						type="oem.edge.ed.odc.remoteviewer.vo.UserApplicationVO">
						<option
							value="<bean:write name="lineItem" property="applicationId" />@<bean:write name="lineItem" property="applicationName" />@<bean:write name="lineItem" property="serverName" />@<bean:write name="lineItem" property="applicationPath" />">
						<bean:write name="lineItem" property="applicationName" /> -> <bean:write
							name="lineItem" property="serverName" /> -> <bean:write
							name="lineItem" property="applicationPath" /></option>
					</logic:iterate>
				</html:select>
            </td>
         </tr>
<logic:notEqual name="UserApplicationForm" property="listPage" value="false">
         <!-- <tr>
            <td width="183"> <font color="#5c81a7"> Select the appliction to <bean:write name="UserApplicationForm" property="moduleLabel"/> : </font> </td>
            <td>
               <html:select property="userName" multiple="false" size="10">
                  <logic:iterate id="lineItem" indexId="index" name="UserApplicationForm" property="userNonApplicationList" type="oem.edge.ed.odc.remoteviewer.vo.UserApplicationVO">
                     <option value="<bean:write name="lineItem" property="userName" />" disabled="disabled">
                        <bean:write name="lineItem" property="applicationName" /> -> 
                        <bean:write name="lineItem" property="serverName" /> -> 
                        <bean:write name="lineItem" property="applicationPath" />
                     </option>
                  </logic:iterate>
               </html:select>
            </td>
         </tr> -->
         <tr bgcolor="#cccccc" valign="middle">
            <td align="center" width="183">
               <input name="submit" src="//www.ibm.com/i/v14/buttons/submit.gif" title="Delete Application to user" alt="Delete Application to user" type="image" align="middle">
            </td>
            <td> <CENTER>
               <html:link action="RemoteAdministrator">
                  <img src="//www.ibm.com/i/v14/buttons/arrow_lt.gif" alt="Back to Admin main" border="0" height="21" width="21"><font color="#5c81a7"> Back to Admin main </font>
               </html:link>
               </CENTER>
            </td> <td>
               <html:link action="LoadAllUser">
                  <img src="//www.ibm.com/i/v14/buttons/arrow_lt.gif" alt="Back" border="0" height="21" width="21"><font color="#5c81a7"> Back </font>
               </html:link>
            </td>
         </tr>
</logic:notEqual>
<logic:equal name="UserApplicationForm" property="listPage" value="false">
         <tr bgcolor="#cccccc" valign="middle">
            <td>
               <html:link action="ListUserApplication">
                  <img src="//www.ibm.com/i/v14/buttons/arrow_lt.gif" alt="Back" border="0" height="21" width="21"><font color="#5c81a7"> Back </font>
               </html:link>
            </td>
            <td>
               <html:link action="RemoteAdministrator">
                  <img src="//www.ibm.com/i/v14/buttons/arrow_lt.gif" alt="Back to Admin main" border="0" height="21" width="21"><font color="#5c81a7"> Back to Admin main </font>
               </html:link>
            </td> 
         </tr>
</logic:equal>
      </table>
   </html:form>
</logic:equal>

<table>
	<tr>
		<td>
			<p> <br /> <span class="ast"> <font color="#ff0000"> 
							<b> <bean:write name="UserApplicationForm" property="errorMessage" /> </b> </font> 
						</span> 
					<br /> </p>
		</td>
	</tr>
	<tr>
		<td>
			<logic:equal name="UserApplicationForm" property="errorMessage" value="Please enter valid ICC user-name....!!!">  <br /><br /><br />
               <html:link action="LoadAllUser">
                  <img src="//www.ibm.com/i/v14/buttons/arrow_lt.gif" alt="Back" border="0" height="21" width="21"><font color="#5c81a7"> Back </font>
               </html:link>
			</logic:equal> 
		</td>
		<td> 
			<logic:equal name="UserApplicationForm" property="errorMessage" value="Please enter valid ICC user-name....!!!">  <br /><br /><br />
	            <html:link action="RemoteAdministrator">
	            	<img src="//www.ibm.com/i/v14/buttons/arrow_lt.gif" alt="Back to Admin main" border="0" height="21" width="21"><font color="#5c81a7"> Back to Admin main </font>
	            </html:link> 
			</logic:equal> 
		<td>
	</tr>
</table>
<hr />
<table summary="" border="0" cellpadding="3" cellspacing="1" width="70%">
   <tr>
      <td headers="d7" width="16" align="left"><img alt="protected content"
         src="http://www.ibm.com/i/v14/icons//key.gif" width="16" height="16" /></td>
      <td headers="d8" align="left"><span class="fnt">A key icon displayed
      in a page indicates that the page is secure and password-protected.</span></td>
   </tr>
</table>
</body>
</html:html>

 