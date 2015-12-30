<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<script language="JavaScript">
<!--

function selectAllColumn(columnName) {
	switch(columnName) {
		case 'admin':
	     if (document.cl.admin.checked) {
	     // will have to be admin, etc
		  for (var i=0; i <document.cl.elements.length; i++) {		
			if (document.cl.elements[i].name.indexOf(columnName) != -1) {
				document.cl.elements[i].checked = true;
			}
		  }
		} else {
		// alert here
		var answer = confirm('<fmt:message key="warning_you_are_removing" bundle="${restext}"/>');
		if (answer) {
			  for (var i=0; i <document.cl.elements.length; i++) {		
				if (document.cl.elements[i].name.indexOf(columnName) != -1) {
					document.cl.elements[i].checked = false;
				}
			  }
		  }
		}
		break;
	   case 'investigator':
	   if (document.cl.investigator.checked) {
	     // will have to be admin, etc
		  for (var i=0; i <document.cl.elements.length; i++) {		
			if (document.cl.elements[i].name.indexOf(columnName) != -1) {
				document.cl.elements[i].checked = true;
			}
		  }
		} else {
		// alert here
		var answer = confirm('<fmt:message key="warning_you_are_removing" bundle="${restext}"/>');
		if (answer) {
		  for (var i=0; i <document.cl.elements.length; i++) {		
			if (document.cl.elements[i].name.indexOf(columnName) != -1) {
				document.cl.elements[i].checked = false;
			}
		  }
		  }
		}
		break;
	   case 'monitor':
	   if (document.cl.monitor.checked) {
	     // will have to be admin, etc
		  for (var i=0; i <document.cl.elements.length; i++) {		
			if (document.cl.elements[i].name.indexOf(columnName) != -1) {
				document.cl.elements[i].checked = true;
			}
		  }
		} else {
		// alert here
		var answer = confirm('<fmt:message key="warning_you_are_removing" bundle="${restext}"/>');
		if (answer) {
		  for (var i=0; i <document.cl.elements.length; i++) {		
			if (document.cl.elements[i].name.indexOf(columnName) != -1) {
				document.cl.elements[i].checked = false;
			}
		  }
		  }
		}
		break;
		case 'coordinator':
		if (document.cl.coordinator.checked) {
	     // will have to be admin, etc
		  for (var i=0; i <document.cl.elements.length; i++) {		
			if (document.cl.elements[i].name.indexOf(columnName) != -1) {
				document.cl.elements[i].checked = true;
			}
		  }
		} else {
		// alert here
		var answer = confirm('<fmt:message key="warning_you_are_removing" bundle="${restext}"/>');
		if (answer) {
		  for (var i=0; i <document.cl.elements.length; i++) {		
			if (document.cl.elements[i].name.indexOf(columnName) != -1) {
				document.cl.elements[i].checked = false;
			}
		  }
		  }
		}
		break;
		case 'director':
		if (document.cl.director.checked) {
	     // will have to be admin, etc
		  for (var i=0; i <document.cl.elements.length; i++) {		
			if (document.cl.elements[i].name.indexOf(columnName) != -1) {
				document.cl.elements[i].checked = true;
			}
		  }
		} else {
		// alert here
		var answer = confirm('<fmt:message key="warning_you_are_removing" bundle="${restext}"/>');
		if (answer) {
		  for (var i=0; i <document.cl.elements.length; i++) {		
			if (document.cl.elements[i].name.indexOf(columnName) != -1) {
				document.cl.elements[i].checked = false;
			}
		  }
		  }
		}
		break;
	}
}

function notSelectAll(columnName) {
	switch(columnName) {
	case 'director':
	if (!this.checked){
		document.cl.director.checked = false;
    }
    break;
	case 'coordinator':
	if (!this.checked){
		document.cl.coordinator.checked = false;
    }
    break;
	case 'investigator':
	if (!this.checked){
		document.cl.investigator.checked = false;
    }
    break;
	case 'admin':
	if (!this.checked){
		document.cl.admin.checked = false;
    }
    break;
	case 'monitor':
	if (!this.checked){
		document.cl.monitor.checked = false;
    }
    break;
    }

}

//-->
</script>

<jsp:include page="../include/managestudy-header.jsp"/>
<%-- <jsp:include page="../include/breadcrumb.jsp"/> --%>
<%-- <jsp:include page="../include/userbox.jsp"/> --%>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<jsp:useBean scope="request" id="eventlist" class="java.util.HashMap"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		<div class="sidebar_tab_content">

		<%--
		<fmt:message key="list_users_roles_status" bundle="${restext}"/>
		--%>

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		</td>
  </tr>
  
<jsp:include page="../include/createDatasetSideInfo.jsp"/>

<%-- place CRF list above --%>

<h1><span class="title_manage"><fmt:message key="manage_extract_data_privileges_in" bundle="${restext}"/> <c:out value="${study.name}"/> <a href="javascript:openDocWindow('help/4_1_users_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>
<%-- start form here --%>

<form action="UpdatePrivileges" method="post" name="cl">
<input type="hidden" name="action" value="save">
<input type="hidden" name="crfId" value="<c:out value="${crf.id}"/>">
<input type="hidden" name="defId" value="<c:out value="${def.id}"/>">
<input type="submit" name="SubmitAndSaveMore" value="<fmt:message key="save_and_add_more_items" bundle="${resword}"/>" class="button_xlong">
<input type="submit" name="SubmitAndContinue" value="<fmt:message key="save_and_finish" bundle="${restext}"/>" class="button_medium"><br><br>

