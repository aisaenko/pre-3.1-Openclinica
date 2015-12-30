<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:useBean scope="request" id="table" class="org.akaza.openclinica.web.bean.EntityBeanTable" />

<c:import url="../include/managestudy-header.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		  <fmt:message key="events_month_shown_default" bundle="${restext}"/>
          <br><br>
          <fmt:message key="subject_scheduled_no_DE_yellow" bundle="${restext}"/>

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

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

<h1><span class="title_manage">
 <fmt:message key="view_all_events_in" bundle="${resword}"/> <c:out value="${study.name}"/>
 <!--<c:choose>
   <c:when test="${userRole.manageStudy}"> -->
      <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/openclinica-user-guide/submit-data-module-overview/view-events')">
      <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
      </a>
  <!--  </c:when>
   <c:otherwise>
    <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/openclinica-user-guide/submit-data-module-overview/view-events')">
    <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
    </a>
   </c:otherwise>
  </c:choose>
   -->
   <a href="javascript:openDocWindow('ViewStudyEvents?print=yes&<c:out value="${queryUrl}"/>')"
	onMouseDown="javascript:setImage('bt_Print0','images/bt_Print_d.gif');"
	onMouseUp="javascript:setImage('bt_Print0','images/bt_Print.gif');">
	<img name="bt_Print0" src="images/bt_Print.gif" border="0" alt="<fmt:message key="print" bundle="${resword}"/>"></a>
  </span>
  </h1>



<form method="POST" action="ViewStudyEvents" name="control">
<jsp:include page="../include/showSubmitted.jsp" />
<table border="0" cellpadding="0" cellspacing="0"   class="shaded_table  table_first_column_w30">
<tr valign="top"><b><fmt:message key="filter_events_by" bundle="${resword}"/>:</b></tr>
<tr valign="top">
  <td><fmt:message key="study_event_definition" bundle="${resword}"/>:</td>
  <td colspan="2"><div class="formfieldL_BG">
   <c:set var="definitionId1" value="${definitionId}"/>
      <select name="definitionId" class="formfieldL">
       <option value="0">--<fmt:message key="all" bundle="${resword}"/>--</option>
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
   <td>&nbsp;&nbsp;<fmt:message key="status" bundle="${resword}"/>:</td>
   <td colspan="2">
   
    <c:set var="status1" value="${statusId}"/>
     <select name="statusId" class="formfieldM">
      <option value="0">--<fmt:message key="all" bundle="${resword}"/>--</option>
      <c:forEach var="status" items="${statuses}">
       <c:choose>
        <c:when test="${status1 == status.id}">
             <option value="<c:out value="${status.id}"/>" selected><c:out value="${status.name}"/>
        </c:when>
        <c:otherwise>
             <c:if test="${status.id != '2'}">
                <option value="<c:out value="${status.id}"/>"><c:out value="${status.name}"/>
             </c:if>
        </c:otherwise>
       </c:choose>
    </c:forEach>
     </select>
 
   </td>
  </tr>
<tr valign="top">
 <td><fmt:message key="date_started" bundle="${resword}"/>: </td>
 <td><div class="formfieldS_BG">
   <input type="text" name="startDate" value="<c:out value="${startDate}"/>" class="formfieldS" id="startDateField"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startDate"/></jsp:include>
  </td>
  <td nowrap><A HREF="#">
      <img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" id="startDateTrigger"/>
      <script type="text/javascript">
      Calendar.setup({inputField  : "startDateField", ifFormat    : "<fmt:message key="date_format_calender" bundle="${resformat}"/>", button      : "startDateTrigger" });
      </script>
  </a>
  (<fmt:message key="date_format" bundle="${resformat}"/>)
  </td>
  <td>&nbsp;&nbsp;<fmt:message key="date_ended" bundle="${resword}"/>:</td>
  <td><div class="formfieldS_BG">
    <input type="text" name="endDate" value="<c:out value="${endDate}"/>" class="formfieldS" id="endDateField"></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endDate"/></jsp:include>
  </td>
   <td nowrap><A HREF="#" >
       <img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" id="endDateTrigger"/>
       <script type="text/javascript">
       Calendar.setup({inputField  : "endDateField", ifFormat    : "<fmt:message key="date_format_calender" bundle="${resformat}"/>", button      : "endDateTrigger" });
       </script>

   </a> (<fmt:message key="date_format" bundle="${resformat}"/>)</td>
  </tr>
  <tr>
  <td colspan="6" align="right"><input type="submit" name="submit" value="<fmt:message key="apply_filter" bundle="${resword}"/>" class="button_medium"></td>
 </tr>
</table>
</form>

<br><br>
<c:if test="${empty allEvents}">
 <p><fmt:message key="no_events_within_parameters" bundle="${restext}"/>
</c:if>
<c:forEach var="eventView" items="${allEvents}">
      
          <span class="table_title">
       

  <fmt:message key="event_name" bundle="${resword}"/>: <c:out value="${eventView.definition.name}"/></span><br>
	<b><fmt:message key="event_type" bundle="${resword}"/></b>:<fmt:message key="${eventView.definition.type}" bundle="${resword}"/>,
	<c:choose>
     <c:when test="${eventView.definition.repeating}">
       <fmt:message key="repeating" bundle="${resword}"/>
     </c:when>
     <c:otherwise>
      <fmt:message key="non_repeating" bundle="${resword}"/>
     </c:otherwise>
    </c:choose>,
	<b><fmt:message key="category" bundle="${resword}"/></b>:
	<c:choose>
	 <c:when test="${eventView.definition.category == null || eventView.definition.category ==''}">
	  <fmt:message key="N/A" bundle="${resword}"/>
	 </c:when>
	 <c:otherwise>
	   <c:out value="${eventView.definition.category}"/>
	 </c:otherwise>
	</c:choose>
	<br>
	<b><fmt:message key="subjects_who_scheduled" bundle="${resword}"/></b>: <c:out value="${eventView.subjectScheduled}"/> (<fmt:message key="start_date_of_first_event" bundle="${resword}"/>:
	<c:choose>
      <c:when test="${eventView.firstScheduledStartDate== null}">
       <fmt:message key="N/A" bundle="${resword}"/>
      </c:when>
     <c:otherwise>
       <fmt:formatDate value="${eventView.firstScheduledStartDate}" pattern="${dteFormat}"/>
     </c:otherwise>
     </c:choose>), <b><fmt:message key="completed" bundle="${resword}"/></b>: <c:out value="${eventView.subjectCompleted}"/> (<fmt:message key="completion_date_of_last_event" bundle="${resword}"/>:
   <c:choose>
   <c:when test="${eventView.lastCompletionDate== null}">
    <fmt:message key="N/A" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <fmt:formatDate value="${eventView.lastCompletionDate}" pattern="${dteFormat}"/>
   </c:otherwise>
 </c:choose>), <b><fmt:message key="discontinued" bundle="${resword}"/></b>: <c:out value="${eventView.subjectDiscontinued}"/><br>
	<c:set var="table" value="${eventView.studyEventTable}" scope="request" />
	<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showEventByDefinitionRow.jsp" /></c:import>
<br>
</c:forEach>

<!--<input type="button" onclick="confirmExit('MainMenu');"  name="exit" value="<fmt:message key="exit" bundle="${resword}"/>   " class="button_medium"/>-->

<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<br><br>

<!-- EXPANDING WORKFLOW BOX -->


<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>
