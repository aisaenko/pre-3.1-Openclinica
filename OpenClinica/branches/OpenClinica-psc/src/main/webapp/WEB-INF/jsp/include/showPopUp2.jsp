<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean scope='request' id='popUpURL' class='java.lang.String'/>

<c:choose>
	<c:when test='${popUpURL != ""}'>
		javascript:window.open('<c:out value="${popUpURL}" />', '_blank', 'width=400,height=350,scrollbars=yes,resizable=yes');
	</c:when>
	<c:otherwise>
	</c:otherwise>
</c:choose>