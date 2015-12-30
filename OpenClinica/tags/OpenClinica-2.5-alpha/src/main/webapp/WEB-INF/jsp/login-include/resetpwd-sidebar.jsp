<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@page import="org.akaza.openclinica.dao.core.SQLInitServlet"%>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/> 

<script language="JavaScript">
function reportBug(versionNumber) {
 var bugtrack = "http://dev.openclinica.org/OpenClinica/bug.php?version=" + versionNumber  +"&url=";
 bugtrack = bugtrack + window.location.href;
 openDocWindow(bugtrack);

}
</script>
<!-- Breadcrumbs -->

	<div class="breadcrumbs">
	<a class="breadcrumb_completed"
			href="MainMenu">
			<fmt:message key="home" bundle="${resworkflow}"/></a>	
	&nbsp;

	
	</div>

<!-- End Breadcrumbs -->

				</td>

<!-- Help and OpenClinica Feedback Buttons -->

				<td valign="top">
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>&nbsp;</tr>
					<tr>
					  <tr>
					  <td style="white-space:nowrap">
						  <a href="javascript:reportBug()">
						  <span class="aka_font_general" style="font-size: 0.9em">
						  <fmt:message key="openclinica_report_issue" bundle="${resword}"/></span> </a> | <a href="javascript:openDocWindow('<c:out value="${sessionScope.supportURL}" />')"> <span class="aka_font_general" style="font-size: 0.9em"><fmt:message key="openclinica_feedback" bundle="${resword}"/></span></a>
												
						</td>
					</tr>
					</tr>
				</table>
				</td>

<!-- end Help and OpenClinica Feedback Buttons -->

	<td valign="top" align="right">


<!-- User Box -->


<!-- End User Box -->

				</td>
			</tr>
		</table>
<!-- End Header Table -->
<table border="0" cellpadding=0" cellspacing="0">
	<tr>
		<td class="sidebar" valign="top">

<!-- Sidebar Contents -->

	<br><br>
	<a href="RequestAccount"><fmt:message key="request_an_account" bundle="${resword}"/></a>
	<br><br>
	<a href="RequestPassword"><fmt:message key="forgot_password" bundle="${resword}"/></a>
	


<!-- End Sidebar Contents -->

				<br><img src="images/spacer.gif" width="120" height="1">

				</td>
				<td class="content" valign="top">