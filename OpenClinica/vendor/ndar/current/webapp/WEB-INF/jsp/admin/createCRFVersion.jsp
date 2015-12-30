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

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		  
        If you have already created a CRF using an Excel spreadsheet template, please enter the version name you specified in your spreadsheet in the 'Version' field to upload it to the system. The Version in your Excel file must exactly match what you enter in the field.

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='session' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='crfName' class='java.lang.String'/>

<c:choose>
<c:when test="${userBean.sysAdmin}">
<h1><span class="title_Admin">
</c:when>
<c:otherwise>
<h1><span class="title_manage">
</c:otherwise>
</c:choose>
Create a CRF Version for <c:out value="${crfName}"/>
</span></h1>



<p>You can download a blank OpenClinica CRF Excel spreadsheet template <a href="DownloadVersionSpreadSheet?template=1"><b>here</b></a>.</p>
<p>You may also download a set of example CRFs and instructions from the OpenClinica.org portal (OpenClinica.org user account required) <a href="http://www.openclinica.org/entities/entity_details.php?eid=151" target="_blank">here</a>.</p>

<P>* indicates required field.</P>

<form action="CreateCRFVersion" method="post">
<input type="hidden" name="action" value="confirm">
<div style="width: 450px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0">
<tr><td class="formlabel">Version:</td>
<td><div class="formfieldXL_BG"><input type="text" name="name" value="<c:out value="${version.name}"/>" class="formfieldXL"></div>
 <br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td>
<td class="formlabel">*</td></tr>
<input type="hidden" name="crfId" value="<c:out value="${version.crfId}"/>">


</table>
<br>
</div>

</div></div></div></div></div></div></div></div>
</div>
<input type="submit" value="Confirm Version" class="button_long">
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
