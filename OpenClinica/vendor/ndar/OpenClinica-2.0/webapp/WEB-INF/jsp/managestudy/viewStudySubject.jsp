<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:choose>
 <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
   <c:import url="../include/admin-header.jsp"/>
 </c:when>
 <c:otherwise>
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
    <c:import url="../include/managestudy-header.jsp"/>
   </c:when>
   <c:otherwise>
    <c:import url="../include/submit-header.jsp"/>
   </c:otherwise> 
  </c:choose>
 </c:otherwise>
</c:choose>
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

<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="father" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="mother" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="parentStudy" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope="request" id="children" class="java.util.ArrayList"/>
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.core.EntityBeanTable'/>
<jsp:useBean scope="request" id="groups" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="from" class="java.lang.String"/>

<script language="JavaScript">
       <!--
         function leftnavExpand(strLeftNavRowElementName){

	       var objLeftNavRowElement;

           objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
           if (objLeftNavRowElement != null) {
             if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; } 
	           objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";		
	         }
           }

       //-->
 </script>    

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr><td>
<h1>
<c:choose>
 <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
  <div class="title_Admin">
</c:when>
 <c:otherwise>
 
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
     <div class="title_manage">
   </c:when>
   <c:otherwise>
    <div class="title_submit">
   </c:otherwise> 
  </c:choose>
    
 </c:otherwise>
</c:choose>
View Subject: <c:out value="${studySub.label}"/>
 </div>
 </td>
 <td align="right">
 <!-- <span style="font-size:11px"><a href="#"><img 
		    src="images/bt_View.gif" border="0" alt="View" title="View"></a>View Printable Record</div>-->
		</h1>
</td></tr>
</table>

<p><a href="#events">Events</a> &nbsp; &nbsp; &nbsp; <a href="#group">Group</a> &nbsp;&nbsp;&nbsp; <a href="#log">Recent Activity Log</a> &nbsp;&nbsp;&nbsp;  <a href="#global">Global Subject Record</a></p>
<c:choose>
 <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
  <div class="table_title_Admin">
</c:when>
 <c:otherwise>
 
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
     <div class="table_title_Manage">
   </c:when>
   <c:otherwise>
    <div class="table_title_Submit">
   </c:otherwise> 
  </c:choose>
    
 </c:otherwise>
</c:choose>	
	
<a href="javascript:leftnavExpand('subjectRecord');javascript:setImage('ExpandGroup1','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup1" src="images/bt_Expand.gif" border="0"> Subject Record for <c:out value="${studySub.label}"/></a></div>

