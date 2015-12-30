<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/submit-header.jsp"/>
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
<jsp:include page="../include/submitSideInfo.jsp"/>

<jsp:useBean scope="request" id="studyEvent" class="org.akaza.openclinica.bean.managestudy.StudyEventBean" />
<jsp:useBean scope="request" id="studySubject" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean" />
<jsp:useBean scope="request" id="uncompletedEventDefinitionCRFs" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="displayEventCRFs" class="java.util.ArrayList" />

<h1><span class="title_submit">Enter or Validate Data for CRFs in <c:out value="${studyEvent.studyEventDefinition.name}" /> <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step2a')"><img src="images/bt_Help_Submit.gif" border="0" alt="Help" title="Help"></a> </span></h1>


<br>
<b>Study Event</b>: <c:out value="${studyEvent.studyEventDefinition.name}"/> &nbsp;
<b>Location</b>: <c:out value="${studyEvent.location}"/><br>
<b>Start Date</b>: <fmt:formatDate value="${studyEvent.dateStarted}" pattern="MM/dd/yyyy"/> &nbsp;
<b>End Date</b>: <fmt:formatDate value="${studyEvent.dateEnded}" pattern="MM/dd/yyyy"/><br>
<b>Last Updated By</b>: <c:out value="${studyEvent.updater.name}"/> (<fmt:formatDate value="${studyEvent.updatedDate}" pattern="MM/dd/yyyy"/>)<br>
<b>Subject Event Status</b>: <c:out value="${studyEvent.subjectEventStatus.name}"/>
<br>
<a href="UpdateStudyEvent?event_id=<c:out value="${studyEvent.id}"/>&ss_id=<c:out value="${studySubject.id}"/>"><img src="images/bt_Edit.gif" border="0" align="left"></a>&nbsp;<a href="UpdateStudyEvent?event_id=<c:out value="${studyEvent.id}"/>&ss_id=<c:out value="${studySubject.id}"/>">Edit Study Event</a>

<p><div class="table_title_submit">CRFs in this Study Event:</div>

