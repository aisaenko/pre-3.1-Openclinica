<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/subjectMgmt-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='request' id='person' class='org.akaza.openclinica.bean.subject.Person'/>
<jsp:useBean scope='request' id='subject' class='org.akaza.openclinica.bean.submit.SubjectBean'/>
<jsp:useBean scope='request' id='studySubs' class='java.util.ArrayList'/>
<h1><span class="title_Admin">
View Subject Details
</span></h1>

<div style="width: 400px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="table_header_column_top">First Name:</td>
		<td class="table_cell_top"><c:out value="${person.surname}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Last Name:</td>
		<td class="table_cell"><c:out value="${person.givenName}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Street Address:</td>
		<td class="table_cell"><c:out value="${person.streetAddress}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">City:</td>
		<td class="table_cell"><c:out value="${person.localityName}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">State:</td>
		<td class="table_cell"><c:out value="${person.stateOrProvinceName}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Postal Code / ZIP Code:</td>
		<td class="table_cell"><c:out value="${person.postalCode}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Telephone:</td>
		<td class="table_cell"><c:out value="${person.telephoneNumber}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Email:</td>
		<td class="table_cell"><c:out value="${person.emailAddress}" />&nbsp;</td>
	</tr>
<!-- TODO:
for each study user is in, show:
•	Role
•	Studies created/owned
•	CRFs created/owned (including versions)
•	Study Events created/owned
•	Subjects created/owned
•	Queries created/owned
•	Datasets downloaded
•	Link to reload page including full audit record for User.

-->

	</table>
	</div>

	</div></div></div></div></div></div></div></div>

	</div>


<br/><br/>
<div class="table_title_Admin"> Associated Study Subject record </div>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0">
 <tr valign="top" ><td class="table_header_column_top">Person ID:</td><td class="table_cell_top">  
  <c:out value="${subject.uniqueIdentifier}"/>
   &nbsp;</td></tr>
  <tr valign="top"><td class="table_header_column">Gender:</td><td class="table_cell">  
  <c:out value="${subject.gender}"/>&nbsp;
  </td>
  <tr valign="top"><td class="table_header_column">Date Of Birth:</td><td class="table_cell">  
  <fmt:formatDate value="${subject.dateOfBirth}" pattern="MM/dd/yyyy"/>&nbsp;
  </td>
  <tr valign="top"><td class="table_header_column">Date Created:</td><td class="table_cell">  
  <fmt:formatDate value="${subject.createdDate}" pattern="MM/dd/yyyy"/>&nbsp;
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
    <td class="table_header_row_left">Subject Unique Idenitifier</td>       
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
	  onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
	  onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
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
<br><p><a href="ListPiiSubject">Go Back to Subject List</a></p>
<jsp:include page="../include/footer.jsp"/>