<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="displayItem" class="org.akaza.openclinica.bean.submit.DisplayItemBean" />
<jsp:useBean scope='request' id='formMessages' class='java.util.HashMap'/>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/> 

<c:set var="interviewer" value="${toc.eventCRF.interviewerName}" />
<c:set var="interviewDate" value="${toc.eventCRF.dateInterviewed}" />
<c:set var="itemId" value="${displayItem.item.id}" />
<%--<c:set var="inputVal" value="input${itemId}" />--%>


<c:forEach var="presetValue" items="${presetValues}">
  <c:if test='${presetValue.key == "interviewer"}'>
    <c:set var="interviewer" value="${presetValue.value}" />
  </c:if>
  <c:if test='${presetValue.key == "interviewDate"}'>
    <c:set var="interviewDate" value="${presetValue.value}" />
  </c:if>
</c:forEach>
<!-- End of Alert Box -->
<table border="0" cellpadding="0" cellspacing="0">
<tr id="CRF_infobox_closed"  style="display: none;">
  <td style="padding-top: 3px; padding-left: 6px; width: 90px;" nowrap>
    <a href="javascript:leftnavExpand('CRF_infobox_closed'); leftnavExpand('CRF_infobox_open');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

    <b><fmt:message key="CRF_info" bundle="${resword}"/></b>
		</td>
</tr>
<tr id="CRF_infobox_open">

<td>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
  <td valign="bottom">
    <table border="0" cellpadding="0" cellspacing="0" width="100">
      <tr>
        <td nowrap>
          <div class="tab_BG_h"><div class="tab_R_h" style="padding-right: 0px;"><div class="tab_L_h" style="padding: 3px 11px 0px 6px; text-align: left;">
            <a href="javascript:leftnavExpand('CRF_infobox_closed'); leftnavExpand('CRF_infobox_open');"><img src="images/sidebar_collapse.gif" border="0" align="right"></a>

            <b><fmt:message key="CRF_info" bundle="${resword}"/></b>
						</div></div></div>
        </td>
      </tr>
    </table>
  </td>
</tr>
<tr>

<td valign="top">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">

				<table border="0" cellpadding="0" cellspacing="0" width="450">
					<tr>
						<td colspan="4" class="table_header_row_left">
						<b><a
							href="ViewCRF?crfId=<c:out value="${toc.crf.id}"/>"> <c:out
							value="${toc.crf.name}" /> <c:out value="${toc.crfVersion.name}" />
						</a> <c:choose>
							<c:when test="${eventCRF.stage.initialDE}">
								<img src="images/icon_InitialDE.gif" alt="<fmt:message key="initial_data_entry" bundle="${resword}"/>"
									title="<fmt:message key="initial_data_entry" bundle="${resword}"/>">
							</c:when>
							<c:when
								test="${eventCRF.stage.initialDE_Complete}">
								<img src="images/icon_InitialDEcomplete.gif"
									alt="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>"
									title="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>">
							</c:when>
							<c:when test="${eventCRF.stage.doubleDE}">
								<img src="images/icon_DDE.gif" alt="<fmt:message key="double_data_entry" bundle="${resword}"/>"
									title="<fmt:message key="double_data_entry" bundle="${resword}"/>">
							</c:when>
							<c:when test="${eventCRF.stage.doubleDE_Complete}">
								<img src="images/icon_DEcomplete.gif" alt="<fmt:message key="data_entry_complete" bundle="${resword}"/>"
									title="<fmt:message key="data_entry_complete" bundle="${resword}"/>">
							</c:when>
							<c:when test="${eventCRF.stage.administrativeEditing}">
								<img src="images/icon_AdminEdit.gif"
									alt="<fmt:message key="administrative_editing" bundle="${resword}"/>" title="<fmt:message key="administrative_editing" bundle="${resword}"/>">
							</c:when>
							<c:when test="${eventCRF.stage.locked}">
								<img src="images/icon_Locked.gif" alt="<fmt:message key="locked" bundle="${resword}"/>" title="<fmt:message key="locked" bundle="${resword}"/>">
							</c:when>
							<c:when test="${eventCRF.stage.invalid}">
								<img src="images/icon_Invalid.gif" alt="<fmt:message key="invalid" bundle="${resword}"/>" title="<fmt:message key="invalid" bundle="${resword}"/>">
							</c:when>
							<c:otherwise>
								<!-- leave blank -->
							</c:otherwise>
						</c:choose></b>

  </td>
</tr>
<tr>
  <td class="table_cell_left" style="color: #789EC5">