<div id="subjectRecord" style="display: none">	
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top" width="330" style="padding-right: 20px">


   
	<!-- These DIVs define shaded box borders -->
	 
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">

			<table border="0" cellpadding="0" cellspacing="0" width="330">

		<!-- Table Actions row (pagination, search, tools) -->

				<tr>

			<!-- Table Tools/Actions cell -->

					<td align="right" valign="top" class="table_actions">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="table_tools">
							<c:if test="${userRole != null }">
                             <c:set var="roleName" value="${userRole.role.name}"/>
                             <c:if test="${roleName=='coordinator' || roleName=='director'}"> 
                             <c:if test="${studySub.status.name=='available'}">  
							<a href="UpdateStudySubject?id=<c:out value="${studySub.id}"/>&action=show">Edit Record</a> 
							</c:if>
							</c:if>
						   </c:if></td>
						</tr>
					</table>
					</td>

			<!-- End Table Tools/Actions cell -->
				</tr>

		<!-- end Table Actions row (pagination, search, tools) -->

				<tr>
					<td valign="top">

			<!-- Table Contents -->

					<table border="0" cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td class="table_header_column_top">Study Subject ID</td>
							<td class="table_cell_top"><c:out value="${studySub.label}"/></td>
						</tr>
						<tr>
							<td class="table_header_column">Secondary Subject ID</td>
							<td class="table_cell"><c:out value="${studySub.secondaryLabel}"/></td>
						</tr>						
						<tr>
							<td class="table_divider" colspan="2">&nbsp;</td>
						</tr>
						
						<tr>
					      <td class="table_header_column_top">Person ID</td>
					      <td class="table_cell_top"><c:out value="${subject.uniqueIdentifier}"/></td>
				        </tr>
						<c:choose>
						<c:when test="${study.studyParameterConfig.collectDob == '1'}">
						<tr>
							<td class="table_header_column_top">Date of Birth</td>
							<td class="table_cell_top"><fmt:formatDate value="${subject.dateOfBirth}" pattern="MM/dd/yyyy"/></td>
						</tr>
						</c:when>
						<c:when test="${study.studyParameterConfig.collectDob == '3'}">
						<tr>
							<td class="table_header_column_top">Date of Birth</td>
							<td class="table_cell_top">Not Used</td>
						</tr>
						</c:when>
						<c:otherwise>
						<tr>
							<td class="table_header_column_top">Year of Birth</td>
							<td class="table_cell_top"><c:out value="${yearOfBirth}"/></td>
						</tr>
						</c:otherwise>
						</c:choose>
						<tr>
							<td class="table_header_column">Gender</td>
							<td class="table_cell">
							 <c:choose>
							 <c:when test="${subject.gender==32}">
							  &nbsp;
							  </c:when>
							  <c:when test="${subject.gender==109 ||subject.gender==77}">
							  Male
							  </c:when>
							  <c:otherwise>
							  Female
							  </c:otherwise>
							 </c:choose>
							
							</td>
						</tr>
						<tr>
					     <td class="table_header_column">Enrollment Date</td>
					     <td class="table_cell_top"><fmt:formatDate value="${studySub.enrollmentDate}" pattern="MM/dd/yyyy"/>&nbsp;</td>
				        </tr>
						
					</table>

			<!-- End Table Contents -->
			
					</td>
				</tr>
			</table>


			</div>

		</div></div></div></div></div></div></div></div>
        
			</td>
			

			<td valign="top" width="350" style="padding-right: 20px">
			
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">
             <table border="0" cellpadding="0" cellspacing="0" width="330">
               <tr>
               <td colspan="2" align="right" valign="top" class="table_actions">&nbsp;
               </td>
               </tr>
               <tr>
				 <td class="table_header_column_top">Study Name</td>
				 <td class="table_cell_top">
				  <c:choose>
                    <c:when test="${study.parentStudyId>0}">
                      <c:out value="${parentStudy.name}"/>
                    </c:when>
                    <c:otherwise>
                      <c:out value="${study.name}"/>
                    </c:otherwise>
                   </c:choose>
                  </td>
				 </tr>
				 <tr>
					<td class="table_header_column">Unique Protocol ID</td>
					<td class="table_cell"><c:out value="${study.identifier}"/></td>
				 </tr>
				 <tr>
					<td class="table_header_column">Site Name</td>
					<td class="table_cell"> 
				    	 <c:if test="${study.parentStudyId>0}">
                            <c:out value="${study.name}"/>
                        </c:if>&nbsp;</td>
				 </tr>
               
			
				<tr>
					<td class="table_divider" colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<td class="table_header_column_top">Date Record Created</td>
					<td class="table_cell_top"><fmt:formatDate value="${studySub.createdDate}" pattern="MM/dd/yyyy"/></td>
				</tr>
				<tr>
				    <td class="table_header_column">Created By</td>
					<td class="table_cell"><c:out value="${studySub.owner.name}"/></td>
				</tr>
				<tr>
					<td class="table_header_column">Date Record Last Updated</td>
					<td class="table_cell"><fmt:formatDate value="${studySub.updatedDate}" pattern="MM/dd/yyyy"/>&nbsp;</td>
				</tr>
				<tr>
				    <td class="table_header_column">Updated By</td>
					<td class="table_cell"><c:out value="${studySub.updater.name}"/>&nbsp;</td>
				</tr>
				<tr>
					<td class="table_header_column">Status</td>
					<td class="table_cell"><c:out value="${studySub.status.name}"/></td>
				</tr>     
             </table>
             </div>

		</div></div></div></div></div></div></div></div>
    
	</td>
		</tr>
	</table>
	<br><br>
 </div>
 
<c:choose>
 <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
  <div class="table_title_Admin">
 </c:when>
 <c:otherwise>
 
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
     <div class="table_title_manage">
   </c:when>
   <c:otherwise>
    <div class="table_title_submit">
   </c:otherwise> 
  </c:choose>
    
 </c:otherwise>
</c:choose>	<a name="events"><a href="javascript:leftnavExpand('subjectEvents');javascript:setImage('ExpandGroup2','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup2" src="images/bt_Expand.gif" border="0"> Events</a></a></div>
<div id="subjectEvents" style="display: ">
<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showStudyEventRow.jsp" /></c:import>
	

</br></br>
</div>

	<div style="width: 250px">

<c:choose>
 <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
  <div class="table_title_Admin">
 </c:when>
 <c:otherwise>
 
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
     <div class="table_title_Manage">
   </c:when>
   <c:otherwise>
    <div class="table_title_Submit">
   </c:otherwise> 
  </c:choose>
    
 </c:otherwise>
