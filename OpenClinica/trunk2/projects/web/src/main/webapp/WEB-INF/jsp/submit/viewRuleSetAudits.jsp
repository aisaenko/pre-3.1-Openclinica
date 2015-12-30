<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>


<c:import url="../include/managestudy-header.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

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

<jsp:useBean scope='request' id='ruleSet' class='org.akaza.openclinica.domain.rule.RuleSetBean'/>

<h1><span class="title_manage"><fmt:message key="rule_audit_title" bundle="${resword}"/></span></h1>

<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
  <tr valign="top">
  <td class="table_header_column"><fmt:message key="rule_expression" bundle="${resword}"/>:</td>
  <td class="table_cell">
  <c:out value="${ruleSet.target.value}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column_top"><fmt:message key="rule_study_event_definition" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${ruleSet.studyEventDefinitionNameWithOID}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="CRF_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${ruleSet.crfWithVersionNameWithOid}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="rule_group_label" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${ruleSet.groupLabelWithOid}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="rule_item_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${ruleSet.itemNameWithOid}"/>
  </td></tr>
</table>

<br>
<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
   <tr valign="top">
    <th><fmt:message key="rule_status" bundle="${resword}"/></th>
    <th><fmt:message key="rule_audit_updater" bundle="${resword}"/></th>
    <th><fmt:message key="rule_audit_update_date" bundle="${resword}"/></th>
  </tr>
  <c:forEach var ="audit" items="${ruleSetAudits}">
    <tr valign="top">
    <td class="table_cell_left"><c:out value="${audit.status.name}"/></td>
    <td class="table_cell"><c:out value="${audit.updater.name}"/></td>
    <td class="table_cell <c:out value='${className}'/>"><fmt:formatDate value="${audit.currentUpdatedDate}" pattern="${dteFormat}" /></td>
    </tr>
  </c:forEach>

</table>

<br>

<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
  <tr valign="top">
    <th><fmt:message key="rule_name" bundle="${resword}"/></th>
    <th><fmt:message key="rule_status" bundle="${resword}"/></th>
    <th><fmt:message key="rule_audit_updater" bundle="${resword}"/></th>
    <th><fmt:message key="rule_audit_update_date" bundle="${resword}"/></th>
  </tr>
  <c:forEach var ="audit" items="${ruleSetRuleAudits}">
    <tr valign="top">
    <td class="table_cell_left"><c:out value="${audit.ruleSetRuleBean.ruleBean.name}"/></td>
    <td class="table_cell"><c:out value="${audit.status.name}"/></td>
    <td class="table_cell"><c:out value="${audit.updater.name}"/></td>
    <td class="table_cell <c:out value='${className}'/>"><fmt:formatDate value="${audit.currentUpdatedDate}" pattern="${dteFormat}" /></td>
    </tr>
  </c:forEach>

</table>
<br>
<p><a   class="go_back_to_" href="ViewRuleSet?ruleSetId=${ruleSet.id}"/><fmt:message key="rule_go_back_to_Manage_rules" bundle="${resword}"/></a></p>
<br><br>
 
<jsp:include page="../include/footer.jsp"/>