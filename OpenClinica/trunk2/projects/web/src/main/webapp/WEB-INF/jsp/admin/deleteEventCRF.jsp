<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:import url="../include/managestudy-header.jsp"/>

<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope="request" id="displayEventCRF" class="org.akaza.openclinica.bean.submit.DisplayEventCRFBean"/>
<jsp:useBean scope="request" id="event" class="org.akaza.openclinica.bean.managestudy.StudyEventBean"/>
<jsp:useBean scope="request" id="items" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<h1><span class="title_manage">
<fmt:message key="delete_CRF_from_event" bundle="${resword}"/>
</span></h1>
<p>
<fmt:message key="confirm_deletion_of_this_CRF" bundle="${restext}">
	<fmt:param value="${event.studyEventDefinition.name}"/>
	<fmt:param>
	  <fmt:formatDate value="${event.dateStarted}" pattern="${dteFormat}"/>
	</fmt:param>
</fmt:message>
</p>
<jsp:include page="../include/alertbox.jsp" />

<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">
   <tr valign="top"><td class="table_header_column"><fmt:message key="event_definition_name" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${event.studyEventDefinition.name}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="location" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${event.location}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="visit" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${event.sampleOrdinal}"/></td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="date_started" bundle="${resword}"/>:</td><td class="table_cell"><fmt:formatDate value="${event.dateStarted}" pattern="${dteFormat}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_ended" bundle="${resword}"/>:</td><td class="table_cell"><fmt:formatDate value="${event.dateEnded}" pattern="${dteFormat}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="status" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${event.status.name}"/>
  </td></tr>

 </table>
 
<br>
<span class="table_title_admin"><fmt:message key="event_CRF" bundle="${resword}"/></span>
<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
     <tr>
		<th><fmt:message key="CRF_name" bundle="${resword}"/></th>
		<th><fmt:message key="version" bundle="${resword}"/></th>
		<th><fmt:message key="date_interviewed" bundle="${resword}"/></th>
		<th><fmt:message key="interviewer_name" bundle="${resword}"/></th>
		<th><fmt:message key="owner" bundle="${resword}"/></th>
		<th><fmt:message key="completion_status" bundle="${resword}"/></th>

	 </tr>
	<tr>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.crf.name}" /></td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.crfVersion.name}" /></td>
		<td class="table_cell"><fmt:formatDate value="${displayEventCRF.eventCRF.dateInterviewed}" pattern="${dteFormat}"/>&nbsp;</td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.interviewerName}"/>&nbsp;</td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.owner.name}" /></td>
		<td class="table_cell"><c:out value="${displayEventCRF.stage.name}" /></td>

	 </tr>

 </table>
<br>
 <c:if test="${!empty items}">
 <span class="table_title_admin"><fmt:message key="item_data" bundle="${resword}"/></span>
 <table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
     <tr>
		<th><fmt:message key="Id" bundle="${resword}"/></th>
		<th><fmt:message key="value" bundle="${resword}"/></th>
	 </tr>
	 <c:set var="count" value="0"/>
  <c:forEach var="item" items="${items}">
	<tr>
		<td class="table_cell"><c:out value="${item.itemId}" /></td>
		<td class="table_cell"><c:out value="${item.value}" />&nbsp;</td>


		<c:if test="${item.status.name !='removed' && item.status.name !='auto-removed'}">
		<c:set var="count" value="${count+1}"/>
		</c:if>
	 </tr>
	</c:forEach>

 </table>
<br>
 </c:if>
   <c:choose>
    <c:when test="${!empty items && count>0}">
     <form action='DeleteEventCRF?action=submit&ecId=<c:out value="${displayEventCRF.eventCRF.id}"/>&ssId=<c:out value="${studySub.id}"/>' method="POST">
      <input type="submit" name="submit" value="<fmt:message key="delete_event_CRF" bundle="${resword}"/>" class="button_xlong" onClick='return confirm("<fmt:message key="this_CRF_has_data_want_delete" bundle="${restext}"/>");'>
         &nbsp;
       <input type="button" onclick="confirmCancel('EnterDataForStudyEvent?eventId=<c:out value="${event.id}"/>');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
     </form>
    </c:when>
    <c:otherwise>
      <form action='DeleteEventCRF?action=submit&ecId=<c:out value="${displayEventCRF.eventCRF.id}"/>&ssId=<c:out value="${studySub.id}"/>' method="POST">
      <input type="submit" name="submit" value="<fmt:message key="delete_event_CRF" bundle="${resword}"/>" class="button_xlong" onClick='return confirm("<fmt:message key="are_you_sure_you_want_to_delete_it" bundle="${restext}"/>");'>
          &nbsp;
        <input type="button" onclick="confirmCancel('EnterDataForStudyEvent?eventId=<c:out value="${event.id}"/>');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
     </form>
    </c:otherwise>
   </c:choose>
<br>
 <jsp:include page="../include/footer.jsp"/>
