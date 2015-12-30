<%-- proper format: usually called from a showAuditRow.jsp page:
	<c:import url="../include/showAuditEntityLink.jsp">
			<c:param name="auditTable" value="${currRow.bean.auditTable}" />
			<c:param name="entityId" value="${currRow.bean.entityId}" />
			<c:param name="rolename" value="${userRole.role.name}"/>
	</c:import>
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:set var="auditTable" value="${param.auditTable}" />
<c:set var="entityId" value="${param.entityId}" />
<c:choose>
	<c:when test='${(auditTable != null)}'>
		<!-- not blank form here -->
		<c:if test="${auditTable == 'Subject'}">
		<!-- subject here -->
		<a href="ViewSubject?id=<c:out value="${entityId}"/>"
	  onMouseDown="setImage('bt_View1','images/bt_View_d.gif');"
	  onMouseUp="setImage('bt_View1','images/bt_View.gif');"><img 
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="View Subject" title="View Subject" align="left" hspace="6"></a>
		</c:if>
		<c:if test="${auditTable == 'Study Subject'}">
		<!-- study subject here -->
		
		</c:if>
		<c:if test="${auditTable == 'User Account'}">
		<!-- user account here -->
			<c:if test="${userBean.sysAdmin}">  
		<a href="ViewUserAccount?userId=<c:out value="${entityId}"/>"
	  onMouseDown="setImage('bt_View1','images/bt_View_d.gif');"
	  onMouseUp="setImage('bt_View1','images/bt_View.gif');"><img 
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="View User Account" title="View User Account" align="left" hspace="6"></a>
			  </c:if>
		</c:if>
	</c:when>
	<c:otherwise>
		<!-- blank form here -->
	</c:otherwise>
</c:choose>