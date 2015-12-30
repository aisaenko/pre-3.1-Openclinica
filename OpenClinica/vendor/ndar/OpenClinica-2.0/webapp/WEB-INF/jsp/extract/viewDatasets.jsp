<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
		The list shows datasets for the current study/site that are currently accessible to you. Click on the icons next to the appropriate dataset to view, edit, delete, or download the dataset.
		

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>

<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="datasets" class="java.util.ArrayList"/>

<h1><span class="title_extract"><c:out value="${study.name}" />: View Datasets <a href="javascript:openDocWindow('help/3_1_viewDatasets_Help.html')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>


<%--<p><center><a href="ViewDatasets?action=owner&ownerId=<c:out value="${userBean.id}"/>">Show Only My Datasets</a> | 
<a href="ViewDatasets">Show All Datasets</a></center></p>
--%>

<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showDatasetRow.jsp" /></c:import>

<c:import url="../include/workflow.jsp">
   <c:param name="module" value="extract"/> 
</c:import>
<jsp:include page="../include/footer.jsp"/>