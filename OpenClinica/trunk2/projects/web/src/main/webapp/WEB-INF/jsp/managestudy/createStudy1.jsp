<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.admin" var="resadmin"/>

  <c:import url="../include/managestudy-header.jsp"/>

<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
        <fmt:message key="enter_the_study_and_protocol" bundle="${resword}"/>
		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='newStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope="request" id="facRecruitStatusMap" class="java.util.HashMap"/>


<h1><span class="title_manage"><fmt:message key="create_a_new_study" bundle="${resword}"/></span></h1>

<p><div class="note_not_bold"><fmt:message key="ClinicalTrials.gov" bundle="${restext}"/></div></p>
<h3><fmt:message key="study_description" bundle="${resword}"/></h3>
<P><span class="indicates_required_field">* </span><fmt:message key="indicates_required_field" bundle="${resword}"/></P>
<form action="CreateStudy" method="post">
<input type="hidden" name="action" value="next">
<input type="hidden" name="pageNum" value="1">

<table border="0" cellpadding="0" width="700"   class="shaded_table table_first_column_w30 ">
  <tr valign="top"><td class="formlabel" width="150px"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId'); return false;"><fmt:message key="unique_protocol_ID" bundle="${resword}"/>:</a></td><td>
  <div><input type="text" name="uniqueProId" value="<c:out value="${newStudy.identifier}"/>" class="formfieldXL required"><span class="indicates_required_field">*</span></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueProId"/></jsp:include></td></tr>

  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#BriefTitle" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#BriefTitle'); return false;"><fmt:message key="brief_title" bundle="${resword}"/></a>:</td><td>
  <div><input type="text" name="name" value="<c:out value="${newStudy.name}"/>" class="formfieldXL required"><span class="indicates_required_field">*</span></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td></tr>

  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#OfficialTitle" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#OfficialTitle'); return false;"><fmt:message key="official_title" bundle="${resword}"/></a>:</td><td>
   <input type="text" name="officialTitle" value="<c:out value="${newStudy.officialTitle}"/>" class="formfieldXL">
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="officialTitle"/></jsp:include>
  </td></tr>

  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#SecondaryIds" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#SecondaryIds'); return false;"><fmt:message key="secondary_IDs" bundle="${resword}"/>:</a><br>(<fmt:message key="separate_by_commas" bundle="${resword}"/>)</td>
  <td><div class="formtextareaXL4_BG">
   <textarea class="formtextareaXL4" name="secondProId" rows="4" cols="50"><c:out value="${newStudy.secondaryIdentifier}"/></textarea></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondProId"/></jsp:include>
  </td></tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="principal_investigator" bundle="${resword}"/>:</td><td>
  <div><input type="text" name="prinInvestigator" value="<c:out value="${newStudy.principalInvestigator}"/>" class="formfieldXL required"><span class="indicates_required_field">*</span></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="prinInvestigator"/></jsp:include></td></tr>

  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#StudyType" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#StudyType'); return false;"><fmt:message key="protocol_type" bundle="${resword}"/></a>:</td><td>
   <c:set var="type1" value="observational"/>
   <c:choose>
    <c:when test="${newStudy.protocolTypeKey == type1}">
      <input type="radio" name="protocolType" value="interventional"><fmt:message key="interventional" bundle="${resword}"/>
      <input type="radio" checked name="protocolType" value="observational"><fmt:message key="observational" bundle="${resadmin}"/>
    </c:when>
    <c:otherwise>
      <input type="radio" checked name="protocolType" value="interventional"><fmt:message key="interventional" bundle="${resword}"/>
      <input type="radio" name="protocolType" value="observational"><fmt:message key="observational" bundle="${resadmin}"/>
    </c:otherwise>
   </c:choose>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="protocolType"/></jsp:include><span class="indicates_required_field">*</span></td></tr>

 <tr valign="top" ><td class="formlabel" width="150px"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#BriefSummary" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#BriefSummary'); return false;"><fmt:message key="brief_summary" bundle="${resword}"/></a>:</td><td><div class="formtextareaXL4_BG">
 <div> <textarea class="formtextareaXL4 required" name="description" rows="4" cols="50"><c:out value="${newStudy.summary}"/></textarea><span class="indicates_required_field">*</span></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include></td></tr>

   <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#DetailedDescription" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#DetailedDescription'); return false;"><fmt:message key="detailed_description" bundle="${resword}"/></a>:</td><td>
   <div class="formtextareaXL4_BG"><textarea class="formtextareaXL4" name="protocolDescription" rows="4" cols="50"><c:out value="${newStudy.protocolDescription}"/></textarea></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="protocolDescription"/></jsp:include>
  </td></tr>

   <tr valign="top"><td class="formlabel" width="150px"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#LeadSponsor" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#LeadSponsor'); return false;"><fmt:message key="sponsor" bundle="${resword}"/></a>:</td><td>
 <div> <input type="text" name="sponsor" value="<c:out value="${newStudy.sponsor}"/>" class="formfieldXL required"><span class="indicates_required_field">*</span></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="sponsor"/></jsp:include></td></tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="collaborators" bundle="${resword}"/>:<br>(<fmt:message key="separate_by_commas" bundle="${resword}"/>)</td><td>
  <div class="formtextareaXL4_BG">
  <textarea class="formtextareaXL4" name="collaborators" rows="4" cols="50"><c:out value="${newStudy.collaborators}"/></textarea></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="collaborators"/></jsp:include>
  </td></tr>

 </table>
  <br>  
 <table border="0" cellpadding="0" width="700"   class="shaded_table table_first_column_w30 ">

        <tr><fmt:message key="select_user_for_edit_update" bundle="${restext}"/> </tr>

        <br>
        <br>
        <tr valign="top"><td class="formlabel"><fmt:message key="Select_User" bundle="${restext}"/> :</td><td>
         <div class="formfieldL_BG">
          <select name="selectedUser" class="formfieldXL">1
          <option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
          <c:forEach var="user" items="${users}">
              <option value="<c:out value="${user.id}"/>"> <c:out value="${user.name}"/>
                  (<c:out value="${user.firstName}"/> <c:out value="${user.lastName}"/>)
              </option>
           </c:forEach>
         </select></div>
         </td></tr>

    </table>
    

 <input type="submit" name="Save" value="<fmt:message key="save" bundle="${resword}"/>" class="button_medium">
<input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" onClick="javascript:myCancel('<fmt:message key="sure_to_cancel" bundle="${resword}"/>');"/>


</form>
<br><br>



<jsp:include page="../include/footer.jsp"/>
