<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>


 <c:import url="../include/managestudy-header.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		<div class="sidebar_tab_content">
			<fmt:message key="fill_to_add_click_help" bundle="${restext}"/>
		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="session" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean" />
<jsp:useBean scope="request" id="pageMessages" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<jsp:useBean scope="request" id="groups" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="fathers" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="mothers" class="java.util.ArrayList" />

<c:set var="uniqueIdentifier" value="" />
<c:set var="chosenGender" value="" />
<c:set var="label" value="" />
<c:set var="secondaryLabel" value="" />
<c:set var="enrollmentDate" value="" />
<c:set var="dob" value="" />
<c:set var="yob" value="" />
<c:set var="groupId" value="${0}" />
<c:set var="fatherId" value="${0}" />
<c:set var="motherId" value="${0}" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "uniqueIdentifier"}'>
		<c:set var="uniqueIdentifier" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "gender"}'>
		<c:set var="chosenGender" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "label"}'>
		<c:set var="label" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "secondaryLabel"}'>
		<c:set var="secondaryLabel" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "enrollmentDate"}'>
		<c:set var="enrollmentDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dob"}'>
		<c:set var="dob" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "yob"}'>
		<c:set var="yob" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "group"}'>
		<c:set var="groupId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "mother"}'>
		<c:set var="motherId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "father"}'>
		<c:set var="fatherId" value="${presetValue.value}" />
	</c:if>
</c:forEach>


<h1><span class="title_manage">
<c:out value="${study.name}" />:
    <fmt:message key="add_subject" bundle="${resword}"/>
    <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/openclinica-user-guide/submit-data-module-overview/add-subject')">
        <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></a>
</span></h1>

 <P><span class="indicates_required_field">*</span> <fmt:message key="indicates_required_field" bundle="${resword}"/><br>
</P>

<form action="AddNewSubject" method="post">
<jsp:include page="../include/showSubmitted.jsp" />

<table border="0" cellpadding="0" cellspacing="0"   class="shaded_table table_first_column_w30 ">
		<tr valign="top">
		<td class="formlabel"><fmt:message key="study_subject_ID" bundle="${resword}"/>:</td>
		<td valign="top">
			<div >
<c:choose>
<c:when test="${study.studyParameterConfig.subjectIdGeneration =='auto non-editable'}">
<input onfocus="this.select()" type="text" value="<c:out value="${label}"/>" size="45" class="formfield required" disabled>
<input type="hidden" name="label" value="<c:out value="${label}"/>">
</c:when>
<c:otherwise>
<input onfocus="this.select()" type="text" name="label" value="<c:out value="${label}"/>" size="50" class="formfieldXL required">
</c:otherwise>
</c:choose><span class="indicates_required_field">*</span></div>
<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="label"/></jsp:include></td>
	</tr>
	<c:choose>
	<c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='required'}">
	<tr valign="top">
	  	<td class="formlabel"><fmt:message key="person_ID" bundle="${resword}"/>:</td>
		<td valign="top">
			<div >
						<input onfocus="this.select()" type="text" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>" size="50" class="formfieldXL">
<span class="indicates_required_field">*</span>
<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
<a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=uniqueIdentifier&column=unique_identifier','spanAlert-uniqueIdentifier'); return false;">
<img name="flag_uniqueIdentifier" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a></c:if>
</div>				
<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueIdentifier"/></jsp:include>
				
		</td>
	</tr>
	</c:when>
	<c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='optional'}">
	<tr valign="top">
	  	<td class="formlabel"><fmt:message key="person_ID" bundle="${resword}"/>:</td>
		<td valign="top">
		<div >
<input onfocus="this.select()" type="text" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>" size="50" class="formfieldXL">
</div></td>	</tr>
<tr>
	<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueIdentifier"/></jsp:include></td>
</tr>
	</c:when>
	<c:otherwise>
	  <input type="hidden" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>">
	</c:otherwise>
	</c:choose>

	<tr valign="top">
	  	<td class="formlabel"><fmt:message key="secondary_ID" bundle="${resword}"/></td>
		<td valign="top"><div>
<input onfocus="this.select()" type="text" name="secondaryLabel" value="<c:out value="${secondaryLabel}"/>" size="50" class="formfieldXL">
	</div></td>
</tr>
<tr>
	<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondaryLabel"/></jsp:include></td></tr>
<tr valign="top">

<td class="formlabel">
          <c:if test="${study.parentStudyId == 0}">
    <fmt:message key="date_of_enrollment_for_study" bundle="${resword}"/>'
    <c:out value="${study.name}" /> ' :
</c:if>
<c:if test="${study.parentStudyId > 0}">
    <fmt:message key="date_of_enrollment_for_study" bundle="${resword}"/>'
    <c:out value="${study.parentStudyName}" /> ' :
</c:if>

</td>
	<td valign="top">
<div>
<input onfocus="this.select()" type="text" name="enrollmentDate" size="15" value="<c:out value="${enrollmentDate}" />" class="formfieldM required" id="enrollmentDateField" />
<span class="indicates_required_field">*</span>
<A HREF="#">
		  <img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" id="enrollmentDateTrigger" />
<script type="text/javascript">
Calendar.setup({inputField  : "enrollmentDateField", ifFormat    : "<fmt:message key="date_format_calender" bundle="${resformat}"/>", button      : "enrollmentDateTrigger" });
</script>

