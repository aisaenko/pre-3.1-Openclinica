<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<html>
<head>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
<script language="JavaScript" src="includes/global_functions_javascript.js"></script>
<style type="text/css">

.popup_BG { background-image: url(images/main_BG.gif);
	background-repeat: repeat-x;
	background-position: top;
	background-color: #FFFFFF;
	}


</style>

</head>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="dataset" class="org.akaza.openclinica.bean.extract.DatasetBean"/>

<script language="JavaScript">
<!--

function refreshMe() {
	var theUrl = "ExportDataset?" + 		
		"datasetId=" + 
		<c:out value="${param.datasetId}"/>;
	window.opener.location=theUrl;
	window.opener.location.reload(true);
	
}


//-->
</script>
<body onload="refreshMe()" class="popup_BG">

<!-- for use with extract-refresh-header.jsp only  -->

<%--<div id="waitpage"> 

<table><tr><td class="title_extract">Page loading ... Please wait.</td></tr></table>

</div>--%> 

<!-- put the rest of your page contents here -->
<!-- work on putting csv, etc into web page tomorrow? -->
<!--<div id="mainpage">-->
	
<h1><span class="title_extract">Download Data: <c:out value="${dataset.name}"/>  <a href="javascript:openDocWindow('help/3_5_exportDatasets_Help.html')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>

<P><jsp:include page="../showInfo.jsp"/></P>

<p>
<b>Dataset Name:</b> <c:out value="${dataset.name}"/><br>
<b>Description:</b> <c:out value="${dataset.description}"/></p>

<%--<c:import url="../include/showTable.jsp">
<c:param name="rowURL" value="showArchivedDatasetFileRow.jsp" />
</c:import>
--%>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center">
<table><tr><td>
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
 <tr valign="top">
 <td class="table_header_row">File Name</td>
 <td class="table_header_row">Run Time</td>
 <td class="table_header_row">File Size</td>
 <td class="table_header_row">Created Date</td>
 <td class="table_header_row">Created By</td>					
</tr> 
<c:choose>
	<c:when test="${empty table.rows}">
		<tr>
		 <td colspan="<c:out value="${i}" />">
			<c:choose>
			  <c:when test='${table.noRowsMessage == ""}'>
					There are no rows to display.					
			  </c:when>
			  <c:otherwise>
				<c:out value="${table.noRowsMessage}" />
			  </c:otherwise>
			</c:choose>
		 </td>
		</tr>
	 </c:when>
	 <c:otherwise>
		<!-- Data -->
		 <c:set var="eblRowCount" value="${0}" />
					
			<c:forEach var="row" items="${table.rows}">
			<c:set var="currRow" scope="request" value="${row}" /> 	
			<tr>
	         <td class="table_cell">
		         <a target="_new" href="AccessFile?fileId=<c:out value="${currRow.bean.id}"/>">
			       <c:out value="${currRow.bean.name}" />
		         </a>
	         </td>
	          <td class="table_cell"><c:out value="${currRow.bean.runTime}" /></td>
	          <td class="table_cell"><c:out value="${currRow.bean.fileSize}" /></td>
	          <td class="table_cell"><fmt:formatDate value="${currRow.bean.dateCreated}" pattern="MM/dd/yyyy"/></td>
	          <td class="table_cell"><c:out value="${currRow.bean.owner.name}" /></td>
           </tr>
		   </c:forEach>			
		<!-- End Data -->
		 </c:otherwise>
	</c:choose>
</table>
</td></tr></table>
</div>
</div></div></div></div></div></div></div></div>
</div>
<p><i>Internet Explorer users</i>: If you are having trouble downloading files over an https connection, see Help <a href="javascript:openDocWindow('help/3_5_exportDatasets_Help.html#downloadsettings')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a> for information no modifying your settings to enable https file downloads.


<!--</div>-->
<%--<jsp:include page="generateMetadataCore.jsp"/>--%>
</body>
</html>