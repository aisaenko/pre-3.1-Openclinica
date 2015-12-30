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


<jsp:useBean scope='session' id='itemsHaveData' class='java.util.ArrayList'/>
<jsp:useBean scope='session' id='eventsForVersion' class='java.util.ArrayList'/>
<jsp:useBean scope='session' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>
<h1><span class="title_manage"><fmt:message key="create_a_new_CRF_version" bundle="${resword}"/> - <fmt:message key="remove_previous_same_version_error" bundle="${resword}"/>
</span></h1>


 <c:if test="${not empty eventsForVersion}">

<span class="alert">
	<fmt:message key="the_previous_CRF_version_has_associated_SE" bundle="${restext}">
	  <fmt:param><c:out value="${version.crfId}"/></fmt:param>
	</fmt:message>
</span>
 <table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">
    <tr valign="top">
      <td class="table_header_column_top"><fmt:message key="event_ID" bundle="${resword}"/></td>
      <td class="table_header_column_top"><fmt:message key="owner" bundle="${resword}"/></td>
    </tr>
  <c:forEach var="event" items="${eventsForVersion}">
  <tr valign="top">
      <td class="table_cell"><c:out value="${event.id}"/></td>
      <td class="table_cell"><c:out value="${event.owner.name}"/></td>
    </tr>
 </c:forEach>
 </table>
 <br>
 </c:if>
 <br><br>
 <c:if test="${!empty itemsHaveData}">
 <span class="alert">
	 <fmt:message key="some_items_in_the_previous_version_have_related" bundle="${restext}">
	 	<fmt:param><c:out value="${version.crfId}"/></fmt:param>
	 </fmt:message>
 </span>
 
<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
   <tr valign="top">
      <th><fmt:message key="item_ID" bundle="${resword}"/></th>
      <th><fmt:message key="name" bundle="${resword}"/></th>
      <th><fmt:message key="owner" bundle="${resword}"/></th>
    </tr>
  <c:forEach var="item" items="${itemsHaveData}">
  <tr valign="top">
      <td class="table_cell"><c:out value="${item.id}"/></td>
      <td class="table_cell"><c:out value="${item.name}"/></td>
      <td class="table_cell"><c:out value="${item.owner.name}"/></td>
    </tr>
 </c:forEach>

</table>
<br>

</c:if>
 <br>
<jsp:include page="../include/footer.jsp"/>
