<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">

			Select any subject to view more details and to add or review subject data.

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<!-- the object inside the array is StudySubjectBean-->
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.core.EntityBeanTable'/>



<h1><span class="title_manage">
Manage All Subjects in <c:out value="${study.name}"/> <a href="javascript:openDocWindow('help/4_2_subjects_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="Help" title="Help"></a>
</span></h1>

<!--<p>List of all subjects with their enrollment dates and status of study events. Select any subject for details on his/her subject record and study events or reassign him/her to a different study/site.</p>

<p>The list of subjects in the current study/site is shown below.</p>

<div class="homebox_bullets"><a href="AddNewSubject">Enroll a New Subject</a></div>
<p></p>
-->
<!---study event definition tabs -->
<table border="0" cellpadding="0" cellspacing="0">
   <tr>
	<td style="padding-left: 12px" valign="bottom">
	<div id="Tab0NotSelected" style="display:none">
	<div class="tab_BG"><div class="tab_L"><div class="tab_R">

<a class="tabtext" href="ListStudySubjectsSubmit" onclick="javascript:HighlightTab(0);">All Events</a>

	</div></div></div>
	</div>
	<div id="Tab0Selected" style="display:all">
	<div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h">

<span class="tabtext">All Events</span>

	</div></div></div>
	</div>
	</td>
	<td align="right" style="padding-left: 12px; display: none" id="TabsBack"><a href="javascript:TabsBack()"><img src="http://demo.openclinica.org:8080/OpenClinica/images/arrow_back.gif" border="0"></a></td>
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
   
DisplayTabs()	

function DisplayTabs()
	{
	TabID=1;

	while (TabID<=TabsNumber)
	 
		{
		defID = TabDefID[TabID-1];
		url = "ListEventsForSubject?defId=" + defID + "&tab=" + TabID;
		if (TabID<=TabsShown)
			{
			document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: all">');
			}
		else
			{
			document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: none">');
			}
		document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG"><div class="tab_L"><div class="tab_R">');
		document.write('<a class="tabtext" title="' + TabFullName[(TabID-1)] + '" href=' + url + ' onclick="javascript:HighlightTab(' + TabID + ');">' + TabLabel[(TabID-1)] + '</a></div></div></div></div>');
		document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
		document.write('</td>');
	
		TabID++
	
		}
	}


//-->
</script>

	<td align="right"id="TabsNextDis" style="display: none"><img src="images/arrow_next_dis.gif" border="0"></td>
	<td align="right"id="TabsNext" style="display: all"><a href="javascript:TabsForward()"><img src="http://demo.openclinica.org:8080/OpenClinica/images/arrow_next.gif" border="0"></a></td>
 
   </tr>
</table>

<script language="JavaScript">
<!--

function showSubjectRow(strLeftNavRowElementName, groupNum, subjectRowID1,subjectRowID2 ){

	var objLeftNavRowElement;

    objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
    if (objLeftNavRowElement != null) {
        if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; } 
	    if (objLeftNavRowElement.display == "none" ){
	     HideGroups(0,groupNum,10);
	    }		
	}
	leftnavExpand(subjectRowID1); 
	leftnavExpand(subjectRowID2);
}

//-->
</script>

<c:import url="../include/showTableWithTabForSubject.jsp">
	<c:param name="rowURL" value="showStudySubjectRow.jsp" />
	<c:param name="groupNum" value="${groupSize}"/>
</c:import>

<c:import url="../submit/addNewSubjectExpress.jsp">	
</c:import>
				
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
					<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

					<b>Workflow</b>

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
	
							<span class="title_manage">
				
					
						
							<a href="ManageStudy"> Manage Study</a>
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_manage">
				
					
							<b>Manage Subjects</b>
					
				
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

