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

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		  
        
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="session" id="subjectToUpdate" class="org.akaza.openclinica.bean.submit.SubjectBean" />


<h1><span class="title_Admin">Confirm Subject Details </span></h1>
<P>* indicates required field.</P>
<form action="UpdateSubject" method="post">
<input type="hidden" name="action" value="submit">
<input type="hidden" name="id" value="<c:out value="${subjectToUpdate.id}"/>">
<input type="hidden" name="studySubId" value="<c:out value="${studySubId}"/>">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr valign="top">
	  	<td class="table_header_column">Person ID:</td>
		<td class="table_cell">
		  <c:out value="${subjectToUpdate.uniqueIdentifier}"/>
		</td>
	</tr>
	
	<tr valign="top">
		<td class="table_header_column">Gender:</td>
		<td class="table_cell">
		 <c:choose>
		 <c:when test="${subjectToUpdate.gender == 109}">
          Male
         </c:when>
         <c:when test="${subjectToUpdate.gender == 102}">
          Female
         </c:when>
         <c:otherwise>
         Not Specified        
        </c:otherwise>
        </c:choose>
		</td>
	</tr>
	
	<c:choose>
	<c:when test="${subjectToUpdate.dobCollected}">
	<tr valign="top">
		<td class="table_header_column">Date of Birth:</td>
	  	<td class="table_cell">
		  <fmt:formatDate value="${subjectToUpdate.dateOfBirth}"  pattern="MM/dd/yyyy"/>	  
	  	</td>
	</tr>
    </c:when>
    <c:otherwise>
      <tr valign="top">
		<td class="table_header_column">Year of Birth:</td>
	  	<td class="table_cell">
		  <c:out value="${yearOfBirth}"/>	  
	  	</td>
	</tr>
    </c:otherwise>
    </c:choose>
	<tr>
		<td class="table_header_column">Father:
	  	<td class="table_cell">
			<c:out value="${disFather.studySubjectIds}"/>&nbsp;
		</td>
	</tr>

	
	<tr>
		<td class="table_header_column">Mother:
	  	<td class="table_cell">
		<c:out value="${disMother.studySubjectIds}"/>&nbsp;
		</td>
	</tr>
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>   
 <input type="submit" name="Submit" value="Submit" class="button_medium">
</form>

<c:import url="../include/workflow.jsp">
  <c:param name="module" value="admin"/>
 </c:import>
<jsp:include page="../include/footer.jsp"/>
