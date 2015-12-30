<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<c:import url="../include/admin-header.jsp"/>
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

<jsp:useBean scope='request' id='studySubs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='events' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='subjectToRestore' class='org.akaza.openclinica.bean.submit.SubjectBean'/>

<h1><span class="title_Admin">Confirm Restore of Subject</span></h1>

<div class="table_title_Admin">You choose to restore the following Subject:</div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">

<table border="0" cellpadding="0"> 
  <tr valign="top" ><td class="table_header_column">Person ID:</td><td class="table_cell">  
  <c:out value="${subjectToRestore.uniqueIdentifier}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column">gender:</td><td class="table_cell">  
  <c:out value="${subjectToRestore.gender}"/>
  </td>
  <tr valign="top"><td class="table_header_column">Date Of Birth:</td><td class="table_cell">  
  <fmt:formatDate value="${subjectToRestore.dateOfBirth}" pattern="MM/dd/yyyy"/>
  </td>
  <tr valign="top"><td class="table_header_column">Date Created:</td><td class="table_cell">  
  <fmt:formatDate value="${subjectToRestore.createdDate}" pattern="MM/dd/yyyy"/>
  </td>
  </tr> 
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br/><br/>
<div class="table_title_Admin">Associated Study Subjects </div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
 <table border="0" cellpadding="0">  
 <tr valign="top">
    <td class="table_header_column_top">Study Subject ID</td>       
    <td class="table_header_column_top">Secondary Label</td> 
    <td class="table_header_column_top">Study Record Id</td>
    <td class="table_header_column_top">Enrollment Date</td> 
    <td class="table_header_column_top">Date Created</td> 
    <td class="table_header_column_top">Created By</td> 
    <td class="table_header_column_top">Status</td>       
    </tr>   
  <c:forEach var="studySub" items="${studySubs}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${studySub.label}"/></td>               
    <td class="table_cell"><c:out value="${studySub.secondaryLabel}"/>&nbsp;</td> 
    <td class="table_cell"><c:out value="${studySub.studyId}"/></td>
    <td class="table_cell"><fmt:formatDate value="${studySub.enrollmentDate}" pattern="MM/dd/yyyy"/></td>       
    <td class="table_cell"><fmt:formatDate value="${studySub.createdDate}" pattern="MM/dd/yyyy"/></td>       
    <td class="table_cell"><c:out value="${studySub.owner.name}"/></td>  
    <td class="table_cell"><c:out value="${studySub.status.name}"/></td>       
    </tr>  
 </c:forEach>    
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br><br>
<div class="table_title_Admin">Associated Study Events</div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
 <table border="0" cellpadding="0">
  <tr valign="top">
    <td class="table_header_column_top">Record ID</td>
    <td class="table_header_column_top">Location</td> 
    <td class="table_header_column_top">Date Started</td> 
    <td class="table_header_column_top">Created By</td> 
    <td class="table_header_column_top">Status</td>
    </tr>       
  <c:forEach var="event" items="${events}">   
    <tr valign="top">
    <td class="table_cell"><c:out value="${event.id}"/></td>           
    <td class="table_cell"><c:out value="${event.location}"/>&nbsp;</td>    
    <td class="table_cell"><fmt:formatDate value="${event.createdDate}" pattern="MM/dd/yyyy"/></td>       
    <td class="table_cell"><c:out value="${event.owner.name}"/></td> 
    <td class="table_cell"><c:out value="${event.status.name}"/></td>
    </tr>    
 </c:forEach>    
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<p>
<form action='RestoreSubject?action=submit&id=<c:out value="${subjectToRestore.id}"/>' method="POST">
 <input type="submit" name="submit" value="Restore Subject" class="button_long" onClick='return confirm("If you restore this Subject, all the assigned uses of the Subject and any data entered using it will be restored as well. Are you sure you want to restore it?");'>
</form>

<c:import url="../include/workflow.jsp">
 <c:param name="module" value="admin"/>
</c:import>

<jsp:include page="../include/footer.jsp"/>
