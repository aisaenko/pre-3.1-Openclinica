<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/home-header.jsp"/>
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

<jsp:useBean scope="request" id="studies" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="roles" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="newRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean"/>
<h1>Request Study Access <a href="javascript:openDocWindow('help/1_2_requestStudy_Help.html')"><img src="images/bt_Help.gif" border="0" alt="Help" title="Help"></a></h1>


<form action="RequestStudy" method="post">
* indicates required field.<br>
<input type="hidden" name="action" value="confirm">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0">
  <tr><td class="formlabel">First Name:</td>
  <td><div class="formfieldXL_BG">
   &nbsp;<c:out value="${userBean.firstName}"/></div></td>
  </tr>
  <tr><td class="formlabel">Last Name:</td><td><div class="formfieldXL_BG">&nbsp;<c:out value="${userBean.lastName}"/>
  </div></td></tr>
  <tr><td class="formlabel">Email:</td>
  <td><div class="formfieldXL_BG">&nbsp;<c:out value="${userBean.email}"/></div>
  </td></tr>
  
  <tr><td class="formlabel">Study Requested:</td><td><div class="formfieldXL_BG">  
   <c:set var="studyId1" value="${newRole.studyId}"/> 	 
    <select name="studyId" class="formfieldXL">     
       <c:forEach var="study" items="${studies}"> 	 
        <c:choose> 	 
         <c:when test="${studyId1 == study.id}"> 	 
          <option value="<c:out value="${study.id}"/>" selected><c:out value="${study.name}"/> 	 
         </c:when> 	 
         <c:otherwise> 	 
          <option value="<c:out value="${study.id}"/>"><c:out value="${study.name}"/> 	 
         </c:otherwise> 	 
        </c:choose> 	 
     </c:forEach> 	 
    </select></div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyId"/></jsp:include> 	 
  </td></tr>
  
  <tr><td class="formlabel">Role of Access Requested:</td><td>
   <c:set var="role1" value="${newRole.role}"/>  
   <div class="formfieldXL_BG"> 
   <select name="studyRoleId" class="formfieldXL">    
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
   </select></div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyRoleId"/></jsp:include>
   
  </td></tr> 
 
  
</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
<input type="submit" name="Submit" value="Confirm Study Access" class="button_xlong">
</form>
<jsp:include page="../include/footer.jsp"/>
