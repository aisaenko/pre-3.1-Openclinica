<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:import url="../include/admin-header.jsp"/>
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


<jsp:useBean scope='request' id='studySubs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='events' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='subjectToRemove' class='org.akaza.openclinica.bean.submit.SubjectBean'/>

<h1><span class="title_Admin"><fmt:message key="confirm_removal_of_subject" bundle="${resword}"/></span></h1>

<div class="table_title_Admin"><fmt:message key="you_choose_to_remove_the_following_subject" bundle="${restext}"/>:</div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">

<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top" ><td class="table_header_column"><fmt:message key="person_ID" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${subjectToRemove.uniqueIdentifier}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="gender" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${subjectToRemove.gender}"/>
  </td>
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_of_birth" bundle="${resword}"/>:</td><td class="table_cell">  
  <fmt:formatDate value="${subjectToRemove.dateOfBirth}" dateStyle="short"/>
  </td>
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_created" bundle="${resword}"/>:</td><td class="table_cell">  
  <fmt:formatDate value="${subjectToRemove.createdDate}" dateStyle="short"/>
  </td>
  </tr> 
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br/><br/>
<div class="table_title_Admin"> <fmt:message key="associated_study_subjects" bundle="${resword}"/> </div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
 <table border="0" cellpadding="0" cellspacing="0" width="100%"> 
 <tr valign="top">
    <td class="table_header_column_top"><fmt:message key="study_subject_ID" bundle="${resword}"/></td>       
    <td class="table_header_column_top"><fmt:message key="secondary_label" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="study_record_ID" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="enrollment_date" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="date_created" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="created_by" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="status" bundle="${resword}"/></td>       
    </tr>   
  <c:forEach var="studySub" items="${studySubs}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${studySub.label}"/></td>               
    <td class="table_cell"><c:out value="${studySub.secondaryLabel}"/>&nbsp;</td> 
    <td class="table_cell"><c:out value="${studySub.studyId}"/></td>
    <td class="table_cell"><fmt:formatDate value="${studySub.enrollmentDate}" dateStyle="short"/></td>       
    <td class="table_cell"><fmt:formatDate value="${studySub.createdDate}" dateStyle="short"/></td>       
    <td class="table_cell"><c:out value="${studySub.owner.name}"/></td>  
    <td class="table_cell"><c:out value="${studySub.status.name}"/></td>       
    </tr>  
 </c:forEach>    
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br><br>
<div class="table_title_Admin"><fmt:message key="associated_study_events" bundle="${resword}"/></div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
 <table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
    <td class="table_header_column_top"><fmt:message key="record_ID" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="location" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="date_started" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="created_by" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="status" bundle="${resword}"/></td>
    </tr>       
  <c:forEach var="event" items="${events}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${event.id}"/></td>           
    <td class="table_cell"><c:out value="${event.location}"/>&nbsp;</td>    
    <td class="table_cell"><fmt:formatDate value="${event.createdDate}" dateStyle="short"/></td>       
    <td class="table_cell"><c:out value="${event.owner.name}"/></td> 
    <td class="table_cell"><c:out value="${event.status.name}"/></td>
    </tr>    
 </c:forEach>    
</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
<br>
<form action='RemoveSubject?action=submit&id=<c:out value="${subjectToRemove.id}"/>' method="POST">
 <input type="submit" name="submit" value="<fmt:message key="remove_subject" bundle="${resword}"/>" class="button_long" onClick='return confirm("<fmt:message key="if_you_remove_this_subject" bundle="${restext}"/>");'>
 <input type="button" value="<fmt:message key="cancel" bundle="${resword}"/>" title="<fmt:message key="cancel" bundle="${resword}"/>" class="button_long" size="50" onclick="history.back();"/>
</form>
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
	
							<span class="title_admin">
				
					
						
							<fmt:message key="business_admin" bundle="${resword}"/>
					
				
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
				
					
							<fmt:message key="administer_subjects" bundle="${resword}"/>
					
				
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
				
					
							<b><fmt:message key="remove_a_subject" bundle="${resword}"/></b>
					
				
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
