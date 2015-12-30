<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/submit-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope="request" id="entityWithStudyEvents" class="org.akaza.openclinica.bean.core.EntityBean" />
<jsp:useBean scope="request" id="displayEntities" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="browseBy" class="java.lang.String" />

<c:choose>
	<c:when test='${browseBy == "Subject"}'>
		<c:set var="browseByEnglish" value="Subject" />
	</c:when>
	<c:otherwise>
		<c:set var="browseByEnglish" value="Study Event Definition" />
	</c:otherwise>
</c:choose>

<p class="breadcrumb">
	<a href="MainMenu">OpenClinica Home</a> >
	<a href="SubmitData">Submit Data Home</a>
</p>

<p class="title">
Find Existing Study Events for <c:out value="${browseByEnglish}" /> <b><c:out value="${entityWithStudyEvents.name}" /></b>
</p>

<jsp:include page="../include/showPageMessages.jsp" />

<p>
<c:choose>
	<c:when test="${empty displayEntities}">
		There are no Study Events for the <b><c:out value="${entityWithStudyEvents.name}" /></b> <c:out value="${browseByEnglish}" />.
	</c:when>
	<c:otherwise>
		Displaying Study Events for the <b><c:out value="${entityWithStudyEvents.name}" /></b> <c:out value="${browseByEnglish}" />:
		<table>
			<tr>
				<td>Location</td>
				<td>Started</td>
				<td>Ended</td>
				<td>Action</td>
			</tr>
		<c:forEach var="event" items="${displayEntities}">
			<tr>
				<td><c:out value="${event.location}" /></td>
				<td><c:out value="${event.dateStarted}" /></td>
				<td><c:out value="${event.dateEnded}" /></td>
				<td><a href="EnterDataForStudyEvent?eventId=<c:out value="${event.id}" />">Enter Data</a></td>
			</tr>
		</c:forEach>
		</table>
		<p>
	</c:otherwise>
</c:choose>

<jsp:include page="../include/footer.jsp"/>