<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/subjectMgmt-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='presetValues' class='java.util.HashMap' />

<c:set var="subjectEntryLabelLabel" value="" />
<c:set var="subjectEntryLabelDescription" value="" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "subjectEntryLabelLabel"}'>
		<c:set var="subjectEntryLabelLabel" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "subjectEntryLabelDescription"}'>
		<c:set var="subjectEntryLabelDescription" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<h1><span class="title_SubjectMgmt">Edit a Subject Entry Label</span></h1>

<jsp:include page="../include/alertbox.jsp" />

<form action="EditSubjectEntryLabel" method="post">
<jsp:include page="../include/showSubmitted.jsp" />
<jsp:include page="../include/showHiddenInput.jsp"><jsp:param name="fieldName" value="subjectEntryLabelId" /></jsp:include>
<jsp:include page="../include/showHiddenInput.jsp"><jsp:param name="fieldName" value="stepNum" /></jsp:include>

<div style="width: 400px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="table_header_column_top">Label:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="subjectEntryLabelLabel" value="<c:out value="${subjectEntryLabelLabel}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="subjectEntryLabelLabel" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr valign="top">
		<td class="table_header_column">Description:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="subjectEntryLabelDescription" value="<c:out value="${subjectEntryLabelDescription}"/>" size="20" class="formfieldM"/>
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="subjectEntryLabelDescription" /></jsp:include>
				</tr>
			</table>
		</td>
	</tr>
 	</table>
	</div>

	</div></div></div></div></div></div></div></div>

	</div>

<input type="submit" name="submit" value="Next" class="button_medium" /><br/>
</form>

<jsp:include page="../include/footer.jsp"/>