<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


 
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/> 


<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
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

<h1><span class="title_manage">
<fmt:message key="update_study_subject_details" bundle="${resword}"/>
</span></h1>
 <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">  
  <%=org.akaza.openclinica.i18n.util.HtmlUtils.getCalendarPopupCode("cal1","testdiv1")%>
  </SCRIPT>

<form action="UpdateStudySubject" method="post">
<input type="hidden" name="action" value="confirm">
<input type="hidden" name="id" value="<c:out value="${studySub.id}"/>">
 <div style="width: 550px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0">
  <tr valign="top">
  <td class="formlabel"><fmt:message key="study_subject_ID" bundle="${resword}"/>:</td>
  <td><div class="formfieldXL_BG"><input type="text" name="label" value="<c:out value="${studySub.label}"/>" class="formfieldXL"></div>
  <br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="label"/></jsp:include></td><td><fmt:message key="field_required" bundle="${resword}"/></td>
  </tr>
  <tr valign="top">
  <td class="formlabel"><fmt:message key="secondary_label" bundle="${resword}"/>:</td><td><div class="formfieldXL_BG"><input type="text" name="secondaryLabel" value="<c:out value="${studySub.secondaryLabel}"/>" class="formfieldXL"></div>
  </td>
  </tr>
  <tr valign="top">
  <td class="formlabel"><fmt:message key="enrollment_date" bundle="${resword}"/>:</td>
  <td>
  <div class="formfieldXL_BG">
  <%-- 
  <c:set var="datestring" value='<fmt:message key="date_format_string" bundle="${resformat}"/>' scope="page"/>
  <input type="text" name="enrollmentDate" value="<fmt:formatDate value="${studySub.enrollmentDate}" dateStyle="short"/>" class="formfieldXL"></div>
  --%>
  <input type="text" name="enrollmentDate" value="<c:out value="${enrollDateStr}" />" class="formfieldXL"></div> 
  <br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="enrollmentDate"/></jsp:include></td>
  
  <td valign="top">
  <A HREF="#" onClick="cal1.select(document.forms[0].enrollmentDate,'anchor3','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" TITLE="cal1.select(document.forms[0].enrollmentDate,'anchor3','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" NAME="anchor3" ID="anchor3"><img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" /></a>
   (<fmt:message key="date_format" bundle="${resformat}"/>)
  <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?id=<c:out value="${studySub.id}"/>&name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate'); return false;"><img name="flag_enrollmentDate" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
  </c:if>
  </td>
 
 </tr>
  <%--<tr valign="top"><td class="formlabel"><fmt:message key="created_by" bundle="${resword}"/>:</td><td><c:out value="${studySub.owner.name}"/></td></tr>
 
  <tr valign="top"><td class="formlabel"><fmt:message key="date_created" bundle="${resword}"/>:</td><td><fmt:formatDate value="${studySub.createdDate}" dateStyle="short"/>
 
  </td></tr>
 
  <tr valign="top"><td class="formlabel"><fmt:message key="last_updated_by" bundle="${resword}"/>:</td><td><c:out value="${studySub.updater.name}"/>&nbsp;
 
  </td></tr>
 
  <tr valign="top"><td class="formlabel"><fmt:message key="date_updated" bundle="${resword}"/>:</td><td><fmt:formatDate value="${studySub.updatedDate}" dateStyle="short"/>&nbsp;
 
  </td></tr>
  --%>
 
 
</table>
</div>
</div></div></div></div></div></div></div></div>

</div> 
<br>
<c:if test="${(!empty groups)}">
<br>
<div style="width: 550px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0"> 

  <tr valign="top">
	<td class="formlabel"><fmt:message key="subject_group_class" bundle="${resword}"/>:
	<td class="table_cell">
	<c:set var="count" value="0"/>
	<table border="0" cellpadding="0">
	  <c:forEach var="group" items="${groups}">
	  <tr valign="top">
	   <td><b><c:out value="${group.name}"/></b></td>
	   <td><div class="formfieldM_BG"> <select name="studyGroupId<c:out value="${count}"/>" class="formfieldM">
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
	    	</select></div>
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
</div>

</div></div></div></div></div></div></div></div>

</div>
</c:if>
<br>
 <input type="submit" name="Submit" value="<fmt:message key="confirm_changes" bundle="${resword}"/>" class="button_long">
</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
 
  <c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/> 
 </c:import>
<jsp:include page="../include/footer.jsp"/>
