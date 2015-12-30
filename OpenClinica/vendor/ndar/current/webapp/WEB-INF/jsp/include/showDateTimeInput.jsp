<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<c:set var="prefix" value="${param.prefix}" />
<c:set var="count" value="${param.count}" />
<c:set var="date" value="" />
<c:set var="hour" value="${12}" />
<c:set var="minute" value="${0}" />
<c:set var="half" value="am" />

<c:set var="dateFieldName" value='${prefix}Date' />
<c:set var="hourFieldName" value='${prefix}Hour' />
<c:set var="minuteFieldName" value='${prefix}Minute' />
<c:set var="halfFieldName" value='${prefix}Half' />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == dateFieldName}'>
		<c:set var="date" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == hourFieldName}'>
		<c:set var="hour" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == minuteFieldName}'>
		<c:set var="minute" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == halfFieldName}'>
		<c:set var="half" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<td valign="top">
<div class="formfieldS_BG">
	<input type="text" name="<c:out value="${dateFieldName}"/>" value="<c:out value="${date}" />" class="formfieldS" />
</div>
</td>
<td valign="top"><a href="javascript:void(0);" onClick="cal1.select(document.forms[0].<c:out value="${dateFieldName}"/>,'anchor<c:out value="${count}"/>','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.forms[0].<c:out value="${dateFieldName}"/>,'anchor<c:out value="${count}"/>','MM/dd/yyyy'); return false;" NAME="anchor<c:out value="${count}"/>" ID="anchor<c:out value="${count}"/>"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a></td>
<td valign="top">
<div class="formfieldXS_BG">
<select name="<c:out value="${hourFieldName}"/>" class="formfieldXS">
	<c:forEach var="currHour" begin="1" end="12" step="1">
		<c:choose>
			<c:when test="${hour == currHour}">
				<option value="<c:out value="${currHour}"/>" selected><c:out value="${currHour}"/></option>
			</c:when>
			<c:otherwise>
				<option value="<c:out value="${currHour}"/>"><c:out value="${currHour}"/></option>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</select>
</div>
</td>

<td class="formlabel">:</td>

<td valign="top">
<div class="formfieldXS_BG">
<select name="<c:out value="${minuteFieldName}"/>" class="formfieldXS">
	<c:forEach var="currMinute" begin="0" end="59" step="1">
		<c:choose>
			<c:when test="${minute == currMinute}">
				<option value="<c:out value="${currMinute}"/>" selected><c:out value="${currMinute}"/></option>
			</c:when>
			<c:otherwise>
				<option value="<c:out value="${currMinute}"/>"><c:out value="${currMinute}"/></option>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</select>
</div>
</td>

<td valign="top">
<div class="formfieldXS_BG">
<select name="<c:out value="${halfFieldName}"/>" class="formfieldXS">
	<c:choose>
		<c:when test='${half == "pm"}'>
			<option value="am">am</option>
			<option value="pm" selected>pm</option>
		</c:when>
		<c:otherwise>
			<option value="am" selected>am</option>
			<option value="pm">pm</option>		
		</c:otherwise>
	</c:choose>
</select>
</div>
</td>