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

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
        Enter the Study and Protocol Information requested to create the study. 
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

<jsp:useBean scope='session' id='newStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope ="request" id="studyPhaseMap" class="java.util.HashMap"/>
<jsp:useBean scope="request" id="statuses" class="java.util.ArrayList"/>
<h1><span class="title_Admin">Create a New Study, Continue...</span></h1>

<span class="title_Admin"><p><b>SECTION C: Conditions and Eligibility</b></p></span>		
<P>* indicates required field.</P>
<form action="CreateStudy" method="post">
<input type="hidden" name="action" value="next">
<input type="hidden" name="pageNum" value="4">
<div style="width: 600px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">
 
  <tr valign="top"><td class="formlabel">
  <a href="http://prsinfo.clinicaltrials.gov/definitions.html#Conditions" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#Conditions'); return false;">Conditions</a>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="conditions" value="<c:out value="${newStudy.conditions}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="conditions"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#Keywords" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#Keywords'); return false;">Keywords</a>:<br>(separate by commas)</td><td>
  <div class="formtextareaXL4_BG"><textarea name="keywords" rows="4" cols="50" class="formtextareaXL4"><c:out value="${newStudy.keywords}"/></textarea></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="keywords"/></jsp:include>
  </td></tr>
  
  <tr valign="top"><td class="formlabel">Eligibility Criteria:</td><td>
  <div class="formtextareaXL4_BG"><textarea name="eligibility" rows="4" cols="50" class="formtextareaXL4"><c:out value="${newStudy.eligibility}"/></textarea></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="eligibility"/></jsp:include>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel">Gender:</td><td> 
  <div class="formfieldXL_BG"><select name="gender" class="formfieldXL">
  <c:choose>
   <c:when test="${newStudy.gender =='female'}">
    <option value="both">Both</option>
    <option value="male">Male</option>
    <option value="female" selected>Female</option>
   </c:when>
   <c:when test="${newStudy.gender =='male'}">
    <option value="both">Both</option>
    <option value="male" selected>Male</option>
    <option value="female">Female</option>
   </c:when>
   <c:otherwise>
    <option value="both" selected>Both</option>
    <option value="male">Male</option>
    <option value="female">Female</option>
   </c:otherwise>
   
  </c:choose>
  </select></div>
  </td></tr>  
  
  <tr valign="top"><td class="formlabel">Minimum Age:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="ageMin" value="<c:out value="${newStudy.ageMin}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="ageMin"/></jsp:include></td></tr>   
  
  <tr valign="top"><td class="formlabel">Maximum Age:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="ageMax" value="<c:out value="${newStudy.ageMax}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="ageMax"/></jsp:include></td></tr> 
  
  <tr valign="top"><td class="formlabel">Healthy Volunteers Accepted:</td><td>
   <div class="formfieldXL_BG"><select name="healthyVolunteerAccepted" class="formfieldXL">
    <c:choose>
     <c:when test="${newStudy.healthyVolunteerAccepted == true}">
      <option value="1" selected>Yes</option>
      <option value="0">No</option>
     </c:when>
     <c:otherwise>
      <option value="1">Yes</option>
      <option value="0" selected>No</option>
     </c:otherwise> 
    </c:choose>
   </select></div>
  </td></tr>       
 
  
  <tr valign="top"><td class="formlabel">Expected total enrollment:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="expectedTotalEnrollment" value="<c:out value="${newStudy.expectedTotalEnrollment}"/>" class="formfieldXL"></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="expectedTotalEnrollment"/></jsp:include>
  </td><td class="formlabel">*</td></tr> 
  
  </table>
</div>
</div></div></div></div></div></div></div></div>

</div>   
 <input type="submit" name="Submit" value="Continue" class="button_medium">
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
				
					        <b>Conditions<br> &<br>Eligibility</b>
				
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
				
					         Study Parameter<br> Configuration										
				
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
   
  