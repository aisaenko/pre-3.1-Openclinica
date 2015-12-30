<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="toc" class="org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean" />

<jsp:include page="../include/submit-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<h1><span class="title_submit">Mark Event CRF Complete: <c:out value="${toc.crf.name}"/></span></h1>

<c:import url="instructionsEnterData.jsp">
	<c:param name="currStep" value="markComplete" />
</c:import>


<jsp:include page="../include/alertbox.jsp" />

<form method="POST" action="MarkEventCRFComplete">
<jsp:include page="../include/showSubmitted.jsp" />
<input type="hidden" name="eventCRFId" value="<c:out value="${toc.eventCRF.id}"/>" />

<p>Marking this CRF complete will finalize data entry. You will no longer be able to add or modify data unless the CRF is reset by an administrator.<br>
<p>If Double Data Entry is required, you or another user may need to complete this CRF again before it is verified as complete.</p>
<p>Are you sure you want to mark this CRF complete?</p>
		<input type="submit" value="Yes" name="markComplete" class="button_medium" />
		<input type="submit" value="No" name="markComplete" class="button_medium" />
</form>

<jsp:include page="../include/footer.jsp"/>