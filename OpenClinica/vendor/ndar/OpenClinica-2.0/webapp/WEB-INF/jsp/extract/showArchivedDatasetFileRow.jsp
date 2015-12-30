<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.extract.ArchivedDatasetFileRow" />

<tr>
	<td class="table_cell_left">
		<a target="_new" href="AccessFile?fileId=<c:out value="${currRow.bean.id}"/>">
			<c:out value="${currRow.bean.name}" />
		</a>
	</td>
	<td class="table_cell"><c:out value="${currRow.bean.runTime}" /></td>
	<td class="table_cell"><c:out value="${currRow.bean.fileSize}" /></td>
	<td class="table_cell"><fmt:formatDate value="${currRow.bean.dateCreated}" pattern="MM/dd/yyyy"/></td>
	<td class="table_cell"><c:out value="${currRow.bean.owner.name}" /></td>
</tr>