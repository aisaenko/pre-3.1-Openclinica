<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<c:set var="count" value="${param.eblRowCount}" />


<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.managestudy.StudyEventDefinitionRow" />
<jsp:useBean scope="request" id="defSize" type="java.lang.Integer" />
<c:set var="last" value="${defSize-1}" />
 <c:choose>
  <c:when test="${count==last}">
    <c:set var="nextRow" value="${allRows[count]}" />
  </c:when>
  <c:otherwise>
    <c:set var="nextRow" value="${allRows[count+1]}" />
  </c:otherwise>
 </c:choose>
<tr valign="top">     
      <td class="table_cell_left">
      <%--<c:out value="${currRow.bean.ordinal}"/>--%>
      <c:choose>
        <c:when test="${count==0}">
            <c:choose>
            <c:when test="${defSize>1}">
            <a href="ChangeDefinitionOrdinal?current=<c:out value="${nextRow.bean.id}"/>&previous=<c:out value="${currRow.bean.id}"/>"><img src="images/bt_sort_descending.gif" border="0" alt="move down" title="move down" /></a>
           </c:when>
           <c:otherwise>
            &nbsp;
           </c:otherwise>
           </c:choose>
        </c:when>
        <c:when test="${count==last}">
           <a href="ChangeDefinitionOrdinal?current=<c:out value="${currRow.bean.id}"/>&previous=<c:out value="${prevRow.bean.id}"/>"><img src="images/bt_sort_ascending.gif" alt="move up" title="move up" border="0" /></a>         
        </c:when>
        <c:otherwise>
          <a href="ChangeDefinitionOrdinal?current=<c:out value="${currRow.bean.id}"/>&previous=<c:out value="${prevRow.bean.id}"/>"><img src="images/bt_sort_ascending.gif" alt="move up" title="move up" border="0" /></a>
          <a href="ChangeDefinitionOrdinal?previous=<c:out value="${currRow.bean.id}"/>&current=<c:out value="${nextRow.bean.id}"/>"><img src="images/bt_sort_descending.gif" alt="move down" title="move down" border="0" /></a>
        </c:otherwise>
      </c:choose>
      </td>   
      <td class="table_cell"><c:out value="${currRow.bean.name}"/></td>
      <td class="table_cell">
        <c:choose>
         <c:when test="${currRow.bean.repeating == true}">Yes</c:when>
         <c:otherwise>No</c:otherwise> 
        </c:choose>
      </td>
      <td class="table_cell"><c:out value="${currRow.bean.type}"/>&nbsp;</td>
      <td class="table_cell"><c:out value="${currRow.bean.category}"/>&nbsp;</td>
      <td class="table_cell"> 
        <c:choose>
         <c:when test="${currRow.bean.populated == true}">Yes</c:when>
         <c:otherwise>No</c:otherwise> 
        </c:choose>
       </td>      
      <td class="table_cell"><fmt:formatDate value="${currRow.bean.updatedDate}" pattern="MM/dd/yyyy"/><br>(<c:out value="${currRow.bean.updater.name}"/>)</td>
   
      <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
		<tr>
		 <td>
	      <a href="ViewEventDefinition?id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		    name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6" /></a>
		 </td>
     
      <c:if test="${userBean.sysAdmin || userRole.role.name == 'director' || userRole.role.name=='coordinator'}">
       <c:choose>
        <c:when test="${currRow.bean.status.name=='available'}">
        <td><a href="InitUpdateEventDefinition?id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
			onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"><img 
			name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6" /></a>
		  </td>
        <td><a href="RemoveEventDefinition?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6" /></a>
		</td>
		<c:if test="${currRow.bean.lockable}">
		  <td><a href="LockEventDefinition?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Lock1','images/bt_Lock_d.gif');"
			onMouseUp="javascript:setImage('bt_Lock1','images/bt_Lock.gif');"><img 
			name="bt_Lock1" src="images/bt_Lock.gif" border="0" alt="Lock" title="Lock" align="left" hspace="6" /></a>
		  </td>
		</c:if>        
        </c:when>
        <c:otherwise>
        <c:if test="${currRow.bean.status.name=='removed'}">
         <td><a href="RestoreEventDefinition?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
			onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"><img 
			name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6" /></a>
		 </td> 
        
        </c:if>
        </c:otherwise>
       </c:choose>     
      </c:if>
       <c:if test="${userBean.sysAdmin &&  currRow.bean.status.name=='locked'}">             
        <td><a href="UnlockEventDefinition?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Unlock1','images/bt_Unlock_d.gif');"
			onMouseUp="javascript:setImage('bt_Unlock1','images/bt_Unlock.gif');"><img 
			name="bt_Unlock1" src="images/bt_Unlock.gif" border="0" alt="Unlock" title="Unlock" align="left" hspace="6" /></a>
		  </td>       
       </c:if>
       </tr>
       </table>
      </td>       
    </tr>