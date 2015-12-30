<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 

<html>
<head>
<title><fmt:message key="openclinica" bundle="${resword}"/>- <fmt:message key="view_discrepancy_note" bundle="${resword}"/></title>
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
<body class="popup_BG">
   
 <jsp:include page="../include/alertbox.jsp"/>
 <table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
  <tr valign="top"> 
 <td>

<div class="table_title_manage"><fmt:message key="view_discrepancy_notes" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;<a href="#" onclick="javascript:window.close();"><fmt:message key="close_window" bundle="${resword}"/></a></div>
<c:forEach var="note" items="${discrepancyNotes}">
  <table border="0" cellpadding="0" cellspacing="0" >
  <tr>
  <td>
  <img src="images/bullet.gif">&nbsp;<b><c:out value="${note.value.description}"/></b>&nbsp;&nbsp;
   <c:if test="${note.value.saved==false}">
   <span class="alert">[<fmt:message key="not_saved" bundle="${resword}"/>]</span>
   </c:if>
  </td><td>
   <c:if test="${note.value.id>0}">
    <a href="CreateDiscrepancyNote?subjectId=<c:out value="${noteSubject.id}" />&itemId=<c:out value="${item.id}" />&id=<c:out value='${note.value.entityId}'/>&name=<c:out value='${note.value.entityType}'/>&field=<c:out value='${note.value.field}'/>&column=<c:out value='${note.value.column}'/>&parentId=<c:out value='${note.value.id}'/>&enterData=<c:out value='${enterData}'/>&monitor=<c:out value='${monitor}'/>&writeToDB=<c:out value='${writeToDB}'/>">
   <b>&nbsp;Update</b></a>
   </c:if>
    </td>
   </tr>
 </table>
 <br>
 <div style="width: 350px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
  <table border="0" cellpadding="0" cellspacing="0" >
  <tr>
   <td bgcolor="#F5F5F5" class="table_header_column_top"><fmt:message key="ID" bundle="${resword}"/></td>
	<td class="table_cell"><c:out value="${note.value.id}"/></td>
   </tr>	
   <tr>
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="type" bundle="${resword}"/></td>
	<td class="table_cell"><c:out value="${note.value.disType.name}"/></td>
   </tr>
   <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="resolution_status" bundle="${resword}"/></td>
	<td class="table_cell"><c:out value="${note.value.resStatus.name}"/></td>
   </tr>	
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="last_updated_date" bundle="${resword}"/></td>
    <td class="table_cell"><fmt:formatDate value="${note.value.lastDateUpdated}" dateStyle="short"/></td>
   </tr> 
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="last_updated_by" bundle="${resword}"/></td>
    <td class="table_cell"><c:out value="${note.value.lastUpdator.name}" /></td>
   </tr> 
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"> <fmt:message key="of_notes" bundle="${resword}"/></td>
    <td class="table_cell"><c:out value="${note.value.numChildren+1}" /></td>
   </tr> 
   </table>
   </div>
  </div></div></div></div></div></div></div></div>
  </div>  
   <br>
   
  <div style="width: 350px">
  <!-- These DIVs define shaded box borders -->
   <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
   <div class="tablebox_center">
   <table border="0" cellpadding="0" cellspacing="0">
   <tr>   	
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column_top"><fmt:message key="subject" bundle="${resword}"/></td>
    <td class="table_cell_top"><c:out value="${note.value.subjectName}" />&nbsp;</td>
   </tr> 
   <c:if test="${note.value.eventName !=''}">
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="event" bundle="${resword}"/></td>
    <td class="table_cell"><c:out value="${note.value.eventName}"/>&nbsp;</td>
   </tr>    
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="event_date" bundle="${resword}"/></td>
    <td class="table_cell"><fmt:formatDate value="${note.value.eventStart}" dateStyle="short"/></td>
   </tr> 
   <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="entity_type" bundle="${resword}"/></td>
	<td class="table_cell"><c:out value="${note.value.entityType}"/></td>
   </tr>
   </c:if>
   <c:if test="${note.value.crfName !=''}">
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="CRF" bundle="${resword}"/></td>
    <td class="table_cell"><c:out value="${note.value.crfName}"/>&nbsp;</td>
   </tr> 
   </c:if> 
  
   </table>
   </div>
  </div></div></div></div></div></div></div></div>
  </div> 
  <br>
  
  <c:if test="${note.value.entityName != ''}">
  <div style="width: 350px">
  <!-- These DIVs define shaded box borders -->
   <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
   <div class="tablebox_center">
   <table border="0" cellpadding="0" cellspacing="0"> 
   
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="entity_name" bundle="${resword}"/></td>
    <td class="table_cell">
     <c:choose>
      <c:when test="${note.value.entityType=='itemData'}">
       <a href="javascript: openDocWindow('ViewItemDetail?itemName=<c:out value="${note.value.entityName}"/>')"><c:out value="${note.value.entityName}"/></a>
      </c:when>
      <c:otherwise>
        <c:out value="${note.value.entityName}"/>
      </c:otherwise>
     </c:choose>     
    </td>
   </tr> 
    <c:if test="${note.value.entityType=='itemData'}">
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="entity_value" bundle="${resword}"/></td>
    <td class="table_cell">     
     <c:out value="${note.value.entityValue}"/>     
    </td>
   </tr> 
  </c:if>
   </table>
   </div>
  </div></div></div></div></div></div></div></div>
  </div> 
  </c:if>
  <p>&nbsp;<fmt:message key="note_details" bundle="${resword}"/></p>
  <div style="width: 350px">
   <!-- These DIVs define shaded box borders -->
    <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

    <div class="tablebox_center">
   <table border="0" cellpadding="0" cellspacing="0">
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="from" bundle="${resword}"/></td>
      <td class="table_cell"><c:out value="${note.value.owner.name}"/>(<c:out value="${note.value.owner.firstName}"/>&nbsp;<c:out value="${note.value.owner.lastName}"/>)&nbsp;&nbsp;Date:<fmt:formatDate value="${note.value.createdDate}" dateStyle="short"/></td>
     </tr>
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column_top"><fmt:message key="description" bundle="${resword}"/></td>
      <td class="table_cell_top"><c:out value="${note.value.description}"/></td>
     </tr> 
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="detailed_note" bundle="${resword}"/></td>
      <td class="table_cell"><c:out value="${note.value.detailedNotes}"/></td>
     </tr>        
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="resolution_status" bundle="${resword}"/></td>
      <td class="table_cell"><c:out value="${note.value.resStatus.name}"/></td>
     </tr>     
      </table>
   </div>
  </div></div></div></div></div></div></div></div>
   </div>  
   <br>
	<c:forEach var="child" items="${note.value.children}" varStatus="status">
	 
	 <div style="width: 350px">
      <!-- These DIVs define shaded box borders -->
     <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

      <div class="tablebox_center">
      <table border="0" cellpadding="0" cellspacing="0">
      <tr>
      <td class="table_header_column"><fmt:message key="from" bundle="${resword}"/></td>
      <td class="table_cell"><c:out value="${child.owner.name}"/>(<c:out value="${child.owner.firstName}"/>&nbsp;<c:out value="${child.owner.lastName}"/>) &nbsp;&nbsp; Date:<fmt:formatDate value="${child.createdDate}" dateStyle="short"/></td>
     </tr>
	  <tr>
      <td class="table_header_column_top"><fmt:message key="description" bundle="${resword}"/></td>
      <td class="table_cell_top"><c:out value="${child.description}"/></td>
     </tr>     
     <tr>
      <td class="table_header_column"><fmt:message key="detailed_note" bundle="${resword}"/></td>
      <td class="table_cell"><c:out value="${child.detailedNotes}"/></td>
     </tr>
     <tr>
      <td class="table_header_column"><fmt:message key="resolution_status" bundle="${resword}"/></td>
      <td class="table_cell"><c:out value="${child.resStatus.name}"/></td>
     </tr>
      </table>
     </div>
    </div></div></div></div></div></div></div></div>
   </div>  
  <br>
     </c:forEach>
 
  
  </c:forEach>
  
  <a href="CreateDiscrepancyNote?subjectId=<c:out value="${noteSubject.id}" />&itemId=<c:out value="${item.id}" />&id=<c:out value='${id}'/>&name=<c:out value='${name}'/>&field=<c:out value='${field}'/>&column=<c:out value='${column}'/>&parentId=0&enterData=<c:out value='${enterData}'/>&monitor=<c:out value='${monitor}'/>&writeToDB=<c:out value="${writeToDB}"/>&new=1"><b>Start New Thread</b></a>
</body>
</html>

