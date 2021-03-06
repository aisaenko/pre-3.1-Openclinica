<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<html>
<head>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
</head>

<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope="request" id="events" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="eventCRFAudits" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="allDeletedEventCRFs" class="java.util.ArrayList"/>

<body>
 
 <h1>
  <c:out value="${studySub.label}"/> <fmt:message key="audit_logs" bundle="${resword}"/>
 </h1>  


<%-- Subject Summary --%>
<table border="0" cellpadding="0" cellspacing="0" width="650" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">				
	<tr>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="study_subject_ID" bundle="${resword}"/></b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="secondary_subject_ID" bundle="${resword}"/></b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="date_of_birth" bundle="${resword}"/></b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="person_ID" bundle="${resword}"/></b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="date_record_created" bundle="${resword}"/></b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="created_by" bundle="${resword}"/></b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="status" bundle="${resword}"/></b></td>
	</tr>
	<tr>
	 <td class="table_header_column"><c:out value="${studySub.label}"/></td>
	 <td class="table_header_column"><c:out value="${studySub.secondaryLabel}"/></td>
	 <td class="table_header_column"><fmt:formatDate value="${subject.dateOfBirth}" dateStyle="short"/></td>
	 <td class="table_header_column"><c:out value="${subject.uniqueIdentifier}"/></td>
	 <td class="table_header_column"><fmt:formatDate value="${studySub.createdDate}" dateStyle="short"/></td>
	 <td class="table_header_column"><c:out value="${studySub.owner.name}"/></td>
	 <td class="table_header_column"><c:out value="${subject.status.name}"/></td>
	 	
	</tr> 
</table><br><br>

<%-- Subject Audit Events --%>
<table border="0" cellpadding="0" cellspacing="0" width="550" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">				
	<tr>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="audit_event" bundle="${resword}"/></b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="date_time" bundle="${resword}"/></b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="user" bundle="${resword}"/></b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="value_type" bundle="${resword}"/></b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="old" bundle="${resword}"/></b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="new" bundle="${resword}"/></b></td>
	</tr>
	<c:forEach var="studySubjectAudit" items="${studySubjectAudits}">
	<tr>
	 <td class="table_header_column"><c:out value="${studySubjectAudit.auditEventTypeName}"/>&nbsp;</td>
	 <!-- YW 12-06-2007, use dateStyle and timeStyle to display datetime -->
	 <td class="table_header_column"><fmt:formatDate value="${studySubjectAudit.auditDate}" type="both" dateStyle="short" timeStyle="short"/>&nbsp;</td>
	 <td class="table_header_column"><c:out value="${studySubjectAudit.userName}"/>&nbsp;</td>
	 <td class="table_header_column"><c:out value="${studySubjectAudit.entityName}"/>&nbsp;</td> 
	 <td class="table_header_column"><c:out value="${studySubjectAudit.oldValue}"/>&nbsp;</td>
	 <td class="table_header_column"><c:out value="${studySubjectAudit.newValue}"/>&nbsp;</td>
	 	
	</tr>
	</c:forEach> 
</table>
<br>
<%-- Study Events--%>
<%-- TODO:Anchor these to the Study Event Summaries --%>
<table border="0" cellpadding="0" cellspacing="0" width="550" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">
	<tr>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="study_events" bundle="${resword}"/></b><br></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="location" bundle="${resword}"/></b><br></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="date_time" bundle="${resword}"/></b><br></td>
	</tr>
	<c:forEach var="event" items="${events}">
		<tr>
		 <td class="table_header_column"><c:out value="${event.studyEventDefinition.name}"/>&nbsp;</td>
		 <td class="table_header_column"><c:out value="${event.location}"/>&nbsp;</td>
		 <c:choose>
		 <c:when test="${event.startTimeFlag=='false'}">
		 	<td class="table_header_column"><fmt:formatDate value="${event.dateStarted}" dateStyle="short"/>&nbsp;</td>
		 </c:when>
		 <c:otherwise>
		 	<td class="table_header_column"><fmt:formatDate value="${event.dateStarted}" type="both" dateStyle="short" timeStyle="short"/>&nbsp;</td>
		 </c:otherwise>
		 </c:choose>
		</tr>
	</c:forEach> 
