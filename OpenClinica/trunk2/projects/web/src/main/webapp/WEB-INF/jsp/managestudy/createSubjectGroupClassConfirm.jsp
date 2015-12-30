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
<script type="text/JavaScript" language="JavaScript">
  <!--
 function myCancel() {
 
    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {
        window.location.href="ListSubjectGroupClass";
       return true;
      } else {
        return false;
       }
     }
     return true;    
   
  }
   //-->
</script>
<h1><span class="title_manage"><fmt:message key="confirm_a_subject_group_class" bundle="${resword}"/></span></h1>

<form action="CreateSubjectGroupClass" method="post">
<!-- * <fmt:message key="indicates_required_field" bundle="${resword}"/><br>-->
<input type="hidden" name="action" value="submit">
<!-- These DIVs define shaded box borders -->
<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">   
   
 <tr valign="top"><td width="30%" class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${group.name}"/>
 </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="type" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${group.groupClassTypeName}"/>
 </td></tr>  
   
   <tr valign="top"><td class="table_header_column"><fmt:message key="subject_assignment" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${group.subjectAssignment}"/>
   </td></tr>  
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="study_groups" bundle="${resword}"/>:</td>
   <td class="table_cell">  
   <c:forEach var="studyGroup" items="${studyGroups}">
    <c:out value="${studyGroup.name}"/>&nbsp;&nbsp;<c:out value="${studyGroup.description}"/><br>   
   </c:forEach>  
  
  </td></tr>  
   
  
 
</table>
<br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
<input type="submit" name="Submit" value="<fmt:message key="submit_subject_group_class" bundle="${resword}"/>" class="button_long">
</td>
<td><input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" onClick="javascript:myCancel();"/></td>
</tr>
</table>
</form>

<br><br>
<jsp:include page="../include/footer.jsp"/>
