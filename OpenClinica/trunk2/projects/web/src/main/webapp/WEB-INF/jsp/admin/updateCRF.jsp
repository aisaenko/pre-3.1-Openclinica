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

<h1><span class="title_manage"><fmt:message key="update_CRF_details" bundle="${resword}"/> </span></h1>

<P>* <fmt:message key="indicates_required_field" bundle="${resword}"/></P>
<form action="UpdateCRF" method="post">
<input type="hidden" name="action" value="confirm">
<table width="500" cellpadding="0" border="0" class="shaded_table table_first_column_w30 ">
  
  <tr valign="top"><td class="formlabel"><fmt:message key="name" bundle="${resword}"/>:</td>
   <td><div>
    <input type="text" name="name" value="<c:out value="${crf.name}"/>" class="formfieldXL"><span class="indicates_required_field">*</span></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include>
   </tr>
  <tr valign="top"><td class="formlabel"><fmt:message key="description" bundle="${resword}"/>:</td>
  <td>
  <div>
  <textarea class="formtextareaXL4" name="description" rows="4" cols="50"><c:out value="${crf.description}"/></textarea>
  </div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include></td>
  </tr>

</table>

 <input type="submit" name="Submit" value="<fmt:message key="confirm" bundle="${resword}"/>" class="button_medium">
 <input type="button" onclick="confirmCancel('ListCRF');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
</form>

<jsp:include page="../include/footer.jsp"/>
