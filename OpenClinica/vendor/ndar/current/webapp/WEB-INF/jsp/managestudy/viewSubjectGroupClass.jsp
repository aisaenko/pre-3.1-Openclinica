<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/managestudy-header.jsp"/>
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

<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>

<h1><span class="title_manage">View a Group Template</span></h1>


<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
   
 <tr valign="top"><td class="table_header_column">Name:</td><td class="table_cell">
  <c:out value="${group.name}"/>
 </td></tr>
  
  <tr valign="top"><td class="table_header_column">Type:</td><td class="table_cell">
  <c:out value="${group.studyTemplateTypeName}"/>&nbsp;
 </td></tr>  
   
   <tr valign="top"><td class="table_header_column">Subject Assignment:</td><td class="table_cell">
    <c:out value="${group.subjectAssignment}"/>
   </td></tr>  
  </table>
</div>
</div></div></div></div></div></div></div></div>
</div>
<br>
 
<div class="table_title_manage"> Group Template (if any): </div>
 <!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
   <tr valign="top">
        <td class="table_header_row_left" rowspan="2">Event</td>
        <td class="table_header_row" rowspan="2">Duration (days)</td>
        <td class="table_header_row" colspan="3" align="center">Time to Next Event (days)</td>
        <td class="table_header_row" rowspan="2">Status</td>
    </tr>
    <tr valign="top">
        <td class="table_header_row">Ideal Time</td>
        <td class="table_header_row">Min Time</td>
        <td class="table_header_row">Max Time</td>
    </tr>
  
   <c:forEach var="def" items="${templateDefs}">
    <tr valign="top">    
    <td class="table_cell_left"><c:out value="${def.studyEventDefinitionName}"/></td>
    <td class="table_cell"><c:out value="${def.eventDurationSt}"/>
    </td>    
    <td class="table_cell"><c:out value="${def.idealTimeToNextEventSt}"/>
    </td>
    
    <td class="table_cell"><c:out value="${def.minTimeToNextEventSt}"/>&nbsp;
    </td>
    
    <td class="table_cell"><c:out value="${def.maxTimeToNextEventSt}"/>&nbsp;
    </td>  
     <td class="table_cell"><c:out value="${def.status.name}"/>
    </td>     
    </tr>
   
   </c:forEach>
   
 </table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<div class="table_title_manage">Study Groups and Associated Subjects (if any):</div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">   
  <tr valign="top">
   <td class="table_header_row_left">Name</td>
   <td class="table_header_row">Description</td>
    <td class="table_header_row">Subjects</td> 
  </tr>    
   <c:forEach var="studyGroup" items="${studyGroups}">   
    <tr valign="top">
     <td class="table_cell_left">  
      <c:out value="${studyGroup.name}"/>  
     </td>
     <td class="table_cell">  
      <c:out value="${studyGroup.description}"/>&nbsp;  
     </td>
     <td class="table_cell">  
      <c:forEach var="subjectMap" items="${studyGroup.subjectMaps}">       
       <c:out value="${subjectMap.subjectLabel}"/><br>      
     </c:forEach>&nbsp;
     </td>
    </tr>     
     
   </c:forEach>  
  
  </td>
  </tr>  
   
  
 
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<form action="ListSubjectGroupClass" method="post">
<input type="submit" name="Submit" value="Back to Group Template List" class="button_long">
</form>

 <c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/> 
 </c:import>
 
<jsp:include page="../include/footer.jsp"/>
