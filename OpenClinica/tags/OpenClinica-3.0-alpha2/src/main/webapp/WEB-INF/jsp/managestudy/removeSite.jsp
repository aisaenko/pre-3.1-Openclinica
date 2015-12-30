<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

 
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:choose>      
 <c:when test="${fromListSite=='yes'}">
  <jsp:include page="../include/managestudy-header.jsp"/>
 </c:when>
 <c:otherwise>
   <jsp:include page="../include/admin-header.jsp"/>
 </c:otherwise>
</c:choose>


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

<jsp:useBean scope='request' id='siteToRemove' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='request' id='userRolesToRemove' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='subjectsToRemove' class='java.util.ArrayList'/>

<c:choose>      
 <c:when test="${fromListSite=='yes'}">
  <h1><span class="title_manage">
 </c:when>
 <c:otherwise>
   <h1><span class="title_Admin">
 </c:otherwise>
</c:choose>
<fmt:message key="confirm_removal_of_site"  bundle="${resword}"/></span></h1>

<p><fmt:message key="you_choose_to_remove_the_following_site"  bundle="${resword}"/>:</p>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToRemove.name}"/>
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="brief_summary" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToRemove.summary}"/>
  </td></tr>  
 </table> 
 
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 <c:choose>      
 <c:when test="${fromListSite=='yes'}">
  <span class="table_title_manage">
 </c:when>
 <c:otherwise>
   <span class="table_title_Admin">
 </c:otherwise>
</c:choose><fmt:message key="users_and_roles" bundle="${resword}"/></span> 
<div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
  <tr valign="top">
   <td class="table_header_column_top"><fmt:message key="name" bundle="${resword}"/></td>   
   <td class="table_header_column_top"><fmt:message key="role" bundle="${resword}"/></td>
  </tr>  
  <c:forEach var="userRole" items="${userRolesToRemove}">
  <tr valign="top">
   <td class="table_cell">
    <c:out value="${userRole.userName}"/>
   </td>
   <td class="table_cell">
    <c:out value="${userRole.role.name}"/>
   </td>
  </tr>  
  </c:forEach>   
  </table>  
</div>
</div></div></div></div></div></div></div></div>

</div>
<br> 
 <c:choose>      
 <c:when test="${fromListSite=='yes'}">
  <span class="table_title_manage">
 </c:when>
 <c:otherwise>
   <span class="table_title_Admin">
 </c:otherwise>
</c:choose><fmt:message key="subjects" bundle="${resword}"/></span> 
<div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
 <tr valign="top">
   <td class="table_header_column_top"><fmt:message key="subject_unique_identifier" bundle="${resword}"/></td>   
   <td class="table_header_column_top"><fmt:message key="Id" bundle="${resword}"/></td>
  </tr>   
  <c:forEach var="subject" items="${subjectsToRemove}">
  <tr valign="top">
   <td class="table_cell">
    <c:out value="${subject.label}"/>
   </td>
   <td class="table_cell">
    <c:out value="${subject.id}"/>
   </td>
  </tr>  
  </c:forEach> 
  </table>  
</div>
</div></div></div></div></div></div></div></div>

</div>
 

<br> 
<form action='RemoveSite?action=submit&id=<c:out value="${siteToRemove.id}"/>' method="POST">
 <input type="submit" name="submit" value="<fmt:message key="remove_site" bundle="${resword}"/>" class="button_long" onClick='return confirm("<fmt:message key="if_you_remove_this_site" bundle="${resword}"/>");'>
    &nbsp;
 <input type="button" onclick="confirmCancel('ListSite');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
    
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
	
							<c:choose>      
                            <c:when test="${fromListSite=='yes'}">
                             <span class="title_manage">	
                             <fmt:message key="manage_study" bundle="${resword}"/>
                             </span>
                            </c:when>
                             <c:otherwise>
                              <span class="title_admin">
                              <fmt:message key="business_admin" bundle="${resword}"/>	
                              </span>
                             </c:otherwise>
                           </c:choose>					
				
							

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<c:choose>      
                            <c:when test="${fromListSite=='yes'}">
                             <span class="title_manage">	
                             <fmt:message key="manage_sites" bundle="${resword}"/>
                             </span>
                            </c:when>
                             <c:otherwise>
                              <span class="title_admin">
                              <fmt:message key="administer_study" bundle="${resword}"/>	
                              </span>
                             </c:otherwise>
                           </c:choose>		

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<c:choose>      
                            <c:when test="${fromListSite=='yes'}">
                             <span class="title_manage">	
                            
                            </c:when>
                             <c:otherwise>
                              <span class="title_admin">
                             
                             </c:otherwise>
                           </c:choose>	
                           <b><fmt:message key="remove_a_site" bundle="${resword}"/></b>
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