</c:choose><a name="group"><a href="javascript:leftnavExpand('groups');javascript:setImage('ExpandGroup3','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup3" src="images/bt_Expand.gif" border="0"> Group</a></a></div>
 <div id="groups" style="display:none">
   <div style="width: 600px">
	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">

			<table border="0" cellpadding="0" cellspacing="0" width="100%">

		<!-- Table Actions row (pagination, search, tools) -->

				<tr>

			<!-- Table Tools/Actions cell -->

					<td align="right" valign="top" class="table_actions">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="table_tools"><a href="UpdateStudySubject?id=<c:out value="${studySub.id}"/>&action=show">Assign Subject to Group</a></td>
						</tr>
					</table>
					</td>

			<!-- End Table Tools/Actions cell -->
				</tr>

		<!-- end Table Actions row (pagination, search, tools) -->

				<tr>
					<td valign="top">

			<!-- Table Contents -->

					<table border="0" cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td class="table_header_row_left">Subject Group Class</td>
							<td class="table_header_row">Study Group</td>
							<td class="table_header_row">Notes</td>
						</tr>
						<c:choose>
						 <c:when test="${!empty groups}">
						  <c:forEach var="group" items="${groups}">
                           <tr>
                            <td class="table_cell_left"><c:out value="${group.groupClassName}"/></td>
                            <td class="table_cell"><c:out value="${group.studyGroupName}"/></td>
                            <td class="table_cell"><c:out value="${group.notes}"/>&nbsp;</td>
                           </tr>
                          </c:forEach>
                         </c:when> 
                         <c:otherwise>
                          <tr>
                            <td class="table_cell" colspan="2">Currently no groups.</td>                           
                           </tr>
                         </c:otherwise>
                       </c:choose>
					</table>

			<!-- End Table Contents -->
			
					</td>
				</tr>
			</table>


			</div>

		</div></div></div></div></div></div></div></div>

		</div>

		<br><br>
</div>

<div style="width: 250px">

<c:choose>
 <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
  <div class="table_title_Admin">
</c:when>
 <c:otherwise>
 
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
     <div class="table_title_Manage">
   </c:when>
   <c:otherwise>
    <div class="table_title_Submit">
   </c:otherwise> 
  </c:choose>
    
 </c:otherwise>
</c:choose><a name="log"><a href="javascript:leftnavExpand('logs');javascript:setImage('ExpandGroup4','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup4" src="images/bt_Expand.gif" border="0"> Recent Activity Log</a></a></div>
<div id="logs" style="display:none">
 <div style="width: 600px">
	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">

			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
							<td class="table_header_column">Date</td>
                            <td class="table_header_column">Study Event</td>
                            <td class="table_header_column">Status Set</td>
                            <td class="table_header_column">Updated By</td>
                            <td class="table_header_column">Reason for Change</td>                           
						</tr>
						<c:forEach var="eventLog" items="${eventLogs}">
						  <tr>
							<td class="table_cell_left">
							<c:if test="${eventLog.auditEvent.auditDate != null}">
							 <fmt:formatDate value="${eventLog.auditEvent.auditDate}" pattern="MM/dd/yyyy"/>
							</c:if>&nbsp;
							</td>
							<td class="table_cell"><c:out value="${eventLog.definition.name}"/></td>
							<td class="table_cell">from <c:out value="${eventLog.oldSubjectEventStatus.name}"/> to <c:out value="${eventLog.newSubjectEventStatus.name}"/></td>
							<td class="table_cell"><c:out value="${eventLog.updater.name}"/></td>
							<td class="table_cell"><c:out value="${eventLog.auditEvent.reasonForChange}"/></td>
						  </tr>
						</c:forEach>
				</table>

		


			</div>

		</div></div></div></div></div></div></div></div>

		</div>

		<br><br>
  </div>
  		
  <c:choose>
 <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
  <div class="table_title_Admin">
</c:when>
 <c:otherwise>
 
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
     <div class="table_title_manage">
   </c:when>
   <c:otherwise>
    <div class="table_title_submit">
   </c:otherwise> 
  </c:choose>
    
 </c:otherwise>
