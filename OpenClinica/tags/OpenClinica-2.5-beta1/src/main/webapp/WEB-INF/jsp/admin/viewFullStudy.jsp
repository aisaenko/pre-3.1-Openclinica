<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

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
<jsp:useBean scope='request' id='studyToView' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='request' id='sitesToView' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='userRolesToView' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='subjectsToView' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='definitionsToView' class='java.util.ArrayList'/>



<script language="JavaScript">
       <!--
         function leftnavExpand(strLeftNavRowElementName){

	       var objLeftNavRowElement;

           objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
           if (objLeftNavRowElement != null) {
             if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; } 
	           objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";		
	         }
           }

       //-->
 </script>    
<h1><span class="title_Admin"><c:out value="${studyToView.name}"/></span></h1>



<a href="javascript:leftnavExpand('overview');javascript:setImage('ExpandGroup1','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup1" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin"><fmt:message key="overview" bundle="${resword}"/></span></a>
<div id="overview" style="display: ">	     
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.name}"/>
  </td></tr> 
  <tr valign="top"><td class="table_header_column"><fmt:message key="unique_protocol_ID" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.identifier}"/>
  </td></tr>
   <tr valign="top"><td class="table_header_column"><fmt:message key="OID" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.oid}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="principal_investigator" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.principalInvestigator}"/>
  </td></tr> 
  <tr valign="top"><td class="table_header_column"><fmt:message key="brief_summary" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.summary}"/>&nbsp;
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="owner" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.owner.name}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_created" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${studyToView.createdDate}" dateStyle="short"/>
  </td></tr>
 </table>  
 
 </div>
</div></div></div></div></div></div></div></div>

</div>
</div>
<br>

<a href="javascript:leftnavExpand('sectiona');javascript:setImage('ExpandGroup2','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup2" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin"><fmt:message key="view_study_details" bundle="${resword}"/>: [<fmt:message key="section" bundle="${resword}"/> A: <fmt:message key="study_description" bundle="${resword}"/>]</span></a>
<div id="sectiona" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
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
</div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<br>


<a href="javascript:leftnavExpand('sectionb');javascript:setImage('ExpandGroup3','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup3" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin"><fmt:message key="view_study_details" bundle="${resword}"/>: [<fmt:message key="section" bundle="${resword}"/> B: <fmt:message key="study_status_and_design" bundle="${resword}"/>]</span></a>
<div id="sectionb" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
 <tr valign="top"><td class="table_header_column"><fmt:message key="study_phase" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.phase}"/>
  </td></tr>   

 <tr valign="top"><td class="table_header_column"><fmt:message key="protocol_type" bundle="${resword}"/>:</td><td class="table_cell">
 <c:out value="${studyToView.protocolType}"/>
 </td></tr> 
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="protocol_verification" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${studyToView.protocolDateVerification}" dateStyle="short"/>
  </td></tr>
  
 <!-- 
  <tr valign="top"><td class="table_header_column">Collect Subject Father/Mother Information?:</td><td class="table_cell">
  <c:choose>
    <c:when test="${studyToView.genetic == true}">
     Yes
    </c:when>
    <c:otherwise>
     No
    </c:otherwise> 
   </c:choose>
 </td></tr> 
 -->
 
 
  <tr valign="top"><td class="table_header_column"><fmt:message key="start_date" bundle="${resword}"/>:</td><td class="table_cell">
   <fmt:formatDate value="${studyToView.datePlannedStart}" dateStyle="short"/>
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="estimated_completion_date" bundle="${resword}"/>:</td><td class="table_cell">
   <fmt:formatDate value="${studyToView.datePlannedEnd}" dateStyle="short"/>&nbsp;
  </td></tr>   
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="study_system_status" bundle="${resword}"/>:</td><td class="table_cell">
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
  
  <tr valign="top">
    <td class="table_header_column">
      <fmt:message key="interventions" bundle="${resword}"/>:
    </td>
    <td class="table_cell">   
      <c:out value="${studyToView.interventions}"/>&nbsp;   
    </td>
  </tr> 
  
  </c:when> 
  <c:otherwise>
  <tr valign="top"><td class="table_header_column"<fmt:message key="duration" bundle="${resword}"/>:</td><td class="table_cell">
   <c:out value="${studyToView.duration}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="selection" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.selection}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="timing" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.timing}"/>&nbsp;
  </td></tr>  
  
  </c:otherwise>
  </c:choose> 
