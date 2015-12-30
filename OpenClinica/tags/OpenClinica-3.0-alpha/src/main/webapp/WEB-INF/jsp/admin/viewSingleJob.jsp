<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:include page="../include/admin-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
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

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>

<jsp:useBean scope="request" id="triggerBean" class="org.akaza.openclinica.bean.admin.TriggerBean" />
<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
<jsp:useBean id="now" class="java.util.Date" />

<h1><span class="title_Admin">View Job: <c:out value="${triggerBean.fullName}" /><a href="javascript:openDocWindow('help/5_1_administerUsers_Help.html')">&nbsp; <img src="images/bt_Help_Admin.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1> 

<P><I>Note that the job is set to run on the server time.  The current server time is <fmt:formatDate value="${now}" pattern="${dtetmeFormat}"/>.</I></P>
<%-- set up table here --%>
<div style="width: 400px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="table_header_column_top">Job Name:</td>
		<td class="table_cell_top"><c:out value="${triggerBean.fullName}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Last Time Fired:</td>
		<td class="table_cell"><fmt:formatDate value="${triggerBean.previousDate}" pattern="${dtetmeFormat}"/>&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Next Time to Fire:</td>
		<td class="table_cell"><fmt:formatDate value="${triggerBean.nextDate}" pattern="${dtetmeFormat}"/>&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Description:</td>
		<td class="table_cell"><c:out value="${triggerBean.description}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Dataset:</td>
		<td class="table_cell"><c:out value="${triggerBean.dataset.name}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Period to Run:</td>
		<td class="table_cell"><c:out value="${triggerBean.periodToRun}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Notification Email:</td>
		<td class="table_cell"><c:out value="${triggerBean.contactEmail}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">File Formats:</td>
		<td class="table_cell"><c:if test="${triggerBean.cdisc == '1'}">CDISC&nbsp;</c:if>
		<c:if test="${triggerBean.tab == '1'}">TAB&nbsp;</c:if><c:if test="${triggerBean.spss == '1'}">SPSS&nbsp;</c:if></td>
	</tr>
	<%--
	<tr>
		<td class="table_header_column"><fmt:message key="business_administrator" bundle="${resword}"/>:</td>
		<c:choose>
			<c:when test="${user.sysAdmin}">
				<td class="table_cell"><fmt:message key="yes" bundle="${resword}"/></td>
			</c:when>
			<c:otherwise>
				<td class="table_cell"><fmt:message key="no" bundle="${resword}"/></td>
			</c:otherwise>
		</c:choose>
	</tr>
	<tr>
		<td class="table_header_column"><fmt:message key="technical_administrator" bundle="${resword}"/>:</td>
		<c:choose>
			<c:when test="${user.techAdmin}">
				<td class="table_cell"><fmt:message key="yes" bundle="${resword}"/></td>
			</c:when>
			<c:otherwise>
				<td class="table_cell"><fmt:message key="no" bundle="${resword}"/></td>
			</c:otherwise>
		</c:choose>
	</tr>
	
	--%>

	</table>
	</div>

	</div></div></div></div></div></div></div></div>

	</div>
	
	<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showAuditEventJobRow.jsp" /></c:import>

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
   <td>
   <form action='UpdateJobExport?tname=<c:out value="${triggerBean.fullName}" />' method="POST">
    <input type="submit" name="submit" value="Edit This Job" class="button_long">
   </form>
   </td>
   <td>
   <form action='CreateJobExport' method="POST">
    <input type="submit" name="submit" value="Create a New Export Data Job" class="button_long">
   </form>
   </td>
   <td>
   <form action='ViewJob' method="POST">
    <input type="submit" name="submit" value="<fmt:message key="exit" bundle="${resword}"/>" class="button">
   </form>
   </td>
   <td>
   </td>
  </tr>
</table>

 <c:import url="../include/workflow.jsp">
  <c:param name="module" value="admin"/>
 </c:import>
<jsp:include page="../include/footer.jsp"/>
