<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='statuses' class='java.util.ArrayList' />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<c:set var="fName" value="" />
<c:set var="fDesc" value="" />
<c:set var="fStatusId" value="${1}" />

<%--
	set the values here, tbh
--%>

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "fName"}'>
		<c:set var="fName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "fDesc"}'>
		<c:set var="fDesc" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "fStatusId"}'>
		<c:set var="fStatusId" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<h1><span class="title_extract">Create Filter: Specify Filter Properties <a href="javascript:openDocWindow('help/3_4_createFilter_Help.html#step5')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>
<P><jsp:include page="../include/showPageMessages.jsp"/></P>
<jsp:include page="createFilterBoxes.jsp">
	<jsp:param name="save" value="1"/>
</jsp:include>
<c:if test='${newExp != null}'>
        <P>Generated Filter: <c:forEach var='str' items='${newExp}'>
        						<c:out value='${str}'/>
        					 </c:forEach>
        </P>
</c:if>
<P>Please enter all the filter metadata in the fields below.  
<font color="red">All fields are required.</font></P>
<form action="CreateFiltersThree">

<input type="hidden" name="action" value="validate"/>
<table>
<tr>
<td><b>Name:</b></td>
	<td>
	<input type="text" name="fName" size="30" value="<c:out value='${fName}' />"/>
	</td>
</tr>
<tr>
<td><b>Description:</b></td>
	<td>
	<textarea name="fDesc" cols="40" rows="4"><c:out value="${fDesc}" /></textarea>
	</td>
</tr>
<tr>
<td><b>Status:</b></td>
	<td>
	<select name="fStatusId">
		<option value="0">-- Select Status --</option>
			<c:forEach var="status" items="${statuses}">
				<c:choose>
					<c:when test="${fStatusId == status.id}">
						<option value="<c:out value='${status.id}' />" selected><c:out value="${status.name}" /></option>
					</c:when>
					<c:otherwise>
						<option value="<c:out value='${status.id}' />"><c:out value="${status.name}" /></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
	</select>
	</td>
</tr>
</table>


<P><input type="submit" value="Save Filter" class="button_xlong"/>

</form>

<jsp:include page="../include/footer.jsp"/>