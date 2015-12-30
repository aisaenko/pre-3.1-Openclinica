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

<h1><span class="title_manage">Confirm a Group Template</span></h1>

<form action="UpdateSubjectGroupClass" method="post">
* indicates required field.<br>
<input type="hidden" name="action" value="submit">
<input type="hidden" name="id" value="<c:out value="${group.id}"/>">
<jsp:include page="../include/showSubmitted.jsp" />
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">
   
 <tr valign="top"><td class="table_header_column">Name:</td><td class="table_cell">
  <c:out value="${group.name}"/>
 </td></tr>
  
  <tr valign="top"><td class="table_header_column">Type:</td><td class="table_cell">
  <c:out value="${group.studyTemplateTypeName}"/>
 </td></tr>  
   
   <tr valign="top"><td class="table_header_column">Subject Assignment:</td><td class="table_cell">
    <c:out value="${group.subjectAssignment}"/>
   </td></tr>  
  
 </table>
</div>
</div></div></div></div></div></div></div></div>
</div>
<br> 
 Group Template:( Duration and ideal time are required if you choose an event for the group template.)
 <!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
   <tr valign="top">
        <td class="table_header_row_left" rowspan="2">Event</td>
        <td class="table_header_row" rowspan="2">Duration (days)</td>
        <td class="table_header_row" colspan="3" align="center">Time to Next Event (days)</td>
    </tr>
    <tr valign="top">
        <td class="table_header_row">Ideal Time</td>
        <td class="table_header_row">Min Time</td>
        <td class="table_header_row">Max Time</td>
    </tr>

   <c:forEach var="def" items="${group.studyTemplateEventDefs}">
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
    </tr>
   
   </c:forEach>
   
 </table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>

 Study Groups:
 <!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
    
   <c:forEach var="studyGroup" items="${studyGroups}">
    <tr>
    <td class="table_cell_left"> 
    <c:out value="${studyGroup.name}"/>
    </td>
    <td class="table_cell"> 
    <c:out value="${studyGroup.description}"/>
    </td>   
    </tr>
   </c:forEach>  
  
  </td></tr>    
  
 
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<input type="submit" name="Submit" value="Submit Group Template" class="button_long">
</form>
<br><br>

<c:import url="../include/workflow.jsp">
  <c:param name="module" value="manage"/>
</c:import>

<jsp:include page="../include/footer.jsp"/>
