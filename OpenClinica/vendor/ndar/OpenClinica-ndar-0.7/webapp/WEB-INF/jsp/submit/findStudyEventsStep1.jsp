<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/submit-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<p class="breadcrumb">
	<a href="MainMenu">OpenClinica Home</a> >
	<a href="SubmitData">Submit Data Home</a>
</p>

<p class="title">
Find an Existing Study Event
</p>

<jsp:include page="../include/alertbox.jsp" />

<p><a href="FindStudyEvent?browseBy=Subject">Browse by Subject</a>

<p><a href="FindStudyEvent?browseBy=StudyEventDefinition">Browse by Study Event Definition</a>

<jsp:include page="../include/footer.jsp"/>