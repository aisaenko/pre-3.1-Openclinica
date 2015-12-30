<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<c:choose>
    <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
        <c:import url="../include/admin-header.jsp"/>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${userRole.manageStudy && module=='manage'}">
                <c:import url="../include/managestudy-header.jsp"/>
            </c:when>
            <c:otherwise>
                <c:import url="../include/submit-header.jsp"/>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

        <div class="sidebar_tab_content">
        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

    </td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="father" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="mother" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="subjectStudy" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="parentStudy" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope="request" id="children" class="java.util.ArrayList"/>
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.core.EntityBeanTable'/>
<jsp:useBean scope="request" id="groups" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="from" class="java.lang.String"/>

<script language="JavaScript">
    <!--
    function leftnavExpand(strLeftNavRowElementName){

        var objLeftNavRowElement;

        objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
        if (objLeftNavRowElement != null) {
            if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
            objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";
        }
    }

    //-->
</script>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td>
        <h1>
            <c:choose>
            <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
            <div class="title_Admin">
                </c:when>
                <c:otherwise>

                <c:choose>
                <c:when test="${userRole.manageStudy}">
                <div class="title_manage">
                    </c:when>
                    <c:otherwise>
                    <div class="title_submit">
                        </c:otherwise>
                        </c:choose>

                        </c:otherwise>
                        </c:choose>
                        <fmt:message key="view_subject2" bundle="${resword}"/><c:out value="${studySub.label}"/>
                    </div>
    </td>
        <td align="right">
            <!-- <span style="font-size:11px"><a href="#"><img
		    src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>"></a>View Printable Record</div>-->
            </h1>
        </td></tr>
</table>

<p>
    <a href="#events"><fmt:message key="events" bundle="${resword}"/></a> &nbsp; &nbsp; &nbsp;
    <a href="#group"><fmt:message key="group" bundle="${resword}"/></a> &nbsp;&nbsp;&nbsp;
    <a href="#global"><fmt:message key="global_subject_record" bundle="${resword}"/></a> &nbsp;&nbsp;&nbsp;
    <a href="javascript:openDocWindow('ViewStudySubjectAuditLog?id=<c:out value="${studySub.id}"/>')"><fmt:message key="audit_logs" bundle="${resword}"/></a>
</p>
<c:choose>
    <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
        <div class="table_title_Admin">
    </c:when>
    <c:otherwise>

        <c:choose>
            <c:when test="${userRole.manageStudy}">
                <div class="table_title_Manage">
            </c:when>
            <c:otherwise>
                <div class="table_title_Submit">
            </c:otherwise>
        </c:choose>

    </c:otherwise>
</c:choose>

<a href="javascript:leftnavExpand('subjectRecord');javascript:setImage('ExpandGroup1','images/bt_Expand.gif');"><img
  name="ExpandGroup1" src="images/bt_Collapse.gif" border="0"> <fmt:message key="subject_record_for" bundle="${restext}"/><c:out value="${studySub.label}"/></a></div>
<%-- removed broken CSS from below element: <div id="subjectRecord" style="display: "> --%>
<div id="subjectRecord">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td valign="top" width="330" style="padding-right: 20px">



<!-- These DIVs define shaded box borders -->

<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">

<table border="0" cellpadding="0" cellspacing="0" width="330">

<!-- Table Actions row (pagination, search, tools) -->

<tr>

    <!-- Table Tools/Actions cell -->

    <td align="right" valign="top" class="table_actions">
        <table border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td class="table_tools">
                    <c:if test="${userRole != null }">
                        <c:set var="roleName" value="${userRole.role.name}"/>
                        <c:if test="${userRole.manageStudy && study.status.available}">
                               <a href="UpdateStudySubject?id=<c:out value="${studySub.id}"/>&action=show"><fmt:message key="edit_record" bundle="${resword}"/></a>
                        </c:if>
                    </c:if></td>
            </tr>
        </table>
    </td>

    <!-- End Table Tools/Actions cell -->
</tr>

<!-- end Table Actions row (pagination, search, tools) -->

<tr>
<td valign="top">

