<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>	
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:include page="../include/managestudy-header.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
        <fmt:message key="enter_the_study_information_requested" bundle="${resword}"/> 
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='session' id='newStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='definitions' class='java.util.ArrayList'/>
<jsp:useBean scope='session' id='sdvOptions' class='java.util.ArrayList'/>
<jsp:useBean scope="request" id="facRecruitStatusMap" class="java.util.HashMap"/>
<jsp:useBean scope="request" id="statuses" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap"/>
<c:set var="startDate" value="" />
<c:set var="endDate" value="" />
<c:set var="protocolDateVerification" value="" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "startDate"}'>
		<c:set var="startDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "endDate"}'>
		<c:set var="endDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "protocolDateVerification"}'>
		<c:set var="protocolDateVerification" value="${presetValue.value}" />	
	</c:if>	
</c:forEach>
<script type="text/JavaScript" language="JavaScript">
  <!--
 function myCancel() {
 
    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {
        window.location.href="ListSite";
       return true;
      } else {
        return false;
       }
     }
     return true;    
   
  }
   //-->
function updateVersionSelection(vsIds, index, count) {
	var s = "vs"+count;
	var mvs = document.getElementById(s);
	if(vsIds.length>0) {
		for(i=0; i<mvs.length; ++i) {
			var t = "," + mvs.options[i].value + ",";
			if(vsIds.indexOf(t)>= 0 ) {
				mvs.options[i].selected = true;
			} else {
				mvs.options[i].selected = false;
			}
		}
		mvs.options[index].selected = true;
	} else {
		for(i=0; i<mvs.length; ++i) {
			mvs.options[i].selected = true;
		}
	}
}
//make sure current chosen default version among those selected versions.
function updateThis(multiSelEle, count) {
	var s = "dv"+count;
	var currentDefault = document.getElementById(s);
	for(i=0; i<multiSelEle.length; ++i) {
		if(multiSelEle.options[i].value == currentDefault.options[currentDefault.selectedIndex].value) {
			multiSelEle.options[i].selected = true;
		}
	}
}

     
</script>

<h1><span class="title_manage"><fmt:message key="create_a_new_site" bundle="${resword}"/> <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/study-setup')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>

<form action="CreateSubStudy" method="post">
* <fmt:message key="indicates_required_field" bundle="${resword}"/><br>
<input type="hidden" name="action" value="confirm">

<a href="javascript:leftnavExpand('siteProperties');"  class="anchor_page_section_oc">
    <img id="excl_sectiond" src="images/bt_Expand.gif" border="0"> <span class="section_link">
   <fmt:message key="create_site_properties" bundle="${resword}"/></span></a>


<div id="siteProperties" style="display:">

