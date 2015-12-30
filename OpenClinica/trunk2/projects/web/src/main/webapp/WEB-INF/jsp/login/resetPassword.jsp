<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<jsp:include page="../login-include/login-header.jsp"/>
<jsp:include page="../login-include/resetpwd-sidebar.jsp"/>

<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jmesa/jquery-1.3.2.min.js'/>"></script>
<jsp:useBean scope="request" id="mustChangePass" class="java.lang.String"/>

<h1><span class="title_manage"><fmt:message key="reset_password" bundle="${resword}"/></span></h1>
<jsp:include page="../login-include/login-alertbox.jsp"/>

<form action="ResetPassword" method="post">
<span class="indicates_required_field">*</span> <fmt:message key="indicates_required_field" bundle="${resword}"/><br>
<input type="hidden" name="mustChangePwd" value=<c:out value="${mustChangePass}"/> >
<!-- These DIVs define shaded box borders -->
<table   cellpadding="0" border="0" class="shaded_table   table_first_column_w30">
     <tr><td class="formlabel"><fmt:message key="old_password" bundle="${resword}"/>:</td>
     <td><div ><input type="password" name="oldPasswd" value="" class="formfieldXL">
     <span class="indicates_required_field">*</span></div>
    <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="oldPasswd"/></jsp:include>
     </tr>
    <tr><td class="formlabel"><fmt:message key="new_password" bundle="${resword}"/>:</td> 
    <td><div ><input type="password" name="passwd" value="" class="formfieldXL">
    
  <c:choose>
    <c:when test="${mustChangePass=='yes'}">
    	 <span class="indicates_required_field">*</span>
    </c:when>
    <c:otherwise>
        (<fmt:message key="leave_new_password_blank" bundle="${resword}"/>)
    </c:otherwise>
     </c:choose>
    </div>
    <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="passwd"/></jsp:include></td></tr>
 
  <tr><td class="formlabel"><fmt:message key="confirm_new_password" bundle="${resword}"/>:</td>
  <td><div ><input type="password" name="passwd1" value="" class="formfieldXL">
  <c:choose>
    <c:when test="${mustChangePass=='yes'}">
    	 <span class="indicates_required_field">*</span>
     </c:when>
    </c:choose>
    </div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="passwd1"/></jsp:include></td></tr>
    <c:if test="${mustChangePass=='yes'}">
        <tr><td class="formlabel"><fmt:message key="password_challenge_question" bundle="${resword}"/>:</td>
        <td><div >
            <select name="passwdChallengeQ" class="formfieldXL">
                <option value="" ><fmt:message key="Please_Select_One" bundle="${resword}"/></option>
                <option><fmt:message key="favourite_pet" bundle="${resword}"/></option>
                <option><fmt:message key="city_of_birth" bundle="${resword}"/></option>
                <option><fmt:message key="mother_maiden_name" bundle="${resword}"/></option>
                <option><fmt:message key="favorite_color" bundle="${resword}"/></option>
            </select>
             <span class="indicates_required_field">*</span>
        </div>
            <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="passwdChallengeQ"/></jsp:include></td>
        </tr>
        <tr><td class="formlabel"><fmt:message key="password_challenge_answer" bundle="${resword}"/>:</td>
        <td><div ><input type="text" name="passwdChallengeA" value="" class="formfieldXL">
        <span class="indicates_required_field">*</span></div>
            <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="passwdChallengeA"/></jsp:include>
            </td>
        </tr>
     </c:if>
</table>


<table border="0" cellpadding="0">
 <tr><td><input type="submit" name="submit" value="<fmt:message key="change_password" bundle="${resword}"/>" class="button_xlong"></td>
 <td><input type="button" name="exit" value="<fmt:message key="exit" bundle="${resword}"/>" class="button_medium" onclick="javascript:window.location.href='j_spring_security_logout'"></td>
 </tr>
 </table>
</form>
<jsp:include page="../login-include/login-footer.jsp"/>
