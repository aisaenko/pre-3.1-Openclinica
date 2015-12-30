<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.managestudy.StudyUserRoleRow" />

<tr valign="top">   
      <td class="table_cell_left"><c:out value="${currRow.bean.userName}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.firstName}"/></td>  
      <td class="table_cell"><c:out value="${currRow.bean.lastName}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.role.description}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.studyName}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.status.name}"/></td>
      <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
		<tr>
		 <td><a href="ViewStudyUser?name=<c:out value="${currRow.bean.userName}"/>&studyId=<c:out value="${currRow.bean.studyId}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
			name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
		</td>      
       <c:choose>
        <c:when test="${currRow.bean.status.name !='removed'}">
        <td><a href="SetStudyUserRole?action=confirm&name=<c:out value="${currRow.bean.userName}"/>&studyId=<c:out value="${currRow.bean.studyId}"/>"
		  onMouseDown="javascript:setImage('bt_SetRole1','images/bt_SetRole_d.gif');"
		  onMouseUp="javascript:setImage('bt_SetRole1','images/bt_SetRole.gif');"><img 
		  name="bt_SetRole1" src="images/bt_SetRole.gif" border="0" alt="Set Role" title="Set Role" align="left" hspace="6"></a>
		</td>
       <td><a href="RemoveStudyUserRole?action=confirm&name=<c:out value="${currRow.bean.userName}"/>&studyId=<c:out value="${currRow.bean.studyId}"/>"
		 onMouseDown="javascript:setImage('bt_RemoveRole1','images/bt_RemoveRole_d.gif');"
		 onMouseUp="javascript:setImage('bt_RemoveRole1','images/bt_RemoveRole.gif');"><img 
		 name="bt_RemoveRole1" src="images/bt_RemoveRole.gif" border="0" alt="Remove Role" title="Remove Role" align="left" hspace="6"></a>
	   </td>      
       </c:when>
       <c:otherwise>
         <td><a href="RestoreStudyUserRole?action=confirm&name=<c:out value="${currRow.bean.userName}"/>&studyId=<c:out value="${currRow.bean.studyId}"/>"
			onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
			onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"><img 
			name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6"></a>
		 </td> 
      
       </c:otherwise>
      </c:choose>
      </tr>
      </table>
      </td>
   </tr>
   