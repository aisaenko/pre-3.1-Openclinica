<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/submit-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
			Fill out the form to add a subject to the study. Click on the help icon for assistance.
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

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


<h1><span class="title_submit">
<c:out value="${study.name}" />: Add Subject <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step1')"><img src="images/bt_Help_Submit.gif" border="0" alt="Help" title="Help"></a>
</span></h1>
<!--
<c:if test="${study.genetic && (!empty mothers) || (!empty fathers)}">
<p class="text">Indicate the subject's parents, if applicable.
</c:if>
-->
<p class="text">
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">  
    var cal1 = new CalendarPopup("testdiv1");
  </SCRIPT>
<br/>* indicates required field.</P>
<form action="AddNewSubject" method="post">
<jsp:include page="../include/showSubmitted.jsp" />

<div style="width: 550px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="5"> 
	<tr valign="top">
		<td class="formlabel">Study Subject ID:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldXL_BG">
					<c:choose>
					 <c:when test="${study.studyParameterConfig.subjectIdGeneration =='auto non-editable'}">
					  <input type="text" value="<c:out value="${label}"/>" size="50" class="formfieldXL" disabled>
					  <input type="hidden" name="label" value="<c:out value="${label}"/>">
					 </c:when>
					 <c:otherwise>
					   <input type="text" name="label" value="<c:out value="${label}"/>" size="50" class="formfieldXL">
					 </c:otherwise>
					</c:choose>
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="label"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<c:choose>
	<c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='required'}">
	<tr valign="top">
	  	<td class="formlabel">Person ID:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldXL_BG">
						<input type="text" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>" size="50" class="formfieldXL">
					</div></td>
					<td>* <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=uniqueIdentifier&column=unique_identifier','spanAlert-uniqueIdentifier'); return false;">
					<img name="flag_uniqueIdentifier" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a></c:if></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueIdentifier"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	</c:when>
	<c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='optional'}">
	<tr valign="top">
	  	<td class="formlabel">Person ID:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldXL_BG">
						<input type="text" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>" size="50" class="formfieldXL">
					</div></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueIdentifier"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	</c:when>
	<c:otherwise>	  
	  <input type="hidden" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>">
	</c:otherwise>
	</c:choose>
	
	<tr valign="top">
	  	<td class="formlabel">Secondary Identifier:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldXL_BG">
						<input type="text" name="secondaryLabel" value="<c:out value="${secondaryLabel}"/>" size="50" class="formfieldXL">
					</div></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondaryLabel"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Date of Enrollment for Study ' <c:out value="${study.name}" /> ' :</td>
	  	<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="enrollmentDate" size="15" value="<c:out value="${enrollmentDate}" />" class="formfieldM" />
					</td>
					<td>
					<A HREF="#" onClick="cal1.select(document.forms[0].enrollmentDate,'anchor1','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.forms[0].enrollmentDate,'anchor1','MM/dd/yyyy'); return false;" NAME="anchor1" ID="anchor1"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a>(MM/DD/YYYY) * 
					<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate'); return false;">
					<img name="flag_enrollmentDate" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a></c:if></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="enrollmentDate"/></jsp:include></td>
				</tr>
			</table>
	  	</td>
	</tr>
	
	<c:choose>
	<c:when test="${study.studyParameterConfig.genderRequired =='required'}">
	<tr valign="top">
		<td class="formlabel">Gender:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldS_BG">
						<select name="gender" class="formfieldS">
							<option value="">-Select-</option>
							<c:choose>
								<c:when test="${!empty chosenGender}">
									<c:choose>
										<c:when test='${chosenGender == "m"}'>
											<option value="m" selected>male</option>
											<option value="f">female</option>
										</c:when>
										<c:otherwise>
											<option value="m">male</option>
											<option value="f" selected>female</option>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<option value="m">male</option>
									<option value="f">female</option>				
								</c:otherwise>
							</c:choose>
						</select>
					</td>
					<td>* <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
					<a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=gender&column=gender','spanAlert-gender'); return false;">
					<img name="flag_gender" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a>
					</c:if>
					</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="gender"/></jsp:include></td>
				</tr>
		
		 	
			</table>
		</td>
	</tr>
	</c:when>
	<c:otherwise>
		    <input type="hidden" name="gender" value="">
	</c:otherwise>
	</c:choose> 
			
	<c:choose>
	<c:when test="${study.studyParameterConfig.collectDob == '1'}">
	<tr valign="top">
		<td class="formlabel">Date of Birth:</td>
	  	<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="dob" size="15" value="<c:out value="${dob}" />" class="formfieldM" />
					</td>
					<td>(MM/DD/YYYY) * <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=dob&column=date_of_birth','spanAlert-dob'); return false;">
					<img name="flag_dob" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a></c:if></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dob"/></jsp:include></td>
				</tr>
			</table>
	  	</td>
	</tr>
	</c:when>
	<c:when test="${study.studyParameterConfig.collectDob == '2'}">
	<tr valign="top">
		<td class="formlabel">Year of Birth:</td>
	  	<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="yob" size="15" value="<c:out value="${yob}" />" class="formfieldM" />
					</td>
					<td>(YYYY) *<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=yob&column=date_of_birth','spanAlert-yob'); return false;">
					<img name="flag_yob" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a></c:if></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="yob"/></jsp:include></td>
				</tr>
			</table>
	  	</td>
	</tr>
  </c:when>	
  <c:otherwise>
    <input type="hidden" name="dob" value="" />
  </c:otherwise>
 </c:choose>
 
