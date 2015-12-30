<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/subjectMgmt-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope="request" id="pageMessages" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />
<jsp:useBean scope="request" id="roles" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="entryLabels" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="labelmap" class="org.akaza.openclinica.bean.subject.SubjectLabelMapBean"/>

<jsp:useBean scope="request" id="fathers" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="mothers" class="java.util.ArrayList" />

<c:set var="guid" value="" />
<c:set var="gender" value="" />
<c:set var="givennameAtBirth" value="" />
<c:set var="middlenameAtBirth" value="" />
<c:set var="surnameAtBirth" value="" />
<c:set var="givenname" value="" />
<c:set var="surname" value="" />
<c:set var="streetAddress" value="" />
<c:set var="localityName" value="" />
<c:set var="stateOrProvinceName" value="" />
<c:set var="postalCode" value="" />
<c:set var="telephoneNumber" value="" />
<c:set var="emailAddress" value="" />
<c:set var="dob" value="" />
<c:set var="cob" value="" />
<c:set var="surnameOfFather" value="" />
<c:set var="givennameOfFather" value="" />
<c:set var="dobOfFather" value="" />
<c:set var="surnameOfMother" value="" />
<c:set var="givennameOfMother" value="" />
<c:set var="dobOfMother" value="" />

<c:set var="fatherId" value="${0}" />
<c:set var="motherId" value="${0}" />
<c:set var="subjectEntryLabelId" value="" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "guid"}'>
		<c:set var="guid" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "givenname"}'>
		<c:set var="givenname" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "surname"}'>
		<c:set var="surname" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "givennameAtBirth"}'>
		<c:set var="givennameAtBirth" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "middlenameAtBirth"}'>
		<c:set var="middlenameAtBirth" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "surnameAtBirth"}'>
		<c:set var="surnameAtBirth" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "givennameOfMother"}'>
		<c:set var="givennameOfMother" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "surnameOfMother"}'>
		<c:set var="surnameOfMother" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "givennameOfFather"}'>
		<c:set var="givennameOfFather" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "surnameOfFather"}'>
		<c:set var="surnameOfFather" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "streetAddress"}'>
		<c:set var="streetAddress" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "localityName"}'>
		<c:set var="localityName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "stateOrProvinceName"}'>
		<c:set var="stateOrProvinceName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "postalCode"}'>
		<c:set var="postalCode" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "telephoneNumber"}'>
		<c:set var="telephoneNumber" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "emailAddress"}'>
		<c:set var="emailAddress" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "gender"}'>
		<c:set var="gender" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dob"}'>
		<c:set var="dob" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "cob"}'>
		<c:set var="cob" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dobOfFather"}'>
		<c:set var="dobOfFather" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dobOfMother"}'>
		<c:set var="dobOfMother" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "mother"}'>
		<c:set var="motherId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "father"}'>
		<c:set var="fatherId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "subjectEntryLabelId"}'>
		<c:set var="subjectEntryLabelId" value="${presetValue.value}" />
	</c:if>
</c:forEach>


<h1><span class="title_subjectmgmt">
Add Subject <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step1')"><img src="images/bt_Help_SubjectMgmt.gif" border="0" alt="Help" title="Help"></a>
</span></h1>

<c:import url="instructionsSetupStudyEvent.jsp">
	<c:param name="currStep" value="enroll" />
</c:import>

<jsp:include page="../include/alertbox.jsp" />

<c:if test="${(!empty mothers) || (!empty fathers)}">
<p class="text">Indicate the subject's parents, if applicable.
</c:if>
<SCRIPT LANGUAGE="JavaScript" type="text/javascript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1" type="text/javascript">  
    var cal1 = new CalendarPopup("testdiv1");
  </SCRIPT>
<br/>* indicates required field.</P>
<form action="AddNewPiiSubject" method="post">
<jsp:include page="../include/showSubmitted.jsp" />

