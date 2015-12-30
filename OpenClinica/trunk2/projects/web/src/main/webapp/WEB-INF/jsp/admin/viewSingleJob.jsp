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
<jsp:useBean scope="request" id="triggerBean" class="org.akaza.openclinica.bean.admin.TriggerBean" />
<jsp:useBean scope="request" id="groupName" class="java.lang.String" />
<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
<jsp:useBean id="now" class="java.util.Date" />
<h1><span class="title_manage">View Job: <c:out value="${triggerBean.fullName}" /><a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/brief-overview/jobs')">&nbsp; <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>

<P><I><fmt:message key="note_that_job_is_set" bundle="${resword}"/> <fmt:formatDate value="${now}" pattern="${dtetmeFormat}"/>.</I></P>
<%-- set up table here --%>
<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">
	<tr>
		<td class="table_header_column_top"><fmt:message key="job_name" bundle="${resword}"/>:</td>
		<td class="table_cell_top"><c:out value="${triggerBean.fullName}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column"><fmt:message key="last_time_fired" bundle="${resword}"/>:</td>
		<td class="table_cell"><fmt:formatDate value="${triggerBean.previousDate}" pattern="${dtetmeFormat}"/>&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column"><fmt:message key="next_time_to_fire" bundle="${resword}"/>:</td>
		<td class="table_cell">
		<c:if test="${triggerBean.active}">
			<fmt:formatDate value="${triggerBean.nextDate}" pattern="${dtetmeFormat}"/>&nbsp;
		</c:if>
		</td>
	</tr>
	<tr>
		<td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td>
		<td class="table_cell"><c:out value="${triggerBean.description}" />&nbsp;</td>
	</tr>
	<c:if test="${groupName=='XsltTriggersExportJobs'}">
	<tr>
		<td class="table_header_column"><fmt:message key="dataset" bundle="${resword}"/>:</td>
		<td class="table_cell"><c:out value="${triggerBean.dataset.name}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column"><fmt:message key="period_to_run" bundle="${resword}"/>:</td>
		<td class="table_cell"><c:out value="${triggerBean.periodToRun}" />&nbsp;</td>
	</tr>
	</c:if>
	<tr>
		<td class="table_header_column"><fmt:message key="contact_email" bundle="${resword}"/>:</td>
		<td class="table_cell"><c:out value="${triggerBean.contactEmail}" />&nbsp;</td>
	</tr>
	<c:if test="${groupName=='XsltTriggersExportJobs'}">
	<tr>
		<td class="table_header_column"><fmt:message key="file_formats" bundle="${resword}"/>:</td>
		<td class="table_cell">
            <c:out value="${triggerBean.exportFormat}"/>
		</td>
	</tr>
	</c:if>

	</table>
	<br>

	<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showAuditEventJobRow.jsp" /></c:import>


<c:choose>
<c:when test="${groupName=='XsltTriggersExportJobs'}">
   <form  class="in-button"  action='UpdateJobExport?tname=<c:out value="${triggerBean.fullName}" />' method="POST">
    <input type="submit" name="submit" value="<fmt:message key="edit_this_job" bundle="${resword}"/>" class="button_long">
   </form>
  <form  class="in-button"  action='CreateJobExport' method="POST">
    <input type="submit" name="submit" value="<fmt:message key="create_a_new_export_data_job" bundle="${resword}"/>" class="button_long">
   </form>
  
<input type="button" onclick="confirmExit('ViewJob');" name="exit" value="<fmt:message key="exit" bundle="${resword}"/>   "class="button_medium"/>   </td>
  
</c:when>
<c:otherwise>
	 <form class="in-button" action='UpdateJobImport?tname=<c:out value="${triggerBean.fullName}" />' method="POST">
    <input type="submit" name="submit" value="<fmt:message key="edit_this_job" bundle="${resword}"/>" class="button_long">
   </form>
   <form  class="in-button"  action='CreateJobImport' method="POST">
    <input type="submit" name="submit" value="<fmt:message key="create_a_new_import_data_job" bundle="${resword}"/>" class="button_long">
   </form>
  
<input type="button" onclick="confirmExit('ViewImportJob');" name="exit" value="<fmt:message key="exit" bundle="${resword}"/>   "class="button_medium"/>   </td>
  
</c:otherwise>
</c:choose>
   
 <br><br>


<jsp:include page="../include/footer.jsp"/>
