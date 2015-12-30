<%@page contentType="text/html"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='isAdminServlet' class='java.lang.String' />

<html>

<head>

<title>OpenClinica</title>

<link rel="stylesheet" href="includes/styles.css" type="text/css">
<link rel="stylesheet" href="includes/styles2.css" type="text/css">
<script language="JavaScript" src="includes/Tabs.js"></script>
<script language="JavaScript" src="includes/global_functions_javascript.js"></script>
<script language="JavaScript" src="includes/CalendarPopup.js"></script>
</head>

<body class="main_BG" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0"
	<jsp:include page="../include/showPopUp.jsp"/>
<c:if test="${tabId!= null && tabId>0}">
onload="TabsForwardByNum(<c:out value="${tabId}"/>)">
</c:if>
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
							   onMouseOver="javascript:setImage('nav_Home','images/nav_Home_h.gif');"
							   onMouseOut="javascript:setImage('nav_Home','images/nav_Home.gif');"><img 
							   name="nav_Home" src="images/nav_Home.gif" border="0" alt="Home" title="Home"></a></td>
         
                           <c:if test="${roleName=='coordinator' || roleName=='director' || roleName=='ra' || roleName=='investigator'}">
							<td>
							 <a href="ListStudySubjectsSubmit"
							   onMouseOver="javascript:setImage('nav_Submit','images/nav_Submit_h.gif');"
							   onMouseOut="javascript:setImage('nav_Submit','images/nav_Submit.gif');"><img 
							   name="nav_Submit" src="images/nav_Submit.gif" border="0" alt="Submit Data" title="Submit Data"></a>
							 </td>
							</c:if> 
							<c:if test="${roleName=='coordinator' || roleName=='director' || roleName=='investigator'}"> 
							  <td><a href="ExtractDatasetsMain"
							   onMouseOver="javascript:setImage('nav_Extract','images/nav_Extract_h.gif');"
							   onMouseOut="javascript:setImage('nav_Extract','images/nav_Extract.gif');"><img 
							   name="nav_Extract" src="images/nav_Extract.gif"" border="0" alt="Extract Data" title="Extract Data"></a></td>
							</c:if>
							<c:if test="${roleName=='coordinator' || roleName=='director'}">
							<td><a href="ManageStudy"
							   onMouseOver="javascript:setImage('nav_Manage','images/nav_Manage_s_h.gif');"
							   onMouseOut="javascript:setImage('nav_Manage','images/nav_Manage_s.gif');"><img 
							   name="nav_Manage" src="images/nav_Manage_s.gif"" border="0" alt="Manage Study" title="Manage Study"></a></td>
							</c:if>
							<c:if test="${userBean.sysAdmin}">  
							<td><a href="AdminSystem"
							   onMouseOver="javascript:setImage('nav_BizAdmin','images/nav_BizAdmin_h.gif');"
							   onMouseOut="javascript:setImage('nav_BizAdmin','images/nav_BizAdmin.gif');"><img 
							   name="nav_BizAdmin" src="images/nav_BizAdmin.gif" border="0" alt="Business Admin" title="Business Admin"></a>
							 </td>
							</c:if>
							<c:if test="${userBean.techAdmin}">  
							<td><a href="AdminSystem"
							   onMouseOver="javascript:setImage('nav_TechAdmin','images/nav_TechAdmin_h.gif');"
							   onMouseOut="javascript:setImage('nav_TechAdmin','images/nav_TechAdmin.gif');"><img 
							   name="nav_TechAdmin" src="images/nav_TechAdmin.gif" border="0" alt="Technical Admin" title="Technical Admin"></a>
							 </td>
							</c:if>
						 </c:when>
						 <c:otherwise>
						 						 
							<td><img name="nav_Home" src="images/nav_Home_s.gif" border="0" alt="Home" title="Home"></td>
							<td><img name="nav_Submit" src="images/nav_Submit_i.gif" border="0" alt="Submit Data" title="Submit Data"></td>
							<td><img name="nav_Extract" src="images/nav_Extract_i.gif"" border="0" alt="Extract Data" title="Extract Data"></td>
							<td><img name="nav_Manage" src="images/nav_Manage_i.gif"" border="0" alt="Manage Study" title="Manage Study"></td>
							<td><img name="nav_Administer" src="images/nav_BizAdmin_i.gif" border="0" alt="Business Admin" title="Business Admin"></td>
							<td><img name="nav_TechAdmin" src="images/nav_TechAdmin_i.gif" border="0" alt="Technical Admin" title="Technical Admin"></td>
						
						 </c:otherwise>
						 </c:choose>
						</tr>
					</table>
					</td>
				</tr>

		<!-- End Top Navigation Row -->
		<!-- Manage Study Sub-Navigation Row -->

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
									   onMouseOver="javascript:setImage('subnav_Manage_Subjects','images/subnav_Manage_Subjects_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_Subjects','images/subnav_Manage_Subjects.gif');"><img 
									   name="subnav_Manage_Subjects" src="images/subnav_Manage_Subjects.gif" border="0" alt="Manage Subjects" title="Manage Subjects"></a></td>
									<td><a href="ListSubjectGroupClass"
									   onMouseOver="javascript:setImage('subnav_Manage_Groups','images/subnav_Manage_Groups_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_Groups','images/subnav_Manage_Groups.gif');"><img 
									   name="subnav_Manage_Groups" src="images/subnav_Manage_Groups.gif" border="0" alt="Manage Groups" title="Manage Groups"></a></td>
									<td><a href="ViewStudyEvents"
									   onMouseOver="javascript:setImage('subnav_Manage_ViewEvents','images/subnav_Manage_ViewEvents_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_ViewEvents','images/subnav_Manage_ViewEvents.gif');"><img 
									   name="subnav_Manage_ViewEvents" src="images/subnav_Manage_ViewEvents.gif" border="0" alt="View Study Events" title="View Study Events"></a></td>   
									<td><a href="ViewNotes"
									   onMouseOver="javascript:setImage('subnav_Manage_Discrepancies','images/subnav_Manage_Discrepancies_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_Discrepancies','images/subnav_Manage_Discrepancies.gif');"><img 
									   name="subnav_Manage_Discrepancies" src="images/subnav_Manage_Discrepancies.gif" border="0" alt="Discrepancies" title="Discrepancies"></a></td>	
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
									   onMouseOver="javascript:setImage('subnav_Manage_Users','images/subnav_Manage_Users_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_Users','images/subnav_Manage_Users.gif');"><img 
									   name="subnav_Manage_Users" src="images/subnav_Manage_Users.gif" border="0" alt="Manage Users" title="Manage Users"></a></td>
									<td><a href="ListSite"
									   onMouseOver="javascript:setImage('subnav_Manage_Sites','images/subnav_Manage_Sites_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_Sites','images/subnav_Manage_Sites.gif');"><img 
									   name="subnav_Manage_Sites" src="images/subnav_Manage_Sites.gif" border="0" alt="Manage Sites" title="Manage Sites"></a></td>
									<td><a href="ListEventDefinition"
									   onMouseOver="javascript:setImage('subnav_Manage_StudyEventDefinitions','images/subnav_Manage_StudyEventDefinitions_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_StudyEventDefinitions','images/subnav_Manage_StudyEventDefinitions.gif');"><img 
									   name="subnav_Manage_StudyEventDefinitions" src="images/subnav_Manage_StudyEventDefinitions.gif" border="0" alt="Manage Study Event Definitions" title="Manage Study Event Definitions"></a></td>
									<td><a href="ListCRF"
									   onMouseOver="javascript:setImage('subnav_Manage_CRFs','images/subnav_Manage_CRFs_h.gif');"
									   onMouseOut="javascript:setImage('subnav_Manage_CRFs','images/subnav_Manage_CRFs.gif');"><img 
									   name="subnav_Manage_CRFs" src="images/subnav_Manage_CRFs.gif" border="0" alt="Manage CRFs" title="Manage CRFs"></a></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>

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