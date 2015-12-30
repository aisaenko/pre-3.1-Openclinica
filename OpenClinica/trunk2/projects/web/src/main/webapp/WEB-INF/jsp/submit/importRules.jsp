<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="respage"/>

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
			<!--<fmt:message key="import_rule_side_bar_instructions" bundle="${restext}"/>-->
            <fmt:message key="rules_Import_info" bundle="${respage}"/>
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

<h1><span class="title_manage"><fmt:message key="import_rule_data" bundle="${resworkflow}"/> ${study.name}
<a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/study-setup')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></a>
</h1>



<form action="ImportRule?action=confirm" method="post" ENCTYPE="multipart/form-data">
<table  cellpadding="0" border="0" class="shaded_table  table_first_column_w30">

<tr>
	<td class="formlabel"><fmt:message key="xml_file_to_upload" bundle="${resterm}"/>: </td>
	<td>
		<div ><input type="file" name="xml_file" > </div>
		<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="xml_file"/></jsp:include>
	</td>
</tr>
<input type="hidden" name="crfId" value="<c:out value="${version.crfId}"/>">


</table>

<br clear="all">
<input type="submit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_medium">
<p><a href="pages/studymodule"><fmt:message key="import_rules_back_to_study_build" bundle="${resword}"/></a></p>
</form>



<span class="table_title_Admin"><fmt:message key="rule_import_getting_started" bundle="${resterm}"/></span>
<div>&nbsp;</div>
<div class="homebox_bullets"><a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/study-setup')"><fmt:message key="rule_import_rules_documentation" bundle="${resterm}"/></a></div><br/>
 

<span class="table_title_Admin"><fmt:message key="rule_import_templates" bundle="${resterm}"/></span>
<div>&nbsp;</div>
<div class="homebox_bullets"><a href="ImportRule?action=downloadtemplateWithNotes"><fmt:message key="rule_import_all_actions_with_notes" bundle="${resterm}"/></a></div><br/>
<div class="homebox_bullets"><a href="ImportRule?action=downloadtemplate"><fmt:message key="rule_import_all_actions_without_notes" bundle="${resterm}"/></a></div><br/>

<jsp:include page="../include/footer.jsp"/>