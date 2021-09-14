<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld"  prefix="html" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
<meta http-equiv="PICS-Label" content='(PICS-1.1 "http://www.icra.org/ratingsv02.html" l gen true r (cz 1 lz 1 nz 1 oz 1 vz 1) "http://www.rsac.org/ratingsv01.html" l gen true r (n 0 s 0 v 0 l 0) "http://www.classify.org/safesurf/" l gen true r (SS~~000 1))' />
<link rel="schema.DC" href="http://purl.org/DC/elements/1.0/"/>
<link rel="SHORTCUT ICON" href="http://www.ibm.com/favicon.ico"/>
<meta name="IBM.Country" content="ZZ"/>
<meta name="DC.Date" scheme="iso8601" content="2005-01-19"/>
<meta name="Source" content="v14 Template Generator, Template 14.1"/>
<meta name="DC.Language" scheme="rfc1766" content="en"/>
<meta name="Owner" content="econnect@us.ibm.com"/>
<meta name="DC.Rights" content="Copyright (c) 2004 by IBM Corporation"/>
<title><%=request.getParameter("title")%></title>
<link rel="stylesheet" type="text/css" href="//www.ibm.com/common/v14/main.css" />
<link rel="stylesheet" type="text/css" media="all" href="//www.ibm.com/common/v14/screen.css" />
<link rel="stylesheet" type="text/css" media="print" href="//www.ibm.com/common/v14/print.css" />
<script src="//www.ibm.com/common/v14/detection.js" language="JavaScript" type="text/javascript">
</script>

</head>
<body>
<!-- MASTHEAD_BEGIN -->
<table border="0" cellpadding="0" cellspacing="0" width="760">
<tr valign="top">
<td class="bbg" width="110"><a href="http://www.ibm.com/"><img alt="IBM&reg;" border="0" height="52" src="//www.ibm.com/i/v14/t/ibm-logo.gif" width="110"/></a></td>
<td align="right" class="mbbg" width="650">
<table border="0" cellpadding="0" cellspacing="0" align="right">
<tr class="cty-tou">
<td rowspan="2" width="17" class="upper-masthead-corner"><a href="#main"><img src="//www.ibm.com/i/c.gif" border="0" width="1" height="1" alt="Skip to main content"/></a></td>
<td align="left">
<table border="0" cellpadding="0" cellspacing="0" align="left">
<tr>
<td><span class="spacer">&nbsp;&nbsp;&nbsp;&nbsp;</span><b class="country">Country/region</b><span class="spacer">&nbsp;[</span><a class="ur-link" href="http://www.ibm.com/planetwide/select/selector.html">select</a><span class="spacer">]</span></td>
<td width="29" class="upper-masthead-divider">&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td align="left"><a class="ur-link" href="http://www.ibm.com/legal/">Terms of use</a></td>
</tr>
</table>
</td>
<td width="40">&nbsp;</td>
</tr>
<tr>
<td class="cty-tou-border" height="1" colspan="2"><img src="//www.ibm.com/i/c.gif" alt="" height="1" width="1"/></td>
</tr>
<tr>
<td colspan="3"><img alt="" height="8" src="//www.ibm.com/i/c.gif" width="1"/></td>
</tr>
<tr>
<td>&nbsp;</td>
<td align="center" colspan="2">
<form action="http://www.ibm.com/Search" method="get" name="search-form" title="Search form">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td width="1"><label for="q"><img src="//www.ibm.com/i/c.gif" width="1" height="1" alt="Search for:"/></label></td>
<td align="right"><input class="input" id="q" maxlength="100" name="q" size="15" type="text" value=""/></td>
<td width="7">&nbsp;<input value="11" name="v" type="hidden"/><input value="en" name="lang" type="hidden"/><input value="zz" name="cc" type="hidden"/></td>
<td><input alt="Search" name="Search" src="//www.ibm.com/i/v14/t/zz/en/search.gif" type="image" value="Search" /></td>
<td width="20">&nbsp;</td>
</tr>
</table>
</form>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td class="blbg" colspan="2">
<table border="0" cellpadding="0" cellspacing="0" width="760">
<tr>
<td>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td><span class="spacer">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
<td><a class="masthead-mainlink" href="http://www.ibm.com/">Home</a></td>
<td class="masthead-divider" width="27">&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td><a class="masthead-mainlink" href="http://www.ibm.com/products/">Products</a></td>
<td class="masthead-divider" width="27">&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td><a class="masthead-mainlink" href="http://www.ibm.com/servicessolutions/">Services &amp; solutions</a></td>
<td class="masthead-divider" width="27">&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td><a class="masthead-mainlink" href="http://www.ibm.com/support/">Support &amp; downloads</a></td>
<td class="masthead-divider" width="27">&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td><a class="masthead-mainlink" href="http://www.ibm.com/account/">My account</a></td>
<td><span class="spacer">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
</tr>
</table>
</td>
</tr>
</table>
</td>
</tr>
</table>
<script src="//www.ibm.com/common/v14/pmh.js" language="JavaScript" type="text/javascript"></script>
<!-- MASTHEAD_END -->
<table width="760" border="0" cellspacing="0" cellpadding="0" id="v14-body-table">
<tr valign="top">
<td width="150" id="navigation">
<!-- LEFTNAV_BEGIN -->
<table border="0" cellpadding="0" cellspacing="0" width="150">
<tr>
<td class="left-nav-spacer">&nbsp;</td>
</tr>
</table>
<table border="0" cellpadding="0" cellspacing="0" width="150">
<tr>
<th class="left-nav-overview" colspan="2"><html:link styleClass="left-nav-overview" action="/FE">IBM Customer Connect</html:link></th>
</tr>
<tr>
<td class="left-nav" colspan="2"><html:link styleClass="left-nav" action="/mainPage">Dropbox reports</html:link></td>
</tr>
<tr class="left-nav-last">
<td width="14"><img src="//www.ibm.com/i/c.gif" width="14" height="1" alt="" class="display-img"/></td>
<td width="136"><img src="//www.ibm.com/i/v14/t/left-nav-corner.gif" width="136" height="19" alt="" class="display-img"/></td>
</tr>
</table>
<!-- LEFTNAV_END -->

</td>
<td width="610"><a name="main"></a><table width="610" cellpadding="0" cellspacing="0" border="0" id="content-table">
<tr valign="top">
<td width="10">&nbsp;</td>
<td width="600"><img alt="" class="display-img" height="8" src="//www.ibm.com/i/c.gif" width="1" />
<h1><%=request.getParameter("h1")%></h1>
<%=request.getParameter("subtitle")==null?"<br /><br />":"<p id=\"subtitle\"><em>"+request.getParameter("subtitle")+"</em></p>"%>
