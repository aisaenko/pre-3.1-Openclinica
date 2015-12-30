<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/extract-header.jsp"/>


<jsp:include page="../include/sidebar.jsp"/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="dataset" class="org.akaza.openclinica.bean.extract.DatasetBean"/>

<h1><span class="title_manage"><fmt:message key="restore_dataset" bundle="${resword}"/>: <c:out value="${dataset.name}"/></span></h1>

<P><jsp:include page="../showInfo.jsp"/></P>



<P><span class="table_title"><fmt:message key="please_review_the_dataset_properties_below" bundle="${restext}"/></span></P>


<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${dataset.description}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="owner" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${dataset.owner.name}"/>
  </td></tr> 
 
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_created" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${dataset.createdDate}" pattern="${dteFormat}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_last_updated" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${dataset.updatedDate}" pattern="${dteFormat}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="status" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${dataset.status.name}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_last_run" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${dataset.dateLastRun}" pattern="${dteFormat}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="number_of_runs" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${dataset.numRuns}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="longest_run_time" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${dataset.runTime}"/>
  </td></tr>

</table>

<br>



<form action="RestoreDataset" method="post">

	<input type="hidden" name="dsId" value="<c:out value="${dataset.id}"/>"/>
	
	
	<input type="submit" name="action" value="<fmt:message key="restore_this_dataset" bundle="${resword}"/>" class="button_xlong"/>
	<input type="submit" name="action" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium"/>

</form>
<br><br>
<jsp:include page="../include/footer.jsp"/>
