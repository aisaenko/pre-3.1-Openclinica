<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<html>
<head>
<title>OpenClinica-Print Discrepancy Notes</title>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
</head>
<body onload="javascript:alert('Press <control + p> or right click on the page to print.')">

<h1><span class="title_manage">
	View Discrepancy Notes
<!--<a href="javascript:openDocWindow('help/4_8_discrepancyNotes_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="Help" title="Help"></a>
 -->
</span></h1>

<jsp:include page="../include/alertbox.jsp" />

<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td> 	
	 <!-- These DIVs define shaded box borders -->
  <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
  <div class="tablebox_center">

  <table border="0" cellpadding="0" cellspacing="0"> 		
	<tr valign="top">						
	<td class="table_header_row_left">Resolution Status</td>
	<td class="table_header_row">Type</td>
	<td class="table_header_row">Subject</td>	
	<td class="table_header_row">Event</td>
	<td class="table_header_row">Event Date</td>
	<td class="table_header_row">CRF</td>
	<td class="table_header_row">Entity Type</td>		
	<td class="table_header_row">Entity Name</td>	
	<td class="table_header_row">Entity Value</td>
	<td class="table_header_row">Description</td>
	<td class="table_header_row">Date Created</td>
	<td class="table_header_row">Created By</td>						
	<td class="table_header_row">Detailed Notes</td>						
	<td class="table_header_row"># of Notes</td>			
  </tr>
   <c:forEach var="note" items="${allNotes}">
   <tr valign="top">
    <td class="table_cell_left"><c:out value="${note.resStatus.name}" /></td>
    <td class="table_cell"><c:out value="${note.disType.name}" /></td>
    <td class="table_cell"><c:out value="${note.subjectName}" /></td>
    <td class="table_cell"><c:out value="${note.eventName}" />&nbsp;</td>
    <td class="table_cell">
     <c:if test="${note.eventStart != null}">
       <fmt:formatDate value="${note.eventStart}" pattern="MM/dd/yyyy"/>
     </c:if>&nbsp;
    </td>
    <td class="table_cell"><c:out value="${note.crfName}" />&nbsp;</td>
    <td class="table_cell"><c:out value="${note.entityType}" />&nbsp;</td>
	<td class="table_cell"><c:out value="${note.entityName}"/>&nbsp;</td>
	<td class="table_cell"><c:out value="${note.entityValue}" />&nbsp;</td>
	<td class="table_cell"><c:out value="${note.description}" /></td>
	<td class="table_cell"><fmt:formatDate value="${note.createdDate}" pattern="MM/dd/yyyy"/></td>
	<td class="table_cell"><c:out value="${note.owner.name}" /></td>	
	
	<td class="table_cell" width="400">		
	 <c:out value="${note.detailedNotes}" />&nbsp; 
	</td>
	
	<td class="table_cell" align="right"><c:out value="${note.numChildren+1}" /></td>
 </tr>
</c:forEach>
</table>

</div>
</div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>
</body>
</html>
