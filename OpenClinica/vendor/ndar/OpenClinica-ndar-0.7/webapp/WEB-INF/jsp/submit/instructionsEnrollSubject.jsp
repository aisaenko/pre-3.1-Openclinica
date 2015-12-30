<jsp:include page="../include/submit-header.jsp"/>
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

	<h1><span class="title_submit">Add Subject - Instructions <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html')"><img src="images/bt_Help_Submit.gif" border="0" alt="Help" title="Help"></a></span></h1>



	<br>


	To add a subject, enter the subject's unique Id, enrollment date, gender, and birth information.
	<br>You should be able to add a subject w/o adding a study event.
	
	<br> You can then add study events (visits or encounters) by going:
	
	<ol>
	<li> Study Event Overview
	<li> Event CRF Data Submission
	<li> Data Entry
	<li> Mark Event CRF Complete
	</ol>

<form method="POST" action="AddNewSubject">
	<input type="submit" value="Proceed to Add Subject" class="button_long">
</form>
<br>
	<jsp:include page="instructionsSetupStudyEvent.jsp" />
<br>
   <%-- <jsp:include page="instructionsEnterData.jsp" />--%>
<jsp:include page="../include/footer.jsp"/>