<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<jsp:useBean scope='request' id='strResStatus' class='java.lang.String' />
<jsp:useBean scope='request' id='writeToDB' class='java.lang.String' />

<html>
<head>
<title>OpenClinica - Add Discrepancy Note</title>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
<script language="JavaScript" src="includes/global_functions_javascript.js"></script>

<script language="JavaScript" src="includes/CalendarPopup.js"></script>


<style type="text/css">

.popup_BG { background-image: url(images/main_BG.gif);
	background-repeat: repeat-x;
	background-position: top;
	background-color: #FFFFFF;
	}


</style>
</head>
<body class="popup_BG" >
<div class="table_title_Submit">Add Discrepancy Note</div>
<div class="alert">    
<c:forEach var="message" items="${pageMessages}">
 <c:out value="${message}" escapeXml="false"/> 
</c:forEach>
</div>
<form method="POST" action="CreateDiscrepancyNote">
<jsp:include page="../include/showSubmitted.jsp" />
<input type="hidden" name="name" value="<c:out value="${discrepancyNote.entityType}"/>">
<input type="hidden" name="column" value="<c:out value="${discrepancyNote.column}"/>">
<input type="hidden" name="parentId" value="<c:out value="${discrepancyNote.parentDnId}"/>">
<input type="hidden" name="id" value="<c:out value="${discrepancyNote.entityId}"/>">
<input type="hidden" name="field" value="<c:out value="${discrepancyNote.field}"/>">
<input type="hidden" name="writeToDB" value="<c:out value="${writeToDB}" />">
   <table border="0">        	
   <tr valign="top">
    <td>Subject</td>
    <td><c:out value="${discrepancyNote.subjectName}" />&nbsp;</td>
   </tr> 
   <c:if test="${discrepancyNote.eventName !=''}">
   <tr valign="top">
    <td>Event</td>
    <td><c:out value="${discrepancyNote.eventName}"/>&nbsp;</td>
   </tr>    
    <tr valign="top">
    <td>Event Date</td>
    <td><fmt:formatDate value="${discrepancyNote.eventStart}" pattern="MM/dd/yyyy"/></td>
   </tr> 
   </c:if>
   <c:if test="${discrepancyNote.crfName !=''}">
    <tr valign="top">
    <td>CRF</td>
    <td><c:out value="${discrepancyNote.crfName}"/>&nbsp;</td>
   </tr> 
   </c:if>      
        <tr valign="top">
            <td>Entity Type/Field</td>
            <td><c:out value="${discrepancyNote.entityType}"/> : <c:out value="${discrepancyNote.column}"/>
            </td>  
        </tr>
        <tr valign="top">
            <td>Short Description</td>
            <td>
            <div class="formfieldXL_BG"><input type="text" name="description" value="<c:out value="${discrepancyNote.description}"/>" class="formfieldXL"></div>
             <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include>
            </td>
        </tr>
        <tr valign="top">
           <td>Type</td>
           <td><div class="formfieldL_BG">
           <c:set var="typeId1" value="${discrepancyNote.discrepancyNoteTypeId}"/>   
            <select name="typeId" class="formfieldL">           
              <c:forEach var="type" items="${discrepancyTypes}">    
               <c:choose>
                 <c:when test="${typeId1 == type.id}">   
                   <option value="<c:out value="${type.id}"/>" selected><c:out value="${type.name}"/>
                 </c:when>
                 <c:otherwise>
                   <option value="<c:out value="${type.id}"/>"><c:out value="${type.name}"/>      
                 </c:otherwise>
               </c:choose> 
             </c:forEach>
            </select></div>
              <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="typeId"/></jsp:include></td>    
   
        </tr>
        <tr valign="top">
            <td>Resolution status</td>
            <td><div class="formfieldL_BG">
			<c:choose>
				<c:when test='${strResStatus != ""}'>
					<c:set var="resStatusId1" value="${strResStatus}"/>
				</c:when>
				<c:otherwise>
					<c:set var="resStatusId1" value="${discrepancyNote.resolutionStatusId}"/>				
				</c:otherwise>
			</c:choose>
            <select name="resStatusId" class="formfieldL">
              <c:forEach var="status" items="${resolutionStatuses}">    
               <c:choose>
                 <c:when test="${resStatusId1 == status.id}">   
                   <option value="<c:out value="${status.id}"/>" selected><c:out value="${status.name}"/>
                 </c:when>
                 <c:otherwise>
                   <option value="<c:out value="${status.id}"/>"><c:out value="${status.name}"/>      
                 </c:otherwise>
               </c:choose> 
             </c:forEach>
            </select></div>
              <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="resStatusId"/></jsp:include></td>    
   
        </tr>
        <tr valign="top">
            <td>Detailed Note</td>
            <td>            
            <c:choose>
            <c:when test="${discrepancyNote.detailedNotes !=''}">
             <div class="formtextareaXL4_BG">
              <textarea name="detailedDes" rows="4" cols="50" class="formtextareaXL4"><c:out value="${discrepancyNote.detailedNotes}"/></textarea>
             </div>
            </c:when>
            <c:otherwise>
             <div class="formtextareaXL4_BG">
              <textarea name="detailedDes" rows="4" cols="50" class="formtextareaXL4"><c:out value="${param.strErrMsg}"/></textarea>
             </div>
            </c:otherwise>
            </c:choose>            
             <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="detailedDes"/></jsp:include>
            </td>
        </tr>
        <tr valign="top">
            <td>Date</td>
            <td><fmt:formatDate value="${discrepancyNote.createdDate}" pattern="MM/dd/yyyy"/></td>
        </tr>
       
        <tr valign="top">
            <td>Parent note</td>
            <td><c:choose>
             <c:when test="${parent== null || parent.description ==''}">
               None
             </c:when>
             <c:otherwise>
             <c:out value="${parent.description}"/>
             </c:otherwise>
             </c:choose>
            </td>
        </tr>
       
        <c:if test="${hasNotes == 'yes'}">
        <tr valign="top">
            <td colspan="2"><a href="ViewDiscrepancyNote?id=<c:out value="${discrepancyNote.entityId}"/>&name=<c:out value="${discrepancyNote.entityType}"/>&field=<c:out value="${discrepancyNote.field}"/>&column=<c:out value="${discrepancyNote.column}"/>">
            view parent and related note(s)</a> 
           </td>         
        </tr>
       </c:if> 
    </table>
    <table border="0"> 
    <tr>
      <td> <input type="submit" name="B1" value="Submit" class="button_medium"></td>
      <td><input type="submit" name="B1" value="Close" class="button_medium" onclick="javascript:window.close();"></td> 
    </tr>
    </table>  
</form>
</body>
</html>
