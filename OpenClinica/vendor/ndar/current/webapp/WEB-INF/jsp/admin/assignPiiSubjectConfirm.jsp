<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/subjectMgmt-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope="request" id="newStudy" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<h1><span class="title_manage">
Confirm Add Subject to Study
</span></h1>

<jsp:include page="../include/alertbox.jsp" />
<form action="AssignPiiSubject" method="post">
<input type="hidden" name="action" value="submit">
<input type="hidden" name="id" value="<c:out value="${subject.id}"/>">
<input type="hidden" name="studyId" value="<c:out value="${newStudy.id}"/>">
<p>You choose to add subject: <c:out value="${subject.uniqueIdentifier}"/> to study <c:out value="${newStudy.name}"/>.</p>
<br>
<input type="submit" name="Submit" value="Submit" class="button_medium"></td></tr>

</form>
<jsp:include page="../include/footer.jsp"/>
