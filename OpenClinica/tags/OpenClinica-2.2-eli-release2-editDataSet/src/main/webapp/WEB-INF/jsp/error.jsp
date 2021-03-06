<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope="session" id="passwordExpired" class="java.lang.String"/>

<c:choose>
	<c:when test="${userBean != null && userRole != null && userRole.role.name != 'invalid' && passwordExpired == 'no'}">
		<jsp:include page="include/home-header.jsp"/>
		<jsp:include page="include/breadcrumb.jsp"/>
		<jsp:include page="include/userbox.jsp"/>
		<jsp:include page="include/sidebar.jsp"/>
	</c:when>
	<c:otherwise>
		<jsp:include page="login-include/login-header.jsp"/>
		<jsp:include page="include/breadcrumb.jsp"/>
		<jsp:include page="include/userbox-inactive.jsp"/>
		<table border="0" cellpadding=0" cellspacing="0">
			<tr><td class="sidebar" valign="top"><br><b><a href="Logout">Logout</a></b></br></td>
				<td class="content" valign="top">
	</c:otherwise>
</c:choose>

<h1><fmt:message key="an_error_has_ocurred" bundle="${resword}"/></h1>

<font class="bodytext">
<fmt:message key="error_page" bundle="${resword}">
	<fmt:param><%=request.getHeader("Referer")%></fmt:param>
</fmt:message>

</font>
</td></tr></table>

<c:choose>
	<c:when test="${userBean != null && userRole != null && userRole.role.name != 'invalid' && passwordExpired == 'no'}">
		<jsp:include page="include/footer.jsp"/>
	</c:when>
	<c:otherwise>
		<jsp:include page="login-include/login-footer.jsp"/>
	</c:otherwise>
</c:choose>
