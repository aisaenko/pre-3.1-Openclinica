<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="table" class="org.akaza.openclinica.core.EntityBeanTable" />
  <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
    <c:import url="../include/managestudy-header.jsp"/>
   </c:when>
   <c:otherwise>
    <c:import url="../include/submit-header.jsp"/>
   </c:otherwise> 
  </c:choose> 
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

		  By default, a list of events in the current month in the current study/site are shown.
          <br><br>
          Subjects who were scheduled/proposed in the past with no status change are highlighted in yellow.
			
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

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "startDate"}'>
		<c:set var="startDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "endDate"}'>
		<c:set var="endDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "definitionId"}'>
		<c:set var="definitionId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "statusId"}'>
		<c:set var="statusId" value="${presetValue.value}" />
	</c:if>
	
</c:forEach>
<!-- the object inside the array is StudySubjectBean-->

<h1><c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
     <div class="title_manage">
   </c:when>
   <c:otherwise>
    <div class="title_submit">
   </c:otherwise> 
  </c:choose>
 View All Events in <c:out value="${study.name}"/> <a href="javascript:openDocWindow('help/4_5_viewEvents_Help.html')">
 <c:choose>
   <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
      <img src="images/bt_Help_Manage.gif" border="0" alt="Help" title="Help">
   </c:when>
   <c:otherwise>
    <img src="images/bt_Help_Submit.gif" border="0" alt="Help" title="Help">
   </c:otherwise> 
  </c:choose> 
</a>
   <a href="javascript:openDocWindow('ViewStudyEvents?print=yes&<c:out value="${queryUrl}"/>')"
	onMouseDown="javascript:setImage('bt_Print0','images/bt_Print_d.gif');"
	onMouseUp="javascript:setImage('bt_Print0','images/bt_Print.gif');">
	<img name="bt_Print0" src="images/bt_Print.gif" border="0" alt="Print"></a>
  </div> 
  </h1>

  
 <SCRIPT LANGUAGE="JavaScript" type="text/javascript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1" type="text/javascript">  
  var cal1 = new CalendarPopup("testdiv1");
  </SCRIPT>
<div style="width: 620px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center">
<form method="POST" action="ViewStudyEvents" name="control">
<jsp:include page="../include/showSubmitted.jsp" />
<table border="0" cellpadding="0" cellspacing="0">
<tr valign="top"><b>Filter events by:</b></tr>
<tr valign="top">
  <td>Study Event Definition:</td>
  <td colspan="2"><div class="formfieldL_BG">
   <c:set var="definitionId1" value="${definitionId}"/>   
      <select name="definitionId" class="formfieldL">
       <option value="0">--All--</option>
       <c:forEach var="definition" items="${definitions}">    
       <c:choose>
        <c:when test="${definitionId1 == definition.id}">   
         <option value="<c:out value="${definition.id}"/>" selected><c:out value="${definition.name}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${definition.id}"/>"><c:out value="${definition.name}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>
    </select> </div>
   </td>
   <td>&nbsp;&nbsp;Status:</td>
   <td colspan="2">
   <div class="formfieldM_BG">
    <c:set var="status1" value="${statusId}"/>
     <select name="statusId" class="formfieldM">
      <option value="0">--All--</option>
      <c:forEach var="status" items="${statuses}">    
       <c:choose>
        <c:when test="${status1 == status.id}">   
         <option value="<c:out value="${status.id}"/>" selected><c:out value="${status.name}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${status.id}"/>"><c:out value="${status.name}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>
     </select>
   </div>
   </td>
  </tr> 
<tr valign="top">
 <td>Date Started: </td>
 <td><div class="formfieldS_BG">
   <input type="text" name="startDate" value="<c:out value="${startDate}"/>" class="formfieldS"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startDate"/></jsp:include>
  </td>
  <td><A HREF="#" onClick="cal1.select(document.control.startDate,'anchor1','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.control.startDate,'anchor1','MM/dd/yyyy'); return false;" NAME="anchor1" ID="anchor1"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a> (MM/DD/YYYY)</td>
  <td>&nbsp;&nbsp;Date Ended:</td> 
  <td><div class="formfieldS_BG">
    <input type="text" name="endDate" value="<c:out value="${endDate}"/>" class="formfieldS"></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endDate"/></jsp:include>
  </td>  
   <td><A HREF="#" onClick="cal1.select(document.control.endDate,'anchor2','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.control.endDate,'anchor2','MM/dd/yyyy'); return false;" NAME="anchor2" ID="anchor2"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a> (MM/DD/YYYY)</td>
  </tr>  
  <tr> 
  <td colspan="6" align="right"><input type="submit" name="submit" value="Apply Filter" class="button_medium"></td>
 </tr>
