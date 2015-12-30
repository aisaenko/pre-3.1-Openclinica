<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">

		List of all users with their roles and status in the current study/site. Select any user for details on his/her user profile or assign the user a different role in the study.

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<h1><span class="title_manage">Manage All Users in <c:out value="${study.name}"/> <a href="javascript:openDocWindow('help/4_1_users_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="Help" title="Help"></a></span></h1>

<div class="homebox_bullets"><a href="AssignUserToStudy">Assign New User to Current Study</a></div>
<p>
<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showUserInStudyRow.jsp" /></c:import>

<c:import url="../include/workflow.jsp">
  <c:param name="module" value="manage"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>
