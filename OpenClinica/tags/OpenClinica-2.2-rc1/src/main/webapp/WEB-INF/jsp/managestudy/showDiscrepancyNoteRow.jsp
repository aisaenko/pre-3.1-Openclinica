<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<c:set var="eblRowCount" value="${param.eblRowCount}" />
<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.managestudy.DiscrepancyNoteRow" />

<tr valign="top">
    <td class="table_cell_left"><c:out value="${currRow.bean.subjectName}" /></td>
    <td class="table_cell"><fmt:formatDate value="${currRow.bean.createdDate}" dateStyle="short"/></td>
    <td class="table_cell">
     <c:if test="${currRow.bean.eventStart != null}">
       <fmt:formatDate value="${currRow.bean.eventStart}" dateStyle="short"/>
     </c:if>&nbsp;
    </td>
    <td class="table_cell"><c:out value="${currRow.bean.eventName}" />&nbsp;</td>
    <td class="table_cell"><c:out value="${currRow.bean.crfName}" />&nbsp;</td>
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
    <td class="table_cell" width="400">		
	 <c:out value="${currRow.bean.detailedNotes}" />&nbsp; 
	</td>
	<td class="table_cell" align="right"><c:out value="${currRow.numChildren+1}" /></td>
    <td class="table_cell" style="display: none" id="Groups_0_10_<c:out value="${eblRowCount+1}"/>"><c:out value="${currRow.status.name}" /></td>
    <td class="table_cell" style="display: none" id="Groups_0_11_<c:out value="${eblRowCount+1}"/>"><c:out value="${currRow.type.name}" /></td>   
   
    <td class="table_cell" style="display: none" id="Groups_0_12_<c:out value="${eblRowCount+1}"/>"><c:out value="${currRow.bean.entityType}" />&nbsp;</td>
	
	<td class="table_cell" style="display: none" id="Groups_0_13_<c:out value="${eblRowCount+1}"/>"><c:out value="${currRow.bean.owner.name}" /></td>	
	
	<%-- ACTIONS --%>
	<td class="table_cell">
	    <table border="0" cellpadding="0" cellspacing="0">
	    <tr>
	    
	    <td>
			<a href="#" onclick="openVNoteWindow('ViewNote?id=<c:out value="${currRow.bean.id}"/>')"
			  onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			  onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
			  ><img name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view_discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_note" bundle="${resword}"/>" align="left" hspace="6"></a>
		</td>		
		<c:if test="${!currRow.status.closed}">
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
