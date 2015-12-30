<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:import url="../include/managestudy-header.jsp"/>

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

<jsp:useBean scope='session' id='crf' class='org.akaza.openclinica.bean.admin.CRFBean'/>

<h1><span class="title_manage"><fmt:message key="confirm_CRF_details" bundle="${resword}"/> </span></h1>

<form action="UpdateCRF" method="post">
<input type="hidden" name="action" value="submit">
<table width="500" cellpadding="0" border="0" class="shaded_table table_first_column_w30 fcolumn cell_borders  ">
  <tr valign="top" ><td class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td>
   <td class="table_cell">
    <c:out value="${crf.name}"/>
   </td>
   </tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td>
   <td class="table_cell">
   <c:out value="${crf.description}"/>
  </td>
  </tr>

</table>



<input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium">

<input type="button" onclick="confirmCancel('ListCRF');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>

</form>

<jsp:include page="../include/footer.jsp"/>
