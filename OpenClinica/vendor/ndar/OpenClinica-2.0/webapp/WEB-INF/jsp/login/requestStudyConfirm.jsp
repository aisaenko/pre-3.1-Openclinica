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
<jsp:useBean scope="session" id="newRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean"/>
<h1>Confirm Your Study Access Request</h1>

<form action="RequestStudy?action=submit" method="post">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0">
 
  <tr valign="top"><td class="table_header_column">First Name:</td><td class="table_cell"><c:out value="${userBean.firstName}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Last Name:</td><td class="table_cell"><c:out value="${userBean.lastName}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Email:</td><td class="table_cell"><c:out value="${userBean.email}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Study Requested:</td><td class="table_cell"><c:out value="${newRole.studyName}"/></td></tr>
  <tr><td class="table_header_column">Role of Access Requested:</td>
  <td class="table_cell"><c:out value="${newRole.role.description}"/></td></tr> 
   
  </td></tr>
</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
 <input type="submit" name="Submit" value="Submit Request" class="button_long">
</form>
<jsp:include page="../include/footer.jsp"/>
