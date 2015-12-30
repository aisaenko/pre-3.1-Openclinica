<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />

<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.core.EntityBeanTable'/>

 <h1><span class="title_manage">View Study Log for <c:out value="${study.name}"/></span></h1>

<jsp:include page="../include/alertbox.jsp" />
<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showAuditEventStudyRow.jsp" /></c:import>

<jsp:include page="../include/footer.jsp"/>