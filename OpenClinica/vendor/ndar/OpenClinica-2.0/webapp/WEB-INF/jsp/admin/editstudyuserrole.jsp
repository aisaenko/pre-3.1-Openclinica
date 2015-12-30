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


<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='studyUserRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean'/>
<jsp:useBean scope='request' id='userName' class='java.lang.String'/>
<jsp:useBean scope='request' id='chosenRoleId' type='java.lang.Integer' />
<jsp:useBean scope='request' id='roles' class='java.util.ArrayList' />

<h1><span class="title_Admin">Modify Role for <b><c:out value="${userName}" /></b> within <b><c:out value="${studyUserRole.studyName}" /></b></span></h1>

<form action="EditStudyUserRole" method="post">
<jsp:include page="../include/showSubmitted.jsp" />
<input type="hidden" name="studyId" value="<c:out value="${studyUserRole.studyId}" />" />
<input type="hidden" name="userName" value="<c:out value="${userName}" />" />

<div style="width: 400px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="table_header_column_top">Username:</td>
		<td class="table_cell_top"><b><c:out value="${userName}" /></b></td>
	</tr>
	<tr>
		<td class="table_header_column">Study:</td>
		<td class="table_cell"><b><c:out value="${studyUserRole.studyName}" /></b></td>
	</tr>
	<tr>
		<td class="table_header_column">Role:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<select name="role" class="formfieldM">
							<option value="0">-Select-</option>
							<c:forEach var="role" items="${roles}">
								<c:choose>
									<c:when test="${chosenRoleId == role.id}" >
										<option value="<c:out value="${role.id}"/>" selected><c:out value="${role.description}" /></option>
									</c:when>
									<c:otherwise>
										<option value="<c:out value="${role.id}"/>"><c:out value="${role.description}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</div></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="role"/></jsp:include></td>
				</tr>
			</table>
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