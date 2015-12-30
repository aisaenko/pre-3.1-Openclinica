<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>  
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>


<jsp:include page="../include/submit-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/submitSideInfo.jsp"/>

<jsp:useBean scope="request" id="studyEvent" class="org.akaza.openclinica.bean.managestudy.StudyEventBean" />
<jsp:useBean scope="request" id="studySubject" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean" />
<jsp:useBean scope="request" id="uncompletedEventDefinitionCRFs" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="displayEventCRFs" class="java.util.ArrayList" />

<h1><span class="title_submit"><fmt:message key="enter_or_validate_data" bundle="${resword}"/><c:out value="${studyEvent.studyEventDefinition.name}" /> <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step2a')"><img src="images/bt_Help_Submit.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a> </span></h1>


<br>
<b><fmt:message key="study_event" bundle="${resword}"/></b>: <c:out value="${studyEvent.studyEventDefinition.name}"/> &nbsp;
<b><fmt:message key="location" bundle="${resword}"/></b>: <c:out value="${studyEvent.location}"/><br>
<b><fmt:message key="start_date" bundle="${resword}"/></b>: <fmt:formatDate value="${studyEvent.dateStarted}" dateStyle="short"/> &nbsp;
<b><fmt:message key="end_date_time" bundle="${resword}"/></b>: <fmt:formatDate value="${studyEvent.dateEnded}" dateStyle="short"/><br>
<b><fmt:message key="last_updated_by" bundle="${resword}"/></b>: <c:out value="${studyEvent.updater.name}"/> (<fmt:formatDate value="${studyEvent.updatedDate}" dateStyle="short"/>)<br>
<b><fmt:message key="subject_event_status" bundle="${resword}"/></b>: <c:out value="${studyEvent.subjectEventStatus.name}"/>
<br>
<a href="UpdateStudyEvent?event_id=<c:out value="${studyEvent.id}"/>&ss_id=<c:out value="${studySubject.id}"/>"><img src="images/bt_Edit.gif" border="0" align="left"></a>&nbsp;<a href="UpdateStudyEvent?event_id=<c:out value="${studyEvent.id}"/>&ss_id=<c:out value="${studySubject.id}"/>"><fmt:message key="edit_study_event" bundle="${resword}"/></a>

<p><div class="table_title_submit"><fmt:message key="CRFs_in_this_study_event" bundle="${resword}"/>:</div>

