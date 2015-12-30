<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
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

<jsp:include page="../include/createDatasetSideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='newDataset' class='org.akaza.openclinica.bean.extract.DatasetBean'/>


<h1><span class="title_extract">Create Dataset: Confirm Dataset Properties</span></h1>

<%--
<jsp:include page="createDatasetBoxes.jsp" flush="true">
<jsp:param name="saveAndExport" value="1"/>
</jsp:include>
--%>
<p>Please confirm the dataset properties below, or click "Back" to return to the previous screen.

<form action="CreateDataset" method="post">
<input type="hidden" name="action" value="confirmall" />

<table>
	<tr>
		<td class="text">Name</td>
		<td class="text"><b><c:out value="${newDataset.name}" /></b>
	</tr>
	<tr>
		<td class="text" valign="top">Description</td>
		<td class="text" valign="top"><b><c:out value="${newDataset.description}" /></b></td>
	</tr>
	<%--<tr>
		<td class="text">Events Sample From:</td>
		<td class="text"><b>
		   <c:choose>
		   <c:when test="${defaultStart==newDataset.dateStart}">
		    Not specified
		   </c:when>
		   <c:otherwise>
		   <fmt:formatDate value="${newDataset.dateStart}" pattern="MM/dd/yyyy"/>
		   </c:otherwise>
		   </c:choose>
		</b>
	</tr>
	<tr>
		<td class="text">Events Sample To:</td>
		<td class="text"><b>
		<c:choose>
		   <c:when test="${defaultEnd==newDataset.dateEnd}">
		   Not specified 
		   </c:when>
		   <c:otherwise>
		   <fmt:formatDate value="${newDataset.dateEnd}" pattern="MM/dd/yyyy"/>	   
		   
		   </c:otherwise>
		   </c:choose>
		</b>
	</tr>
	<tr>
		<td class="text">Status</td>
		<td class="text"><b><c:out value="${newDataset.status.name}" /></b>
	</tr>--%>
	<tr>
		<td colspan="2" align="left">			
		  <input type="submit" name="btnSubmit" value="Confirm and Save" class="button_xlong"/>			
		</td>
		<td>
		  <input type="submit" name="btnSubmit" value="Back" class="button_xlong"/>
		</td>
	</tr>
</table>

</form>

<c:import url="../include/workflow.jsp">
   <c:param name="module" value="extract"/> 
</c:import>
<jsp:include page="../include/footer.jsp"/>