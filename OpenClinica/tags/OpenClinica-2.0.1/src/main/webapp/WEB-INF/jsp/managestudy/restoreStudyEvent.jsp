<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/managestudy-header.jsp"/>
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
        Confirm restore of this event to Study <c:out value="${study.name}"/>. The event and all data associated with it in this Study/Site will be restored.
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

<jsp:useBean scope="request" id="displayEvent" class="org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope="request" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>

<h1><span class="title_manage">
Restore Event from Study
</span></h1>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column">Event Definition Name:</td><td class="table_cell"><c:out value="${displayEvent.studyEvent.studyEventDefinition.name}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Location:</td><td class="table_cell"><c:out value="${displayEvent.studyEvent.location}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Visit#:</td><td class="table_cell"><c:out value="${displayEvent.studyEvent.sampleOrdinal}"/></td></tr>
    
  <tr valign="top"><td class="table_header_column">Date Started:</td><td class="table_cell"><fmt:formatDate value="${displayEvent.studyEvent.dateStarted}" pattern="MM/dd/yyyy"/></td></tr>
  <tr valign="top"><td class="table_header_column">Date Ended:</td><td class="table_cell"><fmt:formatDate value="${displayEvent.studyEvent.dateEnded}" pattern="MM/dd/yyyy"/></td></tr>
  <tr valign="top"><td class="table_header_column">Status:</td><td class="table_cell"><c:out value="${displayEvent.studyEvent.status.name}"/>
  </td></tr>

 </table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 <c:choose>
 <c:when test="${!empty displayEvent.displayEventCRFs}"> 
 <span class="table_title_manage">Event CRFs</span>
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
		<td class="table_header_column_top">CRF Name</td>
		<td class="table_header_column_top">Version</td>
		<td class="table_header_column_top">Date Interviewed</td>
		<td class="table_header_column_top">Interviewer Name</td>
		<td class="table_header_column_top">Owner</td>
		<td class="table_header_column_top">Completion Status</td>	
		<td class="table_header_column_top">Status</td>	
	 </tr>
 <c:forEach var="dec" items="${displayEvent.displayEventCRFs}">
	<tr>
		<td class="table_cell"><c:out value="${dec.eventCRF.crf.name}" /></td>
		<td class="table_cell"><c:out value="${dec.eventCRF.crfVersion.name}" /></td>
		<td class="table_cell"><fmt:formatDate value="${dec.eventCRF.dateInterviewed}" pattern="MM/dd/yyyy"/></td>
		<td class="table_cell"><c:out value="${dec.eventCRF.interviewerName}"/></td>
		<td class="table_cell"><c:out value="${dec.eventCRF.owner.name}" /></td>
		<td class="table_cell"><c:out value="${dec.stage.name}" /></td>	
		<td class="table_cell"><c:out value="${dec.eventCRF.status.name}" /></td>	
	 </tr>
 </c:forEach> 
 
 </table> 
</div>
</div></div></div></div></div></div></div></div>

</div>
 </c:when>
 <c:otherwise>
  <p>No Event CRFs.</p>
 </c:otherwise>
 </c:choose>
   <c:choose>
    <c:when test="${!empty displayEvent.displayEventCRFs}">
     <form action='RestoreStudyEvent?action=submit&id=<c:out value="${displayEvent.studyEvent.id}"/>&studySubId=<c:out value="${studySub.id}"/>' method="POST">
      <input type="submit" name="submit" value="Restore Event to Study" class="button_xlong" onClick='return confirm("This event has CRF data shown on page. Are you sure you want to restore this event?");'>
     </form>
    
    </c:when>
    <c:otherwise>
      <form action='RestoreStudyEvent?action=submit&id=<c:out value="${displayEvent.studyEvent.id}"/>&studySubId=<c:out value="${studySub.id}"/>' method="POST">
      <input type="submit" name="submit" value="Restore Event to Study" class="button_xlong" onClick='return confirm("Are you sure you want to restore it?");'>
     </form>
      
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
				
					
							Manage Subject
					
				
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
				
					
							View Study Subject
					
				
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
					
							<b>Restore Event to Study</b>					
				
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
