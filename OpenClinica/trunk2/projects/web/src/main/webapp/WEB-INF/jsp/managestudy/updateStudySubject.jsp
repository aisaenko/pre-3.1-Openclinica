<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>



<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

<c:import url="../include/managestudy-header.jsp"/>

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
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="session" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope="session" id="enrollDateStr" class="java.lang.String"/>

<body class="aka_bodywidth" onload=
  "if(! detectFirefoxWindows(navigator.userAgent)){document.getElementById('centralContainer').style.display='none';new Effect.Appear('centralContainer', {duration:1});};
        <c:if test='${popUpURL != ""}'>
		openDNoteWindow('<c:out value="${popUpURL}" />');</c:if>">

	<h1><span class="title_manage"><fmt:message key="update_study_subject_details" bundle="${resword}"/>
</span></h1>

<form action="UpdateStudySubject" method="post">
<input type="hidden" name="action" value="confirm">
<input type="hidden" name="id" value="<c:out value="${studySub.id}"/>">

 <P><div class="indicates_required_field">* <fmt:message key="indicates_required_field" bundle="${resword}"/></div></P>
  
<c:choose>
<c:when test="${userBean.techAdmin || userBean.sysAdmin || userRole.manageStudy || userRole.investigator 
    || (study.parentStudyId > 0 && userRole.researchAssistant)}">
     
	<table border="0" cellpadding="0" cellspacing="0"   class="shaded_table table_first_column_w30 ">
	  <tr valign="top">
	  <td class="formlabel">	  	<fmt:message key="study_subject_ID" bundle="${resword}"/>:	  </td>
	  <td>
	  	<div class="formfieldXL_BG">
	  	<input type="text" name="label" value="<c:out value="${studySub.label}"/>" class="formfieldXL">*
	  	</div>
	  	<br>
	  	<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="label"/></jsp:include>
	  </td>
	  </tr>
	  <tr valign="top">
	  <td class="formlabel"><fmt:message key="secondary_ID" bundle="${resword}"/>:</td>
	  <td><div class="formfieldXL_BG"><input type="text" name="secondaryLabel" value="<c:out value="${studySub.secondaryLabel}"/>" class="formfieldXL"></div>	  </td>
	  </tr>
	  <tr valign="top">
	  <td class="formlabel"><fmt:message key="enrollment_date" bundle="${resword}"/>:</td>
	  <td>	  <div class="formfieldXL_BG">

	  <input type="text" name="enrollmentDate" value="<c:out value="${enrollDateStr}" />" class="formfieldXL" id="enrollmentDateField">
	  <A HREF="#" >
	      <img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" id="enrollmentDateTrigger"/>
	      <script type="text/javascript">
	      Calendar.setup({inputField  : "enrollmentDateField", ifFormat    : "<fmt:message key="date_format_calender" bundle="${resformat}"/>", button      : "enrollmentDateTrigger" });
	      </script>
	  </a>
	  <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
	      <a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?subjectId=${studySub.id}&id=<c:out value="${studySub.id}"/>&name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate'); return false;"><img id="flag_enrollmentDate" name="flag_enrollmentDate" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
	  </c:if></div>
	   <br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="enrollmentDate"/></jsp:include></td>
	 
	  </td>
	 </tr>
	</table>
	
</c:when>
<c:otherwise>
	<table border="0" cellpadding="0" cellspacing="0"   class="shaded_table  table_first_column_w30">
	  <tr valign="top"><td class="table_header_column"><fmt:message key="label" bundle="${resword}"/>:</td><td class="table_cell">
	  <input type="text" name="label" value="<c:out value="${studySub.label}"/>" disabled="disabled" class="formfieldM">
	  </td></tr>
	  <tr valign="top"><td class="table_header_column"><fmt:message key="secondary_ID" bundle="${resword}"/>:</td><td class="table_cell">
	  <input type="text" name="secondaryLabel" value="<c:out value="${studySub.secondaryLabel}"/>" disabled="disabled" class="formfieldM">
	  </td></tr>
	  <tr valign="top"><td class="table_header_column"><fmt:message key="enrollment_date" bundle="${resword}"/>:</td><td class="table_cell">
	  <input type="text" name="enrollmentDate" value="<c:out value="${enrollDateStr}" />" disabled="disabled" class="formfieldM" id="enrollmentDateField">
	  </td></tr>
	 </table>


</c:otherwise>
</c:choose>

<br>
<c:if test="${(!empty groups)}">
<br>
<table border="0" cellpadding="0" cellspacing="0"   class="shaded_table  table_first_column_w30">


  <tr valign="top">
	<td class="formlabel" ><fmt:message key="subject_group_class" bundle="${resword}"/>:
	<td class="table_cell">
	<c:set var="count" value="0"/>
	<table border="0" cellpadding="0" cellspacing="0" >
	  <c:forEach var="group" items="${groups}">
	  <tr valign="top">
	   <td><b><c:out value="${group.name}"/></b></td>
	   <td> <select name="studyGroupId<c:out value="${count}"/>" class="formfieldM">
	    	<c:if test="${group.subjectAssignment=='Optional'}">
	    	  <option value="0">--</option>
	    	</c:if>
	    	<c:forEach var="sg" items="${group.studyGroups}">
	    	  <c:choose>
				<c:when test="${group.studyGroupId == sg.id}">
					<option value="<c:out value="${sg.id}" />" selected><c:out value="${sg.name}"/></option>
				</c:when>
				<c:otherwise>
				    <option value="<c:out value="${sg.id}"/>"><c:out value="${sg.name}"/></option>
				</c:otherwise>
			 </c:choose>
	    	</c:forEach>
	    	</select>
	    	<c:if test="${group.subjectAssignment=='Required'}">
	    	  <td align="left">*</td>
	    	</c:if>
	    	</td></tr>
	    	<tr valign="top">
	    	<td><fmt:message key="notes" bundle="${resword}"/>:</td>
	    	<td>
	    	<div class="formfieldXL_BG"><input type="text" class="formfieldXL" name="notes<c:out value="${count}"/>"  value="<c:out value="${group.groupNotes}"/>"></div>
	          <c:import url="../showMessage.jsp"><c:param name="key" value="notes${count}" /></c:import>
	        </td></tr>
	       <c:set var="count" value="${count+1}"/>
	  </c:forEach>
	  </table>
	</td>
  </tr>



</table>

</c:if>
<br>
 <input type="submit" name="Submit" value="<fmt:message key="confirm_changes" bundle="${resword}"/>" class="button_long">
 <input type="button" onclick="confirmCancel('ListStudySubjects');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>

</body>
<br><br>
<jsp:include page="../include/footer.jsp"/>
