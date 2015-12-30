<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="events" class="java.util.HashMap"/>

<h1><span class="title_extract">Create Filter: Specify Parameters <a href="javascript:openDocWindow('help/3_4_createFilter_Help.html#step1')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>
<P><jsp:include page="../include/showPageMessages.jsp"/></P>
<jsp:include page="createFilterBoxes.jsp">
	<jsp:param name="selectCrf" value="1"/>
</jsp:include>
<c:if test='${newExp != null}'>
        <P>Generated Filter: <c:forEach var='str' items='${newExp}'>
        						<c:out value='${str}'/>
        					 </c:forEach>
        </P>
</c:if>
<form action="CreateFiltersTwo" method="post" name="cf2">
<input type="hidden" name="action" value="crfselected"/>
&nbsp;<b>CRF</b>&nbsp;&nbsp;

<select name="crfId">
	<option value="0" selected>-- Select CRF --</option>
<c:forEach var='item' items='${events}'>
	
	<c:set var="crf_name" value='${item.key.name}'/>

	<c:forEach var='crf' items='${item.value}'>
		<option value="<c:out value='${crf.id}'/>">
			<c:out value='${crf_name}'/> -- 
			<c:out value='${crf.name}'/>
		</option>
	</c:forEach>
</c:forEach>
</select><br/>
<input type="submit" value="Select CRF and Continue" class="button_xlong"/>
</form>
<jsp:include page="../include/footer.jsp"/>