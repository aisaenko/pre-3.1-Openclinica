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

<script language="JavaScript" src="includes/global_functions_javascript.js" type="text/javascript"></script>
<script language="JavaScript" src="includes/Tabs.js" type="text/javascript"></script>
<script language="JavaScript" src="includes/CalendarPopup.js" type="text/javascript"></script>
</head>

<body class="main_BG" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0"
	<jsp:include page="../include/showPopUp.jsp"/>
<c:if test="${tabId!= null && tabId>0}">
onload="TabsForwardByNum(<c:out value="${tabId}"/>)"
</c:if>
>

<div class="container">
<div class="ndar_logo">	<img src="images/NDAR-Logo.gif" alt="NDAR - National Database for Autism Research" title="" /></div>
</div>

<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="background">
	<tr>
		<td valign="top">
<!-- Header Table -->

<SCRIPT LANGUAGE="JavaScript" type="text/javascript">

document.write('<table border="0" cellpadding="0" cellspacing="0" width="' + document.body.clientWidth + '" class="header">');

</script>
			<tr>
				<td valign="top">

<!-- Logo -->

	<div class="logo"><img src="images/Logo.gif" alt="OpenClinica" title=""></div>

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
						
						
						 
							<td><img name="nav_Home" src="images/nav_Home_i.gif" border="0" alt="Home" title="Home"></td>
         
                           
							<td><img name="nav_Submit" src="images/nav_Submit_i.gif" border="0" alt="Submit Data" title="Submit Data">
							 </td>
						   	
						   							 
							<td><img name="nav_Extract" src="images/nav_Extract_i.gif" border="0" alt="Extract Data" title="Extract Data"></td>
							
							
							<td><img name="nav_Manage" src="images/nav_Manage_i.gif" border="0" alt="Manage Study" title="Manage Study"></td>
							
							  
							<td><img name="nav_BizAdmin" src="images/nav_BizAdmin_i.gif" border="0" alt="Business Admin" title="Business Admin"></td>
							<td><img name="nav_TechAdmin" src="images/nav_TechAdmin_i.gif" border="0" alt="Technical Admin" title="Technical Admin"></td>
							
							
							
						 
						 
						 
						</tr>
					</table>
					</td>
				</tr>

		<!-- End Top Navigation Row -->
		<!-- Submit Data Sub-Navigation Row -->

				<tr>

					<td><img src="images/spacer.gif" width="1" height="4" alt=""></td>
				</tr>
				<tr>
					<td class="subnav_inactive">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><img name="subnav_Submit_ViewAllSubjects" src="images/subnav_Submit_ViewAllSubjects_i.gif" border="0" alt="View All Subjects" title="View All Subjects"></td>
							<td><img name="subnav_Submit_EnrollSubject" src="images/subnav_Submit_EnrollSubject_i.gif" border="0" alt="Enroll Subject" title="Enroll Subject"></td>
							<td><img name="subnav_Submit_CreateStudyEvent" src="images/subnav_Submit_CreateStudyEvent_i.gif" border="0" alt="Add New Study Event" title="Add New Study Event"></td>

						    <td><img name="subnav_Submit_ViewEvents" src="images/subnav_Submit_ViewEvents_i.gif" border="0" alt="View Study Events" title="View Study Events"></td>   	   
						</tr>
					</table>
					</td>
				</tr>

		<!-- End Submit Data Sub-Navigation Row -->

			</table>

			</div>

		</div></div></div></div></div></div></div></div>

			</td>
		</tr>
	</table>
	
	</div>
	<img src="images/spacer.gif" width="596" height="1" alt=""><br>
<!-- End Main Navigation -->