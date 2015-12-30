<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<table border="0" cellpadding=0" cellspacing="0" style="position: relative; top: -21px;">
  <tr>
	<td valign="top">
	
<table border="0" cellpadding="0" cellspacing="0" width="160">
<!-- Side alert, onlu show the content after user logs in -->

 <c:if test="${userBean != null && userBean.id>0}">	 
  <c:choose>
  
    <c:when test="${!empty pageMessages}">

	<tr id="sidebar_Alerts_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Alerts_open'); leftnavExpand('sidebar_Alerts_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Alerts & Messages</b>

		<div class="sidebar_tab_content">

			<i>	
			<c:choose>
			<c:when test="${userBean!= null && userBean.id>0}">             
            <jsp:include page="../include/showSideMessage.jsp" />
            </c:when>
            <c:otherwise>             
            You have logged out of the application. Please go to the <a href="MainMenu">Login Page</a> in order to re-enter OpenClinica.          
		
            </c:otherwise>
            </c:choose>
            </i>

			<br>

			<!--<a href="#">View Log</a>-->

		</div>

		</td>
	</tr>
	<tr id="sidebar_Alerts_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Alerts_open'); leftnavExpand('sidebar_Alerts_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Alerts & Messages</b>

		</td>
	</tr>
    </c:when>
    <c:otherwise>
       <tr id="sidebar_Alerts_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Alerts_open'); leftnavExpand('sidebar_Alerts_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Alerts & Messages</b>

		<div class="sidebar_tab_content">

			<i>	
			<c:choose>
			<c:when test="${userBean!= null && userBean.id>0}">             
            <jsp:include page="../include/showSideMessage.jsp" />
            </c:when>
            <c:otherwise>             
            You have logged out of the application. Please go to the <a href="MainMenu">Login Page</a> in order to re-enter OpenClinica.          
		
            </c:otherwise>
            </c:choose>
            </i>

		</div>

		</td>
	</tr>
	<tr id="sidebar_Alerts_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Alerts_open'); leftnavExpand('sidebar_Alerts_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Alerts & Messages</b>

		</td>
	</tr>
    </c:otherwise>	
  </c:choose>	
</c:if>
	