<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/subjectMgmt-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='subjectEntryLabelBean' class='org.akaza.openclinica.bean.subject.SubjectEntryLabelBean'/>
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.core.EntityBeanTable'/>
<jsp:useBean scope='request' id='message' class='java.lang.String'/>

<h1><span class="title_SubjectMgmt">Subject Entry Label Management <a href="javascript:openDocWindow('help/5_1_administerUsers_Help.html')"><img src="images/bt_Help_SubjectMgmt.gif" border="0" alt="Help" title="Help"></a></span></h1>

<jsp:include page="../include/alertbox.jsp" />
<div class="homebox_bullets"><a href="CreateSubjectEntryLabel">Create a New Subject Entry Label</a></div>
<p></p>

<c:import url="../include/showTable.jsp">
	<c:param name="rowURL" value="showSubjectEntryLabelRow.jsp" />
</c:import>

<jsp:include page="../include/footer.jsp"/>