</a>
<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
<a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate'); return false;">
<img name="flag_enrollmentDate" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
</a>
</c:if></div>
	<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="enrollmentDate"/></jsp:include></td>
	</tr>


	<tr valign="top">
        <c:if test="${study.studyParameterConfig.genderRequired !='not used'}">
        <td class="formlabel"><fmt:message key="gender" bundle="${resword}"/>:</td>
		<td valign="top"><div >	<select name="gender" class="formfieldS">
		<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
<c:choose>
<c:when test="${!empty chosenGender}">
	<c:choose>
		<c:when test='${chosenGender == "m"}'>
			<option value="m" selected><fmt:message key="male" bundle="${resword}"/></option>
			<option value="f"><fmt:message key="female" bundle="${resword}"/></option>
		</c:when>
		<c:otherwise>
			<option value="m"><fmt:message key="male" bundle="${resword}"/></option>
			<option value="f" selected><fmt:message key="female" bundle="${resword}"/></option>
		</c:otherwise>
	</c:choose>
                        </c:when>
                     <c:otherwise>
                 		<option value="m"><fmt:message key="male" bundle="${resword}"/></option>
                 		<option value="f"><fmt:message key="female" bundle="${resword}"/></option>
                    	</c:otherwise>
                	</c:choose>
                 </select>
                  <c:choose>
        <c:when test="${study.studyParameterConfig.genderRequired !='false'}">
           <span class="indicates_required_field">*</span>
        </c:when>
        </c:choose>
        <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
	        <a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=gender&column=gender','spanAlert-gender'); return false;">
	        <img name="flag_gender" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
	    </c:if>
                 </div>
     <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="gender"/></jsp:include></td>
			
    </c:if>
    </tr>

	<c:choose>
	<c:when test="${study.studyParameterConfig.collectDob == '1'}">
	<tr valign="top">
		<td class="formlabel"><fmt:message key="date_of_birth" bundle="${resword}"/>:</td>
	  	<td valign="top"><div>
	  		<input onfocus="this.select()" type="text" name="dob" size="15" value="<c:out value="${dob}" />" class="formfieldM required" id="dobField" />
				 <span class="formlabel">*</span>
				 <A HREF="#">
  					  <img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" id="dobTrigger" />
                        <script type="text/javascript">
                        Calendar.setup({inputField  : "dobField", ifFormat    : "<fmt:message key="date_format_calender" bundle="${resformat}"/>", button      : "dobTrigger" });
                        </script> </a>
                  <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}" >
					 <a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=dob&column=date_of_birth','spanAlert-dob'); return false;">
					<img name="flag_dob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
					</a>
					</c:if></div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dob"/></jsp:include></td>
				
	</tr>
	</c:when>
	
	
	
	
	<c:when test="${study.studyParameterConfig.collectDob == '2'}">
	<tr valign="top">
		<td class="formlabel"><fmt:message key="year_of_birth" bundle="${resword}"/>:</td>
	  	<td valign="top"><div>
	  	<input onfocus="this.select()" type="text" name="yob" size="15" value="<c:out value="${yob}" />" class="formfieldM required" />
		(<fmt:message key="date_format_year" bundle="${resformat}"/>)
		 <span class="indicates_required_field">*</span>
		<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=yob&column=date_of_birth','spanAlert-yob'); return false;">
			<img name="flag_yob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
			</c:if></div>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="yob"/></jsp:include></td>
	</tr>
  </c:when>
  <c:otherwise>
    <input type="hidden" name="dob" value="" />
  </c:otherwise>
 </c:choose>



</table>

<c:if test="${(!empty groups)}">
<br>
<table border="0" cellpadding="0" cellspacing="0"   class="shaded_table  table_first_column_w30">

  <tr valign="top">
	<td class="formlabel"><fmt:message key="subject_group_class" bundle="${resword}"/>:
	<td class="table_cell">
	<c:set var="count" value="0"/>
	<table border="0" cellpadding="0" cellspacing="0"   >
	  <c:forEach var="group" items="${groups}">
	  <tr valign="top">
	   <td><c:out value="${group.name}"/>:</td>
	   <td><div>
	       <select name="studyGroupId<c:out value="${count}"/>" class="formfieldM">
	    	 <option value="0">--</option>

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
	    	  <span class="indicates_required_field">*</span>
	    	</c:if></div>
	    	<c:import url="../showMessage.jsp"><c:param name="key" value="studyGroupId${count}" /></c:import>
	    	
	    	</tr>
	    	<tr valign="top">
	    	<td><fmt:message key="notes" bundle="${resword}"/>:</td>
	    	<td>
	    	<div class="formfieldXL_BG"><input onfocus="this.select()" type="text" class="formfieldXL" name="notes<c:out value="${count}"/>"  value="<c:out value="${group.groupNotes}"/>"></div>
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


<input type="submit" name="submitEvent" value="<fmt:message key="save_and_assign_study_event" bundle="${restext}"/>" class="button_long">
<input type="submit" name="submitEnroll" value="<fmt:message key="save_and_add_next_subject" bundle="${restext}"/>" class="button_long">
<input type="submit" name="submitDone" value="<fmt:message key="save_and_finish" bundle="${restext}"/>" class="button_long">
<input type="button" onclick="confirmCancel('ListStudySubjects');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>

</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;z-index:10;background-color:white;layer-background-color:white;"></DIV>
<br><br>
<jsp:include page="../include/footer.jsp"/>
