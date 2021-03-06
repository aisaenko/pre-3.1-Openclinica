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
<script language="JavaScript" src="includes/CalendarPopup.js" type="text/javascript"></script>
<script language="JavaScript" type="text/javascript">
<!--
function sendToPage() {
	
	var theUrl = "ExportDataset?datasetId=" + <c:out value="${dataset.id}"/>;
	
	var current = window.location+"";
	
	if (current.indexOf("CreateDataset") != -1) {	
	   window.location=theUrl;	   
	   
	}
}

//-->
</script>
</head>

<body onload="sendToPage()" class="background" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0">

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
						<c:choose>
						<c:when test="${userBean != null && userBean.id>0 && userRole != null }">
						 <c:set var="roleName" value="${userRole.role.name}"/>
							<td><a href="MainMenu"
							   onMouseOver="setImage('nav_Home','images/nav_Home_h.gif');"
							   onMouseOut="setImage('nav_Home','images/nav_Home.gif');"><img 
							   name="nav_Home" src="images/nav_Home.gif" border="0" alt="Home" title="Home"></a></td>
         
                           <c:if test="${roleName=='coordinator' || roleName=='director' || roleName=='ra' || roleName=='investigator'}">
							<td>
							 <a href="ListStudySubjectsSubmit"
							   onMouseOver="setImage('nav_Submit','images/nav_Submit_h.gif');"
							   onMouseOut="setImage('nav_Submit','images/nav_Submit.gif');"><img 
							   name="nav_Submit" src="images/nav_Submit.gif" border="0" alt="Submit Data" title="Submit Data"></a>
							 </td>
							</c:if>
							<c:if test="${roleName=='coordinator' || roleName=='director' || roleName=='investigator'}"> 
							<td><a href="ExtractDatasetsMain"
							   onMouseOver="setImage('nav_Extract','images/nav_Extract_s_h.gif');"
							   onMouseOut="setImage('nav_Extract','images/nav_Extract_s.gif');"><img 
							   name="nav_Extract" src="images/nav_Extract_s.gif" border="0" alt="Extract Data" title="Extract Data"></a></td>
							</c:if>
							<c:if test="${roleName=='coordinator' || roleName=='director'}">
							<td><a href="ManageStudy"
							   onMouseOver="setImage('nav_Manage','images/nav_Manage_h.gif');"
							   onMouseOut="setImage('nav_Manage','images/nav_Manage.gif');"><img 
							   name="nav_Manage" src="images/nav_Manage.gif" border="0" alt="Manage Study" title="Manage Study"></a></td>
							</c:if>
							<c:if test="${userBean.sysAdmin}">  
							<td><a href="AdminSystem"
							   onMouseOver="setImage('nav_BizAdmin','images/nav_BizAdmin_h.gif');"
							   onMouseOut="setImage('nav_BizAdmin','images/nav_BizAdmin.gif');"><img 
							   name="nav_BizAdmin" src="images/nav_BizAdmin.gif" border="0" alt="Business Admin" title="Business Admin"></a></td>
							</c:if>
							
							<c:if test="${userBean.techAdmin}">  
							<td><a href="TechAdmin"
							   onMouseOver="setImage('nav_TechAdmin','images/nav_TechAdmin_h.gif');"
							   onMouseOut="setImage('nav_TechAdmin','images/nav_TechAdmin.gif');"><img 
							   name="nav_TechAdmin" src="images/nav_TechAdmin.gif" border="0" alt="Technical Admin" title="Technical Admin"></a>
							   </td>
							</c:if>
							
							<c:if test="${userBean.piiPrivilege.id>1}">  
							<td><a href="SubjectMgmt"
							   onMouseOver="setImage('nav_SubjectMgmt','images/nav_SubjectMgmt_h.gif');"
							   onMouseOut="setImage('nav_SubjectMgmt','images/nav_SubjectMgmt.gif');"><img 
							   name="nav_SubjectMgmt" src="images/nav_SubjectMgmt.gif" border="0" alt="Subject Mangmt" title="Subject Management"></a>
							   </td>
							</c:if>
						 </c:when>
						 <c:otherwise>
						 						 
							<td><img name="nav_Home" src="images/nav_Home_s.gif" border="0" alt="Home" title="Home"></td>
							<td><img name="nav_Submit" src="images/nav_Submit_i.gif" border="0" alt="Submit Data" title="Submit Data"></td>
							<td><img name="nav_Extract" src="images/nav_Extract_i.gif" border="0" alt="Extract Data" title="Extract Data"></td>
							<td><img name="nav_Manage" src="images/nav_Manage_i.gif" border="0" alt="Manage Study" title="Manage Study"></td>
							<td><img name="nav_BizAdmin" src="images/nav_BizAdmin_i.gif" border="0" alt="Business Admin" title="Business Admin"></td>
							<td><img name="nav_TechAdmin" src="images/nav_TechAdmin_i.gif" border="0" alt="Technical Admin" title="Technical Admin"></td>
							<td><img name="nav_SubjectMgmt" src="images/nav_SubjectMgmt_i.gif" border="0" alt="Subject Mangmt" title="Subject Management"></td>
						
						 </c:otherwise>
						 </c:choose>
						</tr>
					</table>
					</td>
				</tr>

		<!-- End Top Navigation Row -->
		<!-- Administration Sub-Navigation Row -->

				<tr>
					<td><img src="images/spacer.gif" width="1" height="4" alt=""></td>
				</tr>
				<tr>
					<td class="subnav_Extract">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><a href="ViewDatasets"
							   onMouseOver="setImage('subnav_Extract_ViewDatasets','images/subnav_Extract_ViewDatasets_h.gif');"
							   onMouseOut="setImage('subnav_Extract_ViewDatasets','images/subnav_Extract_ViewDatasets.gif');"><img 
							   name="subnav_Extract_ViewDatasets" src="images/subnav_Extract_ViewDatasets.gif" border="0" alt="View Datasets" title="View Datasets"></a></td>
							<td><a href="CreateDataset"
							   onMouseOver="setImage('subnav_Extract_CreateDataset','images/subnav_Extract_CreateDataset_h.gif');"
							   onMouseOut="setImage('subnav_Extract_CreateDataset','images/subnav_Extract_CreateDataset.gif');"><img 
							   name="subnav_Extract_CreateDataset" src="images/subnav_Extract_CreateDataset.gif" border="0" alt="Create Dataset" title="Create Dataset"></a></td>
							<!--<td><a href="CreateFiltersOne"
							   onMouseOver="setImage('subnav_Extract_ViewFilters','images/subnav_Extract_ViewFilters_h.gif');"
							   onMouseOut="setImage('subnav_Extract_ViewFilters','images/subnav_Extract_ViewFilters.gif');"><img 
							   name="subnav_Extract_ViewFilters" src="images/subnav_Extract_ViewFilters.gif" border="0" alt="View Filters" title="View Filters"></a></td>
							<td><a href="CreateFiltersOne?action=begin&submit=Create+New+Filter"
							   onMouseOver="setImage('subnav_Extract_CreateFilter','images/subnav_Extract_CreateFilter_h.gif');"
							   onMouseOut="setImage('subnav_Extract_CreateFilter','images/subnav_Extract_CreateFilter.gif');"><img 
							   name="subnav_Extract_CreateFilter" src="images/subnav_Extract_CreateFilter.gif" border="0" alt="Create Filter" title="Create Filter"></a></td>-->

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
	<img src="images/spacer.gif" width="596" height="1" alt=""><br>
<!-- End Main Navigation -->