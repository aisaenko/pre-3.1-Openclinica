<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />

<jsp:useBean scope='request' id='sites' class='java.util.ArrayList' />
<jsp:useBean scope='request' id='seds' class='java.util.ArrayList' />
<jsp:useBean scope='request' id='users' class='java.util.ArrayList' />
<jsp:useBean scope='request' id='subs' class='java.util.ArrayList' />
<jsp:useBean id="audits" scope="request" class="java.util.ArrayList" />

<jsp:include page="../include/managestudy-header.jsp"/>
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

		   If you have study director or study coordinator privilege, you may manage your current study and sites (if any), as well as all the users, subjects and event definitions in the study and sites. <br><br>

           Each of the right side tables shows the last modified five entities in your current study. Select the links "Show All" to view all entities or "Add New" to create a new entity.
			
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

<h1><span class="title_manage">Manage Study <a href="javascript:openDocWindow('help/4_0_manageStudy_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="Help" title="Help"></a></span></h1>


<h2>Recent Activity <span style="font-size:12px">(select to manage or view all)</span></h2>


	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top" width="330" style="padding-right: 20px">

	<div class="table_title_Manage">Subjects 
	<c:choose>
	  <c:when test="${subsCount>0}">
	   (<c:out value="${subsCount}"/> of <c:out value="${allSubsCount}"/> shown)
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
						<td class="table_header_row_left">Study Subject ID</td>
						<td class="table_header_row">Date Updated</td> 
						<td class="table_header_row">Status</td> 
					</tr>
					<c:forEach var="sub" items="${subs}">
					<tr valign="top">   
						<td class="table_cell_left"><c:out value="${sub.label}"/></td> 
						<td class="table_cell">
						<c:choose>
						<c:when test="${sub.updatedDate != null}">
						 <fmt:formatDate value="${sub.updatedDate}" pattern="MM/dd/yyyy"/>
						 </c:when>
						 <c:otherwise>
						  <fmt:formatDate value="${sub.createdDate}" pattern="MM/dd/yyyy"/>
						 </c:otherwise>
						</c:choose>
						</td>
						<td class="table_cell"><c:out value="${sub.status.name}"/></td>
					</tr>
					</c:forEach>
					<tr valign="top">
					 <td class="table_cell" align="right" colspan="3">
					   <c:if test="${subCount>0}">
					    <a href="ListStudySubject">Show All</a> | 
					   </c:if>
					 <a href="AddNewSubject">Add New</a> </td>
					</tr>					
				</table>

			</div>

		</div></div></div></div></div></div></div></div>

			</td>
			<td valign="top" width="330" style="padding-right: 20px">

	<div class="table_title_Manage">Users
	<c:choose>
	  <c:when test="${usersCount>0}">
	   (<c:out value="${usersCount}"/> of <c:out value="${allUsersCount}"/> shown)
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
						<td class="table_header_row">Role</td> 
						<td class="table_header_row">Status</td> 
					</tr>
					<c:forEach var="user" items="${users}">
					 <tr valign="top">   
						<td class="table_cell_left"><c:out value="${user.userName}"/></td> 
						<td class="table_cell"><c:out value="${user.role.description}"/></td>
						<td class="table_cell"><c:out value="${user.status.name}"/></td>
					 </tr>
					</c:forEach>
					<tr valign="top">
					 <td class="table_cell" align="right" colspan="3"> <c:if test="${userCount>0}"><a href="ListStudyUser">Show All</a> | </c:if><a href="AssignUserToStudy">Add New</a> </td>
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

	<div class="table_title_Manage">Sites
	<c:choose>
	  <c:when test="${sitesCount>0}">
	   (<c:out value="${sitesCount}"/> of <c:out value="${allSitesCount}"/> shown)
	  </c:when>
	  <c:otherwise>
	   (currently no sites)
	  </c:otherwise>
	</c:choose>
	</div>

	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<tr valign="top">
						<td class="table_header_row_left">Name</td>
						<td class="table_header_row">Date Updated</td> 
						<td class="table_header_row">Status</td> 
					</tr>
					<c:choose>
					 <c:when test="${study.parentStudyId>0}">
					  <tr valign="top"><td class="table_cell" colspan="3">Site itself cannot have sites.</td></tr>
					  
					 </c:when>
					<c:otherwise>
					
					<c:forEach var="site" items="${sites}">
					<tr valign="top">   
						<td class="table_cell_left"><c:out value="${site.name}"/></td> 
						<td class="table_cell">
						<c:choose>
						 <c:when test="${site.updatedDate != null}">
						  <fmt:formatDate value="${site.updatedDate}" pattern="MM/dd/yyyy"/> 
						 </c:when>
						 <c:otherwise>
						   <fmt:formatDate value="${site.createdDate}" pattern="MM/dd/yyyy"/> 
						 </c:otherwise>
						</c:choose> 
						</td>
						<td class="table_cell"><c:out value="${site.status.name}"/></td>
					</tr>	
					
					</c:forEach>
					</c:otherwise>
					</c:choose>
					 <c:if test="${study.parentStudyId==0}">
					  <tr valign="top">
					   <td class="table_cell" align="right" colspan="3">
					    <c:if test="${siteCount>0}">
					     <a href="ListSite">Show All</a> | 
					    </c:if>
					    <a href="CreateSubStudy">Add New</a> </td>
					  </tr>
					</c:if>				
				</table>

			</div>

		</div></div></div></div></div></div></div></div>

			</td>
			<td valign="top" width="330" style="padding-right: 20px">

	<div class="table_title_Manage">Study Event Definitions
	<c:choose>
	  <c:when test="${sedsCount>0}">
	   (<c:out value="${sedsCount}"/> of <c:out value="${allSedsCount}"/> shown)
	  </c:when>
	  <c:otherwise>	
	   (currently no definitions) 	  
	  </c:otherwise>
	</c:choose>
	</div>

	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<tr valign="top">
						<td class="table_header_row_left">Name</td>
						<td class="table_header_row">Date Updated</td> 
						<td class="table_header_row">Status</td> 
					</tr>
					<c:choose>
					  <c:when test="${study.parentStudyId>0}">
					    <tr valign="top"><td class="table_cell" colspan="3">Site itself cannot have definitions.</td></tr>
					  </c:when>
					  <c:otherwise>
					   <c:forEach var="def" items="${seds}">
					     <tr valign="top">   
						  <td class="table_cell_left"><c:out value="${def.name}"/></td> 
						  <td class="table_cell">
						   <c:choose>
						   <c:when test="${def.updatedDate != null}">
						    <fmt:formatDate value="${def.updatedDate}" pattern="MM/dd/yyyy"/>
						   </c:when>
						   <c:otherwise>
						     <fmt:formatDate value="${def.createdDate}" pattern="MM/dd/yyyy"/>
						   </c:otherwise>
						  </c:choose> 
						  </td>
						  <td class="table_cell"><c:out value="${def.status.name}"/></td>
					    </tr>
					   </c:forEach>
					  </c:otherwise>
					</c:choose>
					
					 <c:if test="${study.parentStudyId==0}">
					   <tr valign="top">
					    <td class="table_cell" align="right" colspan="3">
					     <c:if test="${sedsCount>0}">					  
					       <a href="ListEventDefinition">Show All</a> | 
					      </c:if>
					     <a href="DefineStudyEvent">Add New</a> 
					    </td>
					  </tr>
					 </c:if>
					
				</table>

			</div>

		</div></div></div></div></div></div></div></div>


	</td>
		</tr>
		<tr><!-- extra row added by tbh, to support direct link to study audit logs -->
			<td colspan="2">
			<!-- following code clipped from view study subject, tbh -->
		