<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
    <td class="table_header_column_top"><fmt:message key="study_subject_ID" bundle="${resword}"/></td>
    <td class="table_cell_top"><c:out value="${studySub.label}"/></td>
</tr>
<tr>
    <td class="table_header_column"><fmt:message key="secondary_label" bundle="${resword}"/></td>
    <td class="table_cell"><c:out value="${studySub.secondaryLabel}"/></td>
</tr>
<tr>
    <td class="table_header_column"><fmt:message key="OID" bundle="${resword}"/></td>
    <td class="table_cell"><c:out value="${studySub.oid}"/></td>
</tr>
<tr>
    <td class="table_divider" colspan="2">&nbsp;</td>
</tr>

<tr>
    <td class="table_header_column_top" style="border-bottom-width:1px;border-bottom-color: #CCCCCC"><fmt:message key="person_ID" bundle="${resword}"/></td>
    <td class="table_cell_top" style="border-bottom-width:1px;border-bottom-color: #CCCCCC"><span style="float:left"><c:out value="${subject.uniqueIdentifier}"/></span>
        <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true'}">
            <c:set var="isNew" value="${hasUniqueIDNote eq 'yes' ? 0 : 1}"/>
             <%-- ViewDiscrepancyNote?writeToDB=1&subjectId=${studySubject.id}&itemId=${itemId}
             &id=${InterviewerDateNote.eventCRFId}&name=${InterviewerDateNote.entityType}&
             field=interviewDate&column=${InterviewerDateNote.column}--%>
            <c:choose>
                <c:when test="${hasUniqueIDNote eq 'yes'}">
                    <span style="float:right"><a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=uniqueIdentifier&column=unique_identifier','spanAlert-uniqueIdentifier'); return false;">
                        <img id="flag_uniqueIdentifier" name="flag_uniqueIdentifier" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                    </a>
                    </span>
                </c:when>
                <c:otherwise>
                    <c:if test="${!study.status.locked}">
                        <span style="float:right">
                            <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=uniqueIdentifier&column=unique_identifier','spanAlert-uniqueIdentifier'); return false;">
                                <img id="flag_uniqueIdentifier" name="flag_uniqueIdentifier" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a></span>
                    </c:if>
                </c:otherwise>
            </c:choose>

        </c:if></td>
</tr>
<c:choose>
    <c:when test="${subjectStudy.studyParameterConfig.collectDob == '1'}">
        <tr>
            <td class="table_header_column_top"><fmt:message key="date_of_birth" bundle="${resword}"/></td>
            <td class="table_cell_top" style="border-bottom-width:1px;border-bottom-color: #CCCCCC"><span style="float:left"><fmt:formatDate value="${subject.dateOfBirth}" pattern="${dteFormat}"/></span>
                <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true'}">
                    <c:set var="isNew" value="${hasDOBNote eq 'yes' ? 0 : 1}"/>

                    <c:choose>
                        <c:when test="${hasDOBNote eq 'yes'}">
                            <span style="float:right"><a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth','spanAlert-dob'); return false;">
                                <img id="flag_dob" name="flag_dob" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>
                            </span>
                        </c:when>
                        <c:otherwise>
                           <c:if test="${!study.status.locked}">
                            <span style="float:right">
                                <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth&new=1','spanAlert-dob'); return false;">
                                    <img id="flag_dob" name="flag_dob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                                </a></span>
                            </c:if>
                        </c:otherwise>
                    </c:choose>

                </c:if></td>
        </tr>
    </c:when>
    <c:when test="${subjectStudy.studyParameterConfig.collectDob == '3'}">
        <tr>
            <td class="table_header_column_top"><fmt:message key="date_of_birth" bundle="${resword}"/></td>
            <td class="table_cell_top"><fmt:message key="not_used" bundle="${resword}"/></td>
        </tr>
    </c:when>
    <c:otherwise>
        <tr>
            <td class="table_header_column_top"><fmt:message key="year_of_birth" bundle="${resword}"/></td>
            <td class="table_cell_top"  style="border-bottom-width:1px;border-bottom-color: #CCCCCC"><span style="float:left"><c:out value="${yearOfBirth}"/></span>

                <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true'}">
                    <c:set var="isNew" value="${hasDOBNote eq 'yes' ? 0 : 1}"/>

                    <c:choose>
                        <c:when test="${hasDOBNote eq 'yes'}">
                            <span style="float:right"><a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth','spanAlert-dob'); return false;">
                                <img id="flag_dob" name="flag_dob" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>
                            </span>
                        </c:when>
                        <c:otherwise>
                           <c:if test="${!study.status.locked}">
                            <span style="float:right">
                                <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth&new=1','spanAlert-dob'); return false;">
                                    <img id="flag_dob" name="flag_dob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                                </a></span>
                            </c:if>
                        </c:otherwise>
                    </c:choose>

                </c:if>
            </td>
        </tr>
    </c:otherwise>
