<%@ page contentType="text/xml; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:out value="<OpenClinica>" escapeXml="false" />
<c:out value="<changeLogCount>${databaseChangeLogCount}</changeLogCount>"  escapeXml="false"/>
<c:out value="<applicationStatus>${applicationStatus}</applicationStatus>" escapeXml="false"/>
<c:out value="</OpenClinica>" escapeXml="false"/>