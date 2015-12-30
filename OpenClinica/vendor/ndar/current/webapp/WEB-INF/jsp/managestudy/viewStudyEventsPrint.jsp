<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<html>
<head>
<title>OpenClinica-Print Study Events</title>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
</head>
<body onload="javascript:alert('Press <control + p> or right click on the page to print.')">

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "startDate"}'>
		<c:set var="startDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "endDate"}'>
		<c:set var="endDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "definitionId"}'>
		<c:set var="definitionId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "statusId"}'>
		<c:set var="statusId" value="${presetValue.value}" />
	</c:if>
	
</c:forEach>
<!-- the object inside the array is StudySubjectBean-->

<h1><c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
     <div class="title_manage">
   </c:when>
   <c:otherwise>
    <div class="title_submit">
   </c:otherwise> 
  </c:choose> 
View All Events in <c:out value="${study.name}"/>
</div></h1>

<jsp:include page="../include/alertbox.jsp"/>

<p>By default, a list of events in the current month in the current study/site are shown.</p>
<p>Subjects who were scheduled/proposed in the past with no status change are highlighted in yellow.</p>
 <c:forEach var="eventView" items="${allEvents}">    
      <c:choose>
        <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
         <span class="table_title_manage">
        </c:when>
        <c:otherwise>
          <span class="table_title_submit">
        </c:otherwise> 
      </c:choose>  
  Event Name: <c:out value="${eventView.definition.name}"/></span>
	<p><b>Event Type</b>:<c:out value="${eventView.definition.type}"/>, 
	<c:choose>
     <c:when test="${eventView.definition.repeating}">
       repeating
     </c:when>
     <c:otherwise>
      non-repeating
     </c:otherwise>
    </c:choose>, 
	<b>Category</b>:
	<c:choose>
	 <c:when test="${eventView.definition.category == null || eventView.definition.category ==''}">
	  N/A
	 </c:when>
	 <c:otherwise>
	   <c:out value="${eventView.definition.category}"/>
	 </c:otherwise>
	</c:choose>	
	<br>
	<b>Subjects who scheduled</b>: <c:out value="${eventView.subjectScheduled}"/> (start date of first event:
	<c:choose>
      <c:when test="${eventView.firstScheduledStartDate== null}">
       N/A
      </c:when>
     <c:otherwise>
       <fmt:formatDate value="${eventView.firstScheduledStartDate}" pattern="MM/dd/yyyy"/>
     </c:otherwise>
     </c:choose>), <b>completed</b>: <c:out value="${eventView.subjectCompleted}"/> (completion date of last event:
   <c:choose>
   <c:when test="${eventView.lastCompletionDate== null}">
    N/A
   </c:when>
   <c:otherwise>
    <fmt:formatDate value="${eventView.lastCompletionDate}" pattern="MM/dd/yyyy"/>
   </c:otherwise>
 </c:choose>), <b>discontinued</b>: <c:out value="${eventView.subjectDiscontinued}"/><br></p>
	<c:set var="events" value="${eventView.studyEvents}" scope="request" />
	<div style="width: 600px">
	 <!-- These DIVs define shaded box borders -->
  <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
  <div class="textbox_center">

   <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
	 <td class="table_header_row">Subject Unique Identifier</td>
	 <td class="table_header_row">Event Date Started</td>
	 <td class="table_header_row">Subject Event Status</td>
	</tr>
	<c:forEach var="currRow" items="${events}">
	 <c:choose>
    <c:when test="${currRow.scheduledDatePast}">
      <tr valign="top" bgcolor="#FFFF80">  
    </c:when>
    <c:otherwise>
    <tr valign="top">  
   </c:otherwise>   
   </c:choose>    
      <td class="table_cell"><c:out value="${currRow.studySubjectLabel}"/></td>
      <td class="table_cell"><fmt:formatDate value="${currRow.dateStarted}" pattern="MM/dd/yyyy"/></td>
      <td class="table_cell"><c:out value="${currRow.subjectEventStatus.name}"/></td>
    
  
   </tr>
   
   
	</c:forEach>
	</table>

</div>
</div></div></div></div></div></div></div></div>
</div>
<br><br>
</c:forEach>
</body>
</html>

