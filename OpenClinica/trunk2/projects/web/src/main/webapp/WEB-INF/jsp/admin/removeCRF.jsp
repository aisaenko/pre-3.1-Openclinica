<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>


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
<jsp:useBean scope='request' id='crfToRemove' class='org.akaza.openclinica.bean.admin.CRFBean'/>

<h1><span class="title_manage"><fmt:message key="confirm_removal_of_CRF" bundle="${resword}"/></span></h1>

<span class="table_title"><fmt:message key="you_choose_to_remove_the_following_CRF" bundle="${restext}"/>:</span>
<table  cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">
  <tr valign="top" ><td class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${crfToRemove.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${crfToRemove.description}"/>
  </td></tr>
</table>
 <br/>

<span class="table_title">
<fmt:message key="CRF_versions" bundle="${resword}"/></span>
<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders hrow">
  <tr valign="top">
    <th ><fmt:message key="CRF_name" bundle="${resword}"/></th>
    <th ><fmt:message key="version_name" bundle="${resword}"/></th>

    <th ><fmt:message key="description" bundle="${resword}"/></th>

     <th ><fmt:message key="status" bundle="${resword}"/></th>

     <th ><fmt:message key="revision_notes" bundle="${resword}"/></td>
    </tr>
  <c:forEach var ="version" items="${crfToRemove.versions}">
    <tr valign="top">
    <td class="table_cell"><c:out value="${crfToRemove.name}"/></td>
    <td class="table_cell"><c:out value="${version.name}"/></td>

    <td class="table_cell"><c:out value="${version.description}"/></td>

     <td class="table_cell"><c:out value="${version.status.name}"/></td>

     <td class="table_cell"><c:out value="${version.revisionNotes}"/></td>
    </tr>
 </c:forEach>
</table>
 <br>
<span class="table_title"><fmt:message key="event_CRFs" bundle="${resword}"/></span>
<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders hrow">
 <tr valign="top">
    <th ><fmt:message key="SE" bundle="${resword}"/></th>
    <th ><fmt:message key="date_interviewed" bundle="${resword}"/></th>
     <th ><fmt:message key="status" bundle="${resword}"/></th>
    </tr>
  <c:forEach var="eventCRF" items="${eventCRFs}">
    <tr valign="top">
    <td class="table_cell"><c:out value="${eventCRF.eventName}"/></td>
    <td class="table_cell"><c:out value="${eventCRF.dateInterviewed}"/></td>
    <td class="table_cell"><c:out value="${eventCRF.status.name}"/></td>
    </tr>
 </c:forEach>
</table>
 
<br>
<form action='RemoveCRF?action=submit&id=<c:out value="${crfToRemove.id}"/>' method="POST">
<input type="submit" name="submit" value="<fmt:message key="remove_CRF" bundle="${resword}"/>" class="button_long" onClick='return confirm("<fmt:message key="if_you_remove_this_CRF" bundle="${restext}"/>");'>
<input type="button" onclick="confirmCancel('ListCRF');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
</form>

<jsp:include page="../include/footer.jsp"/>
