<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="respage"/>


 <c:import url="../include/managestudy-header.jsp"/>



<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->

<tr id="sidebar_Instructions_open" style="display: all">

		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open');
		leftnavExpand('sidebar_Instructions_closed');">
            <img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
		  <b><fmt:message key="create_CRF" bundle="${resword}"/> : </b>
		  <fmt:message key="br_create_new_CRF_entering" bundle="${respage}"/><br/><br/>
		  <b><fmt:message key="create_CRF_version" bundle="${resword}"/> : </b>
		  <fmt:message key="br_create_new_CRF_uploading" bundle="${respage}"/><br/><br/>
		  <b><fmt:message key="revise_CRF_version" bundle="${resword}"/> : </b>
		  <fmt:message key="br_if_you_owner_CRF_version" bundle="${respage}"/><br/><br/>
		  <b><fmt:message key="CRF_spreadsheet_template" bundle="${resword}"/> : </b>
		  <fmt:message key="br_download_blank_CRF_spreadsheet_from" bundle="${respage}"/><br/><br/>
		  <b><fmt:message key="example_CRF_br_spreadsheets" bundle="${resword}"/> : </b>
          <fmt:message key="br_download_example_CRF_instructions_from" bundle="${respage}"/><br/>
		  
		
		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open');
		leftnavExpand('sidebar_Instructions_closed');">
            <img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='session' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>
<jsp:useBean scope='request' id='crfName' class='java.lang.String'/>

<h1><span class="title_manage">
 <c:choose>
     <c:when test="${empty crfName}">
         <fmt:message key="create_a_new_CRF_case_report_form" bundle="${resworkflow}"/>
     </c:when>
     <c:otherwise>
        <fmt:message key="create_CRF_version" bundle="${resworkflow}"/> <c:out value="${crfName}"/>
     </c:otherwise>
 </c:choose>

</span>
</h1>

<script type="text/JavaScript" language="JavaScript">
  <!--
 function myCancel() {

    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {
       window.location.href="ListCRF?module=" + "<c:out value="${module}"/>";
       return true;
      } else {
        return false;
       }
     }
     return true;

  }

function submitform(){
    var crfUpload = document.getElementById('excel_file_path');
    //Does the user browse or select a file or not
    if (crfUpload.value =='' )
    {
        alert("Select a file to upload!");
        return false;
    }
}

   //-->
</script>

<p><fmt:message key="can_download_blank_CRF_excel" bundle="${restext}"/><a href="DownloadVersionSpreadSheet?template=1"><b><fmt:message key="here" bundle="${resword}"/></b></a>.</p>

<div style="font-family: Tahoma, Arial, Helvetica, Sans-Serif;font-size:12px;">
    <p><fmt:message key="openclinica_excel_support" bundle="${restext}"/></p>

 </div>

<%--
<p><fmt:message key="also_download_set_example_CRFs" bundle="${restext}"/><a href="http://www.openclinica.org/entities/entity_details.php?eid=151" target="_blank"><fmt:message key="here" bundle="${resword}"/></a>.</p>
--%>



<form action="CreateCRFVersion?action=confirm&crfId=<c:out value="${version.crfId}"/>&name=<c:out value="${version.name}"/>" method="post" ENCTYPE="multipart/form-data">
<table width="700" cellpadding="0" border="0" class="shaded_table   table_first_column_w30   ">
<tr>
<td class="formlabel"><fmt:message key="ms_excel_file_to_upload" bundle="${resword}"/>:</td>
<td><div >&nbsp;&nbsp;<input type="file" name="excel_file" id="excel_file_path"></div>
<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="excel_file"/></jsp:include></td>
</tr>
<input type="hidden" name="crfId" value="<c:out value="${version.crfId}"/>">


</table>


<br clear="all">

<input type="submit" onclick="return submitform();" value="<fmt:message key="preview_CRF_version" bundle="${resword}"/>" class="button_long">

<input type="button" onclick="confirmExit('ListCRF?module=<c:out value="${module}"/>')" name="exit" value="<fmt:message key="exit" bundle="${resword}"/>" class="button_medium"/>

</form>



<jsp:include page="../include/footer.jsp"/>
