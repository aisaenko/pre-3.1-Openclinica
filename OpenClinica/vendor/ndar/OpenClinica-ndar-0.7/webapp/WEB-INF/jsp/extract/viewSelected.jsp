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
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="eventlist" class="java.util.HashMap"/>

<h1><span class="title_extract">Create Dataset: View Selected Items <a href="javascript:openDocWindow('help/3_2_createDataset_Help.html#step1')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>

<P><jsp:include page="../showInfo.jsp"/></P>

<jsp:include page="createDatasetBoxes.jsp" flush="true">
<jsp:param name="selectStudyEvents" value="1"/>
</jsp:include>

<P><jsp:include page="../include/alertbox.jsp"/></P>

<p>Below you can view all items selected for inclusion in the dataset. You may select all items in the study for inclusion by clicking the "Select All Items in Study" box . Be aware
that if your study is complex by doing so you may be selecting hundreds or thousands of data elements for inclusion in your dataset.
You may also deselect individual items by unchecking the box and clicking one of the buttons below.</p>

<br>
<form action="EditSelected" method="post" name="cl">
 <input type="hidden" name="all" value="1"> 
 <input type="submit" name="submit" value="Select All Items in Study" class="button_xlong" onClick='return confirm("There are a total of <c:out value="${numberOfStudyItems}"/> items in this study. Are you sure you want to include all of them?");'><br/>
</form>
<c:if test="${!empty allSelectedItems}">
<form action="CreateDataset" method="post" name="cl">
<input type="hidden" name="action" value="beginsubmit"/>
<input type="hidden" name="crfId" value="-1">
<P><B>Show the following items in this dataset:</b></p>
<table border="0" cellpadding="0" cellspacing="0" >
  <tr>
   <td><input type="submit" name="save" value="Save and Add More Items" class="button_xlong"/></td>
   <td><input type="submit" name="saveContinue" value="Save and Define Scope" class="button_xlong"/></td>
  </tr>
</table>
<br>

<span class="table_title_extract">Subject and Event Attributes</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
 <td class="table_header_column_top">Event Location</td>
 <td class="table_header_column_top">Start Date</td>
 <td class="table_header_column_top">End Date</td>
 <td class="table_header_column_top"> 
   <c:choose>
    <c:when test="${study.studyParameterConfig.collectDob == '1'}">
     Date Of Birth
    </c:when>
    <c:when test="${study.studyParameterConfig.collectDob == '3'}">
     Date Of Birth
    </c:when>
    <c:otherwise>
     Year of Birth
    </c:otherwise> 
   </c:choose>
 </td>
 <td class="table_header_column_top">Gender</td>
 </tr>
 <tr>
 <td class="table_cell"><c:choose>
     <c:when test="${newDataset.showEventLocation}">
       <input type="checkbox" checked name="location" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="location" value="yes">
     </c:otherwise>
    </c:choose>    
   </td>   
   <td class="table_cell">
   <c:choose>
     <c:when test="${newDataset.showEventStart}">
       <input type="checkbox" checked name="start" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="start" value="yes">
     </c:otherwise>
    </c:choose>
   </td>  
   <td class="table_cell"> 
     <c:choose>
     <c:when test="${newDataset.showEventEnd}">
       <input type="checkbox" checked name="end" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="end" value="yes">
     </c:otherwise>
    </c:choose>   
   </td>
 <td class="table_cell">
 <c:choose>
     <c:when test="${newDataset.showSubjectDob}">
       <input type="checkbox" checked name="dob" value="yes">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="dob" value="yes">
     </c:otherwise>
   </c:choose>  
 </td>
 <td class="table_cell">
  <c:choose>
     <c:when test="${newDataset.showSubjectGender}">
       <input type="checkbox" checked name="gender" value="yes">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="gender" value="yes">
     </c:otherwise>
   </c:choose> 
  
 </td>
</tr>

</table>
</div>
</div></div></div></div></div></div></div></div>
</div>

<br>
<span class="table_title_extract">CRF Data</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr>
    <td class="table_header_column_top">&nbsp;</td>
    <td class="table_header_column_top">Name</td>          
    <td class="table_header_column_top">Description</td> 
    <td class="table_header_column_top">Event</td> 
    <td class="table_header_column_top">CRF</td> 
    <td class="table_header_column_top">Version(s)</td>
    <td class="table_header_column_top">Data Type</td>
    <td class="table_header_column_top">Units</td>    
    <td class="table_header_column_top">Response Label</td>
    <td class="table_header_column_top">PHI</td>  
    
  </tr>	
<c:set var="count" value="0"/>
<c:forEach var='item' items='${allSelectedItems}'>  
  <tr>
   <td class="table_cell">   
   <c:choose>
    <c:when test="${item.selected}">
      <input type="checkbox" name="itemSelected<c:out value="${count}"/>" checked value="yes">
    </c:when>
    <c:otherwise>
      <input type="checkbox" name="itemSelected<c:out value="${count}"/>" value="yes">
    </c:otherwise>
   </c:choose>
   </td>
   <td class="table_cell"><a href="javascript: openDocWindow('ViewItemDetail?itemName=<c:out value="${item.name}"/>')"><c:out value="${item.name}"/></a></td>
   <td class="table_cell"><c:out value="${item.description}"/>&nbsp;</td>
   <td class="table_cell">
    <input type="hidden" name="itemDefName<c:out value="${count}"/>" value="<c:out value="${item.defName}"/>">
   <c:out value="${item.defName}"/>&nbsp;
   </td>
   <td class="table_cell">
   <input type="hidden" name="itemCrfName<c:out value="${count}"/>" value="<c:out value="${item.crfName}"/>">
   <c:out value="${item.crfName}"/>&nbsp;
   </td>
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
<jsp:include page="../include/footer.jsp"/>