</table>
</form>
</div>
</div></div></div></div></div></div></div></div>
</div>
<br><br>
<c:if test="${empty allEvents}">
 <p>There are no study events within the requested parameters. Please refine your parameters and try again.
</c:if>
<c:forEach var="eventView" items="${allEvents}">    
      <c:choose>
        <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
         <span class="table_title_manage">
        </c:when>
        <c:otherwise>
          <span class="table_title_submit">
        </c:otherwise> 
      </c:choose>   
   
  Event Name: <c:out value="${eventView.definition.name}"/></span><br> 
	<b>Event Type</b>:<c:out value="${eventView.definition.type}"/>, 
	<c:choose>
     <c:when test="${eventView.definition.repeating}">
       repeating
     </c:when>
     <c:otherwise>
      non-repeating
     </c:otherwise>
    </c:choose>, 
	<b>Category</b>: 
	<c:choose>
	 <c:when test="${eventView.definition.category == null || eventView.definition.category ==''}">
	  N/A
	 </c:when>
	 <c:otherwise>
	   <c:out value="${eventView.definition.category}"/>
	 </c:otherwise>
	</c:choose>	
	<br>
	<b>Subjects proposed/scheduled</b>: <c:out value="${eventView.subjectScheduled}"/> (start date of first event:
	<c:choose>
      <c:when test="${eventView.firstScheduledStartDate== null}">
       N/A
      </c:when>
     <c:otherwise>
       <fmt:formatDate value="${eventView.firstScheduledStartDate}" pattern="MM/dd/yyyy"/>
     </c:otherwise>
     </c:choose>), <b>completed</b>: <c:out value="${eventView.subjectCompleted}"/> (completion date of last event:
   <c:choose>
   <c:when test="${eventView.lastCompletionDate== null}">
    N/A
   </c:when>
   <c:otherwise>
    <fmt:formatDate value="${eventView.lastCompletionDate}" pattern="MM/dd/yyyy"/>
   </c:otherwise>
 </c:choose>), <b>discontinued</b>: <c:out value="${eventView.subjectDiscontinued}"/><br>
	<c:set var="table" value="${eventView.studyEventTable}" scope="request" />
	<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showEventByDefinitionRow.jsp" /></c:import>
<br><br>
</c:forEach>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<br><br>

<!-- EXPANDING WORKFLOW BOX -->

<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
	<tr>
		<td id="sidebar_Workflow_closed" style="display: none">
		<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/tab_Workflow_closed.gif" border="0"></a>
	</td>
	<td id="sidebar_Workflow_open" style="display: all">
	<table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
		<tr>
			<td class="workflowBox_T" valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="workflow_tab">
					<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

					<b>Workflow</b>

					</td>
				</tr>
			</table>
			</td>
			<td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif" alt=""></td>
		</tr>
		<tr>
			<td colspan="2" class="workflowbox_B">
			<div class="box_R"><div class="box_B"><div class="box_BR">
				<div class="workflowBox_center">


		<!-- Workflow items -->

				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
							<div class="textbox_center" align="center">
	
							<c:choose>
                             <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
                               <span class="title_manage">
                               <a href="ManageStudy">Manage Study</a>
                             </c:when>
                             <c:otherwise>
                               <span class="title_submit">
                               <a href="ListStudySubjectsSubmit">Submit Data</a>
                             </c:otherwise> 
                             </c:choose> 		
				
					
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<c:choose>
                             <c:when test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
                               <span class="title_manage">
                             </c:when>
                             <c:otherwise>
                               <span class="title_submit">
                             </c:otherwise> 
                             </c:choose> 
				
					
							View Events
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
					</tr>
				</table>


		<!-- end Workflow items -->

				</div>
			</div></div></div>
			</td>
		</tr>
	</table>			
	</td>
   </tr>
</table>

<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>
