<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:choose>
<c:when test="${userBean.sysAdmin}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
    <c:import url="../include/managestudy-header.jsp"/>
   </c:when>
   <c:otherwise>
    <c:import url="../include/submit-header.jsp"/>
   </c:otherwise> 
  </c:choose>
</c:otherwise> 
</c:choose>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
         Click the 'view' icon for each section in the table to preview the data entry layout. Click the 'print' icon to print a hard copy of the data entry form.

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="request" id="toc" class="org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean" />

<c:choose>
<c:when test="${userBean.sysAdmin}">
  <h1><span class="title_Admin">
</c:when>
<c:otherwise>
  <h1>
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
     <span class="title_manage">
   </c:when>
   <c:otherwise>
    <span class="title_submit">
   </c:otherwise> 
  </c:choose>
</c:otherwise>
</c:choose>
View CRF Version Data Entry
</span></h1>


<div class="homebox_bullets"><a href="ViewCRFVersion?id=<c:out value="${toc.crfVersion.id}"/>">View CRF Version Metadata</a></div>
<p>
<div class="homebox_bullets" style="width:117">

<a href="javascript:openDocWindow('PrintCRF?id=<c:out value="${toc.crfVersion.id}"/>')"
					onMouseDown="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print_d.gif');"
					onMouseUp="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');">
					<img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_Print.gif" border="0" alt="Print out Form" title="Print out Form" align="right" hspace="6"></a>
					<a href="javascript:openDocWindow('PrintCRF?id=<c:out value="${toc.crfVersion.id}"/>')">Print Entire CRF</a>
					
</div>
<p>
<c:choose>
  <c:when test="${userBean.sysAdmin}">
   <div class="table_title_Admin">
  </c:when>
  <c:otherwise>
     <c:choose>
      <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
       <div class="title_manage">
      </c:when>
      <c:otherwise>
       <div class="title_submit">
      </c:otherwise> 
    </c:choose>
  </c:otherwise>
</c:choose>

Section Properties</div>

<c:choose>
	<c:when test="${empty toc.sections}">
		<br/>There are no sections in this CRF Version.
	</c:when>
	<c:otherwise>
	<div style="width: 600px">

	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">
<!-- Table Contents -->
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="table_header_row">Section Title</td>
		<td class="table_header_row">Total Items</td>
		<td class="table_header_row">Actions</td>
	</tr>
	<c:set var="rowCount" value="${0}" />
	<c:forEach var="section" items="${toc.sections}">
		<c:set var="actionLink" value="ViewSectionDataEntry?sectionId=${section.id}&ecId=${toc.eventCRF.id}" />
		
		<%-- set the action label --%>
		
		<tr>
			<td class="table_cell"><c:out value="${section.title}"/></td>			
			<td class="table_cell"><c:out value="${section.numItems}" /></td>

			<td class="table_cell">
			 <table border="0" cellpadding="0" cellspacing="0">
			 <tr>
			  <td>
				<a href="<c:out value="${actionLink}"/>"
					onMouseDown="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View_d.gif');"
					onMouseUp="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');">
					<img name="bt_View<c:out value="${rowCount}"/>" src="images/bt_View.gif" border="0" alt="View Data Entry" title="View Data Entry" align="left" hspace="6"></a>
			  </td>
			  <td>
			    <a href="javascript:openDocWindow('ViewSectionDataEntry?sectionId=<c:out value="${section.id}"/>&ecId=<c:out value="${toc.eventCRF.id}"/>&print=yes')"
					onMouseDown="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print_d.gif');"
					onMouseUp="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');">
					<img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_Print.gif" border="0" alt="Print out Form" title="Print out Form" align="left" hspace="6"></a>
			  </td>
			  </tr>
			</table>
			</td>
		</tr>
		<c:set var="rowCount" value="${rowCount + 1}" />
	</c:forEach>
</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
</c:otherwise>
</c:choose>


<c:choose>
<c:when test="${userBean.sysAdmin}">
  <c:import url="../include/workflow.jsp">
    <c:param name="module" value="admin"/>
  </c:import>
</c:when>
<c:otherwise>
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
   <c:import url="../include/workflow.jsp">
    <c:param name="module" value="manage"/>
    </c:import>
   </c:when>
   <c:otherwise>
    <c:import url="../include/workflow.jsp">
      <c:param name="module" value="submit"/>
    </c:import>
   </c:otherwise> 
  </c:choose>
</c:otherwise> 
</c:choose>

<jsp:include page="../include/footer.jsp"/>