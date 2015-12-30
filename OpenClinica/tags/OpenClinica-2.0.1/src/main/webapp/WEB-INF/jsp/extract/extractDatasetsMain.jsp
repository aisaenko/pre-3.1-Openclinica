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
		Datasets aggregate subject data from your study into a single file.  
For the current study/site you may download data from previously created datasets
 or create your own dataset. <!-- You may create filters to limit your solutions and
 apply them to your dataset.--> Data are available in multiple formats - browsable online as a webpage, or as a downloadable file in a number of different formats.
<br><br>
Listed below are the five most recently created Datasets.  Click 'View All' to see all the datasets
for this study.  The icons next to each dataset allow you to view information
about the dataset, edit its properties, delete it, and download new or archived
versions of the data.

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

<h1><span class="title_extract"><c:out value="${study.name}" />: Extract Datasets <a href="javascript:openDocWindow('help/3_0_extractData_Help.html')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></h1>

<OL>
<LI><a href="ViewDatasets">View Datasets</a>
<LI><a href="CreateDataset">Create Dataset</a>
<!--<LI><a href="CreateFiltersOne">View Filters</a>-->
<!--<LI><a href="CreateFiltersOne?action=begin&submit=Create+New+Filter">Create Filter</a>-->
</OL>


<c:import url="../include/showTable.jsp">
<c:param name="rowURL" value="showDatasetRow.jsp" />
</c:import>

<c:import url="../include/workflow.jsp">
   <c:param name="module" value="extract"/> 
</c:import>
 
<jsp:include page="../include/footer.jsp"/>