<%-- place crf info here --%>
<div style="width: 100%">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr>
 	<td class="table_header_column_top">Study Event</td>
    <td class="table_header_column_top"><fmt:message key="CRF_name" bundle="${resword}"/></td>          
    <td class="table_header_column_top">CRF OID</td>
 </tr>
 <tr>
  	<td class="table_cell"><c:out value="${def.name}"/> </td>
 	<td class="table_cell"><c:out value="${crf.name}"/> </td>
   	<td class="table_cell"><c:out value="${crf.oid}"/></td>
 </tr>
 </table>
</div>
</div></div></div></div></div></div></div></div>
</div>

<%-- place item list here --%>
<div style="width: 100%">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">

<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr>
    <td class="table_header_column_top"><fmt:message key="name" bundle="${resword}"/></td>          
    <td class="table_header_column_top"><fmt:message key="description" bundle="${resword}"/></td>
	<td class="table_header_column_top"><fmt:message key="version2" bundle="${resword}"/></td>
    <c:forEach var='role' items='${roles}'>
		<c:if test="${role.name != 'ra' && role.name != 'admin'}">
			<c:choose>
				<c:when test="${role.name == 'Data Specialist'}">
					<td class="table_header_column_top">Data Specialist / Investigator</td>
				</c:when>
				<c:when test="${role.name == 'monitor'}">
					<td class="table_header_column_top">Monitor</td>
				</c:when>
				<c:otherwise>
	    			<td class="table_header_column_top"><c:out value='${role.description}'/></td>
	    		</c:otherwise>
    		</c:choose>
		</c:if>
    </c:forEach>
 </tr>
 <%-- row for checkbox 'select all' columns --%>
<tr>
    <td class="table_cell">&nbsp;&nbsp;</td>          
    <td class="table_cell">&nbsp;&nbsp;</td>
	<td class="table_cell">&nbsp;&nbsp;</td>
    <c:forEach var='role' items='${roles}'>
		<c:if test="${role.name != 'ra' && role.name != 'admin'}">
			<c:choose>
				<c:when test="${role.name == 'Data Specialist'}">
					<td class="table_cell"><input type="checkbox" name="investigator" value="1" 
					onClick="javascript:selectAllColumn('investigator');"> <fmt:message key="select_all_items" bundle="${resword}"/><br/>
				</c:when>
				<c:otherwise>
    				<td class="table_cell"><input type="checkbox" name="<c:out value='${role.name}'/>" value="1" 
					onClick="javascript:selectAllColumn('<c:out value='${role.name}'/>');"> <fmt:message key="select_all_items" bundle="${resword}"/><br/>
				</c:otherwise>
			</c:choose>
			</td>
		</c:if>
    </c:forEach>
 </tr>
 
<c:forEach var='item' items='${items}'> 
 <tr>
 	<td class="table_cell"><a href="javascript: openDocWindow('ViewItemDetail?itemId=<c:out value="${item.id}"/>')"><c:out value="${item.name}"/></a></td>
   	<td class="table_cell"><c:out value="${item.description}"/>&nbsp;</td>
	<td class="table_cell">
      <c:forEach var="meta" items="${item.itemMetas}" varStatus="status">
        <c:choose>
          <c:when test="${status.last}">
           <c:out value="${meta.crfVersionName}"/>
          </c:when>
          <c:otherwise>
           <c:out value="${meta.crfVersionName}"/>,<br>
          </c:otherwise> 
        </c:choose> 
      </c:forEach>
      &nbsp;
    </td>
   	<c:forEach var='role' items='${roles}'>
		<c:if test="${role.name != 'ra' && role.name != 'admin'}">
			<c:choose>
				<c:when test="${role.name == 'Data Specialist'}">
					<c:set var="permKey" value="${def.id}_${crf.id}_${item.id}_${role.id}"/>
    		<td class="table_cell"><input type="checkbox" name="<c:out value="${def.id}"/>_<c:out value="${crf.id}"/>_<c:out value="${item.id}"/>_<c:out value="${role.id}"/>_investigator" 
    		<c:if test="${permissions.permissions[permKey] == 1}">
    			checked
    		</c:if>
    		 value="yes" onclick="javascript:notSelectAll('investigator');"></td>
				</c:when>
				<c:otherwise>
			<c:set var="permKey" value="${def.id}_${crf.id}_${item.id}_${role.id}"/>
    		<td class="table_cell"><input type="checkbox" name="<c:out value="${def.id}"/>_<c:out value="${crf.id}"/>_<c:out value="${item.id}"/>_<c:out value="${role.id}"/>_<c:out value="${role.name}"/>" 
    		<c:if test="${permissions.permissions[permKey] == 1}">
    			checked
    		</c:if>
    		 value="yes" onclick="javascript:notSelectAll('<c:out value="${role.name}"/>');"></td>
    		 </c:otherwise>
    		 </c:choose>
		</c:if>
    </c:forEach>
 </tr>
</c:forEach>
</table>

</div>

</div></div></div></div></div></div></div></div>
</div>
<%-- end of item list --%>

<input type="submit" name="SubmitAndSaveMore" value="<fmt:message key="save_and_add_more_items" bundle="${resword}"/>" class="button_xlong">
<input type="submit" name="SubmitAndContinue" value="<fmt:message key="save_and_finish" bundle="${restext}"/>" class="button_medium"><br><br>
<%-- end of form --%>
</form>

<c:import url="../include/workflow.jsp">
  <c:param name="module" value="manage"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>