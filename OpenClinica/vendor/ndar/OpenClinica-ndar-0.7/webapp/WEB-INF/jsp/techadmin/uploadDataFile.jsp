<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:import url="../include/tech-admin-header.jsp"/>
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

<h1><span class="title_TechAdmin">
Upload a Data Spreadsheet File
</span></h1>

<p>Please click "Browse" button below to find the excel spreadsheet on your machine:</p>
<form action="DataImporting?submitted=1&crfId=<c:out value="${crfId}"/>&versionId=<c:out value="${versionId}"/>" method="post" ENCTYPE="multipart/form-data">
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td class="formlabel">MS Excel Data File To Upload:</td>
<td><div class="formfieldFile_BG"><input type="file" name="excel_file" class="formfieldM"></div></td>
</tr>
</table>
 </div>
</div></div></div></div></div></div></div></div>
</div>
<input type="submit" name="submit" value="Upload Data File" class="button_xlong">
</form>

<c:forEach var="error" items="${errors}">
    <span class="alert"><c:out value="${error}"/></span><br />
</c:forEach>
  
  <c:import url="../include/workflow.jsp">
   <c:param name="module" value="techAdmin"/> 
  </c:import>
 
<jsp:include page="../include/footer.jsp"/>