</c:choose>
<tr>
    <td class="table_header_column"><fmt:message key="gender" bundle="${resword}"/></td>
    <td class="table_cell" style="border-bottom-width:1px;border-bottom-color: #CCCCCC">
        <span style="float:left">
        <c:choose>
            <c:when test="${subject.gender==32}">
                &nbsp;
            </c:when>
            <c:when test="${subject.gender==109 ||subject.gender==77}">
                <fmt:message key="male" bundle="${resword}"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="female" bundle="${resword}"/>
            </c:otherwise>
        </c:choose>
            </span>

        <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true'}">
            <c:set var="isNew" value="${hasGenderNote eq 'yes' ? 0 : 1}"/>
            <c:choose>
                <c:when test="${hasGenderNote eq 'yes'}">
                    <span style="float:right"><a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=gender&column=gender','spanAlert-gender'); return false;">
                        <img id="flag_gender" name="flag_gender" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                    </a></span>
                </c:when>
                <c:otherwise>
                   <c:if test="${!study.status.locked}">
                    <span style="float:right"><a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?subjectId=${studySub.id}&id=${subject.id}&writeToDB=1&name=subject&field=gender&column=gender','spanAlert-gender'); return false;">
                        <img id="flag_gender" name="flag_gender" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                    </a></span>
                   </c:if> 
                </c:otherwise>
            </c:choose>
        </c:if>
    </td>
</tr>
<tr>
    <td class="table_header_column"><fmt:message key="enrollment_date" bundle="${resword}"/></td>
    <td class="table_cell_top" style="border-bottom-width:1px;border-bottom-color: #CCCCCC"><span style="float:left"><fmt:formatDate value="${studySub.enrollmentDate}" pattern="${dteFormat}"/></span>
         <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true'}">
            <c:set var="isNew" value="${hasEnrollmentNote eq 'yes' ? 0 : 1}"/>
        <c:choose>
            <c:when test="${hasEnrollmentNote eq 'yes'}">
                <span style="float:right"><a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${studySub.id}&name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate'); return false;">
                    <img id="flag_enrollmentDate" name="flag_enrollmentDate" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                </a></span>
            </c:when>
            <c:otherwise>
               <c:if test="${!study.status.locked}">
                <span style="float:right"><a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?subjectId=${studySub.id}&id=${studySub.id}&writeToDB=1&name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate'); return false;">
                    <img id="flag_enrollmentDate" name="flag_enrollmentDate" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                </a></span>
               </c:if> 
            </c:otherwise>
        </c:choose>
            </c:if></td>
</tr>

</table>

<!-- End Table Contents -->

</td>
</tr>
</table>


</div>

</div></div></div></div></div></div></div></div>

</td>