<div style="width: 600px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<c:choose>
	<c:when test="${empty uncompletedEventDefinitionCRFs && empty displayEventCRFs}">
		<tr>
			<td class="table_cell_left">There are no CRFs in this study event.</td>
		</tr>
	</c:when>
	<c:otherwise>
		<tr>
			<td class="table_header_row_left">CRF Name</td>
			<td class="table_header_row">Version</td>
			<td class="table_header_row">Status</td>
			<td class="table_header_row">Initial Data Entry</td>
			<td class="table_header_row">Validation</td>			
			<td class="table_header_row">Actions</td>
		</tr>
		<c:set var="rowCount" value="${0}" />
		<c:forEach var="dedc" items="${uncompletedEventDefinitionCRFs}">
			<c:set var="getQuery" value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${studyEvent.id}&subjectId=${studySubject.subjectId}&eventCRFId=${dedc.eventCRF.id}" />
				<tr valign="top">
					<td class="table_cell_left"><c:out value="${dedc.edc.crf.name}" /></td>
				<form name="startForm<c:out value="${dedc.edc.crf.id}"/>" action="InitialDataEntry?<c:out value="${getQuery}"/>" method="POST">
				  <td class="table_cell">
					<input type="hidden" name="crfVersionId" value="<c:out value="${dedc.edc.defaultVersionId}"/>">	 
				  <c:set var="versionCount" value="0"/>
				  <c:forEach var="version" items="${dedc.edc.versions}"> 
				    <c:set var="versionCount" value="${versionCount+1}"/>
				  </c:forEach>
				  
				  <c:choose>
				    <c:when test="${versionCount<=1}">
				      <c:forEach var="version" items="${dedc.edc.versions}"> 
				        <c:out value="${version.name}"/>
				      </c:forEach>
				    </c:when>
				    <c:otherwise>
					<select name="versionId<c:out value="${dedc.edc.crf.id}"/>" onchange="javascript:changeQuery<c:out value="${dedc.edc.crf.id}"/>();">  
				      <c:forEach var="version" items="${dedc.edc.versions}">   
				       <c:set var="getQuery" value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${currRow.bean.studyEvent.id}&subjectId=${studySub.subjectId}" />       
                       <c:choose>
                         <c:when test="${dedc.edc.defaultVersionId==version.id}">
                           <option value="<c:out value="${version.id}"/>" selected><c:out value="${version.name}"/>
                         </c:when>
                         <c:otherwise>
                            <option value="<c:out value="${version.id}"/>"><c:out value="${version.name}"/>
                         </c:otherwise>
                       </c:choose>        
             
                      </c:forEach>
                    </select>
                    
                      <SCRIPT LANGUAGE="JavaScript">  
                        function changeQuery<c:out value="${dedc.edc.crf.id}"/>() {
                          var qer = document.startForm<c:out value="${dedc.edc.crf.id}"/>.versionId<c:out value="${dedc.edc.crf.id}"/>.value;
                          document.startForm<c:out value="${dedc.edc.crf.id}"/>.crfVersionId.value=qer;   
                                                    
                        }
                     </SCRIPT>	
                     </c:otherwise>
                     </c:choose>
					</td>
					<td class="table_cell" bgcolor="#F5F5F5" align="center"><img src="images/icon_NotStarted.gif" alt="Not Started" title="Not Started"></td>
					<td class="table_cell">&nbsp;</td>
					<td class="table_cell">&nbsp;</td>
					
					<td class="table_cell">
						<a href="#" onclick="javascript:document.startForm<c:out value="${dedc.edc.crf.id}"/>.submit();"
							onMouseDown="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData_d.gif');"
							onMouseUp="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData.gif');"
						><img name="bt_EnterData<c:out value="${rowCount}"/>" src="images/bt_EnterData.gif" border="0" alt="Enter Data" title="Enter Data" align="left" hspace="6"></a>&nbsp;
					</td>
				</form>	
				</tr>
				
				<c:set var="rowCount" value="${rowCount + 1}" />
		
		</c:forEach>
		<c:forEach var="dec" items="${displayEventCRFs}">
			<tr>
				<td class="table_cell"><c:out value="${dec.eventCRF.crf.name}" />&nbsp;</td>
				<td class="table_cell"><c:out value="${dec.eventCRF.crfVersion.name}" />&nbsp;</td>				
				<td class="table_cell" bgcolor="#F5F5F5" align="center">
				  <c:choose>		 
		           <c:when test="${dec.stage.name=='initial data entry'}">
		             <img src="images/icon_InitialDE.gif" alt="Initial Data Entry" title="Initial Data Entry">
		           </c:when>
		           <c:when test="${dec.stage.name=='initial data entry complete'}">
		             <img src="images/icon_InitialDEcomplete.gif" alt="Initial Data Entry Complete" title="Initial Data Entry Complete">
		           </c:when>
		           <c:when test="${dec.stage.name=='double data entry'}">
		             <img src="images/icon_DDE.gif" alt="Double Data Entry" title="Double Data Entry">
		           </c:when>
		           <c:when test="${dec.stage.name=='data entry complete'}">
		             <img src="images/icon_DEcomplete.gif" alt="Data Entry Complete" title="Data Entry Complete">
		           </c:when>
		           <c:when test="${dec.stage.name=='administrative editing'}">
		             <img src="images/icon_AdminEdit.gif" alt="Administrative Editing" title="Administrative Editing">
		           </c:when>
		           <c:when test="${dec.stage.name=='locked'}">
		             <img src="images/icon_Locked.gif" alt="Locked" title="Locked">
		           </c:when>
		           <c:otherwise>
		             <img src="images/icon_Invalid.gif" alt="Invalid" title="Invalid">
		           </c:otherwise>
		          </c:choose>&nbsp;
				</td>
				<td class="table_cell"><c:out value="${dec.eventCRF.owner.name}" />&nbsp;</td>
				<td class="table_cell"><c:out value="${dec.eventCRF.updater.name}" />&nbsp;</td>
				<td class="table_cell">
					<c:set var="actionQuery" value="" />
					<c:if test="${dec.continueInitialDataEntryPermitted}">
						<c:set var="actionQuery" value="InitialDataEntry?eventCRFId=${dec.eventCRF.id}" />
					</c:if>
					<c:if test="${dec.startDoubleDataEntryPermitted}">
						<c:set var="actionQuery" value="DoubleDataEntry?eventCRFId=${dec.eventCRF.id}" />
					</c:if>
					<c:if test="${dec.continueDoubleDataEntryPermitted}">
						<c:set var="actionQuery" value="DoubleDataEntry?eventCRFId=${dec.eventCRF.id}" />
					</c:if>

					<c:if test="${dec.performAdministrativeEditingPermitted}">
					<c:set var="actionQuery" value="AdministrativeEditing?eventCRFId=${dec.eventCRF.id}" />
					</c:if>

