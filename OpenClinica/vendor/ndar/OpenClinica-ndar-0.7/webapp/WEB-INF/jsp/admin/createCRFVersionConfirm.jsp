<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:choose>
<c:when test="${userBean.sysAdmin}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/managestudy-header.jsp"/>
</c:otherwise> 
</c:choose>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		  
       
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='excelErrors' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='htmlTable' class='java.lang.String'/>
<jsp:useBean scope='session' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>


<c:choose>
<c:when test="${userBean.sysAdmin}">
<h1><span class="title_Admin">
</c:when>
<c:otherwise>
<h1><span class="title_manage">
</c:otherwise>
</c:choose>Check CRF Version Data</span></h1>


<c:choose>
<c:when test="${empty excelErrors}"> 
 <c:if test="${!empty warnings}">
  <p>WARNINGS:<p>
  <c:forEach var="warning" items="${warnings}">
    <span class="alert"><c:out value="${warning}"/></span></br>
  </c:forEach>
 </c:if>
<br/><b>Congratulations!</b>  Your spreadsheet generated no errors.
<br/><b>
Please review the information below. 
<form action="CreateCRFVersion?action=confirmsql&crfId=<c:out value="${version.crfId}"/>&name=<c:out value="${version.name}"/>" method="post">
 <input type="submit" name="submit" value="click here to continue" class="button_xlong"></form></b>
<br/>

<%=htmlTable%>

</c:when>
<c:otherwise>
<br/>There were several invalid fields in your spreadsheet.  Please take a look at the data below, <br>
click <input type="submit" name="submit" value="Go Back" class="button" onclick="history.go(-1)"> to go back and upload your corrected speadsheet for the CRF.
<br/>
<c:forEach var="error" items="${excelErrors}">
<span class="alert"><c:out value="${error}"/></span></br>
</c:forEach>
<br>
<%=htmlTable%>

</c:otherwise>
</c:choose>

<c:choose>
  <c:when test="${userBean.sysAdmin}">
  <c:import url="../include/workflow.jsp">
   <c:param name="module" value="admin"/> 
  </c:import>
 </c:when>
  <c:otherwise>
   <c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/> 
  </c:import>
  </c:otherwise> 
 </c:choose> 

<jsp:include page="../include/footer.jsp"/>