<c:if test="${study.genetic}">
	<c:if test="${!empty fathers}">
	<tr>
		<td class="formlabel">Father:
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldS_BG">
						<select name="father" class="formfieldS">
							<option value="">-Select-</option>
							<c:forEach var="father" items="${fathers}">
								<c:choose>
									<c:when test="${fatherId == father.subject.id}">
										<option value="<c:out value="${father.subject.id}" />" selected>
										<c:choose>
										 <c:when test="${father.subject.name!=null && father.subject.name!=''}">
										  <c:out value="${father.subject.name}"/>
										 </c:when>
										 <c:otherwise>
										   <c:out value="${father.studySubjectIds}"/>
										 </c:otherwise>
										</c:choose>
										</option>
									</c:when>
									<c:otherwise>
										<option value="<c:out value="${father.subject.id}" />">
										  <c:choose>
										 <c:when test="${father.subject.name!=null && father.subject.name!=''}">
										  <c:out value="${father.subject.name}"/>
										 </c:when>
										 <c:otherwise>
										   <c:out value="${father.studySubjectIds}"/>
										 </c:otherwise>
										</c:choose>
										</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</div></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="father"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	</c:if>

	<c:if test="${(!empty mothers)}">
	<tr>
		<td class="formlabel">Mother:
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldS_BG">
						<select name="mother" class="formfieldS">
							<option value="">-Select-</option>
							<c:forEach var="mother" items="${mothers}">
								<c:choose>
									<c:when test="${motherId == mother.subject.id}">
										<option value="<c:out value="${mother.subject.id}" />" selected>
										 <c:choose>
										 <c:when test="${mother.subject.name!=null && mother.subject.name!=''}">
										  <c:out value="${mother.subject.name}"/>
										 </c:when>
										 <c:otherwise>
										   <c:out value="${mother.studySubjectIds}"/>
										 </c:otherwise>
										</c:choose>
										</option>
									</c:when>
									<c:otherwise>
										<option value="<c:out value="${mother.subject.id}" />">
										<c:choose>
										 <c:when test="${mother.subject.name==null || mother.subject.name==''}">
										   <c:out value="${mother.studySubjectIds}"/>
										 </c:when>
										 <c:otherwise>										  
										   <c:out value="${mother.subject.name}"/>
										 </c:otherwise>
										</c:choose>
										</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</td>
					<td></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mother"/></jsp:include></td>
				</tr>
			</table>	
		</td>
	</tr>
	</c:if>
</c:if>

</table>
</div>

</div></div></div></div></div></div></div></div>

</div>

<c:if test="${(!empty groups)}">
<br>
<div style="width: 550px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0"> 

  <tr valign="top">
	<td class="formlabel">Subject Group Class:
	<td class="table_cell">
	<c:set var="count" value="0"/>
	<table border="0" cellpadding="0">
	  <c:forEach var="group" items="${groups}">
	  <tr valign="top">
	   <td><b><c:out value="${group.name}"/></b></td>
	   <td><div class="formfieldM_BG"> 
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
	    	</select></div>	  
	    	<c:import url="../showMessage.jsp"><c:param name="key" value="studyGroupId${count}" /></c:import>  	
	    	
	    	</td>
	    	<c:if test="${group.subjectAssignment=='Required'}">
	    	  <td align="left">*</td>	    	  
	    	</c:if>
	    	</tr>
	    	<tr valign="top">
	    	<td>Notes:</td>
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
</div>

</div></div></div></div></div></div></div></div>

</div>
</c:if>
<br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
<input type="submit" name="submitEvent" value="Save and Assign Study Event" class="button_long">
</td>
<td><input type="submit" name="submitEnroll" value="Save and Add Next Subject" class="button_long"></td>
<td><input type="submit" name="submitDone" value="Save and Finish" class="button_long"></td>
</tr>
</table>
</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>

<br>

<c:import url="instructionsSetupStudyEvent.jsp">
	<c:param name="currStep" value="enroll" />
</c:import>

<jsp:include page="../include/footer.jsp"/>