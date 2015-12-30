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

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='eventCRFs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='versionToRemove' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>

<h1><span class="title_manage"><fmt:message key="confirm_removal_of_CRF_version" bundle="${resword}"/></span></h1>

<span class="table_title"><fmt:message key="you_choose_to_remove_the_following_CRF_version" bundle="${restext}"/>:</span>
<table width="500" cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">
  <tr valign="top" ><td class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${versionToRemove.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${versionToRemove.description}"/>
  </td></tr>

</table>

<br/>

<span class="table_title"><fmt:message key="associated_event_CRFs" bundle="${resword}"/></span>
<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders hrow">
   <tr valign="top">
   <th><fmt:message key="study_subject_label" bundle="${resword}"/></th>
   <th><fmt:message key="study_event" bundle="${resword}"/></th>
    <th><fmt:message key="repeat_number" bundle="${resword}"/></th>
    <th><fmt:message key="date_interviewed" bundle="${resword}"/></th>
   </tr>
  <c:forEach var="eventCRF" items="${eventCRFs}">
    <tr valign="top">
    <td class="table_cell"><c:out value="${eventCRF.studySubjectName}"/></td>
    <td class="table_cell"><c:out value="${eventCRF.eventName}"/></td>
    <td class="table_cell"><c:out value="${eventCRF.eventOrdinal}"/></td>
    <td class="table_cell">
    <c:if test="${eventCRF.dateInterviewed != null}">
     <fmt:formatDate value="${eventCRF.dateInterviewed}" pattern="${dteFormat}"/>
    </c:if>&nbsp;
    </td>
 </c:forEach>
</table>

<br/>
<form action='RemoveCRFVersion?module=<c:out value="${module}"/>&action=submit&id=<c:out value="${versionToRemove.id}"/>' method="POST">

 <input type="submit" name="submit" value="<fmt:message key="remove_CRF_version" bundle="${resword}"/>" class="button_xlong" onClick='return confirm("<fmt:message key="if_you_remove_this_CRF_version" bundle="${restext}"/>");'>
 <input type="button" onclick="confirmCancel('ListCRF');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
</form>

 <jsp:include page="../include/footer.jsp"/>
