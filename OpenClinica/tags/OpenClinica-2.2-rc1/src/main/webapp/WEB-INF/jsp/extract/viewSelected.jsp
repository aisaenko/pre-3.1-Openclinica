<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<script language="JavaScript">
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

<h1><span class="title_extract"><fmt:message key="create_dataset" bundle="${resword}"/>: <fmt:message key="view_selected_items" bundle="${resword}"/> <a href="javascript:openDocWindow('help/3_2_createDataset_Help.html#step1')"><img src="images/bt_Help_Extract.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>

<P><jsp:include page="../showInfo.jsp"/></P>

<jsp:include page="createDatasetBoxes.jsp" flush="true">
<jsp:param name="selectStudyEvents" value="1"/>
</jsp:include>

<P><jsp:include page="../include/alertbox.jsp"/></P>

<p><fmt:message key="can_view_items_selected_inclusion" bundle="${restext}"/><fmt:message key="select_all_items_inclusion_clicking" bundle="${restext}"/></p>

<br>
<form action="EditSelected" method="post" name="cl">
 <input type="hidden" name="all" value="1"> 
 <input type="submit" name="submit" value="<fmt:message key="select_all_items_in_study" bundle="${resword}"/>" class="button_xlong" onClick='return confirm("<fmt:message key="there_a_total_of" bundle="${resword}"><fmt:param value="${numberOfStudyItems}"/></fmt:message>")' /> 
 
  
 <br/>
</form>
<c:if test="${!empty allSelectedItems}">
<form action="CreateDataset" method="post" name="cl">
<input type="hidden" name="action" value="beginsubmit"/>
<input type="hidden" name="crfId" value="-1">
<P><B><fmt:message key="show_items_this_dataset" bundle="${restext}"/></b></p>
<table border="0" cellpadding="0" cellspacing="0" >
  <tr>
   <td><input type="submit" name="save" value="<fmt:message key="save_and_add_more_items" bundle="${resword}"/>" class="button_xlong"/></td>
   <td><input type="submit" name="saveContinue" value="<fmt:message key="save_and_define_scope" bundle="${resword}"/>" class="button_xlong"/></td>
  </tr>
</table>
<br>

<span class="table_title_extract"><fmt:message key="subject_attributes" bundle="${resword}"/></span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
 <td class="table_header_column_top">Subject Status</td>
 <td class="table_header_column_top">Secondary ID</td>
 <td class="table_header_column_top">Age at Event</td>
 <td class="table_header_column_top"> 
   <c:choose>
    <c:when test="${study.studyParameterConfig.collectDob == '1'}">
     <fmt:message key="date_of_birth" bundle="${resword}"/>
    </c:when>
    <c:when test="${study.studyParameterConfig.collectDob == '3'}">
     <fmt:message key="date_of_birth" bundle="${resword}"/>
    </c:when>
    <c:otherwise>
     <fmt:message key="year_of_birth" bundle="${resword}"/>
    </c:otherwise> 
   </c:choose>
 </td>
 <td class="table_header_column_top"><fmt:message key="gender" bundle="${resword}"/></td>
 </tr>
 <tr>
 <td class="table_cell"><c:choose>
     <c:when test="${newDataset.showSubjectStatus}">
       <input type="checkbox" checked name="subj_status" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="subj_status" value="yes">
     </c:otherwise>
    </c:choose>    
   </td>   
   <td class="table_cell">
   <c:choose>
     <c:when test="${newDataset.showSubjectUniqueIdentifier}">
       <input type="checkbox" checked name="unique_identifier" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="unique_identifier" value="yes">
     </c:otherwise>
    </c:choose>
   </td>  
   <td class="table_cell"> 
     <c:choose>
     <c:when test="${newDataset.showSubjectAgeAtEvent}">
       <input type="checkbox" checked name="age_at_event" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="age_at_event" value="yes">
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

<span class="table_title_extract">Event Attributes</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
 <td class="table_header_column_top"><fmt:message key="event_location" bundle="${resword}"/></td>
 <td class="table_header_column_top"><fmt:message key="start_date" bundle="${resword}"/></td>
 <td class="table_header_column_top"><fmt:message key="end_date" bundle="${resword}"/></td>
 <td class="table_header_column_top">Status</td>
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
     <c:when test="${newDataset.showEventStatus}">
       <input type="checkbox" checked name="event_status" value="yes">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="event_status" value="yes">
     </c:otherwise>
   </c:choose>  
 </td>
</tr>

</table>
</div>
</div></div></div></div></div></div></div></div>
</div>

