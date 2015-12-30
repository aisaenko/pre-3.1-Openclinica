<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<tr id="sidebar_IconKey_open">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_IconKey_open'); leftnavExpand('sidebar_IconKey_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Icon Key</b><br clear="all"><br>

		<table border="0" cellpadding="4" cellspacing="0">
			<tr>
				<td><img src="images/icon_Note.gif"></td>
				<td>Discrepancy Note</td>
			</tr>
			<tr>
				<td><img src="images/icon_noNote.gif"></td>
				<td>Add Discrepancy Note</td>
			</tr>
			<tr>
				<td><img src="images/icon_UnchangedData.gif"></td>
				<td>Form data not modified</td>
			</tr>
			<tr>
				<td><img src="images/icon_UnsavedData.gif"></td>
				<td>Unsaved data in form</td>
			</tr>
		</table>

		<div class="sidebar_tab_content">

			<a href="javascript:void(0);">View All Icons</a>

		</div>

		</td>
	</tr>
	<tr id="sidebar_IconKey_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_IconKey_open'); leftnavExpand('sidebar_IconKey_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Icon Key</b>

		</td>
	</tr>