<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/submit-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope="request" id="displayEntities" type="java.util.List" />
<jsp:useBean scope="request" id="browseBy" class="java.lang.String" />
<jsp:useBean scope="request" id="pageNum" type="java.lang.Integer" />
<jsp:useBean scope="request" id="displayNextPage" class="java.lang.String" />

<c:choose>
<c:when test='${browseBy == "Subject"}'>
	<c:set var="browseByEnglish" value="Subject" />
</c:when>
<c:otherwise>
	<c:set var="browseByEnglish" value="Study Event Definition" />
</c:otherwise>
</c:choose>

<c:set var="prevPageNum" value="${pageNum - 1}" />
<c:set var="nextPageNum" value="${pageNum + 1}" />


<p class="breadcrumb">
	<a href="MainMenu">OpenClinica Home</a> >
	<a href="SubmitData">Submit Data Home</a>
</p>

<p class="title">
Find an Existing Study Event by <c:out value="${browseByEnglish}" />
</p>

<jsp:include page="../include/alertbox.jsp" />

<p>
<c:choose>
	<c:when test="${empty displayEntities}">
		There are no <c:out value="${browseByEnglish}" />s with Study Events in this Study.
	</c:when>
	<c:otherwise>
		Displaying <c:out value="${browseByEnglish}" />s with Study Events in this Study:
		<ul>
		<c:forEach var="entity" items="${displayEntities}">
			<li> <a href="FindStudyEvent?browseBy=<c:out value="${browseBy}"/>&id=<c:out value="${entity.id}"/>"><c:out value="${entity.name}" /></a>
		</c:forEach>
		</ul>
		<p>
		<c:if test="${pageNum gt 0}">
			<a href="FindStudyEvent?browseBy=<c:out value="${browseBy}"/>&pageNum=<c:out value="${prevPageNum}" />">&lt;&lt; Previous Page</a>
		</c:if>
		<c:if test='${displayNextPage == "yes"}'>
			<a href="FindStudyEvent?browseBy=<c:out value="${browseBy}"/>&pageNum=<c:out value="${nextPageNum}" />">Next Page &gt;&gt;</a>		
		</c:if>
	</c:otherwise>
</c:choose>

<jsp:include page="../include/footer.jsp"/>