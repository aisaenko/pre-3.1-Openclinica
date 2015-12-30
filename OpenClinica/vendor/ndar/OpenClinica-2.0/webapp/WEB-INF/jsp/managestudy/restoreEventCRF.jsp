<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		
       
         Confirm restore of this CRF to Event <c:out value="${event.studyEventDefinition.name}"/> (Date Started:<fmt:formatDate value="${event.dateStarted}" pattern="MM/dd/yyyy"/>). All data associated with the CRF in this event will be restored.
       
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="request" id="displayEventCRF" class="org.akaza.openclinica.bean.submit.DisplayEventCRFBean"/>
<jsp:useBean scope="request" id="event" class="org.akaza.openclinica.bean.managestudy.StudyEventBean"/>
<jsp:useBean scope="request" id="items" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>

<h1><span class="title_manage">
Restore CRF to Event
</span></h1>
<p>Confirm restore of this CRF to Event <c:out value="${event.studyEventDefinition.name}"/> (Date Started:<fmt:formatDate value="${event.dateStarted}" pattern="MM/dd/yyyy"/>). All data associated with the CRF in this event will be restored.</p>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
  <tr valign="top"><td class="table_header_column_top">Event Definition Name:</td><td class="table_cell"><c:out value="${event.studyEventDefinition.name}"/></td></tr>
  <tr valign="top"><td class="table_header_column_top">Location:</td><td class="table_cell"><c:out value="${event.location}"/></td></tr>
  <tr valign="top"><td class="table_header_column_top">Visit#:</td><td class="table_cell"><c:out value="${event.sampleOrdinal}"/></td></tr>
    
  <tr valign="top"><td class="table_header_column_top">Date Started:</td><td class="table_cell"><fmt:formatDate value="${event.dateStarted}" pattern="MM/dd/yyyy"/></td></tr>
  <tr valign="top"><td class="table_header_column_top">Date Ended:</td><td class="table_cell"><fmt:formatDate value="${event.dateEnded}" pattern="MM/dd/yyyy"/></td></tr>
  <tr valign="top"><td class="table_header_column_top">Status:</td><td class="table_cell"><c:out value="${event.status.name}"/>
  </td></tr>

 </table>
 </div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<span class="title_manage">Event CRF</span>
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
		<td class="table_header_column_top">Status</td>	
	 </tr> 
	<tr>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.crf.name}" /></td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.crfVersion.name}" /></td>
		<td class="table_cell"><fmt:formatDate value="${displayEventCRF.eventCRF.dateInterviewed}" pattern="MM/dd/yyyy"/>&nbsp;</td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.interviewerName}"/>&nbsp;</td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.owner.name}" /></td>	
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.status.name}" /></td>	
	 </tr>
 
 </table>
 
</div>
</div></div></div></div></div></div></div></div>

</div>
 <c:if test="${!empty items}">
 <span class="table_title_manage">Item Data</span>
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
    <tr>
		<td class="table_header_column_top">Id</td>
		<td class="table_header_column_top">Value</td>
		<td class="table_header_column_top">Status</td>	
	 </tr>
	
  <c:forEach var="item" items="${items}"> 
	<tr>
		<td class="table_cell"><c:out value="${item.itemId}" /></td>
		<td class="table_cell"><c:out value="${item.value}" />&nbsp;</td>		
		<td class="table_cell"><c:out value="${item.status.name}" /></td>			
	
	 </tr>
	</c:forEach> 
 
 </table>

</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 </c:if>
 <c:choose>
    <c:when test="${!empty items}">
     <form action='RestoreEventCRF?action=submit&id=<c:out value="${displayEventCRF.eventCRF.id}"/>&studySubId=<c:out value="${studySub.id}"/>' method="POST">
      <input type="submit" name="submit" value="Restore Event CRF" class="button_xlong" onClick='return confirm("This CRF has data. Are you sure you want to restore it?");'>
     </form>
    
    </c:when>
    <c:otherwise>
      <form action='RestoreEventCRF?action=submit&id=<c:out value="${displayEventCRF.eventCRF.id}"/>&studySubId=<c:out value="${studySub.id}"/>' method="POST">
      <input type="submit" name="submit" value="Restore Event CRF" class="button_xlong" onClick='return confirm("Are you sure you want to restore it?");'>
     </form>    
    </c:otherwise>
   </c:choose>  

<c:import url="../include/workflow.jsp">
  <c:param name="module" value="manage"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>