</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<br>

<a href="javascript:leftnavExpand('sectionc');javascript:setImage('ExpandGroup4','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup4" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin"><fmt:message key="view_study_details" bundle="${resword}"/>: [<fmt:message key="section" bundle="${resword}"/> C: <fmt:message key="conditions_and_eligibility" bundle="${resword}"/>]</span></a>
<div id="sectionc" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
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
</div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<br>

<a href="javascript:leftnavExpand('sectiond');javascript:setImage('ExpandGroup5','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup5" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin"><fmt:message key="view_study_details" bundle="${resword}"/>: [<fmt:message key="section" bundle="${resword}"/> D: <fmt:message key="facility_information" bundle="${resword}"/>]</span></a>
<div id="sectiond" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">   
  
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
  </div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<br>


 <a href="javascript:leftnavExpand('sectione');javascript:setImage('ExpandGroup6','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup6" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin"><fmt:message key="view_study_details" bundle="${resword}"/>: [<fmt:message key="section" bundle="${resword}"/> E: <fmt:message key="related_infomation" bundle="${resword}"/>]</span></a>
<div id="sectione" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="medline_identifier_references" bundle="${resword}"/>:</td><td class="table_cell">
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
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="URL_description" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToView.urlDescription}"/>&nbsp;
  </td></tr>  

</table>

</div>
</div></div></div></div></div></div></div></div>

</div>
</div>
<br>
<a href="javascript:leftnavExpand('sectionf');javascript:setImage('ExpandGroup7','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup6" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin"><fmt:message key="view_study_details" bundle="${resword}"/>: [<fmt:message key="section" bundle="${resword}"/> F: <fmt:message key="study_parameter_configuration" bundle="${resword}"/>]</span></a>
<div id="sectionf" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
  
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
  <fmt:message key="yes" bundle="${resword}"/>	
   </c:when>
   <c:otherwise>
   <fmt:message key="no" bundle="${resword}"/>
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
  
  <!--
   <tr valign="top"><td class="table_header_column"><fmt:message key="generate_study_subject_ID_automatically" bundle="${resword}"/>:</td>
   <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.subjectIdPrefixSuffix == 'true'}">
    Yes
   </c:when>    
   <c:otherwise> 
     No   
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
  -->
  
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

  

   <tr valign="top"><td class="table_header_column"><fmt:message key="when_entering_data_entry_interviewer" bundle="${resword}"/>:</td>

   <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewerNameRequired== true}">
    <fmt:message key="yes" bundle="${resword}"/>
   </c:when>    
   <c:otherwise>
   <fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr> 


  <tr valign="top"><td class="table_header_column"><fmt:message key="interviewer_name_default_as_blank" bundle="${restext}"/>:</td>

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

</table>

</div>
</div></div></div></div></div></div></div></div>

</div>
</div>
<br>
 <a href="javascript:leftnavExpand('sites');javascript:setImage('ExpandGroup7','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup7" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin"><fmt:message key="sites" bundle="${resword}"/>(<c:out value="${siteNum}"/> <fmt:message key="sites" bundle="${resword}"/>)</span></a>
