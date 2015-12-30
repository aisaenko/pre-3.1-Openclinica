<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>


<jsp:include page="../include/submit-header.jsp"/>
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
<jsp:useBean scope="session" id="currentRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean" />
<jsp:useBean scope="request" id="pageMessages" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="formMessages" class="java.util.HashMap" />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<jsp:useBean scope="request" id="subjects" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="eventDefinitions" class="java.util.ArrayList" />

<c:set var="location" value="" />
<c:set var="requestStudySubjectFalse" value="no" />

<!-- TODO: HOW TO DEAL WITH PRESET VALUES THAT AREN'T STRINGS? -->
<!-- TODO: CAN I USE PUBLIC STATIC MEMBERS HERE? -->
<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "location"}'>
		<c:set var="location" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "studyEventDefinition"}'>
		<c:set var="chosenDefinition" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "studySubject"}'>
		<c:set var="chosenSubject" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "requestStudySubject"}'>
		<c:set var="requestStudySubject" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "locationScheduled"}'>
		<c:set var="locationScheduled" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "studyEventDefinitionScheduled"}'>
		<c:set var="chosenDefinitionScheduled" value="${presetValue.value}" />
	</c:if>
</c:forEach>
<h1><span class="title_submit">
<c:choose>
	<c:when test="${requestStudySubject == requestStudySubjectFalse}">
<fmt:message key="add_new_study_event_for" bundle="${resword}"/><b> <c:out value="${chosenSubject.name}" /></b> <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step2')"><img src="images/bt_Help_Submit.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
	</c:when>
	<c:otherwise>
<fmt:message key="add_a_new_study_event" bundle="${resword}"/><a href="javascript:openDocWindow('help/2_3_newStudyEvent_Help.html')"><img src="images/bt_Help_Submit.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
	</c:otherwise>
</c:choose>
</span></h1>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" ID="js1">  
    <%=org.akaza.openclinica.i18n.util.HtmlUtils.getCalendarPopupCode("cal1","testdiv1")%>
</SCRIPT>

<P><fmt:message key="field_required" bundle="${resword}"/></P>

<form action="CreateNewStudyEvent" method="post">
<jsp:include page="../include/showSubmitted.jsp" />

<div style="width: 600px">	

<!-- These DIVs define shaded box borders -->
	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="textbox_center">

<table border="0" cellpadding="3" cellspacing="0">
	<tr>
		<td class="formlabel"><fmt:message key="study_subject_ID" bundle="${resword}"/>:</td>
		<td valign="top">
			<c:choose>
				<c:when test="${requestStudySubject == requestStudySubjectFalse}">
					<b><c:out value="${chosenSubject.name}" /></b>
					<input type="hidden" name="studySubject" value="<c:out value="${chosenSubject.id}" />" />
					<input type="hidden" name="requestStudySubject" value="<c:out value="${requestStudySubject}" />" />
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studySubject"/></jsp:include>
				</c:when>
				<c:otherwise>
					<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top">
						<div class="formfieldXL_BG">
							<select name="studySubject" class="formfieldXL">
							<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
							<c:forEach var="subject" items="${subjects}">
								<c:choose>
									<c:when test="${chosenSubject.id == subject.id}">
										<option value="<c:out value="${subject.id}" />" selected><c:out value="${subject.name}"/></option>
									</c:when>
									<c:otherwise>
										<option value="<c:out value="${subject.id}" />"><c:out value="${subject.name}"/></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
							</select>
						</div>
						</td>
						<td>*</td>
					</tr>
					<tr>
						<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studySubject"/></jsp:include></td>
					</tr>
					</table>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>

	<tr>
	  	<td class="formlabel"><fmt:message key="study_event_definition" bundle="${resword}"/>:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<select name="studyEventDefinition" class="formfieldXL">
						<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
						<c:forEach var="definition" items="${eventDefinitions}">
							<c:choose>
								<c:when test="${definition.repeating}">
									<c:set var="repeating">
									 (<fmt:message key="repeating" bundle="${resword}"/>)
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="repeating">
									(<fmt:message key="non_repeating" bundle="${resword}"/>)
									</c:set>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${chosenDefinition.id == definition.id}">
									<option value="<c:out value="${definition.id}" />" selected><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:when>
								<c:otherwise>
									<option value="<c:out value="${definition.id}"/>"><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</div>
				</td>
				<td>*</td>
			</tr>
			<tr>
				<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyEventDefinition"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td class="formlabel"><fmt:message key="location" bundle="${resword}"/>:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<input type="text" name="location" value="<c:out value="${location}"/>" size="50" class="formfieldXL">
				</div>
				</td>
				<td>*<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=studyEvent&field=location&column=location','spanAlert-location'); return false;">
				<img name="flag_location" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a></c:if></td>
			</tr>
			<tr>
				<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="location"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td class="formlabel"><fmt:message key="start_date_time" bundle="${resword}"/>:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<c:import url="../include/showDateTimeInput.jsp">
				<c:param name="prefix" value="start"/>
				<c:param name="count" value="1"/>
				</c:import>
				
				<td>(<fmt:message key="date_time_format" bundle="${resformat}"/>) *<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=studyEvent&field=start&column=date_start','spanAlert-start'); return false;">
				<img name="flag_start" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a></c:if></td>
			</tr>
			<tr>
				<td colspan="7"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="start"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel"><fmt:message key="end_date_time" bundle="${resword}"/>:</td>
		<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<c:import url="../include/showDateTimeInput.jsp">
				<c:param name="prefix" value="end"/>
				<c:param name="count" value="2"/>
				</c:import>
				
				<td>(<fmt:message key="date_time_format" bundle="${resformat}"/>)<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=studyEvent&field=end&column=date_end','spanAlert-end'); return false;">
				<img name="flag_end" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a></c:if></td>
			</tr>
			<tr>
				<td colspan="7">
					<fmt:message key="leave_this_field_blank_if_not_applicable" bundle="${restext}"/><br/>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="end" /></jsp:include>
				</td>
			</tr>
			</table>
		</td>
	</tr>
