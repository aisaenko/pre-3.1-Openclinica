<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<jsp:include page="../include/extract-header.jsp"/>
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

<jsp:include page="../include/createDatasetSideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='months' class='java.util.ArrayList' />
<jsp:useBean scope='request' id='years' class='java.util.ArrayList' />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<c:set var="firstMonth" value="${0}" />
<c:set var="firstYear" value="${0}" />
<c:set var="lastMonth" value="${0}" />
<c:set var="lastYear" value="${0}" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "firstmonth"}'>
		<c:set var="firstMonth" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "firstyear"}'>
		<c:set var="firstYear" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "lastmonth"}'>
		<c:set var="lastMonth" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "lastyear"}'>
		<c:set var="lastYear" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<h1><span class="title_extract">Create Datasets: Define Temporal Scope <a href="javascript:openDocWindow('help/3_2_createDataset_Help.html#step2')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>

<jsp:include page="createDatasetBoxes.jsp" flush="true">
<jsp:param name="longitudinalScope" value="1"/>
</jsp:include>

<p>Filter your data by subject enrollement date by selecting a date range below.</p>

<p>Select the event start and end dates below. To specify data from the beginning of the year, you may leave the month field blank. However, if you specify a month, you must also specify the year.</p>

<p>If you do not wish to filter study event data by date of subject enrollment, you may leave the date fields blank and continue.</p>

<form action="CreateDataset" method="post">
<input type="hidden" name="action" value="scopesubmit"/>

<%--<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="firstmonth"/></jsp:include>--%>

<table>
	<tr>
		<td class="text">Beginning Date:</td>
		<td class="text" valign="top">
			<select name="firstmonth">
				<option value="0">-Select-</option>
			<c:set var="monthNum" value="${1}" />
			<c:forEach var="month" items="${months}">
				<c:choose>
					<c:when test="${firstMonth == monthNum}">
						<option value='<c:out value="${monthNum}" />' selected><c:out value="${month}" /></option>
					</c:when>
					<c:otherwise>
						<option value='<c:out value="${monthNum}" />'><c:out value="${month}" /></option>
					</c:otherwise>
				</c:choose>
				<c:set var="monthNum" value="${monthNum + 1}" />
			</c:forEach>
			</select>
			<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="firstmonth"/></jsp:include>
		</td>
		<td class="text" valign="top">
			<select name="firstyear">
				<option value="1900">-Select-</option>
			<c:set var="yearNum" value="${1980}" />
			<c:forEach var="year" items="${years}">
				<c:choose>
					<c:when test="${firstYear == yearNum}">
						<option value='<c:out value="${year}" />' selected><c:out value="${year}" /></option>
					</c:when>
					<c:otherwise>
						<option value='<c:out value="${year}" />'><c:out value="${year}" /></option>
					</c:otherwise>
				</c:choose>
				<c:set var="yearNum" value="${yearNum + 1}" />
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="text" align="right">End Date:</td>
		<td class="text" valign="top">
			<select name="lastmonth">
				<option value="0">-Select-</option>
			<c:set var="monthNum" value="${1}" />
			<c:forEach var="month" items="${months}">
				<c:choose>
					<c:when test="${lastMonth == monthNum}">
						<option value='<c:out value="${monthNum}" />' selected><c:out value="${month}" /></option>
					</c:when>
					<c:otherwise>
						<option value='<c:out value="${monthNum}" />'><c:out value="${month}" /></option>
					</c:otherwise>
				</c:choose>
				<c:set var="monthNum" value="${monthNum + 1}" />
			</c:forEach>
			</select>
			<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="lastmonth"/></jsp:include>
		</td>
		<td class="text" valign="top">
			<select name="lastyear">
				<option value="2100">-Select-</option>
			<c:set var="yearNum" value="${1980}" />
			<c:forEach var="year" items="${years}">
				<c:choose>
					<c:when test="${lastYear == yearNum}">
						<option value='<c:out value="${year}" />' selected><c:out value="${year}" /></option>
					</c:when>
					<c:otherwise>
						<option value='<c:out value="${year}" />'><c:out value="${year}" /></option>
					</c:otherwise>
				</c:choose>
				<c:set var="yearNum" value="${yearNum + 1}" />
			</c:forEach>
			</select>
		</td>
	</tr>
	
</table>
<p>Save and Export your dataset now or Apply a Filter to specify additional selection criteria based on values of parameters in CRFs.</p>
<table>
  <tr>
		<td>
			<!--<input type="submit" name="submit" value="Continue to Apply Filter" class="button_xlong"/>&nbsp;&nbsp;-->
			<!--<input type="submit" name="submit" value="Skip Apply Filter and Save" class="button_xlong"/>		-->
			<input type="submit" name="submit" value="Save and Continue" class="button_xlong"/>
		</td>
	</tr>
</table>
</form>

<c:import url="../include/workflow.jsp">
   <c:param name="module" value="extract"/> 
</c:import>
<jsp:include page="../include/footer.jsp"/>