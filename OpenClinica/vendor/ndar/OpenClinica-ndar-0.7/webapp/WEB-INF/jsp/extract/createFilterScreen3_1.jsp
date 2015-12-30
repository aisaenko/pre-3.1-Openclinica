<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>

<h1><span class="title_extract">Create Filter: Select Section <a href="javascript:openDocWindow('help/3_4_createFilter_Help.html#step2')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>
<P><jsp:include page="../include/showPageMessages.jsp"/></P>
<jsp:include page="createFilterBoxes.jsp">
	<jsp:param name="selectSection" value="1"/>
</jsp:include>
<form action="CreateFiltersTwo" method="post" name="cf2">
<input type="hidden" name="action" value="sectionselected"/>
&nbsp;<b>CRF</b>&nbsp;&nbsp;<c:out value='${cBean.name}'/>&nbsp;
<c:out value='${cvBean.name}'/><br/>
&nbsp;<b>Section</b>&nbsp;&nbsp;
<select name="sectionId">
	<option value="0" selected>Select Section</option>
<c:forEach var='item' items='${sections}'>
			<option value="<c:out value='${item.id}'/>">
			<c:out value='${item.name}'/>
		</option>
</c:forEach>
</select><br/>
<input type="submit" value="Select Section and Continue" class="button_xlong"/>
</form>
<jsp:include page="../include/footer.jsp"/>