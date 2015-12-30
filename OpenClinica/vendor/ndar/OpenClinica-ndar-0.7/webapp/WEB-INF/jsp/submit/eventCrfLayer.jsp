<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:set var="eblRowCount" value="${param.rowCount}" />
<c:set var="count" value="${param.colCount}" />
<c:set var="eventId" value="${param.eventId}" />
<c:set var="eventCrfId" value="${param.crfId}" />
<c:set var="subjectId" value="${param.subjectId}" />
<c:set var="crfVersionId" value="${param.crfVersionId}" />
<c:set var="edcId" value="${param.edcId}" />
<c:set var="crfStatus" value="${param.crfStatus}" />
<c:set var="crfName" value="${param.crfName}" />
<c:set var="subjectName" value="${param.subjectName}" />


<div id="Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>" style="position: absolute; visibility: hidden; z-index: 3; width: 50px; height: 30px; top: 0px;">
		
 <c:choose>
  <c:when test="${crfStatus =='data entry complete' ||crfStatus =='administrative editing'}">
								
   <a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" onMouseOver="layersShowOrHide('visible','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Complete_collapse.gif');" 
	    onClick="layersShowOrHide('hidden','Lock_all'); 
		layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Complete.gif');"><img src="images/spacer.gif" width="144" height="30" border="0" alt="" /></a>

		
  </c:when>
  <c:when test="${crfStatus =='initial data entry complete'}">
								
   <a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" onMouseOver="layersShowOrHide('visible','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_InitialDEcomplete_collapse.gif');" 
	    onClick="layersShowOrHide('hidden','Lock_all'); 
		layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_InitialDEcomplete.gif');"><img src="images/spacer.gif" width="144" height="30" border="0" alt="" /></a>

		
  </c:when>
  <c:when test="${crfStatus =='double data entry'}">
								
   <a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" onMouseOver="layersShowOrHide('visible','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_DDE_collapse.gif');" 
	    onClick="layersShowOrHide('hidden','Lock_all'); 
		layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_DDE.gif');"><img src="images/spacer.gif" width="144" height="30" border="0" alt="" /></a>

		
  </c:when>
   <c:when test="${crfStatus =='locked' }">
							       
  </c:when>
  <c:when test="${crfStatus =='Not Started' }">	
   <a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" onMouseOver="layersShowOrHide('visible','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Scheduled.gif');" 
	    onClick="layersShowOrHide('hidden','Lock_all'); 
		layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Scheduled.gif');"><img src="images/spacer.gif" width="144" height="30" border="0" alt="" /></a>
  
  </c:when>
  <c:when test="${crfStatus =='invalid' }">	
   <a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" onMouseOver="layersShowOrHide('visible','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Invalid_collapse.gif');" 
	    onClick="layersShowOrHide('hidden','Lock_all'); 
		layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Invalid.gif');"><img src="images/spacer.gif" width="144" height="30" border="0" alt="" /></a>
  
  </c:when>
  <c:otherwise>
    <a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" onMouseOver="layersShowOrHide('visible','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Started.gif');" 
	    onClick="layersShowOrHide('hidden','Lock_all'); 
		layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
		javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Started.gif');"><img src="images/spacer.gif" width="144" height="30" border="0" alt="" /></a>
  
  </c:otherwise>
  </c:choose>
</div>
		
