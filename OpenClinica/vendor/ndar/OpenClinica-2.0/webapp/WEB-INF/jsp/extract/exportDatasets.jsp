<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>



<jsp:include page="../include/extract-refresh-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
	
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="dataset" class="org.akaza.openclinica.bean.extract.DatasetBean"/>
<jsp:useBean scope="request" id="filelist" class="java.util.ArrayList"/>
<h1><span class="title_extract">Download Data: <c:out value="${dataset.name}"/> <a href="javascript:openDocWindow('help/3_5_exportDatasets_Help.html')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">

  <tr valign="top" ><td class="table_header_column">Dataset Name:</td><td class="table_cell">
  <c:out value="${dataset.name}"/>
  </td></tr>
  <tr valign="top" ><td class="table_header_column">Dataset Description:</td><td class="table_cell">
  <c:out value="${dataset.description}"/>
  </td></tr>
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<p>To view or download data, select from the formats provided below. You may also select from the archived dataset files at the bottom of the page.</p>

<input type="hidden" name="datasetId" value="<c:out value="${dataset.id}"/>"/>
<table border="0" cellpadding="5" width="525">
<tr valign="top">
    <td class="text">
	<ul><li><a href="ExportDataset?action=html&datasetId=<c:out value="${dataset.id}"/>">View as HTML</a></li>
	<li>Download a File: 
		<ul>
		<li><a href="javascript:openDocWindow('ExportDataset?action=txt&datasetId=<c:out value="${dataset.id}"/>')">Tab-delimited Text</a></li>
		<li><a href="javascript:openDocWindow('ExportDataset?action=csv&datasetId=<c:out value="${dataset.id}"/>')">Comma-delimited Text</a></li>
		<li><a href="javascript:openDocWindow('ExportDataset?action=spss&datasetId=<c:out value="${dataset.id}"/>')">SPSS Syntax and Data</a>&nbsp;<a href="javascript:openDocWindow('help/3_6_spssSpec_Help.html')"><img src="images/bt_Help_Extract.gif" alt="Help" title="Help" border="0"></a></li>
		<li><a href="javascript:openDocWindow('ExportDataset?action=odm&datasetId=<c:out value="${dataset.id}"/>')">CDISC ODM XML Format</a>
			<ul><li>Import CDISC ODM XML into SAS using the PROC CDISC command&nbsp;<a href="javascript:openDocWindow('help/3_7_sasSpec_Help.html')"><img src="images/bt_Help_Extract.gif" alt="Help" title="Help" border="0"></a></li></ul></li>
		</ul>
	</li>
	</ul>
	</td>
</tr>

<%--<tr valign="top">
    <td class="text"><input type="radio" name="action" value="txt" checked/> Tab Delimited Text</td> 
</tr>
<tr valign="top">
    <td class="text"><input type="radio" name="action" value="csv"/> Comma Delimited Text</td> 
</tr>
<tr valign="top">
    <td class="text"><input type="radio" name="action" value="html"/> Html</td> 
</tr>
<tr valign="top">
    <td class="text"><input type="radio" name="action" value="spss"/> SPSS Formats (text)</td> 
</tr>  --%>


<%--
<tr valign="top">
    <td class="text"><input type="radio" name="action" value="excel"/> Excel</td>     
</tr>
--%>

<%--<tr align="left">
<!--	<td><input type="button" value="Download Data" onClick="sendToPage(theForm.datasetId.value,
      			theForm.action)" class="button_xlong"/></tr>-->
<td><input type="submit" value="Download Data" class="button_xlong"/></td>  			
</tr>--%>

</table>

<P><b>Note: </b>Large datasets may take a few minutes to generate in a pop-up window. 
Please disable pop-up blocking to allow the dataset to be displayed.  Additionally, if you have just entered the data, you may have to wait until the data has been placed into a database view for 
extraction.  See your system administrator for more information.

<p>If you continue to have trouble, please see Help <a href="javascript:openDocWindow('help/3_5_exportDatasets_Help.html#downloadsettings')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a> for additional tips on configuring your browser.
<%--<P>You may select all, copy and paste the text generated into a text editor or excel spreadsheet.
--%>

<p><e>Internet Explorer users</i>: If you are having trouble downloading files over an https connection, see Help <a href="javascript:openDocWindow('help/3_5_exportDatasets_Help.html#downloadsettings')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a> for information no modifying your settings to enable https file downloads.

<P>Archive of Exported Dataset Files:</P>

<c:import url="../include/showTable.jsp">
<c:param name="rowURL" value="showArchivedDatasetFileRow.jsp" />
</c:import>

<jsp:include page="../include/footer.jsp"/>