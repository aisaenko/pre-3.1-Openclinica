<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/admin-header.jsp"/>
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
        As a Business Administrator you have privileges to administer all subjects, users, CRFs and studies/sites created across the overall system. <br><br>
        Each of the following tables shows the last five modified entities in current system. Select the link "Show All" to view all entities or "Add New" to create a new entity.
			
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

<h1><span class="title_Admin">
Business Administrator <a href="javascript:openDocWindow('help/5_0_administerSystem_Help.html')"><img src="images/bt_Help_Admin.gif" border="0" alt="Help" title="Help"></a>
</span></h1>



	<h2>Recent Activity</h2>
<c:set var="userCount" value="0"/>
<c:forEach var="user" items="${users}">
  <c:set var="userCount" value="${userCount+1}"/>
</c:forEach>
<c:set var="studyCount" value="0"/>
<c:forEach var="study" items="${studies}">
  <c:set var="studyCount" value="${studyCount+1}"/>
</c:forEach>
<c:set var="subjectCount" value="0"/>
<c:forEach var="subject" items="${subjects}">
  <c:set var="subjectCount" value="${subjectCount+1}"/>
</c:forEach>
<c:set var="crfCount" value="0"/>
<c:forEach var="crf" items="${crfs}">
  <c:set var="crfCount" value="${crfCount+1}"/>
