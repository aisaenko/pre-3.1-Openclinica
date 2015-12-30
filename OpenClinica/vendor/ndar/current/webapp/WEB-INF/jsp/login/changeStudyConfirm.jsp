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

<jsp:useBean scope="session" id="studyWithRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean"/>
<jsp:useBean scope="request" id="studyId" type="java.lang.Integer"/>
<h1>Confirm Changing Study</h1>


<form action="ChangeStudy" method="post">
<input type="hidden" name="action" value="submit">
<input type="hidden" name="studyId" value="<c:out value="${studyId}"/>">
<p>You are choosing to switch to the study: <c:out value="${studyWithRole.studyName}"/> with a role of <c:out value="${studyWithRole.role.description}"/>.</p>
<br>
<input type="submit" name="Submit" value="Confirm" class="button_medium">

</form>
<jsp:include page="../include/footer.jsp"/>
