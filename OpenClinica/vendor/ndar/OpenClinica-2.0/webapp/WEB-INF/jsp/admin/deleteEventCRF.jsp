<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/admin-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope="request" id="displayEventCRF" class="org.akaza.openclinica.bean.submit.DisplayEventCRFBean"/>
<jsp:useBean scope="request" id="event" class="org.akaza.openclinica.bean.managestudy.StudyEventBean"/>
<jsp:useBean scope="request" id="items" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>

<h1><span class="title_Admin">
Delete CRF from Event
</span></h1>
<p>Confirm DELETION of this CRF from Event <c:out value="${event.studyEventDefinition.name}"/> (Date <fmt:formatDate value="${event.dateStarted}" pattern="MM/dd/yyyy"/>). All data associated with the CRF in this Event will be permanently deleted.
Please notice that deleted Event CRF CANNOT be restored in the future.</p>
<jsp:include page="../include/alertbox.jsp" />

<div style="width: 600px">   
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
  <tr valign="top"><td class="table_header_column">Event Definition Name:</td><td class="table_cell"><c:out value="${event.studyEventDefinition.name}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Location:</td><td class="table_cell"><c:out value="${event.location}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Visit#:</td><td class="table_cell"><c:out value="${event.sampleOrdinal}"/></td></tr>
    
  <tr valign="top"><td class="table_header_column">Date Started:</td><td class="table_cell"><fmt:formatDate value="${event.dateStarted}" pattern="MM/dd/yyyy"/></td></tr>
  <tr valign="top"><td class="table_header_column">Date Ended:</td><td class="table_cell"><fmt:formatDate value="${event.dateEnded}" pattern="MM/dd/yyyy"/></td></tr>
  <tr valign="top"><td class="table_header_column">Status:</td><td class="table_cell"><c:out value="${event.status.name}"/>
  </td></tr>

 </table> 
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<span class="table_title_admin">Event CRF</span>
<div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
    <tr>
		<td class="table_header_column_top">CRF Name</td>
		<td class="table_header_column_top">Version</td>
		<td class="table_header_column_top">Date Interviewed</td>
		<td class="table_header_column_top">Interviewer Name</td>
		<td class="table_header_column_top">Owner</td>
		<td class="table_header_column_top">Completion Status</td>	
		
	 </tr> 
	<tr>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.crf.name}" /></td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.crfVersion.name}" /></td>
		<td class="table_cell"><fmt:formatDate value="${displayEventCRF.eventCRF.dateInterviewed}" pattern="MM/dd/yyyy"/>&nbsp;</td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.interviewerName}"/>&nbsp;</td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.owner.name}" /></td>
		<td class="table_cell"><c:out value="${displayEventCRF.stage.name}" /></td>	
		
	 </tr>
 
 </table> 
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 <c:if test="${!empty items}">
 <span class="table_title_admin">Item Data</span>
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
    <tr>
		<td class="table_header_column_top">Id</td>
		<td class="table_header_column_top">Value</td>	
	 </tr>
	 <c:set var="count" value="0"/>
  <c:forEach var="item" items="${items}"> 
	<tr>
		<td class="table_cell"><c:out value="${item.itemId}" /></td>
		<td class="table_cell"><c:out value="${item.value}" />&nbsp;</td>	
	
		
		<c:if test="${item.status.name !='removed'}">
		<c:set var="count" value="${count+1}"/>
		</c:if>
	 </tr>
	</c:forEach> 
 
 </table>
 
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 </c:if>
   <c:choose>
    <c:when test="${!empty items && count>0}">
     <form action='DeleteEventCRF?action=submit&ecId=<c:out value="${displayEventCRF.eventCRF.id}"/>&ssId=<c:out value="${studySub.id}"/>' method="POST">
      <input type="submit" name="submit" value="Delete Event CRF" class="button_xlong" onClick='return confirm("This CRF has data. Are you sure you want to delete it?");'>
     </form>
    </c:when>
    <c:otherwise>
      <form action='DeleteEventCRF?action=submit&ecId=<c:out value="${displayEventCRF.eventCRF.id}"/>&ssId=<c:out value="${studySub.id}"/>' method="POST">
      <input type="submit" name="submit" value="Delete Event CRF" class="button_xlong" onClick='return confirm("Are you sure you want to delete it?");'>
     </form>
    </c:otherwise>
   </c:choose>  
 
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
