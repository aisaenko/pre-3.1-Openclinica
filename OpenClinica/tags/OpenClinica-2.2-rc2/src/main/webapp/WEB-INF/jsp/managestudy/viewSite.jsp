<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:useBean scope='request' id='siteToView' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope="request" id="parentName" class="java.lang.String"/>
<jsp:useBean scope='session' id='fromListSite' class="java.lang.String"/>

<c:choose>      
  <c:when test="${fromListSite=='yes'}">
    <c:import url="../include/managestudy-header.jsp"/>
  </c:when>
  <c:otherwise>
    <c:choose>
      <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
          <c:import url="../include/admin-header.jsp"/>
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
<c:choose>      
  <c:when test="${fromListSite=='yes'}">
 <h1><span class="title_manage"><fmt:message key="view_site_details" bundle="${resworkflow}"/></span></h1>
</c:when>
<c:otherwise>
  <c:choose>
    <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
      <h1><span class="title_Admin"><fmt:message key="view_site_details" bundle="${resworkflow}"/></span></h1>
    </c:when>
    <c:otherwise>
       <h1><fmt:message key="view_site_details" bundle="${resworkflow}"/></h1>
    </c:otherwise>
   </c:choose>   
</c:otherwise>
</c:choose>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">   
  <tr valign="top"><td class="table_header_column"><fmt:message key="parent_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${parentName}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="site_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.name}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="unique_protocol_ID" bundle="${resword}"/>: </td><td class="table_cell">
  <c:out value="${siteToView.identifier}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="secondary_IDs" bundle="${resword}"/>:</td><td class="table_cell">
   <c:out value="${siteToView.secondaryIdentifier}"/>
   </td></tr>  
   
  <tr valign="top"><td class="table_header_column"><fmt:message key="principal_investigator" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.principalInvestigator}"/>
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="brief_summary" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.summary}"/>
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="principal_investigator" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.principalInvestigator}"/>
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="start_date" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${siteToView.datePlannedStart}" dateStyle="short"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="estimated_completion_date" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${siteToView.datePlannedEnd}" dateStyle="short"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityName}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_city" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityCity}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_state_province" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityState}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_ZIP" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityZip}"/>&nbsp;
 </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_country" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityCountry}"/>&nbsp;
  </td></tr>   
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactName}"/>&nbsp;
  </td></tr>   
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_degree" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactDegree}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_phone" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactPhone}"/>&nbsp;
 </td></tr>       
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_email" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactEmail}"/>&nbsp;
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="status" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.status.name}"/>
   </td></tr>   
 
  <c:forEach var="config" items="${siteToView.studyParameters}">   
   <c:choose>
   <%-- 
   <c:when test="${config.parameter.handle=='collectDOB'}">
     <tr valign="top"><td class="table_header_column"><fmt:message key="collect_subject" bundle="${resword}"/></td><td class="table_cell">
       <c:choose>
         <c:when test="${config.value.value == '1'}">
          <fmt:message key="yes" bundle="${resword}"/>         
         </c:when>
         <c:when test="${config.value.value == '2'}">
           
          <fmt:message key="only_year_of_birth" bundle="${resword}"/>
          
         </c:when>
         <c:otherwise>
          <fmt:message key="not_used" bundle="${resword}"/>
         </c:otherwise>
      </c:choose>  
      </td></tr>
   
   </c:when>
    
   <c:when test="${config.parameter.handle=='discrepancyManagement'}">
		  <tr valign="top"><td class="table_header_column"><fmt:message key="allow_discrepancy_management" bundle="${resword}"/>:</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value == 'false'}">
		    <fmt:message key="No" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		   <fmt:message key="yes" bundle="${resword}"/>		 
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	
	<c:when test="${config.parameter.handle=='genderRequired'}">	  
		  <tr valign="top"><td class="table_header_column"><fmt:message key="gender_required" bundle="${resword}"/>:</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value == false}">
		   <fmt:message key="No" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		   <fmt:message key="yes" bundle="${resword}"/>		  
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>	  
    <c:when test="${config.parameter.handle=='subjectPersonIdRequired'}">		
		  <tr valign="top"><td class="table_header_column"><fmt:message key="subject_person_ID_required" bundle="${resword}"/>:</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value == 'required'}">
		    <fmt:message key="required" bundle="${resword}"/>		   
		   </c:when>
		    <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'optional'}">
		     <fmt:message key="optional" bundle="${resword}"/>		    
		   </c:when>
		   <c:otherwise>
		     <fmt:message key="not_used" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='subjectIdGeneration'}">	  
		   <tr valign="top"><td class="table_header_column"><fmt:message key="how_to_generate" bundle="${resword}"/>:</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value == 'manual'}">
		     <fmt:message key="manual_entry" bundle="${resword}"/>		  
		   </c:when>
		    <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'auto editable'}">
		      <fmt:message key="auto_generated_and_editable" bundle="${resword}"/>		   
		   </c:when>
		   <c:otherwise>
		    <fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='subjectIdPrefixSuffix'}">	  
		   <tr valign="top"><td class="table_header_column"><fmt:message key="generate_subject_ID" bundle="${resword}"/>:</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value == 'true'}">
		    <fmt:message key="yes" bundle="${resword}"/>	    
		   </c:when>    
		   <c:otherwise>
		   <fmt:message key="no" bundle="${resword}"/>		   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	--%>
	<c:when test="${config.parameter.handle=='interviewerNameRequired'}">
		   <tr valign="top"><td class="table_header_column"><fmt:message key="when_entering_data" bundle="${resword}"/></td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		   <fmt:message key="yes" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		  <fmt:message key="no" bundle="${resword}"/>  
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameDefault'}">	  
		  <tr valign="top"><td class="table_header_column"><fmt:message key="interviewer_name_default_as_blank" bundle="${resword}"/></td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'blank'}">
		    <fmt:message key="blank" bundle="${resword}"/>
		    
		   </c:when>    
		   <c:otherwise>
		   <fmt:message key="pre_populated_from_study_event" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<%--
	<c:when test="${config.parameter.handle=='interviewerNameEditable'}">	  
		  <tr valign="top"><td class="table_header_column"><fmt:message key="interviewer_name_editable" bundle="${resword}"/></td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <fmt:message key="yes" bundle="${resword}"/>		    
		   </c:when>    
		   <c:otherwise>
		   <fmt:message key="no" bundle="${resword}"/>  
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	--%>
	<c:when test="${config.parameter.handle=='interviewDateRequired'}">	  
		  <tr valign="top"><td class="table_header_column"><fmt:message key="interview_date_required" bundle="${resword}"/></td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <fmt:message key="yes" bundle="${resword}"/>		    
		   </c:when>    
		   <c:otherwise>
		    <fmt:message key="no" bundle="${resword}"/>  
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
    </c:when>		  
	<c:when test="${config.parameter.handle=='interviewDateDefault'}">	  
		  <tr valign="top"><td class="table_header_column"><fmt:message key="interview_date_default_as_blank" bundle="${resword}"/></td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'blank'}">
		   <fmt:message key="blank" bundle="${resword}"/>
		   
		   </c:when>    
		   <c:otherwise>
		    
		   <fmt:message key="pre_populated_from_study_event" bundle="${resword}"/>  
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	 </c:when>
	 <%-- 
	 <c:otherwise>	  
		  <tr valign="top"><td class="table_header_column"><fmt:message key="interview_date_editable" bundle="${resword}"/></td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    <fmt:message key="yes" bundle="${resword}"/>		   
		   </c:when>    
		   <c:otherwise>
		    <fmt:message key="no" bundle="${resword}"/>  
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
     </c:otherwise>
     --%>
   </c:choose>
   
  </c:forEach>

