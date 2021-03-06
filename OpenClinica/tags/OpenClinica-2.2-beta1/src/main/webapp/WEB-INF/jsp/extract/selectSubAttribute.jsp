<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>


<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="eventlist" class="java.util.HashMap"/>

<h1><span class="title_extract"><fmt:message key="create_dataset" bundle="${resword}"/>: <fmt:message key="select_subject_attributes" bundle="${resword}"/></span></h1>

<P><jsp:include page="../showInfo.jsp"/></P>

<jsp:include page="createDatasetBoxes.jsp" flush="true">
<jsp:param name="selectStudyEvents" value="1"/>
</jsp:include>

<P><jsp:include page="../showMessage.jsp"/></P>

<p><fmt:message key="please_select_one_CRF_from_the" bundle="${restext}"/><fmt:message key="left_side_info_panel" bundle="${restext}"/><fmt:message key="select_items_in_CRF_include_dataset" bundle="${restext}"/>
</p>
<p><fmt:message key="click_event_subject_attributes_specify" bundle="${restext}"/></p>


<form action="CreateDataset" method="post" name="cl">
<input type="hidden" name="action" value="beginsubmit"/>
<input type="hidden" name="crfId" value="0">
<input type="hidden" name="subAttr" value="1">

 
   <p>
   <c:choose>
     <c:when test="${newDataset.showSubjectDob}">
       <input type="checkbox" checked name="dob" value="yes">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="dob" value="yes">
     </c:otherwise>
   </c:choose>
   
   <c:choose>
    <c:when test="${study.studyParameterConfig.collectDob == '1'}">
     <fmt:message key="date_of_birth" bundle="${resword}"/>
    </c:when>
    <c:when test="${study.studyParameterConfig.collectDob == '3'}">
     <fmt:message key="date_of_birth" bundle="${resword}"/>
    </c:when>
    <c:otherwise>
     <fmt:message key="year_of_birth" bundle="${resword}"/>
    </c:otherwise> 
   </c:choose>
   </p>
   <p>
   <c:choose>
     <c:when test="${newDataset.showSubjectGender}">
       <input type="checkbox" checked name="gender" value="yes">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="gender" value="yes">
     </c:otherwise>
   </c:choose>   
   <fmt:message key="gender" bundle="${resword}"/>
   </p>
   <!-- below added 07/09/2007, tbh -->
   <p>
   <c:choose>
     <c:when test="${newDataset.showSubjectStatus}">
       <input type="checkbox" checked name="subj_status" value="yes">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="subj_status" value="yes">
     </c:otherwise>
   </c:choose>   
   Status
   </p>
   <p>
   <c:choose>
     <c:when test="${newDataset.showSubjectUniqueIdentifier}">
       <input type="checkbox" checked name="unique_identifier" value="yes">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="unique_identifier" value="yes">
     </c:otherwise>
   </c:choose>   
   Secondary ID
   </p>
   <p>
   <c:choose>
     <c:when test="${newDataset.showSubjectAgeAtEvent}">
       <input type="checkbox" checked name="age_at_event" value="yes">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="age_at_event" value="yes">
     </c:otherwise>
   </c:choose>   
   Age at Event
   </p>
  
<table border="0" cellpadding="0" cellspacing="0" >
  <tr>
   <td><input type="submit" name="save" value="<fmt:message key="save_and_add_more_items" bundle="${resword}"/>" class="button_xlong"/></td>
   <td><input type="submit" name="saveContinue" value="<fmt:message key="save_and_define_scope" bundle="${resword}"/>" class="button_xlong"/></td>
  </tr>
</table>
</form>

<jsp:include page="../include/footer.jsp"/>
