<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.managestudy.StudyRow" />


<tr valign="top">   
      <td class="table_cell_left"><c:out value="${currRow.bean.name}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.identifier}"/></td> 
      <td class="table_cell"><c:out value="${currRow.bean.principalInvestigator}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.facilityName}"/>&nbsp;</td>         
      <td class="table_cell"><fmt:formatDate value="${currRow.bean.createdDate}" pattern="MM/dd/yyyy"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.status.name}"/></td>
      <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
		<tr>
		 <td><a href="ViewSite?id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
			name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
		</td>       
      <c:choose>
       <c:when test="${currRow.bean.status.name !='removed'}">
        <td><a href="InitUpdateSubStudy?id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
			onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"><img 
			name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6"></a>
		  </td>
           <td><a href="RemoveSite?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
		 </td>          
       </c:when>
       <c:otherwise>
        <td><a href="RestoreSite?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
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
   