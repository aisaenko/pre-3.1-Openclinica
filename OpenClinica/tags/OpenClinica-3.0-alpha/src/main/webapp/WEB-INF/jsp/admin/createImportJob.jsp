<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

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
<h1><span class="title_Admin">Create Scheduled Job: Import Data</span></h1>
<p>

<form action="CreateJobImport" method="post">
<%-- change the above target, tbh--%>
<input type="hidden" name="action" value="confirmall" />

<table>
	<tr>
		<td class="text"><b>The root filepath to import from will be this location:</b></td>
		<td class="text"><c:out value="${filePath}"/></td>
	</tr>
	<tr>
		<td class="text">You can create a new directory here, <br/>or leave it blank to take files from the root directory:</td>
		<td class="text"><input name="filePathDir" type=text/></td>
	</tr>
	<tr>
		<td colspan="2" align="left">You may schedule this import to be run at a specific time each day, week or month.</td>
	</tr>
	<!--  <tr>
		<td colspan="2" align="left">Do you want this import to run daily, weekly or monthly?  Please choose one option from below.</td>
	</tr>-->

	<tr>
		<td class="text"><b>Daily</b></td>
		<td class="text"><input type="radio" name="periodToRun" value="daily"/></td>
	</tr>
	<tr>
		<td class="text"><b>Weekly</b></td>
		<td class="text"><input type="radio" name="periodToRun" value="weekly"/></td>
	</tr>
	<tr>
		<td class="text"><b>Monthly</b></td>
		<td class="text"><input type="radio" name="periodToRun" value="monthly"/></td>
	</tr>
	<tr>
		<td colspan="2" align="left"><b>Start Date/Time:</b>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<c:import url="../include/showDateTimeInput.jsp"><c:param name="prefix" value="job"/><c:param name="count" value="1"/></c:import>
				<td>(<fmt:message key="date_time_format" bundle="${resformat}"/>) </td>
			</tr>
			<tr>
				<td colspan="7"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="start"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td colspan="2" align="left">The time you select it to run the first time will be the time it runs every other time unless you edit this job.</td>
	</tr>

	<tr>
		<td colspan="2" align="left">Please provide an email address you would like OpenClinica to send all notifications to, about this scheduled job.</td>
	</tr>
	<tr>
		<td colspan="2" align="left"><input type="text" name="contactEmail" size="90"/>
		<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="contactEmail"/></jsp:include>
		</td>
	</tr>

	<tr>
		<td align="left">			
		  <input type="submit" name="btnSubmit" value="<fmt:message key="save" bundle="${resword}"/>" class="button_xlong"/>			
		</td>
		<td>
		  <input type="submit" name="btnSubmit" value="Cancel" class="button_xlong"/>
		</td>
	</tr>
</table>

</form>

<c:import url="../include/workflow.jsp">
   <c:param name="module" value="admin"/> 
</c:import>
<jsp:include page="../include/footer.jsp"/>
