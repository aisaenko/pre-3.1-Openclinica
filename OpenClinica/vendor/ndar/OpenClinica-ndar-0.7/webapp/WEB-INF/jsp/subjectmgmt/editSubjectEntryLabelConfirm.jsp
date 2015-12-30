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
	<c:if test='${presetValue.key == "stepNum"}'>
		<c:set var="stepNum" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "subjectEntryLabelId"}'>
		<c:set var="subjectEntryLabelId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "subjectEntryLabelLabel"}'>
		<c:set var="subjectEntryLabelLabel" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "subjectEntryLabelDescription"}'>
		<c:set var="subjectEntryLabelDescription" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<h1><span class="title_SubjectMgmt">Edit a Subject Entry Label - Confirmation Screen</span></h1>

<form action="EditSubjectEntryLabel" method="post">
<jsp:include page="../include/showSubmitted.jsp" />

<input type="hidden" name="subjectEntryLabelId" value='<c:out value="${subjectEntryLabelId}"/>'/>
<input type="hidden" name="stepNum" value='<c:out value="${stepNum}"/>'/>
<input type="hidden" name="subjectEntryLabelLabel" value='<c:out value="${subjectEntryLabelLabel}"/>'/>
<input type="hidden" name="subjectEntryLabelDescription" value='<c:out value="${subjectEntryLabelDescription}"/>'/>

<div style="width: 400px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
	  	<td class="table_header_column_top">Label:</td>
	  	<td class="table_cell"><c:out value="${subjectEntryLabelLabel}" /></td>
	</tr>

  <tr valign="bottom">
  	<td class="table_header_column">Description:</td>
  	<td class="table_cell"><c:out value="${subjectEntryLabelDescription}" /></td>
  </tr>


	</table>
	</div>

	</div></div></div></div></div></div></div></div>

	</div>
<br>

<input type="submit" name="submit" value="Back" class="button_long">
<input type="submit" name="submit" value="Confirm" class="button_long">

</form>

<jsp:include page="../include/footer.jsp"/>
