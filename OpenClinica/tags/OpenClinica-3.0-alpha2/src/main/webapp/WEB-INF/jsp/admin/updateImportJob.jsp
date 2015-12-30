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

<jsp:useBean scope='request' id='filePath' class='java.lang.String'/>
<jsp:useBean scope='request' id='firstFilePath' class='java.lang.String'/>
<jsp:useBean scope='request' id='hours' class='java.lang.String'/>
<jsp:useBean scope='request' id='minutes' class='java.lang.String'/>
<jsp:useBean scope='request' id='study_name' class='java.lang.String'/>
<jsp:useBean scope='request' id='contactEmail' class='java.lang.String'/>

<h1><span class="title_Admin">Update Scheduled Job: Import Data</span></h1>
<p>
<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
<jsp:useBean id="now" class="java.util.Date" />
<P><I>Note that the job is set to run on the server time.  The current server time is <fmt:formatDate value="${now}" pattern="${dtetmeFormat}"/>.</I></P>

<form action="UpdateJobImport" method="post">

<input type="hidden" name="action" value="confirmall" />
<input type="hidden" name="tname" value="<c:out value="${jobName}"/>" />
<table>
	<tr>
		<td class="text"><b>Import Job Name:</b><br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobName"/></jsp:include></td>
		<td class="text">
			<input type="text" name="jobName" size="30" value="<c:out value="${jobName}"/>"/>
		</td>
	</tr>
	<tr>
		<td class="text"><b>Import Job Description:</b><br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobDesc"/></jsp:include></td>
		<td class="text"><input type="text" name="jobDesc" size="60" value="<c:out value="${jobDesc}"/>"/>
		</td>
	</tr>
	<tr>
		<td class="text"><b>Import Job Study:</b><br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobDesc"/></jsp:include></td>
		<td class="text"><select name="studyId">
							<c:forEach var="studyRole" items="${studies}">
								<option value="<c:out value="${studyRole.studyId}"/>"
									<c:if test="${study_name == studyRole.studyName }">
										selected
									</c:if>
								><c:out value="${studyRole.studyName}"/>
							</c:forEach>
						</select>
						<%-- have to put a default picker here next, tbh--%>
		</td>
	</tr>
	<tr>
		<td class="text"><b>The default filepath to import from will be this location:</b></td>
		<td class="text"><c:out value="${firstFilePath}"/></td>
	</tr>
	<tr>
		<td class="text">You can create a new directory here, <br/>or leave it blank to take files from the root directory:<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="filePathDir"/></jsp:include>
		</td>
		<td class="text"><input name="filePathDir" type=text value="<c:out value="${filePath}"/>"/></td>
	</tr>
	
	<%-- <tr>
		<td class="text"><b>Start Date/Time:</b></td>
		<td class="text">
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
	</tr> --%>
	
	<tr>
		<td class="text"><b>Frequency:</b><br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="hours"/></jsp:include>
		</td>
		<td class="text">
		Run every 
		<select name="hours">
			<option value="0"
			<c:if test="${hours=='0'}">
			selected
			</c:if>
			>0</option>
			<option value="1"
			<c:if test="${hours=='1'}">
			selected
			</c:if>
			>1</option>
			<option value="2"
			<c:if test="${hours=='2'}">
			selected
			</c:if>
			>2</option>
			<option value="4"
			<c:if test="${hours=='4'}">
			selected
			</c:if>
			>4</option>
			<option value="8"
			<c:if test="${hours=='8'}">
			selected
			</c:if>
			>8</option>
			<option value="24"
			<c:if test="${hours=='24'}">
			selected
			</c:if>
			>24</option>
		</select> 
		hours and 
		<select name="minutes">
			<option value="0"
			<c:if test="${minutes=='0'}">
			selected
			</c:if>
			>0</option>
			<option value="10"
			<c:if test="${minutes=='10'}">
			selected
			</c:if>
			>10</option>
			<option value="20"
			<c:if test="${minutes=='20'}">
			selected
			</c:if>
			>20</option>
			<option value="30"
			<c:if test="${minutes=='30'}">
			selected
			</c:if>
			>30</option>
			<option value="40"
			<c:if test="${minutes=='40'}">
			selected
			</c:if>
			>40</option>
			<option value="50"
			<c:if test="${minutes=='50'}">
			selected
			</c:if>
			>50</option>
		</select> minutes.
		</td>
	</tr>	
		
	<tr>
		<td colspan="2" align="left">The frequency you select it to run the first time will be the time it runs every other time unless you edit this job.</td>
	</tr>

	<tr>
		<td class="text">
			<b>Contact Email:</b></td>
		<td class="text">
			<input type="text" name="contactEmail" size="90" value="<c:out value="${contactEmail}"/>" />
			<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="contactEmail"/></jsp:include>
		</td>
	</tr>

	<tr>
		<td align="left">			
		  <input type="submit" name="btnSubmit" value="<fmt:message key="save" bundle="${resword}"/>" class="button_xlong"/>			
		</td>
		</form>
		<form action='ViewImportJob' method="POST">
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
