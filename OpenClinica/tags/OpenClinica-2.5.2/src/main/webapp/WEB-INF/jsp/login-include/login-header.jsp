<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/> 


<html>

<head>

<title><fmt:message key="openclinica" bundle="${resword}"/></title>

<link rel="stylesheet" href="includes/styles.css" type="text/css">

<script language="JavaScript" src="includes/global_functions_javascript.js"></script>
<!--added to try and refres the page in the case of Tomcat server interruption-->
<meta http-equiv="refresh" content="80">
</head>

<body class="main_BG" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0">

<!--  Remove the comment tag for the following div if you want to put your own logo on the login page. -->
<!-- Simply replace Akazalogo.gif with your own logo image.-->
<!-- 
<div class="container">
<div class="akaza_logo"><img src="images/Akazalogo.gif" alt="Akaza OpenClinica" title="Akaza OpenCinica" /></div>
</div>
-->
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
							<td><a href="MainMenu"
							   onMouseOver="javascript:setImage('nav_Home','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Home_s.gif');"
							   onMouseOut="javascript:setImage('nav_Home','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Home_s.gif');"><img 
							   name="nav_Home" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Home_s.gif" border="0" alt="<fmt:message key="home" bundle="${resworkflow}"/>" title="<fmt:message key="home" bundle="${resworkflow}"/>"></a></td>
							<td><img name="nav_Submit" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Submit_i.gif" border="0" alt="<fmt:message key="submit_data" bundle="${resworkflow}"/>" title="<fmt:message key="submit_data" bundle="${resworkflow}"/>"></td>
							<td><img name="nav_Extract" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Extract_i.gif"" border="0" alt="<fmt:message key="extract_data" bundle="${resworkflow}"/>" title="<fmt:message key="extract_data" bundle="${resworkflow}"/>"></td>
							<td><img name="nav_Manage" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Manage_i.gif"" border="0" alt="<fmt:message key="manage_study" bundle="${resworkflow}"/>" title="<fmt:message key="manage_study" bundle="${resworkflow}"/>"></td>
							<td><img name="nav_BizAdmin" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin_i.gif" border="0" alt="<fmt:message key="business_admin" bundle="${resworkflow}"/>" title="<fmt:message key="business_admin" bundle="${resworkflow}"/>"></td>
							<%--
							<td><img name="nav_TechAdmin" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_TechAdmin_i.gif" border="0" alt="<fmt:message key="technical_admin" bundle="${resworkflow}"/>" title="<fmt:message key="technical_admin" bundle="${resworkflow}"/>"></td>
							--%>
						</tr>
					</table>
					</td>
				</tr>

		<!-- End Top Navigation Row -->

	</table>

			</div>

		</div></div></div></div></div></div></div></div>

			</td>
		</tr>
	</table>
	
	</div>

	<img src="images/spacer.gif" width="596" height="1"><br>

<!-- End Main Navigation -->

