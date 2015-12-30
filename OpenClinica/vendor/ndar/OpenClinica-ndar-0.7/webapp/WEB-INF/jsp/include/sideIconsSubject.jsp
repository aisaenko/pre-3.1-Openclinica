<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
 
 <tr id="sidebar_IconKey_open">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_IconKey_open'); leftnavExpand('sidebar_IconKey_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title="" /></a>

		<b>Icon Key</b><br clear="all"><br>

		<table border="0" cellpadding="4" cellspacing="0">
			<tr>
				<th colspan="2">Event Status</th>
			</tr>
			<tr>
				<td><img src="images/icon_NotStarted.gif" alt="Proposed" title="" /></td>
				<td>Proposed</td>
			</tr>
			<tr>
				<td><img src="images/icon_Scheduled.gif" alt="Scheduled" title="" /></td>
				<td>Scheduled</td>
			</tr>
			<tr>
				<td><img src="images/icon_InitialDE.gif" alt="In Progress" title="" /></td>
				<td>In Progress</td>
			</tr>
			<tr>
				<td><img src="images/icon_DEcomplete.gif" alt="Completed" title="" /></td>
				<td>Completed</td>
			</tr>
			<tr>
				<td><img src="images/icon_Stopped.gif" alt="Canceled / Reschedule" title="" /></td>
				<td>Canceled / No Reschedule</td>
			</tr>
			<tr>
				<td><img src="images/icon_Stopped.gif" border="1" alt="Canceled / No Reschedule" title="" /></td>
				<td>Canceled / Reschedule</td>
			</tr>
			<tr>
				<td><img src="images/icon_Skipped.gif" alt="No Show / No Reschedule" title="" /></td>
				<td>No Show / No Reschedule</td>
			</tr>
			<tr>
				<td><img src="images/icon_Skipped.gif" border="1" alt="No Show / Reschedule" title="" /></td>
				<td>No Show / Reschedule</td>
			</tr>
<!--
 			<tr>
				<td><img src="images/icon_Locked.gif" alt="Locked" title="" /></td>
				<td>Locked</td>
			</tr>
-->
			<tr>
				<th colspan="2">Data Entry Status</th>
			</tr>
			<tr>
				<td><img src="images/icon_NotStarted.gif" alt="Not Started" title="" /></td>
				<td>Not Started</td>
			</tr>
			<tr>
				<td><img src="images/icon_InitialDE.gif" alt="Started" title="" /></td>
				<td>Started</td>
			</tr>
			<tr>
				<td><img src="images/icon_DEcomplete.gif" alt="Completed" title="" /></td>
				<td>Completed</td>
			</tr>
			<tr>
				<th colspan="2">All</th>
			</tr>
			<tr>
				<td><img src="images/bt_View.gif" alt="View" title="" /></td>
				<td>View</td>
			</tr>
			<tr>
				<td><img src="images/bt_Edit.gif" alt="Edit" title="" /></td>
				<td>Edit</td>
			</tr>			
			<c:if test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
			 <tr>
				<td><img src="images/bt_Restore.gif" alt="Restore" title="" /></td>
				<td>Restore</td>
			</tr>
			
			<tr>
				<td><img src="images/bt_Remove.gif" alt="Remove" title="" /></td>
				<td>Remove</td>
			</tr>
			 <tr>
				<td><img src="images/bt_Assign.gif" alt="Add to Another Study" title="" /></td>
				<td>Add to Another Study</td>
			</tr>
			 <tr>
				<td><img src="images/bt_Reassign.gif" alt="Transfer to Another Site" title="" /></td>
				<td>Transfer to Another Site</td>
			</tr>
			
			</c:if>
		</table>

		<div class="sidebar_tab_content">

			<a href="javascript:void(0);">View All Icons</a>

		</div>

		</td>
	</tr>
	
	<tr id="sidebar_IconKey_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_IconKey_open'); leftnavExpand('sidebar_IconKey_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title="" /></a>

		<b>Icon Key</b>

		</td>
	</tr>