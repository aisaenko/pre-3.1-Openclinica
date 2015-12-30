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
<jsp:useBean scope="request" id="obserPurposeMap" class ="java.util.HashMap"/>
<jsp:useBean scope="request" id="durationMap" class ="java.util.HashMap"/>
<jsp:useBean scope="request" id="selectionMap" class ="java.util.HashMap"/>
<jsp:useBean scope="request" id="timingMap" class ="java.util.HashMap"/>
<h1><span class="title_Admin">
Update Study Details,Continue...
</span></h1>

<form action="UpdateStudy" method="post">
<span class="title_Admin"><p><b>SECTION B: Study Status and Design- Design Details - Observational</b></p></span>
* indicates required field.<br>
<input type="hidden" name="action" value="next">
<input type="hidden" name="pageNum" value="3">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0">
  <tr valign="bottom"><td class="formlabel">Purpose:</td><td>
   <c:set var="purpose1" value="${newStudy.purpose}"/> 
  <div class="formfieldXL_BG"><select name="purpose" class="formfieldXL">
   <c:forEach var="purpose" items="${obserPurposeMap}">    
       <c:choose>
        <c:when test="${purpose1 == purpose.key}">   
         <option value="<c:out value="${purpose.key}"/>" selected><c:out value="${purpose.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${purpose.key}"/>"><c:out value="${purpose.value}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>   
  </select></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="purpose"/></jsp:include></td><td class="formlabel">*</td></tr>
  
  <tr valign="bottom"><td class="formlabel">Duration:</td><td>
  <c:choose>
   <c:when test="${newStudy.duration =='longitudinal'}">
    <input type="radio" checked name="duration" value="longitudinal">Longitudinal
    <input type="radio" name="duration" value="cross-sectional">Cross-sectional
   </c:when>
   <c:otherwise>
    <input type="radio" name="duration" value="longitudinal">Longitudinal
    <input type="radio" checked name="duration" value="cross-sectional">Cross-sectional
   </c:otherwise>
  </c:choose>
  </td></tr>
  
  <tr valign="bottom"><td class="formlabel"><a href="definitions.html#PrimaryId" target="def_win" onClick="openDefWindow('definitions.html#PrimaryId'); return false;">
  Selection:</td><td>
  <c:set var="selection1" value="${newStudy.selection}"/> 
  <div class="formfieldXL_BG">
  <select name="selection" class="formfieldXL">
   <c:forEach var="selection" items="${selectionMap}">    
       <c:choose>
        <c:when test="${selection1 == selection.key}">   
         <option value="<c:out value="${selection.key}"/>" selected><c:out value="${selection.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${selection.key}"/>"><c:out value="${selection.value}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>   
  </select></div>
  </td></tr>
  
  <tr valign="bottom"><td class="formlabel">Timing:</td><td>
   <c:set var="timing1" value="${newStudy.timing}"/> 
   <div class="formfieldXL_BG">
   <select name="timing" class="formfieldXL">
    <c:forEach var="timing" items="${timingMap}">    
       <c:choose>
        <c:when test="${timing1 == timing.key}">   
         <option value="<c:out value="${timing.key}"/>" selected><c:out value="${timing.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${timing.key}"/>"><c:out value="${timing.value}"/>      
        </c:otherwise>
       </c:choose> 
     </c:forEach>   
   </select></div>
  </td></tr>


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
