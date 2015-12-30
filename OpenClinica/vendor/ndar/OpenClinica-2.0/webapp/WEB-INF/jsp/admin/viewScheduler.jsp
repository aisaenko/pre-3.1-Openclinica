<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/tech-admin-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>


<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>

<h1><span class="title_Admin">
View Scheduled Tasks
</span></h1>
<a href="ViewScheduler?action=create">Click here to start up the automated view refresh.</a>

<jsp:include page="../include/alertbox.jsp" />



<jsp:include page="../include/footer.jsp"/>