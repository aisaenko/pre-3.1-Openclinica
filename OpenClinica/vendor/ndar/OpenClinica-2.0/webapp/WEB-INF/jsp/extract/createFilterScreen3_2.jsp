<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>

<h1><span class="title_extract">Create Filter: Specify Parameters <a href="javascript:openDocWindow('help/3_4_createFilter_Help.html#step3')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>
<P><jsp:include page="../include/showPageMessages.jsp"/></P>
<jsp:include page="createFilterBoxes.jsp">
	<jsp:param name="selectParameters" value="1"/>
</jsp:include>
<form action="CreateFiltersTwo" name="cf2">
<input type="hidden" name="action" value="questionsselected"/>

&nbsp;<b>CRF</b>&nbsp;&nbsp;<c:out value='${cBean.name}'/>&nbsp;<c:out value='${cvBean.name}'/><br/>
&nbsp;<b>Section</b>&nbsp;&nbsp;<c:out value='${secBean.name}'/><br/>

<c:forEach var='item' items='${metadatas}'>
	&nbsp;&nbsp;<input type="checkbox" name="ID<c:out value='${item.id}'/>">&nbsp;
			<c:out value='${item.questionNumberLabel}'/> 
			<c:out value='${item.header}'/> 
			<c:out value='${item.leftItemText}'/> ... 
			<c:out value='${item.rightItemText}'/><br/>
</c:forEach>

<br/><br/>
<input type="submit" value="Select Data Elements and Continue" class="button_xlong"/>
</form>
<jsp:include page="../include/footer.jsp"/>