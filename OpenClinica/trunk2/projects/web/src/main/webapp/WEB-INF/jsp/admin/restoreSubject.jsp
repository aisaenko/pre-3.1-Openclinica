<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>



  <c:import url="../include/managestudy-header.jsp"/>

<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='studySubs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='events' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='subjectToRestore' class='org.akaza.openclinica.bean.submit.SubjectBean'/>

<h1><span class="title_manage"><fmt:message key="confirm_restore_of_subject" bundle="${restext}"/></span></h1>

<p><div class="table_name"><fmt:message key="you_choose_to_restore_the_following_subject2" bundle="${restext}"/>:</div>

<table border="0" cellpadding="0" width="700"   class="shaded_table  table_first_column_w30 cell_borders fcolumn">
  <tr valign="top" ><td class="table_header_column"><fmt:message key="person_ID" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${subjectToRestore.uniqueIdentifier}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="gender" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${subjectToRestore.gender}"/>
  </td>
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_of_birth" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${subjectToRestore.dateOfBirth}" pattern="${dteFormat}"/>
  </td>
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_created" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${subjectToRestore.createdDate}" pattern="${dteFormat}"/>
  </td>
  </tr>
</table>
<br/>

<div class="table_name"><fmt:message key="associated_study_subjects" bundle="${restext}"/>:</div>
<table border="0" cellpadding="0" cellspacing="0"  width="700"   class="shaded_table  cell_borders  hrow">
 <tr valign="top">
    <th><fmt:message key="study_subject_ID" bundle="${resword}"/></th>
    <th><fmt:message key="secondary_ID" bundle="${resword}"/></th>
    <th><fmt:message key="study_record_ID" bundle="${resword}"/></th>
    <th><fmt:message key="enrollment_date" bundle="${resword}"/></th>
    <th><fmt:message key="date_created" bundle="${resword}"/></th>
    <th><fmt:message key="created_by" bundle="${resword}"/></th>
    <th><fmt:message key="status" bundle="${resword}"/></th>
    </tr>
  <c:forEach var="studySub" items="${studySubs}">
    <tr valign="top">
    <td class="table_cell"><c:out value="${studySub.label}"/></td>
    <td class="table_cell"><c:out value="${studySub.secondaryLabel}"/>&nbsp;</td>
    <td class="table_cell"><c:out value="${studySub.studyId}"/></td>
    <td class="table_cell"><fmt:formatDate value="${studySub.enrollmentDate}" pattern="${dteFormat}"/></td>
    <td class="table_cell"><fmt:formatDate value="${studySub.createdDate}" pattern="${dteFormat}"/></td>
    <td class="table_cell"><c:out value="${studySub.owner.name}"/></td>
    <td class="table_cell"><c:out value="${studySub.status.name}"/></td>
    </tr>
 </c:forEach>
</table>
<br>
<div class="table_name"><fmt:message key="associated_study_events" bundle="${restext}"/>:</div>
<table border="0" cellpadding="0" cellspacing="0"  width="700"   class="shaded_table cell_borders  hrow  ">
  <tr valign="top">
    <th><fmt:message key="record_ID" bundle="${resword}"/></th>
    <th><fmt:message key="location" bundle="${resword}"/></th>
    <th><fmt:message key="date_started" bundle="${resword}"/></th>
    <th><fmt:message key="created_by" bundle="${resword}"/></th>
    <th><fmt:message key="status" bundle="${resword}"/></th>
    </tr>
  <c:forEach var="event" items="${events}">
    <tr valign="top">
    <td class="table_cell"><c:out value="${event.id}"/></td>
    <td class="table_cell"><c:out value="${event.location}"/>&nbsp;</td>
    <td class="table_cell"><fmt:formatDate value="${event.createdDate}" pattern="${dteFormat}"/></td>
    <td class="table_cell"><c:out value="${event.owner.name}"/></td>
    <td class="table_cell"><c:out value="${event.status.name}"/></td>
    </tr>
 </c:forEach>
</table>

<p>
<form action='RestoreSubject?action=submit&id=<c:out value="${subjectToRestore.id}"/>' method="POST">
 <input type="submit" name="submit" value="<fmt:message key="restore_subject" bundle="${resword}"/>" class="button_long" onClick='return confirm("<fmt:message key="restore_explication" bundle="${restext}"/>");'>
</form>


<jsp:include page="../include/footer.jsp"/>
