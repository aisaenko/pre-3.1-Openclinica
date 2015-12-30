<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/admin-header.jsp"/>
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



<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='presetValues' class='java.util.HashMap' />
<jsp:useBean scope='request' id='userTypes' class='java.util.ArrayList' />

<c:set var="firstName" value="" />
<c:set var="lastName" value="" />
<c:set var="email" value="" />
<c:set var="institutionalAffiliation" value="" />
<c:set var="userTypeId" value="${0}" />
<c:set var="resetPassword" value="${0}" />
<c:set var="displayPwd" value="no" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "firstName"}'>
		<c:set var="firstName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "lastName"}'>
		<c:set var="lastName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "email"}'>
		<c:set var="email" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "institutionalAffiliation"}'>
		<c:set var="institutionalAffiliation" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "userType"}'>
		<c:set var="userTypeId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "resetPassword"}'>
		<c:set var="resetPassword" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "displayPwd"}'>
		<c:set var="displayPwd" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<h1><span class="title_Admin">Edit a User Account</span></h1>


<form action="EditUserAccount" method="post">
<jsp:include page="../include/showSubmitted.jsp" />
<jsp:include page="../include/showHiddenInput.jsp"><jsp:param name="fieldName" value="userId" /></jsp:include>
<jsp:include page="../include/showHiddenInput.jsp"><jsp:param name="fieldName" value="stepNum" /></jsp:include>

<div style="width: 400px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="table_header_column_top">First Name:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="firstName" value="<c:out value="${firstName}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="firstName" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr valign="top">
		<td class="table_header_column">Last Name:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="lastName" value="<c:out value="${lastName}"/>" size="20" class="formfieldM"/>
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="lastName" /></jsp:include>
				</tr>
			</table>
		</td>
	</tr>


	<tr valign="top">
		<td class="table_header_column">Email:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="email" value="<c:out value="${email}"/>" size="20" class="formfieldM"/>
					</div></td>
					<td>(username@institution) *</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="email" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	
	
	<tr valign="top">
		<td class="table_header_column">Institutional Affiliation:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" name="institutionalAffiliation" value="<c:out value="${institutionalAffiliation}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="institutionalAffiliation" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr valign="top">
	  	<td class="table_header_column">User Type:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<select name="userType" class="formfieldM">
							<c:forEach var="type" items="${userTypes}">
								<c:choose>
									<c:when test="${userTypeId == type.id}">
										<option value='<c:out value="${type.id}" />' selected><c:out value="${type.name}" /></option>
									</c:when>
									<c:otherwise>
										<option value='<c:out value="${type.id}" />'><c:out value="${type.name}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="userType" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<SCRIPT LANGUAGE="JavaScript">
	 function a() {
	   if (document.forms[0].resetPassword.checked){
	     document.forms[0].displayPwd[0].disabled=false
	     document.forms[0].displayPwd[1].disabled=false
	   } else {
	     document.forms[0].displayPwd[0].disabled=true
	     document.forms[0].displayPwd[1].disabled=true
	   }
	 }
	</SCRIPT>
	<tr>
		<td colspan=2 class="table_header_column">
			<input type="checkbox" name="resetPassword" value="1" 
					<c:if test="${resetPassword != 0}">
						checked
					</c:if>
			onclick="javascript:a()">
			Reset password
		</td>
	</tr>
    <tr>
	<td colspan="2"> 
	  	<c:choose>
         <c:when test="${displayPwd == 'no'}">            
            <input type="radio" checked name="displayPwd" value="no" disabled="true">Send User Password via Email
            <input type="radio" name="displayPwd" value="yes" disabled="true" >Show User Password to Admin
         </c:when>
         <c:otherwise>
            <input type="radio" name="displayPwd" value="no" disabled="true">Send User Password via Email
            <input type="radio" checked name="displayPwd" value="yes" disabled="true">Show User Password to Admin
           
         </c:otherwise>
       </c:choose>
      </td>
	</tr>
	</table>
	</div>

	</div></div></div></div></div></div></div></div>

	</div>

<input type="submit" name="Submit" value="Next" class="button_medium" /><br/>
</form>

<c:import url="../include/workflow.jsp">
 <c:param name="module" value="admin"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>