<table border="0" cellpadding="0" width="700"   class="shaded_table table_first_column_w30 ">
   <tr valign="top"><td ><fmt:message key="parent_study" bundle="${resword}"/>:</td><td>
   &nbsp;<c:out value="${study.name}"/>
  </td></tr>
  
  <tr valign="top"><td class="formlabel"><fmt:message key="site_name" bundle="${resword}"/>:</td><td>
  <div><input type="text" name="name" value="<c:out value="${newStudy.name}"/>" class="formfieldXL"><span class="indicates_required_field">*</span></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td></tr>
  
  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId'); return false;"><fmt:message key="unique_protocol_ID" bundle="${resword}"/></a>:</td><td>
  <div><input type="text" name="uniqueProId" value="<c:out value="${newStudy.identifier}"/>" class="formfieldXL">
  <span class="indicates_required_field">*</span></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueProId"/></jsp:include></td></tr>
  
  <tr valign="top"><td class="formlabel"><fmt:message key="secondary_IDs" bundle="${resword}"/>
  <br>(<fmt:message key="separate_by_commas" bundle="${resword}"/>):</td><td>
  <div ><textarea class="formtextareaXL4" name="secondProId" rows="4" cols="50"><c:out value="${newStudy.secondaryIdentifier}"/></textarea></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondProId"/></jsp:include>
  </td></tr>  
   
  <tr valign="top"><td class="formlabel"><fmt:message key="principal_investigator" bundle="${resword}"/>:</td><td>
  <div><input type="text" name="prinInvestigator" value="<c:out value="${newStudy.principalInvestigator}"/>" class="formfieldXL">
  <span class="indicates_required_field">*</span></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="prinInvestigator"/></jsp:include></td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="brief_summary" bundle="${resword}"/>:</td>
  <td><div>
  <textarea class="formtextareaXL4" name="description" rows="4" cols="50"><c:out value="${newStudy.summary}"/></textarea></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include></td></tr>

  <tr valign="top"><td class="formlabel">
  	<fmt:message key="protocol_verification" bundle="${resword}"/>:
  </td><td>
  <div><input type="text" name="protocolDateVerification" value="<c:out value="${protocolDateVerification}" />" class="formfieldXL" id="protocolDateVerificationField">
  <A HREF="#" >
      <img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" id="protocolDateVerificationTrigger"/>
      <script type="text/javascript">
      Calendar.setup({inputField  : "protocolDateVerificationField", ifFormat    : "<fmt:message key="date_format_calender" bundle="${resformat}"/>", button      : "protocolDateVerificationTrigger" });
      </script>

  </a>
  </div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="protocolDateVerification"/></jsp:include>
  
  </td>
  </tr>

  
  <tr valign="top"><td class="formlabel"><fmt:message key="start_date" bundle="${resword}"/>:</td><td>
  <div><input type="text" name="startDate" value="<c:out value="${startDate}" />" class="formfieldXL" id="startDateField">
 <A HREF="#" >
      <img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" id="startDateTrigger"/>
      <script type="text/javascript">
      Calendar.setup({inputField  : "startDateField", ifFormat    : "<fmt:message key="date_format_calender" bundle="${resformat}"/>", button      : "startDateTrigger" });
      </script>

  </a></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startDate"/></jsp:include>

  </td>
  </tr>
  
  <tr valign="top"><td class="formlabel"><fmt:message key="estimated_completion_date" bundle="${resword}"/>:</td>
  <td><div class="formfieldXL_BG">
  <input type="text" name="endDate" value="<c:out value="${endDate}" />" class="formfieldXL" id="endDateField">
  
  
      <A HREF="#" >
          <img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" id="endDateTrigger"/>
          <script type="text/javascript">
          Calendar.setup({inputField  : "endDateField", ifFormat    : "<fmt:message key="date_format_calender" bundle="${resformat}"/>", button      : "endDateTrigger" });
          </script>

      </a>
   </div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endDate"/></jsp:include>
      </td></tr>
  
  <tr valign="top"><td class="formlabel"><fmt:message key="expected_total_enrollment" bundle="${resword}"/>:</td>
  <td>
  <div><input type="text" name="expectedTotalEnrollment" value="<c:out value="${newStudy.expectedTotalEnrollment}"/>" class="formfieldXL">
   <span class="indicates_required_field">*</span></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="expectedTotalEnrollment"/></jsp:include>
  </td></tr>
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_name" bundle="${resword}"/>:</td><td>
  <input type="text" name="facName" value="<c:out value="${newStudy.facilityName}"/>" class="formfieldXL">
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facName"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_city" bundle="${resword}"/>:</td><td>
  <input type="text" name="facCity" value="<c:out value="${newStudy.facilityCity}"/>" class="formfieldXL">
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facCity"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_state_province" bundle="${resword}"/>:</td><td>
  <input type="text" name="facState" value="<c:out value="${newStudy.facilityState}"/>" class="formfieldXL">
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facState"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_ZIP" bundle="${resword}"/>:</td><td>
  <input type="text" name="facZip" value="<c:out value="${newStudy.facilityZip}"/>" class="formfieldXL">
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facZip"/></jsp:include>
  </td></tr>
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_country" bundle="${resword}"/>:</td><td>
  <input type="text" name="facCountry" value="<c:out value="${newStudy.facilityCountry}"/>" class="formfieldXL">
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facCountry"/></jsp:include>
  </td></tr> 
  
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_contact_name" bundle="${resword}"/>:</td><td>
  <input type="text" name="facConName" value="<c:out value="${newStudy.facilityContactName}"/>" class="formfieldXL">
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConName"/></jsp:include>
  </td></tr>   
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_contact_degree" bundle="${resword}"/>:</td><td>
  <input type="text" name="facConDegree" value="<c:out value="${newStudy.facilityContactDegree}"/>" class="formfieldXL">
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConDegree"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_contact_phone" bundle="${resword}"/>:</td><td>
  <div><input type="text" name="facConPhone" value="<c:out value="${newStudy.facilityContactPhone}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConPhone"/></jsp:include>
  </td></tr>    
  
  <tr valign="top"><td class="formlabel"><fmt:message key="facility_contact_email" bundle="${resword}"/>:</td><td>
  <div><input type="text" name="facConEmail" value="<c:out value="${newStudy.facilityContactEmail}"/>" class="formfieldXL">
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConEmail"/></jsp:include></div></td></tr>  
  
   <c:choose>
    <c:when test="${newStudy.parentStudyId == 0}">
       <c:set var="key" value="study_system_status"/>
    </c:when>
    <c:otherwise>
        <c:set var="key" value="site_system_status"/>
    </c:otherwise>
   </c:choose>

   <tr valign="top"><td class="formlabel"><fmt:message key="${key}" bundle="${resword}"/>:</td><td>
   <%--
   <c:set var="status1" value="${newStudy.status.id}"/>   
   <select name="statusId" class="formfieldXL">
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
   </select></div> --%>
  <div> <input type="text" name="statusName" value="<c:out value="${study.status.name}"/>" class="formfieldL" disabled>
   <input type="hidden" name="statusId" value="${study.status.id}"><span class="indicates_required_field">*</span></div>

   
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="statusId"/></jsp:include></td></tr>      
     
  <c:forEach var="config" items="${newStudy.studyParameters}">   
   <c:choose>
   <c:when test="${config.parameter.handle=='collectDOB'}">
     <tr valign="top"><td class="formlabel"><fmt:message key="collect_subject_date_of_birth" bundle="${resword}"/>:</td><td>
       <c:choose>
         <c:when test="${config.value.value == '1'}">
           <input type="radio" checked name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
           <input type="radio" name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
           <input type="radio" name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
         </c:when>
         <c:when test="${config.value.value == '2'}">
            <input type="radio" name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
            <input type="radio" checked name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
            <input type="radio" name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
         </c:when>
         <c:otherwise>
            <input type="radio" name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
            <input type="radio" name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
            <input type="radio" checked name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
         </c:otherwise>
      </c:choose>  
      </td></tr>
   
   </c:when>
    
   <c:when test="${config.parameter.handle=='discrepancyManagement'}">
		  <tr valign="top"><td class="formlabel"><fmt:message key="allow_discrepancy_management" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'false'}">
		    <input type="radio" name="discrepancyManagement" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="discrepancyManagement" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" checked name="discrepancyManagement" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="discrepancyManagement" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	
	<c:when test="${config.parameter.handle=='genderRequired'}">	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="gender_required" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == false}">
		    <input type="radio" name="genderRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="genderRequired" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" checked name="genderRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="genderRequired" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>	  
    <c:when test="${config.parameter.handle=='subjectPersonIdRequired'}">		
		  <tr valign="top"><td class="formlabel"><fmt:message key="subject_person_ID_required" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'required'}">
		    <input type="radio" checked name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
		    <input type="radio" name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
		    <input type="radio" name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
		   </c:when>
		    <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'optional'}">
		    <input type="radio" name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
		    <input type="radio" checked name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
		    <input type="radio" name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
		    <input type="radio" name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
		    <input type="radio" checked name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='subjectIdGeneration'}">	  
		   <tr valign="top"><td class="formlabel"><fmt:message key="how_to_generate_the_study_subject_ID" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'manual'}">
		    <input type="radio" checked name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
		   </c:when>
		    <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'auto editable'}">
		    <input type="radio" name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
		    <input type="radio" checked name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
		    <input type="radio" checked name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='subjectIdPrefixSuffix'}">	  
		   <tr valign="top"><td class="formlabel"><fmt:message key="generate_study_subject_ID_automatically" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'true'}">
		    <input type="radio" checked name="subjectIdPrefixSuffix" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="subjectIdPrefixSuffix" value="false"><fmt:message key="no" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="subjectIdPrefixSuffix" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="subjectIdPrefixSuffix" value="false"><fmt:message key="no" bundle="${resword}"/>
		   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameRequired'}">
		   <tr valign="top"><td class="formlabel"><fmt:message key="when_entering_data_entry_interviewer" bundle="${resword}"/></td><td>
               <input type="radio" <c:if test="${newStudy.studyParameterConfig.interviewerNameRequired== 'yes'}">checked</c:if> name="interviewerNameRequired" value="yes"><fmt:message key="yes" bundle="${resword}"/>
               <input type="radio" <c:if test="${newStudy.studyParameterConfig.interviewerNameRequired== 'no'}">checked</c:if> name="interviewerNameRequired" value="no"><fmt:message key="no" bundle="${resword}"/>
               <input type="radio" <c:if test="${newStudy.studyParameterConfig.interviewerNameRequired== 'not_used'}">checked</c:if> name="interviewerNameRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameDefault'}">	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_name_default_as_blank" bundle="${resword}"/></td><td>
              <c:choose>
              <c:when test="${newStudy.studyParameterConfig.interviewerNameDefault== 'blank'}">
               <input type="radio" checked name="interviewerNameDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
               <input type="radio" name="interviewerNameDefault" value="pre-populated"><fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>

              </c:when>
              <c:otherwise>
               <input type="radio" name="interviewerNameDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
               <input type="radio" checked name="interviewerNameDefault" value="re-populated"><fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>
              </c:otherwise>
             </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameEditable'}">	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_name_editable" bundle="${resword}"/></td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <input type="radio" checked name="interviewerNameEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="interviewerNameEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewerNameEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="interviewerNameEditable" value="false"><fmt:message key="no" bundle="${resword}"/>   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewDateRequired'}">	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_date_required" bundle="${resword}"/></td><td>
              <input type="radio" <c:if test="${newStudy.studyParameterConfig.interviewDateRequired== 'yes'}"> checked </c:if> name="interviewDateRequired" value="yes"><fmt:message key="yes" bundle="${resword}"/>
              <input type="radio" <c:if test="${newStudy.studyParameterConfig.interviewDateRequired== 'no'}"> checked </c:if> name="interviewDateRequired" value="no"><fmt:message key="no" bundle="${resword}"/>
              <input type="radio" <c:if test="${newStudy.studyParameterConfig.interviewDateRequired== 'not_used'}"> checked </c:if> name="interviewDateRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
		  </td>
		  </tr>
    </c:when>		  
	<c:when test="${config.parameter.handle=='interviewDateDefault'}">	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_date_default_as_blank" bundle="${resword}"/></td><td>
              <c:choose>
              <c:when test="${newStudy.studyParameterConfig.interviewDateDefault== 'blank'}">
               <input type="radio" checked name="interviewDateDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
               <input type="radio" name="interviewDateDefault" value="pre-populated"><fmt:message key="pre_populated_from_SE" bundle="${resword}"/>

              </c:when>
              <c:otherwise>
               <input type="radio" name="interviewDateDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
               <input type="radio" checked name="interviewDateDefault" value="re-populated"><fmt:message key="pre_populated_from_SE" bundle="${resword}"/>
              </c:otherwise>
             </c:choose>
		  </td>
		  </tr>
	 </c:when>
	 <c:otherwise>	  
		  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_date_editable" bundle="${resword}"/></td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <input type="radio" checked name="interviewDateEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="interviewDateEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewDateEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="interviewDateEditable" value="false"><fmt:message key="no" bundle="${resword}"/>   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
     </c:otherwise>
   </c:choose>
  </c:forEach>
