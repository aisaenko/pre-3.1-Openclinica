<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>


<h1><span class="title_extract">Create Dataset: Apply Dataset Filter <a href="javascript:openDocWindow('help/3_2_createDataset_Help.html#step3')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>

<P><jsp:include page="../showInfo.jsp"/></P>

<jsp:include page="createDatasetBoxes.jsp" flush="true">
<jsp:param name="chooseFilter" value="1"/>
</jsp:include>
<p>Please select a filter you would like to apply to this data set.  
You can do this by simply selecting the 
<img name="bt_Export1" src="images/bt_Export.gif" border="0" alt="Apply Filter" hspace="6"> icon.</p>

<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="applyFilterRow.jsp" /></c:import>


<form action="ApplyFilter" method="post">
<input type="hidden" name="action" value="validate"/>
<%--<input type="submit" name="submit" value="Create New Filter" class="button_xlong"/>
--%>
<input type="submit" name="submit" value="Skip Apply Filter and Save" class="button_xlong"/>


</form>
<jsp:include page="../include/footer.jsp"/>