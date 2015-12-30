<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

<c:import url="../include/managestudy-header.jsp"/>


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

<jsp:useBean scope='request' id='jobName' class='java.lang.String'/>
<jsp:useBean scope='request' id='jobDesc' class='java.lang.String'/>

<jsp:useBean scope='request' id='periodToRun' class='java.lang.String'/>
<jsp:useBean scope='request' id='tab' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc12' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc13' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc13oc' class='java.lang.String'/>
<jsp:useBean scope='request' id='spss' class='java.lang.String'/>
<jsp:useBean scope='request' id='contactEmail' class='java.lang.String'/>

<h1><span class="title_manage"><fmt:message key="create_scheduled_job_export_dataset" bundle="${resword}"/></span></h1>


<form action="CreateJobExport" method="post" class="in-button">
<input type="hidden" name="action" value="confirmall" />

<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
<jsp:useBean id="now" class="java.util.Date" />
<P><I><fmt:message key="note_that_job_is_set" bundle="${resword}"/> <fmt:formatDate value="${now}" pattern="${dtetmeFormat}"/>.</I></P>
<fmt:message key="field_required" bundle="${resword}"/>
<table border="0" cellpadding="0" cellspacing="0"   class="shaded_table table_first_column_w30 ">
   	<tr>
<td class="formlabel"><fmt:message key="job_name" bundle="${resword}"/>:</td>
<td ><div>
	<input type="text" name="jobName" size="30" value="<c:out value="${jobName}"/>" class="formfieldL required" />
	<span class="indicates_required_field">*</span>
</div>
<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobName"/></jsp:include>
	</td> 	
</tr>
	<tr>
		<td class="formlabel"><fmt:message key="description" bundle="${resword}"/>:
		</td>
		<td  ><div><input type="text" name="jobDesc" size="60" value="<c:out value="${jobDesc}"/>" class="formfieldXL required"/><span class="indicates_required_field">*</span>
		</div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobDesc"/></jsp:include>
		</td> 	
	</tr>
	<tr>
		<td class="formlabel"><fmt:message key="please_pick_a_dataset_to_export" bundle="${resword}"/>:</td>
		<td  ><select name="dsId" class="formfieldXL ">
			<c:forEach var="dataset" items="${datasets}">
				<option value="<c:out value="${dataset.id}"/>"
					<c:if test="${dsId == dataset.id}">
						selected
					</c:if>
				><c:out value="${dataset.name}" /></option>
			</c:forEach>
		</select></td>
	</tr>
	<tr>
		<td class="formlabel"><fmt:message key="period_to_run" bundle="${resword}"/>:</td>
		<td><div><input type="radio" name="periodToRun" value="daily" <c:if test="${periodToRun == 'daily'}">checked</c:if>/><fmt:message key="daily" bundle="${resword}"/>
		<br><input type="radio" name="periodToRun" value="weekly"<c:if test="${periodToRun == 'weekly'}">checked</c:if>/><fmt:message key="weekly" bundle="${resword}"/>
		<br><input type="radio" name="periodToRun" value="monthly"	<c:if test="${periodToRun == 'monthly'}">checked</c:if>	/>
		<fmt:message key="monthly" bundle="${resword}"/>
		</div> <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="periodToRun"/></jsp:include>
		</td>
	</tr>
	<tr>
		<td class="formlabel"><fmt:message key="start_date_time" bundle="${resword}"/>:</td>
		<td class="text">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<c:import url="../include/showDateTimeInput.jsp"><c:param name="prefix" value="job"/><c:param name="count" value="1"/></c:import>
				<td nonwrap>(<fmt:message key="date_time_format" bundle="${resformat}"/>) </td>
			</tr>
			<tr>
				<td colspan="7">
				<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobDate"/></jsp:include>
				</td>
			</tr>
			</table>
		
		</td>
	</tr>

	<tr>
		<td class="formlabel"><fmt:message key="file_formats" bundle="${resword}"/>:	</td>
		<td class="text"><div>
			<c:forEach var="extract" items="${extractProperties}" varStatus="loopCounter">
				<br><input type="radio" name="formatId" value="<c:out value="${extract.id}"/>"
					<c:if test="${formatId == extract.id}">	checked		</c:if>	/>
				
			       <c:choose>
                        <c:when test="${fn:startsWith(extract.filedescription, '&')==true}">
                            <fmt:message key="${fn:substringAfter(extract.filedescription, '&')}" bundle="${restext}"/>
                        </c:when>
                        <c:otherwise>
                            <c:out value="${extract.filedescription}"/>
                        </c:otherwise>
                    </c:choose>
				
				<c:if test="${loopCounter.count == 1}">
					<span class="indicates_required_field">*</span>
				</c:if>
            </c:forEach>
            </div>
            <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="formatId"/></jsp:include>
			
		</td>
		
	</tr>



	<tr>
		<td class="formlabel"><fmt:message key="contact_email" bundle="${resword}"/>:</td>
		<td class="text"><div><input type="text" name="contactEmail" size="60" value="<c:out value="${contactEmail}"/>"/>
		<span class="indicates_required_field">*</span></div>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="contactEmail"/></jsp:include>
		</td>		
	</tr>
	</table>
	
	<table>
	<tr>
		<td align="left">
<input type="submit" name="btnSubmit" value="<fmt:message key="confirm_and_save" bundle="${resword}"/>" class="button_xlong"/>
</form></td><td>
<form action='ViewJob' method="POST" class="in-button">
<input type="submit" name="submit" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium">
</form>
</td></tr></table>
<br><br>
	


<jsp:include page="../include/footer.jsp"/>
