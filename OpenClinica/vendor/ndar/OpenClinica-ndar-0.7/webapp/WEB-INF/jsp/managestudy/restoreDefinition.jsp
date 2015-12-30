<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
        
        Confirm restore of this Study Event Definition to Study <c:out value="${study.name}"/>. The Study Event Definition and all subject data associated with it in this Study or its Sites will be restored.

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='definitionToRestore' class='org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean'/>
<jsp:useBean scope='request' id='eventDefinitionCRFs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='events' class='java.util.ArrayList'/>

<h1><span class="title_manage">Confirm Restore of Event Definition </span></h1>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column">Name:</td><td class="table_cell">  
  <c:out value="${definitionToRestore.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column">Description:</td><td class="table_cell">  
  <c:out value="${definitionToRestore.description}"/>
  </td></tr>
 
 <tr valign="top"><td class="table_header_column">Repeating:</td><td class="table_cell">
  <c:choose>
   <c:when test="${definitionToRestore.repeating == true}"> Yes </c:when>
   <c:otherwise> No </c:otherwise>
  </c:choose>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Type:</td><td class="table_cell">
    <c:out value="${definitionToRestore.type}"/>
   </td></tr>
  
  <tr valign="top"><td class="table_header_column">Category:</td><td class="table_cell">  
  <c:out value="${definitionToRestore.category}"/>
  </td></tr>
 </table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<c:if test="${!empty eventDefinitionCRFs}">
<span class="table_title_manage">CRFs</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <c:forEach var ="crf" items="${eventDefinitionCRFs}">   
   <tr valign="top" bgcolor="#F5F5F5">             
    <td class="table_header_column" colspan="2"><c:out value="${crf.crfName}"/></td> 
    <td class="table_header_column" colspan="3"><c:out value="${crf.status.name}"/></td>      
  </tr>
   
   <tr valign="top">   
     
    <td class="table_cell">Required:
    <c:choose>
    <c:when test="${crf.requiredCRF == true}"> Yes </c:when>
     <c:otherwise> No </c:otherwise>
    </c:choose>
   </td>
     
    <td class="table_cell">Double Data Entry:
     <c:choose>
      <c:when test="${crf.doubleEntry == true}"> Yes </c:when>
      <c:otherwise> No </c:otherwise>
     </c:choose>
    </td>         
         
    <td class="table_cell">Enforce Decision Conditions:
     <c:choose>
      <c:when test="${crf.decisionCondition == true}"> Yes </c:when>
      <c:otherwise> No </c:otherwise>
     </c:choose>
   </td>
  
   <td class="table_cell">Default Version:    
    <c:out value="${crf.defaultVersionName}"/>     
   </td>
   <td class="table_cell">Null Values:    
    <c:out value="${crf.nullValues}"/>     
  </td>
  </tr>             
  
 </c:forEach>
 
 </table>
</div>
</div></div></div></div></div></div></div></div>

</div>
 </c:if>
 <span class="table_title_manage">Study Events:</span> 
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
  <tr valign="top">
  <td class="table_header_column_top">Study Subject ID</td>
   <td class="table_header_column_top">Start Date</td>
   <td class="table_header_column_top">End Date</td>
  </tr>  
  <c:choose> 
  <c:when test="${empty events}">
	  <tr valign="top">
	   <td class="table_cell">
		No Study Events Found
	   </td>
	   <td class="table_cell">
	   </td>
	   <td class="table_cell">
	   </td>
  </tr>  
  </c:when>
  <c:otherwise>
  <c:forEach var="event" items="${events}">
  <tr valign="top">
   <td class="table_cell">
    <c:out value="${event.studySubjectId}"/>
   </td>
   <td class="table_cell">
    <fmt:formatDate value="${event.dateStarted}" pattern="MM/dd/yyyy"/>&nbsp;
   </td>
  <td class="table_cell">
    <fmt:formatDate value="${event.dateEnded}" pattern="MM/dd/yyyy"/>&nbsp;
   </td>
  </tr>  
  </c:forEach>  
  </c:otherwise>
  </c:choose>
 </table> 
</div>
</div></div></div></div></div></div></div></div>

</div>
 <!--<p><a href='RestoreEventDefinition?action=submit&id=<c:out value="${definitionToRestore.id}"/>' onClick='return confirm("If you restore this definition, all the data and subjects will be restored. Are you sure you want to restore it?");'>Restore Event Definition</a>      
</p>-->
<br>
<form action='RestoreEventDefinition?action=submit&id=<c:out value="${definitionToRestore.id}"/>' method="POST">
 <input type="submit" name="submit" value="Restore Event Definition" onClick='return confirm("If you restore this definition, all the data and subjects will be restored. Are you sure you want to restore it?");' class="button_xlong">
</form>

<c:import url="../include/workflow.jsp">
  <c:param name="module" value="manage"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>