</c:choose>	<a name="global"><a href="javascript:leftnavExpand('globalRecord');javascript:setImage('ExpandGroup5','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup5" src="images/bt_Expand.gif" border="0"> Global Subject Record</a></a></div>
 
 <div id="globalRecord" style="display:none">
  <div style="width: 350px">
	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="tablebox_center">

			<table border="0" cellpadding="0" cellspacing="0" width="330">

		<!-- Table Actions row (pagination, search, tools) -->

				<tr>

			<!-- Table Tools/Actions cell -->

					<td align="right" valign="top" class="table_actions">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="table_tools">
							<c:if test="${userBean.sysAdmin && subject.status.name=='available' }">
                             <a href="UpdateSubject?id=<c:out value="${subject.id}"/>&studySubId=<c:out value="${studySub.id}"/>&action=show">Edit Record</a>
                            </c:if>
                            </td>
						</tr>
					</table>
					</td>

			<!-- End Table Tools/Actions cell -->
				</tr>

		<!-- end Table Actions row (pagination, search, tools) -->

				<tr>
					<td valign="top">

			<!-- Table Contents -->

					<table border="0" cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td class="table_header_column_top">Person ID</td>
							<td class="table_cell_top"><c:out value="${subject.uniqueIdentifier}"/></td>
						</tr>						
						<tr>
							<td class="table_divider" colspan="2">&nbsp;</td>
						</tr>
						<tr>
							<td class="table_header_column_top">Date Record Created</td>
							<td class="table_cell_top"><fmt:formatDate value="${subject.createdDate}" pattern="MM/dd/yyyy"/></td>
						</tr>
						<tr>
							<td class="table_header_column">Created By</td>
							<td class="table_cell"><c:out value="${subject.owner.name}"/></td>
						</tr>
						<tr>
							<td class="table_header_column">Date Record Last Updated</td>
							<td class="table_cell"><fmt:formatDate value="${subject.updatedDate}" pattern="MM/dd/yyyy"/>&nbsp;</td>
						</tr>
						<tr>
							<td class="table_header_column">Updated By</td>
							<td class="table_cell"><c:out value="${subject.updater.name}"/>&nbsp;</td>
						</tr>
						<tr>
							<td class="table_header_column">Status</td>
							<td class="table_cell"><c:out value="${subject.status.name}"/></td>
						</tr>
						<tr>
							<td class="table_divider" colspan="2">&nbsp;</td>
						</tr>
						<c:choose>
						<c:when test="${study.studyParameterConfig.collectDob == '1'}">
						<tr>
							<td class="table_header_column_top">Date of Birth</td>
							<td class="table_cell_top"><fmt:formatDate value="${subject.dateOfBirth}" pattern="MM/dd/yyyy"/></td>
						</tr>
						</c:when>
						<c:when test="${study.studyParameterConfig.collectDob == '3'}">
						<tr>
							<td class="table_header_column_top">Date of Birth</td>
							<td class="table_cell_top">&nbsp;</td>
						</tr>
						</c:when>
						<c:otherwise>
						<tr>
							<td class="table_header_column_top">Year of Birth</td>
							<td class="table_cell_top"><c:out value="${yearOfBirth}"/></td>
						</tr>
						</c:otherwise>
						</c:choose>
						<tr>
							<td class="table_header_column">Gender</td>
							<td class="table_cell">
							 <c:choose>
							 <c:when test="${subject.gender==32}">
							  &nbsp;
							  </c:when>
							  <c:when test="${subject.gender==109 ||subject.gender==77}">
							  Male
							  </c:when>
							  <c:otherwise>
							  Female
							  </c:otherwise>
							 </c:choose>
							
							</td>
						</tr>
						<c:if test="${study.genetic == true}">
						<tr>
							<td class="table_header_column">Mother</td>
							<td class="table_cell"><c:out value="${mother.uniqueIdentifier}"/>&nbsp;</td>
						</tr>
						<tr>
							<td class="table_header_column">Father</td>
							<td class="table_cell"><c:out value="${father.uniqueIdentifier}"/>&nbsp;</td>
						</tr>
						<tr>
							<td class="table_header_column">Children</td>
							<td class="table_cell">
							<c:forEach var="child" items="${children}">         
                              <c:out value="${child.uniqueIdentifier}"/>,
                             </c:forEach>&nbsp;</td>
						</tr>
						</c:if>
					</table>

			<!-- End Table Contents -->
			
					</td>
				</tr>
			</table>


			</div>

		</div></div></div></div></div></div></div></div>
  	</div>	
  	
  </div>	
	<c:choose>
     <c:when test="${from =='listSubject' && userBean.sysAdmin}">
      <p> <a href="ViewSubject?id=<c:out value="${subject.id}"/>">Go Back to View Subject </a>  </p>
    </c:when>
    <c:otherwise>
    
    <c:choose>
     <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
     <p> <a href="ListStudySubject">Go Back to Study Subject List</a>  </p>
     </c:when>
     <c:otherwise>
       <p><a href="ListStudySubjectsSubmit">Go Back to Subject List</a>  </p>
     </c:otherwise>
     </c:choose>
    </c:otherwise>
    </c:choose>
 <!-- End Main Content Area -->      

 <c:import url="../include/workflow.jsp">
  <c:param name="module" value="manage"/>
 </c:import>

<jsp:include page="../include/footer.jsp"/>
