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

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title="" /></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
        
        Confirm lock of this Study Event Definition from Study <c:out value="${study.name}"/>. All subject event data associated with this Study Event Definition in this Study and its Sites will be locked. No new data will be entered for this Study Event Definition and no existing data will be modifiable.

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title="" /></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='definitionToUnlock' class='org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean'/>
<jsp:useBean scope='request' id='eventDefinitionCRFs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='events' class='java.util.ArrayList'/>

<h1><span class="title_manage">Confirm Unlocking Event Definition </span></h1>

<p>Confirm unlock of this Study Event Definition from Study <c:out value="${study.name}"/>. 
The subject event data will be as it was before it was locked - available for viewing, querying, and extraction, as well as for addition and modification.</p>
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
  <tr valign="top"><td class="table_header_column">Name:</td><td class="table_cell">  
  <c:out value="${definitionToUnlock.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column">Description:</td><td class="table_cell">  
  <c:out value="${definitionToUnlock.description}"/>
  </td></tr>
 
 <tr valign="top"><td class="table_header_column">Repeating:</td><td class="table_cell">
  <c:choose>
   <c:when test="${definitionToUnlock.repeating == true}"> Yes </c:when>
   <c:otherwise> No </c:otherwise>
  </c:choose>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Type:</td><td class="table_cell">
    <c:out value="${definitionToUnlock.type}"/>
   </td></tr>
  
  <tr valign="top"><td class="table_header_column">Category:</td><td class="table_cell">  
  <c:out value="${definitionToUnlock.category}"/>
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
    <td class="table_header_column" colspan="4"><c:out value="${crf.crfName}"/></td> 
    <td class="table_header_column" colspan="1"><c:out value="${crf.status.name}"/></td>      
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
    <c:out value="${crf.defaultVersionId}"/>     
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
<br>
<c:if test="${!empty events}">
 <span class="table_title_manage">Study Events:</span> 
 <div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
 <tr valign="top"><td class="table_header_column_top">Study Subject ID</td>   
   <td class="table_header_column_top">Start Date</td>
   <td class="table_header_column_top">End Date</td>
   <td class="table_header_column_top">Status</td>
  </tr>   
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
   <td class="table_cell">
    <c:out value="${event.status.name}"/>
   </td>
  </tr>  
  </c:forEach>  
 </table>
 </div>
</div></div></div></div></div></div></div></div>

</div> 
</c:if>
<br>
<form action="UnlockEventDefinition?action=submit&id=<c:out value="${definitionToUnlock.id}"/>" method="POST">
 <input type="submit" name="submit" value="Unlock Event Definition" class="button_xlong" onClick='return confirm("Are you sure you want to unlock this event definition?");'>
</form>

<c:import url="../include/workflow.jsp">
  <c:param name="module" value="manage"/>
</c:import>


<jsp:include page="../include/footer.jsp"/>