</c:forEach>

	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top" width="330" style="padding-right: 20px">
    <div class="table_title_Admin">Subjects 
	<c:choose>
	  <c:when test="${allSubjectNumber>0}">
	   (<c:out value="${subjectCount}"/> of <c:out value="${allSubjectNumber}"/> shown)
	  </c:when>
	  <c:otherwise>
	   (currently no subjects)
	  </c:otherwise>
	</c:choose></div>

	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<tr valign="top">
						<td class="table_header_row_left">Person ID</td>
						<td class="table_header_row">Date Updated</td> 
						<td class="table_header_row">Status</td> 
					</tr>
					<c:forEach var="subject" items="${subjects}">
					<tr valign="top">   
						<td class="table_cell_left"><c:out value="${subject.uniqueIdentifier}"/>&nbsp;</td> 
						<td class="table_cell">
						 <c:choose>
						  <c:when test="${subject.updatedDate != null}">
						   <fmt:formatDate value="${subject.updatedDate}" pattern="MM/dd/yyyy"/>
						  </c:when>
						  <c:otherwise>
						    <fmt:formatDate value="${subject.createdDate}" pattern="MM/dd/yyyy"/>
						  </c:otherwise>
						 </c:choose> 
						</td>
						<td class="table_cell"><c:out value="${subject.status.name}"/></td>
					</tr>	
					</c:forEach>
					<tr valign="top">
					 <td class="table_cell" align="right" colspan="3"><c:if test="${subjectCount>0}"><a href="ListSubject">Show All</a></c:if> </td>
					</tr>				
				</table>
	
			</div>

		</div></div></div></div></div></div></div></div>

			</td>
			<td valign="top" width="330" style="padding-right: 20px">
              <div class="table_title_Admin">Users 
	<c:choose>
	  <c:when test="${allUserNumber>0}">
	   (<c:out value="${userCount}"/> of <c:out value="${allUserNumber}"/> shown)
	  </c:when>
	  <c:otherwise>
	   (currently no users)
	  </c:otherwise>
	</c:choose></div>

	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<tr valign="top">
						<td class="table_header_row_left">User Name</td>
						<td class="table_header_row">Date Updated</td> 
						<td class="table_header_row">Status</td> 
					</tr>
					<c:forEach var="user" items="${users}">
					 <tr valign="top">   
						<td class="table_cell_left"><c:out value="${user.name}"/></td> 
						<td class="table_cell">
						<c:choose>
						 <c:when test="${user.updatedDate != null}">
						  <fmt:formatDate value="${user.updatedDate}" pattern="MM/dd/yyyy"/>
						 </c:when> 
						 <c:otherwise>
						    <fmt:formatDate value="${user.createdDate}" pattern="MM/dd/yyyy"/>
						 </c:otherwise>
						</c:choose>
						</td>
						<td class="table_cell"><c:out value="${user.status.name}"/></td>
					 </tr>
					</c:forEach>
					<tr valign="top">
					 <td class="table_cell" align="right" colspan="3"><c:if test="${userCount>0}"><a href="ListUserAccounts">Show All</a> |</c:if> <a href="CreateUserAccount">Add New</a> </td>
					</tr>					
				</table>				
	
	
			</div>

		</div></div></div></div></div></div></div></div>


	</td>
		</tr>
		</table><br>
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top" width="330" style="padding-right: 20px">
   <div class="table_title_Admin">
	Studies
	<c:choose>
	  <c:when test="${allStudyNumber>0}">
	   (<c:out value="${studyCount}"/> of <c:out value="${allStudyNumber}"/> shown)
	  </c:when>
	  <c:otherwise>
	   (currently no studies)
	  </c:otherwise>
	</c:choose></div>

	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<tr valign="top">
						<td class="table_header_row_left">Name</td>
						<td class="table_header_row">Date Updated</td> 
						<td class="table_header_row">Status</td> 
					</tr>
					<c:forEach var="study" items="${studies}">
					<tr valign="top">   
						<td class="table_cell_left"><c:out value="${study.name}"/></td> 
						<td class="table_cell">
						<c:choose>
						 <c:when test="${study.updatedDate != null}">
						  <fmt:formatDate value="${study.updatedDate}" pattern="MM/dd/yyyy"/>
						 </c:when>
						 <c:otherwise>
						  <fmt:formatDate value="${study.createdDate}" pattern="MM/dd/yyyy"/>
						 </c:otherwise>
						</c:choose> 
						</td>
						<td class="table_cell"><c:out value="${study.status.name}"/></td>
					</tr>
					</c:forEach>
					<tr valign="top">
					 <td class="table_cell" align="right" colspan="3"><c:if test="${studyCount>0}"><a href="ListStudy">Show All</a> | </c:if><a href="CreateStudy">Add New</a> </td>
					</tr>					
				</table>
   
   
 

			</div>

		</div></div></div></div></div></div></div></div>

			</td>
			<td valign="top" width="330" style="padding-right: 20px">

	<div class="table_title_Admin">CRFs
	<c:choose>
	  <c:when test="${allCrfNumber>0}">
	   (<c:out value="${crfCount}"/> of <c:out value="${allCrfNumber}"/> shown)
	  </c:when>
	  <c:otherwise>
	   (currently no crfs)
	  </c:otherwise>
	</c:choose></div>

	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<tr valign="top">
						<td class="table_header_row_left">Name</td>
						<td class="table_header_row">Date Updated</td> 
						<td class="table_header_row">Status</td> 
					</tr>
					<c:forEach var="crf" items="${crfs}">
					<tr valign="top">   
						<td class="table_cell_left"><c:out value="${crf.name}"/></td> 
						<td class="table_cell">
						 <c:choose>
						  <c:when test="${crf.updatedDate != null}">
						   <fmt:formatDate value="${crf.updatedDate}" pattern="MM/dd/yyyy"/>
						  </c:when>
						  <c:otherwise>
						    <fmt:formatDate value="${crf.createdDate}" pattern="MM/dd/yyyy"/>
						  </c:otherwise>
						 </c:choose>  
						</td>
						<td class="table_cell"><c:out value="${crf.status.name}"/></td>
					</tr>
					</c:forEach>
					<tr valign="top">
					 <td class="table_cell" align="right" colspan="3"><c:if test="${crfCount>0}"><a href="ListCRF">Show All</a> | </c:if><a href="CreateCRF">Add New</a> </td>
					</tr>
				</table>

			</div>

		</div></div></div></div></div></div></div></div>


	</td>
		</tr>
	</table>


<c:import url="../include/workflow.jsp">
 <c:param name="module" value="admin"/>
</c:import>


<jsp:include page="../include/footer.jsp"/>