<div id="Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>" style="position: absolute; visibility: hidden; z-index: 3; width: 180px; top: 0px;">
		
			<!-- These DIVs define shaded box borders -->
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
		
			<div class="tablebox_center">
			<div class="ViewSubjectsPopup" style="color:#5b5b5b">
		
					  <table border="0" cellpadding="0" cellspacing="0" width="100%">		
						<tr valign="top">
						<td class="table_header_row_left">
						Subject: <c:out value="${subjectName}"/>
		
						<br>
		
						CRF: <c:out value="${crfName}"/>

		
						<br>
						<c:choose>
		                  <c:when test="${crfStatus== 'invalid'}">
						   Status: Removed
						  </c:when>
						  <c:otherwise>
						     Status: <c:out value="${crfStatus}"/>
						  </c:otherwise>
		                </c:choose>
						</td>
						 <c:choose>
                         <c:when test="${crfStatus =='data entry complete' ||
                         crfStatus =='administrative editing'}">
						    <td class="table_header_row_left" align="right"><a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_1_6_1');" 
										onClick="layersShowOrHide('hidden','Lock_all'); 
											layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Complete.gif');">X</a></td>

														
							
					     </c:when>
					     <c:when test="${crfStatus =='initial data entry complete'}">
						    <td class="table_header_row_left" align="right"><a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_1_6_1');" 
										onClick="layersShowOrHide('hidden','Lock_all'); 
											layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_InitialDEcomplete.gif');">X</a></td>

														
							
					     </c:when>
					     <c:when test="${crfStatus =='double data entry'}">
						    <td class="table_header_row_left" align="right"><a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_1_6_1');" 
										onClick="layersShowOrHide('hidden','Lock_all'); 
											layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_DDE.gif');">X</a></td>

														
							
					     </c:when>
		                 <c:when test="${crfStatus =='locked' }">
							       
		                 </c:when>
		                 <c:when test="${crfStatus =='Not Started' }">	
		                   <td class="table_header_row_left" align="right"><a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_1_6_1');" 
										onClick="layersShowOrHide('hidden','Lock_all'); 
											layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Scheduled.gif');">X</a></td>
		                   
		                 </c:when>
		                 <c:when test="${crfStatus =='invalid' }">	
		                   <td class="table_header_row_left" align="right"><a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_1_6_1');" 
										onClick="layersShowOrHide('hidden','Lock_all'); 
											layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Invalid.gif');">X</a></td>
		                   
		                 </c:when>
		                 <c:otherwise>
		                  <td class="table_header_row_left" align="right"><a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_1_6_1');" 
										onClick="layersShowOrHide('hidden','Lock_all'); 
											layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											layersShowOrHide('hidden','Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
											javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Started.gif');">X</a></td>
		                  
		   			
		                 </c:otherwise>
		  
		             </c:choose>			
						</tr>	
						<tr id="Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>" style="display: all">
										<td class="table_cell_left" colspan="2">
										<c:choose>
										 <c:when test="${crfStatus=='Not Started'}">	
										  <c:choose>
										    <c:when test="${eventId>0}">
										      <i>Click to Enter Data.<br>(To use another version of this CRF, click on the magnifying glass.)</i>
										 
										    </c:when>
										    <c:otherwise>
										     <i> In order to enter data, you should create an event for this subject first.</i>
										    </c:otherwise>
										  </c:choose>									  
										  
										 </c:when>
										 <c:when test="${eventId<=0 && crfStatus=='Not Started'}">										  
										  <i>Click to Enter Data.<br>(To use another version of this CRF, click on the magnifying glass.)</i>
										 </c:when>
										 <c:otherwise>
										   <i>Click for more options</i>
										 </c:otherwise>
										</c:choose> 
										
										</td>
						</tr>
						<tr id="Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>" style="display: none">
							<td colspan="2">
							<table border="0" cellpadding="0" cellspacing="0" width="100%">		
							   <c:choose>
							    <c:when test="${crfStatus =='data entry complete' ||  crfStatus =='administrative editing'}">
							      <tr valign="top"><td class="table_cell_left"><a href="ViewEventCRFContent?id=<c:out value="${subjectId}"/>&ecId=<c:out value="${eventCrfId}"/>&eventId=<c:out value="${eventId}"/>"><img src="images/bt_View.gif" border="0" align="left" /></a>&nbsp;&nbsp; 
							      <a href="ViewSectionDataEntry?ecId=<c:out value="${eventCrfId}"/>&tabId=1">View</a></td></tr>
								  
								  <tr valign="top"><td class="table_cell_left"><a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${eventCrfId}"/>')"><img src="images/bt_Print.gif" border="0" align="left" //></a>&nbsp;&nbsp; 
								  <a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${eventCrfId}"/>')">Print</a></td></tr>
								 
								  <c:if test="${userRole.role.name=='director' || userBean.sysAdmin}">
								   <tr valign="top"><td class="table_cell_left"><a href="AdministrativeEditing?eventCRFId=<c:out value="${eventCrfId}"/>">
								   <img src="images/bt_Edit.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="AdministrativeEditing?eventCRFId=<c:out value="${eventCrfId}"/>" />Edit</a></td></tr>
								   
								   <!--
								   <tr valign="top"><td class="table_cell_left"><a href="">
								   <img src="images/bt_Lock.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="">Lock</a></td></tr>
								   -->
								   <tr valign="top"><td class="table_cell_left"><a href="RemoveEventCRF?action=confirm&id=<c:out value="${eventCrfId}"/>&studySubId=<c:out value="${subjectId}"/>">
								   <img src="images/bt_Remove.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="RemoveEventCRF?action=confirm&id=<c:out value="${eventCrfId}"/>&studySubId=<c:out value="${subjectId}"/>">Remove</a></td></tr>
								  </c:if>
                                  <c:if test="${userBean.sysAdmin}">
								   <tr valign="top"><td class="table_cell_left"><a href="DeleteEventCRF?action=confirm&ssId=<c:out value="${subjectId}"/>&ecId=<c:out value="${eventCrfId}"/>"><img src="images/bt_Delete.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="DeleteEventCRF?action=confirm&ssId=<c:out value="${subjectId}"/>&ecId=<c:out value="${eventCrfId}"/>">Delete</a></td></tr>
                                  </c:if>
									
							    </c:when>
							    <c:when test="${crfStatus =='locked' }">
							        <tr valign="top"><td class="table_cell_left"><a href="ViewEventCRFContent?id=<c:out value="${subjectId}"/>&ecId=<c:out value="${eventCrfId}"/>&eventId=<c:out value="${eventId}"/>"><img src="images/bt_View.gif" border="0" align="left" /></a>&nbsp;&nbsp; 
							         <a href="ViewSectionDataEntry?ecId=<c:out value="${eventCrfId}"/>&tabId=1">View</a></td></tr>
								  
									<tr valign="top"><td class="table_cell_left"><a href="ViewEventCRFContent?id=<c:out value="${subjectId}"/>&ecId=<c:out value="${eventCrfId}"/>&eventId=<c:out value="${eventId}"/>"><img src="images/bt_Print.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="ViewEventCRFContent?id=<c:out value="${subjectId}"/>&ecId=<c:out value="${eventCrfId}"/>&eventId=<c:out value="${eventId}"/>">Print</a></td></tr>
							        <c:if test="${userRole.role.name=='director' || userBean.sysAdmin}">
							          <tr valign="top"><td class="table_cell_left"><a href="RemoveEventCRF?action=confirm&id=<c:out value="${eventCrfId}"/>&studySubId=<c:out value="${subjectId}"/>"><img src="images/bt_Remove.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="RemoveEventCRF?action=confirm&id=<c:out value="${eventCrfId}"/>&studySubId=<c:out value="${subjectId}"/>">Remove</a></td></tr>
						 	        </c:if>
						 	    </c:when>
						 	    <c:when test="${crfStatus =='Not Started' }">						 	            
						 	            <c:if test="${eventId>0}">
						 	                 <c:choose>
						 	                   <c:when test="${eventCrfId > 0}">
										       <tr valign="top"><td class="table_cell_left"><a href="InitialDataEntry?<c:out value="eventDefinitionCRFId=${edcId}&studyEventId=${eventId}&subjectId=${subjectId}&eventCRFId=${eventCrfId}&crfVersionId=${crfVersionId}"/>"><img src="images/bt_Edit.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="InitialDataEntry?<c:out value="eventDefinitionCRFId=${edcId}&studyEventId=${eventId}&subjectId=${subjectId}&eventCRFId=${eventCrfId}&crfVersionId=${crfVersionId}"/>">Enter Data</a></td></tr>
						 	    		      </c:when>
						 	    		      <c:otherwise>
						 	    		        <tr valign="top"><td class="table_cell_left"><a href="AssessmentInfo?<c:out value="eventDefinitionCRFId=${edcId}&studyEventId=${eventId}&subjectId=${subjectId}&eventCRFId=${eventCrfId}&crfVersionId=${crfVersionId}"/>"><img src="images/bt_Edit.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="AssessmentInfo?<c:out value="eventDefinitionCRFId=${edcId}&studyEventId=${eventId}&subjectId=${subjectId}&eventCRFId=${eventCrfId}&crfVersionId=${crfVersionId}"/>">Enter Data</a></td></tr>
							    		      </c:otherwise>
						 	    		     </c:choose>
						 	    		</c:if>
						 	           <tr valign="top"><td class="table_cell_left"><a href="ViewSectionDataEntry?crfVersionId=<c:out value="${crfVersionId}"/>&tabId=1"><img src="images/bt_View.gif" border="0" align="left" /></a>&nbsp;&nbsp; 
							           <a href="ViewSectionDataEntry?crfVersionId=<c:out value="${crfVersionId}"/>&tabId=1">View</a></td></tr>
								  
								       <tr valign="top"><td class="table_cell_left"><a href="javascript:openDocWindow('PrintCRF?id=<c:out value="${crfVersionId}"/>')"><img src="images/bt_Print.gif" border="0" align="left" /></a>&nbsp;&nbsp; 
								       <a href="javascript:openDocWindow('PrintCRF?id=<c:out value="${crfVersionId}"/>')">Print</a></td></tr>
								 					 	            
										
										   
							       
						 	    </c:when>
						 	    <c:when test="${crfStatus =='invalid' }">
						 	           <tr valign="top"><td class="table_cell_left"><a href="ViewSectionDataEntry?ecId=<c:out value="${eventCrfId}"/>&tabId=1"><img src="images/bt_View.gif" border="0" align="left" /></a>&nbsp;&nbsp; 
							           <a href="ViewSectionDataEntry?ecId=<c:out value="${eventCrfId}"/>&tabId=1">View</a></td></tr>
								  
								       <tr valign="top"><td class="table_cell_left"><a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${eventCrfId}"/>')"><img src="images/bt_Print.gif" border="0" align="left" /></a>&nbsp;&nbsp; 
								       <a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${eventCrfId}"/>')">Print</a></td></tr>
								    <c:if test="${userRole.role.name=='director' || userBean.sysAdmin}">
									<tr valign="top"><td class="table_cell_left"><a href="RestoreEventCRF?action=confirm&id=<c:out value="${eventCrfId}"/>&studySubId=<c:out value="${subjectId}"/>">
									<img src="images/bt_Restore.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="RestoreEventCRF?action=confirm&id=<c:out value="${eventCrfId}"/>&studySubId=<c:out value="${subjectId}"/>">Restore</a></td></tr>
								   </c:if>
						 	            
							       
						 	    </c:when>
							    <c:otherwise>
							      <c:choose>
							        <c:when test="${crfStatus=='initial data entry complete' ||
							        crfStatus=='double data entry'}">
									<tr valign="top"><td class="table_cell_left"><a href="DoubleDataEntry?eventCRFId=<c:out value="${eventCrfId}" />">
									<img src="images/bt_Edit.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="DoubleDataEntry?eventCRFId=<c:out value="${eventCrfId}" />">Enter Data</a></td></tr>
									 
									</c:when>
									<c:otherwise>
									 <tr valign="top"><td class="table_cell_left"><a href="InitialDataEntry?eventCRFId=<c:out value="${eventCrfId}" />">
									 <img src="images/bt_Edit.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="InitialDataEntry?eventCRFId=<c:out value="${eventCrfId}" />">Enter Data</a></td></tr>
									
									</c:otherwise>
									
								 </c:choose>	
									<%--<tr valign="top"><td class="table_cell_left"><a href="MarkEventCRFComplete?eventCRFId=<c:out value="${eventCrfId}" />"><img src="images/icon_DEcomplete.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="MarkEventCRFComplete?eventCRFId=<c:out value="${eventCrfId}" />" onClick="javascript:return confirm('Click OK to mark the <c:out value="${crfName}"/> completed for Subject <c:out value="${subjectName}"/>.');">Mark Complete</a></td></tr>--%>
									
									<tr valign="top"><td class="table_cell_left"><a href="ViewSectionDataEntry?ecId=<c:out value="${eventCrfId}"/>&tabId=1"><img src="images/bt_View.gif" border="0" align="left" /></a>&nbsp;&nbsp; 
							         <a href="ViewSectionDataEntry?ecId=<c:out value="${eventCrfId}"/>&tabId=1">View</a></td></tr>
								  
									<tr valign="top"><td class="table_cell_left"><a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${eventCrfId}"/>')"><img src="images/bt_Print.gif" border="0" align="left" /></a>&nbsp;&nbsp; 
									<a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${eventCrfId}"/>')">Print</a></td></tr>
								  
								   <c:if test="${userRole.role.name=='director' || userBean.sysAdmin}">
									<tr valign="top"><td class="table_cell_left"><a href="RemoveEventCRF?action=confirm&id=<c:out value="${eventCrfId}"/>&studySubId=<c:out value="${subjectId}"/>">
									<img src="images/bt_Remove.gif" border="0" align="left" /></a>&nbsp;&nbsp; <a href="RemoveEventCRF?action=confirm&id=<c:out value="${eventCrfId}"/>&studySubId=<c:out value="${subjectId}"/>">Remove</a></td></tr>
								   </c:if>
								   <c:if test="${userBean.sysAdmin}">
								    <tr valign="top"><td class="table_cell_left"><a href="DeleteEventCRF?action=confirm&ssId=<c:out value="${subjectId}"/>&ecId=<c:out value="${eventCrfId}"/>">
								    <img src="images/bt_Delete.gif" border="0" align="left" /></a>&nbsp;&nbsp; 
								    <a href="DeleteEventCRF?action=confirm&ssId=<c:out value="${subjectId}"/>&ecId=<c:out value="${eventCrfId}"/>">Delete</a></td></tr>
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
         
         <c:choose>
         <c:when test="${crfStatus =='data entry complete' ||  crfStatus =='administrative editing'}">
          <a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" 
							onmouseover="moveObject('Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Complete_expand.gif');"
							onmouseout="layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Complete.gif');"
							onClick="layersShowOrHide('visible','Lock_all'); LockObject('Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);">
		  
		  </c:when>
		  <c:when test="${crfStatus =='initial data entry complete'}">
          <a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" 
							onmouseover="moveObject('Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_InitialDEcomplete_expand.gif');"
							onmouseout="layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_InitialDEcomplete.gif');"
							onClick="layersShowOrHide('visible','Lock_all'); LockObject('Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);">
		  
		  </c:when>
		  <c:when test="${crfStatus =='double data entry'}">
          <a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" 
							onmouseover="moveObject('Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_DDE_expand.gif');"
							onmouseout="layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_DDE.gif');"
							onClick="layersShowOrHide('visible','Lock_all'); LockObject('Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);">
		  
		  </c:when>
		  <c:when test="${crfStatus =='locked' }">
							       
		 </c:when>
		 <c:when test="${crfStatus =='Not Started' }">
			<a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" 
							onmouseover="moveObject('Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Scheduled.gif');"
							onmouseout="layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Scheduled.gif');"
							onClick="layersShowOrHide('visible','Lock_all'); LockObject('Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);">
		  				    
		 </c:when>
		 <c:when test="${crfStatus =='invalid' }">
			<a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" 
							onmouseover="moveObject('Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Invalid_expand.gif');"
							onmouseout="layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Invalid.gif');"
							onClick="layersShowOrHide('visible','Lock_all'); LockObject('Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);">
		  				    
		 </c:when>
		 <c:otherwise>
		   <a href="javascript:leftnavExpand('Menu_on_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>'); leftnavExpand('Menu_off_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');" 
							onmouseover="moveObject('Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Started.gif');"
							onmouseout="layersShowOrHide('hidden','Event_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>');
							javascript:setImage('CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>','images/CRF_status_icon_Started.gif');"
							onClick="layersShowOrHide('visible','Lock_all'); LockObject('Lock_<c:out value="${eblRowCount}"/>_<c:out value="${count}"/>_<c:out value="${subjectName}"/>', event);">
		  			
		 </c:otherwise>
		  
		</c:choose>					