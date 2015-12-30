<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<html>
<head>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
</head>
<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="session" id="studyEvent" class="org.akaza.openclinica.bean.managestudy.StudyEventBean" />

<body onload="javascript:window.print()">
<h1>
<c:out value="${section.crf.name}" /> <c:out value="${section.crfVersion.name}" />
<c:if test="${studySubject != null && studySubject.id>0}">
  <c:choose>			 
    <c:when test="${EventCRFBean.stage.name=='initial data entry'}">
	   <img src="images/icon_InitialDE.gif" alt="Initial Data Entry" title="Initial Data Entry">
    </c:when>
    <c:when test="${EventCRFBean.stage.name=='initial data entry complete'}">
	   <img src="images/icon_InitialDEcomplete.gif" alt="Initial Data Entry Complete" title="Initial Data Entry Complete">
    </c:when>
    <c:when test="${EventCRFBean.stage.name=='double data entry'}">
	   <img src="images/icon_DDE.gif" alt="Double Data Entry" title="Double Data Entry">
    </c:when>
    <c:when test="${EventCRFBean.stage.name=='data entry complete'}">
	   <img src="images/icon_DEcomplete.gif" alt="Data Entry Complete" title="Data Entry Complete">
    </c:when>
	<c:when test="${EventCRFBean.stage.name=='administrative editing'}">
	   <img src="images/icon_AdminEdit.gif" alt="Administrative Editing" title="Administrative Editing">
    </c:when>
    <c:when test="${EventCRFBean.stage.name=='locked'}">
	   <img src="images/icon_Locked.gif" alt="Locked" title="Locked">
    </c:when>
    <c:otherwise>
	   <img src="images/icon_Invalid.gif" alt="Invalid" title="Invalid">
	</c:otherwise>
  </c:choose>
  </c:if>
</h1>
<c:if test="${studySubject != null && studySubject.id>0}">

<table border="0" cellpadding="0" cellspacing="0" width="650" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">					
<tr>
 <td class="table_cell_noborder" style="color: #789EC5"><b>Study Subject ID:</b><br></td>
 <td class="table_cell_noborder" style="color: #789EC5"><c:out value="${studySubject.label}"/><br>
 </td>
 <td class="table_cell_top" style="color: #789EC5"><b>Person ID:</b><br></td>
 <td class="table_cell_noborder" style="color: #789EC5"><c:out value="${subject.uniqueIdentifier}"/><br></td>
</tr>
<tr>
 <td class="table_cell_noborder" style="color: #789EC5"><b>Study/Site:</b><br></td>
 <td class="table_cell_noborder" style="color: #789EC5"><c:out value="${studyTitle}"/><br></td>
 <td class="table_cell_top" style="color: #789EC5"><b>Age:</b><br></td>
 <td class="table_cell_noborder" style="color: #789EC5"><c:choose><c:when test="${age!=''}"><c:out value="${age}"/></c:when>
	 <c:otherwise> N/A</c:otherwise></c:choose><br></td>
</tr>
<tr>
 <td class="table_cell_noborder" style="color: #789EC5"><b>Event:</b></td>
 <td class="table_cell_noborder" style="color: #789EC5"><c:out value="${studyEvent.studyEventDefinition.name}"/> (<fmt:formatDate value="${studyEvent.dateStarted}" pattern="MM/dd/yyyy"/>)</td>
 <td class="table_cell_top" style="color: #789EC5"><b>Date of Birth:</b><br></td>
 <td class="table_cell_noborder" style="color: #789EC5"><fmt:formatDate value="${subject.dateOfBirth}" pattern="MM/dd/yyyy"/><br></td>
</tr>
<tr>
  <td class="table_cell_noborder" style="color: #789EC5"><b>Interviewer:</b></td>
  <td class="table_cell_noborder" style="color: #789EC5"><c:out value="${EventCRFBean.interviewerName}"/> (<fmt:formatDate value="${EventCRFBean.dateInterviewed}" pattern="MM/dd/yyyy"/>)</td>
  <td class="table_cell_top" style="color: #789EC5"><b>Gender:</b></td>
  <td class="table_cell_noborder" style="color: #789EC5"><c:choose>			 
    <c:when test="${subject.gender==109}">M</c:when>
    <c:when test="${subject.gender==102}">F</c:when>
    <c:otherwise><c:out value="${subject.gender}"/></c:otherwise>
</c:choose></td>
</tr>
</table>

</c:if>
<c:choose>
  <c:when test="${displayAll != null && displayAll =='1'}">
    <jsp:include page="../submit/showAllSectionPrint.jsp" />
  </c:when>	
  <c:when test="${studySubject != null && studySubject.id>0}">
    <jsp:include page="../submit/showFixedSectionPrint.jsp" />
  </c:when>
  <c:when test="${displayAllCRF != null && displayAllCRF =='1'}">
    <jsp:include page="../managestudy/showAllSectionWithoutCommentsPrint.jsp" />
  </c:when>
  <c:otherwise>
    <jsp:include page="../managestudy/showSectionWithoutCommentsPrint.jsp" />   
  </c:otherwise>
</c:choose>

</body>
</html>