<div id="sites" style="display: ">	 
 <div style="width: 600px"> 
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
  <td class="table_header_row_left"><fmt:message key="name" bundle="${resword}"/></td>
  <td class="table_header_row"><fmt:message key="principal_investigator" bundle="${resword}"/></td>
  <td class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>
  <td class="table_header_row">&nbsp;</td> 
  <td></td>
  </tr>
  <c:forEach var="site" items="${sitesToView}">
  <tr valign="top">
   <td class="table_cell_left"><c:out value="${site.name}"/></td>
   <td class="table_cell">
    <c:out value="${site.principalInvestigator}"/>
   </td>
   <td class="table_cell"><c:out value="${site.status.name}"/></td>
   <td class="table_cell">
     <c:if test="${userBean.techAdmin || userBean.sysAdmin || userRole.manageStudy}">
     <a href="ViewSite?id=<c:out value="${site.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
			name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
     </c:if>
   </td>   
  </tr>  
  </c:forEach> 
  </table>  
</div>
</div></div></div></div></div></div></div></div>

</div>
</div>
<br>

 <a href="javascript:leftnavExpand('definitions');javascript:setImage('ExpandGroup8','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup8" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin"><fmt:message key="event_definitions" bundle="${resword}"/>(<c:out value="${defNum}"/> <fmt:message key="definitions" bundle="${resword}"/>)</span></a>
 <div id="definitions" style="display: ">	 
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
 <tr valign="top">
   <td class="table_header_row_left"><fmt:message key="name" bundle="${resword}"/></td>   
   <td class="table_header_row"><fmt:message key="description" bundle="${resword}"/></td>
   <td class="table_header_row"><fmt:message key="of_CRFs" bundle="${resword}"/></td>
   <td class="table_header_row">&nbsp;</td> 
   <td></td>
  </tr>  
  <c:forEach var="definition" items="${definitionsToView}">
   <tr>
   <td class="table_cell_left">
    <c:out value="${definition.name}"/>
   </td>
  <td class="table_cell">
    <c:out value="${definition.description}"/>&nbsp;
   </td>
  <td class="table_cell">
   <c:out value="${definition.crfNum}"/>&nbsp;   
   </td>
   <td class="table_cell">
	   <a href="ViewEventDefinition?id=<c:out value="${definition.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		    name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
	</td>
  </tr>  
  </c:forEach>  
  </table> 
</div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<br>

<a href="javascript:leftnavExpand('subjects');javascript:setImage('ExpandGroup9','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup9" src="images/bt_Expand.gif" border="0">  <span class="table_title_Admin"><fmt:message key="subjects" bundle="${resword}"/>(<c:out value="${subjectNum}"/> <fmt:message key="subjects" bundle="${resword}"/>)</span></a>
<%--
<div id="subjects" style="display: none">
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">
   <td class="table_header_row_left"><fmt:message key="study_subject_ID" bundle="${resword}"/></td>   
   <td class="table_header_row"><fmt:message key="gender" bundle="${resword}"/></td>
   <td class="table_header_row"><fmt:message key="enrollment_date" bundle="${resword}"/></td>
   <td class="table_header_row"><fmt:message key="events" bundle="${resword}"/></td>
   <td class="table_header_row">&nbsp;</td> 
  </tr> 
  <c:forEach var="disSub" items="${subjectsToView}"> 
  <tr valign="top">
    <td class="table_cell_left"><c:out value="${disSub.studySubject.label}"/></td> 
      <td class="table_cell"><c:out value="${disSub.studySubject.gender}"/></td> 
      <td class="table_cell"><fmt:formatDate value="${disSub.studySubject.enrollmentDate}" dateStyle="short"/></td>
      <td class="table_cell">
      <c:set var="count" value="0"/>
      <c:set var="scheduledCount" value="0"/>
      <c:set var="startCount" value="0"/>     
      <c:set var="completeCount" value="0"/>
      <c:set var="discontinuedCount" value="0"/>
     
      <c:forEach var="event" items="${disSub.studyEvents}">          
        <c:set var="count" value="${count+1}"/>
        <c:choose>
        <c:when test="${event.subjectEventStatus.id==1}">
         <c:set var="scheduledCount" value="${scheduledCount+1}"/>
        </c:when>
        <c:when test="${event.subjectEventStatus.id==3}">
         <c:set var="startCount" value="${startCount+1}"/>
        </c:when>
        <c:when test="${event.subjectEventStatus.id==4}">
         <c:set var="completeCount" value="${completeCount+1}"/>
        </c:when>  
        <c:when test="${event.subjectEventStatus.id>4}">
         <c:set var="discontinuedCount" value="${discontinuedCount+1}"/>
        </c:when>       
        </c:choose>
       </c:forEach>
      <table>
       <tr><td><c:out value="${count}"/> <fmt:message key="events" bundle="${resword}"/>
       <c:if test="${!empty disSub.studyEvents}">
       (
	       <c:if test="${scheduledCount>0}">	         
	         <c:out value="${scheduledCount}"/> <img src="images/icon_Scheduled.gif" alt="<fmt:message key="scheduled" bundle="${resword}"/>" title="<fmt:message key="scheduled" bundle="${resword}"/>">	         
            </c:if>
            
            <c:if test="${startCount>0}">                
              &nbsp;<c:out value="${startCount}"/> <img src="images/icon_InitialDE.gif" alt="<fmt:message key="started" bundle="${resword}"/>" title="<fmt:message key="started" bundle="${resword}"/>">              
            </c:if>        
            
            <c:if test="${completeCount>0}">              
              &nbsp;<c:out value="${completeCount}"/> <img src="images/icon_DEcomplete.gif" alt="<fmt:message key="completed" bundle="${resword}"/>" title="<fmt:message key="completed" bundle="${resword}"/>">               
            </c:if> 
            <c:if test="${discontinuedCount>0}">       
             &nbsp;<c:out value="${discontinuedCount}"/> <img src="images/icon_Discontinued.gif" alt="<fmt:message key="discontinued" bundle="${resword}"/>" title="<fmt:message key="discontinued" bundle="${resword}"/>"> 
            </c:if>            
	       )  
       </c:if> 
      </td></tr>
      </table>
      </td> 
      <td class="table_cell">
       <a href="ViewStudySubject?id=<c:out value="${disSub.studySubject.id}"/>"
	  onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
	  onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
      </td>     
  </tr>  
  </c:forEach> 
  </table>  
</div>
</div></div></div></div></div></div></div></div>

</div>
</div>

--%>
<br>
<a href="javascript:leftnavExpand('users');javascript:setImage('ExpandGroup10','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup10" src="images/bt_Expand.gif" border="0">  <span class="table_title_Admin"><fmt:message key="users" bundle="${resword}"/>(<c:out value="${userNum}"/> <fmt:message key="users" bundle="${resword}"/>)</span></a>
<div id="users" style="display:none ">

 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">
   <td class="table_header_row_left"><fmt:message key="user_name" bundle="${resword}"/></td>  
   <td class="table_header_row"><fmt:message key="first_name" bundle="${resword}"/></td>  
   <td class="table_header_row"><fmt:message key="last_name" bundle="${resword}"/></td>  
   <td class="table_header_row"><fmt:message key="role" bundle="${resword}"/></td>  
   <td class="table_header_row"><fmt:message key="study_name" bundle="${resword}"/></td>  
   <td class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>  
   <td class="table_header_row">&nbsp;</td>     
  </tr>  
  <c:forEach var="user" items="${userRolesToView}">
  <tr valign="top">
   <td class="table_cell_left">
    <c:out value="${user.userName}"/>
   </td>  
    <td class="table_cell"><c:out value="${user.firstName}"/></td>  
      <td class="table_cell"><c:out value="${user.lastName}"/></td>
      <td class="table_cell"><c:out value="${user.role.description}"/></td>
      <td class="table_cell"><c:out value="${user.studyName}"/></td>
      <td class="table_cell"><c:out value="${user.status.name}"/></td>
      <td class="table_cell">
        <c:if test="${userBean.techAdmin || userBean.sysAdmin || userRole.manageStudy}">
         <a href="ViewStudyUser?name=<c:out value="${user.userName}"/>&studyId=<c:out value="${user.studyId}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
			name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
		</c:if> 
      </td>
  </tr>  
  </c:forEach>   
  </table> 
</div>
</div></div></div></div></div></div></div></div>

</div> 
</div>

 <c:import url="../include/workflow.jsp">
  <c:param name="module" value="admin"/>
 </c:import>
<jsp:include page="../include/footer.jsp"/>
