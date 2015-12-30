<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterms"/>

 <c:import url="../include/managestudy-header.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
		<div class="sidebar_tab_content">
			<fmt:message key="import_side_bar_instructions" bundle="${restext}"/>
		</div>
	</td>
</tr>

<tr id="sidebar_Instructions_closed" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
	</td>
</tr>



<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='session' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>
<jsp:useBean scope='session' id='crfName' class='java.lang.String'/>

 <c:out value="${crfName}"/>

<h1><span class="title_manage"><fmt:message key="import_crf_data" bundle="${resworkflow}"/>
<a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/openclinica-user-guide/submit-data-module-overview/import-data')">
    <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>">
</a></h1>
<p><fmt:message key="import_instructions" bundle="${restext}"/></p>



<form action="ImportCRFData?action=confirm&crfId=<c:out value="${version.crfId}"/>&name=<c:out value="${version.name}"/>" method="post" ENCTYPE="multipart/form-data">
<table  cellpadding="0" border="0" class="shaded_table table_first_column_w30 ">

<tr>
	<td class="formlabel"><fmt:message key="xml_file_to_upload" bundle="${resterms}"/>:</td>
	<td>
		<div ><input type="file" name="xml_file" > </div>
		<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="xml_file"/></jsp:include>
	</td>
</tr>
<input type="hidden" name="crfId" value="<c:out value="${version.crfId}"/>">


</table>
 

<br clear="all">
<input type="submit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_medium">
<input type="button" onclick="goBack()"  name="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium"/>

</form>
<br><br>

<jsp:include page="../include/footer.jsp"/>