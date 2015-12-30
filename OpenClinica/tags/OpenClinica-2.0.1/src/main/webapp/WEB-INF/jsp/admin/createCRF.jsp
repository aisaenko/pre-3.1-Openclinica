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
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		  
        If you have already created a CRF using an Excel spreadsheet template, please enter the CRF name you specified in your spreadsheet in the 'CRF Name' field below to upload it to the system. The CRF Name in your Excel file must exactly match what you enter in the field below.<br><br>

       
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

<jsp:useBean scope='session' id='crf' class='org.akaza.openclinica.bean.admin.CRFBean'/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>

<c:choose>
<c:when test="${userBean.sysAdmin}">
<h1><span class="title_Admin">
</c:when>
<c:otherwise>
<h1><span class="title_manage">
</c:otherwise> 
</c:choose>
Create a New Case Report Form (CRF)
</span></h1>
<p>You can download a blank OpenClinica CRF Excel spreadsheet template <a href="DownloadVersionSpreadSheet?template=1"><b>here</b></a>.</p>
 <p>You may also download a set of example CRFs and instructions from the OpenClinica.org portal (OpenClinica.org user account required) <a href="http://www.openclinica.org/entities/entity_details.php?eid=151" target="_blank">here</a>.
 The CRF Name is case sensitive and must match the name of the CRF in your CRF Excel spreadsheet.</p>

<P> 
<P>* indicates required field.</P>
<form action="CreateCRF" method="post">
<input type="hidden" name="action" value="confirm">
<!-- These DIVs define shaded box borders -->
<div style="width: 450px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">
  <tr valign="top"><td class="formlabel">CRF Name:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="name" value="<c:out value="${crf.name}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td><td class="formlabel">*</td></tr>
  
  <tr valign="top"><td class="formlabel">CRF Description:</td><td>
  <div class="formtextareaXL4_BG">
  <textarea class="formtextareaXL4" name="description" rows="4" cols="50"><c:out value="${crf.description}"/></textarea>
  </div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include></td><td class="formlabel">*</td></tr>
 
</table>
  </div>
</div></div></div></div></div></div></div></div>

</div>    
</div>     
  <input type="submit" name="Submit" value="Confirm CRF" class="button_long">
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
