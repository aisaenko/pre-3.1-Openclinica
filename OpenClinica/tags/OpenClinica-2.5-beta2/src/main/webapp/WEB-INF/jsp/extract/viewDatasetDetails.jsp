<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/> 


<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
		

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>

<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="dataset" class="org.akaza.openclinica.bean.extract.DatasetBean"/>

<h1><span class="title_extract"><fmt:message key="view_dataset" bundle="${resword}"/>: <c:out value="${dataset.name}"/></span></h1>

<div style="width:600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">   
  <tr valign="top"><td class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${dataset.name}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${dataset.description}"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="owner" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${dataset.owner.name}"/>
  </td></tr> 
 
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_created" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${dataset.createdDate}" dateStyle="short"/>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_last_updated" bundle="${resword}"/>:</td>
  <td class="table_cell"><fmt:formatDate value="${dataset.updatedDate}" dateStyle="short"/>&nbsp;</td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="status" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${dataset.status.name}"/>
  </td></tr>
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br><br>
<jsp:include page="selected-inactive.jsp"></jsp:include>

<table border="0">
<tr> 
  <td>
    <form action="EditDataset">
      <input type="hidden" name="dsId" value="<c:out value="${dataset.id}"/>"/>
      <input type="submit" value="<fmt:message key="edit_dataset" bundle="${resword}"/>" class="button_xlong"/><br>
    </form>
  </td>
  <td>
   <form action="ExportDataset">
    <input type="hidden" name="datasetId" value="<c:out value="${dataset.id}"/>"/>
    <input type="submit" value="<fmt:message key="export_this_dataset" bundle="${resword}"/>" class="button_xlong"/><br>
   </form>
  </td>
</tr>
</table>
<c:import url="../include/workflow.jsp">
   <c:param name="module" value="extract"/> 
</c:import>
<jsp:include page="../include/footer.jsp"/>
