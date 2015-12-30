<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
 
 <tr id="sidebar_IconKey_open">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_IconKey_open'); leftnavExpand('sidebar_IconKey_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Icon Key</b><br clear="all"><br>

		<table border="0" cellpadding="4" cellspacing="0">
			<tr>
				<td><img src="images/icon_DEcomplete.gif"></td>
				<td>Completed</td>
			</tr>
			<tr>
				<td><img src="images/icon_InitialDE.gif"></td>
				<td>Started</td>
			</tr>
			<tr>
				<td><img src="images/icon_NotStarted.gif"></td>
				<td>Not Started</td>
			</tr>
			<tr>
				<td><img src="images/icon_Scheduled.gif"></td>
				<td>Scheduled</td>
			</tr>
			<tr>
				<td><img src="images/icon_Stopped.gif"></td>
				<td>Stopped</td>
			</tr>
			<tr>
				<td><img src="images/icon_Skipped.gif"></td>
				<td>Skipped</td>
			</tr>
			<tr>
				<td><img src="images/icon_Locked.gif"></td>
				<td>Locked</td>
			</tr>
			<tr>
				<td><img src="images/bt_View.gif"></td>
				<td>View</td>
			</tr>
			<tr>
				<td><img src="images/bt_Edit.gif"></td>
				<td>Edit</td>
			</tr>			
			<c:if test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
			 <tr>
				<td><img src="images/bt_Restore.gif"></td>
				<td>Restore</td>
			</tr>
			
			<tr>
				<td><img src="images/bt_Remove.gif"></td>
				<td>Remove</td>
			</tr>
			 <tr>
				<td><img src="images/bt_Reassign.gif"></td>
				<td>Reassign</td>
			</tr>
			
			</c:if>
		</table>

		<div class="sidebar_tab_content">

			<a href="#">View All Icons</a>

		</div>

		</td>
	</tr>
	
	<tr id="sidebar_IconKey_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_IconKey_open'); leftnavExpand('sidebar_IconKey_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Icon Key</b>

		</td>
	</tr>