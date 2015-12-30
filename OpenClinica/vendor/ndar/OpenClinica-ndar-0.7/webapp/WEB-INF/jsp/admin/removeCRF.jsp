<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<c:choose>
<c:when test="${userBean.sysAdmin}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/managestudy-header.jsp"/>
</c:otherwise> 
</c:choose>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		  
       	
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>
<jsp:useBean scope='request' id='eventCRFs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='crfToRemove' class='org.akaza.openclinica.bean.admin.CRFBean'/>

<c:choose>
<c:when test="${userBean.sysAdmin}">
<h1><span class="title_Admin">
</c:when>
<c:otherwise>
<h1><span class="title_manage">
</c:otherwise> 
</c:choose>Confirm Removal of CRF</span></h1>

<p>You choose to remove the following CRF:</p>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top" ><td class="table_header_column">Name:</td><td class="table_cell">  
  <c:out value="${crfToRemove.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column">Description:</td><td class="table_cell">  
  <c:out value="${crfToRemove.description}"/>
  </td></tr> 
</table>
 </div>
</div></div></div></div></div></div></div></div>

</div>
<br/>
<c:choose>
<c:when test="${userBean.sysAdmin}">
<span class="table_title_Admin">
</c:when>
<c:otherwise>
<span class="table_title_manage">
</c:otherwise> 
</c:choose>CRF Versions</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
 <table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
    <td class="table_header_column_top">Version Name</td> 
           
    <td class="table_header_column_top">Description</td> 
   
     <td class="table_header_column_top">Status</td>
   
     <td class="table_header_column_top">Revision Notes</td>       
    </tr>    
  <c:forEach var ="version" items="${crfToRemove.versions}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${version.name}"/></td> 
              
    <td class="table_cell"><c:out value="${version.description}"/></td> 
    
     <td class="table_cell"><c:out value="${version.status.name}"/></td>
   
     <td class="table_cell"><c:out value="${version.revisionNotes}"/></td>       
    </tr>  
 </c:forEach>    
</table>
 </div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<c:choose>
<c:when test="${userBean.sysAdmin}">
<span class="table_title_Admin">
</c:when>
<c:otherwise>
<span class="table_title_manage">
</c:otherwise> 
</c:choose>Event CRFs</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">
    <td class="table_header_column_top">Study Event ID</td>              
    <td class="table_header_column_top">Date Interviewed</td>     
     <td class="table_header_column_top">Status</td>
    </tr> 
  <c:forEach var="eventCRF" items="${eventCRFs}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${eventCRF.studyEventId}"/></td>              
    <td class="table_cell"><c:out value="${eventCRF.dateInterviewed}"/></td> 
    <td class="table_cell"><c:out value="${eventCRF.status.name}"/></td>
    </tr>    
 </c:forEach>    
</table>
 </div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<form action='RemoveCRF?action=submit&id=<c:out value="${crfToRemove.id}"/>' method="POST">
<input type="submit" name="submit" value="Remove CRF" class="button_long" onClick='return confirm("If you remove this CRF, all the assigned uses of the CRF and any data entered using it will be removed as well. Are you sure you want to remove it?");'>
</form>

<c:choose>
  <c:when test="${userBean.sysAdmin}">
  <c:import url="../include/workflow.jsp">
   <c:param name="module" value="admin"/> 
  </c:import>
 </c:when>
  <c:otherwise>
   <c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/> 
  </c:import>
  </c:otherwise> 
 </c:choose> 
<jsp:include page="../include/footer.jsp"/>
