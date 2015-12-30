<!-- NOT USED----------------------->
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../login-include/header-login.jsp"/>
<!-- the object inside the array is StudySubjectBean-->
<jsp:useBean scope="request" id="subjects" class="java.util.ArrayList"/>

<p class="title">
List all Subjects in <c:out value="${study.name}"/>
</p>

<p><a href="AddNewSubject">Enroll a new subject</a></p>

<jsp:include page="../include/showPageMessages.jsp" />
<table border="0" cellpadding="5">
<tr valign="top" bgcolor="#EBEBEB">
   <td class="text">ID</td>
   <td class="text">Unique Identifier</td>
   <td class="text">Study Subject Label</td>
   <td class="text">Gender</td>
   <td class="text">Study Name</td>
   <td class="text">Date Created</td>
   <td class="text">Owner</td>
   <td class="text">Date Last Updated</td> 
   <td class="text">Last Updated By</td>
   <td class="text">Status</td>
   <td></td>     
  </tr>
 <c:choose>
 <c:when test="${!empty subjects}">
 <c:forEach var="studySubject" items="${subjects}">
   <tr valign="top">   
      <td class="text"><c:out value="${studySubject.id}"/></td>
      <td class="text"><c:out value="${studySubject.uniqueIdentifier}"/></td>
      <td class="text"><c:out value="${studySubject.label}"/></td> 
      <td class="text"><c:out value="${studySubject.gender}"/></td> 
      <td class="text"><c:out value="${studySubject.studyName}"/></td> 
      <td class="text"><fmt:formatDate value="${studySubject.createdDate}" pattern="MM/dd/yyyy"/></td>
      <td class="text"><c:out value="${studySubject.owner.name}"/></td> 
      <td class="text"><c:out value="${studySubject.updatedDate}"/></td> 
      <td class="text"><c:out value="${studySubject.updater.name}"/></td> 
      <td class="text"><c:out value="${studySubject.status.name}"/></td>
      <td>
      <a href="ViewStudySubject?id=<c:out value="${studySubject.id}"/>&subjectId=<c:out value="${studySubject.subjectId}"/>&studyId=<c:out value="${studySubject.studyId}"/>">view</a>
      <c:choose>
       <c:when test="${studySubject.status.name !='removed'}">
        /<a href="RemoveStudySubject?action=confirm&id=<c:out value="${studySubject.id}"/>">remove</a>
        /copy
        /reassign
       </c:when>
       <c:otherwise>
        /<a href="RestoreStudySubject?action=confirm&id=<c:out value="${studySubject.id}"/>">restore</a>
       </c:otherwise>
      </c:choose>
      </td>
   </tr>
   
 </c:forEach>
 </c:when>
 <c:otherwise>
  <i>Currently no subjects in current study. </i>
 </c:otherwise>
 </c:choose> 

</table>

<jsp:include page="../login-include/footer.jsp"/>
