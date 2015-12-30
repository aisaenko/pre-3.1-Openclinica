<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/admin-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		  
        
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="request" id="user" class="org.akaza.openclinica.bean.login.UserAccountBean"/>
<jsp:useBean scope="request" id="uRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean"/>
<jsp:useBean scope="request" id="roles" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="studies" class="java.util.ArrayList"/>
<h1><span class="title_Admin">Set User Role</span></h1>

<p>Choose a study from the following study list and set a role for the user to access the study.</p>
<form action="SetUserRole" method="post">
<input type="hidden" name="action" value="submit">
<input type="hidden" name="userId" value="<c:out value="${user.id}"/>">
<input type="hidden" name="name" value="<c:out value="${user.name}"/>">
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr><td class="formlabel">First Name:</td><td><c:out value="${user.firstName}"/></td></tr>
  <tr><td class="formlabel">Last Name:</td><td><c:out value="${user.lastName}"/></td></tr>
  <tr><td class="formlabel">Study Name:</td>  
    <td><div class="formfieldXL_BG">
        <select name="studyId" class="formfieldXL">
         <c:forEach var="userStudy" items="${studies}">     
         
           <option value="<c:out value="${userStudy.id}"/>"><c:out value="${userStudy.name}"/>
                
         </c:forEach>
       </select>
       </div>
      </td>
   </tr>
  <tr><td class="formlabel">Study User Role:</td>
  <td><div class="formfieldXL_BG">
       <c:set var="role1" value="${uRole.role}"/>   
       <select name="roleId" class="formfieldXL">
         <c:forEach var="userRole" items="${roles}">    
          <c:choose>
           <c:when test="${role1.id == userRole.id}">   
           <option value="<c:out value="${userRole.id}"/>" selected><c:out value="${userRole.description}"/>
           </c:when>
           <c:otherwise>
             <option value="<c:out value="${userRole.id}"/>"><c:out value="${userRole.description}"/>      
           </c:otherwise>
          </c:choose> 
         </c:forEach>
       </select>
       </div>
      </td>
  </tr>   
 
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<input type="submit" name="Submit" value="Submit" class="button_medium">
</form>

<c:import url="../include/workflow.jsp">
 <c:param name="module" value="admin"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>
