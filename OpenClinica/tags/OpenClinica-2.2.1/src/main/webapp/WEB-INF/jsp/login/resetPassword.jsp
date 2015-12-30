<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../login-include/login-header.jsp"/>
<jsp:include page="../login-include/resetpwd-sidebar.jsp"/>


<jsp:useBean scope="request" id="mustChangePass" class="java.lang.String"/>

<h1>Reset Password</h1>
<jsp:include page="../login-include/login-alertbox.jsp"/>

<form action="ResetPassword" method="post">
* indicates required field.<br>
<input type="hidden" name="mustChangePwd" value=<c:out value="${mustChangePass}"/> >
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0">
  <tr><td class="formlabel">Old Password:</td><td><div class="formfieldXL_BG"><input type="password" name="oldPasswd" value="" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="oldPasswd"/></jsp:include></td><td class="formlabel">*</td></tr>
  <tr><td class="formlabel">New Password:</td><td><div class="formfieldXL_BG"><input type="password" name="passwd" value="" class="formfieldXL"></div>
  <c:choose>
  	<c:when test="${mustChangePass=='yes'}">
  		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="passwd"/></jsp:include></td><td class="formlabel">*</td></tr>
  	</c:when>
  	<c:otherwise>
  		(leave New Password blank if you don't want to change password)
  		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="passwd"/></jsp:include></td></tr>
  	</c:otherwise>
  </c:choose>
  <tr><td class="formlabel">Confirm New Password:</td><td><div class="formfieldXL_BG"><input type="password" name="passwd1" value="" class="formfieldXL"></div>
  <c:choose>
  	<c:when test="${mustChangePass=='yes'}">
  		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="passwd1"/></jsp:include></td><td class="formlabel">*</td></tr>
    </c:when>
  	<c:otherwise>
  		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="passwd1"/></jsp:include></td></tr>
	</c:otherwise>
  </c:choose>
</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
<table border="0" cellpadding="0">
 <tr><td><input type="submit" name="submit" value="Change Password" class="button_xlong"></td>
 <td><input type="button" name="exit" value="Exit" class="button_medium" onclick="javascript:window.location.href='Logout'"></td>
 </tr> 
 </table>
</form>
<jsp:include page="../login-include/login-footer.jsp"/>
