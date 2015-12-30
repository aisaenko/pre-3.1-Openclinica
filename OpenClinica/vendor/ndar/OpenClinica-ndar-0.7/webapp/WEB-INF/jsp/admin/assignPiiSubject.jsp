<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/subjectMgmt-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope="request" id="displayStudies" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<h1><span class="title_manage">
Add Subject to Another Study
</span></h1>
<jsp:include page="../include/alertbox.jsp" />

<form action="AssignPiiSubject" method="post">
<input type="hidden" name="action" value="confirm">
<input type="hidden" name="id" value="<c:out value="${subject.id}"/>">
 <p>You choose to add the following subject to another study:</p>
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr>
   <td class="table_header_column">Person ID</td>
   <td class="table_cell"><c:out value="${subject.uniqueIdentifier}"/></td>
 </tr>
 <tr>
   <td class="table_header_column">Gender</td>
   <td class="table_cell"><c:out value="${subject.gender}"/></td></tr>
 <tr>
   <td class="table_header_column">Date Created</td>
   <td class="table_cell"><fmt:formatDate value="${subject.createdDate}" pattern="MM/dd/yyyy"/></td></tr>
 </table>
 </div>
</div></div></div></div></div></div></div></div>
</div>
<br>
<c:choose>
<c:when test="${!empty studies}">
<p><strong>Please choose a study in the following list:</strong></p>
    <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
     <table border="0" cellpadding="0" cellspacing="0" width="100%">
       <c:forEach var="studyRole" items="${studies}">   	 
        <c:choose> 	 
         <c:when test="${study.id == studyRole.studyId}">     
             
           <c:choose>
            <c:when test="${studyRole.parentStudyId > 0}">
               <c:if test="${studyRole.role.name != 'invalid'}">
                 <tr>
                   <td class="table_cell">&nbsp;&nbsp;<img src="images/bullet.gif" alt=">" title="">
                   <input type="radio" checked name="studyId" value="<c:out value="${studyRole.studyId}"/>">
                   <c:out value="${studyRole.studyName}"/> (<c:out value="${studyRole.role.description}"/>)</td>
                 </tr>
               </c:if>
            </c:when>
            <c:otherwise>
                <c:if test="${studyRole.role.name != 'invalid'}">
                 <tr>
                 <td class="table_cell"><input type="radio" checked name="studyId" value="<c:out value="${studyRole.studyId}"/>">
                 <b><c:out value="${studyRole.studyName}"/> (<c:out value="${studyRole.role.description}"/>)</b></td>
                 </tr>
               </c:if>
                <c:if test="${studyRole.role.name == 'invalid'}">
                 <tr><td class="table_cell"><b>&nbsp;<c:out value="${studyRole.studyName}"/></b></td></tr>
               </c:if>
            </c:otherwise>
           </c:choose>	 
         
          	 
         </c:when> 	 
         <c:otherwise> 	 
          <c:choose>
            <c:when test="${studyRole.parentStudyId > 0}">
               <c:if test="${studyRole.role.name != 'invalid'}">
                 <tr>
                  <td class="table_cell">&nbsp;&nbsp;<img src="images/bullet.gif" alt=">" title=""><input type="radio" name="studyId" value="<c:out value="${studyRole.studyId}"/>">
                  <c:out value="${studyRole.studyName}"/> (<c:out value="${studyRole.role.description}"/>)</td>
                 </tr>
               </c:if>
            </c:when>
            <c:otherwise>
                <c:if test="${studyRole.role.name != 'invalid'}">
                 <tr>
                  <td class="table_cell"><input type="radio" name="studyId" value="<c:out value="${studyRole.studyId}"/>">
                  <b><c:out value="${studyRole.studyName}"/> (<c:out value="${studyRole.role.description}"/>)</b></td>
                 </tr>
               </c:if>
                <c:if test="${studyRole.role.name == 'invalid'}">
                 <tr><td class="table_cell"><b>&nbsp;<c:out value="${studyRole.studyName}"/></b></td></tr>
               </c:if>
            </c:otherwise>
           </c:choose>	  	 
         </c:otherwise> 	 
        </c:choose>         
      
     </c:forEach> 	 
   
    <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyId"/></jsp:include> 
   </table>
   <br>
	</div>
	</div></div></div></div></div></div></div></div>
  <br>
  <p><input type="submit" name="Submit" value="Add Subject to Study" class="button_long"></p>


</c:when>
<c:otherwise>
  <p><i>No other studies and roles available.</i> <!-- <a href="MainMenu">Go Back</a> --> </p>
</c:otherwise>
</c:choose>
</form>


<jsp:include page="../include/footer.jsp"/>
