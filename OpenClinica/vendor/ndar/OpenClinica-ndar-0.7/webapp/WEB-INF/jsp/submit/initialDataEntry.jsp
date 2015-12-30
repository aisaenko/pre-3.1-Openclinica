<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/submit-header-inactive.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox-inactive.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
         Please enter data in the form provided. To exit this form you must click either the 'Save' or 'Exit' button.
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/submitSideInfo-inactive.jsp"/>

<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />

<%-- set button text depending on whether or not the user is confirming values --%>
<c:choose>
	<c:when test="${section.checkInputs}">
		<c:set var="buttonAction" value="Save" />
		<c:set var="checkInputsValue" value="1" />
	</c:when>
	<c:otherwise>
		<c:set var="buttonAction" value="Confirm values" />
		<c:set var="checkInputsValue" value="0" />	
	</c:otherwise>
</c:choose>

<h1><span class="title_submit">Initial Data Entry for <c:out value="${section.crf.name}" /> <c:out value="${section.crfVersion.name}" /> <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step2c')"><img src="images/bt_Help_Submit.gif" border="0" alt="Help" title="Help"></a></span></h1>
</div>

<form name="crfForm" method="POST" action="InitialDataEntry">
<input type="hidden" name="eventCRFId" value="<c:out value="${section.eventCRF.id}"/>" />
<input type="hidden" name="sectionId" value="<c:out value="${section.section.id}"/>" />
<input type="hidden" name="checkInputs" value="<c:out value="${checkInputsValue}"/>" />
<input type="hidden" name="tab" value="<c:out value="${tabId}"/>" />

<SCRIPT LANGUAGE="JavaScript" type="text/javascript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1" type="text/javascript">  
  var cal1 = new CalendarPopup("testdiv1");
  </SCRIPT>
  
<c:import url="interviewer.jsp"/>
<br><br>
<c:set var="sectionNum" value="0"/> 
<c:forEach var="section" items="${toc.sections}">
<c:set var="sectionNum" value="${sectionNum+1}"/> 
</c:forEach>



<!-- section tabs here -->
<table border="0" cellpadding="0" cellspacing="0">
   <tr>
	<td align="right" style="padding-left: 12px; display: none" id="TabsBack"><a href="javascript:TabsBack()"><img src="images/arrow_back.gif" border="0" alt="<" title=""></a></td>
	<td align="right" style="padding-left: 12px; display: all" id="TabsBackDis"><img src="images/arrow_back.gif" border="0" alt="<" title=""></td>


<script langauge="JavaScript" type="text/javascript">
<!--

// Total number of tabs (one for each CRF)
var TabsNumber = <c:out value="${sectionNum}"/>;


// Number of tabs to display at a time
var TabsShown = 3;


// Labels to display on each tab (name of CRF)
var TabLabel = new Array(TabsNumber)
var TabFullName = new Array(TabsNumber)
var TabSectionId = new Array(TabsNumber)
<c:set var="count" value="0"/>  
<c:forEach var="section" items="${toc.sections}">
    TabFullName[<c:out value="${count}"/>]="<c:out value="${section.label}"/> (<c:out value="${section.numItemsCompleted}"/>/<c:out value="${section.numItems}" />)";
 	
 	TabSectionId[<c:out value="${count}"/>]= <c:out value="${section.id}"/>;
 	
 	TabLabel[<c:out value="${count}"/>]="<c:out value="${section.label}"/>";
    if (TabLabel[<c:out value="${count}"/>].length>8) {
      var shortName = TabLabel[<c:out value="${count}"/>].substring(0,7);
      TabLabel[<c:out value="${count}"/>]= shortName + '...' + "<span style='font-weight: normal;'>(<c:out value="${section.numItemsCompleted}"/>/<c:out value="${section.numItems}" />)</span>";
   } else {
     TabLabel[<c:out value="${count}"/>]="<c:out value="${section.label}"/> " + "<span style='font-weight: normal;'>(<c:out value="${section.numItemsCompleted}"/>/<c:out value="${section.numItems}" />)</span>";
   }
    
     <c:set var="count" value="${count+1}"/> 
</c:forEach>	
DisplaySectionTabs()



function DisplaySectionTabs()
	{
	TabID=1;	

	while (TabID<=TabsNumber)
	 
		{
		sectionId = TabSectionId[TabID-1];
		url = "InitialDataEntry?eventCRFId=" + <c:out value="${section.eventCRF.id}"/> + "&sectionId=" + sectionId + "&tab=" + TabID;
		currTabID = <c:out value="${tabId}"/>;
		
		if (TabID<=TabsShown)
			{
			document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: all" >');
			}
		else
			{
			document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: none" >');
			}
		if (TabID != currTabID) {		    	
		document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG"><div class="tab_L"><div class="tab_R">');
		document.write('<a class="tabtext" title="' + TabFullName[(TabID-1)] + '" href=' + url + ' onclick="return checkSectionStatus();">' + TabLabel[(TabID-1)] + '</a></div></div></div></div>');
		document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
		document.write('</td>');
		} 
		else {
		document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h">');
		document.write('<span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
		document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
		document.write('</td>');
		}			
		TabID++
	
		}
		
		
	}
	
function checkDataStatus() {  
    
    objImage=document.getElementById('status_top');
    if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) { 
       return confirm('You have unsaved data. Are you sure you want to jump to another section without saving?');
    }   
     
    return true;
}		

function gotoLink() {
var OptionIndex=crfForm.sectionName.selectedIndex;
if (checkDataStatus()) {
window.location = crfForm.sectionName.options[OptionIndex].value;
}
}

//-->
</script>

	<td align="right"id="TabsNextDis" style="display: none"><img src="images/arrow_next_dis.gif" border="0" alt=">" title=""></td>
	<td align="right"id="TabsNext" style="display: all"><a href="javascript:TabsForward()"><img src="images/arrow_next.gif" border="0" alt=">" title=""></a></td>
    <td>&nbsp;
       <div class="formfieldM_BG_noMargin">
       <select class="formfieldM" name="sectionName" size="1" onchange="gotoLink();">
        <c:set var="tabCount" value="1"/>
        <option selected>-- Select to Jump --</option>
       <c:forEach var="sec" items="${toc.sections}" >
        <c:set var="tabUrl" value = "InitialDataEntry?eventCRFId=${section.eventCRF.id}&sectionId=${sec.id}&tab=${tabCount}"/>
        <option value="<c:out value="${tabUrl}"/>"><c:out value="${sec.name}"/></option>        
        <c:set var="tabCount" value="${tabCount+1}"/>
        </c:forEach>
        </select>
        </div>
     </td>
   </tr>
</table>

<jsp:include page="../include/showSubmitted.jsp" />

<jsp:include page="showSection.jsp" />
<!--
<table>
	<tr>
<c:if test="${!section.firstSection}">
		<td><input type="submit" name="submittedPrev" value="&lt;&lt; <c:out value="${buttonAction}"/> and Go Back" class="button_xlong" /></td>
</c:if>
		<td><input type="submit" name="submittedResume" value="<c:out value="${buttonAction}"/> and Resume Later" class="button_xlong" /></td>
<c:if test="${!section.lastSection}">
		<td><input type="submit" name="submittedNext" value="<c:out value="${buttonAction}"/> and Go Forward &gt;&gt;" class="button_xlong" /></td>
</c:if>
	</tr>
</table>
-->
</form>

<c:import url="instructionsEnterData.jsp">
	<c:param name="currStep" value="dataEntry" />
</c:import>

<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>


<jsp:include page="../include/footer-inactive.jsp"/>