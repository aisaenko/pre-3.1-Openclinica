<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:import url="../include/managestudy-header.jsp"/>

<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='eventsForVersion' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<h1><span class="title_manage">
<fmt:message key="confirm_deletion_of_CRF_version" bundle="${resword}"/></span>
</h1>
<span class="table_title"><fmt:message key="you_choose_to_delete_the_following" bundle="${restext}"/></span>
<table cellpadding="0" border="0" class="shaded_table table_first_column_w30  cell_borders fcolumn">
  <tr valign="top" ><td class="table_header_column_top"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell_top">
  <c:out value="${version.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${version.description}"/>
  </td></tr>

</table>
<br/>

<c:if test="${!empty definitions}">
<span class="table_title"><fmt:message key="associated_ED" bundle="${resword}"/></span>

<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders hrow">
   <tr valign="top">
    <th><fmt:message key="SE" bundle="${resword}"/></th>
    <th><fmt:message key="study_ID" bundle="${resword}"/></th>
    <th><fmt:message key="date_created" bundle="${resword}"/></th>
    <th><fmt:message key="owner" bundle="${resword}"/></th>
   </tr>
  <c:forEach var="eventDefinitionCRF" items="${definitions}">
    <tr valign="top">
    <td class="table_cell"><c:out value="${eventDefinitionCRF.eventName}"/></td>
      <td class="table_cell"><c:out value="${eventDefinitionCRF.studyId}"/></td>
    <td class="table_cell">
      <c:if test="${eventDefinitionCRF.createdDate != null}">
      <fmt:formatDate value="${eventDefinitionCRF.createdDate}" pattern="${dteFormat}"/>
      </c:if>&nbsp;
    </td>
    <td class="table_cell"><c:out value="${eventDefinitionCRF.owner.name}"/></td>
    </tr>
 </c:forEach>
</table>

</c:if>

<c:if test="${!empty eventsForVersion}">

<span class="table_title_Admin">
<fmt:message key="associated_event_CRFs" bundle="${resword}"/></span>
<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders hrow">
  <tr valign="top">
    <th><fmt:message key="SE_ID" bundle="${resword}"/></th>
    <th><fmt:message key="date_interviewed" bundle="${resword}"/></th>
    <th><fmt:message key="status" bundle="${resword}"/></th>
   </tr>
  <c:forEach var="eventCRF" items="${eventsForVersion}">
    <tr valign="top">
    <td class="table_cell_left"><c:out value="${eventCRF.studyEventId}"/></td>
    <td class="table_cell">
      <c:if test="${eventCRF.dateInterviewed != null}">
      <fmt:formatDate value="${eventCRF.dateInterviewed}" pattern="${dteFormat}"/>
      </c:if>&nbsp;
    </td>
    <td class="table_cell"><c:out value="${eventCRF.status.name}"/></td>
    </tr>
 </c:forEach>
</table>

</c:if>
<br/>
<c:choose>
 <c:when test="${empty eventsForVersion && empty definitions}">
  <form action='DeleteCRFVersion?action=submit&verId=<c:out value="${version.id}"/>' method="POST">
   <input type="submit" name="submit" value="<fmt:message key="delete_CRF_version" bundle="${resword}"/>" class="button_xlong" onClick='return confirm("<fmt:message key="if_you_delete_this_CRF_version" bundle="${restext}"/>");'>
      &nbsp;
   <input type="button" onclick="confirmCancel('ListCRF');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
  </form>
 </c:when>
 <c:otherwise>
  <p><a href="ListCRF"  class="go_back_to_"><fmt:message key="go_back_to_CRF_list" bundle="${resword}"/></a></p>
 </c:otherwise>
</c:choose>

<jsp:include page="../include/footer.jsp"/>
