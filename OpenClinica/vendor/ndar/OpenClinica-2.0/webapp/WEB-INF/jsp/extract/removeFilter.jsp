<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="filter" class="org.akaza.openclinica.bean.extract.FilterBean"/>

<h1><span class="title_extract">Remove Filter: <c:out value="${filter.name}"/></span></h1>

<P><jsp:include page="../showInfo.jsp"/></P>

<P>Please review the filter properties below and confirm removal.  
Once removed, the filter can be restored by a system administrator.</P>

<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">   
  <tr valign="top"><td class="table_header_column">Description:</td><td class="table_cell">
  <c:out value="${filter.description}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Owner:</td><td class="table_cell">
  <c:out value="${filter.owner.name}"/>
  </td></tr> 
 
  <tr valign="top"><td class="table_header_column">Date Created:</td><td class="table_cell">
  <fmt:formatDate value="${filter.createdDate}" pattern="MM/dd/yyyy"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Date Last Updated:</td><td class="table_cell">
  <fmt:formatDate value="${filter.updatedDate}" pattern="MM/dd/yyyy"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Status:</td><td class="table_cell">
  <c:out value="${filter.status.name}"/>
  </td></tr>
  
  

</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>


<form action="RemoveFilter" method="post">

	<input type="hidden" name="filterId" value="<c:out value="${filter.id}"/>"/>
	
	<input type="submit" name="action" value="Remove this Filter" class="button_xlong"/>
	&nbsp;<input type="submit" name="action" value="Cancel" class="button_xlong"/>

</form>

<%--
<P><a href="CreateFiltersOne">View Filters</a> | 
<a href="CreateFiltersOne?action=begin&submit=Create New Filter">Create New Filter</a></P>
--%>
<jsp:include page="../include/footer.jsp"/>