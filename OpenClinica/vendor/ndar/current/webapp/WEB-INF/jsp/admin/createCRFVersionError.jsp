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
<jsp:useBean scope="request" id="excelErrors" class="java.util.ArrayList"/>

<c:choose>
<c:when test="${userBean.sysAdmin}">
<h1><span class="title_Admin">
</c:when>
<c:otherwise>
<h1><span class="title_manage">
</c:otherwise>
</c:choose>Create a New CRF Version - Error Encountered</span></h1>

<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="alertbox_center">
Errors were encountered while committing to the database. None of your changes have been saved.
 Please review them and correct your data as necessary.  You may print this out for your reference.
 Please contact your administrator if you have any questions.
 Please <a href="ListCRF">go back</a> to the CRF List and submit your speadsheet again.
<br><br></div>

</div></div></div></div></div></div></div></div>
<br/>
<c:forEach var="error" items="${excelErrors}">
<span class="alert"><c:out value="${error}"/></span><br/><hr/>
</c:forEach>


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