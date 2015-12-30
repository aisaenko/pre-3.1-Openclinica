<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

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

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
        <fmt:message key="may_change_request_access" bundle="${restext}"/>
		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>

<jsp:include page="include/sideInfo.jsp"/>


<h1> <fmt:message key="description" bundle="${restext}"/></h1>

<c:set var="roleName" value=""/>
<c:if test="${userRole != null && !userRole.invalid}">
<c:set var="roleName" value="${userRole.role.name}"/>

<c:set var="linkStudy">
<c:choose>
   <c:when test="${study.parentStudyId>0}">
	 <a href="ViewSite?id=<c:out value="${study.id}"/>">
   </c:when>
   <c:otherwise>
	 <a href="ViewStudy?id=<c:out value="${study.id}"/>&viewFull=yes">
   </c:otherwise>
</c:choose>
<c:out value="${study.name}"/></a></span>
</c:set>

<c:set var="studyidentifier">
   <span class="alert"><c:out value="${study.identifier}"/></span>
</c:set>
<p>
<fmt:message key="as_a_for_the_study_and_protocol_ID_may_access_modules_and_perform" bundle="${restext}">
	<fmt:param value="${userRole.role.description}"/>
	<fmt:param value="${linkStudy}"/>
	<fmt:param value="${studyidentifier}"/>
</fmt:message>
</p>

