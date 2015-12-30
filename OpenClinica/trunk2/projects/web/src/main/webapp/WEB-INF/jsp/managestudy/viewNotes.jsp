<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<c:import url="../include/managestudy-header.jsp"/>

<link rel="stylesheet" href="includes/jmesa/jmesa.css" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js"></script>

<script type="text/javascript">
    function onInvokeAction(id,action) {
        if(id.indexOf('listNotes') == -1)  {
        setExportToLimit(id, '');
        }
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        location.href = '${pageContext.request.contextPath}/ViewNotes?'+ parameterString;
    }
    function openPopup() {
        openDocWindow(window.location.href +'&print=yes')
    }
</script>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
			<fmt:message key="this_section_allows_a_study_manager_to_view_and_resolve" bundle="${restext}"/>
		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.bean.EntityBeanTable'/>
<jsp:useBean scope='request' id='message' class='java.lang.String'/>

<h1><span class="title_manage">	<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>
    <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/openclinica-user-guide/monitor-and-manage-data/notes-and-discrepancies')">
     </a></span></h1>


<div><a class="anchor_page_section_oc" id="sumBoxParent" href="javascript:void(0)"
        onclick="showSummaryBox('sumBox',document.getElementById('sumBoxParent'),
        '<fmt:message key="show_summary_statistics" bundle="${resword}"/>',
        '<fmt:message key="hide_summary_statistics" bundle="${resword}"/>')">
    <img name="ExpandGroup1" src="images/bt_Collapse.gif" border="0">
    <fmt:message key="hide_summary_statistics" bundle="${resword}"/></a>
</div>
<div id="sumBox" style="display:block; width:600px;">
    <%--<h3>Summary statistics</h3>--%>
    <c:if test="${empty summaryMap}"><fmt:message key="There_are_no_discrepancy_notes" bundle="${resword}"/></c:if>
    <!-- NEW Summary-->
    <table cellspacing="0" class="summaryTable" style="width:600px;">
        <tr><td>&nbsp;</td>
            <c:forEach var="typeName"  items="${typeNames}">
                <td align="center"><strong>${typeName}</strong></td>
            </c:forEach>
            <td align="center"><strong>Total</strong></td>
        </tr>
            <c:forEach var="status" items="${mapKeys}">
                <tr>
                    <td><strong>${status.name}</strong><img src="${status.iconFilePath}" border="0" align="right"></td>
                    <c:forEach var="typeName" items="${typeNames}">
                        <td align="center">${summaryMap[status.name][typeName]}</td>
                    </c:forEach>
                    <td align="center"> ${summaryMap[status.name]['Total']}</td>
                </tr>
            </c:forEach>
        <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
        <tr><td><strong>Total</strong></td>
            <c:forEach var="typeName"  items="${typeNames}">
                <td align="center">${typeKeys[typeName]}</td>
            </c:forEach>
            <td align="center">${grandTotal}</td>
        </tr>
    </table>
    <!-- End Of New Summary -->
</div>

<form  action="${pageContext.request.contextPath}/ViewNotes" style="clear:left; float:left;">
        <input type="hidden" name="module" value="submit">
        ${viewNotesHtml}
    </form>

<br><br>
<jsp:include page="../include/footer.jsp"/>
