<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.managestudy.DiscrepancyNoteRow" />

<tr valign="top">
    <td class="table_cell_left"><c:out value="${currRow.status.name}" /></td>
    <td class="table_cell"><c:out value="${currRow.type.name}" /></td>
    <td class="table_cell"><c:out value="${currRow.bean.subjectName}" /></td>
    <td class="table_cell"><c:out value="${currRow.bean.eventName}" />&nbsp;</td>
    <td class="table_cell">
     <c:if test="${currRow.bean.eventStart != null}">
       <fmt:formatDate value="${currRow.bean.eventStart}" pattern="MM/dd/yyyy"/>
     </c:if>&nbsp;
    </td>
    <td class="table_cell"><c:out value="${currRow.bean.crfName}" />&nbsp;</td>
    <td class="table_cell"><c:out value="${currRow.bean.entityType}" />&nbsp;</td>
	<td class="table_cell">
	  <c:choose>
	  <c:when test="${currRow.bean.entityType=='ItemData'}">
	    <a href="javascript: openDocWindow('ViewItemDetail?itemId=<c:out value="${currRow.bean.itemId}"/>')"><c:out value="${currRow.bean.entityName}" /></a>&nbsp;
	  </c:when>
	  <c:otherwise>
	    <c:out value="${currRow.bean.entityName}"/>&nbsp;
	  </c:otherwise>
	  </c:choose>
	</td>
	<td class="table_cell"><c:out value="${currRow.bean.entityValue}" />&nbsp;</td>
	<td class="table_cell"><c:out value="${currRow.bean.description}" /></td>
	<td class="table_cell"><fmt:formatDate value="${currRow.bean.createdDate}" pattern="MM/dd/yyyy"/></td>
	<td class="table_cell"><c:out value="${currRow.bean.owner.name}" /></td>	
	
	<td class="table_cell" width="400">		
	 <c:out value="${currRow.bean.detailedNotes}" />&nbsp; 
	</td>
	
	<td class="table_cell" align="right"><c:out value="${currRow.numChildren+1}" /></td>
	<%-- ACTIONS --%>
	<td class="table_cell">
	    <table border="0" cellpadding="0" cellspacing="0">
	    <tr>
	    
	    <td>
			<a href="#" onclick="openVNoteWindow('ViewNote?id=<c:out value="${currRow.bean.id}"/>')"
			  onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			  onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
			  ><img name="bt_View1" src="images/bt_View.gif" border="0" alt="View Discrepancy Note" title="View Discrepancy Note" align="left" hspace="6"></a>
		</td>		
		<c:if test="${currRow.status.name !='Resolved/Closed'}">
		<td>
			<c:choose>
				<c:when test="${currRow.bean.entityType != 'ItemData' && currRow.bean.entityType != 'EventCRF'}">
					<a href="ResolveDiscrepancy?noteId=<c:out value="${currRow.bean.id}"/>"
					onMouseDown="javascript:setImage('bt_Reassign1','images/bt_Reassign_d.gif');"
					onMouseUp="javascript:setImage('bt_Reassign1','images/bt_Reassign.gif');"
					><img name="bt_Resolve1" src="images/bt_Reassign.gif" border="0" alt="Resolve Discrepancy Note" title="Resolve Discrepancy Note" align="left" hspace="6"></a>
				</c:when>
				<c:otherwise>
					<c:if test="${currRow.bean.stageId == 5 }">
						<a href="ResolveDiscrepancy?noteId=<c:out value="${currRow.bean.id}"/>"
						onMouseDown="javascript:setImage('bt_Reassign1','images/bt_Reassign_d.gif');"
						onMouseUp="javascript:setImage('bt_Reassign1','images/bt_Reassign.gif');"
						><img name="bt_Resolve1" src="images/bt_Reassign.gif" border="0" alt="Resolve Discrepancy Note" title="Resolve Discrepancy Note" align="left" hspace="6"></a>
		    		</c:if>
				</c:otherwise>
			</c:choose>
		</td>
		</c:if>
		</tr>
		</table>			
	</td>
</tr>