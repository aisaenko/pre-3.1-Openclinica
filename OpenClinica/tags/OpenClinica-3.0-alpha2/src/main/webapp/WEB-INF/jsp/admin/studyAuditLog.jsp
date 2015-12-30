<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>


<jsp:include page="../include/managestudy-header.jsp"/>


<jsp:include page="../include/sidebar.jsp"/>

<link rel="stylesheet" href="includes/jmesa/jmesa.css" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js"></script>


<script type="text/javascript">
    function onInvokeAction(id,action) {
        if(id.indexOf('studyAuditLogs') == -1)  {
        setExportToLimit(id, '');
        }
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        location.href = '${pageContext.request.contextPath}/StudyAuditLog?' + parameterString;
    }
</script>


<h1><span class="title_manage">
<fmt:message key="view_study_log_for" bundle="${resword}"/> <c:out value="${study.name}"/>
</span></h1>

<jsp:include page="../include/alertbox.jsp" />
<div id="findSubjectsDiv">
    <form  action="${pageContext.request.contextPath}/StudyAuditLog">
        <input type="hidden" name="module" value="submit">
        ${auditLogsHtml}
    </form>
</div>


<jsp:include page="../include/footer.jsp"/>
