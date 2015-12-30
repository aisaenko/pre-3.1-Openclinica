<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

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

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='jobName' class='java.lang.String'/>
<jsp:useBean scope='request' id='jobDesc' class='java.lang.String'/>

<jsp:useBean scope='request' id='periodToRun' class='java.lang.String'/>
<jsp:useBean scope='request' id='tab' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc' class='java.lang.String'/>
<jsp:useBean scope='request' id='spss' class='java.lang.String'/>
<jsp:useBean scope='request' id='contactEmail' class='java.lang.String'/>

<h1><span class="title_Admin">Create Scheduled Job: Export Dataset</span></h1>
<p>

<form action="CreateJobExport" method="post">
<input type="hidden" name="action" value="confirmall" />

<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
<jsp:useBean id="now" class="java.util.Date" />
<P><I>Note that the job is set to run on the server time.  The current server time is <fmt:formatDate value="${now}" pattern="${dtetmeFormat}"/>.</I></P>
<table>
	<tr>
		<td class="text"><b>Job Name:</b><br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobName"/></jsp:include></td>
		<td class="text">
			<input type="text" name="jobName" size="30" value="<c:out value="${jobName}"/>"/>
		</td>
	</tr>
	<tr>
		<td class="text"><b>Job Description:</b><br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobDesc"/></jsp:include></td>
		<td class="text"><input type="text" name="jobDesc" size="60" value="<c:out value="${jobDesc}"/>"/>
		</td>
	</tr>
	<tr>
		<td class="text"><b>Please pick a Dataset to Export:</b></td>
		<td class="text"><select name="dsId">
			<c:forEach var="dataset" items="${datasets}">
				<option value="<c:out value="${dataset.id}"/>"
					<c:if test="${dsId == dataset.id}">
						selected
					</c:if>
				><c:out value="${dataset.name}" /></option>
			</c:forEach>
		</select></td>
	</tr>
	<tr>
		<td class="text"><b>Period to Run:</b>
		&nbsp; <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="periodToRun"/></jsp:include></td>
		<td class="text">
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
		<td class="text">Daily</td>
		<td class="text"><input type="radio" name="periodToRun" value="daily"
			<c:if test="${periodToRun == 'daily'}">
				checked
			</c:if>
			/></td>
		</tr>
		<tr>
			<td class="text">Weekly</td>
			<td class="text"><input type="radio" name="periodToRun" value="weekly"
			<c:if test="${periodToRun == 'weekly'}">
				checked
			</c:if>
			/></td>
		</tr>
		<tr>
			<td class="text">Monthly</td>
			<td class="text"><input type="radio" name="periodToRun" value="monthly"
			<c:if test="${periodToRun == 'monthly'}">
				checked
			</c:if>
			/>
		</td>
		</tr>
		</table>
		</td>
	</tr>

	
	<tr>
		<td class="text"><b>Start Date/Time:</b></td>
		<td class="text">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<c:import url="../include/showDateTimeInput.jsp"><c:param name="prefix" value="job"/><c:param name="count" value="1"/></c:import>
				<td>(<fmt:message key="date_time_format" bundle="${resformat}"/>) </td>
			</tr>
			<tr>
				<td colspan="7">
				<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobDate"/></jsp:include>
				</td>
			</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td class="text"><b>Format:</b>
		&nbsp;<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="tab"/></jsp:include>
		</td>
		<td class="text">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="text">Tab-Delimited</td>
				<td class="text"><input type="checkbox" name="tab" value="1"
					<c:if test="${tab != ''}">
						checked
					</c:if>
				/></td>
			</tr>
			<tr>
				<td class="text">CDISC ODM 1.2</td>
				<td class="text"><input type="checkbox" name="cdisc" value="1"
					<c:if test="${cdisc != ''}">
						checked
					</c:if>
				/></td>
			</tr>
			<tr>
				<td class="text">SPSS Syntax and Data</td>
				<td class="text"><input type="checkbox" name="spss" value="1"
					<c:if test="${spss != ''}">
						checked
					</c:if>
				/></td>
			</tr>
			
			</table>
		</td>
	</tr>

	

	<tr>
		<td class="text"><b>Contact Email Address:</b><br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="contactEmail"/></jsp:include></td>
		<td class="text"><input type="text" name="contactEmail" size="60" value="<c:out value="${contactEmail}"/>"/>
		</td>
	</tr>
	<tr>
		
	</tr>

<%-- 	
	<tr>
		<td class="text"><fmt:message key="status" bundle="${resword}"/></td>
		<td class="text"><b><c:out value="${newDataset.status.name}" /></b>
	</tr>--%>
	<tr>
		<td align="left">			
		  <input type="submit" name="btnSubmit" value="<fmt:message key="confirm_and_save" bundle="${resword}"/>" class="button_xlong"/>			
		</td>
		</form>
		<form action='ViewJob' method="POST">
		<td>
			<input type="submit" name="submit" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button">
		</td>
		</form>	
	</tr>
</table>



<c:import url="../include/workflow.jsp">
   <c:param name="module" value="admin"/> 
</c:import>
<jsp:include page="../include/footer.jsp"/>
