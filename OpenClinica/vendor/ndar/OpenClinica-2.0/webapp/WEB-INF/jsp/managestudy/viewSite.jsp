<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

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
<c:choose>      
  <c:when test="${fromListSite=='yes'}">
 <h1><span class="title_manage">View Site Details</span></h1>
</c:when>
<c:otherwise>
  <c:choose>
    <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
      <h1><span class="title_Admin">View Site Details</span></h1>
    </c:when>
    <c:otherwise>
       <h1>View Site Details</h1>
    </c:otherwise>
   </c:choose>   
</c:otherwise>
</c:choose>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">   
  <tr valign="top"><td class="table_header_column">Parent Name:</td><td class="table_cell">
  <c:out value="${parentName}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Site Name:</td><td class="table_cell">
  <c:out value="${siteToView.name}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Unique Protocol ID:</td><td class="table_cell">
  <c:out value="${siteToView.identifier}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Secondary IDs:</td><td class="table_cell">
   <c:out value="${siteToView.secondaryIdentifier}"/>
   </td></tr>  
   
  <tr valign="top"><td class="table_header_column">Principal Investigator:</td><td class="table_cell">
  <c:out value="${siteToView.principalInvestigator}"/>
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Brief Summary:</td><td class="table_cell">
  <c:out value="${siteToView.summary}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Principal Investigator:</td><td class="table_cell">
  <c:out value="${siteToView.principalInvestigator}"/>
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Start Date:</td><td class="table_cell">
  <fmt:formatDate value="${siteToView.datePlannedStart}" pattern="MM/dd/yyyy"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Estimated Completion Date:</td><td class="table_cell">
  <fmt:formatDate value="${siteToView.datePlannedEnd}" pattern="MM/dd/yyyy"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility Name:</td><td class="table_cell">
  <c:out value="${siteToView.facilityName}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility City:</td><td class="table_cell">
  <c:out value="${siteToView.facilityCity}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility State/Province:</td><td class="table_cell">
  <c:out value="${siteToView.facilityState}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility ZIP:</td><td class="table_cell">
  <c:out value="${siteToView.facilityZip}"/>&nbsp;
 </td></tr>
  
  <tr valign="top"><td class="table_header_column">Facility Country:</td><td class="table_cell">
  <c:out value="${siteToView.facilityCountry}"/>&nbsp;
  </td></tr>   
  
  <tr valign="top"><td class="table_header_column">Facility Contact Name:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactName}"/>&nbsp;
  </td></tr>   
  
  <tr valign="top"><td class="table_header_column">Facility Contact Degree:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactDegree}"/>&nbsp;
  </td></tr> 
  
  <tr valign="top"><td class="table_header_column">Facility Contact Phone:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactPhone}"/>&nbsp;
 </td></tr>       
  
  <tr valign="top"><td class="table_header_column">Facility Contact Email:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactEmail}"/>&nbsp;
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column">Status:</td><td class="table_cell">
  <c:out value="${siteToView.status.name}"/>
   </td></tr>   
 
  <c:forEach var="config" items="${siteToView.studyParameters}">   
   <c:choose>
   <c:when test="${config.parameter.handle=='collectDOB'}">
     <tr valign="top"><td class="table_header_column">Collect Subject Date of Birth?:</td><td class="table_cell">
       <c:choose>
         <c:when test="${config.value.value == '1'}">
          Yes          
         </c:when>
         <c:when test="${config.value.value == '2'}">
           
          only Year of Birth
          
         </c:when>
         <c:otherwise>
          Not Used
         </c:otherwise>
      </c:choose>  
      </td></tr>
   
   </c:when>
    
   <c:when test="${config.parameter.handle=='discrepancyManagement'}">
		  <tr valign="top"><td class="table_header_column">Allow Discrepancy Management?:</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value == 'false'}">
		    No
		   </c:when>
		   <c:otherwise>
		   Yes		 
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	
	<c:when test="${config.parameter.handle=='genderRequired'}">	  
		  <tr valign="top"><td class="table_header_column">Gender Required?:</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value == false}">
		   No
		   </c:when>
		   <c:otherwise>
		   Yes		  
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>	  
    <c:when test="${config.parameter.handle=='subjectPersonIdRequired'}">		
		  <tr valign="top"><td class="table_header_column">Subject Person ID Required?:</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value == 'required'}">
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
	</c:when>
	<c:when test="${config.parameter.handle=='subjectIdGeneration'}">	  
		   <tr valign="top"><td class="table_header_column">How to Generate the Subject Person ID?:</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value == 'manual'}">
		     Manual Entry		  
		   </c:when>
		    <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'auto editable'}">
		      Auto-generated and Editable		   
		   </c:when>
		   <c:otherwise>
		    Auto-generated and Non-editable
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='subjectIdPrefixSuffix'}">	  
		   <tr valign="top"><td class="table_header_column">Generate Subject ID Automatically With Prefix/Suffix?:</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value == 'true'}">
		    Yes		    
		   </c:when>    
		   <c:otherwise>
		    No		   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameRequired'}">
		   <tr valign="top"><td class="table_header_column">When Entering Data Entry, Interviewer Name Required?</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		   Yes
		   
		   </c:when>    
		   <c:otherwise>
		   No   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameDefault'}">	  
		  <tr valign="top"><td class="table_header_column">Interviewer Name Default as Blank?</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'blank'}">
		    Blank
		    
		   </c:when>    
		   <c:otherwise>
		   Pre-Populated from Study Event   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameEditable'}">	  
		  <tr valign="top"><td class="table_header_column">Interviewer Name Editable?</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    Yes		    
		   </c:when>    
		   <c:otherwise>
		   No   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewDateRequired'}">	  
		  <tr valign="top"><td class="table_header_column">Interview Date Required?</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    Yes		    
		   </c:when>    
		   <c:otherwise>
		    No   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
    </c:when>		  
	<c:when test="${config.parameter.handle=='interviewDateDefault'}">	  
		  <tr valign="top"><td class="table_header_column">Interview Date Default as Blank?</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'blank'}">
		   Blank
		   
		   </c:when>    
		   <c:otherwise>
		    
		   Pre-Populated from Study Event   
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	 </c:when>
	 <c:otherwise>	  
		  <tr valign="top"><td class="table_header_column">Interview Date Editable?</td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'true'}">
		    Yes		   
		   </c:when>    
		   <c:otherwise>
		    No   
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
<br>
 
 <c:choose>      
  <c:when test="${fromListSite=='yes'}">
   <p><a href="ListSite">Go Back to Site List</a></p> 
  </c:when>
  <c:otherwise>
    <c:choose>
      <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
       <p><a href="ListStudy">Go Back to Study List</a></p> 
      </c:when>
      <c:otherwise>
         <p><a href="MainMenu">Go Back Home</a></p> 
      </c:otherwise>
    </c:choose>
  </c:otherwise>
 </c:choose>    
          
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
				
					
							 <b>View Site</b>
					
				
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