</table>

</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 
 <c:choose>      
  <c:when test="${fromListSite=='yes'}">
   <p><a href="ListSite"><fmt:message key="go_back_to_site_list" bundle="${resword}"/></a></p> 
  </c:when>
  <c:otherwise>
    <c:choose>
      <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
       <p><a href="ListStudy"><fmt:message key="go_back_to_study_list" bundle="${resword}"/></a></p> 
      </c:when>
      <c:otherwise>
         <p><a href="MainMenu"><fmt:message key="go_back_home" bundle="${resword}"/></a></p> 
      </c:otherwise>
    </c:choose>
  </c:otherwise>
 </c:choose>    
          
 <br><br>

<!-- EXPANDING WORKFLOW BOX -->

<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
	<tr>
		<td id="sidebar_Workflow_closed" style="display: none">
		<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/tab_Workflow_closed.gif" border="0"></a>
	</td>
	<td id="sidebar_Workflow_open" style="display: all">
	<table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
		<tr>
			<td class="workflowBox_T" valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="workflow_tab">
					<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

					<b><fmt:message key="workflow" bundle="${resword}"/></b>

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
				
					
						
							<fmt:message key="manage_study" bundle="${resworkflow}"/>
					
				
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
				
					
							 <fmt:message key="manage_sites" bundle="${resworkflow}"/>
					
				
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
				
					
							 <b><fmt:message key="view_site" bundle="${resworkflow}"/></b>
					
				
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
