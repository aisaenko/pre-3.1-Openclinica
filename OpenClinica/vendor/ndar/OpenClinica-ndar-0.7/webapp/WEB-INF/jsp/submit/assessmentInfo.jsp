<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/submit-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
         Please enter data in the form provided. 
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>
  <jsp:include page="../include/submitSideInfo.jsp"/>

<jsp:useBean scope="request" id="toc" class="org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean" />

<c:set var="interviewer" value="" />
<c:set var="assessmentDate" value="" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "interviewer"}'>
		<c:set var="interviewer" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "assessmentDate"}'>
		<c:set var="assessmentDate" value="${presetValue.value}" />
	</c:if>
</c:forEach>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<SCRIPT LANGUAGE="JavaScript" type="text/javascript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" ID="js1" type="text/javascript">  
    var cal1 = new CalendarPopup("testdiv1");
</SCRIPT>
  

<c:choose>
<c:when test="${toc.action == 'ae'}">
<h1><span class="title_submit">Administrative Editing on Event CRF Data</span></h1>
  <c:import url="instructionsAdminEdit.jsp">
	<c:param name="currStep" value="eventCRFOverview" />
 </c:import>
</c:when>
<c:otherwise>
<h1><span class="title_submit">Event CRF Data Submission <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step2b')"><img src="images/bt_Help_Submit.gif" border="0" alt="Help" title="Help"></a></span></h1>
 <c:import url="instructionsEnterData.jsp">
	<c:param name="currStep" value="eventCRFOverview" />
 </c:import>
</c:otherwise>
</c:choose>



<br/>
<b>Study Event</b>: <a href="EnterDataForStudyEvent?eventId=<c:out value="${toc.studyEvent.id}"/>"><c:out value="${toc.studyEventDefinition.name}"/></a> &nbsp;
<b>Location</b>: <c:out value="${toc.studyEvent.location}"/><br>
<b>Start Date</b>: <fmt:formatDate value="${toc.studyEvent.dateStarted}" pattern="MM/dd/yyyy"/> &nbsp;
<b>End Date</b>: <fmt:formatDate value="${toc.studyEvent.dateEnded}" pattern="MM/dd/yyyy"/><br>
<b>Last Updated By</b>: <c:out value="${toc.studyEvent.updater.name}"/> (<fmt:formatDate value="${toc.studyEvent.updatedDate}" pattern="MM/dd/yyyy"/>)<br>
<b>Event CRF</b>: <c:out value="${toc.crf.name}"/> <c:out value="${toc.crfVersion.name}"/> <br>
<b>Subject Event Status</b>: <c:out value="${toc.studyEvent.subjectEventStatus.name}"/>

<p>
<c:if test='${interviewer == ""}'>
Please enter the <b>interviewer name</b> and <b>assessment date</b> before proceeding to enter data for each section of the Subject's Event CRF.
</c:if>
<c:if test="${toc.action != 'ae'}">
After data entry, you should mark the CRF as complete (even if all CRF items have been completed).
</c:if>


<form name="contentForm" action="AssessmentInfo" method="post">
<jsp:include page="../include/showSubmitted.jsp" />
<input type="hidden" name="eventCRFId" value="<c:out value="${toc.eventCRF.id}"/>" />

<div style="width: 450px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="table_header_column_top">Interviewer Name:</td>
		<td class="table_cell_top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
					<div class="formfieldM_BG">
						<input type="text" name="interviewer" size="15" value="<c:out value="${interviewer}" />" class="formfieldM"><c:if test="${study.studyParameterConfig.interviewerNameRequired=='required'}">*</c:if>
					</div>
					</td>
					<td>*<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewer&column=interviewer_name','spanAlert-interviewer'); return false;">
					<img name="flag_interviewer" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a></c:if></td>
				</tr>
				<tr>
					<td valign="top"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="interviewer"/></jsp:include></td>
				</tr>
			</table>
	  	</td>
	</tr>

	<tr valign="top">
		<td class="table_header_column">Assessment Date:</td>
	  	<td class="table_cell">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
					<div class="formfieldM_BG">
						<input type="text" name="assessmentDate" size="15" value="<c:out value="${assessmentDate}" />" class="formfieldM">
					</div>
					</td>
					<td valign="top"><a href="#" onClick="cal1.select(document.contentForm.assessmentDate,'anchor1','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.forms[2].assessmentDate,'anchor1','MM/dd/yyyy'); return false;" NAME="anchor1" ID="anchor1"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a>(MM/DD/YYYY) <c:if test="${study.studyParameterConfig.interviewDateRequired=='true'}">*</c:if>
					<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=assessmentDate&column=date_interviewed','spanAlert-assessmentDate'); return false;">
					<img name="assessmentDate" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a></c:if></td>
				</tr>
				<tr>
					<td valign="top"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="assessmentDate"/></jsp:include></td>
				</tr>
			</table>
	  	</td>
	</tr>
</table>
<!-- End Table Contents -->

</div>

</div></div></div></div></div></div></div></div>

</div>

<input type="submit" value="Submit Assessment Info" class="button_xlong" />
</form>


<jsp:include page="../include/footer.jsp"/>
