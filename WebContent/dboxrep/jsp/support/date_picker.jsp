<%@ page session="true" language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld"           prefix="c" %>

<html:select property="<%=request.getParameter(\"prefix\")+\"Month\"%>" size="1" styleClass="iform">
<html:optionsCollection property="months"/>
</html:select>
<html:select property="<%=request.getParameter(\"prefix\")+\"Day\"%>" size="1" styleClass="iform">
<html:optionsCollection property="days"/>
</html:select>
<html:select property="<%=request.getParameter(\"prefix\")+\"Year\"%>" size="1" styleClass="iform">
<html:optionsCollection property="years"/>
</html:select>

<c:if test="${param.prefix != \"start\" && param.prefix != \"end\"}">

<a href="#" onclick="javascript:
   var pform = document.PackageForm;
   var fform = document.FileForm;
   var sform = document.SessionForm;
   var dform = document.DownloadFileForm;
   var form=pform!=null?pform:fform!=null?fform:sform!=null?sform:dform!=null?dform:null;

   var pf='<c:out value="${param.prefix}"/>';
   if (pf.indexOf('Start') > 0) {
      form.<c:out value="${param.prefix}"/>Month.selectedIndex=form.startMonth.selectedIndex;
      form.<c:out value="${param.prefix}"/>Day.selectedIndex=form.startDay.selectedIndex;
      form.<c:out value="${param.prefix}"/>Year.selectedIndex=form.startYear.selectedIndex;
   } else {
      form.<c:out value="${param.prefix}"/>Month.selectedIndex=form.endMonth.selectedIndex;
      form.<c:out value="${param.prefix}"/>Day.selectedIndex=form.endDay.selectedIndex;
      form.<c:out value="${param.prefix}"/>Year.selectedIndex=form.endYear.selectedIndex;
   }
   return false;
">
  <img src="//www.ibm.com/i/v14/icons/dn.gif" alt="" border="0" />
</a>

</c:if>

