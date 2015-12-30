<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>


<h1><span class="title_extract">Create Datasets: Dataset Filters Applied</span></h1>

<P><jsp:include page="../showInfo.jsp"/></P>
<p>Please select one or more filters you would like to apply to this data set.</p>
<form action="CreateDataset" method="post">
<input type="hidden" name="action" value="beginsubmit"/>
<input type="checkbox" name="all"> Filter 1<br/>

<center>
<input type="submit" name="remove" value="Remove Filter" class="button_xlong"/>
<input type="submit" name="apply" value="Apply New Filter" class="button_xlong"/>
<input type="submit" name="export" value="Save & Export" class="button_xlong"/>
</center>
</form>
<jsp:include page="../include/footer.jsp"/>