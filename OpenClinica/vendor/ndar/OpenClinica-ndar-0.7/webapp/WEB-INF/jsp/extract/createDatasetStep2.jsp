<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<script language="JavaScript" type="text/javascript">
<!--

function selectAll() {
    if (document.cl.all.checked) {
	  for (var i=0; i <document.cl.elements.length; i++) {		
		if (document.cl.elements[i].name.indexOf('itemSelected') != -1) {
			document.cl.elements[i].checked = true;
		}
	  }
	} else {
	  for (var i=0; i <document.cl.elements.length; i++) {		
		if (document.cl.elements[i].name.indexOf('itemSelected') != -1) {
			document.cl.elements[i].checked = false;
		}
	  }
	}
}
function notSelectAll() {
	if (!this.checked){
		document.cl.all.checked = false;
    }

}
//-->
</script>


<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<%--<jsp:include page="../include/sidebar.jsp"/>--%>
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
<jsp:useBean scope="request" id="eventlist" class="java.util.HashMap"/>

<h1><span class="title_extract">Create Dataset: Select Items <a href="javascript:openDocWindow('help/3_2_createDataset_Help.html#step1')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>

<%--
<jsp:include page="createDatasetBoxes.jsp" flush="true">
<jsp:param name="selectStudyEvents" value="1"/>
</jsp:include>
--%>

<p>Please select one CRF from the <b>left side info panel</b>, then select one or more items in a CRF that you would like to include to this dataset.
You may select all items in the study by going to the "View Selected Items" (hyperlink) page and clicking "Select All". </p>
<p>You may also click Event Attributes/Subject Attributes to specify which event/subject attribute will be shown in the dataset.</p>
<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td><img src="images/arrow_left.gif" alt="Select CRF on the Left" title="Select CRF on the Left"></td>
    <td>
      <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
		<div class="textbox_center" align="center">
			<span class="title_extract">
				 <b>Use task pane on the left side to select CRFs</b>	
			</span>
		</div>

	</div></div></div></div></div></div></div></div>      
   
    </td>
  </tr>
</table>

<c:if test="${crf != null && crf.id>0}">

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr valign="top" ><td class="table_header_column">Event Name:</td><td class="table_cell">  
  <c:out value="${definition.name}"/>
   </td></tr>
 <tr valign="top" ><td class="table_header_column">CRF Name:</td><td class="table_cell">  
  <c:out value="${crf.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column">Description:</td><td class="table_cell">  
  <c:out value="${crf.description}"/>
  </td></tr> 
 </table>
</div>

</div></div></div></div></div></div></div></div>
</div>

</c:if>
<br>
<c:if test="${!empty allItems}">
<form action="CreateDataset" method="post" name="cl">
<input type="hidden" name="action" value="beginsubmit"/>
<input type="hidden" name="crfId" value="<c:out value="${crf.id}"/>">
<input type="hidden" name="defId" value="<c:out value="${definition.id}"/>">

<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="all"/></jsp:include>
<input type="checkbox" name="all" value="1" 
	onClick="selectAll();"> Select All Items<br/>
<P><B>Show the following items in this dataset:</b></p>
<!--javascript to select all-->
<div style="width: 100%">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr>
    <td class="table_header_column_top">&nbsp;</td>
    <td class="table_header_column_top">Name</td>          
    <td class="table_header_column_top">Description</td> 
    <td class="table_header_column_top">Version(s)</td>
    <td class="table_header_column_top">Data Type</td>
    <td class="table_header_column_top">Units</td>      
    <td class="table_header_column_top">Response Label</td> 
    <td class="table_header_column_top">PHI</td>  
  </tr>	
<c:set var="count" value="0"/>
<c:forEach var='item' items='${allItems}'>  
  <tr>
   <td class="table_cell">   
   <c:choose>
    <c:when test="${item.selected}">
      <input type="checkbox" name="itemSelected<c:out value="${count}"/>" checked value="yes" onclick="notSelectAll();">
    </c:when>
    <c:otherwise>
      <input type="checkbox" name="itemSelected<c:out value="${count}"/>" value="yes" onclick="notSelectAll();">
    </c:otherwise>
   </c:choose>
   </td>
   <td class="table_cell"><a href="javascript: openDocWindow('ViewItemDetail?itemId=<c:out value="${item.id}"/>')"><c:out value="${item.name}"/></a></td>
   <td class="table_cell"><c:out value="${item.description}"/>&nbsp;</td>
   <td class="table_cell">
      <c:forEach var="meta" items="${item.itemMetas}" varStatus="status">
        <c:choose>
          <c:when test="${status.last}">
           <c:out value="${meta.crfVersionName}"/>
          </c:when>
          <c:otherwise>
           <c:out value="${meta.crfVersionName}"/>,<br>
          </c:otherwise> 
        </c:choose> 
      </c:forEach>
    </td> 
   <td class="table_cell"><c:out value="${item.dataType.name}"/>&nbsp;</td>     
   <td class="table_cell"><c:out value="${item.units}"/>&nbsp;</td>  
    <td class="table_cell">
      <c:forEach var="meta" items="${item.itemMetas}" varStatus="status">
        <c:choose>
          <c:when test="${status.last}">
           <c:out value="${meta.responseSet.label}"/>
          </c:when>
          <c:otherwise>
            <c:out value="${meta.responseSet.label}"/>,<br>
          </c:otherwise> 
        </c:choose> 
      </c:forEach>
    </td>      
    <td class="table_cell">
     <c:choose>
      <c:when test="${item.phiStatus}">
        Yes
      </c:when>
      <c:otherwise>
        No
      </c:otherwise>
    </c:choose>
   </td>  
  </tr>
  <c:set var="count" value="${count+1}"/>
</c:forEach>
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<table border="0" cellpadding="0" cellspacing="0" >
  <tr>
   <td><input type="submit" name="save" value="Save and Add More Items" class="button_xlong"/></td>
   <td><input type="submit" name="saveContinue" value="Save and Define Scope" class="button_xlong"/></td>
  </tr>
</table>
</form>
</c:if>

<c:import url="../include/workflow.jsp">
   <c:param name="module" value="extract"/> 
</c:import>
<jsp:include page="../include/footer.jsp"/>