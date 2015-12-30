<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

 <c:import url="../include/managestudy-header.jsp"/>

<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='presetValues' class='java.util.HashMap' />

<c:set var="lockswitch" value="" />
<c:set var="lockcount" value="" />

<c:forEach var="presetValue" items="${presetValues}">
    <c:if test='${presetValue.key == "lockswitch"}'>
        <c:set var="lockswitch" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == "lockcount"}'>
        <c:set var="lockcount" value="${presetValue.value}" />
    </c:if>
</c:forEach>

<h1><span class="title_manage"><fmt:message key="lock_out_configuration" bundle="${resword}"/></span></h1>

<form action="Configure" method="post">
<jsp:include page="../include/showSubmitted.jsp" />
 

<table  cellpadding="0" border="0" style="width: 400px" class="shaded_table  table_first_column_w30">

    <tr valign="top">
        <td class="table_header_column"><fmt:message key="lockout_enabled" bundle="${resword}"/>:</td>
        <td valign="top"><div>
                        <select name=lockswitch class="formfieldM">
                                <c:choose>
                                    <c:when test="${lockswitch == 'TRUE'}">
                                        <option value='TRUE' selected>TRUE</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value='TRUE'>TRUE</option>
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${lockswitch == 'FALSE'}">
                                        <option value='FALSE' selected>FALSE</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value='FALSE'>FALSE</option>
                                    </c:otherwise>
                                </c:choose>
                        </select></div>
                        <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="lockswitch" /></jsp:include></td>
                
    </tr>

    <tr>
        <td class="table_header_column_top"><fmt:message key="number_of_failed_attempts" bundle="${resword}"/>:</td>
        <td valign="top"><div><input type="text" name="lockcount" value="<c:out value="${lockcount}"/>" size="20" class="formfieldM" />
                  </div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="lockcount" /></jsp:include></td>
              
    </tr>

	</table>
	

<input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium">
<input type="button" onclick="confirmCancel('ListUserAccounts');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>

</form>
<br><br>
<jsp:include page="../include/footer.jsp"/>
