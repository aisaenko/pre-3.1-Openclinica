<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/subjectMgmt-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.core.EntityBeanTable'/>
<h1><span class="title_SubjectMgmt">Manage Subjects <a href="javascript:openDocWindow('help/5_2_administerSubjects_Help.html')"><img src="images/bt_Help_SubjectMgmt.gif" border="0" alt="Help" title="Help"></a></span></h1>
<jsp:include page="../include/alertbox.jsp" />

<div class="homebox_bullets"><a href="AddNewPiiSubject">Add a New Subject</a></div>
<p></p>
<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showPiiSubjectRow.jsp" /></c:import>
<jsp:include page="../include/footer.jsp"/>
