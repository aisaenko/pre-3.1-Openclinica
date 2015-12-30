<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:import url="../include/admin-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='request' id='eventsForVersion' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>

<h1><span class="title_Admin">
<fmt:message key="confirm_deletion_of_CRF_version" bundle="${resword}"/></span>
</h1>
<jsp:include page="../include/alertbox.jsp" />
<p>
<fmt:message key="you_choose_to_delete_the_following" bundle="${restext}"/>
</p>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top" ><td class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${version.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${version.description}"/>
  </td></tr> 
  
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br/>
<c:if test="${!empty definitions}">
<span class="table_title_Admin">
<fmt:message key="associated_ED" bundle="${resword}"/></span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
 <table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr valign="top">
    <td class="table_header_column_top"><fmt:message key="definition_id" bundle="${resword}"/></td>          
    <td class="table_header_column_top"><fmt:message key="date_created" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="owner" bundle="${resword}"/></td>
   </tr>   
  <c:forEach var="eventDefinitionCRF" items="${definitions}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${eventDefinitionCRF.studyEventDefinitionId}"/></td>         
    <td class="table_cell">
      <c:if test="${eventDefinitionCRF.createdDate != null}">
      <fmt:formatDate value="${eventDefinitionCRF.createdDate}" dateStyle="short"/>
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
<fmt:message key="associated_event_CRFs" bundle="${resword}"/></span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
 <table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr valign="top">
    <td class="table_header_column_top"><fmt:message key="SE_ID" bundle="${resword}"/></td>          
    <td class="table_header_column_top"><fmt:message key="date_interviewed" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="status" bundle="${resword}"/></td>
   </tr>   
  <c:forEach var="eventCRF" items="${eventsForVersion}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${eventCRF.studyEventId}"/></td>         
    <td class="table_cell">
      <c:if test="${eventCRF.dateInterviewed != null}">
      <fmt:formatDate value="${eventCRF.dateInterviewed}" dateStyle="short"/>
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
   <input type="submit" name="submit" value="<fmt:message key="delete_CRF_version" bundle="${resword}"/>" class="button_xlong" onClick='return confirm("<fmt:message key="if_you_delete_this_CRF_version" bundle="${restext}"/>");'>
  </form>
 </c:when>
 <c:otherwise>
  <p><a href="ListCRF"><fmt:message key="go_back_to_CRF_list" bundle="${resword}"/></a></p>
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
