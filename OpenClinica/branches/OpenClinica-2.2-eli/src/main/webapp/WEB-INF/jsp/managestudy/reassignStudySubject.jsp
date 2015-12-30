<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>    
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/> 

	
	

<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		<div class="sidebar_tab_content"> 
		
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="request" id="displayStudy" class="org.akaza.openclinica.bean.admin.DisplayStudyBean"/>
<jsp:useBean scope="session" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<h1><span class="title_manage">
<fmt:message key="reassign_study_subject" bundle="${resworkflow}"/> 
</span></h1>
<p><fmt:message key="you_choose_to_reassign_the_following_subject2" bundle="${restext}"/></p>
<form action="ReassignStudySubject" method="post">
<input type="hidden" name="action" value="confirm">
<input type="hidden" name="id" value="<c:out value="${studySub.id}"/>">
 
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr>
   <td class="table_header_column"><fmt:message key="person_ID" bundle="${resword}"/></td>
   <td class="table_cell"><c:out value="${subject.uniqueIdentifier}"/></td>
 </tr>
 <tr>
   <td class="table_header_column"><fmt:message key="gender" bundle="${resword}"/></td>
   <td class="table_cell"><c:out value="${subject.gender}"/></td></tr>
 <tr>
   <td class="table_header_column"><fmt:message key="date_created" bundle="${resword}"/></td>
   <td class="table_cell"><fmt:formatDate value="${subject.createdDate}" dateStyle="short"/></td></tr>
 </table>
 </div>
</div></div></div></div></div></div></div></div>
</div>
<br>
<p><strong><fmt:message key="please_choose_a_study_in_the_following_list2" bundle="${restext}"/></strong></P>
    
   <table border="0" cellpadding="0" cellspacing="0"> 
   <tr><td>   	 
        <c:choose> 	 
         <c:when test="${displayStudy.parent.id==studySub.studyId }">      
                      
          <input type="radio" checked name="studyId" value="<c:out value="${displayStudy.parent.id}"/>"><b><c:out value="${displayStudy.parent.name}"/> <fmt:message key="currently_in" bundle="${restext}"/></b>         
                  	 
         </c:when> 	 
         <c:otherwise>          
            
          <input type="radio" name="studyId" value="<c:out value="${displayStudy.parent.id}"/>"><b><c:out value="${displayStudy.parent.name}"/> </b>
             	 
         </c:otherwise> 	 
        </c:choose> 
        <br>        
     </td></tr> 
      <c:forEach var="child" items="${displayStudy.children}">
      <tr><td>
         <c:choose> 	 
         <c:when test="${child.id==studySub.studyId }">      
                      
           &nbsp;&nbsp;<div class="homebox_bullets"><input type="radio" checked name="studyId" value="<c:out value="${child.id}"/>"><c:out value="${child.name}"/> <fmt:message key="currently_in" bundle="${restext}"/></div>       
                  	 
         </c:when> 	 
         <c:otherwise>          
            
         &nbsp;&nbsp;<div class="homebox_bullets"><input type="radio" name="studyId" value="<c:out value="${child.id}"/>"><c:out value="${child.name}"/></div>
             	 
         </c:otherwise> 	 
        </c:choose>       
      
      </td></tr>
      </c:forEach>
    
   </table>
  <p><input type="submit" name="Submit" value="<fmt:message key="reassign_subject" bundle="${resword}"/>" class="button_long"></p>


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

					<b><fmt:message key="workflow" bundle="${restext}"/></b>

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
				
					
							  <fmt:message key="manage_subjects" bundle="${resworkflow}"/>
					
				
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
					
							  <b> <fmt:message key="reassign_study_subject" bundle="${resworkflow}"/> </b>					
				
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
