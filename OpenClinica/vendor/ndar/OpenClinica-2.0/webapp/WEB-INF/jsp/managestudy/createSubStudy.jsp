<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
        Enter the Study Information requested to create the site. 
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='session' id='newStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="facRecruitStatusMap" class="java.util.HashMap"/>
<jsp:useBean scope="request" id="statuses" class="java.util.ArrayList"/>
<c:set var="startDate" value="" />
<c:set var="endDate" value="" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "startDate"}'>
		<c:set var="startDate" value="${presetconfig.value.value}" />
	</c:if>
	<c:if test='${presetValue.key == "endDate"}'>
		<c:set var="endDate" value="${presetconfig.value.value}" />
	</c:if>	
</c:forEach>
<h1><span class="title_manage">Create a new Site</span></h1>

<form action="CreateSubStudy" method="post">
* indicates required field.<br>
<input type="hidden" name="action" value="confirm">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">
   <tr valign="top"><td class="formlabel">Parent Study:</td><td><div class="formfieldXL_BG">
   &nbsp;<c:out value="${study.name}"/>
  </div></td></tr>
  
  <tr valign="top"><td class="formlabel">Site Name:</td><td><div class="formfieldXL_BG">
  <input type="text" name="name" value="<c:out value="${newStudy.name}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td><td> *</td></tr>
  
  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId'); return false;">Unique Protocol ID:</td><td><div class="formfieldXL_BG">
  <input type="text" name="uniqueProId" value="<c:out value="${newStudy.identifier}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueProId"/></jsp:include></td><td> *</td></tr>
  
  <tr valign="top"><td class="formlabel">Secondary IDs<br>(separate by comma):</td><td>
  <div class="formtextareaXL4_BG"><textarea class="formtextareaXL4" name="secondProId" rows="4" cols="50"><c:out value="${newStudy.secondaryIdentifier}"/></textarea></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondProId"/></jsp:include>
  </td></tr>  
   
  <tr valign="top"><td class="formlabel">Principal Investigator:</td><td><div class="formfieldXL_BG">
  <input type="text" name="prinInvestigator" value="<c:out value="${newStudy.principalInvestigator}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="prinInvestigator"/></jsp:include></td><td> *</td></tr> 
  
  <tr valign="top"><td class="formlabel">Brief Summary:</td><td><div class="formtextareaXL4_BG">
  <textarea class="formtextareaXL4" name="description" rows="4" cols="50"><c:out value="${newStudy.summary}"/></textarea></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include></td><td> *</td></tr>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">  
    var cal1 = new CalendarPopup("testdiv1");
  </SCRIPT>
  <tr valign="top"><td class="formlabel">Start Date:</td><td><div class="formfieldXL_BG">
  <input type="text" name="startDate" value="<c:out value="${startDate}" />" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startDate"/></jsp:include></td>
  <td><A HREF="#" onClick="cal1.select(document.forms[0].startDate,'anchor1','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.forms[0].startDate,'anchor1','MM/dd/yyyy'); return false;" NAME="anchor1" ID="anchor1"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a>(MM/DD/YYYY)</td></tr> 
  
  <tr valign="top"><td class="formlabel">Estimated Completion Date:</td><td><div class="formfieldXL_BG">
  <input type="text" name="endDate" value="<c:out value="${endDate}" />" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endDate"/></jsp:include></td>
  <td><A HREF="#" onClick="cal1.select(document.forms[0].endDate,'anchor2','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.forms[0].endDate,'anchor2','MM/dd/yyyy'); return false;" NAME="anchor2" ID="anchor2"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a>(MM/DD/YYYY)</td></tr> 
  
  <tr valign="top"><td class="formlabel">Facility Name:</td><td><div class="formfieldXL_BG">
  <input type="text" name="facName" value="<c:out value="${newStudy.facilityName}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facName"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel">Facility City:</td><td><div class="formfieldXL_BG">
  <input type="text" name="facCity" value="<c:out value="${newStudy.facilityCity}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facCity"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel">Facility State/Province:</td><td><div class="formfieldXL_BG">
  <input type="text" name="facState" value="<c:out value="${newStudy.facilityState}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facState"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel">Facility ZIP:</td><td><div class="formfieldXL_BG">
  <input type="text" name="facZip" value="<c:out value="${newStudy.facilityZip}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facZip"/></jsp:include>
  </td></tr>
  
  <tr valign="top"><td class="formlabel">Facility Country:</td><td><div class="formfieldXL_BG">
  <input type="text" name="facCountry" value="<c:out value="${newStudy.facilityCountry}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facCountry"/></jsp:include>
  </td></tr> 
  
  
  <tr valign="top"><td class="formlabel">Facility Contact Name:</td><td><div class="formfieldXL_BG">
  <input type="text" name="facConName" value="<c:out value="${newStudy.facilityContactName}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConName"/></jsp:include>
  </td></tr>   
  
  <tr valign="top"><td class="formlabel">Facility Contact Degree:</td><td><div class="formfieldXL_BG">
  <input type="text" name="facConDegree" value="<c:out value="${newStudy.facilityContactDegree}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConDegree"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel">Facility Contact Phone:</td><td><div class="formfieldXL_BG">
  <input type="text" name="facConPhone" value="<c:out value="${newStudy.facilityContactPhone}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConPhone"/></jsp:include>
  </td></tr>    
  
  <tr valign="top"><td class="formlabel">Facility Contact Email:</td><td><div class="formfieldXL_BG">
  <input type="text" name="facConEmail" value="<c:out value="${newStudy.facilityContactEmail}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConEmail"/></jsp:include></td></tr>  
  
  <tr valign="top"><td class="formlabel">Study System Status:</td><td><div class="formfieldXL_BG">
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
   </select></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="statusId"/></jsp:include></td><td> *</td></tr>      
     
  <c:forEach var="config" items="${newStudy.studyParameters}">   
   <c:choose>
   <c:when test="${config.parameter.handle=='collectDOB'}">
     <tr valign="top"><td class="formlabel">Collect Subject Date of Birth?:</td><td>
       <c:choose>
         <c:when test="${config.value.value == '1'}">
           <input type="radio" checked name="collectDob" value="1">Yes
           <input type="radio" name="collectDob" value="2">only Year of Birth
           <input type="radio" name="collectDob" value="3">Not Used
         </c:when>
         <c:when test="${config.value.value == '2'}">
            <input type="radio" name="collectDob" value="1">Yes
            <input type="radio" checked name="collectDob" value="2">only Year of Birth
            <input type="radio" name="collectDob" value="3">Not Used
         </c:when>
         <c:otherwise>
            <input type="radio" name="collectDob" value="1">Yes
            <input type="radio" name="collectDob" value="2">only Year of Birth
            <input type="radio" checked name="collectDob" value="3">Not Used
         </c:otherwise>
      </c:choose>  
      </td></tr>
   
   </c:when>
    
   <c:when test="${config.parameter.handle=='discrepancyManagement'}">
		  <tr valign="top"><td class="formlabel">Allow Discrepancy Management?:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'false'}">
		    <input type="radio" name="discrepancyManagement" value="true">Yes
		    <input type="radio" checked name="discrepancyManagement" value="false">No
		   </c:when>
		   <c:otherwise>
		    <input type="radio" checked name="discrepancyManagement" value="true">Yes
		    <input type="radio" name="discrepancyManagement" value="false">No
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	
	<c:when test="${config.parameter.handle=='genderRequired'}">	  
		  <tr valign="top"><td class="formlabel">Gender Required?:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == false}">
		    <input type="radio" name="genderRequired" value="true">Yes
		    <input type="radio" checked name="genderRequired" value="false">No
		   </c:when>
		   <c:otherwise>
		    <input type="radio" checked name="genderRequired" value="true">Yes
		    <input type="radio" name="genderRequired" value="false">No
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>	  
    <c:when test="${config.parameter.handle=='subjectPersonIdRequired'}">		
		  <tr valign="top"><td class="formlabel">Subject Person ID Required?:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'required'}">
		    <input type="radio" checked name="subjectPersonIdRequired" value="required">Required
		    <input type="radio" name="subjectPersonIdRequired" value="optional">Optional
		    <input type="radio" name="subjectPersonIdRequired" value="not used">Not Used
		   </c:when>
		    <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'optional'}">
		    <input type="radio" name="subjectPersonIdRequired" value="required">Required
		    <input type="radio" checked name="subjectPersonIdRequired" value="optional">Optional
		    <input type="radio" name="subjectPersonIdRequired" value="not used">Not Used
		   </c:when>
		   <c:otherwise>
		    <input type="radio" name="subjectPersonIdRequired" value="required">Required
		    <input type="radio" name="subjectPersonIdRequired" value="optional">Optional
		    <input type="radio" checked name="subjectPersonIdRequired" value="not used">Not Used
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='subjectIdGeneration'}">	  
		   <tr valign="top"><td class="formlabel">How to Generate the Study Subject ID?:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'manual'}">
		    <input type="radio" checked name="subjectIdGeneration" value="manual">Manual Entry
		    <input type="radio" name="subjectIdGeneration" value="auto editable">Auto-generated and Editable
		    <input type="radio" name="subjectIdGeneration" value="auto non-editable">Auto-generated and Non-editable
		   </c:when>
		    <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'auto editable'}">
		    <input type="radio" name="subjectIdGeneration" value="manual">Manual Entry
		    <input type="radio" checked name="subjectIdGeneration" value="auto editable">Auto-generated and Editable
		    <input type="radio" name="subjectIdGeneration" value="auto non-editable">Auto-generated and Non-editable
		   </c:when>
		   <c:otherwise>
		    <input type="radio" name="subjectIdGeneration" value="manual">Manual Entry
		    <input type="radio" name="subjectIdGeneration" value="auto editable">Auto-generated and Editable
		    <input type="radio" checked name="subjectIdGeneration" value="auto non-editable">Auto-generated and Non-editable
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='subjectIdPrefixSuffix'}">	  
		   <tr valign="top"><td class="formlabel">Generate Study Subject ID Automatically With Prefix/Suffix?:</td><td>
		   <c:choose>
		   <c:when test="${config.value.value == 'true'}">
		    <input type="radio" checked name="subjectIdPrefixSuffix" value="true">Yes
		    <input type="radio" name="subjectIdPrefixSuffix" value="false">No
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="subjectIdPrefixSuffix" value="true">Yes
		    <input type="radio" checked name="subjectIdPrefixSuffix" value="false">No
		   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameRequired'}">
		   <tr valign="top"><td class="formlabel">When Entering Data Entry, Interviewer Name Required?</td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <input type="radio" checked name="interviewerNameRequired" value="true">Yes
		    <input type="radio" name="interviewerNameRequired" value="false">No
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewerNameRequired" value="true">Yes
		    <input type="radio" checked name="interviewerNameRequired" value="false">No   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameDefault'}">	  
		  <tr valign="top"><td class="formlabel">Interviewer Name Default as Blank?</td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'blank'}">
		    <input type="radio" checked name="interviewerNameDefault" value="blank">Blank
		    <input type="radio" name="interviewerNameDefault" value="pre-populated">Pre-Populated from Study Event
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewerNameDefault" value="blank">Blank
		    <input type="radio" checked name="interviewerNameDefault" value="re-populated">Pre-Populated from Study Event   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameEditable'}">	  
		  <tr valign="top"><td class="formlabel">Interviewer Name Editable?</td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <input type="radio" checked name="interviewerNameEditable" value="true">Yes
		    <input type="radio" name="interviewerNameEditable" value="false">No
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewerNameEditable" value="true">Yes
		    <input type="radio" checked name="interviewerNameEditable" value="false">No   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewDateRequired'}">	  
		  <tr valign="top"><td class="formlabel">Interview Date Required?</td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <input type="radio" checked name="interviewDateRequired" value="true">Yes
		    <input type="radio" name="interviewDateRequired" value="false">No
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewDateRequired" value="true">Yes
		    <input type="radio" checked name="interviewDateRequired" value="false">No   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
    </c:when>		  
	<c:when test="${config.parameter.handle=='interviewDateDefault'}">	  
		  <tr valign="top"><td class="formlabel">Interview Date Default as Blank?</td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'blank'}">
		    <input type="radio" checked name="interviewDateDefault" value="blank">Blank
		    <input type="radio" name="interviewDateDefault" value="pre-populated">Pre-Populated from Study Event
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewDateDefault" value="blank">Blank
		    <input type="radio" checked name="interviewDateDefault" value="re-populated">Pre-Populated from Study Event   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	 </c:when>
	 <c:otherwise>	  
		  <tr valign="top"><td class="formlabel">Interview Date Editable?</td><td>
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <input type="radio" checked name="interviewDateEditable" value="true">Yes
		    <input type="radio" name="interviewDateEditable" value="false">No
		   
		   </c:when>    
		   <c:otherwise>
		    <input type="radio" name="interviewDateEditable" value="true">Yes
		    <input type="radio" checked name="interviewDateEditable" value="false">No   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
     </c:otherwise>
   </c:choose>
  </c:forEach>
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<input type="submit" name="Submit" value="Confirm Site" class="button_long">
</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<br><br>

<!-- EXPANDING WORKFLOW BOX -->

<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
	<tr>
		<td id="sidebar_Workflow_closed" style="display: none">
		<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/tab_Workflow_closed.gif" border="0"></a>
	</td>
	<td id="sidebar_Workflow_open" style="display: all">
	<table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
		<tr>
			<td class="workflowBox_T" valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="workflow_tab">
					<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

					<b>Workflow</b>

					</td>
				</tr>
			</table>
			</td>
			<td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif"></td>
		</tr>
		<tr>
			<td colspan="2" class="workflowbox_B">
			<div class="box_R"><div class="box_B"><div class="box_BR">
				<div class="workflowBox_center">


		<!-- Workflow items -->

				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
							<div class="textbox_center" align="center">
	
							<span class="title_manage">
				
					
						
							Manage Study
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_manage">
				
					
							 Manage Sites
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_manage">
				
					
							 <b>Create New Site</b> 
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
					</tr>
				</table>


		<!-- end Workflow items -->

				</div>
			</div></div></div>
			</td>
		</tr>
	</table>			
	</td>
   </tr>
</table>

<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>