<div style="width: 600px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<c:choose>
	<c:when test="${empty uncompletedEventDefinitionCRFs && empty displayEventCRFs}">
		<tr>
			<td class="table_cell_left"><fmt:message key="there_are_no_CRF" bundle="${resword}"/></td>
		</tr>
	</c:when>
	<c:otherwise>
		<tr>
			<td class="table_header_row_left"><fmt:message key="CRF_name" bundle="${resword}"/></td>
			<td class="table_header_row"><fmt:message key="version" bundle="${resword}"/></td>
			<td class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>
			<td class="table_header_row"><fmt:message key="initial_data_entry" bundle="${resword}"/></td>
			<td class="table_header_row"><fmt:message key="validation" bundle="${resword}"/></td>			
			<td class="table_header_row"><fmt:message key="actions" bundle="${resword}"/></td>
		</tr>
		<c:set var="rowCount" value="${0}" />
		
		<c:forEach var="dedc" items="${uncompletedEventDefinitionCRFs}">
			<c:choose>
			<c:when test="${dedc.eventCRF.stage.locked}">
				<%-- nothing for right now --%>
			</c:when>
			<c:otherwise>
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
					<td class="table_cell" bgcolor="#F5F5F5" align="center"><img src="images/icon_NotStarted.gif" alt="<fmt:message key="not_started" bundle="${resword}"/>" title="<fmt:message key="not_started" bundle="${resword}"/>"></td>
					<td class="table_cell">&nbsp;</td>
					<td class="table_cell">&nbsp;</td>
					
					<td>
					<table border="0" cellpadding="0" cellspacing="0">
	                  <tr valign="top">
	                   <td class="table_cell">
						<a href="#" onclick="javascript:document.startForm<c:out value="${dedc.edc.crf.id}"/>.submit();"
							onMouseDown="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData_d.gif');"
							onMouseUp="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData.gif');"
						><img name="bt_EnterData<c:out value="${rowCount}"/>" src="images/bt_EnterData.gif" border="0" alt="Enter Data" title="Enter Data" align="left" hspace="6"></a>&nbsp;
					  	           
		            
				      </form>	
				     </td>
				    <td class="table_cell">
		             <a href="ViewSectionDataEntry?eventDefinitionCRFId=<c:out value="${dedc.edc.id}"/>&crfVersionId=<c:out value="${dedc.edc.defaultVersionId}"/>&tabId=1"
			          onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			          onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		              name="bt_View1" src="images/bt_View.gif" border="0" alt="View Default" title="View Default" align="left" hspace="6"></a>
		            </td>
		            
		            <td class="table_cell">
		             <a href="javascript:openDocWindow('PrintCRF?id=<c:out value="${dedc.edc.defaultVersionId}"/>')"
			          onMouseDown="javascript:setImage('bt_Print1','images/bt_Print_d.gif');"
			          onMouseUp="javascript:setImage('bt_Print1','images/bt_Print.gif');"><img 
		              name="bt_Print1" src="images/bt_Print.gif" border="0" alt="Print Default" title="Print Default" align="left" hspace="6"></a>
		            </td>
		            
				</tr>
			    </table>
			    </td>
			   </tr>	
				
				<c:set var="rowCount" value="${rowCount + 1}" />
				</c:otherwise>
			</c:choose>
		</c:forEach>
		<%-- end of for each for uncompleted event crfs --%>
		<c:forEach var="dec" items="${displayEventCRFs}">
			<tr>
				<td class="table_cell"><c:out value="${dec.eventCRF.crf.name}" />&nbsp;</td>
				<td class="table_cell"><c:out value="${dec.eventCRF.crfVersion.name}" />&nbsp;</td>				
				<td class="table_cell" bgcolor="#F5F5F5" align="center">
				  
				  <c:choose>		 
		           <c:when test="${dec.stage.initialDE}">
		             <img src="images/icon_InitialDE.gif" alt="<fmt:message key="initial_data_entry" bundle="${resword}"/>" title="<fmt:message key="initial_data_entry" bundle="${resword}"/>">
		           </c:when>
		           <c:when test="${dec.stage.initialDE_Complete}">
		             <img src="images/icon_InitialDEcomplete.gif" alt="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>" title="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>">
		           </c:when>
		           <c:when test="${dec.stage.doubleDE}">
		             <img src="images/icon_DDE.gif" alt="<fmt:message key="double_data_entry" bundle="${resword}"/>" title="<fmt:message key="double_data_entry" bundle="${resword}"/>">
		           </c:when>
		           <c:when test="${dec.stage.doubleDE_Complete}">
		             <img src="images/icon_DEcomplete.gif" alt="<fmt:message key="data_entry_complete" bundle="${resword}"/>" title="<fmt:message key="data_entry_complete" bundle="${resword}"/>">
		           </c:when>
		           
		           <c:when test="${dec.stage.admin_Editing}">
		             <img src="images/icon_AdminEdit.gif" alt="<fmt:message key="administrative_editing" bundle="${resword}"/>" title="<fmt:message key="administrative_editing" bundle="${resword}"/>">
		           </c:when>
		           
		           <c:when test="${dec.stage.locked}">
		             <img src="images/icon_Locked.gif" alt="<fmt:message key="locked" bundle="${resword}"/>" title="<fmt:message key="locked" bundle="${resword}"/>">
		           </c:when>
		           
		           <c:otherwise>
		             <img src="images/icon_Invalid.gif" alt="<fmt:message key="invalid" bundle="${resword}"/>" title="<fmt:message key="invalid" bundle="${resword}"/>">
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
							<a href="ViewSectionDataEntry?eventDefinitionCRFId=<c:out value="${dedc.edc.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>&tabId=1"
								onMouseDown="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
								onMouseUp="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
								><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_View.gif" border="0" alt="<fmt:message key="view_data" bundle="${resword}"/>" title="<fmt:message key="view_data" bundle="${resword}"/>" align="left" hspace="2"></a>&nbsp;
							
							<a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>')"
								onMouseDown="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
								onMouseUp="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
								><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_Print.gif" border="0" alt="<fmt:message key="print" bundle="${resword}"/>" title="<fmt:message key="print" bundle="${resword}"/>"  hspace="2"></a>&nbsp;	
									
									
							<a href="RestoreEventCRF?action=confirm&id=<c:out value="${dec.eventCRF.id}"/>&studySubId=<c:out value="${studySubject.id}"/>"
								onMouseDown="javascript:setImage('bt_Restore<c:out value="${rowCount}"/>','images/bt_Restore.gif');"
								onMouseUp="javascript:setImage('bt_Restore<c:out value="${rowCount}"/>','images/bt_Restore.gif');"
								><img name="bt_Restore<c:out value="${rowCount}"/>" src="images/bt_Restore.gif" border="0" alt="<fmt:message key="restore" bundle="${resword}"/>" title="<fmt:message key="restore" bundle="${resword}"/>"  hspace="2"></a>&nbsp;	
						</c:when>
				
						<c:when test='${actionQuery == ""}'>
							<a href="ViewSectionDataEntry?eventDefinitionCRFId=<c:out value="${dedc.edc.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>&tabId=1"
								onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
								onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
								><img name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="2"></a>
						</c:when>
						<c:otherwise>
							<a href="<c:out value="${actionQuery}"/>"
								onMouseDown="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData_d.gif');"
								onMouseUp="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData.gif');"
								><img name="bt_EnterData<c:out value="${rowCount}"/>" src="images/bt_EnterData.gif" border="0" alt="<fmt:message key="enter_data" bundle="${resword}"/>" title="<fmt:message key="enter_data" bundle="${resword}"/>" align="left" hspace="2"></a>&nbsp;
							
							<a href="ViewSectionDataEntry?eventDefinitionCRFId=<c:out value="${dedc.edc.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>&tabId=1"
								onMouseDown="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
								onMouseUp="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
								><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_View.gif" border="0" alt="<fmt:message key="view_data" bundle="${resword}"/>" title="<fmt:message key="view_data" bundle="${resword}"/>"  hspace="2"></a>&nbsp;
								
							<a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>')"
								onMouseDown="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
								onMouseUp="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
								><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_Print.gif" border="0" alt="<fmt:message key="print" bundle="${resword}"/>" title="<fmt:message key="print" bundle="${resword}"/>"  hspace="2"></a>&nbsp;	
							
							<c:if test="${userRole.director || userBean.sysAdmin}">		
							<a href="RemoveEventCRF?action=confirm&id=<c:out value="${dec.eventCRF.id}"/>&studySubId=<c:out value="${studySubject.id}"/>"
								onMouseDown="javascript:setImage('bt_Remove<c:out value="${rowCount}"/>','images/bt_Remove.gif');"
								onMouseUp="javascript:setImage('bt_Remove<c:out value="${rowCount}"/>','images/bt_Remove.gif');"
								><img name="bt_Remove<c:out value="${rowCount}"/>" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>"  hspace="2"></a>&nbsp;		
							</c:if>
							
							<c:if test="${userBean.sysAdmin}">		
							<a href="DeleteEventCRF?action=confirm&ssId=<c:out value="${studySubject.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>"
								onMouseDown="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
								onMouseUp="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
								><img name="bt_Remove<c:out value="${rowCount}"/>" src="images/bt_Delete.gif" border="0" alt="<fmt:message key="delete" bundle="${resword}"/>" title="<fmt:message key="delete" bundle="${resword}"/>"  hspace="2"></a>&nbsp;					
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
<input type="submit" name="Submit" value="<fmt:message key="view_this_subject_record" bundle="${resword}"/>" class="button_xlong">
</form>


<c:import url="instructionsEnterData.jsp">
	<c:param name="currStep" value="eventOverview" />
</c:import>

<jsp:include page="../include/footer.jsp"/>
