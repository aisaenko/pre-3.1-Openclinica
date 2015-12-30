<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>OpenClinica</title>

<meta http-equiv="Content-type" content="text/html; charset=UTF-8"/>
<link rel="stylesheet" href="<c:url value='/includes/styles.css'/>" type="text/css"/>
<link rel="stylesheet" href="<c:url value='/includes/styles2.css'/>" type="text/css" />
<link rel="stylesheet" href="<c:url value='/includes/NewLoginStyles.css'/>" type="text/css"/>

<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/global_functions_javascript2.js'/>"></script>
<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/global_functions_javascript.js'/>"></script>

</head>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<body class="login_BG">
    <div class="login_BG">
    <center>

    <!-- OpenClinica logo -->
    <div ID="OClogo">&nbsp;</div>
    <!-- end OpenClinica logo -->

    <table border="0" cellpadding="0" cellspacing="0" class="loginBoxes">
        <tr>
            <td class="loginBox_T">&nbsp;</td>
            <td class="loginBox_T">&nbsp;</td>
       </tr>
       <tr>
            <td class="loginBox">
            <div ID="loginBox">
            <!-- Login box contents -->
                <div ID="login">
                    <form action="<c:url value='/j_spring_security_check'/>" method="post">
                    <h1><fmt:message key="login" bundle="${resword}"/></h1>
                    <b><fmt:message key="user_name" bundle="${resword}"/></b>
                        <div class="formfieldM_BG">
                            <input type="text" id="username" name="j_username" class="formfieldM">
                        </div>
        
                    <b><fmt:message key="password" bundle="${resword}"/></b>
                        <div class="formfieldM_BG">
                            <input type="password" id="j_password" name="j_password"  class="formfieldM">
                        </div>  
                    <input type="submit" name="submit" value="<fmt:message key='login' bundle='${resword}'/>" class="loginbutton" />
                    <a href="#"> <fmt:message key="forgot_password" bundle="${resword}"/></a>
                   </form>
                   <br/><jsp:include page="../login-include/login-alertbox.jsp"/>
                   <%-- <a href="<c:url value="/RequestPassword"/>"> <fmt:message key="forgot_password" bundle="${resword}"/></a> --%>
               </div>
            <!-- End Login box contents -->
            </div>
            </td>
            <td class="loginBox">
            <div ID="newsBox">
                <!-- News box contents -->
                <h1>News</h1>
                <ul>
                    <li><a href="#">May 26-30 - OpenClinica at International Conference on Global Health ...</a></li>
                    <li><a href="#">4/29/09 - OpenClinica Enterprise Selected for the Abuja Heart Study ...</a></li>
                    <li><a href="#">4/22/09 - Akaza Research to Present OpenClinica at ACRP Global Conference and Exhibition ...</a></li>
                    <li><a href="#">3/12/09 - IND Deploys OpenClinica EDC solution for Large Parkinson Study ...</a></li>
                </ul>
                <!-- End News box contents -->
            </div>
            </td>
      </tr>
    </table>
    
    </center>
    <script type="text/javascript">
        document.getElementById('username').setAttribute( 'autocomplete', 'off' );
        document.getElementById('j_password').setAttribute( 'autocomplete', 'off' );
    </script>

<!-- Footer -->
<!-- End Main Content Area -->
<jsp:include page="../login-include/login-footer.jsp"/>