<td valign="top" width="350" style="padding-right: 20px">

    <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

        <div class="tablebox_center">
            <table border="0" cellpadding="0" cellspacing="0" width="330">
                <tr>
                    <td colspan="2" align="right" valign="top" class="table_actions">&nbsp;
                    </td>
                </tr>
                <tr>
                    <td class="table_header_column_top"><fmt:message key="study_name" bundle="${resword}"/></td>
                    <td class="table_cell_top">
                        <c:choose>
                            <c:when test="${subjectStudy.parentStudyId>0}">
                                <c:out value="${parentStudy.name}"/>
                            </c:when>
                            <c:otherwise>
                                <c:out value="${subjectStudy.name}"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td class="table_header_column"><fmt:message key="unique_protocol_ID" bundle="${resword}"/></td>
                    <td class="table_cell"><c:out value="${subjectStudy.identifier}"/></td>
                </tr>
                <tr>
                    <td class="table_header_column"><fmt:message key="site_name" bundle="${resword}"/></td>
                    <td class="table_cell">
                        <c:if test="${subjectStudy.parentStudyId>0}">
                            <c:out value="${subjectStudy.name}"/>
                        </c:if>&nbsp;</td>
                </tr>


                <tr>
                    <td class="table_divider" colspan="2">&nbsp;</td>
                </tr>
                <tr>
                    <td class="table_header_column_top"><fmt:message key="date_record_created" bundle="${resword}"/></td>
                    <td class="table_cell_top"><fmt:formatDate value="${studySub.createdDate}" pattern="${dteFormat}"/></td>
                </tr>
                <tr>
                    <td class="table_header_column"><fmt:message key="created_by" bundle="${resword}"/></td>
                    <td class="table_cell"><c:out value="${studySub.owner.name}"/></td>
                </tr>
                <tr>
                    <td class="table_header_column"><fmt:message key="date_record_last_updated" bundle="${resword}"/></td>
                    <td class="table_cell"><fmt:formatDate value="${studySub.updatedDate}" pattern="${dteFormat}"/>&nbsp;</td>
                </tr>
                <tr>
                    <td class="table_header_column"><fmt:message key="updated_by" bundle="${resword}"/></td>
                    <td class="table_cell"><c:out value="${studySub.updater.name}"/>&nbsp;</td>
                </tr>
                <tr>
                    <td class="table_header_column"><fmt:message key="status" bundle="${resword}"/></td>
                    <td class="table_cell"><c:out value="${studySub.status.name}"/></td>
                </tr>
            </table>
        </div>

    </div></div></div></div></div></div></div></div>

</td>
</tr>
</table>
<br><br>
</div>

<c:choose>
    <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
        <div class="table_title_Admin">
    </c:when>
    <c:otherwise>

        <c:choose>
            <c:when test="${userRole.manageStudy}">
                <div class="table_title_manage">
            </c:when>
            <c:otherwise>
                <div class="table_title_submit">
            </c:otherwise>
        </c:choose>

    </c:otherwise>
</c:choose>	<a name="events"><a href="javascript:leftnavExpand('subjectEvents');javascript:setImage('ExpandGroup2','images/bt_Expand.gif');"><img
  name="ExpandGroup2" src="images/bt_Collapse.gif" border="0"> <fmt:message key="events" bundle="${resword}"/></a></a></div>
<div id="subjectEvents">
    <c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showStudyEventRow.jsp" /></c:import>


    </br></br>
</div>

<div style="width: 250px">

<c:choose>
<c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
<div class="table_title_Admin">
</c:when>
<c:otherwise>

<c:choose>
<c:when test="${userRole.manageStudy}">
<div class="table_title_Manage">
</c:when>
<c:otherwise>
<div class="table_title_Submit">
    </c:otherwise>
    </c:choose>

    </c:otherwise>
    </c:choose><a name="group"><a href="javascript:leftnavExpand('groups');javascript:setImage('ExpandGroup3','images/bt_Collapse.gif');"><img
  name="ExpandGroup3" src="images/bt_Expand.gif" border="0"> <fmt:message key="group" bundle="${resword}"/></a></a></div>
