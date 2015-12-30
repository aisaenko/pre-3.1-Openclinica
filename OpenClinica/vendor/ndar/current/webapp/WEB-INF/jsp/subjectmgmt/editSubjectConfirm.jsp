<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/subjectMgmt-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />
<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>


<c:set var="uniqueIdentifier" value="" />
<c:set var="givenname" value="" />
<c:set var="surname" value="" />
<c:set var="streetAddress" value="" />
<c:set var="localityName" value="" />
<c:set var="stateOrProvinceName" value="" />
<c:set var="postalCode" value="" />
<c:set var="telephoneNumber" value="" />
<c:set var="emailAddress" value="" />
<c:set var="dob" value="" />
<c:set var="yob" value="" />
<c:set var="motherId" value="" />
<c:set var="motherName" value="" />
<c:set var="fatherId" value="" />
<c:set var="fatherName" value="" />
<c:set var="gender" value="" />
<c:set var="subjectEntryLabelId" value="" />
<c:set var="subjectEntryLabelName" value="" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "uniqueIdentifier"}'>
		<c:set var="uniqueIdentifier" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "givenname"}'>
		<c:set var="givenname" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "surname"}'>
		<c:set var="surname" value="${presetValue.value}" />
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
	<c:if test='${presetValue.key == "yob"}'>
		<c:set var="yob" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "motherId"}'>
		<c:set var="motherId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "motherName"}'>
		<c:set var="motherName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "fatherId"}'>
		<c:set var="fatherId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "fatherName"}'>
		<c:set var="fatherName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "subjectEntryLabelId"}'>
		<c:set var="subjectEntryLabelId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "subjectEntryLabelName"}'>
		<c:set var="subjectEntryLabelName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "stepNum"}'>
		<c:set var="stepNum" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<h1><span class="title_Admin">
View Subject Details
</span></h1>

<form action="EditSubject" method="post">
<jsp:include page="../include/showSubmitted.jsp" />

<input type="hidden" name="uniqueIdentifier" value='<c:out value="${uniqueIdentifier}"/>'/>
<input type="hidden" name="givenname" value='<c:out value="${givenname}"/>'/>
<input type="hidden" name="surname" value='<c:out value="${surname}"/>'/>
<input type="hidden" name="streetAddress" value='<c:out value="${streetAddress}"/>'/>
<input type="hidden" name="localityName" value='<c:out value="${localityName}"/>'/>
<input type="hidden" name="stateOrProvinceName" value='<c:out value="${stateOrProvinceName}"/>'/>
<input type="hidden" name="postalCode" value='<c:out value="${postalCode}"/>'/>
<input type="hidden" name="telephoneNumber" value='<c:out value="${telephoneNumber}"/>'/>
<input type="hidden" name="emailAddress" value='<c:out value="${emailAddress}"/>'/>
	<c:choose>
	<c:when test="${subject.dobCollected == true}">
<input type="hidden" name="dob" value='<c:out value="${dob}"/>'/>
	</c:when>
	<c:otherwise>
<input type="hidden" name="yob" value='<c:out value="${yob}"/>'/>
  </c:otherwise>	
 </c:choose>
<input type="hidden" name="gender" value='<c:out value="${gender}"/>'/>
<input type="hidden" name="subjectEntryLabelId" value='<c:out value="${subjectEntryLabelId}"/>'/>
<input type="hidden" name="stepNum" value='<c:out value="${stepNum}"/>'/>
<input type="hidden" name="motherId" value='<c:out value="${motherId}"/>'/>
<input type="hidden" name="fatherId" value='<c:out value="${fatherId}"/>'/>

<div style="width: 400px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="table_header_column_top">First Name:</td>
		<td class="table_cell_top"><c:out value="${surname}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Last Name:</td>
		<td class="table_cell"><c:out value="${givenname}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Street Address:</td>
		<td class="table_cell"><c:out value="${streetAddress}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">City:</td>
		<td class="table_cell"><c:out value="${localityName}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">State:</td>
		<td class="table_cell"><c:out value="${stateOrProvinceName}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Postal Code / ZIP Code:</td>
		<td class="table_cell"><c:out value="${postalCode}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Telephone:</td>
		<td class="table_cell"><c:out value="${telephoneNumber}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Email:</td>
		<td class="table_cell"><c:out value="${emailAddress}" />&nbsp;</td>
	</tr>
	<tr>
		<td class="table_header_column">Gender:</td>
		<td class="table_cell"><c:out value="${gender}"/></td>
	</tr>
	<c:choose>
	<c:when test="${subject.dobCollected == true}">
  	<tr valign="top">
  		<td class="table_header_column">Date Of Birth:</td>
  		<td class="table_cell"><c:out value="${dob}"/></td>
	</tr>
	</c:when>
	<c:otherwise>
  	<tr valign="top">
  		<td class="table_header_column">Year Of Birth:</td>
  		<td class="table_cell"><c:out value="${yob}"/></td>
	</tr>
  </c:otherwise>	
 </c:choose>
  	<tr valign="top">
  		<td class="table_header_column">Subject Entry Label:</td>
  		<td class="table_cell"><c:out value="${subjectEntryLabelName}"/></td>
  	</tr> 
  	<tr valign="top">
  		<td class="table_header_column">Mother:</td>
  		<td class="table_cell"><c:out value="${motherName}"/></td>
  	</tr> 
  	<tr valign="top">
  		<td class="table_header_column">Father:</td>
  		<td class="table_cell"><c:out value="${fatherName}"/></td>
  	</tr> 
</table>
	</div>

	</div></div></div></div></div></div></div></div>

	</div>
<br>

<input type="submit" name="submit" value="Back" class="button_long">
<input type="submit" name="submit" value="Confirm" class="button_long">

</form>
<jsp:include page="../include/footer.jsp"/>