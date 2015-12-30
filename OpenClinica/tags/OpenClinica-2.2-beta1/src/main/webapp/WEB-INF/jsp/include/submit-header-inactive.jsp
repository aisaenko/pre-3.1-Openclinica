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

<html>

<head>

<title><fmt:message key="openclinica" bundle="${resword}"/></title>

  <link rel="stylesheet" href="includes/styles.css" type="text/css">
  <link rel="stylesheet" href="includes/styles2.css" type="text/css">

  <script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>
  <script type="text/JavaScript" language="JavaScript" src="includes/Tabs.js"></script>
  <script type="text/JavaScript" language="JavaScript" src="includes/CalendarPopup.js"></script>
  <script type="text/javascript"  language="JavaScript" src="includes/repetition-model/repetition-model.js"></script>
  <script type="text/JavaScript" language="JavaScript" src="includes/prototype.js"></script>
  <script type="text/JavaScript" language="JavaScript" src="includes/scriptaculous.js"></script>
  <script type="text/JavaScript" language="JavaScript" src="includes/effects.js"></script>
</head>

<body class="main_BG aka_bodywidth" onload="TabsForwardByNum(<c:out value="${tabId}"/>)">
<%--<div style="width:100%;height:100%;opacity:0.1;filter: alpha(opacity=1);">--%>
<div style="width:100%;height:100%">
<table border="0" cellpadding="0" cellspacing="0" width="100%" height=
  "100%" class="background">
<tr>
<td valign="top">
<!-- Header Table -->

<%--<SCRIPT LANGUAGE="JavaScript">

document.write('<table border="0" cellpadding="0" cellspacing="0" width="' +
               document.body.clientWidth + '" class="header">');

</script>--%>
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
						
						
						 
							<td><img name="nav_Home" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Home_i.gif" border="0" alt="<fmt:message key="home" bundle="${resworkflow}"/>" title="<fmt:message key="home" bundle="${resworkflow}"/>"></td>
         
                           
							<td><img name="nav_Submit" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/nav_Submit_i.gif" border="0" alt="<fmt:message key="submit_data" bundle="${resworkflow}"/>" title="<fmt:message key="submit_data" bundle="${resworkflow}"/>">
							 </td>
						   	
						   							 
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
                  <!-- Submit Data Sub-Navigation Row -->

                  <tr>

					<td><img src="images/spacer.gif" width="1" height="4"></td>
				</tr>
				<tr>
					<td class="subnav_inactive">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><img name="subnav_Submit_ViewAllSubjects" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Submit_ViewAllSubjects_i.gif" border="0" alt="<fmt:message key="view_all_subjects" bundle="${resworkflow}"/>" title="<fmt:message key="view_all_subjects" bundle="${resworkflow}"/>"></td>
							<td><img name="subnav_Submit_EnrollSubject" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Submit_EnrollSubject_i.gif" border="0" alt="<fmt:message key="enroll_subject" bundle="${resworkflow}"/>" title="<fmt:message key="enroll_subject" bundle="${resworkflow}"/>"></td>
							<td><img name="subnav_Submit_CreateStudyEvent" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Submit_CreateStudyEvent_i.gif" border="0" alt="<fmt:message key="add_new_study_event" bundle="${resworkflow}"/>" title="<fmt:message key="add_new_study_event" bundle="${resworkflow}"/>"></td>

						    <td><img name="subnav_Submit_ViewEvents" src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/subnav_Submit_ViewEvents_i.gif" border="0" alt="<fmt:message key="view_study_events" bundle="${resworkflow}"/>" title="<fmt:message key="view_study_events" bundle="${resworkflow}"/>"></td>   	   
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
 <!--   <img src="images/spacer.gif" width="596" height="1"/><br />-->
    <!-- End Main Navigation -->