<div id="groups" style="display:none">
    <div style="width: 600px">
        <!-- These DIVs define shaded box borders -->
        <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

            <div class="tablebox_center">

                <table border="0" cellpadding="0" cellspacing="0" width="100%">

                    <!-- Table Actions row (pagination, search, tools) -->

                    <tr>

                        <!-- Table Tools/Actions cell -->

                        <td align="right" valign="top" class="table_actions">
                            <table border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td class="table_tools">
                                        <c:if test="${study.status.available}">
                                            <a href="UpdateStudySubject?id=<c:out value="${studySub.id}"/>&action=show"><fmt:message key="assign_subject_to_group" bundle="${resworkflow}"/></a>
                                        </c:if>
                                    </td>
                                </tr>
                            </table>
                        </td>

                        <!-- End Table Tools/Actions cell -->
                    </tr>

                    <!-- end Table Actions row (pagination, search, tools) -->

                    <tr>
                        <td valign="top">

                            <!-- Table Contents -->

                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                <tr>
                                    <td class="table_header_row_left"><fmt:message key="subject_group_class" bundle="${resword}"/></td>
                                    <td class="table_header_row"><fmt:message key="study_group" bundle="${resword}"/></td>
                                    <td class="table_header_row"><fmt:message key="notes" bundle="${resword}"/></td>
                                </tr>
                                <c:choose>
                                    <c:when test="${!empty groups}">
                                        <c:forEach var="group" items="${groups}">
                                            <tr>
                                                <td class="table_cell_left"><c:out value="${group.groupClassName}"/></td>
                                                <td class="table_cell"><c:out value="${group.studyGroupName}"/></td>
                                                <td class="table_cell"><c:out value="${group.notes}"/>&nbsp;</td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td class="table_cell" colspan="2"><fmt:message key="currently_no_groups" bundle="${resword}"/></td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </table>

                            <!-- End Table Contents -->

                        </td>
                    </tr>
                </table>


            </div>

        </div></div></div></div></div></div></div></div>

    </div>

    <br><br>
</div>

<div style="width: 250px">

<c:choose>
<c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
<div class="table_title_Admin">
</c:when>
<c:otherwise>

<c:choose>
<c:when test="${userRole.manageStudy}">
<div class="table_title_Manage">
</c:when>
<c:otherwise>
<div class="table_title_Submit">
    </c:otherwise>
    </c:choose>

    </c:otherwise>
    </c:choose>
</div>
<c:choose>
<c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
<div class="table_title_Admin">
</c:when>
<c:otherwise>

<c:choose>
<c:when test="${userRole.manageStudy}">
<div class="table_title_manage">
</c:when>
<c:otherwise>
<div class="table_title_submit">
    </c:otherwise>
    </c:choose>

    </c:otherwise>
    </c:choose>	<a name="global"><a href="javascript:leftnavExpand('globalRecord');javascript:setImage('ExpandGroup5','images/bt_Collapse.gif');"><img
  name="ExpandGroup5" src="images/bt_Expand.gif" border="0"> <fmt:message key="global_subject_record" bundle="${resword}"/></a></a></div>

<div id="globalRecord" style="display:none">
<div style="width: 350px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">

<table border="0" cellpadding="0" cellspacing="0" width="330">

<!-- Table Actions row (pagination, search, tools) -->

<tr>

    <!-- Table Tools/Actions cell -->

    <td align="right" valign="top" class="table_actions">
        <table border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td class="table_tools">
                    <c:if test="${userBean.sysAdmin}">
                        <c:if test="${study.status.available}">
                            <a href="UpdateSubject?id=<c:out value="${subject.id}"/>&studySubId=<c:out value="${studySub.id}"/>&action=show"><fmt:message key="edit_record" bundle="${resword}"/></a>
                        </c:if>    
                    </c:if>
                </td>
            </tr>
        </table>
    </td>

    <!-- End Table Tools/Actions cell -->
</tr>

<!-- end Table Actions row (pagination, search, tools) -->

