<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

<script type="text/javascript" language="JavaScript">
  <!--
  function checkOverwriteStatus() {
      //return confirm('<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>');
      return confirm('You will overwrite event CRFs that have already been saved in the database.  Do you want to continue?');
  }
 //-->
</script>

<c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/submit-header.jsp"/>
</c:otherwise> 
</c:choose>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
		<div class="sidebar_tab_content">		  
			<fmt:message key="import_side_bar_instructions" bundle="${restext}"/>
		</div>
	</td>
</tr>

<tr id="sidebar_Instructions_closed" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
	</td>
</tr>



<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='session' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='importedData' class='java.util.ArrayList'/>
<jsp:useBean scope='session' id='crfName' class='java.lang.String'/>

<%-- <c:out value="${crfName}"/> --%>

<c:choose>
	<c:when test="${userBean.sysAdmin && module=='admin'}">
		<h1><span class="title_Admin">
	</c:when>
	<c:otherwise>
		<h1>
		<span class="title_manage">
	</c:otherwise>
</c:choose>

<fmt:message key="import_crf_data" bundle="${resworkflow}"/></h1>
<p><fmt:message key="import_instructions" bundle="${restext}"/></p>



<form action="VerifyImportedCRFData?action=save" method="POST">

 <div class="box_T"><div class="box_L">
<div class="box_R"><div class="box_B">
<div class="box_TL"><div class="box_TR">
<div class="box_BL"><div class="box_BR">
<div class="tablebox_center">

<table border="0" cellpadding="0" cellspacing="0">

				
<tr valign="top">
	<td class="table_header_row_left">
	<fmt:message key="study_event_ID" bundle="${resword}"/>
	<%-- Study Event Name--%></td>
	<td class="table_header_row"><fmt:message key="CRF_version" bundle="${resword}"/></td> 
	<td class="table_header_row"><fmt:message key="study_subject" bundle="${resword}"/></td>
	<td class="table_header_row"><fmt:message key="date_started" bundle="${resword}"/></td>  
	<td class="table_header_row"><fmt:message key="import_error" bundle="${resword}"/></td> 
</tr>

<c:set var="overwriteCount" value="${0}" />
<c:forEach var="displayItemBeanWrapper" items="${importedData}" >
<c:set var="rowCount" value="${0}" />

<c:if test="${displayItemBeanWrapper.overwrite}">
	<c:set var="overwriteCount" value="${overwriteCount + 1}" />
</c:if>

<tr valign="top">   
	<td class="table_cell_left">
		<c:out value="${displayItemBeanWrapper.studyEventId}" />
	</td> 
	<td class="table_cell">
		<c:out value="${displayItemBeanWrapper.crfName}" />&nbsp;<c:out value="${displayItemBeanWrapper.crfVersionName}" />
	</td>
	<td class="table_cell">
		<c:out value="${displayItemBeanWrapper.studySubjectName}" />
	</td> 
	<td class="table_cell">
		<fmt:formatDate value="${displayItemBeanWrapper.dateOfEvent}" dateStyle="short"/>
	</td> 
	<td class="table_cell">
		<c:forEach var="validationErrorKey" items="${displayItemBeanWrapper.validationErrors}" varStatus="status">
			<font color="red"><c:out value="${validationErrorKey.key}" />:</font> <c:out value="${validationErrorKey.value}" /><br/>
			<c:set var="rowCount" value="${rowCount + 1}" />
		</c:forEach>
		<c:if test="${rowCount == 0}">
			<font color="green">Valid</font>
		</c:if>
	</td>
</tr>
</c:forEach>





</table>

</div>
</div></div>
</div></div>
</div></div>
</div></div>

<br clear="all"> 
<input type="hidden" name="crfId" value="<c:out value="${version.crfId}"/>">
<c:if test="${overwriteCount == 0}">
	<input type="submit" value="Continue" class="button_long">
</c:if>
<c:if test="${overwriteCount > 0 }">
	<input type="submit" value="Continue" class="button_long" onClick="return checkOverwriteStatus();">
</c:if>
</form>
<%-- add an alert here --%>
<form action="ListStudySubjectsSubmit">
<input type="submit" value="Cancel" class="button_long">
</form>
<c:choose>
  <c:when test="${userBean.sysAdmin && module=='admin'}">
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