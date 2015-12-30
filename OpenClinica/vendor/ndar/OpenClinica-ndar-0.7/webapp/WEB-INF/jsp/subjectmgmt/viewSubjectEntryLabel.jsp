<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/subjectMgmt-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>


<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='subjectEntryLabel' class='org.akaza.openclinica.bean.subject.SubjectEntryLabelBean'/>
<jsp:useBean scope='request' id='message' class='java.lang.String'/>

<h1><span class="title_SubjectMgmt">
View Subject Entry Label
</span></h1>

<jsp:include page="../include/alertbox.jsp" />

<div style="width: 400px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="table_header_column_top">Label:</td>
		<td class="table_cell_top"><c:out value="${subjectEntryLabel.name}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Description:</td>
		<td class="table_cell"><c:out value="${subjectEntryLabel.description}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Date Created:</td>
		<td class="table_cell"><c:out value="${subjectEntryLabel.createdDate}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Created by:</td>
		<td class="table_cell"><c:out value="${subjectEntryLabel.owner.name}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Date Updated:</td>
		<td class="table_cell"><c:out value="${subjectEntryLabel.updatedDate}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Updated by:</td>
		<td class="table_cell"><c:out value="${subjectEntryLabel.updater.name}" />&nbsp;</td>
	</tr>
<!-- TODO:
for each study user is in, show:
•	Role
•	Studies created/owned
•	CRFs created/owned (including versions)
•	Study Events created/owned
•	Subjects created/owned
•	Queries created/owned
•	Datasets downloaded
•	Link to reload page including full audit record for User.

-->

	</table>
	</div>

	</div></div></div></div></div></div></div></div>

	</div>

<c:if test="${userBean.piiPrivilege.name=='edit'}"> 
<table border="0" cellpadding="0" cellspacing="0">
  <tr>
   <td>
   <form action='EditSubjectEntryLabel?subjectEntryLabelId=<c:out value="${subjectEntryLabel.id}" />' method="POST">
    <input type="submit" name="submit" value="Edit this subject entry label" class="button_xlong">
   </form>
   </td>
  </tr>
</table>
</c:if>
<jsp:include page="../include/footer.jsp"/>