<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:set var="eblRowCount" value="${param.rowCount}" />
<c:set var="count" value="${param.colCount}" />
<c:set var="eventId" value="${param.eventId}" />
<c:set var="eventDefId" value="${param.eventDefId}" />
<c:set var="subjectId" value="${param.subjectId}" />
<c:set var="subjectName" value="${param.subjectName}" />
<c:set var="eventName" value="${param.eventName}" />
<c:set var="eventStatus" value="${param.eventStatus}" />

<div id="Lock_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>" style="position: absolute; visibility: hidden; z-index: 3; width: 50px; height: 30px; top: 0px;">
		
		<a href="javascript:leftnavExpand('Menu_on_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>'); javascript:leftnavExpand('Menu_off_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>');" onMouseOver="layersShowOrHide('visible','Event_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>');
				javascript:setImage('ExpandIcon_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>','images/icon_collapse.gif');" 
			onClick="layersShowOrHide('hidden','Lock_all'); 
				layersShowOrHide('hidden','Event_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>');
				layersShowOrHide('hidden','Lock_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>');
				javascript:setImage('ExpandIcon_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>','images/icon_blank.gif');"><img src="images/spacer.gif" width="50" height="30" border="0"></a>
		</div>
		
		<div id="Event_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>" style="position: absolute; visibility: hidden; z-index: 3; width: 180px; top: 0px;">
		
			<!-- These DIVs define shaded box borders -->
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
		
			<div class="tablebox_center">
			<div class="ViewSubjectsPopup" style="color:#5b5b5b">
		
					  <table border="0" cellpadding="0" cellspacing="0" width="100%">		
						<tr valign="top">
						<td class="table_header_row_left">
						Subject: <c:out value="${subjectName}"/>
		
						<br>
		
						Event: <c:out value="${eventName}"/>

		
						<br>
		
						Status: <c:out value="${eventStatus}"/>
		
						</td>
						<td class="table_header_row_left" align="right">
						<a href="javascript:leftnavExpand('Menu_on_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>'); javascript:leftnavExpand('Menu_off_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>');" 
							onClick="layersShowOrHide('hidden','Lock_all'); 
								layersShowOrHide('hidden','Event_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>');
								layersShowOrHide('hidden','Lock_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>');
								javascript:setImage('ExpandIcon_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>','images/icon_blank.gif');">X</a></td>
						</tr>
						<tr id="Menu_off_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>" style="display: all">
							<td class="table_cell_left" colspan="2"><i>Click for more options</i></td>
						</tr>
						<tr id="Menu_on_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>" style="display: none">
							<td colspan="2">
							<table border="0" cellpadding="0" cellspacing="0" width="100%">		
							   <c:choose>
							    <c:when test="${eventStatus =='not scheduled'}">
							      <tr valign="top"><td class="table_cell_left">
							      <a href="CreateNewStudyEvent?studySubjectId=<c:out value="${subjectId}"/>&studyEventDefinition=<c:out value="${eventDefId}"/>"><img src="images/bt_Schedule.gif" border="0" align="left"></a>&nbsp;&nbsp; <a href="CreateNewStudyEvent?studySubjectId=<c:out value="${subjectId}"/>&studyEventDefinition=<c:out value="${eventDefId}"/>">Schedule</a></td></tr>
							    </c:when>
							    <c:when test="${eventStatus =='completed'}">
							      <tr valign="top"><td class="table_cell_left">
							      <a href="EnterDataForStudyEvent?eventId=<c:out value="${eventId}"/>"><img src="images/bt_View.gif" border="0" align="left"></a>&nbsp;&nbsp; <a href="EnterDataForStudyEvent?eventId=<c:out value="${eventId}"/>">View</a></td></tr>
							    
							      <c:if test="${userRole.role.name=='director' || userBean.sysAdmin}">
								  <tr valign="top"><td class="table_cell_left"><a href="UpdateStudyEvent?event_id=<c:out value="${eventId}"/>&ss_id=<c:out value="${subjectId}"/>"><img src="images/bt_Edit.gif" border="0" align="left"></a>
								  &nbsp;&nbsp; <a href="UpdateStudyEvent?event_id=<c:out value="${eventId}"/>&ss_id=<c:out value="${subjectId}"/>">Edit</a></td></tr>
								  
								  <!--
								  <tr valign="top"><td class="table_cell_left"><a href=""><img src="images/bt_Lock.gif" border="0" align="left"></a>&nbsp;&nbsp; <a href="">Lock</a></td></tr>
								  -->
								  <tr valign="top"><td class="table_cell_left"><a href="RemoveStudyEvent?action=confirm&id=<c:out value="${eventId}"/>&studySubId=<c:out value="${subjectId}"/>"><img src="images/bt_Remove.gif" border="0" align="left"></a>&nbsp;&nbsp; 
								  <a href="RemoveStudyEvent?action=confirm&id=<c:out value="${eventId}"/>&studySubId=<c:out value="${subjectId}"/>">Remove</a></td></tr>
						 	     </c:if>
							    </c:when>
							    <c:when test="${eventStatus =='locked' }">
							      <c:if test="${userRole.role.name=='director' || userBean.sysAdmin}">
							        <tr valign="top"><td class="table_cell_left"><a href="RemoveStudyEvent?action=confirm&id=<c:out value="${eventId}"/>&studySubId=<c:out value="${subjectId}"/>">
							        <img src="images/bt_Remove.gif" border="0" align="left"></a>&nbsp;&nbsp; <a href="RemoveStudyEvent?action=confirm&id=<c:out value="${eventId}"/>&studySubId=<c:out value="${subjectId}"/>">Remove</a></td></tr>
						 	      </c:if>
						 	    </c:when>
							    <c:otherwise>		
							      <tr valign="top"><td class="table_cell_left">
							      <a href="EnterDataForStudyEvent?eventId=<c:out value="${eventId}"/>"><img src="images/bt_View.gif" border="0" align="left"></a>&nbsp;&nbsp; <a href="EnterDataForStudyEvent?eventId=<c:out value="${eventId}"/>">View/Enter Data</a></td></tr>
							    					      
							     <c:if test="${userRole.role.name=='director' || userBean.sysAdmin}">
								  <tr valign="top"><td class="table_cell_left"><a href="UpdateStudyEvent?event_id=<c:out value="${eventId}"/>&ss_id=<c:out value="${subjectId}"/>"><img src="images/bt_Edit.gif" border="0" align="left"></a>
								  &nbsp;&nbsp; <a href="UpdateStudyEvent?event_id=<c:out value="${eventId}"/>&ss_id=<c:out value="${subjectId}"/>">Edit</a></td></tr>
								  
								  <!--
								  <tr valign="top"><td class="table_cell_left"><a href=""><img src="images/bt_Lock.gif" border="0" align="left"></a>&nbsp;&nbsp; <a href="">Lock</a></td></tr>
								  -->
								  <tr valign="top"><td class="table_cell_left"><a href="RemoveStudyEvent?action=confirm&id=<c:out value="${eventId}"/>&studySubId=<c:out value="${subjectId}"/>"><img src="images/bt_Remove.gif" border="0" align="left"></a>&nbsp;&nbsp; 
								  <a href="RemoveStudyEvent?action=confirm&id=<c:out value="${eventId}"/>&studySubId=<c:out value="${subjectId}"/>">Remove</a></td></tr>
						 	     </c:if>
						 	    </c:otherwise>
						 	   </c:choose>
						 	</table>
							</td>
						</tr>
					  </table>
		
			</div>
			</div>
		
			</div></div></div></div></div></div></div></div>
		  </div>
         
         <a href="javascript:leftnavExpand('Menu_on_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>'); javascript:leftnavExpand('Menu_off_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>');" 
							onmouseover="moveObject('Event_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>', event);
							javascript:setImage('ExpandIcon_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>','images/icon_expand.gif');"
							onmouseout="layersShowOrHide('hidden','Event_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>');
							javascript:setImage('ExpandIcon_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>','images/icon_blank.gif');"
							onClick="layersShowOrHide('visible','Lock_all'); LockObject('Lock_<c:out value="${subjectName}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>', event);">