<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<c:import url="../include/admin-header.jsp"/>
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


<jsp:useBean scope='request' id='studySubs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='events' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='subjectToRemove' class='org.akaza.openclinica.bean.submit.SubjectBean'/>

<h1><span class="title_Admin">Confirm Removal of Subject</span></h1>

<div class="table_title_Admin">You choose to remove the following Subject:</div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">

<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top" ><td class="table_header_column">Person ID:</td><td class="table_cell">  
  <c:out value="${subjectToRemove.uniqueIdentifier}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column">gender:</td><td class="table_cell">  
  <c:out value="${subjectToRemove.gender}"/>
  </td>
  <tr valign="top"><td class="table_header_column">Date Of Birth:</td><td class="table_cell">  
  <fmt:formatDate value="${subjectToRemove.dateOfBirth}" pattern="MM/dd/yyyy"/>
  </td>
  <tr valign="top"><td class="table_header_column">Date Created:</td><td class="table_cell">  
  <fmt:formatDate value="${subjectToRemove.createdDate}" pattern="MM/dd/yyyy"/>
  </td>
  </tr> 
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br/><br/>
<div class="table_title_Admin"> Associated Study Subjects </div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
 <table border="0" cellpadding="0">  
 <tr valign="top">
    <td class="table_header_column_top">Study Subject ID</td>       
    <td class="table_header_column_top">Secondary Label</td> 
    <td class="table_header_column_top">Study Record Id</td>
    <td class="table_header_column_top">Enrollment Date</td> 
    <td class="table_header_column_top">Date Created</td> 
    <td class="table_header_column_top">Created By</td> 
    <td class="table_header_column_top">Status</td>       
    </tr>   
  <c:forEach var="studySub" items="${studySubs}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${studySub.label}"/></td>               
    <td class="table_cell"><c:out value="${studySub.secondaryLabel}"/>&nbsp;</td> 
    <td class="table_cell"><c:out value="${studySub.studyId}"/></td>
    <td class="table_cell"><fmt:formatDate value="${studySub.enrollmentDate}" pattern="MM/dd/yyyy"/></td>       
    <td class="table_cell"><fmt:formatDate value="${studySub.createdDate}" pattern="MM/dd/yyyy"/></td>       
    <td class="table_cell"><c:out value="${studySub.owner.name}"/></td>  
    <td class="table_cell"><c:out value="${studySub.status.name}"/></td>       
    </tr>  
 </c:forEach>    
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br><br>
<div class="table_title_Admin">Associated Study Events</div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
 <table border="0" cellpadding="0">
  <tr valign="top">
    <td class="table_header_column_top">Record ID</td>
    <td class="table_header_column_top">Location</td> 
    <td class="table_header_column_top">Date Started</td> 
    <td class="table_header_column_top">Created By</td> 
    <td class="table_header_column_top">Status</td>
    </tr>       
  <c:forEach var="event" items="${events}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${event.id}"/></td>           
    <td class="table_cell"><c:out value="${event.location}"/>&nbsp;</td>    
    <td class="table_cell"><fmt:formatDate value="${event.createdDate}" pattern="MM/dd/yyyy"/></td>       
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
 <input type="submit" name="submit" value="Remove Subject" class="button_long" onClick='return confirm("If you remove this Subject, all the assigned uses of the Subject and any data entered using it will be removed as well. Are you sure you want to remove it?");'>
 <input type="button" value="Cancel" title="Cancel" class="button_long" size="50" onclick="history.back();"/>
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
				
					
						
							Business Admin
					
				
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
				
					
							Administer Subjects
					
				
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
				
					
							<b>Remove a Subject</b>
					
				
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
