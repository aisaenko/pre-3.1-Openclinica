<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="displayItem" class="org.akaza.openclinica.bean.submit.DisplayItemBean" />

<c:set var="inputType" value="${displayItem.metadata.responseSet.responseType.name}" />
<c:set var="itemId" value="${displayItem.item.id}" />
<c:set var="numOfDate" value="${param.key}" />

<%-- for tab index. must start from 1, not 0--%>
<c:set var="tabNum" value="${param.tabNum+1}" />

<c:choose>
	<c:when test="${displayItem.skipped}"><c:set var="disabled" value='disabled="disabled"' /></c:when>
	<c:otherwise><c:set var="disabled" value="" /></c:otherwise>
</c:choose>

<c:if test='${inputType == "text"}'>
	<input tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" name="input<c:out value="${itemId}" />" value="<c:out value="${displayItem.metadata.responseSet.value}"/>"  <c:out value="${disabled}" escapeXml="false"/>/>
	<c:if test="${displayItem.item.itemDataTypeId==9}"><!-- date type-->
	 <A HREF="#" onClick="cal1.select(document.forms[0].input<c:out value="${itemId}"/>,'anchor<c:out value="${itemId}"/>','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.forms[0].input<c:out value="${itemId}"/>,'anchor<c:out value="${itemId}"/>','MM/dd/yyyy'); return false;" NAME="anchor<c:out value="${itemId}"/>" ID="anchor<c:out value="${itemId}"/>"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a>(MM/DD/YYYY) 
	 <c:set var="numOfDate" value="${numOfDate+1}"/>
	</c:if>
</c:if>
<c:if test='${inputType == "textarea"}'>
	<textarea tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" name="input<c:out value="${itemId}" />" rows="5" cols="40" <c:out value="${disabled}" escapeXml="false"/> ><c:out value="${displayItem.metadata.responseSet.value}"/></textarea>
</c:if>
<c:if test='${inputType == "checkbox"}'>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
		<c:choose>
			<c:when test="${option.selected}"><c:set var="checked" value="checked" /></c:when>
			<c:otherwise><c:set var="checked" value="" /></c:otherwise>
		</c:choose>
		<input tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="checkbox" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> <c:out value="${disabled}" escapeXml="false"/> /> <c:out value="${option.text}" /> <br/>
	</c:forEach>
</c:if>
<c:if test='${inputType == "radio"}'>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
		<c:choose>
			<c:when test="${option.selected}"><c:set var="checked" value="checked" /></c:when>
			<c:otherwise><c:set var="checked" value="" /></c:otherwise>
		</c:choose>
		<input tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="radio" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> <c:out value="${disabled}" escapeXml="false"/>  /> <c:out value="${option.text}" /> <br/>
	</c:forEach>
</c:if>
<c:if test='${inputType == "single-select"}'>
	<select tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" name="input<c:out value="${itemId}"/>" class="formfield" <c:out value="${disabled}" escapeXml="false"/> >
		<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
			<c:choose>
				<c:when test="${option.selected}"><c:set var="checked" value="selected" /></c:when>
				<c:otherwise><c:set var="checked" value="" /></c:otherwise>
			</c:choose>
			<option value="<c:out value="${option.value}" />" <c:out value="${checked}"/> ><c:out value="${option.text}" /></option>
		</c:forEach>
	</select>
</c:if>
<c:if test='${inputType == "multi-select"}'>
	<select multiple  tabindex="<c:out value="${tabNum}"/>" name="input<c:out value="${itemId}"/>" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" <c:out value="${disabled}" escapeXml="false"/> >
		<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
			<c:choose>
				<c:when test="${option.selected}"><c:set var="checked" value="selected" /></c:when>
				<c:otherwise><c:set var="checked" value="" /></c:otherwise>
			</c:choose>
			<option value="<c:out value="${option.value}" />" <c:out value="${checked}"/> ><c:out value="${option.text}" /></option>
		</c:forEach>
	</select>
</c:if>
<c:if test="${displayItem.metadata.required}">
<td valign="top"><span class="alert">*</span></td>
</c:if>
<c:if test='${inputType == "calculation"}'>
	<input type="hidden" name="input<c:out value="${itemId}"/>" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" /><input type="text" disabled="disabled" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
</c:if>
<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
	<c:choose>
		<c:when test="${displayItem.numDiscrepancyNotes > 0}">
			<c:set var="imageFileName" value="icon_Note" />
		</c:when>
		<c:otherwise>
			<c:set var="imageFileName" value="icon_noNote" />
		</c:otherwise>
	</c:choose>
	<td valign="top"><a tabindex="<c:out value="${tabNum + 1000}"/>" href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=input<c:out value="${itemId}" />&column=value','spanAlert-input<c:out value="${itemId}"/>'); return false;"
		><img name="flag_input<c:out value="${itemId}" />" src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"
		></a></td>
</c:if>
