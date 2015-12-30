
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="session" id="newUserBean" class="org.akaza.openclinica.bean.login.UserAccountBean"/>
<jsp:useBean scope="session" id="roles" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="studies" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="otherStudy" class="java.lang.String"/>

<jsp:include page="../login-include/login-header.jsp"/>

<jsp:include page="../login-include/request-sidebar.jsp"/>
<!-- Main Content Area -->

<h1>Request an OpenClinica User Account</h1>
<P>Please provide the information below to request a user account to access OpenClinica. The system will send your request to OpenClinica Administrator. Username and password are issued via email 
 upon approval by OpenClinica Administrator.<p>
<jsp:include page="../login-include/login-alertbox.jsp"/>


<form action="RequestAccount" method="post"> 
All fields are required.<br>
<input type="hidden" name="action" value="confirm"> 
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">

<table border="0" cellpadding="0">
  <tr><td class="formlabel">User Name:</td><td>

<div class="formfieldXL_BG"><input type="text" name="name" value="<c:out value="${newUserBean.name}"/>" class="formfieldXL"></div>
<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td></tr>

  <tr><td class="formlabel">First Name:</td><td>

<div class="formfieldXL_BG"><input type="text" name="firstName" value="<c:out value="${newUserBean.firstName}"/>" class="formfieldXL"></div>

<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="firstName"/></jsp:include></td></tr>

  <tr><td class="formlabel">Last Name:</td><td>
<div class="formfieldXL_BG"><input type="text" name="lastName" value="<c:out value="${newUserBean.lastName}"/>" class="formfieldXL"></div>

<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="lastName"/></jsp:include></td></tr>
  <tr><td class="formlabel">Email:</td><td>

<div class="formfieldXL_BG"><input type="text" name="email" value="<c:out value="${newUserBean.email}"/>" class="formfieldXL"></div>
<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="email"/></jsp:include></td></tr>
  <tr><td class="formlabel">Confirm Email:</td><td>
 <div class="formfieldXL_BG"><input type="text" name="email2" value="" class="formfieldXL"></div>
<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="email2"/></jsp:include></td></tr>

 <tr><td class="formlabel">Institutional Affiliation:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="institutionalAffiliation" value="<c:out value="${newUserBean.institutionalAffiliation}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="institutionalAffiliation"/></jsp:include></td></tr>
 <tr><td class="formlabel">Default Active Study:</td><td>
  <c:set var="activeStudy1" value="${newUserBean.activeStudyId}"/>   
   <div class="formfieldXL_BG">
   <select name="activeStudyId" class="formfieldXL">
      <c:forEach var="study" items="${studies}">    
       <c:choose>
        <c:when test="${activeStudy1 == study.id}">   
         <option value="<c:out value="${study.id}"/>" selected><c:out value="${study.name}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${study.id}"/>"><c:out value="${study.name}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>
   </select></div>
</td></tr>
<tr><td class="formlabel">
If the study you are interested <br>
in are not in the above list,<br>
please indicate it here: (Optional)</td>
 <td><div class="formtextareaXL4_BG">
<textarea name="otherStudy" rows="4" cols="50" class="formtextareaXL4"><c:out value="${otherStudy}"/>&nbsp;</textarea>
</div></td></tr>
  
  <tr><td class="formlabel">Role of Access Requested: (Pending Approval)</td><td class="text">
   <c:set var="role1" value="${newUserBean.activeStudyRole}"/>  
   <div class="formfieldXL_BG"> 
   <select name="activeStudyRole" class="formfieldXL">
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
  </td></tr> 
 

</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
<table border="0" cellpadding="0">
 <tr><td>
 <input type="submit" name="submit" value="Confirm Account Request" class="button_xlong">
 </td>
 <td><input type="button" name="cancel" value="Cancel" class="button_medium" onclick="javascript:window.location.href='MainMenu'"></td>
 </tr> 
 </table>
 
</form>

<jsp:include page="../login-include/login-footer.jsp"/>