<div style="width: 250px">


     <div class="table_title_Manage">
  <a name="log"></a><a href="javascript:leftnavExpand('logs');javascript:setImage('ExpandGroup4','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup4" src="images/bt_Expand.gif" border="0"> Recent Activity Log</a>
	     </div>
	<div class="table_title_Manage">
	<c:choose>
	  <c:when test="${auditsCount>0}">
	   (<c:out value="${auditsCount}"/> of <c:out value="${allAuditsCount}"/> shown)
	  </c:when>
	  <c:otherwise>	
	   (currently no activity)
	  </c:otherwise>
	</c:choose>
	</div>
<div id="logs" style="display:none">
 <div style="width: 600px">
	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">

			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
							<td class="table_header_column">Date</td>
                            <td class="table_header_column">Action/Message</td>
                            <td class="table_header_column">Entity/Operation</td>
                            <td class="table_header_column">Subject Unique ID</td>
                            <td class="table_header_column">Updated by</td>                           
						</tr>
						<c:forEach var="audit" items="${audits}">
						  <tr>
							<td class="table_cell">
							<c:if test="${audit.auditDate != null}">
							 <fmt:formatDate value="${audit.auditDate}" pattern="MM/dd/yyyy hh:mm:ss a"/>
							</c:if>&nbsp;
							</td>
							<td class="table_cell"><c:out value="${audit.actionMessage}"/></td>
							<td class="table_cell"><c:out value="${audit.auditTable}"/></td>
							<td class="table_cell"><c:out value="${audit.subjectName}"/></td>
							<td class="table_cell"><c:out value="${audit.updater.name}"/></td>
						  </tr>
						</c:forEach>
						<tr valign="top">
					   <td class="table_cell" align="right" colspan="5">
					     <a href="AuditLogStudy">Show All</a> 
					  </td>
					  </tr>
				</table>

		


			</div>

		</div></div></div></div></div></div></div></div>

		</div>

		<br><br>
  </div>
			
			<!-- above code clipped from view study subject, tbh -->
			
			</td>
		</tr>
	</table>





<!-- End Main Content Area -->
<c:import url="../include/workflow.jsp">
  <c:param name="module" value="manage"/>
</c:import>

<jsp:include page="../include/footer.jsp"/>
