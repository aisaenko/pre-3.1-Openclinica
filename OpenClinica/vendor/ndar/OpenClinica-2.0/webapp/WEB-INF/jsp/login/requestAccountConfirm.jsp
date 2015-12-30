<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<jsp:include page="../login-include/login-header.jsp"/>
<jsp:useBean scope='session' id='newUserBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='otherStudy' class='java.lang.String'/>
<jsp:useBean scope='request' id='studyName' class='java.lang.String'/>


<jsp:include page="../login-include/request-sidebar.jsp"/>
<!-- Main Content Area -->
<h1>
Confirm your User Account Information
</h1>
<P>Please check the information below. If no errors, please press the submit button to <br>
send your information to the administrator.
<p>
<jsp:include page="../login-include/login-alertbox.jsp"/>
<form action="RequestAccount" method="post">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<input type="hidden" name="action" value="submit"> 
<input type="hidden" name="otherStudy" value="<c:out value="${otherStudy}"/>">  
<input type="hidden" name="studyName" value="<c:out value="${studyName}"/>"> 
<table border="0" cellpadding="0" cellspacing="0">
  <tr><td class="table_header_column">User Name:</td><td class="table_cell_top">
<c:out value="${newUserBean.name}"/></td></tr>
  <tr><td class="table_header_column">First Name:</td><td class="table_cell_top">
<c:out value="${newUserBean.firstName}"/></td></tr>
  <tr><td class="table_header_column">Last Name:</td><td class="table_cell_top">
<c:out value="${newUserBean.lastName}"/>
</td></tr>
  <tr><td class="table_header_column">Email:</td><td class="table_cell_top">
<c:out value="${newUserBean.email}"/></td></tr> 
 <tr><td class="table_header_column">Institutional Affiliation:</td><td class="table_cell_top">
  <c:out value="${newUserBean.institutionalAffiliation}"/><br></td></tr>
 <tr><td class="table_header_column">Default Active Study:</td>
 <td class="table_cell_top"><c:out value="${studyName}"/></td></tr>
<tr><td class="table_header_column">
Other studies if any:</td>
 <td class="table_cell_top"><c:out value="${otherStudy}"/></td></tr>
  
  <tr><td class="table_header_column">Role of Access Requested:</td>
  <td class="table_cell_top"><c:out value="${newUserBean.activeStudyRoleName}"/></td></tr> 
 
 
</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
 <input type="submit" name="submit" value="Submit Account Request" class="button_long">
</form>
<jsp:include page="../login-include/login-footer.jsp"/>
