<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="displayItem" class="org.akaza.openclinica.bean.submit.DisplayItemBean" />

<c:set var="inputType" value="${displayItem.metadata.responseSet.responseType.name}" />
<c:set var="itemId" value="${displayItem.item.id}" />

<c:if test='${inputType == "text" || inputType == "calculation"}'>
	<c:out value="${displayItem.metadata.responseSet.value}"/>
</c:if>
<c:if test='${inputType == "textarea"}'>
	<textarea name="input<c:out value="${itemId}" />" rows="5" cols="40" disabled style="background:white;color:#4D4D4D;"><c:out value="${displayItem.metadata.responseSet.value}"/></textarea>
</c:if>
<c:if test='${inputType == "checkbox"}'>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
		<c:choose>
			<c:when test="${option.selected}"><c:set var="checked" value="checked" /></c:when>
			<c:otherwise><c:set var="checked" value="" /></c:otherwise>
		</c:choose>
		<input type="checkbox" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> disabled /> <c:out value="${option.text}" /> <br/>
	</c:forEach>
</c:if>
<c:if test='${inputType == "radio" || inputType == "single-select"}'>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
		<c:choose>
			<c:when test="${option.selected}"><c:set var="checked" value="checked" /></c:when>
			<c:otherwise><c:set var="checked" value="" /></c:otherwise>
		</c:choose>
		<input type="radio" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> disabled /> <c:out value="${option.text}" /> <br/>
	</c:forEach>
</c:if>
<c:if test='${inputType == "multi-select"}'>
	<select multiple name="input<c:out value="${itemId}"/>" disabled style="background:white;color:#4D4D4D;">
		<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
			<c:choose>
				<c:when test="${option.selected}"><c:set var="checked" value="selected" /></c:when>
				<c:otherwise><c:set var="checked" value="" /></c:otherwise>
			</c:choose>
			<option value="<c:out value="${option.value}" />" <c:out value="${checked}"/> ><c:out value="${option.text}" /></option>
		</c:forEach>
	</select>
</c:if>