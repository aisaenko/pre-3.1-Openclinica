<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<html>
<head>
<title>OpenClinica - View Discrepancy Note</title>
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

<div class="table_title_manage">View Discrepancy Notes &nbsp;&nbsp;&nbsp;<a href="#" onclick="javascript:window.close();">Close Window</a></div>

 <p><img src="images/bullet.gif">&nbsp;<b><c:out value="${singleNote.description}"/></b></p>
 <table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
  <table border="0" cellpadding="0" cellspacing="0" >
   <tr>
   <td bgcolor="#F5F5F5" class="table_header_column">Resolution Status</td>
	<td class="table_cell" width="220"><c:out value="${singleNote.resStatus.name}"/></td>
   </tr>	
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column">Last Updated Date</td>
    <td class="table_cell" width="220"><fmt:formatDate value="${singleNote.lastDateUpdated}" pattern="MM/dd/yyyy"/></td>
   </tr> 
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column">Last Updated By</td>
    <td class="table_cell" width="220"><c:out value="${singleNote.lastUpdator.name}" /></td>
   </tr> 
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column"># of Notes</td>
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
   <td bgcolor="#F5F5F5" class="table_header_column" width="130">Entity Type</td>
	<td class="table_cell" width="220"><c:out value="${singleNote.entityType}"/></td>
   </tr>	
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column" width="130">Subject</td>
    <td class="table_cell" width="220"><c:out value="${singleNote.subjectName}" />&nbsp;</td>
   </tr> 
   <c:if test="${singleNote.eventName !=''}">
   <tr>
    <td bgcolor="#F5F5F5" class="table_header_column" width="130">Event</td>
    <td class="table_cell" width="220"><c:out value="${singleNote.eventName}"/>&nbsp;</td>
   </tr>    
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column" width="130">Event Date</td>
    <td class="table_cell" width="220"><fmt:formatDate value="${singleNote.eventStart}" pattern="MM/dd/yyyy"/></td>
   </tr> 
   </c:if>
   <c:if test="${singleNote.crfName !=''}">
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column" width="130">CRF</td>
    <td class="table_cell" width="220"><c:out value="${singleNote.crfName}"/>&nbsp;</td>
   </tr> 
   </c:if>  
   <c:if test="${singleNote.entityName !=''}">
    <tr>
    <td bgcolor="#F5F5F5" class="table_header_column" width="130">Entity Name</td>
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
    <td bgcolor="#F5F5F5" class="table_header_column" width="130">Entity Value</td>
    <td class="table_cell" width="220"><c:out value="${singleNote.entityValue}"/></td>
   </tr> 
   </c:if>  
   </table>
   </div>
  </div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>   
   <p>Note Details</p>
 <table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
   <!-- These DIVs define shaded box borders -->
    <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

    <div class="tablebox_center">
   <table border="0" cellpadding="0" cellspacing="0">
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column" width="130">Description</td>
      <td class="table_cell" width="220"><c:out value="${singleNote.description}"/></td>
     </tr>
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column" width="130">Date Created</td>
      <td class="table_cell" width="220"><fmt:formatDate value="${singleNote.createdDate}" pattern="MM/dd/yyyy"/></td>
     </tr>
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column" width="130">Created By</td>
      <td class="table_cell" width="220"><c:out value="${singleNote.owner.name}"/></td>
     </tr>
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column" width="130">Type</td>
      <td class="table_cell" width="220"><c:out value="${singleNote.disType.name}"/></td>
     </tr>
     <tr>
      <td bgcolor="#F5F5F5" class="table_header_column" width="130">Detailed Note</td>
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
      <td class="table_header_column" width="130">Description</td>
      <td class="table_cell" width="220"><c:out value="${child.description}"/></td>
     </tr>
     <tr>
      <td class="table_header_column" width="130">Date Created</td>
      <td class="table_cell" width="220"><fmt:formatDate value="${child.createdDate}" pattern="MM/dd/yyyy"/></td>
     </tr>
     <tr>
      <td class="table_header_column" width="130">Created By</td>
      <td class="table_cell" width="220"><c:out value="${child.owner.name}"/></td>
     </tr>
     <tr>
      <td class="table_header_column" width="130">Type</td>
      <td class="table_cell" width="220"><c:out value="${child.disType.name}"/></td>
     </tr>
     <tr>
      <td class="table_header_column" width="130">Detailed Note</td>
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

