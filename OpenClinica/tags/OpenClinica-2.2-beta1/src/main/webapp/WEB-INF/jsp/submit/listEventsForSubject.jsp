<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>



 
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

<c:choose>
   <c:when test="${userRole.manageStudy && module=='manage'}">
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

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		<div class="sidebar_tab_content">

			<fmt:message key="select_subject_view_more_details" bundle="${restext}"/>

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<!-- the object inside the array is StudySubjectBean-->
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.core.EntityBeanTable'/>



<h1>
<c:choose>
   <c:when test="${userRole.manageStudy}">
    <span class="title_manage">
    <fmt:message key="manage_all_subjects_in" bundle="${restext}"/> <c:out value="${study.name}"/> 
    <a href="javascript:openDocWindow('help/2_1_viewAllSubjects_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></a></span></h1>
   </c:when>
   <c:otherwise>
    <span class="title_submit">
    <fmt:message key="view_all_subjects_in" bundle="${restext}"/> <c:out value="${study.name}"/>
    
<a href="javascript:openDocWindow('help/2_1_viewAllSubjects_Help.html')"><img src="images/bt_Help_Submit.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></a></span></h1>
   </c:otherwise> 
  </c:choose> 







<!--<p>The following is a list of all the subjects enrolled in the
<c:out value="${study.name}" /> study, together with the status of each subject's
study events.  Select any subject to view more details and to enter subject event data.
You may also enroll a new subject and add a new study event:

<div class="homebox_bullets"><a href="AddNewSubject?instr=1">Enroll a New Subject</a></div><br>

<div class="homebox_bullets"><a href="CreateNewStudyEvent">Add a New Study Event</a></div><br>
-->

<!---study event definition tabs -->
<table border="0" cellpadding="0" cellspacing="0">
   <tr>
	<td style="padding-left: 12px" valign="bottom">
	<div id="Tab0NotSelected" style="display:none">
	<div class="tab_BG"><div class="tab_L"><div class="tab_R">

<c:choose>
   <c:when test="${userRole.manageStudy}">
    <a class="tabtext" href="ListStudySubject" onclick="javascript:HighlightTab(0);">All Events</a>
   </c:when>
   <c:otherwise>
    <a class="tabtext" href="ListStudySubjectsSubmit" onclick="javascript:HighlightTab(0);"><fmt:message key="all_events" bundle="${restext}"/></a>
   </c:otherwise> 
  </c:choose> 


	</div></div></div>
	</div>
	<div id="Tab0Selected" style="display:all">
	<div class="tab_BG"><div class="tab_L"><div class="tab_R">

<c:choose>
   <c:when test="${userRole.manageStudy}">
    <a class="tabtext" href="ListStudySubject" onclick="javascript:HighlightTab(0);"><fmt:message key="all_events" bundle="${restext}"/></a>
   </c:when>
   <c:otherwise>
    <a class="tabtext" href="ListStudySubjectsSubmit" onclick="javascript:HighlightTab(0);"><fmt:message key="all_events" bundle="${restext}"/></a>
   </c:otherwise> 
  </c:choose> 

	</div></div></div>
	</div>
	</td>
	<td align="right" style="padding-left: 12px; display: none" id="TabsBack"><a href="javascript:TabsBack()"><img src="images/arrow_back.gif" border="0"></a></td>
	<td align="right" style="padding-left: 12px; display: all" id="TabsBackDis"><img src="images/arrow_back_dis.gif" border="0"></td>


<script language="JavaScript">
<!--

// Total number of tabs (one for each CRF)
var TabsNumber = <c:out value="${allDefsNumber}"/>;

// Number of tabs to display at a time
var TabsShown = 3;

// Labels to display on each tab (name of CRF)
var TabLabel = new Array(TabsNumber)
var TabFullName = new Array(TabsNumber)
var TabDefID = new Array(TabsNumber)
   <c:set var="count" value="0"/>  
 <c:forEach var="def" items="${allDefsArray}">   
   TabFullName[<c:out value="${count}"/>]= "<c:out value="${def.name}"/>";
  
   TabLabel[<c:out value="${count}"/>]= "<c:out value="${def.name}"/>";
   if (TabLabel[<c:out value="${count}"/>].length>12) 
   {
    var shortName = TabLabel[<c:out value="${count}"/>].substring(0,11);
    TabLabel[<c:out value="${count}"/>]= shortName + '...';
   }
   TabDefID[<c:out value="${count}"/>]= "<c:out value="${def.id}"/>";
   <c:set var="count" value="${count+1}"/> 
 </c:forEach>
   
DisplaySectionTabs()	

function DisplaySectionTabs()
	{
	TabID=1;

	while (TabID<=TabsNumber)
	 
		{
		defID = TabDefID[TabID-1];
		url = "ListEventsForSubject?module=<c:out value="${module}"/>&defId=" + defID + "&tab=" + TabID;
		currTabID = <c:out value="${tabId}"/>;
		if (TabID<=TabsShown)
			{
			document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: all">');
			}
		else
			{
			document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: none">');
			}
	    if (TabID != currTabID) {		    	
		document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG"><div class="tab_L"><div class="tab_R">');
		document.write('<a class="tabtext" title="' + TabFullName[(TabID-1)] + '" href=' + url + ' onclick="javascript:HighlightTab(' + TabID + ');">' + TabLabel[(TabID-1)] + '</a></div></div></div></div>');
		document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
		document.write('</td>');
		} 
		else {
		//alert(TabID);
		document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h">');
		document.write('<span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
		document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
		document.write('</td>');
		}
	
		TabID++
	
		}
	}


//-->
</script>

	<td align="right"id="TabsNextDis" style="display: none"><img src="images/arrow_next_dis.gif" border="0"></td>
	<td align="right"id="TabsNext" style="display: all"><a href="javascript:TabsForward()"><img src="images/arrow_next.gif" border="0"></a></td>
 
   </tr>
</table>



<c:import url="../include/showTableWithTab.jsp">
	<c:param name="rowURL" value="showEventsForSubjectRow.jsp" />
	<c:param name="groupNum" value="${groupSize}"/>
	<c:param name="eventDefCRFNum" value="${eventDefCRFSize}"/>
	<c:param name="module" value="${module}"/>
</c:import>

<br><br>

<!-- EXPANDING WORKFLOW BOX -->

<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
	<tr>
		<td id="sidebar_Workflow_closed" style="display: none">
		<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/tab_Workflow_closed.gif" border="0"></a>
	</td>
	<td id="sidebar_Workflow_open" style="display: all">
	<table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
		<tr>
			<td class="workflowBox_T" valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="workflow_tab">
					<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

					<b><fmt:message key="workflow" bundle="${restext}"/></b>

					</td>
				</tr>
			</table>
			</td>
			<td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif"></td>
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
                             <c:when test="${userRole.manageStudy}">
                               <span class="title_manage">
                               <a href="ManageStudy"><fmt:message key="manage_study" bundle="${resworkflow}"/></a>
                             </c:when>
                             <c:otherwise>
                               <span class="title_submit">
                               <a href="ListStudySubjectsSubmit"><fmt:message key="submit_data" bundle="${resworkflow}"/></a>
                             </c:otherwise> 
                             </c:choose> 		
				
					
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<c:choose>
                             <c:when test="${userRole.manageStudy}">
                               <span class="title_manage">
                             </c:when>
                             <c:otherwise>
                               <span class="title_submit">
                             </c:otherwise> 
                             </c:choose> 				
					
							<b><fmt:message key="list_subjects" bundle="${restext}"/></b>					
				
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
