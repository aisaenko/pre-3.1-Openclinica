<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<c:import url="../include/admin-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='request' id='eventsForVersion' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>

<h1><span class="title_Admin">
Confirm Deletion of CRF Version</span>
</h1>
<jsp:include page="../include/alertbox.jsp" />
<p>You choose to DELETE the following CRF Version permanently, please notice that it CANNOT be restored in the future:</p>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top" ><td class="table_header_column">Name:</td><td class="table_cell">  
  <c:out value="${version.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column">Description:</td><td class="table_cell">  
  <c:out value="${version.description}"/>
  </td></tr> 
  
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br/>
<c:if test="${!empty definitions}">
<span class="table_title_Admin">
Associated Event Definitions</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
 <table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr valign="top">
    <td class="table_header_column_top">Definition Id</td>          
    <td class="table_header_column_top">Date Created</td> 
    <td class="table_header_column_top">Owner</td>
   </tr>   
  <c:forEach var="eventDefinitionCRF" items="${definitions}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${eventDefinitionCRF.studyEventDefinitionId}"/></td>         
    <td class="table_cell">
      <c:if test="${eventDefinitionCRF.createdDate != null}">
      <fmt:formatDate value="${eventDefinitionCRF.createdDate}" pattern="MM/dd/yyyy"/>
      </c:if>&nbsp;
    </td>    
    <td class="table_cell"><c:out value="${eventDefinitionCRF.owner.name}"/></td>
    </tr>    
 </c:forEach>    
</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
</c:if>
<br>
<c:if test="${!empty eventsForVersion}">
<span class="table_title_Admin">
Associated Event CRFs</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
 <table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr valign="top">
    <td class="table_header_column_top">Study Event ID</td>          
    <td class="table_header_column_top">Date Interviewed</td> 
    <td class="table_header_column_top">Status</td>
   </tr>   
  <c:forEach var="eventCRF" items="${eventsForVersion}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${eventCRF.studyEventId}"/></td>         
    <td class="table_cell">
      <c:if test="${eventCRF.dateInterviewed != null}">
      <fmt:formatDate value="${eventCRF.dateInterviewed}" pattern="MM/dd/yyyy"/>
      </c:if>&nbsp;
    </td>    
    <td class="table_cell"><c:out value="${eventCRF.status.name}"/></td>
    </tr>    
 </c:forEach>    
</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
</c:if>
<br/>
<c:choose>
 <c:when test="${empty eventsForVersion && empty definitions}">
  <form action='DeleteCRFVersion?action=submit&verId=<c:out value="${version.id}"/>' method="POST">
   <input type="submit" name="submit" value="Delete CRF Version" class="button_xlong" onClick='return confirm("If you delete this CRF Version, it will be permanently deleted from the system and cannot be restored. Are you sure you want to delete it?");'>
  </form>
 </c:when>
 <c:otherwise>
  <p><a href="ListCRF">Go back to CRF list</a></p>
 </c:otherwise>
</c:choose>

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
