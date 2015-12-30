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

<jsp:useBean scope="session" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>

<h1><span class="title_manage">
Confirm Study Subject Updates
</span></h1>

<form action="UpdateStudySubject" method="post">
<input type="hidden" name="action" value="submit">
<input type="hidden" name="id" value="<c:out value="${studySub.id}"/>">
 <div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="bottom"><td class="table_header_column">Study Subject ID:</td><td class="table_cell"><c:out value="${studySub.label}"/></td></tr>
  <tr valign="bottom"><td class="table_header_column">Secondary Label:</td><td class="table_cell"><c:out value="${studySub.secondaryLabel}"/>&nbsp;
  </td></tr>
  <tr valign="bottom"><td class="table_header_column">Enrollment Date:</td>
  <td class="table_cell"><fmt:formatDate value="${studySub.enrollmentDate}" pattern="MM/dd/yyyy"/></td></tr> 
  <tr valign="top"><td class="table_header_column">Created By:</td><td class="table_cell"><c:out value="${studySub.owner.name}"/></td></tr>
  <tr valign="top"><td class="table_header_column">Date Created:</td><td class="table_cell"><fmt:formatDate value="${studySub.createdDate}" pattern="MM/dd/yyyy"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column">Last Updated By:</td><td class="table_cell"><c:out value="${studySub.updater.name}"/>&nbsp;
  </td></tr>
  <tr valign="top"><td class="table_header_column">Date Updated:</td><td class="table_cell"><fmt:formatDate value="${studySub.updatedDate}" pattern="MM/dd/yyyy"/>&nbsp;
  </td></tr>
 
</table> 
</div>
</div></div></div></div></div></div></div></div>

</div> 
<c:if test="${(!empty groups)}">
<br>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0"> 

  <tr valign="top">
	<td class="table_header_column">Group Template:
	<td class="table_cell">
	
	<table border="0" cellpadding="0">
	  <c:forEach var="group" items="${groups}">
	  <tr valign="top">
	   <td><b><c:out value="${group.name}"/></b></td>
	   <td><c:out value="${group.studyGroupName}"/></td></tr>
	    <tr valign="top">
	      <td>Notes:</td>
	      <td><c:out value="${group.groupNotes}"/></td>
	    </tr>	       
	    <tr valign="top">
	      <td>Template Start Date:</td>
	      <td><fmt:formatDate value="${group.studyTemplateStartDate}" pattern="MM/dd/yyyy"/></td>
	    </tr>	   	       
	  </c:forEach>
	  </table>
	</td>
  </tr>	



</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
</c:if>
 <input type="submit" name="Submit" value="Submit" class="button_medium">
<br>
</form>
 <c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/> 
 </c:import>
<jsp:include page="../include/footer.jsp"/>
