<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<c:set var="count" value="${param.eblRowCount}" />
<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.managestudy.StudyEventRow" />   

   <input type="hidden" name="id<c:out value="${count}"/>" value="<c:out value="${currRow.bean.id}"/>">  
  
   <c:choose>
    <c:when test="${currRow.bean.scheduledDatePast}">
      <tr valign="top" bgcolor="#FFFF80">  
    </c:when>
    <c:otherwise>
    <tr valign="top">  
   </c:otherwise>   
   </c:choose>    
      <td class="table_cell_left"><c:out value="${currRow.bean.studySubjectLabel}"/></td>
      <td class="table_cell"><fmt:formatDate value="${currRow.bean.dateStarted}" pattern="MM/dd/yyyy"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.subjectEventStatus.name}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.summaryDataEntryStatus.name}"/></td>         
      <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
		<tr>
		<td>
        <a href="EnterDataForStudyEvent?eventId=<c:out value="${currRow.bean.id}"/>"
		onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
		onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
		
		</td>
		<c:if test="${userBean.sysAdmin || userRole.role.name=='coordinator' || userRole.role.name=='director' || (currRow.bean.owner.id == userBean.id)}">		
		<td>
        <a href="UpdateStudyEvent?event_id=<c:out value="${currRow.bean.id}"/>&ss_id=<c:out value="${currRow.bean.studySubjectId}"/>"
		onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
		onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"><img 
		name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6"></a>
		</c:if>
		</td>
		</tr>
		</table>
      </td>          
    <c:set var="count" value="${count+1}"/>
   </tr>