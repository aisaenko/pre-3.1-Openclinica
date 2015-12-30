<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />

<jsp:include page="include/home-header.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="include/sideAlert.jsp"/>


<link rel="stylesheet" href="includes/jmesa/jmesa.css" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa-original.js"></script>
<script type="text/javascript" language="JavaScript" src="includes/jmesa/jquery.blockUI.js"></script>
<style type="text/css">

        .graph {
        position: relative; /* IE is dumb */
        width: 100px;
        border: 1px solid #3876C1;
        padding: 2px;
        }
        .graph .bar {
        display: block;
        position: relative;
        background: #FFFF00;
        text-align: center;
        color: #333;
        height: 1em;
        line-height: 1em;
        }
        .graph .bar span { position: absolute; left: 1em; }
</style>

<!-- then instructions-->
<div id="box" class="dialog">
<span id="mbm">
    <br>
     <fmt:message key="study_frozen_locked_note" bundle="${restext}"/>
   </span><br>
    <div style="text-align:center; width:100%;">
        <button onclick="hm('box');">OK</button>
    </div>
</div>
<tr id="sidebar_Instructions_open" style="display: all">
        <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

        <div class="sidebar_tab_content">
        <fmt:message key="may_change_request_access" bundle="${restext}"/>
        </div>

        </td>

    </tr>
    <tr id="sidebar_Instructions_closed" style="display: none">
        <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

        </td>
  </tr>

<jsp:include page="include/sideInfo.jsp"/>


<h1> <span class="title_manage"><fmt:message key="description" bundle="${restext}"/></span></h1>

<c:set var="roleName" value=""/>
<c:if test="${userRole != null && !userRole.invalid}">
<c:set var="roleName" value="${userRole.role.name}"/>

<c:set var="linkStudy">
<c:choose>
   <c:when test="${study.parentStudyId>0}">
     <a href="ViewSite?id=<c:out value="${study.id}"/>">
   </c:when>
   <c:otherwise>
     <a href="ViewStudy?id=<c:out value="${study.id}"/>&viewFull=yes">
   </c:otherwise>
</c:choose>
<c:out value="${study.name}"/></a></span>
</c:set>

<c:set var="studyidentifier">
   <span class="alert"><c:out value="${study.identifier}"/></span>
</c:set>
<p>
<fmt:message key="as_a_for_the_study_and_protocol_ID_may_access_modules_and_perform" bundle="${restext}">
    <fmt:param value="${userRole.role.description}"/>
    <fmt:param value="${linkStudy}"/>
    <fmt:param value="${studyidentifier}"/>
</fmt:message>
</p>

</c:if>

<a href="ViewNotes?module=submit">Notes & Discrepancies Assigned to Me (${assignedDiscrepancies})</a><br /><br /><br />

<c:if test="${userRole.investigator || userRole.researchAssistant}">

<div id="findSubjectsDiv">
    <script type="text/javascript">
    function onInvokeAction(id,action) {
        if(id.indexOf('findSubjects') == -1)  {
        setExportToLimit(id, '');
        }
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        location.href = '${pageContext.request.contextPath}/MainMenu?'+ parameterString;
    }
    jQuery(document).ready(function() {
        jQuery('#addSubject').click(function() {
			jQuery.blockUI({ message: jQuery('#addSubjectForm'), css:{left: "300px", top:"10px" } });
        });

        jQuery('#cancel').click(function() {
            jQuery.unblockUI();
            return false;
        });
    });
    </script>
    <form  action="${pageContext.request.contextPath}/ListStudySubjects">
        <input type="hidden" name="module" value="admin">
        ${findSubjectsHtml}
    </form>
</div>
    <div id="addSubjectForm" style="display:none;">
          <c:import url="submit/addNewSubjectExpressNew.jsp">
          </c:import>
    </div>


</c:if>

<c:if test="${userRole.coordinator || userRole.director}">


    <script type="text/javascript">
    function onInvokeAction(id,action) {
        if(id.indexOf('studySiteStatistics') == -1)  {
            setExportToLimit(id, '');
        }
        if(id.indexOf('subjectEventStatusStatistics') == -1)  {
            setExportToLimit(id, '');
        }
        if(id.indexOf('studySubjectStatusStatistics') == -1)  {
            setExportToLimit(id, '');
        }
        createHiddenInputFieldsForLimitAndSubmit(id);
    }

    </script>

<p>
<table>
<tr>
    <td valign="top">
    <form  action="${pageContext.request.contextPath}/MainMenu">
        ${studySiteStatistics}
    </form>
    </td>
    <td valign="top">
    <form  action="${pageContext.request.contextPath}/MainMenu">
        ${studyStatistics}
    </form>
    </td>
</tr>
</table>
</p>
<p>
<table>
<tr>
    <td valign="top">
    <form  action="${pageContext.request.contextPath}/MainMenu">
        ${subjectEventStatusStatistics}
    </form>
    </td>

    <td valign="top">
    <form  action="${pageContext.request.contextPath}/MainMenu">
        ${studySubjectStatusStatistics}
    </form>
    </td>
</tr>
</table>
</p>
</c:if>

<c:if test="${userRole.monitor}">


<script type="text/javascript">
    function onInvokeAction(id,action) {
        setExportToLimit(id, '');
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
    }
</script>
<div id="subjectSDV">
    <form name='sdvForm' action="${pageContext.request.contextPath}/pages/viewAllSubjectSDVtmp">
        <input type="hidden" name="studyId" value="${study.id}">
        <input type="hidden" name=imagePathPrefix value="">
        <%--This value will be set by an onclick handler associated with an SDV button --%>
        <input type="hidden" name="crfId" value="0">
        <%-- the destination JSP page after removal or adding SDV for an eventCRF --%>
        <input type="hidden" name="redirection" value="viewAllSubjectSDVtmp">
        <%--<input type="hidden" name="decorator" value="mydecorator">--%>
        ${sdvMatrix}
        <br />
        <input type="submit" name="sdvAllFormSubmit" class="button_medium" value="Submit" onclick="this.form.method='POST';this.form.action='${pageContext.request.contextPath}/pages/handleSDVPost';this.form.submit();"/>
        <input type="submit" name="sdvAllFormCancel" class="button_medium" value="Cancel" onclick="this.form.action='${pageContext.request.contextPath}/pages/viewAllSubjectSDVtmp';this.form.submit();"/>
    </form>

</div>
</c:if>


<jsp:include page="include/footer.jsp"/>
