<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/admin-header.jsp"/>
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
	     name="ExpandGroup1" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">Overview</span></a>
<div id="overview" style="display: ">	     
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column">Name:</td><td class="table_cell">
  <c:out value="${studyToView.name}"/>
  </td></tr> 
  <tr valign="top"><td class="table_header_column">Unique Protocol ID:</td><td class="table_cell">
  <c:out value="${studyToView.identifier}"/>
  </td></tr>   
  <tr valign="top"><td class="table_header_column">Principal Investigator:</td><td class="table_cell">
  <c:out value="${studyToView.principalInvestigator}"/>
  </td></tr> 
  <tr valign="top"><td class="table_header_column">Brief Summary:</td><td class="table_cell">
  <c:out value="${studyToView.summary}"/>&nbsp;
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column">Owner:</td><td class="table_cell">
  <c:out value="${studyToView.owner.name}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column">Date Created:</td><td class="table_cell">
  <fmt:formatDate value="${studyToView.createdDate}" pattern="MM/dd/yyyy"/>
  </td></tr>
 </table>  
 
 </div>
</div></div></div></div></div></div></div></div>

</div>
</div>
<br>

<a href="javascript:leftnavExpand('sectiona');javascript:setImage('ExpandGroup2','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup2" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">View Study Details: [Section A:Study Description]</span></a>
<div id="sectiona" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
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
  </table>
</div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<br>


<a href="javascript:leftnavExpand('sectionb');javascript:setImage('ExpandGroup3','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup3" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">View Study Details: [Section B:Study Status and Design]</span></a>
<div id="sectionb" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
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
</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<br>

<a href="javascript:leftnavExpand('sectionc');javascript:setImage('ExpandGroup4','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup4" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">View Study Details: [Section C:Conditions and Eligibility]</span></a>
<div id="sectionc" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
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
  </table>
</div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<br>

<a href="javascript:leftnavExpand('sectiond');javascript:setImage('ExpandGroup5','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup5" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">View Study Details: [Section D:Facility Information]</span></a>
<div id="sectiond" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">   
  
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
  </table>
  </div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<br>


 <a href="javascript:leftnavExpand('sectione');javascript:setImage('ExpandGroup6','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup6" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">View Study Details: [Section E:Related Information]</span></a>
<div id="sectione" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
  
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

</table>

</div>
</div></div></div></div></div></div></div></div>

</div>
</div>
<br>
<a href="javascript:leftnavExpand('sectionf');javascript:setImage('ExpandGroup7','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup6" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">View Study Details: [Section F:Study Parameters]</span></a>
<div id="sectionf" style="display:none ">	   
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
  
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
</div>
<br>
 <a href="javascript:leftnavExpand('sites');javascript:setImage('ExpandGroup7','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup7" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">Sites(<c:out value="${siteNum}"/> sites)</span></a>
<div id="sites" style="display: ">	 
 <div style="width: 600px"> 
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
  <td class="table_header_row_left">Name</td>
  <td class="table_header_row">Principal Investigator</td>
  <td class="table_header_row">Status</td>
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
     <c:if test="${userBean.techAdmin || userBean.sysAdmin || userRole.role.name == 'director' || userRole.role.name=='coordinator'}">
     <a href="ViewSite?id=<c:out value="${site.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
			name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
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
	     name="ExpandGroup8" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">Event Definitions(<c:out value="${defNum}"/> definitions)</span></a>
 <div id="definitions" style="display: ">	 
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
 <tr valign="top">
   <td class="table_header_row_left">Name</td>   
   <td class="table_header_row">Description</td>
   <td class="table_header_row"># of CRFs</td>
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
		    name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
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
	     name="ExpandGroup9" src="images/bt_Expand.gif" border="0">  <span class="table_title_Admin">Subjects(<c:out value="${subjectNum}"/> subjects)</span></a>
<div id="subjects" style="display: none">
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">
   <td class="table_header_row_left">Study Subject ID</td>   
   <td class="table_header_row">Gender</td>
   <td class="table_header_row">Enrollment Date</td>
   <td class="table_header_row">Events</td>
   <td class="table_header_row">&nbsp;</td> 
  </tr> 
  <c:forEach var="disSub" items="${subjectsToView}"> 
  <tr valign="top">
    <td class="table_cell_left"><c:out value="${disSub.studySubject.label}"/></td> 
      <td class="table_cell"><c:out value="${disSub.studySubject.gender}"/></td> 
      <td class="table_cell"><fmt:formatDate value="${disSub.studySubject.enrollmentDate}" pattern="MM/dd/yyyy"/></td>
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
       <tr><td><c:out value="${count}"/> events 
       <c:if test="${!empty disSub.studyEvents}">
       (
	       <c:if test="${scheduledCount>0}">	         
	         <c:out value="${scheduledCount}"/> <img src="images/icon_Scheduled.gif" alt="Scheduled" title="Scheduled">	         
            </c:if>
            
            <c:if test="${startCount>0}">                
              &nbsp;<c:out value="${startCount}"/> <img src="images/icon_InitialDE.gif" alt="Started" title="Started">              
            </c:if>        
            
            <c:if test="${completeCount>0}">              
              &nbsp;<c:out value="${completeCount}"/> <img src="images/icon_DEcomplete.gif" alt="Completed" title="Completed">               
            </c:if> 
            <c:if test="${discontinuedCount>0}">       
             &nbsp;<c:out value="${discontinuedCount}"/> <img src="images/icon_Discontinued.gif" alt="Discontinued" title="Discontinued"> 
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
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
      </td>     
  </tr>  
  </c:forEach> 
  </table>  
</div>
</div></div></div></div></div></div></div></div>

</div>
</div>
<br>

<a href="javascript:leftnavExpand('users');javascript:setImage('ExpandGroup10','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup10" src="images/bt_Expand.gif" border="0">  <span class="table_title_Admin">Users (<c:out value="${userNum}"/> users)</span></a>
<div id="users" style="display:none ">

 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">
   <td class="table_header_row_left">User Name</td>  
   <td class="table_header_row">First Name</td>  
   <td class="table_header_row">Last Name</td>  
   <td class="table_header_row">Role</td>  
   <td class="table_header_row">Study Name</td>  
   <td class="table_header_row">Status</td>  
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
        <c:if test="${userBean.techAdmin || userBean.sysAdmin || userRole.role.name == 'director' || userRole.role.name=='coordinator'}">
         <a href="ViewStudyUser?name=<c:out value="${user.userName}"/>&studyId=<c:out value="${user.studyId}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
			name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
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
