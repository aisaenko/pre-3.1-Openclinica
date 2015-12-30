<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
          <fmt:message key="confirm_removal_of_this_subject_from_study"  bundle="${resword}"/> <c:out value="${study.name}"/>. <fmt:message key="the_subject_and_all_data_associated_with_it_in_this_Study"  bundle="${resword}"/>
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="session" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="events" class="java.util.ArrayList"/>
<h1><span class="title_manage">
<fmt:message key="remove_subject_from_Study"  bundle="${resword}"/>
</span></h1>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column"><fmt:message key="study_subject_ID" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${studySub.label}"/>
  <%--<c:out value="${subject.id}"/>--%>
  <%--above removed 092007, tbh--%>
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="person_ID" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${subject.uniqueIdentifier}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="gender" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${subject.gender}"/></td></tr>
    
    <%-- below line removed because it causes confusion with OpenClinica Study Subject ID line above, tbh --%>
  <%-- <tr valign="top"><td class="table_header_column"><fmt:message key="study_subject_ID" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${studySub.id}"/></td></tr> --%>
  <%-- <tr valign="top"><td class="table_header_column"><fmt:message key="label" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${studySub.label}"/></td></tr> --%>
  <tr valign="top"><td class="table_header_column"><fmt:message key="secondary_identifier" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${studySub.secondaryLabel}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="enrollment_date" bundle="${resword}"/>:</td>
  <td class="table_cell"><fmt:formatDate value="${studySub.enrollmentDate}" dateStyle="short"/>&nbsp;</td></tr> 
  <tr valign="top"><td class="table_header_column"><fmt:message key="created_by" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${studySub.owner.name}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_created" bundle="${resword}"/>:</td><td class="table_cell"><fmt:formatDate value="${studySub.createdDate}" dateStyle="short"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="last_updated_by" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${studySub.updater.name}"/>&nbsp;
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_updated" bundle="${resword}"/>:</td><td class="table_cell"><fmt:formatDate value="${studySub.updatedDate}" dateStyle="short"/>&nbsp;
  </td></tr>
 </table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 <c:if test="${!empty events}">
 <span class="table_title_manage"><fmt:message key="subject_events" bundle="${resword}"/> </span>
 <div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td class="table_header_column_top"><fmt:message key="last_update" bundle="${resword}"/></td>
        <td class="table_header_column_top"><fmt:message key="event" bundle="${resword}"/></td>
        <td class="table_header_column_top"><fmt:message key="start_date" bundle="${resword}"/></td>
        <td class="table_header_column_top"><fmt:message key="end_date" bundle="${resword}"/></td>
        <td class="table_header_column_top"><fmt:message key="location" bundle="${resword}"/></td>
        <td class="table_header_column_top"><fmt:message key="update_by" bundle="${resword}"/></td>
        <td class="table_header_column_top"><fmt:message key="status" bundle="${resword}"/></td>
      </tr>
    <c:forEach var="event" items="${events}">
    <tr>      
        <td class="table_cell"><fmt:formatDate value="${event.updatedDate}" dateStyle="short"/></td>
        <td class="table_cell"><c:out value="${event.studyEventDefinition.name}"/></td>
        <td class="table_cell"><fmt:formatDate value="${event.dateStarted}" dateStyle="short"/></td>
        <td class="table_cell"><fmt:formatDate value="${event.dateEnded}" dateStyle="short"/></td>
        <td class="table_cell"><c:out value="${event.location}"/></td>
        <td class="table_cell"><c:out value="${event.updater.name}"/></td>
        <td class="table_cell"><c:out value="${event.status.name}"/></td>
     </tr>
     </c:forEach>  
   </table>
 
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
   </c:if>  
   
   <c:choose>
    <c:when test="${!empty events}">
     <form action='RemoveStudySubject?action=submit&id=<c:out value="${studySub.id}"/>&subjectId=<c:out value="${studySub.subjectId}"/>&studyId=<c:out value="${studySub.studyId}"/>' method="POST">
      <input type="submit" name="submit" value="<fmt:message key="remove_study_subject" bundle="${resword}"/>" class="button_xlong" onClick='return confirm("<fmt:message key="this_subject_has_data_from_events_remove" bundle="${resword}"/>");'>
     </form>    
    </c:when>
    <c:otherwise>
     <form action='RemoveStudySubject?action=submit&id=<c:out value="${studySub.id}"/>&subjectId=<c:out value="${studySub.subjectId}"/>&studyId=<c:out value="${studySub.studyId}"/>' method="POST">
      <input type="submit" name="submit" value="<fmt:message key="remove_study_subject" bundle="${resword}"/>" class="button_xlong" onClick='return confirm("<fmt:message key="are_you_sure_you_want_to_remove_it" bundle="${resword}"/>");'>
     </form> 
    
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
				
					
						
							<fmt:message key="manage_study" bundle="${resword}"/>
					
				
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
				
					
							<fmt:message key="manage_subject" bundle="${resword}"/>
					
				
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
				
					
							<b><fmt:message key="remove_study_subject" bundle="${resword}"/></b>
					
				
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
