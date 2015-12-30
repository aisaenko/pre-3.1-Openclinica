<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>	
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/managestudy-header.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
        <fmt:message key="confirm_restore_of_this_event_to_Study"  bundle="${resword}"/> <c:out value="${study.name}"/>. <fmt:message key="the_event_and_all_data_associated_with_it"  bundle="${resword}"/>
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="request" id="displayEvent" class="org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope="request" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>

<h1><span class="title_manage">
<fmt:message key="restore_event_from_study" bundle="${resword}"/>
</span></h1>

<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">
  <tr valign="top"><td class="table_header_column"><fmt:message key="event_definition_name" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${displayEvent.studyEvent.studyEventDefinition.name}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="location" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${displayEvent.studyEvent.location}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="visit" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${displayEvent.studyEvent.sampleOrdinal}"/></td></tr>
    
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_started" bundle="${resword}"/>:</td><td class="table_cell"><fmt:formatDate value="${displayEvent.studyEvent.dateStarted}" pattern="${dteFormat}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_ended" bundle="${resword}"/>:</td><td class="table_cell"><fmt:formatDate value="${displayEvent.studyEvent.dateEnded}" pattern="${dteFormat}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="status" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${displayEvent.studyEvent.status.name}"/>
  </td></tr>

 </table>
<br>
 <c:choose>
 <c:when test="${!empty displayEvent.displayEventCRFs}"> 
 <span class="table_title_manage"><fmt:message key="event_CRFs" bundle="${resword}"/></span>
<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
    <tr>
		<th><fmt:message key="CRF_name" bundle="${resword}"/></th>
		<th><fmt:message key="version" bundle="${resword}"/></th>
		<th><fmt:message key="date_interviewed" bundle="${resword}"/></th>
		<th><fmt:message key="interviewer_name" bundle="${resword}"/></th>
		<th><fmt:message key="owner" bundle="${resword}"/></th>
		<th><fmt:message key="completion_status" bundle="${resword}"/></th>	
		<th><fmt:message key="status" bundle="${resword}"/></th>	
	 </tr>
 <c:forEach var="dec" items="${displayEvent.displayEventCRFs}">
	<tr>
		<td class="table_cell"><c:out value="${dec.eventCRF.crf.name}" /></td>
		<td class="table_cell"><c:out value="${dec.eventCRF.crfVersion.name}" /></td>
		<td class="table_cell"><fmt:formatDate value="${dec.eventCRF.dateInterviewed}" pattern="${dteFormat}"/></td>
		<td class="table_cell"><c:out value="${dec.eventCRF.interviewerName}"/></td>
		<td class="table_cell"><c:out value="${dec.eventCRF.owner.name}" /></td>
		<td class="table_cell"><c:out value="${dec.stage.name}" /></td>	
		<td class="table_cell"><c:out value="${dec.eventCRF.status.name}" /></td>	
	 </tr>
 </c:forEach> 
 
 </table> 

<br>
 </c:when>
 <c:otherwise>
  <p><fmt:message key="no_event_CRFs" bundle="${resword}"/></p>
 </c:otherwise>
 </c:choose>
   <c:choose>
    <c:when test="${!empty displayEvent.displayEventCRFs}">
     <form action='RestoreStudyEvent?action=submit&id=<c:out value="${displayEvent.studyEvent.id}"/>&studySubId=<c:out value="${studySub.id}"/>' method="POST">
      <input type="submit" name="submit" value="<fmt:message key="restore_event_to_study" bundle="${resword}"/>" class="button_xlong" onClick='return confirm("<fmt:message key="this_event_has_CRF_data_shown_restore" bundle="${resword}"/>");'>
         &nbsp;
       <input type="button" onclick="confirmCancel('ViewStudySubject?id=<c:out value="${studySub.id}"/>');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
         
     </form>
    
    </c:when>
    <c:otherwise>
      <form action='RestoreStudyEvent?action=submit&id=<c:out value="${displayEvent.studyEvent.id}"/>&studySubId=<c:out value="${studySub.id}"/>' method="POST">
      <input type="submit" name="submit" value="<fmt:message key="restore_event_to_study" bundle="${resword}"/>" class="button_xlong" onClick='return confirm("<fmt:message key="are_you_sure_you_want_to_restore_it" bundle="${resword}"/>");'>
          &nbsp;
        <input type="button" onclick="confirmCancel('ViewStudySubject?id=<c:out value="${studySub.id}"/>');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
     </form>
      
    </c:otherwise>
   </c:choose>  
 
<br><br>

<jsp:include page="../include/footer.jsp"/>
