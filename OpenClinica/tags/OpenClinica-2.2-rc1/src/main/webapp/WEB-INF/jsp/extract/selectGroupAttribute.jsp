<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<script language="JavaScript">
<!--

function selectAll() {
    if (document.cl.all.checked) {
	  for (var i=0; i <document.cl.elements.length; i++) {		
		if (document.cl.elements[i].name.indexOf('groupSelected') != -1) {
			document.cl.elements[i].checked = true;
		}
	  }
	} else {
	  for (var i=0; i <document.cl.elements.length; i++) {		
		if (document.cl.elements[i].name.indexOf('groupSelected') != -1) {
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
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="eventlist" class="java.util.HashMap"/>

<h1><span class="title_extract">Create Dataset: Select Group Attributes</span></h1>

<P><jsp:include page="../showInfo.jsp"/></P>

<jsp:include page="createDatasetBoxes.jsp" flush="true">
<jsp:param name="selectStudyEvents" value="1"/>
</jsp:include>

<P><jsp:include page="../showMessage.jsp"/></P>

<p>Please select one CRF from the left side info panel, then select one or more items in a CRF that you would like to include to this dataset.
You may select all items in the study by going to the "View Selected Items" (hyperlink) page and clicking "Select All".
</p>
<p>You may also click Event Attributes/CRF/Subject Attributes to specify which event/CRF/subject attribute will be shown in the dataset.</p>


<form action="CreateDataset" method="post" name="cl">
<input type="hidden" name="action" value="beginsubmit"/>
<input type="hidden" name="crfId" value="0">
<input type="hidden" name="groupAttr" value="1">

   <%-- <p>
    <c:choose>
     <c:when test="${newDataset.showSubjectGroupInformation}">
       <input type="checkbox" checked name="group_information" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="group_information" value="yes">
     </c:otherwise>
    </c:choose>
    Show All Group Information for Subjects
   </p> --%>
   
   <p><input type="checkbox" name="all" value="1" 
	onClick="javascript:selectAll();"> Select All Groups</p>
	

   <%-- put in a table with metadata, tbh --%>
<div style="width: 100%">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td>&nbsp;</td>
<td class="table_header_column_top">Subject Group Class</td>
<td class="table_header_column_top">Subject Group Type</td>
<td class="table_header_column_top">Subject Groups</td>
<td class="table_header_column_top">Status</td>
<td class="table_header_column_top">Subject Assignment</td>
</tr>
   <c:forEach var='sgclass' items='${allSelectedGroups}'>
   
   <tr>
   
   <td class="table_cell">
   <c:choose>
   	<c:when test="${sgclass.selected}">
   		<input type="checkbox" checked name="groupSelected<c:out value='${sgclass.id}'/>" value="yes">
   	</c:when>
   	<c:otherwise>
   		<input type="checkbox" name="groupSelected<c:out value='${sgclass.id}'/>" value="yes">
   	</c:otherwise>
   	</c:choose>
	</td>
	
	<td class="table_cell"><c:out value="${sgclass.name}"/></td>
	
	<td class="table_cell"><c:out value="${sgclass.groupClassTypeName}"/></td>
	
	<td class="table_cell">
	<c:forEach var='group' items='${sgclass.studyGroups}' varStatus='status'>
	  <c:choose>
		<c:when test="${status.last}">
			<c:out value="${group.name}"/>&nbsp;
		</c:when>
		<c:otherwise>
			<c:out value="${group.name}"/>,&nbsp;
		</c:otherwise>
	  </c:choose>
	</c:forEach>&nbsp;</td>
	
	<td class="table_cell"><c:out value="${sgclass.status.name}"/></td>
	
	<td class="table_cell"><c:out value="${sgclass.subjectAssignment}"/></td>
	
	</tr>
   
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

<jsp:include page="../include/footer.jsp"/>