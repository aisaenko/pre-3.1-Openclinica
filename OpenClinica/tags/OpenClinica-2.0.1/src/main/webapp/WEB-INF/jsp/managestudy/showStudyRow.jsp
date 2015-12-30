<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.admin.DisplayStudyRow" />
   <tr valign="top" bgcolor="#F5F5F5">   
      <td class="table_cell_left"><b><c:out value="${currRow.bean.parent.name}"/></b></td>
      <td class="table_cell"><c:out value="${currRow.bean.parent.identifier}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.parent.principalInvestigator}"/></td>  
      <td class="table_cell"><c:out value="${currRow.bean.parent.facilityName}"/>&nbsp;</td> 
      <td class="table_cell"><fmt:formatDate value="${currRow.bean.parent.createdDate}" pattern="MM/dd/yyyy"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.parent.status.name}"/></td> 
      <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
	    <tr>
	      <td><a href="ViewStudy?id=<c:out value="${currRow.bean.parent.id}"/>&viewFull=yes"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		    name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
		  </td>
          <c:choose>
          <c:when test="${currRow.bean.parent.status.name!='removed'}">
          <td><a href="InitUpdateStudy?id=<c:out value="${currRow.bean.parent.id}"/>"
			onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
			onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"><img 
			name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6"></a>
		  </td>
          
          <td><a href="RemoveStudy?action=confirm&id=<c:out value="${currRow.bean.parent.id}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" align="left" hspace="6"></a>
		 </td>
         
          </c:when>
         <c:otherwise>
          <td><a href="RestoreStudy?action=confirm&id=<c:out value="${currRow.bean.parent.id}"/>"
			onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
			onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"><img 
			name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" align="left" hspace="6"></a>
		 </td>          
         </c:otherwise>
         </c:choose>
	    </tr>
	   </table>       
     </td>   
   </tr>
   <c:forEach var="child" items="${currRow.bean.children}">   
    <tr>
      <td class="table_cell_left"><div class="homebox_bullets"><c:out value="${child.name}"/></div></td>
      <td class="table_cell"><c:out value="${child.identifier}"/></td>
      <td class="table_cell"><c:out value="${child.principalInvestigator}"/></td>  
      <td class="table_cell"><c:out value="${child.facilityName}"/>&nbsp;</td> 
      <td class="table_cell"><fmt:formatDate value="${child.createdDate}" pattern="MM/dd/yyyy"/></td>
      <td class="table_cell"><c:out value="${child.status.name}"/></td> 
      <td class="table_cell">
        <table border="0" cellpadding="0" cellspacing="0">
	    <tr>
	     <td>
	      <a href="ViewSite?id=<c:out value="${child.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		    name="bt_View1" src="images/bt_View.gif" border="0" alt="View" align="left" hspace="6"></a>
		  </td>
        <c:choose>
         <c:when test="${child.status.name!='removed'}">
           <td><a href="InitUpdateSubStudy?id=<c:out value="${child.id}"/>"
			onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
			onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"><img 
			name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" align="left" hspace="6"></a>
		  </td>
           <td><a href="RemoveSite?action=confirm&id=<c:out value="${child.id}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" align="left" hspace="6"></a>
		 </td>          
         </c:when>
         <c:otherwise>
          <td><a href="RestoreSite?action=confirm&id=<c:out value="${child.id}"/>"
			onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
			onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"><img 
			name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" align="left" hspace="6"></a>
		 </td>           
         </c:otherwise>
        </c:choose>
        </tr>
	   </table>  
      </td>
     </tr>
     </c:forEach>