<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.subject.SubjectEntryLabelRow" />

<tr valign="top" bgcolor="#F5F5F5">
	<td class="table_cell_left">
		<c:out value="${currRow.bean.name}" />
	</td>
	<td class="table_cell"><c:out value="${currRow.bean.description}" /></td>
	
	<td class="table_cell"><c:out value="${currRow.bean.owner.firstName}" /></td>
	<td class="table_cell"><c:out value="${currRow.bean.createdDate}" /></td>

	<%-- ACTIONS --%>
	<td class="table_cell">
	 <table border="0" cellpadding="0" cellspacing="0">
	 <tr>
		<td>
		<a href="ViewSubjectEntryLabel?subjectEntryLabelId=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
			><img name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6">
		</a>
		</td>
		<c:if test="${userBean.piiPrivilege.name=='edit'}"> 
		<td>
		<a href="EditSubjectEntryLabel?subjectEntryLabelId=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
			onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"
			><img name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6">
		</a>
		</td>
		<c:set var="confirmQuestion" value="Are you sure you want to delete ${currRow.bean.name}?" />
		<c:set var="onClick" value="return confirm('${confirmQuestion}');"/>
		<td><a href="DeleteSubjectEntryLabel?subjectEntryLabelId=<c:out value="${currRow.bean.id}"/>" onClick="<c:out value="${onClick}" />"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"
			><img name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
		&nbsp;</td>
		</c:if>
		<td>
		<a href="ListPiiSubject?subjectEntryLabelId=<c:out value="${currRow.bean.id}"/>&action=ListSubjectsByLabel"
			onMouseDown="javascript:setImage('bt_List1','images/bt_List.gif');"
			onMouseUp="javascript:setImage('bt_List1','images/bt_List.gif');"
			><img name="bt_List1" src="images/bt_List.gif" border="0" alt="List" title="List" align="left" hspace="6">
		</a>
		</td>
	 </tr>
	</table>
</td>
</tr>