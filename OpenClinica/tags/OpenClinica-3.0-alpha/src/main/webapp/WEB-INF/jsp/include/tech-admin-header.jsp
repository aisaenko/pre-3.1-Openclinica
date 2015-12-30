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

<html>

<head>

<title><fmt:message key="openclinica" bundle="${resword}"/></title>

<link rel="stylesheet" href="includes/styles.css" type="text/css">
<link rel="stylesheet" href="includes/styles2.css" type="text/css">
<script language="JavaScript" src="includes/Tabs.js"></script>
<script language="JavaScript" src="includes/global_functions_javascript.js"></script>
<script language="JavaScript" src="includes/CalendarPopup.js"></script>
    <!-- Added for the new Calender -->

    <link rel="stylesheet" type="text/css" media="all" href="includes/new_cal/skins/aqua/theme.css" title="Aqua" />
    <script type="text/javascript" src="includes/new_cal/calendar.js"></script>
    <script type="text/javascript" src="includes/new_cal/lang/calendar-en.js"></script>
    <script type="text/javascript" src="includes/new_cal/calendar-setup.js"></script>
<!-- End -->
    
</head>

<body class="main_BG" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0"
	<jsp:include page="../include/showPopUp.jsp"/>
>

<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="background">
	<tr>
		<td valign="top">
<!-- Header Table -->

<SCRIPT LANGUAGE="JavaScript">

document.write('<table border="0" cellpadding="0" cellspacing="0" width="' + document.body.clientWidth + '" class="header">');

</script>
			<tr>
				<td valign="top">

<!-- Logo -->

	<div class="logo"><img src="images/Logo.gif"></div>

<!-- Main Navigation -->

	<div class="nav">

	<table border="0" cellpadding="0" cellspacing="0">
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
							   onMouseOver="javascript:setImage('nav_Manage','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Manage_h.gif');"
							   onMouseOut="javascript:setImage('nav_Manage','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Manage.gif');"><img 
							   name="nav_Manage" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Manage.gif"" border="0" alt="<fmt:message key="manage_study" bundle="${resworkflow}"/>" title="<fmt:message key="manage_study" bundle="${resworkflow}"/>"></a></td>
							</c:if>
							<c:if test="${userBean.sysAdmin}">  
							<td><a href="AdminSystem"
							   onMouseOver="javascript:setImage('nav_BizAdmin','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin_h.gif');"
							   onMouseOut="javascript:setImage('nav_BizAdmin','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin.gif');"><img 
							   name="nav_BizAdmin" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin.gif" border="0" alt="<fmt:message key="business_admin" bundle="${resworkflow}"/>" title="<fmt:message key="business_admin" bundle="${resworkflow}"/>"></a></td>
							</c:if>
							<%-- change to tech admin here--%>
							<%--
							<c:if test="${userBean.techAdmin}">  
							<td><a href="TechAdmin"
							   onMouseOver="javascript:setImage('nav_Administer','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_TechAdmin_s_h.gif');"
							   onMouseOut="javascript:setImage('nav_TechAdmin','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_TechAdmin_s.gif');"><img 
							   name="nav_TechAdmin" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_TechAdmin_s.gif" border="0" alt="<fmt:message key="technical_admin" bundle="${resworkflow}"/>" title="<fmt:message key="technical_admin" bundle="${resworkflow}"/>"></a>
							   </td>
							</c:if>
							--%>
							
						 </c:when>
						 <c:otherwise>
						 						 
							<td><img name="nav_Home" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Home_s.gif" border="0" alt="<fmt:message key="home" bundle="${resworkflow}"/>" title="<fmt:message key="home" bundle="${resworkflow}"/>"></td>
							<td><img name="nav_Submit" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Submit_i.gif" border="0" alt="<fmt:message key="submit_data" bundle="${resworkflow}"/>" title="<fmt:message key="submit_data" bundle="${resworkflow}"/>"></td>
							<td><img name="nav_Extract" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Extract_i.gif"" border="0" alt="<fmt:message key="extract_data" bundle="${resworkflow}"/>" title="<fmt:message key="extract_data" bundle="${resworkflow}"/>"></td>
							<td><img name="nav_Manage" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Manage_i.gif"" border="0" alt="<fmt:message key="manage_study" bundle="${resworkflow}"/>" title="<fmt:message key="manage_study" bundle="${resworkflow}"/>"></td>
							<td><img name="nav_BizAdmin" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin_i.gif" border="0" alt="<fmt:message key="business_admin" bundle="${resworkflow}"/>" title="<fmt:message key="business_admin" bundle="${resworkflow}"/>"></td>
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
		<!-- Administration Sub-Navigation Row -->

				<tr>
					<td><img src="images/spacer.gif" width="1" height="4"></td>
				</tr>
				<tr>
					<td class="subnav_TechAdmin">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
						
							<td><a href="ListUserAccounts"
							   onMouseOver="javascript:setImage('subnav_TechAdmin_Users','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_TechAdmin_Users_h.gif');"
							   onMouseOut="javascript:setImage('subnav_TechAdmin_Users','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_TechAdmin_Users.gif');"><img 
							   name="subnav_TechAdmin_Users" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_TechAdmin_Users.gif" border="0" alt="<fmt:message key="users" bundle="${resword}"/>" title=<fmt:message key="users" bundle="${resword}"/>></a></td>
							   
							<%--<td><a href="ViewScheduler"
							   onMouseOver="javascript:setImage('subnav_TechAdmin_SystemProperties','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_TechAdmin_SystemProperties_h.gif');"
							   onMouseOut="javascript:setImage('subnav_TechAdmin_SystemProperties','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_TechAdmin_SystemProperties.gif');"><img 
							   name="subnav_TechAdmin_SystemProperties" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_TechAdmin_SystemProperties.gif" border="0" alt="System Properties" title="System Properties"></a></td>--%>
							   
							<td><a href="Enterprise"
							   onMouseOver="javascript:setImage('subnav_TechAdmin_OCEnterprise','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_TechAdmin_OCEnterprise_h.gif');"
							   onMouseOut="javascript:setImage('subnav_TechAdmin_OCEnterprise','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_TechAdmin_OCEnterprise.gif');"><img 
							   name="subnav_TechAdmin_OCEnterprise" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_TechAdmin_OCEnterprise.gif" border="0" alt="<fmt:message key="openclinica_enterprise" bundle="${resword}"/>" title="<fmt:message key="openclinica_enterprise" bundle="${resword}"/>"></a></td>
							   
							
							   
						</tr>
					</table>
					</td>
				</tr>

		<!-- End Administration Sub-Navigation Row -->

			</table>

			</div>

		</div></div></div></div></div></div></div></div>

			</td>
		</tr>
	</table>
	
	</div>
	<img src="images/spacer.gif" width="596" height="1"><br>
<!-- End Main Navigation -->