</table>
<br>
<c:forEach var="event" items="${events}">
	<%-- Study Event Summary --%>
	<table border="0" cellpadding="0" cellspacing="0" width="550" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">
		<tr>
		 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="name" bundle="${resword}"/></b></td>
		 <td class="table_header_column_top" style="color: #789EC5"><b><c:out value="${event.studyEventDefinition.name}"/></b>&nbsp;</td>
		</tr>
		<tr>
		 <td class="table_header_column"><c:out value="Location"/></td>
		 <td class="table_header_column"><c:out value="${event.location}"/>&nbsp;</td>
		</tr>
		<tr>
		 <td class="table_header_column"><c:out value="Start Date/Time"/></td>
		 <c:choose>
		 <c:when test="${event.startTimeFlag=='false'}">
		 	<td class="table_header_column"><fmt:formatDate value="${event.dateStarted}" dateStyle="short"/>&nbsp;</td>
		 </c:when>
		 <c:otherwise>
		 	<td class="table_header_column"><fmt:formatDate value="${event.dateStarted}" type="both" dateStyle="short" timeStyle="short"/>&nbsp;</td>
		 </c:otherwise>
		 </c:choose>
		</tr>
		<tr>
		 <td class="table_header_column"><c:out value="Status"/></td>
		 <td class="table_header_column"><c:out value="${event.subjectEventStatus.name}"/>&nbsp;</td>
		</tr>
		<tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2">
            <%--Audit for deleted event crfs --%>
                <table border="0"><tr><td width="20">&nbsp;</td><td><%-- Margin --%>
                    <table border="0" cellpadding="0" cellspacing="0" width="550" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">
                <tr>
                    <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="name" bundle="${resword}"/></b></td>
                    <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="version" bundle="${resword}"/></b></td>
                    <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="deleted_by" bundle="${resword}"/></b></td>
                    <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="delete_date" bundle="${resword}"/></b></td>
                </tr>
                <c:forEach var="deletedEventCRF" items="${allDeletedEventCRFs}">
                    <c:if test="${deletedEventCRF.studyEventId==event.id}">

                <tr>
                 <td class="table_header_column"><c:out value="${deletedEventCRF.crfName}"/>&nbsp;</td>
                 <td class="table_header_column"><c:out value="${deletedEventCRF.crfVersion}"/>&nbsp;</td>
                 <td class="table_header_column"><c:out value="${deletedEventCRF.deletedBy}"/>&nbsp;</td>
                 <td class="table_header_column"><fmt:formatDate value="${deletedEventCRF.deletedDate}" type="both" dateStyle="short" timeStyle="short"/>&nbsp;</td>
                </tr>
                    </c:if>
                </c:forEach>
            </table><%-- Event CRFs Audit Events --%>
            </td></tr></table><%-- Margin --%>

            </td>
        </tr>

		<tr><td colspan="2">&nbsp;</td></tr>
		<tr>
            <td colspan="2">
            <%--Audit Events for Study Event --%>
            <table border="0"><tr><td width="20">&nbsp;</td><td><%-- Margin --%>
				<table border="0" cellpadding="0" cellspacing="0" width="550" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">
				<tr>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="audit_event" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="date_time" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="user" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="value_type" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="old" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="new" bundle="${resword}"/></b></td>
				</tr>

				<c:forEach var="studyEventAudit" items="${studyEventAudits}">
					<c:if test="${studyEventAudit.entityId==event.id}">
						<tr>
						 <td class="table_header_column"><c:out value="${studyEventAudit.auditEventTypeName}"/>&nbsp;</td>
						 <td class="table_header_column"><fmt:formatDate value="${studyEventAudit.auditDate}" type="both" dateStyle="short" timeStyle="short"/>&nbsp;</td>
						 <td class="table_header_column"><c:out value="${studyEventAudit.userName}"/>&nbsp;</td>
						 <td class="table_header_column"><c:out value="${studyEventAudit.entityName}"/>&nbsp;</td>
						 <td class="table_header_column">
                             <c:choose>
                                 <c:when test="${studyEventAudit.oldValue eq '0'}">invalid</c:when>
                                 <c:when test="${studyEventAudit.oldValue eq '1'}">scheduled</c:when>
                                 <c:when test="${studyEventAudit.oldValue eq '2'}">not_scheduled</c:when>
                                 <c:when test="${studyEventAudit.oldValue eq '3'}">data_entry_started</c:when>
                                 <c:when test="${studyEventAudit.oldValue eq '4'}">completed</c:when>
                                 <c:when test="${studyEventAudit.oldValue eq '5'}">stopped</c:when>
                                 <c:when test="${studyEventAudit.oldValue eq '6'}">skipped</c:when>
                                 <c:when test="${studyEventAudit.oldValue eq '7'}">locked</c:when>
                                 <c:otherwise><c:out value="${studyEventAudit.oldValue}"/></c:otherwise>
                             </c:choose>
						 &nbsp;</td>
						 <td class="table_header_column">
                             <c:choose>
                                 <c:when test="${studyEventAudit.newValue eq '0'}">invalid</c:when>
                                 <c:when test="${studyEventAudit.newValue eq '1'}">scheduled</c:when>
                                 <c:when test="${studyEventAudit.newValue eq '2'}">not_scheduled</c:when>
                                 <c:when test="${studyEventAudit.newValue eq '3'}">data_entry_started</c:when>
                                 <c:when test="${studyEventAudit.newValue eq '4'}">completed</c:when>
                                 <c:when test="${studyEventAudit.newValue eq '5'}">stopped</c:when>
                                 <c:when test="${studyEventAudit.newValue eq '6'}">skipped</c:when>
                                 <c:when test="${studyEventAudit.newValue eq '7'}">locked</c:when>
                                 <c:otherwise><c:out value="${studyEventAudit.newValue}"/></c:otherwise>
                             </c:choose>
						 &nbsp;</td>
						</tr>
					</c:if>
				</c:forEach>
				</table><%-- Event CRFs Audit Events --%>
			</td></tr></table><%-- Margin --%>

            </td>
        </tr>
		
		<%-- Event CRFs for this Study Event --%>
		<c:forEach var="eventCRF" items="${event.eventCRFs}">
		<tr>
		 <td colspan="2">
			<table border="0" cellpadding="0" cellspacing="0" width="550" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">				
				<tr>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="name" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="version" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="date_interviewed" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="interviewer_name" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="owner" bundle="${resword}"/></b></td>
				</tr>
				<tr>
				 <td class="table_header_column"><c:out value="${eventCRF.crf.name}"/>&nbsp;</td>
		 		 <td class="table_header_column"><c:out value="${eventCRF.crfVersion.name}"/>&nbsp;</td>
				 <td class="table_header_column"><c:out value="${eventCRF.dateInterviewed}"/>&nbsp;</td>
		 		 <td class="table_header_column"><c:out value="${eventCRF.interviewerName}"/>&nbsp;</td>		 				 
				 <td class="table_header_column"><c:out value="${eventCRF.owner.name}"/>&nbsp;</td>
				</tr>
			</table>
		 </td>
		</tr>
		<tr>
		<tr><td colspan="2">&nbsp;</td></tr>
		 <td colspan="2">
		 
			<%-- Event CRFs Audit Events --%>
			<table border="0"><tr><td width="20">&nbsp;</td><td><%-- Margin --%> 
				<table border="0" cellpadding="0" cellspacing="0" width="550" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">				
				<tr>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="audit_event" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="date_time" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="user" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="value_type" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="old" bundle="${resword}"/></b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="new" bundle="${resword}"/></b></td>
				</tr>
				
				<c:forEach var="eventCRFAudit" items="${eventCRFAudits}">
					<c:if test="${eventCRFAudit.eventCRFId==eventCRF.id}">
						<tr>
						 <td class="table_header_column"><c:out value="${eventCRFAudit.auditEventTypeName}"/>&nbsp;</td>
						 <td class="table_header_column"><fmt:formatDate value="${eventCRFAudit.auditDate}" type="both" dateStyle="short" timeStyle="short"/>&nbsp;</td>
						 <td class="table_header_column"><c:out value="${eventCRFAudit.userName}"/>&nbsp;</td>
						 <td class="table_header_column"><c:out value="${eventCRFAudit.entityName}"/>&nbsp;</td> 
						 <td class="table_header_column">
                         <c:choose>
                             <c:when test='${eventCRFAudit.auditEventTypeId == 12 or eventCRFAudit.entityName eq "Status"}'>
                                 <c:if test="${eventCRFAudit.oldValue eq '0'}">invalid</c:if>
                                 <c:if test="${eventCRFAudit.oldValue eq '1'}">available</c:if>
                                 <c:if test="${eventCRFAudit.oldValue eq '2'}">unavailable</c:if>
                                 <c:if test="${eventCRFAudit.oldValue eq '3'}">private</c:if>
                                 <c:if test="${eventCRFAudit.oldValue eq '4'}">pending</c:if>
                                 <c:if test="${eventCRFAudit.oldValue eq '5'}">removed</c:if>
                                 <c:if test="${eventCRFAudit.oldValue eq '6'}">locked</c:if>
                                 <c:if test="${eventCRFAudit.oldValue eq '7'}">auto-removed</c:if>
                             </c:when>
                             <c:otherwise>
                                <c:out value="${eventCRFAudit.oldValue}"/>
                             </c:otherwise>
						 </c:choose>
						 &nbsp;</td>
						 <td class="table_header_column">
                             <c:choose>
                                 <c:when test='${eventCRFAudit.auditEventTypeId == 12 or eventCRFAudit.entityName eq "Status"}'>
                                     <c:if test="${eventCRFAudit.newValue eq '0'}">invalid</c:if>
                                     <c:if test="${eventCRFAudit.newValue eq '1'}">available</c:if>
                                     <c:if test="${eventCRFAudit.newValue eq '2'}">unavailable</c:if>
                                     <c:if test="${eventCRFAudit.newValue eq '3'}">private</c:if>
                                     <c:if test="${eventCRFAudit.newValue eq '4'}">pending</c:if>
                                     <c:if test="${eventCRFAudit.newValue eq '5'}">removed</c:if>
                                     <c:if test="${eventCRFAudit.newValue eq '6'}">locked</c:if>
                                     <c:if test="${eventCRFAudit.newValue eq '7'}">auto-removed</c:if>
                                     </c:when>
                                 <c:otherwise>
                                    <c:out value="${eventCRFAudit.newValue}"/>
                                 </c:otherwise>
                             </c:choose>
						 &nbsp;</td>
						</tr>														
					</c:if>
				</c:forEach>
				</table><%-- Event CRFs Audit Events --%>
			</td></tr></table><%-- Margin --%>
		 </td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>	
		</c:forEach>
	</table>
	<br>
</c:forEach> 

<hr>
</body>