<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope='request' id='userBean1' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='challengeQuestions' class='java.util.ArrayList'/>
<jsp:include page="../login-include/login-header.jsp"/>

<jsp:include page="../login-include/request-sidebar.jsp"/>
<!-- Main Content Area -->
<h1>Request Password Form</h1>
<jsp:include page="../login-include/login-alertbox.jsp"/>
<p>You must be an OpenClinica member to receive a password. Please fill out all the fields in the following form.  OpenClinica will use the e-mail address you registered to send you the password. If you still have questions, please contact OpenClinica Administrator for help.</p>
<form action="RequestPassword" method="post">
All fields are required.<br>
<input type="hidden" name="action" value="confirm">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">  
  <tr><td class="formlabel">User Name:</td>
  <td><div class="formfieldXL_BG"><input type="text" name="name" value="<c:out value="${userBean1.name}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td></tr>
  <tr valign="top"><td class="formlabel">Email:</td>
  <td><div class="formfieldXL_BG"><input type="text" name="email" value="<c:out value="${userBean1.email}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="email"/></jsp:include></td></tr>
  <tr valign="top"><td class="formlabel">Password Challenge Question:</td><td><div class="formfieldXL_BG">
  <select name="passwdChallengeQuestion" class="formfieldXL">  
   <c:forEach var="question" items="${challengeQuestions}"> 
     <option value="<c:out value="${question.name}"/>"><c:out value="${question.name}"/></option>    
   </c:forEach> 
   </select></div></td></tr>
  <tr valign="top"><td class="formlabel">Password Challenge Answer:</td>
  <td><div class="formfieldXL_BG"><input type="text" name="passwdChallengeAnswer" value="<c:out value="${userBean1.passwdChallengeAnswer}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="passwdChallengeAnswer"/></jsp:include></td></tr> 
  
</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
<table border="0" cellpadding="0">
 <tr><td>
 <input type="submit" name="Submit" value="Submit Password Request" class="button_xlong">
 </td>
 <td><input type="button" name="cancel" value="Cancel" class="button_medium" onclick="javascript:window.location.href='MainMenu'"></td>
 </tr> 
 </table>

</form>
<jsp:include page="../login-include/login-footer.jsp"/>
