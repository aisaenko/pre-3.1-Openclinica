<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>


<jsp:include page="../include/admin-header.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='request' id='logmsg' class='java.lang.String'/>

<jsp:useBean scope='request' id='filename' class='java.lang.String'/>
<h1><span class="title_manage">View Log: <c:out value="${filename}"/>
</span></h1>
<p>

		<div class="tablebox_center">

		&nbsp;<c:out value="${logmsg}" escapeXml="false"/>

		</div>



</p>
<p></p>
<div class="homebox_bullets"><a href="ViewSingleJob?tname=<c:out value='${tname}'/>&gname=<c:out value='${gname}'/>">Back to the original job</a></div>
<div class="homebox_bullets"><a href="ViewJob"><fmt:message key="view_all_export_data_jobs" bundle="${resword}"/></a></div>
<div class="homebox_bullets"><a href="ViewImportJob"><fmt:message key="view_all_import_data_jobs" bundle="${resword}"/></a></div>
<p></p>
<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
<jsp:useBean id="now" class="java.util.Date" />
<P><I><fmt:message key="note_that_job_is_set" bundle="${resword}"/> <fmt:formatDate value="${now}" pattern="${dtetmeFormat}"/>.</I></P>
<br><br>
 <jsp:include page="../include/footer.jsp"/>