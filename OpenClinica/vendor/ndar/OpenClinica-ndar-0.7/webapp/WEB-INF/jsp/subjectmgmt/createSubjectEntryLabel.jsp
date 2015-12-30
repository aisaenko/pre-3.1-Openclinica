<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/subjectMgmt-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope="session" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean" />
<jsp:useBean scope="request" id="pageMessages" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<c:set var="subjectEntryLabelabel" value="" />
<c:set var="subjectEntryLabelabelDescription" value="" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "subjectEntryLabelLabel"}'>
		<c:set var="subjectEntryLabelLabel" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "subjectEntryLabelDescription"}'>
		<c:set var="subjectEntryLabelDescription" value="${presetValue.value}" />
	</c:if>
</c:forEach>


<h1><span class="title_SubjectMgmt">
Create A Subject Entry Label <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step1')"><img src="images/bt_Help_SubjectMgmt.gif" border="0" alt="Help" title="Help"></a>
</span></h1>

<c:import url="instructionsSetupStudyEvent.jsp">
	<c:param name="currStep" value="enroll" />
</c:import>

<jsp:include page="../include/alertbox.jsp" />

<br/>* indicates required field.</P>
<form action="CreateSubjectEntryLabel" method="post">
<jsp:include page="../include/showSubmitted.jsp" />

<div style="width: 550px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="5"> 
	<tr valign="top">
		<td class="formlabel">Label:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldXL_BG">
					  <input type="text" name="subjectEntryLabelLabel" value="<c:out value="${subjectEntryLabelLabel}"/>" size="50" class="formfieldXL">
					</div></td>
					<td >*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="subjectEntryLabelLabel"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
	  	<td class="formlabel">Description:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldXL_BG">
						<input type="text" name="subjectEntryLabelDescription" value="<c:out value="${subjectEntryLabelDescription}"/>" size="50" class="formfieldXL">
					</div></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="subjectEntryLabelDescription"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</div>

</div></div></div></div></div></div></div></div>

</div>

<c:if test="${(!empty groups)}">
<br>
<div style="width: 550px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

</div></div></div></div></div></div></div></div>

</div>
</c:if>
<br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td><input type="submit" name="submitBack" value="Save and Go Back" class="button_long"></td>
<td><input type="submit" name="submitCreate" value="Save and Create Another" class="button_long"></td>
<td><input type="submit" name="submitDone" value=" Save and Finish" class="button_long"></td>
</tr>
</table>
</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<jsp:include page="../include/footer.jsp"/>