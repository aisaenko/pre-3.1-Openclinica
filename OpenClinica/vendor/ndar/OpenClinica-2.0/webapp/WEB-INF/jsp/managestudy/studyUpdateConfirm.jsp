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

<jsp:useBean scope='session' id='newStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='session' id='interventions' class='java.util.ArrayList'/>
<h1><span class="title_admin">
Confirm Study Details
</span></h1>

<form action="UpdateStudy" method="post">
<input type="hidden" name="action" value="submit">
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">   
  <tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2">SECTION A: Study Description</td></tr>
  <tr valign="top"><td class="table_header_column">Brief Title:</td><td class="table_cell">
  <c:out value="${newStudy.name}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Official Title:</td><td class="table_cell">
  <c:out value="${newStudy.officialTitle}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Unique Protocol ID:</td><td class="table_cell">
  <c:out value="${newStudy.identifier}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Secondary IDs:</td><td class="table_cell">
   <c:out value="${newStudy.secondaryIdentifier}"/>
   </td></tr>  
   
  <tr valign="top"><td class="table_header_column">Principal Investigator:</td><td class="table_cell">
  <c:out value="${newStudy.principalInvestigator}"/>
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Brief Summary:</td><td class="table_cell">
  <c:out value="${newStudy.summary}"/>
  </td></tr>    
   
  <tr valign="top"><td class="table_header_column">Protocol Detailed Description:</td><td class="table_cell">
  <c:out value="${newStudy.protocolDescription}"/>
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column">Sponsor:</td><td class="table_cell">
  <c:out value="${newStudy.sponsor}"/>
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Collaborators:</td><td class="table_cell">
  <c:out value="${newStudy.collaborators}"/>
  </td></tr> 
  
  <tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2">SECTION B: Study Status and Design</td></tr>
  
  <tr valign="top"><td class="table_header_column">Study Phase:</td><td class="table_cell">
  <c:out value="${newStudy.phase}"/>
  </td></tr>   

 <tr valign="top"><td class="table_header_column">Protocol Type:</td><td class="table_cell">
 <c:out value="${newStudy.protocolType}"/>
 </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Protocol Verification/IRB Approval Date:</td><td class="table_cell">
  <fmt:formatDate value="${newStudy.protocolDateVerification}" pattern="MM/dd/yyyy"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Collect Subject Father/Mother Information?:</td><td class="table_cell">
  <c:choose>
    <c:when test="${newStudy.genetic == true}">
     Yes
    </c:when>
    <c:otherwise>
     No
    </c:otherwise> 
   </c:choose>
 </td></tr> 
 
  <tr valign="top"><td class="table_header_column">Start Date:</td><td class="table_cell">
   <fmt:formatDate value="${newStudy.datePlannedStart}" pattern="MM/dd/yyyy"/>
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Estimated Completion Date:</td><td class="table_cell">
   <fmt:formatDate value="${newStudy.datePlannedEnd}" pattern="MM/dd/yyyy"/>&nbsp;
  </td></tr>   
  
  <tr valign="top"><td class="table_header_column">Study System Status:</td><td class="table_cell">
  <c:out value="${newStudy.status.name}"/>
   </td></tr>   
     

  <tr valign="top"><td class="table_header_column">Purpose:</td><td class="table_cell">
   <c:out value="${newStudy.purpose}"/>&nbsp;
  </td></tr>
  
  <c:choose>
  <c:when test="${newStudy.protocolType=='interventional'}">
  
  <tr valign="top"><td class="table_header_column">Allocation:</td><td class="table_cell">
   <c:out value="${newStudy.allocation}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Masking:</td><td class="table_cell">
    <c:out value="${newStudy.masking}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Control:</td><td class="table_cell">
    <c:out value="${newStudy.control}"/>&nbsp;
  </td></tr>  
   
  <tr valign="top"><td class="table_header_column">Assignment:</td><td class="table_cell">
   <c:out value="${newStudy.assignment}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Endpoint:</td><td class="table_cell">
   <c:out value="${newStudy.endpoint}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Intervention(s):</td><td class="table_cell">
   <c:forEach var ="intervention" items="${interventions}">
     Type :<c:out value="${intervention.type}"/> &nbsp;      
     Name: <c:out value="${intervention.name}"/> <br>   
   </c:forEach>&nbsp;
  </td></tr> 
  
  </c:when> 
  <c:otherwise>
  <tr valign="top"><td class="table_header_column">Duration:</td><td class="table_cell">
   <c:out value="${newStudy.duration}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Selection:</td><td class="table_cell">
  <c:out value="${newStudy.selection}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Timing:</td><td class="table_cell">
  <c:out value="${newStudy.timing}"/>&nbsp;
  </td></tr>  
  
  </c:otherwise>
  </c:choose> 
  
  <tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2">SECTION C: Conditions and Eligibility</td></tr>
  
  <tr valign="top"><td class="table_header_column">Conditions:</td><td class="table_cell">
  <c:out value="${newStudy.conditions}"/>&nbsp;
 </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Keywords:</td><td class="table_cell">
  <c:out value="${newStudy.keywords}"/>&nbsp;
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Eligibility:</td><td class="table_cell">
  <c:out value="${newStudy.eligibility}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Gender:</td><td class="table_cell">  
    <c:out value="${newStudy.gender}"/>&nbsp; 
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column">Minimum Age:</td><td class="table_cell">
  <c:out value="${newStudy.ageMin}"/>&nbsp;
  </td></tr>   
  
  <tr valign="top"><td class="table_header_column">Maximum Age:</td><td class="table_cell">
  <c:out value="${newStudy.ageMax}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Healthy Volunteers Accepted:</td><td class="table_cell">
  <c:choose>
    <c:when test="${newStudy.healthyVolunteerAccepted == true}">
     Yes
    </c:when>
    <c:otherwise>
     No
    </c:otherwise> 
   </c:choose>
 </td></tr>  
  
  <tr valign="top"><td class="table_header_column">Expected total enrollment:</td><td class="table_cell">
  <c:out value="${newStudy.expectedTotalEnrollment}"/>&nbsp;
  </td></tr>   
 
  <tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2">SECTION D: Facility Information</td></tr>      
  
  <tr valign="top"><td class="table_header_column">Facility Name:</td><td class="table_cell">
  <c:out value="${newStudy.facilityName}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility City:</td><td class="table_cell">
  <c:out value="${newStudy.facilityCity}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility State/Province:</td><td class="table_cell">
  <c:out value="${newStudy.facilityState}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility ZIP:</td><td class="table_cell">
  <c:out value="${newStudy.facilityZip}"/>&nbsp;
 </td></tr>
  
  <tr valign="top"><td class="table_header_column">Facility Country:</td><td class="table_cell">
  <c:out value="${newStudy.facilityCountry}"/>&nbsp;
  </td></tr> 
  
  <!--<tr valign="top"><td class="table_header_column">Facility Recruitment Status:</td><td class="table_cell">
  <c:out value="${newStudy.facilityRecruitmentStatus}"/>&nbsp;
 </td></tr>  
  -->
  <tr valign="top"><td class="table_header_column">Facility Contact Name:</td><td class="table_cell">
  <c:out value="${newStudy.facilityContactName}"/>&nbsp;
  </td></tr>   
  
  <tr valign="top"><td class="table_header_column">Facility Contact Degree:</td><td class="table_cell">
  <c:out value="${newStudy.facilityContactDegree}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility Contact Phone:</td><td class="table_cell">
  <c:out value="${newStudy.facilityContactPhone}"/>&nbsp;
 </td></tr>       
  
  <tr valign="top"><td class="table_header_column">Facility Contact Email:</td><td class="table_cell">
  <c:out value="${newStudy.facilityContactEmail}"/>&nbsp;
  </td></tr>  
  
  
 
   <tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2">SECTION E: Related Information</td></tr> 
 
  
  <tr valign="top"><td class="table_header_column">MEDLINE Identifier References:</td><td class="table_cell">
  <c:out value="${newStudy.medlineIdentifier}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Results Reference?</td><td class="table_cell">
  <c:choose>
    <c:when test="${newStudy.resultsReference == true}">
     Yes
    </c:when>
    <c:otherwise>
     No
    </c:otherwise> 
   </c:choose>
 </td></tr>  
  
  <tr valign="top"><td class="table_header_column">URL Reference:</td><td class="table_cell">
  <c:out value="${newStudy.url}"/>&nbsp;
 </td></tr> 
  
  <tr valign="top"><td class="table_header_column">URL Description:</td><td class="table_cell">
  <c:out value="${newStudy.urlDescription}"/>&nbsp;
  </td></tr> 
  
           

  <tr bgcolor="#F5F5F5"><td class="table_header_column" colspan="2">SECTION F: Study Parameter Configuration</td></tr> 
  
  <tr valign="top"><td class="table_header_column">Collect Subject Date of Birth?:</td>
   <td class="table_cell">
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.collectDob == '1'}">
   Yes   
   </c:when>
   <c:when test="${newStudy.studyParameterConfig.collectDob == '2'}">    
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
   <c:when test="${newStudy.studyParameterConfig.discrepancyManagement == 'true'}">
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
   <c:when test="${newStudy.studyParameterConfig.genderRequired == 'false'}">
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
   <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'required'}">
    Required    
   </c:when>
    <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'optional'}">
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
   <c:when test="${newStudy.studyParameterConfig.subjectIdGeneration == 'manual'}">
    Manual Entry    
   </c:when>
    <c:when test="${newStudy.studyParameterConfig.subjectIdGeneration == 'auto editable'}">
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
   <c:when test="${newStudy.studyParameterConfig.subjectIdPrefixSuffix == 'true'}">
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
   <c:when test="${newStudy.studyParameterConfig.interviewerNameRequired== 'true'}">
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
   <c:when test="${newStudy.studyParameterConfig.interviewerNameDefault== 'blank'}">
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
   <c:when test="${newStudy.studyParameterConfig.interviewerNameEditable== 'true'}">
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
   <c:when test="${newStudy.studyParameterConfig.interviewDateRequired== 'true'}">
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
   <c:when test="${newStudy.studyParameterConfig.interviewDateDefault== 'blank'}">
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
   <c:when test="${newStudy.studyParameterConfig.interviewDateEditable== 'true'}">
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
 <input type="submit" name="Submit" value="Submit Study" class="button_long">
</form>

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
	
							<span class="title_admin">					
						
							Study<br>Title &<br>Description<br><br>
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
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
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					       Conditions<br> &<br>Eligibility
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
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
						<td><img src="images/arrow.gif"></td>
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
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					
							 <b>Confirm & Submit<br>Study</b>
					
				
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
