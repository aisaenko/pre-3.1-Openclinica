<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/> 



<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='isAdminServlet' class='java.lang.String' />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <meta http-equiv="content-type" content="text/html; charset=utf-8" />

<title><fmt:message key="openclinica" bundle="${resword}"/></title>

<link rel="stylesheet" href="includes/styles.css" type="text/css">
<link rel="stylesheet" href="includes/styles2.css" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/Tabs.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/CalendarPopup.js"></script>
    <!-- Added for the new Calender -->

    <link rel="stylesheet" type="text/css" media="all" href="includes/new_cal/skins/aqua/theme.css" title="Aqua" />
    <script type="text/javascript" src="includes/new_cal/calendar.js"></script>
    <script type="text/javascript" src="includes/new_cal/lang/calendar-en.js"></script>
    <script type="text/javascript" src="includes/new_cal/calendar-setup.js"></script>
<!-- End -->
    
<%--<script type="text/javascript"  language="JavaScript" src=
    "includes/repetition-model/repetition-model.js"></script>--%>
<script type="text/JavaScript" language="JavaScript" src="includes/prototype.js"></script>
<%--<script type="text/JavaScript" language="JavaScript" src="includes/scriptaculous.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/effects.js"></script>--%></head>


<body style="width:1024px;" class="main_BG"

<c:choose>

<c:when test="${tabId!= null && tabId>0}">
onload="TabsForwardByNum(<c:out value="${tabId}"/>);<jsp:include page="../include/showPopUp2.jsp"/>"
</c:when>

<c:otherwise>

<jsp:include page="../include/showPopUp.jsp"/>

</c:otherwise>
</c:choose>
>
<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class=
  "background">
	<tr>
		<td valign="top">
<!-- Header Table -->

<!-- NEW 06-22 -->
	<script language="JavaScript">
	var StatusBoxValue=1;
	</script>

<table border="0" cellpadding="0" cellspacing="0" class="header">

			<tr>
				<td valign="top">

<!-- Logo -->

	<div class="logo"><img src="images/Logo.gif"></div>

