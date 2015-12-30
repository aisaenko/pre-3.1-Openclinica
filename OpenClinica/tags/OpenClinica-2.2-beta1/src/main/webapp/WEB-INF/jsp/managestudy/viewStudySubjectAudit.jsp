<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<html>
<head>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
</head>

<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope="request" id="events" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="eventCRFAudits" class="java.util.ArrayList"/>

<body>
 
 <h1>
  <c:out value="${studySub.label}"/> Audit Logs
 </h1>  


<%-- Subject Summary --%>
<table border="0" cellpadding="0" cellspacing="0" width="650" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">				
	<tr>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Subject ID</b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Secondary Subject ID</b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Date of Birth</b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Person ID</b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Date Record Created</b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Created By</b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Status</b></td>
	</tr>
	<tr>
	 <td class="table_header_column"><c:out value="${studySub.label}"/></td>
	 <td class="table_header_column"><c:out value="${studySub.secondaryLabel}"/></td>
	 <td class="table_header_column"><fmt:formatDate value="${subject.dateOfBirth}" pattern="MM/dd/yyyy"/></td>
	 <td class="table_header_column"><c:out value="${subject.uniqueIdentifier}"/></td>
	 <td class="table_header_column"><fmt:formatDate value="${studySub.createdDate}" pattern="MM/dd/yyyy"/></td>
	 <td class="table_header_column"><c:out value="${studySub.owner.name}"/></td>
	 <td class="table_header_column"><c:out value="${subject.status.name}"/></td>
	 	
	</tr> 
</table><br><br>

<%-- Subject Audit Events --%>
<table border="0" cellpadding="0" cellspacing="0" width="550" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">				
	<tr>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Audit Event</b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Date/Time</b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>User</b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Value Type</b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Old</b></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>New</b></td>
	</tr>
	<c:forEach var="studySubjectAudit" items="${studySubjectAudits}">
	<tr>
	 <td class="table_header_column"><c:out value="${studySubjectAudit.auditEventTypeName}"/>&nbsp;</td>
	 <td class="table_header_column"><fmt:formatDate value="${studySubjectAudit.auditDate}" pattern="MM/dd/yy hh:mm:ss"/>&nbsp;</td>
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
	 <td class="table_header_column_top" style="color: #789EC5"><b>Study Events</b><br></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Location</b><br></td>
	 <td class="table_header_column_top" style="color: #789EC5"><b>Date/Time</b><br></td>
	</tr>
	<c:forEach var="event" items="${events}">
		<tr>
		 <td class="table_header_column"><c:out value="${event.studyEventDefinition.name}"/>&nbsp;</td>
		 <td class="table_header_column"><c:out value="${event.location}"/>&nbsp;</td>
		 <td class="table_header_column"><fmt:formatDate value="${event.dateStarted}" pattern="MM/dd/yy"/>&nbsp;</td>
		</tr>
	</c:forEach> 
</table>
<br>
<c:forEach var="event" items="${events}">
	<%-- Study Event Summary --%>
	<table border="0" cellpadding="0" cellspacing="0" width="550" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">
		<tr>
		 <td class="table_header_column_top" style="color: #789EC5"><b>Name</b></td>
		 <td class="table_header_column_top" style="color: #789EC5"><b><c:out value="${event.studyEventDefinition.name}"/></b>&nbsp;</td>
		</tr>
		<tr>
		 <td class="table_header_column"><c:out value="Location"/></td>
		 <td class="table_header_column"><c:out value="${event.location}"/>&nbsp;</td>
		</tr>
		<tr>
		 <td class="table_header_column"><c:out value="Start Date/Time"/></td>
		 <td class="table_header_column"><fmt:formatDate value="${event.dateStarted}" pattern="MM/dd/yy hh:mm:ss"/>&nbsp;</td>
		</tr>
		<tr>
		 <td class="table_header_column"><c:out value="Status"/></td>
		 <td class="table_header_column"><c:out value="${event.subjectEventStatus.name}"/>&nbsp;</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>
		
		<%-- Event CRFs for this Study Event --%>
		<c:forEach var="eventCRF" items="${event.eventCRFs}">
		<tr>
		 <td colspan="2">
			<table border="0" cellpadding="0" cellspacing="0" width="550" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">				
				<tr>
				 <td class="table_header_column_top" style="color: #789EC5"><b>Name</b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b>Version</b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b>Date Interviewed</b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b>Interviewer Name</b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b>Owner</b></td>
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
				 <td class="table_header_column_top" style="color: #789EC5"><b>Audit Event</b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b>Date/Time</b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b>User</b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b>Value Type</b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b>Old</b></td>
				 <td class="table_header_column_top" style="color: #789EC5"><b>New</b></td>
				</tr>
				
				<c:forEach var="eventCRFAudit" items="${eventCRFAudits}">
					<c:if test="${eventCRFAudit.eventCRFId==eventCRF.id}">
						<tr>
						 <td class="table_header_column"><c:out value="${eventCRFAudit.auditEventTypeName}"/>&nbsp;</td>
						 <td class="table_header_column"><fmt:formatDate value="${eventCRFAudit.auditDate}" pattern="MM/dd/yy hh:mm:ss"/>&nbsp;</td>
						 <td class="table_header_column"><c:out value="${eventCRFAudit.userName}"/>&nbsp;</td>
						 <td class="table_header_column"><c:out value="${eventCRFAudit.entityName}"/>&nbsp;</td> 
						 <td class="table_header_column"><c:out value="${eventCRFAudit.oldValue}"/>&nbsp;</td>
						 <td class="table_header_column"><c:out value="${eventCRFAudit.newValue}"/>&nbsp;</td>
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