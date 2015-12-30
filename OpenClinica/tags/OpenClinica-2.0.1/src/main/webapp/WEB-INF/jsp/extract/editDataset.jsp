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
<jsp:useBean scope='request' id='dataset' class='org.akaza.openclinica.bean.extract.DatasetBean'/>
<jsp:useBean scope='request' id='statuses' class='java.util.ArrayList' />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<c:set var="dsName" value="${dataset.name}" />
<c:set var="dsDesc" value="${dataset.description}" />
<c:set var="dsStatusId" value="${dataset.status.id}" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "dsName"}'>
		<c:set var="dsName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dsDesc"}'>
		<c:set var="dsDesc" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dsStatusId"}'>
		<c:set var="dsStatusId" value="${presetValue.value}" />
	</c:if>
</c:forEach>



<h1><span class="title_extract">Edit Dataset Properties: <c:out value='${dsName}'/></span></h1>

<p>Please update the dataset properties in the fields below.  
All fields are required.</p>
<form action="EditDataset" method="post">
<input type="hidden" name="action" value="validate"/>
<input type="hidden" name="dsId" value="<c:out value='${dataset.id}'/>"/>
<input type ="hidden" name="dsStatus" value="<c:out value='${dsStatusId}'/>">
<div style="width: 450px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table>
	<tr>		
		<td class="formlabel">Name:</td>
		<td><div class="formfieldXL_BG"><input type="text" name="dsName" value="<c:out value='${dsName}' />" class="formfieldXL"></div>
		<jsp:include page="../showMessage.jsp">
		<jsp:param name="key" value="dsName"/>
		</jsp:include>
		</td>
	</tr>
	<tr>		
		<td class="formlabel">Description:</td>
		<td>
		 <div class="formtextareaXL4_BG">
		  <textarea class="formtextareaXL4" name="dsDesc" cols="40" rows="4"><c:out value='${dsDesc}' /></textarea>
		 </div>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dsDesc"/>
		</jsp:include>
		</td>
	</tr>	
	
</table>
 </div>
</div></div></div></div></div></div></div></div>

</div>    
 
 <input type="submit" name="remove" value="Update Dataset Properties" class="button_xlong"/>		

</form>

<c:import url="../include/workflow.jsp">
   <c:param name="module" value="extract"/> 
</c:import>
<jsp:include page="../include/footer.jsp"/>