<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.managestudy.StudyGroupClassRow" />

<tr valign="top">   
      <td class="table_cell_left"><c:out value="${currRow.bean.name}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.groupClassTypeName}"/></td>  
      <td class="table_cell"><c:out value="${currRow.bean.subjectAssignment}"/></td>     
      <td class="table_cell"><c:out value="${currRow.bean.studyName}"/></td>
       <td class="table_cell">
        <c:forEach var="studyGroup" items="${currRow.bean.studyGroups}">
          <c:out value="${studyGroup.name}"/><br>
        </c:forEach>  
       </td>
      <td class="table_cell"><c:out value="${currRow.bean.status.name}"/></td>
      <td class="table_cell">    
       <table border="0" cellpadding="0" cellspacing="0">
         <tr>
          <td>
           <a href="ViewSubjectGroupClass?id=<c:out value="${currRow.bean.id}"/>"
	         onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
	         onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
	         name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
	     </td>
	     
	     <c:if test="${study.parentStudyId <= 0 }">
	     
	     <c:choose>	  
          <c:when test="${currRow.bean.status.name !='removed'}">
           <td><a href="UpdateSubjectGroupClass?id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
			onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"><img 
			name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6"></a>
		   </td>
           <td><a href="RemoveSubjectGroupClass?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
		   </td>		     
       
          </c:when>
          <c:otherwise>
           <td>
             <a href="RestoreSubjectGroupClass?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
		      onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
		      onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"><img 
		      name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6"></a>
		     </td>
          </c:otherwise>
         </c:choose>
         
         </c:if>
         
	    </tr>
	  </table>
      </td>
   </tr>
   