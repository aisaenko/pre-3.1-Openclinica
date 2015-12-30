<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<jsp:include page="../include/managestudy-header.jsp"/>


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

<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>

<h1><span class="title_manage"><fmt:message key="view_a_subject_group_class" bundle="${resword}"/></span></h1>


<!-- These DIVs define shaded box borders -->
<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">
   
 <tr valign="top"><td class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${group.name}"/>
 </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="type" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${group.groupClassTypeName}"/>&nbsp;
 </td></tr>  
   
   <tr valign="top"><td class="table_header_column"><fmt:message key="subject_assignment" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${group.subjectAssignment}"/>
   </td></tr>  
  </table>

<br>
<div class="table_title_manage"><fmt:message key="study_group_and_associated_subjects" bundle="${resword}"/>:</div>
<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
  <tr valign="top">
   <th><fmt:message key="name" bundle="${resword}"/></th>
   <th><fmt:message key="description" bundle="${resword}"/></th>
    <th><fmt:message key="subjects" bundle="${resword}"/></th> 
  </tr>    
   <c:forEach var="studyGroup" items="${studyGroups}">   
    <tr valign="top">
     <td class="table_cell">  
      <c:out value="${studyGroup.name}"/>  
     </td>
     <td class="table_cell">  
      <c:out value="${studyGroup.description}"/>&nbsp;  
     </td>
     <td class="table_cell">  
      <c:forEach var="subjectMap" items="${studyGroup.subjectMaps}">       
       <c:out value="${subjectMap.subjectLabel}"/><br>      
     </c:forEach>&nbsp;
     </td>
    </tr>     
     
   </c:forEach>  
  
   
   
  
 
</table>
<p><a href="#" onClick="history.go(-1)"><fmt:message key="back_to_group_class_list" bundle="${resword}"/></a></p>

 <br><br>
 
<jsp:include page="../include/footer.jsp"/>
