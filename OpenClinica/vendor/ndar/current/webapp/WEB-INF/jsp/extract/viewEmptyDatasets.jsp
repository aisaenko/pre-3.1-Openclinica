<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="datasets" class="java.util.ArrayList"/>

<h1><span class="title_extract">View Datasets</span></h1>

<P><jsp:include page="../include/showPageMessages.jsp"/></P>

	<p>Currently, you do not have any Datasets.</p>

<p><center>
<a href="ViewDatasets">Show All Datasets</a> |
<a href="CreateDataset">Create Dataset</a>
</center></p>


<jsp:include page="../include/footer.jsp"/>