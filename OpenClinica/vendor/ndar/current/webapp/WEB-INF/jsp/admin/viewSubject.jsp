<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/admin-header.jsp"/>
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

<jsp:useBean scope='request' id='subject' class='org.akaza.openclinica.bean.submit.SubjectBean'/>
<jsp:useBean scope='request' id='studySubs' class='java.util.ArrayList'/>
<h1><span class="title_Admin">
View Subject Details
</span></h1>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0">
 <tr valign="top" ><td class="table_header_column_top">Person ID:</td><td class="table_cell_top">  
  <c:out value="${subject.uniqueIdentifier}"/>
   &nbsp;</td></tr>
  <tr valign="top"><td class="table_header_column">gender:</td><td class="table_cell">  
  <c:out value="${subject.gender}"/>
  </td>
  <tr valign="top"><td class="table_header_column">Date Of Birth:</td><td class="table_cell">  
  <fmt:formatDate value="${subject.dateOfBirth}" pattern="MM/dd/yyyy"/>
  </td>
  <tr valign="top"><td class="table_header_column">Date Created:</td><td class="table_cell">  
  <fmt:formatDate value="${subject.createdDate}" pattern="MM/dd/yyyy"/>
  </td>
  </tr> 
</table>
</div>
</div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>
<br/><br/>
<div class="table_title_Admin"> Associated Study Subjects </div>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0"> 
 <tr valign="top">
    <td class="table_header_row_left">Study Subject ID</td>       
    <td class="table_header_row">Secondary Label</td> 
    <td class="table_header_row">Study Id</td>
    <td class="table_header_row">Enrollment Date</td> 
    <td class="table_header_row">Date Created</td> 
    <td class="table_header_row">Created By</td> 
    <td class="table_header_row">Status</td>  
     <td class="table_header_row">Action</td>       
    </tr>   
  <c:forEach var="studySub" items="${studySubs}">   
    <tr valign="top">
    <td class="table_cell_left"><c:out value="${studySub.label}"/></td>               
    <td class="table_cell"><c:out value="${studySub.secondaryLabel}"/>&nbsp;</td> 
    <td class="table_cell"><c:out value="${studySub.studyId}"/></td>
    <td class="table_cell"><fmt:formatDate value="${studySub.enrollmentDate}" pattern="MM/dd/yyyy"/></td>       
    <td class="table_cell"><fmt:formatDate value="${studySub.createdDate}" pattern="MM/dd/yyyy"/></td>       
    <td class="table_cell"><c:out value="${studySub.owner.name}"/></td>  
    <td class="table_cell"><c:out value="${studySub.status.name}"/></td> 
    <td class="table_cell">    
     <a href="ViewStudySubject?id=<c:out value="${studySub.id}"/>&from=listSubject"
	  onMouseDown="setImage('bt_View1','images/bt_View_d.gif');"
	  onMouseUp="setImage('bt_View1','images/bt_View.gif');"><img 
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>

    </td>      
    </tr>  
 </c:forEach>    
</table>

</div>

</div></div></div></div></div></div></div></div>

		</td>
	</tr>
</table>
<br><p><a href="ListSubject">Go Back to Subject List</a></p>
 <c:import url="../include/workflow.jsp">
  <c:param name="module" value="admin"/>
 </c:import>
<jsp:include page="../include/footer.jsp"/>