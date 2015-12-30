<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

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

<jsp:useBean scope='request' id='studyToView' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<h1><div class="title_manage"><fmt:message key="view_study_metadata" bundle="${resword}"/>
</div></h1>

 <span class="section_link"> <fmt:message key="study_description" bundle="${resword}"/></span>
  
<table border="0" cellpadding="0"    class="shaded_table table_first_column_w30 cell_borders fcolumn ">

  <tr valign="top"><td class="table_header_column"><fmt:message key="brief_title" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.name}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="official_title" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.officialTitle}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="unique_protocol_ID" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.identifier}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="secondary_IDs" bundle="${resword}"/>:</td><td class="table_cell">
   <c:out value="${studyToView.secondaryIdentifier}"/>&nbsp;
   </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="principal_investigator" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.principalInvestigator}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="brief_summary" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.summary}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="protocol_detailed_description" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.protocolDescription}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="sponsor" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.sponsor}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="collaborators" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.collaborators}"/>&nbsp;
  </td></tr>
 </table>
<br>


   <span class="section_link"> <fmt:message key="study_status_and_design" bundle="${resword}"/>    </span>
  <table border="0" cellpadding="0"    class="shaded_table table_first_column_w30 cell_borders fcolumn ">
   
 
  <tr valign="top"><td class="table_header_column"><fmt:message key="study_phase" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.phase}"/>
  </td></tr>

 <tr valign="top"><td class="table_header_column"><fmt:message key="protocol_type" bundle="${resword}"/>:</td><td class="table_cell">
 <c:out value="${studyToView.protocolType}"/>
 </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="protocol_verification" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${studyToView.protocolDateVerification}" pattern="${dteFormat}"/>
  </td></tr>

 

  <tr valign="top"><td class="table_header_column"><fmt:message key="start_date" bundle="${resword}"/>:</td><td class="table_cell">
   <fmt:formatDate value="${studyToView.datePlannedStart}" pattern="${dteFormat}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="estimated_completion_date" bundle="${resword}"/>:</td><td class="table_cell">
   <fmt:formatDate value="${studyToView.datePlannedEnd}" pattern="${dteFormat}"/>&nbsp;
  </td></tr>

  <c:choose>
   <c:when test="${studyToView.parentStudyId == 0}">
      <c:set var="key" value="study_system_status"/>
   </c:when>
   <c:otherwise>
       <c:set var="key" value="site_system_status"/>
   </c:otherwise>
  </c:choose>

  <tr valign="top"><td class="table_header_column"><fmt:message key="${key}" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.status.name}"/>
   </td></tr>


  <tr valign="top"><td class="table_header_column"><fmt:message key="purpose" bundle="${resword}"/>:</td><td class="table_cell">
   <c:out value="${studyToView.purpose}"/>
  </td></tr>

  <c:choose>
  <c:when test="${studyToView.protocolTypeKey=='interventional'}">

  <tr valign="top"><td class="table_header_column"><fmt:message key="allocation" bundle="${resword}"/>:</td><td class="table_cell">
   <c:out value="${studyToView.allocation}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="masking" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${studyToView.masking}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="control" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${studyToView.control}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="assignment" bundle="${resword}"/>:</td><td class="table_cell">
   <c:out value="${studyToView.assignment}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="endpoint" bundle="${resword}"/>:</td><td class="table_cell">
   <c:out value="${studyToView.endpoint}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="interventions" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${studyToView.interventions}"/>&nbsp;


  </td></tr>

  </c:when>
  <c:otherwise>
  <tr valign="top"><td class="table_header_column"><fmt:message key="duration" bundle="${resword}"/>:</td><td class="table_cell">
   <c:out value="${studyToView.duration}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="selection" bundle="${resword}"/></td><td class="table_cell">
  <c:out value="${studyToView.selection}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="timing" bundle="${resword}"/></td><td class="table_cell">
  <c:out value="${studyToView.timing}"/>&nbsp;
  </td></tr>

  </c:otherwise>
  </c:choose>
</table>
<br>
  <span class="section_link"><fmt:message key="conditions_and_eligibility" bundle="${resword}"/>  </span>
<table border="0" cellpadding="0" width="700"   class="shaded_table table_first_column_w30  cell_borders fcolumn">
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="conditions" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.conditions}"/>&nbsp;
 </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="keywords" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.keywords}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="eligibility" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.eligibility}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="gender" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${studyToView.gender}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="minimun_age" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.ageMin}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="maximun_age" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.ageMax}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="healthy_volunteers_accepted" bundle="${resword}"/>:</td><td class="table_cell">
  <c:choose>
    <c:when test="${studyToView.healthyVolunteerAccepted == true}">
    <fmt:message key="yes" bundle="${resword}"/>
    </c:when>
    <c:otherwise>
     <fmt:message key="no" bundle="${resword}"/>
    </c:otherwise>
   </c:choose>
 </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="expected_total_enrollment" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.expectedTotalEnrollment}"/>&nbsp;
  </td></tr>

  
   </table>
<br>

 <span class="section_link">   <fmt:message key="facility_information" bundle="${resword}"/>    </span>
 
