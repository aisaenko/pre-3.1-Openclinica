<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

<jsp:useBean scope="request" id="studyEvent" class="org.akaza.openclinica.bean.managestudy.StudyEventBean" />
<jsp:useBean scope="request" id="studySubject" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean" />

<c:import url="../include/managestudy-header.jsp"/>


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

<body class="aka_bodywidth" onload="<c:if test='${popUpURL != ""}'>openDNoteWindow('<c:out value="${popUpURL}" />');</c:if> ">

<script type="text/JavaScript" language="JavaScript">
  <!--
 function myCancel() {

    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {

        window.location.href="ListStudySubjects";
       return true;
      } else {
        return false;
       }
     }
     return true;

  }
   //-->
</script>
<h1><span class="title_manage"><fmt:message key="update_study_event" bundle="${resworkflow}"/></span></h1>

<form action="UpdateStudyEvent" method="post">
<input type="hidden" name="action" value="submit">
<input type="hidden" name="event_id" value="<c:out value="${studyEvent.id}"/>">
<input type="hidden" name="ss_id" value="<c:out value="${ss_id}"/>">
<input type="hidden" name="changeDate" value="<c:out value="${changeDate}"/>">

 <table border="0" cellpadding="0" width="700"   class="shaded_table table_first_column_w30 ">

  <tr valign="top"><td class="formlabel"><a href="ViewStudySubject?id=<c:out value="${studySubject.id}"/>"><fmt:message key="study_subject_ID" bundle="${resword}"/></a>:</td>
  <td style="padding-top:6px;"><c:out value="${studySubject.label}"/>
  </td></tr>
  <tr valign="top"><td class="formlabel"><fmt:message key="event" bundle="${resword}"/>:</td>
  <td style="padding-top:6px;"><c:out value="${eventDefinition.name}"/>  </td></tr>
  <tr valign="top">
  	<td class="formlabel"><fmt:message key="start_date_time" bundle="${resword}"/>:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<c:import url="../include/showDateTimeInput.jsp"><c:param name="prefix" value="start"/><c:param name="count" value="1"/></c:import>
				<td>(<fmt:message key="date_time_format" bundle="${resformat}"/>) *
                <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
                    <c:choose>
                        <c:when test="${hasStartDateNote eq 'yes'}">
                            <a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=start_date&column=start_date&strErrMsg','spanAlert-start_date'); return false;"
                                    ><img id="flag_start_date" name="flag_start_date" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${!study.status.locked}">
                                <a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=start_date&column=start_date&strErrMsg=','spanAlert-start'); return false;"><img id="flag_start_date" name="flag_start_date" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </c:if></td>
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
				<c:import url="../include/showDateTimeInput.jsp"><c:param name="prefix" value="end"/><c:param name="count" value="2"/></c:import>
				<td>(<fmt:message key="date_time_format" bundle="${resformat}"/>)
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
                    <c:choose>
                        <c:when test="${hasEndDateNote eq 'yes'}">
                            <a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=end_date&column=end_date&strErrMsg','spanAlert-start_date'); return false;"
                                    ><img id="flag_end_date" name="flag_end_date" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${!study.status.locked}">
                                <a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=end_date&column=end_date&strErrMsg=','spanAlert-start'); return false;"><img id="flag_end_date" name="flag_end_date" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                    </c:if></td>
			</tr>
			<tr>
				<td colspan="7"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="end"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    <c:if test="${study.studyParameterConfig.eventLocationRequired != 'not_used'}">
    <tr valign="top"><td class="formlabel"><fmt:message key="location" bundle="${resword}"/>:</td>
    <td>
        <table border="0" cellpadding="0" cellspacing="0">
        <tr><td>
       
      <input type="text" name="location" value="<c:out value="${studyEvent.location}"/>" class="formfieldXL">
       <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="location"/></jsp:include>
      </td>
      <td><c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
          <c:choose>
              <c:when test="${hasLocationNote eq 'yes'}">
                  <a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=location&column=location&strErrMsg','spanAlert-start_date'); return false;"
                          ><img id="flag_location" name="flag_location" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
              </c:when>
              <c:otherwise>
                  <c:if test="${!study.status.locked}">
                      <a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=location&column=location&strErrMsg=','spanAlert-start'); return false;"><img id="flag_location" name="flag_start" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
                  </c:if>
              </c:otherwise>
          </c:choose>
      </c:if>
      </td></tr>
     </table>
    </td>
    </tr>
    </c:if>
  <tr valign="top">
    <td class="formlabel"><fmt:message key="status" bundle="${resword}"/>:</td>
    <td>
          <c:choose>
         <c:when test="${studyEvent.subjectEventStatus.locked && userRole.researchAssistant}">
           <c:set var="status1" value="${studyEvent.subjectEventStatus.id}"/>
           <select class="formfieldXL" name="statusId" disabled="true">
                 <option value="<c:out value="${studyEvent.subjectEventStatus.id}"/>" selected><c:out value="${event.subjectEventStatus.name}"/>
           </select>
        </c:when>
        <c:otherwise>
            <c:set var="status1" value="${studyEvent.subjectEventStatus.id}"/>
            <select class="formfieldXL" name="statusId">
               <c:forEach var="status" items="${statuses}">
                <c:choose>
                 <c:when test="${status1 == status.id}">
                  <option value="<c:out value="${status.id}"/>" selected><c:out value="${status.name}"/>
                 </c:when>
                 <c:otherwise>
                  <option value="<c:out value="${status.id}"/>"><c:out value="${status.name}"/>
                 </c:otherwise>
                </c:choose>
             </c:forEach>
            </select>
        </c:otherwise>
        </c:choose>
   

    </td>
  </tr>

</table>

<br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
 <input type="submit" name="Submit" value="<fmt:message key="submit_changes" bundle="${resword}"/>" class="button_long">
</td>
<td><input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" onClick="javascript:myCancel();"/></td>
</tr>
</table>
</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
</body>
<br><br>


<jsp:include page="../include/footer.jsp"/>
