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

 <p><img src="images/bullet.gif">&nbsp;<b><c:out value="${singleNote.description}"/></b></p>
 <table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
  <table border="0" cellpadding="0" cellspacing="0" >
   <tr>
   <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="resolution_status" bundle="${resword}"/></td>
	<td class="table_cell" width="220"><c:out value="${singleNote.resStatus.name}"/></td>
   </tr>	
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="last_updated_date" bundle="${resword}"/></td>
    <td class="table_cell" width="220"><fmt:formatDate value="${singleNote.lastDateUpdated}" dateStyle="short"/></td>
   </tr> 
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="last_updated_by" bundle="${resword}"/></td>
    <td class="table_cell" width="220"><c:out value="${singleNote.lastUpdator.name}" /></td>
   </tr> 
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"><fmt:message key="of_notes" bundle="${resword}"/></td>
    <td class="table_cell" width="220"><c:out value="${singleNote.numChildren+1}" /></td>
   </tr> 
   </table>
   </div>
  </div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>  
   <br>   
 <table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
  <!-- These DIVs define shaded box borders -->
   <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
   <div class="tablebox_center">
   <table border="0" cellpadding="0" cellspacing="0">
   <tr>
   <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="entity_type" bundle="${resword}"/></td>
	<td class="table_cell" width="220"><c:out value="${singleNote.entityType}"/></td>
   </tr>	
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="subject" bundle="${resword}"/></td>
    <td class="table_cell" width="220"><c:out value="${singleNote.subjectName}" />&nbsp;</td>
   </tr> 
   <c:if test="${singleNote.eventName !=''}">
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="event" bundle="${resword}"/></td>
    <td class="table_cell" width="220"><c:out value="${singleNote.eventName}"/>&nbsp;</td>
   </tr>    
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="event_date" bundle="${resword}"/></td>
    <td class="table_cell" width="220"><fmt:formatDate value="${singleNote.eventStart}" dateStyle="short"/></td>
   </tr> 
   </c:if>
   <c:if test="${singleNote.crfName !=''}">
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="CRF" bundle="${resword}"/></td>
    <td class="table_cell" width="220"><c:out value="${singleNote.crfName}"/>&nbsp;</td>
   </tr> 
   </c:if>  
   <c:if test="${singleNote.entityName !=''}">
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="entity_name" bundle="${resword}"/></td>
    <td class="table_cell" width="220">
    <c:choose>
      <c:when test="${singleNote.entityType=='ItemData'}">
       <a href="javascript: openDocWindow('ViewItemDetail?itemId=<c:out value="${singleNote.itemId}"/>')"><c:out value="${singleNote.entityName}"/></a>
      </c:when>
      <c:otherwise>
        <c:out value="${singleNote.entityName}"/>
      </c:otherwise>
     </c:choose> 
    </td>
   </tr> 
   </c:if>  
   <c:if test="${singleNote.entityValue !=''}">
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="entity_value" bundle="${resword}"/></td>
    <td class="table_cell" width="220"><c:out value="${singleNote.entityValue}"/></td>
   </tr> 
   </c:if>  
   </table>
   </div>
  </div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>   
   <p><fmt:message key="note_details" bundle="${resword}"/></p>
 <table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
   <!-- These DIVs define shaded box borders -->
    <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

    <div class="tablebox_center">
   <table border="0" cellpadding="0" cellspacing="0">
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="description" bundle="${resword}"/></td>
      <td class="table_cell" width="220"><c:out value="${singleNote.description}"/></td>
     </tr>
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="date_created" bundle="${resword}"/></td>
      <td class="table_cell" width="220"><fmt:formatDate value="${singleNote.createdDate}" dateStyle="short"/></td>
     </tr>
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="created_by" bundle="${resword}"/></td>
      <td class="table_cell" width="220"><c:out value="${singleNote.owner.name}"/></td>
     </tr>
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="type" bundle="${resword}"/></td>
      <td class="table_cell" width="220"><c:out value="${singleNote.disType.name}"/></td>
     </tr>
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column" width="130"><fmt:message key="detailed_note" bundle="${resword}"/></td>
      <td class="table_cell" width="220"><c:out value="${singleNote.detailedNotes}"/></td>
     </tr>
     </table>
   </div>
  </div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>   
  <br>
	<c:forEach var="child" items="${singleNote.children}" varStatus="status">
	  
 <table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
   <!-- These DIVs define shaded box borders -->
    <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

    <div class="tablebox_center">
    <table border="0" cellpadding="0" cellspacing="0">
	  <tr>
      <td class="table_header_column" width="130"><fmt:message key="description" bundle="${resword}"/></td>
      <td class="table_cell" width="220"><c:out value="${child.description}"/></td>
     </tr>
     <tr>
      <td class="table_header_column" width="130"><fmt:message key="date_created" bundle="${resword}"/></td>
      <td class="table_cell" width="220"><fmt:formatDate value="${child.createdDate}" dateStyle="short"/></td>
     </tr>
     <tr>
      <td class="table_header_column" width="130"><fmt:message key="created_by" bundle="${resword}"/></td>
      <td class="table_cell" width="220"><c:out value="${child.owner.name}"/></td>
     </tr>
     <tr>
      <td class="table_header_column" width="130"><fmt:message key="type" bundle="${resword}"/></td>
      <td class="table_cell" width="220"><c:out value="${child.disType.name}"/></td>
     </tr>
     <tr>
      <td class="table_header_column" width="130"><fmt:message key="detailed_note" bundle="${resword}"/></td>
      <td class="table_cell" width="220"><c:out value="${child.detailedNotes}"/></td>
     </tr>   
 
   </table>
   </div>
  </div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>  
  </c:forEach>
</body>
</html>

