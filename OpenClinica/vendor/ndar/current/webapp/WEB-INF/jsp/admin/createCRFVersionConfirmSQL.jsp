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
<jsp:useBean scope="session" id="openQueries" class="java.util.HashMap"/>
<jsp:useBean scope='session' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>

<c:choose>
<c:when test="${userBean.sysAdmin}">
<h1><span class="title_Admin">
</c:when>
<c:otherwise>
<h1><span class="title_manage">
</c:otherwise>
</c:choose>Create a New CRF Version -  Check Item Names</span></h1>

<%--this information is to be changed--%>
<p>Item Names are <b>shared</b> across different versions of a CRF
 in OpenClinica system. The CRF version upload process automatically detects Item Names that are new to this CRF and haven't been included in previous versions of this CRF.</p>
<p>If any new Item Names were detected in the spreadsheet, they are
listed below. If an exsiting item's properties will be changed, it is listed below  as well. Names in the list
below will be added or changed in the database if you decide to continue.</p>
<p>If there are no new Item Names or changes, which means all the Items Names in 
this CRF Version already exist for this CRF, the list below will be empty.
<br><br><b>If you have made a mistake, <a href="ListCRF">please go back to the CRF List</a> and
upload your spreadsheet again.</b></p>
<br>
<form action="CreateCRFVersion" method="POST">
<input type="hidden" name="action" value="commitsql">
<input type="hidden" name="crfId" value="<c:out value="${version.crfId}"/>">
<input type="hidden" name="name" value="<c:out value="${version.name}"/>">
<c:forEach var="query" items="${openQueries}">
 <!--<font color="#3891F1"><b><c:out value="${query.key}"/>:</b></font>&nbsp;<c:out value="${query.value}"/><br/>-->
 <b><c:out value="${query.key}"/></b><br/>
<br/>
</c:forEach>

<input type="submit" name="continue" value="Submit Items" class="button_xlong">
</form>

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