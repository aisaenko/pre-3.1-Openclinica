<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<link rel="stylesheet" href="includes/jmesa/jmesa.css" type="text/css">
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

<script language="JavaScript" type="text/JavaScript">
    function showOrHideSubjects(count,showLink,hideLink){
        if ($("tr"+count).style.display == ''){
        $("tr"+count).style.display='none';
        $("a"+count).innerHTML= showLink ;
        }else{
        $("tr"+count).style.display='';
        $("a"+count).innerHTML= hideLink ;
        }
    }

</script>

<jsp:useBean scope='request' id='ruleSet' class='org.akaza.openclinica.domain.rule.RuleSetBean'/>
<jsp:useBean scope='request' id='result' class='java.util.HashMap'/>

<h1><span class="title_manage"><fmt:message key="rule_execute_crf_rule_title" bundle="${resword}"/></span></h1>

<c:set var="count" value="0"/>

<c:forEach items="${result}" varStatus="status">
<table class="shaded_table table_first_column_w30 cell_borders fcolumn" cellpadding="0" border="0">
  <tr valign="top"><td class="table_header_column_top"><fmt:message key="rule_crf_version" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${status.current.key.crfVersion}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="rule_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${status.current.key.ruleName}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="rule_result" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${status.current.key.result}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="rule_actions" bundle="${resword}"/>:</td><td class="table_cell">
  <c:forEach items="${status.current.key.actions}" var="action">
    <c:out value="${action.actionType}"/> : <c:out value="${action.summary}"/><br>
  </c:forEach>
  </td></tr>
 
</table>

<br>

<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
  <tr valign="top">
    <th><fmt:message key="rule_study_event_definition" bundle="${resword}"/></th>
    <th><fmt:message key="rule_group_label" bundle="${resword}"/></th>
    <th><fmt:message key="rule_item_name" bundle="${resword}"/></th>
    <th><fmt:message key="action" bundle="${resword}"/></th>
    </tr>
  <c:forEach items="${status.current.value}" varStatus="status1">
    <c:set var="count" value="${count+1}"/>
    <tr valign="top">
    <td  class="table_cell_left"><c:out value="${status1.current.key.studyEventDefinitionName}"/></td>
    <td  class="table_cell">${status1.current.key.itemGroupName}</td>
    <td class="table_cell">${status1.current.key.itemName}</td>

    <td class="table_cell"><span id="a${count}" style="color: BLUE; text-decoration: underline;" onClick="showOrHideSubjects(${count},'<fmt:message key="rule_show_subjects" bundle="${resword}" />','<fmt:message key="rule_hide_subjects" bundle="${resword}" />')"><u><fmt:message key="rule_show_subjects" bundle="${resword}" /></u></span></td>

    </tr>
    <tr id="tr${count}" style="display: none;">
        <td class="table_cell" colspan="7">
        <span style="color:#D4A718;font-weight:bold;font-size:12px;"><fmt:message key="rule_subjects" bundle="${resword}" /></span><br/>
        <c:forEach var ="subject" items="${status1.current.value}">
            <c:out value="${subject}"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </c:forEach></td>
    </tr>
    </c:forEach>

</table>

</c:forEach>
<br>
<p><fmt:message key="rule_execute_rule_bottom_message" bundle="${resword}"/></p>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
<input type="button" name="Submit" id="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium" onClick="window.location.href='RunRule?${submitLinkParams}';"/></td>
</td>
<td>
<input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" onClick="window.location.href='ViewRuleAssignment';"/></td>
</tr></table>
<br><br>
 
<jsp:include page="../include/footer.jsp"/>