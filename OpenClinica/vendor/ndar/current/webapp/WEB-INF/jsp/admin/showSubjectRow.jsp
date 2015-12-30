<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.submit.DisplaySubjectRow" />

<tr valign="top">        
      <td class="table_cell_left"><c:out value="${currRow.bean.subject.name}"/>&nbsp;</td>
      <td class="table_cell"><c:out value="${currRow.bean.studySubjectIds}"/>&nbsp;</td>
      <td class="table_cell"><c:out value="${currRow.bean.subject.gender}"/>&nbsp;</td>
      <td class="table_cell"><fmt:formatDate value="${currRow.bean.subject.createdDate}" pattern="MM/dd/yyyy"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.subject.owner.name}"/></td>
      <td class="table_cell"><fmt:formatDate value="${currRow.bean.subject.updatedDate}" pattern="MM/dd/yyyy"/>&nbsp;</td>
      <td class="table_cell"><c:out value="${currRow.bean.subject.updater.name}"/>&nbsp;</td>
      <td class="table_cell"><c:out value="${currRow.bean.subject.status.name}"/></td>
      <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
      <tr>
      <td>
      <a href="ViewSubject?id=<c:out value="${currRow.bean.subject.id}"/>"
	  onMouseDown="setImage('bt_View1','images/bt_View_d.gif');"
	  onMouseUp="setImage('bt_View1','images/bt_View.gif');"><img 
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
	  </td>
	  <c:choose>	  
       <c:when test="${currRow.bean.subject.status.name !='removed'}">
        <td><a href="UpdateSubject?action=show&id=<c:out value="${currRow.bean.subject.id}"/>"
			onMouseDown="setImage('bt_Edit1','images/bt_Edit_d.gif');"
			onMouseUp="setImage('bt_Edit1','images/bt_Edit.gif');"><img 
			name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6"></a>
		</td>
        <td><a href="RemoveSubject?action=confirm&id=<c:out value="${currRow.bean.subject.id}"/>"
			onMouseDown="setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
		</td>	    
       
       </c:when>
       <c:otherwise>
        <td>
          <a href="RestoreSubject?action=confirm&id=<c:out value="${currRow.bean.subject.id}"/>"
		   onMouseDown="setImage('bt_Restor3','images/bt_Restore_d.gif');"
		   onMouseUp="setImage('bt_Restore3','images/bt_Restore.gif');"><img 
		   name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6"></a>
		</td>
       </c:otherwise>
      </c:choose>
      </tr>
      </table>
   </td>    
  </tr>  
  
  
  