<span class="table_title_extract">CRF Attributes</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
 <td class="table_header_column_top">CRF Version</td>
 <td class="table_header_column_top">Interviewer Name</td>
 <td class="table_header_column_top">Interviewer Date</td>
 <td class="table_header_column_top">CRF Status</td>
 </tr>
 <tr>
 <td class="table_cell"><c:choose>
     <c:when test="${newDataset.showCRFversion}">
       <input type="checkbox" checked name="crf_version" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="crf_version" value="yes">
     </c:otherwise>
    </c:choose>    
   </td>   
   <td class="table_cell">
   <c:choose>
     <c:when test="${newDataset.showCRFinterviewerName}">
       <input type="checkbox" checked name="interviewer" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="interviewer" value="yes">
     </c:otherwise>
    </c:choose>
   </td>  
   <td class="table_cell"> 
     <c:choose>
     <c:when test="${newDataset.showCRFinterviewerDate}">
       <input type="checkbox" checked name="interviewer_date" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="interviewer_date" value="yes">
     </c:otherwise>
    </c:choose>   
   </td>
 <td class="table_cell">
 <c:choose>
     <c:when test="${newDataset.showCRFstatus}">
       <input type="checkbox" checked name="crf_status" value="yes">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="crf_status" value="yes">
     </c:otherwise>
   </c:choose>  
 </td>
</tr>

</table>
</div>
</div></div></div></div></div></div></div></div>
</div>

<span class="table_title_extract">Subject Group Attributes</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">

<%--<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
 <td class="table_header_column_top">All Subject Groups</td>--%>
 <%--<c:forEach var="sgclass" items="${allSelectedGroups}">
 	<td class="table_header_column_top"><c:out value="${sgclass.name}"/>
 </c:forEach>--%> 
 <%-- <td class="table_header_column_top">Discrepancy Notes</td> --%>
<%-- </tr>
 <tr>
 <td class="table_cell">
 <c:choose>
     <c:when test="${newDataset.showSubjectGroupInformation}">
       <input type="checkbox" checked name="group_information" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="group_information" value="yes">
     </c:otherwise>
    </c:choose>    
   </td>--%>   
 
   <%--<c:forEach var="sgclass" items="${allSelectedGroups}">
   <c:choose>
   	<c:when test="${sgclass.selected}">
 	<td class="table_cell"><input type=checkbox checked name="groupSelected<c:out value="${sgclass.id}"/>" value="yes">
 	</c:when>
 	<c:otherwise>
 	 	<td class="table_cell"><input type=checkbox name="groupSelected<c:out value="${sgclass.id}"/>" value="yes">
 	</c:otherwise>
 	</c:choose>
 	
   </c:forEach>--%>
   
   <%-- <td class="table_cell">
   <c:choose>
     <c:when test="${newDataset.showDiscrepancyInformation}">
       <input type="checkbox" checked name="disc" value="yes">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="disc" value="yes">
     </c:otherwise>
    </c:choose>
   </td> --%>  
   
<%--</tr>

</table>--%>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td></td>
<td class="table_header_column_top">Subject Group Name</td>
<td class="table_header_column_top">Subject Group Type</td>
<td class="table_header_column_top">Status</td>
<td class="table_header_column_top">Subject Assignment</td>
</tr>
<%-- five columns --%>

   <c:forEach var="sgclass" items="${allSelectedGroups}">
   <tr>
	<c:choose>
   		<c:when test="${sgclass.selected}">
 			<td class="table_cell"><input type=checkbox checked name="groupSelected<c:out value="${sgclass.id}"/>" value="yes">
	 	</c:when>
 		<c:otherwise>
 	 		<td class="table_cell"><input type=checkbox name="groupSelected<c:out value="${sgclass.id}"/>" value="yes">
	 	</c:otherwise>
 	</c:choose>
		<td class="table_cell"><c:out value="${sgclass.name}"/></td>
		<td class="table_cell"><c:out value="${sgclass.groupClassTypeName}"/></td>
		<td class="table_cell"><c:out value="${sgclass.status.name}"/></td>
		<td class="table_cell"><c:out value="${sgclass.subjectAssignment}"/></td>
	</tr>
	</c:forEach>

</table>

</div>
</div></div></div></div></div></div></div></div>
</div>


<br>
<span class="table_title_extract"><fmt:message key="CRF_data" bundle="${resword}"/></span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr>
    <td class="table_header_column_top">&nbsp;</td>
    <td class="table_header_column_top"><fmt:message key="name" bundle="${resword}"/></td>          
    <td class="table_header_column_top"><fmt:message key="description" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="event" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="CRF" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="version2" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="data_type" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="units" bundle="${resword}"/></td>    
    <td class="table_header_column_top"><fmt:message key="response_label" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="PHI" bundle="${resword}"/></td>  
    
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
        <fmt:message key="yes" bundle="${resword}"/>
      </c:when>
      <c:otherwise>
        <fmt:message key="no" bundle="${resword}"/>
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
   <td><input type="submit" name="save" value="<fmt:message key="save_and_add_more_items" bundle="${resword}"/>" class="button_xlong"/></td>
   <td><input type="submit" name="saveContinue" value="<fmt:message key="save_and_define_scope" bundle="${resword}"/>" class="button_xlong"/></td>
  </tr>
</table>
</form>
</c:if>
<jsp:include page="../include/footer.jsp"/>