<table border="0" cellpadding="0" width="700"   class="shaded_table table_first_column_w30  cell_borders fcolumn">
  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.facilityName}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_city" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.facilityCity}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_state_province" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.facilityState}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_ZIP" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.facilityZip}"/>&nbsp;
 </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_country" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.facilityCountry}"/>&nbsp;
  </td></tr>

  <!--<tr valign="top"><td class="table_header_column"><fmt:message key="facility_recruitment_status" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.facilityRecruitmentStatus}"/>&nbsp;
 </td></tr> -->

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.facilityContactName}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_degree" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.facilityContactDegree}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_phone" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.facilityContactPhone}"/>&nbsp;
 </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_email" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.facilityContactEmail}"/>&nbsp;
  </td></tr>

 </table>
<br>


  <span class="section_link">  <fmt:message key="related_infomation" bundle="${resword}"/></span>
 <table border="0" cellpadding="0" class="shaded_table table_first_column_w30  cell_borders fcolumn">
 

  <tr valign="top"><td class="table_header_column"><fmt:message key="medline_identifier_references" bundle="${resword}"/></td><td class="table_cell">
  <c:out value="${studyToView.medlineIdentifier}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="results_reference" bundle="${resword}"/></td><td class="table_cell">
  <c:choose>
    <c:when test="${studyToView.resultsReference == true}">
     <fmt:message key="yes" bundle="${resword}"/>
    </c:when>
    <c:otherwise>
      <fmt:message key="no" bundle="${resword}"/>
    </c:otherwise>
   </c:choose>
 </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="URL_reference" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.url}"/>&nbsp;
 </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="URL_description" bundle="${resword}"/></td><td class="table_cell">
  <c:out value="${studyToView.urlDescription}"/>&nbsp;
  </td></tr>
</table>
<br>
<span class="section_link">    <fmt:message key="study_parameter_configuration" bundle="${resword}"/>  </span>  
<table border="0" cellpadding="0" class="shaded_table table_first_column_w30  cell_borders fcolumn">

  <tr valign="top"><td class="table_header_column"><fmt:message key="collect_subject" bundle="${resword}"/></td>
   <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.collectDob == '1'}">
  <fmt:message key="yes" bundle="${resword}"/>
   </c:when>
   <c:when test="${studyToView.studyParameterConfig.collectDob == '2'}">
       <fmt:message key="only_year_of_birth" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <fmt:message key="not_used" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>

  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="allow_discrepancy_management" bundle="${resword}"/></td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.discrepancyManagement == 'true'}">
    <fmt:message key="no" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <fmt:message key="yes" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="gender_required" bundle="${resword}"/></td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.genderRequired == 'false'}">
   <fmt:message key="no" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
  <fmt:message key="yes" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="subject_person_ID_required" bundle="${resword}"/></td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.subjectPersonIdRequired == 'required'}">
    <fmt:message key="required" bundle="${resword}"/>
   </c:when>
    <c:when test="${studyToView.studyParameterConfig.subjectPersonIdRequired == 'optional'}">
    <fmt:message key="optional" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
     <fmt:message key="not_used" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

   <tr valign="top"><td class="table_header_column"><fmt:message key="how_generete_study_subject_ID" bundle="${restext}"/></td>
   <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.subjectIdGeneration == 'manual'}">
    <fmt:message key="manual_entry" bundle="${resword}"/>
   </c:when>
    <c:when test="${studyToView.studyParameterConfig.subjectIdGeneration == 'auto editable'}">
    <fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
     <fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>


 

 <tr valign="top"><td class="table_header_column"><fmt:message key="show_person_id_on_crf_header" bundle="${resword}"/></td>
   <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.personIdShownOnCRF == 'true'}">
    <fmt:message key="yes" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

   <tr valign="top"><td class="table_header_column"><fmt:message key="entering_data_interviewer_name_required" bundle="${restext}"/></td>
   <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewerNameRequired== 'true'}">
  <fmt:message key="yes" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
   <fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>


   <tr valign="top"><td class="table_header_column"><fmt:message key="interviewer_name_default_as_blank" bundle="${restext}"/></td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewerNameDefault== 'blank'}">
   <fmt:message key="blank" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
   <fmt:message key="pre_populated_from_study_event" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="interviewer_name_editable" bundle="${restext}"/></td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewerNameEditable== 'true'}">
  <fmt:message key="yes" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
   <fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="interview_date_required" bundle="${restext}"/></td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewDateRequired== 'true'}">
  <fmt:message key="yes" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
   <fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="interview_date_default_as_blank" bundle="${restext}"/></td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewDateDefault== 'blank'}">
   <fmt:message key="blank" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
   <fmt:message key="pre_populated_from_study_event" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="interview_date_editable" bundle="${restext}"/></td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewDateEditable== 'true'}">
  <fmt:message key="yes" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
   <fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="forced_reason_for_change" bundle="${resword}"/></td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.adminForcedReasonForChange == 'true'}">
  <fmt:message key="yes" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
   <fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

</table>

<br><br>

<jsp:include page="../include/footer.jsp"/>