<b><fmt:message key="study_subject_ID" bundle="${resword}"/></b><br />
  </td>
  <td class="table_cell_left" style="color: #789EC5">
    <c:out value="${studySubject.label}" /><br />
  </td>
  <c:choose>
    <c:when test="${study.studyParameterConfig.personIdShownOnCRF == 'true'}">
      <td class="table_cell" style="color: #789EC5">
        <b><fmt:message key="person_ID" bundle="${resword}"/>:</b><br />
      </td>
      <td class="table_cell_left" style="color: #789EC5">
        <c:out value="${subject.uniqueIdentifier}" /><br />
      </td>

    </c:when>
    <c:otherwise>
      <td class="table_cell" style="color: #789EC5"><b>Person ID:</b></td>
      <td class="table_cell_left" style="color: #789EC5">N/A</td>
    </c:otherwise>
  </c:choose>
</tr>

					<tr>
  					<td class="table_cell_noborder" style="color: #789EC5">

					<b><fmt:message key="study_site" bundle="${resword}"/>:</b><br>
					  	</td>
						<td class="table_cell_noborder" style="color: #789EC5">
						<c:choose>
							<c:when test="${study.parentStudyId>0}">
								<a href="ViewSite?id=<c:out value="${study.id}"/>">
							</c:when>
							<c:otherwise>
								<a href="ViewStudy?id=<c:out value="${study.id}"/>&viewFull=yes">
							</c:otherwise>
						</c:choose> <c:out value="${studyTitle}" /></a><br>
					  	</td>
						<td class="table_cell_top" style="color: #789EC5">
						
						<b><fmt:message key="age_at_enrollment" bundle="${resword}"/>:</b><br>
						
						</td>
						<td class="table_cell_noborder" style="color: #789EC5">
							<c:out value="${age}" /><br>
					  	</td>
					</tr>
					
					<tr>
						<td class="table_cell_noborder" style="color: #789EC5">
							<b><fmt:message key="event" bundle="${resword}"/>:</b>
					  	</td>
						<td class="table_cell_noborder" style="color: #789EC5">
							<a
							href="EnterDataForStudyEvent?eventId=<c:out value="${toc.studyEvent.id}"/>"><c:out
							value="${toc.studyEventDefinition.name}" /></a>(<fmt:formatDate
							value="${toc.studyEvent.dateStarted}" dateStyle="short" />)
					  	</td>

  <td class="table_cell_top" style="color: #789EC5">
	<b><fmt:message key="date_of_birth" bundle="${resword}"/>:</b><br />
  </td>
  
  <%-- BWP>>5/24/07 : moved Gender in span tag to next to DOB --%>
  
  <td class="table_cell_noborder" style="color: #789EC5">
    <fmt:formatDate value="${subject.dateOfBirth}" dateStyle="short" /> 
    <span style="padding-left:6px;padding-right:6px"><b>Gender:</b></span>  <c:choose>
    <c:when test="${subject.gender==109}">M</c:when>
    <c:when test="${subject.gender==102}">F</c:when>
    <c:otherwise>
      <c:out value="${subject.gender}" />
    </c:otherwise>
  </c:choose><br />
  </td>
  
  <td class="table_cell_noborder" style="color: #789EC5">
    <!--<b>Gender:</b>-->
  </td>
  
  <td class="table_cell_noborder" style="color: #789EC5;padding-left:3px">
    <%--<c:choose>
      <c:when test="${subject.gender==109}">M</c:when>
      <c:when test="${subject.gender==102}">F</c:when>
      <c:otherwise>
        <c:out value="${subject.gender}" />
      </c:otherwise>
    </c:choose>--%>
  </td>
</tr>
<%--<tr>
  <td class="table_cell_noborder" style="color: #789EC5">

  </td>
  <td class="table_cell_noborder" style="color: #789EC5">
  </td>
  <td class="table_cell_top" style="color: #789EC5">
    <b><fmt:message key="gender" bundle="${resword}"/>:</b>
  </td>
  <td class="table_cell_noborder" style="color: #789EC5">
    <c:choose>
      <c:when test="${subject.gender==109}">M</c:when>
      <c:when test="${subject.gender==102}">F</c:when>
      <c:otherwise>
        <c:out value="${subject.gender}" />
      </c:otherwise>
    </c:choose>
  </td>
</tr>--%>
<%-- find out whether the item is involved with an error message, and if so, outline the
form element in red <c:out value="FORMMESSAGES: ${formMessages} "/><br/>--%>


<c:forEach var="frmMsg" items="${formMessages}">
  <c:if test="${frmMsg.key eq 'interviewer'}">
    <c:set var="isInError_Int" value="${true}" />
  </c:if>
  <c:if test="${frmMsg.key eq 'interviewDate'}">
    <c:set var="isInError_Dat" value="${true}" />
  </c:if>
</c:forEach>

<tr>
<td class="table_cell_left" nowrap>
  <c:choose>
    <c:when test="${isInError_Int}">
      <fmt:message key="interviewer_name" bundle="${resword}"/>: <span class="aka_exclaim_error">! </span> &nbsp;
    </c:when>
    <c:otherwise>
      <fmt:message key="interviewer_name" bundle="${resword}"/>: *&nbsp;
    </c:otherwise>
  </c:choose>
