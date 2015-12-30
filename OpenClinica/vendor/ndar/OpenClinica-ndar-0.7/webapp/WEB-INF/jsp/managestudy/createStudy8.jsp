<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/admin-header.jsp"/>
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
        Enter the Study and Protocol Information requested to create the study. 
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='newStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope ="request" id="studyPhaseMap" class="java.util.HashMap"/>
<jsp:useBean scope="request" id="statuses" class="java.util.ArrayList"/>
<h1><span class="title_Admin">Create a New Study, Continue...</span></h1>

<span class="title_Admin"><p><b>SECTION F: Study Parameter Configuration </b></p></span>		
<P>* indicates required field.</P>

<form action="CreateStudy" method="post">
<input type="hidden" name="action" value="confirm">
<div style="width: 600px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">
  
  <tr valign="top"><td class="formlabel">Collect Subject Date of Birth?:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.collectDob == '1'}">
    <input type="radio" checked name="collectDob" value="1">Yes
    <input type="radio" name="collectDob" value="2">only Year of Birth
     <input type="radio" name="collectDob" value="3">Not Used
   </c:when>
   <c:when test="${newStudy.studyParameterConfig.collectDob == '2'}">
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
  
  <tr valign="top"><td class="formlabel">Allow Discrepancy Management?:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.discrepancyManagement == 'false'}">
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
  
  <tr valign="top"><td class="formlabel">Gender Required?:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.genderRequired == 'false'}">
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
  <tr><td>&nbsp;</td></tr>
  <tr valign="top"><td class="formlabel">Subject Person ID Required?:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'required'}">
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
  
   <tr valign="top"><td class="formlabel">How to Generate the Study Subject ID?:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.subjectIdGeneration == 'manual'}">
    <input type="radio" checked name="subjectIdGeneration" value="manual">Manual Entry
    <input type="radio" name="subjectIdGeneration" value="auto editable">Auto-generated and Editable
    <input type="radio" name="subjectIdGeneration" value="auto non-editable">Auto-generated and Non-editable
   </c:when>
    <c:when test="${newStudy.studyParameterConfig.subjectIdGeneration == 'auto editable'}">
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
  
  <!--
   <tr valign="top"><td class="formlabel">Generate Study Subject ID Automatically With Prefix/Suffix?:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.subjectIdPrefixSuffix == 'true'}">
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
  -->
  <tr><td>&nbsp;</td></tr>
  
  <tr valign="top"><td class="formlabel">When rescheduling an event, if it causes events to exceed scheduling window, how do you want to reschedule it?:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.reschedule == 'reschedule based on first event'}">
    <input type="radio" name="reschedule" value="reschedule based on first event" checked="checked">Based on the first event
    <input type="radio" name="reschedule" value="reschedule based on previous event">Based on the previous event
   </c:when>    
   <c:otherwise>
    <input type="radio" name="reschedule" value="reschedule based on first event">Based on the first event
    <input type="radio" name="reschedule" value="reschedule based on previous event" checked="checked">Based on the previous event
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
  
 <tr valign="top"><td class="formlabel">Show Person ID on CRF Header?:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.personIdShownOnCRF == 'true'}">
    <input type="radio" checked name="personIdShownOnCRF" value="true">Yes
    <input type="radio" name="personIdShownOnCRF" value="false">No
   
   </c:when>    
   <c:otherwise>
    <input type="radio" name="personIdShownOnCRF" value="true">Yes
    <input type="radio" checked name="personIdShownOnCRF" value="false">No
   
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

   <tr><td>&nbsp;</td></tr>
   <tr valign="top"><td class="formlabel">When Entering Data Entry, Interviewer Name Required?</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.interviewerNameRequired== 'true'}">
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
  
  <tr valign="top"><td class="formlabel">Interviewer Name Default as Blank?</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.interviewerNameDefault== 'blank'}">
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
  
  <tr valign="top"><td class="formlabel">Interviewer Name Editable?</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.interviewerNameEditable== 'true'}">
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
  
  <tr valign="top"><td class="formlabel">Assessment Date Required?</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.interviewDateRequired== 'true'}">
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
  
  <tr valign="top"><td class="formlabel">Assessment Date Default as Blank?</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.interviewDateDefault== 'blank'}">
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
  
  <tr valign="top"><td class="formlabel">Assessment Date Editable?</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.interviewDateEditable== 'true'}">
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
 
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>   
 <input type="submit" name="Submit" value="Confirm Study" class="button_long">
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
					<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

					<b>Workflow</b>

					</td>
				</tr>
			</table>
			</td>
			<td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif" alt=""></td>
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
	
							<span class="title_admin">					
						
							Study<br>Title &<br>Description<br><br>
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					
							Status<br>&<br>Design
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					        Conditions<br> &<br>Eligibility<br><br>
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					
							 Specify <br>Facility
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					
							 Other<br>Related<br>Information
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					
							 <b>Study Parameter<br> Configuration</b>
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					
							 Confirm & Submit<br>Study
					
				
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
