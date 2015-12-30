<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

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
<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/Tabs.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/CalendarPopup.js"></script>
<script type="text/JavaScript" language="JavaScript" src=
  "includes/repetition-model/repetition-model.js"></script>
  <script type="text/JavaScript" language="JavaScript" src="includes/prototype.js"></script>
  <script type="text/JavaScript" language="JavaScript" src="includes/scriptaculous.js"></script>
  <script type="text/JavaScript" language="JavaScript" src="includes/effects.js"></script>
</head>
 <%-- style="width:1152px;" I removed this include because you cannot have both
 	"onLoad" tests: <jsp:include page="../include/showPopUp.jsp"/> reduce file size--%>
 	<%--  --%>
<body style="width:1024px;" class="main_BG"
<c:choose> 
<c:when test="${tabId != null && tabId>0}">
onload="TabsForwardByNum(<c:out value="${tabId}"/>);<jsp:include page="../include/showPopUp2.jsp"/>"
</c:when>
<c:otherwise>
<jsp:include page="../include/showPopUp.jsp"/>
</c:otherwise>
</c:choose>
>

<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="background">
	<tr>
		<td valign="top">
<!-- Header Table -->
<table border="0" cellpadding="0" cellspacing="0" class="header">
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
							   onMouseOver="javascript:setImage('nav_Administer','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin_s_h.gif');"
							   onMouseOut="javascript:setImage('nav_BizAdmin','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin_s.gif');"><img 
							   name="nav_BizAdmin" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_BizAdmin_s.gif" border="0" alt="<fmt:message key="business_admin" bundle="${resworkflow}"/>" title="<fmt:message key="business_admin" bundle="${resworkflow}"/>"></a></td>
							</c:if>
							
							<%--
							<c:if test="${userBean.techAdmin}">  
							<td><a href="TechAdmin"
							   onMouseOver="javascript:setImage('nav_Administer','images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_TechAdmin_s_h.gif');"
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
					<td class="subnav_Admin">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><a href="ListUserAccounts"
							   onMouseOver="javascript:setImage('subnav_Admin_Users','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_Users_h.gif');"
							   onMouseOut="javascript:setImage('subnav_Admin_Users','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_Users.gif');"><img 
							   name="subnav_Admin_Users" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_Users.gif" border="0" alt="<fmt:message key="users" bundle="${resword}"/>" title="<fmt:message key="users" bundle="${resword}"/>"></a></td>
							<td><a href="ListSubject"
							   onMouseOver="javascript:setImage('subnav_Admin_Subjects','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_Subjects_h.gif');"
							   onMouseOut="javascript:setImage('subnav_Admin_Subjects','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_Subjects.gif');"><img 
							   name="subnav_Admin_Subjects" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_Subjects.gif" border="0" alt="<fmt:message key="subjects" bundle="${resword}"/>" title="<fmt:message key="subjects" bundle="${resword}"/>"></a></td>
							<td><a href="ListStudy"
							   onMouseOver="javascript:setImage('subnav_Admin_Studies','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_Studies_h.gif');"
							   onMouseOut="javascript:setImage('subnav_Admin_Studies','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_Studies.gif');"><img 
							   name="subnav_Admin_Studies" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_Studies.gif" border="0" alt="<fmt:message key="studies" bundle="${resword}"/>" title="<fmt:message key="studies" bundle="${resword}"/>"></a></td>
							<td><a href="ListCRF?module=admin"
							   onMouseOver="javascript:setImage('subnav_Admin_CRFs','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_CRFs_h.gif');"
							   onMouseOut="javascript:setImage('subnav_Admin_CRFs','images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_CRFs.gif');"><img 
							   name="subnav_Admin_CRFs" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Admin_CRFs.gif" border="0" alt="<fmt:message key="CRFs" bundle="${resword}"/>" title="<fmt:message key="CRFs" bundle="${resword}"/>"></a></td>
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