</c:if>

	<h2> <fmt:message key="actions_available" bundle="${restext}"/></h2>

		<table border="0" cellpadding="0" cellspacing="0">
		<c:choose>
		<c:when test="${userBean != null && userBean.id>0 }">
			<tr>
			 <c:if test="${userRole.submitData || userRole.monitor}">
			 	<td class="homeboxes">

				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

						<div class="homebox_center">

						 <div class="homebox_title"><a href="javascript:openDocWindow('help/2_0_submitData_Help.html')"><img width="24" height="15" src="images/bt_Help_Home_gray.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
                             <a href="ListStudySubjectsSubmit"><fmt:message key="submit_data" bundle="${resworkflow}"/></a></div><br>

						 <div class="homebox_bullets"><a href="ListStudySubjectsSubmit"> <fmt:message key="view_all_subjects" bundle="${resworkflow}"/> </a> </div><br>
                        <c:if test="${!userRole.monitor}">
						 <div class="homebox_bullets"><a href="AddNewSubject"> <fmt:message key="add_subject" bundle="${resworkflow}"/> </a></div><br>

						 <div class="homebox_bullets"><a href="CreateNewStudyEvent"> <fmt:message key="add_new_study_event" bundle="${resworkflow}"/> </a></div><br>
                        </c:if>
						 <div class="homebox_bullets"><a href="ViewStudyEvents"> <fmt:message key="view_study_events" bundle="${resworkflow}"/> </a></div><br>
                         <div class="homebox_bullets"><a href="ListDiscNotesSubjectServlet?module=submit"><fmt:message key="notes_discrepancies" bundle="${resworkflow}"/></a></div><br>
                        <c:if test="${!userRole.monitor}">
                         <div class="homebox_bullets"><a href="ImportCRFData"><fmt:message key="import_data" bundle="${resworkflow}"/></a></div><br>
                        </c:if>
						</div>

					</div></div></div></div></div></div></div></div>

				</td>
				</c:if>
	            <c:if test="${userRole.extractData || userRole.monitor}">
				<td class="homeboxes">

				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

						<div class="homebox_center">

						<div class="homebox_title"><a href="javascript:openDocWindow('help/3_0_extractData_Help.html')"><img width="24" height="15" src="images/bt_Help_Home_gray.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a> <a href="ExtractDatasetsMain"><fmt:message key="extract_data" bundle="${resworkflow}"/></a></div><br>

						<div class="homebox_bullets"><a href="ViewDatasets"> <fmt:message key="view_dataset" bundle="${resworkflow}"/> </a></div><br>

						<div class="homebox_bullets"><a href="CreateDataset"> <fmt:message key="extract_datasets" bundle="${resworkflow}"/> </a></div><br>

	                    <!--<div class="homebox_bullets"><a href="CreateFiltersOne">View Filters</a></div><br> -->

						</div>

					</div></div></div></div></div></div></div></div>

				</td>
				</c:if>
				<c:if test="${userRole.manageStudy}">

				<td class="homeboxes">

				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

						<div class="homebox_center">

						<div class="homebox_title"><a href="javascript:openDocWindow('help/4_0_manageStudy_Help.html')"><img width="24" height="15" src="images/bt_Help_Home_gray.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a> <a href="ManageStudy"><fmt:message key="manage_study" bundle="${resworkflow}"/></a></div><br>

						<div class="homebox_bullets"><a href="ListStudyUser"><fmt:message key="manage_users" bundle="${resworkflow}"/></a></div><br>

						<div class="homebox_bullets"><a href="ListStudySubject"><fmt:message key="manage_subjects" bundle="${resworkflow}"/></a></div><br>
                       <c:if test="${study.parentStudyId == 0}">
						<div class="homebox_bullets"><a href="ListSite"><fmt:message key="manage_sites" bundle="${resworkflow}"/></a></div><br>
                       </c:if>
	                    <div class="homebox_bullets"><a href="ListEventDefinition"><fmt:message key="manage_event_definitions" bundle="${resworkflow}"/></a></div><br>
                       <c:if test="${study.parentStudyId == 0}">
	                    <div class="homebox_bullets"><a href="ListCRF?module=manage"><fmt:message key="manage_CRFs" bundle="${resworkflow}"/></a></div><br>
                       </c:if>
	                    <div class="homebox_bullets"><a href="ViewStudyEvents?module=manage"><fmt:message key="view_study_events" bundle="${resworkflow}"/></a></div><br>

	                    <div class="homebox_bullets"><a href="ListDiscNotesSubjectServlet?module=manage"><fmt:message key="notes_discrepancies" bundle="${resworkflow}"/></a></div><br>

	                    <div class="homebox_bullets"><a href="ListSubjectGroupClass"><fmt:message key="manage_groups" bundle="${resworkflow}"/></a></div><br>
                       <c:if test="${study.parentStudyId == 0}">
	                    <div class="homebox_bullets"><a href="ViewRuleAssignment"><fmt:message key="manage_rules" bundle="${resworkflow}"/></a></div><br>
                       </c:if>
						</div>

					</div></div></div></div></div></div></div></div>

				</td>
				</c:if>
				<c:if test="${userBean.sysAdmin}">
				<td class="homeboxes">

				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

						<div class="homebox_center">

						<div class="homebox_title">
              <a href="javascript:openDocWindow('help/5_0_administerSystem_Help.html')"><img width="24" height="15" src="images/bt_Help_Home_gray.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a> <a href="AdminSystem"><fmt:message key="business_admin" bundle="${resworkflow}"/></a></div><br>

						<div class="homebox_bullets"><a href="ListUserAccounts"><fmt:message key="administer_users" bundle="${resworkflow}"/></a></div><br>
						<div class="homebox_bullets"><a href="ListSubject"><fmt:message key="administer_subjects" bundle="${resworkflow}"/></a></div><br>

						<div class="homebox_bullets"><a href="ListStudy"><fmt:message key="administer_studies" bundle="${resworkflow}"/></a></div><br>

	                    <div class="homebox_bullets"><a href="ListCRF?module=admin"><fmt:message key="administer_CRFs" bundle="${resworkflow}"/></a></div><br>
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

						<div class="homebox_title"><span class="inactive"><fmt:message key="submit_data" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="view_all_subjects" bundle="${resworkflow}"/></span></div><br>
						<div class="homebox_bullets"><span class="inactive"><fmt:message key="add_subject" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="add_new_study_event" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="view_study_events" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="notes_discrepancies" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="import_data" bundle="${resworkflow}"/></span></div><br>

						</div>

					</div></div></div></div></div></div></div></div>

				</td>
				<td class="homeboxes">

				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

						<div class="homebox_center">

						<div class="homebox_title"><span class="inactive"><fmt:message key="extract_data" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="view_dataset" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="extract_datasets" bundle="${resworkflow}"/></span></div><br>

						<!-- <div class="homebox_bullets"><span class="inactive"><fmt:message key="view_filters" bundle="${resworkflow}"/></span></div><br>-->

						</div>

					</div></div></div></div></div></div></div></div>

				</td>
				<td class="homeboxes">

				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

						<div class="homebox_center">

						<div class="homebox_title"><span class="inactive"><fmt:message key="manage_study" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="manage_users" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="manage_subjects" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="manage_sites" bundle="${resworkflow}"/></span></div><br>
						<div class="homebox_bullets"><span class="inactive"><fmt:message key="manage_event_definitions" bundle="${resworkflow}"/></span></div><br>
						<div class="homebox_bullets"><span class="inactive"><fmt:message key="manage_CRFs" bundle="${resworkflow}"/></span></div><br>
						<div class="homebox_bullets"><span class="inactive"><fmt:message key="view_study_events" bundle="${resworkflow}"/></span></div><br>
	                    <div class="homebox_bullets"><span class="inactive"><fmt:message key="notes_discrepancies" bundle="${resworkflow}"/></span></div><br>
	                    <div class="homebox_bullets"><span class="inactive"><fmt:message key="manage_groups" bundle="${resworkflow}"/></span></div><br>
	                    <div class="homebox_bullets"><span class="inactive"><fmt:message key="manage_rules" bundle="${resworkflow}"/></span></div><br>


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

				<div class="homebox_title"><fmt:message key="my_openclinica" bundle="${restext}"/> <a href="javascript:openDocWindow('help/1_0_myOpenClinica_Help.html')"><img src="images/bt_Help_Home.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></div><br>

				<span class="homebox_bullets"><a href="UpdateProfile"><fmt:message key="update_profile" bundle="${resworkflow}"/></a></span>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<span class="homebox_bullets"><a href="RequestStudy"><fmt:message key="request_study_access" bundle="${resworkflow}"/></a></span>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<!--<span class="homebox_bullets"><a href="<%=request.getHeader("Referer")%>">Recent Links</a></span>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-->
				<span class="homebox_bullets"><a href="ChangeStudy"><fmt:message key="change_study_site" bundle="${resworkflow}"/></a></span>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<span class="homebox_bullets"><a href="Logout"><fmt:message key="log_out" bundle="${resword}"/></a></span>
				 <a href="javascript:openDocWindow('help/1_4_logout_Help.html')"><img src="images/bt_Help_Home.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</div>

			</div></div></div></div></div></div></div></div>

		</div>
		<br>

	<!-- My OpenClinica Box -->
  </c:if>
<!-- End Main Content Area -->

<jsp:include page="include/footer.jsp"/>
