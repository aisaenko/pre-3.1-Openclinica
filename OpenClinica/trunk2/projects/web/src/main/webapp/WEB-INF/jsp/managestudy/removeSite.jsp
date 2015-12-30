<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

 
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:import url="../include/managestudy-header.jsp"/>


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

 <h1><span class="title_manage"><fmt:message key="confirm_removal_of_site"  bundle="${resword}"/></span></h1>

<div class="table_title"><fmt:message key="you_choose_to_remove_the_following_site"  bundle="${resword}"/>:</span>
<table border="0" cellpadding="0"   class="shaded_table table_first_column_w30 cell_borders fcolumn">
 
  <tr valign="top"><td class="table_header_column" width="400"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToRemove.name}"/>
  </td></tr>  
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="brief_summary" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToRemove.summary}"/>
  </td></tr>  
 </table> 
 

<br>
<div class="table_title"><fmt:message key="users_and_roles" bundle="${resword}"/></div>
<table border="0" cellpadding="0"   class="shaded_table table_first_column_w30 cell_borders hrow">
  <tr valign="top">
   <th><fmt:message key="name" bundle="${resword}"/></th>   
   <th><fmt:message key="role" bundle="${resword}"/></th>
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

<br> <div class="table_title"><fmt:message key="subjects" bundle="${resword}"/></div>
<table border="0" cellpadding="0"   class="shaded_table table_first_column_w30 cell_borders hrow">
 <tr valign="top">
   <th><fmt:message key="subject_unique_identifier" bundle="${resword}"/></th>   
   <th><fmt:message key="Id" bundle="${resword}"/></th>
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

 

<br> 
<form action='RemoveSite?action=submit&id=<c:out value="${siteToRemove.id}"/>' method="POST">
 <input type="submit" name="submit" value="<fmt:message key="remove_site" bundle="${resword}"/>" class="button_long" onClick='return confirm("<fmt:message key="if_you_remove_this_site" bundle="${resword}"/>");'>
    &nbsp;
 <input type="button" onclick="confirmCancel('ListSite');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
    
</form>

<br><br>



<jsp:include page="../include/footer.jsp"/>
