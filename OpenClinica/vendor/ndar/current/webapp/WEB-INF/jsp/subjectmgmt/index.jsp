<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />

<jsp:useBean scope='request' id='labels' class='java.util.ArrayList' />
<jsp:useBean scope='request' id='subs' class='java.util.ArrayList' />

<jsp:include page="../include/subjectMgmt-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<h1><span class="title_SubjectMgmt">Subject Management <a href="javascript:openDocWindow('help/4_0_manageStudy_Help.html')"><img src="images/bt_Help_SubjectMgmt.gif" border="0" alt="Help" title="Help"></a></span></h1>
<jsp:include page="../include/alertbox.jsp"/>

 <p>If you have study director or study coordinator privilege, you may manage your current study and sites (if any), as well as all
 the users, subjects and event definitions in the study and sites. </p><p>Each of the following tables shows the last modified five entities in 
 your current study. Select the links "Show All" to view all entities or "Add New" to create a new entity.
 </p>

<c:set var="subCount" value="0"/>
<c:set var="labelCount" value="0"/>
<c:forEach var="sub" items="${subs}">
<c:set var="subCount" value="${subCount+1}"/>
</c:forEach>
<c:forEach var="label" items="${labels}">
<c:set var="labelCount" value="${labelCount+1}"/>
</c:forEach>

	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top" width="500" style="padding-right: 20px">

	<div class="table_title_Manage">Subjects 
	<c:choose>
	  <c:when test="${subCount>0}">
	   (<c:out value="${subCount}"/> of <c:out value="${subCount}"/> shown)
	  </c:when>
	  <c:otherwise>
	   (currently no subjects)
	  </c:otherwise>
	</c:choose>
	</div>

	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<tr valign="top">
						<td class="table_header_row_left">Person ID</td>
						<td class="table_header_row">Person Name</td> 
						<td class="table_header_row">Gender</td> 
						<td class="table_header_row">Date Updated</td> 
						<td class="table_header_row">Owner</td> 
						<td class="table_header_row">Status</td> 
					</tr>
					<c:forEach var="sub" items="${subs}">
					<tr valign="top">   
						<td class="table_cell_left"><c:out value="${sub.person.personId}"/>&nbsp;</td> 
						<td class="table_cell"><c:out value="${sub.person.fullName}"/>&nbsp;</td> 
						<td class="table_cell"><c:out value="${sub.subject.gender}"/>&nbsp;</td> 
						<td class="table_cell">
						<c:choose>
						<c:when test="${sub.subject.updatedDate != null}">
						 <fmt:formatDate value="${sub.subject.updatedDate}" pattern="MM/dd/yyyy"/>
						 </c:when>
						 <c:otherwise>
						  <fmt:formatDate value="${sub.subject.createdDate}" pattern="MM/dd/yyyy"/>
						 </c:otherwise>
						</c:choose>
						&nbsp;</td>
						<td class="table_cell"><c:out value="${sub.subject.owner.name}"/>&nbsp;</td>
						<td class="table_cell"><c:out value="${sub.subject.status.name}"/>&nbsp;</td>
					</tr>
					</c:forEach>
					<tr valign="top">
					 <td class="table_cell" align="right" colspan="6">
					   <c:if test="${subCount>0}">
					    <a href="ListPiiSubject">Show All</a> |
					   </c:if>
					   <c:if test="${userBean.piiPrivilege.name=='edit'}"> 
					    <a href="AddNewPiiSubject">Add New</a> </td>
					   </c:if>
					</tr>					
				</table>

			</div>

		</div></div></div></div></div></div></div></div>

			</td>
		</tr>
		</table><br>
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top" width="500" style="padding-right: 20px">

	<div class="table_title_Manage">Subject Label
	<c:choose>
	  <c:when test="${labelCount>0}">
	   (<c:out value="${labelCount}"/> of <c:out value="${labelCount}"/> shown)
	  </c:when>
	  <c:otherwise>
	   (currently no subject labels)
	  </c:otherwise>
	</c:choose>
	</div>

	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<tr valign="top">
						<td class="table_header_row_left">Name</td>
						<td class="table_header_row">Description</td> 
						<td class="table_header_row">owner</td> 
						<td class="table_header_row">Date Updated</td> 
					</tr>
					
					<c:forEach var="label" items="${labels}">
					<tr valign="top">   
						<td class="table_cell_left"><c:out value="${label.name}"/>&nbsp;</td> 
						<td class="table_cell"><c:out value="${label.description}" />&nbsp;</td>
						<td class="table_cell"><c:out value="${label.owner.name}" />&nbsp;</td>
						<td class="table_cell">
						<c:choose>
						 <c:when test="${label.updatedDate != null}">
						  <fmt:formatDate value="${label.updatedDate}" pattern="MM/dd/yyyy"/> 
						 </c:when>
						 <c:otherwise>
						  <fmt:formatDate value="${label.createdDate}" pattern="MM/dd/yyyy"/> 
						 </c:otherwise>
						</c:choose> 
						&nbsp;</td>
					</tr>	
					</c:forEach>
					
					  <tr valign="top">
					   <td class="table_cell" align="right" colspan="4">
					    <c:if test="${labelCount>0}">
					     <a href="ListSubjectEntryLabels">Show All</a> | 
					    </c:if>
					    <c:if test="${userBean.piiPrivilege.name=='edit'}"> 
					     <a href="CreateSubjectEntryLabel">Add New</a> </td>
					    </c:if>
					  </tr>
				</table>

			</div>

		</div></div></div></div></div></div></div></div>

			</td>
		</tr>
	</table>





<!-- End Main Content Area -->

<jsp:include page="../include/footer.jsp"/>