</table>

</div>

</div></div></div></div></div></div></div></div>
</div><br>
<input type="submit" name="Submit" value="<fmt:message key="proceed_to_enter_data" bundle="${resword}"/>" class="button_long" />
<p><fmt:message key="next_scheduled_event" bundle="${resword}"/></p>
<div style="width: 100%">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">

<table border="0" cellpadding="3" cellspacing="0">
 <tr>
  	<td class="formlabel"><fmt:message key="study_event_definition" bundle="${resword}"/>:</td>
  	<td valign="top">
    	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<select name="studyEventDefinitionScheduled" class="formfieldXL">
						<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
						<c:forEach var="definition" items="${eventDefinitionsScheduled}">
							<c:choose>
								<c:when test="${definition.repeating}">
									<c:set var="repeating">
									 (<fmt:message key="repeating" bundle="${resword}"/>)
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="repeating">
									(<fmt:message key="non_repeating" bundle="${resword}"/>)
									</c:set>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${chosenDefinitionScheduled.id == definition.id}">
									<option value="<c:out value="${definition.id}" />" selected><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:when>
								<c:otherwise>
									<option value="<c:out value="${definition.id}"/>"><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</div>
				</td>
				<td>*</td>
			</tr>
			<tr>
			 <td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyEventDefinitionScheduled"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="formlabel"><fmt:message key="location" bundle="${resword}"/>:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<input type="text" name="locationScheduled" value="<c:out value="${locationScheduled}"/>" size="50" class="formfieldXL">
				</div>
				</td>
				<td>*<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=studyEvent&field=schLocation&column=location','spanAlert-locationScheduled'); return false;">
				<img name="flag_schLocation" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a></c:if></td>
			</tr>
			<tr>
				<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="locationScheduled"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="formlabel"><fmt:message key="start_date_time" bundle="${resword}"/>:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<c:import url="../include/showDateTimeInput.jsp"><c:param name="prefix" value="startScheduled"/><c:param name="count" value="3"/></c:import>
				<td>(<fmt:message key="date_time_format" bundle="${resformat}"/>)*<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=studyEvent&field=startScheduled&column=date_startScheduled','spanAlert-startScheduled'); return false;">
				<img name="flag_startScheduled" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a></c:if></td>
			</tr>
			<tr>
				<td colspan="7"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startScheduled"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    <tr valign="top">
		<td class="formlabel"><fmt:message key="end_date_time" bundle="${resword}"/>:</td>
		<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<c:import url="../include/showDateTimeInput.jsp"><c:param name="prefix" value="endScheduled"/><c:param name="count" value="4"/></c:import>
				<td>(<fmt:message key="date_time_format" bundle="${resformat}"/>)<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=studyEvent&field=endScheduled&column=date_endScheduled','spanAlert-endScheduled'); return false;">
				<img name="flag_endScheduled" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a></c:if></td>
			</tr>
			<tr>
				<td colspan="7">
					<fmt:message key="leave_this_field_blank_if_not_applicable" bundle="${restext}"/><br/>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endScheduled" /></jsp:include>
				</td>
			</tr>
			</table>
		</td>
	</tr>
</table>

</div>

</div></div></div></div></div></div></div></div>

</div>


<input type="submit" name="Submit" value="<fmt:message key="proceed_to_enter_data" bundle="${resword}"/>" class="button_long" />
<br>
</form>

<c:set var="role" value="${currentRole.role}" />
<c:if test="${currentRole.manageStudy}">
<c:if test="${requestStudySubject == requestStudySubjectFalse}">
<p>	<form method="POST" action="UpdateStudySubject">
	<input type="hidden" name="id" value="<c:out value="${chosenSubject.id}"/>" />
	<input type="hidden" name="action" value="show" />
	<input type="submit" name="editstudy" value="<fmt:message key="edit_subject_properties1" bundle="${resword}"/>" class="button_xlong" />
	</form>
	
<p>	<form method="POST" action="UpdateSubject">
	<input type="hidden" name="id" value="<c:out value="${chosenSubject.subjectId}"/>" />
	<input type="hidden" name="studySubjId" value="<c:out value="${chosenSubject.id}"/>" />
	<input type="hidden" name="action" value="show" />
	<input type="submit" name="editstudy" value="<fmt:message key="edit_subject_properties2" bundle="${resword}"/>" class="button_xlong" />
	</form>
</c:if>
</c:if>
<br>
<c:import url="instructionsSetupStudyEvent.jsp">
	<c:param name="currStep" value="createEvent" />
</c:import>


<jsp:include page="../include/footer.jsp"/>
