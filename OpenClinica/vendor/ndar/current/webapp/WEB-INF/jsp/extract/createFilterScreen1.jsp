<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<h1><span class="title_extract">View Dataset Filters <a href="javascript:openDocWindow('help/3_3_viewFilters_Help.html')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>

<P><jsp:include page="../include/showPageMessages.jsp"/></P>
<P>For the current study/site you may include filters to limit the subjects
retrieved in a dataset.</P>
<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showFilterRow.jsp" /></c:import>

<jsp:include page="../include/footer.jsp"/>