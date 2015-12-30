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
<jsp:useBean scope ="request" id="studyPhaseMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="studyTypes" class="java.util.ArrayList"/>
<h1><span class="title_Admin">
Update Study Details,Continue...
</span></h1>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<span class="title_Admin"><p><b>SECTION B: Study Status and Design - Status </b></p></span>		
<P>* indicates required field.</P>
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
<form action="UpdateStudy" method="post">
<input type="hidden" name="action" value="next">
<input type="hidden" name="pageNum" value="2">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0">
 <tr valign="top"><td class="formlabel">Study Phase:</td><td>
  <c:set var="phase1" value="${newStudy.phase}"/> 
  <div class="formfieldM_BG"><select name="phase" class="formfieldM">
   <c:forEach var="phase" items="${studyPhaseMap}">      
       <c:choose>
        <c:when test="${phase1 == phase.key}">   
         <option value="<c:out value="${phase.key}"/>" selected><c:out value="${phase.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${phase.key}"/>"><c:out value="${phase.value}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>   
  </select></div>
  </td><td>*</td></tr>    
  
  <tr valign="top"><td class="formlabel">Protocol Type:</td><td>
  <c:set var="type1" value="observational"/>
  <c:choose>
   <c:when test="${newStudy.protocolType == type1}">
    <input type="radio" checked name="protocolType" value="observational" disabled>Observational
   </c:when>
   <c:otherwise>
    <input type="radio" checked name="protocolType" value="interventional" disabled>Interventional
   </c:otherwise>
  </c:choose>
  </td></tr>
  
  <!--  
  <tr valign="top"><td class="formlabel">Collect Subject Father/Mother Information?:</td><td>
   <c:choose>
   <c:when test="${newStudy.genetic == false}">
    <input type="radio" name="genetic" value="1">Yes
    <input type="radio" checked name="genetic" value="2">No
   </c:when>
   <c:otherwise>
    <input type="radio" checked name="genetic" value="1">Yes
    <input type="radio" name="genetic" value="2">No
   </c:otherwise>
  </c:choose>
  </td></tr>
  -->
   
  
  
  <tr valign="top"><td class="formlabel">Study System Status:</td><td><div class="formfieldM_BG">
   <c:set var="status1" value="${newStudy.status.id}"/>   
   <select class="formfieldM" name="statusId">
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
  </td><td>*</td></tr>  
  
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">  
  var cal1 = new CalendarPopup("testdiv1");
  </SCRIPT>
  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#VerificationDate" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#VerificationDate'); return false;">Protocol Verification/IRB Approval Date:</a></td><td>
  
  <div class="formfieldM_BG"><input type="text" name="protocolDateVerification" value="<c:out value="${protocolDateVerification}"/>" class="formfieldM"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="protocolDateVerification"/></jsp:include></td>
  <td><A HREF="#" onClick="cal1.select(document.forms[0].protocolDateVerification,'anchor1','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.forms[0].protocolDateVerification,'anchor1','MM/dd/yyyy'); return false;" NAME="anchor1" ID="anchor1"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a> (MM/DD/YYYY) *</td></tr>
  
  <tr valign="top"><td class="formlabel">Study Start Date:</td><td><div class="formfieldM_BG">
  
  <input type="text" name="startDate" value="<c:out value="${startDate}" />" class="formfieldM"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startDate"/></jsp:include>
  </td>
  <td><A HREF="#" onClick="cal1.select(document.forms[0].startDate,'anchor2','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.forms[0].startDate,'anchor2','MM/dd/yyyy'); return false;" NAME="anchor2" ID="anchor2"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a> (MM/DD/YYYY) *</td></tr> 
  
  <tr valign="top"><td class="formlabel">Study Completion Date:</td><td><div class="formfieldM_BG">
  <input type="text" name="endDate" value="<c:out value="${endDate}" />" class="formfieldM"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endDate"/></jsp:include></td>
  <td><A HREF="#" onClick="cal1.select(document.forms[0].endDate,'anchor3','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.forms[0].endDate,'anchor3','MM/dd/yyyy'); return false;" NAME="anchor3" ID="anchor3"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a> (MM/DD/YYYY)</td></tr> 
    
 
 
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
				
					
							<b>Status<br>&<br>Design</b>
					
				
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
				
					        Conditions<br> &<br>Eligibility<br><br>
				
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