<!-- Main Navigation -->

	<div class="nav">

	<table border="0" cellpadding="0" cellspacing="0" style="margin-bottom: 4px">
		<tr>
			<td>

	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="navbox_center">

			<table border="0" cellpadding="0" cellspacing="0">

		<!-- Top Navigation Row -->
				<tr>
					<td>
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
						<c:choose>
						<c:when test="${userBean != null && userBean.id>0 && userRole != null }">
						 <c:set var="roleName" value="${userRole.role.name}"/>
							<td><a href="MainMenu"
							   onMouseOver="javascript:setImage('nav_Home','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Home_h.gif');"
							   onMouseOut="javascript:setImage('nav_Home','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Home.gif');"><img 
							   name="nav_Home" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Home.gif" border="0" alt="<fmt:message key="home" bundle="${resworkflow}"/>" title="<fmt:message key="home" bundle="${resworkflow}"/>"></a></td>
         
                           <c:if test="${userRole.submitData}">
							<td>
							 <a href="ListStudySubjectsSubmit"
							   onMouseOver="javascript:setImage('nav_Submit','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Submit_h.gif');"
							   onMouseOut="javascript:setImage('nav_Submit','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Submit.gif');"><img 
							   name="nav_Submit" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Submit.gif" border="0" alt="<fmt:message key="submit_data" bundle="${resworkflow}"/>" title="<fmt:message key="submit_data" bundle="${resworkflow}"/>"></a></td>
							</c:if> 
							<c:if test="${userRole.extractData}"> 
							  <td><a href="ExtractDatasetsMain"
							   onMouseOver="javascript:setImage('nav_Extract','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Extract_h.gif');"
							   onMouseOut="javascript:setImage('nav_Extract','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Extract.gif');"><img 
							   name="nav_Extract" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Extract.gif"" border="0" alt="<fmt:message key="extract_data" bundle="${resworkflow}"/>" title="<fmt:message key="extract_data" bundle="${resworkflow}"/>"></a></td>
							</c:if>
							<c:if test="${userRole.manageStudy}">
							<td><a href="ManageStudy"
							   onMouseOver="javascript:setImage('nav_Manage','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Manage_s_h.gif');"
							   onMouseOut="javascript:setImage('nav_Manage','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Manage_s.gif');"><img 
							   name="nav_Manage" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Manage_s.gif"" border="0" alt="<fmt:message key="manage_study" bundle="${resworkflow}"/>" title="<fmt:message key="manage_study" bundle="${resworkflow}"/>"></a></td>
							</c:if>
							<c:if test="${userBean.sysAdmin}">  
							<td><a href="AdminSystem"
							   onMouseOver="javascript:setImage('nav_BizAdmin','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin_h.gif');"
							   onMouseOut="javascript:setImage('nav_BizAdmin','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin.gif');"><img 
							   name="nav_BizAdmin" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin.gif" border="0" alt="<fmt:message key="business_admin" bundle="${resworkflow}"/>" title="<fmt:message key="business_admin" bundle="${resworkflow}"/>"></a></td>
							</c:if>
							
							<%--
							<c:if test="${userBean.techAdmin}">  
							<td><a href="AdminSystem"
							   onMouseOver="javascript:setImage('nav_TechAdmin','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_TechAdmin_h.gif');"
							   onMouseOut="javascript:setImage('nav_TechAdmin','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_TechAdmin.gif');"><img 
							   name="nav_TechAdmin" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_TechAdmin.gif" border="0" alt="<fmt:message key="technical_admin" bundle="${resworkflow}"/>" title="<fmt:message key="technical_admin" bundle="${resworkflow}"/>"></a>
							 </td>
							</c:if>
							--%>
							
						 </c:when>
						 <c:otherwise>
						 						 
							<td><img name="nav_Home" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Home_s.gif" border="0" alt="<fmt:message key="home" bundle="${resworkflow}"/>" title="<fmt:message key="home" bundle="${resworkflow}"/>"></td>
							<td><img name="nav_Submit" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Submit_i.gif" border="0" alt="<fmt:message key="submit_data" bundle="${resworkflow}"/>" title="<fmt:message key="submit_data" bundle="${resworkflow}"/>"></td>
							<td><img name="nav_Extract" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Extract_i.gif"" border="0" alt="<fmt:message key="extract_data" bundle="${resworkflow}"/>" title="<fmt:message key="extract_data" bundle="${resworkflow}"/>"></td>
							<td><img name="nav_Manage" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Manage_i.gif"" border="0" alt="<fmt:message key="manage_study" bundle="${resworkflow}"/>" title="<fmt:message key="manage_study" bundle="${resworkflow}"/>"></td>
							<td><img name="nav_Administer" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin_i.gif" border="0" alt="<fmt:message key="business_admin" bundle="${resworkflow}"/>" title="<fmt:message key="business_admin" bundle="${resworkflow}"/>"></td>
							<%--
							<td><img name="nav_TechAdmin" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_TechAdmin_i.gif" border="0" alt="<fmt:message key="technical_admin" bundle="${resworkflow}"/>" title="<fmt:message key="technical_admin" bundle="${resworkflow}"/>"></td>
							--%>
						
						 </c:otherwise>
						 </c:choose>
						</tr>
					</table>
					</td>
				</tr>

		<!-- End Top Navigation Row -->
		<!-- <fmt:message key="manage_study" bundle="${resword}"/> Sub-Navigation Row -->
			<%-- <c:if test="${roleName=='coordinator' || roleName=='director'}"> --%>
				<tr>
					<td><img src="images/spacer.gif" width="1" height="4"></td>
				</tr>
				<tr>
					<td class="subnav_Manage">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
							<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td><a href="ListStudySubject"
									   onMouseOver="javascript:setImage('subnav_Manage_Subjects','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Subjects_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_Subjects','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Subjects.gif');"><img 
									   name="subnav_Manage_Subjects" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Subjects.gif" border="0" alt="<fmt:message key="manage_subjects" bundle="${resworkflow}"/>" title="<fmt:message key="manage_subjects" bundle="${resworkflow}"/>"></a></td>
									<td><a href="ListSubjectGroupClass"
									   onMouseOver="javascript:setImage('subnav_Manage_Groups','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Groups_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_Groups','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Groups.gif');"><img 
									   name="subnav_Manage_Groups" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Groups.gif" border="0" alt="<fmt:message key="manage_groups" bundle="${resworkflow}"/>" title="<fmt:message key="manage_groups" bundle="${resworkflow}"/>"></a></td>
									<td><a href="ViewStudyEvents?module=manage"
									   onMouseOver="javascript:setImage('subnav_Manage_ViewEvents','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_ViewEvents_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_ViewEvents','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_ViewEvents.gif');"><img 
									   name="subnav_Manage_ViewEvents" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_ViewEvents.gif" border="0" alt="<fmt:message key="view_study_events" bundle="${resworkflow}"/>" title="<fmt:message key="view_study_events" bundle="${resworkflow}"/>"></a></td>   
									<td><a href="ListDiscNotesSubjectServlet?module=manage"
									   onMouseOver="javascript:setImage('subnav_Manage_NotesDiscrepancies','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_NotesDiscrepancies_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_NotesDiscrepancies','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_NotesDiscrepancies.gif');"><img 
									   name="subnav_Manage_NotesDiscrepancies" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_NotesDiscrepancies.gif" border="0" alt="<fmt:message key="discrepancies" bundle="${resworkflow}"/>" title="<fmt:message key="discrepancies" bundle="${resworkflow}"/>"></a></td>	
									<td><a href="ViewRuleAssignment"
									   onMouseOver="javascript:setImage('subnav_Manage_Rules','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Rules_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_Rules','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Rules.gif');"><img
									   name="subnav_Manage_Rules" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Rules.gif" border="0" alt="<fmt:message key="manage_rule" bundle="${resworkflow}"/>" title="<fmt:message key="manage_rule" bundle="${resworkflow}"/>"></a></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td>
							<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td><img src="images/subnav_Manage_row2_L.gif"></td>
									<td><a href="ListStudyUser"
									   onMouseOver="javascript:setImage('subnav_Manage_Users','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Users_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_Users','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Users.gif');"><img 
									   name="subnav_Manage_Users" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Users.gif" border="0" alt="<fmt:message key="manage_users" bundle="${resworkflow}"/>" title="<fmt:message key="manage_users" bundle="${resworkflow}"/>"></a></td>
									<td><a href="ListSite"
									   onMouseOver="javascript:setImage('subnav_Manage_Sites','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Sites_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_Sites','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Sites.gif');"><img 
									   name="subnav_Manage_Sites" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_Sites.gif" border="0" alt="<fmt:message key="manage_sites" bundle="${resworkflow}"/>" title="<fmt:message key="manage_sites" bundle="${resworkflow}"/>"></a></td>
									<td><a href="ListEventDefinition"
									   onMouseOver="javascript:setImage('subnav_Manage_StudyEventDefinitions','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_StudyEventDefinitions_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_StudyEventDefinitions','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_StudyEventDefinitions.gif');"><img 
									   name="subnav_Manage_StudyEventDefinitions" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_StudyEventDefinitions.gif" border="0" alt="<fmt:message key="manage_study_event_definitions" bundle="${resworkflow}"/>" title="<fmt:message key="manage_study_event_definitions" bundle="${resworkflow}"/>"></a></td>
									<td><a href="ListCRF?module=manage"
									   onMouseOver="javascript:setImage('subnav_Manage_CRFs','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_CRFs_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_CRFs','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_CRFs.gif');">
									   <img name="subnav_Manage_CRFs" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Manage_CRFs.gif" border="0" alt="<fmt:message key="manage_CRFs" bundle="${resworkflow}"/>" title="<fmt:message key="manage_CRFs" bundle="${resworkflow}"/>"></a></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			<%-- </c:if> --%>

		<!-- End Manage Study Sub-Navigation Row -->
		

			</table>

			</div>

		</div></div></div></div></div></div></div></div>

			</td>
		</tr>
	</table>
	
	</div>
	<img src="images/spacer.gif" width="596" height="1"><br>
<!-- End Main Navigation -->
