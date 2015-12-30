<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />

<jsp:include page="include/home-header.jsp"/>
<jsp:include page="include/breadcrumb.jsp"/>
<jsp:include page="include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
        If needed you may change the study/site or request access to a new study with a different role.
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>

<jsp:include page="include/sideInfo.jsp"/>


<h1>OpenClinica: Managing Clinical Research Studies</h1>

<c:set var="roleName" value=""/>
<c:if test="${userRole != null && userRole.role.name != 'invalid'}">
<c:set var="roleName" value="${userRole.role.name}"/>
<p>As a <c:out value="${userRole.role.description}"/> for the study: <span class="alert">
<c:choose>
   <c:when test="${study.parentStudyId>0}">
	 <a href="ViewSite?id=<c:out value="${study.id}"/>">
   </c:when>
   <c:otherwise>
	 <a href="ViewStudy?id=<c:out value="${study.id}"/>&viewFull=yes">
   </c:otherwise>
</c:choose>
<c:out value="${study.name}"/></a></span> and Protocol ID: <span class="alert"><c:out value="${study.identifier}"/></span>,  you may access the following modules and perform available actions authorized for your role in the study.
</p>

</c:if>
	<h2>Available Actions</h2>
      
		<table border="0" cellpadding="0" cellspacing="0">
		<c:choose>
		<c:when test="${userBean != null && userBean.id>0 }">
			<tr>
			  <c:if test="${roleName=='coordinator' || roleName=='director' || roleName=='ra' || roleName=='investigator'}">
				<td class="homeboxes">
	
				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	                   
						<div class="homebox_center">
                      
						 <div class="homebox_title"><a href="ListStudySubjectsSubmit">Submit Data <a href="javascript:openDocWindow('help/2_0_submitData_Help.html')"><img src="images/bt_Help_Home.gif" border="0" alt="Help" title="Help"></a></a></div><br>

						 <div class="homebox_bullets"><a href="ListStudySubjectsSubmit">View All Subjects</a> </div><br>

						 <div class="homebox_bullets"><a href="AddNewSubject?instr=1">Add Subject</a></div><br>

						 <div class="homebox_bullets"><a href="CreateNewStudyEvent">Add a new Study Event</a></div><br>
						 
						 <div class="homebox_bullets"><a href="ViewStudyEvents">View Study Events</a></div><br>


                     
						</div>
	
					</div></div></div></div></div></div></div></div>
	
				</td>
				</c:if>
	            <c:if test="${roleName=='coordinator' || roleName=='director' || roleName=='investigator'}">
				<td class="homeboxes">
	
				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
						<div class="homebox_center">						

						<div class="homebox_title"><a href="ExtractDatasetsMain">Extract Data <a href="javascript:openDocWindow('help/3_0_extractData_Help.html')"><img src="images/bt_Help_Home.gif" border="0" alt="Help" title="Help"></a></a></div><br>

						<div class="homebox_bullets"><a href="ViewDatasets">View Datasets</a></div><br>

						<div class="homebox_bullets"><a href="CreateDataset">Extract Datasets</a></div><br>
	                    
	                    <!--<div class="homebox_bullets"><a href="CreateFiltersOne">View Filters</a></div><br> -->
							
						</div>
	
					</div></div></div></div></div></div></div></div>
	
				</td>
				</c:if>
				<c:if test="${roleName=='coordinator' || roleName=='director'}">
				<td class="homeboxes">
	
				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
						<div class="homebox_center">
						
						<div class="homebox_title"><a href="ManageStudy">Manage Study <a href="javascript:openDocWindow('help/4_0_manageStudy_Help.html')"><img src="images/bt_Help_Home.gif" border="0" alt="Help" title="Help"></a></a></div><br>

						<div class="homebox_bullets"><a href="ListStudyUser">Manage Users</a></div><br>

						<div class="homebox_bullets"><a href="ListStudySubject">Manage Subjects</a></div><br>

						<div class="homebox_bullets"><a href="ListSite">Manage Sites</a></div><br>
	
	                    <div class="homebox_bullets"><a href="ListEventDefinition">Manage Definitions</a></div><br>
	                    
	                    <div class="homebox_bullets"><a href="ListCRF">Manage CRFs</a></div><br>
	                    
	                    <div class="homebox_bullets"><a href="ViewStudyEvents">View Study Events</a></div><br>
	                    
	                    <div class="homebox_bullets"><a href="ViewNotes">Manage Discrepancies</a></div><br>
	                    
	                    <div class="homebox_bullets"><a href="ListSubjectGroupClass">Manage Groups</a></div><br>
					
						</div>
	
					</div></div></div></div></div></div></div></div>
	
				</td>
				</c:if>
				<c:if test="${userBean.sysAdmin}">
				<td class="homeboxes">
	
				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
						<div class="homebox_center">

						<div class="homebox_title"><a href="AdminSystem">Business Admin <a href="javascript:openDocWindow('help/5_0_administerSystem_Help.html')"><img src="images/bt_Help_Home.gif" border="0" alt="Help" title="Help"></a></a></div><br>

						<div class="homebox_bullets"><a href="ListUserAccounts">Administer Users</a></div><br>
						<div class="homebox_bullets"><a href="ListSubject">Administer Subjects</a></div><br>

						<div class="homebox_bullets"><a href="ListStudy">Administer Studies</a></div><br>
						                    
	                    <div class="homebox_bullets"><a href="ListCRF">Administer CRFs</a></div><br>
						</div>
	
					</div></div></div></div></div></div></div></div>
	
				</td>
			   </c:if>	
			</tr>
		 </c:when>
		 <c:otherwise>
		  <tr>
				<td class="homeboxes">
	
				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
						<div class="homebox_center">

						<div class="homebox_title"><span class="inactive">Submit Data</span></div><br>

						<div class="homebox_bullets"><span class="inactive">View All Subjects</span></div><br>
						<div class="homebox_bullets"><span class="inactive">Add Subject</span></div><br>

						<div class="homebox_bullets"><span class="inactive">Add a new Study Event</span></div><br>

						
						<div class="homebox_bullets"><span class="inactive">View Study Events</span></div><br>
	
						</div>
	
					</div></div></div></div></div></div></div></div>
	
				</td>
				<td class="homeboxes">
	
				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
						<div class="homebox_center">

						<div class="homebox_title"><span class="inactive">Extract Data</span></div><br>

						<div class="homebox_bullets"><span class="inactive">View Datasets</span></div><br>

						<div class="homebox_bullets"><span class="inactive">Extract Datasets</span></div><br>
						
						<div class="homebox_bullets"><span class="inactive">View Filters</span></div><br>
	
						</div>
	
					</div></div></div></div></div></div></div></div>
	
				</td>
				<td class="homeboxes">
	
				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
						<div class="homebox_center">

						<div class="homebox_title"><span class="inactive">Manage Study</span></div><br>

						<div class="homebox_bullets"><span class="inactive">Manage Users</span></div><br>

						<div class="homebox_bullets"><span class="inactive">Manage Subjects</span></div><br>

						<div class="homebox_bullets"><span class="inactive">Manage Sites</span></div><br>
						<div class="homebox_bullets"><span class="inactive">Manage Event Definitions</span></div><br>
						<div class="homebox_bullets"><span class="inactive">Manage CRFs</span></div><br>
						<div class="homebox_bullets"><span class="inactive">View Study Events</span></div><br>
						
	
						</div>
	
					</div></div></div></div></div></div></div></div>
	
				</td>
			</tr>
		 </c:otherwise>
		 </c:choose>
		 
		</table>

		<br><br>
  <c:if test="${userBean != null && userBean.id>0 }">	

	<!-- My OpenClinica Box -->
     
		<!-- These DIVs define shaded box borders -->
		 <div style="width: 650px">
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

				<div class="textbox_center">

				<div class="homebox_title">My OpenClinica <a href="javascript:openDocWindow('help/1_0_myOpenClinica_Help.html')"><img src="images/bt_Help_Home.gif" border="0" alt="Help" title="Help"></a></div><br>

				<span class="homebox_bullets"><a href="UpdateProfile">Update Profile</a></span>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<span class="homebox_bullets"><a href="RequestStudy">Request Study Access</a></span>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<!--<span class="homebox_bullets"><a href="<%=request.getHeader("Referer")%>">Recent Links</a></span>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-->
				<span class="homebox_bullets"><a href="ChangeStudy">Change Study/Site</a></span>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<span class="homebox_bullets"><a href="Logout">Logout</a> <a href="javascript:openDocWindow('help/1_4_logout_Help.html')"><img src="images/bt_Help_Home.gif" border="0" alt="Help" title="Help"></a></span>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</div>

			</div></div></div></div></div></div></div></div>

		</div>
		<br>

	<!-- My OpenClinica Box -->
  </c:if>
<!-- End Main Content Area -->

<jsp:include page="include/footer.jsp"/>
