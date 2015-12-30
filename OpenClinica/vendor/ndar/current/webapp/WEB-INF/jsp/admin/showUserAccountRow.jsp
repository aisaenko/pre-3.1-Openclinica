<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.login.UserAccountRow" />

<tr valign="top" bgcolor="#F5F5F5">
	<td class="table_cell_left">
		<c:choose>
			<c:when test='${currRow.bean.status.name == "removed"}'>
				<font color='gray'><c:out value="${currRow.bean.name}" /></font>
			</c:when>
			<c:otherwise>
				<c:out value="${currRow.bean.name}" />
			</c:otherwise>
		</c:choose>
	</td>
	<td class="table_cell"><c:out value="${currRow.bean.firstName}" /></td>
	<td class="table_cell"><c:out value="${currRow.bean.lastName}" /></td>
	<td class="table_cell"><c:out value="${currRow.bean.status.name}" /></td>
	
	<%-- ACTIONS --%>
	<td class="table_cell">
	 <table border="0" cellpadding="0" cellspacing="0">
	 <tr>
		<c:choose>
		<c:when test='${(currRow.bean.techAdmin && !(userBean.techAdmin))}'>
			<%-- put in grayed-out buttons here? --%>
				<%--<td><img src="images/bt_Remove_i.gif" alt="Access denied" title="Access denied"></img></td>
				<td><img src="images/bt_Remove_i.gif" alt="Access denied" title="Access denied"></img></td>
				<td><img src="images/bt_Remove_i.gif" alt="Access denied" title="Access denied"></img></td> --%>
			</c:when>
			<c:otherwise>
				<c:choose>
				<c:when test='${currRow.bean.status.name == "removed"}'>
					<c:set var="confirmQuestion" value="Are you sure you want to restore ${currRow.bean.name}?" />
					<c:set var="onClick" value="return confirm('${confirmQuestion}');"/>
					<td><a href="DeleteUser?action=4&userId=<c:out value="${currRow.bean.id}"/>" onClick="<c:out value="${onClick}" />"
					onMouseDown="setImage('bt_Restor3','images/bt_Restore_d.gif');"
				    onMouseUp="setImage('bt_Restore3','images/bt_Restore.gif');"
				    ><img name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6"></a>
				   	</td>
			</c:when>
			<c:otherwise>
				<td><a href="ViewUserAccount?userId=<c:out value="${currRow.bean.id}"/>"
					onMouseDown="setImage('bt_View1','images/bt_View_d.gif');"
					onMouseUp="setImage('bt_View1','images/bt_View.gif');"
					><img name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a></td>
				<td><a href="EditUserAccount?userId=<c:out value="${currRow.bean.id}"/>"
					onMouseDown="setImage('bt_Edit1','images/bt_Edit_d.gif');"
					onMouseUp="setImage('bt_Edit1','images/bt_Edit.gif');"
					><img name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6"></a></td>
			    <td><a href="SetUserRole?action=confirm&userId=<c:out value="${currRow.bean.id}"/>"
		          onMouseDown="setImage('bt_SetRole1','images/bt_SetRole_d.gif');"
		          onMouseUp="setImage('bt_SetRole1','images/bt_SetRole.gif');"><img 
		          name="bt_SetRole1" src="images/bt_SetRole.gif" border="0" alt="Set Role" title="Set Role" align="left" hspace="6"></a>
		        </td>		
		
				<c:set var="confirmQuestion" value="Are you sure you want to delete ${currRow.bean.name}?" />
				<c:set var="onClick" value="return confirm('${confirmQuestion}');"/>
				<td><a href="DeleteUser?action=3&userId=<c:out value="${currRow.bean.id}"/>" onClick="<c:out value="${onClick}" />"
					onMouseDown="setImage('bt_Remove1','images/bt_Remove_d.gif');"
					onMouseUp="setImage('bt_Remove1','images/bt_Remove.gif');"
					><img name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
				</td>
			</c:otherwise>
			</c:choose>			
			
			</c:otherwise>
		</c:choose>	
		</tr>
		</table>
	</td>
</tr>
<%--<c:choose>
		<c:when test='${(currRow.bean.techAdmin && !(userBean.techAdmin))}'>
		</c:when>
		<c:otherwise>--%>
<%-- start test for tech admin above here --%>
<c:choose>
	<c:when test="${empty currRow.bean.roles}">
		<tr valign="top">
			<td class="table_cell_left">&nbsp;</td>
			<td class="table_cell" colspan="3"><i>No roles assigned.</i></td>
			<td class="table_cell">&nbsp;</td>
		</tr>
	</c:when>
	<c:otherwise>
		<c:forEach var="sur" items="${currRow.bean.roles}">
			<c:choose>
				<c:when test='${sur.studyName != ""}'>
					<c:set var="study" value="${sur.studyName}" />
				</c:when>
				<c:otherwise>
					<c:set var="study" value="Study ${sur.studyId}" />				
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test='${sur.status.name == "removed"}'>
					<c:set var="actionName" value="restore" />
					<c:set var="actionId" value="4" />
				</c:when>
				<c:otherwise>
					<c:set var="actionName" value="delete" />
					<c:set var="actionId" value="3" />
				</c:otherwise>
			</c:choose>
			<c:set var="confirmQuestion" value="Are you sure you want to ${actionName} the ${sur.role.description} role for ${study}?" />
			<c:set var="onClick" value="return confirm('${confirmQuestion}');"/>
			<tr valign="top">
				<td class="table_cell_left">&nbsp;</td>
				<td class="table_cell" colspan="3" >
					<c:if test='${sur.status.name == "removed"}'>
						<font color='gray'>
					</c:if>
					<c:choose>
						<c:when test='${sur.studyName != ""}'><c:out value="${sur.studyName}" /></c:when>
						<c:otherwise>Study <c:out value="${sur.studyId}" /></c:otherwise>
					</c:choose>
					- <c:out value="${sur.role.description}" />
					<c:if test='${sur.status.name == "removed"}'>
						</font>
					</c:if>
				</td>
				<td class="table_cell">
					<c:if test='${sur.status.name != "removed"}'>
						<a href="EditStudyUserRole?studyId=<c:out value="${sur.studyId}" />&userName=<c:out value="${currRow.bean.name}"/>"
							onMouseDown="setImage('bt_Edit1','images/bt_Edit_d.gif');"
							onMouseUp="setImage('bt_Edit1','images/bt_Edit.gif');"
							><img name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6"></a>
					</c:if>
					<c:choose>
					<c:when test='${sur.status.name == "removed"}'>
						<a href="DeleteStudyUserRole?studyId=<c:out value="${sur.studyId}" />&userName=<c:out value="${currRow.bean.name}"/>&action=4" onClick="<c:out value="${onClick}" />"
							onMouseDown="setImage('bt_Restor3','images/bt_Restore_d.gif');"
						    onMouseUp="setImage('bt_Restore3','images/bt_Restore.gif');"
						   	><img name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6"></a>
					</c:when>
					<c:otherwise>
						<a href="DeleteStudyUserRole?studyId=<c:out value="${sur.studyId}" />&userName=<c:out value="${currRow.bean.name}"/>&action=3" onClick="<c:out value="${onClick}" />"
							onMouseDown="setImage('bt_Remove3','images/bt_Remove_d.gif');"
						    onMouseUp="setImage('bt_Remove3','images/bt_Remove.gif');"
						   	><img name="bt_Remove3" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
					</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
	</c:otherwise>
	<%-- end test of tech admin below here --%>
</c:choose>
<%--</c:otherwise>
</c:choose> --%>