</td>
<td class="table_cell_left">
  <table border="0" cellpadding="0" cellspacing="0">
    <tr>

      <td valign="top">
        <!--  formfieldM_BG-->
        <c:choose>
        <c:when
          test="${study.studyParameterConfig.interviewerNameEditable=='true'}">
        <c:choose>
        <c:when test="${isInError_Int}">
        <div class="aka_input_error">
          <label for="interviewer"></label><input id="interviewer" type="text" name="interviewer" size="15"
                                                  value="<c:out value="${interviewer}" />" class="aka_input_error">
          </c:when>
          <c:otherwise>
          <div class=" formfieldM_BG">
            <input type="text" name="interviewer" size="15"
                   value="<c:out value="${interviewer}" />" class="formfieldM">
            </c:otherwise>
            </c:choose>
            </c:when>
            <c:otherwise>
            <div class=" formfieldM_BG">
              <input type="text" disabled size="15"
                     value="<c:out value="${interviewer}" />" class="formfieldM">
              <input type="hidden" name="interviewer"
                     value="<c:out value="${interviewer}" />">
              </c:otherwise>
              </c:choose></div>
            <%--BWP>>new error message design:  <jsp:include page="../showMessage.jsp">
              <jsp:param name="key" value="interviewer" />
            </jsp:include>--%>
      </td>
      <td valign="top" nowrap>
        <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
          <a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewer&column=interviewer_name','spanAlert-interviewer'); return false;">
            <img name="flag_interviewer" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note">
          </a>
        </c:if>
      </td>
    </tr>
    <tr>
      <td valign="top">
        <span ID="spanAlert-interviewer" class="alert"></span>
      </td>
    </tr>
  </table>
</td>

<td class="table_cell" nowrap>
  <c:choose>
    <c:when test="${isInError_Dat}">
      <fmt:message key="interview_date" bundle="${resword}"/>: <span class="aka_exclaim_error">! </span>&nbsp;<br />
      <%--(<fmt:message key="date_format" bundle="${resformat}"/>)--%>
    </c:when>
    <c:otherwise>
      <fmt:message key="interview_date" bundle="${resword}"/>: *&nbsp;<br />
      <%--(<fmt:message key="date_format" bundle="${resformat}"/>)--%>
    </c:otherwise>
  </c:choose>
</td><!--</a>-->
<td class="table_cell_left">
  <table border="0" cellpadding="0" cellspacing="0">

    <tr>
      <%----%>
      <td valign="top">
          <c:choose>
            <c:when
              test="${study.studyParameterConfig.interviewDateEditable=='true'}">
              <c:choose>
                <c:when test="${isInError_Dat}">
                  <div class="aka_input_error">
                  <label for="interviewDate"></label>
                  <input id="interviewDate" type="text" name="interviewDate" size="15"
                         value="<c:out value="${interviewDate}" />" class="aka_input_error">
                </c:when>
                <c:otherwise>
                 <div class="formfieldM_BG">
                  <input type="text" name="interviewDate" size="15"
                         value="<c:out value="${interviewDate}" />" class="formfieldM">
                </c:otherwise>
              </c:choose>
            </c:when>
            <c:otherwise>
              <div class="formfieldM_BG">
              <input type="text" disabled size="15"
                     value="<c:out value="${interviewDate}" />" class="formfieldM">
              <input type="hidden" name="interviewDate"
                     value="<c:out value="${interviewDate}" />">
            </c:otherwise>
          </c:choose></div>
        <%-- BWP>>new error message design: <jsp:include page="../showMessage.jsp">
          <jsp:param name="key" value="interviewDate" />
        </jsp:include>--%>
      </td>
      <%--        document.getElementById('testdiv1').style.top=(parseInt(document.getElementById('testdiv1').style.top) - 10)+'px'; --%>
      <td valign="top" nowrap><a href="#" onClick=
        "cal1.select(document.forms[0].interviewDate,'anchor1','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" title="cal1.select(document.forms[0].interviewDate,'anchor1','<fmt:message key="date_format_string" bundle="${resformat}"/>'); return false;" name="anchor1" id="anchor1"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a>
        <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
          <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewDate&column=date_interviewed','spanAlert-interviewDate'); return false;">
            <img name="flag_interviewDate" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
        </c:if>
      </td>
    </tr>
    <tr>
      <td valign="top">
        <span ID="spanAlert-interviewDate" class="alert"></span>
      </td>
    </tr>
  </table>

</td>
</tr>
</table>

</div>

</div></div></div></div></div></div></div>


</td>
</tr>

</table>

</td>
</tr>
</table>
