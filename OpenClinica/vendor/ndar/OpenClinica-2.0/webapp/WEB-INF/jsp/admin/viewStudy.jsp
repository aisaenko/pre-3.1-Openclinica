<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:choose>
 <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
   <c:import url="../include/admin-header.jsp"/>
 </c:when>
 <c:otherwise>
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
    <c:import url="../include/managestudy-header.jsp"/>
   </c:when>
   <c:otherwise>
    <c:import url="../include/home-header.jsp"/>
   </c:otherwise> 
  </c:choose>
 </c:otherwise>
</c:choose>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		  
        
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='studyToView' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<h1><c:choose>
 <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
  <div class="title_Admin">
</c:when>
 <c:otherwise>
 
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
     <div class="title_manage">
   </c:when>
   <c:otherwise>
    <div class="title_home">
   </c:otherwise> 
  </c:choose>
    
 </c:otherwise>
</c:choose>
View Study Metadata
</div></h1>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2"><b>SECTION A: Study Description</b></td></tr>
  <tr valign="top"><td class="table_header_column">Brief Title:</td><td class="table_cell">
  <c:out value="${studyToView.name}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Official Title:</td><td class="table_cell">
  <c:out value="${studyToView.officialTitle}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Unique Protocol ID:</td><td class="table_cell">
  <c:out value="${studyToView.identifier}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Secondary IDs:</td><td class="table_cell">
   <c:out value="${studyToView.secondaryIdentifier}"/>&nbsp;
   </td></tr>  
   
  <tr valign="top"><td class="table_header_column">Principal Investigator:</td><td class="table_cell">
  <c:out value="${studyToView.principalInvestigator}"/>
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Brief Summary:</td><td class="table_cell">
  <c:out value="${studyToView.summary}"/>
  </td></tr>    
   
  <tr valign="top"><td class="table_header_column">Protocol Detailed Description:</td><td class="table_cell">
  <c:out value="${studyToView.protocolDescription}"/>&nbsp;
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column">Sponsor:</td><td class="table_cell">
  <c:out value="${studyToView.sponsor}"/>
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Collaborators:</td><td class="table_cell">
  <c:out value="${studyToView.collaborators}"/>&nbsp;
  </td></tr> 
  
  <tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2"><b>SECTION B: Study Status and Design</b></td></tr>
  
  <tr valign="top"><td class="table_header_column">Study Phase:</td><td class="table_cell">
  <c:out value="${studyToView.phase}"/>
  </td></tr>   

 <tr valign="top"><td class="table_header_column">Protocol Type:</td><td class="table_cell">
 <c:out value="${studyToView.protocolType}"/>
 </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Protocol Verification/IRB Approval Date:</td><td class="table_cell">
  <fmt:formatDate value="${studyToView.protocolDateVerification}" pattern="MM/dd/yyyy"/>
  </td></tr>
  
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
 
  
  <tr valign="top"><td class="table_header_column">Start Date:</td><td class="table_cell">
   <fmt:formatDate value="${studyToView.datePlannedStart}" pattern="MM/dd/yyyy"/>
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Estimated Completion Date:</td><td class="table_cell">
   <fmt:formatDate value="${studyToView.datePlannedEnd}" pattern="MM/dd/yyyy"/>&nbsp;
  </td></tr>   
  
  <tr valign="top"><td class="table_header_column">Study System Status:</td><td class="table_cell">
  <c:out value="${studyToView.status.name}"/>
   </td></tr>   
     

  <tr valign="top"><td class="table_header_column">Purpose:</td><td class="table_cell">
   <c:out value="${studyToView.purpose}"/>
  </td></tr>
  
  <c:choose>
  <c:when test="${studyToView.protocolType=='interventional'}">
  
  <tr valign="top"><td class="table_header_column">Allocation:</td><td class="table_cell">
   <c:out value="${studyToView.allocation}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Masking:</td><td class="table_cell">
    <c:out value="${studyToView.masking}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Control:</td><td class="table_cell">
    <c:out value="${studyToView.control}"/>&nbsp;
  </td></tr>  
   
  <tr valign="top"><td class="table_header_column">Assignment:</td><td class="table_cell">
   <c:out value="${studyToView.assignment}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Endpoint:</td><td class="table_cell">
   <c:out value="${studyToView.endpoint}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Interventions:</td><td class="table_cell">   
    <c:out value="${studyToView.interventions}"/>&nbsp;   
     
   
  </td></tr> 
  
  </c:when> 
  <c:otherwise>
  <tr valign="top"><td class="table_header_column">Duration:</td><td class="table_cell">
   <c:out value="${studyToView.duration}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Selection:</td><td class="table_cell">
  <c:out value="${studyToView.selection}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Timing:</td><td class="table_cell">
  <c:out value="${studyToView.timing}"/>&nbsp;
  </td></tr>  
  
  </c:otherwise>
  </c:choose> 
  
  <tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2"><b>Section C: Conditions and Eligibility</b></td></tr>
  
  <tr valign="top"><td class="table_header_column">Conditions:</td><td class="table_cell">
  <c:out value="${studyToView.conditions}"/>&nbsp;
 </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Keywords:</td><td class="table_cell">
  <c:out value="${studyToView.keywords}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Eligibility:</td><td class="table_cell">
  <c:out value="${studyToView.eligibility}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Gender:</td><td class="table_cell">  
    <c:out value="${studyToView.gender}"/>&nbsp;    
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column">Minimum Age:</td><td class="table_cell">
  <c:out value="${studyToView.ageMin}"/>&nbsp;
  </td></tr>   
  
  <tr valign="top"><td class="table_header_column">Maximum Age:</td><td class="table_cell">
  <c:out value="${studyToView.ageMax}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Healthy Volunteers Accepted:</td><td class="table_cell">
  <c:choose>
    <c:when test="${studyToView.healthyVolunteerAccepted == true}">
     Yes
    </c:when>
    <c:otherwise>
     No
    </c:otherwise> 
   </c:choose>
 </td></tr>  
  
  <tr valign="top"><td class="table_header_column">Expected total enrollment:</td><td class="table_cell">
  <c:out value="${studyToView.expectedTotalEnrollment}"/>&nbsp;
  </td></tr>   
 
  <tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2"><b>Section D: Facility Information</b></td></tr>      
  
  <tr valign="top"><td class="table_header_column">Facility Name:</td><td class="table_cell">
  <c:out value="${studyToView.facilityName}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility City:</td><td class="table_cell">
  <c:out value="${studyToView.facilityCity}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility State/Province:</td><td class="table_cell">
  <c:out value="${studyToView.facilityState}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility ZIP:</td><td class="table_cell">
  <c:out value="${studyToView.facilityZip}"/>&nbsp;
 </td></tr>
  
  <tr valign="top"><td class="table_header_column">Facility Country:</td><td class="table_cell">
  <c:out value="${studyToView.facilityCountry}"/>&nbsp;
  </td></tr> 
  
  <!--<tr valign="top"><td class="table_header_column">Facility Recruitment Status:</td><td class="table_cell">
  <c:out value="${studyToView.facilityRecruitmentStatus}"/>&nbsp;
 </td></tr> --> 
  
  <tr valign="top"><td class="table_header_column">Facility Contact Name:</td><td class="table_cell">
  <c:out value="${studyToView.facilityContactName}"/>&nbsp;
  </td></tr>   
  
  <tr valign="top"><td class="table_header_column">Facility Contact Degree:</td><td class="table_cell">
  <c:out value="${studyToView.facilityContactDegree}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility Contact Phone:</td><td class="table_cell">
  <c:out value="${studyToView.facilityContactPhone}"/>&nbsp;
 </td></tr>       
  
  <tr valign="top"><td class="table_header_column">Facility Contact Email:</td><td class="table_cell">
  <c:out value="${studyToView.facilityContactEmail}"/>&nbsp;
  </td></tr>  
  
  
 
   <tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2" ><b>Section E: Related Information</b></td></tr> 
 
  
  <tr valign="top"><td class="table_header_column">MEDLINE Identifier References:</td><td class="table_cell">
  <c:out value="${studyToView.medlineIdentifier}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Results Reference?</td><td class="table_cell">
  <c:choose>
    <c:when test="${studyToView.resultsReference == true}">
     Yes
    </c:when>
    <c:otherwise>
     No
    </c:otherwise> 
   </c:choose>
 </td></tr>  
  
  <tr valign="top"><td class="table_header_column">URL Reference:</td><td class="table_cell">
  <c:out value="${studyToView.url}"/>&nbsp;
 </td></tr> 
  
  <tr valign="top"><td class="table_header_column">URL Description:</td><td class="table_cell">
  <c:out value="${studyToView.urlDescription}"/>&nbsp;
  </td></tr>  

<tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2">SECTION F: Study Parameter Configuration</td></tr> 
  
  <tr valign="top"><td class="table_header_column">Collect Subject Date of Birth?:</td>
   <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.collectDob == 1}">
   Yes   
   </c:when>
   <c:when test="${studyToView.studyParameterConfig.collectDob == 2}">    
    only Year of Birth 
   </c:when>
   <c:otherwise>
    Not Used
   </c:otherwise>
  </c:choose>
  
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Allow Discrepancy Management?:</td>
  <td class="table_cell">  
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.discrepancyManagement == false}">
    Yes
   </c:when>
   <c:otherwise>
    No
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
  
  <tr valign="top"><td class="table_header_column">Gender Required?:</td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.genderRequired == false}">
    No
   </c:when>
   <c:otherwise>
   Yes   
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
 
  <tr valign="top"><td class="table_header_column">Subject Person ID Required?:</td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.subjectPersonIdRequired == 'required'}">
    Required    
   </c:when>
    <c:when test="${studyToView.studyParameterConfig.subjectPersonIdRequired == 'optional'}">
     Optional    
   </c:when>
   <c:otherwise>  
    Not Used
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
  
   <tr valign="top"><td class="table_header_column">How to Generate the Study Subject ID?:</td>
   <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.subjectIdGeneration == 'manual'}">
    Manual Entry    
   </c:when>
    <c:when test="${studyToView.studyParameterConfig.subjectIdGeneration == 'auto editable'}">
    Auto-generated and Editable   
   </c:when>
   <c:otherwise>   
    Auto-generated and Non-editable
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
  
  
  <!--
   <tr valign="top"><td class="table_header_column">Generate Study Subject ID Automatically With Prefix/Suffix?:</td>
   <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.subjectIdPrefixSuffix == true}">
    Yes
   </c:when>    
   <c:otherwise> 
     No   
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
 -->
 
   <tr valign="top"><td class="table_header_column">When Entering Data Entry, Interviewer Name Required?</td>
   <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewerNameRequired== true}">
   Yes   
   </c:when>    
   <c:otherwise>
    No   
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
  
  <tr valign="top"><td class="table_header_column">Interviewer Name Default as Blank?</td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewerNameDefault== 'blank'}">
    Blank   
   </c:when>    
   <c:otherwise>
   Pre-Populated from Study Event   
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
  
  <tr valign="top"><td class="table_header_column">Interviewer Name Editable?</td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewerNameEditable== true}">
   Yes   
   </c:when>    
   <c:otherwise>    
   No   
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
  
  <tr valign="top"><td class="table_header_column">Interview Date Required?</td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewDateRequired== true}">
   Yes
   </c:when>    
   <c:otherwise>   
    No   
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
  
  <tr valign="top"><td class="table_header_column">Interview Date Default as Blank?</td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewDateDefault== 'blank'}">
    Blank
   </c:when>    
   <c:otherwise>
    Pre-Populated from Study Event   
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
  
  <tr valign="top"><td class="table_header_column">Interview Date Editable?</td>
  <td class="table_cell">
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.interviewDateEditable== true}">
   Yes   
   </c:when>    
   <c:otherwise>
   No   
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
</table>

</div>
</div></div></div></div></div></div></div></div>

</div>
 <c:import url="../include/workflow.jsp">
  <c:param name="module" value="admin"/>
 </c:import>
<jsp:include page="../include/footer.jsp"/>