</table>
</div>
<br>

<!-- events definitons starts here-->
 <h3><fmt:message key="update_site_event_definitions" bundle="${resword}"/></h3>
<c:set var="defCount" value="0"/>
<c:forEach var="def" items="${definitions}">
	<c:set var="defCount" value="${defCount+1}"/>
	<c:choose>
	<c:when test="${def.status.id==5 || def.status.id==7}">
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<div class="anchor_page_section_oc">
	<img name="ExpandGroup3" src="images/bt_Collapse.gif" border="0">
	 <c:out value="${def.name}"/> &nbsp;&nbsp;&nbsp;&nbsp; (<fmt:message key="this_removed" bundle="${resword}"/>)
	</c:when>
	<c:otherwise>
	
	&nbsp;&nbsp;&nbsp;&nbsp;<div class="table_title">
	<a class="anchor_page_section_oc" href="javascript:leftnavExpand('sed<c:out value="${defCount}"/>');">
    <img id="excl_sed<c:out value="${defCount}"/>" src="images/bt_Expand.gif" border="0">
     <c:out value="${def.name}"/></a> 
	</c:otherwise>
	</c:choose>
	<div id="sed<c:out value="${defCount}"/>" style="display: none">

	<table border="0" cellpadding="0"   class="shaded_table table_first_column_w30  cell_borders fcolumn" style="width: 100%">
	<tr><td class="table_header_column">Name</td><td><c:out value="${def.name}"/></td></tr>
		<tr><td class="table_header_column">Description</td><td><c:out value="${def.description}"/></td></tr>
	</table>
	<br>

	<div class="table_title"><fmt:message key="CRFs" bundle="${resword}"/></div>
	<table border="0" cellpadding="0"   class="shaded_table    " style="width: 100%">
	 	<c:set var="count" value="0"/>
		<c:forEach var="edc" items="${def.crfs}">
		<c:set var="num" value="${count}-${edc.id}" />
		<tr valign="top" bgcolor="#F5F5F5">
		    <td class="table_header_column" colspan="3"><c:out value="${edc.crfName}"/></td>
		</tr>

		<c:if test="${edc.status.id==1}">
		<c:choose>
	    <c:when test="${fn:length(edc.selectedVersionIds)>0}">
			<c:set var="idList" value="${edc.selectedVersionIdList}"/>
			<c:set var="selectedIds" value=",${edc.selectedVersionIds},"/>
	    </c:when>
	    <c:otherwise>
			<c:set var="idList" value=""/>
			<c:set var="selectedIds" value=""/>
		</c:otherwise>
		</c:choose>
		<tr valign="top">
	    	<td class="table_cell"><fmt:message key="required" bundle="${resword}"/>:
		    <c:choose>
	            <c:when test="${edc.requiredCRF == true}">
	                <input type="checkbox" checked name="requiredCRF<c:out value="${num}"/>" value="yes">
	            </c:when>
	            <c:otherwise>
	                <input type="checkbox" name="requiredCRF<c:out value="${num}"/>" value="yes">
	            </c:otherwise>
	        </c:choose>
	    	</td>
	    	<td></td>

		    <td class="table_cell"><fmt:message key="double_data_entry" bundle="${resword}"/>:
		    <c:choose>
	            <c:when test="${edc.doubleEntry == true}">
	                <input type="checkbox" checked name="doubleEntry<c:out value="${num}"/>" value="yes">
	            </c:when>
	            <c:otherwise>
	                <input type="checkbox" name="doubleEntry<c:out value="${num}"/>" value="yes">
            	</c:otherwise>
        	</c:choose>
		    </td>

		    <td class="table_cell"><fmt:message key="password_required" bundle="${resword}"/>:
		    <c:choose>
	            <c:when test="${edc.electronicSignature == true}">
	                <input type="checkbox" checked name="electronicSignature<c:out value="${num}"/>" value="yes">
	            </c:when>
	            <c:otherwise>
	                <input type="checkbox" name="electronicSignature<c:out value="${num}"/>" value="yes">
	            </c:otherwise>
        	</c:choose>
		    </td>

		    <td class="table_cell" colspan="2"><fmt:message key="default_version" bundle="${resword}"/>:
		    <select name="defaultVersionId<c:out value="${num}"/>" id="dv<c:out value="${num}"/>" onclick="updateVersionSelection('<c:out value="${selectedIds}"/>',document.getElementById('dv<c:out value="${num}"/>').selectedIndex, '<c:out value="${num}"/>')">
	            <c:forEach var="version" items="${edc.versions}">
	            <c:choose>
	            <c:when test="${edc.defaultVersionId == version.id}">
	            <option value="<c:out value="${version.id}"/>" selected><c:out value="${version.name}"/>
	                </c:when>
	                <c:otherwise>
	            <option value="<c:out value="${version.id}"/>"><c:out value="${version.name}"/>
	                </c:otherwise>
	                </c:choose>
	                </c:forEach>
        	</select>
		    </td>
		</tr>
		<tr valign="top">
		    <td class="table_cell" colspan="3"><fmt:message key="version_selection" bundle="${resword}"/>&nbsp:
		    <select multiple name="versionSelection<c:out value="${num}"/>" id="vs<c:out value="${num}"/>" onclick="updateThis(document.getElementById('vs<c:out value="${num}"/>'), '<c:out value="${num}"/>')" size="${fn:length(edc.versions)}">
	            <c:forEach var="version" items="${edc.versions}">
	            	<c:choose>
		            <c:when test="${fn:length(idList) > 0}">
		            	<c:set var="versionid" value=",${version.id},"/>
			            <c:choose>
		            	<c:when test="${version.id == defaultVersionId}">
		            		<option value="<c:out value="${version.id}"/>" selected><c:out value="${version.name}"/>
		            	</c:when>
		            	<c:otherwise>
		            		<c:choose>
					        <c:when test="${fn:contains(selectedIds,versionid)}">
					            <option value="<c:out value="${version.id}"/>" selected><c:out value="${version.name}"/>
					        </c:when>
					        <c:otherwise>
					            <option value="<c:out value="${version.id}"/>"><c:out value="${version.name}"/>
					        </c:otherwise>
					        </c:choose>
					    </c:otherwise>
				        </c:choose>
				    </c:when>
		            <c:otherwise>
		            	<option value="<c:out value="${version.id}"/>" selected><c:out value="${version.name}"/>
			        </c:otherwise>
			        </c:choose>
	            </c:forEach>
        	</select>
		    </td>

		    <td class="table_cell" colspan="1"><fmt:message key="hidden_crf" bundle="${resword}"/> :
		    <c:choose>
	            <c:when test="${!edc.hideCrf}">
	                <input type="checkbox" name="hideCRF<c:out value="${num}"/>" value="yes">
	            </c:when>
	            <c:otherwise><input checked="checked" type="checkbox" name="hideCRF<c:out value="${num}"/>" value="yes"></c:otherwise>
	        </c:choose>
		    </td>

		    <td class="table_cell" colspan="6"><fmt:message key="sdv_option" bundle="${resword}"/>:
		    <select name="sdvOption<c:out value="${num}"/>">
	            <c:set var="index" value="1"/>
	            <c:forEach var="sdv" items="${sdvOptions}">
	            	<c:choose>
	            	<c:when test="${edc.sourceDataVerification.code == index}">
	            		<option value="${index}" selected><c:out value="${sdv}"/>
	                </c:when>
	                <c:otherwise>
	            		<option value="${index}"><c:out value="${sdv}"/>
	                </c:otherwise>
	                </c:choose>
	            	<c:set var="index" value="${index+1}"/>
	            </c:forEach>
        	</select>
		    </td>
		</tr>
		<c:set var="count" value="${count+1}"/>
		</c:if>
		<tr><td class="table_divider" colspan="8">&nbsp;</td></tr>
		</c:forEach>
	</table><br>
	</div>
	
</c:forEach>

<br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
 <input type="submit" name="Submit" value="<fmt:message key="confirm_site" bundle="${resword}"/>" class="button_long">
</td>
<td><input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" onClick="javascript:myCancel();"/></td></td>
</tr>
</table>

</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<br><br>


<jsp:include page="../include/footer.jsp"/>
