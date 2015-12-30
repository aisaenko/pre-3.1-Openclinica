<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<jsp:useBean scope='request' id='popUpURL' class='java.lang.String'/>

<c:choose>
	<c:when test='${popUpURL != ""}'>
		onLoad="window.open('<c:out value="${popUpURL}" />', '_blank', 'width=400,height=350,scrollbars=yes,resizable=yes');"
	</c:when>
	<c:otherwise>
	</c:otherwise>
</c:choose>