<tr>
    <td valign="top">

        <!-- Table Contents -->

        <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
                <td class="table_header_column_top"><fmt:message key="person_ID" bundle="${resword}"/></td>
                <td class="table_cell_top"><c:out value="${subject.uniqueIdentifier}"/></td>
            </tr>
            <tr>
                <td class="table_divider" colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td class="table_header_column_top"><fmt:message key="date_record_created" bundle="${resword}"/></td>
                <td class="table_cell_top"><fmt:formatDate value="${subject.createdDate}" pattern="${dteFormat}"/></td>
            </tr>
            <tr>
                <td class="table_header_column"><fmt:message key="created_by" bundle="${resword}"/></td>
                <td class="table_cell"><c:out value="${subject.owner.name}"/></td>
            </tr>
            <tr>
                <td class="table_header_column"><fmt:message key="date_record_last_updated" bundle="${resword}"/></td>
                <td class="table_cell"><fmt:formatDate value="${subject.updatedDate}" pattern="${dteFormat}"/>&nbsp;</td>
            </tr>
            <tr>
                <td class="table_header_column"><fmt:message key="updated_by" bundle="${resword}"/></td>
                <td class="table_cell"><c:out value="${subject.updater.name}"/>&nbsp;</td>
            </tr>
            <tr>
                <td class="table_header_column"><fmt:message key="status" bundle="${resword}"/></td>
                <td class="table_cell"><c:out value="${subject.status.name}"/></td>
            </tr>
            <tr>
                <td class="table_divider" colspan="2">&nbsp;</td>
            </tr>
            <c:choose>
                <c:when test="${subjectStudy.studyParameterConfig.collectDob == '1'}">
                    <tr>
                        <td class="table_header_column_top"><fmt:message key="date_of_birth" bundle="${resword}"/></td>
                        <td class="table_cell_top"><fmt:formatDate value="${subject.dateOfBirth}" pattern="${dteFormat}"/></td>
                    </tr>
                </c:when>
                <c:when test="${subjectStudy.studyParameterConfig.collectDob == '3'}">
                    <tr>
                        <td class="table_header_column_top"><fmt:message key="date_of_birth" bundle="${resword}"/></td>
                        <td class="table_cell_top">&nbsp;</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td class="table_header_column_top"><fmt:message key="year_of_birth" bundle="${resword}"/></td>
                        <td class="table_cell_top"><c:out value="${yearOfBirth}"/></td>
                    </tr>
                </c:otherwise>
            </c:choose>
            <tr>
                <td class="table_header_column"><fmt:message key="gender" bundle="${resword}"/></td>
                <td class="table_cell">
                    <c:choose>
                        <c:when test="${subject.gender==32}">
                            &nbsp;
                        </c:when>
                        <c:when test="${subject.gender==109 ||subject.gender==77}">
                            <fmt:message key="male" bundle="${resword}"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="female" bundle="${resword}"/>
                        </c:otherwise>
                    </c:choose>

                </td>
            </tr>
            <c:if test="${subjectStudy.genetic == true}">
                <tr>
                    <td class="table_header_column"><fmt:message key="mother" bundle="${resword}"/></td>
                    <td class="table_cell"><c:out value="${mother.uniqueIdentifier}"/>&nbsp;</td>
                </tr>
                <tr>
                    <td class="table_header_column"><fmt:message key="father" bundle="${resword}"/></td>
                    <td class="table_cell"><c:out value="${father.uniqueIdentifier}"/>&nbsp;</td>
                </tr>
                <tr>
                    <td class="table_header_column"><fmt:message key="children" bundle="${resword}"/></td>
                    <td class="table_cell">
                        <c:forEach var="child" items="${children}">
                            <c:out value="${child.uniqueIdentifier}"/>,
                        </c:forEach>&nbsp;</td>
                </tr>
            </c:if>
        </table>

        <!-- End Table Contents -->

    </td>
</tr>
</table>


</div>

</div></div></div></div></div></div></div></div>
</div>

</div>
<c:choose>
<c:when test="${from =='listSubject' && userBean.sysAdmin && module=='admin'}">
<p> <a href="ViewSubject?id=<c:out value="${subject.id}"/>"><fmt:message key="go_back_to_view_subject" bundle="${resword}"/></a>  </p>
</c:when>
<c:otherwise>

<c:choose>
<c:when test="${(userRole.manageStudy)&& module=='manage'}">
<p> <a href="ListStudySubject"><fmt:message key="go_back_to_study_subject_list" bundle="${resword}"/></a>  </p>
</c:when>
<c:otherwise>
<p><a href="ListStudySubjectsSubmit"><fmt:message key="go_back_to_subject_list" bundle="${resword}"/></a>  </p>
</c:otherwise>
</c:choose>
</c:otherwise>
</c:choose>
<!-- End Main Content Area -->


<c:import url="../include/workflow.jsp">
    <c:param name="module" value="manage"/>
</c:import>

<jsp:include page="../include/footer.jsp"/>
