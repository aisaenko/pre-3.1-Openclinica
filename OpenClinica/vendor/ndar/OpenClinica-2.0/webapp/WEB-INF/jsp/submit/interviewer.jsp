<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<c:set var="interviewer" value="" />
<c:set var="interviewDate" value="" />

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

		<b>CRF Info</b>
		</td>
	</tr>
	<tr id="CRF_infobox_open" style="display: all">

		<td>
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="bottom">
				<table border="0" cellpadding="0" cellspacing="0" width="100">
					<tr>
						<td nowrap>
						<div class="tab_BG_h"><div class="tab_R_h" style="padding-right: 0px;"><div class="tab_L_h" style="padding: 3px 11px 0px 6px; text-align: left;">
						<a href="javascript:leftnavExpand('CRF_infobox_closed'); leftnavExpand('CRF_infobox_open');"><img src="images/sidebar_collapse.gif" border="0" align="right"></a>

						<b>CRF Info</b>
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
							<c:when test="${eventCRF.stage.name=='initial data entry'}">
								<img src="images/icon_InitialDE.gif" alt="Initial Data Entry"
									title="Initial Data Entry">
							</c:when>
							<c:when
								test="${eventCRF.stage.name=='initial data entry complete'}">
								<img src="images/icon_InitialDEcomplete.gif"
									alt="Initial Data Entry Complete"
									title="Initial Data Entry Complete">
							</c:when>
							<c:when test="${eventCRF.stage.name=='double data entry'}">
								<img src="images/icon_DDE.gif" alt="Double Data Entry"
									title="Double Data Entry">
							</c:when>
							<c:when test="${eventCRF.stage.name=='data entry complete'}">
								<img src="images/icon_DEcomplete.gif" alt="Data Entry Complete"
									title="Data Entry Complete">
							</c:when>
							<c:when test="${eventCRF.stage.name=='administrative editing'}">
								<img src="images/icon_AdminEdit.gif"
									alt="Administrative Editing" title="Administrative Editing">
							</c:when>
							<c:when test="${eventCRF.stage.name=='locked'}">
								<img src="images/icon_Locked.gif" alt="Locked" title="Locked">
							</c:when>
							<c:otherwise>
								<img src="images/icon_Invalid.gif" alt="Invalid" title="Invalid">
							</c:otherwise>
						</c:choose></b>

						</td>
					</tr>
					<tr>
						<td class="table_cell_left" style="color: #789EC5">
<b>Study Subject ID</b><br>
					  	</td>
						<td class="table_cell_left" style="color: #789EC5">
<c:out value="${studySubject.label}" /><br>
					  	</td>

						<td class="table_cell" style="color: #789EC5">
<b>Person ID:</b><br>
						</td>
						<td class="table_cell_left" style="color: #789EC5">
<c:out value="${subject.uniqueIdentifier}" /><br>
					  	</td>
					</tr>
					<tr>
						<td class="table_cell_noborder" style="color: #789EC5">

<b>Study/Site:</b><br>
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
<b>Age:</b><br>
						</td>
						<td class="table_cell_noborder" style="color: #789EC5">

<c:out value="${age}" /><br>
					  	</td>
					</tr>
					<tr>
						<td class="table_cell_noborder" style="color: #789EC5">
<b>Event:</b>
					  	</td>
						<td class="table_cell_noborder" style="color: #789EC5">
<a
							href="EnterDataForStudyEvent?eventId=<c:out value="${toc.studyEvent.id}"/>"><c:out
							value="${toc.studyEventDefinition.name}" /></a>(<fmt:formatDate
							value="${toc.studyEvent.dateStarted}" pattern="MM/dd/yyyy" />)
					  	</td>

						<td class="table_cell_top" style="color: #789EC5">
<b>Date of Birth:</b><br>
						</td>
						<td class="table_cell_noborder" style="color: #789EC5">
<fmt:formatDate value="${subject.dateOfBirth}"
							pattern="MM/dd/yyyy" /><br>
					  	</td>
					</tr>
					<tr>
						<td class="table_cell_noborder" style="color: #789EC5">

					  	</td>
						<td class="table_cell_noborder" style="color: #789EC5">
					  	</td>
						<td class="table_cell_top" style="color: #789EC5">
<b>Gender:</b>
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
					</tr>

	<tr>
		<td class="table_cell_left" nowrap>Interviewer Name: *&nbsp;</td>
		<td class="table_cell_left">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>

					<td valign="top">
					<div class="formfieldM_BG"><c:choose>
									<c:when
										test="${study.studyParameterConfig.interviewerNameEditable=='true'}">
										<input type="text" name="interviewer" size="15"
											value="<c:out value="${interviewer}" />" class="formfieldM">
									</c:when>
									<c:otherwise>
										<input type="text" disabled size="15"
											value="<c:out value="${interviewer}" />" class="formfieldM">
										<input type="hidden" name="interviewer"
											value="<c:out value="${interviewer}" />">
									</c:otherwise>
								</c:choose></div>
								<jsp:include page="../showMessage.jsp">
									<jsp:param name="key" value="interviewer" />
								</jsp:include>
					</td>
					<td valign="top" nowrap>
					<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
					<a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewer&column=interviewer_name','spanAlert-interviewer'); return false;">
					<img name="interviewDate" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a>
					</c:if>				
					</tr>
				<tr>
					<td valign="top">



<span ID="spanAlert-interviewer" class="alert"></span>
</td>
				</tr>
			</table>
	  	</td>

		<td class="table_cell" nowrap>Interview Date: *&nbsp;<br>(MM/DD/YYYY)</a>
		<td class="table_cell_left">
			<table border="0" cellpadding="0" cellspacing="0">

				<tr>

					<td valign="top">
					<div class="formfieldM_BG"><c:choose>
									<c:when
										test="${study.studyParameterConfig.interviewDateEditable=='true'}">
										<input type="text" name="interviewDate" size="15"
											value="<c:out value="${interviewDate}" />" class="formfieldM">
									</c:when>
									<c:otherwise>
										<input type="text" disabled size="15"
											value="<c:out value="${interviewDate}" />" class="formfieldM">
										<input type="hidden" name="interviewDate"
											value="<c:out value="${interviewDate}" />">
									</c:otherwise>
								</c:choose></div>
								<jsp:include page="../showMessage.jsp">
									<jsp:param name="key" value="interviewDate" />
								</jsp:include>
					</td>
					<td valign="top" nowrap><a href="#" onClick="cal1.select(document.forms[0].interviewDate,'anchor1','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.forms[0].interviewDate,'anchor1','MM/dd/yyyy'); return false;" NAME="anchor1" ID="anchor1"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a>
					<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
					<a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewDate&column=date_interviewed','spanAlert-interviewDate'); return false;">
					<img name="interviewDate" src="images/icon_noNote.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"></a>
					</c:if>
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