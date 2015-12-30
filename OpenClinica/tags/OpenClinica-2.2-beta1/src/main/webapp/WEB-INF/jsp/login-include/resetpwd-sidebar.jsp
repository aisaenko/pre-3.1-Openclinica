<script language="JavaScript">
function reportBug(versionNumber) {
 var bugtrack = "http://dev.openclinica.org/OpenClinica/bug.php?version=" + versionNumber  +"&url=";
 bugtrack = bugtrack + window.location.href;
 openDocWindow(bugtrack);

}
</script>
<!-- Breadcrumbs -->

	<div class="breadcrumbs">
	<a class="breadcrumb_completed"
			href="MainMenu">
			Home</a>	
	&nbsp;

	
	</div>

<!-- End Breadcrumbs -->

				</td>

<!-- Help and OpenClinica Feedback Buttons -->

				<td valign="top">
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>&nbsp;</tr>
					<tr>			
						<td><a href="javascript:reportBug(<fmt:message key='version_number' bundle='${resword}'/>)"
							onMouseDown="javascript:setImage('bt_Help','images/bt_ReportIssue.gif');"
							onMouseUp="javascript:setImage('bt_Help','images/bt_ReportIssue.gif');"><img 
							name="bt_ReportIssue" src="images/bt_ReportIssue.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="openclinica_report_issue" bundle="${resword}"/>" style="margin-top: 2px; margin-left: 2px; margin-right: 2px; margin-bottom: 2px"></a>
						</td>
					</tr>
					<tr>
						<td><a href="javascript:openDocWindow(<c:out value="${sessionScope.supportURL}" />)"
							onMouseDown="javascript:setImage('bt_Support','images/bt_Support_d.gif');"
							onMouseUp="javascript:setImage('bt_Support','images/bt_Support.gif');"><img 
							name="bt_Support" src="images/bt_Support.gif" border="0" alt="<fmt:message key="openclinica_feedback" bundle="${resword}"/>" title="<fmt:message key="openclinica_feedback" bundle="${resword}"/>" style="margin-top: 2px; margin-left: 2px; margin-right: 2px; margin-bottom: 15px"></a>
						</td>
					</tr>
				</table>
				</td>

<!-- end Help and OpenClinica Feedback Buttons -->

	<td valign="top" align="right">


<!-- User Box -->


<!-- End User Box -->

				</td>
			</tr>
		</table>
<!-- End Header Table -->
<table border="0" cellpadding=0" cellspacing="0">
	<tr>
		<td class="sidebar" valign="top">

<!-- Sidebar Contents -->

	<br><br>
	<a href="RequestAccount">Request an Account</a>
	<br><br>
	<a href="RequestPassword">Forgot Password?</a>
	


<!-- End Sidebar Contents -->

				<br><img src="images/spacer.gif" width="120" height="1">

				</td>
				<td class="content" valign="top">