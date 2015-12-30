<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

<%
//if (session.isNew()){
// String referer = request.getHeader("Referer");
// if (referer == null){
//   response.sendRedirect("MainMenu");
// } else {
//   response.sendRedirect(referer);
// }
 //}
%>
<jsp:include page="../login-include/login-header.jsp"/>

<jsp:include page="../login-include/login-sidebar.jsp"/>
<!-- Main Content Area -->

	<!-- Page Title -->

   <h1><fmt:message key="description" bundle="${restext}"/></h1>

	<jsp:include page="../login-include/login-alertbox.jsp"/>

		<p><span class="OpenClinica">OpenClinica </span> <fmt:message key="software_platform" bundle="${restext}"/> </p>

		<p><span class="OpenClinica">OpenClinica</span> <fmt:message key="allows" bundle="${restext}"/>

		<ul>

		<li><fmt:message key="data_submission_validation_annotation" bundle="${restext}"/></li>

		<li><fmt:message key="data_extraction_filtering_and_analisys" bundle="${restext}"/></li>

		<li><fmt:message key="study_management_by_directors_coordinators" bundle="${restext}"/></li>

		<li><fmt:message key="system_oversight_auditing_configuration_reporting" bundle="${restext}"/></li>
		</ul>

		</p>

 		<p><fmt:message key="register" bundle="${restext}"/>
		</p>
        <br>
		<h2><fmt:message key="actions_available" bundle="${restext}"/><span style="font-size:12px"><fmt:message key="upon_login_based_on_user_role" bundle="${restext}"/></span></h2>

		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="homeboxes">

				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

						<div class="homebox_center">

						<div class="homebox_title"><span class="inactive"><fmt:message key="submit_data" bundle="${resworkflow}"/></span></div><br>
						<div class="homebox_bullets"><span class="inactive"><fmt:message key="view_all_subjects" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="enroll_subject" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="add_new_study_event" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="view_study_events" bundle="${resworkflow}"/></span></div><br>

						<div class="homebox_bullets"><span class="inactive"><fmt:message key="notes_discrepancies" bundle="${resworkflow}"/></a></div><br>

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

						<!-- <div class="homebox_bullets"><span class="inactive"><fmt:message key="view_filters" bundle="${resworkflow}"/></span></div><br> -->

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
		</table>

<!-- End Main Content Area -->
<jsp:include page="../login-include/login-footer.jsp"/>