<input type="hidden" name="guid" value='<c:out value="${guid}"/>'/>
<div style="width: 550px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="5"> 
	<tr valign="top">
		<td class="formlabel">Current First Name:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="givenname" value="<c:out value="${givenname}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="givenname"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Current Last Name:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="surname" value="<c:out value="${surname}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="surname"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">First Name At Birth:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="givennameAtBirth" value="<c:out value="${givennameAtBirth}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="givennameAtBirth"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Middle Name At Birth:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="middlenameAtBirth" value="<c:out value="${middlenameAtBirth}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="middlenameAtBirth"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Last Name At Birth:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="surnameAtBirth" value="<c:out value="${surnameAtBirth}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="surnameAtBirth"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Street Address:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="streetAddress" value="<c:out value="${streetAddress}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="streetAddress"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">City:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="localityName" value="<c:out value="${localityName}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="localityName"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">State or Province:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="stateOrProvinceName" value="<c:out value="${stateOrProvinceName}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="stateOrProvinceName"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Postal Code / ZIP Code:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="postalCode" value="<c:out value="${postalCode}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="postalCode"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Telephone:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="telephoneNumber" value="<c:out value="${telephoneNumber}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="telephoneNumber"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Email:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="emailAddress" value="<c:out value="${emailAddress}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="emailAddress"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Gender:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldS_BG">
						<select name="gender" class="formfieldS">
							<option value="">-Select-</option>
							<c:choose>
								<c:when test="${!empty gender}">
									<c:choose>
										<c:when test='${gender == "m"}'>
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
					<td>* <%-- <c:if test="${study.discrepancyManagement}">
					<a href="javascript:void(0);" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=gender&column=gender','spanAlert-gender'); return false;">
					<img name="flag_gender" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a>
					</c:if> --%>
					</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="gender"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel">Date of Birth:</td>
	  	<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="dob" size="15" value="<c:out value="${dob}" />" class="formfieldM" />
					</td>
					<td>(MM/DD/YYYY) * <%--<c:if test="${study.discrepancyManagement}"><a href="javascript:void(0);" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=dob&column=date_of_birth','spanAlert-dob'); return false;">
					<img name="flag_dob" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a></c:if>--%></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dob"/></jsp:include></td>
				</tr>
			</table>
	  	</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">City Of Birth:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="cob" value="<c:out value="${cob}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="cob"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Father's First Name:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="givennameOfFather" value="<c:out value="${givennameOfFather}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="givennameOfFather"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Father's Last Name:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="surnameOfFather" value="<c:out value="${surnameOfFather}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="surnameOfFather"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Father's Date of Birth:</td>
	  	<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="dobOfFather" size="15" value="<c:out value="${dobOfFather}" />" class="formfieldM" />
					</td>
					<td>(MM/DD/YYYY) <%--<c:if test="${study.discrepancyManagement}"><a href="javascript:void(0);" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=dob&column=date_of_birth','spanAlert-dob'); return false;">
					<img name="flag_dob" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a></c:if>--%></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dobOfFather"/></jsp:include></td>
				</tr>
			</table>
	  	</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Mother's First Name:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="givennameOfMother" value="<c:out value="${givennameOfMother}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="givennameOfMother"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Mother's Last Name:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
					  <input type="text" name="surnameOfMother" value="<c:out value="${surnameOfMother}"/>" size="50" class="formfieldXL">
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="surnameOfMother"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">Mother's Date of Birth:</td>
	  	<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="dobOfMother" size="15" value="<c:out value="${dobOfMother}" />" class="formfieldM" />
					</td>
					<td>(MM/DD/YYYY) <%--<c:if test="${study.discrepancyManagement}"><a href="javascript:void(0);" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=dob&column=date_of_birth','spanAlert-dob'); return false;">
					<img name="flag_dob" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a></c:if>--%></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dobOfMother"/></jsp:include></td>
				</tr>
			</table>
	  	</td>
	</tr>
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
	<tr valign="top">
		<td class="formlabel">Subject Entry Label:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top" colspan="2"><div class="formfieldXL_BG">
				       <select name="subjectEntryLabelId" class="formfieldXL">
				          <option value="0" />
				         <c:forEach var="anEntryLabel" items="${entryLabels}">    
				          <c:choose>
				           <c:when test="${subjectEntryLabelId == anEntryLabel.id}">   
				           <option value="<c:out value="${anEntryLabel.id}"/>" selected><c:out value="${anEntryLabel.name}"/>
				           </c:when>
				           <c:otherwise>
				             <option value="<c:out value="${anEntryLabel.id}"/>"><c:out value="${anEntryLabel.name}"/>      
				           </c:otherwise>
				          </c:choose> 
				         </c:forEach>
				       </select>
					</div></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="subjectEntryLabelId"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>

</table>
</div>

</div></div></div></div></div></div></div></div>

</div>


<br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td><input type="submit" name="submitEnroll" value="Save and Add Another" class="button_long"></td>
<td><input type="submit" name="submitDone" value="Save and Finish" class="button_long"></td>
</tr>
</table>
</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<jsp:include page="../include/footer.jsp"/>