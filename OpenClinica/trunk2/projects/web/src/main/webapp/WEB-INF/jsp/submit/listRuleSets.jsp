<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:import url="../include/managestudy-header.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
		<div class="sidebar_tab_content">
        <fmt:message key="manage_execute_rule_assignments" bundle="${restext}"/>
		</div>
		</td>
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.domain.EntityBeanTable'/>

<h1><span class="title_manage"><fmt:message key="rule_manage_rule_assignment" bundle="${resworkflow}"/> <c:out value="${study.name}" /> <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/rules')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>

</span></h1>

<p></p>
<c:import url="../include/showTableNewDomain.jsp"><c:param name="rowURL" value="showRuleSetRow.jsp" /></c:import>
<br>

<div class="homebox_bullets"><a href="pages/studymodule"><fmt:message key="go_back_build_study_page" bundle="${resword}"/></a></div>

<br><br>
<jsp:include page="../include/footer.jsp"/>