<%--
					<c:if test="${dec.locked}">
						locked
					</c:if>
					    
--%>
					
					<c:choose>
						<c:when test='${actionQuery == "" && dec.stage.name =="invalid" }'>
							<a href="ViewSectionDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>&tabId=1"
								onMouseDown="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
								onMouseUp="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
								><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_View.gif" border="0" alt="View data" title="View Data" align="left" hspace="2"></a>&nbsp;
							
							<a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>')"
								onMouseDown="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
								onMouseUp="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
								><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_Print.gif" border="0" alt="Print" title="Print"  hspace="2"></a>&nbsp;	
									
									
							<a href="RestoreEventCRF?action=confirm&id=<c:out value="${dec.eventCRF.id}"/>&studySubId=<c:out value="${studySubject.id}"/>"
								onMouseDown="javascript:setImage('bt_Restore<c:out value="${rowCount}"/>','images/bt_Restore.gif');"
								onMouseUp="javascript:setImage('bt_Restore<c:out value="${rowCount}"/>','images/bt_Restore.gif');"
								><img name="bt_Restore<c:out value="${rowCount}"/>" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore"  hspace="2"></a>&nbsp;	
						</c:when>
				
						<c:when test='${actionQuery == ""}'>
							<a href="ViewSectionDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>&tabId=1"
								onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
								onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
								><img name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="2"></a>
						</c:when>
						<c:otherwise>
							<a href="<c:out value="${actionQuery}"/>"
								onMouseDown="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData_d.gif');"
								onMouseUp="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData.gif');"
								><img name="bt_EnterData<c:out value="${rowCount}"/>" src="images/bt_EnterData.gif" border="0" alt="Enter Data" title="Enter Data" align="left" hspace="2"></a>&nbsp;
							
							<a href="ViewSectionDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>&tabId=1"
								onMouseDown="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
								onMouseUp="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
								><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_View.gif" border="0" alt="View data" title="View Data"  hspace="2"></a>&nbsp;
								
							<a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>')"
								onMouseDown="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
								onMouseUp="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
								><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_Print.gif" border="0" alt="Print" title="Print"  hspace="2"></a>&nbsp;	
							
							<c:if test="${userRole.role.name=='director' || userBean.sysAdmin}">		
							<a href="RemoveEventCRF?action=confirm&id=<c:out value="${dec.eventCRF.id}"/>&studySubId=<c:out value="${studySubject.id}"/>"
								onMouseDown="javascript:setImage('bt_Remove<c:out value="${rowCount}"/>','images/bt_Remove.gif');"
								onMouseUp="javascript:setImage('bt_Remove<c:out value="${rowCount}"/>','images/bt_Remove.gif');"
								><img name="bt_Remove<c:out value="${rowCount}"/>" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove"  hspace="2"></a>&nbsp;		
							</c:if>
							
							<c:if test="${userBean.sysAdmin}">		
							<a href="DeleteEventCRF?action=confirm&ssId=<c:out value="${studySubject.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>"
								onMouseDown="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
								onMouseUp="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
								><img name="bt_Remove<c:out value="${rowCount}"/>" src="images/bt_Delete.gif" border="0" alt="Delete" title="Delete"  hspace="2"></a>&nbsp;					
							</c:if>		
					</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:set var="rowCount" value="${rowCount + 1}" />
		</c:forEach>
	</c:otherwise>
</c:choose>
</table>

<!-- End Table Contents -->

</div>
</div></div></div></div></div></div></div></div>
</div>

<form method="POST" action="ViewStudySubject">
<input type="hidden" name="id" value="<c:out value="${studySubject.id}"/>" />
<input type="submit" name="Submit" value="View this Subject's Record" class="button_xlong">
</form>


<c:import url="instructionsEnterData.jsp">
	<c:param name="currStep" value="eventOverview" />
</c:import>

<jsp:include page="../include/footer.jsp"/>